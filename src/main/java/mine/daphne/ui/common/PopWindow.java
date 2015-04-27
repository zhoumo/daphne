package mine.daphne.ui.common;

import org.zkoss.zk.ui.event.Events;

@SuppressWarnings("serial")
public abstract class PopWindow extends BaseWindow {

	@Override
	public void initWindow() {
		if (this.getFellowIfAny("submit") != null) {
			this.addForward(Events.ON_OK, this.getFellow("submit"), Events.ON_CLICK);
		}
		if (this.getFellowIfAny("cancel") != null) {
			this.addForward(Events.ON_CANCEL, this.getFellow("cancel"), Events.ON_CLICK);
		}
	}

	public abstract void initPop();
}
