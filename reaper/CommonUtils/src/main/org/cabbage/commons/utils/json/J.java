package org.cabbage.commons.utils.json;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.cabbage.commons.utils.DateUtils;
import org.cabbage.commons.utils.EmptyUtils;
import org.cabbage.commons.utils.bean.BeanUtils;
import org.json.simple.JSONObject;

import com.google.gson.Gson;

/**
 * 
 * @author wkshen
 * 
 */
public class J extends LinkedHashMap<String, Object> {

	private static final long serialVersionUID = -1749950892164263962L;

	public J(String key, Object value) {
		this.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public J(String jsonString) {
		Gson gson = new Gson();
		this.putAll(gson.fromJson(jsonString, HashMap.class));
	}

	public J() {

	}

	public J(J json) {
		if (json == null)
			return;

		for (Map.Entry<String, Object> entry : json.entrySet()) {
			this.a(entry.getKey(), entry.getValue());
		}
	}

	@SuppressWarnings("unchecked")
	public J(Object obj) {
		try {
			Map map = BeanUtils.getBeanUtils().describe(obj);

			for (Object s : map.keySet()) {
				String p = (String) s;
				if ("class".equals(p)) {
					continue;
				}
				if ("java.util.Date".equals(p)) {
					Date d = (Date) PropertyUtils.getProperty(obj, p);
					String date = DateUtils.getDateTimeString(d);
					this.a(p, date);
				} else {
					this.a(p, PropertyUtils.getProperty(obj, p));
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public J(J data, String includeProperties) {
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

	public J a(String key, Object value) {
		this.put(key, value);
		return this;
	}

	public J al(String key, List<J> value) {
		put(key, value);
		return this;
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

	public Date date(String name, String pattern) {
		Object o = this.o(name);
		if (o instanceof Date) {
			return (Date) o;
		}
		return U.parseDate(o.toString(), pattern);
	}

	public J removeEmpty(String props, Object e) {

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

	public J removeProps(String props) {
		for (String id : props.split(" |,")) {
			this.remove(id);
		}
		return this;
	}

	public String toString() {
		return JSONObject.toJSONString(this);
	}

	public static void main(String[] args) {
		J json = new J();
		json.a("projectID", "123344");
		List<J> list = new ArrayList<J>();
		list.add(new J("id1", "asdfasdfasdf"));
		list.add(new J("id1", "asdfasdfasdf"));
		list.add(new J("id1", "asdfasdfasdf"));
		list.add(new J("id1", "asdfasdfasdf"));

		json.al("ids", list);

		System.out.println(json.toString());

		J j = new J(json.toString());

		System.out.println(j.toString());
	}

}
