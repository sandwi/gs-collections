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

package com.gs.collections.impl.lazy.parallel.set.sorted;

import java.util.NoSuchElementException;

import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.set.sorted.ParallelSortedSetIterable;
import com.gs.collections.api.set.sorted.SortedSetIterable;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.function.PassThruFunction0;
import com.gs.collections.impl.factory.SortedSets;
import com.gs.collections.impl.set.sorted.mutable.TreeSortedSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ImmutableEmptySortedSetParallelTest extends NonParallelSortedSetIterableTestCase
{
    @Override
    protected SortedSetIterable<Integer> getExpected()
    {
        return TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder());
    }

    @Override
    protected SortedSetIterable<Integer> getExpectedWith(Integer... littleElements)
    {
        return TreeSortedSet.newSetWith(Comparators.reverseNaturalOrder());
    }

    @Override
    protected ParallelSortedSetIterable<Integer> classUnderTest()
    {
        return this.newWith();
    }

    @Override
    protected ParallelSortedSetIterable<Integer> newWith(Integer... littleElements)
    {
        return SortedSets.immutable.with(Comparators.<Integer>reverseNaturalOrder()).asParallel(this.executorService, this.batchSize);
    }

    @Test
    public void asParallel_small_batch()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            SortedSets.immutable.with(Comparators.reverseNaturalOrder()).asParallel(this.executorService, 0);
        });
    }

    @Test
    public void asParallel_null_executorService()
    {
        assertThrows(NullPointerException.class, () -> {
            SortedSets.immutable.with(Comparators.reverseNaturalOrder()).asParallel(null, 2);
        });
    }

    @Override
    @Test
    public void allSatisfy()
    {
        Assertions.assertTrue(this.classUnderTest().allSatisfy(Predicates.lessThan(0)));
        Assertions.assertTrue(this.classUnderTest().allSatisfy(Predicates.greaterThanOrEqualTo(0)));
    }

    @Override
    @Test
    public void allSatisfyWith()
    {
        Assertions.assertTrue(this.classUnderTest().allSatisfyWith(Predicates2.<Integer>lessThan(), 0));
        Assertions.assertTrue(this.classUnderTest().allSatisfyWith(Predicates2.<Integer>greaterThanOrEqualTo(), 0));
    }

    @Override
    @Test
    public void anySatisfy()
    {
        Assertions.assertFalse(this.classUnderTest().anySatisfy(Predicates.lessThan(0)));
        Assertions.assertFalse(this.classUnderTest().anySatisfy(Predicates.greaterThanOrEqualTo(0)));
    }

    @Override
    @Test
    public void anySatisfyWith()
    {
        Assertions.assertFalse(this.classUnderTest().anySatisfyWith(Predicates2.<Integer>lessThan(), 0));
        Assertions.assertFalse(this.classUnderTest().anySatisfyWith(Predicates2.<Integer>greaterThanOrEqualTo(), 0));
    }

    @Override
    @Test
    public void noneSatisfy()
    {
        Assertions.assertTrue(this.classUnderTest().noneSatisfy(Predicates.lessThan(0)));
        Assertions.assertTrue(this.classUnderTest().noneSatisfy(Predicates.greaterThanOrEqualTo(0)));
    }

    @Override
    @Test
    public void noneSatisfyWith()
    {
        Assertions.assertTrue(this.classUnderTest().noneSatisfyWith(Predicates2.<Integer>lessThan(), 0));
        Assertions.assertTrue(this.classUnderTest().noneSatisfyWith(Predicates2.<Integer>greaterThanOrEqualTo(), 0));
    }

    @Override
    @Test
    public void appendString_throws()
    {
        // Not applicable for empty collections
    }

    @Override
    @Test
    public void detect()
    {
        Assertions.assertNull(this.classUnderTest().detect(Integer.valueOf(0)::equals));
    }

    @Override
    @Test
    public void detectIfNone()
    {
        Assertions.assertEquals(Integer.valueOf(10), this.classUnderTest().detectIfNone(Integer.valueOf(0)::equals, () -> 10));
    }

    @Override
    @Test
    public void detectWith()
    {
        Assertions.assertNull(this.classUnderTest().detectWith(Object::equals, Integer.valueOf(0)));
    }

    @Override
    @Test
    public void detectWithIfNone()
    {
        Function0<Integer> function = new PassThruFunction0<>(Integer.valueOf(1000));
        Assertions.assertEquals(Integer.valueOf(1000), this.classUnderTest().detectWithIfNone(Object::equals, Integer.valueOf(0), function));
    }

    @Override
    @Test
    public void min()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.classUnderTest().min(Integer::compareTo);
        });
    }

    @Override
    @Test
    public void max()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.classUnderTest().max(Integer::compareTo);
        });
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
    public void min_without_comparator()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.classUnderTest().min();
        });
    }

    @Override
    @Test
    public void max_without_comparator()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.classUnderTest().max();
        });
    }

    @Override
    @Test
    public void minWithEmptyBatch()
    {
        assertThrows(NoSuchElementException.class, () -> {
            super.minWithEmptyBatch();
        });
    }

    @Override
    @Test
    public void maxWithEmptyBatch()
    {
        assertThrows(NoSuchElementException.class, () -> {
            super.minWithEmptyBatch();
        });
    }

    @Override
    @Test
    public void min_null_throws()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.classUnderTest().min(Integer::compareTo);
        });
    }

    @Override
    @Test
    public void max_null_throws()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.classUnderTest().max(Integer::compareTo);
        });
    }

    @Override
    @Test
    public void minBy_null_throws()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.classUnderTest().minBy(Integer::valueOf);
        });
    }

    @Override
    @Test
    public void maxBy_null_throws()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.classUnderTest().maxBy(Integer::valueOf);
        });
    }
}
