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

import com.gs.collections.api.factory.stack.ImmutableStackFactory;
import com.gs.collections.api.stack.ImmutableStack;
import com.gs.collections.impl.stack.mutable.ArrayStack;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StacksTest
{
    @Test
    public void immutables()
    {
        ImmutableStackFactory stackFactory = Stacks.immutable;
        Assertions.assertEquals(ArrayStack.newStack(), stackFactory.of());
        Verify.assertInstanceOf(ImmutableStack.class, stackFactory.of());
        Assertions.assertEquals(ArrayStack.newStackWith(1), stackFactory.of(1));
        Verify.assertInstanceOf(ImmutableStack.class, stackFactory.of(1));
        Assertions.assertEquals(ArrayStack.newStackWith(1, 2), stackFactory.of(1, 2));
        Verify.assertInstanceOf(ImmutableStack.class, stackFactory.of(1, 2));
        Assertions.assertEquals(ArrayStack.newStackWith(1, 2, 3), stackFactory.of(1, 2, 3));
        Verify.assertInstanceOf(ImmutableStack.class, stackFactory.of(1, 2, 3));
        Assertions.assertEquals(ArrayStack.newStackWith(1, 2, 3, 4), stackFactory.of(1, 2, 3, 4));
        Verify.assertInstanceOf(ImmutableStack.class, stackFactory.of(1, 2, 3, 4));
        Assertions.assertEquals(ArrayStack.newStackWith(1, 2, 3, 4, 5), stackFactory.of(1, 2, 3, 4, 5));
        Verify.assertInstanceOf(ImmutableStack.class, stackFactory.of(1, 2, 3, 4, 5));
        Assertions.assertEquals(ArrayStack.newStackWith(1, 2, 3, 4, 5, 6), stackFactory.of(1, 2, 3, 4, 5, 6));
        Verify.assertInstanceOf(ImmutableStack.class, stackFactory.of(1, 2, 3, 4, 5, 6));
        Assertions.assertEquals(ArrayStack.newStackWith(1, 2, 3, 4, 5, 6, 7), stackFactory.of(1, 2, 3, 4, 5, 6, 7));
        Verify.assertInstanceOf(ImmutableStack.class, stackFactory.of(1, 2, 3, 4, 5, 6, 7));
        Assertions.assertEquals(ArrayStack.newStackWith(1, 2, 3, 4, 5, 6, 7, 8), stackFactory.of(1, 2, 3, 4, 5, 6, 7, 8));
        Verify.assertInstanceOf(ImmutableStack.class, stackFactory.of(1, 2, 3, 4, 5, 6, 7, 8));
        Assertions.assertEquals(ArrayStack.newStackWith(1, 2, 3, 4, 5, 6, 7, 8, 9), stackFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Verify.assertInstanceOf(ImmutableStack.class, stackFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Assertions.assertEquals(ArrayStack.newStackWith(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), stackFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Verify.assertInstanceOf(ImmutableStack.class, stackFactory.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Assertions.assertEquals(ArrayStack.newStackWith(3, 2, 1), stackFactory.ofAll(ArrayStack.newStackWith(1, 2, 3)));
        Verify.assertInstanceOf(ImmutableStack.class, stackFactory.ofAll(ArrayStack.newStackWith(1, 2, 3)));
    }

    @Test
    public void emptyStack()
    {
        Assertions.assertTrue(Stacks.immutable.of().isEmpty());
    }

    @Test
    public void newStackWith()
    {
        ImmutableStack<String> stack = Stacks.immutable.of();
        Assertions.assertEquals(stack, Stacks.immutable.of(stack.toArray()));
        Assertions.assertEquals(stack = stack.push("1"), Stacks.immutable.of("1"));
        Assertions.assertEquals(stack = stack.push("2"), Stacks.immutable.of("1", "2"));
        Assertions.assertEquals(stack = stack.push("3"), Stacks.immutable.of("1", "2", "3"));
        Assertions.assertEquals(stack = stack.push("4"), Stacks.immutable.of("1", "2", "3", "4"));
        Assertions.assertEquals(stack = stack.push("5"), Stacks.immutable.of("1", "2", "3", "4", "5"));
        Assertions.assertEquals(stack = stack.push("6"), Stacks.immutable.of("1", "2", "3", "4", "5", "6"));
        Assertions.assertEquals(stack = stack.push("7"), Stacks.immutable.of("1", "2", "3", "4", "5", "6", "7"));
        Assertions.assertEquals(stack = stack.push("8"), Stacks.immutable.of("1", "2", "3", "4", "5", "6", "7", "8"));
        Assertions.assertEquals(stack = stack.push("9"), Stacks.immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9"));
        Assertions.assertEquals(stack = stack.push("10"), Stacks.immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"));
        Assertions.assertEquals(stack = stack.push("11"), Stacks.immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"));
        Assertions.assertEquals(stack = stack.push("12"), Stacks.immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"));
    }

    @SuppressWarnings("RedundantArrayCreation")
    @Test
    public void newStackWithArray()
    {
        ImmutableStack<String> stack = Stacks.immutable.of();
        Assertions.assertEquals(stack = stack.push("1"), Stacks.immutable.of(new String[]{"1"}));
        Assertions.assertEquals(stack = stack.push("2"), Stacks.immutable.of(new String[]{"1", "2"}));
        Assertions.assertEquals(stack = stack.push("3"), Stacks.immutable.of(new String[]{"1", "2", "3"}));
        Assertions.assertEquals(stack = stack.push("4"), Stacks.immutable.of(new String[]{"1", "2", "3", "4"}));
        Assertions.assertEquals(stack = stack.push("5"), Stacks.immutable.of(new String[]{"1", "2", "3", "4", "5"}));
        Assertions.assertEquals(stack = stack.push("6"), Stacks.immutable.of(new String[]{"1", "2", "3", "4", "5", "6"}));
        Assertions.assertEquals(stack = stack.push("7"), Stacks.immutable.of(new String[]{"1", "2", "3", "4", "5", "6", "7"}));
        Assertions.assertEquals(stack = stack.push("8"), Stacks.immutable.of(new String[]{"1", "2", "3", "4", "5", "6", "7", "8"}));
        Assertions.assertEquals(stack = stack.push("9"), Stacks.immutable.of(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"}));
        Assertions.assertEquals(stack = stack.push("10"), Stacks.immutable.of(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"}));
        Assertions.assertEquals(stack = stack.push("11"), Stacks.immutable.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"));
    }

    @Test
    public void newStackWithStack()
    {
        ImmutableStack<String> stack = Stacks.immutable.of();
        ArrayStack<String> arrayStack = ArrayStack.newStackWith("1");
        Assertions.assertEquals(stack = stack.push("1"), arrayStack.toImmutable());
        arrayStack.push("2");
        Assertions.assertEquals(stack = stack.push("2"), arrayStack.toImmutable());
        arrayStack.push("3");
        Assertions.assertEquals(stack = stack.push("3"), arrayStack.toImmutable());
        arrayStack.push("4");
        Assertions.assertEquals(stack = stack.push("4"), arrayStack.toImmutable());
        arrayStack.push("5");
        Assertions.assertEquals(stack = stack.push("5"), arrayStack.toImmutable());
        arrayStack.push("6");
        Assertions.assertEquals(stack = stack.push("6"), arrayStack.toImmutable());
        arrayStack.push("7");
        Assertions.assertEquals(stack = stack.push("7"), arrayStack.toImmutable());
        arrayStack.push("8");
        Assertions.assertEquals(stack = stack.push("8"), arrayStack.toImmutable());
        arrayStack.push("9");
        Assertions.assertEquals(stack = stack.push("9"), arrayStack.toImmutable());
        arrayStack.push("10");
        Assertions.assertEquals(stack = stack.push("10"), arrayStack.toImmutable());
        arrayStack.push("11");
        Assertions.assertEquals(stack = stack.push("11"), arrayStack.toImmutable());
    }

    @Test
    public void newStackWithWithStack()
    {
        ArrayStack<Object> expected = ArrayStack.newStack();
        Assertions.assertEquals(expected, Stacks.mutable.ofAll(ArrayStack.newStack()));
        expected.push(1);
        Assertions.assertEquals(ArrayStack.newStackWith(1), Stacks.mutable.ofAll(expected));
        expected.push(2);
        Assertions.assertEquals(ArrayStack.newStackWith(2, 1), Stacks.mutable.ofAll(expected));
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(Stacks.class);
    }
}
