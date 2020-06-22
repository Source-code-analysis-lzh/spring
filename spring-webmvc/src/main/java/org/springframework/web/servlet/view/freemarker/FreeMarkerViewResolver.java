/*
 * Copyright 2002-2009 the original author or authors.
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

package org.springframework.web.servlet.view.freemarker;

import org.springframework.web.servlet.view.AbstractTemplateViewResolver;

/**
 * 支持{@link FreeMarkerView}的{@link org.springframework.web.servlet.view.UrlBasedViewResolver}
 * 便利子类（即FreeMarker模板）及其自定义子类。
 *
 * <p>可以通过"viewClass"属性指定此解析器生成的所有视图的视图类。 有关详细信息，请参见UrlBasedViewResolver的javadoc。
 *
 * <p>注意：链接ViewResolvers时，FreeMarkerViewResolver将检查指定模板资源的存在，
 * 并且仅在实际找到模板时才返回非null的View对象。
 *
 * @author Juergen Hoeller
 * @since 1.1
 * @see #setViewClass
 * @see #setPrefix
 * @see #setSuffix
 * @see #setRequestContextAttribute
 * @see #setExposeSpringMacroHelpers
 * @see FreeMarkerView
 */
public class FreeMarkerViewResolver extends AbstractTemplateViewResolver {

	/**
	 * Sets the default {@link #setViewClass view class} to {@link #requiredViewClass}:
	 * by default {@link FreeMarkerView}.
	 */
	public FreeMarkerViewResolver() {
		setViewClass(requiredViewClass());
	}

	/**
	 * A convenience constructor that allows for specifying {@link #setPrefix prefix}
	 * and {@link #setSuffix suffix} as constructor arguments.
	 * @param prefix the prefix that gets prepended to view names when building a URL
	 * @param suffix the suffix that gets appended to view names when building a URL
	 * @since 4.3
	 */
	public FreeMarkerViewResolver(String prefix, String suffix) {
		this();
		setPrefix(prefix);
		setSuffix(suffix);
	}


	/**
	 * Requires {@link FreeMarkerView}.
	 */
	@Override
	protected Class<?> requiredViewClass() {
		return FreeMarkerView.class;
	}

}
