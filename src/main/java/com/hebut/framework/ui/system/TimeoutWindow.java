package com.hebut.framework.ui.system;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Progressmeter;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class TimeoutWindow extends Window implements AfterCompose {

	private Progressmeter progressmeter;

	private Timer timer;

	private int counter = 5;

	@Override
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
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
