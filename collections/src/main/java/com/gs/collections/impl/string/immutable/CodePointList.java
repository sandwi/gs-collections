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

package com.gs.collections.impl.string.immutable;

import java.io.IOException;
import java.io.Serializable;

import com.gs.collections.api.IntIterable;
import com.gs.collections.api.LazyIntIterable;
import com.gs.collections.api.bag.primitive.MutableIntBag;
import com.gs.collections.api.block.function.primitive.IntToIntFunction;
import com.gs.collections.api.block.function.primitive.IntToObjectFunction;
import com.gs.collections.api.block.function.primitive.ObjectIntIntToObjectFunction;
import com.gs.collections.api.block.function.primitive.ObjectIntToObjectFunction;
import com.gs.collections.api.block.predicate.primitive.IntPredicate;
import com.gs.collections.api.block.procedure.primitive.IntIntProcedure;
import com.gs.collections.api.block.procedure.primitive.IntProcedure;
import com.gs.collections.api.iterator.IntIterator;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.primitive.ImmutableIntList;
import com.gs.collections.api.list.primitive.IntList;
import com.gs.collections.api.list.primitive.MutableIntList;
import com.gs.collections.api.set.primitive.MutableIntSet;
import com.gs.collections.impl.factory.primitive.IntLists;
import com.gs.collections.impl.list.mutable.primitive.IntArrayList;
import com.gs.collections.impl.primitive.AbstractIntIterable;

/**
 * Calculates and provides the code points stored in a String as an ImmutableIntList.  This is a cleaner more OO way of
 * providing many of the iterable protocols available in StringIterate for code points.
 *
 * @since 7.0
 */
public class CodePointList extends AbstractIntIterable implements CharSequence, ImmutableIntList, Serializable
{
    private static final long serialVersionUID = 2L;

    private final ImmutableIntList codePoints;

    public CodePointList(String value)
    {
        int stringSize = value.length();
        IntArrayList list = new IntArrayList(stringSize);
        for (int i = 0; i < stringSize; )
        {
            int codePoint = value.codePointAt(i);
            i += Character.charCount(codePoint);
            list.add(codePoint);
        }
        this.codePoints = list.toImmutable();
    }

    private CodePointList(ImmutableIntList points)
    {
        this.codePoints = points;
    }

    private CodePointList(int... codePoints)
    {
        this.codePoints = IntLists.immutable.with(codePoints);
    }

    public static CodePointList from(String value)
    {
        return new CodePointList(value);
    }

    public static CodePointList from(int... codePoints)
    {
        return new CodePointList(codePoints);
    }

    public static CodePointList from(IntIterable iterable)
    {
        if (iterable instanceof ImmutableIntList list)
        {
            return new CodePointList(list);
        }
        return new CodePointList(iterable.toArray());
    }

    public StringBuilder toStringBuilder()
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.size(); i++)
        {
            builder.appendCodePoint(this.get(i));
        }
        return builder;
    }

    public char charAt(int index)
    {
        int currentIndex = 0;
        for (int i = 0; i < this.codePoints.size(); i++)
        {
            int codePoint = this.codePoints.get(i);
            int charCount = Character.charCount(codePoint);
            if (index < currentIndex + charCount)
            {
                if (charCount == 1)
                {
                    return (char) codePoint;
                }
                if (index == currentIndex)
                {
                    return Character.highSurrogate(codePoint);
                }
                return Character.lowSurrogate(codePoint);
            }
            currentIndex += charCount;
        }
        throw new IndexOutOfBoundsException("Char value at " + index + " is out of bounds for length " + currentIndex);
    }

    public int length()
    {
        int length = 0;
        for (int i = 0; i < this.codePoints.size(); i++)
        {
            length += Character.charCount(this.codePoints.get(i));
        }
        return length;
    }

    public String subSequence(int start, int end)
    {
        StringBuilder builder = this.toStringBuilder();
        return builder.substring(start, end);
    }

    /**
     * The value of toString must be strictly implemented as defined in CharSequence.
     */
    @Override
    public String toString()
    {
        return this.toStringBuilder().toString();
    }

    public IntIterator intIterator()
    {
        return this.codePoints.intIterator();
    }

    public int[] toArray()
    {
        return this.codePoints.toArray();
    }

    public boolean contains(int expected)
    {
        return this.codePoints.contains(expected);
    }

    public void forEach(IntProcedure procedure)
    {
        this.each(procedure);
    }

    public void each(IntProcedure procedure)
    {
        this.codePoints.each(procedure);
    }

    public CodePointList distinct()
    {
        return new CodePointList(this.codePoints.distinct());
    }

    public CodePointList newWith(int element)
    {
        return new CodePointList(this.codePoints.newWith(element));
    }

    public CodePointList newWithout(int element)
    {
        return new CodePointList(this.codePoints.newWithout(element));
    }

    public CodePointList newWithAll(IntIterable elements)
    {
        return new CodePointList(this.codePoints.newWithAll(elements));
    }

    public CodePointList newWithoutAll(IntIterable elements)
    {
        return new CodePointList(this.codePoints.newWithoutAll(elements));
    }

    public CodePointList toReversed()
    {
        return new CodePointList(this.codePoints.toReversed());
    }

    public ImmutableIntList subList(int fromIndex, int toIndex)
    {
        return this.codePoints.subList(fromIndex, toIndex);
    }

    public int get(int index)
    {
        return this.codePoints.get(index);
    }

    public long dotProduct(IntList list)
    {
        return this.codePoints.dotProduct(list);
    }

    public int binarySearch(int value)
    {
        return this.codePoints.binarySearch(value);
    }

    public int lastIndexOf(int value)
    {
        return this.codePoints.lastIndexOf(value);
    }

    public ImmutableIntList toImmutable()
    {
        return this;
    }

    public int getLast()
    {
        return this.codePoints.getLast();
    }

    public LazyIntIterable asReversed()
    {
        return this.codePoints.asReversed();
    }

    public <T> T injectIntoWithIndex(T injectedValue, ObjectIntIntToObjectFunction<? super T, ? extends T> function)
    {
        return this.codePoints.injectIntoWithIndex(injectedValue, function);
    }

    public int getFirst()
    {
        return this.codePoints.getFirst();
    }

    public int indexOf(int value)
    {
        return this.codePoints.indexOf(value);
    }

    public void forEachWithIndex(IntIntProcedure procedure)
    {
        this.codePoints.forEachWithIndex(procedure);
    }

    public CodePointList select(IntPredicate predicate)
    {
        return new CodePointList(this.codePoints.select(predicate));
    }

    public CodePointList reject(IntPredicate predicate)
    {
        return new CodePointList(this.codePoints.reject(predicate));
    }

    public <V> ImmutableList<V> collect(IntToObjectFunction<? extends V> function)
    {
        return this.codePoints.collect(function);
    }

    public CodePointList collectInt(IntToIntFunction function)
    {
        IntArrayList collected = new IntArrayList(this.size());
        for (int i = 0; i < this.size(); i++)
        {
            int codePoint = this.get(i);
            collected.add(function.valueOf(codePoint));
        }
        return new CodePointList(collected.toImmutable());
    }

    public int detectIfNone(IntPredicate predicate, int ifNone)
    {
        return this.codePoints.detectIfNone(predicate, ifNone);
    }

    public int count(IntPredicate predicate)
    {
        return this.codePoints.count(predicate);
    }

    public boolean anySatisfy(IntPredicate predicate)
    {
        return this.codePoints.anySatisfy(predicate);
    }

    public boolean allSatisfy(IntPredicate predicate)
    {
        return this.codePoints.allSatisfy(predicate);
    }

    public boolean noneSatisfy(IntPredicate predicate)
    {
        return this.codePoints.noneSatisfy(predicate);
    }

    @Override
    public MutableIntList toList()
    {
        return this.codePoints.toList();
    }

    @Override
    public MutableIntSet toSet()
    {
        return this.codePoints.toSet();
    }

    @Override
    public MutableIntBag toBag()
    {
        return this.codePoints.toBag();
    }

    public <T> T injectInto(T injectedValue, ObjectIntToObjectFunction<? super T, ? extends T> function)
    {
        return this.codePoints.injectInto(injectedValue, function);
    }

    public long sum()
    {
        return this.codePoints.sum();
    }

    public int max()
    {
        return this.codePoints.max();
    }

    public int min()
    {
        return this.codePoints.min();
    }

    public int size()
    {
        return this.codePoints.size();
    }

    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        try
        {
            appendable.append(start);
            int size = this.size();
            for (int i = 0; i < size; i++)
            {
                if (i > 0)
                {
                    appendable.append(separator);
                }
                int codePoint = this.get(i);
                if (appendable instanceof StringBuilder builder)
                {
                    builder.appendCodePoint(codePoint);
                }
                else if (appendable instanceof StringBuffer buffer)
                {
                    buffer.appendCodePoint(codePoint);
                }
                else
                {
                    char[] chars = Character.toChars(codePoint);
                    for (int j = 0; j < chars.length; j++)
                    {
                        appendable.append(chars[j]);
                    }
                }
            }
            appendable.append(end);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object otherList)
    {
        return this.codePoints.equals(otherList);
    }

    @Override
    public int hashCode()
    {
        return this.codePoints.hashCode();
    }
}
