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

package org.springframework.web.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

/**
 * {@link javax.servlet.Filter}，用于将提交的方法参数转换为HTTP方法，
 * 可通过{@link HttpServletRequest#getMethod()}进行检索。 由于浏览器当前仅支持GET和POST，
 * 因此一种通用技术（例如，原型库使用的一种技术）是使用带有附加隐藏表单字段（{@code _method}）
 * 的普通POST来传递“真实的” HTTP方法。 该过滤器读取该参数，并相应地更改
 * {@link HttpServletRequestWrapper#getMethod()}返回值。 
 * 仅允许使用{@code "PUT"}, {@code "DELETE"} and {@code "PATCH"} HTTP方法。
 * 
 * <p>浏览器端的form表单只支持GET与POST这两个值,而不支持DELETE、PUT等，就算你硬要写成PUT它也识别不了，会默认用POST发送。
 * 可以用js，除了js，spring3提供啦HiddenHttpMethodFilter过滤器来解决这个问题。
 *
 * <p>请求参数的名称默认为{@code _method}，但可以通过{@link #setMethodParam(String) methodParam}属性进行修改。
 *
 * <p>注意：对于multipart POST请求，此过滤器需要在multipart处理后运行，
 * 因为它固有的检查POST正文参数的需要。 因此，通常，在{@code web.xml}过滤器链中的此
 * HiddenHttpMethodFilter之前放置一个
 * Spring {@link org.springframework.web.multipart.support.MultipartFilter}。
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 */
public class HiddenHttpMethodFilter extends OncePerRequestFilter {

	private static final List<String> ALLOWED_METHODS =
			Collections.unmodifiableList(Arrays.asList(HttpMethod.PUT.name(),
					HttpMethod.DELETE.name(), HttpMethod.PATCH.name()));

	/** Default method parameter: {@code _method}. */
	public static final String DEFAULT_METHOD_PARAM = "_method";

	private String methodParam = DEFAULT_METHOD_PARAM;


	/**
	 * Set the parameter name to look for HTTP methods.
	 * @see #DEFAULT_METHOD_PARAM
	 */
	public void setMethodParam(String methodParam) {
		Assert.hasText(methodParam, "'methodParam' must not be empty");
		this.methodParam = methodParam;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		HttpServletRequest requestToUse = request;

		if ("POST".equals(request.getMethod()) && request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE) == null) {
			String paramValue = request.getParameter(this.methodParam);
			if (StringUtils.hasLength(paramValue)) {
				String method = paramValue.toUpperCase(Locale.ENGLISH);
				if (ALLOWED_METHODS.contains(method)) {
					requestToUse = new HttpMethodRequestWrapper(request, method);
				}
			}
		}

		filterChain.doFilter(requestToUse, response);
	}


	/**
	 * Simple {@link HttpServletRequest} wrapper that returns the supplied method for
	 * {@link HttpServletRequest#getMethod()}.
	 */
	private static class HttpMethodRequestWrapper extends HttpServletRequestWrapper {

		private final String method;

		public HttpMethodRequestWrapper(HttpServletRequest request, String method) {
			super(request);
			this.method = method;
		}

		@Override
		public String getMethod() {
			return this.method;
		}
	}

}
