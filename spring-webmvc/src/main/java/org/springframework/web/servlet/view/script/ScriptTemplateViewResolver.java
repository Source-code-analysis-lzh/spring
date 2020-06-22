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

package org.springframework.web.servlet.view.script;

import org.springframework.web.servlet.view.UrlBasedViewResolver;

/**
 * {@link UrlBasedViewResolver}的便利子类，它支持{@link ScriptTemplateView}及其自定义子类。
 *
 * <p>可以通过{@link #setViewClass(Class)}属性指定此解析器创建的所有视图的视图类。
 *
 * <p>注意：在链ViewResolvers中时，此解析器将检查是否存在指定的模板资源，并且仅在实际找到模板时才返回非null的View对象。
 *
 * @author Sebastien Deleuze
 * @since 4.2
 * @see ScriptTemplateConfigurer
 */
public class ScriptTemplateViewResolver extends UrlBasedViewResolver {

	/**
	 * Sets the default {@link #setViewClass view class} to {@link #requiredViewClass}:
	 * by default {@link ScriptTemplateView}.
	 */
	public ScriptTemplateViewResolver() {
		setViewClass(requiredViewClass());
	}

	/**
	 * A convenience constructor that allows for specifying {@link #setPrefix prefix}
	 * and {@link #setSuffix suffix} as constructor arguments.
	 * @param prefix the prefix that gets prepended to view names when building a URL
	 * @param suffix the suffix that gets appended to view names when building a URL
	 * @since 4.3
	 */
	public ScriptTemplateViewResolver(String prefix, String suffix) {
		this();
		setPrefix(prefix);
		setSuffix(suffix);
	}


	@Override
	protected Class<?> requiredViewClass() {
		return ScriptTemplateView.class;
	}

}
