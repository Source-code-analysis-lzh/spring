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

package org.springframework.beans.factory;

/**
 * 标记超级接口，用于指示bean有资格通过回调样式方法由Spring容器通知特定框架对象。 
 * 实际的方法签名是由各个子接口确定的，但通常应仅由一个接受单个参数的void返回方法组成。
 *
 * <p>请注意，仅实现{@link Aware}不会提供默认功能。 
 * 相反，必须显式完成处理，例如在{@link org.springframework.beans.factory.config.BeanPostProcessor}中。
 * 有关处理特定{@code *Aware}接口回调的示例，
 * 请参考{@link org.springframework.context.support.ApplicationContextAwareProcessor}。
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 */
public interface Aware {

}
