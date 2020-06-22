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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * 在Servlet 3.0+环境中实现的接口，以便以编程方式配置{@link ServletContext}-与传统的基于{@code web.xml}的方法相反（或可能与之结合）。
 *
 * <p>{@link SpringServletContainerInitializer}会自动检测到此SPI的实现，而它本身会被任何Servlet 3.0容器自动启动。 
 * 有关此自举机制的详细信息，请参见其{@linkplain SpringServletContainerInitializer its Javadoc}。
 *
 * <h2>例子</h2>
 * <h3>传统的，基于XML方法</h3>
 * 构建Web应用程序的大多数Spring用户将需要注册Spring的{@code DispatcherServlet}。 作为参考，通常在WEB-INF/web.xml中执行以下操作：
 * <pre class="code">
 * &lt;servlet&gt;
 *   &lt;servlet-name>dispatcher&lt;/servlet-name&gt;
 *   &lt;servlet-class&gt;
 *     org.springframework.web.servlet.DispatcherServlet
 *   &lt;/servlet-class&gt;
 *   &lt;init-param>
 *     &lt;param-name>contextConfigLocation&lt;/param-name&gt;
 *     &lt;param-value>/WEB-INF/spring/dispatcher-config.xml&lt;/param-value&gt;
 *   &lt;/init-param&gt;
 *   &lt;load-on-startup>1&lt;/load-on-startup&gt;
 * &lt;/servlet&gt;
 *
 * &lt;servlet-mapping&gt;
 *   &lt;servlet-name&gt;dispatcher&lt;/servlet-name&gt;
 *   &lt;url-pattern&gt;/&lt;/url-pattern&gt;
 * &lt;/servlet-mapping&gt;</pre>
 *
 * <h3>{@code WebApplicationInitializer}的基于代码的方法</h3>
 * 这是{@code WebApplicationInitializer}样式的等效{@code DispatcherServlet}注册逻辑：
 * <pre class="code">
 * public class MyWebAppInitializer implements WebApplicationInitializer {
 *
 *    &#064;Override
 *    public void onStartup(ServletContext container) {
 *      XmlWebApplicationContext appContext = new XmlWebApplicationContext();
 *      appContext.setConfigLocation("/WEB-INF/spring/dispatcher-config.xml");
 *
 *      ServletRegistration.Dynamic dispatcher =
 *        container.addServlet("dispatcher", new DispatcherServlet(appContext));
 *      dispatcher.setLoadOnStartup(1);
 *      dispatcher.addMapping("/");
 *    }
 *
 * }</pre>
 *
 * As an alternative to the above, you can also extend from {@link
 * org.springframework.web.servlet.support.AbstractDispatcherServletInitializer}.
 * 作为上述替代方案，您还可以从{@link org.springframework.web.servlet.support.AbstractDispatcherServletInitializer}扩展。 
 * 
 * 如您所见，多亏了Servlet 3.0的新{@link ServletContext#addServlet}方法，我们实际上正在注册DispatcherServlet的实例，
 * 这意味着现在可以处理{@code DispatcherServlet}了。 像其它任何对象一样-在这种情况下，接收其应用程序上下文的构造方法注入。
 *
 * <p>这种样式既简单又简洁。 不用担心处理init-params等问题，只需要处理普通的JavaBean样式的属性和构造函数参数即可。 
 * 在将它们注入{@code DispatcherServlet}之前，您可以根据需要自由创建和使用Spring应用程序上下文。
 *
 * <p>大多数主要的Spring Web组件已更新为支持这种注册样式。 
 * 您会发现{@code DispatcherServlet}，{@code FrameworkServlet}，
 * {@code ContextLoaderListener}和{@code DelegatingFilterProxy}现在都支持构造函数参数。 
 * 即使未针对{@code WebApplicationInitializers}中使用的组件（例如非Spring，其他第三方）进行专门更新，
 * 也仍然可以在任何情况下使用它们。 Servlet 3.0 {@code ServletContext} API允许以编程方式设置init-params，context-params等。
 *
 * <h2>100％基于代码的配置方法</h2>
 * 在上面的示例中，成功用{@code WebApplicationInitializer}形式的代码替换了{@code WEB-INF/web.xml}，
 * 但是实际的{@code dispatcher-config.xml} Spring配置仍然基于XML。 
 * {@code WebApplicationInitializer}非常适合与Spring的基于代码的{@code @Configuration}类一起使用。 
 * 有关完整的详细信息，请参见@{@link org.springframework.context.annotation.Configuration Configuration} Javadoc，
 * 但是以下示例演示了重构以使用Spring的{@link org.springframework.web.context.support.AnnotationConfigWebApplicationContext
 * AnnotationConfigWebApplicationContext}代替{@code XmlWebApplicationContext}
 * 以及用户定义的{@code @Configuration}类{@code AppConfig}和{@code DispatcherConfig}而不是Spring XML文件。 
 * 此示例还超出了上面的示例，以演示“根”应用程序上下文的典型配置以及{@code ContextLoaderListener}的注册：
 * <pre class="code">
 * public class MyWebAppInitializer implements WebApplicationInitializer {
 *
 *    &#064;Override
 *    public void onStartup(ServletContext container) {
 *      // Create the 'root' Spring application context
 *      AnnotationConfigWebApplicationContext rootContext =
 *        new AnnotationConfigWebApplicationContext();
 *      rootContext.register(AppConfig.class);
 *
 *      // Manage the lifecycle of the root application context
 *      container.addListener(new ContextLoaderListener(rootContext));
 *
 *      // Create the dispatcher servlet's Spring application context
 *      AnnotationConfigWebApplicationContext dispatcherContext =
 *        new AnnotationConfigWebApplicationContext();
 *      dispatcherContext.register(DispatcherConfig.class);
 *
 *      // Register and map the dispatcher servlet
 *      ServletRegistration.Dynamic dispatcher =
 *        container.addServlet("dispatcher", new DispatcherServlet(dispatcherContext));
 *      dispatcher.setLoadOnStartup(1);
 *      dispatcher.addMapping("/");
 *    }
 *
 * }</pre>
 *
 * 作为上述替代方案，您还可以从{@link
 * org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer}进行扩展。 
 * 
 * 请记住，{@code WebApplicationInitializer}实现是自动检测到的，因此您可以随意将其打包到应用程序中。
 *
 * <h2>{@code WebApplicationInitializer}执行顺序</h2>
 * {@code WebApplicationInitializer}实现可以选择在类级别使用Spring的
 * @{@link org.springframework.core.annotation.Order Order}注释进行注释，
 * 或者可以实现Spring的{@link org.springframework.core.Ordered Ordered}接口。 
 * 如果是这样，将在调用之前对初始化程序进行排序。 这为用户提供了一种机制，以确保servlet容器初始化发生的顺序。 
 * 预计很少会使用此功能，因为典型的应用程序可能会将所有容器初始化集中在单个{@code WebApplicationInitializer}中。
 *
 * <h2>注意事项</h2>
 *
 * <h3>web.xml 版本</h3>
 * <p>{@code WEB-INF/web.xml}和{@code WebApplicationInitializer}的使用不是互相排斥的； 
 * 例如，web.xml可以注册一个servlet，而{@code WebApplicationInitializer}可以注册另一个。 
 * 初始化程序甚至可以通过{@link ServletContext#getServletRegistration(String)}之类的方法来修改{@code web.xml}中执行的注册。 
 * 但是，如果应用程序中存在{@code WEB-INF/web.xml}，则必须将其version属性设置为“ 3.0”或更高，
 * 否则Servlet容器将忽略{@code ServletContainerInitializer}引导。
 *
 * <h3>映射到Tomcat下的'/'</h3>
 * <p>Apache Tomcat将其内部{@code DefaultServlet}映射为"/"，并且在Tomcat版本<= 7.0.14上，
 * 无法以编程方式覆盖此Servlet映射。 7.0.15解决了此问题。 覆盖"/" servlet映射也已在GlassFish 3.1下成功测试。<p>
 *
 * @author Chris Beams
 * @since 3.1
 * @see SpringServletContainerInitializer
 * @see org.springframework.web.context.AbstractContextLoaderInitializer
 * @see org.springframework.web.servlet.support.AbstractDispatcherServletInitializer
 * @see org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer
 */
public interface WebApplicationInitializer {

	/**
	 * 使用初始化此Web应用程序所需的任何servlets, filters, listeners, context-params 和
	 * attributes来配置给定的{@link ServletContext}。 请参阅{@linkplain WebApplicationInitializer 上面}的示例。
	 * @param servletContext the {@code ServletContext} to initialize
	 * @throws ServletException if any call against the given {@code ServletContext}
	 * throws a {@code ServletException}
	 */
	void onStartup(ServletContext servletContext) throws ServletException;

}
