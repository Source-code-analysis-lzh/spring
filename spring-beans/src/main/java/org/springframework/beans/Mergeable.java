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

package org.springframework.beans;

import org.springframework.lang.Nullable;

/**
 * 表示对象的接口，其值集可以与父对象的值集合并。
 *
 * @author Rob Harrop
 * @since 2.0
 * @see org.springframework.beans.factory.support.ManagedSet
 * @see org.springframework.beans.factory.support.ManagedList
 * @see org.springframework.beans.factory.support.ManagedMap
 * @see org.springframework.beans.factory.support.ManagedProperties
 */
public interface Mergeable {

	/**
	 * Is merging enabled for this particular instance?
	 */
	boolean isMergeEnabled();

	/**
	 * 将当前设置的值与提供的对象的设置值合并。
	 * <p>提供的对象被视为父对象，并且被调用者的值集中的值必须覆盖提供的对象的值。
	 * @param parent the object to merge with
	 * @return the result of the merge operation
	 * @throws IllegalArgumentException if the supplied parent is {@code null}
	 * @throws IllegalStateException if merging is not enabled for this instance
	 * (i.e. {@code mergeEnabled} equals {@code false}).
	 */
	Object merge(@Nullable Object parent);

}
