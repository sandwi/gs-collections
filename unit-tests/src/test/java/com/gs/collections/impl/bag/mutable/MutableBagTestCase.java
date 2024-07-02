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

package com.gs.collections.impl.bag.mutable;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.gs.collections.api.bag.Bag;
import com.gs.collections.api.bag.ImmutableBagIterable;
import com.gs.collections.api.bag.MutableBagIterable;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.partition.PartitionMutableCollection;
import com.gs.collections.api.tuple.primitive.ObjectIntPair;
import com.gs.collections.impl.block.factory.IntegerPredicates;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.factory.primitive.IntPredicates;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.collection.mutable.AbstractCollectionTestCase;
import com.gs.collections.impl.factory.Bags;
import com.gs.collections.impl.factory.Iterables;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.math.IntegerSum;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.primitive.PrimitiveTuples;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class MutableBagTestCase extends AbstractCollectionTestCase
{
    @Override
    protected abstract <T> MutableBagIterable<T> newWith(T... littleElements);

    protected abstract <T> MutableBagIterable<T> newWithOccurrences(ObjectIntPair<T>... elementsWithOccurrences);

    @Test
    @Override
    public void equalsAndHashCode()
    {
        super.equalsAndHashCode();
        Assertions.assertNotEquals(this.newWith(1, 1, 2, 3), this.newWith(1, 2, 2, 3));
        Verify.assertEqualsAndHashCode(this.newWith(null, null, 2, 3), this.newWith(null, 2, null, 3));
        Assertions.assertEquals(this.newWith(1, 1, 2, 3).toMapOfItemToCount().hashCode(), this.newWith(1, 1, 2, 3).hashCode());
        Assertions.assertEquals(this.newWith(null, null, 2, 3).toMapOfItemToCount().hashCode(), this.newWith(null, null, 2, 3).hashCode());
    }

    @Test
    public void toStringOfItemToCount()
    {
        Assertions.assertEquals("{}", this.newWith().toStringOfItemToCount());
        Assertions.assertEquals("{1=3}", this.newWith(1, 1, 1).toStringOfItemToCount());
        String actual = this.newWith(1, 2, 2).toStringOfItemToCount();
        Assertions.assertTrue("{1=1, 2=2}".equals(actual) || "{2=2, 1=1}".equals(actual));
    }

    @Test
    public void toMapOfItemToCount()
    {
        MutableBagIterable<Integer> bag = this.newWith(1, 2, 2, 3, 3, 3);
        Assertions.assertEquals(UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3), bag.toMapOfItemToCount());
    }

    @Test
    public void add()
    {
        MutableBagIterable<Integer> bag = this.newWith();
        bag.add(1);
        bag.add(1);
        Verify.assertSize(2, bag);
        bag.add(1);
        Verify.assertSize(3, bag);
    }

    @Override
    @Test
    public void iterator()
    {
        MutableBagIterable<Integer> bag = this.newWith(1, 1, 2);
        MutableList<Integer> validate = Lists.mutable.of();
        for (Integer each : bag)
        {
            validate.add(each);
        }
        Assertions.assertEquals(HashBag.newBagWith(1, 1, 2), HashBag.newBag(validate));

        Iterator<Integer> iterator = bag.iterator();
        MutableBagIterable<Integer> expected = this.newWith(1, 1, 2);
        Verify.assertThrows(IllegalStateException.class, iterator::remove);

        this.assertIteratorRemove(bag, iterator, expected);
        this.assertIteratorRemove(bag, iterator, expected);
        this.assertIteratorRemove(bag, iterator, expected);
        Verify.assertEmpty(bag);
        Assertions.assertFalse(iterator.hasNext());
        Verify.assertThrows(NoSuchElementException.class, (Runnable) iterator::next);
    }

    private void assertIteratorRemove(MutableBagIterable<Integer> bag, Iterator<Integer> iterator, MutableBagIterable<Integer> expected)
    {
        Assertions.assertTrue(iterator.hasNext());
        Integer first = iterator.next();
        iterator.remove();
        expected.remove(first);
        Assertions.assertEquals(expected, bag);
        Verify.assertThrows(IllegalStateException.class, iterator::remove);
    }

    @Test
    public void iteratorRemove()
    {
        MutableBagIterable<Integer> bag = this.newWith(1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4);
        Iterator<Integer> iterator = bag.iterator();
        iterator.next();
        iterator.next();
        Integer value = iterator.next();
        Integer value2 = iterator.next();
        Assertions.assertNotEquals(value, value2);
        iterator.remove();
        Integer value3 = iterator.next();
        Assertions.assertNotEquals(value, value3);
        iterator.remove();
        Integer value4 = iterator.next();
        Assertions.assertNotEquals(value, value4);
        iterator.remove();
        Integer value5 = iterator.next();
        Assertions.assertNotEquals(value, value5);
    }

    @Test
    public void iteratorRemove2()
    {
        MutableBagIterable<Integer> bag = this.newWith(1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4);
        Iterator<Integer> iterator = bag.iterator();
        iterator.next();
        iterator.next();
        iterator.remove();
        iterator.next();
        iterator.next();
        iterator.remove();
        iterator.next();
        iterator.remove();
        iterator.next();
        iterator.next();
        iterator.remove();
        Assertions.assertEquals(4, bag.sizeDistinct());
        Assertions.assertEquals(8, bag.size());
    }

    @Override
    @Test
    public void removeIf()
    {
        super.removeIf();

        MutableBagIterable<Integer> objects = this.newWith(4, 1, 3, 3, 2);
        Assertions.assertTrue(objects.removeIf(Predicates.equal(2)));
        Assertions.assertEquals(HashBag.newBagWith(1, 3, 3, 4), objects);
        Assertions.assertTrue(objects.removeIf(Predicates.equal(3)));
        Assertions.assertEquals(HashBag.newBagWith(1, 4), objects);
    }

    @Override
    @Test
    public void forEach()
    {
        MutableBagIterable<Integer> bag = this.newWith(1, 1, 2);
        MutableList<Integer> validate = Lists.mutable.of();
        bag.forEach(CollectionAddProcedure.on(validate));
        Assertions.assertEquals(HashBag.newBagWith(1, 1, 2), HashBag.newBag(validate));
    }

    @Test
    public void forEachWithOccurrences()
    {
        MutableBagIterable<Integer> bag = this.newWith();
        bag.addOccurrences(1, 3);
        bag.addOccurrences(2, 2);
        bag.addOccurrences(3, 1);
        IntegerSum sum = new IntegerSum(0);
        bag.forEachWithOccurrences((each, index) -> sum.add(each * index));
        Assertions.assertEquals(10, sum.getIntSum());
        bag.removeOccurrences(2, 1);
        IntegerSum sum2 = new IntegerSum(0);
        bag.forEachWithOccurrences((each, index) -> sum2.add(each * index));
        Assertions.assertEquals(8, sum2.getIntSum());
        bag.removeOccurrences(1, 3);
        IntegerSum sum3 = new IntegerSum(0);
        bag.forEachWithOccurrences((each, index) -> sum3.add(each * index));
        Assertions.assertEquals(5, sum3.getIntSum());
    }

    @Override
    @Test
    public void toImmutable()
    {
        super.toImmutable();
        Verify.assertInstanceOf(MutableBagIterable.class, this.newWith());
        Verify.assertInstanceOf(ImmutableBagIterable.class, this.newWith().toImmutable());
        Assertions.assertFalse(this.newWith().toImmutable() instanceof MutableBagIterable);
    }

    @Test
    @Override
    public void getLast()
    {
        Assertions.assertEquals(Integer.valueOf(1), this.newWith(1).getLast());
        Assertions.assertEquals(Integer.valueOf(3), this.newWith(3).getLast());
    }

    @Test
    public void occurrencesOf()
    {
        MutableBagIterable<Integer> bag = this.newWith(1, 1, 2);
        Assertions.assertEquals(2, bag.occurrencesOf(1));
        Assertions.assertEquals(1, bag.occurrencesOf(2));
    }

    @Test
    public void addOccurrences()
    {
        MutableBagIterable<Object> bag = this.newWith();
        bag.addOccurrences(new Object(), 0);
        MutableBagTestCase.assertBagsEqual(HashBag.newBag(), bag);
    }

    @Test
    public void addOccurrences_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            this.newWith().addOccurrences(new Object(), -1);
        });
    }

    @Test
    public void removeOccurrences()
    {
        MutableBagIterable<String> bag = this.newWith("betamax-tape", "betamax-tape");
        MutableBagIterable<String> expected = HashBag.newBag(bag);

        Assertions.assertFalse(bag.removeOccurrences("dvd", 2));
        MutableBagTestCase.assertBagsEqual(expected, bag);

        Assertions.assertFalse(bag.removeOccurrences("dvd", 0));
        MutableBagTestCase.assertBagsEqual(expected, bag);

        Assertions.assertFalse(bag.removeOccurrences("betamax-tape", 0));
        MutableBagTestCase.assertBagsEqual(expected, bag);

        Assertions.assertTrue(bag.removeOccurrences("betamax-tape", 1));
        MutableBagTestCase.assertBagsEqual(HashBag.newBagWith("betamax-tape"), bag);

        Assertions.assertTrue(bag.removeOccurrences("betamax-tape", 10));
        MutableBagTestCase.assertBagsEqual(HashBag.<String>newBag(), bag);
    }

    @Test
    public void removeOccurrences_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            this.newWith().removeOccurrences(new Object(), -1);
        });
    }

    @Test
    public void setOccurrences()
    {
        MutableBagIterable<String> bag = this.newWith();
        MutableBagIterable<String> expected = this.newWith("betamax-tape", "betamax-tape");

        Assertions.assertTrue(bag.setOccurrences("betamax-tape", 2));
        MutableBagTestCase.assertBagsEqual(expected, bag);

        Assertions.assertFalse(bag.setOccurrences("betamax-tape", 2));
        MutableBagTestCase.assertBagsEqual(expected, bag);

        Assertions.assertFalse(bag.setOccurrences("dvd", 0));
        MutableBagTestCase.assertBagsEqual(expected, bag);

        Assertions.assertTrue(bag.setOccurrences("betamax-tape", 3));
        MutableBagTestCase.assertBagsEqual(expected.with("betamax-tape"), bag);

        Assertions.assertTrue(bag.setOccurrences("betamax-tape", 0));
        MutableBagTestCase.assertBagsEqual(HashBag.<String>newBag(), bag);
    }

    @Test
    public void setOccurrences_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            this.newWith().setOccurrences(new Object(), -1);
        });
    }

    protected static void assertBagsEqual(Bag<?> expected, Bag<?> actual)
    {
        Assertions.assertEquals(expected.toMapOfItemToCount(), actual.toMapOfItemToCount());
        Assertions.assertEquals(expected.sizeDistinct(), actual.sizeDistinct());
        Assertions.assertEquals(expected.size(), actual.size());
        Verify.assertEqualsAndHashCode(expected, actual);
    }

    @Test
    public void toSortedListWith()
    {
        Assertions.assertEquals(
                FastList.newListWith(1, 2, 2, 3, 3, 3),
                this.newWith(3, 3, 3, 2, 2, 1).toSortedList());
    }

    @Override
    @Test
    public void toSet()
    {
        super.toSet();
        MutableBagIterable<Integer> bag = this.newWith(3, 3, 3, 2, 2, 1);
        Assertions.assertEquals(UnifiedSet.newSetWith(1, 2, 3), bag.toSet());
    }

    @Override
    @Test
    public void toList()
    {
        super.toList();
        MutableBagIterable<Integer> bag = this.newWith(1, 1, 1);
        Assertions.assertEquals(FastList.newListWith(1, 1, 1), bag.toList());
    }

    @Override
    @Test
    public void removeObject()
    {
        super.removeObject();

        MutableBagIterable<String> bag = this.newWith("dakimakura", "dakimakura");
        Assertions.assertFalse(bag.remove("Mr. T"));
        Assertions.assertTrue(bag.remove("dakimakura"));
        Assertions.assertTrue(bag.remove("dakimakura"));
        Assertions.assertFalse(bag.remove("dakimakura"));
        MutableBagTestCase.assertBagsEqual(Bags.mutable.of(), bag);
    }

    @Override
    @Test
    public void asSynchronized()
    {
        Verify.assertInstanceOf(SynchronizedBag.class, this.newWith().asSynchronized());
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        Verify.assertInstanceOf(UnmodifiableBag.class, this.newWith().asUnmodifiable());
    }

    @Test
    public void serialization()
    {
        MutableBagIterable<String> bag = this.newWith("One", "Two", "Two", "Three", "Three", "Three");
        Verify.assertPostSerializedEqualsAndHashCode(bag);
    }

    @Override
    @Test
    public void partition()
    {
        super.partition();

        MutableBagIterable<Integer> integers = this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        PartitionMutableCollection<Integer> result = integers.partition(IntegerPredicates.isEven());
        Assertions.assertEquals(Iterables.iBag(2, 2, 4, 4, 4, 4), result.getSelected());
        Assertions.assertEquals(Iterables.iBag(1, 3, 3, 3), result.getRejected());
    }

    @Override
    @Test
    public void partitionWith()
    {
        super.partitionWith();

        MutableBagIterable<Integer> integers = this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        PartitionMutableCollection<Integer> result = integers.partitionWith(Predicates2.in(), integers.select(IntegerPredicates.isEven()));
        Assertions.assertEquals(Iterables.iBag(2, 2, 4, 4, 4, 4), result.getSelected());
        Assertions.assertEquals(Iterables.iBag(1, 3, 3, 3), result.getRejected());
    }

    @Test
    public void selectByOccurrences()
    {
        MutableBagIterable<Integer> integers = this.newWith(1, 1, 1, 1, 2, 2, 2, 3, 3, 4);
        Assertions.assertEquals(Iterables.iBag(1, 1, 1, 1, 3, 3), integers.selectByOccurrences(IntPredicates.isEven()));
    }

    @Test
    public void topOccurrences()
    {
        MutableBagIterable<String> strings = this.newWithOccurrences(
                PrimitiveTuples.pair("one", 1),
                PrimitiveTuples.pair("two", 2),
                PrimitiveTuples.pair("three", 3),
                PrimitiveTuples.pair("four", 4),
                PrimitiveTuples.pair("five", 5),
                PrimitiveTuples.pair("six", 6),
                PrimitiveTuples.pair("seven", 7),
                PrimitiveTuples.pair("eight", 8),
                PrimitiveTuples.pair("nine", 9),
                PrimitiveTuples.pair("ten", 10));
        MutableList<ObjectIntPair<String>> top5 = strings.topOccurrences(5);
        Verify.assertSize(5, top5);
        Assertions.assertEquals("ten", top5.getFirst().getOne());
        Assertions.assertEquals(10, top5.getFirst().getTwo());
        Assertions.assertEquals("six", top5.getLast().getOne());
        Assertions.assertEquals(6, top5.getLast().getTwo());
        Verify.assertSize(0, this.newWith("one").topOccurrences(0));
        Verify.assertSize(0, this.newWith().topOccurrences(5));
        Verify.assertSize(3, this.newWith("one", "two", "three").topOccurrences(5));
        Verify.assertSize(3, this.newWith("one", "two", "three").topOccurrences(1));
        Verify.assertSize(3, this.newWith("one", "two", "three").topOccurrences(2));
        Verify.assertSize(3, this.newWith("one", "one", "two", "three").topOccurrences(2));
        Verify.assertSize(2, this.newWith("one", "one", "two", "two", "three").topOccurrences(1));
        Verify.assertSize(3, this.newWith(null, "one", "two").topOccurrences(5));
        Verify.assertSize(3, this.newWith(null, "one", "two").topOccurrences(1));
        Verify.assertSize(3, this.newWith("one", "one", "two", "two", "three", "three").topOccurrences(1));
        Verify.assertSize(0, this.newWith().topOccurrences(0));
        Verify.assertSize(0, this.newWith("one").topOccurrences(0));
        Verify.assertThrows(IllegalArgumentException.class, () -> this.newWith().topOccurrences(-1));
    }

    @Test
    public void bottomOccurrences()
    {
        MutableBagIterable<String> strings = this.newWithOccurrences(
                PrimitiveTuples.pair("one", 1),
                PrimitiveTuples.pair("two", 2),
                PrimitiveTuples.pair("three", 3),
                PrimitiveTuples.pair("four", 4),
                PrimitiveTuples.pair("five", 5),
                PrimitiveTuples.pair("six", 6),
                PrimitiveTuples.pair("seven", 7),
                PrimitiveTuples.pair("eight", 8),
                PrimitiveTuples.pair("nine", 9),
                PrimitiveTuples.pair("ten", 10));
        MutableList<ObjectIntPair<String>> bottom5 = strings.bottomOccurrences(5);
        Verify.assertSize(5, bottom5);
        Assertions.assertEquals("one", bottom5.getFirst().getOne());
        Assertions.assertEquals(1, bottom5.getFirst().getTwo());
        Assertions.assertEquals("five", bottom5.getLast().getOne());
        Assertions.assertEquals(5, bottom5.getLast().getTwo());
        Verify.assertSize(0, this.newWith("one").bottomOccurrences(0));
        Verify.assertSize(0, this.newWith().bottomOccurrences(5));
        Verify.assertSize(3, this.newWith("one", "two", "three").topOccurrences(5));
        Verify.assertSize(3, this.newWith("one", "two", "three").topOccurrences(1));
        Verify.assertSize(3, this.newWith("one", "two", "three").topOccurrences(2));
        Verify.assertSize(3, this.newWith("one", "one", "two", "three").topOccurrences(2));
        Verify.assertSize(2, this.newWith("one", "one", "two", "two", "three").topOccurrences(1));
        Verify.assertSize(3, this.newWith(null, "one", "two").topOccurrences(5));
        Verify.assertSize(3, this.newWith(null, "one", "two").topOccurrences(1));
        Verify.assertSize(3, this.newWith("one", "one", "two", "two", "three", "three").bottomOccurrences(1));
        Verify.assertSize(0, this.newWith().bottomOccurrences(0));
        Verify.assertSize(0, this.newWith("one").bottomOccurrences(0));
        Verify.assertThrows(IllegalArgumentException.class, () -> this.newWith().bottomOccurrences(-1));
    }
}
