/**
 * <a href="https://commons.apache.org/logging"> Commons Logging API </a>的Spring变体：
 * 对Log4J 2，SLF4J和{@code java.util.logging}的特殊支持。
 *
 * <p>这是和{@code jcl-over-slf4j}类似的自定义桥。
 * 如果您更喜欢硬绑定SLF4J桥，则可以排除{@code spring-jcl}并切换到{@code jcl-over-slf4j}。 
 * 但是，当使用Log4J 2或{@code java.util.logging}时，Spring自己的桥提供了更好的即用即用体验，
 * 无需额外的网jar包，并且通过Logback设置SLF4J更加容易（不包括JCL，没有JCL桥）。
 *
 * <p>{@link org.apache.commons.logging.Log}等同于原始版本。
 * 但是，{@link org.apache.commons.logging.LogFactory}是一个非常不同的实现，
 * 已针对Spring的目的对其进行了最小化和优化，在框架类路径中检测到Log4J 2.x和SLF4J 1.7，
 * 并退回到{@code java.util.logging}。 如果您对此实现遇到任何问题，
 * 请考虑排除{@code spring-jcl}并切换到标准{@code commons-logging}包或切换到{@code jcl-over-slf4j}。
 *
 * <p>请注意，此Commons Logging桥仅用于核心框架和扩展中的框架日志记录。
 * 对于应用程序，最好直接使用Log4J/SLF4J或{@code java.util.logging}。
 */
package org.apache.commons.logging;
