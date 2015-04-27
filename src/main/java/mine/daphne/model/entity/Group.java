package mine.daphne.model.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "sys_group")
@SuppressWarnings("serial")
public class Group implements Serializable {

	private Long id;

	private String name;

	private Integer level;

	private Group parent;

	private List<Group> children;

	private Boolean selected;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	@ManyToOne
	@JoinColumn(name = "pid")
	public Group getParent() {
		return parent;
	}

	public void setParent(Group parent) {
		this.parent = parent;
	}

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
	public List<Group> getChildren() {
		return children;
	}

	public void setChildren(List<Group> children) {
		this.children = children;
	}

	@Transient
	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
}
