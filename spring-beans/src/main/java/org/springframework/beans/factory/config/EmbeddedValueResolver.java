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

package org.springframework.beans.factory.config;

import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

/**
 * {@link StringValueResolver}适配器，用于针对{@link ConfigurableBeanFactory}解析占位符和表达式.
 *
 * <p>请注意，与{@link ConfigurableBeanFactory#resolveEmbeddedValue}方法不同，
 * 此适配器也解析表达式. 所使用的{@link BeanExpressionContext}用于普通bean工厂，
 * 没有为要访问的任何上下文对象指定范围.
 *
 * @author Juergen Hoeller
 * @since 4.3
 * @see ConfigurableBeanFactory#resolveEmbeddedValue(String)
 * @see ConfigurableBeanFactory#getBeanExpressionResolver()
 * @see BeanExpressionContext
 */
public class EmbeddedValueResolver implements StringValueResolver {

	private final BeanExpressionContext exprContext;

	@Nullable
	private final BeanExpressionResolver exprResolver;


	public EmbeddedValueResolver(ConfigurableBeanFactory beanFactory) {
		this.exprContext = new BeanExpressionContext(beanFactory, null);
		this.exprResolver = beanFactory.getBeanExpressionResolver();
	}


	@Override
	@Nullable
	public String resolveStringValue(String strVal) {
		String value = this.exprContext.getBeanFactory().resolveEmbeddedValue(strVal);
		// 如果存在SpringEL表达式，则还需要进一步解析
		if (this.exprResolver != null && value != null) {
			Object evaluated = this.exprResolver.evaluate(value, this.exprContext);
			value = (evaluated != null ? evaluated.toString() : null);
		}
		return value;
	}

}
