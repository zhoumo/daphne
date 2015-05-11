package mine.daphne.model.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
public class SysGroup implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	@Column
	private String name;

	@Column
	private Integer level;

	@ManyToOne
	@JoinColumn(name = "pid")
	private SysGroup parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
	private List<SysGroup> children;

	@Transient
	private Boolean selected;

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

	public SysGroup getParent() {
		return parent;
	}

	public void setParent(SysGroup parent) {
		this.parent = parent;
	}

	public List<SysGroup> getChildren() {
		return children;
	}

	public void setChildren(List<SysGroup> children) {
		this.children = children;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}
}
