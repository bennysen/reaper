package org.cabbage.crawler.reaper.coordinator.das.utils;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

public class ConnectObject extends BaseCriteria {

	
	private Class<?> entity;
	private String id;
	private String parentId;
	private String value;
	private Criterion[] criterions;

	public ConnectObject(Class<?> entity, String id, String parentId,
			Object value, Criterion[] criterions) {
		this.entity = entity;
		this.id = id;
		this.parentId = parentId;
		this.value = value.toString();
		this.criterions = criterions;
	}

	public ConnectObject(Class<?> entity, String id, String parentId,
			Object value) {
		this.entity = entity;
		this.id = id;
		this.parentId = parentId;
		this.value = value.toString();
	}

	public Criterion getCriterion(String key) {
		DetachedCriteria cc = DetachedCriteria.forClass(this.getEntity());
		cc.setProjection(Property.forName(this.getId()));
		String cid = this.getId();
		String cPid = this.getParentId();

		String alias = cc.getAlias();
		cc.add(Restrictions.sqlRestriction(" 1=1 connect by prior " + alias
				+ "_." + cid + "=" + alias + "_." + cPid));

		if (this.criterions != null) {
			for (Criterion x : this.criterions)
				cc.add(x);
		}

		cc.add(Restrictions.sqlRestriction(" 1=1 start with " + alias + "_."
				+ cid + "=" + this.getValue()));

		return Property.forName(key).in(cc);
	}

	public Class<?> getEntity() {
		return entity;
	}

	public String getId() {
		return id;
	}

	public String getParentId() {
		return parentId;
	}

	public String getValue() {
		return value;
	}

}
