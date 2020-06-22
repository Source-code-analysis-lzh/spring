/*
 * Copyright 2002-2016 the original author or authors.
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
 * 指示当多个候选者有资格自动装配单值依赖项时，应优先考虑的Bean。 
 * 如果候选对象中仅存在一个“主” bean，它将是自动装配的值。
 *
 * <p>该注释在语义上等效于Spring XML中{@code <bean>}元素的{@code primary}属性。
 *
 * <p>可以用于直接或间接用{@code @Component}注释的任何类，或用@{@link Bean}注释的方法。
 *
 * <h2>Example</h2>
 * <pre class="code">
 * &#064;Component
 * public class FooService {
 *
 *     private FooRepository fooRepository;
 *
 *     &#064;Autowired
 *     public FooService(FooRepository fooRepository) {
 *         this.fooRepository = fooRepository;
 *     }
 * }
 *
 * &#064;Component
 * public class JdbcFooRepository extends FooRepository {
 *
 *     public JdbcFooRepository(DataSource dataSource) {
 *         // ...
 *     }
 * }
 *
 * &#064;Primary
 * &#064;Component
 * public class HibernateFooRepository extends FooRepository {
 *
 *     public HibernateFooRepository(SessionFactory sessionFactory) {
 *         // ...
 *     }
 * }
 * </pre>
 *
 * <p>因为{@code HibernateFooRepository}标记有{@code @Primary}，所以它将优先于基于jdbc的变量注入，
 * 前提是两者都在同一Spring应用程序上下文中以bean的形式出现，这在自由地进行组件扫描时通常是这种情况。
 *
 * <p>请注意，除非正在使用组件扫描，否则在类级别使用{@code @Primary}无效。 
 * 如果通过XML声明了{@code @Primary}注释的类，则{@code @Primary}注释元数据将被忽略，
 * 而应该改为使用{@code <bean primary="true|false"/>}。
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.0
 * @see Lazy
 * @see Bean
 * @see ComponentScan
 * @see org.springframework.stereotype.Component
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Primary {

}
