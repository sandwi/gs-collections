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

package com.gs.collections.impl.list.mutable;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import com.gs.collections.api.LazyIterable;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.block.predicate.Predicate2;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.tuple.Twin;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.factory.Procedures;
import com.gs.collections.impl.block.function.AddFunction;
import com.gs.collections.impl.block.function.MaxSizeFunction;
import com.gs.collections.impl.block.function.MinSizeFunction;
import com.gs.collections.impl.block.function.PassThruFunction0;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.block.procedure.CountProcedure;
import com.gs.collections.impl.factory.Bags;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.factory.Sets;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.fixed.ArrayAdapter;
import com.gs.collections.impl.math.IntegerSum;
import com.gs.collections.impl.math.Sum;
import com.gs.collections.impl.math.SumProcedure;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.SerializeTestHelper;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.Tuples;
import com.gs.collections.impl.utility.LazyIterate;
import com.gs.collections.impl.utility.ListIterate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.gs.collections.impl.factory.Iterables.iList;
import static com.gs.collections.impl.factory.Iterables.mList;
import static com.gs.collections.impl.factory.Iterables.mSet;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JUnit test for {@link FastList}.
 */
public class FastListTest extends AbstractListTestCase
{
    @Override
    protected <T> FastList<T> newWith(T... littleElements)
    {
        return FastList.newListWith(littleElements);
    }

    @Override
    @Test
    public void newEmpty()
    {
        Verify.assertInstanceOf(FastList.class, FastList.newList().newEmpty());
    }

    @Test
    public void withNValues()
    {
        Assertions.assertEquals(FastList.newListWith(1, 1, 1, 1, 1), FastList.newWithNValues(5, () -> 1));
        Assertions.assertEquals(FastList.newListWith(null, null, null, null, null), FastList.newWithNValues(5, () -> null));
        Assertions.assertEquals(
                FastList.newListWith(
                        Lists.mutable.with(),
                        Lists.mutable.with(),
                        Lists.mutable.with(),
                        Lists.mutable.with(),
                        Lists.mutable.with()),
                FastList.newWithNValues(5, FastList::new));
    }

    @Test
    public void constructorWithCollection()
    {
        List<Integer> expected = new ArrayList<>(Interval.oneTo(20));
        FastList<Integer> actual = new FastList<>(expected);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testFastListNewWithContainsAllItems()
    {
        Assertions.assertEquals(
                Bags.mutable.of("Alice", "Bob", "Cooper", "Dio"),
                this.newWith("Alice", "Bob", "Cooper", "Dio").toBag());
    }

    @Test
    public void testAddWithZeroBasedConstructor()
    {
        MutableList<String> strings = FastList.newList(0);
        Assertions.assertEquals(new ArrayList<String>(0), strings);
        strings.add("1");
        Assertions.assertEquals(this.newWith("1"), strings);
    }

    @Test
    public void testWrapCopy()
    {
        Assertions.assertEquals(this.newWith(1, 2, 3, 4), FastList.wrapCopy(1, 2, 3, 4));
    }

    @Override
    @Test
    public void forEach()
    {
        MutableList<Integer> result = FastList.newList();
        MutableList<Integer> collection = FastList.newListWith(1, 2, 3, 4);
        collection.forEach(CollectionAddProcedure.on(result));
        Assertions.assertEquals(this.newWith(1, 2, 3, 4), result);
    }

    @Override
    @Test
    public void injectInto()
    {
        FastList<Integer> list = this.newWith(1, 2, 3);
        Assertions.assertEquals(Integer.valueOf(1 + 1 + 2 + 3), list.injectInto(1, AddFunction.INTEGER));
    }

    @Test
    public void testInjectIntoDouble()
    {
        FastList<Double> list = this.newWith(1.0, 2.0, 3.0);
        Assertions.assertEquals(Double.valueOf(1.0 + 1.0 + 2.0 + 3.0), list.injectInto(Double.valueOf(1.0d), AddFunction.DOUBLE));
    }

    @Test
    public void testInjectIntoFloat()
    {
        FastList<Float> list = this.newWith(1.0f, 2.0f, 3.0f);
        Assertions.assertEquals(Float.valueOf(7.0f), list.injectInto(Float.valueOf(1.0f), AddFunction.FLOAT));
    }

    @Test
    public void testInjectIntoString()
    {
        FastList<String> list = FastList.<String>newList().with("1", "2", "3");
        Assertions.assertEquals("0123", list.injectInto("0", AddFunction.STRING));
    }

    @Test
    public void testInjectIntoMaxString()
    {
        FastList<String> list = FastList.<String>newList().with("1", "12", "123");
        Assertions.assertEquals(Integer.valueOf(3), list.injectInto(Integer.MIN_VALUE, MaxSizeFunction.STRING));
    }

    @Test
    public void testInjectIntoMinString()
    {
        FastList<String> list = FastList.<String>newList().with("1", "12", "123");
        Assertions.assertEquals(Integer.valueOf(1), list.injectInto(Integer.MAX_VALUE, MinSizeFunction.STRING));
    }

    @Override
    @Test
    public void collect()
    {
        FastList<Boolean> list = this.newWith(Boolean.TRUE, Boolean.FALSE, null);
        MutableList<String> newCollection = list.collect(String::valueOf);
        Assertions.assertEquals(this.newWith("true", "false", "null"), newCollection);
    }

    @Override
    @Test
    public void forEachWithIndex()
    {
        super.forEachWithIndex();

        MutableList<Integer> list = FastList.newList(Interval.oneTo(5));
        list.forEachWithIndex((object, index) -> Assertions.assertEquals(index, object - 1));
    }

    @Test
    public void testForEachInBoth()
    {
        MutableList<Twin<String>> list = FastList.newList();
        MutableList<String> list1 = this.newWith("1", "2");
        MutableList<String> list2 = this.newWith("a", "b");
        ListIterate.forEachInBoth(list1, list2, (argument1, argument2) -> list.add(Tuples.twin(argument1, argument2)));
        Assertions.assertEquals(this.newWith(Tuples.twin("1", "a"), Tuples.twin("2", "b")), list);
    }

    @Override
    @Test
    public void detect()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        Assertions.assertEquals(Integer.valueOf(1), list.detect(Integer.valueOf(1)::equals));
        FastList<Integer> list2 = FastList.newListWith(1, 2, 2);
        Assertions.assertSame(list2.get(1), list2.detect(Integer.valueOf(2)::equals));
    }

    @Override
    @Test
    public void detectWith()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        Assertions.assertEquals(Integer.valueOf(1), list.detectWith(Object::equals, 1));
        FastList<Integer> list2 = FastList.newListWith(1, 2, 2);
        Assertions.assertSame(list2.get(1), list2.detectWith(Object::equals, 2));
    }

    @Test
    public void testDetectWithIfNone()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        Assertions.assertNull(list.detectWithIfNone(Object::equals, 6, new PassThruFunction0<>(null)));
    }

    @Override
    @Test
    public void select()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        MutableList<Integer> results = list.select(Integer.class::isInstance);
        Verify.assertSize(5, results);
    }

    @Override
    @Test
    public void selectWith()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        MutableList<Integer> results = list.selectWith(Predicates2.instanceOf(), Integer.class);
        Verify.assertSize(5, results);
    }

    @Override
    @Test
    public void rejectWith()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        MutableList<Integer> results = list.rejectWith(Predicates2.instanceOf(), Integer.class);
        Verify.assertEmpty(results);
    }

    @Override
    @Test
    public void selectAndRejectWith()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        Twin<MutableList<Integer>> result =
                list.selectAndRejectWith(Predicates2.in(), Lists.fixedSize.of(1));
        Verify.assertSize(1, result.getOne());
        Verify.assertSize(4, result.getTwo());
    }

    @Override
    @Test
    public void anySatisfyWith()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        Assertions.assertTrue(list.anySatisfyWith(Predicates2.instanceOf(), Integer.class));
        Assertions.assertFalse(list.anySatisfyWith(Predicates2.instanceOf(), Double.class));
    }

    @Override
    @Test
    public void anySatisfy()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        Assertions.assertTrue(Predicates.<Integer>anySatisfy(Integer.class::isInstance).accept(list));
        Assertions.assertFalse(Predicates.<Integer>anySatisfy(Double.class::isInstance).accept(list));
    }

    @Override
    @Test
    public void allSatisfyWith()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        Assertions.assertTrue(list.allSatisfyWith(Predicates2.instanceOf(), Integer.class));
        Predicate2<Integer, Integer> greaterThanPredicate = Predicates2.greaterThan();
        Assertions.assertFalse(list.allSatisfyWith(greaterThanPredicate, 2));
    }

    @Override
    @Test
    public void allSatisfy()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        Assertions.assertTrue(Predicates.<Integer>allSatisfy(Integer.class::isInstance).accept(list));
        Assertions.assertFalse(Predicates.allSatisfy(Predicates.greaterThan(2)).accept(list));
    }

    @Override
    @Test
    public void noneSatisfy()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        Assertions.assertTrue(Predicates.<Integer>noneSatisfy(String.class::isInstance).accept(list));
        Assertions.assertFalse(Predicates.noneSatisfy(Predicates.greaterThan(0)).accept(list));
    }

    @Override
    @Test
    public void noneSatisfyWith()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        Assertions.assertTrue(list.noneSatisfyWith(Predicates2.instanceOf(), String.class));
        Predicate2<Integer, Integer> greaterThanPredicate = Predicates2.greaterThan();
        Assertions.assertFalse(list.noneSatisfyWith(greaterThanPredicate, 0));
    }

    @Override
    @Test
    public void count()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        Assertions.assertEquals(5, list.count(Integer.class::isInstance));
        Assertions.assertEquals(0, list.count(Double.class::isInstance));
    }

    @Override
    @Test
    public void countWith()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        Assertions.assertEquals(5, list.countWith(Predicates2.instanceOf(), Integer.class));
        Assertions.assertEquals(0, list.countWith(Predicates2.instanceOf(), Double.class));
    }

    @Override
    @Test
    public void detectIfNone()
    {
        Function0<Integer> defaultResultFunction = new PassThruFunction0<>(6);
        Assertions.assertEquals(
                Integer.valueOf(3),
                FastList.newListWith(1, 2, 3, 4, 5).detectIfNone(Integer.valueOf(3)::equals, defaultResultFunction));
        Assertions.assertEquals(
                Integer.valueOf(6),
                FastList.newListWith(1, 2, 3, 4, 5).detectIfNone(Integer.valueOf(6)::equals, defaultResultFunction));
    }

    @Override
    @Test
    public void forEachWith()
    {
        MutableList<Integer> result = FastList.newList();
        MutableList<Integer> list = FastList.newListWith(1, 2, 3, 4);
        list.forEachWith((argument1, argument2) -> result.add(argument1 + argument2), 0);
        Verify.assertSize(4, result);
        Verify.assertContainsAll(result, 1, 2, 3, 4);
    }

    @Test
    public void testForEachIf()
    {
        FastList<Integer> collection = FastList.newListWith(1, 2, 3, 4);
        CountProcedure<Integer> countProcedure = new CountProcedure<>(ignored -> true);
        collection.forEachIf(Predicates.lessThan(4), countProcedure);
        Assertions.assertEquals(3, countProcedure.getCount());
    }

    @Override
    @Test
    public void getFirst()
    {
        Assertions.assertNull(FastList.<Integer>newList().getFirst());
        Assertions.assertEquals(Integer.valueOf(1), FastList.newListWith(1, 2, 3).getFirst());
        Assertions.assertNotEquals(Integer.valueOf(3), FastList.newListWith(1, 2, 3).getFirst());
    }

    @Override
    @Test
    public void getLast()
    {
        Assertions.assertNull(FastList.<Integer>newList().getLast());
        Assertions.assertNotEquals(Integer.valueOf(1), FastList.newListWith(1, 2, 3).getLast());
        Assertions.assertEquals(Integer.valueOf(3), FastList.newListWith(1, 2, 3).getLast());
    }

    @Override
    @Test
    public void isEmpty()
    {
        Verify.assertEmpty(FastList.<Integer>newList());
        Verify.assertNotEmpty(FastList.newListWith(1, 2));
        Assertions.assertTrue(FastList.newListWith(1, 2).notEmpty());
    }

    @Override
    @Test
    public void collectIf()
    {
        Verify.assertContainsAll(FastList.newListWith(1, 2, 3).collectIf(
                Integer.class::isInstance,
                String::valueOf), "1", "2", "3");
        Verify.assertContainsAll(FastList.newListWith(1, 2, 3).collectIf(
                Integer.class::isInstance,
                String::valueOf,
                new ArrayList<>()), "1", "2", "3");
    }

    @Override
    @Test
    public void collectWith()
    {
        Assertions.assertEquals(
                FastList.newListWith(2, 3, 4),
                FastList.newListWith(1, 2, 3).collectWith(AddFunction.INTEGER, 1));
        Assertions.assertEquals(
                FastList.newListWith(2, 3, 4),
                FastList.newListWith(1, 2, 3).collectWith(AddFunction.INTEGER, 1, FastList.<Integer>newList()));
    }

    @Override
    @Test
    public void injectIntoWith()
    {
        MutableList<Integer> objects = FastList.newListWith(1, 2, 3);
        Integer result = objects.injectIntoWith(1, (injectedValued, item, parameter) -> injectedValued + item + parameter, 0);
        Assertions.assertEquals(Integer.valueOf(7), result);
    }

    @Test
    public void testRemoveUsingPredicate()
    {
        MutableList<Integer> objects = FastList.newListWith(1, 2, 3, null);
        Assertions.assertTrue(objects.removeIf(Predicates.isNull()));
        Verify.assertSize(3, objects);
        Verify.assertContainsAll(objects, 1, 2, 3);
    }

    @Override
    @Test
    public void removeAll()
    {
        FastList<Integer> objects = FastList.newListWith(1, 2, 3);
        objects.removeAll(mList(1, 2));
        Verify.assertSize(1, objects);
        Verify.assertContains(3, objects);
        FastList<Integer> objects2 = FastList.newListWith(1, 2, 3);
        objects2.removeAll(mList(1));
        Verify.assertSize(2, objects2);
        Verify.assertContainsAll(objects2, 2, 3);
        FastList<Integer> objects3 = FastList.newListWith(1, 2, 3);
        objects3.removeAll(mList(3));
        Verify.assertSize(2, objects3);
        Verify.assertContainsAll(objects3, 1, 2);
        FastList<Integer> objects4 = FastList.newListWith(1, 2, 3);
        objects4.removeAll(mList());
        Verify.assertSize(3, objects4);
        Verify.assertContainsAll(objects4, 1, 2, 3);
        FastList<Integer> objects5 = FastList.newListWith(1, 2, 3);
        objects5.removeAll(mList(1, 2, 3));
        Verify.assertEmpty(objects5);
        FastList<Integer> objects6 = FastList.newListWith(1, 2, 3);
        objects6.removeAll(mList(2));
        Verify.assertSize(2, objects6);
        Verify.assertContainsAll(objects6, 1, 3);
    }

    @Override
    @Test
    public void removeAllIterable()
    {
        FastList<Integer> objects = FastList.newListWith(1, 2, 3);
        objects.removeAllIterable(iList(1, 2));
        Verify.assertSize(1, objects);
        Verify.assertContains(3, objects);
        FastList<Integer> objects2 = FastList.newListWith(1, 2, 3);
        objects2.removeAllIterable(iList(1));
        Verify.assertSize(2, objects2);
        Verify.assertContainsAll(objects2, 2, 3);
        FastList<Integer> objects3 = FastList.newListWith(1, 2, 3);
        objects3.removeAllIterable(iList(3));
        Verify.assertSize(2, objects3);
        Verify.assertContainsAll(objects3, 1, 2);
        FastList<Integer> objects4 = FastList.newListWith(1, 2, 3);
        objects4.removeAllIterable(iList());
        Verify.assertSize(3, objects4);
        Verify.assertContainsAll(objects4, 1, 2, 3);
        FastList<Integer> objects5 = FastList.newListWith(1, 2, 3);
        objects5.removeAllIterable(iList(1, 2, 3));
        Verify.assertEmpty(objects5);
        FastList<Integer> objects6 = FastList.newListWith(1, 2, 3);
        objects6.removeAllIterable(iList(2));
        Verify.assertSize(2, objects6);
        Verify.assertContainsAll(objects6, 1, 3);
    }

    @Test
    public void testRemoveAllWithWeakReference()
    {
        String fred = new String("Fred");    // Deliberate String copy for unit test purpose
        String wilma = new String("Wilma");  // Deliberate String copy for unit test purpose
        FastList<String> objects = FastList.<String>newList().with(fred, wilma);
        objects.removeAll(mList("Fred"));
        objects.remove(0);
        Verify.assertSize(0, objects);
        WeakReference<String> ref = new WeakReference<>(wilma);
        //noinspection ReuseOfLocalVariable
        fred = null;   // Deliberate null of a local variable for unit test purpose
        //noinspection ReuseOfLocalVariable
        wilma = null;  // Deliberate null of a local variable for unit test purpose
        System.gc();
        Thread.yield();
        System.gc();
        Thread.yield();
        Assertions.assertNull(ref.get());
    }

    @Override
    @Test
    public void retainAll()
    {
        super.retainAll();

        MutableList<Integer> objects = this.newWith(1, 2, 3);
        objects.retainAll(mList(1, 2));
        Verify.assertSize(2, objects);
        Verify.assertContainsAll(objects, 1, 2);
        MutableList<Integer> objects2 = this.newWith(1, 2, 3);
        objects2.retainAll(mList(1));
        Verify.assertSize(1, objects2);
        Verify.assertContainsAll(objects2, 1);
        MutableList<Integer> objects3 = this.newWith(1, 2, 3);
        objects3.retainAll(mList(3));
        Verify.assertSize(1, objects3);
        Verify.assertContainsAll(objects3, 3);
        MutableList<Integer> objects4 = this.newWith(1, 2, 3);
        objects4.retainAll(mList(2));
        Verify.assertSize(1, objects4);
        Verify.assertContainsAll(objects4, 2);
        MutableList<Integer> objects5 = this.newWith(1, 2, 3);
        objects5.retainAll(mList());
        Verify.assertEmpty(objects5);
        MutableList<Integer> objects6 = this.newWith(1, 2, 3);
        objects6.retainAll(mList(1, 2, 3));
        Verify.assertSize(3, objects6);
        Verify.assertContainsAll(objects6, 1, 2, 3);
    }

    @Override
    @Test
    public void retainAllIterable()
    {
        super.retainAllIterable();

        MutableList<Integer> objects = this.newWith(1, 2, 3);
        objects.retainAllIterable(iList(1, 2));
        Verify.assertSize(2, objects);
        Verify.assertContainsAll(objects, 1, 2);
        MutableList<Integer> objects2 = this.newWith(1, 2, 3);
        objects2.retainAllIterable(iList(1));
        Verify.assertSize(1, objects2);
        Verify.assertContainsAll(objects2, 1);
        MutableList<Integer> objects3 = this.newWith(1, 2, 3);
        objects3.retainAllIterable(iList(3));
        Verify.assertSize(1, objects3);
        Verify.assertContainsAll(objects3, 3);
        MutableList<Integer> objects4 = this.newWith(1, 2, 3);
        objects4.retainAllIterable(iList(2));
        Verify.assertSize(1, objects4);
        Verify.assertContainsAll(objects4, 2);
        MutableList<Integer> objects5 = this.newWith(1, 2, 3);
        objects5.retainAllIterable(iList());
        Verify.assertEmpty(objects5);
        MutableList<Integer> objects6 = this.newWith(1, 2, 3);
        objects6.retainAllIterable(iList(1, 2, 3));
        Verify.assertSize(3, objects6);
        Verify.assertContainsAll(objects6, 1, 2, 3);
    }

    @Override
    @Test
    public void reject()
    {
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4).reject(Predicates.lessThan(3)), 3, 4);
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4).reject(
                Predicates.lessThan(3),
                UnifiedSet.<Integer>newSet()), 3, 4);
    }

    @Override
    @Test
    public void distinct()
    {
        super.distinct();
        Verify.assertListsEqual(this.newWith(5, 2, 3, 5, 4, 2).distinct(), this.newWith(5, 2, 3, 4));
        Verify.assertListsEqual(Interval.fromTo(1, 5).toList().distinct(), this.newWith(1, 2, 3, 4, 5));
    }

    @Override
    @Test
    public void serialization()
    {
        MutableList<Integer> collection = this.newWith(1, 2, 3, 4, 5);
        MutableList<Integer> deserializedCollection = SerializeTestHelper.serializeDeserialize(collection);
        Verify.assertSize(5, deserializedCollection);
        Verify.assertStartsWith(deserializedCollection, 1, 2, 3, 4, 5);
        Verify.assertListsEqual(collection, deserializedCollection);
    }

    @Test
    public void testSerializationOfEmpty()
    {
        MutableList<Integer> collection = FastList.newList();
        Verify.assertPostSerializedEqualsAndHashCode(collection);
    }

    @Test
    public void testSerializationOfSublist()
    {
        MutableList<Integer> collection = this.newWith(1, 2, 3, 4, 5);
        MutableList<Integer> deserializedCollection = SerializeTestHelper.serializeDeserialize(collection.subList(0, 2));
        Verify.assertSize(2, deserializedCollection);
        Verify.assertStartsWith(deserializedCollection, 1, 2);
        Verify.assertListsEqual(collection.subList(0, 2), deserializedCollection);
    }

    @Test
    public void testSubList()
    {
        MutableList<String> collection = FastList.newListWith("1", "2", "3", "4", "5");
        MutableList<String> subList = collection.subList(1, 3);
        Verify.assertContainsAll(subList, "2", "3");
        subList.add("6");
        Verify.assertItemAtIndex(2, subList, "6");
        Verify.assertSize(6, collection);
        Verify.assertSize(3, subList);
        Verify.assertItemAtIndex(3, collection, "6");
        subList.remove("6");
        Verify.assertSize(5, collection);
        Verify.assertSize(2, subList);
    }

    @Test
    public void testBAOSSize()
    {
        MutableList<MutableList<Object>> mutableArrayList = FastList.<MutableList<Object>>newList()
                .with(
                        FastList.newList(),
                        FastList.newList(),
                        FastList.newList(),
                        FastList.newList())
                .with(
                        FastList.newList(),
                        FastList.newList(),
                        FastList.newList(),
                        FastList.newList());

        List<List<Object>> arrayList = new ArrayList<>();
        Interval.oneTo(8).forEach(Procedures.cast(object -> arrayList.add(new ArrayList<>())));
        ByteArrayOutputStream stream2 = SerializeTestHelper.getByteArrayOutputStream(arrayList);
        Assertions.assertEquals(194L, stream2.size());

        ByteArrayOutputStream stream1 = SerializeTestHelper.getByteArrayOutputStream(mutableArrayList);
        Assertions.assertEquals(177L, stream1.size());
    }

    @Override
    @Test
    public void addAll()
    {
        super.addAll();

        MutableList<Integer> integers1 = FastList.newList();
        Assertions.assertTrue(integers1.addAll(mList(1, 2, 3, 4)));
        Verify.assertListsEqual(FastList.newListWith(1, 2, 3, 4), integers1);
        Assertions.assertTrue(integers1.addAll(FastList.<Integer>newList(4).with(1, 2, 3, 4)));
        Verify.assertListsEqual(FastList.newListWith(1, 2, 3, 4, 1, 2, 3, 4), integers1);
        Assertions.assertTrue(integers1.addAll(mSet(5)));
        Verify.assertListsEqual(FastList.newListWith(1, 2, 3, 4, 1, 2, 3, 4, 5), integers1);

        MutableList<Integer> integers2 = FastList.newListWith(0);
        Assertions.assertTrue(integers2.addAll(mList(1, 2, 3, 4)));
        Verify.assertListsEqual(FastList.newListWith(0, 1, 2, 3, 4), integers2);
        Assertions.assertTrue(integers2.addAll(FastList.<Integer>newList(4).with(1, 2, 3, 4)));
        Verify.assertListsEqual(FastList.newListWith(0, 1, 2, 3, 4, 1, 2, 3, 4), integers2);
        Assertions.assertTrue(integers2.addAll(mSet(5)));
        Verify.assertListsEqual(FastList.newListWith(0, 1, 2, 3, 4, 1, 2, 3, 4, 5), integers2);

        Verify.assertThrows(IndexOutOfBoundsException.class, () -> FastList.newList().addAll(1, null));
    }

    @Override
    @Test
    public void addAllIterable()
    {
        super.addAllIterable();

        FastList<Integer> integers = FastList.newList();
        Assertions.assertTrue(integers.addAllIterable(iList(1, 2, 3, 4)));
        Verify.assertListsEqual(FastList.newListWith(1, 2, 3, 4), integers);
        Assertions.assertTrue(integers.addAllIterable(FastList.<Integer>newList(4).with(1, 2, 3, 4)));
        Verify.assertListsEqual(FastList.newListWith(1, 2, 3, 4, 1, 2, 3, 4), integers);
        Assertions.assertTrue(integers.addAllIterable(mSet(5)));
        Verify.assertListsEqual(FastList.newListWith(1, 2, 3, 4, 1, 2, 3, 4, 5), integers);
    }

    @Test
    public void testAddAllEmpty()
    {
        FastList<Integer> integers = FastList.newList();
        integers.addAll(Lists.mutable.<Integer>of());
        Verify.assertEmpty(integers);
        integers.addAll(Sets.mutable.<Integer>of());
        Verify.assertEmpty(integers);
        integers.addAll(FastList.<Integer>newList());
        Verify.assertEmpty(integers);
        integers.addAll(ArrayAdapter.<Integer>newArray());
        Verify.assertEmpty(integers);
    }

    @Test
    public void addAllWithMultipleTypes()
    {
        FastList<Integer> list = FastList.newList();
        list.addAll(mList(1, 2, 3, 4));
        list.addAll(mSet(5, 6));
        list.addAll(new ArrayList<>(mList(7, 8)));
        list.addAll(this.newWith(9, 10));
        Assertions.assertFalse(list.addAll(Lists.mutable.<Integer>of()));
        Assertions.assertEquals(this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), list);
    }

    @Override
    @Test
    public void addAllAtIndex()
    {
        super.addAllAtIndex();
        FastList<Integer> integers = this.newWith(5);
        integers.addAll(0, mList(1, 2, 3, 4));
        Verify.assertStartsWith(integers, 1, 2, 3, 4, 5);
        integers.addAll(0, FastList.<Integer>newList(4).with(-3, -2, -1, 0));
        Verify.assertStartsWith(integers, -3, -2, -1, 0, 1, 2, 3, 4, 5);
    }

    @Test
    public void testAddAllAtIndexEmpty()
    {
        FastList<Integer> integers = this.newWith(5);
        integers.addAll(0, Lists.mutable.<Integer>of());
        Verify.assertSize(1, integers);
        Verify.assertStartsWith(integers, 5);
        integers.addAll(0, FastList.<Integer>newList(4));
        Verify.assertSize(1, integers);
        Verify.assertStartsWith(integers, 5);
        integers.addAll(0, Sets.mutable.<Integer>of());
        Verify.assertSize(1, integers);
        Verify.assertStartsWith(integers, 5);
        FastList<String> zeroSizedList = FastList.newList(0);
        zeroSizedList.addAll(0, this.newWith("1", "2"));
    }

    @Override
    @Test
    public void addAtIndex()
    {
        FastList<Integer> integers = this.newWith(1, 2, 3, 5);
        integers.add(3, 4);
        Verify.assertStartsWith(integers, 1, 2, 3, 4, 5);
        integers.add(5, 6);
        Verify.assertStartsWith(integers, 1, 2, 3, 4, 5, 6);
        integers.add(0, 0);
        Verify.assertStartsWith(integers, 0, 1, 2, 3, 4, 5, 6);
        FastList<String> zeroSizedList = FastList.newList(0);
        zeroSizedList.add(0, "1");
        Verify.assertStartsWith(zeroSizedList, "1");
        zeroSizedList.add(1, "3");
        Verify.assertStartsWith(zeroSizedList, "1", "3");
        zeroSizedList.add(1, "2");
        Verify.assertStartsWith(zeroSizedList, "1", "2", "3");
        FastList<Integer> midList = FastList.<Integer>newList(2).with(1, 3);
        midList.add(1, 2);
        Verify.assertStartsWith(midList, 1, 2, 3);
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> midList.add(-1, -1));
    }

    @Test
    public void testSubListSort()
    {
        FastList<Integer> list = (FastList<Integer>) Interval.from(0).to(20).toList();
        MutableList<Integer> sorted = list.subList(2, 18).sortThis();
        Verify.assertListsEqual(sorted, Interval.from(2).to(17));
    }

    @Test
    public void testSubListOfSubList()
    {
        MutableList<String> list = this.newWith("A", "B", "C", "D");
        MutableList<String> sublist = list.subList(0, 3);
        MutableList<String> sublist2 = sublist.subList(0, 2);
        Verify.assertSize(2, sublist2);
        Verify.assertContainsAll(sublist, "A", "B");
        sublist2.add("X");
        Verify.assertSize(3, sublist2);
        Verify.assertStartsWith(sublist2, "A", "B", "X");
        Verify.assertContainsAll(sublist, "A", "B", "C", "X");
        Assertions.assertEquals("X", sublist2.remove(2));
        Verify.assertSize(2, sublist2);
        Verify.assertContainsNone(sublist, "X");
        Verify.assertContainsNone(sublist2, "X");
    }

    @Test
    public void testSubListListIterator()
    {
        MutableList<String> list = this.newWith("A", "B", "C", "D");
        MutableList<String> subList = list.subList(0, 3);
        ListIterator<String> iterator = subList.listIterator();
        MutableList<String> newList = FastList.newList();
        while (iterator.hasNext())
        {
            newList.add(iterator.next());
        }
        Verify.assertSize(3, newList);
        while (iterator.hasPrevious())
        {
            newList.remove(iterator.previous());
        }
        Verify.assertEmpty(newList);
        iterator.add("X");
        Verify.assertSize(4, subList);
    }

    @Test
    public void testSetAtIndex()
    {
        FastList<Integer> integers = this.newWith(1, 2, 3, 5);
        Assertions.assertEquals(Integer.valueOf(5), integers.set(3, 4));
        Verify.assertStartsWith(integers, 1, 2, 3, 4);
    }

    @Override
    @Test
    public void indexOf()
    {
        FastList<Integer> integers = this.newWith(1, 2, 3, 4);
        Assertions.assertEquals(2, integers.indexOf(3));
        Assertions.assertEquals(-1, integers.indexOf(0));
        Assertions.assertEquals(-1, integers.indexOf(null));
        FastList<Integer> integers2 = FastList.<Integer>newList(4).with(null, 2, 3, 4);
        Assertions.assertEquals(0, integers2.indexOf(null));
    }

    @Override
    @Test
    public void lastIndexOf()
    {
        FastList<Integer> integers = FastList.<Integer>newList(4).with(1, 2, 3, 4);
        Assertions.assertEquals(2, integers.lastIndexOf(3));
        Assertions.assertEquals(-1, integers.lastIndexOf(0));
        Assertions.assertEquals(-1, integers.lastIndexOf(null));
        FastList<Integer> integers2 = FastList.<Integer>newList(4).with(null, 2, 3, 4);
        Assertions.assertEquals(0, integers2.lastIndexOf(null));
    }

    @Test
    public void testOutOfBoundsCondition()
    {
        MutableList<Integer> integers = this.newWith(1, 2, 3, 4);
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> integers.get(4));
    }

    @Override
    @Test
    public void clear()
    {
        MutableList<Integer> integers = this.newWith(1, 2, 3, 4);
        Verify.assertNotEmpty(integers);
        integers.clear();
        Verify.assertEmpty(integers);
    }

    @Override
    @Test
    public void testClone()
    {
        MutableList<Integer> integers = this.newWith(1, 2, 3, 4);
        MutableList<Integer> clone = integers.clone();
        Verify.assertListsEqual(integers, clone);
        Verify.assertInstanceOf(FastList.class, clone);
        Assertions.assertEquals(FastList.newList(), FastList.newList().clone());
        Assertions.assertEquals(FastList.newList(0), FastList.newList().clone());
    }

    @Override
    @Test
    public void toArray()
    {
        Object[] typelessArray = this.newWith(1, 2, 3, 4).toArray();
        Assertions.assertArrayEquals(typelessArray, new Object[]{1, 2, 3, 4});
        Integer[] typedArray = this.newWith(1, 2, 3, 4).toArray(new Integer[0]);
        Assertions.assertArrayEquals(typedArray, new Integer[]{1, 2, 3, 4});
        Integer[] typedArray2 = this.newWith(1, 2, 3, 4).toArray(new Integer[5]);
        Assertions.assertArrayEquals(typedArray2, new Integer[]{1, 2, 3, 4, null});
        Integer[] typedArray3 = this.newWith(1, 2, 3, 4).toTypedArray(Integer.class);
        Assertions.assertArrayEquals(typedArray3, new Integer[]{1, 2, 3, 4});
    }

    @Override
    @Test
    public void testToString()
    {
        FastList<Integer> integers = this.newWith(1, 2, 3, 4);
        Assertions.assertNotNull(integers.toString());
        Assertions.assertEquals("[1, 2, 3, 4]", integers.toString());
    }

    @Test
    public void toStringRecursion()
    {
        MutableList<Object> list = FastList.newListWith(1, 2, 3);
        list.add(list);
        Assertions.assertEquals("[1, 2, 3, (this FastList)]", list.toString());
    }

    @Test
    public void makeStringRecursion()
    {
        MutableList<Object> list = FastList.newListWith(1, 2, 3);
        list.add(list);
        Assertions.assertEquals("1, 2, 3, (this FastList)", list.makeString());
    }

    @Test
    public void testTrimToSize()
    {
        FastList<Integer> integers = this.newWith(1, 2, 3, 4);
        integers.trimToSize();
        Assertions.assertEquals("[1, 2, 3, 4]", integers.toString());
    }

    @Test
    public void testTrimToSizeWithLoadFactory()
    {
        FastList<Integer> integers = FastList.<Integer>newList(10).with(1, 2, 3, 4);
        Assertions.assertFalse(integers.trimToSizeIfGreaterThanPercent(0.70));
        Assertions.assertTrue(integers.trimToSizeIfGreaterThanPercent(0.10));
        Assertions.assertEquals("[1, 2, 3, 4]", integers.toString());
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        FastList<Integer> integers = FastList.<Integer>newList(3).with(1, 2, 3);
        FastList<Integer> integers2 = this.newWith(1, 2, 3);
        FastList<Integer> integers3 = this.newWith(1, null, 3, 4, 5);
        FastList<Integer> integers4 = this.newWith(1, null, 3, 4, 5);
        FastList<Integer> integers5 = this.newWith(1, null, 3);
        List<Integer> linkedList = new LinkedList<>(integers);
        List<Integer> linkedList2 = new LinkedList<>(integers3);
        List<Integer> linkedList3 = new LinkedList<>(integers5);
        Verify.assertEqualsAndHashCode(integers, integers);
        Verify.assertPostSerializedEqualsAndHashCode(integers);
        Verify.assertEqualsAndHashCode(integers, integers2);
        Verify.assertEqualsAndHashCode(integers, iList(1, 2, 3));
        Verify.assertEqualsAndHashCode(integers, linkedList);
        Assertions.assertNotEquals(integers, integers3);
        Assertions.assertNotEquals(integers, integers5);
        Assertions.assertNotEquals(integers, iList(2, 3, 4));
        Assertions.assertNotEquals(integers, linkedList2);
        Assertions.assertNotEquals(integers, linkedList3);
        Assertions.assertNotEquals(integers, mSet());
        Verify.assertEqualsAndHashCode(integers3, integers4);
        Verify.assertEqualsAndHashCode(integers3, new ArrayList<>(integers3));
        Verify.assertEqualsAndHashCode(integers3, new LinkedList<>(integers3));
        Verify.assertEqualsAndHashCode(integers3, ArrayAdapter.newArrayWith(1, null, 3, 4, 5));
        Assertions.assertNotEquals(integers3, ArrayAdapter.newArrayWith(1, null, 3, 4, 6));
        Verify.assertEqualsAndHashCode(integers3, ArrayListAdapter.<Integer>newList().with(1, null, 3, 4, 5));
        Assertions.assertEquals(integers, integers2);
        Assertions.assertNotEquals(integers, integers3);
    }

    @Override
    @Test
    public void iterator()
    {
        int sum = 0;
        FastList<Integer> integers = this.newWith(1, 2, 3, 4);
        for (Integer each : integers)
        {
            sum += each.intValue();
        }
        Assertions.assertEquals(10, sum);
    }

    @Override
    @Test
    public void removeObject()
    {
        super.removeObject();

        FastList<Integer> integers = this.newWith(1, 2, 3, 4);
        integers.remove(Integer.valueOf(1));
        Verify.assertStartsWith(integers, 2, 3, 4);
        Assertions.assertFalse(integers.remove(Integer.valueOf(5)));
    }

    @Test
    public void testIteratorRemove()
    {
        FastList<Integer> integers = this.newWith(1, 2, 3, 4);
        Iterator<Integer> iterator = integers.iterator();
        iterator.next();
        iterator.remove();
        Verify.assertStartsWith(integers, 2, 3, 4);
    }

    @Override
    @Test
    public void toList()
    {
        FastList<Integer> integers = this.newWith(1, 2, 3, 4);
        MutableList<Integer> list = integers.toList();
        Verify.assertStartsWith(list, 1, 2, 3, 4);
    }

    @Override
    @Test
    public void toSet()
    {
        FastList<Integer> integers = this.newWith(1, 2, 3, 4);
        MutableSet<Integer> set = integers.toSet();
        Verify.assertContainsAll(set, 1, 2, 3, 4);
    }

    @Test
    public void testSortOnListWithLessThan10Elements()
    {
        FastList<Integer> integers = this.newWith(2, 3, 4, 1, 7, 9, 6, 8, 5);
        Verify.assertStartsWith(integers.sortThis(), 1, 2, 3, 4, 5, 6, 7, 8, 9);
        FastList<Integer> integers2 = this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Verify.assertStartsWith(integers2.sortThis(Collections.<Integer>reverseOrder()), 9, 8, 7, 6, 5, 4, 3, 2, 1);
        FastList<Integer> integers3 = this.newWith(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Verify.assertStartsWith(integers3.sortThis(), 1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    @Test
    public void testSort()
    {
        for (int i = 1; i < 25; i++)
        {
            FastList<Integer> actual = FastList.newList(Interval.oneTo(i));
            for (int j = 0; j < 3; j++)
            {
                actual.shuffleThis();
                Assertions.assertEquals(Interval.oneTo(i), actual.sortThis());
                Assertions.assertEquals(Interval.oneTo(i).reverseThis(), actual.sortThis(Collections.<Integer>reverseOrder()));
            }
        }
    }

    @Test
    public void testNewListWithCollection()
    {
        Verify.assertEmpty(FastList.newList(iList()));
        Verify.assertEmpty(FastList.newList(mSet()));
        Verify.assertEmpty(FastList.newList(FastList.newList()));
        Verify.assertEmpty(FastList.newList(FastList.newList(4)));

        FastList<Integer> setToList = FastList.newList(mSet(1, 2, 3, 4, 5));
        Verify.assertNotEmpty(setToList);
        Verify.assertSize(5, setToList);
        Verify.assertContainsAll(setToList, 1, 2, 3, 4, 5);

        FastList<Integer> arrayListToList = FastList.newList(iList(1, 2, 3, 4, 5));
        Verify.assertNotEmpty(arrayListToList);
        Verify.assertSize(5, arrayListToList);
        Verify.assertStartsWith(arrayListToList, 1, 2, 3, 4, 5);

        FastList<Integer> fastListToList = FastList.newList(this.newWith(1, 2, 3, 4, 5));
        Verify.assertNotEmpty(fastListToList);
        Verify.assertSize(5, fastListToList);
        Verify.assertStartsWith(fastListToList, 1, 2, 3, 4, 5);
    }

    @Test
    public void testNewListWithIterable()
    {
        FastList<Integer> integers = FastList.newList(Interval.oneTo(3));
        Assertions.assertEquals(this.newWith(1, 2, 3), integers);
    }

    @Test
    public void testContainsAll()
    {
        FastList<Integer> list = this.newWith(1, 2, 3, 4, 5, null);
        Assertions.assertTrue(list.containsAll(mList(1, 3, 5, null)));
        Assertions.assertFalse(list.containsAll(mList(2, null, 6)));
        Assertions.assertTrue(list.containsAll(this.newWith(1, 3, 5, null)));
        Assertions.assertFalse(list.containsAll(this.newWith(2, null, 6)));
    }

    @Test
    public void testToArrayFromTo()
    {
        Assertions.assertArrayEquals(new Integer[]{1, 2, 3}, this.newWith(1, 2, 3, 4).toArray(0, 2));
        Assertions.assertArrayEquals(new Integer[]{2, 3, 4}, this.newWith(1, 2, 3, 4).toArray(1, 3));
    }

    @Test
    public void testLazyCollectForEach()
    {
        LazyIterable<String> select =
                FastList.newList(Interval.oneTo(5)).asLazy().collect(Object::toString);
        Procedure<String> builder = Procedures.append(new StringBuilder());
        select.forEach(builder);
        Assertions.assertEquals("12345", builder.toString());
    }

    @Test
    public void testLazyFlattenForEach()
    {
        FastList<Integer> list = (FastList<Integer>) Interval.oneTo(5).toList();
        LazyIterable<String> select = LazyIterate.flatCollect(
                list,
                object -> this.newWith(String.valueOf(object)));
        Appendable builder = new StringBuilder();
        Procedure<String> appendProcedure = Procedures.append(builder);
        select.forEach(appendProcedure);
        Assertions.assertEquals("12345", builder.toString());
    }

    /**
     * @deprecated since 3.0. Use {@code asLazy().reject(Predicate)} instead.
     */
    @Deprecated
    @Test
    public void testLazyRejectForEach()
    {
        LazyIterable<Integer> select = FastList.newList(Interval.oneTo(5)).asLazy().reject(Predicates.lessThan(5));
        Sum sum = new IntegerSum(0);
        select.forEach(new SumProcedure<>(sum));
        Assertions.assertEquals(5, sum.getValue().intValue());
    }

    /**
     * @deprecated since 3.0. Use {@code asLazy().select(Predicate)} instead.
     */
    @Deprecated
    @Test
    public void testLazySelectForEach()
    {
        LazyIterable<Integer> select = FastList.newList(Interval.oneTo(5)).asLazy().select(Predicates.lessThan(5));
        Sum sum = new IntegerSum(0);
        select.forEach(new SumProcedure<>(sum));
        Assertions.assertEquals(10, sum.getValue().intValue());
    }

    @Test
    public void testWith()
    {
        Assertions.assertEquals(
                FastList.newListWith("1"),
                FastList.<String>newList().with("1"));
        Assertions.assertEquals(
                FastList.newListWith("1", "2"),
                FastList.<String>newList().with("1", "2"));
        Assertions.assertEquals(
                FastList.newListWith("1", "2", "3"),
                FastList.<String>newList().with("1", "2", "3"));
        Assertions.assertEquals(
                FastList.newListWith("1", "2", "3", "4"),
                FastList.<String>newList().with("1", "2", "3", "4"));
        Assertions.assertEquals(
                FastList.newListWith("1", "2", "3", "4", "5", "6", "7", "8"),
                FastList.<String>newList().with("1", "2", "3", "4").with("5", "6", "7", "8"));

        MutableList<String> list = FastList.newListWith("A")
                .withAll(Lists.immutable.of("1", "2"))
                .withAll(Lists.immutable.<String>of())
                .withAll(Sets.immutable.of("3", "4"));
        Assertions.assertEquals(
                Bags.mutable.of("A", "1", "2", "3", "4"),
                list.toBag());
        Verify.assertStartsWith(list, "A", "1", "2");  // "3" and "4" are from a set, so may not be in order

        Assertions.assertEquals(
                FastList.newListWith(42, 10, 11, 12),
                FastList.newListWith(42).withAll(Interval.from(10).to(12).toList()));
    }

    @Test
    public void min_empty_throws_without_comparator()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.newWith().min();
        });
    }

    @Test
    public void max_empty_throws_without_comparator()
    {
        assertThrows(NoSuchElementException.class, () -> {
            this.newWith().max();
        });
    }
}
