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

package com.gs.collections.test.set;

import com.gs.collections.test.UnmodifiableCollectionTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public interface UnmodifiableSetTestCase extends UnmodifiableCollectionTestCase, SetTestCase
{
    @Override
    @Test
    default void Iterable_remove()
    {
        UnmodifiableCollectionTestCase.super.Iterable_remove();
    }

    @Override
    @Test
    default void Collection_add()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            UnmodifiableCollectionTestCase.super.Collection_add();
        });
    }
}
