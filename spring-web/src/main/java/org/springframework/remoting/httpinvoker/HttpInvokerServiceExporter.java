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

package org.springframework.remoting.httpinvoker;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.remoting.rmi.RemoteInvocationSerializingExporter;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.util.NestedServletException;

/**
 * 基于Servlet-API的HTTP请求处理器，将指定的服务bean导出为HTTP调用程序服务端点，
 * 可通过HTTP调用程序代理访问。
 *
 * <p>反序列化远程调用对象并序列化远程调用结果对象。 与RMI一样，使用Java序列化，
 * 但是提供了与Caucho基于HTTP的Hessian协议相同的易于设置性。
 *
 * <p><b>HTTP调用程序是Java到Java远程处理的推荐协议。</b> 它比Hessian更强大和可扩展，但以与Java绑定为代价。 
 * 尽管如此，它的设置就像Hessian一样容易，这是它与RMI相比的主要优势。
 *
 * <p>警告：请注意由于不安全的Java反序列化而导致的漏洞：操纵的输入流可能会在反序列化步骤中导致服务器上不必要的代码执行。
 * 因此，不要将HTTP调用者终结点暴露给不受信任的客户端，而应该暴露给您自己的服务之间。 通常，我们强烈建议您使用其它任何消息格式（例如JSON）。
 *
 * @author Juergen Hoeller
 * @since 1.1
 * @see HttpInvokerClientInterceptor
 * @see HttpInvokerProxyFactoryBean
 * @see org.springframework.remoting.rmi.RmiServiceExporter
 * @see org.springframework.remoting.caucho.HessianServiceExporter
 */
public class HttpInvokerServiceExporter extends RemoteInvocationSerializingExporter implements HttpRequestHandler {

	/**
	 * Reads a remote invocation from the request, executes it,
	 * and writes the remote invocation result to the response.
	 * @see #readRemoteInvocation(HttpServletRequest)
	 * @see #invokeAndCreateResult(org.springframework.remoting.support.RemoteInvocation, Object)
	 * @see #writeRemoteInvocationResult(HttpServletRequest, HttpServletResponse, RemoteInvocationResult)
	 */
	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			RemoteInvocation invocation = readRemoteInvocation(request);
			RemoteInvocationResult result = invokeAndCreateResult(invocation, getProxy());
			writeRemoteInvocationResult(request, response, result);
		}
		catch (ClassNotFoundException ex) {
			throw new NestedServletException("Class not found during deserialization", ex);
		}
	}

	/**
	 * Read a RemoteInvocation from the given HTTP request.
	 * <p>Delegates to {@link #readRemoteInvocation(HttpServletRequest, InputStream)} with
	 * the {@link HttpServletRequest#getInputStream() servlet request's input stream}.
	 * @param request current HTTP request
	 * @return the RemoteInvocation object
	 * @throws IOException in case of I/O failure
	 * @throws ClassNotFoundException if thrown by deserialization
	 */
	protected RemoteInvocation readRemoteInvocation(HttpServletRequest request)
			throws IOException, ClassNotFoundException {

		return readRemoteInvocation(request, request.getInputStream());
	}

	/**
	 * Deserialize a RemoteInvocation object from the given InputStream.
	 * <p>Gives {@link #decorateInputStream} a chance to decorate the stream
	 * first (for example, for custom encryption or compression). Creates a
	 * {@link org.springframework.remoting.rmi.CodebaseAwareObjectInputStream}
	 * and calls {@link #doReadRemoteInvocation} to actually read the object.
	 * <p>Can be overridden for custom serialization of the invocation.
	 * @param request current HTTP request
	 * @param is the InputStream to read from
	 * @return the RemoteInvocation object
	 * @throws IOException in case of I/O failure
	 * @throws ClassNotFoundException if thrown during deserialization
	 */
	protected RemoteInvocation readRemoteInvocation(HttpServletRequest request, InputStream is)
			throws IOException, ClassNotFoundException {

		ObjectInputStream ois = createObjectInputStream(decorateInputStream(request, is));
		try {
			return doReadRemoteInvocation(ois);
		}
		finally {
			ois.close();
		}
	}

	/**
	 * Return the InputStream to use for reading remote invocations,
	 * potentially decorating the given original InputStream.
	 * <p>The default implementation returns the given stream as-is.
	 * Can be overridden, for example, for custom encryption or compression.
	 * @param request current HTTP request
	 * @param is the original InputStream
	 * @return the potentially decorated InputStream
	 * @throws IOException in case of I/O failure
	 */
	protected InputStream decorateInputStream(HttpServletRequest request, InputStream is) throws IOException {
		return is;
	}

	/**
	 * Write the given RemoteInvocationResult to the given HTTP response.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param result the RemoteInvocationResult object
	 * @throws IOException in case of I/O failure
	 */
	protected void writeRemoteInvocationResult(
			HttpServletRequest request, HttpServletResponse response, RemoteInvocationResult result)
			throws IOException {

		response.setContentType(getContentType());
		writeRemoteInvocationResult(request, response, result, response.getOutputStream());
	}

	/**
	 * Serialize the given RemoteInvocation to the given OutputStream.
	 * <p>The default implementation gives {@link #decorateOutputStream} a chance
	 * to decorate the stream first (for example, for custom encryption or compression).
	 * Creates an {@link java.io.ObjectOutputStream} for the final stream and calls
	 * {@link #doWriteRemoteInvocationResult} to actually write the object.
	 * <p>Can be overridden for custom serialization of the invocation.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param result the RemoteInvocationResult object
	 * @param os the OutputStream to write to
	 * @throws IOException in case of I/O failure
	 * @see #decorateOutputStream
	 * @see #doWriteRemoteInvocationResult
	 */
	protected void writeRemoteInvocationResult(
			HttpServletRequest request, HttpServletResponse response, RemoteInvocationResult result, OutputStream os)
			throws IOException {

		ObjectOutputStream oos =
				createObjectOutputStream(new FlushGuardedOutputStream(decorateOutputStream(request, response, os)));
		try {
			doWriteRemoteInvocationResult(result, oos);
		}
		finally {
			oos.close();
		}
	}

	/**
	 * Return the OutputStream to use for writing remote invocation results,
	 * potentially decorating the given original OutputStream.
	 * <p>The default implementation returns the given stream as-is.
	 * Can be overridden, for example, for custom encryption or compression.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param os the original OutputStream
	 * @return the potentially decorated OutputStream
	 * @throws IOException in case of I/O failure
	 */
	protected OutputStream decorateOutputStream(
			HttpServletRequest request, HttpServletResponse response, OutputStream os) throws IOException {

		return os;
	}


	/**
	 * Decorate an {@code OutputStream} to guard against {@code flush()} calls,
	 * which are turned into no-ops.
	 * <p>Because {@link ObjectOutputStream#close()} will in fact flush/drain
	 * the underlying stream twice, this {@link FilterOutputStream} will
	 * guard against individual flush calls. Multiple flush calls can lead
	 * to performance issues, since writes aren't gathered as they should be.
	 * @see <a href="https://jira.spring.io/browse/SPR-14040">SPR-14040</a>
	 */
	private static class FlushGuardedOutputStream extends FilterOutputStream {

		public FlushGuardedOutputStream(OutputStream out) {
			super(out);
		}

		@Override
		public void flush() throws IOException {
			// Do nothing on flush
		}
	}

}
