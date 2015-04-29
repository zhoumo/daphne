package mine.daphne.ui.scrum.planning;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mine.daphne.ui.common.BaseWindow;

import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zss.api.CellOperationUtil;
import org.zkoss.zss.api.Importer;
import org.zkoss.zss.api.Importers;
import org.zkoss.zss.api.Ranges;
import org.zkoss.zss.api.model.Book;
import org.zkoss.zss.ui.Spreadsheet;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Menupopup;

@SuppressWarnings("serial")
public class BacklogWindow extends BaseWindow {

	private Spreadsheet spreadsheet;

	private Menupopup memberMenu;

	private void createMemberMenu() {
		List<String> memberList = new ArrayList<String>();
		memberList.addAll(Arrays.asList("周默", "姚荣飞", "赖寿生"));
		for (String memberName : memberList) {
			Menuitem menuitem = new Menuitem(memberName);
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
		Ranges.range(spreadsheet.getSelectedSheet()).setFreezePanel(1, 0);
	}

	public void onClick$mergeButton() {
		CellOperationUtil.merge(Ranges.range(spreadsheet.getSelectedSheet(), spreadsheet.getSelection()), false);
	}

	public void onClick$insertLine() {
		spreadsheet.setMaxVisibleRows(spreadsheet.getMaxVisibleRows() + 1);
		CellOperationUtil.insertRow(Ranges.range(spreadsheet.getSelectedSheet(), spreadsheet.getSelection()));
	}
}
