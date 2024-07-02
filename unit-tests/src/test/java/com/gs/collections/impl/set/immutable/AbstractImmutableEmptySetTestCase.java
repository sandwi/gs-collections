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

package com.gs.collections.impl.set.immutable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.set.ImmutableSet;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.set.primitive.ImmutableBooleanSet;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.factory.PrimitiveFunctions;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class AbstractImmutableEmptySetTestCase extends AbstractImmutableSetTestCase
{
    @Test
    public void containsAll()
    {
        Assertions.assertTrue(this.classUnderTest().castToSet().containsAll(new HashSet<>()));
        Assertions.assertFalse(this.classUnderTest().castToSet().containsAll(UnifiedSet.newSetWith(1)));
    }

    @Override
    @Test
    public void newWith()
    {
        ImmutableSet<Integer> immutable = this.classUnderTest();
        Verify.assertSize(1, immutable.newWith(1).castToSet());
    }

    @Override
    @Test
    public void detect()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Assertions.assertNull(integers.detect(Integer.valueOf(1)::equals));
    }

    @Override
    @Test
    public void detectWith()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Assertions.assertNull(integers.detectWith(Object::equals, Integer.valueOf(1)));
    }

    @Override
    @Test
    public void anySatisfy()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Assertions.assertFalse(integers.anySatisfy(ERROR_THROWING_PREDICATE));
    }

    @Override
    @Test
    public void anySatisfyWith()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Assertions.assertFalse(integers.anySatisfyWith(ERROR_THROWING_PREDICATE_2, Integer.class));
    }

    @Override
    @Test
    public void allSatisfy()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Assertions.assertTrue(integers.allSatisfy(ERROR_THROWING_PREDICATE));
    }

    @Override
    @Test
    public void allSatisfyWith()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Assertions.assertTrue(integers.allSatisfyWith(ERROR_THROWING_PREDICATE_2, Integer.class));
    }

    @Override
    @Test
    public void noneSatisfy()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Assertions.assertTrue(integers.noneSatisfy(ERROR_THROWING_PREDICATE));
    }

    @Override
    @Test
    public void noneSatisfyWith()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Assertions.assertTrue(integers.noneSatisfyWith(ERROR_THROWING_PREDICATE_2, Integer.class));
    }

    @Override
    @Test
    public void getFirst()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Assertions.assertNull(integers.getFirst());
    }

    @Override
    @Test
    public void getLast()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        Assertions.assertNull(integers.getLast());
    }

    @Override
    @Test
    public void isEmpty()
    {
        ImmutableSet<Integer> list = this.classUnderTest();
        Assertions.assertTrue(list.isEmpty());
        Assertions.assertFalse(list.notEmpty());
    }

    @Override
    @Test
    public void min()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.classUnderTest().min(Integer::compareTo);
        });
    }

    @Override
    @Test
    public void max()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.classUnderTest().max(Integer::compareTo);
        });
    }

    @Test
    @Override
    public void min_null_throws()
    {
        // Not applicable for empty collections
        super.min_null_throws();
    }

    @Test
    @Override
    public void max_null_throws()
    {
        // Not applicable for empty collections
        super.max_null_throws();
    }

    @Override
    @Test
    public void min_without_comparator()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.classUnderTest().min();
        });
    }

    @Override
    @Test
    public void max_without_comparator()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.classUnderTest().max();
        });
    }

    @Test
    @Override
    public void min_null_throws_without_comparator()
    {
        // Not applicable for empty collections
        super.min_null_throws_without_comparator();
    }

    @Test
    @Override
    public void max_null_throws_without_comparator()
    {
        // Not applicable for empty collections
        super.max_null_throws_without_comparator();
    }

    @Override
    @Test
    public void minBy()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.classUnderTest().minBy(String::valueOf);
        });
    }

    @Override
    @Test
    public void maxBy()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.classUnderTest().maxBy(String::valueOf);
        });
    }

    @Override
    @Test
    public void zip()
    {
        ImmutableSet<Integer> immutableSet = this.classUnderTest();
        List<Object> nulls = Collections.nCopies(immutableSet.size(), null);
        List<Object> nullsPlusOne = Collections.nCopies(immutableSet.size() + 1, null);

        ImmutableSet<Pair<Integer, Object>> pairs = immutableSet.zip(nulls);
        Assertions.assertEquals(immutableSet, pairs.collect((Function<Pair<Integer, ?>, Integer>) Pair::getOne));
        Assertions.assertEquals(UnifiedSet.newSet(nulls), pairs.collect((Function<Pair<?, Object>, Object>) Pair::getTwo));

        ImmutableSet<Pair<Integer, Object>> pairsPlusOne = immutableSet.zip(nullsPlusOne);
        Assertions.assertEquals(immutableSet, pairsPlusOne.collect((Function<Pair<Integer, ?>, Integer>) Pair::getOne));
        Assertions.assertEquals(UnifiedSet.newSet(nulls), pairsPlusOne.collect((Function<Pair<?, Object>, Object>) Pair::getTwo));

        Assertions.assertEquals(immutableSet.zip(nulls), immutableSet.zip(nulls, UnifiedSet.<Pair<Integer, Object>>newSet()));
    }

    @Override
    @Test
    public void zipWithIndex()
    {
        ImmutableSet<Integer> immutableSet = this.classUnderTest();
        ImmutableSet<Pair<Integer, Integer>> pairs = immutableSet.zipWithIndex();

        Assertions.assertEquals(immutableSet, pairs.collect((Function<Pair<Integer, ?>, Integer>) Pair::getOne));
        Assertions.assertEquals(
                UnifiedSet.<Integer>newSet(),
                pairs.collect((Function<Pair<?, Integer>, Integer>) Pair::getTwo));

        Assertions.assertEquals(
                immutableSet.zipWithIndex(),
                immutableSet.zipWithIndex(UnifiedSet.<Pair<Integer, Integer>>newSet()));
    }

    @Test
    public void chunk()
    {
        Assertions.assertEquals(Lists.mutable.of(), this.classUnderTest().chunk(2));
    }

    @Override
    @Test
    public void chunk_zero_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            this.classUnderTest().chunk(0);
        });
    }

    @Override
    @Test
    public void chunk_large_size()
    {
        Assertions.assertEquals(Lists.mutable.of(), this.classUnderTest().chunk(10));
    }

    @Override
    @Test
    public void union()
    {
        Assertions.assertEquals(
                UnifiedSet.newSetWith(1, 2, 3),
                this.classUnderTest().union(UnifiedSet.newSetWith(1, 2, 3)));
    }

    @Override
    @Test
    public void unionInto()
    {
        Assertions.assertEquals(
                UnifiedSet.newSetWith(1, 2, 3),
                this.classUnderTest().unionInto(UnifiedSet.newSetWith(1, 2, 3), UnifiedSet.<Integer>newSet()));
    }

    @Override
    @Test
    public void intersect()
    {
        Assertions.assertEquals(
                UnifiedSet.<String>newSet(),
                this.classUnderTest().intersect(UnifiedSet.newSetWith(1, 2, 3)));
    }

    @Override
    @Test
    public void intersectInto()
    {
        Assertions.assertEquals(
                UnifiedSet.<String>newSet(),
                this.classUnderTest().intersectInto(UnifiedSet.newSetWith(1, 2, 3), UnifiedSet.<Integer>newSet()));
    }

    @Override
    @Test
    public void difference()
    {
        ImmutableSet<Integer> set = this.classUnderTest();
        ImmutableSet<Integer> difference = set.difference(UnifiedSet.newSetWith(1, 2, 3, 999));
        Assertions.assertEquals(UnifiedSet.<Integer>newSet(), difference);
        Assertions.assertEquals(set, set.difference(UnifiedSet.newSetWith(999)));
    }

    @Override
    @Test
    public void differenceInto()
    {
        ImmutableSet<Integer> set = this.classUnderTest();
        MutableSet<Integer> difference = set.differenceInto(UnifiedSet.newSetWith(1, 2, 3, 999), UnifiedSet.<Integer>newSet());
        Assertions.assertEquals(UnifiedSet.<Integer>newSet(), difference);
        Assertions.assertEquals(set, set.differenceInto(UnifiedSet.newSetWith(99), UnifiedSet.<Integer>newSet()));
    }

    @Override
    @Test
    public void symmetricDifference()
    {
        Assertions.assertEquals(
                UnifiedSet.newSetWith(999),
                this.classUnderTest().symmetricDifference(UnifiedSet.newSetWith(999)));
    }

    @Override
    @Test
    public void symmetricDifferenceInto()
    {
        Assertions.assertEquals(
                UnifiedSet.newSetWith(999),
                this.classUnderTest().symmetricDifferenceInto(UnifiedSet.newSetWith(999), UnifiedSet.<Integer>newSet()));
    }

    @Override
    @Test
    public void collectBoolean()
    {
        ImmutableSet<Integer> integers = this.classUnderTest();
        ImmutableBooleanSet actual = integers.collectBoolean(PrimitiveFunctions.integerIsPositive());
        Verify.assertEmpty(actual);
    }

    @Override
    @Test
    public void collect_target()
    {
        MutableList<Integer> targetCollection = FastList.newList();
        MutableList<Integer> actual = this.classUnderTest().collect(object -> {
            throw new AssertionError();
        }, targetCollection);
        Assertions.assertEquals(targetCollection, actual);
        Assertions.assertSame(targetCollection, actual);
    }

    @Override
    @Test
    public void collectWith_target()
    {
        MutableList<Integer> targetCollection = FastList.newList();
        MutableList<Integer> actual = this.classUnderTest().collectWith((argument1, argument2) -> {
            throw new AssertionError();
        }, 1, targetCollection);
        Assertions.assertEquals(targetCollection, actual);
        Assertions.assertSame(targetCollection, actual);
    }
}
