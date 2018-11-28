package org.cabbage.crawler.reaper.coordinator.das;

import org.cabbage.commons.utils.json.Json;
import org.cabbage.crawler.reaper.coordinator.das.bean.NSDomainBean;
import org.cabbage.crawler.reaper.coordinator.das.bean.ReaperTaskBean;
import org.cabbage.crawler.reaper.coordinator.das.dao.NSDomainDAO;
import org.cabbage.crawler.reaper.coordinator.das.utils.QueryResult;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Test {

	public static void main(String[] args) {
		ApplicationContext ctx = new FileSystemXmlApplicationContext("conf/applicationContext.xml");
		NSDomainDAO dao = (NSDomainDAO) ctx.getBean("NSDomainDAO");
		Json j = new Json();
		j.a("ID", new Long(1358));
		
		Json j2 = new Json();
		j2.a("ID", "DESC");
		QueryResult<NSDomainBean> task = dao.find(j, j2, 0, 10);
		if (null == task) {
			System.out.println("null");
		} else {
			System.out.println("Totalrecord:"+task.getTotalrecord());
			for(NSDomainBean b:task.getResultlist()) {
				System.out.println(b.getICP_NO());
				System.out.println(b.getDomain());
			}
		}
	}

}
