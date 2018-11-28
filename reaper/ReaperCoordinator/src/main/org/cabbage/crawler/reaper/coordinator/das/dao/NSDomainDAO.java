package org.cabbage.crawler.reaper.coordinator.das.dao;

import org.cabbage.crawler.reaper.coordinator.das.bean.NSDomainBean;
import org.springframework.stereotype.Component;

@Component(value = "NSDomainDAO")
public class NSDomainDAO extends BaseDAO<NSDomainBean>{


	@Override
	protected Class<NSDomainBean> getEntityClass() {
		return NSDomainBean.class;
	}
}
