package mine.daphne.model.vo;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class Menu implements Serializable {

	private String name;

	private Object value;

	private Menu parent;

	private List<Menu> children;

	public Menu(String name) {
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

	public Menu getParent() {
		return parent;
	}

	public void setParent(Menu parent) {
		this.parent = parent;
	}

	public List<Menu> getChildren() {
		return children;
	}

	public void setChildren(List<Menu> children) {
		this.children = children;
	}
}
