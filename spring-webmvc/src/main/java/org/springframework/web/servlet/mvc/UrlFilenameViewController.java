/*
 * Copyright 2002-2020 the original author or authors.
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerMapping;

/**
 * 简单的{@code Controller}实现，将URL的虚拟路径转换为视图名称并返回该视图。
 *
 * <p>使用该控制器与ParameterizableViewController控制器相比可以省去实视图名的配置，直接通过url解析。
 * 例如访问的是login.do，那么视图名就是login。经常把它配置为默认的Handler。
 * 
 * <p>可以选择添加{@link #setPrefix prefix}和/或添加{@link #setSuffix suffix}以从URL文件名构建视图名称。
 *
 * <p>在下面找到一些示例：
 * <ol>
 * <li>{@code "/index" -> "index"}</li>
 * <li>{@code "/index.html" -> "index"}</li>
 * <li>{@code "/index.html"} + prefix {@code "pre_"} and suffix {@code "_suf" -> "pre_index_suf"}</li>
 * <li>{@code "/products/view.html" -> "products/view"}</li>
 * </ol>
 *
 * 	   @Bean(&quot;/*&quot;) //&quot;/*&quot;会把它注册为一个默认的handler
 *     public UrlFilenameViewController urlFilenameViewController() {
 *         UrlFilenameViewController controller = new UrlFilenameViewController();
 *         controller.setPrefix(&quot;/api/v1/&quot;);
 *         controller.setSuffix(&quot;.do&quot;);
 *         return controller;
 *     }
 * 
 * 因为这里把它设定为了默认的处理器，所以任何404的请求都会到它这里来，交给它处理。例如我访问：
 * /democontroller22，因为我配置了前缀后缀，所以最终会到视图/api/v1/democontroller22.do里去。
 * 
 * <p>Thanks to David Barri for suggesting prefix/suffix support!
 *
 * @author Alef Arendsen
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @see #setPrefix
 * @see #setSuffix
 */
public class UrlFilenameViewController extends AbstractUrlViewController {

	private String prefix = "";

	private String suffix = "";

	/** Request URL path String to view name String. */
	private final Map<String, String> viewNameCache = new ConcurrentHashMap<>(256);


	/**
	 * Set the prefix to prepend to the request URL filename
	 * to build a view name.
	 */
	public void setPrefix(@Nullable String prefix) {
		this.prefix = (prefix != null ? prefix : "");
	}

	/**
	 * Return the prefix to prepend to the request URL filename.
	 */
	protected String getPrefix() {
		return this.prefix;
	}

	/**
	 * Set the suffix to append to the request URL filename
	 * to build a view name.
	 */
	public void setSuffix(@Nullable String suffix) {
		this.suffix = (suffix != null ? suffix : "");
	}

	/**
	 * Return the suffix to append to the request URL filename.
	 */
	protected String getSuffix() {
		return this.suffix;
	}


	/**
	 * Returns view name based on the URL filename,
	 * with prefix/suffix applied when appropriate.
	 * @see #extractViewNameFromUrlPath
	 * @see #setPrefix
	 * @see #setSuffix
	 */
	@Override
	protected String getViewNameForRequest(HttpServletRequest request) {
		String uri = extractOperableUrl(request);
		return getViewNameForUrlPath(uri);
	}

	/**
	 * Extract a URL path from the given request,
	 * suitable for view name extraction.
	 * @param request current HTTP request
	 * @return the URL to use for view name extraction
	 */
	protected String extractOperableUrl(HttpServletRequest request) {
		String urlPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		if (!StringUtils.hasText(urlPath)) {
			urlPath = getUrlPathHelper().getLookupPathForRequest(request, HandlerMapping.LOOKUP_PATH);
		}
		return urlPath;
	}

	/**
	 * Returns view name based on the URL filename,
	 * with prefix/suffix applied when appropriate.
	 * @param uri the request URI; for example {@code "/index.html"}
	 * @return the extracted URI filename; for example {@code "index"}
	 * @see #extractViewNameFromUrlPath
	 * @see #postProcessViewName
	 */
	protected String getViewNameForUrlPath(String uri) {
		return this.viewNameCache.computeIfAbsent(uri, u -> postProcessViewName(extractViewNameFromUrlPath(u)));
	}

	/**
	 * Extract the URL filename from the given request URI.
	 * @param uri the request URI; for example {@code "/index.html"}
	 * @return the extracted URI filename; for example {@code "index"}
	 */
	protected String extractViewNameFromUrlPath(String uri) {
		int start = (uri.charAt(0) == '/' ? 1 : 0);
		int lastIndex = uri.lastIndexOf('.');
		int end = (lastIndex < 0 ? uri.length() : lastIndex);
		return uri.substring(start, end);
	}

	/**
	 * Build the full view name based on the given view name
	 * as indicated by the URL path.
	 * <p>The default implementation simply applies prefix and suffix.
	 * This can be overridden, for example, to manipulate upper case
	 * / lower case, etc.
	 * @param viewName the original view name, as indicated by the URL path
	 * @return the full view name to use
	 * @see #getPrefix()
	 * @see #getSuffix()
	 */
	protected String postProcessViewName(String viewName) {
		return getPrefix() + viewName + getSuffix();
	}

}
