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

package com.gs.collections.impl.factory;

import com.gs.collections.api.bag.ImmutableBag;
import com.gs.collections.api.factory.bag.ImmutableBagFactory;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BagsTest
{
    @Test
    public void immutables()
    {
        ImmutableBagFactory bagFactory = Bags.immutable;
        Assertions.assertEquals(HashBag.newBag(), bagFactory.of());
        Verify.assertInstanceOf(ImmutableBag.class, bagFactory.of());
        Assertions.assertEquals(HashBag.newBagWith(1), bagFactory.of(1));
        Verify.assertInstanceOf(ImmutableBag.class, bagFactory.of(1));
        Assertions.assertEquals(HashBag.newBagWith(1, 2), bagFactory.of(1, 2));
        Verify.assertInstanceOf(ImmutableBag.class, bagFactory.of(1, 2));
        Assertions.assertEquals(HashBag.newBagWith(1, 2, 3), bagFactory.of(1, 2, 3));
        Verify.assertInstanceOf(ImmutableBag.class, bagFactory.of(1, 2, 3));
        Assertions.assertEquals(HashBag.newBagWith(1, 2, 3, 4), bagFactory.of(1, 2, 3, 4));
        Verify.assertInstanceOf(ImmutableBag.class, bagFactory.of(1, 2, 3, 4));
        Assertions.assertEquals(HashBag.newBagWith(1, 2, 3, 4, 5), bagFactory.of(1, 2, 3, 4, 5));
        Verify.assertInstanceOf(ImmutableBag.class, bagFactory.of(1, 2, 3, 4, 5));
        Assertions.assertEquals(HashBag.newBagWith(1, 2, 3, 4, 5, 6), bagFactory.of(1, 2, 3, 4, 5, 6));
        Verify.assertInstanceOf(ImmutableBag.class, bagFactory.of(1, 2, 3, 4, 5, 6));
        Assertions.assertEquals(HashBag.newBagWith(1, 2, 3, 4, 5, 6, 7), bagFactory.of(1, 2, 3, 4, 5, 6, 7));
        Verify.assertInstanceOf(ImmutableBag.class, bagFactory.of(1, 2, 3, 4, 5, 6, 7));
        Assertions.assertEquals(HashBag.newBagWith(1, 2, 3, 4, 5, 6, 7, 8), bagFactory.of(1, 2, 3, 4, 5, 6, 7, 8));
        Verify.assertInstanceOf(ImmutableBag.class, bagFactory.of(1, 2, 3, 4, 5, 6, 7, 8));
        Assertions.assertEquals(HashBag.newBagWith(1, 2, 3, 4, 5, 6, 7, 8, 9), bagFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Verify.assertInstanceOf(ImmutableBag.class, bagFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Assertions.assertEquals(HashBag.newBagWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), bagFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Verify.assertInstanceOf(ImmutableBag.class, bagFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Assertions.assertEquals(HashBag.newBagWith(3, 2, 1), bagFactory.ofAll(HashBag.newBagWith(1, 2, 3)));
        Verify.assertInstanceOf(ImmutableBag.class, bagFactory.ofAll(HashBag.newBagWith(1, 2, 3)));
    }

    @Test
    public void emptyBag()
    {
        Assertions.assertTrue(Bags.immutable.of().isEmpty());
    }

    @Test
    public void newBagWith()
    {
        ImmutableBag<String> bag = Bags.immutable.of();
        Assertions.assertEquals(bag, Bags.immutable.of(bag.toArray()));
        Assertions.assertEquals(bag = bag.newWith("1"), Bags.immutable.of("1"));
        Assertions.assertEquals(bag = bag.newWith("2"), Bags.immutable.of("1", "2"));
        Assertions.assertEquals(bag = bag.newWith("3"), Bags.immutable.of("1", "2", "3"));
        Assertions.assertEquals(bag = bag.newWith("4"), Bags.immutable.of("1", "2", "3", "4"));
        Assertions.assertEquals(bag = bag.newWith("5"), Bags.immutable.of("1", "2", "3", "4", "5"));
        Assertions.assertEquals(bag = bag.newWith("6"), Bags.immutable.of("1", "2", "3", "4", "5", "6"));
        Assertions.assertEquals(bag = bag.newWith("7"), Bags.immutable.of("1", "2", "3", "4", "5", "6", "7"));
        Assertions.assertEquals(bag = bag.newWith("8"), Bags.immutable.of("1", "2", "3", "4", "5", "6", "7", "8"));
        Assertions.assertEquals(bag = bag.newWith("9"), Bags.immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9"));
        Assertions.assertEquals(bag = bag.newWith("10"), Bags.immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
        Assertions.assertEquals(bag = bag.newWith("11"), Bags.immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"));
        Assertions.assertEquals(bag = bag.newWith("12"), Bags.immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"));
    }

    @SuppressWarnings("RedundantArrayCreation")
    @Test
    public void newBagWithArray()
    {
        ImmutableBag<String> bag = Bags.immutable.of();
        Assertions.assertEquals(bag = bag.newWith("1"), Bags.immutable.of(new String[]{"1"}));
        Assertions.assertEquals(bag = bag.newWith("2"), Bags.immutable.of(new String[]{"1", "2"}));
        Assertions.assertEquals(bag = bag.newWith("3"), Bags.immutable.of(new String[]{"1", "2", "3"}));
        Assertions.assertEquals(bag = bag.newWith("4"), Bags.immutable.of(new String[]{"1", "2", "3", "4"}));
        Assertions.assertEquals(bag = bag.newWith("5"), Bags.immutable.of(new String[]{"1", "2", "3", "4", "5"}));
        Assertions.assertEquals(bag = bag.newWith("6"), Bags.immutable.of(new String[]{"1", "2", "3", "4", "5", "6"}));
        Assertions.assertEquals(bag = bag.newWith("7"), Bags.immutable.of(new String[]{"1", "2", "3", "4", "5", "6", "7"}));
        Assertions.assertEquals(bag = bag.newWith("8"), Bags.immutable.of(new String[]{"1", "2", "3", "4", "5", "6", "7", "8"}));
        Assertions.assertEquals(bag = bag.newWith("9"), Bags.immutable.of(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"}));
        Assertions.assertEquals(bag = bag.newWith("10"), Bags.immutable.of(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}));
        Assertions.assertEquals(bag = bag.newWith("11"), Bags.immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"));
    }

    @Test
    public void newBagWithBag()
    {
        ImmutableBag<String> bag = Bags.immutable.of();
        HashBag<String> hashBag = HashBag.newBagWith("1");
        Assertions.assertEquals(bag = bag.newWith("1"), hashBag.toImmutable());
        hashBag.add("2");
        Assertions.assertEquals(bag = bag.newWith("2"), hashBag.toImmutable());
        hashBag.add("3");
        Assertions.assertEquals(bag = bag.newWith("3"), hashBag.toImmutable());
        hashBag.add("4");
        Assertions.assertEquals(bag = bag.newWith("4"), hashBag.toImmutable());
        hashBag.add("5");
        Assertions.assertEquals(bag = bag.newWith("5"), hashBag.toImmutable());
        hashBag.add("6");
        Assertions.assertEquals(bag = bag.newWith("6"), hashBag.toImmutable());
        hashBag.add("7");
        Assertions.assertEquals(bag = bag.newWith("7"), hashBag.toImmutable());
        hashBag.add("8");
        Assertions.assertEquals(bag = bag.newWith("8"), hashBag.toImmutable());
        hashBag.add("9");
        Assertions.assertEquals(bag = bag.newWith("9"), hashBag.toImmutable());
        hashBag.add("10");
        Assertions.assertEquals(bag = bag.newWith("10"), hashBag.toImmutable());
        hashBag.add("11");
        Assertions.assertEquals(bag = bag.newWith("11"), hashBag.toImmutable());
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(Bags.class);
    }
}
