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

package com.gs.collections.impl.set.sorted.immutable;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.gs.collections.api.LazyIterable;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.sorted.MutableSortedMap;
import com.gs.collections.api.partition.set.sorted.PartitionImmutableSortedSet;
import com.gs.collections.api.set.sorted.ImmutableSortedSet;
import com.gs.collections.api.set.sorted.MutableSortedSet;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.factory.PrimitiveFunctions;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.factory.SortedSets;
import com.gs.collections.impl.factory.Stacks;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.AddToList;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.list.mutable.primitive.ByteArrayList;
import com.gs.collections.impl.list.mutable.primitive.CharArrayList;
import com.gs.collections.impl.list.mutable.primitive.DoubleArrayList;
import com.gs.collections.impl.list.mutable.primitive.FloatArrayList;
import com.gs.collections.impl.list.mutable.primitive.IntArrayList;
import com.gs.collections.impl.list.mutable.primitive.LongArrayList;
import com.gs.collections.impl.list.mutable.primitive.ShortArrayList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.map.sorted.mutable.TreeSortedMap;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.set.sorted.mutable.TreeSortedSet;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ImmutableEmptySortedSetTest extends AbstractImmutableSortedSetTestCase
{
    @Override
    protected ImmutableSortedSet<Integer> classUnderTest()
    {
        return SortedSets.immutable.of();
    }

    @Override
    protected ImmutableSortedSet<Integer> classUnderTest(Comparator<? super Integer> comparator)
    {
        return SortedSets.immutable.of(comparator);
    }

    @Test
    public void testContainsAll()
    {
        Assertions.assertTrue(this.classUnderTest().containsAllIterable(UnifiedSet.<Integer>newSet()));
        Assertions.assertFalse(this.classUnderTest().containsAllIterable(UnifiedSet.newSetWith(1)));
    }

    @Test
    public void testNewSortedSet()
    {
        Assertions.assertSame(SortedSets.immutable.of(), SortedSets.immutable.ofAll(FastList.newList()));
        Assertions.assertSame(SortedSets.immutable.of(), SortedSets.immutable.ofSortedSet(TreeSortedSet.newSet()));
        Assertions.assertNotSame(SortedSets.immutable.of(),
                SortedSets.immutable.ofSortedSet(TreeSortedSet.newSet(Comparators.reverseNaturalOrder())));
    }

    @Override
    @Test
    public void newWith()
    {
        Assertions.assertEquals(UnifiedSet.newSetWith(1), this.classUnderTest().newWith(1));
        Assertions.assertSame(SortedSets.immutable.empty(), SortedSets.immutable.of(FastList.newList().toArray()));
        Assertions.assertEquals(SortedSets.immutable.empty(),
                SortedSets.immutable.of(Comparators.naturalOrder(), FastList.newList().toArray()));

        Assertions.assertEquals(Comparators.<Integer>reverseNaturalOrder(),
                this.classUnderTest(Comparators.<Integer>reverseNaturalOrder()).newWith(1).comparator());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Comparators.<Integer>reverseNaturalOrder(), 1, 2),
                this.classUnderTest(Comparators.<Integer>reverseNaturalOrder()).newWith(1).newWith(2).castToSortedSet());
    }

    @Override
    @Test
    public void newWithout()
    {
        Assertions.assertEquals(SortedSets.immutable.empty(), SortedSets.immutable.empty().newWithout(1));
        Assertions.assertSame(SortedSets.immutable.empty(), SortedSets.immutable.empty().newWithout(1));
        Assertions.assertEquals(SortedSets.immutable.empty(), SortedSets.immutable.empty().newWithoutAll(Interval.oneTo(3)));
        Assertions.assertSame(SortedSets.immutable.empty(), SortedSets.immutable.empty().newWithoutAll(Interval.oneTo(3)));

        Assertions.assertEquals(Comparators.<Integer>reverseNaturalOrder(),
                this.classUnderTest(Comparators.<Integer>reverseNaturalOrder()).newWithout(1).comparator());
    }

    @Override
    @Test
    public void detect()
    {
        Assertions.assertNull(this.classUnderTest().detect(Integer.valueOf(1)::equals));
    }

    @Override
    @Test
    public void detectWith()
    {
        Assertions.assertNull(this.classUnderTest().detectWith(Object::equals, Integer.valueOf(1)));
    }

    @Override
    @Test
    public void detectIndex()
    {
        //Any predicate will result in -1
        Assertions.assertEquals(Integer.valueOf(-1), Integer.valueOf(this.classUnderTest().detectIndex(Predicates.alwaysTrue())));
    }

    @Override
    @Test
    public void corresponds()
    {
        //Evaluates true for all empty sets and false for all non-empty sets

        ImmutableSortedSet<Integer> integers1 = this.classUnderTest();
        Assertions.assertTrue(integers1.corresponds(Lists.mutable.of(), Predicates2.alwaysFalse()));

        ImmutableSortedSet<Integer> integers2 = integers1.newWith(Integer.valueOf(1));
        Assertions.assertFalse(integers2.corresponds(integers1, Predicates2.alwaysTrue()));
    }

    @Override
    @Test
    public void allSatisfy()
    {
        Assertions.assertTrue(this.classUnderTest().allSatisfy(Integer.class::isInstance));
    }

    @Override
    @Test
    public void anySatisfy()
    {
        Assertions.assertFalse(this.classUnderTest().anySatisfy(Integer.class::isInstance));
    }

    @Override
    @Test
    public void getFirst()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.classUnderTest().getFirst();
        });
    }

    @Override
    @Test
    public void getLast()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.classUnderTest().getLast();
        });
    }

    @Override
    @Test
    public void isEmpty()
    {
        Verify.assertIterableEmpty(this.classUnderTest());
        Assertions.assertFalse(this.classUnderTest().notEmpty());
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
        ImmutableSortedSet<Integer> immutableSet = this.classUnderTest(Comparators.reverseNaturalOrder());
        ImmutableList<Pair<Integer, Integer>> pairs = immutableSet.zip(Interval.oneTo(10));
        Assertions.assertEquals(FastList.<Pair<Integer, Integer>>newList(), pairs);

        Assertions.assertEquals(
                UnifiedSet.<Pair<Integer, Integer>>newSet(),
                immutableSet.zip(Interval.oneTo(10), UnifiedSet.<Pair<Integer, Integer>>newSet()));

        ImmutableList<Pair<Integer, Integer>> pairsWithExtras = pairs.newWith(Tuples.pair(1, 5)).newWith(Tuples.pair(5, 1));
        Assertions.assertEquals(FastList.newListWith(Tuples.pair(1, 5), Tuples.pair(5, 1)), pairsWithExtras.toList());
    }

    @Override
    @Test
    public void zipWithIndex()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        ImmutableSortedSet<Pair<Integer, Integer>> pairs = set.zipWithIndex();

        Assertions.assertEquals(UnifiedSet.<Pair<Integer, Integer>>newSet(), pairs);

        Assertions.assertEquals(
                UnifiedSet.<Pair<Integer, Integer>>newSet(),
                set.zipWithIndex(UnifiedSet.<Pair<Integer, Integer>>newSet()));

        Assertions.assertNotNull(pairs.comparator());
        ImmutableSortedSet<Pair<Integer, Integer>> pairsWithExtras = pairs.newWith(Tuples.pair(1, 5)).newWith(Tuples.pair(5, 1));
        Assertions.assertEquals(FastList.newListWith(Tuples.pair(1, 5), Tuples.pair(5, 1)), pairsWithExtras.toList());
    }

    @Test
    public void chunk()
    {
        Verify.assertIterableEmpty(this.classUnderTest().chunk(2));
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
        Verify.assertIterableEmpty(this.classUnderTest().chunk(10));
    }

    @Override
    @Test
    public void union()
    {
        Verify.assertSortedSetsEqual(
                TreeSortedSet.newSetWith("a", "b", "c"),
                SortedSets.immutable.<String>empty().union(UnifiedSet.newSetWith("a", "b", "c")).castToSortedSet());

        Verify.assertListsEqual(FastList.newListWith(3, 2, 1),
                this.classUnderTest(Comparators.<Integer>reverseNaturalOrder()).union(UnifiedSet.newSetWith(1, 2, 3)).toList());
    }

    @Override
    @Test
    public void unionInto()
    {
        Assertions.assertEquals(
                UnifiedSet.newSetWith("a", "b", "c"),
                SortedSets.immutable.<String>empty().unionInto(UnifiedSet.newSetWith("a", "b", "c"), UnifiedSet.<String>newSet()));
    }

    @Override
    @Test
    public void intersect()
    {
        Assertions.assertEquals(
                UnifiedSet.<String>newSet(),
                SortedSets.immutable.<String>empty().intersect(UnifiedSet.newSetWith("1", "2", "3")));

        Assertions.assertEquals(Comparators.<Integer>reverseNaturalOrder(),
                this.classUnderTest(Comparators.<Integer>reverseNaturalOrder()).intersect(UnifiedSet.newSetWith(1, 2, 3)).comparator());
    }

    @Override
    @Test
    public void intersectInto()
    {
        Assertions.assertEquals(
                UnifiedSet.<String>newSet(),
                SortedSets.immutable.<String>empty().intersectInto(UnifiedSet.newSetWith("1", "2", "3"), UnifiedSet.<String>newSet()));
    }

    @Override
    @Test
    public void difference()
    {
        Assertions.assertEquals(
                UnifiedSet.<String>newSet(),
                SortedSets.immutable.<String>empty().difference(UnifiedSet.newSetWith("not present")));

        Assertions.assertEquals(Comparators.<Integer>reverseNaturalOrder(),
                this.classUnderTest(Comparators.<Integer>reverseNaturalOrder()).difference(UnifiedSet.newSetWith(1, 2, 3)).comparator());
    }

    @Override
    @Test
    public void differenceInto()
    {
        ImmutableSortedSet<String> set = SortedSets.immutable.empty();
        Assertions.assertEquals(
                UnifiedSet.<String>newSet(),
                set.differenceInto(UnifiedSet.newSetWith("not present"), UnifiedSet.<String>newSet()));

        Verify.assertListsEqual(FastList.newListWith(3, 2, 1),
                this.classUnderTest(Comparators.<Integer>reverseNaturalOrder()).union(UnifiedSet.newSetWith(1, 2, 3)).toList());
    }

    @Override
    @Test
    public void symmetricDifference()
    {
        Assertions.assertEquals(
                UnifiedSet.newSetWith("not present"),
                SortedSets.immutable.<String>empty().symmetricDifference(UnifiedSet.newSetWith("not present")));

        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Comparators.<Integer>reverseNaturalOrder(), 1, 2, 3, 4),
                SortedSets.immutable.of(Comparators.<Integer>reverseNaturalOrder()).symmetricDifference(UnifiedSet.newSetWith(1, 3, 2, 4)).castToSortedSet());
    }

    @Override
    @Test
    public void symmetricDifferenceInto()
    {
        Assertions.assertEquals(
                UnifiedSet.newSetWith("not present"),
                SortedSets.immutable.<String>empty().symmetricDifferenceInto(UnifiedSet.newSetWith("not present"), UnifiedSet.<String>newSet()));
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        ImmutableSortedSet<Integer> immutable = this.classUnderTest();
        Verify.assertEqualsAndHashCode(UnifiedSet.newSet(), immutable);
        Verify.assertPostSerializedIdentity(immutable);
        Assertions.assertNotEquals(Lists.mutable.empty(), immutable);

        ImmutableSortedSet<Integer> setWithComparator = this.classUnderTest(Comparators.reverseNaturalOrder());
        Verify.assertEqualsAndHashCode(UnifiedSet.newSet(), setWithComparator);
        Verify.assertPostSerializedEqualsAndHashCode(setWithComparator);
    }

    @Override
    @Test
    public void contains()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        Verify.assertNotContains(Integer.valueOf(1), set.castToSortedSet());
        Verify.assertEmpty(set.castToSortedSet());
        Verify.assertThrows(NullPointerException.class, () -> set.contains(null));
    }

    @Override
    @Test
    public void containsAllIterable()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        Assertions.assertFalse(set.containsAllIterable(FastList.newListWith(1, 2, 3)));
        Assertions.assertTrue(set.containsAllIterable(set));
    }

    @Override
    @Test
    public void iterator()
    {
        Iterator<Integer> iterator = this.classUnderTest().iterator();
        Assertions.assertFalse(iterator.hasNext());
        Verify.assertThrows(NoSuchElementException.class, (Runnable) iterator::next);
    }

    @Override
    @Test
    public void select()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertEmpty(set.castToSortedSet());
        Verify.assertEmpty(set.select(Predicates.lessThan(4)).castToSortedSet());
        Assertions.assertEquals(Collections.<Integer>reverseOrder(), set.select(Predicates.lessThan(3)).comparator());
    }

    @Override
    @Test
    public void selectWith()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertEmpty(integers.selectWith(Predicates2.<Integer>lessThan(), 4).castToSortedSet());
        Assertions.assertEquals(Collections.<Integer>reverseOrder(), integers.selectWith(Predicates2.<Integer>lessThan(), 3).comparator());
    }

    @Override
    @Test
    public void reject()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertEmpty(set.castToSortedSet());
        Verify.assertEmpty(set.reject(Predicates.lessThan(3)).castToSortedSet());
        Assertions.assertEquals(Collections.<Integer>reverseOrder(), set.reject(Predicates.lessThan(3)).comparator());
    }

    @Override
    @Test
    public void rejectWith()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertEmpty(integers.rejectWith(Predicates2.<Integer>greaterThanOrEqualTo(), 4).castToSortedSet());
        Assertions.assertEquals(Collections.<Integer>reverseOrder(), integers.rejectWith(Predicates2.<Integer>greaterThanOrEqualTo(), 4).comparator());
    }

    @Override
    @Test
    public void partition()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertEmpty(set.castToSortedSet());
        PartitionImmutableSortedSet<Integer> partition = set.partition(Predicates.lessThan(4));
        Verify.assertIterableEmpty(partition.getSelected());
        Verify.assertIterableEmpty(partition.getRejected());
        Assertions.assertEquals(Collections.<Integer>reverseOrder(), partition.getSelected().comparator());
        Assertions.assertEquals(Collections.<Integer>reverseOrder(), partition.getRejected().comparator());
    }

    @Override
    @Test
    public void partitionWith()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertEmpty(set.castToSortedSet());
        PartitionImmutableSortedSet<Integer> partition = set.partitionWith(Predicates2.<Integer>lessThan(), 4);
        Verify.assertIterableEmpty(partition.getSelected());
        Verify.assertIterableEmpty(partition.getRejected());
        Assertions.assertEquals(Collections.<Integer>reverseOrder(), partition.getSelected().comparator());
        Assertions.assertEquals(Collections.<Integer>reverseOrder(), partition.getRejected().comparator());
    }

    @Override
    @Test
    public void collect()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertIterableEmpty(integers.collect(Functions.getIntegerPassThru()));
    }

    @Override
    @Test
    public void collectWith()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertIterableEmpty(integers.collectWith((value, parameter) -> value / parameter, 1));
    }

    @Override
    @Test
    public void collectToTarget()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest();
        ImmutableSortedSet<Integer> collect = integers.collect(Functions.getIntegerPassThru(), TreeSortedSet.newSet(Collections.<Integer>reverseOrder())).toImmutable();
        Verify.assertIterableEmpty(collect);
        Assertions.assertEquals(Collections.<Integer>reverseOrder(), collect.comparator());
    }

    @Override
    @Test
    public void partitionWhile()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertEmpty(set.castToSortedSet());
        PartitionImmutableSortedSet<Integer> partition = set.partitionWhile(Predicates.lessThan(4));
        Verify.assertIterableEmpty(partition.getSelected());
        Verify.assertIterableEmpty(partition.getRejected());
        Assertions.assertEquals(Collections.<Integer>reverseOrder(), partition.getSelected().comparator());
        Assertions.assertEquals(Collections.<Integer>reverseOrder(), partition.getRejected().comparator());
    }

    @Override
    @Test
    public void takeWhile()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertEmpty(set.castToSortedSet());
        ImmutableSortedSet<Integer> take = set.takeWhile(Predicates.lessThan(4));
        Verify.assertIterableEmpty(take);
        Assertions.assertEquals(Collections.<Integer>reverseOrder(), take.comparator());
    }

    @Override
    @Test
    public void dropWhile()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertEmpty(set.castToSortedSet());
        ImmutableSortedSet<Integer> drop = set.dropWhile(Predicates.lessThan(4));
        Verify.assertIterableEmpty(drop);
        Assertions.assertEquals(Collections.<Integer>reverseOrder(), drop.comparator());
    }

    @Override
    @Test
    public void selectInstancesOf()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.<Integer>reverseOrder());
        Verify.assertEmpty(set.castToSortedSet());
        Verify.assertEmpty(set.selectInstancesOf(Integer.class).castToSortedSet());
        Assertions.assertEquals(Collections.<Double>reverseOrder(), set.selectInstancesOf(Double.class).comparator());
    }

    @Override
    @Test
    public void toSortedSet()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Collections.<Integer>reverseOrder());
        Assertions.assertNull(set.toSortedSet().comparator());
        Verify.assertEmpty(set.toSortedSet());
    }

    @Override
    @Test
    public void toSortedSetWithComparator()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest();
        Assertions.assertEquals(Collections.<Integer>reverseOrder(), set.toSortedSet(Collections.<Integer>reverseOrder()).comparator());
        Verify.assertEmpty(set.toSortedSet());
    }

    @Test
    public void first()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.classUnderTest().castToSortedSet().first();
        });
    }

    @Test
    public void last()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.classUnderTest().castToSortedSet().last();
        });
    }

    @Test
    public void compareTo()
    {
        Assertions.assertEquals(0, (long) this.classUnderTest().compareTo(this.classUnderTest()));
        Assertions.assertEquals(-1, this.classUnderTest().compareTo(TreeSortedSet.newSetWith(1)));
        Assertions.assertEquals(-4, this.classUnderTest().compareTo(TreeSortedSet.newSetWith(1, 2, 3, 4)));
    }

    @Override
    @Test
    public void cartesianProduct()
    {
        LazyIterable<Pair<Integer, Integer>> emptyProduct = this.classUnderTest().cartesianProduct(SortedSets.immutable.of(1, 2, 3));
        Verify.assertEmpty(emptyProduct.toList());

        LazyIterable<Pair<Integer, Integer>> empty2 = this.classUnderTest().cartesianProduct(TreeSortedSet.<Integer>newSet());
        Verify.assertEmpty(empty2.toList());
    }

    @Override
    @Test
    public void indexOf()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Comparators.<Integer>reverseNaturalOrder());
        Assertions.assertEquals(-1, integers.indexOf(4));
        Assertions.assertEquals(-1, integers.indexOf(3));
        Assertions.assertEquals(-1, integers.indexOf(2));
        Assertions.assertEquals(-1, integers.indexOf(1));
        Assertions.assertEquals(-1, integers.indexOf(0));
        Assertions.assertEquals(-1, integers.indexOf(5));
    }

    @Override
    @Test
    public void forEachFromTo()
    {
        MutableSortedSet<Integer> result = TreeSortedSet.<Integer>newSet(Comparators.reverseNaturalOrder());
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Comparators.<Integer>reverseNaturalOrder());
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> integers.forEach(-1, 0, result::add));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> integers.forEach(0, -1, result::add));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> integers.forEach(0, 0, result::add));
    }

    @Override
    @Test
    public void forEachWithIndexWithFromTo()
    {
        MutableList<Integer> result = Lists.mutable.empty();
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Comparators.<Integer>reverseNaturalOrder());

        Verify.assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex(-1, 0, new AddToList(result)));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex(0, -1, new AddToList(result)));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex(0, 0, new AddToList(result)));
    }

    @Override
    @Test
    public void toStack()
    {
        ImmutableSortedSet<Integer> set = this.classUnderTest(Comparators.reverseNaturalOrder());
        Assertions.assertEquals(Stacks.mutable.with(), set.toStack());
    }

    @Override
    @Test
    public void powerSet()
    {
        Verify.assertSize(1, this.classUnderTest().powerSet().castToSortedSet());
        Assertions.assertEquals(SortedSets.immutable.of(SortedSets.immutable.<Integer>empty()), this.classUnderTest().powerSet());
    }

    @Override
    @Test
    public void toSortedMap()
    {
        MutableSortedMap<Integer, Integer> map = this.classUnderTest().toSortedMap(Functions.getIntegerPassThru(), Functions.getIntegerPassThru());
        Verify.assertEmpty(map);
        Verify.assertInstanceOf(TreeSortedMap.class, map);
    }

    @Override
    @Test
    public void toSortedMap_with_comparator()
    {
        MutableSortedMap<Integer, Integer> map = this.classUnderTest().toSortedMap(Comparators.<Integer>reverseNaturalOrder(),
                Functions.getIntegerPassThru(), Functions.getIntegerPassThru());
        Verify.assertEmpty(map);
        Verify.assertInstanceOf(TreeSortedMap.class, map);
        Assertions.assertEquals(Comparators.<Integer>reverseNaturalOrder(), map.comparator());
    }

    @Override
    @Test
    public void subSet()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.classUnderTest().castToSortedSet().subSet(1, 4);
        });
    }

    @Override
    @Test
    public void headSet()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.classUnderTest().castToSortedSet().headSet(4);
        });
    }

    @Override
    @Test
    public void tailSet()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.classUnderTest().castToSortedSet().tailSet(1);
        });
    }

    @Override
    @Test
    public void collectBoolean()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Assertions.assertEquals(BooleanArrayList.newListWith(), integers.collectBoolean(PrimitiveFunctions.integerIsPositive()));
    }

    @Override
    @Test
    public void collectByte()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Assertions.assertEquals(ByteArrayList.newListWith(), integers.collectByte(PrimitiveFunctions.unboxIntegerToByte()));
    }

    @Override
    @Test
    public void collectChar()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Assertions.assertEquals(CharArrayList.newListWith(), integers.collectChar(integer -> (char) (integer.intValue() + 64)));
    }

    @Override
    @Test
    public void collectDouble()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Assertions.assertEquals(DoubleArrayList.newListWith(), integers.collectDouble(PrimitiveFunctions.unboxIntegerToDouble()));
    }

    @Override
    @Test
    public void collectFloat()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Assertions.assertEquals(FloatArrayList.newListWith(), integers.collectFloat(PrimitiveFunctions.unboxIntegerToFloat()));
    }

    @Override
    @Test
    public void collectInt()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Assertions.assertEquals(IntArrayList.newListWith(), integers.collectInt(PrimitiveFunctions.unboxIntegerToInt()));
    }

    @Override
    @Test
    public void collectLong()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Assertions.assertEquals(LongArrayList.newListWith(), integers.collectLong(PrimitiveFunctions.unboxIntegerToLong()));
    }

    @Override
    @Test
    public void collectShort()
    {
        ImmutableSortedSet<Integer> integers = this.classUnderTest(Collections.<Integer>reverseOrder());
        Assertions.assertEquals(ShortArrayList.newListWith(), integers.collectShort(PrimitiveFunctions.unboxIntegerToShort()));
    }

    @Override
    @Test
    public void groupByUniqueKey_throws()
    {
        super.groupByUniqueKey_throws();
        Assertions.assertEquals(UnifiedMap.newMap().toImmutable(), this.classUnderTest().groupByUniqueKey(id -> id));
    }

    @Override
    @Test
    public void groupByUniqueKey_target_throws()
    {
        super.groupByUniqueKey_target_throws();
        Assertions.assertEquals(UnifiedMap.newMap(), this.classUnderTest().groupByUniqueKey(id -> id, UnifiedMap.<Integer, Integer>newMap()));
    }

    @Override
    @Test
    public void take()
    {
        Assertions.assertEquals(this.classUnderTest(), this.classUnderTest().take(2));
    }

    @Override
    @Test
    public void drop()
    {
        Assertions.assertEquals(this.classUnderTest(), this.classUnderTest().drop(2));
    }
}
