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

package com.gs.collections.impl.stack.immutable.primitive;

import com.gs.collections.api.BooleanIterable;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.stack.primitive.ImmutableBooleanStack;
import com.gs.collections.api.stack.primitive.MutableBooleanStack;
import com.gs.collections.impl.factory.primitive.BooleanStacks;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.stack.mutable.ArrayStack;
import com.gs.collections.impl.stack.mutable.primitive.BooleanArrayStack;
import com.gs.collections.impl.stack.primitive.AbstractBooleanStackTestCase;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Abstract JUnit test for {@link ImmutableBooleanStack}.
 */
public abstract class AbstractImmutableBooleanStackTestCase extends AbstractBooleanStackTestCase
{
    @Override
    protected abstract ImmutableBooleanStack classUnderTest();

    @Override
    protected ImmutableBooleanStack newWith(boolean... elements)
    {
        return BooleanStacks.immutable.of(elements);
    }

    @Override
    protected MutableBooleanStack newMutableCollectionWith(boolean... elements)
    {
        return BooleanArrayStack.newStackWith(elements);
    }

    @Override
    protected RichIterable<Object> newObjectCollectionWith(Object... elements)
    {
        return ArrayStack.newStackWith(elements);
    }

    @Override
    protected ImmutableBooleanStack newWithTopToBottom(boolean... elements)
    {
        return ImmutableBooleanArrayStack.newStackFromTopToBottom(elements);
    }

    protected ImmutableBooleanStack newWithIterableTopToBottom(BooleanIterable iterable)
    {
        return ImmutableBooleanArrayStack.newStackFromTopToBottom(iterable);
    }

    protected ImmutableBooleanStack newWithIterable(BooleanIterable iterable)
    {
        return ImmutableBooleanArrayStack.newStack(iterable);
    }

    @Test
    public void push()
    {
        ImmutableBooleanStack stack = this.classUnderTest();
        int size = stack.size();
        ImmutableBooleanStack modified = stack.push(true);
        Assertions.assertTrue(modified.peek());
        Verify.assertSize(size + 1, modified);
        Verify.assertSize(size, stack);
        Assertions.assertNotSame(modified, stack);
        Assertions.assertEquals(this.classUnderTest(), stack);
    }

    @Test
    public void pop()
    {
        ImmutableBooleanStack stack = this.classUnderTest();
        int size = stack.size();
        ImmutableBooleanStack modified = stack.pop();
        Assertions.assertEquals((this.classUnderTest().size() & 1) == 0, modified.peek());
        Verify.assertSize(size - 1, modified);
        Verify.assertSize(size, stack);
        Assertions.assertNotSame(modified, stack);
        Assertions.assertEquals(this.classUnderTest(), stack);
    }

    @Test
    public void popWithCount()
    {
        ImmutableBooleanStack stack = this.classUnderTest();
        ImmutableBooleanStack stack1 = stack.pop(0);
        Assertions.assertSame(stack1, stack);
        Assertions.assertEquals(this.classUnderTest(), stack);
        int size = stack.size();
        ImmutableBooleanStack modified = stack.pop(2);
        Assertions.assertEquals((this.classUnderTest().size() & 1) != 0, modified.peek());
        Verify.assertSize(size - 2, modified);
        Verify.assertSize(size, stack);
        Assertions.assertNotSame(modified, stack);
        Assertions.assertEquals(this.classUnderTest(), stack);
    }

    @Test
    public void pop_with_negative_count_throws_exception()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            this.classUnderTest().pop(-1);
        });
    }

    @Test
    public void pop_with_count_greater_than_stack_size_throws_exception()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            this.classUnderTest().pop(this.classUnderTest().size() + 1);
        });
    }

    @Override
    @Test
    public void testToString()
    {
        Assertions.assertEquals(this.createExpectedString("[", ", ", "]"), this.classUnderTest().toString());
    }

    @Override
    @Test
    public void makeString()
    {
        Assertions.assertEquals(this.createExpectedString("", ", ", ""), this.classUnderTest().makeString());
        Assertions.assertEquals(this.createExpectedString("", "|", ""), this.classUnderTest().makeString("|"));
        Assertions.assertEquals(this.createExpectedString("{", "|", "}"), this.classUnderTest().makeString("{", "|", "}"));
    }

    @Override
    @Test
    public void appendString()
    {
        StringBuilder appendable1 = new StringBuilder();
        this.classUnderTest().appendString(appendable1);
        Assertions.assertEquals(this.createExpectedString("", ", ", ""), appendable1.toString());

        StringBuilder appendable2 = new StringBuilder();
        this.classUnderTest().appendString(appendable2, "|");
        Assertions.assertEquals(this.createExpectedString("", "|", ""), appendable2.toString());

        StringBuilder appendable3 = new StringBuilder();
        this.classUnderTest().appendString(appendable3, "{", "|", "}");
        Assertions.assertEquals(this.createExpectedString("{", "|", "}"), appendable3.toString());
    }

    @Override
    @Test
    public void toList()
    {
        BooleanArrayList expected = BooleanArrayList.newListWith();
        this.classUnderTest().forEach(expected::add);
        Assertions.assertEquals(expected, this.classUnderTest().toList());
    }

    @Override
    @Test
    public void toImmutable()
    {
        super.toImmutable();
        ImmutableBooleanStack expected = this.classUnderTest();
        Assertions.assertSame(expected, expected.toImmutable());
    }
}
