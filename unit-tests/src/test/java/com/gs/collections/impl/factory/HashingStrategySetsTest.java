/*
 * Copyright 2012 Goldman Sachs.
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

package com.gs.collections.impl.factory;

import com.gs.collections.api.factory.set.strategy.ImmutableHashingStrategySetFactory;
import com.gs.collections.api.factory.set.strategy.MutableHashingStrategySetFactory;
import com.gs.collections.api.set.ImmutableSet;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class HashingStrategySetsTest
{
    @Test
    public void immutable()
    {
        ImmutableHashingStrategySetFactory factory = HashingStrategySets.immutable;
        Assertions.assertEquals(UnifiedSet.newSet(), factory.of(HashingStrategies.defaultStrategy()));
        Verify.assertInstanceOf(ImmutableSet.class, factory.of(HashingStrategies.defaultStrategy()));
        Assertions.assertEquals(UnifiedSet.newSetWith(1, 2), factory.of(HashingStrategies.defaultStrategy(), 1, 2));
        Verify.assertInstanceOf(ImmutableSet.class, factory.of(HashingStrategies.defaultStrategy(), 1, 2));
        Assertions.assertEquals(UnifiedSet.newSetWith(1, 2, 3, 4), factory.of(HashingStrategies.defaultStrategy(), 1, 2, 3, 4));
        Verify.assertInstanceOf(ImmutableSet.class, factory.of(HashingStrategies.defaultStrategy(), 1, 2, 3, 4));
        Assertions.assertEquals(UnifiedSet.newSetWith(1, 2, 3, 4, 5, 6), factory.of(HashingStrategies.defaultStrategy(), 1, 2, 3, 4, 5, 6));
        Verify.assertInstanceOf(ImmutableSet.class, factory.of(HashingStrategies.defaultStrategy(), 1, 2, 3, 4, 5, 6));
        Assertions.assertEquals(UnifiedSet.newSetWith(1, 2, 3, 4, 5, 6, 7, 8), factory.of(HashingStrategies.defaultStrategy(), 1, 2, 3, 4, 5, 6, 7, 8));
        Verify.assertInstanceOf(ImmutableSet.class, factory.of(HashingStrategies.defaultStrategy(), 1, 2, 3, 4, 5, 6, 7, 8));
        Assertions.assertEquals(UnifiedSet.newSetWith(), factory.of(HashingStrategies.defaultStrategy(), null));
        Verify.assertInstanceOf(ImmutableSet.class, factory.of(HashingStrategies.defaultStrategy(), null));
    }

    @Test
    public void mutable()
    {
        MutableHashingStrategySetFactory factory = HashingStrategySets.mutable;
        Assertions.assertEquals(UnifiedSet.newSet(), factory.of(HashingStrategies.defaultStrategy()));
        Verify.assertInstanceOf(MutableSet.class, factory.of(HashingStrategies.defaultStrategy()));
        Assertions.assertEquals(UnifiedSet.newSetWith(1, 2), factory.of(HashingStrategies.defaultStrategy(), 1, 2));
        Verify.assertInstanceOf(MutableSet.class, factory.of(HashingStrategies.defaultStrategy(), 1, 2));
        Assertions.assertEquals(UnifiedSet.newSetWith(1, 2, 3, 4), factory.of(HashingStrategies.defaultStrategy(), 1, 2, 3, 4));
        Verify.assertInstanceOf(MutableSet.class, factory.of(HashingStrategies.defaultStrategy(), 1, 2, 3, 4));
        Assertions.assertEquals(UnifiedSet.newSetWith(1, 2, 3, 4, 5, 6), factory.of(HashingStrategies.defaultStrategy(), 1, 2, 3, 4, 5, 6));
        Verify.assertInstanceOf(MutableSet.class, factory.of(HashingStrategies.defaultStrategy(), 1, 2, 3, 4, 5, 6));
        Assertions.assertEquals(UnifiedSet.newSetWith(1, 2, 3, 4, 5, 6, 7, 8), factory.of(HashingStrategies.defaultStrategy(), 1, 2, 3, 4, 5, 6, 7, 8));
        Verify.assertInstanceOf(MutableSet.class, factory.of(HashingStrategies.defaultStrategy(), 1, 2, 3, 4, 5, 6, 7, 8));
        Assertions.assertEquals(UnifiedSet.newSetWith(1, 2, 3, 4, 5, 6, 7, 8), factory.ofAll(HashingStrategies.defaultStrategy(), FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8)));
        Verify.assertInstanceOf(MutableSet.class, factory.of(HashingStrategies.defaultStrategy(), FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8)));
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(HashingStrategySets.class);
    }
}
