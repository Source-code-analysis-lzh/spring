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

package org.springframework.context.support;

import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.lang.Nullable;

/**
 * {@link org.springframework.context.ApplicationContext}实现的基类，
 * 应该支持对{@link #refresh()}的多次调用，每次都创建一个新的内部bean工厂实例.
 * 通常（但不是必须），这样的上下文将由一组配置位置驱动，以从中加载bean定义.
 *
 * <p>子类唯一实现的方法是{@link #loadBeanDefinitions}，该方法在每次刷新时都会调用.
 * 一个具体的实现应该将bean定义加载到给定的{@link org.springframework.beans.factory.support.DefaultListableBeanFactory}中，
 * 通常委托给一个或多个特定的bean定义读取器.
 *
 * <p><b>注意，WebApplicationContexts有一个类似的基类.</b>
 * {@link org.springframework.web.context.support.AbstractRefreshableWebApplicationContext}
 * 提供了相同的子类化策略，但是还预实现了Web环境的所有上下文功能.
 * 还有一种预定义的方式来接收Web上下文的配置位置.
 *
 * <p>以特定的bean定义格式读取的该基类的具体独立子类是{@link ClassPathXmlApplicationContext}
 * 和{@link FileSystemXmlApplicationContext}，它们均从通用{@link AbstractXmlApplicationContext}基类派生.
 * {@link org.springframework.context.annotation.AnnotationConfigApplicationContext}
 * 支持{@code @Configuration}注释的类作为Bean定义的源.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 1.1.3
 * @see #loadBeanDefinitions
 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory
 * @see org.springframework.web.context.support.AbstractRefreshableWebApplicationContext
 * @see AbstractXmlApplicationContext
 * @see ClassPathXmlApplicationContext
 * @see FileSystemXmlApplicationContext
 * @see org.springframework.context.annotation.AnnotationConfigApplicationContext
 */
public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {

	@Nullable
	private Boolean allowBeanDefinitionOverriding;

	@Nullable
	private Boolean allowCircularReferences;

	/** Bean factory for this context. */
	@Nullable
	private DefaultListableBeanFactory beanFactory;

	/** Synchronization monitor for the internal BeanFactory. */
	private final Object beanFactoryMonitor = new Object();


	/**
	 * Create a new AbstractRefreshableApplicationContext with no parent.
	 */
	public AbstractRefreshableApplicationContext() {
	}

	/**
	 * Create a new AbstractRefreshableApplicationContext with the given parent context.
	 * @param parent the parent context
	 */
	public AbstractRefreshableApplicationContext(@Nullable ApplicationContext parent) {
		super(parent);
	}


	/**
	 * 设置是否应该通过注册具有相同名称的其他定义（自动替换前者）来覆盖Bean定义.
	 * 否则，将引发异常. 默认值为"true".
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowBeanDefinitionOverriding
	 */
	public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
		this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
	}

	/**
	 * 设置是否在bean之间允许循环引用-并自动尝试解决它们.
	 * <p>默认值为"true". 禁用此选项会在遇到循环引用时引发异常，从而完全禁止它们.
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowCircularReferences
	 */
	public void setAllowCircularReferences(boolean allowCircularReferences) {
		this.allowCircularReferences = allowCircularReferences;
	}


	/**
	 * 此实现对该上下文的基础bean工厂进行实际刷新，关闭前一个bean工厂（如果有），
	 * 并为该上下文生命周期的下一阶段初始化一个新的bean工厂.
	 */
	@Override
	protected final void refreshBeanFactory() throws BeansException {
		if (hasBeanFactory()) {
			destroyBeans();
			closeBeanFactory();
		}
		try {
			// 使用DefaultListableBeanFactory实例化一个BeanFactory
			DefaultListableBeanFactory beanFactory = createBeanFactory();
			beanFactory.setSerializationId(getId());
			// 自定义beanFactory
			customizeBeanFactory(beanFactory);
			// 载入BeanDefinitions 
			loadBeanDefinitions(beanFactory);
			synchronized (this.beanFactoryMonitor) {
				this.beanFactory = beanFactory;
			}
		}
		catch (IOException ex) {
			throw new ApplicationContextException("I/O error parsing bean definition source for " + getDisplayName(), ex);
		}
	}

	@Override
	protected void cancelRefresh(BeansException ex) {
		synchronized (this.beanFactoryMonitor) {
			if (this.beanFactory != null) {
				this.beanFactory.setSerializationId(null);
			}
		}
		super.cancelRefresh(ex);
	}

	@Override
	protected final void closeBeanFactory() {
		synchronized (this.beanFactoryMonitor) {
			if (this.beanFactory != null) {
				this.beanFactory.setSerializationId(null);
				this.beanFactory = null;
			}
		}
	}

	/**
	 * Determine whether this context currently holds a bean factory,
	 * i.e. has been refreshed at least once and not been closed yet.
	 */
	protected final boolean hasBeanFactory() {
		synchronized (this.beanFactoryMonitor) {
			return (this.beanFactory != null);
		}
	}

	@Override
	public final ConfigurableListableBeanFactory getBeanFactory() {
		synchronized (this.beanFactoryMonitor) {
			if (this.beanFactory == null) {
				throw new IllegalStateException("BeanFactory not initialized or already closed - " +
						"call 'refresh' before accessing beans via the ApplicationContext");
			}
			return this.beanFactory;
		}
	}

	/**
	 * Overridden to turn it into a no-op: With AbstractRefreshableApplicationContext,
	 * {@link #getBeanFactory()} serves a strong assertion for an active context anyway.
	 */
	@Override
	protected void assertBeanFactoryActive() {
	}

	/**
	 * 为此上下文创建一个内部bean工厂. 为每次{@link #refresh()}尝试调用.
	 * <p>默认实现创建一个{@link org.springframework.beans.factory.support.DefaultListableBeanFactory}，
	 * 并以该上下文的父级的内部bean工厂{@linkplain #getInternalParentBeanFactory()}作为父级bean工厂.
	 * 可以在子类中重写，例如，以自定义DefaultListableBeanFactory的设置.
	 * @return the bean factory for this context
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowBeanDefinitionOverriding
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowEagerClassLoading
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowCircularReferences
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowRawInjectionDespiteWrapping
	 */
	protected DefaultListableBeanFactory createBeanFactory() {
		return new DefaultListableBeanFactory(getInternalParentBeanFactory());
	}

	/**
	 * 定制此上下文使用的内部bean工厂. 为每次{@link #refresh()}尝试调用.
	 * <p>默认实现将应用此上下文的{@linkplain #setAllowBeanDefinitionOverriding "allowBeanDefinitionOverriding"}
	 * 和{@linkplain #setAllowCircularReferences "allowCircularReferences"}设置（如果已指定）.
	 * 可以在子类中重写以自定义{@link DefaultListableBeanFactory}的任何设置.
	 * @param beanFactory the newly created bean factory for this context
	 * @see DefaultListableBeanFactory#setAllowBeanDefinitionOverriding
	 * @see DefaultListableBeanFactory#setAllowCircularReferences
	 * @see DefaultListableBeanFactory#setAllowRawInjectionDespiteWrapping
	 * @see DefaultListableBeanFactory#setAllowEagerClassLoading
	 */
	protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
		if (this.allowBeanDefinitionOverriding != null) {
			beanFactory.setAllowBeanDefinitionOverriding(this.allowBeanDefinitionOverriding);
		}
		if (this.allowCircularReferences != null) {
			beanFactory.setAllowCircularReferences(this.allowCircularReferences);
		}
	}

	/**
	 * 通常通过委派一个或多个bean定义读取器，将bean定义加载到给定的bean工厂中.
	 * @param beanFactory the bean factory to load bean definitions into
	 * @throws BeansException if parsing of the bean definitions failed
	 * @throws IOException if loading of bean definition files failed
	 * @see org.springframework.beans.factory.support.PropertiesBeanDefinitionReader
	 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
	 */
	protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory)
			throws BeansException, IOException;

}
