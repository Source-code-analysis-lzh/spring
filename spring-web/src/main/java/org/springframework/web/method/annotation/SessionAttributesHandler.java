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

package org.springframework.web.method.annotation;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionAttributeStore;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.WebRequest;

/**
 * 管理通过{@link SessionAttributes @SessionAttributes}声明的特定的控制器的会话属性。 
 * 实际的存储委托给{@link SessionAttributeStore}实例。
 *
 * <p>当使用{@code @SessionAttributes}注释的控制器向其模型添加属性时，
 * 将根据通过{@code @SessionAttributes}指定的名称和类型检查这些属性。 
 * 匹配的模型属性保存在HTTP会话中，并保持在那里直到该控制器调用{@link SessionStatus#setComplete()}为止。
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.1
 */
public class SessionAttributesHandler {

	private final Set<String> attributeNames = new HashSet<>();

	private final Set<Class<?>> attributeTypes = new HashSet<>();

	private final Set<String> knownAttributeNames = Collections.newSetFromMap(new ConcurrentHashMap<>(4));

	private final SessionAttributeStore sessionAttributeStore;


	/**
	 * Create a new session attributes handler. Session attribute names and types
	 * are extracted from the {@code @SessionAttributes} annotation, if present,
	 * on the given type.
	 * @param handlerType the controller type
	 * @param sessionAttributeStore used for session access
	 */
	public SessionAttributesHandler(Class<?> handlerType, SessionAttributeStore sessionAttributeStore) {
		Assert.notNull(sessionAttributeStore, "SessionAttributeStore may not be null");
		this.sessionAttributeStore = sessionAttributeStore;

		SessionAttributes ann = AnnotatedElementUtils.findMergedAnnotation(handlerType, SessionAttributes.class);
		if (ann != null) {
			Collections.addAll(this.attributeNames, ann.names());
			Collections.addAll(this.attributeTypes, ann.types());
		}
		this.knownAttributeNames.addAll(this.attributeNames);
	}


	/**
	 * Whether the controller represented by this instance has declared any
	 * session attributes through an {@link SessionAttributes} annotation.
	 */
	public boolean hasSessionAttributes() {
		return (!this.attributeNames.isEmpty() || !this.attributeTypes.isEmpty());
	}

	/**
	 * Whether the attribute name or type match the names and types specified
	 * via {@code @SessionAttributes} on the underlying controller.
	 * <p>Attributes successfully resolved through this method are "remembered"
	 * and subsequently used in {@link #retrieveAttributes(WebRequest)} and
	 * {@link #cleanupAttributes(WebRequest)}.
	 * @param attributeName the attribute name to check
	 * @param attributeType the type for the attribute
	 */
	public boolean isHandlerSessionAttribute(String attributeName, Class<?> attributeType) {
		Assert.notNull(attributeName, "Attribute name must not be null");
		if (this.attributeNames.contains(attributeName) || this.attributeTypes.contains(attributeType)) {
			this.knownAttributeNames.add(attributeName);
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * 在会话中存储给定属性的子集。 未通过{@code @SessionAttributes}声明的会话属性将被忽略。
	 * @param request the current request
	 * @param attributes candidate attributes for session storage
	 */
	public void storeAttributes(WebRequest request, Map<String, ?> attributes) {
		attributes.forEach((name, value) -> {
			if (value != null && isHandlerSessionAttribute(name, value.getClass())) {
				this.sessionAttributeStore.storeAttribute(request, name, value);
			}
		});
	}

	/**
	 * Retrieve "known" attributes from the session, i.e. attributes listed
	 * by name in {@code @SessionAttributes} or attributes previously stored
	 * in the model that matched by type.
	 * @param request the current request
	 * @return a map with handler session attributes, possibly empty
	 */
	public Map<String, Object> retrieveAttributes(WebRequest request) {
		Map<String, Object> attributes = new HashMap<>();
		for (String name : this.knownAttributeNames) {
			Object value = this.sessionAttributeStore.retrieveAttribute(request, name);
			if (value != null) {
				attributes.put(name, value);
			}
		}
		return attributes;
	}

	/**
	 * 从会话中删除“已知”属性，即{@code @SessionAttributes}中按名称列出的属性，
	 * 或先前存储在模型中按类型匹配的属性。
	 * @param request the current request
	 */
	public void cleanupAttributes(WebRequest request) {
		for (String attributeName : this.knownAttributeNames) {
			this.sessionAttributeStore.cleanupAttribute(request, attributeName);
		}
	}

	/**
	 * A pass-through call to the underlying {@link SessionAttributeStore}.
	 * @param request the current request
	 * @param attributeName the name of the attribute of interest
	 * @return the attribute value, or {@code null} if none
	 */
	@Nullable
	Object retrieveAttribute(WebRequest request, String attributeName) {
		return this.sessionAttributeStore.retrieveAttribute(request, attributeName);
	}

}
