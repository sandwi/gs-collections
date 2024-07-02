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

package com.gs.collections.impl.bag.sorted.mutable;

import java.util.Comparator;
import java.util.Iterator;

import com.gs.collections.api.bag.sorted.MutableSortedBag;
import com.gs.collections.api.collection.MutableCollection;
import com.gs.collections.api.tuple.primitive.ObjectIntPair;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.test.SerializeTestHelper;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JUnit test for {@link UnmodifiableSortedBag}.
 *
 * @since 4.2
 */
public class UnmodifiableSortedBagTest extends AbstractMutableSortedBagTestCase
{
    @Override
    protected <T> MutableSortedBag<T> newWith(T... elements)
    {
        return TreeBag.newBagWith(elements).asUnmodifiable();
    }

    @SafeVarargs
    @Override
    protected final <T> MutableSortedBag<T> newWithOccurrences(ObjectIntPair<T>... elementsWithOccurrences)
    {
        MutableSortedBag<T> bag = TreeBag.newBag();
        for (int i = 0; i < elementsWithOccurrences.length; i++)
        {
            ObjectIntPair<T> itemToAdd = elementsWithOccurrences[i];
            bag.addOccurrences(itemToAdd.getOne(), itemToAdd.getTwo());
        }
        return bag.asUnmodifiable();
    }

    @Override
    protected <T> MutableSortedBag<T> newWith(Comparator<? super T> comparator, T... elements)
    {
        return TreeBag.newBagWith(comparator, elements).asUnmodifiable();
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        super.equalsAndHashCode();
        Verify.assertInstanceOf(UnmodifiableSortedBag.class, SerializeTestHelper.serializeDeserialize(this.newWith(Comparators.reverseNaturalOrder(), 1, 2, 3)));
        Verify.assertInstanceOf(UnmodifiableSortedBag.class, SerializeTestHelper.serializeDeserialize(this.newWith(1, 2, 3)));
    }

    @Override
    @Test
    public void toString_with_collection_containing_self()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.toString_with_collection_containing_self();

            MutableCollection<Object> collection = this.newWith(1);
            collection.add(collection);
            String simpleName = collection.getClass().getSimpleName();
            String string = collection.toString();
            Assertions.assertTrue(
                    ("[1, (this " + simpleName + ")]").equals(string)
                            || ("[(this " + simpleName + "), 1]").equals(string));
        });
    }

    @Override
    @Test
    public void makeString_with_collection_containing_self()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.makeString_with_collection_containing_self();

            MutableCollection<Object> collection = this.newWith(1, 2, 3);
            collection.add(collection);
            Assertions.assertEquals(collection.toString(), '[' + collection.makeString() + ']');
        });
    }

    @Override
    @Test
    public void appendString_with_collection_containing_self()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.appendString_with_collection_containing_self();

            MutableCollection<Object> collection = this.newWith(1, 2, 3);
            collection.add(collection);
            Appendable builder = new StringBuilder();
            collection.appendString(builder);
            Assertions.assertEquals(collection.toString(), '[' + builder.toString() + ']');
        });
    }

    @Override
    @Test
    public void asSynchronized()
    {
        Verify.assertInstanceOf(SynchronizedSortedBag.class, this.newWith().asSynchronized());
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        Verify.assertInstanceOf(UnmodifiableSortedBag.class, this.newWith());
    }

    @Override
    @Test
    public void iterator()
    {
        MutableSortedBag<Integer> bag = this.newWith(-1, 0, 1, 1, 2);
        Iterator<Integer> iterator = bag.iterator();
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertEquals(Integer.valueOf(-1), iterator.next());
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertEquals(Integer.valueOf(0), iterator.next());
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertEquals(Integer.valueOf(1), iterator.next());
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertEquals(Integer.valueOf(1), iterator.next());
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertEquals(Integer.valueOf(2), iterator.next());
        Assertions.assertFalse(iterator.hasNext());

        MutableSortedBag<Integer> revBag = this.newWith(Comparators.reverseNaturalOrder(), -1, 0, 1, 1, 2);
        Iterator<Integer> revIterator = revBag.iterator();
        Assertions.assertTrue(revIterator.hasNext());
        Assertions.assertEquals(Integer.valueOf(2), revIterator.next());
        Assertions.assertTrue(revIterator.hasNext());
        Assertions.assertEquals(Integer.valueOf(1), revIterator.next());
        Assertions.assertTrue(revIterator.hasNext());
        Assertions.assertEquals(Integer.valueOf(1), revIterator.next());
        Assertions.assertTrue(revIterator.hasNext());
        Assertions.assertEquals(Integer.valueOf(0), revIterator.next());
        Assertions.assertTrue(revIterator.hasNext());
        Assertions.assertEquals(Integer.valueOf(-1), revIterator.next());
        Assertions.assertFalse(revIterator.hasNext());

        Iterator<Integer> iterator3 = this.newWith(Comparators.reverseNaturalOrder(), 2, 1, 1, 0, -1).iterator();
        Verify.assertThrows(UnsupportedOperationException.class, iterator3::remove);
        Assertions.assertEquals(Integer.valueOf(2), iterator3.next());
        Verify.assertThrows(UnsupportedOperationException.class, iterator3::remove);
    }

    @Override
    @Test
    public void iteratorRemove()
    {
        MutableSortedBag<Integer> bag = this.newWith(-1, 0, 1, 1, 2);
        Iterator<Integer> iterator = bag.iterator();
        Verify.assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Override
    @Test
    public void iteratorRemove2()
    {
    }

    @Test
    public void testAsUnmodifiable()
    {
        Verify.assertInstanceOf(UnmodifiableSortedBag.class, this.newWith().asUnmodifiable());
        MutableSortedBag<Object> bag = this.newWith();
        Assertions.assertSame(bag, bag.asUnmodifiable());
    }

    @Override
    @Test
    public void removeIfWith()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.removeIfWith();
        });
    }

    @Override
    @Test
    public void clear()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.clear();
        });
    }

    @Override
    @Test
    public void addAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.addAll();
        });
    }

    @Override
    @Test
    public void addAllIterable()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.addAllIterable();
        });
    }

    @Override
    @Test
    public void removeIf()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.removeIf();
        });
    }

    @Override
    @Test
    public void removeAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.removeAll();
        });
    }

    @Override
    @Test
    public void removeAllIterable()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.removeAllIterable();
        });
    }

    @Override
    @Test
    public void retainAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.retainAll();
        });
    }

    @Override
    @Test
    public void retainAllIterable()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.retainAllIterable();
        });
    }

    @Override
    @Test
    public void add()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.add();
        });
    }

    @Override
    @Test
    public void addOccurrences()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.addOccurrences();
        });
    }

    @Override
    @Test
    public void addOccurrences_throws()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.addOccurrences_throws();
        });
    }

    @Override
    @Test
    public void removeObject()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.removeObject();
        });
    }

    @Override
    @Test
    public void removeOccurrences()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.removeOccurrences();
        });
    }

    @Override
    @Test
    public void removeOccurrences_throws()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.removeOccurrences_throws();
        });
    }

    @Override
    @Test
    public void setOccurrences()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.setOccurrences();
        });
    }

    @Override
    @Test
    public void setOccurrences_throws()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.setOccurrences_throws();
        });
    }

    @Override
    @Test
    public void forEachWithOccurrences()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.forEachWithOccurrences();
        });
    }

    @Override
    @Test
    public void with()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.with();
        });
    }

    @Override
    @Test
    public void withAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.withAll();
        });
    }

    @Override
    @Test
    public void without()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.without();
        });
    }

    @Override
    @Test
    public void withoutAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            super.withoutAll();
        });
    }

    @Override
    @Test
    public void testClone()
    {
        MutableSortedBag<Integer> set = this.newWith(1, 2, 3);
        MutableSortedBag<Integer> clone = set.clone();
        Assertions.assertSame(set, clone);
        Verify.assertSortedBagsEqual(set, clone);
    }
}
