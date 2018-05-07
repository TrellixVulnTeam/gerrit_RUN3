begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Licensed under the Apache License, Version 2.0 (the "License");
end_comment

begin_comment
comment|// you may not use this file except in compliance with the License.
end_comment

begin_comment
comment|// You may obtain a copy of the License at
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// http://www.apache.org/licenses/LICENSE-2.0
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Unless required by applicable law or agreed to in writing, software
end_comment

begin_comment
comment|// distributed under the License is distributed on an "AS IS" BASIS,
end_comment

begin_comment
comment|// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
end_comment

begin_comment
comment|// See the License for the specific language governing permissions and
end_comment

begin_comment
comment|// limitations under the License.
end_comment

begin_package
DECL|package|com.google.gerrit.server.git
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|git
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|events
operator|.
name|LifecycleListener
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|lifecycle
operator|.
name|LifecycleModule
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|reviewdb
operator|.
name|client
operator|.
name|Project
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|config
operator|.
name|GerritServerConfig
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|util
operator|.
name|IdGenerator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Singleton
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|Thread
operator|.
name|UncaughtExceptionHandler
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Delayed
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|RunnableScheduledFuture
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ScheduledThreadPoolExecutor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeoutException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Config
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/** Delayed execution of tasks using a background thread pool. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|WorkQueue
specifier|public
class|class
name|WorkQueue
block|{
DECL|class|Lifecycle
specifier|public
specifier|static
class|class
name|Lifecycle
implements|implements
name|LifecycleListener
block|{
DECL|field|workQueue
specifier|private
specifier|final
name|WorkQueue
name|workQueue
decl_stmt|;
annotation|@
name|Inject
DECL|method|Lifecycle (final WorkQueue workQeueue)
name|Lifecycle
parameter_list|(
specifier|final
name|WorkQueue
name|workQeueue
parameter_list|)
block|{
name|this
operator|.
name|workQueue
operator|=
name|workQeueue
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|workQueue
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|Module
specifier|public
specifier|static
class|class
name|Module
extends|extends
name|LifecycleModule
block|{
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|WorkQueue
operator|.
name|class
argument_list|)
expr_stmt|;
name|listener
argument_list|()
operator|.
name|to
argument_list|(
name|Lifecycle
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|WorkQueue
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|LOG_UNCAUGHT_EXCEPTION
specifier|private
specifier|static
specifier|final
name|UncaughtExceptionHandler
name|LOG_UNCAUGHT_EXCEPTION
init|=
operator|new
name|UncaughtExceptionHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|uncaughtException
parameter_list|(
name|Thread
name|t
parameter_list|,
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"WorkQueue thread "
operator|+
name|t
operator|.
name|getName
argument_list|()
operator|+
literal|" threw exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
DECL|field|defaultQueue
specifier|private
name|Executor
name|defaultQueue
decl_stmt|;
DECL|field|defaultQueueSize
specifier|private
specifier|final
name|int
name|defaultQueueSize
decl_stmt|;
DECL|field|idGenerator
specifier|private
specifier|final
name|IdGenerator
name|idGenerator
decl_stmt|;
DECL|field|queues
specifier|private
specifier|final
name|CopyOnWriteArrayList
argument_list|<
name|Executor
argument_list|>
name|queues
decl_stmt|;
annotation|@
name|Inject
DECL|method|WorkQueue (IdGenerator idGenerator, @GerritServerConfig Config cfg)
name|WorkQueue
parameter_list|(
name|IdGenerator
name|idGenerator
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|)
block|{
name|this
argument_list|(
name|idGenerator
argument_list|,
name|cfg
operator|.
name|getInt
argument_list|(
literal|"execution"
argument_list|,
literal|"defaultThreadPoolSize"
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Constructor to allow binding the WorkQueue more explicitly in a vhost setup. */
DECL|method|WorkQueue (IdGenerator idGenerator, int defaultThreadPoolSize)
specifier|public
name|WorkQueue
parameter_list|(
name|IdGenerator
name|idGenerator
parameter_list|,
name|int
name|defaultThreadPoolSize
parameter_list|)
block|{
name|this
operator|.
name|idGenerator
operator|=
name|idGenerator
expr_stmt|;
name|this
operator|.
name|queues
operator|=
operator|new
name|CopyOnWriteArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|defaultQueueSize
operator|=
name|defaultThreadPoolSize
expr_stmt|;
block|}
comment|/** Get the default work queue, for miscellaneous tasks. */
DECL|method|getDefaultQueue ()
specifier|public
specifier|synchronized
name|Executor
name|getDefaultQueue
parameter_list|()
block|{
if|if
condition|(
name|defaultQueue
operator|==
literal|null
condition|)
block|{
name|defaultQueue
operator|=
name|createQueue
argument_list|(
name|defaultQueueSize
argument_list|,
literal|"WorkQueue"
argument_list|)
expr_stmt|;
block|}
return|return
name|defaultQueue
return|;
block|}
comment|/** Create a new executor queue. */
DECL|method|createQueue (int poolsize, String prefix)
specifier|public
name|Executor
name|createQueue
parameter_list|(
name|int
name|poolsize
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
specifier|final
name|Executor
name|r
init|=
operator|new
name|Executor
argument_list|(
name|poolsize
argument_list|,
name|prefix
argument_list|)
decl_stmt|;
name|r
operator|.
name|setContinueExistingPeriodicTasksAfterShutdownPolicy
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|r
operator|.
name|setExecuteExistingDelayedTasksAfterShutdownPolicy
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|queues
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
comment|/** Get all of the tasks currently scheduled in any work queue. */
DECL|method|getTasks ()
specifier|public
name|List
argument_list|<
name|Task
argument_list|<
name|?
argument_list|>
argument_list|>
name|getTasks
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|Task
argument_list|<
name|?
argument_list|>
argument_list|>
name|r
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Executor
name|e
range|:
name|queues
control|)
block|{
name|e
operator|.
name|addAllTo
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
DECL|method|getTaskInfos (TaskInfoFactory<T> factory)
specifier|public
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|getTaskInfos
parameter_list|(
name|TaskInfoFactory
argument_list|<
name|T
argument_list|>
name|factory
parameter_list|)
block|{
name|List
argument_list|<
name|T
argument_list|>
name|taskInfos
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Executor
name|exe
range|:
name|queues
control|)
block|{
for|for
control|(
name|Task
argument_list|<
name|?
argument_list|>
name|task
range|:
name|exe
operator|.
name|getTasks
argument_list|()
control|)
block|{
name|taskInfos
operator|.
name|add
argument_list|(
name|factory
operator|.
name|getTaskInfo
argument_list|(
name|task
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|taskInfos
return|;
block|}
comment|/** Locate a task by its unique id, null if no task matches. */
DECL|method|getTask (final int id)
specifier|public
name|Task
argument_list|<
name|?
argument_list|>
name|getTask
parameter_list|(
specifier|final
name|int
name|id
parameter_list|)
block|{
name|Task
argument_list|<
name|?
argument_list|>
name|result
init|=
literal|null
decl_stmt|;
for|for
control|(
specifier|final
name|Executor
name|e
range|:
name|queues
control|)
block|{
specifier|final
name|Task
argument_list|<
name|?
argument_list|>
name|t
init|=
name|e
operator|.
name|getTask
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
comment|// Don't return the task if we have a duplicate. Lie instead.
return|return
literal|null
return|;
block|}
name|result
operator|=
name|t
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|getExecutor (String queueName)
specifier|public
name|Executor
name|getExecutor
parameter_list|(
name|String
name|queueName
parameter_list|)
block|{
for|for
control|(
name|Executor
name|e
range|:
name|queues
control|)
block|{
if|if
condition|(
name|e
operator|.
name|queueName
operator|.
name|equals
argument_list|(
name|queueName
argument_list|)
condition|)
block|{
return|return
name|e
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|stop ()
specifier|private
name|void
name|stop
parameter_list|()
block|{
for|for
control|(
specifier|final
name|Executor
name|p
range|:
name|queues
control|)
block|{
name|p
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|boolean
name|isTerminated
decl_stmt|;
do|do
block|{
try|try
block|{
name|isTerminated
operator|=
name|p
operator|.
name|awaitTermination
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|isTerminated
operator|=
literal|false
expr_stmt|;
block|}
block|}
do|while
condition|(
operator|!
name|isTerminated
condition|)
do|;
block|}
name|queues
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/** An isolated queue. */
DECL|class|Executor
specifier|public
class|class
name|Executor
extends|extends
name|ScheduledThreadPoolExecutor
block|{
DECL|field|all
specifier|private
specifier|final
name|ConcurrentHashMap
argument_list|<
name|Integer
argument_list|,
name|Task
argument_list|<
name|?
argument_list|>
argument_list|>
name|all
decl_stmt|;
DECL|field|queueName
specifier|private
specifier|final
name|String
name|queueName
decl_stmt|;
DECL|method|Executor (int corePoolSize, final String prefix)
name|Executor
parameter_list|(
name|int
name|corePoolSize
parameter_list|,
specifier|final
name|String
name|prefix
parameter_list|)
block|{
name|super
argument_list|(
name|corePoolSize
argument_list|,
operator|new
name|ThreadFactory
argument_list|()
block|{
specifier|private
specifier|final
name|ThreadFactory
name|parent
init|=
name|Executors
operator|.
name|defaultThreadFactory
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|tid
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Thread
name|newThread
parameter_list|(
specifier|final
name|Runnable
name|task
parameter_list|)
block|{
specifier|final
name|Thread
name|t
init|=
name|parent
operator|.
name|newThread
argument_list|(
name|task
argument_list|)
decl_stmt|;
name|t
operator|.
name|setName
argument_list|(
name|prefix
operator|+
literal|"-"
operator|+
name|tid
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|setUncaughtExceptionHandler
argument_list|(
name|LOG_UNCAUGHT_EXCEPTION
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|all
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|(
comment|//
name|corePoolSize
operator|<<
literal|1
argument_list|,
comment|// table size
literal|0.75f
argument_list|,
comment|// load factor
name|corePoolSize
operator|+
literal|4
comment|// concurrency level
argument_list|)
expr_stmt|;
name|queueName
operator|=
name|prefix
expr_stmt|;
block|}
DECL|method|unregisterWorkQueue ()
specifier|public
name|void
name|unregisterWorkQueue
parameter_list|()
block|{
name|queues
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|decorateTask ( final Runnable runnable, RunnableScheduledFuture<V> r)
specifier|protected
parameter_list|<
name|V
parameter_list|>
name|RunnableScheduledFuture
argument_list|<
name|V
argument_list|>
name|decorateTask
parameter_list|(
specifier|final
name|Runnable
name|runnable
parameter_list|,
name|RunnableScheduledFuture
argument_list|<
name|V
argument_list|>
name|r
parameter_list|)
block|{
name|r
operator|=
name|super
operator|.
name|decorateTask
argument_list|(
name|runnable
argument_list|,
name|r
argument_list|)
expr_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
specifier|final
name|int
name|id
init|=
name|idGenerator
operator|.
name|next
argument_list|()
decl_stmt|;
name|Task
argument_list|<
name|V
argument_list|>
name|task
decl_stmt|;
if|if
condition|(
name|runnable
operator|instanceof
name|ProjectRunnable
condition|)
block|{
name|task
operator|=
operator|new
name|ProjectTask
argument_list|<>
argument_list|(
operator|(
name|ProjectRunnable
operator|)
name|runnable
argument_list|,
name|r
argument_list|,
name|this
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|task
operator|=
operator|new
name|Task
argument_list|<>
argument_list|(
name|runnable
argument_list|,
name|r
argument_list|,
name|this
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|all
operator|.
name|putIfAbsent
argument_list|(
name|task
operator|.
name|getTaskId
argument_list|()
argument_list|,
name|task
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return
name|task
return|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|decorateTask ( final Callable<V> callable, final RunnableScheduledFuture<V> task)
specifier|protected
parameter_list|<
name|V
parameter_list|>
name|RunnableScheduledFuture
argument_list|<
name|V
argument_list|>
name|decorateTask
parameter_list|(
specifier|final
name|Callable
argument_list|<
name|V
argument_list|>
name|callable
parameter_list|,
specifier|final
name|RunnableScheduledFuture
argument_list|<
name|V
argument_list|>
name|task
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Callable not implemented"
argument_list|)
throw|;
block|}
DECL|method|remove (final Task<?> task)
name|void
name|remove
parameter_list|(
specifier|final
name|Task
argument_list|<
name|?
argument_list|>
name|task
parameter_list|)
block|{
name|all
operator|.
name|remove
argument_list|(
name|task
operator|.
name|getTaskId
argument_list|()
argument_list|,
name|task
argument_list|)
expr_stmt|;
block|}
DECL|method|getTask (final int id)
name|Task
argument_list|<
name|?
argument_list|>
name|getTask
parameter_list|(
specifier|final
name|int
name|id
parameter_list|)
block|{
return|return
name|all
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
DECL|method|addAllTo (final List<Task<?>> list)
name|void
name|addAllTo
parameter_list|(
specifier|final
name|List
argument_list|<
name|Task
argument_list|<
name|?
argument_list|>
argument_list|>
name|list
parameter_list|)
block|{
name|list
operator|.
name|addAll
argument_list|(
name|all
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
comment|// iterator is thread safe
block|}
DECL|method|getTasks ()
name|Collection
argument_list|<
name|Task
argument_list|<
name|?
argument_list|>
argument_list|>
name|getTasks
parameter_list|()
block|{
return|return
name|all
operator|.
name|values
argument_list|()
return|;
block|}
block|}
comment|/**    * Runnable needing to know it was canceled. Note that cancel is called only in case the task is    * not in progress already.    */
DECL|interface|CancelableRunnable
specifier|public
interface|interface
name|CancelableRunnable
extends|extends
name|Runnable
block|{
comment|/** Notifies the runnable it was canceled. */
DECL|method|cancel ()
name|void
name|cancel
parameter_list|()
function_decl|;
block|}
comment|/**    * Base interface handles the case when task was canceled before actual execution and in case it    * was started cancel method is not called yet the task itself will be destroyed anyway (it will    * result in resource opening errors). This interface gives a chance to implementing classes for    * handling such scenario and act accordingly.    */
DECL|interface|CanceledWhileRunning
specifier|public
interface|interface
name|CanceledWhileRunning
extends|extends
name|CancelableRunnable
block|{
comment|/** Notifies the runnable it was canceled during execution. * */
DECL|method|setCanceledWhileRunning ()
name|void
name|setCanceledWhileRunning
parameter_list|()
function_decl|;
block|}
comment|/** A wrapper around a scheduled Runnable, as maintained in the queue. */
DECL|class|Task
specifier|public
specifier|static
class|class
name|Task
parameter_list|<
name|V
parameter_list|>
implements|implements
name|RunnableScheduledFuture
argument_list|<
name|V
argument_list|>
block|{
comment|/**      * Summarized status of a single task.      *      *<p>Tasks have the following state flow:      *      *<ol>      *<li>{@link #SLEEPING}: if scheduled with a non-zero delay.      *<li>{@link #READY}: waiting for an available worker thread.      *<li>{@link #RUNNING}: actively executing on a worker thread.      *<li>{@link #DONE}: finished executing, if not periodic.      *</ol>      */
DECL|enum|State
specifier|public
enum|enum
name|State
block|{
comment|// Ordered like this so ordinal matches the order we would
comment|// prefer to see tasks sorted in: done before running,
comment|// running before ready, ready before sleeping.
comment|//
DECL|enumConstant|DONE
name|DONE
block|,
DECL|enumConstant|CANCELLED
name|CANCELLED
block|,
DECL|enumConstant|RUNNING
name|RUNNING
block|,
DECL|enumConstant|READY
name|READY
block|,
DECL|enumConstant|SLEEPING
name|SLEEPING
block|,
DECL|enumConstant|OTHER
name|OTHER
block|}
DECL|field|runnable
specifier|private
specifier|final
name|Runnable
name|runnable
decl_stmt|;
DECL|field|task
specifier|private
specifier|final
name|RunnableScheduledFuture
argument_list|<
name|V
argument_list|>
name|task
decl_stmt|;
DECL|field|executor
specifier|private
specifier|final
name|Executor
name|executor
decl_stmt|;
DECL|field|taskId
specifier|private
specifier|final
name|int
name|taskId
decl_stmt|;
DECL|field|running
specifier|private
specifier|final
name|AtomicBoolean
name|running
decl_stmt|;
DECL|field|startTime
specifier|private
specifier|final
name|Date
name|startTime
decl_stmt|;
DECL|method|Task (Runnable runnable, RunnableScheduledFuture<V> task, Executor executor, int taskId)
name|Task
parameter_list|(
name|Runnable
name|runnable
parameter_list|,
name|RunnableScheduledFuture
argument_list|<
name|V
argument_list|>
name|task
parameter_list|,
name|Executor
name|executor
parameter_list|,
name|int
name|taskId
parameter_list|)
block|{
name|this
operator|.
name|runnable
operator|=
name|runnable
expr_stmt|;
name|this
operator|.
name|task
operator|=
name|task
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
name|this
operator|.
name|taskId
operator|=
name|taskId
expr_stmt|;
name|this
operator|.
name|running
operator|=
operator|new
name|AtomicBoolean
argument_list|()
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
block|}
DECL|method|getTaskId ()
specifier|public
name|int
name|getTaskId
parameter_list|()
block|{
return|return
name|taskId
return|;
block|}
DECL|method|getState ()
specifier|public
name|State
name|getState
parameter_list|()
block|{
if|if
condition|(
name|isCancelled
argument_list|()
condition|)
block|{
return|return
name|State
operator|.
name|CANCELLED
return|;
block|}
elseif|else
if|if
condition|(
name|isDone
argument_list|()
operator|&&
operator|!
name|isPeriodic
argument_list|()
condition|)
block|{
return|return
name|State
operator|.
name|DONE
return|;
block|}
elseif|else
if|if
condition|(
name|running
operator|.
name|get
argument_list|()
condition|)
block|{
return|return
name|State
operator|.
name|RUNNING
return|;
block|}
specifier|final
name|long
name|delay
init|=
name|getDelay
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|delay
operator|<=
literal|0
condition|)
block|{
return|return
name|State
operator|.
name|READY
return|;
block|}
return|return
name|State
operator|.
name|SLEEPING
return|;
block|}
DECL|method|getStartTime ()
specifier|public
name|Date
name|getStartTime
parameter_list|()
block|{
return|return
name|startTime
return|;
block|}
DECL|method|getQueueName ()
specifier|public
name|String
name|getQueueName
parameter_list|()
block|{
return|return
name|executor
operator|.
name|queueName
return|;
block|}
annotation|@
name|Override
DECL|method|cancel (boolean mayInterruptIfRunning)
specifier|public
name|boolean
name|cancel
parameter_list|(
name|boolean
name|mayInterruptIfRunning
parameter_list|)
block|{
if|if
condition|(
name|task
operator|.
name|cancel
argument_list|(
name|mayInterruptIfRunning
argument_list|)
condition|)
block|{
comment|// Tiny abuse of running: if the task needs to know it was
comment|// canceled (to clean up resources) and it hasn't started
comment|// yet the task's run method won't execute. So we tag it
comment|// as running and allow it to clean up. This ensures we do
comment|// not invoke cancel twice.
comment|//
if|if
condition|(
name|runnable
operator|instanceof
name|CancelableRunnable
condition|)
block|{
if|if
condition|(
name|running
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
operator|(
operator|(
name|CancelableRunnable
operator|)
name|runnable
operator|)
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|runnable
operator|instanceof
name|CanceledWhileRunning
condition|)
block|{
operator|(
operator|(
name|CanceledWhileRunning
operator|)
name|runnable
operator|)
operator|.
name|setCanceledWhileRunning
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|runnable
operator|instanceof
name|Future
argument_list|<
name|?
argument_list|>
condition|)
block|{
comment|// Creating new futures eventually passes through
comment|// AbstractExecutorService#schedule, which will convert the Guava
comment|// Future to a Runnable, thereby making it impossible for the
comment|// cancellation to propagate from ScheduledThreadPool's task back to
comment|// the Guava future, so kludge it here.
operator|(
operator|(
name|Future
argument_list|<
name|?
argument_list|>
operator|)
name|runnable
operator|)
operator|.
name|cancel
argument_list|(
name|mayInterruptIfRunning
argument_list|)
expr_stmt|;
block|}
name|executor
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|executor
operator|.
name|purge
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (Delayed o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Delayed
name|o
parameter_list|)
block|{
return|return
name|task
operator|.
name|compareTo
argument_list|(
name|o
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|V
name|get
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
block|{
return|return
name|task
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|get (long timeout, TimeUnit unit)
specifier|public
name|V
name|get
parameter_list|(
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
throws|,
name|TimeoutException
block|{
return|return
name|task
operator|.
name|get
argument_list|(
name|timeout
argument_list|,
name|unit
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getDelay (TimeUnit unit)
specifier|public
name|long
name|getDelay
parameter_list|(
name|TimeUnit
name|unit
parameter_list|)
block|{
return|return
name|task
operator|.
name|getDelay
argument_list|(
name|unit
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|isCancelled ()
specifier|public
name|boolean
name|isCancelled
parameter_list|()
block|{
return|return
name|task
operator|.
name|isCancelled
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isDone ()
specifier|public
name|boolean
name|isDone
parameter_list|()
block|{
return|return
name|task
operator|.
name|isDone
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isPeriodic ()
specifier|public
name|boolean
name|isPeriodic
parameter_list|()
block|{
return|return
name|task
operator|.
name|isPeriodic
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|running
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
try|try
block|{
name|task
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|isPeriodic
argument_list|()
condition|)
block|{
name|running
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|executor
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
comment|// This is a workaround to be able to print a proper name when the task
comment|// is wrapped into a TrustedListenableFutureTask.
try|try
block|{
if|if
condition|(
name|runnable
operator|.
name|getClass
argument_list|()
operator|.
name|isAssignableFrom
argument_list|(
name|Class
operator|.
name|forName
argument_list|(
literal|"com.google.common.util.concurrent.TrustedListenableFutureTask"
argument_list|)
argument_list|)
condition|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|trustedFutureInterruptibleTask
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"com.google.common.util.concurrent.TrustedListenableFutureTask$TrustedFutureInterruptibleTask"
argument_list|)
decl_stmt|;
for|for
control|(
name|Field
name|field
range|:
name|runnable
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredFields
argument_list|()
control|)
block|{
if|if
condition|(
name|field
operator|.
name|getType
argument_list|()
operator|.
name|isAssignableFrom
argument_list|(
name|trustedFutureInterruptibleTask
argument_list|)
condition|)
block|{
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Object
name|innerObj
init|=
name|field
operator|.
name|get
argument_list|(
name|runnable
argument_list|)
decl_stmt|;
if|if
condition|(
name|innerObj
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Field
name|innerField
range|:
name|innerObj
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredFields
argument_list|()
control|)
block|{
if|if
condition|(
name|innerField
operator|.
name|getType
argument_list|()
operator|.
name|isAssignableFrom
argument_list|(
name|Callable
operator|.
name|class
argument_list|)
condition|)
block|{
name|innerField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
operator|(
operator|(
name|Callable
argument_list|<
name|?
argument_list|>
operator|)
name|innerField
operator|.
name|get
argument_list|(
name|innerObj
argument_list|)
operator|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
decl||
name|IllegalArgumentException
decl||
name|IllegalAccessException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Cannot get a proper name for TrustedListenableFutureTask: {}"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|runnable
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**    * Same as Task class, but with a reference to ProjectRunnable, used to retrieve the project name    * from the operation queued    */
DECL|class|ProjectTask
specifier|public
specifier|static
class|class
name|ProjectTask
parameter_list|<
name|V
parameter_list|>
extends|extends
name|Task
argument_list|<
name|V
argument_list|>
implements|implements
name|ProjectRunnable
block|{
DECL|field|runnable
specifier|private
specifier|final
name|ProjectRunnable
name|runnable
decl_stmt|;
DECL|method|ProjectTask ( ProjectRunnable runnable, RunnableScheduledFuture<V> task, Executor executor, int taskId)
name|ProjectTask
parameter_list|(
name|ProjectRunnable
name|runnable
parameter_list|,
name|RunnableScheduledFuture
argument_list|<
name|V
argument_list|>
name|task
parameter_list|,
name|Executor
name|executor
parameter_list|,
name|int
name|taskId
parameter_list|)
block|{
name|super
argument_list|(
name|runnable
argument_list|,
name|task
argument_list|,
name|executor
argument_list|,
name|taskId
argument_list|)
expr_stmt|;
name|this
operator|.
name|runnable
operator|=
name|runnable
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getProjectNameKey ()
specifier|public
name|Project
operator|.
name|NameKey
name|getProjectNameKey
parameter_list|()
block|{
return|return
name|runnable
operator|.
name|getProjectNameKey
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getRemoteName ()
specifier|public
name|String
name|getRemoteName
parameter_list|()
block|{
return|return
name|runnable
operator|.
name|getRemoteName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hasCustomizedPrint ()
specifier|public
name|boolean
name|hasCustomizedPrint
parameter_list|()
block|{
return|return
name|runnable
operator|.
name|hasCustomizedPrint
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

