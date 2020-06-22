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

package org.springframework.web;

import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;

import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

/**
 * Servlet 3.0 {@link ServletContainerInitializer}设计为
 * 使用Spring的{@link WebApplicationInitializer} SPI支持Servlet容器的基于代码的配置，
 * 这与传统的基于web.xml的方法相反（或可能与之结合）。
 *
 * <h2>运行机制</h2>
 * 此类将被加载和实例化，并在容器启动期间由任何符合Servlet 3.0的容器调用其{@link #onStartup}方法，
 * 并假设该{@code spring-web}模块JAR存在于类路径中。 
 * 这是通过JAR Services API {@link ServiceLoader#load(Class)}方法来检测{@code spring-web}模块的
 * {@code META-INF/services/javax.servlet.ServletContainerInitializer}服务提供程序配置文件而发生的。 
 * 有关完整的详细信息，请参见
 * <a href="https://download.oracle.com/javase/6/docs/technotes/guides/jar/jar.html#Service%20Provider">JAR Services API文档</a>
 * 以及Servlet 3.0最终草案规范的8.2.4节。
 *
 * <h3>与{@code web.xml}结合</h3>
 * Web应用程序可以选择通过限制{@code web.xml}中的{@code metadata-complete}
 * 属性来控制Servlet容器在启动时扫描类路径的数量，该属性控制Servlet注释的扫描，
 * 或者也可以通过{@code web.xml}中的{@code <absolute-ordering>}元素进行控制 ，
 * 它控制允许哪些Web片段（即jars）执行{@code ServletContainerInitializer}扫描。 
 * 使用此功能时，可以通过如下方式启用{@link SpringServletContainerInitializer}：
 * 将"spring_web"添加到{@code web.xml}中的命名Web片段列表中，如下所示：
 *
 * <pre class="code">
 * &lt;absolute-ordering&gt;
 *   &lt;name>some_web_fragment&lt;/name&gt;
 *   &lt;name>spring_web&lt;/name&gt;
 * &lt;/absolute-ordering&gt;
 * </pre>
 *
 * <h2>与Spring的{@code WebApplicationInitializer}的关系</h2>
 * Spring的{@code WebApplicationInitializer} SPI仅包含一种方法：{@link WebApplicationInitializer#onStartup(ServletContext)}。
 * 签名故意与{@link ServletContainerInitializer#onStartup(Set, ServletContext)}非常相似：
 * 简而言之，{@code SpringServletContainerInitializer}负责实例化{@code ServletContext}并将其委托给任何用户定义的
 * {@code WebApplicationInitializer}实现。 然后，每个{@code WebApplicationInitializer}都有责任完成初始化{@code ServletContext}的实际工作。
 * 委派的确切过程在下面的{@link #onStartup onStartup}文档中详细描述。
 *
 * <h2>一般注意事项</h2>
 * 通常，应将此类视为更重要且面向用户的{@code WebApplicationInitializer} SPI的支持基础结构。 
 * 利用此容器初始化程序也是完全可选的：尽管确实会在所有Servlet 3.0+运行时下加载并调用此初始化程序，
 * 但用户仍可以选择是否在类路径上提供任何{@code WebApplicationInitializer}实现。 
 * 如果未检测到{@code WebApplicationInitializer}类型，则此容器初始化程序将无效。
 *
 * <p>请注意，除了包含{@code spring-web}模块JAR到类路径之外，
 * 使用此容器初始化程序和WebApplicationInitializer都不会“绑定”到Spring MVC。 
 * 它们可以被认为有助于{@code ServletContext}的基于代码的便捷配置。 
 * 换句话说，任何servlet, listener, 或者 filter都可以在{@code WebApplicationInitializer}中注册，
 * 而不仅仅是Spring MVC特定的组件。
 *
 * <p>此类既不是为扩展而设计的，也不是旨在扩展的。 
 * 应该将其视为内部类型，{@code WebApplicationInitializer}是面向公众的SPI。
 *
 * <h2>See Also</h2>
 * See {@link WebApplicationInitializer} Javadoc for examples and detailed usage
 * recommendations.<p>
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @author Rossen Stoyanchev
 * @since 3.1
 * @see #onStartup(Set, ServletContext)
 * @see WebApplicationInitializer
 */
@HandlesTypes(WebApplicationInitializer.class)
public class SpringServletContainerInitializer implements ServletContainerInitializer {

	/**
	 * Delegate the {@code ServletContext} to any {@link WebApplicationInitializer}
	 * implementations present on the application classpath.
	 * <p>Because this class declares @{@code HandlesTypes(WebApplicationInitializer.class)},
	 * Servlet 3.0+ containers will automatically scan the classpath for implementations
	 * of Spring's {@code WebApplicationInitializer} interface and provide the set of all
	 * such types to the {@code webAppInitializerClasses} parameter of this method.
	 * <p>If no {@code WebApplicationInitializer} implementations are found on the classpath,
	 * this method is effectively a no-op. An INFO-level log message will be issued notifying
	 * the user that the {@code ServletContainerInitializer} has indeed been invoked but that
	 * no {@code WebApplicationInitializer} implementations were found.
	 * <p>Assuming that one or more {@code WebApplicationInitializer} types are detected,
	 * they will be instantiated (and <em>sorted</em> if the @{@link
	 * org.springframework.core.annotation.Order @Order} annotation is present or
	 * the {@link org.springframework.core.Ordered Ordered} interface has been
	 * implemented). Then the {@link WebApplicationInitializer#onStartup(ServletContext)}
	 * method will be invoked on each instance, delegating the {@code ServletContext} such
	 * that each instance may register and configure servlets such as Spring's
	 * {@code DispatcherServlet}, listeners such as Spring's {@code ContextLoaderListener},
	 * or any other Servlet API componentry such as filters.
	 * @param webAppInitializerClasses all implementations of
	 * {@link WebApplicationInitializer} found on the application classpath
	 * @param servletContext the servlet context to be initialized
	 * @see WebApplicationInitializer#onStartup(ServletContext)
	 * @see AnnotationAwareOrderComparator
	 */
	@Override
	public void onStartup(@Nullable Set<Class<?>> webAppInitializerClasses, ServletContext servletContext)
			throws ServletException {

		List<WebApplicationInitializer> initializers = new LinkedList<>();

		if (webAppInitializerClasses != null) {
			for (Class<?> waiClass : webAppInitializerClasses) {
				// Be defensive: Some servlet containers provide us with invalid classes,
				// no matter what @HandlesTypes says...
				if (!waiClass.isInterface() && !Modifier.isAbstract(waiClass.getModifiers()) &&
						WebApplicationInitializer.class.isAssignableFrom(waiClass)) {
					try {
						// 收集启动器实例
						initializers.add((WebApplicationInitializer)
								ReflectionUtils.accessibleConstructor(waiClass).newInstance());
					}
					catch (Throwable ex) {
						throw new ServletException("Failed to instantiate WebApplicationInitializer class", ex);
					}
				}
			}
		}

		if (initializers.isEmpty()) {
			servletContext.log("No Spring WebApplicationInitializer types detected on classpath");
			return;
		}

		servletContext.log(initializers.size() + " Spring WebApplicationInitializers detected on classpath");
		AnnotationAwareOrderComparator.sort(initializers);
		// 依次调用初始化器
		for (WebApplicationInitializer initializer : initializers) {
			initializer.onStartup(servletContext);
		}
	}

}
