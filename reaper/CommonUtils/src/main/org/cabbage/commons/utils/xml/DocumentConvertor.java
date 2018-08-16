package org.cabbage.commons.utils.xml;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom.JDOMException;
import org.jdom.input.DOMBuilder;
import org.jdom.output.DOMOutputter;
import org.xml.sax.SAXException;

public class DocumentConvertor {

	public synchronized static org.jdom.Document convert(
			org.w3c.dom.Document domDoc) throws JDOMException, IOException {
		return new DOMBuilder().build(domDoc);
	}

	public synchronized static org.w3c.dom.Document convert(
			org.jdom.Document jdomDoc) throws JDOMException {
		return new DOMOutputter().output(jdomDoc);
	}

	public synchronized static org.w3c.dom.Document convert(
			org.dom4j.Document doc) throws ParserConfigurationException,
			SAXException, IOException {
		if (doc == null) {
			return (null);
		}
		java.io.StringReader reader = new java.io.StringReader(doc.asXML());
		org.xml.sax.InputSource source = new org.xml.sax.InputSource(reader);
		javax.xml.parsers.DocumentBuilderFactory documentBuilderFactory = javax.xml.parsers.DocumentBuilderFactory
				.newInstance();
		javax.xml.parsers.DocumentBuilder documentBuilder = documentBuilderFactory
				.newDocumentBuilder();
		org.w3c.dom.Document dom = documentBuilder.parse(source);
		return dom;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
