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

package org.springframework.core.env;

import java.util.Collection;
import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * 由命令行参数支持的{@link PropertySource}实现的抽象基类。 参数化的类型{@code T}表示命令行选项的底层源。 
 * 在{@link SimpleCommandLinePropertySource}的情况下，它可以像String数组一样简单，
 * 在{@link JOptCommandLinePropertySource}的情况下，它具有特定的API，例如JOpt的{@code OptionSet}。
 *
 * <h3>目的和一般用途</h3>
 *
 * 适用于独立的基于Spring的应用程序，即通过传统的{@code main}方法启动的程序，该方法从命令行接受参数的{@code String[]}。 
 * 在许多情况下，直接在{@code main}方法内处理命令行参数就足够了，但是在其他情况下，可能需要将参数作为值注入到Spring bean中。 
 * 在后一种情况下，{@code CommandLinePropertySource}变得有用。 通常，会将{@code CommandLinePropertySource}
 * 添加到Spring {@code ApplicationContext}的{@link Environment}中，此时，所有命令行参数都可以通过
 * {@link Environment#getProperty(String)}方法族获取。 例如：
 *
 * <pre class="code">
 * public static void main(String[] args) {
 *     CommandLinePropertySource clps = ...;
 *     AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
 *     ctx.getEnvironment().getPropertySources().addFirst(clps);
 *     ctx.register(AppConfig.class);
 *     ctx.refresh();
 * }</pre>
 *
 * With the bootstrap logic above, the {@code AppConfig} class may {@code @Inject} the
 * Spring {@code Environment} and query it directly for properties:
 * 使用上面的启动逻辑，{@code AppConfig}类可以{@code @Inject} Spring {@code Environment}并直接向其查询属性：
 *
 * <pre class="code">
 * &#064;Configuration
 * public class AppConfig {
 *
 *     &#064;Inject Environment env;
 *
 *     &#064;Bean
 *     public void DataSource dataSource() {
 *         MyVendorDataSource dataSource = new MyVendorDataSource();
 *         dataSource.setHostname(env.getProperty("db.hostname", "localhost"));
 *         dataSource.setUsername(env.getRequiredProperty("db.username"));
 *         dataSource.setPassword(env.getRequiredProperty("db.password"));
 *         // ...
 *         return dataSource;
 *     }
 * }</pre>
 *
 * 因为{@code CommandLinePropertySource}是使用{@code #addFirst}方法添加到{@code Environment}的
 * {@link MutablePropertySources}集合中的，所以它具有最高的搜索优先级，这意味着，虽然"db.hostname"
 * 和其他属性可能存在于其他属性源中，例如系统环境变量，但它将是首先从命令行属性源中选择。 
 * 考虑到命令行上指定的参数自然比指定为环境变量的参数更具体，因此这是一种合理的方法。
 *
 * <p>作为注入{@code Environment}的替代方法，考虑到已经直接或通过使用{@code <context:property-placeholder>}
 * 元素注册了{@link PropertySourcesPropertyResolver} bean，可以使用Spring的{@code @Value}注释注入这些属性。 例如：
 *
 * <pre class="code">
 * &#064;Component
 * public class MyComponent {
 *
 *     &#064;Value("my.property:defaultVal")
 *     private String myProperty;
 *
 *     public void getMyProperty() {
 *         return this.myProperty;
 *     }
 *
 *     // ...
 * }</pre>
 *
 * <h3>使用选项参数</h3>
 *
 * <p>Individual command line arguments are represented as properties through the usual
 * {@link PropertySource#getProperty(String)} and
 * {@link PropertySource#containsProperty(String)} methods. For example, given the
 * following command line:
 * <p>各个命令行参数通过常用的{@link PropertySource#getProperty(String)}和
 * {@link PropertySource#containsProperty(String)}方法表示为属性。 例如，给定以下命令行：
 *
 * <pre class="code">--o1=v1 --o2</pre>
 *
 * 'o1' and 'o2' are treated as "option arguments", and the following assertions would
 * evaluate true:
 * 'o1'和'o2'被视为“选项参数”，并且以下断言将评估为true：
 *
 * <pre class="code">
 * CommandLinePropertySource<?> ps = ...
 * assert ps.containsProperty("o1") == true;
 * assert ps.containsProperty("o2") == true;
 * assert ps.containsProperty("o3") == false;
 * assert ps.getProperty("o1").equals("v1");
 * assert ps.getProperty("o2").equals("");
 * assert ps.getProperty("o3") == null;
 * </pre>
 *
 * 请注意，'o2'选项没有参数，但是{@code getProperty("o2")}解析为空字符串（{@code ""}）而不是{@code null}，
 * 而{@code getProperty("o3")}解析为{@code null}，因为未指定。 此行为与所有{@code PropertySource}实现遵循的一般约定一致。
 *
 * <p>还要注意，尽管在上面的示例中使用"--"来表示选项参数，但是此语法在各个命令行参数库中可能有所不同。 
 * 例如，基于JOpt-或Commons CLI的实现可能允许使用单破折号（“-”）短选项参数，等等。
 *
 * <h3>使用非选项参数</h3>
 *
 * <p>此抽象还支持非选项参数。 提供的没有选项样式前缀的任何参数（例如“-”或“--”）均被视为“非选项参数”，
 * 可通过指定的{@linkplain #DEFAULT_NON_OPTION_ARGS_PROPERTY_NAME "nonOptionArgs"}属性使用。 
 * 如果指定了多个非选项参数，则此属性的值将是一个包含所有参数的逗号分隔的字符串。 
 * 这种方法可以确保{@code CommandLinePropertySource}中所有属性的返回类型简单且一致，
 * 并且与Spring {@link Environment}及其内置的{@code ConversionService}结合使用时，可以很容易地进行转换。 考虑以下示例：
 *
 * <pre class="code">--o1=v1 --o2=v2 /path/to/file1 /path/to/file2</pre>
 *
 * 在此示例中，“ o1”和“ o2”将被视为“选项参数”，而两个文件系统路径则被视为“非选项参数”。 因此，以下断言将计算为true：
 *
 * <pre class="code">
 * CommandLinePropertySource<?> ps = ...
 * assert ps.containsProperty("o1") == true;
 * assert ps.containsProperty("o2") == true;
 * assert ps.containsProperty("nonOptionArgs") == true;
 * assert ps.getProperty("o1").equals("v1");
 * assert ps.getProperty("o2").equals("v2");
 * assert ps.getProperty("nonOptionArgs").equals("/path/to/file1,/path/to/file2");
 * </pre>
 *
 * <p>如上所述，当与Spring {@code Environment}抽象结合使用时，此逗号分隔的字符串可以轻松转换为String数组或列表：
 *
 * <pre class="code">
 * Environment env = applicationContext.getEnvironment();
 * String[] nonOptionArgs = env.getProperty("nonOptionArgs", String[].class);
 * assert nonOptionArgs[0].equals("/path/to/file1");
 * assert nonOptionArgs[1].equals("/path/to/file2");
 * </pre>
 *
 * <p>特殊的“非选项参数”属性的名称可以通过{@link #setNonOptionArgsPropertyName(String)}方法进行自定义。 
 * 建议这样做，因为它为非选项参数提供了适当的语义值。 例如，如果将文件系统路径指定为非选项参数，则最好将它们称为"file.locations"，
 * 而不是默认的"nonOptionArgs"：
 *
 * <pre class="code">
 * public static void main(String[] args) {
 *     CommandLinePropertySource clps = ...;
 *     clps.setNonOptionArgsPropertyName("file.locations");
 *
 *     AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
 *     ctx.getEnvironment().getPropertySources().addFirst(clps);
 *     ctx.register(AppConfig.class);
 *     ctx.refresh();
 * }</pre>
 *
 * <h3>限制</h3>
 *
 * 此抽象无意于展现底层命令行解析API（如JOpt或Commons CLI）的全部功能。 它的意图恰恰相反：提供对解析后的命令行参数的访问的最简单抽象。
 * 因此，典型情况将涉及完全配置底层命令行解析API，解析进入main方法的参数的{@code String[]}，
 * 然后简单地将解析结果提供给{@code CommandLinePropertySource}的实现。 在这一点上，所有参数都可以被视为“选项”或“非选项”参数，
 * 并且如上所述，可以通过常规的{@code PropertySource}和{@code Environment} API进行访问。
 *
 * @author Chris Beams
 * @since 3.1
 * @param <T> the source type
 * @see PropertySource
 * @see SimpleCommandLinePropertySource
 * @see JOptCommandLinePropertySource
 */
public abstract class CommandLinePropertySource<T> extends EnumerablePropertySource<T> {

	/** The default name given to {@link CommandLinePropertySource} instances: {@value}. */
	public static final String COMMAND_LINE_PROPERTY_SOURCE_NAME = "commandLineArgs";

	/** The default name of the property representing non-option arguments: {@value}. */
	public static final String DEFAULT_NON_OPTION_ARGS_PROPERTY_NAME = "nonOptionArgs";


	private String nonOptionArgsPropertyName = DEFAULT_NON_OPTION_ARGS_PROPERTY_NAME;


	/**
	 * Create a new {@code CommandLinePropertySource} having the default name
	 * {@value #COMMAND_LINE_PROPERTY_SOURCE_NAME} and backed by the given source object.
	 */
	public CommandLinePropertySource(T source) {
		super(COMMAND_LINE_PROPERTY_SOURCE_NAME, source);
	}

	/**
	 * Create a new {@link CommandLinePropertySource} having the given name
	 * and backed by the given source object.
	 */
	public CommandLinePropertySource(String name, T source) {
		super(name, source);
	}


	/**
	 * Specify the name of the special "non-option arguments" property.
	 * The default is {@value #DEFAULT_NON_OPTION_ARGS_PROPERTY_NAME}.
	 */
	public void setNonOptionArgsPropertyName(String nonOptionArgsPropertyName) {
		this.nonOptionArgsPropertyName = nonOptionArgsPropertyName;
	}

	/**
	 * This implementation first checks to see if the name specified is the special
	 * {@linkplain #setNonOptionArgsPropertyName(String) "non-option arguments" property},
	 * and if so delegates to the abstract {@link #getNonOptionArgs()} method
	 * checking to see whether it returns an empty collection. Otherwise delegates to and
	 * returns the value of the abstract {@link #containsOption(String)} method.
	 */
	@Override
	public final boolean containsProperty(String name) {
		if (this.nonOptionArgsPropertyName.equals(name)) {
			return !this.getNonOptionArgs().isEmpty();
		}
		return this.containsOption(name);
	}

	/**
	 * This implementation first checks to see if the name specified is the special
	 * {@linkplain #setNonOptionArgsPropertyName(String) "non-option arguments" property},
	 * and if so delegates to the abstract {@link #getNonOptionArgs()} method. If so
	 * and the collection of non-option arguments is empty, this method returns {@code
	 * null}. If not empty, it returns a comma-separated String of all non-option
	 * arguments. Otherwise delegates to and returns the result of the abstract {@link
	 * #getOptionValues(String)} method.
	 */
	@Override
	@Nullable
	public final String getProperty(String name) {
		if (this.nonOptionArgsPropertyName.equals(name)) {
			Collection<String> nonOptionArguments = this.getNonOptionArgs();
			if (nonOptionArguments.isEmpty()) {
				return null;
			}
			else {
				return StringUtils.collectionToCommaDelimitedString(nonOptionArguments);
			}
		}
		Collection<String> optionValues = this.getOptionValues(name);
		if (optionValues == null) {
			return null;
		}
		else {
			return StringUtils.collectionToCommaDelimitedString(optionValues);
		}
	}


	/**
	 * Return whether the set of option arguments parsed from the command line contains
	 * an option with the given name.
	 */
	protected abstract boolean containsOption(String name);

	/**
	 * Return the collection of values associated with the command line option having the
	 * given name.
	 * <ul>
	 * <li>if the option is present and has no argument (e.g.: "--foo"), return an empty
	 * collection ({@code []})</li>
	 * <li>if the option is present and has a single value (e.g. "--foo=bar"), return a
	 * collection having one element ({@code ["bar"]})</li>
	 * <li>if the option is present and the underlying command line parsing library
	 * supports multiple arguments (e.g. "--foo=bar --foo=baz"), return a collection
	 * having elements for each value ({@code ["bar", "baz"]})</li>
	 * <li>if the option is not present, return {@code null}</li>
	 * </ul>
	 */
	@Nullable
	protected abstract List<String> getOptionValues(String name);

	/**
	 * Return the collection of non-option arguments parsed from the command line.
	 * Never {@code null}.
	 */
	protected abstract List<String> getNonOptionArgs();

}
