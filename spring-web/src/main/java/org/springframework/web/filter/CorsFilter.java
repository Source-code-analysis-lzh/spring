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

package org.springframework.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsProcessor;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.DefaultCorsProcessor;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * 借助{@link CorsProcessor}实现（默认情况下为{@link DefaultCorsProcessor}），
 * 用于处理CORS预检请求并拦截CORS简单请求和实际请求的{@link javax.servlet.Filter}，
 * 以便使用提供的{@link CorsConfigurationSource}（例如，{@link UrlBasedCorsConfigurationSource}实例）
 * 增加关于CORS响应头(如 {@code Access-Control-Allow-Origin})。
 *
 * <p>这是Spring MVC Java配置和XML名称空间CORS配置的替代方法，
 * 对仅依赖于spring-web（不依赖spring-webmvc）的应用程序
 * 或需要在{@link javax.servlet.Filter}级别执行CORS检查的安全性约束很有用。
 *
 * <p>该过滤器可以与{@link DelegatingFilterProxy}结合使用，以帮助其初始化。
 *
 * @author Sebastien Deleuze
 * @since 4.2
 * @see <a href="https://www.w3.org/TR/cors/">CORS W3C recommendation</a>
 */
public class CorsFilter extends OncePerRequestFilter {

	private final CorsConfigurationSource configSource;

	private CorsProcessor processor = new DefaultCorsProcessor();


	/**
	 * Constructor accepting a {@link CorsConfigurationSource} used by the filter
	 * to find the {@link CorsConfiguration} to use for each incoming request.
	 * @see UrlBasedCorsConfigurationSource
	 */
	public CorsFilter(CorsConfigurationSource configSource) {
		Assert.notNull(configSource, "CorsConfigurationSource must not be null");
		this.configSource = configSource;
	}


	/**
	 * Configure a custom {@link CorsProcessor} to use to apply the matched
	 * {@link CorsConfiguration} for a request.
	 * <p>By default {@link DefaultCorsProcessor} is used.
	 */
	public void setCorsProcessor(CorsProcessor processor) {
		Assert.notNull(processor, "CorsProcessor must not be null");
		this.processor = processor;
	}


	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {

		CorsConfiguration corsConfiguration = this.configSource.getCorsConfiguration(request);
		boolean isValid = this.processor.processRequest(corsConfiguration, request, response);
		if (!isValid || CorsUtils.isPreFlightRequest(request)) {
			return;
		}
		filterChain.doFilter(request, response);
	}

}
