/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.format;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * 一个工厂，该工厂创建格式化器以格式化带有特定{@link Annotation}的字段的值.
 *
 * <p>例如，{@code DateTimeFormatAnnotationFormatterFactory}可能会创建一个格式化器，
 * 该格式化器对用{@code @DateTimeFormat}注释的字段上设置的日期值进行格式化.
 *
 * @author Keith Donald
 * @since 3.0
 * @param <A> the annotation type that should trigger formatting
 */
public interface AnnotationFormatterFactory<A extends Annotation> {

	/**
	 * 可以用 &lt;A&gt; 注释的字段类型.
	 */
	Set<Class<?>> getFieldTypes();

	/**
	 * 获取打印机以打印带有{@code annotation}的{@code fieldType}字段的值.
	 * <p>如果打印机接受的类型T不是继承于{@code fieldType}，
	 * 则在调用打印机之前将尝试从{@code fieldType}强制转换为T.
	 * @param annotation the annotation instance
	 * @param fieldType the type of field that was annotated
	 * @return the printer
	 */
	Printer<?> getPrinter(A annotation, Class<?> fieldType);

	/**
	 * 获取解析器以解析带有{@code annotation}的{@code fieldType}字段的提交值.
	 * <p>如果解析器返回的对象不是继承于{@code fieldType}，
	 * 则在设置字段之前将尝试强制转换为{@code fieldType}.
	 * @param annotation the annotation instance
	 * @param fieldType the type of field that was annotated
	 * @return the parser
	 */
	Parser<?> getParser(A annotation, Class<?> fieldType);

}
