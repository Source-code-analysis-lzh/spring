/*
 * Copyright 2002-2016 the original author or authors.
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

import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;

/**
 * 提供Web应用程序配置的接口。 在应用程序运行时，它是只读的，但是如果实现支持，则可以重新加载。
 *
 * <p>此接口将{@code getServletContext()}方法添加到通用ApplicationContext接口，
 * 并定义了根应用程序必须在引导过程中绑定的众所周知的应用程序属性名称。
 *
 * <p>就像普通应用上下文，web应用上下文是分层的。每一个应用程序存在一个根上下文，而应用中的每一个servlet
 * 具有它自己的孩子上下文(在MVC框架中包含一个dispatcher servlet)。
 *
 * <p>除了标准的应用程序上下文生命周期功能外，
 * WebApplicationContext实现还需要检测{@link ServletContextAware} Bean
 * 并相应地调用{@code setServletContext}方法。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since January 19, 2001
 * @see ServletContextAware#setServletContext
 */
public interface WebApplicationContext extends ApplicationContext {

	/**
	 * 成功启动时将根WebApplicationContext绑定到的Context属性。
	 * <p>注意：如果根上下文的启动失败，则此属性可以包含异常或错误作为值。 
	 * 使用WebApplicationContextUtils可以方便地查找根WebApplicationContext。
	 * @see org.springframework.web.context.support.WebApplicationContextUtils#getWebApplicationContext
	 * @see org.springframework.web.context.support.WebApplicationContextUtils#getRequiredWebApplicationContext
	 */
	String ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE = WebApplicationContext.class.getName() + ".ROOT";

	/**
	 * 请求范围的范围标识符："request"。 还支持标准范围"singleton"和"prototype"。
	 */
	String SCOPE_REQUEST = "request";

	/**
	 * Scope identifier for session scope: "session".
	 * Supported in addition to the standard scopes "singleton" and "prototype".
	 */
	String SCOPE_SESSION = "session";

	/**
	 * Scope identifier for the global web application scope: "application".
	 * Supported in addition to the standard scopes "singleton" and "prototype".
	 */
	String SCOPE_APPLICATION = "application";

	/**
	 * 工厂中ServletContext环境Bean的名称。
	 * @see javax.servlet.ServletContext
	 */
	String SERVLET_CONTEXT_BEAN_NAME = "servletContext";

	/**
	 * 工厂中ServletContext init-params环境Bean的名称。
	 * <p>Note: Possibly merged with ServletConfig parameters.
	 * ServletConfig parameters override ServletContext parameters of the same name.
	 * @see javax.servlet.ServletContext#getInitParameterNames()
	 * @see javax.servlet.ServletContext#getInitParameter(String)
	 * @see javax.servlet.ServletConfig#getInitParameterNames()
	 * @see javax.servlet.ServletConfig#getInitParameter(String)
	 */
	String CONTEXT_PARAMETERS_BEAN_NAME = "contextParameters";

	/**
	 * 工厂中ServletContext属性环境bean的名称。
	 * @see javax.servlet.ServletContext#getAttributeNames()
	 * @see javax.servlet.ServletContext#getAttribute(String)
	 */
	String CONTEXT_ATTRIBUTES_BEAN_NAME = "contextAttributes";


	/**
	 * 返回此应用程序的标准Servlet API ServletContext。
	 */
	@Nullable
	ServletContext getServletContext();

}
