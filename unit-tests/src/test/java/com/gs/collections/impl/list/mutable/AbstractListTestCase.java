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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.gs.collections.api.RichIterable;
import com.gs.collections.api.collection.MutableCollection;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.ListIterable;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.list.primitive.MutableBooleanList;
import com.gs.collections.api.list.primitive.MutableByteList;
import com.gs.collections.api.list.primitive.MutableCharList;
import com.gs.collections.api.list.primitive.MutableDoubleList;
import com.gs.collections.api.list.primitive.MutableFloatList;
import com.gs.collections.api.list.primitive.MutableIntList;
import com.gs.collections.api.list.primitive.MutableLongList;
import com.gs.collections.api.list.primitive.MutableShortList;
import com.gs.collections.api.partition.list.PartitionMutableList;
import com.gs.collections.api.stack.MutableStack;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.factory.PrimitiveFunctions;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.collection.mutable.AbstractCollectionTestCase;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.factory.Stacks;
import com.gs.collections.impl.factory.primitive.BooleanLists;
import com.gs.collections.impl.factory.primitive.ByteLists;
import com.gs.collections.impl.factory.primitive.CharLists;
import com.gs.collections.impl.factory.primitive.DoubleLists;
import com.gs.collections.impl.factory.primitive.FloatLists;
import com.gs.collections.impl.factory.primitive.IntLists;
import com.gs.collections.impl.factory.primitive.LongLists;
import com.gs.collections.impl.factory.primitive.ShortLists;
import com.gs.collections.impl.lazy.ReverseIterable;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.fixed.ArrayAdapter;
import com.gs.collections.impl.list.mutable.primitive.IntArrayList;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.SerializeTestHelper;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.gs.collections.impl.factory.Iterables.iList;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Abstract JUnit test for {@link MutableList}s.
 */
public abstract class AbstractListTestCase
        extends AbstractCollectionTestCase
{
    @Override
    protected abstract <T> MutableList<T> newWith(T... littleElements);

    @Test
    public void randomAccess_throws()
    {
        Verify.assertThrows(IllegalArgumentException.class, () -> new ListAdapter<>(FastList.newListWith(1, 2, 3)));
    }

    @Test
    public void detectIndex()
    {
        Assertions.assertEquals(1, this.newWith(1, 2, 3, 4).detectIndex(integer -> integer % 2 == 0));
        Assertions.assertEquals(0, this.newWith(1, 2, 3, 4).detectIndex(integer -> integer % 2 != 0));
        Assertions.assertEquals(-1, this.newWith(1, 2, 3, 4).detectIndex(integer -> integer % 5 == 0));
        Assertions.assertEquals(2, this.newWith(1, 1, 2, 2, 3, 3, 3, 4, 2).detectIndex(integer -> integer == 2));
        Assertions.assertEquals(0, this.newWith(1, 1, 2, 2, 3, 3, 3, 4, 2).detectIndex(integer -> integer != 2));
        Assertions.assertEquals(-1, this.newWith(1, 1, 2, 2, 3, 3, 3, 4, 2).detectIndex(integer -> integer == 5));
    }

    @Test
    public void detectLastIndex()
    {
        Assertions.assertEquals(3, this.newWith(1, 2, 3, 4).detectLastIndex(integer -> integer % 2 == 0));
        Assertions.assertEquals(2, this.newWith(1, 2, 3, 4).detectLastIndex(integer -> integer % 2 != 0));
        Assertions.assertEquals(-1, this.newWith(1, 2, 3, 4).detectLastIndex(integer -> integer % 5 == 0));
        Assertions.assertEquals(8, this.newWith(1, 1, 2, 2, 3, 3, 3, 4, 2).detectLastIndex(integer -> integer == 2));
        Assertions.assertEquals(7, this.newWith(1, 1, 2, 2, 3, 3, 3, 4, 2).detectLastIndex(integer -> integer != 2));
        Assertions.assertEquals(-1, this.newWith(1, 1, 2, 2, 3, 3, 3, 4, 2).detectLastIndex(integer -> integer == 5));
    }

    @Override
    @Test
    public void collectBoolean()
    {
        super.collectBoolean();
        MutableBooleanList result = this.newWith(-1, 0, 1, 4).collectBoolean(PrimitiveFunctions.integerIsPositive());
        Assertions.assertEquals(BooleanLists.mutable.of(false, false, true, true), result);
    }

    @Override
    @Test
    public void collectByte()
    {
        super.collectByte();
        MutableByteList result = this.newWith(1, 2, 3, 4).collectByte(PrimitiveFunctions.unboxIntegerToByte());
        Assertions.assertEquals(ByteLists.mutable.of((byte) 1, (byte) 2, (byte) 3, (byte) 4), result);
    }

    @Override
    @Test
    public void collectChar()
    {
        super.collectChar();
        MutableCharList result = this.newWith(1, 2, 3, 4).collectChar(PrimitiveFunctions.unboxIntegerToChar());
        Assertions.assertEquals(CharLists.mutable.of((char) 1, (char) 2, (char) 3, (char) 4), result);
    }

    @Override
    @Test
    public void collectDouble()
    {
        super.collectDouble();
        MutableDoubleList result = this.newWith(1, 2, 3, 4).collectDouble(PrimitiveFunctions.unboxIntegerToDouble());
        Assertions.assertEquals(DoubleLists.mutable.of(1.0d, 2.0d, 3.0d, 4.0d), result);
    }

    @Override
    @Test
    public void collectFloat()
    {
        super.collectFloat();
        MutableFloatList result = this.newWith(1, 2, 3, 4).collectFloat(PrimitiveFunctions.unboxIntegerToFloat());
        Assertions.assertEquals(FloatLists.mutable.of(1.0f, 2.0f, 3.0f, 4.0f), result);
    }

    @Override
    @Test
    public void collectInt()
    {
        super.collectInt();
        MutableIntList result = this.newWith(1, 2, 3, 4).collectInt(PrimitiveFunctions.unboxIntegerToInt());
        Assertions.assertEquals(IntLists.mutable.of(1, 2, 3, 4), result);
    }

    @Override
    @Test
    public void collectLong()
    {
        super.collectLong();
        MutableLongList result = this.newWith(1, 2, 3, 4).collectLong(PrimitiveFunctions.unboxIntegerToLong());
        Assertions.assertEquals(LongLists.mutable.of(1L, 2L, 3L, 4L), result);
    }

    @Override
    @Test
    public void collectShort()
    {
        super.collectShort();
        MutableShortList result = this.newWith(1, 2, 3, 4).collectShort(PrimitiveFunctions.unboxIntegerToShort());
        Assertions.assertEquals(ShortLists.mutable.of((short) 1, (short) 2, (short) 3, (short) 4), result);
    }

    @Override
    @Test
    public void asSynchronized()
    {
        Verify.assertInstanceOf(SynchronizedMutableList.class, this.newWith().asSynchronized());
    }

    @Override
    @Test
    public void toImmutable()
    {
        super.toImmutable();
        Verify.assertInstanceOf(ImmutableList.class, this.newWith().toImmutable());
        Assertions.assertSame(this.newWith().toImmutable(), this.newWith().toImmutable());
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        Verify.assertInstanceOf(UnmodifiableMutableList.class, this.newWith().asUnmodifiable());
    }

    @Test
    public void testClone()
    {
        MutableList<Integer> list = this.newWith(1, 2, 3);
        MutableList<Integer> list2 = list.clone();
        Verify.assertListsEqual(list, list2);
        Verify.assertShallowClone(list);
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        MutableCollection<Integer> list1 = this.newWith(1, 2, 3);
        MutableCollection<Integer> list2 = this.newWith(1, 2, 3);
        MutableCollection<Integer> list3 = this.newWith(2, 3, 4);
        MutableCollection<Integer> list4 = this.newWith(1, 2, 3, 4);
        Assertions.assertNotEquals(list1, null);
        Verify.assertEqualsAndHashCode(list1, list1);
        Verify.assertEqualsAndHashCode(list1, list2);
        Verify.assertEqualsAndHashCode(new LinkedList<>(Arrays.asList(1, 2, 3)), list1);
        Verify.assertEqualsAndHashCode(new ArrayList<>(Arrays.asList(1, 2, 3)), list1);
        Verify.assertEqualsAndHashCode(ArrayAdapter.newArrayWith(1, 2, 3), list1);
        Assertions.assertNotEquals(list2, list3);
        Assertions.assertNotEquals(list2, list4);
        Assertions.assertNotEquals(new LinkedList<>(Arrays.asList(1, 2, 3)), list4);
        Assertions.assertNotEquals(new LinkedList<>(Arrays.asList(1, 2, 3, 3)), list4);
        Assertions.assertNotEquals(new ArrayList<>(Arrays.asList(1, 2, 3)), list4);
        Assertions.assertNotEquals(new ArrayList<>(Arrays.asList(1, 2, 3, 3)), list4);
        Assertions.assertNotEquals(list4, new LinkedList<>(Arrays.asList(1, 2, 3)));
        Assertions.assertNotEquals(list4, new LinkedList<>(Arrays.asList(1, 2, 3, 3)));
        Assertions.assertNotEquals(list4, new ArrayList<>(Arrays.asList(1, 2, 3)));
        Assertions.assertNotEquals(list4, new ArrayList<>(Arrays.asList(1, 2, 3, 3)));
        Assertions.assertNotEquals(new LinkedList<>(Arrays.asList(1, 2, 3, 4)), list1);
        Assertions.assertNotEquals(new LinkedList<>(Arrays.asList(1, 2, null)), list1);
        Assertions.assertNotEquals(new LinkedList<>(Arrays.asList(1, 2)), list1);
        Assertions.assertNotEquals(new ArrayList<>(Arrays.asList(1, 2, 3, 4)), list1);
        Assertions.assertNotEquals(new ArrayList<>(Arrays.asList(1, 2, null)), list1);
        Assertions.assertNotEquals(new ArrayList<>(Arrays.asList(1, 2)), list1);
        Assertions.assertNotEquals(ArrayAdapter.newArrayWith(1, 2, 3, 4), list1);
    }

    @Test
    public void newListWithSize()
    {
        MutableList<Integer> list = this.newWith(1, 2, 3);
        Verify.assertContainsAll(list, 1, 2, 3);
    }

    @Test
    public void serialization()
    {
        MutableList<Integer> collection = this.newWith(1, 2, 3, 4, 5);
        MutableList<Integer> deserializedCollection = SerializeTestHelper.serializeDeserialize(collection);
        Verify.assertSize(5, deserializedCollection);
        Verify.assertContainsAll(deserializedCollection, 1, 2, 3, 4, 5);
        Assertions.assertEquals(collection, deserializedCollection);
    }

    @Test
    public void corresponds()
    {
        MutableList<Integer> integers1 = this.newWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
        MutableList<Integer> integers2 = this.newWith(1, 2, 3, 4);
        Assertions.assertFalse(integers1.corresponds(integers2, Predicates2.alwaysTrue()));
        Assertions.assertFalse(integers2.corresponds(integers1, Predicates2.alwaysTrue()));

        MutableList<Integer> integers3 = this.newWith(2, 3, 3, 4, 4, 4, 5, 5, 5, 5);
        Assertions.assertTrue(integers1.corresponds(integers3, Predicates2.lessThan()));
        Assertions.assertFalse(integers1.corresponds(integers3, Predicates2.greaterThan()));

        MutableList<Integer> nonRandomAccess = ListAdapter.adapt(new LinkedList<>(integers3));
        Assertions.assertTrue(integers1.corresponds(nonRandomAccess, Predicates2.lessThan()));
        Assertions.assertFalse(integers1.corresponds(nonRandomAccess, Predicates2.greaterThan()));
        Assertions.assertTrue(nonRandomAccess.corresponds(integers1, Predicates2.greaterThan()));
        Assertions.assertFalse(nonRandomAccess.corresponds(integers1, Predicates2.lessThan()));

        MutableList<String> nullBlanks = this.newWith(null, "", " ", null);
        Assertions.assertTrue(nullBlanks.corresponds(FastList.newListWith(null, "", " ", null), Comparators::nullSafeEquals));
        Assertions.assertFalse(nullBlanks.corresponds(FastList.newListWith("", null, " ", ""), Comparators::nullSafeEquals));
    }

    @Test
    public void forEachFromTo()
    {
        MutableList<Integer> result = FastList.newList();
        MutableList<Integer> collection = FastList.newListWith(1, 2, 3, 4);
        collection.forEach(2, 3, result::add);
        Assertions.assertEquals(this.newWith(3, 4), result);

        MutableList<Integer> result2 = FastList.newList();
        collection.forEach(3, 2, CollectionAddProcedure.on(result2));
        Assertions.assertEquals(this.newWith(4, 3), result2);

        MutableList<Integer> result3 = FastList.newList();
        collection.forEach(0, 3, CollectionAddProcedure.on(result3));
        Assertions.assertEquals(this.newWith(1, 2, 3, 4), result3);

        MutableList<Integer> result4 = FastList.newList();
        collection.forEach(3, 0, CollectionAddProcedure.on(result4));
        Assertions.assertEquals(this.newWith(4, 3, 2, 1), result4);

        Verify.assertThrows(IndexOutOfBoundsException.class, () -> collection.forEach(-1, 0, result::add));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> collection.forEach(0, -1, result::add));
    }

    @Test
    public void forEachFromToInReverse()
    {
        MutableList<Integer> result = Lists.mutable.of();
        this.newWith(1, 2, 3, 4).forEach(3, 2, result::add);
        Assertions.assertEquals(FastList.newListWith(4, 3), result);
    }

    @Test
    public void reverseForEach()
    {
        MutableList<Integer> result = Lists.mutable.of();
        MutableList<Integer> collection = this.newWith(1, 2, 3, 4);
        collection.reverseForEach(result::add);
        Assertions.assertEquals(FastList.newListWith(4, 3, 2, 1), result);
    }

    @Test
    public void reverseForEach_emptyList()
    {
        MutableList<Integer> integers = Lists.mutable.of();
        MutableList<Integer> results = Lists.mutable.of();
        integers.reverseForEach(results::add);
        Assertions.assertEquals(integers, results);
    }

    @Test
    public void reverseThis()
    {
        MutableList<Integer> original = this.newWith(1, 2, 3, 4);
        MutableList<Integer> reversed = original.reverseThis();
        Assertions.assertEquals(FastList.newListWith(4, 3, 2, 1), reversed);
        Assertions.assertSame(original, reversed);
    }

    @Test
    public void toReversed()
    {
        MutableList<Integer> original = this.newWith(1, 2, 3, 4);
        MutableList<Integer> actual = original.toReversed();
        MutableList<Integer> expected = this.newWith(4, 3, 2, 1);
        Assertions.assertEquals(expected, actual);
        Assertions.assertNotSame(original, actual);
    }

    @Test
    public void distinct()
    {
        ListIterable<Integer> list = this.newWith(1, 4, 3, 2, 1, 4, 1);
        ListIterable<Integer> actual = list.distinct();
        Verify.assertListsEqual(FastList.newListWith(1, 4, 3, 2), actual.toList());
    }

    @Test
    public void distinctWithHashingStrategy()
    {
        ListIterable<String> list = this.newWith("a", "A", "b", "C", "b", "D", "E", "e");
        ListIterable<String> actual = list.distinct(HashingStrategies.fromFunction(String::toLowerCase));
        Verify.assertListsEqual(FastList.newListWith("a", "b", "C", "D", "E"), actual.toList());
    }

    @Override
    @Test
    public void removeIf()
    {
        MutableCollection<Integer> objects = this.newWith(1, 2, 3, null);
        objects.removeIf(Predicates.isNull());
        Assertions.assertEquals(FastList.newListWith(1, 2, 3), objects);
    }

    @Test
    public void removeIndex()
    {
        MutableList<Integer> objects = this.newWith(1, 2, 3);
        objects.remove(2);
        Assertions.assertEquals(FastList.newListWith(1, 2), objects);
    }

    @Test
    public void indexOf()
    {
        MutableList<Integer> objects = this.newWith(1, 2, 2);
        Assertions.assertEquals(1, objects.indexOf(2));
        Assertions.assertEquals(0, objects.indexOf(1));
        Assertions.assertEquals(-1, objects.indexOf(3));
    }

    @Test
    public void lastIndexOf()
    {
        MutableList<Integer> objects = this.newWith(2, 2, 3);
        Assertions.assertEquals(1, objects.lastIndexOf(2));
        Assertions.assertEquals(2, objects.lastIndexOf(3));
        Assertions.assertEquals(-1, objects.lastIndexOf(1));
    }

    @Test
    public void set()
    {
        MutableList<Integer> objects = this.newWith(1, 2, 3);
        Assertions.assertEquals(Integer.valueOf(2), objects.set(1, 4));
        Assertions.assertEquals(FastList.newListWith(1, 4, 3), objects);
    }

    @Test
    public void addAtIndex()
    {
        MutableList<Integer> objects = this.newWith(1, 2, 3);
        objects.add(0, 0);
        Assertions.assertEquals(FastList.newListWith(0, 1, 2, 3), objects);
    }

    @Test
    public void addAllAtIndex()
    {
        MutableList<Integer> objects = this.newWith(1, 2, 3);
        objects.addAll(0, Lists.fixedSize.of(0));
        Integer one = -1;
        objects.addAll(0, new ArrayList<>(Lists.fixedSize.of(one)));
        objects.addAll(0, FastList.newListWith(-2));
        objects.addAll(0, UnifiedSet.newSetWith(-3));
        Assertions.assertEquals(FastList.newListWith(-3, -2, -1, 0, 1, 2, 3), objects);
    }

    @Test
    public void withMethods()
    {
        Verify.assertContainsAll(this.newWith().with(1), 1);
        Verify.assertContainsAll(this.newWith(1), 1);
        Verify.assertContainsAll(this.newWith(1).with(2), 1, 2);
    }

    @Test
    public void sortThis_with_null()
    {
        MutableList<Integer> integers = this.newWith(2, null, 3, 4, 1);
        Verify.assertStartsWith(integers.sortThis(Comparators.safeNullsLow(Integer::compareTo)), null, 1, 2, 3, 4);
    }

    @Test
    public void sortThis_small()
    {
        MutableList<Integer> actual = this.newWith(1, 2, 3).shuffleThis();
        MutableList<Integer> sorted = actual.sortThis();
        Assertions.assertSame(actual, sorted);
        Assertions.assertEquals(FastList.newListWith(1, 2, 3), actual);
    }

    @Test
    public void sortThis()
    {
        MutableList<Integer> actual = this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).shuffleThis();
        MutableList<Integer> sorted = actual.sortThis();
        Assertions.assertSame(actual, sorted);
        Assertions.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), actual);
    }

    @Test
    public void sortThis_large()
    {
        MutableList<Integer> actual = this.newWith(Interval.oneTo(1000).toArray()).shuffleThis();
        MutableList<Integer> sorted = actual.sortThis();
        Assertions.assertSame(actual, sorted);
        Assertions.assertEquals(Interval.oneTo(1000).toList(), actual);
    }

    @Test
    public void sortThis_with_comparator_small()
    {
        MutableList<Integer> actual = this.newWith(1, 2, 3).shuffleThis();
        MutableList<Integer> sorted = actual.sortThis(Collections.<Integer>reverseOrder());
        Assertions.assertSame(actual, sorted);
        Assertions.assertEquals(FastList.newListWith(3, 2, 1), actual);
    }

    @Test
    public void sortThis_with_comparator()
    {
        MutableList<Integer> actual = this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).shuffleThis();
        MutableList<Integer> sorted = actual.sortThis(Collections.<Integer>reverseOrder());
        Assertions.assertSame(actual, sorted);
        Assertions.assertEquals(FastList.newListWith(10, 9, 8, 7, 6, 5, 4, 3, 2, 1), actual);
    }

    @Test
    public void sortThis_with_comparator_large()
    {
        MutableList<Integer> actual = this.newWith(Interval.oneTo(1000).toArray()).shuffleThis();
        MutableList<Integer> sorted = actual.sortThis(Collections.<Integer>reverseOrder());
        Assertions.assertSame(actual, sorted);
        Assertions.assertEquals(Interval.fromToBy(1000, 1, -1).toList(), actual);
    }

    @Test
    public void sortThisBy()
    {
        MutableList<Integer> actual = this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).shuffleThis();
        MutableList<Integer> sorted = actual.sortThisBy(String::valueOf);
        Assertions.assertSame(actual, sorted);
        Assertions.assertEquals(FastList.newListWith(1, 10, 2, 3, 4, 5, 6, 7, 8, 9), actual);
    }

    @Test
    public void sortThisByBoolean()
    {
        MutableList<Integer> actual = this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        MutableList<Integer> sorted = actual.sortThisByBoolean(i -> i % 2 == 0);
        Assertions.assertSame(actual, sorted);
        Assertions.assertEquals(FastList.newListWith(1, 3, 5, 7, 9, 2, 4, 6, 8, 10), actual);
    }

    @Test
    public void sortThisByInt()
    {
        MutableList<String> actual = this.newWith("1", "2", "3", "4", "5", "6", "7", "8", "9", "10").shuffleThis();
        MutableList<String> sorted = actual.sortThisByInt(Integer::parseInt);
        Assertions.assertSame(actual, sorted);
        Assertions.assertEquals(FastList.newListWith("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"), actual);
    }

    @Test
    public void sortThisByChar()
    {
        MutableList<String> actual = this.newWith("1", "2", "3", "4", "5", "6", "7", "8", "9").shuffleThis();
        MutableList<String> sorted = actual.sortThisByChar(s -> s.charAt(0));
        Assertions.assertSame(actual, sorted);
        Assertions.assertEquals(FastList.newListWith("1", "2", "3", "4", "5", "6", "7", "8", "9"), actual);
    }

    @Test
    public void sortThisByByte()
    {
        MutableList<String> actual = this.newWith("1", "2", "3", "4", "5", "6", "7", "8", "9", "10").shuffleThis();
        MutableList<String> sorted = actual.sortThisByByte(Byte::parseByte);
        Assertions.assertSame(actual, sorted);
        Assertions.assertEquals(FastList.newListWith("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"), actual);
    }

    @Test
    public void sortThisByShort()
    {
        MutableList<String> actual = this.newWith("1", "2", "3", "4", "5", "6", "7", "8", "9", "10").shuffleThis();
        MutableList<String> sorted = actual.sortThisByShort(Short::parseShort);
        Assertions.assertSame(actual, sorted);
        Assertions.assertEquals(FastList.newListWith("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"), actual);
    }

    @Test
    public void sortThisByFloat()
    {
        MutableList<String> actual = this.newWith("1", "2", "3", "4", "5", "6", "7", "8", "9", "10").shuffleThis();
        MutableList<String> sorted = actual.sortThisByFloat(Float::parseFloat);
        Assertions.assertSame(actual, sorted);
        Assertions.assertEquals(FastList.newListWith("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"), actual);
    }

    @Test
    public void sortThisByLong()
    {
        MutableList<String> actual = this.newWith("1", "2", "3", "4", "5", "6", "7", "8", "9", "10").shuffleThis();
        MutableList<String> sorted = actual.sortThisByLong(Long::parseLong);
        Assertions.assertSame(actual, sorted);
        Assertions.assertEquals(FastList.newListWith("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"), actual);
    }

    @Test
    public void sortThisByDouble()
    {
        MutableList<String> actual = this.newWith("1", "2", "3", "4", "5", "6", "7", "8", "9", "10").shuffleThis();
        MutableList<String> sorted = actual.sortThisByDouble(Double::parseDouble);
        Assertions.assertSame(actual, sorted);
        Assertions.assertEquals(FastList.newListWith("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"), actual);
    }

    @Override
    @Test
    public void newEmpty()
    {
        Verify.assertInstanceOf(MutableList.class, this.newWith().newEmpty());
    }

    @Override
    @Test
    public void testToString()
    {
        MutableList<Object> list = this.newWith(1, 2, 3);
        list.add(list);
        Assertions.assertEquals("[1, 2, 3, (this " + list.getClass().getSimpleName() + ")]", list.toString());
    }

    @Override
    @Test
    public void makeString()
    {
        MutableList<Object> list = this.newWith(1, 2, 3);
        list.add(list);
        Assertions.assertEquals("1, 2, 3, (this " + list.getClass().getSimpleName() + ')', list.makeString());
    }

    @Override
    @Test
    public void makeStringWithSeparator()
    {
        MutableList<Object> list = this.newWith(1, 2, 3);
        Assertions.assertEquals("1/2/3", list.makeString("/"));
    }

    @Override
    @Test
    public void makeStringWithSeparatorAndStartAndEnd()
    {
        MutableList<Object> list = this.newWith(1, 2, 3);
        Assertions.assertEquals("[1/2/3]", list.makeString("[", "/", "]"));
    }

    @Override
    @Test
    public void appendString()
    {
        MutableList<Object> list = this.newWith(1, 2, 3);
        list.add(list);

        Appendable builder = new StringBuilder();
        list.appendString(builder);
        Assertions.assertEquals("1, 2, 3, (this " + list.getClass().getSimpleName() + ')', builder.toString());
    }

    @Override
    @Test
    public void appendStringWithSeparator()
    {
        MutableList<Object> list = this.newWith(1, 2, 3);

        Appendable builder = new StringBuilder();
        list.appendString(builder, "/");
        Assertions.assertEquals("1/2/3", builder.toString());
    }

    @Override
    @Test
    public void appendStringWithSeparatorAndStartAndEnd()
    {
        MutableList<Object> list = this.newWith(1, 2, 3);

        Appendable builder = new StringBuilder();
        list.appendString(builder, "[", "/", "]");
        Assertions.assertEquals("[1/2/3]", builder.toString());
    }

    @Test
    public void forEachWithIndexWithFromTo()
    {
        MutableList<Integer> integers = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        StringBuilder builder = new StringBuilder();
        integers.forEachWithIndex(5, 7, (each, index) -> builder.append(each).append(index));
        Assertions.assertEquals("353627", builder.toString());

        StringBuilder builder2 = new StringBuilder();
        integers.forEachWithIndex(5, 5, (each, index) -> builder2.append(each).append(index));
        Assertions.assertEquals("35", builder2.toString());

        StringBuilder builder3 = new StringBuilder();
        integers.forEachWithIndex(0, 9, (each, index) -> builder3.append(each).append(index));
        Assertions.assertEquals("40414243343536272819", builder3.toString());

        StringBuilder builder4 = new StringBuilder();
        integers.forEachWithIndex(7, 5, (each, index) -> builder4.append(each).append(index));
        Assertions.assertEquals("273635", builder4.toString());

        StringBuilder builder5 = new StringBuilder();
        integers.forEachWithIndex(9, 0, (each, index) -> builder5.append(each).append(index));
        Assertions.assertEquals("19282736353443424140", builder5.toString());

        MutableList<Integer> result = Lists.mutable.of();
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex(-1, 0, new AddToList(result)));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> integers.forEachWithIndex(0, -1, new AddToList(result)));
    }

    @Test
    public void forEachWithIndexWithFromToInReverse()
    {
        MutableList<Integer> result = Lists.mutable.of();
        this.newWith(1, 2, 3).forEachWithIndex(2, 1, new AddToList(result));
        Assertions.assertEquals(FastList.newListWith(3, 2), result);
    }

    @Test
    public void sortThisWithNullWithNoComparator()
    {
        assertThrows(NullPointerException.class, () -> {
            MutableList<Integer> integers = this.newWith(2, null, 3, 4, 1);
            integers.sortThis();
        });
    }

    @Test
    public void sortThisWithNullWithNoComparatorOnListWithMoreThan10Elements()
    {
        assertThrows(NullPointerException.class, () -> {
            MutableList<Integer> integers = this.newWith(2, null, 3, 4, 1, 5, 6, 7, 8, 9, 10, 11);
            integers.sortThis();
        });
    }

    @Test
    public void toSortedListWithNullWithNoComparator()
    {
        assertThrows(NullPointerException.class, () -> {
            MutableList<Integer> integers = this.newWith(2, null, 3, 4, 1);
            integers.toSortedList();
        });
    }

    @Test
    public void toSortedListWithNullWithNoComparatorOnListWithMoreThan10Elements()
    {
        assertThrows(NullPointerException.class, () -> {
            MutableList<Integer> integers = this.newWith(2, null, 3, 4, 1, 5, 6, 7, 8, 9, 10, 11);
            integers.toSortedList();
        });
    }

    @Test
    public void forEachOnRange()
    {
        MutableList<Integer> list = this.newWith();

        list.addAll(FastList.newListWith(0, 1, 2, 3));
        list.addAll(FastList.newListWith(4, 5, 6));
        list.addAll(FastList.<Integer>newList());
        list.addAll(FastList.newListWith(7, 8, 9));

        this.validateForEachOnRange(list, 0, 0, FastList.newListWith(0));
        this.validateForEachOnRange(list, 3, 5, FastList.newListWith(3, 4, 5));
        this.validateForEachOnRange(list, 4, 6, FastList.newListWith(4, 5, 6));
        this.validateForEachOnRange(list, 9, 9, FastList.newListWith(9));
        this.validateForEachOnRange(list, 0, 9, FastList.newListWith(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));

        Verify.assertThrows(
                IndexOutOfBoundsException.class,
                () -> this.validateForEachOnRange(list, 10, 10, FastList.<Integer>newList()));
    }

    protected void validateForEachOnRange(MutableList<Integer> list, int from, int to, List<Integer> expectedOutput)
    {
        List<Integer> outputList = Lists.mutable.of();
        list.forEach(from, to, outputList::add);

        Assertions.assertEquals(expectedOutput, outputList);
    }

    @Test
    public void forEachWithIndexOnRange()
    {
        MutableList<Integer> list = this.newWith();

        list.addAll(FastList.newListWith(0, 1, 2, 3));
        list.addAll(FastList.newListWith(4, 5, 6));
        list.addAll(FastList.<Integer>newList());
        list.addAll(FastList.newListWith(7, 8, 9));

        this.validateForEachWithIndexOnRange(list, 0, 0, FastList.newListWith(0));
        this.validateForEachWithIndexOnRange(list, 3, 5, FastList.newListWith(3, 4, 5));
        this.validateForEachWithIndexOnRange(list, 4, 6, FastList.newListWith(4, 5, 6));
        this.validateForEachWithIndexOnRange(list, 9, 9, FastList.newListWith(9));
        this.validateForEachWithIndexOnRange(list, 0, 9, FastList.newListWith(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        Verify.assertThrows(
                IndexOutOfBoundsException.class,
                () -> this.validateForEachWithIndexOnRange(list, 10, 10, FastList.<Integer>newList()));
    }

    protected void validateForEachWithIndexOnRange(
            MutableList<Integer> list,
            int from,
            int to,
            List<Integer> expectedOutput)
    {
        MutableList<Integer> outputList = Lists.mutable.of();
        list.forEachWithIndex(from, to, (each, index) -> outputList.add(each));

        Assertions.assertEquals(expectedOutput, outputList);
    }

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
        sublist.addAll(Arrays.asList("W", "G"));
        Verify.assertContainsAll(sublist, "B", "R", "W", "G");
        Verify.assertContainsAll(list, "A", "B", "R", "W", "G", "D");
        sublist.clear();
        Verify.assertEmpty(sublist);
        Assertions.assertFalse(sublist.remove("X"));
        Verify.assertEmpty(sublist);
        Verify.assertContainsAll(list, "A", "D");
    }

    @Test
    public void subListFromOutOfBoundsException()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.newWith(1).subList(-1, 0);
        });
    }

    @Test
    public void subListToGreaterThanSizeException()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.newWith(1).subList(0, 2);
        });
    }

    @Test
    public void subListFromGreaterThanToException()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            this.newWith(1).subList(1, 0);
        });
    }

    @Test
    public void getWithIndexOutOfBoundsException()
    {
        Object item = new Object();

        Verify.assertThrows(IndexOutOfBoundsException.class, () -> this.newWith(item).get(1));
    }

    @Test
    public void getWithArrayIndexOutOfBoundsException()
    {
        Object item = new Object();

        Verify.assertThrows(ArrayIndexOutOfBoundsException.class, () -> this.newWith(item).get(-1));
    }

    @Test
    public void listIterator()
    {
        int sum = 0;
        MutableList<Integer> integers = this.newWith(1, 2, 3, 4);
        for (Iterator<Integer> iterator = integers.listIterator(); iterator.hasNext(); )
        {
            Integer each = iterator.next();
            sum += each.intValue();
        }
        for (ListIterator<Integer> iterator = integers.listIterator(4); iterator.hasPrevious(); )
        {
            Integer each = iterator.previous();
            sum += each.intValue();
        }
        Assertions.assertEquals(20, sum);
    }

    @Test
    public void listIteratorIndexTooSmall()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.newWith(1).listIterator(-1);
        });
    }

    @Test
    public void listIteratorIndexTooBig()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.newWith(1).listIterator(2);
        });
    }

    @Override
    @Test
    public void chunk()
    {
        super.chunk();

        MutableCollection<String> collection = this.newWith("1", "2", "3", "4", "5", "6", "7");
        RichIterable<RichIterable<String>> groups = collection.chunk(2);
        Assertions.assertEquals(
                FastList.<RichIterable<String>>newListWith(
                        FastList.newListWith("1", "2"),
                        FastList.newListWith("3", "4"),
                        FastList.newListWith("5", "6"),
                        FastList.newListWith("7")),
                groups);
    }

    @Test
    public void toStack()
    {
        MutableStack<Integer> stack = this.newWith(1, 2, 3, 4).toStack();
        Assertions.assertEquals(Stacks.mutable.of(1, 2, 3, 4), stack);
    }

    @Test
    public void take()
    {
        MutableList<Integer> mutableList = this.newWith(1, 2, 3, 4, 5);
        Assertions.assertEquals(iList(), mutableList.take(0));
        Assertions.assertEquals(iList(1, 2, 3), mutableList.take(3));
        Assertions.assertEquals(iList(1, 2, 3, 4), mutableList.take(mutableList.size() - 1));

        ImmutableList<Integer> expectedList = iList(1, 2, 3, 4, 5);
        Assertions.assertEquals(expectedList, mutableList.take(mutableList.size()));
        Assertions.assertEquals(expectedList, mutableList.take(10));
        Assertions.assertEquals(expectedList, mutableList.take(Integer.MAX_VALUE));
        Assertions.assertNotSame(mutableList, mutableList.take(Integer.MAX_VALUE));
    }

    @Test
    public void take_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            this.newWith(1, 2, 3, 4, 5).take(-1);
        });
    }

    @Test
    public void takeWhile()
    {
        Assertions.assertEquals(
                iList(1, 2, 3),
                this.newWith(1, 2, 3, 4, 5).takeWhile(Predicates.lessThan(4)));

        Assertions.assertEquals(
                iList(1, 2, 3, 4, 5),
                this.newWith(1, 2, 3, 4, 5).takeWhile(Predicates.lessThan(10)));

        Assertions.assertEquals(
                iList(),
                this.newWith(1, 2, 3, 4, 5).takeWhile(Predicates.lessThan(0)));
    }

    @Test
    public void drop()
    {
        MutableList<Integer> mutableList = this.newWith(1, 2, 3, 4, 5);
        Assertions.assertEquals(iList(1, 2, 3, 4, 5), mutableList.drop(0));
        Assertions.assertNotSame(mutableList, mutableList.drop(0));
        Assertions.assertEquals(iList(4, 5), mutableList.drop(3));
        Assertions.assertEquals(iList(5), mutableList.drop(mutableList.size() - 1));
        Assertions.assertEquals(iList(), mutableList.drop(mutableList.size()));
        Assertions.assertEquals(iList(), mutableList.drop(10));
        Assertions.assertEquals(iList(), mutableList.drop(Integer.MAX_VALUE));
    }

    @Test
    public void drop_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            this.newWith(1, 2, 3, 4, 5).drop(-1);
        });
    }

    @Test
    public void dropWhile()
    {
        Assertions.assertEquals(
                iList(4, 5),
                this.newWith(1, 2, 3, 4, 5).dropWhile(Predicates.lessThan(4)));

        Assertions.assertEquals(
                iList(),
                this.newWith(1, 2, 3, 4, 5).dropWhile(Predicates.lessThan(10)));

        Assertions.assertEquals(
                iList(1, 2, 3, 4, 5),
                this.newWith(1, 2, 3, 4, 5).dropWhile(Predicates.lessThan(0)));
    }

    @Test
    public void partitionWhile()
    {
        PartitionMutableList<Integer> partition1 = this.newWith(1, 2, 3, 4, 5).partitionWhile(Predicates.lessThan(4));
        Assertions.assertEquals(iList(1, 2, 3), partition1.getSelected());
        Assertions.assertEquals(iList(4, 5), partition1.getRejected());

        PartitionMutableList<Integer> partition2 = this.newWith(1, 2, 3, 4, 5).partitionWhile(Predicates.lessThan(0));
        Assertions.assertEquals(iList(), partition2.getSelected());
        Assertions.assertEquals(iList(1, 2, 3, 4, 5), partition2.getRejected());

        PartitionMutableList<Integer> partition3 = this.newWith(1, 2, 3, 4, 5).partitionWhile(Predicates.lessThan(10));
        Assertions.assertEquals(iList(1, 2, 3, 4, 5), partition3.getSelected());
        Assertions.assertEquals(iList(), partition3.getRejected());
    }

    @Test
    public void asReversed()
    {
        Verify.assertInstanceOf(ReverseIterable.class, this.newWith().asReversed());

        Verify.assertIterablesEqual(iList(4, 3, 2, 1), this.newWith(1, 2, 3, 4).asReversed());
    }

    @Test
    public void binarySearch()
    {
        MutableList<Integer> sortedList = this.newWith(1, 2, 3, 4, 5, 7).toList();
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
        MutableList<Integer> sortedList = this.newWith(1, 2, 3, 4, 5, 7).toSortedList(Comparators.reverseNaturalOrder());
        Assertions.assertEquals(4, sortedList.binarySearch(2, Comparators.reverseNaturalOrder()));
        Assertions.assertEquals(-2, sortedList.binarySearch(6, Comparators.reverseNaturalOrder()));
        for (Integer integer : sortedList)
        {
            Assertions.assertEquals(
                    Collections.binarySearch(sortedList, integer, Comparators.reverseNaturalOrder()),
                    sortedList.binarySearch(integer, Comparators.reverseNaturalOrder()));
        }
    }

    @Override
    @Test
    public void forEachWithIndex()
    {
        super.forEachWithIndex();

        MutableList<Integer> elements = FastList.newList();
        IntArrayList indexes = new IntArrayList();
        MutableList<Integer> collection = this.newWith(1, 2, 3, 4);
        collection.forEachWithIndex((Integer object, int index) -> {
            elements.add(object);
            indexes.add(index);
        });
        Assertions.assertEquals(FastList.newListWith(1, 2, 3, 4), elements);
        Assertions.assertEquals(IntArrayList.newListWith(0, 1, 2, 3), indexes);
    }
}
