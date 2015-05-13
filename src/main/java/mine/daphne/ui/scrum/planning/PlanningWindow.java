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
import mine.daphne.service.JiraService;
import mine.daphne.service.ManageService;
import mine.daphne.service.ScrumService;
import mine.daphne.service.SessionService;
import mine.daphne.ui.common.BaseWindow;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zss.api.AreaRef;
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
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@SuppressWarnings("serial")
public class PlanningWindow extends BaseWindow {

	private Menupopup memberMenu;

	private Spreadsheet spreadsheet;

	private Map<String, String> userNamesMap;

	private ScrumBacklog backlog;

	private SysUser sysUser;

	@Autowired
	private ManageService manageService;

	@Autowired
	private ScrumService scrumService;

	private void initSpreadsheet() {
		userNamesMap = new HashMap<String, String>();
		userNamesMap.put("", "");
		for (SysUser user : manageService.findUsersByGroupName(backlog.getProject())) {
			userNamesMap.put(user.getTrueName(), user.getLoginName());
			userNamesMap.put(user.getLoginName(), user.getTrueName());
			Menuitem menuitem = new Menuitem(user.getTrueName());
			menuitem.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

				@Override
				public void onEvent(Event event) throws Exception {
					Menuitem menuitem = (Menuitem) event.getTarget();
					AreaRef areaRef = spreadsheet.getSelection();
					Ranges.range(spreadsheet.getSelectedSheet(), areaRef.getRow(), areaRef.getColumn()).setCellEditText(menuitem.getLabel());
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
		sysUser = SessionService.getUserInfoSession().getUser();
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

	private void validateEmpty(ScrumStory story, String methodName, Range range) throws Exception {
		CellOperationUtil.clearStyles(range);
		if (StringUtils.isEmpty(range.getCellEditText().trim())) {
			throw new Exception();
		}
		try {
			ScrumStory.class.getMethod(methodName, String.class).invoke(story, new Object[] { range.getCellEditText() });
		} catch (Exception e) {
			throw new Exception();
		}
	}

	private void validateNumber(ScrumStory story, String methodName, Range range) throws Exception {
		CellOperationUtil.clearStyles(range);
		try {
			ScrumStory.class.getMethod(methodName, Float.class).invoke(story, new Object[] { Float.parseFloat(range.getCellEditText()) });
		} catch (Exception e) {
			throw new Exception();
		}
	}

	private void validateNames(ScrumStory story, String methodName, Range range) throws Exception {
		CellOperationUtil.clearStyles(range);
		if (StringUtils.isEmpty(range.getCellEditText().trim())) {
			return;
		}
		String names = "";
		for (String name : range.getCellEditText().replace("，", ",").split(",")) {
			if (!userNamesMap.containsKey(name)) {
				throw new Exception();
			}
			names += userNamesMap.get(name) + ",";
		}
		try {
			ScrumStory.class.getMethod(methodName, String.class).invoke(story, new Object[] { names.substring(0, names.length() - 1) });
		} catch (Exception e) {
			throw new Exception();
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
		for (int row = 1; row <= spreadsheet.getSelectedSheet().getLastRow(); row++) {
			String taker = Ranges.range(spreadsheet.getSelectedSheet(), row, 6).getCellEditText();
			String point = Ranges.range(spreadsheet.getSelectedSheet(), row, 4).getCellEditText();
			if (!StringUtils.isEmpty(taker) && !StringUtils.isEmpty(point)) {
				pointMap.put(taker, pointMap.get(taker) + Float.parseFloat(point));
			}
			taker = Ranges.range(spreadsheet.getSelectedSheet(), row, 7).getCellEditText();
			point = Ranges.range(spreadsheet.getSelectedSheet(), row, 3).getCellEditText();
			if (!StringUtils.isEmpty(taker) && !StringUtils.isEmpty(point)) {
				pointMap.put(taker, pointMap.get(taker) + Float.parseFloat(point));
			}
			point = Ranges.range(spreadsheet.getSelectedSheet(), row, 5).getCellEditText();
			if (!StringUtils.isEmpty(taker) && !StringUtils.isEmpty(point)) {
				pointMap.put(taker, pointMap.get(taker) + Float.parseFloat(point));
			}
		}
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
				this.validateEmpty(story, "setProduct", range = Ranges.range(spreadsheet.getSelectedSheet(), row, 0));
				this.validateEmpty(story, "setSummary", range = Ranges.range(spreadsheet.getSelectedSheet(), row, 1));
				this.validateNumber(story, "setDesignPoint", range = Ranges.range(spreadsheet.getSelectedSheet(), row, 3));
				this.validateNumber(story, "setCodePoint", range = Ranges.range(spreadsheet.getSelectedSheet(), row, 4));
				this.validateNumber(story, "setTestPoint", range = Ranges.range(spreadsheet.getSelectedSheet(), row, 5));
				this.validateNames(story, "setCodeTaker", range = Ranges.range(spreadsheet.getSelectedSheet(), row, 6));
				this.validateNames(story, "setTestTaker", range = Ranges.range(spreadsheet.getSelectedSheet(), row, 7));
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
			JiraRestClient client = JiraService.instanceClient(sysUser.getLoginName(), sysUser.getPassword());
			JiraService.createStories(client, backlog);
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

	private void createPdfTable(PdfPTable mainTable, ScrumStory story) throws Exception {
		PdfPTable table = new PdfPTable(1);
		table.addCell(this.createPdfCell(story.getJiraKey(), 15f, BaseColor.LIGHT_GRAY));
		table.addCell(this.createPdfCell(story.getSummary(), 100f, BaseColor.WHITE));
		table.addCell(this.createPdfCell("负责人: " + userNamesMap.get(story.getCodeTaker()) + " " + userNamesMap.get(story.getTestTaker()), 15f, BaseColor.LIGHT_GRAY));
		mainTable.addCell(table);
	}

	private PdfPCell createPdfCell(String text, float height, BaseColor color) throws Exception {
		BaseFont baseFont = BaseFont.createFont("C:\\Windows\\Fonts\\MSYH.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		PdfPCell cell = new PdfPCell(new Paragraph(text, new Font(baseFont, 8, Font.NORMAL)));
		cell.setFixedHeight(height);
		cell.setBackgroundColor(color);
		return cell;
	}

	public void onClick$print() throws Exception {
		File file = new File(new Date().getTime() + ".pdf");
		Document document = new Document(PageSize.A4, 0, 0, 30, 30);
		PdfWriter.getInstance(document, new FileOutputStream(file));
		document.open();
		PdfPTable mainTable = new PdfPTable(2);
		mainTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		for (int index = 0; index < backlog.getStories().size();) {
			this.createPdfTable(mainTable, backlog.getStories().get(index++));
			if (index < backlog.getStories().size()) {
				this.createPdfTable(mainTable, backlog.getStories().get(index++));
			} else {
				mainTable.addCell("");
			}
		}
		document.add(mainTable);
		document.close();
		Filedownload.save(new AMedia(backlog.getProject() + "_" + backlog.getModule() + ".pdf", null, null, file, true));
	}
}
