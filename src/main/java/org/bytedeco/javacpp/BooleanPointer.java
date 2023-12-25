/*
 * Copyright (C) 2018-2020 Samuel Audet
 *
 * Licensed either under the Apache License, Version 2.0, or (at your option)
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation (subject to the "Classpath" exception),
 * either version 2, or any later version (collectively, the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     http://www.gnu.org/licenses/
 *     http://www.gnu.org/software/classpath/license.html
 *
 * or as provided in the LICENSE.txt file that accompanied this code.
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bytedeco.javacpp;

import java.nio.ByteBuffer;
import org.bytedeco.javacpp.tools.Logger;

/**
 * The peer class to native pointers and arrays of {@code jboolean}.
 * All operations take into account the position and limit, when appropriate.
 * <p>
 * Assumes {@code sizeof(jboolean)} is always 1 byte with {@code false == 0} and {@code true == 1}.
 *
 * @author Samuel Audet
 */
@org.bytedeco.javacpp.annotation.Properties(inherit = org.bytedeco.javacpp.presets.javacpp.class)
public class BooleanPointer extends Pointer {
    private static final Logger logger = Logger.create(BooleanPointer.class);

    static {
        try {
            Loader.load();
        } catch (Throwable t) {
            if (logger.isDebugEnabled()) {
                logger.debug("Could not load BooleanPointer: " + t);
            }
        }
    }

    /**
     * Allocates enough memory for the array and copies it.
     *
     * @param array the array to copy
     * @see #put(boolean[])
     */
    public BooleanPointer(boolean ... array) {
        this(array.length);
        put(array);
    }
    /**
     * For direct buffers, calls {@link Pointer#Pointer(Buffer)}, while for buffers
     * backed with an array, allocates enough memory for the array and copies it.
     *
     * @param buffer the Buffer to reference or copy
     * @see #put(boolean[])
     */
    public BooleanPointer(ByteBuffer buffer) {
        super(buffer);
        if (buffer != null && !buffer.isDirect() && buffer.hasArray()) {
            byte[] array = buffer.array();
            allocateArray(array.length - buffer.arrayOffset());
            for (int i = buffer.arrayOffset(); i < array.length; i++) {
                put(i - buffer.arrayOffset(), array[i] != 0);
            }
            position(buffer.position());
            limit(buffer.limit());
        }
    }
    /**
     * Allocates a native {@code boolean} array of the given size.
     *
     * @param size the number of {@code boolean} elements to allocate
     */
    public BooleanPointer(long size) {
        try {
            allocateArray(size);
            if (size > 0 && address == 0) {
                throw new OutOfMemoryError("Native allocator returned address == 0");
            }
        } catch (UnsatisfiedLinkError e) {
            throw new RuntimeException("No native JavaCPP library in memory. (Has Loader.load() been called?)", e);
        } catch (OutOfMemoryError e) {
            OutOfMemoryError e2 = new OutOfMemoryError("Cannot allocate new BooleanPointer(" + size + "): "
                    + "totalBytes = " + formatBytes(totalBytes()) + ", physicalBytes = " + formatBytes(physicalBytes()));
            e2.initCause(e);
            throw e2;
        }
    }
    /** @see Pointer#Pointer() */
    public BooleanPointer() { }
    /** @see Pointer#Pointer(Pointer) */
    public BooleanPointer(Pointer p) { super(p); }
    private native void allocateArray(long size);

    /** @see Pointer#position(long) */
    @Override public BooleanPointer position(long position) {
        return super.position(position);
    }
    /** @see Pointer#limit(long) */
    @Override public BooleanPointer limit(long limit) {
        return super.limit(limit);
    }
    /** @see Pointer#capacity(long) */
    @Override public BooleanPointer capacity(long capacity) {
        return super.capacity(capacity);
    }
    @Override public int sizeof() {
        return getClass() == BooleanPointer.class ? 1 : super.sizeof();
    }
    @Override public BooleanPointer getPointer(long i) {
        return new BooleanPointer(this).offsetAddress(i);
    }

    /** @return {@code get(0)} */
    public boolean get() { return get(0); }
    /** @return the i-th {@code boolean} value of a native array */
    public native boolean get(long i);
    /** @return {@code put(0, b)} */
    public BooleanPointer put(boolean b) { return put(0, b); }
    /**
     * Copies the {@code boolean} value to the i-th element of a native array.
     *
     * @param i the index into the array
     * @param b the {@code boolean} value to copy
     * @return this
     */
    public native BooleanPointer put(long i, boolean b);

    /** @return {@code get(array, 0, array.length)} */
    public BooleanPointer get(boolean[] array) { return get(array, 0, array.length); }
    /** @return {@code put(array, 0, array.length)} */
    public BooleanPointer put(boolean ... array) { return put(array, 0, array.length); }
    /**
     * Reads a portion of the native array into a Java array.
     *
     * @param array the array to write to
     * @param offset the offset into the array where to start writing
     * @param length the length of data to read and write
     * @return this
     */
    public native BooleanPointer get(boolean[] array, int offset, int length);
    /**
     * Writes a portion of a Java array into the native array.
     *
     * @param array the array to read from
     * @param offset the offset into the array where to start reading
     * @param length the length of data to read and write
     * @return this
     */
    public native BooleanPointer put(boolean[] array, int offset, int length);
}
