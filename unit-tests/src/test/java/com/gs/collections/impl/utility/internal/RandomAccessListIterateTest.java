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

package com.gs.collections.impl.utility.internal;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.gs.collections.api.RichIterable;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.predicate.Predicate2;
import com.gs.collections.api.block.procedure.Procedure2;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.api.tuple.Twin;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.factory.Procedures2;
import com.gs.collections.impl.block.function.AddFunction;
import com.gs.collections.impl.block.function.MaxSizeFunction;
import com.gs.collections.impl.block.function.MinSizeFunction;
import com.gs.collections.impl.block.procedure.DoNothingProcedure;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.AddToList;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.Tuples;
import com.gs.collections.impl.utility.Iterate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.gs.collections.impl.factory.Iterables.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RandomAccessListIterateTest
{
    @Test
    public void forEachWithNegativeFroms()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            RandomAccessListIterate.forEach(FastList.newList(), -1, 1, DoNothingProcedure.DO_NOTHING);
        });
    }

    @Test
    public void forEachWithNegativeTos()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            RandomAccessListIterate.forEach(FastList.newList(), 1, -1, DoNothingProcedure.DO_NOTHING);
        });
    }

    @Test
    public void forEachInBothWithNull()
    {
        RandomAccessListIterate.forEachInBoth(null, FastList.newListWith(1, 2, 3), new FailProcedure2());
        RandomAccessListIterate.forEachInBoth(FastList.newListWith(1, 2, 3), null, new FailProcedure2());
    }

    @Test
    public void forEachInBothThrowsOnMisMatchedLists()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            RandomAccessListIterate.forEachInBoth(FastList.newListWith("1", 2), FastList.newListWith(1, 2, 3),
                    Procedures2.fromProcedure(DoNothingProcedure.DO_NOTHING));
        });
    }

    @Test
    public void removeIf()
    {
        Assertions.assertTrue(RandomAccessListIterate.removeIf(FastList.newListWith(1, 2, 3), Predicates.greaterThan(1)));
        Assertions.assertTrue(RandomAccessListIterate.removeIf(FastList.newListWith(1, 2, 3), Predicates.greaterThan(0)));
        Assertions.assertFalse(RandomAccessListIterate.removeIf(FastList.newListWith(1, 2, 3), Predicates.greaterThan(4)));
        Assertions.assertFalse(RandomAccessListIterate.removeIf(FastList.newList(), Predicates.greaterThan(4)));
    }

    @Test
    public void removeIfWith()
    {
        Assertions.assertTrue(RandomAccessListIterate.removeIfWith(FastList.newListWith(1, 2, 3), Predicates2.greaterThan(), 1));
        Assertions.assertTrue(RandomAccessListIterate.removeIfWith(FastList.newListWith(1, 2, 3), Predicates2.greaterThan(), 0));
        Assertions.assertFalse(RandomAccessListIterate.removeIfWith(FastList.newListWith(1, 2, 3), Predicates2.greaterThan(), 4));
        Assertions.assertFalse(RandomAccessListIterate.removeIfWith(FastList.newList(), Predicates2.greaterThan(), 1));
    }

    @Test
    public void injectInto()
    {
        MutableList<Integer> list = Lists.fixedSize.of(1, 2, 3);
        Assertions.assertEquals(Integer.valueOf(7), RandomAccessListIterate.injectInto(1, list, AddFunction.INTEGER));
    }

    @Test
    public void injectIntoInt()
    {
        MutableList<Integer> list = Lists.fixedSize.of(1, 2, 3);
        Assertions.assertEquals(7, RandomAccessListIterate.injectInto(1, list, AddFunction.INTEGER_TO_INT));
    }

    @Test
    public void injectIntoLong()
    {
        MutableList<Integer> list = Lists.fixedSize.of(1, 2, 3);
        Assertions.assertEquals(7, RandomAccessListIterate.injectInto(1, list, AddFunction.INTEGER_TO_LONG));
    }

    @Test
    public void injectIntoDouble()
    {
        MutableList<Double> list = Lists.fixedSize.of(1.0, 2.0, 3.0);
        Assertions.assertEquals(7.0d, RandomAccessListIterate.injectInto(1.0, list, AddFunction.DOUBLE), 0.001);
    }

    @Test
    public void injectIntoString()
    {
        MutableList<String> list = Lists.fixedSize.of("1", "2", "3");
        Assertions.assertEquals("0123", RandomAccessListIterate.injectInto("0", list, AddFunction.STRING));
    }

    @Test
    public void injectIntoMaxString()
    {
        MutableList<String> list = Lists.fixedSize.of("1", "12", "123");
        Function2<Integer, String, Integer> function = MaxSizeFunction.STRING;
        Assertions.assertEquals(Integer.valueOf(3), RandomAccessListIterate.injectInto(Integer.MIN_VALUE, list, function));
    }

    @Test
    public void injectIntoMinString()
    {
        MutableList<String> list = Lists.fixedSize.of("1", "12", "123");
        Function2<Integer, String, Integer> function = MinSizeFunction.STRING;
        Assertions.assertEquals(Integer.valueOf(1), RandomAccessListIterate.injectInto(Integer.MAX_VALUE, list, function));
    }

    @Test
    public void collect()
    {
        Assertions.assertEquals(
                iList("true", "false", "null"),
                RandomAccessListIterate.collect(mList(true, false, null), String::valueOf));
    }

    @Test
    public void collectReflective()
    {
        Assertions.assertEquals(
                iList("true", "false", "null"),
                RandomAccessListIterate.collect(mList(true, false, null), String::valueOf));

        Assertions.assertEquals(
                iList("true", "false", "null"),
                RandomAccessListIterate.collect(mList(true, false, null), String::valueOf, new ArrayList<>()));
    }

    @Test
    public void flattenReflective()
    {
        MutableList<MutableList<Boolean>> list = Lists.fixedSize.<MutableList<Boolean>>of(
                Lists.fixedSize.of(true, false),
                Lists.fixedSize.of(true, null));
        MutableList<Boolean> newList = RandomAccessListIterate.flatCollect(list, RichIterable::toList);
        Verify.assertListsEqual(
                FastList.newListWith(true, false, true, null),
                newList);

        MutableSet<Boolean> newSet = RandomAccessListIterate.flatCollect(list, RichIterable::toSet, UnifiedSet.<Boolean>newSet());
        Verify.assertSetsEqual(
                UnifiedSet.newSetWith(true, false, null),
                newSet);
    }

    @Test
    public void getLast()
    {
        MutableList<Boolean> list = Lists.fixedSize.of(true, null, false);
        Assertions.assertEquals(Boolean.FALSE, RandomAccessListIterate.getLast(list));
    }

    @Test
    public void getLastOnEmpty()
    {
        List<?> list = new ArrayList<>();
        Assertions.assertNull(RandomAccessListIterate.getLast(list));
    }

    @Test
    public void count()
    {
        MutableList<Integer> list = this.getIntegerList();
        int result = RandomAccessListIterate.count(list, Predicates.attributeEqual(Number::intValue, 3));
        Assertions.assertEquals(1, result);
        int result2 = RandomAccessListIterate.count(list, Predicates.attributeEqual(Number::intValue, 6));
        Assertions.assertEquals(0, result2);
    }

    private MutableList<Integer> getIntegerList()
    {
        return Interval.toReverseList(1, 5);
    }

    @Test
    public void forEachWithIndex()
    {
        MutableList<Integer> list = this.getIntegerList();
        Iterate.sortThis(list);
        RandomAccessListIterate.forEachWithIndex(list, (object, index) -> Assertions.assertEquals(index, object - 1));
    }

    @Test
    public void forEachFromTo()
    {
        MutableList<Integer> integers = Lists.mutable.with(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);

        MutableList<Integer> result = Lists.mutable.empty();
        RandomAccessListIterate.forEach(integers, 5, 7, result::add);
        Assertions.assertEquals(Lists.immutable.with(3, 3, 2), result);

        MutableList<Integer> result2 = Lists.mutable.empty();
        RandomAccessListIterate.forEach(integers, 5, 5, result2::add);
        Assertions.assertEquals(Lists.immutable.with(3), result2);

        MutableList<Integer> result3 = Lists.mutable.empty();
        RandomAccessListIterate.forEach(integers, 0, 9, result3::add);
        Assertions.assertEquals(Lists.immutable.with(4, 4, 4, 4, 3, 3, 3, 2, 2, 1), result3);

        MutableList<Integer> result4 = Lists.mutable.empty();
        RandomAccessListIterate.forEach(integers, 7, 5, result4::add);
        Assertions.assertEquals(Lists.immutable.with(2, 3, 3), result4);

        Verify.assertThrows(IndexOutOfBoundsException.class, () -> RandomAccessListIterate.forEach(integers, -1, 0, result::add));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> RandomAccessListIterate.forEach(integers, 0, -1, result::add));
    }

    @Test
    public void forEachWithIndexWithFromTo()
    {
        MutableList<Integer> integers = Lists.mutable.with(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);

        StringBuilder builder = new StringBuilder();
        RandomAccessListIterate.forEachWithIndex(integers, 5, 7, (each, index) -> builder.append(each).append(index));
        Assertions.assertEquals("353627", builder.toString());

        StringBuilder builder2 = new StringBuilder();
        RandomAccessListIterate.forEachWithIndex(integers, 5, 5, (each, index) -> builder2.append(each).append(index));
        Assertions.assertEquals("35", builder2.toString());

        StringBuilder builder3 = new StringBuilder();
        RandomAccessListIterate.forEachWithIndex(integers, 0, 9, (each, index) -> builder3.append(each).append(index));
        Assertions.assertEquals("40414243343536272819", builder3.toString());

        StringBuilder builder4 = new StringBuilder();
        RandomAccessListIterate.forEachWithIndex(integers, 7, 5, (each, index) -> builder4.append(each).append(index));
        Assertions.assertEquals("273635", builder4.toString());

        MutableList<Integer> result = Lists.mutable.of();
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> RandomAccessListIterate.forEachWithIndex(integers, -1, 0, new AddToList(result)));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> RandomAccessListIterate.forEachWithIndex(integers, 0, -1, new AddToList(result)));
    }

    @Test
    public void forEachInBoth()
    {
        MutableList<String> list1 = Lists.fixedSize.of("1", "2");
        MutableList<String> list2 = Lists.fixedSize.of("a", "b");
        List<Pair<String, String>> list = new ArrayList<>();
        RandomAccessListIterate.forEachInBoth(list1, list2, (argument1, argument2) -> list.add(Tuples.twin(argument1, argument2)));
        Assertions.assertEquals(FastList.newListWith(Tuples.twin("1", "a"), Tuples.twin("2", "b")), list);
    }

    @Test
    public void detectWith()
    {
        MutableList<Integer> list = this.getIntegerList();
        Assertions.assertEquals(Integer.valueOf(1), RandomAccessListIterate.detectWith(list, Object::equals, 1));
        MutableList<Integer> list2 = Lists.fixedSize.of(1, 2, 2);
        Assertions.assertSame(list2.get(1), RandomAccessListIterate.detectWith(list2, Object::equals, 2));
    }

    @Test
    public void selectWith()
    {
        MutableList<Integer> list = this.getIntegerList();
        Verify.assertSize(5, RandomAccessListIterate.selectWith(list, Predicates2.instanceOf(), Integer.class));
    }

    @Test
    public void rejectWith()
    {
        MutableList<Integer> list = this.getIntegerList();
        Verify.assertEmpty(RandomAccessListIterate.rejectWith(list, Predicates2.instanceOf(), Integer.class));
    }

    @Test
    public void distinct()
    {
        MutableList<Integer> list = FastList.newListWith(5, 2, 6, 2, 3, 5, 2);
        MutableList<Integer> actualList = FastList.newList();
        RandomAccessListIterate.distinct(list, actualList);
        Verify.assertListsEqual(FastList.newListWith(5, 2, 6, 3), actualList);
        Verify.assertSize(7, list);
    }

    @Test
    public void selectAndRejectWith()
    {
        MutableList<Integer> list = this.getIntegerList();
        Twin<MutableList<Integer>> result = RandomAccessListIterate.selectAndRejectWith(list, Predicates2.in(), Lists.fixedSize.of(1));
        Verify.assertSize(1, result.getOne());
        Verify.assertSize(4, result.getTwo());
    }

    @Test
    public void anySatisfyWith()
    {
        MutableList<Integer> list = this.getIntegerList();
        Assertions.assertTrue(RandomAccessListIterate.anySatisfyWith(list, Predicates2.instanceOf(), Integer.class));
        Assertions.assertFalse(RandomAccessListIterate.anySatisfyWith(list, Predicates2.instanceOf(), Double.class));
    }

    @Test
    public void allSatisfyWith()
    {
        MutableList<Integer> list = this.getIntegerList();
        Assertions.assertTrue(RandomAccessListIterate.allSatisfyWith(list, Predicates2.instanceOf(), Integer.class));
        Predicate2<Integer, Integer> greaterThanPredicate = Predicates2.greaterThan();
        Assertions.assertFalse(RandomAccessListIterate.allSatisfyWith(list, greaterThanPredicate, 2));
    }

    @Test
    public void countWith()
    {
        Assertions.assertEquals(5, RandomAccessListIterate.countWith(this.getIntegerList(), Predicates2.instanceOf(), Integer.class));
    }

    @Test
    public void collectIf()
    {
        MutableList<Integer> integers = Lists.fixedSize.of(1, 2, 3);
        Verify.assertContainsAll(RandomAccessListIterate.collectIf(integers, Integer.class::isInstance, String::valueOf), "1", "2", "3");
        Verify.assertContainsAll(RandomAccessListIterate.collectIf(integers, Integer.class::isInstance, String::valueOf, new ArrayList<>()), "1", "2", "3");
    }

    @Test
    public void take()
    {
        MutableList<Integer> integers = this.getIntegerList();
        Verify.assertListsEqual(integers.take(0), RandomAccessListIterate.take(integers, 0));
        Verify.assertListsEqual(integers.take(1), RandomAccessListIterate.take(integers, 1));
        Verify.assertListsEqual(integers.take(2), RandomAccessListIterate.take(integers, 2));
        Verify.assertListsEqual(integers.take(5), RandomAccessListIterate.take(integers, 5));
        Verify.assertListsEqual(
                integers.take(integers.size() - 1),
                RandomAccessListIterate.take(integers, integers.size() - 1));
        Verify.assertListsEqual(integers.take(integers.size()), RandomAccessListIterate.take(integers, integers.size()));
        Verify.assertListsEqual(integers.take(10), RandomAccessListIterate.take(integers, 10));
        Verify.assertListsEqual(integers.take(Integer.MAX_VALUE), RandomAccessListIterate.take(integers, Integer.MAX_VALUE));
        Verify.assertListsEqual(FastList.newList(), RandomAccessListIterate.take(Lists.fixedSize.of(), 2));
    }

    @Test
    public void take_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            RandomAccessListIterate.take(this.getIntegerList(), -1);
        });
    }

    @Test
    public void take_target()
    {
        MutableList<Integer> integers = this.getIntegerList();

        MutableList<Integer> expected1 = FastList.newListWith(-1);
        expected1.addAll(integers.take(2));
        Verify.assertListsEqual(expected1, RandomAccessListIterate.take(integers, 2, FastList.newListWith(-1)));

        MutableList<Integer> expected2 = FastList.newListWith(-1);
        expected2.addAll(integers.take(0));
        Verify.assertListsEqual(expected2, RandomAccessListIterate.take(integers, 0, FastList.newListWith(-1)));

        MutableList<Integer> expected3 = FastList.newListWith(-1);
        expected3.addAll(integers.take(5));
        Verify.assertListsEqual(expected3, RandomAccessListIterate.take(integers, 5, FastList.newListWith(-1)));

        MutableList<Integer> expected4 = FastList.newListWith(-1);
        expected4.addAll(integers.take(10));
        Verify.assertListsEqual(expected4, RandomAccessListIterate.take(integers, 10, FastList.newListWith(-1)));

        MutableList<Integer> expected5 = FastList.newListWith(-1);
        expected5.addAll(integers.take(Integer.MAX_VALUE));
        Verify.assertListsEqual(expected5, RandomAccessListIterate.take(integers, Integer.MAX_VALUE, FastList.newListWith(-1)));
        Verify.assertListsEqual(FastList.newListWith(-1), RandomAccessListIterate.take(Lists.fixedSize.of(), 2, FastList.newListWith(-1)));
    }

    @Test
    public void take__target_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            RandomAccessListIterate.take(this.getIntegerList(), -1, FastList.newList());
        });
    }

    @Test
    public void drop()
    {
        MutableList<Integer> integers = this.getIntegerList();
        Verify.assertListsEqual(integers.drop(0), RandomAccessListIterate.drop(integers, 0));
        Verify.assertListsEqual(integers.drop(1), RandomAccessListIterate.drop(integers, 1));
        Verify.assertListsEqual(integers.drop(2), RandomAccessListIterate.drop(integers, 2));
        Verify.assertListsEqual(integers.drop(5), RandomAccessListIterate.drop(integers, 5));
        Verify.assertListsEqual(integers.drop(6), RandomAccessListIterate.drop(integers, 6));
        Verify.assertListsEqual(
                integers.drop(integers.size() - 1),
                RandomAccessListIterate.drop(integers, integers.size() - 1));
        Verify.assertListsEqual(integers.drop(integers.size()), RandomAccessListIterate.drop(integers, integers.size()));
        Verify.assertListsEqual(FastList.newList(), RandomAccessListIterate.drop(Lists.fixedSize.of(), 0));
        Verify.assertListsEqual(FastList.newList(), RandomAccessListIterate.drop(Lists.fixedSize.of(), 2));
        Verify.assertListsEqual(integers.drop(Integer.MAX_VALUE), RandomAccessListIterate.drop(integers, Integer.MAX_VALUE));
    }

    @Test
    public void drop_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            RandomAccessListIterate.drop(this.getIntegerList(), -1);
        });
    }

    @Test
    public void drop_target()
    {
        MutableList<Integer> integers = this.getIntegerList();

        MutableList<Integer> expected1 = FastList.newListWith(-1);
        expected1.addAll(integers.drop(2));
        Verify.assertListsEqual(expected1, RandomAccessListIterate.drop(integers, 2, FastList.newListWith(-1)));

        MutableList<Integer> expected2 = FastList.newListWith(-1);
        expected2.addAll(integers.drop(5));
        Verify.assertListsEqual(expected2, RandomAccessListIterate.drop(integers, 5, FastList.newListWith(-1)));

        MutableList<Integer> expected3 = FastList.newListWith(-1);
        expected3.addAll(integers.drop(6));
        Verify.assertListsEqual(expected3, RandomAccessListIterate.drop(integers, 6, FastList.newListWith(-1)));

        MutableList<Integer> expected4 = FastList.newListWith(-1);
        expected4.addAll(integers.drop(Integer.MAX_VALUE));
        Verify.assertListsEqual(expected4, RandomAccessListIterate.drop(integers, Integer.MAX_VALUE, FastList.newListWith(-1)));

        MutableList<Integer> expected5 = FastList.newListWith(-1);
        expected5.addAll(integers.drop(0));
        Verify.assertListsEqual(expected5, RandomAccessListIterate.drop(integers, 0, FastList.newListWith(-1)));

        Verify.assertListsEqual(FastList.newListWith(-1), RandomAccessListIterate.drop(Lists.fixedSize.of(), 0, FastList.newListWith(-1)));
        Verify.assertListsEqual(FastList.newListWith(-1), RandomAccessListIterate.drop(Lists.fixedSize.of(), 2, FastList.newListWith(-1)));
    }

    @Test
    public void drop_target_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            RandomAccessListIterate.drop(this.getIntegerList(), -1, FastList.newList());
        });
    }

    @Test
    public void corresponds_throws_nonRandomAccess()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            RandomAccessListIterate.corresponds(new LinkedList<>(), FastList.newList(), Predicates2.alwaysTrue());
        });
    }

    private static class FailProcedure2 implements Procedure2<Object, Integer>
    {
        private static final long serialVersionUID = 1L;

        @Override
        public void value(Object argument1, Integer argument2)
        {
            Assertions.fail();
        }
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(RandomAccessListIterate.class);
    }
}
