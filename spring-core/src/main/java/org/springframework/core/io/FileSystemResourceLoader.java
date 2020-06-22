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

/**
 * {@link ResourceLoader}实现，将纯路径解析为文件系统资源，
 * 而不是类路径资源（后者是{@link DefaultResourceLoader}的默认策略）.
 *
 * <p>注意：即使纯路径以斜杠开头，它们也始终会被解释为相对于当前VM工作目录.
 * （这与Servlet容器中的语义一致.）使用显式的"file:"前缀来强制执行绝对文件路径.
 *
 * <p>{@link org.springframework.context.support.FileSystemXmlApplicationContext}
 * 是成熟的ApplicationContext实现，提供了相同的资源路径解析策略.
 *
 * @author Juergen Hoeller
 * @since 1.1.3
 * @see DefaultResourceLoader
 * @see org.springframework.context.support.FileSystemXmlApplicationContext
 */
public class FileSystemResourceLoader extends DefaultResourceLoader {

	/**
	 * 将资源路径解析为文件系统路径.
	 * <p>注意：即使给定路径以斜杠开头，它也将被解释为相对于当前VM工作目录的相对路径.
	 * @param path the path to the resource
	 * @return the corresponding Resource handle
	 * @see FileSystemResource
	 * @see org.springframework.web.context.support.ServletContextResourceLoader#getResourceByPath
	 */
	@Override
	protected Resource getResourceByPath(String path) {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		return new FileSystemContextResource(path);
	}


	/**
	 * FileSystemResource通过实现ContextResource接口显式表示一个上下文相对路径.
	 */
	private static class FileSystemContextResource extends FileSystemResource implements ContextResource {

		public FileSystemContextResource(String path) {
			super(path);
		}

		@Override
		public String getPathWithinContext() {
			return getPath();
		}
	}

}
