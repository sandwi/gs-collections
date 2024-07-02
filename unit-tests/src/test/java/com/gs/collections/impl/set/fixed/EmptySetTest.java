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

package com.gs.collections.impl.set.fixed;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.multimap.set.MutableSetMultimap;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.factory.Procedures;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.factory.Sets;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmptySetTest extends AbstractMemoryEfficientMutableSetTestCase
{
    private EmptySet<Object> emptySet;

    @BeforeEach
    public void setUp()
    {
        this.emptySet = new EmptySet<>();
    }

    @Override
    protected MutableSet<String> classUnderTest()
    {
        return new EmptySet<>();
    }

    @Override
    protected MutableSet<String> classUnderTestWithNull()
    {
        throw new AssertionError();
    }

    @Test
    public void testEmpty()
    {
        Assertions.assertTrue(this.emptySet.isEmpty());
        Assertions.assertFalse(this.emptySet.notEmpty());
        Assertions.assertTrue(Sets.fixedSize.of().isEmpty());
        Assertions.assertFalse(Sets.fixedSize.of().notEmpty());
    }

    @Test
    public void testSize()
    {
        Verify.assertSize(0, this.emptySet);
    }

    @Test
    public void testContains()
    {
        Assertions.assertFalse(this.emptySet.contains("Something"));
        Assertions.assertFalse(this.emptySet.contains(null));
    }

    @Test
    public void testGetFirstLast()
    {
        Assertions.assertNull(this.emptySet.getFirst());
        Assertions.assertNull(this.emptySet.getLast());
    }

    @Test
    public void testReadResolve()
    {
        Verify.assertInstanceOf(EmptySet.class, Sets.fixedSize.of());
        Verify.assertPostSerializedIdentity(Sets.fixedSize.of());
    }

    @Override
    @Test
    public void testClone()
    {
        Assertions.assertSame(Sets.fixedSize.of().clone(), Sets.fixedSize.of());
    }

    @Test
    public void testForEach()
    {
        this.emptySet.forEach(Procedures.cast(each -> Assertions.fail()));
    }

    @Test
    public void testForEachWithIndex()
    {
        this.emptySet.forEachWithIndex((each, index) -> Assertions.fail());
    }

    @Test
    public void testForEachWith()
    {
        this.emptySet.forEachWith((argument1, argument2) -> Assertions.fail(), "param");
    }

    @Test
    public void testIterator()
    {
        Iterator<Object> it = this.emptySet.iterator();
        Assertions.assertFalse(it.hasNext());

        Verify.assertThrows(NoSuchElementException.class, (Runnable) it::next);

        Verify.assertThrows(UnsupportedOperationException.class, it::remove);
    }

    @Test
    @Override
    public void groupBy()
    {
        MutableSetMultimap<Class<?>, String> multimap = this.classUnderTest().groupBy(Object::getClass);
        Verify.assertSize(this.classUnderTest().size(), multimap);
        Assertions.assertTrue(multimap.keysView().isEmpty());
        Assertions.assertEquals(this.classUnderTest(), multimap.get(String.class));
    }

    @Test
    @Override
    public void min()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.classUnderTest().min(String::compareTo);
        });
    }

    @Test
    @Override
    public void max()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.classUnderTest().max(String::compareTo);
        });
    }

    @Test
    @Override
    public void min_null_throws()
    {
        // Not applicable for empty collections
    }

    @Test
    @Override
    public void max_null_throws()
    {
        // Not applicable for empty collections
    }

    @Test
    @Override
    public void min_without_comparator()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.classUnderTest().min();
        });
    }

    @Test
    @Override
    public void max_without_comparator()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.classUnderTest().max();
        });
    }

    @Test
    @Override
    public void min_null_throws_without_comparator()
    {
        // Not applicable for empty collections
    }

    @Test
    @Override
    public void max_null_throws_without_comparator()
    {
        // Not applicable for empty collections
    }

    @Override
    @Test
    public void minBy()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.classUnderTest().minBy(String::valueOf);
        });
    }

    @Override
    @Test
    public void maxBy()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.classUnderTest().maxBy(String::valueOf);
        });
    }

    @Override
    @Test
    public void zip()
    {
        MutableSet<String> set = this.classUnderTest();
        List<Object> nulls = Collections.nCopies(set.size(), null);
        List<Object> nullsPlusOne = Collections.nCopies(set.size() + 1, null);

        MutableSet<Pair<String, Object>> pairs = set.zip(nulls);
        Assertions.assertEquals(set, pairs.collect((Function<Pair<String, ?>, String>) Pair::getOne));
        Assertions.assertEquals(nulls, pairs.collect((Function<Pair<?, Object>, Object>) Pair::getTwo, Lists.mutable.of()));

        MutableSet<Pair<String, Object>> pairsPlusOne = set.zip(nullsPlusOne);
        Assertions.assertEquals(set, pairsPlusOne.collect((Function<Pair<String, ?>, String>) Pair::getOne));
        Assertions.assertEquals(nulls, pairsPlusOne.collect((Function<Pair<?, Object>, Object>) Pair::getTwo, Lists.mutable.of()));

        Assertions.assertEquals(
                set.zip(nulls),
                set.zip(nulls, UnifiedSet.<Pair<String, Object>>newSet()));
    }

    @Override
    @Test
    public void zipWithIndex()
    {
        MutableSet<String> set = this.classUnderTest();
        MutableSet<Pair<String, Integer>> pairs = set.zipWithIndex();

        Assertions.assertEquals(
                set,
                pairs.collect((Function<Pair<String, ?>, String>) Pair::getOne));
        Assertions.assertEquals(
                UnifiedSet.newSet(),
                pairs.collect((Function<Pair<?, Integer>, Integer>) Pair::getTwo));

        Assertions.assertEquals(
                set.zipWithIndex(),
                set.zipWithIndex(UnifiedSet.<Pair<String, Integer>>newSet()));
    }

    @Override
    @Test
    public void chunk_large_size()
    {
        Assertions.assertEquals(Lists.mutable.of(), this.classUnderTest().chunk(10));
    }

    @Override
    @Test
    public void union()
    {
        Assertions.assertEquals(
                UnifiedSet.newSetWith("a", "b", "c"),
                this.classUnderTest().union(UnifiedSet.newSetWith("a", "b", "c")));
    }

    @Override
    @Test
    public void unionInto()
    {
        Assertions.assertEquals(
                UnifiedSet.newSetWith("a", "b", "c"),
                this.classUnderTest().unionInto(UnifiedSet.newSetWith("a", "b", "c"), UnifiedSet.<String>newSet()));
    }

    @Override
    @Test
    public void intersect()
    {
        Assertions.assertEquals(
                UnifiedSet.<String>newSet(),
                this.classUnderTest().intersect(UnifiedSet.newSetWith("1", "2", "3")));
    }

    @Override
    @Test
    public void intersectInto()
    {
        Assertions.assertEquals(
                UnifiedSet.<String>newSet(),
                this.classUnderTest().intersectInto(UnifiedSet.newSetWith("1", "2", "3"), UnifiedSet.<String>newSet()));
    }

    @Override
    @Test
    public void difference()
    {
        MutableSet<String> set = this.classUnderTest();
        MutableSet<String> difference = set.difference(UnifiedSet.newSetWith("2", "3", "4", "not present"));
        Assertions.assertEquals(UnifiedSet.<String>newSet(), difference);
        Assertions.assertEquals(set, set.difference(UnifiedSet.newSetWith("not present")));
    }

    @Override
    @Test
    public void differenceInto()
    {
        MutableSet<String> set = this.classUnderTest();
        MutableSet<String> difference = set.differenceInto(UnifiedSet.newSetWith("2", "3", "4", "not present"), UnifiedSet.<String>newSet());
        Assertions.assertEquals(UnifiedSet.<String>newSet(), difference);
        Assertions.assertEquals(set, set.differenceInto(UnifiedSet.newSetWith("not present"), UnifiedSet.<String>newSet()));
    }

    @Override
    @Test
    public void symmetricDifference()
    {
        Assertions.assertEquals(
                UnifiedSet.newSetWith("not present"),
                this.classUnderTest().symmetricDifference(UnifiedSet.newSetWith("not present")));
    }

    @Override
    @Test
    public void symmetricDifferenceInto()
    {
        Assertions.assertEquals(
                UnifiedSet.newSetWith("not present"),
                this.classUnderTest().symmetricDifferenceInto(UnifiedSet.newSetWith("not present"), UnifiedSet.<String>newSet()));
    }
}
