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

package com.gs.collections.impl.block.function;

import com.gs.collections.api.block.function.Function;
import com.gs.collections.impl.block.factory.StringFunctions;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class StringFunctionsTest
{
    @Test
    public void toUpperCase()
    {
        Function<String, String> function = StringFunctions.toUpperCase();
        Assertions.assertEquals("UPPER", function.valueOf("upper"));
        Assertions.assertEquals("UPPER", function.valueOf("Upper"));
        Assertions.assertEquals("UPPER", function.valueOf("UPPER"));
        Assertions.assertSame("UPPER", function.valueOf("UPPER"));
    }

    @Test
    public void toLowerCase()
    {
        Function<String, String> function = StringFunctions.toLowerCase();
        Assertions.assertEquals("lower", function.valueOf("LOWER"));
        Assertions.assertEquals("lower", function.valueOf("Lower"));
        Assertions.assertEquals("lower", function.valueOf("lower"));
        Assertions.assertSame("lower", function.valueOf("lower"));
    }

    @Test
    public void toInteger()
    {
        Assertions.assertEquals(-42L, StringFunctions.toInteger().valueOf("-42").longValue());
        Verify.assertInstanceOf(Integer.class, StringFunctions.toInteger().valueOf("10"));
    }

    @Test
    public void length()
    {
        Function<String, Integer> function = StringFunctions.length();
        Assertions.assertEquals(Integer.valueOf(6), function.valueOf("string"));
        Assertions.assertEquals(Integer.valueOf(0), function.valueOf(""));
        Assertions.assertEquals("string.length()", function.toString());
    }

    @Test
    public void trim()
    {
        Function<String, String> function = StringFunctions.trim();
        Assertions.assertEquals("trim", function.valueOf("trim "));
        Assertions.assertEquals("trim", function.valueOf(" trim"));
        Assertions.assertEquals("trim", function.valueOf("  trim  "));
        Assertions.assertEquals("trim", function.valueOf("trim"));
        Assertions.assertSame("trim", function.valueOf("trim"));
        Assertions.assertEquals("string.trim()", function.toString());
    }

    @Test
    public void firstLetter()
    {
        Function<String, Character> function = StringFunctions.firstLetter();
        Assertions.assertNull(function.valueOf(null));
        Assertions.assertNull(function.valueOf(""));
        Assertions.assertEquals('A', function.valueOf("Autocthonic").charValue());
    }

    @Test
    public void subString()
    {
        Function<String, String> function1 = StringFunctions.subString(2, 5);
        String testString = "habits";
        Assertions.assertEquals("bit", function1.valueOf(testString));
        Verify.assertContains(function1.toString(), "string.subString");

        Function<String, String> function2 = StringFunctions.subString(0, testString.length());
        Assertions.assertEquals(testString, function2.valueOf(testString));

        Function<String, String> function3 = StringFunctions.subString(0, testString.length() + 1);
        Verify.assertThrows(StringIndexOutOfBoundsException.class, () -> function3.valueOf(testString));

        Function<String, String> function4 = StringFunctions.subString(-1, 1);
        Verify.assertThrows(StringIndexOutOfBoundsException.class, () -> function4.valueOf(testString));
    }

    @Test
    public void subString_throws_on_short_string()
    {
        assertThrows(StringIndexOutOfBoundsException.class, () -> {
            StringFunctions.subString(2, 4).valueOf("hi");
        });
    }

    @Test
    public void subString_throws_on_null()
    {
        assertThrows(NullPointerException.class, () -> {
            StringFunctions.subString(2, 4).valueOf(null);
        });
    }

    @Test
    public void toPrimitiveBoolean()
    {
        Assertions.assertTrue(StringFunctions.toPrimitiveBoolean().booleanValueOf("true"));
        Assertions.assertFalse(StringFunctions.toPrimitiveBoolean().booleanValueOf("nah"));
    }

    @Test
    public void toPrimitiveByte()
    {
        Assertions.assertEquals((byte) 16, StringFunctions.toPrimitiveByte().byteValueOf("16"));
    }

    @Test
    public void toFirstChar()
    {
        Assertions.assertEquals('X', StringFunctions.toFirstChar().charValueOf("X-ray"));
    }

    @Test
    public void toPrimitiveChar()
    {
        Assertions.assertEquals('A', StringFunctions.toPrimitiveChar().charValueOf("65"));
    }

    @Test
    public void toPrimitiveCharWithEmptyString()
    {
        assertThrows(StringIndexOutOfBoundsException.class, () -> {
            StringFunctions.toFirstChar().charValueOf("");
        });
    }

    @Test
    public void toPrimitiveDouble()
    {
        Assertions.assertEquals(3.14159265359d, StringFunctions.toPrimitiveDouble().doubleValueOf("3.14159265359"), 0.0);
    }

    @Test
    public void toPrimitiveFloat()
    {
        Assertions.assertEquals(3.1415d, StringFunctions.toPrimitiveFloat().floatValueOf("3.1415"), 0.00001);
    }

    @Test
    public void toPrimitiveInt()
    {
        Assertions.assertEquals(256, StringFunctions.toPrimitiveInt().intValueOf("256"));
    }

    @Test
    public void toPrimitiveLong()
    {
        Assertions.assertEquals(0x7fffffffffffffffL, StringFunctions.toPrimitiveLong().longValueOf("9223372036854775807"));
    }

    @Test
    public void toPrimitiveShort()
    {
        Assertions.assertEquals(-32768, StringFunctions.toPrimitiveShort().shortValueOf("-32768"));
    }

    @Test
    public void append()
    {
        Verify.assertContainsAll(FastList.newListWith("1", "2", "3", "4", "5").collect(StringFunctions.append("!")), "1!", "2!", "3!", "4!", "5!");
    }

    @Test
    public void prepend()
    {
        Verify.assertContainsAll(FastList.newListWith("1", "2", "3", "4", "5").collect(StringFunctions.prepend("@")), "@1", "@2", "@3", "@4", "@5");
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(StringFunctions.class);
    }
}
