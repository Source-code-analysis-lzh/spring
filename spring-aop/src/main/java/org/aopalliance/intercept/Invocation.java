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

import javax.annotation.Nonnull;

/**
 * 该接口表示程序中的调用。
 *
 * <p>调用是连接点，并且可以被拦截器拦截。
 *
 * @author Rod Johnson
 */
public interface Invocation extends Joinpoint {

	/**
	 * 将参数作为数组对象获取。
	 * 可以更改此数组中的元素值以更改参数。
	 * @return the argument of the invocation
	 */
	@Nonnull
	Object[] getArguments();

}
