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

package org.springframework.web.servlet.view;

import java.util.Locale;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.View;

/**
 * 一个{@link org.springframework.web.servlet.ViewResolver}实现，
 * 该实现使用专用XML文件中的bean定义作为视图定义（由资源位置指定）。 
 * 该文件通常位于WEB-INF目录中。 默认值为"/WEB-INF/views.xml"。
 *
 * <p>此{@code ViewResolver}在其定义资源级别不支持国际化。 如果您需要在每个语言环境中应用不同的视图资源，
 * 请考虑使用{@link ResourceBundleViewResolver}。
 *
 * <p>注意：此{@code ViewResolver}实现{@link Ordered}接口，以允许灵活地参与ViewResolver链中。 
 * 例如，可以通过此{@code ViewResolver}定义一些特殊的视图（将其设置为"order" 0值），
 * 而所有其余视图都可以由{@link UrlBasedViewResolver}解析。
 *
 * @author Juergen Hoeller
 * @since 18.06.2003
 * @see org.springframework.context.ApplicationContext#getResource
 * @see ResourceBundleViewResolver
 * @see UrlBasedViewResolver
 */
public class XmlViewResolver extends AbstractCachingViewResolver
		implements Ordered, InitializingBean, DisposableBean {

	/** Default if no other location is supplied. */
	public static final String DEFAULT_LOCATION = "/WEB-INF/views.xml";


	@Nullable
	private Resource location;

	@Nullable
	private ConfigurableApplicationContext cachedFactory;

	private int order = Ordered.LOWEST_PRECEDENCE;  // default: same as non-Ordered


	/**
	 * Set the location of the XML file that defines the view beans.
	 * <p>The default is "/WEB-INF/views.xml".
	 * @param location the location of the XML file.
	 */
	public void setLocation(Resource location) {
		this.location = location;
	}

	/**
	 * Specify the order value for this ViewResolver bean.
	 * <p>The default value is {@code Ordered.LOWEST_PRECEDENCE}, meaning non-ordered.
	 * @see org.springframework.core.Ordered#getOrder()
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	/**
	 * Pre-initialize the factory from the XML file.
	 * Only effective if caching is enabled.
	 */
	@Override
	public void afterPropertiesSet() throws BeansException {
		if (isCache()) {
			initFactory();
		}
	}


	/**
	 * This implementation returns just the view name,
	 * as XmlViewResolver doesn't support localized resolution.
	 */
	@Override
	protected Object getCacheKey(String viewName, Locale locale) {
		return viewName;
	}

	@Override
	protected View loadView(String viewName, Locale locale) throws BeansException {
		BeanFactory factory = initFactory();
		try {
			return factory.getBean(viewName, View.class);
		}
		catch (NoSuchBeanDefinitionException ex) {
			// Allow for ViewResolver chaining...
			return null;
		}
	}

	/**
	 * Initialize the view bean factory from the XML file.
	 * Synchronized because of access by parallel threads.
	 * @throws BeansException in case of initialization errors
	 */
	protected synchronized BeanFactory initFactory() throws BeansException {
		if (this.cachedFactory != null) {
			return this.cachedFactory;
		}

		ApplicationContext applicationContext = obtainApplicationContext();

		Resource actualLocation = this.location;
		if (actualLocation == null) {
			actualLocation = applicationContext.getResource(DEFAULT_LOCATION);
		}

		// Create child ApplicationContext for views.
		GenericWebApplicationContext factory = new GenericWebApplicationContext();
		factory.setParent(applicationContext);
		factory.setServletContext(getServletContext());

		// Load XML resource with context-aware entity resolver.
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
		reader.setEnvironment(applicationContext.getEnvironment());
		reader.setEntityResolver(new ResourceEntityResolver(applicationContext));
		reader.loadBeanDefinitions(actualLocation);

		factory.refresh();

		if (isCache()) {
			this.cachedFactory = factory;
		}
		return factory;
	}


	/**
	 * Close the view bean factory on context shutdown.
	 */
	@Override
	public void destroy() throws BeansException {
		if (this.cachedFactory != null) {
			this.cachedFactory.close();
		}
	}

}
