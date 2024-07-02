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

import java.util.NoSuchElementException;

import com.gs.collections.api.BooleanIterable;
import com.gs.collections.api.block.function.primitive.BooleanToObjectFunction;
import com.gs.collections.api.collection.primitive.MutableBooleanCollection;
import com.gs.collections.api.iterator.BooleanIterator;
import com.gs.collections.api.iterator.MutableBooleanIterator;
import com.gs.collections.impl.block.factory.primitive.BooleanPredicates;
import com.gs.collections.impl.collection.mutable.primitive.AbstractMutableBooleanCollectionTestCase;
import com.gs.collections.impl.collection.mutable.primitive.SynchronizedBooleanCollection;
import com.gs.collections.impl.collection.mutable.primitive.UnmodifiableBooleanCollection;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;


public abstract class ObjectBooleanHashMapValuesTestCase  extends AbstractMutableBooleanCollectionTestCase
{
    @Override
    @Test
    public void booleanIterator()
    {
        MutableBooleanCollection bag = this.newWith(true, false, true, true);
        BooleanArrayList list = BooleanArrayList.newListWith(true, false, true, true);
        BooleanIterator iterator1 = bag.booleanIterator();
        for (int i = 0; i < 4; i++)
        {
            Assertions.assertTrue(iterator1.hasNext());
            Assertions.assertTrue(list.remove(iterator1.next()));
        }
        Verify.assertEmpty(list);
        Assertions.assertFalse(iterator1.hasNext());

        Verify.assertThrows(NoSuchElementException.class, (Runnable) iterator1::next);

        ObjectBooleanHashMap<String> map2 = new ObjectBooleanHashMap<>();
        for (int each = 2; each < 100; each++)
        {
            map2.put(String.valueOf(each), each % 2 == 0);
        }
        MutableBooleanIterator iterator2 = map2.booleanIterator();
        while (iterator2.hasNext())
        {
            iterator2.next();
            iterator2.remove();
        }
        Assertions.assertTrue(map2.isEmpty());
    }

    @Override
    @Test
    public void addAllIterable()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.classUnderTest().addAll(new BooleanArrayList());
        });
    }

    @Override
    @Test
    public void add()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.classUnderTest().add(true);
        });
    }

    @Override
    @Test
    public void addAllArray()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.classUnderTest().addAll(true, false);
        });
    }

    @Override
    @Test
    public void with()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.classUnderTest().with(false);
        });
    }

    @Override
    @Test
    public void without()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.classUnderTest().without(true);
        });
    }

    @Override
    @Test
    public void withAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.classUnderTest().withAll(new BooleanArrayList());
        });
    }

    @Override
    @Test
    public void withoutAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.classUnderTest().withoutAll(new BooleanArrayList());
        });
    }

    @Override
    @Test
    public void remove()
    {
        ObjectBooleanHashMap<Integer> map = ObjectBooleanHashMap.newWithKeysValues(1, true, 2, false, 3, true);
        MutableBooleanCollection collection = map.values();
        Assertions.assertTrue(collection.remove(false));
        Assertions.assertFalse(collection.contains(false));
        Assertions.assertTrue(collection.contains(true));
        Assertions.assertFalse(map.contains(false));
        Assertions.assertTrue(map.contains(true));
    }

    @Override
    @Test
    public void removeAll()
    {
        Assertions.assertFalse(this.newWith().removeAll());

        ObjectBooleanHashMap<Integer> map = ObjectBooleanHashMap.newWithKeysValues(1, true, null, false);
        MutableBooleanCollection collection = map.values();
        Assertions.assertFalse(collection.removeAll());

        Assertions.assertTrue(collection.removeAll(false));
        Assertions.assertFalse(collection.contains(false));
        Assertions.assertTrue(collection.contains(true));
        Assertions.assertFalse(map.contains(false));
        Assertions.assertTrue(map.contains(true));

        Assertions.assertTrue(collection.removeAll(true));
        Assertions.assertTrue(collection.isEmpty());
        Assertions.assertFalse(collection.contains(true));
        Assertions.assertFalse(collection.contains(false));
        Assertions.assertFalse(map.contains(true));
        Assertions.assertFalse(map.contains(false));
        Assertions.assertTrue(map.isEmpty());
    }

    @Override
    @Test
    public void removeAll_iterable()
    {
        Assertions.assertFalse(this.newWith().removeAll(new BooleanArrayList()));

        ObjectBooleanHashMap<Integer> map = ObjectBooleanHashMap.newWithKeysValues(1, true, null, false);
        MutableBooleanCollection collection = map.values();
        Assertions.assertFalse(collection.removeAll());

        Assertions.assertTrue(collection.removeAll(BooleanArrayList.newListWith(false)));
        Assertions.assertFalse(collection.contains(false));
        Assertions.assertTrue(collection.contains(true));
        Assertions.assertFalse(map.contains(false));
        Assertions.assertTrue(map.contains(true));

        Assertions.assertTrue(collection.removeAll(BooleanArrayList.newListWith(true)));
        Assertions.assertTrue(collection.isEmpty());
        Assertions.assertFalse(collection.contains(true));
        Assertions.assertFalse(collection.contains(false));
        Assertions.assertFalse(map.contains(true));
        Assertions.assertFalse(map.contains(false));
        Assertions.assertTrue(map.isEmpty());

        ObjectBooleanHashMap<Integer> map1 = ObjectBooleanHashMap.newWithKeysValues(1, true, null, false);
        MutableBooleanCollection collection1 = map1.values();
        Assertions.assertTrue(collection1.removeAll(BooleanArrayList.newListWith(true, false)));
        Assertions.assertTrue(collection1.isEmpty());
        Assertions.assertFalse(collection1.contains(true));
        Assertions.assertFalse(collection.contains(false));
        Assertions.assertFalse(map1.contains(true));
        Assertions.assertFalse(map1.contains(false));
        Assertions.assertTrue(map1.isEmpty());
    }

    @Override
    @Test
    public void retainAll()
    {
        Assertions.assertFalse(this.newWith().retainAll());

        ObjectBooleanHashMap<Integer> map = ObjectBooleanHashMap.newWithKeysValues(1, true, null, false);
        MutableBooleanCollection collection = map.values();
        Assertions.assertFalse(collection.retainAll(false, true));

        Assertions.assertTrue(collection.retainAll(true));
        Assertions.assertFalse(collection.contains(false));
        Assertions.assertTrue(collection.contains(true));
        Assertions.assertFalse(map.contains(false));
        Assertions.assertTrue(map.contains(true));

        Assertions.assertTrue(collection.retainAll(false));
        Assertions.assertTrue(collection.isEmpty());
        Assertions.assertFalse(collection.contains(true));
        Assertions.assertFalse(collection.contains(false));
        Assertions.assertFalse(map.contains(true));
        Assertions.assertFalse(map.contains(false));
        Assertions.assertTrue(map.isEmpty());

        ObjectBooleanHashMap<Integer> map1 = ObjectBooleanHashMap.newWithKeysValues(1, true, null, false);
        MutableBooleanCollection collection1 = map1.values();
        Assertions.assertTrue(collection1.retainAll());
        Assertions.assertTrue(collection1.isEmpty());
        Assertions.assertFalse(collection1.contains(true));
        Assertions.assertFalse(collection.contains(false));
        Assertions.assertFalse(map1.contains(true));
        Assertions.assertFalse(map1.contains(false));
        Assertions.assertTrue(map1.isEmpty());
    }

    @Override
    @Test
    public void retainAll_iterable()
    {
        Assertions.assertFalse(this.newWith().retainAll(new BooleanArrayList()));

        ObjectBooleanHashMap<Integer> map = ObjectBooleanHashMap.newWithKeysValues(1, true, null, false);
        MutableBooleanCollection collection = map.values();
        Assertions.assertFalse(collection.retainAll(BooleanArrayList.newListWith(false, true)));

        Assertions.assertTrue(collection.retainAll(BooleanArrayList.newListWith(true)));
        Assertions.assertFalse(collection.contains(false));
        Assertions.assertTrue(collection.contains(true));
        Assertions.assertFalse(map.contains(false));
        Assertions.assertTrue(map.contains(true));

        Assertions.assertTrue(collection.retainAll(BooleanArrayList.newListWith(false)));
        Assertions.assertTrue(collection.isEmpty());
        Assertions.assertFalse(collection.contains(true));
        Assertions.assertFalse(collection.contains(false));
        Assertions.assertFalse(map.contains(true));
        Assertions.assertFalse(map.contains(false));
        Assertions.assertTrue(map.isEmpty());

        ObjectBooleanHashMap<Integer> map1 = ObjectBooleanHashMap.newWithKeysValues(1, true, null, false);
        MutableBooleanCollection collection1 = map1.values();
        Assertions.assertTrue(collection1.retainAll(new BooleanArrayList()));
        Assertions.assertTrue(collection1.isEmpty());
        Assertions.assertFalse(collection1.contains(true));
        Assertions.assertFalse(collection.contains(false));
        Assertions.assertFalse(map1.contains(true));
        Assertions.assertFalse(map1.contains(false));
        Assertions.assertTrue(map1.isEmpty());
    }

    @Override
    @Test
    public void clear()
    {
        MutableBooleanCollection emptyCollection = this.newWith();
        emptyCollection.clear();
        Verify.assertSize(0, emptyCollection);

        ObjectBooleanHashMap<Integer> map = ObjectBooleanHashMap.newWithKeysValues(1, true, 2, false, 3, true);
        MutableBooleanCollection collection = map.values();
        collection.clear();
        Verify.assertEmpty(collection);
        Verify.assertEmpty(map);
        Verify.assertSize(0, collection);
        Assertions.assertFalse(collection.contains(true));
        Assertions.assertFalse(collection.contains(false));
    }

    @Override
    @Test
    public void contains()
    {
        MutableBooleanCollection collection = this.classUnderTest();
        Assertions.assertTrue(collection.contains(true));
        Assertions.assertTrue(collection.contains(false));
        Assertions.assertTrue(collection.remove(false));
        Assertions.assertFalse(collection.contains(false));
        Assertions.assertTrue(collection.remove(true));
        Assertions.assertFalse(collection.contains(false));
        Assertions.assertTrue(collection.contains(true));
        Assertions.assertTrue(collection.remove(true));
        Assertions.assertFalse(collection.contains(false));
        Assertions.assertFalse(collection.contains(true));
    }

    @Override
    @Test
    public void containsAllArray()
    {
        MutableBooleanCollection emptyCollection = this.newWith();
        Assertions.assertFalse(emptyCollection.containsAll(true));
        Assertions.assertFalse(emptyCollection.containsAll(false));
        Assertions.assertFalse(emptyCollection.containsAll(false, true, false));

        MutableBooleanCollection collection = this.classUnderTest();
        Assertions.assertTrue(collection.containsAll());
        Assertions.assertTrue(collection.containsAll(true));
        Assertions.assertTrue(collection.containsAll(false));
        Assertions.assertTrue(collection.containsAll(false, true));
    }

    @Override
    @Test
    public void containsAllIterable()
    {
        MutableBooleanCollection emptyCollection1 = this.newWith();
        Assertions.assertTrue(emptyCollection1.containsAll(new BooleanArrayList()));
        Assertions.assertFalse(emptyCollection1.containsAll(BooleanArrayList.newListWith(true)));
        Assertions.assertFalse(emptyCollection1.containsAll(BooleanArrayList.newListWith(false)));
        MutableBooleanCollection collection = this.classUnderTest();
        Assertions.assertTrue(collection.containsAll(new BooleanArrayList()));
        Assertions.assertTrue(collection.containsAll(BooleanArrayList.newListWith(true)));
        Assertions.assertTrue(collection.containsAll(BooleanArrayList.newListWith(false)));
        Assertions.assertTrue(collection.containsAll(BooleanArrayList.newListWith(false, true)));
    }

    @Override
    @Test
    public void reject()
    {
        BooleanIterable iterable = this.classUnderTest();
        Verify.assertSize(1, iterable.reject(BooleanPredicates.isTrue()));
        Verify.assertSize(2, iterable.reject(BooleanPredicates.isFalse()));
    }

    @Override
    @Test
    public void select()
    {
        BooleanIterable iterable = this.classUnderTest();
        Verify.assertSize(1, iterable.select(BooleanPredicates.isFalse()));
        Verify.assertSize(2, iterable.select(BooleanPredicates.isTrue()));
    }

    @Override
    @Test
    public void collect()
    {
        BooleanToObjectFunction<Integer> function = parameter -> parameter ? 1 : 0;
        Assertions.assertEquals(this.newObjectCollectionWith(1, 0, 1).toBag(), this.newWith(true, false, true).collect(function).toBag());
        Assertions.assertEquals(this.newObjectCollectionWith(), this.newWith().collect(function));
    }

    @Override
    @Test
    public void appendString()
    {
        StringBuilder appendable = new StringBuilder();
        this.newWith().appendString(appendable);
        Assertions.assertEquals("", appendable.toString());
        this.newWith().appendString(appendable, "/");
        Assertions.assertEquals("", appendable.toString());
        this.newWith().appendString(appendable, "[", "/", "]");
        Assertions.assertEquals("[]", appendable.toString());
        StringBuilder appendable1 = new StringBuilder();
        this.newWith(true).appendString(appendable1);
        Assertions.assertEquals("true", appendable1.toString());
        StringBuilder appendable2 = new StringBuilder();
        BooleanIterable iterable = this.newWith(true, false);
        iterable.appendString(appendable2);
        Assertions.assertTrue("true, false".equals(appendable2.toString())
                || "false, true".equals(appendable2.toString()));
        StringBuilder appendable3 = new StringBuilder();
        iterable.appendString(appendable3, "/");
        Assertions.assertTrue("true/false".equals(appendable3.toString())
                || "false/true".equals(appendable3.toString()));
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        MutableBooleanCollection unmodifiable = this.classUnderTest().asUnmodifiable();
        Verify.assertInstanceOf(UnmodifiableBooleanCollection.class, unmodifiable);
        Assertions.assertTrue(unmodifiable.containsAll(this.classUnderTest()));
    }

    @Override
    @Test
    public void asSynchronized()
    {
        MutableBooleanCollection synch = this.classUnderTest().asSynchronized();
        Verify.assertInstanceOf(SynchronizedBooleanCollection.class, synch);
        Assertions.assertTrue(synch.containsAll(this.classUnderTest()));
    }

    @Override
    @Test
    public void testEquals()
    {
    }

    @Override
    @Test
    public void testToString()
    {
    }

    @Override
    @Test
    public void testHashCode()
    {
    }

    @Override
    @Test
    public void newCollection()
    {
    }
}
