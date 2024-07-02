/*
 * Copyright 2011 Goldman Sachs.
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

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.set.MutableSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static com.gs.collections.impl.factory.Iterables.*;

/**
 * JUnit test to make sure that the methods {@link Verify#assertListsEqual(String, List, List)},
 * {@link Verify#assertSetsEqual(String, Set, Set)},
 * and {@link Verify#assertMapsEqual(String, Map, Map)} really throw when they ought to.
 */
public class CollectionsEqualTest
{
    private final MutableList<String> list = mList("asdf", "qwer");
    private final MutableList<String> list2 = mList("asdf", "zxcv");
    private final MutableList<String> list3 = mList("asdf");

    private final MutableSet<String> set = mSet("asdf", "qwer");
    private final MutableSet<String> set2 = mSet("asdf", "zxcv");
    private final MutableSet<String> set3 = mSet("asdf");

    private final MutableSet<String> bigSet1 = mSet("1", "2", "3", "4", "5", "6");
    private final MutableSet<String> bigSet2 = mSet("7", "8", "9", "10", "11", "12");

    private final Map<String, String> map = mMap("asdf", "asdf", "qwer", "qwer");
    private final Map<String, String> map2 = mMap("asdf", "zxcv", "qwer", "qwer");
    private final Map<String, String> map3 = mMap("zxcv", "asdf", "qwer", "qwer");
    private final Map<String, String> map4 = mMap("zxcv", "zxcv", "qwer", "qwer");
    private final Map<String, String> map5 = mMap("asdf", "asdf");

    @Test
    public void listsEqual()
    {
        List<?> nullList = null;

        Verify.assertListsEqual(nullList, nullList);
        Verify.assertListsEqual(nullList, nullList, "assertListsEqual(nullList, nullList)");

        Verify.assertListsEqual(this.list, this.list);
        Verify.assertListsEqual(this.list, this.list, "assertListsEqual(list, list)");

        try
        {
            Verify.assertListsEqual(nullList, this.list, "assertListsEqual(nullList, list)");
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }

        try
        {
            Verify.assertListsEqual(nullList, this.list);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }

        try
        {
            Verify.assertListsEqual(this.list, nullList, "assertListsEqual(list, nullList)");
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }

        try
        {
            Verify.assertListsEqual(this.list, nullList);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }
    }

    @Test
    public void setsEqual()
    {
        Set<?> nullSet = null;

        Verify.assertSetsEqual(nullSet, nullSet);
        Verify.assertSetsEqual(nullSet, nullSet, "assertSetsEqual(nullSet, nullSet)");

        Verify.assertSetsEqual(this.set, this.set);
        Verify.assertSetsEqual(this.set, this.set, "assertSetsEqual(set, set)");

        try
        {
            Verify.assertSetsEqual(nullSet, this.set, "assertSetsEqual(nullSet, set)");
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }

        try
        {
            Verify.assertSetsEqual(nullSet, this.set);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }

        try
        {
            Verify.assertSetsEqual(this.set, nullSet, "assertSetsEqual(set, nullSet)");
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }

        try
        {
            Verify.assertSetsEqual(this.set, nullSet);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }
    }

    @Test
    public void mapsEqual()
    {
        Map<?, ?> nullMap = null;

        Verify.assertMapsEqual(nullMap, nullMap);
        Verify.assertMapsEqual(nullMap, nullMap, "assertMapsEqual(nullMap, nullMap)");

        Verify.assertMapsEqual(this.map, this.map);
        Verify.assertMapsEqual(this.map, this.map, "assertMapsEqual(map, map)");

        try
        {
            Verify.assertMapsEqual(nullMap, this.map, "assertMapsEqual(nullMap, map)");
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }

        try
        {
            Verify.assertMapsEqual(nullMap, this.map);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }

        try
        {
            Verify.assertMapsEqual(this.map, nullMap, "assertMapsEqual(map, nullMap)");
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }

        try
        {
            Verify.assertMapsEqual(this.map, nullMap);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }
    }

    @Test
    public void listsDifferentSizes()
    {
        try
        {
            Verify.assertListsEqual(this.list, this.list3, "assertListsEqual(list, list3)");
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }

        try
        {
            Verify.assertListsEqual(this.list, this.list3);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }
    }

    @Test
    public void differentLists()
    {
        try
        {
            Verify.assertListsEqual(this.list, this.list2, "assertListsEqual(list, list2)");
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }

        try
        {
            Verify.assertListsEqual(this.list, this.list2);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }
    }

    @Test
    public void setsDifferentSizes()
    {
        try
        {
            Verify.assertSetsEqual(this.set, this.set2, "assertSetsEqual(set, set2)");
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }

        try
        {
            Verify.assertSetsEqual(this.set, this.set2);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }
    }

    @Test
    public void differentSets()
    {
        try
        {
            Verify.assertSetsEqual(this.set, this.set3, "assertSetsEqual(set, set3)");
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }

        try
        {
            Verify.assertSetsEqual(this.set, this.set3);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }
    }

    @Test
    public void mapsDifferentValue()
    {
        try
        {
            Verify.assertMapsEqual(this.map, this.map2, "assertMapsEqual(map, map2)");
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }

        try
        {
            Verify.assertMapsEqual(this.map, this.map2);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }
    }

    @Test
    public void mapsDifferentKey()
    {
        try
        {
            Verify.assertMapsEqual(this.map, this.map3, "assertMapsEqual(map, map3)");
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }

        try
        {
            Verify.assertMapsEqual(this.map, this.map3);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }
    }

    @Test
    public void mapsDifferentEntry()
    {
        try
        {
            Verify.assertMapsEqual(this.map, this.map4, "assertMapsEqual(map, map4)");
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }

        try
        {
            Verify.assertMapsEqual(this.map, this.map4);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }
    }

    @Test
    public void mapsDifferentSize()
    {
        try
        {
            Verify.assertMapsEqual(this.map, this.map5, "assertMapsEqual(map, map5)");
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }

        try
        {
            Verify.assertMapsEqual(this.map, this.map5);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }
    }

    @Test
    public void bigSetsDiffer()
    {
        try
        {
            Verify.assertSetsEqual(this.bigSet1, this.bigSet2);
            Assertions.fail("AssertionError expected");
        }
        catch (AssertionError e)
        {
            Verify.assertContains(e.getStackTrace()[0].toString(), CollectionsEqualTest.class.getName());
        }
    }
}
