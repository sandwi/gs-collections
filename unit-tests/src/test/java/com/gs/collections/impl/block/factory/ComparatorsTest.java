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

package com.gs.collections.impl.block.factory;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.gs.collections.api.block.SerializableComparator;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.primitive.BooleanFunction;
import com.gs.collections.api.block.function.primitive.ByteFunction;
import com.gs.collections.api.block.function.primitive.CharFunction;
import com.gs.collections.api.block.function.primitive.DoubleFunction;
import com.gs.collections.api.block.function.primitive.FloatFunction;
import com.gs.collections.api.block.function.primitive.IntFunction;
import com.gs.collections.api.block.function.primitive.LongFunction;
import com.gs.collections.api.block.function.primitive.ShortFunction;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.test.domain.Person;
import com.gs.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.gs.collections.impl.factory.Iterables.*;

public class ComparatorsTest
{
    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(Comparators.class);
    }

    @Test
    public void naturalOrder()
    {
        MutableList<String> list = FastList.newListWith("1", "4", "2", "3");
        Assertions.assertEquals(
                FastList.newListWith("1", "2", "3", "4"),
                list.sortThis(Comparators.naturalOrder()));
        Verify.assertThrows(NullPointerException.class, () -> FastList.newListWith("1", "2", null, "4").sortThis(Comparators.naturalOrder()));
    }

    @Test
    public void reverseNaturalOrder()
    {
        MutableList<String> list = FastList.newListWith("1", "4", "2", "3");
        Assertions.assertEquals(
                FastList.newListWith("4", "3", "2", "1"),
                list.sortThis(Comparators.<String>reverseNaturalOrder()));
    }

    @Test
    public void reverse()
    {
        MutableList<String> list = FastList.newListWith("1", "4", "2", "3");
        Assertions.assertEquals(
                FastList.newListWith("4", "3", "2", "1"),
                list.sortThis(Comparators.reverse(String::compareTo)));
        Verify.assertThrows(NullPointerException.class, () -> Comparators.reverse(null));
    }

    @Test
    public void safeNullsLow()
    {
        SerializableComparator<Integer> comparator = Comparators.safeNullsLow(Comparators.byFunction(Functions.getIntegerPassThru()));
        Assertions.assertEquals(-1, comparator.compare(null, 1));
        Assertions.assertEquals(1, comparator.compare(1, null));
        Assertions.assertEquals(0, comparator.compare(null, null));
        Assertions.assertEquals(0, comparator.compare(1, 1));
    }

    @Test
    public void byBooleanFunction()
    {
        SerializableComparator<Integer> comparator = Comparators.byBooleanFunction((BooleanFunction<Integer>) anObject -> anObject.intValue() % 2 == 0);
        Verify.assertPositive(comparator.compare(2, 1));
        Verify.assertZero(comparator.compare(1, 1));
        Verify.assertZero(comparator.compare(2, 2));
        Verify.assertNegative(comparator.compare(1, 2));
    }

    @Test
    public void byByteFunction()
    {
        this.assertScalarFunctionParameter(Comparators.byByteFunction((ByteFunction<Integer>) Integer::byteValue));
    }

    @Test
    public void byCharFunction()
    {
        this.assertScalarFunctionParameter(Comparators.byCharFunction((CharFunction<Integer>) anObject -> (char) anObject.intValue()));
    }

    @Test
    public void byDoubleFunction()
    {
        this.assertScalarFunctionParameter(Comparators.byDoubleFunction((DoubleFunction<Integer>) Integer::doubleValue));
    }

    @Test
    public void byFloatFunction()
    {
        this.assertScalarFunctionParameter(Comparators.byFloatFunction((FloatFunction<Integer>) Integer::floatValue));
    }

    @Test
    public void byIntFunction()
    {
        this.assertScalarFunctionParameter(Comparators.byIntFunction((IntFunction<Integer>) Integer::intValue));
    }

    @Test
    public void byLongFunction()
    {
        this.assertScalarFunctionParameter(Comparators.byLongFunction((LongFunction<Integer>) Integer::longValue));
    }

    @Test
    public void byShortFunction()
    {
        this.assertScalarFunctionParameter(Comparators.byShortFunction((ShortFunction<Integer>) Integer::shortValue));
    }

    private void assertScalarFunctionParameter(SerializableComparator<Integer> comparator)
    {
        Verify.assertPositive(comparator.compare(2, 1));
        Verify.assertPositive(comparator.compare(-1, -2));
        Verify.assertZero(comparator.compare(1, 1));
        Verify.assertZero(comparator.compare(0, 0));
        Verify.assertZero(comparator.compare(-1, -1));
        Verify.assertNegative(comparator.compare(3, 5));
        Verify.assertNegative(comparator.compare(-5, -3));
    }

    @Test
    public void safeNullsHigh()
    {
        SerializableComparator<Integer> comparator = Comparators.safeNullsHigh(Comparators.byFunction(Functions.getIntegerPassThru()));
        Assertions.assertEquals(1, comparator.compare(null, 1));
        Assertions.assertEquals(-1, comparator.compare(1, null));
        Assertions.assertEquals(0, comparator.compare(null, null));
        Assertions.assertEquals(0, comparator.compare(1, 1));
    }

    @Test
    public void chainedComparator()
    {
        Verify.assertThrows(IllegalArgumentException.class, (Runnable) Comparators::chain);

        Comparator<Person> byName = Comparators.byFunction(Person.TO_FIRST);
        Comparator<Person> byAge = Comparators.byFunction(Person.TO_AGE);

        Person fred10 = new Person("Fred", "Smith", 10);
        Person jim10 = new Person("Jim", "Smith", 10);
        Person jim16 = new Person("Jim", "Smith", 16);
        Person sheila12 = new Person("Sheila", "Smith", 12);
        Person sheila14 = new Person("Sheila", "Smith", 14);

        MutableList<Person> people = mList(jim16, fred10, sheila14, sheila12, fred10, jim10);

        MutableList<Person> expectedNameThenAgeOrder = mList(fred10, fred10, jim10, jim16, sheila12, sheila14);
        MutableList<Person> expectedAgeThenNameOrder = mList(fred10, fred10, jim10, sheila12, sheila14, jim16);

        Verify.assertListsEqual(expectedNameThenAgeOrder, people.sortThis(Comparators.chain(byName, byAge)));
        Verify.assertListsEqual(expectedAgeThenNameOrder, people.sortThis(Comparators.chain(byAge, byName)));
    }

    @Test
    public void fromFunctions()
    {
        Person raab = new Person(null, "Raab", 0);
        Person white = new Person(null, "White", 0);
        Comparator<Person> personComparator = Comparators.fromFunctions(Person.TO_LAST);
        Verify.assertNegative(personComparator.compare(raab, white));
        Verify.assertPositive(personComparator.compare(white, raab));
        Verify.assertZero(personComparator.compare(raab, raab));
    }

    @Test
    public void fromFunctionsWithTwoArgs()
    {
        Person raab = new Person("Don", "Raab", 0);
        Person white = new Person("Barry", "White", 0);
        Person manilow = new Person("Barry", "Manilow", 0);
        Comparator<Person> personComparator = Comparators.fromFunctions(Person.TO_FIRST, Person.TO_LAST);
        Verify.assertPositive(personComparator.compare(raab, white));
        Verify.assertNegative(personComparator.compare(white, raab));
        Verify.assertZero(personComparator.compare(raab, raab));
        Verify.assertPositive(personComparator.compare(white, manilow));
        Verify.assertNegative(personComparator.compare(manilow, white));
    }

    @Test
    public void fromFunctionsWithThreeArgs()
    {
        Person raab = new Person("Don", "Raab", 21);
        Person white = new Person("Barry", "White", 16);
        Person manilow = new Person("Barry", "Manilow", 60);
        Person manilow2 = new Person("Barry", "Manilow", 61);
        Comparator<Person> personComparator = Comparators.fromFunctions(Person.TO_FIRST, Person.TO_LAST, Person.TO_AGE);
        Verify.assertPositive(personComparator.compare(raab, white));
        Verify.assertNegative(personComparator.compare(white, raab));
        Verify.assertZero(personComparator.compare(raab, raab));
        Verify.assertPositive(personComparator.compare(white, manilow));
        Verify.assertNegative(personComparator.compare(manilow, white));
        Verify.assertNegative(personComparator.compare(manilow, manilow2));
        Verify.assertPositive(personComparator.compare(manilow2, manilow));
    }

    @Test
    public void descendingCollectionSizeCompare()
    {
        MutableList<List<Integer>> list = FastList.<List<Integer>>newListWith(
                Interval.oneTo(1),
                Interval.oneTo(3),
                Interval.oneTo(2));
        list.sortThis(Comparators.descendingCollectionSizeComparator());
        Assertions.assertEquals(
                FastList.<MutableList<Integer>>newListWith(
                        FastList.newListWith(1, 2, 3),
                        FastList.newListWith(1, 2),
                        FastList.newListWith(1)),
                list);
    }

    @Test
    public void ascendingCollectionSizeCompare()
    {
        MutableList<List<Integer>> list = FastList.<List<Integer>>newListWith(
                Interval.oneTo(1),
                Interval.oneTo(3),
                Interval.oneTo(2));
        list.sortThis(Comparators.ascendingCollectionSizeComparator());
        Assertions.assertEquals(
                FastList.<MutableList<Integer>>newListWith(
                        FastList.newListWith(1),
                        FastList.newListWith(1, 2),
                        FastList.newListWith(1, 2, 3)),
                list);
    }

    @Test
    public void nullSafeEquals()
    {
        Assertions.assertTrue(Comparators.nullSafeEquals("Fred", "Fred"));
        Assertions.assertTrue(Comparators.nullSafeEquals(null, null));

        Assertions.assertFalse(Comparators.nullSafeEquals("Fred", "Jim"));
        Assertions.assertFalse(Comparators.nullSafeEquals("Fred", null));
        Assertions.assertFalse(Comparators.nullSafeEquals(null, "Jim"));
    }

    @Test
    public void nullSafeCompare()
    {
        Assertions.assertEquals(0, Comparators.nullSafeCompare(null, null));
        Assertions.assertEquals(0, Comparators.nullSafeCompare("Sheila", "Sheila"));

        Assertions.assertTrue(Comparators.nullSafeCompare("Fred", "Jim") < 0);
        Assertions.assertTrue(Comparators.nullSafeCompare("Sheila", "Jim") > 0);

        Assertions.assertEquals(1, Comparators.nullSafeCompare("Fred", null));
        Assertions.assertEquals(-1, Comparators.nullSafeCompare(null, "Jim"));
    }

    @Test
    public void byFirstOfPair()
    {
        MutableList<Pair<Integer, String>> list = FastList.newListWith(Tuples.pair(3, "B"), Tuples.pair(1, "C"), Tuples.pair(2, "A"));
        MutableList<Pair<Integer, String>> sorted = FastList.newListWith(Tuples.pair(1, "C"), Tuples.pair(2, "A"), Tuples.pair(3, "B"));
        Verify.assertListsEqual(sorted, list.sortThis(Comparators.byFirstOfPair(Integer::compareTo)));
    }

    @Test
    public void bySecondOfPair()
    {
        MutableList<Pair<Integer, String>> list = FastList.newListWith(Tuples.pair(3, "B"), Tuples.pair(1, "C"), Tuples.pair(2, "A"));
        MutableList<Pair<Integer, String>> sorted = FastList.newListWith(Tuples.pair(2, "A"), Tuples.pair(3, "B"), Tuples.pair(1, "C"));
        Verify.assertListsEqual(sorted, list.sortThis(Comparators.bySecondOfPair(String::compareTo)));
    }

    @Test
    public void specializedComparator()
    {
        OneOfEach february = new OneOfEach(Timestamp.valueOf("2004-02-12 22:20:30"));
        OneOfEach april = new OneOfEach(Timestamp.valueOf("2004-04-12 22:20:30"));
        OneOfEach december = new OneOfEach(Timestamp.valueOf("2004-12-12 22:20:30"));

        Comparator<OneOfEach> comparator = Comparators.byFunction(OneOfEach.TO_DATE_VALUE, new FancyDateComparator());

        Assertions.assertEquals(
                iList(april, december, february),
                iList(february, april, december).toSortedList(comparator));
    }

    public static class OneOfEach
    {
        public static final Function<OneOfEach, Date> TO_DATE_VALUE = oneOfEach -> new Date(oneOfEach.dateValue.getTime());
        private Date dateValue = Timestamp.valueOf("2004-12-12 22:20:30");

        public OneOfEach(Date dateValue)
        {
            this.dateValue = new Date(dateValue.getTime());
        }
    }

    public static class FancyDateComparator implements Comparator<Date>, Serializable
    {
        private static final String FANCY_DATE_FORMAT = "EEE, MMM d, ''yy";
        private static final long serialVersionUID = 1L;

        private final DateFormat formatter = new SimpleDateFormat(FANCY_DATE_FORMAT);

        @Override
        public int compare(Date o1, Date o2)
        {
            String date1 = this.formatter.format(o1);
            String date2 = this.formatter.format(o2);
            return date1.compareTo(date2);
        }
    }
}
