package org.cabbage.crawler.reaper.commons.filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 组合过滤器
 * 
 * @author wkshen
 *
 */
public class ComplexFilter extends AbstractFilter {

	//过滤器集合
	private List<AbstractFilter> filterList = new ArrayList<AbstractFilter>();

	/**
	 * 添加过滤器
	 * @param filter 过滤器
	 * @return 当前组合过滤器引用
	 */
	public ComplexFilter addFilter(AbstractFilter filter) {
		if (null == filter) {

		} else {
			if (filter instanceof ComplexFilter) {

			} else {
				filterList.add(filter);
			}
		}
		return this;
	}

	/**
	 * 清空过滤器集合
	 */
	public void clearFilter() {
		filterList.clear();
	}

	@Override
	public Set<String> filter() {
		Set<String> result = new HashSet<String>();
		List<Set<String>> sets = new ArrayList<Set<String>>();
		if (null == filterList || filterList.size() == 0) {
			return null;
		}
		for (AbstractFilter filter : filterList) {
			Set<String> set = filter.filter();
			if (null == set || set.size() == 0) {
				continue;
			}
			sets.add(set);
		}

		if (sets.size() == 0) {
			return result;
		} else if (sets.size() == 1) {
			result = sets.get(0);
		} else {
			result = sets.get(0);
			for (int i = 1; i < sets.size(); i++) {
				Set<String> s = sets.get(i);
				result.retainAll(s);
			}
		}

		return result;
	}

	public static void main(String[] args) {
		ComplexFilter sf = new ComplexFilter().addFilter(null);
		sf.filter();
	}

}
