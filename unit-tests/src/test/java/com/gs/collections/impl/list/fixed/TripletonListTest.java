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

package com.gs.collections.impl.list.fixed;

import java.util.ListIterator;

import com.gs.collections.api.list.MutableList;
import com.gs.collections.impl.block.factory.Procedures2;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.test.SerializeTestHelper;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * JUnit test for {@link TripletonList}.
 */
public class TripletonListTest extends AbstractMemoryEfficientMutableListTestCase
{
    @Override
    protected int getSize()
    {
        return 3;
    }

    @Override
    protected Class<?> getListType()
    {
        return TripletonList.class;
    }

    @Test
    public void testClone()
    {
        MutableList<String> growableList = this.list.clone();
        Verify.assertEqualsAndHashCode(this.list, growableList);
        Verify.assertInstanceOf(TripletonList.class, growableList);
    }

    @Test
    public void testContains()
    {
        Assertions.assertTrue(this.list.contains("1"));
        Assertions.assertTrue(this.list.contains("2"));
        Assertions.assertTrue(this.list.contains("3"));
        Assertions.assertFalse(this.list.contains("4"));
    }

    @Test
    public void testRemove()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.list.remove(0));
        this.assertUnchanged();
    }

    @Test
    public void testAddAtIndex()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.list.add(0, "1"));
        this.assertUnchanged();
    }

    @Test
    public void testAdd()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.list.add("1"));
        this.assertUnchanged();
    }

    @Test
    public void testAddingAllToOtherList()
    {
        MutableList<String> newList = FastList.newList(this.list);
        newList.add("4");
        Assertions.assertEquals(FastList.newListWith("1", "2", "3", "4"), newList);
    }

    @Test
    public void testGet()
    {
        Verify.assertStartsWith(this.list, "1", "2", "3");
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> this.list.get(3));
    }

    @Test
    public void testSet()
    {
        MutableList<String> list = Lists.fixedSize.of("1", "2", "3");
        Assertions.assertEquals("1", list.set(0, "3"));
        Assertions.assertEquals("2", list.set(1, "2"));
        Assertions.assertEquals("3", list.set(2, "1"));
        Assertions.assertEquals(FastList.newListWith("3", "2", "1"), list);
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> list.set(3, "0"));
    }

    private void assertUnchanged()
    {
        Verify.assertInstanceOf(TripletonList.class, this.list);
        Verify.assertSize(3, this.list);
        Verify.assertNotContains(this.list, "4");
        Assertions.assertEquals(FastList.newListWith("1", "2", "3"), this.list);
    }

    @Test
    public void testSerializableEqualsAndHashCode()
    {
        Verify.assertPostSerializedEqualsAndHashCode(this.list);
        MutableList<String> copyOfList = SerializeTestHelper.serializeDeserialize(this.list);
        Assertions.assertNotSame(this.list, copyOfList);
    }

    @Test
    public void testCreate1()
    {
        MutableList<String> list = Lists.fixedSize.of("1");
        Verify.assertSize(1, list);
        Verify.assertItemAtIndex(0, list, "1");
    }

    @Test
    public void testEqualsAndHashCode()
    {
        MutableList<String> one = Lists.fixedSize.of("1", "2", "3");
        MutableList<String> oneA = FastList.newList(one);
        Verify.assertEqualsAndHashCode(one, oneA);
        Verify.assertPostSerializedEqualsAndHashCode(one);
    }

    @Test
    public void testForEach()
    {
        MutableList<String> result = Lists.mutable.of();
        MutableList<String> source = Lists.fixedSize.of("1", "2", "3");
        source.forEach(CollectionAddProcedure.on(result));
        Assertions.assertEquals(FastList.newListWith("1", "2", "3"), result);
    }

    @Test
    public void forEachFromTo()
    {
        MutableList<String> result = Lists.mutable.of();
        MutableList<String> source = Lists.fixedSize.of("1", "2", "3");
        source.forEach(0, 2, result::add);
        Assertions.assertEquals(FastList.newListWith("1", "2", "3"), result);
    }

    @Test
    public void forEachWithIndex()
    {
        int[] indexSum = new int[1];
        MutableList<String> result = Lists.mutable.of();
        MutableList<String> source = Lists.fixedSize.of("1", "2", "3");
        source.forEachWithIndex((each, index) -> {
            result.add(each);
            indexSum[0] += index;
        });
        Assertions.assertEquals(FastList.newListWith("1", "2", "3"), result);
        Assertions.assertEquals(3, indexSum[0]);
    }

    @Test
    public void forEachWithIndexFromTo()
    {
        int[] indexSum = new int[1];
        MutableList<String> result = Lists.mutable.of();
        MutableList<String> source = Lists.fixedSize.of("1", "2", "3");
        source.forEachWithIndex(0, 2, (each, index) -> {
            result.add(each);
            indexSum[0] += index;
        });
        Assertions.assertEquals(FastList.newListWith("1", "2", "3"), result);
        Assertions.assertEquals(3, indexSum[0]);
    }

    @Test
    public void testForEachWith()
    {
        MutableList<String> result = Lists.mutable.of();
        MutableList<String> source = Lists.fixedSize.of("1", "2", "3");
        source.forEachWith(Procedures2.fromProcedure(result::add), null);
        Assertions.assertEquals(FastList.newListWith("1", "2", "3"), result);
    }

    @Test
    public void testGetFirstGetLast()
    {
        MutableList<String> list3 = Lists.fixedSize.of("1", "2", "3");
        Assertions.assertEquals("1", list3.getFirst());
        Assertions.assertEquals("3", list3.getLast());
    }

    @Test
    public void testForLoop()
    {
        MutableList<String> list = Lists.fixedSize.of("one", "two", "three");
        MutableList<String> upperList = Lists.fixedSize.of("ONE", "TWO", "THREE");
        for (String each : list)
        {
            Verify.assertContains(upperList, each.toUpperCase());
        }
    }

    @Test
    public void testSubList()
    {
        MutableList<String> list = Lists.fixedSize.of("one", "two", "three");
        MutableList<String> subList = list.subList(0, 3);
        MutableList<String> upperList = Lists.fixedSize.of("ONE", "TWO", "THREE");
        for (String each : subList)
        {
            Verify.assertContains(upperList, each.toUpperCase());
        }
        Assertions.assertEquals("one", subList.getFirst());
        Assertions.assertEquals("three", subList.getLast());
        MutableList<String> subList2 = list.subList(1, 2);
        Assertions.assertEquals("two", subList2.getFirst());
        Assertions.assertEquals("two", subList2.getLast());
        MutableList<String> subList3 = list.subList(0, 1);
        Assertions.assertEquals("one", subList3.getFirst());
        Assertions.assertEquals("one", subList3.getLast());
        MutableList<String> subList4 = subList.subList(1, 3);
        Assertions.assertEquals("two", subList4.getFirst());
        Assertions.assertEquals("three", subList4.getLast());
    }

    @Test
    public void testListIterator()
    {
        MutableList<String> list = Lists.fixedSize.of("one", "two", "three");
        ListIterator<String> iterator = list.listIterator();
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertFalse(iterator.hasPrevious());
        Assertions.assertEquals("one", iterator.next());
        Assertions.assertEquals("two", iterator.next());
        Assertions.assertEquals("three", iterator.next());
        Assertions.assertTrue(iterator.hasPrevious());
        Assertions.assertEquals("three", iterator.previous());
        Assertions.assertEquals("two", iterator.previous());
        Assertions.assertEquals("one", iterator.previous());
        iterator.set("1");
        Assertions.assertEquals("1", iterator.next());
        Assertions.assertEquals("1", list.getFirst());
        list.subList(1, 3);
    }

    @Test
    public void testSubListListIterator()
    {
        MutableList<String> list = Lists.fixedSize.of("one", "two", "three");
        MutableList<String> subList = list.subList(1, 3);
        ListIterator<String> iterator = subList.listIterator();
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertFalse(iterator.hasPrevious());
        Assertions.assertEquals("two", iterator.next());
        Assertions.assertEquals("three", iterator.next());
        Assertions.assertTrue(iterator.hasPrevious());
        Assertions.assertEquals("three", iterator.previous());
        Assertions.assertEquals("two", iterator.previous());
        iterator.set("2");
        Assertions.assertEquals("2", iterator.next());
        Assertions.assertEquals("2", subList.getFirst());
        Assertions.assertEquals("2", list.get(1));
    }

    @Test
    public void testSubListSet()
    {
        MutableList<String> list = Lists.fixedSize.of("one", "two", "three");
        MutableList<String> subList = list.subList(1, 3);
        Assertions.assertEquals("two", subList.set(0, "2"));
        Assertions.assertEquals("2", subList.getFirst());
        Assertions.assertEquals("2", list.get(1));
    }

    @Test
    public void testNewEmpty()
    {
        MutableList<String> list = Lists.fixedSize.of("one", "two", "three");
        Verify.assertEmpty(list.newEmpty());
    }

    @Test
    public void subListForEach()
    {
        MutableList<String> list = Lists.fixedSize.of("1", "2", "3");
        MutableList<String> source = list.subList(1, 3);
        MutableList<String> result = Lists.mutable.of();
        source.forEach(CollectionAddProcedure.on(result));
        Assertions.assertEquals(FastList.newListWith("2", "3"), result);
    }

    @Test
    public void testSubListForEachWithIndex()
    {
        MutableList<String> list = Lists.fixedSize.of("1", "2", "3");
        MutableList<String> source = list.subList(1, 3);
        int[] indexSum = new int[1];
        MutableList<String> result = Lists.mutable.of();
        source.forEachWithIndex((each, index) -> {
            result.add(each);
            indexSum[0] += index;
        });
        Assertions.assertEquals(FastList.newListWith("2", "3"), result);
        Assertions.assertEquals(1, indexSum[0]);
    }

    @Test
    public void testSubListForEachWith()
    {
        MutableList<String> list = Lists.fixedSize.of("1", "2", "3");
        MutableList<String> source = list.subList(1, 3);
        MutableList<String> result = Lists.mutable.of();
        source.forEachWith(Procedures2.fromProcedure(result::add), null);
        Assertions.assertEquals(FastList.newListWith("2", "3"), result);
    }

    @Test
    public void testIndexOf()
    {
        MutableList<String> list = Lists.fixedSize.of("1", null, "3");
        Assertions.assertEquals(0, list.indexOf("1"));
        Assertions.assertEquals(1, list.indexOf(null));
        Assertions.assertEquals(2, list.indexOf("3"));
        Assertions.assertEquals(-1, list.indexOf("4"));
    }

    @Test
    public void testLastIndexOf()
    {
        MutableList<String> list = Lists.fixedSize.of("1", null, "1");
        Assertions.assertEquals(2, list.lastIndexOf("1"));
        Assertions.assertEquals(1, list.lastIndexOf(null));
        Assertions.assertEquals(-1, list.lastIndexOf("4"));
    }

    @Test
    public void without()
    {
        MutableList<Integer> list = new TripletonList<>(2, 3, 2);
        Assertions.assertSame(list, list.without(9));
        list = list.without(2);
        Verify.assertListsEqual(FastList.newListWith(3, 2), list);
        Verify.assertInstanceOf(DoubletonList.class, list);
    }
}
