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

package com.gs.collections.impl.bag.sorted.immutable;

import java.util.Comparator;

import com.gs.collections.api.bag.sorted.ImmutableSortedBag;
import com.gs.collections.impl.bag.sorted.mutable.TreeBag;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.factory.SortedBags;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ImmutableSortedBagFactoryTest
{
    @Test
    public void empty()
    {
        Assertions.assertEquals(TreeBag.newBag(), SortedBags.immutable.empty());
        Verify.assertInstanceOf(ImmutableSortedBag.class, SortedBags.immutable.empty());

        Assertions.assertEquals(TreeBag.newBag(Comparators.reverseNaturalOrder()), SortedBags.immutable.empty(Comparators.reverseNaturalOrder()));
        Verify.assertInstanceOf(ImmutableSortedBag.class, SortedBags.immutable.empty(Comparators.reverseNaturalOrder()));
    }

    @Test
    public void ofElements()
    {
        Assertions.assertEquals(new ImmutableSortedBagImpl<>(SortedBags.mutable.of(1, 1, 2)), SortedBags.immutable.of(1, 1, 2));
        Assertions.assertEquals(new ImmutableSortedBagImpl<>(SortedBags.mutable.of(Comparators.reverseNaturalOrder(), 1, 1, 2)), SortedBags.immutable.of(Comparators.reverseNaturalOrder(), 1, 1, 2));

        Assertions.assertEquals(TreeBag.newBag(), SortedBags.immutable.of());
        Verify.assertInstanceOf(ImmutableSortedBag.class, SortedBags.immutable.of());
        Comparator<Integer> nullComparator = null;
        Assertions.assertEquals(TreeBag.newBag(), SortedBags.immutable.of(nullComparator));
        Verify.assertInstanceOf(ImmutableSortedBag.class, SortedBags.immutable.of(nullComparator));
        Assertions.assertEquals(TreeBag.newBag(Comparators.reverseNaturalOrder()), SortedBags.immutable.of(Comparator.reverseOrder()));
        Verify.assertInstanceOf(ImmutableSortedBag.class, SortedBags.immutable.of(Comparator.reverseOrder()));
        Assertions.assertEquals(TreeBag.newBag(Comparators.reverseNaturalOrder()), SortedBags.immutable.of(Comparator.reverseOrder(), new Integer[]{}));
        Verify.assertInstanceOf(ImmutableSortedBag.class, SortedBags.immutable.of(Comparator.reverseOrder(), new Integer[]{}));
        Assertions.assertEquals(TreeBag.newBag(), SortedBags.immutable.of(new Integer[]{}));
        Verify.assertInstanceOf(ImmutableSortedBag.class, SortedBags.immutable.of(new Integer[]{}));
    }

    @Test
    public void withElements()
    {
        Assertions.assertEquals(new ImmutableSortedBagImpl<>(SortedBags.mutable.with(1, 1, 2)), SortedBags.immutable.with(1, 1, 2));
        Verify.assertThrows(IllegalArgumentException.class, () -> {
            new ImmutableSortedBagImpl<>(SortedBags.mutable.with(Comparators.reverseNaturalOrder(), FastList.newList().toArray()));
        });
        Assertions.assertEquals(new ImmutableSortedBagImpl<>(SortedBags.mutable.with(Comparators.reverseNaturalOrder(), 1, 1, 2)), SortedBags.immutable.with(Comparators.reverseNaturalOrder(), 1, 1, 2));
    }

    @Test
    public void ofAll()
    {
        Assertions.assertEquals(new ImmutableSortedBagImpl<>(SortedBags.mutable.of(1, 1, 2)), SortedBags.immutable.ofAll(new ImmutableSortedBagImpl<>(TreeBag.newBagWith(1, 1, 2))));
        Assertions.assertEquals(new ImmutableSortedBagImpl<>(SortedBags.mutable.of(1, 1, 2)), SortedBags.immutable.ofAll(FastList.newListWith(1, 1, 2)));
        Assertions.assertEquals(new ImmutableSortedBagImpl<>(SortedBags.mutable.of(Comparators.reverseNaturalOrder(), 1, 1, 2)), SortedBags.immutable.ofAll(Comparators.reverseNaturalOrder(), FastList.newListWith(1, 1, 2)));
    }

    @Test
    public void ofSortedBag()
    {
        Assertions.assertEquals(new ImmutableSortedBagImpl<>(SortedBags.immutable.of(1)), SortedBags.immutable.ofSortedBag(new ImmutableSortedBagImpl<>(TreeBag.newBagWith(1))));
        Assertions.assertEquals(new ImmutableSortedBagImpl<>(SortedBags.immutable.of(1)), SortedBags.immutable.ofSortedBag(TreeBag.newBagWith(1)));
        Assertions.assertEquals(SortedBags.immutable.of(Comparators.reverseNaturalOrder()), SortedBags.immutable.ofSortedBag(TreeBag.newBag(Comparators.reverseNaturalOrder())));
    }

    @Test
    public void withSortedBag()
    {
        Assertions.assertEquals(new ImmutableSortedBagImpl<>(SortedBags.immutable.of(1)), SortedBags.immutable.ofSortedBag(new ImmutableSortedBagImpl<>(TreeBag.newBagWith(1))));
    }
}
