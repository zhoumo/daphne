package mine.daphne.model.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "scrum_backlog")
@SuppressWarnings("serial")
public class ScrumBacklog implements Serializable {

	@Id
	@GeneratedValue
	private Long id;

	@Column
	private String project;

	@Column
	private String module;

	@Column
	private String assignee;

	@Column
	private Date timestamp = new Date();

	@OneToMany(mappedBy = "backlog", cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	private List<ScrumStory> stories;

	public ScrumBacklog() {
		super();
	}

	public ScrumBacklog(String project, String module, String assignee) {
		this.project = project;
		this.module = module;
		this.assignee = assignee;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public List<ScrumStory> getStories() {
		return stories;
	}

	public void setStories(List<ScrumStory> stories) {
		this.stories = stories;
	}
}
