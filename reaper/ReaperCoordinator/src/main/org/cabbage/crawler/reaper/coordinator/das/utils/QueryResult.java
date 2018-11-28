package org.cabbage.crawler.reaper.coordinator.das.utils;

import java.util.ArrayList;
import java.util.List;

import org.cabbage.commons.utils.json.Json;


public class QueryResult<T> {

	private List<T> resultlist;

	private Long totalrecord;

	public QueryResult() {

	}

	public QueryResult(List<T> list, Long total) {
		this.resultlist = list;
		this.totalrecord = total;
	}

	public List<T> getResultlist() {
		return resultlist;
	}

	public void setResultlist(List<T> resultlist) {
		this.resultlist = resultlist;
	}

	public Long getTotalrecord() {
		return totalrecord;
	}

	public void setTotalrecord(Long totalrecord) {
		this.totalrecord = totalrecord;
	}

	public QueryResult<Json> toJson() {
		QueryResult<Json> result = new QueryResult<Json>();
		List<Json> list = new ArrayList<Json>();
		for (T t : resultlist) {
			list.add(new Json(t));
		}
		result.setResultlist(list);
		result.setTotalrecord(totalrecord);
		return result;
	}

}
