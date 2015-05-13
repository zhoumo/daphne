package mine.daphne.model.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "scrum_story")
@SuppressWarnings("serial")
public class ScrumStory implements Serializable {

	public static final Long STORY = 7L, SUBTASK = 8L;

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "jira_key")
	private String jiraKey;

	@Column
	private String product;

	@Column
	private String summary;

	@Column
	private String description;

	@Column(name = "design_point")
	private Float designPoint;

	@Column(name = "code_point")
	private Float codePoint;

	@Column(name = "test_point")
	private Float testPoint;

	@Column(name = "design_taker")
	private String designTaker;

	@Column(name = "code_taker")
	private String codeTaker;

	@Column(name = "test_taker")
	private String testTaker;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "backlog")
	private ScrumBacklog backlog;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getJiraKey() {
		return jiraKey;
	}

	public void setJiraKey(String jiraKey) {
		this.jiraKey = jiraKey;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Float getDesignPoint() {
		return designPoint;
	}

	public void setDesignPoint(Float designPoint) {
		this.designPoint = designPoint;
	}

	public Float getCodePoint() {
		return codePoint;
	}

	public void setCodePoint(Float codePoint) {
		this.codePoint = codePoint;
	}

	public Float getTestPoint() {
		return testPoint;
	}

	public void setTestPoint(Float testPoint) {
		this.testPoint = testPoint;
	}

	public String getDesignTaker() {
		return designTaker;
	}

	public void setDesignTaker(String designTaker) {
		this.designTaker = designTaker;
	}

	public String getCodeTaker() {
		return codeTaker == null ? "" : codeTaker;
	}

	public void setCodeTaker(String codeTaker) {
		this.codeTaker = codeTaker;
	}

	public String getTestTaker() {
		return testTaker == null ? "" : testTaker;
	}

	public void setTestTaker(String testTaker) {
		this.testTaker = testTaker;
	}

	public ScrumBacklog getBacklog() {
		return backlog;
	}

	public void setBacklog(ScrumBacklog backlog) {
		this.backlog = backlog;
	}
}
