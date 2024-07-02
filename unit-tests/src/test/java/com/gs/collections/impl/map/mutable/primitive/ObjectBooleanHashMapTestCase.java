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

import java.lang.reflect.Field;
import java.util.BitSet;
import java.util.Iterator;

import com.gs.collections.api.block.function.primitive.BooleanFunction;
import com.gs.collections.api.block.function.primitive.BooleanFunction0;
import com.gs.collections.api.block.function.primitive.BooleanToBooleanFunction;
import com.gs.collections.api.map.primitive.MutableObjectBooleanMap;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class ObjectBooleanHashMapTestCase extends AbstractMutableObjectBooleanMapTestCase
{
    private final MutableObjectBooleanMap<String> map = this.classUnderTest();

    private final Class<?> targetClass = this.getTargetClass();

    protected abstract Class<?> getTargetClass();

    protected abstract <T> MutableObjectBooleanMap<T> newMapWithInitialCapacity(int size);

    @Test
    public void defaultInitialCapacity() throws Exception
    {
        Field keys = this.targetClass.getDeclaredField("keys");
        keys.setAccessible(true);
        Field values = this.targetClass.getDeclaredField("values");
        values.setAccessible(true);

        MutableObjectBooleanMap<String> hashMap = this.getEmptyMap();
        Assertions.assertEquals(16L, ((Object[]) keys.get(hashMap)).length);
        Assertions.assertEquals(64L, ((BitSet) values.get(hashMap)).size());
    }

    @Test
    public void newWithInitialCapacity() throws Exception
    {
        Field keys = this.targetClass.getDeclaredField("keys");
        keys.setAccessible(true);
        Field values = this.targetClass.getDeclaredField("values");
        values.setAccessible(true);

        MutableObjectBooleanMap<String> hashMap = this.newMapWithInitialCapacity(3);
        Assertions.assertEquals(8L, ((Object[]) keys.get(hashMap)).length);
        Assertions.assertEquals(64L, ((BitSet) values.get(hashMap)).size());

        MutableObjectBooleanMap<String> hashMap2 = this.newMapWithInitialCapacity(15);
        Assertions.assertEquals(32L, ((Object[]) keys.get(hashMap2)).length);
        Assertions.assertEquals(64L, ((BitSet) values.get(hashMap)).size());
    }

    @Test
    public void newWithInitialCapacity_negative_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            this.newMapWithInitialCapacity(-1);
        });
    }

    @Test
    public void newMap() throws Exception
    {
        Field keys = this.targetClass.getDeclaredField("keys");
        keys.setAccessible(true);
        Field values = this.targetClass.getDeclaredField("values");
        values.setAccessible(true);

        MutableObjectBooleanMap<String> hashMap = this.getEmptyMap();
        Assertions.assertEquals(16L, ((Object[]) keys.get(hashMap)).length);
        Assertions.assertEquals(64L, ((BitSet) values.get(hashMap)).size());
        Assertions.assertEquals(this.getEmptyMap(), hashMap);
    }

    @Test
    public void removeKeyIfAbsent()
    {
        MutableObjectBooleanMap<String> map0 = this.newWithKeysValues("0", false, "1", true);
        Assertions.assertTrue(map0.removeKeyIfAbsent("1", false));
        Assertions.assertEquals(this.newWithKeysValues("0", false), map0);
        Assertions.assertFalse(map0.removeKeyIfAbsent("0", true));
        Assertions.assertEquals(this.getEmptyMap(), map0);
        Assertions.assertFalse(map0.removeKeyIfAbsent("1", false));
        Assertions.assertTrue(map0.removeKeyIfAbsent("0", true));

        MutableObjectBooleanMap<String> map1 = this.newWithKeysValues("0", true, "1", false);
        Assertions.assertTrue(map1.removeKeyIfAbsent("0", false));
        Assertions.assertEquals(this.newWithKeysValues("1", false), map1);
        Assertions.assertFalse(map1.removeKeyIfAbsent("1", true));
        Assertions.assertEquals(this.getEmptyMap(), map1);
        Assertions.assertFalse(map1.removeKeyIfAbsent("0", false));
        Assertions.assertTrue(map1.removeKeyIfAbsent("1", true));

        Assertions.assertTrue(this.map.removeKeyIfAbsent("5", true));
        Assertions.assertEquals(this.newWithKeysValues("0", true, "1", true, "2", false), this.map);
        Assertions.assertTrue(this.map.removeKeyIfAbsent("0", false));
        Assertions.assertEquals(this.newWithKeysValues("1", true, "2", false), this.map);
        Assertions.assertTrue(this.map.removeKeyIfAbsent("1", false));
        Assertions.assertEquals(this.newWithKeysValues("2", false), this.map);
        Assertions.assertFalse(this.map.removeKeyIfAbsent("2", true));
        Assertions.assertEquals(this.getEmptyMap(), this.map);
        Assertions.assertFalse(this.map.removeKeyIfAbsent("0", false));
        Assertions.assertFalse(this.map.removeKeyIfAbsent("1", false));
        Assertions.assertTrue(this.map.removeKeyIfAbsent("2", true));
        Assertions.assertEquals(this.getEmptyMap(), this.map);
        Verify.assertEmpty(this.map);

        this.map.put(null, true);

        Assertions.assertTrue(this.map.get(null));
        Assertions.assertTrue(this.map.removeKeyIfAbsent(null, false));
        Assertions.assertFalse(this.map.get(null));
    }

    @Test
    public void putWithRehash() throws Exception
    {
        ObjectBooleanHashMap<Integer> hashMap = ObjectBooleanHashMap.newMap();
        for (int i = 2; i < 10; i++)
        {
            Assertions.assertFalse(hashMap.containsKey(i));
            hashMap.put(i, (i & 1) == 0);
        }

        Field keys = ObjectBooleanHashMap.class.getDeclaredField("keys");
        Field values = ObjectBooleanHashMap.class.getDeclaredField("values");
        keys.setAccessible(true);
        values.setAccessible(true);
        Assertions.assertEquals(16L, ((Object[]) keys.get(hashMap)).length);
        Assertions.assertEquals(64L, ((BitSet) values.get(hashMap)).size());
        Verify.assertSize(8, hashMap);
        for (int i = 2; i < 10; i++)
        {
            Assertions.assertTrue(hashMap.containsKey(i));
        }

        Assertions.assertTrue(hashMap.containsValue(false));
        Assertions.assertTrue(hashMap.containsValue(true));
        hashMap.put(10, true);
        Assertions.assertEquals(32L, ((Object[]) keys.get(hashMap)).length);
        Assertions.assertEquals(64L, ((BitSet) values.get(hashMap)).size());

        for (int i = 11; i < 75; i++)
        {
            Assertions.assertFalse(hashMap.containsKey(i), String.valueOf(i));
            hashMap.put(i, (i & 1) == 0);
        }
        Assertions.assertEquals(256L, ((Object[]) keys.get(hashMap)).length);
        Assertions.assertEquals(256L, ((BitSet) values.get(hashMap)).size());
    }

    @Test
    public void getIfAbsentPut()
    {
        ObjectBooleanHashMap<Integer> map1 = ObjectBooleanHashMap.newMap();
        Assertions.assertTrue(map1.getIfAbsentPut(0, true));
        Assertions.assertTrue(map1.getIfAbsentPut(0, false));
        Assertions.assertEquals(ObjectBooleanHashMap.newWithKeysValues(0, true), map1);
        Assertions.assertTrue(map1.getIfAbsentPut(1, true));
        Assertions.assertTrue(map1.getIfAbsentPut(1, false));
        Assertions.assertEquals(ObjectBooleanHashMap.newWithKeysValues(0, true, 1, true), map1);

        ObjectBooleanHashMap<Integer> map2 = ObjectBooleanHashMap.newMap();
        Assertions.assertFalse(map2.getIfAbsentPut(1, false));
        Assertions.assertFalse(map2.getIfAbsentPut(1, true));
        Assertions.assertEquals(ObjectBooleanHashMap.newWithKeysValues(1, false), map2);
        Assertions.assertFalse(map2.getIfAbsentPut(0, false));
        Assertions.assertFalse(map2.getIfAbsentPut(0, true));
        Assertions.assertEquals(ObjectBooleanHashMap.newWithKeysValues(0, false, 1, false), map2);

        ObjectBooleanHashMap<Integer> map3 = ObjectBooleanHashMap.newMap();
        Assertions.assertTrue(map3.getIfAbsentPut(null, true));
        Assertions.assertTrue(map3.getIfAbsentPut(null, false));
        Assertions.assertEquals(ObjectBooleanHashMap.newWithKeysValues(null, true), map3);
    }

    @Test
    public void updateValue()
    {
        BooleanToBooleanFunction flip = value -> !value;

        ObjectBooleanHashMap<Integer> map1 = ObjectBooleanHashMap.newMap();
        Assertions.assertTrue(map1.updateValue(0, false, flip));
        Assertions.assertEquals(ObjectBooleanHashMap.newWithKeysValues(0, true), map1);
        Assertions.assertFalse(map1.updateValue(0, false, flip));
        Assertions.assertEquals(ObjectBooleanHashMap.newWithKeysValues(0, false), map1);
        Assertions.assertFalse(map1.updateValue(1, true, flip));
        Assertions.assertEquals(ObjectBooleanHashMap.newWithKeysValues(0, false, 1, false), map1);
        Assertions.assertTrue(map1.updateValue(1, true, flip));
        Assertions.assertEquals(ObjectBooleanHashMap.newWithKeysValues(0, false, 1, true), map1);

        ObjectBooleanHashMap<Integer> map2 = ObjectBooleanHashMap.newMap();
        Assertions.assertTrue(map2.updateValue(1, false, flip));
        Assertions.assertEquals(ObjectBooleanHashMap.newWithKeysValues(1, true), map2);
        Assertions.assertFalse(map2.updateValue(1, false, flip));
        Assertions.assertEquals(ObjectBooleanHashMap.newWithKeysValues(1, false), map2);
        Assertions.assertFalse(map2.updateValue(0, true, flip));
        Assertions.assertEquals(ObjectBooleanHashMap.newWithKeysValues(0, false, 1, false), map2);
        Assertions.assertTrue(map2.updateValue(0, true, flip));
        Assertions.assertEquals(ObjectBooleanHashMap.newWithKeysValues(0, true, 1, false), map2);

        ObjectBooleanHashMap<Integer> map3 = ObjectBooleanHashMap.newMap();
        Assertions.assertFalse(map3.updateValue(null, true, flip));
        Assertions.assertEquals(ObjectBooleanHashMap.newWithKeysValues(null, false), map3);
        Assertions.assertTrue(map3.updateValue(null, true, flip));
        Assertions.assertEquals(ObjectBooleanHashMap.newWithKeysValues(null, true), map3);
    }

    @Override
    @Test
    public void withKeysValues()
    {
        super.withKeysValues();
        ObjectBooleanHashMap<Integer> hashMap0 = new ObjectBooleanHashMap<Integer>().withKeysValues(1, true, 2, false);
        ObjectBooleanHashMap<Integer> hashMap1 = new ObjectBooleanHashMap<Integer>().withKeysValues(1, false, 2, false, 3, true);
        ObjectBooleanHashMap<Integer> hashMap2 = new ObjectBooleanHashMap<Integer>().withKeysValues(1, true, 2, true, 3, false, 4, false);
        Assertions.assertEquals(ObjectBooleanHashMap.newWithKeysValues(1, true, 2, false), hashMap0);
        Assertions.assertEquals(ObjectBooleanHashMap.newWithKeysValues(1, false, 2, false, 3, true), hashMap1);
        Assertions.assertEquals(ObjectBooleanHashMap.newWithKeysValues(1, true, 2, true, 3, false, 4, false), hashMap2);
    }

    @Test
    public void injectInto()
    {
        ObjectBooleanHashMap<Integer> hashMap0 = new ObjectBooleanHashMap<Integer>().withKeysValues(1, true, 2, true, 3, false, 4, false);

        Integer total = hashMap0.injectInto(Integer.valueOf(0), (result, value) -> {
            if (value)
            {
                return result + 2;
            }

            return result;
        });

        Assertions.assertEquals(Integer.valueOf(4), total);
    }

    @Test
    public void put_every_slot()
    {
        ObjectBooleanHashMap<String> hashMap = ObjectBooleanHashMap.newMap();
        for (int each = 2; each < 100; each++)
        {
            Assertions.assertFalse(hashMap.get(String.valueOf(each)));
            hashMap.put(String.valueOf(each), each % 2 == 0);
            Assertions.assertEquals(each % 2 == 0, hashMap.get(String.valueOf(each)));
            hashMap.remove(String.valueOf(each));
            Assertions.assertFalse(hashMap.get(String.valueOf(each)));
        }
    }

    @Test
    public void remove_iterator_every_slot()
    {
        ObjectBooleanHashMap<String> hashMap = ObjectBooleanHashMap.newMap();
        for (int each = 2; each < 100; each++)
        {
            Assertions.assertFalse(hashMap.get(String.valueOf(each)));
            hashMap.put(String.valueOf(each), false);
            Iterator<String> iterator = hashMap.keySet().iterator();
            Assertions.assertTrue(iterator.hasNext());
            Assertions.assertEquals(String.valueOf(each), iterator.next());
            iterator.remove();
            Assertions.assertFalse(hashMap.get(String.valueOf(each)));
        }
    }

    @Test
    public void getIfAbsentPut_every_slot()
    {
        ObjectBooleanHashMap<String> hashMap = ObjectBooleanHashMap.newMap();
        for (int each = 2; each < 100; each++)
        {
            Assertions.assertFalse(hashMap.get(String.valueOf(each)));
            hashMap.getIfAbsentPut(String.valueOf(each), each % 2 == 0);
            Assertions.assertEquals(each % 2 == 0, hashMap.get(String.valueOf(each)));
        }
    }

    @Test
    public void getIfAbsentPutWith_every_slot()
    {
        BooleanFunction<String> functionLength = String::isEmpty;

        MutableObjectBooleanMap<String> hashMap = this.getEmptyMap();

        for (int each = 2; each < 100; each++)
        {
            Assertions.assertFalse(hashMap.get(String.valueOf(each)));
            Assertions.assertTrue(hashMap.getIfAbsentPutWith(String.valueOf(each), functionLength, ""));
            Assertions.assertTrue(hashMap.get(String.valueOf(each)));
        }
    }

    @Test
    public void getIfAbsentPutWithKey_every_slot()
    {
        BooleanFunction<Integer> function = (Integer each) -> each % 2 == 0;

        MutableObjectBooleanMap<Integer> hashMap = this.getEmptyMap();

        for (int each = 2; each < 100; each++)
        {
            Assertions.assertFalse(hashMap.get(each));
            Assertions.assertEquals(each % 2 == 0, hashMap.getIfAbsentPutWithKey(each, function));
            Assertions.assertEquals(each % 2 == 0, hashMap.get(each));
        }
    }

    @Test
    public void getIfAbsentPut_Function_every_slot()
    {
        BooleanFunction0 factory = () -> true;

        MutableObjectBooleanMap<String> hashMap = this.getEmptyMap();

        for (int each = 2; each < 100; each++)
        {
            Assertions.assertFalse(hashMap.get(String.valueOf(each)));
            Assertions.assertTrue(hashMap.getIfAbsentPut(String.valueOf(each), factory));
            Assertions.assertTrue(hashMap.get(String.valueOf(each)));
        }
    }

    @Test
    public void updateValue_every_slot()
    {
        BooleanToBooleanFunction function = (boolean value) -> !value;

        ObjectBooleanHashMap<String> hashMap = new ObjectBooleanHashMap<>();

        for (int each = 2; each < 100; each++)
        {
            Assertions.assertFalse(hashMap.get(String.valueOf(each)));
            Assertions.assertEquals(each % 2 != 0, hashMap.updateValue(String.valueOf(each), each % 2 == 0, function));
            Assertions.assertEquals(each % 2 != 0, hashMap.get(String.valueOf(each)));
        }
    }
}
