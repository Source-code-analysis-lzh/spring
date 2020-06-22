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

package org.springframework.format.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明字段或方法参数应设置为数字格式.
 *
 * <p>支持按样式或自定义模式字符串格式化. 可以应用于任何JDK {@code Number}类型，例如{@code Double}和{@code Long}.
 *
 * <p>对于基于样式的格式设置，将{@link #style}属性设置为所需的{@link Style}.
 * 对于自定义格式，将{@link #pattern}属性设置为数字模式，例如{@code #, ###.##}.
 *
 * <p>每个属性都是互斥的，因此每个注释实例仅设置一个属性（一种最方便的格式设置）.
 * 指定{@link #pattern}属性指定后，它优先于{@link #style}属性.
 * 如果未指定注释属性，则默认的格式是基于样式的货币数量，具体取决于注释字段或方法参数类型.
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 3.0
 * @see java.text.NumberFormat
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
public @interface NumberFormat {

	/**
	 * The style pattern to use to format the field.
	 * <p>Defaults to {@link Style#DEFAULT} for general-purpose number formatting
	 * for most annotated types, except for money types which default to currency
	 * formatting. Set this attribute when you wish to format your field in
	 * accordance with a common style other than the default style.
	 * 用于格式化字段的样式模式.
	 * <p>对于大多数带注释的类型，通用类型的数字格式默认为{@link Style#DEFAULT}，但默认为货币格式的货币类型除外. 当您希望按照默认样式以外的常见样式设置字段格式时，请设置此属性.
	 */
	Style style() default Style.DEFAULT;

	/**
	 * The custom pattern to use to format the field.
	 * <p>Defaults to empty String, indicating no custom pattern String has been specified.
	 * Set this attribute when you wish to format your field in accordance with a
	 * custom number pattern not represented by a style.
	 */
	String pattern() default "";


	/**
	 * Common number format styles.
	 */
	enum Style {

		/**
		 * The default format for the annotated type: typically 'number' but possibly
		 * 'currency' for a money type (e.g. {@code javax.money.MonetaryAmount)}.
		 * @since 4.2
		 */
		DEFAULT,

		/**
		 * The general-purpose number format for the current locale.
		 */
		NUMBER,

		/**
		 * The percent format for the current locale.
		 */
		PERCENT,

		/**
		 * The currency format for the current locale.
		 */
		CURRENCY
	}

}
