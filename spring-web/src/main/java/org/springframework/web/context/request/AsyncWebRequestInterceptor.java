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
package org.springframework.web.context.request;

/**
 * 它使用回调方法扩展了{@code WebRequestInterceptor}接口，该回调方法在异步请求处理期间调用。
 *
 * <p>当处理器开始异步请求处理时，DispatcherServlet退出而不会像通常那样调用
 * {@code postHandle}和{@code afterCompletion}，
 * 因为请求处理的结果（例如ModelAndView）在当前线程中不可用，并且处理尚未完成。 
 * 在这种情况下，将调用{@link #afterConcurrentHandlingStarted(WebRequest)}方法替换，
 * 该方法允许实现执行诸如清除线程绑定属性之类的任务。
 *
 * <p>异步处理完成后，请求将分派到容器以进行进一步处理。 在此阶段，DispatcherServlet照常调用
 * {@code preHandle}，{@code postHandle}和{@code afterCompletion}。
 *
 * @author Rossen Stoyanchev
 * @since 3.2
 *
 * @see org.springframework.web.context.request.async.WebAsyncManager
 */
public interface AsyncWebRequestInterceptor extends WebRequestInterceptor{

	/**
	 * 当处理器开始并发处理请求时，调用该方法而不是调用{@code postHandle}和{@code afterCompletion}。
	 *
	 * @param request the current request
	 */
	void afterConcurrentHandlingStarted(WebRequest request);

}
