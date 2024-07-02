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

package com.gs.collections.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.gs.collections.api.BooleanIterable;
import com.gs.collections.api.ByteIterable;
import com.gs.collections.api.CharIterable;
import com.gs.collections.api.DoubleIterable;
import com.gs.collections.api.FloatIterable;
import com.gs.collections.api.IntIterable;
import com.gs.collections.api.LongIterable;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.ShortIterable;
import com.gs.collections.api.bag.MutableBag;
import com.gs.collections.api.bag.sorted.MutableSortedBag;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.collection.primitive.MutableBooleanCollection;
import com.gs.collections.api.collection.primitive.MutableByteCollection;
import com.gs.collections.api.collection.primitive.MutableCharCollection;
import com.gs.collections.api.collection.primitive.MutableDoubleCollection;
import com.gs.collections.api.collection.primitive.MutableFloatCollection;
import com.gs.collections.api.collection.primitive.MutableIntCollection;
import com.gs.collections.api.collection.primitive.MutableLongCollection;
import com.gs.collections.api.collection.primitive.MutableShortCollection;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MapIterable;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.map.primitive.ObjectDoubleMap;
import com.gs.collections.api.map.primitive.ObjectLongMap;
import com.gs.collections.api.map.sorted.MutableSortedMap;
import com.gs.collections.api.multimap.Multimap;
import com.gs.collections.api.multimap.MutableMultimap;
import com.gs.collections.api.partition.PartitionIterable;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.set.sorted.MutableSortedSet;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.bag.mutable.primitive.BooleanHashBag;
import com.gs.collections.impl.bag.mutable.primitive.ByteHashBag;
import com.gs.collections.impl.bag.mutable.primitive.CharHashBag;
import com.gs.collections.impl.bag.mutable.primitive.DoubleHashBag;
import com.gs.collections.impl.bag.mutable.primitive.FloatHashBag;
import com.gs.collections.impl.bag.mutable.primitive.IntHashBag;
import com.gs.collections.impl.bag.mutable.primitive.LongHashBag;
import com.gs.collections.impl.bag.mutable.primitive.ShortHashBag;
import com.gs.collections.impl.bag.sorted.mutable.TreeBag;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.IntegerPredicates;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.factory.PrimitiveFunctions;
import com.gs.collections.impl.block.function.AddFunction;
import com.gs.collections.impl.block.function.NegativeIntervalFunction;
import com.gs.collections.impl.block.function.PassThruFunction0;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.factory.Bags;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.factory.Sets;
import com.gs.collections.impl.factory.primitive.BooleanBags;
import com.gs.collections.impl.factory.primitive.ByteBags;
import com.gs.collections.impl.factory.primitive.CharBags;
import com.gs.collections.impl.factory.primitive.DoubleBags;
import com.gs.collections.impl.factory.primitive.FloatBags;
import com.gs.collections.impl.factory.primitive.IntBags;
import com.gs.collections.impl.factory.primitive.LongBags;
import com.gs.collections.impl.factory.primitive.ShortBags;
import com.gs.collections.impl.list.Interval;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class AbstractRichIterableTestCase
{
    protected abstract <T> RichIterable<T> newWith(T... littleElements);

    @Test
    public void testNewCollection()
    {
        RichIterable<Object> collection = this.newWith();
        Verify.assertIterableEmpty(collection);
        Verify.assertIterableSize(0, collection);
    }

    @Test
    public void equalsAndHashCode()
    {
        Verify.assertEqualsAndHashCode(this.newWith(1, 2, 3), this.newWith(1, 2, 3));
        Assertions.assertNotEquals(this.newWith(1, 2, 3), this.newWith(1, 2));
    }

    @Test
    public void contains()
    {
        RichIterable<Integer> collection = this.newWith(1, 2, 3, 4);
        Assertions.assertTrue(collection.contains(1));
        Assertions.assertTrue(collection.contains(4));
        Assertions.assertFalse(collection.contains(5));
    }

    @Test
    public void containsAllIterable()
    {
        RichIterable<Integer> collection = this.newWith(1, 2, 3, 4);
        Assertions.assertTrue(collection.containsAllIterable(FastList.newListWith(1, 2)));
        Assertions.assertFalse(collection.containsAllIterable(FastList.newListWith(1, 5)));
    }

    @Test
    public void containsAllArray()
    {
        RichIterable<Integer> collection = this.newWith(1, 2, 3, 4);
        Assertions.assertTrue(collection.containsAllArguments(1, 2));
        Assertions.assertFalse(collection.containsAllArguments(1, 5));
    }

    @Test
    public void containsAllCollection()
    {
        RichIterable<Integer> collection = this.newWith(1, 2, 3, 4);
        Assertions.assertTrue(collection.containsAll(FastList.newListWith(1, 2)));
        Assertions.assertFalse(collection.containsAll(FastList.newListWith(1, 5)));
    }

    @Test
    public void tap()
    {
        MutableList<Integer> tapResult = Lists.mutable.of();
        RichIterable<Integer> collection = this.newWith(1, 2, 3, 4);
        Assertions.assertSame(collection, collection.tap(tapResult::add));
        Assertions.assertEquals(collection.toList(), tapResult);
    }

    @Test
    public void forEach()
    {
        MutableList<Integer> result = Lists.mutable.of();
        RichIterable<Integer> collection = this.newWith(1, 2, 3, 4);
        collection.forEach(CollectionAddProcedure.on(result));
        Verify.assertSize(4, result);
        Verify.assertContainsAll(result, 1, 2, 3, 4);
    }

    @Test
    public void forEachWith()
    {
        MutableList<Integer> result = Lists.mutable.of();
        RichIterable<Integer> collection = this.newWith(1, 2, 3, 4);
        collection.forEachWith((argument1, argument2) -> result.add(argument1 + argument2), 0);
        Verify.assertSize(4, result);
        Verify.assertContainsAll(result, 1, 2, 3, 4);
    }

    @Test
    public void forEachWithIndex()
    {
        MutableBag<Integer> elements = Bags.mutable.of();
        MutableBag<Integer> indexes = Bags.mutable.of();
        RichIterable<Integer> collection = this.newWith(1, 2, 3, 4);
        collection.forEachWithIndex((object, index) -> {
            elements.add(object);
            indexes.add(index);
        });
        Assertions.assertEquals(Bags.mutable.of(1, 2, 3, 4), elements);
        Assertions.assertEquals(Bags.mutable.of(0, 1, 2, 3), indexes);
    }

    @Test
    public void select()
    {
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4, 5).select(Predicates.lessThan(3)), 1, 2);
        RichIterable<Integer> result = this.newWith(-1, 2, 3, 4, 5).select(Predicates.lessThan(3));
        Verify.assertNotContains(3, result);
        Verify.assertNotContains(4, result);
        Verify.assertNotContains(5, result);
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4, 5).select(Predicates.lessThan(3), UnifiedSet.<Integer>newSet()), 1, 2);
    }

    @Test
    public void selectWith()
    {
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4, 5).selectWith(Predicates2.<Integer>lessThan(), 3), 1, 2);
        RichIterable<Integer> result = this.newWith(-1, 2, 3, 4, 5).selectWith(Predicates2.<Integer>lessThan(), 3);
        Verify.assertNotContains(3, result);
        Verify.assertNotContains(4, result);
        Verify.assertNotContains(5, result);
        Verify.assertContainsAll(
                this.newWith(1, 2, 3, 4, 5).selectWith(
                        Predicates2.<Integer>lessThan(),
                        3),
                1, 2);
    }

    @Test
    public void selectWith_target()
    {
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4, 5).selectWith(Predicates2.<Integer>lessThan(), 3, HashBag.<Integer>newBag()), 1, 2);
        Verify.denyContainsAny(this.newWith(-1, 2, 3, 4, 5).selectWith(Predicates2.<Integer>lessThan(), 3, HashBag.<Integer>newBag()), 3, 4, 5);
        Verify.assertContainsAll(
                this.newWith(1, 2, 3, 4, 5).selectWith(Predicates2.<Integer>lessThan(), 3, HashBag.<Integer>newBag()),
                1, 2);
    }

    @Test
    public void reject()
    {
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4).reject(Predicates.lessThan(3)), 3, 4);
        Verify.assertContainsAll(
                this.newWith(1, 2, 3, 4).reject(Predicates.lessThan(3), UnifiedSet.<Integer>newSet()), 3, 4);
    }

    @Test
    public void rejectWith()
    {
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4).rejectWith(Predicates2.<Integer>lessThan(), 3), 3, 4);
        Verify.assertContainsAll(
                this.newWith(1, 2, 3, 4).rejectWith(Predicates2.<Integer>lessThan(), 3, UnifiedSet.<Integer>newSet()),
                3, 4);
    }

    @Test
    public void rejectWith_target()
    {
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4).rejectWith(Predicates2.<Integer>lessThan(), 3, HashBag.<Integer>newBag()), 3, 4);
        Verify.assertContainsAll(
                this.newWith(1, 2, 3, 4).rejectWith(Predicates2.<Integer>lessThan(), 3, UnifiedSet.<Integer>newSet()),
                3, 4);
    }

    @Test
    public void selectInstancesOf()
    {
        RichIterable<Number> numbers = this.<Number>newWith(1, 2.0, 3, 4.0, 5);
        Assertions.assertEquals(HashBag.newBagWith(1, 3, 5), numbers.selectInstancesOf(Integer.class).toBag());
        Assertions.assertEquals(HashBag.newBagWith(1, 2.0, 3, 4.0, 5), numbers.selectInstancesOf(Number.class).toBag());
    }

    @Test
    public void collect()
    {
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4).collect(String::valueOf), "1", "2", "3", "4");
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4).collect(String::valueOf, UnifiedSet.<String>newSet()), "1", "2", "3", "4");
    }

    @Test
    public void collectBoolean()
    {
        BooleanIterable result = this.newWith(1, 0).collectBoolean(PrimitiveFunctions.integerIsPositive());
        Assertions.assertEquals(BooleanBags.mutable.of(true, false), result.toBag());
        Assertions.assertEquals(BooleanBags.mutable.of(true, false), BooleanBags.mutable.ofAll(result));
    }

    @Test
    public void collectBooleanWithTarget()
    {
        MutableBooleanCollection target = new BooleanArrayList();
        BooleanIterable result = this.newWith(1, 0).collectBoolean(PrimitiveFunctions.integerIsPositive(), target);
        Assertions.assertSame(target, result, "Target list sent as parameter not returned");
        Assertions.assertEquals(BooleanBags.mutable.of(true, false), result.toBag());
    }

    @Test
    public void collectBooleanWithBagTarget()
    {
        BooleanHashBag target = new BooleanHashBag();
        BooleanHashBag result = this.newWith(1, 0).collectBoolean(PrimitiveFunctions.integerIsPositive(), target);
        Assertions.assertSame(target, result, "Target list sent as parameter not returned");
        Assertions.assertEquals(BooleanHashBag.newBagWith(true, false), result);
    }

    @Test
    public void collectByte()
    {
        ByteIterable result = this.newWith(1, 2, 3, 4).collectByte(PrimitiveFunctions.unboxIntegerToByte());
        Assertions.assertEquals(ByteBags.mutable.of((byte) 1, (byte) 2, (byte) 3, (byte) 4), result.toBag());
        Assertions.assertEquals(ByteBags.mutable.of((byte) 1, (byte) 2, (byte) 3, (byte) 4), ByteBags.mutable.ofAll(result));
    }

    @Test
    public void collectByteWithTarget()
    {
        MutableByteCollection target = new ByteArrayList();
        ByteIterable result = this.newWith(1, 2, 3, 4).collectByte(PrimitiveFunctions.unboxIntegerToByte(), target);
        Assertions.assertSame(target, result, "Target list sent as parameter not returned");
        Assertions.assertEquals(ByteHashBag.newBagWith((byte) 1, (byte) 2, (byte) 3, (byte) 4), result.toBag());
    }

    @Test
    public void collectByteWithBagTarget()
    {
        ByteHashBag target = new ByteHashBag();
        ByteHashBag result = this.newWith(1, 2, 3, 4).collectByte(PrimitiveFunctions.unboxIntegerToByte(), target);
        Assertions.assertSame(target, result, "Target list sent as parameter not returned");
        Assertions.assertEquals(ByteHashBag.newBagWith((byte) 1, (byte) 2, (byte) 3, (byte) 4), result);
    }

    @Test
    public void collectChar()
    {
        CharIterable result = this.newWith(1, 2, 3, 4).collectChar(PrimitiveFunctions.unboxIntegerToChar());
        Assertions.assertEquals(CharBags.mutable.of((char) 1, (char) 2, (char) 3, (char) 4), result.toBag());
        Assertions.assertEquals(CharBags.mutable.of((char) 1, (char) 2, (char) 3, (char) 4), CharBags.mutable.ofAll(result));
    }

    @Test
    public void collectCharWithTarget()
    {
        MutableCharCollection target = new CharArrayList();
        CharIterable result = this.newWith(1, 2, 3, 4).collectChar(PrimitiveFunctions.unboxIntegerToChar(), target);
        Assertions.assertSame(target, result, "Target list sent as parameter not returned");
        Assertions.assertEquals(CharHashBag.newBagWith((char) 1, (char) 2, (char) 3, (char) 4), result.toBag());
    }

    @Test
    public void collectCharWithBagTarget()
    {
        CharHashBag target = new CharHashBag();
        CharHashBag result = this.newWith(1, 2, 3, 4).collectChar(PrimitiveFunctions.unboxIntegerToChar(), target);
        Assertions.assertSame(target, result, "Target list sent as parameter not returned");
        Assertions.assertEquals(CharHashBag.newBagWith((char) 1, (char) 2, (char) 3, (char) 4), result);
    }

    @Test
    public void collectDouble()
    {
        DoubleIterable result = this.newWith(1, 2, 3, 4).collectDouble(PrimitiveFunctions.unboxIntegerToDouble());
        Assertions.assertEquals(DoubleBags.mutable.of(1.0d, 2.0d, 3.0d, 4.0d), result.toBag());
        Assertions.assertEquals(DoubleBags.mutable.of(1.0d, 2.0d, 3.0d, 4.0d), DoubleBags.mutable.ofAll(result));
    }

    @Test
    public void collectDoubleWithTarget()
    {
        MutableDoubleCollection target = new DoubleArrayList();
        DoubleIterable result = this.newWith(1, 2, 3, 4).collectDouble(PrimitiveFunctions.unboxIntegerToDouble(), target);
        Assertions.assertSame(target, result, "Target list sent as parameter not returned");
        Assertions.assertEquals(DoubleHashBag.newBagWith(1.0d, 2.0d, 3.0d, 4.0d), result.toBag());
    }

    @Test
    public void collectDoubleWithBagTarget()
    {
        DoubleHashBag target = new DoubleHashBag();
        DoubleHashBag result = this.newWith(1, 2, 3, 4).collectDouble(PrimitiveFunctions.unboxIntegerToDouble(), target);
        Assertions.assertSame(target, result, "Target list sent as parameter not returned");
        Assertions.assertEquals(DoubleHashBag.newBagWith(1.0d, 2.0d, 3.0d, 4.0d), result);
    }

    @Test
    public void collectFloat()
    {
        FloatIterable result = this.newWith(1, 2, 3, 4).collectFloat(PrimitiveFunctions.unboxIntegerToFloat());
        Assertions.assertEquals(FloatBags.mutable.of(1.0f, 2.0f, 3.0f, 4.0f), result.toBag());
        Assertions.assertEquals(FloatBags.mutable.of(1.0f, 2.0f, 3.0f, 4.0f), FloatBags.mutable.ofAll(result));
    }

    @Test
    public void collectFloatWithTarget()
    {
        MutableFloatCollection target = new FloatArrayList();
        FloatIterable result = this.newWith(1, 2, 3, 4).collectFloat(PrimitiveFunctions.unboxIntegerToFloat(), target);
        Assertions.assertSame(target, result, "Target list sent as parameter not returned");
        Assertions.assertEquals(FloatHashBag.newBagWith(1.0f, 2.0f, 3.0f, 4.0f), result.toBag());
    }

    @Test
    public void collectFloatWithBagTarget()
    {
        FloatHashBag target = new FloatHashBag();
        FloatHashBag result = this.newWith(1, 2, 3, 4).collectFloat(PrimitiveFunctions.unboxIntegerToFloat(), target);
        Assertions.assertSame(target, result, "Target list sent as parameter not returned");
        Assertions.assertEquals(FloatHashBag.newBagWith(1.0f, 2.0f, 3.0f, 4.0f), result);
    }

    @Test
    public void collectInt()
    {
        IntIterable result = this.newWith(1, 2, 3, 4).collectInt(PrimitiveFunctions.unboxIntegerToInt());
        Assertions.assertEquals(IntBags.mutable.of(1, 2, 3, 4), result.toBag());
        Assertions.assertEquals(IntBags.mutable.of(1, 2, 3, 4), IntBags.mutable.ofAll(result));
    }

    @Test
    public void collectIntWithTarget()
    {
        MutableIntCollection target = new IntArrayList();
        IntIterable result = this.newWith(1, 2, 3, 4).collectInt(PrimitiveFunctions.unboxIntegerToInt(), target);
        Assertions.assertSame(target, result, "Target list sent as parameter not returned");
        Assertions.assertEquals(IntHashBag.newBagWith(1, 2, 3, 4), result.toBag());
    }

    @Test
    public void collectIntWithBagTarget()
    {
        IntHashBag target = new IntHashBag();
        IntHashBag result = this.newWith(1, 2, 3, 4).collectInt(PrimitiveFunctions.unboxIntegerToInt(), target);
        Assertions.assertSame(target, result, "Target list sent as parameter not returned");
        Assertions.assertEquals(IntHashBag.newBagWith(1, 2, 3, 4), result);
    }

    @Test
    public void collectLong()
    {
        LongIterable result = this.newWith(1, 2, 3, 4).collectLong(PrimitiveFunctions.unboxIntegerToLong());
        Assertions.assertEquals(LongBags.mutable.of(1, 2, 3, 4), result.toBag());
        Assertions.assertEquals(LongBags.mutable.of(1, 2, 3, 4), LongBags.mutable.ofAll(result));
    }

    @Test
    public void collectLongWithTarget()
    {
        MutableLongCollection target = new LongArrayList();
        LongIterable result = this.newWith(1, 2, 3, 4).collectLong(PrimitiveFunctions.unboxIntegerToLong(), target);
        Assertions.assertSame(target, result, "Target list sent as parameter not returned");
        Assertions.assertEquals(LongHashBag.newBagWith(1, 2, 3, 4), result.toBag());
    }

    @Test
    public void collectLongWithBagTarget()
    {
        LongHashBag target = new LongHashBag();
        LongHashBag result = this.newWith(1, 2, 3, 4).collectLong(PrimitiveFunctions.unboxIntegerToLong(), target);
        Assertions.assertSame(target, result, "Target list sent as parameter not returned");
        Assertions.assertEquals(LongHashBag.newBagWith(1, 2, 3, 4), result);
    }

    @Test
    public void collectShort()
    {
        ShortIterable result = this.newWith(1, 2, 3, 4).collectShort(PrimitiveFunctions.unboxIntegerToShort());
        Assertions.assertEquals(ShortBags.mutable.of((short) 1, (short) 2, (short) 3, (short) 4), result.toBag());
        Assertions.assertEquals(ShortBags.mutable.of((short) 1, (short) 2, (short) 3, (short) 4), ShortBags.mutable.ofAll(result));
    }

    @Test
    public void collectShortWithTarget()
    {
        MutableShortCollection target = new ShortArrayList();
        ShortIterable result = this.newWith(1, 2, 3, 4).collectShort(PrimitiveFunctions.unboxIntegerToShort(), target);
        Assertions.assertSame(target, result, "Target list sent as parameter not returned");
        Assertions.assertEquals(ShortHashBag.newBagWith((short) 1, (short) 2, (short) 3, (short) 4), result.toBag());
    }

    @Test
    public void collectShortWithBagTarget()
    {
        ShortHashBag target = new ShortHashBag();
        ShortHashBag result = this.newWith(1, 2, 3, 4).collectShort(PrimitiveFunctions.unboxIntegerToShort(), target);
        Assertions.assertSame(target, result, "Target list sent as parameter not returned");
        Assertions.assertEquals(ShortHashBag.newBagWith((short) 1, (short) 2, (short) 3, (short) 4), result);
    }

    @Test
    public void flatCollect()
    {
        RichIterable<Integer> collection = this.newWith(1, 2, 3, 4);
        Function<Integer, MutableList<String>> function = object -> FastList.newListWith(String.valueOf(object));

        Verify.assertListsEqual(
                FastList.newListWith("1", "2", "3", "4"),
                collection.flatCollect(function).toSortedList());

        Verify.assertSetsEqual(
                UnifiedSet.newSetWith("1", "2", "3", "4"),
                collection.flatCollect(function, UnifiedSet.<String>newSet()));
    }

    @Test
    public void detect()
    {
        Assertions.assertEquals(Integer.valueOf(3), this.newWith(1, 2, 3, 4, 5).detect(Integer.valueOf(3)::equals));
        Assertions.assertNull(this.newWith(1, 2, 3, 4, 5).detect(Integer.valueOf(6)::equals));
    }

    @Test
    public void min_empty_throws()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.<Integer>newWith().min(Integer::compareTo);
        });
    }

    @Test
    public void max_empty_throws()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.<Integer>newWith().max(Integer::compareTo);
        });
    }

    @Test
    public void min_null_throws()
    {
        assertThrows(NullPointerException.class, () -> {
            this.newWith(1, null, 2).min(Integer::compareTo);
        });
    }

    @Test
    public void max_null_throws()
    {
        assertThrows(NullPointerException.class, () -> {
            this.newWith(1, null, 2).max(Integer::compareTo);
        });
    }

    @Test
    public void min()
    {
        Assertions.assertEquals(Integer.valueOf(1), this.newWith(1, 3, 2).min(Integer::compareTo));
    }

    @Test
    public void max()
    {
        Assertions.assertEquals(Integer.valueOf(3), this.newWith(1, 3, 2).max(Integer::compareTo));
    }

    @Test
    public void min_null_throws_without_comparator()
    {
        assertThrows(NullPointerException.class, () -> {
            this.newWith(1, null, 2).min();
        });
    }

    @Test
    public void max_null_throws_without_comparator()
    {
        assertThrows(NullPointerException.class, () -> {
            this.newWith(1, null, 2).max();
        });
    }

    @Test
    public void min_without_comparator()
    {
        Assertions.assertEquals(Integer.valueOf(1), this.newWith(3, 1, 2).min());
    }

    @Test
    public void max_without_comparator()
    {
        Assertions.assertEquals(Integer.valueOf(3), this.newWith(1, 3, 2).max());
    }

    @Test
    public void min_null_safe()
    {
        RichIterable<Integer> integers = this.newWith(1, 3, 2, null);
        Assertions.assertEquals(Integer.valueOf(1), integers.min(Comparators.safeNullsHigh(Integer::compareTo)));
        Assertions.assertNull(integers.min(Comparators.safeNullsLow(Integer::compareTo)));
    }

    @Test
    public void max_null_safe()
    {
        RichIterable<Integer> integers = this.newWith(1, 3, 2, null);
        Assertions.assertEquals(Integer.valueOf(3), integers.max(Comparators.safeNullsLow(Integer::compareTo)));
        Assertions.assertNull(integers.max(Comparators.safeNullsHigh(Integer::compareTo)));
    }

    @Test
    public void minBy()
    {
        Assertions.assertEquals(Integer.valueOf(1), this.newWith(1, 3, 2).minBy(String::valueOf));
    }

    @Test
    public void maxBy()
    {
        Assertions.assertEquals(Integer.valueOf(3), this.newWith(1, 3, 2).maxBy(String::valueOf));
    }

    @Test
    public void minBy_null_throws()
    {
        assertThrows(NullPointerException.class, () -> {
            this.newWith(1, null, 2).minBy(Integer::valueOf);
        });
    }

    @Test
    public void maxBy_null_throws()
    {
        assertThrows(NullPointerException.class, () -> {
            this.newWith(1, null, 2).maxBy(Integer::valueOf);
        });
    }

    @Test
    public void detectWith()
    {
        Assertions.assertEquals(Integer.valueOf(3), this.newWith(1, 2, 3, 4, 5).detectWith(Object::equals, 3));
        Assertions.assertNull(this.newWith(1, 2, 3, 4, 5).detectWith(Object::equals, 6));
    }

    @Test
    public void detectIfNone()
    {
        Assertions.assertEquals(Integer.valueOf(3), this.newWith(1, 2, 3, 4, 5).detectIfNone(Integer.valueOf(3)::equals, () -> 6));
        Assertions.assertEquals(Integer.valueOf(6), this.newWith(1, 2, 3, 4, 5).detectIfNone(Integer.valueOf(6)::equals, () -> 6));
    }

    @Test
    public void detectWithIfNoneBlock()
    {
        Function0<Integer> function = new PassThruFunction0<>(-42);
        Assertions.assertEquals(
                Integer.valueOf(5),
                this.newWith(1, 2, 3, 4, 5).detectWithIfNone(
                        Predicates2.<Integer>greaterThan(),
                        4,
                        function));
        Assertions.assertEquals(
                Integer.valueOf(-42),
                this.newWith(1, 2, 3, 4, 5).detectWithIfNone(
                        Predicates2.<Integer>lessThan(),
                        0,
                        function));
    }

    @Test
    public void allSatisfy()
    {
        Assertions.assertTrue(this.newWith(1, 2, 3).allSatisfy(Integer.class::isInstance));
        Assertions.assertFalse(this.newWith(1, 2, 3).allSatisfy(Integer.valueOf(1)::equals));
    }

    @Test
    public void allSatisfyWith()
    {
        Assertions.assertTrue(this.newWith(1, 2, 3).allSatisfyWith(Predicates2.instanceOf(), Integer.class));
        Assertions.assertFalse(this.newWith(1, 2, 3).allSatisfyWith(Object::equals, 1));
    }

    @Test
    public void noneSatisfy()
    {
        Assertions.assertTrue(this.newWith(1, 2, 3).noneSatisfy(Boolean.class::isInstance));
        Assertions.assertFalse(this.newWith(1, 1, 3).noneSatisfy(Integer.valueOf(1)::equals));
        Assertions.assertTrue(this.newWith(1, 2, 3).noneSatisfy(Integer.valueOf(4)::equals));
    }

    @Test
    public void noneSatisfyWith()
    {
        Assertions.assertTrue(this.newWith(1, 2, 3).noneSatisfyWith(Predicates2.instanceOf(), Boolean.class));
        Assertions.assertFalse(this.newWith(1, 2, 3).noneSatisfyWith(Object::equals, 1));
    }

    @Test
    public void anySatisfy()
    {
        Assertions.assertFalse(this.newWith(1, 2, 3).anySatisfy(String.class::isInstance));
        Assertions.assertTrue(this.newWith(1, 2, 3).anySatisfy(Integer.class::isInstance));
    }

    @Test
    public void anySatisfyWith()
    {
        Assertions.assertFalse(this.newWith(1, 2, 3).anySatisfyWith(Predicates2.instanceOf(), String.class));
        Assertions.assertTrue(this.newWith(1, 2, 3).anySatisfyWith(Predicates2.instanceOf(), Integer.class));
    }

    @Test
    public void count()
    {
        Assertions.assertEquals(3, this.newWith(1, 2, 3).count(Integer.class::isInstance));
    }

    @Test
    public void countWith()
    {
        Assertions.assertEquals(3, this.newWith(1, 2, 3).countWith(Predicates2.instanceOf(), Integer.class));
    }

    @Test
    public void collectIf()
    {
        Verify.assertContainsAll(
                this.newWith(1, 2, 3).collectIf(
                        Integer.class::isInstance,
                        Object::toString),
                "1", "2", "3");
        Verify.assertContainsAll(
                this.newWith(1, 2, 3).collectIf(
                        Integer.class::isInstance,
                        Object::toString,
                        UnifiedSet.<String>newSet()),
                "1", "2", "3");
    }

    @Test
    public void collectWith()
    {
        Assertions.assertEquals(
                Bags.mutable.of(2, 3, 4),
                this.newWith(1, 2, 3).collectWith(AddFunction.INTEGER, 1).toBag());
    }

    @Test
    public void collectWith_target()
    {
        Assertions.assertEquals(
                Bags.mutable.of(2, 3, 4),
                this.newWith(1, 2, 3).collectWith(AddFunction.INTEGER, 1, Lists.mutable.empty()).toBag());
        Assertions.assertEquals(
                Bags.mutable.of(2, 3, 4),
                this.newWith(1, 2, 3).collectWith(AddFunction.INTEGER, 1, Bags.mutable.empty()));
        Assertions.assertEquals(
                Sets.mutable.of(2, 3, 4),
                this.newWith(1, 2, 3).collectWith(AddFunction.INTEGER, 1, Sets.mutable.empty()));
    }

    @Test
    public void getFirst()
    {
        Assertions.assertEquals(Integer.valueOf(1), this.newWith(1, 2, 3).getFirst());
        Assertions.assertNotEquals(Integer.valueOf(3), this.newWith(1, 2, 3).getFirst());
    }

    @Test
    public void getLast()
    {
        Assertions.assertNotEquals(Integer.valueOf(1), this.newWith(1, 2, 3).getLast());
        Assertions.assertEquals(Integer.valueOf(3), this.newWith(1, 2, 3).getLast());
    }

    @Test
    public void isEmpty()
    {
        Verify.assertIterableEmpty(this.newWith());
        Verify.assertIterableNotEmpty(this.newWith(1, 2));
        Assertions.assertTrue(this.newWith(1, 2).notEmpty());
    }

    @Test
    public void iterator()
    {
        RichIterable<Integer> objects = this.newWith(1, 2, 3);
        Iterator<Integer> iterator = objects.iterator();
        for (int i = objects.size(); i-- > 0; )
        {
            Assertions.assertTrue(iterator.hasNext());
            Integer integer = iterator.next();
            Assertions.assertEquals(3, integer.intValue() + i);
        }
        Assertions.assertFalse(iterator.hasNext());
    }

    @Test
    public void iterator_throws()
    {
        assertThrows(NoSuchElementException.class, () -> {
            RichIterable<Integer> objects = this.newWith(1, 2, 3);
            Iterator<Integer> iterator = objects.iterator();
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
        RichIterable<Integer> objects = this.newWith(1, 2, 3);
        Integer result = objects.injectInto(1, AddFunction.INTEGER);
        Assertions.assertEquals(Integer.valueOf(7), result);
        int sum = objects.injectInto(0, AddFunction.INTEGER_TO_INT);
        Assertions.assertEquals(6, sum);
    }

    @Test
    public void injectIntoInt()
    {
        RichIterable<Integer> objects = this.newWith(1, 2, 3);
        int result = objects.injectInto(1, AddFunction.INTEGER_TO_INT);
        Assertions.assertEquals(7, result);
        int sum = objects.injectInto(0, AddFunction.INTEGER_TO_INT);
        Assertions.assertEquals(6, sum);
    }

    @Test
    public void injectIntoLong()
    {
        RichIterable<Integer> objects = this.newWith(1, 2, 3);
        long result = objects.injectInto(1, AddFunction.INTEGER_TO_LONG);
        Assertions.assertEquals(7, result);
        long sum = objects.injectInto(0, AddFunction.INTEGER_TO_LONG);
        Assertions.assertEquals(6, sum);
    }

    @Test
    public void injectIntoDouble()
    {
        RichIterable<Integer> objects = this.newWith(1, 2, 3);
        double result = objects.injectInto(1, AddFunction.INTEGER_TO_DOUBLE);
        Assertions.assertEquals(7.0d, result, 0.001);
        double sum = objects.injectInto(0, AddFunction.INTEGER_TO_DOUBLE);
        Assertions.assertEquals(6.0d, sum, 0.001);
    }

    @Test
    public void injectIntoFloat()
    {
        RichIterable<Integer> objects = this.newWith(1, 2, 3);
        float result = objects.injectInto(1, AddFunction.INTEGER_TO_FLOAT);
        Assertions.assertEquals(7.0f, result, 0.001f);
        float sum = objects.injectInto(0, AddFunction.INTEGER_TO_FLOAT);
        Assertions.assertEquals(6.0f, sum, 0.001f);
    }

    @Test
    public void sumFloat()
    {
        RichIterable<Integer> objects = this.newWith(1, 2, 3);
        float expected = objects.injectInto(0, AddFunction.INTEGER_TO_FLOAT);
        double actual = objects.sumOfFloat(Integer::floatValue);
        Assertions.assertEquals(expected, actual, 0.001);
    }

    @Test
    public void sumFloatConsistentRounding1()
    {
        MutableList<Integer> list = Interval.oneTo(100_000).toList().shuffleThis();

        // The test only ensures the consistency/stability of rounding. This is not meant to test the "correctness" of the float calculation result.
        // Indeed the lower bits of this calculation result are always incorrect due to the information loss of original float values.
        Assertions.assertEquals(
                1.082323233761663,
                this.newWith(list.toArray(new Integer[]{})).sumOfFloat(i -> 1.0f / (i.floatValue() * i.floatValue() * i.floatValue() * i.floatValue())),
                1.0e-15);
    }

    @Test
    public void sumFloatConsistentRounding2()
    {
        MutableList<Integer> list = Interval.oneTo(99_999).toList().shuffleThis();

        // The test only ensures the consistency/stability of rounding. This is not meant to test the "correctness" of the float calculation result.
        // Indeed the lower bits of this calculation result are always incorrect due to the information loss of original float values.
        Assertions.assertEquals(
                33333.00099340081,
                this.newWith(list.toArray(new Integer[]{})).sumOfFloat(i -> 1.0f / 3.0f),
                0.0);
    }

    @Test
    public void sumDouble()
    {
        RichIterable<Integer> objects = this.newWith(1, 2, 3);
        double expected = objects.injectInto(0, AddFunction.INTEGER_TO_DOUBLE);
        double actual = objects.sumOfDouble(Integer::doubleValue);
        Assertions.assertEquals(expected, actual, 0.001);
    }

    @Test
    public void sumDoubleConsistentRounding1()
    {
        MutableList<Integer> list = Interval.oneTo(100_000).toList().shuffleThis();

        Assertions.assertEquals(
                1.082323233711138,
                this.newWith(list.toArray(new Integer[]{})).sumOfDouble(i -> 1.0d / (i.doubleValue() * i.doubleValue() * i.doubleValue() * i.doubleValue())),
                1.0e-15);
    }

    @Test
    public void sumDoubleConsistentRounding2()
    {
        MutableList<Integer> list = Interval.oneTo(99_999).toList().shuffleThis();

        Assertions.assertEquals(
                33333.0,
                this.newWith(list.toArray(new Integer[]{})).sumOfDouble(i -> 1.0d / 3.0d),
                0.0);
    }

    @Test
    public void sumInteger()
    {
        RichIterable<Integer> objects = this.newWith(1, 2, 3);
        long expected = objects.injectInto(0L, AddFunction.INTEGER_TO_LONG);
        long actual = objects.sumOfInt(integer -> integer);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void sumLong()
    {
        RichIterable<Integer> objects = this.newWith(1, 2, 3);
        long expected = objects.injectInto(0L, AddFunction.INTEGER_TO_LONG);
        long actual = objects.sumOfLong(Integer::longValue);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void sumByInt()
    {
        RichIterable<Integer> values = this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        ObjectLongMap<Integer> result = values.sumByInt(i -> i % 2, e -> e);
        Assertions.assertEquals(25, result.get(1));
        Assertions.assertEquals(30, result.get(0));
    }

    @Test
    public void sumByFloat()
    {
        RichIterable<Integer> values = this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        ObjectDoubleMap<Integer> result = values.sumByFloat(f -> f % 2, e -> e);
        Assertions.assertEquals(25.0f, result.get(1), 0.0);
        Assertions.assertEquals(30.0f, result.get(0), 0.0);
    }

    @Test
    public void sumByFloatConsistentRounding()
    {
        MutableList<Integer> group1 = Interval.oneTo(100_000).toList().shuffleThis();
        MutableList<Integer> group2 = Interval.fromTo(100_001, 200_000).toList().shuffleThis();
        MutableList<Integer> integers = Lists.mutable.withAll(group1);
        integers.addAll(group2);
        ObjectDoubleMap<Integer> result = integers.sumByFloat(
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
        RichIterable<Integer> values = this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        ObjectLongMap<Integer> result = values.sumByLong(l -> l % 2, e -> e);
        Assertions.assertEquals(25, result.get(1));
        Assertions.assertEquals(30, result.get(0));
    }

    @Test
    public void sumByDouble()
    {
        RichIterable<Integer> values = this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        ObjectDoubleMap<Integer> result = values.sumByDouble(d -> d % 2, e -> e);
        Assertions.assertEquals(25.0d, result.get(1), 0.0);
        Assertions.assertEquals(30.0d, result.get(0), 0.0);
    }

    @Test
    public void sumByDoubleConsistentRounding()
    {
        MutableList<Integer> group1 = Interval.oneTo(100_000).toList().shuffleThis();
        MutableList<Integer> group2 = Interval.fromTo(100_001, 200_000).toList().shuffleThis();
        MutableList<Integer> integers = Lists.mutable.withAll(group1);
        integers.addAll(group2);
        ObjectDoubleMap<Integer> result = integers.sumByDouble(
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
    public void toArray()
    {
        RichIterable<Integer> objects = this.newWith(1, 2, 3);
        Object[] array = objects.toArray();
        Verify.assertSize(3, array);
        Integer[] array2 = objects.toArray(new Integer[3]);
        Verify.assertSize(3, array2);
    }

    @Test
    public void partition()
    {
        RichIterable<Integer> integers = this.newWith(-3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        PartitionIterable<Integer> result = integers.partition(IntegerPredicates.isEven());
        Assertions.assertEquals(this.newWith(-2, 0, 2, 4, 6, 8), result.getSelected());
        Assertions.assertEquals(this.newWith(-3, -1, 1, 3, 5, 7, 9), result.getRejected());
    }

    @Test
    public void partitionWith()
    {
        RichIterable<Integer> integers = this.newWith(-3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        PartitionIterable<Integer> result = integers.partitionWith(Predicates2.in(), FastList.newListWith(-2, 0, 2, 4, 6, 8));
        Assertions.assertEquals(this.newWith(-2, 0, 2, 4, 6, 8), result.getSelected());
        Assertions.assertEquals(this.newWith(-3, -1, 1, 3, 5, 7, 9), result.getRejected());
    }

    @Test
    public void toList()
    {
        MutableList<Integer> list = this.newWith(1, 2, 3, 4).toList();
        Verify.assertContainsAll(list, 1, 2, 3, 4);
    }

    @Test
    public void toBag()
    {
        MutableBag<Integer> bag = this.newWith(1, 2, 3, 4).toBag();
        Verify.assertContainsAll(bag, 1, 2, 3, 4);
    }

    @Test
    public void toSortedList_natural_ordering()
    {
        RichIterable<Integer> integers = this.newWith(2, 1, 5, 3, 4);
        MutableList<Integer> list = integers.toSortedList();
        Verify.assertStartsWith(list, 1, 2, 3, 4, 5);
    }

    @Test
    public void toSortedList_with_comparator()
    {
        RichIterable<Integer> integers = this.newWith(2, 4, 1, 3);
        MutableList<Integer> list = integers.toSortedList(Collections.<Integer>reverseOrder());
        Assertions.assertEquals(FastList.newListWith(4, 3, 2, 1), list);
    }

    @Test
    public void toSortedList_with_null()
    {
        assertThrows(NullPointerException.class, () -> {
            this.newWith(2, 4, null, 1, 3).toSortedList();
        });
    }

    @Test
    public void toSortedBag_natural_ordering()
    {
        RichIterable<Integer> integers = this.newWith(2, 2, 5, 3, 4);
        MutableSortedBag<Integer> bag = integers.toSortedBag();
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(2, 2, 3, 4, 5), bag);
    }

    @Test
    public void toSortedBag_with_comparator()
    {
        RichIterable<Integer> integers = this.newWith(2, 4, 2, 3);
        MutableSortedBag<Integer> bag = integers.toSortedBag(Collections.<Integer>reverseOrder());
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.<Integer>reverseOrder(), 4, 3, 2, 2), bag);
    }

    @Test
    public void toSortedBag_with_null()
    {
        assertThrows(NullPointerException.class, () -> {
            this.newWith(2, 4, null, 1, 2).toSortedBag();
        });
    }

    @Test
    public void toSortedBagBy()
    {
        RichIterable<Integer> integers = this.newWith(2, 4, 1, 3);
        MutableSortedBag<Integer> bag = integers.toSortedBagBy(String::valueOf);
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(1, 2, 3, 4), bag);
    }

    @Test
    public void toSortedListBy()
    {
        RichIterable<Integer> integers = this.newWith(2, 4, 1, 3);
        MutableList<Integer> list = integers.toSortedListBy(String::valueOf);
        Assertions.assertEquals(FastList.newListWith(1, 2, 3, 4), list);
    }

    @Test
    public void toSortedSet_natural_ordering()
    {
        RichIterable<Integer> integers = this.newWith(2, 4, 1, 3, 2, 1, 3, 4);
        MutableSortedSet<Integer> set = integers.toSortedSet();
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3, 4), set);
    }

    @Test
    public void toSortedSet_with_comparator()
    {
        RichIterable<Integer> integers = this.newWith(2, 4, 4, 2, 1, 4, 1, 3);
        MutableSortedSet<Integer> set = integers.toSortedSet(Collections.<Integer>reverseOrder());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.<Integer>reverseOrder(), 1, 2, 3, 4), set);
    }

    @Test
    public void toSortedSetBy()
    {
        RichIterable<Integer> integers = this.newWith(2, 4, 1, 3);
        MutableSortedSet<Integer> set = integers.toSortedSetBy(String::valueOf);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3, 4), set);
    }

    @Test
    public void toSortedListBy_with_null()
    {
        assertThrows(NullPointerException.class, () -> {
            this.newWith(2, 4, null, 1, 3).toSortedListBy(Functions.getIntegerPassThru());
        });
    }

    @Test
    public void toSet()
    {
        RichIterable<Integer> integers = this.newWith(1, 2, 3, 4);
        MutableSet<Integer> set = integers.toSet();
        Verify.assertContainsAll(set, 1, 2, 3, 4);
    }

    @Test
    public void toMap()
    {
        RichIterable<Integer> integers = this.newWith(1, 2, 3, 4);
        MutableMap<String, String> map =
                integers.toMap(Object::toString, Object::toString);
        Assertions.assertEquals(UnifiedMap.newWithKeysValues("1", "1", "2", "2", "3", "3", "4", "4"), map);
    }

    @Test
    public void toSortedMap()
    {
        RichIterable<Integer> integers = this.newWith(1, 2, 3);
        MutableSortedMap<Integer, String> map = integers.toSortedMap(Functions.getIntegerPassThru(), Object::toString);
        Verify.assertMapsEqual(TreeSortedMap.newMapWith(1, "1", 2, "2", 3, "3"), map);
        Verify.assertListsEqual(FastList.newListWith(1, 2, 3), map.keySet().toList());
    }

    @Test
    public void toSortedMap_with_comparator()
    {
        RichIterable<Integer> integers = this.newWith(1, 2, 3);
        MutableSortedMap<Integer, String> map = integers.toSortedMap(Comparators.<Integer>reverseNaturalOrder(),
                Functions.getIntegerPassThru(), Object::toString);
        Verify.assertMapsEqual(TreeSortedMap.newMapWith(Comparators.<Integer>reverseNaturalOrder(), 1, "1", 2, "2", 3, "3"), map);
        Verify.assertListsEqual(FastList.newListWith(3, 2, 1), map.keySet().toList());
    }

    @Test
    public void testToString()
    {
        RichIterable<Object> collection = this.newWith(1, 2, 3);
        Assertions.assertEquals("[1, 2, 3]", collection.toString());
    }

    @Test
    public void makeString()
    {
        RichIterable<Object> collection = this.newWith(1, 2, 3);
        Assertions.assertEquals(collection.toString(), '[' + collection.makeString() + ']');
    }

    @Test
    public void makeStringWithSeparator()
    {
        RichIterable<Object> collection = this.newWith(1, 2, 3);
        Assertions.assertEquals(collection.toString(), '[' + collection.makeString(", ") + ']');
    }

    @Test
    public void makeStringWithSeparatorAndStartAndEnd()
    {
        RichIterable<Object> collection = this.newWith(1, 2, 3);
        Assertions.assertEquals(collection.toString(), collection.makeString("[", ", ", "]"));
    }

    @Test
    public void appendString()
    {
        RichIterable<Object> collection = this.newWith(1, 2, 3);
        Appendable builder = new StringBuilder();
        collection.appendString(builder);
        Assertions.assertEquals(collection.toString(), '[' + builder.toString() + ']');
    }

    @Test
    public void appendStringWithSeparator()
    {
        RichIterable<Object> collection = this.newWith(1, 2, 3);
        Appendable builder = new StringBuilder();
        collection.appendString(builder, ", ");
        Assertions.assertEquals(collection.toString(), '[' + builder.toString() + ']');
    }

    @Test
    public void appendStringWithSeparatorAndStartAndEnd()
    {
        RichIterable<Object> collection = this.newWith(1, 2, 3);
        Appendable builder = new StringBuilder();
        collection.appendString(builder, "[", ", ", "]");
        Assertions.assertEquals(collection.toString(), builder.toString());
    }

    @Test
    public void groupBy()
    {
        RichIterable<Integer> collection = this.newWith(1, 2, 3, 4, 5, 6, 7);
        Function<Integer, Boolean> isOddFunction = object -> IntegerPredicates.isOdd().accept(object);

        MutableMap<Boolean, RichIterable<Integer>> expected =
                UnifiedMap.newWithKeysValues(
                        Boolean.TRUE, this.newWith(1, 3, 5, 7),
                        Boolean.FALSE, this.newWith(2, 4, 6));

        Multimap<Boolean, Integer> multimap = collection.groupBy(isOddFunction);
        Assertions.assertEquals(expected, multimap.toMap());

        Function<Integer, Boolean> function = (Integer object) -> true;
        MutableMultimap<Boolean, Integer> multimap2 = collection.groupBy(
                isOddFunction,
                this.<Integer>newWith().groupBy(function).toMutable());
        Assertions.assertEquals(expected, multimap2.toMap());
    }

    @Test
    public void groupByEach()
    {
        RichIterable<Integer> collection = this.newWith(1, 2, 3, 4, 5, 6, 7);

        NegativeIntervalFunction function = new NegativeIntervalFunction();
        MutableMultimap<Integer, Integer> expected = this.<Integer>newWith().groupByEach(function).toMutable();
        for (int i = 1; i < 8; i++)
        {
            expected.putAll(-i, Interval.fromTo(i, 7));
        }

        Multimap<Integer, Integer> actual =
                collection.groupByEach(function);
        Assertions.assertEquals(expected, actual);

        Multimap<Integer, Integer> actualWithTarget =
                collection.groupByEach(function, this.<Integer>newWith().groupByEach(function).toMutable());
        Assertions.assertEquals(expected, actualWithTarget);
    }

    @Test
    public void groupByUniqueKey()
    {
        RichIterable<Integer> collection = this.newWith(1, 2, 3);
        Assertions.assertEquals(UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3), collection.groupByUniqueKey(id -> id));
    }

    @Test
    public void groupByUniqueKey_throws_for_duplicate()
    {
        assertThrows(IllegalStateException.class, () -> {
            RichIterable<Integer> collection = this.newWith(1, 2, 3);
            collection.groupByUniqueKey(id -> 2);
        });
    }

    @Test
    public void groupByUniqueKey_target()
    {
        RichIterable<Integer> collection = this.newWith(1, 2, 3);
        Assertions.assertEquals(
                UnifiedMap.newWithKeysValues(0, 0, 1, 1, 2, 2, 3, 3),
                collection.groupByUniqueKey(id -> id, UnifiedMap.newWithKeysValues(0, 0)));
    }

    @Test
    public void groupByUniqueKey_target_throws_for_duplicate()
    {
        assertThrows(IllegalStateException.class, () -> {
            RichIterable<Integer> collection = this.newWith(1, 2, 3);
            Assertions.assertEquals(
                    UnifiedMap.newWithKeysValues(0, 0, 1, 1, 2, 2, 3, 3),
                    collection.groupByUniqueKey(id -> id, UnifiedMap.newWithKeysValues(2, 2)));
        });
    }

    @Test
    public void zip()
    {
        RichIterable<String> collection = this.newWith("1", "2", "3", "4", "5", "6", "7");
        List<Object> nulls = Collections.nCopies(collection.size(), null);
        List<Object> nullsPlusOne = Collections.nCopies(collection.size() + 1, null);
        List<Object> nullsMinusOne = Collections.nCopies(collection.size() - 1, null);

        RichIterable<Pair<String, Object>> pairs = collection.zip(nulls);
        Assertions.assertEquals(
                collection.toSet(),
                pairs.collect((Function<Pair<String, ?>, String>) Pair::getOne).toSet());
        Assertions.assertEquals(
                nulls,
                pairs.collect((Function<Pair<?, Object>, Object>) Pair::getTwo, Lists.mutable.of()));

        RichIterable<Pair<String, Object>> pairsPlusOne = collection.zip(nullsPlusOne);
        Assertions.assertEquals(
                collection.toSet(),
                pairsPlusOne.collect((Function<Pair<String, ?>, String>) Pair::getOne).toSet());
        Assertions.assertEquals(nulls, pairsPlusOne.collect((Function<Pair<?, Object>, Object>) Pair::getTwo, Lists.mutable.of()));

        RichIterable<Pair<String, Object>> pairsMinusOne = collection.zip(nullsMinusOne);
        Assertions.assertEquals(collection.size() - 1, pairsMinusOne.size());
        Assertions.assertTrue(collection.containsAllIterable(pairsMinusOne.collect((Function<Pair<String, ?>, String>) Pair::getOne)));

        Assertions.assertEquals(
                collection.zip(nulls).toSet(),
                collection.zip(nulls, UnifiedSet.<Pair<String, Object>>newSet()));
    }

    @Test
    public void zipWithIndex()
    {
        RichIterable<String> collection = this.newWith("1", "2", "3", "4", "5", "6", "7");
        RichIterable<Pair<String, Integer>> pairs = collection.zipWithIndex();

        Assertions.assertEquals(
                collection.toSet(),
                pairs.collect((Function<Pair<String, ?>, String>) Pair::getOne).toSet());
        Assertions.assertEquals(
                Interval.zeroTo(collection.size() - 1).toSet(),
                pairs.collect((Function<Pair<?, Integer>, Integer>) Pair::getTwo, UnifiedSet.<Integer>newSet()));

        Assertions.assertEquals(
                collection.zipWithIndex().toSet(),
                collection.zipWithIndex(UnifiedSet.<Pair<String, Integer>>newSet()));
    }

    @Test
    public void chunk()
    {
        RichIterable<String> collection = this.newWith("1", "2", "3", "4", "5", "6", "7");
        RichIterable<RichIterable<String>> groups = collection.chunk(2);
        RichIterable<Integer> sizes = groups.collect(RichIterable::size);
        Assertions.assertEquals(FastList.newListWith(2, 2, 2, 1), sizes);
    }

    @Test
    public void chunk_zero_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            RichIterable<String> collection = this.newWith("1", "2", "3", "4", "5", "6", "7");
            collection.chunk(0);
        });
    }

    @Test
    public void chunk_large_size()
    {
        RichIterable<String> collection = FastList.newListWith("1", "2", "3", "4", "5", "6", "7");
        Assertions.assertEquals(collection, collection.chunk(10).getFirst());
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
        RichIterable<Integer> notEmpty = this.newWith(1);
        Verify.assertIterableNotEmpty(notEmpty);
    }

    @Test
    public void aggregateByMutating()
    {
        RichIterable<Integer> collection = this.newWith(1, 1, 1, 2, 2, 3);
        MapIterable<String, AtomicInteger> aggregation = collection.aggregateInPlaceBy(String::valueOf, AtomicInteger::new, AtomicInteger::addAndGet);
        if (collection instanceof Set)
        {
            Assertions.assertEquals(1, aggregation.get("1").intValue());
            Assertions.assertEquals(2, aggregation.get("2").intValue());
            Assertions.assertEquals(3, aggregation.get("3").intValue());
        }
        else
        {
            Assertions.assertEquals(3, aggregation.get("1").intValue());
            Assertions.assertEquals(4, aggregation.get("2").intValue());
            Assertions.assertEquals(3, aggregation.get("3").intValue());
        }
    }

    @Test
    public void aggregateByNonMutating()
    {
        MapIterable<String, Integer> aggregation =
                this.newWith(1, 1, 1, 2, 2, 3).aggregateBy(
                        Object::toString,
                        () -> 0,
                        (integer1, integer2) -> integer1 + integer2);

        if (this.newWith(1, 1, 1, 2, 2, 3) instanceof Set)
        {
            Assertions.assertEquals(1, aggregation.get("1").intValue());
            Assertions.assertEquals(2, aggregation.get("2").intValue());
            Assertions.assertEquals(3, aggregation.get("3").intValue());
        }
        else
        {
            Assertions.assertEquals(3, aggregation.get("1").intValue());
            Assertions.assertEquals(4, aggregation.get("2").intValue());
            Assertions.assertEquals(3, aggregation.get("3").intValue());
        }
    }
}
