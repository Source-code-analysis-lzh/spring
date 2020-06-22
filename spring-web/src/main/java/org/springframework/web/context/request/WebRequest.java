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

package org.springframework.web.context.request;

import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.springframework.lang.Nullable;

/**
 * Web请求的通用接口。 主要用于通用Web请求拦截器，使它们可以访问通用请求元数据，而不是用于实际处理请求。
 *
 * @author Juergen Hoeller
 * @author Brian Clozel
 * @since 2.0
 * @see WebRequestInterceptor
 */
public interface WebRequest extends RequestAttributes {

	/**
	 * 返回给定名称的请求标头，如果没有，则返回{@code null}。
	 * <p>如果是多值标头，则检索第一个标头值。
	 * @since 3.0
	 * @see javax.servlet.http.HttpServletRequest#getHeader(String)
	 */
	@Nullable
	String getHeader(String headerName);

	/**
	 * 返回给定标头名称的请求标头值，如果没有，则返回{@code null}。
	 * <p>单值标头将作为具有单个元素的数组公开。
	 * @since 3.0
	 * @see javax.servlet.http.HttpServletRequest#getHeaders(String)
	 */
	@Nullable
	String[] getHeaderValues(String headerName);

	/**
	 * 通过请求标头名称返回迭代器。
	 * @since 3.0
	 * @see javax.servlet.http.HttpServletRequest#getHeaderNames()
	 */
	Iterator<String> getHeaderNames();

	/**
	 * 返回给定名称的请求参数；如果没有，则返回{@code null}。
	 * <p>如果是多值参数，则检索第一个参数值。
	 * @see javax.servlet.http.HttpServletRequest#getParameter(String)
	 */
	@Nullable
	String getParameter(String paramName);

	/**
	 * Return the request parameter values for the given parameter name,
	 * or {@code null} if none.
	 * <p>A single-value parameter will be exposed as an array with a single element.
	 * @see javax.servlet.http.HttpServletRequest#getParameterValues(String)
	 */
	@Nullable
	String[] getParameterValues(String paramName);

	/**
	 * Return a Iterator over request parameter names.
	 * @since 3.0
	 * @see javax.servlet.http.HttpServletRequest#getParameterNames()
	 */
	Iterator<String> getParameterNames();

	/**
	 * 返回请求参数的不变Map，其中参数名称作为映射键，参数值作为映射值。 映射值将为String数组类型。
	 * <p>A single-value parameter will be exposed as an array with a single element.
	 * @see javax.servlet.http.HttpServletRequest#getParameterMap()
	 */
	Map<String, String[]> getParameterMap();

	/**
	 * 返回此请求的主要语言环境。
	 * @see javax.servlet.http.HttpServletRequest#getLocale()
	 */
	Locale getLocale();

	/**
	 * Return the context path for this request
	 * (usually the base path that the current web application is mapped to).
	 * @see javax.servlet.http.HttpServletRequest#getContextPath()
	 */
	String getContextPath();

	/**
	 * Return the remote user for this request, if any.
	 * @see javax.servlet.http.HttpServletRequest#getRemoteUser()
	 */
	@Nullable
	String getRemoteUser();

	/**
	 * Return the user principal for this request, if any.
	 * @see javax.servlet.http.HttpServletRequest#getUserPrincipal()
	 */
	@Nullable
	Principal getUserPrincipal();

	/**
	 * Determine whether the user is in the given role for this request.
	 * @see javax.servlet.http.HttpServletRequest#isUserInRole(String)
	 */
	boolean isUserInRole(String role);

	/**
	 * Return whether this request has been sent over a secure transport
	 * mechanism (such as SSL).
	 * @see javax.servlet.http.HttpServletRequest#isSecure()
	 */
	boolean isSecure();

	/**
	 * 给定所提供的上次修改的时间戳（由应用程序确定），检查请求的资源是否已被修改。
	 * <p>如果适用，这还将透明地设置"Last-Modified"响应标头和HTTP状态。
	 * <p>Typical usage:
	 * <pre class="code">
	 * public String myHandleMethod(WebRequest webRequest, Model model) {
	 *   long lastModified = // application-specific calculation
	 *   if (request.checkNotModified(lastModified)) {
	 *     // shortcut exit - no further processing necessary
	 *     return null;
	 *   }
	 *   // further request processing, actually building content
	 *   model.addAttribute(...);
	 *   return "myViewName";
	 * }</pre>
	 * <p>此方法适用于有条件的GET/HEAD请求，也适用于有条件的POST/PUT/DELETE请求。
	 * <p>注意：您可以使用此{@link #checkNotModified(String)}方法； 或
	 * {@link #checkNotModified(String)}。 如果要同时执行HTTP规范建议的强实体标签(tag)和
	 * Last-Modified值，则应使用{@link #checkNotModified(String, long)}。
	 * <p>如果设置了"If-Modified-Since"标头，但无法将其解析为日期值，则此方法将忽略标头，
	 * 并继续在响应上设置上次修改的时间戳。
	 * @param lastModifiedTimestamp the last-modified timestamp in
	 * milliseconds that the application determined for the underlying
	 * resource
	 * @return whether the request qualifies as not modified,
	 * allowing to abort request processing and relying on the response
	 * telling the client that the content has not been modified
	 */
	boolean checkNotModified(long lastModifiedTimestamp);

	/**
	 * 给定由应用程序确定的提供的{@code ETag}（实体标签），检查请求的资源是否已被修改。
	 * <p>如果适用，这还将透明地设置"ETag"响应标头和HTTP状态。
	 * <p>Typical usage:
	 * <pre class="code">
	 * public String myHandleMethod(WebRequest webRequest, Model model) {
	 *   String eTag = // application-specific calculation
	 *   if (request.checkNotModified(eTag)) {
	 *     // shortcut exit - no further processing necessary
	 *     return null;
	 *   }
	 *   // further request processing, actually building content
	 *   model.addAttribute(...);
	 *   return "myViewName";
	 * }</pre>
	 * <p><strong>Note:</strong> you can use either
	 * this {@code #checkNotModified(String)} method; or
	 * {@link #checkNotModified(long)}. If you want enforce both
	 * a strong entity tag and a Last-Modified value,
	 * as recommended by the HTTP specification,
	 * then you should use {@link #checkNotModified(String, long)}.
	 * @param etag the entity tag that the application determined
	 * for the underlying resource. This parameter will be padded
	 * with quotes (") if necessary.
	 * @return true if the request does not require further processing.
	 */
	boolean checkNotModified(String etag);

	/**
	 * 给定由应用程序确定并提供的{@code ETag}（实体标签）和上次修改的时间戳，检查请求的资源是否已被修改。
	 * <p>如果适用，这还将透明地设置"ETag"和"Last-Modified"响应标头以及HTTP状态。
	 * <p>Typical usage:
	 * <pre class="code">
	 * public String myHandleMethod(WebRequest webRequest, Model model) {
	 *   String eTag = // application-specific calculation
	 *   long lastModified = // application-specific calculation
	 *   if (request.checkNotModified(eTag, lastModified)) {
	 *     // shortcut exit - no further processing necessary
	 *     return null;
	 *   }
	 *   // further request processing, actually building content
	 *   model.addAttribute(...);
	 *   return "myViewName";
	 * }</pre>
	 * <p>This method works with conditional GET/HEAD requests, but
	 * also with conditional POST/PUT/DELETE requests.
	 * <p><strong>Note:</strong> The HTTP specification recommends
	 * setting both ETag and Last-Modified values, but you can also
	 * use {@code #checkNotModified(String)} or
	 * {@link #checkNotModified(long)}.
	 * @param etag the entity tag that the application determined
	 * for the underlying resource. This parameter will be padded
	 * with quotes (") if necessary.
	 * @param lastModifiedTimestamp the last-modified timestamp in
	 * milliseconds that the application determined for the underlying
	 * resource
	 * @return true if the request does not require further processing.
	 * @since 4.2
	 */
	boolean checkNotModified(@Nullable String etag, long lastModifiedTimestamp);

	/**
	 * 获取此请求的简短描述，通常包含请求URI和会话ID。
	 * @param includeClientInfo whether to include client-specific
	 * information such as session id and user name
	 * @return the requested description as String
	 */
	String getDescription(boolean includeClientInfo);

}
