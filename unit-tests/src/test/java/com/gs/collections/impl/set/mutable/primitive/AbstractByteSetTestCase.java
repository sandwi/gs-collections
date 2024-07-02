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

package com.gs.collections.impl.set.mutable.primitive;

import java.util.NoSuchElementException;

import com.gs.collections.api.LazyByteIterable;
import com.gs.collections.api.iterator.ByteIterator;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.set.primitive.MutableByteSet;
import com.gs.collections.impl.bag.mutable.primitive.ByteHashBag;
import com.gs.collections.impl.block.factory.primitive.BytePredicates;
import com.gs.collections.impl.collection.mutable.primitive.AbstractMutableByteCollectionTestCase;
import com.gs.collections.impl.factory.primitive.ByteSets;
import com.gs.collections.impl.list.mutable.primitive.ByteArrayList;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Abstract JUnit test for {@link MutableByteSet}.
 */
public abstract class AbstractByteSetTestCase extends AbstractMutableByteCollectionTestCase
{
    @Override
    protected abstract MutableByteSet classUnderTest();

    @Override
    protected abstract MutableByteSet newWith(byte... elements);

    @Override
    protected MutableByteSet newMutableCollectionWith(byte... elements)
    {
        return ByteHashSet.newSetWith(elements);
    }

    @Override
    protected MutableSet<Byte> newObjectCollectionWith(Byte... elements)
    {
        return UnifiedSet.newSetWith(elements);
    }

    @Override
    @Test
    public void size()
    {
        super.size();
        Verify.assertSize(5, this.newWith((byte) 0, (byte) 1, (byte) 31, (byte) -1, (byte) -128));
    }

    @Override
    @Test
    public void isEmpty()
    {
        super.isEmpty();
        Assertions.assertFalse(this.newWith((byte) 0, (byte) 1, (byte) 31, (byte) -1, (byte) -128).isEmpty());
    }

    @Override
    @Test
    public void notEmpty()
    {
        Assertions.assertTrue(this.newWith((byte) 0, (byte) 1, (byte) 31, (byte) -1, (byte) -128).notEmpty());
    }

    @Override
    @Test
    public void clear()
    {
        super.clear();
        MutableByteSet set = this.newWith((byte) 0, (byte) 1, (byte) 31, (byte) -1, (byte) -128);
        set.clear();
        Verify.assertSize(0, set);
        Assertions.assertFalse(set.contains((byte) 0));
        Assertions.assertFalse(set.contains((byte) 31));
        Assertions.assertFalse(set.contains((byte) 1));
        Assertions.assertFalse(set.contains((byte) -1));
        Assertions.assertFalse(set.contains((byte) -128));
    }

    @Override
    @Test
    public void add()
    {
        super.add();
        MutableByteSet set = this.newWith();
        Assertions.assertTrue(set.add((byte) 14));
        Assertions.assertFalse(set.add((byte) 14));
        Assertions.assertTrue(set.add((byte) 2));
        Assertions.assertFalse(set.add((byte) 2));
        Assertions.assertTrue(set.add((byte) 35));
        Assertions.assertFalse(set.add((byte) 35));
        Assertions.assertTrue(set.add((byte) 31));
        Assertions.assertFalse(set.add((byte) 31));
        Assertions.assertTrue(set.add((byte) 32));
        Assertions.assertFalse(set.add((byte) 32));
        Assertions.assertTrue(set.add((byte) 0));
        Assertions.assertFalse(set.add((byte) 0));
        Assertions.assertTrue(set.add((byte) 1));
        Assertions.assertFalse(set.add((byte) 1));
    }

    @Override
    @Test
    public void addAllIterable()
    {
        super.addAllIterable();
        MutableByteSet set = this.newWith((byte) 0, (byte) 1, (byte) 31, (byte) -1, (byte) -128);
        Assertions.assertFalse(set.addAll(new ByteArrayList()));
        Assertions.assertFalse(set.addAll(ByteArrayList.newListWith((byte) 31, (byte) -1, (byte) -128)));
        Assertions.assertEquals(ByteHashSet.newSetWith((byte) 0, (byte) 1, (byte) 31, (byte) -1, (byte) -128), set);

        Assertions.assertTrue(set.addAll(ByteHashSet.newSetWith((byte) 0, (byte) 1, (byte) 2, (byte) 30, (byte) -1, (byte) -128)));
        Assertions.assertEquals(ByteHashSet.newSetWith((byte) 0, (byte) 1, (byte) 2, (byte) 30, (byte) 31, (byte) -1, (byte) -128), set);

        Assertions.assertTrue(set.addAll(ByteHashSet.newSetWith((byte) 5)));
        Assertions.assertEquals(ByteHashSet.newSetWith((byte) 0, (byte) 1, (byte) 2, (byte) 5, (byte) 30, (byte) 31, (byte) 31, (byte) -1, (byte) -128), set);

        ByteHashSet set1 = new ByteHashSet();
        Assertions.assertTrue(set1.addAll((byte) 2, (byte) 35));
        Assertions.assertEquals(ByteHashSet.newSetWith((byte) 2, (byte) 35), set1);
    }

    @Override
    @Test
    public void remove()
    {
        super.remove();
        MutableByteSet set = this.newWith((byte) 0, (byte) 1, (byte) 31, (byte) -1, (byte) -8);
        Assertions.assertFalse(this.newWith().remove((byte) 15));
        Assertions.assertFalse(set.remove((byte) 15));
        Assertions.assertTrue(set.remove((byte) 0));
        Assertions.assertEquals(ByteHashSet.newSetWith((byte) 1, (byte) 31, (byte) -1, (byte) -8), set);
        Assertions.assertFalse(set.remove((byte) -10));
        Assertions.assertFalse(set.remove((byte) -7));
        Assertions.assertTrue(set.remove((byte) -1));
        Assertions.assertEquals(ByteHashSet.newSetWith((byte) 1, (byte) 31, (byte) -8), set);
        Assertions.assertTrue(set.remove((byte) -8));
        Assertions.assertEquals(ByteHashSet.newSetWith((byte) 1, (byte) 31), set);
        Assertions.assertTrue(set.remove((byte) 31));
        Assertions.assertEquals(ByteHashSet.newSetWith((byte) 1), set);
        Assertions.assertTrue(set.remove((byte) 1));
        Assertions.assertEquals(ByteHashSet.newSetWith(), set);
    }

    @Override
    @Test
    public void removeAll()
    {
        super.removeAll();
        MutableByteSet set = this.newWith((byte) 0, (byte) 1, (byte) 31, (byte) 63, (byte) 100, (byte) 127, (byte) -1, (byte) -35, (byte) -64, (byte) -100, (byte) -128);
        Assertions.assertFalse(set.removeAll());
        Assertions.assertFalse(set.removeAll((byte) 15, (byte) -5, (byte) -32));
        Assertions.assertEquals(ByteHashSet.newSetWith((byte) 0, (byte) 1, (byte) 31, (byte) 63, (byte) 100, (byte) 127, (byte) -1, (byte) -35, (byte) -64, (byte) -100, (byte) -128), set);
        Assertions.assertTrue(set.removeAll((byte) 0, (byte) 1, (byte) -1, (byte) -128));
        Assertions.assertEquals(ByteHashSet.newSetWith((byte) 31, (byte) 63, (byte) 100, (byte) 127, (byte) -35, (byte) -64, (byte) -100), set);
        Assertions.assertTrue(set.removeAll((byte) 31, (byte) 63, (byte) 14, (byte) -100));
        Assertions.assertEquals(ByteHashSet.newSetWith((byte) 100, (byte) 127, (byte) -35, (byte) -64), set);
        Assertions.assertFalse(set.removeAll((byte) -34, (byte) -36, (byte) -63, (byte) -65, (byte) 99, (byte) 101, (byte) 126, (byte) 128));
        Assertions.assertEquals(ByteHashSet.newSetWith((byte) 100, (byte) 127, (byte) -35, (byte) -64), set);
        Assertions.assertTrue(set.removeAll((byte) -35, (byte) -63, (byte) -64, (byte) 100, (byte) 127));
        Assertions.assertEquals(new ByteHashSet(), set);
    }

    @Override
    @Test
    public void removeAll_iterable()
    {
        super.removeAll_iterable();
        MutableByteSet set = this.newWith((byte) 0, (byte) 1, (byte) 31, (byte) 63, (byte) 100, (byte) 127, (byte) -1, (byte) -35, (byte) -64, (byte) -100, (byte) -128);
        Assertions.assertFalse(set.removeAll(new ByteArrayList()));
        Assertions.assertFalse(set.removeAll(ByteArrayList.newListWith((byte) 15, (byte) 98, (byte) -98, (byte) -127)));
        Assertions.assertEquals(ByteHashSet.newSetWith((byte) 0, (byte) 1, (byte) 31, (byte) 63, (byte) 100, (byte) 127, (byte) -1, (byte) -35, (byte) -64, (byte) -100, (byte) -128), set);
        Assertions.assertTrue(set.removeAll(ByteHashSet.newSetWith((byte) 0, (byte) 31, (byte) -128, (byte) -100)));
        Assertions.assertEquals(ByteHashSet.newSetWith((byte) 1, (byte) 63, (byte) 100, (byte) 127, (byte) -1, (byte) -35, (byte) -64), set);
        Assertions.assertTrue(set.removeAll(ByteHashSet.newSetWith((byte) 1, (byte) 63, (byte) 100, (byte) 127, (byte) -1, (byte) -35, (byte) -64)));
        Assertions.assertEquals(new ByteHashSet(), set);
        Assertions.assertFalse(set.removeAll(ByteHashSet.newSetWith((byte) 1)));
        Assertions.assertEquals(new ByteHashSet(), set);
    }

    @Override
    @Test
    public void byteIterator()
    {
        MutableSet<Byte> expected = UnifiedSet.newSetWith((byte) 0, (byte) 1, (byte) 31, (byte) 63, (byte) 100, (byte) 127, (byte) -1, (byte) -35, (byte) -64, (byte) -100, (byte) -128);
        MutableSet<Byte> actual = UnifiedSet.newSet();
        MutableByteSet set = this.newWith((byte) 0, (byte) 1, (byte) 31, (byte) 63, (byte) 100, (byte) 127, (byte) -1, (byte) -35, (byte) -64, (byte) -100, (byte) -128);
        ByteIterator iterator = set.byteIterator();
        Assertions.assertTrue(iterator.hasNext());
        actual.add(iterator.next());
        Assertions.assertTrue(iterator.hasNext());
        actual.add(iterator.next());
        Assertions.assertTrue(iterator.hasNext());
        actual.add(iterator.next());
        Assertions.assertTrue(iterator.hasNext());
        actual.add(iterator.next());
        Assertions.assertTrue(iterator.hasNext());
        actual.add(iterator.next());
        Assertions.assertTrue(iterator.hasNext());
        actual.add(iterator.next());
        Assertions.assertTrue(iterator.hasNext());
        actual.add(iterator.next());
        Assertions.assertTrue(iterator.hasNext());
        actual.add(iterator.next());
        Assertions.assertTrue(iterator.hasNext());
        actual.add(iterator.next());
        Assertions.assertTrue(iterator.hasNext());
        actual.add(iterator.next());
        Assertions.assertTrue(iterator.hasNext());
        actual.add(iterator.next());
        Assertions.assertFalse(iterator.hasNext());
        Assertions.assertEquals(expected, actual);
        Verify.assertThrows(NoSuchElementException.class, (Runnable) iterator::next);
    }

    @Override
    @Test
    public void byteIterator_throws()
    {
        assertThrows(NoSuchElementException.class, () -> {
            MutableByteSet set = this.newWith((byte) 0, (byte) 1, (byte) 31, (byte) -1, (byte) -31);
            ByteIterator iterator = set.byteIterator();
            while (iterator.hasNext())
            {
                iterator.next();
            }

            iterator.next();
        });
    }

    @Override
    @Test
    public void forEach()
    {
        super.forEach();
        long[] sum = new long[1];
        MutableByteSet set = this.newWith((byte) 0, (byte) 1, (byte) 31, (byte) 63, (byte) 65, (byte) 100, (byte) 127, (byte) 12, (byte) -76, (byte) -1, (byte) -54, (byte) -64, (byte) -63, (byte) -95, (byte) -128, (byte) -127);
        set.forEach(each -> sum[0] += each);

        Assertions.assertEquals(-209L, sum[0]);
    }

    @Override
    @Test
    public void count()
    {
        super.count();
        MutableByteSet set = this.newWith((byte) 0, (byte) 1, (byte) 31, (byte) 63, (byte) 65, (byte) 100, (byte) 127, (byte) 12, (byte) -76, (byte) -1, (byte) -54, (byte) -64, (byte) -63, (byte) -95, (byte) -128, (byte) -127);
        Assertions.assertEquals(7L, set.count(BytePredicates.greaterThan((byte) 0)));
        Assertions.assertEquals(12L, set.count(BytePredicates.lessThan((byte) 32)));
        Assertions.assertEquals(4L, set.count(BytePredicates.greaterThan((byte) 32)));
        Assertions.assertEquals(1L, set.count(BytePredicates.greaterThan((byte) 100)));
        Assertions.assertEquals(14L, set.count(BytePredicates.lessThan((byte) 100)));
        Assertions.assertEquals(7L, set.count(BytePredicates.lessThan((byte) -50)));
        Assertions.assertEquals(6L, set.count(BytePredicates.lessThan((byte) -54)));
        Assertions.assertEquals(15L, set.count(BytePredicates.greaterThan((byte) -128)));
    }

    @Override
    @Test
    public void select()
    {
        super.select();
        MutableByteSet set = this.newWith((byte) 0, (byte) 1, (byte) 31, (byte) 63, (byte) 65, (byte) 100, (byte) 127, (byte) 12, (byte) -76, (byte) -1, (byte) -54, (byte) -64, (byte) -63, (byte) -95, (byte) -128, (byte) -127);
        Assertions.assertEquals(this.newWith((byte) 63, (byte) 65, (byte) 100, (byte) 127), set.select(BytePredicates.greaterThan((byte) 32)));
        Assertions.assertEquals(this.newWith((byte) -76, (byte) -1, (byte) -54, (byte) -64, (byte) -63, (byte) -95, (byte) -128, (byte) -127), set.select(BytePredicates.lessThan((byte) 0)));
    }

    @Override
    @Test
    public void reject()
    {
        super.reject();
        MutableByteSet set = this.newWith((byte) 0, (byte) 1, (byte) 31, (byte) -1, (byte) -127, (byte) -63);
        Assertions.assertEquals(this.newWith((byte) 0, (byte) -1, (byte) -127, (byte) -63), set.reject(BytePredicates.greaterThan((byte) 0)));
        Assertions.assertEquals(this.newWith((byte) 0, (byte) 1, (byte) 31), set.reject(BytePredicates.lessThan((byte) 0)));
    }

    @Override
    @Test
    public void detectIfNone()
    {
        super.detectIfNone();
        MutableByteSet set = this.newWith((byte) 0, (byte) 1, (byte) 31, (byte) 64, (byte) 127, (byte) -1, (byte) -67, (byte) -128, (byte) -63);
        Assertions.assertEquals(127, set.detectIfNone(BytePredicates.greaterThan((byte) 126), (byte) 9));
        Assertions.assertEquals(127, set.detectIfNone(BytePredicates.greaterThan((byte) 64), (byte) 9));
        Assertions.assertEquals(-128, set.detectIfNone(BytePredicates.lessThan((byte) -68), (byte) 9));

        MutableByteSet set1 = this.newWith((byte) 0, (byte) -1, (byte) 12, (byte) 64);
        Assertions.assertEquals(-1, set1.detectIfNone(BytePredicates.lessThan((byte) 0), (byte) 9));
    }

    @Override
    @Test
    public void collect()
    {
        super.collect();
        MutableByteSet set = this.newWith((byte) 0, (byte) 1, (byte) 31, (byte) -127, (byte) -63);

        Assertions.assertEquals(UnifiedSet.newSetWith((byte) -1, (byte) 0, (byte) 30, (byte) -128, (byte) -64), set.collect(byteParameter -> (byte) (byteParameter - 1)));
    }

    @Override
    @Test
    public void toSortedArray()
    {
        super.toSortedArray();
        MutableByteSet set = this.newWith((byte) 0, (byte) 1, (byte) 31, (byte) -1, (byte) -123, (byte) -53);
        Assertions.assertArrayEquals(new byte[]{(byte) -123, (byte) -53, (byte) -1, (byte) 0, (byte) 1, (byte) 31}, set.toSortedArray());
    }

    @Override
    @Test
    public void testEquals()
    {
        super.testEquals();
        MutableByteSet set1 = this.newWith((byte) 1, (byte) 31, (byte) 32);
        MutableByteSet set2 = this.newWith((byte) 32, (byte) 31, (byte) 1);
        MutableByteSet set3 = this.newWith((byte) 32, (byte) 32, (byte) 31, (byte) 1);
        MutableByteSet set4 = this.newWith((byte) 32, (byte) 32, (byte) 31, (byte) 1, (byte) 1);
        Verify.assertEqualsAndHashCode(set1, set2);
        Verify.assertEqualsAndHashCode(set1, set3);
        Verify.assertEqualsAndHashCode(set1, set4);
        Verify.assertEqualsAndHashCode(set2, set3);
        Verify.assertEqualsAndHashCode(set2, set4);
    }

    @Override
    @Test
    public void testHashCode()
    {
        super.testEquals();
        MutableByteSet set1 = this.newWith((byte) 1, (byte) 31, (byte) 32);
        MutableByteSet set2 = this.newWith((byte) 32, (byte) 31, (byte) 1);
        Assertions.assertEquals(set1.hashCode(), set2.hashCode());
    }

    @Override
    @Test
    public void toBag()
    {
        Assertions.assertEquals(ByteHashBag.newBagWith((byte) 1, (byte) 2, (byte) 3), this.classUnderTest().toBag());
        Assertions.assertEquals(ByteHashBag.newBagWith((byte) 0, (byte) 1, (byte) 31), this.newWith((byte) 0, (byte) 1, (byte) 31).toBag());
        Assertions.assertEquals(ByteHashBag.newBagWith((byte) 0, (byte) 1, (byte) 31, (byte) 32), this.newWith((byte) 0, (byte) 1, (byte) 31, (byte) 32).toBag());
    }

    @Override
    @Test
    public void asLazy()
    {
        super.asLazy();
        MutableByteSet set = this.newWith((byte) 0, (byte) 1, (byte) 31, (byte) -1, (byte) -31, (byte) -24);
        Assertions.assertEquals(set.toSet(), set.asLazy().toSet());
        Verify.assertInstanceOf(LazyByteIterable.class, set.asLazy());
    }

    @Override
    @Test
    public void asSynchronized()
    {
        super.asSynchronized();
        MutableByteSet set = this.newWith((byte) 0, (byte) 1, (byte) 31, (byte) -1, (byte) -31, (byte) -24);
        Verify.assertInstanceOf(SynchronizedByteSet.class, set.asSynchronized());
        Assertions.assertEquals(new SynchronizedByteSet(set), set.asSynchronized());
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        super.asUnmodifiable();
        MutableByteSet set = this.newWith((byte) 0, (byte) 1, (byte) 31, (byte) -1, (byte) -31, (byte) -24);
        Verify.assertInstanceOf(UnmodifiableByteSet.class, set.asUnmodifiable());
        Assertions.assertEquals(new UnmodifiableByteSet(set), set.asUnmodifiable());
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(ByteSets.class);
    }
}
