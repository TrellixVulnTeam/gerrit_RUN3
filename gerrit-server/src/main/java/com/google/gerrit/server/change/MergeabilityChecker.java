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
DECL|package|com.google.gerrit.server.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|change
package|;
end_package

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
name|Function
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
name|Throwables
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
name|Iterables
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
name|CheckedFuture
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
name|extensions
operator|.
name|events
operator|.
name|GitReferenceUpdatedListener
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
name|extensions
operator|.
name|restapi
operator|.
name|ResourceConflictException
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
name|Branch
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
name|PatchSet
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
name|client
operator|.
name|RefNames
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
name|CurrentUser
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
name|IdentifiedUser
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
name|MergeabilityChecksExecutor
operator|.
name|Priority
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
name|Mergeable
operator|.
name|MergeableInfo
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
name|MetaDataUpdate
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
name|ProjectConfig
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
name|WorkQueue
operator|.
name|Executor
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
name|ChangeIndexer
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
name|ChangeControl
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
name|RequestContext
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
name|ThreadLocalRequestContext
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
name|OrmException
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
name|Provider
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
name|ProvisionException
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
name|ConfigInvalidException
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
name|ExecutionException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_class
DECL|class|MergeabilityChecker
specifier|public
class|class
name|MergeabilityChecker
implements|implements
name|GitReferenceUpdatedListener
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
name|MergeabilityChecker
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|MAPPER
specifier|private
specifier|static
specifier|final
name|Function
argument_list|<
name|Exception
argument_list|,
name|IOException
argument_list|>
name|MAPPER
init|=
operator|new
name|Function
argument_list|<
name|Exception
argument_list|,
name|IOException
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|IOException
name|apply
parameter_list|(
name|Exception
name|in
parameter_list|)
block|{
if|if
condition|(
name|in
operator|instanceof
name|IOException
condition|)
block|{
return|return
operator|(
name|IOException
operator|)
name|in
return|;
block|}
elseif|else
if|if
condition|(
name|in
operator|instanceof
name|ExecutionException
operator|&&
name|in
operator|.
name|getCause
argument_list|()
operator|instanceof
name|IOException
condition|)
block|{
return|return
operator|(
name|IOException
operator|)
name|in
operator|.
name|getCause
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|new
name|IOException
argument_list|(
name|in
argument_list|)
return|;
block|}
block|}
block|}
decl_stmt|;
DECL|class|Check
specifier|public
class|class
name|Check
block|{
DECL|field|changes
specifier|private
name|List
argument_list|<
name|Change
argument_list|>
name|changes
decl_stmt|;
DECL|field|branches
specifier|private
name|List
argument_list|<
name|Branch
operator|.
name|NameKey
argument_list|>
name|branches
decl_stmt|;
DECL|field|projects
specifier|private
name|List
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|projects
decl_stmt|;
DECL|field|force
specifier|private
name|boolean
name|force
decl_stmt|;
DECL|field|reindex
specifier|private
name|boolean
name|reindex
decl_stmt|;
DECL|field|interactive
specifier|private
name|boolean
name|interactive
decl_stmt|;
DECL|method|Check ()
specifier|private
name|Check
parameter_list|()
block|{
name|changes
operator|=
name|Lists
operator|.
name|newArrayListWithExpectedSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|branches
operator|=
name|Lists
operator|.
name|newArrayListWithExpectedSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|projects
operator|=
name|Lists
operator|.
name|newArrayListWithExpectedSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|interactive
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|addChange (Change change)
specifier|public
name|Check
name|addChange
parameter_list|(
name|Change
name|change
parameter_list|)
block|{
name|changes
operator|.
name|add
argument_list|(
name|change
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addBranch (Branch.NameKey branch)
specifier|public
name|Check
name|addBranch
parameter_list|(
name|Branch
operator|.
name|NameKey
name|branch
parameter_list|)
block|{
name|branches
operator|.
name|add
argument_list|(
name|branch
argument_list|)
expr_stmt|;
name|interactive
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addProject (Project.NameKey project)
specifier|public
name|Check
name|addProject
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|)
block|{
name|projects
operator|.
name|add
argument_list|(
name|project
argument_list|)
expr_stmt|;
name|interactive
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Force reindexing regardless of whether mergeable flag was modified. */
DECL|method|reindex ()
specifier|public
name|Check
name|reindex
parameter_list|()
block|{
name|reindex
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Force mergeability check even if change is not stale. */
DECL|method|force ()
specifier|private
name|Check
name|force
parameter_list|()
block|{
name|force
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getExecutor ()
specifier|private
name|ListeningExecutorService
name|getExecutor
parameter_list|()
block|{
return|return
name|interactive
condition|?
name|interactiveExecutor
else|:
name|backgroundExecutor
return|;
block|}
DECL|method|runAsync ()
specifier|public
name|CheckedFuture
argument_list|<
name|?
argument_list|,
name|IOException
argument_list|>
name|runAsync
parameter_list|()
block|{
specifier|final
name|ListeningExecutorService
name|executor
init|=
name|getExecutor
argument_list|()
decl_stmt|;
name|ListenableFuture
argument_list|<
name|List
argument_list|<
name|Change
argument_list|>
argument_list|>
name|getChanges
decl_stmt|;
if|if
condition|(
name|branches
operator|.
name|isEmpty
argument_list|()
operator|&&
name|projects
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|getChanges
operator|=
name|Futures
operator|.
name|immediateFuture
argument_list|(
name|changes
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|getChanges
operator|=
name|executor
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|List
argument_list|<
name|Change
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Change
argument_list|>
name|call
parameter_list|()
throws|throws
name|OrmException
block|{
return|return
name|getChanges
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|Futures
operator|.
name|makeChecked
argument_list|(
name|Futures
operator|.
name|transform
argument_list|(
name|getChanges
argument_list|,
operator|new
name|AsyncFunction
argument_list|<
name|List
argument_list|<
name|Change
argument_list|>
argument_list|,
name|List
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ListenableFuture
argument_list|<
name|List
argument_list|<
name|Object
argument_list|>
argument_list|>
name|apply
parameter_list|(
name|List
argument_list|<
name|Change
argument_list|>
name|changes
parameter_list|)
block|{
name|List
argument_list|<
name|ListenableFuture
argument_list|<
name|?
argument_list|>
argument_list|>
name|result
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|changes
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Change
name|c
range|:
name|changes
control|)
block|{
name|ListenableFuture
argument_list|<
name|Boolean
argument_list|>
name|b
init|=
name|executor
operator|.
name|submit
argument_list|(
operator|new
name|Task
argument_list|(
name|c
argument_list|,
name|force
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|reindex
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|Futures
operator|.
name|transform
argument_list|(
name|b
argument_list|,
operator|new
name|AsyncFunction
argument_list|<
name|Boolean
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
name|ListenableFuture
argument_list|<
name|Object
argument_list|>
name|apply
parameter_list|(
name|Boolean
name|indexUpdated
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|indexUpdated
condition|)
block|{
return|return
operator|(
name|ListenableFuture
argument_list|<
name|Object
argument_list|>
operator|)
name|indexer
operator|.
name|indexAsync
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
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
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|add
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|Futures
operator|.
name|allAsList
argument_list|(
name|result
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|,
name|MAPPER
argument_list|)
return|;
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|runAsync
argument_list|()
operator|.
name|checkedGet
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Throwables
operator|.
name|propagateIfPossible
argument_list|(
name|e
argument_list|,
name|IOException
operator|.
name|class
argument_list|)
expr_stmt|;
throw|throw
name|MAPPER
operator|.
name|apply
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getChanges ()
specifier|private
name|List
argument_list|<
name|Change
argument_list|>
name|getChanges
parameter_list|()
throws|throws
name|OrmException
block|{
name|ReviewDb
name|db
init|=
name|schemaFactory
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|Change
argument_list|>
name|results
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|results
operator|.
name|addAll
argument_list|(
name|changes
argument_list|)
expr_stmt|;
for|for
control|(
name|Project
operator|.
name|NameKey
name|p
range|:
name|projects
control|)
block|{
name|Iterables
operator|.
name|addAll
argument_list|(
name|results
argument_list|,
name|db
operator|.
name|changes
argument_list|()
operator|.
name|byProjectOpenAll
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Branch
operator|.
name|NameKey
name|b
range|:
name|branches
control|)
block|{
name|Iterables
operator|.
name|addAll
argument_list|(
name|results
argument_list|,
name|db
operator|.
name|changes
argument_list|()
operator|.
name|byBranchOpenAll
argument_list|(
name|b
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to fetch changes for mergeability check"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|field|tl
specifier|private
specifier|final
name|ThreadLocalRequestContext
name|tl
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
DECL|field|identifiedUserFactory
specifier|private
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|identifiedUserFactory
decl_stmt|;
DECL|field|changeControlFactory
specifier|private
specifier|final
name|ChangeControl
operator|.
name|GenericFactory
name|changeControlFactory
decl_stmt|;
DECL|field|mergeable
specifier|private
specifier|final
name|Provider
argument_list|<
name|Mergeable
argument_list|>
name|mergeable
decl_stmt|;
DECL|field|indexer
specifier|private
specifier|final
name|ChangeIndexer
name|indexer
decl_stmt|;
DECL|field|backgroundExecutor
specifier|private
specifier|final
name|ListeningExecutorService
name|backgroundExecutor
decl_stmt|;
DECL|field|interactiveExecutor
specifier|private
specifier|final
name|ListeningExecutorService
name|interactiveExecutor
decl_stmt|;
DECL|field|mergeabilityCheckQueue
specifier|private
specifier|final
name|MergeabilityCheckQueue
name|mergeabilityCheckQueue
decl_stmt|;
DECL|field|metaDataUpdateFactory
specifier|private
specifier|final
name|MetaDataUpdate
operator|.
name|Server
name|metaDataUpdateFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|MergeabilityChecker (ThreadLocalRequestContext tl, SchemaFactory<ReviewDb> schemaFactory, IdentifiedUser.GenericFactory identifiedUserFactory, ChangeControl.GenericFactory changeControlFactory, Provider<Mergeable> mergeable, ChangeIndexer indexer, @MergeabilityChecksExecutor(Priority.BACKGROUND) Executor backgroundExecutor, @MergeabilityChecksExecutor(Priority.INTERACTIVE) Executor interactiveExecutor, MergeabilityCheckQueue mergeabilityCheckQueue, MetaDataUpdate.Server metaDataUpdateFactory)
specifier|public
name|MergeabilityChecker
parameter_list|(
name|ThreadLocalRequestContext
name|tl
parameter_list|,
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schemaFactory
parameter_list|,
name|IdentifiedUser
operator|.
name|GenericFactory
name|identifiedUserFactory
parameter_list|,
name|ChangeControl
operator|.
name|GenericFactory
name|changeControlFactory
parameter_list|,
name|Provider
argument_list|<
name|Mergeable
argument_list|>
name|mergeable
parameter_list|,
name|ChangeIndexer
name|indexer
parameter_list|,
annotation|@
name|MergeabilityChecksExecutor
argument_list|(
name|Priority
operator|.
name|BACKGROUND
argument_list|)
name|Executor
name|backgroundExecutor
parameter_list|,
annotation|@
name|MergeabilityChecksExecutor
argument_list|(
name|Priority
operator|.
name|INTERACTIVE
argument_list|)
name|Executor
name|interactiveExecutor
parameter_list|,
name|MergeabilityCheckQueue
name|mergeabilityCheckQueue
parameter_list|,
name|MetaDataUpdate
operator|.
name|Server
name|metaDataUpdateFactory
parameter_list|)
block|{
name|this
operator|.
name|tl
operator|=
name|tl
expr_stmt|;
name|this
operator|.
name|schemaFactory
operator|=
name|schemaFactory
expr_stmt|;
name|this
operator|.
name|identifiedUserFactory
operator|=
name|identifiedUserFactory
expr_stmt|;
name|this
operator|.
name|changeControlFactory
operator|=
name|changeControlFactory
expr_stmt|;
name|this
operator|.
name|mergeable
operator|=
name|mergeable
expr_stmt|;
name|this
operator|.
name|indexer
operator|=
name|indexer
expr_stmt|;
name|this
operator|.
name|backgroundExecutor
operator|=
name|MoreExecutors
operator|.
name|listeningDecorator
argument_list|(
name|backgroundExecutor
argument_list|)
expr_stmt|;
name|this
operator|.
name|interactiveExecutor
operator|=
name|MoreExecutors
operator|.
name|listeningDecorator
argument_list|(
name|interactiveExecutor
argument_list|)
expr_stmt|;
name|this
operator|.
name|mergeabilityCheckQueue
operator|=
name|mergeabilityCheckQueue
expr_stmt|;
name|this
operator|.
name|metaDataUpdateFactory
operator|=
name|metaDataUpdateFactory
expr_stmt|;
block|}
DECL|method|newCheck ()
specifier|public
name|Check
name|newCheck
parameter_list|()
block|{
return|return
operator|new
name|Check
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|onGitReferenceUpdated (GitReferenceUpdatedListener.Event event)
specifier|public
name|void
name|onGitReferenceUpdated
parameter_list|(
name|GitReferenceUpdatedListener
operator|.
name|Event
name|event
parameter_list|)
block|{
name|String
name|ref
init|=
name|event
operator|.
name|getRefName
argument_list|()
decl_stmt|;
if|if
condition|(
name|ref
operator|.
name|startsWith
argument_list|(
name|Constants
operator|.
name|R_HEADS
argument_list|)
operator|||
name|ref
operator|.
name|equals
argument_list|(
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|)
condition|)
block|{
name|Branch
operator|.
name|NameKey
name|branch
init|=
operator|new
name|Branch
operator|.
name|NameKey
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|event
operator|.
name|getProjectName
argument_list|()
argument_list|)
argument_list|,
name|ref
argument_list|)
decl_stmt|;
name|newCheck
argument_list|()
operator|.
name|addBranch
argument_list|(
name|branch
argument_list|)
operator|.
name|runAsync
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|ref
operator|.
name|equals
argument_list|(
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|)
condition|)
block|{
name|Project
operator|.
name|NameKey
name|p
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|event
operator|.
name|getProjectName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|ProjectConfig
name|oldCfg
init|=
name|parseConfig
argument_list|(
name|p
argument_list|,
name|event
operator|.
name|getOldObjectId
argument_list|()
argument_list|)
decl_stmt|;
name|ProjectConfig
name|newCfg
init|=
name|parseConfig
argument_list|(
name|p
argument_list|,
name|event
operator|.
name|getNewObjectId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|recheckMerges
argument_list|(
name|oldCfg
argument_list|,
name|newCfg
argument_list|)
condition|)
block|{
name|newCheck
argument_list|()
operator|.
name|addProject
argument_list|(
name|p
argument_list|)
operator|.
name|force
argument_list|()
operator|.
name|runAsync
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ConfigInvalidException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Failed to update mergeability flags for project "
operator|+
name|p
operator|.
name|get
argument_list|()
operator|+
literal|" on update of "
operator|+
name|RefNames
operator|.
name|REFS_CONFIG
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|recheckMerges (ProjectConfig oldCfg, ProjectConfig newCfg)
specifier|private
name|boolean
name|recheckMerges
parameter_list|(
name|ProjectConfig
name|oldCfg
parameter_list|,
name|ProjectConfig
name|newCfg
parameter_list|)
block|{
if|if
condition|(
name|oldCfg
operator|==
literal|null
operator|||
name|newCfg
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
operator|!
name|oldCfg
operator|.
name|getProject
argument_list|()
operator|.
name|getSubmitType
argument_list|()
operator|.
name|equals
argument_list|(
name|newCfg
operator|.
name|getProject
argument_list|()
operator|.
name|getSubmitType
argument_list|()
argument_list|)
operator|||
name|oldCfg
operator|.
name|getProject
argument_list|()
operator|.
name|getUseContentMerge
argument_list|()
operator|!=
name|newCfg
operator|.
name|getProject
argument_list|()
operator|.
name|getUseContentMerge
argument_list|()
operator|||
operator|(
name|oldCfg
operator|.
name|getRulesId
argument_list|()
operator|==
literal|null
condition|?
name|newCfg
operator|.
name|getRulesId
argument_list|()
operator|!=
literal|null
else|:
operator|!
name|oldCfg
operator|.
name|getRulesId
argument_list|()
operator|.
name|equals
argument_list|(
name|newCfg
operator|.
name|getRulesId
argument_list|()
argument_list|)
operator|)
return|;
block|}
DECL|method|parseConfig (Project.NameKey p, String idStr)
specifier|private
name|ProjectConfig
name|parseConfig
parameter_list|(
name|Project
operator|.
name|NameKey
name|p
parameter_list|,
name|String
name|idStr
parameter_list|)
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
throws|,
name|RepositoryNotFoundException
block|{
name|ObjectId
name|id
init|=
name|ObjectId
operator|.
name|fromString
argument_list|(
name|idStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|ObjectId
operator|.
name|zeroId
argument_list|()
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|ProjectConfig
operator|.
name|read
argument_list|(
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|p
argument_list|)
argument_list|,
name|id
argument_list|)
return|;
block|}
DECL|class|Task
specifier|private
class|class
name|Task
implements|implements
name|Callable
argument_list|<
name|Boolean
argument_list|>
block|{
DECL|field|change
specifier|private
specifier|final
name|Change
name|change
decl_stmt|;
DECL|field|force
specifier|private
specifier|final
name|boolean
name|force
decl_stmt|;
DECL|field|reviewDb
specifier|private
name|ReviewDb
name|reviewDb
decl_stmt|;
DECL|method|Task (Change change, boolean force)
name|Task
parameter_list|(
name|Change
name|change
parameter_list|,
name|boolean
name|force
parameter_list|)
block|{
name|this
operator|.
name|change
operator|=
name|change
expr_stmt|;
name|this
operator|.
name|force
operator|=
name|force
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|Boolean
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|mergeabilityCheckQueue
operator|.
name|updatingMergeabilityFlag
argument_list|(
name|change
argument_list|,
name|force
argument_list|)
expr_stmt|;
name|RequestContext
name|context
init|=
operator|new
name|RequestContext
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|CurrentUser
name|getCurrentUser
parameter_list|()
block|{
return|return
name|identifiedUserFactory
operator|.
name|create
argument_list|(
name|change
operator|.
name|getOwner
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|getReviewDbProvider
parameter_list|()
block|{
return|return
operator|new
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ReviewDb
name|get
parameter_list|()
block|{
if|if
condition|(
name|reviewDb
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|reviewDb
operator|=
name|schemaFactory
operator|.
name|open
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ProvisionException
argument_list|(
literal|"Cannot open ReviewDb"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|reviewDb
return|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
name|RequestContext
name|old
init|=
name|tl
operator|.
name|setContext
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|ReviewDb
name|db
init|=
name|context
operator|.
name|getReviewDbProvider
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
try|try
block|{
name|PatchSet
name|ps
init|=
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|==
literal|null
condition|)
block|{
comment|// Cannot compute mergeability if current patch set is missing.
return|return
literal|false
return|;
block|}
name|Mergeable
name|m
init|=
name|mergeable
operator|.
name|get
argument_list|()
decl_stmt|;
name|m
operator|.
name|setForce
argument_list|(
name|force
argument_list|)
expr_stmt|;
name|ChangeControl
name|control
init|=
name|changeControlFactory
operator|.
name|controlFor
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|context
operator|.
name|getCurrentUser
argument_list|()
argument_list|)
decl_stmt|;
name|MergeableInfo
name|info
init|=
name|m
operator|.
name|apply
argument_list|(
operator|new
name|RevisionResource
argument_list|(
operator|new
name|ChangeResource
argument_list|(
name|control
argument_list|)
argument_list|,
name|ps
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|change
operator|.
name|isMergeable
argument_list|()
operator|!=
name|info
operator|.
name|mergeable
return|;
block|}
catch|catch
parameter_list|(
name|ResourceConflictException
name|e
parameter_list|)
block|{
comment|// change is closed
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"cannot update mergeability flag of change %d in project %s after update of %s"
argument_list|,
name|change
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|change
operator|.
name|getDest
argument_list|()
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|change
operator|.
name|getDest
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
name|tl
operator|.
name|setContext
argument_list|(
name|old
argument_list|)
expr_stmt|;
if|if
condition|(
name|reviewDb
operator|!=
literal|null
condition|)
block|{
name|reviewDb
operator|.
name|close
argument_list|()
expr_stmt|;
name|reviewDb
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

