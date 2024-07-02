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

import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.RandomAccess;

import com.gs.collections.api.list.MutableList;
import com.gs.collections.impl.block.factory.IntegerPredicates;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.parallel.ParallelIterate;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CompositeFastListTest extends AbstractListTestCase
{
    @Override
    protected <T> MutableList<T> newWith(T... littleElements)
    {
        CompositeFastList<T> result = new CompositeFastList<>();
        for (T element : littleElements)
        {
            result.add(element);
        }
        return result;
    }

    @Override
    @Test
    public void testClone()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.newWith().clone());
    }

    @Test
    public void size()
    {
        CompositeFastList<String> list = new CompositeFastList<>();
        Verify.assertSize(0, list);
        list.add("1");
        Verify.assertSize(1, list);
        list.addAll(FastList.newListWith("1", "1", "1", "1"));
        Verify.assertSize(5, list);
        list.remove(1);
        Verify.assertSize(4, list);
        list.remove("1");
        Verify.assertSize(3, list);
        list.add(3, "1");
        Verify.assertSize(4, list);
        ListIterator<String> listIterator = list.listIterator();
        listIterator.add("1");
        Verify.assertSize(5, list);
        listIterator.next();
        listIterator.remove();
        Verify.assertSize(4, list);
        Iterator<String> iterator = list.iterator();
        iterator.next();
        iterator.remove();
        Verify.assertSize(3, list);
        list.removeAll(FastList.newListWith("1"));
        Verify.assertSize(0, list);
        list.addAll(FastList.newListWith("1", "2", "3", "4"));
        list.addAll(FastList.newListWith("1", "2", "3", "4"));
        Verify.assertSize(8, list);
        list.clear();
        Verify.assertSize(0, list);
        CompositeFastList<String> list2 = new CompositeFastList<>();
        Verify.assertSize(0, list2);
        list2.listIterator().add("1");
        Verify.assertSize(1, list2);
    }

    @Test
    public void testDefaultConstructor()
    {
        MutableList<String> list = new CompositeFastList<>();
        list.add("1");
        list.add("2");
        Verify.assertSize(2, list);
        Verify.assertContains(list, "1");
    }

    @Test
    public void parallelBatchForEach()
    {
        MutableList<Integer> integers = Interval.oneTo(1_000_000).toList().shuffleThis();

        Collection<Integer> evens = ParallelIterate.select(integers, IntegerPredicates.isEven());
        Verify.assertInstanceOf(CompositeFastList.class, evens);
        Collection<Integer> evens2 = ParallelIterate.select(evens, e -> e <= 100_000);
        Verify.assertInstanceOf(CompositeFastList.class, evens2);
        Verify.assertSize(50_000, evens2);
        Collection<String> evenStrings = ParallelIterate.collect(evens2, Object::toString);
        Verify.assertInstanceOf(CompositeFastList.class, evenStrings);
        Verify.assertSize(50_000, evenStrings);
        Assertions.assertEquals(integers.select(e -> e <= 100_000).select(IntegerPredicates.isEven()).collect(Object::toString).toList(), evenStrings);

        Collection<Integer> odds = ParallelIterate.select(integers, IntegerPredicates.isOdd());
        Verify.assertInstanceOf(CompositeFastList.class, odds);
        Collection<Integer> odds2 = ParallelIterate.select(odds, e -> e <= 100_000);
        Verify.assertInstanceOf(CompositeFastList.class, odds2);
        Verify.assertSize(50_000, odds2);
        Collection<String> oddStrings = ParallelIterate.collect(odds2, Object::toString);
        Verify.assertInstanceOf(CompositeFastList.class, oddStrings);
        Verify.assertSize(50_000, oddStrings);
        Assertions.assertEquals(integers.select(e -> e <= 100_000).select(IntegerPredicates.isOdd()).collect(Object::toString).toList(), oddStrings);

        MutableList<Integer> range = Interval.fromTo(-1_234_567, 1_234_567).toList().shuffleThis();
        Collection<Integer> positives = ParallelIterate.select(range, IntegerPredicates.isPositive());
        Verify.assertInstanceOf(CompositeFastList.class, positives);
        Verify.assertSize(1_234_567, positives);
        Collection<Integer> evenPositives = ParallelIterate.select(positives, IntegerPredicates.isEven());
        Verify.assertSize(617_283, evenPositives);
        Assertions.assertEquals(2000, ParallelIterate.count(evenPositives, e -> e <= 4000));
        Collection<Integer> oddPositives = ParallelIterate.select(positives, IntegerPredicates.isOdd());
        Verify.assertSize(617_284, oddPositives);
        Assertions.assertEquals(2000, ParallelIterate.count(oddPositives, e -> e <= 4000));

        Collection<Integer> negatives = ParallelIterate.select(range, IntegerPredicates.isNegative());
        Verify.assertInstanceOf(CompositeFastList.class, negatives);
        Verify.assertSize(1_234_567, negatives);
        Collection<Integer> evenNegatives = ParallelIterate.select(negatives, IntegerPredicates.isEven());
        Verify.assertSize(617_283, evenNegatives);
        Assertions.assertEquals(2000, ParallelIterate.count(evenNegatives, e -> e >= -4000));
        Collection<Integer> oddNegatives = ParallelIterate.select(negatives, IntegerPredicates.isOdd());
        Verify.assertSize(617_284, oddNegatives);
        Assertions.assertEquals(2000, ParallelIterate.count(oddNegatives, e -> e >= -4000));
    }

    @Test
    public void testGet()
    {
        MutableList<String> list = new CompositeFastList<>();
        list.addAll(FastList.newListWith("1", "2", "3", "4"));
        list.addAll(FastList.newListWith("A", "B", "C", "B"));
        list.addAll(FastList.newListWith("Cat", "Dog", "Mouse", "Bird"));
        Assertions.assertEquals("1", list.get(0));
        Assertions.assertEquals("2", list.get(1));
        Assertions.assertEquals("A", list.get(4));
        Assertions.assertEquals("4", list.get(3));
        Assertions.assertEquals("Cat", list.get(8));
        Assertions.assertEquals("Bird", list.get(11));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> list.get(12));
    }

    @Test
    public void testAddWithIndex()
    {
        MutableList<String> list = new CompositeFastList<>();
        list.addAll(FastList.newListWith("1", "2", "3", "4"));
        list.addAll(FastList.newListWith("A", "B", "C", "B"));
        list.add(3, "NEW");
        Verify.assertSize(9, list);
        Assertions.assertEquals("NEW", list.get(3));
        Assertions.assertEquals("4", list.get(4));
        list.add(0, "START");
        Verify.assertSize(10, list);
        Assertions.assertEquals("START", list.getFirst());
        list.add(10, "END");
        Verify.assertSize(11, list);
        Assertions.assertEquals("END", list.getLast());
    }

    @Override
    @Test
    public void reverseThis()
    {
        super.reverseThis();
        CompositeFastList<Integer> composite = new CompositeFastList<>();
        composite.addAll(FastList.newListWith(9, 8, 7));
        composite.addAll(FastList.newListWith(6, 5, 4));
        composite.addAll(FastList.newListWith(3, 2, 1));
        CompositeFastList<Integer> reversed = composite.reverseThis();
        Assertions.assertSame(composite, reversed);
        Assertions.assertEquals(Interval.oneTo(9), reversed);
    }

    @Override
    @Test
    public void addAllAtIndex()
    {
        Verify.assertThrows(UnsupportedOperationException.class, CompositeFastListTest.super::addAllAtIndex);
    }

    @Override
    @Test
    public void set()
    {
        super.set();
        MutableList<String> list = new CompositeFastList<>();
        list.addAll(FastList.newListWith("1", "2", "3", "4"));
        list.addAll(FastList.newListWith("A", "B", "C", "B"));
        Assertions.assertEquals("1", list.set(0, "NEW"));
        Verify.assertSize(8, list);
        Assertions.assertEquals("NEW", list.getFirst());
        Assertions.assertEquals("2", list.get(1));
        Assertions.assertEquals("B", list.set(7, "END"));
        Verify.assertSize(8, list);
        Assertions.assertEquals("END", list.getLast());
    }

    @Test
    public void set_bugFix_off_by_one_error()
    {
        CompositeFastList<Integer> compositeList = new CompositeFastList<>();
        MutableList<Integer> list1 = FastList.newListWith(1, 2, 3);
        MutableList<Integer> list2 = FastList.newListWith(4, 5);
        MutableList<Integer> list3 = FastList.newList();

        compositeList.addAll(list1);
        compositeList.addAll(list2);
        compositeList.addAll(list3);

        Assertions.assertEquals(Integer.valueOf(4), compositeList.get(3));
        Assertions.assertEquals(Integer.valueOf(4), compositeList.set(3, 99));
        Assertions.assertEquals(Integer.valueOf(99), compositeList.get(3));
    }

    @Override
    @Test
    public void indexOf()
    {
        super.indexOf();
        MutableList<String> list = new CompositeFastList<>();
        list.addAll(FastList.newListWith("1", "2", "3", "4"));
        list.addAll(FastList.newListWith("3", "B", "3", "B"));
        list.addAll(FastList.newListWith("3", "B", "3", "X"));

        Assertions.assertEquals(2, list.indexOf("3"));
        Assertions.assertEquals(5, list.indexOf("B"));
        Assertions.assertEquals(11, list.indexOf("X"));

        Assertions.assertEquals(-1, list.indexOf("missing"));
    }

    @Override
    @Test
    public void lastIndexOf()
    {
        super.lastIndexOf();
        MutableList<String> list = new CompositeFastList<>();
        list.addAll(FastList.newListWith("1", "2", "3", "4"));
        list.addAll(FastList.newListWith("3", "B", "3", "B"));
        Assertions.assertEquals(6, list.lastIndexOf("3"));
        Assertions.assertEquals(3, list.lastIndexOf("4"));
        Assertions.assertEquals(-1, list.lastIndexOf("missing"));
    }

    @Test
    public void testRemoveWithIndex()
    {
        MutableList<String> list = new CompositeFastList<>();
        list.addAll(FastList.newListWith("1", "2", "3", "4"));
        list.addAll(FastList.newListWith("3", "B", "3", "B"));
        Assertions.assertEquals("1", list.remove(0));
        Verify.assertSize(7, list);
        Assertions.assertEquals("2", list.getFirst());
        Assertions.assertEquals("B", list.remove(6));
        Verify.assertSize(6, list);
        Assertions.assertEquals("3", list.getLast());
    }

    @Override
    @Test
    public void toArray()
    {
        super.toArray();
        MutableList<String> list = new CompositeFastList<>();
        list.addAll(FastList.newListWith("1", "2", "3", "4"));
        list.addAll(Lists.mutable.<String>of());
        list.addAll(FastList.newListWith("3", "B", "3", "B"));
        list.addAll(Lists.mutable.<String>of());
        Assertions.assertArrayEquals(new String[]{"1", "2", "3", "4", "3", "B", "3", "B"}, list.toArray());
    }

    @Test
    public void testEmptyIterator()
    {
        Assertions.assertFalse(new CompositeFastList<String>().iterator().hasNext());
    }

    @Override
    @Test
    public void clear()
    {
        super.clear();
        MutableList<String> list = new CompositeFastList<>();
        list.addAll(FastList.newListWith("1", "2", "3", "4"));
        list.addAll(FastList.newListWith("3", "B", "3", "B"));
        list.clear();
        Assertions.assertTrue(list.isEmpty());
        Assertions.assertEquals(0, list.size());
    }

    @Test
    public void testContainsAll()
    {
        MutableList<String> list = new CompositeFastList<>();
        list.addAll(FastList.newListWith("1", "2", "3", "4"));
        list.addAll(FastList.newListWith("3", "B", "3", "B"));
        Assertions.assertTrue(list.containsAll(FastList.newList().with("2", "B")));
    }

    @Override
    @Test
    public void removeAll()
    {
        super.removeAll();
        MutableList<String> list = new CompositeFastList<>();
        list.addAll(FastList.newListWith("1", "2", "3", "4"));
        list.addAll(FastList.newListWith("3", "B", "3", "B"));
        list.removeAll(FastList.newList().with("2", "B"));
        Verify.assertSize(5, list);
    }

    @Override
    @Test
    public void retainAll()
    {
        super.retainAll();
        MutableList<String> list = new CompositeFastList<>();
        list.addAll(FastList.newListWith("1", "2", "3", "4"));
        list.addAll(FastList.newListWith("3", "B", "3", "B"));
        list.retainAll(FastList.newList().with("2", "B"));
        Verify.assertSize(3, list);
    }

    @Override
    @Test
    public void forEach()
    {
        super.forEach();
        MutableList<Integer> list = FastList.newList();
        CompositeFastList<Integer> iterables = new CompositeFastList<>();
        iterables.addComposited(Interval.oneTo(5).toList());
        iterables.addComposited(Interval.fromTo(6, 10).toList());
        iterables.forEach(CollectionAddProcedure.on(list));
        Verify.assertSize(10, list);
        Verify.assertAllSatisfy(list, Predicates.greaterThan(0).and(Predicates.lessThan(11)));
    }

    @Override
    @Test
    public void forEachWithIndex()
    {
        super.forEachWithIndex();

        MutableList<Integer> list = FastList.newList();
        CompositeFastList<Integer> iterables = new CompositeFastList<>();
        iterables.addComposited(Interval.fromTo(6, 10).toList());
        iterables.addComposited(Interval.oneTo(5).toList());
        iterables.forEachWithIndex((each, index) -> list.add(index, each));
        Verify.assertSize(10, list);
        Verify.assertAllSatisfy(list, Predicates.greaterThan(0).and(Predicates.lessThan(11)));
        Verify.assertStartsWith(list, 6, 7, 8, 9, 10, 1, 2, 3, 4, 5);
    }

    @Override
    @Test
    public void forEachWith()
    {
        super.forEachWith();
        MutableList<Integer> list = FastList.newList();
        CompositeFastList<Integer> iterables = new CompositeFastList<>();
        iterables.addComposited(Interval.fromTo(6, 10).toList());
        iterables.addComposited(Interval.oneTo(5).toList());
        iterables.forEachWith((each, parameter) -> list.add(parameter.intValue(), each), 0);
        Verify.assertSize(10, list);
        Verify.assertAllSatisfy(list, Predicates.greaterThan(0).and(Predicates.lessThan(11)));
        Verify.assertStartsWith(list, 5, 4, 3, 2, 1, 10, 9, 8, 7, 6);
    }

    @Test
    public void testEquals()
    {
        CompositeFastList<String> composite = new CompositeFastList<>();
        MutableList<String> list = FastList.newList();

        Verify.assertEqualsAndHashCode(composite, list);

        MutableList<String> list2 = FastList.newListWith("one", "two", "three");

        CompositeFastList<String> composite2 = new CompositeFastList<>();
        MutableList<String> firstBit = FastList.newListWith("one", "two");
        MutableList<String> secondBit = FastList.newListWith("three");
        composite2.addAll(firstBit);
        composite2.addAll(secondBit);

        Verify.assertEqualsAndHashCode(list2, composite2);

        Assertions.assertNotEquals(firstBit, composite2);
        Assertions.assertNotEquals(composite2, firstBit);

        MutableList<String> list1 = FastList.newListWith("one", null, "three");

        CompositeFastList<String> composite1 = new CompositeFastList<>();
        MutableList<String> firstBit1 = FastList.newListWith("one", null);
        MutableList<String> secondBit1 = FastList.newListWith("three");
        composite1.addAll(firstBit1);
        composite1.addAll(secondBit1);

        Verify.assertEqualsAndHashCode(list1, composite1);
    }

    @Test
    public void testHashCode()
    {
        CompositeFastList<String> composite = new CompositeFastList<>();
        MutableList<String> list = FastList.newList();
        Verify.assertEqualsAndHashCode(composite, list);

        MutableList<String> list2 = FastList.newListWith("one", "two", "three");

        CompositeFastList<String> composite2 = new CompositeFastList<>();
        MutableList<String> firstBit = FastList.newListWith("one", "two");
        MutableList<String> secondBit = FastList.newListWith("three");
        composite2.addAll(firstBit);
        composite2.addAll(secondBit);

        Verify.assertEqualsAndHashCode(list2, composite2);

        MutableList<String> list1 = FastList.newListWith("one", null, "three");

        CompositeFastList<String> composite1 = new CompositeFastList<>();
        MutableList<String> firstBit1 = FastList.newListWith("one", null);
        MutableList<String> secondBit1 = FastList.newListWith("three");
        composite1.addAll(firstBit1);
        composite1.addAll(secondBit1);

        Verify.assertEqualsAndHashCode(list1, composite1);
    }

    @Override
    @Test
    public void listIterator()
    {
        super.listIterator();
        CompositeFastList<String> composite = new CompositeFastList<>();
        FastList<String> firstBit = FastList.newListWith("one", "two");
        FastList<String> secondBit = FastList.newListWith("three");
        composite.addAll(firstBit);
        composite.addAll(secondBit);

        ListIterator<String> listIterator = composite.listIterator();
        listIterator.add("four");
        Verify.assertSize(4, composite);
        Assertions.assertTrue(listIterator.hasNext());
        String element = listIterator.next();

        Assertions.assertEquals("one", element);

        String element3 = listIterator.next();

        Assertions.assertEquals("two", element3);

        String element2 = listIterator.previous();
        Assertions.assertEquals("two", element2);

        String element1 = listIterator.next();

        Assertions.assertEquals("two", element1);

        listIterator.remove();

        Verify.assertSize(3, composite);
    }

    @Override
    @Test
    public void subList()
    {
        MutableList<String> list = this.newWith("A", "B", "C", "D");
        MutableList<String> sublist = list.subList(1, 3);
        Verify.assertPostSerializedEqualsAndHashCode(sublist);
        Verify.assertSize(2, sublist);
        Verify.assertContainsAll(sublist, "B", "C");
        sublist.add("X");
        Verify.assertSize(3, sublist);
        Verify.assertContainsAll(sublist, "B", "C", "X");
        Verify.assertSize(5, list);
        Verify.assertContainsAll(list, "A", "B", "C", "X", "D");
        sublist.remove("X");
        Verify.assertContainsAll(sublist, "B", "C");
        Verify.assertContainsAll(list, "A", "B", "C", "D");
        Assertions.assertEquals("C", sublist.set(1, "R"));
        Verify.assertContainsAll(sublist, "B", "R");
        Verify.assertContainsAll(list, "A", "B", "R", "D");
        sublist.clear();
        Verify.assertEmpty(sublist);
        Verify.assertContainsAll(list, "A", "D");
    }

    @Test
    public void notRandomAccess()
    {
        Assertions.assertFalse(this.newWith() instanceof RandomAccess);
    }

    @Test
    public void removingFromIteratorIsCool()
    {
        CompositeFastList<String> undertest = new CompositeFastList<>();
        undertest.addAll(FastList.newListWith("a"));
        undertest.addAll(FastList.newListWith("b", "c", "d"));

        Iterator<String> iterator1 = undertest.iterator();
        iterator1.next();
        iterator1.next();
        iterator1.next();
        iterator1.remove();
        Assertions.assertEquals("d", iterator1.next());
        Assertions.assertEquals(FastList.newListWith("a", "b", "d"), undertest);

        Iterator<String> iterator2 = undertest.iterator();
        iterator2.next();
        iterator2.next();
        iterator2.remove();
        Assertions.assertEquals(FastList.newListWith("a", "d"), undertest);

        Iterator<String> iterator3 = undertest.iterator();
        iterator3.next();
        iterator3.remove();
        Assertions.assertEquals(FastList.newListWith("d"), undertest);
        iterator3.next();
        iterator3.remove();
        Assertions.assertEquals(FastList.newList(), undertest);
    }

    @Test
    public void removingFromIteratorIsUncoolFromEmptyIterator()
    {
        assertThrows(IllegalStateException.class, () -> {
            new CompositeFastList<String>().iterator().remove();
        });
    }
}
