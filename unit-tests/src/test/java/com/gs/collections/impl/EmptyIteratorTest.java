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

package com.gs.collections.impl;

import java.util.NoSuchElementException;

import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EmptyIteratorTest
{
    private EmptyIterator<Object> emptyIterator;

    @BeforeEach
    public void setUp()
    {
        this.emptyIterator = EmptyIterator.getInstance();
    }

    @Test
    public void hasPrevious()
    {
        Assertions.assertFalse(this.emptyIterator.hasPrevious());
    }

    @Test
    public void previous()
    {
        Verify.assertThrows(NoSuchElementException.class, (Runnable) this.emptyIterator::previous);
    }

    @Test
    public void previousIndex()
    {
        Assertions.assertEquals(-1, this.emptyIterator.previousIndex());
    }

    @Test
    public void set()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.emptyIterator.set(1));
    }

    @Test
    public void add()
    {
        Verify.assertThrows(UnsupportedOperationException.class, () -> this.emptyIterator.add(1));
    }
}
