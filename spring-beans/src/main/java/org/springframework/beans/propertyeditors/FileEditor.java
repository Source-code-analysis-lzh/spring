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

package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.io.File;
import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/**
 * {@code java.io.File}的编辑器，用于直接从Spring资源位置填充File属性.
 *
 * <p>支持Spring样式的URL表示法：任何完全限定的标准URL("file:", "http:", etc)
 * 和Spring的特有"classpath:"伪URL.
 *
 * <p>注意：此编辑器的行为在Spring 2.0中已更改.
 * 以前，它直接从文件名创建File实例. 从Spring 2.0开始，它以标准Spring资源位置作为输入.
 * 这与URLEditor和InputStreamEditor现在一致.
 *
 * <p>注意：在Spring 2.5中，进行了以下修改. 如果指定的文件名没有URL前缀或绝对路径，
 * 则我们尝试使用标准ResourceLoader语义查找文件.
 * 如果未找到文件，则假定文件名引用相对文件位置，则创建一个File实例.
 *
 * @author Juergen Hoeller
 * @author Thomas Risberg
 * @since 09.12.2003
 * @see java.io.File
 * @see org.springframework.core.io.ResourceEditor
 * @see org.springframework.core.io.ResourceLoader
 * @see URLEditor
 * @see InputStreamEditor
 */
public class FileEditor extends PropertyEditorSupport {

	private final ResourceEditor resourceEditor;


	/**
	 * Create a new FileEditor, using a default ResourceEditor underneath.
	 */
	public FileEditor() {
		this.resourceEditor = new ResourceEditor();
	}

	/**
	 * Create a new FileEditor, using the given ResourceEditor underneath.
	 * @param resourceEditor the ResourceEditor to use
	 */
	public FileEditor(ResourceEditor resourceEditor) {
		Assert.notNull(resourceEditor, "ResourceEditor must not be null");
		this.resourceEditor = resourceEditor;
	}


	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (!StringUtils.hasText(text)) {
			setValue(null);
			return;
		}

		// Check whether we got an absolute file path without "file:" prefix.
		// For backwards compatibility, we'll consider those as straight file path.
		File file = null;
		if (!ResourceUtils.isUrl(text)) {
			file = new File(text);
			if (file.isAbsolute()) {
				setValue(file);
				return;
			}
		}

		// Proceed with standard resource location parsing.
		this.resourceEditor.setAsText(text);
		Resource resource = (Resource) this.resourceEditor.getValue();

		// If it's a URL or a path pointing to an existing resource, use it as-is.
		if (file == null || resource.exists()) {
			try {
				setValue(resource.getFile());
			}
			catch (IOException ex) {
				throw new IllegalArgumentException(
						"Could not retrieve file for " + resource + ": " + ex.getMessage());
			}
		}
		else {
			// Set a relative File reference and hope for the best.
			setValue(file);
		}
	}

	@Override
	public String getAsText() {
		File value = (File) getValue();
		return (value != null ? value.getPath() : "");
	}

}
