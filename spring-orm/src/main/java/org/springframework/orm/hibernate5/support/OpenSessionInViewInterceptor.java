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

package org.springframework.orm.hibernate5.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.lang.Nullable;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.context.request.AsyncWebRequestInterceptor;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;

/**
 * Spring Web请求拦截器，它将Hibernate {@code Session}绑定到线程，以完成请求的整个处理。
 *
 * <p>该类是"Open Session in View"模式的具体表达，该模式允许尽管原始事务已经完成，
 * 但也可以延迟加载Web视图中的关联。
 *
 * <p>该拦截器通过当前线程使Hibernate Sessions可用，它将由事务管理器自动检测到。 
 * 它适用于通过{@link org.springframework.orm.hibernate5.HibernateTransactionManager}
 * 进行的服务层事务以及非事务执行（如果配置正确）。
 *
 * <p>与{@link OpenSessionInViewFilter}相比，此拦截器是在Spring应用程序上下文中配置的，
 * 因此可以利用Bean自动连接。
 *
 * <p>警告：通过使用单个休Hibernate {@code Session}处理整个请求，将此拦截器应用于现有逻辑可能会导致以前未出现的问题。
 * 特别是，持久对象与Hibernate {@code Session}的重新关联必须在请求处理的最开始就进行，以避免与已经加载的相同对象实例发生冲突。
 *
 * @author Juergen Hoeller
 * @since 4.2
 * @see OpenSessionInViewFilter
 * @see OpenSessionInterceptor
 * @see org.springframework.orm.hibernate5.HibernateTransactionManager
 * @see TransactionSynchronizationManager
 * @see SessionFactory#getCurrentSession()
 */
public class OpenSessionInViewInterceptor implements AsyncWebRequestInterceptor {

	/**
	 * Suffix that gets appended to the {@code SessionFactory}
	 * {@code toString()} representation for the "participate in existing
	 * session handling" request attribute.
	 * @see #getParticipateAttributeName
	 */
	public static final String PARTICIPATE_SUFFIX = ".PARTICIPATE";

	protected final Log logger = LogFactory.getLog(getClass());

	@Nullable
	private SessionFactory sessionFactory;


	/**
	 * Set the Hibernate SessionFactory that should be used to create Hibernate Sessions.
	 */
	public void setSessionFactory(@Nullable SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Return the Hibernate SessionFactory that should be used to create Hibernate Sessions.
	 */
	@Nullable
	public SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}

	private SessionFactory obtainSessionFactory() {
		SessionFactory sf = getSessionFactory();
		Assert.state(sf != null, "No SessionFactory set");
		return sf;
	}


	/**
	 * Open a new Hibernate {@code Session} according and bind it to the thread via the
	 * {@link TransactionSynchronizationManager}.
	 */
	@Override
	public void preHandle(WebRequest request) throws DataAccessException {
		String key = getParticipateAttributeName();
		WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
		if (asyncManager.hasConcurrentResult() && applySessionBindingInterceptor(asyncManager, key)) {
			return;
		}

		if (TransactionSynchronizationManager.hasResource(obtainSessionFactory())) {
			// Do not modify the Session: just mark the request accordingly.
			Integer count = (Integer) request.getAttribute(key, WebRequest.SCOPE_REQUEST);
			int newCount = (count != null ? count + 1 : 1);
			request.setAttribute(getParticipateAttributeName(), newCount, WebRequest.SCOPE_REQUEST);
		}
		else {
			logger.debug("Opening Hibernate Session in OpenSessionInViewInterceptor");
			Session session = openSession();
			SessionHolder sessionHolder = new SessionHolder(session);
			TransactionSynchronizationManager.bindResource(obtainSessionFactory(), sessionHolder);

			AsyncRequestInterceptor asyncRequestInterceptor =
					new AsyncRequestInterceptor(obtainSessionFactory(), sessionHolder);
			asyncManager.registerCallableInterceptor(key, asyncRequestInterceptor);
			asyncManager.registerDeferredResultInterceptor(key, asyncRequestInterceptor);
		}
	}

	@Override
	public void postHandle(WebRequest request, @Nullable ModelMap model) {
	}

	/**
	 * Unbind the Hibernate {@code Session} from the thread and close it).
	 * @see TransactionSynchronizationManager
	 */
	@Override
	public void afterCompletion(WebRequest request, @Nullable Exception ex) throws DataAccessException {
		if (!decrementParticipateCount(request)) {
			SessionHolder sessionHolder =
					(SessionHolder) TransactionSynchronizationManager.unbindResource(obtainSessionFactory());
			logger.debug("Closing Hibernate Session in OpenSessionInViewInterceptor");
			SessionFactoryUtils.closeSession(sessionHolder.getSession());
		}
	}

	private boolean decrementParticipateCount(WebRequest request) {
		String participateAttributeName = getParticipateAttributeName();
		Integer count = (Integer) request.getAttribute(participateAttributeName, WebRequest.SCOPE_REQUEST);
		if (count == null) {
			return false;
		}
		// Do not modify the Session: just clear the marker.
		if (count > 1) {
			request.setAttribute(participateAttributeName, count - 1, WebRequest.SCOPE_REQUEST);
		}
		else {
			request.removeAttribute(participateAttributeName, WebRequest.SCOPE_REQUEST);
		}
		return true;
	}

	@Override
	public void afterConcurrentHandlingStarted(WebRequest request) {
		if (!decrementParticipateCount(request)) {
			TransactionSynchronizationManager.unbindResource(obtainSessionFactory());
		}
	}

	/**
	 * Open a Session for the SessionFactory that this interceptor uses.
	 * <p>The default implementation delegates to the {@link SessionFactory#openSession}
	 * method and sets the {@link Session}'s flush mode to "MANUAL".
	 * @return the Session to use
	 * @throws DataAccessResourceFailureException if the Session could not be created
	 * @see FlushMode#MANUAL
	 */
	@SuppressWarnings("deprecation")
	protected Session openSession() throws DataAccessResourceFailureException {
		try {
			Session session = obtainSessionFactory().openSession();
			session.setFlushMode(FlushMode.MANUAL);
			return session;
		}
		catch (HibernateException ex) {
			throw new DataAccessResourceFailureException("Could not open Hibernate Session", ex);
		}
	}

	/**
	 * Return the name of the request attribute that identifies that a request is
	 * already intercepted.
	 * <p>The default implementation takes the {@code toString()} representation
	 * of the {@code SessionFactory} instance and appends {@link #PARTICIPATE_SUFFIX}.
	 */
	protected String getParticipateAttributeName() {
		return obtainSessionFactory().toString() + PARTICIPATE_SUFFIX;
	}

	private boolean applySessionBindingInterceptor(WebAsyncManager asyncManager, String key) {
		CallableProcessingInterceptor cpi = asyncManager.getCallableInterceptor(key);
		if (cpi == null) {
			return false;
		}
		((AsyncRequestInterceptor) cpi).bindSession();
		return true;
	}

}
