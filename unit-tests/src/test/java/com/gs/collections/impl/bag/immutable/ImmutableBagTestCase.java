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

package com.gs.collections.impl.bag.immutable;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.gs.collections.api.LazyIterable;
import com.gs.collections.api.bag.ImmutableBag;
import com.gs.collections.api.bag.MutableBag;
import com.gs.collections.api.bag.primitive.ImmutableBooleanBag;
import com.gs.collections.api.bag.primitive.ImmutableByteBag;
import com.gs.collections.api.bag.primitive.ImmutableCharBag;
import com.gs.collections.api.bag.primitive.ImmutableDoubleBag;
import com.gs.collections.api.bag.primitive.ImmutableFloatBag;
import com.gs.collections.api.bag.primitive.ImmutableIntBag;
import com.gs.collections.api.bag.primitive.ImmutableLongBag;
import com.gs.collections.api.bag.primitive.ImmutableShortBag;
import com.gs.collections.api.bag.sorted.MutableSortedBag;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.function.primitive.CharFunction;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MapIterable;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.map.sorted.MutableSortedMap;
import com.gs.collections.api.multimap.Multimap;
import com.gs.collections.api.multimap.MutableMultimap;
import com.gs.collections.api.multimap.bag.ImmutableBagMultimap;
import com.gs.collections.api.partition.bag.PartitionImmutableBag;
import com.gs.collections.api.set.ImmutableSet;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.AbstractRichIterableTestCase;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.bag.mutable.primitive.BooleanHashBag;
import com.gs.collections.impl.bag.mutable.primitive.ByteHashBag;
import com.gs.collections.impl.bag.mutable.primitive.CharHashBag;
import com.gs.collections.impl.bag.mutable.primitive.DoubleHashBag;
import com.gs.collections.impl.bag.mutable.primitive.FloatHashBag;
import com.gs.collections.impl.bag.mutable.primitive.IntHashBag;
import com.gs.collections.impl.bag.mutable.primitive.LongHashBag;
import com.gs.collections.impl.bag.mutable.primitive.ShortHashBag;
import com.gs.collections.impl.bag.sorted.mutable.TreeBag;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.IntegerPredicates;
import com.gs.collections.impl.block.factory.ObjectIntProcedures;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.factory.primitive.IntPredicates;
import com.gs.collections.impl.block.function.AddFunction;
import com.gs.collections.impl.block.function.NegativeIntervalFunction;
import com.gs.collections.impl.block.function.PassThruFunction0;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.factory.Bags;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.multimap.bag.HashBagMultimap;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.utility.StringIterate;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public abstract class ImmutableBagTestCase extends AbstractRichIterableTestCase
{
    /**
     * @return A bag containing "1", "2", "2", "3", "3", "3", etc.
     */
    protected abstract ImmutableBag<String> newBag();

    @Override
    protected <T> ImmutableBag<T> newWith(T... littleElements)
    {
        return ImmutableHashBag.newBagWith(littleElements);
    }

    /**
     * @return The number of unique keys.
     */
    protected abstract int numKeys();

    @Test
    public abstract void testSize();

    @Override
    @Test
    @Test
    public void equalsAndHashCode()
    {
        super.equalsAndHashCode();

        ImmutableBag<String> immutable = this.newBag();
        MutableBag<String> mutable = HashBag.newBag(immutable);
        Verify.assertEqualsAndHashCode(immutable, mutable);
        Assertions.assertNotEquals(immutable, FastList.newList(mutable));
        Assertions.assertEquals(this.newBag().toMapOfItemToCount().hashCode(), this.newBag().hashCode());
        Assertions.assertNotEquals(immutable, mutable.with("5").without("1"));
    }

    @Test
    public void newWith()
    {
        ImmutableBag<String> bag = this.newBag();
        ImmutableBag<String> newBag = bag.newWith("1");
        Assertions.assertNotEquals(bag, newBag);
        Assertions.assertEquals(bag.size() + 1, newBag.size());
        Assertions.assertEquals(bag.sizeDistinct(), newBag.sizeDistinct());
        ImmutableBag<String> newBag2 = bag.newWith("0");
        Assertions.assertNotEquals(bag, newBag2);
        Assertions.assertEquals(bag.size() + 1, newBag2.size());
        Assertions.assertEquals(newBag.sizeDistinct() + 1, newBag2.sizeDistinct());
    }

    @Test
    public void newWithout()
    {
        ImmutableBag<String> bag = this.newBag();
        ImmutableBag<String> newBag = bag.newWithout("1");
        Assertions.assertNotEquals(bag, newBag);
        Assertions.assertEquals(bag.size() - 1, newBag.size());
        Assertions.assertEquals(bag.sizeDistinct() - 1, newBag.sizeDistinct());
        ImmutableBag<String> newBag2 = bag.newWithout("0");
        Assertions.assertEquals(bag, newBag2);
        Assertions.assertEquals(bag.size(), newBag2.size());
        Assertions.assertEquals(bag.sizeDistinct(), newBag2.sizeDistinct());
    }

    @Test
    public void newWithAll()
    {
        ImmutableBag<String> bag = this.newBag();
        ImmutableBag<String> newBag = bag.newWithAll(Bags.mutable.of("0"));
        Assertions.assertNotEquals(bag, newBag);
        Assertions.assertEquals(HashBag.newBag(bag).with("0"), newBag);
        Assertions.assertEquals(newBag.size(), bag.size() + 1);
    }

    @Test
    public abstract void toStringOfItemToCount();

    @Test
    public void newWithoutAll()
    {
        ImmutableBag<String> bag = this.newBag();
        ImmutableBag<String> withoutAll = bag.newWithoutAll(UnifiedSet.newSet(this.newBag()));
        Assertions.assertEquals(Bags.immutable.of(), withoutAll);

        ImmutableBag<String> newBag =
                bag.newWithAll(Lists.fixedSize.of("0", "0", "0"))
                        .newWithoutAll(Lists.fixedSize.of("0"));

        Assertions.assertEquals(0, newBag.occurrencesOf("0"));
    }

    @Override
    @Test
    @Test
    public void contains()
    {
        super.contains();

        ImmutableBag<String> bag = this.newBag();
        for (int i = 1; i <= this.numKeys(); i++)
        {
            String key = String.valueOf(i);
            Assertions.assertTrue(bag.contains(key));
            Assertions.assertEquals(i, bag.occurrencesOf(key));
        }
        String missingKey = "0";
        Assertions.assertFalse(bag.contains(missingKey));
        Assertions.assertEquals(0, bag.occurrencesOf(missingKey));
    }

    @Override
    @Test
    @Test
    public void containsAllArray()
    {
        super.containsAllArray();

        Assertions.assertTrue(this.newBag().containsAllArguments(this.newBag().toArray()));
    }

    @Override
    @Test
    @Test
    public void containsAllIterable()
    {
        super.containsAllIterable();

        Assertions.assertTrue(this.newBag().containsAllIterable(this.newBag()));
    }

    @Test
    public void add()
    {
        Verify.assertThrows(
                UnsupportedOperationException.class,
                () -> ((Collection<String>) this.newBag()).add("1"));
    }

    @Test
    public void remove()
    {
        Verify.assertThrows(
                UnsupportedOperationException.class,
                () -> ((Collection<String>) this.newBag()).remove("1"));
    }

    @Test
    public void addAll()
    {
        Verify.assertThrows(
                UnsupportedOperationException.class,
                () -> ((Collection<String>) this.newBag()).addAll(FastList.newListWith("1", "2", "3")));
    }

    @Test
    public void removeAll()
    {
        Verify.assertThrows(
                UnsupportedOperationException.class,
                () -> ((Collection<String>) this.newBag()).removeAll(FastList.newListWith("1", "2", "3")));
    }

    @Test
    public void retainAll()
    {
        Verify.assertThrows(
                UnsupportedOperationException.class,
                () -> ((Collection<String>) this.newBag()).retainAll(FastList.newListWith("1", "2", "3")));
    }

    @Test
    public void clear()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ((Collection<String>) this.newBag()).clear());
    }

    @Override
    @Test
    @Test
    public void tap()
    {
        super.tap();

        MutableList<String> tapResult = Lists.mutable.of();
        ImmutableBag<String> collection = this.newBag();
        Assertions.assertSame(collection, collection.tap(tapResult::add));
        Assertions.assertEquals(collection.toList(), tapResult);
    }

    @Override
    @Test
    @Test
    public void forEach()
    {
        super.forEach();

        MutableBag<String> result = Bags.mutable.of();
        ImmutableBag<String> collection = this.newBag();
        collection.forEach(CollectionAddProcedure.on(result));
        Assertions.assertEquals(collection, result);
    }

    @Override
    @Test
    @Test
    public void forEachWith()
    {
        super.forEachWith();

        MutableBag<String> result = Bags.mutable.of();
        ImmutableBag<String> bag = this.newBag();
        bag.forEachWith((argument1, argument2) -> result.add(argument1 + argument2), "");
        Assertions.assertEquals(bag, result);
    }

    @Override
    @Test
    @Test
    public void forEachWithIndex()
    {
        super.forEachWithIndex();

        MutableBag<String> result = Bags.mutable.of();
        ImmutableBag<String> strings = this.newBag();
        strings.forEachWithIndex(ObjectIntProcedures.fromProcedure(result::add));
        Assertions.assertEquals(strings, result);
    }

    @Test
    public void selectByOccurrences()
    {
        ImmutableBag<String> strings = this.newBag().selectByOccurrences(IntPredicates.isEven());
        ImmutableBag<Integer> collect = strings.collect(Integer::valueOf);
        Verify.assertAllSatisfy(collect, IntegerPredicates.isEven());
    }

    @Override
    @Test
    @Test
    public void select()
    {
        super.select();

        ImmutableBag<String> strings = this.newBag();
        Verify.assertContainsAll(
                FastList.newList(strings.select(Predicates.greaterThan("0"))),
                strings.toArray());
        Verify.assertIterableEmpty(strings.select(Predicates.lessThan("0")));
        Verify.assertIterableSize(strings.size() - 1, strings.select(Predicates.greaterThan("1")));
    }

    @Override
    @Test
    @Test
    public void selectWith()
    {
        super.selectWith();

        ImmutableBag<String> strings = this.newBag();

        Assertions.assertEquals(strings, strings.selectWith(Predicates2.<String>greaterThan(), "0"));
    }

    @Test
    public void selectWithToTarget()
    {
        ImmutableBag<String> strings = this.newBag();

        Assertions.assertEquals(
                strings,
                strings.selectWith(Predicates2.<String>greaterThan(), "0", FastList.<String>newList()).toBag());
    }

    @Test
    public void selectToTarget()
    {
        ImmutableBag<String> strings = this.newBag();
        Assertions.assertEquals(strings, strings.select(Predicates.greaterThan("0"), FastList.<String>newList()).toBag());
        Verify.assertEmpty(strings.select(Predicates.lessThan("0"), FastList.<String>newList()));
    }

    @Override
    @Test
    @Test
    public void reject()
    {
        super.reject();

        ImmutableBag<String> strings = this.newBag();
        Verify.assertIterableEmpty(strings.reject(Predicates.greaterThan("0")));
        Assertions.assertEquals(strings, strings.reject(Predicates.lessThan("0")));
        Verify.assertIterableSize(strings.size() - 1, strings.reject(Predicates.lessThan("2")));
    }

    @Override
    @Test
    @Test
    public void rejectWith()
    {
        super.rejectWith();

        ImmutableBag<String> strings = this.newBag();

        Assertions.assertEquals(strings, strings.rejectWith(Predicates2.<String>lessThan(), "0"));
    }

    @Test
    public void rejectWithToTarget()
    {
        ImmutableBag<String> strings = this.newBag();
        Assertions.assertEquals(strings, strings.reject(Predicates.lessThan("0")));

        Verify.assertEmpty(strings.rejectWith(Predicates2.<String>greaterThan(), "0", FastList.<String>newList()));
    }

    @Test
    public void rejectToTarget()
    {
        ImmutableBag<String> strings = this.newBag();
        Assertions.assertEquals(strings, strings.reject(Predicates.lessThan("0"), FastList.<String>newList()).toBag());
        Verify.assertEmpty(strings.reject(Predicates.greaterThan("0"), FastList.<String>newList()));
    }

    @Override
    @Test
    @Test
    public void partition()
    {
        super.partition();

        ImmutableBag<String> strings = this.newBag();
        PartitionImmutableBag<String> partition = strings.partition(Predicates.greaterThan("0"));
        Assertions.assertEquals(strings, partition.getSelected());
        Verify.assertIterableEmpty(partition.getRejected());

        Verify.assertIterableSize(strings.size() - 1, strings.partition(Predicates.greaterThan("1")).getSelected());
    }

    @Override
    @Test
    @Test
    public void partitionWith()
    {
        super.partitionWith();

        ImmutableBag<String> strings = this.newBag();
        PartitionImmutableBag<String> partition = strings.partitionWith(Predicates2.<String>greaterThan(), "0");
        Assertions.assertEquals(strings, partition.getSelected());
        Verify.assertIterableEmpty(partition.getRejected());

        Verify.assertIterableSize(strings.size() - 1, strings.partitionWith(Predicates2.<String>greaterThan(), "1").getSelected());
    }

    @Override
    @Test
    @Test
    public void collect()
    {
        super.collect();

        Assertions.assertEquals(this.newBag(), this.newBag().collect(Functions.getStringPassThru()));
    }

    @Override
    @Test
    @Test
    public void collectBoolean()
    {
        super.collectBoolean();

        ImmutableBooleanBag result = this.newBag().collectBoolean("4"::equals);
        Assertions.assertEquals(2, result.sizeDistinct());
        Assertions.assertEquals(4, result.occurrencesOf(true));
        Assertions.assertEquals(6, result.occurrencesOf(false));
    }

    @Override
    @Test
    @Test
    public void collectBooleanWithTarget()
    {
        super.collectBooleanWithTarget();

        BooleanHashBag target = new BooleanHashBag();
        BooleanHashBag result = this.newBag().collectBoolean("4"::equals, target);
        Assertions.assertSame(target, result, "Target sent as parameter not returned");
        Assertions.assertEquals(2, result.sizeDistinct());
        Assertions.assertEquals(4, result.occurrencesOf(true));
        Assertions.assertEquals(6, result.occurrencesOf(false));
    }

    @Override
    @Test
    @Test
    public void collectByte()
    {
        super.collectByte();

        ImmutableByteBag result = this.newBag().collectByte(Byte::parseByte);
        Assertions.assertEquals(this.numKeys(), result.sizeDistinct());
        for (int i = 1; i <= this.numKeys(); i++)
        {
            Assertions.assertEquals(i, result.occurrencesOf((byte) i));
        }
    }

    @Override
    @Test
    @Test
    public void collectByteWithTarget()
    {
        super.collectByteWithTarget();

        ByteHashBag target = new ByteHashBag();
        ByteHashBag result = this.newBag().collectByte(Byte::parseByte, target);
        Assertions.assertSame(target, result, "Target sent as parameter not returned");
        Assertions.assertEquals(this.numKeys(), result.sizeDistinct());
        for (int i = 1; i <= this.numKeys(); i++)
        {
            Assertions.assertEquals(i, result.occurrencesOf((byte) i));
        }
    }

    @Override
    @Test
    @Test
    public void collectChar()
    {
        super.collectChar();

        ImmutableCharBag result = this.newBag().collectChar((CharFunction<String>) string -> string.charAt(0));
        Assertions.assertEquals(this.numKeys(), result.sizeDistinct());
        for (int i = 1; i <= this.numKeys(); i++)
        {
            Assertions.assertEquals(i, result.occurrencesOf((char) ('0' + i)));
        }
    }

    @Override
    @Test
    @Test
    public void collectCharWithTarget()
    {
        super.collectCharWithTarget();

        CharHashBag target = new CharHashBag();
        CharHashBag result = this.newBag().collectChar((CharFunction<String>) string -> string.charAt(0), target);
        Assertions.assertSame(target, result, "Target sent as parameter not returned");
        Assertions.assertEquals(this.numKeys(), result.sizeDistinct());
        for (int i = 1; i <= this.numKeys(); i++)
        {
            Assertions.assertEquals(i, result.occurrencesOf((char) ('0' + i)));
        }
    }

    @Override
    @Test
    @Test
    public void collectDouble()
    {
        super.collectDouble();

        ImmutableDoubleBag result = this.newBag().collectDouble(Double::parseDouble);
        Assertions.assertEquals(this.numKeys(), result.sizeDistinct());
        for (int i = 1; i <= this.numKeys(); i++)
        {
            Assertions.assertEquals(i, result.occurrencesOf(i));
        }
    }

    @Override
    @Test
    @Test
    public void collectDoubleWithTarget()
    {
        super.collectDoubleWithTarget();

        DoubleHashBag target = new DoubleHashBag();
        DoubleHashBag result = this.newBag().collectDouble(Double::parseDouble, target);
        Assertions.assertSame(target, result, "Target sent as parameter not returned");
        Assertions.assertEquals(this.numKeys(), result.sizeDistinct());
        for (int i = 1; i <= this.numKeys(); i++)
        {
            Assertions.assertEquals(i, result.occurrencesOf(i));
        }
    }

    @Override
    @Test
    @Test
    public void collectFloat()
    {
        super.collectFloat();

        ImmutableFloatBag result = this.newBag().collectFloat(Float::parseFloat);
        Assertions.assertEquals(this.numKeys(), result.sizeDistinct());
        for (int i = 1; i <= this.numKeys(); i++)
        {
            Assertions.assertEquals(i, result.occurrencesOf(i));
        }
    }

    @Override
    @Test
    @Test
    public void collectFloatWithTarget()
    {
        super.collectFloatWithTarget();

        FloatHashBag target = new FloatHashBag();
        FloatHashBag result = this.newBag().collectFloat(Float::parseFloat, target);
        Assertions.assertSame(target, result, "Target sent as parameter not returned");
        Assertions.assertEquals(this.numKeys(), result.sizeDistinct());
        for (int i = 1; i <= this.numKeys(); i++)
        {
            Assertions.assertEquals(i, result.occurrencesOf(i));
        }
    }

    @Override
    @Test
    @Test
    public void collectInt()
    {
        super.collectInt();

        ImmutableIntBag result = this.newBag().collectInt(Integer::parseInt);
        Assertions.assertEquals(this.numKeys(), result.sizeDistinct());
        for (int i = 1; i <= this.numKeys(); i++)
        {
            Assertions.assertEquals(i, result.occurrencesOf(i));
        }
    }

    @Override
    @Test
    @Test
    public void collectIntWithTarget()
    {
        super.collectIntWithTarget();

        IntHashBag target = new IntHashBag();
        IntHashBag result = this.newBag().collectInt(Integer::parseInt, target);
        Assertions.assertSame(target, result, "Target sent as parameter not returned");
        Assertions.assertEquals(this.numKeys(), result.sizeDistinct());
        for (int i = 1; i <= this.numKeys(); i++)
        {
            Assertions.assertEquals(i, result.occurrencesOf(i));
        }
    }

    @Override
    @Test
    @Test
    public void collectLong()
    {
        super.collectLong();

        ImmutableLongBag result = this.newBag().collectLong(Long::parseLong);
        Assertions.assertEquals(this.numKeys(), result.sizeDistinct());
        for (int i = 1; i <= this.numKeys(); i++)
        {
            Assertions.assertEquals(i, result.occurrencesOf(i));
        }
    }

    @Override
    @Test
    @Test
    public void collectLongWithTarget()
    {
        super.collectLongWithTarget();

        LongHashBag target = new LongHashBag();
        LongHashBag result = this.newBag().collectLong(Long::parseLong, target);
        Assertions.assertSame(target, result, "Target sent as parameter not returned");
        Assertions.assertEquals(this.numKeys(), result.sizeDistinct());
        for (int i = 1; i <= this.numKeys(); i++)
        {
            Assertions.assertEquals(i, result.occurrencesOf(i));
        }
    }

    @Override
    @Test
    @Test
    public void collectShort()
    {
        super.collectShort();

        ImmutableShortBag result = this.newBag().collectShort(Short::parseShort);
        Assertions.assertEquals(this.numKeys(), result.sizeDistinct());
        for (int i = 1; i <= this.numKeys(); i++)
        {
            Assertions.assertEquals(i, result.occurrencesOf((short) i));
        }
    }

    @Override
    @Test
    @Test
    public void collectShortWithTarget()
    {
        super.collectShortWithTarget();

        ShortHashBag target = new ShortHashBag();
        ShortHashBag result = this.newBag().collectShort(Short::parseShort, target);
        Assertions.assertEquals(this.numKeys(), result.sizeDistinct());
        for (int i = 1; i <= this.numKeys(); i++)
        {
            Assertions.assertEquals(i, result.occurrencesOf((short) i));
        }
    }

    private Function2<String, String, String> generateAssertingPassThroughFunction2(String valueToAssert)
    {
        return (argument1, argument2) -> {
            Assertions.assertEquals(valueToAssert, argument2);
            return argument1;
        };
    }

    @Override
    @Test
    @Test
    public void collectWith()
    {
        super.collectWith();

        ImmutableBag<String> strings = this.newBag();

        String argument = "thing";
        Assertions.assertEquals(strings, strings.collectWith(this.generateAssertingPassThroughFunction2(argument), argument));
    }

    @Override
    @Test
    @Test
    public void collectWith_target()
    {
        super.collectWith_target();

        ImmutableBag<String> strings = this.newBag();

        String argument = "thing";
        HashBag<String> targetCollection = HashBag.<String>newBag();
        HashBag<String> actual = strings.collectWith(this.generateAssertingPassThroughFunction2(argument), argument, targetCollection);
        Assertions.assertEquals(strings, actual);
        Assertions.assertSame(targetCollection, actual);
    }

    @Test
    public void collect_target()
    {
        ImmutableBag<String> strings = this.newBag();
        HashBag<String> target = HashBag.<String>newBag();
        HashBag<String> actual = strings.collect(Functions.getStringPassThru(), target);
        Assertions.assertEquals(strings, actual);
        Assertions.assertSame(target, actual);
        Assertions.assertEquals(strings, strings.collect(Functions.getStringPassThru(), FastList.<String>newList()).toBag());
    }

    @Override
    @Test
    @Test
    public void flatCollect()
    {
        super.flatCollect();

        ImmutableBag<String> actual = this.newBag().flatCollect(Lists.fixedSize::of);

        ImmutableBag<String> expected = this.newBag().collect(String::valueOf);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void flatCollectWithTarget()
    {
        MutableBag<String> actual = this.newBag().flatCollect(Lists.fixedSize::of, HashBag.<String>newBag());

        ImmutableBag<String> expected = this.newBag().collect(String::valueOf);

        Assertions.assertEquals(expected, actual);
    }

    @Override
    @Test
    @Test
    public void detect()
    {
        super.detect();

        ImmutableBag<String> strings = this.newBag();
        Assertions.assertEquals("1", strings.detect("1"::equals));
        Assertions.assertNull(strings.detect(String.valueOf(this.numKeys() + 1)::equals));
    }

    @Override
    @Test
    @Test
    public void detectWith()
    {
        super.detectWith();

        ImmutableBag<String> immutableStrings = this.newBag();
        Assertions.assertEquals("1", immutableStrings.detectWith(Object::equals, "1"));
    }

    @Test
    public void detectWithIfNone()
    {
        ImmutableBag<String> immutableStrings = this.newBag();
        Assertions.assertEquals("1", immutableStrings.detectWithIfNone(Object::equals, "1", new PassThruFunction0<>("Not Found")));
        Assertions.assertEquals("Not Found", immutableStrings.detectWithIfNone(Object::equals, "10000", new PassThruFunction0<>("Not Found")));
    }

    @Override
    @Test
    @Test
    public void zip()
    {
        super.zip();

        ImmutableBag<String> immutableBag = this.newBag();
        List<Object> nulls = Collections.nCopies(immutableBag.size(), null);
        List<Object> nullsPlusOne = Collections.nCopies(immutableBag.size() + 1, null);
        List<Object> nullsMinusOne = Collections.nCopies(immutableBag.size() - 1, null);

        ImmutableBag<Pair<String, Object>> pairs = immutableBag.zip(nulls);
        Assertions.assertEquals(
                immutableBag,
                pairs.collect((Function<Pair<String, ?>, String>) Pair::getOne));
        Assertions.assertEquals(
                HashBag.newBag(nulls),
                pairs.collect((Function<Pair<?, Object>, Object>) Pair::getTwo));

        ImmutableBag<Pair<String, Object>> pairsPlusOne = immutableBag.zip(nullsPlusOne);
        Assertions.assertEquals(
                immutableBag,
                pairsPlusOne.collect((Function<Pair<String, ?>, String>) Pair::getOne));
        Assertions.assertEquals(
                HashBag.newBag(nulls),
                pairsPlusOne.collect((Function<Pair<?, Object>, Object>) Pair::getTwo));

        ImmutableBag<Pair<String, Object>> pairsMinusOne = immutableBag.zip(nullsMinusOne);
        Assertions.assertEquals(immutableBag.size() - 1, pairsMinusOne.size());
        Assertions.assertTrue(immutableBag.containsAllIterable(pairsMinusOne.collect((Function<Pair<String, ?>, String>) Pair::getOne)));

        Assertions.assertEquals(immutableBag.zip(nulls), immutableBag.zip(nulls, HashBag.<Pair<String, Object>>newBag()));
    }

    @Override
    @Test
    @Test
    public void zipWithIndex()
    {
        super.zipWithIndex();

        ImmutableBag<String> immutableBag = this.newBag();
        ImmutableSet<Pair<String, Integer>> pairs = immutableBag.zipWithIndex();

        Assertions.assertEquals(immutableBag, pairs.collect((Function<Pair<String, ?>, String>) Pair::getOne, HashBag.<String>newBag()));
        Assertions.assertEquals(Interval.zeroTo(immutableBag.size() - 1).toSet(), pairs.collect((Function<Pair<?, Integer>, Integer>) Pair::getTwo));

        Assertions.assertEquals(immutableBag.zipWithIndex(), immutableBag.zipWithIndex(UnifiedSet.<Pair<String, Integer>>newSet()));
    }

    @Override
    @Test(expected = IllegalArgumentException.class)
    @Test
    public void chunk_zero_throws()
    {
        super.chunk_zero_throws();

        this.newBag().chunk(0);
    }

    @Override
    @Test
    @Test
    public void chunk_large_size()
    {
        super.chunk_large_size();
        Assertions.assertEquals(this.newBag(), this.newBag().chunk(10).getFirst());
        Verify.assertInstanceOf(ImmutableBag.class, this.newBag().chunk(10).getFirst());
    }

    private ImmutableBag<String> classUnderTestWithNull()
    {
        return this.newBag().newWith(null);
    }

    @Override
    @Test(expected = NullPointerException.class)
    @Test
    public void min_null_throws()
    {
        this.classUnderTestWithNull().min(String::compareTo);
    }

    @Override
    @Test(expected = NullPointerException.class)
    @Test
    public void max_null_throws()
    {
        this.classUnderTestWithNull().max(String::compareTo);
    }

    @Override
    @Test
    @Test
    public void min()
    {
        super.min();

        Assertions.assertEquals("1", this.newBag().min(String::compareTo));
    }

    @Override
    @Test
    @Test
    public void max()
    {
        super.max();

        Assertions.assertEquals(String.valueOf(this.numKeys()), this.newBag().max(String::compareTo));
    }

    @Override
    @Test(expected = NullPointerException.class)
    @Test
    public void min_null_throws_without_comparator()
    {
        this.classUnderTestWithNull().min();
    }

    @Override
    @Test(expected = NullPointerException.class)
    @Test
    public void max_null_throws_without_comparator()
    {
        this.classUnderTestWithNull().max();
    }

    @Override
    @Test
    @Test
    public void min_without_comparator()
    {
        super.min_without_comparator();

        Assertions.assertEquals("1", this.newBag().min());
    }

    @Override
    @Test
    @Test
    public void max_without_comparator()
    {
        super.max_without_comparator();

        Assertions.assertEquals(String.valueOf(this.numKeys()), this.newBag().max());
    }

    @Override
    @Test
    @Test
    public void minBy()
    {
        super.minBy();

        Assertions.assertEquals("1", this.newBag().minBy(String::valueOf));
    }

    @Override
    @Test
    @Test
    public void maxBy()
    {
        super.maxBy();

        Assertions.assertEquals(String.valueOf(this.numKeys()), this.newBag().maxBy(String::valueOf));
    }

    @Override
    @Test
    @Test
    public void detectIfNone()
    {
        super.detectIfNone();

        ImmutableBag<String> strings = this.newBag();
        Function0<String> function = new PassThruFunction0<>(String.valueOf(this.numKeys() + 1));
        Assertions.assertEquals("1", strings.detectIfNone("1"::equals, function));
        Assertions.assertEquals(String.valueOf(this.numKeys() + 1), strings.detectIfNone(String.valueOf(this.numKeys() + 1)::equals, function));
    }

    @Override
    @Test
    @Test
    public void allSatisfy()
    {
        super.allSatisfy();

        ImmutableBag<String> strings = this.newBag();
        Assertions.assertTrue(strings.allSatisfy(String.class::isInstance));
        Assertions.assertFalse(strings.allSatisfy("0"::equals));
    }

    @Override
    @Test
    @Test
    public void anySatisfy()
    {
        super.anySatisfy();

        ImmutableBag<String> strings = this.newBag();
        Assertions.assertFalse(strings.anySatisfy(Integer.class::isInstance));
        Assertions.assertTrue(strings.anySatisfy(String.class::isInstance));
    }

    @Override
    @Test
    @Test
    public void noneSatisfy()
    {
        super.noneSatisfy();

        ImmutableBag<String> strings = this.newBag();
        Assertions.assertTrue(strings.noneSatisfy(Integer.class::isInstance));
        Assertions.assertTrue(strings.noneSatisfy("0"::equals));
    }

    @Override
    @Test
    @Test
    public void count()
    {
        super.count();

        ImmutableBag<String> strings = this.newBag();
        Assertions.assertEquals(strings.size(), strings.count(String.class::isInstance));
        Assertions.assertEquals(0, strings.count(Integer.class::isInstance));
    }

    @Override
    @Test
    @Test
    public void countWith()
    {
        super.countWith();

        ImmutableBag<String> strings = this.newBag();
        Assertions.assertEquals(strings.size(), strings.countWith(Predicates2.instanceOf(), String.class));
        Assertions.assertEquals(0, strings.countWith(Predicates2.instanceOf(), Integer.class));
    }

    @Override
    @Test
    @Test
    public void collectIf()
    {
        super.collectIf();

        ImmutableBag<String> strings = this.newBag();
        Assertions.assertEquals(
                strings,
                strings.collectIf(
                        String.class::isInstance,
                        Functions.getStringPassThru()));
    }

    @Test
    public void collectIfWithTarget()
    {
        ImmutableBag<String> strings = this.newBag();
        Assertions.assertEquals(
                strings,
                strings.collectIf(
                        String.class::isInstance,
                        Functions.getStringPassThru(),
                        HashBag.<String>newBag()));
    }

    @Override
    @Test
    @Test
    public void getFirst()
    {
        super.getFirst();

        // Cannot assert much here since there's no order.
        ImmutableBag<String> bag = this.newBag();
        Assertions.assertTrue(bag.contains(bag.getFirst()));
    }

    @Override
    @Test
    @Test
    public void getLast()
    {
        super.getLast();

        // Cannot assert much here since there's no order.
        ImmutableBag<String> bag = this.newBag();
        Assertions.assertTrue(bag.contains(bag.getLast()));
    }

    @Override
    @Test
    @Test
    public void isEmpty()
    {
        super.isEmpty();

        ImmutableBag<String> bag = this.newBag();
        Assertions.assertFalse(bag.isEmpty());
        Assertions.assertTrue(bag.notEmpty());
    }

    @Override
    @Test
    @Test
    public void iterator()
    {
        super.iterator();

        ImmutableBag<String> strings = this.newBag();
        MutableBag<String> result = Bags.mutable.of();
        Iterator<String> iterator = strings.iterator();
        for (int i = 0; iterator.hasNext(); i++)
        {
            String string = iterator.next();
            result.add(string);
        }
        Assertions.assertEquals(strings, result);

        Verify.assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Override
    @Test
    @Test
    public void injectInto()
    {
        super.injectInto();

        ImmutableBag<Integer> integers = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        Integer result = integers.injectInto(0, AddFunction.INTEGER);
        Assertions.assertEquals(FastList.newList(integers).injectInto(0, AddFunction.INTEGER_TO_INT), result.intValue());
        String result1 = this.newBag().injectInto("0", String::concat);
        Assertions.assertEquals(FastList.newList(this.newBag()).injectInto("0", String::concat), result1);
    }

    @Override
    @Test
    @Test
    public void injectIntoInt()
    {
        super.injectIntoInt();

        ImmutableBag<Integer> integers = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        int result = integers.injectInto(0, AddFunction.INTEGER_TO_INT);
        Assertions.assertEquals(FastList.newList(integers).injectInto(0, AddFunction.INTEGER_TO_INT), result);
    }

    @Override
    @Test
    @Test
    public void injectIntoLong()
    {
        super.injectIntoLong();

        ImmutableBag<Integer> integers = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        long result = integers.injectInto(0, AddFunction.INTEGER_TO_LONG);
        Assertions.assertEquals(FastList.newList(integers).injectInto(0, AddFunction.INTEGER_TO_INT), result);
    }

    @Override
    @Test
    @Test
    public void injectIntoDouble()
    {
        super.injectIntoDouble();

        ImmutableBag<Integer> integers = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        double result = integers.injectInto(0, AddFunction.INTEGER_TO_DOUBLE);
        double expected = FastList.newList(integers).injectInto(0, AddFunction.INTEGER_TO_DOUBLE);
        Assertions.assertEquals(expected, result, 0.001);
    }

    @Override
    @Test
    @Test
    public void injectIntoFloat()
    {
        super.injectIntoFloat();

        ImmutableBag<Integer> integers = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        float result = integers.injectInto(0, AddFunction.INTEGER_TO_FLOAT);
        float expected = FastList.newList(integers).injectInto(0, AddFunction.INTEGER_TO_FLOAT);
        Assertions.assertEquals(expected, result, 0.001);
    }

    @Override
    @Test
    @Test
    public void sumFloat()
    {
        super.sumFloat();

        ImmutableBag<Integer> integers = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        double result = integers.sumOfFloat(Integer::floatValue);
        float expected = FastList.newList(integers).injectInto(0, AddFunction.INTEGER_TO_FLOAT);
        Assertions.assertEquals(expected, result, 0.001);
    }

    @Override
    @Test
    @Test
    public void sumDouble()
    {
        super.sumDouble();

        ImmutableBag<Integer> integers = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        double result = integers.sumOfDouble(Integer::doubleValue);
        double expected = FastList.newList(integers).injectInto(0, AddFunction.INTEGER_TO_DOUBLE);
        Assertions.assertEquals(expected, result, 0.001);
    }

    @Override
    @Test
    @Test
    public void sumInteger()
    {
        super.sumInteger();

        ImmutableBag<Integer> integers = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        long result = integers.sumOfInt(integer -> integer);
        int expected = FastList.newList(integers).injectInto(0, AddFunction.INTEGER_TO_INT);
        Assertions.assertEquals(expected, result);
    }

    @Override
    @Test
    @Test
    public void sumLong()
    {
        super.sumLong();

        ImmutableBag<Integer> integers = this.newWith(4, 4, 4, 4, 3, 3, 3, 2, 2, 1);
        long result = integers.sumOfLong(Integer::longValue);
        long expected = FastList.newList(integers).injectInto(0, AddFunction.INTEGER_TO_LONG);
        Assertions.assertEquals(expected, result);
    }

    @Override
    @Test
    @Test
    public void toArray()
    {
        super.toArray();

        ImmutableBag<String> bag = this.newBag();
        Object[] array = bag.toArray();
        Verify.assertSize(bag.size(), array);

        String[] array2 = bag.toArray(new String[bag.size() + 1]);
        Verify.assertSize(bag.size() + 1, array2);
        Assertions.assertNull(array2[bag.size()]);
    }

    @Override
    @Test
    @Test
    public void testToString()
    {
        super.testToString();

        String string = this.newBag().toString();
        for (int i = 1; i < this.numKeys(); i++)
        {
            Assertions.assertEquals(i, StringIterate.occurrencesOf(string, String.valueOf(i)));
        }
    }

    @Override
    @Test
    @Test
    public void toList()
    {
        super.toList();

        ImmutableBag<String> strings = this.newBag();
        MutableList<String> list = strings.toList();
        Verify.assertEqualsAndHashCode(FastList.newList(strings), list);
    }

    @Test
    public void toSortedList()
    {
        ImmutableBag<String> strings = this.newBag();
        MutableList<String> copy = FastList.newList(strings);
        MutableList<String> list = strings.toSortedList(Collections.<String>reverseOrder());
        Assertions.assertEquals(copy.sortThis(Collections.<String>reverseOrder()), list);
        MutableList<String> list2 = strings.toSortedList();
        Assertions.assertEquals(copy.sortThis(), list2);
    }

    @Override
    @Test
    @Test
    public void toSortedListBy()
    {
        super.toSortedListBy();

        MutableList<String> expected = this.newBag().toList();
        Collections.sort(expected);
        ImmutableBag<String> immutableBag = this.newBag();
        MutableList<String> sortedList = immutableBag.toSortedListBy(String::valueOf);
        Assertions.assertEquals(expected, sortedList);
    }

    @Test
    public void forLoop()
    {
        ImmutableBag<String> bag = this.newBag();
        for (String each : bag)
        {
            Assertions.assertNotNull(each);
        }
    }

    @Test
    public void iteratorRemove()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.newBag().iterator().remove());
    }

    @Test
    public void toMapOfItemToCount()
    {
        MapIterable<String, Integer> mapOfItemToCount = this.newBag().toMapOfItemToCount();

        for (int i = 1; i <= this.numKeys(); i++)
        {
            String key = String.valueOf(i);
            Assertions.assertTrue(mapOfItemToCount.containsKey(key));
            Assertions.assertEquals(Integer.valueOf(i), mapOfItemToCount.get(key));
        }

        String missingKey = "0";
        Assertions.assertFalse(mapOfItemToCount.containsKey(missingKey));
        Assertions.assertNull(mapOfItemToCount.get(missingKey));
    }

    @Test
    public void toImmutable()
    {
        ImmutableBag<String> bag = this.newBag();
        Assertions.assertSame(bag, bag.toImmutable());
    }

    @Override
    @Test
    @Test
    public void groupBy()
    {
        super.groupBy();

        ImmutableBagMultimap<Boolean, String> multimap = this.newBag().groupBy(string -> IntegerPredicates.isOdd().accept(Integer.valueOf(string)));

        this.groupByAssertions(multimap);
    }

    @Test
    public void groupBy_with_target()
    {
        ImmutableBagMultimap<Boolean, String> multimap = this.newBag().groupBy(string -> IntegerPredicates.isOdd().accept(Integer.valueOf(string)), new HashBagMultimap<>()).toImmutable();

        this.groupByAssertions(multimap);
    }

    @Override
    @Test
    @Test
    public void groupByEach()
    {
        super.groupByEach();

        ImmutableBag<Integer> immutableBag = this.newBag().collect(Integer::valueOf);

        MutableMultimap<Integer, Integer> expected = HashBagMultimap.newMultimap();
        int keys = this.numKeys();
        immutableBag.forEachWithOccurrences((each, parameter) -> {
            HashBag<Integer> bag = HashBag.newBag();
            Interval.fromTo(each, keys).forEach((int eachInt) -> bag.addOccurrences(eachInt, eachInt));
            expected.putAll(-each, bag);
        });
        Multimap<Integer, Integer> actual =
                immutableBag.groupByEach(new NegativeIntervalFunction());
        Assertions.assertEquals(expected, actual);

        Multimap<Integer, Integer> actualWithTarget =
                immutableBag.groupByEach(new NegativeIntervalFunction(), HashBagMultimap.<Integer, Integer>newMultimap());
        Assertions.assertEquals(expected, actualWithTarget);
    }

    private void groupByAssertions(ImmutableBagMultimap<Boolean, String> multimap)
    {
        Verify.assertIterableEmpty(multimap.get(null));

        ImmutableBag<String> odds = multimap.get(true);
        ImmutableBag<String> evens = multimap.get(false);
        for (int i = 1; i <= this.numKeys(); i++)
        {
            String key = String.valueOf(i);
            ImmutableBag<String> containingBag = IntegerPredicates.isOdd().accept(i) ? odds : evens;
            ImmutableBag<String> nonContainingBag = IntegerPredicates.isOdd().accept(i) ? evens : odds;
            Assertions.assertTrue(containingBag.contains(key));
            Assertions.assertFalse(nonContainingBag.contains(key));

            Assertions.assertEquals(i, containingBag.occurrencesOf(key));
        }
    }

    @Override
    @Test(expected = IllegalStateException.class)
    @Test
    public abstract void groupByUniqueKey();

    @Test(expected = IllegalStateException.class)
    public void groupByUniqueKey_throws()
    {
        this.newBag().groupByUniqueKey(id -> id);
    }

    @Override
    @Test(expected = IllegalStateException.class)
    @Test
    public abstract void groupByUniqueKey_target();

    @Test(expected = IllegalStateException.class)
    public void groupByUniqueKey_target_throws()
    {
        this.newBag().groupByUniqueKey(id -> id, UnifiedMap.newWithKeysValues("1", "1"));
    }

    @Override
    @Test
    @Test
    public void toSet()
    {
        super.toSet();

        MutableSet<String> expectedSet = this.numKeys() == 0
                ? UnifiedSet.<String>newSet()
                : Interval.oneTo(this.numKeys()).collect(String::valueOf).toSet();
        Assertions.assertEquals(expectedSet, this.newBag().toSet());
    }

    @Override
    @Test
    @Test
    public void toBag()
    {
        super.toBag();

        ImmutableBag<String> immutableBag = this.newBag();
        MutableBag<String> mutableBag = immutableBag.toBag();
        Assertions.assertEquals(immutableBag, mutableBag);
    }

    @Override
    @Test
    @Test
    public void toMap()
    {
        super.toMap();

        MutableMap<String, String> map = this.newBag().toMap(Functions.<String>getPassThru(), Functions.<String>getPassThru());

        for (int i = 1; i <= this.numKeys(); i++)
        {
            String key = String.valueOf(i);
            Assertions.assertTrue(map.containsKey(key));
            Assertions.assertEquals(key, map.get(key));
        }

        String missingKey = "0";
        Assertions.assertFalse(map.containsKey(missingKey));
        Assertions.assertNull(map.get(missingKey));
    }

    @Override
    @Test
    @Test
    public void toSortedMap()
    {
        super.toSortedMap();

        MutableSortedMap<Integer, String> map = this.newBag().toSortedMap(Integer::valueOf, Functions.<String>getPassThru());

        Verify.assertMapsEqual(this.newBag().toMap(Integer::valueOf, Functions.<String>getPassThru()), map);
        Verify.assertListsEqual(Interval.oneTo(this.numKeys()), map.keySet().toList());
    }

    @Override
    @Test
    @Test
    public void toSortedMap_with_comparator()
    {
        super.toSortedMap_with_comparator();

        MutableSortedMap<Integer, String> map = this.newBag().toSortedMap(Comparators.<Integer>reverseNaturalOrder(),
                Integer::valueOf, Functions.<String>getPassThru());

        Verify.assertMapsEqual(this.newBag().toMap(Integer::valueOf, Functions.<String>getPassThru()), map);
        Verify.assertListsEqual(Interval.fromTo(this.numKeys(), 1), map.keySet().toList());
    }

    @Test
    public void asLazy()
    {
        ImmutableBag<String> bag = this.newBag();
        LazyIterable<String> lazyIterable = bag.asLazy();
        Verify.assertInstanceOf(LazyIterable.class, lazyIterable);
        Assertions.assertEquals(bag, lazyIterable.toBag());
    }

    @Override
    @Test
    @Test
    public void makeString()
    {
        super.makeString();

        ImmutableBag<String> bag = this.newBag();
        Assertions.assertEquals(FastList.newList(bag).makeString(), bag.makeString());
        Assertions.assertEquals(bag.toString(), '[' + bag.makeString() + ']');
        Assertions.assertEquals(bag.toString(), '[' + bag.makeString(", ") + ']');
        Assertions.assertEquals(bag.toString(), bag.makeString("[", ", ", "]"));
    }

    @Override
    @Test
    @Test
    public void appendString()
    {
        super.appendString();

        ImmutableBag<String> bag = this.newBag();

        Appendable builder = new StringBuilder();
        bag.appendString(builder);
        Assertions.assertEquals(FastList.newList(bag).makeString(), builder.toString());
    }

    @Test
    public void appendString_with_separator()
    {
        ImmutableBag<String> bag = this.newBag();

        Appendable builder = new StringBuilder();
        bag.appendString(builder, ", ");
        Assertions.assertEquals(bag.toString(), '[' + builder.toString() + ']');
    }

    @Test
    public void appendString_with_start_separator_end()
    {
        ImmutableBag<String> bag = this.newBag();

        Appendable builder = new StringBuilder();
        bag.appendString(builder, "[", ", ", "]");
        Assertions.assertEquals(bag.toString(), builder.toString());
    }

    @Test
    public void serialization()
    {
        ImmutableBag<String> bag = this.newBag();
        Verify.assertPostSerializedEqualsAndHashCode(bag);
    }

    @Test
    public void toSortedBag()
    {
        ImmutableBag<String> immutableBag = this.newBag();
        MutableSortedBag<String> sortedBag = immutableBag.toSortedBag();

        Verify.assertSortedBagsEqual(TreeBag.newBagWith("1", "2", "2", "3", "3", "3", "4", "4", "4", "4"), sortedBag);

        MutableSortedBag<String> reverse = immutableBag.toSortedBag(Comparator.<String>reverseOrder());
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparator.reverseOrder(), "1", "2", "2", "3", "3", "3", "4", "4", "4", "4"), reverse);
    }

    @Override
    @Test
    @Test
    public void toSortedBagBy()
    {
        super.toSortedBagBy();

        ImmutableBag<String> immutableBag = this.newBag();
        MutableSortedBag<String> sortedBag = immutableBag.toSortedBagBy(String::valueOf);

        Verify.assertSortedBagsEqual(TreeBag.newBagWith("1", "2", "2", "3", "3", "3", "4", "4", "4", "4"), sortedBag);
    }
}
