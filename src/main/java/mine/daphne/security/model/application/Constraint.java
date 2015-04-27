package mine.daphne.security.model.application;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@SuppressWarnings("serial")
@XmlRootElement(name = "constraint")
public class Constraint implements Serializable {

	private String url;

	private String index;

	@XmlAttribute(name = "url")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@XmlAttribute(name = "index")
	public String getIndex() {
		return index == null ? "" : index;
	}

	public void setIndex(String index) {
		this.index = index;
	}
}
