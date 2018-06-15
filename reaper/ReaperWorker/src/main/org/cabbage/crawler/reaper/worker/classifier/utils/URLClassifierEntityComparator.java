package org.cabbage.crawler.reaper.worker.classifier.utils;

import java.util.Comparator;

/**
 * URL分类数量排序
 * 
 * @author wkshen
 *
 */
public class URLClassifierEntityComparator implements Comparator<Object> {
	public int compare(Object o1, Object o2) {
		if (null != o1 && null != o2) {
			if (o1 == o2) {
				return 0;
			}
			URLClassifierEntity u1 = (URLClassifierEntity) o1;
			URLClassifierEntity u2 = (URLClassifierEntity) o2;
			if (u1.getSize() < u2.getSize()) {
				return 1;
			} else {
				return -1;
			}
		}
		return -1;
	}
}
