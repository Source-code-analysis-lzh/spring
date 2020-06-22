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

package org.springframework.aop.target;

import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

/**
 * 懒惰地从{@link org.springframework.beans.factory.BeanFactory}
 * 访问Singleton Bean的{@link org.springframework.aop.TargetSource}。
 *
 * <p>当初始化时需要代理引用但实际的目标对象要在首次使用之前才进行初始化时很有用。 
 * 当在{@link org.springframework.context.ApplicationContext}（或渴望预先实例化单例bean的{@code BeanFactory}）
 * 中定义目标bean时，也必须将其标记为"lazy-init"，
 * 否则它将在启动时由所述{@code ApplicationContext}（或{@code BeanFactory}）实例化。
 * <p>例如:
 *
 * <pre class="code">
 * &lt;bean id="serviceTarget" class="example.MyService" lazy-init="true"&gt;
 *   ...
 * &lt;/bean&gt;
 *
 * &lt;bean id="service" class="org.springframework.aop.framework.ProxyFactoryBean"&gt;
 *   &lt;property name="targetSource"&gt;
 *     &lt;bean class="org.springframework.aop.target.LazyInitTargetSource"&gt;
 *       &lt;property name="targetBeanName"&gt;&lt;idref local="serviceTarget"/&gt;&lt;/property&gt;
 *     &lt;/bean&gt;
 *   &lt;/property&gt;
 * &lt;/bean&gt;</pre>
 *
 * 在调用“服务”代理上的方法之前，不会初始化"serviceTarget" bean。
 *
 * <p>子类可以扩展此类，并覆盖{@link #postProcessTargetObject(Object)}以在首次加载目标对象时执行一些其他处理。
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 1.1.4
 * @see org.springframework.beans.factory.BeanFactory#getBean
 * @see #postProcessTargetObject
 */
@SuppressWarnings("serial")
public class LazyInitTargetSource extends AbstractBeanFactoryBasedTargetSource {

	@Nullable
	private Object target;


	@Override
	@Nullable
	public synchronized Object getTarget() throws BeansException {
		if (this.target == null) {
			this.target = getBeanFactory().getBean(getTargetBeanName());
			postProcessTargetObject(this.target);
		}
		return this.target;
	}

	/**
	 * Subclasses may override this method to perform additional processing on
	 * the target object when it is first loaded.
	 * @param targetObject the target object that has just been instantiated (and configured)
	 */
	protected void postProcessTargetObject(Object targetObject) {
	}

}
