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

package com.gs.collections.impl.map.strategy.immutable;

import java.util.NoSuchElementException;

import com.gs.collections.api.block.HashingStrategy;
import com.gs.collections.api.map.ImmutableMap;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.block.function.PassThruFunction0;
import com.gs.collections.impl.map.immutable.ImmutableMemoryEfficientMapTestCase;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JUnit test for {@link ImmutableEmptyMapWithHashingStrategy}.
 */
public class ImmutableEmptyMapWithHashingStrategyTest extends ImmutableMemoryEfficientMapTestCase
{
    //Not using the static factor method in order to have concrete types for test cases
    private static final HashingStrategy<Integer> HASHING_STRATEGY = HashingStrategies.nullSafeHashingStrategy(new HashingStrategy<Integer>()
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

    @Override
    protected ImmutableMap<Integer, String> classUnderTest()
    {
        return new ImmutableEmptyMapWithHashingStrategy<>(HASHING_STRATEGY);
    }

    @Override
    protected int size()
    {
        return 0;
    }

    @Override
    @Test
    public void testToString()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();
        Assertions.assertEquals("{}", map.toString());
    }

    @Override
    @Test
    public void flipUniqueValues()
    {
        Verify.assertEmpty(this.classUnderTest().flipUniqueValues());
    }

    @Override
    @Test
    public void get()
    {
        Integer absentKey = this.size() + 1;
        String absentValue = String.valueOf(absentKey);

        // Absent key behavior
        ImmutableMap<Integer, String> classUnderTest = this.classUnderTest();
        Assertions.assertNull(classUnderTest.get(absentKey));
        Assertions.assertFalse(classUnderTest.containsValue(absentValue));

        // Still unchanged
        Assertions.assertEquals(this.equalUnifiedMap(), classUnderTest);
    }

    @Override
    @Test
    public void getIfAbsent_function()
    {
        Integer absentKey = this.size() + 1;
        String absentValue = String.valueOf(absentKey);

        // Absent key behavior
        ImmutableMap<Integer, String> classUnderTest = this.classUnderTest();
        Assertions.assertEquals(absentValue, classUnderTest.getIfAbsent(absentKey, new PassThruFunction0<>(absentValue)));

        // Still unchanged
        Assertions.assertEquals(this.equalUnifiedMap(), classUnderTest);
    }

    @Override
    @Test
    public void getIfAbsent()
    {
        Integer absentKey = this.size() + 1;
        String absentValue = String.valueOf(absentKey);

        // Absent key behavior
        ImmutableMap<Integer, String> classUnderTest = this.classUnderTest();
        Assertions.assertEquals(absentValue, classUnderTest.getIfAbsentValue(absentKey, absentValue));

        // Still unchanged
        Assertions.assertEquals(this.equalUnifiedMap(), classUnderTest);
    }

    @Override
    @Test
    public void getIfAbsentWith()
    {
        Integer absentKey = this.size() + 1;
        String absentValue = String.valueOf(absentKey);

        // Absent key behavior
        ImmutableMap<Integer, String> classUnderTest = this.classUnderTest();
        Assertions.assertEquals(absentValue, classUnderTest.getIfAbsentWith(absentKey, String::valueOf, absentValue));

        // Still unchanged
        Assertions.assertEquals(this.equalUnifiedMap(), classUnderTest);
    }

    @Override
    @Test
    public void ifPresentApply()
    {
        Integer absentKey = this.size() + 1;

        ImmutableMap<Integer, String> classUnderTest = this.classUnderTest();
        Assertions.assertNull(classUnderTest.ifPresentApply(absentKey, Functions.<String>getPassThru()));
    }

    @Override
    @Test
    public void notEmpty()
    {
        Assertions.assertFalse(this.classUnderTest().notEmpty());
    }

    @Override
    @Test
    public void allSatisfy()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();

        Assertions.assertTrue(map.allSatisfy(String.class::isInstance));
        Assertions.assertTrue(map.allSatisfy("Monkey"::equals));
    }

    @Override
    @Test
    public void noneSatisfy()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();

        Assertions.assertTrue(map.noneSatisfy(Integer.class::isInstance));
        Assertions.assertTrue(map.noneSatisfy("Monkey"::equals));
    }

    @Override
    @Test
    public void anySatisfy()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();

        Assertions.assertFalse(map.anySatisfy(String.class::isInstance));
        Assertions.assertFalse(map.anySatisfy("Monkey"::equals));
    }

    @Override
    @Test
    public void max()
    {
        assertThrows(NoSuchElementException.class, () -> {
            ImmutableMap<Integer, String> map = this.classUnderTest();

            map.max();
        });
    }

    @Override
    @Test
    public void maxBy()
    {
        assertThrows(NoSuchElementException.class, () -> {
            ImmutableMap<Integer, String> map = this.classUnderTest();

            map.maxBy(Functions.getStringPassThru());
        });
    }

    @Override
    @Test
    public void min()
    {
        assertThrows(NoSuchElementException.class, () -> {
            ImmutableMap<Integer, String> map = this.classUnderTest();

            map.min();
        });
    }

    @Override
    @Test
    public void minBy()
    {
        assertThrows(NoSuchElementException.class, () -> {
            ImmutableMap<Integer, String> map = this.classUnderTest();

            map.minBy(Functions.getStringPassThru());
        });
    }

    @Override
    @Test
    public void select()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();
        ImmutableMap<Integer, String> actual = map.select((ignored1, ignored2) -> true);
        Verify.assertInstanceOf(ImmutableEmptyMapWithHashingStrategy.class, actual);
    }

    @Override
    @Test
    public void reject()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();
        ImmutableMap<Integer, String> actual = map.reject((ignored1, ignored2) -> false);
        Verify.assertInstanceOf(ImmutableEmptyMapWithHashingStrategy.class, actual);
    }

    @Override
    @Test
    public void detect()
    {
        ImmutableMap<Integer, String> map = this.classUnderTest();
        Assertions.assertNull(map.detect((ignored1, ignored2) -> true));
    }

    @Override
    protected <K, V> ImmutableMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4)
    {
        return new ImmutableEmptyMapWithHashingStrategy<>(HashingStrategies.nullSafeHashingStrategy(
                HashingStrategies.<K>defaultStrategy()));
    }
}
