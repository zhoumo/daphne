package mine.daphne.service;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

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

	public <T> T loadById(Class<T> clazz, Serializable id) {
		return (T) this.hibernateTemplate.get(clazz, id);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> listAll(Class<T> clazz) {
		return this.hibernateTemplate.find("from " + clazz.getName());
	}

	public void saveOrUpdate(Object entity) {
		this.hibernateTemplate.saveOrUpdate(entity);
	}

	public void delete(Object entity) {
		this.hibernateTemplate.delete(entity);
	}

	public int count(final String queryString, final Object... object) {
		return ((Long) this.hibernateTemplate.execute(new HibernateCallback<Object>() {

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
		return this.hibernateTemplate.executeFind(new HibernateCallback<Object>() {

			public Object doInHibernate(Session session) throws HibernateException, SQLException {
				Query query = session.createQuery(queryString);
				for (int i = 0; i < object.length; i++) {
					query.setParameter(i, object[i]);
				}
				return query.setMaxResults(pageSize).setFirstResult(pageNo * pageSize).list();
			}
		});
	}
}
