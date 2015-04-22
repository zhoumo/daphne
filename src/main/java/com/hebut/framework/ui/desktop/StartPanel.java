package com.hebut.framework.ui.desktop;

import java.util.List;

import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class StartPanel extends Window {

	private CssDiv modulePanel;

	private CssDiv toolPanel;

	private List<MenuModel> menuModel;

	private String[] portraitModel;

	public StartPanel(List<MenuModel> menuModel, String[] portraitModel) {
		this.menuModel = menuModel;
		this.portraitModel = portraitModel;
		this.setMode("overlapped");
		this.setWidth("250px");
		this.setHeight("350px");
		this.setZclass("start_panel");
		this.createPanelFrame();
		this.createModulePanel();
	}

	public CssDiv getToolPanel() {
		return this.toolPanel;
	}

	private void createPanelFrame() {
		new CssDiv("start_panel_top", this);
		Hbox hbox = new Hbox();
		hbox.setParent(this);
		hbox.setZclass("start_panel_center");
		{
			new CssDiv("start_panel_left", hbox);
			Vbox vbox = new Vbox();
			vbox.setParent(hbox);
			{
				Hbox titleBox = new Hbox();
				titleBox.setParent(new CssDiv("start_panel_title", vbox));
				titleBox.setAlign("center");
				{
					titleBox.appendChild(new Image(this.portraitModel[0]));
					titleBox.appendChild(new Label(this.portraitModel[1]));
				}
				Separator separator = new Separator();
				separator.setBar(true);
				separator.setHeight("1px");
				separator.setParent(vbox);
				Hbox mainPanel = new Hbox();
				mainPanel.setParent(vbox);
				{
					this.modulePanel = new CssDiv("start_panel_module", mainPanel);
					this.toolPanel = new CssDiv(null, mainPanel);
				}
			}
			new CssDiv("start_panel_right", hbox);
		}
		new CssDiv("start_panel_bottom", this);
	}

	private void createModulePanel() {
		for (MenuModel menuModel : this.menuModel) {
			this.modulePanel.appendChild(new StartItem("images/desktop/module.png", menuModel.getName(), menuModel));
		}
	}
}
