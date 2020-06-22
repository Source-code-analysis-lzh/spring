/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.web.servlet.handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.StringUtils;

/**
 * {@link org.springframework.web.servlet.HandlerMapping}接口的实现，
 * 该接口从URL映射到以反斜杠（"/"）开头的bean名称，类似于Struts如何将URL映射到动作名称。
 *
 * <p>这是{@link org.springframework.web.servlet.DispatcherServlet}
 * 和{@link org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping}
 * 一起使用时的默认实现。 另外，{@link SimpleUrlHandlerMapping}允许声明性地自定义处理器映射。
 *
 * <p>映射是从URL到bean名称。 因此，传入URL "/foo"将映射到名为"/foo"的处理器，
 * 或者在多个映射到单个处理器的情况下，将映射到"/foo /foo2"。
 *
 * <p>支持直接匹配（给定为"/test" -&gt; 已注册的"/test"）和"*"匹配项（给定为"/test" -&gt; 已注册"/t*"）。 
 * 请注意，如果适用，默认值是在当前servlet映射中进行映射。 
 * 有关详细信息，请参见{@link #setAlwaysUseFullPath "alwaysUseFullPath"}属性。 
 * 有关模式选项的详细信息，请参见{@link org.springframework.util.AntPathMatcher} javadoc。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see SimpleUrlHandlerMapping
 */
public class BeanNameUrlHandlerMapping extends AbstractDetectingUrlHandlerMapping {

	/**
	 * Checks name and aliases of the given bean for URLs, starting with "/".
	 */
	@Override
	protected String[] determineUrlsForHandler(String beanName) {
		List<String> urls = new ArrayList<>();
		if (beanName.startsWith("/")) {
			urls.add(beanName);
		}
		String[] aliases = obtainApplicationContext().getAliases(beanName);
		for (String alias : aliases) {
			if (alias.startsWith("/")) {
				urls.add(alias);
			}
		}
		return StringUtils.toStringArray(urls);
	}

}
