package mine.daphne.ui.desktop;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mine.daphne.model.vo.Menu;
import mine.daphne.security.ComponentCheck;
import mine.daphne.service.SessionService;
import mine.daphne.ui.common.BaseWindow;
import mine.daphne.ui.common.CustomDiv;
import net.sf.json.JSONObject;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.util.ConventionWires;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class MenuPanel extends Div implements AfterCompose {

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
	public void afterCompose() {
		ConventionWires.wireVariables(this, this);
		ConventionWires.addForwards(this, this);
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
				new CustomDiv("start_panel_menu_icon", hbox);
				new CustomDiv("start_panel_menu_label", hbox).appendChild(new Label(menu.getName()));
				CustomDiv moreDiv = new CustomDiv(null, hbox);
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
						Window window = SessionService.getWindowInSession(menu.getName());
						if (window == null) {
							if (menu.getValue().toString().startsWith("http://")) {
								Map<Object, Object> argMap = new HashMap<Object, Object>();
								if (menu.getValue().toString().endsWith("client")) {
									argMap.put("url", menu.getValue().toString() + "?rbac=" + JSONObject.fromObject(SessionService.getUserInfoSession()).toString());
								} else {
									argMap.put("url", menu.getValue().toString());
								}
								window = (Window) ComponentCheck.createComponents(event.getTarget(), BaseWindow.SYSTEM_IFRAME, null, argMap);
							} else {
								window = (Window) ComponentCheck.createComponents(event.getTarget(), menu.getValue().toString(), null, null);
							}
							window.setTitle(menu.getName());
							window.doOverlapped();
							SessionService.createWindowSession(menu.getName(), window);
						} else {
							window.setZIndex(SessionService.getZindex());
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
