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

package org.springframework.validation.support;

import java.util.Map;

import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BindingResult;

/**
 * Subclass of {@link org.springframework.ui.ExtendedModelMap} that automatically removes
 * a {@link org.springframework.validation.BindingResult} object if the corresponding
 * target attribute gets replaced through regular {@link Map} operations.
 * 如果通过常规{@link Map}操作替换了相应的target属性，则ExtendedModelMap的子类将自动删除BindingResult对象。
 *
 * <p>这是Spring MVC暴露给处理器方法的类，通常通过{@link org.springframework.ui.Model}
 * 接口的声明使用。 无需在用户代码中构建它； 一个普通的{@link org.springframework.ui.ModelMap}
 * 甚至只是一个带有String键的常规{@link Map}都足以返回用户该模型。
 *
 * @author Juergen Hoeller
 * @since 2.5.6
 * @see org.springframework.validation.BindingResult
 */
@SuppressWarnings("serial")
public class BindingAwareModelMap extends ExtendedModelMap {

	@Override
	public Object put(String key, Object value) {
		removeBindingResultIfNecessary(key, value);
		return super.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ?> map) {
		map.forEach(this::removeBindingResultIfNecessary);
		super.putAll(map);
	}

	private void removeBindingResultIfNecessary(Object key, Object value) {
		if (key instanceof String) {
			String attributeName = (String) key;
			if (!attributeName.startsWith(BindingResult.MODEL_KEY_PREFIX)) {
				String bindingResultKey = BindingResult.MODEL_KEY_PREFIX + attributeName;
				BindingResult bindingResult = (BindingResult) get(bindingResultKey);
				if (bindingResult != null && bindingResult.getTarget() != value) {
					remove(bindingResultKey);
				}
			}
		}
	}

}
