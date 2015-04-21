package com.hebut.rbac.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.hebut.rbac.model.actor.Actor;
import com.hebut.rbac.model.application.Application;
import com.hebut.rbac.model.system.System;

@SuppressWarnings("serial")
@XmlRootElement(name = "config")
public class RbacConfig implements Serializable {

	private Actor actor;

	private Application application;

	private System system;

	@XmlElement(name = "actor")
	public Actor getActor() {
		return actor;
	}

	public void setActor(Actor actor) {
		this.actor = actor;
	}

	@XmlElement(name = "application")
	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	@XmlElement(name = "system")
	public System getSystem() {
		return system;
	}

	public void setSystem(System system) {
		this.system = system;
	}
}
