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

package com.gs.collections.impl.map.mutable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentMap;

import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.ImmutableMapIterable;
import com.gs.collections.api.map.MapIterable;
import com.gs.collections.api.map.MutableMapIterable;
import com.gs.collections.impl.IntegerWithCast;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.function.PassThruFunction0;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.factory.Maps;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.AbstractSynchronizedMapIterable;
import com.gs.collections.impl.map.MapIterableTestCase;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.test.domain.Key;
import com.gs.collections.impl.tuple.ImmutableEntry;
import com.gs.collections.impl.tuple.Tuples;
import com.gs.collections.impl.utility.Iterate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.gs.collections.impl.factory.Iterables.iMap;
import static com.gs.collections.impl.factory.Iterables.mList;

/**
 * Abstract JUnit TestCase for {@link MutableMapIterable}s.
 */
public abstract class MutableMapIterableTestCase extends MapIterableTestCase
{
    @Override
    protected abstract <K, V> MutableMapIterable<K, V> newMap();

    @Override
    protected abstract <K, V> MutableMapIterable<K, V> newMapWithKeyValue(K key, V value);

    @Override
    protected abstract <K, V> MutableMapIterable<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2);

    @Override
    protected abstract <K, V> MutableMapIterable<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3);

    @Override
    protected abstract <K, V> MutableMapIterable<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4);

    @Test
    public void toImmutable()
    {
        MutableMapIterable<Integer, String> map = this.newMapWithKeyValue(1, "One");
        ImmutableMapIterable<Integer, String> immutable = map.toImmutable();
        Assertions.assertEquals(Maps.immutable.with(1, "One"), immutable);
    }

    @Test
    public void clear()
    {
        MutableMapIterable<Integer, Object> map =
                this.<Integer, Object>newMapWithKeysValues(1, "One", 2, "Two", 3, "Three");
        map.clear();
        Verify.assertEmpty(map);

        MutableMapIterable<Object, Object> map2 = this.newMap();
        map2.clear();
        Verify.assertEmpty(map2);
    }

    @Test
    public void removeObject()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        map.remove("Two");
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues("One", 1, "Three", 3), map);
    }

    @Test
    public void removeFromEntrySet()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        Assertions.assertTrue(map.entrySet().remove(ImmutableEntry.of("Two", 2)));
        Assertions.assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Three", 3), map);

        Assertions.assertFalse(map.entrySet().remove(ImmutableEntry.of("Four", 4)));
        Assertions.assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Three", 3), map);

        Assertions.assertFalse(map.entrySet().remove(null));

        MutableMapIterable<String, Integer> mapWithNullKey = this.newMapWithKeysValues("One", 1, null, 2, "Three", 3);
        Assertions.assertTrue(mapWithNullKey.entrySet().remove(new ImmutableEntry<String, Integer>(null, 2)));
    }

    @Test
    public void removeAllFromEntrySet()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        Assertions.assertTrue(map.entrySet().removeAll(FastList.newListWith(
                ImmutableEntry.of("One", 1),
                ImmutableEntry.of("Three", 3))));
        Assertions.assertEquals(UnifiedMap.newWithKeysValues("Two", 2), map);

        Assertions.assertFalse(map.entrySet().removeAll(FastList.newListWith(ImmutableEntry.of("Four", 4))));
        Assertions.assertEquals(UnifiedMap.newWithKeysValues("Two", 2), map);

        Assertions.assertFalse(map.entrySet().remove(null));

        MutableMapIterable<String, Integer> mapWithNullKey = this.newMapWithKeysValues("One", 1, null, 2, "Three", 3);
        Assertions.assertTrue(mapWithNullKey.entrySet().removeAll(FastList.newListWith(ImmutableEntry.of(null, 2))));
    }

    @Test
    public void retainAllFromEntrySet()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        Assertions.assertFalse(map.entrySet().retainAll(FastList.newListWith(
                ImmutableEntry.of("One", 1),
                ImmutableEntry.of("Two", 2),
                ImmutableEntry.of("Three", 3),
                ImmutableEntry.of("Four", 4))));

        Assertions.assertTrue(map.entrySet().retainAll(FastList.newListWith(
                ImmutableEntry.of("One", 1),
                ImmutableEntry.of("Three", 3),
                ImmutableEntry.of("Four", 4))));
        Assertions.assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Three", 3), map);

        MutableMapIterable<Integer, Integer> integers = this.newMapWithKeysValues(1, 1, 2, 2, 3, 3);
        Integer copy = Integer.valueOf(1);
        Assertions.assertTrue(integers.entrySet().retainAll(mList(ImmutableEntry.of(copy, copy))));
        Assertions.assertEquals(iMap(copy, copy), integers);
        Assertions.assertNotSame(copy, Iterate.getOnly(integers.entrySet()).getKey());
        Assertions.assertNotSame(copy, Iterate.getOnly(integers.entrySet()).getValue());
    }

    @Test
    public void clearEntrySet()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        map.entrySet().clear();
        Verify.assertEmpty(map);
    }

    @Test
    public void entrySetEqualsAndHashCode()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        Verify.assertEqualsAndHashCode(
                UnifiedSet.newSetWith(
                        ImmutableEntry.of("One", 1),
                        ImmutableEntry.of("Two", 2),
                        ImmutableEntry.of("Three", 3)),
                map.entrySet());
    }

    @Test
    public void removeFromKeySet()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        Assertions.assertFalse(map.keySet().remove("Four"));

        Assertions.assertTrue(map.keySet().remove("Two"));
        Assertions.assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Three", 3), map);
    }

    @Test
    public void removeNullFromKeySet()
    {
        if (this.newMap() instanceof ConcurrentMap || this.newMap() instanceof SortedMap)
        {
            return;
        }

        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        Assertions.assertFalse(map.keySet().remove(null));
        Assertions.assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Two", 2, "Three", 3), map);
        map.put(null, 4);
        Assertions.assertTrue(map.keySet().remove(null));
        Assertions.assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Two", 2, "Three", 3), map);
    }

    @Test
    public void removeAllFromKeySet()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        Assertions.assertFalse(map.keySet().removeAll(FastList.newListWith("Four")));

        Assertions.assertTrue(map.keySet().removeAll(FastList.newListWith("Two", "Four")));
        Assertions.assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Three", 3), map);
    }

    @Test
    public void retainAllFromKeySet()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        Assertions.assertFalse(map.keySet().retainAll(FastList.newListWith("One", "Two", "Three", "Four")));

        Assertions.assertTrue(map.keySet().retainAll(FastList.newListWith("One", "Three")));
        Assertions.assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Three", 3), map);
    }

    @Test
    public void clearKeySet()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        map.keySet().clear();
        Verify.assertEmpty(map);
    }

    @Test
    public void keySetEqualsAndHashCode()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3, null, null);
        Verify.assertEqualsAndHashCode(UnifiedSet.newSetWith("One", "Two", "Three", null), map.keySet());
    }

    @Test
    public void keySetToArray()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        MutableList<String> expected = FastList.newListWith("One", "Two", "Three").toSortedList();
        Set<String> keySet = map.keySet();
        Assertions.assertEquals(expected, FastList.newListWith(keySet.toArray()).toSortedList());
        Assertions.assertEquals(expected, FastList.newListWith(keySet.toArray(new String[keySet.size()])).toSortedList());
    }

    @Test
    public void removeFromValues()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        Assertions.assertFalse(map.values().remove(4));

        Assertions.assertTrue(map.values().remove(2));
        Assertions.assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Three", 3), map);
    }

    @Test
    public void removeNullFromValues()
    {
        if (this.newMap() instanceof ConcurrentMap)
        {
            return;
        }

        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        Assertions.assertFalse(map.values().remove(null));
        Assertions.assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Two", 2, "Three", 3), map);
        map.put("Four", null);
        Assertions.assertTrue(map.values().remove(null));
        Assertions.assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Two", 2, "Three", 3), map);
    }

    @Test
    public void removeAllFromValues()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        Assertions.assertFalse(map.values().removeAll(FastList.newListWith(4)));

        Assertions.assertTrue(map.values().removeAll(FastList.newListWith(2, 4)));
        Assertions.assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Three", 3), map);
    }

    @Test
    public void retainAllFromValues()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3);
        Assertions.assertFalse(map.values().retainAll(FastList.newListWith(1, 2, 3, 4)));

        Assertions.assertTrue(map.values().retainAll(FastList.newListWith(1, 3)));
        Assertions.assertEquals(UnifiedMap.newWithKeysValues("One", 1, "Three", 3), map);
    }

    @Test
    public void put()
    {
        MutableMapIterable<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two");
        Assertions.assertNull(map.put(3, "Three"));
        Assertions.assertEquals(UnifiedMap.newWithKeysValues(1, "One", 2, "Two", 3, "Three"), map);

        ImmutableList<Integer> key1 = Lists.immutable.with(null);
        ImmutableList<Integer> key2 = Lists.immutable.with(null);
        Object value1 = new Object();
        Object value2 = new Object();
        MutableMapIterable<ImmutableList<Integer>, Object> map2 = this.newMapWithKeyValue(key1, value1);
        Object previousValue = map2.put(key2, value2);
        Assertions.assertSame(value1, previousValue);
        Assertions.assertSame(key1, map2.keysView().getFirst());
    }

    @Test
    public void putAll()
    {
        MutableMapIterable<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "2");
        MutableMapIterable<Integer, String> toAdd = this.newMapWithKeysValues(2, "Two", 3, "Three");

        map.putAll(toAdd);
        Verify.assertSize(3, map);
        Verify.assertContainsAllKeyValues(map, 1, "One", 2, "Two", 3, "Three");

        //Testing JDK map
        MutableMapIterable<Integer, String> map2 = this.newMapWithKeysValues(1, "One", 2, "2");
        HashMap<Integer, String> hashMaptoAdd = new HashMap<>(toAdd);
        map2.putAll(hashMaptoAdd);
        Verify.assertSize(3, map2);
        Verify.assertContainsAllKeyValues(map2, 1, "One", 2, "Two", 3, "Three");
    }

    @Test
    public void removeKey()
    {
        MutableMapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "Two");

        Assertions.assertEquals("1", map.removeKey(1));
        Verify.assertSize(1, map);
        Verify.denyContainsKey(1, map);

        Assertions.assertNull(map.removeKey(42));
        Verify.assertSize(1, map);

        Assertions.assertEquals("Two", map.removeKey(2));
        Verify.assertEmpty(map);
    }

    @Test
    public void getIfAbsentPut()
    {
        MutableMapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        Assertions.assertNull(map.get(4));
        Assertions.assertEquals("4", map.getIfAbsentPut(4, new PassThruFunction0<>("4")));
        Assertions.assertEquals("3", map.getIfAbsentPut(3, new PassThruFunction0<>("3")));
        Verify.assertContainsKeyValue(4, "4", map);
    }

    @Test
    public void getIfAbsentPutValue()
    {
        MutableMapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        Assertions.assertNull(map.get(4));
        Assertions.assertEquals("4", map.getIfAbsentPut(4, "4"));
        Assertions.assertEquals("3", map.getIfAbsentPut(3, "5"));
        Verify.assertContainsKeyValue(1, "1", map);
        Verify.assertContainsKeyValue(2, "2", map);
        Verify.assertContainsKeyValue(3, "3", map);
        Verify.assertContainsKeyValue(4, "4", map);
    }

    @Test
    public void getIfAbsentPutWithKey()
    {
        MutableMapIterable<Integer, Integer> map = this.newMapWithKeysValues(1, 1, 2, 2, 3, 3);
        Assertions.assertNull(map.get(4));
        Assertions.assertEquals(Integer.valueOf(4), map.getIfAbsentPutWithKey(4, Functions.getIntegerPassThru()));
        Assertions.assertEquals(Integer.valueOf(3), map.getIfAbsentPutWithKey(3, Functions.getIntegerPassThru()));
        Verify.assertContainsKeyValue(Integer.valueOf(4), Integer.valueOf(4), map);
    }

    @Test
    public void getIfAbsentPutWith()
    {
        MutableMapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        Assertions.assertNull(map.get(4));
        Assertions.assertEquals("4", map.getIfAbsentPutWith(4, String::valueOf, 4));
        Assertions.assertEquals("3", map.getIfAbsentPutWith(3, String::valueOf, 3));
        Verify.assertContainsKeyValue(4, "4", map);
    }

    @Test
    public void getIfAbsentPut_block_throws()
    {
        MutableMapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        Verify.assertThrows(RuntimeException.class, () -> map.getIfAbsentPut(4, () -> {
            throw new RuntimeException();
        }));
        Assertions.assertEquals(UnifiedMap.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);
    }

    @Test
    public void getIfAbsentPutWith_block_throws()
    {
        MutableMapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        Verify.assertThrows(RuntimeException.class, () -> map.getIfAbsentPutWith(4, object -> {
            throw new RuntimeException();
        }, null));
        Assertions.assertEquals(UnifiedMap.newWithKeysValues(1, "1", 2, "2", 3, "3"), map);
    }

    @Test
    public void getKeysAndGetValues()
    {
        MutableMapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2", 3, "3");
        Verify.assertContainsAll(map.keySet(), 1, 2, 3);
        Verify.assertContainsAll(map.values(), "1", "2", "3");
    }

    @Test
    public void newEmpty()
    {
        MutableMapIterable<Integer, Integer> map = this.newMapWithKeysValues(1, 1, 2, 2);
        Verify.assertEmpty(map.newEmpty());
    }

    @Test
    public void keysAndValues_toString()
    {
        MutableMapIterable<Integer, String> map = this.newMapWithKeysValues(1, "1", 2, "2");
        Verify.assertContains(FastList.newListWith("[1, 2]", "[2, 1]"), map.keySet().toString());
        Verify.assertContains(FastList.newListWith("[1, 2]", "[2, 1]"), map.values().toString());
        Verify.assertContains(FastList.newListWith("[1, 2]", "[2, 1]"), map.keysView().toString());
        Verify.assertContains(FastList.newListWith("[1, 2]", "[2, 1]"), map.valuesView().toString());
    }

    @Test
    public void keyPreservation()
    {
        Key key = new Key("key");

        Key duplicateKey1 = new Key("key");
        MapIterable<Key, Integer> map1 = this.newMapWithKeysValues(key, 1, duplicateKey1, 2);
        Verify.assertSize(1, map1);
        Verify.assertContainsKeyValue(key, 2, map1);
        Assertions.assertSame(key, map1.keysView().getFirst());

        Key duplicateKey2 = new Key("key");
        MapIterable<Key, Integer> map2 = this.newMapWithKeysValues(key, 1, duplicateKey1, 2, duplicateKey2, 3);
        Verify.assertSize(1, map2);
        Verify.assertContainsKeyValue(key, 3, map2);
        Assertions.assertSame(key, map1.keysView().getFirst());

        Key duplicateKey3 = new Key("key");
        MapIterable<Key, Integer> map3 = this.newMapWithKeysValues(key, 1, new Key("not a dupe"), 2, duplicateKey3, 3);
        Verify.assertSize(2, map3);
        Verify.assertContainsAllKeyValues(map3, key, 3, new Key("not a dupe"), 2);
        Assertions.assertSame(key, map3.keysView().detect(key::equals));

        Key duplicateKey4 = new Key("key");
        MapIterable<Key, Integer> map4 = this.newMapWithKeysValues(key, 1, new Key("still not a dupe"), 2, new Key("me neither"), 3, duplicateKey4, 4);
        Verify.assertSize(3, map4);
        Verify.assertContainsAllKeyValues(map4, key, 4, new Key("still not a dupe"), 2, new Key("me neither"), 3);
        Assertions.assertSame(key, map4.keysView().detect(key::equals));

        MapIterable<Key, Integer> map5 = this.newMapWithKeysValues(key, 1, duplicateKey1, 2, duplicateKey3, 3, duplicateKey4, 4);
        Verify.assertSize(1, map5);
        Verify.assertContainsKeyValue(key, 4, map5);
        Assertions.assertSame(key, map5.keysView().getFirst());
    }

    @Test
    public void asUnmodifiable()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.newMapWithKeysValues(1, 1, 2, 2).asUnmodifiable().put(3, 3));
    }

    @Test
    public void asSynchronized()
    {
        MapIterable<Integer, Integer> map = this.newMapWithKeysValues(1, 1, 2, 2).asSynchronized();
        Verify.assertInstanceOf(AbstractSynchronizedMapIterable.class, map);
    }

    @Test
    public void add()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeyValue("A", 1);

        Assertions.assertEquals(Integer.valueOf(1), map.add(Tuples.pair("A", 3)));
        Assertions.assertNull(map.add(Tuples.pair("B", 2)));
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues("A", 3, "B", 2), map);
    }

    @Test
    public void withKeyValue()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeyValue("A", 1);

        MutableMapIterable<String, Integer> mapWith = map.withKeyValue("B", 2);
        Assertions.assertSame(map, mapWith);
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues("A", 1, "B", 2), mapWith);

        MutableMapIterable<String, Integer> mapWith2 = mapWith.withKeyValue("A", 11);
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues("A", 11, "B", 2), mapWith);
    }

    @Test
    public void withAllKeyValues()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("A", 1, "B", 2);
        MutableMapIterable<String, Integer> mapWith = map.withAllKeyValues(
                FastList.newListWith(Tuples.pair("B", 22), Tuples.pair("C", 3)));
        Assertions.assertSame(map, mapWith);
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues("A", 1, "B", 22, "C", 3), mapWith);
    }

    @Test
    public void withAllKeyValueArguments()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("A", 1, "B", 2);
        MutableMapIterable<String, Integer> mapWith = map.withAllKeyValueArguments(Tuples.pair("B", 22), Tuples.pair("C", 3));
        Assertions.assertSame(map, mapWith);
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues("A", 1, "B", 22, "C", 3), mapWith);
    }

    @Test
    public void withoutKey()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("A", 1, "B", 2);
        MutableMapIterable<String, Integer> mapWithout = map.withoutKey("B");
        Assertions.assertSame(map, mapWithout);
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues("A", 1), mapWithout);
    }

    @Test
    public void withoutAllKeys()
    {
        MutableMapIterable<String, Integer> map = this.newMapWithKeysValues("A", 1, "B", 2, "C", 3);
        MutableMapIterable<String, Integer> mapWithout = map.withoutAllKeys(FastList.newListWith("A", "C"));
        Assertions.assertSame(map, mapWithout);
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues("B", 2), mapWithout);
    }

    @Test
    public void retainAllFromKeySet_null_collision()
    {
        if (this.newMap() instanceof ConcurrentMap || this.newMap() instanceof SortedMap)
        {
            return;
        }

        IntegerWithCast key = new IntegerWithCast(0);
        MutableMapIterable<IntegerWithCast, String> mutableMapIterable = this.newMapWithKeysValues(
                null, "Test 1",
                key, "Test 2");

        Assertions.assertFalse(mutableMapIterable.keySet().retainAll(FastList.newListWith(key, null)));

        Assertions.assertEquals(
                this.newMapWithKeysValues(
                        null, "Test 1",
                        key, "Test 2"),
                mutableMapIterable);
    }

    @Test
    public void rehash_null_collision()
    {
        if (this.newMap() instanceof ConcurrentMap || this.newMap() instanceof SortedMap)
        {
            return;
        }
        MutableMapIterable<IntegerWithCast, String> mutableMapIterable = this.newMapWithKeyValue(null, null);

        for (int i = 0; i < 256; i++)
        {
            mutableMapIterable.put(new IntegerWithCast(i), String.valueOf(i));
        }
    }

    @Test
    public void updateValue()
    {
        MutableMapIterable<Integer, Integer> map = this.newMap();
        Iterate.forEach(Interval.oneTo(1000), each -> map.updateValue(each % 10, () -> 0, integer -> integer + 1));
        Assertions.assertEquals(Interval.zeroTo(9).toSet(), map.keySet());
        Assertions.assertEquals(FastList.newList(Collections.nCopies(10, 100)), FastList.newList(map.values()));
    }

    @Test
    public void updateValue_collisions()
    {
        MutableMapIterable<Integer, Integer> map = this.newMap();
        MutableList<Integer> list = Interval.oneTo(2000).toList().shuffleThis();
        Iterate.forEach(list, each -> map.updateValue(each % 1000, () -> 0, integer -> integer + 1));
        Assertions.assertEquals(Interval.zeroTo(999).toSet(), map.keySet());
        Assertions.assertEquals(
                FastList.newList(Collections.nCopies(1000, 2)),
                FastList.newList(map.values()),
                HashBag.newBag(map.values()).toStringOfItemToCount());
    }

    @Test
    public void updateValueWith()
    {
        MutableMapIterable<Integer, Integer> map = this.newMap();
        Iterate.forEach(Interval.oneTo(1000), each -> map.updateValueWith(each % 10, () -> 0, (integer, parameter) -> {
            Assertions.assertEquals("test", parameter);
            return integer + 1;
        }, "test"));
        Assertions.assertEquals(Interval.zeroTo(9).toSet(), map.keySet());
        Assertions.assertEquals(FastList.newList(Collections.nCopies(10, 100)), FastList.newList(map.values()));
    }

    @Test
    public void updateValueWith_collisions()
    {
        MutableMapIterable<Integer, Integer> map = this.newMap();
        MutableList<Integer> list = Interval.oneTo(2000).toList().shuffleThis();
        Iterate.forEach(list, each -> map.updateValueWith(each % 1000, () -> 0, (integer, parameter) -> {
            Assertions.assertEquals("test", parameter);
            return integer + 1;
        }, "test"));
        Assertions.assertEquals(Interval.zeroTo(999).toSet(), map.keySet());
        Assertions.assertEquals(
                FastList.newList(Collections.nCopies(1000, 2)),
                FastList.newList(map.values()),
                HashBag.newBag(map.values()).toStringOfItemToCount());
    }
}
