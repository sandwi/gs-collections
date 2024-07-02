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

package com.gs.collections.impl.map.immutable;

import java.util.Collections;
import java.util.List;

import com.gs.collections.api.LazyIterable;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.bag.MutableBag;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.ImmutableMap;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.multimap.Multimap;
import com.gs.collections.api.multimap.MutableMultimap;
import com.gs.collections.api.partition.PartitionImmutableCollection;
import com.gs.collections.api.set.ImmutableSet;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.IntegerPredicates;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.function.AddFunction;
import com.gs.collections.impl.block.function.NegativeIntervalFunction;
import com.gs.collections.impl.factory.Bags;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.math.IntegerSum;
import com.gs.collections.impl.math.Sum;
import com.gs.collections.impl.math.SumProcedure;
import com.gs.collections.impl.multimap.bag.HashBagMultimap;
import com.gs.collections.impl.multimap.list.FastListMultimap;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.gs.collections.impl.factory.Iterables.*;

public abstract class ImmutableMemoryEfficientMapTestCase extends ImmutableMapTestCase
{
    protected abstract <K, V> ImmutableMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4);

    @Test
    public abstract void select();

    @Test
    public abstract void reject();

    @Test
    public abstract void detect();

    @Test
    public void collectValues()
    {
        ImmutableMap<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three", "4", "Four");
        ImmutableMap<String, String> result = map.collectValues((argument1, argument2) -> new StringBuilder(argument2).reverse().toString());

        switch (map.size())
        {
            case 0:
                Verify.assertEmpty(result);
                break;
            case 1:
                Verify.assertContainsKeyValue("enO", result, "1");
                break;
            case 2:
                Verify.assertContainsAllKeyValues(result, "1", "enO", "2", "owT");
                break;
            case 3:
                Verify.assertContainsAllKeyValues(result, "1", "enO", "2", "owT", "3", "eerhT");
                break;
            case 4:
                Verify.assertContainsAllKeyValues(result, "1", "enO", "2", "owT", "3", "eerhT", "4", "ruoF");
                break;
            default:
                Assertions.fail();
        }
    }

    @Test
    public void collect()
    {
        ImmutableMap<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three", "4", "Four");
        ImmutableMap<Integer, String> result = map.collect((Function2<String, String, Pair<Integer, String>>) (argument1, argument2) -> Tuples.pair(Integer.valueOf(argument1), argument1 + ':' + new StringBuilder(argument2).reverse()));

        switch (map.size())
        {
            case 0:
                Verify.assertEmpty(result);
                break;
            case 1:
                Verify.assertContainsKeyValue(1, "1:enO", result);
                break;
            case 2:
                Verify.assertContainsAllKeyValues(result, 1, "1:enO", 2, "2:owT");
                break;
            case 3:
                Verify.assertContainsAllKeyValues(result, 1, "1:enO", 2, "2:owT", 3, "3:eerhT");
                break;
            case 4:
                Verify.assertContainsAllKeyValues(result, 1, "1:enO", 2, "2:owT", 3, "3:eerhT", 4, "4:ruoF");
                break;
            default:
                Assertions.fail();
        }
    }

    @Test
    public void allSatisfy()
    {
        ImmutableMap<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three", "4", "Four");

        Assertions.assertTrue(map.allSatisfy(String.class::isInstance));
        Assertions.assertFalse(map.allSatisfy("Monkey"::equals));
    }

    @Test
    public void anySatisfy()
    {
        ImmutableMap<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three", "4", "Four");

        Assertions.assertTrue(map.anySatisfy(String.class::isInstance));
        Assertions.assertFalse(map.anySatisfy("Monkey"::equals));
    }

    @Test
    public void noneSatisfy()
    {
        ImmutableMap<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three", "4", "Four");

        Assertions.assertTrue(map.noneSatisfy(Integer.class::isInstance));
        Assertions.assertTrue(map.noneSatisfy("Monkey"::equals));
    }

    @Test
    public void appendString()
    {
        ImmutableMap<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three", "4", "Four");

        StringBuilder builder1 = new StringBuilder();
        map.appendString(builder1);
        String defaultString = builder1.toString();

        StringBuilder builder2 = new StringBuilder();
        map.appendString(builder2, "|");
        String delimitedString = builder2.toString();

        StringBuilder builder3 = new StringBuilder();
        map.appendString(builder3, "{", "|", "}");
        String wrappedString = builder3.toString();

        switch (map.size())
        {
            case 1:
                Assertions.assertEquals("One", defaultString);
                Assertions.assertEquals("One", delimitedString);
                Assertions.assertEquals("{One}", wrappedString);
                break;
            case 2:
                Assertions.assertEquals(8, defaultString.length());
                Assertions.assertEquals(7, delimitedString.length());
                Verify.assertContains(delimitedString, "|");
                Assertions.assertEquals(9, wrappedString.length());
                Verify.assertContains(wrappedString, "|");
                Assertions.assertTrue(wrappedString.startsWith("{"));
                Assertions.assertTrue(wrappedString.endsWith("}"));
                break;
            case 3:
                Assertions.assertEquals(15, defaultString.length());
                Assertions.assertEquals(13, delimitedString.length());
                Verify.assertContains(delimitedString, "|");
                Assertions.assertEquals(15, wrappedString.length());
                Verify.assertContains(wrappedString, "|");
                Assertions.assertTrue(wrappedString.startsWith("{"));
                Assertions.assertTrue(wrappedString.endsWith("}"));
                break;
            case 4:
                Assertions.assertEquals(21, defaultString.length());
                Assertions.assertEquals(18, delimitedString.length());
                Verify.assertContains(delimitedString, "|");
                Assertions.assertEquals(20, wrappedString.length());
                Verify.assertContains(wrappedString, "|");
                Assertions.assertTrue(wrappedString.startsWith("{"));
                Assertions.assertTrue(wrappedString.endsWith("}"));
                break;
            default:
                Assertions.assertEquals("", defaultString);
                Assertions.assertEquals("", delimitedString);
                Assertions.assertEquals("{}", wrappedString);
                break;
        }
    }

    @Test
    public void toBag()
    {
        ImmutableMap<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three", "4", "Four");

        MutableBag<String> bag = map.toBag();
        switch (map.size())
        {
            case 1:
                Verify.assertContains(bag, "One");
                break;
            case 2:
                Verify.assertContainsAll(bag, "One", "Two");
                break;
            case 3:
                Verify.assertContainsAll(bag, "One", "Two", "Three");
                break;
            case 4:
                Verify.assertContainsAll(bag, "One", "Two", "Three", "Four");
                break;
            default:
                Verify.assertEmpty(bag);
                break;
        }
    }

    @Test
    public void asLazy()
    {
        ImmutableMap<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three", "4", "Four");

        LazyIterable<String> lazy = map.asLazy();
        switch (map.size())
        {
            case 1:
                Verify.assertContains(lazy.toList(), "One");
                break;
            case 2:
                Verify.assertContainsAll(lazy.toList(), "One", "Two");
                break;
            case 3:
                Verify.assertContainsAll(lazy.toList(), "One", "Two", "Three");
                break;
            case 4:
                Verify.assertContainsAll(lazy.toList(), "One", "Two", "Three", "Four");
                break;
            default:
                Verify.assertEmpty(lazy.toList());
                break;
        }
    }

    @Test
    public void toList()
    {
        ImmutableMap<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three", "4", "Four");

        MutableList<String> list = map.toList();
        switch (map.size())
        {
            case 1:
                Verify.assertContains(list, "One");
                break;
            case 2:
                Verify.assertContainsAll(list, "One", "Two");
                break;
            case 3:
                Verify.assertContainsAll(list, "One", "Two", "Three");
                break;
            case 4:
                Verify.assertContainsAll(list, "One", "Two", "Three", "Four");
                break;
            default:
                Verify.assertEmpty(list);
                break;
        }
    }

    @Test
    public void toMapWithFunction()
    {
        ImmutableMap<String, String> map = this.newMapWithKeysValues("1", "One", "3", "Three", "4", "Four", "11", "Eleven");

        MutableMap<Integer, String> actual = map.toMap(String::length, String::valueOf);

        switch (map.size())
        {
            case 1:
                Assertions.assertEquals(UnifiedMap.newWithKeysValues(3, "One"), actual);
                break;
            case 2:
                Assertions.assertEquals(UnifiedMap.newWithKeysValues(3, "One", 5, "Three"), actual);
                break;
            case 3:
                Assertions.assertEquals(UnifiedMap.newWithKeysValues(3, "One", 5, "Three", 4, "Four"), actual);
                break;
            case 4:
                Assertions.assertEquals(UnifiedMap.newWithKeysValues(3, "One", 5, "Three", 4, "Four", 6, "Eleven"), actual);
                break;
            default:
                Verify.assertEmpty(actual);
                break;
        }
    }

    @Test
    public void toSet()
    {
        ImmutableMap<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three", "4", "Four");

        MutableSet<String> set = map.toSet();
        switch (map.size())
        {
            case 1:
                Verify.assertContains(set, "One");
                break;
            case 2:
                Verify.assertContainsAll(set, "One", "Two");
                break;
            case 3:
                Verify.assertContainsAll(set, "One", "Two", "Three");
                break;
            case 4:
                Verify.assertContainsAll(set, "One", "Two", "Three", "Four");
                break;
            default:
                Verify.assertEmpty(set);
                break;
        }
    }

    @Test
    public void toSortedList()
    {
        ImmutableMap<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        MutableList<Integer> sorted = map.toSortedList();
        switch (map.size())
        {
            case 1:
                Assertions.assertEquals(iList(1), sorted);
                break;
            case 2:
                Assertions.assertEquals(iList(1, 2), sorted);
                break;
            case 3:
                Assertions.assertEquals(iList(1, 2, 3), sorted);
                break;
            case 4:
                Assertions.assertEquals(iList(1, 2, 3, 4), sorted);
                break;
            default:
                Verify.assertEmpty(sorted);
                break;
        }

        MutableList<Integer> reverse = map.toSortedList(Collections.<Integer>reverseOrder());
        switch (map.size())
        {
            case 1:
                Assertions.assertEquals(iList(1), reverse);
                break;
            case 2:
                Assertions.assertEquals(iList(2, 1), reverse);
                break;
            case 3:
                Assertions.assertEquals(iList(3, 2, 1), reverse);
                break;
            case 4:
                Assertions.assertEquals(iList(4, 3, 2, 1), reverse);
                break;
            default:
                Verify.assertEmpty(reverse);
                break;
        }
    }

    @Test
    public void toSortedListBy()
    {
        ImmutableMap<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        MutableList<Integer> list = map.toSortedListBy(String::valueOf);
        switch (map.size())
        {
            case 1:
                Assertions.assertEquals(iList(1), list);
                break;
            case 2:
                Assertions.assertEquals(iList(1, 2), list);
                break;
            case 3:
                Assertions.assertEquals(iList(1, 2, 3), list);
                break;
            case 4:
                Assertions.assertEquals(iList(1, 2, 3, 4), list);
                break;
            default:
                Verify.assertEmpty(list);
                break;
        }
    }

    @Test
    public void chunk()
    {
        ImmutableMap<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three", "4", "Four");

        RichIterable<RichIterable<String>> chunks = map.chunk(2).toList();

        RichIterable<Integer> sizes = chunks.collect(RichIterable::size);

        switch (map.size())
        {
            case 1:
                Assertions.assertEquals(iList(1), sizes);
                break;
            case 2:
                Assertions.assertEquals(iList(2), sizes);
                break;
            case 3:
                Assertions.assertEquals(iList(2, 1), sizes);
                break;
            case 4:
                Assertions.assertEquals(iList(2, 2), sizes);
                break;
            default:
                Assertions.assertEquals(0, chunks.size());
                break;
        }
    }

    @Test
    public void collect_value()
    {
        ImmutableMap<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);
        MutableSet<String> collect = map.collect(Functions.getToString()).toSet();
        UnifiedSet<String> collectToTarget = map.collect(String::valueOf, UnifiedSet.<String>newSet());

        switch (map.size())
        {
            case 1:
                Verify.assertContainsAll(collect, "1");
                Verify.assertContainsAll(collectToTarget, "1");
                break;
            case 2:
                Verify.assertContainsAll(collect, "1", "2");
                Verify.assertContainsAll(collectToTarget, "1", "2");
                break;
            case 3:
                Verify.assertContainsAll(collect, "1", "2", "3");
                Verify.assertContainsAll(collectToTarget, "1", "2", "3");
                break;
            case 4:
                Verify.assertContainsAll(collect, "1", "2", "3", "4");
                Verify.assertContainsAll(collectToTarget, "1", "2", "3", "4");
                break;
            default:
                Verify.assertEmpty(collect);
                break;
        }
    }

    @Test
    public void collectIf()
    {
        ImmutableMap<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        MutableSet<String> collect = map.collectIf(Integer.class::isInstance, String::valueOf).toSet();
        UnifiedSet<String> collectToTarget = map.collectIf(Integer.class::isInstance, String::valueOf, UnifiedSet.<String>newSet());

        switch (map.size())
        {
            case 1:
                Verify.assertContainsAll(collect, "1");
                Verify.assertContainsAll(collectToTarget, "1");
                break;
            case 2:
                Verify.assertContainsAll(collect, "1", "2");
                Verify.assertContainsAll(collectToTarget, "1", "2");
                break;
            case 3:
                Verify.assertContainsAll(collect, "1", "2", "3");
                Verify.assertContainsAll(collectToTarget, "1", "2", "3");
                break;
            case 4:
                Verify.assertContainsAll(collect, "1", "2", "3", "4");
                Verify.assertContainsAll(collectToTarget, "1", "2", "3", "4");
                break;
            default:
                Verify.assertEmpty(collect);
                break;
        }
    }

    @Test
    public void collectWith()
    {
        ImmutableMap<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        MutableBag<Integer> collectWith = map.collectWith(AddFunction.INTEGER, 1, HashBag.<Integer>newBag());

        switch (map.size())
        {
            case 1:
                Assertions.assertEquals(Bags.mutable.of(2), collectWith);
                break;
            case 2:
                Assertions.assertEquals(Bags.mutable.of(2, 3), collectWith);
                break;
            case 3:
                Assertions.assertEquals(Bags.mutable.of(2, 3, 4), collectWith);
                break;
            case 4:
                Assertions.assertEquals(Bags.mutable.of(2, 3, 4, 5), collectWith);
                break;
            default:
                Verify.assertEmpty(collectWith);
                break;
        }
    }

    @Test
    public void contains()
    {
        ImmutableMap<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three", "4", "Four");

        switch (map.size())
        {
            case 1:
                Assertions.assertTrue(map.contains("One"));
                Assertions.assertFalse(map.contains("Two"));
                break;
            case 2:
                Assertions.assertTrue(map.contains("Two"));
                Assertions.assertFalse(map.contains("Three"));
                break;
            case 3:
                Assertions.assertTrue(map.contains("Three"));
                Assertions.assertFalse(map.contains("Four"));
                break;
            case 4:
                Assertions.assertTrue(map.contains("Four"));
                Assertions.assertFalse(map.contains("Five"));
                break;
            default:
                Verify.assertEmpty(map);
                break;
        }
    }

    @Test
    public void getFirst()
    {
        ImmutableMap<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three", "4", "Four");

        if (map.isEmpty())
        {
            String value = map.getFirst();
            Assertions.assertNull(value);
        }
        else
        {
            String value = map.getFirst();
            Assertions.assertNotNull(value);
            Verify.assertContains(UnifiedSet.newSetWith("One", "Two", "Three", "Four"), value);
        }
    }

    @Test
    public void getLast()
    {
        ImmutableMap<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three", "4", "Four");

        if (map.isEmpty())
        {
            String value = map.getLast();
            Assertions.assertNull(value);
        }
        else
        {
            String value = map.getLast();
            Assertions.assertNotNull(value);
            Verify.assertContains(UnifiedSet.newSetWith("One", "Two", "Three", "Four"), value);
        }
    }

    @Test
    public void containsAllIterable()
    {
        ImmutableMap<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three", "4", "Four");

        switch (map.size())
        {
            case 1:
                Assertions.assertTrue(map.containsAllIterable(iList("One")));
                break;
            case 2:
                Assertions.assertTrue(map.containsAllIterable(iList("One", "Two")));
                break;
            case 3:
                Assertions.assertTrue(map.containsAllIterable(iList("One", "Two", "Three")));
                break;
            case 4:
                Assertions.assertTrue(map.containsAllIterable(iList("One", "Two", "Three", "Four")));
                break;
            default:
                Verify.assertEmpty(map);
                break;
        }
    }

    @Test
    public void containsAllArguments()
    {
        ImmutableMap<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three", "4", "Four");

        switch (map.size())
        {
            case 1:
                Assertions.assertTrue(map.containsAllArguments("One"));
                break;
            case 2:
                Assertions.assertTrue(map.containsAllArguments("One", "Two"));
                break;
            case 3:
                Assertions.assertTrue(map.containsAllArguments("One", "Two", "Three"));
                break;
            case 4:
                Assertions.assertTrue(map.containsAllArguments("One", "Two", "Three", "Four"));
                break;
            default:
                Verify.assertEmpty(map);
                break;
        }
    }

    @Test
    public void count()
    {
        ImmutableMap<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three", "4", "Four");

        int actual = map.count(Predicates.or("One"::equals, "Three"::equals));

        switch (map.size())
        {
            case 1:
                Assertions.assertEquals(1, actual);
                break;
            case 2:
                Assertions.assertEquals(1, actual);
                break;
            case 3:
                Assertions.assertEquals(2, actual);
                break;
            case 4:
                Assertions.assertEquals(2, actual);
                break;
            default:
                Assertions.assertEquals(0, actual);
                break;
        }
    }

    @Test
    public void detect_value()
    {
        ImmutableMap<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three", "4", "Four");

        if (map.isEmpty())
        {
            String resultNotFound = map.detect(ignored -> true);
            Assertions.assertNull(resultNotFound);
        }
        else
        {
            String resultFound = map.detect("One"::equals);
            Assertions.assertEquals("One", resultFound);

            String resultNotFound = map.detect("Five"::equals);
            Assertions.assertNull(resultNotFound);
        }
    }

    @Test
    public void detectIfNone_value()
    {
        ImmutableMap<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three", "4", "Four");

        if (map.isEmpty())
        {
            String resultNotFound = map.detectIfNone(ignored -> true, () -> "Zero");
            Assertions.assertEquals("Zero", resultNotFound);
        }
        else
        {
            String resultNotFound = map.detectIfNone("Five"::equals, () -> "Zero");
            Assertions.assertEquals("Zero", resultNotFound);

            String resultFound = map.detectIfNone("One"::equals, () -> "Zero");
            Assertions.assertEquals("One", resultFound);
        }
    }

    @Test
    public void flatCollect()
    {
        ImmutableMap<Integer, Integer> map = this.newMapWithKeysValues(1, 1, 2, 2, 3, 3, 4, 4);

        if (map.isEmpty())
        {
            Function<Integer, Iterable<Object>> fail = each -> {
                throw new AssertionError();
            };
            Assertions.assertEquals(Bags.immutable.empty(), map.flatCollect(fail));
            Assertions.assertEquals(Bags.immutable.empty(), map.flatCollect(fail, HashBag.newBag()));
        }
        else
        {
            MutableBag<Integer> expected = Interval.oneTo(map.size()).flatCollect(each -> Interval.oneTo(each)).toBag();
            Assertions.assertEquals(expected, map.flatCollect(each -> Interval.oneTo(each)));
            Assertions.assertEquals(expected, map.flatCollect(each -> Interval.oneTo(each), HashBag.newBag()));
        }
    }

    @Test
    public void groupBy()
    {
        ImmutableMap<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        Function<Integer, Boolean> isOddFunction = object -> IntegerPredicates.isOdd().accept(object);

        Multimap<Boolean, Integer> expected;

        switch (map.size())
        {
            case 1:
                expected = FastListMultimap.newMultimap(Tuples.pair(Boolean.TRUE, 1));
                break;
            case 2:
                expected = FastListMultimap.newMultimap(Tuples.pair(Boolean.TRUE, 1), Tuples.pair(Boolean.FALSE, 2));
                break;
            case 3:
                expected = FastListMultimap.newMultimap(Tuples.pair(Boolean.TRUE, 1), Tuples.pair(Boolean.TRUE, 3), Tuples.pair(Boolean.FALSE, 2));
                break;
            case 4:
                expected = FastListMultimap.newMultimap(Tuples.pair(Boolean.TRUE, 1), Tuples.pair(Boolean.TRUE, 3), Tuples.pair(Boolean.FALSE, 2), Tuples.pair(Boolean.FALSE, 4));
                break;
            default:
                expected = FastListMultimap.newMultimap();
                break;
        }

        Multimap<Boolean, Integer> actual = map.groupBy(isOddFunction);
        Assertions.assertEquals(HashBagMultimap.newMultimap(expected), HashBagMultimap.newMultimap(actual));

        Multimap<Boolean, Integer> actualFromTarget = map.groupBy(isOddFunction, FastListMultimap.<Boolean, Integer>newMultimap());
        Assertions.assertEquals(HashBagMultimap.newMultimap(expected), HashBagMultimap.newMultimap(actualFromTarget));
    }

    @Test
    public void groupByEach()
    {
        ImmutableMap<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        NegativeIntervalFunction function = new NegativeIntervalFunction();
        MutableMultimap<Integer, Integer> expected = FastListMultimap.newMultimap();
        for (int i = 1; i < map.size(); i++)
        {
            expected.putAll(-i, Interval.fromTo(i, map.size()));
        }

        Multimap<Integer, Integer> actual = map.groupByEach(function);
        expected.forEachKey(each -> {
            Assertions.assertTrue(actual.containsKey(each));
            MutableList<Integer> values = actual.get(each).toList();
            Verify.assertNotEmpty(values);
            Assertions.assertTrue(expected.get(each).containsAllIterable(values));
        });

        Multimap<Integer, Integer> actualFromTarget = map.groupByEach(function, FastListMultimap.<Integer, Integer>newMultimap());
        expected.forEachKey(each -> {
            Assertions.assertTrue(actualFromTarget.containsKey(each));
            MutableList<Integer> values = actualFromTarget.get(each).toList();
            Verify.assertNotEmpty(values);
            Assertions.assertTrue(expected.get(each).containsAllIterable(values));
        });
    }

    @Test
    public void injectInto()
    {
        ImmutableMap<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        Integer expectedInteger;
        IntegerSum expectedSum;

        switch (map.size())
        {
            case 1:
                expectedSum = new IntegerSum(1);
                expectedInteger = Integer.valueOf(1);
                break;
            case 2:
                expectedSum = new IntegerSum(3);
                expectedInteger = Integer.valueOf(3);
                break;
            case 3:
                expectedSum = new IntegerSum(6);
                expectedInteger = Integer.valueOf(6);
                break;
            case 4:
                expectedSum = new IntegerSum(10);
                expectedInteger = Integer.valueOf(10);
                break;
            default:
                expectedSum = new IntegerSum(0);
                expectedInteger = Integer.valueOf(0);
                break;
        }

        Integer actual = map.injectInto(0, AddFunction.INTEGER);
        Assertions.assertEquals(expectedInteger, actual);

        Sum sum = map.injectInto(new IntegerSum(0), SumProcedure.number());
        Assertions.assertEquals(expectedSum, sum);
    }

    @Test
    public void makeString()
    {
        ImmutableMap<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three", "4", "Four");

        String defaultString = map.makeString();
        String delimitedString = map.makeString("|");
        String wrappedString = map.makeString("{", "|", "}");

        switch (map.size())
        {
            case 1:
                Assertions.assertEquals("One", defaultString);
                Assertions.assertEquals("One", delimitedString);
                Assertions.assertEquals("{One}", wrappedString);
                break;
            case 2:
                Assertions.assertEquals(8, defaultString.length());
                Assertions.assertEquals(7, delimitedString.length());
                Verify.assertContains(delimitedString, "|");
                Assertions.assertEquals(9, wrappedString.length());
                Verify.assertContains(wrappedString, "|");
                Assertions.assertTrue(wrappedString.startsWith("{"));
                Assertions.assertTrue(wrappedString.endsWith("}"));
                break;
            case 3:
                Assertions.assertEquals(15, defaultString.length());
                Assertions.assertEquals(13, delimitedString.length());
                Verify.assertContains(delimitedString, "|");
                Assertions.assertEquals(15, wrappedString.length());
                Verify.assertContains(wrappedString, "|");
                Assertions.assertTrue(wrappedString.startsWith("{"));
                Assertions.assertTrue(wrappedString.endsWith("}"));
                break;
            case 4:
                Assertions.assertEquals(21, defaultString.length());
                Assertions.assertEquals(18, delimitedString.length());
                Verify.assertContains(delimitedString, "|");
                Assertions.assertEquals(20, wrappedString.length());
                Verify.assertContains(wrappedString, "|");
                Assertions.assertTrue(wrappedString.startsWith("{"));
                Assertions.assertTrue(wrappedString.endsWith("}"));
                break;
            default:
                Assertions.assertEquals("", defaultString);
                Assertions.assertEquals("", delimitedString);
                Assertions.assertEquals("{}", wrappedString);
                break;
        }
    }

    @Test
    public void min()
    {
        ImmutableMap<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        Assertions.assertEquals(Integer.valueOf(1), map.min());
        Assertions.assertEquals(Integer.valueOf(1), map.min(Integer::compareTo));
    }

    @Test
    public void max()
    {
        ImmutableMap<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        Integer max;

        switch (map.size())
        {
            case 1:
                max = Integer.valueOf(1);
                break;
            case 2:
                max = Integer.valueOf(2);
                break;
            case 3:
                max = Integer.valueOf(3);
                break;
            case 4:
                max = Integer.valueOf(4);
                break;
            default:
                max = Integer.valueOf(0);
                break;
        }

        Assertions.assertEquals(max, map.max());
        Assertions.assertEquals(max, map.max(Integer::compareTo));
    }

    @Test
    public void minBy()
    {
        ImmutableMap<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        Assertions.assertEquals(Integer.valueOf(1), map.minBy(String::valueOf));
    }

    @Test
    public void maxBy()
    {
        ImmutableMap<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);
        Assertions.assertEquals(Integer.valueOf(map.size()), map.maxBy(String::valueOf));
        Verify.assertContains(Integer.valueOf(map.size()), iList(1, 2, 3, 4));
    }

    @Test
    public void reject_value()
    {
        ImmutableMap<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        MutableSet<Integer> rejected = map.reject(IntegerPredicates.isEven()).toSet();
        UnifiedSet<Integer> rejectedIntoTarget = map.reject(IntegerPredicates.isEven(), UnifiedSet.<Integer>newSet());

        ImmutableSet<Integer> expected = this.expectReject(map.size());
        Assertions.assertEquals(expected, rejected);
        Assertions.assertEquals(expected, rejectedIntoTarget);
    }

    private ImmutableSet<Integer> expectReject(int size)
    {
        switch (size)
        {
            case 0:
                return iSet();
            case 1:
            case 2:
                return iSet(1);
            case 3:
            case 4:
                return iSet(1, 3);
            default:
                throw new AssertionError();
        }
    }

    @Test
    public void rejectWith_value()
    {
        ImmutableMap<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        switch (map.size())
        {
            case 1:
                Verify.assertEmpty(map.rejectWith(Predicates2.<Integer>lessThan(), 2, UnifiedSet.<Integer>newSet()));
                break;
            case 2:
                Verify.assertContainsAll(map.rejectWith(Predicates2.<Integer>lessThan(), 2, UnifiedSet.<Integer>newSet()), 2);
                break;
            case 3:
                Verify.assertContainsAll(map.rejectWith(Predicates2.<Integer>lessThan(), 2, UnifiedSet.<Integer>newSet()), 2, 3);
                break;
            case 4:
                Verify.assertContainsAll(map.rejectWith(Predicates2.<Integer>lessThan(), 2, UnifiedSet.<Integer>newSet()), 2, 3, 4);
                break;
            default:
                Verify.assertEmpty(map);
                break;
        }
    }

    @Test
    public void select_value()
    {
        ImmutableMap<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);
        ImmutableSet<Integer> expected = this.expectSelect(map.size());

        Assertions.assertEquals(expected, map.select(IntegerPredicates.isEven()).toSet());
        Assertions.assertEquals(expected, map.select(IntegerPredicates.isEven(), UnifiedSet.<Integer>newSet()));
    }

    private ImmutableSet<Integer> expectSelect(int size)
    {
        switch (size)
        {
            case 0:
            case 1:
                return iSet();
            case 2:
            case 3:
                return iSet(2);
            case 4:
                return iSet(2, 4);
            default:
                throw new AssertionError();
        }
    }

    @Test
    public void selectWith_value()
    {
        ImmutableMap<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        switch (map.size())
        {
            case 1:
                Verify.assertContainsAll(map.selectWith(Predicates2.<Integer>lessThan(), 3, UnifiedSet.<Integer>newSet()), 1);
                break;
            case 2:
            case 3:
            case 4:
                Verify.assertContainsAll(map.selectWith(Predicates2.<Integer>lessThan(), 3, UnifiedSet.<Integer>newSet()), 1, 2);
                break;
            default:
                Verify.assertEmpty(map);
                break;
        }
    }

    @Test
    public void partition_value()
    {
        ImmutableMap<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);
        PartitionImmutableCollection<Integer> partition = map.partition(IntegerPredicates.isEven());

        Assertions.assertEquals(this.expectSelect(map.size()), partition.getSelected().toSet());
        Assertions.assertEquals(this.expectReject(map.size()), partition.getRejected().toSet());
    }

    @Test
    public void partitionWith_value()
    {
        ImmutableMap<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);
        PartitionImmutableCollection<Integer> partition = map.partitionWith(Predicates2.in(), map.select(IntegerPredicates.isEven()));

        Assertions.assertEquals(this.expectSelect(map.size()), partition.getSelected().toSet());
        Assertions.assertEquals(this.expectReject(map.size()), partition.getRejected().toSet());
    }

    @Test
    public void toArray()
    {
        ImmutableMap<String, Integer> map = this.newMapWithKeysValues("1", 1, "2", 2, "3", 3, "4", 4);

        Object[] array = map.toArray();
        Verify.assertSize(map.size(), array);

        Integer[] array2 = map.toArray(new Integer[0]);
        Verify.assertSize(map.size(), array2);

        Integer[] array3 = map.toArray(new Integer[map.size()]);
        Verify.assertSize(map.size(), array3);

        if (map.size() > 1)
        {
            Integer[] array4 = map.toArray(new Integer[map.size() - 1]);
            Verify.assertSize(map.size(), array4);
        }

        Integer[] array5 = map.toArray(new Integer[6]);
        Verify.assertSize(6, array5);
    }

    @Test
    public void zip()
    {
        ImmutableMap<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three", "4", "Four");

        List<Object> nulls = Collections.nCopies(map.size(), null);
        List<Object> nullsPlusOne = Collections.nCopies(map.size() + 1, null);

        RichIterable<Pair<String, Object>> pairs = map.zip(nulls);
        Assertions.assertEquals(
                map.toSet(),
                pairs.collect((Function<Pair<String, ?>, String>) Pair::getOne).toSet());
        Assertions.assertEquals(
                nulls,
                pairs.collect((Function<Pair<?, Object>, Object>) Pair::getTwo, Lists.mutable.of()));

        RichIterable<Pair<String, Object>> pairsPlusOne = map.zip(nullsPlusOne);
        Assertions.assertEquals(
                map.toSet(),
                pairsPlusOne.collect((Function<Pair<String, ?>, String>) Pair::getOne).toSet());
        Assertions.assertEquals(nulls, pairsPlusOne.collect((Function<Pair<?, Object>, Object>) Pair::getTwo, Lists.mutable.of()));

        if (map.notEmpty())
        {
            List<Object> nullsMinusOne = Collections.nCopies(map.size() - 1, null);
            RichIterable<Pair<String, Object>> pairsMinusOne = map.zip(nullsMinusOne);
            Assertions.assertEquals(map.size() - 1, pairsMinusOne.size());
        }

        Assertions.assertEquals(
                map.zip(nulls).toSet(),
                map.zip(nulls, UnifiedSet.<Pair<String, Object>>newSet()));
    }

    @Test
    public void zipWithIndex()
    {
        ImmutableMap<String, String> map = this.newMapWithKeysValues("1", "One", "2", "Two", "3", "Three", "4", "Four");

        RichIterable<Pair<String, Integer>> pairs = map.zipWithIndex();

        Assertions.assertEquals(
                map.toSet(),
                pairs.collect((Function<Pair<String, ?>, String>) Pair::getOne).toSet());
        if (map.notEmpty())
        {
            Assertions.assertEquals(
                    Interval.zeroTo(map.size() - 1).toSet(),
                    pairs.collect((Function<Pair<?, Integer>, Integer>) Pair::getTwo, UnifiedSet.<Integer>newSet()));
        }

        Assertions.assertEquals(
                map.zipWithIndex().toSet(),
                map.zipWithIndex(UnifiedSet.<Pair<String, Integer>>newSet()));
    }
}
