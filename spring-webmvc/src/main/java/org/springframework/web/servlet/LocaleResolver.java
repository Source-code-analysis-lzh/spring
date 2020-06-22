/*
 * Copyright 2002-2017 the original author or authors.
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

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;

/**
 * 基于Web的语言环境解析策略的接口，它既可以通过请求进行语言环境解析，
 * 又可以通过请求和响应进行语言环境修改。
 *
 * <p>该接口允许基于请求，会话，Cookie等的实现。默认实现为
 * {@link org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver}，
 * 只需使用相应HTTP标头提供的请求语言环境即可。
 *
 * <p>使用{@link org.springframework.web.servlet.support.RequestContext#getLocale()}
 * 可以在控制器或视图中检索当前语言环境，而与实际的解析策略无关。
 *
 * <p>注意：从Spring 4.0开始，有一个名为{@link LocaleContextResolver}的扩展策略接口，
 * 允许解析{@link org.springframework.context.i18n.LocaleContext}对象，并可能包括相关的时区信息。 
 * Spring提供的解析器实现以便在适当的地方实现扩展的{@link LocaleContextResolver}接口。
 *
 * @author Juergen Hoeller
 * @since 27.02.2003
 * @see LocaleContextResolver
 * @see org.springframework.context.i18n.LocaleContextHolder
 * @see org.springframework.web.servlet.support.RequestContext#getLocale
 * @see org.springframework.web.servlet.support.RequestContextUtils#getLocale
 */
public interface LocaleResolver {

	/**
	 * 通过给定的请求解析当前语言环境。 在任何情况下都可以返回默认语言环境作为后备。
	 * @param request the request to resolve the locale for
	 * @return the current locale (never {@code null})
	 */
	Locale resolveLocale(HttpServletRequest request);

	/**
	 * 将当前语言环境设置为给定的语言环境。
	 * @param request the request to be used for locale modification
	 * @param response the response to be used for locale modification
	 * @param locale the new locale, or {@code null} to clear the locale
	 * @throws UnsupportedOperationException if the LocaleResolver
	 * implementation does not support dynamic changing of the locale
	 */
	void setLocale(HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Locale locale);

}
