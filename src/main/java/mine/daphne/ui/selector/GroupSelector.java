package mine.daphne.ui.selector;

import mine.daphne.model.entity.Group;
import mine.daphne.service.ManageService;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

@SuppressWarnings("serial")
public class GroupSelector extends Listbox {

	private ManageService manageService;

	public GroupSelector(ManageService manageService, Component parent) {
		this.manageService = manageService;
		this.setParent(parent);
		this.setMold("select");
		this.setWidth("250px");
		this.setItemRenderer(new ListitemRenderer<Group>() {

			@Override
			public void render(Listitem item, Group group, int index) throws Exception {
				String blank = "| ";
				for (int i = 0; i < group.getLevel(); i++) {
					blank += "----";
				}
				item.setLabel(blank + group.getName());
				item.setValue(group);
			}
		});
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void initAllGroup() {
		this.setModel(new ListModelList(this.manageService.buildGroupTree()));
		if (this.getItemCount() != 0) {
			this.getItemAtIndex(0).setSelected(true);
			Events.postEvent(Events.ON_SELECT, this, null);
		}
	}
}
