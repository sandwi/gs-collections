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

import java.util.HashSet;
import java.util.Iterator;

import com.gs.collections.api.LazyIterable;
import com.gs.collections.api.block.HashingStrategy;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.set.ImmutableSet;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.set.Pool;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.block.factory.IntegerPredicates;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Procedures;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.factory.Sets;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.math.IntegerSum;
import com.gs.collections.impl.math.Sum;
import com.gs.collections.impl.math.SumProcedure;
import com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.test.domain.Key;
import com.gs.collections.impl.test.domain.Person;
import com.gs.collections.impl.utility.ArrayIterate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JUnit test suite for {@link UnifiedSetWithHashingStrategy}.
 */
public class UnifiedSetWithHashingStrategyTest extends AbstractUnifiedSetTestCase
{
    //Not using the static factory method in order to have concrete types for test cases
    private static final HashingStrategy<Integer> INTEGER_HASHING_STRATEGY = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<Integer>()
    {
        public int computeHashCode(Integer object)
        {
            return object.hashCode();
        }

        public boolean equals(Integer object1, Integer object2)
        {
            return object1.equals(object2);
        }
    });
    private static final HashingStrategy<String> STRING_HASHING_STRATEGY = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<String>()
    {
        public int computeHashCode(String object)
        {
            return object.hashCode();
        }

        public boolean equals(String object1, String object2)
        {
            return object1.equals(object2);
        }
    });
    private static final HashingStrategy<Person> FIRST_NAME_HASHING_STRATEGY = HashingStrategies.fromFunction(Person.TO_FIRST);
    private static final HashingStrategy<Person> LAST_NAME_HASHING_STRATEGY = HashingStrategies.fromFunction(Person.TO_LAST);

    private static final Person JOHNSMITH = new Person("John", "Smith");
    private static final Person JANESMITH = new Person("Jane", "Smith");
    private static final Person JOHNDOE = new Person("John", "Doe");
    private static final Person JANEDOE = new Person("Jane", "Doe");
    private static final ImmutableList<Person> PEOPLE = Lists.immutable.of(JOHNSMITH, JANESMITH, JOHNDOE, JANEDOE);
    private static final ImmutableSet<Person> LAST_NAME_HASHED_SET = Sets.immutable.of(JOHNSMITH, JOHNDOE);

    @Override
    protected <T> MutableSet<T> newWith(T... littleElements)
    {
        return UnifiedSetWithHashingStrategy.newSetWith(HashingStrategies.nullSafeHashingStrategy(HashingStrategies.<T>defaultStrategy()), littleElements);
    }

    @Test
    public void newSet_throws()
    {
        Verify.assertThrows(IllegalArgumentException.class, () -> new UnifiedSetWithHashingStrategy<Integer>(INTEGER_HASHING_STRATEGY, -1, 0.5f));
        Verify.assertThrows(IllegalArgumentException.class, () -> new UnifiedSetWithHashingStrategy<Integer>(INTEGER_HASHING_STRATEGY, 1, -0.5f));
        Verify.assertThrows(IllegalArgumentException.class, () -> new UnifiedSetWithHashingStrategy<Integer>(INTEGER_HASHING_STRATEGY, 1, 1.5f));
    }

    @Override
    @Test
    public void tap()
    {
        super.tap();

        MutableList<Person> tapResult = Lists.mutable.of();
        UnifiedSetWithHashingStrategy<Person> people = UnifiedSetWithHashingStrategy.newSet(LAST_NAME_HASHING_STRATEGY).withAll(PEOPLE.castToList());
        Assertions.assertSame(people, people.tap(tapResult::add));
        Assertions.assertEquals(people.toList(), tapResult);
    }

    @Override
    @Test
    public void select()
    {
        super.select();

        UnifiedSetWithHashingStrategy<Person> people = UnifiedSetWithHashingStrategy.newSet(LAST_NAME_HASHING_STRATEGY).withAll(PEOPLE.castToList());
        Verify.assertSetsEqual(LAST_NAME_HASHED_SET.castToSet(), people);
        Verify.assertSetsEqual(UnifiedSet.newSetWith(JOHNSMITH), people.select(each -> "Smith".equals(each.getLastName())).with(JANESMITH));
    }

    @Override
    @Test
    public void reject()
    {
        super.reject();

        UnifiedSetWithHashingStrategy<Person> people = UnifiedSetWithHashingStrategy.newSet(LAST_NAME_HASHING_STRATEGY).withAll(PEOPLE.castToList());
        Verify.assertSetsEqual(UnifiedSet.newSetWith(JOHNSMITH), people.reject(each -> "Doe".equals(each.getLastName())).with(JANESMITH));
    }

    /**
     * @deprecated since 3.0.
     */
    @Deprecated
    @Test
    public void lazyCollectForEach()
    {
        UnifiedSetWithHashingStrategy<Integer> integers =
                UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, 1, 2, 3, 4, 5);
        LazyIterable<String> select = integers.lazyCollect(String::valueOf);
        Procedure<String> builder = Procedures.append(new StringBuilder());
        select.forEach(builder);
        String result = builder.toString();
        Verify.assertContains(result, "1");
        Verify.assertContains(result, "2");
        Verify.assertContains(result, "3");
        Verify.assertContains(result, "4");
        Verify.assertContains(result, "5");
    }

    /**
     * @deprecated since 3.0.
     */
    @Deprecated
    @Test
    public void lazyRejectForEach()
    {
        UnifiedSetWithHashingStrategy<Integer> integers =
                UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, 1, 2, 3, 4, 5);
        LazyIterable<Integer> select = integers.lazyReject(Predicates.lessThan(5));
        Sum sum = new IntegerSum(0);
        select.forEach(new SumProcedure<>(sum));
        Assertions.assertEquals(5L, sum.getValue().intValue());
    }

    /**
     * @deprecated since 3.0.
     */
    @Deprecated
    @Test
    public void lazySelectForEach()
    {
        UnifiedSetWithHashingStrategy<Integer> integers =
                UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, 1, 2, 3, 4, 5);
        LazyIterable<Integer> select = integers.lazySelect(Predicates.lessThan(5));
        Sum sum = new IntegerSum(0);
        select.forEach(new SumProcedure<>(sum));
        Assertions.assertEquals(10, sum.getValue().intValue());
    }

    @Override
    @Test
    public void with()
    {
        Verify.assertEqualsAndHashCode(
                UnifiedSetWithHashingStrategy.newSetWith(STRING_HASHING_STRATEGY, "1"),
                UnifiedSetWithHashingStrategy.newSet(STRING_HASHING_STRATEGY).with("1"));
        Verify.assertEqualsAndHashCode(
                UnifiedSetWithHashingStrategy.newSetWith(STRING_HASHING_STRATEGY, "1", "2"),
                UnifiedSetWithHashingStrategy.newSet(STRING_HASHING_STRATEGY).with("1", "2"));
        Verify.assertEqualsAndHashCode(
                UnifiedSetWithHashingStrategy.newSetWith(STRING_HASHING_STRATEGY, "1", "2", "3"),
                UnifiedSetWithHashingStrategy.newSet(STRING_HASHING_STRATEGY).with("1", "2", "3"));
        Verify.assertEqualsAndHashCode(
                UnifiedSetWithHashingStrategy.newSetWith(STRING_HASHING_STRATEGY, "1", "2", "3", "4"),
                UnifiedSetWithHashingStrategy.newSet(STRING_HASHING_STRATEGY).with("1", "2", "3", "4"));

        MutableSet<String> list = UnifiedSetWithHashingStrategy.newSet(STRING_HASHING_STRATEGY).with("A")
                .withAll(Lists.fixedSize.of("1", "2"))
                .withAll(Lists.fixedSize.<String>of())
                .withAll(Sets.fixedSize.of("3", "4"));
        Verify.assertEqualsAndHashCode(UnifiedSetWithHashingStrategy.newSetWith(
                STRING_HASHING_STRATEGY, "A", "1", "2", "3", "4"), list);
    }

    @Test
    public void newSetWithIterable()
    {
        //testing collection
        MutableSet<Integer> integers = UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY, Interval.oneTo(3));
        Assertions.assertEquals(UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, 1, 2, 3), integers);

        //testing iterable
        UnifiedSetWithHashingStrategy<Integer> set1 = UnifiedSetWithHashingStrategy.newSet(
                INTEGER_HASHING_STRATEGY, FastList.newListWith(1, 2, 3).asLazy());
        Assertions.assertEquals(UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, 1, 2, 3), set1);

        //testing null
        Verify.assertThrows(NullPointerException.class, () -> UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY, null));
    }

    @Override
    @Test
    public void add()
    {
        super.add();

        // force rehashing at each step of adding a new colliding entry
        for (int i = 0; i < COLLISIONS.size(); i++)
        {
            UnifiedSetWithHashingStrategy<Integer> unifiedSet = UnifiedSetWithHashingStrategy.newSet(
                    INTEGER_HASHING_STRATEGY, i, 0.75f).withAll(COLLISIONS.subList(0, i));
            if (i == 2)
            {
                unifiedSet.add(Integer.valueOf(1));
            }
            if (i == 4)
            {
                unifiedSet.add(Integer.valueOf(1));
                unifiedSet.add(Integer.valueOf(2));
            }
            Integer value = COLLISIONS.get(i);
            Assertions.assertTrue(unifiedSet.add(value));
        }

        // Rehashing Case A: a bucket with only one entry and a low capacity forcing a rehash, where the trigging element goes in the bucket
        // set up a chained bucket
        UnifiedSetWithHashingStrategy<Integer> caseA = UnifiedSetWithHashingStrategy.newSet(
                INTEGER_HASHING_STRATEGY, 2).with(COLLISION_1, COLLISION_2);
        // clear the bucket to one element
        caseA.remove(COLLISION_2);
        // increase the occupied count to the threshold
        caseA.add(Integer.valueOf(1));
        caseA.add(Integer.valueOf(2));

        // add the colliding value back and force the rehash
        Assertions.assertTrue(caseA.add(COLLISION_2));

        // Rehashing Case B: a bucket with only one entry and a low capacity forcing a rehash, where the triggering element is not in the chain
        // set up a chained bucket
        UnifiedSetWithHashingStrategy<Integer> caseB = UnifiedSetWithHashingStrategy.newSet(
                INTEGER_HASHING_STRATEGY, 2).with(COLLISION_1, COLLISION_2);
        // clear the bucket to one element
        caseB.remove(COLLISION_2);
        // increase the occupied count to the threshold
        caseB.add(Integer.valueOf(1));
        caseB.add(Integer.valueOf(2));

        // add a new value and force the rehash
        Assertions.assertTrue(caseB.add(3));
    }

    @Test
    public void add_with_hashingStrategy()
    {
        HashingStrategy<Integer> hashingStrategy = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<Integer>()
        {
            public int computeHashCode(Integer object)
            {
                return object % 1000;
            }

            public boolean equals(Integer object1, Integer object2)
            {
                return object1.equals(object2);
            }
        });

        //Same as case A above except with a different hashing strategy
        UnifiedSetWithHashingStrategy<Integer> caseA = UnifiedSetWithHashingStrategy.newSet(hashingStrategy, 2);
        //Adding an element to a slot
        Assertions.assertTrue(caseA.add(COLLISION_1));
        //Setting up a chained bucked by forcing a collision
        Assertions.assertTrue(caseA.add(COLLISION_1 + 1000));
        //Increasing the occupied to the thresh hold
        Assertions.assertTrue(caseA.add(COLLISION_1 + 2000));
        //Forcing a rehash where the element that forced the rehash goes in the chained bucket
        Assertions.assertTrue(caseA.add(null));
        Verify.assertSetsEqual(UnifiedSet.newSetWith(COLLISION_1, COLLISION_1 + 1000, COLLISION_1 + 2000, null), caseA);

        //Same as case B above except with a different hashing strategy
        UnifiedSetWithHashingStrategy<Integer> caseB = UnifiedSetWithHashingStrategy.newSet(hashingStrategy, 2);
        //Adding an element to a slot
        Assertions.assertTrue(caseB.add(null));
        //Setting up a chained bucked by forcing a collision
        Assertions.assertTrue(caseB.add(1));
        //Increasing the occupied to the threshold
        Assertions.assertTrue(caseB.add(2));
        //Forcing a rehash where the element that forced the rehash does not go in the chained bucket
        Assertions.assertTrue(caseB.add(3));
        Verify.assertSetsEqual(UnifiedSet.newSetWith(null, 1, 2, 3), caseB);

        //Testing add throws NullPointerException if the hashingStrategy is not null safe
        Verify.assertThrows(NullPointerException.class, () -> UnifiedSetWithHashingStrategy.newSet(LAST_NAME_HASHING_STRATEGY).add(null));
    }

    @Override
    @Test
    public void addAllIterable()
    {
        super.addAllIterable();

        // test adding a fully populated chained bucket
        MutableSet<Integer> expected = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4, COLLISION_5, COLLISION_6, COLLISION_7);
        Assertions.assertTrue(UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY).addAllIterable(expected));

        // add an odd-sized collection to a set with a small max to ensure that its capacity is maintained after the operation.
        UnifiedSetWithHashingStrategy<Integer> tiny = UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY, 0);
        Assertions.assertTrue(tiny.addAllIterable(FastList.newListWith(COLLISION_1)));

        //Testing copying set with 3rd slot in chained bucket == null
        UnifiedSetWithHashingStrategy<Integer> integers = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4);
        UnifiedSetWithHashingStrategy<Integer> set = UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY);
        integers.remove(COLLISION_4);
        Assertions.assertTrue(set.addAllIterable(integers));
        Assertions.assertEquals(UnifiedSet.newSetWith(COLLISION_1, COLLISION_2, COLLISION_3), set);

        //Testing copying set with 2nd slot in chained bucket == null
        integers.remove(COLLISION_3);
        Assertions.assertFalse(set.addAllIterable(integers));

        //Testing copying set with the 1st slot in chained bucket == null
        integers.remove(COLLISION_2);
        Assertions.assertFalse(set.addAllIterable(integers));

        Assertions.assertEquals(UnifiedSet.newSetWith(COLLISION_1, COLLISION_2, COLLISION_3), set);
    }

    @Test
    public void addALLIterable_with_hashingStrategy()
    {
        UnifiedSetWithHashingStrategy<Person> people = UnifiedSetWithHashingStrategy.newSet(
                HashingStrategies.nullSafeHashingStrategy(LAST_NAME_HASHING_STRATEGY), 2);
        //Testing adding an iterable
        Assertions.assertTrue(people.addAllIterable(PEOPLE));
        Verify.assertSetsEqual(UnifiedSet.newSet(LAST_NAME_HASHED_SET), people);

        //Testing the set uses its own hashing strategy and not the target sets
        Assertions.assertFalse(people.addAllIterable(UnifiedSetWithHashingStrategy.newSet(FIRST_NAME_HASHING_STRATEGY, PEOPLE)));
        Verify.assertSize(2, people);

        //Testing adding with null where the call to addALLIterable forces a rehash
        Person notInSet = new Person("Not", "InSet");
        Assertions.assertTrue(people.addAllIterable(UnifiedSet.newSetWith(notInSet, null)));
        Verify.assertSetsEqual(UnifiedSet.newSet(LAST_NAME_HASHED_SET).with(notInSet, null), people);

        //Testing addAllIterable throws NullPointerException if the hashingStrategy is not null safe
        Verify.assertThrows(NullPointerException.class, () -> UnifiedSetWithHashingStrategy.newSet(LAST_NAME_HASHING_STRATEGY).addAllIterable(UnifiedSet.newSetWith((Person) null)));
    }

    @Test
    public void get()
    {
        UnifiedSetWithHashingStrategy<Integer> set = UnifiedSetWithHashingStrategy.newSet(
                INTEGER_HASHING_STRATEGY, SIZE).withAll(COLLISIONS);
        set.removeAll(COLLISIONS);
        for (Integer integer : COLLISIONS)
        {
            Assertions.assertNull(set.get(integer));
            Assertions.assertNull(set.get(null));
            set.add(integer);
            //noinspection UnnecessaryBoxing,CachedNumberConstructorCall,BoxingBoxedValue
            Assertions.assertSame(integer, set.get(Integer.valueOf(integer)));
        }
        Assertions.assertEquals(COLLISIONS.toSet(), set);

        // the pool interface supports getting null keys
        UnifiedSetWithHashingStrategy<Integer> chainedWithNull = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, null, COLLISION_1);
        Verify.assertContains(null, chainedWithNull);
        Assertions.assertNull(chainedWithNull.get(null));

        // getting a non-existent from a chain with one slot should short-circuit to return null
        UnifiedSetWithHashingStrategy<Integer> chainedWithOneSlot = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2);
        chainedWithOneSlot.remove(COLLISION_2);
        Assertions.assertNull(chainedWithOneSlot.get(COLLISION_2));
    }

    @Test
    public void get_with_hashingStrategy()
    {
        UnifiedSetWithHashingStrategy<Person> people = UnifiedSetWithHashingStrategy.newSet(
                HashingStrategies.nullSafeHashingStrategy(LAST_NAME_HASHING_STRATEGY), 2).withAll(PEOPLE.castToList());

        //Putting null then testing geting a null
        Verify.assertSize(3, people.with((Person) null));
        Assertions.assertNull(people.get(null));

        //Testing it is getting the same reference
        Assertions.assertSame(JOHNSMITH, people.get(JANESMITH));
        Assertions.assertSame(JOHNSMITH, people.get(JOHNSMITH));
        Assertions.assertSame(JOHNDOE, people.get(JANEDOE));
        Assertions.assertSame(JOHNDOE, people.get(JOHNDOE));

        Assertions.assertSame(JOHNSMITH, people.get(new Person("Anything", "Smith")));
        Assertions.assertNull(people.get(new Person("John", "NotHere")));

        //Testing get throws NullPointerException if the hashingStrategy is not null safe
        Verify.assertThrows(NullPointerException.class, () -> UnifiedSetWithHashingStrategy.newSet(LAST_NAME_HASHING_STRATEGY).get(null));
    }

    @Test
    public void put()
    {
        int size = MORE_COLLISIONS.size();
        for (int i = 1; i <= size; i++)
        {
            Pool<Integer> unifiedSet = UnifiedSetWithHashingStrategy.newSet(
                    INTEGER_HASHING_STRATEGY, 1).withAll(MORE_COLLISIONS.subList(0, i - 1));
            Integer newValue = MORE_COLLISIONS.get(i - 1);

            Assertions.assertSame(newValue, unifiedSet.put(newValue));
            //noinspection UnnecessaryBoxing,CachedNumberConstructorCall,BoxingBoxedValue
            Assertions.assertSame(newValue, unifiedSet.put(Integer.valueOf(newValue)));
        }

        // assert that all redundant puts into a each position of chain bucket return the original element added
        Pool<Integer> set = UnifiedSetWithHashingStrategy.newSet(
                INTEGER_HASHING_STRATEGY, 4).with(COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4);
        for (int i = 0; i < set.size(); i++)
        {
            Integer value = COLLISIONS.get(i);
            Assertions.assertSame(value, set.put(value));
        }

        // force rehashing at each step of putting a new colliding entry
        for (int i = 0; i < COLLISIONS.size(); i++)
        {
            Pool<Integer> pool = UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY, i).withAll(COLLISIONS.subList(0, i));
            if (i == 2)
            {
                pool.put(Integer.valueOf(1));
            }
            if (i == 4)
            {
                pool.put(Integer.valueOf(1));
                pool.put(Integer.valueOf(2));
            }
            Integer value = COLLISIONS.get(i);
            Assertions.assertSame(value, pool.put(value));
        }

        // cover one case not covered in the above: a bucket with only one entry and a low capacity forcing a rehash
        // set up a chained bucket
        Pool<Integer> pool = UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY, 2).with(COLLISION_1, COLLISION_2);
        // clear the bucket to one element
        pool.removeFromPool(COLLISION_2);
        // increase the occupied count to the threshold
        pool.put(Integer.valueOf(1));
        pool.put(Integer.valueOf(2));

        // put the colliding value back and force the rehash
        Assertions.assertSame(COLLISION_2, pool.put(COLLISION_2));

        // put chained items into a pool without causing a rehash
        Pool<Integer> olympicPool = UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY);
        Assertions.assertSame(COLLISION_1, olympicPool.put(COLLISION_1));
        Assertions.assertSame(COLLISION_2, olympicPool.put(COLLISION_2));
    }

    @Test
    public void put_with_hashingStrategy()
    {
        UnifiedSetWithHashingStrategy<Person> people = UnifiedSetWithHashingStrategy.newSet(
                HashingStrategies.nullSafeHashingStrategy(LAST_NAME_HASHING_STRATEGY), 2).withAll(PEOPLE.castToList());
        //Testing if element already exists, returns the instance in the set
        Assertions.assertSame(JOHNSMITH, people.put(new Person("Anything", "Smith")));
        Verify.assertSize(2, people);

        //Testing if the element doesn't exist, returns the element itself
        Person notInSet = new Person("Not", "inSet");
        Assertions.assertSame(notInSet, people.put(notInSet));
        Verify.assertSize(3, people);

        //Testing putting a null to force a rehash
        Assertions.assertNull(people.put(null));
        Verify.assertSize(4, people);

        //Testing put throws NullPointerException if the hashingStrategy is not null safe
        Verify.assertThrows(NullPointerException.class, () -> UnifiedSetWithHashingStrategy.newSet(LAST_NAME_HASHING_STRATEGY).put(null));
    }

    @Test
    public void remove_with_hashingStrategy()
    {
        HashingStrategy<Integer> hashingStrategy = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<Integer>()
        {
            public int computeHashCode(Integer object)
            {
                return object % 1000;
            }

            public boolean equals(Integer object1, Integer object2)
            {
                return object1.equals(object2);
            }
        });

        UnifiedSetWithHashingStrategy<Integer> integers = UnifiedSetWithHashingStrategy.newSet(
                hashingStrategy, 2).with(COLLISION_1, COLLISION_1 + 1000, COLLISION_1 + 2000, null);

        //Testing remove null from the end of the chain
        Assertions.assertTrue(integers.remove(null));

        //Adding null back and creating a deep chain.
        integers.with(null, COLLISION_1 + 3000, COLLISION_1 + 4000, COLLISION_1 + 5000);

        //Removing null from the first position of a bucket in the deep chain
        Assertions.assertTrue(integers.remove(null));
        Assertions.assertFalse(integers.remove(null));

        //Removing from the end of the deep chain
        Assertions.assertTrue(integers.remove(COLLISION_1 + 4000));

        //Removing from the first spot of the chain
        Assertions.assertTrue(integers.remove(COLLISION_1));
        Verify.assertSize(4, integers);

        //Testing removing a non existent element from a non bucket slot
        integers.add(2);
        integers.add(4);
        Assertions.assertFalse(integers.remove(1002));

        //Testing removeIf
        Assertions.assertTrue(integers.removeIf(IntegerPredicates.isEven()));
        Verify.assertEmpty(integers);
    }

    @Test
    public void removeFromPool()
    {
        Pool<Integer> unifiedSet = UnifiedSetWithHashingStrategy.newSet(
                INTEGER_HASHING_STRATEGY, 8).withAll(COLLISIONS);
        COLLISIONS.reverseForEach(each -> {
            Assertions.assertNull(unifiedSet.removeFromPool(null));
            Assertions.assertSame(each, unifiedSet.removeFromPool(each));
            Assertions.assertNull(unifiedSet.removeFromPool(each));
            Assertions.assertNull(unifiedSet.removeFromPool(null));
            Assertions.assertNull(unifiedSet.removeFromPool(COLLISION_10));
        });

        Assertions.assertEquals(UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY), unifiedSet);

        COLLISIONS.forEach(Procedures.cast(each -> {
            Pool<Integer> unifiedSet2 = UnifiedSetWithHashingStrategy.newSet(
                    INTEGER_HASHING_STRATEGY, 8).withAll(COLLISIONS);

            Assertions.assertNull(unifiedSet2.removeFromPool(null));
            Assertions.assertSame(each, unifiedSet2.removeFromPool(each));
            Assertions.assertNull(unifiedSet2.removeFromPool(each));
            Assertions.assertNull(unifiedSet2.removeFromPool(null));
            Assertions.assertNull(unifiedSet2.removeFromPool(COLLISION_10));
        }));

        // search a chain for a non-existent element
        Pool<Integer> chain = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4);
        Assertions.assertNull(chain.removeFromPool(COLLISION_5));

        // search a deep chain for a non-existent element
        Pool<Integer> deepChain = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4, COLLISION_5, COLLISION_6, COLLISION_7);
        Assertions.assertNull(deepChain.removeFromPool(COLLISION_8));

        // search for a non-existent element
        Pool<Integer> empty = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1);
        Assertions.assertNull(empty.removeFromPool(COLLISION_2));
    }

    @Test
    public void removeFromPool_with_hashingStrategy()
    {
        HashingStrategy<Integer> hashingStrategy = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<Integer>()
        {
            public int computeHashCode(Integer object)
            {
                return object % 1000;
            }

            public boolean equals(Integer object1, Integer object2)
            {
                return object1.equals(object2);
            }
        });

        UnifiedSetWithHashingStrategy<Integer> integers = UnifiedSetWithHashingStrategy.newSet(
                hashingStrategy, 2).with(COLLISION_1, COLLISION_1 + 1000, COLLISION_1 + 2000, null);

        //Testing remove null from the end of the chain
        Assertions.assertNull(integers.removeFromPool(null));

        Integer collision4000 = COLLISION_1 + 4000;
        //Adding null back and creating a deep chain.
        integers.with(null, COLLISION_1 + 3000, collision4000, COLLISION_1 + 5000);

        //Removing null from the first position of a bucket in the deep chain
        Assertions.assertNull(integers.removeFromPool(null));
        Verify.assertSize(6, integers);
        Assertions.assertNull(integers.removeFromPool(null));
        Verify.assertSize(6, integers);

        //Removing from the end of the deep chain
        Assertions.assertSame(collision4000, integers.removeFromPool(COLLISION_1 + 4000));

        //Removing from the first spot of the chain
        Assertions.assertSame(COLLISION_1, integers.removeFromPool(COLLISION_1));
        Verify.assertSize(4, integers);

        //Testing removing an element that is not in a chained bucket
        Assertions.assertSame(JOHNSMITH, UnifiedSetWithHashingStrategy.newSetWith(LAST_NAME_HASHING_STRATEGY, JOHNSMITH).removeFromPool(JOHNSMITH));
    }

    @Test
    public void serialization()
    {
        int size = COLLISIONS.size();
        for (int i = 1; i < size; i++)
        {
            MutableSet<Integer> set = UnifiedSetWithHashingStrategy.newSet(
                    INTEGER_HASHING_STRATEGY, SIZE).withAll(COLLISIONS.subList(0, i));
            Verify.assertPostSerializedEqualsAndHashCode(set);
            set.add(null);
            Verify.assertPostSerializedEqualsAndHashCode(set);
        }

        UnifiedSetWithHashingStrategy<Integer> nullBucketZero = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, null, COLLISION_1, COLLISION_2);
        Verify.assertPostSerializedEqualsAndHashCode(nullBucketZero);

        UnifiedSetWithHashingStrategy<Integer> simpleSetWithNull = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, null, 1, 2);
        Verify.assertPostSerializedEqualsAndHashCode(simpleSetWithNull);

        UnifiedSetWithHashingStrategy<Person> people = UnifiedSetWithHashingStrategy.newSet(LAST_NAME_HASHING_STRATEGY, PEOPLE);
        Verify.assertPostSerializedEqualsAndHashCode(people);
        //Testing the hashingStrategy is serialized correctly by making sure it is still hashing by last name
        Verify.assertSetsEqual(LAST_NAME_HASHED_SET.castToSet(), people.withAll(PEOPLE.castToList()));
    }

    @Test
    public void null_behavior()
    {
        UnifiedSetWithHashingStrategy<Integer> unifiedSet = UnifiedSetWithHashingStrategy.newSet(
                INTEGER_HASHING_STRATEGY, 8).withAll(MORE_COLLISIONS);
        MORE_COLLISIONS.clone().reverseForEach(each -> {
            Assertions.assertTrue(unifiedSet.add(null));
            Assertions.assertFalse(unifiedSet.add(null));
            Verify.assertContains(null, unifiedSet);
            Verify.assertPostSerializedEqualsAndHashCode(unifiedSet);

            Assertions.assertTrue(unifiedSet.remove(null));
            Assertions.assertFalse(unifiedSet.remove(null));
            Verify.assertNotContains(null, unifiedSet);

            Verify.assertPostSerializedEqualsAndHashCode(unifiedSet);

            Assertions.assertNull(unifiedSet.put(null));
            Assertions.assertNull(unifiedSet.put(null));
            Assertions.assertNull(unifiedSet.removeFromPool(null));
            Assertions.assertNull(unifiedSet.removeFromPool(null));

            Verify.assertContains(each, unifiedSet);
            Assertions.assertTrue(unifiedSet.remove(each));
            Assertions.assertFalse(unifiedSet.remove(each));
            Verify.assertNotContains(each, unifiedSet);
        });
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        super.equalsAndHashCode();

        UnifiedSetWithHashingStrategy<Integer> singleCollisionBucket = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2);
        singleCollisionBucket.remove(COLLISION_2);
        Assertions.assertEquals(singleCollisionBucket, UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1));

        Verify.assertEqualsAndHashCode(
                UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, null, COLLISION_1, COLLISION_2, COLLISION_3),
                UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, null, COLLISION_1, COLLISION_2, COLLISION_3));
        Verify.assertEqualsAndHashCode(
                UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, COLLISION_1, null, COLLISION_2, COLLISION_3),
                UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, COLLISION_1, null, COLLISION_2, COLLISION_3));
        Verify.assertEqualsAndHashCode(
                UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, null, COLLISION_3),
                UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, null, COLLISION_3));
        Verify.assertEqualsAndHashCode(
                UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, null),
                UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, null));
    }

    @Test
    public void equals_with_hashingStrategy()
    {
        HashingStrategy<Person> personHashingStrategy = HashingStrategies.fromFunction(Person.TO_LAST);
        HashingStrategy<Person> personHashingStrategyCopy = HashingStrategies.fromFunction(Person.TO_LAST);

        UnifiedSetWithHashingStrategy<Person> setA = UnifiedSetWithHashingStrategy.newSet(personHashingStrategy, PEOPLE);
        UnifiedSetWithHashingStrategy<Person> setB = UnifiedSetWithHashingStrategy.newSet(personHashingStrategyCopy, PEOPLE);

        //Test sets with different instances of the same hashing strategy are equal symmetrically
        Verify.assertEqualsAndHashCode(setA, setB);

        //Checking that a hashing set is symmetrically equal to an identical JDK set
        HashSet<Person> hashSet = new HashSet<>(setA);
        Assertions.assertTrue(hashSet.equals(setA) && setA.equals(hashSet));

        //Checking that a hash set is symmetrically equal to an identical GS Collections set
        UnifiedSet<Person> unifiedSet = UnifiedSet.newSet(setA);
        Assertions.assertTrue(unifiedSet.equals(setA) && setA.equals(unifiedSet));

        //Testing the asymmetry of equals
        HashingStrategy<String> firstLetterHashingStrategy = new HashingStrategy<String>()
        {
            public int computeHashCode(String object)
            {
                return Character.valueOf(object.charAt(0));
            }

            public boolean equals(String object1, String object2)
            {
                return object1.charAt(0) == object2.charAt(0);
            }
        };

        UnifiedSetWithHashingStrategy<String> hashedString = UnifiedSetWithHashingStrategy.newSetWith(firstLetterHashingStrategy, "apple", "banana", "cheese");
        UnifiedSetWithHashingStrategy<String> anotherHashedString =
                UnifiedSetWithHashingStrategy.newSetWith(firstLetterHashingStrategy, "a", "b", "c");
        UnifiedSet<String> normalString = UnifiedSet.newSetWith("alpha", "bravo", "charlie");

        //Testing hashedString equals normalString
        Assertions.assertTrue(hashedString.equals(normalString) && hashedString.equals(hashedString));
        //Testing normalString does not equal a hashedString, note cannot use Assert.notEquals because it assumes symmetric equals behavior
        Assertions.assertFalse(normalString.equals(hashedString) && hashedString.equals(normalString));
        //Testing 2 sets with same hashing strategies must obey object equals definition
        Verify.assertEqualsAndHashCode(hashedString, anotherHashedString);
        //Testing set size matters
        Assertions.assertNotEquals(hashedString, normalString.remove("alpha"));
    }

    @Test
    public void constructor_from_UnifiedSet()
    {
        Verify.assertEqualsAndHashCode(new HashSet<>(MORE_COLLISIONS),
                UnifiedSetWithHashingStrategy.newSet(
                        INTEGER_HASHING_STRATEGY,
                        MORE_COLLISIONS));
    }

    @Test
    public void copyConstructor()
    {
        // test copying a chained bucket
        UnifiedSetWithHashingStrategy<Integer> set = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4, COLLISION_5, COLLISION_6, COLLISION_7);
        Verify.assertEqualsAndHashCode(set, UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY, set));
    }

    @Test
    public void newSet_null()
    {
        assertThrows(NullPointerException.class, () -> {
            UnifiedSetWithHashingStrategy.newSet((UnifiedSetWithHashingStrategy<Object>) null);
        });
    }

    @Test
    public void newSet_null_hashingStrategy()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            UnifiedSetWithHashingStrategy.newSet((HashingStrategy<Object>) null);
        });
    }

    @Test
    public void batchForEach()
    {
        //Testing batch size of 1 to 16 with no chains
        UnifiedSet<Integer> set = UnifiedSet.<Integer>newSet(10).with(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        for (int sectionCount = 1; sectionCount <= 16; ++sectionCount)
        {
            Sum sum = new IntegerSum(0);
            for (int sectionIndex = 0; sectionIndex < sectionCount; ++sectionIndex)
            {
                set.batchForEach(new SumProcedure<>(sum), sectionIndex, sectionCount);
            }
            Assertions.assertEquals(55, sum.getValue());
        }

        //Testing 1 batch with chains
        Sum sum2 = new IntegerSum(0);
        UnifiedSetWithHashingStrategy<Integer> set2 = UnifiedSetWithHashingStrategy.newSet(
                INTEGER_HASHING_STRATEGY, 3).with(COLLISION_1, COLLISION_2, COLLISION_3, 1, 2);
        int numBatches = set2.getBatchCount(100);
        for (int i = 0; i < numBatches; ++i)
        {
            set2.batchForEach(new SumProcedure<>(sum2), i, numBatches);
        }
        Assertions.assertEquals(1, numBatches);
        Assertions.assertEquals(54, sum2.getValue());

        //Testing batch size of 3 with chains and uneven last batch
        Sum sum3 = new IntegerSum(0);
        UnifiedSetWithHashingStrategy<Integer> set3 = UnifiedSetWithHashingStrategy.newSet(
                INTEGER_HASHING_STRATEGY, 4, 1.0F).with(COLLISION_1, COLLISION_2, 1, 2, 3, 4, 5);
        int numBatches2 = set3.getBatchCount(3);
        for (int i = 0; i < numBatches2; ++i)
        {
            set3.batchForEach(new SumProcedure<>(sum3), i, numBatches2);
        }
        Assertions.assertEquals(32, sum3.getValue());

        //Test batchForEach on empty set, it should simply do nothing and not throw any exceptions
        Sum sum4 = new IntegerSum(0);
        UnifiedSetWithHashingStrategy<Integer> set4 = UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY);
        set4.batchForEach(new SumProcedure<>(sum4), 0, set4.getBatchCount(1));
        Assertions.assertEquals(0, sum4.getValue());
    }

    @Override
    @Test
    public void toArray()
    {
        super.toArray();

        int size = COLLISIONS.size();
        for (int i = 1; i < size; i++)
        {
            MutableSet<Integer> set = UnifiedSetWithHashingStrategy.newSet(
                    INTEGER_HASHING_STRATEGY, SIZE).withAll(COLLISIONS.subList(0, i));
            Object[] objects = set.toArray();
            Assertions.assertEquals(set, UnifiedSet.newSetWith(objects));
        }

        MutableSet<Integer> deepChain = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4, COLLISION_5, COLLISION_6);
        Assertions.assertArrayEquals(new Integer[]{COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4, COLLISION_5, COLLISION_6}, deepChain.toArray());

        MutableSet<Integer> minimumChain = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2);
        minimumChain.remove(COLLISION_2);
        Assertions.assertArrayEquals(new Integer[]{COLLISION_1}, minimumChain.toArray());

        MutableSet<Integer> set = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4);
        Integer[] target = {Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(1)};
        Integer[] actual = set.toArray(target);
        ArrayIterate.sort(actual, actual.length, Comparators.safeNullsHigh(Integer::compareTo));
        Assertions.assertArrayEquals(new Integer[]{COLLISION_1, 1, COLLISION_2, COLLISION_3, COLLISION_4, null}, actual);
    }

    @Test
    public void iterator_remove()
    {
        int size = MORE_COLLISIONS.size();
        for (int i = 0; i < size; i++)
        {
            MutableSet<Integer> actual = UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY, SIZE).withAll(MORE_COLLISIONS);
            Iterator<Integer> iterator = actual.iterator();
            for (int j = 0; j <= i; j++)
            {
                Assertions.assertTrue(iterator.hasNext());
                iterator.next();
            }
            iterator.remove();

            MutableSet<Integer> expected = UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY, MORE_COLLISIONS);
            expected.remove(MORE_COLLISIONS.get(i));
            Assertions.assertEquals(expected, actual);
        }

        // remove the last element from within a 2-level long chain that is fully populated
        MutableSet<Integer> set = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4, COLLISION_5, COLLISION_6, COLLISION_7);
        Iterator<Integer> iterator1 = set.iterator();
        for (int i = 0; i < 7; i++)
        {
            iterator1.next();
        }
        iterator1.remove();
        Assertions.assertEquals(UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4, COLLISION_5, COLLISION_6), set);

        // remove the second-to-last element from a 2-level long chain that that has one empty slot
        Iterator<Integer> iterator2 = set.iterator();
        for (int i = 0; i < 6; i++)
        {
            iterator2.next();
        }
        iterator2.remove();
        Assertions.assertEquals(UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4, COLLISION_5), set);

        //Testing removing the last element in a fully populated chained bucket
        UnifiedSetWithHashingStrategy<Integer> set2 = UnifiedSetWithHashingStrategy.newSetWith(
                INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2, COLLISION_3, COLLISION_4);
        Iterator<Integer> iterator3 = set2.iterator();
        for (int i = 0; i < 3; ++i)
        {
            iterator3.next();
        }
        iterator3.next();
        iterator3.remove();
        Verify.assertSetsEqual(UnifiedSet.newSetWith(COLLISION_1, COLLISION_2, COLLISION_3), set2);
    }

    @Test
    public void setKeyPreservation()
    {
        Key key = new Key("key");

        Key duplicateKey1 = new Key("key");
        MutableSet<Key> set1 = UnifiedSetWithHashingStrategy.newSet(
                HashingStrategies.<Key>defaultStrategy()).with(key, duplicateKey1);
        Verify.assertSize(1, set1);
        Verify.assertContains(key, set1);
        Assertions.assertSame(key, set1.getFirst());

        Key duplicateKey2 = new Key("key");
        MutableSet<Key> set2 = UnifiedSetWithHashingStrategy.newSet(
                HashingStrategies.<Key>defaultStrategy()).with(key, duplicateKey1, duplicateKey2);
        Verify.assertSize(1, set2);
        Verify.assertContains(key, set2);
        Assertions.assertSame(key, set2.getFirst());

        Key duplicateKey3 = new Key("key");
        MutableSet<Key> set3 = UnifiedSetWithHashingStrategy.newSet(
                HashingStrategies.<Key>defaultStrategy()).with(key, new Key("not a dupe"), duplicateKey3);
        Verify.assertSize(2, set3);
        Verify.assertContainsAll(set3, key, new Key("not a dupe"));
        Assertions.assertSame(key, set3.detect(key::equals));
    }

    @Test
    public void withSameIfNotModified()
    {
        UnifiedSetWithHashingStrategy<Integer> integers = UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY);
        Assertions.assertEquals(UnifiedSet.newSetWith(1, 2), integers.with(1, 2));
        Assertions.assertEquals(UnifiedSet.newSetWith(1, 2, 3, 4), integers.with(2, 3, 4));
        Assertions.assertSame(integers, integers.with(5, 6, 7));
    }

    @Override
    @Test
    public void retainAll()
    {
        super.retainAll();

        MutableSet<Object> setWithNull = this.newWith((Object) null);
        Assertions.assertFalse(setWithNull.retainAll(FastList.newListWith((Object) null)));
        Assertions.assertEquals(UnifiedSet.newSetWith((Object) null), setWithNull);
    }

    @Override
    @Test
    public void getFirst()
    {
        super.getFirst();

        int size = MORE_COLLISIONS.size();
        for (int i = 1; i <= size - 1; i++)
        {
            MutableSet<Integer> unifiedSet = UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY, 1).withAll(MORE_COLLISIONS.subList(0, i));
            Assertions.assertSame(MORE_COLLISIONS.get(0), unifiedSet.getFirst());
        }
    }

    @Override
    @Test
    public void getLast()
    {
        super.getLast();

        int size = MORE_COLLISIONS.size();
        for (int i = 1; i <= size - 1; i++)
        {
            MutableSet<Integer> unifiedSet = UnifiedSetWithHashingStrategy.newSet(INTEGER_HASHING_STRATEGY, 1).withAll(MORE_COLLISIONS.subList(0, i));
            Assertions.assertSame(MORE_COLLISIONS.get(i - 1), unifiedSet.getLast());
        }

        MutableSet<Integer> chainedWithOneSlot = UnifiedSetWithHashingStrategy.newSetWith(INTEGER_HASHING_STRATEGY, COLLISION_1, COLLISION_2);
        chainedWithOneSlot.remove(COLLISION_2);
        Assertions.assertSame(COLLISION_1, chainedWithOneSlot.getLast());
    }
}
