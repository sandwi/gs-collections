/*
 * Copyright 2015 Goldman Sachs.
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

package com.gs.collections.impl.list.immutable;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.ListIterable;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.partition.list.PartitionImmutableList;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.block.factory.ObjectIntProcedures;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ImmutableEmptyListTest extends AbstractImmutableListTestCase
{
    @Override
    protected ImmutableList<Integer> classUnderTest()
    {
        return Lists.immutable.of();
    }

    @Override
    @Test
    public void indexOf()
    {
        Assertions.assertEquals(-1, this.classUnderTest().indexOf(1));
        Assertions.assertEquals(-1, this.classUnderTest().indexOf(null));
        ImmutableList<Integer> immutableList = this.classUnderTest().newWith(null);
        Assertions.assertEquals(immutableList.size() - 1, immutableList.indexOf(null));
        Assertions.assertEquals(-1, this.classUnderTest().indexOf(Integer.MAX_VALUE));
    }

    @Override
    @Test
    public void lastIndexOf()
    {
        Assertions.assertEquals(-1, this.classUnderTest().lastIndexOf(1));
        Assertions.assertEquals(-1, this.classUnderTest().lastIndexOf(null));
        Assertions.assertEquals(-1, this.classUnderTest().lastIndexOf(null));
        ImmutableList<Integer> immutableList = this.classUnderTest().newWith(null);
        Assertions.assertEquals(immutableList.size() - 1, immutableList.lastIndexOf(null));
        Assertions.assertEquals(-1, this.classUnderTest().lastIndexOf(Integer.MAX_VALUE));
    }

    @Test
    public void newWithout()
    {
        Assertions.assertSame(Lists.immutable.of(), Lists.immutable.of().newWithout(1));
        Assertions.assertSame(Lists.immutable.of(), Lists.immutable.of().newWithoutAll(Interval.oneTo(3)));
    }

    @Override
    @Test
    public void reverseForEach()
    {
        ImmutableList<Integer> list = Lists.immutable.of();
        MutableList<Integer> result = Lists.mutable.of();
        list.reverseForEach(CollectionAddProcedure.on(result));
        Assertions.assertEquals(list, result);
    }

    @Override
    @Test
    public void forEachFromTo()
    {
        MutableList<Integer> result = Lists.mutable.of();
        MutableList<Integer> reverseResult = Lists.mutable.of();
        ImmutableList<Integer> list = this.classUnderTest();
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> list.forEach(0, list.size() - 1, CollectionAddProcedure.on(result)));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> list.forEach(list.size() - 1, 0, CollectionAddProcedure.on(reverseResult)));
    }

    @Override
    @Test
    public void forEachWithIndexFromTo()
    {
        MutableList<Integer> result = Lists.mutable.of();
        MutableList<Integer> reverseResult = Lists.mutable.of();
        ImmutableList<Integer> list = this.classUnderTest();
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> list.forEachWithIndex(0, list.size() - 1, ObjectIntProcedures.fromProcedure(CollectionAddProcedure.on(result))));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> list.forEachWithIndex(list.size() - 1, 0, ObjectIntProcedures.fromProcedure(CollectionAddProcedure.on(reverseResult))));
    }

    @Override
    @Test
    public void detect()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        Assertions.assertNull(integers.detect(Integer.valueOf(1)::equals));
    }

    @Override
    @Test
    public void detectWith()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        Assertions.assertNull(integers.detectWith(Object::equals, Integer.valueOf(1)));
    }

    @Override
    @Test
    public void distinct()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        Assertions.assertNotNull(integers.distinct());
        Assertions.assertTrue(integers.isEmpty());
    }

    @Override
    @Test
    public void distinctWithHashingStrategy()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        Assertions.assertNotNull(integers.distinct(HashingStrategies.defaultStrategy()));
        Assertions.assertTrue(integers.isEmpty());
    }

    @Override
    @Test
    public void countWith()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        Assertions.assertEquals(0, integers.countWith(ERROR_THROWING_PREDICATE_2, Integer.class));
    }

    @Override
    @Test
    public void corresponds()
    {
        //Evaluates true for all empty lists and false for all non-empty lists

        Assertions.assertTrue(this.classUnderTest().corresponds(Lists.mutable.of(), Predicates2.alwaysFalse()));

        ImmutableList<Integer> integers = this.classUnderTest().newWith(Integer.valueOf(1));
        Assertions.assertFalse(this.classUnderTest().corresponds(integers, Predicates2.alwaysTrue()));
    }

    @Override
    @Test
    public void allSatisfy()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        Assertions.assertTrue(integers.allSatisfy(ERROR_THROWING_PREDICATE));
    }

    @Override
    @Test
    public void allSatisfyWith()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        Assertions.assertTrue(integers.allSatisfyWith(ERROR_THROWING_PREDICATE_2, Integer.class));
    }

    @Override
    @Test
    public void noneSatisfy()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        Assertions.assertTrue(integers.noneSatisfy(ERROR_THROWING_PREDICATE));
    }

    @Override
    @Test
    public void noneSatisfyWith()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        Assertions.assertTrue(integers.noneSatisfyWith(ERROR_THROWING_PREDICATE_2, Integer.class));
    }

    @Override
    @Test
    public void anySatisfy()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        Assertions.assertFalse(integers.anySatisfy(ERROR_THROWING_PREDICATE));
    }

    @Override
    @Test
    public void anySatisfyWith()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        Assertions.assertFalse(integers.anySatisfyWith(ERROR_THROWING_PREDICATE_2, Integer.class));
    }

    @Override
    @Test
    public void getFirst()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        Assertions.assertNull(integers.getFirst());
    }

    @Override
    @Test
    public void getLast()
    {
        ImmutableList<Integer> integers = this.classUnderTest();
        Assertions.assertNull(integers.getLast());
    }

    @Override
    @Test
    public void isEmpty()
    {
        ImmutableList<Integer> list = this.classUnderTest();
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
    public void subList()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.classUnderTest().subList(0, 1);
        });
    }

    @Override
    @Test
    public void zip()
    {
        ImmutableList<Integer> immutableList = this.classUnderTest();
        List<Object> nulls = Collections.nCopies(immutableList.size(), null);
        List<Object> nullsPlusOne = Collections.nCopies(immutableList.size() + 1, null);

        ImmutableList<Pair<Integer, Object>> pairs = immutableList.zip(nulls);
        Assertions.assertEquals(immutableList, pairs.collect((Function<Pair<Integer, ?>, Integer>) Pair::getOne));
        Assertions.assertEquals(nulls, pairs.collect((Function<Pair<?, Object>, Object>) Pair::getTwo));

        ImmutableList<Pair<Integer, Object>> pairsPlusOne = immutableList.zip(nullsPlusOne);
        Assertions.assertEquals(immutableList, pairsPlusOne.collect((Function<Pair<Integer, ?>, Integer>) Pair::getOne));
        Assertions.assertEquals(nulls, pairsPlusOne.collect((Function<Pair<?, Object>, Object>) Pair::getTwo));

        Assertions.assertEquals(immutableList.zip(nulls), immutableList.zip(nulls, FastList.<Pair<Integer, Object>>newList()));
    }

    @Override
    @Test
    public void zipWithIndex()
    {
        ImmutableList<Integer> immutableList = this.classUnderTest();
        ImmutableList<Pair<Integer, Integer>> pairs = immutableList.zipWithIndex();

        Assertions.assertEquals(immutableList, pairs.collect((Function<Pair<Integer, ?>, Integer>) Pair::getOne));
        Assertions.assertEquals(FastList.<Integer>newList(), pairs.collect((Function<Pair<?, Integer>, Integer>) Pair::getTwo));

        Assertions.assertEquals(immutableList.zipWithIndex(), immutableList.zipWithIndex(FastList.<Pair<Integer, Integer>>newList()));
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
        Assertions.assertEquals(this.classUnderTest(), this.classUnderTest().chunk(10));
        Verify.assertInstanceOf(ImmutableList.class, this.classUnderTest().chunk(10));
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        ImmutableList<Integer> immutable = this.classUnderTest();
        MutableList<Integer> mutable = FastList.newList(immutable);
        Verify.assertEqualsAndHashCode(immutable, mutable);
        Verify.assertPostSerializedIdentity(immutable);
        Assertions.assertNotEquals(immutable, UnifiedSet.newSet(mutable));
    }

    @Override
    @Test
    public void take()
    {
        ImmutableList<Integer> immutableList = this.classUnderTest();
        Assertions.assertSame(immutableList, immutableList.take(0));
        Assertions.assertSame(immutableList, immutableList.take(10));
        Assertions.assertSame(immutableList, immutableList.take(Integer.MAX_VALUE));
    }

    @Override
    @Test
    public void takeWhile()
    {
        Assertions.assertEquals(Lists.immutable.of(), this.classUnderTest().takeWhile(ignored -> true));
        Assertions.assertEquals(Lists.immutable.of(), this.classUnderTest().takeWhile(ignored -> false));
    }

    @Override
    @Test
    public void drop()
    {
        super.drop();

        ImmutableList<Integer> immutableList = this.classUnderTest();
        Assertions.assertSame(immutableList, immutableList.drop(10));
        Assertions.assertSame(immutableList, immutableList.drop(0));
        Assertions.assertSame(immutableList, immutableList.drop(Integer.MAX_VALUE));
    }

    @Override
    @Test
    public void dropWhile()
    {
        super.dropWhile();

        Assertions.assertEquals(Lists.immutable.of(), this.classUnderTest().dropWhile(ignored -> true));
        Assertions.assertEquals(Lists.immutable.of(), this.classUnderTest().dropWhile(ignored -> false));
    }

    @Override
    @Test
    public void partitionWhile()
    {
        super.partitionWhile();

        PartitionImmutableList<Integer> partition1 = this.classUnderTest().partitionWhile(ignored -> true);
        Assertions.assertEquals(Lists.immutable.of(), partition1.getSelected());
        Assertions.assertEquals(Lists.immutable.of(), partition1.getRejected());

        PartitionImmutableList<Integer> partiton2 = this.classUnderTest().partitionWhile(ignored -> false);
        Assertions.assertEquals(Lists.immutable.of(), partiton2.getSelected());
        Assertions.assertEquals(Lists.immutable.of(), partiton2.getRejected());
    }

    @Override
    @Test
    public void listIterator()
    {
        ListIterator<Integer> it = this.classUnderTest().listIterator();
        Assertions.assertFalse(it.hasPrevious());
        Assertions.assertEquals(-1, it.previousIndex());
        Assertions.assertEquals(0, it.nextIndex());

        Verify.assertThrows(NoSuchElementException.class, (Runnable) it::next);

        Verify.assertThrows(UnsupportedOperationException.class, it::remove);

        Verify.assertThrows(UnsupportedOperationException.class, () -> it.add(null));
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

    @Test
    public void binarySearch()
    {
        ListIterable<Integer> sortedList = this.classUnderTest();
        Assertions.assertEquals(-1, sortedList.binarySearch(1));
    }

    @Test
    public void binarySearchWithComparator()
    {
        ListIterable<Integer> sortedList = this.classUnderTest();
        Assertions.assertEquals(-1, sortedList.binarySearch(1, Integer::compareTo));
    }

    @Override
    @Test
    public void detectIndex()
    {
        // any predicate will result in -1
        Assertions.assertEquals(-1, this.classUnderTest().detectIndex(Predicates.alwaysTrue()));
    }

    @Override
    @Test
    public void detectLastIndex()
    {
        // any predicate will result in -1
        Assertions.assertEquals(-1, this.classUnderTest().detectLastIndex(Predicates.alwaysTrue()));
    }
}
