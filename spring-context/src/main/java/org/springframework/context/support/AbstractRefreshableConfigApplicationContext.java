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

package org.springframework.context.support;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * AbstractRefreshableApplicationContext子类，用于添加对指定配置位置的通用处理.
 * 作为基于XML的应用程序上下文实现（例如{@link ClassPathXmlApplicationContext}和
 * {@link FileSystemXmlApplicationContext}以及{@link org.springframework.web.context.support.XmlWebApplicationContext}）的基类.
 *
 * @author Juergen Hoeller
 * @since 2.5.2
 * @see #setConfigLocation
 * @see #setConfigLocations
 * @see #getDefaultConfigLocations
 */
public abstract class AbstractRefreshableConfigApplicationContext extends AbstractRefreshableApplicationContext
		implements BeanNameAware, InitializingBean {

	@Nullable
	private String[] configLocations;

	private boolean setIdCalled = false;


	/**
	 * Create a new AbstractRefreshableConfigApplicationContext with no parent.
	 */
	public AbstractRefreshableConfigApplicationContext() {
	}

	/**
	 * Create a new AbstractRefreshableConfigApplicationContext with the given parent context.
	 * @param parent the parent context
	 */
	public AbstractRefreshableConfigApplicationContext(@Nullable ApplicationContext parent) {
		super(parent);
	}


	/**
	 * 使用init-param样式为此应用程序上下文设置配置位置，即以逗号，分号或空格分隔不同的配置位置.
	 * <p>如果未设置，则实现可酌情使用默认值.
	 */
	public void setConfigLocation(String location) {
		setConfigLocations(StringUtils.tokenizeToStringArray(location, CONFIG_LOCATION_DELIMITERS));
	}

	/**
	 * 设置此应用程序上下文的配置位置.
	 * <p>如果未设置，则实现可酌情使用默认值.
	 */
	public void setConfigLocations(@Nullable String... locations) {
		if (locations != null) {
			Assert.noNullElements(locations, "Config locations must not be null");
			this.configLocations = new String[locations.length];
			for (int i = 0; i < locations.length; i++) {
				this.configLocations[i] = resolvePath(locations[i]).trim();
			}
		}
		else {
			this.configLocations = null;
		}
	}

	/**
	 * 返回一个资源位置数组，引用此上下文应使用的XML bean定义文件.
	 * 还可以包括位置模式，这将通过ResourcePatternResolver进行解析.
	 * <p>默认实现返回{@code null}. 子类可以重写此方法，以提供一组资源位置以从中加载bean定义.
	 * @return an array of resource locations, or {@code null} if none
	 * @see #getResources
	 * @see #getResourcePatternResolver
	 */
	@Nullable
	protected String[] getConfigLocations() {
		return (this.configLocations != null ? this.configLocations : getDefaultConfigLocations());
	}

	/**
	 * 对于未指定显式制定配置位置的情况，返回要使用的默认配置位置.
	 * <p>默认实现返回{@code null}，需要明确的配置位置.
	 * @return an array of default config locations, if any
	 * @see #setConfigLocations
	 */
	@Nullable
	protected String[] getDefaultConfigLocations() {
		return null;
	}

	/**
	 * 解析给定的路径，必要时用相应的环境属性值替换占位符. 应用于配置位置.
	 * @param path the original file path
	 * @return the resolved file path
	 * @see org.springframework.core.env.Environment#resolveRequiredPlaceholders(String)
	 */
	protected String resolvePath(String path) {
		return getEnvironment().resolveRequiredPlaceholders(path);
	}


	@Override
	public void setId(String id) {
		super.setId(id);
		this.setIdCalled = true;
	}

	/**
	 * 对于将上下文实例本身定义为Bean的情况，默认情况下将此上下文的ID设置为Bean名称.
	 */
	@Override
	public void setBeanName(String name) {
		if (!this.setIdCalled) {
			super.setId(name);
			setDisplayName("ApplicationContext '" + name + "'");
		}
	}

	/**
	 * 如果尚未在具体上下文的构造函数中刷新，则触发{@link #refresh()}.
	 */
	@Override
	public void afterPropertiesSet() {
		if (!isActive()) { // 如果应用上下文没有启动，则刷新启动
			refresh();
		}
	}

}
