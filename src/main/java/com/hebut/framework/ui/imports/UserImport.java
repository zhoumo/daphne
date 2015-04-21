package com.hebut.framework.ui.imports;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;

import com.hebut.framework.entity.FwkGroup;
import com.hebut.framework.entity.FwkUser;
import com.hebut.framework.service.ManageService;
import com.hebut.framework.ui.common.PopWindow;
import com.hebut.framework.ui.selector.GroupSelector;
import com.hebut.framework.ui.selector.RoleSelector;
import com.hebut.framework.util.ExcelUtil;
import com.hebut.framework.util.UploadUtil;
import com.hebut.rbac.core.AuthorityParser;
import com.hebut.rbac.core.CommonUtil;

@SuppressWarnings("serial")
public class UserImport extends PopWindow {

	public static final String ROLE_TYPE = "ROLE", GROUP_TYPE = "GROUP";

	private ManageService manageService;

	private Button previous, next, finish, upload;

	private Row roleRow, groupRow;

	private Textbox fileName;

	private Label validate, number, time;

	private int stepCurrent = 1;

	private int stepTotal = 3;

	public void initWindow(String type, Object parameter) {
		if (type.equals(ROLE_TYPE)) {
			Label roleLabel = new Label(CommonUtil.getRoleShowByRoleKey(((String[]) parameter)[1]));
			roleLabel.setParent(this.roleRow);
			this.roleRow.setAttribute("role", ((String[]) parameter)[1]);
			GroupSelector groupSelector = new GroupSelector(this.manageService, this.groupRow);
			groupSelector.addEventListener(Events.ON_SELECT, new EventListener() {

				@Override
				public void onEvent(Event event) throws Exception {
					GroupSelector groupSelector = (GroupSelector) event.getTarget();
					Row row = (Row) groupSelector.getParent();
					row.setAttribute("group", groupSelector.getSelectedItem().getValue());
				}
			});
			groupSelector.initAllGroup();
		} else if (type.equals(GROUP_TYPE)) {
			RoleSelector roleSelector = new RoleSelector(this.roleRow);
			roleSelector.initAllRole();
			roleSelector.addEventListener(Events.ON_SELECT, new EventListener() {

				@Override
				public void onEvent(Event event) throws Exception {
					RoleSelector roleSelector = (RoleSelector) event.getTarget();
					Row row = (Row) roleSelector.getParent();
					row.setAttribute("role", roleSelector.getSelectedItem().getValue());
				}
			});
			Label groupLabel = new Label(((FwkGroup) parameter).getFgName());
			groupLabel.setParent(this.groupRow);
			this.groupRow.setAttribute("group", parameter);
		}
		this.initStep();
	}

	public void onClick$upload() {
		try {
			String uploadPath = UploadUtil.saveFile("test", UploadUtil.DEFAULT_FILENAME, UploadUtil.EXCEL_TYPE);
			this.fileName.setValue(UploadUtil.formatFilePath(uploadPath));
			this.fileName.setAttribute("uploadPath", uploadPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onClick$previous() {
		if (this.stepCurrent == 2) {
			this.initStep();
		}
		this.visibleAll();
		this.getFellowIfAny("step" + (--this.stepCurrent)).setVisible(true);
		this.buttonControl();
	}

	public void onClick$next() {
		switch (this.stepCurrent) {
			case 1:
				if ("".equals(this.fileName.getValue())) {
					throw new WrongValueException(this.upload, "请先上传EXCEL！");
				} else {
					this.validateStep();
				}
				break;
			case 2:
				if (Boolean.parseBoolean(this.validate.getAttribute("state").toString())) {
					this.importStep();
				} else {
					throw new WrongValueException(this.validate, "请先通过验证！");
				}
				break;
		}
		this.visibleAll();
		this.getFellowIfAny("step" + (++this.stepCurrent)).setVisible(true);
		this.buttonControl();
	}

	public void onClick$finish() {
		Events.postEvent(Events.ON_CHANGE, this, null);
		this.detach();
	}

	private void visibleAll() {
		for (int i = 0; i < this.stepTotal; i++) {
			this.getFellowIfAny("step" + this.stepCurrent).setVisible(false);
		}
	}

	private void buttonControl() {
		if (this.stepCurrent == 1) {
			this.previous.setVisible(false);
		} else {
			this.previous.setVisible(true);
		}
		if (this.stepCurrent == this.stepTotal) {
			this.next.setVisible(false);
			this.previous.setVisible(false);
			this.finish.setVisible(true);
		} else {
			this.next.setVisible(true);
		}
	}

	private void initStep() {
		this.fileName.setValue("");
	}

	private void validateStep() {
		if (ExcelUtil.validateExcel(new File(this.fileName.getAttribute("uploadPath").toString()), 0, ExcelUtil.USER_VALIDATE_STRING)) {
			this.validate.setValue("通过验证");
			this.validate.setAttribute("state", "true");
		} else {
			this.validate.setValue("验证失败！请检查EXCEL表头是否符合要求...");
			this.validate.setAttribute("state", "false");
		}
	}

	private void importStep() {
		long startTime = System.currentTimeMillis();
		int number = 0;
		ArrayList<ArrayList<String>> rowList = ExcelUtil.readExcel(new File(this.fileName.getAttribute("uploadPath").toString()), 0);
		for (int rowIndex = 1; rowIndex < rowList.size(); rowIndex++) {
			ArrayList<String> columnList = rowList.get(rowIndex);
			String error = "";
			if (!manageService.existUserByLoginName(columnList.get(2))) {
				FwkUser user = new FwkUser();
				user.setFuTrueName(columnList.get(1));
				user.setFuLoginName(columnList.get(2));
				user.setFuPassword(columnList.get(3));
				List<FwkGroup> groupList = new ArrayList<FwkGroup>();
				groupList.add((FwkGroup) this.groupRow.getAttribute("group"));
				user.setGroups(groupList);
				user.setFuAuthority(AuthorityParser.appendRoleKeys("", ((FwkGroup) this.groupRow.getAttribute("group")).getFgId().toString(), this.roleRow.getAttribute("role").toString()));
				manageService.saveOrUpdateWithCache(user);
				number++;
			} else {
				error = "登录名[" + columnList.get(2) + "]重复";
			}
			System.out.println(error);
		}
		this.number.setValue(number + "");
		this.time.setValue(System.currentTimeMillis() - startTime + "");
	}
}
