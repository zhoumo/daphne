package mine.daphne.ui.manage.member;

import mine.daphne.model.entity.Group;
import mine.daphne.service.ManageService;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Label;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Treerow;

public class GroupTreeRenderer implements TreeitemRenderer<Group> {

	private ManageService manageService;

	private EventListener<Event> clickEventListener;

	public GroupTreeRenderer(ManageService manageService, EventListener<Event> clickEventListener) {
		this.manageService = manageService;
		this.clickEventListener = clickEventListener;
	}

	@Override
	public void render(Treeitem item, Group group, int index) throws Exception {
		Treerow row = new Treerow();
		{
			Treecell c0 = new Treecell();
			c0.appendChild(new Label(group.getName()));
			c0.setParent(row);
			Treecell c1 = new Treecell();
			Integer number = this.manageService.countUsersByGroup(group);
			c1.appendChild(new Label(number.toString()));
			c1.setParent(row);
			item.setAttribute("number", number);
		}
		item.appendChild(row);
		item.setValue(group);
		item.setOpen(true);
		if (group.getSelected().booleanValue()) {
			item.setSelected(true);
		}
		if (this.clickEventListener != null) {
			item.addEventListener(Events.ON_CLICK, this.clickEventListener);
		}
	}
}
