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
package org.springframework.core.io.buffer;

/**
 * 异常，该异常指示从{@link DataBuffer DataBuffer}的流消耗的累积字节数超过了某些预配置的限制.
 * 当数据缓冲区被缓存和聚合时可能引起该异常，例如，{@link DataBufferUtils#join}.
 * 或者，当数据缓冲区已释放但已解析的表示形式正在聚合时（例如，与Jackson进行异步解析，每个事件进行SSE解析和聚合行.
 *
 * @author Rossen Stoyanchev
 * @since 5.1.11
 */
@SuppressWarnings("serial")
public class DataBufferLimitException extends IllegalStateException {


	public DataBufferLimitException(String message) {
		super(message);
	}

}
