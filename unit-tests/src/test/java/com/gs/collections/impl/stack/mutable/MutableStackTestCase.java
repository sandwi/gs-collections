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

package com.gs.collections.impl.stack.mutable;

import java.util.EmptyStackException;

import com.gs.collections.api.stack.MutableStack;
import com.gs.collections.impl.factory.Stacks;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.stack.StackIterableTestCase;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class MutableStackTestCase extends StackIterableTestCase
{
    @Override
    protected abstract <T> MutableStack<T> newStackWith(T... elements);

    @Override
    protected abstract <T> MutableStack<T> newStackFromTopToBottom(T... elements);

    @Override
    protected abstract <T> MutableStack<T> newStackFromTopToBottom(Iterable<T> elements);

    @Override
    protected abstract <T> MutableStack<T> newStack(Iterable<T> elements);

    @Test
    public void testPushPopAndPeek()
    {
        MutableStack<String> stack = this.newStackWith();
        stack.push("1");
        Assertions.assertEquals("1", stack.peek());
        Assertions.assertEquals(this.newStackWith("1"), stack);

        stack.push("2");
        Assertions.assertEquals("2", stack.peek());
        Assertions.assertEquals(this.newStackWith("1", "2"), stack);

        stack.push("3");
        Assertions.assertEquals("3", stack.peek());
        Assertions.assertEquals(this.newStackWith("1", "2", "3"), stack);

        Assertions.assertEquals("2", stack.peekAt(1));
        Assertions.assertEquals("3", stack.pop());
        Assertions.assertEquals("2", stack.peek());
        Assertions.assertEquals("2", stack.pop());
        Assertions.assertEquals("1", stack.peek());
        Assertions.assertEquals("1", stack.pop());

        MutableStack<Integer> stack2 = this.newStackFromTopToBottom(5, 4, 3, 2, 1);
        stack2.pop(2);
        Assertions.assertEquals(this.newStackFromTopToBottom(3, 2, 1), stack2);
        Assertions.assertEquals(FastList.newListWith(3, 2), stack2.peek(2));

        MutableStack<Integer> stack3 = Stacks.mutable.ofReversed(1, 2, 3);
        Assertions.assertEquals(this.newStackFromTopToBottom(1, 2, 3), stack3);

        MutableStack<Integer> stack4 = Stacks.mutable.ofAll(FastList.newListWith(1, 2, 3));
        MutableStack<Integer> stack5 = Stacks.mutable.ofAllReversed(FastList.newListWith(1, 2, 3));

        Assertions.assertEquals(this.newStackFromTopToBottom(3, 2, 1), stack4);
        Assertions.assertEquals(this.newStackFromTopToBottom(1, 2, 3), stack5);

        MutableStack<Integer> stack6 = this.newStackFromTopToBottom(1, 2, 3, 4);
        Assertions.assertEquals(FastList.newListWith(1, 2), stack6.pop(2, FastList.<Integer>newList()));

        MutableStack<Integer> stack7 = this.newStackFromTopToBottom(1, 2, 3, 4);
        Assertions.assertEquals(ArrayStack.newStackFromTopToBottom(2, 1), stack7.pop(2, ArrayStack.<Integer>newStack()));

        MutableStack<Integer> stack8 = this.newStackFromTopToBottom(1, 2, 3, 4);
        Verify.assertIterableEmpty(stack8.pop(0));
        Assertions.assertEquals(ArrayStack.newStackFromTopToBottom(1, 2, 3, 4), stack8);
        Assertions.assertEquals(FastList.newList(), stack8.peek(0));

        MutableStack<Integer> stack9 = ArrayStack.newStack();
        Assertions.assertEquals(FastList.newList(), stack9.pop(0));
        Assertions.assertEquals(FastList.newList(), stack9.peek(0));
        Assertions.assertEquals(FastList.newList(), stack9.pop(0, FastList.<Integer>newList()));
        Assertions.assertEquals(ArrayStack.newStack(), stack9.pop(0, ArrayStack.<Integer>newStack()));
    }

    @Test
    public void clear()
    {
        MutableStack<Integer> stack = this.newStackFromTopToBottom(1, 2, 3);
        stack.clear();
        Assertions.assertEquals(ArrayStack.newStack(), stack);
        Verify.assertIterableEmpty(stack);
    }

    @Test
    public void testNewStackWithOrder()
    {
        MutableStack<String> stack = this.newStackWith("1", "2", "3");
        Assertions.assertEquals("3", stack.pop());
        Assertions.assertEquals("2", stack.pop());
        Assertions.assertEquals("1", stack.pop());
    }

    @Test
    public void testNewStackIterableOrder()
    {
        MutableStack<String> stack = this.newStack(FastList.newListWith("1", "2", "3"));
        Assertions.assertEquals("3", stack.pop());
        Assertions.assertEquals("2", stack.pop());
        Assertions.assertEquals("1", stack.pop());
    }

    @Test
    public void testNewStackFromTopToBottomOrder()
    {
        MutableStack<String> stack = this.newStackFromTopToBottom("3", "2", "1");
        Assertions.assertEquals("3", stack.pop());
        Assertions.assertEquals("2", stack.pop());
        Assertions.assertEquals("1", stack.pop());
    }

    @Test
    public void testNewStackFromTopToBottomIterableOrder()
    {
        MutableStack<String> stack = this.newStackFromTopToBottom(FastList.newListWith("3", "2", "1"));
        Assertions.assertEquals("3", stack.pop());
        Assertions.assertEquals("2", stack.pop());
        Assertions.assertEquals("1", stack.pop());
    }

    @Test
    public void pop_empty_throws()
    {
        assertThrows(EmptyStackException.class, () -> {
            this.newStackWith().pop();
        });
    }

    @Test
    public void pop_int_empty_throws()
    {
        assertThrows(EmptyStackException.class, () -> {
            this.newStackWith().pop(1);
        });
    }

    @Test
    public void pop_int_count_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            this.newStackWith(1, 2, 3).pop(4);
        });
    }

    @Test
    public void pop_int_neg_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            this.newStackWith(1, 2, 3).pop(-1);
        });
    }

    @Test
    public void pop_target_empty_throws()
    {
        assertThrows(EmptyStackException.class, () -> {
            this.newStackWith().pop(5, FastList.newList());
        });
    }

    @Test
    public void pop_target_count_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            this.newStackWith(1, 2, 3).pop(5, FastList.<Integer>newList());
        });
    }

    @Test
    public void pop_target_neg_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            this.newStackWith(1, 2, 3).pop(-1, FastList.<Integer>newList());
        });
    }

    @Test
    public void pop_targetStack_empty_throws()
    {
        assertThrows(EmptyStackException.class, () -> {
            this.newStackWith().pop(5, ArrayStack.newStack());
        });
    }

    @Test
    public void pop_targetStack_count_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            this.newStackWith(1, 2, 3).pop(5, ArrayStack.<Integer>newStack());
        });
    }

    @Test
    public void pop_targetStack_neg_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            this.newStackWith(1, 2, 3).pop(-1, ArrayStack.<Integer>newStack());
        });
    }

    @Test
    public void asUnmodifiable()
    {
        Verify.assertInstanceOf(UnmodifiableStack.class, this.newStackWith().asUnmodifiable());
    }

    @Test
    public void asSynchronized()
    {
        Verify.assertInstanceOf(SynchronizedStack.class, this.newStackWith().asSynchronized());
    }
}
