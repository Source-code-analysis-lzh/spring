/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.web.servlet.mvc;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

/**
 * Spring Controller实现可转发到命名的servlet，即web.xml中的"servlet-name"，
 * 而不是URL路径映射。 首先，目标servlet甚至不需要在web.xml中进行"servlet-mapping"：
 * 一个"servlet"声明就足够了。
 * 
 * <p>将拦截的请求交由某个servlet来处理。和ServletWrappingController类似，它也是一个Servlet相关的controller，
 * 他们都实现将HTTP请求适配到一个已存的Servlet实现。
 *
 * <p>通过Spring的调度基础结构调用现有servlet很有用，例如将Spring HandlerInterceptors应用于其请求。 
 * 即使在不支持Servlet过滤器的最小Servlet容器中，这也将工作。
 *
 * <p><b>Example:</b> web.xml, mapping all "/myservlet" requests to a Spring dispatcher.
 * Also defines a custom "myServlet", but <i>without</i> servlet mapping.
 *
 * <pre class="code">
 * &lt;servlet&gt;
 *   &lt;servlet-name&gt;myServlet&lt;/servlet-name&gt;
 *   &lt;servlet-class&gt;mypackage.TestServlet&lt;/servlet-class&gt;
 * &lt;/servlet&gt;
 *
 * &lt;servlet&gt;
 *   &lt;servlet-name&gt;myDispatcher&lt;/servlet-name&gt;
 *   &lt;servlet-class&gt;org.springframework.web.servlet.DispatcherServlet&lt;/servlet-class&gt;
 * &lt;/servlet&gt;
 *
 * &lt;servlet-mapping&gt;
 *   &lt;servlet-name&gt;myDispatcher&lt;/servlet-name&gt;
 *   &lt;url-pattern&gt;/myservlet&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;</pre>
 *
 * <b>Example:</b> myDispatcher-servlet.xml, in turn forwarding "/myservlet" to your
 * servlet (identified by servlet name). All such requests will go through the
 * configured HandlerInterceptor chain (e.g. an OpenSessionInViewInterceptor).
 * From the servlet point of view, everything will work as usual.
 *
 * <pre class="code">
 * &lt;bean id="urlMapping" class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping"&gt;
 *   &lt;property name="interceptors"&gt;
 *     &lt;list&gt;
 *       &lt;ref bean="openSessionInViewInterceptor"/&gt;
 *     &lt;/list&gt;
 *   &lt;/property&gt;
 *   &lt;property name="mappings"&gt;
 *     &lt;props&gt;
 *       &lt;prop key="/myservlet"&gt;myServletForwardingController&lt;/prop&gt;
 *     &lt;/props&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 *
 * &lt;bean id="myServletForwardingController" class="org.springframework.web.servlet.mvc.ServletForwardingController"&gt;
 *   &lt;property name="servletName"&gt;&lt;value&gt;myServlet&lt;/value&gt;&lt;/property&gt;
 * &lt;/bean&gt;</pre>
 *
 * @author Juergen Hoeller
 * @since 1.1.1
 * @see ServletWrappingController
 * @see org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor
 * @see org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter
 */
public class ServletForwardingController extends AbstractController implements BeanNameAware {

	@Nullable
	private String servletName;

	@Nullable
	private String beanName;


	public ServletForwardingController() {
		super(false);
	}


	/**
	 * Set the name of the servlet to forward to,
	 * i.e. the "servlet-name" of the target servlet in web.xml.
	 * <p>Default is the bean name of this controller.
	 */
	public void setServletName(String servletName) {
		this.servletName = servletName;
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
		if (this.servletName == null) {
			this.servletName = name;
		}
	}


	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		ServletContext servletContext = getServletContext();
		Assert.state(servletContext != null, "No ServletContext");
		RequestDispatcher rd = servletContext.getNamedDispatcher(this.servletName);
		if (rd == null) {
			throw new ServletException("No servlet with name '" + this.servletName + "' defined in web.xml");
		}

		// If already included, include again, else forward.
		if (useInclude(request, response)) {
			rd.include(request, response);
			if (logger.isTraceEnabled()) {
				logger.trace("Included servlet [" + this.servletName +
						"] in ServletForwardingController '" + this.beanName + "'");
			}
		}
		else {
			rd.forward(request, response);
			if (logger.isTraceEnabled()) {
				logger.trace("Forwarded to servlet [" + this.servletName +
						"] in ServletForwardingController '" + this.beanName + "'");
			}
		}

		return null;
	}

	/**
	 * 确定是使用RequestDispatcher的{@code include}还是{@code forward}方法。
	 * <p>执行检查是否在请求中找到包含URI属性（指示包含请求），以及响应是否已经提交。 
	 * 在这两种情况下，都将执行包含操作，因为不再可能进行转发。
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @return {@code true} for include, {@code false} for forward
	 * @see javax.servlet.RequestDispatcher#forward
	 * @see javax.servlet.RequestDispatcher#include
	 * @see javax.servlet.ServletResponse#isCommitted
	 * @see org.springframework.web.util.WebUtils#isIncludeRequest
	 */
	protected boolean useInclude(HttpServletRequest request, HttpServletResponse response) {
		return (WebUtils.isIncludeRequest(request) || response.isCommitted());
	}

}
