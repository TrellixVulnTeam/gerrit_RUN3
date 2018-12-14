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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|flogger
operator|.
name|FluentLogger
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
name|Atomics
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
name|extensions
operator|.
name|events
operator|.
name|ChangeIndexedListener
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
name|index
operator|.
name|Index
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
name|IndexUtils
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
name|logging
operator|.
name|TraceContext
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
name|logging
operator|.
name|TraceContext
operator|.
name|TraceTimer
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
name|plugincontext
operator|.
name|PluginSetContext
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
name|OutOfScopeException
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|assistedinject
operator|.
name|Assisted
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
name|assistedinject
operator|.
name|AssistedInject
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
name|util
operator|.
name|Providers
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
name|Collections
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
name|atomic
operator|.
name|AtomicReference
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
name|Config
import|;
end_import

begin_comment
comment|/**  * Helper for (re)indexing a change document.  *  *<p>Indexing is run in the background, as it may require substantial work to compute some of the  * fields and/or update the index.  */
end_comment

begin_class
DECL|class|ChangeIndexer
specifier|public
class|class
name|ChangeIndexer
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|FluentLogger
name|logger
init|=
name|FluentLogger
operator|.
name|forEnclosingClass
argument_list|()
decl_stmt|;
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (ListeningExecutorService executor, ChangeIndex index)
name|ChangeIndexer
name|create
parameter_list|(
name|ListeningExecutorService
name|executor
parameter_list|,
name|ChangeIndex
name|index
parameter_list|)
function_decl|;
DECL|method|create (ListeningExecutorService executor, ChangeIndexCollection indexes)
name|ChangeIndexer
name|create
parameter_list|(
name|ListeningExecutorService
name|executor
parameter_list|,
name|ChangeIndexCollection
name|indexes
parameter_list|)
function_decl|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|allAsList ( List<? extends ListenableFuture<?>> futures)
specifier|public
specifier|static
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
argument_list|<
name|?
argument_list|,
name|IOException
argument_list|>
name|allAsList
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|ListenableFuture
argument_list|<
name|?
argument_list|>
argument_list|>
name|futures
parameter_list|)
block|{
comment|// allAsList propagates the first seen exception, wrapped in
comment|// ExecutionException, so we can reuse the same mapper as for a single
comment|// future. Assume the actual contents of the exception are not useful to
comment|// callers. All exceptions are already logged by IndexTask.
return|return
name|Futures
operator|.
name|makeChecked
argument_list|(
name|Futures
operator|.
name|allAsList
argument_list|(
name|futures
argument_list|)
argument_list|,
name|IndexUtils
operator|.
name|MAPPER
argument_list|)
return|;
block|}
DECL|field|indexes
annotation|@
name|Nullable
specifier|private
specifier|final
name|ChangeIndexCollection
name|indexes
decl_stmt|;
DECL|field|index
annotation|@
name|Nullable
specifier|private
specifier|final
name|ChangeIndex
name|index
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
DECL|field|context
specifier|private
specifier|final
name|ThreadLocalRequestContext
name|context
decl_stmt|;
DECL|field|batchExecutor
specifier|private
specifier|final
name|ListeningExecutorService
name|batchExecutor
decl_stmt|;
DECL|field|executor
specifier|private
specifier|final
name|ListeningExecutorService
name|executor
decl_stmt|;
DECL|field|indexedListeners
specifier|private
specifier|final
name|PluginSetContext
argument_list|<
name|ChangeIndexedListener
argument_list|>
name|indexedListeners
decl_stmt|;
DECL|field|stalenessChecker
specifier|private
specifier|final
name|StalenessChecker
name|stalenessChecker
decl_stmt|;
DECL|field|autoReindexIfStale
specifier|private
specifier|final
name|boolean
name|autoReindexIfStale
decl_stmt|;
annotation|@
name|AssistedInject
DECL|method|ChangeIndexer ( @erritServerConfig Config cfg, SchemaFactory<ReviewDb> schemaFactory, ChangeData.Factory changeDataFactory, ThreadLocalRequestContext context, PluginSetContext<ChangeIndexedListener> indexedListeners, StalenessChecker stalenessChecker, @IndexExecutor(BATCH) ListeningExecutorService batchExecutor, @Assisted ListeningExecutorService executor, @Assisted ChangeIndex index)
name|ChangeIndexer
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|,
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
name|ThreadLocalRequestContext
name|context
parameter_list|,
name|PluginSetContext
argument_list|<
name|ChangeIndexedListener
argument_list|>
name|indexedListeners
parameter_list|,
name|StalenessChecker
name|stalenessChecker
parameter_list|,
annotation|@
name|IndexExecutor
argument_list|(
name|BATCH
argument_list|)
name|ListeningExecutorService
name|batchExecutor
parameter_list|,
annotation|@
name|Assisted
name|ListeningExecutorService
name|executor
parameter_list|,
annotation|@
name|Assisted
name|ChangeIndex
name|index
parameter_list|)
block|{
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
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
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|indexedListeners
operator|=
name|indexedListeners
expr_stmt|;
name|this
operator|.
name|stalenessChecker
operator|=
name|stalenessChecker
expr_stmt|;
name|this
operator|.
name|batchExecutor
operator|=
name|batchExecutor
expr_stmt|;
name|this
operator|.
name|autoReindexIfStale
operator|=
name|autoReindexIfStale
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|indexes
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|AssistedInject
DECL|method|ChangeIndexer ( SchemaFactory<ReviewDb> schemaFactory, @GerritServerConfig Config cfg, ChangeData.Factory changeDataFactory, ThreadLocalRequestContext context, PluginSetContext<ChangeIndexedListener> indexedListeners, StalenessChecker stalenessChecker, @IndexExecutor(BATCH) ListeningExecutorService batchExecutor, @Assisted ListeningExecutorService executor, @Assisted ChangeIndexCollection indexes)
name|ChangeIndexer
parameter_list|(
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schemaFactory
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|,
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
parameter_list|,
name|ThreadLocalRequestContext
name|context
parameter_list|,
name|PluginSetContext
argument_list|<
name|ChangeIndexedListener
argument_list|>
name|indexedListeners
parameter_list|,
name|StalenessChecker
name|stalenessChecker
parameter_list|,
annotation|@
name|IndexExecutor
argument_list|(
name|BATCH
argument_list|)
name|ListeningExecutorService
name|batchExecutor
parameter_list|,
annotation|@
name|Assisted
name|ListeningExecutorService
name|executor
parameter_list|,
annotation|@
name|Assisted
name|ChangeIndexCollection
name|indexes
parameter_list|)
block|{
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
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
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|indexedListeners
operator|=
name|indexedListeners
expr_stmt|;
name|this
operator|.
name|stalenessChecker
operator|=
name|stalenessChecker
expr_stmt|;
name|this
operator|.
name|batchExecutor
operator|=
name|batchExecutor
expr_stmt|;
name|this
operator|.
name|autoReindexIfStale
operator|=
name|autoReindexIfStale
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|indexes
operator|=
name|indexes
expr_stmt|;
block|}
DECL|method|autoReindexIfStale (Config cfg)
specifier|private
specifier|static
name|boolean
name|autoReindexIfStale
parameter_list|(
name|Config
name|cfg
parameter_list|)
block|{
return|return
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"index"
argument_list|,
literal|null
argument_list|,
literal|"autoReindexIfStale"
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Start indexing a change.    *    * @param id change to index.    * @return future for the indexing task.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|indexAsync ( Project.NameKey project, Change.Id id)
specifier|public
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
argument_list|<
name|?
argument_list|,
name|IOException
argument_list|>
name|indexAsync
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|Change
operator|.
name|Id
name|id
parameter_list|)
block|{
return|return
name|submit
argument_list|(
operator|new
name|IndexTask
argument_list|(
name|project
argument_list|,
name|id
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Start indexing multiple changes in parallel.    *    * @param ids changes to index.    * @return future for completing indexing of all changes.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|indexAsync ( Project.NameKey project, Collection<Change.Id> ids)
specifier|public
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
argument_list|<
name|?
argument_list|,
name|IOException
argument_list|>
name|indexAsync
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|Collection
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|ids
parameter_list|)
block|{
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
argument_list|(
name|ids
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Change
operator|.
name|Id
name|id
range|:
name|ids
control|)
block|{
name|futures
operator|.
name|add
argument_list|(
name|indexAsync
argument_list|(
name|project
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|allAsList
argument_list|(
name|futures
argument_list|)
return|;
block|}
comment|/**    * Synchronously index a change, then check if the index is stale due to a race condition.    *    * @param cd change to index.    */
DECL|method|index (ChangeData cd)
specifier|public
name|void
name|index
parameter_list|(
name|ChangeData
name|cd
parameter_list|)
throws|throws
name|IOException
block|{
name|indexImpl
argument_list|(
name|cd
argument_list|)
expr_stmt|;
comment|// Always double-check whether the change might be stale immediately after
comment|// interactively indexing it. This fixes up the case where two writers write
comment|// to the primary storage in one order, and the corresponding index writes
comment|// happen in the opposite order:
comment|//  1. Writer A writes to primary storage.
comment|//  2. Writer B writes to primary storage.
comment|//  3. Writer B updates index.
comment|//  4. Writer A updates index.
comment|//
comment|// Without the extra reindexIfStale step, A has no way of knowing that it's
comment|// about to overwrite the index document with stale data. It doesn't work to
comment|// have A check for staleness before attempting its index update, because
comment|// B's index update might not have happened when it does the check.
comment|//
comment|// With the extra reindexIfStale step after (3)/(4), we are able to detect
comment|// and fix the staleness. It doesn't matter which order the two
comment|// reindexIfStale calls actually execute in; we are guaranteed that at least
comment|// one of them will execute after the second index write, (4).
name|autoReindexIfStale
argument_list|(
name|cd
argument_list|)
expr_stmt|;
block|}
DECL|method|indexImpl (ChangeData cd)
specifier|private
name|void
name|indexImpl
parameter_list|(
name|ChangeData
name|cd
parameter_list|)
throws|throws
name|IOException
block|{
name|logger
operator|.
name|atFine
argument_list|()
operator|.
name|log
argument_list|(
literal|"Replace change %d in index."
argument_list|,
name|cd
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Index
argument_list|<
name|?
argument_list|,
name|ChangeData
argument_list|>
name|i
range|:
name|getWriteIndexes
argument_list|()
control|)
block|{
try|try
init|(
name|TraceTimer
name|traceTimer
init|=
name|TraceContext
operator|.
name|newTimer
argument_list|(
literal|"Replacing change %d in index version %d"
argument_list|,
name|cd
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|i
operator|.
name|getSchema
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
init|)
block|{
name|i
operator|.
name|replace
argument_list|(
name|cd
argument_list|)
expr_stmt|;
block|}
block|}
name|fireChangeIndexedEvent
argument_list|(
name|cd
operator|.
name|project
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|cd
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|fireChangeIndexedEvent (String projectName, int id)
specifier|private
name|void
name|fireChangeIndexedEvent
parameter_list|(
name|String
name|projectName
parameter_list|,
name|int
name|id
parameter_list|)
block|{
name|indexedListeners
operator|.
name|runEach
argument_list|(
name|l
lambda|->
name|l
operator|.
name|onChangeIndexed
argument_list|(
name|projectName
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|fireChangeDeletedFromIndexEvent (int id)
specifier|private
name|void
name|fireChangeDeletedFromIndexEvent
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|indexedListeners
operator|.
name|runEach
argument_list|(
name|l
lambda|->
name|l
operator|.
name|onChangeDeleted
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Synchronously index a change.    *    * @param db review database.    * @param change change to index.    */
DECL|method|index (ReviewDb db, Change change)
specifier|public
name|void
name|index
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Change
name|change
parameter_list|)
throws|throws
name|IOException
block|{
name|index
argument_list|(
name|changeDataFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|change
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Synchronously index a change.    *    * @param db review database.    * @param project the project to which the change belongs.    * @param changeId ID of the change to index.    */
DECL|method|index (ReviewDb db, Project.NameKey project, Change.Id changeId)
specifier|public
name|void
name|index
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|Change
operator|.
name|Id
name|changeId
parameter_list|)
throws|throws
name|IOException
block|{
name|index
argument_list|(
name|changeDataFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|project
argument_list|,
name|changeId
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Start deleting a change.    *    * @param id change to delete.    * @return future for the deleting task.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|deleteAsync (Change.Id id)
specifier|public
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
argument_list|<
name|?
argument_list|,
name|IOException
argument_list|>
name|deleteAsync
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|)
block|{
return|return
name|submit
argument_list|(
operator|new
name|DeleteTask
argument_list|(
name|id
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Synchronously delete a change.    *    * @param id change ID to delete.    */
DECL|method|delete (Change.Id id)
specifier|public
name|void
name|delete
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|)
throws|throws
name|IOException
block|{
operator|new
name|DeleteTask
argument_list|(
name|id
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
block|}
comment|/**    * Asynchronously check if a change is stale, and reindex if it is.    *    *<p>Always run on the batch executor, even if this indexer instance is configured to use a    * different executor.    *    * @param project the project to which the change belongs.    * @param id ID of the change to index.    * @return future for reindexing the change; returns true if the change was stale.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|reindexIfStale ( Project.NameKey project, Change.Id id)
specifier|public
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
argument_list|<
name|Boolean
argument_list|,
name|IOException
argument_list|>
name|reindexIfStale
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|Change
operator|.
name|Id
name|id
parameter_list|)
block|{
return|return
name|submit
argument_list|(
operator|new
name|ReindexIfStaleTask
argument_list|(
name|project
argument_list|,
name|id
argument_list|)
argument_list|,
name|batchExecutor
argument_list|)
return|;
block|}
DECL|method|autoReindexIfStale (ChangeData cd)
specifier|private
name|void
name|autoReindexIfStale
parameter_list|(
name|ChangeData
name|cd
parameter_list|)
block|{
name|autoReindexIfStale
argument_list|(
name|cd
operator|.
name|project
argument_list|()
argument_list|,
name|cd
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|autoReindexIfStale (Project.NameKey project, Change.Id id)
specifier|private
name|void
name|autoReindexIfStale
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|Change
operator|.
name|Id
name|id
parameter_list|)
block|{
if|if
condition|(
name|autoReindexIfStale
condition|)
block|{
comment|// Don't retry indefinitely; if this fails the change will be stale.
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|Future
argument_list|<
name|?
argument_list|>
name|possiblyIgnoredError
init|=
name|reindexIfStale
argument_list|(
name|project
argument_list|,
name|id
argument_list|)
decl_stmt|;
block|}
block|}
DECL|method|getWriteIndexes ()
specifier|private
name|Collection
argument_list|<
name|ChangeIndex
argument_list|>
name|getWriteIndexes
parameter_list|()
block|{
return|return
name|indexes
operator|!=
literal|null
condition|?
name|indexes
operator|.
name|getWriteIndexes
argument_list|()
else|:
name|Collections
operator|.
name|singleton
argument_list|(
name|index
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|submit ( Callable<T> task)
specifier|private
parameter_list|<
name|T
parameter_list|>
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
argument_list|<
name|T
argument_list|,
name|IOException
argument_list|>
name|submit
parameter_list|(
name|Callable
argument_list|<
name|T
argument_list|>
name|task
parameter_list|)
block|{
return|return
name|submit
argument_list|(
name|task
argument_list|,
name|executor
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|submit ( Callable<T> task, ListeningExecutorService executor)
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
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
argument_list|<
name|T
argument_list|,
name|IOException
argument_list|>
name|submit
parameter_list|(
name|Callable
argument_list|<
name|T
argument_list|>
name|task
parameter_list|,
name|ListeningExecutorService
name|executor
parameter_list|)
block|{
return|return
name|Futures
operator|.
name|makeChecked
argument_list|(
name|Futures
operator|.
name|nonCancellationPropagating
argument_list|(
name|executor
operator|.
name|submit
argument_list|(
name|task
argument_list|)
argument_list|)
argument_list|,
name|IndexUtils
operator|.
name|MAPPER
argument_list|)
return|;
block|}
DECL|class|AbstractIndexTask
specifier|private
specifier|abstract
class|class
name|AbstractIndexTask
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Callable
argument_list|<
name|T
argument_list|>
block|{
DECL|field|project
specifier|protected
specifier|final
name|Project
operator|.
name|NameKey
name|project
decl_stmt|;
DECL|field|id
specifier|protected
specifier|final
name|Change
operator|.
name|Id
name|id
decl_stmt|;
DECL|method|AbstractIndexTask (Project.NameKey project, Change.Id id)
specifier|protected
name|AbstractIndexTask
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|Change
operator|.
name|Id
name|id
parameter_list|)
block|{
name|this
operator|.
name|project
operator|=
name|project
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
DECL|method|callImpl (Provider<ReviewDb> db)
specifier|protected
specifier|abstract
name|T
name|callImpl
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|)
throws|throws
name|Exception
function_decl|;
annotation|@
name|Override
DECL|method|toString ()
specifier|public
specifier|abstract
name|String
name|toString
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|call ()
specifier|public
specifier|final
name|T
name|call
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
specifier|final
name|AtomicReference
argument_list|<
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
argument_list|>
name|dbRef
init|=
name|Atomics
operator|.
name|newReference
argument_list|()
decl_stmt|;
name|RequestContext
name|newCtx
init|=
operator|new
name|RequestContext
argument_list|()
block|{
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
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
init|=
name|dbRef
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|db
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|db
operator|=
name|Providers
operator|.
name|of
argument_list|(
name|schemaFactory
operator|.
name|open
argument_list|()
argument_list|)
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
literal|"error opening ReviewDb"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|dbRef
operator|.
name|set
argument_list|(
name|db
argument_list|)
expr_stmt|;
block|}
return|return
name|db
return|;
block|}
annotation|@
name|Override
specifier|public
name|CurrentUser
name|getUser
parameter_list|()
block|{
throw|throw
operator|new
name|OutOfScopeException
argument_list|(
literal|"No user during ChangeIndexer"
argument_list|)
throw|;
block|}
block|}
decl_stmt|;
name|RequestContext
name|oldCtx
init|=
name|context
operator|.
name|setContext
argument_list|(
name|newCtx
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|callImpl
argument_list|(
name|newCtx
operator|.
name|getReviewDbProvider
argument_list|()
argument_list|)
return|;
block|}
finally|finally
block|{
name|context
operator|.
name|setContext
argument_list|(
name|oldCtx
argument_list|)
expr_stmt|;
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
init|=
name|dbRef
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|db
operator|!=
literal|null
condition|)
block|{
name|db
operator|.
name|get
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Failed to execute %s"
argument_list|,
name|this
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
DECL|class|IndexTask
specifier|private
class|class
name|IndexTask
extends|extends
name|AbstractIndexTask
argument_list|<
name|Void
argument_list|>
block|{
DECL|method|IndexTask (Project.NameKey project, Change.Id id)
specifier|private
name|IndexTask
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|Change
operator|.
name|Id
name|id
parameter_list|)
block|{
name|super
argument_list|(
name|project
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|callImpl (Provider<ReviewDb> db)
specifier|public
name|Void
name|callImpl
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|)
throws|throws
name|Exception
block|{
name|ChangeData
name|cd
init|=
name|changeDataFactory
operator|.
name|create
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|project
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|index
argument_list|(
name|cd
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"index-change-"
operator|+
name|id
return|;
block|}
block|}
comment|// Not AbstractIndexTask as it doesn't need ReviewDb.
DECL|class|DeleteTask
specifier|private
class|class
name|DeleteTask
implements|implements
name|Callable
argument_list|<
name|Void
argument_list|>
block|{
DECL|field|id
specifier|private
specifier|final
name|Change
operator|.
name|Id
name|id
decl_stmt|;
DECL|method|DeleteTask (Change.Id id)
specifier|private
name|DeleteTask
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
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
name|IOException
block|{
name|logger
operator|.
name|atFine
argument_list|()
operator|.
name|log
argument_list|(
literal|"Delete change %d from index."
argument_list|,
name|id
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
comment|// Don't bother setting a RequestContext to provide the DB.
comment|// Implementations should not need to access the DB in order to delete a
comment|// change ID.
for|for
control|(
name|ChangeIndex
name|i
range|:
name|getWriteIndexes
argument_list|()
control|)
block|{
try|try
init|(
name|TraceTimer
name|traceTimer
init|=
name|TraceContext
operator|.
name|newTimer
argument_list|(
literal|"Deleteing change %d in index version %d"
argument_list|,
name|id
operator|.
name|get
argument_list|()
argument_list|,
name|i
operator|.
name|getSchema
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
init|)
block|{
name|i
operator|.
name|delete
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
name|fireChangeDeletedFromIndexEvent
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|class|ReindexIfStaleTask
specifier|private
class|class
name|ReindexIfStaleTask
extends|extends
name|AbstractIndexTask
argument_list|<
name|Boolean
argument_list|>
block|{
DECL|method|ReindexIfStaleTask (Project.NameKey project, Change.Id id)
specifier|private
name|ReindexIfStaleTask
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|Change
operator|.
name|Id
name|id
parameter_list|)
block|{
name|super
argument_list|(
name|project
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|callImpl (Provider<ReviewDb> db)
specifier|public
name|Boolean
name|callImpl
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
if|if
condition|(
name|stalenessChecker
operator|.
name|isStale
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|indexImpl
argument_list|(
name|changeDataFactory
operator|.
name|create
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|project
argument_list|,
name|id
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isCausedByRepositoryNotFoundException
argument_list|(
name|e
argument_list|)
condition|)
block|{
throw|throw
name|e
throw|;
block|}
name|logger
operator|.
name|atFine
argument_list|()
operator|.
name|log
argument_list|(
literal|"Change %s belongs to deleted project %s, aborting reindexing the change."
argument_list|,
name|id
operator|.
name|get
argument_list|()
argument_list|,
name|project
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"reindex-if-stale-change-"
operator|+
name|id
return|;
block|}
block|}
DECL|method|isCausedByRepositoryNotFoundException (Throwable throwable)
specifier|private
name|boolean
name|isCausedByRepositoryNotFoundException
parameter_list|(
name|Throwable
name|throwable
parameter_list|)
block|{
while|while
condition|(
name|throwable
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|throwable
operator|instanceof
name|RepositoryNotFoundException
condition|)
block|{
return|return
literal|true
return|;
block|}
name|throwable
operator|=
name|throwable
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

