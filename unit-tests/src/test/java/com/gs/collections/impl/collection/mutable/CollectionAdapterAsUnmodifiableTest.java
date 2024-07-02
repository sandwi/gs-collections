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

package com.gs.collections.impl.collection.mutable;

import java.util.ArrayList;
import java.util.List;

import com.gs.collections.api.collection.MutableCollection;
import com.gs.collections.api.partition.PartitionMutableCollection;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.Functions2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CollectionAdapterAsUnmodifiableTest extends UnmodifiableMutableCollectionTestCase<Integer>
{
    @Override
    protected MutableCollection<Integer> getCollection()
    {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        return new CollectionAdapter<>(list).asUnmodifiable();
    }

    @Override
    @Test
    public void select()
    {
        Assertions.assertEquals(this.getCollection().toList(), this.getCollection().select(ignored -> true));
        Assertions.assertNotEquals(this.getCollection().toList(), this.getCollection().select(ignored -> false));
    }

    @Override
    @Test
    public void selectWith()
    {
        Assertions.assertEquals(this.getCollection().toList(), this.getCollection().selectWith((ignored1, ignored2) -> true, null));
        Assertions.assertNotEquals(this.getCollection().toList(), this.getCollection().selectWith((ignored1, ignored2) -> false, null));
    }

    @Override
    @Test
    public void reject()
    {
        Assertions.assertEquals(this.getCollection().toList(), this.getCollection().reject(ignored1 -> false));
        Assertions.assertNotEquals(this.getCollection().toList(), this.getCollection().reject(ignored -> true));
    }

    @Override
    @Test
    public void rejectWith()
    {
        Assertions.assertEquals(this.getCollection().toList(), this.getCollection().rejectWith((ignored11, ignored21) -> false, null));
        Assertions.assertNotEquals(this.getCollection().toList(), this.getCollection().rejectWith((ignored1, ignored2) -> true, null));
    }

    @Override
    @Test
    public void partition()
    {
        PartitionMutableCollection<?> partition = this.getCollection().partition(ignored -> true);
        Assertions.assertEquals(this.getCollection().toList(), partition.getSelected());
        Assertions.assertNotEquals(this.getCollection().toList(), partition.getRejected());
    }

    @Override
    @Test
    public void partitionWith()
    {
        PartitionMutableCollection<?> partition = this.getCollection().partitionWith((ignored1, ignored2) -> true, null);
        Assertions.assertEquals(this.getCollection().toList(), partition.getSelected());
        Assertions.assertNotEquals(this.getCollection().toList(), partition.getRejected());
    }

    @Override
    @Test
    public void collect()
    {
        Assertions.assertEquals(this.getCollection().toList(), this.getCollection().collect(Functions.getPassThru()));
        Assertions.assertNotEquals(this.getCollection().toList(), this.getCollection().collect(Object::getClass));
    }

    @Override
    @Test
    public void collectWith()
    {
        Assertions.assertEquals(this.getCollection().toList(), this.getCollection().collectWith(Functions2.fromFunction(Functions.getPassThru()), null));
        Assertions.assertNotEquals(this.getCollection().toList(), this.getCollection().collectWith(Functions2.fromFunction(Object::getClass), null));
    }

    @Override
    @Test
    public void collectIf()
    {
        Assertions.assertEquals(this.getCollection().toList(), this.getCollection().collectIf(ignored -> true, Functions.getPassThru()));
        Assertions.assertNotEquals(this.getCollection().toList(), this.getCollection().collectIf(ignored -> false, Object::getClass));
    }
}
