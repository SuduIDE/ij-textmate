package org.rri.ijTextmate;

import junit.framework.TestCase;

public class TestHelper {
    public static final Assert ASSERT_FALSE = TestCase::assertFalse;
    public static final Assert ASSERT_TRUE = TestCase::assertTrue;

    @FunctionalInterface
    public interface Assert {
        void test(boolean b);
    }
}
