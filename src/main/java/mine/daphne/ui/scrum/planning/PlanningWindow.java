package mine.daphne.ui.scrum.planning;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import mine.daphne.model.entity.User;
import mine.daphne.model.vo.Story;
import mine.daphne.service.JiraService;
import mine.daphne.service.ManageService;
import mine.daphne.ui.common.BaseWindow;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zss.api.CellOperationUtil;
import org.zkoss.zss.api.Importer;
import org.zkoss.zss.api.Importers;
import org.zkoss.zss.api.Range;
import org.zkoss.zss.api.Range.ApplyBorderType;
import org.zkoss.zss.api.Ranges;
import org.zkoss.zss.api.model.Book;
import org.zkoss.zss.api.model.CellStyle.BorderType;
import org.zkoss.zss.ui.Spreadsheet;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;

@SuppressWarnings("serial")
public class PlanningWindow extends BaseWindow {

	private Spreadsheet spreadsheet;

	private Menupopup operateMenu, contextMenu, memberMenu;

	private Map<String, String> namesMap;

	@Autowired
	private ManageService manageService;

	private void createMemberMenu() {
		namesMap = new HashMap<String, String>();
		for (User user : manageService.findUsersByGroup(0, Integer.MAX_VALUE, 3L)) {
			namesMap.put(user.getTrueName(), user.getLoginName());
			Menuitem menuitem = new Menuitem(user.getTrueName());
			menuitem.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

				@Override
				public void onEvent(Event event) throws Exception {
					Menuitem menuitem = (Menuitem) event.getTarget();
					int row = spreadsheet.getSelection().getRow();
					int column = spreadsheet.getSelection().getColumn();
					Ranges.range(spreadsheet.getSelectedSheet(), row, column).setCellEditText(menuitem.getLabel());
				}
			});
			memberMenu.appendChild(menuitem);
		}
	}

	@Override
	public void initWindow() {
		Importer importer = Importers.getImporter();
		try {
			Book profileBook = importer.imports(new File(WebApps.getCurrent().getRealPath("/files/backlog.xlsx")), "backlog.xlsx");
			spreadsheet.setBook(profileBook);
		} catch (IOException e) {
			e.printStackTrace();
		}
		createMemberMenu();
		int index = 0;
		for (Object menuitem : operateMenu.getChildren()) {
			EventListener<? extends Event> listener = ((Menuitem) menuitem).getEventListeners(Events.ON_CLICK).iterator().next();
			contextMenu.getChildren().get(index++).addEventListener(Events.ON_CLICK, listener);
		}
	}

	public void onClick$create() {
		System.out.println("create");
	}

	public void onClick$save() {
		System.out.println("save");
	}

	private void validateEmpty(Story story, String methodName, int row, int column) throws Exception {
		Range range = Ranges.range(spreadsheet.getSelectedSheet(), row, column);
		CellOperationUtil.clearStyles(range);
		if (StringUtils.isEmpty(range.getCellEditText())) {
			CellOperationUtil.applyBorder(range, ApplyBorderType.FULL, BorderType.DOTTED, "#FF0000");
			throw new Exception();
		}
		try {
			Method method = Story.class.getMethod(methodName, String.class);
			method.invoke(story, new Object[] { range.getCellEditText() });
		} catch (Exception e) {
			CellOperationUtil.applyBorder(range, ApplyBorderType.FULL, BorderType.DOTTED, "#FF0000");
			throw new Exception();
		}
	}

	private void validateNumber(Story story, String methodName, int row, int column) throws Exception {
		Range range = Ranges.range(spreadsheet.getSelectedSheet(), row, column);
		CellOperationUtil.clearStyles(range);
		try {
			Method method = Story.class.getMethod(methodName, Float.class);
			method.invoke(story, new Object[] { Float.parseFloat(range.getCellEditText()) });
		} catch (Exception e) {
			CellOperationUtil.applyBorder(range, ApplyBorderType.FULL, BorderType.DOTTED, "#FF0000");
			throw new Exception();
		}
	}

	private void validateNames(Story story, String methodName, int row, int column) throws Exception {
		Range range = Ranges.range(spreadsheet.getSelectedSheet(), row, column);
		CellOperationUtil.clearStyles(range);
		String names = "";
		for (String name : range.getCellEditText().replace("，", ",").split(",")) {
			if (!namesMap.containsKey(name)) {
				CellOperationUtil.applyBorder(range, ApplyBorderType.FULL, BorderType.DOTTED, "#FF0000");
				throw new Exception();
			}
			names += namesMap.get(name) + ",";
		}
		names = names.substring(0, names.length() - 1);
		try {
			Method method = Story.class.getMethod(methodName, String.class);
			method.invoke(story, new Object[] { namesMap.get(range.getCellEditText()) });
		} catch (Exception e) {
			CellOperationUtil.applyBorder(range, ApplyBorderType.FULL, BorderType.DOTTED, "#FF0000");
			throw new Exception();
		}
	}

	public void onClick$syncJira() {
		final SyncJiraWindow window = (SyncJiraWindow) Executions.createComponents(SYNC_JIRA, null, null);
		window.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				JiraRestClient client = JiraService.instanceClient(window.getUserName(), window.getPassword());
				for (int row = 1; row <= spreadsheet.getSelectedSheet().getLastRow(); row++) {
					Story story = new Story("ADSP", window.getModule(), window.getUserName());
					story.setDescription(Ranges.range(spreadsheet.getSelectedSheet(), row, 2).getCellEditText());
					try {
						validateEmpty(story, "setSummary", row, 1);
						validateNumber(story, "setDesignPoint", row, 3);
						validateNumber(story, "setCodePoint", row, 4);
						validateNumber(story, "setTestPoint", row, 5);
						validateEmpty(story, "setCodeTaker", row, 6);
						validateNames(story, "setCodeTaker", row, 6);
						validateEmpty(story, "setTestTaker", row, 7);
						validateNames(story, "setTestTaker", row, 7);
					} catch (Exception e) {
						return;
					}
					BasicIssue issue = JiraService.createTask(client, story);
					JiraService.createSubTask(client, issue.getKey(), story);
				}
				client.close();
				return;
			}
		});
		window.doModal();
	}

	public void onClick$insertLine() {
		spreadsheet.setMaxVisibleRows(spreadsheet.getMaxVisibleRows() + 1);
		CellOperationUtil.insertRow(Ranges.range(spreadsheet.getSelectedSheet(), spreadsheet.getSelection()));
	}

	public void onClick$removeLine() {
		spreadsheet.setMaxVisibleRows(spreadsheet.getMaxVisibleRows() - 1);
		CellOperationUtil.deleteRow(Ranges.range(spreadsheet.getSelectedSheet(), spreadsheet.getSelection()));
	}

	public void onClick$mergeCells() {
		CellOperationUtil.merge(Ranges.range(spreadsheet.getSelectedSheet(), spreadsheet.getSelection()), false);
	}

	public void onClick$unmergeCells() {
		CellOperationUtil.unmerge(Ranges.range(spreadsheet.getSelectedSheet(), spreadsheet.getSelection()));
	}

	public void onClick$point() {
		System.out.println("point");
	}
}
