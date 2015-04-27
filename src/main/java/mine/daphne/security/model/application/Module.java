package mine.daphne.security.model.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@XmlRootElement(name = "module")
public class Module implements Serializable, Cloneable {

	private String name;

	private String show;

	private String roles;

	private List<Function> functions = new ArrayList<Function>();

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
		return roles;
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

	@Override
	protected Object clone() throws CloneNotSupportedException {
		Module module = (Module) super.clone();
		List<Function> functionList = new ArrayList<Function>();
		for (Function function : module.getFunctions()) {
			functionList.add((Function) function.clone());
		}
		module.setFunctions(functionList);
		return module;
	}
}
