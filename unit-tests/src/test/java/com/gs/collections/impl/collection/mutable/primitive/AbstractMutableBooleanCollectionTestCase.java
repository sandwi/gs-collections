/*
 * Copyright 2013 Goldman Sachs.
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

package com.gs.collections.impl.collection.mutable.primitive;

import java.util.NoSuchElementException;

import com.gs.collections.api.collection.primitive.MutableBooleanCollection;
import com.gs.collections.api.iterator.MutableBooleanIterator;
import com.gs.collections.impl.bag.mutable.primitive.BooleanHashBag;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Abstract JUnit test for {@link MutableBooleanCollection}s.
 */
public abstract class AbstractMutableBooleanCollectionTestCase extends AbstractBooleanIterableTestCase
{
    @Override
    protected abstract MutableBooleanCollection classUnderTest();

    @Override
    protected abstract MutableBooleanCollection newWith(boolean... elements);

    @Override
    protected abstract MutableBooleanCollection newMutableCollectionWith(boolean... elements);

    @Test
    public void clear()
    {
        MutableBooleanCollection collection = this.classUnderTest();
        collection.clear();
        Verify.assertSize(0, collection);
        Verify.assertEmpty(collection);
        Assertions.assertFalse(collection.contains(true));
        Assertions.assertFalse(collection.contains(false));

        MutableBooleanCollection collection0 = this.newWith();
        MutableBooleanCollection collection1 = this.newWith(false);
        MutableBooleanCollection collection2 = this.newWith(true);
        MutableBooleanCollection collection3 = this.newWith(true, false);
        MutableBooleanCollection collection4 = this.newWith(true, false, true, false, true);
        collection0.clear();
        collection1.clear();
        collection2.clear();
        collection3.clear();
        collection4.clear();
        Verify.assertEmpty(collection0);
        Verify.assertEmpty(collection1);
        Verify.assertEmpty(collection2);
        Verify.assertEmpty(collection3);
        Verify.assertEmpty(collection4);
        Verify.assertSize(0, collection0);
        Verify.assertSize(0, collection1);
        Verify.assertSize(0, collection2);
        Verify.assertSize(0, collection3);
        Verify.assertSize(0, collection4);
        Assertions.assertFalse(collection1.contains(false));
        Assertions.assertFalse(collection2.contains(true));
        Assertions.assertFalse(collection3.contains(true));
        Assertions.assertFalse(collection3.contains(false));
        Assertions.assertFalse(collection4.contains(false));
        Assertions.assertEquals(this.newMutableCollectionWith(), collection0);
        Assertions.assertEquals(this.newMutableCollectionWith(), collection1);
        Assertions.assertEquals(this.newMutableCollectionWith(), collection2);
        Assertions.assertEquals(this.newMutableCollectionWith(), collection3);
        Assertions.assertEquals(this.newMutableCollectionWith(), collection4);
    }

    @Override
    @Test
    public void testEquals()
    {
        super.testEquals();
        Verify.assertPostSerializedEqualsAndHashCode(this.newWith());
    }

    @Override
    @Test
    public void containsAllArray()
    {
        super.containsAllArray();
        MutableBooleanCollection emptyCollection = this.newWith();
        Assertions.assertFalse(emptyCollection.containsAll(true));
        Assertions.assertFalse(emptyCollection.containsAll(false));
        Assertions.assertFalse(emptyCollection.containsAll(false, true, false));
        emptyCollection.add(false);
        Assertions.assertFalse(emptyCollection.containsAll(true));
        Assertions.assertTrue(emptyCollection.containsAll(false));
    }

    @Override
    @Test
    public void containsAllIterable()
    {
        super.containsAllIterable();
        MutableBooleanCollection emptyCollection = this.newWith();
        Assertions.assertTrue(emptyCollection.containsAll(new BooleanArrayList()));
        Assertions.assertFalse(emptyCollection.containsAll(BooleanArrayList.newListWith(true)));
        Assertions.assertFalse(emptyCollection.containsAll(BooleanArrayList.newListWith(false)));
        emptyCollection.add(false);
        Assertions.assertFalse(emptyCollection.containsAll(BooleanArrayList.newListWith(true)));
        Assertions.assertTrue(emptyCollection.containsAll(BooleanArrayList.newListWith(false)));
    }

    @Test
    public void add()
    {
        MutableBooleanCollection emptyCollection = this.newWith();
        Assertions.assertTrue(emptyCollection.add(true));
        Assertions.assertEquals(this.newMutableCollectionWith(true), emptyCollection);
        Assertions.assertTrue(emptyCollection.add(false));
        Assertions.assertEquals(this.newMutableCollectionWith(true, false), emptyCollection);
        Assertions.assertTrue(emptyCollection.add(true));
        Assertions.assertEquals(this.newMutableCollectionWith(true, false, true), emptyCollection);
        Assertions.assertTrue(emptyCollection.add(false));
        Assertions.assertEquals(this.newMutableCollectionWith(true, false, true, false), emptyCollection);
        MutableBooleanCollection collection = this.classUnderTest();
        Assertions.assertTrue(collection.add(false));
        Assertions.assertEquals(this.newMutableCollectionWith(true, false, true, false), collection);
    }

    @Test
    public void addAllArray()
    {
        MutableBooleanCollection collection = this.classUnderTest();
        Assertions.assertFalse(collection.addAll());
        Assertions.assertTrue(collection.addAll(false, true, false));
        Assertions.assertEquals(this.newMutableCollectionWith(true, false, true, false, true, false), collection);
        Assertions.assertTrue(collection.addAll(this.newMutableCollectionWith(true, false, true, false, true)));
        Assertions.assertEquals(this.newMutableCollectionWith(true, false, true, false, true, false, true, false, true, false, true), collection);
    }

    @Test
    public void addAllIterable()
    {
        MutableBooleanCollection collection = this.classUnderTest();
        Assertions.assertFalse(collection.addAll(this.newMutableCollectionWith()));
        Assertions.assertTrue(collection.addAll(this.newMutableCollectionWith(false, true, false)));
        Assertions.assertEquals(this.newMutableCollectionWith(true, false, true, false, true, false), collection);
        Assertions.assertTrue(collection.addAll(this.newMutableCollectionWith(true, false, true, false, true)));
        Assertions.assertEquals(this.newMutableCollectionWith(true, false, true, false, true, false, true, false, true, false, true), collection);

        MutableBooleanCollection emptyCollection = this.newWith();
        Assertions.assertTrue(emptyCollection.addAll(BooleanArrayList.newListWith(true, false, true, false, true)));
        Assertions.assertFalse(emptyCollection.addAll(new BooleanArrayList()));
        Assertions.assertEquals(this.newMutableCollectionWith(true, false, true, false, true), emptyCollection);
    }

    @Test
    public void remove()
    {
        MutableBooleanCollection collection = this.classUnderTest();
        Assertions.assertTrue(collection.remove(false));
        Assertions.assertEquals(this.newMutableCollectionWith(true, true), collection);
        Assertions.assertFalse(collection.remove(false));
        Assertions.assertEquals(this.newMutableCollectionWith(true, true), collection);
        Assertions.assertTrue(collection.remove(true));
        Assertions.assertEquals(this.newMutableCollectionWith(true), collection);

        MutableBooleanCollection collection1 = this.newWith();
        Assertions.assertFalse(collection1.remove(false));
        Assertions.assertEquals(this.newMutableCollectionWith(), collection1);
        Assertions.assertTrue(collection1.add(false));
        Assertions.assertTrue(collection1.add(false));
        Assertions.assertTrue(collection1.remove(false));
        Assertions.assertEquals(this.newMutableCollectionWith(false), collection1);
        Assertions.assertTrue(collection1.remove(false));
        Assertions.assertEquals(this.newMutableCollectionWith(), collection1);

        MutableBooleanCollection collection2 = this.newWith();
        Assertions.assertFalse(collection2.remove(true));
        Assertions.assertEquals(this.newMutableCollectionWith(), collection2);
        Assertions.assertTrue(collection2.add(true));
        Assertions.assertTrue(collection2.add(true));
        Assertions.assertTrue(collection2.remove(true));
        Assertions.assertEquals(this.newMutableCollectionWith(true), collection2);
        Assertions.assertTrue(collection2.remove(true));
        Assertions.assertEquals(this.newMutableCollectionWith(), collection2);
    }

    @Test
    public void removeAll()
    {
        Assertions.assertFalse(this.newWith().removeAll(true));

        MutableBooleanCollection collection = this.classUnderTest();
        Assertions.assertFalse(collection.removeAll());
        Assertions.assertTrue(collection.removeAll(true));
        Assertions.assertEquals(this.newMutableCollectionWith(false), collection);
        Assertions.assertFalse(collection.removeAll(true));
        Assertions.assertEquals(this.newMutableCollectionWith(false), collection);
        Assertions.assertTrue(collection.removeAll(false, true));
        Assertions.assertEquals(this.newMutableCollectionWith(), collection);

        MutableBooleanCollection booleanArrayCollection = this.newWith(false, false);
        Assertions.assertFalse(booleanArrayCollection.removeAll(true));
        Assertions.assertEquals(this.newMutableCollectionWith(false, false), booleanArrayCollection);
        Assertions.assertTrue(booleanArrayCollection.removeAll(false));
        Assertions.assertEquals(this.newMutableCollectionWith(), booleanArrayCollection);
        MutableBooleanCollection collection1 = this.classUnderTest();
        Assertions.assertFalse(collection1.removeAll());
        Assertions.assertTrue(collection1.removeAll(true, false));
        Assertions.assertEquals(this.newMutableCollectionWith(), collection1);

        MutableBooleanCollection trueFalseList = this.newWith(true, false);
        Assertions.assertTrue(trueFalseList.removeAll(true));
        Assertions.assertEquals(this.newMutableCollectionWith(false), trueFalseList);

        MutableBooleanCollection collection2 = this.newWith(true, false, true, false, true);
        Assertions.assertFalse(collection2.removeAll());
        Assertions.assertTrue(collection2.removeAll(true, true));
        Assertions.assertEquals(this.newMutableCollectionWith(false, false), collection2);

        MutableBooleanCollection collection3 = this.newWith(true, false, true, false, true);
        Assertions.assertFalse(collection3.removeAll());
        Assertions.assertTrue(collection3.removeAll(true, false));
        Assertions.assertEquals(this.newMutableCollectionWith(), collection3);

        MutableBooleanCollection collection4 = this.newWith(true, false, true, false, true);
        Assertions.assertFalse(collection4.removeAll());
        Assertions.assertTrue(collection4.removeAll(false, false));
        Assertions.assertEquals(this.newMutableCollectionWith(true, true, true), collection4);
    }

    @Test
    public void removeAll_iterable()
    {
        MutableBooleanCollection collection = this.classUnderTest();
        Assertions.assertFalse(collection.removeAll(this.newMutableCollectionWith()));
        Assertions.assertTrue(collection.removeAll(this.newMutableCollectionWith(false)));
        Assertions.assertEquals(this.newMutableCollectionWith(true, true), collection);
        Assertions.assertTrue(collection.removeAll(this.newMutableCollectionWith(true, true)));
        Assertions.assertEquals(this.newMutableCollectionWith(), collection);

        MutableBooleanCollection list = this.classUnderTest();
        Assertions.assertFalse(list.removeAll(new BooleanArrayList()));
        MutableBooleanCollection booleanArrayList = this.newWith(false, false);
        Assertions.assertFalse(booleanArrayList.removeAll(new BooleanArrayList(true)));
        Assertions.assertEquals(this.newMutableCollectionWith(false, false), booleanArrayList);
        Assertions.assertTrue(booleanArrayList.removeAll(new BooleanArrayList(false)));
        Assertions.assertEquals(this.newMutableCollectionWith(), booleanArrayList);
        Assertions.assertTrue(list.removeAll(new BooleanArrayList(true)));
        Assertions.assertEquals(this.newMutableCollectionWith(false), list);
        Assertions.assertTrue(list.removeAll(BooleanArrayList.newListWith(true, false)));
        Assertions.assertEquals(this.newMutableCollectionWith(), list);
        Assertions.assertFalse(list.removeAll(BooleanArrayList.newListWith(true, false)));
        Assertions.assertEquals(this.newMutableCollectionWith(), list);

        MutableBooleanCollection list1 = this.newWith(true, false, true, true);
        Assertions.assertFalse(list1.removeAll(new BooleanArrayList()));
        Assertions.assertTrue(list1.removeAll(BooleanArrayList.newListWith(true, true)));
        Verify.assertSize(1, list1);
        Assertions.assertFalse(list1.contains(true));
        Assertions.assertEquals(this.newMutableCollectionWith(false), list1);
        Assertions.assertTrue(list1.removeAll(BooleanArrayList.newListWith(false, false)));
        Assertions.assertEquals(this.newMutableCollectionWith(), list1);

        MutableBooleanCollection list2 = this.newWith(true, false, true, false, true);
        Assertions.assertTrue(list2.removeAll(BooleanHashBag.newBagWith(true, false)));
        Assertions.assertEquals(this.newMutableCollectionWith(), list2);
    }

    @Test
    public void retainAll_iterable()
    {
        MutableBooleanCollection collection = this.classUnderTest();
        Assertions.assertFalse(collection.retainAll(true, false));
        Assertions.assertTrue(collection.retainAll(true));
        Assertions.assertEquals(this.newMutableCollectionWith(true, true), collection);
        Assertions.assertTrue(collection.retainAll(false, false));
        Assertions.assertEquals(this.newMutableCollectionWith(), collection);

        MutableBooleanCollection list = this.classUnderTest();
        Assertions.assertFalse(list.retainAll(false, false, true));
        MutableBooleanCollection booleanArrayList = this.newWith(false, false);
        Assertions.assertFalse(booleanArrayList.retainAll(false));
        Assertions.assertEquals(this.newMutableCollectionWith(false, false), booleanArrayList);
        Assertions.assertTrue(booleanArrayList.retainAll(true));
        Assertions.assertEquals(this.newMutableCollectionWith(), booleanArrayList);
        Assertions.assertTrue(list.retainAll(false));
        Assertions.assertEquals(this.newMutableCollectionWith(false), list);
        Assertions.assertTrue(list.retainAll());
        Assertions.assertEquals(this.newMutableCollectionWith(), list);
        Assertions.assertFalse(list.retainAll(true, false));
        Assertions.assertEquals(this.newMutableCollectionWith(), list);

        MutableBooleanCollection list1 = this.newWith(true, false, true, true);
        Assertions.assertFalse(list1.retainAll(false, false, true));
        Assertions.assertTrue(list1.retainAll(false, false));
        Verify.assertSize(1, list1);
        Assertions.assertFalse(list1.contains(true));
        Assertions.assertEquals(this.newMutableCollectionWith(false), list1);
        Assertions.assertTrue(list1.retainAll(true, true));
        Assertions.assertEquals(this.newMutableCollectionWith(), list1);

        MutableBooleanCollection list2 = this.newWith(true, false, true, false, true);
        Assertions.assertTrue(list2.retainAll());
        Assertions.assertEquals(this.newMutableCollectionWith(), list2);
    }

    @Test
    public void retainAll()
    {
        MutableBooleanCollection collection = this.classUnderTest();
        Assertions.assertFalse(collection.retainAll(this.newMutableCollectionWith(true, false)));
        Assertions.assertTrue(collection.retainAll(this.newMutableCollectionWith(true)));
        Assertions.assertEquals(this.newMutableCollectionWith(true, true), collection);
        Assertions.assertTrue(collection.retainAll(this.newMutableCollectionWith(false, false)));
        Assertions.assertEquals(this.newMutableCollectionWith(), collection);

        MutableBooleanCollection list = this.classUnderTest();
        Assertions.assertFalse(list.retainAll(BooleanArrayList.newListWith(false, false, true)));
        MutableBooleanCollection booleanArrayList = this.newWith(false, false);
        Assertions.assertFalse(booleanArrayList.retainAll(new BooleanArrayList(false)));
        Assertions.assertEquals(this.newMutableCollectionWith(false, false), booleanArrayList);
        Assertions.assertTrue(booleanArrayList.retainAll(new BooleanArrayList(true)));
        Assertions.assertEquals(this.newMutableCollectionWith(), booleanArrayList);
        Assertions.assertTrue(list.retainAll(new BooleanArrayList(false)));
        Assertions.assertEquals(this.newMutableCollectionWith(false), list);
        Assertions.assertTrue(list.retainAll(new BooleanArrayList()));
        Assertions.assertEquals(this.newMutableCollectionWith(), list);
        Assertions.assertFalse(list.retainAll(BooleanArrayList.newListWith(true, false)));
        Assertions.assertEquals(this.newMutableCollectionWith(), list);

        MutableBooleanCollection list1 = this.newWith(true, false, true, true);
        Assertions.assertFalse(list1.retainAll(BooleanArrayList.newListWith(false, false, true)));
        Assertions.assertTrue(list1.retainAll(BooleanArrayList.newListWith(false, false)));
        Verify.assertSize(1, list1);
        Assertions.assertFalse(list1.contains(true));
        Assertions.assertEquals(this.newMutableCollectionWith(false), list1);
        Assertions.assertTrue(list1.retainAll(BooleanArrayList.newListWith(true, true)));
        Assertions.assertEquals(this.newMutableCollectionWith(), list1);

        MutableBooleanCollection list2 = this.newWith(true, false, true, false, true);
        Assertions.assertTrue(list2.retainAll(new BooleanHashBag()));
        Assertions.assertEquals(this.newMutableCollectionWith(), list2);
    }

    @Test
    public void with()
    {
        MutableBooleanCollection emptyCollection = this.newWith();
        MutableBooleanCollection collection = emptyCollection.with(true);
        MutableBooleanCollection collection0 = this.newWith().with(true).with(false);
        MutableBooleanCollection collection1 = this.newWith().with(true).with(false).with(true);
        MutableBooleanCollection collection2 = this.newWith().with(true).with(false).with(true).with(false);
        MutableBooleanCollection collection3 = this.newWith().with(true).with(false).with(true).with(false).with(true);
        Assertions.assertSame(emptyCollection, collection);
        Assertions.assertEquals(this.newMutableCollectionWith(true), collection);
        Assertions.assertEquals(this.newMutableCollectionWith(true, false), collection0);
        Assertions.assertEquals(this.newMutableCollectionWith(true, false, true), collection1);
        Assertions.assertEquals(this.newMutableCollectionWith(true, false, true, false), collection2);
        Assertions.assertEquals(this.newMutableCollectionWith(true, false, true, false, true), collection3);
    }

    @Test
    public void withAll()
    {
        MutableBooleanCollection emptyCollection = this.newWith();
        MutableBooleanCollection collection = emptyCollection.withAll(this.newMutableCollectionWith(true));
        MutableBooleanCollection collection0 = this.newWith().withAll(this.newMutableCollectionWith(true, false));
        MutableBooleanCollection collection1 = this.newWith().withAll(this.newMutableCollectionWith(true, false, true));
        MutableBooleanCollection collection2 = this.newWith().withAll(this.newMutableCollectionWith(true, false, true, false));
        MutableBooleanCollection collection3 = this.newWith().withAll(this.newMutableCollectionWith(true, false, true, false, true));
        Assertions.assertSame(emptyCollection, collection);
        Assertions.assertEquals(this.newMutableCollectionWith(true), collection);
        Assertions.assertEquals(this.newMutableCollectionWith(true, false), collection0);
        Assertions.assertEquals(this.classUnderTest(), collection1);
        Assertions.assertEquals(this.newMutableCollectionWith(true, false, true, false), collection2);
        Assertions.assertEquals(this.newMutableCollectionWith(true, false, true, false, true), collection3);
    }

    @Test
    public void without()
    {
        MutableBooleanCollection collection = this.newWith(true, false, true, false, true);
        Assertions.assertEquals(this.newMutableCollectionWith(true, true, false, true), collection.without(false));
        Assertions.assertEquals(this.newMutableCollectionWith(true, false, true), collection.without(true));
        Assertions.assertEquals(this.newMutableCollectionWith(true, true), collection.without(false));
        Assertions.assertEquals(this.newMutableCollectionWith(true), collection.without(true));
        Assertions.assertEquals(this.newMutableCollectionWith(true), collection.without(false));
        Assertions.assertEquals(this.newMutableCollectionWith(), collection.without(true));
        Assertions.assertEquals(this.newMutableCollectionWith(), collection.without(false));

        MutableBooleanCollection collection1 = this.newWith(true, false, true, false, true);
        Assertions.assertSame(collection1, collection1.without(false));
        Assertions.assertEquals(this.newMutableCollectionWith(true, true, false, true), collection1);
    }

    @Test
    public void withoutAll()
    {
        MutableBooleanCollection mainCollection = this.newWith(true, false, true, false, true);
        Assertions.assertEquals(this.newMutableCollectionWith(true, true, true), mainCollection.withoutAll(this.newMutableCollectionWith(false, false)));

        MutableBooleanCollection collection = this.newWith(true, false, true, false, true);
        Assertions.assertEquals(this.newMutableCollectionWith(true, true, true), collection.withoutAll(BooleanHashBag.newBagWith(false)));
        Assertions.assertEquals(this.newMutableCollectionWith(), collection.withoutAll(BooleanHashBag.newBagWith(true, false)));
        Assertions.assertEquals(this.newMutableCollectionWith(), collection.withoutAll(BooleanHashBag.newBagWith(true, false)));

        MutableBooleanCollection trueCollection = this.newWith(true, true, true);
        Assertions.assertEquals(this.newMutableCollectionWith(true, true, true), trueCollection.withoutAll(BooleanArrayList.newListWith(false)));
        MutableBooleanCollection mutableBooleanCollection = trueCollection.withoutAll(BooleanArrayList.newListWith(true));
        Assertions.assertEquals(this.newMutableCollectionWith(), mutableBooleanCollection);
        Assertions.assertSame(trueCollection, mutableBooleanCollection);
    }

    @Test
    public void asSynchronized()
    {
        MutableBooleanCollection collection = this.classUnderTest();
        Verify.assertInstanceOf(this.newWith(true, false, true).asSynchronized().getClass(), collection.asSynchronized());
        Assertions.assertEquals(this.newWith(true, false, true).asSynchronized(), collection.asSynchronized());
        Assertions.assertEquals(collection, collection.asSynchronized());
    }

    @Test
    public void asUnmodifiable()
    {
        Verify.assertInstanceOf(this.newWith(true, false, true).asUnmodifiable().getClass(), this.classUnderTest().asUnmodifiable());
        Assertions.assertEquals(this.newWith(true, false, true).asUnmodifiable(), this.classUnderTest().asUnmodifiable());
        Assertions.assertEquals(this.classUnderTest(), this.classUnderTest().asUnmodifiable());
    }

    @Test
    public void booleanIterator_with_remove()
    {
        MutableBooleanCollection booleanIterable = this.classUnderTest();
        MutableBooleanIterator iterator = booleanIterable.booleanIterator();
        int iterationCount = booleanIterable.size();
        int iterableSize = booleanIterable.size();
        for (int i = 0; i < iterationCount; i++)
        {
            Verify.assertSize(iterableSize--, booleanIterable);
            Assertions.assertTrue(iterator.hasNext());
            iterator.next();
            iterator.remove();
            Verify.assertSize(iterableSize, booleanIterable);
        }
        Verify.assertEmpty(booleanIterable);
        Assertions.assertFalse(iterator.hasNext());
        Verify.assertThrows(NoSuchElementException.class, (Runnable) iterator::next);
    }

    @Test
    public void iterator_throws_on_invocation_of_remove_before_next()
    {
        MutableBooleanCollection booleanIterable = this.newWith(true, false);
        MutableBooleanIterator iterator = booleanIterable.booleanIterator();
        Assertions.assertTrue(iterator.hasNext());
        Verify.assertThrows(IllegalStateException.class, iterator::remove);
    }

    @Test
    public void iterator_throws_on_consecutive_invocation_of_remove()
    {
        MutableBooleanCollection booleanIterable = this.newWith(true, false);
        MutableBooleanIterator iterator = booleanIterable.booleanIterator();
        iterator.next();
        iterator.remove();
        Verify.assertThrows(IllegalStateException.class, iterator::remove);
    }
}
