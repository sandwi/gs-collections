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

package com.gs.collections.test.set.mutable;

import com.gs.collections.api.set.MutableSet;
import com.gs.collections.test.collection.mutable.MutableCollectionUniqueTestCase;
import com.gs.collections.test.set.SetTestCase;
import com.gs.collections.test.set.UnsortedSetIterableTestCase;
import org.junit.jupiter.api.Test;

public interface MutableSetTestCase extends SetTestCase, UnsortedSetIterableTestCase, MutableCollectionUniqueTestCase
{
    @Override
    <T> MutableSet<T> newWith(T... elements);

    @Override
    default void Object_PostSerializedEqualsAndHashCode()
    {
        UnsortedSetIterableTestCase.super.Object_PostSerializedEqualsAndHashCode();
    }

    @Override
    default void Object_equalsAndHashCode()
    {
        UnsortedSetIterableTestCase.super.Object_equalsAndHashCode();
    }

    @Override
    default void Iterable_next()
    {
        UnsortedSetIterableTestCase.super.Iterable_next();
    }

    @Override
    @Test
    default void Iterable_remove()
    {
        SetTestCase.super.Iterable_remove();
    }
}
