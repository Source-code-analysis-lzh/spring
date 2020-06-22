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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

/**
 * Overrides {@link HttpServletResponse#sendRedirect(String)} and handles it by
 * setting the HTTP status and "Location" headers, which keeps the Servlet
 * container from re-writing relative redirect URLs into absolute ones.
 * Servlet containers are required to do that but against the recommendation of
 * <a href="https://tools.ietf.org/html/rfc7231#section-7.1.2"> RFC 7231 Section 7.1.2</a>,
 * and furthermore not necessarily taking into account "X-Forwarded" headers.
 * 重写{@link HttpServletResponse#sendRedirect(String)}并通过设置HTTP状态和"Location"标头
 * 来处理它，以防止Servlet容器将相对重定向URL重写为绝对URL。 需要Servlet容器来执行此操作，
 * 但要违反<a href="https://tools.ietf.org/html/rfc7231#section-7.1.2"> RFC 7231 Section 7.1.2</a>
 * 的建议，并且不必考虑"X-Forwarded"标头。
 * 
 * <p>通过RelativeRedirectResponseWrapper包装原Response对象,重写默认的sendRedirect重定向方法改为相对路径重定向。
 *
 * <p>注意：虽然在RFC中建议使用相对重定向，但在某些具有反向代理的配置下，它们可能无法正常工作。
 *
 * @author Rob Winch
 * @author Rossen Stoyanchev
 * @since 4.3.10
 */
public class RelativeRedirectFilter extends OncePerRequestFilter {

	private HttpStatus redirectStatus = HttpStatus.SEE_OTHER;


	/**
	 * Set the default HTTP Status to use for redirects.
	 * <p>By default this is {@link HttpStatus#SEE_OTHER}.
	 * @param status the 3xx redirect status to use
	 */
	public void setRedirectStatus(HttpStatus status) {
		Assert.notNull(status, "Property 'redirectStatus' is required");
		Assert.isTrue(status.is3xxRedirection(), "Not a redirect status code");
		this.redirectStatus = status;
	}

	/**
	 * Return the configured redirect status.
	 */
	public HttpStatus getRedirectStatus() {
		return this.redirectStatus;
	}


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		response = RelativeRedirectResponseWrapper.wrapIfNecessary(response, this.redirectStatus);
		filterChain.doFilter(request, response);
	}

}
