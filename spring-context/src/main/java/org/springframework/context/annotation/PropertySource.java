/*
 * Copyright 2002-2020 the original author or authors.
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
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.io.support.PropertySourceFactory;

/**
 * 注释提供了一种方便的声明性机制，用于将
 * {@link org.springframework.core.env.PropertySource PropertySource}添加到Spring的
 * {@link org.springframework.core.env.Environment Environment}中。 与@{@link Configuration}类一起使用。
 *
 * <h3>Example usage</h3>
 *
 * <p>给定一个包含键/值对{@code testbean.name=myTestBean}的文件{@code app.properties}，
 * 以下{@code @Configuration}类使用{@code @PropertySource}将{@code app.properties}
 * 贡献给{@code Environment}的{@code PropertySources}集合。
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;PropertySource("classpath:/com/myco/app.properties")
 * public class AppConfig {
 *
 *     &#064;Autowired
 *     Environment env;
 *
 *     &#064;Bean
 *     public TestBean testBean() {
 *         TestBean testBean = new TestBean();
 *         testBean.setName(env.getProperty("testbean.name"));
 *         return testBean;
 *     }
 * }</pre>
 *
 * <p>注意，将{@code Environment}对象{@link org.springframework.beans.factory.annotation.Autowired @Autowired}
 * 插入配置类，然后在填充{@code TestBean}对象时使用。 考虑上述配置，对{@code testBean.getName()}的调用将返回"myTestBean"。
 *
 * <h3>Resolving <code>${...}</code> placeholders in {@code <bean>} and {@code @Value} annotations</h3>
 *
 * <p>In order to resolve ${...} placeholders in {@code <bean>} definitions or {@code @Value}
 * annotations using properties from a {@code PropertySource}, you must ensure that an
 * appropriate <em>embedded value resolver</em> is registered in the {@code BeanFactory}
 * used by the {@code ApplicationContext}. This happens automatically when using
 * {@code <context:property-placeholder>} in XML. When using {@code @Configuration} classes
 * this can be achieved by explicitly registering a {@code PropertySourcesPlaceholderConfigurer}
 * via a {@code static} {@code @Bean} method. Note, however, that explicit registration
 * of a {@code PropertySourcesPlaceholderConfigurer} via a {@code static} {@code @Bean}
 * method is typically only required if you need to customize configuration such as the
 * placeholder syntax, etc. See the "Working with externalized values" section of
 * {@link Configuration @Configuration}'s javadocs and "a note on
 * BeanFactoryPostProcessor-returning {@code @Bean} methods" of {@link Bean @Bean}'s
 * javadocs for details and examples.
 * <p>为了使用{@code PropertySource}中的属性解析{@code <bean>}定义或{@code @Value}批注中的${...}占位符，
 * 必须确保在{@code ApplicationContext}使用的{@code BeanFactory}中注册了适当的嵌入式值解析器。 
 * 在XML中使用{@code <context:property-placeholder>}时，这会自动发生。 使用{@code @Configuration}类时，
 * 可以通过{@code static} {@code @Bean}方法显式注册{@code PropertySourcesPlaceholderConfigurer}来实现。 
 * 但是请注意，通常仅在需要自定义配置（例如占位符语法等）时，才需要通过{@code static} {@code @Bean}方法
 * 显式注册{@code PropertySourcesPlaceholderConfigurer}。
 * 请参阅{@link Configuration @Configuration}的javadocs的“使用外部化的值”有关详细信息和示例，
 * 和请参见{@code @Bean}的javadocs的BeanFactoryPostProcessor-returning @Bean方法。
 *
 * <h3>Resolving ${...} placeholders within {@code @PropertySource} resource locations</h3>
 *
 * <p>{@code @PropertySource}资源位置中存在的任何${...}占位符都将根据已针对该环境注册的一组属性源进行解析。 例如：
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;PropertySource("classpath:/com/${my.placeholder:default/path}/app.properties")
 * public class AppConfig {
 *
 *     &#064;Autowired
 *     Environment env;
 *
 *     &#064;Bean
 *     public TestBean testBean() {
 *         TestBean testBean = new TestBean();
 *         testBean.setName(env.getProperty("testbean.name"));
 *         return testBean;
 *     }
 * }</pre>
 *
 * <p>假设"my.placeholder"存在于已注册的属性来源之一中，例如 系统属性或环境变量，则占位符将解析为相应的值。 
 * 如果不是，则将使用“默认值/路径”作为默认值。 表示默认值（用冒号":"分隔）是可选的。 
 * 如果未指定默认值并且无法解析属性，则将抛出{@code IllegalArgumentException}。
 *
 * <h3>A note on property overriding with @PropertySource</h3>
 *
 * <p>In cases where a given property key exists in more than one {@code .properties}
 * file, the last {@code @PropertySource} annotation processed will 'win' and override.
 *
 * <p>For example, given two properties files {@code a.properties} and
 * {@code b.properties}, consider the following two configuration classes
 * that reference them with {@code @PropertySource} annotations:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;PropertySource("classpath:/com/myco/a.properties")
 * public class ConfigA { }
 *
 * &#064;Configuration
 * &#064;PropertySource("classpath:/com/myco/b.properties")
 * public class ConfigB { }
 * </pre>
 *
 * <p>The override ordering depends on the order in which these classes are registered
 * with the application context.
 *
 * <pre class="code">
 * AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
 * ctx.register(ConfigA.class);
 * ctx.register(ConfigB.class);
 * ctx.refresh();
 * </pre>
 *
 * <p>In the scenario above, the properties in {@code b.properties} will override any
 * duplicates that exist in {@code a.properties}, because {@code ConfigB} was registered
 * last.
 *
 * <p>在某些情况下，使用{@code @PropertySource}注释严格控制属性源的顺序可能是不可行的。 
 * 例如，如果上面的{@code @Configuration}类是通过组件扫描注册的，则顺序很难预测。 
 * 在这种情况下（如果覆盖很重要），建议用户退回到使用编程式PropertySource API。 
 * 有关详细信息，请参见{@link org.springframework.core.env.ConfigurableEnvironment ConfigurableEnvironment}
 * 和{@link org.springframework.core.env.MutablePropertySources MutablePropertySources} javadocs。
 *
 * <p>注意：根据Java 8约定，此注释可以重复。 但是，所有此类{@code @PropertySource}批注都必须在同一级别上声明：
 * 直接在配置类上声明，或者在同一自定义注释中作为元注释声明。 不建议将直接注释和元注释混合使用，因为直接注释将有效地覆盖元注释。
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Phillip Webb
 * @author Sam Brannen
 * @since 3.1
 * @see PropertySources
 * @see Configuration
 * @see org.springframework.core.env.PropertySource
 * @see org.springframework.core.env.ConfigurableEnvironment#getPropertySources()
 * @see org.springframework.core.env.MutablePropertySources
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(PropertySources.class)
public @interface PropertySource {

	/**
	 * Indicate the name of this property source. If omitted, the {@link #factory()}
	 * will generate a name based on the underlying resource (in the case of
	 * {@link org.springframework.core.io.support.DefaultPropertySourceFactory}:
	 * derived from the resource description through a corresponding name-less
	 * {@link org.springframework.core.io.support.ResourcePropertySource} constructor).
	 * @see org.springframework.core.env.PropertySource#getName()
	 * @see org.springframework.core.io.Resource#getDescription()
	 */
	String name() default "";

	/**
	 * Indicate the resource location(s) of the properties file to be loaded.
	 * <p>Both traditional and XML-based properties file formats are supported
	 * &mdash; for example, {@code "classpath:/com/myco/app.properties"}
	 * or {@code "file:/path/to/file.xml"}.
	 * <p>Resource location wildcards (e.g. *&#42;/*.properties) are not permitted;
	 * each location must evaluate to exactly one {@code .properties} resource.
	 * <p>${...} placeholders will be resolved against any/all property sources already
	 * registered with the {@code Environment}. See {@linkplain PropertySource above}
	 * for examples.
	 * <p>Each location will be added to the enclosing {@code Environment} as its own
	 * property source, and in the order declared.
	 */
	String[] value();

	/**
	 * Indicate if failure to find the a {@link #value() property resource} should be
	 * ignored.
	 * <p>{@code true} is appropriate if the properties file is completely optional.
	 * Default is {@code false}.
	 * @since 4.0
	 */
	boolean ignoreResourceNotFound() default false;

	/**
	 * A specific character encoding for the given resources, e.g. "UTF-8".
	 * @since 4.3
	 */
	String encoding() default "";

	/**
	 * Specify a custom {@link PropertySourceFactory}, if any.
	 * <p>By default, a default factory for standard resource files will be used.
	 * @since 4.3
	 * @see org.springframework.core.io.support.DefaultPropertySourceFactory
	 * @see org.springframework.core.io.support.ResourcePropertySource
	 */
	Class<? extends PropertySourceFactory> factory() default PropertySourceFactory.class;

}
