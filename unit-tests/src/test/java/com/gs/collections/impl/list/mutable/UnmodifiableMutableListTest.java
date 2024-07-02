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

package com.gs.collections.impl.list.mutable;

import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Random;

import com.gs.collections.api.LazyIterable;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.partition.list.PartitionMutableList;
import com.gs.collections.impl.Counter;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.test.SerializeTestHelper;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.gs.collections.impl.factory.Iterables.iList;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JUnit test for {@link UnmodifiableMutableList}.
 */
public class UnmodifiableMutableListTest
{
    private static final String LED_ZEPPELIN = "Led Zeppelin";
    private static final String METALLICA = "Metallica";

    private MutableList<String> mutableList;
    private MutableList<String> unmodifiableList;

    @BeforeEach
    public void setUp()
    {
        this.mutableList = Lists.mutable.of(METALLICA, "Bon Jovi", "Europe", "Scorpions");
        this.unmodifiableList = UnmodifiableMutableList.of(this.mutableList);
    }

    @Test
    public void equalsAndHashCode()
    {
        Verify.assertEqualsAndHashCode(this.mutableList, this.unmodifiableList);
        Verify.assertPostSerializedEqualsAndHashCode(this.unmodifiableList);
        Verify.assertInstanceOf(UnmodifiableMutableList.class, SerializeTestHelper.serializeDeserialize(this.unmodifiableList));
    }

    @Test
    public void delegatingMethods()
    {
        Verify.assertItemAtIndex(2, this.unmodifiableList, "Europe");
        Assertions.assertEquals(2, this.unmodifiableList.indexOf("Europe"));
        Assertions.assertEquals(0, this.unmodifiableList.lastIndexOf(METALLICA));
    }

    @Test
    public void forEachFromTo()
    {
        Counter counter = new Counter();
        this.unmodifiableList.forEach(1, 2, band -> counter.increment());
        Assertions.assertEquals(2, counter.getCount());
    }

    @Test
    public void listIterator()
    {
        ListIterator<String> it = this.unmodifiableList.listIterator();
        Assertions.assertFalse(it.hasPrevious());
        Assertions.assertEquals(-1, it.previousIndex());
        Assertions.assertEquals(METALLICA, it.next());
        Assertions.assertTrue(it.hasNext());
        Assertions.assertEquals(1, it.nextIndex());

        Verify.assertThrows(UnsupportedOperationException.class, () -> it.set("Rick Astley"));

        Verify.assertThrows(UnsupportedOperationException.class, it::remove);

        Verify.assertThrows(UnsupportedOperationException.class, () -> it.add("Gloria Gaynor"));

        Assertions.assertEquals(METALLICA, it.previous());
    }

    @Test
    public void sortThis()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.unmodifiableList.sortThis());
    }

    @Test
    public void sortThisWithComparator()
    {
        Verify.assertThrows(
                UnsupportedOperationException.class,
                () -> this.unmodifiableList.sortThis(String::compareTo));
    }

    @Test
    public void sortThisBy()
    {
        Verify.assertThrows(
                UnsupportedOperationException.class,
                () -> this.unmodifiableList.sortThisBy(Functions.getStringToInteger()));
    }

    @Test
    public void sortThisByBoolean()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.unmodifiableList.sortThisByBoolean(null));
    }

    @Test
    public void sortThisByChar()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.unmodifiableList.sortThisByChar(null));
    }

    @Test
    public void sortThisByByte()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.unmodifiableList.sortThisByByte(null));
    }

    @Test
    public void sortThisByShort()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.unmodifiableList.sortThisByShort(null));
    }

    @Test
    public void sortThisByInt()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.unmodifiableList.sortThisByInt(null));
    }

    @Test
    public void sortThisByFloat()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.unmodifiableList.sortThisByFloat(null));
    }

    @Test
    public void sortThisByLong()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.unmodifiableList.sortThisByLong(null));
    }

    @Test
    public void sortThisByDouble()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.unmodifiableList.sortThisByDouble(null));
    }

    @Test
    public void shuffleThis()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.unmodifiableList.shuffleThis(null));
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.unmodifiableList.shuffleThis(new Random(4)));
    }

    @Test
    public void reverseThis()
    {
        Verify.assertThrows(
                UnsupportedOperationException.class,
                () -> this.unmodifiableList.reverseThis());
    }

    @Test
    public void addAllAtIndex()
    {
        Verify.assertThrows(
                UnsupportedOperationException.class,
                () -> this.unmodifiableList.addAll(0, Lists.mutable.of("Madonna")));
    }

    @Test
    public void set()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.unmodifiableList.set(0, "Madonna"));
    }

    @Test
    public void addAtIndex()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.unmodifiableList.add(0, "Madonna"));
    }

    @Test
    public void removeFromIndex()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.unmodifiableList.remove(0));
    }

    @Test
    public void subList()
    {
        MutableList<String> subList = this.unmodifiableList.subList(1, 3);
        Assertions.assertEquals(Lists.immutable.of("Bon Jovi", "Europe"), subList);
        Verify.assertThrows(UnsupportedOperationException.class, subList::clear);
    }

    @Test
    public void newEmpty()
    {
        MutableList<String> list = this.unmodifiableList.newEmpty();
        list.add(LED_ZEPPELIN);
        Verify.assertContains(list, LED_ZEPPELIN);
    }

    @Test
    public void toImmutable()
    {
        Verify.assertInstanceOf(ImmutableList.class, this.unmodifiableList.toImmutable());
        Assertions.assertEquals(this.unmodifiableList, this.unmodifiableList.toImmutable());
    }

    @Test
    public void asUnmodifiable()
    {
        Assertions.assertSame(this.unmodifiableList, this.unmodifiableList.asUnmodifiable());
    }

    @Test
    public void asSynchronized()
    {
        MutableList<String> synchronizedList = this.unmodifiableList.asSynchronized();
        Verify.assertInstanceOf(SynchronizedMutableList.class, synchronizedList);
        Verify.assertThrows(UnsupportedOperationException.class, () -> {
            Iterator<String> iterator = synchronizedList.iterator();
            iterator.next();
            iterator.remove();
        });
    }

    @Test
    public void asReversed()
    {
        LazyIterable<String> lazyIterable = this.unmodifiableList.asReversed();
        Verify.assertThrows(UnsupportedOperationException.class, () -> {
            Iterator<String> iterator = lazyIterable.iterator();
            iterator.next();
            iterator.remove();
        });
    }

    @Test
    public void toReversed()
    {
        Assertions.assertEquals(Lists.mutable.ofAll(this.unmodifiableList).toReversed(), this.unmodifiableList.toReversed());
    }

    @Test
    public void selectInstancesOf()
    {
        MutableList<Number> numbers = UnmodifiableMutableList.of(FastList.<Number>newListWith(1, 2.0, 3, 4.0, 5));
        Assertions.assertEquals(iList(1, 3, 5), numbers.selectInstancesOf(Integer.class));
        Assertions.assertEquals(iList(1, 2.0, 3, 4.0, 5), numbers.selectInstancesOf(Number.class));
    }

    @Test
    public void distinct()
    {
        MutableList<Integer> list = UnmodifiableMutableList.of(FastList.newListWith(3, 1, 2, 2, 1, 3));
        Verify.assertListsEqual(FastList.newListWith(3, 1, 2), list.distinct());
    }

    @Test
    public void distinctWithHashingStrategy()
    {
        MutableList<String> letters = UnmodifiableMutableList.of(FastList.<String>newListWith("a", "A", "b", "C", "b", "D", "E", "e"));
        MutableList<String> expectedLetters = UnmodifiableMutableList.of(FastList.<String>newListWith("a", "b", "C", "D", "E"));
        Verify.assertListsEqual(letters.distinct(HashingStrategies.fromFunction(String::toLowerCase)), expectedLetters);
    }

    @Test
    public void take()
    {
        UnmodifiableMutableList<Integer> unmodifiableList = UnmodifiableMutableList.of(FastList.newListWith(1, 2, 3, 4, 5));
        Assertions.assertEquals(iList(), unmodifiableList.take(0));
        Assertions.assertEquals(iList(1, 2, 3), unmodifiableList.take(3));
        Assertions.assertEquals(iList(1, 2, 3, 4), unmodifiableList.take(unmodifiableList.size() - 1));
        Assertions.assertEquals(iList(1, 2, 3, 4, 5), unmodifiableList.take(unmodifiableList.size()));
        Assertions.assertEquals(iList(1, 2, 3, 4, 5), unmodifiableList.take(10));
        Assertions.assertEquals(iList(1, 2, 3, 4, 5), unmodifiableList.take(Integer.MAX_VALUE));
        Assertions.assertNotSame(unmodifiableList, unmodifiableList.take(Integer.MAX_VALUE));
    }

    @Test
    public void take_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            UnmodifiableMutableList.of(FastList.newListWith(1, 2, 3, 4, 5)).take(-1);
        });
    }

    @Test
    public void takeWhile()
    {
        Assertions.assertEquals(
                iList(1, 2, 3),
                UnmodifiableMutableList.of(FastList.newListWith(1, 2, 3, 4, 5)).takeWhile(Predicates.lessThan(4)));
    }

    @Test
    public void drop()
    {
        UnmodifiableMutableList<Integer> unmodifiableList = UnmodifiableMutableList.of(FastList.newListWith(1, 2, 3, 4, 5));
        Assertions.assertEquals(iList(1, 2, 3, 4, 5), unmodifiableList.drop(0));
        Assertions.assertNotSame(unmodifiableList, unmodifiableList.drop(0));
        Assertions.assertEquals(iList(4, 5), unmodifiableList.drop(3));
        Assertions.assertEquals(iList(5), unmodifiableList.drop(unmodifiableList.size() - 1));
        Assertions.assertEquals(iList(), unmodifiableList.drop(unmodifiableList.size()));
        Assertions.assertEquals(iList(), unmodifiableList.drop(10));
        Assertions.assertEquals(iList(), unmodifiableList.drop(Integer.MAX_VALUE));
    }

    @Test
    public void drop_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            UnmodifiableMutableList.of(FastList.newListWith(1, 2, 3, 4, 5)).drop(-1);
        });
    }

    @Test
    public void dropWhile()
    {
        Assertions.assertEquals(
                iList(4, 5),
                UnmodifiableMutableList.of(FastList.newListWith(1, 2, 3, 4, 5)).dropWhile(Predicates.lessThan(4)));
    }

    @Test
    public void partitionWhile()
    {
        PartitionMutableList<Integer> partition = UnmodifiableMutableList.of(FastList.newListWith(1, 2, 3, 4, 5)).partitionWhile(Predicates.lessThan(4));
        MutableList<Integer> selected = partition.getSelected();
        MutableList<Integer> rejected = partition.getRejected();

        Assertions.assertEquals(iList(1, 2, 3), selected);
        Assertions.assertEquals(iList(4, 5), rejected);
    }

    @Test
    public void binarySearch()
    {
        UnmodifiableMutableList<Integer> sortedList = UnmodifiableMutableList.of(FastList.newListWith(1, 2, 3, 4, 5, 7));
        Assertions.assertEquals(1, sortedList.binarySearch(2));
        Assertions.assertEquals(-6, sortedList.binarySearch(6));

        for (Integer integer : sortedList)
        {
            Assertions.assertEquals(
                    Collections.binarySearch(sortedList, integer),
                    sortedList.binarySearch(integer));
        }
    }

    @Test
    public void binarySearchWithComparator()
    {
        UnmodifiableMutableList<Integer> sortedList = UnmodifiableMutableList.of(FastList.newListWith(1, 2, 3, 4, 5, 7)
                .toSortedList(Comparators.reverseNaturalOrder()));
        Assertions.assertEquals(sortedList.size() - 1, sortedList.binarySearch(1, Comparators.reverseNaturalOrder()));
        Assertions.assertEquals(-1 - sortedList.size(), sortedList.binarySearch(-1, Comparators.reverseNaturalOrder()));

        for (Integer integer : sortedList)
        {
            Assertions.assertEquals(
                    Collections.binarySearch(sortedList, integer, Comparators.reverseNaturalOrder()),
                    sortedList.binarySearch(integer, Comparators.reverseNaturalOrder()));
        }
    }
}
