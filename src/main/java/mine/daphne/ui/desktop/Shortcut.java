package mine.daphne.ui.desktop;

import java.util.HashMap;
import java.util.Map;

import mine.daphne.security.ComponentCheck;
import mine.daphne.ui.common.BaseWindow;
import mine.daphne.utils.SessionUtil;
import net.sf.json.JSONObject;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.util.ConventionWires;
import org.zkoss.zul.Div;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

@SuppressWarnings("serial")
public class Shortcut extends Div implements AfterCompose {

	private Image icon = new Image();

	private Label label = new Label();

	private String path;

	private String css;

	public Shortcut(String icon, String label, String path, String css) {
		this.icon.setSrc(icon);
		this.label.setValue(label);
		this.path = path;
		this.css = css;
		this.afterCompose();
	}

	public Label getLabel() {
		return label;
	}

	@Override
	public void afterCompose() {
		ConventionWires.wireVariables(this, this);
		ConventionWires.addForwards(this, this);
		Vbox vbox = new Vbox();
		vbox.setAlign("center");
		vbox.appendChild(this.icon);
		vbox.appendChild(this.label);
		this.setStyle(this.css);
		this.setZclass("shortcut");
		this.setDraggable("true");
		this.appendChild(vbox);
		this.addMouseEvent(Events.ON_MOUSE_OVER, "cursor: pointer;opacity: 0.8;filter: alpha(opacity=80)");
		this.addMouseEvent(Events.ON_MOUSE_OUT, "cursor: pointer;opacity: 1.0;filter: alpha(opacity=100)");
		this.addEventListener(Events.ON_DOUBLE_CLICK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Shortcut shortcut = (Shortcut) event.getTarget();
				Window window = SessionUtil.getWindowInSession(shortcut.label.getValue());
				if (window == null) {
					if (shortcut.path.startsWith("http://")) {
						Map<Object, Object> argMap = new HashMap<Object, Object>();
						if (shortcut.path.endsWith("client")) {
							argMap.put("url", shortcut.path + "?rbac=" + JSONObject.fromObject(SessionUtil.getUserInfoSession()).toString());
						} else {
							argMap.put("url", shortcut.path);
						}
						window = (Window) ComponentCheck.createComponents(event.getTarget(), BaseWindow.SYSTEM_IFRAME, null, argMap);
					} else {
						window = (Window) ComponentCheck.createComponents(event.getTarget(), shortcut.path, null, null);
					}
					window.setTitle(shortcut.label.getValue());
					window.doOverlapped();
					SessionUtil.createWindowSession(shortcut.label.getValue(), window);
				} else {
					window.setZIndex(SessionUtil.getZindex());
				}
			}
		});
	}

	private void addMouseEvent(String eventName, final String css) {
		this.icon.addEventListener(eventName, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Image image = (Image) event.getTarget();
				image.setStyle(css);
			}
		});
	}
}
