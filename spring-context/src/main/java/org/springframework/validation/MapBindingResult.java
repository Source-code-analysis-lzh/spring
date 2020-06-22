/*
 * Copyright 2002-2006 the original author or authors.
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

package org.springframework.validation;

import java.io.Serializable;
import java.util.Map;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * BindingResult接口的基于Map的实现，支持注册和计算Map属性上的绑定错误.
 *
 * <p>Can be used as errors holder for custom binding onto a
 * Map, for example when invoking a Validator for a Map object.
 * <p>可以用作错误绑定，用于自定义绑定到Map，例如，当为Map对象调用Validator时.
 *
 * @author Juergen Hoeller
 * @since 2.0
 * @see java.util.Map
 */
@SuppressWarnings("serial")
public class MapBindingResult extends AbstractBindingResult implements Serializable {

	private final Map<?, ?> target;


	/**
	 * Create a new MapBindingResult instance.
	 * @param target the target Map to bind onto
	 * @param objectName the name of the target object
	 */
	public MapBindingResult(Map<?, ?> target, String objectName) {
		super(objectName);
		Assert.notNull(target, "Target Map must not be null");
		this.target = target;
	}


	public final Map<?, ?> getTargetMap() {
		return this.target;
	}

	@Override
	public final Object getTarget() {
		return this.target;
	}

	@Override
	@Nullable
	protected Object getActualFieldValue(String field) {
		return this.target.get(field);
	}

}
