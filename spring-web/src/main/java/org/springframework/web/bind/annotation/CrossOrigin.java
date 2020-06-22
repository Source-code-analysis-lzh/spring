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

package org.springframework.web.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.cors.CorsConfiguration;

/**
 * 允许在特定处理器类和/或处理器方法上进行跨域请求的注释。 如果配置了适当的{@code HandlerMapping}，则进行处理。
 *
 * <p>Spring Web MVC和Spring WebFlux都通过各自模块中的{@code RequestMappingHandlerMapping}
 * 支持此注释。 来自每个类型和方法级别对的注释的值将添加到{@link CorsConfiguration}，
 * 然后通过{@link CorsConfiguration#applyPermitDefaultValues()}应用默认值。
 *
 * <p>组合全局和局部配置的规则通常是相加的-例如 所有全局和所有本地源。 
 * 对于只能接受单个值的那些属性（例如{@code allowCredentials}和{@code maxAge}），
 * 局部属性将覆盖全局值。 有关更多详细信息，请参见{@link CorsConfiguration#combine(CorsConfiguration)}。
 *
 * @author Russell Allen
 * @author Sebastien Deleuze
 * @author Sam Brannen
 * @since 4.2
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CrossOrigin {

	/** @deprecated as of Spring 5.0, in favor of {@link CorsConfiguration#applyPermitDefaultValues} */
	@Deprecated
	String[] DEFAULT_ORIGINS = {"*"};

	/** @deprecated as of Spring 5.0, in favor of {@link CorsConfiguration#applyPermitDefaultValues} */
	@Deprecated
	String[] DEFAULT_ALLOWED_HEADERS = {"*"};

	/** @deprecated as of Spring 5.0, in favor of {@link CorsConfiguration#applyPermitDefaultValues} */
	@Deprecated
	boolean DEFAULT_ALLOW_CREDENTIALS = false;

	/** @deprecated as of Spring 5.0, in favor of {@link CorsConfiguration#applyPermitDefaultValues} */
	@Deprecated
	long DEFAULT_MAX_AGE = 1800;


	/**
	 * Alias for {@link #origins}.
	 */
	@AliasFor("origins")
	String[] value() default {};

	/**
	 * 指定来源的允许来源列表，例如 {@code "https://domain1.com"}或所有来源的{@code "*"}。
	 * <p>实际CORS请求的预检请求的Access-Control-Allow-Origin响应标头中列出了匹配的来源。
	 * <p>默认情况下，所有来源都是允许的。
	 * <p>注意：CORS会检查来自"Forwarded"（<a href="https://tools.ietf.org/html/rfc7239">RFC 7239</a>）
	 * ，"X-Forwarded-Host", "X-Forwarded-Port", 和 "X-Forwarded-Proto"标头（如果存在）的值，
	 * 以反映客户端起源的地址。 考虑使用{@code ForwardedHeaderFilter}
	 * 以便从中心位置选择是提取还是使用还是丢弃此类标头。 有关此过滤器的更多信息，
	 * 请参见Spring Framework参考。
	 * @see #value
	 */
	@AliasFor("value")
	String[] origins() default {};

	/**
	 * 实际请求中允许的请求标头列表，可能为{@code "*"}以允许所有标头。
	 * <p>预检请求的{@code Access-Control-Allow-Headers}响应标头中列出了允许的标头。
	 * <p>如果标头名称是以下之一，则不需要列出该标头名称：
	 * {@code Cache-Control}, {@code Content-Language}, {@code Expires},
	 * {@code Last-Modified}, or {@code Pragma}。
	 * <p>默认情况下，所有请求的标头都是允许的。
	 */
	String[] allowedHeaders() default {};

	/**
	 * 用户代理将允许客户端访问的实际响应标头列表，而不是"simple"标头，如
	 * {@code Cache-Control}, {@code Content-Language}, {@code Content-Type},
	 * {@code Expires}, {@code Last-Modified}, or {@code Pragma}
	 * <p>公开的标头在实际CORS请求的{@code Access-Control-Expose-Headers}响应标头中列出。
	 * <p>默认情况下，没有标为公开的标题。
	 */
	String[] exposedHeaders() default {};

	/**
	 * 受支持的HTTP请求方法的列表。
	 * <p>默认情况下，受支持的方法与控制器方法所映射的方法相同。
	 */
	RequestMethod[] methods() default {};

	/**
	 * 浏览器是否应将凭据（例如跨域请求带cookies）发送到带注释的端点。 
	 * 在预检请求的{@code Access-Control-Allow-Credentials}响应标头上设置配置的值。
	 * <p>注意：请注意，此选项通过暴露敏感的用户特定信息（例如cookie和CSRF令牌）
	 * 建立了与配置域的高度信任，并且还增加了Web应用程序的遭受攻击。
	 * <p>默认情况下不设置此选项，在这种情况下，也不会设置
	 * {@code Access-Control-Allow-Credentials}标头，因此不允许使用凭据。
	 */
	String allowCredentials() default "";

	/**
	 * 预检响应的缓存持续时间的最长期限（以秒为单位）。
	 * <p>此属性控制预检请求的{@code Access-Control-Max-Age}响应标头的值。
	 * <p>将此值设置为合理的值可以减少浏览器所需的预检请求/响应交互的次数。 负值表示未定义。
	 * <p>默认情况下，此设置为{@code 1800}秒（30分钟）。
	 */
	long maxAge() default -1;

}
