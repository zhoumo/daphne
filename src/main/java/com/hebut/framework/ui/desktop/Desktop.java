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

import com.hebut.framework.util.CookieUtil;
import com.hebut.framework.util.SessionUtil;

@SuppressWarnings("serial")
public abstract class Desktop extends Window implements AfterCompose {

	private CssDiv toolPanel;

	protected Map<String, String[]> shortcutModel = new HashMap<String, String[]>();

	protected List<MenuModel> menuModel = new ArrayList<MenuModel>();

	protected String[] portraitModel;

	@Override
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		SessionUtil.clearWindowSessions();
		this.setWidth("100%");
		this.setHeight("100%");
		this.initModel();
		this.createDesktopArea();
		this.createTaskbar();
		this.createStartPanel();
		this.initToolbarbuttons();
	}

	protected abstract void initModel();

	protected abstract void initToolbarbuttons();

	protected void appendToolbarbuttons(Toolbarbutton[] toolbarbuttons) {
		for (Toolbarbutton toolbarbutton : toolbarbuttons) {
			toolbarbutton.setParent(this.toolPanel);
			toolbarbutton.setStyle("color: white");
			this.addMouseEvent(toolbarbutton, Events.ON_MOUSE_OVER, "black");
			this.addMouseEvent(toolbarbutton, Events.ON_MOUSE_OUT, "white");
		}
	}

	private void createDesktopArea() {
		CssDiv desktopArea = null;
		if (CookieUtil.getBrowserType().equals("MSIE6.0") || CookieUtil.getBrowserType().equals("Firefox")) {
			desktopArea = new CssDiv("desktop_special", this);
		} else {
			desktopArea = new CssDiv("desktop_standard", this);
		}
		int index = 0, row = 0, column = -1;
		for (String key : this.shortcutModel.keySet()) {
			row = index % CookieUtil.getMaxRowNumber();
			if (row == 0) {
				column++;
			}
			String[] value = this.shortcutModel.get(key);
			String css = "position: absolute;left: " + (column * 90) + "px;top: " + (row * 85) + "px";
			desktopArea.appendChild(new Shortcut("images/desktop/shortcut.png", value[0], value[1], css));
			index++;
		}
		desktopArea.setDroppable("true");
		desktopArea.addEventListener(Events.ON_CLICK, new EventListener() {

			@Override
			public void onEvent(Event event) throws Exception {
				event.getTarget().getPage().getFellow("startPanel").setVisible(false);
			}
		});
		desktopArea.addEventListener(Events.ON_DROP, new EventListener() {

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
		CssDiv taskbar = null;
		if (CookieUtil.getBrowserType().equals("MSIE6.0") || CookieUtil.getBrowserType().equals("Firefox")) {
			taskbar = new CssDiv("taskbar_special", this);
		} else {
			taskbar = new CssDiv("taskbar_standard", this);
		}
		new CssDiv("start_button", taskbar).addEventListener(Events.ON_CLICK, new EventListener() {

			@Override
			public void onEvent(Event event) throws Exception {
				StartPanel startPanel = (StartPanel) event.getTarget().getPage().getFellow("startPanel");
				if (startPanel.isVisible()) {
					startPanel.setVisible(false);
				} else {
					startPanel.setZIndex(SessionUtil.getZindex());
					startPanel.setVisible(true);
				}
			}
		});
	}

	private void createStartPanel() {
		StartPanel startPanel = new StartPanel(this.menuModel, this.portraitModel);
		startPanel.setPage(this.getPage());
		startPanel.setId("startPanel");
		if (!CookieUtil.getBrowserType().equals("Firefox")) {
			startPanel.setVisible(false);
		}
		this.toolPanel = startPanel.getToolPanel();
	}

	private void addMouseEvent(final Toolbarbutton toolbarbutton, String eventName, final String color) {
		toolbarbutton.addEventListener(eventName, new EventListener() {

			@Override
			public void onEvent(Event event) throws Exception {
				toolbarbutton.setStyle("color: " + color);
			}
		});
	}
}