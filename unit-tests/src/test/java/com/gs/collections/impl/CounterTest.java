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

import com.gs.collections.impl.block.factory.Procedures;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CounterTest
{
    @Test
    public void basicLifecycle()
    {
        Counter counter = new Counter();

        Assertions.assertEquals(0, counter.getCount());
        counter.increment();
        Assertions.assertEquals(1, counter.getCount());
        counter.increment();
        Assertions.assertEquals(2, counter.getCount());
        counter.add(16);
        Assertions.assertEquals(18, counter.getCount());
        Interval.oneTo(1000).forEach(Procedures.cast(each -> counter.increment()));
        Assertions.assertEquals(1018, counter.getCount());
        Assertions.assertEquals("1018", counter.toString());

        counter.reset();
        Assertions.assertEquals(0, counter.getCount());
        counter.add(4);
        Assertions.assertEquals(4, counter.getCount());
        counter.increment();
        Assertions.assertEquals(5, counter.getCount());

        Assertions.assertEquals("5", counter.toString());
    }

    @Test
    public void equalsAndHashCode()
    {
        Verify.assertEqualsAndHashCode(new Counter(1), new Counter(1));
        Assertions.assertNotEquals(new Counter(1), new Counter(2));
    }

    @Test
    public void serialization()
    {
        Verify.assertPostSerializedEqualsAndHashCode(new Counter());
    }
}
