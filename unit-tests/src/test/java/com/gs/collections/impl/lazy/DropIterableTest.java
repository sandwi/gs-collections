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

package com.gs.collections.impl.lazy;

import com.gs.collections.api.LazyIterable;
import com.gs.collections.api.block.procedure.Procedure2;
import com.gs.collections.api.block.procedure.primitive.ObjectIntProcedure;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.math.IntegerSum;
import com.gs.collections.impl.math.Sum;
import com.gs.collections.impl.math.SumProcedure;
import com.gs.collections.impl.utility.LazyIterate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DropIterableTest extends AbstractLazyIterableTestCase
{
    private DropIterable<Integer> dropIterable;
    private DropIterable<Integer> emptyListDropIterable;
    private DropIterable<Integer> zeroCountDropIterable;
    private DropIterable<Integer> nearCountDropIterable;
    private DropIterable<Integer> sameCountDropIterable;
    private DropIterable<Integer> higherCountDropIterable;

    @BeforeEach
    public void setUp()
    {
        this.dropIterable = new DropIterable<>(Interval.oneTo(5), 2);
        this.emptyListDropIterable = new DropIterable<>(FastList.<Integer>newList(), 2);
        this.zeroCountDropIterable = new DropIterable<>(Interval.oneTo(5), 0);
        this.nearCountDropIterable = new DropIterable<>(Interval.oneTo(5), 4);
        this.sameCountDropIterable = new DropIterable<>(Interval.oneTo(5), 5);
        this.higherCountDropIterable = new DropIterable<>(Interval.oneTo(5), 6);
    }

    @Test
    public void negative_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new DropIterable<>(Interval.oneTo(5), -1);
        });
    }

    @Test
    public void basic()
    {
        Assertions.assertEquals(3, this.dropIterable.size());
        Assertions.assertEquals(FastList.newListWith(3, 4, 5), this.dropIterable.toList());

        Assertions.assertEquals(0, this.emptyListDropIterable.size());
        Assertions.assertEquals(5, this.zeroCountDropIterable.size());
        Assertions.assertEquals(1, this.nearCountDropIterable.size());
        Assertions.assertEquals(0, this.sameCountDropIterable.size());
        Assertions.assertEquals(0, this.higherCountDropIterable.size());
    }

    @Test
    public void forEach()
    {
        Sum sum1 = new IntegerSum(0);
        this.dropIterable.forEach(new SumProcedure<>(sum1));
        Assertions.assertEquals(12, sum1.getValue().intValue());

        Sum sum2 = new IntegerSum(0);
        this.emptyListDropIterable.forEach(new SumProcedure<>(sum2));
        Assertions.assertEquals(0, sum2.getValue().intValue());

        Sum sum3 = new IntegerSum(0);
        this.zeroCountDropIterable.forEach(new SumProcedure<>(sum3));
        Assertions.assertEquals(15, sum3.getValue().intValue());

        Sum sum5 = new IntegerSum(0);
        this.nearCountDropIterable.forEach(new SumProcedure<>(sum5));
        Assertions.assertEquals(5, sum5.getValue().intValue());

        Sum sum6 = new IntegerSum(0);
        this.sameCountDropIterable.forEach(new SumProcedure<>(sum6));
        Assertions.assertEquals(0, sum6.getValue().intValue());

        Sum sum7 = new IntegerSum(0);
        this.higherCountDropIterable.forEach(new SumProcedure<>(sum7));
        Assertions.assertEquals(0, sum7.getValue().intValue());
    }

    @Test
    public void forEachWithIndex()
    {
        Sum sum = new IntegerSum(0);
        FastList<Integer> indices = FastList.newList(5);
        ObjectIntProcedure<Integer> indexRecordingAndSumProcedure = (each, index) -> {
            indices.add(index);
            sum.add(each);
        };

        this.dropIterable.forEachWithIndex(indexRecordingAndSumProcedure);
        Assertions.assertEquals(FastList.newListWith(0, 1, 2), indices);
        Assertions.assertEquals(12, sum.getValue().intValue());

        indices.clear();
        sum.add(sum.getValue().intValue() * -1);
        this.emptyListDropIterable.forEachWithIndex(indexRecordingAndSumProcedure);
        Assertions.assertEquals(0, indices.size());

        indices.clear();
        sum.add(sum.getValue().intValue() * -1);
        this.zeroCountDropIterable.forEachWithIndex(indexRecordingAndSumProcedure);
        Assertions.assertEquals(FastList.newListWith(0, 1, 2, 3, 4), indices);
        Assertions.assertEquals(15, sum.getValue().intValue());

        indices.clear();
        sum.add(sum.getValue().intValue() * -1);
        this.nearCountDropIterable.forEachWithIndex(indexRecordingAndSumProcedure);
        Assertions.assertEquals(FastList.newListWith(0), indices);
        Assertions.assertEquals(5, sum.getValue().intValue());

        indices.clear();
        sum.add(sum.getValue().intValue() * -1);
        this.sameCountDropIterable.forEachWithIndex(indexRecordingAndSumProcedure);
        Assertions.assertEquals(0, indices.size());

        indices.clear();
        sum.add(sum.getValue().intValue() * -1);
        this.higherCountDropIterable.forEachWithIndex(indexRecordingAndSumProcedure);
        Assertions.assertEquals(0, indices.size());
    }

    @Test
    public void forEachWith()
    {
        Procedure2<Integer, Sum> sumAdditionProcedure = (each, sum) -> sum.add(each);

        Sum sum1 = new IntegerSum(0);
        this.dropIterable.forEachWith(sumAdditionProcedure, sum1);
        Assertions.assertEquals(12, sum1.getValue().intValue());

        Sum sum2 = new IntegerSum(0);
        this.emptyListDropIterable.forEachWith(sumAdditionProcedure, sum2);
        Assertions.assertEquals(0, sum2.getValue().intValue());

        Sum sum3 = new IntegerSum(0);
        this.zeroCountDropIterable.forEachWith(sumAdditionProcedure, sum3);
        Assertions.assertEquals(15, sum3.getValue().intValue());

        Sum sum5 = new IntegerSum(0);
        this.nearCountDropIterable.forEachWith(sumAdditionProcedure, sum5);
        Assertions.assertEquals(5, sum5.getValue().intValue());

        Sum sum6 = new IntegerSum(0);
        this.sameCountDropIterable.forEachWith(sumAdditionProcedure, sum6);
        Assertions.assertEquals(0, sum6.getValue().intValue());

        Sum sum7 = new IntegerSum(0);
        this.higherCountDropIterable.forEachWith(sumAdditionProcedure, sum7);
        Assertions.assertEquals(0, sum7.getValue().intValue());
    }

    @Override
    @Test
    public void iterator()
    {
        Sum sum1 = new IntegerSum(0);
        for (Integer each : this.dropIterable)
        {
            sum1.add(each);
        }
        Assertions.assertEquals(12, sum1.getValue().intValue());

        Sum sum2 = new IntegerSum(0);
        for (Integer each : this.emptyListDropIterable)
        {
            sum2.add(each);
        }
        Assertions.assertEquals(0, sum2.getValue().intValue());

        Sum sum3 = new IntegerSum(0);
        for (Integer each : this.zeroCountDropIterable)
        {
            sum3.add(each);
        }
        Assertions.assertEquals(15, sum3.getValue().intValue());

        Sum sum5 = new IntegerSum(0);
        for (Integer each : this.nearCountDropIterable)
        {
            sum5.add(each);
        }
        Assertions.assertEquals(5, sum5.getValue().intValue());

        Sum sum6 = new IntegerSum(0);
        for (Integer each : this.sameCountDropIterable)
        {
            sum6.add(each);
        }
        Assertions.assertEquals(0, sum6.getValue().intValue());

        Sum sum7 = new IntegerSum(0);
        for (Integer each : this.higherCountDropIterable)
        {
            sum7.add(each);
        }
        Assertions.assertEquals(0, sum7.getValue().intValue());
    }

    @Override
    protected <T> LazyIterable<T> newWith(T... elements)
    {
        return LazyIterate.drop(FastList.newListWith(elements), 0);
    }

    @Override
    @Test
    public void distinct()
    {
        super.distinct();
        Assertions.assertEquals(
                FastList.newListWith(2, 3, 4, 5),
                new DropIterable<>(FastList.newListWith(1, 1, 2, 3, 3, 3, 4, 5), 2).distinct().toList());
    }
}
