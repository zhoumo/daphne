package mine.daphne.web;

import javax.servlet.ServletContextEvent;

import mine.daphne.security.core.RoleFactory;
import mine.daphne.security.core.XmlParser;

import org.apache.log4j.Logger;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ContextLoader extends ContextLoaderListener {

	private static Logger logger = Logger.getLogger(ContextLoader.class);

	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		BeanFactory.setWebApplicationContext(WebApplicationContextUtils.getWebApplicationContext(event.getServletContext()));
		try {
			RoleFactory.createFactory(XmlParser.getRbacConfig());
		} catch (CloneNotSupportedException e) {
			logger.error("配置文件解析失败", e);
		}
	}
}
