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

package com.gs.collections.impl.map.mutable.primitive;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import com.gs.collections.api.map.primitive.MutableObjectBooleanMap;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class ObjectBooleanHashMapKeySetTestCase
{
    public abstract MutableObjectBooleanMap<String> newMapWithKeysValues(String key1, boolean value1);

    public abstract MutableObjectBooleanMap<String> newMapWithKeysValues(String key1, boolean value1, String key2, boolean value2);

    public abstract MutableObjectBooleanMap<String> newMapWithKeysValues(String key1, boolean value1, String key2, boolean value2, String key3, boolean value3);

    public abstract MutableObjectBooleanMap<String> newMapWithKeysValues(String key1, boolean value1, String key2, boolean value2, String key3, boolean value3, String key4, boolean value4);

    public abstract MutableObjectBooleanMap<String> newEmptyMap();

    @Test
    public void add()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.newMapWithKeysValues("One", true, "Two", false, "Three", true).keySet().add("Four");
        });
    }

    @Test
    public void addAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.newMapWithKeysValues("One", true, "Two", false, "Three", true).keySet().addAll(FastList.newListWith("Four"));
        });
    }

    @Test
    public void contains()
    {
        MutableObjectBooleanMap<String> map = this.newMapWithKeysValues("One", true, "Two", false, "Three", true, null, false);
        Set<String> keySet = map.keySet();
        Assertions.assertTrue(keySet.contains("One"));
        Assertions.assertTrue(keySet.contains("Two"));
        Assertions.assertTrue(keySet.contains("Three"));
        Assertions.assertFalse(keySet.contains("Four"));
        Assertions.assertTrue(keySet.contains(null));
        keySet.remove(null);
        Assertions.assertFalse(keySet.contains(null));
        map.removeKey("One");
        Assertions.assertFalse(keySet.contains("One"));
    }

    @Test
    public void containsAll()
    {
        MutableObjectBooleanMap<String> map = this.newMapWithKeysValues("One", true, "Two", false, "Three", true, null, false);
        Set<String> keySet = map.keySet();
        Assertions.assertTrue(keySet.containsAll(FastList.newListWith("One", "Two")));
        Assertions.assertTrue(keySet.containsAll(FastList.newListWith("One", "Two", "Three", null)));
        Assertions.assertTrue(keySet.containsAll(FastList.newListWith(null, null)));
        Assertions.assertFalse(keySet.containsAll(FastList.newListWith("One", "Four")));
        Assertions.assertFalse(keySet.containsAll(FastList.newListWith("Five", "Four")));
        keySet.remove(null);
        Assertions.assertFalse(keySet.containsAll(FastList.newListWith("One", "Two", "Three", null)));
        Assertions.assertTrue(keySet.containsAll(FastList.newListWith("One", "Two", "Three")));
        map.removeKey("One");
        Assertions.assertFalse(keySet.containsAll(FastList.newListWith("One", "Two")));
        Assertions.assertTrue(keySet.containsAll(FastList.newListWith("Three", "Two")));
    }

    @Test
    public void isEmpty()
    {
        MutableObjectBooleanMap<String> map = this.newMapWithKeysValues("One", true, "Two", false, "Three", true, null, false);
        Set<String> keySet = map.keySet();
        Assertions.assertFalse(keySet.isEmpty());
        MutableObjectBooleanMap<String> map1 = this.newEmptyMap();
        Set<String> keySet1 = map1.keySet();
        Assertions.assertTrue(keySet1.isEmpty());
        map1.put("One", true);
        Assertions.assertFalse(keySet1.isEmpty());
    }

    @Test
    public void size()
    {
        MutableObjectBooleanMap<String> map = this.newMapWithKeysValues("One", true, "Two", false, "Three", true, null, false);
        Set<String> keySet = map.keySet();
        Verify.assertSize(4, keySet);
        map.remove("One");
        Verify.assertSize(3, keySet);
        map.put("Five", true);
        Verify.assertSize(4, keySet);

        MutableObjectBooleanMap<String> map1 = this.newEmptyMap();
        Set<String> keySet1 = map1.keySet();
        Verify.assertSize(0, keySet1);
        map1.put(null, true);
        Verify.assertSize(1, keySet1);
    }

    @Test
    public void iterator()
    {
        MutableObjectBooleanMap<String> map = this.newMapWithKeysValues("One", true, "Two", false, "Three", true, null, false);
        Set<String> keySet = map.keySet();
        Iterator<String> iterator = keySet.iterator();

        HashBag<String> expected = HashBag.newBagWith("One", "Two", "Three", null);
        HashBag<String> actual = HashBag.newBag();
        Verify.assertThrows(IllegalStateException.class, iterator::remove);
        for (int i = 0; i < 4; i++)
        {
            Assertions.assertTrue(iterator.hasNext());
            actual.add(iterator.next());
        }
        Assertions.assertFalse(iterator.hasNext());
        Verify.assertThrows(NoSuchElementException.class, (Runnable) iterator::next);
        Assertions.assertEquals(expected, actual);

        Iterator<String> iterator1 = keySet.iterator();
        for (int i = 4; i > 0; i--)
        {
            Assertions.assertTrue(iterator1.hasNext());
            iterator1.next();
            iterator1.remove();
            Verify.assertThrows(IllegalStateException.class, iterator1::remove);
            Verify.assertSize(i - 1, keySet);
            Verify.assertSize(i - 1, map);
        }

        Assertions.assertFalse(iterator1.hasNext());
        Verify.assertEmpty(map);
        Verify.assertEmpty(keySet);
    }

    @Test
    public void removeFromKeySet()
    {
        MutableObjectBooleanMap<String> map = this.newMapWithKeysValues("One", true, "Two", false, "Three", true);
        Assertions.assertFalse(map.keySet().remove("Four"));

        Assertions.assertTrue(map.keySet().remove("Two"));
        Assertions.assertEquals(this.newMapWithKeysValues("One", true, "Three", true), map);
        Assertions.assertEquals(UnifiedSet.newSetWith("One", "Three"), map.keySet());
    }

    @Test
    public void removeNullFromKeySet()
    {
        MutableObjectBooleanMap<String> map = this.newMapWithKeysValues("One", true, "Two", false, "Three", true);
        Assertions.assertFalse(map.keySet().remove(null));
        Assertions.assertEquals(this.newMapWithKeysValues("One", true, "Two", false, "Three", true), map);
        Assertions.assertEquals(UnifiedSet.newSetWith("One", "Two", "Three"), map.keySet());

        map.put(null, true);
        Assertions.assertEquals(UnifiedSet.newSetWith("One", "Two", "Three", null), map.keySet());
        Assertions.assertTrue(map.keySet().remove(null));
        Assertions.assertEquals(this.newMapWithKeysValues("One", true, "Two", false, "Three", true), map);
        Assertions.assertEquals(UnifiedSet.newSetWith("One", "Two", "Three"), map.keySet());
    }

    @Test
    public void removeAllFromKeySet()
    {
        MutableObjectBooleanMap<String> map = this.newMapWithKeysValues("One", true, "Two", false, "Three", true);
        Assertions.assertFalse(map.keySet().removeAll(FastList.newListWith("Four")));
        Assertions.assertEquals(UnifiedSet.newSetWith("One", "Two", "Three"), map.keySet());

        Assertions.assertTrue(map.keySet().removeAll(FastList.newListWith("Two", "Four")));
        Assertions.assertEquals(this.newMapWithKeysValues("One", true, "Three", true), map);
        Assertions.assertEquals(UnifiedSet.newSetWith("One", "Three"), map.keySet());
    }

    @Test
    public void retainAllFromKeySet()
    {
        MutableObjectBooleanMap<String> map = this.newMapWithKeysValues("One", true, "Two", false, "Three", true);
        Assertions.assertFalse(map.keySet().retainAll(FastList.newListWith("One", "Two", "Three", "Four")));
        Assertions.assertEquals(UnifiedSet.newSetWith("One", "Two", "Three"), map.keySet());

        Assertions.assertTrue(map.keySet().retainAll(FastList.newListWith("One", "Three")));
        Assertions.assertEquals(this.newMapWithKeysValues("One", true, "Three", true), map);
        Assertions.assertEquals(UnifiedSet.newSetWith("One", "Three"), map.keySet());
    }

    @Test
    public void clearKeySet()
    {
        MutableObjectBooleanMap<String> map = this.newMapWithKeysValues("One", true, "Two", false, "Three", true);
        map.keySet().clear();
        Verify.assertEmpty(map);
        Verify.assertEmpty(map.keySet());
    }

    @Test
    public void keySetEqualsAndHashCode()
    {
        MutableObjectBooleanMap<String> map = this.newMapWithKeysValues("One", true, "Two", false, "Three", true, null, false);
        Verify.assertEqualsAndHashCode(UnifiedSet.newSetWith("One", "Two", "Three", null), map.keySet());
        Assertions.assertNotEquals(UnifiedSet.newSetWith("One", "Two", "Three"), map.keySet());
        Assertions.assertNotEquals(FastList.newListWith("One", "Two", "Three", null), map.keySet());
    }

    @Test
    public void keySetToArray()
    {
        MutableObjectBooleanMap<String> map = this.newMapWithKeysValues("One", true, "Two", false, "Three", true);
        HashBag<String> expected = HashBag.newBagWith("One", "Two", "Three");
        Set<String> keySet = map.keySet();
        Assertions.assertEquals(expected, HashBag.newBagWith(keySet.toArray()));
        Assertions.assertEquals(expected, HashBag.newBagWith(keySet.toArray(new String[keySet.size()])));
        Assertions.assertEquals(expected, HashBag.newBagWith(keySet.toArray(new String[0])));
        expected.add(null);
        Assertions.assertEquals(expected, HashBag.newBagWith(keySet.toArray(new String[keySet.size() + 1])));
    }
}
