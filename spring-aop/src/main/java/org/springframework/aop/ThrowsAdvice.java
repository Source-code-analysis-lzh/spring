/*
 * Copyright 2002-2008 the original author or authors.
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

package org.springframework.aop;

/**
 * throws advice标记接口。
 *
 * <p>此接口上没有任何方法，因为方法是通过反射调用的。 实现类必须实现以下形式的方法：
 *
 * <pre class="code">void afterThrowing([Method, args, target], ThrowableSubclass);</pre>
 *
 * <p>有效方法的一些示例如下：
 *
 * <pre class="code">public void afterThrowing(Exception ex)</pre>
 * <pre class="code">public void afterThrowing(RemoteException)</pre>
 * <pre class="code">public void afterThrowing(Method method, Object[] args, Object target, Exception ex)</pre>
 * <pre class="code">public void afterThrowing(Method method, Object[] args, Object target, ServletException ex)</pre>
 *
 * 前三个参数是可选的，并且仅在我们需要有关连接点的更多信息时才有用，例如在AspectJ <b>after-throwing</b>中。
 *
 * <p>如果throws-advice方法本身引发异常，它将覆盖原始异常（即，将更改最初的异常给用户）。 
 * 覆盖的异常通常是RuntimeException。 这与任何方法签名都兼容。 
 * 但是，如果throws-advice方法抛出一个检查异常，则它必须与目标方法的已声明异常匹配，
 * 因此在某种程度上与特定的目标方法签名相关。 不要抛出与目标方法签名不兼容的未声明检查异常！
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see AfterReturningAdvice
 * @see MethodBeforeAdvice
 */
public interface ThrowsAdvice extends AfterAdvice {

}
