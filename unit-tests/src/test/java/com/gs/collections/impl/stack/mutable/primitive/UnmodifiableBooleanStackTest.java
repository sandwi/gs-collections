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

package com.gs.collections.impl.stack.mutable.primitive;

import com.gs.collections.api.iterator.MutableBooleanIterator;
import com.gs.collections.api.stack.primitive.MutableBooleanStack;
import com.gs.collections.impl.stack.primitive.AbstractBooleanStackTestCase;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JUnit test for {@link UnmodifiableBooleanStack}.
 */
public class UnmodifiableBooleanStackTest extends AbstractBooleanStackTestCase
{
    @Override
    protected UnmodifiableBooleanStack classUnderTest()
    {
        return new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(true, false, true, false));
    }

    @Override
    protected UnmodifiableBooleanStack newWith(boolean... elements)
    {
        return new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(elements));
    }

    @Override
    protected UnmodifiableBooleanStack newMutableCollectionWith(boolean... elements)
    {
        return new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(elements));
    }

    @Override
    protected UnmodifiableBooleanStack newWithTopToBottom(boolean... elements)
    {
        return new UnmodifiableBooleanStack(BooleanArrayStack.newStackFromTopToBottom(elements));
    }

    @Test
    public void push()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            MutableBooleanStack stack = new UnmodifiableBooleanStack(BooleanArrayStack.newStackFromTopToBottom(true, true, false, true, false));
            stack.push(true);
        });
    }

    @Test
    public void pop()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            MutableBooleanStack stack = new UnmodifiableBooleanStack(BooleanArrayStack.newStackFromTopToBottom(true, true, false, true, false));
            stack.pop();
        });
    }

    @Test
    public void popWithCount()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            MutableBooleanStack stack = new UnmodifiableBooleanStack(BooleanArrayStack.newStackFromTopToBottom(true, true, false, true, false));
            stack.pop(2);
        });
    }

    @Test
    public void clear()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            this.classUnderTest().clear();
        });
    }

    @Test
    public void asUnmodifiable()
    {
        MutableBooleanStack stack1 = new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(true, false, true));
        Assertions.assertSame(stack1, stack1.asUnmodifiable());
    }

    @Test
    public void asSynchronized()
    {
        MutableBooleanStack stack1 = new UnmodifiableBooleanStack(BooleanArrayStack.newStackWith(true, false, true));
        Verify.assertInstanceOf(SynchronizedBooleanStack.class, stack1.asSynchronized());
    }

    @Test
    public void booleanIterator_with_remove()
    {
        MutableBooleanIterator booleanIterator = (MutableBooleanIterator) this.classUnderTest().booleanIterator();
        Assertions.assertTrue(booleanIterator.hasNext());
        booleanIterator.next();
        Verify.assertThrows(UnsupportedOperationException.class, booleanIterator::remove);
    }

    @Test
    public void iterator_throws_on_invocation_of_remove_before_next()
    {
        MutableBooleanIterator booleanIterator = (MutableBooleanIterator) this.classUnderTest().booleanIterator();
        Assertions.assertTrue(booleanIterator.hasNext());
        Verify.assertThrows(UnsupportedOperationException.class, booleanIterator::remove);
    }
}
