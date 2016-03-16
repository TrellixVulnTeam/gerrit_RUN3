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
DECL|package|com.google.gerrit.lucene
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|lucene
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
name|checkArgument
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
name|Maps
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
name|primitives
operator|.
name|Ints
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
name|config
operator|.
name|SitePaths
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
name|Schema
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
name|storage
operator|.
name|file
operator|.
name|FileBasedConfig
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
name|FS
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
name|nio
operator|.
name|file
operator|.
name|DirectoryStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|LuceneVersionManager
specifier|public
class|class
name|LuceneVersionManager
implements|implements
name|LifecycleListener
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
name|LuceneVersionManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CHANGES_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|CHANGES_PREFIX
init|=
literal|"changes_"
decl_stmt|;
DECL|class|Version
specifier|private
specifier|static
class|class
name|Version
block|{
DECL|field|schema
specifier|private
specifier|final
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|schema
decl_stmt|;
DECL|field|version
specifier|private
specifier|final
name|int
name|version
decl_stmt|;
DECL|field|exists
specifier|private
specifier|final
name|boolean
name|exists
decl_stmt|;
DECL|field|ready
specifier|private
specifier|final
name|boolean
name|ready
decl_stmt|;
DECL|method|Version (Schema<ChangeData> schema, int version, boolean exists, boolean ready)
specifier|private
name|Version
parameter_list|(
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|schema
parameter_list|,
name|int
name|version
parameter_list|,
name|boolean
name|exists
parameter_list|,
name|boolean
name|ready
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|schema
operator|==
literal|null
operator|||
name|schema
operator|.
name|getVersion
argument_list|()
operator|==
name|version
argument_list|)
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|exists
operator|=
name|exists
expr_stmt|;
name|this
operator|.
name|ready
operator|=
name|ready
expr_stmt|;
block|}
block|}
DECL|method|getDir (SitePaths sitePaths, Schema<ChangeData> schema)
specifier|static
name|Path
name|getDir
parameter_list|(
name|SitePaths
name|sitePaths
parameter_list|,
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|schema
parameter_list|)
block|{
return|return
name|sitePaths
operator|.
name|index_dir
operator|.
name|resolve
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s%04d"
argument_list|,
name|CHANGES_PREFIX
argument_list|,
name|schema
operator|.
name|getVersion
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|loadGerritIndexConfig (SitePaths sitePaths)
specifier|static
name|FileBasedConfig
name|loadGerritIndexConfig
parameter_list|(
name|SitePaths
name|sitePaths
parameter_list|)
throws|throws
name|ConfigInvalidException
throws|,
name|IOException
block|{
name|FileBasedConfig
name|cfg
init|=
operator|new
name|FileBasedConfig
argument_list|(
name|sitePaths
operator|.
name|index_dir
operator|.
name|resolve
argument_list|(
literal|"gerrit_index.config"
argument_list|)
operator|.
name|toFile
argument_list|()
argument_list|,
name|FS
operator|.
name|detect
argument_list|()
argument_list|)
decl_stmt|;
name|cfg
operator|.
name|load
argument_list|()
expr_stmt|;
return|return
name|cfg
return|;
block|}
DECL|method|setReady (Config cfg, int version, boolean ready)
specifier|static
name|void
name|setReady
parameter_list|(
name|Config
name|cfg
parameter_list|,
name|int
name|version
parameter_list|,
name|boolean
name|ready
parameter_list|)
block|{
name|cfg
operator|.
name|setBoolean
argument_list|(
literal|"index"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|version
argument_list|)
argument_list|,
literal|"ready"
argument_list|,
name|ready
argument_list|)
expr_stmt|;
block|}
DECL|method|getReady (Config cfg, int version)
specifier|private
specifier|static
name|boolean
name|getReady
parameter_list|(
name|Config
name|cfg
parameter_list|,
name|int
name|version
parameter_list|)
block|{
return|return
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"index"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|version
argument_list|)
argument_list|,
literal|"ready"
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|field|sitePaths
specifier|private
specifier|final
name|SitePaths
name|sitePaths
decl_stmt|;
DECL|field|indexFactory
specifier|private
specifier|final
name|LuceneChangeIndex
operator|.
name|Factory
name|indexFactory
decl_stmt|;
DECL|field|indexes
specifier|private
specifier|final
name|ChangeIndexCollection
name|indexes
decl_stmt|;
DECL|field|reindexerFactory
specifier|private
specifier|final
name|OnlineReindexer
operator|.
name|Factory
name|reindexerFactory
decl_stmt|;
DECL|field|onlineUpgrade
specifier|private
specifier|final
name|boolean
name|onlineUpgrade
decl_stmt|;
DECL|field|reindexer
specifier|private
name|OnlineReindexer
name|reindexer
decl_stmt|;
annotation|@
name|Inject
DECL|method|LuceneVersionManager ( @erritServerConfig Config cfg, SitePaths sitePaths, LuceneChangeIndex.Factory indexFactory, ChangeIndexCollection indexes, OnlineReindexer.Factory reindexerFactory)
name|LuceneVersionManager
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|,
name|SitePaths
name|sitePaths
parameter_list|,
name|LuceneChangeIndex
operator|.
name|Factory
name|indexFactory
parameter_list|,
name|ChangeIndexCollection
name|indexes
parameter_list|,
name|OnlineReindexer
operator|.
name|Factory
name|reindexerFactory
parameter_list|)
block|{
name|this
operator|.
name|sitePaths
operator|=
name|sitePaths
expr_stmt|;
name|this
operator|.
name|indexFactory
operator|=
name|indexFactory
expr_stmt|;
name|this
operator|.
name|indexes
operator|=
name|indexes
expr_stmt|;
name|this
operator|.
name|reindexerFactory
operator|=
name|reindexerFactory
expr_stmt|;
name|this
operator|.
name|onlineUpgrade
operator|=
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"index"
argument_list|,
literal|null
argument_list|,
literal|"onlineUpgrade"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|String
name|runReindex
init|=
literal|"No index versions ready; run java -jar "
operator|+
name|sitePaths
operator|.
name|gerrit_war
operator|.
name|toAbsolutePath
argument_list|()
operator|+
literal|" reindex"
decl_stmt|;
name|FileBasedConfig
name|cfg
decl_stmt|;
try|try
block|{
name|cfg
operator|=
name|loadGerritIndexConfig
argument_list|(
name|sitePaths
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConfigInvalidException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|fail
argument_list|(
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|sitePaths
operator|.
name|index_dir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ProvisionException
argument_list|(
name|runReindex
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|sitePaths
operator|.
name|index_dir
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Not a directory: %s"
argument_list|,
name|sitePaths
operator|.
name|index_dir
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ProvisionException
argument_list|(
name|runReindex
argument_list|)
throw|;
block|}
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|Version
argument_list|>
name|versions
init|=
name|scanVersions
argument_list|(
name|cfg
argument_list|)
decl_stmt|;
comment|// Search from the most recent ready version.
comment|// Write to the most recent ready version and the most recent version.
name|Version
name|search
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|Version
argument_list|>
name|write
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
name|Version
name|v
range|:
name|versions
operator|.
name|descendingMap
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|v
operator|.
name|schema
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|write
operator|.
name|isEmpty
argument_list|()
operator|&&
name|onlineUpgrade
condition|)
block|{
name|write
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|v
operator|.
name|ready
condition|)
block|{
name|search
operator|=
name|v
expr_stmt|;
if|if
condition|(
operator|!
name|write
operator|.
name|contains
argument_list|(
name|v
argument_list|)
condition|)
block|{
name|write
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
if|if
condition|(
name|search
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ProvisionException
argument_list|(
name|runReindex
argument_list|)
throw|;
block|}
name|markNotReady
argument_list|(
name|cfg
argument_list|,
name|versions
operator|.
name|values
argument_list|()
argument_list|,
name|write
argument_list|)
expr_stmt|;
name|LuceneChangeIndex
name|searchIndex
init|=
name|indexFactory
operator|.
name|create
argument_list|(
name|search
operator|.
name|schema
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|indexes
operator|.
name|setSearchIndex
argument_list|(
name|searchIndex
argument_list|)
expr_stmt|;
for|for
control|(
name|Version
name|v
range|:
name|write
control|)
block|{
if|if
condition|(
name|v
operator|.
name|schema
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|v
operator|.
name|version
operator|!=
name|search
operator|.
name|version
condition|)
block|{
name|indexes
operator|.
name|addWriteIndex
argument_list|(
name|indexFactory
operator|.
name|create
argument_list|(
name|v
operator|.
name|schema
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|indexes
operator|.
name|addWriteIndex
argument_list|(
name|searchIndex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|int
name|latest
init|=
name|write
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|version
decl_stmt|;
if|if
condition|(
name|onlineUpgrade
operator|&&
name|latest
operator|!=
name|search
operator|.
name|version
condition|)
block|{
name|reindexer
operator|=
name|reindexerFactory
operator|.
name|create
argument_list|(
name|latest
argument_list|)
expr_stmt|;
name|reindexer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Start the online reindexer if the current index is not already the latest.    *    * @return true if started, otherwise false.    * @throws ReindexerAlreadyRunningException    */
DECL|method|startReindexer ()
specifier|public
specifier|synchronized
name|boolean
name|startReindexer
parameter_list|()
throws|throws
name|ReindexerAlreadyRunningException
block|{
name|validateReindexerNotRunning
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|isCurrentIndexVersionLatest
argument_list|()
condition|)
block|{
name|reindexer
operator|.
name|start
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
comment|/**    * Activate the latest index if the current index is not already the latest.    *    * @return true if index was activate, otherwise false.    * @throws ReindexerAlreadyRunningException    */
DECL|method|activateLatestIndex ()
specifier|public
specifier|synchronized
name|boolean
name|activateLatestIndex
parameter_list|()
throws|throws
name|ReindexerAlreadyRunningException
block|{
name|validateReindexerNotRunning
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|isCurrentIndexVersionLatest
argument_list|()
condition|)
block|{
name|reindexer
operator|.
name|activateIndex
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
DECL|method|isCurrentIndexVersionLatest ()
specifier|private
name|boolean
name|isCurrentIndexVersionLatest
parameter_list|()
block|{
return|return
name|reindexer
operator|==
literal|null
operator|||
name|reindexer
operator|.
name|getVersion
argument_list|()
operator|==
name|indexes
operator|.
name|getSearchIndex
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getVersion
argument_list|()
return|;
block|}
DECL|method|validateReindexerNotRunning ()
specifier|private
name|void
name|validateReindexerNotRunning
parameter_list|()
throws|throws
name|ReindexerAlreadyRunningException
block|{
if|if
condition|(
name|reindexer
operator|!=
literal|null
operator|&&
name|reindexer
operator|.
name|isRunning
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ReindexerAlreadyRunningException
argument_list|()
throw|;
block|}
block|}
DECL|method|scanVersions (Config cfg)
specifier|private
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|Version
argument_list|>
name|scanVersions
parameter_list|(
name|Config
name|cfg
parameter_list|)
block|{
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|Version
argument_list|>
name|versions
init|=
name|Maps
operator|.
name|newTreeMap
argument_list|()
decl_stmt|;
for|for
control|(
name|Schema
argument_list|<
name|ChangeData
argument_list|>
name|schema
range|:
name|ChangeSchemas
operator|.
name|ALL
operator|.
name|values
argument_list|()
control|)
block|{
name|Path
name|p
init|=
name|getDir
argument_list|(
name|sitePaths
argument_list|,
name|schema
argument_list|)
decl_stmt|;
name|boolean
name|isDir
init|=
name|Files
operator|.
name|isDirectory
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|p
argument_list|)
operator|&&
operator|!
name|isDir
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Not a directory: %s"
argument_list|,
name|p
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|v
init|=
name|schema
operator|.
name|getVersion
argument_list|()
decl_stmt|;
name|versions
operator|.
name|put
argument_list|(
name|v
argument_list|,
operator|new
name|Version
argument_list|(
name|schema
argument_list|,
name|v
argument_list|,
name|isDir
argument_list|,
name|getReady
argument_list|(
name|cfg
argument_list|,
name|v
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|DirectoryStream
argument_list|<
name|Path
argument_list|>
name|paths
init|=
name|Files
operator|.
name|newDirectoryStream
argument_list|(
name|sitePaths
operator|.
name|index_dir
argument_list|)
init|)
block|{
for|for
control|(
name|Path
name|p
range|:
name|paths
control|)
block|{
name|String
name|n
init|=
name|p
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|n
operator|.
name|startsWith
argument_list|(
name|CHANGES_PREFIX
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|String
name|versionStr
init|=
name|n
operator|.
name|substring
argument_list|(
name|CHANGES_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|Integer
name|v
init|=
name|Ints
operator|.
name|tryParse
argument_list|(
name|versionStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
operator|||
name|versionStr
operator|.
name|length
argument_list|()
operator|!=
literal|4
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Unrecognized version in index directory: {}"
argument_list|,
name|p
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
operator|!
name|versions
operator|.
name|containsKey
argument_list|(
name|v
argument_list|)
condition|)
block|{
name|versions
operator|.
name|put
argument_list|(
name|v
argument_list|,
operator|new
name|Version
argument_list|(
literal|null
argument_list|,
name|v
argument_list|,
literal|true
argument_list|,
name|getReady
argument_list|(
name|cfg
argument_list|,
name|v
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"Error scanning index directory: "
operator|+
name|sitePaths
operator|.
name|index_dir
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|versions
return|;
block|}
DECL|method|markNotReady (FileBasedConfig cfg, Iterable<Version> versions, Collection<Version> inUse)
specifier|private
name|void
name|markNotReady
parameter_list|(
name|FileBasedConfig
name|cfg
parameter_list|,
name|Iterable
argument_list|<
name|Version
argument_list|>
name|versions
parameter_list|,
name|Collection
argument_list|<
name|Version
argument_list|>
name|inUse
parameter_list|)
block|{
name|boolean
name|dirty
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Version
name|v
range|:
name|versions
control|)
block|{
if|if
condition|(
operator|!
name|inUse
operator|.
name|contains
argument_list|(
name|v
argument_list|)
operator|&&
name|v
operator|.
name|exists
condition|)
block|{
name|setReady
argument_list|(
name|cfg
argument_list|,
name|v
operator|.
name|version
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|dirty
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|dirty
condition|)
block|{
try|try
block|{
name|cfg
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|fail
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|fail (Throwable t)
specifier|private
name|ProvisionException
name|fail
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|ProvisionException
name|e
init|=
operator|new
name|ProvisionException
argument_list|(
literal|"Error scanning indexes"
argument_list|)
decl_stmt|;
name|e
operator|.
name|initCause
argument_list|(
name|t
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
comment|// Do nothing; indexes are closed on demand by IndexCollection.
block|}
block|}
end_class

end_unit

