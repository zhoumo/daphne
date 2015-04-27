package mine.daphne.security.model.actor;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@XmlRootElement(name = "role")
public class Role implements Serializable {

	private String name;

	private String show;

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
}
