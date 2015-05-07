package mine.daphne.model.vo;

public class Story {

	public static final Long STORY = 7L, SUBTASK = 8L;

	private String project;

	private String module;

	private String summary;

	private String description;

	private String assignee;

	private Float designPoint;

	private Float codePoint;

	private Float testPoint;

	private String designTaker;

	private String codeTaker;

	private String testTaker;

	public Story(String project, String module, String assignee) {
		this.project = project;
		this.module = module;
		this.assignee = assignee;
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

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
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
		return codeTaker;
	}

	public void setCodeTaker(String codeTaker) {
		this.codeTaker = codeTaker;
	}

	public String getTestTaker() {
		return testTaker;
	}

	public void setTestTaker(String testTaker) {
		this.testTaker = testTaker;
	}
}
