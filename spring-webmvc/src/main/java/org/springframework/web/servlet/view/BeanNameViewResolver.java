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
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

/**
 * {@link org.springframework.web.servlet.ViewResolver}的简单实现，
 * 它将视图名称解释为在当前应用程序上下文中的Bean名称，如通常在正在执行DispatcherServlet的XML文件中。
 *
 * <p>对于小型应用程序，此解析器可能很方便，将所有定义（从控制器到视图）都保留在同一位置。 
 * 对于较大的应用程序，{@link XmlViewResolver}将是更好的选择，因为它将XML视图Bean定义分离到专用的视图文件中。
 *
 * <p>注意：此{@code ViewResolver}和{@link XmlViewResolver}都不支持国际化。 
 * 如果您需要在每个语言环境中应用不同的视图资源，请考虑使用{@link ResourceBundleViewResolver}。
 *
 * <p>注意：此{@code ViewResolver}实现{@link Ordered}接口，以允许灵活地参与{@code ViewResolver}链。 
 * 例如，可以通过此{@code ViewResolver}定义一些特殊的视图（将其0表示为"order"值），
 * 而所有其余视图都可以由{@link UrlBasedViewResolver}解析。
 *
 * @author Juergen Hoeller
 * @since 18.06.2003
 * @see XmlViewResolver
 * @see ResourceBundleViewResolver
 * @see UrlBasedViewResolver
 */
public class BeanNameViewResolver extends WebApplicationObjectSupport implements ViewResolver, Ordered {

	private int order = Ordered.LOWEST_PRECEDENCE;  // default: same as non-Ordered


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


	@Override
	@Nullable
	public View resolveViewName(String viewName, Locale locale) throws BeansException {
		ApplicationContext context = obtainApplicationContext();
		if (!context.containsBean(viewName)) {
			// Allow for ViewResolver chaining...
			return null;
		}
		if (!context.isTypeMatch(viewName, View.class)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Found bean named '" + viewName + "' but it does not implement View");
			}
			// Since we're looking into the general ApplicationContext here,
			// let's accept this as a non-match and allow for chaining as well...
			return null;
		}
		return context.getBean(viewName, View.class);
	}

}
