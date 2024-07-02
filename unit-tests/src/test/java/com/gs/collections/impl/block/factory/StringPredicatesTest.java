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

package com.gs.collections.impl.block.factory;

import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StringPredicatesTest
{
    @Test
    public void startsWith()
    {
        Assertions.assertFalse(StringPredicates.startsWith("Hello").accept(null));
        Assertions.assertTrue(StringPredicates.startsWith("Hello").accept("HelloWorld"));
        Assertions.assertFalse(StringPredicates.startsWith("World").accept("HelloWorld"));
        Assertions.assertEquals("StringPredicates.startsWith(\"Hello\")", StringPredicates.startsWith("Hello").toString());
    }

    @Test
    public void endsWith()
    {
        Assertions.assertFalse(StringPredicates.endsWith("Hello").accept(null));
        Assertions.assertFalse(StringPredicates.endsWith("Hello").accept("HelloWorld"));
        Assertions.assertTrue(StringPredicates.endsWith("World").accept("HelloWorld"));
        Assertions.assertEquals("StringPredicates.endsWith(\"Hello\")", StringPredicates.endsWith("Hello").toString());
    }

    @Test
    public void equalsIgnoreCase()
    {
        Assertions.assertFalse(StringPredicates.equalsIgnoreCase("HELLO").accept(null));
        Assertions.assertTrue(StringPredicates.equalsIgnoreCase("HELLO").accept("hello"));
        Assertions.assertTrue(StringPredicates.equalsIgnoreCase("world").accept("WORLD"));
        Assertions.assertFalse(StringPredicates.equalsIgnoreCase("Hello").accept("World"));
        Assertions.assertEquals("StringPredicates.equalsIgnoreCase(\"Hello\")", StringPredicates.equalsIgnoreCase("Hello").toString());
    }

    @Test
    public void containsString()
    {
        Assertions.assertTrue(StringPredicates.contains("Hello").accept("WorldHelloWorld"));
        Assertions.assertTrue(StringPredicates.contains("Hello").and(StringPredicates.contains("World")).accept("WorldHelloWorld"));
        Assertions.assertFalse(StringPredicates.contains("Goodbye").accept("WorldHelloWorld"));
        Assertions.assertEquals("StringPredicates.contains(\"Hello\")", StringPredicates.contains("Hello").toString());
    }

    @Test
    public void containsCharacter()
    {
        Assertions.assertTrue(StringPredicates.contains("H".charAt(0)).accept("WorldHelloWorld"));
        Assertions.assertFalse(StringPredicates.contains("B".charAt(0)).accept("WorldHelloWorld"));
        Assertions.assertEquals("StringPredicates.contains(\"H\")", StringPredicates.contains("H".charAt(0)).toString());
    }

    @Test
    public void emptyAndNotEmpty()
    {
        Assertions.assertFalse(StringPredicates.empty().accept("WorldHelloWorld"));
        Assertions.assertEquals("StringPredicates.empty()", StringPredicates.empty().toString());
        Assertions.assertTrue(StringPredicates.notEmpty().accept("WorldHelloWorld"));
        Assertions.assertEquals("StringPredicates.notEmpty()", StringPredicates.notEmpty().toString());
        Assertions.assertTrue(StringPredicates.empty().accept(""));
        Assertions.assertFalse(StringPredicates.notEmpty().accept(""));
    }

    @Test
    public void lessThan()
    {
        Assertions.assertTrue(StringPredicates.lessThan("b").accept("a"));
        Assertions.assertFalse(StringPredicates.lessThan("b").accept("b"));
        Assertions.assertFalse(StringPredicates.lessThan("b").accept("c"));
        Assertions.assertEquals("StringPredicates.lessThan(\"b\")", StringPredicates.lessThan("b").toString());
    }

    @Test
    public void lessThanOrEqualTo()
    {
        Assertions.assertTrue(StringPredicates.lessThanOrEqualTo("b").accept("a"));
        Assertions.assertTrue(StringPredicates.lessThanOrEqualTo("b").accept("b"));
        Assertions.assertFalse(StringPredicates.lessThanOrEqualTo("b").accept("c"));
        Assertions.assertEquals("StringPredicates.lessThanOrEqualTo(\"b\")", StringPredicates.lessThanOrEqualTo("b").toString());
    }

    @Test
    public void greaterThan()
    {
        Assertions.assertFalse(StringPredicates.greaterThan("b").accept("a"));
        Assertions.assertFalse(StringPredicates.greaterThan("b").accept("b"));
        Assertions.assertTrue(StringPredicates.greaterThan("b").accept("c"));
        Assertions.assertEquals("StringPredicates.greaterThan(\"b\")", StringPredicates.greaterThan("b").toString());
    }

    @Test
    public void greaterThanOrEqualTo()
    {
        Assertions.assertFalse(StringPredicates.greaterThanOrEqualTo("b").accept("a"));
        Assertions.assertTrue(StringPredicates.greaterThanOrEqualTo("b").accept("b"));
        Assertions.assertTrue(StringPredicates.greaterThanOrEqualTo("b").accept("c"));
        Assertions.assertEquals("StringPredicates.greaterThanOrEqualTo(\"b\")", StringPredicates.greaterThanOrEqualTo("b").toString());
    }

    @Test
    public void matches()
    {
        Assertions.assertTrue(StringPredicates.matches("a*b*").accept("aaaaabbbbb"));
        Assertions.assertFalse(StringPredicates.matches("a*b").accept("ba"));
        Assertions.assertEquals("StringPredicates.matches(\"a*b\")", StringPredicates.matches("a*b").toString());
    }

    @Test
    public void size()
    {
        Assertions.assertTrue(StringPredicates.size(1).accept("a"));
        Assertions.assertFalse(StringPredicates.size(0).accept("a"));
        Assertions.assertTrue(StringPredicates.size(2).accept("ab"));
        Assertions.assertEquals("StringPredicates.size(2)", StringPredicates.size(2).toString());
    }

    @Test
    public void hasLetters()
    {
        Assertions.assertTrue(StringPredicates.hasLetters().accept("a2a"));
        Assertions.assertFalse(StringPredicates.hasLetters().accept("222"));
        Assertions.assertEquals("StringPredicates.hasLetters()", StringPredicates.hasLetters().toString());
    }

    @Test
    public void hasDigits()
    {
        Assertions.assertFalse(StringPredicates.hasDigits().accept("aaa"));
        Assertions.assertTrue(StringPredicates.hasDigits().accept("a22"));
        Assertions.assertEquals("StringPredicates.hasDigits()", StringPredicates.hasDigits().toString());
    }

    @Test
    public void hasLettersAndDigits()
    {
        Predicate<String> predicate = StringPredicates.hasLettersAndDigits();
        Assertions.assertTrue(predicate.accept("a2a"));
        Assertions.assertFalse(predicate.accept("aaa"));
        Assertions.assertFalse(predicate.accept("222"));
        Assertions.assertEquals("StringPredicates.hasLettersAndDigits()", predicate.toString());
    }

    @Test
    public void hasLettersOrDigits()
    {
        Predicate<String> predicate = StringPredicates.hasLettersOrDigits();
        Assertions.assertTrue(predicate.accept("a2a"));
        Assertions.assertTrue(predicate.accept("aaa"));
        Assertions.assertTrue(predicate.accept("222"));
        Assertions.assertEquals("StringPredicates.hasLettersOrDigits()", predicate.toString());
    }

    @Test
    public void isAlpha()
    {
        Predicate<String> predicate = StringPredicates.isAlpha();
        Assertions.assertTrue(predicate.accept("aaa"));
        Assertions.assertFalse(predicate.accept("a2a"));
        Assertions.assertEquals("StringPredicates.isAlpha()", predicate.toString());
    }

    @Test
    public void isAlphaNumeric()
    {
        Predicate<String> predicate = StringPredicates.isAlphanumeric();
        Assertions.assertTrue(predicate.accept("aaa"));
        Assertions.assertTrue(predicate.accept("a2a"));
        Assertions.assertEquals("StringPredicates.isAlphanumeric()", predicate.toString());
    }

    @Test
    public void isBlank()
    {
        Predicate<String> predicate = StringPredicates.isBlank();
        Assertions.assertTrue(predicate.accept(""));
        Assertions.assertTrue(predicate.accept(" "));
        Assertions.assertFalse(predicate.accept("a2a"));
        Assertions.assertEquals("StringPredicates.isBlank()", predicate.toString());
    }

    @Test
    public void notBlank()
    {
        Predicate<String> predicate = StringPredicates.notBlank();
        Assertions.assertFalse(predicate.accept(""));
        Assertions.assertFalse(predicate.accept(" "));
        Assertions.assertTrue(predicate.accept("a2a"));
        Assertions.assertEquals("StringPredicates.notBlank()", predicate.toString());
    }

    @Test
    public void isNumeric()
    {
        Predicate<String> predicate = StringPredicates.isNumeric();
        Assertions.assertTrue(predicate.accept("222"));
        Assertions.assertFalse(predicate.accept("a2a2a2"));
        Assertions.assertFalse(predicate.accept("aaa"));
        Assertions.assertEquals("StringPredicates.isNumeric()", predicate.toString());
    }

    @Test
    public void hasLowerCase()
    {
        Predicate<String> predicate = StringPredicates.hasLowerCase();
        Assertions.assertTrue(predicate.accept("aaa"));
        Assertions.assertFalse(predicate.accept("AAA"));
        Assertions.assertEquals("StringPredicates.hasLowerCase()", predicate.toString());
    }

    @Test
    public void hasUpperCase()
    {
        Predicate<String> predicate = StringPredicates.hasUpperCase();
        Assertions.assertFalse(predicate.accept("aaa"));
        Assertions.assertTrue(predicate.accept("AAA"));
        Assertions.assertEquals("StringPredicates.hasUpperCase()", predicate.toString());
    }

    @Test
    public void hasUndefined()
    {
        Predicate<String> predicate = StringPredicates.hasUndefined();
        Assertions.assertFalse(predicate.accept("aaa"));
        Assertions.assertEquals("StringPredicates.hasUndefined()", predicate.toString());
    }

    @Test
    public void hasSpaces()
    {
        Predicate<String> predicate = StringPredicates.hasSpaces();
        Assertions.assertTrue(predicate.accept("a a a"));
        Assertions.assertTrue(predicate.accept(" "));
        Assertions.assertFalse(predicate.accept("aaa"));
        Assertions.assertEquals("StringPredicates.hasSpaces()", predicate.toString());
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(StringPredicates.class);
    }
}
