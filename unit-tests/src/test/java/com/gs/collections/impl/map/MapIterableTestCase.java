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

package com.gs.collections.impl.map;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.gs.collections.api.BooleanIterable;
import com.gs.collections.api.ByteIterable;
import com.gs.collections.api.CharIterable;
import com.gs.collections.api.DoubleIterable;
import com.gs.collections.api.FloatIterable;
import com.gs.collections.api.IntIterable;
import com.gs.collections.api.LazyIterable;
import com.gs.collections.api.LongIterable;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.ShortIterable;
import com.gs.collections.api.bag.Bag;
import com.gs.collections.api.bag.MutableBag;
import com.gs.collections.api.bag.sorted.MutableSortedBag;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.function.primitive.CharFunction;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MapIterable;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.map.primitive.ObjectDoubleMap;
import com.gs.collections.api.map.primitive.ObjectLongMap;
import com.gs.collections.api.multimap.Multimap;
import com.gs.collections.api.multimap.MutableMultimap;
import com.gs.collections.api.multimap.set.MutableSetMultimap;
import com.gs.collections.api.partition.PartitionIterable;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.set.sorted.MutableSortedSet;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.IntegerWithCast;
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
import com.gs.collections.impl.block.factory.StringFunctions;
import com.gs.collections.impl.block.function.AddFunction;
import com.gs.collections.impl.block.function.NegativeIntervalFunction;
import com.gs.collections.impl.block.function.PassThruFunction0;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.factory.Bags;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.factory.Maps;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.map.sorted.mutable.TreeSortedMap;
import com.gs.collections.impl.math.IntegerSum;
import com.gs.collections.impl.math.Sum;
import com.gs.collections.impl.math.SumProcedure;
import com.gs.collections.impl.multimap.list.FastListMultimap;
import com.gs.collections.impl.multimap.set.UnifiedSetMultimap;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.set.sorted.mutable.TreeSortedSet;
import com.gs.collections.impl.test.SerializeTestHelper;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.gs.collections.impl.factory.Iterables.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class MapIterableTestCase
{
    protected abstract <K, V> MapIterable<K, V> newMap();

    protected abstract <K, V> MapIterable<K, V> newMapWithKeyValue(
            K key1, V value1);

    protected abstract <K, V> MapIterable<K, V> newMapWithKeysValues(
            K key1, V value1,
            K key2, V value2);

    protected abstract <K, V> MapIterable<K, V> newMapWithKeysValues(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3);

    protected abstract <K, V> MapIterable<K, V> newMapWithKeysValues(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4);

    @Test
    public void equalsAndHashCode()
    {
        MapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        Verify.assertPostSerializedEqualsAndHashCode(map);
        Verify.assertEqualsAndHashCode(Maps.mutable.of(1, "1", 2, "2", 3, "3"), map);
        Verify.assertEqualsAndHashCode(Maps.immutable.of(1, "1", 2, "2", 3, "3"), map);

        Assertions.assertNotEquals(map, this.newMapWithKeysValues(1, "1", 2, "2"));
        Assertions.assertNotEquals(map, this.newMapWithKeysValues(1, "1", 2, "2", 3, "3", 4, "4"));
        Assertions.assertNotEquals(map, this.newMapWithKeysValues(1, "1", 2, "2", 4, "4"));

        Verify.assertEqualsAndHashCode(
                Maps.immutable.with(1, "1", 2, "2", 3, null),
                this.newMapWithKeysValues(1, "1", 2, "2", 3, null));
    }

    @Test
    public void serialization()
    {
        MapIterable<Integer, String> original = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        MapIterable<Integer, String> copy = SerializeTestHelper.serializeDeserialize(original);
        Verify.assertIterableSize(3, copy);
        Assertions.assertEquals(original, copy);
    }

    @Test
    public void isEmpty()
    {
        Assertions.assertFalse(this.newMapWithKeysValues(1, "1", 2, "2").isEmpty());
        Assertions.assertTrue(this.newMap().isEmpty());
    }

    @Test
    public void notEmpty()
    {
        Assertions.assertFalse(this.newMap().notEmpty());
        Assertions.assertTrue(this.newMapWithKeysValues(1, "1", 2, "2").notEmpty());
    }

    @Test
    public void ifPresentApply()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2);
        Assertions.assertEquals("1", map.ifPresentApply("1", String::valueOf));
        Assertions.assertNull(map.ifPresentApply("3", String::valueOf));
    }

    @Test
    public void getIfAbsent_function()
    {
        MapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        Assertions.assertNull(map.get(4));
        Assertions.assertEquals("4", map.getIfAbsent(4, new PassThruFunction0<>("4")));
        Assertions.assertEquals("3", map.getIfAbsent(3, new PassThruFunction0<>("3")));
        Assertions.assertNull(map.get(4));
    }

    @Test
    public void getIfAbsent()
    {
        MapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        Assertions.assertNull(map.get(4));
        Assertions.assertEquals("4", map.getIfAbsentValue(4, "4"));
        Assertions.assertEquals("3", map.getIfAbsentValue(3, "3"));
        Assertions.assertNull(map.get(4));
    }

    @Test
    public void getIfAbsentWith()
    {
        MapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        Assertions.assertNull(map.get(4));
        Assertions.assertEquals("4", map.getIfAbsentWith(4, String::valueOf, 4));
        Assertions.assertEquals("3", map.getIfAbsentWith(3, String::valueOf, 3));
        Assertions.assertNull(map.get(4));
    }

    @Test
    public void tap()
    {
        MutableList<String> tapResult = Lists.mutable.of();
        MapIterable<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two", 3, "Three", 4, "Four");
        Assertions.assertSame(map, map.tap(tapResult::add));
        Assertions.assertEquals(tapResult.toList(), tapResult);
    }

    @Test
    public void forEach()
    {
        MutableBag<String> result = Bags.mutable.of();
        MapIterable<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two", 3, "Three", 4, "Four");
        map.forEach(CollectionAddProcedure.on(result));
        Assertions.assertEquals(Bags.mutable.of("One", "Two", "Three", "Four"), result);
    }

    @Test
    public void forEachWith()
    {
        MutableList<Integer> result = Lists.mutable.of();
        MapIterable<Integer, Integer> map = this.newMapWithKeysValues(-1, 1, -2, 2, -3, 3, -4, 4);
        map.forEachWith((argument1, argument2) -> result.add(argument1 + argument2), 10);
        Verify.assertSize(4, result);
        Verify.assertContainsAll(result, 11, 12, 13, 14);
    }

    @Test
    public void forEachWithIndex()
    {
        MutableList<String> result = Lists.mutable.of();
        MapIterable<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two", 3, "Three", 4, "Four");
        map.forEachWithIndex((value, index) -> {
            result.add(value);
            result.add(String.valueOf(index));
        });
        Verify.assertSize(8, result);
        Verify.assertContainsAll(
                result,
                "One", "Two", "Three", "Four", // Map values
                "0", "1", "2", "3");  // Stringified index values
    }

    @Test
    public void forEachKey()
    {
        UnifiedSet<Integer> result = UnifiedSet.newSet();
        MapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        map.forEachKey(CollectionAddProcedure.on(result));
        Verify.assertSetsEqual(UnifiedSet.newSetWith(1, 2, 3), result);
    }

    @Test
    public void forEachValue()
    {
        UnifiedSet<String> result = UnifiedSet.newSet();
        MapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        map.forEachValue(CollectionAddProcedure.on(result));
        Verify.assertSetsEqual(UnifiedSet.newSetWith("1", "2", "3"), result);
    }

    @Test
    public void forEachKeyValue()
    {
        UnifiedMap<Integer, String> result = UnifiedMap.newMap();
        MapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        map.forEachKeyValue(result::put);
        Assertions.assertEquals(UnifiedMap.newWithKeysValues(1, "1", 2, "2", 3, "3"), result);

        MutableBag<String> result2 = Bags.mutable.of();
        MapIterable<Integer, String> map2 = this.newMapWithKeysValues(1, "One", 2, "Two", 3, "Three");
        map2.forEachKeyValue((key, value) -> result2.add(key + value));
        Assertions.assertEquals(Bags.mutable.of("1One", "2Two", "3Three"), result2);
    }

    @Test
    public void flipUniqueValues()
    {
        MapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        MapIterable<String, Integer> result = map.flipUniqueValues();
        Assertions.assertEquals(UnifiedMap.newWithKeysValues("1", 1, "2", 2, "3", 3), result);

        Verify.assertThrows(IllegalStateException.class, () -> this.newMapWithKeysValues(1, "2", 2, "2").flipUniqueValues());
    }

    @Test
    public void collectMap()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");
        MapIterable<Integer, String> actual = map.collect((Function2<String, String, Pair<Integer, String>>) (argument1, argument2) -> Tuples.pair(Integer.valueOf(argument1), argument1 + ':' + new StringBuilder(argument2).reverse()));
        Assertions.assertEquals(UnifiedMap.newWithKeysValues(1, "1:enO", 2, "2:owT", 3, "3:eerhT"), actual);
    }

    @Test
    public void collectBoolean()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("One", "true", "Two", "nah", "Three", "TrUe");
        BooleanIterable actual = map.collectBoolean(Boolean::parseBoolean);
        Assertions.assertEquals(BooleanHashBag.newBagWith(true, false, true), actual.toBag());
    }

    @Test
    public void collectBooleanWithTarget()
    {
        BooleanHashBag target = new BooleanHashBag();
        MapIterable<String, String> map = this.newMapWithKeysValues("One", "true", "Two", "nah", "Three", "TrUe");
        BooleanHashBag result = map.collectBoolean(Boolean::parseBoolean, target);
        Assertions.assertSame(target, result, "Target sent as parameter not returned");
        Assertions.assertEquals(BooleanHashBag.newBagWith(true, false, true), result.toBag());
    }

    @Test
    public void collectByte()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("One", "1", "Two", "2", "Three", "3");
        ByteIterable actual = map.collectByte(Byte::parseByte);
        Assertions.assertEquals(ByteHashBag.newBagWith((byte) 1, (byte) 2, (byte) 3), actual.toBag());
    }

    @Test
    public void collectByteWithTarget()
    {
        ByteHashBag target = new ByteHashBag();
        MapIterable<String, String> map = this.newMapWithKeysValues("One", "1", "Two", "2", "Three", "3");
        ByteHashBag result = map.collectByte(Byte::parseByte, target);
        Assertions.assertSame(target, result, "Target sent as parameter not returned");
        Assertions.assertEquals(ByteHashBag.newBagWith((byte) 1, (byte) 2, (byte) 3), result.toBag());
    }

    @Test
    public void collectChar()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("One", "A1", "Two", "B", "Three", "C#++");
        CharIterable actual = map.collectChar((CharFunction<String>) string -> string.charAt(0));
        Assertions.assertEquals(CharHashBag.newBagWith('A', 'B', 'C'), actual.toBag());
    }

    @Test
    public void collectCharWithTarget()
    {
        CharHashBag target = new CharHashBag();
        MapIterable<String, String> map = this.newMapWithKeysValues("One", "A1", "Two", "B", "Three", "C#++");
        CharHashBag result = map.collectChar((CharFunction<String>) string -> string.charAt(0), target);
        Assertions.assertSame(target, result, "Target sent as parameter not returned");
        Assertions.assertEquals(CharHashBag.newBagWith('A', 'B', 'C'), result.toBag());
    }

    @Test
    public void collectDouble()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("One", "1", "Two", "2", "Three", "3");
        DoubleIterable actual = map.collectDouble(Double::parseDouble);
        Assertions.assertEquals(DoubleHashBag.newBagWith(1.0d, 2.0d, 3.0d), actual.toBag());
    }

    @Test
    public void collectDoubleWithTarget()
    {
        DoubleHashBag target = new DoubleHashBag();
        MapIterable<String, String> map = this.newMapWithKeysValues("One", "1", "Two", "2", "Three", "3");
        DoubleHashBag result = map.collectDouble(Double::parseDouble, target);
        Assertions.assertSame(target, result, "Target sent as parameter not returned");
        Assertions.assertEquals(DoubleHashBag.newBagWith(1.0d, 2.0d, 3.0d), result.toBag());
    }

    @Test
    public void collectFloat()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("One", "1", "Two", "2", "Three", "3");
        FloatIterable actual = map.collectFloat(Float::parseFloat);
        Assertions.assertEquals(FloatHashBag.newBagWith(1.0f, 2.0f, 3.0f), actual.toBag());
    }

    @Test
    public void collectFloatWithTarget()
    {
        FloatHashBag target = new FloatHashBag();
        MapIterable<String, String> map = this.newMapWithKeysValues("One", "1", "Two", "2", "Three", "3");
        FloatHashBag result = map.collectFloat(Float::parseFloat, target);
        Assertions.assertSame(target, result, "Target sent as parameter not returned");
        Assertions.assertEquals(FloatHashBag.newBagWith(1.0f, 2.0f, 3.0f), result.toBag());
    }

    @Test
    public void collectInt()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("One", "1", "Two", "2", "Three", "3");
        IntIterable actual = map.collectInt(Integer::parseInt);
        Assertions.assertEquals(IntHashBag.newBagWith(1, 2, 3), actual.toBag());
    }

    @Test
    public void collectIntWithTarget()
    {
        IntHashBag target = new IntHashBag();
        MapIterable<String, String> map = this.newMapWithKeysValues("One", "1", "Two", "2", "Three", "3");
        IntHashBag result = map.collectInt(Integer::parseInt, target);
        Assertions.assertSame(target, result, "Target sent as parameter not returned");
        Assertions.assertEquals(IntHashBag.newBagWith(1, 2, 3), result.toBag());
    }

    @Test
    public void collectLong()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("One", "1", "Two", "2", "Three", "3");
        LongIterable actual = map.collectLong(Long::parseLong);
        Assertions.assertEquals(LongHashBag.newBagWith(1L, 2L, 3L), actual.toBag());
    }

    @Test
    public void collectLongWithTarget()
    {
        LongHashBag target = new LongHashBag();
        MapIterable<String, String> map = this.newMapWithKeysValues("One", "1", "Two", "2", "Three", "3");
        LongHashBag result = map.collectLong(Long::parseLong, target);
        Assertions.assertSame(target, result, "Target sent as parameter not returned");
        Assertions.assertEquals(LongHashBag.newBagWith(1L, 2L, 3L), result.toBag());
    }

    @Test
    public void collectShort()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("One", "1", "Two", "2", "Three", "3");
        ShortIterable actual = map.collectShort(Short::parseShort);
        Assertions.assertEquals(ShortHashBag.newBagWith((short) 1, (short) 2, (short) 3), actual.toBag());
    }

    @Test
    public void collectShortWithTarget()
    {
        ShortHashBag target = new ShortHashBag();
        MapIterable<String, String> map = this.newMapWithKeysValues("One", "1", "Two", "2", "Three", "3");
        ShortHashBag result = map.collectShort(Short::parseShort, target);
        Assertions.assertSame(target, result, "Target sent as parameter not returned");
        Assertions.assertEquals(ShortHashBag.newBagWith((short) 1, (short) 2, (short) 3), result.toBag());
    }

    @Test
    public void collectValues()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");
        MapIterable<String, String> actual = map.collectValues((argument1, argument2) -> new StringBuilder(argument2).reverse().toString());
        Assertions.assertEquals(UnifiedMap.newWithKeysValues("1", "enO", "2", "owT", "3", "eerhT"), actual);
    }

    @Test
    public void select()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");
        RichIterable<String> actual = map.select("Two"::equals);
        Assertions.assertEquals(HashBag.newBagWith("Two"), actual.toBag());
    }

    @Test
    public void selectWith()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");
        RichIterable<String> actual = map.selectWith(Object::equals, "Two");
        Assertions.assertEquals(HashBag.newBagWith("Two"), actual.toBag());
    }

    @Test
    public void reject()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");
        RichIterable<String> actual = map.reject("Two"::equals);
        Assertions.assertEquals(HashBag.newBagWith("One", "Three"), actual.toBag());
    }

    @Test
    public void rejectWith()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");
        RichIterable<String> actual = map.rejectWith(Object::equals, "Two");
        Assertions.assertEquals(HashBag.newBagWith("One", "Three"), actual.toBag());
    }

    @Test
    public void collect()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");
        RichIterable<String> actual = map.collect(StringFunctions.toLowerCase());
        Assertions.assertEquals(HashBag.newBagWith("one", "two", "three"), actual.toBag());
    }

    @Test
    public void selectMap()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");
        MapIterable<String, String> actual = map.select((argument1, argument2) -> "1".equals(argument1) || "Two".equals(argument2));
        Assertions.assertEquals(2, actual.size());
        Assertions.assertTrue(actual.keysView().containsAllArguments("1", "2"));
        Assertions.assertTrue(actual.valuesView().containsAllArguments("One", "Two"));
    }

    @Test
    public void rejectMap()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");
        MapIterable<String, String> actual = map.reject((argument1, argument2) -> "1".equals(argument1) || "Two".equals(argument2));
        Assertions.assertEquals(UnifiedMap.newWithKeysValues("3", "Three"), actual);
    }

    @Test
    public void flip()
    {
        Verify.assertEmpty(this.newMap().flip());

        MutableSetMultimap<String, String> expected = UnifiedSetMultimap.newMultimap();
        expected.put("odd", "One");
        expected.put("even", "Two");
        expected.put("odd", "Three");
        expected.put("even", "Four");

        Assertions.assertEquals(
                expected,
                this.newMapWithKeysValues("One", "odd", "Two", "even", "Three", "odd", "Four", "even").flip());
    }

    @Test
    public void detect()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");
        Pair<String, String> one = map.detect((argument1, argument2) -> "1".equals(argument1));
        Assertions.assertNotNull(one);
        Assertions.assertEquals("1", one.getOne());
        Assertions.assertEquals("One", one.getTwo());

        Pair<String, String> two = map.detect((argument1, argument2) -> "Two".equals(argument2));
        Assertions.assertNotNull(two);
        Assertions.assertEquals("2", two.getOne());
        Assertions.assertEquals("Two", two.getTwo());

        Assertions.assertNull(map.detect((ignored1, ignored2) -> false));
    }

    @Test
    public void anySatisfy()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        Verify.assertAnySatisfy((Map<String, String>) map, String.class::isInstance);
        Assertions.assertFalse(map.anySatisfy("Monkey"::equals));
    }

    @Test
    public void anySatisfyWith()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        Assertions.assertTrue(map.anySatisfyWith(Predicates2.instanceOf(), String.class));
        Assertions.assertFalse(map.anySatisfyWith(Object::equals, "Monkey"));
    }

    @Test
    public void allSatisfy()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        Verify.assertAllSatisfy((Map<String, String>) map, String.class::isInstance);
        Assertions.assertFalse(map.allSatisfy("Monkey"::equals));
    }

    @Test
    public void allSatisfyWith()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        Assertions.assertTrue(map.allSatisfyWith(Predicates2.instanceOf(), String.class));
        Assertions.assertFalse(map.allSatisfyWith(Object::equals, "Monkey"));
    }

    @Test
    public void noneSatisfy()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        Verify.assertNoneSatisfy((Map<String, String>) map, Integer.class::isInstance);
        Assertions.assertTrue(map.noneSatisfy("Monkey"::equals));
        Assertions.assertFalse(map.noneSatisfy("Two"::equals));
    }

    @Test
    public void noneSatisfyWith()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        Assertions.assertTrue(map.noneSatisfyWith(Predicates2.instanceOf(), Integer.class));
        Assertions.assertTrue(map.noneSatisfyWith(Object::equals, "Monkey"));
        Assertions.assertFalse(map.noneSatisfyWith(Object::equals, "Two"));
    }

    @Test
    public void appendString()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        StringBuilder builder1 = new StringBuilder();
        map.appendString(builder1);
        String defaultString = builder1.toString();
        Assertions.assertEquals(15, defaultString.length());

        StringBuilder builder2 = new StringBuilder();
        map.appendString(builder2, "|");
        String delimitedString = builder2.toString();
        Assertions.assertEquals(13, delimitedString.length());
        Verify.assertContains(delimitedString, "|");

        StringBuilder builder3 = new StringBuilder();
        map.appendString(builder3, "{", "|", "}");
        String wrappedString = builder3.toString();
        Assertions.assertEquals(15, wrappedString.length());
        Verify.assertContains(wrappedString, "|");
        Assertions.assertTrue(wrappedString.startsWith("{"));
        Assertions.assertTrue(wrappedString.endsWith("}"));
    }

    @Test
    public void toBag()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        MutableBag<String> bag = map.toBag();
        Assertions.assertEquals(Bags.mutable.of("One", "Two", "Three"), bag);
    }

    @Test
    public void toSortedBag()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        MutableSortedBag<Integer> sorted = map.toSortedBag();
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(1, 2, 3, 4), sorted);

        MutableSortedBag<Integer> reverse = map.toSortedBag(Collections.<Integer>reverseOrder());
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.<Integer>reverseNaturalOrder(), 1, 2, 3, 4), reverse);
    }

    @Test
    public void toSortedBagBy()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        MutableSortedBag<Integer> sorted = map.toSortedBagBy(String::valueOf);
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(1, 2, 3, 4), sorted);
    }

    @Test
    public void asLazy()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        LazyIterable<String> lazy = map.asLazy();
        Verify.assertContainsAll(lazy.toList(), "One", "Two", "Three");
    }

    @Test
    public void toList()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");
        MutableList<String> list = map.toList();
        Verify.assertContainsAll(list, "One", "Two", "Three");
    }

    @Test
    public void toMap()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "3", "Three", "4", "Four");

        MapIterable<Integer, String> actual = map.toMap(String::length, String::valueOf);

        Assertions.assertEquals(UnifiedMap.newWithKeysValues(3, "One", 5, "Three", 4, "Four"), actual);
    }

    @Test
    public void toSet()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        MutableSet<String> set = map.toSet();
        Verify.assertContainsAll(set, "One", "Two", "Three");
    }

    @Test
    public void toSortedList()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        MutableList<Integer> sorted = map.toSortedList();
        Assertions.assertEquals(FastList.newListWith(1, 2, 3, 4), sorted);

        MutableList<Integer> reverse = map.toSortedList(Collections.<Integer>reverseOrder());
        Assertions.assertEquals(FastList.newListWith(4, 3, 2, 1), reverse);
    }

    @Test
    public void toSortedListBy()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        MutableList<Integer> list = map.toSortedListBy(String::valueOf);
        Assertions.assertEquals(FastList.newListWith(1, 2, 3, 4), list);
    }

    @Test
    public void toSortedSet()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        MutableSortedSet<Integer> sorted = map.toSortedSet();
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3, 4), sorted);

        MutableSortedSet<Integer> reverse = map.toSortedSet(Collections.<Integer>reverseOrder());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Comparators.<Integer>reverseNaturalOrder(), 1, 2, 3, 4), reverse);
    }

    @Test
    public void toSortedSetBy()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        MutableSortedSet<Integer> sorted = map.toSortedSetBy(String::valueOf);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3, 4), sorted);
    }

    @Test
    public void toSortedMap()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "3", "Three", "4", "Four");

        MapIterable<Integer, String> actual = map.toSortedMap(String::length, String::valueOf);

        MapIterable<Integer, String> actualWithComparator = map.toSortedMap(Comparators.reverseNaturalOrder(), String::length, String::valueOf);

        Verify.assertIterablesEqual(TreeSortedMap.newMapWith(3, "One", 5, "Three", 4, "Four"), actual);
        TreeSortedMap<Object, Object> expectedIterable = TreeSortedMap.newMap(Comparators.reverseNaturalOrder());
        expectedIterable.put(3, "One");
        expectedIterable.put(5, "Three");
        expectedIterable.put(4, "Four");
        Verify.assertIterablesEqual(expectedIterable, actualWithComparator);
    }

    @Test
    public void chunk()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        RichIterable<RichIterable<String>> chunks = map.chunk(2).toList();

        RichIterable<Integer> sizes = chunks.collect(RichIterable::size);
        Assertions.assertEquals(FastList.newListWith(2, 1), sizes);
    }

    @Test
    public void collect_value()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        Verify.assertContainsAll(
                map.collect(Functions.getToString()).toSet(),
                "1", "2", "3", "4");
        Verify.assertContainsAll(
                map.collect(
                        String::valueOf,
                        UnifiedSet.<String>newSet()), "1", "2", "3", "4");
    }

    @Test
    public void collectIf()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        Bag<String> odd = map.collectIf(IntegerPredicates.isOdd(), Functions.getToString()).toBag();
        Assertions.assertEquals(Bags.mutable.of("1", "3"), odd);

        Bag<String> even = map.collectIf(IntegerPredicates.isEven(), String::valueOf, HashBag.<String>newBag());
        Assertions.assertEquals(Bags.mutable.of("2", "4"), even);
    }

    @Test
    public void collectWith()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        RichIterable<Integer> actual = map.collectWith(AddFunction.INTEGER, 1);
        Verify.assertContainsAll(actual, 2, 3, 4, 5);
    }

    @Test
    public void collectWithToTarget()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        FastList<Integer> actual = map.collectWith(AddFunction.INTEGER, 1, FastList.<Integer>newList());
        Verify.assertContainsAll(actual, 2, 3, 4, 5);
    }

    @Test
    public void contains()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        Assertions.assertTrue(map.contains("Two"));
    }

    @Test
    public void containsAll()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        Assertions.assertTrue(map.containsAll(FastList.newListWith("One", "Two")));
        Assertions.assertTrue(map.containsAll(FastList.newListWith("One", "Two", "Three")));
        Assertions.assertFalse(map.containsAll(FastList.newListWith("One", "Two", "Three", "Four")));
    }

    @Test
    public void containsKey()
    {
        MapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        Assertions.assertTrue(map.containsKey(1));
        Assertions.assertFalse(map.containsKey(4));
    }

    @Test
    public void containsValue()
    {
        MapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        Assertions.assertTrue(map.containsValue("1"));
        Assertions.assertFalse(map.containsValue("4"));

        MapIterable<Integer, String> map2 = this.newMapWithKeysValues(3, "1", 2, "2", 1, "3");
        Assertions.assertTrue(map2.containsValue("1"));
        Assertions.assertFalse(map2.containsValue("4"));
    }

    @Test
    public void getFirst()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        String value = map.getFirst();
        Assertions.assertNotNull(value);
        Assertions.assertTrue(map.valuesView().contains(value), value);
    }

    @Test
    public void getLast()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        String value = map.getLast();
        Assertions.assertNotNull(value);
        Assertions.assertTrue(map.valuesView().contains(value), value);
    }

    @Test
    public void containsAllIterable()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        Assertions.assertTrue(map.containsAllIterable(FastList.newListWith("One", "Two")));
        Assertions.assertFalse(map.containsAllIterable(FastList.newListWith("One", "Four")));
    }

    @Test
    public void containsAllArguments()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        Assertions.assertTrue(map.containsAllArguments("One", "Two"));
        Assertions.assertFalse(map.containsAllArguments("One", "Four"));
    }

    @Test
    public void count()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        int actual = map.count(Predicates.or("One"::equals, "Three"::equals));

        Assertions.assertEquals(2, actual);
    }

    @Test
    public void countWith()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        int actual = map.countWith(Object::equals, "One");

        Assertions.assertEquals(1, actual);
    }

    @Test
    public void detect_value()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        String resultFound = map.detect("One"::equals);
        Assertions.assertEquals("One", resultFound);

        String resultNotFound = map.detect("Four"::equals);
        Assertions.assertNull(resultNotFound);
    }

    @Test
    public void detectWith()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        String resultFound = map.detectWith(Object::equals, "One");
        Assertions.assertEquals("One", resultFound);

        String resultNotFound = map.detectWith(Object::equals, "Four");
        Assertions.assertNull(resultNotFound);
    }

    @Test
    public void detectIfNone_value()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        String resultNotFound = map.detectIfNone("Four"::equals, () -> "Zero");
        Assertions.assertEquals("Zero", resultNotFound);

        String resultFound = map.detectIfNone("One"::equals, () -> "Zero");
        Assertions.assertEquals("One", resultFound);
    }

    @Test
    public void detectWithIfNone()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        String resultNotFound = map.detectWithIfNone(Object::equals, "Four", () -> "Zero");
        Assertions.assertEquals("Zero", resultNotFound);

        String resultFound = map.detectWithIfNone(Object::equals, "One", () -> "Zero");
        Assertions.assertEquals("One", resultFound);
    }

    @Test
    public void flatten_value()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two");

        Function<String, Iterable<Character>> function = object -> {
            MutableList<Character> result = Lists.mutable.of();
            char[] chars = object.toCharArray();
            for (char aChar : chars)
            {
                result.add(Character.valueOf(aChar));
            }
            return result;
        };

        RichIterable<Character> blob = map.flatCollect(function);
        Assertions.assertTrue(blob.containsAllArguments(
                Character.valueOf('O'),
                Character.valueOf('n'),
                Character.valueOf('e'),
                Character.valueOf('T'),
                Character.valueOf('w'),
                Character.valueOf('o')));

        RichIterable<Character> blobFromTarget = map.flatCollect(function, FastList.<Character>newList());
        Assertions.assertTrue(blobFromTarget.containsAllArguments(
                Character.valueOf('O'),
                Character.valueOf('n'),
                Character.valueOf('e'),
                Character.valueOf('T'),
                Character.valueOf('w'),
                Character.valueOf('o')));
    }

    @Test
    public void groupBy()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        Function<Integer, Boolean> isOddFunction = object -> IntegerPredicates.isOdd().accept(object);

        Multimap<Boolean, Integer> expected = FastListMultimap.newMultimap(
                Tuples.pair(Boolean.TRUE, 1), Tuples.pair(Boolean.TRUE, 3),
                Tuples.pair(Boolean.FALSE, 2), Tuples.pair(Boolean.FALSE, 4));

        Multimap<Boolean, Integer> actual = map.groupBy(isOddFunction);
        expected.forEachKey(each -> {
            Assertions.assertTrue(actual.containsKey(each));
            MutableList<Integer> values = actual.get(each).toList();
            Verify.assertNotEmpty(values);
            Assertions.assertTrue(expected.get(each).containsAllIterable(values));
        });

        Multimap<Boolean, Integer> actualFromTarget = map.groupBy(isOddFunction, FastListMultimap.<Boolean, Integer>newMultimap());
        expected.forEachKey(each -> {
            Assertions.assertTrue(actualFromTarget.containsKey(each));
            MutableList<Integer> values = actualFromTarget.get(each).toList();
            Verify.assertNotEmpty(values);
            Assertions.assertTrue(expected.get(each).containsAllIterable(values));
        });
    }

    @Test
    public void groupByEach()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        MutableMultimap<Integer, Integer> expected = FastListMultimap.newMultimap();
        for (int i = 1; i < 4; i++)
        {
            expected.putAll(-i, Interval.fromTo(i, 4));
        }

        NegativeIntervalFunction function = new NegativeIntervalFunction();
        Multimap<Integer, Integer> actual = map.groupByEach(function);
        expected.forEachKey(each -> {
            Assertions.assertTrue(actual.containsKey(each));
            MutableList<Integer> values = actual.get(each).toList();
            Verify.assertNotEmpty(values);
            Assertions.assertTrue(expected.get(each).containsAllIterable(values));
        });

        Multimap<Integer, Integer> actualFromTarget = map.groupByEach(function, FastListMultimap.<Integer, Integer>newMultimap());
        expected.forEachKey(each -> {
            Assertions.assertTrue(actualFromTarget.containsKey(each));
            MutableList<Integer> values = actualFromTarget.get(each).toList();
            Verify.assertNotEmpty(values);
            Assertions.assertTrue(expected.get(each).containsAllIterable(values));
        });
    }

    @Test
    public void groupByUniqueKey()
    {
        MapIterable<Integer, Integer> map = this.newMapWithKeysValues(1, 1, 2, 2, 3, 3);
        Assertions.assertEquals(UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3), map.groupByUniqueKey(id -> id));
    }

    @Test
    public void groupByUniqueKey_throws()
    {
        assertThrows(IllegalStateException.class, () -> {
            this.newMapWithKeysValues(1, 1, 2, 2, 3, 3).groupByUniqueKey(Functions.getFixedValue(1));
        });
    }

    @Test
    public void groupByUniqueKey_target()
    {
        MapIterable<Integer, Integer> map = this.newMapWithKeysValues(1, 1, 2, 2, 3, 3);
        MutableMap<Integer, Integer> integers = map.groupByUniqueKey(id -> id, UnifiedMap.newWithKeysValues(0, 0));
        Assertions.assertEquals(UnifiedMap.newWithKeysValues(0, 0, 1, 1, 2, 2, 3, 3), integers);
    }

    @Test
    public void groupByUniqueKey_target_throws()
    {
        assertThrows(IllegalStateException.class, () -> {
            this.newMapWithKeysValues(1, 1, 2, 2, 3, 3).groupByUniqueKey(id -> id, UnifiedMap.newWithKeysValues(2, 2));
        });
    }

    @Test
    public void injectInto()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        Integer actual = map.injectInto(0, AddFunction.INTEGER);
        Assertions.assertEquals(Integer.valueOf(10), actual);

        Sum sum = map.injectInto(new IntegerSum(0), SumProcedure.number());
        Assertions.assertEquals(new IntegerSum(10), sum);
    }

    @Test
    public void injectIntoInt()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        int actual = map.injectInto(0, AddFunction.INTEGER_TO_INT);
        Assertions.assertEquals(10, actual);
    }

    @Test
    public void injectIntoLong()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        long actual = map.injectInto(0, AddFunction.INTEGER_TO_LONG);
        Assertions.assertEquals(10, actual);
    }

    @Test
    public void injectIntoFloat()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        float actual = map.injectInto(0, AddFunction.INTEGER_TO_FLOAT);
        Assertions.assertEquals(10.0F, actual, 0.01);
    }

    @Test
    public void injectIntoDouble()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        double actual = map.injectInto(0, AddFunction.INTEGER_TO_DOUBLE);
        Assertions.assertEquals(10.0d, actual, 0.01);
    }

    @Test
    public void sumOfInt()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        long actual = map.sumOfInt(integer -> integer);
        Assertions.assertEquals(10L, actual);
    }

    @Test
    public void sumOfLong()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        long actual = map.sumOfLong(Integer::longValue);
        Assertions.assertEquals(10, actual);
    }

    @Test
    public void sumOfFloat()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        double actual = map.sumOfFloat(Integer::floatValue);
        Assertions.assertEquals(10.0d, actual, 0.01);
    }

    @Test
    public void sumOfDouble()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        double actual = map.sumOfDouble(Integer::doubleValue);
        Assertions.assertEquals(10.0d, actual, 0.01);
    }

    @Test
    public void sumByInt()
    {
        RichIterable<String> values = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        ObjectLongMap<Integer> result = values.sumByInt(s -> Integer.parseInt(s) % 2, Integer::parseInt);
        Assertions.assertEquals(4, result.get(1));
        Assertions.assertEquals(2, result.get(0));
    }

    @Test
    public void sumByFloat()
    {
        RichIterable<String> values = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        ObjectDoubleMap<Integer> result = values.sumByFloat(s -> Integer.parseInt(s) % 2, Float::parseFloat);
        Assertions.assertEquals(4.0f, result.get(1), 0.0);
        Assertions.assertEquals(2.0f, result.get(0), 0.0);
    }

    @Test
    public void sumByLong()
    {
        RichIterable<String> values = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        ObjectLongMap<Integer> result = values.sumByLong(s -> Integer.parseInt(s) % 2, Long::parseLong);
        Assertions.assertEquals(4, result.get(1));
        Assertions.assertEquals(2, result.get(0));
    }

    @Test
    public void sumByDouble()
    {
        RichIterable<String> values = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        ObjectDoubleMap<Integer> result = values.sumByDouble(s -> Integer.parseInt(s) % 2, Double::parseDouble);
        Assertions.assertEquals(4.0d, result.get(1), 0.0);
        Assertions.assertEquals(2.0d, result.get(0), 0.0);
    }

    @Test
    public void makeString()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        String defaultString = map.makeString();
        Assertions.assertEquals(15, defaultString.length());

        String delimitedString = map.makeString("|");
        Assertions.assertEquals(13, delimitedString.length());
        Verify.assertContains(delimitedString, "|");

        String wrappedString = map.makeString("{", "|", "}");
        Assertions.assertEquals(15, wrappedString.length());
        Verify.assertContains(wrappedString, "|");
        Assertions.assertTrue(wrappedString.startsWith("{"));
        Assertions.assertTrue(wrappedString.endsWith("}"));
    }

    @Test
    public void min()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        Assertions.assertEquals(Integer.valueOf(1), map.min());
        Assertions.assertEquals(Integer.valueOf(1), map.min(Integer::compareTo));
    }

    @Test
    public void max()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        Assertions.assertEquals(Integer.valueOf(4), map.max());
        Assertions.assertEquals(Integer.valueOf(4), map.max(Integer::compareTo));
    }

    @Test
    public void minBy()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        Assertions.assertEquals(Integer.valueOf(1), map.minBy(String::valueOf));
    }

    @Test
    public void maxBy()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        Assertions.assertEquals(Integer.valueOf(4), map.maxBy(String::valueOf));
    }

    @Test
    public void reject_value()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        Verify.assertContainsAll(map.reject(Predicates.lessThan(3)).toSet(), 3, 4);
        Verify.assertContainsAll(map.reject(Predicates.lessThan(3), UnifiedSet.<Integer>newSet()), 3, 4);
    }

    @Test
    public void rejectWith_value()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        Verify.assertContainsAll(map.rejectWith(Predicates2.<Integer>lessThan(), 3, UnifiedSet.<Integer>newSet()), 3, 4);
    }

    @Test
    public void select_value()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        Verify.assertContainsAll(map.select(Predicates.lessThan(3)).toSet(), 1, 2);
        Verify.assertContainsAll(map.select(Predicates.lessThan(3), UnifiedSet.<Integer>newSet()), 1, 2);
    }

    @Test
    public void selectWith_value()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        Verify.assertContainsAll(map.selectWith(Predicates2.<Integer>lessThan(), 3, UnifiedSet.<Integer>newSet()), 1, 2);
    }

    @Test
    public void partition_value()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues(
                "A", 1,
                "B", 2,
                "C", 3,
                "D", 4);
        PartitionIterable<Integer> partition = map.partition(IntegerPredicates.isEven());
        Assertions.assertEquals(iSet(4, 2), partition.getSelected().toSet());
        Assertions.assertEquals(iSet(3, 1), partition.getRejected().toSet());
    }

    @Test
    public void partitionWith_value()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues(
                "A", 1,
                "B", 2,
                "C", 3,
                "D", 4);
        PartitionIterable<Integer> partition = map.partitionWith(Predicates2.in(), map.select(IntegerPredicates.isEven()));
        Assertions.assertEquals(iSet(4, 2), partition.getSelected().toSet());
        Assertions.assertEquals(iSet(3, 1), partition.getRejected().toSet());
    }

    @Test
    public void selectInstancesOf_value()
    {
        MapIterable<String, Number> map = this.<String, Number>newMapWithKeysValues("1", 1, "2", 2.0, "3", 3, "4", 4.0);
        Assertions.assertEquals(iBag(1, 3), map.selectInstancesOf(Integer.class).toBag());
    }

    @Test
    public void toArray()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        Object[] array = map.toArray();
        Verify.assertSize(4, array);
        Integer[] array2 = map.toArray(new Integer[0]);
        Verify.assertSize(4, array2);
        Integer[] array3 = map.toArray(new Integer[4]);
        Verify.assertSize(4, array3);
        Integer[] array4 = map.toArray(new Integer[5]);
        Verify.assertSize(5, array4);
    }

    @Test
    public void zip()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        List<Object> nulls = Collections.nCopies(map.size(), null);
        List<Object> nullsPlusOne = Collections.nCopies(map.size() + 1, null);
        List<Object> nullsMinusOne = Collections.nCopies(map.size() - 1, null);

        RichIterable<Pair<String, Object>> pairs = map.zip(nulls);
        Assertions.assertEquals(
                map.toSet(),
                pairs.collect((Function<Pair<String, ?>, String>) Pair::getOne).toSet());
        Assertions.assertEquals(
                nulls,
                pairs.collect((Function<Pair<?, Object>, Object>) Pair::getTwo, Lists.mutable.of()));

        RichIterable<Pair<String, Object>> pairsPlusOne = map.zip(nullsPlusOne);
        Assertions.assertEquals(
                map.toSet(),
                pairsPlusOne.collect((Function<Pair<String, ?>, String>) Pair::getOne).toSet());
        Assertions.assertEquals(nulls, pairsPlusOne.collect((Function<Pair<?, Object>, Object>) Pair::getTwo, Lists.mutable.of()));

        RichIterable<Pair<String, Object>> pairsMinusOne = map.zip(nullsMinusOne);
        Assertions.assertEquals(map.size() - 1, pairsMinusOne.size());
        Assertions.assertTrue(map.valuesView().containsAllIterable(pairsMinusOne.collect((Function<Pair<String, ?>, String>) Pair::getOne).toSet()));

        Assertions.assertEquals(
                map.zip(nulls).toSet(),
                map.zip(nulls, UnifiedSet.<Pair<String, Object>>newSet()));
    }

    @Test
    public void zipWithIndex()
    {
        MapIterable<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three");

        RichIterable<Pair<String, Integer>> pairs = map.zipWithIndex();

        Assertions.assertEquals(
                map.toSet(),
                pairs.collect((Function<Pair<String, ?>, String>) Pair::getOne).toSet());
        Assertions.assertEquals(
                Interval.zeroTo(map.size() - 1).toSet(),
                pairs.collect((Function<Pair<?, Integer>, Integer>) Pair::getTwo, UnifiedSet.<Integer>newSet()));

        Assertions.assertEquals(
                map.zipWithIndex().toSet(),
                map.zipWithIndex(UnifiedSet.<Pair<String, Integer>>newSet()));
    }

    @Test
    public void aggregateByMutating()
    {
        Function0<AtomicInteger> valueCreator = AtomicInteger::new;
        RichIterable<Integer> collection = this.newMapWithKeysValues(1, 1, 2, 2, 3, 3);
        MapIterable<String, AtomicInteger> aggregation = collection.aggregateInPlaceBy(String::valueOf, valueCreator, AtomicInteger::addAndGet);
        Assertions.assertEquals(1, aggregation.get("1").intValue());
        Assertions.assertEquals(2, aggregation.get("2").intValue());
        Assertions.assertEquals(3, aggregation.get("3").intValue());
    }

    @Test
    public void aggregateByNonMutating()
    {
        Function0<Integer> valueCreator = () -> 0;
        Function2<Integer, Integer, Integer> sumAggregator = (integer1, integer2) -> integer1 + integer2;
        RichIterable<Integer> collection = this.newMapWithKeysValues(1, 1, 2, 2, 3, 3);
        MapIterable<String, Integer> aggregation = collection.aggregateBy(String::valueOf, valueCreator, sumAggregator);
        Assertions.assertEquals(1, aggregation.get("1").intValue());
        Assertions.assertEquals(2, aggregation.get("2").intValue());
        Assertions.assertEquals(3, aggregation.get("3").intValue());
    }

    @Test
    public void keyValuesView()
    {
        MapIterable<Integer, String> map = this.newMapWithKeysValues(1, "A", 2, "B", 3, "C", 4, "D");
        MutableSet<Pair<Integer, String>> keyValues = map.keyValuesView().toSet();
        Assertions.assertEquals(UnifiedSet.newSetWith(Tuples.pair(1, "A"), Tuples.pair(2, "B"), Tuples.pair(3, "C"), Tuples.pair(4, "D")), keyValues);
    }

    @Test
    public void nullCollisionWithCastInEquals()
    {
        if (this.newMap() instanceof SortedMap || this.newMap() instanceof ConcurrentMap)
        {
            return;
        }
        MapIterable<IntegerWithCast, String> mutableMap = this.newMapWithKeysValues(
                new IntegerWithCast(0), "Test 2",
                new IntegerWithCast(0), "Test 3",
                null, "Test 1");
        Assertions.assertEquals(
                this.newMapWithKeysValues(
                        new IntegerWithCast(0), "Test 3",
                        null, "Test 1"),
                mutableMap);
        Assertions.assertEquals("Test 3", mutableMap.get(new IntegerWithCast(0)));
        Assertions.assertEquals("Test 1", mutableMap.get(null));
    }

    @Test
    public void testNewMap()
    {
        MapIterable<Integer, Integer> map = this.newMap();
        Verify.assertEmpty(map);
        Verify.assertSize(0, map);
    }

    @Test
    public void testNewMapWithKeyValue()
    {
        MapIterable<Integer, String> map = this.newMapWithKeyValue(1, "One");
        Verify.assertNotEmpty(map);
        Verify.assertSize(1, map);
        Verify.assertContainsKeyValue(1, "One", map);
    }

    @Test
    public void newMapWithWith()
    {
        MapIterable<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two");
        Verify.assertNotEmpty(map);
        Verify.assertSize(2, map);
        Verify.assertContainsAllKeyValues(map, 1, "One", 2, "Two");
    }

    @Test
    public void newMapWithWithWith()
    {
        MapIterable<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two", 3, "Three");
        Verify.assertNotEmpty(map);
        Verify.assertSize(3, map);
        Verify.assertContainsAllKeyValues(map, 1, "One", 2, "Two", 3, "Three");
    }

    @Test
    public void newMapWithWithWithWith()
    {
        MapIterable<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two", 3, "Three", 4, "Four");
        Verify.assertNotEmpty(map);
        Verify.assertSize(4, map);
        Verify.assertContainsAllKeyValues(map, 1, "One", 2, "Two", 3, "Three", 4, "Four");
    }

    @Test
    public void iterator()
    {
        MapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        Iterator<Integer> iterator = map.iterator();
        Assertions.assertTrue(iterator.hasNext());
        int sum = 0;
        while (iterator.hasNext())
        {
            sum += iterator.next();
        }
        Assertions.assertFalse(iterator.hasNext());
        Assertions.assertEquals(6, sum);
    }

    @Test
    public void keysView()
    {
        MutableList<Integer> keys = this.newMapWithKeysValues(1, 1, 2, 2).keysView().toSortedList();
        Assertions.assertEquals(FastList.newListWith(1, 2), keys);
    }

    @Test
    public void valuesView()
    {
        MutableList<Integer> values = this.newMapWithKeysValues(1, 1, 2, 2).valuesView().toSortedList();
        Assertions.assertEquals(FastList.newListWith(1, 2), values);
    }

    @Test
    public void test_toString()
    {
        MapIterable<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two", 3, "Three");

        String stringToSearch = map.toString();
        Verify.assertContains(stringToSearch, "1=One");
        Verify.assertContains(stringToSearch, "2=Two");
        Verify.assertContains(stringToSearch, "3=Three");
    }
}
