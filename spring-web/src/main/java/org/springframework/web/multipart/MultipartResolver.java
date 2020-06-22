/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.web.multipart;

import javax.servlet.http.HttpServletRequest;

/**
 * 符合<a href="https://www.ietf.org/rfc/rfc1867.txt">RFC 1867</a>
 * 的用于multipart文件上传解析的策略接口。实现通常可在应用程序上下文中使用，也可独立使用。
 *
 * <p>从Spring 3.1开始，Spring包含两个具体的实现：
 * <ul>
 * <li>{@link org.springframework.web.multipart.commons.CommonsMultipartResolver}
 * for Apache Commons FileUpload
 * <li>{@link org.springframework.web.multipart.support.StandardServletMultipartResolver}
 * for the Servlet 3.0+ Part API
 * </ul>
 *
 * <p>Spring {@link org.springframework.web.servlet.DispatcherServlet DispatcherServlets}
 * 没有默认的解析器实现，因为应用程序可能会选择自行解析其multipart请求。 
 * 要定义实现，请在{@link org.springframework.web.servlet.DispatcherServlet DispatcherServlet}
 * 的应用程序上下文中创建一个ID为"multipartResolver"的bean。 
 * 这样的解析器将应用于该{@link org.springframework.web.servlet.DispatcherServlet}处理的所有请求。
 *
 * <p>如果{@link org.springframework.web.servlet.DispatcherServlet}检测到multipart请求，
 * 它将通过配置的{@link MultipartResolver}对其进行解析，并传递已包装的{@link javax.servlet.http.HttpServletRequest}。 
 * 然后，控制器可以将给定的请求转换为{@link MultipartHttpServletRequest}接口，
 * 该接口允许访问任何{@link MultipartFile MultipartFiles}。 请注意，仅在实际的multipart请求的情况下才支持此转换。
 *
 * <pre class="code">
 * public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) {
 *   MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
 *   MultipartFile multipartFile = multipartRequest.getFile("image");
 *   ...
 * }</pre>
 *
 * 代替直接访问，命令或表单控制器可以使用其数据绑定器(data binder)注册
 * {@link org.springframework.web.multipart.support.ByteArrayMultipartFileEditor}
 * 或{@link org.springframework.web.multipart.support.StringMultipartFileEditor}，
 * 以自动将multipart内容应用于表单bean属性。
 *
 * <p>作为将{@link MultipartResolver}与{@link org.springframework.web.servlet.DispatcherServlet}
 * 结合使用的替代方法，可以在{@code web.xml}中注册{@link org.springframework.web.multipart.support.MultipartFilter}。
 * 它将委派给根应用程序上下文中的相应{@link MultipartResolver} bean。 
 * 这主要用于不使用Spring自己的Web MVC框架的应用程序。
 *
 * <p>注意：几乎不需要从应用程序代码访问{@link MultipartResolver}本身。 
 * 它将简单地在后台进行工作，使{@link MultipartHttpServletRequest MultipartHttpServletRequests}可用于控制器。
 *
 * @author Juergen Hoeller
 * @author Trevor D. Cook
 * @since 29.09.2003
 * @see MultipartHttpServletRequest
 * @see MultipartFile
 * @see org.springframework.web.multipart.commons.CommonsMultipartResolver
 * @see org.springframework.web.multipart.support.ByteArrayMultipartFileEditor
 * @see org.springframework.web.multipart.support.StringMultipartFileEditor
 * @see org.springframework.web.servlet.DispatcherServlet
 */
public interface MultipartResolver {

	/**
	 * 确定给定的请求是否包含多部分内容。
	 * <p>通常将检查内容类型"multipart/form-data"，但实际接受的请求可能取决于解析器实现的功能。
	 * @param request the servlet request to be evaluated
	 * @return whether the request contains multipart content
	 */
	boolean isMultipart(HttpServletRequest request);

	/**
	 * 将给定的HTTP请求解析为multipart文件和参数，并将请求包装在
	 * {@link org.springframework.web.multipart.MultipartHttpServletRequest}对象中，
	 * 该对象提供对文件描述符的访问，并使包含的参数可通过标准ServletRequest方法访问。
	 * @param request the servlet request to wrap (must be of a multipart content type)
	 * @return the wrapped servlet request
	 * @throws MultipartException if the servlet request is not multipart, or if
	 * implementation-specific problems are encountered (such as exceeding file size limits)
	 * @see MultipartHttpServletRequest#getFile
	 * @see MultipartHttpServletRequest#getFileNames
	 * @see MultipartHttpServletRequest#getFileMap
	 * @see javax.servlet.http.HttpServletRequest#getParameter
	 * @see javax.servlet.http.HttpServletRequest#getParameterNames
	 * @see javax.servlet.http.HttpServletRequest#getParameterMap
	 */
	MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException;

	/**
	 * 清理用于multipart处理的所有资源，例如上载文件的存储。
	 * @param request the request to cleanup resources for
	 */
	void cleanupMultipart(MultipartHttpServletRequest request);

}
