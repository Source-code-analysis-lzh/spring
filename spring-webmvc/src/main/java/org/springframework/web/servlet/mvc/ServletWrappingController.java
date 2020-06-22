/*
 * Copyright 2002-2019 the original author or authors.
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

import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.ModelAndView;

/**
 * Spring Controller实现包装了它在内部管理的servlet实例。 这种包装的servlet在此控制器之外是未知的。 
 * 它的整个生命周期都在这里进行（与{@link ServletForwardingController}相反）。
 * 
 * <p>ServletWrappingController则是将当前应用中的某个 Servlet直接包装为一个Controller，
 * 所有到ServletWrappingController的请求实际上是由它内部所包装的这个Servlet来处理的。
 *
 * <p>通过Spring的调度基础结构调用现有servlet很有用，例如将Spring HandlerInterceptors应用于其请求。
 *
 * <p>请注意，Struts有一个特殊要求，因为它解析{@code web.xml}来查找其servlet映射。 
 * 因此，您需要在此控制器上将DispatcherServlet的servlet名称指定为"servletName"，
 * 以便Struts找到DispatcherServlet的映射（认为它引用了ActionServlet）。
 *
 * <p>示例：DispatcherServlet XML上下文，将"*.do"转发到由ServletWrappingController包装的Struts ActionServlet。 
 * 所有此类请求将通过已配置的HandlerInterceptor链（例如，OpenSessionInViewInterceptor）进行。 
 * 从Struts的角度来看，一切都会照常进行。
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
 *       &lt;prop key="*.do"&gt;strutsWrappingController&lt;/prop&gt;
 *     &lt;/props&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 *
 * &lt;bean id="strutsWrappingController" class="org.springframework.web.servlet.mvc.ServletWrappingController"&gt;
 *   &lt;property name="servletClass"&gt;
 *     &lt;value&gt;org.apache.struts.action.ActionServlet&lt;/value&gt;
 *   &lt;/property&gt;
 *   &lt;property name="servletName"&gt;
 *     &lt;value&gt;action&lt;/value&gt;
 *   &lt;/property&gt;
 *   &lt;property name="initParameters"&gt;
 *     &lt;props&gt;
 *       &lt;prop key="config"&gt;/WEB-INF/struts-config.xml&lt;/prop&gt;
 *     &lt;/props&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;</pre>
 *
 * @author Juergen Hoeller
 * @since 1.1.1
 * @see ServletForwardingController
 */
public class ServletWrappingController extends AbstractController
		implements BeanNameAware, InitializingBean, DisposableBean {

	@Nullable
	private Class<? extends Servlet> servletClass;

	@Nullable
	private String servletName;

	private Properties initParameters = new Properties();

	@Nullable
	private String beanName;

	@Nullable
	private Servlet servletInstance;


	public ServletWrappingController() {
		super(false);
	}


	/**
	 * Set the class of the servlet to wrap.
	 * Needs to implement {@code javax.servlet.Servlet}.
	 * @see javax.servlet.Servlet
	 */
	public void setServletClass(Class<? extends Servlet> servletClass) {
		this.servletClass = servletClass;
	}

	/**
	 * Set the name of the servlet to wrap.
	 * Default is the bean name of this controller.
	 */
	public void setServletName(String servletName) {
		this.servletName = servletName;
	}

	/**
	 * Specify init parameters for the servlet to wrap,
	 * as name-value pairs.
	 */
	public void setInitParameters(Properties initParameters) {
		this.initParameters = initParameters;
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}


	/**
	 * Initialize the wrapped Servlet instance.
	 * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		// 必须制定它关联的是哪个Servlet
		if (this.servletClass == null) {
			throw new IllegalArgumentException("'servletClass' is required");
		}
		// 如果没有指定servlet的名字，就用beanName作为名字
		if (this.servletName == null) {
			this.servletName = this.beanName;
		}
		// 对servlet进行init方法  初始化
		this.servletInstance = ReflectionUtils.accessibleConstructor(this.servletClass).newInstance();
		this.servletInstance.init(new DelegatingServletConfig());
	}


	/**
	 * Invoke the wrapped Servlet instance.
	 * @see javax.servlet.Servlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		Assert.state(this.servletInstance != null, "No Servlet instance");
		// 最终请求是交给了这个servlet去真正处理的
		this.servletInstance.service(request, response);
		return null;
	}


	/**
	 * Destroy the wrapped Servlet instance.
	 * @see javax.servlet.Servlet#destroy()
	 */
	@Override
	public void destroy() {
		if (this.servletInstance != null) {
			this.servletInstance.destroy();
		}
	}


	/**
	 * Internal implementation of the ServletConfig interface, to be passed
	 * to the wrapped servlet. Delegates to ServletWrappingController fields
	 * and methods to provide init parameters and other environment info.
	 */
	private class DelegatingServletConfig implements ServletConfig {

		@Override
		@Nullable
		public String getServletName() {
			return servletName;
		}

		@Override
		@Nullable
		public ServletContext getServletContext() {
			return ServletWrappingController.this.getServletContext();
		}

		@Override
		public String getInitParameter(String paramName) {
			return initParameters.getProperty(paramName);
		}

		@Override
		@SuppressWarnings({"rawtypes", "unchecked"})
		public Enumeration<String> getInitParameterNames() {
			return (Enumeration) initParameters.keys();
		}
	}

}
