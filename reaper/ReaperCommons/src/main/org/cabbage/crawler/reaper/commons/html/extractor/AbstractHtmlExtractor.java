package org.cabbage.crawler.reaper.commons.html.extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cabbage.commons.utils.url.URLUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.VisitorSupport;
import org.dom4j.tree.DefaultAttribute;
import org.dom4j.tree.DefaultElement;

import au.id.jericho.lib.html.Attribute;
import au.id.jericho.lib.html.Attributes;
import au.id.jericho.lib.html.CharacterReference;
import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.HTMLElements;
import au.id.jericho.lib.html.MasonTagTypes;
import au.id.jericho.lib.html.MicrosoftTagTypes;
import au.id.jericho.lib.html.Segment;
import au.id.jericho.lib.html.Source;
import au.id.jericho.lib.html.StartTag;
import au.id.jericho.lib.html.Tag;

/**
 * HTML页面的抽取抽象接口
 * 
 * @author wkshen
 * 
 */
public abstract class AbstractHtmlExtractor extends AbstractExtractor {

	protected static Log LOGGER = LogFactory.getLog(AbstractHtmlExtractor.class);

	/**
	 * 构建的
	 */
	protected final static String ROOT_ELEMENT_NAME = "document";

	protected final static HashSet<String> HTML_FLAG_SET = buildHtmlFlagSet();

	protected static Pattern _styleDisplayNonePattern = null;

	protected Source source = null;

	protected Document document = null;

	protected String title = null;

	protected String meta = null;

	/**
	 * 存储文本节点信息，利用这些节点的偏移量作为索引
	 */
	protected HashMap<Integer, String> textMap = new HashMap<Integer, String>();

	@SuppressWarnings("unchecked")
	private static HashSet<String> buildHtmlFlagSet() {
		List<String> list = HTMLElements.getElementNames();
		HashSet<String> flagSet = new HashSet<String>(list.size());
		for (String name : list) {
			flagSet.add(name);
		}
		return flagSet;
	}

	public void parse(final String url) throws ExtractException {
		try {
			parse(new URL(url));
		} catch (Exception e) {
			throw new ExtractException(e);
		}
	}

	public void parse(final URL url) throws ExtractException {
		try {
			parse(url.openConnection().getInputStream());
		} catch (IOException e) {
			throw new ExtractException(e);
		}
	}

	public void parse(final InputStream in) throws ExtractException {
		try {
			MicrosoftTagTypes.register();
			MasonTagTypes.register();
			source = new Source(in);
			source.fullSequentialParse();
			buildTextIndex();
			buildDom();
		} catch (IOException e) {
			throw new ExtractException(e);
		}
	}

	public void parse(CharSequence text) throws ExtractException {
		MicrosoftTagTypes.register();
		MasonTagTypes.register();
		source = new Source(text);
		source.fullSequentialParse();
		buildTextIndex();
		buildDom();
	}

	/**
	 * 用指定的编码对流进行编译操作，避免部分页面没有meta标签导致返回数据乱码的问题
	 * 
	 * @param in
	 *            待处理流
	 * @param charset
	 *            字符编码集
	 * @throws ExtractException
	 */
	public void parse(InputStream in, String charset) throws ExtractException {
		StringBuffer sb = readInputStream(in, charset);
		if (sb == null || StringUtils.isEmpty(sb.toString())) {
			return;
		}
		String str = sb.toString();
		parse(str.subSequence(0, str.length() - 1));
	}

	@SuppressWarnings("unchecked")
	private void buildTextIndex() {
		for (Iterator<Segment> i = source.getNodeIterator(); i.hasNext();) {
			Segment node = i.next();
			if (node instanceof Tag) {
				Tag tag = (Tag) node;
				if (tag.getTagType().isServerTag())
					continue; // ignore server tags
			} else {
				// Process the text segment (just output its text in this
				// example):
				String text = CharacterReference.decodeCollapseWhiteSpace(node);
				if (text != null && text.trim().length() > 0) {
					String content = AbstractExtractor.earseISOControlChar(text);
					if (content.length() < 1)
						continue;
					if (this.isAcceptNodeText(node)) {
						this.textMap.put(node.getBegin(), content);
					} else {
						continue;
					}
				}
			}
		}
	}

	/**
	 * 处理是否需要提取这个标记段的text内容，此处的节点由于html的特殊性，采用的是标记段的方式
	 * 
	 * @param node
	 *            一个标记节点
	 * @return 需要保留，返回true,否则返回false
	 */
	public abstract boolean isAcceptNodeText(Segment node);

	@SuppressWarnings("unchecked")
	private void buildDom() {
		document = DocumentHelper.createDocument();
		org.dom4j.Element domElement = document.addElement(ROOT_ELEMENT_NAME);
		List<Element> list = source.getChildElements();

		for (Element htmlElement : list) {
			visitNodes(htmlElement, domElement);
		}
	}

	@SuppressWarnings("unchecked")
	private void visitNodes(final Element htmlElement, org.dom4j.Element domElement) {
		if (!isAcceptNode(htmlElement))
			return;

		org.dom4j.Element htmlDomElement = html2dom(domElement, htmlElement);

		if (isScriptStyleFlag(htmlElement)) {
			htmlDomElement.setText(htmlElement.getContent().toString());
		}
		// 处理title
		if (htmlElement.getName() == HTMLElements.TITLE) {
			String text = htmlElement.getTextExtractor().toString();
			if (text != null && text.trim().length() > 0) {
				if (this.title == null || this.title.trim().length() == 0) {
					this.title = text;
				} else {
					if (null != this.title && this.title.trim().length() > 0
							&& text.trim().length() > this.title.trim().length()) {
						this.title = text;
					}
				}
			}
			if (text != null)
				htmlDomElement.setText(this.title);
			return;
		}
		// 处理meta
		if (htmlElement.getName() == HTMLElements.META) {
			String name = htmlElement.getAttributeValue("name");
			if (name != null && name.equalsIgnoreCase("KEYOWRDS")) {
				this.meta = htmlElement.getAttributeValue("content");
			}
			return;
		}

		// 处理text数据，由于html的tag是不完全的，需要分类处理
		// 这里不区分闭合与非闭合标签，只区分谁更近一些
		StartTag stag = htmlElement.getStartTag();
		if (this.textMap.containsKey(stag.getEnd())) {// 有text
			htmlDomElement.setText(this.textMap.get(stag.getEnd()));
		}

		List<Element> list = htmlElement.getChildElements();
		if (list.isEmpty())
			return;
		for (Element e : list) {
			visitNodes(e, htmlDomElement);
			if (e.getStartTag().isEndTagRequired() && this.textMap.containsKey(e.getEnd())) {
				htmlDomElement.addText(this.textMap.get(e.getEnd()));
			}
		}
	}

	@SuppressWarnings("unchecked")
	private org.dom4j.Element html2dom(org.dom4j.Element domElement, final Element htmlElement) {
		org.dom4j.Element domHtmlElement = domElement.addElement(earseErrorDomNameCharset(htmlElement.getName()));
		Attributes attributes = htmlElement.getAttributes();
		if (attributes != null && !attributes.isEmpty()) {
			for (Iterator<Attribute> i = (Iterator<Attribute>) attributes.iterator(); i.hasNext();) {
				Attribute attribute = i.next();
				domHtmlElement.addAttribute(earseErrorDomNameCharset(attribute.getName()), attribute.getValue());
			}
		}
		return domHtmlElement;
	}

	/**
	 * 是否为html结束标记
	 * 
	 * @param name
	 *            标记的名称
	 * @return true 是html结束标记，false 不是
	 */
	public static synchronized boolean isHtmlElementFlag(final String name) {
		return HTML_FLAG_SET.contains(name);
	}

	/**
	 * 是否为script标记
	 * 
	 * @param htmlElement
	 *            html元素
	 * @return true 是script标记，false 不是
	 */
	public static synchronized boolean isScriptStyleFlag(final Element htmlElement) {
		String name = htmlElement.getName();
		if (name.equals(Element.SCRIPT) || name.equals(Element.STYLE)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断元素是否为超链接标记
	 * 
	 * @param htmlElement
	 *            html元素
	 * @return true:是超链接，false:不是
	 */
	public static synchronized boolean isLinkElement(final Element htmlElement) {
		String name = htmlElement.getName();
		if (name.equals(Element.A) && htmlElement.getAttributeValue("href") != null) {
			return true;
		}
		if (name.equals(Element.AREA) && htmlElement.getAttributeValue("href") != null) {
			return true;
		}
		return false;
	}

	/**
	 * 过滤title中的噪音信息，仅提取关键的标题信息
	 * 
	 * @param title
	 *            已知的标题
	 * @return 消除噪音后的标题信息
	 */
	public static synchronized String filterTitleNoise(String title) {
		if (title == null || title.length() < 6)
			return title;
		int lpos = -1;
		lpos = title.indexOf("-");
		if (lpos == -1)
			lpos = title.indexOf("_");
		if (lpos == -1)
			lpos = title.indexOf("-");
		if (lpos == -1) {
			return title;
		} else if (lpos < 6 && lpos + 1 < title.length()) {
			return filterTitleNoise(title.substring(lpos + 1));
		} else {
			return title.substring(0, lpos);
		}
	}

	public String getEncoding() {
		return source.getEncoding();
	}

	public String getText() {
		return this.source.getTextExtractor().toString();
	}

	@SuppressWarnings("unchecked")
	public List<String> getUrlsFilterBlackLinks() {
		ArrayList<String> list = new ArrayList<String>();
		try {
			List<DefaultElement> nodes = this.document.selectNodes("//div[contains(@style,'hidden')]");
			if (null != nodes && nodes.size() > 0) {
				for (DefaultElement e : nodes) {
					e.getParent().remove(e);
				}
			}
		} catch (Exception e) {

		}

		List<DefaultAttribute> elements = this.document.selectNodes("//@href");
		elements.addAll(this.document.selectNodes("//@HREF"));
		for (DefaultAttribute element : elements) {
			String url = element.getText();
			if (isJunkUrl(url)) {
				continue;
			}
			if (isBlackLink(element)) {
				continue;
			}
			list.add(url);
		}
		// elements = this._doc.selectNodes("//@HREF");
		// for (DefaultAttribute element : elements) {
		// String url = element.getText();
		// if (isJunkUrl(url)) {
		// continue;
		// }
		// list.add(url);
		// }

		// for (String url : list) {
		// System.out.println(url);
		// }
		return list;
	}

	private boolean isBlackLink(DefaultAttribute e) {
		boolean isBlackLink = false;
		int count = 0;
		if (null != e) {
			// org.dom4j.Attribute title = e.getParent().attribute("title");
			// if (null == title) {
			// title = e.getParent().attribute("TITLE");
			// }
			// if (null == title) {
			// count++;
			// } else {
			// }

			// if(e.getText().equals("http://news.163.com/17/1226/09/D6ISFN08000189FH.html")){
			// System.out.println(e.getText());
			// }
			if (null == e.getParent().getTextTrim() || e.getParent().getTextTrim().length() == 0
					|| "&lt;/a".equals(e.getParent().getTextTrim())) {
				count++;
			}
			if (count > 0) {
				isBlackLink = true;
			}
		}
		return isBlackLink;
	}

	@SuppressWarnings("unchecked")
	public List<String> getUrls() {
		ArrayList<String> list = new ArrayList<String>();
		List<DefaultAttribute> elements = this.document.selectNodes("//@href");
		elements.addAll(this.document.selectNodes("//@HREF"));
		for (DefaultAttribute element : elements) {
			String url = element.getText();
			if (isJunkUrl(url)) {
				continue;
			}
			list.add(url);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public Map<String, DefaultAttribute> getUrlsWithAttribute() {
		Map<String, DefaultAttribute> map = new HashMap<String, DefaultAttribute>();
		if (null == document) {
			return null;
		}
		List<DefaultAttribute> elements = document.selectNodes("//@href");
		elements.addAll(document.selectNodes("//@HREF"));
		for (DefaultAttribute element : elements) {
			String url = element.getText();
			if (null == url || "".equals(url.trim()) || isJunkUrl(url)) {
				continue;
			}
			url = this.assembledUrl(super.getUrl(), url.trim());
			map.put(url, element);
		}
		return map;
	}

	private boolean isJunkUrl(String url) {
		if (url.equals("") || url.length() < 1) {
			return true;
		}
		if (url.startsWith("#")) {
			return true;
		}
		if (url.startsWith("javascript")) {
			return true;
		}
		if (url.startsWith("mailto")) {
			return true;
		}
		return false;
	}

	public String assembledUrl(String originalUrl, String oppositeUrl) {
		if (oppositeUrl.toLowerCase().startsWith("https:") || oppositeUrl.toLowerCase().startsWith("http:")
				|| oppositeUrl.toLowerCase().startsWith("ftp:")) {
			return oppositeUrl;
		}

		Node n = this.document.getRootElement().selectSingleNode("/document/html/head/base");
		org.dom4j.Element e = null;
		String baseHref = null;
		if (null != n) {
			e = this.document.getRootElement().element("html").element("head").element("base");
			baseHref = e.attributeValue("href");
			if (null != baseHref && baseHref.trim().length() > 0) {
				originalUrl = baseHref;
			}
		}

		if (oppositeUrl.startsWith("../")) {
			int flag = 0;
			while (oppositeUrl.indexOf("../") != -1) {
				oppositeUrl = oppositeUrl.substring(3);
				flag++;
			}
			for (int i = 0; i <= flag; i++) {
				int index = originalUrl.lastIndexOf("/");
				if (index != -1) {
					originalUrl = originalUrl.substring(0, index);
				}
			}
			return originalUrl + "/" + oppositeUrl;
		} else if (oppositeUrl.startsWith("//")) {
			// String domain = NetUtils.getDomain(originalUrl);
			String domain = null;
			try {
				domain = URLUtils.getDomain(originalUrl);
			} catch (MalformedURLException e1) {
				LOGGER.error("URLUtils.getDomain(" + originalUrl + ") error!", e1);
			}
			if (null != domain && oppositeUrl.contains(domain)) {
				if (originalUrl.toLowerCase().startsWith("https:")) {
					return "https:" + oppositeUrl;
				} else if (originalUrl.toLowerCase().startsWith("http:")) {
					return "http:" + oppositeUrl;
				} else if (originalUrl.toLowerCase().startsWith("ftp:")) {
					return "ftp:" + oppositeUrl;
				}
			}
			return oppositeUrl;
		} else if (oppositeUrl.startsWith("/")) {
			int lf = originalUrl.indexOf("//");
			if (lf != -1) {
				int rf = originalUrl.indexOf("/", lf + 2);
				if (rf != -1) {
					originalUrl = originalUrl.substring(0, rf);
				}
			}
			return originalUrl + oppositeUrl;
		} else {
			int index = originalUrl.lastIndexOf("/");
			if (index != -1) {
				originalUrl = originalUrl.substring(0, index + 1);
			}
			if (oppositeUrl.startsWith("./")) {
				return originalUrl + oppositeUrl.substring(2);
			} else {
				return originalUrl + oppositeUrl;
			}
		}
	}

	public Document getDom() {
		return document;
	}

	public String getDomFile(final String fileName) {
		AbstractExtractor.xmlDocWrite(this.document, fileName);
		return fileName;
	}

	public void free() {
		this.source.clearCache();
		this.document.clearContent();
	}

	/**
	 * 提取一个节点元素内的所有文本内容，并且计算换行符
	 * 
	 * @param node
	 *            待提取文本内容的HTML转DOM对象
	 * @return 抽取后节点内的所有子节点的内容
	 */
	public static synchronized String extractElementValue(final org.dom4j.Element node) {
		if (node == null)
			return "";
		final StringBuffer buf = new StringBuffer();
		/**
		 * 
		 * @author unknown
		 * 
		 */
		class Visitor extends VisitorSupport {
			private boolean isEnd = false;

			private boolean isMayBr = false;

			public void visit(org.dom4j.Element node) {
				String name = node.getName();
				if (name.equals("img")) {
					buf.append("图片内容");
					buf.append("\r\n");
				}
				if (name.equals("embed")) {
					buf.append("视频或音频内容");
					buf.append("\r\n");
				}
				if (isMayBr && (name.equals("br") || name.equals("p"))) {
					isMayBr = false;
					buf.append("\r\n");
				}
			}

			public void visit(org.dom4j.Text node) {
				if (isEnd) {
					return;
				}
				String text = node.getText().trim();
				if (text.length() < 1) {
					return;
				}
				buf.append(text);
				isMayBr = true;
			}
		}

		Visitor vis = new Visitor();
		node.accept(vis);
		return buf.toString();
	}

	/**
	 * 在解析HTML并且处理那些错误页面的时候，校验那些节点是需要的，那些节点是不需要的
	 * 
	 * @param htmlElement
	 *            HTML节点元素，节点操作方法，请参见jericho的Element说明
	 * @return 返回true，那么该节点保留；返回false，那么该节点丢弃
	 */
	public abstract boolean isAcceptNode(final Element htmlElement);

	/**
	 * 将流数据转换成StringBuffer
	 * 
	 * @param in
	 *            要转换的流
	 * @param charset
	 *            流的字符集
	 * @return 转换后的字符串
	 */
	protected StringBuffer readInputStream(InputStream in, String charset) {
		InputStreamReader isr = null;
		BufferedReader br = null;
		StringBuffer sb = new StringBuffer();
		try {
			isr = new InputStreamReader(in, charset);
			br = new BufferedReader(isr);
			String str = br.readLine();
			while (str != null) {
				sb.append(str);
				str = br.readLine();
			}
		} catch (IOException e) {
			LOGGER.warn("Extract inputStream error! continue");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
				}
			}
		}
		return sb;
	}

}
