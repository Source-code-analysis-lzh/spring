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

package org.springframework.format;

import java.lang.annotation.Annotation;

import org.springframework.core.convert.converter.ConverterRegistry;

/**
 * 字段格式逻辑的注册表.
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 3.0
 */
public interface FormatterRegistry extends ConverterRegistry {

	/**
	 * 添加打印机以打印特定类型的字段. 参数化的Printer实例隐含了字段类型.
	 * @param printer the printer to add
	 * @since 5.2
	 * @see #addFormatter(Formatter)
	 */
	void addPrinter(Printer<?> printer);

	/**
	 * 添加解析器以解析特定类型的字段. 参数化的解析器实例隐含了字段类型.
	 * @param parser the parser to add
	 * @since 5.2
	 * @see #addFormatter(Formatter)
	 */
	void addParser(Parser<?> parser);

	/**
	 * 添加格式化程序以格式化特定类型的字段. 参数化的Formatter实例隐含字段类型.
	 * @param formatter the formatter to add
	 * @since 3.1
	 * @see #addFormatterForFieldType(Class, Formatter)
	 */
	void addFormatter(Formatter<?> formatter);

	/**
	 * 添加格式化程序以格式化给定类型的字段.
	 * <p>在打印时，如果声明了格式化程序的类型T且{@code fieldType}没有继承于T，
	 * 则在委派{@code formatter}打印字段值之前，将尝试强制转换为T.
	 * 解析时，如果不能将{@code formatter}返回的解析对象不继承于运行时字段类型，
	 * 则在返回解析的字段值之前，将尝试强制转换为字段类型.
	 * @param fieldType the field type to format
	 * @param formatter the formatter to add
	 */
	void addFormatterForFieldType(Class<?> fieldType, Formatter<?> formatter);

	/**
	 * Adds a Printer/Parser pair to format fields of a specific type.
	 * The formatter will delegate to the specified {@code printer} for printing
	 * and the specified {@code parser} for parsing.
	 * <p>On print, if the Printer's type T is declared and {@code fieldType} is not assignable to T,
	 * a coercion to T will be attempted before delegating to {@code printer} to print a field value.
	 * On parse, if the object returned by the Parser is not assignable to the runtime field type,
	 * a coercion to the field type will be attempted before returning the parsed field value.
	 * @param fieldType the field type to format
	 * @param printer the printing part of the formatter
	 * @param parser the parsing part of the formatter
	 */
	void addFormatterForFieldType(Class<?> fieldType, Printer<?> printer, Parser<?> parser);

	/**
	 * 添加格式化程序以格式化带有特定格式注释的字段.
	 * @param annotationFormatterFactory the annotation formatter factory to add
	 */
	void addFormatterForFieldAnnotation(AnnotationFormatterFactory<? extends Annotation> annotationFormatterFactory);

}
