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

package org.springframework.web.servlet.i18n;

import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.lang.Nullable;
import org.springframework.web.util.WebUtils;

/**
 * {@link org.springframework.web.servlet.LocaleResolver}实现，
 * 在自定义设置的情况下在用户会话中使用locale属性，并回退到指定的默认语言环境或请求的接受标头语言环境。
 *
 * <p>如果应用程序需要用户会话，这是最合适的，但不必仅为了存储用户的语言环境而创建HttpSession。 
 * 会话也可以选择包含关联的时区属性； 或者，您可以指定默认时区。
 *
 * <p>自定义控制器可以通过在解析器上调用{@code #setLocale(Context)}来覆盖用户的语言环境和时区，
 * 例如 响应语言环境更改请求。 作为更方便的替代方法，请考虑使用
 * {@link org.springframework.web.servlet.support.RequestContext#changeLocale}。
 *
 * <p>与{@link CookieLocaleResolver}相比，此策略将本地选择的语言环境设置存储在Servlet容器的{@code HttpSession}中。
 * 因此，这些设置对于每个会话来说都是临时的，因此在每个会话终止时都会丢失。
 *
 * <p>请注意，与外部会话管理机制（例如“ Spring Session”项目）没有直接关系。 
 * 该{@code LocaleResolver}将仅针对当前的{@code HttpServletRequest}评估并修改相应的HttpSession属性。
 *
 * @author Juergen Hoeller
 * @since 27.02.2003
 * @see #setDefaultLocale
 * @see #setDefaultTimeZone
 */
public class SessionLocaleResolver extends AbstractLocaleContextResolver {

	/**
	 * Name of the session attribute that holds the Locale.
	 * Only used internally by this implementation.
	 * <p>Use {@code RequestContext(Utils).getLocale()}
	 * to retrieve the current locale in controllers or views.
	 * @see org.springframework.web.servlet.support.RequestContext#getLocale
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getLocale
	 */
	public static final String LOCALE_SESSION_ATTRIBUTE_NAME = SessionLocaleResolver.class.getName() + ".LOCALE";

	/**
	 * Name of the session attribute that holds the TimeZone.
	 * Only used internally by this implementation.
	 * <p>Use {@code RequestContext(Utils).getTimeZone()}
	 * to retrieve the current time zone in controllers or views.
	 * @see org.springframework.web.servlet.support.RequestContext#getTimeZone
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getTimeZone
	 */
	public static final String TIME_ZONE_SESSION_ATTRIBUTE_NAME = SessionLocaleResolver.class.getName() + ".TIME_ZONE";


	private String localeAttributeName = LOCALE_SESSION_ATTRIBUTE_NAME;

	private String timeZoneAttributeName = TIME_ZONE_SESSION_ATTRIBUTE_NAME;


	/**
	 * Specify the name of the corresponding attribute in the {@code HttpSession},
	 * holding the current {@link Locale} value.
	 * <p>The default is an internal {@link #LOCALE_SESSION_ATTRIBUTE_NAME}.
	 * @since 4.3.8
	 */
	public void setLocaleAttributeName(String localeAttributeName) {
		this.localeAttributeName = localeAttributeName;
	}

	/**
	 * Specify the name of the corresponding attribute in the {@code HttpSession},
	 * holding the current {@link TimeZone} value.
	 * <p>The default is an internal {@link #TIME_ZONE_SESSION_ATTRIBUTE_NAME}.
	 * @since 4.3.8
	 */
	public void setTimeZoneAttributeName(String timeZoneAttributeName) {
		this.timeZoneAttributeName = timeZoneAttributeName;
	}


	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		Locale locale = (Locale) WebUtils.getSessionAttribute(request, this.localeAttributeName);
		if (locale == null) {
			locale = determineDefaultLocale(request);
		}
		return locale;
	}

	@Override
	public LocaleContext resolveLocaleContext(final HttpServletRequest request) {
		return new TimeZoneAwareLocaleContext() {
			@Override
			public Locale getLocale() {
				Locale locale = (Locale) WebUtils.getSessionAttribute(request, localeAttributeName);
				if (locale == null) {
					locale = determineDefaultLocale(request);
				}
				return locale;
			}
			@Override
			@Nullable
			public TimeZone getTimeZone() {
				TimeZone timeZone = (TimeZone) WebUtils.getSessionAttribute(request, timeZoneAttributeName);
				if (timeZone == null) {
					timeZone = determineDefaultTimeZone(request);
				}
				return timeZone;
			}
		};
	}

	@Override
	public void setLocaleContext(HttpServletRequest request, @Nullable HttpServletResponse response,
			@Nullable LocaleContext localeContext) {

		Locale locale = null;
		TimeZone timeZone = null;
		if (localeContext != null) {
			locale = localeContext.getLocale();
			if (localeContext instanceof TimeZoneAwareLocaleContext) {
				timeZone = ((TimeZoneAwareLocaleContext) localeContext).getTimeZone();
			}
		}
		WebUtils.setSessionAttribute(request, this.localeAttributeName, locale);
		WebUtils.setSessionAttribute(request, this.timeZoneAttributeName, timeZone);
	}


	/**
	 * Determine the default locale for the given request,
	 * Called if no Locale session attribute has been found.
	 * <p>The default implementation returns the specified default locale,
	 * if any, else falls back to the request's accept-header locale.
	 * @param request the request to resolve the locale for
	 * @return the default locale (never {@code null})
	 * @see #setDefaultLocale
	 * @see javax.servlet.http.HttpServletRequest#getLocale()
	 */
	protected Locale determineDefaultLocale(HttpServletRequest request) {
		Locale defaultLocale = getDefaultLocale();
		if (defaultLocale == null) {
			defaultLocale = request.getLocale();
		}
		return defaultLocale;
	}

	/**
	 * Determine the default time zone for the given request,
	 * Called if no TimeZone session attribute has been found.
	 * <p>The default implementation returns the specified default time zone,
	 * if any, or {@code null} otherwise.
	 * @param request the request to resolve the time zone for
	 * @return the default time zone (or {@code null} if none defined)
	 * @see #setDefaultTimeZone
	 */
	@Nullable
	protected TimeZone determineDefaultTimeZone(HttpServletRequest request) {
		return getDefaultTimeZone();
	}

}
