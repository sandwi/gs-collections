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

package com.gs.collections.impl.collection.mutable.primitive;

import java.util.Arrays;
import java.util.NoSuchElementException;

import com.gs.collections.api.BooleanIterable;
import com.gs.collections.api.LazyBooleanIterable;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.iterator.BooleanIterator;
import com.gs.collections.impl.bag.mutable.primitive.BooleanHashBag;
import com.gs.collections.impl.block.factory.primitive.BooleanPredicates;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.math.MutableInteger;
import com.gs.collections.impl.set.mutable.primitive.BooleanHashSet;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Abstract JUnit test for {@link BooleanIterable}s.
 */
public abstract class AbstractBooleanIterableTestCase
{
    protected abstract BooleanIterable classUnderTest();

    protected abstract BooleanIterable newWith(boolean... elements);

    protected abstract BooleanIterable newMutableCollectionWith(boolean... elements);

    protected abstract RichIterable<Object> newObjectCollectionWith(Object... elements);

    @Test
    public void newCollectionWith()
    {
        BooleanIterable iterable = this.newWith(true, false, true);
        Verify.assertSize(3, iterable);
        Assertions.assertTrue(iterable.containsAll(true, false, true));

        BooleanIterable iterable1 = this.newWith();
        Verify.assertEmpty(iterable1);
        Assertions.assertFalse(iterable1.containsAll(true, false, true));

        BooleanIterable iterable2 = this.newWith(true);
        Verify.assertSize(1, iterable2);
        Assertions.assertFalse(iterable2.containsAll(true, false, true));
        Assertions.assertTrue(iterable2.containsAll(true, true));
    }

    @Test
    public void newCollection()
    {
        Assertions.assertEquals(this.newMutableCollectionWith(), this.newWith());
        Assertions.assertEquals(this.newMutableCollectionWith(true, false, true), this.newWith(true, false, true));
    }

    @Test
    public void isEmpty()
    {
        Verify.assertEmpty(this.newWith());
        Verify.assertNotEmpty(this.classUnderTest());
        Verify.assertNotEmpty(this.newWith(false));
        Verify.assertNotEmpty(this.newWith(true));
    }

    @Test
    public void notEmpty()
    {
        Assertions.assertFalse(this.newWith().notEmpty());
        Assertions.assertTrue(this.classUnderTest().notEmpty());
        Assertions.assertTrue(this.newWith(false).notEmpty());
        Assertions.assertTrue(this.newWith(true).notEmpty());
    }

    @Test
    public void contains()
    {
        BooleanIterable emptyCollection = this.newWith();
        Assertions.assertFalse(emptyCollection.contains(true));
        Assertions.assertFalse(emptyCollection.contains(false));
        BooleanIterable booleanIterable = this.classUnderTest();
        int size = booleanIterable.size();
        Assertions.assertEquals(size >= 1, booleanIterable.contains(true));
        Assertions.assertEquals(size >= 2, booleanIterable.contains(false));
        Assertions.assertFalse(this.newWith(true, true, true).contains(false));
        Assertions.assertFalse(this.newWith(false, false, false).contains(true));
    }

    @Test
    public void containsAllArray()
    {
        BooleanIterable iterable = this.classUnderTest();
        int size = iterable.size();
        Assertions.assertEquals(size >= 1, iterable.containsAll(true));
        Assertions.assertEquals(size >= 2, iterable.containsAll(true, false, true));
        Assertions.assertEquals(size >= 2, iterable.containsAll(true, false));
        Assertions.assertEquals(size >= 1, iterable.containsAll(true, true));
        Assertions.assertEquals(size >= 2, iterable.containsAll(false, false));

        BooleanIterable emptyCollection = this.newWith();
        Assertions.assertFalse(emptyCollection.containsAll(true));
        Assertions.assertFalse(emptyCollection.containsAll(false));
        Assertions.assertFalse(emptyCollection.containsAll(false, true, false));
        Assertions.assertFalse(this.newWith(true, true).containsAll(false, true, false));

        BooleanIterable trueCollection = this.newWith(true, true, true, true);
        Assertions.assertFalse(trueCollection.containsAll(true, false));
        BooleanIterable falseCollection = this.newWith(false, false, false, false);
        Assertions.assertFalse(falseCollection.containsAll(true, false));
    }

    @Test
    public void containsAllIterable()
    {
        BooleanIterable emptyCollection = this.newWith();
        Assertions.assertTrue(emptyCollection.containsAll(new BooleanArrayList()));
        Assertions.assertFalse(emptyCollection.containsAll(BooleanArrayList.newListWith(true)));
        Assertions.assertFalse(emptyCollection.containsAll(BooleanArrayList.newListWith(false)));
        BooleanIterable booleanIterable = this.classUnderTest();
        int size = booleanIterable.size();
        Assertions.assertTrue(booleanIterable.containsAll(new BooleanArrayList()));
        Assertions.assertEquals(size >= 1, booleanIterable.containsAll(BooleanArrayList.newListWith(true)));
        Assertions.assertEquals(size >= 2, booleanIterable.containsAll(BooleanArrayList.newListWith(false)));
        Assertions.assertEquals(size >= 2, booleanIterable.containsAll(BooleanArrayList.newListWith(true, false)));
        BooleanIterable iterable = this.newWith(true, true, false, false, false);
        Assertions.assertTrue(iterable.containsAll(BooleanArrayList.newListWith(true)));
        Assertions.assertTrue(iterable.containsAll(BooleanArrayList.newListWith(false)));
        Assertions.assertTrue(iterable.containsAll(BooleanArrayList.newListWith(true, false)));
        Assertions.assertTrue(iterable.containsAll(BooleanArrayList.newListWith(true, true)));
        Assertions.assertTrue(iterable.containsAll(BooleanArrayList.newListWith(false, false)));
        Assertions.assertTrue(iterable.containsAll(BooleanArrayList.newListWith(true, false, true)));
        Assertions.assertFalse(this.newWith(true, true).containsAll(BooleanArrayList.newListWith(false, true, false)));

        BooleanIterable trueCollection = this.newWith(true, true, true, true);
        Assertions.assertFalse(trueCollection.containsAll(BooleanArrayList.newListWith(true, false)));
        BooleanIterable falseCollection = this.newWith(false, false, false, false);
        Assertions.assertFalse(falseCollection.containsAll(BooleanArrayList.newListWith(true, false)));
    }

    @Test
    public void iterator_throws()
    {
        assertThrows(NoSuchElementException.class, () -> {
            BooleanIterator iterator = this.classUnderTest().booleanIterator();
            while (iterator.hasNext())
            {
                iterator.next();
            }

            iterator.next();
        });
    }

    @Test
    public void iterator_throws_non_empty_collection()
    {
        assertThrows(NoSuchElementException.class, () -> {
            BooleanIterable iterable = this.newWith(true, true, true);
            BooleanIterator iterator = iterable.booleanIterator();
            while (iterator.hasNext())
            {
                Assertions.assertTrue(iterator.next());
            }
            iterator.next();
        });
    }

    @Test
    public void iterator_throws_emptyList()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.newWith().booleanIterator().next();
        });
    }

    @Test
    public void booleanIterator()
    {
        BooleanArrayList list = BooleanArrayList.newListWith(true, false, true);
        BooleanIterator iterator = this.classUnderTest().booleanIterator();
        for (int i = 0; i < 3; i++)
        {
            Assertions.assertTrue(iterator.hasNext());
            Assertions.assertTrue(list.remove(iterator.next()));
        }
        Verify.assertEmpty(list);
        Assertions.assertFalse(iterator.hasNext());
    }

    @Test
    public void forEach()
    {
        long[] sum = new long[1];
        this.classUnderTest().forEach(each -> sum[0] += each ? 1 : 0);

        int size = this.classUnderTest().size();
        int halfSize = size / 2;
        Assertions.assertEquals((size & 1) == 0 ? halfSize : halfSize + 1, sum[0]);

        long[] sum1 = new long[1];
        this.newWith(true, false, false, true, true, true).forEach(each -> sum1[0] += each ? 1 : 2);

        Assertions.assertEquals(8L, sum1[0]);
    }

    @Test
    public void size()
    {
        Verify.assertSize(0, this.newWith());
        Verify.assertSize(1, this.newWith(true));
        Verify.assertSize(1, this.newWith(false));
        Verify.assertSize(2, this.newWith(true, false));
    }

    @Test
    public void count()
    {
        Assertions.assertEquals(2L, this.newWith(true, false, true).count(BooleanPredicates.isTrue()));
        Assertions.assertEquals(0L, this.newWith().count(BooleanPredicates.isFalse()));

        BooleanIterable iterable = this.newWith(true, false, false, true, true, true);
        Assertions.assertEquals(4L, iterable.count(BooleanPredicates.isTrue()));
        Assertions.assertEquals(2L, iterable.count(BooleanPredicates.isFalse()));
        Assertions.assertEquals(6L, iterable.count(BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())));

        BooleanIterable iterable1 = this.classUnderTest();
        int size = iterable1.size();
        int halfSize = size / 2;
        Assertions.assertEquals((size & 1) == 1 ? halfSize + 1 : halfSize, iterable1.count(BooleanPredicates.isTrue()));
        Assertions.assertEquals(halfSize, iterable1.count(BooleanPredicates.isFalse()));
    }

    @Test
    public void anySatisfy()
    {
        BooleanIterable booleanIterable = this.classUnderTest();
        int size = booleanIterable.size();
        Assertions.assertEquals(size >= 1, booleanIterable.anySatisfy(BooleanPredicates.isTrue()));
        Assertions.assertEquals(size >= 2, booleanIterable.anySatisfy(BooleanPredicates.isFalse()));
        Assertions.assertFalse(this.newWith(true, true).anySatisfy(BooleanPredicates.isFalse()));
        Assertions.assertFalse(this.newWith().anySatisfy(BooleanPredicates.isFalse()));
        Assertions.assertFalse(this.newWith().anySatisfy(BooleanPredicates.isTrue()));
        Assertions.assertTrue(this.newWith(true).anySatisfy(BooleanPredicates.isTrue()));
        Assertions.assertFalse(this.newWith(false).anySatisfy(BooleanPredicates.isTrue()));
        Assertions.assertTrue(this.newWith(false, false, false).anySatisfy(BooleanPredicates.isFalse()));
    }

    @Test
    public void allSatisfy()
    {
        BooleanIterable booleanIterable = this.classUnderTest();
        int size = booleanIterable.size();
        Assertions.assertEquals(size <= 1, booleanIterable.allSatisfy(BooleanPredicates.isTrue()));
        Assertions.assertEquals(size == 0, booleanIterable.allSatisfy(BooleanPredicates.isFalse()));
        Assertions.assertTrue(this.newWith().allSatisfy(BooleanPredicates.isTrue()));
        Assertions.assertTrue(this.newWith().allSatisfy(BooleanPredicates.isFalse()));
        Assertions.assertTrue(this.newWith(false, false).allSatisfy(BooleanPredicates.isFalse()));
        Assertions.assertFalse(this.newWith(true, false).allSatisfy(BooleanPredicates.isTrue()));
        Assertions.assertTrue(this.newWith(true, true, true).allSatisfy(BooleanPredicates.isTrue()));
        Assertions.assertTrue(this.newWith(false, false, false).allSatisfy(BooleanPredicates.isFalse()));
    }

    @Test
    public void noneSatisfy()
    {
        BooleanIterable booleanIterable = this.classUnderTest();
        int size = booleanIterable.size();
        Assertions.assertEquals(size == 0, booleanIterable.noneSatisfy(BooleanPredicates.isTrue()));
        Assertions.assertEquals(size <= 1, booleanIterable.noneSatisfy(BooleanPredicates.isFalse()));
        Assertions.assertTrue(this.newWith().noneSatisfy(BooleanPredicates.isTrue()));
        Assertions.assertTrue(this.newWith().noneSatisfy(BooleanPredicates.isFalse()));
        Assertions.assertTrue(this.newWith(false, false).noneSatisfy(BooleanPredicates.isTrue()));
        Assertions.assertTrue(this.newWith(true, true).noneSatisfy(BooleanPredicates.isFalse()));
        Assertions.assertFalse(this.newWith(true, true).noneSatisfy(BooleanPredicates.isTrue()));
        Assertions.assertTrue(this.newWith(false, false, false).noneSatisfy(BooleanPredicates.isTrue()));
    }

    @Test
    public void select()
    {
        BooleanIterable iterable = this.classUnderTest();
        int size = iterable.size();
        int halfSize = size / 2;
        Verify.assertSize((size & 1) == 1 ? halfSize + 1 : halfSize, iterable.select(BooleanPredicates.isTrue()));
        Verify.assertSize(halfSize, iterable.select(BooleanPredicates.isFalse()));

        BooleanIterable iterable1 = this.newWith(false, true, false, false, true, true, true);
        Assertions.assertEquals(this.newMutableCollectionWith(true, true, true, true), iterable1.select(BooleanPredicates.isTrue()));
        Assertions.assertEquals(this.newMutableCollectionWith(false, false, false), iterable1.select(BooleanPredicates.isFalse()));
    }

    @Test
    public void reject()
    {
        BooleanIterable iterable = this.classUnderTest();
        int size = iterable.size();
        int halfSize = size / 2;
        Verify.assertSize(halfSize, iterable.reject(BooleanPredicates.isTrue()));
        Verify.assertSize((size & 1) == 1 ? halfSize + 1 : halfSize, iterable.reject(BooleanPredicates.isFalse()));

        BooleanIterable iterable1 = this.newWith(false, true, false, false, true, true, true);
        Assertions.assertEquals(this.newMutableCollectionWith(false, false, false), iterable1.reject(BooleanPredicates.isTrue()));
        Assertions.assertEquals(this.newMutableCollectionWith(true, true, true, true), iterable1.reject(BooleanPredicates.isFalse()));
    }

    @Test
    public void detectIfNone()
    {
        BooleanIterable iterable = this.classUnderTest();
        int size = iterable.size();
        Assertions.assertEquals(size < 2, iterable.detectIfNone(BooleanPredicates.isFalse(), true));
        Assertions.assertTrue(iterable.detectIfNone(BooleanPredicates.and(BooleanPredicates.isTrue(), BooleanPredicates.isFalse()), true));

        BooleanIterable iterable1 = this.newWith(true, true, true);
        Assertions.assertFalse(iterable1.detectIfNone(BooleanPredicates.isFalse(), false));
        Assertions.assertTrue(iterable1.detectIfNone(BooleanPredicates.isFalse(), true));
        Assertions.assertTrue(iterable1.detectIfNone(BooleanPredicates.isTrue(), false));
        Assertions.assertTrue(iterable1.detectIfNone(BooleanPredicates.isTrue(), true));

        BooleanIterable iterable2 = this.newWith(false, false, false);
        Assertions.assertTrue(iterable2.detectIfNone(BooleanPredicates.isTrue(), true));
        Assertions.assertFalse(iterable2.detectIfNone(BooleanPredicates.isTrue(), false));
        Assertions.assertFalse(iterable2.detectIfNone(BooleanPredicates.isFalse(), true));
        Assertions.assertFalse(iterable2.detectIfNone(BooleanPredicates.isFalse(), false));
    }

    @Test
    public void collect()
    {
        FastList<Object> objects = FastList.newListWith();
        for (int i = 0; i < this.classUnderTest().size(); i++)
        {
            objects.add((i & 1) == 0 ? 1 : 0);
        }
        RichIterable<Object> expected = this.newObjectCollectionWith(objects.toArray());
        Assertions.assertEquals(expected, this.classUnderTest().collect(value -> Integer.valueOf(value ? 1 : 0)));

        Assertions.assertEquals(this.newObjectCollectionWith(false, true, false), this.newWith(true, false, true).collect(parameter -> !parameter));
        Assertions.assertEquals(this.newObjectCollectionWith(), this.newWith().collect(parameter -> !parameter));
        Assertions.assertEquals(this.newObjectCollectionWith(true), this.newWith(false).collect(parameter -> !parameter));
    }

    @Test
    public void injectInto()
    {
        BooleanIterable iterable = this.newWith(true, false, true);
        MutableInteger result = iterable.injectInto(new MutableInteger(0), (object, value) -> object.add(value ? 1 : 0));
        Assertions.assertEquals(new MutableInteger(2), result);
    }

    @Test
    public void toArray()
    {
        Assertions.assertEquals(0L, this.newWith().toArray().length);
        Assertions.assertTrue(Arrays.equals(new boolean[]{true}, this.newWith(true).toArray()));
        Assertions.assertTrue(Arrays.equals(new boolean[]{false, true}, this.newWith(true, false).toArray())
                || Arrays.equals(new boolean[]{true, false}, this.newWith(true, false).toArray()));
    }

    @Test
    public void testEquals()
    {
        BooleanIterable iterable1 = this.newWith(true, false, true, false);
        BooleanIterable iterable2 = this.newWith(true, false, true, false);
        BooleanIterable iterable3 = this.newWith(false, false, false, true);
        BooleanIterable iterable4 = this.newWith(true, true, true);
        BooleanIterable iterable5 = this.newWith(true, true, false, false, false);
        BooleanIterable iterable6 = this.newWith(true);

        Verify.assertEqualsAndHashCode(iterable1, iterable2);
        Verify.assertEqualsAndHashCode(this.newWith(), this.newWith());
        Verify.assertPostSerializedEqualsAndHashCode(iterable6);
        Verify.assertPostSerializedEqualsAndHashCode(iterable1);
        Verify.assertPostSerializedEqualsAndHashCode(iterable5);
        Assertions.assertNotEquals(iterable1, iterable3);
        Assertions.assertNotEquals(iterable1, iterable4);
        Assertions.assertNotEquals(this.newWith(), this.newWith(true));
        Assertions.assertNotEquals(iterable6, this.newWith(true, false));
    }

    @Test
    public void testHashCode()
    {
        Assertions.assertEquals(this.newObjectCollectionWith().hashCode(), this.newWith().hashCode());
        Assertions.assertEquals(this.newObjectCollectionWith(true, false, true).hashCode(),
                this.newWith(true, false, true).hashCode());
        Assertions.assertEquals(this.newObjectCollectionWith(true).hashCode(),
                this.newWith(true).hashCode());
        Assertions.assertEquals(this.newObjectCollectionWith(false).hashCode(),
                this.newWith(false).hashCode());
    }

    @Test
    public void testToString()
    {
        Assertions.assertEquals("[]", this.newWith().toString());
        Assertions.assertEquals("[true]", this.newWith(true).toString());
        BooleanIterable iterable = this.newWith(true, false);
        Assertions.assertTrue("[true, false]".equals(iterable.toString())
                || "[false, true]".equals(iterable.toString()));
    }

    @Test
    public void makeString()
    {
        Assertions.assertEquals("true", this.newWith(true).makeString("/"));
        Assertions.assertEquals("", this.newWith().makeString());
        Assertions.assertEquals("", this.newWith().makeString("/"));
        Assertions.assertEquals("[]", this.newWith().makeString("[", "/", "]"));
        BooleanIterable iterable = this.newWith(true, false);
        Assertions.assertTrue("true, false".equals(iterable.makeString())
                || "false, true".equals(iterable.makeString()));
        Assertions.assertTrue("true/false".equals(iterable.makeString("/"))
                || "false/true".equals(iterable.makeString("/")), iterable.makeString("/"));
        Assertions.assertTrue("[true/false]".equals(iterable.makeString("[", "/", "]"))
                || "[false/true]".equals(iterable.makeString("[", "/", "]")), iterable.makeString("[", "/", "]"));
    }

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
        StringBuilder appendable4 = new StringBuilder();
        iterable.appendString(appendable4, "[", ", ", "]");
        Assertions.assertEquals(iterable.toString(), appendable4.toString());
    }

    @Test
    public void toList()
    {
        BooleanIterable iterable = this.newWith(true, false);
        Assertions.assertTrue(BooleanArrayList.newListWith(false, true).equals(iterable.toList())
                || BooleanArrayList.newListWith(true, false).equals(iterable.toList()));
        BooleanIterable iterable1 = this.newWith(true);
        Assertions.assertEquals(BooleanArrayList.newListWith(true), iterable1.toList());
        BooleanIterable iterable0 = this.newWith();
        Assertions.assertEquals(BooleanArrayList.newListWith(), iterable0.toList());
    }

    @Test
    public void toSet()
    {
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.newWith().toSet());
        Assertions.assertEquals(BooleanHashSet.newSetWith(true), this.newWith(true).toSet());
        Assertions.assertEquals(BooleanHashSet.newSetWith(true, false), this.newWith(true, false, false, true, true, true).toSet());
    }

    @Test
    public void toBag()
    {
        Assertions.assertEquals(BooleanHashBag.newBagWith(), this.newWith().toBag());
        Assertions.assertEquals(BooleanHashBag.newBagWith(true), this.newWith(true).toBag());
        Assertions.assertEquals(BooleanHashBag.newBagWith(true, false, true), this.newWith(true, false, true).toBag());
        Assertions.assertEquals(BooleanHashBag.newBagWith(false, false, true, true, true, true), this.newWith(true, false, false, true, true, true).toBag());
    }

    @Test
    public void asLazy()
    {
        BooleanIterable iterable = this.classUnderTest();
        Assertions.assertEquals(iterable.toBag(), iterable.asLazy().toBag());
        Verify.assertInstanceOf(LazyBooleanIterable.class, iterable.asLazy());
    }
}
