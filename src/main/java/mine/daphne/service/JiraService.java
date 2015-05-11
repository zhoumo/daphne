package mine.daphne.service;

import java.net.URI;
import java.util.Arrays;

import mine.daphne.model.entity.ScrumBacklog;
import mine.daphne.model.entity.ScrumStory;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.IssueInputBuilder;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.google.common.collect.ImmutableList;

public class JiraService {

	private static final String JIRA_URL = "http://jira.funshion.com:8080";

	public static JiraRestClient instanceClient(String userName, String password) {
		try {
			URI uri = new URI(JIRA_URL);
			return new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(uri, userName, password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void createStories(JiraRestClient client, ScrumBacklog backlog) {
		for (ScrumStory story : backlog.getStories()) {
			IssueInputBuilder builder = new IssueInputBuilder(backlog.getProject(), ScrumStory.STORY, story.getSummary());
			builder.setFieldValue("customfield_10008", story.getDesignPoint() + story.getCodePoint() + story.getTestPoint());
			builder.setFieldValue("customfield_10101", ComplexIssueInputFieldValue.with("name", story.getBacklog().getAssignee()));
			builder.setFieldValue("customfield_10104", ComplexIssueInputFieldValue.with("name", story.getBacklog().getAssignee()));
			builder.setFieldValue("customfield_10200", Arrays.asList(new ComplexIssueInputFieldValue[] { ComplexIssueInputFieldValue.with("value", "业务相关") }));
			builder.setFieldValue("customfield_10401", ImmutableList.of("产品人员"));
			builder.setAssigneeName(story.getBacklog().getAssignee());
			builder.setReporterName(story.getBacklog().getAssignee());
			builder.setComponentsNames(Arrays.asList(new String[] { story.getBacklog().getModule() }));
			builder.setDescription(story.getDescription());
			BasicIssue issue = client.getIssueClient().createIssue(builder.build()).claim();
			createSubTask(client, issue.getKey(), story);
		}
	}

	public static void createSubTask(JiraRestClient client, String issueKey, ScrumStory story) {
		for (String taker : story.getTestTaker().split(",")) {
			IssueInputBuilder builder = new IssueInputBuilder(story.getBacklog().getProject(), ScrumStory.SUBTASK, "TDR");
			builder.setFieldValue("parent", ComplexIssueInputFieldValue.with("key", issueKey));
			builder.setFieldValue("customfield_10104", ComplexIssueInputFieldValue.with("name", story.getBacklog().getAssignee()));
			builder.setFieldValue("timetracking", ComplexIssueInputFieldValue.with("originalEstimate", story.getDesignPoint() * 4 + "h"));
			builder.setAssigneeName(taker);
			builder.setReporterName(taker);
			client.getIssueClient().createIssue(builder.build()).claim();
		}
		for (String taker : story.getCodeTaker().split(",")) {
			IssueInputBuilder builder = new IssueInputBuilder(story.getBacklog().getProject(), ScrumStory.SUBTASK, "CODE");
			builder.setFieldValue("parent", ComplexIssueInputFieldValue.with("key", issueKey));
			builder.setFieldValue("customfield_10104", ComplexIssueInputFieldValue.with("name", story.getBacklog().getAssignee()));
			builder.setFieldValue("timetracking", ComplexIssueInputFieldValue.with("originalEstimate", story.getCodePoint() * 4 + "h"));
			builder.setAssigneeName(taker);
			builder.setReporterName(taker);
			client.getIssueClient().createIssue(builder.build()).claim();
		}
		for (String taker : story.getTestTaker().split(",")) {
			IssueInputBuilder builder = new IssueInputBuilder(story.getBacklog().getProject(), ScrumStory.SUBTASK, "TEST");
			builder.setFieldValue("parent", ComplexIssueInputFieldValue.with("key", issueKey));
			builder.setFieldValue("customfield_10104", ComplexIssueInputFieldValue.with("name", story.getBacklog().getAssignee()));
			builder.setFieldValue("timetracking", ComplexIssueInputFieldValue.with("originalEstimate", story.getTestPoint() * 4 + "h"));
			builder.setAssigneeName(taker);
			builder.setReporterName(taker);
			client.getIssueClient().createIssue(builder.build()).claim();
		}
	}
}
