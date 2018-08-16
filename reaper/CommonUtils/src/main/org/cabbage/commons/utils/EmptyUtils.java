package org.cabbage.commons.utils;

import java.util.List;
import java.util.Set;

/**
 * 
 * @author wkshen
 * 
 */
public class EmptyUtils {

	public static boolean isEmpty(List<?> l) {
		if (null == l || l.isEmpty() || 0 == l.size()) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(Set<?> s) {
		if (null == s || s.isEmpty() || 0 == s.size()) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(String s) {
		if (null == s || "".equals(s) || 0 == s.length()) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(Double d) {
		if (null == d || "".equals(d) || d == 0.0) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(String[] s) {
		if (null == s || "".equals(s) || 0 == s.length) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(Integer i) {
		if (null == i || "".equals(i) || 0 == i || -1 == i) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(Long l) {
		if (null == l || "".equals(l) || 0 == l || -1 == l) {
			return true;
		}
		return false;
	}

	public static boolean isEmpty(Object obj) {
		if (null == obj || "".equals(obj)) {
			return true;
		}
		return false;
	}

}
