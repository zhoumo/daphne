package mine.daphne.model.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
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
public class SysUser implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "true_name")
	private String trueName;

	@Column(name = "login_name")
	private String loginName;

	@Column
	private String password;

	@Column
	private String authority;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "sys_user_group", joinColumns = { @JoinColumn(name = "uid") }, inverseJoinColumns = { @JoinColumn(name = "gid") })
	private List<SysGroup> groups;

	@Transient
	private String currentGroup;

	@Transient
	private String currentRoleKey;

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

	public List<SysGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<SysGroup> groups) {
		this.groups = groups;
	}

	public String getCurrentGroup() {
		return currentGroup;
	}

	public void setCurrentGroup(String currentGroup) {
		this.currentGroup = currentGroup;
	}

	public String getCurrentRoleKey() {
		return currentRoleKey;
	}

	public void setCurrentRoleKey(String currentRoleKey) {
		this.currentRoleKey = currentRoleKey;
	}

	public void removeGroup(SysGroup removeGroup) {
		for (SysGroup group : this.getGroups()) {
			if (group.getId().equals(removeGroup.getId())) {
				this.getGroups().remove(group);
				break;
			}
		}
	}
}
