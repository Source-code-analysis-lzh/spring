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

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;

/**
 * Bean定义读取器的简单接口. 使用“资源”和“字符串”位置参数指定加载方法.
 *
 * <p>当然，具体的Bean定义读取器可以为Bean定义添加特定于其Bean定义格式的其它加载和注册方法.
 *
 * <p>注意，bean定义读取器不必实现此接口. 它仅对希望遵循标准命名约定的bean定义读者提供建议.
 *
 * @author Juergen Hoeller
 * @since 1.1
 * @see org.springframework.core.io.Resource
 */
public interface BeanDefinitionReader {

	/**
	 * 返回Bean工厂以向其注册Bean定义.
	 * <p>工厂通过BeanDefinitionRegistry接口公开，封装了与Bean定义处理相关的方法.
	 */
	BeanDefinitionRegistry getRegistry();

	/**
	 * 返回资源加载器以用于加载指定位置资源. 可以检查ResourcePatternResolver接口并进行相应的转换，
	 * 以针对给定的资源模式加载多个资源.
	 * <p>返回值为{@code null}表示该bean定义读取器无法使用绝对资源加载.
	 * <p>这主要用于从bean定义资源中导入其他资源，
	 * 例如，通过XML bean定义中的"import"标记. 但是，建议相对于定义资源应用此类导入；
	 * 只有明确的完整资源位置才会触发绝对资源加载.
	 * <p>还有一个{@code loadBeanDefinitions(String)}方法可用于从资源位置（或位置模式）加载Bean定义.
	 * 这是避免显式ResourceLoader处理的一种便利.
	 * @see #loadBeanDefinitions(String)
	 * @see org.springframework.core.io.support.ResourcePatternResolver
	 */
	@Nullable
	ResourceLoader getResourceLoader();

	/**
	 * 返回用于Bean类的类加载器.
	 * <p>{@code null}建议不要急于加载Bean类，而只是用类名注册Bean定义，并在以后（或永不）解析相应的类.
	 */
	@Nullable
	ClassLoader getBeanClassLoader();

	/**
	 * 返回BeanNameGenerator用于匿名Bean（未显式指定Bean名称）.
	 */
	BeanNameGenerator getBeanNameGenerator();


	/**
	 * 从指定的资源加载bean定义.
	 * @param resource the resource descriptor
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException;

	/**
	 * 从指定的资源加载bean定义.
	 * @param resources the resource descriptors
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	int loadBeanDefinitions(Resource... resources) throws BeanDefinitionStoreException;

	/**
	 * 从指定的资源位置加载bean定义.
	 * <p>该位置也可以是位置模式，前提是此bean定义读取器的ResourceLoader是ResourcePatternResolver.
	 * @param location the resource location, to be loaded with the ResourceLoader
	 * (or ResourcePatternResolver) of this bean definition reader
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 * @see #getResourceLoader()
	 * @see #loadBeanDefinitions(org.springframework.core.io.Resource)
	 * @see #loadBeanDefinitions(org.springframework.core.io.Resource[])
	 */
	int loadBeanDefinitions(String location) throws BeanDefinitionStoreException;

	/**
	 * 从指定的资源位置加载bean定义.
	 * @param locations the resource locations, to be loaded with the ResourceLoader
	 * (or ResourcePatternResolver) of this bean definition reader
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	int loadBeanDefinitions(String... locations) throws BeanDefinitionStoreException;

}
