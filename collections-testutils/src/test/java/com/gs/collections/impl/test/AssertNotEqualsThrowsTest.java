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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AssertNotEqualsThrowsTest
{
    private static final Object OBJECT = new Object();
    private static final String STRING = "1";
    private static final double DOUBLE = 1.0d;
    private static final double DOUBLE_DELTA = 0.5d;
    private static final float FLOAT = 1.0f;
    private static final float FLOAT_DELTA = 0.5f;
    private static final long LONG = 1L;
    private static final boolean BOOLEAN = true;
    private static final byte BYTE = 1;
    private static final char CHAR = '1';
    private static final short SHORT = 1;
    private static final int INT = 1;

    @Test
    public void objectObject()
    {
        try
        {
            Verify.assertNotEquals(OBJECT, OBJECT);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), AssertNotEqualsThrowsTest.class.getName());
        }
    }

    @Test
    public void stringObjectObject()
    {
        try
        {
            Verify.assertNotEquals(OBJECT, OBJECT, "items");
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), AssertNotEqualsThrowsTest.class.getName());
        }
    }

    @Test
    public void stringString()
    {
        try
        {
            Verify.assertNotEquals(STRING, STRING);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), AssertNotEqualsThrowsTest.class.getName());
        }
        try
        {
            Verify.assertNotEquals(null, null);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), AssertNotEqualsThrowsTest.class.getName());
        }
    }

    @Test
    public void stringStringString()
    {
        try
        {
            Verify.assertNotEquals(STRING, STRING, "strings");
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), AssertNotEqualsThrowsTest.class.getName());
        }
    }

    @Test
    public void doubleDoubleDouble()
    {
        try
        {
            Verify.assertNotEquals(DOUBLE, DOUBLE, DOUBLE_DELTA);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), AssertNotEqualsThrowsTest.class.getName());
        }

        try
        {
            Verify.assertNotEquals(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, DOUBLE_DELTA);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), AssertNotEqualsThrowsTest.class.getName());
        }
    }

    @Test
    public void stringDoubleDoubleDouble()
    {
        try
        {
            Verify.assertNotEquals(DOUBLE, DOUBLE, DOUBLE_DELTA, "doubles");
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), AssertNotEqualsThrowsTest.class.getName());
        }
    }

    @Test
    public void floatFloatFloat()
    {
        try
        {
            Verify.assertNotEquals(FLOAT, FLOAT, FLOAT_DELTA);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), AssertNotEqualsThrowsTest.class.getName());
        }

        try
        {
            Verify.assertNotEquals(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, FLOAT_DELTA);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), AssertNotEqualsThrowsTest.class.getName());
        }
    }

    @Test
    public void stringFloatFloatFloat()
    {
        try
        {
            Verify.assertNotEquals(FLOAT, FLOAT, FLOAT_DELTA, "floats");
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), AssertNotEqualsThrowsTest.class.getName());
        }
    }

    @Test
    public void longLong()
    {
        try
        {
            Verify.assertNotEquals(LONG, LONG);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), AssertNotEqualsThrowsTest.class.getName());
        }
    }

    @Test
    public void stringLongLong()
    {
        try
        {
            Verify.assertNotEquals(LONG, LONG, "longs");
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), AssertNotEqualsThrowsTest.class.getName());
        }
    }

    @Test
    public void booleanBoolean()
    {
        try
        {
            Verify.assertNotEquals(BOOLEAN, BOOLEAN);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), AssertNotEqualsThrowsTest.class.getName());
        }
    }

    @Test
    public void stringBooleanBoolean()
    {
        try
        {
            Verify.assertNotEquals(BOOLEAN, BOOLEAN, "booleans");
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), AssertNotEqualsThrowsTest.class.getName());
        }
    }

    @Test
    public void byteByte()
    {
        try
        {
            Verify.assertNotEquals(BYTE, BYTE);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), AssertNotEqualsThrowsTest.class.getName());
        }
    }

    @Test
    public void stringByteByte()
    {
        try
        {
            Verify.assertNotEquals(BYTE, BYTE, "bytes");
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), AssertNotEqualsThrowsTest.class.getName());
        }
    }

    @Test
    public void charChar()
    {
        try
        {
            Verify.assertNotEquals(CHAR, CHAR);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), AssertNotEqualsThrowsTest.class.getName());
        }
    }

    @Test
    public void stringCharChar()
    {
        try
        {
            Verify.assertNotEquals(CHAR, CHAR, "chars");
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), AssertNotEqualsThrowsTest.class.getName());
        }
    }

    @Test
    public void shortShort()
    {
        try
        {
            Verify.assertNotEquals(SHORT, SHORT);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), AssertNotEqualsThrowsTest.class.getName());
        }
    }

    @Test
    public void stringShortShort()
    {
        try
        {
            Verify.assertNotEquals(SHORT, SHORT, "shorts");
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), AssertNotEqualsThrowsTest.class.getName());
        }
    }

    @Test
    public void intInt()
    {
        try
        {
            Verify.assertNotEquals(INT, INT);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), AssertNotEqualsThrowsTest.class.getName());
        }
    }

    @Test
    public void stringIntInt()
    {
        try
        {
            Verify.assertNotEquals(INT, INT, "ints");
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), AssertNotEqualsThrowsTest.class.getName());
        }
    }
}
