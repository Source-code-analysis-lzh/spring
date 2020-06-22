/*
 * Copyright 2002-2019 the original author or authors.
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

/**
 * 指示要导入的一个或多个组件类，通常是{@link Configuration @Configuration}类。
 *
 * <p>提供与Spring XML中的{@code <import/>}元素等效的功能。 允许导入{@code @Configuration}类，
 * {@link ImportSelector}和{@link ImportBeanDefinitionRegistrar}实现以及常规组件类
 * （从4.2开始；类似于{@link AnnotationConfigApplicationContext#register}）。
 *
 * <p>导入的{@code @Configuration}类中声明的{@code @Bean}定义应使用
 * {@link org.springframework.beans.factory.annotation.Autowired @Autowired}注入进行访问。 
 * 可以对bean本身进行自动装配，也可以对声明bean的配置类实例进行自动装配。 
 * 后一种方法允许在{@code @Configuration}类方法之间进行显式的，IDE友好的导航。
 *
 * <p>可以在类级别或作为元注释声明。
 *
 * <p>如果需要导入XML或其他非{@code @Configuration} bean定义资源，
 * 请改用{@link ImportResource @ImportResource}注释。
 * 
 * <p>@Import注解的作用和在使用spring的xml配置时用到的<import/>类似。但应注意是@Import在使用时必须要保证能被IOC容器扫描到，所以通常它会和@Configuration或者@ComponentScan配套使用。
 *
 * <p>@Import可以用来如下四种方式的导入：
 *
 * 带有@Configuration注解的类
 * 实现了ImportSelector接口的类
 * 实现了ImportBeanDefinitionRegistrar接口的类
 * 被IOC容器注册的bean的class 
 * 
 * <p>@Import在使用时可以声明在JAVA类上，或者作为元注解使用（即声明在其他注解上）
 * 
 * 参考： https://www.jianshu.com/p/640a339474fb
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.0
 * @see Configuration
 * @see ImportSelector
 * @see ImportBeanDefinitionRegistrar
 * @see ImportResource
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Import {

	/**
	 * {@link Configuration @Configuration}, {@link ImportSelector},
	 * {@link ImportBeanDefinitionRegistrar}或要导入的常规组件类。
	 */
	Class<?>[] value();

}
