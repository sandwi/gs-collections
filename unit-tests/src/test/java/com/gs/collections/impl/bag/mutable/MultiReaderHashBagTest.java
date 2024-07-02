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

package com.gs.collections.impl.bag.mutable;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

import com.gs.collections.api.bag.ImmutableBag;
import com.gs.collections.api.bag.MutableBag;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.partition.PartitionMutableCollection;
import com.gs.collections.impl.block.factory.IntegerPredicates;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.factory.Procedures;
import com.gs.collections.impl.block.factory.primitive.IntPredicates;
import com.gs.collections.impl.block.function.AddFunction;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.factory.Bags;
import com.gs.collections.impl.factory.Sets;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.parallel.ParallelIterate;
import com.gs.collections.impl.set.mutable.MultiReaderMutableCollectionTestCase;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.SerializeTestHelper;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JUnit test for {@link MultiReaderHashBag}.
 */
public class MultiReaderHashBagTest extends MultiReaderMutableCollectionTestCase
{
    @Override
    protected <T> MultiReaderHashBag<T> newWith(T... littleElements)
    {
        return MultiReaderHashBag.newBagWith(littleElements);
    }

    @Override
    @Test
    public void newEmpty()
    {
        Verify.assertInstanceOf(MultiReaderHashBag.class, MultiReaderHashBag.newBag().newEmpty());
        Verify.assertEmpty(MultiReaderHashBag.<Integer>newBagWith(null, null).newEmpty());
    }

    @Test
    public void hashBagNewWith()
    {
        Assertions.assertEquals(
                HashBag.newBagWith("Alice", "Bob", "Bob", "Bob", "Cooper", "Dio"),
                HashBag.newBagWith("Alice", "Bob", "Cooper", "Dio", "Bob", "Bob"));
    }

    @Override
    @Test
    public void asSynchronized()
    {
        Verify.assertInstanceOf(SynchronizedBag.class, MultiReaderHashBag.newBag().asSynchronized());
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        Verify.assertInstanceOf(UnmodifiableBag.class, this.newWith().asUnmodifiable());
    }

    @Override
    @Test
    public void toImmutable()
    {
        Verify.assertInstanceOf(ImmutableBag.class, this.newWith().toImmutable());
    }

    @Test
    public void addOccurrences()
    {
        MultiReaderHashBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 1, 2, 3);
        bag.addOccurrences(1, 2);
        bag.addOccurrences(4, 2);
        bag.addOccurrences(2, 1);
        MutableBagTestCase.assertBagsEqual(HashBag.newBagWith(1, 1, 1, 1, 2, 2, 3, 4, 4), bag);
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
        MultiReaderHashBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 1, 1, 1, 2, 2, 3);
        Assertions.assertFalse(bag.removeOccurrences(4, 2));
        MutableBagTestCase.assertBagsEqual(HashBag.newBagWith(1, 1, 1, 1, 2, 2, 3), bag);

        bag.removeOccurrences(1, 3);
        bag.removeOccurrences(3, 1);
        MutableBagTestCase.assertBagsEqual(HashBag.newBagWith(1, 2, 2), bag);
    }

    @Test
    public void setOccurrences()
    {
        MultiReaderHashBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 1, 2);

        Assertions.assertFalse(bag.setOccurrences(1, 2));
        Assertions.assertTrue(bag.setOccurrences(3, 3));
        MutableBagTestCase.assertBagsEqual(HashBag.newBagWith(1, 1, 2, 3, 3, 3), bag);
        Assertions.assertTrue(bag.setOccurrences(2, 0));
        MutableBagTestCase.assertBagsEqual(HashBag.newBagWith(1, 1, 3, 3, 3), bag);
    }

    @Test
    public void occurrencesOf()
    {
        MultiReaderHashBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 1, 2);
        Assertions.assertEquals(2, bag.occurrencesOf(1));
        Assertions.assertEquals(1, bag.occurrencesOf(2));
    }

    @Test
    public void sizeDistinct()
    {
        MultiReaderHashBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 1, 2, 2, 3);
        Assertions.assertEquals(3, bag.sizeDistinct());
    }

    @Override
    @Test
    public void collect()
    {
        MutableBag<Boolean> bag = MultiReaderHashBag.newBagWith(Boolean.TRUE, Boolean.FALSE, null);
        MutableBag<String> newCollection = bag.collect(String::valueOf);
        Assertions.assertEquals(HashBag.newBagWith("true", "false", "null"), newCollection);
    }

    @Override
    @Test
    public void flatCollect()
    {
        MutableBag<Integer> collection = MultiReaderHashBag.newBagWith(1, 1, 2, 3, 4);
        Function<Integer, MutableBag<String>> function = object -> HashBag.newBagWith(String.valueOf(object));

        MutableBagTestCase.assertBagsEqual(
                HashBag.newBagWith("1", "1", "2", "3", "4"),
                collection.flatCollect(function));
    }

    @Override
    @Test
    public void collectIf()
    {
        Assertions.assertEquals(
                HashBag.newBagWith("1", "1", "2", "3"),
                MultiReaderHashBag.newBagWith(1, 1, 2, 3).collectIf(
                        Integer.class::isInstance,
                        String::valueOf));
        Assertions.assertEquals(
                HashBag.newBagWith("1", "1"),
                MultiReaderHashBag.newBagWith(1, 1, 2, 3).collectIf(
                        Predicates.lessThan(2),
                        String::valueOf));
    }

    @Override
    @Test
    public void collectWith()
    {
        Function2<Integer, Integer, Integer> addZeroFunction = (each, parameter) -> each + parameter;
        Verify.assertContainsAll(MultiReaderHashBag.newBagWith(1, 1, 2, 3).collectWith(addZeroFunction, 0), 1, 2, 3);
        Verify.assertContainsAll(
                MultiReaderHashBag.newBagWith(1, 1, 2, 3).collectWith(
                        addZeroFunction,
                        0,
                        HashBag.<Integer>newBag()), 1, 2, 3);
    }

    @Override
    @Test
    public void reject()
    {
        Verify.assertContainsAll(MultiReaderHashBag.newBagWith(1, 1, 2, 3, 4).reject(Predicates.lessThan(3)), 3, 4);
        Verify.assertContainsAll(MultiReaderHashBag.newBagWith(1, 2, 3, 3, 4).reject(
                Predicates.lessThan(3),
                HashBag.<Integer>newBag()), 3, 4);
    }

    @Override
    @Test
    public void rejectWith()
    {
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 2, 1);
        MutableBag<Integer> results = bag.rejectWith(Predicates2.instanceOf(), Integer.class);
        Verify.assertEmpty(results);
    }

    @Override
    @Test
    public void select()
    {
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 2, 2, 3, 4, 5, 5, 1);
        MutableBag<Integer> results = bag.select(Integer.valueOf(1)::equals);
        MutableBagTestCase.assertBagsEqual(results, MultiReaderHashBag.newBagWith(1, 1));
    }

    @Override
    @Test
    public void selectWith()
    {
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 1, 2, 2);
        MutableBag<Integer> results = bag.selectWith(Predicates2.instanceOf(), Integer.class);
        Verify.assertSize(4, results);
    }

    @Test
    public void selectByOccurrences()
    {
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 1, 2, 2, 2, 3);
        MutableBag<Integer> results = bag.selectByOccurrences(IntPredicates.isEven());
        Verify.assertSize(2, results);
        MutableBagTestCase.assertBagsEqual(results, MultiReaderHashBag.newBagWith(1, 1));
    }

    @Override
    @Test
    public void selectInstancesOf()
    {
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 1, 2, 2, 2, 3);
        MutableBagTestCase.assertBagsEqual(bag.selectInstancesOf(Integer.class), MultiReaderHashBag.newBagWith(1, 1, 2, 2, 2, 3));
    }

    @Override
    @Test
    public void partition()
    {
        MutableBag<Integer> integers = MultiReaderHashBag.newBagWith(-3, -2, -1, 0, 1, 2, 2, 2, 3, 3, 4, 5);
        PartitionMutableCollection<Integer> result = integers.partition(IntegerPredicates.isEven());
        Assertions.assertEquals(MultiReaderHashBag.newBagWith(-2, 0, 2, 2, 2, 4), result.getSelected());
        Assertions.assertEquals(MultiReaderHashBag.newBagWith(-3, -1, 1, 3, 3, 5), result.getRejected());
    }

    @Override
    @Test
    public void partitionWith()
    {
        MutableBag<Integer> integers = MultiReaderHashBag.newBagWith(-3, -2, -1, 0, 1, 2, 2, 2, 3, 3, 4, 5);
        PartitionMutableCollection<Integer> result = integers.partitionWith(Predicates2.in(), integers.select(IntegerPredicates.isEven()));
        Assertions.assertEquals(MultiReaderHashBag.newBagWith(-2, 0, 2, 2, 2, 4), result.getSelected());
        Assertions.assertEquals(MultiReaderHashBag.newBagWith(-3, -1, 1, 3, 3, 5), result.getRejected());
    }

    @Override
    @Test
    public void with()
    {
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 2, 3, 3);
        MutableBag<Integer> bagWith = bag.with(3);
        MutableBagTestCase.assertBagsEqual(MultiReaderHashBag.newBagWith(1, 2, 3, 3, 3), bagWith);
    }

    @Override
    @Test
    public void without()
    {
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 2, 3, 3);
        MutableBag<Integer> bagWithout = bag.without(3);
        MutableBagTestCase.assertBagsEqual(MultiReaderHashBag.newBagWith(1, 2, 3), bagWithout);
    }

    @Override
    @Test
    public void withAll()
    {
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 2, 3, 3);
        MutableBag<Integer> bagWith = bag.withAll(FastList.newListWith(2, 4, 4));
        MutableBagTestCase.assertBagsEqual(MultiReaderHashBag.newBagWith(1, 2, 2, 3, 3, 4, 4), bagWith);
    }

    @Override
    @Test
    public void withoutAll()
    {
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 2, 3, 3, 4);
        MutableBag<Integer> bagWithout = bag.withoutAll(FastList.newListWith(3, 4));
        MutableBagTestCase.assertBagsEqual(MultiReaderHashBag.newBagWith(1, 2), bagWithout);
    }

    @Test
    public void toMapOfItemToCount()
    {
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 2, 2, 3, 3, 3);
        Assertions.assertEquals(UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3), bag.toMapOfItemToCount());
    }

    @Test
    public void toStringOfItemToCount()
    {
        Assertions.assertEquals("{}", MultiReaderHashBag.newBagWith().toStringOfItemToCount());
        Assertions.assertEquals("{1=3}", MultiReaderHashBag.newBagWith(1, 1, 1).toStringOfItemToCount());
        String actual = MultiReaderHashBag.newBagWith(1, 2, 2).toStringOfItemToCount();
        Assertions.assertTrue("{1=1, 2=2}".equals(actual) || "{2=2, 1=1}".equals(actual));
    }

    @Test
    public void forEachWithOccurrences()
    {
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 2, 2, 3, 3, 3);
        int[] sum = new int[1];
        bag.forEachWithOccurrences((each, occurrences) -> {
            if (occurrences > 1)
            {
                sum[0] += each * occurrences;
            }
        });

        Assertions.assertEquals(13, sum[0]);
    }

    @Test
    public void equalsAndHashCose()
    {
        MutableBag<Integer> integers = MultiReaderHashBag.newBagWith(1, 2, 3);
        MutableBag<Integer> integers2 = MultiReaderHashBag.newBagWith(1, 2, 3);
        MutableBag<Integer> integers3 = MultiReaderHashBag.newBagWith(1, null, 3, 4, 5);
        MutableBag<Integer> integers4 = MultiReaderHashBag.newBagWith(1, null, 3, 4, 5);
        MutableBag<Integer> integers5 = MultiReaderHashBag.newBagWith(1, null, 3);
        MutableBag<Integer> randomAccessList = Bags.mutable.of(1, 2, 3);
        MutableBag<Integer> randomAccessList2 = Bags.mutable.of(2, 3, 4);
        Verify.assertEqualsAndHashCode(integers, integers);
        Verify.assertPostSerializedEqualsAndHashCode(integers);
        Verify.assertEqualsAndHashCode(integers, integers2);
        Verify.assertEqualsAndHashCode(integers, randomAccessList);
        Assertions.assertNotEquals(integers, integers3);
        Assertions.assertNotEquals(integers, integers5);
        Assertions.assertNotEquals(integers, randomAccessList2);
        Assertions.assertNotEquals(integers, Sets.fixedSize.of());
        Verify.assertEqualsAndHashCode(integers3, integers4);
        Assertions.assertEquals(integers, integers2);
        Assertions.assertNotEquals(integers, integers3);
    }

    @Override
    @Test
    public void toSet()
    {
        super.toSet();
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(3, 3, 3, 2, 2, 1);
        Assertions.assertEquals(UnifiedSet.newSetWith(1, 2, 3), bag.toSet());
    }

    @Override
    @Test
    public void toList()
    {
        super.toList();
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 1, 1);
        Assertions.assertEquals(FastList.newListWith(1, 1, 1), bag.toList());
    }

    @Override
    @Test
    public void injectInto()
    {
        MutableBag<Integer> bag = MultiReaderHashBag.newBagWith(1, 1, 3);
        Assertions.assertEquals(Integer.valueOf(6), bag.injectInto(1, AddFunction.INTEGER));
    }

    @Override
    @Test
    public void forEach()
    {
        MutableBag<Integer> result = HashBag.newBag();
        MutableBag<Integer> collection = MultiReaderHashBag.newBagWith(1, 2, 3, 4, 4);
        collection.forEach(CollectionAddProcedure.on(result));
        Assertions.assertEquals(HashBag.newBagWith(1, 2, 3, 4, 4), result);
    }

    @Override
    @Test
    public void isEmpty()
    {
        Verify.assertEmpty(MultiReaderHashBag.newBag());
        Verify.assertNotEmpty(MultiReaderHashBag.newBagWith(1, 1));
    }

    @Test
    public void serialization()
    {
        MutableBag<Integer> collection = MultiReaderHashBag.newBagWith(1, 1, 3, 4, 5);
        MutableBag<Integer> deserializedCollection = SerializeTestHelper.serializeDeserialize(collection);
        Verify.assertSize(5, deserializedCollection);
        Assertions.assertEquals(collection, deserializedCollection);
    }

    private void verifyDelegateIsUnmodifiable(MutableBag<Integer> delegate)
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> delegate.add(2));
        Verify.assertThrows(UnsupportedOperationException.class, () -> delegate.remove(0));
    }

    @Test
    public void withReadLockAndDelegate()
    {
        MultiReaderHashBag<Integer> bag = MultiReaderHashBag.newBagWith(1);
        Object[] result = new Object[1];
        bag.withReadLockAndDelegate(delegate -> {
            result[0] = delegate.getFirst();
            this.verifyDelegateIsUnmodifiable(delegate);
        });
        Assertions.assertNotNull(result[0]);
    }

    @Override
    @Test
    public void makeString()
    {
        Assertions.assertEquals("[1, 1, 2, 3]", MultiReaderHashBag.newBagWith(1, 1, 2, 3).toString());
    }

    @Override
    @Test
    public void appendString()
    {
        Appendable builder = new StringBuilder();
        MultiReaderHashBag.newBagWith(1, 1, 2, 3).appendString(builder);
        Assertions.assertEquals("1, 1, 2, 3", builder.toString());
    }

    @Override
    @Test
    public void testToString()
    {
        Assertions.assertEquals("[1, 1, 2, 3]", MultiReaderHashBag.newBagWith(1, 1, 2, 3).toString());
    }

    @Override
    @Test
    public void iterator()
    {
        MultiReaderHashBag<Integer> integers = MultiReaderHashBag.newBagWith(1, 1, 2, 3, 4);
        Verify.assertThrows(UnsupportedOperationException.class, (Runnable) integers::iterator);
    }

    @Test
    public void listIterator()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            MultiReaderHashBag<Integer> integers = MultiReaderHashBag.newBagWith(1, 1, 2, 3, 4);
            integers.iterator();
        });
    }

    @Test
    public void withWriteLockAndDelegate()
    {
        MultiReaderHashBag<Integer> bag = MultiReaderHashBag.newBagWith(2);
        AtomicReference<MutableBag<?>> delegateList = new AtomicReference<>();
        AtomicReference<Iterator<?>> iterator = new AtomicReference<>();
        bag.withWriteLockAndDelegate(delegate -> {
            delegate.add(1);
            delegate.add(2);
            delegate.add(3);
            delegate.add(4);
            delegateList.set(delegate);
            iterator.set(delegate.iterator());
        });
        Assertions.assertEquals(HashBag.newBagWith(1, 2, 2, 3, 4), bag);

        Verify.assertThrows(NullPointerException.class, () -> iterator.get().hasNext());

        Verify.assertThrows(NullPointerException.class, () -> delegateList.get().iterator());
    }

    @Test
    public void concurrentWrite()
    {
        MultiReaderHashBag<Integer> numbers = this.newWith();
        Interval interval = Interval.oneTo(100);
        ParallelIterate.forEach(interval, each -> {
            numbers.add(each);
            Verify.assertSize(1, numbers.select(each::equals));
            numbers.add(each);
            Assertions.assertEquals(2, numbers.count(each::equals));
            numbers.add(each);
            Integer[] removed = new Integer[1];
            numbers.withWriteLockAndDelegate(bag -> {
                Iterator<Integer> iterator = bag.iterator();
                removed[0] = iterator.next();
                bag.remove(removed[0]);
                bag.add(removed[0]);
            });
            numbers.add(each);
            Assertions.assertEquals(4, numbers.count(each::equals));
        }, 1);

        interval.forEach(Procedures.cast(each -> Assertions.assertEquals(4, numbers.occurrencesOf(each))));
    }

    @Test
    public void parallelCollect()
    {
        MultiReaderHashBag<String> numbers = this.newWith();
        Interval interval = Interval.oneTo(50000);
        ParallelIterate.collect(interval, String::valueOf, numbers, true);
        Assertions.assertEquals(numbers, interval.collect(String::valueOf).toBag());
    }
}
