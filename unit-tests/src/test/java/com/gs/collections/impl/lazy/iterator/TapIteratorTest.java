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

package com.gs.collections.impl.lazy.iterator;

import java.util.NoSuchElementException;

import com.gs.collections.impl.factory.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TapIteratorTest
{
    @Test
    public void nextIfDoesntHaveAnything()
    {
        assertThrows(NoSuchElementException.class, () -> {
            new TapIterator<>(Lists.immutable.of(), object -> {
            }).next();
        });
    }

    @Test
    public void removeIsUnsupported()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            new TapIterator<>(Lists.immutable.of().iterator(), object -> {
            }).remove();
        });
    }

    @Test
    public void nextAfterEmptyIterable()
    {
        Object expected = new Object();
        TapIterator<Object> iterator = new TapIterator<>(
                Lists.fixedSize.of(expected), object -> { });
        Assertions.assertSame(expected, iterator.next());
    }
}
