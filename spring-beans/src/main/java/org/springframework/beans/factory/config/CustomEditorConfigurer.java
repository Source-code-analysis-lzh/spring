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

package org.springframework.beans.factory.config;

import java.beans.PropertyEditor;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/**
 * {@link BeanFactoryPostProcessor}实现，可以方便地注册自定义{@link PropertyEditor property editors}.
 *
 * <p>如果要注册{@link PropertyEditor}实例，从Spring 2.0开始，
 * 推荐的用法是使用自定义的{@link PropertyEditorRegistrar}实现，
 * 该实现又将给定{@link org.springframework.beans.PropertyEditorRegistry registry}
 * 上的任何所需编辑器实例注册.
 * 每个PropertyEditorRegistrar可以注册任意数量的自定义编辑器.
 *
 * <pre class="code">
 * &lt;bean id="customEditorConfigurer" class="org.springframework.beans.factory.config.CustomEditorConfigurer"&gt;
 *   &lt;property name="propertyEditorRegistrars"&gt;
 *     &lt;list&gt;
 *       &lt;bean class="mypackage.MyCustomDateEditorRegistrar"/&gt;
 *       &lt;bean class="mypackage.MyObjectEditorRegistrar"/&gt;
 *     &lt;/list&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 * </pre>
 *
 * <p>通过{@code customEditors}属性注册{@link PropertyEditor}类非常好.
 * Spring将为每次编辑尝试创建它们的新实例，然后：
 *
 * <pre class="code">
 * &lt;bean id="customEditorConfigurer" class="org.springframework.beans.factory.config.CustomEditorConfigurer"&gt;
 *   &lt;property name="customEditors"&gt;
 *     &lt;map&gt;
 *       &lt;entry key="java.util.Date" value="mypackage.MyCustomDateEditor"/&gt;
 *       &lt;entry key="mypackage.MyObject" value="mypackage.MyObjectEditor"/&gt;
 *     &lt;/map&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;
 * </pre>
 *
 * <p>注意，您不应该通过{@code customEditors}属性注册{@link PropertyEditor} Bean实例，
 * 因为{@link PropertyEditor PropertyEditors}是有状态的，因此对于每次编辑尝试，实例都必须同步.
 * 如果您需要控制{@link PropertyEditor PropertyEditors}的实例化过程，
 * 请使用{@link PropertyEditorRegistrar}进行注册.
 *
 * <p>还支持"java.lang.String[]"样式的数组类名称和基础类名称（例如"boolean"）.
 * 委托{@link ClassUtils}进行实际的类名解析.
 *
 * <p>注意：在此配置程序中注册的自定义属性编辑器不适用于数据绑定.
 * 用于数据绑定的自定义编辑器需要在{@link org.springframework.validation.DataBinder}上注册：
 * 可以使用公共基类或委托给常见的PropertyEditorRegistrar实现，以在此处重用编辑器注册.
 * https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#validation
 *
 * @author Juergen Hoeller
 * @since 27.02.2004
 * @see java.beans.PropertyEditor
 * @see org.springframework.beans.PropertyEditorRegistrar
 * @see ConfigurableBeanFactory#addPropertyEditorRegistrar
 * @see ConfigurableBeanFactory#registerCustomEditor
 * @see org.springframework.validation.DataBinder#registerCustomEditor
 */
public class CustomEditorConfigurer implements BeanFactoryPostProcessor, Ordered {

	protected final Log logger = LogFactory.getLog(getClass());

	private int order = Ordered.LOWEST_PRECEDENCE;  // default: same as non-Ordered

	@Nullable
	private PropertyEditorRegistrar[] propertyEditorRegistrars;

	@Nullable
	private Map<Class<?>, Class<? extends PropertyEditor>> customEditors;


	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	/**
	 * Specify the {@link PropertyEditorRegistrar PropertyEditorRegistrars}
	 * to apply to beans defined within the current application context.
	 * <p>This allows for sharing {@code PropertyEditorRegistrars} with
	 * {@link org.springframework.validation.DataBinder DataBinders}, etc.
	 * Furthermore, it avoids the need for synchronization on custom editors:
	 * A {@code PropertyEditorRegistrar} will always create fresh editor
	 * instances for each bean creation attempt.
	 * @see ConfigurableListableBeanFactory#addPropertyEditorRegistrar
	 */
	public void setPropertyEditorRegistrars(PropertyEditorRegistrar[] propertyEditorRegistrars) {
		this.propertyEditorRegistrars = propertyEditorRegistrars;
	}

	/**
	 * Specify the custom editors to register via a {@link Map}, using the
	 * class name of the required type as the key and the class name of the
	 * associated {@link PropertyEditor} as value.
	 * @see ConfigurableListableBeanFactory#registerCustomEditor
	 */
	public void setCustomEditors(Map<Class<?>, Class<? extends PropertyEditor>> customEditors) {
		this.customEditors = customEditors;
	}


	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		if (this.propertyEditorRegistrars != null) {
			for (PropertyEditorRegistrar propertyEditorRegistrar : this.propertyEditorRegistrars) {
				beanFactory.addPropertyEditorRegistrar(propertyEditorRegistrar);
			}
		}
		if (this.customEditors != null) {
			this.customEditors.forEach(beanFactory::registerCustomEditor);
		}
	}

}
