package mine.daphne.model.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "sys_user")
@SuppressWarnings("serial")
public class User implements Serializable {

	private Long id;

	private String trueName;

	private String loginName;

	private String password;

	private String authority;

	private List<Group> groups;

	private String currentGroup;

	private String currentRoleKey;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTrueName() {
		return trueName;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "sys_user_group", joinColumns = { @JoinColumn(name = "uid") }, inverseJoinColumns = { @JoinColumn(name = "gid") })
	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public void removeGroup(Group removeGroup) {
		for (Group group : this.getGroups()) {
			if (group.getId().equals(removeGroup.getId())) {
				this.getGroups().remove(group);
				break;
			}
		}
	}

	@Transient
	public String getCurrentGroup() {
		return currentGroup;
	}

	public void setCurrentGroup(String currentGroup) {
		this.currentGroup = currentGroup;
	}

	@Transient
	public String getCurrentRoleKey() {
		return currentRoleKey;
	}

	public void setCurrentRoleKey(String currentRoleKey) {
		this.currentRoleKey = currentRoleKey;
	}
}
