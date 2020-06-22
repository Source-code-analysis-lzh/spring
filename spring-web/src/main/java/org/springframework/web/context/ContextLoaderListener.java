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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 启动监听器，用于启动和关闭Spring的根{@link WebApplicationContext}。 
 * 只需将其委派给{@link ContextLoader}以及{@link ContextCleanupListener}。
 *
 * <p>从Spring 3.1开始，{@code ContextLoaderListener}支持通过
 * {@link #ContextLoaderListener(WebApplicationContext)}构造函数注入根Web应用程序上下文，
 * 从而允许在Servlet 3.0+环境中进行编程配置。 
 * 有关用法示例，请参见{@link org.springframework.web.WebApplicationInitializer}。
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 17.02.2003
 * @see #setContextInitializers
 * @see org.springframework.web.WebApplicationInitializer
 */
public class ContextLoaderListener extends ContextLoader implements ServletContextListener {

	/**
	 * 创建一个新的{@code ContextLoaderListener}，它将基于"contextClass"和"contextConfigLocation"
	 * servlet上下文参数创建一个Web应用程序上下文。 有关每个默认值的详细信息，请参见{@link ContextLoader}超类文档。
	 * <p>当在{@code web.xml}中将{@code ContextLoaderListener}声明为{@code <listener>}时，
	 * 通常使用此构造函数，需要无参数的构造函数。
	 * <p>创建的应用程序上下文将以属性名称{@link WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE}
	 * 注册到ServletContext中，并且在此侦听器上调用{@link #contextDestroyed}生命周期方法时，Spring应用程序上下文将关闭。
	 * @see ContextLoader
	 * @see #ContextLoaderListener(WebApplicationContext)
	 * @see #contextInitialized(ServletContextEvent)
	 * @see #contextDestroyed(ServletContextEvent)
	 */
	public ContextLoaderListener() {
	}

	/**
	 * 使用给定的应用程序上下文创建一个新的{@code ContextLoaderListener}。 
	 * 此构造函数在Servlet 3.0+环境中很有用，在该环境中，可以通过{@link javax.servlet.ServletContext#addListener}
	 * API进行基于实例的侦听器注册。
	 * 上下文可能会或可能不会{@linkplain org.springframework.context.ConfigurableApplicationContext#refresh() refreshed}。 
	 * 如果它是{@link ConfigurableWebApplicationContext}的实现，并且尚未刷新（推荐的方法），则将发生以下情况：
	 * <ul>
	 * <li>如果还没有为给定上下文分配{@linkplain org.springframework.context.ConfigurableApplicationContext#setId id}，
	 * 则将为其分配一个。</li>
	 * <li>{@code ServletContext}和{@code ServletConfig}对象将被委托给应用程序上下文</li>
	 * <li>{@link #customizeContext}将会被调用</li>
	 * <li>任何通过init-param参数"contextInitializerClasses"指定的
	 * {@link org.springframework.context.ApplicationContextInitializer ApplicationContextInitializer org.springframework.context.ApplicationContextInitializer ApplicationContextInitializers}
	 * 将会被应用。</li>
	 * <li>{@link org.springframework.context.ConfigurableApplicationContext#refresh refresh()}将会被调用</li>
	 * </ul>
	 * 如果上下文已经刷新或未实现{@code ConfigurableWebApplicationContext}，
	 * 则在用户根据其特定需求执行了（或未执行）这些操作的假设下，上述所有操作均不会发生。
	 * <p>参见{@link org.springframework.web.WebApplicationInitializer}获取使用示例。
	 * <p>无论如何，给定的应用程序上下文都将在属性名称{@link WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE}
	 * 下注册到ServletContext中，并且在此侦听器上调用{@link #contextDestroyed}生命周期方法时，Spring应用程序上下文将关闭。
	 * @param context the application context to manage
	 * @see #contextInitialized(ServletContextEvent)
	 * @see #contextDestroyed(ServletContextEvent)
	 */
	public ContextLoaderListener(WebApplicationContext context) {
		super(context);
	}


	/**
	 * 初始化根Web应用程序上下文。
	 * 这里的contextInitialized 方法会在web容器启动时被调用，从而启动整个spring容器的创建
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		initWebApplicationContext(event.getServletContext());
	}


	/**
	 * 关闭根Web应用程序上下文。
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		closeWebApplicationContext(event.getServletContext());
		ContextCleanupListener.cleanupAttributes(event.getServletContext());
	}

}
