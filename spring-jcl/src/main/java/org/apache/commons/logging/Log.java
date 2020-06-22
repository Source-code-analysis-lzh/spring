/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.logging;

/**
 * 一个简单的日志记录接口，抽象了日志记录API。 
 * 为了被{@link LogFactory}成功实例化，实现此接口的类必须具有一个构造函数，
 * 该构造函数采用单个String参数表示此Log的“名称”。
 *
 * <p>Log使用的六个<code>Log</code>记录级别是（按顺序）：
 * <ol>
 * <li>trace (the least serious)</li>
 * <li>debug</li>
 * <li>info</li>
 * <li>warn</li>
 * <li>error</li>
 * <li>fatal (the most serious)</li>
 * </ol>
 *
 * 这些日志级别到底层日志系统使用的概念的映射取决于实现。 但是，实现应确保此排序行为符合预期。
 *
 * <p>性能通常是一个日志关心的问题。 
 * 通过检查适当的属性，组件可以避免进行昂贵的操作（生成要记录的信息）。
 *
 * <p>For example,
 * <pre>
 *    if (log.isDebugEnabled()) {
 *        ... do something expensive ...
 *        log.debug(theResult);
 *    }
 * </pre>
 *
 * <p>底层日志系统的配置通常将通过该系统支持的任何机制在日志API外部进行。
 *
 * @author Juergen Hoeller (for the {@code spring-jcl} variant)
 * @since 5.0
 */
public interface Log {

	/**
	 * Is fatal logging currently enabled?
	 * <p>Call this method to prevent having to perform expensive operations
	 * (for example, <code>String</code> concatenation)
	 * when the log level is more than fatal.
	 * @return true if fatal is enabled in the underlying logger.
	 */
	boolean isFatalEnabled();

	/**
	 * Is error logging currently enabled?
	 * <p>Call this method to prevent having to perform expensive operations
	 * (for example, <code>String</code> concatenation)
	 * when the log level is more than error.
	 * @return true if error is enabled in the underlying logger.
	 */
	boolean isErrorEnabled();

	/**
	 * Is warn logging currently enabled?
	 * <p>Call this method to prevent having to perform expensive operations
	 * (for example, <code>String</code> concatenation)
	 * when the log level is more than warn.
	 * @return true if warn is enabled in the underlying logger.
	 */
	boolean isWarnEnabled();

	/**
	 * Is info logging currently enabled?
	 * <p>Call this method to prevent having to perform expensive operations
	 * (for example, <code>String</code> concatenation)
	 * when the log level is more than info.
	 * @return true if info is enabled in the underlying logger.
	 */
	boolean isInfoEnabled();

	/**
	 * Is debug logging currently enabled?
	 * <p>Call this method to prevent having to perform expensive operations
	 * (for example, <code>String</code> concatenation)
	 * when the log level is more than debug.
	 * @return true if debug is enabled in the underlying logger.
	 */
	boolean isDebugEnabled();

	/**
	 * Is trace logging currently enabled?
	 * <p>Call this method to prevent having to perform expensive operations
	 * (for example, <code>String</code> concatenation)
	 * when the log level is more than trace.
	 * @return true if trace is enabled in the underlying logger.
	 */
	boolean isTraceEnabled();


	/**
	 * Logs a message with fatal log level.
	 * @param message log this message
	 */
	void fatal(Object message);

	/**
	 * Logs an error with fatal log level.
	 * @param message log this message
	 * @param t log this cause
	 */
	void fatal(Object message, Throwable t);

	/**
	 * Logs a message with error log level.
	 * @param message log this message
	 */
	void error(Object message);

	/**
	 * Logs an error with error log level.
	 * @param message log this message
	 * @param t log this cause
	 */
	void error(Object message, Throwable t);

	/**
	 * Logs a message with warn log level.
	 * @param message log this message
	 */
	void warn(Object message);

	/**
	 * Logs an error with warn log level.
	 * @param message log this message
	 * @param t log this cause
	 */
	void warn(Object message, Throwable t);

	/**
	 * Logs a message with info log level.
	 * @param message log this message
	 */
	void info(Object message);

	/**
	 * Logs an error with info log level.
	 * @param message log this message
	 * @param t log this cause
	 */
	void info(Object message, Throwable t);

	/**
	 * Logs a message with debug log level.
	 * @param message log this message
	 */
	void debug(Object message);

	/**
	 * Logs an error with debug log level.
	 * @param message log this message
	 * @param t log this cause
	 */
	void debug(Object message, Throwable t);

	/**
	 * Logs a message with trace log level.
	 * @param message log this message
	 */
	void trace(Object message);

	/**
	 * Logs an error with trace log level.
	 * @param message log this message
	 * @param t log this cause
	 */
	void trace(Object message, Throwable t);

}
