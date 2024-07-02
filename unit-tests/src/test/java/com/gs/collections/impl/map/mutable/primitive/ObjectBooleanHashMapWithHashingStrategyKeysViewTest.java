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

package com.gs.collections.impl.map.mutable.primitive;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.gs.collections.api.LazyIterable;
import com.gs.collections.api.block.HashingStrategy;
import com.gs.collections.api.map.primitive.MutableObjectBooleanMap;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.lazy.AbstractLazyIterableTestCase;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * JUnit test for {@link ObjectBooleanHashMapWithHashingStrategy#keysView()}.
 */
public class ObjectBooleanHashMapWithHashingStrategyKeysViewTest extends AbstractLazyIterableTestCase
{
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

    @Override
    protected <T> LazyIterable<T> newWith(T... elements)
    {
        ObjectBooleanHashMapWithHashingStrategy<T> map = new ObjectBooleanHashMapWithHashingStrategy<>(HashingStrategies.nullSafeHashingStrategy(HashingStrategies.<T>defaultStrategy()));
        for (int i = 0; i < elements.length; i++)
        {
            map.put(elements[i], (i & 1) == 0);
        }
        return map.keysView();
    }

    @Override
    @Test
    public void iterator()
    {
        MutableSet<String> expected = UnifiedSet.newSetWith("zero", "thirtyOne", "thirtyTwo");
        MutableSet<String> actual = UnifiedSet.newSet();

        Iterator<String> iterator = ObjectBooleanHashMapWithHashingStrategy.newWithKeysValues(STRING_HASHING_STRATEGY, "zero", true, "thirtyOne", false, "thirtyTwo", true).keysView().iterator();
        Assertions.assertTrue(iterator.hasNext());
        actual.add(iterator.next());
        Verify.assertThrows(UnsupportedOperationException.class, iterator::remove);
        Assertions.assertTrue(iterator.hasNext());
        actual.add(iterator.next());
        Assertions.assertTrue(iterator.hasNext());
        actual.add(iterator.next());
        Assertions.assertFalse(iterator.hasNext());

        Assertions.assertEquals(expected, actual);
        Verify.assertThrows(NoSuchElementException.class, (Runnable) iterator::next);
    }
}
