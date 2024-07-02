/*
 * Copyright 2015 Goldman Sachs.
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

package com.gs.collections.impl.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.Callable;

import com.gs.collections.api.InternalIterable;
import com.gs.collections.api.PrimitiveIterable;
import com.gs.collections.api.bag.Bag;
import com.gs.collections.api.bag.sorted.SortedBag;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.block.procedure.primitive.ObjectIntProcedure;
import com.gs.collections.api.collection.ImmutableCollection;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.ImmutableMapIterable;
import com.gs.collections.api.map.MapIterable;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.map.MutableMapIterable;
import com.gs.collections.api.map.sorted.SortedMapIterable;
import com.gs.collections.api.multimap.Multimap;
import com.gs.collections.api.set.ImmutableSet;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.factory.Sets;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.tuple.ImmutableEntry;
import com.gs.collections.impl.utility.ArrayIterate;
import com.gs.collections.impl.utility.Iterate;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * An extension of the {@link Assert} class, which adds useful additional "assert" methods.
 * You can import this class instead of Assert, and use it thus, e.g.:
 * <pre>
 *     Verify.assertEquals("fred", name);  // from original Assert class
 *     Verify.assertContains("fred", nameList);  // from new extensions
 *     Verify.assertBefore("fred", "jim", orderedNamesList);  // from new extensions
 * </pre>
 */
public final class Verify extends Assert
{
    private static final int MAX_DIFFERENCES = 5;
    private static final byte[] LINE_SEPARATOR = {'\n'};

    private Verify()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    /**
     * Mangles the stack trace of {@link AssertionError} so that it looks like its been thrown from the line that
     * called to a custom assertion.
     * <p>
     * This method behaves identically to {@link #throwMangledException(AssertionError, int)} and is provided
     * for convenience for assert methods that only want to pop two stack frames. The only time that you would want to
     * call the other {@link #throwMangledException(AssertionError, int)} method is if you have a custom assert
     * that calls another custom assert i.e. the source line calling the custom asserts is more than two stack frames
     * away
     *
     * @param e The exception to mangle.
     * @see #throwMangledException(AssertionError, int)
     */
    public static void throwMangledException(AssertionError e)
    {
        /*
         * Note that we actually remove 3 frames from the stack trace because
         * we wrap the real method doing the work: e.fillInStackTrace() will
         * include us in the exceptions stack frame.
         */
        Verify.throwMangledException(e, 3);
    }

    /**
     * Mangles the stack trace of {@link AssertionError} so that it looks like
     * its been thrown from the line that called to a custom assertion.
     * <p>
     * This is useful for when you are in a debugging session and you want to go to the source
     * of the problem in the test case quickly. The regular use case for this would be something
     * along the lines of:
     * <pre>
     * public class TestFoo extends junit.framework.TestCase
     * {
     *   public void testFoo() throws Exception
     *   {
     *     Foo foo = new Foo();
     *     ...
     *     assertFoo(foo);
     *   }
     *
     *   // Custom assert
     *   private static void assertFoo(Foo foo)
     *   {
     *     try
     *     {
     *       assertEquals(...);
     *       ...
     *       assertSame(...);
     *     }
     *     catch (AssertionFailedException e)
     *     {
     *       AssertUtils.throwMangledException(e, 2);
     *     }
     *   }
     * }
     * </pre>
     * <p>
     * Without the {@code try ... catch} block around lines 11-13 the stack trace following a test failure
     * would look a little like:
     * <p>
     * <pre>
     * java.lang.AssertionError: ...
     *  at TestFoo.assertFoo(TestFoo.java:11)
     *  at TestFoo.testFoo(TestFoo.java:5)
     *  at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
     *  at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
     *  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
     *  at java.lang.reflect.Method.invoke(Method.java:324)
     *  ...
     * </pre>
     * <p>
     * Note that the source of the error isn't readily apparent as the first line in the stack trace
     * is the code within the custom assert. If we were debugging the failure we would be more interested
     * in the second line of the stack trace which shows us where in our tests the assert failed.
     * <p>
     * With the {@code try ... catch} block around lines 11-13 the stack trace would look like the
     * following:
     * <p>
     * <pre>
     * java.lang.AssertionError: ...
     *  at TestFoo.testFoo(TestFoo.java:5)
     *  at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
     *  at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
     *  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
     *  at java.lang.reflect.Method.invoke(Method.java:324)
     *  ...
     * </pre>
     * <p>
     * Here the source of the error is more visible as we can instantly see that the testFoo test is
     * failing at line 5.
     *
     * @param e           The exception to mangle.
     * @param framesToPop The number of frames to remove from the stack trace.
     * @throws AssertionError that was given as an argument with its stack trace mangled.
     */
    public static void throwMangledException(AssertionError e, int framesToPop)
    {
        e.fillInStackTrace();
        StackTraceElement[] stackTrace = e.getStackTrace();
        StackTraceElement[] newStackTrace = new StackTraceElement[stackTrace.length - framesToPop];
        System.arraycopy(stackTrace, framesToPop, newStackTrace, 0, newStackTrace.length);
        e.setStackTrace(newStackTrace);
        throw e;
    }

    public static void fail(String message, Throwable cause)
    {
        AssertionError failedException = new AssertionError(message);
        failedException.initCause(cause);
        Verify.throwMangledException(failedException);
    }

    /**
     * Assert that two items are not the same. If one item is null, the the other must be non-null.
     *
     * @deprecated in 3.0. Use {@link Assertions#assertNotEquals(Object, Object, String)} in JUnit 4.11 instead.
     */
    @Deprecated
    public static void assertNotEquals(String itemsName, Object item1, Object item2)
    {
        try
        {
            if (Comparators.nullSafeEquals(item1, item2) || Comparators.nullSafeEquals(item2, item1))
            {
                fail(itemsName + " should not be equal, item1:<" + item1 + ">, item2:<" + item2 + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that two items are not the same. If one item is null, the the other must be non-null.
     *
     * @deprecated in 3.0. Use {@link Assertions#assertNotEquals(Object, Object)} in JUnit 4.11 instead.
     */
    @Deprecated
    public static void assertNotEquals(Object item1, Object item2)
    {
        try
        {
            Verify.assertNotEquals(item1, item2, "items");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Asserts that two Strings are not equal.
     *
     * @deprecated in 3.0. Use {@link Assertions#assertNotEquals(Object, Object, String)} in JUnit 4.11 instead.
     */
    @Deprecated
    public static void assertNotEquals(String itemName, String notExpected, String actual)
    {
        try
        {
            if (Comparators.nullSafeEquals(notExpected, actual))
            {
                fail(itemName + " should not equal:<" + notExpected + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Asserts that two Strings are not equal.
     *
     * @deprecated in 3.0. Use {@link Assertions#assertNotEquals(Object, Object)} in JUnit 4.11 instead.
     */
    @Deprecated
    public static void assertNotEquals(String notExpected, String actual)
    {
        try
        {
            Verify.assertNotEquals(actual, "string", notExpected);
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Asserts that two doubles are not equal concerning a delta. If the expected value is infinity then the delta value
     * is ignored.
     *
     * @deprecated in 3.0. Use {@link Assertions#assertNotEquals(double, double, double, String)} in JUnit 4.11 instead.
     */
    @Deprecated
    public static void assertNotEquals(String itemName, double notExpected, double actual, double delta)
    {
        // handle infinity specially since subtracting to infinite values gives NaN and the
        // the following test fails
        try
        {
            //noinspection FloatingPointEquality
            if (Double.isInfinite(notExpected) && notExpected == actual || Math.abs(notExpected - actual) <= delta)
            {
                fail(itemName + " should not be equal:<" + notExpected + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Asserts that two doubles are not equal concerning a delta. If the expected value is infinity then the delta value
     * is ignored.
     *
     * @deprecated in 3.0. Use {@link Assertions#assertNotEquals(double, double, double)} in JUnit 4.11 instead.
     */
    @Deprecated
    public static void assertNotEquals(double notExpected, double actual, double delta)
    {
        try
        {
            Verify.assertNotEquals(notExpected, actual, delta, "double");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Asserts that two floats are not equal concerning a delta. If the expected value is infinity then the delta value
     * is ignored.
     */
    public static void assertNotEquals(String itemName, float notExpected, float actual, float delta)
    {
        try
        {
            // handle infinity specially since subtracting to infinite values gives NaN and the
            // the following test fails
            //noinspection FloatingPointEquality
            if (Float.isInfinite(notExpected) && notExpected == actual || Math.abs(notExpected - actual) <= delta)
            {
                fail(itemName + " should not be equal:<" + notExpected + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Asserts that two floats are not equal concerning a delta. If the expected value is infinity then the delta value
     * is ignored.
     */
    public static void assertNotEquals(float expected, float actual, float delta)
    {
        try
        {
            Verify.assertNotEquals(expected, actual, delta, "float");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Asserts that two longs are not equal.
     *
     * @deprecated in 3.0. Use {@link Assertions#assertNotEquals(long, long, String)} in JUnit 4.11 instead.
     */
    @Deprecated
    public static void assertNotEquals(String itemName, long notExpected, long actual)
    {
        try
        {
            if (notExpected == actual)
            {
                fail(itemName + " should not be equal:<" + notExpected + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Asserts that two longs are not equal.
     *
     * @deprecated in 3.0. Use {@link Assertions#assertNotEquals(long, long)} in JUnit 4.11 instead.
     */
    @Deprecated
    public static void assertNotEquals(long notExpected, long actual)
    {
        try
        {
            Verify.assertNotEquals(notExpected, actual, "long");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Asserts that two booleans are not equal.
     */
    public static void assertNotEquals(String itemName, boolean notExpected, boolean actual)
    {
        try
        {
            if (notExpected == actual)
            {
                fail(itemName + " should not be equal:<" + notExpected + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Asserts that two booleans are not equal.
     */
    public static void assertNotEquals(boolean notExpected, boolean actual)
    {
        try
        {
            Verify.assertNotEquals(notExpected, actual, "boolean");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Asserts that two bytes are not equal.
     */
    public static void assertNotEquals(String itemName, byte notExpected, byte actual)
    {
        try
        {
            if (notExpected == actual)
            {
                fail(itemName + " should not be equal:<" + notExpected + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Asserts that two bytes are not equal.
     */
    public static void assertNotEquals(byte notExpected, byte actual)
    {
        try
        {
            Verify.assertNotEquals(notExpected, actual, "byte");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Asserts that two chars are not equal.
     */
    public static void assertNotEquals(String itemName, char notExpected, char actual)
    {
        try
        {
            if (notExpected == actual)
            {
                fail(itemName + " should not be equal:<" + notExpected + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Asserts that two chars are not equal.
     */
    public static void assertNotEquals(char notExpected, char actual)
    {
        try
        {
            Verify.assertNotEquals(notExpected, actual, "char");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Asserts that two shorts are not equal.
     */
    public static void assertNotEquals(String itemName, short notExpected, short actual)
    {
        try
        {
            if (notExpected == actual)
            {
                fail(itemName + " should not be equal:<" + notExpected + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Asserts that two shorts are not equal.
     */
    public static void assertNotEquals(short notExpected, short actual)
    {
        try
        {
            Verify.assertNotEquals(notExpected, actual, "short");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Asserts that two ints are not equal.
     *
     * @deprecated in 3.0. Use {@link Assertions#assertNotEquals(long, long, String)} in JUnit 4.11 instead.
     */
    @Deprecated
    public static void assertNotEquals(String itemName, int notExpected, int actual)
    {
        try
        {
            if (notExpected == actual)
            {
                fail(itemName + " should not be equal:<" + notExpected + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Asserts that two ints are not equal.
     *
     * @deprecated in 3.0. Use {@link Assertions#assertNotEquals(long, long)} in JUnit 4.11 instead.
     */
    @Deprecated
    public static void assertNotEquals(int notExpected, int actual)
    {
        try
        {
            Verify.assertNotEquals(notExpected, actual, "int");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Iterable} is empty.
     */
    public static void assertEmpty(Iterable<?> actualIterable)
    {
        try
        {
            Verify.assertEmpty(actualIterable, "iterable");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Collection} is empty.
     */
    public static void assertEmpty(String iterableName, Iterable<?> actualIterable)
    {
        try
        {
            Verify.assertObjectNotNull(actualIterable, iterableName);

            if (Iterate.notEmpty(actualIterable))
            {
                fail(iterableName + " should be empty; actual size:<" + Iterate.sizeOf(actualIterable) + '>');
            }
            if (!Iterate.isEmpty(actualIterable))
            {
                fail(iterableName + " should be empty; actual size:<" + Iterate.sizeOf(actualIterable) + '>');
            }
            if (Iterate.sizeOf(actualIterable) != 0)
            {
                fail(iterableName + " should be empty; actual size:<" + Iterate.sizeOf(actualIterable) + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link MutableMapIterable} is empty.
     */
    public static void assertEmpty(MutableMapIterable<?, ?> actualMutableMapIterable)
    {
        try
        {
            Verify.assertEmpty(actualMutableMapIterable, "mutableMapIterable");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Collection} is empty.
     */
    public static void assertEmpty(String mutableMapIterableName, MutableMapIterable<?, ?> actualMutableMapIterable)
    {
        try
        {
            Verify.assertObjectNotNull(actualMutableMapIterable, mutableMapIterableName);

            if (Iterate.notEmpty(actualMutableMapIterable))
            {
                fail(mutableMapIterableName + " should be empty; actual size:<" + Iterate.sizeOf(actualMutableMapIterable) + '>');
            }
            if (!Iterate.isEmpty(actualMutableMapIterable))
            {
                fail(mutableMapIterableName + " should be empty; actual size:<" + Iterate.sizeOf(actualMutableMapIterable) + '>');
            }
            if (!actualMutableMapIterable.isEmpty())
            {
                fail(mutableMapIterableName + " should be empty; actual size:<" + Iterate.sizeOf(actualMutableMapIterable) + '>');
            }
            if (actualMutableMapIterable.notEmpty())
            {
                fail(mutableMapIterableName + " should be empty; actual size:<" + Iterate.sizeOf(actualMutableMapIterable) + '>');
            }
            if (actualMutableMapIterable.size() != 0)
            {
                fail(mutableMapIterableName + " should be empty; actual size:<" + actualMutableMapIterable.size() + '>');
            }
            if (actualMutableMapIterable.keySet().size() != 0)
            {
                fail(mutableMapIterableName + " should be empty; actual size:<" + actualMutableMapIterable.keySet().size() + '>');
            }
            if (actualMutableMapIterable.values().size() != 0)
            {
                fail(mutableMapIterableName + " should be empty; actual size:<" + actualMutableMapIterable.values().size() + '>');
            }
            if (actualMutableMapIterable.entrySet().size() != 0)
            {
                fail(mutableMapIterableName + " should be empty; actual size:<" + actualMutableMapIterable.entrySet().size() + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link PrimitiveIterable} is empty.
     */
    public static void assertEmpty(PrimitiveIterable primitiveIterable)
    {
        try
        {
            Verify.assertEmpty(primitiveIterable, "primitiveIterable");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link PrimitiveIterable} is empty.
     */
    public static void assertEmpty(String iterableName, PrimitiveIterable primitiveIterable)
    {
        try
        {
            Verify.assertObjectNotNull(primitiveIterable, iterableName);

            if (primitiveIterable.notEmpty())
            {
                fail(iterableName + " should be empty; actual size:<" + primitiveIterable.size() + '>');
            }
            if (!primitiveIterable.isEmpty())
            {
                fail(iterableName + " should be empty; actual size:<" + primitiveIterable.size() + '>');
            }
            if (primitiveIterable.size() != 0)
            {
                fail(iterableName + " should be empty; actual size:<" + primitiveIterable.size() + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Iterable} is empty.
     */
    public static void assertIterableEmpty(Iterable<?> iterable)
    {
        try
        {
            Verify.assertIterableEmpty(iterable, "iterable");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Iterable} is empty.
     */
    public static void assertIterableEmpty(String iterableName, Iterable<?> iterable)
    {
        try
        {
            Verify.assertObjectNotNull(iterable, iterableName);

            if (Iterate.notEmpty(iterable))
            {
                fail(iterableName + " should be empty; actual size:<" + Iterate.sizeOf(iterable) + '>');
            }
            if (!Iterate.isEmpty(iterable))
            {
                fail(iterableName + " should be empty; actual size:<" + Iterate.sizeOf(iterable) + '>');
            }
            if (Iterate.sizeOf(iterable) != 0)
            {
                fail(iterableName + " should be empty; actual size:<" + Iterate.sizeOf(iterable) + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given object is an instanceof expectedClassType.
     */
    public static void assertInstanceOf(Class<?> expectedClassType, Object actualObject)
    {
        try
        {
            Verify.assertInstanceOf(expectedClassType, actualObject, actualObject.getClass().getName());
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given object is an instanceof expectedClassType.
     */
    public static void assertInstanceOf(String objectName, Class<?> expectedClassType, Object actualObject)
    {
        try
        {
            if (!expectedClassType.isInstance(actualObject))
            {
                fail(objectName + " is not an instance of " + expectedClassType.getName());
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Map} is empty.
     */
    public static void assertEmpty(Map<?, ?> actualMap)
    {
        try
        {
            Verify.assertEmpty(actualMap, "map");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Multimap} is empty.
     */
    public static void assertEmpty(Multimap<?, ?> actualMultimap)
    {
        try
        {
            Verify.assertEmpty(actualMultimap, "multimap");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Multimap} is empty.
     */
    public static void assertEmpty(String multimapName, Multimap<?, ?> actualMultimap)
    {
        try
        {
            Verify.assertObjectNotNull(actualMultimap, multimapName);

            if (actualMultimap.notEmpty())
            {
                fail(multimapName + " should be empty; actual size:<" + actualMultimap.size() + '>');
            }
            if (!actualMultimap.isEmpty())
            {
                fail(multimapName + " should be empty; actual size:<" + actualMultimap.size() + '>');
            }
            if (actualMultimap.size() != 0)
            {
                fail(multimapName + " should be empty; actual size:<" + actualMultimap.size() + '>');
            }
            if (actualMultimap.sizeDistinct() != 0)
            {
                fail(multimapName + " should be empty; actual size:<" + actualMultimap.size() + '>');
            }
            if (actualMultimap.keyBag().size() != 0)
            {
                fail(multimapName + " should be empty; actual size:<" + actualMultimap.keyBag().size() + '>');
            }
            if (actualMultimap.keysView().size() != 0)
            {
                fail(multimapName + " should be empty; actual size:<" + actualMultimap.keysView().size() + '>');
            }
            if (actualMultimap.valuesView().size() != 0)
            {
                fail(multimapName + " should be empty; actual size:<" + actualMultimap.valuesView().size() + '>');
            }
            if (actualMultimap.keyValuePairsView().size() != 0)
            {
                fail(multimapName + " should be empty; actual size:<" + actualMultimap.keyValuePairsView().size() + '>');
            }
            if (actualMultimap.keyMultiValuePairsView().size() != 0)
            {
                fail(multimapName + " should be empty; actual size:<" + actualMultimap.keyMultiValuePairsView().size() + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Map} is empty.
     */
    public static void assertEmpty(String mapName, Map<?, ?> actualMap)
    {
        try
        {
            Verify.assertObjectNotNull(actualMap, mapName);

            if (!actualMap.isEmpty())
            {
                fail(mapName + " should be empty; actual size:<" + actualMap.size() + '>');
            }
            if (actualMap.size() != 0)
            {
                fail(mapName + " should be empty; actual size:<" + actualMap.size() + '>');
            }
            if (actualMap.keySet().size() != 0)
            {
                fail(mapName + " should be empty; actual size:<" + actualMap.keySet().size() + '>');
            }
            if (actualMap.values().size() != 0)
            {
                fail(mapName + " should be empty; actual size:<" + actualMap.values().size() + '>');
            }
            if (actualMap.entrySet().size() != 0)
            {
                fail(mapName + " should be empty; actual size:<" + actualMap.entrySet().size() + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Iterable} is <em>not</em> empty.
     */
    public static void assertNotEmpty(Iterable<?> actualIterable)
    {
        try
        {
            Verify.assertNotEmpty(actualIterable, "iterable");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Iterable} is <em>not</em> empty.
     */
    public static void assertNotEmpty(String iterableName, Iterable<?> actualIterable)
    {
        try
        {
            Verify.assertObjectNotNull(actualIterable, iterableName);
            assertFalse(Iterate.isEmpty(actualIterable), iterableName + " should be non-empty, but was empty");
            assertTrue(Iterate.notEmpty(actualIterable), iterableName + " should be non-empty, but was empty");
            assertNotEquals(0, Iterate.sizeOf(actualIterable), iterableName + " should be non-empty, but was empty");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link MutableMapIterable} is <em>not</em> empty.
     */
    public static void assertNotEmpty(MutableMapIterable<?, ?> actualMutableMapIterable)
    {
        try
        {
            Verify.assertNotEmpty(actualMutableMapIterable, "mutableMapIterable");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link MutableMapIterable} is <em>not</em> empty.
     */
    public static void assertNotEmpty(String mutableMapIterableName, MutableMapIterable<?, ?> actualMutableMapIterable)
    {
        try
        {
            Verify.assertObjectNotNull(actualMutableMapIterable, mutableMapIterableName);
            assertFalse(Iterate.isEmpty(actualMutableMapIterable), mutableMapIterableName + " should be non-empty, but was empty");
            assertTrue(Iterate.notEmpty(actualMutableMapIterable), mutableMapIterableName + " should be non-empty, but was empty");
            assertTrue(actualMutableMapIterable.notEmpty(), mutableMapIterableName + " should be non-empty, but was empty");
            assertNotEquals(0, actualMutableMapIterable.size(), mutableMapIterableName + " should be non-empty, but was empty");
            assertNotEquals(0, actualMutableMapIterable.keySet().size(), mutableMapIterableName + " should be non-empty, but was empty");
            assertNotEquals(0, actualMutableMapIterable.values().size(), mutableMapIterableName + " should be non-empty, but was empty");
            assertNotEquals(0, actualMutableMapIterable.entrySet().size(), mutableMapIterableName + " should be non-empty, but was empty");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link PrimitiveIterable} is <em>not</em> empty.
     */
    public static void assertNotEmpty(PrimitiveIterable primitiveIterable)
    {
        try
        {
            Verify.assertNotEmpty(primitiveIterable, "primitiveIterable");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link PrimitiveIterable} is <em>not</em> empty.
     */
    public static void assertNotEmpty(String iterableName, PrimitiveIterable primitiveIterable)
    {
        try
        {
            Verify.assertObjectNotNull(primitiveIterable, iterableName);
            assertFalse(primitiveIterable.isEmpty(), iterableName + " should be non-empty, but was empty");
            assertTrue(primitiveIterable.notEmpty(), iterableName + " should be non-empty, but was empty");
            assertNotEquals(0, primitiveIterable.size(), iterableName + " should be non-empty, but was empty");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Iterable} is <em>not</em> empty.
     */
    public static void assertIterableNotEmpty(Iterable<?> iterable)
    {
        try
        {
            Verify.assertIterableNotEmpty(iterable, "iterable");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Iterable} is <em>not</em> empty.
     */
    public static void assertIterableNotEmpty(String iterableName, Iterable<?> iterable)
    {
        try
        {
            Verify.assertObjectNotNull(iterable, iterableName);
            assertFalse(Iterate.isEmpty(iterable), iterableName + " should be non-empty, but was empty");
            assertTrue(Iterate.notEmpty(iterable), iterableName + " should be non-empty, but was empty");
            assertNotEquals(0, Iterate.sizeOf(iterable), iterableName + " should be non-empty, but was empty");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Map} is <em>not</em> empty.
     */
    public static void assertNotEmpty(Map<?, ?> actualMap)
    {
        try
        {
            Verify.assertNotEmpty(actualMap, "map");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Map} is <em>not</em> empty.
     */
    public static void assertNotEmpty(String mapName, Map<?, ?> actualMap)
    {
        try
        {
            Verify.assertObjectNotNull(actualMap, mapName);
            assertFalse(actualMap.isEmpty(), mapName + " should be non-empty, but was empty");
            assertNotEquals(0, actualMap.size(), mapName + " should be non-empty, but was empty");
            assertNotEquals(0, actualMap.keySet().size(), mapName + " should be non-empty, but was empty");
            assertNotEquals(0, actualMap.values().size(), mapName + " should be non-empty, but was empty");
            assertNotEquals(0, actualMap.entrySet().size(), mapName + " should be non-empty, but was empty");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Multimap} is <em>not</em> empty.
     */
    public static void assertNotEmpty(Multimap<?, ?> actualMultimap)
    {
        try
        {
            Verify.assertNotEmpty(actualMultimap, "multimap");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Multimap} is <em>not</em> empty.
     */
    public static void assertNotEmpty(String multimapName, Multimap<?, ?> actualMultimap)
    {
        try
        {
            Verify.assertObjectNotNull(actualMultimap, multimapName);
            assertTrue(actualMultimap.notEmpty(), multimapName + " should be non-empty, but was empty");
            assertFalse(actualMultimap.isEmpty(), multimapName + " should be non-empty, but was empty");
            assertNotEquals(0, actualMultimap.size(), multimapName + " should be non-empty, but was empty");
            assertNotEquals(0, actualMultimap.sizeDistinct(), multimapName + " should be non-empty, but was empty");
            assertNotEquals(0, actualMultimap.keyBag().size(), multimapName + " should be non-empty, but was empty");
            assertNotEquals(0, actualMultimap.keysView().size(), multimapName + " should be non-empty, but was empty");
            assertNotEquals(0, actualMultimap.valuesView().size(), multimapName + " should be non-empty, but was empty");
            assertNotEquals(0, actualMultimap.keyValuePairsView().size(), multimapName + " should be non-empty, but was empty");
            assertNotEquals(0, actualMultimap.keyMultiValuePairsView().size(), multimapName + " should be non-empty, but was empty");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static <T> void assertNotEmpty(String itemsName, T[] items)
    {
        try
        {
            Verify.assertObjectNotNull(items, itemsName);
            Verify.assertNotEquals(0, items.length, itemsName);
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static <T> void assertNotEmpty(T[] items)
    {
        try
        {
            Verify.assertNotEmpty(items, "items");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert the size of the given array.
     */
    public static void assertSize(int expectedSize, Object[] actualArray)
    {
        try
        {
            Verify.assertSize(expectedSize, actualArray, "array");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert the size of the given array.
     */
    public static void assertSize(String arrayName, int expectedSize, Object[] actualArray)
    {
        try
        {
            assertNotNull(actualArray, arrayName + " should not be null");

            int actualSize = actualArray.length;
            if (actualSize != expectedSize)
            {
                fail("Incorrect size for "
                        + arrayName
                        + "; expected:<"
                        + expectedSize
                        + "> but was:<"
                        + actualSize
                        + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert the size of the given {@link Iterable}.
     */
    public static void assertSize(int expectedSize, Iterable<?> actualIterable)
    {
        try
        {
            Verify.assertSize(expectedSize, actualIterable, "iterable");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert the size of the given {@link Iterable}.
     */
    public static void assertSize(
            String iterableName,
            int expectedSize,
            Iterable<?> actualIterable)
    {
        try
        {
            Verify.assertObjectNotNull(actualIterable, iterableName);

            int actualSize = Iterate.sizeOf(actualIterable);
            if (actualSize != expectedSize)
            {
                fail("Incorrect size for "
                        + iterableName
                        + "; expected:<"
                        + expectedSize
                        + "> but was:<"
                        + actualSize
                        + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert the size of the given {@link PrimitiveIterable}.
     */
    public static void assertSize(int expectedSize, PrimitiveIterable primitiveIterable)
    {
        try
        {
            Verify.assertSize(expectedSize, primitiveIterable, "primitiveIterable");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert the size of the given {@link PrimitiveIterable}.
     */
    public static void assertSize(
            String primitiveIterableName,
            int expectedSize,
            PrimitiveIterable actualPrimitiveIterable)
    {
        try
        {
            Verify.assertObjectNotNull(actualPrimitiveIterable, primitiveIterableName);

            int actualSize = actualPrimitiveIterable.size();
            if (actualSize != expectedSize)
            {
                fail("Incorrect size for "
                        + primitiveIterableName
                        + "; expected:<"
                        + expectedSize
                        + "> but was:<"
                        + actualSize
                        + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert the size of the given {@link Iterable}.
     */
    public static void assertIterableSize(int expectedSize, Iterable<?> actualIterable)
    {
        try
        {
            Verify.assertIterableSize(expectedSize, actualIterable, "iterable");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert the size of the given {@link Iterable}.
     */
    public static void assertIterableSize(
            String iterableName,
            int expectedSize,
            Iterable<?> actualIterable)
    {
        try
        {
            Verify.assertObjectNotNull(actualIterable, iterableName);

            int actualSize = Iterate.sizeOf(actualIterable);
            if (actualSize != expectedSize)
            {
                fail("Incorrect size for "
                        + iterableName
                        + "; expected:<"
                        + expectedSize
                        + "> but was:<"
                        + actualSize
                        + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert the size of the given {@link Map}.
     */
    public static void assertSize(String mapName, int expectedSize, Map<?, ?> actualMap)
    {
        try
        {
            Verify.assertSize(expectedSize, actualMap.keySet(), mapName);
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert the size of the given {@link Map}.
     */
    public static void assertSize(int expectedSize, Map<?, ?> actualMap)
    {
        try
        {
            Verify.assertSize(expectedSize, actualMap, "map");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert the size of the given {@link Multimap}.
     */
    public static void assertSize(int expectedSize, Multimap<?, ?> actualMultimap)
    {
        try
        {
            Verify.assertSize(expectedSize, actualMultimap, "multimap");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert the size of the given {@link Multimap}.
     */
    public static void assertSize(String multimapName, int expectedSize, Multimap<?, ?> actualMultimap)
    {
        try
        {
            int actualSize = actualMultimap.size();
            if (actualSize != expectedSize)
            {
                fail("Incorrect size for "
                        + multimapName
                        + "; expected:<"
                        + expectedSize
                        + "> but was:<"
                        + actualSize
                        + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert the size of the given {@link MutableMapIterable}.
     */
    public static void assertSize(int expectedSize, MutableMapIterable<?, ?> mutableMapIterable)
    {
        try
        {
            Verify.assertSize(expectedSize, mutableMapIterable, "map");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert the size of the given {@link MutableMapIterable}.
     */
    public static void assertSize(String mapName, int expectedSize, MutableMapIterable<?, ?> mutableMapIterable)
    {
        try
        {
            int actualSize = mutableMapIterable.size();
            if (actualSize != expectedSize)
            {
                fail("Incorrect size for " + mapName + "; expected:<" + expectedSize + "> but was:<" + actualSize + '>');
            }
            int keySetSize = mutableMapIterable.keySet().size();
            if (keySetSize != expectedSize)
            {
                fail("Incorrect size for " + mapName + ".keySet(); expected:<" + expectedSize + "> but was:<" + actualSize + '>');
            }
            int valuesSize = mutableMapIterable.values().size();
            if (valuesSize != expectedSize)
            {
                fail("Incorrect size for " + mapName + ".values(); expected:<" + expectedSize + "> but was:<" + actualSize + '>');
            }
            int entrySetSize = mutableMapIterable.entrySet().size();
            if (entrySetSize != expectedSize)
            {
                fail("Incorrect size for " + mapName + ".entrySet(); expected:<" + expectedSize + "> but was:<" + actualSize + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert the size of the given {@link ImmutableSet}.
     */
    public static void assertSize(int expectedSize, ImmutableSet<?> actualImmutableSet)
    {
        try
        {
            Verify.assertSize(expectedSize, actualImmutableSet, "immutable set");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert the size of the given {@link ImmutableSet}.
     */
    public static void assertSize(String immutableSetName, int expectedSize, ImmutableSet<?> actualImmutableSet)
    {
        try
        {
            int actualSize = actualImmutableSet.size();
            if (actualSize != expectedSize)
            {
                fail("Incorrect size for "
                        + immutableSetName
                        + "; expected:<"
                        + expectedSize
                        + "> but was:<"
                        + actualSize
                        + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@code stringToFind} is contained within the {@code stringToSearch}.
     */
    public static void assertContains(String stringToFind, String stringToSearch)
    {
        try
        {
            Verify.assertContains(stringToSearch, "string", stringToFind);
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@code unexpectedString} is <em>not</em> contained within the {@code stringToSearch}.
     */
    public static void assertNotContains(String unexpectedString, String stringToSearch)
    {
        try
        {
            Verify.assertNotContains(stringToSearch, "string", unexpectedString);
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@code stringToFind} is contained within the {@code stringToSearch}.
     */
    public static void assertContains(String stringName, String stringToFind, String stringToSearch)
    {
        try
        {
            assertNotNull(stringToFind, "stringToFind should not be null");
            assertNotNull(stringToSearch, "stringToSearch should not be null");

            if (!stringToSearch.contains(stringToFind))
            {
                fail(stringName
                        + " did not contain stringToFind:<"
                        + stringToFind
                        + "> in stringToSearch:<"
                        + stringToSearch
                        + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@code unexpectedString} is <em>not</em> contained within the {@code stringToSearch}.
     */
    public static void assertNotContains(String stringName, String unexpectedString, String stringToSearch)
    {
        try
        {
            assertNotNull(unexpectedString, "unexpectedString should not be null");
            assertNotNull(stringToSearch, "stringToSearch should not be null");

            if (stringToSearch.contains(unexpectedString))
            {
                fail(stringName
                        + " contains unexpectedString:<"
                        + unexpectedString
                        + "> in stringToSearch:<"
                        + stringToSearch
                        + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static <T> void assertCount(
            int expectedCount,
            Iterable<T> iterable,
            Predicate<? super T> predicate)
    {
        assertEquals(expectedCount, Iterate.count(iterable, predicate));
    }

    public static <T> void assertAllSatisfy(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        try
        {
            Verify.assertAllSatisfy(iterable, predicate, "The following items failed to satisfy the condition");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static <K, V> void assertAllSatisfy(Map<K, V> map, Predicate<? super V> predicate)
    {
        try
        {
            Verify.assertAllSatisfy(map.values(), predicate);
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static <T> void assertAllSatisfy(String message, Iterable<T> iterable, Predicate<? super T> predicate)
    {
        try
        {
            MutableList<T> unnacceptable = Iterate.reject(iterable, predicate, Lists.mutable.<T>of());
            if (unnacceptable.notEmpty())
            {
                fail(message + " <" + unnacceptable + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static <T> void assertAnySatisfy(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        try
        {
            Verify.assertAnySatisfy(iterable, predicate, "No items satisfied the condition");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static <K, V> void assertAnySatisfy(Map<K, V> map, Predicate<? super V> predicate)
    {
        try
        {
            Verify.assertAnySatisfy(map.values(), predicate);
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static <T> void assertAnySatisfy(String message, Iterable<T> iterable, Predicate<? super T> predicate)
    {
        try
        {
            assertTrue(Predicates.anySatisfy(predicate).accept(iterable), message);
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static <T> void assertNoneSatisfy(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        try
        {
            Verify.assertNoneSatisfy(iterable, predicate, "The following items satisfied the condition");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static <K, V> void assertNoneSatisfy(Map<K, V> map, Predicate<? super V> predicate)
    {
        try
        {
            Verify.assertNoneSatisfy(map.values(), predicate);
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static <T> void assertNoneSatisfy(String message, Iterable<T> iterable, Predicate<? super T> predicate)
    {
        try
        {
            MutableList<T> unnacceptable = Iterate.select(iterable, predicate, Lists.mutable.<T>empty());
            if (unnacceptable.notEmpty())
            {
                fail(message + " <" + unnacceptable + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Map} contains all of the given keys and values.
     */
    public static void assertContainsAllKeyValues(Map<?, ?> actualMap, Object... keyValues)
    {
        try
        {
            Verify.assertContainsAllKeyValues(actualMap, keyValues, "map");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Map} contains all of the given keys and values.
     */
    public static void assertContainsAllKeyValues(
            String mapName,
            Map<?, ?> actualMap,
            Object... expectedKeyValues)
    {
        try
        {
            Verify.assertNotEmpty(expectedKeyValues, "Expected keys/values in assertion");

            if (expectedKeyValues.length % 2 != 0)
            {
                fail("Odd number of keys and values (every key must have a value)");
            }

            Verify.assertObjectNotNull(actualMap, mapName);
            Verify.assertMapContainsKeys(actualMap, expectedKeyValues, mapName);
            Verify.assertMapContainsValues(actualMap, expectedKeyValues, mapName);
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link MapIterable} contains all of the given keys and values.
     */
    public static void assertContainsAllKeyValues(MapIterable<?, ?> mapIterable, Object... keyValues)
    {
        try
        {
            Verify.assertContainsAllKeyValues(mapIterable, keyValues, "map");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link MapIterable} contains all of the given keys and values.
     */
    public static void assertContainsAllKeyValues(
            String mapIterableName,
            MapIterable<?, ?> mapIterable,
            Object... expectedKeyValues)
    {
        try
        {
            Verify.assertNotEmpty(expectedKeyValues, "Expected keys/values in assertion");

            if (expectedKeyValues.length % 2 != 0)
            {
                fail("Odd number of keys and values (every key must have a value)");
            }

            Verify.assertObjectNotNull(mapIterable, mapIterableName);
            Verify.assertMapContainsKeys(mapIterable, expectedKeyValues, mapIterableName);
            Verify.assertMapContainsValues(mapIterable, expectedKeyValues, mapIterableName);
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link MutableMapIterable} contains all of the given keys and values.
     */
    public static void assertContainsAllKeyValues(MutableMapIterable<?, ?> mutableMapIterable, Object... keyValues)
    {
        try
        {
            Verify.assertContainsAllKeyValues(mutableMapIterable, keyValues, "map");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link MutableMapIterable} contains all of the given keys and values.
     */
    public static void assertContainsAllKeyValues(
            String mutableMapIterableName,
            MutableMapIterable<?, ?> mutableMapIterable,
            Object... expectedKeyValues)
    {
        try
        {
            Verify.assertNotEmpty(expectedKeyValues, "Expected keys/values in assertion");

            if (expectedKeyValues.length % 2 != 0)
            {
                fail("Odd number of keys and values (every key must have a value)");
            }

            Verify.assertObjectNotNull(mutableMapIterable, mutableMapIterableName);
            Verify.assertMapContainsKeys(mutableMapIterable, expectedKeyValues, mutableMapIterableName);
            Verify.assertMapContainsValues(mutableMapIterable, expectedKeyValues, mutableMapIterableName);
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link ImmutableMapIterable} contains all of the given keys and values.
     */
    public static void assertContainsAllKeyValues(ImmutableMapIterable<?, ?> immutableMapIterable, Object... keyValues)
    {
        try
        {
            Verify.assertContainsAllKeyValues(immutableMapIterable, keyValues, "map");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link ImmutableMapIterable} contains all of the given keys and values.
     */
    public static void assertContainsAllKeyValues(
            String immutableMapIterableName,
            ImmutableMapIterable<?, ?> immutableMapIterable,
            Object... expectedKeyValues)
    {
        try
        {
            Verify.assertNotEmpty(expectedKeyValues, "Expected keys/values in assertion");

            if (expectedKeyValues.length % 2 != 0)
            {
                fail("Odd number of keys and values (every key must have a value)");
            }

            Verify.assertObjectNotNull(immutableMapIterable, immutableMapIterableName);
            Verify.assertMapContainsKeys(immutableMapIterable, expectedKeyValues, immutableMapIterableName);
            Verify.assertMapContainsValues(immutableMapIterable, expectedKeyValues, immutableMapIterableName);
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void denyContainsAny(Collection<?> actualCollection, Object... items)
    {
        try
        {
            Verify.denyContainsAny(actualCollection, items, "collection");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertContainsNone(Collection<?> actualCollection, Object... items)
    {
        try
        {
            Verify.denyContainsAny(actualCollection, items, "collection");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Collection} contains the given item.
     */
    public static void assertContains(Object expectedItem, Collection<?> actualCollection)
    {
        try
        {
            Verify.assertContains(expectedItem, actualCollection, "collection");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Collection} contains the given item.
     */
    public static void assertContains(
            String collectionName,
            Object expectedItem,
            Collection<?> actualCollection)
    {
        try
        {
            Verify.assertObjectNotNull(actualCollection, collectionName);

            if (!actualCollection.contains(expectedItem))
            {
                fail(collectionName + " did not contain expectedItem:<" + expectedItem + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link ImmutableCollection} contains the given item.
     */
    public static void assertContains(Object expectedItem, ImmutableCollection<?> actualImmutableCollection)
    {
        try
        {
            Verify.assertContains(expectedItem, actualImmutableCollection, "ImmutableCollection");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link ImmutableCollection} contains the given item.
     */
    public static void assertContains(
            String immutableCollectionName,
            Object expectedItem,
            ImmutableCollection<?> actualImmutableCollection)
    {
        try
        {
            Verify.assertObjectNotNull(actualImmutableCollection, immutableCollectionName);

            if (!actualImmutableCollection.contains(expectedItem))
            {
                fail(immutableCollectionName + " did not contain expectedItem:<" + expectedItem + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertContainsAll(
            Iterable<?> iterable,
            Object... items)
    {
        try
        {
            Verify.assertContainsAll(iterable, items, "iterable");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertContainsAll(
            String collectionName,
            final Iterable<?> iterable,
            Object... items)
    {
        try
        {
            Verify.assertObjectNotNull(iterable, collectionName);

            Verify.assertNotEmpty(items, "Expected items in assertion");

            Predicate<Object> containsPredicate = new Predicate<Object>()
            {
                public boolean accept(Object each)
                {
                    return Iterate.contains(iterable, each);
                }
            };

            if (!ArrayIterate.allSatisfy(items, containsPredicate))
            {
                ImmutableList<Object> result = Lists.immutable.of(items).newWithoutAll(iterable);
                fail(collectionName + " did not contain these items" + ":<" + result + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertListsEqual(List<?> expectedList, List<?> actualList)
    {
        try
        {
            Verify.assertListsEqual(expectedList, actualList, "list");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertListsEqual(String listName, List<?> expectedList, List<?> actualList)
    {
        try
        {
            if (expectedList == null && actualList == null)
            {
                return;
            }
            assertNotNull(expectedList);
            assertNotNull(actualList);
            assertEquals(expectedList.size(), actualList.size(), listName + " size");
            for (int index = 0; index < actualList.size(); index++)
            {
                Object eachExpected = expectedList.get(index);
                Object eachActual = actualList.get(index);
                if (!Comparators.nullSafeEquals(eachExpected, eachActual))
                {
                    failNotEquals(eachExpected, eachActual, listName + " first differed at element [" + index + "];");
                }
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertSetsEqual(Set<?> expectedSet, Set<?> actualSet)
    {
        try
        {
            Verify.assertSetsEqual(expectedSet, actualSet, "set");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertSetsEqual(String setName, Set<?> expectedSet, Set<?> actualSet)
    {
        try
        {
            if (expectedSet == null)
            {
                assertNull(actualSet, setName + " should be null");
                return;
            }

            Verify.assertObjectNotNull(actualSet, setName);
            Verify.assertSize(expectedSet.size(), actualSet, setName);

            if (!actualSet.equals(expectedSet))
            {
                MutableSet<?> inExpectedOnlySet = UnifiedSet.newSet(expectedSet);
                inExpectedOnlySet.removeAll(actualSet);

                int numberDifferences = inExpectedOnlySet.size();
                String message = setName + ": " + numberDifferences + " elements different.";

                if (numberDifferences > MAX_DIFFERENCES)
                {
                    fail(message);
                }

                MutableSet<?> inActualOnlySet = UnifiedSet.newSet(actualSet);
                inActualOnlySet.removeAll(expectedSet);

                //noinspection UseOfObsoleteAssert
                failNotEquals(inExpectedOnlySet, inActualOnlySet, message);
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertSortedSetsEqual(SortedSet<?> expectedSet, SortedSet<?> actualSet)
    {
        try
        {
            Verify.assertSortedSetsEqual(expectedSet, actualSet, "sortedSets");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertSortedSetsEqual(String setName, SortedSet<?> expectedSet, SortedSet<?> actualSet)
    {
        try
        {
            assertEquals(expectedSet, actualSet, setName);
            Verify.assertIterablesEqual(expectedSet, actualSet, setName);
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertSortedBagsEqual(SortedBag<?> expectedBag, SortedBag<?> actualBag)
    {
        try
        {
            Verify.assertSortedBagsEqual(expectedBag, actualBag, "sortedBags");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertSortedBagsEqual(String bagName, SortedBag<?> expectedBag, SortedBag<?> actualBag)
    {
        try
        {
            assertEquals(expectedBag, actualBag, bagName);
            Verify.assertIterablesEqual(expectedBag, actualBag, bagName);
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertSortedMapsEqual(SortedMapIterable<?, ?> expectedMap, SortedMapIterable<?, ?> actualMap)
    {
        try
        {
            Verify.assertSortedMapsEqual(expectedMap, actualMap, "sortedMaps");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertSortedMapsEqual(String mapName, SortedMapIterable<?, ?> expectedMap, SortedMapIterable<?, ?> actualMap)
    {
        try
        {
            assertEquals(expectedMap, actualMap, mapName);
            Verify.assertIterablesEqual(expectedMap, actualMap, mapName);
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertIterablesEqual(Iterable<?> expectedIterable, Iterable<?> actualIterable)
    {
        try
        {
            Verify.assertIterablesEqual(expectedIterable, actualIterable, "iterables");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertIterablesEqual(String iterableName, Iterable<?> expectedIterable, Iterable<?> actualIterable)
    {
        try
        {
            if (expectedIterable == null)
            {
                assertNull(actualIterable, iterableName + " should be null");
                return;
            }

            Verify.assertObjectNotNull(actualIterable, iterableName);

            if (expectedIterable instanceof InternalIterable<?> iterable && actualIterable instanceof InternalIterable<?> iterable)
            {
                MutableList<Object> expectedList = FastList.newList();
                MutableList<Object> actualList = FastList.newList();
                iterable.forEach(CollectionAddProcedure.on(expectedList));
                iterable.forEach(CollectionAddProcedure.on(actualList));
                Verify.assertListsEqual(expectedList, actualList, iterableName);
            }
            else
            {
                Iterator<?> expectedIterator = expectedIterable.iterator();
                Iterator<?> actualIterator = actualIterable.iterator();
                int index = 0;

                while (expectedIterator.hasNext() && actualIterator.hasNext())
                {
                    Object eachExpected = expectedIterator.next();
                    Object eachActual = actualIterator.next();

                    if (!Comparators.nullSafeEquals(eachExpected, eachActual))
                    {
                        //noinspection UseOfObsoleteAssert
                        failNotEquals(eachExpected, eachActual, iterableName + " first differed at element [" + index + "];");
                    }
                    index++;
                }

                assertFalse(expectedIterator.hasNext(), "Actual " + iterableName + " had " + index + " elements but expected " + iterableName + " had more.");
                assertFalse(actualIterator.hasNext(), "Expected " + iterableName + " had " + index + " elements but actual " + iterableName + " had more.");
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertMapsEqual(Map<?, ?> expectedMap, Map<?, ?> actualMap)
    {
        try
        {
            Verify.assertMapsEqual(expectedMap, actualMap, "map");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertMapsEqual(String mapName, Map<?, ?> expectedMap, Map<?, ?> actualMap)
    {
        try
        {
            if (expectedMap == null)
            {
                assertNull(actualMap, mapName + " should be null");
                return;
            }

            assertNotNull(actualMap, mapName + " should not be null");

            Set<? extends Map.Entry<?, ?>> expectedEntries = expectedMap.entrySet();
            for (Map.Entry<?, ?> expectedEntry : expectedEntries)
            {
                Object expectedKey = expectedEntry.getKey();
                Object expectedValue = expectedEntry.getValue();
                Object actualValue = actualMap.get(expectedKey);
                if (!Comparators.nullSafeEquals(actualValue, expectedValue))
                {
                    fail("Values differ at key " + expectedKey + " expected " + expectedValue + " but was " + actualValue);
                }
            }
            Verify.assertSetsEqual(expectedMap.keySet(), actualMap.keySet(), mapName + " keys");
            Verify.assertSetsEqual(expectedMap.entrySet(), actualMap.entrySet(), mapName + " entries");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertBagsEqual(Bag<?> expectedBag, Bag<?> actualBag)
    {
        try
        {
            Verify.assertBagsEqual(expectedBag, actualBag, "bag");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertBagsEqual(String bagName, Bag<?> expectedBag, final Bag<?> actualBag)
    {
        try
        {
            if (expectedBag == null)
            {
                assertNull(actualBag, bagName + " should be null");
                return;
            }

            assertNotNull(actualBag, bagName + " should not be null");

            assertEquals(expectedBag.size(), actualBag.size(), bagName + " size");
            assertEquals(expectedBag.sizeDistinct(), actualBag.sizeDistinct(), bagName + " sizeDistinct");

            expectedBag.forEachWithOccurrences(new ObjectIntProcedure<Object>()
            {
                public void value(Object expectedKey, int expectedValue)
                {
                    int actualValue = actualBag.occurrencesOf(expectedKey);
                    assertEquals(expectedValue, actualValue, "Occurrences of " + expectedKey);
                }
            });
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    private static void assertMapContainsKeys(
            String mapName,
            Map<?, ?> actualMap,
            Object... expectedKeyValues)
    {
        try
        {
            Verify.assertNotEmpty(expectedKeyValues, "Expected keys/values in assertion");

            MutableList<Object> expectedKeys = Lists.mutable.of();
            for (int i = 0; i < expectedKeyValues.length; i += 2)
            {
                expectedKeys.add(expectedKeyValues[i]);
            }

            Verify.assertContainsAll(actualMap.keySet(), expectedKeys.toArray(), mapName + ".keySet()");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    private static void assertMapContainsValues(
            String mapName,
            Map<?, ?> actualMap,
            Object... expectedKeyValues)
    {
        try
        {
            Verify.assertNotEmpty(expectedKeyValues, "Expected keys/values in assertion");

            MutableMap<Object, String> missingEntries = UnifiedMap.newMap();
            int i = 0;
            while (i < expectedKeyValues.length)
            {
                Object expectedKey = expectedKeyValues[i++];
                Object expectedValue = expectedKeyValues[i++];
                Object actualValue = actualMap.get(expectedKey);
                if (!Comparators.nullSafeEquals(expectedValue, actualValue))
                {
                    missingEntries.put(
                            expectedKey,
                            "expectedValue:<" + expectedValue + ">, actualValue:<" + actualValue + '>');
                }
            }
            if (!missingEntries.isEmpty())
            {
                StringBuilder buf = new StringBuilder(mapName + " has incorrect values for keys:[");
                for (Map.Entry<Object, String> expectedEntry : missingEntries.entrySet())
                {
                    buf.append("key:<")
                            .append(expectedEntry.getKey())
                            .append(',')
                            .append(expectedEntry.getValue())
                            .append("> ");
                }
                buf.append(']');
                fail(buf.toString());
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    private static void assertMapContainsKeys(
            String mapIterableName,
            MapIterable<?, ?> mapIterable,
            Object... expectedKeyValues)
    {
        try
        {
            Verify.assertNotEmpty(expectedKeyValues, "Expected keys/values in assertion");

            MutableList<Object> expectedKeys = Lists.mutable.of();
            for (int i = 0; i < expectedKeyValues.length; i += 2)
            {
                expectedKeys.add(expectedKeyValues[i]);
            }

            Verify.assertContainsAll(mapIterable.keysView(), expectedKeys.toArray(), mapIterableName + ".keysView()");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    private static void assertMapContainsValues(
            String mapIterableName,
            MapIterable<?, ?> mapIterable,
            Object... expectedKeyValues)
    {
        try
        {
            Verify.assertNotEmpty(expectedKeyValues, "Expected keys/values in assertion");

            MutableList<Object> expectedValues = Lists.mutable.of();
            for (int i = 1; i < expectedKeyValues.length; i += 2)
            {
                expectedValues.add(expectedKeyValues[i]);
            }

            Verify.assertContainsAll(mapIterable.valuesView(), expectedValues.toArray(), mapIterableName + ".valuesView()");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    private static void assertMapContainsKeys(
            String mutableMapIterableName,
            MutableMapIterable<?, ?> mutableMapIterable,
            Object... expectedKeyValues)
    {
        try
        {
            Verify.assertNotEmpty(expectedKeyValues, "Expected keys/values in assertion");

            MutableList<Object> expectedKeys = Lists.mutable.of();
            for (int i = 0; i < expectedKeyValues.length; i += 2)
            {
                expectedKeys.add(expectedKeyValues[i]);
            }

            Verify.assertContainsAll(mutableMapIterable.keysView(), expectedKeys.toArray(), mutableMapIterableName + ".keysView()");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    private static void assertMapContainsValues(
            String mutableMapIterableName,
            MutableMapIterable<?, ?> mutableMapIterable,
            Object... expectedKeyValues)
    {
        try
        {
            Verify.assertNotEmpty(expectedKeyValues, "Expected keys/values in assertion");

            MutableList<Object> expectedValues = Lists.mutable.of();
            for (int i = 1; i < expectedKeyValues.length; i += 2)
            {
                expectedValues.add(expectedKeyValues[i]);
            }

            Verify.assertContainsAll(mutableMapIterable.valuesView(), expectedValues.toArray(), mutableMapIterableName + ".valuesView()");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    private static void assertMapContainsKeys(
            String immutableMapIterableName,
            ImmutableMapIterable<?, ?> immutableMapIterable,
            Object... expectedKeyValues)
    {
        try
        {
            Verify.assertNotEmpty(expectedKeyValues, "Expected keys/values in assertion");

            MutableList<Object> expectedKeys = Lists.mutable.of();
            for (int i = 0; i < expectedKeyValues.length; i += 2)
            {
                expectedKeys.add(expectedKeyValues[i]);
            }

            Verify.assertContainsAll(immutableMapIterable.keysView(), expectedKeys.toArray(), immutableMapIterableName + ".keysView()");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    private static void assertMapContainsValues(
            String immutableMapIterableName,
            ImmutableMapIterable<?, ?> immutableMapIterable,
            Object... expectedKeyValues)
    {
        try
        {
            Verify.assertNotEmpty(expectedKeyValues, "Expected keys/values in assertion");

            MutableList<Object> expectedValues = Lists.mutable.of();
            for (int i = 1; i < expectedKeyValues.length; i += 2)
            {
                expectedValues.add(expectedKeyValues[i]);
            }

            Verify.assertContainsAll(immutableMapIterable.valuesView(), expectedValues.toArray(), immutableMapIterableName + ".valuesView()");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Multimap} contains an entry with the given key and value.
     */
    public static <K, V> void assertContainsEntry(
            K expectedKey,
            V expectedValue,
            Multimap<K, V> actualMultimap)
    {
        try
        {
            Verify.assertContainsEntry(expectedKey, expectedValue, actualMultimap, "multimap");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Multimap} contains an entry with the given key and value.
     */
    public static <K, V> void assertContainsEntry(
            String multimapName,
            K expectedKey,
            V expectedValue,
            Multimap<K, V> actualMultimap)
    {
        try
        {
            assertNotNull(actualMultimap, multimapName);

            if (!actualMultimap.containsKeyAndValue(expectedKey, expectedValue))
            {
                fail(multimapName + " did not contain entry: <" + expectedKey + ", " + expectedValue + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert the given {@link Multimap} contains all of the given keys and values.
     */
    public static void assertContainsAllEntries(Multimap<?, ?> actualMultimap, Object... keyValues)
    {
        try
        {
            Verify.assertContainsAllEntries(actualMultimap, keyValues, "multimap");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert the given {@link Multimap} contains all of the given keys and values.
     */
    public static void assertContainsAllEntries(
            String multimapName,
            Multimap<?, ?> actualMultimap,
            Object... expectedKeyValues)
    {
        try
        {
            Verify.assertNotEmpty(expectedKeyValues, "Expected keys/values in assertion");

            if (expectedKeyValues.length % 2 != 0)
            {
                fail("Odd number of keys and values (every key must have a value)");
            }

            Verify.assertObjectNotNull(actualMultimap, multimapName);

            MutableList<Map.Entry<?, ?>> missingEntries = Lists.mutable.of();
            for (int i = 0; i < expectedKeyValues.length; i += 2)
            {
                Object expectedKey = expectedKeyValues[i];
                Object expectedValue = expectedKeyValues[i + 1];

                if (!actualMultimap.containsKeyAndValue(expectedKey, expectedValue))
                {
                    missingEntries.add(new ImmutableEntry<Object, Object>(expectedKey, expectedValue));
                }
            }

            if (!missingEntries.isEmpty())
            {
                fail(multimapName + " is missing entries: " + missingEntries);
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void denyContainsAny(
            String collectionName,
            Collection<?> actualCollection,
            Object... items)
    {
        try
        {
            Verify.assertNotEmpty(items, "Expected items in assertion");

            Verify.assertObjectNotNull(actualCollection, collectionName);

            MutableSet<Object> intersection = Sets.intersect(UnifiedSet.newSet(actualCollection), UnifiedSet.newSetWith(items));
            if (intersection.notEmpty())
            {
                fail(collectionName
                        + " has an intersection with these items and should not :<" + intersection + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Map} contains an entry with the given key.
     */
    public static void assertContainsKey(Object expectedKey, Map<?, ?> actualMap)
    {
        try
        {
            Verify.assertContainsKey(expectedKey, actualMap, "map");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Map} contains an entry with the given key.
     */
    public static void assertContainsKey(String mapName, Object expectedKey, Map<?, ?> actualMap)
    {
        try
        {
            assertNotNull(actualMap, mapName);

            if (!actualMap.containsKey(expectedKey))
            {
                fail(mapName + " did not contain expectedKey:<" + expectedKey + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link MapIterable} contains an entry with the given key.
     */
    public static void assertContainsKey(Object expectedKey, MapIterable<?, ?> mapIterable)
    {
        try
        {
            Verify.assertContainsKey(expectedKey, mapIterable, "map");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link MapIterable} contains an entry with the given key.
     */
    public static void assertContainsKey(
            String mapIterableName,
            Object expectedKey,
            MapIterable<?, ?> mapIterable)
    {
        try
        {
            assertNotNull(mapIterable, mapIterableName);

            if (!mapIterable.containsKey(expectedKey))
            {
                fail(mapIterableName + " did not contain expectedKey:<" + expectedKey + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link MutableMapIterable} contains an entry with the given key.
     */
    public static void assertContainsKey(Object expectedKey, MutableMapIterable<?, ?> mutableMapIterable)
    {
        try
        {
            Verify.assertContainsKey(expectedKey, mutableMapIterable, "map");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link MutableMapIterable} contains an entry with the given key.
     */
    public static void assertContainsKey(
            String mutableMapIterableName,
            Object expectedKey,
            MutableMapIterable<?, ?> mutableMapIterable)
    {
        try
        {
            assertNotNull(mutableMapIterable, mutableMapIterableName);

            if (!mutableMapIterable.containsKey(expectedKey))
            {
                fail(mutableMapIterableName + " did not contain expectedKey:<" + expectedKey + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link ImmutableMapIterable} contains an entry with the given key.
     */
    public static void assertContainsKey(Object expectedKey, ImmutableMapIterable<?, ?> immutableMapIterable)
    {
        try
        {
            Verify.assertContainsKey(expectedKey, immutableMapIterable, "map");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link ImmutableMapIterable} contains an entry with the given key.
     */
    public static void assertContainsKey(
            String immutableMapIterableName,
            Object expectedKey,
            ImmutableMapIterable<?, ?> immutableMapIterable)
    {
        try
        {
            assertNotNull(immutableMapIterable, immutableMapIterableName);

            if (!immutableMapIterable.containsKey(expectedKey))
            {
                fail(immutableMapIterableName + " did not contain expectedKey:<" + expectedKey + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Deny that the given {@link Map} contains an entry with the given key.
     */
    public static void denyContainsKey(Object unexpectedKey, Map<?, ?> actualMap)
    {
        try
        {
            Verify.denyContainsKey(unexpectedKey, actualMap, "map");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Deny that the given {@link Map} contains an entry with the given key.
     */
    public static void denyContainsKey(String mapName, Object unexpectedKey, Map<?, ?> actualMap)
    {
        try
        {
            assertNotNull(actualMap, mapName);

            if (actualMap.containsKey(unexpectedKey))
            {
                fail(mapName + " contained unexpectedKey:<" + unexpectedKey + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Map} contains an entry with the given key and value.
     */
    public static void assertContainsKeyValue(
            Object expectedKey,
            Object expectedValue,
            Map<?, ?> actualMap)
    {
        try
        {
            Verify.assertContainsKeyValue(expectedKey, expectedValue, actualMap, "map");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Map} contains an entry with the given key and value.
     */
    public static void assertContainsKeyValue(
            String mapName,
            Object expectedKey,
            Object expectedValue,
            Map<?, ?> actualMap)
    {
        try
        {
            Verify.assertContainsKey(expectedKey, actualMap, mapName);

            Object actualValue = actualMap.get(expectedKey);
            if (!Comparators.nullSafeEquals(actualValue, expectedValue))
            {
                fail(
                        mapName
                                + " entry with expectedKey:<"
                                + expectedKey
                                + "> "
                                + "did not contain expectedValue:<"
                                + expectedValue
                                + ">, "
                                + "but had actualValue:<"
                                + actualValue
                                + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link MapIterable} contains an entry with the given key and value.
     */
    public static void assertContainsKeyValue(
            Object expectedKey,
            Object expectedValue,
            MapIterable<?, ?> mapIterable)
    {
        try
        {
            Verify.assertContainsKeyValue(expectedKey, expectedValue, mapIterable, "map");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link MapIterable} contains an entry with the given key and value.
     */
    public static void assertContainsKeyValue(
            String mapIterableName,
            Object expectedKey,
            Object expectedValue,
            MapIterable<?, ?> mapIterable)
    {
        try
        {
            Verify.assertContainsKey(expectedKey, mapIterable, mapIterableName);

            Object actualValue = mapIterable.get(expectedKey);
            if (!Comparators.nullSafeEquals(actualValue, expectedValue))
            {
                fail(
                        mapIterableName
                                + " entry with expectedKey:<"
                                + expectedKey
                                + "> "
                                + "did not contain expectedValue:<"
                                + expectedValue
                                + ">, "
                                + "but had actualValue:<"
                                + actualValue
                                + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link MutableMapIterable} contains an entry with the given key and value.
     */
    public static void assertContainsKeyValue(
            Object expectedKey,
            Object expectedValue,
            MutableMapIterable<?, ?> mapIterable)
    {
        try
        {
            Verify.assertContainsKeyValue(expectedKey, expectedValue, mapIterable, "map");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link MutableMapIterable} contains an entry with the given key and value.
     */
    public static void assertContainsKeyValue(
            String mapIterableName,
            Object expectedKey,
            Object expectedValue,
            MutableMapIterable<?, ?> mutableMapIterable)
    {
        try
        {
            Verify.assertContainsKey(expectedKey, mutableMapIterable, mapIterableName);

            Object actualValue = mutableMapIterable.get(expectedKey);
            if (!Comparators.nullSafeEquals(actualValue, expectedValue))
            {
                fail(
                        mapIterableName
                                + " entry with expectedKey:<"
                                + expectedKey
                                + "> "
                                + "did not contain expectedValue:<"
                                + expectedValue
                                + ">, "
                                + "but had actualValue:<"
                                + actualValue
                                + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link ImmutableMapIterable} contains an entry with the given key and value.
     */
    public static void assertContainsKeyValue(
            Object expectedKey,
            Object expectedValue,
            ImmutableMapIterable<?, ?> mapIterable)
    {
        try
        {
            Verify.assertContainsKeyValue(expectedKey, expectedValue, mapIterable, "map");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link ImmutableMapIterable} contains an entry with the given key and value.
     */
    public static void assertContainsKeyValue(
            String mapIterableName,
            Object expectedKey,
            Object expectedValue,
            ImmutableMapIterable<?, ?> immutableMapIterable)
    {
        try
        {
            Verify.assertContainsKey(expectedKey, immutableMapIterable, mapIterableName);

            Object actualValue = immutableMapIterable.get(expectedKey);
            if (!Comparators.nullSafeEquals(actualValue, expectedValue))
            {
                fail(
                        mapIterableName
                                + " entry with expectedKey:<"
                                + expectedKey
                                + "> "
                                + "did not contain expectedValue:<"
                                + expectedValue
                                + ">, "
                                + "but had actualValue:<"
                                + actualValue
                                + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Collection} does <em>not</em> contain the given item.
     */
    public static void assertNotContains(Object unexpectedItem, Collection<?> actualCollection)
    {
        try
        {
            Verify.assertNotContains(unexpectedItem, actualCollection, "collection");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Collection} does <em>not</em> contain the given item.
     */
    public static void assertNotContains(
            String collectionName,
            Object unexpectedItem,
            Collection<?> actualCollection)
    {
        try
        {
            Verify.assertObjectNotNull(actualCollection, collectionName);

            if (actualCollection.contains(unexpectedItem))
            {
                fail(collectionName + " should not contain unexpectedItem:<" + unexpectedItem + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Iterable} does <em>not</em> contain the given item.
     */
    public static void assertNotContains(Object unexpectedItem, Iterable<?> iterable)
    {
        try
        {
            Verify.assertNotContains(unexpectedItem, iterable, "iterable");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Iterable} does <em>not</em> contain the given item.
     */
    public static void assertNotContains(
            String collectionName,
            Object unexpectedItem,
            Iterable<?> iterable)
    {
        try
        {
            Verify.assertObjectNotNull(iterable, collectionName);

            if (Iterate.contains(iterable, unexpectedItem))
            {
                fail(collectionName + " should not contain unexpectedItem:<" + unexpectedItem + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Collection} does <em>not</em> contain the given item.
     */
    public static void assertNotContainsKey(Object unexpectedKey, Map<?, ?> actualMap)
    {
        try
        {
            Verify.assertNotContainsKey(unexpectedKey, actualMap, "map");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@link Collection} does <em>not</em> contain the given item.
     */
    public static void assertNotContainsKey(String mapName, Object unexpectedKey, Map<?, ?> actualMap)
    {
        try
        {
            Verify.assertObjectNotNull(actualMap, mapName);

            if (actualMap.containsKey(unexpectedKey))
            {
                fail(mapName + " should not contain unexpectedItem:<" + unexpectedKey + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the formerItem appears before the latterItem in the given {@link Collection}.
     * Both the formerItem and the latterItem must appear in the collection, or this assert will fail.
     */
    public static void assertBefore(Object formerItem, Object latterItem, List<?> actualList)
    {
        try
        {
            Verify.assertBefore(formerItem, latterItem, actualList, "list");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the formerItem appears before the latterItem in the given {@link Collection}.
     * {@link #assertContains(String, Object, Collection)} will be called for both the formerItem and the
     * latterItem, prior to the "before" assertion.
     */
    public static void assertBefore(
            String listName,
            Object formerItem,
            Object latterItem,
            List<?> actualList)
    {
        try
        {
            Verify.assertObjectNotNull(actualList, listName);
            Verify.assertNotEquals(
                    formerItem,
                    latterItem,
                    "Bad test, formerItem and latterItem are equal, listName:<" + listName + '>');
            Verify.assertContainsAll(actualList, formerItem, latterItem, listName);
            int formerPosition = actualList.indexOf(formerItem);
            int latterPosition = actualList.indexOf(latterItem);
            if (latterPosition < formerPosition)
            {
                fail("Items in "
                        + listName
                        + " are in incorrect order; "
                        + "expected formerItem:<"
                        + formerItem
                        + "> "
                        + "to appear before latterItem:<"
                        + latterItem
                        + ">, but didn't");
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertObjectNotNull(String objectName, Object actualObject)
    {
        try
        {
            assertNotNull(actualObject, objectName + " should not be null");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@code item} is at the {@code index} in the given {@link List}.
     */
    public static void assertItemAtIndex(Object expectedItem, int index, List<?> list)
    {
        try
        {
            Verify.assertItemAtIndex(expectedItem, index, list, "list");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@code item} is at the {@code index} in the given {@code array}.
     */
    public static void assertItemAtIndex(Object expectedItem, int index, Object[] array)
    {
        try
        {
            Verify.assertItemAtIndex(expectedItem, index, array, "array");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static <T> void assertStartsWith(T[] array, T... items)
    {
        try
        {
            Verify.assertNotEmpty(items, "Expected items in assertion");

            for (int i = 0; i < items.length; i++)
            {
                T item = items[i];
                Verify.assertItemAtIndex(item, i, array, "array");
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static <T> void assertStartsWith(List<T> list, T... items)
    {
        try
        {
            Verify.assertStartsWith(list, items, "list");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static <T> void assertStartsWith(String listName, List<T> list, T... items)
    {
        try
        {
            Verify.assertNotEmpty(items, "Expected items in assertion");

            for (int i = 0; i < items.length; i++)
            {
                T item = items[i];
                Verify.assertItemAtIndex(item, i, list, listName);
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static <T> void assertEndsWith(List<T> list, T... items)
    {
        try
        {
            Verify.assertNotEmpty(items, "Expected items in assertion");

            for (int i = 0; i < items.length; i++)
            {
                T item = items[i];
                Verify.assertItemAtIndex(item, list.size() - items.length + i, list, "list");
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static <T> void assertEndsWith(T[] array, T... items)
    {
        try
        {
            Verify.assertNotEmpty(items, "Expected items in assertion");

            for (int i = 0; i < items.length; i++)
            {
                T item = items[i];
                Verify.assertItemAtIndex(item, array.length - items.length + i, array, "array");
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@code item} is at the {@code index} in the given {@link List}.
     */
    public static void assertItemAtIndex(
            String listName,
            Object expectedItem,
            int index,
            List<?> list)
    {
        try
        {
            Verify.assertObjectNotNull(list, listName);

            Object actualItem = list.get(index);
            if (!Comparators.nullSafeEquals(expectedItem, actualItem))
            {
                assertEquals(
                        expectedItem,
                        actualItem,
                        listName + " has incorrect element at index:<" + index + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that the given {@code item} is at the {@code index} in the given {@link List}.
     */
    public static void assertItemAtIndex(
            String arrayName,
            Object expectedItem,
            int index,
            Object[] array)
    {
        try
        {
            assertNotNull(array);
            Object actualItem = array[index];
            if (!Comparators.nullSafeEquals(expectedItem, actualItem))
            {
                assertEquals(
                        expectedItem,
                        actualItem,
                        arrayName + " has incorrect element at index:<" + index + '>');
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertPostSerializedEqualsAndHashCode(Object object)
    {
        try
        {
            Object deserialized = SerializeTestHelper.serializeDeserialize(object);
            Verify.assertEqualsAndHashCode(object, deserialized, "objects");
            assertNotSame(object, deserialized, "not same object");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertPostSerializedIdentity(Object object)
    {
        try
        {
            Object deserialized = SerializeTestHelper.serializeDeserialize(object);
            Verify.assertEqualsAndHashCode(object, deserialized, "objects");
            assertSame(object, deserialized, "same object");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertSerializedForm(String expectedBase64Form, Object actualObject)
    {
        try
        {
            Verify.assertInstanceOf(Serializable.class, actualObject);
            assertEquals(
                    expectedBase64Form,
                    Verify.encodeObject(actualObject),
                    "Serialization was broken.");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertSerializedForm(
            long expectedSerialVersionUID,
            String expectedBase64Form,
            Object actualObject)
    {
        try
        {
            Verify.assertInstanceOf(Serializable.class, actualObject);

            assertEquals(
                    expectedBase64Form,
                    Verify.encodeObject(actualObject),
                    "Serialization was broken.");

            Object decodeToObject = Verify.decodeObject(expectedBase64Form);

            assertEquals(
                    expectedSerialVersionUID,
                    ObjectStreamClass.lookup(decodeToObject.getClass()).getSerialVersionUID(),
                    "serialVersionUID's differ");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertDeserializedForm(String expectedBase64Form, Object actualObject)
    {
        try
        {
            Verify.assertInstanceOf(Serializable.class, actualObject);

            Object decodeToObject = Verify.decodeObject(expectedBase64Form);
            assertEquals(decodeToObject, actualObject, "Serialization was broken.");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    private static Object decodeObject(String expectedBase64Form)
    {
        try
        {
            byte[] bytes = Base64.decodeBase64(expectedBase64Form);
            return new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
        }
        catch (IOException e)
        {
            throw new AssertionError(e);
        }
        catch (ClassNotFoundException e)
        {
            throw new AssertionError(e);
        }
    }

    private static String encodeObject(Object actualObject)
    {
        try
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(actualObject);
            objectOutputStream.flush();
            objectOutputStream.close();

            String string = new Base64(76, LINE_SEPARATOR, false).encodeAsString(byteArrayOutputStream.toByteArray());
            String trimmedString = Verify.removeFinalNewline(string);
            return Verify.addFinalNewline(trimmedString);
        }
        catch (IOException e)
        {
            throw new AssertionError(e);
        }
    }

    private static String removeFinalNewline(String string)
    {
        return string.substring(0, string.length() - 1);
    }

    private static String addFinalNewline(String string)
    {
        if (string.length() % 77 == 76)
        {
            return string + '\n';
        }
        return string;
    }

    /**
     * Assert that {@code objectA} and {@code objectB} are equal (via the {@link Object#equals(Object)} method,
     * and that they both return the same {@link Object#hashCode()}.
     */
    public static void assertEqualsAndHashCode(Object objectA, Object objectB)
    {
        try
        {
            Verify.assertEqualsAndHashCode(objectA, objectB, "objects");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Asserts that a value is negative.
     */
    public static void assertNegative(int value)
    {
        try
        {
            assertTrue(value < 0, value + " is not negative");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Asserts that a value is positive.
     */
    public static void assertPositive(int value)
    {
        try
        {
            assertTrue(value > 0, value + " is not positive");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Asserts that a value is positive.
     */
    public static void assertZero(int value)
    {
        try
        {
            assertEquals(0, value);
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Assert that {@code objectA} and {@code objectB} are equal (via the {@link Object#equals(Object)} method,
     * and that they both return the same {@link Object#hashCode()}.
     */
    public static void assertEqualsAndHashCode(String itemNames, Object objectA, Object objectB)
    {
        try
        {
            if (objectA == null || objectB == null)
            {
                fail("Neither item should be null: <" + objectA + "> <" + objectB + '>');
            }

            assertFalse(objectA.equals(null), "Neither item should equal null");
            assertFalse(objectB.equals(null), "Neither item should equal null");
            Verify.assertNotEquals("Neither item should equal new Object()", objectA.equals(new Object()));
            Verify.assertNotEquals("Neither item should equal new Object()", objectB.equals(new Object()));
            assertEquals(objectA, objectA, "Expected " + itemNames + " to be equal.");
            assertEquals(objectB, objectB, "Expected " + itemNames + " to be equal.");
            assertEquals(objectA, objectB, "Expected " + itemNames + " to be equal.");
            assertEquals(objectB, objectA, "Expected " + itemNames + " to be equal.");
            assertEquals(
                    objectA.hashCode(),
                    objectB.hashCode(),
                    "Expected " + itemNames + " to have the same hashCode().");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertShallowClone(Cloneable object)
    {
        try
        {
            Verify.assertShallowClone(object, "object");
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static void assertShallowClone(String itemName, Cloneable object)
    {
        try
        {
            Method method = Object.class.getDeclaredMethod("clone", (Class<?>[]) null);
            method.setAccessible(true);
            Object clone = method.invoke(object);
            String prefix = itemName + " and its clone";
            assertNotSame(object, clone, prefix);
            Verify.assertEqualsAndHashCode(object, clone, prefix);
        }
        catch (IllegalArgumentException e)
        {
            throw new AssertionError(e.getLocalizedMessage());
        }
        catch (InvocationTargetException e)
        {
            throw new AssertionError(e.getLocalizedMessage());
        }
        catch (SecurityException e)
        {
            throw new AssertionError(e.getLocalizedMessage());
        }
        catch (NoSuchMethodException e)
        {
            throw new AssertionError(e.getLocalizedMessage());
        }
        catch (IllegalAccessException e)
        {
            throw new AssertionError(e.getLocalizedMessage());
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    public static <T> void assertClassNonInstantiable(Class<T> aClass)
    {
        try
        {
            try
            {
                aClass.newInstance();
                fail("Expected class '" + aClass + "' to be non-instantiable");
            }
            catch (InstantiationException e)
            {
                // pass
            }
            catch (IllegalAccessException e)
            {
                if (Verify.canInstantiateThroughReflection(aClass))
                {
                    fail("Expected constructor of non-instantiable class '" + aClass + "' to throw an exception, but didn't");
                }
            }
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    private static <T> boolean canInstantiateThroughReflection(Class<T> aClass)
    {
        try
        {
            Constructor<T> declaredConstructor = aClass.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            declaredConstructor.newInstance();
            return true;
        }
        catch (NoSuchMethodException e)
        {
            return false;
        }
        catch (InvocationTargetException e)
        {
            return false;
        }
        catch (InstantiationException e)
        {
            return false;
        }
        catch (IllegalAccessException e)
        {
            return false;
        }
        catch (AssertionError e)
        {
            return false;
        }
    }

    public static void assertError(Class<? extends Error> expectedErrorClass, Runnable code)
    {
        try
        {
            code.run();
        }
        catch (Error ex)
        {
            try
            {
                assertSame(
                        expectedErrorClass,
                        ex.getClass(),
                        "Caught error of type <"
                                + ex.getClass().getName()
                                + ">, expected one of type <"
                                + expectedErrorClass.getName()
                                + '>');
                return;
            }
            catch (AssertionError e)
            {
                Verify.throwMangledException(e);
            }
        }

        try
        {
            fail("Block did not throw an error of type " + expectedErrorClass.getName());
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Runs the {@link Callable} {@code code} and asserts that it throws an {@code Exception} of the type
     * {@code expectedExceptionClass}.
     * <p>
     * {@code Callable} is most appropriate when a checked exception will be thrown.
     * If a subclass of {@link RuntimeException} will be thrown, the form
     * {@link #assertThrows(Class, Runnable)} may be more convenient.
     * <p>
     * e.g.
     * <pre>
     * Verify.<b>assertThrows</b>(StringIndexOutOfBoundsException.class, new Callable&lt;String&gt;()
     * {
     *    public String call() throws Exception
     *    {
     *        return "Craig".substring(42, 3);
     *    }
     * });
     * </pre>
     *
     * @see #assertThrows(Class, Runnable)
     */
    public static void assertThrows(
            Class<? extends Exception> expectedExceptionClass,
            Callable<?> code)
    {
        try
        {
            code.call();
        }
        catch (Exception ex)
        {
            try
            {
                assertSame(
                        expectedExceptionClass,
                        ex.getClass(),
                        "Caught exception of type <"
                                + ex.getClass().getName()
                                + ">, expected one of type <"
                                + expectedExceptionClass.getName()
                                + '>'
                                + '\n'
                                + "Exception Message: " + ex.getMessage()
                                + '\n');
                return;
            }
            catch (AssertionError e)
            {
                Verify.throwMangledException(e);
            }
        }

        try
        {
            fail("Block did not throw an exception of type " + expectedExceptionClass.getName());
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Runs the {@link Runnable} {@code code} and asserts that it throws an {@code Exception} of the type
     * {@code expectedExceptionClass}.
     * <p>
     * {@code Runnable} is most appropriate when a subclass of {@link RuntimeException} will be thrown.
     * If a checked exception will be thrown, the form {@link #assertThrows(Class, Callable)} may be more
     * convenient.
     * <p>
     * e.g.
     * <pre>
     * Verify.<b>assertThrows</b>(NullPointerException.class, new Runnable()
     * {
     *    public void run()
     *    {
     *        final Integer integer = null;
     *        LOGGER.info(integer.toString());
     *    }
     * });
     * </pre>
     *
     * @see #assertThrows(Class, Callable)
     */
    public static void assertThrows(
            Class<? extends Exception> expectedExceptionClass,
            Runnable code)
    {
        try
        {
            code.run();
        }
        catch (RuntimeException ex)
        {
            try
            {
                assertSame(
                        expectedExceptionClass,
                        ex.getClass(),
                        "Caught exception of type <"
                                + ex.getClass().getName()
                                + ">, expected one of type <"
                                + expectedExceptionClass.getName()
                                + '>'
                                + '\n'
                                + "Exception Message: " + ex.getMessage()
                                + '\n');
                return;
            }
            catch (AssertionError e)
            {
                Verify.throwMangledException(e);
            }
        }

        try
        {
            fail("Block did not throw an exception of type " + expectedExceptionClass.getName());
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Runs the {@link Callable} {@code code} and asserts that it throws an {@code Exception} of the type
     * {@code expectedExceptionClass}, which contains a cause of type expectedCauseClass.
     * <p>
     * {@code Callable} is most appropriate when a checked exception will be thrown.
     * If a subclass of {@link RuntimeException} will be thrown, the form
     * {@link #assertThrowsWithCause(Class, Class, Runnable)} may be more convenient.
     * <p>
     * e.g.
     * <pre>
     * Verify.assertThrowsWithCause(RuntimeException.class, IOException.class, new Callable<Void>()
     * {
     *    public Void call() throws Exception
     *    {
     *        try
     *        {
     *            new File("").createNewFile();
     *        }
     *        catch (final IOException e)
     *        {
     *            throw new RuntimeException("Uh oh!", e);
     *        }
     *        return null;
     *    }
     * });
     * </pre>
     *
     * @see #assertThrowsWithCause(Class, Class, Runnable)
     */
    public static void assertThrowsWithCause(
            Class<? extends Exception> expectedExceptionClass,
            Class<? extends Throwable> expectedCauseClass,
            Callable<?> code)
    {
        try
        {
            code.call();
        }
        catch (Exception ex)
        {
            try
            {
                assertSame(
                        expectedExceptionClass,
                        ex.getClass(),
                        "Caught exception of type <"
                                + ex.getClass().getName()
                                + ">, expected one of type <"
                                + expectedExceptionClass.getName()
                                + '>');
                Throwable actualCauseClass = ex.getCause();
                assertNotNull(
                        actualCauseClass,
                        "Caught exception with null cause, expected cause of type <"
                                + expectedCauseClass.getName()
                                + '>');
                assertSame(
                        expectedCauseClass,
                        actualCauseClass.getClass(),
                        "Caught exception with cause of type<"
                                + actualCauseClass.getClass().getName()
                                + ">, expected cause of type <"
                                + expectedCauseClass.getName()
                                + '>');
                return;
            }
            catch (AssertionError e)
            {
                Verify.throwMangledException(e);
            }
        }

        try
        {
            fail("Block did not throw an exception of type " + expectedExceptionClass.getName());
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }

    /**
     * Runs the {@link Runnable} {@code code} and asserts that it throws an {@code Exception} of the type
     * {@code expectedExceptionClass}, which contains a cause of type expectedCauseClass.
     * <p>
     * {@code Runnable} is most appropriate when a subclass of {@link RuntimeException} will be thrown.
     * If a checked exception will be thrown, the form {@link #assertThrowsWithCause(Class, Class, Callable)}
     * may be more convenient.
     * <p>
     * e.g.
     * <pre>
     * Verify.assertThrowsWithCause(RuntimeException.class, StringIndexOutOfBoundsException.class, new Runnable()
     * {
     *    public void run()
     *    {
     *        try
     *        {
     *            LOGGER.info("Craig".substring(42, 3));
     *        }
     *        catch (final StringIndexOutOfBoundsException e)
     *        {
     *            throw new RuntimeException("Uh oh!", e);
     *        }
     *    }
     * });
     * </pre>
     *
     * @see #assertThrowsWithCause(Class, Class, Callable)
     */
    public static void assertThrowsWithCause(
            Class<? extends Exception> expectedExceptionClass,
            Class<? extends Throwable> expectedCauseClass,
            Runnable code)
    {
        try
        {
            code.run();
        }
        catch (RuntimeException ex)
        {
            try
            {
                assertSame(
                        expectedExceptionClass,
                        ex.getClass(),
                        "Caught exception of type <"
                                + ex.getClass().getName()
                                + ">, expected one of type <"
                                + expectedExceptionClass.getName()
                                + '>');
                Throwable actualCauseClass = ex.getCause();
                assertNotNull(
                        actualCauseClass,
                        "Caught exception with null cause, expected cause of type <"
                                + expectedCauseClass.getName()
                                + '>');
                assertSame(
                        expectedCauseClass,
                        actualCauseClass.getClass(),
                        "Caught exception with cause of type<"
                                + actualCauseClass.getClass().getName()
                                + ">, expected cause of type <"
                                + expectedCauseClass.getName()
                                + '>');
                return;
            }
            catch (AssertionError e)
            {
                Verify.throwMangledException(e);
            }
        }

        try
        {
            fail("Block did not throw an exception of type " + expectedExceptionClass.getName());
        }
        catch (AssertionError e)
        {
            Verify.throwMangledException(e);
        }
    }
}
