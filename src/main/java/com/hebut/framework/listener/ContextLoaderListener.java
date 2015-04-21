package com.hebut.framework.listener;

import javax.servlet.ServletContextEvent;
import org.apache.log4j.Logger;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hebut.framework.entity.FwkUser;
import com.hebut.framework.factory.BeanFactory;
import com.hebut.framework.util.CacheUtil;
import com.hebut.rbac.core.RoleFactory;
import com.hebut.rbac.core.XmlParser;

public class ContextLoaderListener extends org.springframework.web.context.ContextLoaderListener {

	private static Logger logger = Logger.getLogger(ContextLoaderListener.class);

	public void contextInitialized(ServletContextEvent event) {
		if (logger.isDebugEnabled()) {
			logger.debug("容器正在启动");
		}
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
