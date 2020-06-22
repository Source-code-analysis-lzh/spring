/*
 * Copyright 2002-2013 the original author or authors.
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
 * 指示是否延迟初始化该bean。
 *
 * <p>可以直接在类上使用或者间接使用{@link org.springframework.stereotype.Component @Component}注释
 * 或者在方法上使用{@link Bean @Bean}注释。
 *
 * <p>如果{@code @Component}或{@code @Bean}定义上不存在此注释，则会进行立即的初始化。 
 * 如果存在并设置为{@code true}，则@Bean或@Component不会被初始化，直到被另一个bean引用或从封闭的
 * {@link org.springframework.beans.factory.BeanFactory BeanFactory}中显式检索。 
 * 如果存在并且设置为{@code false}，则将在执行启动单例初始化的bean工厂时立即实例化bean。
 *
 * <p>如果{@link Configuration @Configuration}类上存在Lazy，则表明该{@code @Configuration}中的所有
 * {@code @Bean}方法都应延迟初始化。 如果{@code @Lazy}存在且在{@code @Lazy}注释的{@code @Configuration}
 * 类中的{@code @Bean}方法上为false，则表明重写了“默认懒加载”行为，并且应立即初始化Bean。
 *
 * <p>除了其在组件初始化中的作用外，还可以将该注释放置在标记有
 * {@link org.springframework.beans.factory.annotation.Autowired}或{@link javax.inject.Inject}
 * 的注入点上：在这种情况下，它会为所有受影响的依赖项创建一个惰性解析代理，以替代使用
 * {@link org.springframework.beans.factory.ObjectFactory}的方法或{@link javax.inject.Provider}。
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.0
 * @see Primary
 * @see Bean
 * @see Configuration
 * @see org.springframework.stereotype.Component
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lazy {

	/**
	 * 是否应该发生延迟初始化。
	 */
	boolean value() default true;

}
