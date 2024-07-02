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

package com.gs.collections.impl.multimap;

import com.gs.collections.api.block.HashingStrategy;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.multimap.bag.MutableBagMultimap;
import com.gs.collections.api.multimap.set.MutableSetMultimap;
import com.gs.collections.api.set.ImmutableSet;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.factory.Sets;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.multimap.bag.HashBagMultimap;
import com.gs.collections.impl.multimap.set.AbstractMutableSetMultimapTestCase;
import com.gs.collections.impl.multimap.set.UnifiedSetMultimap;
import com.gs.collections.impl.multimap.set.strategy.UnifiedSetWithHashingStrategyMultimap;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy;
import com.gs.collections.impl.test.SerializeTestHelper;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.test.domain.Person;
import com.gs.collections.impl.tuple.Tuples;
import com.gs.collections.impl.utility.Iterate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test of {@link UnifiedSetWithHashingStrategyMultimap}.
 */
public class UnifiedSetWithHashingStrategyMultimapTest extends AbstractMutableSetMultimapTestCase
{
    private static final HashingStrategy<Person> LAST_NAME_STRATEGY = HashingStrategies.fromFunction(Person.TO_LAST);

    private static final HashingStrategy<Person> FIRST_NAME_STRATEGY = HashingStrategies.fromFunction(Person.TO_FIRST);

    private static final Person JOHNSMITH = new Person("John", "Smith");
    private static final Person JANESMITH = new Person("Jane", "Smith");
    private static final Person JOHNDOE = new Person("John", "Doe");
    private static final Person JANEDOE = new Person("Jane", "Doe");
    private static final ImmutableList<Person> PEOPLE = Lists.immutable.of(JOHNSMITH, JANESMITH, JOHNDOE, JANEDOE);
    private static final ImmutableSet<Person> LAST_NAME_HASHED_SET = Sets.immutable.of(JOHNSMITH, JOHNDOE);

    @Override
    public <K, V> UnifiedSetWithHashingStrategyMultimap<K, V> newMultimap()
    {
        return UnifiedSetWithHashingStrategyMultimap.newMultimap(HashingStrategies.<V>defaultStrategy());
    }

    @Override
    public <K, V> UnifiedSetWithHashingStrategyMultimap<K, V> newMultimapWithKeyValue(K key, V value)
    {
        UnifiedSetWithHashingStrategyMultimap<K, V> mutableMultimap = this.newMultimap();
        mutableMultimap.put(key, value);
        return mutableMultimap;
    }

    @Override
    public <K, V> UnifiedSetWithHashingStrategyMultimap<K, V> newMultimapWithKeysValues(K key1, V value1, K key2, V value2)
    {
        UnifiedSetWithHashingStrategyMultimap<K, V> mutableMultimap = this.newMultimap();
        mutableMultimap.put(key1, value1);
        mutableMultimap.put(key2, value2);
        return mutableMultimap;
    }

    @Override
    public <K, V> UnifiedSetWithHashingStrategyMultimap<K, V> newMultimapWithKeysValues(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3)
    {
        UnifiedSetWithHashingStrategyMultimap<K, V> mutableMultimap = this.newMultimap();
        mutableMultimap.put(key1, value1);
        mutableMultimap.put(key2, value2);
        mutableMultimap.put(key3, value3);
        return mutableMultimap;
    }

    @Override
    public <K, V> UnifiedSetWithHashingStrategyMultimap<K, V> newMultimapWithKeysValues(
            K key1, V value1,
            K key2, V value2,
            K key3, V value3,
            K key4, V value4)
    {
        UnifiedSetWithHashingStrategyMultimap<K, V> mutableMultimap = this.newMultimap();
        mutableMultimap.put(key1, value1);
        mutableMultimap.put(key2, value2);
        mutableMultimap.put(key3, value3);
        mutableMultimap.put(key4, value4);
        return mutableMultimap;
    }

    @Override
    protected final <K, V> UnifiedSetWithHashingStrategyMultimap<K, V> newMultimap(Pair<K, V>... pairs)
    {
        return UnifiedSetWithHashingStrategyMultimap.newMultimap(HashingStrategies.<V>defaultStrategy(), pairs);
    }

    @Override
    protected <K, V> UnifiedSetWithHashingStrategyMultimap<K, V> newMultimapFromPairs(Iterable<Pair<K, V>> inputIterable)
    {
        return UnifiedSetWithHashingStrategyMultimap.newMultimap(HashingStrategies.<V>defaultStrategy(), inputIterable);
    }

    @Override
    protected final <V> MutableSet<V> createCollection(V... args)
    {
        return UnifiedSetWithHashingStrategy.newSetWith(HashingStrategies.<V>defaultStrategy(), args);
    }

    @Override
    @Test
    public void testClear()
    {
        UnifiedSetWithHashingStrategyMultimap<Integer, String> map = this.newMultimapWithKeysValues(1, "1", 1, "One", 2, "2", 2, "Two");
        map.clear();
        Verify.assertEmpty(map);
    }

    @Test
    public void testHashingStrategyConstructors()
    {
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> peopleMap = UnifiedSetWithHashingStrategyMultimap.newMultimap(HashingStrategies.<Person>defaultStrategy());
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> lastNameMap = UnifiedSetWithHashingStrategyMultimap.newMultimap(LAST_NAME_STRATEGY);
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> firstNameMap = UnifiedSetWithHashingStrategyMultimap.newMultimap(FIRST_NAME_STRATEGY);
        peopleMap.putAll(1, PEOPLE);
        lastNameMap.putAll(1, PEOPLE);
        firstNameMap.putAll(1, PEOPLE);

        Verify.assertSetsEqual(PEOPLE.toSet(), peopleMap.get(1));
        Verify.assertSetsEqual(UnifiedSet.newSetWith(JOHNSMITH, JANESMITH), firstNameMap.get(1));
        Verify.assertSetsEqual(LAST_NAME_HASHED_SET.castToSet(), lastNameMap.get(1));
    }

    @Test
    public void testMultimapConstructor()
    {
        MutableSetMultimap<Integer, Person> map = UnifiedSetMultimap.newMultimap();
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> map2 = UnifiedSetWithHashingStrategyMultimap.newMultimap(LAST_NAME_STRATEGY);
        for (Person person : PEOPLE)
        {
            map.put(1, person);
            map2.put(1, person);
        }

        UnifiedSetWithHashingStrategyMultimap<Integer, Person> hashingMap =
                UnifiedSetWithHashingStrategyMultimap.newMultimap(LAST_NAME_STRATEGY, map);
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> hashingMap2 = UnifiedSetWithHashingStrategyMultimap.newMultimap(map2);

        Verify.assertSetsEqual(hashingMap.get(1), hashingMap2.get(1));
        Assertions.assertSame(hashingMap.getValueHashingStrategy(), hashingMap2.getValueHashingStrategy());
    }

    @Test
    public void testNewEmpty()
    {
        UnifiedMap<Integer, MutableSet<Person>> expected = UnifiedMap.newMap();
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> lastNameMap = UnifiedSetWithHashingStrategyMultimap.newMultimap(LAST_NAME_STRATEGY);
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> newEmptyMap = lastNameMap.newEmpty();
        for (int i = 1; i < 4; ++i)
        {
            expected.put(i, LAST_NAME_HASHED_SET.toSet());
            lastNameMap.putAll(i, PEOPLE);
            newEmptyMap.putAll(i, PEOPLE);
        }

        Verify.assertMapsEqual(expected, lastNameMap.getMap());
        Verify.assertMapsEqual(expected, newEmptyMap.getMap());
        Assertions.assertSame(LAST_NAME_STRATEGY, lastNameMap.getValueHashingStrategy());
        Assertions.assertSame(LAST_NAME_STRATEGY, newEmptyMap.getValueHashingStrategy());
    }

    @Override
    @Test
    public void serialization()
    {
        super.serialization();

        UnifiedSetWithHashingStrategyMultimap<Object, Person> lastNameMap = UnifiedSetWithHashingStrategyMultimap.newMultimap(LAST_NAME_STRATEGY);
        lastNameMap.putAll(1, PEOPLE);
        lastNameMap.putAll(2, PEOPLE.toList().reverseThis());

        Verify.assertPostSerializedEqualsAndHashCode(lastNameMap);
        UnifiedSetWithHashingStrategyMultimap<Object, Person> deserialized = SerializeTestHelper.serializeDeserialize(lastNameMap);
        Verify.assertSetsEqual(LAST_NAME_HASHED_SET.castToSet(), deserialized.get(1));
        Verify.assertSetsEqual(UnifiedSet.newSetWith(JANEDOE, JANESMITH), deserialized.get(2));

        deserialized.putAll(3, PEOPLE);
        Verify.assertSetsEqual(LAST_NAME_HASHED_SET.castToSet(), deserialized.get(3));
    }

    @Override
    @Test
    public void selectKeysValues()
    {
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> multimap = UnifiedSetWithHashingStrategyMultimap.newMultimap(FIRST_NAME_STRATEGY);
        multimap.put(1, JOHNSMITH);
        multimap.put(1, JANESMITH);
        multimap.put(1, JOHNDOE);
        multimap.put(1, JANEDOE);
        multimap.put(2, JOHNSMITH);
        multimap.put(2, JANESMITH);
        multimap.put(2, JOHNDOE);
        multimap.put(2, JANEDOE);

        UnifiedSetWithHashingStrategyMultimap<Integer, Person> selectedMultimap = multimap.selectKeysValues((key, value) -> (key % 2 == 0) && "Jane".equals(value.getFirstName()));
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> expectedMultimap = UnifiedSetWithHashingStrategyMultimap.newMultimap(FIRST_NAME_STRATEGY);
        expectedMultimap.put(2, JANESMITH);
        expectedMultimap.put(2, JANEDOE);
        Assertions.assertEquals(expectedMultimap, selectedMultimap);
        Verify.assertMapsEqual(expectedMultimap.getMap(), selectedMultimap.getMap());
        Assertions.assertSame(expectedMultimap.getValueHashingStrategy(), selectedMultimap.getValueHashingStrategy());
    }

    @Override
    @Test
    public void rejectKeysValues()
    {
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> multimap = UnifiedSetWithHashingStrategyMultimap.newMultimap(FIRST_NAME_STRATEGY);
        multimap.put(1, JOHNSMITH);
        multimap.put(1, JANESMITH);
        multimap.put(1, JOHNDOE);
        multimap.put(1, JANEDOE);
        multimap.put(2, JOHNSMITH);
        multimap.put(2, JANESMITH);
        multimap.put(2, JOHNDOE);
        multimap.put(2, JANEDOE);

        UnifiedSetWithHashingStrategyMultimap<Integer, Person> rejectedMultimap = multimap.rejectKeysValues((key, value) -> (key % 2 == 0) || "Jane".equals(value.getFirstName()));
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> expectedMultimap = UnifiedSetWithHashingStrategyMultimap.newMultimap(FIRST_NAME_STRATEGY);
        expectedMultimap.put(1, JOHNSMITH);
        expectedMultimap.put(1, JOHNDOE);
        Assertions.assertEquals(expectedMultimap, rejectedMultimap);
        Verify.assertMapsEqual(expectedMultimap.getMap(), rejectedMultimap.getMap());
        Assertions.assertSame(expectedMultimap.getValueHashingStrategy(), rejectedMultimap.getValueHashingStrategy());
    }

    @Override
    @Test
    public void selectKeysMultiValues()
    {
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> multimap = UnifiedSetWithHashingStrategyMultimap.newMultimap(FIRST_NAME_STRATEGY);
        multimap.put(1, JOHNSMITH);
        multimap.put(1, JANESMITH);
        multimap.put(1, JOHNDOE);
        multimap.put(1, JANEDOE);
        multimap.put(2, JANESMITH);
        multimap.put(2, JOHNDOE);
        multimap.put(2, JANEDOE);
        multimap.put(3, JANESMITH);
        multimap.put(3, JOHNDOE);
        multimap.put(3, JANEDOE);
        multimap.put(4, JOHNSMITH);
        multimap.put(4, JOHNDOE);

        UnifiedSetWithHashingStrategyMultimap<Integer, Person> selectedMultimap = multimap.selectKeysMultiValues((key, values) -> key % 2 == 0 && Iterate.contains(values, JANEDOE));
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> expectedMultimap = UnifiedSetWithHashingStrategyMultimap.newMultimap(FIRST_NAME_STRATEGY);
        expectedMultimap.put(2, JANESMITH);
        expectedMultimap.put(2, JANEDOE);
        expectedMultimap.put(2, JOHNDOE);
        Assertions.assertEquals(expectedMultimap, selectedMultimap);
        Verify.assertMapsEqual(expectedMultimap.getMap(), selectedMultimap.getMap());
        Assertions.assertSame(expectedMultimap.getValueHashingStrategy(), selectedMultimap.getValueHashingStrategy());
    }

    @Override
    @Test
    public void rejectKeysMultiValues()
    {
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> multimap = UnifiedSetWithHashingStrategyMultimap.newMultimap(FIRST_NAME_STRATEGY);
        multimap.put(1, JANESMITH);
        multimap.put(1, JOHNDOE);
        multimap.put(1, JANEDOE);
        multimap.put(1, JANEDOE);
        multimap.put(2, JANESMITH);
        multimap.put(2, JOHNSMITH);
        multimap.put(2, JOHNDOE);
        multimap.put(2, JANEDOE);
        multimap.put(3, JOHNSMITH);
        multimap.put(3, JOHNDOE);
        multimap.put(4, JOHNSMITH);
        multimap.put(4, JOHNDOE);

        UnifiedSetWithHashingStrategyMultimap<Integer, Person> rejectedMultimap = multimap.rejectKeysMultiValues((key, values) -> key % 2 == 0 || Iterate.contains(values, JANEDOE));
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> expectedMultimap = UnifiedSetWithHashingStrategyMultimap.newMultimap(FIRST_NAME_STRATEGY);
        expectedMultimap.put(3, JOHNSMITH);
        expectedMultimap.put(3, JOHNDOE);
        Assertions.assertEquals(expectedMultimap, rejectedMultimap);
        Verify.assertMapsEqual(expectedMultimap.getMap(), rejectedMultimap.getMap());
        Assertions.assertSame(expectedMultimap.getValueHashingStrategy(), rejectedMultimap.getValueHashingStrategy());
    }

    @Override
    @Test
    public void collectKeysValues()
    {
        super.collectKeysValues();

        UnifiedSetWithHashingStrategyMultimap<Integer, Person> multimap = UnifiedSetWithHashingStrategyMultimap.newMultimap(FIRST_NAME_STRATEGY);
        multimap.put(1, JANESMITH);
        multimap.put(1, JOHNDOE);
        multimap.put(1, JANEDOE);
        multimap.put(1, JANEDOE);
        multimap.put(2, JANESMITH);
        multimap.put(2, JOHNSMITH);
        multimap.put(2, JOHNDOE);
        multimap.put(2, JANEDOE);

        MutableBagMultimap<String, Integer> collectedMultimap1 = multimap.collectKeysValues((key, value) -> Tuples.pair(key.toString(), key * value.getAge()));
        MutableBagMultimap<String, Integer> expectedMultimap1 = HashBagMultimap.newMultimap();
        expectedMultimap1.put("1", 100);
        expectedMultimap1.put("1", 100);
        expectedMultimap1.put("2", 200);
        expectedMultimap1.put("2", 200);

        Assertions.assertEquals(expectedMultimap1, collectedMultimap1);

        MutableBagMultimap<String, Integer> collectedMultimap2 = multimap.collectKeysValues((key, value) -> Tuples.pair("1", key * value.getAge()));
        MutableBagMultimap<String, Integer> expectedMultimap2 = HashBagMultimap.newMultimap();
        expectedMultimap2.put("1", 100);
        expectedMultimap2.put("1", 100);
        expectedMultimap2.put("1", 200);
        expectedMultimap2.put("1", 200);

        Assertions.assertEquals(expectedMultimap2, collectedMultimap2);
    }

    @Override
    @Test
    public void collectValues()
    {
        UnifiedSetWithHashingStrategyMultimap<Integer, Person> multimap = UnifiedSetWithHashingStrategyMultimap.newMultimap(FIRST_NAME_STRATEGY);
        multimap.put(1, JANESMITH);
        multimap.put(1, JOHNDOE);
        multimap.put(1, JANEDOE);
        multimap.put(1, JANEDOE);
        multimap.put(2, JANESMITH);
        multimap.put(2, JOHNSMITH);
        multimap.put(2, JOHNDOE);
        multimap.put(2, JANEDOE);

        MutableBagMultimap<Integer, Integer> collectedMultimap = multimap.collectValues(Person::getAge);
        MutableBagMultimap<Integer, Integer> expectedMultimap = HashBagMultimap.newMultimap();
        expectedMultimap.put(1, 100);
        expectedMultimap.put(2, 100);

        Assertions.assertEquals(expectedMultimap, collectedMultimap);
    }
}
