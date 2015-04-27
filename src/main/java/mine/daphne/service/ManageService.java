package mine.daphne.service;

import java.util.ArrayList;
import java.util.List;

import mine.daphne.model.entity.Group;
import mine.daphne.model.entity.User;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

@Service("manageService")
public class ManageService extends BaseService {

	public User findByLoginNameAndPassword(String loginName, String password) {
		String queryString = "from User u where u.loginName=? and u.password=?";
		User user = (User) super.hibernateTemplate.find(queryString, new Object[] { loginName, password }).get(0);
		for (Group group : user.getGroups()) {
			group.setChildren(null);
		}
		return user;
	}

	@SuppressWarnings("unchecked")
	public List<Group> findRootGroup(Group selectedGroup) {
		String queryString = "from Group where pid is null order by name";
		Session session = super.hibernateTemplate.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<Group> groupList = session.createQuery(queryString).list();
		this.initialize(groupList, selectedGroup);
		session.beginTransaction().commit();
		return groupList;
	}

	private void initialize(List<Group> groupList, Group selectedGroup) {
		for (Group group : groupList) {
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

	public List<Group> buildGroupTree() {
		String queryString = "from Group where pid is null order by name";
		Session session = super.hibernateTemplate.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		Query query = session.createQuery(queryString);
		List<Group> groupList = new ArrayList<Group>();
		for (Object object : query.list().toArray()) {
			this.getChildrenGroup((Group) object, groupList);
		}
		session.beginTransaction().commit();
		return groupList;
	}

	private void getChildrenGroup(Group parentGroup, List<Group> groupList) {
		groupList.add(parentGroup);
		for (Group group : parentGroup.getChildren()) {
			this.getChildrenGroup(group, groupList);
		}
	}

	public int countUsersByGroup(Group group) {
		String queryString = "select count(*) from User u inner join u.groups g where g.id=?";
		return count(queryString, group.getId());
	}

	@SuppressWarnings("unchecked")
	public List<User> findUsersByGroup(int pageNo, int pageSize, Group group) {
		String queryString = "select u from User u inner join u.groups g where g.id=?";
		Session session = super.hibernateTemplate.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<User> userList = session.createQuery(queryString).setParameter(0, group.getId()).setMaxResults(pageSize).setFirstResult(pageNo * pageSize).list();
		for (User user : userList) {
			user.setCurrentGroup(group.getId().toString());
		}
		session.beginTransaction().commit();
		return userList;
	}

	public int countUsersByRoleKey(String roleKey) {
		String queryString = "select count(*) from User u where u.authority like '%" + roleKey + "%'";
		return count(queryString);
	}

	@SuppressWarnings("unchecked")
	public List<User> findUsersByRoleKey(int pageNo, int pageSize, String roleKey) {
		String queryString = "select u from User u where u.authority like '%" + roleKey + "%'";
		Session session = super.hibernateTemplate.getSessionFactory().getCurrentSession();
		session.beginTransaction();
		List<User> userList = session.createQuery(queryString).setMaxResults(pageSize).setFirstResult(pageNo * pageSize).list();
		for (User user : userList) {
			user.setCurrentRoleKey(roleKey);
		}
		session.beginTransaction().commit();
		return userList;
	}

	public boolean existUserByLoginName(String loginName) {
		String queryString = "select count(*) from User u where u.loginName=?";
		if (count(queryString, loginName) == 0) {
			return false;
		} else {
			return true;
		}
	}
}
