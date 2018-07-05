package org.cabbage.crawler.reaper.worker.html.extractor;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cabbage.crawler.reaper.worker.network.httpclient.HttpClientUtils;
import org.dom4j.tree.DefaultAttribute;

/**
 * 
 * 页面通用抽取器。 根据配置文件中的抽取参数，将页面中的各个属性抽取下来
 * 
 * @author wkshen
 * 
 */
public class GeneralExtractor extends AbstractHtmlExtractor {

	/**
	 * log4j
	 */
	protected static Log LOGGER = LogFactory.getLog(GeneralExtractor.class);

	@Override
	public boolean isAcceptNode(au.id.jericho.lib.html.Element htmlElement) {
		if (htmlElement.getName().equalsIgnoreCase("span") && htmlElement.getAttributeValue("style") != null
				&& htmlElement.getAttributeValue("style").equalsIgnoreCase("display:none")) {
			// 过滤baise.cc中的干扰码
			return false;
		}
		return true;
	}

	@Override
	public boolean isAcceptNodeText(au.id.jericho.lib.html.Segment node) {
		return true;
	}

	public static void main(String[] args) {
		GeneralExtractor extractor = new GeneralExtractor();
		try {
			HttpClientUtils httpClient = new HttpClientUtils();
			byte[] responseData = httpClient.get("http://news.qq.com/", null, null, null).getBytes();
			ByteArrayInputStream bais = new ByteArrayInputStream(responseData);
			extractor.parse(bais, "utf-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, DefaultAttribute> urls = extractor.getUrlsWithAttribute();

		if (null == urls || urls.size() == 0) {
			System.err.println("Get nothing!");
		} else {
			Iterator<String> i = urls.keySet().iterator();
			while (i.hasNext()) {
				String url = i.next();
				DefaultAttribute a = urls.get(url);
				try {
					System.out.println(url + "    " + a.getParent().getText());
					// System.out.println(url + " " + new
					// String(a.getParent().getText().getBytes("ISO-8859-1"), "GB2312"));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
