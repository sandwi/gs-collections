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

package com.gs.collections.impl.bag.mutable.primitive;

import com.gs.collections.api.bag.primitive.MutableBooleanBag;
import com.gs.collections.api.iterator.MutableBooleanIterator;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JUnit test for {@link UnmodifiableBooleanBag}.
 */
public class UnmodifiableBooleanBagTest extends AbstractMutableBooleanBagTestCase
{
    private final MutableBooleanBag bag = this.classUnderTest();

    @Override
    protected final UnmodifiableBooleanBag classUnderTest()
    {
        return new UnmodifiableBooleanBag(BooleanHashBag.newBagWith(true, false, true));
    }

    @Override
    protected UnmodifiableBooleanBag newWith(boolean... elements)
    {
        return new UnmodifiableBooleanBag(BooleanHashBag.newBagWith(elements));
    }

    @Override
    @Test
    public void addOccurrences()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.bag.addOccurrences(false, 3);
        });
    }

    @Override
    @Test
    public void addOccurrences_throws()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.newWith().addOccurrences(true, -1);
        });
    }

    @Override
    @Test
    public void removeOccurrences()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.bag.removeOccurrences(true, 1);
        });
    }

    @Override
    @Test
    public void removeOccurrences_throws()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.newWith().removeOccurrences(true, -1);
        });
    }

    @Override
    @Test
    public void clear()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.classUnderTest().clear();
        });
    }

    @Override
    @Test
    public void add()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.newWith().add(true);
        });
    }

    @Override
    @Test
    public void addAllArray()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.classUnderTest().addAll(true, false, true);
        });
    }

    @Override
    @Test
    public void addAllIterable()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.classUnderTest().addAll(this.newMutableCollectionWith());
        });
    }

    @Override
    @Test
    public void remove()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.classUnderTest().remove(false);
        });
    }

    @Override
    @Test
    public void removeAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.classUnderTest().removeAll(true, false);
        });
    }

    @Override
    @Test
    public void removeAll_iterable()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.classUnderTest().removeAll(this.newMutableCollectionWith());
        });
    }

    @Override
    @Test
    public void retainAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.classUnderTest().retainAll(true, false);
        });
    }

    @Override
    @Test
    public void retainAll_iterable()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.classUnderTest().retainAll(this.newMutableCollectionWith());
        });
    }

    @Override
    @Test
    public void with()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.newWith().with(true);
        });
    }

    @Override
    @Test
    public void withAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.newWith().withAll(this.newMutableCollectionWith(true));
        });
    }

    @Override
    @Test
    public void without()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.newWith(true, false, true, false, true).without(true);
        });
    }

    @Override
    @Test
    public void withoutAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.newWith(true, false, true, false, true).withoutAll(this.newMutableCollectionWith(false, false));
        });
    }

    @Override
    @Test
    public void containsAllArray()
    {
        UnmodifiableBooleanBag collection = this.classUnderTest();
        Assertions.assertTrue(collection.containsAll(true));
        Assertions.assertTrue(collection.containsAll(true, false, true));
        Assertions.assertTrue(collection.containsAll(true, false));
        Assertions.assertTrue(collection.containsAll(true, true));
        Assertions.assertTrue(collection.containsAll(false, false));
        UnmodifiableBooleanBag emptyCollection = this.newWith();
        Assertions.assertFalse(emptyCollection.containsAll(true));
        Assertions.assertFalse(emptyCollection.containsAll(false));
        Assertions.assertFalse(emptyCollection.containsAll(false, true, false));
        Assertions.assertFalse(this.newWith(true, true).containsAll(false, true, false));

        UnmodifiableBooleanBag trueCollection = this.newWith(true, true, true, true);
        Assertions.assertFalse(trueCollection.containsAll(true, false));
        UnmodifiableBooleanBag falseCollection = this.newWith(false, false, false, false);
        Assertions.assertFalse(falseCollection.containsAll(true, false));
    }

    @Override
    @Test
    public void containsAllIterable()
    {
        UnmodifiableBooleanBag emptyCollection = this.newWith();
        Assertions.assertTrue(emptyCollection.containsAll(new BooleanArrayList()));
        Assertions.assertFalse(emptyCollection.containsAll(BooleanArrayList.newListWith(true)));
        Assertions.assertFalse(emptyCollection.containsAll(BooleanArrayList.newListWith(false)));
        UnmodifiableBooleanBag collection = this.newWith(true, true, false, false, false);
        Assertions.assertTrue(collection.containsAll(BooleanArrayList.newListWith(true)));
        Assertions.assertTrue(collection.containsAll(BooleanArrayList.newListWith(false)));
        Assertions.assertTrue(collection.containsAll(BooleanArrayList.newListWith(true, false)));
        Assertions.assertTrue(collection.containsAll(BooleanArrayList.newListWith(true, true)));
        Assertions.assertTrue(collection.containsAll(BooleanArrayList.newListWith(false, false)));
        Assertions.assertTrue(collection.containsAll(BooleanArrayList.newListWith(true, false, true)));
        Assertions.assertFalse(this.newWith(true, true).containsAll(BooleanArrayList.newListWith(false, true, false)));

        UnmodifiableBooleanBag trueCollection = this.newWith(true, true, true, true);
        Assertions.assertFalse(trueCollection.containsAll(BooleanArrayList.newListWith(true, false)));
        UnmodifiableBooleanBag falseCollection = this.newWith(false, false, false, false);
        Assertions.assertFalse(falseCollection.containsAll(BooleanArrayList.newListWith(true, false)));
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        super.asUnmodifiable();
        Assertions.assertSame(this.bag, this.bag.asUnmodifiable());
        Assertions.assertEquals(this.bag, this.bag.asUnmodifiable());
    }

    @Override
    @Test
    public void booleanIterator_with_remove()
    {
        MutableBooleanIterator booleanIterator = this.classUnderTest().booleanIterator();
        Assertions.assertTrue(booleanIterator.hasNext());
        booleanIterator.next();
        Verify.assertThrows(UnsupportedOperationException.class, booleanIterator::remove);
    }

    @Override
    @Test
    public void iterator_throws_on_invocation_of_remove_before_next()
    {
        MutableBooleanIterator booleanIterator = this.classUnderTest().booleanIterator();
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
