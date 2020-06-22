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

package org.springframework.web.bind.support;

import org.springframework.lang.Nullable;
import org.springframework.web.context.request.WebRequest;

/**
 * 策略接口，用于在后端会话中存储模型属性。
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see org.springframework.web.bind.annotation.SessionAttributes
 */
public interface SessionAttributeStore {

	/**
	 * 将提供的属性存储在后端会话中。
	 * <p>可以为新属性以及现有属性调用。 在后一种情况下，这表明属性值可能已被修改。
	 * @param request the current request
	 * @param attributeName the name of the attribute
	 * @param attributeValue the attribute value to store
	 */
	void storeAttribute(WebRequest request, String attributeName, Object attributeValue);

	/**
	 * 从后端会话中检索指定的属性。
	 * <p>This will typically be called with the expectation that the
	 * attribute is already present, with an exception to be thrown
	 * if this method returns {@code null}.
	 * <p>通常会在期望属性已经存在的情况下调用此方法，如果此方法返回{@code null}，则会抛出异常。
	 * @param request the current request
	 * @param attributeName the name of the attribute
	 * @return the current attribute value, or {@code null} if none
	 */
	@Nullable
	Object retrieveAttribute(WebRequest request, String attributeName);

	/**
	 * 清理后端会话中的指定属性。
	 * <p>指示将不再使用属性名称。
	 * @param request the current request
	 * @param attributeName the name of the attribute
	 */
	void cleanupAttribute(WebRequest request, String attributeName);

}
