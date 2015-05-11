package mine.daphne.service;

import java.util.ArrayList;
import java.util.List;

import mine.daphne.model.entity.SysGroup;
import mine.daphne.model.entity.SysUser;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("manageService")
@Transactional
public class ManageService extends BaseService {

	public SysUser findByLoginNameAndPassword(String loginName, String password) {
		String queryString = "from SysUser u where u.loginName=? and u.password=?";
		List<?> userList = super.hibernateTemplate.find(queryString, new Object[] { loginName, password });
		if (userList.size() == 0) {
			return null;
		}
		SysUser user = (SysUser) userList.get(0);
		for (SysGroup group : user.getGroups()) {
			group.setChildren(null);
		}
		return user;
	}

	@SuppressWarnings("unchecked")
	public List<SysGroup> findAllGroup() {
		String queryString = "from SysGroup order by name";
		return super.hibernateTemplate.find(queryString);
	}

	@SuppressWarnings("unchecked")
	public List<SysGroup> findRootGroup(SysGroup selectedGroup) {
		String queryString = "from SysGroup where pid is null order by name";
		Session session = super.hibernateTemplate.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<SysGroup> groupList = session.createQuery(queryString).list();
		this.initialize(groupList, selectedGroup);
		session.beginTransaction().commit();
		return groupList;
	}

	private void initialize(List<SysGroup> groupList, SysGroup selectedGroup) {
		for (SysGroup group : groupList) {
			if (!Hibernate.isInitialized(group)) {
				Hibernate.initialize(group);
			}
			if (selectedGroup != null && group.getId().longValue() == selectedGroup.getId().longValue()) {
				group.setSelected(true);
			} else {
				group.setSelected(false);
			}
			this.initialize(group.getChildren(), selectedGroup);
		}
	}

	public List<SysGroup> buildGroupTree() {
		String queryString = "from SysGroup where pid is null order by name";
		Session session = super.hibernateTemplate.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Query query = session.createQuery(queryString);
		List<SysGroup> groupList = new ArrayList<SysGroup>();
		for (Object object : query.list().toArray()) {
			this.getChildrenGroup((SysGroup) object, groupList);
		}
		session.beginTransaction().commit();
		return groupList;
	}

	private void getChildrenGroup(SysGroup parentGroup, List<SysGroup> groupList) {
		groupList.add(parentGroup);
		for (SysGroup group : parentGroup.getChildren()) {
			this.getChildrenGroup(group, groupList);
		}
	}

	public int countUsersByGroup(SysGroup group) {
		String queryString = "select count(*) from SysUser u inner join u.groups g where g.id=?";
		return count(queryString, group.getId());
	}

	@SuppressWarnings("unchecked")
	public List<SysUser> findUsersByGroup(int pageNo, int pageSize, Long groupId) {
		String queryString = "select u from SysUser u inner join u.groups g where g.id=? order by u.loginName";
		Session session = super.hibernateTemplate.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<SysUser> userList = session.createQuery(queryString).setParameter(0, groupId).setMaxResults(pageSize).setFirstResult(pageNo * pageSize).list();
		for (SysUser user : userList) {
			user.setCurrentGroup(groupId.toString());
		}
		session.beginTransaction().commit();
		return userList;
	}

	public int countUsersByRoleKey(String roleKey) {
		String queryString = "select count(*) from SysUser u where u.authority like '%" + roleKey + "%'";
		return count(queryString);
	}

	@SuppressWarnings("unchecked")
	public List<SysUser> findUsersByRoleKey(int pageNo, int pageSize, String roleKey) {
		String queryString = "select u from SysUser u where u.authority like '%" + roleKey + "%'";
		Session session = super.hibernateTemplate.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<SysUser> userList = session.createQuery(queryString).setMaxResults(pageSize).setFirstResult(pageNo * pageSize).list();
		for (SysUser user : userList) {
			user.setCurrentRoleKey(roleKey);
		}
		session.beginTransaction().commit();
		return userList;
	}

	public boolean existUserByLoginName(String loginName) {
		String queryString = "select count(*) from SysUser u where u.loginName=?";
		if (count(queryString, loginName) == 0) {
			return false;
		} else {
			return true;
		}
	}

	@SuppressWarnings("unchecked")
	public List<SysUser> findUsersByGroupName(String name) {
		return this.hibernateTemplate.find("select u from SysUser u inner join u.groups g where g.name=? order by u.loginName", name);
	}
}
