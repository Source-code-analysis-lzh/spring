/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.web.servlet.handler;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;

/**
 * 适配器可将Servlet接口与通用DispatcherServlet一起使用。 
 * 调用Servlet的{@code service}方法来处理请求。
 *
 * <p>不明确支持最后修改(Last-modified)的检查：通常由Servlet实现本身处理（通常从HttpServlet基类派生）。
 *
 * <p>默认情况下，此适配器未激活。 它需要在DispatcherServlet上下文中定义为bean。 
 * 随后它将自动应用于实现Servlet接口的映射处理器bean。
 *
 * <p>注意，除非在DispatcherServlet上下文中定义了特殊的后处理器（例如SimpleServletPostProcessor），
 * 否则定义为bean的Servlet实例将不会接收初始化和销毁回调。
 *
 * <p>或者，考虑使用Spring的ServletWrappingController包装Servlet。 
 * 这特别适用于现有的Servlet类，允许指定Servlet初始化参数等。
 *
 * @author Juergen Hoeller
 * @since 1.1.5
 * @see javax.servlet.Servlet
 * @see javax.servlet.http.HttpServlet
 * @see SimpleServletPostProcessor
 * @see org.springframework.web.servlet.mvc.ServletWrappingController
 */
public class SimpleServletHandlerAdapter implements HandlerAdapter {

	@Override
	public boolean supports(Object handler) {
		return (handler instanceof Servlet);
	}

	@Override
	@Nullable
	public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		((Servlet) handler).service(request, response);
		return null;
	}

	@Override
	public long getLastModified(HttpServletRequest request, Object handler) {
		return -1;
	}

}
