/*
 * Copyright 2002-2015 the original author or authors.
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

package org.springframework.core.type;

/**
 * Interface that defines abstract access to the annotations of a specific
 * class, in a form that does not require that class to be loaded yet.
 * 该接口定义了对指定类的注释的抽象访问，不要求加载该类。
 *
 * @author Juergen Hoeller
 * @author Mark Pollack
 * @author Chris Beams
 * @author Phillip Webb
 * @since 3.0
 * @see StandardMethodMetadata
 * @see AnnotationMetadata#getAnnotatedMethods
 * @see AnnotatedTypeMetadata
 */
public interface MethodMetadata extends AnnotatedTypeMetadata {

	/**
	 * 返回方法的名称。
	 */
	String getMethodName();

	/**
	 * 返回声明此方法的类的标准名称。
	 */
	String getDeclaringClassName();

	/**
	 * 返回此方法的声明的返回类型的全限定名称。
	 * @since 4.2
	 */
	String getReturnTypeName();

	/**
	 * 返回底层方法是否有效地抽象：即在类上标记为抽象或在接口中声明为常规的非默认方法。
	 * @since 4.2
	 */
	boolean isAbstract();

	/**
	 * 返回底层方法是否声明为'static'。
	 */
	boolean isStatic();

	/**
	 * 返回底层方法是否标记为'final'。
	 */
	boolean isFinal();

	/**
	 * 返回底层方法是否可重写，即未标记为static, final or private。
	 */
	boolean isOverridable();

}
