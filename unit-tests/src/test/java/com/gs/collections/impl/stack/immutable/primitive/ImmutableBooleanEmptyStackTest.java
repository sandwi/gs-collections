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

import java.util.EmptyStackException;

import com.gs.collections.api.iterator.BooleanIterator;
import com.gs.collections.api.stack.primitive.ImmutableBooleanStack;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JUnit test for {@link ImmutableBooleanEmptyStack}.
 */
public class ImmutableBooleanEmptyStackTest extends AbstractImmutableBooleanStackTestCase
{
    @Override
    protected ImmutableBooleanStack classUnderTest()
    {
        return ImmutableBooleanEmptyStack.INSTANCE;
    }

    @Override
    @Test
    public void pop()
    {
        assertThrows(EmptyStackException.class, () -> {
            this.classUnderTest().pop();
        });
    }

    @Override
    @Test
    public void pop_with_count_greater_than_stack_size_throws_exception()
    {
        assertThrows(EmptyStackException.class, () -> {
            this.classUnderTest().pop(1);
        });
    }

    @Override
    @Test
    public void popWithCount()
    {
        ImmutableBooleanStack stack = this.classUnderTest();
        ImmutableBooleanStack stack1 = stack.pop(0);
        Assertions.assertSame(stack1, stack);
        Assertions.assertEquals(this.classUnderTest(), stack);
    }

    @Override
    @Test
    public void booleanIterator()
    {
        BooleanIterator iterator = this.classUnderTest().booleanIterator();
        Assertions.assertFalse(iterator.hasNext());
    }

    @Override
    @Test
    public void peek()
    {
        assertThrows(EmptyStackException.class, () -> {
            this.classUnderTest().peek();
        });
    }

    @Test
    public void peekWithCount()
    {
        Assertions.assertEquals(BooleanArrayList.newListWith(), this.classUnderTest().peek(0));
        Verify.assertThrows(EmptyStackException.class, () -> this.classUnderTest().peek(1));
    }

    @Override
    @Test
    public void peek_at_index_equal_to_size_throws_exception()
    {
        assertThrows(EmptyStackException.class, () -> {
            this.classUnderTest().peekAt(0);
        });
    }

    @Override
    @Test
    public void peek_at_index_greater_than_size_throws_exception()
    {
        assertThrows(EmptyStackException.class, () -> {
            this.classUnderTest().peekAt(1);
        });
    }

    @Override
    @Test
    public void notEmpty()
    {
        Assertions.assertFalse(this.newWith().notEmpty());
    }

    @Override
    @Test
    public void isEmpty()
    {
        Verify.assertEmpty(this.newWith());
    }

    @Override
    @Test
    public void testEquals()
    {
        super.testEquals();
        Verify.assertPostSerializedIdentity(this.classUnderTest());
    }
}
