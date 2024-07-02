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

package com.gs.collections.impl.test;

import java.util.Collection;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import com.gs.collections.api.multimap.list.MutableListMultimap;
import com.gs.collections.impl.bag.mutable.HashBag;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.IntegerPredicates;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.factory.Maps;
import com.gs.collections.impl.factory.Sets;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.primitive.IntArrayList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.multimap.list.FastListMultimap;
import com.gs.collections.impl.set.mutable.UnifiedSet;
import com.gs.collections.impl.set.sorted.mutable.TreeSortedSet;
import com.gs.collections.impl.tuple.Tuples;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * JUnit test for our extensions to JUnit.  These tests make sure that methods in {@link Verify} really fail when they
 * ought to.
 */
public class VerifyTest
{
    @Test
    public void assertThrowsWithCause()
    {
        Verify.assertThrowsWithCause(RuntimeException.class, NullPointerException.class, new Callable<Void>()
        {
            public Void call() throws Exception
            {
                throw new RuntimeException(new NullPointerException());
            }
        });
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertThrowsWithCause(RuntimeException.class, NullPointerException.class, new Callable<Void>()
                {
                    public Void call() throws Exception
                    {
                        return null;
                    }
                });
            }
        });
    }

    @Test
    public void assertBefore()
    {
        Verify.assertBefore(Integer.valueOf(1), Integer.valueOf(2), FastList.newListWith(1, 2), "numbers");
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertBefore(Integer.valueOf(2), Integer.valueOf(1), FastList.newListWith(1, 2), "numbers");
            }
        });
    }

    @Test
    public void assertEndsWithArray()
    {
        Verify.assertEndsWith(new Integer[]{1, 2, 3, 4}, 3, 4);
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertEndsWith(new Integer[]{1, 2, 3, 4}, 3, 2);
            }
        });
    }

    @Test
    public void assertStartsWithArray()
    {
        Verify.assertStartsWith(new Integer[]{1, 2, 3, 4}, 1, 2);
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertStartsWith(new Integer[]{1, 2, 3, 4}, 3, 2);
            }
        });
    }

    @Test
    public void assertStartsWithList()
    {
        Verify.assertStartsWith(FastList.newListWith(1, 2, 3, 4), 1, 2);
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertStartsWith(FastList.newListWith(1, 2, 3, 4), 3, 2);
            }
        });
    }

    @Test
    public void assertEndsWithList()
    {
        Verify.assertEndsWith(FastList.newListWith(1, 2, 3, 4), 3, 4);
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertEndsWith(FastList.newListWith(1, 2, 3, 4), 3, 2);
            }
        });
    }

    @Test
    public void assertNotEqualsString()
    {
        Verify.assertNotEquals("yes", "no");
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertNotEquals("yes", "yes");
            }
        });
    }

    @Test
    public void assertNotEqualsDouble()
    {
        Verify.assertNotEquals(0.5d, 0.6d, 0.0001);
        Verify.assertNotEquals(0.5d, 0.6d, 0.0001, "message");
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertNotEquals(0.5d, 0.5d, 0.0001);
            }
        });
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertNotEquals(0.5d, 0.5d, 0.0001, "message");
            }
        });
    }

    @Test
    public void assertNotEqualsFloat()
    {
        Verify.assertNotEquals(0.5f, 0.6f, 0.0001f);
        Verify.assertNotEquals(0.5f, 0.6f, 0.0001f, "message");
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertNotEquals(0.5f, 0.5f, 0.0001f);
            }
        });
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertNotEquals(0.5f, 0.5f, 0.0001f, "message");
            }
        });
    }

    @Test
    public void assertNotEqualsLong()
    {
        Verify.assertNotEquals(5L, 6L);
        Verify.assertNotEquals(5L, 6L, "message");
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertNotEquals(5L, 5L);
            }
        });
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertNotEquals(5L, 5L, "message");
            }
        });
    }

    @Test
    public void assertNotEqualsBoolean()
    {
        Verify.assertNotEquals(true, false);
        Verify.assertNotEquals(true, false, "message");
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertNotEquals(true, true);
            }
        });
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertNotEquals(true, true, "message");
            }
        });
    }

    @Test
    public void assertNotEqualsByte()
    {
        Verify.assertNotEquals((byte) 1, (byte) 2);
        Verify.assertNotEquals((byte) 1, (byte) 2, "message");
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertNotEquals((byte) 1, (byte) 1);
            }
        });
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertNotEquals((byte) 1, (byte) 1, "message");
            }
        });
    }

    @Test
    public void assertNotEqualsChar()
    {
        Verify.assertNotEquals((char) 1, (char) 2);
        Verify.assertNotEquals((char) 1, (char) 2, "message");
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertNotEquals((char) 1, (char) 1);
            }
        });
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertNotEquals((char) 1, (byte) 1, "message");
            }
        });
    }

    @Test
    public void assertNotEqualsShort()
    {
        Verify.assertNotEquals((short) 1, (short) 2);
        Verify.assertNotEquals((short) 1, (short) 2, "message");
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertNotEquals((short) 1, (short) 1);
            }
        });
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertNotEquals((short) 1, (short) 1, "message");
            }
        });
    }

    @Test
    public void assertNotEqualsInt()
    {
        Verify.assertNotEquals(1, 2);
        Verify.assertNotEquals(1, 2, "message");
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertNotEquals(1, 1);
            }
        });
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertNotEquals(1, 1, "message");
            }
        });
    }

    @Test
    public void assertNotContainsString()
    {
        Verify.assertNotContains("0", "1");
        Verify.assertNotContains("1", "0", "message");
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertNotContains("1", "1");
            }
        });
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertNotContains("1", "1", "message");
            }
        });
    }

    @Test
    public void assertListsEqual()
    {
        Verify.assertListsEqual(FastList.newListWith(1, 2, 3), FastList.newListWith(1, 2, 3));
        Verify.assertListsEqual(FastList.newListWith(1, 2, 3), FastList.newListWith(1, 2, 3), "message");
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertListsEqual(FastList.newListWith(1, 2, 3), FastList.newListWith(1, 2));
            }
        });
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertListsEqual(FastList.newListWith(1, 2, 3), FastList.newListWith(1, 2), "message");
            }
        });
    }

    @Test
    public void assertBagsEqual()
    {
        Verify.assertBagsEqual(HashBag.newBagWith(1, 1, 2, 2), HashBag.newBagWith(1, 2, 2, 1));
        Verify.assertBagsEqual(HashBag.newBagWith(1, 1, 2, 2), HashBag.newBagWith(1, 1, 2, 2), "message");
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertBagsEqual(HashBag.newBagWith(1, 1, 2, 2), HashBag.newBagWith(1, 1, 2, 2, 3, 3));
            }
        });
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertBagsEqual(HashBag.newBagWith(1, 1, 2, 2, 3, 3), HashBag.newBagWith(1, 1, 2, 2), "message");
            }
        });
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertBagsEqual(HashBag.newBagWith(1, 1, 2, 2, 4, 4), HashBag.newBagWith(1, 1, 2, 2, 3, 3), "message");
            }
        });
    }

    @Test
    public void assertSetsEqual()
    {
        Verify.assertSetsEqual(UnifiedSet.newSetWith(1, 2, 3), UnifiedSet.newSetWith(1, 2, 3));
        Verify.assertSetsEqual(UnifiedSet.newSetWith(1, 2, 3), UnifiedSet.newSetWith(1, 2, 3), "message");
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertSetsEqual(UnifiedSet.newSetWith(1, 2, 3), UnifiedSet.newSetWith(1, 2));
            }
        });
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertSetsEqual(UnifiedSet.newSetWith(1, 2, 3), UnifiedSet.newSetWith(1, 2), "message");
            }
        });
    }

    @Test
    public void assertMapsEqual()
    {
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues(1, 1, 2, 2), UnifiedMap.newWithKeysValues(1, 1, 2, 2));
        Verify.assertMapsEqual(UnifiedMap.newWithKeysValues(1, 1, 2, 2), UnifiedMap.newWithKeysValues(1, 1, 2, 2), "message");
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertMapsEqual(UnifiedMap.newWithKeysValues(1, 1, 2, 2), UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3));
            }
        });
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertMapsEqual(UnifiedMap.newWithKeysValues(1, 1, 2, 2), UnifiedMap.newWithKeysValues(1, 1, 2, 2, 3, 3), "message");
            }
        });
    }

    @Test
    public void assertIterablesEqual()
    {
        Verify.assertIterablesEqual(FastList.newListWith(1, 2, 3), TreeSortedSet.newSetWith(1, 2, 3));
        Verify.assertIterablesEqual(FastList.newListWith(1, 2, 3), TreeSortedSet.newSetWith(1, 2, 3), "message");
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertIterablesEqual(FastList.newListWith(1, 2, 3), FastList.newListWith(1, 2));
            }
        });
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertIterablesEqual(FastList.newListWith(1, 2, 3), FastList.newListWith(1, 2), "message");
            }
        });
    }

    @Test
    public void assertError()
    {
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                throw new AssertionError();
            }
        });
        Verify.assertError(AssertionError.class, new Runnable()
        {
            public void run()
            {
                Verify.assertError(AssertionError.class, new Runnable()
                {
                    public void run()
                    {
                        // do nothing
                    }
                });
            }
        });
    }

    @Test
    public void shallowClone1()
    {
        try
        {
            Cloneable unclonable = new Cloneable()
            {
            };
            Verify.assertShallowClone(unclonable);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void shallowClone2()
    {
        Cloneable simpleCloneable = new SimpleCloneable();
        Verify.assertShallowClone(simpleCloneable);
    }

    private static class SimpleCloneable implements Cloneable
    {
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
            {
                return true;
            }
            return !(obj == null || this.getClass() != obj.getClass());
        }

        @Override
        public int hashCode()
        {
            return 0;
        }
    }

    @Test
    public void assertNotEquals()
    {
        Object object = new Object()
        {
            @Override
            public boolean equals(Object obj)
            {
                return false;
            }
        };

        Verify.assertNotEquals(object, object);
    }

    @Test
    public void assertNotEqualsFailsOnSameReference()
    {
        try
        {
            Object object = new Object();
            Verify.assertNotEquals(object, object);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertNotEqualsFailsOnDifferentReference()
    {
        try
        {
            //noinspection CachedNumberConstructorCall,UnnecessaryBoxing
            Integer integer1 = Integer.valueOf(12345);
            //noinspection CachedNumberConstructorCall,UnnecessaryBoxing
            Integer integer2 = Integer.valueOf(12345);
            Verify.assertNotEquals(integer1, integer2);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertEqualsAndHashCode()
    {
        try
        {
            Verify.assertEqualsAndHashCode(new ConstantHashCode(), new ConstantHashCode());
            Assertions.fail();
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }

        try
        {
            Verify.assertEqualsAndHashCode(new AlwaysEqualWithHashCodeOf(1), new AlwaysEqualWithHashCodeOf(2));
            Assertions.fail();
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    private static class ConstantHashCode
    {
        @Override
        public int hashCode()
        {
            return 1;
        }
    }

    private static final class AlwaysEqualWithHashCodeOf
    {
        private final int hashcode;

        private AlwaysEqualWithHashCodeOf(int hashcode)
        {
            this.hashcode = hashcode;
        }

        @Override
        public int hashCode()
        {
            return this.hashcode;
        }

        @Override
        public boolean equals(Object obj)
        {
            return obj != null;
        }
    }

    @Test
    public void assertContainsAllEntries()
    {
        try
        {
            MutableListMultimap<String, Integer> multimap = FastListMultimap.newMultimap(Tuples.pair("one", 1), Tuples.pair("two", 2));
            Verify.assertContainsAllEntries(multimap, "one", 1, "three", 3);
            Assertions.fail();
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertContainsAllEntries_OddArgumentCount()
    {
        try
        {
            MutableListMultimap<String, Integer> multimap = FastListMultimap.newMultimap(Tuples.pair("one", 1), Tuples.pair("two", 2));
            Verify.assertContainsAllEntries(multimap, "one", 1, "three");
            Assertions.fail();
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertContainsAll()
    {
        try
        {
            Collection<String> list = FastList.newListWith("One", "Two", "Three");
            Verify.assertContainsAll(list, "Foo", "Bar", "Baz");
            Assertions.fail();
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getMessage(), "these items");
        }
    }

    @Test
    public void assertInstanceOf()
    {
        try
        {
            Verify.assertInstanceOf(Integer.class, 1L);
            Assertions.fail();
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertSortedSetsEqual()
    {
        TreeSortedSet<Integer> integers = TreeSortedSet.newSetWith(Comparators.<Integer>reverseNaturalOrder(), 1, 2, 3, 4);
        Verify.assertSortedSetsEqual(null, null);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(), new TreeSet<Object>());
        Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3), new TreeSet<Integer>(FastList.newListWith(1, 2, 3)));
        Verify.assertSortedSetsEqual(new TreeSet<Integer>(integers), integers);
        Verify.assertSortedSetsEqual(TreeSortedSet.newSet(integers), integers);

        try
        {
            Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3), new TreeSet<Object>(FastList.newListWith()));
            Assertions.fail();
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }

        try
        {
            Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(1, 2, 3), integers);
            Assertions.fail();
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }

        try
        {
            Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Comparators.<Integer>reverseNaturalOrder(), 1, 2, 3, 4, 5), integers);
            Assertions.fail();
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }

        try
        {
            Verify.assertSortedSetsEqual(TreeSortedSet.newSetWith(Comparators.<Integer>reverseNaturalOrder(), 3, 4), integers);
            Assertions.fail();
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertEmpty()
    {
        try
        {
            Verify.assertEmpty(FastList.newListWith("foo"));
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "actual size:<1>");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertEmpty_PrimitiveIterable()
    {
        try
        {
            Verify.assertEmpty(IntArrayList.newListWith(1));
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "actual size:<1>");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertEmpty_Iterable()
    {
        try
        {
            Verify.assertIterableEmpty(FastList.newListWith("foo"));
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "actual size:<1>");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertEmpty_Map()
    {
        try
        {
            Verify.assertEmpty(UnifiedMap.newWithKeysValues("foo", "bar"));
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "actual size:<1>");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertEmpty_ImmutableMap()
    {
        try
        {
            Verify.assertEmpty(Maps.immutable.of("foo", "bar"));
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "actual size:<1>");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertEmpty_Multimap()
    {
        try
        {
            Verify.assertEmpty(FastListMultimap.newMultimap(Tuples.pair("foo", "1"), Tuples.pair("foo", "2")));
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "actual size:<2>");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertNotEmpty()
    {
        try
        {
            Verify.assertNotEmpty(Lists.mutable.of());
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "should be non-empty");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertNotEmpty_PrimitiveIterable()
    {
        try
        {
            Verify.assertNotEmpty(new IntArrayList());
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "should be non-empty");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertNotEmpty_Iterable()
    {
        try
        {
            Verify.assertIterableNotEmpty(Lists.mutable.of());
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "should be non-empty");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertNotEmpty_Map()
    {
        try
        {
            Verify.assertNotEmpty(UnifiedMap.newMap());
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "should be non-empty");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertNotEmpty_Multimap()
    {
        try
        {
            Verify.assertNotEmpty(FastListMultimap.newMultimap());
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "should be non-empty");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertNotEmpty_Array()
    {
        Verify.assertNotEmpty(new Object[]{new Object()});
        try
        {
            Verify.assertNotEmpty(new Object[0]);
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "items should not be equal");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertSize()
    {
        try
        {
            Verify.assertSize(3, FastList.newListWith("foo", "bar"));
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "Incorrect size");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertCount()
    {
        try
        {
            Verify.assertSize(3, FastList.newListWith("foo", "bar"));
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "Incorrect size");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertSize_Array()
    {
        try
        {
            Verify.assertSize(3, new Object[]{new Object()});
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "Incorrect size");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertSize_Iterable()
    {
        try
        {
            Verify.assertIterableSize(3, FastList.newListWith("foo", "bar"));
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "Incorrect size");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertSize_PrimitiveIterable()
    {
        try
        {
            Verify.assertSize(3, IntArrayList.newListWith(1, 2));
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "Incorrect size");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertSize_Map()
    {
        try
        {
            Verify.assertSize(3, UnifiedMap.newWithKeysValues("foo", "bar"));
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "Incorrect size");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertSize_Multimap()
    {
        try
        {
            Verify.assertSize(3, FastListMultimap.newMultimap());
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "Incorrect size");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertSize_ImmutableMap()
    {
        try
        {
            Verify.assertSize(3, Maps.immutable.of("foo", "bar"));
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "Incorrect size");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertSize_ImmutableSet()
    {
        try
        {
            Verify.assertSize(3, Sets.immutable.of("foo", "bar"));
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "Incorrect size");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertContains_String()
    {
        try
        {
            Verify.assertContains("bar", "foo");
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "did not contain");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertAllSatisfy()
    {
        try
        {
            Verify.assertAllSatisfy(FastList.newListWith(1, 3), IntegerPredicates.isEven());
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "failed to satisfy the condition");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertAllSatisfy_Map()
    {
        try
        {
            Verify.assertAllSatisfy((Map<?, Integer>) UnifiedMap.newWithKeysValues(1, 1, 3, 3), IntegerPredicates.isEven());
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "failed to satisfy the condition");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertNoneSatisfy()
    {
        try
        {
            Verify.assertNoneSatisfy(FastList.newListWith(1, 3), IntegerPredicates.isOdd());
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "satisfied the condition");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertNoneSatisfy_Map()
    {
        try
        {
            Verify.assertNoneSatisfy((Map<?, Integer>) UnifiedMap.newWithKeysValues(1, 1, 3, 3), IntegerPredicates.isOdd());
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "satisfied the condition");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertAnySatisfy()
    {
        try
        {
            Verify.assertAnySatisfy(FastList.newListWith(1, 3), IntegerPredicates.isEven());
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "No items satisfied the condition");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertAnySatisfy_Map()
    {
        try
        {
            Verify.assertAnySatisfy((Map<?, Integer>) UnifiedMap.newWithKeysValues(1, 1, 3, 3), IntegerPredicates.isEven());
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "No items satisfied the condition");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertContainsAllKeyValues_MissingKeys()
    {
        try
        {
            Verify.assertContainsAllKeyValues(UnifiedMap.newWithKeysValues("foo", "bar"), "baz", "quaz");
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "did not contain");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertContainsAllKeyValues_MissingValues()
    {
        try
        {
            Verify.assertContainsAllKeyValues(UnifiedMap.newWithKeysValues("foo", "bar"), "foo", "quaz");
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "map.valuesView() did not contain these items:<[quaz]>");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertContainsAllKeyValues_OddVarArgCount()
    {
        try
        {
            Verify.assertContainsAllKeyValues(UnifiedMap.newWithKeysValues("foo", "bar"), "baz");
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "Odd number of keys and values");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertContainsAllKeyValues_ImmutableMap_MissingKey()
    {
        try
        {
            Verify.assertContainsAllKeyValues(Maps.immutable.of("foo", "bar"), "baz", "quaz");
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "did not contain these items");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertContainsAllKeyValues_ImmutableMap_MissingValue()
    {
        try
        {
            Verify.assertContainsAllKeyValues(Maps.immutable.of("foo", "bar"), "foo", "quaz");
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "did not contain these items");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertContainsAllKeyValues_ImmutableMap_OddVarArgCount()
    {
        try
        {
            Verify.assertContainsAllKeyValues(Maps.immutable.of("foo", "bar"), "baz");
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "Odd number of keys and values");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertContainsNone()
    {
        try
        {
            Verify.assertContainsNone(FastList.newListWith("foo", "bar"), "foo", "bar");
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "has an intersection with");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void denyContainsAny()
    {
        try
        {
            Verify.denyContainsAny(FastList.newListWith("foo", "bar"), "foo", "bar");
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "has an intersection with");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertContains_Collection()
    {
        try
        {
            Verify.assertContains(FastList.newListWith("foo", "bar"), "baz");
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "did not contain");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertContains_ImmutableSet()
    {
        try
        {
            Verify.assertContains(Sets.immutable.of("foo"), "bar");
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "did not contain");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertContainsEntry()
    {
        try
        {
            Verify.assertContainsEntry("bar", FastListMultimap.newMultimap(Tuples.pair("foo", "baz")), "foo");
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "did not contain");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertContainsKey()
    {
        try
        {
            Verify.assertContainsKey(UnifiedMap.newWithKeysValues("foozle", "bar"), "foo");
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "did not contain");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertContainsKey_ImmutableMap()
    {
        try
        {
            Verify.assertContainsKey(Maps.immutable.of("foozle", "bar"), "foo");
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "did not contain");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void denyContainsKey()
    {
        try
        {
            Verify.denyContainsKey(UnifiedMap.newWithKeysValues("foo", "bar"), "foo");
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "contained unexpected");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertContainsKeyValue_MissingKey()
    {
        try
        {
            Verify.assertContainsKeyValue("bar", UnifiedMap.newWithKeysValues("baz", "quaz"), "foo");
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "did not contain");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertContainsKeyValue_MissingValue()
    {
        try
        {
            Verify.assertContainsKeyValue("bar", UnifiedMap.newWithKeysValues("foo", "quaz"), "foo");
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "did not contain");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertContainsKeyValue_ImmutableMap_MissingKey()
    {
        try
        {
            Verify.assertContainsKeyValue("bar", Maps.immutable.of("baz", "quaz"), "foo");
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "did not contain");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertContainsKeyValue_ImmutableMap_MissingValue()
    {
        try
        {
            Verify.assertContainsKeyValue("bar", Maps.immutable.of("baz", "quaz"), "foo");
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "did not contain");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertNotContains_Collection()
    {
        try
        {
            Verify.assertNotContains(FastList.newListWith("foo"), "foo");
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "should not contain");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertNotContains_Iterable()
    {
        try
        {
            Verify.assertNotContains((Iterable<?>) FastList.newListWith("foo"), "foo");
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "should not contain");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertNotContainsKey()
    {
        try
        {
            Verify.assertNotContainsKey(UnifiedMap.newWithKeysValues("foo", "bar"), "foo");
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "should not contain");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }

    @Test
    public void assertClassNonInstantiable()
    {
        Verify.assertClassNonInstantiable(SerializeTestHelper.class);

        try
        {
            Verify.assertClassNonInstantiable(VerifyTest.class);
            Assertions.fail();
        }
        catch (AssertionError ex)
        {
            Verify.assertContains(ex.getMessage(), "to be non-instantiable");
            Verify.assertContains(ex.getStackTrace()[0].toString(), VerifyTest.class.getName());
        }
    }
}
