/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.web.servlet.mvc.condition;

import java.util.Collection;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import org.springframework.lang.Nullable;

/**
 * {@link RequestCondition}的持有者，在事先不知道请求条件的类型时有用，
 * 例如 自定义条件。 由于此类也是{@code RequestCondition}的实现，
 * 因此它有效地装饰了所保留的请求条件，并允许以类型和null安全的方式将其组合并与其它请求条件进行比较。
 *
 * <p>当两个{@code RequestConditionHolder}实例组合或相互比较时，
 * 期望它们所持有的条件是相同的类型。 如果不是，则引发{@link ClassCastException}。
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 */
public final class RequestConditionHolder extends AbstractRequestCondition<RequestConditionHolder> {

	@Nullable
	private final RequestCondition<Object> condition;


	/**
	 * Create a new holder to wrap the given request condition.
	 * @param requestCondition the condition to hold, may be {@code null}
	 */
	@SuppressWarnings("unchecked")
	public RequestConditionHolder(@Nullable RequestCondition<?> requestCondition) {
		this.condition = (RequestCondition<Object>) requestCondition;
	}


	/**
	 * Return the held request condition, or {@code null} if not holding one.
	 */
	@Nullable
	public RequestCondition<?> getCondition() {
		return this.condition;
	}

	@Override
	protected Collection<?> getContent() {
		return (this.condition != null ? Collections.singleton(this.condition) : Collections.emptyList());
	}

	@Override
	protected String getToStringInfix() {
		return " ";
	}

	/**
	 * Combine the request conditions held by the two RequestConditionHolder
	 * instances after making sure the conditions are of the same type.
	 * Or if one holder is empty, the other holder is returned.
	 */
	@Override
	public RequestConditionHolder combine(RequestConditionHolder other) {
		if (this.condition == null && other.condition == null) {
			return this;
		}
		else if (this.condition == null) {
			return other;
		}
		else if (other.condition == null) {
			return this;
		}
		else {
			assertEqualConditionTypes(this.condition, other.condition);
			RequestCondition<?> combined = (RequestCondition<?>) this.condition.combine(other.condition);
			return new RequestConditionHolder(combined);
		}
	}

	/**
	 * Ensure the held request conditions are of the same type.
	 */
	private void assertEqualConditionTypes(RequestCondition<?> thisCondition, RequestCondition<?> otherCondition) {
		Class<?> clazz = thisCondition.getClass();
		Class<?> otherClazz = otherCondition.getClass();
		if (!clazz.equals(otherClazz)) {
			throw new ClassCastException("Incompatible request conditions: " + clazz + " and " + otherClazz);
		}
	}

	/**
	 * Get the matching condition for the held request condition wrap it in a
	 * new RequestConditionHolder instance. Or otherwise if this is an empty
	 * holder, return the same holder instance.
	 */
	@Override
	@Nullable
	public RequestConditionHolder getMatchingCondition(HttpServletRequest request) {
		if (this.condition == null) {
			return this;
		}
		RequestCondition<?> match = (RequestCondition<?>) this.condition.getMatchingCondition(request);
		return (match != null ? new RequestConditionHolder(match) : null);
	}

	/**
	 * Compare the request conditions held by the two RequestConditionHolder
	 * instances after making sure the conditions are of the same type.
	 * Or if one holder is empty, the other holder is preferred.
	 */
	@Override
	public int compareTo(RequestConditionHolder other, HttpServletRequest request) {
		if (this.condition == null && other.condition == null) {
			return 0;
		}
		else if (this.condition == null) {
			return 1;
		}
		else if (other.condition == null) {
			return -1;
		}
		else {
			assertEqualConditionTypes(this.condition, other.condition);
			return this.condition.compareTo(other.condition, request);
		}
	}

}
