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

package com.gs.collections.impl.collection.immutable;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.gs.collections.api.RichIterable;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.block.procedure.Procedure2;
import com.gs.collections.api.collection.ImmutableCollection;
import com.gs.collections.api.collection.MutableCollection;
import com.gs.collections.api.collection.primitive.ImmutableBooleanCollection;
import com.gs.collections.api.collection.primitive.ImmutableByteCollection;
import com.gs.collections.api.collection.primitive.ImmutableCharCollection;
import com.gs.collections.api.collection.primitive.ImmutableDoubleCollection;
import com.gs.collections.api.collection.primitive.ImmutableFloatCollection;
import com.gs.collections.api.collection.primitive.ImmutableIntCollection;
import com.gs.collections.api.collection.primitive.ImmutableLongCollection;
import com.gs.collections.api.collection.primitive.ImmutableShortCollection;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MapIterable;
import com.gs.collections.api.partition.PartitionImmutableCollection;
import com.gs.collections.api.set.sorted.MutableSortedSet;
import com.gs.collections.impl.Counter;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.IntegerPredicates;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.factory.PrimitiveFunctions;
import com.gs.collections.impl.block.factory.Procedures;
import com.gs.collections.impl.block.function.AddFunction;
import com.gs.collections.impl.block.function.PassThruFunction0;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.set.sorted.mutable.TreeSortedSet;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.gs.collections.impl.factory.Iterables.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class AbstractImmutableCollectionTestCase
{
    public static final Predicate<Integer> ERROR_THROWING_PREDICATE = each -> { throw new AssertionError(); };

    public static final Predicates2<Integer, Class<Integer>> ERROR_THROWING_PREDICATE_2 = new Predicates2<Integer, Class<Integer>>()
    {
        public boolean accept(Integer argument1, Class<Integer> argument2)
        {
            throw new AssertionError();
        }
    };

    protected abstract ImmutableCollection<Integer> classUnderTest();

    protected abstract <T> MutableCollection<T> newMutable();

    @Test
    public void selectWith()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assertions.assertEquals(
                this.<Integer>newMutable().withAll(integers).select(IntegerPredicates.isOdd()),
                integers.selectWith(Predicates2.in(), iList(1, 3, 5, 7, 9)));
    }

    @Test
    public void selectWith_target()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assertions.assertEquals(
                this.<Integer>newMutable().with(101).withAll(integers).select(IntegerPredicates.isOdd()),
                integers.selectWith(Predicates2.in(), iList(1, 3, 5, 7, 9), this.<Integer>newMutable().with(101)));
    }

    @Test
    public void rejectWith()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assertions.assertEquals(
                this.<Integer>newMutable().withAll(integers).reject(IntegerPredicates.isOdd()),
                integers.rejectWith(Predicates2.in(), iList(1, 3, 5, 7, 9)));
    }

    @Test
    public void rejectWith_target()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assertions.assertEquals(
                this.<Integer>newMutable().with(100).withAll(integers).reject(IntegerPredicates.isOdd()),
                integers.rejectWith(Predicates2.in(), iList(1, 3, 5, 7, 9), this.<Integer>newMutable().with(100)));
    }

    @Test
    public void partition()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        PartitionImmutableCollection<Integer> partition = integers.partition(IntegerPredicates.isOdd());
        Assertions.assertEquals(integers.select(IntegerPredicates.isOdd()), partition.getSelected());
        Assertions.assertEquals(integers.select(IntegerPredicates.isEven()), partition.getRejected());
    }

    @Test
    public void partitionWith()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        PartitionImmutableCollection<Integer> partition = integers.partitionWith(Predicates2.in(), integers.select(IntegerPredicates.isOdd()));
        Assertions.assertEquals(integers.select(IntegerPredicates.isOdd()), partition.getSelected());
        Assertions.assertEquals(integers.select(IntegerPredicates.isEven()), partition.getRejected());
    }

    @Test
    public void collectWith()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableCollection<String> expected = integers.collect(Functions.chain(String::valueOf, string -> string + "!"));
        ImmutableCollection<String> actual = integers.collectWith((argument1, argument2) -> argument1 + argument2, "!");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void collect_target()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        MutableCollection<String> strings = this.<String>newMutable();
        integers.forEach(Procedures.cast(each -> strings.add(each.toString())));
        MutableCollection<String> target = this.<String>newMutable();
        MutableCollection<String> actual = integers.collect(String::valueOf, target);
        Assertions.assertEquals(strings, actual);
        Assertions.assertSame(target, actual);
    }

    @Test
    public void collectWith_target()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        MutableCollection<String> expected = this.<String>newMutable().with("?").withAll(integers.collect(Functions.chain(String::valueOf, string -> string + "!")));
        MutableCollection<String> targetCollection = this.<String>newMutable().with("?");
        MutableCollection<String> actual = integers.collectWith((argument1, argument2) -> argument1 + argument2, "!", targetCollection);

        Assertions.assertEquals(expected, actual);
        Assertions.assertSame(targetCollection, actual);
    }

    @Test
    public void injectInto()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Integer result = integers.injectInto(0, AddFunction.INTEGER);
        Assertions.assertEquals(FastList.newList(integers).injectInto(0, AddFunction.INTEGER_TO_INT), result.intValue());
    }

    @Test
    public void injectIntoInt()
    {
        Assertions.assertEquals(
                this.classUnderTest().injectInto(0, AddFunction.INTEGER).longValue(),
                this.classUnderTest().injectInto(0, AddFunction.INTEGER_TO_INT));
    }

    @Test
    public void injectIntoLong()
    {
        Assertions.assertEquals(
                this.classUnderTest().injectInto(0, AddFunction.INTEGER).longValue(),
                this.classUnderTest().injectInto(0, AddFunction.INTEGER_TO_LONG));
    }

    @Test
    public void injectIntoDouble()
    {
        Assertions.assertEquals(
                this.classUnderTest().injectInto(0, AddFunction.INTEGER).doubleValue(),
                this.classUnderTest().injectInto(0, AddFunction.INTEGER_TO_DOUBLE),
                0.0);
    }

    @Test
    public void injectIntoFloat()
    {
        Assertions.assertEquals(
                this.classUnderTest().injectInto(0, AddFunction.INTEGER).floatValue(),
                this.classUnderTest().injectInto(0, AddFunction.INTEGER_TO_FLOAT),
                0.0);
    }

    @Test
    public void sumFloat()
    {
        Assertions.assertEquals(
                this.classUnderTest().injectInto(0, AddFunction.INTEGER_TO_FLOAT),
                this.classUnderTest().sumOfFloat(Integer::floatValue),
                0.0);
    }

    @Test
    public void sumDouble()
    {
        Assertions.assertEquals(
                this.classUnderTest().injectInto(0, AddFunction.INTEGER_TO_DOUBLE),
                this.classUnderTest().sumOfDouble(Integer::doubleValue),
                0.0);
    }

    @Test
    public void sumInteger()
    {
        Assertions.assertEquals(
                this.classUnderTest().injectInto(0, AddFunction.INTEGER_TO_INT),
                this.classUnderTest().sumOfInt(integer -> integer));
    }

    @Test
    public void sumLong()
    {
        Assertions.assertEquals(
                this.classUnderTest().injectInto(0, AddFunction.INTEGER_TO_LONG),
                this.classUnderTest().sumOfLong(Integer::longValue));
    }

    @Test
    public void makeString()
    {
        Assertions.assertEquals(FastList.newList(this.classUnderTest()).toString(), '[' + this.classUnderTest().makeString() + ']');
        Assertions.assertEquals(FastList.newList(this.classUnderTest()).toString(), '[' + this.classUnderTest().makeString(", ") + ']');
        Assertions.assertEquals(FastList.newList(this.classUnderTest()).toString(), this.classUnderTest().makeString("[", ", ", "]"));
    }

    @Test
    public void appendString()
    {
        Appendable builder1 = new StringBuilder();
        this.classUnderTest().appendString(builder1);
        Assertions.assertEquals(FastList.newList(this.classUnderTest()).toString(), '[' + builder1.toString() + ']');

        Appendable builder2 = new StringBuilder();
        this.classUnderTest().appendString(builder2, ", ");
        Assertions.assertEquals(FastList.newList(this.classUnderTest()).toString(), '[' + builder2.toString() + ']');

        Appendable builder3 = new StringBuilder();
        this.classUnderTest().appendString(builder3, "[", ", ", "]");
        Assertions.assertEquals(FastList.newList(this.classUnderTest()).toString(), builder3.toString());
    }

    @Test
    public void testToString()
    {
        Assertions.assertEquals(FastList.newList(this.classUnderTest()).toString(), this.classUnderTest().toString());
    }

    @Test
    public void select()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assertions.assertEquals(integers, integers.select(Predicates.lessThan(integers.size() + 1)));
        Verify.assertIterableEmpty(integers.select(Predicates.greaterThan(integers.size())));
    }

    @Test
    public void selectInstancesOf()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableCollection<Integer> result = integers.selectInstancesOf(Integer.class);
        Assertions.assertEquals(integers, result);
    }

    @Test
    public void reject()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Verify.assertIterableEmpty(integers.reject(Predicates.lessThan(integers.size() + 1)));
        Assertions.assertEquals(integers, integers.reject(Predicates.greaterThan(integers.size())));
    }

    @Test
    public void collect()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assertions.assertEquals(integers, integers.collect(Functions.getIntegerPassThru()));
    }

    @Test
    public void collectBoolean()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableBooleanCollection immutableCollection = integers.collectBoolean(PrimitiveFunctions.integerIsPositive());
        Verify.assertSize(1, immutableCollection);
    }

    @Test
    public void collectByte()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableByteCollection immutableCollection = integers.collectByte(PrimitiveFunctions.unboxIntegerToByte());
        Verify.assertSize(integers.size(), immutableCollection);
        Assertions.assertEquals(integers, immutableCollection.collect(Integer::valueOf));
    }

    @Test
    public void collectChar()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableCharCollection immutableCollection = integers.collectChar(PrimitiveFunctions.unboxIntegerToChar());
        Verify.assertSize(integers.size(), immutableCollection);
        Assertions.assertEquals(integers, immutableCollection.collect(Integer::valueOf));
    }

    @Test
    public void collectDouble()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableDoubleCollection immutableCollection = integers.collectDouble(PrimitiveFunctions.unboxIntegerToDouble());
        Verify.assertSize(integers.size(), immutableCollection);
        Assertions.assertEquals(integers, immutableCollection.collect(doubleParameter -> Integer.valueOf((int) doubleParameter)));
    }

    @Test
    public void collectFloat()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableFloatCollection immutableCollection = integers.collectFloat(PrimitiveFunctions.unboxIntegerToFloat());
        Verify.assertSize(integers.size(), immutableCollection);
        Assertions.assertEquals(integers, immutableCollection.collect(floatParameter -> Integer.valueOf((int) floatParameter)));
    }

    @Test
    public void collectInt()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableIntCollection immutableCollection = integers.collectInt(PrimitiveFunctions.unboxIntegerToInt());
        Verify.assertSize(integers.size(), immutableCollection);
        Assertions.assertEquals(integers, immutableCollection.collect(Integer::valueOf));
    }

    @Test
    public void collectLong()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableLongCollection immutableCollection = integers.collectLong(PrimitiveFunctions.unboxIntegerToLong());
        Verify.assertSize(integers.size(), immutableCollection);
        Assertions.assertEquals(integers, immutableCollection.collect(longParameter -> Integer.valueOf((int) longParameter)));
    }

    @Test
    public void collectShort()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        ImmutableShortCollection immutableCollection = integers.collectShort(PrimitiveFunctions.unboxIntegerToShort());
        Verify.assertSize(integers.size(), immutableCollection);
        Assertions.assertEquals(integers, immutableCollection.collect(Integer::valueOf));
    }

    @Test
    public void flatCollect()
    {
        RichIterable<String> actual = this.classUnderTest().flatCollect(integer -> Lists.fixedSize.of(String.valueOf(integer)));

        ImmutableCollection<String> expected = this.classUnderTest().collect(String::valueOf);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void chunk_zero_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            this.classUnderTest().chunk(0);
        });
    }

    @Test
    public void detect()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assertions.assertEquals(Integer.valueOf(1), integers.detect(Predicates.equal(1)));
        Assertions.assertNull(integers.detect(Predicates.equal(integers.size() + 1)));
    }

    @Test
    public void detectWith()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assertions.assertEquals(Integer.valueOf(1), integers.detectWith(Object::equals, Integer.valueOf(1)));
        Assertions.assertNull(integers.detectWith(Object::equals, Integer.valueOf(integers.size() + 1)));

        FastList<String> strings = FastList.newListWith("1", "2", "3");
        Assertions.assertEquals("1", strings.detectWith(Object::equals, "1"));
    }

    @Test
    public void detectIfNone()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Function0<Integer> function = new PassThruFunction0<>(integers.size() + 1);
        Assertions.assertEquals(Integer.valueOf(1), integers.detectIfNone(Predicates.equal(1), function));
        Assertions.assertEquals(Integer.valueOf(integers.size() + 1), integers.detectIfNone(Predicates.equal(integers.size() + 1), function));
    }

    @Test
    public void detectWithIfNone()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Integer sum = Integer.valueOf(integers.size() + 1);
        Function0<Integer> function = new PassThruFunction0<>(sum);
        Assertions.assertEquals(Integer.valueOf(1), integers.detectWithIfNone(Object::equals, Integer.valueOf(1), function));
        Assertions.assertEquals(sum, integers.detectWithIfNone(Object::equals, sum, function));
    }

    @Test
    public void allSatisfy()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assertions.assertTrue(integers.allSatisfy(Integer.class::isInstance));
        Assertions.assertFalse(integers.allSatisfy(Integer.valueOf(0)::equals));
    }

    @Test
    public void allSatisfyWith()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assertions.assertTrue(integers.allSatisfyWith(Predicates2.instanceOf(), Integer.class));
        Assertions.assertFalse(integers.allSatisfyWith(Object::equals, 0));
    }

    @Test
    public void noneSatisfy()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assertions.assertTrue(integers.noneSatisfy(String.class::isInstance));
        Assertions.assertFalse(integers.noneSatisfy(Integer.valueOf(1)::equals));
    }

    @Test
    public void noneSatisfyWith()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assertions.assertTrue(integers.noneSatisfyWith(Predicates2.instanceOf(), String.class));
        Assertions.assertFalse(integers.noneSatisfyWith(Object::equals, 1));
    }

    @Test
    public void anySatisfy()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assertions.assertFalse(integers.anySatisfy(String.class::isInstance));
        Assertions.assertTrue(integers.anySatisfy(Integer.class::isInstance));
    }

    @Test
    public void anySatisfyWith()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assertions.assertFalse(integers.anySatisfyWith(Predicates2.instanceOf(), String.class));
        Assertions.assertTrue(integers.anySatisfyWith(Predicates2.instanceOf(), Integer.class));
    }

    @Test
    public void count()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assertions.assertEquals(integers.size(), integers.count(Integer.class::isInstance));
        Assertions.assertEquals(0, integers.count(String.class::isInstance));
    }

    @Test
    public void countWith()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assertions.assertEquals(integers.size(), integers.countWith(Predicates2.instanceOf(), Integer.class));
        Assertions.assertEquals(0, integers.countWith(Predicates2.instanceOf(), String.class));
    }

    @Test
    public void collectIf()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assertions.assertEquals(integers, integers.collectIf(Integer.class::isInstance, Functions.getIntegerPassThru()));
    }

    @Test
    public void getFirst()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assertions.assertEquals(Integer.valueOf(1), integers.getFirst());
    }

    @Test
    public void getLast()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Assertions.assertEquals(Integer.valueOf(integers.size()), integers.getLast());
    }

    @Test
    public void isEmpty()
    {
        ImmutableCollection<Integer> immutableCollection = this.classUnderTest();
        Assertions.assertFalse(immutableCollection.isEmpty());
        Assertions.assertTrue(immutableCollection.notEmpty());
    }

    @Test
    public void iterator()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        Iterator<Integer> iterator = integers.iterator();
        for (int i = 0; iterator.hasNext(); i++)
        {
            Integer integer = iterator.next();
            Assertions.assertEquals(i + 1, integer.intValue());
        }
        Verify.assertThrows(NoSuchElementException.class, (Runnable) iterator::next);
    }

    @Test
    public void toArray()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        MutableList<Integer> copy = FastList.newList(integers);
        Assertions.assertArrayEquals(integers.toArray(), copy.toArray());
        Assertions.assertArrayEquals(integers.toArray(new Integer[integers.size()]), copy.toArray(new Integer[integers.size()]));
    }

    @Test
    public void toSortedList()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        MutableList<Integer> copy = FastList.newList(integers);
        MutableList<Integer> list = integers.toSortedList(Collections.<Integer>reverseOrder());
        Assertions.assertEquals(copy.sortThis(Collections.<Integer>reverseOrder()), list);
        MutableList<Integer> list2 = integers.toSortedList();
        Assertions.assertEquals(copy.sortThis(), list2);
    }

    @Test
    public void toSortedSet()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        MutableSortedSet<Integer> set = integers.toSortedSet();
        Verify.assertListsEqual(integers.toSortedList(), set.toList());
    }

    @Test
    public void toSortedSetWithComparator()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        MutableSortedSet<Integer> set = integers.toSortedSet(Comparators.<Integer>reverseNaturalOrder());
        Assertions.assertEquals(integers.toSet(), set);
        Assertions.assertEquals(integers.toSortedList(Comparators.<Integer>reverseNaturalOrder()), set.toList());
    }

    @Test
    public void toSortedSetBy()
    {
        ImmutableCollection<Integer> integers = this.classUnderTest();
        MutableSortedSet<Integer> set = integers.toSortedSetBy(String::valueOf);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(integers), set);
    }

    @Test
    public void forLoop()
    {
        ImmutableCollection<Integer> immutableCollection = this.classUnderTest();
        for (Integer each : immutableCollection)
        {
            Assertions.assertNotNull(each);
        }
    }

    private ImmutableCollection<Integer> classUnderTestWithNull()
    {
        return this.classUnderTest().reject(Integer.valueOf(1)::equals).newWith(null);
    }

    @Test
    public void min_null_throws()
    {
        assertThrows(NullPointerException.class, () -> {
            this.classUnderTestWithNull().min(Integer::compareTo);
        });
    }

    @Test
    public void max_null_throws()
    {
        assertThrows(NullPointerException.class, () -> {
            this.classUnderTestWithNull().max(Integer::compareTo);
        });
    }

    @Test
    public void min()
    {
        Assertions.assertEquals(Integer.valueOf(1), this.classUnderTest().min(Integer::compareTo));
    }

    @Test
    public void max()
    {
        Assertions.assertEquals(Integer.valueOf(1), this.classUnderTest().max(Comparators.reverse(Integer::compareTo)));
    }

    @Test
    public void min_null_throws_without_comparator()
    {
        assertThrows(NullPointerException.class, () -> {
            this.classUnderTestWithNull().min();
        });
    }

    @Test
    public void max_null_throws_without_comparator()
    {
        assertThrows(NullPointerException.class, () -> {
            this.classUnderTestWithNull().max();
        });
    }

    @Test
    public void min_without_comparator()
    {
        Assertions.assertEquals(Integer.valueOf(1), this.classUnderTest().min());
    }

    @Test
    public void max_without_comparator()
    {
        Assertions.assertEquals(Integer.valueOf(this.classUnderTest().size()), this.classUnderTest().max());
    }

    @Test
    public void minBy()
    {
        Assertions.assertEquals(Integer.valueOf(1), this.classUnderTest().minBy(String::valueOf));
    }

    @Test
    public void maxBy()
    {
        Assertions.assertEquals(Integer.valueOf(this.classUnderTest().size()), this.classUnderTest().maxBy(Functions.getIntegerPassThru()));
    }

    @Test
    public void iteratorRemove()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.classUnderTest().iterator().remove());
    }

    @Test
    public void add()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ((Collection<Integer>) this.classUnderTest()).add(1));
    }

    @Test
    public void remove()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ((Collection<Integer>) this.classUnderTest()).remove(Integer.valueOf(1)));
    }

    @Test
    public void clear()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ((Collection<Integer>) this.classUnderTest()).clear());
    }

    @Test
    public void removeAll()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ((Collection<Integer>) this.classUnderTest()).removeAll(Lists.fixedSize.of()));
    }

    @Test
    public void retainAll()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ((Collection<Integer>) this.classUnderTest()).retainAll(Lists.fixedSize.of()));
    }

    @Test
    public void addAll()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ((Collection<Integer>) this.classUnderTest()).addAll(Lists.fixedSize.<Integer>of()));
    }

    @Test
    public void aggregateByMutating()
    {
        Procedure2<Counter, Integer> sumAggregator = Counter::add;
        MapIterable<String, Counter> actual = this.classUnderTest().aggregateInPlaceBy(String::valueOf, Counter::new, sumAggregator);
        MapIterable<String, Counter> expected = this.classUnderTest().toBag().aggregateInPlaceBy(String::valueOf, Counter::new, sumAggregator);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void aggregateByNonMutating()
    {
        Function2<Integer, Integer, Integer> sumAggregator = (integer1, integer2) -> integer1 + integer2;
        MapIterable<String, Integer> actual = this.classUnderTest().aggregateBy(String::valueOf, () -> 0, sumAggregator);
        MapIterable<String, Integer> expected = this.classUnderTest().toBag().aggregateBy(String::valueOf, () -> 0, sumAggregator);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void tap()
    {
        MutableList<Integer> tapResult = Lists.mutable.of();
        ImmutableCollection<Integer> collection = this.classUnderTest();
        Assertions.assertSame(collection, collection.tap(tapResult::add));
        Assertions.assertEquals(collection.toList(), tapResult);
    }
}
