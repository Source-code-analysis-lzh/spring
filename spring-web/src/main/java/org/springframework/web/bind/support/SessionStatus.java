/*
 * Copyright 2002-2007 the original author or authors.
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

package org.springframework.web.bind.support;

/**
 * 可以注入到处理器方法中的简单接口，使它们可以发出其会话处理已完成的信号。 
 * 然后，处理器调用者可以进行适当的清理，例如清理在此处理器的处理期间隐式创建的会话属性
 * （根据{@link org.springframework.web.bind.annotation.SessionAttributes @SessionAttributes}
 *  annotation)注释）。
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see org.springframework.web.bind.annotation.RequestMapping
 * @see org.springframework.web.bind.annotation.SessionAttributes
 */
public interface SessionStatus {

	/**
	 * 将当前处理器的会话处理标记为已完成，以便允许清除会话属性。
	 */
	void setComplete();

	/**
	 * 返回当前处理器的会话处理是否已标记为完成。
	 */
	boolean isComplete();

}
