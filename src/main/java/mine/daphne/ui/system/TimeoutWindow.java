package mine.daphne.ui.system;

import mine.daphne.ui.common.BaseWindow;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Progressmeter;
import org.zkoss.zul.Timer;

@SuppressWarnings("serial")
public class TimeoutWindow extends BaseWindow {

	private Progressmeter progressmeter;

	private Timer timer;

	private int counter = 5;

	@Override
	public void initWindow() {
	}

	public void onTimer$timer() {
		if (this.counter-- == 0) {
			this.timer.stop();
			Executions.getCurrent().sendRedirect("/");
			return;
		}
		this.progressmeter.setValue(this.progressmeter.getValue() + 20);
	}
}
