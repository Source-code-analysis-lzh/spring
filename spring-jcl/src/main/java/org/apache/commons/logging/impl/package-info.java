/**
 * <a href="https://commons.apache.org/logging"> Commons Logging API </a>的Spring变体：
 * 对Log4J 2，SLF4J和{@code java.util.logging}的特殊支持。
 *
 * <p>仅提供此{@code impl}软件包是为了与现有的Commons Logging用法二进制兼容。
 * 如，在Commons Configuration中。 {@code NoOpLog}可以用作{@code Log}的后备实例，
 * 而{@code SimpleLog}并不起作用（使用时发出警告）。
 */
package org.apache.commons.logging.impl;
