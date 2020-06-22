/*
 * Copyright 2002-2015 the original author or authors.
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

import java.util.Map;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * 专为与{@linkplain AbstractEnvironment#getSystemEnvironment() 系统环境变量}一起使用而设计的{@link MapPropertySource}。 
 * 补全Bash和其他shell中的约束，这些约束不允许包含句点字符和/或连字符的变量； 还允许在属性名称上使用大写形式，以更方便地使用shell。
 *
 * <p>例如，对{@code getProperty("foo.bar")}的调用将尝试查找原始属性或任何“等效”属性的值，并返回找到的第一个：
 * <ul>
 * <li>{@code foo.bar} - the original name</li>
 * <li>{@code foo_bar} - with underscores for periods (if any)</li>
 * <li>{@code FOO.BAR} - original, with upper case</li>
 * <li>{@code FOO_BAR} - with underscores and upper case</li>
 * </ul>
 * 上面的任何连字符变体也可以使用，甚至可以混合点/连字符变体。
 *
 * <p>对于{@link #containsProperty(String)}的调用也是如此，如果存在以上任何属性，
 * 则返回{@code true}，否则返回{@code false}。
 *
 * <p>当指定活动或默认配置(profiles)文件作为环境变量时，此功能特别有用。 Bash不允许以下内容：
 *
 * <pre class="code">spring.profiles.active=p1 java -classpath ... MyApp</pre>
 *
 * 但是，以下语法是允许的，也是更常规的语法：
 *
 * <pre class="code">SPRING_PROFILES_ACTIVE=p1 java -classpath ... MyApp</pre>
 *
 * <p>为此类（或程序包）启用调试或跟踪级别的日志记录，以获取解释何时发生这些“属性名称解析”的消息。
 *
 * <p>默认情况下，此属性源包含在{@link StandardEnvironment}及其所有子类中。
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see StandardEnvironment
 * @see AbstractEnvironment#getSystemEnvironment()
 * @see AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME
 */
public class SystemEnvironmentPropertySource extends MapPropertySource {

	/**
	 * Create a new {@code SystemEnvironmentPropertySource} with the given name and
	 * delegating to the given {@code MapPropertySource}.
	 */
	public SystemEnvironmentPropertySource(String name, Map<String, Object> source) {
		super(name, source);
	}


	/**
	 * Return {@code true} if a property with the given name or any underscore/uppercase variant
	 * thereof exists in this property source.
	 */
	@Override
	public boolean containsProperty(String name) {
		return (getProperty(name) != null);
	}

	/**
	 * This implementation returns {@code true} if a property with the given name or
	 * any underscore/uppercase variant thereof exists in this property source.
	 */
	@Override
	@Nullable
	public Object getProperty(String name) {
		String actualName = resolvePropertyName(name);
		if (logger.isDebugEnabled() && !name.equals(actualName)) {
			logger.debug("PropertySource '" + getName() + "' does not contain property '" + name +
					"', but found equivalent '" + actualName + "'");
		}
		return super.getProperty(actualName);
	}

	/**
	 * Check to see if this property source contains a property with the given name, or
	 * any underscore / uppercase variation thereof. Return the resolved name if one is
	 * found or otherwise the original name. Never returns {@code null}.
	 */
	protected final String resolvePropertyName(String name) {
		Assert.notNull(name, "Property name must not be null");
		String resolvedName = checkPropertyName(name);
		if (resolvedName != null) {
			return resolvedName;
		}
		String uppercasedName = name.toUpperCase();
		if (!name.equals(uppercasedName)) {
			resolvedName = checkPropertyName(uppercasedName);
			if (resolvedName != null) {
				return resolvedName;
			}
		}
		return name;
	}

	@Nullable
	private String checkPropertyName(String name) {
		// Check name as-is
		if (containsKey(name)) {
			return name;
		}
		// Check name with just dots replaced
		String noDotName = name.replace('.', '_');
		if (!name.equals(noDotName) && containsKey(noDotName)) {
			return noDotName;
		}
		// Check name with just hyphens replaced
		String noHyphenName = name.replace('-', '_');
		if (!name.equals(noHyphenName) && containsKey(noHyphenName)) {
			return noHyphenName;
		}
		// Check name with dots and hyphens replaced
		String noDotNoHyphenName = noDotName.replace('-', '_');
		if (!noDotName.equals(noDotNoHyphenName) && containsKey(noDotNoHyphenName)) {
			return noDotNoHyphenName;
		}
		// Give up
		return null;
	}

	private boolean containsKey(String name) {
		return (isSecurityManagerPresent() ? this.source.keySet().contains(name) : this.source.containsKey(name));
	}

	protected boolean isSecurityManagerPresent() {
		return (System.getSecurityManager() != null);
	}

}
