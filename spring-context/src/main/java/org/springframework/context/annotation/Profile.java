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

import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Profiles;

/**
 * 指示一个或多个{@linkplain #value specified profiles}文件处于活动状态时，该组件有资格注册。
 *
 * <p>配置(profile)文件是一个命名的逻辑分组，可以通过{@link ConfigurableEnvironment#setActiveProfiles}
 * 以编程方式激活，也可以通过将{@link AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME
 * spring.profiles.active}属性设置为JVM系统属性，环境变量或 Web应用程序的{@code web.xml}中的Servlet上下文参数。 
 * 还可以通过{@code @ActiveProfiles}注释在集成测试中声明性地激活配置文件。
 *
 * <p>{@code @Profile}注释可以通过以下任何一种方式使用：
 * <ul>
 * <li>as a type-level annotation on any class directly or indirectly annotated with
 * {@code @Component}, including {@link Configuration @Configuration} classes</li>
 * <li>as a meta-annotation, for the purpose of composing custom stereotype annotations</li>
 * <li>as a method-level annotation on any {@link Bean @Bean} method</li>
 * </ul>
 *
 * <p>如果{@code @Configuration}类标记有{@code @Profile}，则除非与一个或多个指定的配置文件处于活动状态，
 * 否则将忽略与该类关联的所有{@code @Bean}方法和{@link Import @Import}注释。 
 * 配置(profile)文件字符串可以包含简单的配置文件名称（例如{@code "p1"}）或配置文件表达式。 
 * 配置文件表达式允许表达更复杂的配置文件逻辑，例如{@code "p1 & p2"}。 有关支持的格式的更多详细信息，
 * 请参见{@link Profiles#of(String...)}。
 *
 * <p>这类似于Spring XML中的行为：如果提供了{@code beans}元素的{@code profile}属性，
 * 例如{@code <beans profile="p1,p2">}，除非至少激活配置文件'p1'或'p2，否则将不解析bean元素。 
 * 同样，如果{@code @Component}或{@code @Configuration}类标记有{@code @Profile({"p1", "p2"})}，
 * 则除非至少激活了配置文件'p1'或'p2'，否则不会注册或处理该类。
 *
 * <p>如果给定的配置(profile)文件以NOT运算符({@code !})为前缀，则如果该配置文件处于非活动状态，
 * 则将注册带注释的组件-例如，给定{@code @Profile({"p1", "!p2"})}，如果“p1”处于活动状态，
 * 或者配置文件“p2”未处于活动状态，则将进行注册。
 *
 * <p>如果省略{@code @Profile}注释，则无论哪个（如果有）配置文件处于活动状态，都将进行注册。
 *
 * <p><b>NOTE:</b> With {@code @Profile} on {@code @Bean} methods, a special scenario may
 * apply: In the case of overloaded {@code @Bean} methods of the same Java method name
 * (analogous to constructor overloading), an {@code @Profile} condition needs to be
 * consistently declared on all overloaded methods. If the conditions are inconsistent,
 * only the condition on the first declaration among the overloaded methods will matter.
 * {@code @Profile} can therefore not be used to select an overloaded method with a
 * particular argument signature over another; resolution between all factory methods
 * for the same bean follows Spring's constructor resolution algorithm at creation time.
 * <b>Use distinct Java method names pointing to the same {@link Bean#name bean name}
 * if you'd like to define alternative beans with different profile conditions</b>;
 * see {@code ProfileDatabaseConfig} in {@link Configuration @Configuration}'s javadoc.
 * <p>注意：对于{@code @Bean}方法上的{@code @Profile}，可能适用特殊场景：如果具有相同Java方法名称的
 * 重载{@code @Bean}方法（类似于构造函数重载），则必须在所有重载方法上一致声明{@code @Profile}条件 。 
 * 如果条件不一致，则只有重载方法中第一个声明的条件才重要。 因此，{@code @Profile}不能用于选择具有特定参数签名的重载方法； 
 * 同一bean的所有工厂方法之间的解析在创建时遵循Spring的构造函数解析算法。 如果要定义具有不同概要文件条件的备用Bean，
 * 请使用指向相同Bean名称的不同Java方法名称。 请参阅@Configuration的javadoc中的ProfileDatabaseConfig。
 *
 * <p>通过XML定义Spring bean时，可以使用{@code <beans>}元素的{@code "profile"}属性。 
 * 有关详细信息，请参见{@code spring-beans} XSD（版本3.1或更高版本）中的文档。
 *
 * @author Chris Beams
 * @author Phillip Webb
 * @author Sam Brannen
 * @since 3.1
 * @see ConfigurableEnvironment#setActiveProfiles
 * @see ConfigurableEnvironment#setDefaultProfiles
 * @see AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME
 * @see AbstractEnvironment#DEFAULT_PROFILES_PROPERTY_NAME
 * @see Conditional
 * @see org.springframework.test.context.ActiveProfiles
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(ProfileCondition.class)
public @interface Profile {

	/**
	 * 注释组件应为其注册的配置(profiles)文件集。
	 */
	String[] value();

}
