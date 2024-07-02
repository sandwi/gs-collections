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

package com.gs.collections.test.list;

import java.util.List;

import com.gs.collections.impl.factory.Lists;
import com.gs.collections.test.CollectionTestCase;
import org.junit.jupiter.api.Test;

import static com.gs.collections.test.IterableTestCase.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public interface ListTestCase extends CollectionTestCase
{
    @Override
    <T> List<T> newWith(T... elements);

    @Test
    default void List_get()
    {
        List<Integer> list = this.newWith(1, 2, 3);
        assertEquals(Integer.valueOf(1), list.get(0));
        assertEquals(Integer.valueOf(2), list.get(1));
        assertEquals(Integer.valueOf(3), list.get(2));
    }

    @Test
    default void List_get_negative()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.newWith(1, 2, 3).get(-1);
        });
    }

    @Test
    default void List_get_out_of_bounds()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            this.newWith(1, 2, 3).get(4);
        });
    }

    @Test
    default void List_set()
    {
        List<Integer> list = this.newWith(1, 2, 3);
        assertEquals(Integer.valueOf(2), list.set(1, 4));
        assertEquals(Lists.immutable.with(1, 4, 3), list);
    }

    @Test
    default void List_set_negative()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            List<Integer> list = this.newWith(1, 2, 3);
            assertEquals(Integer.valueOf(2), list.set(-1, 4));
            assertEquals(Lists.immutable.with(1, 4, 3), list);
        });
    }

    @Test
    default void List_set_out_of_bounds()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> {
            List<Integer> list = this.newWith(1, 2, 3);
            assertEquals(Integer.valueOf(2), list.set(4, 4));
            assertEquals(Lists.immutable.with(1, 4, 3), list);
        });
    }
}
