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

package com.gs.collections.impl.list.fixed;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.gs.collections.api.list.MutableList;
import com.gs.collections.impl.block.factory.Procedures2;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * JUnit test for {@link FixedSizeListFactoryImpl}.
 */
public class FixedSizeListFactoryTest
{
    @Test
    public void createEmpty()
    {
        MutableList<String> list = Lists.fixedSize.of();
        Assertions.assertSame(list, Lists.fixedSize.of());
        Verify.assertInstanceOf(EmptyList.class, list);
        Verify.assertSize(0, list);
        Assertions.assertTrue(list.isEmpty());
        Assertions.assertFalse(list.notEmpty());
        Assertions.assertNull(list.getFirst());
        Assertions.assertNull(list.getLast());
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> list.set(0, "nope"));
    }

    @Test
    public void withExtra()
    {
        MutableList<String> list0 = Lists.fixedSize.of();
        Verify.assertEmpty(list0);

        MutableList<String> list1 = list0.with("1");
        Assertions.assertEquals(FastList.newListWith("1"), list1);
        Verify.assertInstanceOf(SingletonList.class, list1);

        MutableList<String> list2 = list1.with("2");
        Assertions.assertEquals(FastList.newListWith("1", "2"), list2);
        Verify.assertInstanceOf(DoubletonList.class, list2);

        MutableList<String> list3 = list2.with("3");
        Assertions.assertEquals(FastList.newListWith("1", "2", "3"), list3);
        Verify.assertInstanceOf(TripletonList.class, list3);

        MutableList<String> list4 = list3.with("4");
        Assertions.assertEquals(FastList.newListWith("1", "2", "3", "4"), list4);
        Verify.assertInstanceOf(QuadrupletonList.class, list4);

        MutableList<String> list5 = list4.with("5");
        Assertions.assertEquals(FastList.newListWith("1", "2", "3", "4", "5"), list5);
        Verify.assertInstanceOf(QuintupletonList.class, list5);

        MutableList<String> list6 = list5.with("6");
        Assertions.assertEquals(FastList.newListWith("1", "2", "3", "4", "5", "6"), list6);
        Verify.assertInstanceOf(SextupletonList.class, list6);

        MutableList<String> list7 = list6.with("7");
        Assertions.assertEquals(FastList.newListWith("1", "2", "3", "4", "5", "6", "7"), list7);
        Verify.assertInstanceOf(ArrayAdapter.class, list7);

        MutableList<String> list8 = list7.with("8");
        Assertions.assertEquals(FastList.newListWith("1", "2", "3", "4", "5", "6", "7", "8"), list8);
        Verify.assertInstanceOf(ArrayAdapter.class, list8);
    }

    @Test
    public void create1()
    {
        MutableList<String> list = Lists.fixedSize.of("1");
        Verify.assertSize(1, list);
        Verify.assertItemAtIndex(0, list, "1");
    }

    @Test
    public void create2()
    {
        MutableList<String> list = Lists.fixedSize.of("1", "2");
        Verify.assertSize(2, list);
        Verify.assertStartsWith(list, "1", "2");
    }

    @Test
    public void create3()
    {
        MutableList<String> list = Lists.fixedSize.of("1", "2", "3");
        Verify.assertSize(3, list);
        Verify.assertStartsWith(list, "1", "2", "3");
    }

    @Test
    public void create4()
    {
        MutableList<String> list = Lists.fixedSize.of("1", "2", "3", "4");
        Verify.assertSize(4, list);
        Verify.assertStartsWith(list, "1", "2", "3", "4");
    }

    @Test
    public void createList_singleton()
    {
        Verify.assertEmpty(Lists.fixedSize.of());
        Assertions.assertSame(Lists.fixedSize.of(), Lists.fixedSize.of());
    }

    @Test
    public void varArgsListCreation()
    {
        String[] content = {"one", "two"};

        //List<Object>   list1 = Lists.fixedSize.of(content);  // incompatible types: List<Object> vs List<String>
        //List<String[]> list2 = Lists.fixedSize.of(content);  // incompatible types: List<String[]> vs List<String>
        List<String[]> list3 = Lists.fixedSize.<String[]>of(content);  // correct!
        Verify.assertSize(1, list3);

        MutableList<String> list4 = Lists.fixedSize.of(content);
        Verify.assertSize(2, list4);
    }

    @Test
    public void equalsAndHashCode()
    {
        MutableList<String> empty = Lists.fixedSize.of();
        MutableList<String> emptyA = Lists.mutable.of();
        Verify.assertEqualsAndHashCode(empty, emptyA);

        MutableList<String> one = Lists.fixedSize.of("1");
        MutableList<String> oneA = Lists.mutable.of();
        oneA.add("1");
        Verify.assertEqualsAndHashCode(one, oneA);

        MutableList<String> two = Lists.fixedSize.of("1", "2");
        MutableList<String> twoA = Lists.mutable.of();
        twoA.add("1");
        twoA.add("2");
        Verify.assertEqualsAndHashCode(two, twoA);

        MutableList<String> three = Lists.fixedSize.of("1", "2", "3");
        MutableList<String> threeA = Lists.mutable.of();
        threeA.add("1");
        threeA.add("2");
        threeA.add("3");
        Verify.assertEqualsAndHashCode(three, threeA);

        Assertions.assertNotEquals(three, twoA);
        Assertions.assertNotEquals(twoA, three);

        MutableList<String> differentThree = Lists.mutable.of();
        differentThree.add("1");
        differentThree.add("Two");
        differentThree.add("3");
        Assertions.assertNotEquals(three, differentThree);
        Assertions.assertNotEquals(differentThree, three);

        Assertions.assertEquals(new LinkedList<>(threeA), three);
        Assertions.assertNotEquals(new LinkedList<>(differentThree), three);
        Assertions.assertNotEquals(new LinkedList<>(FastList.newListWith("1", "2", "3", "4")), three);
        Assertions.assertNotEquals(new LinkedList<>(FastList.newListWith("1", "2")), three);
    }

    @Test
    public void serializationOfEmptyList()
    {
        Serializable list = (Serializable) Lists.fixedSize.of();
        Serializable list2 = (Serializable) Lists.fixedSize.of();
        Assertions.assertSame(list, list2);
        Verify.assertPostSerializedIdentity(list);
    }

    @Test
    public void forEach()
    {
        MutableList<String> result = Lists.mutable.of();
        MutableList<String> source = Lists.fixedSize.of("1", "2", "3", "4", "5", "6");
        source.forEach(CollectionAddProcedure.on(result));
        Assertions.assertEquals(FastList.newListWith("1", "2", "3", "4", "5", "6"), result);
    }

    @Test
    public void forEachWithIndex()
    {
        int[] indexSum = new int[1];
        MutableList<String> result = Lists.mutable.of();
        MutableList<String> source = Lists.fixedSize.of("1", "2", "3", "4", "5", "6");
        source.forEachWithIndex((each, index) -> {
            result.add(each);
            indexSum[0] += index;
        });
        Assertions.assertEquals(FastList.newListWith("1", "2", "3", "4", "5", "6"), result);
        Assertions.assertEquals(15, indexSum[0]);
    }

    @Test
    public void forEachWith()
    {
        MutableList<String> result = Lists.mutable.of();
        MutableList<String> source = Lists.fixedSize.of("1", "2", "3", "4", "5", "6");
        source.forEachWith(Procedures2.fromProcedure(result::add), null);
        Assertions.assertEquals(FastList.newListWith("1", "2", "3", "4", "5", "6"), result);
    }

    @Test
    public void getFirstGetLast()
    {
        MutableList<String> list1 = Lists.fixedSize.of("1");
        Assertions.assertEquals("1", list1.getFirst());
        Assertions.assertEquals("1", list1.getLast());
        MutableList<String> list2 = Lists.fixedSize.of("1", "2");
        Assertions.assertEquals("1", list2.getFirst());
        Assertions.assertEquals("2", list2.getLast());
        MutableList<String> list3 = Lists.fixedSize.of("1", "2", "3");
        Assertions.assertEquals("1", list3.getFirst());
        Assertions.assertEquals("3", list3.getLast());
        MutableList<String> list4 = Lists.fixedSize.of("1", "2", "3", "4");
        Assertions.assertEquals("1", list4.getFirst());
        Assertions.assertEquals("4", list4.getLast());
        MutableList<String> list5 = Lists.fixedSize.of("1", "2", "3", "4", "5");
        Assertions.assertEquals("1", list5.getFirst());
        Assertions.assertEquals("5", list5.getLast());
        MutableList<String> list6 = Lists.fixedSize.of("1", "2", "3", "4", "5", "6");
        Assertions.assertEquals("1", list6.getFirst());
        Assertions.assertEquals("6", list6.getLast());
    }
}
