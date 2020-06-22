/*
 * Copyright 2002-2020 the original author or authors.
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

package org.springframework.core.env;

import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * 由简单的String数组支持的{@link CommandLinePropertySource}实现。
 *
 * <h3>目的</h3>
 * <p>此{@code CommandLinePropertySource}实现旨在提供最简单的方法来解析命令行参数。 
 * 与所有{@code CommandLinePropertySource}实现一样，命令行参数分为两个不同的组：选项参数和非选项参数，
 * 如下所述（从Javadoc复制用于{@link SimpleCommandLineArgsParser}的一些部分）：
 *
 * <h3>使用选项参数</h3>
 * <p>选项参数必须遵循确切的语法：
 *
 * <pre class="code">--optName[=optValue]</pre>
 *
 * <p>也就是说，选项必须以{@code --}为前缀，并且可以指定也可以不指定值。 如果指定了值，则名称和值必须用等号（"="）隔开，
 * 且不能使用空格。 该值可以是一个空字符串。
 *
 * <h4>Valid examples of option arguments</h4>
 * <pre class="code">
 * --foo
 * --foo=
 * --foo=""
 * --foo=bar
 * --foo="bar then baz"
 * --foo=bar,baz,biz</pre>
 *
 * <h4>选项参数的无效示例</h4>
 * <pre class="code">
 * -foo
 * --foo bar
 * --foo = bar
 * --foo=bar --foo=baz --foo=biz</pre>
 *
 * <h3>使用非选项参数</h3>
 * <p>在命令行中指定的所有不带"{@code --}"选项前缀的参数都将被视为“非选项参数”，
 * 并可以通过{@link CommandLineArgs#getNonOptionArgs()}方法使用。
 *
 * <h3>Typical usage</h3>
 * <pre class="code">
 * public static void main(String[] args) {
 *     PropertySource<?> ps = new SimpleCommandLinePropertySource(args);
 *     // ...
 * }</pre>
 *
 * See {@link CommandLinePropertySource} for complete general usage examples.
 *
 * <h3>Beyond the basics</h3>
 *
 * <p>当需要更全功能的命令行解析时，请考虑使用提供的{@link JOptCommandLinePropertySource}，
 * 或针对您选择的命令行解析库实现自己的{@code CommandLinePropertySource}。
 *
 * @author Chris Beams
 * @since 3.1
 * @see CommandLinePropertySource
 * @see JOptCommandLinePropertySource
 */
public class SimpleCommandLinePropertySource extends CommandLinePropertySource<CommandLineArgs> {

	/**
	 * Create a new {@code SimpleCommandLinePropertySource} having the default name
	 * and backed by the given {@code String[]} of command line arguments.
	 * @see CommandLinePropertySource#COMMAND_LINE_PROPERTY_SOURCE_NAME
	 * @see CommandLinePropertySource#CommandLinePropertySource(Object)
	 */
	public SimpleCommandLinePropertySource(String... args) {
		super(new SimpleCommandLineArgsParser().parse(args));
	}

	/**
	 * Create a new {@code SimpleCommandLinePropertySource} having the given name
	 * and backed by the given {@code String[]} of command line arguments.
	 */
	public SimpleCommandLinePropertySource(String name, String[] args) {
		super(name, new SimpleCommandLineArgsParser().parse(args));
	}

	/**
	 * Get the property names for the option arguments.
	 */
	@Override
	public String[] getPropertyNames() {
		return StringUtils.toStringArray(this.source.getOptionNames());
	}

	@Override
	protected boolean containsOption(String name) {
		return this.source.containsOption(name);
	}

	@Override
	@Nullable
	protected List<String> getOptionValues(String name) {
		return this.source.getOptionValues(name);
	}

	@Override
	protected List<String> getNonOptionArgs() {
		return this.source.getNonOptionArgs();
	}

}
