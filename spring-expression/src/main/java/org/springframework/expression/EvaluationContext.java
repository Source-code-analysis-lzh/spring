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

package org.springframework.expression;

import java.util.List;

import org.springframework.lang.Nullable;

/**
 * 表达式在计算上下文中执行.在表达式求值期间遇到引用时，在这个上下文中解析引用.
 *
 * <p>该EvaluationContext接口的默认实现是：{@link org.springframework.expression.spel.support.StandardEvaluationContext}，
 * 可以对其进行扩展，而不必手动实现所有功能.
 *
 * @author Andy Clement
 * @author Juergen Hoeller
 * @since 3.0
 */
public interface EvaluationContext {

	/**
	 * 返回默认的根上下文对象，应针对该对象解析非全限定的属性/方法等. 计算表达式时可以覆盖此设置.
	 */
	TypedValue getRootObject();

	/**
	 * 返回访问器列表，依次要求其读取/写入属性.
	 */
	List<PropertyAccessor> getPropertyAccessors();

	/**
	 * 返回一个解析器列表，该列表将依次被要求查找构造函数.
	 */
	List<ConstructorResolver> getConstructorResolvers();

	/**
	 * 返回一个解析器列表，将依次要求其定位方法.
	 */
	List<MethodResolver> getMethodResolvers();

	/**
	 * 返回一个可以按名称查找bean的bean解析器.
	 */
	@Nullable
	BeanResolver getBeanResolver();

	/**
	 * 返回一个类型定位器，该类型定位器可用于按简短名称或完全限定名称查找类型.
	 */
	TypeLocator getTypeLocator();

	/**
	 * 返回一个可以将值从一种类型转换（或强制）为另一种类型的类型转换器.
	 */
	TypeConverter getTypeConverter();

	/**
	 * 返回类型比较器，以比较对象对是否相等.
	 */
	TypeComparator getTypeComparator();

	/**
	 * 返回一个运算符重载程序，该重载程序可能支持比标准类型集更多的数学运算.
	 */
	OperatorOverloader getOperatorOverloader();

	/**
	 * 在此计算上下文中将命名变量设置为指定值.
	 * @param name the name of the variable to set
	 * @param value the value to be placed in the variable
	 */
	void setVariable(String name, @Nullable Object value);

	/**
	 * 在此计算上下文中查找命名变量.
	 * @param name variable to lookup
	 * @return the value of the variable, or {@code null} if not found
	 */
	@Nullable
	Object lookupVariable(String name);

}
