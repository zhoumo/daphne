package com.hebut.rbac.model.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@XmlRootElement(name = "function")
public class Function implements Serializable, Cloneable {

	private String name;

	private String show;

	private String roles;

	private List<Function> functions = new ArrayList<Function>();

	private List<Constraint> constraints = new ArrayList<Constraint>();

	@XmlAttribute(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlAttribute(name = "show")
	public String getShow() {
		return show;
	}

	public void setShow(String show) {
		this.show = show;
	}

	@XmlAttribute(name = "roles")
	public String getRoles() {
		return roles == null ? "" : roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	@XmlElement(name = "function")
	public List<Function> getFunctions() {
		return functions;
	}

	public void setFunctions(List<Function> functions) {
		this.functions = functions;
	}

	@XmlElement(name = "constraint")
	public List<Constraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(List<Constraint> constraints) {
		this.constraints = constraints;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
