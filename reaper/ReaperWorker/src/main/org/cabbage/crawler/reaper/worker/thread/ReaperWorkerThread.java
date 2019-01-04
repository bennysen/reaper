package org.cabbage.crawler.reaper.worker.thread;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.cabbage.commons.utils.URLUtils;
import org.cabbage.commons.utils.json.Json;
import org.cabbage.crawler.reaper.beans.business.task.ReaperTask;
import org.cabbage.crawler.reaper.commons.classifier.URLClassifier;
import org.cabbage.crawler.reaper.commons.classifier.utils.URLClassifierEntity;
import org.cabbage.crawler.reaper.commons.filter.BlackLinkFilter;
import org.cabbage.crawler.reaper.commons.filter.SuffixFilter;
import org.cabbage.crawler.reaper.commons.filter.ULRStringFilterFactory;
import org.cabbage.crawler.reaper.commons.filter.URLStringFilter;
import org.cabbage.crawler.reaper.commons.html.extractor.GeneralExtractor;
import org.cabbage.crawler.reaper.commons.network.httpclient.HttpClientUtils;
import org.cabbage.crawler.reaper.exception.ReaperException;
import org.cabbage.crawler.reaper.worker.config.Configure;
import org.cabbage.crawler.reaper.worker.context.AppContext;
import org.cabbage.crawler.reaper.worker.utils.CassandraUtils;
import org.cabbage.crawler.reaper.worker.utils.ReduceUtils;
import org.dom4j.tree.DefaultAttribute;

import com.datastax.driver.core.querybuilder.QueryBuilder;

/**
 * Reaper 工作线程
 * 
 * @author wkshen
 *
 */
public class ReaperWorkerThread extends Thread {

	private static final Log LOGGER = LogFactory.getLog(ReaperWorkerThread.class);

	ReaperTask task = null;
	String domain = null;
	Map<String, DefaultAttribute> url2node = null;
	Set<String> filteredLinks = null;
	Set<String> produceLinks = null;
	Set<String> seedLinks = new CopyOnWriteArraySet<String>();

	/**
	 * 
	 * @throws ReaperException
	 * 
	 */
	public ReaperWorkerThread(ReaperTask task) {
		this.task = task;
		if (null == task || task.isInvalid()) {
			LOGGER.error("Task is invalid!");
			disable();
		}
		try {
			domain = URLUtils.getDomain(task.getURL());
		} catch (MalformedURLException e) {
			LOGGER.error("Task [" + task.getURL() + "] can not get domain!!", e);
			disable();
		}
		if (null == domain || domain.trim().length() == 0) {
			LOGGER.error("Task [" + task.getURL() + "] can not get domain!!");
			disable();
		}
	}

	/**
	 * 
	 * @return
	 */
	public Long getWorkerState() {
		Long status = ReaperTask.INIT;
		if (null == task || null == task.getStatus()) {

		} else {
			status = task.getStatus();
		}
		return status;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isFinished() {
		boolean isFinished = false;
		if (null == task || null == task.getStatus()) {

		} else {
			if (task.getStatus() == ReaperTask.FINISH || task.getStatus() == ReaperTask.DISABLE
					|| task.getStatus() == ReaperTask.STOP) {
				isFinished = true;
			}
		}
		return isFinished;
	}

	private void finish() {

		task.setStatus(ReaperTask.FINISH);
		LOGGER.info("Task [" + task.getURL() + "] is work done!");
	}

	private void disable() {
		task.setStatus(ReaperTask.DISABLE);
		LOGGER.info("Task [" + task.getURL() + "] is work done!");
	}

	/**
	 * 
	 */
	public void run() {
		LOGGER.info("Task [" + task.getURL() + "] is running!");
		process();
		while (true) {
			if (null == seedLinks || seedLinks.size() == 0) {
				break;
			}
			String perURL = task.getURL();
			for (String seed : seedLinks) {
				task.setPreURL(perURL);
				task.setURL(seed);
				filteredLinks = null;
				produceLinks = null;
				url2node = null;
				process();
			}
			LOGGER.warn("Seed links size = [" + seedLinks.size() + "]");
		}
		finish();
	}

	private void process() {
		LOGGER.info("                         【" + this.task.getURL()
				+ "】                                                            ");
		AppContext.setLastActiveTimeMap(task.getID(), new Date());
		// step 1 paser url page,get page outlinks
		doHttp();
		if (null == url2node || url2node.size() == 0) {
			LOGGER.warn("Task [" + task.getURL() + "] is get nothing!");
			finish();
			return;
		}
		// step x filter
		filteredLinks = doFilter(url2node);
		// step x classify
		classify();
		// setp x calculate
		calculate();

		URLStringFilter usf = ULRStringFilterFactory.getInstance().getFilter(domain, URLStringFilter.OUTPUT);
		if (null == usf) {
		} else {
			usf.setLinks(filteredLinks);
			filteredLinks = usf.filter();
		}
		if (null == filteredLinks || filteredLinks.size() == 0) {
			LOGGER.warn("Task [" + task.getURL() + "] is get nothing!");
			finish();
			return;
		}

		// step x reduce
		produceLinks = ReduceUtils.reduce(domain, filteredLinks);
		// for (String url : produceLinks) {
		// System.err.println(url);
		// }
		// step x mark
		// mark();

		toMerge();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean toMerge() {
		boolean toMerge = false;
		Json j = new Json();
		j.a("projectid", task.getID());
		j.a("projectname", task.getSiteName());
		j.a("srcurl", task.getURL());
		j.a("sitetype", task.getSiteType());
		j.a("domaintype", task.getDomainType());
		j.a("countryid", task.getCountryID());
		j.a("areaid", 0);
		j.a("proxytype", task.getProxyType());
		j.a("postdata", null);
		j.a("posturl", null);
		j.a("trycount", 3);
		j.a("timeout", 60);
		j.a("iscrawlercomment", "0");
		j.a("domainid", task.getDomainID());
		if (null == task.getProxyType() || task.getProxyType() == 0 || null == task.getProxyIP()
				|| null == task.getProxyPort() || task.getProxyIP() == 0 || task.getProxyPort() == 0) {
			j.a("proxyip", "");
			j.a("proxyport", "");
		} else {

			j.a("proxyip", task.getProxyIP());
			j.a("proxyport", task.getProxyPort());

		}
		List<Json> js = new ArrayList<Json>();
		for (String url : produceLinks) {
			if (null != url && url.trim().length() > 0) {
				js.add(new Json("url", url.trim()));
				System.out.println(url);
			}
		}
		if (null != js && js.size() > 0) {
			j.al("urls", js);
			Map<String, String> params = new HashMap<String, String>();
			params.put("job", j.toString());
			try {
				String mergeService = Configure.getInstance(false).getProperty("mergeService");
				HttpClientUtils client = new HttpClientUtils();
				HttpResponse httpResponse = client.post(new HttpPost(mergeService), null, params, null);
				String res = null;
				int statusCode = httpResponse.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					toMerge = true;
					HttpEntity resEntity = httpResponse.getEntity();
					res = EntityUtils.toString(resEntity);
				} else {
					res = httpResponse.getStatusLine().toString();
				}
				LOGGER.info("pid[" + task.getID() + "] post[" + js.size() + "] to MergeService end![" + res + "]");
			} catch (Exception e) {
				LOGGER.error(e);
			}
		}
		return toMerge;
	}

	/**
	 * 搜索过程mark
	 */
	private void mark() {
		CassandraUtils ct = new CassandraUtils();
		try {
			ct.connect();
			if (null == produceLinks) {
				produceLinks = new HashSet<String>();
			}
			ct.insert(QueryBuilder.insertInto(CassandraUtils.getKeyspace(), "page_mark")
					.values(new String[] { "domain", "url", "timestamp", "outlinks", "perlink", "producelinks",
							"producelinkscount" },
							new Object[] { domain, task.getURL(), System.currentTimeMillis() / 1000, filteredLinks,
									task.getPreURL(), produceLinks, produceLinks.size() }));
		} catch (Exception e) {
			LOGGER.error("Mark [" + task.getURL() + "] error!", e);
		} finally {
			ct.close();
		}
	}

	/**
	 * 各类计算，包括链接变化率计算
	 */
	private void calculate() {
		// TODO
	}

	/**
	 * 链接URL格式分类
	 */
	private void classify() {
		List<URLClassifierEntity> uEntityList = null;
		try {
			uEntityList = new URLClassifier().urlCrawledFilter(task.getURL(), filteredLinks);
		} catch (Exception e) {
			LOGGER.warn("URLClassifier error!", e);
			return;
		}
		int j = uEntityList.size();
		if (j > 3) {
			j = 3;
		}
		if (j < 2) {
			// can not classifier!
			return;
		}
		for (int i = 0; i < j; i++) {
			URLClassifierEntity uEntity = uEntityList.get(i);
			LOGGER.info("Feature url[" + uEntity.getRatio() + "]:" + uEntity.getFeatureURL());
			Set<String> uSet = uEntity.getURLSet();
			if (null == uSet || uSet.size() == 0) {
				break;
			} else {
				for (String seed : uSet) {
					if (seedLinks.size() > 1024) {
						break;
					}
					if (null == seed || seed.trim().length() == 0) {
						continue;
					}
					// if (seed.contains("index")) {
					// continue;
					// }
					if (StringUtils.countMatches(seed, "/") < 5) {
						seedLinks.add(seed.trim());
					}
				}
			}
		}
	}

	private void doHttp() {
		GeneralExtractor extractor = new GeneralExtractor(task.getURL());
		String charset = null;
		if (null == task || task.isInvalid()) {
			return;
		}
		if (null == task.getCharset() || "".equals(task.getCharset().trim())) {
			charset = "utf-8";
		} else {
			charset = task.getCharset();
		}
		ByteArrayInputStream bais = null;
		try {
			HttpClientUtils httpClient = new HttpClientUtils();
			String data = httpClient.get(task.getURL(), task.getProxy(), task.getCookieStore(), charset);
			if (null == data) {
				return;
			}
			byte[] responseData = data.getBytes();
			bais = new ByteArrayInputStream(responseData);
			extractor.parse(bais, charset);
		} catch (Exception e) {
			LOGGER.error("HTTP request error!", e);
		} finally {
			if (null == bais) {
			} else {
				try {
					bais.close();
				} catch (IOException e) {
				}
			}
		}
		url2node = extractor.getUrlsWithAttribute();
	}

	private Set<String> doFilter(Map<String, DefaultAttribute> url2node) {
		if (null == url2node || url2node.size() == 0) {
			LOGGER.warn("GeneralExtractor get nothing!");
			return null;
		}

		Map<String, String> link2text = new HashMap<String, String>();
		Iterator<String> i = url2node.keySet().iterator();
		while (i.hasNext()) {
			String url = i.next();
			DefaultAttribute a = url2node.get(url);
			try {
				if (null != a.getParent().getText() && a.getParent().getText().trim().length() > 0) {
					link2text.put(url, a.getParent().getText().trim());
				} else if (null != a.getText() && a.getText().trim().length() > 0) {
					link2text.put(url, a.getText().trim());
				} else if (null != a.getValue() && a.getValue().trim().length() > 0) {
					link2text.put(url, a.getValue().trim());
				} else {
					// System.out.println(url);
				}
			} catch (Exception e) {
			}
		}
		BlackLinkFilter bf = new BlackLinkFilter(link2text);
		Set<String> urls = bf.filter();
		if (null == urls || urls.size() == 0) {
			return null;
		}

		SuffixFilter sf = new SuffixFilter(urls);
		urls = sf.filter();
		if (null == urls || urls.size() == 0) {
			return null;
		}

		URLStringFilter usf = ULRStringFilterFactory.getInstance().getFilter(domain, URLStringFilter.SEED);
		if (null == usf) {
		} else {
			usf.setLinks(urls);
			urls = usf.filter();
			if (null == urls || urls.size() == 0) {
				return null;
			}
		}

		Set<String> urls2 = new HashSet<String>();
		for (String url : urls) {
			if (URLUtils.isInvalid(url)) {
				continue;
			}
			if (url.contains(domain)) {
				urls2.add(url);
			}
		}

		return urls2;
	}

	public static void main(String[] args) throws ReaperException {
		ReduceUtils.initMapDB();
		ReaperTask task = new ReaperTask();
		task.setCharset("utf-8");
		task.setURL("http://www.ifeng.com/");
		task.setID(30003l);
		ReaperWorkerThread t = new ReaperWorkerThread(task);
		t.start();
	}
}
