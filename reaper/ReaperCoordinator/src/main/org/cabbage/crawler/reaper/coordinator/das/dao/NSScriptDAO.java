package org.cabbage.crawler.reaper.coordinator.das.dao;

import org.cabbage.crawler.reaper.coordinator.das.bean.NSScriptBean;
import org.springframework.stereotype.Component;

@Component(value = "NSScriptDAO")
public class NSScriptDAO extends BaseDAO<NSScriptBean> {

	@Override
	protected Class<NSScriptBean> getEntityClass() {
		return NSScriptBean.class;
	}
}
