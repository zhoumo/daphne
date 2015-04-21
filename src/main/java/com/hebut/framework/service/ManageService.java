package com.hebut.framework.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import com.hebut.framework.entity.FwkGroup;
import com.hebut.framework.entity.FwkUser;
import com.hebut.framework.service.ManageService;
import com.hebut.framework.util.CacheUtil;

@Service("manageService")
public class ManageService extends BaseService {

	public FwkUser findByLoginNameAndPassword(String loginName, String password) {
		FwkUser user = (FwkUser) this.loadEntity(CacheUtil.getCacheKey(FwkUser.class, loginName), null);
		if (user != null) {
			if (!user.getFuPassword().equals(password)) {
				user = null;
			}
		} else {
			String queryString = "from FwkUser u where u.fuLoginName=? and u.fuPassword=?";
			user = (FwkUser) getHibernateTemplate().find(queryString, new Object[] { loginName, password }).get(0);
		}
		for (FwkGroup group : user.getGroups()) {
			group.setChildren(null);
		}
		return user;
	}

	public List<FwkGroup> findRootGroup(FwkGroup selectedGroup) {
		String queryString = "from FwkGroup where fgPid is null order by fgName";
		Session session = this.hibernateTemplate.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		@SuppressWarnings("unchecked")
		List<FwkGroup> groupList = session.createQuery(queryString).list();
		this.initialize(groupList, selectedGroup);
		session.beginTransaction().commit();
		return groupList;
	}

	private void initialize(List<FwkGroup> groupList, FwkGroup selectedGroup) {
		for (FwkGroup group : groupList) {
			if (!Hibernate.isInitialized(group)) {
				Hibernate.initialize(group);
			}
			if (selectedGroup != null && group.getFgId().longValue() == selectedGroup.getFgId().longValue()) {
				group.setSelected(true);
			} else {
				group.setSelected(false);
			}
			this.initialize(group.getChildren(), selectedGroup);
		}
	}

	public List<FwkGroup> buildGroupTree() {
		String queryString = "from FwkGroup where fgPid is null order by fgName";
		Session session = this.hibernateTemplate.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Query query = session.createQuery(queryString);
		List<FwkGroup> groupList = new ArrayList<FwkGroup>();
		for (Object object : query.list().toArray()) {
			this.getChildrenGroup((FwkGroup) object, groupList);
		}
		session.beginTransaction().commit();
		return groupList;
	}

	private void getChildrenGroup(FwkGroup parentGroup, List<FwkGroup> groupList) {
		groupList.add(parentGroup);
		for (FwkGroup group : parentGroup.getChildren()) {
			this.getChildrenGroup(group, groupList);
		}
	}

	public int countUsersByGroup(FwkGroup group) {
		String queryString = "select count(*) from FwkUser u inner join u.groups g where g.fgId=?";
		return count(queryString, group.getFgId());
	}

	public List<FwkUser> findUsersByGroup(int pageNo, int pageSize, FwkGroup group) {
		String queryString = "select u from FwkUser u inner join u.groups g where g.fgId=?";
		Session session = this.hibernateTemplate.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		@SuppressWarnings("unchecked")
		List<FwkUser> userList = session.createQuery(queryString).setParameter(0, group.getFgId()).setMaxResults(pageSize).setFirstResult(pageNo * pageSize).list();
		for (FwkUser user : userList) {
			user.setCurrentGroup(group.getFgId().toString());
		}
		session.beginTransaction().commit();
		return userList;
	}

	public int countUsersByRoleKey(String roleKey) {
		String queryString = "select count(*) from FwkUser u where u.fuAuthority like '%" + roleKey + "%'";
		return count(queryString);
	}

	public List<FwkUser> findUsersByRoleKey(int pageNo, int pageSize, String roleKey) {
		String queryString = "select u from FwkUser u where u.fuAuthority like '%" + roleKey + "%'";
		Session session = this.hibernateTemplate.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		@SuppressWarnings("unchecked")
		List<FwkUser> userList = session.createQuery(queryString).setMaxResults(pageSize).setFirstResult(pageNo * pageSize).list();
		for (FwkUser user : userList) {
			user.setCurrentRoleKey(roleKey);
		}
		session.beginTransaction().commit();
		return userList;
	}

	public boolean existUserByLoginName(String loginName) {
		String queryString = "select count(*) from FwkUser u where u.fuLoginName=?";
		if (count(queryString, loginName) == 0) {
			return false;
		} else {
			return true;
		}
	}
}
