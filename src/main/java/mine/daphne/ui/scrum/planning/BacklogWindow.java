package mine.daphne.ui.scrum.planning;

import java.io.File;
import java.io.IOException;

import mine.daphne.ui.common.BaseWindow;

import org.zkoss.zk.ui.WebApps;
import org.zkoss.zss.api.CellOperationUtil;
import org.zkoss.zss.api.Importer;
import org.zkoss.zss.api.Importers;
import org.zkoss.zss.api.Ranges;
import org.zkoss.zss.api.model.Book;
import org.zkoss.zss.ui.Spreadsheet;

@SuppressWarnings("serial")
public class BacklogWindow extends BaseWindow {

	private Spreadsheet spreadsheet;

	public void onClick$mergeButton() {
		CellOperationUtil.merge(Ranges.range(spreadsheet.getSelectedSheet(), spreadsheet.getSelection()), false);
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
	}
}
