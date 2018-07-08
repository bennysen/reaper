package org.cabbage.crawler.reaper.worker.comparator;

import org.cabbage.crawler.reaper.worker.comparator.object.CompareObject;
import org.cabbage.crawler.reaper.worker.comparator.result.CompareResult;

public abstract class Comparator {
	
	public abstract CompareResult compare(CompareObject a,CompareObject b);

}
