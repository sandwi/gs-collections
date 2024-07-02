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

import com.gs.collections.api.block.function.primitive.BooleanFunction;
import com.gs.collections.api.block.function.primitive.BooleanFunction0;
import com.gs.collections.api.iterator.MutableBooleanIterator;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.primitive.MutableObjectBooleanMap;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public abstract class AbstractMutableObjectBooleanMapTestCase extends AbstractObjectBooleanMapTestCase
{
    protected final MutableObjectBooleanMap<String> map = this.classUnderTest();

    @Override
    protected abstract MutableObjectBooleanMap<String> classUnderTest();

    @Override
    protected abstract <T> MutableObjectBooleanMap<T> newWithKeysValues(T key1, boolean value1);

    @Override
    protected abstract <T> MutableObjectBooleanMap<T> newWithKeysValues(T key1, boolean value1, T key2, boolean value2);

    @Override
    protected abstract <T> MutableObjectBooleanMap<T> newWithKeysValues(T key1, boolean value1, T key2, boolean value2, T key3, boolean value3);

    @Override
    protected abstract <T> MutableObjectBooleanMap<T> newWithKeysValues(T key1, boolean value1, T key2, boolean value2, T key3, boolean value3, T key4, boolean value4);

    @Override
    protected abstract <T> MutableObjectBooleanMap<T> getEmptyMap();

    @Override
    @Test
    public void get()
    {
        super.get();
        MutableObjectBooleanMap<String> map1 = this.classUnderTest();
        map1.put("0", false);
        Assertions.assertFalse(map1.get("0"));

        map1.put("5", true);
        Assertions.assertTrue(map1.get("5"));

        map1.put(null, true);
        Assertions.assertTrue(map1.get(null));
    }

    @Override
    @Test
    public void getIfAbsent()
    {
        super.getIfAbsent();
        MutableObjectBooleanMap<String> map1 = this.classUnderTest();
        map1.removeKey("0");
        Assertions.assertTrue(map1.getIfAbsent("0", true));
        Assertions.assertFalse(map1.getIfAbsent("0", false));

        map1.put("0", false);
        Assertions.assertFalse(map1.getIfAbsent("0", true));

        map1.put("5", true);
        Assertions.assertTrue(map1.getIfAbsent("5", false));

        map1.put(null, false);
        Assertions.assertFalse(map1.getIfAbsent(null, true));
    }

    @Override
    @Test
    public void getOrThrow()
    {
        super.getOrThrow();
        MutableObjectBooleanMap<String> map1 = this.classUnderTest();
        map1.removeKey("0");
        Verify.assertThrows(IllegalStateException.class, () -> map1.getOrThrow("0"));
        map1.put("0", false);
        Assertions.assertFalse(map1.getOrThrow("0"));

        map1.put("5", true);
        Assertions.assertTrue(map1.getOrThrow("5"));

        map1.put(null, false);
        Assertions.assertFalse(map1.getOrThrow(null));
    }

    @Override
    @Test
    public void containsKey()
    {
        super.containsKey();
        MutableObjectBooleanMap<String> map1 = this.classUnderTest();
        map1.removeKey("0");
        Assertions.assertFalse(map1.containsKey("0"));
        Assertions.assertFalse(map1.get("0"));
        map1.removeKey("0");
        Assertions.assertFalse(map1.containsKey("0"));
        Assertions.assertFalse(map1.get("0"));

        map1.removeKey("1");
        Assertions.assertFalse(map1.containsKey("1"));
        Assertions.assertFalse(map1.get("1"));

        map1.removeKey("2");
        Assertions.assertFalse(map1.containsKey("2"));
        Assertions.assertFalse(map1.get("2"));

        map1.removeKey("3");
        Assertions.assertFalse(map1.containsKey("3"));
        Assertions.assertFalse(map1.get("3"));
        map1.put(null, true);
        Assertions.assertTrue(map1.containsKey(null));
        map1.removeKey(null);
        Assertions.assertFalse(map1.containsKey(null));
    }

    @Override
    @Test
    public void containsValue()
    {
        super.containsValue();
        this.classUnderTest().clear();

        this.classUnderTest().put("5", true);
        Assertions.assertTrue(this.classUnderTest().containsValue(true));

        this.classUnderTest().put(null, false);
        Assertions.assertTrue(this.classUnderTest().containsValue(false));
    }

    @Override
    @Test
    public void size()
    {
        super.size();
        MutableObjectBooleanMap<Integer> hashMap1 = this.newWithKeysValues(1, true, 0, false);
        Verify.assertSize(2, hashMap1);
        hashMap1.removeKey(1);
        Verify.assertSize(1, hashMap1);
        hashMap1.removeKey(0);
        Verify.assertSize(0, hashMap1);
    }

    @Override
    @Test
    public void contains()
    {
        super.contains();
        MutableObjectBooleanMap<String> map1 = this.classUnderTest();
        map1.clear();

        map1.put("5", true);
        Assertions.assertTrue(map1.contains(true));

        map1.put(null, false);
        Assertions.assertTrue(map1.contains(false));

        map1.removeKey("5");
        Assertions.assertFalse(map1.contains(true));
        Assertions.assertTrue(map1.contains(false));

        map1.removeKey(null);
        Assertions.assertFalse(map1.contains(false));
    }

    @Override
    @Test
    public void containsAll()
    {
        super.containsAll();
        MutableObjectBooleanMap<String> map1 = this.classUnderTest();
        map1.clear();

        map1.put("5", true);
        Assertions.assertTrue(map1.containsAll(true));
        Assertions.assertFalse(map1.containsAll(true, false));
        Assertions.assertFalse(map1.containsAll(false, false));

        map1.put(null, false);
        Assertions.assertTrue(map1.containsAll(false));
        Assertions.assertTrue(map1.containsAll(true, false));

        map1.removeKey("5");
        Assertions.assertFalse(map1.containsAll(true));
        Assertions.assertFalse(map1.containsAll(true, false));
        Assertions.assertTrue(map1.containsAll(false, false));

        map1.removeKey(null);
        Assertions.assertFalse(map1.containsAll(false, true));
    }

    @Override
    @Test
    public void containsAllIterable()
    {
        super.containsAllIterable();
        MutableObjectBooleanMap<String> map1 = this.classUnderTest();
        map1.clear();

        map1.put("5", true);
        Assertions.assertTrue(map1.containsAll(BooleanArrayList.newListWith(true)));
        Assertions.assertFalse(map1.containsAll(BooleanArrayList.newListWith(true, false)));
        Assertions.assertFalse(map1.containsAll(BooleanArrayList.newListWith(false, false)));

        map1.put(null, false);
        Assertions.assertTrue(map1.containsAll(BooleanArrayList.newListWith(false)));
        Assertions.assertTrue(map1.containsAll(BooleanArrayList.newListWith(true, false)));

        map1.removeKey("5");
        Assertions.assertFalse(map1.containsAll(BooleanArrayList.newListWith(true)));
        Assertions.assertFalse(map1.containsAll(BooleanArrayList.newListWith(true, false)));
        Assertions.assertTrue(map1.containsAll(BooleanArrayList.newListWith(false, false)));

        map1.removeKey(null);
        Assertions.assertFalse(map1.containsAll(BooleanArrayList.newListWith(false, true)));
    }

    protected static MutableList<String> generateCollisions()
    {
        MutableList<String> collisions = FastList.newList();
        ObjectBooleanHashMap<String> hashMap = new ObjectBooleanHashMap<>();
        for (int each = 3; collisions.size() <= 10; each++)
        {
            if (hashMap.spread(String.valueOf(each)) == hashMap.spread(String.valueOf(3)))
            {
                collisions.add(String.valueOf(each));
            }
        }
        return collisions;
    }

    @Test
    public void clear()
    {
        MutableObjectBooleanMap<String> hashMap = this.getEmptyMap();
        hashMap.put("0", true);
        hashMap.clear();
        Assertions.assertEquals(ObjectBooleanHashMap.newMap(), hashMap);

        hashMap.put("1", false);
        hashMap.clear();
        Assertions.assertEquals(ObjectBooleanHashMap.newMap(), hashMap);

        hashMap.put(null, true);
        hashMap.clear();
        Assertions.assertEquals(ObjectBooleanHashMap.newMap(), hashMap);
    }

    @Test
    public void removeKey()
    {
        MutableObjectBooleanMap<String> map0 = this.newWithKeysValues("0", true, "1", false);
        map0.removeKey("1");
        Assertions.assertEquals(this.newWithKeysValues("0", true), map0);
        map0.removeKey("0");
        Assertions.assertEquals(ObjectBooleanHashMap.newMap(), map0);

        MutableObjectBooleanMap<String> map1 = this.newWithKeysValues("0", false, "1", true);
        map1.removeKey("0");
        Assertions.assertEquals(this.newWithKeysValues("1", true), map1);
        map1.removeKey("1");
        Assertions.assertEquals(ObjectBooleanHashMap.newMap(), map1);

        this.map.removeKey("5");
        Assertions.assertEquals(this.newWithKeysValues("0", true, "1", true, "2", false), this.map);
        this.map.removeKey("0");
        Assertions.assertEquals(this.newWithKeysValues("1", true, "2", false), this.map);
        this.map.removeKey("1");
        Assertions.assertEquals(this.newWithKeysValues("2", false), this.map);
        this.map.removeKey("2");
        Assertions.assertEquals(ObjectBooleanHashMap.newMap(), this.map);
        this.map.removeKey("0");
        this.map.removeKey("1");
        this.map.removeKey("2");
        Assertions.assertEquals(ObjectBooleanHashMap.newMap(), this.map);
        Verify.assertEmpty(this.map);

        this.map.put(null, true);
        Assertions.assertTrue(this.map.get(null));
        this.map.removeKey(null);
        Assertions.assertFalse(this.map.get(null));
    }

    @Test
    public void put()
    {
        this.map.put("0", false);
        this.map.put("1", false);
        this.map.put("2", true);
        ObjectBooleanHashMap<String> expected = ObjectBooleanHashMap.newWithKeysValues("0", false, "1", false, "2", true);
        Assertions.assertEquals(expected, this.map);

        this.map.put("5", true);
        expected.put("5", true);
        Assertions.assertEquals(expected, this.map);

        this.map.put(null, false);
        expected.put(null, false);
        Assertions.assertEquals(expected, this.map);
    }

    @Test
    public void putDuplicateWithRemovedSlot()
    {
        String collision1 = generateCollisions().getFirst();
        String collision2 = generateCollisions().get(1);
        String collision3 = generateCollisions().get(2);
        String collision4 = generateCollisions().get(3);

        MutableObjectBooleanMap<String> hashMap = this.getEmptyMap();
        hashMap.put(collision1, true);
        hashMap.put(collision2, false);
        hashMap.put(collision3, true);
        Assertions.assertFalse(hashMap.get(collision2));
        hashMap.removeKey(collision2);
        hashMap.put(collision4, false);
        Assertions.assertEquals(this.newWithKeysValues(collision1, true, collision3, true, collision4, false), hashMap);

        MutableObjectBooleanMap<String> hashMap1 = this.getEmptyMap();
        hashMap1.put(collision1, false);
        hashMap1.put(collision2, false);
        hashMap1.put(collision3, true);
        Assertions.assertFalse(hashMap1.get(collision1));
        hashMap1.removeKey(collision1);
        hashMap1.put(collision4, true);
        Assertions.assertEquals(this.newWithKeysValues(collision2, false, collision3, true, collision4, true), hashMap1);

        MutableObjectBooleanMap<String> hashMap2 = this.getEmptyMap();
        hashMap2.put(collision1, true);
        hashMap2.put(collision2, true);
        hashMap2.put(collision3, false);
        Assertions.assertFalse(hashMap2.get(collision3));
        hashMap2.removeKey(collision3);
        hashMap2.put(collision4, false);
        Assertions.assertEquals(this.newWithKeysValues(collision1, true, collision2, true, collision4, false), hashMap2);

        MutableObjectBooleanMap<String> hashMap3 = this.getEmptyMap();
        hashMap3.put(collision1, true);
        hashMap3.put(collision2, true);
        hashMap3.put(collision3, false);
        Assertions.assertTrue(hashMap3.get(collision2));
        Assertions.assertFalse(hashMap3.get(collision3));
        hashMap3.removeKey(collision2);
        hashMap3.removeKey(collision3);
        hashMap3.put(collision4, false);
        Assertions.assertEquals(this.newWithKeysValues(collision1, true, collision4, false), hashMap3);

        MutableObjectBooleanMap<String> hashMap4 = this.getEmptyMap();
        hashMap4.put(null, false);
        Assertions.assertEquals(this.newWithKeysValues(null, false), hashMap4);
        hashMap4.put(null, true);
        Assertions.assertEquals(this.newWithKeysValues(null, true), hashMap4);
    }

    @Test
    public void getIfAbsentPut_Function()
    {
        MutableObjectBooleanMap<Integer> map1 = this.getEmptyMap();
        Assertions.assertTrue(map1.getIfAbsentPut(0, () -> true));
        BooleanFunction0 factoryThrows = () -> { throw new AssertionError(); };
        Assertions.assertTrue(map1.getIfAbsentPut(0, factoryThrows));
        Assertions.assertEquals(this.newWithKeysValues(0, true), map1);
        Assertions.assertTrue(map1.getIfAbsentPut(1, () -> true));
        Assertions.assertTrue(map1.getIfAbsentPut(1, factoryThrows));
        Assertions.assertEquals(this.newWithKeysValues(0, true, 1, true), map1);

        MutableObjectBooleanMap<Integer> map2 = this.getEmptyMap();
        Assertions.assertFalse(map2.getIfAbsentPut(1, () -> false));
        Assertions.assertFalse(map2.getIfAbsentPut(1, factoryThrows));
        Assertions.assertEquals(this.newWithKeysValues(1, false), map2);
        Assertions.assertFalse(map2.getIfAbsentPut(0, () -> false));
        Assertions.assertFalse(map2.getIfAbsentPut(0, factoryThrows));
        Assertions.assertEquals(this.newWithKeysValues(0, false, 1, false), map2);

        MutableObjectBooleanMap<Integer> map3 = this.getEmptyMap();
        Assertions.assertTrue(map3.getIfAbsentPut(null, () -> true));
        Assertions.assertTrue(map3.getIfAbsentPut(null, factoryThrows));
        Assertions.assertEquals(this.newWithKeysValues(null, true), map3);
    }

    @Test
    public void getIfAbsentPutWith()
    {
        BooleanFunction<String> functionLengthEven = string -> (string.length() & 1) == 0;

        MutableObjectBooleanMap<Integer> map1 = this.getEmptyMap();
        Assertions.assertFalse(map1.getIfAbsentPutWith(0, functionLengthEven, "123456789"));
        BooleanFunction<String> functionThrows = string -> { throw new AssertionError(); };
        Assertions.assertFalse(map1.getIfAbsentPutWith(0, functionThrows, "unused"));
        Assertions.assertEquals(this.newWithKeysValues(0, false), map1);
        Assertions.assertFalse(map1.getIfAbsentPutWith(1, functionLengthEven, "123456789"));
        Assertions.assertFalse(map1.getIfAbsentPutWith(1, functionThrows, "unused"));
        Assertions.assertEquals(this.newWithKeysValues(0, false, 1, false), map1);

        MutableObjectBooleanMap<Integer> map2 = this.getEmptyMap();
        Assertions.assertTrue(map2.getIfAbsentPutWith(1, functionLengthEven, "1234567890"));
        Assertions.assertTrue(map2.getIfAbsentPutWith(1, functionThrows, "unused0"));
        Assertions.assertEquals(this.newWithKeysValues(1, true), map2);
        Assertions.assertTrue(map2.getIfAbsentPutWith(0, functionLengthEven, "1234567890"));
        Assertions.assertTrue(map2.getIfAbsentPutWith(0, functionThrows, "unused0"));
        Assertions.assertEquals(this.newWithKeysValues(0, true, 1, true), map2);

        MutableObjectBooleanMap<Integer> map3 = this.getEmptyMap();
        Assertions.assertFalse(map3.getIfAbsentPutWith(null, functionLengthEven, "123456789"));
        Assertions.assertFalse(map3.getIfAbsentPutWith(null, functionThrows, "unused"));
        Assertions.assertEquals(this.newWithKeysValues(null, false), map3);
    }

    @Test
    public void getIfAbsentPutWithKey()
    {
        BooleanFunction<Integer> function = anObject -> anObject == null || (anObject & 1) == 0;

        MutableObjectBooleanMap<Integer> map1 = this.getEmptyMap();
        Assertions.assertTrue(map1.getIfAbsentPutWithKey(0, function));
        BooleanFunction<Integer> functionThrows = anObject -> { throw new AssertionError(); };
        Assertions.assertTrue(map1.getIfAbsentPutWithKey(0, functionThrows));
        Assertions.assertEquals(this.newWithKeysValues(0, true), map1);
        Assertions.assertFalse(map1.getIfAbsentPutWithKey(1, function));
        Assertions.assertFalse(map1.getIfAbsentPutWithKey(1, functionThrows));
        Assertions.assertEquals(this.newWithKeysValues(0, true, 1, false), map1);

        MutableObjectBooleanMap<Integer> map2 = this.getEmptyMap();
        Assertions.assertFalse(map2.getIfAbsentPutWithKey(1, function));
        Assertions.assertFalse(map2.getIfAbsentPutWithKey(1, functionThrows));
        Assertions.assertEquals(this.newWithKeysValues(1, false), map2);
        Assertions.assertTrue(map2.getIfAbsentPutWithKey(0, function));
        Assertions.assertTrue(map2.getIfAbsentPutWithKey(0, functionThrows));
        Assertions.assertEquals(this.newWithKeysValues(0, true, 1, false), map2);

        MutableObjectBooleanMap<Integer> map3 = this.getEmptyMap();
        Assertions.assertTrue(map3.getIfAbsentPutWithKey(null, function));
        Assertions.assertTrue(map3.getIfAbsentPutWithKey(null, functionThrows));
        Assertions.assertEquals(this.newWithKeysValues(null, true), map3);
    }

    @Test
    public void withKeysValues()
    {
        MutableObjectBooleanMap<Integer> emptyMap = this.getEmptyMap();
        MutableObjectBooleanMap<Integer> hashMap = emptyMap.withKeyValue(1, true);
        Assertions.assertEquals(this.newWithKeysValues(1, true), hashMap);
        Assertions.assertSame(emptyMap, hashMap);
    }

    @Test
    public void withoutKey()
    {
        MutableObjectBooleanMap<Integer> hashMap = this.newWithKeysValues(1, true, 2, true, 3, false, 4, false);
        MutableObjectBooleanMap<Integer> actual = hashMap.withoutKey(5);
        Assertions.assertSame(hashMap, actual);
        Assertions.assertEquals(this.newWithKeysValues(1, true, 2, true, 3, false, 4, false), actual);
        Assertions.assertEquals(this.newWithKeysValues(1, true, 2, true, 3, false), hashMap.withoutKey(4));
        Assertions.assertEquals(this.newWithKeysValues(1, true, 2, true), hashMap.withoutKey(3));
        Assertions.assertEquals(this.newWithKeysValues(1, true), hashMap.withoutKey(2));
        Assertions.assertEquals(ObjectBooleanHashMap.newMap(), hashMap.withoutKey(1));
        Assertions.assertEquals(ObjectBooleanHashMap.newMap(), hashMap.withoutKey(1));
    }

    @Test
    public void withoutAllKeys()
    {
        MutableObjectBooleanMap<Integer> hashMap = this.newWithKeysValues(1, true, 2, true, 3, false, 4, false);
        MutableObjectBooleanMap<Integer> actual = hashMap.withoutAllKeys(FastList.newListWith(5, 6, 7));
        Assertions.assertSame(hashMap, actual);
        Assertions.assertEquals(this.newWithKeysValues(1, true, 2, true, 3, false, 4, false), actual);
        Assertions.assertEquals(this.newWithKeysValues(1, true, 2, true), hashMap.withoutAllKeys(FastList.newListWith(5, 4, 3)));
        Assertions.assertEquals(this.newWithKeysValues(1, true), hashMap.withoutAllKeys(FastList.newListWith(2)));
        Assertions.assertEquals(ObjectBooleanHashMap.newMap(), hashMap.withoutAllKeys(FastList.newListWith(1)));
        Assertions.assertEquals(ObjectBooleanHashMap.newMap(), hashMap.withoutAllKeys(FastList.newListWith(5, 6)));
    }

    @Test
    public void asUnmodifiable()
    {
        Verify.assertInstanceOf(UnmodifiableObjectBooleanMap.class, this.map.asUnmodifiable());
        Assertions.assertEquals(new UnmodifiableObjectBooleanMap<>(this.map), this.map.asUnmodifiable());
    }

    @Test
    public void asSynchronized()
    {
        Verify.assertInstanceOf(SynchronizedObjectBooleanMap.class, this.map.asSynchronized());
        Assertions.assertEquals(new SynchronizedObjectBooleanMap<>(this.map), this.map.asSynchronized());
    }

    @Test
    public void iterator_remove()
    {
        MutableObjectBooleanMap<String> map = this.classUnderTest();
        Verify.assertNotEmpty(map);
        MutableBooleanIterator booleanIterator = map.booleanIterator();

        while (booleanIterator.hasNext())
        {
            booleanIterator.next();
            booleanIterator.remove();
        }
        Verify.assertEmpty(map);
    }

    @Test
    public void iterator_throws_on_consecutive_invocation_of_remove()
    {
        MutableObjectBooleanMap<String> map = this.classUnderTest();
        Verify.assertNotEmpty(map);
        MutableBooleanIterator booleanIterator = map.booleanIterator();
        Assertions.assertTrue(booleanIterator.hasNext());
        booleanIterator.next();
        booleanIterator.remove();
        Verify.assertThrows(IllegalStateException.class, booleanIterator::remove);
    }

    @Test
    public void iterator_throws_on_invocation_of_remove_before_next()
    {
        MutableObjectBooleanMap<String> map = this.classUnderTest();
        MutableBooleanIterator booleanIterator = map.booleanIterator();
        Assertions.assertTrue(booleanIterator.hasNext());
        Verify.assertThrows(IllegalStateException.class, booleanIterator::remove);
    }
}
