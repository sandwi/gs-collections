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

package com.gs.collections.impl.list;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.gs.collections.api.LazyIterable;
import com.gs.collections.api.block.procedure.primitive.ObjectIntProcedure;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.impl.block.factory.IntegerPredicates;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.function.AddFunction;
import com.gs.collections.impl.block.procedure.CollectionAddProcedure;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.math.IntegerSum;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.utility.ArrayIterate;
import com.gs.collections.impl.utility.Iterate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class IntervalTest
{
    @Test
    public void into()
    {
        int sum = Interval.oneTo(5)
                .select(Predicates.lessThan(5))
                .into(FastList.<Integer>newList())
                .injectInto(0, AddFunction.INTEGER_TO_INT);
        Assertions.assertEquals(10, sum);
    }

    @Test
    public void fromAndToAndBy()
    {
        Interval interval = Interval.from(1);
        Interval interval2 = interval.to(10);
        Interval interval3 = interval2.by(2);
        Verify.assertEqualsAndHashCode(interval, Interval.fromTo(1, 1));
        Verify.assertEqualsAndHashCode(interval2, Interval.fromTo(1, 10));
        Verify.assertEqualsAndHashCode(interval3, Interval.fromToBy(1, 10, 2));
    }

    @Test
    public void fromToBy_throws_step_size_zero()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            Interval.fromToBy(0, 0, 0);
        });
    }

    @Test
    public void oneToBy_throws_step_size_zero()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            Interval.oneToBy(1, 0);
        });
    }

    @Test
    public void zeroToBy_throws_step_size_zero()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            Interval.zeroToBy(0, 0);
        });
    }

    @Test
    public void equalsAndHashCode()
    {
        Interval interval1 = Interval.oneTo(5);
        Interval interval2 = Interval.oneTo(5);
        Interval interval3 = Interval.zeroTo(5);
        Verify.assertPostSerializedEqualsAndHashCode(interval1);
        Verify.assertEqualsAndHashCode(interval1, interval2);
        Assertions.assertNotEquals(interval1, interval3);
        Assertions.assertNotEquals(interval3, interval1);

        Verify.assertEqualsAndHashCode(Interval.fromToBy(1, 5, 2), Interval.fromToBy(1, 6, 2));
        Verify.assertEqualsAndHashCode(FastList.newListWith(1, 2, 3), Interval.fromTo(1, 3));
        Verify.assertEqualsAndHashCode(FastList.newListWith(3, 2, 1), Interval.fromTo(3, 1));

        Assertions.assertNotEquals(FastList.newListWith(1, 2, 3, 4), Interval.fromTo(1, 3));
        Assertions.assertNotEquals(FastList.newListWith(1, 2, 4), Interval.fromTo(1, 3));
        Assertions.assertNotEquals(FastList.newListWith(3, 2, 0), Interval.fromTo(3, 1));

        Verify.assertEqualsAndHashCode(FastList.newListWith(-1, -2, -3), Interval.fromTo(-1, -3));
    }

    @Test
    public void forEachOnFromToInterval()
    {
        MutableList<Integer> result = Lists.mutable.of();
        Interval interval = Interval.oneTo(5);
        interval.forEach(CollectionAddProcedure.on(result));
        Assertions.assertEquals(FastList.newListWith(1, 2, 3, 4, 5), result);
    }

    @Test
    public void forEachWithExecutor()
    {
        MutableList<Integer> result = Lists.mutable.of();
        Interval interval = Interval.oneTo(5);
        interval.forEach(CollectionAddProcedure.on(result), Executors.newSingleThreadExecutor());
        Assertions.assertEquals(FastList.newListWith(1, 2, 3, 4, 5), result);
    }

    @Test
    public void forEachWithExecutorInReverse()
    {
        MutableList<Integer> result = Lists.mutable.of();
        Interval interval = Interval.fromToBy(5, 1, -1);
        interval.forEach(CollectionAddProcedure.on(result), Executors.newSingleThreadExecutor());
        Assertions.assertEquals(FastList.newListWith(5, 4, 3, 2, 1), result);
    }

    @Test
    public void runWithExecutor() throws InterruptedException
    {
        MutableList<String> result = Lists.mutable.of();
        ExecutorService service = Executors.newSingleThreadExecutor();
        Interval.oneTo(3).run(() -> result.add(null), service);
        service.shutdown();
        service.awaitTermination(20, TimeUnit.SECONDS);
        Assertions.assertEquals(FastList.<String>newListWith(null, null, null), result);
    }

    @Test
    public void runWithExecutorInReverse() throws InterruptedException
    {
        MutableList<String> result = Lists.mutable.of();
        ExecutorService service = Executors.newSingleThreadExecutor();
        Interval.fromTo(3, 1).run(() -> result.add(null), service);
        service.shutdown();
        service.awaitTermination(20, TimeUnit.SECONDS);
        Assertions.assertEquals(FastList.<String>newListWith(null, null, null), result);
    }

    @Test
    public void reverseForEachOnFromToInterval()
    {
        List<Integer> result = new ArrayList<>();
        Interval interval = Interval.oneTo(5);
        interval.reverseForEach(result::add);
        Verify.assertSize(5, result);
        Verify.assertContains(1, result);
        Verify.assertContains(5, result);
        Assertions.assertEquals(Integer.valueOf(5), Iterate.getFirst(result));
        Assertions.assertEquals(Integer.valueOf(1), Iterate.getLast(result));

        result.clear();
        interval.reverseThis().reverseForEach(result::add);
        Verify.assertSize(5, result);
        Verify.assertContains(1, result);
        Verify.assertContains(5, result);
        Assertions.assertEquals(Integer.valueOf(1), Iterate.getFirst(result));
        Assertions.assertEquals(Integer.valueOf(5), Iterate.getLast(result));
    }

    @Test
    public void forEachOnFromToByInterval()
    {
        List<Integer> result = new ArrayList<>();
        Interval interval = Interval.fromToBy(1, 5, 2);
        interval.forEach(CollectionAddProcedure.on(result));
        Verify.assertSize(3, result);
        Verify.assertContains(1, result);
        Verify.assertNotContains(2, result);
        Verify.assertContains(5, result);
    }

    @Test
    public void forEachOnFromToByInterval2()
    {
        List<Integer> result = new ArrayList<>();
        Interval interval = Interval.fromToBy(5, 1, -2);
        interval.forEach(CollectionAddProcedure.on(result));
        Verify.assertSize(3, result);
        Verify.assertContains(1, result);
        Verify.assertNotContains(2, result);
        Verify.assertContains(5, result);
    }

    @Test
    public void injectIntoOnFromToByInterval()
    {
        Interval interval = Interval.oneTo(5);
        Assertions.assertEquals(Integer.valueOf(20), interval.injectInto(5, AddFunction.INTEGER));
        Assertions.assertEquals(Integer.valueOf(20), interval.reverseThis().injectInto(5, AddFunction.INTEGER));
    }

    @Test
    public void sumInterval()
    {
        int sum = Interval.oneTo(5).injectInto(0, AddFunction.INTEGER_TO_INT);
        Assertions.assertEquals(15, sum);
    }

    @Test
    public void maxInterval()
    {
        Integer value = Interval.oneTo(5).injectInto(0, Integer::max);
        Assertions.assertEquals(5, value.intValue());
    }

    @Test
    public void reverseInjectIntoOnFromToByInterval()
    {
        Interval interval = Interval.oneTo(5);
        Assertions.assertEquals(Integer.valueOf(20), interval.reverseInjectInto(5, AddFunction.INTEGER));
        Assertions.assertEquals(Integer.valueOf(20), interval.reverseThis().reverseInjectInto(5, AddFunction.INTEGER));
    }

    @Test
    public void collectOnFromToByInterval()
    {
        Interval interval = Interval.oneToBy(5, 2);
        LazyIterable<String> result = interval.collect(String::valueOf);
        Verify.assertIterableSize(3, result);
        Verify.assertContainsAll(result, "1", "5");
        Verify.assertNotContains(result, "2");
    }

    @Test
    public void collectOnFromToInterval()
    {
        Interval interval = Interval.oneTo(5);
        LazyIterable<String> result = interval.collect(String::valueOf);
        Verify.assertIterableSize(5, result);
        Verify.assertContainsAll(result, "1", "5");
    }

    @Test
    public void selectOnFromToInterval()
    {
        Interval interval = Interval.oneTo(5);
        Assertions.assertEquals(FastList.newListWith(2, 4), interval.select(IntegerPredicates.isEven()).toList());
        Assertions.assertEquals(FastList.newListWith(4, 2), interval.reverseThis().select(IntegerPredicates.isEven()).toList());
    }

    @Test
    public void rejectOnFromToInterval()
    {
        Interval interval = Interval.oneTo(5);
        Assertions.assertEquals(FastList.newListWith(1, 3, 5), interval.reject(IntegerPredicates.isEven()).toList());
        Assertions.assertEquals(FastList.newListWith(5, 3, 1), interval.reverseThis().reject(IntegerPredicates.isEven()).toList());
    }

    @Test
    public void reverseThis()
    {
        Interval interval = Interval.fromToBy(5, 1, -1);
        Interval interval2 = interval.reverseThis();
        List<Integer> result = new ArrayList<>();
        interval2.forEach(CollectionAddProcedure.on(result));
        Assertions.assertEquals(FastList.newListWith(1, 2, 3, 4, 5), result);
    }

    @Test
    public void intervalAsArray()
    {
        Assertions.assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, Interval.toArray(1, 5));
    }

    @Test
    public void intervalAsIntArray()
    {
        Assertions.assertArrayEquals(new int[]{1, 2, 3, 4, 5}, Interval.fromTo(1, 5).toIntArray());
    }

    @Test
    public void intervalAsReverseArray()
    {
        Integer[] array = Interval.toReverseArray(1, 5);
        Verify.assertSize(5, array);
        Assertions.assertTrue(ArrayIterate.contains(array, 1));
        Assertions.assertTrue(ArrayIterate.contains(array, 5));
        Assertions.assertEquals(ArrayIterate.getFirst(array), Integer.valueOf(5));
        Assertions.assertEquals(ArrayIterate.getLast(array), Integer.valueOf(1));
    }

    @Test
    public void intervalToList()
    {
        MutableList<Integer> list = Interval.fromTo(1, 5).toList();
        Verify.assertSize(5, list);
        Verify.assertContainsAll(list, 1, 2, 3, 4, 5);
    }

    @Test
    public void intervalAsReverseList()
    {
        MutableList<Integer> list = Interval.toReverseList(1, 5);
        Verify.assertSize(5, list);
        Verify.assertStartsWith(list, 5, 4, 3, 2, 1);
    }

    @Test
    public void intervalToSet()
    {
        MutableSet<Integer> set = Interval.toSet(1, 5);
        Verify.assertSize(5, set);
        Verify.assertContainsAll(set, 1, 2, 3, 4, 5);
    }

    @Test
    public void invalidIntervals()
    {
        try
        {
            Interval.fromToBy(5, 1, 2);
            Assertions.fail();
        }
        catch (IllegalArgumentException ignored)
        {
        }
        try
        {
            Interval.fromToBy(5, 1, 0);
            Assertions.fail();
        }
        catch (IllegalArgumentException ignored)
        {
        }
        try
        {
            Interval.fromToBy(-5, 1, -1);
            Assertions.fail();
        }
        catch (IllegalArgumentException ignored)
        {
        }
    }

    @Test
    public void toFastList()
    {
        Interval interval = Interval.evensFromTo(0, 10);
        FastList<Integer> toList = (FastList<Integer>) interval.toList();
        Verify.assertStartsWith(toList, 0, 2, 4, 6, 8, 10);
        Verify.assertSize(6, toList);
    }

    @Test
    public void toSet()
    {
        Interval interval = Interval.evensFromTo(0, 10);
        MutableSet<Integer> set = interval.toSet();
        Verify.assertContainsAll(set, 0, 2, 4, 6, 8, 10);
        Verify.assertSize(6, set);
    }

    @Test
    public void testToString()
    {
        Interval interval = Interval.evensFromTo(0, 10);
        Assertions.assertEquals("Interval from: 0 to: 10 step: 2 size: 6", interval.toString());
    }

    @Test
    public void evens()
    {
        Interval interval = Interval.evensFromTo(0, 10);
        int[] evens = {0, 2, 4, 6, 8, 10};
        int[] odds = {1, 3, 5, 7, 9};
        this.assertIntervalContainsAll(interval, evens);
        this.denyIntervalContainsAny(interval, odds);
        Assertions.assertEquals(6, interval.size());

        Interval reverseInterval = Interval.evensFromTo(10, 0);
        this.assertIntervalContainsAll(reverseInterval, evens);
        this.denyIntervalContainsAny(reverseInterval, odds);
        Assertions.assertEquals(6, reverseInterval.size());

        Interval negativeInterval = Interval.evensFromTo(-5, 5);
        int[] negativeEvens = {-4, -2, 0, 2, 4};
        int[] negativeOdds = {-3, -1, 1, 3};
        this.assertIntervalContainsAll(negativeInterval, negativeEvens);
        this.denyIntervalContainsAny(negativeInterval, negativeOdds);
        Assertions.assertEquals(5, negativeInterval.size());

        Interval reverseNegativeInterval = Interval.evensFromTo(5, -5);
        this.assertIntervalContainsAll(reverseNegativeInterval, negativeEvens);
        this.denyIntervalContainsAny(reverseNegativeInterval, negativeOdds);
        Assertions.assertEquals(5, reverseNegativeInterval.size());
    }

    private void assertIntervalContainsAll(Interval interval, int[] expectedValues)
    {
        for (int value : expectedValues)
        {
            Assertions.assertTrue(interval.contains(value));
        }
    }

    private void denyIntervalContainsAny(Interval interval, int[] expectedValues)
    {
        for (int value : expectedValues)
        {
            Assertions.assertFalse(interval.contains(value));
        }
    }

    @Test
    public void odds()
    {
        Interval interval1 = Interval.oddsFromTo(0, 10);
        Assertions.assertTrue(interval1.containsAll(1, 3, 5, 7, 9));
        Assertions.assertTrue(interval1.containsNone(2, 4, 6, 8));
        Assertions.assertEquals(5, interval1.size());

        Interval reverseInterval1 = Interval.oddsFromTo(10, 0);
        Assertions.assertTrue(reverseInterval1.containsAll(1, 3, 5, 7, 9));
        Assertions.assertTrue(reverseInterval1.containsNone(0, 2, 4, 6, 8, 10));
        Assertions.assertEquals(5, reverseInterval1.size());

        Interval interval2 = Interval.oddsFromTo(-5, 5);
        Assertions.assertTrue(interval2.containsAll(-5, -3, -1, 1, 3, 5));
        Assertions.assertTrue(interval2.containsNone(-4, -2, 0, 2, 4));
        Assertions.assertEquals(6, interval2.size());

        Interval reverseInterval2 = Interval.oddsFromTo(5, -5);
        Assertions.assertTrue(reverseInterval2.containsAll(-5, -3, -1, 1, 3, 5));
        Assertions.assertTrue(reverseInterval2.containsNone(-4, -2, 0, 2, 4));
        Assertions.assertEquals(6, reverseInterval2.size());
    }

    @Test
    public void size()
    {
        Assertions.assertEquals(100, Interval.fromTo(1, 100).size());
        Assertions.assertEquals(50, Interval.fromToBy(1, 100, 2).size());
        Assertions.assertEquals(34, Interval.fromToBy(1, 100, 3).size());
        Assertions.assertEquals(25, Interval.fromToBy(1, 100, 4).size());
        Assertions.assertEquals(20, Interval.fromToBy(1, 100, 5).size());
        Assertions.assertEquals(17, Interval.fromToBy(1, 100, 6).size());
        Assertions.assertEquals(15, Interval.fromToBy(1, 100, 7).size());
        Assertions.assertEquals(13, Interval.fromToBy(1, 100, 8).size());
        Assertions.assertEquals(12, Interval.fromToBy(1, 100, 9).size());
        Assertions.assertEquals(10, Interval.fromToBy(1, 100, 10).size());
        Assertions.assertEquals(11, Interval.fromTo(0, 10).size());
        Assertions.assertEquals(1, Interval.zero().size());
        Assertions.assertEquals(11, Interval.fromTo(0, -10).size());
        Assertions.assertEquals(3, Interval.evensFromTo(2, -2).size());
        Assertions.assertEquals(2, Interval.oddsFromTo(2, -2).size());
    }

    @Test
    public void contains()
    {
        Assertions.assertTrue(Interval.zero().contains(0));
        Assertions.assertTrue(Interval.oneTo(5).containsAll(1, 5));
        Assertions.assertTrue(Interval.oneTo(5).containsNone(6, 7));
        Assertions.assertFalse(Interval.oneTo(5).containsAll(1, 6));
        Assertions.assertFalse(Interval.oneTo(5).containsNone(1, 6));
        Assertions.assertFalse(Interval.oneTo(5).contains(0));
        Assertions.assertTrue(Interval.fromTo(-1, -5).containsAll(-1, -5));
        Assertions.assertFalse(Interval.fromTo(-1, -5).contains(1));

        Assertions.assertTrue(Interval.zero().contains(Integer.valueOf(0)));
        Assertions.assertFalse(Interval.oneTo(5).contains(Integer.valueOf(0)));
        Assertions.assertFalse(Interval.fromTo(-1, -5).contains(Integer.valueOf(1)));

        Assertions.assertFalse(Interval.zeroTo(5).contains(new Object()));
    }

    @Test
    public void factorial()
    {
        Verify.assertThrows(IllegalStateException.class, () -> Interval.fromTo(-1, -5).factorial());
        Assertions.assertEquals(1, Interval.zero().factorial().intValue());
        Assertions.assertEquals(1, Interval.oneTo(1).factorial().intValue());
        Assertions.assertEquals(6, Interval.oneTo(3).factorial().intValue());
        Assertions.assertEquals(2432902008176640000L, Interval.oneTo(20).factorial().longValue());
        Assertions.assertEquals(new BigInteger("51090942171709440000"), Interval.oneTo(21).factorial());
        Assertions.assertEquals(new BigInteger("1405006117752879898543142606244511569936384000000000"), Interval.oneTo(42).factorial());
    }

    @Test
    public void product()
    {
        Assertions.assertEquals(0, Interval.zero().product().intValue());
        Assertions.assertEquals(0, Interval.fromTo(-1, 1).product().intValue());
        Assertions.assertEquals(2, Interval.fromTo(-2, -1).product().intValue());
        Assertions.assertEquals(-6, Interval.fromTo(-3, -1).product().intValue());
        Assertions.assertEquals(200, Interval.fromToBy(10, 20, 10).product().intValue());
        Assertions.assertEquals(200, Interval.fromToBy(-10, -20, -10).product().intValue());
        Assertions.assertEquals(-6000, Interval.fromToBy(-10, -30, -10).product().intValue());
        Assertions.assertEquals(6000, Interval.fromToBy(30, 10, -10).product().intValue());
        Assertions.assertEquals(6000, Interval.fromToBy(30, 10, -10).reverseThis().product().intValue());
    }

    @Test
    public void iterator()
    {
        Interval zero = Interval.zero();
        Iterator<Integer> zeroIterator = zero.iterator();
        Assertions.assertTrue(zeroIterator.hasNext());
        Assertions.assertEquals(Integer.valueOf(0), zeroIterator.next());
        Assertions.assertFalse(zeroIterator.hasNext());
        Interval oneToFive = Interval.oneTo(5);
        Iterator<Integer> oneToFiveIterator = oneToFive.iterator();
        for (int i = 1; i < 6; i++)
        {
            Assertions.assertTrue(oneToFiveIterator.hasNext());
            Assertions.assertEquals(Integer.valueOf(i), oneToFiveIterator.next());
        }
        Verify.assertThrows(NoSuchElementException.class, (Runnable) oneToFiveIterator::next);
        Interval threeToNegativeThree = Interval.fromTo(3, -3);
        Iterator<Integer> threeToNegativeThreeIterator = threeToNegativeThree.iterator();
        for (int i = 3; i > -4; i--)
        {
            Assertions.assertTrue(threeToNegativeThreeIterator.hasNext());
            Assertions.assertEquals(Integer.valueOf(i), threeToNegativeThreeIterator.next());
        }
        Verify.assertThrows(NoSuchElementException.class, (Runnable) threeToNegativeThreeIterator::next);
        Verify.assertThrows(UnsupportedOperationException.class, () -> Interval.zeroTo(10).iterator().remove());
    }

    @Test
    public void forEachWithIndex()
    {
        IntegerSum sum = new IntegerSum(0);
        Interval.oneTo(5).forEachWithIndex((ObjectIntProcedure<Integer>) (each, index) -> sum.add(each + index));
        Assertions.assertEquals(25, sum.getIntSum());
        IntegerSum zeroSum = new IntegerSum(0);
        Interval.fromTo(0, -4).forEachWithIndex((ObjectIntProcedure<Integer>) (each, index) -> zeroSum.add(each + index));
        Assertions.assertEquals(0, zeroSum.getIntSum());

        Verify.assertThrows(IndexOutOfBoundsException.class, () -> Interval.zeroTo(10).forEachWithIndex(null, -1, 10));
    }

    @Test
    public void run()
    {
        IntegerSum sum = new IntegerSum(0);
        Interval.oneTo(5).run(() -> sum.add(1));
        Assertions.assertEquals(5, sum.getIntSum());
        IntegerSum sum2 = new IntegerSum(0);
        Interval.fromTo(5, 1).run(() -> sum2.add(1));
        Assertions.assertEquals(5, sum2.getIntSum());
    }

    @Test
    public void forEachWith()
    {
        IntegerSum sum = new IntegerSum(0);
        Interval.oneTo(5).forEachWith((Integer each, Integer parameter) -> sum.add(each + parameter), 0);
        Assertions.assertEquals(15, sum.getIntSum());
        IntegerSum sum2 = new IntegerSum(0);
        Interval.fromTo(5, 1).forEachWith((Integer each, Integer parameter) -> sum2.add(each + parameter), 0);
        Assertions.assertEquals(15, sum2.getIntSum());
    }

    @Test
    public void select()
    {
        Interval interval = Interval.fromTo(10, -10).by(-5);

        MutableList<Integer> expected = FastList.newListWith(10, 0, -10);
        Assertions.assertEquals(expected, interval.select(IntegerPredicates.isEven()).toList());
        Assertions.assertEquals(expected, interval.select(IntegerPredicates.isEven(), FastList.<Integer>newList()));
    }

    @Test
    public void reject()
    {
        Interval interval = Interval.fromTo(10, -10).by(-5);

        MutableList<Integer> expected = FastList.newListWith(5, -5);
        Assertions.assertEquals(expected, interval.reject(IntegerPredicates.isEven(), FastList.<Integer>newList()));
    }

    @Test
    public void collect()
    {
        Interval interval = Interval.fromTo(10, -10).by(-5);

        MutableList<String> expected = FastList.newListWith("10", "5", "0", "-5", "-10");
        Assertions.assertEquals(expected, interval.collect(String::valueOf).toList());
        Assertions.assertEquals(expected, interval.collect(String::valueOf, FastList.<String>newList()));
    }

    @Test
    public void getFirst()
    {
        Assertions.assertEquals(Integer.valueOf(10), Interval.fromTo(10, -10).by(-5).getFirst());
        Assertions.assertEquals(Integer.valueOf(-10), Interval.fromTo(-10, 10).by(5).getFirst());
        Assertions.assertEquals(Integer.valueOf(0), Interval.zero().getFirst());
    }

    @Test
    public void getLast()
    {
        Assertions.assertEquals(Integer.valueOf(-10), Interval.fromTo(10, -10).by(-5).getLast());
        Assertions.assertEquals(Integer.valueOf(-10), Interval.fromTo(10, -12).by(-5).getLast());
        Assertions.assertEquals(Integer.valueOf(10), Interval.fromTo(-10, 10).by(5).getLast());
        Assertions.assertEquals(Integer.valueOf(10), Interval.fromTo(-10, 12).by(5).getLast());
        Assertions.assertEquals(Integer.valueOf(0), Interval.zero().getLast());
    }

    @Test
    public void forEach_with_start_end()
    {
        Interval interval = Interval.fromTo(-10, 12).by(5);

        MutableList<Integer> forwardResult = Lists.mutable.of();
        interval.forEach(CollectionAddProcedure.on(forwardResult), 1, 3);
        Assertions.assertEquals(FastList.newListWith(-5, 0, 5), forwardResult);

        MutableList<Integer> backwardsResult = Lists.mutable.of();
        interval.forEach(CollectionAddProcedure.on(backwardsResult), 3, 1);
        Assertions.assertEquals(FastList.newListWith(5, 0, -5), backwardsResult);

        Verify.assertThrows(IndexOutOfBoundsException.class, () -> interval.forEach(null, -1, 3));
    }

    @Test
    public void forEachWith_with_start_end()
    {
        Interval interval = Interval.fromTo(-10, 12).by(5);

        MutableList<Integer> forwardResult = Lists.mutable.of();
        interval.forEachWithIndex(new AddParametersProcedure(forwardResult), 1, 3);
        Assertions.assertEquals(FastList.newListWith(-4, 2, 8), forwardResult);

        MutableList<Integer> backwardsResult = Lists.mutable.of();
        interval.forEachWithIndex(new AddParametersProcedure(backwardsResult), 3, 1);
        Assertions.assertEquals(FastList.newListWith(8, 2, -4), backwardsResult);
    }

    @Test
    public void indexOf()
    {
        Interval interval = Interval.fromTo(-10, 12).by(5);
        Assertions.assertEquals(0, interval.indexOf(-10));
        Assertions.assertEquals(1, interval.indexOf(-5));
        Assertions.assertEquals(2, interval.indexOf(0));
        Assertions.assertEquals(3, interval.indexOf(5));
        Assertions.assertEquals(4, interval.indexOf(10));

        Assertions.assertEquals(-1, interval.indexOf(-15));
        Assertions.assertEquals(-1, interval.indexOf(-11));
        Assertions.assertEquals(-1, interval.indexOf(-9));
        Assertions.assertEquals(-1, interval.indexOf(11));
        Assertions.assertEquals(-1, interval.indexOf(15));

        Interval backwardsInterval = Interval.fromTo(10, -12).by(-5);
        Assertions.assertEquals(0, backwardsInterval.indexOf(10));
        Assertions.assertEquals(1, backwardsInterval.indexOf(5));
        Assertions.assertEquals(2, backwardsInterval.indexOf(0));
        Assertions.assertEquals(3, backwardsInterval.indexOf(-5));
        Assertions.assertEquals(4, backwardsInterval.indexOf(-10));

        Assertions.assertEquals(-1, backwardsInterval.indexOf(15));
        Assertions.assertEquals(-1, backwardsInterval.indexOf(11));
        Assertions.assertEquals(-1, backwardsInterval.indexOf(9));
        Assertions.assertEquals(-1, backwardsInterval.indexOf(-11));
        Assertions.assertEquals(-1, backwardsInterval.indexOf(-15));
    }

    @Test
    public void lastIndexOf()
    {
        Interval interval = Interval.fromTo(-10, 12).by(5);
        Assertions.assertEquals(0, interval.lastIndexOf(-10));
        Assertions.assertEquals(1, interval.lastIndexOf(-5));
        Assertions.assertEquals(2, interval.lastIndexOf(0));
        Assertions.assertEquals(3, interval.lastIndexOf(5));
        Assertions.assertEquals(4, interval.lastIndexOf(10));

        Assertions.assertEquals(-1, interval.lastIndexOf(-15));
        Assertions.assertEquals(-1, interval.lastIndexOf(-11));
        Assertions.assertEquals(-1, interval.lastIndexOf(-9));
        Assertions.assertEquals(-1, interval.lastIndexOf(11));
        Assertions.assertEquals(-1, interval.lastIndexOf(15));
        Assertions.assertEquals(-1, interval.lastIndexOf(new Object()));

        Interval backwardsInterval = Interval.fromTo(10, -12).by(-5);
        Assertions.assertEquals(0, backwardsInterval.lastIndexOf(10));
        Assertions.assertEquals(1, backwardsInterval.lastIndexOf(5));
        Assertions.assertEquals(2, backwardsInterval.lastIndexOf(0));
        Assertions.assertEquals(3, backwardsInterval.lastIndexOf(-5));
        Assertions.assertEquals(4, backwardsInterval.lastIndexOf(-10));

        Assertions.assertEquals(-1, backwardsInterval.lastIndexOf(15));
        Assertions.assertEquals(-1, backwardsInterval.lastIndexOf(11));
        Assertions.assertEquals(-1, backwardsInterval.lastIndexOf(9));
        Assertions.assertEquals(-1, backwardsInterval.lastIndexOf(-11));
        Assertions.assertEquals(-1, backwardsInterval.lastIndexOf(-15));
    }

    @Test
    public void get()
    {
        Interval interval = Interval.fromTo(-10, 12).by(5);
        Assertions.assertEquals(Integer.valueOf(-10), interval.get(0));
        Assertions.assertEquals(Integer.valueOf(-5), interval.get(1));
        Assertions.assertEquals(Integer.valueOf(0), interval.get(2));
        Assertions.assertEquals(Integer.valueOf(5), interval.get(3));
        Assertions.assertEquals(Integer.valueOf(10), interval.get(4));

        Verify.assertThrows(IndexOutOfBoundsException.class, () -> interval.get(-1));
        Verify.assertThrows(IndexOutOfBoundsException.class, () -> interval.get(5));
    }

    @Test
    public void subList()
    {
        Interval interval = Interval.fromTo(1, 5);
        Assertions.assertEquals(FastList.newListWith(2, 3), interval.subList(1, 3));
    }

    @Test
    public void containsAll()
    {
        Assertions.assertTrue(Interval.fromTo(1, 3).containsAll(FastList.newListWith(1, 2, 3)));
        Assertions.assertFalse(Interval.fromTo(1, 3).containsAll(FastList.newListWith(1, 2, 4)));
    }

    @Test
    public void add()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            Interval.fromTo(1, 3).add(4);
        });
    }

    @Test
    public void add_at_index()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            Interval.fromTo(1, 3).add(0, 4);
        });
    }

    @Test
    public void remove()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            Interval.fromTo(1, 3).remove(Integer.valueOf(4));
        });
    }

    @Test
    public void remove_at_index()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            Interval.fromTo(1, 3).remove(0);
        });
    }

    @Test
    public void addAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            Interval.fromTo(1, 3).addAll(FastList.newListWith(4, 5, 6));
        });
    }

    @Test
    public void addAll_at_index()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            Interval.fromTo(1, 3).addAll(0, FastList.newListWith(4, 5, 6));
        });
    }

    @Test
    public void removeAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            Interval.fromTo(1, 3).removeAll(FastList.newListWith(4, 5, 6));
        });
    }

    @Test
    public void retainAll()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            Interval.fromTo(1, 3).retainAll(FastList.newListWith(4, 5, 6));
        });
    }

    @Test
    public void clear()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            Interval.fromTo(1, 3).clear();
        });
    }

    @Test
    public void set()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            Interval.fromTo(1, 3).set(0, 0);
        });
    }

    @Test
    public void take()
    {
        Assertions.assertEquals(FastList.newListWith(1, 2), Interval.fromTo(1, 3).take(2));
        Assertions.assertEquals(FastList.newListWith(1, 2), Interval.fromTo(1, 2).take(3));

        Verify.assertThrows(IllegalArgumentException.class, () -> Interval.fromTo(1, 3).take(-1));
    }

    @Test
    public void drop()
    {
        Assertions.assertEquals(FastList.newListWith(3, 4), Interval.fromTo(1, 4).drop(2));
        Verify.assertIterableEmpty(Interval.fromTo(1, 2).drop(3));

        Verify.assertThrows(IllegalArgumentException.class, () -> Interval.fromTo(1, 3).drop(-1));
    }

    @Test
    public void distinct()
    {
        LazyIterable<Integer> integers = Interval.oneTo(1000000000);

        Assertions.assertEquals(
                FastList.newListWith(1, 2, 3, 4, 5),
                integers.distinct().take(5).toList());

        LazyIterable<Integer> lazyInterval = Interval.oneTo(1000000).flatCollect(Interval::oneTo);
        LazyIterable<Integer> distinct = lazyInterval.distinct();
        LazyIterable<Integer> take = distinct.take(5);
        Assertions.assertEquals(Lists.immutable.of(1, 2, 3, 4, 5), take.toList());
    }

    private static final class AddParametersProcedure implements ObjectIntProcedure<Integer>
    {
        private final MutableList<Integer> forwardResult;

        private AddParametersProcedure(MutableList<Integer> forwardResult)
        {
            this.forwardResult = forwardResult;
        }

        @Override
        public void value(Integer each, int index)
        {
            this.forwardResult.add(each + index);
        }
    }

    @Test
    public void tap()
    {
        MutableList<Integer> tapResult = Lists.mutable.of();
        Interval interval = Interval.fromTo(10, -10).by(-5);
        LazyIterable<Integer> lazyTapIterable = interval.tap(tapResult::add);
        lazyTapIterable.each(x -> { }); //force evaluation
        Assertions.assertEquals(interval, tapResult);
    }
}
