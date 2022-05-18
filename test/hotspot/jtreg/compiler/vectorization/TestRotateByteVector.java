/*
 * Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2022, Loongson Technology. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/**
 * @test
 * @bug 8286847
 * @summary Test vectorization of rotate byte
 * @library /test/lib
 * @run main/othervm -XX:-TieredCompilation -XX:CompileCommand=compileonly,TestRotateByteVector::testRotate* -Xbatch TestRotateByteVector
 */

import java.util.Random;
import jdk.test.lib.Utils;

public class TestRotateByteVector {
    private static final Random random = Utils.getRandomInstance();
    private static final int ARRLEN = 512;
    private static final int ITERS = 11000;
    private static byte[] arr = new byte[ARRLEN];
    private static byte[] rol = new byte[ARRLEN];
    private static byte[] ror = new byte[ARRLEN];
    private static byte res = 0;

    public static void main(String[] args) {
        System.out.println("warmup");
        warmup();

        System.out.println("Testing rotate byte...");
        runRotateLeftTest();
        runRotateRightTest();

        System.out.println("PASSED");
    }

    static void warmup() {
        random.nextBytes(arr);
        for (int i = 0; i < ITERS; i++) {
            testRotateLeft(rol, arr, i);
            testRotateRight(ror, arr, i);
        }
    }

    static void runRotateLeftTest() {
        for (int shift = 0; shift < 64; shift++) {
            random.nextBytes(arr);
            testRotateLeft(rol, arr, shift);
            for (int i = 0; i < ARRLEN; i++) {
                res = (byte) ((arr[i] << shift) | (arr[i] >>> -shift));
                if (rol[i] != res) {
                    throw new RuntimeException("rol value = " + arr[i] + ", shift = " + shift + ", error: " + "expect " + res + " but result is " + rol[i]);
                }
            }
        }
    }

    static void runRotateRightTest() {
        for (int shift = 0; shift < 64; shift++) {
            random.nextBytes(arr);
            testRotateRight(ror, arr, shift);
            for (int i = 0; i < ARRLEN; i++) {
                res = (byte) ((arr[i] >>> shift) | (arr[i] << -shift));
                if (ror[i] != res) {
                    throw new RuntimeException("ror value = " + arr[i] + ", shift = " + shift + ", error: " + "expect " + res + " but result is " + ror[i]);
                }
            }
        }
    }

    static void testRotateLeft(byte[] test, byte[] arr, int shift) {
        for (int i = 0; i < ARRLEN; i++) {
            test[i] = (byte) ((arr[i] << shift) | (arr[i] >>> -shift));
        }
    }

    static void testRotateRight(byte[] test, byte[] arr, int shift) {
        for (int i = 0; i < ARRLEN; i++) {
            test[i] = (byte) ((arr[i] >>> shift) | (arr[i] << -shift));
        }
    }
}
