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

package com.gs.collections.impl.list.mutable.primitive;

import com.gs.collections.api.iterator.BooleanIterator;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.list.primitive.ImmutableBooleanList;
import com.gs.collections.api.list.primitive.MutableBooleanList;
import com.gs.collections.impl.collection.mutable.primitive.AbstractMutableBooleanCollectionTestCase;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.math.MutableInteger;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Abstract JUnit test for {@link MutableBooleanList}.
 */
public abstract class AbstractBooleanListTestCase extends AbstractMutableBooleanCollectionTestCase
{
    @Override
    protected abstract MutableBooleanList classUnderTest();

    @Override
    protected abstract MutableBooleanList newWith(boolean... elements);

    @Override
    protected MutableBooleanList newMutableCollectionWith(boolean... elements)
    {
        return BooleanArrayList.newListWith(elements);
    }

    @Override
    protected MutableList<Object> newObjectCollectionWith(Object... elements)
    {
        return FastList.newListWith(elements);
    }

    @Test
    public void get()
    {
        MutableBooleanList list = this.classUnderTest();
        Assertions.assertTrue(list.get(0));
        Assertions.assertFalse(list.get(1));
        Assertions.assertTrue(list.get(2));
    }

    @Test
    public void get_throws_index_greater_than_size()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.classUnderTest().get(3);
        });
    }

    @Test
    public void get_throws_empty_list()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.newWith().get(0);
        });
    }

    @Test
    public void get_throws_index_negative()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.classUnderTest().get(-1);
        });
    }

    @Test
    public void getFirst()
    {
        MutableBooleanList singleItemList = this.newWith(true);
        Assertions.assertTrue(singleItemList.getFirst());
        Assertions.assertTrue(this.classUnderTest().getFirst());
    }

    @Test
    public void getFirst_emptyList_throws()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.newWith().getFirst();
        });
    }

    @Test
    public void getLast()
    {
        MutableBooleanList singleItemList = this.newWith(true);
        Assertions.assertTrue(singleItemList.getLast());
        Assertions.assertTrue(this.classUnderTest().getLast());
        Assertions.assertFalse(this.newWith(true, true, false).getLast());
    }

    @Test
    public void getLast_emptyList_throws()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.newWith().getLast();
        });
    }

    @Test
    public void subList()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.classUnderTest().subList(0, 1);
        });
    }

    @Test
    public void indexOf()
    {
        MutableBooleanList arrayList = this.newWith(true, false, true);
        Assertions.assertEquals(0L, arrayList.indexOf(true));
        Assertions.assertEquals(1L, arrayList.indexOf(false));
        Assertions.assertEquals(-1L, this.newWith(false, false).indexOf(true));
        MutableBooleanList emptyList = this.newWith();
        Assertions.assertEquals(-1L, emptyList.indexOf(true));
        Assertions.assertEquals(-1L, emptyList.indexOf(false));
    }

    @Test
    public void lastIndexOf()
    {
        MutableBooleanList list = this.newWith(true, false, true);
        Assertions.assertEquals(2L, list.lastIndexOf(true));
        Assertions.assertEquals(1L, list.lastIndexOf(false));
        Assertions.assertEquals(-1L, this.newWith(false, false).lastIndexOf(true));
        MutableBooleanList emptyList = this.newWith();
        Assertions.assertEquals(-1L, emptyList.lastIndexOf(true));
        Assertions.assertEquals(-1L, emptyList.lastIndexOf(false));
    }

    @Test
    public void addAtIndex()
    {
        MutableBooleanList emptyList = this.newWith();
        emptyList.addAtIndex(0, false);
        Assertions.assertEquals(BooleanArrayList.newListWith(false), emptyList);
        MutableBooleanList list = this.classUnderTest();
        list.addAtIndex(3, true);
        Assertions.assertEquals(BooleanArrayList.newListWith(true, false, true, true), list);
        list.addAtIndex(2, false);
        Assertions.assertEquals(BooleanArrayList.newListWith(true, false, false, true, true), list);
    }

    @Test
    public void addAtIndex_throws_index_greater_than_size()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.newWith().addAtIndex(1, false);
        });
    }

    @Test
    public void addAtIndex_throws_index_negative()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.classUnderTest().addAtIndex(-1, true);
        });
    }

    @Override
    @Test
    public void addAllArray()
    {
        super.addAllArray();
        MutableBooleanList list = this.classUnderTest();
        Assertions.assertFalse(list.addAllAtIndex(1));
        Assertions.assertTrue(list.addAll(false, true, false));
        Assertions.assertTrue(list.addAllAtIndex(4, true, true));
        Assertions.assertEquals(BooleanArrayList.newListWith(true, false, true, false, true, true, true, false), list);
    }

    @Override
    @Test
    public void addAllIterable()
    {
        super.addAllIterable();
        MutableBooleanList list = this.classUnderTest();
        Assertions.assertFalse(list.addAllAtIndex(1, new BooleanArrayList()));
        Assertions.assertTrue(list.addAll(BooleanArrayList.newListWith(false, true, false)));
        Assertions.assertTrue(list.addAllAtIndex(4, BooleanArrayList.newListWith(true, true)));
        Assertions.assertEquals(BooleanArrayList.newListWith(true, false, true, false, true, true, true, false), list);
    }

    @Test
    public void addAll_throws_index_negative()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.classUnderTest().addAllAtIndex(-1, false, true);
        });
    }

    @Test
    public void addAll_throws_index_greater_than_size()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.classUnderTest().addAllAtIndex(5, false, true);
        });
    }

    @Test
    public void addAll_throws_index_greater_than_size_empty_list()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.newWith().addAllAtIndex(1, false);
        });
    }

    @Override
    @Test
    public void remove()
    {
        super.remove();
        Assertions.assertFalse(this.newWith(true, true).remove(false));
        MutableBooleanList list = this.classUnderTest();
        Assertions.assertTrue(list.remove(true));
        Assertions.assertEquals(BooleanArrayList.newListWith(false, true), list);
    }

    @Test
    public void removeAtIndex()
    {
        MutableBooleanList list = this.classUnderTest();
        list.removeAtIndex(1);
        Assertions.assertEquals(BooleanArrayList.newListWith(true, true), list);
        list.removeAtIndex(1);
        Assertions.assertEquals(BooleanArrayList.newListWith(true), list);
        list.removeAtIndex(0);
        Assertions.assertEquals(BooleanArrayList.newListWith(), list);
    }

    @Test
    public void removeAtIndex_throws_index_greater_than_size()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.newWith().removeAtIndex(1);
        });
    }

    @Test
    public void removeAtIndex_throws_index_negative()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.classUnderTest().removeAtIndex(-1);
        });
    }

    @Test
    public void set()
    {
        MutableBooleanList list = this.classUnderTest();
        list.set(1, false);
        Assertions.assertEquals(BooleanArrayList.newListWith(true, false, true), list);
        list.set(1, true);
        Assertions.assertEquals(BooleanArrayList.newListWith(true, true, true), list);
    }

    @Test
    public void set_throws_index_greater_than_size()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.newWith().set(1, false);
        });
    }

    @Override
    @Test
    public void booleanIterator()
    {
        BooleanIterator iterator = this.classUnderTest().booleanIterator();
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertTrue(iterator.next());
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertFalse(iterator.next());
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertTrue(iterator.next());
        Assertions.assertFalse(iterator.hasNext());
    }

    @Override
    @Test
    public void forEach()
    {
        super.forEach();
        String[] sum = new String[2];
        sum[0] = "";
        sum[1] = "";
        this.classUnderTest().forEach(each -> sum[0] += each + " ");
        this.newWith().forEach(each -> sum[1] += each);
        Assertions.assertEquals("true false true ", sum[0]);
        Assertions.assertEquals("", sum[1]);
    }

    @Override
    @Test
    public void size()
    {
        super.size();
        Verify.assertSize(3, this.classUnderTest());
    }

    @Override
    @Test
    public void toArray()
    {
        super.toArray();
        MutableBooleanList list = this.classUnderTest();
        Assertions.assertEquals(3L, (long) list.toArray().length);
        Assertions.assertTrue(list.toArray()[0]);
        Assertions.assertFalse(list.toArray()[1]);
        Assertions.assertTrue(list.toArray()[2]);
    }

    @Test
    public void reverseThis()
    {
        Assertions.assertEquals(BooleanArrayList.newListWith(true, true, false, false), this.newWith(false, false, true, true).reverseThis());
        MutableBooleanList originalList = this.newWith(true, true, false, false);
        Assertions.assertSame(originalList, originalList.reverseThis());
    }

    @Test
    public void toReversed()
    {
        Assertions.assertEquals(BooleanArrayList.newListWith(true, true, false, false), this.newWith(false, false, true, true).toReversed());
        MutableBooleanList originalList = this.newWith(true, true, false, false);
        Assertions.assertNotSame(originalList, originalList.toReversed());
    }

    @Test
    public void injectIntoWithIndex()
    {
        MutableBooleanList list = this.newWith(true, false, true);
        MutableInteger result = list.injectIntoWithIndex(new MutableInteger(0), (object, value, index) -> object.add((value ? 1 : 0) + index));
        Assertions.assertEquals(new MutableInteger(5), result);
    }

    @Test
    public void forEachWithIndex()
    {
        String[] sum = new String[2];
        sum[0] = "";
        sum[1] = "";
        this.classUnderTest().forEachWithIndex((each, index) -> sum[0] += index + ":" + each);
        this.newWith().forEachWithIndex((each, index) -> sum[1] += index + ":" + each);
        Assertions.assertEquals("0:true1:false2:true", sum[0]);
        Assertions.assertEquals("", sum[1]);
    }

    @Override
    @Test
    public void testEquals()
    {
        super.testEquals();
        MutableBooleanList list1 = this.newWith(true, false, true, true);
        MutableBooleanList list2 = this.newWith(true, true, false, true);
        Assertions.assertNotEquals(list1, list2);
    }

    @Override
    @Test
    public void testToString()
    {
        super.testToString();
        Assertions.assertEquals("[true, false, true]", this.classUnderTest().toString());
        Assertions.assertEquals("[]", this.newWith().toString());
    }

    @Override
    @Test
    public void makeString()
    {
        super.makeString();
        Assertions.assertEquals("true, false, true", this.classUnderTest().makeString());
        Assertions.assertEquals("true", this.newWith(true).makeString("/"));
        Assertions.assertEquals("true/false/true", this.classUnderTest().makeString("/"));
        Assertions.assertEquals(this.classUnderTest().toString(), this.classUnderTest().makeString("[", ", ", "]"));
        Assertions.assertEquals("", this.newWith().makeString());
    }

    @Override
    @Test
    public void appendString()
    {
        super.appendString();
        StringBuilder appendable = new StringBuilder();
        this.newWith().appendString(appendable);
        Assertions.assertEquals("", appendable.toString());
        StringBuilder appendable2 = new StringBuilder();
        this.classUnderTest().appendString(appendable2);
        Assertions.assertEquals("true, false, true", appendable2.toString());
        StringBuilder appendable3 = new StringBuilder();
        this.classUnderTest().appendString(appendable3, "/");
        Assertions.assertEquals("true/false/true", appendable3.toString());
        StringBuilder appendable4 = new StringBuilder();
        this.classUnderTest().appendString(appendable4, "[", ", ", "]");
        Assertions.assertEquals(this.classUnderTest().toString(), appendable4.toString());
    }

    @Override
    @Test
    public void toList()
    {
        super.toList();
        Assertions.assertEquals(BooleanArrayList.newListWith(true, false, true), this.classUnderTest().toList());
    }

    @Test
    public void toImmutable()
    {
        ImmutableBooleanList immutable = this.classUnderTest().toImmutable();
        Assertions.assertEquals(BooleanArrayList.newListWith(true, false, true), immutable);
    }
}
