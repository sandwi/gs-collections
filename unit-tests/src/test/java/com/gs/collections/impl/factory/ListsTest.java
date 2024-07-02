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

package com.gs.collections.impl.factory;

import java.util.List;

import com.gs.collections.api.factory.list.FixedSizeListFactory;
import com.gs.collections.api.factory.list.ImmutableListFactory;
import com.gs.collections.api.list.FixedSizeList;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ListsTest
{
    @Test
    public void immutables()
    {
        ImmutableListFactory listFactory = Lists.immutable;
        Assertions.assertEquals(FastList.newList(), listFactory.of());
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of());
        Assertions.assertEquals(FastList.newListWith(1), listFactory.of(1));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1));
        Assertions.assertEquals(FastList.newListWith(1, 2), listFactory.of(1, 2));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2));
        Assertions.assertEquals(FastList.newListWith(1, 2, 3), listFactory.of(1, 2, 3));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2, 3));
        Assertions.assertEquals(FastList.newListWith(1, 2, 3, 4), listFactory.of(1, 2, 3, 4));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2, 3, 4));
        Assertions.assertEquals(FastList.newListWith(1, 2, 3, 4, 5), listFactory.of(1, 2, 3, 4, 5));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2, 3, 4, 5));
        Assertions.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6), listFactory.of(1, 2, 3, 4, 5, 6));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2, 3, 4, 5, 6));
        Assertions.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7), listFactory.of(1, 2, 3, 4, 5, 6, 7));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7));
        Assertions.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8), listFactory.of(1, 2, 3, 4, 5, 6, 7, 8));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7, 8));
        Assertions.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8, 9), listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Assertions.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Assertions.assertEquals(FastList.newListWith(1, 2, 3), listFactory.ofAll(FastList.newListWith(1, 2, 3)));
        Verify.assertInstanceOf(ImmutableList.class, listFactory.ofAll(FastList.newListWith(1, 2, 3)));
    }

    @Test
    public void immutableWithListTest()
    {
        Assertions.assertEquals(Lists.mutable.of(), Lists.mutable.of().toImmutable());
        Assertions.assertEquals(Lists.mutable.of(1).without(1), Lists.mutable.of(1).without(1).toImmutable());
        for (int i = 0; i < 12; i++)
        {
            MutableList<Integer> integers = Interval.fromTo(0, i).toList();
            Assertions.assertEquals(integers, integers.toImmutable());
            Assertions.assertEquals(integers.toImmutable(), integers.toImmutable());
        }
    }

    @Test
    public void fixedSize()
    {
        FixedSizeListFactory listFactory = Lists.fixedSize;
        Assertions.assertEquals(FastList.newList(), listFactory.of());
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of());
        Assertions.assertEquals(FastList.newListWith(1), listFactory.of(1));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1));
        Assertions.assertEquals(FastList.newListWith(1, 2), listFactory.of(1, 2));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2));
        Assertions.assertEquals(FastList.newListWith(1, 2, 3), listFactory.of(1, 2, 3));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2, 3));
        Assertions.assertEquals(FastList.newListWith(1, 2, 3, 4), listFactory.of(1, 2, 3, 4));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2, 3, 4));
        Assertions.assertEquals(FastList.newListWith(1, 2, 3, 4, 5), listFactory.of(1, 2, 3, 4, 5));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2, 3, 4, 5));
        Assertions.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6), listFactory.of(1, 2, 3, 4, 5, 6));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2, 3, 4, 5, 6));
        Assertions.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7), listFactory.of(1, 2, 3, 4, 5, 6, 7));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7));
        Assertions.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8), listFactory.of(1, 2, 3, 4, 5, 6, 7, 8));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7, 8));
        Assertions.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8, 9), listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Assertions.assertEquals(FastList.newListWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Assertions.assertEquals(FastList.newListWith(1, 2, 3), listFactory.ofAll(FastList.newListWith(1, 2, 3)));
        Verify.assertInstanceOf(FixedSizeList.class, listFactory.ofAll(FastList.newListWith(1, 2, 3)));
    }

    @Test
    public void castToList()
    {
        List<Object> list = Lists.immutable.of().castToList();
        Assertions.assertNotNull(list);
        Assertions.assertSame(Lists.immutable.of(), list);
    }

    @Test
    public void newList()
    {
        for (int i = 1; i <= 11; i++)
        {
            Interval interval = Interval.oneTo(i);
            Verify.assertEqualsAndHashCode(FastList.newList(interval), Lists.immutable.ofAll(interval));
        }
    }

    @Test
    public void copyList()
    {
        Verify.assertInstanceOf(ImmutableList.class, Lists.immutable.ofAll(Lists.fixedSize.of()));
        MutableList<Integer> list = Lists.fixedSize.of(1);
        ImmutableList<Integer> immutableList = list.toImmutable();
        Verify.assertInstanceOf(ImmutableList.class, Lists.immutable.ofAll(list));
        Assertions.assertSame(Lists.immutable.ofAll(immutableList.castToList()), immutableList);
    }

    @Test
    public void emptyList()
    {
        Assertions.assertTrue(Lists.immutable.of().isEmpty());
        Assertions.assertSame(Lists.immutable.of(), Lists.immutable.of());
        Verify.assertPostSerializedIdentity(Lists.immutable.of());
    }

    @Test
    public void newListWith()
    {
        ImmutableList<String> list = Lists.immutable.of();
        Assertions.assertEquals(list, Lists.immutable.of(list.toArray()));
        Assertions.assertEquals(list = list.newWith("1"), Lists.immutable.of("1"));
        Assertions.assertEquals(list = list.newWith("2"), Lists.immutable.of("1", "2"));
        Assertions.assertEquals(list = list.newWith("3"), Lists.immutable.of("1", "2", "3"));
        Assertions.assertEquals(list = list.newWith("4"), Lists.immutable.of("1", "2", "3", "4"));
        Assertions.assertEquals(list = list.newWith("5"), Lists.immutable.of("1", "2", "3", "4", "5"));
        Assertions.assertEquals(list = list.newWith("6"), Lists.immutable.of("1", "2", "3", "4", "5", "6"));
        Assertions.assertEquals(list = list.newWith("7"), Lists.immutable.of("1", "2", "3", "4", "5", "6", "7"));
        Assertions.assertEquals(list = list.newWith("8"), Lists.immutable.of("1", "2", "3", "4", "5", "6", "7", "8"));
        Assertions.assertEquals(list = list.newWith("9"), Lists.immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9"));
        Assertions.assertEquals(list = list.newWith("10"), Lists.immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
        Assertions.assertEquals(list = list.newWith("11"), Lists.immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"));
        Assertions.assertEquals(list = list.newWith("12"), Lists.immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"));
    }

    @Test
    public void newListWithArray()
    {
        ImmutableList<String> list = Lists.immutable.of();
        Assertions.assertEquals(list = list.newWith("1"), Lists.immutable.of(new String[]{"1"}));
        Assertions.assertEquals(list = list.newWith("2"), Lists.immutable.of(new String[]{"1", "2"}));
        Assertions.assertEquals(list = list.newWith("3"), Lists.immutable.of(new String[]{"1", "2", "3"}));
        Assertions.assertEquals(list = list.newWith("4"), Lists.immutable.of(new String[]{"1", "2", "3", "4"}));
        Assertions.assertEquals(list = list.newWith("5"), Lists.immutable.of(new String[]{"1", "2", "3", "4", "5"}));
        Assertions.assertEquals(list = list.newWith("6"), Lists.immutable.of(new String[]{"1", "2", "3", "4", "5", "6"}));
        Assertions.assertEquals(list = list.newWith("7"), Lists.immutable.of(new String[]{"1", "2", "3", "4", "5", "6", "7"}));
        Assertions.assertEquals(list = list.newWith("8"), Lists.immutable.of(new String[]{"1", "2", "3", "4", "5", "6", "7", "8"}));
        Assertions.assertEquals(list = list.newWith("9"), Lists.immutable.of(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"}));
        Assertions.assertEquals(list = list.newWith("10"), Lists.immutable.of(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}));
        Assertions.assertEquals(list = list.newWith("11"), Lists.immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"));
    }

    @Test
    public void newListWithList()
    {
        ImmutableList<String> list = Lists.immutable.of();
        FastList<String> fastList = FastList.newListWith("1");
        Assertions.assertEquals(list = list.newWith("1"), fastList.toImmutable());
        Assertions.assertEquals(list = list.newWith("2"), fastList.with("2").toImmutable());
        Assertions.assertEquals(list = list.newWith("3"), fastList.with("3").toImmutable());
        Assertions.assertEquals(list = list.newWith("4"), fastList.with("4").toImmutable());
        Assertions.assertEquals(list = list.newWith("5"), fastList.with("5").toImmutable());
        Assertions.assertEquals(list = list.newWith("6"), fastList.with("6").toImmutable());
        Assertions.assertEquals(list = list.newWith("7"), fastList.with("7").toImmutable());
        Assertions.assertEquals(list = list.newWith("8"), fastList.with("8").toImmutable());
        Assertions.assertEquals(list = list.newWith("9"), fastList.with("9").toImmutable());
        Assertions.assertEquals(list = list.newWith("10"), fastList.with("10").toImmutable());
        Assertions.assertEquals(list = list.newWith("11"), fastList.with("11").toImmutable());
    }

    @Test
    public void newListWithWithList()
    {
        Assertions.assertEquals(FastList.newList(), Lists.immutable.ofAll(FastList.newList()));
        for (int i = 0; i < 12; i++)
        {
            List<Integer> list = Interval.fromTo(0, i);
            Assertions.assertEquals(list, Lists.immutable.ofAll(list));
        }
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(Lists.class);
    }
}
