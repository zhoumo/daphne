package com.hebut.framework.service;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import com.hebut.framework.entity.IEntityCache;
import com.hebut.framework.service.BaseService;
import com.hebut.framework.util.CacheUtil;

import javax.annotation.Resource;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Service;

@Service("baseService")
public class BaseService {

	public HibernateTemplate hibernateTemplate;

	@Resource(name = "hibernateTemplate")
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	public <T> T loadById(Class<T> clazz, Serializable id) {
		return (T) getHibernateTemplate().get(clazz, id);
	}

	@SuppressWarnings("unchecked")
	public <T> List<IEntityCache> listAll(Class<T> clazz) {
		return getHibernateTemplate().find("from " + clazz.getName());
	}

	public void saveOrUpdate(Object entity) {
		getHibernateTemplate().saveOrUpdate(entity);
	}

	public void delete(Object entity) {
		getHibernateTemplate().delete(entity);
	}

	public int count(final String queryString, final Object... object) {
		return ((Long) getHibernateTemplate().execute(new HibernateCallback<Object>() {

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(queryString);
				for (int i = 0; i < object.length; i++) {
					query.setParameter(i, object[i]);
				}
				return query.setMaxResults(1).uniqueResult();
			}
		})).intValue();
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> pageQuery(final int pageNo, final int pageSize, final String queryString, final Object... object) {
		return getHibernateTemplate().executeFind(new HibernateCallback<Object>() {

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(queryString);
				for (int i = 0; i < object.length; i++) {
					query.setParameter(i, object[i]);
				}
				return query.setMaxResults(pageSize).setFirstResult(pageNo * pageSize).list();
			}
		});
	}

	public boolean insertCache(IEntityCache entity, String cacheName) {
		try {
			CacheUtil.getCache(cacheName).put(new Element(entity.getCacheKey(), entity));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean batchInsertCache(Class<?> clazz, List<IEntityCache> entityList, String cacheName) {
		try {
			Cache cache = CacheUtil.getCache(cacheName);
			for (IEntityCache entity : entityList) {
				if (cache.get(entity.getCacheKey()) != null) {
					cache.remove(entity.getCacheKey());
				}
				cache.put(new Element(entity.getCacheKey(), (Serializable) entity));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean removeCache(IEntityCache entity, String cacheName) {
		try {
			Cache cache = CacheUtil.getCache(cacheName);
			Element element = cache.get(entity.getCacheKey());
			if (element != null) {
				cache.remove(entity.getClass().toString());
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean modifyCache(IEntityCache entity, String cacheName) {
		try {
			if (!removeCache(entity, cacheName)) {
				return false;
			}
			if (!insertCache(entity, cacheName)) {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public IEntityCache loadEntity(String key, String cacheName) {
		Element element = CacheUtil.getCache(cacheName).get(key);
		if (element != null) {
			return (IEntityCache) element.getValue();
		} else {
			return null;
		}
	}

	public void saveOrUpdateWithCache(Object entity) {
		this.saveOrUpdate(entity);
		this.modifyCache((IEntityCache) entity, null);
	}
}
