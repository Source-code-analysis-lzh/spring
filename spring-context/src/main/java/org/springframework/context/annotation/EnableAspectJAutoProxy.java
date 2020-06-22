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

/**
 * 支持处理标有AspectJ的{@code @Aspect}批注的组件，类似于Spring的{@code <aop:aspectj-autoproxy>} XML元素中的功能。 
 * 要在@{@link Configuration}类上使用，如下所示：
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableAspectJAutoProxy
 * public class AppConfig {
 *
 *     &#064;Bean
 *     public FooService fooService() {
 *         return new FooService();
 *     }
 *
 *     &#064;Bean
 *     public MyAspect myAspect() {
 *         return new MyAspect();
 *     }
 * }</pre>
 *
 * 其中{@code FooService}是典型的POJO组件，而{@code MyAspect}是{@code @Aspect}样式的方面：
 *
 * <pre class="code">
 * public class FooService {
 *
 *     // various methods
 * }</pre>
 *
 * <pre class="code">
 * &#064;Aspect
 * public class MyAspect {
 *
 *     &#064;Before("execution(* FooService+.*(..))")
 *     public void advice() {
 *         // advise FooService methods as appropriate
 *     }
 * }</pre>
 *
 * 在上面的场景中，{@code @EnableAspectJAutoProxy}确保{@code MyAspect}将被正确处理，
 * 并且{@code FooService}将被代理混合到它所提供的advice中。
 *
 * <p>用户可以使用{@link #proxyTargetClass()}属性控制为{@code FooService}创建的代理类型。 
 * 与默认的基于接口的JDK代理方法相反，以下启用了CGLIB风格的“子类”代理。
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableAspectJAutoProxy(proxyTargetClass=true)
 * public class AppConfig {
 *     // ...
 * }</pre>
 *
 * <p>请注意，{@code @Aspect} bean可以像其它任何组件一样进行组件扫描。 
 * 只需使用{@code @Aspect}和{@code @Component}标记方面：
 *
 * <pre class="code">
 * package com.foo;
 *
 * &#064;Component
 * public class FooService { ... }
 *
 * &#064;Aspect
 * &#064;Component
 * public class MyAspect { ... }</pre>
 *
 * 然后使用@{@link ComponentScan}注释进行选择：
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;ComponentScan("com.foo")
 * &#064;EnableAspectJAutoProxy
 * public class AppConfig {
 *
 *     // no explicit &#064Bean definitions required
 * }</pre>
 *
 * <b>注意：{@code @EnableAspectJAutoProxy}仅适用于其本地应用程序上下文，允许在不同级别选择性地代理Bean。 
 * 请在每个单独的上下文中重新声明{@code @EnableAspectJAutoProxy}，例如 公共根Web应用程序上下文以及
 * 任何单独的{@code DispatcherServlet}应用程序上下文（如果需要在多个级别上应用其行为）。
 *
 * <p>此功能要求在类路径上存在@code aspectjweaver}。 虽然通常对于{@code spring-aop}该依赖项是可选的，
 * 但{@code @EnableAspectJAutoProxy}及其底层设施则需要此依赖项。
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see org.aspectj.lang.annotation.Aspect
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AspectJAutoProxyRegistrar.class)
public @interface EnableAspectJAutoProxy {

	/**
	 * 指示与基于标准Java接口的代理相反，是否要创建基于子类（CGLIB）的代理。 默认为{@code false}。
	 */
	boolean proxyTargetClass() default false;

	/**
	 * 指示代理应由AOP框架作为{@code ThreadLocal}公开，以便通过{@link org.springframework.aop.framework.AopContext}类进行检索。 
	 * 默认情况下处于关闭状态，即无法保证{@code AopContext}访问将正常进行。
	 * @since 4.3.1
	 */
	boolean exposeProxy() default false;

}
