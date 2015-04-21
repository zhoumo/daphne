package com.hebut.framework.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hebut.framework.entity.FwkUser;

@SuppressWarnings("serial")
public class UserInfo implements Serializable {

	private FwkUser user;

	private String authority;

	private Map<String, List<String>> roleMap;

	public UserInfo(String authority) {
		super();
		this.authority = authority;
		this.roleMap = new HashMap<String, List<String>>();
		for (String authoritySever : authority.split(",")) {
			String[] groupSever = authoritySever.split("\\*");
			if (groupSever.length == 0) {
				continue;
			}
			List<String> roleList = new ArrayList<String>();
			for (String roleKey : groupSever[1].split("\\+")) {
				roleList.add(roleKey);
			}
			this.roleMap.put(groupSever[0], roleList);
		}
	}

	public FwkUser getUser() {
		return user;
	}

	public void setUser(FwkUser user) {
		this.user = user;
	}

	public String getAuthority() {
		return authority;
	}

	public Map<String, List<String>> getRoleMap() {
		return this.roleMap;
	}
}
