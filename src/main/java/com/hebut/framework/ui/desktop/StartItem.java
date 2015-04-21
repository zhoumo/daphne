package com.hebut.framework.ui.desktop;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;

@SuppressWarnings("serial")
public class StartItem extends Hbox implements AfterCompose {

	private Div iconDiv = new Div();

	private Div titleDiv = new Div();

	private Div moreDiv = new Div();

	private MenuModel menuModel;

	public StartItem(String icon, String label, MenuModel menuModel) {
		super();
		this.iconDiv.setWidth("30px");
		this.iconDiv.setAlign("center");
		this.iconDiv.appendChild(new Image(icon));
		this.titleDiv.setWidth("85px");
		this.titleDiv.appendChild(new Label(label));
		this.menuModel = menuModel;
		this.appendChild(this.iconDiv);
		this.appendChild(this.titleDiv);
		this.appendChild(this.moreDiv);
		this.afterCompose();
	}

	@Override
	public void afterCompose() {
		Components.wireVariables(this, this);
		Components.addForwards(this, this);
		if (this.menuModel.getChildren().size() != 0) {
			this.moreDiv.setZclass("start_panel_module_more");
			MenuPanel menuPanel = new MenuPanel(this, this.menuModel.getChildren());
			menuPanel.setVisible(false);
			menuPanel.createMenu();
			menuPanel.setParent(this);
		} else {
			this.moreDiv.setZclass("start_panel_module_more_hidden");
		}
		this.setZclass("start_panel_module_item");
		this.setAlign("center");
		this.addMouseEvent(Events.ON_MOUSE_OVER, "start_panel_module_item_hover", "start_panel_module_more_hidden_hover", this.moreDiv);
		this.addMouseEvent(Events.ON_MOUSE_OUT, "start_panel_module_item", "start_panel_module_more_hidden", this.moreDiv);
	}

	private void addMouseEvent(String eventName, final String css, final String cssMore, final Div moreDiv) {
		this.addEventListener(eventName, new EventListener() {

			@Override
			public void onEvent(Event event) throws Exception {
				StartItem startItem = (StartItem) event.getTarget();
				startItem.setZclass(css);
				if (startItem.menuModel.getChildren().size() == 0) {
					moreDiv.setZclass(cssMore);
				}
			}
		});
	}
}
