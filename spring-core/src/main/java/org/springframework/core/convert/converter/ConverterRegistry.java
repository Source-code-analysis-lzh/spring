/*
 * Copyright 2002-2016 the original author or authors.
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

/**
 * 用于在类型转换系统中注册转换器.
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 3.0
 */
public interface ConverterRegistry {

	/**
	 * 将转换器添加到此注册表. 可转换源/目标类型对是从Converter的参数化类型派生的.
	 * @throws IllegalArgumentException if the parameterized types could not be resolved
	 */
	void addConverter(Converter<?, ?> converter);

	/**
	 * 将纯换器添加到此注册表. 可转换源/目标类型对已明确指定.
	 * <p>允许将Converter重复用于多个不同的对，而不必为每个对创建Converter类.
	 * @since 3.1
	 */
	<S, T> void addConverter(Class<S> sourceType, Class<T> targetType, Converter<? super S, ? extends T> converter);

	/**
	 * 将通用转换器添加到此注册表.
	 */
	void addConverter(GenericConverter converter);

	/**
	 * 将一个范围转换器工厂添加到此注册表. 可转换源/目标类型对是从ConverterFactory的参数化类型派生的.
	 * @throws IllegalArgumentException if the parameterized types could not be resolved
	 */
	void addConverterFactory(ConverterFactory<?, ?> factory);

	/**
	 * 删除从{@code sourceType}转换到{@code targetType}的所有转换器.
	 * @param sourceType the source type
	 * @param targetType the target type
	 */
	void removeConvertible(Class<?> sourceType, Class<?> targetType);

}
