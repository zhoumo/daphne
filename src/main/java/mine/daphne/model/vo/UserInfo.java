package mine.daphne.model.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mine.daphne.model.entity.User;

@SuppressWarnings("serial")
public class UserInfo implements Serializable {

	private User user;

	private String authority;

	private Map<String, List<String>> roleMap;

	public UserInfo(String authority) {
		this.authority = authority;
		this.roleMap = new HashMap<String, List<String>>();
		for (String authoritySever : authority.split(",")) {
			String[] groupSever = authoritySever.split("\\*");
			if (groupSever.length < 2) {
				continue;
			}
			List<String> roleList = new ArrayList<String>();
			for (String roleKey : groupSever[1].split("\\+")) {
				roleList.add(roleKey);
			}
			this.roleMap.put(groupSever[0], roleList);
		}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getAuthority() {
		return authority;
	}

	public Map<String, List<String>> getRoleMap() {
		return this.roleMap;
	}
}
