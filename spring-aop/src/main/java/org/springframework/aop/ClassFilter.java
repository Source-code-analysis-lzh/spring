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

package org.springframework.aop;

/**
 * 限制切入点或引入与给定目标类集匹配的过滤器。
 *
 * <p>可以用作{@link Pointcut}的一部分，也可以用作{@link IntroductionAdvisor}的整个目标。
 *
 * <p>此接口的具体实现通常应提供{@link Object#equals(Object)}和{@link Object#hashCode()}的正确实现，
 * 以便允许在缓存方案中使用该过滤器，例如在CGLIB生成的代理中。
 *
 * @author Rod Johnson
 * @see Pointcut
 * @see MethodMatcher
 */
@FunctionalInterface
public interface ClassFilter {

	/**
	 * 切入点应该应用于给定的接口或目标类吗？
	 * @param clazz the candidate target class
	 * @return whether the advice should apply to the given target class
	 */
	boolean matches(Class<?> clazz);


	/**
	 * 匹配所有类的ClassFilter的规范实例。使用它会对所有类返回true
	 */
	ClassFilter TRUE = TrueClassFilter.INSTANCE;

}
