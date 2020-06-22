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

package org.springframework.core.env;

import java.security.AccessControlException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.SpringProperties;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * {@link Environment}实现的抽象基类。 支持保留的默认配置(profile)文件名称的概念，
 * 并允许通过{@link #ACTIVE_PROFILES_PROPERTY_NAME}和{@link #DEFAULT_PROFILES_PROPERTY_NAME}属性指定活动和默认配置文件。
 *
 * <p>具体的子类主要区别在于默认情况下添加的{@link PropertySource}对象。 {@code AbstractEnvironment}不添加任何内容。 
 * 子类应通过受保护的{@link #customizePropertySources(MutablePropertySources)}挂钩提供属性源，
 * 而客户端应使用{@link ConfigurableEnvironment#getPropertySources()}进行自定义，并使用{@link MutablePropertySources} API。 
 * 有关用法示例，请参见{@link ConfigurableEnvironment} javadoc。
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see ConfigurableEnvironment
 * @see StandardEnvironment
 */
public abstract class AbstractEnvironment implements ConfigurableEnvironment {

	/**
	 * 指示Spring忽略系统环境变量的系统属性，即从不尝试通过{@link System#getenv()}检索此类变量。
	 * <p>默认值为"false"，回退到系统环境变量，检查Spring环境属性（例如配置字符串中的占位符）是否不可解析。 
	 * 如果您遇到来自Spring的{@code getenv}调用的日志警告，请考虑将此标志设置为"true"，例如 
	 * 在WebSphere上具有严格的SecurityManager设置和AccessControlExceptions警告。
	 * @see #suppressGetenvAccess()
	 */
	public static final String IGNORE_GETENV_PROPERTY_NAME = "spring.getenv.ignore";

	/**
	 * 要设置为指定活动配置(profiles)文件的属性名称：{@value}。 值可以用逗号分隔。
	 * <p>请注意，某些shell环境（例如Bash）不允许在变量名称中使用句点字符。 
	 * 假设正在使用Spring的{@link SystemEnvironmentPropertySource}，
	 * 则可以将此属性指定为{@code SPRING_PROFILES_ACTIVE}作为环境变量。
	 * @see ConfigurableEnvironment#setActiveProfiles
	 */
	public static final String ACTIVE_PROFILES_PROPERTY_NAME = "spring.profiles.active";

	/**
	 * 设置为指定默认情况下处于活动状态的配置(profiles)文件的属性名称：{@value}。 值可以用逗号分隔。
	 * <p>请注意，某些shell环境（例如Bash）不允许在变量名称中使用句点字符。 
	 * 假设正在使用Spring的{@link SystemEnvironmentPropertySource}，
	 * 则可以将此属性指定为{@code SPRING_PROFILES_DEFAULT}作为环境变量。
	 * @see ConfigurableEnvironment#setDefaultProfiles
	 */
	public static final String DEFAULT_PROFILES_PROPERTY_NAME = "spring.profiles.default";

	/**
	 * 保留的默认配置文件名称的名称：{@value}。 如果未明确指定默认配置文件名称，也未明确设置活动配置文件名称，
	 * 则默认情况下将自动激活此配置文件。
	 * @see #getReservedDefaultProfiles
	 * @see ConfigurableEnvironment#setDefaultProfiles
	 * @see ConfigurableEnvironment#setActiveProfiles
	 * @see AbstractEnvironment#DEFAULT_PROFILES_PROPERTY_NAME
	 * @see AbstractEnvironment#ACTIVE_PROFILES_PROPERTY_NAME
	 */
	protected static final String RESERVED_DEFAULT_PROFILE_NAME = "default";


	protected final Log logger = LogFactory.getLog(getClass());

	private final Set<String> activeProfiles = new LinkedHashSet<>();

	private final Set<String> defaultProfiles = new LinkedHashSet<>(getReservedDefaultProfiles());

	private final MutablePropertySources propertySources = new MutablePropertySources();

	private final ConfigurablePropertyResolver propertyResolver =
			new PropertySourcesPropertyResolver(this.propertySources);


	/**
	 * 创建一个新的{@code Environment}实例，在构造过程中回调{@link #customizePropertySources(MutablePropertySources)}，
	 * 以允许子类酌情提供或操纵{@link PropertySource}实例。
	 * @see #customizePropertySources(MutablePropertySources)
	 */
	public AbstractEnvironment() {
		customizePropertySources(this.propertySources);
	}


	/**
	 * 自定义在调用{@link #getProperty(String)}和相关方法期间此{@code Environment}
	 * 要搜索的{@link PropertySource}对象集。
	 *
	 * <p>鼓励重写此方法的子类使用{@link MutablePropertySources#addLast(PropertySource)}添加属性源，
	 * 以便其它子类可以调用具有可预测结果的{@code super.customizePropertySources()}。 例如：
	 * <pre class="code">
	 * public class Level1Environment extends AbstractEnvironment {
	 *     &#064;Override
	 *     protected void customizePropertySources(MutablePropertySources propertySources) {
	 *         super.customizePropertySources(propertySources); // no-op from base class
	 *         propertySources.addLast(new PropertySourceA(...));
	 *         propertySources.addLast(new PropertySourceB(...));
	 *     }
	 * }
	 *
	 * public class Level2Environment extends Level1Environment {
	 *     &#064;Override
	 *     protected void customizePropertySources(MutablePropertySources propertySources) {
	 *         super.customizePropertySources(propertySources); // add all from superclass
	 *         propertySources.addLast(new PropertySourceC(...));
	 *         propertySources.addLast(new PropertySourceD(...));
	 *     }
	 * }
	 * </pre>
	 * 在这种安排下，将按照源A，B，C，D的顺序解析属性。 也就是说，属性源"A"优先于属性源"D"。 
	 * 如果{@code Level2Environment}子类希望为属性源C和D提供比A和B更高的优先级，
	 * 则可以在之后而不是在添加其自身之前简单地调用{@code super.customizePropertySources}：
	 * <pre class="code">
	 * public class Level2Environment extends Level1Environment {
	 *     &#064;Override
	 *     protected void customizePropertySources(MutablePropertySources propertySources) {
	 *         propertySources.addLast(new PropertySourceC(...));
	 *         propertySources.addLast(new PropertySourceD(...));
	 *         super.customizePropertySources(propertySources); // add all from superclass
	 *     }
	 * }
	 * </pre>
	 * 现在，根据需要，搜索顺序为C，D，A，B。
	 *
	 * <p>除了这些建议之外，子类可以使用{@link MutablePropertySources}公开的任何{@code add&#42;}，
	 * {@code remove}或{@code replace}方法来创建所需属性源的精确排列。
	 *
	 * <p>基类实现未注册任何属性源。
	 *
	 * <p>请注意，任何{@link ConfigurableEnvironment}的客户端都可以通过{@link #getPropertySources()}
	 * 访问器（通常在{@link org.springframework.context.ApplicationContextInitializer
	 * ApplicationContextInitializer}中）进一步自定义属性源。 例如：
	 * <pre class="code">
	 * ConfigurableEnvironment env = new StandardEnvironment();
	 * env.getPropertySources().addLast(new PropertySourceX(...));
	 * </pre>
	 *
	 * <h2>有关实例变量访问的警告</h2>
	 * Instance variables declared in subclasses and having default initial values should
	 * <em>not</em> be accessed from within this method. Due to Java object creation
	 * lifecycle constraints, any initial value will not yet be assigned when this
	 * callback is invoked by the {@link #AbstractEnvironment()} constructor, which may
	 * lead to a {@code NullPointerException} or other problems. If you need to access
	 * default values of instance variables, leave this method as a no-op and perform
	 * property source manipulation and instance variable access directly within the
	 * subclass constructor. Note that <em>assigning</em> values to instance variables is
	 * not problematic; it is only attempting to read default values that must be avoided.
	 * 在子类中声明的具有默认初始值的实例变量不应在此方法内访问。 由于Java对象创建生命周期的限制，
	 * 当{@link #AbstractEnvironment()}构造函数调用此回调时，尚未分配任何初始值，这可能导致{@code NullPointerException}
	 * 或其他问题。 如果需要访问实例变量的默认值，请将此方法保留为空操作，并直接在子类构造函数中执行属性源操作和实例变量访问。 
	 * 注意，给实例变量赋值是没有问题的。 它只是试图读取必须避免的默认值。
	 *
	 * @see MutablePropertySources
	 * @see PropertySourcesPropertyResolver
	 * @see org.springframework.context.ApplicationContextInitializer
	 */
	protected void customizePropertySources(MutablePropertySources propertySources) {
	}

	/**
	 * 返回保留的默认配置文件名称集。 此实现返回{@value #RESERVED_DEFAULT_PROFILE_NAME}。 
	 * 子类可以重写以自定义保留名称集。
	 * @see #RESERVED_DEFAULT_PROFILE_NAME
	 * @see #doGetDefaultProfiles()
	 */
	protected Set<String> getReservedDefaultProfiles() {
		return Collections.singleton(RESERVED_DEFAULT_PROFILE_NAME);
	}


	//---------------------------------------------------------------------
	// Implementation of ConfigurableEnvironment interface
	//---------------------------------------------------------------------

	@Override
	public String[] getActiveProfiles() {
		return StringUtils.toStringArray(doGetActiveProfiles());
	}

	/**
	 * 返回通过{@link #setActiveProfiles}显式设置的活动配置文件集，或者如果当前活动配置文件集为空，
	 * 请检查是否存在{@value #ACTIVE_PROFILES_PROPERTY_NAME}属性并赋予其值为活动配置文件集。
	 * @see #getActiveProfiles()
	 * @see #ACTIVE_PROFILES_PROPERTY_NAME
	 */
	protected Set<String> doGetActiveProfiles() {
		synchronized (this.activeProfiles) {
			if (this.activeProfiles.isEmpty()) {
				String profiles = getProperty(ACTIVE_PROFILES_PROPERTY_NAME);
				if (StringUtils.hasText(profiles)) {
					setActiveProfiles(StringUtils.commaDelimitedListToStringArray(
							StringUtils.trimAllWhitespace(profiles)));
				}
			}
			return this.activeProfiles;
		}
	}

	@Override
	public void setActiveProfiles(String... profiles) {
		Assert.notNull(profiles, "Profile array must not be null");
		if (logger.isDebugEnabled()) {
			logger.debug("Activating profiles " + Arrays.asList(profiles));
		}
		synchronized (this.activeProfiles) {
			this.activeProfiles.clear();
			for (String profile : profiles) {
				validateProfile(profile);
				this.activeProfiles.add(profile);
			}
		}
	}

	@Override
	public void addActiveProfile(String profile) {
		if (logger.isDebugEnabled()) {
			logger.debug("Activating profile '" + profile + "'");
		}
		validateProfile(profile);
		doGetActiveProfiles();
		synchronized (this.activeProfiles) {
			this.activeProfiles.add(profile);
		}
	}


	@Override
	public String[] getDefaultProfiles() {
		return StringUtils.toStringArray(doGetDefaultProfiles());
	}

	/**
	 * 返回通过{@link #setDefaultProfiles(String...)}显式设置的默认配置文件集，
	 * 或者如果当前的默认配置文件集仅由{@linkplain #getReservedDefaultProfiles() 保留的默认配置}文件组成，
	 * 则检查是否存在{@value #DEFAULT_PROFILES_PROPERTY_NAME}属性并赋予其值 （如果有）为默认配置文件集。
	 * @see #AbstractEnvironment()
	 * @see #getDefaultProfiles()
	 * @see #DEFAULT_PROFILES_PROPERTY_NAME
	 * @see #getReservedDefaultProfiles()
	 */
	protected Set<String> doGetDefaultProfiles() {
		synchronized (this.defaultProfiles) {
			if (this.defaultProfiles.equals(getReservedDefaultProfiles())) {
				String profiles = getProperty(DEFAULT_PROFILES_PROPERTY_NAME);
				if (StringUtils.hasText(profiles)) {
					setDefaultProfiles(StringUtils.commaDelimitedListToStringArray(
							StringUtils.trimAllWhitespace(profiles)));
				}
			}
			return this.defaultProfiles;
		}
	}

	/**
	 * 如果没有通过{@link #setActiveProfiles}显式激活其他配置文件，则指定默认情况下将其激活的配置文件集。
	 * <p>调用此方法将删除覆盖在构造环境期间可能已添加的所有保留的默认profiles文件。
	 * @see #AbstractEnvironment()
	 * @see #getReservedDefaultProfiles()
	 */
	@Override
	public void setDefaultProfiles(String... profiles) {
		Assert.notNull(profiles, "Profile array must not be null");
		synchronized (this.defaultProfiles) {
			this.defaultProfiles.clear();
			for (String profile : profiles) {
				validateProfile(profile);
				this.defaultProfiles.add(profile);
			}
		}
	}

	@Override
	@Deprecated
	public boolean acceptsProfiles(String... profiles) {
		Assert.notEmpty(profiles, "Must specify at least one profile");
		for (String profile : profiles) {
			if (StringUtils.hasLength(profile) && profile.charAt(0) == '!') {
				if (!isProfileActive(profile.substring(1))) {
					return true;
				}
			}
			else if (isProfileActive(profile)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean acceptsProfiles(Profiles profiles) {
		Assert.notNull(profiles, "Profiles must not be null");
		return profiles.matches(this::isProfileActive);
	}

	/**
	 * Return whether the given profile is active, or if active profiles are empty
	 * whether the profile should be active by default.
	 * @throws IllegalArgumentException per {@link #validateProfile(String)}
	 */
	protected boolean isProfileActive(String profile) {
		validateProfile(profile);
		Set<String> currentActiveProfiles = doGetActiveProfiles();
		return (currentActiveProfiles.contains(profile) ||
				(currentActiveProfiles.isEmpty() && doGetDefaultProfiles().contains(profile)));
	}

	/**
	 * Validate the given profile, called internally prior to adding to the set of
	 * active or default profiles.
	 * <p>Subclasses may override to impose further restrictions on profile syntax.
	 * @throws IllegalArgumentException if the profile is null, empty, whitespace-only or
	 * begins with the profile NOT operator (!).
	 * @see #acceptsProfiles
	 * @see #addActiveProfile
	 * @see #setDefaultProfiles
	 */
	protected void validateProfile(String profile) {
		if (!StringUtils.hasText(profile)) {
			throw new IllegalArgumentException("Invalid profile [" + profile + "]: must contain text");
		}
		if (profile.charAt(0) == '!') {
			throw new IllegalArgumentException("Invalid profile [" + profile + "]: must not begin with ! operator");
		}
	}

	@Override
	public MutablePropertySources getPropertySources() {
		return this.propertySources;
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public Map<String, Object> getSystemProperties() {
		try {
			return (Map) System.getProperties();
		}
		catch (AccessControlException ex) {
			return (Map) new ReadOnlySystemAttributesMap() {
				@Override
				@Nullable
				protected String getSystemAttribute(String attributeName) {
					try {
						return System.getProperty(attributeName);
					}
					catch (AccessControlException ex) {
						if (logger.isInfoEnabled()) {
							logger.info("Caught AccessControlException when accessing system property '" +
									attributeName + "'; its value will be returned [null]. Reason: " + ex.getMessage());
						}
						return null;
					}
				}
			};
		}
	}

	@Override
	@SuppressWarnings({"rawtypes", "unchecked"})
	public Map<String, Object> getSystemEnvironment() {
		if (suppressGetenvAccess()) {
			return Collections.emptyMap();
		}
		try {
			return (Map) System.getenv();
		}
		catch (AccessControlException ex) {
			return (Map) new ReadOnlySystemAttributesMap() {
				@Override
				@Nullable
				protected String getSystemAttribute(String attributeName) {
					try {
						return System.getenv(attributeName);
					}
					catch (AccessControlException ex) {
						if (logger.isInfoEnabled()) {
							logger.info("Caught AccessControlException when accessing system environment variable '" +
									attributeName + "'; its value will be returned [null]. Reason: " + ex.getMessage());
						}
						return null;
					}
				}
			};
		}
	}

	/**
	 * 确定是否出于{@link #getSystemEnvironment()}的目的抑制{@link System#getenv()}/{@link System#getenv(String)}访问。
	 * <p>如果此方法返回{@code true}，则将使用空的虚拟Map代替常规的系统环境Map，
	 * 甚至从不尝试调用{@code getenv}，因此避免了安全管理器警告（如果有）。
	 * <p>默认实现检查"spring.getenv.ignore"系统属性，如果在任何情况下其值等于"true"，则返回{@code true}。
	 * @see #IGNORE_GETENV_PROPERTY_NAME
	 * @see SpringProperties#getFlag
	 */
	protected boolean suppressGetenvAccess() {
		return SpringProperties.getFlag(IGNORE_GETENV_PROPERTY_NAME);
	}

	@Override
	public void merge(ConfigurableEnvironment parent) {
		for (PropertySource<?> ps : parent.getPropertySources()) {
			if (!this.propertySources.contains(ps.getName())) {
				this.propertySources.addLast(ps);
			}
		}
		String[] parentActiveProfiles = parent.getActiveProfiles();
		if (!ObjectUtils.isEmpty(parentActiveProfiles)) {
			synchronized (this.activeProfiles) {
				Collections.addAll(this.activeProfiles, parentActiveProfiles);
			}
		}
		String[] parentDefaultProfiles = parent.getDefaultProfiles();
		if (!ObjectUtils.isEmpty(parentDefaultProfiles)) {
			synchronized (this.defaultProfiles) {
				this.defaultProfiles.remove(RESERVED_DEFAULT_PROFILE_NAME);
				Collections.addAll(this.defaultProfiles, parentDefaultProfiles);
			}
		}
	}


	//---------------------------------------------------------------------
	// Implementation of ConfigurablePropertyResolver interface
	//---------------------------------------------------------------------

	@Override
	public ConfigurableConversionService getConversionService() {
		return this.propertyResolver.getConversionService();
	}

	@Override
	public void setConversionService(ConfigurableConversionService conversionService) {
		this.propertyResolver.setConversionService(conversionService);
	}

	@Override
	public void setPlaceholderPrefix(String placeholderPrefix) {
		this.propertyResolver.setPlaceholderPrefix(placeholderPrefix);
	}

	@Override
	public void setPlaceholderSuffix(String placeholderSuffix) {
		this.propertyResolver.setPlaceholderSuffix(placeholderSuffix);
	}

	@Override
	public void setValueSeparator(@Nullable String valueSeparator) {
		this.propertyResolver.setValueSeparator(valueSeparator);
	}

	@Override
	public void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders) {
		this.propertyResolver.setIgnoreUnresolvableNestedPlaceholders(ignoreUnresolvableNestedPlaceholders);
	}

	@Override
	public void setRequiredProperties(String... requiredProperties) {
		this.propertyResolver.setRequiredProperties(requiredProperties);
	}

	@Override
	public void validateRequiredProperties() throws MissingRequiredPropertiesException {
		this.propertyResolver.validateRequiredProperties();
	}


	//---------------------------------------------------------------------
	// Implementation of PropertyResolver interface
	//---------------------------------------------------------------------

	@Override
	public boolean containsProperty(String key) {
		return this.propertyResolver.containsProperty(key);
	}

	@Override
	@Nullable
	public String getProperty(String key) {
		return this.propertyResolver.getProperty(key);
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		return this.propertyResolver.getProperty(key, defaultValue);
	}

	@Override
	@Nullable
	public <T> T getProperty(String key, Class<T> targetType) {
		return this.propertyResolver.getProperty(key, targetType);
	}

	@Override
	public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
		return this.propertyResolver.getProperty(key, targetType, defaultValue);
	}

	@Override
	public String getRequiredProperty(String key) throws IllegalStateException {
		return this.propertyResolver.getRequiredProperty(key);
	}

	@Override
	public <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
		return this.propertyResolver.getRequiredProperty(key, targetType);
	}

	@Override
	public String resolvePlaceholders(String text) {
		return this.propertyResolver.resolvePlaceholders(text);
	}

	@Override
	public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
		return this.propertyResolver.resolveRequiredPlaceholders(text);
	}


	@Override
	public String toString() {
		return getClass().getSimpleName() + " {activeProfiles=" + this.activeProfiles +
				", defaultProfiles=" + this.defaultProfiles + ", propertySources=" + this.propertySources + "}";
	}

}
