package org.cabbage.crawler.reaper.coordinator.das.utils;

import org.hibernate.criterion.Criterion;

public class BaseCriteria {

	protected Criterion criterion;

	public Criterion getCriterion() {
		return criterion;
	}

}
