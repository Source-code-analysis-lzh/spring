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

package org.springframework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;

/**
 * MVC框架SPI，允许对核心MVC工作流程进行参数化。
 *
 * <p>为每种处理器类型处理请求必须实现的接口。 
 * 此接口用于允许{@link DispatcherServlet}无限地扩展。 
 * {@link DispatcherServlet}通过此接口访问所有已安装的处理器，
 * 这意味着{@link DispatcherServlet}不包含特定于任何处理器类型的代码。
 *
 * <p>请注意，处理器可以是{@code Object}类型。 这是为了使其它框架中的处理器可以与该框架集成，
 * 而无需自定义编码，并且允许注释驱动的处理器对象不遵循任何特定的Java接口。
 *
 * <p>此接口不适用于应用程序开发人员。 想要开发自己的Web工作流程的处理器可以使用它。
 *
 * <p>注意：{@code HandlerAdapter}实现者可以实现{@link org.springframework.core.Ordered}接口，
 * 以能够指定要由{@code DispatcherServlet}调用的顺序（从而确定优先级）。 非排序实例被视为最低优先级。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter
 * @see org.springframework.web.servlet.handler.SimpleServletHandlerAdapter
 */
public interface HandlerAdapter {

	/**
	 * 给定一个处理器实例，返回此{@code HandlerAdapter}是否可以支持它。 
	 * 典型的HandlerAdapters将根据处理器类型做出决定。 HandlerAdapters通常仅支持一种处理器类型。
	 * <p>A typical implementation:
	 * <p>{@code
	 * return (handler instanceof MyHandler);
	 * }
	 * @param handler the handler object to check
	 * @return whether or not this object can use the given handler
	 */
	boolean supports(Object handler);

	/**
	 * 使用给定的处理器来处理此请求。 所需的工作流程可能相差很大。
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the handler to use. This object must have previously been passed
	 * to the {@code supports} method of this interface, which must have
	 * returned {@code true}.
	 * @throws Exception in case of errors
	 * @return a ModelAndView object with the name of the view and the required
	 * model data, or {@code null} if the request has been handled directly
	 */
	@Nullable
	ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;

	/**
	 * 与HttpServlet的{@code getLastModified}方法具有相同的约定。 
	 * 如果处理器类不支持，则可以简单地返回-1。
	 * @param request current HTTP request
	 * @param handler the handler to use
	 * @return the lastModified value for the given handler
	 * @see javax.servlet.http.HttpServlet#getLastModified
	 * @see org.springframework.web.servlet.mvc.LastModified#getLastModified
	 */
	long getLastModified(HttpServletRequest request, Object handler);

}
