package org.cabbage.crawler.reaper.commons.html.extractor;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.tree.DefaultAttribute;

/**
 * 内容抽取的标准公共接口
 * 
 * @author wkshen
 *
 */
public interface Extractor {
	/**
	 * 解析URL所表示的文章载体，并且抽取其文章内容
	 * 
	 * @param url
	 *            待处理文章载体的URL路径
	 * @throws ExtractException
	 *             文章抽取中出现的异常错误
	 */
	public void parse(final String url) throws ExtractException;

	/**
	 * 解析URL所表示的文章载体，并且抽取其文章内容
	 * 
	 * @param url
	 *            待处理文章载体的URL路径
	 * @throws ExtractException
	 *             文章抽取中出现的异常错误
	 */
	public void parse(final URL url) throws ExtractException;

	/**
	 * 解析输入流中文章载体，并且抽取出其文章内容
	 * 
	 * @param in
	 *            文本输入流
	 * @throws ExtractException
	 *             文章抽取中出现的异常错误
	 */
	public void parse(final InputStream in) throws ExtractException;

	/**
	 * 解析输入字符系列，并且抽取出其文章内容
	 * 
	 * @param text
	 *            字符系列
	 * @throws ExtractException
	 *             文章抽取中出现的异常错误
	 */
	public void parse(CharSequence text) throws ExtractException;

	/**
	 * 用传入的字符编码集，解析输入流中文章载体，并且抽取出其文章内容
	 * 
	 * @param in
	 *            文本输入流
	 * @param charset
	 *            指定字符编码集
	 * @throws ExtractException
	 *             文章抽取中出现的异常错误
	 */
	public void parse(InputStream in, String charset) throws ExtractException;

	/**
	 * 获取当前载体的编码方式
	 * 
	 * @return 编码方式
	 */
	public String getEncoding();

	/**
	 * 获取当前文章的纯文本内容
	 * 
	 * @return 文章纯文本内容
	 */
	public String getText();

	/**
	 * 获取当前文章中所有url； 1. 网络url，例如：http://10.0.125.60/web/ 2.
	 * 本地url，例如：file://C:\Documents and Settings\All Users\Documents
	 * 
	 * @return Url列表
	 */
	public List<String> getUrls();

	/**
	 * 获取当前文章中所有url； 1. 网络url，例如：http://10.0.125.60/web/ 2.
	 * 本地url，例如：file://C:\Documents and Settings\All Users\Documents
	 * 
	 * @return Url列表
	 */
	public Map<String, DefaultAttribute> getUrlsWithAttribute();

	/**
	 * 将当前Url和一个相对Url进行拼接 例如：1.
	 * "http://10.0.125.60/web/"和"./index.php?mods=forumdisplay&forumid=2" -->
	 * "http://10.0.125.60/web/index.php?mods=forumdisplay&forumid=2" 2.
	 * "http://10.0.125.60/web/index.php?mods=forumdisplay&forumid=2"和"../tools/"
	 * --> "http://10.0.125.60/tools/" 若相对url以http和ftp协议开头，则直接返回相对url
	 * 若相对url开头无任何特殊符号，则与开头为“./”的url处理方式相同
	 * 
	 * @param originalUrl
	 *            原始Url
	 * @param oppositeUrl
	 *            相对Url
	 * @return 拼接好的Url字串
	 */
	public String assembledUrl(String originalUrl, String oppositeUrl);

	/**
	 * 获取当前输入流的DOM模型（dom4j描述)
	 * 
	 * @return org.dom4j.Document DOM模型
	 */
	public Document getDom();

	/**
	 * 获取当前输入流的DOM模型（dom4j描述)的文件描述体
	 * 
	 * @param fileName
	 *            DOM保存的文件路径
	 * @return 文件名
	 */
	public String getDomFile(final String fileName);

	/**
	 * 释放此次抽取过程用到的所有临时对象
	 */
	public void free();
}
