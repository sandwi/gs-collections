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

package com.gs.collections.impl.utility;

import com.gs.collections.api.bag.MutableBag;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.procedure.primitive.CharProcedure;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.list.primitive.CharList;
import com.gs.collections.api.list.primitive.ImmutableCharList;
import com.gs.collections.api.list.primitive.IntList;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.set.primitive.ImmutableCharSet;
import com.gs.collections.api.set.primitive.ImmutableIntSet;
import com.gs.collections.api.tuple.Twin;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.Procedures;
import com.gs.collections.impl.block.factory.primitive.CharPredicates;
import com.gs.collections.impl.block.factory.primitive.CharToCharFunctions;
import com.gs.collections.impl.block.function.AddFunction;
import com.gs.collections.impl.block.function.primitive.CodePointFunction;
import com.gs.collections.impl.block.predicate.CodePointPredicate;
import com.gs.collections.impl.block.procedure.primitive.CodePointProcedure;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.factory.primitive.CharSets;
import com.gs.collections.impl.factory.primitive.IntSets;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.primitive.CharArrayList;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.string.immutable.CharAdapter;
import com.gs.collections.impl.string.immutable.CodePointAdapter;
import com.gs.collections.impl.string.immutable.CodePointList;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JUnit test for {@link StringIterate}.
 */
public class StringIterateTest
{
    public static final String THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG = "The quick brown fox jumps over the lazy dog.";
    public static final String ALPHABET_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    public static final Twin<String> HALF_ABET = StringIterate.splitAtIndex(ALPHABET_LOWERCASE, 13);
    public static final String TQBFJOTLD_MINUS_HALF_ABET_1 = "t qu rown ox ups ovr t zy o.";
    public static final String TQBFJOTLD_MINUS_HALF_ABET_2 = "he ick b f jm e he la dg.";

    @Test
    public void asCharAdapter()
    {
        CharAdapter answer =
                StringIterate.asCharAdapter("HelloHellow")
                        .collectChar(Character::toUpperCase)
                        .select(c -> c != 'W')
                        .distinct()
                        .toReversed()
                        .reject(CharAdapter.adapt("LE")::contains)
                        .newWith('!');

        Assertions.assertEquals("OH!", answer.toString());
        Assertions.assertEquals("OH!", answer.toStringBuilder().toString());
        Assertions.assertEquals("OH!", answer.makeString(""));

        CharList charList = StringIterate.asCharAdapter("HelloHellow")
                .asLazy()
                .collectChar(Character::toUpperCase)
                .select(c -> c != 'W')
                .toList()
                .distinct()
                .toReversed()
                .reject(CharAdapter.adapt("LE")::contains)
                .with('!');

        Assertions.assertEquals("OH!", CharAdapter.from(charList).toString());
        Assertions.assertEquals("OH!", CharAdapter.from(CharAdapter.from(charList)).toString());

        String helloUppercase2 = StringIterate.asCharAdapter("Hello")
                .asLazy()
                .collectChar(Character::toUpperCase)
                .makeString("");
        Assertions.assertEquals("HELLO", helloUppercase2);

        CharArrayList arraylist = new CharArrayList();
        StringIterate.asCharAdapter("Hello".toUpperCase())
                .chars()
                .sorted()
                .forEach(e -> arraylist.add((char) e));
        Assertions.assertEquals(StringIterate.asCharAdapter("EHLLO"), arraylist);

        ImmutableCharList arrayList2 =
                StringIterate.asCharAdapter("Hello".toUpperCase())
                        .toSortedList()
                        .toImmutable();

        Assertions.assertEquals(StringIterate.asCharAdapter("EHLLO"), arrayList2);

        Assertions.assertEquals(StringIterate.asCharAdapter("HELLO"), CharAdapter.adapt("hello").collectChar(Character::toUpperCase));
    }

    @Test
    public void asCharAdapterExtra()
    {
        Assertions.assertEquals(9,
                StringIterate.asCharAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG)
                        .count(c -> !Character.isLetter(c)));

        Assertions.assertTrue(
                StringIterate.asCharAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).anySatisfy(Character::isWhitespace));

        Assertions.assertEquals(8,
                StringIterate.asCharAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG)
                        .count(Character::isWhitespace));

        Verify.assertSize(26,
                StringIterate.asCharAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG)
                        .asLazy()
                        .select(Character::isLetter)
                        .collectChar(Character::toLowerCase).toSet());

        ImmutableCharSet alphaCharAdapter =
                StringIterate.asCharAdapter(ALPHABET_LOWERCASE).toSet().toImmutable();
        Assertions.assertTrue(
                StringIterate.asCharAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).containsAll(alphaCharAdapter));
        Assertions.assertEquals(
                CharSets.immutable.empty(),
                alphaCharAdapter.newWithoutAll(StringIterate.asCharAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase())));
        Assertions.assertEquals(
                TQBFJOTLD_MINUS_HALF_ABET_1,
                StringIterate.asCharAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase())
                        .newWithoutAll(StringIterate.asCharAdapter(HALF_ABET.getOne()))
                        .toString());
        Assertions.assertEquals(
                TQBFJOTLD_MINUS_HALF_ABET_2,
                StringIterate.asCharAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase())
                        .newWithoutAll(StringIterate.asCharAdapter(HALF_ABET.getTwo()))
                        .toString());
    }

    @Test
    public void buildTheAlphabetFromEmpty()
    {
        String alphabet = StringIterate.asCharAdapter("")
                .newWith('a')
                .newWithAll(StringIterate.asCharAdapter(HALF_ABET.getOne()))
                .newWithAll(StringIterate.asCharAdapter(HALF_ABET.getTwo()))
                .newWithout('a').toString();
        Assertions.assertEquals(ALPHABET_LOWERCASE, alphabet);
    }

    @Test
    public void asCodePointAdapter()
    {
        CodePointAdapter answer =
                StringIterate.asCodePointAdapter("HelloHellow")
                        .collectInt(Character::toUpperCase)
                        .select(i -> i != 'W')
                        .distinct()
                        .toReversed()
                        .reject(CodePointAdapter.adapt("LE")::contains)
                        .newWith('!');

        Assertions.assertEquals("OH!", answer.toString());
        Assertions.assertEquals("OH!", answer.toStringBuilder().toString());
        Assertions.assertEquals("OH!", answer.makeString(""));

        IntList intList = StringIterate.asCodePointAdapter("HelloHellow")
                .asLazy()
                .collectInt(Character::toUpperCase)
                .select(i -> i != 'W')
                .toList()
                .distinct()
                .toReversed()
                .reject(CodePointAdapter.adapt("LE")::contains)
                .with('!');

        Assertions.assertEquals("OH!", CodePointAdapter.from(intList).toString());
        Assertions.assertEquals("OH!", CodePointAdapter.from(CodePointAdapter.from(intList)).toString());
    }

    @Test
    public void asCodePointAdapterExtra()
    {
        Assertions.assertEquals(9,
                StringIterate.asCodePointAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG)
                        .count(i -> !Character.isLetter(i)));

        Assertions.assertTrue(
                StringIterate.asCodePointAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).anySatisfy(Character::isWhitespace));

        Assertions.assertEquals(8,
                StringIterate.asCodePointAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG)
                        .count(Character::isWhitespace));

        Verify.assertSize(26,
                StringIterate.asCodePointAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG)
                        .asLazy()
                        .select(Character::isLetter)
                        .collectInt(Character::toLowerCase).toSet());

        ImmutableIntSet alphaints =
                StringIterate.asCodePointAdapter(ALPHABET_LOWERCASE).toSet().toImmutable();
        Assertions.assertTrue(
                StringIterate.asCodePointAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).containsAll(alphaints));
        Assertions.assertEquals(
                IntSets.immutable.empty(),
                alphaints.newWithoutAll(StringIterate.asCodePointAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase())));
        Assertions.assertEquals(
                TQBFJOTLD_MINUS_HALF_ABET_1,
                StringIterate.asCodePointAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase())
                        .newWithoutAll(StringIterate.asCodePointAdapter(HALF_ABET.getOne()))
                        .toString());
        Assertions.assertEquals(
                TQBFJOTLD_MINUS_HALF_ABET_2,
                StringIterate.asCodePointAdapter(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase())
                        .newWithoutAll(StringIterate.asCodePointAdapter(HALF_ABET.getTwo()))
                        .toString());
    }

    @Test
    public void toCodePointList()
    {
        CodePointList answer =
                StringIterate.toCodePointList("Hello")
                        .collectInt(Character::toUpperCase)
                        .select(i -> i != 'W')
                        .distinct()
                        .toReversed()
                        .reject(CodePointList.from("LE")::contains)
                        .newWith('!');

        Assertions.assertEquals("OH!", answer.toString());
        Assertions.assertEquals("OH!", answer.toStringBuilder().toString());
        Assertions.assertEquals("OH!", answer.makeString(""));

        IntList intList = StringIterate.toCodePointList("HelloHellow")
                .asLazy()
                .collectInt(Character::toUpperCase)
                .select(i -> i != 'W')
                .toList()
                .distinct()
                .toReversed()
                .reject(CodePointList.from("LE")::contains)
                .with('!');

        Assertions.assertEquals("OH!", CodePointList.from(intList).toString());
        Assertions.assertEquals("OH!", CodePointList.from(CodePointList.from(intList)).toString());
    }

    @Test
    public void toCodePointListExtra()
    {
        Assertions.assertEquals(9,
                StringIterate.toCodePointList(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG)
                        .count(i -> !Character.isLetter(i)));

        Assertions.assertTrue(
                StringIterate.toCodePointList(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).anySatisfy(Character::isWhitespace));

        Assertions.assertEquals(8,
                StringIterate.toCodePointList(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG)
                        .count(Character::isWhitespace));

        Verify.assertSize(26,
                StringIterate.toCodePointList(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG)
                        .asLazy()
                        .select(Character::isLetter)
                        .collectInt(Character::toLowerCase).toSet());

        ImmutableIntSet alphaints =
                StringIterate.toCodePointList(ALPHABET_LOWERCASE).toSet().toImmutable();
        Assertions.assertTrue(
                StringIterate.toCodePointList(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG).containsAll(alphaints));
        Assertions.assertEquals(
                IntSets.immutable.empty(),
                alphaints.newWithoutAll(StringIterate.toCodePointList(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase())));
        Assertions.assertTrue(
                StringIterate.toCodePointList(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG)
                        .containsAll(StringIterate.toCodePointList(HALF_ABET.getOne())));
        Assertions.assertEquals(
                TQBFJOTLD_MINUS_HALF_ABET_1,
                StringIterate.toCodePointList(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase())
                        .newWithoutAll(StringIterate.toCodePointList(HALF_ABET.getOne()))
                        .toString());
        Assertions.assertEquals(
                TQBFJOTLD_MINUS_HALF_ABET_2,
                StringIterate.toCodePointList(THE_QUICK_BROWN_FOX_JUMPS_OVER_THE_LAZY_DOG.toLowerCase())
                        .newWithoutAll(StringIterate.toCodePointList(HALF_ABET.getTwo()))
                        .toString());
    }

    @Test
    public void englishToUpperLowerCase()
    {
        Assertions.assertEquals("ABC", StringIterate.englishToUpperCase("abc"));
        Assertions.assertEquals("abc", StringIterate.englishToLowerCase("ABC"));
    }

    @Test
    public void collect()
    {
        Assertions.assertEquals("ABC", StringIterate.collect("abc", CharToCharFunctions.toUpperCase()));
        Assertions.assertEquals("abc", StringIterate.collect("abc", CharToCharFunctions.toLowerCase()));
    }

    @Test
    public void collectCodePoint()
    {
        Assertions.assertEquals("ABC", StringIterate.collect("abc", CodePointFunction.TO_UPPERCASE));
        Assertions.assertEquals("abc", StringIterate.collect("abc", CodePointFunction.TO_LOWERCASE));
    }

    @Test
    public void collectCodePointUnicode()
    {
        Assertions.assertEquals("\u3042\uD840\uDC00\u3044\uD840\uDC03\u3046\uD83D\uDE09", StringIterate.collect("\u3042\uD840\uDC00\u3044\uD840\uDC03\u3046\uD83D\uDE09", CodePointFunction.PASS_THRU));
        Assertions.assertEquals("\u3042\uD840\uDC00\u3044\uD840\uDC03\u3046\uD83D\uDE09", StringIterate.collect("\u3042\uD840\uDC00\u3044\uD840\uDC03\u3046\uD83D\uDE09", CodePointFunction.PASS_THRU));
    }

    @Test
    public void englishToUpperCase()
    {
        Assertions.assertEquals("ABC", StringIterate.englishToUpperCase("abc"));
        Assertions.assertEquals("A,B,C", StringIterate.englishToUpperCase("a,b,c"));
        Assertions.assertSame("A,B,C", StringIterate.englishToUpperCase("A,B,C"));
    }

    @Test
    public void englishToLowerCase()
    {
        Assertions.assertEquals("abc", StringIterate.englishToLowerCase("ABC"));
        Assertions.assertEquals("a,b,c", StringIterate.englishToLowerCase("A,B,C"));
        Assertions.assertSame("a,b,c", StringIterate.englishToLowerCase("a,b,c"));
    }

    @Test
    public void englishIsUpperLowerCase()
    {
        String allValues = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890~`!@#$%^&*()_-+=[]{};<>,.?/|";
        String jdkUpper = allValues.toUpperCase();
        String upper = StringIterate.englishToUpperCase(allValues);
        Assertions.assertEquals(jdkUpper.length(), upper.length());
        Assertions.assertEquals(jdkUpper, upper);
        String jdkLower = allValues.toLowerCase();
        String lower = StringIterate.englishToLowerCase(allValues);
        Assertions.assertEquals(jdkLower.length(), lower.length());
        Assertions.assertEquals(jdkLower, lower);
    }

    @Test
    public void select()
    {
        String string = StringIterate.select("1a2a3", CharPredicates.isDigit());
        Assertions.assertEquals("123", string);
    }

    @Test
    public void selectCodePoint()
    {
        String string = StringIterate.select("1a2a3", CodePointPredicate.IS_DIGIT);
        Assertions.assertEquals("123", string);
    }

    @Test
    public void selectCodePointUnicode()
    {
        String string = StringIterate.select("\u3042\uD840\uDC00\u3044\uD840\uDC03\u3046\uD83D\uDE09", CodePointPredicate.IS_BMP);
        Assertions.assertEquals("\u3042\u3044\u3046", string);
    }

    @Test
    public void detect()
    {
        char character = StringIterate.detect("1a2a3", CharPredicates.isLetter());
        Assertions.assertEquals('a', character);
    }

    @Test
    public void detectIfNone()
    {
        char character = StringIterate.detectIfNone("123", CharPredicates.isLetter(), "b".charAt(0));
        Assertions.assertEquals('b', character);
    }

    @Test
    public void detectIfNoneWithString()
    {
        char character = StringIterate.detectIfNone("123", CharPredicates.isLetter(), "b");
        Assertions.assertEquals('b', character);
    }

    @Test
    public void allSatisfy()
    {
        Assertions.assertTrue(StringIterate.allSatisfy("MARY", CharPredicates.isUpperCase()));
        Assertions.assertFalse(StringIterate.allSatisfy("Mary", CharPredicates.isUpperCase()));
    }

    @Test
    public void allSatisfyCodePoint()
    {
        Assertions.assertTrue(StringIterate.allSatisfy("MARY", CodePointPredicate.IS_UPPERCASE));
        Assertions.assertFalse(StringIterate.allSatisfy("Mary", CodePointPredicate.IS_UPPERCASE));
    }

    @Test
    public void allSatisfyCodePointUnicode()
    {
        Assertions.assertTrue(StringIterate.allSatisfy("\u3042\u3044\u3046", CodePointPredicate.IS_BMP));
        Assertions.assertFalse(StringIterate.allSatisfy("\uD840\uDC00\uD840\uDC03\uD83D\uDE09", CodePointPredicate.IS_BMP));
    }

    @Test
    public void anySatisfy()
    {
        Assertions.assertTrue(StringIterate.anySatisfy("MARY", CharPredicates.isUpperCase()));
        Assertions.assertFalse(StringIterate.anySatisfy("mary", CharPredicates.isUpperCase()));
    }

    @Test
    public void anySatisfyCodePoint()
    {
        Assertions.assertTrue(StringIterate.anySatisfy("MARY", CodePointPredicate.IS_UPPERCASE));
        Assertions.assertFalse(StringIterate.anySatisfy("mary", CodePointPredicate.IS_UPPERCASE));
    }

    @Test
    public void anySatisfyCodePointUnicode()
    {
        Assertions.assertTrue(StringIterate.anySatisfy("\u3042\u3044\u3046", CodePointPredicate.IS_BMP));
        Assertions.assertFalse(StringIterate.anySatisfy("\uD840\uDC00\uD840\uDC03\uD83D\uDE09", CodePointPredicate.IS_BMP));
    }

    @Test
    public void noneSatisfy()
    {
        Assertions.assertFalse(StringIterate.noneSatisfy("MaRy", CharPredicates.isUpperCase()));
        Assertions.assertTrue(StringIterate.noneSatisfy("mary", CharPredicates.isUpperCase()));
    }

    @Test
    public void noneSatisfyCodePoint()
    {
        Assertions.assertFalse(StringIterate.noneSatisfy("MaRy", CodePointPredicate.IS_UPPERCASE));
        Assertions.assertTrue(StringIterate.noneSatisfy("mary", CodePointPredicate.IS_UPPERCASE));
    }

    @Test
    public void noneSatisfyCodePointUnicode()
    {
        Assertions.assertFalse(StringIterate.noneSatisfy("\u3042\u3044\u3046", CodePointPredicate.IS_BMP));
        Assertions.assertTrue(StringIterate.noneSatisfy("\uD840\uDC00\uD840\uDC03\uD83D\uDE09", CodePointPredicate.IS_BMP));
    }

    @Test
    public void isNumber()
    {
        Assertions.assertTrue(StringIterate.isNumber("123"));
        Assertions.assertFalse(StringIterate.isNumber("abc"));
        Assertions.assertFalse(StringIterate.isNumber(""));
    }

    @Test
    public void isAlphaNumeric()
    {
        Assertions.assertTrue(StringIterate.isAlphaNumeric("123"));
        Assertions.assertTrue(StringIterate.isAlphaNumeric("abc"));
        Assertions.assertTrue(StringIterate.isAlphaNumeric("123abc"));
        Assertions.assertFalse(StringIterate.isAlphaNumeric("!@#"));
        Assertions.assertFalse(StringIterate.isAlphaNumeric(""));
    }

    @Test
    public void csvTokensToList()
    {
        String tokens = "Ted,Mary  ";
        MutableList<String> results = StringIterate.csvTokensToList(tokens);
        Verify.assertSize(2, results);
        Verify.assertStartsWith(results, "Ted", "Mary  ");
    }

    @Test
    public void csvTokensToSortedList()
    {
        String tokens = " Ted, Mary ";
        MutableList<String> results = StringIterate.csvTokensToSortedList(tokens);
        Verify.assertSize(2, results);
        Verify.assertStartsWith(results, " Mary ", " Ted");
    }

    @Test
    public void csvTrimmedTokensToSortedList()
    {
        String tokens = " Ted,Mary ";
        MutableList<String> results = StringIterate.csvTrimmedTokensToSortedList(tokens);
        Verify.assertSize(2, results);
        Verify.assertStartsWith(results, "Mary", "Ted");
    }

    @Test
    public void csvTokensToSet()
    {
        String tokens = "Ted,Mary";
        MutableSet<String> results = StringIterate.csvTokensToSet(tokens);
        Verify.assertSize(2, results);
        Verify.assertContainsAll(results, "Mary", "Ted");
    }

    @Test
    public void csvTokensToReverseSortedList()
    {
        String tokens = "Ted,Mary";
        MutableList<String> results = StringIterate.csvTokensToReverseSortedList(tokens);
        Verify.assertSize(2, results);
    }

    @Test
    public void tokensToMap()
    {
        String tokens = "1:Ted|2:Mary";
        MutableMap<String, String> results = StringIterate.tokensToMap(tokens);
        Verify.assertSize(2, results);
        Verify.assertContainsKeyValue("Ted", results, "1");
        Verify.assertContainsKeyValue("Mary", results, "2");
    }

    @Test
    public void tokensToMapWithFunctions()
    {
        String tokens = "1:Ted|2:Mary";
        Function<String, String> stringPassThruFunction = Functions.getPassThru();
        MutableMap<Integer, String> results = StringIterate.tokensToMap(tokens, "|", ":", Integer::valueOf, stringPassThruFunction);
        Verify.assertSize(2, results);
        Verify.assertContainsKeyValue(1, "Ted", results);
        Verify.assertContainsKeyValue(2, "Mary", results);
    }

    @Test
    public void reject()
    {
        String string = StringIterate.reject("1a2b3c", CharPredicates.isDigit());
        Assertions.assertEquals("abc", string);
    }

    @Test
    public void rejectCodePoint()
    {
        String string = StringIterate.reject("1a2b3c", CodePointPredicate.IS_DIGIT);
        Assertions.assertEquals("abc", string);
    }

    @Test
    public void count()
    {
        int count = StringIterate.count("1a2a3", CharPredicates.isDigit());
        Assertions.assertEquals(3, count);
    }

    @Test
    public void countCodePoint()
    {
        int count = StringIterate.count("1a2a3", CodePointPredicate.IS_DIGIT);
        Assertions.assertEquals(3, count);
    }

    @Test
    public void occurrencesOf()
    {
        int count = StringIterate.occurrencesOf("1a2a3", 'a');
        Assertions.assertEquals(2, count);
    }

    @Test
    public void occurrencesOf_multiple_character_string_throws()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            StringIterate.occurrencesOf("1a2a3", "abc");
        });
    }

    @Test
    public void occurrencesOfCodePoint()
    {
        int count = StringIterate.occurrencesOf("1a2a3", "a".codePointAt(0));
        Assertions.assertEquals(2, count);
    }

    @Test
    public void occurrencesOfString()
    {
        int count = StringIterate.occurrencesOf("1a2a3", "a");
        Assertions.assertEquals(2, count);
    }

    @Test
    public void count2()
    {
        int count = StringIterate.count("1a2a3", CharPredicates.isUndefined());
        Assertions.assertEquals(0, count);
    }

    @Test
    public void count2CodePoint()
    {
        int count = StringIterate.count("1a2a3", CodePointPredicate.IS_UNDEFINED);
        Assertions.assertEquals(0, count);
    }

    @Test
    public void forEach()
    {
        StringBuilder builder = new StringBuilder();
        StringIterate.forEach("1a2b3c", (CharProcedure) builder::append);
        Assertions.assertEquals("1a2b3c", builder.toString());
    }

    @Test
    public void forEachCodePoint()
    {
        StringBuilder builder = new StringBuilder();
        StringIterate.forEach("1a2b3c", (CodePointProcedure) builder::appendCodePoint);
        Assertions.assertEquals("1a2b3c", builder.toString());
    }

    @Test
    public void forEachCodePointUnicode()
    {
        StringBuilder builder = new StringBuilder();
        StringIterate.forEach("\u3042\uD840\uDC00\u3044\uD840\uDC03\u3046\uD83D\uDE09", (CodePointProcedure) builder::appendCodePoint);
        Assertions.assertEquals("\u3042\uD840\uDC00\u3044\uD840\uDC03\u3046\uD83D\uDE09", builder.toString());
    }

    @Test
    public void reverseForEach()
    {
        StringBuilder builder = new StringBuilder();
        StringIterate.reverseForEach("1a2b3c", (CharProcedure) builder::append);
        Assertions.assertEquals("c3b2a1", builder.toString());

        StringIterate.reverseForEach("", (char character) -> Assertions.fail());
    }

    @Test
    public void reverseForEachCodePoint()
    {
        StringBuilder builder = new StringBuilder();
        StringIterate.reverseForEach("1a2b3c", (CodePointProcedure) builder::appendCodePoint);
        Assertions.assertEquals("c3b2a1", builder.toString());

        StringIterate.reverseForEach("", (int codePoint) -> Assertions.fail());
    }

    @Test
    public void reverseForEachCodePointUnicode()
    {
        StringBuilder builder = new StringBuilder();
        StringIterate.reverseForEach("\u3042\uD840\uDC00\u3044\uD840\uDC03\u3046\uD83D\uDE09", (CodePointProcedure) builder::appendCodePoint);
        Assertions.assertEquals("\uD83D\uDE09\u3046\uD840\uDC03\u3044\uD840\uDC00\u3042", builder.toString());
        StringIterate.reverseForEach("", (int codePoint) -> Assertions.fail());
    }

    @Test
    public void reverseForEachCodePointInvalidUnicode()
    {
        StringBuilder builder = new StringBuilder();
        StringIterate.reverseForEach("\u3042\uDC00\uD840\u3044\uDC03\uD840\u3046\uDE09\uD83D", (CodePointProcedure) builder::appendCodePoint);
        Assertions.assertEquals("\uD83D\uDE09\u3046\uD840\uDC03\u3044\uD840\uDC00\u3042", builder.toString());

        StringBuilder builder2 = new StringBuilder();
        StringIterate.reverseForEach("\u3042\uD840\u3044\uD840\u3046\uD840", (CodePointProcedure) builder2::appendCodePoint);
        Assertions.assertEquals("\uD840\u3046\uD840\u3044\uD840\u3042", builder2.toString());

        StringBuilder builder3 = new StringBuilder();
        StringIterate.reverseForEach("\u3042\uDC00\u3044\uDC03\u3046\uDC06", (CodePointProcedure) builder3::appendCodePoint);
        Assertions.assertEquals("\uDC06\u3046\uDC03\u3044\uDC00\u3042", builder3.toString());

        StringIterate.reverseForEach("", (int codePoint) -> Assertions.fail());
    }

    @Test
    public void forEachToken()
    {
        String tokens = "1,2";
        MutableList<Integer> list = Lists.mutable.of();
        StringIterate.forEachToken(tokens, ",", Procedures.throwing(string -> list.add(Integer.valueOf(string))));
        Verify.assertSize(2, list);
        Verify.assertContains(1, list);
        Verify.assertContains(2, list);
    }

    @Test
    public void forEachTrimmedToken()
    {
        String tokens = " 1,2 ";
        MutableList<Integer> list = Lists.mutable.of();
        StringIterate.forEachTrimmedToken(tokens, ",", Procedures.throwing(string -> list.add(Integer.valueOf(string))));
        Verify.assertSize(2, list);
        Verify.assertContains(1, list);
        Verify.assertContains(2, list);
    }

    @Test
    public void csvTrimmedTokenToList()
    {
        String tokens = " 1,2 ";
        Assertions.assertEquals(FastList.newListWith("1", "2"), StringIterate.csvTrimmedTokensToList(tokens));
    }

    @Test
    public void injectIntoTokens()
    {
        Assertions.assertEquals("123", StringIterate.injectIntoTokens("1,2,3", ",", null, AddFunction.STRING));
    }

    @Test
    public void getLastToken()
    {
        Assertions.assertEquals("charlie", StringIterate.getLastToken("alpha~|~beta~|~charlie", "~|~"));
        Assertions.assertEquals("123", StringIterate.getLastToken("123", "~|~"));
        Assertions.assertEquals("", StringIterate.getLastToken("", "~|~"));
        Assertions.assertNull(StringIterate.getLastToken(null, "~|~"));
        Assertions.assertEquals("", StringIterate.getLastToken("123~|~", "~|~"));
        Assertions.assertEquals("123", StringIterate.getLastToken("~|~123", "~|~"));
    }

    @Test
    public void getFirstToken()
    {
        Assertions.assertEquals("alpha", StringIterate.getFirstToken("alpha~|~beta~|~charlie", "~|~"));
        Assertions.assertEquals("123", StringIterate.getFirstToken("123", "~|~"));
        Assertions.assertEquals("", StringIterate.getFirstToken("", "~|~"));
        Assertions.assertNull(StringIterate.getFirstToken(null, "~|~"));
        Assertions.assertEquals("123", StringIterate.getFirstToken("123~|~", "~|~"));
        Assertions.assertEquals("", StringIterate.getFirstToken("~|~123,", "~|~"));
    }

    @Test
    public void isEmptyOrWhitespace()
    {
        Assertions.assertTrue(StringIterate.isEmptyOrWhitespace("   "));
        Assertions.assertFalse(StringIterate.isEmptyOrWhitespace(" 1  "));
    }

    @Test
    public void notEmptyOrWhitespace()
    {
        Assertions.assertFalse(StringIterate.notEmptyOrWhitespace("   "));
        Assertions.assertTrue(StringIterate.notEmptyOrWhitespace(" 1  "));
    }

    @Test
    public void isEmpty()
    {
        Assertions.assertTrue(StringIterate.isEmpty(""));
        Assertions.assertFalse(StringIterate.isEmpty("   "));
        Assertions.assertFalse(StringIterate.isEmpty("1"));
    }

    @Test
    public void notEmpty()
    {
        Assertions.assertFalse(StringIterate.notEmpty(""));
        Assertions.assertTrue(StringIterate.notEmpty("   "));
        Assertions.assertTrue(StringIterate.notEmpty("1"));
    }

    @Test
    public void repeat()
    {
        Assertions.assertEquals("", StringIterate.repeat("", 42));
        Assertions.assertEquals("    ", StringIterate.repeat(' ', 4));
        Assertions.assertEquals("        ", StringIterate.repeat(" ", 8));
        Assertions.assertEquals("CubedCubedCubed", StringIterate.repeat("Cubed", 3));
    }

    @Test
    public void padOrTrim()
    {
        Assertions.assertEquals("abcdefghijkl", StringIterate.padOrTrim("abcdefghijkl", 12));
        Assertions.assertEquals("this n", StringIterate.padOrTrim("this needs to be trimmed", 6));
        Assertions.assertEquals("pad this      ", StringIterate.padOrTrim("pad this", 14));
    }

    @Test
    public void string()
    {
        Assertions.assertEquals("Token2", StringIterate.getLastToken("Token1DelimiterToken2", "Delimiter"));
    }

    @Test
    public void toList()
    {
        Assertions.assertEquals(FastList.newListWith('a', 'a', 'b', 'c', 'd', 'e'), StringIterate.toList("aabcde"));
    }

    @Test
    public void toLowercaseList()
    {
        MutableList<Character> set = StringIterate.toLowercaseList("America");
        Assertions.assertEquals(FastList.newListWith('a', 'm', 'e', 'r', 'i', 'c', 'a'), set);
    }

    @Test
    public void toUppercaseList()
    {
        MutableList<Character> set = StringIterate.toUppercaseList("America");
        Assertions.assertEquals(FastList.newListWith('A', 'M', 'E', 'R', 'I', 'C', 'A'), set);
    }

    @Test
    public void toSet()
    {
        Verify.assertSetsEqual(UnifiedSet.newSetWith('a', 'b', 'c', 'd', 'e'), StringIterate.toSet("aabcde"));
    }

    @Test
    public void chunk()
    {
        Assertions.assertEquals(
                Lists.immutable.with("ab", "cd", "ef"),
                StringIterate.chunk("abcdef", 2));

        Assertions.assertEquals(
                Lists.immutable.with("abc", "def"),
                StringIterate.chunk("abcdef", 3));

        Assertions.assertEquals(
                Lists.immutable.with("abc", "def", "g"),
                StringIterate.chunk("abcdefg", 3));

        Assertions.assertEquals(
                Lists.immutable.with("abcdef"),
                StringIterate.chunk("abcdef", 6));

        Assertions.assertEquals(
                Lists.immutable.with("abcdef"),
                StringIterate.chunk("abcdef", 7));

        Assertions.assertEquals(
                Lists.immutable.with(),
                StringIterate.chunk("", 2));
    }

    @Test
    public void chunkWithZeroSize()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            StringIterate.chunk("abcdef", 0);
        });
    }

    @Test
    public void chunkWithNegativeSize()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            StringIterate.chunk("abcdef", -42);
        });
    }

    @Test
    public void toLowercaseSet()
    {
        MutableSet<Character> set = StringIterate.toLowercaseSet("America");
        Assertions.assertEquals(UnifiedSet.newSetWith('a', 'm', 'e', 'r', 'i', 'c'), set);
        Assertions.assertEquals(StringIterate.asLowercaseSet("America"), set);
    }

    @Test
    public void toUppercaseSet()
    {
        MutableSet<Character> set = StringIterate.toUppercaseSet("America");
        Assertions.assertEquals(UnifiedSet.newSetWith('A', 'M', 'E', 'R', 'I', 'C'), set);
        Assertions.assertEquals(StringIterate.asUppercaseSet("America"), set);
    }

    @Test
    public void splitAtIndex()
    {
        String oompaLoompa = "oompaloompa";

        Assertions.assertEquals(Tuples.twin("oompa", "loompa"), StringIterate.splitAtIndex(oompaLoompa, 5));
        Assertions.assertEquals(Tuples.twin("", oompaLoompa), StringIterate.splitAtIndex(oompaLoompa, 0));
        Assertions.assertEquals(Tuples.twin(oompaLoompa, ""), StringIterate.splitAtIndex(oompaLoompa, oompaLoompa.length()));

        Assertions.assertEquals(Tuples.twin("", ""), StringIterate.splitAtIndex("", 0));

        Verify.assertThrows(StringIndexOutOfBoundsException.class, () -> StringIterate.splitAtIndex(oompaLoompa, 17));
        Verify.assertThrows(StringIndexOutOfBoundsException.class, () -> StringIterate.splitAtIndex(oompaLoompa, -8));
    }

    @Test
    public void toLowercaseBag()
    {
        MutableBag<Character> lowercaseBag = StringIterate.toLowercaseBag("America");
        Assertions.assertEquals(2, lowercaseBag.occurrencesOf(Character.valueOf('a')));
        Assertions.assertEquals(1, lowercaseBag.occurrencesOf(Character.valueOf('m')));
        Assertions.assertEquals(1, lowercaseBag.occurrencesOf(Character.valueOf('e')));
        Assertions.assertEquals(1, lowercaseBag.occurrencesOf(Character.valueOf('r')));
        Assertions.assertEquals(1, lowercaseBag.occurrencesOf(Character.valueOf('i')));
        Assertions.assertEquals(1, lowercaseBag.occurrencesOf(Character.valueOf('c')));
    }

    @Test
    public void toUppercaseBag()
    {
        MutableBag<Character> uppercaseBag = StringIterate.toUppercaseBag("America");
        Assertions.assertEquals(2, uppercaseBag.occurrencesOf(Character.valueOf('A')));
        Assertions.assertEquals(1, uppercaseBag.occurrencesOf(Character.valueOf('M')));
        Assertions.assertEquals(1, uppercaseBag.occurrencesOf(Character.valueOf('E')));
        Assertions.assertEquals(1, uppercaseBag.occurrencesOf(Character.valueOf('R')));
        Assertions.assertEquals(1, uppercaseBag.occurrencesOf(Character.valueOf('I')));
        Assertions.assertEquals(1, uppercaseBag.occurrencesOf(Character.valueOf('C')));
    }

    @Test
    public void toBag()
    {
        MutableBag<Character> bag = StringIterate.toBag("America");
        Assertions.assertEquals(1, bag.occurrencesOf(Character.valueOf('A')));
        Assertions.assertEquals(1, bag.occurrencesOf(Character.valueOf('m')));
        Assertions.assertEquals(1, bag.occurrencesOf(Character.valueOf('e')));
        Assertions.assertEquals(1, bag.occurrencesOf(Character.valueOf('r')));
        Assertions.assertEquals(1, bag.occurrencesOf(Character.valueOf('i')));
        Assertions.assertEquals(1, bag.occurrencesOf(Character.valueOf('c')));
        Assertions.assertEquals(1, bag.occurrencesOf(Character.valueOf('a')));
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(StringIterate.class);
    }
}
