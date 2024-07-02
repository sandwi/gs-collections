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

package com.gs.collections.impl.map.immutable.primitive;

import java.util.NoSuchElementException;

import com.gs.collections.api.iterator.BooleanIterator;
import com.gs.collections.api.map.primitive.ImmutableObjectBooleanMap;
import com.gs.collections.api.map.primitive.ObjectBooleanMap;
import com.gs.collections.impl.bag.mutable.primitive.BooleanHashBag;
import com.gs.collections.impl.block.factory.primitive.BooleanPredicates;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.map.mutable.primitive.ObjectBooleanHashMap;
import com.gs.collections.impl.set.mutable.primitive.BooleanHashSet;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * JUnit test for {@link ImmutableObjectBooleanSingletonMap}.
 */
public class ImmutableObjectBooleanSingletonMapTest extends AbstractImmutableObjectBooleanMapTestCase
{
    @Override
    protected ImmutableObjectBooleanMap<String> classUnderTest()
    {
        return ObjectBooleanHashMap.newWithKeysValues("1", true).toImmutable();
    }

    @Test
    public void newWithKeyValue()
    {
        ImmutableObjectBooleanMap<String> map1 = this.classUnderTest();
        ImmutableObjectBooleanMap<String> expected = ObjectBooleanHashMap.newWithKeysValues("1", true, "3", true).toImmutable();
        Assertions.assertEquals(expected, map1.newWithKeyValue("3", true));
        Assertions.assertNotSame(map1, map1.newWithKeyValue("3", true));
        Assertions.assertEquals(this.classUnderTest(), map1);
    }

    @Test
    public void newWithoutKeyValue()
    {
        ImmutableObjectBooleanMap<String> map1 = this.classUnderTest();
        ImmutableObjectBooleanMap<String> expected1 = this.newWithKeysValues("1", true);
        Assertions.assertEquals(expected1, map1.newWithoutKey("2"));
        Assertions.assertEquals(this.classUnderTest(), map1);

        ImmutableObjectBooleanMap<String> expected2 = this.getEmptyMap();
        Assertions.assertEquals(expected2, map1.newWithoutKey("1"));
        Assertions.assertNotSame(map1, map1.newWithoutKey("1"));
        Assertions.assertEquals(this.classUnderTest(), map1);
    }

    @Test
    public void newWithoutAllKeys()
    {
        ImmutableObjectBooleanMap<String> map1 = this.classUnderTest();
        ImmutableObjectBooleanMap<String> expected1 = this.newWithKeysValues("1", true);
        Assertions.assertEquals(expected1, map1.newWithoutAllKeys(FastList.newListWith("2", "3")));
        Assertions.assertNotSame(map1, map1.newWithoutAllKeys(FastList.newListWith("2", "3")));
        Assertions.assertEquals(this.classUnderTest(), map1);

        ImmutableObjectBooleanMap<String> expected2 = this.getEmptyMap();
        Assertions.assertEquals(expected2, map1.newWithoutAllKeys(FastList.newListWith("1", "3")));
        Assertions.assertNotSame(map1, map1.newWithoutAllKeys(FastList.newListWith("1", "3")));
        Assertions.assertEquals(this.classUnderTest(), map1);
    }

    @Override
    @Test
    public void containsKey()
    {
        Assertions.assertFalse(this.classUnderTest().containsKey("0"));
        Assertions.assertTrue(this.classUnderTest().containsKey("1"));
        Assertions.assertFalse(this.classUnderTest().containsKey("2"));
        Assertions.assertFalse(this.classUnderTest().containsKey("3"));
        Assertions.assertFalse(this.classUnderTest().containsKey(null));
    }

    @Override
    @Test
    public void containsValue()
    {
        Assertions.assertFalse(this.classUnderTest().containsValue(false));
        Assertions.assertTrue(this.classUnderTest().containsValue(true));
    }

    @Override
    @Test
    public void detectIfNone()
    {
        boolean detect = this.classUnderTest().detectIfNone(value -> true, false);
        Assertions.assertTrue(detect);

        boolean detect1 = this.classUnderTest().detectIfNone(value -> false, false);
        Assertions.assertFalse(detect1);
    }

    @Override
    @Test
    public void getIfAbsent()
    {
        Assertions.assertTrue(this.classUnderTest().getIfAbsent("0", true));
        Assertions.assertTrue(this.classUnderTest().getIfAbsent("1", false));
        Assertions.assertFalse(this.classUnderTest().getIfAbsent("2", false));
        Assertions.assertFalse(this.classUnderTest().getIfAbsent("5", false));
        Assertions.assertTrue(this.classUnderTest().getIfAbsent("5", true));

        Assertions.assertTrue(this.classUnderTest().getIfAbsent(null, true));
        Assertions.assertFalse(this.classUnderTest().getIfAbsent(null, false));
    }

    @Override
    @Test
    public void allSatisfy()
    {
        Assertions.assertFalse(this.classUnderTest().allSatisfy(value1 -> false));

        Assertions.assertTrue(this.classUnderTest().allSatisfy(value -> true));
    }

    @Override
    @Test
    public void reject()
    {
        Assertions.assertEquals(this.classUnderTest(), this.classUnderTest().reject((object1, value3) -> false));

        Assertions.assertEquals(this.getEmptyMap(), this.classUnderTest().reject((object, value2) -> true));

        Assertions.assertEquals(new BooleanHashBag(), this.classUnderTest().reject(value1 -> true).toBag());

        Assertions.assertEquals(BooleanHashBag.newBagWith(true), this.classUnderTest().reject(value -> false).toBag());
    }

    @Override
    @Test
    public void select()
    {
        Assertions.assertEquals(this.classUnderTest(), this.classUnderTest().select((object1, value3) -> true));

        Assertions.assertEquals(this.getEmptyMap(), this.classUnderTest().select((object, value2) -> false));

        Assertions.assertEquals(new BooleanHashBag(), this.classUnderTest().select(value1 -> false).toBag());

        Assertions.assertEquals(BooleanHashBag.newBagWith(true), this.classUnderTest().select(value -> true).toBag());
    }

    @Override
    @Test
    public void iterator()
    {
        BooleanIterator iterator = this.classUnderTest().booleanIterator();
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertTrue(iterator.next());
        Assertions.assertFalse(iterator.hasNext());
        Verify.assertThrows(NoSuchElementException.class, (Runnable) iterator::next);
    }

    @Override
    @Test
    public void anySatisfy()
    {
        Assertions.assertTrue(this.classUnderTest().anySatisfy(BooleanPredicates.isTrue()));
        Assertions.assertFalse(this.classUnderTest().anySatisfy(BooleanPredicates.isFalse()));
        Assertions.assertTrue(this.classUnderTest().anySatisfy(BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())));
        Assertions.assertFalse(this.classUnderTest().anySatisfy(BooleanPredicates.and(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())));
    }

    @Override
    @Test
    public void contains()
    {
        Assertions.assertFalse(this.classUnderTest().contains(false));
        Assertions.assertTrue(this.classUnderTest().contains(true));
    }

    @Override
    @Test
    public void getOrThrow()
    {
        Assertions.assertTrue(this.classUnderTest().getOrThrow("1"));
        Verify.assertThrows(IllegalStateException.class, () -> this.classUnderTest().getOrThrow("5"));
        Verify.assertThrows(IllegalStateException.class, () -> this.classUnderTest().getOrThrow("0"));
        Verify.assertThrows(IllegalStateException.class, () -> this.classUnderTest().getOrThrow(null));
    }

    @Override
    @Test
    public void get()
    {
        Assertions.assertFalse(this.classUnderTest().get("0"));
        Assertions.assertTrue(this.classUnderTest().get("1"));
        Assertions.assertFalse(this.classUnderTest().get(null));
    }

    @Override
    @Test
    public void count()
    {
        Assertions.assertEquals(1L, this.classUnderTest().count(value1 -> true));

        Assertions.assertEquals(0L, this.classUnderTest().count(value -> false));
    }

    @Override
    @Test
    public void toBag()
    {
        Assertions.assertEquals(BooleanHashBag.newBagWith(true), this.classUnderTest().toBag());
    }

    @Override
    @Test
    public void toSet()
    {
        Assertions.assertEquals(BooleanHashSet.newSetWith(true), this.classUnderTest().toSet());
    }

    @Override
    @Test
    public void containsAll()
    {
        Assertions.assertFalse(this.classUnderTest().containsAll(false, false));
        Assertions.assertFalse(this.classUnderTest().containsAll(true, false));
        Assertions.assertTrue(this.classUnderTest().containsAll(true));
        Assertions.assertTrue(this.classUnderTest().containsAll());
    }

    @Override
    @Test
    public void containsAllIterable()
    {
        Assertions.assertFalse(this.classUnderTest().containsAll(BooleanArrayList.newListWith(false, false)));
        Assertions.assertFalse(this.classUnderTest().containsAll(BooleanArrayList.newListWith(true, false)));
        Assertions.assertTrue(this.classUnderTest().containsAll(BooleanArrayList.newListWith(true)));
        Assertions.assertTrue(this.classUnderTest().containsAll(new BooleanArrayList()));
    }

    @Override
    @Test
    public void testEquals()
    {
        ObjectBooleanMap<String> map1 = this.newWithKeysValues("1", true);
        ObjectBooleanMap<String> map2 = this.newWithKeysValues("0", false);
        ObjectBooleanMap<String> map3 = this.newWithKeysValues("0", false, "1", true);

        Assertions.assertNotEquals(this.classUnderTest(), map3);
        Assertions.assertNotEquals(this.classUnderTest(), map2);
        Verify.assertEqualsAndHashCode(this.classUnderTest(), map1);
        Verify.assertPostSerializedEqualsAndHashCode(this.classUnderTest());
    }

    @Override
    @Test
    public void isEmpty()
    {
        Verify.assertNotEmpty(this.classUnderTest());
    }

    @Override
    @Test
    public void notEmpty()
    {
        Assertions.assertTrue(this.classUnderTest().notEmpty());
    }

    @Override
    @Test
    public void noneSatisfy()
    {
        Assertions.assertFalse(this.classUnderTest().noneSatisfy(value1 -> true));

        Assertions.assertTrue(this.classUnderTest().noneSatisfy(value -> false));
    }
}
