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

package org.springframework.core.io.buffer;

/**
 * {@link DataBuffer}的扩展，它允许共享内存池的缓冲区. 引入引用计数的方法.
 *
 * @author Arjen Poutsma
 * @since 5.0
 */
public interface PooledDataBuffer extends DataBuffer {

	/**
	 * 如果分配了此缓冲区，则返回{@code true}；否则，如果已被释放，则返回{@code false}.
	 * @since 5.1
	 */
	boolean isAllocated();

	/**
	 * 该缓冲区的引用计数增加一.
	 * @return this buffer
	 */
	PooledDataBuffer retain();

	/**
	 * Decrease the reference count for this buffer by one,
	 * and deallocate it once the count reaches zero.
	 * 将该缓冲区的引用计数减少1，并在计数达到零后将其释放回池中.
	 * @return {@code true} if the buffer was deallocated;
	 * {@code false} otherwise
	 */
	boolean release();

}
