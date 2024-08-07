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

package com.gs.collections.impl.multimap.bag.sorted.mutable;

import java.util.Collections;
import java.util.Comparator;

import com.gs.collections.api.bag.sorted.MutableSortedBag;
import com.gs.collections.api.multimap.MutableMultimap;
import com.gs.collections.api.multimap.sortedbag.MutableSortedBagMultimap;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.bag.sorted.mutable.TreeBag;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.IntegerPredicates;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.test.SerializeTestHelper;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test of {@link TreeBagMultimap}.
 */
public class TreeBagMultimapTest extends AbstractMutableSortedBagMultimapTestCase
{
    @Override
    protected <K, V> MutableSortedBagMultimap<K, V> newMultimap(Comparator<V> comparator)
    {
        return TreeBagMultimap.newMultimap(comparator);
    }

    @Override
    public <K, V> MutableSortedBagMultimap<K, V> newMultimap()
    {
        return TreeBagMultimap.newMultimap();
    }

    @Override
    public <K, V> MutableSortedBagMultimap<K, V> newMultimapWithKeyValue(K key, V value)
    {
        MutableSortedBagMultimap<K, V> mutableMultimap = this.newMultimap();
        mutableMultimap.put(key, value);
        return mutableMultimap;
    }

    @Override
    public <K, V> MutableSortedBagMultimap<K, V> newMultimapWithKeysValues(K key1, V value1, K key2, V value2)
    {
        MutableSortedBagMultimap<K, V> mutableMultimap = this.newMultimap();
        mutableMultimap.put(key1, value1);
        mutableMultimap.put(key2, value2);
        return mutableMultimap;
    }

    @Override
    public <K, V> MutableSortedBagMultimap<K, V> newMultimapWithKeysValues(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3)
    {
        MutableSortedBagMultimap<K, V> mutableMultimap = this.newMultimap();
        mutableMultimap.put(key1, value1);
        mutableMultimap.put(key2, value2);
        mutableMultimap.put(key3, value3);
        return mutableMultimap;
    }

    @Override
    public <K, V> MutableSortedBagMultimap<K, V> newMultimapWithKeysValues(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4)
    {
        MutableSortedBagMultimap<K, V> mutableMultimap = this.newMultimap();
        mutableMultimap.put(key1, value1);
        mutableMultimap.put(key2, value2);
        mutableMultimap.put(key3, value3);
        mutableMultimap.put(key4, value4);
        return mutableMultimap;
    }

    @Override
    public <K, V> MutableSortedBagMultimap<K, V> newMultimap(Pair<K, V>... pairs)
    {
        return TreeBagMultimap.newMultimap(pairs);
    }

    @Override
    protected <K, V> MutableSortedBagMultimap<K, V> newMultimapFromPairs(Iterable<Pair<K, V>> inputIterable)
    {
        return TreeBagMultimap.newMultimap(inputIterable);
    }

    @Override
    protected <V> TreeBag<V> createCollection(V... args)
    {
        return TreeBag.newBagWith(args);
    }

    @Test
    public void testEmptyConstructor()
    {
        MutableSortedBagMultimap<Integer, Integer> map = TreeBagMultimap.newMultimap();
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
            Verify.assertSortedBagsEqual(TreeBag.newBag(Interval.oneTo(i)), map.get(i));
        }
    }

    @Test
    public void testComparatorConstructors()
    {
        MutableSortedBagMultimap<Boolean, Integer> revMap = TreeBagMultimap.newMultimap(Collections.<Integer>reverseOrder());
        for (int i = 1; i < 10; ++i)
        {
            revMap.put(IntegerPredicates.isOdd().accept(i), i);
        }
        Verify.assertSize(2, revMap.keysView().toList());
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.<Integer>reverseOrder(), 9, 7, 5, 3, 1), revMap.get(Boolean.TRUE));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.<Integer>reverseOrder(), 8, 6, 4, 2), revMap.get(Boolean.FALSE));
        MutableSortedBagMultimap<Boolean, Integer> revMap2 = TreeBagMultimap.newMultimap(revMap);
        Verify.assertMapsEqual(revMap2.toMap(), revMap.toMap());
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.<Integer>reverseOrder(), 9, 7, 5, 3, 1), revMap2.get(Boolean.TRUE));
    }

    @Test
    public void testCollection()
    {
        TreeBagMultimap<Integer, Integer> bagMultimap = TreeBagMultimap.newMultimap(Collections.<Integer>reverseOrder());
        MutableSortedBag<Integer> collection = bagMultimap.createCollection();
        collection.addAll(FastList.newListWith(1, 4, 2, 3, 5, 5));
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.<Integer>reverseOrder(), 5, 5, 4, 3, 2, 1), collection);
        bagMultimap.putAll(1, collection);
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.<Integer>reverseOrder(), 5, 5, 4, 3, 2, 1), collection);
        bagMultimap.put(1, 0);
        Assertions.assertEquals(Integer.valueOf(0), bagMultimap.get(1).getLast());
        bagMultimap.putAll(2, FastList.newListWith(0, 1, 2, 3, 4, 5, 5));
        Verify.assertSortedBagsEqual(bagMultimap.get(1), bagMultimap.get(2));
    }

    @Test
    public void testNewEmpty()
    {
        TreeBagMultimap<Object, Integer> expected = TreeBagMultimap.newMultimap(Collections.<Integer>reverseOrder());
        TreeBagMultimap<Object, Integer> actual = expected.newEmpty();
        expected.putAll(1, FastList.newListWith(4, 3, 1, 2));
        expected.putAll(2, FastList.newListWith(5, 7, 6, 8));
        actual.putAll(1, FastList.newListWith(4, 3, 1, 2));
        actual.putAll(2, FastList.newListWith(5, 7, 6, 8));
        Verify.assertMapsEqual(expected.toMap(), actual.toMap());
        Verify.assertSortedBagsEqual(expected.get(1), actual.get(1));
        Verify.assertSortedBagsEqual(expected.get(2), actual.get(2));
    }

    @Override
    @Test
    public void serialization()
    {
        TreeBagMultimap<Integer, Integer> map = TreeBagMultimap.newMultimap(Comparators.<Integer>reverseNaturalOrder());
        map.putAll(1, FastList.newListWith(1, 2, 3, 4));
        map.putAll(2, FastList.newListWith(2, 3, 4, 5));
        Verify.assertPostSerializedEqualsAndHashCode(map);

        TreeBagMultimap<Integer, Integer> deserialized = SerializeTestHelper.serializeDeserialize(map);
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.<Integer>reverseNaturalOrder(), 1, 2, 3, 4),
                deserialized.get(1));

        deserialized.putAll(3, FastList.newListWith(8, 9, 10));
        Verify.assertListsEqual(FastList.newListWith(10, 9, 8), deserialized.get(3).toList());
    }

    @Override
    @Test
    public void testClear()
    {
        MutableMultimap<Integer, String> multimap = this.newMultimapWithKeysValues(1, "One", 2, "Two", 3, "Three");
        multimap.clear();
        Verify.assertEmpty(multimap);
    }
}
