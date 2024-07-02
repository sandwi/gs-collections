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

package com.gs.collections.impl.list.immutable;

import java.util.ListIterator;

import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.impl.factory.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ImmutableSubListTest extends AbstractImmutableListTestCase
{
    @Override
    protected ImmutableList<Integer> classUnderTest()
    {
        return Lists.immutable.of(0, 1, 2, 3, 4, 5, 6, 7).subList(1, 5);
    }

    @Test
    public void testSubListListIterator()
    {
        ImmutableList<Integer> subList = this.classUnderTest();
        ListIterator<Integer> iterator = subList.listIterator();
        Assertions.assertTrue(iterator.hasNext());
        Assertions.assertFalse(iterator.hasPrevious());
        Assertions.assertEquals(Integer.valueOf(1), iterator.next());
        Assertions.assertEquals(Integer.valueOf(2), iterator.next());
        Assertions.assertEquals(Integer.valueOf(3), iterator.next());
        Assertions.assertTrue(iterator.hasPrevious());
        Assertions.assertEquals(Integer.valueOf(3), iterator.previous());
        Assertions.assertEquals(Integer.valueOf(2), iterator.previous());
        Assertions.assertEquals(Integer.valueOf(1), iterator.previous());
    }

    @Test
    public void testSubListListIteratorSet_throws()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            ImmutableList<Integer> subList = this.classUnderTest();
            ListIterator<Integer> iterator = subList.listIterator();
            iterator.set(4);
        });
    }

    @Test
    public void testSubListListIteratorRemove_throws()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            ImmutableList<Integer> subList = this.classUnderTest();
            ListIterator<Integer> iterator = subList.listIterator();
            iterator.remove();
        });
    }

    @Test
    public void testSubListListIteratorAdd_throws()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            ImmutableList<Integer> subList = this.classUnderTest();
            ListIterator<Integer> iterator = subList.listIterator();
            iterator.add(4);
        });
    }
}
