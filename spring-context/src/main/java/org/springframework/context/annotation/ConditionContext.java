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

package org.springframework.context.annotation;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;

/**
 * {@link Condition Conditions}使用的上下文信息。
 *
 * @author Phillip Webb
 * @author Juergen Hoeller
 * @since 4.0
 */
public interface ConditionContext {

	/**
	 * 返回将保存条件匹配的Bean定义的{@link BeanDefinitionRegistry}。
	 * @throws IllegalStateException if no registry is available (which is unusual:
	 * only the case with a plain {@link ClassPathScanningCandidateComponentProvider})
	 */
	BeanDefinitionRegistry getRegistry();

	/**
	 * 返回将保存条件匹配的Bean定义的{@link ConfigurableListableBeanFactory}；
	 * 如果Bean工厂不可用（或不能向下转换为{@code ConfigurableListableBeanFactory}），则返回{@code null}。
	 */
	@Nullable
	ConfigurableListableBeanFactory getBeanFactory();

	/**
	 * 返回当前应用程序正在运行的{@link Environment}。
	 */
	Environment getEnvironment();

	/**
	 * 返回当前使用的{@link ResourceLoader}。
	 */
	ResourceLoader getResourceLoader();

	/**
	 * 返回应用于加载其它类的{@link ClassLoader}（即使无法访问系统ClassLoader，也只能为{@code null}）。
	 * @see org.springframework.util.ClassUtils#forName(String, ClassLoader)
	 */
	@Nullable
	ClassLoader getClassLoader();

}
