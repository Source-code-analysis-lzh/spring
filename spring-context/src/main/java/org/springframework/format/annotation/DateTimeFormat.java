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
 * 声明字段或方法参数应格式化为日期或时间.
 *
 * <p>支持按样式模式，ISO日期时间模式或自定义格式模式字符串格式化.
 * 可以应用于{@code java.util.Date}, {@code java.util.Calendar}, {@code Long}（用于毫秒时间戳）
 * 以及JSR-310 <code>java.time</code>和Joda-Time值类型.
 *
 * <p>对于基于样式的格式设置，将{@link #style}属性设置为样式模式代码.
 * 代码的第一个字符是日期样式，第二个字符是时间样式.
 * 指定字符'S'表示短样式，'M'表示中号，'L'表示长号，'F'表示完整. 通过指定样式字符'-'可以省略日期或时间.
 *
 * <p>For ISO-based formatting, set the {@link #iso} attribute to be the desired {@link ISO} format,
 * such as {@link ISO#DATE}. For custom formatting, set the {@link #pattern} attribute to be the
 * DateTime pattern, such as {@code yyyy/MM/dd hh:mm:ss a}.
 * <p>对于基于ISO的格式，请将{@link #iso}属性设置为所需的{@link ISO}格式，
 * 例如{@link ISO#DATE}. 对于自定义格式，请将{@link #pattern}属性设置为DateTime模式，例如{@code yyyy/MM/dd hh:mm:ss a}.
 *
 * <p>每个属性都是互斥的，因此每个注释实例仅设置一个属性（一种最方便的格式设置）.
 * 指定pattern属性时，它优先于style和ISO属性. 指定{@link #iso}属性时，它优先于style属性.
 * 如果未指定注释属性，则应用的默认格式是基于样式的样式代码，样式代码为'SS'（短日期，短时间）.
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 3.0
 * @see java.time.format.DateTimeFormatter
 * @see org.joda.time.format.DateTimeFormat
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
public @interface DateTimeFormat {

	/**
	 * 用于格式化字段的样式模式.
	 * <p>短日期时间默认为'SS'. 当您希望按照默认样式以外的常见样式设置字段格式时，请设置此属性.
	 */
	String style() default "SS";

	/**
	 * 用于格式化字段的ISO模式.
	 * <p>可能的ISO模式在{@link ISO}枚举中定义.
	 * <p>默认值为{@link ISO#NONE}，指示应忽略此属性. 当您希望按照ISO格式设置字段格式时，请设置此属性.
	 */
	ISO iso() default ISO.NONE;

	/**
	 * The custom pattern to use to format the field.
	 * <p>Defaults to empty String, indicating no custom pattern String has been specified.
	 * Set this attribute when you wish to format your field in accordance with a custom
	 * date time pattern not represented by a style or ISO format.
	 * <p>Note: This pattern follows the original {@link java.text.SimpleDateFormat} style,
	 * as also supported by Joda-Time, with strict parsing semantics towards overflows
	 * (e.g. rejecting a Feb 29 value for a non-leap-year). As a consequence, 'yy'
	 * characters indicate a year in the traditional style, not a "year-of-era" as in the
	 * {@link java.time.format.DateTimeFormatter} specification (i.e. 'yy' turns into 'uu'
	 * when going through that {@code DateTimeFormatter} with strict resolution mode).
	 * 用于格式化字段的自定义模式.
	 * <p>默认为空字符串，表示未指定自定义模式字符串. 当您希望根据样式或ISO格式未表示的自定义日期时间格式设置字段格式时，请设置此属性.
	 * <p>注意：此模式遵循原始的{@link java.text.SimpleDateFormat}样式（Joda-Time也支持该样式），
	 * 并且对溢出进行严格的语法解析（例如，对于非闰年则拒绝2月29日的值）. 因此，'yy'字符表示传统风格的年份，
	 * 而不是{@link java.time.format.DateTimeFormatter}规范中的“年份”（即，在严格解析模式下通过该{@code DateTimeFormatter}时，“ yy”变成“ uu”） .
	 */
	String pattern() default "";


	/**
	 * Common ISO date time format patterns.
	 */
	enum ISO {

		/**
		 * The most common ISO Date Format {@code yyyy-MM-dd},
		 * e.g. "2000-10-31".
		 */
		DATE,

		/**
		 * The most common ISO Time Format {@code HH:mm:ss.SSSXXX},
		 * e.g. "01:30:00.000-05:00".
		 */
		TIME,

		/**
		 * The most common ISO DateTime Format {@code yyyy-MM-dd'T'HH:mm:ss.SSSXXX},
		 * e.g. "2000-10-31T01:30:00.000-05:00".
		 * <p>This is the default if no annotation value is specified.
		 */
		DATE_TIME,

		/**
		 * Indicates that no ISO-based format pattern should be applied.
		 */
		NONE
	}

}
