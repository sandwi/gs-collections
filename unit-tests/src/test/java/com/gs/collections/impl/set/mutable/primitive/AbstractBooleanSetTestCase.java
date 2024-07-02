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
import com.gs.collections.api.collection.primitive.MutableBooleanCollection;
import com.gs.collections.api.iterator.BooleanIterator;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.set.primitive.MutableBooleanSet;
import com.gs.collections.impl.bag.mutable.primitive.BooleanHashBag;
import com.gs.collections.impl.block.factory.primitive.BooleanPredicates;
import com.gs.collections.impl.collection.mutable.primitive.AbstractMutableBooleanCollectionTestCase;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.math.MutableInteger;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractBooleanSetTestCase extends AbstractMutableBooleanCollectionTestCase
{
    private MutableBooleanSet emptySet;
    private MutableBooleanSet setWithFalse;
    private MutableBooleanSet setWithTrue;
    private MutableBooleanSet setWithTrueFalse;

    @Override
    protected abstract MutableBooleanSet classUnderTest();

    @Override
    protected abstract MutableBooleanSet newWith(boolean... elements);

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
        this.setWithFalse = this.newWith(false);
        this.setWithTrue = this.newWith(true);
        this.setWithTrueFalse = this.newWith(true, false);
    }

    @Override
    @Test
    public void newCollectionWith()
    {
        MutableBooleanSet set = this.classUnderTest();
        Verify.assertSize(2, set);
        Assertions.assertTrue(set.containsAll(true, false, true));
    }

    @Override
    @Test
    public void isEmpty()
    {
        super.isEmpty();
        Verify.assertEmpty(this.emptySet);
        Verify.assertNotEmpty(this.setWithFalse);
        Verify.assertNotEmpty(this.setWithTrue);
        Verify.assertNotEmpty(this.setWithTrueFalse);
    }

    @Override
    @Test
    public void notEmpty()
    {
        super.notEmpty();
        Assertions.assertFalse(this.emptySet.notEmpty());
        Assertions.assertTrue(this.setWithFalse.notEmpty());
        Assertions.assertTrue(this.setWithTrue.notEmpty());
        Assertions.assertTrue(this.setWithTrueFalse.notEmpty());
    }

    @Override
    @Test
    public void clear()
    {
        super.clear();
        this.emptySet.clear();
        this.setWithFalse.clear();
        this.setWithTrue.clear();
        this.setWithTrueFalse.clear();
        Verify.assertEmpty(this.emptySet);
        Verify.assertEmpty(this.setWithFalse);
        Verify.assertEmpty(this.setWithTrue);
        Verify.assertEmpty(this.setWithTrueFalse);
        Assertions.assertFalse(this.setWithFalse.contains(false));
        Assertions.assertFalse(this.setWithTrue.contains(true));
        Assertions.assertFalse(this.setWithTrueFalse.contains(true));
        Assertions.assertFalse(this.setWithTrueFalse.contains(false));
    }

    @Override
    @Test
    public void contains()
    {
        super.contains();
        Assertions.assertFalse(this.emptySet.contains(true));
        Assertions.assertFalse(this.emptySet.contains(false));
        Assertions.assertTrue(this.setWithFalse.contains(false));
        Assertions.assertFalse(this.setWithFalse.contains(true));
        Assertions.assertTrue(this.setWithTrue.contains(true));
        Assertions.assertFalse(this.setWithTrue.contains(false));
        Assertions.assertTrue(this.setWithTrueFalse.contains(true));
        Assertions.assertTrue(this.setWithTrueFalse.contains(false));
    }

    @Override
    @Test
    public void containsAllArray()
    {
        super.containsAllArray();
        Assertions.assertFalse(this.emptySet.containsAll(true));
        Assertions.assertFalse(this.emptySet.containsAll(true, false));
        Assertions.assertTrue(this.setWithFalse.containsAll(false, false));
        Assertions.assertFalse(this.setWithFalse.containsAll(true, true));
        Assertions.assertFalse(this.setWithFalse.containsAll(true, false, true));
        Assertions.assertTrue(this.setWithTrue.containsAll(true, true));
        Assertions.assertFalse(this.setWithTrue.containsAll(false, false));
        Assertions.assertFalse(this.setWithTrue.containsAll(true, false, false));
        Assertions.assertTrue(this.setWithTrueFalse.containsAll(true, true));
        Assertions.assertTrue(this.setWithTrueFalse.containsAll(false, false));
        Assertions.assertTrue(this.setWithTrueFalse.containsAll(false, true, true));
    }

    @Override
    @Test
    public void containsAllIterable()
    {
        super.containsAllIterable();
        Assertions.assertFalse(this.emptySet.containsAll(BooleanArrayList.newListWith(true)));
        Assertions.assertFalse(this.emptySet.containsAll(BooleanArrayList.newListWith(true, false)));
        Assertions.assertTrue(this.setWithFalse.containsAll(BooleanArrayList.newListWith(false, false)));
        Assertions.assertFalse(this.setWithFalse.containsAll(BooleanArrayList.newListWith(true, true)));
        Assertions.assertFalse(this.setWithFalse.containsAll(BooleanArrayList.newListWith(true, false, true)));
        Assertions.assertTrue(this.setWithTrue.containsAll(BooleanArrayList.newListWith(true, true)));
        Assertions.assertFalse(this.setWithTrue.containsAll(BooleanArrayList.newListWith(false, false)));
        Assertions.assertFalse(this.setWithTrue.containsAll(BooleanArrayList.newListWith(true, false, false)));
        Assertions.assertTrue(this.setWithTrueFalse.containsAll(BooleanArrayList.newListWith(true, true)));
        Assertions.assertTrue(this.setWithTrueFalse.containsAll(BooleanArrayList.newListWith(false, false)));
        Assertions.assertTrue(this.setWithTrueFalse.containsAll(BooleanArrayList.newListWith(false, true, true)));
    }

    @Override
    @Test
    public void add()
    {
        Assertions.assertTrue(this.emptySet.add(true));
        Assertions.assertEquals(BooleanHashSet.newSetWith(true), this.emptySet);
        MutableBooleanSet set = this.newWith();
        Assertions.assertTrue(set.add(false));
        Assertions.assertEquals(BooleanHashSet.newSetWith(false), set);
        Assertions.assertFalse(this.setWithFalse.add(false));
        Assertions.assertTrue(this.setWithFalse.add(true));
        Assertions.assertEquals(BooleanHashSet.newSetWith(true, false), this.setWithFalse);
        Assertions.assertFalse(this.setWithTrue.add(true));
        Assertions.assertTrue(this.setWithTrue.add(false));
        Assertions.assertEquals(BooleanHashSet.newSetWith(true, false), this.setWithTrue);
        Assertions.assertFalse(this.setWithTrueFalse.add(true));
        Assertions.assertFalse(this.setWithTrueFalse.add(false));
        Assertions.assertEquals(BooleanHashSet.newSetWith(true, false), this.setWithTrueFalse);
    }

    @Override
    @Test
    public void addAllArray()
    {
        Assertions.assertTrue(this.emptySet.addAll(true, false, true));
        Assertions.assertEquals(BooleanHashSet.newSetWith(true, false), this.emptySet);
        Assertions.assertFalse(this.setWithFalse.addAll(false, false));
        Assertions.assertTrue(this.setWithFalse.addAll(true, false, true));
        Assertions.assertEquals(BooleanHashSet.newSetWith(true, false), this.setWithFalse);
        Assertions.assertFalse(this.setWithTrue.addAll(true, true));
        Assertions.assertTrue(this.setWithTrue.addAll(true, false, true));
        Assertions.assertEquals(BooleanHashSet.newSetWith(true, false), this.setWithTrue);
        Assertions.assertFalse(this.setWithTrueFalse.addAll(true, false));
        Assertions.assertEquals(BooleanHashSet.newSetWith(true, false), this.setWithTrueFalse);
    }

    @Override
    @Test
    public void addAllIterable()
    {
        Assertions.assertTrue(this.emptySet.addAll(BooleanHashSet.newSetWith(true, false, true)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(true, false), this.emptySet);
        Assertions.assertFalse(this.setWithFalse.addAll(BooleanHashSet.newSetWith(false, false)));
        Assertions.assertTrue(this.setWithFalse.addAll(BooleanHashSet.newSetWith(true, false, true)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(true, false), this.setWithFalse);
        Assertions.assertFalse(this.setWithTrue.addAll(BooleanHashSet.newSetWith(true, true)));
        Assertions.assertTrue(this.setWithTrue.addAll(BooleanHashSet.newSetWith(true, false, true)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(true, false), this.setWithTrue);
        Assertions.assertFalse(this.setWithTrueFalse.addAll(BooleanHashSet.newSetWith(true, false)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(true, false), this.setWithTrueFalse);
    }

    @Override
    @Test
    public void remove()
    {
        Assertions.assertTrue(this.setWithTrueFalse.remove(true));
        Assertions.assertEquals(BooleanHashSet.newSetWith(false), this.setWithTrueFalse);
        MutableBooleanSet set = this.newWith(true, false);
        Assertions.assertTrue(set.remove(false));
        Assertions.assertEquals(BooleanHashSet.newSetWith(true), set);
        Assertions.assertFalse(this.setWithTrue.remove(false));
        Assertions.assertTrue(this.setWithTrue.remove(true));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.setWithTrue);
        Assertions.assertFalse(this.setWithFalse.remove(true));
        Assertions.assertTrue(this.setWithFalse.remove(false));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.setWithFalse);
        Assertions.assertFalse(this.emptySet.remove(true));
        Assertions.assertFalse(this.emptySet.remove(false));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.emptySet);
    }

    @Override
    @Test
    public void removeAll()
    {
        super.removeAll();
        Assertions.assertFalse(this.emptySet.removeAll());
        Assertions.assertFalse(this.setWithFalse.removeAll());
        Assertions.assertFalse(this.setWithTrue.removeAll());
        Assertions.assertFalse(this.setWithTrueFalse.removeAll());

        Assertions.assertTrue(this.setWithTrueFalse.removeAll(true, true));
        Assertions.assertEquals(BooleanHashSet.newSetWith(false), this.setWithTrueFalse);
        MutableBooleanSet set = this.newWith(true, false);
        Assertions.assertTrue(set.removeAll(true, false));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), set);
        MutableBooleanSet sett = this.newWith(true, false);
        Assertions.assertTrue(sett.removeAll(false, false));
        Assertions.assertEquals(BooleanHashSet.newSetWith(true), sett);

        Assertions.assertFalse(this.setWithTrue.removeAll(false, false));
        MutableBooleanSet sett2 = this.newWith(true);
        Assertions.assertTrue(sett2.removeAll(true, true));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), sett2);
        Assertions.assertTrue(this.setWithTrue.removeAll(true, false));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.setWithTrue);

        Assertions.assertFalse(this.setWithFalse.removeAll(true, true));
        MutableBooleanSet sett3 = this.newWith(false);
        Assertions.assertTrue(sett3.removeAll(false, false));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), sett3);
        Assertions.assertTrue(this.setWithFalse.removeAll(true, false));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.setWithFalse);

        Assertions.assertFalse(this.emptySet.removeAll(true, true));
        Assertions.assertFalse(this.emptySet.removeAll(true, false));
        Assertions.assertFalse(this.emptySet.removeAll(false, false));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.emptySet);
    }

    @Override
    @Test
    public void removeAll_iterable()
    {
        super.removeAll_iterable();
        Assertions.assertFalse(this.emptySet.removeAll(new BooleanArrayList()));
        Assertions.assertFalse(this.setWithFalse.removeAll(new BooleanArrayList()));
        Assertions.assertFalse(this.setWithTrue.removeAll(new BooleanArrayList()));
        Assertions.assertFalse(this.setWithTrueFalse.removeAll(new BooleanArrayList()));

        Assertions.assertTrue(this.setWithTrueFalse.removeAll(BooleanArrayList.newListWith(true, true)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(false), this.setWithTrueFalse);
        MutableBooleanSet set = this.newWith(true, false);
        Assertions.assertTrue(set.removeAll(BooleanArrayList.newListWith(true, false)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), set);
        MutableBooleanSet sett = this.newWith(true, false);
        Assertions.assertTrue(sett.removeAll(BooleanArrayList.newListWith(false, false)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(true), sett);

        Assertions.assertFalse(this.setWithTrue.removeAll(BooleanArrayList.newListWith(false, false)));
        MutableBooleanSet sett2 = this.newWith(true);
        Assertions.assertTrue(sett2.removeAll(BooleanArrayList.newListWith(true, true)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), sett2);
        Assertions.assertTrue(this.setWithTrue.removeAll(BooleanArrayList.newListWith(true, false)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.setWithTrue);

        Assertions.assertFalse(this.setWithFalse.removeAll(true, true));
        MutableBooleanSet sett3 = this.newWith(false);
        Assertions.assertTrue(sett3.removeAll(BooleanArrayList.newListWith(false, false)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), sett3);
        Assertions.assertTrue(this.setWithFalse.removeAll(BooleanArrayList.newListWith(true, false)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.setWithFalse);

        Assertions.assertFalse(this.emptySet.removeAll(BooleanArrayList.newListWith(true, true)));
        Assertions.assertFalse(this.emptySet.removeAll(BooleanArrayList.newListWith(true, false)));
        Assertions.assertFalse(this.emptySet.removeAll(BooleanArrayList.newListWith(false, false)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.emptySet);
    }

    @Override
    @Test
    public void retainAll()
    {
        super.retainAll();
        Assertions.assertFalse(this.emptySet.retainAll());
        Assertions.assertTrue(this.setWithTrueFalse.retainAll(true, true));
        Assertions.assertEquals(BooleanHashSet.newSetWith(true), this.setWithTrueFalse);
        MutableBooleanSet set = this.newWith(true, false);
        Assertions.assertTrue(set.retainAll());
        Assertions.assertEquals(BooleanHashSet.newSetWith(), set);
        MutableBooleanSet sett = this.newWith(true, false);
        Assertions.assertTrue(sett.retainAll(false, false));
        Assertions.assertEquals(BooleanHashSet.newSetWith(false), sett);

        MutableBooleanSet sett2 = this.newWith(true);
        Assertions.assertTrue(sett2.retainAll(false, false));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), sett2);
        Assertions.assertTrue(this.setWithTrue.retainAll(false, false));
        Assertions.assertFalse(this.setWithTrue.retainAll(true, false));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.setWithTrue);

        MutableBooleanSet sett3 = this.newWith(false);
        Assertions.assertFalse(sett3.retainAll(false, false));
        Assertions.assertEquals(BooleanHashSet.newSetWith(false), sett3);
        Assertions.assertTrue(this.setWithFalse.retainAll(true, true));
        Assertions.assertFalse(this.setWithFalse.retainAll(true, false));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.setWithFalse);

        Assertions.assertFalse(this.emptySet.retainAll(true, true));
        Assertions.assertFalse(this.emptySet.retainAll(true, false));
        Assertions.assertFalse(this.emptySet.retainAll(false, false));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.emptySet);
    }

    @Override
    @Test
    public void retainAll_iterable()
    {
        super.retainAll_iterable();
        Assertions.assertFalse(this.emptySet.retainAll(new BooleanArrayList()));
        Assertions.assertTrue(this.setWithTrueFalse.retainAll(BooleanArrayList.newListWith(true, true)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(true), this.setWithTrueFalse);
        MutableBooleanSet set = this.newWith(true, false);
        Assertions.assertTrue(set.retainAll(BooleanArrayList.newListWith()));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), set);
        MutableBooleanSet sett = this.newWith(true, false);
        Assertions.assertTrue(sett.retainAll(BooleanArrayList.newListWith(false, false)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(false), sett);

        MutableBooleanSet sett2 = this.newWith(true);
        Assertions.assertTrue(sett2.retainAll(BooleanArrayList.newListWith(false, false)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), sett2);
        Assertions.assertTrue(this.setWithTrue.retainAll(BooleanArrayList.newListWith(false, false)));
        Assertions.assertFalse(this.setWithTrue.retainAll(BooleanArrayList.newListWith(true, false)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.setWithTrue);

        MutableBooleanSet sett3 = this.newWith(false);
        Assertions.assertFalse(sett3.retainAll(BooleanArrayList.newListWith(false, false)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(false), sett3);
        Assertions.assertTrue(this.setWithFalse.retainAll(BooleanArrayList.newListWith(true, true)));
        Assertions.assertFalse(this.setWithFalse.retainAll(BooleanArrayList.newListWith(true, false)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.setWithFalse);

        Assertions.assertFalse(this.emptySet.retainAll(BooleanArrayList.newListWith(true, true)));
        Assertions.assertFalse(this.emptySet.retainAll(BooleanArrayList.newListWith(true, false)));
        Assertions.assertFalse(this.emptySet.retainAll(BooleanArrayList.newListWith(false, false)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.emptySet);
    }

    @Override
    @Test
    public void with()
    {
        super.with();
        MutableBooleanCollection emptySet = this.newWith();
        MutableBooleanCollection set = emptySet.with(false);
        MutableBooleanSet set1 = this.newWith().with(true);
        MutableBooleanSet set2 = this.newWith().with(true).with(false);
        MutableBooleanSet set3 = this.newWith().with(false).with(true);
        Assertions.assertSame(emptySet, set);
        Assertions.assertEquals(this.setWithFalse, set);
        Assertions.assertEquals(this.setWithTrue, set1);
        Assertions.assertEquals(this.setWithTrueFalse, set2);
        Assertions.assertEquals(this.setWithTrueFalse, set3);
        Assertions.assertEquals(BooleanHashSet.newSetWith(true, false), this.setWithTrueFalse.with(true));
    }

    @Override
    @Test
    public void withAll()
    {
        super.withAll();
        MutableBooleanCollection emptySet = this.newWith();
        MutableBooleanCollection set = emptySet.withAll(BooleanArrayList.newListWith(false));
        MutableBooleanSet set1 = this.newWith().withAll(BooleanArrayList.newListWith(true));
        MutableBooleanSet set2 = this.newWith().withAll(BooleanArrayList.newListWith(true, false));
        MutableBooleanSet set3 = this.newWith().withAll(BooleanArrayList.newListWith(true, false));
        Assertions.assertSame(emptySet, set);
        Assertions.assertEquals(this.setWithFalse, set);
        Assertions.assertEquals(this.setWithTrue, set1);
        Assertions.assertEquals(this.setWithTrueFalse, set2);
        Assertions.assertEquals(this.setWithTrueFalse, set3);
        Assertions.assertEquals(BooleanHashSet.newSetWith(true, false), this.setWithTrueFalse.withAll(BooleanHashSet.newSetWith(true, false)));
    }

    @Override
    @Test
    public void without()
    {
        Assertions.assertEquals(BooleanHashSet.newSetWith(true), this.setWithTrueFalse.without(false));
        Assertions.assertSame(this.setWithTrueFalse, this.setWithTrueFalse.without(false));
        Assertions.assertEquals(BooleanHashSet.newSetWith(false), this.newWith(true, false).without(true));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.setWithTrueFalse.without(true));
        Assertions.assertEquals(BooleanHashSet.newSetWith(true), this.setWithTrue.without(false));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.setWithTrue.without(true));
        Assertions.assertEquals(BooleanHashSet.newSetWith(false), this.setWithFalse.without(true));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.setWithFalse.without(false));
        Assertions.assertEquals(new BooleanHashSet(), this.emptySet.without(true));
        Assertions.assertEquals(new BooleanHashSet(), this.emptySet.without(false));
    }

    @Override
    @Test
    public void withoutAll()
    {
        super.withoutAll();
        Assertions.assertEquals(BooleanHashSet.newSetWith(true), this.setWithTrueFalse.withoutAll(BooleanArrayList.newListWith(false, false)));
        Assertions.assertSame(this.setWithTrueFalse, this.setWithTrueFalse.withoutAll(BooleanArrayList.newListWith(false, false)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(false), this.newWith(true, false).withoutAll(BooleanArrayList.newListWith(true, true)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.newWith(true, false).withoutAll(BooleanArrayList.newListWith(true, false)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.setWithTrueFalse.withoutAll(BooleanArrayList.newListWith(true)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(true), this.setWithTrue.withoutAll(BooleanArrayList.newListWith(false)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.setWithTrue.withoutAll(BooleanArrayList.newListWith(true)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.newWith(true).withoutAll(BooleanArrayList.newListWith(false, true)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(false), this.setWithFalse.withoutAll(BooleanArrayList.newListWith(true)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.setWithFalse.withoutAll(BooleanArrayList.newListWith(false)));
        Assertions.assertEquals(BooleanHashSet.newSetWith(), this.newWith(false).withoutAll(BooleanArrayList.newListWith(true, false)));
        Assertions.assertEquals(new BooleanHashSet(), this.emptySet.withoutAll(BooleanArrayList.newListWith(true)));
        Assertions.assertEquals(new BooleanHashSet(), this.emptySet.withoutAll(BooleanArrayList.newListWith(false)));
        Assertions.assertEquals(new BooleanHashSet(), this.emptySet.withoutAll(BooleanArrayList.newListWith(false, true)));
    }

    @Override
    @Test
    public void toArray()
    {
        super.toArray();
        Assertions.assertEquals(0L, this.emptySet.toArray().length);

        Assertions.assertEquals(1L, this.setWithFalse.toArray().length);
        Assertions.assertFalse(this.setWithFalse.toArray()[0]);

        Assertions.assertEquals(1L, this.setWithTrue.toArray().length);
        Assertions.assertTrue(this.setWithTrue.toArray()[0]);

        Assertions.assertEquals(2L, this.setWithTrueFalse.toArray().length);
        Assertions.assertTrue(Arrays.equals(new boolean[]{false, true}, this.setWithTrueFalse.toArray())
                || Arrays.equals(new boolean[]{true, false}, this.setWithTrueFalse.toArray()));
    }

    @Override
    @Test
    public void toList()
    {
        super.toList();
        Assertions.assertEquals(new BooleanArrayList(), this.emptySet.toList());
        Assertions.assertEquals(BooleanArrayList.newListWith(false), this.setWithFalse.toList());
        Assertions.assertEquals(BooleanArrayList.newListWith(true), this.setWithTrue.toList());
        Assertions.assertTrue(BooleanArrayList.newListWith(false, true).equals(this.setWithTrueFalse.toList())
                || BooleanArrayList.newListWith(true, false).equals(this.setWithTrueFalse.toList()));
    }

    @Override
    @Test
    public void toSet()
    {
        super.toSet();
        Assertions.assertEquals(new BooleanHashSet(), this.emptySet.toSet());
        Assertions.assertEquals(BooleanHashSet.newSetWith(false), this.setWithFalse.toSet());
        Assertions.assertEquals(BooleanHashSet.newSetWith(true), this.setWithTrue.toSet());
        Assertions.assertEquals(BooleanHashSet.newSetWith(false, true), this.setWithTrueFalse.toSet());
    }

    @Override
    @Test
    public void toBag()
    {
        Assertions.assertEquals(new BooleanHashBag(), this.emptySet.toBag());
        Assertions.assertEquals(BooleanHashBag.newBagWith(false), this.setWithFalse.toBag());
        Assertions.assertEquals(BooleanHashBag.newBagWith(true), this.setWithTrue.toBag());
        Assertions.assertEquals(BooleanHashBag.newBagWith(false, true), this.setWithTrueFalse.toBag());
    }

    @Override
    @Test
    public void testEquals()
    {
        Assertions.assertNotEquals(this.setWithFalse, this.emptySet);
        Assertions.assertNotEquals(this.setWithFalse, this.setWithTrue);
        Assertions.assertNotEquals(this.setWithFalse, this.setWithTrueFalse);
        Assertions.assertNotEquals(this.setWithTrue, this.emptySet);
        Assertions.assertNotEquals(this.setWithTrue, this.setWithTrueFalse);
        Assertions.assertNotEquals(this.setWithTrueFalse, this.emptySet);
        Verify.assertEqualsAndHashCode(this.newWith(false, true), this.setWithTrueFalse);
        Verify.assertEqualsAndHashCode(this.newWith(true, false), this.setWithTrueFalse);

        Verify.assertPostSerializedEqualsAndHashCode(this.emptySet);
        Verify.assertPostSerializedEqualsAndHashCode(this.setWithFalse);
        Verify.assertPostSerializedEqualsAndHashCode(this.setWithTrue);
        Verify.assertPostSerializedEqualsAndHashCode(this.setWithTrueFalse);
    }

    @Override
    @Test
    public void testHashCode()
    {
        super.testHashCode();
        Assertions.assertEquals(UnifiedSet.newSet().hashCode(), this.emptySet.hashCode());
        Assertions.assertEquals(UnifiedSet.newSetWith(false).hashCode(), this.setWithFalse.hashCode());
        Assertions.assertEquals(UnifiedSet.newSetWith(true).hashCode(), this.setWithTrue.hashCode());
        Assertions.assertEquals(UnifiedSet.newSetWith(true, false).hashCode(), this.setWithTrueFalse.hashCode());
        Assertions.assertEquals(UnifiedSet.newSetWith(false, true).hashCode(), this.setWithTrueFalse.hashCode());
        Assertions.assertNotEquals(UnifiedSet.newSetWith(false).hashCode(), this.setWithTrueFalse.hashCode());
    }

    @Override
    @Test
    public void booleanIterator()
    {
        BooleanIterator booleanIterator0 = this.emptySet.booleanIterator();
        Assertions.assertFalse(booleanIterator0.hasNext());
        Verify.assertThrows(NoSuchElementException.class, (Runnable) booleanIterator0::next);

        BooleanIterator booleanIterator1 = this.setWithFalse.booleanIterator();
        Assertions.assertTrue(booleanIterator1.hasNext());
        Assertions.assertFalse(booleanIterator1.next());
        Assertions.assertFalse(booleanIterator1.hasNext());
        Verify.assertThrows(NoSuchElementException.class, (Runnable) booleanIterator1::next);

        BooleanIterator booleanIterator2 = this.setWithTrue.booleanIterator();
        Assertions.assertTrue(booleanIterator2.hasNext());
        Assertions.assertTrue(booleanIterator2.next());
        Assertions.assertFalse(booleanIterator2.hasNext());
        Verify.assertThrows(NoSuchElementException.class, (Runnable) booleanIterator2::next);

        BooleanIterator booleanIterator3 = this.setWithTrueFalse.booleanIterator();
        BooleanHashSet actual = new BooleanHashSet();
        Assertions.assertTrue(booleanIterator3.hasNext());
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
        this.setWithFalse.forEach(each -> sum[1] += each);
        this.setWithTrue.forEach(each -> sum[2] += each);
        this.setWithTrueFalse.forEach(each -> sum[3] += each);

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
        Assertions.assertEquals(0L, this.setWithFalse.count(BooleanPredicates.isTrue()));
        Assertions.assertEquals(1L, this.setWithFalse.count(BooleanPredicates.isFalse()));
        Assertions.assertEquals(0L, this.setWithTrue.count(BooleanPredicates.isFalse()));
        Assertions.assertEquals(1L, this.setWithTrueFalse.count(BooleanPredicates.isTrue()));
        Assertions.assertEquals(0L, this.setWithTrueFalse.count(BooleanPredicates.and(BooleanPredicates.isFalse(), BooleanPredicates.isTrue())));
        Assertions.assertEquals(1L, this.setWithTrueFalse.count(BooleanPredicates.isFalse()));
        Assertions.assertEquals(1L, this.setWithTrueFalse.count(BooleanPredicates.isTrue()));
        Assertions.assertEquals(2L, this.setWithTrueFalse.count(BooleanPredicates.or(BooleanPredicates.isFalse(), BooleanPredicates.isTrue())));
    }

    @Override
    @Test
    public void anySatisfy()
    {
        super.anySatisfy();
        Assertions.assertFalse(this.emptySet.anySatisfy(BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())));
        Assertions.assertFalse(this.setWithFalse.anySatisfy(BooleanPredicates.isTrue()));
        Assertions.assertTrue(this.setWithFalse.anySatisfy(BooleanPredicates.isFalse()));
        Assertions.assertFalse(this.setWithTrue.anySatisfy(BooleanPredicates.isFalse()));
        Assertions.assertTrue(this.setWithTrue.anySatisfy(BooleanPredicates.isTrue()));
        Assertions.assertTrue(this.setWithTrueFalse.anySatisfy(BooleanPredicates.isTrue()));
        Assertions.assertTrue(this.setWithTrueFalse.anySatisfy(BooleanPredicates.isFalse()));
        Assertions.assertFalse(this.setWithTrueFalse.anySatisfy(BooleanPredicates.and(BooleanPredicates.isFalse(), BooleanPredicates.isTrue())));
    }

    @Override
    @Test
    public void allSatisfy()
    {
        super.allSatisfy();
        Assertions.assertTrue(this.emptySet.allSatisfy(BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())));
        Assertions.assertFalse(this.setWithFalse.allSatisfy(BooleanPredicates.isTrue()));
        Assertions.assertTrue(this.setWithFalse.allSatisfy(BooleanPredicates.isFalse()));
        Assertions.assertFalse(this.setWithTrue.allSatisfy(BooleanPredicates.isFalse()));
        Assertions.assertTrue(this.setWithTrue.allSatisfy(BooleanPredicates.isTrue()));
        Assertions.assertFalse(this.setWithTrueFalse.allSatisfy(BooleanPredicates.isTrue()));
        Assertions.assertFalse(this.setWithTrueFalse.allSatisfy(BooleanPredicates.isFalse()));
        Assertions.assertFalse(this.setWithTrueFalse.allSatisfy(BooleanPredicates.and(BooleanPredicates.isFalse(), BooleanPredicates.isTrue())));
        Assertions.assertTrue(this.setWithTrueFalse.allSatisfy(BooleanPredicates.or(BooleanPredicates.isFalse(), BooleanPredicates.isTrue())));
    }

    @Override
    @Test
    public void noneSatisfy()
    {
        Assertions.assertTrue(this.emptySet.noneSatisfy(BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())));
        Assertions.assertFalse(this.setWithFalse.noneSatisfy(BooleanPredicates.isFalse()));
        Assertions.assertTrue(this.setWithFalse.noneSatisfy(BooleanPredicates.isTrue()));
        Assertions.assertFalse(this.setWithTrue.noneSatisfy(BooleanPredicates.isTrue()));
        Assertions.assertTrue(this.setWithTrue.noneSatisfy(BooleanPredicates.isFalse()));
        Assertions.assertFalse(this.setWithTrueFalse.noneSatisfy(BooleanPredicates.isTrue()));
        Assertions.assertFalse(this.setWithTrueFalse.noneSatisfy(BooleanPredicates.isFalse()));
        Assertions.assertTrue(this.setWithTrueFalse.noneSatisfy(BooleanPredicates.and(BooleanPredicates.isFalse(), BooleanPredicates.isTrue())));
        Assertions.assertFalse(this.setWithTrueFalse.noneSatisfy(BooleanPredicates.or(BooleanPredicates.isFalse(), BooleanPredicates.isTrue())));
    }

    @Override
    @Test
    public void select()
    {
        Verify.assertEmpty(this.emptySet.select(BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())));
        Verify.assertEmpty(this.setWithFalse.select(BooleanPredicates.isTrue()));
        Verify.assertSize(1, this.setWithFalse.select(BooleanPredicates.isFalse()));
        Verify.assertEmpty(this.setWithTrue.select(BooleanPredicates.isFalse()));
        Verify.assertSize(1, this.setWithTrue.select(BooleanPredicates.isTrue()));
        Verify.assertSize(1, this.setWithTrueFalse.select(BooleanPredicates.isFalse()));
        Verify.assertSize(1, this.setWithTrueFalse.select(BooleanPredicates.isTrue()));
        Verify.assertEmpty(this.setWithTrueFalse.select(BooleanPredicates.and(BooleanPredicates.isFalse(), BooleanPredicates.isTrue())));
        Verify.assertSize(2, this.setWithTrueFalse.select(BooleanPredicates.or(BooleanPredicates.isFalse(), BooleanPredicates.isTrue())));
    }

    @Override
    @Test
    public void reject()
    {
        Verify.assertEmpty(this.emptySet.reject(BooleanPredicates.and(BooleanPredicates.isTrue(), BooleanPredicates.isFalse())));
        Verify.assertEmpty(this.setWithTrue.reject(BooleanPredicates.isTrue()));
        Verify.assertSize(1, this.setWithTrue.reject(BooleanPredicates.isFalse()));
        Verify.assertEmpty(this.setWithFalse.reject(BooleanPredicates.isFalse()));
        Verify.assertSize(1, this.setWithFalse.reject(BooleanPredicates.isTrue()));
        Verify.assertSize(1, this.setWithTrueFalse.reject(BooleanPredicates.isFalse()));
        Verify.assertSize(1, this.setWithTrueFalse.reject(BooleanPredicates.isTrue()));
        Verify.assertEmpty(this.setWithTrueFalse.reject(BooleanPredicates.or(BooleanPredicates.isFalse(), BooleanPredicates.isTrue())));
        Verify.assertSize(2, this.setWithTrueFalse.reject(BooleanPredicates.and(BooleanPredicates.isFalse(), BooleanPredicates.isTrue())));
    }

    @Override
    @Test
    public void detectIfNone()
    {
        super.detectIfNone();
        Assertions.assertTrue(this.emptySet.detectIfNone(BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.isFalse()), true));
        Assertions.assertFalse(this.emptySet.detectIfNone(BooleanPredicates.or(BooleanPredicates.isTrue(), BooleanPredicates.isFalse()), false));
        Assertions.assertTrue(this.setWithFalse.detectIfNone(BooleanPredicates.isTrue(), true));
        Assertions.assertFalse(this.setWithFalse.detectIfNone(BooleanPredicates.isTrue(), false));
        Assertions.assertFalse(this.setWithFalse.detectIfNone(BooleanPredicates.isFalse(), true));
        Assertions.assertFalse(this.setWithFalse.detectIfNone(BooleanPredicates.isFalse(), false));
        Assertions.assertTrue(this.setWithTrue.detectIfNone(BooleanPredicates.isFalse(), true));
        Assertions.assertFalse(this.setWithTrue.detectIfNone(BooleanPredicates.isFalse(), false));
        Assertions.assertTrue(this.setWithTrue.detectIfNone(BooleanPredicates.isTrue(), true));
        Assertions.assertTrue(this.setWithTrue.detectIfNone(BooleanPredicates.isTrue(), false));
        Assertions.assertTrue(this.setWithTrueFalse.detectIfNone(BooleanPredicates.and(BooleanPredicates.isFalse(), BooleanPredicates.isTrue()), true));
        Assertions.assertFalse(this.setWithTrueFalse.detectIfNone(BooleanPredicates.and(BooleanPredicates.isFalse(), BooleanPredicates.isTrue()), false));
        Assertions.assertFalse(this.setWithTrueFalse.detectIfNone(BooleanPredicates.isFalse(), true));
        Assertions.assertTrue(this.setWithTrueFalse.detectIfNone(BooleanPredicates.isTrue(), false));
    }

    @Override
    @Test
    public void collect()
    {
        super.collect();
        BooleanToObjectFunction<Boolean> function = parameter -> !parameter;
        Assertions.assertEquals(UnifiedSet.newSetWith(true, false), this.setWithTrueFalse.collect(function));
        Assertions.assertEquals(UnifiedSet.newSetWith(false), this.setWithTrue.collect(function));
        Assertions.assertEquals(UnifiedSet.newSetWith(true), this.setWithFalse.collect(function));
        Assertions.assertEquals(UnifiedSet.newSetWith(), this.emptySet.collect(function));
    }

    @Override
    @Test
    public void testToString()
    {
        super.testToString();
        Assertions.assertEquals("[]", this.emptySet.toString());
        Assertions.assertEquals("[false]", this.setWithFalse.toString());
        Assertions.assertEquals("[true]", this.setWithTrue.toString());
        Assertions.assertTrue("[true, false]".equals(this.setWithTrueFalse.toString())
                || "[false, true]".equals(this.setWithTrueFalse.toString()));
    }

    @Override
    @Test
    public void makeString()
    {
        super.makeString();
        Assertions.assertEquals("", this.emptySet.makeString());
        Assertions.assertEquals("false", this.setWithFalse.makeString());
        Assertions.assertEquals("true", this.setWithTrue.makeString());
        Assertions.assertTrue("true, false".equals(this.setWithTrueFalse.makeString())
                || "false, true".equals(this.setWithTrueFalse.makeString()));

        Assertions.assertEquals("", this.emptySet.makeString("/"));
        Assertions.assertEquals("false", this.setWithFalse.makeString("/"));
        Assertions.assertEquals("true", this.setWithTrue.makeString("/"));
        Assertions.assertTrue("true/false".equals(this.setWithTrueFalse.makeString("/"))
                || "false/true".equals(this.setWithTrueFalse.makeString("/")), this.setWithTrueFalse.makeString("/"));

        Assertions.assertEquals("[]", this.emptySet.makeString("[", "/", "]"));
        Assertions.assertEquals("[false]", this.setWithFalse.makeString("[", "/", "]"));
        Assertions.assertEquals("[true]", this.setWithTrue.makeString("[", "/", "]"));
        Assertions.assertTrue("[true/false]".equals(this.setWithTrueFalse.makeString("[", "/", "]"))
                || "[false/true]".equals(this.setWithTrueFalse.makeString("[", "/", "]")), this.setWithTrueFalse.makeString("[", "/", "]"));
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
        this.setWithFalse.appendString(appendable1);
        Assertions.assertEquals("false", appendable1.toString());

        StringBuilder appendable2 = new StringBuilder();
        this.setWithTrue.appendString(appendable2);
        Assertions.assertEquals("true", appendable2.toString());

        StringBuilder appendable3 = new StringBuilder();
        this.setWithTrueFalse.appendString(appendable3);
        Assertions.assertTrue("true, false".equals(appendable3.toString())
                || "false, true".equals(appendable3.toString()));

        StringBuilder appendable4 = new StringBuilder();
        this.setWithTrueFalse.appendString(appendable4, "[", ", ", "]");
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
        Assertions.assertEquals(this.setWithFalse, this.setWithFalse.asLazy().toSet());
        Assertions.assertEquals(this.setWithTrue, this.setWithTrue.asLazy().toSet());
        Assertions.assertEquals(this.setWithTrueFalse, this.setWithTrueFalse.asLazy().toSet());
    }

    @Override
    @Test
    public void asSynchronized()
    {
        super.asSynchronized();
        Verify.assertInstanceOf(SynchronizedBooleanSet.class, this.emptySet.asSynchronized());
        Assertions.assertEquals(new SynchronizedBooleanSet(this.emptySet), this.emptySet.asSynchronized());
        Assertions.assertEquals(new SynchronizedBooleanSet(this.setWithFalse), this.setWithFalse.asSynchronized());
        Assertions.assertEquals(new SynchronizedBooleanSet(this.setWithTrue), this.setWithTrue.asSynchronized());
        Assertions.assertEquals(new SynchronizedBooleanSet(this.setWithTrueFalse), this.setWithTrueFalse.asSynchronized());
    }

    @Override
    @Test
    public void asUnmodifiable()
    {
        super.asUnmodifiable();
        Verify.assertInstanceOf(UnmodifiableBooleanSet.class, this.emptySet.asUnmodifiable());
        Assertions.assertEquals(new UnmodifiableBooleanSet(this.emptySet), this.emptySet.asUnmodifiable());
        Assertions.assertEquals(new UnmodifiableBooleanSet(this.setWithFalse), this.setWithFalse.asUnmodifiable());
        Assertions.assertEquals(new UnmodifiableBooleanSet(this.setWithTrue), this.setWithTrue.asUnmodifiable());
        Assertions.assertEquals(new UnmodifiableBooleanSet(this.setWithTrueFalse), this.setWithTrueFalse.asUnmodifiable());
    }
}
