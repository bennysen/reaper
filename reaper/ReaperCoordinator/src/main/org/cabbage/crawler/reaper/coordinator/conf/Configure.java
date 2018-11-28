package org.cabbage.crawler.reaper.coordinator.conf;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.cabbage.commons.utils.file.FileUtils;
import org.cabbage.crawler.reaper.exception.ReaperException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class Configure {

	private static Logger LOGGER = Logger.getLogger(Configure.class);
	private static Configure INSTANCE = null;
	private static final String CONFIG_FILE_PATH = Configure.class.getClassLoader().getResource("").getPath()
			+ "../conf/";

	private static Map<String, String> KV = new ConcurrentHashMap<String, String>(64);

	static {
		if (null == INSTANCE) {
			INSTANCE = new Configure();
			try {
				init();
			} catch (ReaperException e) {
				LOGGER.error("Init Configure failed!", e);
			}
		}
	}

	private static Element loadElement(String filePath) throws JDOMException, IOException {
		Element rootElement = null;
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new File(filePath));
		rootElement = doc.getRootElement();
		return rootElement;
	}

	public static synchronized Configure getInstance(boolean reload) throws ReaperException {
		if (null == INSTANCE) {
			INSTANCE = new Configure();
			init();
		} else {
			if (reload) {
				KV.clear();
				init();
			}
		}
		return INSTANCE;
	}

	private Configure() {
	}

	private static void init() throws ReaperException {
		load();
	}

	@SuppressWarnings("unchecked")
	private static void load() throws ReaperException {
		List<String> files = FileUtils.scanFile(CONFIG_FILE_PATH);
		LOGGER.info("Scannig configure files ...[" + CONFIG_FILE_PATH + "]");
		if (null != files) {
			Element root = null;
			for (String file : files) {
				if (!file.endsWith(".xml")) {
					continue;
				}
				LOGGER.info("File [" + file + "] Loading...");
				try {
					root = loadElement(file);
				} catch (JDOMException e) {
					LOGGER.error("Load file[" + file + "] failed!", e);
					throw new ReaperException("Load file[" + file + "] failed!", e);
				} catch (IOException e) {
					LOGGER.error("Load file[" + file + "] failed!", e);
					throw new ReaperException("Load file[" + file + "] failed!", e);
				}
				List<Element> list = root.getChildren();
				if (null != root) {
					for (Element e : list) {
						if (null != e.getChildTextTrim("name") && null != e.getChildTextTrim("value")) {
							KV.put(e.getChildTextTrim("name"), e.getChildTextTrim("value"));
							LOGGER.info("Property:[" + e.getChildTextTrim("name") + "]-[" + e.getChildTextTrim("value")
									+ "]");
						}
					}
				}
			}
			LOGGER.info("Configure files Load finished!");
		}

	}

	public synchronized String getProperty(String key) {
		String value = null;
		if (null != KV && null != key && key.trim().length() > 0) {
			value = KV.get(key.trim());
		}
		return value;
	}

	public synchronized Long getPropertyLong(String key) throws ReaperException {
		String value = getProperty(key);
		Long v = null;
		if (null != value) {
			try {
				v = Long.parseLong(value);
			} catch (Exception e) {
				throw new ReaperException(e);
			}
		}
		return v;
	}

	public synchronized Integer getPropertyInteger(String key) throws ReaperException {
		String value = getProperty(key);
		Integer v = null;
		if (null != value) {
			try {
				v = Integer.parseInt(value);
			} catch (Exception e) {
				throw new ReaperException(e);
			}
		}
		return v;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}

}
