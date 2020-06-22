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

package org.springframework.core.env;

/**
 * 指示包含并公开{@link Environment}引用的组件的接口.
 *
 * <p>所有Spring应用程序上下文都具有EnvironmentCapable功能，
 * 并且该接口主要用于在接受BeanFactory实例的框架方法中执行{@code instanceof}检查，
 * 以便可以与环境进行交互.
 *
 * <p>如前所述，{@link org.springframework.context.ApplicationContext ApplicationContext}
 * 扩展了EnvironmentCapable，因此公开了{@link #getEnvironment()}方法；
 * 但是，{@link org.springframework.context.ConfigurableApplicationContext#getEnvironment getEnvironment()}
 * 重新定义了getEnvironment（）并缩小了签名范围，以返回{@link ConfigurableEnvironment}.
 * 结果是环境对象是“只读的”，直到从ConfigurableApplicationContext访问它为止，此时也可以对其进行配置.
 *
 * @author Chris Beams
 * @since 3.1
 * @see Environment
 * @see ConfigurableEnvironment
 * @see org.springframework.context.ConfigurableApplicationContext#getEnvironment()
 */
public interface EnvironmentCapable {

	/**
	 * 返回与此组件关联的{@link Environment}。
	 */
	Environment getEnvironment();

}
