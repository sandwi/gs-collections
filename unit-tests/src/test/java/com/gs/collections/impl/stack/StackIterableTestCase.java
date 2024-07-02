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

package com.gs.collections.impl.stack;

import java.util.Collections;
import java.util.EmptyStackException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.gs.collections.api.RichIterable;
import com.gs.collections.api.bag.sorted.SortedBag;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.block.predicate.Predicate2;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MapIterable;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.map.primitive.ObjectDoubleMap;
import com.gs.collections.api.map.primitive.ObjectLongMap;
import com.gs.collections.api.multimap.Multimap;
import com.gs.collections.api.multimap.MutableMultimap;
import com.gs.collections.api.multimap.list.ListMultimap;
import com.gs.collections.api.partition.stack.PartitionStack;
import com.gs.collections.api.set.SetIterable;
import com.gs.collections.api.set.sorted.MutableSortedSet;
import com.gs.collections.api.stack.StackIterable;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.bag.sorted.mutable.TreeBag;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.IntegerPredicates;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Predicates2;
import com.gs.collections.impl.block.factory.PrimitiveFunctions;
import com.gs.collections.impl.block.factory.Procedures;
import com.gs.collections.impl.block.function.AddFunction;
import com.gs.collections.impl.block.function.NegativeIntervalFunction;
import com.gs.collections.impl.block.function.PassThruFunction0;
import com.gs.collections.impl.factory.Bags;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.factory.Stacks;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.map.sorted.mutable.TreeSortedMap;
import com.gs.collections.impl.multimap.list.FastListMultimap;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.set.mutable.primitive.BooleanHashSet;
import com.gs.collections.impl.set.mutable.primitive.ByteHashSet;
import com.gs.collections.impl.set.mutable.primitive.CharHashSet;
import com.gs.collections.impl.set.mutable.primitive.DoubleHashSet;
import com.gs.collections.impl.set.mutable.primitive.FloatHashSet;
import com.gs.collections.impl.set.mutable.primitive.IntHashSet;
import com.gs.collections.impl.set.mutable.primitive.LongHashSet;
import com.gs.collections.impl.set.mutable.primitive.ShortHashSet;
import com.gs.collections.impl.set.sorted.mutable.TreeSortedSet;
import com.gs.collections.impl.stack.mutable.ArrayStack;
import com.gs.collections.impl.stack.mutable.primitive.BooleanArrayStack;
import com.gs.collections.impl.stack.mutable.primitive.ByteArrayStack;
import com.gs.collections.impl.stack.mutable.primitive.CharArrayStack;
import com.gs.collections.impl.stack.mutable.primitive.DoubleArrayStack;
import com.gs.collections.impl.stack.mutable.primitive.FloatArrayStack;
import com.gs.collections.impl.stack.mutable.primitive.IntArrayStack;
import com.gs.collections.impl.stack.mutable.primitive.LongArrayStack;
import com.gs.collections.impl.stack.mutable.primitive.ShortArrayStack;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class StackIterableTestCase
{
    protected abstract <T> StackIterable<T> newStackWith(T... elements);

    protected abstract <T> StackIterable<T> newStackFromTopToBottom(T... elements);

    protected abstract <T> StackIterable<T> newStackFromTopToBottom(Iterable<T> elements);

    protected abstract <T> StackIterable<T> newStack(Iterable<T> elements);

    @Test
    public void testNewStackFromTopToBottom()
    {
        Assertions.assertEquals(
                this.newStackWith(3, 2, 1),
                this.newStackFromTopToBottom(1, 2, 3));
    }

    @Test
    public void peek_empty_throws()
    {
        assertThrows(EmptyStackException.class, () -> {
            this.newStackWith().peek();
        });
    }

    @Test
    public void peek_int_empty_throws()
    {
        assertThrows(EmptyStackException.class, () -> {
            this.newStackWith().peek(1);
        });
    }

    @Test
    public void peek_int_count_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            this.newStackWith(1, 2, 3).peek(4);
        });
    }

    @Test
    public void peek_int_neg_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            this.newStackWith(1, 2, 3).peek(-1);
        });
    }

    @Test
    public void peek_illegal_arguments()
    {
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3);
        Verify.assertThrows(IllegalArgumentException.class, () -> stack.peek(-1));

        Verify.assertThrows(IllegalArgumentException.class, () -> stack.peek(4));

        Assertions.assertEquals(FastList.newListWith(1, 2, 3), stack.peek(3));
    }

    @Test
    public void peek()
    {
        Assertions.assertEquals("3", this.newStackWith("1", "2", "3").peek());
        Assertions.assertEquals(FastList.newListWith(), this.newStackWith("1", "2", "3").peek(0));
        Assertions.assertEquals(FastList.newListWith("3", "2"), this.newStackWith("1", "2", "3").peek(2));
    }

    @Test
    public void peekAt()
    {
        Assertions.assertEquals("3", this.newStackWith("1", "2", "3").peekAt(0));
        Assertions.assertEquals("2", this.newStackWith("1", "2", "3").peekAt(1));
        Assertions.assertEquals("1", this.newStackWith("1", "2", "3").peekAt(2));
    }

    @Test
    public void peekAt_illegal_arguments()
    {
        StackIterable<String> stack = this.newStackWith("1", "2", "3");
        Verify.assertThrows(IllegalArgumentException.class, () -> stack.peekAt(stack.size()));
    }

    @Test
    public void size()
    {
        StackIterable<Integer> stack1 = this.newStackWith();
        Assertions.assertEquals(0, stack1.size());

        StackIterable<Integer> stack2 = this.newStackWith(1, 2);
        Assertions.assertEquals(2, stack2.size());
    }

    @Test
    public void isEmpty()
    {
        StackIterable<Integer> stack = this.newStackWith();
        Assertions.assertTrue(stack.isEmpty());
        Assertions.assertFalse(stack.notEmpty());
    }

    @Test
    public void notEmpty()
    {
        StackIterable<Integer> stack = this.newStackWith(1);
        Assertions.assertTrue(stack.notEmpty());
        Assertions.assertFalse(stack.isEmpty());
    }

    @Test
    public void getFirst()
    {
        StackIterable<Integer> stack = this.newStackWith(1, 2, 3);
        Assertions.assertEquals(Integer.valueOf(3), stack.getFirst());
        Assertions.assertEquals(stack.peek(), stack.getFirst());
        Verify.assertThrows(EmptyStackException.class, () -> this.newStackWith().getFirst());
        StackIterable<Integer> stack2 = this.newStackFromTopToBottom(1, 2, 3);
        Assertions.assertEquals(Integer.valueOf(1), stack2.getFirst());
    }

    @Test
    public void getLast()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            StackIterable<Integer> stack = this.newStackWith(1, 2, 3);
            Assertions.assertEquals(Integer.valueOf(1), stack.getLast());
        });
    }

    @Test
    public void contains()
    {
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3);
        Assertions.assertTrue(stack.contains(2));
        Assertions.assertTrue(stack.contains(3));
        Assertions.assertFalse(stack.contains(4));
    }

    @Test
    public void containsAllIterable()
    {
        StackIterable<Integer> stack = this.newStackWith(1, 2, 3);
        Assertions.assertTrue(stack.containsAllIterable(Interval.fromTo(2, 3)));
        Assertions.assertFalse(stack.containsAllIterable(Interval.fromTo(2, 4)));
    }

    @Test
    public void containsAll()
    {
        StackIterable<Integer> stack = this.newStackWith(1, 2, 3, 4);
        Assertions.assertTrue(stack.containsAll(Interval.oneTo(2)));
        Assertions.assertFalse(stack.containsAll(FastList.newListWith(1, 2, 5)));
    }

    @Test
    public void containsAllArguments()
    {
        StackIterable<Integer> stack = this.newStackWith(1, 2, 3, 4);
        Assertions.assertTrue(stack.containsAllArguments(2, 1, 3));
        Assertions.assertFalse(stack.containsAllArguments(2, 1, 3, 5));
    }

    @Test
    public void collect()
    {
        StackIterable<Boolean> stack = this.newStackFromTopToBottom(Boolean.TRUE, Boolean.FALSE, null);
        CountingFunction<Object, String> function = CountingFunction.of(String::valueOf);
        Assertions.assertEquals(
                this.newStackFromTopToBottom("true", "false", "null"),
                stack.collect(function));
        Assertions.assertEquals(3, function.count);

        Assertions.assertEquals(FastList.newListWith("true", "false", "null"), stack.collect(String::valueOf, FastList.<String>newList()));
    }

    @Test
    public void collectBoolean()
    {
        StackIterable<String> stack = this.newStackFromTopToBottom("true", "nah", "TrUe", "false");
        Assertions.assertEquals(
                BooleanArrayStack.newStackFromTopToBottom(true, false, true, false),
                stack.collectBoolean(Boolean::parseBoolean));
    }

    @Test
    public void collectBooleanWithTarget()
    {
        BooleanHashSet target = new BooleanHashSet();
        StackIterable<String> stack = this.newStackFromTopToBottom("true", "nah", "TrUe", "false");
        BooleanHashSet result = stack.collectBoolean(Boolean::parseBoolean, target);
        Assertions.assertEquals(BooleanHashSet.newSetWith(true, false, true, false), result);
        Assertions.assertSame(target, result, "Target sent as parameter not returned");
    }

    @Test
    public void collectByte()
    {
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3);
        Assertions.assertEquals(ByteArrayStack.newStackFromTopToBottom((byte) 1, (byte) 2, (byte) 3), stack.collectByte(PrimitiveFunctions.unboxIntegerToByte()));
    }

    @Test
    public void collectByteWithTarget()
    {
        ByteHashSet target = new ByteHashSet();
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3);
        ByteHashSet result = stack.collectByte(PrimitiveFunctions.unboxIntegerToByte(), target);
        Assertions.assertEquals(ByteHashSet.newSetWith((byte) 1, (byte) 2, (byte) 3), result);
        Assertions.assertSame(target, result, "Target sent as parameter not returned");
    }

    @Test
    public void collectChar()
    {
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3);
        Assertions.assertEquals(CharArrayStack.newStackFromTopToBottom((char) 1, (char) 2, (char) 3), stack.collectChar(PrimitiveFunctions.unboxIntegerToChar()));
    }

    @Test
    public void collectCharWithTarget()
    {
        CharHashSet target = new CharHashSet();
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3);
        CharHashSet result = stack.collectChar(PrimitiveFunctions.unboxIntegerToChar(), target);
        Assertions.assertEquals(CharHashSet.newSetWith((char) 1, (char) 2, (char) 3), result);
        Assertions.assertSame(target, result, "Target sent as parameter not returned");
    }

    @Test
    public void collectDouble()
    {
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3);
        Assertions.assertEquals(DoubleArrayStack.newStackFromTopToBottom(1, 2, 3), stack.collectDouble(PrimitiveFunctions.unboxIntegerToDouble()));
    }

    @Test
    public void collectDoubleWithTarget()
    {
        DoubleHashSet target = new DoubleHashSet();
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3);
        DoubleHashSet result = stack.collectDouble(PrimitiveFunctions.unboxIntegerToDouble(), target);
        Assertions.assertEquals(DoubleHashSet.newSetWith(1, 2, 3), result);
        Assertions.assertSame(target, result, "Target sent as parameter not returned");
    }

    @Test
    public void collectFloat()
    {
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3);
        Assertions.assertEquals(FloatArrayStack.newStackFromTopToBottom(1, 2, 3), stack.collectFloat(PrimitiveFunctions.unboxIntegerToFloat()));
    }

    @Test
    public void collectFloatWithTarget()
    {
        FloatHashSet target = new FloatHashSet();
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3);
        FloatHashSet result = stack.collectFloat(PrimitiveFunctions.unboxIntegerToFloat(), target);
        Assertions.assertEquals(FloatHashSet.newSetWith(1, 2, 3), result);
        Assertions.assertSame(target, result, "Target sent as parameter not returned");
    }

    @Test
    public void collectInt()
    {
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3);
        Assertions.assertEquals(IntArrayStack.newStackFromTopToBottom(1, 2, 3), stack.collectInt(PrimitiveFunctions.unboxIntegerToInt()));
    }

    @Test
    public void collectIntWithTarget()
    {
        IntHashSet target = new IntHashSet();
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3);
        IntHashSet result = stack.collectInt(PrimitiveFunctions.unboxIntegerToInt(), target);
        Assertions.assertEquals(IntHashSet.newSetWith(1, 2, 3), result);
        Assertions.assertSame(target, result, "Target sent as parameter not returned");
    }

    @Test
    public void collectLong()
    {
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3);
        Assertions.assertEquals(LongArrayStack.newStackFromTopToBottom(1, 2, 3), stack.collectLong(PrimitiveFunctions.unboxIntegerToLong()));
    }

    @Test
    public void collectLongWithTarget()
    {
        LongHashSet target = new LongHashSet();
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3);
        LongHashSet result = stack.collectLong(PrimitiveFunctions.unboxIntegerToLong(), target);
        Assertions.assertEquals(LongHashSet.newSetWith(1, 2, 3), result);
        Assertions.assertSame(target, result, "Target sent as parameter not returned");
    }

    @Test
    public void collectShort()
    {
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3);
        Assertions.assertEquals(ShortArrayStack.newStackFromTopToBottom((short) 1, (short) 2, (short) 3), stack.collectShort(PrimitiveFunctions.unboxIntegerToShort()));
    }

    @Test
    public void collectShortWithTarget()
    {
        ShortHashSet target = new ShortHashSet();
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3);
        ShortHashSet result = stack.collectShort(PrimitiveFunctions.unboxIntegerToShort(), target);
        Assertions.assertEquals(ShortHashSet.newSetWith((short) 1, (short) 2, (short) 3), result);
        Assertions.assertSame(target, result, "Target sent as parameter not returned");
    }

    @Test
    public void collectIf()
    {
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3, 4, 5);

        CountingPredicate<Integer> predicate1 = CountingPredicate.of(Predicates.lessThan(3));
        CountingFunction<Object, String> function1 = CountingFunction.of(String::valueOf);
        Assertions.assertEquals(
                this.newStackFromTopToBottom("1", "2"),
                stack.collectIf(predicate1, function1));
        Assertions.assertEquals(5, predicate1.count);
        Assertions.assertEquals(2, function1.count);

        CountingPredicate<Integer> predicate2 = CountingPredicate.of(Predicates.lessThan(3));
        CountingFunction<Object, String> function2 = CountingFunction.of(String::valueOf);
        Assertions.assertEquals(
                FastList.newListWith("1", "2"),
                stack.collectIf(predicate2, function2, FastList.<String>newList()));
        Assertions.assertEquals(5, predicate2.count);
        Assertions.assertEquals(2, function2.count);
    }

    @Test
    public void collectWith()
    {
        StackIterable<Integer> stack = this.newStackFromTopToBottom(3, 2, 1);
        Assertions.assertEquals(
                ArrayStack.newStackFromTopToBottom(4, 3, 2),
                stack.collectWith(AddFunction.INTEGER, 1));
    }

    @Test
    public void collectWithTarget()
    {
        StackIterable<Integer> stack = this.newStackFromTopToBottom(3, 2, 1);
        Assertions.assertEquals(
                FastList.newListWith(4, 3, 2),
                stack.collectWith(AddFunction.INTEGER, 1, FastList.<Integer>newList()));
    }

    @Test
    public void flatCollect()
    {
        StackIterable<String> stack = this.newStackFromTopToBottom("1", "One", "2", "Two");

        CountingFunction<String, Iterable<Character>> function = CountingFunction.of(object -> {
            MutableList<Character> result = Lists.mutable.of();
            char[] chars = object.toCharArray();
            for (char aChar : chars)
            {
                result.add(Character.valueOf(aChar));
            }
            return result;
        });

        Assertions.assertEquals(
                this.newStackFromTopToBottom('1', 'O', 'n', 'e', '2', 'T', 'w', 'o'),
                stack.flatCollect(function));
        Assertions.assertEquals(4, function.count);

        Assertions.assertEquals(
                FastList.newListWith('1', 'O', 'n', 'e', '2', 'T', 'w', 'o'),
                stack.flatCollect(function, FastList.<Character>newList()));
    }

    @Test
    public void select()
    {
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3);
        CountingPredicate<Object> predicate = new CountingPredicate<>(Integer.valueOf(1)::equals);
        StackIterable<Integer> actual = stack.select(predicate);
        Assertions.assertEquals(this.newStackFromTopToBottom(1), actual);
        Assertions.assertEquals(3, predicate.count);
        Assertions.assertEquals(
                this.newStackFromTopToBottom(2, 3),
                stack.select(Predicates.greaterThan(1)));
        Assertions.assertEquals(
                FastList.newListWith(2, 3),
                stack.select(Predicates.greaterThan(1), FastList.<Integer>newList()));
    }

    @Test
    public void selectInstancesOf()
    {
        StackIterable<Number> numbers = this.<Number>newStackFromTopToBottom(1, 2.0, 3, 4.0, 5);
        Assertions.assertEquals(this.newStackFromTopToBottom(1, 3, 5), numbers.selectInstancesOf(Integer.class));
        Assertions.assertEquals(this.<Number>newStackFromTopToBottom(1, 2.0, 3, 4.0, 5), numbers.selectInstancesOf(Number.class));
    }

    @Test
    public void selectWith()
    {
        Assertions.assertEquals(
                ArrayStack.newStackFromTopToBottom(2, 1),
                this.newStackFromTopToBottom(5, 4, 3, 2, 1).selectWith(Predicates2.<Integer>lessThan(), 3));
    }

    @Test
    public void selectWithTarget()
    {
        Assertions.assertEquals(
                UnifiedSet.newSetWith(2, 1),
                this.newStackFromTopToBottom(5, 4, 3, 2, 1).selectWith(Predicates2.<Integer>lessThan(), 3, UnifiedSet.<Integer>newSet()));
    }

    @Test
    public void reject()
    {
        StackIterable<Integer> stack = this.newStackFromTopToBottom(3, 2, 1);
        CountingPredicate<Integer> predicate = new CountingPredicate<>(Predicates.greaterThan(2));
        Assertions.assertEquals(
                this.newStackFromTopToBottom(2, 1),
                stack.reject(predicate));
        Assertions.assertEquals(3, predicate.count);
        Assertions.assertEquals(
                FastList.newListWith(2, 1),
                stack.reject(Predicates.greaterThan(2), FastList.<Integer>newList()));
    }

    @Test
    public void rejectWith()
    {
        StackIterable<Integer> stack = this.newStackFromTopToBottom(3, 2, 1);
        Assertions.assertEquals(
                this.newStackFromTopToBottom(2, 1),
                stack.rejectWith(Predicates2.<Integer>greaterThan(), 2));
    }

    @Test
    public void rejectWithTarget()
    {
        Assertions.assertEquals(
                UnifiedSet.newSetWith(5, 4, 3),
                this.newStackFromTopToBottom(5, 4, 3, 2, 1).rejectWith(Predicates2.<Integer>lessThan(), 3, UnifiedSet.<Integer>newSet()));
    }

    @Test
    public void detect()
    {
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3);
        CountingPredicate<Integer> predicate = new CountingPredicate<>(Predicates.lessThan(3));
        Assertions.assertEquals(Integer.valueOf(1), stack.detect(predicate));
        Assertions.assertEquals(1, predicate.count);
        Assertions.assertNull(stack.detect(Integer.valueOf(4)::equals));
    }

    @Test
    public void detectWith()
    {
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3);
        CountingPredicate2<Integer, Integer> predicate = new CountingPredicate2<>(Predicates2.<Integer>lessThan());
        Assertions.assertEquals(Integer.valueOf(1), stack.detectWith(predicate, 3));
        Assertions.assertEquals(1, predicate.count);
        Assertions.assertNull(stack.detectWith(Object::equals, Integer.valueOf(4)));
    }

    @Test
    public void detectIfNone()
    {
        Function0<Integer> defaultResultFunction = new PassThruFunction0<>(-1);
        CountingPredicate<Integer> predicate = new CountingPredicate<>(Predicates.lessThan(3));
        Assertions.assertEquals(
                Integer.valueOf(1),
                this.newStackFromTopToBottom(1, 2, 3, 4, 5).detectIfNone(predicate, defaultResultFunction));
        Assertions.assertEquals(1, predicate.count);
        Assertions.assertEquals(
                Integer.valueOf(-1),
                this.newStackWith(1, 2, 3, 4, 5).detectIfNone(Predicates.lessThan(-1), defaultResultFunction));
    }

    @Test
    public void detectWithIfNone()
    {
        Function0<Integer> defaultResultFunction = new PassThruFunction0<>(-1);
        CountingPredicate2<Integer, Integer> predicate = new CountingPredicate2<>(Predicates2.<Integer>lessThan());
        Assertions.assertEquals(
                Integer.valueOf(1),
                this.newStackFromTopToBottom(1, 2, 3, 4, 5).detectWithIfNone(predicate, Integer.valueOf(3), defaultResultFunction));
        Assertions.assertEquals(1, predicate.count);
        Assertions.assertEquals(
                Integer.valueOf(-1),
                this.newStackWith(1, 2, 3, 4, 5).detectIfNone(Predicates.lessThan(-1), defaultResultFunction));
    }

    @Test
    public void partition()
    {
        CountingPredicate<Integer> predicate = new CountingPredicate<>(Predicates.lessThan(3));
        PartitionStack<Integer> partition = this.newStackFromTopToBottom(1, 2, 3, 4, 5).partition(predicate);
        Assertions.assertEquals(5, predicate.count);
        Assertions.assertEquals(this.newStackFromTopToBottom(1, 2), partition.getSelected());
        Assertions.assertEquals(this.newStackFromTopToBottom(3, 4, 5), partition.getRejected());
    }

    @Test
    public void partitionWith()
    {
        PartitionStack<Integer> partition = this.newStackFromTopToBottom(1, 2, 3, 4, 5).partitionWith(Predicates2.<Integer>lessThan(), 3);
        Assertions.assertEquals(this.newStackFromTopToBottom(1, 2), partition.getSelected());
        Assertions.assertEquals(this.newStackFromTopToBottom(3, 4, 5), partition.getRejected());
    }

    @Test
    public void zip()
    {
        StackIterable<String> stack = this.newStackFromTopToBottom("7", "6", "5", "4", "3", "2", "1");
        List<Integer> interval = Interval.oneTo(7);

        StackIterable<Pair<String, Integer>> expected = this.newStackFromTopToBottom(
                Tuples.pair("7", 1),
                Tuples.pair("6", 2),
                Tuples.pair("5", 3),
                Tuples.pair("4", 4),
                Tuples.pair("3", 5),
                Tuples.pair("2", 6),
                Tuples.pair("1", 7));

        Assertions.assertEquals(expected, stack.zip(interval));

        Assertions.assertEquals(
                expected.toSet(),
                stack.zip(interval, UnifiedSet.<Pair<String, Integer>>newSet()));
    }

    @Test
    public void zipWithIndex()
    {
        StackIterable<String> stack = this.newStackFromTopToBottom("4", "3", "2", "1");

        StackIterable<Pair<String, Integer>> expected = this.newStackFromTopToBottom(
                Tuples.pair("4", 0),
                Tuples.pair("3", 1),
                Tuples.pair("2", 2),
                Tuples.pair("1", 3));
        Assertions.assertEquals(expected, stack.zipWithIndex());
        Assertions.assertEquals(expected.toSet(), stack.zipWithIndex(UnifiedSet.<Pair<String, Integer>>newSet()));
    }

    @Test
    public void count()
    {
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3, 4, 5);
        CountingPredicate<Integer> predicate = new CountingPredicate<>(Predicates.greaterThan(2));
        Assertions.assertEquals(3, stack.count(predicate));
        Assertions.assertEquals(5, predicate.count);
        Assertions.assertEquals(0, stack.count(Predicates.greaterThan(6)));
    }

    @Test
    public void countWith()
    {
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3, 4, 5);
        CountingPredicate2<Object, Object> predicate = new CountingPredicate2<>(Object::equals);
        Assertions.assertEquals(1, stack.countWith(predicate, 1));
        Assertions.assertEquals(5, predicate.count);
        Assertions.assertNotEquals(2, stack.countWith(predicate, 4));
    }

    @Test
    public void anySatisfy()
    {
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3);
        CountingPredicate<Object> predicate = new CountingPredicate<>(Integer.valueOf(1)::equals);
        Assertions.assertTrue(stack.anySatisfy(predicate));
        Assertions.assertEquals(1, predicate.count);
        Assertions.assertFalse(stack.anySatisfy(Integer.valueOf(4)::equals));
    }

    @Test
    public void allSatisfy()
    {
        StackIterable<Integer> stack = this.newStackWith(3, 3, 3);
        CountingPredicate<Object> predicate = new CountingPredicate<>(Integer.valueOf(3)::equals);
        Assertions.assertTrue(stack.allSatisfy(predicate));
        Assertions.assertEquals(3, predicate.count);
        Assertions.assertFalse(stack.allSatisfy(Integer.valueOf(2)::equals));
    }

    @Test
    public void noneSatisfy()
    {
        StackIterable<Integer> stack = this.newStackWith(3, 3, 3);
        CountingPredicate<Object> predicate = new CountingPredicate<>(Integer.valueOf(4)::equals);
        Assertions.assertTrue(stack.noneSatisfy(predicate));
        Assertions.assertEquals(3, predicate.count);
        Assertions.assertTrue(stack.noneSatisfy(Integer.valueOf(2)::equals));
    }

    @Test
    public void anySatisfyWith()
    {
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3);
        CountingPredicate2<Object, Object> predicate = new CountingPredicate2<>(Object::equals);
        Assertions.assertTrue(stack.anySatisfyWith(predicate, 1));
        Assertions.assertEquals(1, predicate.count);
        Assertions.assertFalse(stack.anySatisfyWith(Object::equals, 4));
    }

    @Test
    public void allSatisfyWith()
    {
        StackIterable<Integer> stack = this.newStackWith(3, 3, 3);
        CountingPredicate2<Object, Object> predicate = new CountingPredicate2<>(Object::equals);
        Assertions.assertTrue(stack.allSatisfyWith(predicate, 3));
        Assertions.assertEquals(3, predicate.count);
        Assertions.assertFalse(stack.allSatisfyWith(Object::equals, 2));
    }

    @Test
    public void noneSatisfyWith()
    {
        StackIterable<Integer> stack = this.newStackWith(3, 3, 3);
        CountingPredicate2<Object, Object> predicate = new CountingPredicate2<>(Object::equals);
        Assertions.assertTrue(stack.noneSatisfyWith(predicate, 4));
        Assertions.assertEquals(3, predicate.count);
        Assertions.assertTrue(stack.noneSatisfyWith(Object::equals, 2));
    }

    @Test
    public void injectInto()
    {
        Assertions.assertEquals(
                Integer.valueOf(10),
                this.newStackWith(1, 2, 3, 4).injectInto(Integer.valueOf(0), AddFunction.INTEGER));
        Assertions.assertEquals(
                10,
                this.newStackWith(1, 2, 3, 4).injectInto(0, AddFunction.INTEGER_TO_INT));
        Assertions.assertEquals(
                7.0,
                this.newStackWith(1.0, 2.0, 3.0).injectInto(1.0d, AddFunction.DOUBLE_TO_DOUBLE), 0.001);
        Assertions.assertEquals(
                7,
                this.newStackWith(1, 2, 3).injectInto(1L, AddFunction.INTEGER_TO_LONG));
        Assertions.assertEquals(
                7.0,
                this.newStackWith(1, 2, 3).injectInto(1.0f, AddFunction.INTEGER_TO_FLOAT), 0.001);
    }

    @Test
    public void sumOf()
    {
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3, 4);
        Assertions.assertEquals(10, stack.sumOfInt(integer -> integer));
        Assertions.assertEquals(10, stack.sumOfLong(Integer::longValue));
        Assertions.assertEquals(10.0d, stack.sumOfDouble(Integer::doubleValue), 0.001);
        Assertions.assertEquals(10.0f, stack.sumOfFloat(Integer::floatValue), 0.001);
    }

    @Test
    public void sumOfFloatConsistentRounding()
    {
        MutableList<Integer> list = Interval.oneTo(100_000).toList().shuffleThis();
        StackIterable<Integer> stack = this.newStackWith(list.toArray(new Integer[]{}));

        // The test only ensures the consistency/stability of rounding. This is not meant to test the "correctness" of the float calculation result.
        // Indeed the lower bits of this calculation result are always incorrect due to the information loss of original float values.
        Assertions.assertEquals(
                1.082323233761663,
                stack.sumOfFloat(i -> 1.0f / (i.floatValue() * i.floatValue() * i.floatValue() * i.floatValue())),
                1.0e-15);
    }

    @Test
    public void sumOfDoubleConsistentRounding()
    {
        MutableList<Integer> list = Interval.oneTo(100_000).toList().shuffleThis();
        StackIterable<Integer> stack = this.newStackWith(list.toArray(new Integer[]{}));

        Assertions.assertEquals(
                1.082323233711138,
                stack.sumOfDouble(i -> 1.0d / (i.doubleValue() * i.doubleValue() * i.doubleValue() * i.doubleValue())),
                1.0e-15);
    }

    @Test
    public void sumByInt()
    {
        RichIterable<Integer> values = this.newStackFromTopToBottom(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        ObjectLongMap<Integer> result = values.sumByInt(i -> i % 2, e -> e);
        Assertions.assertEquals(25, result.get(1));
        Assertions.assertEquals(30, result.get(0));
    }

    @Test
    public void sumByFloat()
    {
        RichIterable<Integer> values = this.newStackFromTopToBottom(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        ObjectDoubleMap<Integer> result = values.sumByFloat(f -> f % 2, e -> e);
        Assertions.assertEquals(25.0f, result.get(1), 0.0);
        Assertions.assertEquals(30.0f, result.get(0), 0.0);
    }

    @Test
    public void sumByFloatConsistentRounding()
    {
        MutableList<Integer> group1 = Interval.oneTo(100_000).toList().shuffleThis();
        MutableList<Integer> group2 = Interval.fromTo(100_001, 200_000).toList().shuffleThis();
        MutableList<Integer> integers = Lists.mutable.withAll(group1);
        integers.addAll(group2);
        StackIterable<Integer> values = this.newStackWith(integers.toArray(new Integer[]{}));
        ObjectDoubleMap<Integer> result = values.sumByFloat(
                integer -> integer > 100_000 ? 2 : 1,
                integer -> {
                    Integer i = integer > 100_000 ? integer - 100_000 : integer;
                    return 1.0f / (i.floatValue() * i.floatValue() * i.floatValue() * i.floatValue());
                });

        // The test only ensures the consistency/stability of rounding. This is not meant to test the "correctness" of the float calculation result.
        // Indeed the lower bits of this calculation result are always incorrect due to the information loss of original float values.
        Assertions.assertEquals(
                1.082323233761663,
                result.get(1),
                1.0e-15);

        Assertions.assertEquals(
                1.082323233761663,
                result.get(2),
                1.0e-15);
    }

    @Test
    public void sumByLong()
    {
        RichIterable<Integer> values = this.newStackFromTopToBottom(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        ObjectLongMap<Integer> result = values.sumByLong(l -> l % 2, e -> e);
        Assertions.assertEquals(25, result.get(1));
        Assertions.assertEquals(30, result.get(0));
    }

    @Test
    public void sumByDouble()
    {
        RichIterable<Integer> values = this.newStackFromTopToBottom(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        ObjectDoubleMap<Integer> result = values.sumByDouble(d -> d % 2, e -> e);
        Assertions.assertEquals(25.0d, result.get(1), 0.0);
        Assertions.assertEquals(30.0d, result.get(0), 0.0);
    }

    @Test
    public void sumByDoubleConsistentRounding()
    {
        MutableList<Integer> group1 = Interval.oneTo(100_000).toList().shuffleThis();
        MutableList<Integer> group2 = Interval.fromTo(100_001, 200_000).toList().shuffleThis();
        MutableList<Integer> integers = Lists.mutable.withAll(group1);
        integers.addAll(group2);
        StackIterable<Integer> values = this.newStackWith(integers.toArray(new Integer[]{}));
        ObjectDoubleMap<Integer> result = values.sumByDouble(
                integer -> integer > 100_000 ? 2 : 1,
                integer -> {
                    Integer i = integer > 100_000 ? integer - 100_000 : integer;
                    return 1.0d / (i.doubleValue() * i.doubleValue() * i.doubleValue() * i.doubleValue());
                });

        Assertions.assertEquals(
                1.082323233711138,
                result.get(1),
                1.0e-15);

        Assertions.assertEquals(
                1.082323233711138,
                result.get(2),
                1.0e-15);
    }

    @Test
    public void max()
    {
        Assertions.assertEquals(
                Integer.valueOf(4),
                this.newStackFromTopToBottom(4, 3, 2, 1).max());
        Assertions.assertEquals(
                Integer.valueOf(1),
                this.newStackFromTopToBottom(4, 3, 2, 1).max(Comparators.<Integer>reverseNaturalOrder()));
    }

    @Test
    public void maxBy()
    {
        Assertions.assertEquals(
                Integer.valueOf(3),
                this.newStackWith(1, 2, 3).maxBy(String::valueOf));
    }

    @Test
    public void min()
    {
        Assertions.assertEquals(
                Integer.valueOf(1),
                this.newStackWith(1, 2, 3, 4).min());
        Assertions.assertEquals(
                Integer.valueOf(4),
                this.newStackWith(1, 2, 3, 4).min(Comparators.<Integer>reverseNaturalOrder()));
    }

    @Test
    public void minBy()
    {
        CountingFunction<Object, String> function = CountingFunction.of(String::valueOf);
        Assertions.assertEquals(
                Integer.valueOf(1),
                this.newStackWith(1, 2, 3).minBy(function));
        Assertions.assertEquals(3, function.count);
    }

    @Test
    public void testToString()
    {
        StackIterable<Integer> stack = this.newStackFromTopToBottom(4, 3, 2, 1);
        Assertions.assertEquals("[4, 3, 2, 1]", stack.toString());
    }

    @Test
    public void makeString()
    {
        Assertions.assertEquals("3, 2, 1", this.newStackFromTopToBottom(3, 2, 1).makeString());
        Assertions.assertEquals("3~2~1", this.newStackFromTopToBottom(3, 2, 1).makeString("~"));
        Assertions.assertEquals("[3/2/1]", this.newStackFromTopToBottom(3, 2, 1).makeString("[", "/", "]"));
    }

    @Test
    public void appendString()
    {
        StackIterable<String> stack = this.newStackFromTopToBottom("3", "2", "1");
        Appendable appendable = new StringBuilder();

        stack.appendString(appendable);
        Assertions.assertEquals("3, 2, 1", appendable.toString());

        Appendable appendable2 = new StringBuilder();
        stack.appendString(appendable2, "/");
        Assertions.assertEquals("3/2/1", appendable2.toString());

        Appendable appendable3 = new StringBuilder();
        stack.appendString(appendable3, "[", "/", "]");
        Assertions.assertEquals("[3/2/1]", appendable3.toString());
    }

    @Test
    public void groupBy()
    {
        StackIterable<String> stack = this.newStackWith("1", "2", "3");
        ListMultimap<Boolean, String> expected = FastListMultimap.newMultimap(
                Tuples.pair(Boolean.TRUE, "3"),
                Tuples.pair(Boolean.FALSE, "2"),
                Tuples.pair(Boolean.TRUE, "1"));
        Assertions.assertEquals(expected, stack.groupBy(object -> IntegerPredicates.isOdd().accept(Integer.parseInt(object))));
        Assertions.assertEquals(expected, stack.groupBy(object -> IntegerPredicates.isOdd().accept(Integer.parseInt(object)), FastListMultimap.<Boolean, String>newMultimap()));
    }

    @Test
    public void groupByEach()
    {
        StackIterable<Integer> stack = this.newStackFromTopToBottom(1, 2, 3);

        MutableMultimap<Integer, Integer> expected = FastListMultimap.newMultimap();
        stack.forEach(Procedures.cast(value -> expected.putAll(-value, Interval.fromTo(value, stack.size()))));

        Multimap<Integer, Integer> actual =
                stack.groupByEach(new NegativeIntervalFunction());
        Assertions.assertEquals(expected, actual);

        Multimap<Integer, Integer> actualWithTarget =
                stack.groupByEach(new NegativeIntervalFunction(), FastListMultimap.<Integer, Integer>newMultimap());
        Assertions.assertEquals(expected, actualWithTarget);
    }

    @Test
    public void groupByUniqueKey()
    {
        Assertions.assertEquals(UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3), this.newStackWith(1, 2, 3).groupByUniqueKey(id -> id));
    }

    @Test
    public void groupByUniqueKey_throws()
    {
        assertThrows(IllegalStateException.class, () -> {
            this.newStackWith(1, 2, 3).groupByUniqueKey(Functions.getFixedValue(1));
        });
    }

    @Test
    public void groupByUniqueKey_target()
    {
        MutableMap<Integer, Integer> integers = this.newStackWith(1, 2, 3).groupByUniqueKey(id -> id, UnifiedMap.newWithKeysValues(0, 0));
        Assertions.assertEquals(UnifiedMap.newWithKeysValues(0, 0, 1, 1, 2, 2, 3, 3), integers);
    }

    @Test
    public void groupByUniqueKey_target_throws()
    {
        assertThrows(IllegalStateException.class, () -> {
            this.newStackWith(1, 2, 3).groupByUniqueKey(id -> id, UnifiedMap.newWithKeysValues(2, 2));
        });
    }

    @Test
    public void chunk()
    {
        Verify.assertIterablesEqual(
                FastList.<RichIterable<String>>newListWith(
                        FastList.newListWith("7", "6"),
                        FastList.newListWith("5", "4"),
                        FastList.newListWith("3", "2"),
                        FastList.newListWith("1")),
                this.newStackFromTopToBottom("7", "6", "5", "4", "3", "2", "1").chunk(2));
    }

    @Test
    public void tap()
    {
        MutableList<String> tapResult = Lists.mutable.of();
        StackIterable<String> stack = this.newStackWith("1", "2", "3", "4", "5");
        Assertions.assertSame(stack, stack.tap(tapResult::add));
        Assertions.assertEquals(stack.toList(), tapResult);
    }

    @Test
    public void forEach()
    {
        StackIterable<String> stack = this.newStackWith("1", "2", "3", "4", "5");
        Appendable builder = new StringBuilder();
        Procedure<String> appendProcedure = Procedures.append(builder);
        stack.forEach(appendProcedure);
        Assertions.assertEquals("54321", builder.toString());
    }

    @Test
    public void forEachWith()
    {
        StackIterable<String> stack = this.newStackWith("1", "2", "3", "4");
        StringBuilder builder = new StringBuilder();
        stack.forEachWith((argument1, argument2) -> builder.append(argument1).append(argument2), 0);
        Assertions.assertEquals("40302010", builder.toString());
    }

    @Test
    public void forEachWithIndex()
    {
        StackIterable<String> stack = this.newStackFromTopToBottom("5", "4", "3", "2", "1");
        StringBuilder builder = new StringBuilder();
        stack.forEachWithIndex((each, index) -> builder.append(each).append(index));
        Assertions.assertEquals("5041322314", builder.toString());
    }

    @Test
    public void toList()
    {
        Assertions.assertEquals(
                FastList.newListWith(4, 3, 2, 1),
                this.newStackFromTopToBottom(4, 3, 2, 1).toList());
    }

    @Test
    public void toStack()
    {
        Assertions.assertEquals(this.newStackFromTopToBottom(3, 2, 1), this.newStackFromTopToBottom(3, 2, 1).toStack());
    }

    @Test
    public void toSortedList()
    {
        Assertions.assertEquals(
                Interval.oneTo(4),
                this.newStackFromTopToBottom(4, 3, 1, 2).toSortedList());
        Assertions.assertEquals(
                Interval.fromTo(4, 1),
                this.newStackFromTopToBottom(4, 3, 1, 2).toSortedList(Collections.<Integer>reverseOrder()));
    }

    @Test
    public void toSortedListBy()
    {
        MutableList<Integer> list = FastList.newList(Interval.oneTo(10)).shuffleThis();
        Assertions.assertEquals(
                Interval.oneTo(10),
                this.newStack(list).toSortedListBy(Functions.getIntegerPassThru()));
    }

    @Test
    public void toSet()
    {
        Assertions.assertEquals(UnifiedSet.newSetWith(4, 3, 2, 1),
                this.newStackWith(1, 2, 3, 4).toSet());
    }

    @Test
    public void toSortedSet()
    {
        MutableSortedSet<Integer> expected = TreeSortedSet.newSetWith(1, 2, 4, 5);
        StackIterable<Integer> stack = this.newStackWith(2, 1, 5, 4);

        Assertions.assertEquals(expected, stack.toSortedSet());
        Assertions.assertEquals(FastList.newListWith(1, 2, 4, 5), stack.toSortedSet().toList());

        MutableSortedSet<Integer> reversed = stack.toSortedSet(Comparators.reverseNaturalOrder());
        Verify.assertSortedSetsEqual(reversed, stack.toSortedSet(Comparators.reverseNaturalOrder()));
        Assertions.assertEquals(
                FastList.newListWith(5, 4, 2, 1),
                stack.toSortedSet(Comparators.reverseNaturalOrder()).toList());
    }

    @Test
    public void toSortedSetBy()
    {
        SetIterable<Integer> expected = UnifiedSet.newSetWith(10, 9, 8, 7, 6, 5, 4, 3, 2, 1);

        StackIterable<Integer> stack = this.newStackWith(5, 2, 4, 3, 1, 6, 7, 8, 9, 10);
        Assertions.assertEquals(
                expected,
                stack.toSortedSetBy(String::valueOf));
        Assertions.assertEquals(
                FastList.newListWith(1, 10, 2, 3, 4, 5, 6, 7, 8, 9),
                stack.toSortedSetBy(String::valueOf).toList());
    }

    @Test
    public void toBag()
    {
        Assertions.assertEquals(Bags.mutable.of("C", "B", "A"),
                this.newStackFromTopToBottom("C", "B", "A").toBag());
    }

    @Test
    public void toSortedBag()
    {
        SortedBag<Integer> expected = TreeBag.newBagWith(1, 2, 2, 4, 5);
        StackIterable<Integer> stack = this.newStackWith(2, 2, 1, 5, 4);

        Verify.assertSortedBagsEqual(expected, stack.toSortedBag());
        Assertions.assertEquals(FastList.newListWith(1, 2, 2, 4, 5), stack.toSortedBag().toList());

        SortedBag<Integer> expected2 = TreeBag.newBagWith(Comparators.reverseNaturalOrder(), 1, 2, 2, 4, 5);
        Verify.assertSortedBagsEqual(expected2, stack.toSortedBag(Comparators.reverseNaturalOrder()));
        Assertions.assertEquals(
                FastList.newListWith(5, 4, 2, 2, 1),
                stack.toSortedBag(Comparators.reverseNaturalOrder()).toList());
    }

    @Test
    public void toSortedBagBy()
    {
        SortedBag<Integer> expected = TreeBag.newBagWith(1, 2, 3, 3, 4, 5);

        StackIterable<Integer> stack = this.newStackWith(1, 2, 3, 3, 4, 5);
        Verify.assertSortedBagsEqual(
                expected,
                stack.toSortedBagBy(String::valueOf));
    }

    @Test
    public void toMap()
    {
        Assertions.assertEquals(UnifiedMap.newWithKeysValues("4", "4", "3", "3", "2", "2", "1", "1"),
                this.newStackFromTopToBottom(4, 3, 2, 1).toMap(String::valueOf, String::valueOf));
    }

    @Test
    public void toSortedMap()
    {
        Assertions.assertEquals(UnifiedMap.newWithKeysValues(3, "3", 2, "2", 1, "1"),
                this.newStackFromTopToBottom(3, 2, 1).toSortedMap(Functions.getIntegerPassThru(), String::valueOf));

        Assertions.assertEquals(TreeSortedMap.newMapWith(Comparators.<Integer>reverseNaturalOrder(), 3, "3", 2, "2", 1, "1"),
                this.newStackFromTopToBottom(3, 2, 1).toSortedMap(Comparators.<Integer>reverseNaturalOrder(),
                        Functions.getIntegerPassThru(), String::valueOf));
    }

    @Test
    public void asLazy()
    {
        Assertions.assertEquals(FastList.newListWith("3", "2", "1"),
                this.newStackFromTopToBottom("3", "2", "1").asLazy().toList());
    }

    @Test
    public void toArray()
    {
        Assertions.assertArrayEquals(new Object[]{4, 3, 2, 1}, this.newStackFromTopToBottom(4, 3, 2, 1).toArray());
        Assertions.assertArrayEquals(new Integer[]{4, 3, 2, 1}, this.newStackFromTopToBottom(4, 3, 2, 1).toArray(new Integer[0]));
    }

    @Test
    public void iterator()
    {
        StringBuilder builder = new StringBuilder();
        StackIterable<String> stack = this.newStackFromTopToBottom("5", "4", "3", "2", "1");
        for (String string : stack)
        {
            builder.append(string);
        }
        Assertions.assertEquals("54321", builder.toString());
    }

    @Test
    public void testEquals()
    {
        StackIterable<Integer> stack1 = this.newStackFromTopToBottom(1, 2, 3, 4);
        StackIterable<Integer> stack2 = this.newStackFromTopToBottom(1, 2, 3, 4);
        StackIterable<Integer> stack3 = this.newStackFromTopToBottom(5, 2, 1, 4);
        StackIterable<Integer> stack4 = this.newStackFromTopToBottom(1, 2, 3);
        StackIterable<Integer> stack5 = this.newStackFromTopToBottom(1, 2, 3, 4, 5);
        StackIterable<Integer> stack6 = this.newStackFromTopToBottom(1, 2, 3, null);

        Verify.assertEqualsAndHashCode(stack1, stack2);
        Verify.assertPostSerializedEqualsAndHashCode(this.newStackWith(1, 2, 3, 4));
        Assertions.assertNotEquals(stack1, stack3);
        Assertions.assertNotEquals(stack1, stack4);
        Assertions.assertNotEquals(stack1, stack5);
        Assertions.assertNotEquals(stack1, stack6);

        Verify.assertPostSerializedEqualsAndHashCode(this.newStackWith(null, null, null));

        Assertions.assertEquals(Stacks.mutable.of(), this.newStackWith());
    }

    @Test
    public void testHashCode()
    {
        StackIterable<Integer> stack1 = this.newStackWith(1, 2, 3, 5);
        StackIterable<Integer> stack2 = this.newStackWith(1, 2, 3, 4);
        Assertions.assertNotEquals(stack1.hashCode(), stack2.hashCode());

        Assertions.assertEquals(
                31 * 31 * 31 * 31 + 1 * 31 * 31 * 31 + 2 * 31 * 31 + 3 * 31 + 4,
                this.newStackFromTopToBottom(1, 2, 3, 4).hashCode());
        Assertions.assertEquals(31 * 31 * 31, this.newStackFromTopToBottom(null, null, null).hashCode());

        Assertions.assertNotEquals(this.newStackFromTopToBottom(1, 2, 3, 4).hashCode(), this.newStackFromTopToBottom(4, 3, 2, 1).hashCode());
    }

    @Test
    public void aggregateByMutating()
    {
        Function0<AtomicInteger> valueCreator = AtomicInteger::new;
        StackIterable<Integer> collection = this.newStackWith(1, 1, 1, 2, 2, 3);
        MapIterable<String, AtomicInteger> aggregation = collection.aggregateInPlaceBy(String::valueOf, valueCreator, AtomicInteger::addAndGet);
        Assertions.assertEquals(3, aggregation.get("1").intValue());
        Assertions.assertEquals(4, aggregation.get("2").intValue());
        Assertions.assertEquals(3, aggregation.get("3").intValue());
    }

    @Test
    public void aggregateByNonMutating()
    {
        Function0<Integer> valueCreator = () -> 0;
        Function2<Integer, Integer, Integer> sumAggregator = (integer1, integer2) -> integer1 + integer2;
        StackIterable<Integer> collection = this.newStackWith(1, 1, 1, 2, 2, 3);
        MapIterable<String, Integer> aggregation = collection.aggregateBy(String::valueOf, valueCreator, sumAggregator);
        Assertions.assertEquals(3, aggregation.get("1").intValue());
        Assertions.assertEquals(4, aggregation.get("2").intValue());
        Assertions.assertEquals(3, aggregation.get("3").intValue());
    }

    private static final class CountingPredicate<T>
            implements Predicate<T>
    {
        private static final long serialVersionUID = 1L;
        private final Predicate<T> predicate;
        private int count;

        private CountingPredicate(Predicate<T> predicate)
        {
            this.predicate = predicate;
        }

        private static <T> CountingPredicate<T> of(Predicate<T> predicate)
        {
            return new CountingPredicate<>(predicate);
        }

        @Override
        public boolean accept(T anObject)
        {
            this.count++;
            return this.predicate.accept(anObject);
        }
    }

    private static final class CountingPredicate2<T1, T2>
            implements Predicate2<T1, T2>
    {
        private static final long serialVersionUID = 1L;
        private final Predicate2<T1, T2> predicate;
        private int count;

        private CountingPredicate2(Predicate2<T1, T2> predicate)
        {
            this.predicate = predicate;
        }

        private static <T1, T2> CountingPredicate2<T1, T2> of(Predicate2<T1, T2> predicate)
        {
            return new CountingPredicate2<>(predicate);
        }

        @Override
        public boolean accept(T1 each, T2 parameter)
        {
            this.count++;
            return this.predicate.accept(each, parameter);
        }
    }

    private static final class CountingFunction<T, V>
            implements Function<T, V>
    {
        private static final long serialVersionUID = 1L;
        private int count;
        private final Function<T, V> function;

        private CountingFunction(Function<T, V> function)
        {
            this.function = function;
        }

        private static <T, V> CountingFunction<T, V> of(Function<T, V> function)
        {
            return new CountingFunction<>(function);
        }

        @Override
        public V valueOf(T object)
        {
            this.count++;
            return this.function.valueOf(object);
        }
    }
}
