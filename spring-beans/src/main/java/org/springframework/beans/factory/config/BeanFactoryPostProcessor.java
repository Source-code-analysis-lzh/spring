/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;

/**
 * 工厂钩子允许对应用程序上下文的bean定义进行自定义修改，以适应上下文底层bean工厂的bean属性值.
 *
 * <p>对于针对系统管理员的自定义配置文件很有用，这些文件覆盖了在应用程序上下文中配置的Bean属性.
 * 请参阅{@link PropertyResourceConfigurer}及其具体实现，以获取可解决此类配置需求的即用型解决方案.
 *
 * <p>{@code BeanFactoryPostProcessor}可以与Bean定义进行交互并对其进行修改，
 * 但不能与Bean实例进行交互. 这样做可能会导致bean实例化过早，从而违反了容器并造成了意外的副作用.
 * 如果需要与bean实例交互，请考虑实现{@link BeanPostProcessor}.
 *
 * <h3>注册</h3>
 * <p>{@code ApplicationContext}在其Bean定义中自动检测{@code BeanFactoryPostProcessor} Bean，
 * 并在创建任何其他Bean之前应用它们.
 * {@code BeanFactoryPostProcessor}也可以通过编程方式注册到{@code ConfigurableApplicationContext}.
 *
 * <h3>排序</h3>
 * <p>在{@code ApplicationContext}中自动检测到的{@code BeanFactoryPostProcessor} Bean
 * 将根据{@link org.springframework.core.PriorityOrdered}和{@link org.springframework.core.Ordered}语义进行排序.
 * 相反，通过{@code ConfigurableApplicationContext}以编程方式注册的{@code BeanFactoryPostProcessor} Bean将按注册顺序应用；
 * 以编程方式注册的后处理器将忽略通过实现{@code PriorityOrdered}或{@code Ordered}接口表示的任何排序语义.
 * 此外，{@code BeanFactoryPostProcessor} bean不考虑{@link org.springframework.core.annotation.Order @Order}批注.
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 06.07.2003
 * @see BeanPostProcessor
 * @see PropertyResourceConfigurer
 */
@FunctionalInterface
public interface BeanFactoryPostProcessor {

	/**
	 * bean工厂初始化后，修改应用程序上下文的内部bean工厂.
	 * 所有bean定义都将被加载，但尚未实例化任何bean. 这甚至可以覆盖或添加属性，甚至可以用于早期初始化bean.
	 * @param beanFactory the bean factory used by the application context
	 * @throws org.springframework.beans.BeansException in case of errors
	 */
	void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;

}
