package mine.daphne.ui.scrum.planning;

import mine.daphne.ui.common.PopWindow;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;

@SuppressWarnings("serial")
public class SyncJiraWindow extends PopWindow {

	private Textbox userName, password;

	private Listbox module;

	@Override
	public void initPop() {
	}

	public void onClick$submit() throws Exception {
		Events.postEvent(Events.ON_CHANGE, this, null);
		this.detach();
	}

	public String getUserName() {
		return userName.getText();
	}

	public String getPassword() {
		return password.getText();
	}

	public String getModule() {
		return module.getSelectedItem().getLabel();
	}
}
