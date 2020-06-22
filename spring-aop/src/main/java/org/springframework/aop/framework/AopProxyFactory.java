/*
 * Copyright 2002-2012 the original author or authors.
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

package org.springframework.aop.framework;

/**
 * 由能够基于{@link AdvisedSupport}配置对象创建AOP代理的工厂实现的接口。
 *
 * <p>代理应遵守以下合同：
 * <ul>
 * <li>他们应该实现配置指示的所有接口的代理。
 * <li>他们应该实现{@link Advised}的接口。
 * <li>他们应该实施equals方法来比较代理接口，advice和目标。
 * <li>如果所有advisors程序和目标都可序列化，则它们应可序列化。
 * <li>如果advisors程序和目标是线程安全的，则它们应该是线程安全的。
 * </ul>
 *
 * <p>代理可能允许也可能不允许更改advice。 
 * 如果他们不允许更改advice（例如，由于配置被冻结），
 * 则代理应在尝试更改advice时引发{@link AopConfigException}。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public interface AopProxyFactory {

	/**
	 * 为给定的AOP配置创建一个{@link AopProxy}。
	 * @param config the AOP configuration in the form of an
	 * AdvisedSupport object
	 * @return the corresponding AOP proxy
	 * @throws AopConfigException if the configuration is invalid
	 */
	AopProxy createAopProxy(AdvisedSupport config) throws AopConfigException;

}
