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
 * 实现该接口对象可以解决在处理器映射或执行期间抛出的异常，在典型情况下为错误视图。 
 * 实现者通常在应用程序上下文中注册为bean。
 *
 * <p>错误视图类似于JSP错误页面，但是可以与任何类型的异常（包括任何检查的异常）一起使用，
 * 并且可能具有特定处理器的细粒度映射。
 *
 * @author Juergen Hoeller
 * @since 22.11.2003
 */
public interface HandlerExceptionResolver {

	/**
	 * 尝试解决在处理器执行期间引发的给定异常，如果合适的话，返回代表特定错误页面的{@link ModelAndView}。
	 * <p>返回的{@code ModelAndView}可以为{@linkplain ModelAndView#isEmpty() empty}，
	 * 以指示异常已成功解决，但不应渲染任何视图（例如，通过设置状态代码）。
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler, or {@code null} if none chosen at the
	 * time of the exception (for example, if multipart resolution failed)
	 * @param ex the exception that got thrown during handler execution
	 * @return 要转发到的对应{@code ModelAndView}，或者为{@code null}（为解析链中的默认处理）
	 */
	@Nullable
	ModelAndView resolveException(
			HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex);

}
