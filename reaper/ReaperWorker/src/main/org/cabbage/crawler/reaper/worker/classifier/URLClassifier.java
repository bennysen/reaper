package org.cabbage.crawler.reaper.worker.classifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.cabbage.crawler.reaper.worker.classifier.utils.URLClassifierEntityComparator;
import org.cabbage.crawler.reaper.worker.classifier.utils.LevenshteinDistance;
import org.cabbage.crawler.reaper.worker.classifier.utils.URLClassifierEntity;

/**
 * URL格式分类器
 * 
 * @author wkshen
 *
 */
public class URLClassifier {

	public static List<URLClassifierEntity> urlCrawledFilter(String taskurl, Set<String> urlSet) {
		List<URLClassifierEntity> urlList = new ArrayList<URLClassifierEntity>();
		if (CollectionUtils.isEmpty(urlSet)) {
			return urlList;
		}
		int total = urlSet.size();
		Set<String> urlSetCopy = new HashSet<String>();
		urlSetCopy.addAll(urlSet);
		// 保存分类过的url
		Set<String> urlSetDis = new HashSet<String>();
		Map<String, String> pd = new HashMap<String, String>();
		// 开始分类 使用字符串相似算法
		for (String key : urlSet) {
			if (pd.containsKey(key)) {
				continue;
			}

			Set<String> urlSetStore = new HashSet<String>();
			for (String key1 : urlSetCopy) {
				if (pd.containsKey(key1)) {
					continue;
				}
				double dis = LevenshteinDistance.sim(key, key1);// 相似度>0.7认为相似
				if (dis > 0.7) {
					urlSetStore.add(key1);
					urlSetDis.add(key1);// 相似的所有url 包括自身
					pd.put(key, key);
				}
			}
			urlSetStore.add(key);
			int size = urlSetStore.size();
			double ratio = (double) size / (double) total;
			URLClassifierEntity uEntity = new URLClassifierEntity();
			uEntity.setFeatureURL(key);
			uEntity.setSize(size);
			uEntity.setOriginalURL(taskurl);
			uEntity.setRatio(FormatDouble(ratio));
			uEntity.setURLSet(urlSetStore);
			urlList.add(uEntity);
		}

		URLClassifierEntityComparator cu = new URLClassifierEntityComparator();
		Collections.sort(urlList, cu);

		return sort(urlList);
	}

	private static List<URLClassifierEntity> sort(List<URLClassifierEntity> src) {

		if (null == src || src.size() == 0) {
			return null;
		}
		List<URLClassifierEntity> dist = new ArrayList<URLClassifierEntity>();
		URLClassifierEntity max = null;
		int s = src.size();
		for (int i = 0; i < s; i++) {
			max = null;
			for (URLClassifierEntity ue : src) {
				if (null == max) {
					max = ue;
				} else {
					if (ue.getSize() > max.getSize()) {
						max = ue;
					}
				}
			}
			src.remove(max);
			dist.add(max);
		}
		return dist;
	}

	private static String FormatDouble(Double ratio) {
		int i = (int) (ratio * 100000);
		ratio = (double) i / 1000;
		return ratio + "%";
	}

	public static void main(String[] args) {
		String url = "http://www.qq.com/";
		// TODO must get the page outlinks
		Set<String> outlinks = new HashSet<String>();
		Set<String> urlSet = new HashSet<String>();
		for (String u : outlinks) {
			if (u.contains(".html")) {
				u = u.substring(0, u.indexOf(".html") + 5);
			} else if (u.contains(".htm")) {
				u = u.substring(0, u.indexOf(".htm") + 4);
			}
			urlSet.add(u);
		}
		List<URLClassifierEntity> uEntityList = URLClassifier.urlCrawledFilter(url, urlSet);
		System.out.println(urlSet.size() + "," + uEntityList.size());
		System.out.println("--------------------");
		for (URLClassifierEntity uEntity : uEntityList) {
			Set<String> uSet = uEntity.getURLSet();
			System.out.println(uEntity.getRatio() + ", size:" + uSet.size() + ", " + uEntity.getFeatureURL());
			for (String s : uSet) {
				System.out.println(s);
			}
			System.out.println("--------------------");
		}
	}

}
