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

package org.springframework.beans.factory;

/**
 * {@link FactoryBean}接口的扩展。 实现可能会指示它们是否始终返回独立实例，
 * 因为其{@link #isSingleton()}实现返回{@code false}并不能清楚地指示独立实例。
 *
 * <p>如果未实现此扩展接口的普通{@link FactoryBean}实现被假定为始终返回独立的实例，
 * 则其{@link #isSingleton()}实现返回{@code false}； 暴露的对象仅按需访问。
 *
 * <p>注意：这个接口是一个有特殊用途的接口，主要用于框架内部使用与Spring相关。
 * 通常，应用提供的{@link FactoryBean}接口实现应当只需要实现简单的{@link FactoryBean}接口即可，
 * 新方法应当加入到扩展接口中去
 *
 * @author Juergen Hoeller
 * @since 2.0.3
 * @param <T> the bean type
 * @see #isPrototype()
 * @see #isSingleton()
 */
public interface SmartFactoryBean<T> extends FactoryBean<T> {

	/**
	 * 该工厂管理的对象是否是原型？ 也就是说，{@link #getObject()}是否总是返回一个独立的实例？
	 * <p>FactoryBean本身的原型状态通常由拥有的{@link BeanFactory}提供； 
	 * 通常，它必须在那里定义为单例。
	 * <p>该方法应严格检查独立实例； 对于范围对象或其它类型的非单例，非独立对象，
	 * 它不应返回{@code true}。 因此，这不只是{@link #isSingleton()}的反转形式。
	 * <p>The default implementation returns {@code false}.
	 * @return whether the exposed object is a prototype
	 * @see #getObject()
	 * @see #isSingleton()
	 */
	default boolean isPrototype() {
		return false;
	}

	/**
	 * 这个FactoryBean是否期望进行热切的初始化，
	 * 即热切地初始化自身以及期望对其单例对象（如果有）进行热切的初始化？
	 * <p>不能期望标准的FactoryBean急于初始化：即使是单例对象，
	 * 也只会为实际访问而调用其{@link #getObject()}。 
	 * 从此方法返回true意味着应积极调用{@link #getObject()}，同时也应积极地应用后处理器。
	 * 对于{@link #isSingleton() singleton}对象，这可能是有道理的，特
	 * 别是如果后处理器希望在启动时应用。
	 * <p>The default implementation returns {@code false}.
	 * @return whether eager initialization applies
	 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory#preInstantiateSingletons()
	 */
	default boolean isEagerInit() {
		return false;
	}

}
