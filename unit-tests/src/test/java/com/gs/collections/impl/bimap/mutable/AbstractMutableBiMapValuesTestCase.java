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

package com.gs.collections.impl.bimap.mutable;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.gs.collections.api.bimap.MutableBiMap;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class AbstractMutableBiMapValuesTestCase
{
    protected abstract MutableBiMap<Float, String> newMapWithKeysValues(float key1, String value1, float key2, String value2);

    protected abstract MutableBiMap<Float, Integer> newMapWithKeysValues(float key1, Integer value1, float key2, Integer value2, float key3, Integer value3);

    protected abstract MutableBiMap<Float, Integer> newMapWithKeysValues(float key1, Integer value1, float key2, Integer value2, float key3, Integer value3, float key4, Integer value4);

    @Test
    public void add()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.newMapWithKeysValues(1.0f, 1, 2.0f, 2, 3.0f, 3).values().add(4);
        });
    }

    @Test
    public void addAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.newMapWithKeysValues(1.0f, 1, 2.0f, 2, 3.0f, 3).values().addAll(FastList.newListWith(4));
        });
    }

    @Test
    public void clear()
    {
        MutableBiMap<Float, Integer> map = this.newMapWithKeysValues(1.0f, 1, 2.0f, 2, 3.0f, 3);
        map.values().clear();
        Verify.assertIterableEmpty(map);
        Verify.assertIterableEmpty(map.inverse());
        Verify.assertEmpty(map.values());
    }

    @Test
    public void contains()
    {
        MutableBiMap<Float, Integer> map = this.newMapWithKeysValues(1.0f, 1, 2.0f, 2, 3.0f, null);
        Collection<Integer> values = map.values();
        Assertions.assertTrue(values.contains(1));
        Assertions.assertTrue(values.contains(2));
        Assertions.assertTrue(values.contains(null));
        Assertions.assertFalse(values.contains(4));
        values.remove(null);
        Assertions.assertFalse(values.contains(null));
        map.remove(1.0f);
        Assertions.assertFalse(values.contains(1));
    }

    @Test
    public void containsAll()
    {
        MutableBiMap<Float, Integer> map = this.newMapWithKeysValues(1.0f, 1, 2.0f, 2, 3.0f, null);
        Collection<Integer> values = map.values();
        Assertions.assertTrue(values.containsAll(FastList.newListWith(1, 2)));
        Assertions.assertTrue(values.containsAll(FastList.newListWith(1, 2, null)));
        Assertions.assertTrue(values.containsAll(FastList.newListWith(null, null)));
        Assertions.assertFalse(values.containsAll(FastList.newListWith(1, 4)));
        Assertions.assertFalse(values.containsAll(FastList.newListWith(5, 4)));
        values.remove(null);
        Assertions.assertFalse(values.containsAll(FastList.newListWith(1, 2, null)));
        Assertions.assertTrue(values.containsAll(FastList.newListWith(1, 2)));
        map.remove(1.0f);
        Assertions.assertFalse(values.containsAll(FastList.newListWith(1, 2)));
        Assertions.assertTrue(values.containsAll(FastList.newListWith(2)));
    }

    @Test
    public void isEmpty()
    {
        MutableBiMap<Float, Integer> map = this.newMapWithKeysValues(1.0f, null, 2.0f, 2, 3.0f, 3);
        Collection<Integer> values = map.values();
        Assertions.assertFalse(values.isEmpty());
        HashBiMap<Float, Integer> map1 = HashBiMap.newMap();
        Collection<Integer> values1 = map1.values();
        Assertions.assertTrue(values1.isEmpty());
        map1.put(1.0f, 1);
        Assertions.assertFalse(values1.isEmpty());
    }

    @Test
    public void size()
    {
        MutableBiMap<Float, Integer> map = this.newMapWithKeysValues(1.0f, 1, 2.0f, 2, 3.0f, 3, 4.0f, null);
        Collection<Integer> values = map.values();
        Verify.assertSize(4, values);
        map.remove(1.0f);
        Verify.assertSize(3, values);
        map.put(5.0f, 5);
        Verify.assertSize(4, values);

        HashBiMap<Float, Integer> map1 = HashBiMap.newMap();
        Collection<Integer> keySet1 = map1.values();
        Verify.assertSize(0, keySet1);
        map1.put(1.0f, null);
        Verify.assertSize(1, keySet1);
    }

    @Test
    public void iterator()
    {
        MutableSet<String> expected = UnifiedSet.newSetWith("zero", "thirtyOne", null);
        MutableSet<String> actual = UnifiedSet.newSet();

        Iterator<String> iterator = HashBiMap.newWithKeysValues(0.0f, "zero", 31.0f, "thirtyOne", 32.0f, null).iterator();
        Assertions.assertTrue(iterator.hasNext());
        actual.add(iterator.next());
        Assertions.assertTrue(iterator.hasNext());
        actual.add(iterator.next());
        Assertions.assertTrue(iterator.hasNext());
        actual.add(iterator.next());
        Assertions.assertFalse(iterator.hasNext());

        Assertions.assertEquals(expected, actual);
        Verify.assertThrows(NoSuchElementException.class, (Runnable) iterator::next);

        MutableBiMap<Float, String> map1 = this.newMapWithKeysValues(0.0f, "zero", 1.0f, null);
        Iterator<String> iterator1 = map1.iterator();
        Verify.assertThrows(IllegalStateException.class, iterator1::remove);
        iterator1.next();
        iterator1.remove();
        Assertions.assertTrue(HashBiMap.newWithKeysValues(0.0f, "zero").equals(map1)
                || HashBiMap.newWithKeysValues(1.0f, null).equals(map1), map1.toString());
        Assertions.assertTrue(HashBiMap.newWithKeysValues(0.0f, "zero").inverse().equals(map1.inverse())
                || HashBiMap.newWithKeysValues(1.0f, null).inverse().equals(map1.inverse()), map1.toString());
        iterator1.next();
        iterator1.remove();
        Assertions.assertEquals(HashBiMap.newMap(), map1);
        Verify.assertThrows(IllegalStateException.class, iterator1::remove);

        MutableBiMap<Float, String> map2 = this.newMapWithKeysValues(0.0f, null, 9.0f, "nine");
        Iterator<String> iterator2 = map2.iterator();
        Verify.assertThrows(IllegalStateException.class, iterator2::remove);
        iterator2.next();
        iterator2.remove();
        Assertions.assertTrue(HashBiMap.newWithKeysValues(0.0f, null).equals(map2)
                || HashBiMap.newWithKeysValues(9.0f, "nine").equals(map2), map2.toString());
        Assertions.assertTrue(HashBiMap.newWithKeysValues(0.0f, null).inverse().equals(map2.inverse())
                || HashBiMap.newWithKeysValues(9.0f, "nine").inverse().equals(map2.inverse()), map2.toString());
        iterator2.next();
        iterator2.remove();
        Assertions.assertEquals(HashBiMap.newMap(), map2);

        MutableBiMap<Float, String> map3 = this.newMapWithKeysValues(8.0f, "eight", 9.0f, null);
        Iterator<String> iterator3 = map3.iterator();
        Verify.assertThrows(IllegalStateException.class, iterator3::remove);
        iterator3.next();
        iterator3.remove();
        Assertions.assertTrue(HashBiMap.newWithKeysValues(8.0f, "eight").equals(map3)
                || HashBiMap.newWithKeysValues(9.0f, null).equals(map3), map3.toString());
        iterator3.next();
        iterator3.remove();
        Assertions.assertEquals(HashBiMap.newMap(), map3);
    }

    @Test
    public void values()
    {
        MutableBiMap<Float, Integer> map = this.newMapWithKeysValues(1.0f, 1, 2.0f, 2, 3.0f, 3);
        Verify.assertContainsAll(map.values(), 1, 2, 3);
    }

    @Test
    public void removeFromValues()
    {
        MutableBiMap<Float, Integer> map = this.newMapWithKeysValues(1.0f, 1, 2.0f, 2, 3.0f, 3);
        Assertions.assertFalse(map.values().remove(4));

        Assertions.assertTrue(map.values().remove(2));
        Assertions.assertEquals(HashBiMap.newWithKeysValues(1.0f, 1, 3.0f, 3), map);
        Assertions.assertEquals(HashBiMap.newWithKeysValues(1.0f, 1, 3.0f, 3).inverse(), map.inverse());
    }

    @Test
    public void removeNullFromValues()
    {
        MutableBiMap<Float, Integer> map = this.newMapWithKeysValues(1.0f, 1, 2.0f, 2, 3.0f, 3);
        Assertions.assertFalse(map.values().remove(null));
        Assertions.assertEquals(HashBiMap.newWithKeysValues(1.0f, 1, 2.0f, 2, 3.0f, 3), map);
        Assertions.assertEquals(HashBiMap.newWithKeysValues(1.0f, 1, 2.0f, 2, 3.0f, 3).inverse(), map.inverse());
        map.put(4.0f, null);
        Assertions.assertTrue(map.values().remove(null));
        Assertions.assertEquals(HashBiMap.newWithKeysValues(1.0f, 1, 2.0f, 2, 3.0f, 3), map);
        Assertions.assertEquals(HashBiMap.newWithKeysValues(1.0f, 1, 2.0f, 2, 3.0f, 3).inverse(), map.inverse());
    }

    @Test
    public void removeAllFromValues()
    {
        MutableBiMap<Float, Integer> map = this.newMapWithKeysValues(1.0f, 1, 2.0f, 2, 3.0f, 3);
        Assertions.assertFalse(map.values().removeAll(FastList.newListWith(4)));

        Assertions.assertTrue(map.values().removeAll(FastList.newListWith(2, 4)));
        Assertions.assertEquals(HashBiMap.newWithKeysValues(1.0f, 1, 3.0f, 3), map);
        Assertions.assertEquals(HashBiMap.newWithKeysValues(1.0f, 1, 3.0f, 3).inverse(), map.inverse());
    }

    @Test
    public void retainAllFromValues()
    {
        MutableBiMap<Float, Integer> map = this.newMapWithKeysValues(1.0f, 1, 2.0f, 2, 3.0f, 3);
        Assertions.assertFalse(map.values().retainAll(FastList.newListWith(1, 2, 3, 4)));

        Assertions.assertTrue(map.values().retainAll(FastList.newListWith(1, 3)));
        Assertions.assertEquals(HashBiMap.newWithKeysValues(1.0f, 1, 3.0f, 3), map);
        Assertions.assertEquals(HashBiMap.newWithKeysValues(1.0f, 1, 3.0f, 3).inverse(), map.inverse());
    }

    @Test
    public void valuesToArray()
    {
        MutableBiMap<Float, Integer> map = this.newMapWithKeysValues(1.0f, 1, 2.0f, 2, 3.0f, null);
        HashBag<Integer> expected = HashBag.newBagWith(1, 2, null);
        Collection<Integer> values = map.values();
        Assertions.assertEquals(expected, HashBag.newBagWith(values.toArray()));
        Assertions.assertEquals(expected, HashBag.newBagWith(values.toArray(new Integer[values.size()])));
        Assertions.assertEquals(expected, HashBag.newBagWith(values.toArray(new Integer[0])));
        expected.add(null);
        Assertions.assertEquals(expected, HashBag.newBagWith(values.toArray(new Integer[values.size() + 1])));
    }
}
