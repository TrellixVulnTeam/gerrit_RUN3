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
name|INTERACTIVE
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
name|FluentIterable
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
name|ImmutableCollection
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
name|extensions
operator|.
name|registration
operator|.
name|DynamicSet
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
name|IndexDefinition
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
name|SchemaDefinitions
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
name|project
operator|.
name|ProjectIndexCollection
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
name|project
operator|.
name|ProjectIndexRewriter
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
name|project
operator|.
name|ProjectIndexer
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
name|project
operator|.
name|ProjectSchemaDefinitions
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
name|git
operator|.
name|WorkQueue
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
name|account
operator|.
name|AccountIndexCollection
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
name|account
operator|.
name|AccountIndexDefinition
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
name|account
operator|.
name|AccountIndexRewriter
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
name|account
operator|.
name|AccountIndexer
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
name|account
operator|.
name|AccountIndexerImpl
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
name|account
operator|.
name|AccountSchemaDefinitions
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
name|change
operator|.
name|ChangeIndexCollection
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
name|change
operator|.
name|ChangeIndexDefinition
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
name|change
operator|.
name|ChangeIndexRewriter
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
name|change
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
name|index
operator|.
name|change
operator|.
name|ChangeSchemaDefinitions
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
name|group
operator|.
name|GroupIndexCollection
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
name|group
operator|.
name|GroupIndexDefinition
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
name|group
operator|.
name|GroupIndexRewriter
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
name|group
operator|.
name|GroupIndexer
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
name|group
operator|.
name|GroupIndexerImpl
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
name|group
operator|.
name|GroupSchemaDefinitions
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
name|project
operator|.
name|ProjectIndexDefinition
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
name|project
operator|.
name|ProjectIndexerImpl
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
name|Injector
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
name|Key
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
name|Provides
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
name|Singleton
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
name|TimeUnit
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
comment|/**  * Module for non-indexer-specific secondary index setup.  *  *<p>This module should not be used directly except by specific secondary indexer implementations  * (e.g. Lucene).  */
end_comment

begin_class
DECL|class|IndexModule
specifier|public
class|class
name|IndexModule
extends|extends
name|LifecycleModule
block|{
DECL|enum|IndexType
specifier|public
enum|enum
name|IndexType
block|{
DECL|enumConstant|LUCENE
name|LUCENE
block|,
DECL|enumConstant|ELASTICSEARCH
name|ELASTICSEARCH
block|}
DECL|field|ALL_SCHEMA_DEFS
specifier|public
specifier|static
specifier|final
name|ImmutableCollection
argument_list|<
name|SchemaDefinitions
argument_list|<
name|?
argument_list|>
argument_list|>
name|ALL_SCHEMA_DEFS
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|AccountSchemaDefinitions
operator|.
name|INSTANCE
argument_list|,
name|ChangeSchemaDefinitions
operator|.
name|INSTANCE
argument_list|,
name|GroupSchemaDefinitions
operator|.
name|INSTANCE
argument_list|,
name|ProjectSchemaDefinitions
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
comment|/** Type of secondary index. */
DECL|method|getIndexType (Injector injector)
specifier|public
specifier|static
name|IndexType
name|getIndexType
parameter_list|(
name|Injector
name|injector
parameter_list|)
block|{
name|Config
name|cfg
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|Key
operator|.
name|get
argument_list|(
name|Config
operator|.
name|class
argument_list|,
name|GerritServerConfig
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|cfg
operator|.
name|getEnum
argument_list|(
literal|"index"
argument_list|,
literal|null
argument_list|,
literal|"type"
argument_list|,
name|IndexType
operator|.
name|LUCENE
argument_list|)
return|;
block|}
DECL|field|threads
specifier|private
specifier|final
name|int
name|threads
decl_stmt|;
DECL|field|interactiveExecutor
specifier|private
specifier|final
name|ListeningExecutorService
name|interactiveExecutor
decl_stmt|;
DECL|field|batchExecutor
specifier|private
specifier|final
name|ListeningExecutorService
name|batchExecutor
decl_stmt|;
DECL|field|closeExecutorsOnShutdown
specifier|private
specifier|final
name|boolean
name|closeExecutorsOnShutdown
decl_stmt|;
DECL|field|slave
specifier|private
specifier|final
name|boolean
name|slave
decl_stmt|;
DECL|method|IndexModule (int threads, boolean slave)
specifier|public
name|IndexModule
parameter_list|(
name|int
name|threads
parameter_list|,
name|boolean
name|slave
parameter_list|)
block|{
name|this
operator|.
name|threads
operator|=
name|threads
expr_stmt|;
name|this
operator|.
name|slave
operator|=
name|slave
expr_stmt|;
name|this
operator|.
name|interactiveExecutor
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|batchExecutor
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|closeExecutorsOnShutdown
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|IndexModule ( ListeningExecutorService interactiveExecutor, ListeningExecutorService batchExecutor)
specifier|public
name|IndexModule
parameter_list|(
name|ListeningExecutorService
name|interactiveExecutor
parameter_list|,
name|ListeningExecutorService
name|batchExecutor
parameter_list|)
block|{
name|this
operator|.
name|threads
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|interactiveExecutor
operator|=
name|interactiveExecutor
expr_stmt|;
name|this
operator|.
name|batchExecutor
operator|=
name|batchExecutor
expr_stmt|;
name|this
operator|.
name|closeExecutorsOnShutdown
operator|=
literal|false
expr_stmt|;
name|slave
operator|=
literal|false
expr_stmt|;
block|}
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
name|AccountIndexRewriter
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|AccountIndexCollection
operator|.
name|class
argument_list|)
expr_stmt|;
name|listener
argument_list|()
operator|.
name|to
argument_list|(
name|AccountIndexCollection
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
argument_list|(
name|AccountIndexerImpl
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ChangeIndexRewriter
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ChangeIndexCollection
operator|.
name|class
argument_list|)
expr_stmt|;
name|listener
argument_list|()
operator|.
name|to
argument_list|(
name|ChangeIndexCollection
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
argument_list|(
name|ChangeIndexer
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|GroupIndexRewriter
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// GroupIndexCollection is already bound very high up in SchemaModule.
name|listener
argument_list|()
operator|.
name|to
argument_list|(
name|GroupIndexCollection
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
argument_list|(
name|GroupIndexerImpl
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ProjectIndexRewriter
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ProjectIndexCollection
operator|.
name|class
argument_list|)
expr_stmt|;
name|listener
argument_list|()
operator|.
name|to
argument_list|(
name|ProjectIndexCollection
operator|.
name|class
argument_list|)
expr_stmt|;
name|factory
argument_list|(
name|ProjectIndexerImpl
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|closeExecutorsOnShutdown
condition|)
block|{
comment|// The executors must be shutdown _before_ closing the indexes.
comment|// On Gerrit start the LifecycleListeners are invoked in the order in which they are
comment|// registered, but on shutdown of Gerrit the order is reversed. This means the
comment|// LifecycleListener to shutdown the executors must be registered _after_ the
comment|// LifecycleListeners that close the indexes. The closing of the indexes is done by
comment|// *IndexCollection which have been registered as LifecycleListener above. The
comment|// registration of the ShutdownIndexExecutors LifecycleListener must happen afterwards.
name|listener
argument_list|()
operator|.
name|to
argument_list|(
name|ShutdownIndexExecutors
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|DynamicSet
operator|.
name|setOf
argument_list|(
name|binder
argument_list|()
argument_list|,
name|OnlineUpgradeListener
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Provides
DECL|method|getIndexDefinitions ( AccountIndexDefinition accounts, ChangeIndexDefinition changes, GroupIndexDefinition groups, ProjectIndexDefinition projects)
name|Collection
argument_list|<
name|IndexDefinition
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|getIndexDefinitions
parameter_list|(
name|AccountIndexDefinition
name|accounts
parameter_list|,
name|ChangeIndexDefinition
name|changes
parameter_list|,
name|GroupIndexDefinition
name|groups
parameter_list|,
name|ProjectIndexDefinition
name|projects
parameter_list|)
block|{
if|if
condition|(
name|slave
condition|)
block|{
comment|// In slave mode, we only have the group index.
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|groups
argument_list|)
return|;
block|}
name|Collection
argument_list|<
name|IndexDefinition
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|>
argument_list|>
name|result
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|accounts
argument_list|,
name|groups
argument_list|,
name|changes
argument_list|,
name|projects
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|FluentIterable
operator|.
name|from
argument_list|(
name|ALL_SCHEMA_DEFS
argument_list|)
operator|.
name|transform
argument_list|(
name|SchemaDefinitions
operator|::
name|getName
argument_list|)
operator|.
name|toSet
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|actual
init|=
name|FluentIterable
operator|.
name|from
argument_list|(
name|result
argument_list|)
operator|.
name|transform
argument_list|(
name|IndexDefinition
operator|::
name|getName
argument_list|)
operator|.
name|toSet
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|expected
operator|.
name|equals
argument_list|(
name|actual
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ProvisionException
argument_list|(
literal|"need index definitions for all schemas: "
operator|+
name|expected
operator|+
literal|" != "
operator|+
name|actual
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Provides
annotation|@
name|Singleton
DECL|method|getAccountIndexer ( AccountIndexerImpl.Factory factory, AccountIndexCollection indexes)
name|AccountIndexer
name|getAccountIndexer
parameter_list|(
name|AccountIndexerImpl
operator|.
name|Factory
name|factory
parameter_list|,
name|AccountIndexCollection
name|indexes
parameter_list|)
block|{
return|return
name|factory
operator|.
name|create
argument_list|(
name|indexes
argument_list|)
return|;
block|}
annotation|@
name|Provides
annotation|@
name|Singleton
DECL|method|getChangeIndexer ( @ndexExecutorINTERACTIVE) ListeningExecutorService executor, ChangeIndexer.Factory factory, ChangeIndexCollection indexes)
name|ChangeIndexer
name|getChangeIndexer
parameter_list|(
annotation|@
name|IndexExecutor
argument_list|(
name|INTERACTIVE
argument_list|)
name|ListeningExecutorService
name|executor
parameter_list|,
name|ChangeIndexer
operator|.
name|Factory
name|factory
parameter_list|,
name|ChangeIndexCollection
name|indexes
parameter_list|)
block|{
comment|// Bind default indexer to interactive executor; callers who need a
comment|// different executor can use the factory directly.
return|return
name|factory
operator|.
name|create
argument_list|(
name|executor
argument_list|,
name|indexes
argument_list|)
return|;
block|}
annotation|@
name|Provides
annotation|@
name|Singleton
DECL|method|getGroupIndexer (GroupIndexerImpl.Factory factory, GroupIndexCollection indexes)
name|GroupIndexer
name|getGroupIndexer
parameter_list|(
name|GroupIndexerImpl
operator|.
name|Factory
name|factory
parameter_list|,
name|GroupIndexCollection
name|indexes
parameter_list|)
block|{
return|return
name|factory
operator|.
name|create
argument_list|(
name|indexes
argument_list|)
return|;
block|}
annotation|@
name|Provides
annotation|@
name|Singleton
DECL|method|getProjectIndexer ( ProjectIndexerImpl.Factory factory, ProjectIndexCollection indexes)
name|ProjectIndexer
name|getProjectIndexer
parameter_list|(
name|ProjectIndexerImpl
operator|.
name|Factory
name|factory
parameter_list|,
name|ProjectIndexCollection
name|indexes
parameter_list|)
block|{
return|return
name|factory
operator|.
name|create
argument_list|(
name|indexes
argument_list|)
return|;
block|}
annotation|@
name|Provides
annotation|@
name|Singleton
annotation|@
name|IndexExecutor
argument_list|(
name|INTERACTIVE
argument_list|)
DECL|method|getInteractiveIndexExecutor ( @erritServerConfig Config config, WorkQueue workQueue)
name|ListeningExecutorService
name|getInteractiveIndexExecutor
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|config
parameter_list|,
name|WorkQueue
name|workQueue
parameter_list|)
block|{
if|if
condition|(
name|interactiveExecutor
operator|!=
literal|null
condition|)
block|{
return|return
name|interactiveExecutor
return|;
block|}
name|int
name|threads
init|=
name|this
operator|.
name|threads
decl_stmt|;
if|if
condition|(
name|threads
operator|<
literal|0
condition|)
block|{
return|return
name|MoreExecutors
operator|.
name|newDirectExecutorService
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|threads
operator|==
literal|0
condition|)
block|{
name|threads
operator|=
name|config
operator|.
name|getInt
argument_list|(
literal|"index"
argument_list|,
literal|null
argument_list|,
literal|"threads"
argument_list|,
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|availableProcessors
argument_list|()
operator|/
literal|2
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|MoreExecutors
operator|.
name|listeningDecorator
argument_list|(
name|workQueue
operator|.
name|createQueue
argument_list|(
name|threads
argument_list|,
literal|"Index-Interactive"
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Provides
annotation|@
name|Singleton
annotation|@
name|IndexExecutor
argument_list|(
name|BATCH
argument_list|)
DECL|method|getBatchIndexExecutor ( @erritServerConfig Config config, WorkQueue workQueue)
name|ListeningExecutorService
name|getBatchIndexExecutor
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|config
parameter_list|,
name|WorkQueue
name|workQueue
parameter_list|)
block|{
if|if
condition|(
name|batchExecutor
operator|!=
literal|null
condition|)
block|{
return|return
name|batchExecutor
return|;
block|}
name|int
name|threads
init|=
name|config
operator|.
name|getInt
argument_list|(
literal|"index"
argument_list|,
literal|null
argument_list|,
literal|"batchThreads"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|threads
operator|<
literal|0
condition|)
block|{
return|return
name|MoreExecutors
operator|.
name|newDirectExecutorService
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|threads
operator|==
literal|0
condition|)
block|{
name|threads
operator|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|availableProcessors
argument_list|()
expr_stmt|;
block|}
return|return
name|MoreExecutors
operator|.
name|listeningDecorator
argument_list|(
name|workQueue
operator|.
name|createQueue
argument_list|(
name|threads
argument_list|,
literal|"Index-Batch"
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Singleton
DECL|class|ShutdownIndexExecutors
specifier|private
specifier|static
class|class
name|ShutdownIndexExecutors
implements|implements
name|LifecycleListener
block|{
DECL|field|interactiveExecutor
specifier|private
specifier|final
name|ListeningExecutorService
name|interactiveExecutor
decl_stmt|;
DECL|field|batchExecutor
specifier|private
specifier|final
name|ListeningExecutorService
name|batchExecutor
decl_stmt|;
annotation|@
name|Inject
DECL|method|ShutdownIndexExecutors ( @ndexExecutorINTERACTIVE) ListeningExecutorService interactiveExecutor, @IndexExecutor(BATCH) ListeningExecutorService batchExecutor)
name|ShutdownIndexExecutors
parameter_list|(
annotation|@
name|IndexExecutor
argument_list|(
name|INTERACTIVE
argument_list|)
name|ListeningExecutorService
name|interactiveExecutor
parameter_list|,
annotation|@
name|IndexExecutor
argument_list|(
name|BATCH
argument_list|)
name|ListeningExecutorService
name|batchExecutor
parameter_list|)
block|{
name|this
operator|.
name|interactiveExecutor
operator|=
name|interactiveExecutor
expr_stmt|;
name|this
operator|.
name|batchExecutor
operator|=
name|batchExecutor
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
name|MoreExecutors
operator|.
name|shutdownAndAwaitTermination
argument_list|(
name|interactiveExecutor
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|MoreExecutors
operator|.
name|shutdownAndAwaitTermination
argument_list|(
name|batchExecutor
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

