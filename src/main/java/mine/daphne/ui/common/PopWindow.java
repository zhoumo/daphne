package mine.daphne.ui.common;

import org.zkoss.zk.ui.event.Events;

@SuppressWarnings("serial")
public abstract class PopWindow extends BaseWindow {

	public abstract void initPop();

	@Override
	public void initWindow() {
		if (this.getFellowIfAny("cancel") != null) {
			this.addForward(Events.ON_CANCEL, this.getFellow("cancel"), Events.ON_CLICK);
		}
		initPop();
	}

	public void onClick$cancel() {
		this.detach();
	}
}
