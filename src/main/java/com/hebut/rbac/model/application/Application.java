package com.hebut.rbac.model.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@XmlRootElement(name = "application")
public class Application implements Serializable, Cloneable {

	private List<Module> module = new ArrayList<Module>();

	@XmlElement(name = "module")
	public List<Module> getModule() {
		return module;
	}

	public void setModule(List<Module> module) {
		this.module = module;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Application application = (Application) super.clone();
		List<Module> moduleList = new ArrayList<Module>();
		for (Module module : application.getModule()) {
			moduleList.add((Module) module.clone());
		}
		application.setModule(moduleList);
		return application;
	}
}
