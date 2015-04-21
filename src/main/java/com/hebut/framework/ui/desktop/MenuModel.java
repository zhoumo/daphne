package com.hebut.framework.ui.desktop;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class MenuModel implements Serializable {

	private String name;

	private Object value;

	private MenuModel parent;

	private List<MenuModel> children;

	public MenuModel(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public MenuModel getParent() {
		return parent;
	}

	public void setParent(MenuModel parent) {
		this.parent = parent;
	}

	public List<MenuModel> getChildren() {
		return children;
	}

	public void setChildren(List<MenuModel> children) {
		this.children = children;
	}
}
