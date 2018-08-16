package org.cabbage.commons.utils.xml;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

public class XPathUtils {
	
	//http://www.ibm.com/developerworks/cn/xml/x-javaxpathapi.html
	//http://blog.csdn.net/jefferxun1/article/details/7805062
	//http://www.w3school.com.cn/xpath/xpath_syntax.asp
	

	/**
	 * evaluate
	 * @param document
	 * @param xpathStr
	 * @param qName
	 *            XPathConstants.STRING
	 * @return
	 * @throws XPathExpressionException
	 */
	public synchronized static Object evaluate(Document document,
			String xpathStr, QName qName) throws XPathExpressionException {
		Object value = null;
		if (null != xpathStr && null != document) {
			XPath xpath = XPathFactory.newInstance().newXPath();
			value = xpath.evaluate(xpathStr, document, qName);
		}
		return value;
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}

}
