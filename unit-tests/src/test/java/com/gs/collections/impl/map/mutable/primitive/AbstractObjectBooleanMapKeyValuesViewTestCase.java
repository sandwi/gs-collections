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

package com.gs.collections.impl.map.mutable.primitive;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import com.gs.collections.api.RichIterable;
import com.gs.collections.api.bag.MutableBag;
import com.gs.collections.api.bag.sorted.MutableSortedBag;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.procedure.Procedure2;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MapIterable;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.map.primitive.ObjectBooleanMap;
import com.gs.collections.api.map.sorted.MutableSortedMap;
import com.gs.collections.api.multimap.Multimap;
import com.gs.collections.api.partition.PartitionIterable;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.set.sorted.MutableSortedSet;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.api.tuple.primitive.ObjectBooleanPair;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.bag.sorted.mutable.TreeBag;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.factory.Bags;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.map.sorted.mutable.TreeSortedMap;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.set.sorted.mutable.TreeSortedSet;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.Tuples;
import com.gs.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Abstract JUnit test for {@link ObjectBooleanMap#keyValuesView()}.
 */
public abstract class AbstractObjectBooleanMapKeyValuesViewTestCase
{
    public abstract <K> ObjectBooleanMap<K> newWithKeysValues(K key1, boolean value1, K key2, boolean value2, K key3, boolean value3);

    public abstract <K> ObjectBooleanMap<K> newWithKeysValues(K key1, boolean value1, K key2, boolean value2);

    public abstract <K> ObjectBooleanMap<K> newWithKeysValues(K key1, boolean value1);

    public abstract <K> ObjectBooleanMap<K> newEmpty();

    public RichIterable<ObjectBooleanPair<Object>> newWith()
    {
        return this.newEmpty().keyValuesView();
    }

    public <K> RichIterable<ObjectBooleanPair<K>> newWith(K key1, boolean value1)
    {
        return this.newWithKeysValues(key1, value1).keyValuesView();
    }

    public <K> RichIterable<ObjectBooleanPair<K>> newWith(K key1, boolean value1, K key2, boolean value2)
    {
        return this.newWithKeysValues(key1, value1, key2, value2).keyValuesView();
    }

    public <K> RichIterable<ObjectBooleanPair<K>> newWith(K key1, boolean value1, K key2, boolean value2, K key3, boolean value3)
    {
        return this.newWithKeysValues(key1, value1, key2, value2, key3, value3).keyValuesView();
    }

    @Test
    public void containsAllIterable()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Assertions.assertTrue(collection.containsAllIterable(FastList.newListWith(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), false))));
        Assertions.assertFalse(collection.containsAllIterable(FastList.newListWith(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(1.0, Integer.valueOf(5)))));
    }

    @Test
    public void containsAllArray()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Assertions.assertTrue(collection.containsAllArguments(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), false)));
        Assertions.assertFalse(collection.containsAllArguments(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(1.0, Integer.valueOf(5))));
    }

    @Test
    public void forEach()
    {
        MutableList<ObjectBooleanPair<Integer>> result = Lists.mutable.of();
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        collection.forEach(CollectionAddProcedure.on(result));
        Verify.assertSize(3, result);
        Verify.assertContainsAll(result, PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), false), PrimitiveTuples.pair(Integer.valueOf(3), true));
    }

    @Test
    public void forEachWith()
    {
        MutableBag<ObjectBooleanPair<Integer>> result = Bags.mutable.of();
        MutableBag<Integer> result2 = Bags.mutable.of();
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        collection.forEachWith((argument1, argument2) -> {
            result.add(argument1);
            result2.add(argument2);
        }, 0);

        Assertions.assertEquals(Bags.immutable.of(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), false), PrimitiveTuples.pair(Integer.valueOf(3), true)), result);
        Assertions.assertEquals(Bags.immutable.of(0, 0, 0), result2);
    }

    @Test
    public void forEachWithIndex()
    {
        MutableBag<ObjectBooleanPair<Integer>> elements = Bags.mutable.of();
        MutableBag<Integer> indexes = Bags.mutable.of();
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        collection.forEachWithIndex((object, index) -> {
            elements.add(object);
            indexes.add(index);
        });
        Assertions.assertEquals(Bags.mutable.of(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), false), PrimitiveTuples.pair(Integer.valueOf(3), true)), elements);
        Assertions.assertEquals(Bags.mutable.of(0, 1, 2), indexes);
    }

    @Test
    public void select()
    {
        MutableList<ObjectBooleanPair<Integer>> result = this.newWith(1, true, 2, false, 3, true).select(PrimitiveTuples.pair(Integer.valueOf(2), false)::equals).toList();
        Verify.assertContains(PrimitiveTuples.pair(Integer.valueOf(2), false), result);
        Verify.assertNotContains(PrimitiveTuples.pair(Integer.valueOf(1), true), result);
        Verify.assertNotContains(PrimitiveTuples.pair(Integer.valueOf(3), true), result);
    }

    @Test
    public void selectWith()
    {
        MutableList<ObjectBooleanPair<Integer>> result = this.newWith(1, true, 2, false, 3, true).selectWith(Object::equals, PrimitiveTuples.pair(Integer.valueOf(2), false)).toList();
        Verify.assertContains(PrimitiveTuples.pair(Integer.valueOf(2), false), result);
        Verify.assertNotContains(PrimitiveTuples.pair(Integer.valueOf(1), true), result);
        Verify.assertNotContains(PrimitiveTuples.pair(Integer.valueOf(3), true), result);
    }

    @Test
    public void selectWith_target()
    {
        HashBag<ObjectBooleanPair<Integer>> result = this.newWith(1, true, 2, false, 3, true).selectWith(Predicates2.notEqual(), PrimitiveTuples.pair(Integer.valueOf(2), false), HashBag.<ObjectBooleanPair<Integer>>newBag());
        Assertions.assertEquals(Bags.immutable.of(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(3), true)), result);
    }

    @Test
    public void reject()
    {
        MutableList<ObjectBooleanPair<Integer>> result = this.newWith(1, true, 2, false, 3, true).reject(Predicates.notEqual(PrimitiveTuples.pair(Integer.valueOf(2), false))).toList();
        Verify.assertContains(PrimitiveTuples.pair(Integer.valueOf(2), false), result);
        Verify.assertNotContains(PrimitiveTuples.pair(Integer.valueOf(1), true), result);
        Verify.assertNotContains(PrimitiveTuples.pair(Integer.valueOf(3), true), result);
    }

    @Test
    public void rejectWith()
    {
        MutableList<ObjectBooleanPair<Integer>> result = this.newWith(1, true, 2, false, 3, true).rejectWith(Predicates2.notEqual(), PrimitiveTuples.pair(Integer.valueOf(2), false)).toList();
        Verify.assertContains(PrimitiveTuples.pair(Integer.valueOf(2), false), result);
        Verify.assertNotContains(PrimitiveTuples.pair(Integer.valueOf(1), true), result);
        Verify.assertNotContains(PrimitiveTuples.pair(Integer.valueOf(3), true), result);
    }

    @Test
    public void rejectWith_target()
    {
        HashBag<ObjectBooleanPair<Integer>> result = this.newWith(1, true, 2, false, 3, true).rejectWith(Object::equals, PrimitiveTuples.pair(Integer.valueOf(2), false), HashBag.<ObjectBooleanPair<Integer>>newBag());
        Assertions.assertEquals(Bags.immutable.of(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(3), true)), result);
    }

    @Test
    public void selectInstancesOf()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(1, true, 2, false, 3, true);
        Verify.assertIterableEmpty(pairs.selectInstancesOf(Integer.class));
        Verify.assertContainsAll(pairs.selectInstancesOf(ObjectBooleanPair.class), PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(3), true), PrimitiveTuples.pair(Integer.valueOf(2), false));
    }

    @Test
    public void collect()
    {
        RichIterable<Integer> result1 = this.newWith(2, true, 3, false, 4, true).collect(ObjectBooleanPair::getOne);

        Assertions.assertEquals(HashBag.newBagWith(2, 3, 4), result1.toBag());
    }

    @Test
    public void flatCollect()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Function<ObjectBooleanPair<Integer>, MutableList<String>> function =
                object -> FastList.newListWith(String.valueOf(object));

        Verify.assertListsEqual(
                FastList.newListWith("1:true", "2:false", "3:true"),
                collection.flatCollect(function).toSortedList());

        Verify.assertSetsEqual(
                UnifiedSet.newSetWith("1:true", "2:false", "3:true"),
                collection.flatCollect(function, UnifiedSet.<String>newSet()));
    }

    @Test
    public void detect()
    {
        Assertions.assertEquals(PrimitiveTuples.pair(Integer.valueOf(2), false), this.newWith(1, true, 2, false, 3, true).detect(PrimitiveTuples.pair(Integer.valueOf(2), false)::equals));
        Assertions.assertNull(this.newWith(1, true, 2, false, 3, true).detect(PrimitiveTuples.pair(true, Integer.valueOf(4))::equals));
    }

    @Test
    public void min_empty_throws()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.newWith().min(ObjectBooleanPair::compareTo);
        });
    }

    @Test
    public void max_empty_throws()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.newWith().max(ObjectBooleanPair::compareTo);
        });
    }

    @Test
    public void min()
    {
        Assertions.assertEquals(PrimitiveTuples.pair(Integer.valueOf(1), true), this.newWith(1, true, 2, false, 3, true).min(ObjectBooleanPair::compareTo));
    }

    @Test
    public void max()
    {
        Assertions.assertEquals(PrimitiveTuples.pair(Integer.valueOf(3), true), this.newWith(1, true, 2, false, 3, true).max(ObjectBooleanPair::compareTo));
    }

    @Test
    public void min_without_comparator()
    {
        Assertions.assertEquals(PrimitiveTuples.pair(Integer.valueOf(1), true), this.newWith(1, true, 2, false, 3, true).min(ObjectBooleanPair::compareTo));
    }

    @Test
    public void max_without_comparator()
    {
        Assertions.assertEquals(PrimitiveTuples.pair(Integer.valueOf(3), true), this.newWith(1, true, 2, false, 3, true).max(ObjectBooleanPair::compareTo));
    }

    @Test
    public void minBy()
    {
        Function<ObjectBooleanPair<Integer>, Integer> function = ObjectBooleanPair::getOne;
        Assertions.assertEquals(PrimitiveTuples.pair(Integer.valueOf(2), true), this.newWith(2, true, 3, false, 4, true).minBy(function));
    }

    @Test
    public void maxBy()
    {
        Function<ObjectBooleanPair<Integer>, Integer> function = object -> object.getOne() & 1;
        Assertions.assertEquals(PrimitiveTuples.pair(Integer.valueOf(3), false), this.newWith(2, true, 3, false, 4, true).maxBy(function));
    }

    @Test
    public void detectWith()
    {
        Assertions.assertEquals(PrimitiveTuples.pair(Integer.valueOf(2), false), this.newWith(1, true, 2, false, 3, true).detectWith(Object::equals, PrimitiveTuples.pair(Integer.valueOf(2), false)));
        Assertions.assertNull(this.newWith(1, true, 2, false, 3, true).detectWith(Object::equals, PrimitiveTuples.pair(true, Integer.valueOf(4))));
    }

    @Test
    public void detectIfNone()
    {
        Function0<ObjectBooleanPair<Integer>> function = () -> PrimitiveTuples.pair(Integer.valueOf(5), true);
        Assertions.assertEquals(PrimitiveTuples.pair(Integer.valueOf(2), false), this.newWith(1, true, 2, false, 3, true).detectIfNone(PrimitiveTuples.pair(Integer.valueOf(2), false)::equals, function));
        Assertions.assertEquals(PrimitiveTuples.pair(Integer.valueOf(5), true), this.newWith(1, true, 2, false, 3, true).detectIfNone(PrimitiveTuples.pair(true, Integer.valueOf(4))::equals, function));
    }

    @Test
    public void detectWithIfNoneBlock()
    {
        Function0<ObjectBooleanPair<Integer>> function = () -> PrimitiveTuples.pair(Integer.valueOf(5), true);
        Assertions.assertEquals(PrimitiveTuples.pair(Integer.valueOf(2), false), this.newWith(1, true, 2, false, 3, true).detectWithIfNone(
                Object::equals,
                PrimitiveTuples.pair(Integer.valueOf(2), false),
                function));
        Assertions.assertEquals(PrimitiveTuples.pair(Integer.valueOf(5), true), this.newWith(1, true, 2, false, 3, true).detectWithIfNone(
                Object::equals,
                PrimitiveTuples.pair(true, Integer.valueOf(4)),
                function));
    }

    @Test
    public void allSatisfy()
    {
        Assertions.assertTrue(this.newWith(1, true, 2, false, 3, true).allSatisfy(ObjectBooleanPair.class::isInstance));
        Assertions.assertFalse(this.newWith(1, true, 2, false, 3, true).allSatisfy(PrimitiveTuples.pair(Integer.valueOf(2), false)::equals));
    }

    @Test
    public void allSatisfyWith()
    {
        Assertions.assertTrue(this.newWith(1, true, 2, false, 3, true).allSatisfyWith(Predicates2.instanceOf(), ObjectBooleanPair.class));
        Assertions.assertFalse(this.newWith(1, true, 2, false, 3, true).allSatisfyWith(Object::equals, PrimitiveTuples.pair(Integer.valueOf(2), false)));
    }

    @Test
    public void noneSatisfy()
    {
        Assertions.assertTrue(this.newWith(1, true, 2, false, 3, true).noneSatisfy(Boolean.class::isInstance));
        Assertions.assertFalse(this.newWith(1, true, 2, false, 3, true).noneSatisfy(PrimitiveTuples.pair(Integer.valueOf(2), false)::equals));
    }

    @Test
    public void noneSatisfyWith()
    {
        Assertions.assertTrue(this.newWith(1, true, 2, false, 3, true).noneSatisfyWith(Predicates2.instanceOf(), Boolean.class));
        Assertions.assertFalse(this.newWith(1, true, 2, false, 3, true).noneSatisfyWith(Object::equals, PrimitiveTuples.pair(Integer.valueOf(2), false)));
    }

    @Test
    public void anySatisfy()
    {
        Assertions.assertTrue(this.newWith(1, true, 2, false, 3, true).anySatisfy(PrimitiveTuples.pair(Integer.valueOf(2), false)::equals));
        Assertions.assertFalse(this.newWith(1, true, 2, false, 3, true).anySatisfy(PrimitiveTuples.pair(true, Integer.valueOf(5))::equals));
    }

    @Test
    public void anySatisfyWith()
    {
        Assertions.assertTrue(this.newWith(1, true, 2, false, 3, true).anySatisfyWith(Object::equals, PrimitiveTuples.pair(Integer.valueOf(2), false)));
        Assertions.assertFalse(this.newWith(1, true, 2, false, 3, true).anySatisfyWith(Object::equals, PrimitiveTuples.pair(true, Integer.valueOf(5))));
    }

    @Test
    public void count()
    {
        Assertions.assertEquals(0, this.newWith(1, true, 2, false, 3, true).count(Boolean.class::isInstance));
        Assertions.assertEquals(3, this.newWith(1, true, 2, false, 3, true).count(ObjectBooleanPair.class::isInstance));
        Assertions.assertEquals(1, this.newWith(1, true, 2, false, 3, true).count(PrimitiveTuples.pair(Integer.valueOf(2), false)::equals));
    }

    @Test
    public void countWith()
    {
        Assertions.assertEquals(0, this.newWith(1, true, 2, false, 3, true).countWith(Predicates2.instanceOf(), Boolean.class));
        Assertions.assertEquals(3, this.newWith(1, true, 2, false, 3, true).countWith(Predicates2.instanceOf(), ObjectBooleanPair.class));
        Assertions.assertEquals(1, this.newWith(1, true, 2, false, 3, true).countWith(Object::equals, PrimitiveTuples.pair(Integer.valueOf(2), false)));
    }

    @Test
    public void collectIf()
    {
        Verify.assertContainsAll(
                this.newWith(1, true, 2, false, 3, true).collectIf(
                        ObjectBooleanPair.class::isInstance,
                        String::valueOf),
                "1:true", "2:false", "3:true");
        Verify.assertContainsAll(
                this.newWith(1, true, 2, false, 3, true).collectIf(
                        ObjectBooleanPair.class::isInstance,
                        String::valueOf,
                        UnifiedSet.<String>newSet()),
                "1:true", "2:false", "3:true");
    }

    @Test
    public void collectWith()
    {
        Assertions.assertEquals(
                Bags.mutable.of(5L, 7L, 9L),
                this.newWith(2, true, 3, false, 4, true).collectWith((argument1, argument2) -> (argument1.getOne() + argument1.getOne() + argument2), 1L).toBag());
    }

    @Test
    public void collectWith_target()
    {
        Assertions.assertEquals(
                Bags.mutable.of(5L, 7L, 9L),
                this.newWith(2, true, 3, false, 4, true).collectWith((argument1, argument2) -> (argument1.getOne() + argument1.getOne() + argument2), 1L, HashBag.<Long>newBag()));
    }

    @Test
    public void getFirst()
    {
        ObjectBooleanPair<Integer> first = this.newWith(1, true, 2, false, 3, true).getFirst();
        Assertions.assertTrue(PrimitiveTuples.pair(Integer.valueOf(1), true).equals(first)
                || PrimitiveTuples.pair(Integer.valueOf(2), false).equals(first)
                || PrimitiveTuples.pair(Integer.valueOf(3), true).equals(first));
        Assertions.assertEquals(PrimitiveTuples.pair(Integer.valueOf(1), true), this.newWith(1, true).getFirst());
    }

    @Test
    public void getLast()
    {
        ObjectBooleanPair<Integer> last = this.newWith(1, true, 2, false, 3, true).getLast();
        Assertions.assertTrue(PrimitiveTuples.pair(Integer.valueOf(1), true).equals(last)
                || PrimitiveTuples.pair(Integer.valueOf(2), false).equals(last)
                || PrimitiveTuples.pair(Integer.valueOf(3), true).equals(last));
        Assertions.assertEquals(PrimitiveTuples.pair(Integer.valueOf(1), true), this.newWith(1, true).getLast());
    }

    @Test
    public void isEmpty()
    {
        Verify.assertIterableEmpty(this.newWith());
        Verify.assertIterableNotEmpty(this.newWith(1, true));
        Assertions.assertTrue(this.newWith(1, true).notEmpty());
    }

    @Test
    public void iterator()
    {
        RichIterable<ObjectBooleanPair<Integer>> objects = this.newWith(1, true, 2, false, 3, true);
        MutableBag<ObjectBooleanPair<Integer>> actual = Bags.mutable.of();
        Iterator<ObjectBooleanPair<Integer>> iterator = objects.iterator();
        for (int i = objects.size(); i-- > 0; )
        {
            Assertions.assertTrue(iterator.hasNext());
            actual.add(iterator.next());
        }

        Assertions.assertFalse(iterator.hasNext());
        Verify.assertThrows(UnsupportedOperationException.class, iterator::remove);
        Assertions.assertEquals(objects.toBag(), actual);
    }

    @Test
    public void iterator_throws()
    {
        assertThrows(NoSuchElementException.class, () -> {
            RichIterable<ObjectBooleanPair<Integer>> objects = this.newWith(1, true, 2, false, 3, true);
            Iterator<ObjectBooleanPair<Integer>> iterator = objects.iterator();
            for (int i = objects.size(); i-- > 0; )
            {
                Assertions.assertTrue(iterator.hasNext());
                iterator.next();
            }
            Assertions.assertFalse(iterator.hasNext());
            iterator.next();
        });
    }

    @Test
    public void injectInto()
    {
        RichIterable<ObjectBooleanPair<Integer>> objects = this.newWith(2, true, 3, false, 4, true);
        Long result = objects.injectInto(1L, (Long argument1, ObjectBooleanPair<Integer> argument2) -> argument1 + argument2.getOne() + argument2.getOne());
        Assertions.assertEquals(Long.valueOf(19), result);
    }

    @Test
    public void injectIntoInt()
    {
        RichIterable<ObjectBooleanPair<Integer>> objects = this.newWith(2, true, 3, false, 4, true);
        int result = objects.injectInto(1, (int intParameter, ObjectBooleanPair<Integer> argument2) -> intParameter + argument2.getOne() + argument2.getOne());
        Assertions.assertEquals(19, result);
    }

    @Test
    public void injectIntoLong()
    {
        RichIterable<ObjectBooleanPair<Integer>> objects = this.newWith(2, true, 3, false, 4, true);
        long result = objects.injectInto(1L, (long parameter, ObjectBooleanPair<Integer> argument2) -> parameter + argument2.getOne() + argument2.getOne());
        Assertions.assertEquals(19, result);
    }

    @Test
    public void injectIntoDouble()
    {
        RichIterable<ObjectBooleanPair<Integer>> objects = this.newWith(2, true, 3, false, 4, true);
        double result = objects.injectInto(1.0, (parameter, argument2) -> parameter + argument2.getOne() + argument2.getOne());
        Assertions.assertEquals(19.0, result, 0.0);
    }

    @Test
    public void injectIntoFloat()
    {
        RichIterable<ObjectBooleanPair<Integer>> objects = this.newWith(2, true, 3, false, 4, true);
        float result = objects.injectInto(1.0f, (float parameter, ObjectBooleanPair<Integer> argument2) -> parameter + argument2.getOne() + argument2.getOne());
        Assertions.assertEquals(19.0, result, 0.0);
    }

    @Test
    public void sumFloat()
    {
        RichIterable<ObjectBooleanPair<Integer>> objects = this.newWith(2, true, 3, false, 4, true);
        double actual = objects.sumOfFloat(each -> (float) (each.getOne() + each.getOne()));
        Assertions.assertEquals(18.0, actual, 0.0);
    }

    @Test
    public void sumDouble()
    {
        RichIterable<ObjectBooleanPair<Integer>> objects = this.newWith(2, true, 3, false, 4, true);
        double actual = objects.sumOfDouble(each -> (double) (each.getOne() + each.getOne()));
        Assertions.assertEquals(18.0, actual, 0.0);
    }

    @Test
    public void sumInteger()
    {
        RichIterable<ObjectBooleanPair<Integer>> objects = this.newWith(2, true, 3, false, 4, true);
        long actual = objects.sumOfInt(each -> each.getOne() + each.getOne());
        Assertions.assertEquals(18, actual);
    }

    @Test
    public void sumLong()
    {
        RichIterable<ObjectBooleanPair<Integer>> objects = this.newWith(2, true, 3, false, 4, true);
        long actual = objects.sumOfLong(each -> (long) (each.getOne() + each.getOne()));
        Assertions.assertEquals(18, actual);
    }

    @Test
    public void toArray()
    {
        RichIterable<ObjectBooleanPair<Integer>> objects = this.newWith(1, true, 2, false, 3, true);
        Object[] array = objects.toArray();
        Verify.assertSize(3, array);
        ObjectBooleanPair<Integer>[] array2 = objects.toArray(new ObjectBooleanPair[3]);
        Verify.assertSize(3, array2);
    }

    @Test
    public void partition()
    {
        PartitionIterable<ObjectBooleanPair<Integer>> result = this.newWith(1, true, 2, false, 3, true).partition(PrimitiveTuples.pair(Integer.valueOf(2), false)::equals);
        Verify.assertContains(PrimitiveTuples.pair(Integer.valueOf(2), false), result.getSelected().toList());
        Verify.assertIterableSize(1, result.getSelected());
        Verify.assertContains(PrimitiveTuples.pair(Integer.valueOf(1), true), result.getRejected().toList());
        Verify.assertContains(PrimitiveTuples.pair(Integer.valueOf(3), true), result.getRejected().toList());
        Verify.assertIterableSize(2, result.getRejected());
    }

    @Test
    public void toList()
    {
        MutableList<ObjectBooleanPair<Integer>> list = this.newWith(1, true, 2, false, 3, true).toList();
        Verify.assertContainsAll(list, PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), false), PrimitiveTuples.pair(Integer.valueOf(3), true));
    }

    @Test
    public void toBag()
    {
        MutableBag<ObjectBooleanPair<Integer>> bag = this.newWith(1, true, 2, false, 3, true).toBag();
        Verify.assertContainsAll(bag, PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), false), PrimitiveTuples.pair(Integer.valueOf(3), true));
    }

    @Test
    public void toSortedList_natural_ordering()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(5, false, 1, true, 2, true);
        MutableList<ObjectBooleanPair<Integer>> list = pairs.toSortedList();
        Assertions.assertEquals(Lists.mutable.of(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), true), PrimitiveTuples.pair(Integer.valueOf(5), false)), list);
    }

    @Test
    public void toSortedList_with_comparator()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(5, false, 1, true, 2, true);
        MutableList<ObjectBooleanPair<Integer>> list = pairs.toSortedList(Comparators.reverseNaturalOrder());
        Assertions.assertEquals(Lists.mutable.of(PrimitiveTuples.pair(Integer.valueOf(5), false), PrimitiveTuples.pair(Integer.valueOf(2), true), PrimitiveTuples.pair(Integer.valueOf(1), true)), list);
    }

    @Test
    public void toSortedListBy()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(5, false, 1, true, 2, true);
        MutableList<ObjectBooleanPair<Integer>> list = pairs.toSortedListBy(String::valueOf);
        Assertions.assertEquals(Lists.mutable.of(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), true), PrimitiveTuples.pair(Integer.valueOf(5), false)), list);
    }

    @Test
    public void toSortedBag_natural_ordering()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(5, false, 1, true, 2, true);
        MutableSortedBag<ObjectBooleanPair<Integer>> bag = pairs.toSortedBag();
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), true), PrimitiveTuples.pair(Integer.valueOf(5), false)), bag);
    }

    @Test
    public void toSortedBag_with_comparator()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(5, false, 1, true, 2, true);
        MutableSortedBag<ObjectBooleanPair<Integer>> bag = pairs.toSortedBag(Comparators.reverseNaturalOrder());
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), PrimitiveTuples.pair(Integer.valueOf(5), false), PrimitiveTuples.pair(Integer.valueOf(2), true), PrimitiveTuples.pair(Integer.valueOf(1), true)), bag);
    }

    @Test
    public void toSortedBagBy()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(5, false, 1, true, 2, true);
        MutableSortedBag<ObjectBooleanPair<Integer>> bag = pairs.toSortedBagBy(String::valueOf);
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), true), PrimitiveTuples.pair(Integer.valueOf(5), false)), bag);
    }

    @Test
    public void toSortedSet_natural_ordering()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(5, false, 1, true, 2, true);
        MutableSortedSet<ObjectBooleanPair<Integer>> set = pairs.toSortedSet();
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), true), PrimitiveTuples.pair(Integer.valueOf(5), false)), set);
    }

    @Test
    public void toSortedSet_with_comparator()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(5, false, 1, true, 2, true);
        MutableSortedSet<ObjectBooleanPair<Integer>> set = pairs.toSortedSet(Comparators.reverseNaturalOrder());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder(),
                        PrimitiveTuples.pair(Integer.valueOf(5), false),
                        PrimitiveTuples.pair(Integer.valueOf(2), true),
                        PrimitiveTuples.pair(Integer.valueOf(1), true)),
                set);
    }

    @Test
    public void toSortedSetBy()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(5, false, 1, true, 2, true);
        MutableSortedSet<ObjectBooleanPair<Integer>> set = pairs.toSortedSetBy(String::valueOf);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), true), PrimitiveTuples.pair(Integer.valueOf(5), false)), set);
    }

    @Test
    public void toSet()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(1, true, 2, false, 3, true);
        MutableSet<ObjectBooleanPair<Integer>> set = pairs.toSet();
        Verify.assertContainsAll(set, PrimitiveTuples.pair(Integer.valueOf(1), true), PrimitiveTuples.pair(Integer.valueOf(2), false), PrimitiveTuples.pair(Integer.valueOf(3), true));
    }

    @Test
    public void toMap()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(1, true, 2, false, 3, true);
        MutableMap<String, String> map =
                pairs.toMap(String::valueOf, String::valueOf);
        Assertions.assertEquals(UnifiedMap.newWithKeysValues("1:true", "1:true", "2:false", "2:false", "3:true", "3:true"), map);
    }

    @Test
    public void toSortedMap()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(1, true, 2, false, 3, true);
        MutableSortedMap<String, String> map =
                pairs.toSortedMap(String::valueOf, String::valueOf);
        Assertions.assertEquals(TreeSortedMap.newMapWith("1:true", "1:true", "2:false", "2:false", "3:true", "3:true"), map);
    }

    @Test
    public void toSortedMap_with_comparator()
    {
        RichIterable<ObjectBooleanPair<Integer>> pairs = this.newWith(1, true, 2, false, 3, true);
        MutableSortedMap<String, String> map =
                pairs.toSortedMap(Comparators.reverseNaturalOrder(), String::valueOf, String::valueOf);
        Assertions.assertEquals(TreeSortedMap.newMapWith(Comparators.reverseNaturalOrder(), "1:true", "1:true", "2:false", "2:false", "3:true", "3:true"), map);
    }

    @Test
    public void testToString()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false);
        Assertions.assertTrue("[1:true, 2:false]".equals(collection.toString())
                || "[2:false, 1:true]".equals(collection.toString()));
    }

    @Test
    public void makeString()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Assertions.assertEquals(collection.toString(), '[' + collection.makeString() + ']');
    }

    @Test
    public void makeStringWithSeparator()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Assertions.assertEquals(collection.toString(), '[' + collection.makeString(", ") + ']');
    }

    @Test
    public void makeStringWithSeparatorAndStartAndEnd()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Assertions.assertEquals(collection.toString(), collection.makeString("[", ", ", "]"));
    }

    @Test
    public void appendString()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Appendable builder = new StringBuilder();
        collection.appendString(builder);
        Assertions.assertEquals(collection.toString(), '[' + builder.toString() + ']');
    }

    @Test
    public void appendStringWithSeparator()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Appendable builder = new StringBuilder();
        collection.appendString(builder, ", ");
        Assertions.assertEquals(collection.toString(), '[' + builder.toString() + ']');
    }

    @Test
    public void appendStringWithSeparatorAndStartAndEnd()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Appendable builder = new StringBuilder();
        collection.appendString(builder, "[", ", ", "]");
        Assertions.assertEquals(collection.toString(), builder.toString());
    }

    @Test
    public void groupBy()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Function<ObjectBooleanPair<Integer>, Boolean> function = object -> PrimitiveTuples.pair(Integer.valueOf(1), true).equals(object);

        Multimap<Boolean, ObjectBooleanPair<Integer>> multimap = collection.groupBy(function);
        Assertions.assertEquals(3, multimap.size());
        Assertions.assertTrue(multimap.containsKeyAndValue(Boolean.TRUE, PrimitiveTuples.pair(Integer.valueOf(1), true)));
        Assertions.assertTrue(multimap.containsKeyAndValue(Boolean.FALSE, PrimitiveTuples.pair(Integer.valueOf(2), false)));
        Assertions.assertTrue(multimap.containsKeyAndValue(Boolean.FALSE, PrimitiveTuples.pair(Integer.valueOf(3), true)));
    }

    @Test
    public void groupByEach()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Function<ObjectBooleanPair<Integer>, MutableList<Boolean>> function = object -> Lists.mutable.of(PrimitiveTuples.pair(Integer.valueOf(1), true).equals(object));

        Multimap<Boolean, ObjectBooleanPair<Integer>> multimap = collection.groupByEach(function);
        Assertions.assertEquals(3, multimap.size());
        Assertions.assertTrue(multimap.containsKeyAndValue(Boolean.TRUE, PrimitiveTuples.pair(Integer.valueOf(1), true)));
        Assertions.assertTrue(multimap.containsKeyAndValue(Boolean.FALSE, PrimitiveTuples.pair(Integer.valueOf(2), false)));
        Assertions.assertTrue(multimap.containsKeyAndValue(Boolean.FALSE, PrimitiveTuples.pair(Integer.valueOf(3), true)));
    }

    @Test
    public void zip()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false);
        RichIterable<Pair<ObjectBooleanPair<Integer>, Integer>> result = collection.zip(Interval.oneTo(5));

        Assertions.assertTrue(Bags.mutable.of(Tuples.pair(PrimitiveTuples.pair(Integer.valueOf(1), true), 1), Tuples.pair(PrimitiveTuples.pair(Integer.valueOf(2), false), 2)).equals(result.toBag())
                || Bags.mutable.of(Tuples.pair(PrimitiveTuples.pair(Integer.valueOf(2), false), 1), Tuples.pair(PrimitiveTuples.pair(Integer.valueOf(1), true), 2)).equals(result.toBag()));
    }

    @Test
    public void zipWithIndex()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false);
        RichIterable<Pair<ObjectBooleanPair<Integer>, Integer>> result = collection.zipWithIndex();
        Assertions.assertTrue(Bags.mutable.of(Tuples.pair(PrimitiveTuples.pair(Integer.valueOf(1), true), 0), Tuples.pair(PrimitiveTuples.pair(Integer.valueOf(2), false), 1)).equals(result.toBag())
                || Bags.mutable.of(Tuples.pair(PrimitiveTuples.pair(Integer.valueOf(2), false), 0), Tuples.pair(PrimitiveTuples.pair(Integer.valueOf(1), true), 1)).equals(result.toBag()));
    }

    @Test
    public void chunk()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Assertions.assertEquals(Bags.immutable.of(FastList.newListWith(PrimitiveTuples.pair(Integer.valueOf(1), true)),
                        FastList.newListWith(PrimitiveTuples.pair(Integer.valueOf(2), false)),
                        FastList.newListWith(PrimitiveTuples.pair(Integer.valueOf(3), true))),
                collection.chunk(1).toBag());
    }

    @Test
    public void chunk_zero_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
            collection.chunk(0);
        });
    }

    @Test
    public void chunk_large_size()
    {
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, true, 2, false, 3, true);
        Verify.assertIterableSize(3, collection.chunk(10).getFirst());
    }

    @Test
    public void empty()
    {
        Verify.assertIterableEmpty(this.newWith());
        Assertions.assertTrue(this.newWith().isEmpty());
        Assertions.assertFalse(this.newWith().notEmpty());
    }

    @Test
    public void notEmpty()
    {
        RichIterable<ObjectBooleanPair<Integer>> notEmpty = this.newWith(1, true);
        Verify.assertIterableNotEmpty(notEmpty);
    }

    @Test
    public void aggregateByMutating()
    {
        Procedure2<AtomicInteger, ObjectBooleanPair<Integer>> sumAggregator = (aggregate, value) -> aggregate.addAndGet(value.getOne());
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(2, true, 3, false, 4, true);
        MapIterable<String, AtomicInteger> aggregation = collection.aggregateInPlaceBy(String::valueOf, AtomicInteger::new, sumAggregator);
        Assertions.assertEquals(4, aggregation.get("4:true").intValue());
        Assertions.assertEquals(3, aggregation.get("3:false").intValue());
        Assertions.assertEquals(2, aggregation.get("2:true").intValue());
    }

    @Test
    public void aggregateByNonMutating()
    {
        Function2<Integer, ObjectBooleanPair<Integer>, Integer> sumAggregator = (aggregate, value) -> aggregate + value.getOne();
        RichIterable<ObjectBooleanPair<Integer>> collection = this.newWith(1, false, 1, false, 2, true);
        MapIterable<String, Integer> aggregation = collection.aggregateBy(String::valueOf, () -> 0, sumAggregator);
        Assertions.assertEquals(2, aggregation.get("2:true").intValue());
        Assertions.assertEquals(1, aggregation.get("1:false").intValue());
    }
}
