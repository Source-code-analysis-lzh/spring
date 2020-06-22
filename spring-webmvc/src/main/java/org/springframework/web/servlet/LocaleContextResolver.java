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

import org.springframework.context.i18n.LocaleContext;
import org.springframework.lang.Nullable;

/**
 * 扩展了{@link LocaleResolver}，增加了对丰富的语言上下文环境的支持（可能包括语言环境和时区信息）。
 *
 * @author Juergen Hoeller
 * @since 4.0
 * @see org.springframework.context.i18n.LocaleContext
 * @see org.springframework.context.i18n.TimeZoneAwareLocaleContext
 * @see org.springframework.context.i18n.LocaleContextHolder
 * @see org.springframework.web.servlet.support.RequestContext#getTimeZone
 * @see org.springframework.web.servlet.support.RequestContextUtils#getTimeZone
 */
public interface LocaleContextResolver extends LocaleResolver {

	/**
	 * 通过给定的请求解析当前的语言上下文环境。
	 * <p>这主要用于框架级别的处理； 考虑使用
	 * {@link org.springframework.web.servlet.support.RequestContextUtils}或
	 * {@link org.springframework.web.servlet.support.RequestContext}
	 * 对当前语言环境和/或时区进行应用程序级访问。
	 * <p>返回的上下文可以是{@link org.springframework.context.i18n.TimeZoneAwareLocaleContext}，
	 * 其中包含具有关联的时区信息的语言环境。 只需应用一个{@code instanceof} check并进行相应的强制类型转换。
	 * <p>自定义解析器实现也可能会在返回的上下文中返回额外的设置，这些设置又可以通过向下转换进行访问。
	 * @param request the request to resolve the locale context for
	 * @return the current locale context (never {@code null}
	 * @see #resolveLocale(HttpServletRequest)
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getLocale
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getTimeZone
	 */
	LocaleContext resolveLocaleContext(HttpServletRequest request);

	/**
	 * 将当前语言环境设置为给定的语言环境，可能包括具有相关时区信息的语言环境。
	 * @param request the request to be used for locale modification
	 * @param response the response to be used for locale modification
	 * @param localeContext the new locale context, or {@code null} to clear the locale
	 * @throws UnsupportedOperationException if the LocaleResolver implementation
	 * does not support dynamic changing of the locale or time zone
	 * @see #setLocale(HttpServletRequest, HttpServletResponse, Locale)
	 * @see org.springframework.context.i18n.SimpleLocaleContext
	 * @see org.springframework.context.i18n.SimpleTimeZoneAwareLocaleContext
	 */
	void setLocaleContext(HttpServletRequest request, @Nullable HttpServletResponse response,
			@Nullable LocaleContext localeContext);

}
