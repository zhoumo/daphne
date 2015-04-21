package com.hebut.framework.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import com.hebut.framework.util.CacheUtil;

@Entity
@SuppressWarnings("serial")
public class FwkUser implements IEntityCache {

	private Long fuId;

	private String fuTrueName;

	private String fuLoginName;

	private String fuPassword;

	private String fuAuthority;

	private List<FwkGroup> groups;

	private String currentGroup;

	private String currentRoleKey;

	public FwkUser() {
	}

	public FwkUser(Long fuId, String fuTrueName, String fuLoginName, String fuPassword, String fuAuthority, List<FwkGroup> groups) {
		super();
		this.fuId = fuId;
		this.fuTrueName = fuTrueName;
		this.fuLoginName = fuLoginName;
		this.fuPassword = fuPassword;
		this.fuAuthority = fuAuthority;
		this.groups = groups;
	}

	@Id
	@GeneratedValue
	public Long getFuId() {
		return fuId;
	}

	public void setFuId(Long fuId) {
		this.fuId = fuId;
	}

	public String getFuTrueName() {
		return fuTrueName;
	}

	public void setFuTrueName(String fuTrueName) {
		this.fuTrueName = fuTrueName;
	}

	public String getFuLoginName() {
		return fuLoginName;
	}

	public void setFuLoginName(String fuLoginName) {
		this.fuLoginName = fuLoginName;
	}

	public String getFuPassword() {
		return fuPassword;
	}

	public void setFuPassword(String fuPassword) {
		this.fuPassword = fuPassword;
	}

	public String getFuAuthority() {
		return fuAuthority;
	}

	public void setFuAuthority(String fuAuthority) {
		this.fuAuthority = fuAuthority;
	}

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "FwkUserGroup", joinColumns = { @JoinColumn(name = "fuId") }, inverseJoinColumns = { @JoinColumn(name = "fgId") })
	public List<FwkGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<FwkGroup> groups) {
		this.groups = groups;
	}

	public void removeGroup(FwkGroup removeGroup) {
		for (FwkGroup group : this.getGroups()) {
			if (group.getFgId().longValue() == removeGroup.getFgId().longValue()) {
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

	@Override
	@Transient
	public String getCacheKey() {
		return CacheUtil.getCacheKey(FwkUser.class, this.fuLoginName);
	}
}
