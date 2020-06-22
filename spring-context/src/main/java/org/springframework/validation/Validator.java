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

package org.springframework.validation;

/**
 * 应用程序中指定对象的验证器.
 *
 * <p>此接口与任何基础结构或上下文完全脱离；也就是说，它不仅验证Web层，还能验证数据访问层或任何层中的对象.
 * 因此，它可以在应用程序的任何层中使用，并支持将验证逻辑本身封装为一等公民.
 *
 * <p>下面是一个简单但完整的{@code Validator}实现，
 * 该实现验证{@code UserLogin}实例的各种{@link String}属性不为空（即它们不为{@code null}且不完全由空格组成），
 * 并且存在的任何密码至少为{@code 'MINIMUM_PASSWORD_LENGTH'}长度的字符串.
 *
 * <pre class="code"> public class UserLoginValidator implements Validator {
 *
 *    private static final int MINIMUM_PASSWORD_LENGTH = 6;
 *
 *    public boolean supports(Class clazz) {
 *       return UserLogin.class.isAssignableFrom(clazz);
 *    }
 *
 *    public void validate(Object target, Errors errors) {
 *       ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "field.required");
 *       ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "field.required");
 *       UserLogin login = (UserLogin) target;
 *       if (login.getPassword() != null
 *             && login.getPassword().trim().length() < MINIMUM_PASSWORD_LENGTH) {
 *          errors.rejectValue("password", "field.min.length",
 *                new Object[]{Integer.valueOf(MINIMUM_PASSWORD_LENGTH)},
 *                "The password must be at least [" + MINIMUM_PASSWORD_LENGTH + "] characters in length.");
 *       }
 *    }
 * }</pre>
 *
 * <p>See also the Spring reference manual for a fuller discussion of
 * the {@code Validator} interface and its role in an enterprise
 * application.
 *
 * @author Rod Johnson
 * @see SmartValidator
 * @see Errors
 * @see ValidationUtils
 */
public interface Validator {

	/**
	 * 该{@link Validator}可以验证的{@code clazz}的实例.
	 * <p>This method is <i>typically</i> implemented like so:
	 * <pre class="code">return Foo.class.isAssignableFrom(clazz);</pre>
	 * (Where {@code Foo} is the class (or superclass) of the actual
	 * object instance that is to be {@link #validate(Object, Errors) validated}.)
	 * @param clazz the {@link Class} that this {@link Validator} is
	 * being asked if it can {@link #validate(Object, Errors) validate}
	 * @return {@code true} if this {@link Validator} can indeed
	 * {@link #validate(Object, Errors) validate} instances of the
	 * supplied {@code clazz}
	 */
	boolean supports(Class<?> clazz);

	/**
	 * 验证提供的{@code target}对象，该对象必须是{@link #supports(Class)}方法返回{@code true}的{@link Class}.
	 * <p>The supplied {@link Errors errors} instance can be used to report
	 * any resulting validation errors.
	 * @param target the object that is to be validated
	 * @param errors contextual state about the validation process
	 * @see ValidationUtils
	 */
	void validate(Object target, Errors errors);

}
