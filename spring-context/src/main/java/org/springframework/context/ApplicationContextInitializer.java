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

package org.springframework.context;

/**
 * spring上下文初始化的回调函数在上下文
 * {@linkplain ConfigurableApplicationContext#refresh() refreshed}之前调用。
 * 
 * <p>ApplicationContextInitializer是Spring框架原有的概念, 这个类的主要目的就是在
 * ConfigurableApplicationContext类型（或者子类型）的ApplicationContext进行刷新refresh之前，
 * 允许我们对ConfigurableApplicationContext的实例做进一步的设置或者处理。
 * 
 * <p>它在org.springframework.web.context.ContextLoader以及
 * org.springframework.web.servlet.FrameworkServlet中的应用。
 *
 * <p>通常被用在需要编程初始化应用上下文的web应用。比如，使用{@linkplain ConfigurableApplicationContext#getEnvironment()
 * context's environment}注册属性源或者激活profiles。请参见{@code ContextLoader}和{@code FrameworkServlet}支持，
 * 以分别声明"contextInitializerClasses"上下文参数(context-param)和初始化(init-param)参数。
 *
 * <p>鼓励检测{@code ApplicationContextInitializer}处理器是否已实现Spring的
 * {@link org.springframework.core.Ordered Ordered}接口，
 * 或者是否存在@{@link org.springframework.core.annotation.Order Order}注释，并在调用之前对实例进行相应排序。
 *
 * 该接口在org.springframework.web.context.ContextLoader#customizeContext(javax.servlet.ServletContext, org.springframework.web.context.ConfigurableWebApplicationContext)
 * 中在应用启动时被调用。
 * 
 * @author Chris Beams
 * @since 3.1
 * @param <C> the application context type
 * @see org.springframework.web.context.ContextLoader#customizeContext
 * @see org.springframework.web.context.ContextLoader#CONTEXT_INITIALIZER_CLASSES_PARAM
 * @see org.springframework.web.servlet.FrameworkServlet#setContextInitializerClasses
 * @see org.springframework.web.servlet.FrameworkServlet#applyInitializers
 */
public interface ApplicationContextInitializer<C extends ConfigurableApplicationContext> {

	/**
	 * 初始化给定的应用程序上下文。
	 * @param applicationContext the application to configure
	 */
	void initialize(C applicationContext);

}
