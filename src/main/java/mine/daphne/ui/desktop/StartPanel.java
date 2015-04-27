package mine.daphne.ui.desktop;

import java.util.List;

import mine.daphne.model.vo.Menu;
import mine.daphne.ui.common.CustomDiv;

import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Vbox;

@SuppressWarnings("serial")
public class StartPanel extends Div {

	private CustomDiv modulePanel;

	private CustomDiv toolPanel;

	private List<Menu> menu;

	private String[] portrait;

	public StartPanel(List<Menu> menu, String[] portrait) {
		this.menu = menu;
		this.portrait = portrait;
		this.setWidth("250px");
		this.setHeight("350px");
		this.setZclass("start_panel");
		this.createPanelFrame();
		this.createModulePanel();
	}

	public CustomDiv getToolPanel() {
		return this.toolPanel;
	}

	private void createPanelFrame() {
		new CustomDiv("start_panel_top", this);
		Hbox hbox = new Hbox();
		hbox.setParent(this);
		hbox.setZclass("start_panel_center");
		{
			new CustomDiv("start_panel_left", hbox);
			Vbox vbox = new Vbox();
			vbox.setParent(hbox);
			{
				Hbox titleBox = new Hbox();
				titleBox.setParent(new CustomDiv("start_panel_title", vbox));
				titleBox.setAlign("center");
				{
					titleBox.appendChild(new Image(this.portrait[0]));
					titleBox.appendChild(new Label(this.portrait[1]));
				}
				Separator separator = new Separator();
				separator.setBar(true);
				separator.setHeight("1px");
				separator.setParent(vbox);
				Hbox mainPanel = new Hbox();
				mainPanel.setParent(vbox);
				{
					this.modulePanel = new CustomDiv("start_panel_module", mainPanel);
					this.toolPanel = new CustomDiv(null, mainPanel);
				}
			}
			new CustomDiv("start_panel_right", hbox);
		}
		new CustomDiv("start_panel_bottom", this);
	}

	private void createModulePanel() {
		for (Menu menu : this.menu) {
			this.modulePanel.appendChild(new StartItem("images/desktop/module.png", menu.getName(), menu));
		}
	}
}
