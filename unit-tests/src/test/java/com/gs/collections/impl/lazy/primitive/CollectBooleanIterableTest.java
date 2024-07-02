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

package com.gs.collections.impl.lazy.primitive;

import com.gs.collections.api.BooleanIterable;
import com.gs.collections.api.LazyBooleanIterable;
import com.gs.collections.api.iterator.BooleanIterator;
import com.gs.collections.impl.bag.mutable.primitive.BooleanHashBag;
import com.gs.collections.impl.block.factory.PrimitiveFunctions;
import com.gs.collections.impl.block.factory.primitive.BooleanPredicates;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.set.mutable.primitive.BooleanHashSet;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CollectBooleanIterableTest
{
    private final BooleanIterable booleanIterable = Interval.zeroTo(2).collectBoolean(PrimitiveFunctions.integerIsPositive());

    @Test
    public void iterator()
    {
        long count = 0;
        long isTrueCount = 0;
        BooleanIterator iterator = this.booleanIterable.booleanIterator();
        while (iterator.hasNext())
        {
            count++;
            if (iterator.next())
            {
                isTrueCount++;
            }
        }
        Assertions.assertEquals(3L, count);
        Assertions.assertEquals(2L, isTrueCount);
    }

    @Test
    public void size()
    {
        Assertions.assertEquals(3, this.booleanIterable.size());
    }

    @Test
    public void empty()
    {
        Assertions.assertTrue(this.booleanIterable.notEmpty());
        Assertions.assertFalse(this.booleanIterable.isEmpty());
    }

    @Test
    public void forEach()
    {
        long[] value = new long[2];
        this.booleanIterable.forEach(each -> {
            value[0]++;
            if (each)
            {
                value[1]++;
            }
        });
        Assertions.assertEquals(3, value[0]);
        Assertions.assertEquals(2, value[1]);
    }

    @Test
    public void count()
    {
        Assertions.assertEquals(2, this.booleanIterable.count(BooleanPredicates.equal(true)));
        Assertions.assertEquals(1, this.booleanIterable.count(BooleanPredicates.equal(false)));
    }

    @Test
    public void anySatisfy()
    {
        Assertions.assertTrue(this.booleanIterable.anySatisfy(BooleanPredicates.equal(true)));
    }

    @Test
    public void noneSatisfy()
    {
        Assertions.assertFalse(this.booleanIterable.noneSatisfy(BooleanPredicates.equal(true)));
    }

    @Test
    public void allSatisfy()
    {
        Assertions.assertFalse(this.booleanIterable.allSatisfy(BooleanPredicates.equal(false)));
    }

    @Test
    public void select()
    {
        Assertions.assertEquals(2, this.booleanIterable.select(BooleanPredicates.equal(true)).size());
        Assertions.assertEquals(1, this.booleanIterable.select(BooleanPredicates.equal(false)).size());
    }

    @Test
    public void reject()
    {
        Assertions.assertEquals(1, this.booleanIterable.reject(BooleanPredicates.equal(true)).size());
        Assertions.assertEquals(2, this.booleanIterable.reject(BooleanPredicates.equal(false)).size());
    }

    @Test
    public void detectIfNone()
    {
        Assertions.assertTrue(this.booleanIterable.detectIfNone(BooleanPredicates.equal(true), false));
    }

    @Test
    public void toArray()
    {
        boolean[] actual = Interval.zeroTo(2).collectBoolean(PrimitiveFunctions.integerIsPositive()).toArray();
        Assertions.assertEquals(3, actual.length);
        Assertions.assertFalse(actual[0]);
        Assertions.assertTrue(actual[1]);
        Assertions.assertTrue(actual[2]);
    }

    @Test
    public void contains()
    {
        Assertions.assertFalse(Interval.fromTo(-4, 0).collectBoolean(PrimitiveFunctions.integerIsPositive()).contains(true));
        Assertions.assertTrue(Interval.fromTo(-2, 2).collectBoolean(PrimitiveFunctions.integerIsPositive()).contains(true));
    }

    @Test
    public void containsAllArray()
    {
        BooleanIterable booleanIterable = Interval.oneTo(3).collectBoolean(PrimitiveFunctions.integerIsPositive());
        Assertions.assertTrue(booleanIterable.containsAll(true));
        Assertions.assertTrue(booleanIterable.containsAll(true, true));
        Assertions.assertFalse(booleanIterable.containsAll(false));
        Assertions.assertFalse(booleanIterable.containsAll(false, false));
    }

    @Test
    public void containsAllIterable()
    {
        BooleanIterable booleanIterable = Interval.oneTo(3).collectBoolean(PrimitiveFunctions.integerIsPositive());
        Assertions.assertTrue(booleanIterable.containsAll(BooleanArrayList.newListWith(true)));
        Assertions.assertTrue(booleanIterable.containsAll(BooleanArrayList.newListWith(true, true)));
        Assertions.assertFalse(booleanIterable.containsAll(BooleanArrayList.newListWith(false)));
        Assertions.assertFalse(booleanIterable.containsAll(BooleanArrayList.newListWith(false, false)));
    }

    @Test
    public void collect()
    {
        Assertions.assertEquals(FastList.newListWith("false", "true", "true"), this.booleanIterable.collect(String::valueOf).toList());
    }

    @Test
    public void testToString()
    {
        Assertions.assertEquals("[false, true, true]", this.booleanIterable.toString());
    }

    @Test
    public void makeString()
    {
        Assertions.assertEquals("false, true, true", this.booleanIterable.makeString());
        Assertions.assertEquals("false/true/true", this.booleanIterable.makeString("/"));
        Assertions.assertEquals("[false, true, true]", this.booleanIterable.makeString("[", ", ", "]"));
    }

    @Test
    public void appendString()
    {
        StringBuilder appendable = new StringBuilder();
        this.booleanIterable.appendString(appendable);
        Assertions.assertEquals("false, true, true", appendable.toString());
        StringBuilder appendable2 = new StringBuilder();
        this.booleanIterable.appendString(appendable2, "/");
        Assertions.assertEquals("false/true/true", appendable2.toString());
        StringBuilder appendable3 = new StringBuilder();
        this.booleanIterable.appendString(appendable3, "[", ", ", "]");
        Assertions.assertEquals(this.booleanIterable.toString(), appendable3.toString());
    }

    @Test
    public void toList()
    {
        Assertions.assertEquals(BooleanArrayList.newListWith(false, true, true), this.booleanIterable.toList());
    }

    @Test
    public void toSet()
    {
        Assertions.assertEquals(BooleanHashSet.newSetWith(false, true), this.booleanIterable.toSet());
    }

    @Test
    public void toBag()
    {
        Assertions.assertEquals(BooleanHashBag.newBagWith(false, true, true), this.booleanIterable.toBag());
    }

    @Test
    public void asLazy()
    {
        Assertions.assertEquals(this.booleanIterable.toSet(), this.booleanIterable.asLazy().toSet());
        Verify.assertInstanceOf(LazyBooleanIterable.class, this.booleanIterable.asLazy());
    }
}
