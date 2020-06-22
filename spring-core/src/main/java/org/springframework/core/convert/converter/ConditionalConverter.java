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

package org.springframework.core.convert.converter;

import org.springframework.core.convert.TypeDescriptor;

/**
 * 允许{@link Converter}, {@link GenericConverter} 或 {@link ConverterFactory}
 * 基于{@code source}和{@code target} {@link TypeDescriptor}的属性有条件地执行.
 *
 * <p>通常用于根据字段或类级别特征（例如注释或方法）的存在来选择性地匹配自定义转换逻辑.
 * 例如，当从字符串字段转换为日期字段时，如果目标字段也已使用{@code @DateTimeFormat}进行注释，
 * 则实现可能返回{@code true}.
 *
 * <p>作为另一个示例，当从String字段转换为{@code Account}字段时，
 * 如果目标Account类定义了公共静态{@code public static findAccount(String)}方法，则实现可能返回{@code true}.
 *
 * @author Phillip Webb
 * @author Keith Donald
 * @since 3.2
 * @see Converter
 * @see GenericConverter
 * @see ConverterFactory
 * @see ConditionalGenericConverter
 */
public interface ConditionalConverter {

	/**
	 * 是否应该选择当前正在考虑的从{@code sourceType}到{@code targetType}的转换.
	 * @param sourceType the type descriptor of the field we are converting from
	 * @param targetType the type descriptor of the field we are converting to
	 * @return true if conversion should be performed, false otherwise
	 */
	boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType);

}
