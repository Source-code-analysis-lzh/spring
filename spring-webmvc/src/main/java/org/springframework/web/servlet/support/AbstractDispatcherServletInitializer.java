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

package org.springframework.web.servlet.support;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.Conventions;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.AbstractContextLoaderInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.FrameworkServlet;

/**
 * {@link org.springframework.web.WebApplicationInitializer}实现的基类，
 * 该实现在Servlet上下文中注册{@link DispatcherServlet}。
 *
 * <p>大多数应用程序应考虑扩展Spring Java配置子类
 * {@link AbstractAnnotationConfigDispatcherServletInitializer}。
 *
 * @author Arjen Poutsma
 * @author Chris Beams
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @author Stephane Nicoll
 * @since 3.2
 */
public abstract class AbstractDispatcherServletInitializer extends AbstractContextLoaderInitializer {

	/**
	 * 默认的servlet名称。 可以通过{@link #getServletName}覆盖进行自定义。
	 */
	public static final String DEFAULT_SERVLET_NAME = "dispatcher";


	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		super.onStartup(servletContext); // 这里初始化根应用上下文
		registerDispatcherServlet(servletContext); // 这里初始化web应用上下文
	}

	/**
	 * 针对给定的servlet上下文注册一个{@link DispatcherServlet}。
	 * <p>此方法将创建一个由{@link #getServletName()}返回的名称的{@code DispatcherServlet}，
	 * 并使用从{@link #createServletApplicationContext()}返回的应用程序上下文对其进行初始化，
	 * 并将其映射到从{@link #getServletMappings()}返回的模式。
	 * <p>可以通过重写{@link #customizeRegistration(ServletRegistration.Dynamic)}
	 * 或{@link #createDispatcherServlet(WebApplicationContext)}来实现进一步的自定义。
	 * @param servletContext the context to register the servlet against
	 */
	protected void registerDispatcherServlet(ServletContext servletContext) {
		String servletName = getServletName();
		Assert.hasLength(servletName, "getServletName() must not return null or empty");

		// 创建web应用上下文
		WebApplicationContext servletAppContext = createServletApplicationContext();
		Assert.notNull(servletAppContext, "createServletApplicationContext() must not return null");

		FrameworkServlet dispatcherServlet = createDispatcherServlet(servletAppContext);
		Assert.notNull(dispatcherServlet, "createDispatcherServlet(WebApplicationContext) must not return null");
		// 配置ApplicationContextInitializer用来初始化web上下文
		dispatcherServlet.setContextInitializers(getServletApplicationContextInitializers());
		
		ServletRegistration.Dynamic registration = servletContext.addServlet(servletName, dispatcherServlet);
		if (registration == null) {
			throw new IllegalStateException("Failed to register servlet with name '" + servletName + "'. " +
					"Check if there is another servlet registered under the same name.");
		}

		registration.setLoadOnStartup(1);
		registration.addMapping(getServletMappings());
		registration.setAsyncSupported(isAsyncSupported());

		Filter[] filters = getServletFilters();
		if (!ObjectUtils.isEmpty(filters)) {
			for (Filter filter : filters) {
				registerServletFilter(servletContext, filter);
			}
		}

		// 可以进一步配置dispatcherServlet这一servlet。
		customizeRegistration(registration);
	}

	/**
	 * 返回用于注册{@link DispatcherServlet}的名称。 默认为{@link #DEFAULT_SERVLET_NAME}。
	 * @see #registerDispatcherServlet(ServletContext)
	 */
	protected String getServletName() {
		return DEFAULT_SERVLET_NAME;
	}

	/**
	 * 创建要提供给{@code DispatcherServlet}的Servlet应用程序上下文。
	 * <p>返回的上下文委托给Spring的{@link DispatcherServlet#DispatcherServlet(WebApplicationContext)}。 
	 * 因此，它通常包含控制器，视图解析器，语言环境解析器和其它与Web相关的bean。
	 * @see #registerDispatcherServlet(ServletContext)
	 */
	protected abstract WebApplicationContext createServletApplicationContext();

	/**
	 * 使用指定的{@link WebApplicationContext}创建一个{@link DispatcherServlet}（或其它类型的{@link FrameworkServlet}派生的调度程序）。
	 * <p>注意：这允许从4.2.3开始的任何{@link FrameworkServlet}子类。 以前，它坚持要返回{@link DispatcherServlet}或其子类。
	 */
	protected FrameworkServlet createDispatcherServlet(WebApplicationContext servletAppContext) {
		return new DispatcherServlet(servletAppContext);
	}

	/**
	 * 指定要用于创建{@code DispatcherServlet}的特定于servlet的应用程序上下文的应用程序上下文初始化器。
	 * @since 4.2
	 * @see #createServletApplicationContext()
	 * @see DispatcherServlet#setContextInitializers
	 * @see #getRootApplicationContextInitializers()
	 */
	@Nullable
	protected ApplicationContextInitializer<?>[] getServletApplicationContextInitializers() {
		return null;
	}

	/**
	 * 指定{@code DispatcherServlet}的servlet映射-例如{@code "/"}, {@code "/app"}等。
	 * @see #registerDispatcherServlet(ServletContext)
	 */
	protected abstract String[] getServletMappings();

	/**
	 * 指定要添加并映射到{@code DispatcherServlet}的过滤器。
	 * @return an array of filters or {@code null}
	 * @see #registerServletFilter(ServletContext, Filter)
	 */
	@Nullable
	protected Filter[] getServletFilters() {
		return null;
	}

	/**
	 * 将给定的过滤器添加到ServletContext并将其映射到{@code DispatcherServlet}，如下所示：
	 * <ul>
	 * <li>a default filter name is chosen based on its concrete type
	 * <li>the {@code asyncSupported} flag is set depending on the
	 * return value of {@link #isAsyncSupported() asyncSupported}
	 * <li>a filter mapping is created with dispatcher types {@code REQUEST},
	 * {@code FORWARD}, {@code INCLUDE}, and conditionally {@code ASYNC} depending
	 * on the return value of {@link #isAsyncSupported() asyncSupported}
	 * </ul>
	 * <p>如果以上默认值不合适或不足，请重写此方法并直接向{@code ServletContext}注册过滤器。
	 * @param servletContext the servlet context to register filters with
	 * @param filter the filter to be registered
	 * @return the filter registration
	 */
	protected FilterRegistration.Dynamic registerServletFilter(ServletContext servletContext, Filter filter) {
		String filterName = Conventions.getVariableName(filter);
		Dynamic registration = servletContext.addFilter(filterName, filter);

		if (registration == null) {
			int counter = 0;
			while (registration == null) {
				if (counter == 100) {
					throw new IllegalStateException("Failed to register filter with name '" + filterName + "'. " +
							"Check if there is another filter registered under the same name.");
				}
				registration = servletContext.addFilter(filterName + "#" + counter, filter);
				counter++;
			}
		}

		registration.setAsyncSupported(isAsyncSupported());
		registration.addMappingForServletNames(getDispatcherTypes(), false, getServletName());
		return registration;
	}

	private EnumSet<DispatcherType> getDispatcherTypes() {
		return (isAsyncSupported() ?
				EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ASYNC) :
				EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE));
	}

	/**
	 * 一个可以控制{@code DispatcherServlet}和{@link #getServletFilters()}添加的所有过滤器的asyncSupported标志地方。
	 * <p>The default value is "true".
	 */
	protected boolean isAsyncSupported() {
		return true;
	}

	/**
	 * （可选）执行一次更多的注册定制
	 * {@link #registerDispatcherServlet(ServletContext)} has completed.
	 * @param registration the {@code DispatcherServlet} registration to be customized
	 * @see #registerDispatcherServlet(ServletContext)
	 */
	protected void customizeRegistration(ServletRegistration.Dynamic registration) {
	}

}
