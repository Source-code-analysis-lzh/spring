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

package org.springframework.aop.target;

import java.io.Serializable;

import org.springframework.aop.TargetSource;
import org.springframework.util.Assert;

/**
 * {@link org.springframework.aop.TargetSource}实现缓存本地目标对象，但允许在应用程序运行时交换目标。
 *
 * <p>如果在Spring IoC容器中配置此类的对象，请使用构造函数注入。
 *
 * <p>如果目标可序列化时，则该TargetSource是可序列化的。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public class HotSwappableTargetSource implements TargetSource, Serializable {

	/** use serialVersionUID from Spring 1.2 for interoperability. */
	private static final long serialVersionUID = 7497929212653839187L;


	/** The current target object. */
	private Object target;


	/**
	 * Create a new HotSwappableTargetSource with the given initial target object.
	 * @param initialTarget the initial target object
	 */
	public HotSwappableTargetSource(Object initialTarget) {
		Assert.notNull(initialTarget, "Target object must not be null");
		this.target = initialTarget;
	}


	/**
	 * Return the type of the current target object.
	 * <p>The returned type should usually be constant across all target objects.
	 */
	@Override
	public synchronized Class<?> getTargetClass() {
		return this.target.getClass();
	}

	@Override
	public final boolean isStatic() {
		return false;
	}

	@Override
	public synchronized Object getTarget() {
		return this.target;
	}

	@Override
	public void releaseTarget(Object target) {
		// nothing to do
	}


	/**
	 * Swap the target, returning the old target object.
	 * @param newTarget the new target object
	 * @return the old target object
	 * @throws IllegalArgumentException if the new target is invalid
	 */
	public synchronized Object swap(Object newTarget) throws IllegalArgumentException {
		Assert.notNull(newTarget, "Target object must not be null");
		Object old = this.target;
		this.target = newTarget;
		return old;
	}


	/**
	 * Two HotSwappableTargetSources are equal if the current target
	 * objects are equal.
	 */
	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof HotSwappableTargetSource &&
				this.target.equals(((HotSwappableTargetSource) other).target)));
	}

	@Override
	public int hashCode() {
		return HotSwappableTargetSource.class.hashCode();
	}

	@Override
	public String toString() {
		return "HotSwappableTargetSource for target: " + this.target;
	}

}
