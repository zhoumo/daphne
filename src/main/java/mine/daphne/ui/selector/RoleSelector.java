package mine.daphne.ui.selector;

import mine.daphne.security.core.CommonUtil;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

@SuppressWarnings("serial")
public class RoleSelector extends Listbox {

	public RoleSelector(Component parent) {
		this.setParent(parent);
		this.setMold("select");
		this.setWidth("250px");
	}

	public void initAllRole() {
		for (String roleKey : CommonUtil.getRoleKeySet()) {
			Listitem item = new Listitem(CommonUtil.getRoleShowByRoleKey(roleKey));
			item.setValue(roleKey);
			item.setParent(this);
		}
		if (this.getItemCount() != 0) {
			this.getItemAtIndex(0).setSelected(true);
			Events.postEvent(Events.ON_SELECT, this, null);
		}
	}
}
