/*
 * Copyright 2002-2015 the original author or authors.
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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.core.annotation.AliasFor;

/**
 * 指示一个或多个包含要导入的bean定义的资源。
 *
 * <p>与{@link Import @Import}一样，此注释提供的功能类似于Spring XML中的{@code <import/>}元素。 
 * 通常，在设计要由{@link AnnotationConfigApplicationContext}启动的{@link Configuration @Configuration}类时，
 * 通常使用它，但是在某些XML功能（例如名称空间）仍是必需的情况下。
 *
 * <p>默认情况下，如果以{@code ".groovy"}结尾，则将使用
 * {@link org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader GroovyBeanDefinitionReader}
 * 处理{@link #value}属性的参数。 否则，将使用
 * {@link org.springframework.beans.factory.xml.XmlBeanDefinitionReader XmlBeanDefinitionReader}
 * 解析Spring {@code <beans/>} XML文件。 （可选）可以声明{@link #reader}属性，以允许用户选择自定义{@link BeanDefinitionReader}实现。
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 3.0
 * @see Configuration
 * @see Import
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ImportResource {

	/**
	 * Alias for {@link #locations}.
	 * @see #locations
	 * @see #reader
	 */
	@AliasFor("locations")
	String[] value() default {};

	/**
	 * 导入资源的位置。
	 * <p>支持资源加载前缀，例如{@code classpath:}，{@code file:}等。
	 * <p>有关如何处理资源的详细信息，请查阅Javadoc的{@link #reader}。
	 * @since 4.2
	 * @see #value
	 * @see #reader
	 */
	@AliasFor("value")
	String[] locations() default {};

	/**
	 * 在处理通过{@link #value}属性指定的资源时使用的{@link BeanDefinitionReader}实现。
	 * <p>默认情况下，阅读器将适配于指定的资源路径：{@code ".groovy"}文件将使用
	 * {@link org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader GroovyBeanDefinitionReader};
	 * 处理； 相反，所有其它资源将使用
	 * {@link org.springframework.beans.factory.xml.XmlBeanDefinitionReader XmlBeanDefinitionReader}处理。
	 * @see #value
	 */
	Class<? extends BeanDefinitionReader> reader() default BeanDefinitionReader.class;

}
