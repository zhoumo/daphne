package com.hebut.framework.ui.desktop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import com.hebut.framework.model.vo.Menu;
import com.hebut.framework.service.SessionService;
import com.hebut.framework.ui.common.BaseDiv;

@SuppressWarnings("serial")
public abstract class Desktop extends Window implements AfterCompose {

	private BaseDiv toolPanel;

	protected Map<String, String[]> shortcut = new HashMap<String, String[]>();

	protected List<Menu> menu = new ArrayList<Menu>();

	protected String[] portrait;

	@Override
	@SuppressWarnings("deprecation")
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		SessionService.clearWindowSessions();
		this.setWidth("100%");
		this.setHeight("100%");
		this.init();
		this.createDesktopArea();
		this.createTaskbar();
		this.createStartPanel();
		this.initToolbarbuttons();
	}

	protected abstract void init();

	protected abstract void initToolbarbuttons();

	protected void appendToolbarbuttons(Toolbarbutton[] toolbarbuttons) {
		for (Toolbarbutton toolbarbutton : toolbarbuttons) {
			toolbarbutton.setParent(this.toolPanel);
			toolbarbutton.setStyle("color: white; font-size: 12px");
		}
	}

	private void createDesktopArea() {
		BaseDiv desktopArea = new BaseDiv("desktop", this);
		int index = 0, row = 0, column = -1;
		for (String key : this.shortcut.keySet()) {
			row = index % 5;
			if (row == 0) {
				column++;
			}
			String[] value = this.shortcut.get(key);
			String css = "position: absolute;left: " + (column * 90) + "px;top: " + (row * 85) + "px";
			desktopArea.appendChild(new Shortcut("images/desktop/shortcut.png", value[0], value[1], css));
			index++;
		}
		desktopArea.setDroppable("true");
		desktopArea.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				event.getTarget().getPage().getFellow("startPanel").setVisible(false);
			}
		});
		desktopArea.addEventListener(Events.ON_DROP, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				DropEvent dropEvent = (DropEvent) event;
				if (dropEvent.getDragged() instanceof Shortcut) {
					((Shortcut) dropEvent.getDragged()).setStyle("position: absolute;left: " + dropEvent.getPageX() + ";top: " + dropEvent.getPageY());
				}
			}
		});
	}

	private void createTaskbar() {
		BaseDiv taskbar = new BaseDiv("taskbar", this);
		new BaseDiv("start_button", taskbar).addEventListener(Events.ON_CLICK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				StartPanel startPanel = (StartPanel) event.getTarget().getPage().getFellow("startPanel");
				if (startPanel.isVisible()) {
					startPanel.setVisible(false);
				} else {
					startPanel.setZIndex(SessionService.getZindex());
					startPanel.setVisible(true);
				}
			}
		});
	}

	private void createStartPanel() {
		StartPanel startPanel = new StartPanel(this.menu, this.portrait);
		startPanel.setPage(this.getPage());
		startPanel.setId("startPanel");
		startPanel.setVisible(true);
		this.toolPanel = startPanel.getToolPanel();
	}
}
