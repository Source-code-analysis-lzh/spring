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

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.core.annotation.AliasFor;

/**
 * 指示方法产生一个由Spring容器管理的bean。
 *
 * <h3>总览</h3>
 *
 * <p>该注释的属性的名称和语义故意类似于Spring XML模式中的{@code <bean/>}元素。 例如：
 *
 * <pre class="code">
 *     &#064;Bean
 *     public MyBean myBean() {
 *         // instantiate and configure MyBean obj
 *         return obj;
 *     }
 * </pre>
 *
 * <h3>Bean Names</h3>
 *
 * <p>虽然{@link #name}属性可用，但确定bean名称的默认策略是使用{@code @Bean}方法的名称。 
 * 这是方便且直观的，但是如果需要显式命名，则可以使用{@code name}属性（或其别名{@code value}）。 
 * 还要注意，{@code name}接受一个字符串数组，允许为单个bean使用多个名称（即主bean名称加上一个或多个别名）。
 *
 * <pre class="code">
 *     &#064;Bean({"b1", "b2"}) // bean available as 'b1' and 'b2', but not 'myBean'
 *     public MyBean myBean() {
 *         // instantiate and configure MyBean obj
 *         return obj;
 *     }
 * </pre>
 *
 * <h3>Profile, Scope, Lazy, DependsOn, Primary, Order</h3>
 *
 * <p>请注意，{@code @Bean}注释不提供概要文件(profile)，范围(scope)，懒加载(lazy)，依赖项(depends-on)或主要(primary)属性。 
 * 相反，它应与{@link Scope @Scope}, {@link Lazy @Lazy}, {@link DependsOn @DependsOn}
 * 和{@link Primary @Primary}注释结合使用以声明这些语义。 例如：
 *
 * <pre class="code">
 *     &#064;Bean
 *     &#064;Profile("production")
 *     &#064;Scope("prototype")
 *     public MyBean myBean() {
 *         // instantiate and configure MyBean obj
 *         return obj;
 *     }
 * </pre>
 *
 * 上述注释的语义在组件类级别与它们的用法匹配：{@code @Profile}允许选择性地包含某些bean。 
 * {@code @Scope}将bean的范围从单例更改为指定的范围。 {@code @Lazy}仅在默认单例作用域的情况下才具有实际效果。 
 * {@code @DependsOn}会在创建该bean之前强制创建特定的其它bean，此外，bean通过直接引用表示的任何依赖关系
 * 通常对单例启动很有帮助。 {@code @Primary}是一种机制，用于在注入点级别解决歧义性（
 * 如果需要注入单个目标组件，但多个bean按类型匹配。）
 *
 * <p>此外，{@code @Bean}方法还可以声明限定符注释和{@link org.springframework.core.annotation.Order @Order}值，
 * 以便在注入点解析期间将其考虑在内，就像在相应组件类上的相应注释一样，但每个bean定义可能非常个别
 * （如果使用多个相同的定义bean类）。 在初始类型匹配之后，限定词会缩小候选集的范围； 
 * order值确定在收集注入点的情况下解析元素的顺序（几个目标bean通过类型和限定符进行匹配）。
 *
 * <p>注意：{@code @Order}值可能会影响注入点的优先级，但请注意它们不会影响单例启动顺序，
 * 这是由依赖关系和@DependsOn声明确定的正交关系。 同样，由于不能在方法上声明{@link javax.annotation.Priority}，
 * 因此该级别上不可用。 它的相同的语义可以通过在每种类型的单个bean上使用{@code @Order}与{@code @Primary}组合达成。
 *
 * <h3>{@code @Bean} Methods in {@code @Configuration} Classes</h3>
 *
 * <p>通常，{@code @Bean}方法在{@code @Configuration}类中声明。 
 * 在这种情况下，bean方法可以通过直接调用它们来引用同一类中的其它{@code @Bean}方法。 
 * 这确保了bean之间的引用是强类型的且可导航的。 保证此类所谓的“ bean间引用”遵循作用域和AOP语义，
 * 就像{@code getBean()}查找一样。 这些是从原始“ Spring JavaConfig”项目中已知的语义，
 * 这些语义要求在运行时将每个此类配置类的CGLIB子类化。 因此，在此模式下，
 * 不得将{@code @Configuration}类及其工厂方法标记为final或private。 例如：
 *
 * <pre class="code">
 * &#064;Configuration
 * public class AppConfig {
 *
 *     &#064;Bean
 *     public FooService fooService() {
 *         return new FooService(fooRepository());
 *     }
 *
 *     &#064;Bean
 *     public FooRepository fooRepository() {
 *         return new JdbcFooRepository(dataSource());
 *     }
 *
 *     // ...
 * }</pre>
 *
 * <h3>{@code @Bean} <em>Lite</em> Mode</h3>
 *
 * <p>{@code @Bean}方法也可以在未使用{@code @Configuration}注释的类中声明。 例如，
 * 可以在{@code @Component}类中甚至在普通的旧类中声明bean方法。 在这种情况下，{@code @Bean}
 * 方法将以所谓的“精简”模式进行处理。
 *
 * <p>容器将精简模式下的Bean方法视为普通工厂方法（类似于XML中的工厂方法声明），并适当地应用了作用域和生命周期回调。 
 * 在这种情况下，包含类保持不变，并且包含类或工厂方法没有特别约束。
 *
 * <p>与{@code @Configuration}类中bean方法的语义相反，精简模式不支持“ bean间引用”。 
 * 相反，当一个{@code @Bean}方法在精简模式下调用另一个{@code @Bean}方法时，该调用是标准的Java方法调用。 
 * Spring不会通过CGLIB代理拦截调用。 这类似于内部{@code @Transactional}方法调用
 * （在代理模式下，Spring不会拦截调用-Spring仅在AspectJ模式下会拦截调用。）
 *
 * <p>For example:
 *
 * <pre class="code">
 * &#064;Component
 * public class Calculator {
 *     public int sum(int a, int b) {
 *         return a+b;
 *     }
 *
 *     &#064;Bean
 *     public MyBean myBean() {
 *         return new MyBean();
 *     }
 * }</pre>
 *
 * <h3>Bootstrapping</h3>
 *
 * <p>See the @{@link Configuration} javadoc for further details including how to bootstrap
 * the container using {@link AnnotationConfigApplicationContext} and friends.
 *
 * <h3>{@code BeanFactoryPostProcessor}-returning {@code @Bean} methods</h3>
 *
 * <p>对于返回Spring {@link org.springframework.beans.factory.config.BeanFactoryPostProcessor BeanFactoryPostProcessor}
 * （BFPP）类型的{@code @Bean}方法，必须特别注意。 因为{@code BFPP}对象必须在容器生命周期的早期就实例化，
 * 所以它们会干扰{@code @Configuration}类中的{@code @Autowired}，{@code @Value}和{@code @PostConstruct}等注释的处理。 
 * 为了避免这些生命周期问题，请将{@code BFPP}返回的{@code @Bean}方法标记为{@code static}。 例如：
 *
 * <pre class="code">
 *     &#064;Bean
 *     public static PropertySourcesPlaceholderConfigurer pspc() {
 *         // instantiate, configure and return pspc ...
 *     }
 * </pre>
 *
 * 通过将此方法标记为{@code static}，可以在不引起其声明{@code @Configuration}类实例化的情况下调用该方法，
 * 从而避免了上述生命周期冲突。 但是请注意，如上所述，{@code static} {@code @Bean}方法不会针对范围和AOP语义进行增强。 
 * 这在{@code BFPP}情况下可行，因为其它{@code @Bean}方法通常不引用它们。 提醒一下，
 * 将为具有返回类型属于{@code BeanFactoryPostProcessor}类型的任何非静态{@code @Bean}方法发出WARN级别的日志消息。
 *
 * @author Rod Johnson
 * @author Costin Leau
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 3.0
 * @see Configuration
 * @see Scope
 * @see DependsOn
 * @see Lazy
 * @see Primary
 * @see org.springframework.stereotype.Component
 * @see org.springframework.beans.factory.annotation.Autowired
 * @see org.springframework.beans.factory.annotation.Value
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {

	/**
	 * Alias for {@link #name}.
	 * 旨在在不需要其它属性时使用，例如：{@code @Bean("customBeanName")}。
	 * @since 4.3.3
	 * @see #name
	 */
	@AliasFor("name")
	String[] value() default {};

	/**
	 * 此Bean的名称，或者，如果有多个名称，则为主Bean名称加别名。
	 * <p>如果未指定，则Bean的名称为带注释的方法的名称。 如果指定，则使用指定的名称。
	 * <p>如果未声明其它属性，也可以通过{@link #value}属性配置Bean名称和别名。
	 * @see #value
	 */
	@AliasFor("value")
	String[] name() default {};

	/**
	 * 是否通过名称或类型通过基于约定的自动装配来注入依赖项？
	 * <p>Note that this autowire mode is just about externally driven autowiring based
	 * on bean property setter methods by convention, analogous to XML bean definitions.
	 * <p>请注意，这种自动装配模式只是基于约定的基于bean属性设置器方法的外部驱动自动装配，类似于XML bean定义。
	 * <p>The default mode does allow for annotation-driven autowiring. "no" refers to
	 * externally driven autowiring only, not affecting any autowiring demands that the
	 * bean class itself expresses through annotations.
	 * <p>默认模式确实允许注释驱动的自动装配。 "no"仅指外部驱动的自动装配，不影响bean类本身通过注释表示的任何自动装配要求。
	 * @see Autowire#BY_NAME
	 * @see Autowire#BY_TYPE
	 * @deprecated as of 5.1, since {@code @Bean} factory method argument resolution and
	 * {@code @Autowired} processing supersede name/type-based bean property injection
	 */
	@Deprecated
	Autowire autowire() default Autowire.NO;

	/**
	 * 该bean是否适合自动连接到其它bean？
	 * <p>Default is {@code true}; set this to {@code false} for internal delegates
	 * that are not meant to get in the way of beans of the same type in other places.
	 * <p>默认为{@code true}； 对于不打算在其它地方妨碍相同类型的bean的内部委托，请将其设置为{@code false}。
	 * @since 5.1
	 */
	boolean autowireCandidate() default true;

	/**
	 * 初始化期间在Bean实例上调用的方法的可选名称。 鉴于可以在Bean注释方法的主体内直接以编程方式调用该方法，因此不常用。
	 * <p>The default value is {@code ""}, indicating no init method to be called.
	 * @see org.springframework.beans.factory.InitializingBean
	 * @see org.springframework.context.ConfigurableApplicationContext#refresh()
	 */
	String initMethod() default "";

	/**
	 * 关闭应用程序上下文时要在Bean实例上调用的方法的可选名称，例如JDBC {@code DataSource}实现上的{@code close()}方法
	 * 或Hibernate {@code SessionFactory}对象。 该方法必须没有参数，但可以引发任何异常。
	 * <p>为了方便用户，容器将尝试针对{@code @Bean}方法返回的对象推断一个destroy方法。 
	 * 例如，给定{@code @Bean}方法返回Apache Commons DBCP {@code BasicDataSource}，
	 * 容器将注意到该对象上可用的{@code close()}方法，并自动将其注册为{@code destroyMethod}。 
	 * 目前，这种“销毁方法推论”仅限于仅检测名为“close”或“shutdown”的公共无参数方法。 
	 * 可以在继承层次结构的任何级别上声明该方法，并且无论{@code @Bean}方法的返回类型如何，
	 * 都将对其进行检测（即，检测是在创建时针对bean实例自身进行反射性进行的）。
	 * <p>要禁用特定{@code @Bean}的destroy方法推断，请指定一个空字符串作为值，例如 {@code @Bean(destroyMethod="")}。 
	 * 请注意，仍然会检测到{@link org.springframework.beans.factory.DisposableBean}回调接口并调用相应的destroy方法：
	 * 换句话说，{@code destroyMethod=""}仅影响自定义的close / shutdown方法和
	 * {@link java.io.Closeable}/{@link java.lang.AutoCloseable}声明的close方法。
	 * <p>Note: Only invoked on beans whose lifecycle is under the full control of the
	 * factory, which is always the case for singletons but not guaranteed for any
	 * other scope.
	 * <p>注意：仅在生命周期在工厂的完全控制下的bean上调用，对于单例来说总是这样，但对于任何其它范围都不能保证。
	 * @see org.springframework.beans.factory.DisposableBean
	 * @see org.springframework.context.ConfigurableApplicationContext#close()
	 */
	String destroyMethod() default AbstractBeanDefinition.INFER_METHOD;

}
