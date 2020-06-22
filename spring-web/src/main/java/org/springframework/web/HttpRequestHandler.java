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

package org.springframework.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用于处理HTTP请求的组件的普通处理程序接口，类似于Servlet。 
 * 仅声明{@link javax.servlet.ServletException}和{@link java.io.IOException}，
 * 以允许在任何{@link javax.servlet.http.HttpServlet}中使用。 
 * 该接口实质上是HttpServlet的直接等效项，简化为中央处理方法。
 * 
 * <p>HttpRequestHandler也是一个Handler，它能够处理请求。由HttpRequestHandlerAdapter
 * 进行调用HttpRequestHandler的handleRequest(request, response)方法。
 *
 * <p>以Spring样式公开HttpRequestHandler bean的最简单方法是在Spring的根Web应用程序上下文中对其进行定义，
 * 并在{@code web.xml}中定义一个{@link org.springframework.web.context.support.HttpRequestHandlerServlet}，
 * 并通过其{@code servlet-name}名称指向目标HttpRequestHandler bean，
 * 该servlet名称需要与目标bean名称匹配。
 *
 * <p>作为Spring的{@link org.springframework.web.servlet.DispatcherServlet}中的处理器类型受支持，
 * 能够与调度程序的高级映射和拦截功能进行交互。 这是公开HttpRequestHandler的推荐方法，
 * 同时使处理程序实现不受DispatcherServlet环境的直接依赖。
 *
 * <p>通常实现为直接生成二进制响应，而不涉及单独的视图资源。 这使其与Spring的Web MVC框架中的
 * {@link org.springframework.web.servlet.mvc.Controller}有所区别。 
 * 缺少{@link org.springframework.web.servlet.ModelAndView}返回值可为除DispatcherServlet以外的调用者提供更清晰的签名，
 * 指示永远不会渲染视图。
 *
 * <p>从Spring 2.0开始，Spring的基于HTTP的远程导出器（例如
 * {@link org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter}
 * 和{@link org.springframework.remoting.caucho.HessianServiceExporter}）
 * 实现了此接口，而不是更广泛的Controller接口，从而最大限度地减少了对特定于Spring的Web基础结构的依赖。
 *
 * <p>注意，HttpRequestHandlers可以选择实现{@link org.springframework.web.servlet.mvc.LastModified}接口，
 * 就像Controller可以实现的一样，只要它们在Spring的DispatcherServlet中运行即可。 
 * 但是，这通常不是必需的，因为HttpRequestHandlers通常仅支持以POST请求开头。 
 * 或者，处理程序可以在其{@code handle}方法中手动实现"If-Modified-Since" HTTP标头处理。
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see org.springframework.web.context.support.HttpRequestHandlerServlet
 * @see org.springframework.web.servlet.DispatcherServlet
 * @see org.springframework.web.servlet.ModelAndView
 * @see org.springframework.web.servlet.mvc.Controller
 * @see org.springframework.web.servlet.mvc.LastModified
 * @see org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter
 * @see org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter
 * @see org.springframework.remoting.caucho.HessianServiceExporter
 */
@FunctionalInterface
public interface HttpRequestHandler {

	/**
	 * 处理给定的请求，生成响应。
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @throws ServletException in case of general errors
	 * @throws IOException in case of I/O errors
	 */
	void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException;

}
