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

package com.gs.collections.impl;

import java.util.Collections;

import com.gs.collections.api.RichIterable;
import com.gs.collections.api.partition.PartitionIterable;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.multimap.list.FastListMultimap;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * JUnit test for {@link UnmodifiableRichIterable}.
 */
public class UnmodifiableRichIterableTest extends AbstractRichIterableTestCase
{
    private static final String METALLICA = "Metallica";
    private static final String BON_JOVI = "Bon Jovi";
    private static final String EUROPE = "Europe";
    private static final String SCORPIONS = "Scorpions";
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private RichIterable<String> mutableCollection;
    private RichIterable<String> unmodifiableCollection;

    @Override
    protected <T> RichIterable<T> newWith(T... elements)
    {
        return UnmodifiableRichIterable.of(Lists.mutable.of(elements));
    }

    @BeforeEach
    public void setUp()
    {
        this.mutableCollection = Lists.mutable.of(METALLICA, BON_JOVI, EUROPE, SCORPIONS);
        this.unmodifiableCollection = UnmodifiableRichIterable.of(this.mutableCollection);
    }

    @Test
    public void testDelegatingMethods()
    {
        Assertions.assertTrue(this.mutableCollection.notEmpty());
        Assertions.assertTrue(this.unmodifiableCollection.notEmpty());
        Assertions.assertFalse(this.mutableCollection.isEmpty());
        Assertions.assertFalse(this.unmodifiableCollection.isEmpty());
        Verify.assertIterableSize(this.mutableCollection.size(), this.unmodifiableCollection);
        Assertions.assertEquals(this.mutableCollection.getFirst(), this.unmodifiableCollection.getFirst());
        Assertions.assertEquals(this.mutableCollection.getLast(), this.unmodifiableCollection.getLast());
    }

    @Test
    public void converters()
    {
        Assertions.assertEquals(
                this.mutableCollection.toBag(),
                this.unmodifiableCollection.toBag());
        Assertions.assertEquals(
                this.mutableCollection.asLazy().toBag(),
                this.unmodifiableCollection.asLazy().toBag());
        Assertions.assertArrayEquals(
                this.mutableCollection.toArray(),
                this.unmodifiableCollection.toArray());
        Assertions.assertArrayEquals(
                this.mutableCollection.toArray(EMPTY_STRING_ARRAY),
                this.unmodifiableCollection.toArray(EMPTY_STRING_ARRAY));
        Assertions.assertEquals(this.mutableCollection.toList(), this.unmodifiableCollection.toList());
        Verify.assertListsEqual(Lists.mutable.of(BON_JOVI, EUROPE, METALLICA, SCORPIONS),
                this.unmodifiableCollection
                        .toSortedList());
        Verify.assertListsEqual(Lists.mutable.of(SCORPIONS, METALLICA, EUROPE, BON_JOVI),
                this.unmodifiableCollection
                        .toSortedList(Collections.reverseOrder()));
        Verify.assertListsEqual(Lists.mutable.of(BON_JOVI, EUROPE, METALLICA, SCORPIONS),
                this.unmodifiableCollection
                        .toSortedListBy(Functions.getStringPassThru()));
        Verify.assertSize(4, this.unmodifiableCollection.toSet());
        Verify.assertSize(4, this.unmodifiableCollection.toMap(Functions.getStringPassThru(), Functions.getStringPassThru()));
    }

    @Test
    public void nullCheck()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            UnmodifiableRichIterable.of(null);
        });
    }

    @Test
    @Override
    public void equalsAndHashCode()
    {
        Assertions.assertNotEquals(this.newWith(1, 2, 3).hashCode(), this.newWith(1, 2, 3).hashCode());
        Assertions.assertNotEquals(this.newWith(1, 2, 3), this.newWith(1, 2, 3));
    }

    @Test
    @Override
    public void partition()
    {
        PartitionIterable<String> partition = this.mutableCollection.partition(ignored -> true);
        PartitionIterable<String> unmodifiablePartition = this.unmodifiableCollection.partition(ignored -> true);
        Assertions.assertEquals(partition.getSelected(), unmodifiablePartition.getSelected());
        Assertions.assertEquals(partition.getRejected(), unmodifiablePartition.getRejected());
    }

    @Test
    @Override
    public void partitionWith()
    {
        PartitionIterable<String> partition = this.mutableCollection.partitionWith((ignored1, ignored2) -> true, null);
        PartitionIterable<String> unmodifiablePartition = this.unmodifiableCollection.partitionWith((ignored1, ignored2) -> true, null);
        Assertions.assertEquals(partition.getSelected(), unmodifiablePartition.getSelected());
        Assertions.assertEquals(partition.getRejected(), unmodifiablePartition.getRejected());
    }

    @Test
    @Override
    public void groupBy()
    {
        Assertions.assertEquals(this.mutableCollection.groupBy(Functions.getStringPassThru()), this.unmodifiableCollection.groupBy(Functions.getStringPassThru()));
        Assertions.assertEquals(this.mutableCollection.groupBy(Functions.getStringPassThru(), FastListMultimap.<String, String>newMultimap()), this.unmodifiableCollection.groupBy(Functions.getStringPassThru(), FastListMultimap.<String, String>newMultimap()));
    }
}
