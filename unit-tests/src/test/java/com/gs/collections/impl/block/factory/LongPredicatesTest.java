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

public class LongPredicatesTest
{
    private static final Function<Long, Long> LONG_VALUE = Long::longValue;

    @Test
    public void isOdd()
    {
        Assertions.assertTrue(LongPredicates.isOdd().accept(1L));
        Assertions.assertFalse(LongPredicates.isOdd().accept(-2L));
    }

    @Test
    public void isEven()
    {
        Assertions.assertTrue(LongPredicates.isEven().accept(-42L));
        Assertions.assertTrue(LongPredicates.isEven().accept(0L));
        Assertions.assertFalse(LongPredicates.isEven().accept(1L));
    }

    @Test
    public void attributeIsOdd()
    {
        Assertions.assertTrue(LongPredicates.attributeIsOdd(LONG_VALUE).accept(1L));
        Assertions.assertFalse(LongPredicates.attributeIsOdd(LONG_VALUE).accept(-2L));
    }

    @Test
    public void attributeIsEven()
    {
        Assertions.assertTrue(LongPredicates.attributeIsEven(LONG_VALUE).accept(-42L));
        Assertions.assertTrue(LongPredicates.attributeIsEven(LONG_VALUE).accept(0L));
        Assertions.assertFalse(LongPredicates.attributeIsEven(LONG_VALUE).accept(1L));
    }

    @Test
    public void isZero()
    {
        Assertions.assertTrue(LongPredicates.isZero().accept(0L));
        Assertions.assertFalse(LongPredicates.isZero().accept(1L));
        Assertions.assertFalse(LongPredicates.isZero().accept(-1L));
    }

    @Test
    public void isPositive()
    {
        Assertions.assertFalse(LongPredicates.isPositive().accept(0L));
        Assertions.assertTrue(LongPredicates.isPositive().accept(1L));
        Assertions.assertFalse(LongPredicates.isPositive().accept(-1L));
    }

    @Test
    public void isNegative()
    {
        Assertions.assertFalse(LongPredicates.isNegative().accept(0L));
        Assertions.assertFalse(LongPredicates.isNegative().accept(1L));
        Assertions.assertTrue(LongPredicates.isNegative().accept(-1L));
    }

    @Test
    public void attributeIsZero()
    {
        Assertions.assertTrue(LongPredicates.attributeIsZero(Integer::longValue).accept(0));
        Assertions.assertFalse(LongPredicates.attributeIsZero(Integer::longValue).accept(1));
    }

    @Test
    public void attributeIsPositive()
    {
        Assertions.assertTrue(LongPredicates.attributeIsPositive(Integer::longValue).accept(1));
        Assertions.assertFalse(LongPredicates.attributeIsPositive(Integer::longValue).accept(0));
        Assertions.assertFalse(LongPredicates.attributeIsPositive(Integer::longValue).accept(-1));
    }

    @Test
    public void attributeIsNegative()
    {
        Assertions.assertTrue(LongPredicates.attributeIsNegative(Integer::longValue).accept(-1));
        Assertions.assertFalse(LongPredicates.attributeIsNegative(Integer::longValue).accept(0));
        Assertions.assertFalse(LongPredicates.attributeIsNegative(Integer::longValue).accept(1));
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(LongPredicates.class);
    }
}
