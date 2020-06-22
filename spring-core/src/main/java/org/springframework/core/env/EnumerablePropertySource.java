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

package org.springframework.core.env;

import org.springframework.util.ObjectUtils;

/**
 * 一个{@link PropertySource}实现，可以查询其底层源对象以枚举所有可能的属性键/值对。 
 * 公开{@link #getPropertyNames()}方法，以使调用者可以内省可用属性，而不必访问底层源对象。 
 * 这还有助于更有效地实现{@link #containsProperty(String)}，因为它可以调用{@link #getPropertyNames()}
 * 并遍历返回的数组，而不是尝试调用可能更昂贵的{@link #getProperty(String)}。 
 * 实现可以考虑缓存{@link #getPropertyNames()}的结果以充分利用此性能。
 *
 * <p>多数框架提供的{@code PropertySource}实现都是可枚举的。 一个反例是{@code JndiPropertySource}，
 * 其中由于JNDI的性质，不可能在任何给定时间确定所有可能的属性名称； 
 * 而是只能尝试访问属性（通过{@link #getProperty(String)}）以评估它是否存在。
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @param <T> the source type
 */
public abstract class EnumerablePropertySource<T> extends PropertySource<T> {

	public EnumerablePropertySource(String name, T source) {
		super(name, source);
	}

	protected EnumerablePropertySource(String name) {
		super(name);
	}


	/**
	 * Return whether this {@code PropertySource} contains a property with the given name.
	 * <p>This implementation checks for the presence of the given name within the
	 * {@link #getPropertyNames()} array.
	 * @param name the name of the property to find
	 */
	@Override
	public boolean containsProperty(String name) {
		return ObjectUtils.containsElement(getPropertyNames(), name);
	}

	/**
	 * Return the names of all properties contained by the
	 * {@linkplain #getSource() source} object (never {@code null}).
	 */
	public abstract String[] getPropertyNames();

}
