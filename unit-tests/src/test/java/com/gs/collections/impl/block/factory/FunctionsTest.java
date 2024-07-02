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

package com.gs.collections.impl.block.factory;

import java.io.IOException;
import java.util.Map;

import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.primitive.DoubleFunction;
import com.gs.collections.api.block.function.primitive.IntFunction;
import com.gs.collections.api.block.function.primitive.LongFunction;
import com.gs.collections.api.block.function.primitive.ShortFunction;
import com.gs.collections.api.collection.MutableCollection;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.function.primitive.IntegerFunctionImpl;
import com.gs.collections.impl.block.function.primitive.LongFunctionImpl;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.list.Interval;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.set.mutable.SetAdapter;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import com.gs.collections.impl.test.domain.Person;
import com.gs.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.gs.collections.impl.factory.Iterables.iList;

public class FunctionsTest
{
    private static final Function<String, Integer> STRING_LENGTH = String::length;

    private static final Function<Integer, Boolean> IS_ODD = object -> Boolean.valueOf(object.intValue() % 2 != 0);

    private static final Function<Boolean, String> BOOLEAN_STRING = String::valueOf;

    @Test
    public void throwing()
    {
        Verify.assertThrowsWithCause(
                RuntimeException.class,
                IOException.class,
                () -> Functions.throwing(a -> { throw new IOException(); }).valueOf(null));
    }

    @Test
    public void getPassThru()
    {
        Object object = new Object();
        Assertions.assertSame(object, Functions.getPassThru().valueOf(object));
    }

    @Test
    public void getFixedValue()
    {
        Assertions.assertEquals(Integer.valueOf(5), Functions.getFixedValue(5).valueOf(null));
    }

    @Test
    public void getToClass()
    {
        Assertions.assertSame(Integer.class, Functions.getToClass().valueOf(0));
    }

    @Test
    public void getMathSinFunction()
    {
        Function<Number, Double> function = Functions.getMathSinFunction();
        Assertions.assertEquals(Math.sin(1.0), function.valueOf(1), 0.0);
    }

    @Test
    public void getNumberPassThru()
    {
        Function<Number, Number> function = Functions.getNumberPassThru();
        Assertions.assertEquals(1, function.valueOf(1));
    }

    @Test
    public void getIntegerPassThru()
    {
        Function<Integer, Integer> function = Functions.getIntegerPassThru();
        Assertions.assertEquals(Integer.valueOf(1), function.valueOf(1));
        Assertions.assertEquals("IntegerPassThruFunction", function.toString());
    }

    @Test
    public void getLongPassThru()
    {
        Function<Long, Long> function = Functions.getLongPassThru();
        Assertions.assertEquals(Long.valueOf(1), function.valueOf(1L));
        Assertions.assertEquals(Long.valueOf(1L), Long.valueOf(((LongFunction<Long>) function).longValueOf(1L)));
        Assertions.assertEquals("LongPassThruFunction", function.toString());
    }

    @Test
    public void getDoublePassThru()
    {
        Function<Double, Double> function = Functions.getDoublePassThru();
        Assertions.assertEquals(Double.valueOf(1).doubleValue(), function.valueOf(1.0).doubleValue(), 0.0);
        Assertions.assertEquals(Double.valueOf(1).doubleValue(), ((DoubleFunction<Double>) function).doubleValueOf(1.0), 0.0);
        Assertions.assertEquals("DoublePassThruFunction", function.toString());
    }

    @Test
    public void getStringPassThru()
    {
        Function<String, String> function = Functions.getStringPassThru();
        Assertions.assertEquals("hello", function.valueOf("hello"));
    }

    @Test
    public void getStringTrim()
    {
        Assertions.assertEquals("hello", Functions.getStringTrim().valueOf(" hello  "));
    }

    @Test
    public void getToString()
    {
        Function<Object, String> function = Functions.getToString();
        Assertions.assertEquals("1", function.valueOf(1));
        Assertions.assertEquals("null", function.valueOf(null));
    }

    @Test
    public void getDefaultToString()
    {
        Function<Object, String> function = Functions.getNullSafeToString("N/A");
        Assertions.assertEquals("1", function.valueOf(1));
        Assertions.assertEquals("N/A", function.valueOf(null));
    }

    @Test
    public void getStringToInteger()
    {
        Function<String, Integer> function = Functions.getStringToInteger();
        Assertions.assertEquals(Integer.valueOf(1), function.valueOf("1"));
    }

    @Test
    public void firstNotNullValue()
    {
        Function<Object, Integer> function1 =
                Functions.firstNotNullValue(Functions.<Object, Integer>getFixedValue(null), Functions.getFixedValue(1), Functions.getFixedValue(2));
        Assertions.assertEquals(Integer.valueOf(1), function1.valueOf(null));
        Function<Object, Integer> function2 =
                Functions.firstNotNullValue(Functions.<Object, Integer>getFixedValue(null), Functions.getFixedValue(null));
        Assertions.assertNull(function2.valueOf(null));
    }

    @Test
    public void firstNotEmptyStringValue()
    {
        Function<Object, String> function1 =
                Functions.firstNotEmptyStringValue(Functions.getFixedValue(""), Functions.getFixedValue("hello"), Functions.getFixedValue(""));
        Assertions.assertEquals("hello", function1.valueOf(null));
        Function<Object, String> function2 =
                Functions.firstNotEmptyStringValue(Functions.getFixedValue(""), Functions.getFixedValue(""));
        Assertions.assertNull(function2.valueOf(null));
    }

    @Test
    public void firstNotEmptyCollectionValue()
    {
        Function<Object, ImmutableList<String>> function1 = Functions.firstNotEmptyCollectionValue(
                Functions.getFixedValue(Lists.immutable.<String>of()),
                Functions.getFixedValue(Lists.immutable.of("hello")),
                Functions.getFixedValue(Lists.immutable.<String>of()));
        Assertions.assertEquals(iList("hello"), function1.valueOf(null));

        Function<Object, ImmutableList<String>> function2 = Functions.firstNotEmptyCollectionValue(
                Functions.getFixedValue(Lists.immutable.<String>of()),
                Functions.getFixedValue(Lists.immutable.<String>of()));
        Assertions.assertNull(function2.valueOf(null));
    }

    @Test
    public void ifTrue()
    {
        String result = "1";
        Assertions.assertSame(result, Functions.ifTrue(Predicates.alwaysTrue(), Functions.getPassThru()).valueOf(result));
        Assertions.assertNull(Functions.ifTrue(Predicates.alwaysFalse(), Functions.getPassThru()).valueOf(result), result);
    }

    @Test
    public void ifElse()
    {
        String result1 = "1";
        String result2 = "2";
        Assertions.assertSame(result1, Functions.ifElse(Predicates.alwaysTrue(), Functions.getFixedValue(result1), Functions.getFixedValue(result2)).valueOf(null));
        Assertions.assertSame(result2, Functions.ifElse(Predicates.alwaysFalse(), Functions.getFixedValue(result1), Functions.getFixedValue(result2)).valueOf(null));
        Verify.assertContains(Functions.ifElse(Predicates.alwaysTrue(), Functions.getFixedValue(result1), Functions.getFixedValue(result2)).toString(), "IfFunction");
    }

    @Test
    public void synchronizedEach()
    {
        Function<Integer, String> function = Functions.synchronizedEach(Object::toString);
        Verify.assertSetsEqual(
                UnifiedSet.newSetWith("1", "2", "3"),
                UnifiedSet.newSetWith(1, 2, 3).collect(function));
    }

    @Test
    public void chains()
    {
        Function<String, Integer> toInteger = Functions.getStringToInteger();
        Function<Object, String> toString = String::valueOf;

        Assertions.assertEquals("42", Functions.chain(toInteger, toString).valueOf("42"));
        Assertions.assertEquals(Integer.valueOf(42), Functions.chain(toString, toInteger).valueOf(42));

        Function<String, Integer> chain = Functions.chain(toInteger, toString).chain(toInteger);
        Assertions.assertEquals(Integer.valueOf(42), chain.valueOf("42"));
        Assertions.assertEquals("42", Functions.chain(toString, toInteger).chain(toString).valueOf(42));

        Assertions.assertEquals("42", Functions.chain(toInteger, toString).chain(toInteger).chain(toString).valueOf("42"));
        Assertions.assertEquals(Integer.valueOf(42), Functions.chain(toString, toInteger).chain(toString).chain(toInteger).valueOf(42));

        Assertions.assertEquals(Integer.valueOf(42), Functions.chain(toInteger, toString).chain(toInteger).chain(toString).chain(toInteger).valueOf("42"));
        Assertions.assertEquals(Integer.valueOf(42), Functions.chain(toString, toInteger).chain(toString).chain(toInteger).chain(toString).chain(toInteger).valueOf(42));
    }

    @Test
    public void chain_two()
    {
        Function<Boolean, Integer> chain = Functions.chain(BOOLEAN_STRING, STRING_LENGTH);
        Assertions.assertEquals(Integer.valueOf(5), chain.valueOf(Boolean.FALSE));
    }

    @Test
    public void chain_three()
    {
        Function<String, String> chain = Functions.chain(STRING_LENGTH, IS_ODD).chain(BOOLEAN_STRING);
        Assertions.assertEquals("true", chain.valueOf("foo"));
    }

    @Test
    public void chain_four()
    {
        Function<Integer, Boolean> chain = Functions.chain(IS_ODD, BOOLEAN_STRING).chain(STRING_LENGTH).chain(IS_ODD);
        Assertions.assertEquals(Boolean.TRUE, chain.valueOf(Integer.valueOf(4)));
    }

    @Test
    public void chainBoolean()
    {
        Function<String, Integer> toInteger = Functions.getStringToInteger();
        Functions.BooleanFunctionChain<String, Integer> booleanFunctionChain = Functions.chainBoolean(toInteger, integerObject -> integerObject.intValue() >= 0);
        Assertions.assertTrue(booleanFunctionChain.booleanValueOf("45"));
        Assertions.assertFalse(booleanFunctionChain.booleanValueOf("-45"));
    }

    @Test
    public void chainByte()
    {
        Function<String, Integer> toInteger = Functions.getStringToInteger();
        Functions.ByteFunctionChain<String, Integer> byteFunctionChain = Functions.chainByte(toInteger, Integer::byteValue);
        Assertions.assertEquals((byte) 45, byteFunctionChain.byteValueOf("45"));
        Assertions.assertEquals((byte) -45, byteFunctionChain.byteValueOf("-45"));
    }

    @Test
    public void chainChar()
    {
        Function<Object, String> toString = String::valueOf;
        Functions.CharFunctionChain<Object, String> charFunctionChain = Functions.chainChar(toString, stringObject -> stringObject.charAt(0));
        Assertions.assertEquals('g', charFunctionChain.charValueOf("gscollections"));
        Assertions.assertEquals('-', charFunctionChain.charValueOf("-4"));
    }

    @Test
    public void chainDouble()
    {
        Function<String, Integer> toInteger = Functions.getStringToInteger();
        Functions.DoubleFunctionChain<String, Integer> doubleFunctionChain = Functions.chainDouble(toInteger, Integer::doubleValue);
        Assertions.assertEquals(146.0, doubleFunctionChain.doubleValueOf("146"), 0.0);
        Assertions.assertEquals(-456.0, doubleFunctionChain.doubleValueOf("-456"), 0.0);
    }

    @Test
    public void chainFloat()
    {
        Functions.FloatFunctionChain<Integer, String> floatFunctionChain = Functions.chainFloat(String::valueOf, stringObject -> Float.valueOf(stringObject).floatValue());
        Assertions.assertEquals(146.0, floatFunctionChain.floatValueOf(146), 0.0);
        Assertions.assertEquals(-456.0, floatFunctionChain.floatValueOf(-456), 0.0);
    }

    @Test
    public void chainInt()
    {
        Function<Float, String> toString = String::valueOf;

        IntFunction<String> stringToLength = new IntegerFunctionImpl<String>()
        {
            public int intValueOf(String stringObject)
            {
                return stringObject.length();
            }
        };
        Functions.IntFunctionChain<Float, String> intFunctionChain = Functions.chainInt(toString, stringToLength);
        Assertions.assertEquals(5, intFunctionChain.intValueOf(Float.valueOf(145)));
        Assertions.assertEquals(6, intFunctionChain.intValueOf(Float.valueOf(-145)));
    }

    @Test
    public void chainLong()
    {
        Function<Float, String> toString = String::valueOf;

        LongFunction<String> stringToLengthLong = stringObject -> Long.valueOf(stringObject.length()).longValue();
        Functions.LongFunctionChain<Float, String> longFunctionChain = Functions.chainLong(toString, stringToLengthLong);
        Assertions.assertEquals(5L, longFunctionChain.longValueOf(Float.valueOf(145)));
        Assertions.assertEquals(6L, longFunctionChain.longValueOf(Float.valueOf(-145)));
    }

    @Test
    public void chainShort()
    {
        Functions.ShortFunctionChain<Integer, String> shortFunctionChain = Functions.chainShort(String::valueOf, stringObject -> Short.valueOf(stringObject).shortValue());
        Assertions.assertEquals((short) 145, shortFunctionChain.shortValueOf(145));
        Assertions.assertEquals((short) -145, shortFunctionChain.shortValueOf(-145));
    }

    @Test
    public void chain_two_chainBoolean()
    {
        Functions.FunctionChain<Boolean, String, Integer> chain = Functions.chain(String::valueOf, STRING_LENGTH);
        Functions.BooleanFunctionChain<Boolean, Integer> booleanChain = chain.chainBoolean(integerObject -> integerObject.intValue() >= 0);
        Assertions.assertTrue(booleanChain.booleanValueOf(Boolean.TRUE));
    }

    @Test
    public void chain_two_chainByte()
    {
        Functions.FunctionChain<Boolean, String, Integer> chain = Functions.chain(String::valueOf, STRING_LENGTH);
        Functions.ByteFunctionChain<Boolean, Integer> byteChain = chain.chainByte(Integer::byteValue);
        Assertions.assertEquals((byte) 5, byteChain.byteValueOf(Boolean.FALSE));
    }

    @Test
    public void chain_three_chainChar()
    {
        Functions.FunctionChain<String, Boolean, String> chain = Functions.chain(STRING_LENGTH, IS_ODD).chain(BOOLEAN_STRING);
        Functions.CharFunctionChain<String, String> charChain = chain.chainChar(stringObject -> stringObject.charAt(0));
        Assertions.assertEquals('t', charChain.charValueOf("foo"));
    }

    @Test
    public void chain_three_chainDouble()
    {
        Functions.FunctionChain<Boolean, String, Integer> chain = Functions.chain(String::valueOf, STRING_LENGTH);
        Functions.DoubleFunctionChain<Boolean, Integer> doubleChain = chain.chainDouble(Integer::doubleValue);
        Assertions.assertEquals(4.0, doubleChain.doubleValueOf(Boolean.TRUE), 0.0);
    }

    @Test
    public void chain_three_chainFloat()
    {
        Functions.FunctionChain<String, Boolean, String> chain = Functions.chain(STRING_LENGTH, IS_ODD).chain(BOOLEAN_STRING);
        Functions.FloatFunctionChain<String, String> floatChain = chain.chainFloat(stringObject -> Integer.valueOf(stringObject.length()).floatValue());
        Assertions.assertEquals(5.0, floatChain.floatValueOf("12.2"), 0);
    }

    @Test
    public void chain_three_chainInt()
    {
        Functions.FunctionChain<String, Boolean, String> chain = Functions.chain(STRING_LENGTH, IS_ODD).chain(BOOLEAN_STRING);
        IntFunction<String> stringToLength = new IntegerFunctionImpl<String>()
        {
            public int intValueOf(String stringObject)
            {
                return stringObject.length();
            }
        };
        Functions.IntFunctionChain<String, String> intChain = chain.chainInt(stringToLength);
        Assertions.assertEquals(4, intChain.intValueOf("gsc"));
        Assertions.assertNotEquals(4, intChain.intValueOf("kata"));
    }

    @Test
    public void chain_three_chainLong()
    {
        Functions.FunctionChain<String, Boolean, String> chain = Functions.chain(STRING_LENGTH, IS_ODD).chain(BOOLEAN_STRING);
        LongFunction<String> stringToLengthLong = stringObject -> Long.valueOf(stringObject.length()).longValue();
        Functions.LongFunctionChain<String, String> longChain = chain.chainLong(stringToLengthLong);
        Assertions.assertEquals(4L, longChain.longValueOf("gsc"));
        Assertions.assertNotEquals(4L, longChain.longValueOf("kata"));
    }

    @Test
    public void chain_three_chainShort()
    {
        Functions.FunctionChain<String, Boolean, String> chain = Functions.chain(STRING_LENGTH, IS_ODD).chain(BOOLEAN_STRING);
        ShortFunction<String> stringToShort = stringObject -> Integer.valueOf(stringObject.length()).shortValue();
        Functions.ShortFunctionChain<String, String> shortChain = chain.chainShort(stringToShort);
        Assertions.assertEquals((short) 4, shortChain.shortValueOf("gsc"));
        Assertions.assertNotEquals((short) 4, shortChain.shortValueOf("kata"));
    }

    @Test
    public void intValueFunctionToComparator()
    {
        MutableList<Integer> list = Interval.oneTo(100).toList().shuffleThis();
        Function<Integer, Integer> function = Integer::intValue;
        list.sortThis(Comparators.byFunction(function));
        Assertions.assertEquals(Interval.oneTo(100).toList(), list);
    }

    @Test
    public void doubleValueFunctionToComparator()
    {
        MutableList<Double> list = FastList.newListWith(5.0, 4.0, 3.0, 2.0, 1.0).shuffleThis();
        Function<Double, Double> function = Double::doubleValue;
        list.sortThis(Comparators.byFunction(function));
        Assertions.assertEquals(FastList.newListWith(1.0, 2.0, 3.0, 4.0, 5.0), list);
    }

    @Test
    public void longValueFunctionToComparator()
    {
        MutableList<Long> list = FastList.newListWith(5L, 4L, 3L, 2L, 1L).shuffleThis();
        list.sortThis(Comparators.byFunction(new LongFunctionImpl<Long>()
        {
            public long longValueOf(Long each)
            {
                return each.longValue();
            }
        }));
        Assertions.assertEquals(FastList.newListWith(1L, 2L, 3L, 4L, 5L), list);
    }

    @Test
    public void classFunctionToString()
    {
        Assertions.assertEquals("object.getClass()", Functions.getToClass().toString());
    }

    @Test
    public void mathSinToString()
    {
        Assertions.assertEquals("Math.sin()", Functions.getMathSinFunction().toString());
    }

    @Test
    public void mathStringToIntegerToString()
    {
        Assertions.assertEquals("stringToInteger", Functions.getStringToInteger().toString());
    }


    @Test
    public void pair()
    {
        Person john = new Person("John", "Smith");
        Person jane = new Person("Jane", "Smith");
        Person johnDoe = new Person("John", "Doe");
        MutableList<Person> people = FastList.newListWith(john, jane, johnDoe);
        MutableList<Person> sorted = people.sortThisBy(Functions.pair(Person.TO_LAST, Person.TO_FIRST));
        Assertions.assertEquals(FastList.newListWith(johnDoe, jane, john), sorted);
    }

    @Test
    public void key()
    {
        MutableMap<String, Integer> map = UnifiedMap.newWithKeysValues("One", 1);
        MutableSet<Map.Entry<String, Integer>> entries = SetAdapter.adapt(map.entrySet());
        MutableSet<String> keys = entries.collect(Functions.<String>getKeyFunction());
        Assertions.assertEquals(UnifiedSet.newSetWith("One"), keys);
    }

    @Test
    public void value()
    {
        MutableMap<String, Integer> map = UnifiedMap.newWithKeysValues("One", 1);
        MutableSet<Map.Entry<String, Integer>> entries = SetAdapter.adapt(map.entrySet());
        MutableSet<Integer> values = entries.collect(Functions.<Integer>getValueFunction());
        Assertions.assertEquals(UnifiedSet.newSetWith(1), values);
    }

    @Test
    public void size()
    {
        ImmutableList<ImmutableList<Integer>> list = Lists.immutable.of(Lists.immutable.of(1), Lists.immutable.of(1, 2), Lists.immutable.of(1, 2, 3));
        ImmutableList<Integer> sizes = list.collect(Functions.getSizeOf());
        Assertions.assertEquals(FastList.newListWith(1, 2, 3), sizes);
    }

    @Test
    public void squaredCollection()
    {
        MutableCollection<Integer> squareCollection = FastList.newListWith(1, 2, 3, 4, 5).collect(Functions.squaredInteger());
        Verify.assertContainsAll(squareCollection, 1, 4, 9, 16, 25);
    }

    @Test
    public void withDefault()
    {
        Object expected = new Object();
        Assertions.assertSame(expected, Functions.withDefault(Functions.getFixedValue(null), expected).valueOf(new Object()));

        Object expected2 = new Object();
        Assertions.assertSame(expected2, Functions.withDefault(Functions.getFixedValue(expected2), expected).valueOf(new Object()));
    }

    @Test
    public void nullSafe()
    {
        Object expected = new Object();
        Function<Object, Object> throwsFunction = new ThrowsFunction();
        Assertions.assertSame(expected, Functions.nullSafe(throwsFunction, expected).valueOf(null));
        Assertions.assertSame(expected, Functions.nullSafe(Functions.getFixedValue(expected)).valueOf(new Object()));
        Assertions.assertNull(Functions.nullSafe(throwsFunction).valueOf(null));
    }

    @Test
    public void classForName()
    {
        Class<?> objectClass = Functions.classForName().valueOf("java.lang.Object");
        Assertions.assertSame(Object.class, objectClass);
    }

    @Test
    public void bind_function2_parameter()
    {
        MutableCollection<Integer> multiplied = FastList.newListWith(1, 2, 3, 4, 5).collect(Functions.bind((value, parameter) -> value * parameter, 2));
        Verify.assertContainsAll(multiplied, 2, 4, 6, 8, 10);
    }

    @Test
    public void swappedPair()
    {
        Pair<Integer, String> pair1 = Tuples.pair(1, "One");
        Pair<Integer, String> pair2 = Tuples.pair(2, "Two");
        Pair<Integer, String> pair3 = Tuples.pair(3, "Three");
        Pair<Integer, String> pair4 = Tuples.pair(4, "Four");

        MutableList<Pair<Integer, String>> testList = FastList.<Pair<Integer, String>>newListWith(pair1, pair2, pair3, pair4);
        MutableList<Pair<String, Integer>> actual = testList.collect(Functions.swappedPair());

        MutableList<Pair<String, Integer>> expected = FastList.<Pair<String, Integer>>newListWith(Tuples.pair("One", 1), Tuples.pair("Two", 2), Tuples.pair("Three", 3), Tuples.pair("Four", 4));

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void getTrue()
    {
        Assertions.assertTrue(Functions.getTrue().valueOf(false));
    }

    @Test
    public void getFalse()
    {
        Assertions.assertFalse(Functions.getFalse().valueOf(true));
    }

    private static class ThrowsFunction implements Function<Object, Object>
    {
        @Override
        public Object valueOf(Object object)
        {
            throw new RuntimeException();
        }
    }

    @Test
    public void classIsNonInstantiable()
    {
        Verify.assertClassNonInstantiable(Functions.class);
    }
}
