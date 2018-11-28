package org.cabbage.crawler.reaper.commons.filter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cabbage.commons.utils.file.FileUtils;

public class ULRStringFilterFactory {

	private static ULRStringFilterFactory INSTANCE = null;

	private static Map<String, Set<String>> FILTER_STRINGS_4_SEED = new HashMap<String, Set<String>>();

	private static Map<String, Set<String>> FILTER_STRINGS_4_OUTPUT = new HashMap<String, Set<String>>();

	private static List<String> FILTER_FILES_PATH = new ArrayList<String>();

	private ULRStringFilterFactory() {
		FILTER_FILES_PATH.add("conf/filters/output");
		FILTER_FILES_PATH.add("conf/filters/seed");
		for (String path : FILTER_FILES_PATH) {
			if (FileUtils.isExist(path)) {
				List<File> files = FileUtils.listFiles(new File(path));
				if (null == files || files.size() == 0) {
					continue;
				}
				for (File file : files) {
					String domain = file.getName();
					List<String> list = FileUtils.readTxtFile(file.getAbsolutePath());
					if (null == domain || null == file || domain.trim().length() == 0 || list.size() == 0) {
						continue;
					}
					Set<String> set = new HashSet<String>();
					set.addAll(list);
					if (path.equals("conf/filters/seed")) {
						FILTER_STRINGS_4_SEED.put(domain, set);
					} else if (path.equals("conf/filters/output")) {
						FILTER_STRINGS_4_OUTPUT.put(domain, set);
					}
				}
			}
		}
	}

	public static synchronized ULRStringFilterFactory getInstance() {
		if (null == INSTANCE) {
			INSTANCE = new ULRStringFilterFactory();
		}
		return INSTANCE;
	}

	public URLStringFilter getFilter(String domain, String type) {
		URLStringFilter filter = null;
		if (null == domain || domain.trim().length() == 0 || null == type || type.trim().length() == 0) {
			return null;
		}
		if (URLStringFilter.SEED.equals(type) || URLStringFilter.OUTPUT.equals(type)) {
			Set<String> filterLinks = null;
			if (URLStringFilter.SEED.equals(type)) {
				filterLinks = FILTER_STRINGS_4_SEED.get(domain);
			} else if (URLStringFilter.OUTPUT.equals(type)) {
				filterLinks = FILTER_STRINGS_4_OUTPUT.get(domain);
			}
			if (null == filterLinks) {

			} else {
				filter = new URLStringFilter();
				filter.setFilters(filterLinks);
			}
		}
		return filter;
	}

}
