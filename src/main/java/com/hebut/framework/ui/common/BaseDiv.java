package com.hebut.framework.ui.common;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Div;

@SuppressWarnings("serial")
public class BaseDiv extends Div {

	public BaseDiv(String css, Object parent) {
		this.setZclass(css);
		try {
			parent.getClass().getMethod("appendChild", Component.class).invoke(parent, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
