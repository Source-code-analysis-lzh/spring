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

package org.springframework.context;

import org.springframework.beans.factory.Aware;

/**
 * 希望通过在运行时得到MessageSource（通常是ApplicationContext）的任何对象所实现的接口.
 *
 * <p>注意，MessageSource通常也可以作为bean引用（传递给任意bean属性或构造函数参数）传递，
 * 因为它在应用程序上下文中被定义为名称为"messageSource"的bean.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 1.1.1
 * @see ApplicationContextAware
 */
public interface MessageSourceAware extends Aware {

	/**
	 * 设置此对象在其中运行的MessageSource.
	 * <p>在填充常规bean属性之后但在诸如InitializingBean的afterPropertiesSet或自定义init方法之类的init回调之前调用.
	 * 在ApplicationContextAware的setApplicationContext之前调用.
	 * @param messageSource message source to be used by this object
	 */
	void setMessageSource(MessageSource messageSource);

}
