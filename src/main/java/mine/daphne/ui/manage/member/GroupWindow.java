package mine.daphne.ui.manage.member;

import mine.daphne.model.entity.SysGroup;
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
		Object isNew = this.getAttribute("isNew");
		if (!Boolean.parseBoolean(isNew == null ? "true" : isNew.toString())) {
			SysGroup group = (SysGroup) this.getAttribute("group");
			this.groupName.setValue(group.getName());
		}
	}

	public void onClick$submit() {
		SysGroup group = (SysGroup) this.getAttribute("group");
		group.setName(this.groupName.getValue());
		if (Boolean.parseBoolean(this.getAttribute("isNew").toString())) {
			SysGroup parent = (SysGroup) this.getAttribute("parent");
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
}
