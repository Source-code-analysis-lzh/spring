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

package org.springframework.core.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.springframework.lang.Nullable;
import org.springframework.util.ResourceUtils;

/**
 * {@link UrlResource}的子类，该子类假定已实现文件解析，
 * 并且为此实现了{@link WritableResource}接口.
 * 此资源变体还从{@link #getFile()}缓存已解析的{@link File}句柄.
 *
 * <p>这是{@link DefaultResourceLoader}为"file:..." URL位置解析的类，
 * 从而允许将其向下转换为{@link WritableResource}.
 *
 * <p>或者，要从{@link java.io.File}句柄或NIO{@link java.nio.file.Path}直接构建，
 * 请考虑使用{@link FileSystemResource}.
 *
 * @author Juergen Hoeller
 * @since 5.0.2
 */
public class FileUrlResource extends UrlResource implements WritableResource {

	@Nullable
	private volatile File file;


	/**
	 * Create a new {@code FileUrlResource} based on the given URL object.
	 * <p>Note that this does not enforce "file" as URL protocol. If a protocol
	 * is known to be resolvable to a file,
	 * @param url a URL
	 * @see ResourceUtils#isFileURL(URL)
	 * @see #getFile()
	 */
	public FileUrlResource(URL url) {
		super(url);
	}

	/**
	 * Create a new {@code FileUrlResource} based on the given file location,
	 * using the URL protocol "file".
	 * <p>The given parts will automatically get encoded if necessary.
	 * @param location the location (i.e. the file path within that protocol)
	 * @throws MalformedURLException if the given URL specification is not valid
	 * @see UrlResource#UrlResource(String, String)
	 * @see ResourceUtils#URL_PROTOCOL_FILE
	 */
	public FileUrlResource(String location) throws MalformedURLException {
		super(ResourceUtils.URL_PROTOCOL_FILE, location);
	}


	@Override
	public File getFile() throws IOException {
		File file = this.file;
		if (file != null) {
			return file;
		}
		file = super.getFile();
		this.file = file;
		return file;
	}

	@Override
	public boolean isWritable() {
		try {
			URL url = getURL();
			if (ResourceUtils.isFileURL(url)) {
				// Proceed with file system resolution
				File file = getFile();
				return (file.canWrite() && !file.isDirectory());
			}
			else {
				return true;
			}
		}
		catch (IOException ex) {
			return false;
		}
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return Files.newOutputStream(getFile().toPath());
	}

	@Override
	public WritableByteChannel writableChannel() throws IOException {
		return FileChannel.open(getFile().toPath(), StandardOpenOption.WRITE);
	}

	@Override
	public Resource createRelative(String relativePath) throws MalformedURLException {
		return new FileUrlResource(createRelativeURL(relativePath));
	}

}
