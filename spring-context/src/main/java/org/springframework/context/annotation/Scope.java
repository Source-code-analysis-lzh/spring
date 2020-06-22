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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.annotation.AliasFor;

/**
 * 当与{@link org.springframework.stereotype.Component @Component}一起用作类型级别的注释时，
 * {@code @Scope}表示要用于带注释类型的实例的范围的名称。
 *
 * <p>当与{@link Bean @Bean}一起用作方法级注释时，{@code @Scope}表示要用于从方法返回的实例的范围的名称。
 *
 * <p><b>NOTE:</b> {@code @Scope} annotations are only introspected on the
 * concrete bean class (for annotated components) or the factory method
 * (for {@code @Bean} methods). In contrast to XML bean definitions,
 * there is no notion of bean definition inheritance, and inheritance
 * hierarchies at the class level are irrelevant for metadata purposes.
 * <p>注意：{@code @Scope}注释仅在具体的bean类（对于带注释的组件）或工厂方法（对于{@code @Bean}方法）上内省。 
 * 与XML bean定义相反，没有bean定义继承的概念，并且在类级别的继承层次结构与元数据目的无关。
 *
 * <p>在这种情况下，作用域是指实例的生命周期，例如{@code singleton}, {@code prototype}等。 
 * 可以使用{@link ConfigurableBeanFactory}和{@code WebApplicationContext}接口中可用的
 * {@code SCOPE_*}常量来引用Spring中开箱即用的范围。
 *
 * <p>要注册其他自定义范围，请参见{@link org.springframework.beans.factory.config.CustomScopeConfigurer
 * CustomScopeConfigurer}。
 *
 * @author Mark Fisher
 * @author Chris Beams
 * @author Sam Brannen
 * @since 2.5
 * @see org.springframework.stereotype.Component
 * @see org.springframework.context.annotation.Bean
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scope {

	/**
	 * Alias for {@link #scopeName}.
	 * @see #scopeName
	 */
	@AliasFor("scopeName")
	String value() default "";

	/**
	 * 指定用于带注释的组件/bean的作用域名称。
	 * <p>默认为空字符串（{@code ""}），表示{@link ConfigurableBeanFactory#SCOPE_SINGLETON SCOPE_SINGLETON}。
	 * @since 4.2
	 * @see ConfigurableBeanFactory#SCOPE_PROTOTYPE
	 * @see ConfigurableBeanFactory#SCOPE_SINGLETON
	 * @see org.springframework.web.context.WebApplicationContext#SCOPE_REQUEST
	 * @see org.springframework.web.context.WebApplicationContext#SCOPE_SESSION
	 * @see #value
	 */
	@AliasFor("value")
	String scopeName() default "";

	/**
	 * 指定是否应将组件配置为作用域代理，如果是，则指定代理是基于接口还是基于子类。
	 * <p>默认值为{@link ScopedProxyMode#DEFAULT}，通常指示除非在组件扫描指令级别配置了其它默认值，否则不应创建任何带范围的代理。
	 * <p>类似于Spring XML中的{@code <aop:scoped-proxy/>}支持。
	 * @see ScopedProxyMode
	 */
	ScopedProxyMode proxyMode() default ScopedProxyMode.DEFAULT;

}
