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

package org.aopalliance.intercept;

import java.lang.reflect.AccessibleObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 该接口表示通用的运行时连接点（在AOP术语中）。
 *
 * <p>运行时连接点是在静态连接点（即程序中的位置）上发生的事件。 
 * 例如，invocation是方法上的运行时连接点（静态连接点）。 
 * 可以使用{@link #getStaticPart()}方法来一般检索给定连接点的静态部分。
 *
 * <p>然后在拦截框架的上下文中，运行时连接点是对可访问对象（方法，构造函数，字段）
 * （即连接点的静态部分）的访问的验证。 它被传递到安装在静态连接点上的拦截器。
 *
 * @author Rod Johnson
 * @see Interceptor
 */
public interface Joinpoint {

	/**
	 * Proceed to the next interceptor in the chain.
	 * <p>The implementation and the semantics of this method depends
	 * on the actual joinpoint type (see the children interfaces).
	 * @return see the children interfaces' proceed definition
	 * @throws Throwable if the joinpoint throws an exception
	 */
	@Nullable
	Object proceed() throws Throwable;

	/**
	 * Return the object that holds the current joinpoint's static part.
	 * <p>For instance, the target object for an invocation.
	 * @return the object (can be null if the accessible object is static)
	 */
	@Nullable
	Object getThis();

	/**
	 * Return the static part of this joinpoint.
	 * <p>The static part is an accessible object on which a chain of
	 * interceptors are installed.
	 */
	@Nonnull
	AccessibleObject getStaticPart();

}
