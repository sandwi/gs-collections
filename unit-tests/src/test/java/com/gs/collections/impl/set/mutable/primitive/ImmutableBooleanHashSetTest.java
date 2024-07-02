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

package com.gs.collections.impl.set.mutable.primitive;

import java.util.Arrays;
import java.util.NoSuchElementException;

import com.gs.collections.api.LazyBooleanIterable;
import com.gs.collections.api.block.function.primitive.BooleanToObjectFunction;
import com.gs.collections.api.block.function.primitive.ObjectBooleanToObjectFunction;
import com.gs.collections.api.collection.primitive.ImmutableBooleanCollection;
import com.gs.collections.api.iterator.BooleanIterator;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.set.primitive.ImmutableBooleanSet;
import com.gs.collections.api.set.primitive.MutableBooleanSet;
import com.gs.collections.impl.bag.mutable.primitive.BooleanHashBag;
import com.gs.collections.impl.block.factory.primitive.BooleanPredicates;
import com.gs.collections.impl.collection.immutable.primitive.AbstractImmutableBooleanCollectionTestCase;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.math.MutableInteger;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ImmutableBooleanHashSetTest extends AbstractImmutableBooleanCollectionTestCase
{
    private ImmutableBooleanSet emptySet;
    private ImmutableBooleanSet falseSet;
    private ImmutableBooleanSet trueSet;
    private ImmutableBooleanSet trueFalseSet;

    @Override
    protected ImmutableBooleanSet classUnderTest()
    {
        return BooleanHashSet.newSetWith(true, false).toImmutable();
    }

    @Override
    protected ImmutableBooleanSet newWith(boolean... elements)
    {
        return BooleanHashSet.newSetWith(elements).toImmutable();
    }

    @Override
    protected MutableBooleanSet newMutableCollectionWith(boolean... elements)
    {
        return BooleanHashSet.newSetWith(elements);
    }

    @Override
    protected MutableSet<Object> newObjectCollectionWith(Object... elements)
    {
        return UnifiedSet.newSetWith(elements);
    }

    @BeforeEach
    public void setup()
    {
        this.emptySet = this.newWith();
        this.falseSet = this.newWith(false);
        this.trueSet = this.newWith(true);
        this.trueFalseSet = this.newWith(true, false);
    }

    @Override
    @Test
    public void newCollectionWith()
    {
        ImmutableBooleanSet set = this.classUnderTest();
        Verify.assertSize(2, set);
        Assertions.assertTrue(set.containsAll(true, false, true));
    }

    @Override
    @Test
    public void isEmpty()
    {
        super.isEmpty();
        Verify.assertEmpty(this.emptySet);
        Verify.assertNotEmpty(this.falseSet);
        Verify.assertNotEmpty(this.trueSet);
        Verify.assertNotEmpty(this.trueFalseSet);
    }

    @Override
    @Test
    public void notEmpty()
    {
        super.notEmpty();
        Assertions.assertFalse(this.emptySet.notEmpty());
        Assertions.assertTrue(this.falseSet.notEmpty());
        Assertions.assertTrue(this.trueSet.notEmpty());
        Assertions.assertTrue(this.trueFalseSet.notEmpty());
    }

    @Override
    @Test
    public void contains()
    {
        super.contains();
        Assertions.assertFalse(this.emptySet.contains(true));
        Assertions.assertFalse(this.emptySet.contains(false));
        Assertions.assertTrue(this.falseSet.contains(false));
        Assertions.assertFalse(this.falseSet.contains(true));
        Assertions.assertTrue(this.trueSet.contains(true));
        Assertions.assertFalse(this.trueSet.contains(false));
        Assertions.assertTrue(this.trueFalseSet.contains(true));
        Assertions.assertTrue(this.trueFalseSet.contains(false));
    }

    @Override
    @Test
    public void containsAllArray()
    {
        super.containsAllArray();
        Assertions.assertFalse(this.emptySet.containsAll(true));
        Assertions.assertFalse(this.emptySet.containsAll(true, false));
        Assertions.assertTrue(this.falseSet.containsAll(false, false));
        Assertions.assertFalse(this.falseSet.containsAll(true, true));
        Assertions.assertFalse(this.falseSet.containsAll(true, false, true));
        Assertions.assertTrue(this.trueSet.containsAll(true, true));
        Assertions.assertFalse(this.trueSet.containsAll(false, false));
        Assertions.assertFalse(this.trueSet.containsAll(true, false, false));
        Assertions.assertTrue(this.trueFalseSet.containsAll(true, true));
        Assertions.assertTrue(this.trueFalseSet.containsAll(false, false));
        Assertions.assertTrue(this.trueFalseSet.containsAll(false, true, true));
    }

    @Override
    @Test
    public void containsAllIterable()
    {
        super.containsAllIterable();
        Assertions.assertFalse(this.emptySet.containsAll(BooleanArrayList.newListWith(true)));
        Assertions.assertFalse(this.emptySet.containsAll(BooleanArrayList.newListWith(true, false)));
        Assertions.assertTrue(this.falseSet.containsAll(BooleanArrayList.newListWith(false, false)));
        Assertions.assertFalse(this.falseSet.containsAll(BooleanArrayList.newListWith(true, true)));
        Assertions.assertFalse(this.falseSet.containsAll(BooleanArrayList.newListWith(true, false, true)));
        Assertions.assertTrue(this.trueSet.containsAll(BooleanArrayList.newListWith(true, true)));
        Assertions.assertFalse(this.trueSet.containsAll(BooleanArrayList.newListWith(false, false)));
        Assertions.assertFalse(this.trueSet.containsAll(BooleanArrayList.newListWith(true, false, false)));
        Assertions.assertTrue(this.trueFalseSet.containsAll(BooleanArrayList.newListWith(true, true)));
        Assertions.assertTrue(this.trueFalseSet.containsAll(BooleanArrayList.newListWith(false, false)));
        Assertions.assertTrue(this.trueFalseSet.containsAll(BooleanArrayList.newListWith(false, true, true)));
    }

    @Override
    @Test
    public void toArray()
    {
        super.toArray();
        Assertions.assertEquals(0L, this.emptySet.toArray().length);

        Assertions.assertEquals(1L, this.falseSet.toArray().length);
        Assertions.assertFalse(this.falseSet.toArray()[0]);

        Assertions.assertEquals(1L, this.trueSet.toArray().length);
        Assertions.assertTrue(this.trueSet.toArray()[0]);

        Assertions.assertEquals(2L, this.trueFalseSet.toArray().length);
        Assertions.assertTrue(Arrays.equals(new boolean[]{false, true}, this.trueFalseSet.toArray())
                || Arrays.equals(new boolean[]{true, false}, this.trueFalseSet.toArray()));
    }

    @Override
    @Test
    public void toList()
    {
        super.toList();
        Assertions.assertEquals(new BooleanArrayList(), this.emptySet.toList());
        Assertions.assertEquals(BooleanArrayList.newListWith(false), this.falseSet.toList());
        Assertions.assertEquals(BooleanArrayList.newListWith(true), this.trueSet.toList());
        Assertions.assertTrue(BooleanArrayList.newListWith(false, true).equals(this.trueFalseSet.toList())
                || BooleanArrayList.newListWith(true, false).equals(this.trueFalseSet.toList()));
    }

    @Override
    @Test
    public void toSet()
    {
        super.toSet();
        Assertions.assertEquals(new BooleanHashSet(), this.emptySet.toSet());
        Assertions.assertEquals(BooleanHashSet.newSetWith(false), this.falseSet.toSet());
        Assertions.assertEquals(BooleanHashSet.newSetWith(true), this.trueSet.toSet());
        Assertions.assertEquals(BooleanHashSet.newSetWith(false, true), this.trueFalseSet.toSet());
    }

    @Override
    @Test
    public void toBag()
    {
        Assertions.assertEquals(new BooleanHashBag(), this.emptySet.toBag());
        Assertions.assertEquals(BooleanHashBag.newBagWith(false), this.falseSet.toBag());
        Assertions.assertEquals(BooleanHashBag.newBagWith(true), this.trueSet.toBag());
        Assertions.assertEquals(BooleanHashBag.newBagWith(false, true), this.trueFalseSet.toBag());
    }

    @Override
    @Test
    public void testEquals()
    {
        Assertions.assertNotEquals(this.falseSet, this.emptySet);
        Assertions.assertNotEquals(this.falseSet, this.trueSet);
        Assertions.assertNotEquals(this.falseSet, this.trueFalseSet);
        Assertions.assertNotEquals(this.trueSet, this.emptySet);
        Assertions.assertNotEquals(this.trueSet, this.trueFalseSet);
        Assertions.assertNotEquals(this.trueFalseSet, this.emptySet);
        Verify.assertEqualsAndHashCode(this.newWith(false, true), this.trueFalseSet);
        Verify.assertEqualsAndHashCode(this.newWith(true, false), this.trueFalseSet);

        Verify.assertPostSerializedIdentity(this.emptySet);
        Verify.assertPostSerializedIdentity(this.falseSet);
        Verify.assertPostSerializedIdentity(this.trueSet);
        Verify.assertPostSerializedIdentity(this.trueFalseSet);
    }

    @Override
    @Test
    public void testHashCode()
    {
        super.testHashCode();
        Assertions.assertEquals(UnifiedSet.newSet().hashCode(), this.emptySet.hashCode());
        Assertions.assertEquals(UnifiedSet.newSetWith(false).hashCode(), this.falseSet.hashCode());
        Assertions.assertEquals(UnifiedSet.newSetWith(true).hashCode(), this.trueSet.hashCode());
        Assertions.assertEquals(UnifiedSet.newSetWith(true, false).hashCode(), this.trueFalseSet.hashCode());
        Assertions.assertEquals(UnifiedSet.newSetWith(false, true).hashCode(), this.trueFalseSet.hashCode());
        Assertions.assertNotEquals(UnifiedSet.newSetWith(false).hashCode(), this.trueFalseSet.hashCode());
    }

    @Override
    @Test
    public void booleanIterator()
    {
        BooleanIterator booleanIterator0 = this.emptySet.booleanIterator();
        Assertions.assertFalse(booleanIterator0.hasNext());
        Verify.assertThrows(NoSuchElementException.class, (Runnable) booleanIterator0::next);

        BooleanIterator booleanIterator1 = this.falseSet.booleanIterator();
        Assertions.assertTrue(booleanIterator1.hasNext());
        Assertions.assertFalse(booleanIterator1.next());
        Assertions.assertFalse(booleanIterator1.hasNext());
        Verify.assertThrows(NoSuchElementException.class, (Runnable) booleanIterator1::next);

        BooleanIterator booleanIterator2 = this.trueSet.booleanIterator();
        Assertions.assertTrue(booleanIterator2.hasNext());
        Assertions.assertTrue(booleanIterator2.next());
        Assertions.assertFalse(booleanIterator2.hasNext());
        Verify.assertThrows(NoSuchElementException.class, (Runnable) booleanIterator2::next);

        BooleanIterator booleanIterator3 = this.trueFalseSet.booleanIterator();
        Assertions.assertTrue(booleanIterator3.hasNext());
        BooleanHashSet actual = new BooleanHashSet();
        actual.add(booleanIterator3.next());
        Assertions.assertTrue(booleanIterator3.hasNext());
        actual.add(booleanIterator3.next());
        Assertions.assertEquals(BooleanHashSet.newSetWith(true, false), actual);
        Assertions.assertFalse(booleanIterator3.hasNext());
        Verify.assertThrows(NoSuchElementException.class, (Runnable) booleanIterator3::next);
    }

    @Override
    @Test
    public void forEach()
    {
        String[] sum = new String[4];
        for (int i = 0; i < sum.length; i++)
        {
            sum[i] = "";
        }
        this.emptySet.forEach(each -> sum[0] += each);
        this.falseSet.forEach(each -> sum[1] += each);
        this.trueSet.forEach(each -> sum[2] += each);
        this.trueFalseSet.forEach(each -> sum[3] += each);

        Assertions.assertEquals("", sum[0]);
        Assertions.assertEquals("false", sum[1]);
        Assertions.assertEquals("true", sum[2]);
        Assertions.assertTrue("truefalse".equals(sum[3]) || "falsetrue".equals(sum[3]));
    }

    @Override
    @Test
    public void injectInto()
    {
        ObjectBooleanToObjectFunction<MutableInteger, MutableInteger> function = (object, value) -> object.add(value ? 1 : 0);
        Assertions.assertEquals(new MutableInteger(1), BooleanHashSet.newSetWith(true, false, true).injectInto(new MutableInteger(0), function));
        Assertions.assertEquals(new MutableInteger(1), BooleanHashSet.newSetWith(true).injectInto(new MutableInteger(0), function));
        Assertions.assertEquals(new MutableInteger(0), BooleanHashSet.newSetWith(false).injectInto(new MutableInteger(0), function));
        Assertions.assertEquals(new MutableInteger(0), new BooleanHashSet().injectInto(new MutableInteger(0), function));
    }

    @Override
    @Test
    public void size()
    {
        super.size();
        Verify.assertSize(2, this.classUnderTest());
    }

    @Override
    @Test
    public void count()
    {
        Assertions.assertEquals(0L, this.emptySet.count(BooleanPredicates.isTrue()));
        Assertions.assertEquals(0L, this.falseSet.count(BooleanPredicates.isTrue()));
        Assertions.assertEquals(1L, this.falseSet.count(BooleanPredicates.isFalse()));
        Assertions.assertEquals(0L, this.trueSet.count(BooleanPredicates.isFalse()));
        Assertions.assertEquals(1L, this.trueFalseSet.count(BooleanPredicates.isTrue()));
        Assertions.assertEquals(0L, this.trueFalseSet.count(BooleanPredicates.and(BooleanPredicates.isFalse(), BooleanPredicates.isTrue())));
        Assertions.assertEquals(1L, this.trueFalseSet.count(BooleanPredicates.isFalse()));
        Assertions.assertEquals(1L, this.trueFalseSet.count(BooleanPredicates.isTrue()));
        Assertions.assertEquals(2L, this.trueFalseSet.count(BooleanPredicates.or(BooleanPredicates.isFalse(), BooleanPredicates.isTrue())));
    }

    @Override
    @Test
    public void anySatisfy()
    {
        super.anySatisfy();
        Assertions.assertFalse(this.emptySet.anySatisfy(BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())));
        Assertions.assertFalse(this.falseSet.anySatisfy(BooleanPredicates.isTrue()));
        Assertions.assertTrue(this.falseSet.anySatisfy(BooleanPredicates.isFalse()));
        Assertions.assertFalse(this.trueSet.anySatisfy(BooleanPredicates.isFalse()));
        Assertions.assertTrue(this.trueSet.anySatisfy(BooleanPredicates.isTrue()));
        Assertions.assertTrue(this.trueFalseSet.anySatisfy(BooleanPredicates.isTrue()));
        Assertions.assertTrue(this.trueFalseSet.anySatisfy(BooleanPredicates.isFalse()));
        Assertions.assertFalse(this.trueFalseSet.anySatisfy(BooleanPredicates.and(BooleanPredicates.isFalse(), BooleanPredicates.isTrue())));
    }

    @Override
    @Test
    public void allSatisfy()
    {
        super.allSatisfy();
        Assertions.assertTrue(this.emptySet.allSatisfy(BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())));
        Assertions.assertFalse(this.falseSet.allSatisfy(BooleanPredicates.isTrue()));
        Assertions.assertTrue(this.falseSet.allSatisfy(BooleanPredicates.isFalse()));
        Assertions.assertFalse(this.trueSet.allSatisfy(BooleanPredicates.isFalse()));
        Assertions.assertTrue(this.trueSet.allSatisfy(BooleanPredicates.isTrue()));
        Assertions.assertFalse(this.trueFalseSet.allSatisfy(BooleanPredicates.isTrue()));
        Assertions.assertFalse(this.trueFalseSet.allSatisfy(BooleanPredicates.isFalse()));
        Assertions.assertFalse(this.trueFalseSet.allSatisfy(BooleanPredicates.and(BooleanPredicates.isFalse(), BooleanPredicates.isTrue())));
        Assertions.assertTrue(this.trueFalseSet.allSatisfy(BooleanPredicates.or(BooleanPredicates.isFalse(), BooleanPredicates.isTrue())));
    }

    @Override
    @Test
    public void noneSatisfy()
    {
        Assertions.assertFalse(this.emptySet.noneSatisfy(BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())));
        Assertions.assertFalse(this.falseSet.noneSatisfy(BooleanPredicates.isFalse()));
        Assertions.assertTrue(this.falseSet.noneSatisfy(BooleanPredicates.isTrue()));
        Assertions.assertFalse(this.trueSet.noneSatisfy(BooleanPredicates.isTrue()));
        Assertions.assertTrue(this.trueSet.noneSatisfy(BooleanPredicates.isFalse()));
        Assertions.assertFalse(this.trueFalseSet.noneSatisfy(BooleanPredicates.isTrue()));
        Assertions.assertFalse(this.trueFalseSet.noneSatisfy(BooleanPredicates.isFalse()));
        Assertions.assertTrue(this.trueFalseSet.noneSatisfy(BooleanPredicates.and(BooleanPredicates.isFalse(), BooleanPredicates.isTrue())));
        Assertions.assertFalse(this.trueFalseSet.noneSatisfy(BooleanPredicates.or(BooleanPredicates.isFalse(), BooleanPredicates.isTrue())));
    }

    @Override
    @Test
    public void select()
    {
        Verify.assertEmpty(this.emptySet.select(BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())));
        Verify.assertEmpty(this.falseSet.select(BooleanPredicates.isTrue()));
        Verify.assertSize(1, this.falseSet.select(BooleanPredicates.isFalse()));
        Verify.assertEmpty(this.trueSet.select(BooleanPredicates.isFalse()));
        Verify.assertSize(1, this.trueSet.select(BooleanPredicates.isTrue()));
        Verify.assertSize(1, this.trueFalseSet.select(BooleanPredicates.isFalse()));
        Verify.assertSize(1, this.trueFalseSet.select(BooleanPredicates.isTrue()));
        Verify.assertEmpty(this.trueFalseSet.select(BooleanPredicates.and(BooleanPredicates.isFalse(), BooleanPredicates.isTrue())));
        Verify.assertSize(2, this.trueFalseSet.select(BooleanPredicates.or(BooleanPredicates.isFalse(), BooleanPredicates.isTrue())));
    }

    @Override
    @Test
    public void reject()
    {
        Verify.assertEmpty(this.emptySet.reject(BooleanPredicates.and(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())));
        Verify.assertEmpty(this.trueSet.reject(BooleanPredicates.isTrue()));
        Verify.assertSize(1, this.trueSet.reject(BooleanPredicates.isFalse()));
        Verify.assertEmpty(this.falseSet.reject(BooleanPredicates.isFalse()));
        Verify.assertSize(1, this.falseSet.reject(BooleanPredicates.isTrue()));
        Verify.assertSize(1, this.trueFalseSet.reject(BooleanPredicates.isFalse()));
        Verify.assertSize(1, this.trueFalseSet.reject(BooleanPredicates.isTrue()));
        Verify.assertEmpty(this.trueFalseSet.reject(BooleanPredicates.or(BooleanPredicates.isFalse(), BooleanPredicates.isTrue())));
        Verify.assertSize(2, this.trueFalseSet.reject(BooleanPredicates.and(BooleanPredicates.isFalse(), BooleanPredicates.isTrue())));
    }

    @Override
    @Test
    public void detectIfNone()
    {
        super.detectIfNone();
        Assertions.assertTrue(this.emptySet.detectIfNone(BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.isFalse()), true));
        Assertions.assertFalse(this.emptySet.detectIfNone(BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.isFalse()), false));
        Assertions.assertTrue(this.falseSet.detectIfNone(BooleanPredicates.isTrue(), true));
        Assertions.assertFalse(this.falseSet.detectIfNone(BooleanPredicates.isTrue(), false));
        Assertions.assertFalse(this.falseSet.detectIfNone(BooleanPredicates.isFalse(), true));
        Assertions.assertFalse(this.falseSet.detectIfNone(BooleanPredicates.isFalse(), false));
        Assertions.assertTrue(this.trueSet.detectIfNone(BooleanPredicates.isFalse(), true));
        Assertions.assertFalse(this.trueSet.detectIfNone(BooleanPredicates.isFalse(), false));
        Assertions.assertTrue(this.trueSet.detectIfNone(BooleanPredicates.isTrue(), true));
        Assertions.assertTrue(this.trueSet.detectIfNone(BooleanPredicates.isTrue(), false));
        Assertions.assertTrue(this.trueFalseSet.detectIfNone(BooleanPredicates.and(BooleanPredicates.isFalse(), BooleanPredicates.isTrue()), true));
        Assertions.assertFalse(this.trueFalseSet.detectIfNone(BooleanPredicates.and(BooleanPredicates.isFalse(), BooleanPredicates.isTrue()), false));
        Assertions.assertFalse(this.trueFalseSet.detectIfNone(BooleanPredicates.isFalse(), true));
        Assertions.assertTrue(this.trueFalseSet.detectIfNone(BooleanPredicates.isTrue(), false));
    }

    @Override
    @Test
    public void collect()
    {
        super.collect();
        BooleanToObjectFunction<Boolean> function = parameter -> !parameter;
        Assertions.assertEquals(UnifiedSet.newSetWith(true, false), this.trueFalseSet.collect(function));
        Assertions.assertEquals(UnifiedSet.newSetWith(false), this.trueSet.collect(function));
        Assertions.assertEquals(UnifiedSet.newSetWith(true), this.falseSet.collect(function));
        Assertions.assertEquals(UnifiedSet.newSetWith(), this.emptySet.collect(function));
    }

    @Override
    @Test
    public void testToString()
    {
        super.testToString();
        Assertions.assertEquals("[]", this.emptySet.toString());
        Assertions.assertEquals("[false]", this.falseSet.toString());
        Assertions.assertEquals("[true]", this.trueSet.toString());
        Assertions.assertTrue("[true, false]".equals(this.trueFalseSet.toString())
                || "[false, true]".equals(this.trueFalseSet.toString()));
    }

    @Override
    @Test
    public void makeString()
    {
        super.makeString();
        Assertions.assertEquals("", this.emptySet.makeString());
        Assertions.assertEquals("false", this.falseSet.makeString());
        Assertions.assertEquals("true", this.trueSet.makeString());
        Assertions.assertTrue("true, false".equals(this.trueFalseSet.makeString())
                || "false, true".equals(this.trueFalseSet.makeString()));

        Assertions.assertEquals("", this.emptySet.makeString("/"));
        Assertions.assertEquals("false", this.falseSet.makeString("/"));
        Assertions.assertEquals("true", this.trueSet.makeString("/"));
        Assertions.assertTrue("true/false".equals(this.trueFalseSet.makeString("/"))
                || "false/true".equals(this.trueFalseSet.makeString("/")), this.trueFalseSet.makeString("/"));

        Assertions.assertEquals("[]", this.emptySet.makeString("[", "/", "]"));
        Assertions.assertEquals("[false]", this.falseSet.makeString("[", "/", "]"));
        Assertions.assertEquals("[true]", this.trueSet.makeString("[", "/", "]"));
        Assertions.assertTrue("[true/false]".equals(this.trueFalseSet.makeString("[", "/", "]"))
                || "[false/true]".equals(this.trueFalseSet.makeString("[", "/", "]")), this.trueFalseSet.makeString("[", "/", "]"));
    }

    @Override
    @Test
    public void appendString()
    {
        super.appendString();
        StringBuilder appendable = new StringBuilder();
        this.emptySet.appendString(appendable);
        Assertions.assertEquals("", appendable.toString());

        StringBuilder appendable1 = new StringBuilder();
        this.falseSet.appendString(appendable1);
        Assertions.assertEquals("false", appendable1.toString());

        StringBuilder appendable2 = new StringBuilder();
        this.trueSet.appendString(appendable2);
        Assertions.assertEquals("true", appendable2.toString());

        StringBuilder appendable3 = new StringBuilder();
        this.trueFalseSet.appendString(appendable3);
        Assertions.assertTrue("true, false".equals(appendable3.toString())
                || "false, true".equals(appendable3.toString()));

        StringBuilder appendable4 = new StringBuilder();
        this.trueFalseSet.appendString(appendable4, "[", ", ", "]");
        Assertions.assertTrue("[true, false]".equals(appendable4.toString())
                || "[false, true]".equals(appendable4.toString()));
    }

    @Override
    @Test
    public void asLazy()
    {
        super.asLazy();
        Verify.assertInstanceOf(LazyBooleanIterable.class, this.emptySet.asLazy());
        Assertions.assertEquals(this.emptySet, this.emptySet.asLazy().toSet());
        Assertions.assertEquals(this.falseSet, this.falseSet.asLazy().toSet());
        Assertions.assertEquals(this.trueSet, this.trueSet.asLazy().toSet());
        Assertions.assertEquals(this.trueFalseSet, this.trueFalseSet.asLazy().toSet());
    }

    private void assertSizeAndContains(ImmutableBooleanCollection collection, boolean... elements)
    {
        Assertions.assertEquals(elements.length, collection.size());
        for (boolean i : elements)
        {
            Assertions.assertTrue(collection.contains(i));
        }
    }

    @Override
    @Test
    public void testNewWith()
    {
        ImmutableBooleanCollection immutableCollection = this.newWith();
        ImmutableBooleanCollection collection = immutableCollection.newWith(true);
        ImmutableBooleanCollection collection0 = immutableCollection.newWith(true).newWith(false);
        this.assertSizeAndContains(immutableCollection);
        this.assertSizeAndContains(collection, true);
        this.assertSizeAndContains(collection0, true, false);
    }

    @Override
    @Test
    public void newWithAll()
    {
        ImmutableBooleanCollection immutableCollection = this.newWith();
        ImmutableBooleanCollection collection = immutableCollection.newWithAll(this.newMutableCollectionWith(true));
        ImmutableBooleanCollection collection0 = immutableCollection.newWithAll(this.newMutableCollectionWith(false));
        ImmutableBooleanCollection collection1 = immutableCollection.newWithAll(this.newMutableCollectionWith(true, false));
        this.assertSizeAndContains(immutableCollection);
        this.assertSizeAndContains(collection, true);
        this.assertSizeAndContains(collection0, false);
        this.assertSizeAndContains(collection1, true, false);
    }

    @Override
    @Test
    public void newWithout()
    {
        ImmutableBooleanCollection collection3 = this.newWith(true, false);
        ImmutableBooleanCollection collection2 = collection3.newWithout(true);
        ImmutableBooleanCollection collection1 = collection3.newWithout(false);

        this.assertSizeAndContains(collection1, true);
        this.assertSizeAndContains(collection2, false);
    }

    @Override
    @Test
    public void newWithoutAll()
    {
        ImmutableBooleanCollection collection3 = this.newWith(true, false);
        ImmutableBooleanCollection collection2 = collection3.newWithoutAll(this.newMutableCollectionWith(true));
        ImmutableBooleanCollection collection1 = collection3.newWithoutAll(this.newMutableCollectionWith(false));
        ImmutableBooleanCollection collection0 = collection3.newWithoutAll(this.newMutableCollectionWith(true, false));

        this.assertSizeAndContains(collection0);
        this.assertSizeAndContains(collection1, true);
        this.assertSizeAndContains(collection2, false);
    }
}
