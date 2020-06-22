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
 * 要在销毁时释放资源的bean所实现的接口。 
 * {@link BeanFactory}将在单个范围bean销毁时调用destroy方法。 
 * 应该假定{@link org.springframework.context.ApplicationContext}
 * 在关闭时由应用程序生命周期来驱动其所有单例销毁。
 *
 * <p>出于同样的目的，Spring管理的bean也可以实现Java的{@link AutoCloseable}接口。 
 * 实现接口的另一种方法是指定自定义的destroy方法，例如在XML bean定义中。 
 * 有关所有bean生命周期方法的列表，请参见{@link BeanFactory BeanFactory javadocs}。
 *
 * @author Juergen Hoeller
 * @since 12.08.2003
 * @see InitializingBean
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getDestroyMethodName()
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#destroySingletons()
 * @see org.springframework.context.ConfigurableApplicationContext#close()
 */
public interface DisposableBean {

	/**
	 * 由包含{@code BeanFactory}的销毁bean调用。
	 * @throws Exception in case of shutdown errors. Exceptions will get logged
	 * but not rethrown to allow other beans to release their resources as well.
	 */
	void destroy() throws Exception;

}
