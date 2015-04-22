package com.hebut.framework.model.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
@SuppressWarnings("serial")
public class FwkGroup implements Serializable {

	public static final short DEPT = 0, UNIT = 1;

	private Long fgId;

	private String fgName;

	private Integer fgLevel;

	private Short fgType;

	private FwkGroup parent;

	private List<FwkGroup> children;

	private Boolean selected;

	@Id
	@GeneratedValue
	public Long getFgId() {
		return fgId;
	}

	public void setFgId(Long fgId) {
		this.fgId = fgId;
	}

	public String getFgName() {
		return fgName;
	}

	public void setFgName(String fgName) {
		this.fgName = fgName;
	}

	public Integer getFgLevel() {
		return fgLevel;
	}

	public void setFgLevel(Integer fgLevel) {
		this.fgLevel = fgLevel;
	}

	public Short getFgType() {
		return fgType;
	}

	public void setFgType(Short fgType) {
		this.fgType = fgType;
	}

	@ManyToOne
	@JoinColumn(name = "fgPid")
	public FwkGroup getParent() {
		return parent;
	}

	public void setParent(FwkGroup parent) {
		this.parent = parent;
	}

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
	public List<FwkGroup> getChildren() {
		return children;
	}

	public void setChildren(List<FwkGroup> children) {
		this.children = children;
	}

	@Transient
	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
}
