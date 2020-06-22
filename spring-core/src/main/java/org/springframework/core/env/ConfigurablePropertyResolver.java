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

package org.springframework.core.env;

import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.lang.Nullable;

/**
 * 大多数（如果不是全部）{@link PropertyResolver}类型都将实现的配置接口.
 * 提供用于访问和定制将属性值从一种类型转换为另一种类型时使用的
 * {@link org.springframework.core.convert.ConversionService ConversionService}的功能.
 *
 * @author Chris Beams
 * @since 3.1
 */
public interface ConfigurablePropertyResolver extends PropertyResolver {

	/**
	 * 返回对属性执行类型转换时使用的{@link ConfigurableConversionService}。
	 * <p>返回的转换服务的可配置性质允许方便地添加和删除各个{@code Converter}实例：
	 * <pre class="code">
	 * ConfigurableConversionService cs = env.getConversionService();
	 * cs.addConverter(new FooConverter());
	 * </pre>
	 * @see PropertyResolver#getProperty(String, Class)
	 * @see org.springframework.core.convert.converter.ConverterRegistry#addConverter
	 */
	ConfigurableConversionService getConversionService();

	/**
	 * 设置在属性上执行类型转换时要使用的{@link ConfigurableConversionService}。
	 * <p>注意：作为完全替换{@code ConversionService}的方法，
	 * 请考虑通过钻入{@link #getConversionService()}并调用诸如{@code #addConverter}
	 * 之类的方法来添加或删除单个{@code Converter}实例。
	 * @see PropertyResolver#getProperty(String, Class)
	 * @see #getConversionService()
	 * @see org.springframework.core.convert.converter.ConverterRegistry#addConverter
	 */
	void setConversionService(ConfigurableConversionService conversionService);

	/**
	 * 设置由此解析器替换的占位符必须的前缀。
	 */
	void setPlaceholderPrefix(String placeholderPrefix);

	/**
	 * 设置此解析器替换的占位符必须的后缀。
	 */
	void setPlaceholderSuffix(String placeholderSuffix);

	/**
	 * 指定由此解析器替换的占位符及其关联的默认值之间的分隔字符；
	 * 如果没有此类特殊字符作为值分隔符处理，则为{@code null}。
	 */
	void setValueSeparator(@Nullable String valueSeparator);

	/**
	 * 设置遇到嵌套在给定属性值内的无法解析的占位符时是否引发异常。 {@code false}值表示严格的解决方案，即会引发异常。 
	 * {@code true}值表示无法解析的嵌套占位符应以其未解析的${...}形式传递。
	 * <p>{@link #getProperty(String)}及其变体的实现必须检查此处设置的值，以便在属性值包含无法解析的占位符时确定正确的行为。
	 * @since 3.2
	 */
	void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders);

	/**
	 * 指定必须存在哪些属性，以通过{@link #validateRequiredProperties()}进行验证。
	 */
	void setRequiredProperties(String... requiredProperties);

	/**
	 * 验证{@link #setRequiredProperties}指定的每个属性是否存在并解析为非{@code null}值。
	 * @throws MissingRequiredPropertiesException if any of the required
	 * properties are not resolvable.
	 */
	void validateRequiredProperties() throws MissingRequiredPropertiesException;

}
