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

package org.apache.commons.logging;

/**
 * LogFactory调用静态方法 getLog，通过适配器LogAdapter返回具体的LOGS对象.
 * Apache Commons Logging的{@code LogFactory} API的最小化形式，仅提供了常见的{@link Log}查找方法。 
 * 这是受JCL-over-SLF4J桥的启发，并且应该与Commons Logging API的所有常用用法
 * （特别是：带有{@code LogFactory.getLog(Class/String)}字段初始化程序）的源代码和二进制文件兼容。
 *
 * <p>此实现不支持Commons Logging的原始提供程序检测。 而是只检查Spring Framework类路径中
 * Log4j 2.x API和SLF4J 1.7 API是否存在，如果两者都不可用，则退回到{@code java.util.logging}。
 * 从这个意义上讲，它可以替代Log4j 2 Commons Logging桥接器以及JCL-over-SLF4J桥接器，
 * 因此，这两者对于基于Spring的设置都是无关紧要的（不需要手动排除标准 Commons Logging API jar也可以）。 
 * 此外，对于没有外部日志记录提供程序的简单设置，Spring在类路径上不再需要任何额外的jar，
 * 因为在这种情况下，此嵌入式日志工厂会自动委托给{@code java.util.logging}。
 *
 * <p>请注意，此Commons Logging变体仅用于核心框架和扩展中的基础结构日志记录目的，
 * 它还用作使用Commons Logging API的第三方库的通用桥梁。 如 Apache HttpClient和HtmlUnit，
 * 使它们进入相同的一致安排，而无需任何额外的桥接包。
 *
 * <p>对于应用程序代码中的日志记录需求，建议直接使用Log4j 2.x或SLF4J或{@code java.util.logging}。
 * 只需将Log4j 2.x或Logback（或其他SLF4J提供程序）放在您的类路径中，没有任何额外的桥梁，让框架自动适应您的选择。
 *
 * @author Juergen Hoeller (for the {@code spring-jcl} variant)
 * @since 5.0
 */
public abstract class LogFactory {

	/**
	 * 返回命名记录器的便捷方法。
	 * @param clazz containing Class from which a log name will be derived
	 */
	public static Log getLog(Class<?> clazz) {
		return getLog(clazz.getName());
	}

	/**
	 * Convenience method to return a named logger.
	 * @param name logical name of the <code>Log</code> instance to be returned
	 */
	public static Log getLog(String name) {
		return LogAdapter.createLog(name);
	}


	/**
	 * This method only exists for compatibility with unusual Commons Logging API
	 * usage like e.g. {@code LogFactory.getFactory().getInstance(Class/String)}.
	 * @see #getInstance(Class)
	 * @see #getInstance(String)
	 * @deprecated in favor of {@link #getLog(Class)}/{@link #getLog(String)}
	 */
	@Deprecated
	public static LogFactory getFactory() {
		return new LogFactory() {};
	}

	/**
	 * Convenience method to return a named logger.
	 * <p>This variant just dispatches straight to {@link #getLog(Class)}.
	 * @param clazz containing Class from which a log name will be derived
	 * @deprecated in favor of {@link #getLog(Class)}
	 */
	@Deprecated
	public Log getInstance(Class<?> clazz) {
		return getLog(clazz);
	}

	/**
	 * Convenience method to return a named logger.
	 * <p>This variant just dispatches straight to {@link #getLog(String)}.
	 * @param name logical name of the <code>Log</code> instance to be returned
	 * @deprecated in favor of {@link #getLog(String)}
	 */
	@Deprecated
	public Log getInstance(String name) {
		return getLog(name);
	}

}
