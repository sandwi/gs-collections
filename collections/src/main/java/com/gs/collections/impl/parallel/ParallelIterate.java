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

package com.gs.collections.impl.parallel;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.function.primitive.DoubleFunction;
import com.gs.collections.api.block.function.primitive.DoubleFunction0;
import com.gs.collections.api.block.function.primitive.FloatFunction;
import com.gs.collections.api.block.function.primitive.IntFunction;
import com.gs.collections.api.block.function.primitive.LongFunction;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.block.procedure.Procedure2;
import com.gs.collections.api.block.procedure.primitive.ObjectIntProcedure;
import com.gs.collections.api.block.procedure.primitive.ObjectLongProcedure;
import com.gs.collections.api.list.ListIterable;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.map.primitive.ObjectDoubleMap;
import com.gs.collections.api.map.primitive.ObjectLongMap;
import com.gs.collections.api.multimap.MutableMultimap;
import com.gs.collections.api.tuple.primitive.DoubleDoublePair;
import com.gs.collections.impl.block.factory.Functions0;
import com.gs.collections.impl.block.procedure.MultimapPutProcedure;
import com.gs.collections.impl.block.procedure.MutatingAggregationProcedure;
import com.gs.collections.impl.block.procedure.NonMutatingAggregationProcedure;
import com.gs.collections.impl.factory.Maps;
import com.gs.collections.impl.list.fixed.ArrayAdapter;
import com.gs.collections.impl.map.mutable.ConcurrentHashMap;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.map.mutable.primitive.ObjectDoubleHashMap;
import com.gs.collections.impl.map.mutable.primitive.ObjectLongHashMap;
import com.gs.collections.impl.multimap.list.SynchronizedPutFastListMultimap;
import com.gs.collections.impl.tuple.primitive.PrimitiveTuples;
import com.gs.collections.impl.utility.Iterate;

import static com.gs.collections.impl.factory.Iterables.iList;

/**
 * The ParallelIterate class contains several parallel algorithms that work with Collections.  All of the higher
 * level parallel algorithms depend on the basic parallel algorithm named {@code forEach}.  The forEach algorithm employs
 * a batching fork and join approach.
 * <p>
 * All Collections that are not either a {@link RandomAccess} or {@link List} are first converted to a Java array
 * using {@link Iterate#toArray(Iterable)}, and then run with one of the {@code ParallelArrayIterate.forEach} methods.
 *
 * @see ParallelArrayIterate
 */
public final class ParallelIterate
{
    static final int DEFAULT_MIN_FORK_SIZE = 10000;
    static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    static final int TASK_RATIO = 2;
    static final int DEFAULT_PARALLEL_TASK_COUNT = ParallelIterate.getDefaultTaskCount();
    static final ExecutorService EXECUTOR_SERVICE = ParallelIterate.newPooledExecutor(ParallelIterate.class.getSimpleName(), true);

    private ParallelIterate()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    static boolean isExecutorShutdown()
    {
        return ParallelIterate.EXECUTOR_SERVICE.isShutdown();
    }

    static void shutdownExecutor()
    {
        ParallelIterate.EXECUTOR_SERVICE.shutdown();
    }

    /**
     * Iterate over the collection specified, in parallel batches using default runtime parameter values.  The
     * {@code ObjectIntProcedure} used must be stateless, or use concurrent aware objects if they are to be shared.
     * <p>
     * e.g.
     * <pre>
     * {@code final Map<Integer, Object> chm = new ConcurrentHashMap<Integer, Object>();}
     * ParallelIterate.<b>forEachWithIndex</b>(collection, new ObjectIntProcedure()
     * {
     *     public void value(Object object, int index)
     *     {
     *         chm.put(index, object);
     *     }
     * });
     * </pre>
     */
    public static <T> void forEachWithIndex(
            Iterable<T> iterable,
            ObjectIntProcedure<? super T> objectIntProcedure)
    {
        ParallelIterate.forEachWithIndex(iterable, objectIntProcedure, ParallelIterate.EXECUTOR_SERVICE);
    }

    /**
     * Iterate over the collection specified in parallel batches using the default runtime parameters.  The
     * ObjectIntProcedure used must be stateless, or use concurrent aware objects if they are to be shared.  The code
     * is executed against the specified executor.
     * <p>
     * <pre>e.g.
     * {@code final Map<Integer, Object> chm = new ConcurrentHashMap<Integer, Object>();}
     * ParallelIterate.<b>forEachWithIndex</b>(collection, new ObjectIntProcedure()
     * {
     *     public void value(Object object, int index)
     *     {
     *         chm.put(index, object);
     *     }
     * }, executor);
     * </pre>
     *
     * @param executor Use this executor for all execution.
     */
    public static <T, BT extends ObjectIntProcedure<? super T>> void forEachWithIndex(
            Iterable<T> iterable,
            BT procedure,
            Executor executor)
    {
        ParallelIterate.forEachWithIndex(
                iterable,
                new PassThruObjectIntProcedureFactory<BT>(procedure),
                new PassThruCombiner<BT>(), executor);
    }

    /**
     * Iterate over the collection specified in parallel batches.  The
     * ObjectIntProcedure used must be stateless, or use concurrent aware objects if they are to be shared.  The
     * specified minimum fork size and task count are used instead of the default values.
     *
     * @param minForkSize Only run in parallel if input collection is longer than this.
     * @param taskCount   How many parallel tasks to submit to the executor.
     * @see #forEachWithIndex(Iterable, ObjectIntProcedure)
     */
    public static <T, BT extends ObjectIntProcedure<? super T>> void forEachWithIndex(
            Iterable<T> iterable,
            BT procedure,
            int minForkSize,
            int taskCount)
    {
        ParallelIterate.forEachWithIndex(
                iterable,
                new PassThruObjectIntProcedureFactory<BT>(procedure),
                new PassThruCombiner<BT>(),
                minForkSize,
                taskCount);
    }

    public static <T, BT extends ObjectIntProcedure<? super T>> void forEachWithIndex(
            Iterable<T> iterable,
            ObjectIntProcedureFactory<BT> procedureFactory,
            Combiner<BT> combiner,
            Executor executor)
    {
        int taskCount = Math.max(
                ParallelIterate.DEFAULT_PARALLEL_TASK_COUNT,
                Iterate.sizeOf(iterable) / ParallelIterate.DEFAULT_MIN_FORK_SIZE);
        ParallelIterate.forEachWithIndex(
                iterable,
                procedureFactory,
                combiner,
                ParallelIterate.DEFAULT_MIN_FORK_SIZE,
                taskCount,
                executor);
    }

    public static <T, BT extends ObjectIntProcedure<? super T>> void forEachWithIndex(
            Iterable<T> iterable,
            ObjectIntProcedureFactory<BT> procedureFactory,
            Combiner<BT> combiner,
            int minForkSize,
            int taskCount)
    {
        ParallelIterate.forEachWithIndex(iterable, procedureFactory, combiner, minForkSize, taskCount, ParallelIterate.EXECUTOR_SERVICE);
    }

    public static <T, BT extends ObjectIntProcedure<? super T>> void forEachWithIndex(
            Iterable<T> iterable,
            ObjectIntProcedureFactory<BT> procedureFactory,
            Combiner<BT> combiner,
            int minForkSize,
            int taskCount,
            Executor executor)
    {
        if (Iterate.notEmpty(iterable))
        {
            if (iterable instanceof RandomAccess || iterable instanceof ListIterable
                    && iterable instanceof List)
            {
                ParallelIterate.forEachWithIndexInListOnExecutor(
                        (List<T>) iterable,
                        procedureFactory,
                        combiner,
                        minForkSize,
                        taskCount,
                        executor);
            }
            else
            {
                ParallelIterate.forEachWithIndexInListOnExecutor(
                        ArrayAdapter.adapt((T[]) Iterate.toArray(iterable)),
                        procedureFactory,
                        combiner,
                        minForkSize,
                        taskCount,
                        executor);
            }
        }
    }

    public static <T, BT extends ObjectIntProcedure<? super T>> void forEachWithIndexInListOnExecutor(
            List<T> list,
            ObjectIntProcedureFactory<BT> procedureFactory,
            Combiner<BT> combiner,
            int minForkSize,
            int taskCount,
            Executor executor)
    {
        int size = list.size();
        if (size < minForkSize)
        {
            BT procedure = procedureFactory.create();
            Iterate.forEachWithIndex(list, procedure);
            if (combiner.useCombineOne())
            {
                combiner.combineOne(procedure);
            }
            else
            {
                combiner.combineAll(iList(procedure));
            }
        }
        else
        {
            int threadCount = Math.min(size, taskCount);
            ObjectIntProcedureFJTaskRunner<T, BT> runner =
                    new ObjectIntProcedureFJTaskRunner<T, BT>(combiner, threadCount);
            runner.executeAndCombine(executor, procedureFactory, list);
        }
    }

    /**
     * Iterate over the collection specified in parallel batches using default runtime parameter values.  The
     * {@code Procedure} used must be stateless, or use concurrent aware objects if they are to be shared.
     * <p>
     * e.g.
     * <pre>
     * {@code final Map<Object, Boolean> chm = new ConcurrentHashMap<Object, Boolean>();}
     * ParallelIterate.<b>forEach</b>(collection, new Procedure()
     * {
     *     public void value(Object object)
     *     {
     *         chm.put(object, Boolean.TRUE);
     *     }
     * });
     * </pre>
     */
    public static <T> void forEach(Iterable<T> iterable, Procedure<? super T> procedure)
    {
        ParallelIterate.forEach(iterable, procedure, ParallelIterate.EXECUTOR_SERVICE);
    }

    /**
     * Iterate over the collection specified in parallel batches using default runtime parameter values.  The
     * {@code Procedure} used must be stateless, or use concurrent aware objects if they are to be shared.
     * <p>
     * e.g.
     * <pre>
     * {@code final Map<Object, Boolean> chm = new ConcurrentHashMap<Object, Boolean>();}
     * ParallelIterate.<b>forEachBatchSize</b>(collection, new Procedure()
     * {
     *     public void value(Object object)
     *     {
     *         chm.put(object, Boolean.TRUE);
     *     }
     * }, 100);
     * </pre>
     */
    public static <T> void forEach(Iterable<T> iterable, Procedure<? super T> procedure, int batchSize)
    {
        ParallelIterate.forEach(iterable, procedure, batchSize, ParallelIterate.EXECUTOR_SERVICE);
    }

    public static <T> void forEach(Iterable<T> iterable, Procedure<? super T> procedure, int batchSize, Executor executor)
    {
        ParallelIterate.forEach(iterable, procedure, batchSize, ParallelIterate.calculateTaskCount(iterable, batchSize), executor);
    }

    /**
     * Iterate over the collection specified in parallel batches using default runtime parameter values
     * and the specified executor.
     * The {@code Procedure} used must be stateless, or use concurrent aware objects if they are to be shared.
     *
     * @param executor Use this executor for all execution.
     * @see #forEach(Iterable, Procedure)
     */
    public static <T, BT extends Procedure<? super T>> void forEach(
            Iterable<T> iterable,
            BT procedure,
            Executor executor)
    {
        ParallelIterate.forEach(
                iterable,
                new PassThruProcedureFactory<BT>(procedure),
                new PassThruCombiner<BT>(),
                executor);
    }

    /**
     * Iterate over the collection specified in parallel batches using the specified minimum fork and task count sizes.
     * The {@code Procedure} used must be stateless, or use concurrent aware objects if they are to be shared.
     *
     * @param minForkSize Only run in parallel if input collection is longer than this.
     * @param taskCount   How many parallel tasks to submit to the executor.
     *                    TODO: How does the taskCount relate to the number of threads in the executor?
     * @see #forEach(Iterable, Procedure)
     */
    public static <T, BT extends Procedure<? super T>> void forEach(
            Iterable<T> iterable,
            BT procedure,
            int minForkSize,
            int taskCount)
    {
        ParallelIterate.forEach(iterable, procedure, minForkSize, taskCount, ParallelIterate.EXECUTOR_SERVICE);
    }

    public static <T, BT extends Procedure<? super T>> void forEach(
            Iterable<T> iterable,
            BT procedure,
            int minForkSize,
            int taskCount,
            Executor executor)
    {
        ParallelIterate.forEach(
                iterable,
                new PassThruProcedureFactory<BT>(procedure),
                new PassThruCombiner<BT>(),
                minForkSize,
                taskCount,
                executor);
    }

    public static <T, BT extends Procedure<? super T>> void forEach(
            Iterable<T> iterable,
            ProcedureFactory<BT> procedureFactory,
            Combiner<BT> combiner,
            Executor executor)
    {
        ParallelIterate.forEach(iterable, procedureFactory, combiner, ParallelIterate.DEFAULT_MIN_FORK_SIZE, executor);
    }

    public static <T, BT extends Procedure<? super T>> void forEach(
            Iterable<T> iterable,
            ProcedureFactory<BT> procedureFactory,
            Combiner<BT> combiner)
    {
        ParallelIterate.forEach(iterable, procedureFactory, combiner, ParallelIterate.EXECUTOR_SERVICE);
    }

    /**
     * Iterate over the collection specified in parallel batches using the default values for the task size.  The
     * ProcedureFactory can create stateful closures that will be collected and combined using the specified Combiner.
     * <p>
     * <pre>e.g. The <b>ParallelIterate.select()</b> implementation
     * <p>
     * {@code CollectionCombiner<T, SelectProcedure<T>> combiner = CollectionCombiner.forSelect(collection);}
     * ParallelIterate.<b>forEach</b>(collection,{@code new SelectProcedureFactory<T>(predicate, taskSize), combiner, 1000);}
     * </pre>
     */
    @SuppressWarnings("JavaDoc")
    public static <T, BT extends Procedure<? super T>> void forEach(
            Iterable<T> iterable,
            ProcedureFactory<BT> procedureFactory,
            Combiner<BT> combiner,
            int batchSize)
    {
        ParallelIterate.forEach(iterable, procedureFactory, combiner, batchSize, ParallelIterate.EXECUTOR_SERVICE);
    }

    public static <T, BT extends Procedure<? super T>> void forEach(
            Iterable<T> iterable,
            ProcedureFactory<BT> procedureFactory,
            Combiner<BT> combiner,
            int batchSize,
            Executor executor)
    {
        ParallelIterate.forEach(iterable, procedureFactory, combiner, batchSize, ParallelIterate.calculateTaskCount(iterable, batchSize), executor);
    }

    /**
     * Iterate over the collection specified in parallel batches using the default values for the task size.  The
     * ProcedureFactory can create stateful closures that will be collected and combined using the specified Combiner.
     * <p>
     * <pre>e.g. The <b>ParallelIterate.select()</b> implementation
     * <p>
     * int taskCount = Math.max(DEFAULT_PARALLEL_TASK_COUNT, collection.size() / DEFAULT_MIN_FORK_SIZE);
     * final int taskSize = collection.size() / taskCount / 2;
     * {@code CollectionCombiner<T, SelectProcedure<T>> combiner = CollectionCombiner.forSelect(collection);}
     * ParallelIterate.<b>forEach</b>(collection,{@code new SelectProcedureFactory<T>(predicate, taskSize), combiner, DEFAULT_MIN_FORK_SIZE, taskCount);}
     * </pre>
     */
    @SuppressWarnings("JavaDoc")
    public static <T, BT extends Procedure<? super T>> void forEach(
            Iterable<T> iterable,
            ProcedureFactory<BT> procedureFactory,
            Combiner<BT> combiner,
            int minForkSize,
            int taskCount)
    {
        ParallelIterate.forEach(iterable, procedureFactory, combiner, minForkSize, taskCount, ParallelIterate.EXECUTOR_SERVICE);
    }

    public static <T, BT extends Procedure<? super T>> void forEach(
            Iterable<T> iterable,
            ProcedureFactory<BT> procedureFactory,
            Combiner<BT> combiner,
            int minForkSize,
            int taskCount,
            Executor executor)
    {
        if (Iterate.notEmpty(iterable))
        {
            if (iterable instanceof BatchIterable)
            {
                ParallelIterate.forEachInBatchWithExecutor(
                        (BatchIterable<T>) iterable,
                        procedureFactory,
                        combiner,
                        minForkSize,
                        taskCount,
                        executor);
            }
            else if ((iterable instanceof RandomAccess || iterable instanceof ListIterable)
                    && iterable instanceof List)
            {
                ParallelIterate.forEachInListOnExecutor(
                        (List<T>) iterable,
                        procedureFactory,
                        combiner,
                        minForkSize,
                        taskCount,
                        executor);
            }
            else
            {
                ParallelArrayIterate.forEachOn(
                        (T[]) Iterate.toArray(iterable),
                        procedureFactory,
                        combiner,
                        minForkSize,
                        taskCount,
                        executor);
            }
        }
    }

    public static <T, BT extends Procedure<? super T>> void forEachInListOnExecutor(
            List<T> list,
            ProcedureFactory<BT> procedureFactory,
            Combiner<BT> combiner,
            int minForkSize,
            int taskCount,
            Executor executor)
    {
        int size = list.size();
        if (size < minForkSize)
        {
            BT procedure = procedureFactory.create();
            Iterate.forEach(list, procedure);
            if (combiner.useCombineOne())
            {
                combiner.combineOne(procedure);
            }
            else
            {
                combiner.combineAll(iList(procedure));
            }
        }
        else
        {
            int threadCount = Math.min(size, taskCount);
            ProcedureFJTaskRunner<T, BT> runner =
                    new ProcedureFJTaskRunner<T, BT>(combiner, threadCount);
            runner.executeAndCombine(executor, procedureFactory, list);
        }
    }

    public static <T, BT extends Procedure<? super T>> void forEachInBatchWithExecutor(
            BatchIterable<T> set,
            ProcedureFactory<BT> procedureFactory,
            Combiner<BT> combiner,
            int minForkSize,
            int taskCount,
            Executor executor)
    {
        int size = set.size();
        if (size < minForkSize)
        {
            BT procedure = procedureFactory.create();
            set.forEach(procedure);
            if (combiner.useCombineOne())
            {
                combiner.combineOne(procedure);
            }
            else
            {
                combiner.combineAll(iList(procedure));
            }
        }
        else
        {
            int threadCount = Math.min(size, Math.min(taskCount, set.getBatchCount((int) Math.ceil((double) size / (double) taskCount))));
            BatchIterableProcedureFJTaskRunner<T, BT> runner =
                    new BatchIterableProcedureFJTaskRunner<T, BT>(combiner, threadCount);
            runner.executeAndCombine(executor, procedureFactory, set);
        }
    }

    /**
     * Same effect as {@link Iterate#select(Iterable, Predicate)}, but executed in parallel batches.
     *
     * @return The selected elements. The Collection will be of the same type as the input (List or Set)
     * and will be in the same order as the input (if it is an ordered collection).
     * @see ParallelIterate#select(Iterable, Predicate, boolean)
     */
    public static <T> Collection<T> select(
            Iterable<T> iterable,
            Predicate<? super T> predicate)
    {
        return ParallelIterate.select(iterable, predicate, false);
    }

    /**
     * Same effect as {@link Iterate#select(Iterable, Predicate)}, but executed in parallel batches,
     * and with a potentially reordered result.
     *
     * @param allowReorderedResult If the result can be in a different order.
     *                             Allowing reordering may yield faster execution.
     * @return The selected elements. The Collection will be of the same type (List or Set) as the input.
     */
    public static <T> Collection<T> select(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            boolean allowReorderedResult)
    {
        return ParallelIterate.select(iterable, predicate, null, allowReorderedResult);
    }

    /**
     * Same effect as {@link Iterate#select(Iterable, Predicate)}, but executed in parallel batches,
     * and writing output into the specified collection.
     *
     * @param target               Where to write the output.
     * @param allowReorderedResult If the result can be in a different order.
     *                             Allowing reordering may yield faster execution.
     * @return The 'target' collection, with the selected elements added.
     */
    public static <T, R extends Collection<T>> R select(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            R target,
            boolean allowReorderedResult)
    {
        return ParallelIterate.select(
                iterable,
                predicate,
                target,
                ParallelIterate.DEFAULT_MIN_FORK_SIZE,
                ParallelIterate.EXECUTOR_SERVICE,
                allowReorderedResult);
    }

    /**
     * Same effect as {@link Iterate#select(Iterable, Predicate)}, but executed in parallel batches,
     * and writing output into the specified collection.
     *
     * @param target               Where to write the output.
     * @param allowReorderedResult If the result can be in a different order.
     *                             Allowing reordering may yield faster execution.
     * @return The 'target' collection, with the selected elements added.
     */
    public static <T, R extends Collection<T>> R select(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            R target,
            int batchSize,
            Executor executor,
            boolean allowReorderedResult)
    {
        FastListSelectProcedureCombiner<T> combiner = new FastListSelectProcedureCombiner<T>(iterable, target, 10, allowReorderedResult);
        FastListSelectProcedureFactory<T> procedureFactory = new FastListSelectProcedureFactory<T>(predicate, batchSize);
        ParallelIterate.forEach(
                iterable,
                procedureFactory,
                combiner,
                batchSize,
                ParallelIterate.calculateTaskCount(iterable, batchSize),
                executor);
        return (R) combiner.getResult();
    }

    private static <T> int calculateTaskCount(Iterable<T> iterable, int batchSize)
    {
        if (iterable instanceof BatchIterable<?> batchIterable)
        {
            return ParallelIterate.calculateTaskCount(batchIterable, batchSize);
        }
        return ParallelIterate.calculateTaskCount(Iterate.sizeOf(iterable), batchSize);
    }

    private static <T> int calculateTaskCount(BatchIterable<T> batchIterable, int batchSize)
    {
        return Math.max(2, batchIterable.getBatchCount(batchSize));
    }

    private static int calculateTaskCount(int size, int batchSize)
    {
        return Math.max(2, size / batchSize);
    }

    /**
     * Same effect as {@link Iterate#reject(Iterable, Predicate)}, but executed in parallel batches.
     *
     * @return The rejected elements. The Collection will be of the same type as the input (List or Set)
     * and will be in the same order as the input (if it is an ordered collection).
     * @see ParallelIterate#reject(Iterable, Predicate, boolean)
     */
    public static <T> Collection<T> reject(
            Iterable<T> iterable,
            Predicate<? super T> predicate)
    {
        return ParallelIterate.reject(iterable, predicate, false);
    }

    /**
     * Same effect as {@link Iterate#reject(Iterable, Predicate)}, but executed in parallel batches,
     * and with a potentially reordered result.
     *
     * @param allowReorderedResult If the result can be in a different order.
     *                             Allowing reordering may yield faster execution.
     * @return The rejected elements. The Collection will be of the same type (List or Set) as the input.
     */
    public static <T> Collection<T> reject(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            boolean allowReorderedResult)
    {
        return ParallelIterate.reject(iterable, predicate, null, allowReorderedResult);
    }

    /**
     * Same effect as {@link Iterate#reject(Iterable, Predicate)}, but executed in parallel batches,
     * and writing output into the specified collection.
     *
     * @param target               Where to write the output.
     * @param allowReorderedResult If the result can be in a different order.
     *                             Allowing reordering may yield faster execution.
     * @return The 'target' collection, with the rejected elements added.
     */
    public static <T, R extends Collection<T>> R reject(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            R target,
            boolean allowReorderedResult)
    {
        return ParallelIterate.reject(
                iterable,
                predicate,
                target,
                ParallelIterate.DEFAULT_MIN_FORK_SIZE,
                ParallelIterate.EXECUTOR_SERVICE,
                allowReorderedResult);
    }

    public static <T, R extends Collection<T>> R reject(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            R target,
            int batchSize,
            Executor executor,
            boolean allowReorderedResult)
    {
        FastListRejectProcedureCombiner<T> combiner = new FastListRejectProcedureCombiner<T>(iterable, target, 10, allowReorderedResult);
        FastListRejectProcedureFactory<T> procedureFactory = new FastListRejectProcedureFactory<T>(predicate, batchSize);
        ParallelIterate.forEach(
                iterable,
                procedureFactory,
                combiner,
                batchSize,
                ParallelIterate.calculateTaskCount(iterable, batchSize),
                executor);
        return (R) combiner.getResult();
    }

    /**
     * Same effect as {@link Iterate#count(Iterable, Predicate)}, but executed in parallel batches.
     *
     * @return The number of elements which satisfy the predicate.
     */
    public static <T> int count(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        return ParallelIterate.count(iterable, predicate, ParallelIterate.DEFAULT_MIN_FORK_SIZE, ParallelIterate.EXECUTOR_SERVICE);
    }

    /**
     * Same effect as {@link Iterate#count(Iterable, Predicate)}, but executed in parallel batches.
     *
     * @return The number of elements which satisfy the predicate.
     */
    public static <T> int count(Iterable<T> iterable, Predicate<? super T> predicate, int batchSize, Executor executor)
    {
        CountCombiner<T> combiner = new CountCombiner<T>();
        CountProcedureFactory<T> procedureFactory = new CountProcedureFactory<T>(predicate);
        ParallelIterate.forEach(
                iterable,
                procedureFactory,
                combiner,
                batchSize,
                executor);
        return combiner.getCount();
    }

    /**
     * Same effect as {@link Iterate#collect(Iterable, Function)},
     * but executed in parallel batches.
     *
     * @return The collected elements. The Collection will be of the same type as the input (List or Set)
     * and will be in the same order as the input (if it is an ordered collection).
     * @see ParallelIterate#collect(Iterable, Function, boolean)
     */
    public static <T, V> Collection<V> collect(
            Iterable<T> iterable,
            Function<? super T, V> function)
    {
        return ParallelIterate.collect(iterable, function, false);
    }

    /**
     * Same effect as {@link Iterate#collect(Iterable, Function)}, but executed in parallel batches,
     * and with potentially reordered result.
     *
     * @param allowReorderedResult If the result can be in a different order.
     *                             Allowing reordering may yield faster execution.
     * @return The collected elements. The Collection will be of the same type
     * (List or Set) as the input.
     */
    public static <T, V> Collection<V> collect(
            Iterable<T> iterable,
            Function<? super T, V> function,
            boolean allowReorderedResult)
    {
        return ParallelIterate.collect(iterable, function, null, allowReorderedResult);
    }

    /**
     * Same effect as {@link Iterate#collect(Iterable, Function)}, but executed in parallel batches,
     * and writing output into the specified collection.
     *
     * @param target               Where to write the output.
     * @param allowReorderedResult If the result can be in a different order.
     *                             Allowing reordering may yield faster execution.
     * @return The 'target' collection, with the collected elements added.
     */
    public static <T, V, R extends Collection<V>> R collect(
            Iterable<T> iterable,
            Function<? super T, V> function,
            R target,
            boolean allowReorderedResult)
    {
        return ParallelIterate.collect(
                iterable,
                function,
                target,
                ParallelIterate.DEFAULT_MIN_FORK_SIZE,
                ParallelIterate.EXECUTOR_SERVICE,
                allowReorderedResult);
    }

    public static <T, V, R extends Collection<V>> R collect(
            Iterable<T> iterable,
            Function<? super T, V> function,
            R target,
            int batchSize,
            Executor executor,
            boolean allowReorderedResult)
    {
        int size = Iterate.sizeOf(iterable);
        FastListCollectProcedureCombiner<T, V> combiner = new FastListCollectProcedureCombiner<T, V>(iterable, target, size, allowReorderedResult);
        int taskCount = ParallelIterate.calculateTaskCount(iterable, batchSize);
        FastListCollectProcedureFactory<T, V> procedureFactory = new FastListCollectProcedureFactory<T, V>(function, size / taskCount);
        ParallelIterate.forEach(
                iterable,
                procedureFactory,
                combiner,
                batchSize,
                taskCount,
                executor);
        return (R) combiner.getResult();
    }

    public static <T, V> Collection<V> flatCollect(
            Iterable<T> iterable,
            Function<? super T, Collection<V>> function)
    {
        return ParallelIterate.flatCollect(iterable, function, false);
    }

    public static <T, V> Collection<V> flatCollect(
            Iterable<T> iterable,
            Function<? super T, Collection<V>> function,
            boolean allowReorderedResult)
    {
        return ParallelIterate.flatCollect(iterable, function, null, allowReorderedResult);
    }

    public static <T, V, R extends Collection<V>> R flatCollect(
            Iterable<T> iterable,
            Function<? super T, Collection<V>> function,
            R target,
            boolean allowReorderedResult)
    {
        return ParallelIterate.flatCollect(
                iterable,
                function,
                target,
                ParallelIterate.DEFAULT_MIN_FORK_SIZE,
                ParallelIterate.EXECUTOR_SERVICE,
                allowReorderedResult);
    }

    public static <T, V, R extends Collection<V>> R flatCollect(
            Iterable<T> iterable,
            Function<? super T, Collection<V>> function,
            R target,
            int batchSize,
            Executor executor,
            boolean allowReorderedResult)
    {
        int size = Iterate.sizeOf(iterable);
        int taskCount = ParallelIterate.calculateTaskCount(iterable, batchSize);
        int taskSize = size / taskCount;
        FlatCollectProcedureCombiner<T, V> combiner =
                new FlatCollectProcedureCombiner<T, V>(iterable, target, size, allowReorderedResult);
        FlatCollectProcedureFactory<T, V> procedureFactory = new FlatCollectProcedureFactory<T, V>(function, taskSize);
        ParallelIterate.forEach(
                iterable,
                procedureFactory,
                combiner,
                batchSize,
                taskCount,
                executor);
        return (R) combiner.getResult();
    }

    /**
     * Same effect as {@link Iterate#collectIf(Iterable, Predicate, Function)},
     * but executed in parallel batches.
     *
     * @return The collected elements. The Collection will be of the same type as the input (List or Set)
     * and will be in the same order as the input (if it is an ordered collection).
     * @see ParallelIterate#collectIf(Iterable, Predicate, Function, boolean)
     */
    public static <T, V> Collection<V> collectIf(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            Function<? super T, V> function)
    {
        return ParallelIterate.collectIf(iterable, predicate, function, false);
    }

    /**
     * Same effect as {@link Iterate#collectIf(Iterable, Predicate, Function)},
     * but executed in parallel batches, and with potentially reordered results.
     *
     * @param allowReorderedResult If the result can be in a different order.
     *                             Allowing reordering may yield faster execution.
     * @return The collected elements. The Collection will be of the same type
     * as the input (List or Set)
     */
    public static <T, V> Collection<V> collectIf(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            Function<? super T, V> function,
            boolean allowReorderedResult)
    {
        return ParallelIterate.collectIf(iterable, predicate, function, null, allowReorderedResult);
    }

    /**
     * Same effect as {@link Iterate#collectIf(Iterable, Predicate, Function)},
     * but executed in parallel batches, and writing output into the specified collection.
     *
     * @param target               Where to write the output.
     * @param allowReorderedResult If the result can be in a different order.
     *                             Allowing reordering may yield faster execution.
     * @return The 'target' collection, with the collected elements added.
     */
    public static <T, V, R extends Collection<V>> R collectIf(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            Function<? super T, V> function,
            R target,
            boolean allowReorderedResult)
    {
        return ParallelIterate.collectIf(
                iterable,
                predicate,
                function,
                target,
                ParallelIterate.DEFAULT_MIN_FORK_SIZE,
                ParallelIterate.EXECUTOR_SERVICE,
                allowReorderedResult);
    }

    public static <T, V, R extends Collection<V>> R collectIf(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            Function<? super T, V> function,
            R target,
            int batchSize,
            Executor executor,
            boolean allowReorderedResult)
    {
        FastListCollectIfProcedureCombiner<T, V> combiner = new FastListCollectIfProcedureCombiner<T, V>(iterable, target, 10, allowReorderedResult);
        FastListCollectIfProcedureFactory<T, V> procedureFactory = new FastListCollectIfProcedureFactory<T, V>(function, predicate, batchSize);
        ParallelIterate.forEach(
                iterable,
                procedureFactory,
                combiner,
                batchSize,
                ParallelIterate.calculateTaskCount(iterable, batchSize),
                executor);
        return (R) combiner.getResult();
    }

    /**
     * Same effect as {@link Iterate#groupBy(Iterable, Function)},
     * but executed in parallel batches, and writing output into a SynchronizedPutFastListMultimap.
     */
    public static <K, V> MutableMultimap<K, V> groupBy(
            Iterable<V> iterable,
            Function<? super V, ? extends K> function)
    {
        return ParallelIterate.groupBy(iterable, function, ParallelIterate.DEFAULT_MIN_FORK_SIZE, ParallelIterate.EXECUTOR_SERVICE);
    }

    public static <T, K, V> MutableMap<K, V> aggregateBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator)
    {
        return ParallelIterate.aggregateBy(
                iterable,
                groupBy,
                zeroValueFactory,
                nonMutatingAggregator,
                ParallelIterate.DEFAULT_MIN_FORK_SIZE);
    }

    public static <T, K, V, R extends MutableMap<K, V>> R aggregateBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator,
            R mutableMap)
    {
        return ParallelIterate.aggregateBy(
                iterable,
                groupBy,
                zeroValueFactory,
                nonMutatingAggregator,
                mutableMap,
                ParallelIterate.DEFAULT_MIN_FORK_SIZE);
    }

    public static <T, K, V> MutableMap<K, V> aggregateBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator,
            int batchSize)
    {
        return ParallelIterate.aggregateBy(
                iterable,
                groupBy,
                zeroValueFactory,
                nonMutatingAggregator,
                batchSize,
                ParallelIterate.EXECUTOR_SERVICE);
    }

    public static <T, K, V, R extends MutableMap<K, V>> R aggregateBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator,
            R mutableMap,
            int batchSize)
    {
        return ParallelIterate.aggregateBy(
                iterable,
                groupBy,
                zeroValueFactory,
                nonMutatingAggregator,
                mutableMap,
                batchSize,
                ParallelIterate.EXECUTOR_SERVICE);
    }

    public static <T, K, V> MutableMap<K, V> aggregateBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator,
            int batchSize,
            Executor executor)
    {
        return ParallelIterate.aggregateBy(
                iterable,
                groupBy,
                zeroValueFactory,
                nonMutatingAggregator,
                ConcurrentHashMap.<K, V>newMap(),
                batchSize,
                executor);
    }

    public static <T, K, V, R extends MutableMap<K, V>> R aggregateBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator,
            R mutableMap,
            int batchSize,
            Executor executor)
    {
        NonMutatingAggregationProcedure<T, K, V> nonMutatingAggregationProcedure =
                new NonMutatingAggregationProcedure<T, K, V>(mutableMap, groupBy, zeroValueFactory, nonMutatingAggregator);
        ParallelIterate.forEach(
                iterable,
                new PassThruProcedureFactory<Procedure<T>>(nonMutatingAggregationProcedure),
                Combiners.<Procedure<T>>passThru(),
                batchSize,
                executor);
        return mutableMap;
    }

    public static <T, K, V> MutableMap<K, V> aggregateInPlaceBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Procedure2<? super V, ? super T> mutatingAggregator)
    {
        return ParallelIterate.aggregateInPlaceBy(
                iterable,
                groupBy,
                zeroValueFactory,
                mutatingAggregator,
                ParallelIterate.DEFAULT_MIN_FORK_SIZE);
    }

    public static <T, K, V, R extends MutableMap<K, V>> R aggregateInPlaceBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Procedure2<? super V, ? super T> mutatingAggregator,
            R mutableMap)
    {
        return ParallelIterate.aggregateInPlaceBy(
                iterable,
                groupBy,
                zeroValueFactory,
                mutatingAggregator,
                mutableMap,
                ParallelIterate.DEFAULT_MIN_FORK_SIZE);
    }

    public static <T, K, V> MutableMap<K, V> aggregateInPlaceBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Procedure2<? super V, ? super T> mutatingAggregator,
            int batchSize)
    {
        return ParallelIterate.aggregateInPlaceBy(
                iterable,
                groupBy,
                zeroValueFactory,
                mutatingAggregator,
                batchSize,
                ParallelIterate.EXECUTOR_SERVICE);
    }

    public static <T, K, V, R extends MutableMap<K, V>> R aggregateInPlaceBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Procedure2<? super V, ? super T> mutatingAggregator,
            R mutableMap,
            int batchSize)
    {
        return ParallelIterate.aggregateInPlaceBy(
                iterable,
                groupBy,
                zeroValueFactory,
                mutatingAggregator,
                mutableMap,
                batchSize,
                ParallelIterate.EXECUTOR_SERVICE);
    }

    public static <T, K, V> MutableMap<K, V> aggregateInPlaceBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Procedure2<? super V, ? super T> mutatingAggregator,
            int batchSize,
            Executor executor)
    {
        MutableMap<K, V> map = ConcurrentHashMap.newMap();
        MutatingAggregationProcedure<T, K, V> mutatingAggregationProcedure =
                new MutatingAggregationProcedure<T, K, V>(map, groupBy, zeroValueFactory, mutatingAggregator);
        ParallelIterate.forEach(
                iterable,
                new PassThruProcedureFactory<Procedure<T>>(mutatingAggregationProcedure),
                Combiners.<Procedure<T>>passThru(),
                batchSize,
                executor);
        return map;
    }

    public static <T, K, V, R extends MutableMap<K, V>> R aggregateInPlaceBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Procedure2<? super V, ? super T> mutatingAggregator,
            R mutableMap,
            int batchSize,
            Executor executor)
    {
        MutatingAggregationProcedure<T, K, V> mutatingAggregationProcedure =
                new MutatingAggregationProcedure<T, K, V>(mutableMap, groupBy, zeroValueFactory, mutatingAggregator);
        ParallelIterate.forEach(
                iterable,
                new PassThruProcedureFactory<Procedure<T>>(mutatingAggregationProcedure),
                Combiners.<Procedure<T>>passThru(),
                batchSize,
                executor);
        return mutableMap;
    }

    /**
     * Same effect as {@link Iterate#groupBy(Iterable, Function)},
     * but executed in parallel batches, and writing output into a SynchronizedPutFastListMultimap.
     */
    public static <K, V, R extends MutableMultimap<K, V>> MutableMultimap<K, V> groupBy(
            Iterable<V> iterable,
            Function<? super V, ? extends K> function,
            R concurrentMultimap)
    {
        return ParallelIterate.groupBy(iterable, function, concurrentMultimap, ParallelIterate.DEFAULT_MIN_FORK_SIZE);
    }

    /**
     * Same effect as {@link Iterate#groupBy(Iterable, Function)},
     * but executed in parallel batches, and writing output into a SynchronizedPutFastListMultimap.
     */
    public static <K, V, R extends MutableMultimap<K, V>> MutableMultimap<K, V> groupBy(
            Iterable<V> iterable,
            Function<? super V, ? extends K> function,
            R concurrentMultimap,
            int batchSize)
    {
        return ParallelIterate.groupBy(iterable, function, concurrentMultimap, batchSize, ParallelIterate.EXECUTOR_SERVICE);
    }

    /**
     * Same effect as {@link Iterate#groupBy(Iterable, Function)},
     * but executed in parallel batches, and writing output into a SynchronizedPutFastListMultimap.
     */
    public static <K, V> MutableMultimap<K, V> groupBy(
            Iterable<V> iterable,
            Function<? super V, ? extends K> function,
            int batchSize)
    {
        return ParallelIterate.groupBy(iterable, function, batchSize, ParallelIterate.EXECUTOR_SERVICE);
    }

    /**
     * Same effect as {@link Iterate#groupBy(Iterable, Function)},
     * but executed in parallel batches, and writing output into a SynchronizedPutFastListMultimap.
     */
    public static <K, V> MutableMultimap<K, V> groupBy(
            Iterable<V> iterable,
            Function<? super V, ? extends K> function,
            int batchSize,
            Executor executor)
    {
        return ParallelIterate.groupBy(iterable, function, SynchronizedPutFastListMultimap.<K, V>newMultimap(), batchSize, executor);
    }

    /**
     * Same effect as {@link Iterate#groupBy(Iterable, Function)},
     * but executed in parallel batches, and writing output into a SynchronizedPutFastListMultimap.
     */
    public static <K, V, R extends MutableMultimap<K, V>> MutableMultimap<K, V> groupBy(
            Iterable<V> iterable,
            Function<? super V, ? extends K> function,
            R concurrentMultimap,
            int batchSize,
            Executor executor)
    {
        ParallelIterate.forEach(
                iterable,
                new PassThruProcedureFactory<Procedure<V>>(new MultimapPutProcedure<K, V>(concurrentMultimap, function)),
                Combiners.<Procedure<V>>passThru(),
                batchSize,
                executor);
        return concurrentMultimap;
    }

    public static <T, V> ObjectDoubleMap<V> sumByDouble(
            Iterable<T> iterable,
            Function<T, V> groupBy,
            DoubleFunction<? super T> function)
    {
        ObjectDoubleHashMap<V> result = ObjectDoubleHashMap.newMap();
        ParallelIterate.forEach(
                iterable,
                new SumByDoubleProcedure<T, V>(groupBy, function),
                new SumByDoubleCombiner<T, V>(result),
                ParallelIterate.DEFAULT_MIN_FORK_SIZE,
                ParallelIterate.EXECUTOR_SERVICE);
        return result;
    }

    public static <T, V> ObjectDoubleMap<V> sumByFloat(
            Iterable<T> iterable,
            Function<T, V> groupBy,
            FloatFunction<? super T> function)
    {
        ObjectDoubleHashMap<V> result = ObjectDoubleHashMap.newMap();
        ParallelIterate.forEach(
                iterable,
                new SumByFloatProcedure<T, V>(groupBy, function),
                new SumByFloatCombiner<T, V>(result),
                ParallelIterate.DEFAULT_MIN_FORK_SIZE,
                ParallelIterate.EXECUTOR_SERVICE);
        return result;
    }

    public static <T, V> ObjectLongMap<V> sumByLong(
            Iterable<T> iterable,
            Function<T, V> groupBy,
            LongFunction<? super T> function)
    {
        ObjectLongHashMap<V> result = ObjectLongHashMap.newMap();
        ParallelIterate.forEach(
                iterable,
                new SumByLongProcedure<T, V>(groupBy, function),
                new SumByLongCombiner<T, V>(result),
                ParallelIterate.DEFAULT_MIN_FORK_SIZE,
                ParallelIterate.EXECUTOR_SERVICE);
        return result;
    }

    public static <T, V> ObjectLongMap<V> sumByInt(
            Iterable<T> iterable,
            Function<T, V> groupBy,
            IntFunction<? super T> function)
    {
        ObjectLongHashMap<V> result = ObjectLongHashMap.newMap();
        ParallelIterate.forEach(
                iterable,
                new SumByIntProcedure<T, V>(groupBy, function),
                new SumByIntCombiner<T, V>(result),
                ParallelIterate.DEFAULT_MIN_FORK_SIZE,
                ParallelIterate.EXECUTOR_SERVICE);
        return result;
    }

    /**
     * @since 6.0
     */
    public static <V, T> MutableMap<V, BigDecimal> sumByBigDecimal(Iterable<T> iterable, Function<T, V> groupBy, Function<? super T, BigDecimal> function)
    {
        MutableMap<V, BigDecimal> result = UnifiedMap.newMap();
        ParallelIterate.forEach(
                iterable,
                new SumByBigDecimalProcedure<T, V>(groupBy, function),
                new SumByBigDecimalCombiner<T, V>(result),
                ParallelIterate.DEFAULT_MIN_FORK_SIZE,
                ParallelIterate.EXECUTOR_SERVICE);
        return result;
    }

    /**
     * @since 6.0
     */
    public static <V, T> MutableMap<V, BigInteger> sumByBigInteger(Iterable<T> iterable, Function<T, V> groupBy, Function<? super T, BigInteger> function)
    {
        MutableMap<V, BigInteger> result = UnifiedMap.newMap();
        ParallelIterate.forEach(
                iterable,
                new SumByBigIntegerProcedure<T, V>(groupBy, function),
                new SumByBigIntegerCombiner<T, V>(result),
                ParallelIterate.DEFAULT_MIN_FORK_SIZE,
                ParallelIterate.EXECUTOR_SERVICE);
        return result;
    }

    /**
     * Returns a brand new ExecutorService using the specified poolName with the specified maximum thread pool size. The
     * same poolName may be used more than once resulting in multiple pools with the same name.
     * <p>
     * The pool will be initialised with newPoolSize threads.  If that number of threads are in use and another thread
     * is requested, the pool will reject execution and the submitting thread will execute the task.
     */
    public static ExecutorService newPooledExecutor(int newPoolSize, String poolName, boolean useDaemonThreads)
    {
        return new ThreadPoolExecutor(
                newPoolSize,
                newPoolSize,
                0L,
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<Runnable>(),
                new CollectionsThreadFactory(poolName, useDaemonThreads),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    /**
     * Returns a brand new ExecutorService using the specified poolName and uses the optional property named
     * to set the maximum thread pool size.  The same poolName may be used more than
     * once resulting in multiple pools with the same name.
     */
    public static ExecutorService newPooledExecutor(String poolName, boolean useDaemonThreads)
    {
        return ParallelIterate.newPooledExecutor(ParallelIterate.getDefaultMaxThreadPoolSize(), poolName, useDaemonThreads);
    }

    public static int getDefaultTaskCount()
    {
        return ParallelIterate.getDefaultMaxThreadPoolSize() * ParallelIterate.getTaskRatio();
    }

    public static int getDefaultMaxThreadPoolSize()
    {
        return Math.min(AVAILABLE_PROCESSORS + 1, 100);
    }

    public static int getTaskRatio()
    {
        return TASK_RATIO;
    }

    private static final class SumByDoubleProcedure<T, V> implements Procedure<T>, ProcedureFactory<SumByDoubleProcedure<T, V>>
    {
        private final MutableMap<V, DoubleDoublePair> map = Maps.mutable.of();
        private final Function<T, V> groupBy;
        private final DoubleFunction<? super T> function;

        private SumByDoubleProcedure(Function<T, V> groupBy, DoubleFunction<? super T> function)
        {
            this.groupBy = groupBy;
            this.function = function;
        }

        public void value(T each)
        {
            V groupKey = this.groupBy.valueOf(each);
            DoubleDoublePair sumCompensation = this.map.getIfAbsentPut(groupKey, new Function0<DoubleDoublePair>()
            {
                public DoubleDoublePair value()
                {
                    return PrimitiveTuples.pair(0.0d, 0.0d);
                }
            });
            double sum = sumCompensation.getOne();
            double compensation = sumCompensation.getTwo();
            double adjustedValue = this.function.doubleValueOf(each) - compensation;
            double nextSum = sum + adjustedValue;
            compensation = nextSum - sum - adjustedValue;
            sum = nextSum;
            this.map.put(groupKey, PrimitiveTuples.pair(sum, compensation));
        }

        public MutableMap<V, DoubleDoublePair> getResult()
        {
            return this.map;
        }

        public SumByDoubleProcedure<T, V> create()
        {
            return new SumByDoubleProcedure<T, V>(this.groupBy, this.function);
        }
    }

    private static final class SumByDoubleCombiner<T, V> extends AbstractProcedureCombiner<SumByDoubleProcedure<T, V>>
    {
        private final ObjectDoubleHashMap<V> result;
        private final ObjectDoubleHashMap<V> compensation = ObjectDoubleHashMap.newMap();

        private SumByDoubleCombiner(ObjectDoubleHashMap<V> result)
        {
            super(true);
            this.result = result;
        }

        public void combineOne(SumByDoubleProcedure<T, V> thingToCombine)
        {
            if (this.result.isEmpty())
            {
                thingToCombine.getResult().forEachKeyValue(new Procedure2<V, DoubleDoublePair>()
                {
                    public void value(V each, DoubleDoublePair sumCompensation)
                    {
                        SumByDoubleCombiner.this.result.put(each, sumCompensation.getOne());
                        SumByDoubleCombiner.this.compensation.put(each, sumCompensation.getTwo());
                    }
                });
            }
            else
            {
                thingToCombine.getResult().forEachKeyValue(new Procedure2<V, DoubleDoublePair>()
                {
                    public void value(V each, DoubleDoublePair sumCompensation)
                    {
                        double sum = SumByDoubleCombiner.this.result.get(each);
                        double currentCompensation = SumByDoubleCombiner.this.compensation.getIfAbsentPut(each, new DoubleFunction0()
                        {
                            public double value()
                            {
                                return 0.0d;
                            }
                        }) + sumCompensation.getTwo();

                        double adjustedValue = sumCompensation.getOne() - currentCompensation;
                        double nextSum = sum + adjustedValue;
                        SumByDoubleCombiner.this.compensation.put(each, nextSum - sum - adjustedValue);
                        SumByDoubleCombiner.this.result.put(each, nextSum);
                    }
                });
            }
        }
    }

    private static final class SumByFloatProcedure<T, V> implements Procedure<T>, ProcedureFactory<SumByFloatProcedure<T, V>>
    {
        private final MutableMap<V, DoubleDoublePair> map = Maps.mutable.of();
        private final Function<T, V> groupBy;
        private final FloatFunction<? super T> function;

        private SumByFloatProcedure(Function<T, V> groupBy, FloatFunction<? super T> function)
        {
            this.groupBy = groupBy;
            this.function = function;
        }

        public void value(T each)
        {
            V groupKey = this.groupBy.valueOf(each);
            DoubleDoublePair sumCompensation = this.map.getIfAbsentPut(groupKey, new Function0<DoubleDoublePair>()
            {
                public DoubleDoublePair value()
                {
                    return PrimitiveTuples.pair(0.0d, 0.0d);
                }
            });
            double sum = sumCompensation.getOne();
            double compensation = sumCompensation.getTwo();
            double adjustedValue = this.function.floatValueOf(each) - compensation;
            double nextSum = sum + adjustedValue;
            compensation = nextSum - sum - adjustedValue;
            sum = nextSum;
            this.map.put(groupKey, PrimitiveTuples.pair(sum, compensation));
        }

        public MutableMap<V, DoubleDoublePair> getResult()
        {
            return this.map;
        }

        public SumByFloatProcedure<T, V> create()
        {
            return new SumByFloatProcedure<T, V>(this.groupBy, this.function);
        }
    }

    private static final class SumByFloatCombiner<T, V> extends AbstractProcedureCombiner<SumByFloatProcedure<T, V>>
    {
        private final ObjectDoubleHashMap<V> result;
        private final ObjectDoubleHashMap<V> compensation = ObjectDoubleHashMap.newMap();

        private SumByFloatCombiner(ObjectDoubleHashMap<V> result)
        {
            super(true);
            this.result = result;
        }

        public void combineOne(SumByFloatProcedure<T, V> thingToCombine)
        {
            if (this.result.isEmpty())
            {
                thingToCombine.getResult().forEachKeyValue(new Procedure2<V, DoubleDoublePair>()
                {
                    public void value(V each, DoubleDoublePair sumCompensation)
                    {
                        SumByFloatCombiner.this.result.put(each, sumCompensation.getOne());
                        SumByFloatCombiner.this.compensation.put(each, sumCompensation.getTwo());
                    }
                });
            }
            else
            {
                thingToCombine.getResult().forEachKeyValue(new Procedure2<V, DoubleDoublePair>()
                {
                    public void value(V each, DoubleDoublePair sumCompensation)
                    {
                        double sum = SumByFloatCombiner.this.result.get(each);
                        double currentCompensation = SumByFloatCombiner.this.compensation.getIfAbsentPut(each, new DoubleFunction0()
                        {
                            public double value()
                            {
                                return 0.0d;
                            }
                        }) + sumCompensation.getTwo();

                        double adjustedValue = sumCompensation.getOne() - currentCompensation;
                        double nextSum = sum + adjustedValue;
                        SumByFloatCombiner.this.compensation.put(each, nextSum - sum - adjustedValue);
                        SumByFloatCombiner.this.result.put(each, nextSum);
                    }
                });
            }
        }
    }

    private static final class SumByLongProcedure<T, V> implements Procedure<T>, ProcedureFactory<SumByLongProcedure<T, V>>
    {
        private final ObjectLongHashMap<V> map = ObjectLongHashMap.newMap();
        private final Function<T, V> groupBy;
        private final LongFunction<? super T> function;

        private SumByLongProcedure(Function<T, V> groupBy, LongFunction<? super T> function)
        {
            this.groupBy = groupBy;
            this.function = function;
        }

        public void value(T each)
        {
            this.map.addToValue(this.groupBy.valueOf(each), this.function.longValueOf(each));
        }

        public ObjectLongHashMap<V> getResult()
        {
            return this.map;
        }

        public SumByLongProcedure<T, V> create()
        {
            return new SumByLongProcedure<T, V>(this.groupBy, this.function);
        }
    }

    private static final class SumByLongCombiner<T, V> extends AbstractProcedureCombiner<SumByLongProcedure<T, V>>
    {
        private final ObjectLongHashMap<V> result;

        private SumByLongCombiner(ObjectLongHashMap<V> result)
        {
            super(true);
            this.result = result;
        }

        public void combineOne(SumByLongProcedure<T, V> thingToCombine)
        {
            if (this.result.isEmpty())
            {
                this.result.putAll(thingToCombine.getResult());
            }
            else
            {
                thingToCombine.getResult().forEachKeyValue(new ObjectLongProcedure<V>()
                {
                    public void value(V each, long value)
                    {
                        SumByLongCombiner.this.result.addToValue(each, value);
                    }
                });
            }
        }
    }

    private static final class SumByIntProcedure<T, V> implements Procedure<T>, ProcedureFactory<SumByIntProcedure<T, V>>
    {
        private final ObjectLongHashMap<V> map = ObjectLongHashMap.newMap();
        private final Function<T, V> groupBy;
        private final IntFunction<? super T> function;

        private SumByIntProcedure(Function<T, V> groupBy, IntFunction<? super T> function)
        {
            this.groupBy = groupBy;
            this.function = function;
        }

        public void value(T each)
        {
            this.map.addToValue(this.groupBy.valueOf(each), (long) this.function.intValueOf(each));
        }

        public ObjectLongHashMap<V> getResult()
        {
            return this.map;
        }

        public SumByIntProcedure<T, V> create()
        {
            return new SumByIntProcedure<T, V>(this.groupBy, this.function);
        }
    }

    private static final class SumByIntCombiner<T, V> extends AbstractProcedureCombiner<SumByIntProcedure<T, V>>
    {
        private final ObjectLongHashMap<V> result;

        private SumByIntCombiner(ObjectLongHashMap<V> result)
        {
            super(true);
            this.result = result;
        }

        public void combineOne(SumByIntProcedure<T, V> thingToCombine)
        {
            if (this.result.isEmpty())
            {
                this.result.putAll(thingToCombine.getResult());
            }
            else
            {
                thingToCombine.getResult().forEachKeyValue(new ObjectLongProcedure<V>()
                {
                    public void value(V each, long value)
                    {
                        SumByIntCombiner.this.result.addToValue(each, value);
                    }
                });
            }
        }
    }

    private static final class SumByBigDecimalProcedure<T, V> implements Procedure<T>, ProcedureFactory<SumByBigDecimalProcedure<T, V>>
    {
        private final MutableMap<V, BigDecimal> map = UnifiedMap.newMap();
        private final Function<T, V> groupBy;
        private final Function<? super T, BigDecimal> function;

        private SumByBigDecimalProcedure(Function<T, V> groupBy, Function<? super T, BigDecimal> function)
        {
            this.groupBy = groupBy;
            this.function = function;
        }

        public void value(final T each)
        {
            this.map.updateValue(this.groupBy.valueOf(each), Functions0.zeroBigDecimal(), new Function<BigDecimal, BigDecimal>()
            {
                public BigDecimal valueOf(BigDecimal original)
                {
                    return original.add(SumByBigDecimalProcedure.this.function.valueOf(each));
                }
            });
        }

        public MutableMap<V, BigDecimal> getResult()
        {
            return this.map;
        }

        public SumByBigDecimalProcedure<T, V> create()
        {
            return new SumByBigDecimalProcedure<T, V>(this.groupBy, this.function);
        }
    }

    private static final class SumByBigDecimalCombiner<T, V> extends AbstractProcedureCombiner<SumByBigDecimalProcedure<T, V>>
    {
        private final MutableMap<V, BigDecimal> result;

        private SumByBigDecimalCombiner(MutableMap<V, BigDecimal> result)
        {
            super(true);
            this.result = result;
        }

        public void combineOne(SumByBigDecimalProcedure<T, V> thingToCombine)
        {
            if (this.result.isEmpty())
            {
                this.result.putAll(thingToCombine.getResult());
            }
            else
            {
                thingToCombine.getResult().forEachKeyValue(new Procedure2<V, BigDecimal>()
                {
                    public void value(V key, final BigDecimal value)
                    {
                        SumByBigDecimalCombiner.this.result.updateValue(key, Functions0.zeroBigDecimal(), new Function<BigDecimal, BigDecimal>()
                        {
                            public BigDecimal valueOf(BigDecimal original)
                            {
                                return original.add(value);
                            }
                        });
                    }
                });
            }
        }
    }

    private static final class SumByBigIntegerProcedure<T, V> implements Procedure<T>, ProcedureFactory<SumByBigIntegerProcedure<T, V>>
    {
        private final MutableMap<V, BigInteger> map = UnifiedMap.newMap();
        private final Function<T, V> groupBy;
        private final Function<? super T, BigInteger> function;

        private SumByBigIntegerProcedure(Function<T, V> groupBy, Function<? super T, BigInteger> function)
        {
            this.groupBy = groupBy;
            this.function = function;
        }

        public void value(final T each)
        {
            this.map.updateValue(this.groupBy.valueOf(each), Functions0.zeroBigInteger(), new Function<BigInteger, BigInteger>()
            {
                public BigInteger valueOf(BigInteger original)
                {
                    return original.add(SumByBigIntegerProcedure.this.function.valueOf(each));
                }
            });
        }

        public MutableMap<V, BigInteger> getResult()
        {
            return this.map;
        }

        public SumByBigIntegerProcedure<T, V> create()
        {
            return new SumByBigIntegerProcedure<T, V>(this.groupBy, this.function);
        }
    }

    private static final class SumByBigIntegerCombiner<T, V> extends AbstractProcedureCombiner<SumByBigIntegerProcedure<T, V>>
    {
        private final MutableMap<V, BigInteger> result;

        private SumByBigIntegerCombiner(MutableMap<V, BigInteger> result)
        {
            super(true);
            this.result = result;
        }

        public void combineOne(SumByBigIntegerProcedure<T, V> thingToCombine)
        {
            if (this.result.isEmpty())
            {
                this.result.putAll(thingToCombine.getResult());
            }
            else
            {
                thingToCombine.getResult().forEachKeyValue(new Procedure2<V, BigInteger>()
                {
                    public void value(V key, final BigInteger value)
                    {
                        SumByBigIntegerCombiner.this.result.updateValue(key, Functions0.zeroBigInteger(), new Function<BigInteger, BigInteger>()
                        {
                            public BigInteger valueOf(BigInteger original)
                            {
                                return original.add(value);
                            }
                        });
                    }
                });
            }
        }
    }
}
