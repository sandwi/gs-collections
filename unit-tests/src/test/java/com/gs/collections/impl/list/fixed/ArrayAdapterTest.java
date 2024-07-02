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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.collection.MutableCollection;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.tuple.Twin;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.function.AddFunction;
import com.gs.collections.impl.block.function.PassThruFunction0;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.list.mutable.AbstractListTestCase;
import com.gs.collections.impl.list.mutable.ArrayListAdapter;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.UnmodifiableMutableList;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.SerializeTestHelper;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JUnit test for {@link ArrayAdapter}.
 */
public class ArrayAdapterTest extends AbstractListTestCase
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ArrayAdapterTest.class);

    @Override
    protected <T> MutableList<T> newWith(T... littleElements)
    {
        return ArrayAdapter.newArrayWith(littleElements);
    }

    @Test
    public void testNewList()
    {
        MutableList<Integer> collection = this.newArray();
        Verify.assertEmpty(collection);
        Verify.assertSize(0, collection);
        MutableList<Integer> collection1 = ArrayAdapter.newArrayWith(1, 2, 3, 4, 5, 6);
        Verify.assertSize(6, collection1);
        Verify.assertInstanceOf(ArrayAdapter.class, collection1);
    }

    private MutableList<Integer> newArray()
    {
        return ArrayAdapter.newArray();
    }

    @Test
    public void adapt()
    {
        MutableList<Integer> collection = ArrayAdapter.newArrayWith();
        Verify.assertEmpty(collection);
    }

    @Test
    public void newListWith()
    {
        MutableList<Integer> collection = ArrayAdapter.newArrayWith(1);
        Verify.assertNotEmpty(collection);
        Verify.assertSize(1, collection);
        Verify.assertContains(1, collection);
    }

    @Test
    public void newListWithWith()
    {
        MutableList<Integer> collection = ArrayAdapter.newArrayWith(1, 2);
        Verify.assertNotEmpty(collection);
        Verify.assertSize(2, collection);
        Verify.assertContainsAll(collection, 1, 2);
    }

    @Test
    public void newListWithWithWith()
    {
        MutableList<Integer> collection = ArrayAdapter.newArrayWith(1, 2, 3);
        Verify.assertNotEmpty(collection);
        Verify.assertSize(3, collection);
        Verify.assertContainsAll(collection, 1, 2, 3);
    }

    @Test
    public void newListWithVarArgs()
    {
        MutableList<Integer> collection = this.newWith(1, 2, 3, 4);
        Verify.assertNotEmpty(collection);
        Verify.assertSize(4, collection);
        Verify.assertContainsAll(collection, 1, 2, 3, 4);
    }

    @Override
    @Test
    public void forEach()
    {
        super.forEach();

        List<Integer> result = new ArrayList<>();
        MutableList<Integer> collection = this.newWith(1, 2, 3, 4);
        collection.forEach(CollectionAddProcedure.on(result));
        Verify.assertSize(4, result);
        Verify.assertContainsAll(result, 1, 2, 3, 4);
    }

    @Override
    @Test
    public void forEachFromTo()
    {
        super.forEachFromTo();

        MutableList<Integer> result = Lists.mutable.of();
        MutableList<Integer> collection = this.newWith(1, 2, 3, 4);
        collection.forEach(2, 3, result::add);
        Verify.assertSize(2, result);
        Verify.assertContainsAll(result, 3, 4);
    }

    @Override
    @Test
    public void forEachWithIndex()
    {
        super.forEachWithIndex();

        List<Integer> result = new ArrayList<>();
        MutableList<Integer> collection = this.newWith(1, 2, 3, 4);
        collection.forEachWithIndex((object, index) -> result.add(object + index));
        Verify.assertContainsAll(result, 1, 3, 5, 7);
    }

    @Test
    public void add()
    {
        MutableList<String> collection = ArrayAdapter.newArray();
        Verify.assertThrows(UnsupportedOperationException.class, () -> collection.add(null));
    }

    @Override
    @Test
    public void allSatisfy()
    {
        super.allSatisfy();

        Assertions.assertTrue(this.newWith(1, 2, 3).allSatisfy(Integer.class::isInstance));
        Assertions.assertFalse(this.newWith(1, 2, 3).allSatisfy(Integer.valueOf(1)::equals));
    }

    @Override
    @Test
    public void anySatisfy()
    {
        super.anySatisfy();

        Assertions.assertFalse(this.newWith(1, 2, 3).anySatisfy(String.class::isInstance));
        Assertions.assertTrue(this.newWith(1, 2, 3).anySatisfy(Integer.class::isInstance));
    }

    @Override
    @Test
    public void noneSatisfy()
    {
        super.noneSatisfy();

        Assertions.assertTrue(this.newWith(1, 2, 3).noneSatisfy(String.class::isInstance));
        Assertions.assertFalse(this.newWith(1, 2, 3).noneSatisfy(Integer.valueOf(1)::equals));
    }

    @Override
    @Test
    public void count()
    {
        super.count();

        Assertions.assertEquals(3, this.newWith(1, 2, 3).count(Integer.class::isInstance));
    }

    @Override
    @Test
    public void collectIf()
    {
        super.collectIf();

        Verify.assertContainsAll(this.newWith(1, 2, 3).collectIf(
                Integer.class::isInstance,
                String::valueOf), "1", "2", "3");
        Verify.assertContainsAll(this.newWith(1, 2, 3).collectIf(
                Integer.class::isInstance,
                String::valueOf,
                new ArrayList<>()), "1", "2", "3");
    }

    @Override
    @Test
    public void getFirst()
    {
        super.getFirst();

        Assertions.assertEquals(Integer.valueOf(1), this.newWith(1, 2, 3).getFirst());
        Assertions.assertNotEquals(Integer.valueOf(3), this.newWith(1, 2, 3).getFirst());
    }

    @Override
    @Test
    public void getLast()
    {
        super.getLast();

        Assertions.assertNotEquals(Integer.valueOf(1), this.newWith(1, 2, 3).getLast());
        Assertions.assertEquals(Integer.valueOf(3), this.newWith(1, 2, 3).getLast());
    }

    @Override
    @Test
    public void isEmpty()
    {
        super.isEmpty();

        Verify.assertEmpty(this.newArray());
        Verify.assertNotEmpty(this.newWith(1, 2));
        Assertions.assertTrue(this.newWith(1, 2).notEmpty());
    }

    @Override
    @Test
    public void iterator()
    {
        super.iterator();

        MutableList<Integer> objects = this.newWith(1, 2, 3);
        Iterator<Integer> iterator = objects.iterator();
        for (int i = objects.size(); i-- > 0; )
        {
            Integer integer = iterator.next();
            Assertions.assertEquals(3, integer.intValue() + i);
        }
    }

    @Override
    @Test
    public void injectInto()
    {
        super.injectInto();

        MutableList<Integer> objects = this.newWith(1, 2, 3);
        Integer result = objects.injectInto(1, AddFunction.INTEGER);
        Assertions.assertEquals(Integer.valueOf(7), result);
    }

    @Override
    @Test
    public void toArray()
    {
        super.toArray();

        MutableList<Integer> objects = this.newWith(1, 2, 3);
        Object[] array = objects.toArray();
        Verify.assertSize(3, array);
        Integer[] array2 = objects.toArray(new Integer[3]);
        Verify.assertSize(3, array2);
        Integer[] array3 = objects.toArray(new Integer[1]);
        Verify.assertSize(3, array3);
        Integer[] expected = {1, 2, 3};
        Assertions.assertArrayEquals(expected, array);
        Assertions.assertArrayEquals(expected, array2);
        Assertions.assertArrayEquals(expected, array3);
    }

    @Override
    @Test
    public void selectAndRejectWith()
    {
        super.selectAndRejectWith();

        MutableList<Integer> objects = this.newWith(1, 2);
        Twin<MutableList<Integer>> result = objects.selectAndRejectWith(Object::equals, 1);
        Verify.assertSize(1, result.getOne());
        Verify.assertSize(1, result.getTwo());
    }

    @Override
    @Test
    public void removeIf()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ArrayAdapter.newArrayWith(1, 2, 3, null).removeIf(Predicates.isNull()));
    }

    @Override
    @Test
    public void removeIndex()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ArrayAdapter.newArrayWith(1, 2, 3, null).remove(0));
    }

    @Override
    @Test
    public void removeIfWith()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> ArrayAdapter.newArrayWith(1, 2, 3, null).removeIfWith((each, ignored) -> each == null, null));
    }

    @Override
    @Test
    public void indexOf()
    {
        super.indexOf();

        MutableList<Integer> objects = ArrayAdapter.newArrayWith(1, 2, 3);
        Assertions.assertEquals(1, objects.indexOf(2));
    }

    @Override
    @Test
    public void lastIndexOf()
    {
        super.lastIndexOf();

        MutableList<Integer> objects = ArrayAdapter.newArrayWith(1, 2, 3);
        Assertions.assertEquals(1, objects.lastIndexOf(2));
    }

    @Override
    @Test
    public void set()
    {
        super.set();

        MutableList<Integer> objects = ArrayAdapter.newArrayWith(1, 2, 3);
        Assertions.assertEquals(Integer.valueOf(2), objects.set(1, 4));
        Assertions.assertEquals(FastList.newListWith(1, 4, 3), objects);
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        super.equalsAndHashCode();

        ArrayAdapter<Integer> array1 = ArrayAdapter.newArrayWith(1, 2, 3, 4);
        ArrayAdapter<Integer> array2 = ArrayAdapter.newArrayWith(1, 2, 3, 4);
        ArrayAdapter<Integer> array3 = ArrayAdapter.newArrayWith(2, 3, 4);
        ArrayAdapter<Integer> array4 = ArrayAdapter.newArrayWith(1, 2, 3, 5);
        Assertions.assertNotEquals(array1, null);
        Verify.assertEqualsAndHashCode(array1, array1);
        Verify.assertEqualsAndHashCode(array1, array2);
        Assertions.assertNotEquals(array2, array3);
        Verify.assertEqualsAndHashCode(array1, new ArrayList<>(array1));
        Verify.assertEqualsAndHashCode(array1, new LinkedList<>(array1));
        Verify.assertEqualsAndHashCode(array1, ArrayListAdapter.<Integer>newList().with(1, 2, 3, 4));
        Verify.assertEqualsAndHashCode(array1, FastList.<Integer>newList().with(1, 2, 3, 4));
        Assertions.assertNotEquals(array1, new LinkedList<>(array4));
    }

    @Override
    @Test
    public void forEachWith()
    {
        super.forEachWith();

        List<Integer> result = new ArrayList<>();
        MutableList<Integer> collection = ArrayAdapter.newArrayWith(1, 2, 3, 4);
        collection.forEachWith((argument1, argument2) -> result.add(argument1 + argument2), 0);
        Verify.assertSize(4, result);
        Verify.assertContainsAll(result, 1, 2, 3, 4);
    }

    @Override
    @Test
    public void selectWith()
    {
        super.selectWith();

        Verify.assertContainsAll(ArrayAdapter.newArrayWith(1, 2, 3, 4, 5).selectWith(Predicates2.<Integer>lessThan(),
                3), 1, 2);
        Verify.denyContainsAny(ArrayAdapter.newArrayWith(-1, 2, 3, 4, 5).selectWith(Predicates2.<Integer>lessThan(),
                3), 3, 4, 5);
        Verify.assertContainsAll(ArrayAdapter.newArrayWith(1, 2, 3, 4, 5).selectWith(Predicates2.<Integer>lessThan(),
                3,
                UnifiedSet.<Integer>newSet()), 1, 2);
    }

    @Override
    @Test
    public void rejectWith()
    {
        super.rejectWith();

        Verify.assertContainsAll(ArrayAdapter.newArrayWith(1, 2, 3, 4).rejectWith(Predicates2.<Integer>lessThan(), 3),
                3,
                4);
        Verify.assertContainsAll(ArrayAdapter.newArrayWith(1, 2, 3, 4).rejectWith(Predicates2.<Integer>lessThan(),
                3,
                UnifiedSet.<Integer>newSet()), 3, 4);
    }

    @Override
    @Test
    public void detectWith()
    {
        Assertions.assertEquals(Integer.valueOf(3),
                ArrayAdapter.newArrayWith(1, 2, 3, 4, 5).detectWith(Object::equals, 3));
        Assertions.assertNull(ArrayAdapter.newArrayWith(1, 2, 3, 4, 5).detectWith(Object::equals, 6));
    }

    @Test
    public void detectWithIfNone()
    {
        MutableList<Integer> list = ArrayAdapter.newArrayWith(1, 2, 3, 4, 5);
        Assertions.assertNull(list.detectWithIfNone(Object::equals, 6, new PassThruFunction0<>(null)));
        Assertions.assertEquals(Integer.valueOf(10000), list.detectWithIfNone(Object::equals, 6, new PassThruFunction0<>(Integer.valueOf(10000))));
    }

    @Override
    @Test
    public void allSatisfyWith()
    {
        super.allSatisfyWith();

        Assertions.assertTrue(ArrayAdapter.newArrayWith(1, 2, 3).allSatisfyWith(Predicates2.instanceOf(),
                Integer.class));
        Assertions.assertFalse(ArrayAdapter.newArrayWith(1, 2, 3).allSatisfyWith(Object::equals, 1));
    }

    @Override
    @Test
    public void anySatisfyWith()
    {
        super.anySatisfyWith();
        Assertions.assertFalse(ArrayAdapter.newArrayWith(1, 2, 3).anySatisfyWith(Predicates2.instanceOf(),
                String.class));
        Assertions.assertTrue(ArrayAdapter.newArrayWith(1, 2, 3).anySatisfyWith(Predicates2.instanceOf(),
                Integer.class));
    }

    @Override
    @Test
    public void noneSatisfyWith()
    {
        super.noneSatisfyWith();

        Assertions.assertTrue(ArrayAdapter.newArrayWith(1, 2, 3).noneSatisfyWith(Predicates2.instanceOf(),
                String.class));
        Assertions.assertFalse(ArrayAdapter.newArrayWith(1, 2, 3).noneSatisfyWith(Object::equals, 1));
    }

    @Override
    @Test
    public void countWith()
    {
        super.countWith();

        Assertions.assertEquals(
                3,
                ArrayAdapter.newArrayWith(1, 2, 3).countWith(Predicates2.instanceOf(), Integer.class));
    }

    @Override
    @Test
    public void collectWith()
    {
        super.collectWith();

        Function2<Integer, Integer, Integer> addBlock = (each, parameter) -> each + parameter;
        Assertions.assertEquals(
                FastList.newListWith(2, 3, 4),
                ArrayAdapter.newArrayWith(1, 2, 3).collectWith(addBlock, 1));
        Assertions.assertEquals(
                FastList.newListWith(2, 3, 4),
                ArrayAdapter.newArrayWith(1, 2, 3).collectWith(addBlock, 1, FastList.<Integer>newList()));
    }

    @Override
    @Test
    public void injectIntoWith()
    {
        super.injectIntoWith();

        MutableList<Integer> objects = ArrayAdapter.newArrayWith(1, 2, 3);
        Integer result = objects.injectIntoWith(1, (injectedValued, item, parameter) -> injectedValued + item + parameter, 0);
        Assertions.assertEquals(Integer.valueOf(7), result);
    }

    @Override
    @Test
    public void serialization()
    {
        super.serialization();

        MutableList<Integer> collection = ArrayAdapter.newArrayWith(1, 2, 3, 4, 5);
        MutableList<Integer> deserializedCollection = SerializeTestHelper.serializeDeserialize(collection);
        Verify.assertSize(5, deserializedCollection);
        Verify.assertStartsWith(deserializedCollection, 1, 2, 3, 4, 5);
        Verify.assertListsEqual(collection, deserializedCollection);
    }

    @Test
    public void testBAOSSize()
    {
        MutableList<Integer> mutableArrayList = ArrayAdapter.newArray();

        List<Integer> arrayList = new ArrayList<>();
        ByteArrayOutputStream stream2 = SerializeTestHelper.getByteArrayOutputStream(arrayList);
        LOGGER.info("ArrayList size: {}", stream2.size());
        LOGGER.info("{}", stream2);

        ByteArrayOutputStream stream1 = SerializeTestHelper.getByteArrayOutputStream(mutableArrayList);
        LOGGER.info("ArrayAdapter size: {}", stream1.size());
        LOGGER.info("{}", stream1);
    }

    @Override
    @Test
    public void testToString()
    {
        // ArrayAdapter doesn't support add and cannot contain itself

        Assertions.assertEquals(
                FastList.newList(this.newWith(1, 2, 3, 4)).toString(),
                this.newWith(1, 2, 3, 4).toString());
    }

    @Override
    @Test
    public void makeString()
    {
        // ArrayAdapter doesn't support add and cannot contain itself

        Assertions.assertEquals(
                FastList.newList(this.newWith(1, 2, 3, 4)).makeString(),
                this.newWith(1, 2, 3, 4).makeString());
    }

    @Override
    @Test
    public void appendString()
    {
        // ArrayAdapter doesn't support add and cannot contain itself

        StringBuilder stringBuilder = new StringBuilder();
        this.newWith(1, 2, 3, 4).appendString(stringBuilder);

        Assertions.assertEquals(
                FastList.newList(this.newWith(1, 2, 3, 4)).makeString(),
                stringBuilder.toString());
    }


    @Override
    @Test
    public void asUnmodifiable()
    {
        super.asUnmodifiable();

        Verify.assertInstanceOf(UnmodifiableMutableList.class, this.newWith().asUnmodifiable());
    }

    @Override
    @Test
    public void clear()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.newArray().clear();
        });
    }

    @Override
    @Test
    public void addAtIndex()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.newArray().add(0, null);
        });
    }

    @Override
    @Test
    public void addAllAtIndex()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.newArray().addAll(0, FastList.<Integer>newList());
        });
    }

    @Override
    @Test
    public void addAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.newArray().addAll(FastList.<Integer>newList());
        });
    }

    @Override
    @Test
    public void addAllIterable()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.newArray().addAllIterable(FastList.<Integer>newList());
        });
    }

    @Override
    @Test
    public void removeObject()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.newArray().remove(null);
        });
    }

    @Override
    @Test
    public void removeAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.newArray().removeAll(FastList.newList());
        });
    }

    @Override
    @Test
    public void removeAllIterable()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.newArray().removeAllIterable(FastList.newList());
        });
    }

    @Override
    @Test
    public void retainAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.newArray().retainAll(FastList.newList());
        });
    }

    @Override
    @Test
    public void retainAllIterable()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.newArray().retainAllIterable(FastList.newList());
        });
    }

    @Override
    @Test
    public void forEachOnRange()
    {
        MutableList<Integer> list = this.newWith(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        this.validateForEachOnRange(list, 0, 0, FastList.newListWith(0));
        this.validateForEachOnRange(list, 3, 5, FastList.newListWith(3, 4, 5));
        this.validateForEachOnRange(list, 4, 6, FastList.newListWith(4, 5, 6));
        this.validateForEachOnRange(list, 9, 9, FastList.newListWith(9));
        this.validateForEachOnRange(list, 0, 9, FastList.newListWith(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));

        Verify.assertThrows(IndexOutOfBoundsException.class, () -> this.validateForEachOnRange(list, 10, 10, FastList.<Integer>newList()));
    }

    @Override
    @Test
    public void forEachWithIndexOnRange()
    {
        MutableList<Integer> list = this.newWith(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

        this.validateForEachWithIndexOnRange(list, 0, 0, FastList.newListWith(0));
        this.validateForEachWithIndexOnRange(list, 3, 5, FastList.newListWith(3, 4, 5));
        this.validateForEachWithIndexOnRange(list, 4, 6, FastList.newListWith(4, 5, 6));
        this.validateForEachWithIndexOnRange(list, 9, 9, FastList.newListWith(9));
        this.validateForEachWithIndexOnRange(list, 0, 9, FastList.newListWith(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> this.validateForEachWithIndexOnRange(list, 10, 10, FastList.<Integer>newList()));
    }

    @Override
    @Test
    public void subList()
    {
        MutableList<String> list = this.newWith("A", "B", "C", "D");
        MutableList<String> sublist = list.subList(1, 3);
        Verify.assertSize(2, sublist);
        Verify.assertContainsAll(sublist, "B", "C");
    }

    @Override
    @Test
    public void with()
    {
        MutableCollection<Integer> coll = this.newWith(1, 2, 3);
        MutableCollection<Integer> collWith = coll.with(4);
        Assertions.assertNotSame(coll, collWith);
        Assertions.assertEquals(FastList.newListWith(1, 2, 3, 4), collWith);
    }

    @Override
    @Test
    public void withAll()
    {
        MutableCollection<Integer> coll = this.newWith(1, 2, 3);
        MutableCollection<Integer> collWith = coll.withAll(FastList.newListWith(4, 5));
        Assertions.assertNotSame(coll, collWith);
        Assertions.assertEquals(FastList.newListWith(1, 2, 3, 4, 5), collWith);
        Assertions.assertSame(collWith, collWith.withAll(FastList.<Integer>newList()));
    }

    @Override
    @Test
    public void without()
    {
        MutableCollection<Integer> coll = this.newWith(1, 2, 3, 2);
        MutableCollection<Integer> collWithout = coll.without(2);
        Assertions.assertNotSame(coll, collWithout);
        Assertions.assertEquals(FastList.newListWith(1, 3, 2), collWithout);
        Assertions.assertSame(collWithout, collWithout.without(9));
    }

    @Override
    @Test
    public void withoutAll()
    {
        MutableCollection<Integer> coll = this.newWith(1, 2, 4, 2, 3, 4, 5);
        MutableCollection<Integer> collWithout = coll.withoutAll(FastList.newListWith(2, 4));
        Assertions.assertNotSame(coll, collWithout);
        Assertions.assertEquals(FastList.newListWith(1, 3, 5), collWithout);
        Assertions.assertSame(collWithout, collWithout.withoutAll(FastList.newListWith(8, 9)));
        Assertions.assertSame(collWithout, collWithout.withoutAll(FastList.<Integer>newList()));
    }
}
