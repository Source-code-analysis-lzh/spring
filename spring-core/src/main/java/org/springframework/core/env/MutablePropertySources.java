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

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import org.springframework.lang.Nullable;

/**
 * {@link PropertySources}接口的默认实现。 允许操纵包含的属性源，
 * 并提供用于复制现有{@code PropertySources}实例的构造函数。
 *
 * <p>在诸如{@link #addFirst}和{@link #addLast}之类的方法于优先级有关，可以使用{@link PropertyResolver}解析给定属性。
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see PropertySourcesPropertyResolver
 */
public class MutablePropertySources implements PropertySources {

	private final List<PropertySource<?>> propertySourceList = new CopyOnWriteArrayList<>();


	/**
	 * Create a new {@link MutablePropertySources} object.
	 */
	public MutablePropertySources() {
	}

	/**
	 * 从给定的{@code PropertySource}对象创建一个新的{@code MutablePropertySources}，
	 * 并保留包含的{@code PropertySource}对象的原始顺序。
	 */
	public MutablePropertySources(PropertySources propertySources) {
		this();
		for (PropertySource<?> propertySource : propertySources) {
			addLast(propertySource);
		}
	}


	@Override
	public Iterator<PropertySource<?>> iterator() {
		return this.propertySourceList.iterator();
	}

	@Override
	public Spliterator<PropertySource<?>> spliterator() {
		return Spliterators.spliterator(this.propertySourceList, 0);
	}

	@Override
	public Stream<PropertySource<?>> stream() {
		return this.propertySourceList.stream();
	}

	@Override
	public boolean contains(String name) {
		return this.propertySourceList.contains(PropertySource.named(name));
	}

	@Override
	@Nullable
	public PropertySource<?> get(String name) { // 根据名称获取PropertySource实例对象
		int index = this.propertySourceList.indexOf(PropertySource.named(name));
		return (index != -1 ? this.propertySourceList.get(index) : null);
	}


	/**
	 * Add the given property source object with highest precedence.
	 */
	public void addFirst(PropertySource<?> propertySource) {
		removeIfPresent(propertySource);
		this.propertySourceList.add(0, propertySource);
	}

	/**
	 * Add the given property source object with lowest precedence.
	 */
	public void addLast(PropertySource<?> propertySource) {
		removeIfPresent(propertySource);
		this.propertySourceList.add(propertySource);
	}

	/**
	 * Add the given property source object with precedence immediately higher
	 * than the named relative property source.
	 */
	public void addBefore(String relativePropertySourceName, PropertySource<?> propertySource) {
		assertLegalRelativeAddition(relativePropertySourceName, propertySource);
		removeIfPresent(propertySource);
		int index = assertPresentAndGetIndex(relativePropertySourceName);
		addAtIndex(index, propertySource);
	}

	/**
	 * Add the given property source object with precedence immediately lower
	 * than the named relative property source.
	 */
	public void addAfter(String relativePropertySourceName, PropertySource<?> propertySource) {
		assertLegalRelativeAddition(relativePropertySourceName, propertySource);
		removeIfPresent(propertySource);
		int index = assertPresentAndGetIndex(relativePropertySourceName);
		addAtIndex(index + 1, propertySource);
	}

	/**
	 * Return the precedence of the given property source, {@code -1} if not found.
	 */
	public int precedenceOf(PropertySource<?> propertySource) {
		return this.propertySourceList.indexOf(propertySource);
	}

	/**
	 * Remove and return the property source with the given name, {@code null} if not found.
	 * @param name the name of the property source to find and remove
	 */
	@Nullable
	public PropertySource<?> remove(String name) {
		int index = this.propertySourceList.indexOf(PropertySource.named(name));
		return (index != -1 ? this.propertySourceList.remove(index) : null);
	}

	/**
	 * Replace the property source with the given name with the given property source object.
	 * @param name the name of the property source to find and replace
	 * @param propertySource the replacement property source
	 * @throws IllegalArgumentException if no property source with the given name is present
	 * @see #contains
	 */
	public void replace(String name, PropertySource<?> propertySource) {
		int index = assertPresentAndGetIndex(name);
		this.propertySourceList.set(index, propertySource);
	}

	/**
	 * Return the number of {@link PropertySource} objects contained.
	 */
	public int size() {
		return this.propertySourceList.size();
	}

	@Override
	public String toString() {
		return this.propertySourceList.toString();
	}

	/**
	 * Ensure that the given property source is not being added relative to itself.
	 */
	protected void assertLegalRelativeAddition(String relativePropertySourceName, PropertySource<?> propertySource) {
		String newPropertySourceName = propertySource.getName();
		if (relativePropertySourceName.equals(newPropertySourceName)) {
			throw new IllegalArgumentException(
					"PropertySource named '" + newPropertySourceName + "' cannot be added relative to itself");
		}
	}

	/**
	 * Remove the given property source if it is present.
	 */
	protected void removeIfPresent(PropertySource<?> propertySource) {
		this.propertySourceList.remove(propertySource);
	}

	/**
	 * Add the given property source at a particular index in the list.
	 */
	private void addAtIndex(int index, PropertySource<?> propertySource) {
		removeIfPresent(propertySource);
		this.propertySourceList.add(index, propertySource);
	}

	/**
	 * Assert that the named property source is present and return its index.
	 * @param name {@linkplain PropertySource#getName() name of the property source} to find
	 * @throws IllegalArgumentException if the named property source is not present
	 */
	private int assertPresentAndGetIndex(String name) {
		int index = this.propertySourceList.indexOf(PropertySource.named(name));
		if (index == -1) {
			throw new IllegalArgumentException("PropertySource named '" + name + "' does not exist");
		}
		return index;
	}

}
