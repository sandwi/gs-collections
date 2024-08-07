/*
 * Copyright 2012 Goldman Sachs.
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

import java.util.Random;

import com.gs.collections.impl.set.mutable.primitive.IntHashSet;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntHashSetAddTest
{
    private static final Logger LOGGER = LoggerFactory.getLogger(IntHashSetAddTest.class);

    @Test
    @Category(PerformanceTests.class)
    public void testHashPut()
    {
        this.runIntHashPut();
        this.runIntHashPut();
        this.runIntHashPut();
        this.runIntHashPut();
        this.runIntHashPut();
    }

    private void runIntHashPut()
    {
        System.currentTimeMillis();
        Random r = new Random(123412123);
        int[] ints = new int[1000000];
        for (int i = 0; i < ints.length; i++)
        {
            ints[i] = r.nextInt();
        }
        this.runHashPut(ints);
    }

    private void runHashPut(int[] values)
    {
        for (int i = 0; i < 100; i++)
        {
            this.runHashContains(this.runHashPut(values, 1000, 1000), values, 1000, 1000);
        }
        for (int i = 0; i < 100; i++)
        {
            this.runHashContains(this.runHashPut(values, 1000000, 1), values, 1000000, 1);
        }
        IntHashSet set = null;
        long now1 = System.currentTimeMillis();
        for (int i = 0; i < 100; i++)
        {
            set = this.runHashPut(values, 1000000, 1);
        }
        long time1 = System.currentTimeMillis() - now1;
        LOGGER.info("IntHashSet, set size 1,000,000, puts/msec: {}", 100000000 / time1);
        long now2 = System.currentTimeMillis();
        for (int i = 0; i < 100; i++)
        {
            this.runHashContains(this.runHashPut(values, 1000000, 1), values, 1000000, 1);
        }
        long time2 = System.currentTimeMillis() - now2;
        LOGGER.info("IntHashSet, set size 1,000,000, contains/msec: {}", 100000000 / time2);
    }

    public IntHashSet runHashPut(int[] values, int length, int runs)
    {
        IntHashSet set = null;
        for (int i = 0; i < runs; i++)
        {
            set = new IntHashSet(8);
            for (int j = 0; j < length; j++)
            {
                set.add(values[j]);
            }
        }
        return set;
    }

    public boolean runHashContains(IntHashSet set, int[] values, int length, int runs)
    {
        boolean result = false;
        for (int i = 0; i < runs; i++)
        {
            for (int j = 0; j < length; j++)
            {
                result = set.contains(values[j]);
            }
        }
        return result;
    }
}
