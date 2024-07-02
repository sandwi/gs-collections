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

package com.gs.collections.impl.multimap.set.sorted;

import java.util.Collections;
import java.util.Comparator;

import com.gs.collections.api.multimap.set.MutableSetMultimap;
import com.gs.collections.api.multimap.sortedset.MutableSortedSetMultimap;
import com.gs.collections.api.set.sorted.MutableSortedSet;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.factory.IntegerPredicates;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.multimap.set.UnifiedSetMultimap;
import com.gs.collections.impl.set.sorted.mutable.TreeSortedSet;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test of {@link TreeSortedSetMultimap}.
 */
public class TreeSortedSetMultimapTest extends AbstractMutableSortedSetMultimapTestCase
{
    public <K, V> MutableSortedSetMultimap<K, V> newMultimap(Comparator<? super V> comparator)
    {
        return TreeSortedSetMultimap.newMultimap(comparator);
    }

    @Override
    public <K, V> MutableSortedSetMultimap<K, V> newMultimap()
    {
        return TreeSortedSetMultimap.newMultimap();
    }

    @Override
    public <K, V> MutableSortedSetMultimap<K, V> newMultimapWithKeyValue(K key, V value)
    {
        MutableSortedSetMultimap<K, V> mutableMultimap = this.newMultimap();
        mutableMultimap.put(key, value);
        return mutableMultimap;
    }

    @Override
    public <K, V> MutableSortedSetMultimap<K, V> newMultimapWithKeysValues(K key1, V value1, K key2, V value2)
    {
        MutableSortedSetMultimap<K, V> mutableMultimap = this.newMultimap();
        mutableMultimap.put(key1, value1);
        mutableMultimap.put(key2, value2);
        return mutableMultimap;
    }

    @Override
    public <K, V> MutableSortedSetMultimap<K, V> newMultimapWithKeysValues(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3)
    {
        MutableSortedSetMultimap<K, V> mutableMultimap = this.newMultimap();
        mutableMultimap.put(key1, value1);
        mutableMultimap.put(key2, value2);
        mutableMultimap.put(key3, value3);
        return mutableMultimap;
    }

    @Override
    public <K, V> MutableSortedSetMultimap<K, V> newMultimapWithKeysValues(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4)
    {
        MutableSortedSetMultimap<K, V> mutableMultimap = this.newMultimap();
        mutableMultimap.put(key1, value1);
        mutableMultimap.put(key2, value2);
        mutableMultimap.put(key3, value3);
        mutableMultimap.put(key4, value4);
        return mutableMultimap;
    }

    @SafeVarargs
    @Override
    public final <K, V> MutableSortedSetMultimap<K, V> newMultimap(Pair<K, V>... pairs)
    {
        return TreeSortedSetMultimap.newMultimap(pairs);
    }

    @Override
    protected <K, V> MutableSortedSetMultimap<K, V> newMultimapFromPairs(Iterable<Pair<K, V>> inputIterable)
    {
        return TreeSortedSetMultimap.newMultimap(inputIterable);
    }

    @SafeVarargs
    @Override
    protected final <V> TreeSortedSet<V> createCollection(V... args)
    {
        return TreeSortedSet.newSetWith(args);
    }

    @Test
    public void testEmptyConstructor()
    {
        MutableSortedSetMultimap<Integer, Integer> map = TreeSortedSetMultimap.newMultimap();
        for (int i = 1; i < 6; ++i)
        {
            for (int j = 1; j < i + 1; ++j)
            {
                map.put(i, j);
            }
        }
        Verify.assertSize(5, map.keysView().toList());
        for (int i = 1; i < 6; ++i)
        {
            Verify.assertSortedSetsEqual(TreeSortedSet.newSet(Interval.oneTo(i)), map.get(i));
        }
    }

    @Test
    public void testComparatorConstructors()
    {
        MutableSortedSetMultimap<Boolean, Integer> revMap = TreeSortedSetMultimap.newMultimap(Collections.<Integer>reverseOrder());
        for (int i = 1; i < 10; ++i)
        {
            revMap.put(IntegerPredicates.isOdd().accept(i), i);
        }
        Verify.assertSize(2, revMap.keysView().toList());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.<Integer>reverseOrder(), 9, 7, 5, 3, 1), revMap.get(Boolean.TRUE));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.<Integer>reverseOrder(), 8, 6, 4, 2), revMap.get(Boolean.FALSE));
        MutableSortedSetMultimap<Boolean, Integer> revMap2 = TreeSortedSetMultimap.newMultimap(revMap);
        Verify.assertMapsEqual(revMap2.toMap(), revMap.toMap());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.<Integer>reverseOrder(), 9, 7, 5, 3, 1), revMap2.get(Boolean.TRUE));
    }

    @Test
    public void testMultimapConstructor()
    {
        MutableSetMultimap<Integer, Integer> map = UnifiedSetMultimap.newMultimap();
        TreeSortedSetMultimap<Integer, Integer> map2 = TreeSortedSetMultimap.newMultimap();
        for (int i = 1; i < 6; ++i)
        {
            map.putAll(i, Interval.oneTo(i));
            map2.putAll(i, Interval.oneTo(i));
        }
        TreeSortedSetMultimap<Integer, Integer> sortedMap = TreeSortedSetMultimap.newMultimap(map);
        TreeSortedSetMultimap<Integer, Integer> sortedMap2 = TreeSortedSetMultimap.newMultimap(map2);
        for (int i = 1; i < 6; ++i)
        {
            Verify.assertSortedSetsEqual(map.get(i).toSortedSet(), sortedMap.get(i));
            Verify.assertSortedSetsEqual(map.get(i).toSortedSet(), sortedMap2.get(i));
        }
    }

    @Test
    public void testCollection()
    {
        TreeSortedSetMultimap<Integer, Integer> setMultimap = TreeSortedSetMultimap.newMultimap(Collections.<Integer>reverseOrder());
        MutableSortedSet<Integer> collection = setMultimap.createCollection();
        collection.addAll(FastList.newListWith(1, 4, 2, 3, 5));
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.<Integer>reverseOrder(), 5, 4, 3, 2, 1), collection);
        setMultimap.putAll(1, collection);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Collections.<Integer>reverseOrder(), 5, 4, 3, 2, 1), collection);
        setMultimap.put(1, 0);
        Assertions.assertEquals(Integer.valueOf(0), setMultimap.get(1).getLast());
        setMultimap.putAll(2, FastList.newListWith(0, 1, 2, 4, 2, 1, 4, 5, 3, 4, 5));
        Verify.assertSortedSetsEqual(setMultimap.get(1), setMultimap.get(2));
    }

    @Test
    public void testNewEmpty()
    {
        TreeSortedSetMultimap<Object, Integer> expected = TreeSortedSetMultimap.newMultimap(Collections.<Integer>reverseOrder());
        TreeSortedSetMultimap<Object, Integer> actual = expected.newEmpty();
        expected.putAll(1, FastList.newListWith(4, 3, 1, 2));
        expected.putAll(2, FastList.newListWith(5, 7, 6, 8));
        actual.putAll(1, FastList.newListWith(4, 3, 1, 2));
        actual.putAll(2, FastList.newListWith(5, 7, 6, 8));
        Verify.assertMapsEqual(expected.toMap(), actual.toMap());
        Verify.assertSortedSetsEqual(expected.get(1), actual.get(1));
        Verify.assertSortedSetsEqual(expected.get(2), actual.get(2));
    }
}
