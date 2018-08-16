package org.cabbage.commons.utils.xml;

import java.io.File;
import java.io.IOException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class JDomUtils {

	public static synchronized Element loadElement(String filePath)
			throws JDOMException, IOException {
		Element rootElement = null;
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new File(filePath));
		rootElement = doc.getRootElement();
		return rootElement;
	}

	public static synchronized Document loadDocument(String filePath)
			throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new File(filePath));
		return doc;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
