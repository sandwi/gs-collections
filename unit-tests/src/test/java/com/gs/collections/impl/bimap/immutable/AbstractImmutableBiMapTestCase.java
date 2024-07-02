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

package com.gs.collections.impl.bimap.immutable;

import com.gs.collections.api.bimap.ImmutableBiMap;
import com.gs.collections.impl.map.immutable.ImmutableMapIterableTestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public abstract class AbstractImmutableBiMapTestCase extends ImmutableMapIterableTestCase
{
    @Override
    protected abstract ImmutableBiMap<Integer, String> classUnderTest();

    protected abstract ImmutableBiMap<Integer, String> newEmpty();

    protected abstract ImmutableBiMap<Integer, String> newWithMap();

    protected abstract ImmutableBiMap<Integer, String> newWithHashBiMap();

    protected abstract ImmutableBiMap<Integer, String> newWithImmutableMap();

    @Override
    protected int size()
    {
        return 4;
    }

    @Override
    @Test
    public void testToString()
    {
        Assertions.assertEquals("{1=1, 2=2, 3=3, 4=4}", this.classUnderTest().toString());
    }

    @Test
    public void testNewEmpty()
    {
        Assertions.assertTrue(this.newEmpty().isEmpty());
    }

    @Test
    public void testNewWithMap()
    {
        Assertions.assertEquals(this.classUnderTest(), this.newWithMap());
    }

    @Test
    public void testNewWithHashBiMap()
    {
        Assertions.assertEquals(this.classUnderTest(), this.newWithHashBiMap());
    }

    @Test
    public void testNewWithImmutableMap()
    {
        Assertions.assertEquals(this.classUnderTest(), this.newWithImmutableMap());
    }

    @Test
    public void containsKey()
    {
        Assertions.assertTrue(this.classUnderTest().containsKey(1));
        Assertions.assertFalse(this.classUnderTest().containsKey(5));
    }

    @Test
    public void toImmutable()
    {
        Assertions.assertEquals(this.classUnderTest(), this.classUnderTest().toImmutable());
    }
}
