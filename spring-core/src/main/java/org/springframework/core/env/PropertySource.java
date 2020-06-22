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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * 表示键/值属性对来源的抽象基类。 底层{@linkplain #getSource() source object}可以是封装属性的任何{@code T}类型。 
 * 示例包括{@link java.util.Properties}对象，{@link java.util.Map}对象，{@code ServletContext}和
 * {@code ServletConfig}对象（用于访问init参数）。 探索{@code PropertySource}类型层次结构以查看提供的实现。
 *
 * <p>{@code PropertySource}对象通常不是孤立使用的，而是通过{@link PropertySources}对象使用的，
 * 该对象聚合属性源，并与{@link PropertyResolver}实现联合使用，该实现可以对整个{@code PropertySources}执行基于优先级的搜索。
 *
 * <p>{@code PropertySource}身份的确定不是基于封装属性的内容，而是仅基于{@code PropertySource}的
 * {@link #getName() name}。 这对于在集合上下文中操作{@code PropertySource}对象很有用。 
 * 有关详细信息，请参见{@link MutablePropertySources}中的操作以及{@link #named(String)}和{@link #toString()}方法。
 *
 * <p>请注意，在使用@{@link org.springframework.context.annotation.Configuration Configuration}类时，
 * @{@link org.springframework.context.annotation.PropertySource PropertySource}批注
 * 提供了将属性源添加到封闭{@code Environment}的便捷声明式方法。
 *
 * @author Chris Beams
 * @since 3.1
 * @param <T> the source type
 * @see PropertySources
 * @see PropertyResolver
 * @see PropertySourcesPropertyResolver
 * @see MutablePropertySources
 * @see org.springframework.context.annotation.PropertySource
 */
public abstract class PropertySource<T> {

	protected final Log logger = LogFactory.getLog(getClass());

	protected final String name;

	protected final T source;


	/**
	 * 使用给定的名称和源对象创建一个新的{@code PropertySource}。
	 */
	public PropertySource(String name, T source) {
		Assert.hasText(name, "Property source name must contain at least one character");
		Assert.notNull(source, "Property source must not be null");
		this.name = name;
		this.source = source;
	}

	/**
	 * 用给定的名称和新的{@code Object}实例作为底层源来创建一个新的{@code PropertySource}。
	 * <p>在创建从不查询实际源而是返回硬编码值的匿名实现时，在测试场景中通常很有用。
	 */
	@SuppressWarnings("unchecked")
	public PropertySource(String name) {
		this(name, (T) new Object());
	}


	/**
	 * Return the name of this {@code PropertySource}.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Return the underlying source object for this {@code PropertySource}.
	 */
	public T getSource() {
		return this.source;
	}

	/**
	 * 返回此{@code PropertySource}是否包含给定名称。
	 * <p>此实现仅检查{@link #getProperty(String)}中的空返回值。 如果可能，子类可能希望实现更有效的算法。
	 * @param name the property name to find
	 */
	public boolean containsProperty(String name) {
		return (getProperty(name) != null);
	}

	/**
	 * Return the value associated with the given name,
	 * or {@code null} if not found.
	 * @param name the property to find
	 * @see PropertyResolver#getRequiredProperty(String)
	 */
	@Nullable
	public abstract Object getProperty(String name);


	/**
	 * This {@code PropertySource} object is equal to the given object if:
	 * <ul>
	 * <li>they are the same instance
	 * <li>the {@code name} properties for both objects are equal
	 * </ul>
	 * <p>No properties other than {@code name} are evaluated.
	 */
	@Override
	public boolean equals(@Nullable Object other) {
		return (this == other || (other instanceof PropertySource &&
				ObjectUtils.nullSafeEquals(this.name, ((PropertySource<?>) other).name)));
	}

	/**
	 * Return a hash code derived from the {@code name} property
	 * of this {@code PropertySource} object.
	 */
	@Override
	public int hashCode() {
		return ObjectUtils.nullSafeHashCode(this.name);
	}

	/**
	 * Produce concise output (type and name) if the current log level does not include
	 * debug. If debug is enabled, produce verbose output including the hash code of the
	 * PropertySource instance and every name/value property pair.
	 * <p>This variable verbosity is useful as a property source such as system properties
	 * or environment variables may contain an arbitrary number of property pairs,
	 * potentially leading to difficult to read exception and log messages.
	 * @see Log#isDebugEnabled()
	 */
	@Override
	public String toString() {
		if (logger.isDebugEnabled()) {
			return getClass().getSimpleName() + "@" + System.identityHashCode(this) +
					" {name='" + this.name + "', properties=" + this.source + "}";
		}
		else {
			return getClass().getSimpleName() + " {name='" + this.name + "'}";
		}
	}


	/**
	 * 返回仅用于集合比较目的的{@code PropertySource}实现。
	 * <p>Primarily for internal use, but given a collection of {@code PropertySource} objects, may be
	 * used as follows:
	 * <pre class="code">
	 * {@code List<PropertySource<?>> sources = new ArrayList<PropertySource<?>>();
	 * sources.add(new MapPropertySource("sourceA", mapA));
	 * sources.add(new MapPropertySource("sourceB", mapB));
	 * assert sources.contains(PropertySource.named("sourceA"));
	 * assert sources.contains(PropertySource.named("sourceB"));
	 * assert !sources.contains(PropertySource.named("sourceC"));
	 * }</pre>
	 * The returned {@code PropertySource} will throw {@code UnsupportedOperationException}
	 * if any methods other than {@code equals(Object)}, {@code hashCode()}, and {@code toString()}
	 * are called.
	 * @param name the name of the comparison {@code PropertySource} to be created and returned.
	 */
	public static PropertySource<?> named(String name) {
		return new ComparisonPropertySource(name);
	}


	/**
	 * 在无法在应用程序上下文创建时立即初始化实际属性源的情况下，该{@code PropertySource}用作占位符。 
	 * 例如，基于{@code ServletContext}的属性源必须等待，直到{@code ServletContext}对象可用于
	 * 其包含的{@code ApplicationContext}。 在这种情况下，应使用存根保留属性源的预期默认位置/顺序，然后在上下文刷新期间将其替换。
	 * @see org.springframework.context.support.AbstractApplicationContext#initPropertySources()
	 * @see org.springframework.web.context.support.StandardServletEnvironment
	 * @see org.springframework.web.context.support.ServletContextPropertySource
	 */
	public static class StubPropertySource extends PropertySource<Object> {

		public StubPropertySource(String name) {
			super(name, new Object());
		}

		/**
		 * Always returns {@code null}.
		 */
		@Override
		@Nullable
		public String getProperty(String name) {
			return null;
		}
	}


	/**
	 * A {@code PropertySource} implementation intended for collection comparison
	 * purposes.
	 *
	 * @see PropertySource#named(String)
	 */
	static class ComparisonPropertySource extends StubPropertySource {

		private static final String USAGE_ERROR =
				"ComparisonPropertySource instances are for use with collection comparison only";

		public ComparisonPropertySource(String name) {
			super(name);
		}

		@Override
		public Object getSource() {
			throw new UnsupportedOperationException(USAGE_ERROR);
		}

		@Override
		public boolean containsProperty(String name) {
			throw new UnsupportedOperationException(USAGE_ERROR);
		}

		@Override
		@Nullable
		public String getProperty(String name) {
			throw new UnsupportedOperationException(USAGE_ERROR);
		}
	}

}
