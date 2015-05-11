package mine.daphne.ui.scrum.planning;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mine.daphne.model.entity.ScrumBacklog;
import mine.daphne.model.entity.ScrumStory;
import mine.daphne.model.entity.SysUser;
import mine.daphne.service.JiraService;
import mine.daphne.service.ManageService;
import mine.daphne.service.ScrumService;
import mine.daphne.service.SessionService;
import mine.daphne.ui.common.BaseWindow;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zss.api.CellOperationUtil;
import org.zkoss.zss.api.Importers;
import org.zkoss.zss.api.Range;
import org.zkoss.zss.api.Range.ApplyBorderType;
import org.zkoss.zss.api.Ranges;
import org.zkoss.zss.api.model.CellStyle.BorderType;
import org.zkoss.zss.ui.Spreadsheet;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;

import com.atlassian.jira.rest.client.api.JiraRestClient;

@SuppressWarnings("serial")
public class PlanningWindow extends BaseWindow {

	private Menupopup memberMenu;

	private Spreadsheet spreadsheet;

	private Map<String, String> trueNameToLoginName = new HashMap<String, String>();

	private Map<String, String> loginNameToTrueName = new HashMap<String, String>();

	private ScrumBacklog backlog;

	private SysUser sysUser;

	@Autowired
	private ManageService manageService;

	@Autowired
	private ScrumService scrumService;

	private void initSpreadsheet() {
		for (SysUser user : manageService.findUsersByGroupName(backlog.getProject())) {
			trueNameToLoginName.put(user.getTrueName(), user.getLoginName());
			loginNameToTrueName.put(user.getLoginName(), user.getTrueName());
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
		for (Component component : this.getFellows()) {
			if (component instanceof Menuitem) {
				((Menuitem) component).setDisabled(false);
			}
		}
		((Menuitem) this.getFellow("create")).setDisabled(true);
		((Menuitem) this.getFellow("load")).setDisabled(true);
		try {
			spreadsheet.setVisible(true);
			spreadsheet.setContext("contextMenu");
			spreadsheet.setBook(Importers.getImporter().imports(new File(WebApps.getCurrent().getRealPath("/files/backlog.xlsx")), "backlog.xlsx"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initWindow() {
		sysUser = SessionService.getUserInfoSession().getUser();
		backlog = scrumService.getBacklogByAssignee(sysUser.getLoginName());
		if (backlog == null) {
			((Menuitem) this.getFellow("load")).setDisabled(true);
		}
	}

	public void onClick$create() {
		final CreateWindow window = (CreateWindow) Executions.createComponents(CREATE_BACKLOG, null, null);
		window.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				backlog = new ScrumBacklog(window.getProject().getLabel(), window.getModule(), sysUser.getLoginName());
				initSpreadsheet();
			}
		});
		window.initPop();
		window.doModal();
	}

	public void onClick$load() {
		if (backlog.getStories() == null) {
			return;
		}
		initSpreadsheet();
		int index = 1;
		for (ScrumStory story : backlog.getStories()) {
			Ranges.range(spreadsheet.getSelectedSheet(), index, 0).setCellEditText(story.getProduct());
			Ranges.range(spreadsheet.getSelectedSheet(), index, 1).setCellEditText(story.getSummary());
			Ranges.range(spreadsheet.getSelectedSheet(), index, 2).setCellEditText(story.getDescription());
			Ranges.range(spreadsheet.getSelectedSheet(), index, 3).setCellEditText(story.getDesignPoint().toString());
			Ranges.range(spreadsheet.getSelectedSheet(), index, 4).setCellEditText(story.getCodePoint().toString());
			Ranges.range(spreadsheet.getSelectedSheet(), index, 5).setCellEditText(story.getTestPoint().toString());
			String codeTakers = "";
			for (String codeTaker : story.getCodeTaker().split(",")) {
				codeTakers += loginNameToTrueName.get(codeTaker) + ",";
			}
			Ranges.range(spreadsheet.getSelectedSheet(), index, 6).setCellEditText(StringUtils.isEmpty(codeTakers) ? "" : codeTakers.substring(0, codeTakers.length() - 1));
			String testTakers = "";
			for (String testTaker : story.getTestTaker().split(",")) {
				testTakers += loginNameToTrueName.get(testTaker) + ",";
			}
			Ranges.range(spreadsheet.getSelectedSheet(), index, 7).setCellEditText(StringUtils.isEmpty(testTakers) ? "" : testTakers.substring(0, testTakers.length() - 1));
			index++;
		}
	}

	public void onClick$save() {
		if (this.buildBacklog() && backlog.getStories().size() > 0) {
			scrumService.saveBacklog(backlog);
		}
	}

	private void validateEmpty(ScrumStory story, String methodName, int row, int column) throws Exception {
		Range range = Ranges.range(spreadsheet.getSelectedSheet(), row, column);
		CellOperationUtil.clearStyles(range);
		if (StringUtils.isEmpty(range.getCellEditText())) {
			CellOperationUtil.applyBorder(range, ApplyBorderType.FULL, BorderType.DOTTED, "#FF0000");
			throw new Exception();
		}
		try {
			Method method = ScrumStory.class.getMethod(methodName, String.class);
			method.invoke(story, new Object[] { range.getCellEditText() });
		} catch (Exception e) {
			CellOperationUtil.applyBorder(range, ApplyBorderType.FULL, BorderType.DOTTED, "#FF0000");
			throw new Exception();
		}
	}

	private void validateNumber(ScrumStory story, String methodName, int row, int column) throws Exception {
		Range range = Ranges.range(spreadsheet.getSelectedSheet(), row, column);
		CellOperationUtil.clearStyles(range);
		try {
			Method method = ScrumStory.class.getMethod(methodName, Float.class);
			method.invoke(story, new Object[] { Float.parseFloat(range.getCellEditText()) });
		} catch (Exception e) {
			CellOperationUtil.applyBorder(range, ApplyBorderType.FULL, BorderType.DOTTED, "#FF0000");
			throw new Exception();
		}
	}

	private void validateNames(ScrumStory story, String methodName, int row, int column) throws Exception {
		Range range = Ranges.range(spreadsheet.getSelectedSheet(), row, column);
		CellOperationUtil.clearStyles(range);
		String names = "";
		for (String name : range.getCellEditText().replace("，", ",").split(",")) {
			if (!trueNameToLoginName.containsKey(name)) {
				CellOperationUtil.applyBorder(range, ApplyBorderType.FULL, BorderType.DOTTED, "#FF0000");
				throw new Exception();
			}
			names += trueNameToLoginName.get(name) + ",";
		}
		try {
			Method method = ScrumStory.class.getMethod(methodName, String.class);
			method.invoke(story, new Object[] { names.substring(0, names.length() - 1) });
		} catch (Exception e) {
			CellOperationUtil.applyBorder(range, ApplyBorderType.FULL, BorderType.DOTTED, "#FF0000");
			throw new Exception();
		}
	}

	private boolean buildBacklog() {
		List<ScrumStory> stories = new ArrayList<ScrumStory>();
		for (int row = 1; row <= spreadsheet.getSelectedSheet().getLastRow(); row++) {
			ScrumStory story = new ScrumStory();
			story.setDescription(Ranges.range(spreadsheet.getSelectedSheet(), row, 2).getCellEditText());
			try {
				validateEmpty(story, "setProduct", row, 0);
				validateEmpty(story, "setSummary", row, 1);
				validateNumber(story, "setDesignPoint", row, 3);
				validateNumber(story, "setCodePoint", row, 4);
				validateNumber(story, "setTestPoint", row, 5);
				validateEmpty(story, "setCodeTaker", row, 6);
				validateNames(story, "setCodeTaker", row, 6);
				validateEmpty(story, "setTestTaker", row, 7);
				validateNames(story, "setTestTaker", row, 7);
				story.setDesignTaker(story.getTestTaker());
				story.setBacklog(backlog);
				stories.add(story);
			} catch (Exception e) {
				return false;
			}
		}
		backlog.setStories(stories);
		return true;
	}

	public void onClick$syncJira() throws Exception {
		if (this.buildBacklog()) {
			JiraRestClient client = JiraService.instanceClient(sysUser.getLoginName(), sysUser.getPassword());
			JiraService.createStories(client, backlog);
			client.close();
		}
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
