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

package org.springframework.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.springframework.lang.Nullable;

/**
 * 用于发现方法和构造函数的参数名称的接口。
 *
 * <p>参数名称发现并非总是可能的，但是可以尝试各种策略，例如查找可能在编译时发出的调试信息，
 * 以及查找可选地带有AspectJ注释方法的argname注释值。
 *
 * @author Rod Johnson
 * @author Adrian Colyer
 * @since 2.0
 */
public interface ParameterNameDiscoverer {

	/**
	 * 返回方法的参数名称；如果无法确定，则返回{@code null}。
	 * <p>如果参数名称仅可用于给定方法的某些参数，而不适用于其它参数，则数组中的单个条目可能为{@code null}。 
	 * 但是，建议在可行的地方改用存根参数名称。
	 * @param method the method to find parameter names for
	 * @return an array of parameter names if the names can be resolved,
	 * or {@code null} if they cannot
	 */
	@Nullable
	String[] getParameterNames(Method method);

	/**
	 * 返回构造函数的参数名称；如果无法确定，则返回{@code null}。
	 * <p>Individual entries in the array may be {@code null} if parameter names are only
	 * available for some parameters of the given constructor but not for others. However,
	 * it is recommended to use stub parameter names instead wherever feasible.
	 * @param ctor the constructor to find parameter names for
	 * @return an array of parameter names if the names can be resolved,
	 * or {@code null} if they cannot
	 */
	@Nullable
	String[] getParameterNames(Constructor<?> ctor);

}
