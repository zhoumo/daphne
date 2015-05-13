package mine.daphne.ui.scrum.planning;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mine.daphne.model.entity.ScrumBacklog;
import mine.daphne.model.entity.ScrumStory;
import mine.daphne.model.entity.SysUser;
import mine.daphne.security.ComponentCheck;
import mine.daphne.service.ManageService;
import mine.daphne.service.ScrumService;
import mine.daphne.ui.common.BaseWindow;
import mine.daphne.utils.JiraUtil;
import mine.daphne.utils.PdfUtil;
import mine.daphne.utils.SessionUtil;
import mine.daphne.utils.SheetUtil;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zss.api.CellOperationUtil;
import org.zkoss.zss.api.Exporters;
import org.zkoss.zss.api.Importers;
import org.zkoss.zss.api.Range;
import org.zkoss.zss.api.Range.ApplyBorderType;
import org.zkoss.zss.api.Ranges;
import org.zkoss.zss.api.model.CellStyle.BorderType;
import org.zkoss.zss.ui.Spreadsheet;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;
import org.zkoss.zul.Messagebox;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@SuppressWarnings("serial")
public class PlanningWindow extends BaseWindow {

	private ManageService manageService;

	private ScrumService scrumService;

	private Menupopup memberMenu;

	private Spreadsheet spreadsheet;

	private Map<String, String> userNamesMap;

	private ScrumBacklog backlog;

	private SysUser sysUser;

	private void initSpreadsheet() {
		userNamesMap = new HashMap<String, String>();
		userNamesMap.put("", "");
		for (SysUser user : manageService.findUsersByGroupName(backlog.getProject())) {
			userNamesMap.put(user.getTrueName(), user.getLoginName());
			userNamesMap.put(user.getLoginName(), user.getTrueName());
			memberMenu.appendChild(SheetUtil.createMenuitem(user.getTrueName(), spreadsheet));
		}
		SheetUtil.enabledMenuitems(this);
		((Menuitem) this.getFellow("create")).setDisabled(true);
		((Menuitem) this.getFellow("load")).setDisabled(true);
		((Menuitem) this.getFellow("print")).setDisabled(true);
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
		sysUser = SessionUtil.getUserInfoSession().getUser();
		backlog = scrumService.getBacklogByAssignee(sysUser.getLoginName());
		if (backlog == null) {
			((Menuitem) this.getFellow("load")).setDisabled(true);
		}
	}

	public void onClick$create() {
		final CreateWindow window = (CreateWindow) ComponentCheck.createComponents(this, PLANNING_CREATE, null, null);
		window.addEventListener(Events.ON_CHANGE, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				backlog = new ScrumBacklog(window.getProject().getLabel(), window.getModule(), sysUser.getLoginName());
				initSpreadsheet();
			}
		});
		window.initPop();
		window.doHighlighted();
	}

	public void onClick$load() {
		if (backlog.getStories() == null) {
			return;
		}
		this.initSpreadsheet();
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
				codeTakers += userNamesMap.get(codeTaker) + ",";
			}
			Ranges.range(spreadsheet.getSelectedSheet(), index, 6).setCellEditText(codeTakers.replace(",", " ").trim().replace(" ", ","));
			String testTakers = "";
			for (String testTaker : story.getTestTaker().split(",")) {
				testTakers += userNamesMap.get(testTaker) + ",";
			}
			Ranges.range(spreadsheet.getSelectedSheet(), index, 7).setCellEditText(testTakers.replace(",", " ").trim().replace(" ", ","));
			index++;
		}
		if (!StringUtils.isEmpty(backlog.getStories().get(0).getJiraKey())) {
			((Menuitem) this.getFellow("save")).setDisabled(true);
			((Menuitem) this.getFellow("sync")).setDisabled(true);
			((Menuitem) this.getFellow("print")).setDisabled(false);
		}
	}

	public void onClick$save() {
		if (this.buildBacklog() && backlog.getStories().size() > 0) {
			scrumService.saveBacklog(backlog);
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

	private Map<String, Float> buildPointMap() {
		Map<String, Float> pointMap = new HashMap<String, Float>();
		for (SysUser user : manageService.findUsersByGroupName(backlog.getProject())) {
			pointMap.put(user.getTrueName(), 0F);
		}
		SheetUtil.buildPointMap(spreadsheet, pointMap);
		return pointMap;
	}

	public void onClick$point() {
		PointWindow window = (PointWindow) ComponentCheck.createComponents(this, PLANNING_POINT, null, null);
		window.setPointMap(this.buildPointMap());
		window.initPop();
		window.doHighlighted();
	}

	private boolean buildBacklog() {
		List<ScrumStory> stories = new ArrayList<ScrumStory>();
		for (int row = 1; row <= spreadsheet.getSelectedSheet().getLastRow(); row++) {
			ScrumStory story = new ScrumStory();
			story.setDescription(Ranges.range(spreadsheet.getSelectedSheet(), row, 2).getCellEditText());
			Range range = null;
			try {
				SheetUtil.validateEmpty(story, "setProduct", range = Ranges.range(spreadsheet.getSelectedSheet(), row, 0));
				SheetUtil.validateEmpty(story, "setSummary", range = Ranges.range(spreadsheet.getSelectedSheet(), row, 1));
				SheetUtil.validateNumber(story, "setDesignPoint", range = Ranges.range(spreadsheet.getSelectedSheet(), row, 3));
				SheetUtil.validateNumber(story, "setCodePoint", range = Ranges.range(spreadsheet.getSelectedSheet(), row, 4));
				SheetUtil.validateNumber(story, "setTestPoint", range = Ranges.range(spreadsheet.getSelectedSheet(), row, 5));
				SheetUtil.validateNames(story, "setCodeTaker", range = Ranges.range(spreadsheet.getSelectedSheet(), row, 6), userNamesMap);
				SheetUtil.validateNames(story, "setTestTaker", range = Ranges.range(spreadsheet.getSelectedSheet(), row, 7), userNamesMap);
				story.setDesignTaker(story.getTestTaker());
				story.setBacklog(backlog);
				stories.add(story);
			} catch (Exception e) {
				CellOperationUtil.applyBorder(range, ApplyBorderType.FULL, BorderType.DOTTED, "#FF0000");
				return false;
			}
		}
		backlog.setStories(stories);
		return true;
	}

	public void onClick$sync() throws IOException {
		if (this.buildBacklog() && backlog.getStories().size() > 0) {
			JiraRestClient client = JiraUtil.instanceClient(sysUser.getLoginName(), sysUser.getPassword());
			JiraUtil.createStories(client, backlog);
			client.close();
			scrumService.saveBacklog(backlog);
			Events.postEvent(Events.ON_CLOSE, this, null);
			Messagebox.show("操作完成.");
		}
	}

	public void onClick$export() throws IOException {
		File file = File.createTempFile(Long.toString(System.currentTimeMillis()), "temp");
		FileOutputStream outputStream = new FileOutputStream(file);
		Exporters.getExporter().export(spreadsheet.getBook(), outputStream);
		outputStream.close();
		Filedownload.save(new AMedia(backlog.getProject() + "_" + backlog.getModule() + ".xlsx", null, null, file, true));
	}

	public void onClick$print() throws Exception {
		File file = new File(new Date().getTime() + ".pdf");
		Document document = new Document(PageSize.A4, 0, 0, 30, 30);
		PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		PdfPTable mainTable = new PdfPTable(2);
		mainTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		for (int index = 0; index < backlog.getStories().size();) {
			PdfUtil.createPdfTable(mainTable, backlog.getStories().get(index++), userNamesMap);
			if (index < backlog.getStories().size()) {
				PdfUtil.createPdfTable(mainTable, backlog.getStories().get(index++), userNamesMap);
			} else {
				mainTable.addCell("");
			}
		}
		document.add(mainTable);
		document.close();
		Filedownload.save(new AMedia(backlog.getProject() + "_" + backlog.getModule() + ".pdf", null, null, file, true));
	}
}
