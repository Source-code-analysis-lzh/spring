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

package org.springframework.core.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

/**
 * 支持写入资源的扩展接口. 提供一个{@link #getOutputStream() OutputStream accessor}访问器.
 *
 * @author Juergen Hoeller
 * @since 3.1
 * @see java.io.OutputStream
 */
public interface WritableResource extends Resource {

	/**
	 * 指示是否可以通过{@link #getOutputStream()}编写此资源的内容.
	 * <p>对于典型的资源描述符将返回{@code true}；
	 * 请注意，尝试进行实际的内容写入仍可能会失败. 但是，值为{@code false}表示无法修改资源内容.
	 * @see #getOutputStream()
	 * @see #isReadable()
	 */
	default boolean isWritable() {
		return true;
	}

	/**
	 * 返回一个底层资源的{@link OutputStream}，允许覆盖它的内容.
	 * @throws IOException if the stream could not be opened
	 * @see #getInputStream()
	 */
	OutputStream getOutputStream() throws IOException;

	/**
	 * 返回一个{@link WritableByteChannel}.
	 * <p>期望每个调用都会创建一个新的频道.
	 * <p>默认实现返回带有{@link #getOutputStream()}的{@link Channels#newChannel(OutputStream)}结果.
	 * @return the byte channel for the underlying resource (must not be {@code null})
	 * @throws java.io.FileNotFoundException if the underlying resource doesn't exist
	 * @throws IOException if the content channel could not be opened
	 * @since 5.0
	 * @see #getOutputStream()
	 */
	default WritableByteChannel writableChannel() throws IOException {
		return Channels.newChannel(getOutputStream());
	}

}
