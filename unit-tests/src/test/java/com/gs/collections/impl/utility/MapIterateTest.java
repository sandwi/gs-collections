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

package com.gs.collections.impl.utility;

import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.gs.collections.api.bag.MutableBag;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.predicate.Predicate2;
import com.gs.collections.api.collection.primitive.MutableBooleanCollection;
import com.gs.collections.api.collection.primitive.MutableByteCollection;
import com.gs.collections.api.collection.primitive.MutableCharCollection;
import com.gs.collections.api.collection.primitive.MutableDoubleCollection;
import com.gs.collections.api.collection.primitive.MutableFloatCollection;
import com.gs.collections.api.collection.primitive.MutableIntCollection;
import com.gs.collections.api.collection.primitive.MutableLongCollection;
import com.gs.collections.api.collection.primitive.MutableShortCollection;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MutableMap;
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
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.PrimitiveFunctions;
import com.gs.collections.impl.block.function.AddFunction;
import com.gs.collections.impl.block.predicate.MapEntryPredicate;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.block.procedure.MapPutProcedure;
import com.gs.collections.impl.factory.Bags;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.factory.Maps;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.set.sorted.mutable.TreeSortedSet;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MapIterateTest
{
    @Test
    public void occurrencesOf()
    {
        MutableMap<String, Integer> map = this.getIntegerMap();
        Assertions.assertEquals(0, MapIterate.occurrencesOf(map, -1));
        Assertions.assertEquals(1, MapIterate.occurrencesOf(map, 1));
        Assertions.assertEquals(1, MapIterate.occurrencesOf(map, 2));
    }

    @Test
    public void occurrencesOfAttribute()
    {
        MutableMap<String, Integer> map = this.getIntegerMap();
        Assertions.assertEquals(0, MapIterate.occurrencesOfAttribute(map, String::valueOf, "-1"));
        Assertions.assertEquals(1, MapIterate.occurrencesOfAttribute(map, String::valueOf, "1"));
        Assertions.assertEquals(1, MapIterate.occurrencesOfAttribute(map, String::valueOf, "2"));
    }

    @Test
    public void toListOfPairs()
    {
        MutableMap<String, Integer> map = this.getIntegerMap();
        MutableList<Pair<String, Integer>> pairs = MapIterate.toListOfPairs(map);
        Verify.assertSize(5, pairs);
        Verify.assertContains(Tuples.pair("1", 1), pairs);
        Verify.assertContains(Tuples.pair("2", 2), pairs);
        Verify.assertContains(Tuples.pair("3", 3), pairs);
        Verify.assertContains(Tuples.pair("4", 4), pairs);
        Verify.assertContains(Tuples.pair("5", 5), pairs);
    }

    @Test
    public void injectInto()
    {
        MutableMap<String, Integer> map = this.getIntegerMap();
        Assertions.assertEquals(Integer.valueOf(1 + 2 + 3 + 4 + 5), MapIterate.injectInto(0, map, AddFunction.INTEGER));
    }

    @Test
    public void functionMapTransformation()
    {
        MutableMap<Integer, Integer> input = Maps.fixedSize.of(1, 10, 2, 20);

        MutableMap<String, String> result = MapIterate.collect(input, Functions.getToString(), Functions.getToString());

        Verify.assertContainsKeyValue("10", result, "1");
        Verify.assertContainsKeyValue("20", result, "2");
        Verify.assertSize(2, result);
    }

    @Test
    public void simpleMapTransformation()
    {
        MutableMap<Locale, Currency> input = Maps.fixedSize.of(Locale.UK, Currency.getInstance(Locale.UK), Locale.JAPAN, Currency.getInstance(Locale.JAPAN));

        Function<Locale, String> getCountry = Locale::getCountry;
        Function<Currency, String> getCurrencyCode = Currency::getCurrencyCode;
        MutableMap<String, String> result = MapIterate.collect(input, getCountry, getCurrencyCode);
        Verify.assertContainsKeyValue("GBP", result, "GB");
        Verify.assertContainsKeyValue("JPY", result, "JP");
        Verify.assertSize(2, result);
    }

    @Test
    public void complexMapTransformation()
    {
        MutableMap<Locale, Currency> input = Maps.fixedSize.of(Locale.UK, Currency.getInstance(Locale.UK), Locale.JAPAN, Currency.getInstance(Locale.JAPAN));

        Function2<Locale, Currency, Pair<String, String>> function = (locale, currency) -> Tuples.pair(locale.getDisplayCountry() + ':' + currency.getCurrencyCode(), currency.getCurrencyCode());
        MutableMap<String, String> result = MapIterate.collect(input, function);

        Verify.assertContainsKeyValue("GBP", result, "United Kingdom:GBP");
        Verify.assertContainsKeyValue("JPY", result, "Japan:JPY");
        Verify.assertSize(2, result);
    }

    @Test
    public void conditionalMapTransformation()
    {
        MutableMap<Locale, Currency> input = UnifiedMap.newWithKeysValues(Locale.UK, Currency.getInstance(Locale.UK), Locale.JAPAN, Currency.getInstance(Locale.JAPAN), Locale.CHINA, Currency.getInstance(Locale.GERMANY), Locale.GERMANY, Currency.getInstance(Locale.CHINA));

        MutableMap<String, String> result = MapIterate.collectIf(input, (locale, currency) -> Tuples.pair(locale.getDisplayCountry() + ':' + currency.getCurrencyCode(), currency.getCurrencyCode()), (locale, currency) -> Currency.getInstance(locale).equals(currency));

        Verify.assertContainsKeyValue("GBP", result, "United Kingdom:GBP");
        Verify.assertContainsKeyValue("JPY", result, "Japan:JPY");
        Verify.assertSize(2, result);
    }

    @Test
    public void reverseMapping()
    {
        MutableMap<Integer, Integer> input = Maps.fixedSize.of(1, 10, 2, 20);

        MutableMap<Integer, Integer> result = MapIterate.reverseMapping(input);
        Verify.assertContainsKeyValue(10, 1, result);
        Verify.assertContainsKeyValue(20, 2, result);
        Verify.assertSize(2, result);
    }

    @Test
    public void toSortedList_with_comparator()
    {
        MutableMap<String, Integer> integers = this.getIntegerMap();
        MutableList<Integer> list = MapIterate.toSortedList(integers, Collections.<Integer>reverseOrder());
        MutableList<Integer> expected = FastList.newList(integers.values()).sortThis(Collections.<Integer>reverseOrder());
        Assertions.assertEquals(expected, list);
    }

    @Test
    public void toSortedSetBy()
    {
        MutableMap<String, Integer> integers = this.getIntegerMap();
        MutableSortedSet<Integer> set = integers.toSortedSetBy(String::valueOf);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(integers.values()), set);
    }

    private MutableMap<String, Integer> getIntegerMap()
    {
        MutableMap<String, Integer> map = UnifiedMap.newMap();
        this.populateIntegerMap(map);
        return map;
    }

    private void populateIntegerMap(Map<String, Integer> map)
    {
        map.put("5", 5);
        map.put("4", 4);
        map.put("3", 3);
        map.put("2", 2);
        map.put("1", 1);
    }

    @Test
    public void selectWithDifferentTargetCollection()
    {
        MutableMap<String, Integer> map = this.getIntegerMap();
        Collection<Integer> results = MapIterate.select(map, Integer.class::isInstance, FastList.<Integer>newList());
        Assertions.assertEquals(Bags.mutable.of(1, 2, 3, 4, 5), HashBag.newBag(results));
    }

    @Test
    public void count()
    {
        MutableMap<String, Integer> map = this.getIntegerMap();
        Assertions.assertEquals(5, MapIterate.count(map, Integer.class::isInstance));
    }

    @Test
    public void rejectWithDifferentTargetCollection()
    {
        MutableMap<String, Integer> map = this.getIntegerMap();
        MutableList<Integer> list = MapIterate.reject(map, Integer.class::isInstance, FastList.<Integer>newList());
        Verify.assertEmpty(list);
    }

    @Test
    public void forEachValue()
    {
        MutableMap<String, Integer> map = UnifiedMap.newMap();
        map.putAll(this.getIntegerMap());
        MutableList<Integer> list = Lists.mutable.of();
        MapIterate.forEachValue(map, CollectionAddProcedure.on(list));
        MapIterate.forEachValue(new HashMap<>(map), CollectionAddProcedure.on(list));
        MapIterate.forEachValue(new HashMap<>(), CollectionAddProcedure.on(list));
        Verify.assertSize(10, list);
        Assertions.assertEquals(30, list.injectInto(0, AddFunction.INTEGER_TO_INT));
    }

    @Test
    public void forEachValueThrowsOnNull()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            MapIterate.forEachValue(null, null);
        });
    }

    @Test
    public void forEachKeyThrowsOnNull()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            MapIterate.forEachKey(null, null);
        });
    }

    @Test
    public void forEachKeyValueThrowsOnNull()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            MapIterate.forEachKeyValue(null, null);
        });
    }

    @Test
    public void forEachKeyWithEmpty()
    {
        MapIterate.forEachKey(UnifiedMap.newMap(), null);
        //the implicit assertion is that it doesn't blow up with a NPE
    }

    @Test
    public void forEachKey()
    {
        MutableMap<String, Integer> map = UnifiedMap.newMap();
        map.putAll(this.getIntegerMap());
        MutableBag<String> bag = Bags.mutable.of();
        MapIterate.forEachKey(map, CollectionAddProcedure.on(bag));
        MapIterate.forEachKey(new HashMap<>(map), CollectionAddProcedure.on(bag));
        MapIterate.forEachKey(new HashMap<>(), CollectionAddProcedure.on(bag));
        Assertions.assertEquals(HashBag.newBagWith("1", "1", "2", "2", "3", "3", "4", "4", "5", "5"), bag);
    }

    @Test
    public void forEachKeyValueWithEmpty()
    {
        MapIterate.forEachKeyValue(UnifiedMap.newMap(), null);
        //the implicit assertion is that it doesn't blow up with a NPE
    }

    @Test
    public void forEachKeyValue()
    {
        MutableMap<String, Integer> map = UnifiedMap.newMap();
        map.putAll(UnifiedMap.newMap(this.getIntegerMap()));
        MutableMap<String, Integer> newMap = UnifiedMap.newMap();
        MapPutProcedure<String, Integer> procedure = new MapPutProcedure<>(newMap);
        MapIterate.forEachKeyValue(map, procedure);
        Verify.assertMapsEqual(map, newMap);
    }

    @Test
    public void getIfAbsentPut()
    {
        MutableMap<String, String> unifiedMap = UnifiedMap.newMap();
        Map<String, String> hashMap = new HashMap<>();
        String value = new String("value");
        String value1 = MapIterate.getIfAbsentPut(unifiedMap, "key", () -> value);
        String value2 = MapIterate.getIfAbsentPut(unifiedMap, "key", () -> value);
        Assertions.assertEquals("value", value1);
        Assertions.assertSame(value1, value2);
        String value3 = MapIterate.getIfAbsentPut(hashMap, "key", () -> value);
        String value4 = MapIterate.getIfAbsentPut(hashMap, "key", () -> value);
        Assertions.assertEquals("value", value3);
        Assertions.assertSame(value3, value4);
    }

    @Test
    public void getIfAbsentPutWith()
    {
        MutableMap<String, String> map = UnifiedMap.newMap();
        Function<String, String> function = object -> "value:" + object;
        String value1 = MapIterate.getIfAbsentPutWith(map, "key", function, "1");
        String value2 = MapIterate.getIfAbsentPutWith(map, "key", function, "2");
        Assertions.assertSame(value1, value2);
    }

    @Test
    public void getIfAbsentPutWithNullValue()
    {
        MutableMap<String, String> map = UnifiedMap.newMap();
        map.put("nullValueKey", null);
        Assertions.assertNull(MapIterate.getIfAbsentPut(map, "nullValueKey", () -> "aValue"));
    }

    @Test
    public void getIfAbsent()
    {
        MutableMap<String, String> unifiedMap = UnifiedMap.newMapWith(Tuples.pair("key1", "key1Value"));
        Map<String, String> hashMap = new HashMap<>(unifiedMap);
        String value1 = MapIterate.getIfAbsent(unifiedMap, "key", () -> new String("value"));
        String value2 = MapIterate.getIfAbsent(unifiedMap, "key", () -> new String("value"));
        String value3 = MapIterate.getIfAbsent(hashMap, "key", () -> new String("value"));
        Assertions.assertEquals("value", value1);
        Assertions.assertEquals("value", value2);
        Assertions.assertEquals("value", value3);
        Assertions.assertNotSame(value1, value2);
        Assertions.assertNotSame(value1, value3);
        Assertions.assertEquals("key1Value", MapIterate.getIfAbsent(hashMap, "key1", () -> new String("value")));
        Assertions.assertEquals("key1Value", MapIterate.getIfAbsent(unifiedMap, "key1", () -> new String("value")));
    }

    @Test
    public void getIfAbsentDefault()
    {
        MutableMap<String, String> map = UnifiedMap.<String, String>newMap().withKeysValues("key", "value");
        Assertions.assertEquals("value", MapIterate.getIfAbsentDefault(map, "key", "defaultValue1"));
        Assertions.assertEquals("defaultValue2", MapIterate.getIfAbsentDefault(map, "noKey", "defaultValue2"));
        Verify.assertNotContainsKey(map, "noKey");
        Verify.assertSize(1, map);
    }

    @Test
    public void getIfAbsentWith()
    {
        MutableMap<String, Integer> unifiedMap = UnifiedMap.newMap();
        this.populateIntegerMap(unifiedMap);
        Map<String, Integer> hashMap = new HashMap<>(unifiedMap);
        Function<Integer, Integer> function = Functions.getPassThru();
        Integer ifAbsentValue = Integer.valueOf(6);
        Assertions.assertEquals(ifAbsentValue, MapIterate.getIfAbsentWith(unifiedMap, "six", function, ifAbsentValue));
        Assertions.assertEquals(Integer.valueOf(5), MapIterate.getIfAbsentWith(unifiedMap, "5", function, ifAbsentValue));
        Assertions.assertEquals(ifAbsentValue, MapIterate.getIfAbsentWith(hashMap, "six", function, ifAbsentValue));
        Assertions.assertEquals(Integer.valueOf(5), MapIterate.getIfAbsentWith(hashMap, "5", function, ifAbsentValue));
    }

    @Test
    public void withNullValue()
    {
        MutableMap<String, String> map = UnifiedMap.newWithKeysValues("key", null);
        String value = "value";
        Assertions.assertNull(MapIterate.getIfAbsent(map, "key", () -> value));
        Assertions.assertNull(MapIterate.getIfAbsentPut(map, "key", () -> value));
        Assertions.assertEquals("result", MapIterate.ifPresentApply(map, "key", object -> "result"));
    }

    @Test
    public void ifPresentApply()
    {
        MutableMap<String, String> unifiedMap = UnifiedMap.newWithKeysValues("testKey", "testValue");
        Map<String, String> hashMap = new HashMap<>(unifiedMap);
        Assertions.assertEquals("TESTVALUE", MapIterate.ifPresentApply(unifiedMap, "testKey", String::toUpperCase));
        Assertions.assertEquals("TESTVALUE", MapIterate.ifPresentApply(hashMap, "testKey", String::toUpperCase));
    }

    @Test
    public void selectMapOnEntry()
    {
        MutableMap<String, String> map = UnifiedMap.newWithKeysValues(
                "1", "2",
                "2", "1",
                "3", "3");
        MutableMap<String, String> resultMap = MapIterate.selectMapOnEntry(map, (argument1, argument2) -> "1".equals(argument1) || "1".equals(argument2));
        Verify.assertSize(2, resultMap);
        Verify.assertContainsKeyValue("2", resultMap, "1");
        Verify.assertContainsKeyValue("1", resultMap, "2");
    }

    @Test
    public void rejectMapOnEntry()
    {
        MutableMap<String, String> map = UnifiedMap.newWithKeysValues(
                "1", "2",
                "2", "1",
                "3", "3");
        MutableMap<String, String> resultMap = MapIterate.rejectMapOnEntry(map, (argument1, argument2) -> "1".equals(argument1) || "1".equals(argument2));
        Verify.assertSize(1, resultMap);
        Verify.assertContainsKeyValue("3", resultMap, "3");
    }

    @Test
    public void select()
    {
        MutableMap<String, String> map = UnifiedMap.newWithKeysValues(
                "1", "2",
                "2", "1",
                "3", "3");
        Assertions.assertEquals(FastList.newListWith("1"), MapIterate.select(map, "1"::equals));
    }

    @Test
    public void selectMapEntries()
    {
        MutableMap<String, String> map = UnifiedMap.newWithKeysValues(
                "1", "2",
                "2", "1",
                "3", "3");
        Collection<Map.Entry<String, String>> results = Iterate.select(map.entrySet(), new MapEntryPredicate<String, String>()
        {
            public boolean accept(String argument1, String argument2)
            {
                return "1".equals(argument1) || "1".equals(argument2);
            }
        });
        Verify.assertSize(2, results);
    }

    @Test
    public void selectMapOnKey()
    {
        MutableMap<String, String> map = UnifiedMap.newWithKeysValues(
                "1", "2",
                "2", "1",
                "3", "3");
        MutableMap<String, String> resultMap = MapIterate.selectMapOnKey(map, "1"::equals);
        Assertions.assertEquals(UnifiedMap.newWithKeysValues("1", "2"), resultMap);
    }

    @Test
    public void selectMapOnValue()
    {
        MutableMap<String, String> map = UnifiedMap.newWithKeysValues(
                "1", "2",
                "2", "1",
                "3", "3");
        MutableMap<String, String> resultMap = MapIterate.selectMapOnValue(map, "1"::equals);
        Assertions.assertEquals(UnifiedMap.newWithKeysValues("2", "1"), resultMap);
    }

    @Test
    public void detect()
    {
        MutableMap<String, String> map = UnifiedMap.newWithKeysValues(
                "1", "2",
                "2", "1",
                "3", "3");
        String resultFound = MapIterate.detect(map, "1"::equals);
        Assertions.assertEquals("1", resultFound);
        String resultNotFound = MapIterate.detect(map, "4"::equals);
        Assertions.assertNull(resultNotFound);
    }

    @Test
    public void detectThrowsOnNull()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            MapIterate.detect(null, (Predicate2<? super Object, ? super Object>) null);
        });
    }

    @Test
    public void detectIfNone()
    {
        MutableMap<String, String> map = UnifiedMap.newWithKeysValues(
                "1", "2",
                "2", "1",
                "3", "3");
        String resultNotFound = MapIterate.detectIfNone(map, "4"::equals, "0");
        Assertions.assertEquals("0", resultNotFound);
        String resultFound = MapIterate.detectIfNone(map, "1"::equals, "0");
        Assertions.assertEquals("1", resultFound);
    }

    @Test
    public void anySatisfy()
    {
        MutableMap<String, String> map = UnifiedMap.newWithKeysValues(
                "1", "2",
                "2", "1",
                "3", "3");
        Assertions.assertTrue(MapIterate.anySatisfy(map, "1"::equals));
        Assertions.assertTrue(MapIterate.anySatisfy(map, "3"::equals));
        Assertions.assertFalse(MapIterate.anySatisfy(map, "4"::equals));
    }

    @Test
    public void allSatisfy()
    {
        MutableMap<String, String> map = UnifiedMap.newWithKeysValues(
                "1", "2",
                "2", "1",
                "3", "3");
        Assertions.assertFalse(MapIterate.allSatisfy(map, Predicates.notEqual("1")));
        Assertions.assertFalse(MapIterate.allSatisfy(map, Predicates.notEqual("3")));
        Assertions.assertTrue(MapIterate.anySatisfy(map, Predicates.notEqual("4")));
    }

    @Test
    public void noneSatisfy()
    {
        MutableMap<String, String> map = UnifiedMap.newWithKeysValues(
                "1", "2",
                "2", "1",
                "3", "3");
        Assertions.assertFalse(MapIterate.noneSatisfy(map, "1"::equals));
        Assertions.assertFalse(MapIterate.noneSatisfy(map, "3"::equals));
        Assertions.assertTrue(MapIterate.noneSatisfy(map, "4"::equals));
    }

    @Test
    public void isEmpty()
    {
        Assertions.assertTrue(MapIterate.isEmpty(null));
        Assertions.assertTrue(MapIterate.isEmpty(UnifiedMap.newMap()));
        Assertions.assertFalse(MapIterate.isEmpty(Maps.fixedSize.of("1", "1")));
    }

    @Test
    public void notEmpty()
    {
        Assertions.assertFalse(MapIterate.notEmpty(null));
        Assertions.assertFalse(MapIterate.notEmpty(UnifiedMap.newMap()));
        Assertions.assertTrue(MapIterate.notEmpty(Maps.fixedSize.of("1", "1")));
    }

    @Test
    public void reject()
    {
        MutableList<Integer> result = MapIterate.reject(newLittleMap(),
                Predicates.greaterThanOrEqualTo(2));
        Assertions.assertEquals(FastList.newListWith(1), result);
    }

    private static MutableMap<Character, Integer> newLittleMap()
    {
        return UnifiedMap.<Character, Integer>newMap().withKeysValues('a', 1).withKeysValues('b', 2);
    }

    @Test
    public void addAllKeysToCollection()
    {
        MutableList<Character> target = Lists.mutable.of();
        MapIterate.addAllKeysTo(newLittleMap(), target);
        Assertions.assertEquals(FastList.newListWith('a', 'b').toBag(), target.toBag());
    }

    @Test
    public void addAllValuesToCollection()
    {
        MutableList<Integer> target = Lists.mutable.of();
        MapIterate.addAllValuesTo(newLittleMap(), target);
        Assertions.assertEquals(FastList.newListWith(1, 2).toBag(), target.toBag());
    }

    @Test
    public void collect()
    {
        MutableList<String> result = MapIterate.collect(newLittleMap(), Functions.getToString());
        Assertions.assertEquals(FastList.newListWith("1", "2").toBag(), result.toBag());
    }

    @Test
    public void collectBoolean()
    {
        MutableBooleanCollection result = MapIterate.collectBoolean(MapIterateTest.newLittleMap(), PrimitiveFunctions.integerIsPositive());
        Assertions.assertEquals(BooleanHashBag.newBagWith(true, true), result.toBag());
    }

    @Test
    public void collectBooleanWithTarget()
    {
        BooleanHashBag target = new BooleanHashBag();
        BooleanHashBag result = MapIterate.collectBoolean(MapIterateTest.newLittleMap(), PrimitiveFunctions.integerIsPositive(), target);
        Assertions.assertEquals(BooleanHashBag.newBagWith(true, true), result.toBag());
        Assertions.assertSame(target, result, "Target sent as parameter was not returned as result");
    }

    @Test
    public void collectByte()
    {
        MutableByteCollection result = MapIterate.collectByte(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToByte());
        Assertions.assertEquals(ByteHashBag.newBagWith((byte) 1, (byte) 2), result.toBag());
    }

    @Test
    public void collectByteWithTarget()
    {
        ByteHashBag target = new ByteHashBag();
        ByteHashBag result = MapIterate.collectByte(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToByte(), target);
        Assertions.assertEquals(ByteHashBag.newBagWith((byte) 1, (byte) 2), result.toBag());
        Assertions.assertSame(target, result, "Target sent as parameter was not returned as result");
    }

    @Test
    public void collectChar()
    {
        MutableCharCollection result = MapIterate.collectChar(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToChar());
        Assertions.assertEquals(CharHashBag.newBagWith((char) 1, (char) 2), result.toBag());
    }

    @Test
    public void collectCharWithTarget()
    {
        CharHashBag target = new CharHashBag();
        CharHashBag result = MapIterate.collectChar(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToChar(), target);
        Assertions.assertEquals(CharHashBag.newBagWith((char) 1, (char) 2), result.toBag());
        Assertions.assertSame(target, result, "Target sent as parameter was not returned as result");
    }

    @Test
    public void collectDouble()
    {
        MutableDoubleCollection result = MapIterate.collectDouble(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToDouble());
        Assertions.assertEquals(DoubleHashBag.newBagWith(1, 2), result.toBag());
    }

    @Test
    public void collectDoubleWithTarget()
    {
        DoubleHashBag target = new DoubleHashBag();
        DoubleHashBag result = MapIterate.collectDouble(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToDouble(), target);
        Assertions.assertEquals(DoubleHashBag.newBagWith(1, 2), result.toBag());
        Assertions.assertSame(target, result, "Target sent as parameter was not returned as result");
    }

    @Test
    public void collectFloat()
    {
        MutableFloatCollection result = MapIterate.collectFloat(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToFloat());
        Assertions.assertEquals(FloatHashBag.newBagWith(1, 2), result.toBag());
    }

    @Test
    public void collectFloatWithTarget()
    {
        FloatHashBag target = new FloatHashBag();
        FloatHashBag result = MapIterate.collectFloat(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToFloat(), target);
        Assertions.assertEquals(FloatHashBag.newBagWith(1, 2), result.toBag());
        Assertions.assertSame(target, result, "Target sent as parameter was not returned as result");
    }

    @Test
    public void collectInt()
    {
        MutableIntCollection result = MapIterate.collectInt(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToInt());
        Assertions.assertEquals(IntHashBag.newBagWith(1, 2), result.toBag());
    }

    @Test
    public void collectIntWithTarget()
    {
        IntHashBag target = new IntHashBag();
        IntHashBag result = MapIterate.collectInt(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToInt(), target);
        Assertions.assertEquals(IntHashBag.newBagWith(1, 2), result.toBag());
        Assertions.assertSame(target, result, "Target sent as parameter was not returned as result");
    }

    @Test
    public void collectLong()
    {
        MutableLongCollection result = MapIterate.collectLong(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToLong());
        Assertions.assertEquals(LongHashBag.newBagWith(1L, 2L), result.toBag());
    }

    @Test
    public void collectLongWithTarget()
    {
        LongHashBag target = new LongHashBag();
        LongHashBag result = MapIterate.collectLong(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToLong(), target);
        Assertions.assertEquals(LongHashBag.newBagWith(1L, 2L), result.toBag());
        Assertions.assertSame(target, result, "Target sent as parameter was not returned as result");
    }

    @Test
    public void collectShort()
    {
        MutableShortCollection result = MapIterate.collectShort(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToShort());
        Assertions.assertEquals(ShortHashBag.newBagWith((short) 1, (short) 2), result.toBag());
    }

    @Test
    public void collectShortWithTarget()
    {
        ShortHashBag target = new ShortHashBag();
        MutableShortCollection result = MapIterate.collectShort(MapIterateTest.newLittleMap(), PrimitiveFunctions.unboxIntegerToShort(), target);
        Assertions.assertEquals(ShortHashBag.newBagWith((short) 1, (short) 2), result.toBag());
        Assertions.assertSame(target, result, "Target sent as parameter was not returned as result");
    }

    @Test
    public void collectValues()
    {
        MutableMap<Character, String> result = MapIterate.collectValues(newLittleMap(), (argument1, argument2) -> argument2.toString());
        Assertions.assertEquals(UnifiedMap.newWithKeysValues('a', "1", 'b', "2").toBag(), result.toBag());
    }

    @Test
    public void collectIntoTarget()
    {
        MutableList<String> target = Lists.mutable.of();
        MutableList<String> result = MapIterate.collect(newLittleMap(), String::valueOf, target);
        Assertions.assertEquals(FastList.newListWith("1", "2").toBag(), result.toBag());
        Assertions.assertSame(target, result);
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(MapIterate.class);
    }
}
