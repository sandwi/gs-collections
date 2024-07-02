/*
 * Copyright 2013 Goldman Sachs.
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

package com.gs.collections.impl.map.immutable.primitive;

import com.gs.collections.api.map.primitive.ImmutableObjectBooleanMap;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.primitive.ObjectBooleanHashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * JUnit test for {@link ImmutableObjectBooleanHashMap}.
 */
public class ImmutableObjectBooleanHashMapTest extends AbstractImmutableObjectBooleanMapTestCase
{
    @Override
    protected ImmutableObjectBooleanMap<String> classUnderTest()
    {
        return ObjectBooleanHashMap.newWithKeysValues("0", true, "1", true, "2", false).toImmutable();
    }

    @Test
    public void newWithKeyValue()
    {
        ImmutableObjectBooleanMap<String> map1 = this.classUnderTest();
        ImmutableObjectBooleanMap<String> expected = ObjectBooleanHashMap.newWithKeysValues("0", true, "1", true, "2", false, "3", false).toImmutable();
        Assertions.assertEquals(expected, map1.newWithKeyValue("3", false));
        Assertions.assertNotSame(map1, map1.newWithKeyValue("3", false));
        Assertions.assertEquals(this.classUnderTest(), map1);
    }

    @Test
    public void newWithoutKeyValue()
    {
        ImmutableObjectBooleanMap<String> map1 = this.classUnderTest();
        ImmutableObjectBooleanMap<String> expected = this.newWithKeysValues("0", true, "1", true);
        Assertions.assertEquals(expected, map1.newWithoutKey("2"));
        Assertions.assertNotSame(map1, map1.newWithoutKey("2"));
        Assertions.assertEquals(this.classUnderTest(), map1);
    }

    @Test
    public void newWithoutAllKeys()
    {
        ImmutableObjectBooleanMap<String> map1 = this.classUnderTest();
        ImmutableObjectBooleanMap<String> expected = this.newWithKeysValues("1", true);
        Assertions.assertEquals(expected, map1.newWithoutAllKeys(FastList.newListWith("0", "2")));
        Assertions.assertNotSame(map1, map1.newWithoutAllKeys(FastList.newListWith("0", "2")));
        Assertions.assertEquals(this.classUnderTest(), map1);
    }
}
