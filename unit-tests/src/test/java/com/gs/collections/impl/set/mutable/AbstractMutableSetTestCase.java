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

package com.gs.collections.impl.set.mutable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.gs.collections.api.LazyIterable;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.bag.sorted.MutableSortedBag;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.set.UnsortedSetIterable;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.Counter;
import com.gs.collections.impl.IntegerWithCast;
import com.gs.collections.impl.bag.sorted.mutable.TreeBag;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.factory.Procedures;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.collection.mutable.AbstractCollectionTestCase;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.fixed.ArrayAdapter;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.utility.Iterate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.gs.collections.impl.factory.Iterables.iSet;
import static com.gs.collections.impl.factory.Iterables.mList;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JUnit test for {@link AbstractMutableSet}.
 */
public abstract class AbstractMutableSetTestCase extends AbstractCollectionTestCase
{
    protected static final Integer COLLISION_1 = 0;
    protected static final Integer COLLISION_2 = 17;
    protected static final Integer COLLISION_3 = 34;
    protected static final Integer COLLISION_4 = 51;
    protected static final Integer COLLISION_5 = 68;
    protected static final Integer COLLISION_6 = 85;
    protected static final Integer COLLISION_7 = 102;
    protected static final Integer COLLISION_8 = 119;
    protected static final Integer COLLISION_9 = 136;
    protected static final Integer COLLISION_10 = 152;
    protected static final MutableList<Integer> COLLISIONS =
            FastList.newListWith(COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4, COLLISION_5);
    protected static final MutableList<Integer> MORE_COLLISIONS = FastList.newList(COLLISIONS)
            .with(COLLISION_6, COLLISION_7, COLLISION_8, COLLISION_9);
    protected static final int SIZE = 8;
    protected static final String[] FREQUENT_COLLISIONS = {"\u9103\ufffe", "\u9104\uffdf",
            "\u9105\uffc0", "\u9106\uffa1", "\u9107\uff82", "\u9108\uff63", "\u9109\uff44",
            "\u910a\uff25", "\u910b\uff06", "\u910c\ufee7"};

    @Override
    protected abstract <T> MutableSet<T> newWith(T... littleElements);

    @Override
    @Test
    public void asSynchronized()
    {
        Verify.assertInstanceOf(SynchronizedMutableSet.class, this.newWith().asSynchronized());
    }

    @Override
    @Test
    public void addAll()
    {
        super.addAll();

        UnifiedSet<Integer> expected = UnifiedSet.newSetWith(1, 2, 3);
        MutableSet<Integer> collection = this.newWith();

        Assertions.assertTrue(collection.addAll(FastList.newListWith(1, 2, 3)));
        Assertions.assertEquals(expected, collection);

        Assertions.assertFalse(collection.addAll(FastList.newListWith(1, 2, 3)));
        Assertions.assertEquals(expected, collection);
    }

    @Override
    @Test
    public void addAllIterable()
    {
        super.addAllIterable();

        UnifiedSet<Integer> expected = UnifiedSet.newSetWith(1, 2, 3);
        MutableSet<Integer> collection = this.newWith();

        Assertions.assertTrue(collection.addAllIterable(FastList.newListWith(1, 2, 3)));
        Assertions.assertEquals(expected, collection);

        Assertions.assertFalse(collection.addAllIterable(FastList.newListWith(1, 2, 3)));
        Assertions.assertEquals(expected, collection);
    }

    @Test
    public void union()
    {
        MutableSet<String> set = this.newWith("1", "2", "3", "4");
        MutableSet<String> union = set.union(UnifiedSet.newSetWith("a", "b", "c", "1"));
        Verify.assertSize(set.size() + 3, union);
        Assertions.assertTrue(union.containsAllIterable(Interval.oneTo(set.size()).collect(String::valueOf)));
        Verify.assertContainsAll(union, "a", "b", "c");

        Assertions.assertEquals(set, set.union(UnifiedSet.newSetWith("1")));
    }

    @Test
    public void unionInto()
    {
        MutableSet<String> set = this.newWith("1", "2", "3", "4");
        MutableSet<String> union = set.unionInto(UnifiedSet.newSetWith("a", "b", "c", "1"), UnifiedSet.<String>newSet());
        Verify.assertSize(set.size() + 3, union);
        Assertions.assertTrue(union.containsAllIterable(Interval.oneTo(set.size()).collect(String::valueOf)));
        Verify.assertContainsAll(union, "a", "b", "c");

        Assertions.assertEquals(set, set.unionInto(UnifiedSet.newSetWith("1"), UnifiedSet.<String>newSet()));
    }

    @Test
    public void intersect()
    {
        MutableSet<String> set = this.newWith("1", "2", "3", "4");
        MutableSet<String> intersect = set.intersect(UnifiedSet.newSetWith("a", "b", "c", "1"));
        Verify.assertSize(1, intersect);
        Assertions.assertEquals(UnifiedSet.newSetWith("1"), intersect);

        Verify.assertEmpty(set.intersect(UnifiedSet.newSetWith("not present")));
    }

    @Test
    public void intersectInto()
    {
        MutableSet<String> set = this.newWith("1", "2", "3", "4");
        MutableSet<String> intersect = set.intersectInto(UnifiedSet.newSetWith("a", "b", "c", "1"), UnifiedSet.<String>newSet());
        Verify.assertSize(1, intersect);
        Assertions.assertEquals(UnifiedSet.newSetWith("1"), intersect);

        Verify.assertEmpty(set.intersectInto(UnifiedSet.newSetWith("not present"), UnifiedSet.<String>newSet()));
    }

    @Test
    public void difference()
    {
        MutableSet<String> set = this.newWith("1", "2", "3", "4");
        MutableSet<String> difference = set.difference(UnifiedSet.newSetWith("2", "3", "4", "not present"));
        Assertions.assertEquals(UnifiedSet.newSetWith("1"), difference);
        Assertions.assertEquals(set, set.difference(UnifiedSet.newSetWith("not present")));
    }

    @Test
    public void differenceInto()
    {
        MutableSet<String> set = this.newWith("1", "2", "3", "4");
        MutableSet<String> difference = set.differenceInto(UnifiedSet.newSetWith("2", "3", "4", "not present"), UnifiedSet.<String>newSet());
        Assertions.assertEquals(UnifiedSet.newSetWith("1"), difference);
        Assertions.assertEquals(set, set.differenceInto(UnifiedSet.newSetWith("not present"), UnifiedSet.<String>newSet()));
    }

    @Test
    public void symmetricDifference()
    {
        MutableSet<String> set = this.newWith("1", "2", "3", "4");
        MutableSet<String> difference = set.symmetricDifference(UnifiedSet.newSetWith("2", "3", "4", "5", "not present"));
        Verify.assertContains(difference, "1");
        Assertions.assertTrue(difference.containsAllIterable(Interval.fromTo(set.size() + 1, 5).collect(String::valueOf)));
        for (int i = 2; i <= set.size(); i++)
        {
            Verify.assertNotContains(difference, String.valueOf(i));
        }

        Verify.assertSize(set.size() + 1, set.symmetricDifference(UnifiedSet.newSetWith("not present")));
    }

    @Test
    public void symmetricDifferenceInto()
    {
        MutableSet<String> set = this.newWith("1", "2", "3", "4");
        MutableSet<String> difference = set.symmetricDifferenceInto(
                UnifiedSet.newSetWith("2", "3", "4", "5", "not present"),
                UnifiedSet.<String>newSet());
        Verify.assertContains(difference, "1");
        Assertions.assertTrue(difference.containsAllIterable(Interval.fromTo(set.size() + 1, 5).collect(String::valueOf)));
        for (int i = 2; i <= set.size(); i++)
        {
            Verify.assertNotContains(difference, String.valueOf(i));
        }

        Verify.assertSize(
                set.size() + 1,
                set.symmetricDifferenceInto(UnifiedSet.newSetWith("not present"), UnifiedSet.<String>newSet()));
    }

    @Test
    public void isSubsetOf()
    {
        MutableSet<String> set = this.newWith("1", "2", "3", "4");
        Assertions.assertTrue(set.isSubsetOf(UnifiedSet.newSetWith("1", "2", "3", "4", "5")));
    }

    @Test
    public void isProperSubsetOf()
    {
        MutableSet<String> set = this.newWith("1", "2", "3", "4");
        Assertions.assertTrue(set.isProperSubsetOf(UnifiedSet.newSetWith("1", "2", "3", "4", "5")));
        Assertions.assertFalse(set.isProperSubsetOf(set));
    }

    @Test
    public void powerSet()
    {
        MutableSet<String> set = this.newWith("1", "2", "3", "4");
        MutableSet<UnsortedSetIterable<String>> powerSet = set.powerSet();
        Verify.assertSize((int) StrictMath.pow(2, set.size()), powerSet);
        Verify.assertContains(UnifiedSet.<String>newSet(), powerSet);
        Verify.assertContains(set, powerSet);
    }

    @Test
    public void cartesianProduct()
    {
        MutableSet<String> set = this.newWith("1", "2", "3", "4");
        LazyIterable<Pair<String, String>> cartesianProduct = set.cartesianProduct(UnifiedSet.newSetWith("One", "Two"));
        Verify.assertIterableSize(set.size() * 2, cartesianProduct);
        Assertions.assertEquals(
                set,
                cartesianProduct
                        .select(Predicates.attributeEqual((Function<Pair<?, String>, String>) Pair::getTwo, "One"))
                        .collect((Function<Pair<String, ?>, String>) Pair::getOne).toSet());
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        Verify.assertInstanceOf(UnmodifiableMutableSet.class, this.newWith().asUnmodifiable());
    }

    @Override
    @Test
    public void select()
    {
        super.select();
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4, 5).select(Predicates.lessThan(3)), 1, 2);
        Verify.assertContainsAll(
                this.newWith(-1, 2, 3, 4, 5).select(Predicates.lessThan(3),
                        FastList.<Integer>newList()), -1, 2);
    }

    @Override
    @Test
    public void reject()
    {
        super.reject();
        Verify.assertContainsAll(this.newWith(1, 2, 3, 4).reject(Predicates.lessThan(3)), 3, 4);
        Verify.assertContainsAll(
                this.newWith(1, 2, 3, 4).reject(Predicates.lessThan(3),
                        FastList.<Integer>newList()), 3, 4);
    }

    @Override
    @Test
    public void getFirst()
    {
        super.getFirst();

        Assertions.assertNotNull(this.newWith(1, 2, 3).getFirst());
        Assertions.assertNull(this.newWith().getFirst());
    }

    @Override
    @Test
    public void getLast()
    {
        Assertions.assertNotNull(this.newWith(1, 2, 3).getLast());
        Assertions.assertNull(this.newWith().getLast());
    }

    @Test
    public void unifiedSetKeySetToArrayDest()
    {
        MutableSet<Integer> set = this.newWith(1, 2, 3, 4);
        // deliberately to small to force the method to allocate one of the correct size
        Integer[] dest = new Integer[2];
        Integer[] result = set.toArray(dest);
        Verify.assertSize(4, result);
        Arrays.sort(result);
        Assertions.assertArrayEquals(new Integer[]{1, 2, 3, 4}, result);
    }

    @Test
    public void unifiedSetToString()
    {
        MutableSet<Integer> set = this.newWith(1, 2);
        String s = set.toString();
        Assertions.assertTrue("[1, 2]".equals(s) || "[2, 1]".equals(s));
    }

    @Test
    public void testClone()
    {
        MutableSet<String> set = this.newWith();
        MutableSet<String> clone = set.clone();
        Assertions.assertNotSame(clone, set);
        Verify.assertEqualsAndHashCode(clone, set);
    }

    @Override
    @Test
    public void isEmpty()
    {
        super.isEmpty();

        MutableSet<String> set = this.newWith();
        this.assertIsEmpty(true, set);

        set.add("stuff");
        this.assertIsEmpty(false, set);

        set.remove("stuff");
        this.assertIsEmpty(true, set);

        set.add("Bon");
        set.add("Jovi");
        this.assertIsEmpty(false, set);
        set.remove("Jovi");
        this.assertIsEmpty(false, set);
        set.clear();
        this.assertIsEmpty(true, set);
    }

    private void assertIsEmpty(boolean isEmpty, MutableSet<?> set)
    {
        Assertions.assertEquals(isEmpty, set.isEmpty());
        Assertions.assertEquals(!isEmpty, set.notEmpty());
    }

    @Test
    public void add()
    {
        MutableSet<IntegerWithCast> set = this.newWith();
        MutableList<IntegerWithCast> collisions = COLLISIONS.collect(IntegerWithCast::new);
        set.addAll(collisions);
        set.removeAll(collisions);
        for (Integer integer : COLLISIONS)
        {
            Assertions.assertTrue(set.add(new IntegerWithCast(integer)));
            Assertions.assertFalse(set.add(new IntegerWithCast(integer)));
        }
        Assertions.assertEquals(collisions.toSet(), set);
    }

    @Override
    @Test
    public void removeIf()
    {
        super.removeIf();

        MutableSet<IntegerWithCast> set = this.newWith();
        MutableList<IntegerWithCast> collisions = COLLISIONS.collect(IntegerWithCast::new);
        set.addAll(collisions);
        collisions.reverseForEach(each -> {
            Assertions.assertFalse(set.remove(null));
            Assertions.assertTrue(set.remove(each));
            Assertions.assertFalse(set.remove(each));
            Assertions.assertFalse(set.remove(null));
            Assertions.assertFalse(set.remove(new IntegerWithCast(COLLISION_10)));
        });

        Assertions.assertEquals(UnifiedSet.<IntegerWithCast>newSet(), set);

        collisions.forEach(Procedures.cast(each -> {
            MutableSet<IntegerWithCast> set2 = this.newWith();
            set2.addAll(collisions);

            Assertions.assertFalse(set2.remove(null));
            Assertions.assertTrue(set2.remove(each));
            Assertions.assertFalse(set2.remove(each));
            Assertions.assertFalse(set2.remove(null));
            Assertions.assertFalse(set2.remove(new IntegerWithCast(COLLISION_10)));
        }));

        // remove the second-to-last item in a fully populated single chain to cause the last item to move
        MutableSet<Integer> set3 = this.newWith(COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4);
        Assertions.assertTrue(set3.remove(COLLISION_3));
        Assertions.assertEquals(UnifiedSet.newSetWith(COLLISION_1, COLLISION_2, COLLISION_4), set3);

        Assertions.assertTrue(set3.remove(COLLISION_2));
        Assertions.assertEquals(UnifiedSet.newSetWith(COLLISION_1, COLLISION_4), set3);

        // search a chain for a non-existent element
        MutableSet<Integer> chain = this.newWith(COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4);
        Assertions.assertFalse(chain.remove(COLLISION_5));

        // search a deep chain for a non-existent element
        MutableSet<Integer> deepChain = this.newWith(COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4, COLLISION_5, COLLISION_6, COLLISION_7);
        Assertions.assertFalse(deepChain.remove(COLLISION_8));

        // search for a non-existent element
        MutableSet<Integer> empty = this.newWith();
        Assertions.assertFalse(empty.remove(COLLISION_1));
    }

    @Override
    @Test
    public void retainAll()
    {
        super.retainAll();

        MutableList<Integer> collisions = MORE_COLLISIONS.clone();
        collisions.add(COLLISION_10);

        int size = MORE_COLLISIONS.size();
        for (int i = 0; i < size; i++)
        {
            MutableList<Integer> list = MORE_COLLISIONS.subList(0, i);
            MutableSet<Integer> set = this.<Integer>newWith().withAll(list);
            Assertions.assertFalse(set.retainAll(collisions));
            Assertions.assertEquals(list.toSet(), set);
        }

        for (Integer item : MORE_COLLISIONS)
        {
            MutableSet<Integer> integers = this.<Integer>newWith().withAll(MORE_COLLISIONS);
            @SuppressWarnings("BoxingBoxedValue")
            Integer keyCopy = Integer.valueOf(item);
            Assertions.assertTrue(integers.retainAll(mList(keyCopy)));
            Assertions.assertEquals(iSet(keyCopy), integers);
            Assertions.assertNotSame(keyCopy, Iterate.getOnly(integers));
        }

        // retain all on a bucket with a single element
        MutableSet<Integer> singleCollisionBucket = this.newWith(COLLISION_1, COLLISION_2);
        singleCollisionBucket.remove(COLLISION_2);
        Assertions.assertTrue(singleCollisionBucket.retainAll(FastList.newListWith(COLLISION_2)));
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        super.equalsAndHashCode();

        UnifiedSet<Integer> expected = UnifiedSet.newSetWith(COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4);
        Assertions.assertNotEquals(expected, this.newWith(COLLISION_2, COLLISION_3, COLLISION_4, COLLISION_5));
        Assertions.assertNotEquals(expected, this.newWith(COLLISION_1, COLLISION_3, COLLISION_4, COLLISION_5));
        Assertions.assertNotEquals(expected, this.newWith(COLLISION_1, COLLISION_2, COLLISION_4, COLLISION_5));
        Assertions.assertNotEquals(expected, this.newWith(COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_5));

        Assertions.assertEquals(expected, this.newWith(COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4));
    }

    @Override
    @Test
    public void tap()
    {
        super.tap();

        int size = MORE_COLLISIONS.size();
        for (int i = 1; i < size; i++)
        {
            MutableList<Integer> tapResult = Lists.mutable.of();
            MutableSet<Integer> set = this.newWith();
            set.addAll(MORE_COLLISIONS.subList(0, i));
            Assertions.assertSame(set, set.tap(tapResult::add));
            Assertions.assertEquals(set.toList(), tapResult);
        }

        // test iterating on a bucket with only one element
        MutableSet<Integer> set = this.newWith(COLLISION_1, COLLISION_2);
        set.remove(COLLISION_2);
        Counter counter = new Counter();
        Assertions.assertSame(set, set.tap(x -> counter.increment()));
        Assertions.assertEquals(1, counter.getCount());
    }

    @Override
    @Test
    public void forEach()
    {
        super.forEach();

        int size = MORE_COLLISIONS.size();
        for (int i = 1; i < size; i++)
        {
            MutableSet<Integer> set = this.newWith();
            set.addAll(MORE_COLLISIONS.subList(0, i));
            MutableSet<Integer> result = UnifiedSet.newSet();
            set.forEach(CollectionAddProcedure.on(result));
            Assertions.assertEquals(set, result);
        }

        // test iterating on a bucket with only one element
        MutableSet<Integer> set = this.newWith(COLLISION_1, COLLISION_2);
        set.remove(COLLISION_2);
        Counter counter = new Counter();
        set.forEach(Procedures.cast(each -> counter.increment()));
        Assertions.assertEquals(1, counter.getCount());
    }

    @Override
    @Test
    public void forEachWith()
    {
        super.forEachWith();

        Object sentinel = new Object();
        int size = MORE_COLLISIONS.size();
        for (int i = 1; i < size; i++)
        {
            MutableSet<Integer> set = this.newWith();
            set.addAll(MORE_COLLISIONS.subList(0, i));
            MutableSet<Integer> result = UnifiedSet.newSet();

            set.forEachWith((argument1, argument2) -> {
                Assertions.assertSame(sentinel, argument2);
                result.add(argument1);
            }, sentinel);
            Assertions.assertEquals(set, result);
        }

        // test iterating on a bucket with only one element
        MutableSet<Integer> set = this.newWith(COLLISION_1, COLLISION_2);
        set.remove(COLLISION_2);
        Counter counter = new Counter();
        set.forEachWith((argument1, argument2) -> argument2.increment(), counter);
        Assertions.assertEquals(1, counter.getCount());
    }

    @Override
    @Test
    public void forEachWithIndex()
    {
        super.forEachWithIndex();

        int size = MORE_COLLISIONS.size();
        for (int i = 1; i < size; i++)
        {
            MutableSet<Integer> set = this.newWith();
            set.addAll(MORE_COLLISIONS.subList(0, i));
            MutableSet<Integer> result = UnifiedSet.newSet();
            MutableList<Integer> indexes = Lists.mutable.of();
            set.forEachWithIndex((each, index) -> {
                result.add(each);
                indexes.add(index);
            });
            Assertions.assertEquals(set, result);
            Assertions.assertEquals(Interval.zeroTo(i - 1), indexes);
        }

        // test iterating on a bucket with only one element
        UnifiedSet<Integer> set = UnifiedSet.newSetWith(COLLISION_1, COLLISION_2);
        set.remove(COLLISION_2);
        Counter counter = new Counter();
        set.forEachWithIndex((each, index) -> counter.increment());
        Assertions.assertEquals(1, counter.getCount());
    }

    @Override
    @Test
    public void anySatisfy()
    {
        super.anySatisfy();

        int size = MORE_COLLISIONS.size();
        for (int i = 1; i < size; i++)
        {
            MutableSet<Integer> set = this.newWith();
            set.addAll(MORE_COLLISIONS.subList(0, i));
            Assertions.assertTrue(set.anySatisfy(MORE_COLLISIONS.subList(0, i).getLast()::equals));
            Assertions.assertFalse(set.anySatisfy(Predicates.greaterThan(MORE_COLLISIONS.subList(0, i).getLast())));
        }

        // test anySatisfy on a bucket with only one element
        MutableSet<Integer> set = this.newWith(COLLISION_1, COLLISION_2);
        set.remove(COLLISION_2);
        Assertions.assertTrue(set.anySatisfy(COLLISION_1::equals));
        Assertions.assertFalse(set.anySatisfy(COLLISION_2::equals));

        // Rehashing Case A: a bucket with only one entry and a low capacity forcing a rehash, where the triggering element goes in the bucket
        // set up a chained bucket
        MutableSet<Integer> caseA = this.newWith(COLLISION_1, COLLISION_2);
        // clear the bucket to one element
        caseA.remove(COLLISION_2);
        // increase the occupied count to the threshold
        caseA.add(Integer.valueOf(1));
        caseA.add(Integer.valueOf(2));

        // add the colliding value back and force the rehash
        caseA.add(COLLISION_2);
        Assertions.assertTrue(caseA.anySatisfy(COLLISION_2::equals));
    }

    @Override
    @Test
    public void anySatisfyWith()
    {
        super.anySatisfyWith();

        int size = MORE_COLLISIONS.size();
        for (int i = 1; i < size; i++)
        {
            MutableSet<Integer> set = this.newWith();
            set.addAll(MORE_COLLISIONS.subList(0, i));
            Assertions.assertTrue(set.anySatisfyWith(Object::equals, MORE_COLLISIONS.subList(0, i).getLast()));
            Assertions.assertFalse(set.anySatisfyWith(Predicates2.greaterThan(), MORE_COLLISIONS.subList(0, i).getLast()));
        }

        // test anySatisfyWith on a bucket with only one element
        MutableSet<Integer> set = this.newWith(COLLISION_1, COLLISION_2);
        set.remove(COLLISION_2);
        Assertions.assertTrue(set.anySatisfyWith(Object::equals, COLLISION_1));
        Assertions.assertFalse(set.anySatisfyWith(Object::equals, COLLISION_2));

        // Rehashing Case A: a bucket with only one entry and a low capacity forcing a rehash, where the triggering element goes in the bucket
        // set up a chained bucket
        MutableSet<Integer> caseA = this.newWith(COLLISION_1, COLLISION_2);
        // clear the bucket to one element
        caseA.remove(COLLISION_2);
        // increase the occupied count to the threshold
        caseA.add(Integer.valueOf(1));
        caseA.add(Integer.valueOf(2));

        // add the colliding value back and force the rehash
        caseA.add(COLLISION_2);
        Assertions.assertTrue(caseA.anySatisfyWith(Object::equals, COLLISION_2));
    }

    @Override
    @Test
    public void allSatisfy()
    {
        super.allSatisfy();

        int size = MORE_COLLISIONS.size();
        for (int i = 1; i < size; i++)
        {
            MutableSet<Integer> set = this.newWith();
            set.addAll(MORE_COLLISIONS.subList(0, i));
            Assertions.assertTrue(set.allSatisfy(Predicates.greaterThanOrEqualTo(MORE_COLLISIONS.subList(0, i).getFirst())));
            Assertions.assertFalse(set.allSatisfy(Predicates.lessThan(MORE_COLLISIONS.subList(0, i).get(i - 1))));
        }

        // test allSatisfy on a bucket with only one element
        MutableSet<Integer> set = this.newWith(COLLISION_1, COLLISION_2);
        set.remove(COLLISION_2);
        Assertions.assertTrue(set.allSatisfy(COLLISION_1::equals));
        Assertions.assertFalse(set.allSatisfy(COLLISION_2::equals));

        // Rehashing Case A: a bucket with only one entry and a low capacity forcing a rehash, where the triggering element goes in the bucket
        // set up a chained bucket
        MutableSet<Integer> caseA = this.newWith(COLLISION_1, COLLISION_2);
        // clear the bucket to one element
        caseA.remove(COLLISION_2);
        // increase the occupied count to the threshold
        caseA.add(Integer.valueOf(1));
        caseA.add(Integer.valueOf(2));

        // add the colliding value back and force the rehash
        caseA.add(COLLISION_2);
        Assertions.assertTrue(caseA.allSatisfy(Predicates.lessThanOrEqualTo(COLLISION_2)));
    }

    @Override
    @Test
    public void allSatisfyWith()
    {
        super.allSatisfyWith();

        int size = MORE_COLLISIONS.size();
        for (int i = 1; i < size; i++)
        {
            MutableSet<Integer> set = this.newWith();
            set.addAll(MORE_COLLISIONS.subList(0, i));
            Assertions.assertTrue(set.allSatisfyWith(Predicates2.greaterThanOrEqualTo(), MORE_COLLISIONS.subList(0, i).getFirst()));
            Assertions.assertFalse(set.allSatisfyWith(Predicates2.lessThan(), MORE_COLLISIONS.subList(0, i).get(i - 1)));
        }

        // test allSatisfyWith on a bucket with only one element
        MutableSet<Integer> set = this.newWith(COLLISION_1, COLLISION_2);
        set.remove(COLLISION_2);
        Assertions.assertTrue(set.allSatisfyWith(Object::equals, COLLISION_1));
        Assertions.assertFalse(set.allSatisfyWith(Object::equals, COLLISION_2));

        // Rehashing Case A: a bucket with only one entry and a low capacity forcing a rehash, where the triggering element goes in the bucket
        // set up a chained bucket
        MutableSet<Integer> caseA = this.newWith(COLLISION_1, COLLISION_2);
        // clear the bucket to one element
        caseA.remove(COLLISION_2);
        // increase the occupied count to the threshold
        caseA.add(Integer.valueOf(1));
        caseA.add(Integer.valueOf(2));

        // add the colliding value back and force the rehash
        caseA.add(COLLISION_2);
        Assertions.assertTrue(caseA.allSatisfyWith(Predicates2.lessThanOrEqualTo(), COLLISION_2));
    }

    @Override
    @Test
    public void noneSatisfy()
    {
        super.noneSatisfy();

        int size = MORE_COLLISIONS.size();
        for (int i = 1; i < size; i++)
        {
            MutableSet<Integer> set = this.newWith();
            set.addAll(MORE_COLLISIONS.subList(0, i));
            Assertions.assertTrue(set.noneSatisfy(Predicates.lessThan(MORE_COLLISIONS.subList(0, i).getFirst())));
            Assertions.assertFalse(set.noneSatisfy(Predicates.greaterThanOrEqualTo(MORE_COLLISIONS.subList(0, i).get(i - 1))));
        }

        // test noneSatisfy on a bucket with only one element
        MutableSet<Integer> set = this.newWith(COLLISION_1, COLLISION_2);
        set.remove(COLLISION_2);
        Assertions.assertFalse(set.noneSatisfy(COLLISION_1::equals));
        Assertions.assertTrue(set.noneSatisfy(COLLISION_2::equals));

        // Rehashing Case A: a bucket with only one entry and a low capacity forcing a rehash, where the triggering element goes in the bucket
        // set up a chained bucket
        MutableSet<Integer> caseA = this.newWith(COLLISION_1, COLLISION_2);
        // clear the bucket to one element
        caseA.remove(COLLISION_2);
        // increase the occupied count to the threshold
        caseA.add(Integer.valueOf(1));
        caseA.add(Integer.valueOf(2));

        // add the colliding value back and force the rehash
        caseA.add(COLLISION_2);
        Assertions.assertTrue(caseA.noneSatisfy(Predicates.greaterThan(COLLISION_2)));
    }

    @Override
    @Test
    public void noneSatisfyWith()
    {
        super.noneSatisfyWith();

        int size = MORE_COLLISIONS.size();
        for (int i = 1; i < size; i++)
        {
            MutableSet<Integer> set = this.newWith();
            set.addAll(MORE_COLLISIONS.subList(0, i));
            Assertions.assertTrue(set.noneSatisfyWith(Predicates2.lessThan(), MORE_COLLISIONS.subList(0, i).getFirst()));
            Assertions.assertFalse(set.noneSatisfyWith(Predicates2.greaterThanOrEqualTo(), MORE_COLLISIONS.subList(0, i).get(i - 1)));
        }

        // test noneSatisfyWith on a bucket with only one element
        MutableSet<Integer> set = this.newWith(COLLISION_1, COLLISION_2);
        set.remove(COLLISION_2);
        Assertions.assertFalse(set.noneSatisfyWith(Object::equals, COLLISION_1));
        Assertions.assertTrue(set.noneSatisfyWith(Object::equals, COLLISION_2));

        // Rehashing Case A: a bucket with only one entry and a low capacity forcing a rehash, where the triggering element goes in the bucket
        // set up a chained bucket
        MutableSet<Integer> caseA = this.newWith(COLLISION_1, COLLISION_2);
        // clear the bucket to one element
        caseA.remove(COLLISION_2);
        // increase the occupied count to the threshold
        caseA.add(Integer.valueOf(1));
        caseA.add(Integer.valueOf(2));

        // add the colliding value back and force the rehash
        caseA.add(COLLISION_2);
        Assertions.assertTrue(caseA.noneSatisfyWith(Predicates2.greaterThan(), COLLISION_2));
    }

    @Override
    @Test
    public void detect()
    {
        super.detect();

        int size = MORE_COLLISIONS.size();
        for (int i = 1; i < size; i++)
        {
            MutableSet<Integer> set = this.newWith();
            set.addAll(MORE_COLLISIONS.subList(0, i));
            Verify.assertItemAtIndex(set.detect(MORE_COLLISIONS.get(i - 1)::equals), i - 1, MORE_COLLISIONS);
        }

        // test detect on a bucket with only one element
        MutableSet<Integer> set = this.newWith(COLLISION_1, COLLISION_2);
        set.remove(COLLISION_2);
        Assertions.assertEquals(COLLISION_1, set.detect(COLLISION_1::equals));
        Assertions.assertNull(set.detect(COLLISION_2::equals));

        for (int i = 0; i < COLLISIONS.size(); i++)
        {
            MutableSet<Integer> rehashingSet = this.newWith();
            rehashingSet.addAll(COLLISIONS.subList(0, i));
            Integer last = COLLISIONS.subList(0, i).getLast();
            rehashingSet.remove(last);

            int rehashingSetSize = rehashingSet.size();
            for (int j = 0; j < rehashingSetSize; j++)
            {
                rehashingSet.add(Integer.valueOf(j + 1));
            }

            rehashingSet.add(last);
            Assertions.assertEquals(last, rehashingSet.detect(Predicates.equal(last)));
            Assertions.assertNull(rehashingSet.detect(Integer.valueOf(5)::equals));
        }
    }

    @Test
    public void iterator_increment_past_end()
    {
        assertThrows(NoSuchElementException.class, () -> {
            MutableSet<Integer> set = this.newWith();
            Iterator<Integer> iterator = set.iterator();
            iterator.next();
            iterator.next();
        });
    }

    @Test
    public void iterator_remove_without_next()
    {
        assertThrows(IllegalStateException.class, () -> {
            Iterator<Integer> iterator = this.<Integer>newWith().iterator();
            iterator.remove();
        });
    }

    @Override
    @Test
    public void toArray()
    {
        super.toArray();

        MutableSet<Integer> integers = this.newWith(1);
        Integer[] target = new Integer[3];
        target[0] = 2;
        target[1] = 2;
        target[2] = 2;
        integers.toArray(target);
        Assertions.assertArrayEquals(new Integer[]{1, null, 2}, target);
    }

    @Override
    @Test
    public void toSortedBag_natural_ordering()
    {
        RichIterable<Integer> integers = this.newWith(1, 2, 5, 3, 4);
        MutableSortedBag<Integer> bag = integers.toSortedBag();
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(1, 2, 3, 4, 5), bag);
    }

    @Override
    @Test
    public void toSortedBag_with_comparator()
    {
        RichIterable<Integer> integers = this.newWith(2, 4, 1, 3);
        MutableSortedBag<Integer> bag = integers.toSortedBag(Collections.<Integer>reverseOrder());
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(Collections.<Integer>reverseOrder(), 4, 3, 2, 1), bag);
    }

    @Override
    @Test
    public void toSortedBag_with_null()
    {
        assertThrows(NullPointerException.class, () -> {
            this.newWith(3, 4, null, 1, 2).toSortedBag();
        });
    }

    @Override
    @Test
    public void toSortedBagBy()
    {
        RichIterable<Integer> integers = this.newWith(2, 4, 1, 3);
        MutableSortedBag<Integer> bag = integers.toSortedBagBy(String::valueOf);
        Verify.assertSortedBagsEqual(TreeBag.newBagWith(1, 2, 3, 4), bag);
    }

    @Test
    public void frequentCollisions()
    {
        String[] expected = ArrayAdapter.adapt(FREQUENT_COLLISIONS)
                .subList(0, FREQUENT_COLLISIONS.length - 2)
                .toArray(new String[FREQUENT_COLLISIONS.length - 2]);
        MutableSet<String> set1 = this.newWith();
        MutableSet<String> set2 = this.newWith();

        Collections.addAll(set1, FREQUENT_COLLISIONS);
        Collections.addAll(set2, expected);

        set1.retainAll(set2);

        Assertions.assertArrayEquals(expected, set1.toArray());
    }
}
