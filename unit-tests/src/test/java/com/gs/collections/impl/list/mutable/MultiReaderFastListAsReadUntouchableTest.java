/*
 * Copyright 2011 Goldman Sachs.
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

package com.gs.collections.impl.list.mutable;

import java.io.Serializable;

import com.gs.collections.api.collection.MutableCollection;
import com.gs.collections.api.list.MutableList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MultiReaderFastListAsReadUntouchableTest extends UnmodifiableMutableListTestCase
{
    @Override
    protected MutableList<Integer> getCollection()
    {
        return MultiReaderFastList.newListWith(1).asReadUntouchable();
    }

    @Override
    @Test
    public void serialization()
    {
        MutableCollection<Integer> collection = this.getCollection();
        Assertions.assertFalse(collection instanceof Serializable);
    }
}
