/*
 * Copyright 2011 Goldman Sachs.
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

import java.util.concurrent.Callable;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * JUnit test to make sure that methods like {@link Verify#assertThrows(Class, Runnable)} really throw when
 * they ought to.
 */
public class ExceptionThrownTest
{
    @Test
    public void specificRuntimeException()
    {
        try
        {
            Verify.assertThrows(NullPointerException.class, new EmptyRunnable());
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), ExceptionThrownTest.class.getName());
        }
    }

    @Test
    public void callableException()
    {
        try
        {
            Verify.assertThrows(NullPointerException.class, new EmptyCallable());
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), ExceptionThrownTest.class.getName());
        }
    }

    @Test
    public void nullCause()
    {
        try
        {
            Verify.assertThrowsWithCause(
                    IllegalStateException.class,
                    IllegalArgumentException.class,
                    new Runnable()
                    {
                        public void run()
                        {
                            throw new IllegalStateException();
                        }
                    });
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), ExceptionThrownTest.class.getName());
        }
    }

    private static final class EmptyRunnable implements Runnable
    {
        public void run()
        {
        }
    }

    private static final class EmptyCallable implements Callable<Void>
    {
        public Void call()
        {
            return null;
        }
    }
}
