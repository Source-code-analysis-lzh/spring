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

package org.springframework.beans.factory.xml;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.Resource;

/**
 * {@link DefaultListableBeanFactory}的便捷扩展，可以从XML文档中读取bean定义.
 * 委托给下面的{@link XmlBeanDefinitionReader}；
 * 实际上等效于将XmlBeanDefinitionReader与DefaultListableBeanFactory一起使用.
 *
 * <p>所需XML文档的结构，元素和属性名称在此类中进行了硬编码.
 * （当然，如果需要生成此格式，可以运行转换）. "beans"不必是XML文档的根元素：此类将解析XML文件中的所有bean定义元素.
 *
 * <p>此类使用{@link DefaultListableBeanFactory}超类注册每个bean定义，
 * 并依赖后者的{@link BeanFactory}接口实现. 它支持单例，原型和对这两种bean的引用.
 * 有关选项和配置样式的详细信息，请参见{@code "spring-beans-3.x.xsd"}（或以前的{@code "spring-beans-2.0.dtd"}）.
 *
 * <p>对于高级需求，请考虑将{@link DefaultListableBeanFactory}与{@link XmlBeanDefinitionReader}一起使用.
 * 后者允许从多个XML资源读取，并且在其实际XML解析行为中可以高度配置.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 15 April 2001
 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory
 * @see XmlBeanDefinitionReader
 * @deprecated as of Spring 3.1 in favor of {@link DefaultListableBeanFactory} and
 * {@link XmlBeanDefinitionReader}
 */
@Deprecated
@SuppressWarnings({"serial", "all"})
public class XmlBeanFactory extends DefaultListableBeanFactory {

	private final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this);


	/**
	 * 使用给定资源创建一个新的XmlBeanFactory，该资源必须可以使用DOM进行解析.
	 * @param resource the XML resource to load bean definitions from
	 * @throws BeansException in case of loading or parsing errors
	 */
	public XmlBeanFactory(Resource resource) throws BeansException {
		this(resource, null);
	}

	/**
	 * 使用给定的输入流创建一个新的XmlBeanFactory，必须使用DOM对其进行解析.
	 * @param resource the XML resource to load bean definitions from
	 * @param parentBeanFactory parent bean factory
	 * @throws BeansException in case of loading or parsing errors
	 */
	public XmlBeanFactory(Resource resource, BeanFactory parentBeanFactory) throws BeansException {
		super(parentBeanFactory);
		this.reader.loadBeanDefinitions(resource);
	}

}
