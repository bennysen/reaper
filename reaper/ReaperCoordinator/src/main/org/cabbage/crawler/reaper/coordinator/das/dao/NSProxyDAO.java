package org.cabbage.crawler.reaper.coordinator.das.dao;

import org.cabbage.crawler.reaper.coordinator.das.bean.NSProxyBean;
import org.springframework.stereotype.Component;

@Component(value = "NSProxyDAO")
public class NSProxyDAO extends BaseDAO<NSProxyBean>{


	@Override
	protected Class<NSProxyBean> getEntityClass() {
		return NSProxyBean.class;
	}
}
