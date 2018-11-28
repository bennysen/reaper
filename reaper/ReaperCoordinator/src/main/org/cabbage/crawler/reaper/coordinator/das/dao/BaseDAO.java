package org.cabbage.crawler.reaper.coordinator.das.dao;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Table;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cabbage.commons.utils.json.Json;
import org.cabbage.crawler.reaper.coordinator.das.utils.BaseCriteria;
import org.cabbage.crawler.reaper.coordinator.das.utils.ConnectObject;
import org.cabbage.crawler.reaper.coordinator.das.utils.QueryResult;
import org.cabbage.crawler.reaper.coordinator.das.utils.U;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.jdbc.Work;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

public abstract class BaseDAO<T> extends HibernateDaoSupport {

	private static final Log LOGGER = LogFactory.getLog(BaseDAO.class);

	protected abstract Class<T> getEntityClass();

	@Autowired
	public void setSessionFactoryOverride(SessionFactory sessionFactory) {
		super.setSessionFactory(sessionFactory);
	}

	@Transactional(readOnly = true)
	public QueryResult<T> find(Json query, Json order, int start, int limit) {

		DetachedCriteria criteria = getFindCriteria(query, order, start, limit);

		if (limit < 0 || start < 0) {
			limit = Integer.MAX_VALUE;
		}
		if (start < 0)
			start = 0;

		List<?> result = this.getHibernateTemplate().findByCriteria(criteria, start, limit);

		List<T> list = this.trans(result);
		criteria.setProjection(Projections.rowCount());
		Long totalCount = (Long) criteria.setProjection(Projections.rowCount())
				.getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession())
				.uniqueResult();

		return new QueryResult<T>(list, totalCount);
	}

	private List<T> trans(List<?> result) {
		List<T> list = new ArrayList<T>();
		if (null == result || result.size() == 0) {

		} else {
			for (Object o : result) {
				Class<T> clazz = getEntityClass();
				if (clazz != null) {
					if (clazz.isInstance(o)) {
						list.add(clazz.cast(o));
					}
				}
			}
		}
		return list;
	}

	private DetachedCriteria getFindCriteria(Json query, Json order, int start, int limit) {
		DetachedCriteria criteria = DetachedCriteria.forClass(this.getEntityClass());
		addCriteria(criteria, this.getEntityClass(), query, order, start, limit);
		return criteria;
	}

	@SuppressWarnings("rawtypes")
	private void addCriteria(DetachedCriteria criteria, Class entityClass, Json query, Json order, int start,
			int limit) {
		if (query == null) {
			query = new Json();
		}
		if (order == null) {
			order = new Json();
		}
		query = U.typeConversion(query, entityClass);

		for (Map.Entry<String, Object> entry : query.entrySet()) {
			Object value = entry.getValue();
			if (value instanceof ConnectObject) {
				ConnectObject connect = (ConnectObject) value;
				criteria.add(connect.getCriterion(entry.getKey()));
			} else if (value instanceof BaseCriteria) {
				criteria.add(((BaseCriteria) value).getCriterion());
			} else if (value instanceof Criterion) {
				criteria.add((Criterion) value);
			} else if (value instanceof List) {
				criteria.add(Restrictions.in(entry.getKey(), (List) entry.getValue()));
			} else if (value == null) {
				criteria.add(Restrictions.isNull(entry.getKey()));
			} else {
				criteria.add(Restrictions.eq(entry.getKey(), entry.getValue()));
			}
			// LogicalExpression and = Restrictions.and(null, null);
		}

		for (Map.Entry<String, Object> e : order.entrySet()) {
			if (e.getValue().equals("desc"))
				criteria.addOrder(Order.desc(e.getKey()));
			else
				criteria.addOrder(Order.asc(e.getKey()));
		}
	}

	@Transactional
	public void delete(T obj) {
		this.getHibernateTemplate().delete(obj);
	}

	@Transactional
	public T update(T entity) {
		this.getHibernateTemplate().merge(entity);
		return entity;
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public T persist(Json entity) {
		T t = (T) entity.toBean(getEntityClass());
		return this.persist(t);
	}

	@Transactional
	public T persist(T entity) {
		this.getHibernateTemplate().persist(entity);
		return entity;
	}

	@Transactional
	public void update(List<T> list, boolean tran) {
		if (list == null || list.size() == 0) {
			return;
		}

		if (tran) {
			batchUpdate(getEntityArray(list));
		} else {
			try {
				batchUpdate(getEntityArray(list));
			} catch (Exception ex) {
				LOGGER.error("Batch insert exception! May exist repeat record! Still Insert and Commit!", ex);
			}
		}
	}

	@Transactional
	public void delete(final List<Json> deleteList) {
		final String tableName = getTableName();

		Json singlequery = deleteList.get(0);
		final Set<String> propertyNames = singlequery.keySet();

		this.getHibernateTemplate().getSessionFactory().getCurrentSession().doWork(new Work() {
			public void execute(Connection conn) throws SQLException {
				// prepare sql
				StringBuffer sql = new StringBuffer();
				sql.append("delete from " + tableName);
				sql.append(" where 1=1 ");

				for (String propertyName : propertyNames) {
					sql.append("and " + propertyName + " = ? ");
				}

				PreparedStatement ps = conn.prepareStatement(sql.toString());
				try {
					for (Json query : deleteList) {
						int index = 1;
						for (String propertyName : propertyNames) {
							Object property = query.get(propertyName);
							if (property instanceof Date) {
								ps.setObject(index, getSqlTimestamp((Date) property));
							} else {
								ps.setObject(index, property);
							}
							index++;
						}
						ps.addBatch();
					}
					ps.executeBatch();
				} catch (Exception ex) {
					throw new SQLException(ex.getMessage());
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	private T[] getEntityArray(List<?> entityList) {
		if (entityList.size() == 0) {
			return null;
		}
		T[] entityArray = (T[]) Array.newInstance(this.getEntityClass(), entityList.size());

		if (entityList.get(0) instanceof Json) {
			List<T> tList = new ArrayList<T>();
			for (Object entity : entityList) {
				tList.add((T) ((Json) entity).toBean(getEntityClass()));
			}
			tList.toArray(entityArray);
		} else {
			entityList.toArray(entityArray);
		}
		return entityArray;
	}

	private String getTableName() {
		Class<?> c = this.getEntityClass();
		Table table = c.getAnnotation(Table.class);
		return table.name();
	}

	@Transactional
	private void batchUpdate(final T[] entityList) {
		final ClassMetadata meta = this.getSessionFactory().getClassMetadata(this.getEntityClass());
		final String[] propertiesName = meta.getPropertyNames();

		final String tableName = getTableName();

		this.getHibernateTemplate().getSessionFactory().getCurrentSession().doWork(new Work() {
			public void execute(Connection conn) throws SQLException {
				// prepare sql
				StringBuffer sql = new StringBuffer();
				sql.append("update " + tableName);
				sql.append(" set ");
				for (String propertyName : propertiesName) {
					sql.append(" " + propertyName + " = ? ,");
				}
				sql.deleteCharAt(sql.length() - 1);
				sql.append(" where " + meta.getIdentifierPropertyName() + " = ? ");
				LOGGER.info(sql);
				PreparedStatement ps = conn.prepareStatement(sql.toString());

				try {
					for (T entity : entityList) {
						int index = 1;
						for (String propertyName : propertiesName) {
							Object property = PropertyUtils.getProperty(entity, propertyName);

							if (property instanceof Date) {
								ps.setObject(index, getSqlTimestamp((Date) property));
							} else {
								ps.setObject(index, property);
								LOGGER.info("[" + index + "][" + property + "]");
							}
							index++;
						}
						Object property = PropertyUtils.getProperty(entity, meta.getIdentifierPropertyName());
						ps.setObject(index, property);
						ps.addBatch();
					}
					ps.executeBatch();
				} catch (Exception e) {
					throw new SQLException(e.getMessage());
				}
			}
		});
	}

	private java.sql.Timestamp getSqlTimestamp(Date date) {
		if (date == null)
			return null;
		return new java.sql.Timestamp(date.getTime());
	}

	@Transactional
	public void persist(List<?> list, boolean tran) {
		if (list == null || list.size() == 0) {
			return;
		}

		if (tran) {
			batchInsert(getEntityArray(list));
		} else {
			try {
				batchInsert(getEntityArray(list));
			} catch (Exception ex) {
				LOGGER.error("Batch insert exception! May exist repeat record! Still Insert and Commit!", ex);
			}
		}
	}

	@Transactional
	private void batchInsert(final T[] entityList) {
		final ClassMetadata meta = this.getSessionFactory().getClassMetadata(this.getEntityClass());
		final String[] propertiesName = meta.getPropertyNames();

		final String tableName = getTableName();

		this.getHibernateTemplate().getSessionFactory().getCurrentSession().doWork(new Work() {
			public void execute(Connection conn) throws SQLException {
				// prepare sql
				StringBuffer sql = new StringBuffer();
				sql.append("insert into " + tableName);
				sql.append(" ( " + meta.getIdentifierPropertyName());
				for (String propertyName : propertiesName) {
					sql.append("," + propertyName);
				}
				sql.append(" )values( ");
				sql.append("null");
				for (int i = 0; i < propertiesName.length; i++) {
					sql.append(",?");
				}
				sql.append(")");

				PreparedStatement ps = conn.prepareStatement(sql.toString());

				try {
					for (T entity : entityList) {
						int index = 1;
						for (String propertyName : propertiesName) {
							Object property = PropertyUtils.getProperty(entity, propertyName);

							if (property instanceof Date) {
								ps.setObject(index, getSqlTimestamp((Date) property));
							} else {
								ps.setObject(index, property);
							}
							index++;
						}
						ps.addBatch();
					}
					ps.executeBatch();
				} catch (Exception e) {
					throw new SQLException(e.getMessage());
				}
			}
		});
	}
}
