/*
 * Copyright 2002-2017 the original author or authors.
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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.MessageSource;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.support.JstlUtils;
import org.springframework.web.servlet.support.RequestContext;

/**
 * 用于JSTL页面的{@link InternalResourceView}的特殊化，即使用JSP标准标签库的JSP页面。
 *
 * <p>使用Spring的语言环境和{@link org.springframework.context.MessageSource}，
 * 公开特定于JSTL的请求属性，这些属性指定JSTL格式和消息标签的语言环境和资源包。
 *
 * <p>从DispatcherServlet上下文定义的角度来看，{@link InternalResourceViewResolver}的典型用法如下所示：
 *
 * <pre class="code">
 * &lt;bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver"&gt;
 *   &lt;property name="viewClass" value="org.springframework.web.servlet.view.JstlView"/&gt;
 *   &lt;property name="prefix" value="/WEB-INF/jsp/"/&gt;
 *   &lt;property name="suffix" value=".jsp"/&gt;
 * &lt;/bean&gt;
 *
 * &lt;bean id="messageSource" class="org.springframework.context.support.ResourceBundleMessageSource"&gt;
 *   &lt;property name="basename" value="messages"/&gt;
 * &lt;/bean&gt;</pre>
 *
 * 使用此视图类启用显式的JSTL支持，将从处理器返回的每个视图名称都转换为JSP资源
 * （例如："myView" -> "/WEB-INF/jsp/myView.jsp"）。
 *
 * <p>指定的MessageSource从类路径中的"messages.properties"等文件加载消息。 
 * 这将作为JSTL本地化上下文自动显示给JSTL fmt标签（消息等）使用的视图。 
 * 考虑使用Spring的ReloadableResourceBundleMessageSource而不是标准
 * ResourceBundleMessageSource以获得更多的复杂特性。 
 * 当然，任何其它Spring组件都可以共享相同的MessageSource。
 *
 * <p>这是一个单独的类，主要是为了避免{@link InternalResourceView}本身中的JSTL依赖关系。 
 * 直到J2EE 1.4，JSTL才成为标准J2EE的一部分，因此我们不能假定JSTL API jar在类路径中可用。
 *
 * <p>提示：将{@link #setExposeContextBeansAsAttributes}标志设置为"true"，
 * 以使应用程序上下文中的所有Spring bean在JSTL表达式（例如，在{@code c:out}值表达式中）中均可访问。 
 * 这还将使所有此类Bean在JSP 2.0页面中都可以通过简单的{@code ${...}表达式进行访问。
 *
 * @author Juergen Hoeller
 * @since 27.02.2003
 * @see org.springframework.web.servlet.support.JstlUtils#exposeLocalizationContext
 * @see InternalResourceViewResolver
 * @see org.springframework.context.support.ResourceBundleMessageSource
 * @see org.springframework.context.support.ReloadableResourceBundleMessageSource
 */
public class JstlView extends InternalResourceView {

	@Nullable
	private MessageSource messageSource;


	/**
	 * Constructor for use as a bean.
	 * @see #setUrl
	 */
	public JstlView() {
	}

	/**
	 * Create a new JstlView with the given URL.
	 * @param url the URL to forward to
	 */
	public JstlView(String url) {
		super(url);
	}

	/**
	 * Create a new JstlView with the given URL.
	 * @param url the URL to forward to
	 * @param messageSource the MessageSource to expose to JSTL tags
	 * (will be wrapped with a JSTL-aware MessageSource that is aware of JSTL's
	 * {@code javax.servlet.jsp.jstl.fmt.localizationContext} context-param)
	 * @see JstlUtils#getJstlAwareMessageSource
	 */
	public JstlView(String url, MessageSource messageSource) {
		this(url);
		this.messageSource = messageSource;
	}


	/**
	 * Wraps the MessageSource with a JSTL-aware MessageSource that is aware
	 * of JSTL's {@code javax.servlet.jsp.jstl.fmt.localizationContext}
	 * context-param.
	 * @see JstlUtils#getJstlAwareMessageSource
	 */
	@Override
	protected void initServletContext(ServletContext servletContext) {
		if (this.messageSource != null) {
			this.messageSource = JstlUtils.getJstlAwareMessageSource(servletContext, this.messageSource);
		}
		super.initServletContext(servletContext);
	}

	/**
	 * Exposes a JSTL LocalizationContext for Spring's locale and MessageSource.
	 * @see JstlUtils#exposeLocalizationContext
	 */
	@Override
	protected void exposeHelpers(HttpServletRequest request) throws Exception {
		if (this.messageSource != null) {
			JstlUtils.exposeLocalizationContext(request, this.messageSource);
		}
		else {
			JstlUtils.exposeLocalizationContext(new RequestContext(request, getServletContext()));
		}
	}

}
