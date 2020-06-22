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

package org.springframework.context.annotation;

/**
 * A {@link Condition} that offers more fine-grained control when used with
 * {@code @Configuration}. Allows certain {@link Condition Conditions} to adapt when they match
 * based on the configuration phase. For example, a condition that checks if a bean
 * has already been registered might choose to only be evaluated during the
 * {@link ConfigurationPhase#REGISTER_BEAN REGISTER_BEAN} {@link ConfigurationPhase}.
 * 与{@code @Configuration}一起使用时，可以提供更细粒度控制的{@link Condition}。 
 * 允许某些{@link Condition Conditions}在匹配时根据配置阶段进行调整。 例如，
 * 检查bean是否已经注册的条件可能选择仅在{@link ConfigurationPhase#REGISTER_BEAN REGISTER_BEAN} 
 * {@link ConfigurationPhase}期间进行评估。
 * 
 * <p>Condition可以用于判断只有当某个Bean已注册的情况下才进行Configuration，
 * 大部分情况下直接用Condition就OK了。ConfigurationCondition只是在Condition上面加了一个东西，
 * 就是什么时候做这个判断，也就是代码里的PARSE_CONFIGURATION和REGISTER_BEAN，具体啥意思参加这两个枚举的注释了。
 *
 * @author Phillip Webb
 * @since 4.0
 * @see Configuration
 */
public interface ConfigurationCondition extends Condition {

	/**
	 * 返回要在其中评估条件的{@link ConfigurationPhase}。
	 */
	ConfigurationPhase getConfigurationPhase();


	/**
	 * 可以评估条件的各种配置阶段。
	 */
	enum ConfigurationPhase {

		/**
		 * 应该在解析{@code @Configuration}类时评估{@link Condition}。
		 * <p>如果此时条件不匹配，则不会添加{@code @Configuration}类。
		 */
		PARSE_CONFIGURATION,

		/**
		 * 添加常规（非{@code @Configuration}）bean时，应评估{@link Condition}。 
		 * 该条件不会阻止添加{@code @Configuration}类。
		 * <p>在评估条件时，所有{@code @Configuration}都将被解析。
		 */
		REGISTER_BEAN
	}

}
