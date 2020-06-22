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

import org.springframework.context.weaving.DefaultContextLoadTimeWeaver;
import org.springframework.instrument.classloading.LoadTimeWeaver;

/**
 * 为此应用程序上下文激活一个Spring {@link LoadTimeWeaver}，它可以获得一个名为"loadTimeWeaver"的bean，
 * 类似于Spring XML中的{@code <context:load-time-weaver>}元素。
 *
 * <p>用于@{@link org.springframework.context.annotation.Configuration Configuration}类； 最简单的示例如下：
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableLoadTimeWeaving
 * public class AppConfig {
 *
 *     // application-specific &#064;Bean definitions ...
 * }</pre>
 *
 * 上面的示例等效于以下Spring XML配置：
 *
 * <pre class="code">
 * &lt;beans&gt;
 *
 *     &lt;context:load-time-weaver/&gt;
 *
 *     &lt;!-- application-specific &lt;bean&gt; definitions --&gt;
 *
 * &lt;/beans&gt;
 * </pre>
 *
 * <h2>The {@code LoadTimeWeaverAware} interface</h2>
 * 任何实现{@link org.springframework.context.weaving.LoadTimeWeaverAware LoadTimeWeaverAware}
 * 接口的bean都将自动接收{@code LoadTimeWeaver}引用。 例如，Spring的JPA启动支持。
 *
 * <h2>Customizing the {@code LoadTimeWeaver}</h2>
 * 默认的编织器是自动确定的：请参见{@link DefaultContextLoadTimeWeaver}。
 *
 * <p>为了使用自定义的Weaver，用{@code @EnableLoadTimeWeaving}注释的{@code @Configuration}
 * 类还可以实现{@link LoadTimeWeavingConfigurer}接口，并通过{@code #getLoadTimeWeaver}
 * 方法返回自定义的{@code LoadTimeWeaver}实例：
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableLoadTimeWeaving
 * public class AppConfig implements LoadTimeWeavingConfigurer {
 *
 *     &#064;Override
 *     public LoadTimeWeaver getLoadTimeWeaver() {
 *         MyLoadTimeWeaver ltw = new MyLoadTimeWeaver();
 *         ltw.addClassTransformer(myClassFileTransformer);
 *         // ...
 *         return ltw;
 *     }
 * }</pre>
 *
 * <p>可以将上面的示例与以下Spring XML配置进行比较：
 *
 * <pre class="code">
 * &lt;beans&gt;
 *
 *     &lt;context:load-time-weaver weaverClass="com.acme.MyLoadTimeWeaver"/&gt;
 *
 * &lt;/beans&gt;
 * </pre>
 *
 * <p>该代码示例与XML示例的不同之处在于，它实际上实例化了{@code MyLoadTimeWeaver}类型，
 * 这意味着它还可以配置实例，例如 调用{@code #addClassTransformer}方法。 
 * 这说明了基于代码的配置方法通过直接编程访问如何更加灵活。
 *
 * <h2>Enabling AspectJ-based weaving</h2>
 * 可以使用{@link #aspectjWeaving()}属性启用AspectJ加载时编织，这将导致通过{@link LoadTimeWeaver#addTransformer}
 * 注册{@linkplain org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter AspectJ 类转换器}。 
 * 如果类路径中存在"META-INF/aop.xml"资源，则默认情况下将激活AspectJ编织。 例：
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableLoadTimeWeaving(aspectjWeaving=ENABLED)
 * public class AppConfig {
 * }</pre>
 *
 * <p>可以将上面的示例与以下Spring XML配置进行比较：
 *
 * <pre class="code">
 * &lt;beans&gt;
 *
 *     &lt;context:load-time-weaver aspectj-weaving="on"/&gt;
 *
 * &lt;/beans&gt;
 * </pre>
 *
 * <p>这两个示例等效，但有一个重大例外：在XML情况下，当{@code aspectj-weaving}为"on"时，
 * 隐式启用{@code <context:spring-configured>}的功能。 
 * 使用{@code @EnableLoadTimeWeaving(aspectjWeaving=ENABLED)}时不会发生这种情况。 
 * 相反，您必须显式添加{@code @EnableSpringConfigured}（包含在{@code spring-aspects}模块中）
 *
 * @author Chris Beams
 * @since 3.1
 * @see LoadTimeWeaver
 * @see DefaultContextLoadTimeWeaver
 * @see org.aspectj.weaver.loadtime.ClassPreProcessorAgentAdapter
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LoadTimeWeavingConfiguration.class)
public @interface EnableLoadTimeWeaving {

	/**
	 * Whether AspectJ weaving should be enabled.
	 */
	AspectJWeaving aspectjWeaving() default AspectJWeaving.AUTODETECT;


	/**
	 * AspectJ weaving enablement options.
	 */
	enum AspectJWeaving {

		/**
		 * Switches on Spring-based AspectJ load-time weaving.
		 */
		ENABLED,

		/**
		 * Switches off Spring-based AspectJ load-time weaving (even if a
		 * "META-INF/aop.xml" resource is present on the classpath).
		 */
		DISABLED,

		/**
		 * Switches on AspectJ load-time weaving if a "META-INF/aop.xml" resource
		 * is present in the classpath. If there is no such resource, then AspectJ
		 * load-time weaving will be switched off.
		 */
		AUTODETECT;
	}

}
