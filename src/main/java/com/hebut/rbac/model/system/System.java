package com.hebut.rbac.model.system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@XmlRootElement(name = "system")
public class System implements Serializable {

	private List<Admin> admin = new ArrayList<Admin>();

	@XmlElement(name = "admin")
	public List<Admin> getAdmin() {
		return admin;
	}

	public void setAdmin(List<Admin> admin) {
		this.admin = admin;
	}
}
