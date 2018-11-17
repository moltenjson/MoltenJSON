/*
 * * Copyright 2018 github.com/ReflxctionDev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.reflxction.simplejson.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * A utility for simplified (and fast) writing.
 *
 * While this class is not intended for public usage, it may be freely
 * used.
 */
public class SimpleWriter {

    // The maximum buffered size
    private static final int BUFFER_SIZE = 256;

    // The byte buffer
    private ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);

    private SeekableByteChannel channel;

    public SimpleWriter(String path) throws IOException {
        this.channel = Files.newByteChannel(Paths.get(path), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    }

    public void write(String text) {
        byte[] asBytes = text.getBytes();
        int size = asBytes.length;
        if (size <= BUFFER_SIZE) {
            write(asBytes);
        } else {
            byte[] copy = new byte[256];
            int index = 0;
            while (true) {
                int nextIndex = index + 256;
                if (nextIndex >= size) {
                    if (nextIndex != size) {
                        int copyIndex = 0;
                        for (int i = index; i < size; i++) {
                            copy[copyIndex++] = asBytes[i];
                        }
                        write(copy);
                    }
                    break;
                }
                int copyIndex = 0;
                for (int i = index; i < nextIndex; i++) {
                    copy[copyIndex++] = asBytes[i];
                }
                write(copy);
            }
        }
    }

    public void newLine() {
        write(System.lineSeparator());
    }

    private void write(byte[] bytes) {
        try {
            buffer.clear();
            buffer.put(bytes);
            buffer.flip();
            channel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() throws IOException {
        channel.close();
    }

}
