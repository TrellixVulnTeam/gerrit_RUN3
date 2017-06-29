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
DECL|package|com.google.gerrit.server.index.change
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
operator|.
name|change
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkState
import|;
end_import

begin_import
import|import static
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
operator|.
name|successfulAsList
import|;
end_import

begin_import
import|import static
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
operator|.
name|transform
import|;
end_import

begin_import
import|import static
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
operator|.
name|directExecutor
import|;
end_import

begin_import
import|import static
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
name|QueueProvider
operator|.
name|QueueType
operator|.
name|BATCH
import|;
end_import

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
name|ComparisonChain
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
name|ListMultimap
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
name|MultimapBuilder
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
name|index
operator|.
name|IndexExecutor
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
name|index
operator|.
name|SiteIndexer
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
name|notedb
operator|.
name|ChangeNotes
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
name|project
operator|.
name|ProjectCache
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
name|PrintWriter
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
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|atomic
operator|.
name|AtomicBoolean
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
name|lib
operator|.
name|TextProgressMonitor
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
name|RevWalk
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

begin_class
DECL|class|AllChangesIndexer
specifier|public
class|class
name|AllChangesIndexer
extends|extends
name|SiteIndexer
argument_list|<
name|Change
operator|.
name|Id
argument_list|,
name|ChangeData
argument_list|,
name|ChangeIndex
argument_list|>
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
name|AllChangesIndexer
operator|.
name|class
argument_list|)
decl_stmt|;
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
DECL|field|notesFactory
specifier|private
specifier|final
name|ChangeNotes
operator|.
name|Factory
name|notesFactory
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
annotation|@
name|Inject
DECL|method|AllChangesIndexer ( SchemaFactory<ReviewDb> schemaFactory, ChangeData.Factory changeDataFactory, GitRepositoryManager repoManager, @IndexExecutor(BATCH) ListeningExecutorService executor, ChangeIndexer.Factory indexerFactory, ChangeNotes.Factory notesFactory, ProjectCache projectCache)
name|AllChangesIndexer
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
argument_list|(
name|BATCH
argument_list|)
name|ListeningExecutorService
name|executor
parameter_list|,
name|ChangeIndexer
operator|.
name|Factory
name|indexerFactory
parameter_list|,
name|ChangeNotes
operator|.
name|Factory
name|notesFactory
parameter_list|,
name|ProjectCache
name|projectCache
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
name|notesFactory
operator|=
name|notesFactory
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
block|}
DECL|class|ProjectHolder
specifier|private
specifier|static
class|class
name|ProjectHolder
implements|implements
name|Comparable
argument_list|<
name|ProjectHolder
argument_list|>
block|{
DECL|field|name
specifier|private
name|Project
operator|.
name|NameKey
name|name
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
DECL|method|ProjectHolder (Project.NameKey name, int size)
name|ProjectHolder
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|,
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareTo (ProjectHolder other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|ProjectHolder
name|other
parameter_list|)
block|{
return|return
name|ComparisonChain
operator|.
name|start
argument_list|()
operator|.
name|compare
argument_list|(
name|other
operator|.
name|size
argument_list|,
name|size
argument_list|)
operator|.
name|compare
argument_list|(
name|other
operator|.
name|name
operator|.
name|get
argument_list|()
argument_list|,
name|name
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|result
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|indexAll (ChangeIndex index)
specifier|public
name|Result
name|indexAll
parameter_list|(
name|ChangeIndex
name|index
parameter_list|)
block|{
name|ProgressMonitor
name|pm
init|=
operator|new
name|TextProgressMonitor
argument_list|()
decl_stmt|;
name|pm
operator|.
name|beginTask
argument_list|(
literal|"Collecting projects"
argument_list|,
name|ProgressMonitor
operator|.
name|UNKNOWN
argument_list|)
expr_stmt|;
name|SortedSet
argument_list|<
name|ProjectHolder
argument_list|>
name|projects
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|changeCount
init|=
literal|0
decl_stmt|;
name|Stopwatch
name|sw
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
for|for
control|(
name|Project
operator|.
name|NameKey
name|name
range|:
name|projectCache
operator|.
name|all
argument_list|()
control|)
block|{
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|name
argument_list|)
init|)
block|{
name|int
name|size
init|=
name|ChangeNotes
operator|.
name|Factory
operator|.
name|scan
argument_list|(
name|repo
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
name|changeCount
operator|+=
name|size
expr_stmt|;
name|projects
operator|.
name|add
argument_list|(
operator|new
name|ProjectHolder
argument_list|(
name|name
argument_list|,
name|size
argument_list|)
argument_list|)
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
literal|"Error collecting projects"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
operator|new
name|Result
argument_list|(
name|sw
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
return|;
block|}
name|pm
operator|.
name|update
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|pm
operator|.
name|endTask
argument_list|()
expr_stmt|;
name|setTotalWork
argument_list|(
name|changeCount
argument_list|)
expr_stmt|;
return|return
name|indexAll
argument_list|(
name|index
argument_list|,
name|projects
argument_list|)
return|;
block|}
DECL|method|indexAll (ChangeIndex index, SortedSet<ProjectHolder> projects)
specifier|private
name|SiteIndexer
operator|.
name|Result
name|indexAll
parameter_list|(
name|ChangeIndex
name|index
parameter_list|,
name|SortedSet
argument_list|<
name|ProjectHolder
argument_list|>
name|projects
parameter_list|)
block|{
name|Stopwatch
name|sw
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
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
name|Task
name|projTask
init|=
name|mpm
operator|.
name|beginSubTask
argument_list|(
literal|"projects"
argument_list|,
name|projects
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|checkState
argument_list|(
name|totalWork
operator|>=
literal|0
argument_list|)
expr_stmt|;
name|Task
name|doneTask
init|=
name|mpm
operator|.
name|beginSubTask
argument_list|(
literal|null
argument_list|,
name|totalWork
argument_list|)
decl_stmt|;
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
name|List
argument_list|<
name|ListenableFuture
argument_list|<
name|?
argument_list|>
argument_list|>
name|futures
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
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
name|ProjectHolder
name|project
range|:
name|projects
control|)
block|{
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
name|executor
argument_list|,
name|index
argument_list|)
argument_list|,
name|project
operator|.
name|name
argument_list|,
name|doneTask
argument_list|,
name|failedTask
argument_list|,
name|verboseWriter
argument_list|)
argument_list|)
decl_stmt|;
name|addErrorListener
argument_list|(
name|future
argument_list|,
literal|"project "
operator|+
name|project
operator|.
name|name
argument_list|,
name|projTask
argument_list|,
name|ok
argument_list|)
expr_stmt|;
name|futures
operator|.
name|add
argument_list|(
name|future
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|mpm
operator|.
name|waitFor
argument_list|(
name|transform
argument_list|(
name|successfulAsList
argument_list|(
name|futures
argument_list|)
argument_list|,
name|x
lambda|->
block|{
name|mpm
operator|.
name|end
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
argument_list|,
name|directExecutor
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
comment|// If too many changes failed, maybe there was a bug in the indexer. Don't
comment|// trust the results. This is not an exact percentage since we bump the same
comment|// failure counter if a project can't be read, but close enough.
name|int
name|nFailed
init|=
name|failedTask
operator|.
name|getCount
argument_list|()
decl_stmt|;
name|int
name|nDone
init|=
name|doneTask
operator|.
name|getCount
argument_list|()
decl_stmt|;
name|int
name|nTotal
init|=
name|nFailed
operator|+
name|nDone
decl_stmt|;
name|double
name|pctFailed
init|=
operator|(
operator|(
name|double
operator|)
name|nFailed
operator|)
operator|/
name|nTotal
operator|*
literal|100
decl_stmt|;
if|if
condition|(
name|pctFailed
operator|>
literal|10
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed {}/{} changes ({}%); not marking new index as ready"
argument_list|,
name|nFailed
argument_list|,
name|nTotal
argument_list|,
name|Math
operator|.
name|round
argument_list|(
name|pctFailed
argument_list|)
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
name|nDone
argument_list|,
name|nFailed
argument_list|)
return|;
block|}
DECL|method|reindexProject ( ChangeIndexer indexer, Project.NameKey project, Task done, Task failed, PrintWriter verboseWriter)
specifier|public
name|Callable
argument_list|<
name|Void
argument_list|>
name|reindexProject
parameter_list|(
name|ChangeIndexer
name|indexer
parameter_list|,
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|Task
name|done
parameter_list|,
name|Task
name|failed
parameter_list|,
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
name|ListMultimap
argument_list|<
name|ObjectId
argument_list|,
name|ChangeData
argument_list|>
name|byId
init|=
name|MultimapBuilder
operator|.
name|hashKeys
argument_list|()
operator|.
name|arrayListValues
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// TODO(dborowitz): Opening all repositories in a live server may be
comment|// wasteful; see if we can determine which ones it is safe to close
comment|// with RepositoryCache.close(repo).
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|project
argument_list|)
init|;
name|ReviewDb
name|db
operator|=
name|schemaFactory
operator|.
name|open
argument_list|()
init|)
block|{
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
comment|// TODO(dborowitz): Pre-loading all notes is almost certainly a
comment|// terrible idea for performance. If we can get rid of walking by
comment|// commit (see note below), then all we need to discover here is the
comment|// change IDs.
for|for
control|(
name|ChangeNotes
name|cn
range|:
name|notesFactory
operator|.
name|scan
argument_list|(
name|repo
argument_list|,
name|db
argument_list|,
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
name|cn
operator|.
name|getChange
argument_list|()
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
name|cn
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
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Index all changes of project "
operator|+
name|project
operator|.
name|get
argument_list|()
return|;
block|}
block|}
return|;
block|}
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
name|ListMultimap
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
DECL|method|ProjectIndexer ( ChangeIndexer indexer, ListMultimap<ObjectId, ChangeData> changesByCommitId, Repository repo, ProgressMonitor done, ProgressMonitor failed, PrintWriter verboseWriter)
specifier|private
name|ProjectIndexer
parameter_list|(
name|ChangeIndexer
name|indexer
parameter_list|,
name|ListMultimap
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
try|try
init|(
name|RevWalk
name|walk
init|=
operator|new
name|RevWalk
argument_list|(
name|repo
argument_list|)
init|)
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
name|index
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
name|index
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|index (ObjectId b)
specifier|private
name|void
name|index
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
if|if
condition|(
operator|!
name|cds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
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

end_unit

