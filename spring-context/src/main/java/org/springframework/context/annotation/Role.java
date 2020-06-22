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

import org.springframework.beans.factory.config.BeanDefinition;

/**
 * 指示给定bean的“角色”提示。
 *
 * <p>可以用于直接或间接用{@link org.springframework.stereotype.Component}
 * 注释的任何类，或用{@link Bean}注释的方法。
 *
 * <p>如果在组件或Bean定义上不存在此注释，则将应用{@link BeanDefinition#ROLE_APPLICATION}的默认值。
 *
 * <p>如果{@link Configuration @Configuration}类上存在Role，则表明配置类bean定义的角色，
 * 并且不会级联到其中定义的所有@{@code Bean}方法。 例如，此行为与@{@link Lazy}批注的行为不同。
 *
 * @author Chris Beams
 * @since 3.1
 * @see BeanDefinition#ROLE_APPLICATION
 * @see BeanDefinition#ROLE_INFRASTRUCTURE
 * @see BeanDefinition#ROLE_SUPPORT
 * @see Bean
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Role {

	/**
	 * Set the role hint for the associated bean.
	 * @see BeanDefinition#ROLE_APPLICATION
	 * @see BeanDefinition#ROLE_INFRASTRUCTURE (基础设施)
	 * @see BeanDefinition#ROLE_SUPPORT
	 */
	int value();

}
