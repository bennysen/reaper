package org.cabbage.crawler.reaper.commons.classifier.utils;

import java.util.Comparator;

/**
 * URL分类数量排序
 * 
 * @author wkshen
 *
 */
public class URLClassifierEntityComparator implements Comparator<Object> {
	public int compare(Object o1, Object o2) {
		if (null == o1 && null != o2) {
			return -1;
		}
		if (null != o1 && null == o2) {
			return 1;
		}
		if (null == o1 && null == o2) {
			return 0;
		}
		if (null != o1 && null != o2) {
			if (o1 == o2) {
				return 0;
			}
			if (o1.getClass() == o2.getClass()) {
				URLClassifierEntity u1 = (URLClassifierEntity) o1;
				URLClassifierEntity u2 = (URLClassifierEntity) o2;
				if (u1.getSize() < u2.getSize()) {
					return 1;
				} else if (u1.getSize() > u2.getSize()) {
					return -1;
				} else {
					return 0;
				}
			} else {
				return 0;
			}
		}
		return 0;
	}
}
