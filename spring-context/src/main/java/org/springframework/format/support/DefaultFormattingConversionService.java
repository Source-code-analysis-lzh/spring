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

package org.springframework.format.support;

import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatterRegistrar;
import org.springframework.format.datetime.joda.JodaTimeFormatterRegistrar;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.number.NumberFormatAnnotationFormatterFactory;
import org.springframework.format.number.money.CurrencyUnitFormatter;
import org.springframework.format.number.money.Jsr354NumberFormatAnnotationFormatterFactory;
import org.springframework.format.number.money.MonetaryAmountFormatter;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringValueResolver;

/**
 * 默认情况下，{@link FormattingConversionService}的一种具体配置，
 * 它使用适用于大多数应用程序的转换器和格式化器.
 *
 * <p>专为直接实例化而设计，但还公开了静态的{@link #addDefaultFormatters}实用程序方法，
 * 可临时用于任何{@code FormatterRegistry}实例，
 * 就像{@code DefaultConversionService}公开其自己的{@link DefaultConversionService#addDefaultConverters addDefaultConverters}方法一样.
 *
 * <p>根据类路径上存在的相应API，自动注册JSR-354货币和货币，JSR-310日期时间和/或Joda时间的格式化器.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 */
public class DefaultFormattingConversionService extends FormattingConversionService {

	private static final boolean jsr354Present;

	private static final boolean jodaTimePresent;

	static {
		ClassLoader classLoader = DefaultFormattingConversionService.class.getClassLoader();
		jsr354Present = ClassUtils.isPresent("javax.money.MonetaryAmount", classLoader);
		jodaTimePresent = ClassUtils.isPresent("org.joda.time.LocalDate", classLoader);
	}


	/**
	 * Create a new {@code DefaultFormattingConversionService} with the set of
	 * {@linkplain DefaultConversionService#addDefaultConverters default converters} and
	 * {@linkplain #addDefaultFormatters default formatters}.
	 */
	public DefaultFormattingConversionService() {
		this(null, true);
	}

	/**
	 * Create a new {@code DefaultFormattingConversionService} with the set of
	 * {@linkplain DefaultConversionService#addDefaultConverters default converters} and,
	 * based on the value of {@code registerDefaultFormatters}, the set of
	 * {@linkplain #addDefaultFormatters default formatters}.
	 * @param registerDefaultFormatters whether to register default formatters
	 */
	public DefaultFormattingConversionService(boolean registerDefaultFormatters) {
		this(null, registerDefaultFormatters);
	}

	/**
	 * Create a new {@code DefaultFormattingConversionService} with the set of
	 * {@linkplain DefaultConversionService#addDefaultConverters default converters} and,
	 * based on the value of {@code registerDefaultFormatters}, the set of
	 * {@linkplain #addDefaultFormatters default formatters}.
	 * @param embeddedValueResolver delegated to {@link #setEmbeddedValueResolver(StringValueResolver)}
	 * prior to calling {@link #addDefaultFormatters}.
	 * @param registerDefaultFormatters whether to register default formatters
	 */
	public DefaultFormattingConversionService(
			@Nullable StringValueResolver embeddedValueResolver, boolean registerDefaultFormatters) {

		if (embeddedValueResolver != null) {
			setEmbeddedValueResolver(embeddedValueResolver);
		}
		DefaultConversionService.addDefaultConverters(this);
		if (registerDefaultFormatters) {
			addDefaultFormatters(this);
		}
	}


	/**
	 * Add formatters appropriate for most environments: including number formatters,
	 * JSR-354 Money & Currency formatters, JSR-310 Date-Time and/or Joda-Time formatters,
	 * depending on the presence of the corresponding API on the classpath.
	 * @param formatterRegistry the service to register default formatters with
	 */
	public static void addDefaultFormatters(FormatterRegistry formatterRegistry) {
		// Default handling of number values
		formatterRegistry.addFormatterForFieldAnnotation(new NumberFormatAnnotationFormatterFactory());

		// Default handling of monetary values
		if (jsr354Present) {
			formatterRegistry.addFormatter(new CurrencyUnitFormatter());
			formatterRegistry.addFormatter(new MonetaryAmountFormatter());
			formatterRegistry.addFormatterForFieldAnnotation(new Jsr354NumberFormatAnnotationFormatterFactory());
		}

		// Default handling of date-time values

		// just handling JSR-310 specific date and time types
		new DateTimeFormatterRegistrar().registerFormatters(formatterRegistry);

		if (jodaTimePresent) {
			// handles Joda-specific types as well as Date, Calendar, Long
			new JodaTimeFormatterRegistrar().registerFormatters(formatterRegistry);
		}
		else {
			// regular DateFormat-based Date, Calendar, Long converters
			new DateFormatterRegistrar().registerFormatters(formatterRegistry);
		}
	}

}
