package org.cabbage.crawler.reaper.coordinator.das.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.cabbage.commons.utils.json.Json;
import org.hibernate.criterion.Criterion;

@SuppressWarnings("unchecked")
/**
 * Common Utils
 */
public class U {

	private static Map<String, SimpleDateFormat> dateFormatters = Collections
			.synchronizedMap(new HashMap<String, SimpleDateFormat>());

	public static Double pointData(Double value) {
		DecimalFormat df = new DecimalFormat("#0.000");
		return value != null ? new Double(df.format(value)) : null;
	}

	public static Json typeConversion(Json input, Class<?> clazz) {
		Object bean = input.toBean(clazz);
		Json result = new Json(input);
		for (Map.Entry<String, Object> entry : input.entrySet()) {
			try {
				Object value = entry.getValue();
				String key = entry.getKey();
				if (value == null || value instanceof Criterion
						|| value instanceof List
						|| value instanceof BaseCriteria) {
					continue;
				}
				if (PropertyUtils.getPropertyDescriptor(bean, key) != null) {
					Object obj = PropertyUtils.getProperty(bean, key);
					result.put(key, obj);
				} else {
					result.remove(key);
				}
			} catch (Exception e) {

			}
		}
		return result;
	}

	public static Json e() {
		return new Json();
	}

	public static Json e(String key, Object value) {
		return new Json(key, value);
	}

	public static Long toLong(Object obj) {
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

		}

		try {
			return Integer.valueOf(obj.toString()).longValue();
		} catch (Exception e) {

		}

		try {
			return Double.valueOf(obj.toString()).longValue();
		} catch (Exception e) {

		}
		return null;
	}

	public static Integer toInteger(Object obj) {
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

		}

		try {
			return Long.valueOf(obj.toString()).intValue();
		} catch (Exception e) {

		}

		try {
			return Double.valueOf(obj.toString()).intValue();
		} catch (Exception e) {

		}
		return null;
	}

	public static Double toDouble(Object obj) {
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

		}

		try {
			return Integer.valueOf(obj.toString()).doubleValue();
		} catch (Exception e) {

		}

		try {
			return Long.valueOf(obj.toString()).doubleValue();
		} catch (Exception e) {

		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static String concat(List objs, String propName) {
		StringBuffer buffer = new StringBuffer();
		try {
			for (Object o : objs) {
				if (propName != null)
					buffer.append(",").append(
							PropertyUtils.getProperty(o, propName));
				else
					buffer.append(",").append(o.toString());
			}
			if (buffer.length() > 0) {
				return buffer.substring(1);
			}
			return "";
		} catch (Exception e) {
			return "";
		}
	}

	@SuppressWarnings("rawtypes")
	public static List<Long> toLongList(List<?> obj) {
		List<Long> list = new ArrayList(obj.size());
		for (Object o : obj) {
			list.add(U.toLong(o));
		}
		return list;
	}

	public static List<Long> toLongList(Object obj) {
		String[] splits = obj.toString().split(",");
		List<Long> list = new ArrayList<Long>(splits.length);
		for (String s : splits) {
			if (!EmptyUtils.isEmpty(s))
				list.add(U.toLong(s));
		}
		return list;
	}

	public static Date parseDate(String date, String pattern) {
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

	public static String parseDate(Date date, String pattern) {
		SimpleDateFormat sFormat = new SimpleDateFormat(pattern);
		String dateString = sFormat.format(date);
		return dateString;

	}

	public static Long[] toLongArray(Object obj) {
		String[] splits = obj.toString().split(",");
		Long[] result = new Long[splits.length];
		for (int i = 0; i < splits.length; i++) {
			result[i] = U.toLong(splits[i]);
		}
		return result;
	}

	public static Long toLong(Object obj, long defaultValue) {
		Long l = U.toLong(obj);
		if (l == null)
			return defaultValue;
		else
			return l;
	}
}
