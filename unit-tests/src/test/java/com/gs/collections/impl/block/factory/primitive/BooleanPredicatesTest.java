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

package com.gs.collections.impl.block.factory.primitive;

import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class BooleanPredicatesTest
{
    @Test
    public void testEqual()
    {
        Assertions.assertTrue(BooleanPredicates.equal(true).accept(true));
        Assertions.assertTrue(BooleanPredicates.equal(false).accept(false));
        Assertions.assertFalse(BooleanPredicates.equal(true).accept(false));
        Assertions.assertFalse(BooleanPredicates.equal(false).accept(true));
    }

    @Test
    public void testIsTrue()
    {
        Assertions.assertTrue(BooleanPredicates.isTrue().accept(true));
        Assertions.assertFalse(BooleanPredicates.isTrue().accept(false));
    }

    @Test
    public void testIsFalse()
    {
        Assertions.assertTrue(BooleanPredicates.isFalse().accept(false));
        Assertions.assertFalse(BooleanPredicates.isFalse().accept(true));
    }

    @Test
    public void testAnd()
    {
        Assertions.assertFalse(BooleanPredicates.and(BooleanPredicates.isTrue(), BooleanPredicates.equal(true)).accept(false));
        Assertions.assertFalse(BooleanPredicates.and(BooleanPredicates.isTrue(), BooleanPredicates.equal(false)).accept(false));
        Assertions.assertFalse(BooleanPredicates.and(BooleanPredicates.isFalse(), BooleanPredicates.equal(true)).accept(false));
        Assertions.assertTrue(BooleanPredicates.and(BooleanPredicates.isFalse(), BooleanPredicates.equal(false)).accept(false));

        Assertions.assertTrue(BooleanPredicates.and(BooleanPredicates.isTrue(), BooleanPredicates.equal(true)).accept(true));
        Assertions.assertFalse(BooleanPredicates.and(BooleanPredicates.isTrue(), BooleanPredicates.equal(false)).accept(false));
        Assertions.assertFalse(BooleanPredicates.and(BooleanPredicates.isFalse(), BooleanPredicates.equal(true)).accept(true));
        Assertions.assertFalse(BooleanPredicates.and(BooleanPredicates.isFalse(), BooleanPredicates.equal(false)).accept(true));

        Assertions.assertFalse(BooleanPredicates.and(BooleanPredicates.isFalse(), value -> !value).accept(true));
        Assertions.assertTrue(BooleanPredicates.and(BooleanPredicates.isFalse(), value -> !value).accept(false));
    }

    @Test
    public void testOr()
    {
        Assertions.assertFalse(BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.equal(true)).accept(false));
        Assertions.assertTrue(BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.equal(false)).accept(false));
        Assertions.assertTrue(BooleanPredicates.or(BooleanPredicates.isFalse(), BooleanPredicates.equal(true)).accept(false));
        Assertions.assertTrue(BooleanPredicates.or(BooleanPredicates.isFalse(), BooleanPredicates.equal(false)).accept(false));

        Assertions.assertTrue(BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.equal(true)).accept(true));
        Assertions.assertTrue(BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.equal(false)).accept(true));
        Assertions.assertTrue(BooleanPredicates.or(BooleanPredicates.isFalse(), BooleanPredicates.equal(true)).accept(true));
        Assertions.assertFalse(BooleanPredicates.or(BooleanPredicates.isFalse(), BooleanPredicates.equal(false)).accept(true));

        Assertions.assertTrue(BooleanPredicates.or(BooleanPredicates.isFalse(), value -> !value).accept(false));
        Assertions.assertFalse(BooleanPredicates.or(BooleanPredicates.isFalse(), value -> !value).accept(true));
    }

    @Test
    public void testNot()
    {
        Assertions.assertTrue(BooleanPredicates.not(BooleanPredicates.isTrue()).accept(false));
        Assertions.assertFalse(BooleanPredicates.not(BooleanPredicates.isTrue()).accept(true));
        Assertions.assertTrue(BooleanPredicates.not(BooleanPredicates.isFalse()).accept(true));
        Assertions.assertFalse(BooleanPredicates.not(BooleanPredicates.isFalse()).accept(false));
        Assertions.assertTrue(BooleanPredicates.not(true).accept(false));
        Assertions.assertFalse(BooleanPredicates.not(true).accept(true));
        Assertions.assertTrue(BooleanPredicates.not(false).accept(true));
        Assertions.assertFalse(BooleanPredicates.not(false).accept(false));
    }

    @Test
    public void testAlwaysTrue()
    {
        Assertions.assertTrue(BooleanPredicates.alwaysTrue().accept(false));
        Assertions.assertTrue(BooleanPredicates.alwaysTrue().accept(true));
    }

    @Test
    public void testAlwaysFalse()
    {
        Assertions.assertFalse(BooleanPredicates.alwaysFalse().accept(false));
        Assertions.assertFalse(BooleanPredicates.alwaysFalse().accept(true));
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(BooleanPredicates.class);
    }
}
