/*
 * Copyright 2002-2017 the original author or authors.
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

import org.springframework.web.method.HandlerMethod;

/**
 * 使用在异步请求处理开始后调用的回调方法来扩展{@code HandlerInterceptor}。
 *
 * <p>当处理器启动一个异步请求时，{@link DispatcherServlet}退出而没有调用{@code postHandle}
 * 和{@code afterCompletion}，就像通常对同步请求所做的那样，因为请求处理的结果（例如ModelAndView）
 * 可能尚未准备好，并且将由另一个线程同时产生。 在这种情况下，将改为调用{@link #afterConcurrentHandlingStarted}，
 * 以使实现能够执行一些任务，例如在释放Servlet容器的线程前清除线程绑定的属性。
 *
 * <p>当异步处理完成后，请求将分派到容器以进行进一步处理。 在此阶段，{@code DispatcherServlet}
 * 调用{@code preHandle}，{@code postHandle}和{@code afterCompletion}。 
 * 为了在异步处理完成之后区分初始请求和后续分派，拦截器可以检查{@link javax.servlet.ServletRequest}
 * 的{@code javax.servlet.DispatcherType}是{@code "REQUEST"}还是{@code "ASYNC"}。
 *
 * <p>请注意，当异步请求超时或因网络错误而完成时，{@code HandlerInterceptor}实现可能需要完成工作。 
 * 在这种情况下，Servlet容器不会分派，因此将不会调用{@code postHandle}和{@code afterCompletion}方法。
 * 相反，拦截器可以通过{@link org.springframework.web.context.request.async.WebAsyncManager
 * WebAsyncManager}上的{@code registerCallbackInterceptor}和{@code registerDeferredResultInterceptor}
 * 方法进行注册，以跟踪异步请求。 无论是否开始异步请求处理，都可以对{@code preHandle}的每个请求主动处理。
 *
 * @author Rossen Stoyanchev
 * @since 3.2
 * @see org.springframework.web.context.request.async.WebAsyncManager
 * @see org.springframework.web.context.request.async.CallableProcessingInterceptor
 * @see org.springframework.web.context.request.async.DeferredResultProcessingInterceptor
 */
public interface AsyncHandlerInterceptor extends HandlerInterceptor {

	/**
	 * 在开始并发执行处理器时，调用该方法而不是{@code postHandle}和{@code afterCompletion}。
	 * <p>实现可以使用提供的请求和响应，但应避免以与处理器的并发执行冲突的方式修改它们。 
	 * 此方法的典型用法是清除线程局部变量。
	 * @param request the current request
	 * @param response the current response
	 * @param handler the handler (or {@link HandlerMethod}) that started async
	 * execution, for type and/or instance examination
	 * @throws Exception in case of errors
	 */
	default void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response,
			Object handler) throws Exception {
	}

}
