package org.cabbage.crawler.reaper.commons.comparator;

import org.cabbage.crawler.reaper.commons.comparator.object.CompareObject;
import org.cabbage.crawler.reaper.commons.comparator.result.CompareResult;

public abstract class Comparator {
	
	public abstract CompareResult compare(CompareObject a,CompareObject b);

}
