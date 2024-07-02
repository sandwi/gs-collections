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

package com.gs.collections.impl.bimap.mutable;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.gs.collections.api.bimap.BiMap;
import com.gs.collections.api.bimap.ImmutableBiMap;
import com.gs.collections.api.bimap.MutableBiMap;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.multimap.set.MutableSetMultimap;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.impl.IntegerWithCast;
import com.gs.collections.impl.factory.BiMaps;
import com.gs.collections.impl.map.mutable.MutableMapIterableTestCase;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.multimap.set.UnifiedSetMultimap;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class AbstractMutableBiMapTestCase extends MutableMapIterableTestCase
{
    public abstract MutableBiMap<Integer, Character> classUnderTest();

    public abstract MutableBiMap<Integer, Character> getEmptyMap();

    @Override
    protected abstract <K, V> MutableBiMap<K, V> newMap();

    @Override
    protected abstract <K, V> MutableBiMap<K, V> newMapWithKeyValue(K key, V value);

    @Override
    protected abstract <K, V> MutableBiMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2);

    @Override
    protected abstract <K, V> MutableBiMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3);

    @Override
    protected abstract <K, V> MutableBiMap<K, V> newMapWithKeysValues(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4);

    public static void assertBiMapsEqual(BiMap<?, ?> expected, BiMap<?, ?> actual)
    {
        Assertions.assertEquals(expected, actual);
        Assertions.assertEquals(expected.inverse(), actual.inverse());
    }

    @Test
    @Override
    public void flip()
    {
        Verify.assertEmpty(this.newMap().flip());

        MutableSetMultimap<Integer, String> expected = UnifiedSetMultimap.newMultimap();
        expected.put(1, "One");
        expected.put(2, "Two");
        expected.put(3, "Three");
        expected.put(4, "Four");

        Assertions.assertEquals(
                expected,
                this.newMapWithKeysValues("One", 1, "Two", 2, "Three", 3, "Four", 4).flip());
    }

    @Test
    public void size()
    {
        Verify.assertSize(3, this.classUnderTest());
        Verify.assertSize(0, this.getEmptyMap());
    }

    @Test
    public void forcePut()
    {
        MutableBiMap<Integer, Character> biMap = this.classUnderTest();
        Assertions.assertNull(biMap.forcePut(4, 'd'));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, null, null, 'b', 3, 'c', 4, 'd'), biMap);
        Assertions.assertEquals(UnifiedMap.newWithKeysValues(1, null, null, 'b', 3, 'c', 4, 'd'), biMap);

        Assertions.assertNull(biMap.forcePut(1, null));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, null, null, 'b', 3, 'c', 4, 'd'), biMap);

        Assertions.assertNull(biMap.forcePut(1, 'e'));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, 'e', null, 'b', 3, 'c', 4, 'd'), biMap);

        Assertions.assertNull(biMap.forcePut(5, 'e'));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(5, 'e', null, 'b', 3, 'c', 4, 'd'), biMap);

        Assertions.assertEquals(Character.valueOf('d'), biMap.forcePut(4, 'e'));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(4, 'e', null, 'b', 3, 'c'), biMap);

        HashBiMap<Integer, Character> actual = HashBiMap.newMap();
        actual.forcePut(1, null);
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, null), actual);
    }

    @Override
    @Test
    public void put()
    {
        MutableBiMap<Integer, Character> biMap = this.classUnderTest();
        Assertions.assertNull(biMap.put(4, 'd'));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, null, null, 'b', 3, 'c', 4, 'd'), biMap);
        Assertions.assertEquals(UnifiedMap.newWithKeysValues(1, null, null, 'b', 3, 'c', 4, 'd'), biMap);

        Assertions.assertNull(biMap.put(1, null));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, null, null, 'b', 3, 'c', 4, 'd'), biMap);

        Assertions.assertNull(biMap.put(1, 'e'));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, 'e', null, 'b', 3, 'c', 4, 'd'), biMap);

        Verify.assertThrows(IllegalArgumentException.class, () -> biMap.put(5, 'e'));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, 'e', null, 'b', 3, 'c', 4, 'd'), biMap);

        Verify.assertThrows(IllegalArgumentException.class, () -> biMap.put(4, 'e'));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, 'e', null, 'b', 3, 'c', 4, 'd'), biMap);

        HashBiMap<Integer, Character> actual = HashBiMap.newMap();
        actual.put(1, null);
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, null), actual);
    }

    @Override
    @Test
    public void flipUniqueValues()
    {
        MutableBiMap<Integer, Character> map = this.classUnderTest();
        MutableBiMap<Character, Integer> result = map.flipUniqueValues();
        Assertions.assertEquals(map.inverse(), result);
        Assertions.assertNotSame(map.inverse(), result);
        result.put('d', 4);
        Assertions.assertEquals(this.classUnderTest(), map);
    }

    @Test
    public void get()
    {
        MutableBiMap<Integer, Character> biMap = this.classUnderTest();
        Assertions.assertNull(biMap.get(1));
        Assertions.assertEquals(Character.valueOf('b'), biMap.get(null));
        Assertions.assertEquals(Character.valueOf('c'), biMap.get(3));
        Assertions.assertNull(biMap.get(4));

        Assertions.assertNull(biMap.put(4, 'd'));
        Assertions.assertNull(biMap.get(1));
        Assertions.assertEquals(Character.valueOf('b'), biMap.get(null));
        Assertions.assertEquals(Character.valueOf('c'), biMap.get(3));
        Assertions.assertEquals(Character.valueOf('d'), biMap.get(4));

        Assertions.assertNull(biMap.put(1, null));
        Assertions.assertNull(biMap.get(1));
        Assertions.assertEquals(Character.valueOf('b'), biMap.get(null));
        Assertions.assertEquals(Character.valueOf('c'), biMap.get(3));
        Assertions.assertEquals(Character.valueOf('d'), biMap.get(4));

        Assertions.assertNull(biMap.forcePut(1, 'e'));
        Assertions.assertEquals(Character.valueOf('e'), biMap.get(1));
        Assertions.assertEquals(Character.valueOf('b'), biMap.get(null));
        Assertions.assertEquals(Character.valueOf('c'), biMap.get(3));
        Assertions.assertEquals(Character.valueOf('d'), biMap.get(4));

        Assertions.assertNull(biMap.forcePut(5, 'e'));
        Assertions.assertNull(biMap.get(1));
        Assertions.assertEquals(Character.valueOf('e'), biMap.get(5));
        Assertions.assertEquals(Character.valueOf('b'), biMap.get(null));
        Assertions.assertEquals(Character.valueOf('c'), biMap.get(3));
        Assertions.assertEquals(Character.valueOf('d'), biMap.get(4));

        Assertions.assertEquals(Character.valueOf('d'), biMap.forcePut(4, 'e'));
        Assertions.assertNull(biMap.get(1));
        Assertions.assertNull(biMap.get(5));
        Assertions.assertEquals(Character.valueOf('b'), biMap.get(null));
        Assertions.assertEquals(Character.valueOf('c'), biMap.get(3));
        Assertions.assertEquals(Character.valueOf('e'), biMap.get(4));

        HashBiMap<Integer, Character> actual = HashBiMap.newMap();
        Assertions.assertNull(actual.get(1));
        actual.put(1, null);
        Assertions.assertNull(actual.get(1));
    }

    @Override
    @Test
    public void containsKey()
    {
        super.containsKey();

        MutableBiMap<Integer, Character> biMap = this.classUnderTest();

        Assertions.assertTrue(biMap.containsKey(1));
        Assertions.assertTrue(biMap.containsKey(null));
        Assertions.assertTrue(biMap.containsKey(3));
        Assertions.assertFalse(biMap.containsKey(4));

        Assertions.assertNull(biMap.put(4, 'd'));
        Assertions.assertTrue(biMap.containsKey(1));
        Assertions.assertTrue(biMap.containsKey(null));
        Assertions.assertTrue(biMap.containsKey(3));
        Assertions.assertTrue(biMap.containsKey(4));

        Assertions.assertNull(biMap.put(1, null));
        Assertions.assertTrue(biMap.containsKey(1));
        Assertions.assertTrue(biMap.containsKey(null));
        Assertions.assertTrue(biMap.containsKey(3));
        Assertions.assertTrue(biMap.containsKey(4));

        Assertions.assertNull(biMap.forcePut(1, 'e'));
        Assertions.assertTrue(biMap.containsKey(1));
        Assertions.assertTrue(biMap.containsKey(null));
        Assertions.assertTrue(biMap.containsKey(3));
        Assertions.assertTrue(biMap.containsKey(4));

        Assertions.assertNull(biMap.forcePut(5, 'e'));
        Assertions.assertFalse(biMap.containsKey(1));
        Assertions.assertTrue(biMap.containsKey(5));
        Assertions.assertTrue(biMap.containsKey(null));
        Assertions.assertTrue(biMap.containsKey(3));
        Assertions.assertTrue(biMap.containsKey(4));

        Assertions.assertEquals(Character.valueOf('d'), biMap.forcePut(4, 'e'));
        Assertions.assertFalse(biMap.containsKey(1));
        Assertions.assertTrue(biMap.containsKey(null));
        Assertions.assertTrue(biMap.containsKey(3));
        Assertions.assertTrue(biMap.containsKey(4));
        Assertions.assertFalse(biMap.containsKey(5));

        HashBiMap<Integer, Character> actual = HashBiMap.newMap();
        actual.put(1, null);
        Assertions.assertTrue(actual.containsKey(1));
        Assertions.assertFalse(actual.containsKey(0));
    }

    @Override
    @Test
    public void containsValue()
    {
        super.containsValue();

        MutableBiMap<Integer, Character> biMap = this.classUnderTest();

        Assertions.assertTrue(biMap.containsValue(null));
        Assertions.assertTrue(biMap.containsValue('b'));
        Assertions.assertTrue(biMap.containsValue('c'));
        Assertions.assertFalse(biMap.containsValue('d'));

        Assertions.assertNull(biMap.put(4, 'd'));
        Assertions.assertTrue(biMap.containsValue(null));
        Assertions.assertTrue(biMap.containsValue('b'));
        Assertions.assertTrue(biMap.containsValue('c'));
        Assertions.assertTrue(biMap.containsValue('d'));

        Assertions.assertNull(biMap.put(1, null));
        Assertions.assertTrue(biMap.containsValue(null));
        Assertions.assertTrue(biMap.containsValue('b'));
        Assertions.assertTrue(biMap.containsValue('c'));
        Assertions.assertTrue(biMap.containsValue('d'));

        Assertions.assertNull(biMap.forcePut(1, 'e'));
        Assertions.assertTrue(biMap.containsValue('e'));
        Assertions.assertFalse(biMap.containsValue(null));
        Assertions.assertTrue(biMap.containsValue('b'));
        Assertions.assertTrue(biMap.containsValue('c'));
        Assertions.assertTrue(biMap.containsValue('d'));

        Assertions.assertNull(biMap.forcePut(5, 'e'));
        Assertions.assertFalse(biMap.containsValue(null));
        Assertions.assertTrue(biMap.containsValue('e'));
        Assertions.assertTrue(biMap.containsValue('b'));
        Assertions.assertTrue(biMap.containsValue('c'));
        Assertions.assertTrue(biMap.containsValue('d'));

        Assertions.assertEquals(Character.valueOf('d'), biMap.forcePut(4, 'e'));
        Assertions.assertFalse(biMap.containsValue(null));
        Assertions.assertTrue(biMap.containsValue('e'));
        Assertions.assertTrue(biMap.containsValue('b'));
        Assertions.assertTrue(biMap.containsValue('c'));
        Assertions.assertFalse(biMap.containsValue('d'));

        HashBiMap<Integer, Character> actual = HashBiMap.newMap();
        actual.put(1, null);
        Assertions.assertTrue(actual.containsValue(null));
        Assertions.assertFalse(actual.containsValue('\0'));
    }

    @Override
    @Test
    public void putAll()
    {
        MutableBiMap<Integer, Character> biMap = this.classUnderTest();
        biMap.putAll(UnifiedMap.<Integer, Character>newMap());
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, null, null, 'b', 3, 'c'), biMap);

        biMap.putAll(UnifiedMap.newWithKeysValues(1, null, null, 'b', 3, 'c'));
        HashBiMap<Integer, Character> expected = HashBiMap.newWithKeysValues(1, null, null, 'b', 3, 'c');
        Assertions.assertEquals(expected, biMap);

        biMap.putAll(UnifiedMap.newWithKeysValues(4, 'd', 5, 'e', 6, 'f'));
        expected.put(4, 'd');
        expected.put(5, 'e');
        expected.put(6, 'f');
        Assertions.assertEquals(expected, biMap);
    }

    @Test
    public void remove()
    {
        MutableBiMap<Integer, Character> biMap = this.classUnderTest();
        Assertions.assertNull(biMap.remove(4));
        Verify.assertSize(3, biMap);
        Assertions.assertNull(biMap.remove(1));
        Assertions.assertNull(biMap.get(1));
        Assertions.assertNull(biMap.inverse().get(null));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(null, 'b', 3, 'c'), biMap);

        Assertions.assertEquals(Character.valueOf('b'), biMap.remove(null));
        Assertions.assertNull(biMap.get(null));
        Assertions.assertNull(biMap.inverse().get('b'));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(3, 'c'), biMap);

        Assertions.assertEquals(Character.valueOf('c'), biMap.remove(3));
        Assertions.assertNull(biMap.get(3));
        Assertions.assertNull(biMap.inverse().get('c'));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newMap(), biMap);
        Verify.assertEmpty(biMap);

        Assertions.assertNull(HashBiMap.newMap().remove(1));
    }

    @Override
    @Test
    public void clear()
    {
        MutableBiMap<Integer, Character> biMap = this.classUnderTest();
        biMap.clear();
        Verify.assertEmpty(biMap);
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newMap(), biMap);
    }

    @Test
    public void testToString()
    {
        Assertions.assertEquals("{}", this.getEmptyMap().toString());
        String actualString = HashBiMap.newWithKeysValues(1, null, 2, 'b').toString();
        Assertions.assertTrue("{1=null, 2=b}".equals(actualString) || "{2=b, 1=null}".equals(actualString));
    }

    @Override
    @Test
    public void equalsAndHashCode()
    {
        super.equalsAndHashCode();

        MutableBiMap<Integer, Character> emptyMap = this.getEmptyMap();
        Verify.assertEqualsAndHashCode(UnifiedMap.newMap(), emptyMap);
        Assertions.assertEquals(emptyMap, emptyMap);
        Verify.assertEqualsAndHashCode(UnifiedMap.newWithKeysValues(1, null, null, 'b', 3, 'c'), this.classUnderTest());
        Verify.assertEqualsAndHashCode(UnifiedMap.newWithKeysValues(null, 'b', 1, null, 3, 'c'), this.classUnderTest());
        Assertions.assertNotEquals(HashBiMap.newWithKeysValues(null, 1, 'b', null, 'c', 3), this.classUnderTest());
        Verify.assertEqualsAndHashCode(HashBiMap.newWithKeysValues(null, 1, 'b', null, 'c', 3), this.classUnderTest().inverse());
    }

    @Override
    @Test
    public void nullCollisionWithCastInEquals()
    {
        MutableBiMap<IntegerWithCast, String> mutableMap = this.newMap();
        mutableMap.put(new IntegerWithCast(0), "Test 2");
        mutableMap.forcePut(new IntegerWithCast(0), "Test 3");
        mutableMap.put(null, "Test 1");
        Assertions.assertEquals(
                this.newMapWithKeysValues(
                        new IntegerWithCast(0), "Test 3",
                        null, "Test 1"),
                mutableMap);
        Assertions.assertEquals("Test 3", mutableMap.get(new IntegerWithCast(0)));
        Assertions.assertEquals("Test 1", mutableMap.get(null));
    }

    @Override
    @Test
    public void iterator()
    {
        MutableSet<Character> expected = UnifiedSet.newSetWith(null, 'b', 'c');
        MutableSet<Character> actual = UnifiedSet.newSet();
        MutableBiMap<Integer, Character> biMap = this.classUnderTest();
        Iterator<Character> iterator = biMap.iterator();
        Assertions.assertTrue(iterator.hasNext());
        Verify.assertThrows(IllegalStateException.class, iterator::remove);
        Verify.assertSize(3, biMap);
        Verify.assertSize(3, biMap.inverse());
        for (int i = 0; i < 3; i++)
        {
            Assertions.assertTrue(iterator.hasNext());
            actual.add(iterator.next());
        }
        Assertions.assertEquals(expected, actual);
        Assertions.assertFalse(iterator.hasNext());
        Verify.assertThrows(NoSuchElementException.class, (Runnable) iterator::next);

        Iterator<Character> iteratorRemove = biMap.iterator();

        Assertions.assertTrue(iteratorRemove.hasNext());
        Character first = iteratorRemove.next();
        iteratorRemove.remove();
        MutableBiMap<Integer, Character> expectedMap = this.classUnderTest();
        expectedMap.inverse().remove(first);
        Assertions.assertEquals(expectedMap, biMap);
        Assertions.assertEquals(expectedMap.inverse(), biMap.inverse());
        Verify.assertSize(2, biMap);
        Verify.assertSize(2, biMap.inverse());

        Assertions.assertTrue(iteratorRemove.hasNext());
        Character second = iteratorRemove.next();
        iteratorRemove.remove();
        expectedMap.inverse().remove(second);
        Assertions.assertEquals(expectedMap, biMap);
        Assertions.assertEquals(expectedMap.inverse(), biMap.inverse());
        Verify.assertSize(1, biMap);
        Verify.assertSize(1, biMap.inverse());

        Assertions.assertTrue(iteratorRemove.hasNext());
        Character third = iteratorRemove.next();
        iteratorRemove.remove();
        expectedMap.inverse().remove(third);
        Assertions.assertEquals(expectedMap, biMap);
        Assertions.assertEquals(expectedMap.inverse(), biMap.inverse());
        Verify.assertEmpty(biMap);
        Verify.assertEmpty(biMap.inverse());

        Assertions.assertFalse(iteratorRemove.hasNext());
        Verify.assertThrows(NoSuchElementException.class, (Runnable) iteratorRemove::next);
    }

    @Override
    @Test
    public void updateValueWith()
    {
        MutableBiMap<Integer, Character> biMap = this.classUnderTest();
        Function2<Character, Boolean, Character> toUpperOrLowerCase = (character, parameter) -> parameter ? Character.toUpperCase(character) : Character.toLowerCase(character);
        Assertions.assertEquals(Character.valueOf('D'), biMap.updateValueWith(4, () -> 'd', toUpperOrLowerCase, true));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, null, null, 'b', 3, 'c', 4, 'D'), biMap);
        Assertions.assertEquals(Character.valueOf('B'), biMap.updateValueWith(null, () -> 'd', toUpperOrLowerCase, true));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, null, null, 'B', 3, 'c', 4, 'D'), biMap);
        Assertions.assertEquals(Character.valueOf('d'), biMap.updateValueWith(4, () -> 'x', toUpperOrLowerCase, false));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, null, null, 'B', 3, 'c', 4, 'd'), biMap);
    }

    @Override
    @Test
    public void updateValue()
    {
        MutableBiMap<Integer, Character> biMap = this.classUnderTest();
        Assertions.assertEquals(Character.valueOf('D'), biMap.updateValue(4, () -> 'd', Character::toUpperCase));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, null, null, 'b', 3, 'c', 4, 'D'), biMap);
        Assertions.assertEquals(Character.valueOf('B'), biMap.updateValue(null, () -> 'd', Character::toUpperCase));
        AbstractMutableBiMapTestCase.assertBiMapsEqual(HashBiMap.newWithKeysValues(1, null, null, 'B', 3, 'c', 4, 'D'), biMap);
    }

    @Override
    @Test
    public void updateValue_collisions()
    {
        // testing collisions not applicable here
    }

    @Override
    @Test
    public void updateValueWith_collisions()
    {
        // testing collisions not applicable here
    }

    @Override
    @Test
    public void toImmutable()
    {
        ImmutableBiMap<Integer, Character> expectedImmutableBiMap = BiMaps.immutable.of(null, 'b', 1, null, 3, 'c');
        ImmutableBiMap<Integer, Character> characters = this.classUnderTest().toImmutable();
        Assertions.assertEquals(expectedImmutableBiMap, characters);
    }

    @Override
    @Test
    public void asSynchronized()
    {
        assertThrows(UnsupportedOperationException.class, () -> {
            //asSynchronized not implemented yet
            this.classUnderTest().asSynchronized();
        });
    }

    @Test
    public void testClone()
    {
        MutableBiMap<Integer, String> map = this.newMapWithKeysValues(1, "One", 2, "Two");
        MutableBiMap<Integer, String> clone = map.clone();
        Assertions.assertNotSame(map, clone);
        Verify.assertEqualsAndHashCode(map, clone);
    }
}
