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

package com.gs.collections.impl.factory.primitive;

import com.gs.collections.api.factory.list.primitive.ImmutableBooleanListFactory;
import com.gs.collections.api.list.primitive.ImmutableBooleanList;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BooleanListsTest
{
    @Test
    public void immutables()
    {
        ImmutableBooleanListFactory listFactory = BooleanLists.immutable;
        Assertions.assertEquals(new BooleanArrayList(), listFactory.of());
        Verify.assertInstanceOf(ImmutableBooleanList.class, listFactory.of());
        Assertions.assertEquals(BooleanArrayList.newListWith(true), listFactory.of(true));
        Verify.assertInstanceOf(ImmutableBooleanList.class, listFactory.of(true));
        Assertions.assertEquals(BooleanArrayList.newListWith(true, false), listFactory.of(true, false));
        Verify.assertInstanceOf(ImmutableBooleanList.class, listFactory.of(true, false));
        Assertions.assertEquals(BooleanArrayList.newListWith(true, false, true), listFactory.of(true, false, true));
        Verify.assertInstanceOf(ImmutableBooleanList.class, listFactory.of(true, false, true));
        Assertions.assertEquals(BooleanArrayList.newListWith(true, false, true, false), listFactory.of(true, false, true, false));
        Verify.assertInstanceOf(ImmutableBooleanList.class, listFactory.of(true, false, true, false));
        Assertions.assertEquals(BooleanArrayList.newListWith(true, false, true, false, true), listFactory.of(true, false, true, false, true));
        Verify.assertInstanceOf(ImmutableBooleanList.class, listFactory.of(true, false, true, false, true));
        Assertions.assertEquals(BooleanArrayList.newListWith(true, false, true, false, true, false), listFactory.of(true, false, true, false, true, false));
        Verify.assertInstanceOf(ImmutableBooleanList.class, listFactory.of(true, false, true, false, true, false));
        Assertions.assertEquals(BooleanArrayList.newListWith(true, false, true, false, true, false, true), listFactory.of(true, false, true, false, true, false, true));
        Verify.assertInstanceOf(ImmutableBooleanList.class, listFactory.of(true, false, true, false, true, false, true));
        Assertions.assertEquals(BooleanArrayList.newListWith(true, false, true, false, true, false, true, true), listFactory.of(true, false, true, false, true, false, true, true));
        Verify.assertInstanceOf(ImmutableBooleanList.class, listFactory.of(true, false, true, false, true, false, true, true));
        Assertions.assertEquals(BooleanArrayList.newListWith(true, false, true, false, true, false, true, true, true), listFactory.of(true, false, true, false, true, false, true, true, true));
        Verify.assertInstanceOf(ImmutableBooleanList.class, listFactory.of(true, false, true, false, true, false, true, true, true));
        Assertions.assertEquals(BooleanArrayList.newListWith(true, false, true, false, true, false, true, true, true, false), listFactory.of(true, false, true, false, true, false, true, true, true, false));
        Verify.assertInstanceOf(ImmutableBooleanList.class, listFactory.of(true, false, true, false, true, false, true, true, true, false));
        Assertions.assertEquals(BooleanArrayList.newListWith(true, false, true), listFactory.ofAll(BooleanArrayList.newListWith(true, false, true)));
        Verify.assertInstanceOf(ImmutableBooleanList.class, listFactory.ofAll(BooleanArrayList.newListWith(true, false, true)));
    }

    @Test
    public void emptyList()
    {
        Verify.assertEmpty(BooleanLists.immutable.of());
        Assertions.assertSame(BooleanLists.immutable.of(), BooleanLists.immutable.of());
        Verify.assertPostSerializedIdentity(BooleanLists.immutable.of());
    }

    @Test
    public void newListWith()
    {
        ImmutableBooleanList list = BooleanLists.immutable.of();
        Assertions.assertEquals(list, BooleanLists.immutable.of(list.toArray()));
        Assertions.assertEquals(list = list.newWith(true), BooleanLists.immutable.of(true));
        Assertions.assertEquals(list = list.newWith(false), BooleanLists.immutable.of(true, false));
        Assertions.assertEquals(list = list.newWith(true), BooleanLists.immutable.of(true, false, true));
        Assertions.assertEquals(list = list.newWith(false), BooleanLists.immutable.of(true, false, true, false));
        Assertions.assertEquals(list = list.newWith(true), BooleanLists.immutable.of(true, false, true, false, true));
        Assertions.assertEquals(list = list.newWith(false), BooleanLists.immutable.of(true, false, true, false, true, false));
        Assertions.assertEquals(list = list.newWith(true), BooleanLists.immutable.of(true, false, true, false, true, false, true));
        Assertions.assertEquals(list = list.newWith(true), BooleanLists.immutable.of(true, false, true, false, true, false, true, true));
        Assertions.assertEquals(list = list.newWith(true), BooleanLists.immutable.of(true, false, true, false, true, false, true, true, true));
        Assertions.assertEquals(list = list.newWith(false), BooleanLists.immutable.of(true, false, true, false, true, false, true, true, true, false));
        Assertions.assertEquals(list = list.newWith(true), BooleanLists.immutable.of(true, false, true, false, true, false, true, true, true, false, true));
        Assertions.assertEquals(list = list.newWith(false), BooleanLists.immutable.of(true, false, true, false, true, false, true, true, true, false, true, false));
    }

    @SuppressWarnings("RedundantArrayCreation")
    @Test
    public void newListWithArray()
    {
        ImmutableBooleanList list = BooleanLists.immutable.of();
        Assertions.assertEquals(list = list.newWith(true), BooleanLists.immutable.of(new boolean[]{true}));
        Assertions.assertEquals(list = list.newWith(false), BooleanLists.immutable.of(new boolean[]{true, false}));
        Assertions.assertEquals(list = list.newWith(true), BooleanLists.immutable.of(new boolean[]{true, false, true}));
        Assertions.assertEquals(list = list.newWith(false), BooleanLists.immutable.of(new boolean[]{true, false, true, false}));
        Assertions.assertEquals(list = list.newWith(true), BooleanLists.immutable.of(new boolean[]{true, false, true, false, true}));
        Assertions.assertEquals(list = list.newWith(false), BooleanLists.immutable.of(new boolean[]{true, false, true, false, true, false}));
        Assertions.assertEquals(list = list.newWith(true), BooleanLists.immutable.of(new boolean[]{true, false, true, false, true, false, true}));
        Assertions.assertEquals(list = list.newWith(true), BooleanLists.immutable.of(new boolean[]{true, false, true, false, true, false, true, true}));
        Assertions.assertEquals(list = list.newWith(true), BooleanLists.immutable.of(new boolean[]{true, false, true, false, true, false, true, true, true}));
        Assertions.assertEquals(list = list.newWith(false), BooleanLists.immutable.of(new boolean[]{true, false, true, false, true, false, true, true, true, false}));
        Assertions.assertEquals(list = list.newWith(true), BooleanLists.immutable.of(new boolean[]{true, false, true, false, true, false, true, true, true, false, true}));
    }

    @Test
    public void newListWithList()
    {
        ImmutableBooleanList list = BooleanLists.immutable.of();
        BooleanArrayList booleanArrayList = BooleanArrayList.newListWith(true);
        Assertions.assertEquals(list = list.newWith(true), booleanArrayList.toImmutable());
        Assertions.assertEquals(list = list.newWith(false), booleanArrayList.with(false).toImmutable());
        Assertions.assertEquals(list = list.newWith(true), booleanArrayList.with(true).toImmutable());
        Assertions.assertEquals(list = list.newWith(false), booleanArrayList.with(false).toImmutable());
        Assertions.assertEquals(list = list.newWith(true), booleanArrayList.with(true).toImmutable());
        Assertions.assertEquals(list = list.newWith(false), booleanArrayList.with(false).toImmutable());
        Assertions.assertEquals(list = list.newWith(true), booleanArrayList.with(true).toImmutable());
        Assertions.assertEquals(list = list.newWith(true), booleanArrayList.with(true).toImmutable());
        Assertions.assertEquals(list = list.newWith(true), booleanArrayList.with(true).toImmutable());
        Assertions.assertEquals(list = list.newWith(false), booleanArrayList.with(false).toImmutable());
        Assertions.assertEquals(list = list.newWith(true), booleanArrayList.with(true).toImmutable());
    }

    @Test
    public void newListWithWithList()
    {
        Assertions.assertEquals(new BooleanArrayList(), BooleanLists.immutable.ofAll(new BooleanArrayList()));
        Assertions.assertEquals(BooleanArrayList.newListWith(true), BooleanLists.immutable.ofAll(BooleanArrayList.newListWith(true)));
        Assertions.assertEquals(BooleanArrayList.newListWith(true, false), BooleanLists.immutable.ofAll(BooleanArrayList.newListWith(true, false)));
        Assertions.assertEquals(BooleanArrayList.newListWith(true, false, true), BooleanLists.immutable.ofAll(BooleanArrayList.newListWith(true, false, true)));
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(BooleanLists.class);
    }
}
