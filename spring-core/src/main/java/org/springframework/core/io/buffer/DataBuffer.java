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

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.function.IntPredicate;

import org.springframework.util.Assert;

/**
 * 字节缓冲区的基本抽象.
 *
 * <p>与{@code ByteBuffer}的单个位置相反，{@code DataBuffer}具有单独的
 * {@linkplain #readPosition() read}和{@linkplain #writePosition() write}位置.
 * 这样，the {@code DataBuffer}不需要在写入后进行翻转即可读取. 通常，以下不变式适用于读取和写入位置以及容量：
 *
 * <blockquote>
 *     <tt>0</tt> <tt>&lt;=</tt>
 *     <i>readPosition</i> <tt>&lt;=</tt>
 *     <i>writePosition</i> <tt>&lt;=</tt>
 *     <i>capacity</i>
 * </blockquote>
 *
 * <p>与{@code StringBuilder}相似，可按需扩展{@code DataBuffer}的{@linkplain #capacity() capacity}.
 *
 * <p>{@code DataBuffer}抽象的主要目的是为{@link ByteBuffer}提供一个方便的包装器，
 * 类似于Netty的{@link io.netty.buffer.ByteBuf}，但也可以在非Netty平台（即Servlet容器）上使用.
 *
 * @author Arjen Poutsma
 * @author Brian Clozel
 * @since 5.0
 * @see DataBufferFactory
 */
public interface DataBuffer {

	/**
	 * 返回创建此缓冲区的{@link DataBufferFactory}.
	 * @return the creating buffer factory
	 */
	DataBufferFactory factory();

	/**
	 * 返回此缓冲区中与给定谓词匹配的第一个字节的索引.
	 * @param predicate the predicate to match
	 * @param fromIndex the index to start the search from
	 * @return the index of the first byte that matches {@code predicate};
	 * or {@code -1} if none match
	 */
	int indexOf(IntPredicate predicate, int fromIndex);

	/**
	 * 返回此缓冲区中与给定谓词匹配的最后一个字节的索引.
	 * @param predicate the predicate to match
	 * @param fromIndex the index to start the search from
	 * @return the index of the last byte that matches {@code predicate};
	 * or {@code -1} if none match
	 */
	int lastIndexOf(IntPredicate predicate, int fromIndex);

	/**
	 * 返回可以从此数据缓冲区读取的字节数.
	 * @return the readable byte count
	 */
	int readableByteCount();

	/**
	 * 返回可以写入此数据缓冲区的字节数.
	 * @return the writable byte count
	 * @since 5.0.1
	 */
	int writableByteCount();

	/**
	 * 返回此缓冲区可以包含的字节数.
	 * @return the capacity
	 * @since 5.0.1
	 */
	int capacity();

	/**
	 * 设置此缓冲区可以包含的字节数.
	 * <p>如果新容量小于当前容量，则该缓冲区的内容将被截断. 如果新容量高于当前容量，则会对其进行扩展.
	 * @param capacity the new capacity
	 * @return this buffer
	 */
	DataBuffer capacity(int capacity);

	/**
	 * 确保当前缓冲区具有足够的{@link #writableByteCount()}以写入作为参数给出的数据量.
	 * 如果没有，不足的容量将添加到缓冲区.
	 * @param capacity the writable capacity to check for
	 * @return this buffer
	 * @since 5.1.4
	 */
	default DataBuffer ensureCapacity(int capacity) {
		return this;
	}

	/**
	 * Return the position from which this buffer will read.
	 * @return the read position
	 * @since 5.0.1
	 */
	int readPosition();

	/**
	 * 返回此缓冲区将从其读取的位置.
	 * @param readPosition the new read position
	 * @return this buffer
	 * @throws IndexOutOfBoundsException if {@code readPosition} is smaller than 0
	 * or greater than {@link #writePosition()}
	 * @since 5.0.1
	 */
	DataBuffer readPosition(int readPosition);

	/**
	 * 设置从中读取该缓冲区的位置.
	 * @return the write position
	 * @since 5.0.1
	 */
	int writePosition();

	/**
	 * 返回此缓冲区将写入的位置.
	 * @param writePosition the new write position
	 * @return this buffer
	 * @throws IndexOutOfBoundsException if {@code writePosition} is smaller than
	 * {@link #readPosition()} or greater than {@link #capacity()}
	 * @since 5.0.1
	 */
	DataBuffer writePosition(int writePosition);

	/**
	 * 从该数据缓冲区读取给定索引处的单个字节.
	 * @param index the index at which the byte will be read
	 * @return the byte at the given index
	 * @throws IndexOutOfBoundsException when {@code index} is out of bounds
	 * @since 5.0.4
	 */
	byte getByte(int index);

	/**
	 * 从该数据缓冲区的当前读取位置读取一个字节.
	 * @return the byte at this buffer's current reading position
	 */
	byte read();

	/**
	 * 从该缓冲区的当前读取位置开始，将该缓冲区的数据读取到指定的目的地.
	 * @param destination the array into which the bytes are to be written
	 * @return this buffer
	 */
	DataBuffer read(byte[] destination);

	/**
	 * 从此缓冲区的当前读取位置开始，最多将此缓冲区的字节{@code length}读取到指定的目标.
	 * @param destination the array into which the bytes are to be written
	 * @param offset the index within {@code destination} of the first byte to be written
	 * @param length the maximum number of bytes to be written in {@code destination}
	 * @return this buffer
	 */
	DataBuffer read(byte[] destination, int offset, int length);

	/**
	 * 在当前写入位置将一个字节写入此缓冲区.
	 * @param b the byte to be written
	 * @return this buffer
	 */
	DataBuffer write(byte b);

	/**
	 * 从该缓冲区的当前写入位置开始，将给定的源写入该缓冲区.
	 * @param source the bytes to be written into this buffer
	 * @return this buffer
	 */
	DataBuffer write(byte[] source);

	/**
	 * 从此缓冲区的当前写入位置开始，将最大长度的给定源字节{@code length}写入此缓冲区.
	 * @param source the bytes to be written into this buffer
	 * @param offset the index within {@code source} to start writing from
	 * @param length the maximum number of bytes to be written from {@code source}
	 * @return this buffer
	 */
	DataBuffer write(byte[] source, int offset, int length);

	/**
	 * 从当前写入位置开始，将一个或多个{@code DataBuffer}写入此缓冲区.
	 * 调用者有责任{@linkplain DataBufferUtils#release(DataBuffer) release}给定的数据缓冲区.
	 * @param buffers the byte buffers to write into this buffer
	 * @return this buffer
	 */
	DataBuffer write(DataBuffer... buffers);

	/**
	 * Write one or more {@link ByteBuffer} to this buffer, starting at the current
	 * writing position.
	 * @param buffers the byte buffers to write into this buffer
	 * @return this buffer
	 */
	DataBuffer write(ByteBuffer... buffers);

	/**
	 * 从当前写入位置开始，使用给定的{@code Charset}写入给定的{@code CharSequence}.
	 * @param charSequence the char sequence to write into this buffer
	 * @param charset the charset to encode the char sequence with
	 * @return this buffer
	 * @since 5.1.4
	 */
	default DataBuffer write(CharSequence charSequence, Charset charset) {
		Assert.notNull(charSequence, "CharSequence must not be null");
		Assert.notNull(charset, "Charset must not be null");
		if (charSequence.length() != 0) {
			CharsetEncoder charsetEncoder = charset.newEncoder()
					.onMalformedInput(CodingErrorAction.REPLACE)
					.onUnmappableCharacter(CodingErrorAction.REPLACE);
			CharBuffer inBuffer = CharBuffer.wrap(charSequence);
			int estimatedSize = (int) (inBuffer.remaining() * charsetEncoder.averageBytesPerChar());
			ByteBuffer outBuffer = ensureCapacity(estimatedSize)
					.asByteBuffer(writePosition(), writableByteCount());
			while (true) {
				CoderResult cr = (inBuffer.hasRemaining() ?
						charsetEncoder.encode(inBuffer, outBuffer, true) : CoderResult.UNDERFLOW);
				if (cr.isUnderflow()) {
					cr = charsetEncoder.flush(outBuffer);
				}
				if (cr.isUnderflow()) {
					break;
				}
				if (cr.isOverflow()) {
					writePosition(writePosition() + outBuffer.position());
					int maximumSize = (int) (inBuffer.remaining() * charsetEncoder.maxBytesPerChar());
					ensureCapacity(maximumSize);
					outBuffer = asByteBuffer(writePosition(), writableByteCount());
				}
			}
			writePosition(writePosition() + outBuffer.position());
		}
		return this;
	}

	/**
	 * 创建一个新的{@code DataBuffer}，其内容是此数据缓冲区内容的共享子序列.
	 * 该数据缓冲区和返回的缓冲区之间的数据是共享的；
	 * 但是返回缓冲区位置的变化不会反映在此数据缓冲区的读取或写入位置中.
	 * <p>请注意，此方法不会在结果切片上调用{@link DataBufferUtils#retain(DataBuffer)}：不会增加引用计数.
	 * @param index the index at which to start the slice
	 * @param length the length of the slice
	 * @return the specified slice of this data buffer
	 */
	DataBuffer slice(int index, int length);

	/**
	 * 创建一个新的{@code DataBuffer}，其内容是此数据缓冲区内容的共享保留子序列.
	 * 该数据缓冲区和返回的缓冲区之间的数据是共享的；
	 * 但是返回缓冲区位置的变化不会反映在此数据缓冲区的读取或写入位置中.
	 * <p>请注意，与{@link #slice(int, int)}不同，此方法将在结果切片上调用
	 * {@link DataBufferUtils#retain(DataBuffer)}（或等效方法）.
	 * @param index the index at which to start the slice
	 * @param length the length of the slice
	 * @return the specified, retained slice of this data buffer
	 * @since 5.2
	 */
	default DataBuffer retainedSlice(int index, int length) {
		return DataBufferUtils.retain(slice(index, length));
	}

	/**
	 * 将此缓冲区的字节公开为{@link ByteBuffer}.
	 * 此{@code DataBuffer}与返回的{@code ByteBuffer}之间的数据是共享的；
	 * 但是返回缓冲区位置的变化不会反映在此数据缓冲区的读取或写入位置中.
	 * @return this data buffer as a byte buffer
	 */
	ByteBuffer asByteBuffer();

	/**
	 * 将此缓冲区字节的子序列公开为{@link ByteBuffer}.
	 * 此DataBuffer与返回的ByteBuffer之间的数据是共享的；
	 * 但是返回缓冲区位置的变化不会反映在此数据缓冲区的读取或写入位置中.
	 * @param index the index at which to start the byte buffer
	 * @param length the length of the returned byte buffer
	 * @return this data buffer as a byte buffer
	 * @since 5.0.1
	 */
	ByteBuffer asByteBuffer(int index, int length);

	/**
	 * 将此缓冲区的数据公开为{@link InputStream}.
	 * 数据和读取位置在返回的流和此数据缓冲区之间共享.
	 * {@linkplain InputStream#close() closed}输入流时，
	 * 不会{@linkplain DataBufferUtils#release(DataBuffer) released}底层缓冲区.
	 * @return this data buffer as an input stream
	 * @see #asInputStream(boolean)
	 */
	InputStream asInputStream();

	/**
	 * 将此缓冲区的数据公开为{@link InputStream}. 数据和读取位置在返回的流和此数据缓冲区之间共享.
	 * @param releaseOnClose whether the underlying buffer will be
	 * {@linkplain DataBufferUtils#release(DataBuffer) released} when the input stream is
	 * {@linkplain InputStream#close() closed}.
	 * @return this data buffer as an input stream
	 * @since 5.0.4
	 */
	InputStream asInputStream(boolean releaseOnClose);

	/**
	 * 将此缓冲区的数据公开为{@link OutputStream}. 数据和写入位置在返回的流和此数据缓冲区之间共享.
	 * @return this data buffer as an output stream
	 */
	OutputStream asOutputStream();

	/**
	 * 使用指定的字符集将此缓冲区的数据返回String.
	 * 默认实现将委托委托给{@code toString(readPosition(), readableByteCount(), charset)}.
	 * @param charset the character set to use
	 * @return a string representation of all this buffers data
	 * @since 5.2
	 */
	default String toString(Charset charset) {
		Assert.notNull(charset, "Charset must not be null");
		return toString(readPosition(), readableByteCount(), charset);
	}

	/**
	 * 使用指定的字符集以String形式返回此缓冲区数据的一部分.
	 * @param index the index at which to start the string
	 * @param length the number of bytes to use for the string
	 * @param charset the charset to use
	 * @return a string representation of a part of this buffers data
	 * @since 5.2
	 */
	String toString(int index, int length, Charset charset);

}
