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

package org.springframework.core;

/**
 * 扩展了{@link Ordered}接口，表示优先顺序：{@code PriorityOrdered}对象始终在普通{@link Ordered}对象之前应用，
 * 而不管其顺序值如何.
 *
 * <p>在对一组{@code Ordered}对象进行排序时，{@code PriorityOrdered}对象
 * 和普通{@code Ordered}对象被有效地视为两个单独的子集，
 * 其中{@code PriorityOrdered}对象组位于普通{@code Ordered}对象集之前，并且在这些子集中应用了相对顺序.
 *
 * <p>这主要是一个专用接口，在框架本身内用于对象，在该对象中，首先识别优先对象非常重要，
 * 甚至可能根本无法获得其余对象. 一个典型的例子：在Spring {@link org.springframework.context.ApplicationContext}中优先处理后处理器.
 *
 * <p>注意：{@code PriorityOrdered}后处理器Bean在特殊阶段中比其它后处理器Bean先初始化.
 * 这微妙地影响了它们的自动装配行为：它们将仅针对不需要为类型匹配而急切初始化的bean自动装配.
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @since 2.5
 * @see org.springframework.beans.factory.config.PropertyOverrideConfigurer
 * @see org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
 */
public interface PriorityOrdered extends Ordered {
}
