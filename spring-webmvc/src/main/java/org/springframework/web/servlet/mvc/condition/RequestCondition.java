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

package org.springframework.web.servlet.mvc.condition;

import javax.servlet.http.HttpServletRequest;

import org.springframework.lang.Nullable;

/**
 * 请求映射条件的合同。
 *
 * <p>可以通过{@link #combine(Object)}组合请求条件，
 * 通过{@link #getMatchingCondition(HttpServletRequest)}与请求进行匹配，
 * 并通过{@link #compareTo(Object, HttpServletRequest)}相互比较以确定哪个条件更适合给定请求。
 *
 * @author Rossen Stoyanchev
 * @author Arjen Poutsma
 * @since 3.1
 * @param <T> the type of objects that this RequestCondition can be combined
 * with and compared to
 */
public interface RequestCondition<T> {

	/**
	 * 将此条件与另一个条件（例如来自类型级别和方法级别{@code @RequestMapping}注释的条件）组合。
	 * @param other the condition to combine with.
	 * @return a request condition instance that is the result of combining
	 * the two condition instances.
	 */
	T combine(T other);

	/**
	 * 检查条件是否与请求匹配，返回可能为当前请求创建的新实例。 
	 * 例如，具有多个URL模式的条件可能仅返回具有与请求匹配的那些模式的新实例。
	 * <p>对于CORS pre-flight请求，条件应与可能的实际请求相匹配
	 * （例如，URL模式，查询参数和"Access-Control-Request-Method"标头中的HTTP方法）。 
	 * 如果条件无法与pre-flight请求匹配，则它应返回一个内容为空的实例，从而不会导致匹配失败。
	 * @return a condition instance in case of a match or {@code null} otherwise.
	 */
	@Nullable
	T getMatchingCondition(HttpServletRequest request);

	/**
	 * 在特定请求的上下文中，将此条件与另一个条件进行比较。 
	 * 此方法假定两个实例均已通过{@link #getMatchingCondition(HttpServletRequest)}获得，
	 * 以确保它们仅具有与当前请求相关的内容。
	 */
	int compareTo(T other, HttpServletRequest request);

}
