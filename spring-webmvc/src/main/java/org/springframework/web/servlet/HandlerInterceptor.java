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

package org.springframework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;

/**
 * 工作流接口，允许自定义处理器执行链。 应用程序可以为某些处理器组注册任意数量的现有或自定义拦截器，
 * 以添加常见的预处理行为，而无需修改每个处理器实现。
 *
 * <p>在适当的HandlerAdapter触发处理器本身的执行之前，将调用HandlerInterceptor。 
 * 该机制可以用于预处理方面的大领域，例如。 用于授权检查或常见的处理程序行为，例如语言环境或主题更改。 
 * 其主要目的是允许排除重复的处理器代码。
 *
 * <p>在异步处理方案中，处理器可以在主线程退出时在单独的线程中执行，
 * 而无需渲染或调用{@code postHandle}和{@code afterCompletion}回调。 
 * 当并发处理器执行完成时，将回发该请求，以继续渲染模型，并再次调用该协定的所有方法。 
 * 有关更多选项和详细信息，请参见{@code org.springframework.web.servlet.AsyncHandlerInterceptor}
 *
 * <p>通常，每个HandlerMapping bean定义一个拦截器链，共享其粒度。 
 * 为了能够将某个拦截器链应用于一组处理器，需要通过一个HandlerMapping bean映射所需的处理器。 
 * 拦截器本身在应用程序上下文中定义为bean，由映射bean定义通过其"interceptors"属性
 * （在XML：&lt;list&gt; 的 &lt;ref&gt; 中）引用。
 *
 * <p>HandlerInterceptor基本上类似于Servlet过滤器，但与后者相比，
 * 它仅允许自定义预处理以及禁止执行处理器本身和自定义后处理的选项。 
 * 过滤器功能更强大，例如，它们允许交换传递到链中的请求和响应对象。 
 * 请注意，在web.xml中配置了过滤器而在应用程序上下文中的HandlerInterceptor。
 *
 * <p>作为基本准则，与处理器相关的细粒度预处理任务考虑使用HandlerInterceptor对象，
 * 尤其是分解出的公共处理器代码和授权检查。 另一方面，过滤器非常适合请求内容和视图内容处理，
 * 例如多部分(multipart)表单和GZIP压缩。 这通常显示何时需要将过滤器映射到某些内容类型（例如图片）或所有请求。
 *
 * @author Juergen Hoeller
 * @since 20.06.2003
 * @see HandlerExecutionChain#getInterceptors
 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter
 * @see org.springframework.web.servlet.handler.AbstractHandlerMapping#setInterceptors
 * @see org.springframework.web.servlet.handler.UserRoleAuthorizationInterceptor
 * @see org.springframework.web.servlet.i18n.LocaleChangeInterceptor
 * @see org.springframework.web.servlet.theme.ThemeChangeInterceptor
 * @see javax.servlet.Filter
 */
public interface HandlerInterceptor {

	/**
	 * 拦截处理程序的执行。 在HandlerMapping确定适当的处理器对象之后但在HandlerAdapter调用处理程序之前调用。
	 * <p>DispatcherServlet处理执行链中的处理器，该处理器由任意数量的拦截器组成，
	 * 处理器本身位于链末尾。 使用此方法，每个拦截器都可以决定终止执行链，
	 * 通常是发送HTTP错误或编写自定义响应。
	 * <p>注意：特殊注意事项适用于异步请求处理。 有关更多详细信息，请参见
	 * {@link org.springframework.web.servlet.AsyncHandlerInterceptor}。
	 * <p>The default implementation returns {@code true}.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler chosen handler to execute, for type and/or instance evaluation
	 * @return {@code true} if the execution chain should proceed with the
	 * next interceptor or the handler itself. Else, DispatcherServlet assumes
	 * that this interceptor has already dealt with the response itself.
	 * @throws Exception in case of errors
	 */
	default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		return true;
	}

	/**
	 * 拦截处理器的执行。 在HandlerAdapter实际调用处理程序之后但在DispatcherServlet渲染视图之前调用。
	 * 可以通过给定的ModelAndView将额外的模型对象暴露给视图。
	 * <p>DispatcherServlet处理执行链中的处理器，该处理器由任意数量的拦截器组成，
	 * 处理器本身位于链末尾。 使用此方法，每个拦截器都可以对执行进行后处理，
	 * 并以与执行链相反的顺序进行应用。
	 * <p>注意：特殊注意事项适用于异步请求处理。 有关更多详细信息，
	 * 请参见{@link org.springframework.web.servlet.AsyncHandlerInterceptor}。
	 * <p>The default implementation is empty.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the handler (or {@link HandlerMethod}) that started asynchronous
	 * execution, for type and/or instance examination
	 * @param modelAndView the {@code ModelAndView} that the handler returned
	 * (can also be {@code null})
	 * @throws Exception in case of errors
	 */
	default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable ModelAndView modelAndView) throws Exception {
	}

	/**
	 * 完成请求处理后（即渲染视图之后）的回调。 将在处理器执行的任何结果上被调用，从而允许适当的资源清理。
	 * <p>注意：仅当此拦截器的{@code preHandle}方法成功完成并返回{@code true}时，才会调用它！
	 * <p>与{@code postHandle}方法一样，该方法将以相反的顺序在链中的每个拦截器上调用，
	 * 因此第一个拦截器将是最后一个被调用。
	 * <p><strong>Note:</strong> special considerations apply for asynchronous
	 * request processing. For more details see
	 * {@link org.springframework.web.servlet.AsyncHandlerInterceptor}.
	 * <p>The default implementation is empty.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the handler (or {@link HandlerMethod}) that started asynchronous
	 * execution, for type and/or instance examination
	 * @param ex any exception thrown on handler execution, if any; this does not
	 * include exceptions that have been handled through an exception resolver
	 * @throws Exception in case of errors
	 */
	default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable Exception ex) throws Exception {
	}

}
