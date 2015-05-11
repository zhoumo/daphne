package mine.daphne.ui.scrum.planning;

import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;

import mine.daphne.model.entity.SysGroup;
import mine.daphne.service.ManageService;
import mine.daphne.ui.common.PopWindow;

@SuppressWarnings("serial")
public class CreateWindow extends PopWindow {

	private Listbox project, module;

	@Autowired
	private ManageService manageService;

	@Override
	public void initPop() {
		for (SysGroup group : manageService.findAllGroup()) {
			Listitem item = new Listitem(group.getName());
			item.setValue(group.getId());
			project.appendChild(item);
		}
		project.setSelectedIndex(0);
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
