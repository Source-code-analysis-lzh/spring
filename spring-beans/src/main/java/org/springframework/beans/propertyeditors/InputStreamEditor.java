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
import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * 单向PropertyEditor可以从文本String转换为{@code java.io.InputStream}，
 * 将给定的String解释为Spring资源位置（例如URL String）.
 *
 * <p>支持Spring样式的URL表示法：任何完全限定的标准URL("file:", "http:", etc.)
 * 和Spring的特有"classpath:"伪URL.
 *
 * <p>注意，这样的流通常不会被Spring自身关闭！
 *
 * @author Juergen Hoeller
 * @since 1.0.1
 * @see java.io.InputStream
 * @see org.springframework.core.io.ResourceEditor
 * @see org.springframework.core.io.ResourceLoader
 * @see URLEditor
 * @see FileEditor
 */
public class InputStreamEditor extends PropertyEditorSupport {

	private final ResourceEditor resourceEditor;


	/**
	 * Create a new InputStreamEditor, using the default ResourceEditor underneath.
	 */
	public InputStreamEditor() {
		this.resourceEditor = new ResourceEditor();
	}

	/**
	 * Create a new InputStreamEditor, using the given ResourceEditor underneath.
	 * @param resourceEditor the ResourceEditor to use
	 */
	public InputStreamEditor(ResourceEditor resourceEditor) {
		Assert.notNull(resourceEditor, "ResourceEditor must not be null");
		this.resourceEditor = resourceEditor;
	}


	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		this.resourceEditor.setAsText(text);
		Resource resource = (Resource) this.resourceEditor.getValue();
		try {
			setValue(resource != null ? resource.getInputStream() : null);
		}
		catch (IOException ex) {
			throw new IllegalArgumentException("Failed to retrieve InputStream for " + resource, ex);
		}
	}

	/**
	 * This implementation returns {@code null} to indicate that
	 * there is no appropriate text representation.
	 */
	@Override
	@Nullable
	public String getAsText() {
		return null;
	}

}
