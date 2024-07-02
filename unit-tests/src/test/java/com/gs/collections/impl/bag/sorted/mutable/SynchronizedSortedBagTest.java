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

package com.gs.collections.impl.bag.sorted.mutable;

import java.util.Comparator;

import com.gs.collections.api.bag.sorted.MutableSortedBag;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.tuple.primitive.ObjectIntPair;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * JUnit test for {@link SynchronizedSortedBag}.
 */
public class SynchronizedSortedBagTest extends AbstractMutableSortedBagTestCase
{
    @Override
    protected <T> MutableSortedBag<T> newWith(T... littleElements)
    {
        return new SynchronizedSortedBag<>(TreeBag.newBagWith(littleElements));
    }

    @SafeVarargs
    @Override
    protected final <T> MutableSortedBag<T> newWithOccurrences(ObjectIntPair<T>... elementsWithOccurrences)
    {
        return super.newWithOccurrences(elementsWithOccurrences).asSynchronized();
    }

    @Override
    protected <T> MutableSortedBag<T> newWith(Comparator<? super T> comparator, T... elements)
    {
        return new SynchronizedSortedBag<>(TreeBag.newBagWith(comparator, elements));
    }

    @Override
    @Test
    public void asSynchronized()
    {
        MutableSortedBag<Object> synchronizedBag = this.newWith();
        Assertions.assertSame(synchronizedBag, synchronizedBag.asSynchronized());
    }

    @Override
    @Test
    public void topOccurrences()
    {
        super.topOccurrences();

        MutableSortedBag<String> mutable = TreeBag.newBag();
        mutable.addOccurrences("one", 1);
        mutable.addOccurrences("two", 2);
        mutable.addOccurrences("three", 3);
        mutable.addOccurrences("four", 4);
        mutable.addOccurrences("five", 5);
        mutable.addOccurrences("six", 6);
        mutable.addOccurrences("seven", 7);
        mutable.addOccurrences("eight", 8);
        mutable.addOccurrences("nine", 9);
        mutable.addOccurrences("ten", 10);
        MutableSortedBag<String> strings = mutable.asSynchronized();
        MutableList<ObjectIntPair<String>> top5 = strings.topOccurrences(5);
        Verify.assertSize(5, top5);
        Assertions.assertEquals("ten", top5.getFirst().getOne());
        Assertions.assertEquals(10, top5.getFirst().getTwo());
        Assertions.assertEquals("six", top5.getLast().getOne());
        Assertions.assertEquals(6, top5.getLast().getTwo());
    }

    @Override
    @Test
    public void bottomOccurrences()
    {
        super.bottomOccurrences();

        MutableSortedBag<String> mutable = TreeBag.newBag();
        mutable.addOccurrences("one", 1);
        mutable.addOccurrences("two", 2);
        mutable.addOccurrences("three", 3);
        mutable.addOccurrences("four", 4);
        mutable.addOccurrences("five", 5);
        mutable.addOccurrences("six", 6);
        mutable.addOccurrences("seven", 7);
        mutable.addOccurrences("eight", 8);
        mutable.addOccurrences("nine", 9);
        mutable.addOccurrences("ten", 10);
        MutableSortedBag<String> strings = mutable.asSynchronized();
        MutableList<ObjectIntPair<String>> bottom5 = strings.bottomOccurrences(5);
        Verify.assertSize(5, bottom5);
        Assertions.assertEquals("one", bottom5.getFirst().getOne());
        Assertions.assertEquals(1, bottom5.getFirst().getTwo());
        Assertions.assertEquals("five", bottom5.getLast().getOne());
        Assertions.assertEquals(5, bottom5.getLast().getTwo());
    }
}
