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
import com.gs.collections.api.iterator.MutableBooleanIterator;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UnmodifiableObjectBooleanMapTest extends AbstractMutableObjectBooleanMapTestCase
{
    private final UnmodifiableObjectBooleanMap<String> map = this.classUnderTest();

    @Override
    protected UnmodifiableObjectBooleanMap<String> classUnderTest()
    {
        return new UnmodifiableObjectBooleanMap<>(ObjectBooleanHashMap.newWithKeysValues("0", true, "1", true, "2", false));
    }

    @Override
    protected <T> UnmodifiableObjectBooleanMap<T> newWithKeysValues(T key1, boolean value1)
    {
        return new UnmodifiableObjectBooleanMap<>(ObjectBooleanHashMap.newWithKeysValues(key1, value1));
    }

    @Override
    protected <T> UnmodifiableObjectBooleanMap<T> newWithKeysValues(T key1, boolean value1, T key2, boolean value2)
    {
        return new UnmodifiableObjectBooleanMap<>(ObjectBooleanHashMap.newWithKeysValues(key1, value1, key2, value2));
    }

    @Override
    protected <T> UnmodifiableObjectBooleanMap<T> newWithKeysValues(T key1, boolean value1, T key2, boolean value2, T key3, boolean value3)
    {
        return new UnmodifiableObjectBooleanMap<>(ObjectBooleanHashMap.newWithKeysValues(key1, value1, key2, value2, key3, value3));
    }

    @Override
    protected <T> UnmodifiableObjectBooleanMap<T> newWithKeysValues(T key1, boolean value1, T key2, boolean value2, T key3, boolean value3, T key4, boolean value4)
    {
        return new UnmodifiableObjectBooleanMap<>(ObjectBooleanHashMap.newWithKeysValues(key1, value1, key2, value2, key3, value3, key4, value4));
    }

    @Override
    protected <T> UnmodifiableObjectBooleanMap<T> getEmptyMap()
    {
        return new UnmodifiableObjectBooleanMap<>(new ObjectBooleanHashMap<>());
    }

    @Override
    @Test
    public void clear()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.map.clear();
        });
    }

    @Override
    @Test
    public void removeKey()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.map.removeKey("0");
        });
    }

    @Override
    @Test
    public void put()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.map.put("0", true);
        });
    }

    @Override
    @Test
    public void withKeysValues()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.map.withKeyValue("1", true);
        });
    }

    @Override
    @Test
    public void withoutKey()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.map.withoutKey("0");
        });
    }

    @Override
    @Test
    public void withoutAllKeys()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.map.withoutAllKeys(FastList.newListWith("0", "1"));
        });
    }

    @Override
    @Test
    public void putDuplicateWithRemovedSlot()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            String collision1 = AbstractMutableObjectBooleanMapTestCase.generateCollisions().getFirst();
            this.getEmptyMap().put(collision1, true);
        });
    }

    @Override
    @Test
    public void get()
    {
        Assertions.assertTrue(this.map.get("0"));
        Assertions.assertTrue(this.map.get("1"));
        Assertions.assertFalse(this.map.get("2"));

        Assertions.assertFalse(this.map.get("5"));
    }

    @Override
    @Test
    public void getIfAbsent()
    {
        Assertions.assertTrue(this.map.getIfAbsent("0", false));
        Assertions.assertTrue(this.map.getIfAbsent("1", false));
        Assertions.assertFalse(this.map.getIfAbsent("2", true));

        Assertions.assertTrue(this.map.getIfAbsent("33", true));
        Assertions.assertFalse(this.map.getIfAbsent("33", false));
    }

    @Override
    @Test
    public void getIfAbsentPut_Function()
    {
        Assertions.assertTrue(this.map.getIfAbsentPut("0", () -> false));
    }

    @Test
    public void getIfAbsentPut_FunctionThrowsException()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.map.getIfAbsentPut("10", () -> false);
        });
    }

    @Override
    @Test
    public void getIfAbsentPutWith()
    {
        BooleanFunction<String> functionLengthEven = string -> (string.length() & 1) == 0;

        Assertions.assertTrue(this.map.getIfAbsentPutWith("0", functionLengthEven, "zeroValue"));
    }

    @Test
    public void getIfAbsentPutWithThrowsException()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            BooleanFunction<String> functionLengthEven = string -> (string.length() & 1) == 0;

            this.map.getIfAbsentPutWith("10", functionLengthEven, "zeroValue");
        });
    }

    @Override
    @Test
    public void getIfAbsentPutWithKey()
    {
        BooleanFunction<Integer> function = anObject -> anObject == null || (anObject & 1) == 0;

        Assertions.assertTrue(this.newWithKeysValues(0, true).getIfAbsentPutWithKey(0, function));
    }

    @Test
    public void getIfAbsentPutWithKeyThrowsException()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            BooleanFunction<Integer> function = anObject -> anObject == null || (anObject & 1) == 0;

            this.<Integer>getEmptyMap().getIfAbsentPutWithKey(10, function);
        });
    }

    @Override
    @Test
    public void getOrThrow()
    {
        Assertions.assertTrue(this.map.getOrThrow("0"));
        Assertions.assertTrue(this.map.getOrThrow("1"));
        Assertions.assertFalse(this.map.getOrThrow("2"));

        Verify.assertThrows(IllegalStateException.class, () -> this.map.getOrThrow("5"));
        Verify.assertThrows(IllegalStateException.class, () -> this.map.getOrThrow(null));
    }

    @Override
    @Test
    public void contains()
    {
        Assertions.assertTrue(this.map.contains(true));
        Assertions.assertTrue(this.map.contains(false));
        Assertions.assertFalse(this.getEmptyMap().contains(false));
        Assertions.assertFalse(this.newWithKeysValues("0", true).contains(false));
    }

    @Override
    @Test
    public void containsAllIterable()
    {
        Assertions.assertTrue(this.map.containsAll(BooleanArrayList.newListWith(true, false)));
        Assertions.assertTrue(this.map.containsAll(BooleanArrayList.newListWith(true, true)));
        Assertions.assertTrue(this.map.containsAll(BooleanArrayList.newListWith(false, false)));
        Assertions.assertFalse(this.getEmptyMap().containsAll(BooleanArrayList.newListWith(false, true)));
        Assertions.assertFalse(this.newWithKeysValues("0", true).containsAll(BooleanArrayList.newListWith(false)));
    }

    @Override
    @Test
    public void containsAll()
    {
        Assertions.assertTrue(this.map.containsAll(true, false));
        Assertions.assertTrue(this.map.containsAll(true, true));
        Assertions.assertTrue(this.map.containsAll(false, false));
        Assertions.assertFalse(this.getEmptyMap().containsAll(false, true));
        Assertions.assertFalse(this.newWithKeysValues("0", true).containsAll(false));
    }

    @Override
    @Test
    public void containsKey()
    {
        Assertions.assertTrue(this.map.containsKey("0"));
        Assertions.assertTrue(this.map.containsKey("1"));
        Assertions.assertTrue(this.map.containsKey("2"));
        Assertions.assertFalse(this.map.containsKey("3"));
        Assertions.assertFalse(this.map.containsKey(null));
    }

    @Override
    @Test
    public void containsValue()
    {
        Assertions.assertTrue(this.map.containsValue(true));
        Assertions.assertTrue(this.map.containsValue(false));
        Assertions.assertFalse(this.getEmptyMap().contains(true));
        Assertions.assertFalse(this.newWithKeysValues("0", false).contains(true));
    }

    @Override
    @Test
    public void size()
    {
        Verify.assertSize(0, this.getEmptyMap());
        Verify.assertSize(1, this.newWithKeysValues(0, false));
        Verify.assertSize(1, this.newWithKeysValues(1, true));
        Verify.assertSize(1, this.newWithKeysValues(null, false));

        Verify.assertSize(2, this.newWithKeysValues(1, false, 5, false));
        Verify.assertSize(2, this.newWithKeysValues(0, true, 5, true));
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        super.asUnmodifiable();
        Assertions.assertSame(this.map, this.map.asUnmodifiable());
    }

    @Override
    @Test
    public void iterator_remove()
    {
        UnmodifiableObjectBooleanMap<String> map = this.classUnderTest();
        Verify.assertNotEmpty(map);
        MutableBooleanIterator booleanIterator = map.booleanIterator();
        Assertions.assertTrue(booleanIterator.hasNext());
        booleanIterator.next();
        Verify.assertThrows(UnsupportedOperationException.class, booleanIterator::remove);
    }

    @Override
    @Test
    public void iterator_throws_on_invocation_of_remove_before_next()
    {
        UnmodifiableObjectBooleanMap<String> map = this.classUnderTest();
        MutableBooleanIterator booleanIterator = map.booleanIterator();
        Assertions.assertTrue(booleanIterator.hasNext());
        Verify.assertThrows(UnsupportedOperationException.class, booleanIterator::remove);
    }

    @Override
    @Test
    public void iterator_throws_on_consecutive_invocation_of_remove()
    {
        // Not applicable for Unmodifiable*
    }
}
