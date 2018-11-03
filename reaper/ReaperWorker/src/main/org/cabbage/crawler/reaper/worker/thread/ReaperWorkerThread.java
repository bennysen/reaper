package org.cabbage.crawler.reaper.worker.thread;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cabbage.commons.utils.URLUtils;
import org.cabbage.crawler.reaper.beans.business.task.ReaperTask;
import org.cabbage.crawler.reaper.commons.filter.BlackLinkFilter;
import org.cabbage.crawler.reaper.commons.filter.SuffixFilter;
import org.cabbage.crawler.reaper.commons.html.extractor.GeneralExtractor;
import org.cabbage.crawler.reaper.commons.network.httpclient.HttpClientUtils;
import org.cabbage.crawler.reaper.exception.ReaperException;
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
	Set<String> produceLinks = null;

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
		this.task.setStatus(ReaperTask.FINISH);
	}

	private void disable() {
		this.task.setStatus(ReaperTask.DISABLE);
	}

	/**
	 * 
	 */
	public void run() {
		LOGGER.info("Task [" + task.getURL() + "] is running!");

		// step 1 paser url page,get page outlinks
		doHttp();
		if (null == url2node || url2node.size() == 0) {
			LOGGER.warn("Task [" + task.getURL() + "] is get nothing!");
			finish();
			return;
		}

		// step x classify
		classify();
		// setp x calculate
		calculate();
		// step x filter
		Set<String> urls = doFilter(url2node);
		if (null == urls || urls.size() == 0) {
			LOGGER.warn("Task [" + task.getURL() + "] is get nothing!");
			finish();
		}

		// step x reduce
		produceLinks = ReduceUtils.reduce(domain, urls);
		for (String url : urls) {
			System.out.println(url);
		}
		// step x mark
		mark();
		// step x output result
		LOGGER.info("Task [" + task.getURL() + "] is work done!");
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
							new Object[] { domain, task.getURL(), System.currentTimeMillis() / 1000,
									this.url2node.keySet(), task.getPreURL(), produceLinks, produceLinks.size() }));
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
		// TODO
	}

	private void doHttp() {
		GeneralExtractor extractor = new GeneralExtractor();
		String charset = null;
		if (null == task || task.isInvalid()) {
			return;
		}
		if (null == task.getCharset() || "".equals(task.getCharset().trim())) {
			charset = "utf-8";
		} else {
			charset = task.getCharset();
		}
		try {
			HttpClientUtils httpClient = new HttpClientUtils();
			byte[] responseData = httpClient.get(task.getURL(), task.getProxy(), task.getCookieStore(), charset)
					.getBytes();
			ByteArrayInputStream bais = new ByteArrayInputStream(responseData);
			extractor.parse(bais, charset);
		} catch (Exception e) {
			LOGGER.error("HTTP request error!", e);
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
				if (null == a.getParent().getText() || a.getParent().getText().trim().length() == 0) {
					continue;
					// TODO this condition is not enough
				}
				link2text.put(url, a.getParent().getText().trim());
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
		task.setURL("http://news.qq.com/");
		ReaperWorkerThread t = new ReaperWorkerThread(task);
		t.start();
	}
}
