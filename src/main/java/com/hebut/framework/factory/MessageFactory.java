package com.hebut.framework.factory;

import org.zkoss.zul.Messagebox;

public class MessageFactory {

	public static void saveMessage() {
		try {
			Messagebox.show("保存成功！", "提示", Messagebox.OK, Messagebox.INFORMATION);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void deleteMessage() {
		try {
			Messagebox.show("删除成功！", "提示", Messagebox.OK, Messagebox.INFORMATION);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void showMessage(String message) {
		try {
			Messagebox.show(message, "提示", Messagebox.OK, Messagebox.INFORMATION);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
