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

import com.gs.collections.api.block.function.Function;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class IntegerPredicatesTest
{
    private static final Function<Integer, Integer> INT_VALUE = integer -> integer;

    @Test
    public void isOdd()
    {
        Assertions.assertTrue(IntegerPredicates.isOdd().accept(1));
        Assertions.assertFalse(IntegerPredicates.isOdd().accept(-2));
    }

    @Test
    public void isEven()
    {
        Assertions.assertTrue(IntegerPredicates.isEven().accept(-42));
        Assertions.assertTrue(IntegerPredicates.isEven().accept(0));
        Assertions.assertFalse(IntegerPredicates.isEven().accept(1));
    }

    @Test
    public void attributeIsOdd()
    {
        Assertions.assertTrue(IntegerPredicates.attributeIsOdd(INT_VALUE).accept(1));
        Assertions.assertFalse(IntegerPredicates.attributeIsOdd(INT_VALUE).accept(-2));
    }

    @Test
    public void attributeIsEven()
    {
        Assertions.assertTrue(IntegerPredicates.attributeIsEven(INT_VALUE).accept(-42));
        Assertions.assertTrue(IntegerPredicates.attributeIsEven(INT_VALUE).accept(0));
        Assertions.assertFalse(IntegerPredicates.attributeIsEven(INT_VALUE).accept(1));
    }

    @Test
    public void attributeIsZero()
    {
        Assertions.assertFalse(IntegerPredicates.attributeIsZero(INT_VALUE).accept(-42));
        Assertions.assertTrue(IntegerPredicates.attributeIsZero(INT_VALUE).accept(0));
        Assertions.assertFalse(IntegerPredicates.attributeIsZero(INT_VALUE).accept(1));
    }

    @Test
    public void attributeIsPositive()
    {
        Assertions.assertFalse(IntegerPredicates.attributeIsPositive(INT_VALUE).accept(-42));
        Assertions.assertFalse(IntegerPredicates.attributeIsPositive(INT_VALUE).accept(0));
        Assertions.assertTrue(IntegerPredicates.attributeIsPositive(INT_VALUE).accept(1));
    }

    @Test
    public void attributeIsNegative()
    {
        Assertions.assertTrue(IntegerPredicates.attributeIsNegative(INT_VALUE).accept(-42));
        Assertions.assertFalse(IntegerPredicates.attributeIsNegative(INT_VALUE).accept(0));
        Assertions.assertFalse(IntegerPredicates.attributeIsNegative(INT_VALUE).accept(1));
    }

    @Test
    public void isZero()
    {
        Assertions.assertTrue(IntegerPredicates.isZero().accept(0));
        Assertions.assertFalse(IntegerPredicates.isZero().accept(1));
        Assertions.assertFalse(IntegerPredicates.isZero().accept(-1));
    }

    @Test
    public void isPositive()
    {
        Assertions.assertFalse(IntegerPredicates.isPositive().accept(0));
        Assertions.assertTrue(IntegerPredicates.isPositive().accept(1));
        Assertions.assertFalse(IntegerPredicates.isPositive().accept(-1));
    }

    @Test
    public void isNegative()
    {
        Assertions.assertFalse(IntegerPredicates.isNegative().accept(0));
        Assertions.assertFalse(IntegerPredicates.isNegative().accept(1));
        Assertions.assertTrue(IntegerPredicates.isNegative().accept(-1));
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(IntegerPredicates.class);
    }
}
