package mine.daphne.ui.common;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Div;

@SuppressWarnings("serial")
public class CustomDiv extends Div {

	public CustomDiv(String css, Object parent) {
		this.setZclass(css);
		try {
			parent.getClass().getMethod("appendChild", Component.class).invoke(parent, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
