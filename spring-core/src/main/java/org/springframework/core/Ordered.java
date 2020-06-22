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

package org.springframework.core;

/**
 * {@code Ordered}接口是可以由应该可排序的对象（例如在{@code Collection}中）实现的接口.
 *
 * <p>实际{@link #getOrder() order}可以解释为优先级排序，第一个对象（具有最低顺序值）具有最高优先级.
 *
 * <p>请注意，此接口还有一个<em>priority</em>标记接口：{@link PriorityOrdered}.
 * 有关{@code PriorityOrdered}对象相对于普通{@link Ordered}对象如何排序的详细信息，请查阅Javadoc.
 *
 * <p>Consult the Javadoc for {@link OrderComparator} for details on the
 * sort semantics for non-ordered objects.
 * <p>有关{@link OrderComparator}的信息，请查阅Javadoc，以获取有关非排序对象的排序语义的详细信息.
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 07.04.2003
 * @see PriorityOrdered
 * @see OrderComparator
 * @see org.springframework.core.annotation.Order
 * @see org.springframework.core.annotation.AnnotationAwareOrderComparator
 */
public interface Ordered {

	/**
	 * 最高优先级值的有用常数.
	 * @see java.lang.Integer#MIN_VALUE
	 */
	int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

	/**
	 * 最低优先级值的有用常数.
	 * @see java.lang.Integer#MAX_VALUE
	 */
	int LOWEST_PRECEDENCE = Integer.MAX_VALUE;


	/**
	 * 获取此对象的顺序值.
	 * <p>较高的值将解释为较低的优先级.
	 * 结果，具有最低值的对象具有最高优先级（与Servlet加载时{@code load-on-startup}值类似）.
	 * <p>相同的顺序值将导致受影响对象的任意排序位置.
	 * @return the order value
	 * @see #HIGHEST_PRECEDENCE
	 * @see #LOWEST_PRECEDENCE
	 */
	int getOrder();

}
