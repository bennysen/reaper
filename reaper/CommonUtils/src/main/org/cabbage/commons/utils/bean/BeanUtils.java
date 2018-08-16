package org.cabbage.commons.utils.bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;

/**
 * 
 * @author wkshen
 * 
 */
public class BeanUtils {

	private static BeanUtilsBean beanUtils;

	static {
		beanUtils = new BeanUtilsBean();
		beanUtils.getConvertUtils().register(new DateConverter(),
				java.util.Date.class);
		beanUtils.getConvertUtils().register(new IntegerConverter(null),
				Integer.class);
		beanUtils.getConvertUtils().register(new DoubleConverter(null),
				Double.class);
		beanUtils.getConvertUtils().register(new LongConverter(null),
				Long.class);
	}

	public synchronized static BeanUtilsBean getBeanUtils() {
		return beanUtils;
	}

	/***************************************************************************
	 * 判断属性的类型
	 * 
	 * @param clazz
	 **************************************************************************/
	public synchronized static Map<String, String> getPropertyType(
			Class<?> clazz) {
		Map<String, String> type = new HashMap<String, String>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			Class<?> clazz0 = field.getType();
			if (clazz0 == String.class) {
				type.put(field.getName(), "String");
			} else if (clazz0 == int.class) {
				type.put(field.getName(), "int");
			} else if (clazz0 == Integer.class) {
				type.put(field.getName(), "Integer");
			} else if (clazz0 == long.class) {
				type.put(field.getName(), "long");
			} else if (clazz0 == Long.class) {
				type.put(field.getName(), "Long");
			} else if (clazz0 == float.class) {
				type.put(field.getName(), "float");
			} else if (clazz0 == Float.class) {
				type.put(field.getName(), "Float");
			} else if (clazz0 == double.class) {
				type.put(field.getName(), "double");
			} else if (clazz0 == Double.class) {
				type.put(field.getName(), "Double");
			} else if (clazz0 == boolean.class) {
				type.put(field.getName(), "boolean");
			} else if (clazz0 == Boolean.class) {
				type.put(field.getName(), "Boolean");
			} else if (clazz0 == Date.class) {
				type.put(field.getName(), "Date");
			}
		}
		return type;
	}

	public synchronized static boolean isEquals(Object a, Object b)
			throws IllegalArgumentException, IllegalAccessException {
		boolean isEquals = true;
		if (a.getClass() == b.getClass()) {
			Field[] fas = a.getClass().getDeclaredFields();
			Field[] fbs = b.getClass().getDeclaredFields();
			if (null != fas && fas.length > 0)
				for (int i = 0; i < fas.length; i++) {
					Field fa = fas[i];
					Field fb = fbs[i];
					fa.setAccessible(true);
					fb.setAccessible(true);
					Object va = fa.get(a);
					Object vb = fb.get(b);
					if (va == null && vb == null) {
						continue;
					}else if (va == null && vb != null) {
						return false;
					}else if (va != null && vb == null) {
						return false;
					}
					Class<?> clazz = va.getClass();
					if (clazz == String.class) {
						isEquals = ((String) va).equals((String) vb);
						if (!isEquals) {
							return isEquals;
						}
					} else if (clazz == int.class) {
						isEquals = ((Integer) va).equals((Integer) vb);
						if (!isEquals) {
							return isEquals;
						}
					} else if (clazz == Integer.class) {
						isEquals = ((Integer) va).equals((Integer) vb);
						if (!isEquals) {
							return isEquals;
						}
					} else if (clazz == long.class) {
						isEquals = ((Long) va).equals((Long) vb);
						if (!isEquals) {
							return isEquals;
						}
					} else if (clazz == Long.class) {
						isEquals = ((Long) va).equals((Long) vb);
						if (!isEquals) {
							return isEquals;
						}
					} else if (clazz == float.class) {
						isEquals = ((Float) va).equals((Float) vb);
						if (!isEquals) {
							return isEquals;
						}
					} else if (clazz == Float.class) {
						isEquals = ((Float) va).equals((Float) vb);
						if (!isEquals) {
							return isEquals;
						}
					} else if (clazz == double.class) {
						isEquals = ((Double) va).equals((Double) vb);
						if (!isEquals) {
							return isEquals;
						}
					} else if (clazz == Double.class) {
						isEquals = ((Double) va).equals((Double) vb);
						if (!isEquals) {
							return isEquals;
						}
					} else if (clazz == boolean.class) {
						isEquals = ((Boolean) va).equals((Boolean) vb);
						if (!isEquals) {
							return isEquals;
						}
					} else if (clazz == Boolean.class) {
						isEquals = ((Boolean) va).equals((Boolean) vb);
						if (!isEquals) {
							return isEquals;
						}
					} else if (clazz == Date.class) {
						isEquals = ((Date) va).equals((Date) vb);
						if (!isEquals) {
							return isEquals;
						}
					} else {
						isEquals = isEquals(va, vb);
						if (!isEquals) {
							return isEquals;
						}
					}
				}
		}
		return isEquals;
	}

	/**
	 * @param args
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static void main(String[] args) throws IllegalArgumentException,
			IllegalAccessException {
		TestBean b1 = new TestBean();
		TestBean b2 = new TestBean();
		TestBean b3 = new TestBean();
		b3.setB(false);
		TestBean b4 = new TestBean();
		b4.setB(false);
		
		b1.setB(true);
		b2.setB(true);
		b1.setI(1);
		b2.setI(1);
//		b1.setBean(b3);
//		b2.setBean(b4);
		
		List<String> l1 = new ArrayList<String>();
		List<String> l2 = new ArrayList<String>();
		l1.add("a");
		l2.add("aa");
		
		b1.setList(l1);
		b2.setList(l2);
		
		
		System.out.println(BeanUtils.isEquals(b1, b2));
	}
}
