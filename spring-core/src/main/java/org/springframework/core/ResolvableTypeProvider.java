/*
 * Copyright 2002-2017 the original author or authors.
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

import org.springframework.lang.Nullable;

/**
 * 任何对象都可以实现此接口以提供其实际的{@link ResolvableType}.
 *
 * <p>当确定实例是否与泛型签名匹配时，此类信息非常有用，因为Java在运行时不会传递签名.
 *
 * <p>使用此接口的用户在复杂的层次结构方案中应格外小心，尤其是当类的泛型类型签名在子类中更改时.
 * 始终可以在默认行为上回退返回{@code null}.
 *
 * @author Stephane Nicoll
 * @since 4.2
 */
public interface ResolvableTypeProvider {

	/**
	 * 返回描述此实例的{@link ResolvableType}（如果应采用某种默认值，则返回{@code null}）.
	 */
	@Nullable
	ResolvableType getResolvableType();

}
