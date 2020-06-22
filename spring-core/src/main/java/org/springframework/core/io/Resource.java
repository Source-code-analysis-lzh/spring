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

package org.springframework.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.springframework.lang.Nullable;

/**
 * 资源描述符的接口，它从底层资源的实际类型中抽象出来，例如文件或类路径资源.
 *
 * <p>如果InputStream以物理形式存在，则可以为每个资源打开InputStream，
 * 但是仅可以为某些资源返回URL或File句柄. 实际行为是特定于具体实现的.
 *
 * @author Juergen Hoeller
 * @since 28.12.2003
 * @see #getInputStream()
 * @see #getURL()
 * @see #getURI()
 * @see #getFile()
 * @see WritableResource
 * @see ContextResource
 * @see UrlResource
 * @see FileUrlResource
 * @see FileSystemResource
 * @see ClassPathResource
 * @see ByteArrayResource
 * @see InputStreamResource
 */
public interface Resource extends InputStreamSource {

	/**
	 * 确定此资源是否实际以物理形式存在.
	 * <p>此方法执行确定的存在性检查，而{@code Resource}句柄的存在仅保证有效的描述符句柄.
	 */
	boolean exists();

	/**
	 * 指示是否可以通过{@link #getInputStream()}读取此资源的非空内容.
	 * <p>对于存在的典型资源描述符将是正确的，因为从5.1开始它严格隐含了{@link #exists()}语义.
	 * 请注意，尝试进行实际的内容读取仍可能会失败. 但是，值为{@code false}表示无法读取资源内容.
	 * @see #getInputStream()
	 * @see #exists()
	 */
	default boolean isReadable() {
		return exists();
	}

	/**
	 * 指示此资源是否代表具有打开流的句柄. 如果为{@code true}，
	 * 则不能多次读取InputStream，必须对其进行读取和关闭，以避免资源泄漏.
	 * <p>对于典型的资源描述符将为{@code false}.
	 */
	default boolean isOpen() {
		return false;
	}

	/**
	 * 确定此资源是否代表文件系统中的文件. 值为{@code true}强烈建议（但不能保证）
	 * {@link #getFile()}调用将成功.
	 * <p>默认情况下，将返回{@code false}.
	 * @since 5.0
	 * @see #getFile()
	 */
	default boolean isFile() {
		return false;
	}

	/**
	 * 返回此资源的URL句柄.
	 * @throws IOException if the resource cannot be resolved as URL,
	 * i.e. if the resource is not available as descriptor
	 */
	URL getURL() throws IOException;

	/**
	 * 返回此资源的URI句柄.
	 * @throws IOException if the resource cannot be resolved as URI,
	 * i.e. if the resource is not available as descriptor
	 * @since 2.5
	 */
	URI getURI() throws IOException;

	/**
	 * 返回此资源的文件句柄.
	 * @throws java.io.FileNotFoundException if the resource cannot be resolved as
	 * absolute file path, i.e. if the resource is not available in a file system
	 * @throws IOException in case of general resolution/reading failures
	 * @see #getInputStream()
	 */
	File getFile() throws IOException;

	/**
	 * 返回一个{@link ReadableByteChannel}.
	 * <p>期望每次调用都会创建一个新的管道.
	 * <p>默认实现返回使用{@link #getInputStream()}的{@link Channels#newChannel(InputStream)}结果.
	 * @return the byte channel for the underlying resource (must not be {@code null})
	 * @throws java.io.FileNotFoundException if the underlying resource doesn't exist
	 * @throws IOException if the content channel could not be opened
	 * @since 5.0
	 * @see #getInputStream()
	 */
	default ReadableByteChannel readableChannel() throws IOException {
		return Channels.newChannel(getInputStream());
	}

	/**
	 * 确定此资源的内容长度.
	 * @throws IOException if the resource cannot be resolved
	 * (in the file system or as some other known physical resource type)
	 */
	long contentLength() throws IOException;

	/**
	 * 确定此资源的最后修改的时间戳.
	 * @throws IOException if the resource cannot be resolved
	 * (in the file system or as some other known physical resource type)
	 */
	long lastModified() throws IOException;

	/**
	 * 创建相对于该资源的资源.
	 * @param relativePath the relative path (relative to this resource)
	 * @return the resource handle for the relative resource
	 * @throws IOException if the relative resource cannot be determined
	 */
	Resource createRelative(String relativePath) throws IOException;

	/**
	 * 确定此资源的文件名，即通常路径的最后一部分：例如"myfile.txt".
	 * <p>如果此类型的资源没有文件名，则返回{@code null}.
	 */
	@Nullable
	String getFilename();

	/**
	 * 返回对此资源的描述，以便在使用该资源时用于错误输出.
	 * <p>还鼓励实现从其{@code toString}方法返回此值.
	 * @see Object#toString()
	 */
	String getDescription();

}
