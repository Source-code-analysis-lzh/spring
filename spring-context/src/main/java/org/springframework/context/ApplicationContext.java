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

package org.springframework.context;

import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;

/**
 * 为应用程序提供配置的中心接口. 在应用程序运行时，它是只读的，但是如果实现支持，则可以重新加载.
 *
 * <p>一个应用程序上下文提供如下：
 * <ul>
 * <li>用于访问应用程序组件的Bean工厂方法.继承自 {@link org.springframework.beans.factory.ListableBeanFactory} .</li>
 * <li>以通用方式加载文件资源的能力. 继承自 {@link org.springframework.core.io.ResourceLoader} 接口.</li>
 * <li>将事件发布给注册的侦听器的能力. 继承自 {@link ApplicationEventPublisher} 接口.</li>
 * <li>解析消息，支持国际化的能力. 继承自 {@link MessageSource} 接口.</li>
 * <li>继承父上下文.在后代上下文中的定义将始终优先.
 * 例如，这意味着整个Web应用程序都可以使用单个父上下文，而每个servlet都有其自己的子上下文，该子上下文独立于任何其他servlet的子上下文.</li>
 * </ul>
 *
 * <p>除了标准的 {@link org.springframework.beans.factory.BeanFactory} 生命周期能力，应用上下文
 * 实现了检测和调用 {@link ApplicationContextAware} 以及 {@link ResourceLoaderAware},
 * {@link ApplicationEventPublisherAware} 和 {@link MessageSourceAware} beans.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see ConfigurableApplicationContext
 * @see org.springframework.beans.factory.BeanFactory
 * @see org.springframework.core.io.ResourceLoader
 */
public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory,
		MessageSource, ApplicationEventPublisher, ResourcePatternResolver {

	/**
	 * 返回此应用程序上下文的唯一ID.
	 * @return the unique id of the context, or {@code null} if none
	 */
	@Nullable
	String getId();

	/**
	 * 返回此上下文所属的已部署应用程序的名称.
	 * @return a name for the deployed application, or the empty String by default
	 */
	String getApplicationName();

	/**
	 * 返回此上下文的友好名称.
	 * @return a display name for this context (never {@code null})
	 */
	String getDisplayName();

	/**
	 * 返回首次加载此上下文时的时间戳.
	 * @return the timestamp (ms) when this context was first loaded
	 */
	long getStartupDate();

	/**
	 * 返回父级上下文，如果没有父级，则返回 {@code null} ，则是上下文层次结构的根.
	 * @return the parent context, or {@code null} if there is no parent
	 */
	@Nullable
	ApplicationContext getParent();

	/**
	 * 针对此上下文公开AutowireCapableBeanFactory功能.
	 * 除了在应用程序上下文之外初始化bean实例，将Spring bean生命周期（全部或部分）应用于它们之外，
	 * 应用程序代码通常不使用此方法.
	 * <p>另外，通过{@link ConfigurableApplicationContext}接口公开的内部BeanFactory也可以访问{@link AutowireCapableBeanFactory}接口.
	 * 本方法主要用作ApplicationContext接口上的一种方便的特定功能.
	 * <p><b>注意：从4.2版本开始，在关闭应用程序上下文之后，此方法将始终引发IllegalStateException.</b>
	 * 在当前的Spring Framework版本中，只有可刷新的应用程序上下文才有这种行为.
	 * 从4.2开始，将要求所有应用程序上下文实现都必须遵守.
	 * @return the AutowireCapableBeanFactory for this context
	 * @throws IllegalStateException if the context does not support the
	 * {@link AutowireCapableBeanFactory} interface, or does not hold an
	 * autowire-capable bean factory yet (e.g. if {@code refresh()} has
	 * never been called), or if the context has been closed already
	 * @see ConfigurableApplicationContext#refresh()
	 * @see ConfigurableApplicationContext#getBeanFactory()
	 */
	AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException;

}
