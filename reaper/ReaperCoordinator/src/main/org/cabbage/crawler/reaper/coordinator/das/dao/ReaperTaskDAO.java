package org.cabbage.crawler.reaper.coordinator.das.dao;

import org.cabbage.crawler.reaper.coordinator.das.bean.ReaperTaskBean;
import org.springframework.stereotype.Component;

@Component(value = "ReaperTaskDAO")
public class ReaperTaskDAO extends BaseDAO<ReaperTaskBean>{


	@Override
	protected Class<ReaperTaskBean> getEntityClass() {
		return ReaperTaskBean.class;
	}
}
