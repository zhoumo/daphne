package com.hebut.framework.ui.desktop;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Div;

@SuppressWarnings("serial")
public class CssDiv extends Div {

	public CssDiv(String css, Object parent) {
		super();
		this.setZclass(css);
		try {
			parent.getClass().getMethod("appendChild", Component.class).invoke(parent, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
