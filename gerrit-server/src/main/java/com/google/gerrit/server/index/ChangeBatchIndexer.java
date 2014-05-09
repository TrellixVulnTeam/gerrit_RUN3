begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.index
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|index
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|RefDatabase
operator|.
name|ALL
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Stopwatch
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ArrayListMultimap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Multimap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|AsyncFunction
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Futures
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ListenableFuture
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ListeningExecutorService
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|MoreExecutors
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
name|common
operator|.
name|Nullable
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
name|Change
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
name|reviewdb
operator|.
name|server
operator|.
name|ReviewDb
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
name|change
operator|.
name|MergeabilityChecker
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
name|git
operator|.
name|GitRepositoryManager
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
name|git
operator|.
name|MultiProgressMonitor
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
name|git
operator|.
name|MultiProgressMonitor
operator|.
name|Task
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
name|patch
operator|.
name|PatchListLoader
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
name|query
operator|.
name|change
operator|.
name|ChangeData
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|server
operator|.
name|SchemaFactory
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|diff
operator|.
name|DiffEntry
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
name|diff
operator|.
name|DiffFormatter
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
name|errors
operator|.
name|RepositoryNotFoundException
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
name|Constants
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
name|ObjectId
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
name|ObjectInserter
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
name|ProgressMonitor
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
name|Ref
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
name|Repository
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
name|revwalk
operator|.
name|RevCommit
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
name|revwalk
operator|.
name|RevObject
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
name|revwalk
operator|.
name|RevTree
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
name|revwalk
operator|.
name|RevWalk
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
name|util
operator|.
name|io
operator|.
name|DisabledOutputStream
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
name|util
operator|.
name|io
operator|.
name|NullOutputStream
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_class
DECL|class|ChangeBatchIndexer
specifier|public
class|class
name|ChangeBatchIndexer
block|{
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
name|ChangeBatchIndexer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|Result
specifier|public
specifier|static
class|class
name|Result
block|{
DECL|field|elapsedNanos
specifier|private
specifier|final
name|long
name|elapsedNanos
decl_stmt|;
DECL|field|success
specifier|private
specifier|final
name|boolean
name|success
decl_stmt|;
DECL|field|done
specifier|private
specifier|final
name|int
name|done
decl_stmt|;
DECL|field|failed
specifier|private
specifier|final
name|int
name|failed
decl_stmt|;
DECL|method|Result (Stopwatch sw, boolean success, int done, int failed)
specifier|private
name|Result
parameter_list|(
name|Stopwatch
name|sw
parameter_list|,
name|boolean
name|success
parameter_list|,
name|int
name|done
parameter_list|,
name|int
name|failed
parameter_list|)
block|{
name|this
operator|.
name|elapsedNanos
operator|=
name|sw
operator|.
name|elapsed
argument_list|(
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
expr_stmt|;
name|this
operator|.
name|success
operator|=
name|success
expr_stmt|;
name|this
operator|.
name|done
operator|=
name|done
expr_stmt|;
name|this
operator|.
name|failed
operator|=
name|failed
expr_stmt|;
block|}
DECL|method|success ()
specifier|public
name|boolean
name|success
parameter_list|()
block|{
return|return
name|success
return|;
block|}
DECL|method|doneCount ()
specifier|public
name|int
name|doneCount
parameter_list|()
block|{
return|return
name|done
return|;
block|}
DECL|method|failedCount ()
specifier|public
name|int
name|failedCount
parameter_list|()
block|{
return|return
name|failed
return|;
block|}
DECL|method|elapsed (TimeUnit timeUnit)
specifier|public
name|long
name|elapsed
parameter_list|(
name|TimeUnit
name|timeUnit
parameter_list|)
block|{
return|return
name|timeUnit
operator|.
name|convert
argument_list|(
name|elapsedNanos
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
return|;
block|}
block|}
DECL|field|schemaFactory
specifier|private
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schemaFactory
decl_stmt|;
DECL|field|changeDataFactory
specifier|private
specifier|final
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|executor
specifier|private
specifier|final
name|ListeningExecutorService
name|executor
decl_stmt|;
DECL|field|indexerFactory
specifier|private
specifier|final
name|ChangeIndexer
operator|.
name|Factory
name|indexerFactory
decl_stmt|;
DECL|field|mergeabilityChecker
specifier|private
specifier|final
name|MergeabilityChecker
name|mergeabilityChecker
decl_stmt|;
annotation|@
name|Inject
DECL|method|ChangeBatchIndexer (SchemaFactory<ReviewDb> schemaFactory, ChangeData.Factory changeDataFactory, GitRepositoryManager repoManager, @IndexExecutor ListeningExecutorService executor, ChangeIndexer.Factory indexerFactory, @Nullable MergeabilityChecker mergeabilityChecker)
name|ChangeBatchIndexer
parameter_list|(
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schemaFactory
parameter_list|,
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
parameter_list|,
name|GitRepositoryManager
name|repoManager
parameter_list|,
annotation|@
name|IndexExecutor
name|ListeningExecutorService
name|executor
parameter_list|,
name|ChangeIndexer
operator|.
name|Factory
name|indexerFactory
parameter_list|,
annotation|@
name|Nullable
name|MergeabilityChecker
name|mergeabilityChecker
parameter_list|)
block|{
name|this
operator|.
name|schemaFactory
operator|=
name|schemaFactory
expr_stmt|;
name|this
operator|.
name|changeDataFactory
operator|=
name|changeDataFactory
expr_stmt|;
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
name|this
operator|.
name|indexerFactory
operator|=
name|indexerFactory
expr_stmt|;
name|this
operator|.
name|mergeabilityChecker
operator|=
name|mergeabilityChecker
expr_stmt|;
block|}
DECL|method|indexAll (ChangeIndex index, Iterable<Project.NameKey> projects, int numProjects, int numChanges, OutputStream progressOut, OutputStream verboseOut)
specifier|public
name|Result
name|indexAll
parameter_list|(
name|ChangeIndex
name|index
parameter_list|,
name|Iterable
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|projects
parameter_list|,
name|int
name|numProjects
parameter_list|,
name|int
name|numChanges
parameter_list|,
name|OutputStream
name|progressOut
parameter_list|,
name|OutputStream
name|verboseOut
parameter_list|)
block|{
if|if
condition|(
name|progressOut
operator|==
literal|null
condition|)
block|{
name|progressOut
operator|=
name|NullOutputStream
operator|.
name|INSTANCE
expr_stmt|;
block|}
name|PrintWriter
name|verboseWriter
init|=
name|verboseOut
operator|!=
literal|null
condition|?
operator|new
name|PrintWriter
argument_list|(
name|verboseOut
argument_list|)
else|:
literal|null
decl_stmt|;
name|Stopwatch
name|sw
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
specifier|final
name|MultiProgressMonitor
name|mpm
init|=
operator|new
name|MultiProgressMonitor
argument_list|(
name|progressOut
argument_list|,
literal|"Reindexing changes"
argument_list|)
decl_stmt|;
specifier|final
name|Task
name|projTask
init|=
name|mpm
operator|.
name|beginSubTask
argument_list|(
literal|"projects"
argument_list|,
name|numProjects
operator|>=
literal|0
condition|?
name|numProjects
else|:
name|MultiProgressMonitor
operator|.
name|UNKNOWN
argument_list|)
decl_stmt|;
specifier|final
name|Task
name|doneTask
init|=
name|mpm
operator|.
name|beginSubTask
argument_list|(
literal|null
argument_list|,
name|numChanges
operator|>=
literal|0
condition|?
name|numChanges
else|:
name|MultiProgressMonitor
operator|.
name|UNKNOWN
argument_list|)
decl_stmt|;
specifier|final
name|Task
name|failedTask
init|=
name|mpm
operator|.
name|beginSubTask
argument_list|(
literal|"failed"
argument_list|,
name|MultiProgressMonitor
operator|.
name|UNKNOWN
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ListenableFuture
argument_list|<
name|?
argument_list|>
argument_list|>
name|futures
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|final
name|AtomicBoolean
name|ok
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Project
operator|.
name|NameKey
name|project
range|:
name|projects
control|)
block|{
if|if
condition|(
operator|!
name|updateMergeable
argument_list|(
name|project
argument_list|)
condition|)
block|{
name|ok
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ListenableFuture
argument_list|<
name|?
argument_list|>
name|future
init|=
name|executor
operator|.
name|submit
argument_list|(
name|reindexProject
argument_list|(
name|indexerFactory
operator|.
name|create
argument_list|(
name|index
argument_list|)
argument_list|,
name|project
argument_list|,
name|doneTask
argument_list|,
name|failedTask
argument_list|,
name|verboseWriter
argument_list|)
argument_list|)
decl_stmt|;
name|futures
operator|.
name|add
argument_list|(
name|future
argument_list|)
expr_stmt|;
name|future
operator|.
name|addListener
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|future
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|project
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|project
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|failAndThrow
argument_list|(
name|project
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Error
name|e
parameter_list|)
block|{
name|failAndThrow
argument_list|(
name|project
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|projTask
operator|.
name|update
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|fail
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to index project "
operator|+
name|project
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|ok
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|failAndThrow
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|RuntimeException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|project
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
specifier|private
name|void
name|failAndThrow
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|Error
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|project
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
argument_list|,
name|MoreExecutors
operator|.
name|sameThreadExecutor
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|mpm
operator|.
name|waitFor
argument_list|(
name|Futures
operator|.
name|transform
argument_list|(
name|Futures
operator|.
name|successfulAsList
argument_list|(
name|futures
argument_list|)
argument_list|,
operator|new
name|AsyncFunction
argument_list|<
name|List
argument_list|<
name|?
argument_list|>
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ListenableFuture
argument_list|<
name|Void
argument_list|>
name|apply
parameter_list|(
name|List
argument_list|<
name|?
argument_list|>
name|input
parameter_list|)
block|{
name|mpm
operator|.
name|end
argument_list|()
expr_stmt|;
return|return
name|Futures
operator|.
name|immediateFuture
argument_list|(
literal|null
argument_list|)
return|;
block|}
block|}
block|)
block|)
function|;
block|}
end_class

begin_catch
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error in batch indexer"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|ok
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
end_catch

begin_return
return|return
operator|new
name|Result
argument_list|(
name|sw
argument_list|,
name|ok
operator|.
name|get
argument_list|()
argument_list|,
name|doneTask
operator|.
name|getCount
argument_list|()
argument_list|,
name|failedTask
operator|.
name|getCount
argument_list|()
argument_list|)
return|;
end_return

begin_function
unit|}    private
DECL|method|updateMergeable (Project.NameKey project)
name|boolean
name|updateMergeable
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|)
block|{
if|if
condition|(
name|mergeabilityChecker
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|mergeabilityChecker
operator|.
name|newCheck
argument_list|()
operator|.
name|addProject
argument_list|(
name|project
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error in mergeability checker"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
end_function

begin_function
DECL|method|reindexProject (final ChangeIndexer indexer, final Project.NameKey project, final Task done, final Task failed, final PrintWriter verboseWriter)
specifier|private
name|Callable
argument_list|<
name|Void
argument_list|>
name|reindexProject
parameter_list|(
specifier|final
name|ChangeIndexer
name|indexer
parameter_list|,
specifier|final
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
specifier|final
name|Task
name|done
parameter_list|,
specifier|final
name|Task
name|failed
parameter_list|,
specifier|final
name|PrintWriter
name|verboseWriter
parameter_list|)
block|{
return|return
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|Multimap
argument_list|<
name|ObjectId
argument_list|,
name|ChangeData
argument_list|>
name|byId
init|=
name|ArrayListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
name|Repository
name|repo
init|=
literal|null
decl_stmt|;
name|ReviewDb
name|db
init|=
literal|null
decl_stmt|;
try|try
block|{
name|repo
operator|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|project
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Ref
argument_list|>
name|refs
init|=
name|repo
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|getRefs
argument_list|(
name|ALL
argument_list|)
decl_stmt|;
name|db
operator|=
name|schemaFactory
operator|.
name|open
argument_list|()
expr_stmt|;
for|for
control|(
name|Change
name|c
range|:
name|db
operator|.
name|changes
argument_list|()
operator|.
name|byProject
argument_list|(
name|project
argument_list|)
control|)
block|{
name|Ref
name|r
init|=
name|refs
operator|.
name|get
argument_list|(
name|c
operator|.
name|currentPatchSetId
argument_list|()
operator|.
name|toRefName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
name|byId
operator|.
name|put
argument_list|(
name|r
operator|.
name|getObjectId
argument_list|()
argument_list|,
name|changeDataFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
operator|new
name|ProjectIndexer
argument_list|(
name|indexer
argument_list|,
name|byId
argument_list|,
name|repo
argument_list|,
name|done
argument_list|,
name|failed
argument_list|,
name|verboseWriter
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryNotFoundException
name|rnfe
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|rnfe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|db
operator|!=
literal|null
condition|)
block|{
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|repo
operator|!=
literal|null
condition|)
block|{
name|repo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// TODO(dborowitz): Opening all repositories in a live server may be
comment|// wasteful; see if we can determine which ones it is safe to close
comment|// with RepositoryCache.close(repo).
block|}
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
end_function

begin_class
DECL|class|ProjectIndexer
specifier|private
specifier|static
class|class
name|ProjectIndexer
implements|implements
name|Callable
argument_list|<
name|Void
argument_list|>
block|{
DECL|field|indexer
specifier|private
specifier|final
name|ChangeIndexer
name|indexer
decl_stmt|;
DECL|field|byId
specifier|private
specifier|final
name|Multimap
argument_list|<
name|ObjectId
argument_list|,
name|ChangeData
argument_list|>
name|byId
decl_stmt|;
DECL|field|done
specifier|private
specifier|final
name|ProgressMonitor
name|done
decl_stmt|;
DECL|field|failed
specifier|private
specifier|final
name|ProgressMonitor
name|failed
decl_stmt|;
DECL|field|verboseWriter
specifier|private
specifier|final
name|PrintWriter
name|verboseWriter
decl_stmt|;
DECL|field|repo
specifier|private
specifier|final
name|Repository
name|repo
decl_stmt|;
DECL|field|walk
specifier|private
name|RevWalk
name|walk
decl_stmt|;
DECL|method|ProjectIndexer (ChangeIndexer indexer, Multimap<ObjectId, ChangeData> changesByCommitId, Repository repo, ProgressMonitor done, ProgressMonitor failed, PrintWriter verboseWriter)
specifier|private
name|ProjectIndexer
parameter_list|(
name|ChangeIndexer
name|indexer
parameter_list|,
name|Multimap
argument_list|<
name|ObjectId
argument_list|,
name|ChangeData
argument_list|>
name|changesByCommitId
parameter_list|,
name|Repository
name|repo
parameter_list|,
name|ProgressMonitor
name|done
parameter_list|,
name|ProgressMonitor
name|failed
parameter_list|,
name|PrintWriter
name|verboseWriter
parameter_list|)
block|{
name|this
operator|.
name|indexer
operator|=
name|indexer
expr_stmt|;
name|this
operator|.
name|byId
operator|=
name|changesByCommitId
expr_stmt|;
name|this
operator|.
name|repo
operator|=
name|repo
expr_stmt|;
name|this
operator|.
name|done
operator|=
name|done
expr_stmt|;
name|this
operator|.
name|failed
operator|=
name|failed
expr_stmt|;
name|this
operator|.
name|verboseWriter
operator|=
name|verboseWriter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|walk
operator|=
operator|new
name|RevWalk
argument_list|(
name|repo
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Walk only refs first to cover as many changes as we can without having
comment|// to mark every single change.
for|for
control|(
name|Ref
name|ref
range|:
name|repo
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|getRefs
argument_list|(
name|Constants
operator|.
name|R_HEADS
argument_list|)
operator|.
name|values
argument_list|()
control|)
block|{
name|RevObject
name|o
init|=
name|walk
operator|.
name|parseAny
argument_list|(
name|ref
operator|.
name|getObjectId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|RevCommit
condition|)
block|{
name|walk
operator|.
name|markStart
argument_list|(
operator|(
name|RevCommit
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
block|}
name|RevCommit
name|bCommit
decl_stmt|;
while|while
condition|(
operator|(
name|bCommit
operator|=
name|walk
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
operator|&&
operator|!
name|byId
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|byId
operator|.
name|containsKey
argument_list|(
name|bCommit
argument_list|)
condition|)
block|{
name|getPathsAndIndex
argument_list|(
name|bCommit
argument_list|)
expr_stmt|;
name|byId
operator|.
name|removeAll
argument_list|(
name|bCommit
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|ObjectId
name|id
range|:
name|byId
operator|.
name|keySet
argument_list|()
control|)
block|{
name|getPathsAndIndex
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|walk
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getPathsAndIndex (ObjectId b)
specifier|private
name|void
name|getPathsAndIndex
parameter_list|(
name|ObjectId
name|b
parameter_list|)
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|ChangeData
argument_list|>
name|cds
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|byId
operator|.
name|get
argument_list|(
name|b
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|RevCommit
name|bCommit
init|=
name|walk
operator|.
name|parseCommit
argument_list|(
name|b
argument_list|)
decl_stmt|;
name|RevTree
name|bTree
init|=
name|bCommit
operator|.
name|getTree
argument_list|()
decl_stmt|;
name|RevTree
name|aTree
init|=
name|aFor
argument_list|(
name|bCommit
argument_list|,
name|walk
argument_list|)
decl_stmt|;
name|DiffFormatter
name|df
init|=
operator|new
name|DiffFormatter
argument_list|(
name|DisabledOutputStream
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
try|try
block|{
name|df
operator|.
name|setRepository
argument_list|(
name|repo
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|cds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|paths
init|=
operator|(
name|aTree
operator|!=
literal|null
operator|)
condition|?
name|getPaths
argument_list|(
name|df
operator|.
name|scan
argument_list|(
name|aTree
argument_list|,
name|bTree
argument_list|)
argument_list|)
else|:
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptyList
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|ChangeData
argument_list|>
name|cdit
init|=
name|cds
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|ChangeData
name|cd
init|;
name|cdit
operator|.
name|hasNext
argument_list|()
condition|;
name|cdit
operator|.
name|remove
argument_list|()
control|)
block|{
name|cd
operator|=
name|cdit
operator|.
name|next
argument_list|()
expr_stmt|;
try|try
block|{
name|cd
operator|.
name|setCurrentFilePaths
argument_list|(
name|paths
argument_list|)
expr_stmt|;
name|indexer
operator|.
name|index
argument_list|(
name|cd
argument_list|)
expr_stmt|;
name|done
operator|.
name|update
argument_list|(
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|verboseWriter
operator|!=
literal|null
condition|)
block|{
name|verboseWriter
operator|.
name|println
argument_list|(
literal|"Reindexed change "
operator|+
name|cd
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Failed to index change "
operator|+
name|cd
operator|.
name|getId
argument_list|()
argument_list|,
literal|true
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|df
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Failed to index commit "
operator|+
name|b
operator|.
name|name
argument_list|()
argument_list|,
literal|false
argument_list|,
name|e
argument_list|)
expr_stmt|;
for|for
control|(
name|ChangeData
name|cd
range|:
name|cds
control|)
block|{
name|fail
argument_list|(
literal|"Failed to index change "
operator|+
name|cd
operator|.
name|getId
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getPaths (List<DiffEntry> filenames)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|getPaths
parameter_list|(
name|List
argument_list|<
name|DiffEntry
argument_list|>
name|filenames
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|Sets
operator|.
name|newTreeSet
argument_list|()
decl_stmt|;
for|for
control|(
name|DiffEntry
name|e
range|:
name|filenames
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getOldPath
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|e
operator|.
name|getOldPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|e
operator|.
name|getNewPath
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|e
operator|.
name|getNewPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|paths
argument_list|)
return|;
block|}
DECL|method|aFor (RevCommit b, RevWalk walk)
specifier|private
name|RevTree
name|aFor
parameter_list|(
name|RevCommit
name|b
parameter_list|,
name|RevWalk
name|walk
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|b
operator|.
name|getParentCount
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
return|return
name|walk
operator|.
name|parseTree
argument_list|(
name|emptyTree
argument_list|()
argument_list|)
return|;
case|case
literal|1
case|:
name|RevCommit
name|a
init|=
name|b
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|walk
operator|.
name|parseBody
argument_list|(
name|a
argument_list|)
expr_stmt|;
return|return
name|walk
operator|.
name|parseTree
argument_list|(
name|a
operator|.
name|getTree
argument_list|()
argument_list|)
return|;
case|case
literal|2
case|:
return|return
name|PatchListLoader
operator|.
name|automerge
argument_list|(
name|repo
argument_list|,
name|walk
argument_list|,
name|b
argument_list|)
return|;
default|default:
return|return
literal|null
return|;
block|}
block|}
DECL|method|emptyTree ()
specifier|private
name|ObjectId
name|emptyTree
parameter_list|()
throws|throws
name|IOException
block|{
name|ObjectInserter
name|oi
init|=
name|repo
operator|.
name|newObjectInserter
argument_list|()
decl_stmt|;
try|try
block|{
name|ObjectId
name|id
init|=
name|oi
operator|.
name|insert
argument_list|(
name|Constants
operator|.
name|OBJ_TREE
argument_list|,
operator|new
name|byte
index|[]
block|{}
argument_list|)
decl_stmt|;
name|oi
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|id
return|;
block|}
finally|finally
block|{
name|oi
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|fail (String error, boolean failed, Exception e)
specifier|private
name|void
name|fail
parameter_list|(
name|String
name|error
parameter_list|,
name|boolean
name|failed
parameter_list|,
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|failed
condition|)
block|{
name|this
operator|.
name|failed
operator|.
name|update
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|e
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|error
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|verboseWriter
operator|!=
literal|null
condition|)
block|{
name|verboseWriter
operator|.
name|println
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

unit|}
end_unit

