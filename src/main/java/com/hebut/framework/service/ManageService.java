package com.hebut.framework.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import com.hebut.framework.model.entity.FwkGroup;
import com.hebut.framework.model.entity.FwkUser;
import com.hebut.framework.service.ManageService;

@Service("manageService")
public class ManageService extends BaseService {

	public FwkUser findByLoginNameAndPassword(String loginName, String password) {
		String queryString = "from FwkUser u where u.fuLoginName=? and u.fuPassword=?";
		FwkUser user = (FwkUser) super.hibernateTemplate.find(queryString, new Object[] { loginName, password }).get(0);
		for (FwkGroup group : user.getGroups()) {
			group.setChildren(null);
		}
		return user;
	}

	@SuppressWarnings("unchecked")
	public List<FwkGroup> findRootGroup(FwkGroup selectedGroup) {
		String queryString = "from FwkGroup where fgPid is null order by fgName";
		Session session = super.hibernateTemplate.getSessionFactory().getCurrentSession();
		session.beginTransaction();
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
		Session session = super.hibernateTemplate.getSessionFactory().getCurrentSession();
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

	@SuppressWarnings("unchecked")
	public List<FwkUser> findUsersByGroup(int pageNo, int pageSize, FwkGroup group) {
		String queryString = "select u from FwkUser u inner join u.groups g where g.fgId=?";
		Session session = super.hibernateTemplate.getSessionFactory().getCurrentSession();
		session.beginTransaction();
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

	@SuppressWarnings("unchecked")
	public List<FwkUser> findUsersByRoleKey(int pageNo, int pageSize, String roleKey) {
		String queryString = "select u from FwkUser u where u.fuAuthority like '%" + roleKey + "%'";
		Session session = super.hibernateTemplate.getSessionFactory().getCurrentSession();
		session.beginTransaction();
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
