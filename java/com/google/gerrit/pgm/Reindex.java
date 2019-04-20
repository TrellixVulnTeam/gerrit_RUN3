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
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toSet
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
name|elasticsearch
operator|.
name|ElasticIndexModule
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
name|config
operator|.
name|FactoryModule
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
name|server
operator|.
name|change
operator|.
name|ChangeResource
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
name|plugins
operator|.
name|PluginGuiceEnvironment
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
name|Module
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
name|HashMap
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
literal|"--changes-schema-version"
argument_list|,
name|usage
operator|=
literal|"Schema version to reindex, for changes; default is most recent version"
argument_list|)
DECL|field|changesVersion
specifier|private
name|Integer
name|changesVersion
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
literal|"--list"
argument_list|,
name|usage
operator|=
literal|"List supported indices and exit"
argument_list|)
DECL|field|list
specifier|private
name|boolean
name|list
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--index"
argument_list|,
name|usage
operator|=
literal|"Only reindex specified indices"
argument_list|)
DECL|field|indices
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|indices
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
DECL|field|cfgInjector
specifier|private
name|Injector
name|cfgInjector
decl_stmt|;
DECL|field|globalConfig
specifier|private
name|Config
name|globalConfig
decl_stmt|;
DECL|field|indexDefs
annotation|@
name|Inject
specifier|private
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
name|indexDefs
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
argument_list|()
expr_stmt|;
name|cfgInjector
operator|=
name|dbInjector
operator|.
name|createChildInjector
argument_list|()
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
name|overrideConfig
argument_list|()
expr_stmt|;
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
name|sysInjector
operator|.
name|getInstance
argument_list|(
name|PluginGuiceEnvironment
operator|.
name|class
argument_list|)
operator|.
name|setDbCfgInjector
argument_list|(
name|dbInjector
argument_list|,
name|cfgInjector
argument_list|)
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
name|sysInjector
operator|.
name|injectMembers
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|checkIndicesOption
argument_list|()
expr_stmt|;
try|try
block|{
name|boolean
name|ok
init|=
name|list
condition|?
name|list
argument_list|()
else|:
name|reindex
argument_list|()
decl_stmt|;
return|return
name|ok
condition|?
literal|0
else|:
literal|1
return|;
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
finally|finally
block|{
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
block|}
block|}
DECL|method|list ()
specifier|private
name|boolean
name|list
parameter_list|()
block|{
for|for
control|(
name|IndexDefinition
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|>
name|def
range|:
name|indexDefs
control|)
block|{
name|System
operator|.
name|out
operator|.
name|format
argument_list|(
literal|"%s\n"
argument_list|,
name|def
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|reindex ()
specifier|private
name|boolean
name|reindex
parameter_list|()
block|{
name|boolean
name|ok
init|=
literal|true
decl_stmt|;
for|for
control|(
name|IndexDefinition
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|>
name|def
range|:
name|indexDefs
control|)
block|{
if|if
condition|(
name|indices
operator|.
name|isEmpty
argument_list|()
operator|||
name|indices
operator|.
name|contains
argument_list|(
name|def
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|ok
operator|&=
name|reindex
argument_list|(
name|def
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ok
return|;
block|}
DECL|method|checkIndicesOption ()
specifier|private
name|void
name|checkIndicesOption
parameter_list|()
throws|throws
name|Die
block|{
if|if
condition|(
name|indices
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|requireNonNull
argument_list|(
name|indexDefs
argument_list|,
literal|"Called this method before injectMembers?"
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|valid
init|=
name|indexDefs
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|IndexDefinition
operator|::
name|getName
argument_list|)
operator|.
name|sorted
argument_list|()
operator|.
name|collect
argument_list|(
name|toSet
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|invalid
init|=
name|Sets
operator|.
name|difference
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|indices
argument_list|)
argument_list|,
name|valid
argument_list|)
decl_stmt|;
if|if
condition|(
name|invalid
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
throw|throw
name|die
argument_list|(
literal|"invalid index name(s): "
operator|+
operator|new
name|TreeSet
argument_list|<>
argument_list|(
name|invalid
argument_list|)
operator|+
literal|" available indices are: "
operator|+
name|valid
argument_list|)
throw|;
block|}
DECL|method|createSysInjector ()
specifier|private
name|Injector
name|createSysInjector
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|versions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|changesVersion
operator|!=
literal|null
condition|)
block|{
name|versions
operator|.
name|put
argument_list|(
name|ChangeSchemaDefinitions
operator|.
name|INSTANCE
operator|.
name|getName
argument_list|()
argument_list|,
name|changesVersion
argument_list|)
expr_stmt|;
block|}
name|boolean
name|slave
init|=
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
decl_stmt|;
name|List
argument_list|<
name|Module
argument_list|>
name|modules
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Module
name|indexModule
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
name|indexModule
operator|=
name|LuceneIndexModule
operator|.
name|singleVersionWithExplicitVersions
argument_list|(
name|versions
argument_list|,
name|threads
argument_list|,
name|slave
argument_list|)
expr_stmt|;
break|break;
case|case
name|ELASTICSEARCH
case|:
name|indexModule
operator|=
name|ElasticIndexModule
operator|.
name|singleVersionWithExplicitVersions
argument_list|(
name|versions
argument_list|,
name|threads
argument_list|,
name|slave
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
name|indexModule
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|BatchProgramModule
argument_list|()
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|FactoryModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|factory
argument_list|(
name|ChangeResource
operator|.
name|Factory
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
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
DECL|method|overrideConfig ()
specifier|private
name|void
name|overrideConfig
parameter_list|()
block|{
comment|// Disable auto-commit for speed; committing will happen at the end of the process.
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
comment|// Disable change cache.
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
comment|// Disable auto-reindexing if stale, since there are no concurrent writes to race with.
name|globalConfig
operator|.
name|setBoolean
argument_list|(
literal|"index"
argument_list|,
literal|null
argument_list|,
literal|"autoReindexIfStale"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|reindex (IndexDefinition<K, V, I> def)
specifier|private
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|,
name|I
extends|extends
name|Index
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
parameter_list|>
name|boolean
name|reindex
parameter_list|(
name|IndexDefinition
argument_list|<
name|K
argument_list|,
name|V
argument_list|,
name|I
argument_list|>
name|def
parameter_list|)
block|{
name|I
name|index
init|=
name|def
operator|.
name|getIndexCollection
argument_list|()
operator|.
name|getSearchIndex
argument_list|()
decl_stmt|;
name|requireNonNull
argument_list|(
name|index
argument_list|,
parameter_list|()
lambda|->
name|String
operator|.
name|format
argument_list|(
literal|"no active search index configured for %s"
argument_list|,
name|def
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
name|SiteIndexer
argument_list|<
name|K
argument_list|,
name|V
argument_list|,
name|I
argument_list|>
name|siteIndexer
init|=
name|def
operator|.
name|getSiteIndexer
argument_list|()
decl_stmt|;
name|siteIndexer
operator|.
name|setProgressOut
argument_list|(
name|System
operator|.
name|err
argument_list|)
expr_stmt|;
name|siteIndexer
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
expr_stmt|;
name|SiteIndexer
operator|.
name|Result
name|result
init|=
name|siteIndexer
operator|.
name|indexAll
argument_list|(
name|index
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
literal|"Reindexed %d documents in %s index in %.01fs (%.01f/s)\n"
argument_list|,
name|n
argument_list|,
name|def
operator|.
name|getName
argument_list|()
argument_list|,
name|t
argument_list|,
name|n
operator|/
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|.
name|success
argument_list|()
condition|)
block|{
name|index
operator|.
name|markReady
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|success
argument_list|()
return|;
block|}
block|}
end_class

end_unit

