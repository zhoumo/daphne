package mine.daphne.ui.manage.actor;

import mine.daphne.service.ManageService;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Label;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;

public class RoleTreeRenderer implements TreeitemRenderer<String[]> {

	private ManageService manageService;

	private EventListener<Event> clickEventListener;

	public RoleTreeRenderer(ManageService manageService, EventListener<Event> clickEventListener) {
		this.manageService = manageService;
		this.clickEventListener = clickEventListener;
	}

	@Override
	public void render(Treeitem item, String[] roleInfo, int index) throws Exception {
		Treerow row = new Treerow();
		{
			Treecell c0 = new Treecell();
			c0.appendChild(new Label(roleInfo[0]));
			c0.setParent(row);
			Treecell c1 = new Treecell();
			Integer number = manageService.countUsersByRoleKey(roleInfo[1]);
			c1.appendChild(new Label(number.toString()));
			c1.setParent(row);
			item.setAttribute("number", number);
		}
		item.appendChild(row);
		item.setValue(roleInfo);
		item.setOpen(true);
		item.setSelected(Boolean.parseBoolean(roleInfo[2]));
		if (this.clickEventListener != null) {
			item.addEventListener(Events.ON_CLICK, this.clickEventListener);
		}
	}
}
