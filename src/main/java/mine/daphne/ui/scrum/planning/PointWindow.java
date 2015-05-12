package mine.daphne.ui.scrum.planning;

import java.util.Map;

import mine.daphne.ui.common.PopWindow;

import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

@SuppressWarnings("serial")
public class PointWindow extends PopWindow {

	private Listbox showList;

	private Map<String, Float> pointMap;

	@Override
	public void initPop() {
		for (String key : pointMap.keySet()) {
			Listitem item = new Listitem();
			item.appendChild(new Listcell(key));
			item.appendChild(new Listcell(pointMap.get(key).toString()));
			showList.appendChild(item);
		}
		int height = Integer.parseInt(this.getHeight().replace("px", ""));
		this.setHeight((height + pointMap.size() * 35) + "px");
	}

	public void setPointMap(Map<String, Float> pointMap) {
		this.pointMap = pointMap;
	}
}
