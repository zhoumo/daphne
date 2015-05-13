package mine.daphne.utils;

import java.util.Map;

import mine.daphne.model.entity.ScrumStory;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zss.api.AreaRef;
import org.zkoss.zss.api.CellOperationUtil;
import org.zkoss.zss.api.Range;
import org.zkoss.zss.api.Ranges;
import org.zkoss.zss.ui.Spreadsheet;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Window;

public class SheetUtil {

	public static Menuitem createMenuitem(String text, final Spreadsheet spreadsheet) {
		Menuitem menuitem = new Menuitem(text);
		menuitem.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Menuitem menuitem = (Menuitem) event.getTarget();
				AreaRef areaRef = spreadsheet.getSelection();
				Ranges.range(spreadsheet.getSelectedSheet(), areaRef.getRow(), areaRef.getColumn()).setCellEditText(menuitem.getLabel());
			}
		});
		return menuitem;
	}

	public static void enabledMenuitems(Window window) {
		for (Component component : window.getFellows()) {
			if (component instanceof Menuitem) {
				((Menuitem) component).setDisabled(false);
			}
		}
	}

	public static void buildPointMap(Spreadsheet spreadsheet, Map<String, Float> pointMap) {
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
	}

	public static void validateEmpty(ScrumStory story, String methodName, Range range) throws Exception {
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

	public static void validateNumber(ScrumStory story, String methodName, Range range) throws Exception {
		CellOperationUtil.clearStyles(range);
		try {
			ScrumStory.class.getMethod(methodName, Float.class).invoke(story, new Object[] { Float.parseFloat(range.getCellEditText()) });
		} catch (Exception e) {
			throw new Exception();
		}
	}

	public static void validateNames(ScrumStory story, String methodName, Range range, Map<String, String> userNamesMap) throws Exception {
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
}
