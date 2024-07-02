/*
 * Copyright 2014 Goldman Sachs.
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

import java.io.IOException;

import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.utility.ListIterate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Predicates2Test
{
    private static final Predicates2<Object, Object> TRUE = Predicates2.alwaysTrue();
    private static final Predicates2<Object, Object> FALSE = Predicates2.alwaysFalse();
    private static final Object OBJECT = new Object();

    @Test
    public void throwing()
    {
        Verify.assertThrowsWithCause(
                RuntimeException.class,
                IOException.class,
                () -> Predicates2.throwing((a, b) -> { throw new IOException(); }).accept(null, null));
    }

    @Test
    public void staticOr()
    {
        Assertions.assertTrue(Predicates2.or(TRUE, FALSE).accept(OBJECT, OBJECT));
        Assertions.assertFalse(Predicates2.or(FALSE, FALSE).accept(OBJECT, OBJECT));
        Assertions.assertTrue(Predicates2.or(TRUE, TRUE).accept(OBJECT, OBJECT));
        Assertions.assertNotNull(Predicates2.or(TRUE, TRUE).toString());
    }

    @Test
    public void instanceOr()
    {
        Assertions.assertTrue(TRUE.or(FALSE).accept(OBJECT, OBJECT));
        Assertions.assertFalse(FALSE.or(FALSE).accept(OBJECT, OBJECT));
        Assertions.assertTrue(TRUE.or(TRUE).accept(OBJECT, OBJECT));
        Assertions.assertNotNull(TRUE.or(TRUE).toString());
    }

    @Test
    public void staticAnd()
    {
        Assertions.assertTrue(Predicates2.and(TRUE, TRUE).accept(OBJECT, OBJECT));
        Assertions.assertFalse(Predicates2.and(TRUE, FALSE).accept(OBJECT, OBJECT));
        Assertions.assertFalse(Predicates2.and(FALSE, FALSE).accept(OBJECT, OBJECT));
        Assertions.assertNotNull(Predicates2.and(FALSE, FALSE).toString());
    }

    @Test
    public void instanceAnd()
    {
        Assertions.assertTrue(TRUE.and(TRUE).accept(OBJECT, OBJECT));
        Assertions.assertFalse(TRUE.and(FALSE).accept(OBJECT, OBJECT));
        Assertions.assertFalse(FALSE.and(FALSE).accept(OBJECT, OBJECT));
        Assertions.assertNotNull(FALSE.and(FALSE).toString());
    }

    @Test
    public void equal()
    {
        Assertions.assertTrue(Predicates2.equal().accept(1, 1));
        Assertions.assertFalse(Predicates2.equal().accept(2, 1));
        Assertions.assertFalse(Predicates2.equal().accept(null, 1));
        Assertions.assertNotNull(Predicates2.equal().toString());
    }

    @Test
    public void notEqual()
    {
        Assertions.assertFalse(Predicates2.notEqual().accept(1, 1));
        Assertions.assertTrue(Predicates2.notEqual().accept(2, 1));
        Assertions.assertTrue(Predicates2.notEqual().accept(1, 2));
        Assertions.assertTrue(Predicates2.notEqual().accept(null, 1));
        Assertions.assertTrue(Predicates2.notEqual().accept(1, null));
        Assertions.assertFalse(Predicates2.notEqual().accept(null, null));
        Assertions.assertNotNull(Predicates2.notEqual().toString());
    }

    @Test
    public void not()
    {
        Assertions.assertFalse(Predicates2.not(TRUE).accept(OBJECT, OBJECT));
        Assertions.assertTrue(Predicates2.not(FALSE).accept(OBJECT, OBJECT));
        Assertions.assertNotNull(Predicates2.not(FALSE).toString());
    }

    @Test
    public void testNull()
    {
        Assertions.assertFalse(Predicates2.isNull().accept(OBJECT, null));
        Assertions.assertTrue(Predicates2.isNull().accept(null, null));
        Assertions.assertNotNull(Predicates2.isNull().toString());
    }

    @Test
    public void notNull()
    {
        Assertions.assertTrue(Predicates2.notNull().accept(OBJECT, null));
        Assertions.assertFalse(Predicates2.notNull().accept(null, null));
        Assertions.assertNotNull(Predicates2.notNull().toString());
    }

    @Test
    public void sameAs()
    {
        Assertions.assertTrue(Predicates2.sameAs().accept(OBJECT, OBJECT));
        Assertions.assertFalse(Predicates2.sameAs().accept(OBJECT, new Object()));
        Assertions.assertNotNull(Predicates2.sameAs().toString());
    }

    @Test
    public void notSameAs()
    {
        Assertions.assertFalse(Predicates2.notSameAs().accept(OBJECT, OBJECT));
        Assertions.assertTrue(Predicates2.notSameAs().accept(OBJECT, new Object()));
        Assertions.assertNotNull(Predicates2.notSameAs().toString());
    }

    @Test
    public void instanceOf()
    {
        Assertions.assertTrue(Predicates2.instanceOf().accept(1, Integer.class));
        Assertions.assertFalse(Predicates2.instanceOf().accept(1.0, Integer.class));
        Assertions.assertNotNull(Predicates2.instanceOf().toString());
    }

    @Test
    public void notInstanceOf()
    {
        Assertions.assertFalse(Predicates2.notInstanceOf().accept(1, Integer.class));
        Assertions.assertTrue(Predicates2.notInstanceOf().accept(1.0, Integer.class));
        Assertions.assertNotNull(Predicates2.notInstanceOf().toString());
    }

    @Test
    public void attributeEqual()
    {
        Integer one = 1;
        Assertions.assertTrue(Predicates2.attributeEqual(Functions.getToString()).accept(one, "1"));
        Assertions.assertFalse(Predicates2.attributeEqual(Functions.getToString()).accept(one, "2"));
        Assertions.assertNotNull(Predicates2.attributeEqual(Functions.getToString()).toString());
    }

    @Test
    public void attributeNotEqual()
    {
        Integer one = 1;
        Assertions.assertFalse(Predicates2.attributeNotEqual(Functions.getToString()).accept(one, "1"));
        Assertions.assertTrue(Predicates2.attributeNotEqual(Functions.getToString()).accept(one, "2"));
        Assertions.assertNotNull(Predicates2.attributeNotEqual(Functions.getToString()).toString());
    }

    @Test
    public void attributeLessThan()
    {
        Integer one = 1;
        Assertions.assertFalse(Predicates2.attributeLessThan(Functions.getToString()).accept(one, "1"));
        Assertions.assertTrue(Predicates2.attributeLessThan(Functions.getToString()).accept(one, "2"));
        Assertions.assertNotNull(Predicates2.attributeLessThan(Functions.getToString()).toString());
    }

    @Test
    public void attributeGreaterThan()
    {
        Integer one = 1;
        Assertions.assertTrue(Predicates2.attributeGreaterThan(Functions.getToString()).accept(one, "0"));
        Assertions.assertFalse(Predicates2.attributeGreaterThan(Functions.getToString()).accept(one, "1"));
        Assertions.assertNotNull(Predicates2.attributeGreaterThan(Functions.getToString()).toString());
    }

    @Test
    public void attributeGreaterThanOrEqualTo()
    {
        Integer one = 1;
        Assertions.assertTrue(Predicates2.attributeGreaterThanOrEqualTo(Functions.getToString()).accept(one, "0"));
        Assertions.assertTrue(Predicates2.attributeGreaterThanOrEqualTo(Functions.getToString()).accept(one, "1"));
        Assertions.assertFalse(Predicates2.attributeGreaterThanOrEqualTo(Functions.getToString()).accept(one, "2"));
        Assertions.assertNotNull(Predicates2.attributeGreaterThanOrEqualTo(Functions.getToString()).toString());
    }

    @Test
    public void attributeLessThanOrEqualTo()
    {
        Assertions.assertFalse(Predicates2.attributeLessThanOrEqualTo(Functions.getToString()).accept(1, "0"));
        Assertions.assertTrue(Predicates2.attributeLessThanOrEqualTo(Functions.getToString()).accept(1, "1"));
        Assertions.assertTrue(Predicates2.attributeLessThanOrEqualTo(Functions.getToString()).accept(1, "2"));
        Assertions.assertNotNull(Predicates2.attributeLessThanOrEqualTo(Functions.getToString()).toString());
    }

    @Test
    public void in()
    {
        MutableList<String> list1 = Lists.fixedSize.of("1", "3");
        Assertions.assertTrue(Predicates2.in().accept("1", list1));
        Assertions.assertFalse(Predicates2.in().accept("2", list1));
        Assertions.assertNotNull(Predicates2.in().toString());
        MutableList<String> list2 = Lists.fixedSize.of("1", "2");
        MutableList<String> newList = ListIterate.selectWith(list2, Predicates2.in(), list1);
        Assertions.assertEquals(FastList.newListWith("1"), newList);
    }

    @Test
    public void attributeIn()
    {
        MutableList<String> upperList = Lists.fixedSize.of("A", "B");
        Assertions.assertTrue(Predicates2.attributeIn(StringFunctions.toUpperCase()).accept("a", upperList));
        Assertions.assertFalse(Predicates2.attributeIn(StringFunctions.toUpperCase()).accept("c", upperList));
        MutableList<String> lowerList = Lists.fixedSize.of("a", "c");
        MutableList<String> newList =
                ListIterate.selectWith(lowerList, Predicates2.attributeIn(StringFunctions.toUpperCase()), upperList);
        Assertions.assertEquals(FastList.newListWith("a"), newList);
    }

    @Test
    public void attributeIn_MultiTypes()
    {
        MutableList<String> stringInts = Lists.fixedSize.of("1", "2");
        Assertions.assertTrue(Predicates2.attributeIn(Functions.getToString()).accept(1, stringInts));
        Assertions.assertFalse(Predicates2.attributeIn(Functions.getToString()).accept(3, stringInts));
        Assertions.assertFalse(Predicates2.attributeIn(Functions.getToString()).accept(3, stringInts));
        MutableList<Integer> intList = Lists.fixedSize.of(1, 3);
        MutableList<Integer> newList =
                ListIterate.selectWith(intList, Predicates2.attributeIn(Functions.getToString()), stringInts);
        Assertions.assertEquals(FastList.newListWith(1), newList);
    }

    @Test
    public void notIn()
    {
        MutableList<String> odds = Lists.fixedSize.of("1", "3");
        Assertions.assertFalse(Predicates2.notIn().accept("1", odds));
        Assertions.assertTrue(Predicates2.notIn().accept("2", odds));
        Assertions.assertNotNull(Predicates2.notIn().toString());
        MutableList<String> list = Lists.fixedSize.of("1", "2");
        MutableList<String> newList = ListIterate.selectWith(list, Predicates2.notIn(), odds);
        Assertions.assertEquals(FastList.newListWith("2"), newList);
    }

    @Test
    public void attributeNotIn()
    {
        Function<String, String> function = StringFunctions.toLowerCase();
        MutableList<String> lowerList = Lists.fixedSize.of("a", "b");
        Assertions.assertFalse(Predicates2.attributeNotIn(function).accept("A", lowerList));
        Assertions.assertTrue(Predicates2.attributeNotIn(function).accept("C", lowerList));
        MutableList<String> upperList = Lists.fixedSize.of("A", "C");
        MutableList<String> newList = ListIterate.rejectWith(upperList, Predicates2.attributeNotIn(function), lowerList);
        Assertions.assertEquals(FastList.newListWith("A"), newList);
    }

    @Test
    public void lessThanNumber()
    {
        Assertions.assertTrue(Predicates2.<Integer>lessThan().accept(-1, 0));
        Assertions.assertTrue(Predicates2.<Double>lessThan().accept(-1.0, 0.0));
        Assertions.assertFalse(Predicates2.<Double>lessThan().accept(0.0, -1.0));
        Assertions.assertNotNull(Predicates2.<Integer>lessThan().toString());
    }

    @Test
    public void greaterThanNumber()
    {
        Assertions.assertFalse(Predicates2.<Integer>greaterThan().accept(-1, 0));
        Assertions.assertFalse(Predicates2.<Double>greaterThan().accept(-1.0, 0.0));
        Assertions.assertTrue(Predicates2.<Double>greaterThan().accept(0.0, -1.0));
        Assertions.assertNotNull(Predicates2.<Integer>greaterThan().toString());
    }

    @Test
    public void lessEqualThanNumber()
    {
        Assertions.assertTrue(Predicates2.<Integer>lessThanOrEqualTo().accept(-1, 0));
        Assertions.assertTrue(Predicates2.<Double>lessThanOrEqualTo().accept(-1.0, 0.0));
        Assertions.assertTrue(Predicates2.<Double>lessThanOrEqualTo().accept(-1.0, -1.0));
        Assertions.assertFalse(Predicates2.<Double>lessThanOrEqualTo().accept(0.0, -1.0));
        Assertions.assertNotNull(Predicates2.<Integer>lessThanOrEqualTo().toString());
    }

    @Test
    public void greaterEqualNumber()
    {
        Assertions.assertFalse(Predicates2.<Integer>greaterThanOrEqualTo().accept(-1, 0));
        Assertions.assertFalse(Predicates2.<Double>greaterThanOrEqualTo().accept(-1.0, 0.0));
        Assertions.assertTrue(Predicates2.<Double>greaterThanOrEqualTo().accept(-1.0, -1.0));
        Assertions.assertTrue(Predicates2.<Double>greaterThanOrEqualTo().accept(0.0, -1.0));
        Assertions.assertNotNull(Predicates2.<Integer>greaterThanOrEqualTo().toString());
    }
}
