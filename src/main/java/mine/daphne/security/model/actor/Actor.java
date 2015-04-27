package mine.daphne.security.model.actor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@XmlRootElement(name = "actor")
public class Actor implements Serializable {

	private List<Group> group = new ArrayList<Group>();

	@XmlElement(name = "group")
	public List<Group> getGroup() {
		return group;
	}

	public void setGroup(List<Group> group) {
		this.group = group;
	}
}
