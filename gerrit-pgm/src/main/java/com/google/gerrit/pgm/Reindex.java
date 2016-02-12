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
DECL|package|com.google.gerrit.pgm
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
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
name|schema
operator|.
name|DataSourceProvider
operator|.
name|Context
operator|.
name|MULTI_USER
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
name|Sets
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
name|Die
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
name|LifecycleManager
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
name|lucene
operator|.
name|LuceneIndexModule
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
name|pgm
operator|.
name|util
operator|.
name|BatchProgramModule
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
name|pgm
operator|.
name|util
operator|.
name|SiteProgram
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
name|pgm
operator|.
name|util
operator|.
name|ThreadLimiter
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
name|ScanningChangeCacheImpl
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
name|ChangeIndex
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
name|ChangeSchemas
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
name|IndexCollection
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
name|IndexModule
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
name|IndexModule
operator|.
name|IndexType
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
name|Module
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
name|kohsuke
operator|.
name|args4j
operator|.
name|Option
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

begin_class
DECL|class|Reindex
specifier|public
class|class
name|Reindex
extends|extends
name|SiteProgram
block|{
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--threads"
argument_list|,
name|usage
operator|=
literal|"Number of threads to use for indexing"
argument_list|)
DECL|field|threads
specifier|private
name|int
name|threads
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|availableProcessors
argument_list|()
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--schema-version"
argument_list|,
name|usage
operator|=
literal|"Schema version to reindex; default is most recent version"
argument_list|)
DECL|field|version
specifier|private
name|Integer
name|version
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--output"
argument_list|,
name|usage
operator|=
literal|"Prefix for output; path for local disk index, or prefix for remote index"
argument_list|)
DECL|field|outputBase
specifier|private
name|String
name|outputBase
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--verbose"
argument_list|,
name|usage
operator|=
literal|"Output debug information for each change"
argument_list|)
DECL|field|verbose
specifier|private
name|boolean
name|verbose
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--dry-run"
argument_list|,
name|usage
operator|=
literal|"Dry run: don't write anything to index"
argument_list|)
DECL|field|dryRun
specifier|private
name|boolean
name|dryRun
decl_stmt|;
DECL|field|dbInjector
specifier|private
name|Injector
name|dbInjector
decl_stmt|;
DECL|field|sysInjector
specifier|private
name|Injector
name|sysInjector
decl_stmt|;
DECL|field|globalConfig
specifier|private
name|Config
name|globalConfig
decl_stmt|;
DECL|field|index
specifier|private
name|ChangeIndex
name|index
decl_stmt|;
DECL|field|projectCache
specifier|private
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|repoManager
specifier|private
name|GitRepositoryManager
name|repoManager
decl_stmt|;
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|int
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|mustHaveValidSite
argument_list|()
expr_stmt|;
name|dbInjector
operator|=
name|createDbInjector
argument_list|(
name|MULTI_USER
argument_list|)
expr_stmt|;
name|globalConfig
operator|=
name|dbInjector
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
expr_stmt|;
name|threads
operator|=
name|ThreadLimiter
operator|.
name|limitThreads
argument_list|(
name|dbInjector
argument_list|,
name|threads
argument_list|)
expr_stmt|;
name|checkNotSlaveMode
argument_list|()
expr_stmt|;
name|disableLuceneAutomaticCommit
argument_list|()
expr_stmt|;
name|disableChangeCache
argument_list|()
expr_stmt|;
if|if
condition|(
name|version
operator|==
literal|null
condition|)
block|{
name|version
operator|=
name|ChangeSchemas
operator|.
name|getLatest
argument_list|()
operator|.
name|getVersion
argument_list|()
expr_stmt|;
block|}
name|LifecycleManager
name|dbManager
init|=
operator|new
name|LifecycleManager
argument_list|()
decl_stmt|;
name|dbManager
operator|.
name|add
argument_list|(
name|dbInjector
argument_list|)
expr_stmt|;
name|dbManager
operator|.
name|start
argument_list|()
expr_stmt|;
name|sysInjector
operator|=
name|createSysInjector
argument_list|()
expr_stmt|;
name|LifecycleManager
name|sysManager
init|=
operator|new
name|LifecycleManager
argument_list|()
decl_stmt|;
name|sysManager
operator|.
name|add
argument_list|(
name|sysInjector
argument_list|)
expr_stmt|;
name|sysManager
operator|.
name|start
argument_list|()
expr_stmt|;
name|projectCache
operator|=
name|sysInjector
operator|.
name|getInstance
argument_list|(
name|ProjectCache
operator|.
name|class
argument_list|)
expr_stmt|;
name|repoManager
operator|=
name|sysInjector
operator|.
name|getInstance
argument_list|(
name|GitRepositoryManager
operator|.
name|class
argument_list|)
expr_stmt|;
name|index
operator|=
name|sysInjector
operator|.
name|getInstance
argument_list|(
name|IndexCollection
operator|.
name|class
argument_list|)
operator|.
name|getSearchIndex
argument_list|()
expr_stmt|;
name|int
name|result
init|=
literal|0
decl_stmt|;
try|try
block|{
name|index
operator|.
name|markReady
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|index
operator|.
name|deleteAll
argument_list|()
expr_stmt|;
name|result
operator|=
name|indexAll
argument_list|()
expr_stmt|;
name|index
operator|.
name|markReady
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|die
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|sysManager
operator|.
name|stop
argument_list|()
expr_stmt|;
name|dbManager
operator|.
name|stop
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|checkNotSlaveMode ()
specifier|private
name|void
name|checkNotSlaveMode
parameter_list|()
throws|throws
name|Die
block|{
if|if
condition|(
name|globalConfig
operator|.
name|getBoolean
argument_list|(
literal|"container"
argument_list|,
literal|"slave"
argument_list|,
literal|false
argument_list|)
condition|)
block|{
throw|throw
name|die
argument_list|(
literal|"Cannot run reindex in slave mode"
argument_list|)
throw|;
block|}
block|}
DECL|method|createSysInjector ()
specifier|private
name|Injector
name|createSysInjector
parameter_list|()
block|{
name|List
argument_list|<
name|Module
argument_list|>
name|modules
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|Module
name|changeIndexModule
decl_stmt|;
switch|switch
condition|(
name|IndexModule
operator|.
name|getIndexType
argument_list|(
name|dbInjector
argument_list|)
condition|)
block|{
case|case
name|LUCENE
case|:
name|changeIndexModule
operator|=
operator|new
name|LuceneIndexModule
argument_list|(
name|version
argument_list|,
name|threads
argument_list|,
name|outputBase
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"unsupported index.type"
argument_list|)
throw|;
block|}
name|modules
operator|.
name|add
argument_list|(
name|changeIndexModule
argument_list|)
expr_stmt|;
comment|// Scan changes from git instead of relying on the secondary index, as we
comment|// will have just deleted the old (possibly corrupt) index.
name|modules
operator|.
name|add
argument_list|(
name|ScanningChangeCacheImpl
operator|.
name|module
argument_list|()
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
name|dbInjector
operator|.
name|getInstance
argument_list|(
name|BatchProgramModule
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|dbInjector
operator|.
name|createChildInjector
argument_list|(
name|modules
argument_list|)
return|;
block|}
DECL|method|disableLuceneAutomaticCommit ()
specifier|private
name|void
name|disableLuceneAutomaticCommit
parameter_list|()
block|{
if|if
condition|(
name|IndexModule
operator|.
name|getIndexType
argument_list|(
name|dbInjector
argument_list|)
operator|==
name|IndexType
operator|.
name|LUCENE
condition|)
block|{
name|globalConfig
operator|.
name|setLong
argument_list|(
literal|"index"
argument_list|,
literal|"changes_open"
argument_list|,
literal|"commitWithin"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|globalConfig
operator|.
name|setLong
argument_list|(
literal|"index"
argument_list|,
literal|"changes_closed"
argument_list|,
literal|"commitWithin"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|disableChangeCache ()
specifier|private
name|void
name|disableChangeCache
parameter_list|()
block|{
name|globalConfig
operator|.
name|setLong
argument_list|(
literal|"cache"
argument_list|,
literal|"changes"
argument_list|,
literal|"maximumWeight"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|indexAll ()
specifier|private
name|int
name|indexAll
parameter_list|()
throws|throws
name|Exception
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
name|start
argument_list|(
literal|1
argument_list|)
expr_stmt|;
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
name|Set
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|projects
init|=
name|Sets
operator|.
name|newTreeSet
argument_list|()
decl_stmt|;
name|int
name|changeCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Project
operator|.
name|NameKey
name|project
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
name|project
argument_list|)
init|)
block|{
name|changeCount
operator|+=
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
expr_stmt|;
block|}
name|projects
operator|.
name|add
argument_list|(
name|project
argument_list|)
expr_stmt|;
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
name|SiteIndexer
name|batchIndexer
init|=
name|sysInjector
operator|.
name|getInstance
argument_list|(
name|SiteIndexer
operator|.
name|class
argument_list|)
decl_stmt|;
name|SiteIndexer
operator|.
name|Result
name|result
init|=
name|batchIndexer
operator|.
name|setNumChanges
argument_list|(
name|changeCount
argument_list|)
operator|.
name|setProgressOut
argument_list|(
name|System
operator|.
name|err
argument_list|)
operator|.
name|setVerboseOut
argument_list|(
name|verbose
condition|?
name|System
operator|.
name|out
else|:
name|NullOutputStream
operator|.
name|INSTANCE
argument_list|)
operator|.
name|indexAll
argument_list|(
name|index
argument_list|,
name|projects
argument_list|)
decl_stmt|;
name|int
name|n
init|=
name|result
operator|.
name|doneCount
argument_list|()
operator|+
name|result
operator|.
name|failedCount
argument_list|()
decl_stmt|;
name|double
name|t
init|=
name|result
operator|.
name|elapsed
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|/
literal|1000d
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|format
argument_list|(
literal|"Reindexed %d changes in %.01fs (%.01f/s)\n"
argument_list|,
name|n
argument_list|,
name|t
argument_list|,
name|n
operator|/
name|t
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|success
argument_list|()
condition|?
literal|0
else|:
literal|1
return|;
block|}
block|}
end_class

end_unit

