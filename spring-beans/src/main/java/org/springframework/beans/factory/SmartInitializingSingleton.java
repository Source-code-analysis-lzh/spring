/*
 * Copyright 2002-2014 the original author or authors.
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
 * 在{@link BeanFactory}启动期间的单例预实例化阶段结束时触发的回调接口。 
 * 此接口可以由单例bean实现，以便在常规的单例实例化算法之后执行一些初始化，
 * 避免意外的早期初始化带来的副作用（例如，来自{@link ListableBeanFactory#getBeansOfType}调用）。 
 * 从这个意义上讲，它是{@link InitializingBean}的替代方法，后者在bean的本地构造阶段结束时立即触发。
 *
 * <p>此回调变体与{@link org.springframework.context.event.ContextRefreshedEvent}有点相似，
 * 但不需要实现{@link org.springframework.context.ApplicationListener}，
 * 无需在整个上下文层次结构中过滤上下文引用等。它还意味着对{@code beans}包的依赖性最小，
 * 并且由独立的{@link ListableBeanFactory}实现，而不仅仅是在{@link org.springframework.context.ApplicationContext}环境中。
 *
 * <p><b>NOTE:</b> If you intend to start/manage asynchronous tasks, preferably
 * implement {@link org.springframework.context.Lifecycle} instead which offers
 * a richer model for runtime management and allows for phased startup/shutdown.
 *
 * @author Juergen Hoeller
 * @since 4.1
 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory#preInstantiateSingletons()
 */
public interface SmartInitializingSingleton {

	/**
	 * Invoked right at the end of the singleton pre-instantiation phase,
	 * with a guarantee that all regular singleton beans have been created
	 * already. {@link ListableBeanFactory#getBeansOfType} calls within
	 * this method won't trigger accidental side effects during bootstrap.
	 * <p><b>NOTE:</b> This callback won't be triggered for singleton beans
	 * lazily initialized on demand after {@link BeanFactory} bootstrap,
	 * and not for any other bean scope either. Carefully use it for beans
	 * with the intended bootstrap semantics only.
	 */
	void afterSingletonsInstantiated();

}
