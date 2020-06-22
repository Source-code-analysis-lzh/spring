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

import java.lang.reflect.Method;

/**
 * {@link Pointcut}的一部分：检查目标方法是否符合advice。
 *
 * <p>MethodMatcher可以静态计算，也可以在运行时（动态）计算。 
 * 静态匹配涉及方法和（可能）方法属性。 动态匹配还使特定调用的参数可用，以及运行先前advice应用于连接点的任何效果。
 *
 * <p>如果实现从其{@link #isRuntime()}方法返回{@code false}，则可以静态执行计算，
 * 并且此方法的所有调用（无论其参数如何）的结果都将相同。 这意味着，如果{@link #isRuntime()}方法返回{@code false}，
 * 则将永远不会调用3个参数的{@link #matches(java.lang.reflect.Method, Class, Object[])}方法。
 *
 * <p>如果实现从其2个参数的{@link #matches(java.lang.reflect.Method, Class)}方法返回true，
 * 且其{@link #isRuntime()}方法返回{@code true}，则3个参数的{@link #matches(java.lang.reflect.Method, Class, Object[])}
 * 方法将在每次可能执行相关advice之前立即调用，以确定是否应运行该建议。 
 * 所有先前的advice（例如，拦截器链中较早的拦截器）都将运行，因此在计算时可以使用它们在参数或ThreadLocal状态中产生的任何状态更改。
 *
 * <p>此接口的具体实现通常应提供{@link Object#equals(Object)}和{@link Object#hashCode()}的正确实现，
 * 以便允许将匹配器用于缓存方案中，例如在CGLIB生成的代理中。
 *
 * @author Rod Johnson
 * @since 11.11.2003
 * @see Pointcut
 * @see ClassFilter
 */
public interface MethodMatcher {

	/**
	 * Perform static checking whether the given method matches.
	 * <p>If this returns {@code false} or if the {@link #isRuntime()}
	 * method returns {@code false}, no runtime check (i.e. no
	 * {@link #matches(java.lang.reflect.Method, Class, Object[])} call)
	 * will be made.
	 * @param method the candidate method
	 * @param targetClass the target class
	 * @return whether or not this method matches statically
	 */
	boolean matches(Method method, Class<?> targetClass);

	/**
	 * Is this MethodMatcher dynamic, that is, must a final call be made on the
	 * {@link #matches(java.lang.reflect.Method, Class, Object[])} method at
	 * runtime even if the 2-arg matches method returns {@code true}?
	 * <p>Can be invoked when an AOP proxy is created, and need not be invoked
	 * again before each method invocation,
	 * @return whether or not a runtime match via the 3-arg
	 * {@link #matches(java.lang.reflect.Method, Class, Object[])} method
	 * is required if static matching passed
	 */
	boolean isRuntime();

	/**
	 * Check whether there a runtime (dynamic) match for this method,
	 * which must have matched statically.
	 * <p>This method is invoked only if the 2-arg matches method returns
	 * {@code true} for the given method and target class, and if the
	 * {@link #isRuntime()} method returns {@code true}. Invoked
	 * immediately before potential running of the advice, after any
	 * advice earlier in the advice chain has run.
	 * @param method the candidate method
	 * @param targetClass the target class
	 * @param args arguments to the method
	 * @return whether there's a runtime match
	 * @see MethodMatcher#matches(Method, Class)
	 */
	boolean matches(Method method, Class<?> targetClass, Object... args);


	/**
	 * 符合所有方法的规范实例。使用它，会对所有检测方法返回true
	 */
	MethodMatcher TRUE = TrueMethodMatcher.INSTANCE;

}
