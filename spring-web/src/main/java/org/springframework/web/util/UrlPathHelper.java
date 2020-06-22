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

package org.springframework.web.util;

import java.net.URLDecoder;
import java.nio.charset.UnsupportedCharsetException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

/**
 * URL路径匹配的帮助工具类。 提供对{@code RequestDispatcher}包含的URL路径的支持，并支持一致的URL解码。
 *
 * <p>由{@link org.springframework.web.servlet.handler.AbstractUrlHandlerMapping}
 * 和{@link org.springframework.web.servlet.support.RequestContext}用于路径匹配和/或URI确定。
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Rossen Stoyanchev
 * @since 14.01.2004
 * @see #getLookupPathForRequest
 * @see javax.servlet.RequestDispatcher
 */
public class UrlPathHelper {

	/**
	 * Special WebSphere request attribute, indicating the original request URI.
	 * Preferable over the standard Servlet 2.4 forward attribute on WebSphere,
	 * simply because we need the very first URI in the request forwarding chain.
	 * 特殊的WebSphere请求属性，指示原始请求URI。 比WebSphere上的标准Servlet 2.4转发属性更好，
	 * 这是因为我们需要请求转发链中的第一个URI。
	 */
	private static final String WEBSPHERE_URI_ATTRIBUTE = "com.ibm.websphere.servlet.uri_non_decoded";

	private static final Log logger = LogFactory.getLog(UrlPathHelper.class);

	@Nullable
	static volatile Boolean websphereComplianceFlag;


	private boolean alwaysUseFullPath = false;

	private boolean urlDecode = true;

	private boolean removeSemicolonContent = true;

	private String defaultEncoding = WebUtils.DEFAULT_CHARACTER_ENCODING;


	/**
	 * Whether URL lookups should always use the full path within the current
	 * web application context, i.e. within
	 * {@link javax.servlet.ServletContext#getContextPath()}.
	 * URL查找是否应始终使用当前Web应用程序上下文（如{@link javax.servlet.ServletContext#getContextPath()}）
	 * 中的完整路径。
	 * <p>If set to {@literal false} the path within the current servlet mapping
	 * is used instead if applicable (i.e. in the case of a prefix based Servlet
	 * mapping such as "/myServlet/*").
	 * <p>如果设置为{@literal false}，则在适用的情况下（例如，在基于前缀的Servlet映射如"/myServlet/*"的情况下）
	 * 使用当前Servlet映射中的路径。
	 * <p>By default this is set to "false".
	 */
	public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
		this.alwaysUseFullPath = alwaysUseFullPath;
	}

	/**
	 * 相比于Servlet路径，是否应该解码上下文路径和请求URI-两者均由Servlet API未经解码返回。
	 * <p>设置为"true"时，将使用请求编码或默认的Servlet规范编码（ISO-8859-1）。
	 * <p>By default this is set to {@literal true}.
	 * <p><strong>Note:</strong> Be aware the servlet path will not match when
	 * compared to encoded paths. Therefore use of {@code urlDecode=false} is
	 * not compatible with a prefix-based Servlet mapping and likewise implies
	 * also setting {@code alwaysUseFullPath=true}.
	 * <p>注意：请注意，与编码路径相比，servlet路径将不匹配。 因此，使用{@code urlDecode=false}
	 * 与基于前缀的Servlet映射不兼容，并且同样意味着还必须设置{@code alwaysUseFullPath=true}。
	 * @see #getServletPath
	 * @see #getContextPath
	 * @see #getRequestUri
	 * @see WebUtils#DEFAULT_CHARACTER_ENCODING
	 * @see javax.servlet.ServletRequest#getCharacterEncoding()
	 * @see java.net.URLDecoder#decode(String, String)
	 */
	public void setUrlDecode(boolean urlDecode) {
		this.urlDecode = urlDecode;
	}

	/**
	 * Whether to decode the request URI when determining the lookup path.
	 * @since 4.3.13
	 */
	public boolean isUrlDecode() {
		return this.urlDecode;
	}

	/**
	 * Set if ";" (semicolon) content should be stripped from the request URI.
	 * <p>Default is "true".
	 */
	public void setRemoveSemicolonContent(boolean removeSemicolonContent) {
		this.removeSemicolonContent = removeSemicolonContent;
	}

	/**
	 * Whether configured to remove ";" (semicolon) content from the request URI.
	 */
	public boolean shouldRemoveSemicolonContent() {
		return this.removeSemicolonContent;
	}

	/**
	 * Set the default character encoding to use for URL decoding.
	 * Default is ISO-8859-1, according to the Servlet spec.
	 * <p>If the request specifies a character encoding itself, the request
	 * encoding will override this setting. This also allows for generically
	 * overriding the character encoding in a filter that invokes the
	 * {@code ServletRequest.setCharacterEncoding} method.
	 * @param defaultEncoding the character encoding to use
	 * @see #determineEncoding
	 * @see javax.servlet.ServletRequest#getCharacterEncoding()
	 * @see javax.servlet.ServletRequest#setCharacterEncoding(String)
	 * @see WebUtils#DEFAULT_CHARACTER_ENCODING
	 */
	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	/**
	 * Return the default character encoding to use for URL decoding.
	 */
	protected String getDefaultEncoding() {
		return this.defaultEncoding;
	}


	/**
	 * Return the mapping lookup path for the given request, within the current
	 * servlet mapping if applicable, else within the web application.
	 * 如果合适在当前Servlet映射中返回给定请求的映射查找路径，否则在Web应用程序中返回。
	 * <p>Detects include request URL if called within a RequestDispatcher include.
	 * <p>如果在RequestDispatcher包含中调用，则检测包含请求URL。
	 * @param request current HTTP request
	 * @return the lookup path
	 * @see #getPathWithinServletMapping
	 * @see #getPathWithinApplication
	 */
	public String getLookupPathForRequest(HttpServletRequest request) {
		// Always use full path within current servlet context?
		if (this.alwaysUseFullPath) {
			return getPathWithinApplication(request);
		}
		// Else, use path within current servlet mapping if applicable
		String rest = getPathWithinServletMapping(request);
		if (!"".equals(rest)) {
			return rest;
		}
		else {
			return getPathWithinApplication(request);
		}
	}

	/**
	 * Variant of {@link #getLookupPathForRequest(HttpServletRequest)} that
	 * automates checking for a previously computed lookupPath saved as a
	 * request attribute. The attribute is only used for lookup purposes.
	 * {@link #getLookupPathForRequest(HttpServletRequest)}的变体，
	 * 用于自动检查保存为请求属性的先前计算的lookupPath。 该属性仅用于查找目的。
	 * @param request current HTTP request
	 * @param lookupPathAttributeName the request attribute to check
	 * @return the lookup path
	 * @since 5.2
	 * @see org.springframework.web.servlet.HandlerMapping#LOOKUP_PATH
	 */
	public String getLookupPathForRequest(HttpServletRequest request, @Nullable String lookupPathAttributeName) {
		if (lookupPathAttributeName != null) {
			String result = (String) request.getAttribute(lookupPathAttributeName);
			if (result != null) {
				return result;
			}
		}
		return getLookupPathForRequest(request);
	}

	/**
	 * 返回给定请求的servlet映射内的路径，即，请求URL的超出servlet的部分；
	 * 如果整个URL已用于标识servlet，则返回“”。
	 * <p>Detects include request URL if called within a RequestDispatcher include.
	 * <p>E.g.: servlet mapping = "/*"; request URI = "/test/a" -> "/test/a".
	 * <p>E.g.: servlet mapping = "/"; request URI = "/test/a" -> "/test/a".
	 * <p>E.g.: servlet mapping = "/test/*"; request URI = "/test/a" -> "/a".
	 * <p>E.g.: servlet mapping = "/test"; request URI = "/test" -> "".
	 * <p>E.g.: servlet mapping = "/*.test"; request URI = "/a.test" -> "".
	 * @param request current HTTP request
	 * @return the path within the servlet mapping, or ""
	 * @see #getLookupPathForRequest
	 */
	public String getPathWithinServletMapping(HttpServletRequest request) {
		String pathWithinApp = getPathWithinApplication(request);
		String servletPath = getServletPath(request);
		String sanitizedPathWithinApp = getSanitizedPath(pathWithinApp);
		String path;

		// If the app container sanitized the servletPath, check against the sanitized version
		if (servletPath.contains(sanitizedPathWithinApp)) {
			path = getRemainingPath(sanitizedPathWithinApp, servletPath, false);
		}
		else {
			path = getRemainingPath(pathWithinApp, servletPath, false);
		}

		if (path != null) {
			// Normal case: URI contains servlet path.
			return path;
		}
		else {
			// Special case: URI is different from servlet path.
			String pathInfo = request.getPathInfo();
			if (pathInfo != null) {
				// Use path info if available. Indicates index page within a servlet mapping?
				// e.g. with index page: URI="/", servletPath="/index.html"
				return pathInfo;
			}
			if (!this.urlDecode) {
				// No path info... (not mapped by prefix, nor by extension, nor "/*")
				// For the default servlet mapping (i.e. "/"), urlDecode=false can
				// cause issues since getServletPath() returns a decoded path.
				// If decoding pathWithinApp yields a match just use pathWithinApp.
				path = getRemainingPath(decodeInternal(request, pathWithinApp), servletPath, false);
				if (path != null) {
					return pathWithinApp;
				}
			}
			// Otherwise, use the full servlet path.
			return servletPath;
		}
	}

	/**
	 * 返回Web应用程序中给定请求的路径。
	 * <p>如果在RequestDispatcher包含(include)中调用，则检测包含请求URL。
	 * @param request current HTTP request
	 * @return the path within the web application
	 * @see #getLookupPathForRequest
	 */
	public String getPathWithinApplication(HttpServletRequest request) {
		String contextPath = getContextPath(request); // 获取请求上下文路径
		String requestUri = getRequestUri(request); // 获取请求部分路径
		String path = getRemainingPath(requestUri, contextPath, true);
		if (path != null) {
			// Normal case: URI contains context path.
			return (StringUtils.hasText(path) ? path : "/");
		}
		else {
			return requestUri;
		}
	}

	/**
	 * Match the given "mapping" to the start of the "requestUri" and if there
	 * is a match return the extra part. This method is needed because the
	 * context path and the servlet path returned by the HttpServletRequest are
	 * stripped of semicolon content unlike the requestUri.
	 */
	@Nullable
	private String getRemainingPath(String requestUri, String mapping, boolean ignoreCase) {
		int index1 = 0;
		int index2 = 0;
		for (; (index1 < requestUri.length()) && (index2 < mapping.length()); index1++, index2++) {
			char c1 = requestUri.charAt(index1);
			char c2 = mapping.charAt(index2);
			if (c1 == ';') {
				index1 = requestUri.indexOf('/', index1);
				if (index1 == -1) {
					return null;
				}
				c1 = requestUri.charAt(index1);
			}
			if (c1 == c2 || (ignoreCase && (Character.toLowerCase(c1) == Character.toLowerCase(c2)))) {
				continue;
			}
			return null;
		}
		if (index2 != mapping.length()) {
			return null;
		}
		else if (index1 == requestUri.length()) {
			return "";
		}
		else if (requestUri.charAt(index1) == ';') {
			index1 = requestUri.indexOf('/', index1);
		}
		return (index1 != -1 ? requestUri.substring(index1) : "");
	}

	/**
	 * 使用以下规则清理给定的路径：
	 * <ul>
	 * <li>replace all "//" by "/"</li>
	 * </ul>
	 */
	private String getSanitizedPath(final String path) {
		String sanitized = path;
		while (true) {
			int index = sanitized.indexOf("//");
			if (index < 0) {
				break;
			}
			else {
				sanitized = sanitized.substring(0, index) + sanitized.substring(index + 1);
			}
		}
		return sanitized;
	}

	/**
	 * 返回给定请求的请求URI，如果在RequestDispatcher包含(include)中调用了该请求，则检测包含请求URL。
	 * <p>由于servlet容器未解码{@code request.getRequestURI()}返回的值，因此此方法将对其进行解码。
	 * <p>The URI that the web container resolves <i>should</i> be correct, but some
	 * containers like JBoss/Jetty incorrectly include ";" strings like ";jsessionid"
	 * in the URI. This method cuts off such incorrect appendices.
	 * @param request current HTTP request
	 * @return the request URI
	 */
	public String getRequestUri(HttpServletRequest request) {
		// 判断include请求
		String uri = (String) request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE);
		if (uri == null) {
			uri = request.getRequestURI(); // 获取请求部分路径
		}
		return decodeAndCleanUriString(request, uri);
	}

	/**
	 * Return the context path for the given request, detecting an include request
	 * URL if called within a RequestDispatcher include.
	 * 返回给定请求的上下文路径，如果在RequestDispatcher包含(include)中调用了包含请求URL，则检测到该请求。
	 * <p>As the value returned by {@code request.getContextPath()} is <i>not</i>
	 * decoded by the servlet container, this method will decode it.
	 * @param request current HTTP request
	 * @return the context path
	 */
	public String getContextPath(HttpServletRequest request) {
		String contextPath = (String) request.getAttribute(WebUtils.INCLUDE_CONTEXT_PATH_ATTRIBUTE);
		if (contextPath == null) {
			contextPath = request.getContextPath();
		}
		if ("/".equals(contextPath)) {
			// Invalid case, but happens for includes on Jetty: silently adapt it.
			contextPath = "";
		}
		return decodeRequestString(request, contextPath);
	}

	/**
	 * Return the servlet path for the given request, regarding an include request
	 * URL if called within a RequestDispatcher include.
	 * <p>As the value returned by {@code request.getServletPath()} is already
	 * decoded by the servlet container, this method will not attempt to decode it.
	 * @param request current HTTP request
	 * @return the servlet path
	 */
	public String getServletPath(HttpServletRequest request) {
		String servletPath = (String) request.getAttribute(WebUtils.INCLUDE_SERVLET_PATH_ATTRIBUTE);
		if (servletPath == null) {
			servletPath = request.getServletPath();
		}
		if (servletPath.length() > 1 && servletPath.endsWith("/") && shouldRemoveTrailingServletPathSlash(request)) {
			// On WebSphere, in non-compliant mode, for a "/foo/" case that would be "/foo"
			// on all other servlet containers: removing trailing slash, proceeding with
			// that remaining slash as final lookup path...
			servletPath = servletPath.substring(0, servletPath.length() - 1);
		}
		return servletPath;
	}


	/**
	 * Return the request URI for the given request. If this is a forwarded request,
	 * correctly resolves to the request URI of the original request.
	 */
	public String getOriginatingRequestUri(HttpServletRequest request) {
		String uri = (String) request.getAttribute(WEBSPHERE_URI_ATTRIBUTE);
		if (uri == null) {
			uri = (String) request.getAttribute(WebUtils.FORWARD_REQUEST_URI_ATTRIBUTE);
			if (uri == null) {
				uri = request.getRequestURI();
			}
		}
		return decodeAndCleanUriString(request, uri);
	}

	/**
	 * Return the context path for the given request, detecting an include request
	 * URL if called within a RequestDispatcher include.
	 * <p>As the value returned by {@code request.getContextPath()} is <i>not</i>
	 * decoded by the servlet container, this method will decode it.
	 * @param request current HTTP request
	 * @return the context path
	 */
	public String getOriginatingContextPath(HttpServletRequest request) {
		String contextPath = (String) request.getAttribute(WebUtils.FORWARD_CONTEXT_PATH_ATTRIBUTE);
		if (contextPath == null) {
			contextPath = request.getContextPath();
		}
		return decodeRequestString(request, contextPath);
	}

	/**
	 * Return the servlet path for the given request, detecting an include request
	 * URL if called within a RequestDispatcher include.
	 * @param request current HTTP request
	 * @return the servlet path
	 */
	public String getOriginatingServletPath(HttpServletRequest request) {
		String servletPath = (String) request.getAttribute(WebUtils.FORWARD_SERVLET_PATH_ATTRIBUTE);
		if (servletPath == null) {
			servletPath = request.getServletPath();
		}
		return servletPath;
	}

	/**
	 * Return the query string part of the given request's URL. If this is a forwarded request,
	 * correctly resolves to the query string of the original request.
	 * @param request current HTTP request
	 * @return the query string
	 */
	public String getOriginatingQueryString(HttpServletRequest request) {
		if ((request.getAttribute(WebUtils.FORWARD_REQUEST_URI_ATTRIBUTE) != null) ||
			(request.getAttribute(WebUtils.ERROR_REQUEST_URI_ATTRIBUTE) != null)) {
			return (String) request.getAttribute(WebUtils.FORWARD_QUERY_STRING_ATTRIBUTE);
		}
		else {
			return request.getQueryString();
		}
	}

	/**
	 * Decode the supplied URI string and strips any extraneous portion after a ';'.
	 */
	private String decodeAndCleanUriString(HttpServletRequest request, String uri) {
		uri = removeSemicolonContent(uri);
		uri = decodeRequestString(request, uri);
		uri = getSanitizedPath(uri);
		return uri;
	}

	/**
	 * 使用URLDecoder解码给定的源字符串。编码将从请求中获取，如果没有，默认使用"ISO-8859-1"
	 * <p>默认实现使用{@code URLDecoder.decode(input，enc)}。
	 * @param request current HTTP request
	 * @param source the String to decode
	 * @return the decoded String
	 * @see WebUtils#DEFAULT_CHARACTER_ENCODING
	 * @see javax.servlet.ServletRequest#getCharacterEncoding
	 * @see java.net.URLDecoder#decode(String, String)
	 * @see java.net.URLDecoder#decode(String)
	 */
	public String decodeRequestString(HttpServletRequest request, String source) {
		if (this.urlDecode) {
			return decodeInternal(request, source);
		}
		return source;
	}

	@SuppressWarnings("deprecation")
	private String decodeInternal(HttpServletRequest request, String source) {
		String enc = determineEncoding(request);
		try {
			return UriUtils.decode(source, enc);
		}
		catch (UnsupportedCharsetException ex) {
			if (logger.isWarnEnabled()) {
				logger.warn("Could not decode request string [" + source + "] with encoding '" + enc +
						"': falling back to platform default encoding; exception message: " + ex.getMessage());
			}
			return URLDecoder.decode(source);
		}
	}

	/**
	 * 确定给定请求的编码。可以在子类中重写。
	 * <p>默认实现检查请求编码，并回退为此解析器指定的默认编码。
	 * @param request current HTTP request
	 * @return the encoding for the request (never {@code null})
	 * @see javax.servlet.ServletRequest#getCharacterEncoding()
	 * @see #setDefaultEncoding
	 */
	protected String determineEncoding(HttpServletRequest request) {
		String enc = request.getCharacterEncoding();
		if (enc == null) {
			enc = getDefaultEncoding();
		}
		return enc;
	}

	/**
	 * Remove ";" (semicolon) content from the given request URI if the
	 * {@linkplain #setRemoveSemicolonContent removeSemicolonContent}
	 * property is set to "true". Note that "jsessionid" is always removed.
	 * @param requestUri the request URI string to remove ";" content from
	 * @return the updated URI string
	 */
	public String removeSemicolonContent(String requestUri) {
		return (this.removeSemicolonContent ?
				removeSemicolonContentInternal(requestUri) : removeJsessionid(requestUri));
	}

	private String removeSemicolonContentInternal(String requestUri) {
		int semicolonIndex = requestUri.indexOf(';');
		while (semicolonIndex != -1) {
			int slashIndex = requestUri.indexOf('/', semicolonIndex);
			String start = requestUri.substring(0, semicolonIndex);
			requestUri = (slashIndex != -1) ? start + requestUri.substring(slashIndex) : start;
			semicolonIndex = requestUri.indexOf(';', semicolonIndex);
		}
		return requestUri;
	}

	private String removeJsessionid(String requestUri) {
		int startIndex = requestUri.toLowerCase().indexOf(";jsessionid=");
		if (startIndex != -1) {
			int endIndex = requestUri.indexOf(';', startIndex + 12);
			String start = requestUri.substring(0, startIndex);
			requestUri = (endIndex != -1) ? start + requestUri.substring(endIndex) : start;
		}
		return requestUri;
	}

	/**
	 * Decode the given URI path variables via {@link #decodeRequestString} unless
	 * {@link #setUrlDecode} is set to {@code true} in which case it is assumed
	 * the URL path from which the variables were extracted is already decoded
	 * through a call to {@link #getLookupPathForRequest(HttpServletRequest)}.
	 * @param request current HTTP request
	 * @param vars the URI variables extracted from the URL path
	 * @return the same Map or a new Map instance
	 */
	public Map<String, String> decodePathVariables(HttpServletRequest request, Map<String, String> vars) {
		if (this.urlDecode) {
			return vars;
		}
		else {
			Map<String, String> decodedVars = new LinkedHashMap<>(vars.size());
			vars.forEach((key, value) -> decodedVars.put(key, decodeInternal(request, value)));
			return decodedVars;
		}
	}

	/**
	 * Decode the given matrix variables via {@link #decodeRequestString} unless
	 * {@link #setUrlDecode} is set to {@code true} in which case it is assumed
	 * the URL path from which the variables were extracted is already decoded
	 * through a call to {@link #getLookupPathForRequest(HttpServletRequest)}.
	 * @param request current HTTP request
	 * @param vars the URI variables extracted from the URL path
	 * @return the same Map or a new Map instance
	 */
	public MultiValueMap<String, String> decodeMatrixVariables(
			HttpServletRequest request, MultiValueMap<String, String> vars) {

		if (this.urlDecode) {
			return vars;
		}
		else {
			MultiValueMap<String, String> decodedVars = new LinkedMultiValueMap<>(vars.size());
			vars.forEach((key, values) -> {
				for (String value : values) {
					decodedVars.add(key, decodeInternal(request, value));
				}
			});
			return decodedVars;
		}
	}

	private boolean shouldRemoveTrailingServletPathSlash(HttpServletRequest request) {
		if (request.getAttribute(WEBSPHERE_URI_ATTRIBUTE) == null) {
			// Regular servlet container: behaves as expected in any case,
			// so the trailing slash is the result of a "/" url-pattern mapping.
			// Don't remove that slash.
			return false;
		}
		Boolean flagToUse = websphereComplianceFlag;
		if (flagToUse == null) {
			ClassLoader classLoader = UrlPathHelper.class.getClassLoader();
			String className = "com.ibm.ws.webcontainer.WebContainer";
			String methodName = "getWebContainerProperties";
			String propName = "com.ibm.ws.webcontainer.removetrailingservletpathslash";
			boolean flag = false;
			try {
				Class<?> cl = classLoader.loadClass(className);
				Properties prop = (Properties) cl.getMethod(methodName).invoke(null);
				flag = Boolean.parseBoolean(prop.getProperty(propName));
			}
			catch (Throwable ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Could not introspect WebSphere web container properties: " + ex);
				}
			}
			flagToUse = flag;
			websphereComplianceFlag = flag;
		}
		// Don't bother if WebSphere is configured to be fully Servlet compliant.
		// However, if it is not compliant, do remove the improper trailing slash!
		return !flagToUse;
	}

}
