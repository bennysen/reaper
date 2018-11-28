package org.cabbage.commons.utils.json;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.beanutils.PropertyUtils;
import org.cabbage.commons.utils.EmptyUtils;
import org.cabbage.commons.utils.bean.BeanUtils;

import com.google.gson.Gson;

/**
 * 
 * @author wkshen
 * 
 */
public class Json extends LinkedHashMap<String, Object> {

	private static final long serialVersionUID = -1749950892164263962L;

	public Json(String key, Object value) {
		this.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public Json(String jsonString) {
		Gson gson = new Gson();
		this.putAll(gson.fromJson(jsonString, HashMap.class));
	}

	public Json() {

	}

	public Json(Json json) {
		if (json == null)
			return;

		for (Map.Entry<String, Object> entry : json.entrySet()) {
			this.a(entry.getKey(), entry.getValue());
		}
	}

	@SuppressWarnings("unchecked")
	public Json(Object obj) {
		try {
			Map map = BeanUtils.getBeanUtils().describe(obj);

			for (Object s : map.keySet()) {
				String p = (String) s;
				if ("class".equals(p)) {
					continue;
				}
				this.a(p, PropertyUtils.getProperty(obj, p));
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public Json(Json data, String includeProperties) {
		if (data == null) {
			return;
		}

		for (String id : includeProperties.split(" |,")) {
			if (data.get(id) != null && !"".equals(id.trim()))
				if (data.get(id) instanceof Double) {
					this.put(id, data.l(id));
				} else {
					this.put(id, data.get(id));
				}

		}
	}

	public Json a(String key, Object value) {
		this.put(key, value);
		return this;
	}

	public Json al(String key, List<Json> value) {
		put(key, value);
		return this;
	}

	@Deprecated
	@SuppressWarnings("unchecked")
	public List list(String key, Class<?> clazz) {
		List list = null;
		if (this.containsKey(key)) {
			String value = s(key);
			// System.out.println(value);
			value = fixJsonString(value);
			// System.out.println(value);
			JSONArray ja = JSONArray.fromObject(value);

			// JSONArray ja = JSONArray.fromObject(value);
			if (null != ja && ja.size() > 0) {
				list = new ArrayList();
				Iterator<JSONObject> i = ja.iterator();
				while (i.hasNext()) {
					JSONObject jo = i.next();
					// Json j = new Json(jo.toString());
					Object o = JSONObject.toBean(jo, clazz);
					if (null != o) {
						list.add(o);
					}
				}
			}
		}
		return list;
	}

	private String fixJsonString(String json) {
		StringBuffer sb = new StringBuffer();
		if (null == json) {
			return null;
		}
		char[] array = json.toCharArray();
		if (null != array) {
			for (int x = -1, y = 0, z = 1; y < array.length; x++, y++, z++) {
				char cx, cy, cz;
				cy = array[y];
				if (y == 0 || y == (array.length - 1)) {
					sb.append(cy);
					continue;
				}
				cx = array[x];
				cz = array[z];
				if (cy == '{') {
					sb.append(cy);
					if (cz != '"') {
						sb.append('"');
					}
				} else if (cy == '}') {
					if (cx != '"') {
						sb.append('"');
					}
					sb.append(cy);
				} else if (cy == '=') {
					if (cx != '"') {
						sb.append('"');
					}
					sb.append(cy);
					if (cz != '"') {
						sb.append('"');
					}
				} else if (cy == ',') {
					if (cx == '}') {

					} else if (cx != '"') {
						sb.append('"');
					}
					sb.append(cy);
				} else if (cx == ',' && cy == ' ' && cz == '{') {
					sb.append(cy);
				} else if (cx == ',' && cy == ' ' && cz != '"') {
					sb.append(cy);
					sb.append('"');
				} else if (cy == ':') {
					int xx = y - 3;
					int zz = y + 3;
					if (xx > 0 && xx < zz && zz > 0 && zz < (json.length())) {
						String sub = json.substring(xx, zz);
						if ("ttp://".equals(sub) || "ftp://".equals(sub)
								|| "TTP://".equals(sub) || "FTP://".equals(sub)) {
							sb.append(cy);
							continue;
						}
					}

					xx = y - 3;
					zz = y + 5;
					if (xx > 0 && xx < zz && zz > 0 && zz < (json.length())) {
						String sub = json.substring(xx, zz);
						if ("ttp:\\/\\/".equals(sub)
								|| "ftp:\\/\\/".equals(sub)
								|| "TTP:\\/\\/".equals(sub)
								|| "FTP:\\/\\/".equals(sub)) {
							sb.append(cy);
							continue;
						}
					}

					if (cx != '"') {
						sb.append('"');
					}
					sb.append(cy);
					if (cz != '"') {
						sb.append('"');

					}
				} else {
					sb.append(cy);
				}
			}
		}
		return sb.toString().replaceAll("\"null\"", "null");
	}

	public Long l(String name) {
		return U.toLong(this.o(name));
	}

	public Object o(String name) {
		return this.get(name);
	}

	public Double d(String name) {
		return U.toDouble(this.o(name));
	}

	public String s(String name) {
		if (EmptyUtils.isEmpty(this.get(name)))
			return "";
		else
			return this.get(name).toString();
	}

	public Integer i(String name) {
		return U.toInteger(this.o(name));
	}

	public Boolean b(String name) {
		if (this.get(name) == null)
			return null;

		if (!EmptyUtils.isEmpty(this.l(name)) && this.l(name).equals(1l))
			return true;
		return Boolean.parseBoolean(this.s(name));
	}

	public Long[] toLongArray(String name) {
		return U.toLongArray(this.s(name));
	}

	public List<Long> toLongList(String name) {
		Object o = this.o(name);
		if (o instanceof List<?>) {
			return U.toLongList((List<?>) o);
		}
		return U.toLongList(this.s(name));
	}

	public Object toBean(Class<?> clazz) {
		try {
			Object o = clazz.newInstance();
			BeanUtils.getBeanUtils().copyProperties(o, this);
			return o;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object toSimpleBean(Class<?> clazz) {
		try {
			Object o = clazz.newInstance();
			BeanUtils.getBeanUtils().copyProperties(o, this);
			return o;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Date date(String name, String pattern) {
		Object o = this.o(name);
		if (o instanceof Date) {
			return (Date) o;
		}
		return U.parseDate(o.toString(), pattern);
	}

	@Deprecated
	public Json removeEmpty(String props, Object e) {

		for (String id : props.split(" |,")) {
			if (this.o(id) == null) {
				this.remove(id);
				continue;
			}
			if (this.o(id).equals(e)) {
				this.remove(id);
				continue;
			}
		}
		return this;
	}

	@Deprecated
	public Json removeProps(String props) {
		for (String id : props.split(" |,")) {
			this.remove(id);
		}
		return this;
	}

	public String toString() {
		return org.json.simple.JSONObject.toJSONString(this);
	}

	public static void main(String[] args) {}
}
