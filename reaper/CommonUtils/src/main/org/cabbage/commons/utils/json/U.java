package org.cabbage.commons.utils.json;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cabbage.commons.utils.EmptyUtils;


/**
 * 
 * @author wkshen
 * 
 */
public class U {

	private static Map<String, SimpleDateFormat> dateFormatters = Collections
			.synchronizedMap(new HashMap<String, SimpleDateFormat>());


	public synchronized static Long toLong(Object obj) {
		if (obj == null)
			return null;

		if (obj instanceof Integer) {
			return ((Integer) obj).longValue();
		}

		if (obj instanceof Double) {
			return ((Double) obj).longValue();
		}

		try {
			return Long.valueOf(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			return Integer.valueOf(obj.toString()).longValue();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			return Double.valueOf(obj.toString()).longValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public synchronized static Integer toInteger(Object obj) {
		if (obj == null)
			return null;

		if (obj instanceof Long) {
			return ((Long) obj).intValue();
		}

		if (obj instanceof Double) {
			return ((Double) obj).intValue();
		}

		try {
			return Integer.valueOf(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			return Long.valueOf(obj.toString()).intValue();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			return Double.valueOf(obj.toString()).intValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public synchronized static Double toDouble(Object obj) {
		if (obj == null)
			return null;
		if (obj instanceof Long) {
			return ((Long) obj).doubleValue();
		}

		if (obj instanceof Integer) {
			return ((Integer) obj).doubleValue();
		}

		try {
			return Double.valueOf(obj.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			return Integer.valueOf(obj.toString()).doubleValue();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			return Long.valueOf(obj.toString()).doubleValue();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public synchronized static List<Long> toLongList(List<?> obj) {
		List<Long> list = new ArrayList(obj.size());
		for (Object o : obj) {
			list.add(U.toLong(o));
		}
		return list;
	}

	public synchronized static List<Long> toLongList(Object obj) {
		String[] splits = obj.toString().split(",");
		List<Long> list = new ArrayList<Long>(splits.length);
		for (String s : splits) {
			if (!EmptyUtils.isEmpty(s))
				list.add(U.toLong(s));
		}
		return list;
	}

	public synchronized static Date parseDate(String date, String pattern) {
		SimpleDateFormat format = dateFormatters.get(pattern);

		if (format == null) {
			format = new SimpleDateFormat(pattern);
			dateFormatters.put(pattern, format);
		}

		try {
			return format.parse(date);
		} catch (ParseException e) {
			return null;
		}
	}

	public synchronized static String parseDate(Date date, String pattern) {
		SimpleDateFormat sFormat = new SimpleDateFormat(pattern);
		String dateString = sFormat.format(date);
		return dateString;

	}

	public synchronized static Long[] toLongArray(Object obj) {
		String[] splits = obj.toString().split(",");
		Long[] result = new Long[splits.length];
		for (int i = 0; i < splits.length; i++) {
			result[i] = U.toLong(splits[i]);
		}
		return result;
	}

	public synchronized static Long toLong(Object obj, long defaultValue) {
		Long l = U.toLong(obj);
		if (l == null) {
			return defaultValue;
		} else {
			return l;
		}
	}
}
