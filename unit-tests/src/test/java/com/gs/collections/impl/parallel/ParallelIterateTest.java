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

package com.gs.collections.impl.parallel;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.gs.collections.api.LazyIterable;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.bag.Bag;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.block.procedure.Procedure2;
import com.gs.collections.api.block.procedure.primitive.ObjectIntProcedure;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MapIterable;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.map.primitive.ObjectDoubleMap;
import com.gs.collections.api.map.primitive.ObjectLongMap;
import com.gs.collections.api.multimap.Multimap;
import com.gs.collections.api.multimap.MutableMultimap;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Procedures;
import com.gs.collections.impl.block.factory.StringFunctions;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.ArrayListAdapter;
import com.gs.collections.impl.list.mutable.CompositeFastList;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.ListAdapter;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.multimap.bag.HashBagMultimap;
import com.gs.collections.impl.multimap.bag.SynchronizedPutHashBagMultimap;
import com.gs.collections.impl.multimap.set.SynchronizedPutUnifiedSetMultimap;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.utility.ArrayIterate;
import com.gs.collections.impl.utility.Iterate;
import com.gs.collections.impl.utility.LazyIterate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ParallelIterateTest
{
    private static final Procedure<Integer> EXCEPTION_PROCEDURE = value -> {
        throw new RuntimeException("Thread death on its way!");
    };

    private static final ObjectIntProcedure<Integer> EXCEPTION_OBJECT_INT_PROCEDURE = (object, index) -> {
        throw new RuntimeException("Thread death on its way!");
    };

    private static final Function<Integer, Collection<String>> INT_TO_TWO_STRINGS = integer -> Lists.fixedSize.of(integer.toString(), integer.toString());

    private static final Function<Integer, String> EVEN_OR_ODD = value -> value % 2 == 0 ? "Even" : "Odd";
    private static final Function<BigDecimal, String> EVEN_OR_ODD_BD = value -> value.intValue() % 2 == 0 ? "Even" : "Odd";
    private static final Function<BigInteger, String> EVEN_OR_ODD_BI = value -> value.intValue() % 2 == 0 ? "Even" : "Odd";
    private static final int UNEVEN_COUNT_FOR_SUMBY = 43957;

    private ImmutableList<RichIterable<Integer>> iterables;
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    @BeforeEach
    public void setUp()
    {
        Interval interval = Interval.oneTo(200);
        this.iterables = Lists.immutable.of(
                interval.toList(),
                interval.toList().asUnmodifiable(),
                interval.toList().asSynchronized(),
                interval.toList().toImmutable(),
                interval.toSet(),
                interval.toSet().asUnmodifiable(),
                interval.toSet().asSynchronized(),
                interval.toSet().toImmutable(),
                interval.toBag(),
                interval.toBag().asUnmodifiable(),
                interval.toBag().asSynchronized(),
                interval.toBag().toImmutable(),
                interval.toSortedSet(),
                interval.toSortedSet().asUnmodifiable(),
                interval.toSortedSet().asSynchronized(),
                interval.toSortedSet().toImmutable(),
                interval.toMap(Functions.<Integer>identity(), Functions.<Integer>identity()),
                interval.toMap(Functions.<Integer>identity(), Functions.<Integer>identity()).asUnmodifiable(),
                interval.toMap(Functions.<Integer>identity(), Functions.<Integer>identity()).asSynchronized(),
                interval.toMap(Functions.<Integer>identity(), Functions.<Integer>identity()).toImmutable(),
                new CompositeFastList<Integer>().withAll(interval.toList()),
                new CompositeFastList<Integer>().withAll(interval.toList()).asUnmodifiable(),
                new CompositeFastList<Integer>().withAll(interval.toList()).asSynchronized(),
                ArrayListAdapter.<Integer>newList().withAll(interval),
                ArrayListAdapter.<Integer>newList().withAll(interval).asUnmodifiable(),
                ArrayListAdapter.<Integer>newList().withAll(interval).asSynchronized(),
                ListAdapter.adapt(new LinkedList<Integer>()).withAll(interval),
                ListAdapter.adapt(new LinkedList<Integer>()).withAll(interval).asUnmodifiable(),
                ListAdapter.adapt(new LinkedList<Integer>()).withAll(interval).asSynchronized(),
                UnifiedSetWithHashingStrategy.<Integer>newSet(HashingStrategies.defaultStrategy()).withAll(interval),
                UnifiedSetWithHashingStrategy.<Integer>newSet(HashingStrategies.defaultStrategy()).withAll(interval).asUnmodifiable(),
                UnifiedSetWithHashingStrategy.<Integer>newSet(HashingStrategies.defaultStrategy()).withAll(interval).asSynchronized(),
                UnifiedSetWithHashingStrategy.<Integer>newSet(HashingStrategies.defaultStrategy()).withAll(interval).toImmutable());
    }

    @AfterEach
    public void tearDown()
    {
        this.executor.shutdown();
    }

    @Test
    public void testForEachUsingSet()
    {
        //Tests the default batch size calculations
        IntegerSum sum = new IntegerSum(0);
        MutableSet<Integer> set = Interval.toSet(1, 100);
        ParallelIterate.forEach(set, new SumProcedure(sum), new SumCombiner(sum));
        Assertions.assertEquals(5050, sum.getSum());

        //Testing batch size 1
        IntegerSum sum2 = new IntegerSum(0);
        UnifiedSet<Integer> set2 = UnifiedSet.newSet(Interval.oneTo(100));
        ParallelIterate.forEach(set2, new SumProcedure(sum2), new SumCombiner(sum2), 1, set2.getBatchCount(set2.size()));
        Assertions.assertEquals(5050, sum2.getSum());

        //Testing an uneven batch size
        IntegerSum sum3 = new IntegerSum(0);
        UnifiedSet<Integer> set3 = UnifiedSet.newSet(Interval.oneTo(100));
        ParallelIterate.forEach(set3, new SumProcedure(sum3), new SumCombiner(sum3), 1, set3.getBatchCount(13));
        Assertions.assertEquals(5050, sum3.getSum());

        //Testing divideByZero exception by passing 1 as batchSize
        IntegerSum sum4 = new IntegerSum(0);
        UnifiedSet<Integer> set4 = UnifiedSet.newSet(Interval.oneTo(100));
        ParallelIterate.forEach(set4, new SumProcedure(sum4), new SumCombiner(sum4), 1);
        Assertions.assertEquals(5050, sum4.getSum());
    }

    @Test
    public void testForEachUsingMap()
    {
        //Test the default batch size calculations
        IntegerSum sum1 = new IntegerSum(0);
        MutableMap<String, Integer> map1 = Interval.fromTo(1, 100).toMap(String::valueOf, Functions.getIntegerPassThru());
        ParallelIterate.forEach(map1, new SumProcedure(sum1), new SumCombiner(sum1));
        Assertions.assertEquals(5050, sum1.getSum());

        //Testing batch size 1
        IntegerSum sum2 = new IntegerSum(0);
        UnifiedMap<String, Integer> map2 = (UnifiedMap<String, Integer>) Interval.fromTo(1, 100).toMap(String::valueOf, Functions.getIntegerPassThru());
        ParallelIterate.forEach(map2, new SumProcedure(sum2), new SumCombiner(sum2), 1, map2.getBatchCount(map2.size()));
        Assertions.assertEquals(5050, sum2.getSum());

        //Testing an uneven batch size
        IntegerSum sum3 = new IntegerSum(0);
        UnifiedMap<String, Integer> set3 = (UnifiedMap<String, Integer>) Interval.fromTo(1, 100).toMap(String::valueOf, Functions.getIntegerPassThru());
        ParallelIterate.forEach(set3, new SumProcedure(sum3), new SumCombiner(sum3), 1, set3.getBatchCount(13));
        Assertions.assertEquals(5050, sum3.getSum());
    }

    @Test
    public void testForEach()
    {
        IntegerSum sum1 = new IntegerSum(0);
        List<Integer> list1 = ParallelIterateTest.createIntegerList(16);
        ParallelIterate.forEach(list1, new SumProcedure(sum1), new SumCombiner(sum1), 1, list1.size() / 2);
        Assertions.assertEquals(16, sum1.getSum());

        IntegerSum sum2 = new IntegerSum(0);
        List<Integer> list2 = ParallelIterateTest.createIntegerList(7);
        ParallelIterate.forEach(list2, new SumProcedure(sum2), new SumCombiner(sum2));
        Assertions.assertEquals(7, sum2.getSum());

        IntegerSum sum3 = new IntegerSum(0);
        List<Integer> list3 = ParallelIterateTest.createIntegerList(15);
        ParallelIterate.forEach(list3, new SumProcedure(sum3), new SumCombiner(sum3), 1, list3.size() / 2);
        Assertions.assertEquals(15, sum3.getSum());

        IntegerSum sum4 = new IntegerSum(0);
        List<Integer> list4 = ParallelIterateTest.createIntegerList(35);
        ParallelIterate.forEach(list4, new SumProcedure(sum4), new SumCombiner(sum4));
        Assertions.assertEquals(35, sum4.getSum());

        IntegerSum sum5 = new IntegerSum(0);
        MutableList<Integer> list5 = FastList.newList(list4);
        ParallelIterate.forEach(list5, new SumProcedure(sum5), new SumCombiner(sum5));
        Assertions.assertEquals(35, sum5.getSum());

        IntegerSum sum6 = new IntegerSum(0);
        List<Integer> list6 = ParallelIterateTest.createIntegerList(40);
        ParallelIterate.forEach(list6, new SumProcedure(sum6), new SumCombiner(sum6), 1, list6.size() / 2);
        Assertions.assertEquals(40, sum6.getSum());

        IntegerSum sum7 = new IntegerSum(0);
        MutableList<Integer> list7 = FastList.newList(list6);
        ParallelIterate.forEach(list7, new SumProcedure(sum7), new SumCombiner(sum7), 1, list6.size() / 2);
        Assertions.assertEquals(40, sum7.getSum());
    }

    @Test
    public void testForEachImmutable()
    {
        IntegerSum sum1 = new IntegerSum(0);
        ImmutableList<Integer> list1 = Lists.immutable.ofAll(ParallelIterateTest.createIntegerList(16));
        ParallelIterate.forEach(list1, new SumProcedure(sum1), new SumCombiner(sum1), 1, list1.size() / 2);
        Assertions.assertEquals(16, sum1.getSum());

        IntegerSum sum2 = new IntegerSum(0);
        ImmutableList<Integer> list2 = Lists.immutable.ofAll(ParallelIterateTest.createIntegerList(7));
        ParallelIterate.forEach(list2, new SumProcedure(sum2), new SumCombiner(sum2));
        Assertions.assertEquals(7, sum2.getSum());

        IntegerSum sum3 = new IntegerSum(0);
        ImmutableList<Integer> list3 = Lists.immutable.ofAll(ParallelIterateTest.createIntegerList(15));
        ParallelIterate.forEach(list3, new SumProcedure(sum3), new SumCombiner(sum3), 1, list3.size() / 2);
        Assertions.assertEquals(15, sum3.getSum());

        IntegerSum sum4 = new IntegerSum(0);
        ImmutableList<Integer> list4 = Lists.immutable.ofAll(ParallelIterateTest.createIntegerList(35));
        ParallelIterate.forEach(list4, new SumProcedure(sum4), new SumCombiner(sum4));
        Assertions.assertEquals(35, sum4.getSum());

        IntegerSum sum5 = new IntegerSum(0);
        ImmutableList<Integer> list5 = FastList.newList(list4).toImmutable();
        ParallelIterate.forEach(list5, new SumProcedure(sum5), new SumCombiner(sum5));
        Assertions.assertEquals(35, sum5.getSum());

        IntegerSum sum6 = new IntegerSum(0);
        ImmutableList<Integer> list6 = Lists.immutable.ofAll(ParallelIterateTest.createIntegerList(40));
        ParallelIterate.forEach(list6, new SumProcedure(sum6), new SumCombiner(sum6), 1, list6.size() / 2);
        Assertions.assertEquals(40, sum6.getSum());

        IntegerSum sum7 = new IntegerSum(0);
        ImmutableList<Integer> list7 = FastList.newList(list6).toImmutable();
        ParallelIterate.forEach(list7, new SumProcedure(sum7), new SumCombiner(sum7), 1, list6.size() / 2);
        Assertions.assertEquals(40, sum7.getSum());
    }

    @Test
    public void testForEachWithException()
    {
        Verify.assertThrows(
                RuntimeException.class,
                () -> ParallelIterate.forEach(
                        ParallelIterateTest.createIntegerList(5),
                        new PassThruProcedureFactory<>(EXCEPTION_PROCEDURE),
                        new PassThruCombiner<>(),
                        1,
                        5));
    }

    @Test
    public void testForEachWithIndexToArrayUsingFastListSerialPath()
    {
        Integer[] array = new Integer[200];
        FastList<Integer> list = (FastList<Integer>) Interval.oneTo(200).toList();
        Assertions.assertTrue(ArrayIterate.allSatisfy(array, Predicates.isNull()));
        ParallelIterate.forEachWithIndex(list, (each, index) -> array[index] = each);
        Assertions.assertArrayEquals(array, list.toArray(new Integer[]{}));
    }

    @Test
    public void testForEachWithIndexToArrayUsingFastList()
    {
        Integer[] array = new Integer[200];
        FastList<Integer> list = (FastList<Integer>) Interval.oneTo(200).toList();
        Assertions.assertTrue(ArrayIterate.allSatisfy(array, Predicates.isNull()));
        ParallelIterate.forEachWithIndex(list, (each, index) -> array[index] = each, 10, 10);
        Assertions.assertArrayEquals(array, list.toArray(new Integer[]{}));
    }

    @Test
    public void testForEachWithIndexToArrayUsingImmutableList()
    {
        Integer[] array = new Integer[200];
        ImmutableList<Integer> list = Interval.oneTo(200).toList().toImmutable();
        Assertions.assertTrue(ArrayIterate.allSatisfy(array, Predicates.isNull()));
        ParallelIterate.forEachWithIndex(list, (each, index) -> array[index] = each, 10, 10);
        Assertions.assertArrayEquals(array, list.toArray(new Integer[]{}));
    }

    @Test
    public void testForEachWithIndexToArrayUsingArrayList()
    {
        Integer[] array = new Integer[200];
        List<Integer> list = new ArrayList<>(Interval.oneTo(200));
        Assertions.assertTrue(ArrayIterate.allSatisfy(array, Predicates.isNull()));
        ParallelIterate.forEachWithIndex(list, (each, index) -> array[index] = each, 10, 10);
        Assertions.assertArrayEquals(array, list.toArray(new Integer[]{}));
    }

    @Test
    public void testForEachWithIndexToArrayUsingFixedArrayList()
    {
        Integer[] array = new Integer[10];
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Assertions.assertTrue(ArrayIterate.allSatisfy(array, Predicates.isNull()));
        ParallelIterate.forEachWithIndex(list, (each, index) -> array[index] = each, 1, 2);
        Assertions.assertArrayEquals(array, list.toArray(new Integer[list.size()]));
    }

    @Test
    public void testForEachWithIndexException()
    {
        Verify.assertThrows(
                RuntimeException.class,
                () -> ParallelIterate.forEachWithIndex(
                        ParallelIterateTest.createIntegerList(5),
                        new PassThruObjectIntProcedureFactory<>(EXCEPTION_OBJECT_INT_PROCEDURE),
                        new PassThruCombiner<>(),
                        1,
                        5));
    }

    @Test
    public void select()
    {
        this.iterables.forEach(Procedures.cast(this::basicSelect));
    }

    private void basicSelect(RichIterable<Integer> iterable)
    {
        Collection<Integer> actual1 = ParallelIterate.select(iterable, Predicates.greaterThan(100));
        Collection<Integer> actual2 = ParallelIterate.select(iterable, Predicates.greaterThan(100), HashBag.<Integer>newBag(), 3, this.executor, true);
        Collection<Integer> actual3 = ParallelIterate.select(iterable, Predicates.greaterThan(100), true);
        RichIterable<Integer> expected = iterable.select(Predicates.greaterThan(100));
        Assertions.assertEquals(expected, actual1, expected.getClass().getSimpleName() + '/' + actual1.getClass().getSimpleName());
        Assertions.assertEquals(expected.toBag(), actual2, expected.getClass().getSimpleName() + '/' + actual2.getClass().getSimpleName());
        Assertions.assertEquals(expected, actual3, expected.getClass().getSimpleName() + '/' + actual3.getClass().getSimpleName());
    }

    @Test
    public void selectSortedSet()
    {
        RichIterable<Integer> iterable = Interval.oneTo(200).toSortedSet();
        Collection<Integer> actual1 = ParallelIterate.select(iterable, Predicates.greaterThan(100));
        Collection<Integer> actual2 = ParallelIterate.select(iterable, Predicates.greaterThan(100), true);
        RichIterable<Integer> expected = iterable.select(Predicates.greaterThan(100));
        Assertions.assertSame(expected.getClass(), actual1.getClass());
        Assertions.assertSame(expected.getClass(), actual2.getClass());
        Assertions.assertEquals(expected, actual1, expected.getClass().getSimpleName() + '/' + actual1.getClass().getSimpleName());
        Assertions.assertEquals(expected, actual2, expected.getClass().getSimpleName() + '/' + actual2.getClass().getSimpleName());
    }

    @Test
    public void count()
    {
        this.iterables.forEach(Procedures.cast(this::basicCount));
    }

    private void basicCount(RichIterable<Integer> iterable)
    {
        int actual1 = ParallelIterate.count(iterable, Predicates.greaterThan(100));
        int actual2 = ParallelIterate.count(iterable, Predicates.greaterThan(100), 6, this.executor);
        Assertions.assertEquals(100, actual1);
        Assertions.assertEquals(100, actual2);
    }

    @Test
    public void reject()
    {
        this.iterables.forEach(Procedures.cast(this::basicReject));
    }

    private void basicReject(RichIterable<Integer> iterable)
    {
        Collection<Integer> actual1 = ParallelIterate.reject(iterable, Predicates.greaterThan(100));
        Collection<Integer> actual2 = ParallelIterate.reject(iterable, Predicates.greaterThan(100), HashBag.<Integer>newBag(), 3, this.executor, true);
        Collection<Integer> actual3 = ParallelIterate.reject(iterable, Predicates.greaterThan(100), true);
        RichIterable<Integer> expected = iterable.reject(Predicates.greaterThan(100));
        Assertions.assertEquals(expected, actual1, expected.getClass().getSimpleName() + '/' + actual1.getClass().getSimpleName());
        Assertions.assertEquals(expected.toBag(), actual2, expected.getClass().getSimpleName() + '/' + actual2.getClass().getSimpleName());
        Assertions.assertEquals(expected, actual3, expected.getClass().getSimpleName() + '/' + actual3.getClass().getSimpleName());
    }

    @Test
    public void collect()
    {
        this.iterables.forEach(Procedures.cast(this::basicCollect));
    }

    private void basicCollect(RichIterable<Integer> iterable)
    {
        Collection<String> actual1 = ParallelIterate.collect(iterable, String::valueOf);
        Collection<String> actual2 = ParallelIterate.collect(iterable, String::valueOf, HashBag.<String>newBag(), 3, this.executor, false);
        Collection<String> actual3 = ParallelIterate.collect(iterable, String::valueOf, true);
        RichIterable<String> expected = iterable.collect(String::valueOf);
        Verify.assertSize(200, actual1);
        Verify.assertContains(actual1, String.valueOf(200));
        Assertions.assertEquals(expected, actual1, expected.getClass().getSimpleName() + '/' + actual1.getClass().getSimpleName());
        Assertions.assertEquals(expected.toBag(), actual2, expected.getClass().getSimpleName() + '/' + actual2.getClass().getSimpleName());
        Assertions.assertEquals(expected.toBag(), HashBag.newBag(actual3), expected.getClass().getSimpleName() + '/' + actual3.getClass().getSimpleName());
    }

    @Test
    public void collectIf()
    {
        this.iterables.forEach(Procedures.cast(this::basicCollectIf));
    }

    private void basicCollectIf(RichIterable<Integer> collection)
    {
        Predicate<Integer> greaterThan = Predicates.greaterThan(100);
        Collection<String> actual1 = ParallelIterate.collectIf(collection, greaterThan, String::valueOf);
        Collection<String> actual2 = ParallelIterate.collectIf(collection, greaterThan, String::valueOf, HashBag.<String>newBag(), 3, this.executor, true);
        Collection<String> actual3 = ParallelIterate.collectIf(collection, greaterThan, String::valueOf, HashBag.<String>newBag(), 3, this.executor, true);
        Bag<String> expected = collection.collectIf(greaterThan, String::valueOf).toBag();
        Verify.assertSize(100, actual1);
        Verify.assertNotContains(actual1, String.valueOf(90));
        Verify.assertNotContains(actual1, String.valueOf(210));
        Verify.assertContains(actual1, String.valueOf(159));
        Assertions.assertEquals(expected, HashBag.newBag(actual1), expected.getClass().getSimpleName() + '/' + actual1.getClass().getSimpleName());
        Assertions.assertEquals(expected, actual2, expected.getClass().getSimpleName() + '/' + actual2.getClass().getSimpleName());
        Assertions.assertEquals(expected, actual3, expected.getClass().getSimpleName() + '/' + actual3.getClass().getSimpleName());
    }

    @Test
    public void groupByWithInterval()
    {
        LazyIterable<Integer> iterable = Interval.oneTo(1000).concatenate(Interval.oneTo(1000)).concatenate(Interval.oneTo(1000));
        Multimap<String, Integer> expected = iterable.toBag().groupBy(String::valueOf);
        Multimap<String, Integer> expectedAsSet = iterable.toSet().groupBy(String::valueOf);
        Multimap<String, Integer> result1 = ParallelIterate.groupBy(iterable.toList(), String::valueOf, 100);
        Multimap<String, Integer> result2 = ParallelIterate.groupBy(iterable.toList(), String::valueOf);
        Multimap<String, Integer> result3 = ParallelIterate.groupBy(iterable.toSet(), String::valueOf, SynchronizedPutUnifiedSetMultimap.<String, Integer>newMultimap(), 100);
        Multimap<String, Integer> result4 = ParallelIterate.groupBy(iterable.toSet(), String::valueOf, SynchronizedPutUnifiedSetMultimap.<String, Integer>newMultimap());
        Multimap<String, Integer> result5 = ParallelIterate.groupBy(iterable.toSortedSet(), String::valueOf, SynchronizedPutUnifiedSetMultimap.<String, Integer>newMultimap(), 100);
        Multimap<String, Integer> result6 = ParallelIterate.groupBy(iterable.toSortedSet(), String::valueOf, SynchronizedPutUnifiedSetMultimap.<String, Integer>newMultimap());
        Multimap<String, Integer> result7 = ParallelIterate.groupBy(iterable.toBag(), String::valueOf, SynchronizedPutHashBagMultimap.<String, Integer>newMultimap(), 100);
        Multimap<String, Integer> result8 = ParallelIterate.groupBy(iterable.toBag(), String::valueOf, SynchronizedPutHashBagMultimap.<String, Integer>newMultimap());
        Multimap<String, Integer> result9 = ParallelIterate.groupBy(iterable.toList().toImmutable(), String::valueOf);
        Assertions.assertEquals(expected, HashBagMultimap.newMultimap(result1));
        Assertions.assertEquals(expected, HashBagMultimap.newMultimap(result2));
        Assertions.assertEquals(expected, HashBagMultimap.newMultimap(result9));
        Assertions.assertEquals(expectedAsSet, result3);
        Assertions.assertEquals(expectedAsSet, result4);
        Assertions.assertEquals(expectedAsSet, result5);
        Assertions.assertEquals(expectedAsSet, result6);
        Assertions.assertEquals(expected, result7);
        Assertions.assertEquals(expected, result8);
    }

    @Test
    public void groupBy()
    {
        FastList<String> source = FastList.newListWith("Ted", "Sally", "Mary", "Bob", "Sara");
        Multimap<Character, String> result1 = ParallelIterate.groupBy(source, StringFunctions.firstLetter(), 1);
        Multimap<Character, String> result2 = ParallelIterate.groupBy(Collections.synchronizedList(source), StringFunctions.firstLetter(), 1);
        Multimap<Character, String> result3 = ParallelIterate.groupBy(Collections.synchronizedCollection(source), StringFunctions.firstLetter(), 1);
        Multimap<Character, String> result4 = ParallelIterate.groupBy(LazyIterate.adapt(source), StringFunctions.firstLetter(), 1);
        Multimap<Character, String> result5 = ParallelIterate.groupBy(new ArrayList<>(source), StringFunctions.firstLetter(), 1);
        Multimap<Character, String> result6 = ParallelIterate.groupBy(source.toSet(), StringFunctions.firstLetter(), 1);
        Multimap<Character, String> result7 = ParallelIterate.groupBy(source.toMap(Functions.getStringPassThru(), Functions.getStringPassThru()), StringFunctions.firstLetter(), 1);
        Multimap<Character, String> result8 = ParallelIterate.groupBy(source.toBag(), StringFunctions.firstLetter(), 1);
        Multimap<Character, String> result9 = ParallelIterate.groupBy(source.toImmutable(), StringFunctions.firstLetter(), 1);
        MutableMultimap<Character, String> expected = HashBagMultimap.newMultimap();
        expected.put('T', "Ted");
        expected.put('S', "Sally");
        expected.put('M', "Mary");
        expected.put('B', "Bob");
        expected.put('S', "Sara");
        Assertions.assertEquals(expected, HashBagMultimap.newMultimap(result1));
        Assertions.assertEquals(expected, HashBagMultimap.newMultimap(result2));
        Assertions.assertEquals(expected, HashBagMultimap.newMultimap(result3));
        Assertions.assertEquals(expected, HashBagMultimap.newMultimap(result4));
        Assertions.assertEquals(expected, HashBagMultimap.newMultimap(result5));
        Assertions.assertEquals(expected, HashBagMultimap.newMultimap(result6));
        Assertions.assertEquals(expected, HashBagMultimap.newMultimap(result7));
        Assertions.assertEquals(expected, HashBagMultimap.newMultimap(result8));
        Assertions.assertEquals(expected, HashBagMultimap.newMultimap(result9));
        Verify.assertThrows(IllegalArgumentException.class, () -> ParallelIterate.groupBy(null, null, 1));
    }

    @Test
    public void aggregateInPlaceBy()
    {
        Procedure2<AtomicInteger, Integer> countAggregator = (aggregate, value) -> aggregate.incrementAndGet();
        List<Integer> list = Interval.oneTo(2000);
        MutableMap<String, AtomicInteger> aggregation =
                ParallelIterate.aggregateInPlaceBy(list, EVEN_OR_ODD, AtomicInteger::new, countAggregator);
        Assertions.assertEquals(1000, aggregation.get("Even").intValue());
        Assertions.assertEquals(1000, aggregation.get("Odd").intValue());
        ParallelIterate.aggregateInPlaceBy(list, EVEN_OR_ODD, AtomicInteger::new, countAggregator, aggregation);
        Assertions.assertEquals(2000, aggregation.get("Even").intValue());
        Assertions.assertEquals(2000, aggregation.get("Odd").intValue());
    }

    @Test
    public void aggregateInPlaceByWithBatchSize()
    {
        MutableList<Integer> list = LazyIterate.adapt(Collections.nCopies(100, 1))
                .concatenate(Collections.nCopies(200, 2))
                .concatenate(Collections.nCopies(300, 3))
                .toList()
                .shuffleThis();
        MapIterable<String, AtomicInteger> aggregation =
                ParallelIterate.aggregateInPlaceBy(list, String::valueOf, AtomicInteger::new, AtomicInteger::addAndGet, 50);
        Assertions.assertEquals(100, aggregation.get("1").intValue());
        Assertions.assertEquals(400, aggregation.get("2").intValue());
        Assertions.assertEquals(900, aggregation.get("3").intValue());
    }

    @Test
    public void aggregateBy()
    {
        Function2<Integer, Integer, Integer> countAggregator = (aggregate, value) -> aggregate + 1;
        List<Integer> list = Interval.oneTo(20000);
        MutableMap<String, Integer> aggregation =
                ParallelIterate.aggregateBy(list, EVEN_OR_ODD, () -> 0, countAggregator);
        Assertions.assertEquals(10000, aggregation.get("Even").intValue());
        Assertions.assertEquals(10000, aggregation.get("Odd").intValue());
        ParallelIterate.aggregateBy(list, EVEN_OR_ODD, () -> 0, countAggregator, aggregation);
        Assertions.assertEquals(20000, aggregation.get("Even").intValue());
        Assertions.assertEquals(20000, aggregation.get("Odd").intValue());
    }

    @Test
    public void sumByDouble()
    {
        Interval interval = Interval.oneTo(100000);
        ObjectDoubleMap<String> sumByCount = ParallelIterate.sumByDouble(interval, EVEN_OR_ODD, i -> 1.0d);
        Assertions.assertEquals(50000.0, sumByCount.get("Even"), 0.0);
        Assertions.assertEquals(50000.0, sumByCount.get("Odd"), 0.0);
        ObjectDoubleMap<String> sumByValue = ParallelIterate.sumByDouble(interval, EVEN_OR_ODD, Integer::doubleValue);
        Assertions.assertEquals(interval.sumByDouble(EVEN_OR_ODD, Integer::doubleValue), sumByValue);
        ObjectDoubleMap<Integer> sumByValue2 = ParallelIterate.sumByDouble(interval, i -> i % 1000, Integer::doubleValue);
        Assertions.assertEquals(interval.sumByDouble(i -> i % 1000, Integer::doubleValue), sumByValue2);
        Interval interval2 = Interval.oneTo(UNEVEN_COUNT_FOR_SUMBY);
        ObjectDoubleMap<String> sumByValue3 = ParallelIterate.sumByDouble(interval2, EVEN_OR_ODD, Integer::doubleValue);
        Assertions.assertEquals(interval2.sumByDouble(EVEN_OR_ODD, Integer::doubleValue), sumByValue3);
        ObjectDoubleMap<Integer> sumByValue4 = ParallelIterate.sumByDouble(interval2, i -> i % 1000, Integer::doubleValue);
        Assertions.assertEquals(interval2.sumByDouble(i -> i % 1000, Integer::doubleValue), sumByValue4);
        Interval small = Interval.oneTo(11);
        ObjectDoubleMap<String> smallSumByCount = ParallelIterate.sumByDouble(small, EVEN_OR_ODD, i -> 1.0d);
        Assertions.assertEquals(5.0, smallSumByCount.get("Even"), 0.0);
        Assertions.assertEquals(6.0, smallSumByCount.get("Odd"), 0.0);
    }

    @Test
    public void sumByDoubleConsistentRounding()
    {
        MutableList<Integer> group1 = Interval.oneTo(100_000).toList().shuffleThis();
        MutableList<Integer> group2 = Interval.fromTo(100_001, 200_000).toList().shuffleThis();
        MutableList<Integer> integers = Lists.mutable.withAll(group1);
        integers.addAll(group2);
        ObjectDoubleMap<Integer> result = ParallelIterate.<Integer, Integer>sumByDouble(
                integers,
                integer -> integer > 100_000 ? 2 : 1,
                integer -> {
                    Integer i = integer > 100_000 ? integer - 100_000 : integer;
                    return 1.0d / (i.doubleValue() * i.doubleValue() * i.doubleValue() * i.doubleValue());
                });

        Assertions.assertEquals(
                1.082323233711138,
                result.get(1),
                1.0e-15);

        Assertions.assertEquals(
                1.082323233711138,
                result.get(2),
                1.0e-15);
    }

    @Test
    public void sumByFloat()
    {
        Interval interval = Interval.oneTo(100000);
        ObjectDoubleMap<String> sumByCount = ParallelIterate.sumByFloat(interval, EVEN_OR_ODD, i -> 1.0f);
        Assertions.assertEquals(50000.0, sumByCount.get("Even"), 0.0);
        Assertions.assertEquals(50000.0, sumByCount.get("Odd"), 0.0);
        ObjectDoubleMap<String> sumByValue = ParallelIterate.sumByFloat(interval, EVEN_OR_ODD, Integer::floatValue);
        Assertions.assertEquals(interval.sumByFloat(EVEN_OR_ODD, Integer::floatValue), sumByValue);
        ObjectDoubleMap<Integer> sumByValue2 = ParallelIterate.sumByFloat(interval, i -> i % 1000, Integer::floatValue);
        Assertions.assertEquals(interval.sumByDouble(i -> i % 1000, Integer::doubleValue), sumByValue2);
        Interval interval2 = Interval.oneTo(UNEVEN_COUNT_FOR_SUMBY);
        ObjectDoubleMap<String> sumByValue3 = ParallelIterate.sumByFloat(interval2, EVEN_OR_ODD, Integer::floatValue);
        Assertions.assertEquals(interval2.sumByFloat(EVEN_OR_ODD, Integer::floatValue), sumByValue3);
        ObjectDoubleMap<Integer> sumByValue4 = ParallelIterate.sumByFloat(interval2, i -> i % 1000, Integer::floatValue);
        Assertions.assertEquals(interval2.sumByFloat(i -> i % 1000, Integer::floatValue), sumByValue4);
        Interval small = Interval.oneTo(11);
        ObjectDoubleMap<String> smallSumByCount = ParallelIterate.sumByFloat(small, EVEN_OR_ODD, i -> 1.0f);
        Assertions.assertEquals(5.0, smallSumByCount.get("Even"), 0.0);
        Assertions.assertEquals(6.0, smallSumByCount.get("Odd"), 0.0);
    }

    @Test
    public void sumByFloatConsistentRounding()
    {
        MutableList<Integer> group1 = Interval.oneTo(100_000).toList().shuffleThis();
        MutableList<Integer> group2 = Interval.fromTo(100_001, 200_000).toList().shuffleThis();
        MutableList<Integer> integers = Lists.mutable.withAll(group1);
        integers.addAll(group2);
        ObjectDoubleMap<Integer> result = ParallelIterate.<Integer, Integer>sumByFloat(
                integers,
                integer -> integer > 100_000 ? 2 : 1,
                integer -> {
                    Integer i = integer > 100_000 ? integer - 100_000 : integer;
                    return 1.0f / (i.floatValue() * i.floatValue() * i.floatValue() * i.floatValue());
                });

        // The test only ensures the consistency/stability of rounding. This is not meant to test the "correctness" of the float calculation result.
        // Indeed the lower bits of this calculation result are always incorrect due to the information loss of original float values.
        Assertions.assertEquals(
                1.082323233761663,
                result.get(1),
                1.0e-15);

        Assertions.assertEquals(
                1.082323233761663,
                result.get(2),
                1.0e-15);
    }

    @Test
    public void sumByLong()
    {
        Interval interval = Interval.oneTo(100000);
        ObjectLongMap<String> sumByCount = ParallelIterate.sumByLong(interval, EVEN_OR_ODD, i -> 1L);
        Assertions.assertEquals(50000, sumByCount.get("Even"));
        Assertions.assertEquals(50000, sumByCount.get("Odd"));
        ObjectLongMap<String> sumByValue = ParallelIterate.sumByLong(interval, EVEN_OR_ODD, Integer::longValue);
        Assertions.assertEquals(interval.sumByLong(EVEN_OR_ODD, Integer::longValue), sumByValue);
        ObjectLongMap<Integer> sumByValue2 = ParallelIterate.sumByLong(interval, i -> i % 1000, Integer::longValue);
        Assertions.assertEquals(interval.sumByLong(i -> i % 1000, Integer::longValue), sumByValue2);
        Interval interval2 = Interval.oneTo(UNEVEN_COUNT_FOR_SUMBY);
        ObjectLongMap<String> sumByValue3 = ParallelIterate.sumByLong(interval2, EVEN_OR_ODD, Integer::longValue);
        Assertions.assertEquals(interval2.sumByLong(EVEN_OR_ODD, Integer::longValue), sumByValue3);
        ObjectLongMap<Integer> sumByValue4 = ParallelIterate.sumByLong(interval2, i -> i % 1000, Integer::longValue);
        Assertions.assertEquals(interval2.sumByLong(i -> i % 1000, Integer::longValue), sumByValue4);
        Interval small = Interval.oneTo(11);
        ObjectLongMap<String> smallSumByCount = ParallelIterate.sumByLong(small, EVEN_OR_ODD, i -> 1L);
        Assertions.assertEquals(5.0, smallSumByCount.get("Even"), 0.0);
        Assertions.assertEquals(6.0, smallSumByCount.get("Odd"), 0.0);
    }

    @Test
    public void sumByInt()
    {
        Interval interval = Interval.oneTo(100000);
        ObjectLongMap<String> sumByCount = ParallelIterate.sumByInt(interval, EVEN_OR_ODD, i -> 1);
        Assertions.assertEquals(50000, sumByCount.get("Even"));
        Assertions.assertEquals(50000, sumByCount.get("Odd"));
        ObjectLongMap<String> sumByValue = ParallelIterate.sumByInt(interval, EVEN_OR_ODD, Integer::intValue);
        Assertions.assertEquals(interval.sumByInt(EVEN_OR_ODD, Integer::intValue), sumByValue);
        ObjectLongMap<Integer> sumByValue2 = ParallelIterate.sumByInt(interval, i -> i % 1000, Integer::intValue);
        Assertions.assertEquals(interval.sumByInt(i -> i % 1000, Integer::intValue), sumByValue2);
        Interval interval2 = Interval.oneTo(UNEVEN_COUNT_FOR_SUMBY);
        ObjectLongMap<String> sumByValue3 = ParallelIterate.sumByInt(interval2, EVEN_OR_ODD, Integer::intValue);
        Assertions.assertEquals(interval2.sumByInt(EVEN_OR_ODD, Integer::intValue), sumByValue3);
        ObjectLongMap<Integer> sumByValue4 = ParallelIterate.sumByInt(interval2, i -> i % 1000, Integer::intValue);
        Assertions.assertEquals(interval2.sumByInt(i -> i % 1000, Integer::intValue), sumByValue4);
        Interval small = Interval.oneTo(11);
        ObjectLongMap<String> smallSumByCount = ParallelIterate.sumByInt(small, EVEN_OR_ODD, i -> 1);
        Assertions.assertEquals(5.0, smallSumByCount.get("Even"), 0.0);
        Assertions.assertEquals(6.0, smallSumByCount.get("Odd"), 0.0);
    }

    @Test
    public void sumByBigDecimal()
    {
        MutableList<BigDecimal> list = Interval.oneTo(100000).collect(BigDecimal::new).toList().shuffleThis();
        MutableMap<String, BigDecimal> sumByCount = ParallelIterate.sumByBigDecimal(list, EVEN_OR_ODD_BD, bd -> new BigDecimal(1L));
        Assertions.assertEquals(BigDecimal.valueOf(50000L), sumByCount.get("Even"));
        Assertions.assertEquals(BigDecimal.valueOf(50000L), sumByCount.get("Odd"));
        MutableMap<String, BigDecimal> sumByValue = ParallelIterate.sumByBigDecimal(list, EVEN_OR_ODD_BD, bd -> bd);
        Assertions.assertEquals(Iterate.sumByBigDecimal(list, EVEN_OR_ODD_BD, bd -> bd), sumByValue);
        MutableMap<Integer, BigDecimal> sumByValue2 = ParallelIterate.sumByBigDecimal(list, bd -> bd.intValue() % 1000, bd -> bd);
        Assertions.assertEquals(Iterate.sumByBigDecimal(list, bd -> bd.intValue() % 1000, bd -> bd), sumByValue2);
        MutableList<BigDecimal> list2 = Interval.oneTo(UNEVEN_COUNT_FOR_SUMBY).collect(BigDecimal::new).toList();
        MutableMap<String, BigDecimal> sumByValue3 = ParallelIterate.sumByBigDecimal(list2, EVEN_OR_ODD_BD, bd -> bd);
        Assertions.assertEquals(Iterate.sumByBigDecimal(list2, EVEN_OR_ODD_BD, bd -> bd), sumByValue3);
        MutableMap<Integer, BigDecimal> sumByValue4 = ParallelIterate.sumByBigDecimal(list2, bd -> bd.intValue() % 1000, bd -> bd);
        Assertions.assertEquals(Iterate.sumByBigDecimal(list2, bd -> bd.intValue() % 1000, bd -> bd), sumByValue4);
        Interval small = Interval.oneTo(11);
        MutableMap<String, BigDecimal> smallSumByCount = ParallelIterate.sumByBigDecimal(small, EVEN_OR_ODD, i -> BigDecimal.valueOf(1L));
        Assertions.assertEquals(new BigDecimal(5), smallSumByCount.get("Even"));
        Assertions.assertEquals(new BigDecimal(6), smallSumByCount.get("Odd"));
    }

    @Test
    public void sumByBigInteger()
    {
        MutableList<BigInteger> list = Interval.oneTo(100000).collect(Object::toString).collect(BigInteger::new).toList().shuffleThis();
        MutableMap<String, BigInteger> sumByCount = ParallelIterate.sumByBigInteger(list, EVEN_OR_ODD_BI, bi -> BigInteger.valueOf(1L));
        Assertions.assertEquals(BigInteger.valueOf(50000L), sumByCount.get("Even"));
        Assertions.assertEquals(BigInteger.valueOf(50000L), sumByCount.get("Odd"));
        MutableMap<String, BigInteger> sumByValue = ParallelIterate.sumByBigInteger(list, EVEN_OR_ODD_BI, bi -> bi);
        Assertions.assertEquals(Iterate.sumByBigInteger(list, EVEN_OR_ODD_BI, bi -> bi), sumByValue);
        MutableMap<Integer, BigInteger> sumByValue2 = ParallelIterate.sumByBigInteger(list, bi -> bi.intValue() % 1000, bi -> bi);
        Assertions.assertEquals(Iterate.sumByBigInteger(list, bi -> bi.intValue() % 1000, bi -> bi), sumByValue2);
        MutableList<BigInteger> list2 = Interval.oneTo(UNEVEN_COUNT_FOR_SUMBY).collect(Object::toString).collect(BigInteger::new).toList();
        MutableMap<String, BigInteger> sumByValue3 = ParallelIterate.sumByBigInteger(list2, EVEN_OR_ODD_BI, bi -> bi);
        Assertions.assertEquals(Iterate.sumByBigInteger(list2, EVEN_OR_ODD_BI, bi -> bi), sumByValue3);
        MutableMap<Integer, BigInteger> sumByValue4 = ParallelIterate.sumByBigInteger(list2, bi -> bi.intValue() % 1000, bi -> bi);
        Assertions.assertEquals(Iterate.sumByBigInteger(list2, bi -> bi.intValue() % 1000, bi -> bi), sumByValue4);
        Interval small = Interval.oneTo(11);
        MutableMap<String, BigInteger> smallSumByCount = ParallelIterate.sumByBigInteger(small, EVEN_OR_ODD, i -> BigInteger.valueOf(1L));
        Assertions.assertEquals(new BigInteger("5"), smallSumByCount.get("Even"));
        Assertions.assertEquals(new BigInteger("6"), smallSumByCount.get("Odd"));
    }

    @Test
    public void aggregateByWithBatchSize()
    {
        Function2<Integer, Integer, Integer> sumAggregator = (aggregate, value) -> aggregate + value;
        MutableList<Integer> list = LazyIterate.adapt(Collections.nCopies(1000, 1))
                .concatenate(Collections.nCopies(2000, 2))
                .concatenate(Collections.nCopies(3000, 3))
                .toList()
                .shuffleThis();
        MapIterable<String, Integer> aggregation =
                ParallelIterate.aggregateBy(list, String::valueOf, () -> 0, sumAggregator, 100);
        Assertions.assertEquals(1000, aggregation.get("1").intValue());
        Assertions.assertEquals(4000, aggregation.get("2").intValue());
        Assertions.assertEquals(9000, aggregation.get("3").intValue());
    }

    private static List<Integer> createIntegerList(int size)
    {
        return Collections.nCopies(size, Integer.valueOf(1));
    }

    @Test
    public void flatCollect()
    {
        this.iterables.forEach(Procedures.cast(this::basicFlatCollect));
    }

    private void basicFlatCollect(RichIterable<Integer> iterable)
    {
        Collection<String> actual1 = ParallelIterate.flatCollect(iterable, INT_TO_TWO_STRINGS);
        Collection<String> actual2 = ParallelIterate.flatCollect(iterable, INT_TO_TWO_STRINGS, HashBag.<String>newBag(), 3, this.executor, false);
        Collection<String> actual3 = ParallelIterate.flatCollect(iterable, INT_TO_TWO_STRINGS, true);
        RichIterable<String> expected1 = iterable.flatCollect(INT_TO_TWO_STRINGS);
        RichIterable<String> expected2 = iterable.flatCollect(INT_TO_TWO_STRINGS, HashBag.<String>newBag());
        Verify.assertContains(actual1, String.valueOf(200));
        Assertions.assertEquals(expected1, actual1, expected1.getClass().getSimpleName() + '/' + actual1.getClass().getSimpleName());
        Assertions.assertEquals(expected2, actual2, expected2.getClass().getSimpleName() + '/' + actual2.getClass().getSimpleName());
        Assertions.assertEquals(expected1, actual3, expected1.getClass().getSimpleName() + '/' + actual3.getClass().getSimpleName());
    }

    public static final class IntegerSum
    {
        private int sum;

        public IntegerSum(int newSum)
        {
            this.sum = newSum;
        }

        public IntegerSum add(int value)
        {
            this.sum += value;
            return this;
        }

        public int getSum()
        {
            return this.sum;
        }
    }

    public static final class SumProcedure
            implements Procedure<Integer>, Function2<IntegerSum, Integer, IntegerSum>, ProcedureFactory<SumProcedure>
    {
        private static final long serialVersionUID = 1L;

        private final IntegerSum sum;

        public SumProcedure(IntegerSum newSum)
        {
            this.sum = newSum;
        }

        @Override
        public SumProcedure create()
        {
            return new SumProcedure(new IntegerSum(0));
        }

        @Override
        public IntegerSum value(IntegerSum s1, Integer s2)
        {
            return s1.add(s2);
        }

        @Override
        public void value(Integer object)
        {
            this.sum.add(object);
        }

        public int getSum()
        {
            return this.sum.getSum();
        }
    }

    public static final class SumCombiner extends AbstractProcedureCombiner<SumProcedure>
    {
        private static final long serialVersionUID = 1L;
        private final IntegerSum sum;

        public SumCombiner(IntegerSum initialSum)
        {
            super(true);
            this.sum = initialSum;
        }

        @Override
        public void combineOne(SumProcedure sumProcedure)
        {
            this.sum.add(sumProcedure.getSum());
        }
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(ParallelIterate.class);
    }
}
