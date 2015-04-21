package com.hebut.framework.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hebut.framework.security.UrlCheck;
import com.hebut.framework.ui.common.BaseWindow;

public class ZulFilter implements Filter {

	private static String basePath;

	public void init(FilterConfig config) throws ServletException {
		basePath = config.getServletContext().getContextPath();
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		if (UrlCheck.check(httpRequest.getServletPath(), httpRequest.getSession())) {
			chain.doFilter(request, response);
		} else {
			httpResponse.sendRedirect(basePath + BaseWindow.SYSTEM_LOGIN);
		}
	}

	public void destroy() {
	}
}
