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
import java.util.stream.Collectors;

import com.gs.collections.api.IntIterable;
import com.gs.collections.api.list.primitive.ImmutableIntList;
import com.gs.collections.impl.block.factory.primitive.IntPredicates;
import com.gs.collections.impl.list.immutable.primitive.AbstractImmutableIntListTestCase;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CodePointAdapterTest extends AbstractImmutableIntListTestCase
{
    private static final String UNICODE_STRING = "\u3042\uD840\uDC00\u3044\uD840\uDC03\u3046\uD83D\uDE09";

    @Override
    protected ImmutableIntList classUnderTest()
    {
        return CodePointAdapter.from(1, 2, 3);
    }

    @Override
    protected ImmutableIntList newWith(int... elements)
    {
        return CodePointAdapter.from(elements);
    }

    @SuppressWarnings("StringBufferReplaceableByString")
    @Test
    public void stringBuilder()
    {
        CodePointAdapter adapt = CodePointAdapter.adapt(UNICODE_STRING);
        Assertions.assertEquals(UNICODE_STRING, new StringBuilder(adapt).toString());
    }

    @Test
    public void subSequence()
    {
        CodePointAdapter adapt = CodePointAdapter.adapt(UNICODE_STRING);
        CharSequence sequence = adapt.subSequence(1, 3);
        Assertions.assertEquals(UNICODE_STRING.subSequence(1, 3), sequence);
    }

    @Override
    @Test
    public void testEquals()
    {
        super.testEquals();
        ImmutableIntList list1 = this.newWith(1, 2, 3, 4);
        ImmutableIntList list2 = this.newWith(4, 3, 2, 1);
        Assertions.assertNotEquals(list1, list2);
        Assertions.assertEquals(CodePointAdapter.adapt(UNICODE_STRING), CodePointAdapter.adapt(UNICODE_STRING));
        Assertions.assertNotEquals(CodePointAdapter.adapt("\u3042\uD840\uDC00\u3044\uD840\uDC03\u3046"), CodePointAdapter.adapt(UNICODE_STRING));
        Assertions.assertEquals(CodePointAdapter.adapt("ABC"), CodePointAdapter.adapt("ABC"));
        Assertions.assertNotEquals(CodePointAdapter.adapt("123"), CodePointAdapter.adapt("ABC"));
        Verify.assertEqualsAndHashCode(CodePointAdapter.adapt("ABC"), CodePointList.from("ABC"));
        Verify.assertEqualsAndHashCode(CodePointAdapter.adapt(UNICODE_STRING), CodePointList.from(UNICODE_STRING));
        Assertions.assertNotEquals(CodePointList.from("123"), CodePointAdapter.adapt("ABC"));
        Assertions.assertNotEquals(CodePointAdapter.adapt("ABC"), CodePointList.from("123"));
        Assertions.assertNotEquals(CodePointList.from("ABCD"), CodePointAdapter.adapt("ABC"));
        Assertions.assertNotEquals(CodePointAdapter.adapt("ABC"), CodePointList.from("ABCD"));
        Assertions.assertNotEquals(CodePointList.from("ABC"), CodePointAdapter.adapt("ABCD"));
        Assertions.assertNotEquals(CodePointAdapter.adapt("ABCD"), CodePointList.from("ABC"));
    }

    @Override
    @Test
    public void max()
    {
        Assertions.assertEquals(9L, this.newWith(1, 2, 9).max());
        Assertions.assertEquals(32L, this.newWith(1, 0, 9, 30, 31, 32).max());
        Assertions.assertEquals(32L, this.newWith(0, 9, 30, 31, 32).max());
        Assertions.assertEquals(31L, this.newWith(31, 0, 30).max());
        Assertions.assertEquals(39L, this.newWith(32, 39, 35).max());
        Assertions.assertEquals(this.classUnderTest().size(), this.classUnderTest().max());
    }

    @Override
    @Test
    public void min()
    {
        Assertions.assertEquals(1L, this.newWith(1, 2, 9).min());
        Assertions.assertEquals(0L, this.newWith(1, 0, 9, 30, 31, 32).min());
        Assertions.assertEquals(31L, this.newWith(31, 32, 33).min());
        Assertions.assertEquals(32L, this.newWith(32, 39, 35).min());
        Assertions.assertEquals(1L, this.classUnderTest().min());
    }

    @Override
    @Test
    public void allSatisfy()
    {
        Assertions.assertFalse(this.newWith(1, 0, 2).allSatisfy(IntPredicates.greaterThan(0)));
        Assertions.assertTrue(this.newWith(1, 2, 3).allSatisfy(IntPredicates.greaterThan(0)));
        Assertions.assertFalse(this.newWith(1, 0, 31, 32).allSatisfy(IntPredicates.greaterThan(0)));
        Assertions.assertFalse(this.newWith(1, 0, 31, 32).allSatisfy(IntPredicates.greaterThan(0)));
        Assertions.assertTrue(this.newWith(1, 2, 31, 32).allSatisfy(IntPredicates.greaterThan(0)));
        Assertions.assertFalse(this.newWith(32).allSatisfy(IntPredicates.equal(33)));
        IntIterable iterable = this.newWith(0, 1, 2);
        Assertions.assertFalse(iterable.allSatisfy(value -> 3 < value));
        Assertions.assertTrue(iterable.allSatisfy(IntPredicates.lessThan(3)));

        IntIterable iterable1 = this.classUnderTest();
        int size = iterable1.size();
        Assertions.assertEquals(size == 0, iterable1.allSatisfy(IntPredicates.greaterThan(3)));
        Assertions.assertEquals(size < 3, iterable1.allSatisfy(IntPredicates.lessThan(3)));
    }

    @Override
    @Test
    public void anySatisfy()
    {
        Assertions.assertTrue(this.newWith(1, 2).anySatisfy(IntPredicates.greaterThan(0)));
        Assertions.assertFalse(this.newWith(1, 2).anySatisfy(IntPredicates.equal(0)));
        Assertions.assertTrue(this.newWith(31, 32).anySatisfy(IntPredicates.greaterThan(0)));
        Assertions.assertTrue(this.newWith(2, 31, 32).anySatisfy(IntPredicates.greaterThan(0)));
        Assertions.assertFalse(this.newWith(1, 31, 32).anySatisfy(IntPredicates.equal(0)));
        Assertions.assertTrue(this.newWith(32).anySatisfy(IntPredicates.greaterThan(0)));
        IntIterable iterable = this.newWith(0, 1, 2);
        Assertions.assertTrue(iterable.anySatisfy(value -> value < 3));
        Assertions.assertFalse(iterable.anySatisfy(IntPredicates.greaterThan(3)));

        IntIterable iterable1 = this.classUnderTest();
        int size = iterable1.size();
        Assertions.assertEquals(size > 3, iterable1.anySatisfy(IntPredicates.greaterThan(3)));
        Assertions.assertEquals(size != 0, iterable1.anySatisfy(IntPredicates.lessThan(3)));
    }

    @Override
    @Test
    public void testToString()
    {
        StringBuilder expectedString = new StringBuilder();
        int size = this.classUnderTest().size();
        for (int each = 0; each < size; each++)
        {
            expectedString.appendCodePoint(each + 1);
        }
        Assertions.assertEquals(expectedString.toString(), this.classUnderTest().toString());
    }

    @Override
    @Test
    public void makeString()
    {
        ImmutableIntList list = this.classUnderTest();
        StringBuilder expectedString = new StringBuilder("");
        StringBuilder expectedString1 = new StringBuilder("");
        int size = list.size();
        for (int each = 0; each < size; each++)
        {
            expectedString.appendCodePoint(each + 1);
            expectedString1.appendCodePoint(each + 1);
            expectedString.append(each == size - 1 ? "" : ", ");
            expectedString1.append(each == size - 1 ? "" : "/");
        }
        Assertions.assertEquals(expectedString.toString(), list.makeString());
        Assertions.assertEquals(expectedString1.toString(), list.makeString("/"));
    }

    @Override
    @Test
    public void appendString()
    {
        StringBuilder expectedString = new StringBuilder("");
        StringBuilder expectedString1 = new StringBuilder("");
        int size = this.classUnderTest().size();
        for (int each = 0; each < size; each++)
        {
            expectedString.appendCodePoint(each + 1);
            expectedString1.appendCodePoint(each + 1);
            expectedString.append(each == size - 1 ? "" : ", ");
            expectedString1.append(each == size - 1 ? "" : "/");
        }
        ImmutableIntList list = this.classUnderTest();
        StringBuilder appendable2 = new StringBuilder();
        list.appendString(appendable2);
        Assertions.assertEquals(expectedString.toString(), appendable2.toString());
        StringBuilder appendable3 = new StringBuilder();
        list.appendString(appendable3, "/");
        Assertions.assertEquals(expectedString1.toString(), appendable3.toString());
    }

    @SuppressWarnings("StringBufferMayBeStringBuilder")
    @Test
    public void appendStringStringBuffer()
    {
        StringBuffer expectedString = new StringBuffer("");
        StringBuffer expectedString1 = new StringBuffer("");
        int size = this.classUnderTest().size();
        for (int each = 0; each < size; each++)
        {
            expectedString.appendCodePoint(each + 1);
            expectedString1.appendCodePoint(each + 1);
            expectedString.append(each == size - 1 ? "" : ", ");
            expectedString1.append(each == size - 1 ? "" : "/");
        }
        ImmutableIntList list = this.classUnderTest();
        StringBuffer appendable2 = new StringBuffer();
        list.appendString(appendable2);
        Assertions.assertEquals(expectedString.toString(), appendable2.toString());
        StringBuffer appendable3 = new StringBuffer();
        list.appendString(appendable3, "/");
        Assertions.assertEquals(expectedString1.toString(), appendable3.toString());
        StringBuffer appendable4 = new StringBuffer();
        this.classUnderTest().appendString(appendable4, "", "", "");
        Assertions.assertEquals(this.classUnderTest().toString(), appendable4.toString());
    }

    @Test
    public void appendStringAppendable()
    {
        StringBuilder expectedString = new StringBuilder();
        StringBuilder expectedString1 = new StringBuilder();
        int size = this.classUnderTest().size();
        for (int each = 0; each < size; each++)
        {
            expectedString.appendCodePoint(each + 1);
            expectedString1.appendCodePoint(each + 1);
            expectedString.append(each == size - 1 ? "" : ", ");
            expectedString1.append(each == size - 1 ? "" : "/");
        }
        ImmutableIntList list = this.classUnderTest();
        SBAppendable appendable2 = new SBAppendable();
        list.appendString(appendable2);
        Assertions.assertEquals(expectedString.toString(), appendable2.toString());
        SBAppendable appendable3 = new SBAppendable();
        list.appendString(appendable3, "/");
        Assertions.assertEquals(expectedString1.toString(), appendable3.toString());
    }

    @Test
    public void collectCodePointUnicode()
    {
        Assertions.assertEquals(
                UNICODE_STRING.codePoints().boxed().collect(Collectors.toList()),
                CodePointAdapter.adapt(UNICODE_STRING).collect(i -> i));
        Assertions.assertEquals(
                UNICODE_STRING.codePoints().boxed().collect(Collectors.toList()),
                CodePointAdapter.adapt(UNICODE_STRING).collect(i -> i));
    }

    @Test
    public void selectCodePointUnicode()
    {
        String string = CodePointAdapter.adapt(UNICODE_STRING).select(Character::isBmpCodePoint).toString();
        Assertions.assertEquals("\u3042\u3044\u3046", string);
    }

    @Test
    public void allSatisfyUnicode()
    {
        Assertions.assertTrue(CodePointAdapter.adapt("\u3042\u3044\u3046").allSatisfy(Character::isBmpCodePoint));
        Assertions.assertFalse(CodePointAdapter.adapt("\uD840\uDC00\uD840\uDC03\uD83D\uDE09").allSatisfy(Character::isBmpCodePoint));
    }

    @Test
    public void anySatisfyUnicode()
    {
        Assertions.assertTrue(CodePointAdapter.adapt("\u3042\u3044\u3046").anySatisfy(Character::isBmpCodePoint));
        Assertions.assertFalse(CodePointAdapter.adapt("\uD840\uDC00\uD840\uDC03\uD83D\uDE09").anySatisfy(Character::isBmpCodePoint));
    }

    @Test
    public void noneSatisfyUnicode()
    {
        Assertions.assertFalse(CodePointAdapter.adapt("\u3042\u3044\u3046").noneSatisfy(Character::isBmpCodePoint));
        Assertions.assertTrue(CodePointAdapter.adapt("\uD840\uDC00\uD840\uDC03\uD83D\uDE09").noneSatisfy(Character::isBmpCodePoint));
    }

    @Test
    public void forEachUnicode()
    {
        StringBuilder builder = new StringBuilder();
        CodePointAdapter.adapt(UNICODE_STRING).forEach(builder::appendCodePoint);
        Assertions.assertEquals(UNICODE_STRING, builder.toString());
    }

    @Test
    public void asReversedForEachUnicode()
    {
        StringBuilder builder = new StringBuilder();
        CodePointAdapter.adapt(UNICODE_STRING).asReversed().forEach(builder::appendCodePoint);
        Assertions.assertEquals("\uD83D\uDE09\u3046\uD840\uDC03\u3044\uD840\uDC00\u3042", builder.toString());

        StringBuilder builder2 = new StringBuilder();
        CodePointAdapter.adapt("\uD840\uDC00\u3042\uD840\uDC03\u3044\uD83D\uDE09\u3046").asReversed().forEach(builder2::appendCodePoint);
        Assertions.assertEquals("\u3046\uD83D\uDE09\u3044\uD840\uDC03\u3042\uD840\uDC00", builder2.toString());

        CodePointAdapter.adapt("").asReversed().forEach((int codePoint) -> Assertions.fail());
    }

    @Test
    public void asReversedForEachInvalidUnicode()
    {
        StringBuilder builder = new StringBuilder();
        CodePointAdapter.adapt("\u3042\uDC00\uD840\u3044\uDC03\uD840\u3046\uDE09\uD83D").asReversed().forEach(builder::appendCodePoint);
        Assertions.assertEquals("\uD83D\uDE09\u3046\uD840\uDC03\u3044\uD840\uDC00\u3042", builder.toString());

        StringBuilder builder2 = new StringBuilder();
        CodePointAdapter.adapt("\u3042\uD840\u3044\uD840\u3046\uD840").asReversed().forEach(builder2::appendCodePoint);
        Assertions.assertEquals("\uD840\u3046\uD840\u3044\uD840\u3042", builder2.toString());

        StringBuilder builder3 = new StringBuilder();
        CodePointAdapter.adapt("\u3042\uDC00\u3044\uDC03\u3046\uDC06").asReversed().forEach(builder3::appendCodePoint);
        Assertions.assertEquals("\uDC06\u3046\uDC03\u3044\uDC00\u3042", builder3.toString());

        CodePointAdapter.adapt("").asReversed().forEach((int codePoint) -> Assertions.fail());
    }

    @Test
    public void toReversedForEachUnicode()
    {
        StringBuilder builder = new StringBuilder();
        CodePointAdapter.adapt(UNICODE_STRING).toReversed().forEach(builder::appendCodePoint);
        Assertions.assertEquals("\uD83D\uDE09\u3046\uD840\uDC03\u3044\uD840\uDC00\u3042", builder.toString());

        StringBuilder builder2 = new StringBuilder();
        CodePointAdapter.adapt("\uD840\uDC00\u3042\uD840\uDC03\u3044\uD83D\uDE09\u3046").toReversed().forEach(builder2::appendCodePoint);
        Assertions.assertEquals("\u3046\uD83D\uDE09\u3044\uD840\uDC03\u3042\uD840\uDC00", builder2.toString());

        CodePointAdapter.adapt("").toReversed().forEach((int codePoint) -> Assertions.fail());
    }

    @Test
    public void toReversedForEachInvalidUnicode()
    {
        StringBuilder builder = new StringBuilder();
        CodePointAdapter.adapt("\u3042\uDC00\uD840\u3044\uDC03\uD840\u3046\uDE09\uD83D").toReversed().forEach(builder::appendCodePoint);
        Assertions.assertEquals("\uD83D\uDE09\u3046\uD840\uDC03\u3044\uD840\uDC00\u3042", builder.toString());

        StringBuilder builder2 = new StringBuilder();
        CodePointAdapter.adapt("\u3042\uD840\u3044\uD840\u3046\uD840").toReversed().forEach(builder2::appendCodePoint);
        Assertions.assertEquals("\uD840\u3046\uD840\u3044\uD840\u3042", builder2.toString());

        StringBuilder builder3 = new StringBuilder();
        CodePointAdapter.adapt("\u3042\uDC00\u3044\uDC03\u3046\uDC06").toReversed().forEach(builder3::appendCodePoint);
        Assertions.assertEquals("\uDC06\u3046\uDC03\u3044\uDC00\u3042", builder3.toString());

        CodePointAdapter.adapt("").toReversed().forEach((int codePoint) -> Assertions.fail());
    }

    @Test
    public void distinctUnicode()
    {
        Assertions.assertEquals(
                "\uD840\uDC00\uD840\uDC03\uD83D\uDE09",
                CodePointAdapter.adapt("\uD840\uDC00\uD840\uDC03\uD83D\uDE09\uD840\uDC00\uD840\uDC03\uD83D\uDE09").distinct().toString());
    }

    @Test
    public void newWithUnicode()
    {
        CodePointAdapter codePointAdapter = CodePointAdapter.adapt("");
        CodePointAdapter collection = codePointAdapter.newWith(12354);
        CodePointAdapter collection0 = codePointAdapter.newWith(12354).newWith(131072);
        CodePointAdapter collection1 = codePointAdapter.newWith(12354).newWith(131072).newWith(12356);
        CodePointAdapter collection2 = codePointAdapter.newWith(12354).newWith(131072).newWith(12356).newWith(131075);
        CodePointAdapter collection3 = codePointAdapter.newWith(12354).newWith(131072).newWith(12356).newWith(131075).newWith(12358);
        this.assertSizeAndContains(codePointAdapter);
        this.assertSizeAndContains(collection, 12354);
        this.assertSizeAndContains(collection0, 12354, 131072);
        this.assertSizeAndContains(collection1, 12354, 131072, 12356);
        this.assertSizeAndContains(collection2, 12354, 131072, 12356, 131075);
        this.assertSizeAndContains(collection3, 12354, 131072, 12356, 131075, 12358);
    }

    @Test
    public void newWithoutUnicode()
    {
        CodePointAdapter collection0 = CodePointAdapter.adapt("\u3042\uD840\uDC00\u3044\uD840\uDC03\u3046");
        CodePointAdapter collection1 = collection0.newWithout(12358);
        CodePointAdapter collection2 = collection1.newWithout(131075);
        CodePointAdapter collection3 = collection2.newWithout(12356);
        CodePointAdapter collection4 = collection3.newWithout(131072);
        CodePointAdapter collection5 = collection4.newWithout(12354);
        CodePointAdapter collection6 = collection5.newWithout(131078);

        this.assertSizeAndContains(collection6);
        this.assertSizeAndContains(collection5);
        this.assertSizeAndContains(collection4, 12354);
        this.assertSizeAndContains(collection3, 12354, 131072);
        this.assertSizeAndContains(collection2, 12354, 131072, 12356);
        this.assertSizeAndContains(collection1, 12354, 131072, 12356, 131075);
    }

    @Override
    @Test
    public void toReversed()
    {
        super.toReversed();
        Assertions.assertEquals("cba", CodePointAdapter.adapt("abc").toReversed().toString());
    }

    private static class SBAppendable implements Appendable
    {
        private final StringBuilder builder = new StringBuilder();

        @Override
        public Appendable append(char c) throws IOException
        {
            return this.builder.append(c);
        }

        @Override
        public Appendable append(CharSequence csq) throws IOException
        {
            return this.builder.append(csq);
        }

        @Override
        public Appendable append(CharSequence csq, int start, int end) throws IOException
        {
            return this.builder.append(csq, start, end);
        }

        @Override
        public String toString()
        {
            return this.builder.toString();
        }
    }
}
