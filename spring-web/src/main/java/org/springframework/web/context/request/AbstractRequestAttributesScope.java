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

package org.springframework.web.context.request;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.lang.Nullable;

/**
 * 从当前线程绑定的{@link RequestAttributes}对象中的特定范围读取的抽象{@link Scope}实现。
 *
 * <p>子类只需要实现{@link #getScope()}即可指示此类从哪个{@link RequestAttributes}范围读取属性。
 *
 * <p>Subclasses may wish to override the {@link #get} and {@link #remove}
 * methods to add synchronization around the call back into this super class.
 * <p>子类可能希望覆盖{@link #get}和{@link #remove}方法，以将同步块包含回调添加到此超类中。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 2.0
 */
public abstract class AbstractRequestAttributesScope implements Scope {

	@Override
	public Object get(String name, ObjectFactory<?> objectFactory) {
		RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
		Object scopedObject = attributes.getAttribute(name, getScope());
		if (scopedObject == null) {
			scopedObject = objectFactory.getObject();
			attributes.setAttribute(name, scopedObject, getScope());
			// Retrieve object again, registering it for implicit session attribute updates.
			// As a bonus, we also allow for potential decoration at the getAttribute level.
			Object retrievedObject = attributes.getAttribute(name, getScope());
			if (retrievedObject != null) {
				// Only proceed with retrieved object if still present (the expected case).
				// If it disappeared concurrently, we return our locally created instance.
				scopedObject = retrievedObject;
			}
		}
		return scopedObject;
	}

	@Override
	@Nullable
	public Object remove(String name) {
		RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
		Object scopedObject = attributes.getAttribute(name, getScope());
		if (scopedObject != null) {
			attributes.removeAttribute(name, getScope());
			return scopedObject;
		}
		else {
			return null;
		}
	}

	@Override
	public void registerDestructionCallback(String name, Runnable callback) {
		RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
		attributes.registerDestructionCallback(name, callback, getScope());
	}

	@Override
	@Nullable
	public Object resolveContextualObject(String key) {
		RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
		return attributes.resolveReference(key);
	}


	/**
	 * Template method that determines the actual target scope.
	 * @return the target scope, in the form of an appropriate
	 * {@link RequestAttributes} constant
	 * @see RequestAttributes#SCOPE_REQUEST
	 * @see RequestAttributes#SCOPE_SESSION
	 */
	protected abstract int getScope();

}
