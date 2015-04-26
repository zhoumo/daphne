package com.hebut.framework.ui.desktop;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import com.hebut.framework.model.vo.Menu;
import com.hebut.framework.security.ComponentCheck;
import com.hebut.framework.service.SessionService;
import com.hebut.framework.ui.common.BaseDiv;
import com.hebut.framework.ui.common.BaseWindow;

@SuppressWarnings("serial")
public class MenuPanel extends Window implements AfterCompose {

	private Div menuLeft = new Div();

	private Div menuDiv = new Div();

	private Object parent;

	private List<Menu> menuList;

	public MenuPanel(Object parent, List<Menu> menuList) {
		this.parent = parent;
		this.menuList = menuList;
		this.afterCompose();
	}

	@Override
	@SuppressWarnings("deprecation")
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		Hbox hbox = new Hbox();
		hbox.appendChild(this.menuLeft);
		hbox.appendChild(this.menuDiv);
		this.appendChild(hbox);
		this.setZclass("start_panel_menu");
		this.setZindex(SessionService.getZindex());
		this.addParentMouseEvent(Events.ON_MOUSE_OVER, true);
		this.addParentMouseEvent(Events.ON_MOUSE_OUT, false);
		this.menuLeft.setZclass("start_panel_menu_left");
	}

	public void createMenu() {
		int counter = 0;
		for (final Menu menu : this.menuList) {
			Hbox hbox = new Hbox();
			hbox.setAlign("center");
			hbox.setZclass("start_panel_menu_item");
			hbox.setParent(this.menuDiv);
			{
				new BaseDiv("start_panel_menu_icon", hbox);
				new BaseDiv("start_panel_menu_label", hbox).appendChild(new Label(menu.getName()));
				BaseDiv moreDiv = new BaseDiv(null, hbox);
				if (menu.getChildren().size() != 0) {
					moreDiv.setZclass("start_panel_menu_more");
					MenuPanel menuPanel = new MenuPanel(hbox, menu.getChildren());
					menuPanel.setVisible(false);
					menuPanel.createMenu();
					menuPanel.setTop(counter * 25 + "px");
					menuPanel.setParent(this);
				} else {
					moreDiv.setZclass("start_panel_menu_more_hidden");
				}
			}
			this.addMouseEvent(hbox, Events.ON_MOUSE_OVER, "start_panel_menu_item_hover", menu.getChildren().size() == 0 ? true : false, "start_panel_menu_more_hidden_hover");
			this.addMouseEvent(hbox, Events.ON_MOUSE_OUT, "start_panel_menu_item", menu.getChildren().size() == 0 ? true : false, "start_panel_menu_more_hidden");
			if (menu.getValue() != null) {
				hbox.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						event.getTarget().getPage().getFellow("startPanel").setVisible(false);
						BaseWindow baseWindow = SessionService.getWindowInSession(menu.getName());
						if (baseWindow == null) {
							if (menu.getValue().toString().startsWith("http://")) {
								Map<Object, Object> argMap = new HashMap<Object, Object>();
								if (menu.getValue().toString().endsWith("client")) {
									argMap.put("url", menu.getValue().toString() + "?rbac=" + JSONObject.fromObject(SessionService.getUserInfoSession()).toString());
								} else {
									argMap.put("url", menu.getValue().toString());
								}
								baseWindow = (BaseWindow) ComponentCheck.createComponents(event.getTarget(), BaseWindow.SYSTEM_IFRAME, null, argMap);
							} else {
								baseWindow = (BaseWindow) ComponentCheck.createComponents(event.getTarget(), menu.getValue().toString(), null, null);
							}
							baseWindow.setTitle(menu.getName());
							baseWindow.doOverlapped();
							SessionService.createWindowSession(menu.getName(), baseWindow);
						} else {
							baseWindow.setZIndex(SessionService.getZindex());
						}
					}
				});
			}
			counter++;
		}
		this.menuLeft.setHeight(counter * 25 + "px");
	}

	private void addMouseEvent(Hbox hbox, final String eventName, final String css, final boolean isMore, final String cssMore) {
		hbox.addEventListener(eventName, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Hbox hbox = (Hbox) event.getTarget();
				hbox.setZclass(css);
				if (eventName.equals(Events.ON_MOUSE_OVER)) {
					visible(true);
				}
				if (isMore) {
					((Div) hbox.getLastChild()).setZclass(cssMore);
				}
			}
		});
	}

	private void addParentMouseEvent(String eventName, final boolean b) {
		try {
			Method method = this.parent.getClass().getMethod("addEventListener", String.class, EventListener.class);
			method.invoke(this.parent, eventName, new EventListener<Event>() {

				@Override
				public void onEvent(Event event) throws Exception {
					visible(b);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void visible(boolean b) {
		this.setVisible(b);
	}
}
