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

package org.springframework.scheduling.quartz;

import org.springframework.core.NestedRuntimeException;
import org.springframework.util.MethodInvoker;

/**
 * 包含从目标方法抛出的异常的未经检查的异常。从Job传播到Quartz调度程序，该Job反射性地调用任意目标方法。
 *
 * @author Juergen Hoeller
 * @since 2.5.3
 * @see MethodInvokingJobDetailFactoryBean
 */
@SuppressWarnings("serial")
public class JobMethodInvocationFailedException extends NestedRuntimeException {

	/**
	 * Constructor for JobMethodInvocationFailedException.
	 * @param methodInvoker the MethodInvoker used for reflective invocation
	 * @param cause the root cause (as thrown from the target method)
	 */
	public JobMethodInvocationFailedException(MethodInvoker methodInvoker, Throwable cause) {
		super("Invocation of method '" + methodInvoker.getTargetMethod() +
				"' on target class [" + methodInvoker.getTargetClass() + "] failed", cause);
	}

}
