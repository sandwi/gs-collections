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

package com.gs.collections.impl.map.sorted.immutable;

import java.util.Comparator;
import java.util.NoSuchElementException;

import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.map.ImmutableMap;
import com.gs.collections.api.map.MapIterable;
import com.gs.collections.api.map.sorted.ImmutableSortedMap;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.function.PassThruFunction0;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.factory.SortedMaps;
import com.gs.collections.impl.test.SerializeTestHelper;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JUnit test for {@link ImmutableEmptySortedMap}.
 */
public class ImmutableEmptySortedMapTest extends ImmutableSortedMapTestCase
{
    @Override
    protected ImmutableSortedMap<Integer, String> classUnderTest()
    {
        return SortedMaps.immutable.of();
    }

    @Override
    protected ImmutableSortedMap<Integer, String> classUnderTest(Comparator<? super Integer> comparator)
    {
        return SortedMaps.immutable.of(comparator);
    }

    @Override
    protected <K, V> MapIterable<K, V> newMap()
    {
        return SortedMaps.immutable.of();
    }

    @Override
    protected <K, V> MapIterable<K, V> newMapWithKeyValue(K key1, V value1)
    {
        return SortedMaps.immutable.of(key1, value1);
    }

    @Override
    protected <K, V> MapIterable<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2)
    {
        return SortedMaps.immutable.of(key1, value1, key2, value2);
    }

    @Override
    protected <K, V> MapIterable<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3)
    {
        return SortedMaps.immutable.of(key1, value1, key2, value2, key3, value3);
    }

    @Override
    protected <K, V> MapIterable<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        return SortedMaps.immutable.of(key1, value1, key2, value2, key3, value3, key4, value4);
    }

    @Override
    protected int size()
    {
        return 0;
    }

    @Override
    @Test
    public void flipUniqueValues()
    {
        Verify.assertEmpty(this.classUnderTest().flipUniqueValues());
    }

    @Override
    @Test
    public void testToString()
    {
        ImmutableSortedMap<Integer, String> map = this.classUnderTest();
        Assertions.assertEquals("{}", map.toString());
    }

    @Test
    public void firstKey()
    {
        assertThrows(NoSuchElementException.class, () -> {
            new ImmutableEmptySortedMap<Object, Object>().firstKey();
        });
    }

    @Test
    public void lastKey()
    {
        assertThrows(NoSuchElementException.class, () -> {
            new ImmutableEmptySortedMap<Object, Object>().lastKey();
        });
    }

    @Override
    @Test
    public void get()
    {
        //Cannot call super.get() as map is empty and present key behavior does not exist.

        Integer absentKey = this.size() + 1;
        String absentValue = String.valueOf(absentKey);

        // Absent key behavior
        ImmutableSortedMap<Integer, String> classUnderTest = this.classUnderTest();
        Assertions.assertNull(classUnderTest.get(absentKey));
        Assertions.assertFalse(classUnderTest.containsValue(absentValue));

        // Still unchanged
        Assertions.assertEquals(this.equalUnifiedMap(), classUnderTest);
    }

    @Override
    @Test
    public void getIfAbsent()
    {
        super.getIfAbsent();

        Integer absentKey = this.size() + 1;
        String absentValue = String.valueOf(absentKey);

        // Absent key behavior
        ImmutableSortedMap<Integer, String> classUnderTest = this.classUnderTest();
        Assertions.assertEquals(absentValue, classUnderTest.getIfAbsent(absentKey, new PassThruFunction0<>(absentValue)));

        // Still unchanged
        Assertions.assertEquals(this.equalUnifiedMap(), classUnderTest);
    }

    @Override
    @Test
    public void getIfAbsentWith()
    {
        super.getIfAbsentWith();

        Integer absentKey = this.size() + 1;
        String absentValue = String.valueOf(absentKey);

        // Absent key behavior
        ImmutableSortedMap<Integer, String> classUnderTest = this.classUnderTest();
        Assertions.assertEquals(absentValue, classUnderTest.getIfAbsentWith(absentKey, String::valueOf, absentValue));

        // Still unchanged
        Assertions.assertEquals(this.equalUnifiedMap(), classUnderTest);
    }

    @Override
    @Test
    public void ifPresentApply()
    {
        super.ifPresentApply();

        Integer absentKey = this.size() + 1;

        ImmutableSortedMap<Integer, String> classUnderTest = this.classUnderTest();
        Assertions.assertNull(classUnderTest.ifPresentApply(absentKey, Functions.<String>getPassThru()));
    }

    @Override
    @Test
    public void notEmpty()
    {
        //Cannot call super.notEmpty() as map is empty.
        Assertions.assertFalse(this.classUnderTest().notEmpty());
    }

    @Override
    @Test
    public void allSatisfy()
    {
        super.allSatisfy();

        ImmutableSortedMap<String, String> map = new ImmutableEmptySortedMap<>();

        Assertions.assertTrue(map.allSatisfy(String.class::isInstance));
        Assertions.assertTrue(map.allSatisfy("Monkey"::equals));
    }

    @Override
    @Test
    public void noneSatisfy()
    {
        super.noneSatisfy();

        ImmutableSortedMap<String, String> map = new ImmutableEmptySortedMap<>();

        Assertions.assertTrue(map.noneSatisfy(Integer.class::isInstance));
        Assertions.assertTrue(map.noneSatisfy("Monkey"::equals));
    }

    @Override
    @Test
    public void anySatisfy()
    {
        super.anySatisfy();

        ImmutableSortedMap<String, String> map = new ImmutableEmptySortedMap<>();

        Assertions.assertFalse(map.anySatisfy(String.class::isInstance));
        Assertions.assertFalse(map.anySatisfy("Monkey"::equals));
    }

    @Override
    @Test
    public void max()
    {
        assertThrows(NoSuchElementException.class, () -> {
            super.max();

            this.classUnderTest().max();
        });
    }

    @Override
    @Test
    public void maxBy()
    {
        assertThrows(NoSuchElementException.class, () -> {
            super.maxBy();

            this.classUnderTest().maxBy(Functions.getStringPassThru());
        });
    }

    @Override
    @Test
    public void min()
    {
        assertThrows(NoSuchElementException.class, () -> {
            super.min();

            this.classUnderTest().min();
        });
    }

    @Override
    @Test
    public void minBy()
    {
        assertThrows(NoSuchElementException.class, () -> {
            super.minBy();

            this.classUnderTest().minBy(Functions.getStringPassThru());
        });
    }

    @Override
    @Test
    public void selectMap()
    {
        ImmutableSortedMap<Integer, String> map = this.classUnderTest();
        ImmutableSortedMap<Integer, String> actual = map.select((ignored1, ignored2) -> true);
        Verify.assertInstanceOf(ImmutableEmptySortedMap.class, actual);
        Assertions.assertSame(ImmutableEmptySortedMap.INSTANCE, actual);

        ImmutableSortedMap<Integer, String> revMap = this.classUnderTest(Comparators.<Integer>reverseNaturalOrder());
        ImmutableSortedMap<Integer, String> revActual = revMap.select((ignored1, ignored2) -> true);
        Verify.assertInstanceOf(ImmutableEmptySortedMap.class, revActual);
        Assertions.assertSame(revMap.comparator(), revActual.comparator());
    }

    @Override
    @Test
    public void rejectMap()
    {
        ImmutableSortedMap<Integer, String> map = this.classUnderTest();
        ImmutableSortedMap<Integer, String> actual = map.reject((ignored1, ignored2) -> false);
        Verify.assertInstanceOf(ImmutableEmptySortedMap.class, actual);
        Assertions.assertSame(ImmutableEmptySortedMap.INSTANCE, actual);

        ImmutableSortedMap<Integer, String> revMap = this.classUnderTest(Comparators.<Integer>reverseNaturalOrder());
        ImmutableSortedMap<Integer, String> revActual = revMap.reject((ignored1, ignored2) -> true);
        Verify.assertInstanceOf(ImmutableEmptySortedMap.class, revActual);
        Assertions.assertSame(revMap.comparator(), revActual.comparator());
    }

    @Override
    @Test
    public void collectMap()
    {
        ImmutableSortedMap<Integer, String> map = this.classUnderTest();
        ImmutableSortedMap<Integer, String> revMap = this.classUnderTest(Comparators.<Integer>reverseNaturalOrder());

        Function2<Integer, String, Pair<Integer, String>> alwaysTrueFunction = Tuples::pair;
        ImmutableMap<Integer, String> collect = map.collect(alwaysTrueFunction);
        ImmutableMap<Integer, String> revCollect = revMap.collect(alwaysTrueFunction);

        Verify.assertEmpty(collect);
        Assertions.assertSame(collect, revCollect);
    }

    @Override
    @Test
    public void detect()
    {
        super.detect();

        ImmutableSortedMap<Integer, String> map = this.classUnderTest();
        Assertions.assertNull(map.detect((ignored1, ignored2) -> true));
    }

    @Override
    @Test
    public void containsKey()
    {
        super.containsKey();

        ImmutableSortedMap<Integer, String> map = this.classUnderTest();
        ImmutableSortedMap<Integer, String> revMap = this.classUnderTest(Comparators.<Integer>reverseNaturalOrder());
        Assertions.assertFalse(map.containsKey(0));
        Assertions.assertFalse(revMap.containsKey(1));
    }

    @Test
    public void values()
    {
        ImmutableEmptySortedMap<Integer, String> map = (ImmutableEmptySortedMap<Integer, String>)
                this.classUnderTest();
        ImmutableEmptySortedMap<Integer, String> revMap = (ImmutableEmptySortedMap<Integer, String>)
                this.classUnderTest(Comparators.<Integer>reverseNaturalOrder());

        Verify.assertEmpty(map.values());
        Assertions.assertSame(Lists.immutable.of(), map.values());

        Verify.assertEmpty(revMap.values());

        Assertions.assertSame(Lists.immutable.of(), revMap.values());
    }

    @Override
    @Test
    public void serialization()
    {
        super.serialization();

        ImmutableSortedMap<Integer, String> map = this.classUnderTest();
        ImmutableSortedMap<Integer, String> deserialized = SerializeTestHelper.serializeDeserialize(map);
        Assertions.assertSame(ImmutableEmptySortedMap.INSTANCE, map);
        Assertions.assertSame(map, deserialized);

        ImmutableSortedMap<Integer, String> revMap = this.classUnderTest(Comparators.<Integer>reverseNaturalOrder());
        ImmutableSortedMap<Integer, String> revDeserialized = SerializeTestHelper.serializeDeserialize(revMap);
        Verify.assertInstanceOf(ImmutableSortedMap.class, revDeserialized);
        Assertions.assertNotNull(revDeserialized.comparator());
    }

    @Override
    @Test
    public void keyValuesView()
    {
        super.keyValuesView();

        Assertions.assertTrue(this.classUnderTest().keyValuesView().isEmpty());
    }

    @Override
    @Test
    public void take()
    {
        Assertions.assertEquals(this.classUnderTest(), this.classUnderTest().take(2));
    }

    @Override
    @Test
    public void drop()
    {
        Assertions.assertEquals(this.classUnderTest(), this.classUnderTest().drop(2));
    }
}
