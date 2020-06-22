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

package org.springframework.web.context.request;

import org.springframework.lang.Nullable;
import org.springframework.ui.ModelMap;

/**
 * 通用Web请求拦截的接口。 允许通过构建{@link WebRequest}抽象来应用于Servlet请求。
 *
 * <p>该接口假定采用MVC样式的请求处理：执行处理器，公开一组模型对象，然后根据该模型渲染视图。 
 * 或者，处理器也可以完全处理请求，而无需渲染视图。
 *
 * <p>在异步处理方案中，处理器可以在主线程退出时在单独的线程中执行，而无需渲染或调用{@code postHandle}
 * 和{@code afterCompletion}回调。 当并发处理器执行完成时，将回发该请求，以继续渲染模型，
 * 并再次调用该协定的所有方法。 有关更多选项和评论，请参见
 * {@code org.springframework.web.context.request.async.AsyncWebRequestInterceptor}
 *
 * <p>此接口故意极简，以使通用请求拦截器的依存关系尽可能地小。
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see ServletWebRequest
 * @see org.springframework.web.servlet.DispatcherServlet
 * @see org.springframework.web.servlet.handler.AbstractHandlerMapping#setInterceptors
 * @see org.springframework.web.servlet.HandlerInterceptor
 */
public interface WebRequestInterceptor {

	/**
	 * 在调用请求处理器之前对其进行拦截。
	 * <p>允许准备上下文资源（例如Hibernate Session）并将它们公开为请求属性或线程本地对象。
	 * 注意区别于HandlerInterceptor接口类似方法，本方法没有返回值，不能中断请求。
	 * @param request the current web request
	 * @throws Exception in case of errors
	 */
	void preHandle(WebRequest request) throws Exception;

	/**
	 * 成功调用请求处理器之后，即在视图渲染之前（如果有），拦截执行该请求处理器。
	 * 允许在成功执行处理器后修改上下文资源（例如，刷新Hibernate会话）。
	 * @param request the current web request
	 * @param model the map of model objects that will be exposed to the view
	 * (may be {@code null}). Can be used to analyze the exposed model
	 * and/or to add further model attributes, if desired.
	 * @throws Exception in case of errors
	 */
	void postHandle(WebRequest request, @Nullable ModelMap model) throws Exception;

	/**
	 * 完成请求处理后（即渲染视图之后）的回调。 无论处理器执行的结果如何，该方法都将被调用，
	 * 从而允许适当的资源清理。
	 * 注意：仅当此拦截器的{@code preHandle}方法成功完成时才会调用！
	 * @param request the current web request
	 * @param ex exception thrown on handler execution, if any
	 * @throws Exception in case of errors
	 */
	void afterCompletion(WebRequest request, @Nullable Exception ex) throws Exception;

}
