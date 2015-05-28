package mine.daphne.ui.scrum.planning;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import mine.daphne.model.entity.SysGroup;
import mine.daphne.service.ManageService;
import mine.daphne.ui.common.PopWindow;

@SuppressWarnings("serial")
public class CreateWindow extends PopWindow {

	private ManageService manageService;

	private Listbox project, module;

	@Override
	public void initPop() {
		for (SysGroup group : manageService.findRootGroup(null)) {
			Listitem item = new Listitem(group.getName());
			item.setValue(group.getId());
			project.appendChild(item);
		}
		project.setSelectedIndex(0);
		for (SysGroup group : manageService.findChildrenGroups((Long) project.getSelectedItem().getValue())) {
			Listitem item = new Listitem(group.getName());
			item.setValue(group.getId());
			module.appendChild(item);
		}
		module.setSelectedIndex(0);
	}

	public void onClick$submit() throws Exception {
		Events.postEvent(Events.ON_CHANGE, this, null);
		this.detach();
	}

	public Listitem getProject() {
		return project.getSelectedItem();
	}

	public String getModule() {
		return module.getSelectedItem().getLabel();
	}
}
