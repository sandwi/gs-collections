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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import com.gs.collections.api.bag.ImmutableBag;
import com.gs.collections.api.bag.primitive.ImmutableBooleanBag;
import com.gs.collections.api.bag.sorted.MutableSortedBag;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.sorted.MutableSortedMap;
import com.gs.collections.api.partition.bag.PartitionImmutableBag;
import com.gs.collections.api.set.ImmutableSet;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.bag.mutable.primitive.BooleanHashBag;
import com.gs.collections.impl.bag.sorted.mutable.TreeBag;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.function.PassThruFunction0;
import com.gs.collections.impl.factory.Bags;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.map.sorted.mutable.TreeSortedMap;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.gs.collections.impl.factory.Iterables.iBag;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ImmutableEmptyBagTest extends ImmutableBagTestCase
{
    public static final Predicate<String> ERROR_THROWING_PREDICATE = each -> { throw new AssertionError(); };

    public static final Predicates2<String, Class<Integer>> ERROR_THROWING_PREDICATE_2 = new Predicates2<String, Class<Integer>>()
    {
        public boolean accept(String argument1, Class<Integer> argument2)
        {
            throw new AssertionError();
        }
    };

    @Override
    protected ImmutableBag<String> newBag()
    {
        return (ImmutableBag<String>) ImmutableEmptyBag.INSTANCE;
    }

    @Override
    protected int numKeys()
    {
        return 0;
    }

    @Test
    public void testFactory()
    {
        Verify.assertInstanceOf(ImmutableEmptyBag.class, Bags.immutable.of());
    }

    @Test
    @Override
    public void newWith()
    {
        ImmutableBag<String> bag = this.newBag();
        ImmutableBag<String> newBag = bag.newWith("1");
        Assertions.assertNotEquals(bag, newBag);
        Assertions.assertEquals(newBag.size(), bag.size() + 1);
        ImmutableBag<String> newBag2 = bag.newWith("5");
        Assertions.assertNotEquals(bag, newBag2);
        Assertions.assertEquals(newBag2.size(), bag.size() + 1);
        Assertions.assertEquals(1, newBag2.sizeDistinct());
    }

    @Test
    @Override
    public void select()
    {
        ImmutableBag<String> strings = this.newBag();
        Verify.assertIterableEmpty(strings.select(Predicates.lessThan("0")));
    }

    @Test
    @Override
    public void reject()
    {
        ImmutableBag<String> strings = this.newBag();
        Verify.assertIterableEmpty(strings.reject(Predicates.greaterThan("0")));
    }

    @Override
    @Test
    public void partition()
    {
        PartitionImmutableBag<String> partition = this.newBag().partition(Predicates.lessThan("0"));
        Verify.assertIterableEmpty(partition.getSelected());
        Verify.assertIterableEmpty(partition.getRejected());
    }

    @Override
    @Test
    public void partitionWith()
    {
        PartitionImmutableBag<String> partition = this.newBag().partitionWith(Predicates2.<String>lessThan(), "0");
        Verify.assertIterableEmpty(partition.getSelected());
        Verify.assertIterableEmpty(partition.getRejected());
    }

    @Override
    @Test
    public void selectInstancesOf()
    {
        ImmutableBag<Number> numbers = Bags.immutable.of();
        Assertions.assertEquals(iBag(), numbers.selectInstancesOf(Integer.class));
        Assertions.assertEquals(iBag(), numbers.selectInstancesOf(Double.class));
        Assertions.assertEquals(iBag(), numbers.selectInstancesOf(Number.class));
    }

    @Override
    @Test
    public void testToString()
    {
        super.testToString();
        Assertions.assertEquals("[]", this.newBag().toString());
    }

    @Override
    @Test
    public void testSize()
    {
        Verify.assertIterableSize(0, this.newBag());
    }

    @Override
    @Test
    public void newWithout()
    {
        Assertions.assertSame(this.newBag(), this.newBag().newWithout("1"));
    }

    @Override
    @Test
    public void toStringOfItemToCount()
    {
        Assertions.assertEquals("{}", Bags.immutable.of().toStringOfItemToCount());
    }

    @Override
    @Test
    public void detect()
    {
        Assertions.assertNull(this.newBag().detect("1"::equals));
    }

    @Override
    @Test
    public void detectWith()
    {
        Assertions.assertNull(this.newBag().detectWith(Predicates2.<String>greaterThan(), "3"));
    }

    @Override
    @Test
    public void detectWithIfNone()
    {
        Assertions.assertEquals("Not Found", this.newBag().detectWithIfNone(Object::equals, "1", new PassThruFunction0<>("Not Found")));
    }

    @Override
    @Test
    public void detectIfNone()
    {
        super.detectIfNone();

        Assertions.assertEquals("Not Found", this.newBag().detectIfNone("2"::equals, new PassThruFunction0<>("Not Found")));
    }

    @Override
    @Test
    public void allSatisfy()
    {
        ImmutableBag<String> strings = this.newBag();
        Assertions.assertTrue(strings.allSatisfy(ERROR_THROWING_PREDICATE));
    }

    @Override
    @Test
    public void anySatisfy()
    {
        ImmutableBag<String> strings = this.newBag();
        Assertions.assertFalse(strings.anySatisfy(ERROR_THROWING_PREDICATE));
    }

    @Override
    @Test
    public void noneSatisfy()
    {
        ImmutableBag<String> strings = this.newBag();
        Assertions.assertTrue(strings.noneSatisfy(ERROR_THROWING_PREDICATE));
    }

    @Override
    @Test
    public void allSatisfyWith()
    {
        ImmutableBag<String> strings = this.newBag();
        Assertions.assertTrue(strings.allSatisfyWith(ERROR_THROWING_PREDICATE_2, Integer.class));
    }

    @Override
    @Test
    public void anySatisfyWith()
    {
        ImmutableBag<String> strings = this.newBag();
        Assertions.assertFalse(strings.anySatisfyWith(ERROR_THROWING_PREDICATE_2, Integer.class));
    }

    @Override
    @Test
    public void noneSatisfyWith()
    {
        ImmutableBag<String> strings = this.newBag();
        Assertions.assertTrue(strings.noneSatisfyWith(ERROR_THROWING_PREDICATE_2, Integer.class));
    }

    @Override
    @Test
    public void getFirst()
    {
        Assertions.assertNull(this.newBag().getFirst());
    }

    @Override
    @Test
    public void getLast()
    {
        Assertions.assertNull(this.newBag().getLast());
    }

    @Override
    @Test
    public void isEmpty()
    {
        ImmutableBag<String> bag = this.newBag();
        Assertions.assertTrue(bag.isEmpty());
        Assertions.assertFalse(bag.notEmpty());
    }

    @Override
    @Test
    public void min()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.newBag().min(String::compareTo);
        });
    }

    @Override
    @Test
    public void max()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.newBag().max(String::compareTo);
        });
    }

    @Test
    @Override
    public void min_null_throws()
    {
        super.min_null_throws();
    }

    @Test
    @Override
    public void max_null_throws()
    {
        super.max_null_throws();
    }

    @Override
    @Test
    public void min_without_comparator()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.newBag().min();
        });
    }

    @Override
    @Test
    public void max_without_comparator()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.newBag().max();
        });
    }

    @Test
    @Override
    public void min_null_throws_without_comparator()
    {
        // Not applicable for empty collections
        super.min_null_throws_without_comparator();
    }

    @Test
    @Override
    public void max_null_throws_without_comparator()
    {
        // Not applicable for empty collections
        super.max_null_throws_without_comparator();
    }

    @Override
    @Test
    public void minBy()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.newBag().minBy(String::valueOf);
        });
    }

    @Override
    @Test
    public void maxBy()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.newBag().maxBy(String::valueOf);
        });
    }

    @Override
    @Test
    public void zip()
    {
        ImmutableBag<String> immutableBag = this.newBag();
        List<Object> nulls = Collections.nCopies(immutableBag.size(), null);
        List<Object> nullsPlusOne = Collections.nCopies(immutableBag.size() + 1, null);

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

        Assertions.assertEquals(immutableBag.zip(nulls), immutableBag.zip(nulls, HashBag.<Pair<String, Object>>newBag()));
    }

    @Override
    @Test
    public void zipWithIndex()
    {
        ImmutableBag<String> immutableBag = this.newBag();
        ImmutableSet<Pair<String, Integer>> pairs = immutableBag.zipWithIndex();

        Assertions.assertEquals(UnifiedSet.<String>newSet(), pairs.collect((Function<Pair<String, ?>, String>) Pair::getOne));
        Assertions.assertEquals(UnifiedSet.<Integer>newSet(), pairs.collect((Function<Pair<?, Integer>, Integer>) Pair::getTwo));

        Assertions.assertEquals(immutableBag.zipWithIndex(), immutableBag.zipWithIndex(UnifiedSet.<Pair<String, Integer>>newSet()));
    }

    @Override
    @Test
    public void chunk()
    {
        Assertions.assertEquals(this.newBag(), this.newBag().chunk(2));
    }

    @Override
    @Test
    public void chunk_zero_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            this.newBag().chunk(0);
        });
    }

    @Override
    @Test
    public void chunk_large_size()
    {
        Assertions.assertEquals(this.newBag(), this.newBag().chunk(10));
        Verify.assertInstanceOf(ImmutableBag.class, this.newBag().chunk(10));
    }

    @Override
    @Test
    public void toSortedMap()
    {
        MutableSortedMap<String, String> map = this.newBag().toSortedMap(Functions.getStringPassThru(), Functions.getStringPassThru());
        Verify.assertEmpty(map);
        Verify.assertInstanceOf(TreeSortedMap.class, map);
    }

    @Override
    @Test
    public void toSortedMap_with_comparator()
    {
        MutableSortedMap<String, String> map = this.newBag().toSortedMap(Comparators.<String>reverseNaturalOrder(),
                Functions.getStringPassThru(), Functions.getStringPassThru());
        Verify.assertEmpty(map);
        Verify.assertInstanceOf(TreeSortedMap.class, map);
        Assertions.assertEquals(Comparators.<String>reverseNaturalOrder(), map.comparator());
    }

    @Override
    @Test
    public void serialization()
    {
        ImmutableBag<String> bag = this.newBag();
        Verify.assertPostSerializedIdentity(bag);
    }

    @Override
    @Test
    public void collectBoolean()
    {
        ImmutableBooleanBag result = this.newBag().collectBoolean("4"::equals);
        Assertions.assertEquals(0, result.sizeDistinct());
        Assertions.assertEquals(0, result.occurrencesOf(true));
        Assertions.assertEquals(0, result.occurrencesOf(false));
    }

    @Override
    @Test
    public void collectBooleanWithTarget()
    {
        BooleanHashBag target = new BooleanHashBag();
        BooleanHashBag result = this.newBag().collectBoolean("4"::equals, target);
        Assertions.assertSame(target, result, "Target sent as parameter not returned");
        Assertions.assertEquals(0, result.sizeDistinct());
        Assertions.assertEquals(0, result.occurrencesOf(true));
        Assertions.assertEquals(0, result.occurrencesOf(false));
    }

    @Override
    @Test
    public void collect_target()
    {
        MutableList<Integer> targetCollection = FastList.newList();
        MutableList<Integer> actual = this.newBag().collect(object -> {
            throw new AssertionError();
        }, targetCollection);
        Assertions.assertEquals(targetCollection, actual);
        Assertions.assertSame(targetCollection, actual);
    }

    @Override
    @Test
    public void collectWith_target()
    {
        MutableList<Integer> targetCollection = FastList.newList();
        MutableList<Integer> actual = this.newBag().collectWith((argument1, argument2) -> {
            throw new AssertionError();
        }, 1, targetCollection);
        Assertions.assertEquals(targetCollection, actual);
        Assertions.assertSame(targetCollection, actual);
    }

    @Override
    @Test
    public void groupByUniqueKey()
    {
        Assertions.assertEquals(UnifiedMap.newMap().toImmutable(), this.newBag().groupByUniqueKey(id -> id));
    }

    @Override
    @Test
    public void groupByUniqueKey_throws()
    {
        super.groupByUniqueKey_throws();
        Assertions.assertEquals(UnifiedMap.newMap().toImmutable(), this.newBag().groupByUniqueKey(id -> id));
    }

    @Override
    @Test
    public void groupByUniqueKey_target()
    {
        Assertions.assertEquals(UnifiedMap.newMap(), this.newBag().groupByUniqueKey(id -> id, UnifiedMap.<String, String>newMap()));
    }

    @Override
    @Test
    public void groupByUniqueKey_target_throws()
    {
        super.groupByUniqueKey_target_throws();
        Assertions.assertEquals(UnifiedMap.newMap(), this.newBag().groupByUniqueKey(id -> id, UnifiedMap.<String, String>newMap()));
    }

    @Override
    @Test
    public void toSortedBag()
    {
        ImmutableBag<String> immutableBag = this.newBag();
        MutableSortedBag<String> sortedBag = immutableBag.toSortedBag();

        Verify.assertSortedBagsEqual(TreeBag.newBag(), sortedBag);

        MutableSortedBag<String> reverse = immutableBag.toSortedBag(Comparator.<String>reverseOrder());
        Verify.assertSortedBagsEqual(TreeBag.newBag(Comparator.<String>reverseOrder()), reverse);

        ImmutableBag<String> immutableBag1 = this.newBag();
        MutableSortedBag<String> sortedBag1 = immutableBag1.toSortedBag(Comparator.reverseOrder());
        Verify.assertSortedBagsEqual(TreeBag.newBag(), sortedBag1.toSortedBag());

        ImmutableBag<String> immutableBag2 = this.newBag();
        MutableSortedBag<String> sortedBag2 = immutableBag2.toSortedBag(Comparator.reverseOrder());
        Verify.assertSortedBagsEqual(TreeBag.newBag(Comparator.<String>reverseOrder()), sortedBag2);
    }

    @Test
    public void toSortedBag_empty()
    {
        ImmutableBag<String> immutableBag = Bags.immutable.of();

        MutableSortedBag<String> sortedBag = immutableBag.toSortedBag(Comparators.reverseNaturalOrder());
        sortedBag.addOccurrences("apple", 3);
        sortedBag.addOccurrences("orange", 2);

        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.reverseNaturalOrder(), "orange", "orange", "apple", "apple", "apple"), sortedBag);
    }

    @Test
    public void toSortedBagBy_empty()
    {
        ImmutableBag<Integer> immutableBag = Bags.immutable.of();

        Function<Integer, Integer> function = object -> object * -1;
        MutableSortedBag<Integer> sortedBag = immutableBag.toSortedBagBy(function);
        sortedBag.addOccurrences(1, 3);
        sortedBag.addOccurrences(10, 2);

        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Comparators.byFunction(function), 10, 10, 1, 1, 1), sortedBag);
    }

    @Override
    @Test
    public void toSortedBagBy()
    {
        ImmutableBag<String> immutableBag = this.newBag();
        MutableSortedBag<String> sortedBag = immutableBag.toSortedBagBy(String::valueOf);
        TreeBag<Object> expectedBag = TreeBag.newBag(Comparators.byFunction(String::valueOf));

        Verify.assertSortedBagsEqual(expectedBag, sortedBag);
    }
}
