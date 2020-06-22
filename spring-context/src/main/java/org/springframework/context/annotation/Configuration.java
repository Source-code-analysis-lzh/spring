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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

/**
 * 指示一个类，它声明了一个或多个{@link Bean @Bean}方法，并且可以由Spring容器进行处理以在运行时为这些bean生成bean定义和服务请求，例如：
 *
 * <pre class="code">
 * &#064;Configuration
 * public class AppConfig {
 *
 *     &#064;Bean
 *     public MyBean myBean() {
 *         // instantiate, configure and return bean ...
 *     }
 * }</pre>
 *
 * <h2>Bootstrapping {@code @Configuration} classes</h2>
 *
 * <h3>Via {@code AnnotationConfigApplicationContext}</h3>
 *
 * <p>通常使用{@link AnnotationConfigApplicationContext}或其支持Web的变体
 * {@link org.springframework.web.context.support.AnnotationConfigWebApplicationContext
 * AnnotationConfigWebApplicationContext}来启动{@code @Configuration}类。 前者的一个简单示例如下：
 *
 * <pre class="code">
 * AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
 * ctx.register(AppConfig.class);
 * ctx.refresh();
 * MyBean myBean = ctx.getBean(MyBean.class);
 * // use myBean ...
 * </pre>
 *
 * <p>有关更多详细信息，请参见{@link AnnotationConfigApplicationContext} javadocs，
 * 有关{@code Servlet}容器中的Web配置说明，请参见
 * {@link org.springframework.web.context.support.AnnotationConfigWebApplicationContext
 * AnnotationConfigWebApplicationContext}。
 *
 * <h3>Via Spring {@code <beans>} XML</h3>
 *
 * <p>作为直接针对{@code AnnotationConfigApplicationContext}注册{@code @Configuration}类的替代方法，
 * 可以将{@code @Configuration}类声明为Spring XML文件中的常规{@code <bean>}定义：
 *
 * <pre class="code">
 * &lt;beans&gt;
 *    &lt;context:annotation-config/&gt;
 *    &lt;bean class="com.acme.AppConfig"/&gt;
 * &lt;/beans&gt;
 * </pre>
 *
 * <p>在上面的示例中，需要{@code <context:annotation-config/>}才能启用{@link ConfigurationClassPostProcessor}
 * 和其它与注释相关的后处理器，这些处理器有助于处理{@code @Configuration}类。
 *
 * <h3>Via component scanning</h3>
 *
 * <p>{@code @Configuration}用{@link Component @Component}进行元注释，因此{@code @Configuration}
 * 类是组件扫描的候选对象（通常使用Spring XML的{@code <context:component-scan/>}元素），
 * 因此也可以像任何常规{@code @Component}一样利用{@link Autowired @Autowired}/{@link javax.inject.Inject @Inject}。 
 * 特别是，如果存在单个构造函数，自动装配语义将透明地应用于该构造函数：
 *
 * <pre class="code">
 * &#064;Configuration
 * public class AppConfig {
 *
 *     private final SomeBean someBean;
 *
 *     public AppConfig(SomeBean someBean) {
 *         this.someBean = someBean;
 *     }
 *
 *     // &#064;Bean definition using "SomeBean"
 *
 * }</pre>
 *
 * <p>{@code @Configuration}类不仅可以使用组件扫描来启动，还可以自己使用{@link ComponentScan @ComponentScan}注释配置组件扫描：
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;ComponentScan("com.acme.app.services")
 * public class AppConfig {
 *     // various &#064;Bean definitions ...
 * }</pre>
 *
 * <p>See the {@link ComponentScan @ComponentScan} javadocs for details.
 *
 * <h2>Working with externalized values</h2>
 *
 * <h3>Using the {@code Environment} API</h3>
 *
 * <p>可以通过将Spring {@link org.springframework.core.env.Environment}注入
 * {@code @Configuration}类中来查找外部化的值，例如，使用{@code @Autowired}批注：
 *
 * <pre class="code">
 * &#064;Configuration
 * public class AppConfig {
 *
 *     &#064Autowired Environment env;
 *
 *     &#064;Bean
 *     public MyBean myBean() {
 *         MyBean myBean = new MyBean();
 *         myBean.setName(env.getProperty("bean.name"));
 *         return myBean;
 *     }
 * }</pre>
 *
 * <p>通过{@code Environment}解析的属性驻留在一个或多个“属性源”对象中，并且{@code @Configuration}类可以使用
 * {@link PropertySource @PropertySource}批注将属性源贡献给{@code Environment}对象：
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;PropertySource("classpath:/com/acme/app.properties")
 * public class AppConfig {
 *
 *     &#064Inject Environment env;
 *
 *     &#064;Bean
 *     public MyBean myBean() {
 *         return new MyBean(env.getProperty("bean.name"));
 *     }
 * }</pre>
 *
 * <p>See the {@link org.springframework.core.env.Environment Environment}
 * and {@link PropertySource @PropertySource} javadocs for further details.
 *
 * <h3>Using the {@code @Value} annotation</h3>
 *
 * <p>可以使用{@link Value @Value}批注将外部化的值注入{@code @Configuration}类：
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;PropertySource("classpath:/com/acme/app.properties")
 * public class AppConfig {
 *
 *     &#064Value("${bean.name}") String beanName;
 *
 *     &#064;Bean
 *     public MyBean myBean() {
 *         return new MyBean(beanName);
 *     }
 * }</pre>
 *
 * <p>此方法通常与Spring的{@link org.springframework.context.support.PropertySourcesPlaceholderConfigurer
 * PropertySourcesPlaceholderConfigurer}结合使用，可以通过{@code <context:property-placeholder/>}在XML配置中自动启用它，
 * 也可以通过专用的{@code static} {@code @Bean}方法在{@code @Configuration}类中显式启用该方法
 * （请参阅{@link Bean @Bean}“关于BeanFactoryPostProcessor-returning {@code @Bean}的javadocs详情）。
 * 但是请注意，通常仅在需要自定义配置（例如占位符语法等）时，才需要通过{@code static} {@code @Bean}方法显式注册
 * {@code PropertySourcesPlaceholderConfigurer}。特别是，如果未注册任何bean后处理器
 * （例如{@code PropertySourcesPlaceholderConfigurer}）作为{@code ApplicationContext}的嵌入式值解析器，
 * Spring将注册一个默认的嵌入式值解析器，该解析器根据{@code Environment}中注册的属性源解析占位符。
 * 请参见下面有关使用{@code @ImportResource}与Spring XML组合{@code @Configuration}类的部分；
 * 参见{@link Value @Value} javadocs;并请参阅{@link Bean @Bean} javadocs，以获取有关使用
 * {@code BeanFactoryPostProcessor}类型（例如{@code PropertySourcesPlaceholderConfigurer}）的详细信息。
 *
 * <h2>Composing {@code @Configuration} classes</h2>
 *
 * <h3>With the {@code @Import} annotation</h3>
 *
 * <p>{@code @Configuration}类可以使用{@link Import @Import}注释组成，类似于{@code <import>}在Spring XML中的工作方式。 
 * 因为{@code @Configuration}对象作为容器内的Spring bean管理，所以可以注入导入的配置-例如，通过构造函数注入：
 *
 * <pre class="code">
 * &#064;Configuration
 * public class DatabaseConfig {
 *
 *     &#064;Bean
 *     public DataSource dataSource() {
 *         // instantiate, configure and return DataSource
 *     }
 * }
 *
 * &#064;Configuration
 * &#064;Import(DatabaseConfig.class)
 * public class AppConfig {
 *
 *     private final DatabaseConfig dataConfig;
 *
 *     public AppConfig(DatabaseConfig dataConfig) {
 *         this.dataConfig = dataConfig;
 *     }
 *
 *     &#064;Bean
 *     public MyBean myBean() {
 *         // reference the dataSource() bean method
 *         return new MyBean(dataConfig.dataSource());
 *     }
 * }</pre>
 *
 * <p>现在，可以通过仅在Spring上下文中注册{@code AppConfig}来启动{@code AppConfig}和导入的{@code DatabaseConfig}：
 *
 * <pre class="code">
 * new AnnotationConfigApplicationContext(AppConfig.class);</pre>
 *
 * <h3>With the {@code @Profile} annotation</h3>
 *
 * <p>{@code @Configuration}类可以用{@link Profile @Profile}批注标记，以指示仅当给定的一个或多个配置文件处于活动状态时才应处理它们：
 *
 * <pre class="code">
 * &#064;Profile("development")
 * &#064;Configuration
 * public class EmbeddedDatabaseConfig {
 *
 *     &#064;Bean
 *     public DataSource dataSource() {
 *         // instantiate, configure and return embedded DataSource
 *     }
 * }
 *
 * &#064;Profile("production")
 * &#064;Configuration
 * public class ProductionDatabaseConfig {
 *
 *     &#064;Bean
 *     public DataSource dataSource() {
 *         // instantiate, configure and return production DataSource
 *     }
 * }</pre>
 *
 * <p>另外，您也可以在{@code @Bean}方法级别声明配置文件条件-例如，对于同一配置类中的可替代bean变体：
 *
 * <pre class="code">
 * &#064;Configuration
 * public class ProfileDatabaseConfig {
 *
 *     &#064;Bean("dataSource")
 *     &#064;Profile("development")
 *     public DataSource embeddedDatabase() { ... }
 *
 *     &#064;Bean("dataSource")
 *     &#064;Profile("production")
 *     public DataSource productionDatabase() { ... }
 * }</pre>
 *
 * <p>See the {@link Profile @Profile} and {@link org.springframework.core.env.Environment}
 * javadocs for further details.
 *
 * <h3>With Spring XML using the {@code @ImportResource} annotation</h3>
 *
 * <p>如上所述，{@code @Configuration}类可以在Spring XML文件中声明为常规的Spring {@code <bean>}定义。 
 * 也可以使用{@link ImportResource @ImportResource}批注将Spring XML配置文件导入{@code @Configuration}类。 
 * 可以注入从XML导入的Bean定义，例如，使用{@code @Inject}批注：
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;ImportResource("classpath:/com/acme/database-config.xml")
 * public class AppConfig {
 *
 *     &#064Inject DataSource dataSource; // from XML
 *
 *     &#064;Bean
 *     public MyBean myBean() {
 *         // inject the XML-defined dataSource bean
 *         return new MyBean(this.dataSource);
 *     }
 * }</pre>
 *
 * <h3>With nested {@code @Configuration} classes</h3>
 *
 * <p>{@code @Configuration}类可以相互嵌套如下：
 *
 * <pre class="code">
 * &#064;Configuration
 * public class AppConfig {
 *
 *     &#064;Inject DataSource dataSource;
 *
 *     &#064;Bean
 *     public MyBean myBean() {
 *         return new MyBean(dataSource);
 *     }
 *
 *     &#064;Configuration
 *     static class DatabaseConfig {
 *         &#064;Bean
 *         DataSource dataSource() {
 *             return new EmbeddedDatabaseBuilder().build();
 *         }
 *     }
 * }</pre>
 *
 * <p>当按这种安排启动时，仅需要针对应用程序上下文注册{@code AppConfig}。 由于是嵌套的{@code @Configuration}类，
 * 因此将自动注册{@code DatabaseConfig}。 当{@code AppConfig}和{@code DatabaseConfig}之间的关系已经隐式清楚时，
 * 这避免了使用{@code @Import}注释的需要。
 *
 * <p>还要注意，嵌套的{@code @Configuration}类可以与{@code @Profile}批注一起使用，
 * 以为封闭的{@code @Configuration}类提供同一bean的两个选项。
 *
 * <h2>配置延迟初始化</h2>
 *
 * <p>默认情况下，{@code @Bean}方法将在容器启动时立即实例化。 为了避免这种情况，{@code @Configuration}
 * 可以与{@link Lazy @Lazy}批注结合使用，以指示默认情况下延迟初始化了类中声明的所有{@code @Bean}方法。 
 * 请注意，{@code @Lazy}也可以在单个{@code @Bean}方法上使用。
 *
 * <h2>Testing support for {@code @Configuration} classes</h2>
 *
 * <p>{@code spring-test}模块中可用的Spring TestContext框架提供{@code @ContextConfiguration}批注，
 * 该批注可以接受一组组件类引用（通常为{@code @Configuration}或{@code @Component}类）。
 *
 * <pre class="code">
 * &#064;RunWith(SpringRunner.class)
 * &#064;ContextConfiguration(classes = {AppConfig.class, DatabaseConfig.class})
 * public class MyTests {
 *
 *     &#064;Autowired MyBean myBean;
 *
 *     &#064;Autowired DataSource dataSource;
 *
 *     &#064;Test
 *     public void test() {
 *         // assertions against myBean ...
 *     }
 * }</pre>
 *
 * <p>See the
 * <a href="https://docs.spring.io/spring/docs/current/spring-framework-reference/testing.html#testcontext-framework">TestContext framework</a>
 * reference documentation for details.
 *
 * <h2>Enabling built-in Spring features using {@code @Enable} annotations</h2>
 *
 * <p>Spring features such as asynchronous method execution, scheduled task execution,
 * annotation driven transaction management, and even Spring MVC can be enabled and
 * configured from {@code @Configuration} classes using their respective "{@code @Enable}"
 * annotations. See
 * {@link org.springframework.scheduling.annotation.EnableAsync @EnableAsync},
 * {@link org.springframework.scheduling.annotation.EnableScheduling @EnableScheduling},
 * {@link org.springframework.transaction.annotation.EnableTransactionManagement @EnableTransactionManagement},
 * {@link org.springframework.context.annotation.EnableAspectJAutoProxy @EnableAspectJAutoProxy},
 * and {@link org.springframework.web.servlet.config.annotation.EnableWebMvc @EnableWebMvc}
 * for details.
 *
 * <h2>Constraints when authoring {@code @Configuration} classes</h2>
 *
 * <ul>
 * <li>Configuration classes must be provided as classes (i.e. not as instances returned
 * from factory methods), allowing for runtime enhancements through a generated subclass.
 * <li>Configuration classes must be non-final (allowing for subclasses at runtime),
 * unless the {@link #proxyBeanMethods() proxyBeanMethods} flag is set to {@code false}
 * in which case no runtime-generated subclass is necessary.
 * <li>Configuration classes must be non-local (i.e. may not be declared within a method).
 * <li>Any nested configuration classes must be declared as {@code static}.
 * <li>{@code @Bean} methods may not in turn create further configuration classes
 * (any such instances will be treated as regular beans, with their configuration
 * annotations remaining undetected).
 * </ul>
 *
 * @author Rod Johnson
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.0
 * @see Bean
 * @see Profile
 * @see Import
 * @see ImportResource
 * @see ComponentScan
 * @see Lazy
 * @see PropertySource
 * @see AnnotationConfigApplicationContext
 * @see ConfigurationClassPostProcessor
 * @see org.springframework.core.env.Environment
 * @see org.springframework.test.context.ContextConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Configuration {

	/**
	 * 明确指定与{@code @Configuration}类关联的Spring bean定义的名称。 如果未指定（通常情况），将自动生成Bean名称。
	 * <p>仅当通过组件扫描选择{@code @Configuration}类或将其直接提供给{@link AnnotationConfigApplicationContext}时，才应用自定义名称。 
	 * 如果{@code @Configuration}类注册为传统XML bean定义，则bean元素的名称/id将优先。
	 * @return the explicit component name, if any (or empty String otherwise)
	 * @see AnnotationBeanNameGenerator
	 */
	@AliasFor(annotation = Component.class)
	String value() default "";

	/**
	 * 指定是否应代理{@code @Bean}方法以强制执行bean生命周期行为，例如 即使在用户代码中
	 * 直接{@code @Bean}方法调用的情况下，返回共享的单例bean实例。 此功能需要通过运行时生成的CGLIB子类来实现方法拦截，
	 * 该子类具有一些限制，例如配置类及其方法不允许声明为{@code final}。
	 * 默认值为{@code true}，它允许通过配置类中的直接方法调用进行'bean间引用'，以及允许对此配置的{@code @Bean}方法进行外部调用。 
	 * 例如，从另一个配置类。 如果由于每个特定配置的{@code @Bean}方法都是自包含的并且被设计为供容器使用的普通工厂方法，
	 * 则不需要这样做，请将此标志切换为{@code false}，以避免处理CGLIB子类。
	 * <p>关闭bean方法拦截可以有效地单独处理{@code @Bean}方法，就像在非@Configuration类（也称为“ @Bean Lite模式”）
	 * 上声明时一样（请参阅{@link Bean @Bean's javadoc}）。 因此，从行为上讲，它等效于删除{@code @Configuration}注释。
	 * <p>有了 proxyBeanMethods 属性后，配置类不会被代理了。主要是为了提高性能，
	 * 如果你的 @Bean 方法之间没有调用关系的话可以把 proxyBeanMethods 设置为 false。否则，
	 * 方法内部引用的类生产的类和 Spring 容器中类是两个类。
	 * @since 5.2
	 */
	boolean proxyBeanMethods() default true;

}
