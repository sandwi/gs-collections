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

package com.gs.collections.impl.set.mutable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import com.gs.collections.api.LazyIterable;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.annotation.Beta;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.block.predicate.Predicate2;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.block.procedure.Procedure2;
import com.gs.collections.api.block.procedure.primitive.ObjectIntProcedure;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.partition.set.PartitionMutableSet;
import com.gs.collections.api.set.ImmutableSet;
import com.gs.collections.api.set.MutableSet;
import com.gs.collections.api.set.ParallelUnsortedSetIterable;
import com.gs.collections.api.tuple.Twin;
import com.gs.collections.impl.block.factory.Procedures2;
import com.gs.collections.impl.block.procedure.PartitionPredicate2Procedure;
import com.gs.collections.impl.block.procedure.PartitionProcedure;
import com.gs.collections.impl.block.procedure.SelectInstancesOfProcedure;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.factory.Sets;
import com.gs.collections.impl.lazy.AbstractLazyIterable;
import com.gs.collections.impl.lazy.parallel.AbstractBatch;
import com.gs.collections.impl.lazy.parallel.AbstractParallelIterable;
import com.gs.collections.impl.lazy.parallel.bag.CollectUnsortedBagBatch;
import com.gs.collections.impl.lazy.parallel.bag.FlatCollectUnsortedBagBatch;
import com.gs.collections.impl.lazy.parallel.bag.UnsortedBagBatch;
import com.gs.collections.impl.lazy.parallel.set.AbstractParallelUnsortedSetIterable;
import com.gs.collections.impl.lazy.parallel.set.RootUnsortedSetBatch;
import com.gs.collections.impl.lazy.parallel.set.SelectUnsortedSetBatch;
import com.gs.collections.impl.lazy.parallel.set.UnsortedSetBatch;
import com.gs.collections.impl.multimap.set.UnifiedSetMultimap;
import com.gs.collections.impl.partition.set.PartitionUnifiedSet;
import com.gs.collections.impl.set.AbstractUnifiedSet;
import com.gs.collections.impl.tuple.Tuples;
import com.gs.collections.impl.utility.Iterate;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class UnifiedSet<T>
        extends AbstractUnifiedSet<T>
        implements Externalizable
{
    protected static final Object NULL_KEY = new Object()
    {
        @Override
        public boolean equals(Object obj)
        {
            throw new RuntimeException("Possible corruption through unsynchronized concurrent modification.");
        }

        @Override
        public int hashCode()
        {
            throw new RuntimeException("Possible corruption through unsynchronized concurrent modification.");
        }

        @Override
        public String toString()
        {
            return "UnifiedSet.NULL_KEY";
        }
    };

    private static final long serialVersionUID = 1L;

    protected transient Object[] table;

    protected transient int occupied;

    public UnifiedSet()
    {
        this.allocate(DEFAULT_INITIAL_CAPACITY << 1);
    }

    public UnifiedSet(int initialCapacity)
    {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public UnifiedSet(int initialCapacity, float loadFactor)
    {
        if (initialCapacity < 0)
        {
            throw new IllegalArgumentException("initial capacity cannot be less than 0");
        }
        if (loadFactor <= 0.0)
        {
            throw new IllegalArgumentException("load factor cannot be less than or equal to 0");
        }
        if (loadFactor > 1.0)
        {
            throw new IllegalArgumentException("load factor cannot be greater than 1");
        }
        this.loadFactor = loadFactor;
        this.init(this.fastCeil(initialCapacity / loadFactor));
    }

    public UnifiedSet(Collection<? extends T> collection)
    {
        this(Math.max(collection.size(), DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);
        this.addAll(collection);
    }

    public UnifiedSet(UnifiedSet<T> set)
    {
        this.maxSize = set.maxSize;
        this.loadFactor = set.loadFactor;
        this.occupied = set.occupied;
        this.allocateTable(set.table.length);

        for (int i = 0; i < set.table.length; i++)
        {
            Object key = set.table[i];
            if (key instanceof ChainedBucket bucket)
            {
                this.table[i] = bucket.copy();
            }
            else if (key != null)
            {
                this.table[i] = key;
            }
        }
    }

    public static <K> UnifiedSet<K> newSet()
    {
        return new UnifiedSet<K>();
    }

    public static <K> UnifiedSet<K> newSet(int size)
    {
        return new UnifiedSet<K>(size);
    }

    public static <K> UnifiedSet<K> newSet(Iterable<? extends K> source)
    {
        if (source instanceof UnifiedSet)
        {
            return new UnifiedSet<K>((UnifiedSet<K>) source);
        }
        if (source instanceof Collection)
        {
            return new UnifiedSet<K>((Collection<K>) source);
        }
        if (source == null)
        {
            throw new NullPointerException();
        }
        UnifiedSet<K> result = source instanceof RichIterable ri
                ? UnifiedSet.<K>newSet(ri.size())
                : UnifiedSet.<K>newSet();
        Iterate.forEachWith(source, Procedures2.<K>addToCollection(), result);
        return result;
    }

    public static <K> UnifiedSet<K> newSet(int size, float loadFactor)
    {
        return new UnifiedSet<K>(size, loadFactor);
    }

    public static <K> UnifiedSet<K> newSetWith(K... elements)
    {
        return UnifiedSet.<K>newSet(elements.length).with(elements);
    }

    private int fastCeil(float v)
    {
        int possibleResult = (int) v;
        if (v - possibleResult > 0.0F)
        {
            possibleResult++;
        }
        return possibleResult;
    }

    @Override
    protected Object[] getTable()
    {
        return this.table;
    }

    @Override
    protected void allocateTable(int sizeToAllocate)
    {
        this.table = new Object[sizeToAllocate];
    }

    protected final int index(Object key)
    {
        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        int h = key == null ? 0 : key.hashCode();
        h ^= h >>> 20 ^ h >>> 12;
        h ^= h >>> 7 ^ h >>> 4;
        return h & this.table.length - 1;
    }

    public void clear()
    {
        if (this.occupied == 0)
        {
            return;
        }
        this.occupied = 0;
        Object[] set = this.table;

        for (int i = set.length; i-- > 0; )
        {
            set[i] = null;
        }
    }

    public boolean add(T key)
    {
        int index = this.index(key);
        Object cur = this.table[index];
        if (cur == null)
        {
            this.table[index] = UnifiedSet.toSentinelIfNull(key);
            if (++this.occupied > this.maxSize)
            {
                this.rehash();
            }
            return true;
        }
        if (cur instanceof ChainedBucket || !this.nonNullTableObjectEquals(cur, key))
        {
            return this.chainedAdd(key, index);
        }
        return false;
    }

    private boolean chainedAdd(T key, int index)
    {
        Object realKey = UnifiedSet.toSentinelIfNull(key);
        if (this.table[index] instanceof ChainedBucket bucket)
        {
            do
            {
                if (this.nonNullTableObjectEquals(bucket.zero, key))
                {
                    return false;
                }
                if (bucket.one == null)
                {
                    bucket.one = realKey;
                    if (++this.occupied > this.maxSize)
                    {
                        this.rehash();
                    }
                    return true;
                }
                if (this.nonNullTableObjectEquals(bucket.one, key))
                {
                    return false;
                }
                if (bucket.two == null)
                {
                    bucket.two = realKey;
                    if (++this.occupied > this.maxSize)
                    {
                        this.rehash();
                    }
                    return true;
                }
                if (this.nonNullTableObjectEquals(bucket.two, key))
                {
                    return false;
                }
                if (bucket.three instanceof ChainedBucket chainedBucket)
                {
                    bucket = chainedBucket;
                    continue;
                }
                if (bucket.three == null)
                {
                    bucket.three = realKey;
                    if (++this.occupied > this.maxSize)
                    {
                        this.rehash();
                    }
                    return true;
                }
                if (this.nonNullTableObjectEquals(bucket.three, key))
                {
                    return false;
                }
                bucket.three = new ChainedBucket(bucket.three, realKey);
                if (++this.occupied > this.maxSize)
                {
                    this.rehash();
                }
                return true;
            }
            while (true);
        }
        ChainedBucket newBucket = new ChainedBucket(this.table[index], realKey);
        this.table[index] = newBucket;
        if (++this.occupied > this.maxSize)
        {
            this.rehash();
        }
        return true;
    }

    @Override
    protected void rehash(int newCapacity)
    {
        int oldLength = this.table.length;
        Object[] old = this.table;
        this.allocate(newCapacity);
        this.occupied = 0;

        for (int i = 0; i < oldLength; i++)
        {
            Object oldKey = old[i];
            if (oldKey instanceof ChainedBucket bucket)
            {
                do
                {
                    if (bucket.zero != null)
                    {
                        this.add(this.nonSentinel(bucket.zero));
                    }
                    if (bucket.one == null)
                    {
                        break;
                    }
                    this.add(this.nonSentinel(bucket.one));
                    if (bucket.two == null)
                    {
                        break;
                    }
                    this.add(this.nonSentinel(bucket.two));
                    if (bucket.three != null)
                    {
                        if (bucket.three instanceof ChainedBucket chainedBucket)
                        {
                            bucket = chainedBucket;
                            continue;
                        }
                        this.add(this.nonSentinel(bucket.three));
                    }
                    break;
                }
                while (true);
            }
            else if (oldKey != null)
            {
                this.add(this.nonSentinel(oldKey));
            }
        }
    }

    @Override
    public boolean contains(Object key)
    {
        int index = this.index(key);
        Object cur = this.table[index];
        if (cur == null)
        {
            return false;
        }
        if (cur instanceof ChainedBucket bucket)
        {
            return this.chainContains(bucket, (T) key);
        }
        return this.nonNullTableObjectEquals(cur, (T) key);
    }

    private boolean chainContains(ChainedBucket bucket, T key)
    {
        do
        {
            if (this.nonNullTableObjectEquals(bucket.zero, key))
            {
                return true;
            }
            if (bucket.one == null)
            {
                return false;
            }
            if (this.nonNullTableObjectEquals(bucket.one, key))
            {
                return true;
            }
            if (bucket.two == null)
            {
                return false;
            }
            if (this.nonNullTableObjectEquals(bucket.two, key))
            {
                return true;
            }
            if (bucket.three == null)
            {
                return false;
            }
            if (bucket.three instanceof ChainedBucket chainedBucket)
            {
                bucket = chainedBucket;
                continue;
            }
            return this.nonNullTableObjectEquals(bucket.three, key);
        }
        while (true);
    }

    public void batchForEach(Procedure<? super T> procedure, int sectionIndex, int sectionCount)
    {
        Object[] set = this.table;
        int sectionSize = set.length / sectionCount;
        int start = sectionSize * sectionIndex;
        int end = sectionIndex == sectionCount - 1 ? set.length : start + sectionSize;
        for (int i = start; i < end; i++)
        {
            Object cur = set[i];
            if (cur != null)
            {
                if (cur instanceof ChainedBucket bucket)
                {
                    this.chainedForEach(bucket, procedure);
                }
                else
                {
                    procedure.value(this.nonSentinel(cur));
                }
            }
        }
    }

    public MutableSet<T> tap(Procedure<? super T> procedure)
    {
        this.forEach(procedure);
        return this;
    }

    public void each(Procedure<? super T> procedure)
    {
        this.each(procedure, 0, this.table.length);
    }

    protected void each(Procedure<? super T> procedure, int start, int end)
    {
        for (int i = start; i < end; i++)
        {
            Object cur = this.table[i];
            if (cur instanceof ChainedBucket bucket)
            {
                this.chainedForEach(bucket, procedure);
            }
            else if (cur != null)
            {
                procedure.value(this.nonSentinel(cur));
            }
        }
    }

    private void chainedForEach(ChainedBucket bucket, Procedure<? super T> procedure)
    {
        do
        {
            procedure.value(this.nonSentinel(bucket.zero));
            if (bucket.one == null)
            {
                return;
            }
            procedure.value(this.nonSentinel(bucket.one));
            if (bucket.two == null)
            {
                return;
            }
            procedure.value(this.nonSentinel(bucket.two));
            if (bucket.three == null)
            {
                return;
            }
            if (bucket.three instanceof ChainedBucket chainedBucket)
            {
                bucket = chainedBucket;
                continue;
            }
            procedure.value(this.nonSentinel(bucket.three));
            return;
        }
        while (true);
    }

    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        for (int i = 0; i < this.table.length; i++)
        {
            Object cur = this.table[i];
            if (cur instanceof ChainedBucket bucket)
            {
                this.chainedForEachWith(bucket, procedure, parameter);
            }
            else if (cur != null)
            {
                procedure.value(this.nonSentinel(cur), parameter);
            }
        }
    }

    private <P> void chainedForEachWith(
            ChainedBucket bucket,
            Procedure2<? super T, ? super P> procedure,
            P parameter)
    {
        do
        {
            procedure.value(this.nonSentinel(bucket.zero), parameter);
            if (bucket.one == null)
            {
                return;
            }
            procedure.value(this.nonSentinel(bucket.one), parameter);
            if (bucket.two == null)
            {
                return;
            }
            procedure.value(this.nonSentinel(bucket.two), parameter);
            if (bucket.three == null)
            {
                return;
            }
            if (bucket.three instanceof ChainedBucket chainedBucket)
            {
                bucket = chainedBucket;
                continue;
            }
            procedure.value(this.nonSentinel(bucket.three), parameter);
            return;
        }
        while (true);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        int count = 0;
        for (int i = 0; i < this.table.length; i++)
        {
            Object cur = this.table[i];
            if (cur instanceof ChainedBucket bucket)
            {
                count = this.chainedForEachWithIndex(bucket, objectIntProcedure, count);
            }
            else if (cur != null)
            {
                objectIntProcedure.value(this.nonSentinel(cur), count++);
            }
        }
    }

    private int chainedForEachWithIndex(ChainedBucket bucket, ObjectIntProcedure<? super T> procedure, int count)
    {
        do
        {
            procedure.value(this.nonSentinel(bucket.zero), count++);
            if (bucket.one == null)
            {
                return count;
            }
            procedure.value(this.nonSentinel(bucket.one), count++);
            if (bucket.two == null)
            {
                return count;
            }
            procedure.value(this.nonSentinel(bucket.two), count++);
            if (bucket.three == null)
            {
                return count;
            }
            if (bucket.three instanceof ChainedBucket chainedBucket)
            {
                bucket = chainedBucket;
                continue;
            }
            procedure.value(this.nonSentinel(bucket.three), count++);
            return count;
        }
        while (true);
    }

    public UnifiedSet<T> newEmpty()
    {
        return UnifiedSet.newSet();
    }

    public T getFirst()
    {
        for (int i = 0; i < this.table.length; i++)
        {
            Object cur = this.table[i];
            if (cur instanceof ChainedBucket bucket)
            {
                return this.nonSentinel(bucket.zero);
            }
            if (cur != null)
            {
                return this.nonSentinel(cur);
            }
        }
        return null;
    }

    public T getLast()
    {
        for (int i = this.table.length - 1; i >= 0; i--)
        {
            Object cur = this.table[i];
            if (cur instanceof ChainedBucket bucket)
            {
                return this.getLast(bucket);
            }
            if (cur != null)
            {
                return this.nonSentinel(cur);
            }
        }
        return null;
    }

    private T getLast(ChainedBucket bucket)
    {
        while (bucket.three instanceof ChainedBucket)
        {
            bucket = (ChainedBucket) bucket.three;
        }

        if (bucket.three != null)
        {
            return this.nonSentinel(bucket.three);
        }
        if (bucket.two != null)
        {
            return this.nonSentinel(bucket.two);
        }
        if (bucket.one != null)
        {
            return this.nonSentinel(bucket.one);
        }
        assert bucket.zero != null;
        return this.nonSentinel(bucket.zero);
    }

    public UnifiedSet<T> select(Predicate<? super T> predicate)
    {
        return this.select(predicate, this.newEmpty());
    }

    public <P> UnifiedSet<T> selectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        return this.selectWith(predicate, parameter, this.newEmpty());
    }

    public UnifiedSet<T> reject(Predicate<? super T> predicate)
    {
        return this.reject(predicate, this.newEmpty());
    }

    public <P> UnifiedSet<T> rejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        return this.rejectWith(predicate, parameter, this.newEmpty());
    }

    public <P> Twin<MutableList<T>> selectAndRejectWith(
            final Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        final MutableList<T> positiveResult = Lists.mutable.empty();
        final MutableList<T> negativeResult = Lists.mutable.empty();
        this.forEachWith(new Procedure2<T, P>()
        {
            public void value(T each, P parm)
            {
                (predicate.accept(each, parm) ? positiveResult : negativeResult).add(each);
            }
        }, parameter);
        return Tuples.twin(positiveResult, negativeResult);
    }

    public PartitionMutableSet<T> partition(Predicate<? super T> predicate)
    {
        PartitionMutableSet<T> partitionUnifiedSet = new PartitionUnifiedSet<T>();
        this.forEach(new PartitionProcedure<T>(predicate, partitionUnifiedSet));
        return partitionUnifiedSet;
    }

    public <P> PartitionMutableSet<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        PartitionMutableSet<T> partitionUnifiedSet = new PartitionUnifiedSet<T>();
        this.forEach(new PartitionPredicate2Procedure<T, P>(predicate, parameter, partitionUnifiedSet));
        return partitionUnifiedSet;
    }

    public <S> UnifiedSet<S> selectInstancesOf(Class<S> clazz)
    {
        UnifiedSet<S> result = UnifiedSet.newSet();
        this.forEach(new SelectInstancesOfProcedure<S>(clazz, result));
        return result;
    }

    @Override
    protected T detect(Predicate<? super T> predicate, int start, int end)
    {
        for (int i = start; i < end; i++)
        {
            Object cur = this.table[i];
            if (cur instanceof ChainedBucket bucket)
            {
                Object chainedDetect = this.chainedDetect(bucket, predicate);
                if (chainedDetect != null)
                {
                    return this.nonSentinel(chainedDetect);
                }
            }
            else if (cur != null)
            {
                T each = this.nonSentinel(cur);
                if (predicate.accept(each))
                {
                    return each;
                }
            }
        }
        return null;
    }

    private Object chainedDetect(ChainedBucket bucket, Predicate<? super T> predicate)
    {
        do
        {
            if (predicate.accept(this.nonSentinel(bucket.zero)))
            {
                return bucket.zero;
            }
            if (bucket.one == null)
            {
                return null;
            }
            if (predicate.accept(this.nonSentinel(bucket.one)))
            {
                return bucket.one;
            }
            if (bucket.two == null)
            {
                return null;
            }
            if (predicate.accept(this.nonSentinel(bucket.two)))
            {
                return bucket.two;
            }
            if (bucket.three == null)
            {
                return null;
            }
            if (bucket.three instanceof ChainedBucket chainedBucket)
            {
                bucket = chainedBucket;
                continue;
            }
            if (predicate.accept(this.nonSentinel(bucket.three)))
            {
                return bucket.three;
            }
            return null;
        }
        while (true);
    }

    @Override
    protected boolean shortCircuit(
            Predicate<? super T> predicate,
            boolean expected,
            boolean onShortCircuit,
            boolean atEnd,
            int start,
            int end)
    {
        for (int i = start; i < end; i++)
        {
            Object cur = this.table[i];
            if (cur instanceof ChainedBucket bucket)
            {
                if (this.chainedShortCircuit(bucket, predicate, expected))
                {
                    return onShortCircuit;
                }
            }
            else if (cur != null)
            {
                T each = this.nonSentinel(cur);
                if (predicate.accept(each) == expected)
                {
                    return onShortCircuit;
                }
            }
        }
        return atEnd;
    }

    private boolean chainedShortCircuit(
            ChainedBucket bucket,
            Predicate<? super T> predicate,
            boolean expected)
    {
        do
        {
            if (predicate.accept(this.nonSentinel(bucket.zero)) == expected)
            {
                return true;
            }
            if (bucket.one == null)
            {
                return false;
            }
            if (predicate.accept(this.nonSentinel(bucket.one)) == expected)
            {
                return true;
            }
            if (bucket.two == null)
            {
                return false;
            }
            if (predicate.accept(this.nonSentinel(bucket.two)) == expected)
            {
                return true;
            }
            if (bucket.three == null)
            {
                return false;
            }
            if (bucket.three instanceof ChainedBucket chainedBucket)
            {
                bucket = chainedBucket;
                continue;
            }
            return predicate.accept(this.nonSentinel(bucket.three)) == expected;
        }
        while (true);
    }

    @Override
    protected <P> boolean shortCircuitWith(
            Predicate2<? super T, ? super P> predicate2,
            P parameter,
            boolean expected,
            boolean onShortCircuit,
            boolean atEnd)
    {
        for (int i = 0; i < this.table.length; i++)
        {
            Object cur = this.table[i];
            if (cur instanceof ChainedBucket bucket)
            {
                if (this.chainedShortCircuitWith(bucket, predicate2, parameter, expected))
                {
                    return onShortCircuit;
                }
            }
            else if (cur != null)
            {
                T each = this.nonSentinel(cur);
                if (predicate2.accept(each, parameter) == expected)
                {
                    return onShortCircuit;
                }
            }
        }
        return atEnd;
    }

    private <P> boolean chainedShortCircuitWith(
            ChainedBucket bucket,
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            boolean expected)
    {
        do
        {
            if (predicate.accept(this.nonSentinel(bucket.zero), parameter) == expected)
            {
                return true;
            }
            if (bucket.one == null)
            {
                return false;
            }
            if (predicate.accept(this.nonSentinel(bucket.one), parameter) == expected)
            {
                return true;
            }
            if (bucket.two == null)
            {
                return false;
            }
            if (predicate.accept(this.nonSentinel(bucket.two), parameter) == expected)
            {
                return true;
            }
            if (bucket.three == null)
            {
                return false;
            }
            if (bucket.three instanceof ChainedBucket chainedBucket)
            {
                bucket = chainedBucket;
                continue;
            }
            return predicate.accept(this.nonSentinel(bucket.three), parameter) == expected;
        }
        while (true);
    }

    public ImmutableSet<T> toImmutable()
    {
        return Sets.immutable.withAll(this);
    }

    public UnifiedSet<T> with(T element)
    {
        this.add(element);
        return this;
    }

    public UnifiedSet<T> with(T element1, T element2)
    {
        this.add(element1);
        this.add(element2);
        return this;
    }

    public UnifiedSet<T> with(T element1, T element2, T element3)
    {
        this.add(element1);
        this.add(element2);
        this.add(element3);
        return this;
    }

    public UnifiedSet<T> with(T... elements)
    {
        this.addAll(Arrays.asList(elements));
        return this;
    }

    public UnifiedSet<T> withAll(Iterable<? extends T> iterable)
    {
        this.addAllIterable(iterable);
        return this;
    }

    public UnifiedSet<T> without(T element)
    {
        this.remove(element);
        return this;
    }

    public UnifiedSet<T> withoutAll(Iterable<? extends T> elements)
    {
        this.removeAllIterable(elements);
        return this;
    }

    public boolean addAllIterable(Iterable<? extends T> iterable)
    {
        if (iterable instanceof UnifiedSet set)
        {
            return this.copySet(set);
        }

        int size = Iterate.sizeOf(iterable);
        this.ensureCapacity(size);
        int oldSize = this.size();

        if (iterable instanceof List && iterable instanceof RandomAccess)
        {
            List<T> list = (List<T>) iterable;
            for (int i = 0; i < size; i++)
            {
                this.add(list.get(i));
            }
        }
        else
        {
            Iterate.forEachWith(iterable, Procedures2.<T>addToCollection(), this);
        }
        return this.size() != oldSize;
    }

    private void ensureCapacity(int size)
    {
        if (size > this.maxSize)
        {
            size = (int) (size / this.loadFactor) + 1;
            int capacity = Integer.highestOneBit(size);
            if (size != capacity)
            {
                capacity <<= 1;
            }
            this.rehash(capacity);
        }
    }

    protected boolean copySet(UnifiedSet<?> unifiedset)
    {
        //todo: optimize for current size == 0
        boolean changed = false;
        for (int i = 0; i < unifiedset.table.length; i++)
        {
            Object cur = unifiedset.table[i];
            if (cur instanceof ChainedBucket bucket)
            {
                changed |= this.copyChain(bucket);
            }
            else if (cur != null)
            {
                changed |= this.add(this.nonSentinel(cur));
            }
        }
        return changed;
    }

    private boolean copyChain(ChainedBucket bucket)
    {
        boolean changed = false;
        do
        {
            changed |= this.add(this.nonSentinel(bucket.zero));
            if (bucket.one == null)
            {
                return changed;
            }
            changed |= this.add(this.nonSentinel(bucket.one));
            if (bucket.two == null)
            {
                return changed;
            }
            changed |= this.add(this.nonSentinel(bucket.two));
            if (bucket.three == null)
            {
                return changed;
            }
            if (bucket.three instanceof ChainedBucket chainedBucket)
            {
                bucket = chainedBucket;
                continue;
            }
            changed |= this.add(this.nonSentinel(bucket.three));
            return changed;
        }
        while (true);
    }

    public boolean remove(Object key)
    {
        int index = this.index(key);

        Object cur = this.table[index];
        if (cur == null)
        {
            return false;
        }
        if (cur instanceof ChainedBucket bucket)
        {
            return this.removeFromChain(bucket, (T) key, index);
        }
        if (this.nonNullTableObjectEquals(cur, (T) key))
        {
            this.table[index] = null;
            this.occupied--;
            return true;
        }
        return false;
    }

    private boolean removeFromChain(ChainedBucket bucket, T key, int index)
    {
        if (this.nonNullTableObjectEquals(bucket.zero, key))
        {
            bucket.zero = bucket.removeLast(0);
            if (bucket.zero == null)
            {
                this.table[index] = null;
            }
            this.occupied--;
            return true;
        }
        if (bucket.one == null)
        {
            return false;
        }
        if (this.nonNullTableObjectEquals(bucket.one, key))
        {
            bucket.one = bucket.removeLast(1);
            this.occupied--;
            return true;
        }
        if (bucket.two == null)
        {
            return false;
        }
        if (this.nonNullTableObjectEquals(bucket.two, key))
        {
            bucket.two = bucket.removeLast(2);
            this.occupied--;
            return true;
        }
        if (bucket.three == null)
        {
            return false;
        }
        if (bucket.three instanceof ChainedBucket)
        {
            return this.removeDeepChain(bucket, key);
        }
        if (this.nonNullTableObjectEquals(bucket.three, key))
        {
            bucket.three = bucket.removeLast(3);
            this.occupied--;
            return true;
        }
        return false;
    }

    private boolean removeDeepChain(ChainedBucket oldBucket, T key)
    {
        do
        {
            ChainedBucket bucket = (ChainedBucket) oldBucket.three;
            if (this.nonNullTableObjectEquals(bucket.zero, key))
            {
                bucket.zero = bucket.removeLast(0);
                if (bucket.zero == null)
                {
                    oldBucket.three = null;
                }
                this.occupied--;
                return true;
            }
            if (bucket.one == null)
            {
                return false;
            }
            if (this.nonNullTableObjectEquals(bucket.one, key))
            {
                bucket.one = bucket.removeLast(1);
                this.occupied--;
                return true;
            }
            if (bucket.two == null)
            {
                return false;
            }
            if (this.nonNullTableObjectEquals(bucket.two, key))
            {
                bucket.two = bucket.removeLast(2);
                this.occupied--;
                return true;
            }
            if (bucket.three == null)
            {
                return false;
            }
            if (bucket.three instanceof ChainedBucket)
            {
                oldBucket = bucket;
                continue;
            }
            if (this.nonNullTableObjectEquals(bucket.three, key))
            {
                bucket.three = bucket.removeLast(3);
                this.occupied--;
                return true;
            }
            return false;
        }
        while (true);
    }

    public int size()
    {
        return this.occupied;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }

        if (!(object instanceof Set))
        {
            return false;
        }

        Set<?> other = (Set<?>) object;
        return this.size() == other.size() && this.containsAll(other);
    }

    @Override
    public int hashCode()
    {
        int hashCode = 0;
        for (int i = 0; i < this.table.length; i++)
        {
            Object cur = this.table[i];
            if (cur instanceof ChainedBucket bucket)
            {
                hashCode += this.chainedHashCode(bucket);
            }
            else if (cur != null)
            {
                hashCode += cur == NULL_KEY ? 0 : cur.hashCode();
            }
        }
        return hashCode;
    }

    private int chainedHashCode(ChainedBucket bucket)
    {
        int hashCode = 0;
        do
        {
            hashCode += bucket.zero == NULL_KEY ? 0 : bucket.zero.hashCode();
            if (bucket.one == null)
            {
                return hashCode;
            }
            hashCode += bucket.one == NULL_KEY ? 0 : bucket.one.hashCode();
            if (bucket.two == null)
            {
                return hashCode;
            }
            hashCode += bucket.two == NULL_KEY ? 0 : bucket.two.hashCode();
            if (bucket.three == null)
            {
                return hashCode;
            }
            if (bucket.three instanceof ChainedBucket chainedBucket)
            {
                bucket = chainedBucket;
                continue;
            }
            hashCode += bucket.three == NULL_KEY ? 0 : bucket.three.hashCode();
            return hashCode;
        }
        while (true);
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
    {
        int size = in.readInt();
        this.loadFactor = in.readFloat();
        this.init(Math.max((int) (size / this.loadFactor) + 1, DEFAULT_INITIAL_CAPACITY));
        for (int i = 0; i < size; i++)
        {
            this.add((T) in.readObject());
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException
    {
        out.writeInt(this.size());
        out.writeFloat(this.loadFactor);
        for (int i = 0; i < this.table.length; i++)
        {
            Object o = this.table[i];
            if (o != null)
            {
                if (o instanceof ChainedBucket bucket)
                {
                    this.writeExternalChain(out, bucket);
                }
                else
                {
                    out.writeObject(this.nonSentinel(o));
                }
            }
        }
    }

    private void writeExternalChain(ObjectOutput out, ChainedBucket bucket) throws IOException
    {
        do
        {
            out.writeObject(this.nonSentinel(bucket.zero));
            if (bucket.one == null)
            {
                return;
            }
            out.writeObject(this.nonSentinel(bucket.one));
            if (bucket.two == null)
            {
                return;
            }
            out.writeObject(this.nonSentinel(bucket.two));
            if (bucket.three == null)
            {
                return;
            }
            if (bucket.three instanceof ChainedBucket chainedBucket)
            {
                bucket = chainedBucket;
                continue;
            }
            out.writeObject(this.nonSentinel(bucket.three));
            return;
        }
        while (true);
    }

    private void addIfFound(T key, UnifiedSet<T> other)
    {
        int index = this.index(key);

        Object cur = this.table[index];
        if (cur == null)
        {
            return;
        }
        if (cur instanceof ChainedBucket bucket)
        {
            this.addIfFoundFromChain(bucket, key, other);
            return;
        }
        if (this.nonNullTableObjectEquals(cur, key))
        {
            other.add(this.nonSentinel(cur));
        }
    }

    private void addIfFoundFromChain(ChainedBucket bucket, T key, UnifiedSet<T> other)
    {
        do
        {
            if (this.nonNullTableObjectEquals(bucket.zero, key))
            {
                other.add(this.nonSentinel(bucket.zero));
                return;
            }
            if (bucket.one == null)
            {
                return;
            }
            if (this.nonNullTableObjectEquals(bucket.one, key))
            {
                other.add(this.nonSentinel(bucket.one));
                return;
            }
            if (bucket.two == null)
            {
                return;
            }
            if (this.nonNullTableObjectEquals(bucket.two, key))
            {
                other.add(this.nonSentinel(bucket.two));
                return;
            }
            if (bucket.three == null)
            {
                return;
            }
            if (bucket.three instanceof ChainedBucket chainedBucket)
            {
                bucket = chainedBucket;
                continue;
            }
            if (this.nonNullTableObjectEquals(bucket.three, key))
            {
                other.add(this.nonSentinel(bucket.three));
                return;
            }
            return;
        }
        while (true);
    }

    public boolean retainAllIterable(Iterable<?> iterable)
    {
        if (iterable instanceof Set set)
        {
            return this.retainAllFromSet(set);
        }
        return this.retainAllFromNonSet(iterable);
    }

    private boolean retainAllFromNonSet(Iterable<?> iterable)
    {
        int retainedSize = Iterate.sizeOf(iterable);
        UnifiedSet<T> retainedCopy = new UnifiedSet<T>(retainedSize, this.loadFactor);
        for (Object key : iterable)
        {
            this.addIfFound((T) key, retainedCopy);
        }
        if (retainedCopy.size() < this.size())
        {
            this.maxSize = retainedCopy.maxSize;
            this.occupied = retainedCopy.occupied;
            this.table = retainedCopy.table;
            return true;
        }
        return false;
    }

    private boolean retainAllFromSet(Set<?> collection)
    {
        // TODO: turn iterator into a loop
        boolean result = false;
        Iterator<T> e = this.iterator();
        while (e.hasNext())
        {
            if (!collection.contains(e.next()))
            {
                e.remove();
                result = true;
            }
        }
        return result;
    }

    @Override
    public UnifiedSet<T> clone()
    {
        return new UnifiedSet<T>(this);
    }

    @Override
    public Object[] toArray()
    {
        Object[] result = new Object[this.occupied];
        this.copyToArray(result);
        return result;
    }

    private void copyToArray(Object[] result)
    {
        Object[] table = this.table;
        int count = 0;
        for (int i = 0; i < table.length; i++)
        {
            Object cur = table[i];
            if (cur != null)
            {
                if (cur instanceof ChainedBucket bucket)
                {
                    count = this.copyBucketToArray(result, bucket, count);
                }
                else
                {
                    result[count++] = this.nonSentinel(cur);
                }
            }
        }
    }

    private int copyBucketToArray(Object[] result, ChainedBucket bucket, int count)
    {
        do
        {
            result[count++] = this.nonSentinel(bucket.zero);
            if (bucket.one == null)
            {
                break;
            }
            result[count++] = this.nonSentinel(bucket.one);
            if (bucket.two == null)
            {
                break;
            }
            result[count++] = this.nonSentinel(bucket.two);
            if (bucket.three == null)
            {
                break;
            }
            if (bucket.three instanceof ChainedBucket chainedBucket)
            {
                bucket = chainedBucket;
                continue;
            }
            result[count++] = this.nonSentinel(bucket.three);
            break;
        }
        while (true);
        return count;
    }

    @Override
    public <T> T[] toArray(T[] array)
    {
        int size = this.size();
        T[] result = array.length < size
                ? (T[]) Array.newInstance(array.getClass().getComponentType(), size)
                : array;

        this.copyToArray(result);
        if (size < result.length)
        {
            result[size] = null;
        }
        return result;
    }

    public Iterator<T> iterator()
    {
        return new PositionalIterator();
    }

    protected class PositionalIterator implements Iterator<T>
    {
        protected int count;
        protected int position;
        protected int chainPosition;
        protected boolean lastReturned;

        public boolean hasNext()
        {
            return this.count < UnifiedSet.this.size();
        }

        public void remove()
        {
            if (!this.lastReturned)
            {
                throw new IllegalStateException("next() must be called as many times as remove()");
            }
            this.count--;
            UnifiedSet.this.occupied--;

            if (this.chainPosition != 0)
            {
                this.removeFromChain();
                return;
            }

            int pos = this.position - 1;
            Object key = UnifiedSet.this.table[pos];
            if (key instanceof ChainedBucket bucket)
            {
                this.removeLastFromChain(bucket, pos);
                return;
            }
            UnifiedSet.this.table[pos] = null;
            this.position = pos;
            this.lastReturned = false;
        }

        protected void removeFromChain()
        {
            ChainedBucket chain = (ChainedBucket) UnifiedSet.this.table[this.position];
            chain.remove(--this.chainPosition);
            this.lastReturned = false;
        }

        protected void removeLastFromChain(ChainedBucket bucket, int tableIndex)
        {
            bucket.removeLast(0);
            if (bucket.zero == null)
            {
                UnifiedSet.this.table[tableIndex] = null;
            }
            this.lastReturned = false;
        }

        protected T nextFromChain()
        {
            ChainedBucket bucket = (ChainedBucket) UnifiedSet.this.table[this.position];
            Object cur = bucket.get(this.chainPosition);
            this.chainPosition++;
            if (bucket.get(this.chainPosition) == null)
            {
                this.chainPosition = 0;
                this.position++;
            }
            this.lastReturned = true;
            return UnifiedSet.this.nonSentinel(cur);
        }

        public T next()
        {
            if (!this.hasNext())
            {
                throw new NoSuchElementException("next() called, but the iterator is exhausted");
            }
            this.count++;
            Object[] table = UnifiedSet.this.table;
            if (this.chainPosition != 0)
            {
                return this.nextFromChain();
            }
            while (table[this.position] == null)
            {
                this.position++;
            }
            Object cur = table[this.position];
            if (cur instanceof ChainedBucket)
            {
                return this.nextFromChain();
            }
            this.position++;
            this.lastReturned = true;
            return UnifiedSet.this.nonSentinel(cur);
        }
    }

    private static final class ChainedBucket
    {
        private Object zero;
        private Object one;
        private Object two;
        private Object three;

        private ChainedBucket()
        {
        }

        private ChainedBucket(Object first, Object second)
        {
            this.zero = first;
            this.one = second;
        }

        public void remove(int i)
        {
            if (i > 3)
            {
                this.removeLongChain(this, i - 3);
            }
            else
            {
                switch (i)
                {
                    case 0:
                        this.zero = this.removeLast(0);
                        return;
                    case 1:
                        this.one = this.removeLast(1);
                        return;
                    case 2:
                        this.two = this.removeLast(2);
                        return;
                    case 3:
                        if (this.three instanceof ChainedBucket)
                        {
                            this.removeLongChain(this, i - 3);
                            return;
                        }
                        this.three = null;
                        return;
                    default:
                        throw new AssertionError();
                }
            }
        }

        private void removeLongChain(ChainedBucket oldBucket, int i)
        {
            do
            {
                ChainedBucket bucket = (ChainedBucket) oldBucket.three;
                switch (i)
                {
                    case 0:
                        bucket.zero = bucket.removeLast(0);
                        return;
                    case 1:
                        bucket.one = bucket.removeLast(1);
                        return;
                    case 2:
                        bucket.two = bucket.removeLast(2);
                        return;
                    case 3:
                        if (bucket.three instanceof ChainedBucket)
                        {
                            i -= 3;
                            oldBucket = bucket;
                            continue;
                        }
                        bucket.three = null;
                        return;
                    default:
                        if (bucket.three instanceof ChainedBucket)
                        {
                            i -= 3;
                            oldBucket = bucket;
                            continue;
                        }
                        throw new AssertionError();
                }
            }
            while (true);
        }

        public Object get(int i)
        {
            ChainedBucket bucket = this;
            while (i > 3 && bucket.three instanceof ChainedBucket)
            {
                bucket = (ChainedBucket) bucket.three;
                i -= 3;
            }
            do
            {
                switch (i)
                {
                    case 0:
                        return bucket.zero;
                    case 1:
                        return bucket.one;
                    case 2:
                        return bucket.two;
                    case 3:
                        if (bucket.three instanceof ChainedBucket chainedBucket)
                        {
                            i -= 3;
                            bucket = chainedBucket;
                            continue;
                        }
                        return bucket.three;
                    case 4:
                        return null; // this happens when a bucket is exactly full and we're iterating
                    default:
                        throw new AssertionError();
                }
            }
            while (true);
        }

        public Object removeLast(int cur)
        {
            if (this.three instanceof ChainedBucket)
            {
                return this.removeLast(this);
            }
            if (this.three != null)
            {
                Object result = this.three;
                this.three = null;
                return cur == 3 ? null : result;
            }
            if (this.two != null)
            {
                Object result = this.two;
                this.two = null;
                return cur == 2 ? null : result;
            }
            if (this.one != null)
            {
                Object result = this.one;
                this.one = null;
                return cur == 1 ? null : result;
            }
            this.zero = null;
            return null;
        }

        private Object removeLast(ChainedBucket oldBucket)
        {
            do
            {
                ChainedBucket bucket = (ChainedBucket) oldBucket.three;
                if (bucket.three instanceof ChainedBucket)
                {
                    oldBucket = bucket;
                    continue;
                }
                if (bucket.three != null)
                {
                    Object result = bucket.three;
                    bucket.three = null;
                    return result;
                }
                if (bucket.two != null)
                {
                    Object result = bucket.two;
                    bucket.two = null;
                    return result;
                }
                if (bucket.one != null)
                {
                    Object result = bucket.one;
                    bucket.one = null;
                    return result;
                }
                Object result = bucket.zero;
                oldBucket.three = null;
                return result;
            }
            while (true);
        }

        public ChainedBucket copy()
        {
            ChainedBucket result = new ChainedBucket();
            ChainedBucket dest = result;
            ChainedBucket src = this;
            do
            {
                dest.zero = src.zero;
                dest.one = src.one;
                dest.two = src.two;
                if (src.three instanceof ChainedBucket bucket)
                {
                    dest.three = new ChainedBucket();
                    src = bucket;
                    dest = (ChainedBucket) dest.three;
                    continue;
                }
                dest.three = src.three;
                return result;
            }
            while (true);
        }
    }

    public <V> UnifiedSetMultimap<V, T> groupBy(
            Function<? super T, ? extends V> function)
    {
        return this.groupBy(function, UnifiedSetMultimap.<V, T>newMultimap());
    }

    public T get(T key)
    {
        int index = this.index(key);
        Object cur = this.table[index];

        if (cur == null)
        {
            return null;
        }
        if (cur instanceof ChainedBucket bucket)
        {
            return this.chainedGet(key, bucket);
        }
        if (this.nonNullTableObjectEquals(cur, key))
        {
            return (T) cur;
        }
        return null;
    }

    private T chainedGet(T key, ChainedBucket bucket)
    {
        do
        {
            if (this.nonNullTableObjectEquals(bucket.zero, key))
            {
                return this.nonSentinel(bucket.zero);
            }
            if (bucket.one == null)
            {
                return null;
            }
            if (this.nonNullTableObjectEquals(bucket.one, key))
            {
                return this.nonSentinel(bucket.one);
            }
            if (bucket.two == null)
            {
                return null;
            }
            if (this.nonNullTableObjectEquals(bucket.two, key))
            {
                return this.nonSentinel(bucket.two);
            }
            if (bucket.three instanceof ChainedBucket chainedBucket)
            {
                bucket = chainedBucket;
                continue;
            }
            if (bucket.three == null)
            {
                return null;
            }
            if (this.nonNullTableObjectEquals(bucket.three, key))
            {
                return this.nonSentinel(bucket.three);
            }
            return null;
        }
        while (true);
    }

    public T put(T key)
    {
        int index = this.index(key);
        Object cur = this.table[index];

        if (cur == null)
        {
            this.table[index] = UnifiedSet.toSentinelIfNull(key);
            if (++this.occupied > this.maxSize)
            {
                this.rehash();
            }
            return key;
        }

        if (cur instanceof ChainedBucket || !this.nonNullTableObjectEquals(cur, key))
        {
            return this.chainedPut(key, index);
        }
        return this.nonSentinel(cur);
    }

    private T chainedPut(T key, int index)
    {
        if (this.table[index] instanceof ChainedBucket bucket)
        {
            do
            {
                if (this.nonNullTableObjectEquals(bucket.zero, key))
                {
                    return this.nonSentinel(bucket.zero);
                }
                if (bucket.one == null)
                {
                    bucket.one = UnifiedSet.toSentinelIfNull(key);
                    if (++this.occupied > this.maxSize)
                    {
                        this.rehash();
                    }
                    return key;
                }
                if (this.nonNullTableObjectEquals(bucket.one, key))
                {
                    return this.nonSentinel(bucket.one);
                }
                if (bucket.two == null)
                {
                    bucket.two = UnifiedSet.toSentinelIfNull(key);
                    if (++this.occupied > this.maxSize)
                    {
                        this.rehash();
                    }
                    return key;
                }
                if (this.nonNullTableObjectEquals(bucket.two, key))
                {
                    return this.nonSentinel(bucket.two);
                }
                if (bucket.three instanceof ChainedBucket chainedBucket)
                {
                    bucket = chainedBucket;
                    continue;
                }
                if (bucket.three == null)
                {
                    bucket.three = UnifiedSet.toSentinelIfNull(key);
                    if (++this.occupied > this.maxSize)
                    {
                        this.rehash();
                    }
                    return key;
                }
                if (this.nonNullTableObjectEquals(bucket.three, key))
                {
                    return this.nonSentinel(bucket.three);
                }
                bucket.three = new ChainedBucket(bucket.three, key);
                if (++this.occupied > this.maxSize)
                {
                    this.rehash();
                }
                return key;
            }
            while (true);
        }
        ChainedBucket newBucket = new ChainedBucket(this.table[index], key);
        this.table[index] = newBucket;
        if (++this.occupied > this.maxSize)
        {
            this.rehash();
        }
        return key;
    }

    public T removeFromPool(T key)
    {
        int index = this.index(key);
        Object cur = this.table[index];
        if (cur == null)
        {
            return null;
        }
        if (cur instanceof ChainedBucket bucket)
        {
            return this.removeFromChainForPool(bucket, key, index);
        }
        if (this.nonNullTableObjectEquals(cur, key))
        {
            this.table[index] = null;
            this.occupied--;
            return this.nonSentinel(cur);
        }
        return null;
    }

    private T removeFromChainForPool(ChainedBucket bucket, T key, int index)
    {
        if (this.nonNullTableObjectEquals(bucket.zero, key))
        {
            Object result = bucket.zero;
            bucket.zero = bucket.removeLast(0);
            if (bucket.zero == null)
            {
                this.table[index] = null;
            }
            this.occupied--;
            return this.nonSentinel(result);
        }
        if (bucket.one == null)
        {
            return null;
        }
        if (this.nonNullTableObjectEquals(bucket.one, key))
        {
            Object result = bucket.one;
            bucket.one = bucket.removeLast(1);
            this.occupied--;
            return this.nonSentinel(result);
        }
        if (bucket.two == null)
        {
            return null;
        }
        if (this.nonNullTableObjectEquals(bucket.two, key))
        {
            Object result = bucket.two;
            bucket.two = bucket.removeLast(2);
            this.occupied--;
            return this.nonSentinel(result);
        }
        if (bucket.three == null)
        {
            return null;
        }
        if (bucket.three instanceof ChainedBucket)
        {
            return this.removeDeepChainForPool(bucket, key);
        }
        if (this.nonNullTableObjectEquals(bucket.three, key))
        {
            Object result = bucket.three;
            bucket.three = bucket.removeLast(3);
            this.occupied--;
            return this.nonSentinel(result);
        }
        return null;
    }

    private T removeDeepChainForPool(ChainedBucket oldBucket, T key)
    {
        do
        {
            ChainedBucket bucket = (ChainedBucket) oldBucket.three;
            if (this.nonNullTableObjectEquals(bucket.zero, key))
            {
                Object result = bucket.zero;
                bucket.zero = bucket.removeLast(0);
                if (bucket.zero == null)
                {
                    oldBucket.three = null;
                }
                this.occupied--;
                return this.nonSentinel(result);
            }
            if (bucket.one == null)
            {
                return null;
            }
            if (this.nonNullTableObjectEquals(bucket.one, key))
            {
                Object result = bucket.one;
                bucket.one = bucket.removeLast(1);
                this.occupied--;
                return this.nonSentinel(result);
            }
            if (bucket.two == null)
            {
                return null;
            }
            if (this.nonNullTableObjectEquals(bucket.two, key))
            {
                Object result = bucket.two;
                bucket.two = bucket.removeLast(2);
                this.occupied--;
                return this.nonSentinel(result);
            }
            if (bucket.three == null)
            {
                return null;
            }
            if (bucket.three instanceof ChainedBucket)
            {
                oldBucket = bucket;
                continue;
            }
            if (this.nonNullTableObjectEquals(bucket.three, key))
            {
                Object result = bucket.three;
                bucket.three = bucket.removeLast(3);
                this.occupied--;
                return this.nonSentinel(result);
            }
            return null;
        }
        while (true);
    }

    private T nonSentinel(Object key)
    {
        return key == NULL_KEY ? null : (T) key;
    }

    private static Object toSentinelIfNull(Object key)
    {
        if (key == null)
        {
            return NULL_KEY;
        }
        return key;
    }

    private boolean nonNullTableObjectEquals(Object cur, T key)
    {
        return cur == key || (cur == NULL_KEY ? key == null : cur.equals(key));
    }

    @Beta
    public ParallelUnsortedSetIterable<T> asParallel(ExecutorService executorService, int batchSize)
    {
        if (executorService == null)
        {
            throw new NullPointerException();
        }
        if (batchSize < 1)
        {
            throw new IllegalArgumentException();
        }
        return new UnifiedSetParallelUnsortedIterable(executorService, batchSize);
    }

    private final class UnifiedUnsortedSetBatch extends AbstractBatch<T> implements RootUnsortedSetBatch<T>
    {
        private final int chunkStartIndex;
        private final int chunkEndIndex;

        private UnifiedUnsortedSetBatch(int chunkStartIndex, int chunkEndIndex)
        {
            this.chunkStartIndex = chunkStartIndex;
            this.chunkEndIndex = chunkEndIndex;
        }

        public void forEach(Procedure<? super T> procedure)
        {
            UnifiedSet.this.each(procedure, this.chunkStartIndex, this.chunkEndIndex);
        }

        public boolean anySatisfy(Predicate<? super T> predicate)
        {
            return UnifiedSet.this.shortCircuit(predicate, true, true, false, this.chunkStartIndex, this.chunkEndIndex);
        }

        public boolean allSatisfy(Predicate<? super T> predicate)
        {
            return UnifiedSet.this.shortCircuit(predicate, false, false, true, this.chunkStartIndex, this.chunkEndIndex);
        }

        public T detect(Predicate<? super T> predicate)
        {
            return UnifiedSet.this.detect(predicate, this.chunkStartIndex, this.chunkEndIndex);
        }

        public UnsortedSetBatch<T> select(Predicate<? super T> predicate)
        {
            return new SelectUnsortedSetBatch<T>(this, predicate);
        }

        public <V> UnsortedBagBatch<V> collect(Function<? super T, ? extends V> function)
        {
            return new CollectUnsortedBagBatch<T, V>(this, function);
        }

        public <V> UnsortedBagBatch<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
        {
            return new FlatCollectUnsortedBagBatch<T, V>(this, function);
        }
    }

    private final class UnifiedSetParallelUnsortedIterable extends AbstractParallelUnsortedSetIterable<T, RootUnsortedSetBatch<T>>
    {
        private final ExecutorService executorService;
        private final int batchSize;

        private UnifiedSetParallelUnsortedIterable(ExecutorService executorService, int batchSize)
        {
            this.executorService = executorService;
            this.batchSize = batchSize;
        }

        @Override
        public ExecutorService getExecutorService()
        {
            return this.executorService;
        }

        @Override
        public int getBatchSize()
        {
            return this.batchSize;
        }

        @Override
        public LazyIterable<RootUnsortedSetBatch<T>> split()
        {
            return new UnifiedSetParallelSplitLazyIterable();
        }

        public void forEach(Procedure<? super T> procedure)
        {
            AbstractParallelIterable.forEach(this, procedure);
        }

        public boolean anySatisfy(Predicate<? super T> predicate)
        {
            return AbstractParallelIterable.anySatisfy(this, predicate);
        }

        public boolean allSatisfy(Predicate<? super T> predicate)
        {
            return AbstractParallelIterable.allSatisfy(this, predicate);
        }

        public T detect(Predicate<? super T> predicate)
        {
            return AbstractParallelIterable.detect(this, predicate);
        }

        @Override
        public Object[] toArray()
        {
            // TODO: Implement in parallel
            return UnifiedSet.this.toArray();
        }

        @Override
        public <E> E[] toArray(E[] array)
        {
            // TODO: Implement in parallel
            return UnifiedSet.this.toArray(array);
        }

        private class UnifiedSetParallelSplitIterator implements Iterator<RootUnsortedSetBatch<T>>
        {
            protected int chunkIndex;

            public boolean hasNext()
            {
                return this.chunkIndex * UnifiedSetParallelUnsortedIterable.this.batchSize < UnifiedSet.this.table.length;
            }

            public RootUnsortedSetBatch<T> next()
            {
                int chunkStartIndex = this.chunkIndex * UnifiedSetParallelUnsortedIterable.this.batchSize;
                int chunkEndIndex = (this.chunkIndex + 1) * UnifiedSetParallelUnsortedIterable.this.batchSize;
                int truncatedChunkEndIndex = Math.min(chunkEndIndex, UnifiedSet.this.table.length);
                this.chunkIndex++;
                return new UnifiedUnsortedSetBatch(chunkStartIndex, truncatedChunkEndIndex);
            }

            public void remove()
            {
                throw new UnsupportedOperationException("Cannot call remove() on " + this.getClass().getSimpleName());
            }
        }

        private class UnifiedSetParallelSplitLazyIterable
                extends AbstractLazyIterable<RootUnsortedSetBatch<T>>
        {
            public void each(Procedure<? super RootUnsortedSetBatch<T>> procedure)
            {
                for (RootUnsortedSetBatch<T> chunk : this)
                {
                    procedure.value(chunk);
                }
            }

            public Iterator<RootUnsortedSetBatch<T>> iterator()
            {
                return new UnifiedSetParallelSplitIterator();
            }
        }
    }
}
