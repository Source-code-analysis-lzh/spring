/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.servlet.resource;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.ServletContextAware;

/**
 * 一个{@link HttpRequestHandler}，用于使用Servlet容器的"default" Servlet来提供静态文件。
 *
 * <p>&lt;mvc:default-servlet-handler /&gt;来专门处理静态资源文件。
 * 它其实就是向MVC的容器内注入了一个DefaultServletHttpRequestHandler实例，
 * 它会像一个检查员，对进入DispatcherServlet的URL进行筛查，如果发现是静态资源的请求，
 * 就将该请求转由Web应用服务器默认的Servlet处理，如果不是静态资源的请求，才由DispatcherServlet继续处理。
 * 
 * <p>当{@link org.springframework.web.servlet.DispatcherServlet DispatcherServlet}映射到"/"时，
 * 该处理器旨在与"/*"映射一起使用，从而覆盖Servlet容器对静态资源的默认处理。 
 * 到此处理器的映射通常应位于链顺序的最后，以便仅在无法匹配其它更具体的映射（即到控制器）时才执行。
 *
 * <p>通过{@link RequestDispatcher}转发请求给{@link #setDefaultServletName "defaultServletName" property}指定的名称默认servlet
 * 处理请求。 在大多数情况下，不需要显式设置{@code defaultServletName}，因为处理程序会在初始化时检查知名容器
 * （例如Tomcat，Jetty，Resin，WebLogic和WebSphere）的默认Servlet是否存在。 
 * 但是，当在不知道默认Servlet名称或已通过服务器配置自定义的容器中运行时，将需要显式设置{@code defaultServletName}。
 *
 * @author Jeremy Grelle
 * @author Juergen Hoeller
 * @since 3.0.4
 */
public class DefaultServletHttpRequestHandler implements HttpRequestHandler, ServletContextAware {

	/** Default Servlet name used by Tomcat, Jetty, JBoss, and GlassFish. */
	private static final String COMMON_DEFAULT_SERVLET_NAME = "default";

	/** Default Servlet name used by Google App Engine. */
	private static final String GAE_DEFAULT_SERVLET_NAME = "_ah_default";

	/** Default Servlet name used by Resin. */
	private static final String RESIN_DEFAULT_SERVLET_NAME = "resin-file";

	/** Default Servlet name used by WebLogic. */
	private static final String WEBLOGIC_DEFAULT_SERVLET_NAME = "FileServlet";

	/** Default Servlet name used by WebSphere. */
	private static final String WEBSPHERE_DEFAULT_SERVLET_NAME = "SimpleFileServlet";


	@Nullable
	private String defaultServletName;

	@Nullable
	private ServletContext servletContext;


	/**
	 * 设置要转发给处理静态资源请求的默认Servlet的名称。
	 */
	public void setDefaultServletName(String defaultServletName) {
		this.defaultServletName = defaultServletName;
	}

	/**
	 * 如果尚未显式设置{@code defaultServletName}属性，请尝试使用已知的通用容器特定名称来查找默认Servlet。
	 */
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
		if (!StringUtils.hasText(this.defaultServletName)) {
			if (this.servletContext.getNamedDispatcher(COMMON_DEFAULT_SERVLET_NAME) != null) {
				this.defaultServletName = COMMON_DEFAULT_SERVLET_NAME;
			}
			else if (this.servletContext.getNamedDispatcher(GAE_DEFAULT_SERVLET_NAME) != null) {
				this.defaultServletName = GAE_DEFAULT_SERVLET_NAME;
			}
			else if (this.servletContext.getNamedDispatcher(RESIN_DEFAULT_SERVLET_NAME) != null) {
				this.defaultServletName = RESIN_DEFAULT_SERVLET_NAME;
			}
			else if (this.servletContext.getNamedDispatcher(WEBLOGIC_DEFAULT_SERVLET_NAME) != null) {
				this.defaultServletName = WEBLOGIC_DEFAULT_SERVLET_NAME;
			}
			else if (this.servletContext.getNamedDispatcher(WEBSPHERE_DEFAULT_SERVLET_NAME) != null) {
				this.defaultServletName = WEBSPHERE_DEFAULT_SERVLET_NAME;
			}
			else {
				throw new IllegalStateException("Unable to locate the default servlet for serving static content. " +
						"Please set the 'defaultServletName' property explicitly.");
			}
		}
	}


	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Assert.state(this.servletContext != null, "No ServletContext set");
		RequestDispatcher rd = this.servletContext.getNamedDispatcher(this.defaultServletName);
		if (rd == null) {
			throw new IllegalStateException("A RequestDispatcher could not be located for the default servlet '" +
					this.defaultServletName + "'");
		}
		rd.forward(request, response); // 转发到容器默认资源servlet上
	}

}
