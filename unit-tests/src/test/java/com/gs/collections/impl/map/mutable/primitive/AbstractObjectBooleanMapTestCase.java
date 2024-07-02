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

package com.gs.collections.impl.map.mutable.primitive;

import java.util.Arrays;
import java.util.NoSuchElementException;

import com.gs.collections.api.iterator.BooleanIterator;
import com.gs.collections.api.map.primitive.ImmutableObjectBooleanMap;
import com.gs.collections.api.map.primitive.ObjectBooleanMap;
import com.gs.collections.impl.bag.mutable.primitive.BooleanHashBag;
import com.gs.collections.impl.block.factory.primitive.BooleanPredicates;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.set.mutable.primitive.BooleanHashSet;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public abstract class AbstractObjectBooleanMapTestCase
{
    protected abstract ObjectBooleanMap<String> classUnderTest();

    protected abstract <T> ObjectBooleanMap<T> newWithKeysValues(T key1, boolean value1);

    protected abstract <T> ObjectBooleanMap<T> newWithKeysValues(T key1, boolean value1, T key2, boolean value2);

    protected abstract <T> ObjectBooleanMap<T> newWithKeysValues(T key1, boolean value1, T key2, boolean value2, T key3, boolean value3);

    protected abstract <T> ObjectBooleanMap<T> newWithKeysValues(T key1, boolean value1, T key2, boolean value2, T key3, boolean value3, T key4, boolean value4);

    protected abstract <T> ObjectBooleanMap<T> getEmptyMap();

    @Test
    public void get()
    {
        Assertions.assertTrue(this.classUnderTest().get("0"));
        Assertions.assertTrue(this.classUnderTest().get("1"));
        Assertions.assertFalse(this.classUnderTest().get("2"));

        Assertions.assertFalse(this.classUnderTest().get("5"));
    }

    @Test
    public void getIfAbsent()
    {
        Assertions.assertTrue(this.classUnderTest().getIfAbsent("0", false));
        Assertions.assertTrue(this.classUnderTest().getIfAbsent("1", false));
        Assertions.assertFalse(this.classUnderTest().getIfAbsent("2", true));

        Assertions.assertTrue(this.classUnderTest().getIfAbsent("5", true));
        Assertions.assertFalse(this.classUnderTest().getIfAbsent("5", false));

        Assertions.assertTrue(this.classUnderTest().getIfAbsent(null, true));
        Assertions.assertFalse(this.classUnderTest().getIfAbsent(null, false));
    }

    @Test
    public void getOrThrow()
    {
        Assertions.assertTrue(this.classUnderTest().getOrThrow("0"));
        Assertions.assertTrue(this.classUnderTest().getOrThrow("1"));
        Assertions.assertFalse(this.classUnderTest().getOrThrow("2"));

        Verify.assertThrows(IllegalStateException.class, () -> this.classUnderTest().getOrThrow("5"));
        Verify.assertThrows(IllegalStateException.class, () -> this.classUnderTest().getOrThrow(null));
    }

    @Test
    public void containsKey()
    {
        Assertions.assertTrue(this.classUnderTest().containsKey("0"));
        Assertions.assertTrue(this.classUnderTest().containsKey("1"));
        Assertions.assertTrue(this.classUnderTest().containsKey("2"));
        Assertions.assertFalse(this.classUnderTest().containsKey("3"));
        Assertions.assertFalse(this.classUnderTest().containsKey(null));
    }

    @Test
    public void containsValue()
    {
        Assertions.assertTrue(this.classUnderTest().containsValue(true));
        Assertions.assertTrue(this.classUnderTest().containsValue(false));
    }

    @Test
    public void size()
    {
        Verify.assertEmpty(this.getEmptyMap());
        Verify.assertSize(1, this.newWithKeysValues(0, false));
        Verify.assertSize(1, this.newWithKeysValues(1, true));
        Verify.assertSize(1, this.newWithKeysValues(null, false));

        Verify.assertSize(2, this.newWithKeysValues(1, false, 5, false));
        Verify.assertSize(2, this.newWithKeysValues(0, true, 5, true));
    }

    @Test
    public void isEmpty()
    {
        Verify.assertEmpty(this.getEmptyMap());
        Assertions.assertFalse(this.classUnderTest().isEmpty());
        Assertions.assertFalse(this.newWithKeysValues(null, false).isEmpty());
        Assertions.assertFalse(this.newWithKeysValues(1, true).isEmpty());
        Assertions.assertFalse(this.newWithKeysValues(0, false).isEmpty());
        Assertions.assertFalse(this.newWithKeysValues(50, true).isEmpty());
    }

    @Test
    public void notEmpty()
    {
        Assertions.assertFalse(this.getEmptyMap().notEmpty());
        Assertions.assertTrue(this.classUnderTest().notEmpty());
        Assertions.assertTrue(this.newWithKeysValues(1, true).notEmpty());
        Assertions.assertTrue(this.newWithKeysValues(null, false).notEmpty());
        Assertions.assertTrue(this.newWithKeysValues(0, true).notEmpty());
        Assertions.assertTrue(this.newWithKeysValues(50, false).notEmpty());
    }

    @Test
    public void testEquals()
    {
        ObjectBooleanMap<Integer> map1 = this.newWithKeysValues(0, true, 1, false, null, false);
        ObjectBooleanMap<Integer> map2 = this.newWithKeysValues(null, false, 0, true, 1, false);
        ObjectBooleanMap<Integer> map3 = this.newWithKeysValues(0, true, 1, true, null, false);
        ObjectBooleanMap<Integer> map4 = this.newWithKeysValues(0, false, 1, false, null, false);
        ObjectBooleanMap<Integer> map5 = this.newWithKeysValues(0, true, 1, false, null, true);
        ObjectBooleanMap<Integer> map6 = this.newWithKeysValues(null, true, 60, false, 70, true);
        ObjectBooleanMap<Integer> map7 = this.newWithKeysValues(null, true, 60, false);
        ObjectBooleanMap<Integer> map8 = this.newWithKeysValues(0, true, 1, false);

        Verify.assertEqualsAndHashCode(map1, map2);
        Verify.assertPostSerializedEqualsAndHashCode(map1);
        Assertions.assertNotEquals(map1, map3);
        Assertions.assertNotEquals(map1, map4);
        Assertions.assertNotEquals(map1, map5);
        Assertions.assertNotEquals(map7, map6);
        Assertions.assertNotEquals(map7, map8);
    }

    @Test
    public void testHashCode()
    {
        Assertions.assertEquals(
                UnifiedMap.newWithKeysValues(0, false, 1, true, 32, true).hashCode(),
                this.newWithKeysValues(32, true, 0, false, 1, true).hashCode());
        Assertions.assertEquals(
                UnifiedMap.newWithKeysValues(50, true, 60, true, null, false).hashCode(),
                this.newWithKeysValues(50, true, 60, true, null, false).hashCode());
        Assertions.assertEquals(UnifiedMap.newMap().hashCode(), this.getEmptyMap().hashCode());
    }

    @Test
    public void testToString()
    {
        Assertions.assertEquals("{}", this.getEmptyMap().toString());
        Assertions.assertEquals("{0=false}", this.newWithKeysValues(0, false).toString());
        Assertions.assertEquals("{1=true}", this.newWithKeysValues(1, true).toString());
        Assertions.assertEquals("{5=true}", this.newWithKeysValues(5, true).toString());

        ObjectBooleanMap<Integer> map1 = this.newWithKeysValues(0, true, 1, false);
        Assertions.assertTrue(
                "{0=true, 1=false}".equals(map1.toString())
                        || "{1=false, 0=true}".equals(map1.toString()),
                map1.toString());

        ObjectBooleanMap<Integer> map2 = this.newWithKeysValues(1, false, null, true);
        Assertions.assertTrue(
                "{1=false, null=true}".equals(map2.toString())
                        || "{null=true, 1=false}".equals(map2.toString()),
                map2.toString());

        ObjectBooleanMap<Integer> map3 = this.newWithKeysValues(1, true, null, true);
        Assertions.assertTrue(
                "{1=true, null=true}".equals(map3.toString())
                        || "{null=true, 1=true}".equals(map3.toString()),
                map3.toString());
    }

    @Test
    public void forEachValue()
    {
        ObjectBooleanMap<Integer> map01 = this.newWithKeysValues(0, true, 1, false);
        String[] sum01 = new String[1];
        sum01[0] = "";
        map01.forEachValue(each -> sum01[0] += String.valueOf(each));
        Assertions.assertTrue("truefalse".equals(sum01[0]) || "falsetrue".equals(sum01[0]));

        ObjectBooleanMap<Integer> map = this.newWithKeysValues(3, true, 4, true);
        String[] sum = new String[1];
        sum[0] = "";
        map.forEachValue(each -> sum[0] += String.valueOf(each));
        Assertions.assertEquals("truetrue", sum[0]);

        ObjectBooleanMap<Integer> map1 = this.newWithKeysValues(3, false, null, true);
        String[] sum1 = new String[1];
        sum1[0] = "";
        map1.forEachValue(each -> sum1[0] += String.valueOf(each));
        Assertions.assertTrue("truefalse".equals(sum1[0]) || "falsetrue".equals(sum1[0]));
    }

    @Test
    public void forEach()
    {
        ObjectBooleanMap<Integer> map01 = this.newWithKeysValues(0, true, 1, false);
        String[] sum01 = new String[1];
        sum01[0] = "";
        map01.forEach(each -> sum01[0] += String.valueOf(each));
        Assertions.assertTrue("truefalse".equals(sum01[0]) || "falsetrue".equals(sum01[0]));

        ObjectBooleanMap<Integer> map = this.newWithKeysValues(3, true, 4, true);
        String[] sum = new String[1];
        sum[0] = "";
        map.forEach(each -> sum[0] += String.valueOf(each));
        Assertions.assertEquals("truetrue", sum[0]);

        ObjectBooleanMap<Integer> map1 = this.newWithKeysValues(3, false, null, true);
        String[] sum1 = new String[1];
        sum1[0] = "";
        map1.forEach(each -> sum1[0] += String.valueOf(each));
        Assertions.assertTrue("truefalse".equals(sum1[0]) || "falsetrue".equals(sum1[0]));
    }

    @Test
    public void forEachKey()
    {
        ObjectBooleanMap<Integer> map01 = this.newWithKeysValues(0, true, 1, false);
        int[] sum01 = new int[1];
        map01.forEachKey(each -> sum01[0] += each);
        Assertions.assertEquals(1, sum01[0]);

        ObjectBooleanMap<Integer> map = this.newWithKeysValues(3, false, null, true);
        String[] sum = new String[1];
        sum[0] = "";
        map.forEachKey(each -> sum[0] += String.valueOf(each));
        Assertions.assertTrue("3null".equals(sum[0]) || "null3".equals(sum[0]));
    }

    @Test
    public void forEachKeyValue()
    {
        ObjectBooleanMap<Integer> map01 = this.newWithKeysValues(0, true, 1, false);
        String[] sumValue01 = new String[1];
        sumValue01[0] = "";
        int[] sumKey01 = new int[1];
        map01.forEachKeyValue((eachKey, eachValue) -> {
            sumKey01[0] += eachKey;
            sumValue01[0] += eachValue;
        });
        Assertions.assertEquals(1, sumKey01[0]);
        Assertions.assertTrue("truefalse".equals(sumValue01[0]) || "falsetrue".equals(sumValue01[0]));

        ObjectBooleanMap<Integer> map = this.newWithKeysValues(3, true, null, false);
        String[] sumKey = new String[1];
        sumKey[0] = "";
        String[] sumValue = new String[1];
        sumValue[0] = "";
        map.forEachKeyValue((eachKey, eachValue) -> {
            sumKey[0] += String.valueOf(eachKey);
            sumValue[0] += eachValue;
        });
        Assertions.assertTrue("3null".equals(sumKey[0]) || "null3".equals(sumKey[0]), sumKey[0]);
        Assertions.assertTrue("truefalse".equals(sumValue[0]) || "falsetrue".equals(sumValue[0]));
    }

    @Test
    public void makeString()
    {
        Assertions.assertEquals("", this.<String>getEmptyMap().makeString());
        Assertions.assertEquals("true", this.newWithKeysValues(0, true).makeString());
        Assertions.assertEquals("false", this.newWithKeysValues(1, false).makeString());
        Assertions.assertEquals("true", this.newWithKeysValues(null, true).makeString());

        ObjectBooleanMap<Integer> map2 = this.newWithKeysValues(1, true, 32, false);
        Assertions.assertTrue(
                "[true/false]".equals(map2.makeString("[", "/", "]"))
                        || "[false/true]".equals(map2.makeString("[", "/", "]")),
                map2.makeString("[", "/", "]"));

        Assertions.assertTrue(
                "true/false".equals(map2.makeString("/"))
                        || "false/true".equals(map2.makeString("/")),
                map2.makeString("/"));
    }

    @Test
    public void appendString()
    {
        Appendable appendable = new StringBuilder();
        this.getEmptyMap().appendString(appendable);
        Assertions.assertEquals("", appendable.toString());

        Appendable appendable0 = new StringBuilder();
        this.newWithKeysValues(0, true).appendString(appendable0);
        Assertions.assertEquals("true", appendable0.toString());

        Appendable appendable1 = new StringBuilder();
        this.newWithKeysValues(1, false).appendString(appendable1);
        Assertions.assertEquals("false", appendable1.toString());

        Appendable appendable2 = new StringBuilder();
        this.newWithKeysValues(null, false).appendString(appendable2);
        Assertions.assertEquals("false", appendable2.toString());

        Appendable appendable3 = new StringBuilder();
        ObjectBooleanMap<Integer> map1 = this.newWithKeysValues(0, true, 1, false);
        map1.appendString(appendable3);
        Assertions.assertTrue(
                "true, false".equals(appendable3.toString())
                        || "false, true".equals(appendable3.toString()),
                appendable3.toString());

        Appendable appendable4 = new StringBuilder();
        map1.appendString(appendable4, "/");
        Assertions.assertTrue(
                "true/false".equals(appendable4.toString())
                        || "false/true".equals(appendable4.toString()),
                appendable4.toString());

        Appendable appendable5 = new StringBuilder();
        map1.appendString(appendable5, "[", "/", "]");
        Assertions.assertTrue(
                "[true/false]".equals(appendable5.toString())
                        || "[false/true]".equals(appendable5.toString()),
                appendable5.toString());
    }

    @Test
    public void select()
    {
        Assertions.assertEquals(BooleanHashBag.newBagWith(true, true), this.classUnderTest().select(BooleanPredicates.isTrue()).toBag());
        Assertions.assertEquals(BooleanHashBag.newBagWith(false), this.classUnderTest().select(BooleanPredicates.isFalse()).toBag());
        Assertions.assertEquals(BooleanHashBag.newBagWith(true, true, false), this.classUnderTest().select(BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())).toBag());
        Assertions.assertEquals(new BooleanHashBag(), this.classUnderTest().select(BooleanPredicates.and(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())).toBag());

        Assertions.assertEquals(this.newWithKeysValues("0", true), this.classUnderTest().select((object, value) -> (Integer.parseInt(object) & 1) == 0 && value));
        Assertions.assertEquals(this.newWithKeysValues("2", false), this.classUnderTest().select((object, value) -> (Integer.parseInt(object) & 1) == 0 && !value));
        Assertions.assertEquals(ObjectBooleanHashMap.newMap(), this.classUnderTest().select((object, value) -> (Integer.parseInt(object) & 1) != 0 && !value));
    }

    @Test
    public void reject()
    {
        Assertions.assertEquals(BooleanHashBag.newBagWith(false), this.classUnderTest().reject(BooleanPredicates.isTrue()).toBag());
        Assertions.assertEquals(BooleanHashBag.newBagWith(true, true), this.classUnderTest().reject(BooleanPredicates.isFalse()).toBag());
        Assertions.assertEquals(new BooleanHashBag(), this.classUnderTest().reject(BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())).toBag());
        Assertions.assertEquals(BooleanHashBag.newBagWith(true, true, false), this.classUnderTest().reject(BooleanPredicates.and(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())).toBag());

        Assertions.assertEquals(this.newWithKeysValues("1", true, "2", false), this.classUnderTest().reject((object, value) -> (Integer.parseInt(object) & 1) == 0 && value));
        Assertions.assertEquals(this.newWithKeysValues("0", true, "1", true), this.classUnderTest().reject((object, value) -> (Integer.parseInt(object) & 1) == 0 && !value));
        Assertions.assertEquals(this.newWithKeysValues("0", true, "1", true, "2", false), this.classUnderTest().reject((object, value) -> (Integer.parseInt(object) & 1) != 0 && !value));
    }

    @Test
    public void count()
    {
        Assertions.assertEquals(2L, this.classUnderTest().count(BooleanPredicates.isTrue()));
        Assertions.assertEquals(1L, this.classUnderTest().count(BooleanPredicates.isFalse()));
        Assertions.assertEquals(3L, this.classUnderTest().count(BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())));
        Assertions.assertEquals(0L, this.classUnderTest().count(BooleanPredicates.and(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())));
    }

    @Test
    public void anySatisfy()
    {
        Assertions.assertTrue(this.classUnderTest().anySatisfy(BooleanPredicates.isTrue()));
        Assertions.assertTrue(this.classUnderTest().anySatisfy(BooleanPredicates.isFalse()));
        Assertions.assertTrue(this.classUnderTest().anySatisfy(BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())));
        Assertions.assertFalse(this.classUnderTest().anySatisfy(BooleanPredicates.and(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())));
    }

    @Test
    public void allSatisfy()
    {
        Assertions.assertFalse(this.classUnderTest().allSatisfy(BooleanPredicates.isTrue()));
        Assertions.assertFalse(this.classUnderTest().allSatisfy(BooleanPredicates.isFalse()));
        Assertions.assertTrue(this.classUnderTest().allSatisfy(BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())));
        Assertions.assertFalse(this.classUnderTest().allSatisfy(BooleanPredicates.and(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())));
    }

    @Test
    public void noneSatisfy()
    {
        Assertions.assertFalse(this.classUnderTest().noneSatisfy(BooleanPredicates.isTrue()));
        Assertions.assertFalse(this.classUnderTest().noneSatisfy(BooleanPredicates.isFalse()));
        Assertions.assertTrue(this.classUnderTest().noneSatisfy(BooleanPredicates.and(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())));
        Assertions.assertFalse(this.classUnderTest().noneSatisfy(BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())));
    }

    @Test
    public void detectIfNone()
    {
        Assertions.assertTrue(this.classUnderTest().detectIfNone(BooleanPredicates.isTrue(), false));
        Assertions.assertFalse(this.classUnderTest().detectIfNone(BooleanPredicates.isFalse(), true));
        Assertions.assertFalse(this.newWithKeysValues("0", true, "1", true).detectIfNone(BooleanPredicates.and(BooleanPredicates.isTrue(), BooleanPredicates.isFalse()), false));
    }

    @Test
    public void collect()
    {
        ObjectBooleanMap<String> map1 = this.newWithKeysValues("0", true, "1", false);
        ObjectBooleanMap<String> map2 = this.newWithKeysValues("0", true);
        ObjectBooleanMap<String> map3 = this.newWithKeysValues("0", false);
        Assertions.assertTrue(FastList.newListWith("true", "false").equals(map1.collect(String::valueOf)) || FastList.newListWith("false", "true").equals(map1.collect(String::valueOf)));
        Assertions.assertEquals(FastList.newListWith("true"), map2.collect(String::valueOf));
        Assertions.assertEquals(FastList.newListWith("false"), map3.collect(String::valueOf));
    }

    @Test
    public void toArray()
    {
        ObjectBooleanMap<String> map1 = this.newWithKeysValues(null, true, "1", false);
        ObjectBooleanMap<String> map2 = this.newWithKeysValues("0", true);
        ObjectBooleanMap<String> map3 = this.newWithKeysValues("0", false);

        Assertions.assertTrue(Arrays.equals(new boolean[]{true, false}, map1.toArray())
                || Arrays.equals(new boolean[]{false, true}, map1.toArray()));
        Assertions.assertTrue(Arrays.equals(new boolean[]{true}, map2.toArray()));
        Assertions.assertTrue(Arrays.equals(new boolean[]{false}, map3.toArray()));
    }

    @Test
    public void contains()
    {
        Assertions.assertTrue(this.classUnderTest().contains(true));
        Assertions.assertTrue(this.classUnderTest().contains(false));
    }

    @Test
    public void containsAll()
    {
        Assertions.assertTrue(this.classUnderTest().containsAll(true, false));
        Assertions.assertTrue(this.classUnderTest().containsAll(true, true));
        Assertions.assertTrue(this.classUnderTest().containsAll(false, false));
    }

    @Test
    public void containsAllIterable()
    {
        Assertions.assertTrue(this.classUnderTest().containsAll(BooleanArrayList.newListWith(true, false)));
        Assertions.assertTrue(this.classUnderTest().containsAll(BooleanArrayList.newListWith(true, true)));
        Assertions.assertTrue(this.classUnderTest().containsAll(BooleanArrayList.newListWith(false, false)));
    }

    @Test
    public void toList()
    {
        ObjectBooleanMap<String> map1 = this.newWithKeysValues(null, true, "1", false);
        ObjectBooleanMap<String> map2 = this.newWithKeysValues("0", true);
        ObjectBooleanMap<String> map3 = this.newWithKeysValues("0", false);

        Assertions.assertTrue(BooleanArrayList.newListWith(true, false).equals(map1.toList())
                || BooleanArrayList.newListWith(false, true).equals(map1.toList()), map1.toList().toString());
        Assertions.assertEquals(BooleanArrayList.newListWith(true), map2.toList());
        Assertions.assertEquals(BooleanArrayList.newListWith(false), map3.toList());
    }

    @Test
    public void toSet()
    {
        ObjectBooleanMap<String> map1 = this.newWithKeysValues("1", false, null, true, "2", false);
        ObjectBooleanMap<String> map0 = this.newWithKeysValues("1", false, null, true, "2", true);
        ObjectBooleanMap<String> map2 = this.newWithKeysValues("0", true);
        ObjectBooleanMap<String> map3 = this.newWithKeysValues("0", false);

        Assertions.assertEquals(BooleanHashSet.newSetWith(false, true), map1.toSet());
        Assertions.assertEquals(BooleanHashSet.newSetWith(false, true), map0.toSet());
        Assertions.assertEquals(BooleanHashSet.newSetWith(true), map2.toSet());
        Assertions.assertEquals(BooleanHashSet.newSetWith(false), map3.toSet());
    }

    @Test
    public void toBag()
    {
        ObjectBooleanMap<String> map1 = this.newWithKeysValues("1", false, null, true, "2", false);
        ObjectBooleanMap<String> map0 = this.newWithKeysValues("1", false, null, true, "2", true);
        ObjectBooleanMap<String> map2 = this.newWithKeysValues("0", true);
        ObjectBooleanMap<String> map3 = this.newWithKeysValues("0", false);

        Assertions.assertEquals(BooleanHashBag.newBagWith(false, false, true), map1.toBag());
        Assertions.assertEquals(BooleanHashBag.newBagWith(false, true, true), map0.toBag());
        Assertions.assertEquals(BooleanHashBag.newBagWith(true), map2.toBag());
        Assertions.assertEquals(BooleanHashBag.newBagWith(false), map3.toBag());
    }

    @Test
    public void asLazy()
    {
        Verify.assertSize(this.classUnderTest().toList().size(), this.classUnderTest().asLazy().toList());
        Assertions.assertTrue(this.classUnderTest().asLazy().toList().containsAll(this.classUnderTest().toList()));
    }

    @Test
    public void iterator()
    {
        ObjectBooleanMap<String> map1 = this.newWithKeysValues(null, true, "GSCollections", false);
        ObjectBooleanMap<String> map2 = this.newWithKeysValues("0", true);
        ObjectBooleanMap<String> map3 = this.newWithKeysValues("0", false);

        BooleanIterator iterator1 = map1.booleanIterator();
        Assertions.assertTrue(iterator1.hasNext());
        boolean first = iterator1.next();
        Assertions.assertTrue(iterator1.hasNext());
        boolean second = iterator1.next();
        Assertions.assertEquals(first, !second);
        Assertions.assertFalse(iterator1.hasNext());
        Verify.assertThrows(NoSuchElementException.class, (Runnable) iterator1::next);

        BooleanIterator iterator2 = map2.booleanIterator();
        Assertions.assertTrue(iterator2.hasNext());
        Assertions.assertTrue(iterator2.next());
        Assertions.assertFalse(iterator2.hasNext());
        Verify.assertThrows(NoSuchElementException.class, (Runnable) iterator2::next);

        BooleanIterator iterator3 = map3.booleanIterator();
        Assertions.assertTrue(iterator3.hasNext());
        Assertions.assertFalse(iterator3.next());
        Assertions.assertFalse(iterator3.hasNext());
        Verify.assertThrows(NoSuchElementException.class, (Runnable) iterator3::next);
    }

    @Test
    public void toImmutable()
    {
        Assertions.assertEquals(this.classUnderTest(), this.classUnderTest().toImmutable());
        Verify.assertInstanceOf(ImmutableObjectBooleanMap.class, this.classUnderTest().toImmutable());
    }
}
