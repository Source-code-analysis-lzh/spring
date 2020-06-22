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

package org.springframework.web.servlet.mvc;

import javax.servlet.http.HttpServletRequest;

/**
 * 支持最后修改(last-modified)的HTTP请求，以帮助内容缓存。 
 * 与Servlet API的{@code getLastModified}方法具有相同的约定。
 *
 * <p>由{@link org.springframework.web.servlet.HandlerAdapter#getLastModified}委托实现。 
 * 默认情况下，Spring的默认框架内的任何Controller或HttpRequestHandler都可以实现此接口以启用最后修改的检查。
 *
 * <p>注意：不同处理器实现方法具有不同的上次修改(last-modified)处理风格。 
 * 例如，Spring 2.5的带注释的控制器方法（使用{@code @RequestMapping}）通过
 * {@link org.springframework.web.context.request.WebRequest#checkNotModified}方法提供了最后修改的支持，
 * 从而允许在主处理器方法中进行最后修改的检查。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see javax.servlet.http.HttpServlet#getLastModified
 * @see Controller
 * @see SimpleControllerHandlerAdapter
 * @see org.springframework.web.HttpRequestHandler
 * @see HttpRequestHandlerAdapter
 */
public interface LastModified {

	/**
	 * 与HttpServlet的{@code getLastModified}方法具有相同的约定。 在请求处理之前调用。
	 * <p>返回值将作为Last-Modified标头发送到HTTP客户端，并与客户端发送回的If-Modified-Since标头进行比较。 
	 * 只有进行了修改，内容才会重新生成。
	 * @param request current HTTP request
	 * @return the time the underlying resource was last modified, or -1
	 * meaning that the content must always be regenerated
	 * @see org.springframework.web.servlet.HandlerAdapter#getLastModified
	 * @see javax.servlet.http.HttpServlet#getLastModified
	 */
	long getLastModified(HttpServletRequest request);

}
