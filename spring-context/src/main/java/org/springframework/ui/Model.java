/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.ui;

import java.util.Collection;
import java.util.Map;

import org.springframework.lang.Nullable;

/**
 * 特定于Java-5的接口，用于定义模型属性的持有者。 主要设计用于向模型添加属性。 
 * 允许以{@code java.util.Map}的形式访问整个模型。
 *
 * @author Juergen Hoeller
 * @since 2.5.1
 */
public interface Model {

	/**
	 * 在提供的名称下添加提供的属性。
	 * @param attributeName the name of the model attribute (never {@code null})
	 * @param attributeValue the model attribute value (can be {@code null})
	 */
	Model addAttribute(String attributeName, @Nullable Object attributeValue);

	/**
	 * 使用{@link org.springframework.core.Conventions#getVariableName 生成的名称}
	 * 将提供的属性添加到此{@code Map}。
	 * <p>注意：使用此方法时，空{@link java.util.Collection Collections}不会添加到模型中，
	 * 因为我们无法正确确定真实的约定名称。 查看代码应检查是否为{@code null}，
	 * 而不是像JSTL标记那样检查空集合。
	 * @param attributeValue the model attribute value (never {@code null})
	 */
	Model addAttribute(Object attributeValue);

	/**
	 * Copy all attributes in the supplied {@code Collection} into this
	 * {@code Map}, using attribute name generation for each element.
	 * @see #addAttribute(Object)
	 */
	Model addAllAttributes(Collection<?> attributeValues);

	/**
	 * Copy all attributes in the supplied {@code Map} into this {@code Map}.
	 * @see #addAttribute(String, Object)
	 */
	Model addAllAttributes(Map<String, ?> attributes);

	/**
	 * Copy all attributes in the supplied {@code Map} into this {@code Map},
	 * with existing objects of the same name taking precedence (i.e. not getting
	 * replaced).
	 */
	Model mergeAttributes(Map<String, ?> attributes);

	/**
	 * Does this model contain an attribute of the given name?
	 * @param attributeName the name of the model attribute (never {@code null})
	 * @return whether this model contains a corresponding attribute
	 */
	boolean containsAttribute(String attributeName);

	/**
	 * Return the attribute value for the given name, if any.
	 * @param attributeName the name of the model attribute (never {@code null})
	 * @return the corresponding attribute value, or {@code null} if none
	 * @since 5.2
	 */
	@Nullable
	Object getAttribute(String attributeName);

	/**
	 * Return the current set of model attributes as a Map.
	 */
	Map<String, Object> asMap();

}
