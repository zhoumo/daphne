package mine.daphne.ui.manage.member;

import mine.daphne.model.entity.Group;
import mine.daphne.service.ManageService;
import mine.daphne.ui.common.PopWindow;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Textbox;

@SuppressWarnings("serial")
public class GroupWindow extends PopWindow {

	private Textbox groupName;

	private ManageService manageService;

	@Override
	public void initPop() {
		if (!Boolean.parseBoolean(this.getAttribute("isNew").toString())) {
			Group group = (Group) this.getAttribute("group");
			this.groupName.setValue(group.getName());
		}
	}

	public void onClick$submit() {
		Group group = (Group) this.getAttribute("group");
		group.setName(this.groupName.getValue());
		if (Boolean.parseBoolean(this.getAttribute("isNew").toString())) {
			Group parent = (Group) this.getAttribute("parent");
			group.setParent(parent);
			if (parent == null) {
				group.setLevel(0);
			} else {
				group.setLevel(parent.getLevel() + 1);
			}
		}
		this.manageService.saveOrUpdate(group);
		Events.postEvent(Events.ON_CHANGE, this, null);
		this.detach();
	}

	public void onClick$cancel() {
		this.detach();
	}
}
