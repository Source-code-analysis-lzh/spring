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

import java.util.function.Predicate;

/**
 * {@link Environment}的{@linkplain Environment#acceptsProfiles(Profiles) 接受}的Profile谓词.
 *
 * <p>可以直接实现，或更通常地，可以使用{@link #of(String...) of(...)}工厂方法创建.
 *
 * @author Phillip Webb
 * @since 5.1
 */
@FunctionalInterface
public interface Profiles {

	/**
	 * 测试此{@code Profiles}实例是否与给定的活动Profile谓词匹配.
	 * @param activeProfiles predicate that tests whether a given profile is
	 * currently active
	 */
	boolean matches(Predicate<String> activeProfiles);


	/**
	 * 创建一个新的{@link Profiles}实例，以检查是否与给定的<em>profile strings</em>字符串匹配.
	 * <p>如果任何给定的配置文件字符串匹配，则返回的实例将{@linkplain Profiles#matches(Predicate) 匹配}.
	 * <p>配置文件字符串可以包含简单的profile文件名称（例如{@code "production"}）或profile表达式.
	 * profile表达式允许表达更复杂的配置文件逻辑，例如{@code "production & cloud"}.
	 * <p>profile文件表达式中支持以下运算符：
	 * <ul>
	 * <li>{@code !} - A logical <em>not</em> of the profile</li>
	 * <li>{@code &} - A logical <em>and</em> of the profiles</li>
	 * <li>{@code |} - A logical <em>or</em> of the profiles</li>
	 * </ul>
	 * <p>Please note that the {@code &} and {@code |} operators may not be mixed
	 * without using parentheses. For example {@code "a & b | c"} is not a valid
	 * expression; it must be expressed as {@code "(a & b) | c"} or
	 * {@code "a & (b | c)"}.
	 * @param profiles the <em>profile strings</em> to include
	 * @return a new {@link Profiles} instance
	 */
	static Profiles of(String... profiles) {
		return ProfilesParser.parse(profiles);
	}

}
