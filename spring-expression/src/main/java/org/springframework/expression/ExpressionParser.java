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

package org.springframework.expression;

/**
 * 将表达式字符串解析为可求值的已编译表达式. 支持解析模板以及标准表达式字符串.
 *
 * @author Keith Donald
 * @author Andy Clement
 * @since 3.0
 */
public interface ExpressionParser {

	/**
	 * 解析表达式字符串，并返回可用于重复求值的Expression对象.
	 * <p>Some examples:
	 * <pre class="code">
	 *     3 + 4
	 *     name.firstName
	 * </pre>
	 * @param expressionString the raw expression string to parse
	 * @return an evaluator for the parsed expression
	 * @throws ParseException an exception occurred during parsing
	 */
	Expression parseExpression(String expressionString) throws ParseException;

	/**
	 * 解析表达式字符串，并返回可用于重复求值的Expression对象.
	 * <p>Some examples:
	 * <pre class="code">
	 *     3 + 4
	 *     name.firstName
	 * </pre>
	 * @param expressionString the raw expression string to parse
	 * @param context 影响此表达式解析路径的上下文（可选）
	 * @return an evaluator for the parsed expression
	 * @throws ParseException an exception occurred during parsing
	 */
	Expression parseExpression(String expressionString, ParserContext context) throws ParseException;

}
