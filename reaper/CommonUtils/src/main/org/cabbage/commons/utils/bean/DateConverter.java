package org.cabbage.commons.utils.bean;

import java.text.SimpleDateFormat;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

/**
 * 
 * @author wkshen
 * 
 */
class DateConverter implements Converter {

	static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// HH:mm:ss

	public DateConverter() {
	}

	@SuppressWarnings("rawtypes")
	public Object convert(Class type, Object value) {
		if (value instanceof java.util.Date) {
			return value;
		}
		if (value == null) {
			return null;
		}
		if (((String) value).trim().length() == 0) {
			return null;
		}
		String date = (String) value;
		if (date.length() == 7) {
			date = date + "-01";
		}
		if (value instanceof String) {
			try {
				return df.parse(date);
			} catch (Exception ex) {
				throw new ConversionException("输入的日期类型不合乎yyyy-MM-dd HH:mm:ss" + value.getClass());
			}
		} else {
			throw new ConversionException("输入的不是字符类型" + value.getClass());
		}
	}

}