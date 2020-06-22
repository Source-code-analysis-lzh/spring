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

package org.springframework.context;

import org.springframework.beans.factory.Aware;
import org.springframework.util.StringValueResolver;

/**
 * 希望获取{@code StringValueResolver}对象的任何对象实现的接口，StringValueResolver用于解析嵌入式定义值.
 *
 * <p>这是通过{@code ApplicationContextAware}/{@code BeanFactoryAware}
 * 接口替代完整ConfigurableBeanFactory依赖关系的替代方法.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 3.0.3
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#resolveEmbeddedValue(String)
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#getBeanExpressionResolver()
 * @see org.springframework.beans.factory.config.EmbeddedValueResolver
 */
public interface EmbeddedValueResolverAware extends Aware {

	/**
	 * Set the StringValueResolver to use for resolving embedded definition values.
	 */
	void setEmbeddedValueResolver(StringValueResolver resolver);

}
