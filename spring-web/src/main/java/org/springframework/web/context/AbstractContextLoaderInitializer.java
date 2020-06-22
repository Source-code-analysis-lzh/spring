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

package org.springframework.web.context;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.lang.Nullable;
import org.springframework.web.WebApplicationInitializer;

/**
 * {@link WebApplicationInitializer}实现的便捷基类，
 * 该实现在Servlet上下文中注册了{@link ContextLoaderListener}。
 *
 * <p>子类唯一需要实现的方法是{@link #createRootApplicationContext()}，
 * 该方法从{@link #registerContextLoaderListener(ServletContext)}调用。
 *
 * @author Arjen Poutsma
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.2
 */
public abstract class AbstractContextLoaderInitializer implements WebApplicationInitializer {

	/** Logger available to subclasses. */
	protected final Log logger = LogFactory.getLog(getClass());


	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		registerContextLoaderListener(servletContext);
	}

	/**
	 * 针对给定的servlet上下文注册{@link ContextLoaderListener}。 
	 * 使用从{@link #createRootApplicationContext()}模板方法返回的应用程序上下文初始化{@code ContextLoaderListener}。
	 * @param servletContext the servlet context to register the listener against
	 */
	protected void registerContextLoaderListener(ServletContext servletContext) {
		// 创建根应用上下文
		WebApplicationContext rootAppContext = createRootApplicationContext();
		if (rootAppContext != null) {
			// 这里传入根应用上下文到ContextLoader中
			ContextLoaderListener listener = new ContextLoaderListener(rootAppContext);
			// 这里传入ApplicationContextInitializers到ContextLoader中
			listener.setContextInitializers(getRootApplicationContextInitializers());
			servletContext.addListener(listener);
		}
		else {
			logger.debug("No ContextLoaderListener registered, as " +
					"createRootApplicationContext() did not return an application context");
		}
	}

	/**
	 * 创建要提供给{@code ContextLoaderListener}的“根”应用程序上下文。
	 * <p>返回的上下文委派给{@link ContextLoaderListener#ContextLoaderListener(WebApplicationContext)}，
	 * 并将其建立为任何{@code DispatcherServlet}应用程序上下文的父上下文。 因此，它通常包含中间层服务，数据源等。
	 * @return the root application context, or {@code null} if a root context is not
	 * desired
	 * @see org.springframework.web.servlet.support.AbstractDispatcherServletInitializer
	 */
	@Nullable
	protected abstract WebApplicationContext createRootApplicationContext();

	/**
	 * 指定要应用于创建{@code ContextLoaderListener}的根应用程序上下文的应用程序上下文初始化器。
	 * @since 4.2
	 * @see #createRootApplicationContext()
	 * @see ContextLoaderListener#setContextInitializers
	 */
	@Nullable
	protected ApplicationContextInitializer<?>[] getRootApplicationContextInitializers() {
		return null;
	}

}
