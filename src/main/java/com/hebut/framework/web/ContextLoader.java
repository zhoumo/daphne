package com.hebut.framework.web;

import javax.servlet.ServletContextEvent;
import org.apache.log4j.Logger;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hebut.framework.entity.FwkUser;
import com.hebut.framework.util.CacheUtil;
import com.hebut.rbac.core.RoleFactory;
import com.hebut.rbac.core.XmlParser;

public class ContextLoader extends ContextLoaderListener {

	private static Logger logger = Logger.getLogger(ContextLoader.class);

	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		if (logger.isDebugEnabled()) {
			logger.debug("容器启动完成");
		}
		BeanFactory.setWebApplicationContext(WebApplicationContextUtils.getWebApplicationContext(event.getServletContext()));
		try {
			RoleFactory.createFactory(XmlParser.getRbacConfig());
		} catch (CloneNotSupportedException e) {
			logger.error("配置文件解析失败", e);
		}
		CacheUtil.initCache(FwkUser.class);
	}
}
