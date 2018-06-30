package org.cabbage.crawler.reaper.worker.filter;

import java.util.Set;

/**
 * 过滤器抽象类
 * 
 * @author wkshen
 *
 */
public abstract class AbstractFilter {

	public abstract Set<String> filter();

	public static void main(String[] args) {
		AbstractFilter f = new ComplexFilter();
		f.filter();
	}

}
