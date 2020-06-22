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

package org.springframework.web.method.support;

import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 通过查看方法参数和参数值并确定应更新目标URL的哪一部分来促进{@link UriComponents}构建的策略。
 *
 * @author Oliver Gierke
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public interface UriComponentsContributor {

	/**
	 * 此贡献者是否支持给定的method参数。
	 */
	boolean supportsParameter(MethodParameter parameter);

	/**
	 * 处理给定的方法参数，然后更新{@link UriComponentsBuilder}或使用URI变量将其添加到map中，
	 * 以便在处理完所有参数后用来扩展URI。
	 * @param parameter the controller method parameter (never {@code null})
	 * @param value the argument value (possibly {@code null})
	 * @param builder the builder to update (never {@code null})
	 * @param uriVariables a map to add URI variables to (never {@code null})
	 * @param conversionService a ConversionService to format values as Strings
	 */
	void contributeMethodArgument(MethodParameter parameter, Object value, UriComponentsBuilder builder,
			Map<String, Object> uriVariables, ConversionService conversionService);

}
