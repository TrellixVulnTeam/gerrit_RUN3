begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|MoreObjects
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
name|notedb
operator|.
name|NotesMigration
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
name|internal
operator|.
name|storage
operator|.
name|file
operator|.
name|LockFile
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
name|ConfigConstants
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
name|RepositoryCache
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
name|RepositoryCache
operator|.
name|FileKey
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
name|StoredConfig
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
name|WindowCacheConfig
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
name|eclipse
operator|.
name|jgit
operator|.
name|util
operator|.
name|IO
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
name|RawParseUtils
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|FileVisitOption
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
name|FileVisitResult
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
name|nio
operator|.
name|file
operator|.
name|SimpleFileVisitor
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
name|attribute
operator|.
name|BasicFileAttributes
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
name|EnumSet
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
name|locks
operator|.
name|Lock
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
name|locks
operator|.
name|ReentrantLock
import|;
end_import

begin_comment
comment|/** Manages Git repositories stored on the local filesystem. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|LocalDiskRepositoryManager
specifier|public
class|class
name|LocalDiskRepositoryManager
implements|implements
name|GitRepositoryManager
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
name|LocalDiskRepositoryManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|UNNAMED
specifier|private
specifier|static
specifier|final
name|String
name|UNNAMED
init|=
literal|"Unnamed repository; edit this file to name it for gitweb."
decl_stmt|;
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
name|GitRepositoryManager
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|LocalDiskRepositoryManager
operator|.
name|class
argument_list|)
expr_stmt|;
name|listener
argument_list|()
operator|.
name|to
argument_list|(
name|LocalDiskRepositoryManager
operator|.
name|Lifecycle
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Lifecycle
specifier|public
specifier|static
class|class
name|Lifecycle
implements|implements
name|LifecycleListener
block|{
DECL|field|serverConfig
specifier|private
specifier|final
name|Config
name|serverConfig
decl_stmt|;
annotation|@
name|Inject
DECL|method|Lifecycle (@erritServerConfig final Config cfg)
name|Lifecycle
parameter_list|(
annotation|@
name|GerritServerConfig
specifier|final
name|Config
name|cfg
parameter_list|)
block|{
name|this
operator|.
name|serverConfig
operator|=
name|cfg
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
name|WindowCacheConfig
name|cfg
init|=
operator|new
name|WindowCacheConfig
argument_list|()
decl_stmt|;
name|cfg
operator|.
name|fromConfig
argument_list|(
name|serverConfig
argument_list|)
expr_stmt|;
if|if
condition|(
name|serverConfig
operator|.
name|getString
argument_list|(
literal|"core"
argument_list|,
literal|null
argument_list|,
literal|"streamFileThreshold"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|long
name|mx
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|maxMemory
argument_list|()
decl_stmt|;
name|int
name|limit
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|mx
operator|/
literal|4
argument_list|,
comment|// don't use more than 1/4 of the heap.
literal|2047
operator|<<
literal|20
argument_list|)
decl_stmt|;
comment|// cannot exceed array length
if|if
condition|(
operator|(
literal|5
operator|<<
literal|20
operator|)
operator|<
name|limit
operator|&&
name|limit
operator|%
operator|(
literal|1
operator|<<
literal|20
operator|)
operator|!=
literal|0
condition|)
block|{
comment|// If the limit is at least 5 MiB but is not a whole multiple
comment|// of MiB round up to the next one full megabyte. This is a very
comment|// tiny memory increase in exchange for nice round units.
name|limit
operator|=
operator|(
operator|(
name|limit
operator|/
operator|(
literal|1
operator|<<
literal|20
operator|)
operator|)
operator|+
literal|1
operator|)
operator|<<
literal|20
expr_stmt|;
block|}
name|String
name|desc
decl_stmt|;
if|if
condition|(
name|limit
operator|%
operator|(
literal|1
operator|<<
literal|20
operator|)
operator|==
literal|0
condition|)
block|{
name|desc
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"%dm"
argument_list|,
name|limit
operator|/
operator|(
literal|1
operator|<<
literal|20
operator|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|limit
operator|%
operator|(
literal|1
operator|<<
literal|10
operator|)
operator|==
literal|0
condition|)
block|{
name|desc
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"%dk"
argument_list|,
name|limit
operator|/
operator|(
literal|1
operator|<<
literal|10
operator|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|desc
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"%d"
argument_list|,
name|limit
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Defaulting core.streamFileThreshold to %s"
argument_list|,
name|desc
argument_list|)
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setStreamFileThreshold
argument_list|(
name|limit
argument_list|)
expr_stmt|;
block|}
name|cfg
operator|.
name|install
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{     }
block|}
DECL|field|basePath
specifier|private
specifier|final
name|Path
name|basePath
decl_stmt|;
DECL|field|noteDbPath
specifier|private
specifier|final
name|Path
name|noteDbPath
decl_stmt|;
DECL|field|namesUpdateLock
specifier|private
specifier|final
name|Lock
name|namesUpdateLock
decl_stmt|;
DECL|field|names
specifier|private
specifier|volatile
name|SortedSet
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|names
decl_stmt|;
annotation|@
name|Inject
DECL|method|LocalDiskRepositoryManager (SitePaths site, @GerritServerConfig Config cfg, NotesMigration notesMigration)
name|LocalDiskRepositoryManager
parameter_list|(
name|SitePaths
name|site
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|,
name|NotesMigration
name|notesMigration
parameter_list|)
block|{
name|basePath
operator|=
name|site
operator|.
name|resolve
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"gerrit"
argument_list|,
literal|null
argument_list|,
literal|"basePath"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|basePath
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"gerrit.basePath must be configured"
argument_list|)
throw|;
block|}
if|if
condition|(
name|notesMigration
operator|.
name|enabled
argument_list|()
condition|)
block|{
name|noteDbPath
operator|=
name|site
operator|.
name|resolve
argument_list|(
name|MoreObjects
operator|.
name|firstNonNull
argument_list|(
name|cfg
operator|.
name|getString
argument_list|(
literal|"gerrit"
argument_list|,
literal|null
argument_list|,
literal|"noteDbPath"
argument_list|)
argument_list|,
literal|"notedb"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|noteDbPath
operator|=
literal|null
expr_stmt|;
block|}
name|namesUpdateLock
operator|=
operator|new
name|ReentrantLock
argument_list|(
literal|true
comment|/* fair */
argument_list|)
expr_stmt|;
name|names
operator|=
name|list
argument_list|()
expr_stmt|;
block|}
comment|/** @return base directory under which all projects are stored. */
DECL|method|getBasePath ()
specifier|public
name|Path
name|getBasePath
parameter_list|()
block|{
return|return
name|basePath
return|;
block|}
annotation|@
name|Override
DECL|method|openRepository (Project.NameKey name)
specifier|public
name|Repository
name|openRepository
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|)
throws|throws
name|RepositoryNotFoundException
block|{
return|return
name|openRepository
argument_list|(
name|basePath
argument_list|,
name|name
argument_list|)
return|;
block|}
DECL|method|openRepository (Path path, Project.NameKey name)
specifier|private
name|Repository
name|openRepository
parameter_list|(
name|Path
name|path
parameter_list|,
name|Project
operator|.
name|NameKey
name|name
parameter_list|)
throws|throws
name|RepositoryNotFoundException
block|{
if|if
condition|(
name|isUnreasonableName
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RepositoryNotFoundException
argument_list|(
literal|"Invalid name: "
operator|+
name|name
argument_list|)
throw|;
block|}
name|File
name|gitDir
init|=
name|path
operator|.
name|resolve
argument_list|(
name|name
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|toFile
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|names
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// The this.names list does not hold the project-name but it can still exist
comment|// on disk; for instance when the project has been created directly on the
comment|// file-system through replication.
comment|//
if|if
condition|(
operator|!
name|name
operator|.
name|get
argument_list|()
operator|.
name|endsWith
argument_list|(
name|Constants
operator|.
name|DOT_GIT_EXT
argument_list|)
condition|)
block|{
if|if
condition|(
name|FileKey
operator|.
name|resolve
argument_list|(
name|gitDir
argument_list|,
name|FS
operator|.
name|DETECTED
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|onCreateProject
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RepositoryNotFoundException
argument_list|(
name|gitDir
argument_list|)
throw|;
block|}
block|}
else|else
block|{
specifier|final
name|File
name|directory
init|=
name|gitDir
decl_stmt|;
if|if
condition|(
name|FileKey
operator|.
name|isGitRepository
argument_list|(
operator|new
name|File
argument_list|(
name|directory
argument_list|,
name|Constants
operator|.
name|DOT_GIT
argument_list|)
argument_list|,
name|FS
operator|.
name|DETECTED
argument_list|)
condition|)
block|{
name|onCreateProject
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|FileKey
operator|.
name|isGitRepository
argument_list|(
operator|new
name|File
argument_list|(
name|directory
operator|.
name|getParentFile
argument_list|()
argument_list|,
name|directory
operator|.
name|getName
argument_list|()
operator|+
name|Constants
operator|.
name|DOT_GIT_EXT
argument_list|)
argument_list|,
name|FS
operator|.
name|DETECTED
argument_list|)
condition|)
block|{
name|onCreateProject
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RepositoryNotFoundException
argument_list|(
name|gitDir
argument_list|)
throw|;
block|}
block|}
block|}
specifier|final
name|FileKey
name|loc
init|=
name|FileKey
operator|.
name|lenient
argument_list|(
name|gitDir
argument_list|,
name|FS
operator|.
name|DETECTED
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|RepositoryCache
operator|.
name|open
argument_list|(
name|loc
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
specifier|final
name|RepositoryNotFoundException
name|e2
decl_stmt|;
name|e2
operator|=
operator|new
name|RepositoryNotFoundException
argument_list|(
literal|"Cannot open repository "
operator|+
name|name
argument_list|)
expr_stmt|;
name|e2
operator|.
name|initCause
argument_list|(
name|e1
argument_list|)
expr_stmt|;
throw|throw
name|e2
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|createRepository (Project.NameKey name)
specifier|public
name|Repository
name|createRepository
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|)
throws|throws
name|RepositoryNotFoundException
throws|,
name|RepositoryCaseMismatchException
block|{
name|Repository
name|repo
init|=
name|createRepository
argument_list|(
name|basePath
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|noteDbPath
operator|!=
literal|null
operator|&&
operator|!
name|noteDbPath
operator|.
name|equals
argument_list|(
name|basePath
argument_list|)
condition|)
block|{
name|createRepository
argument_list|(
name|noteDbPath
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|repo
return|;
block|}
DECL|method|createRepository (Path path, Project.NameKey name)
specifier|private
name|Repository
name|createRepository
parameter_list|(
name|Path
name|path
parameter_list|,
name|Project
operator|.
name|NameKey
name|name
parameter_list|)
throws|throws
name|RepositoryNotFoundException
throws|,
name|RepositoryCaseMismatchException
block|{
if|if
condition|(
name|isUnreasonableName
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RepositoryNotFoundException
argument_list|(
literal|"Invalid name: "
operator|+
name|name
argument_list|)
throw|;
block|}
name|File
name|dir
init|=
name|FileKey
operator|.
name|resolve
argument_list|(
name|path
operator|.
name|resolve
argument_list|(
name|name
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|toFile
argument_list|()
argument_list|,
name|FS
operator|.
name|DETECTED
argument_list|)
decl_stmt|;
name|FileKey
name|loc
decl_stmt|;
if|if
condition|(
name|dir
operator|!=
literal|null
condition|)
block|{
comment|// Already exists on disk, use the repository we found.
comment|//
name|loc
operator|=
name|FileKey
operator|.
name|exact
argument_list|(
name|dir
argument_list|,
name|FS
operator|.
name|DETECTED
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|names
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RepositoryCaseMismatchException
argument_list|(
name|name
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// It doesn't exist under any of the standard permutations
comment|// of the repository name, so prefer the standard bare name.
comment|//
name|String
name|n
init|=
name|name
operator|.
name|get
argument_list|()
operator|+
name|Constants
operator|.
name|DOT_GIT_EXT
decl_stmt|;
name|loc
operator|=
name|FileKey
operator|.
name|exact
argument_list|(
name|path
operator|.
name|resolve
argument_list|(
name|n
argument_list|)
operator|.
name|toFile
argument_list|()
argument_list|,
name|FS
operator|.
name|DETECTED
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Repository
name|db
init|=
name|RepositoryCache
operator|.
name|open
argument_list|(
name|loc
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|db
operator|.
name|create
argument_list|(
literal|true
comment|/* bare */
argument_list|)
expr_stmt|;
name|StoredConfig
name|config
init|=
name|db
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|config
operator|.
name|setBoolean
argument_list|(
name|ConfigConstants
operator|.
name|CONFIG_CORE_SECTION
argument_list|,
literal|null
argument_list|,
name|ConfigConstants
operator|.
name|CONFIG_KEY_LOGALLREFUPDATES
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|config
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// JGit only writes to the reflog for refs/meta/config if the log file
comment|// already exists.
comment|//
name|File
name|metaConfigLog
init|=
operator|new
name|File
argument_list|(
name|db
operator|.
name|getDirectory
argument_list|()
argument_list|,
literal|"logs/"
operator|+
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|metaConfigLog
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
operator|||
operator|!
name|metaConfigLog
operator|.
name|createNewFile
argument_list|()
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Failed to create ref log for %s in repository %s"
argument_list|,
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|onCreateProject
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|db
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
specifier|final
name|RepositoryNotFoundException
name|e2
decl_stmt|;
name|e2
operator|=
operator|new
name|RepositoryNotFoundException
argument_list|(
literal|"Cannot create repository "
operator|+
name|name
argument_list|)
expr_stmt|;
name|e2
operator|.
name|initCause
argument_list|(
name|e1
argument_list|)
expr_stmt|;
throw|throw
name|e2
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|openMetadataRepository (Project.NameKey name)
specifier|public
name|Repository
name|openMetadataRepository
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|)
throws|throws
name|RepositoryNotFoundException
throws|,
name|IOException
block|{
name|checkState
argument_list|(
name|noteDbPath
operator|!=
literal|null
argument_list|,
literal|"notedb disabled"
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|openRepository
argument_list|(
name|noteDbPath
argument_list|,
name|name
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryNotFoundException
name|e
parameter_list|)
block|{
return|return
name|createRepository
argument_list|(
name|noteDbPath
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
DECL|method|onCreateProject (final Project.NameKey newProjectName)
specifier|private
name|void
name|onCreateProject
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|newProjectName
parameter_list|)
block|{
name|namesUpdateLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|SortedSet
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|n
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|(
name|names
argument_list|)
decl_stmt|;
name|n
operator|.
name|add
argument_list|(
name|newProjectName
argument_list|)
expr_stmt|;
name|names
operator|=
name|Collections
operator|.
name|unmodifiableSortedSet
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|namesUpdateLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getProjectDescription (final Project.NameKey name)
specifier|public
name|String
name|getProjectDescription
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|name
parameter_list|)
throws|throws
name|RepositoryNotFoundException
throws|,
name|IOException
block|{
try|try
init|(
name|Repository
name|e
init|=
name|openRepository
argument_list|(
name|name
argument_list|)
init|)
block|{
return|return
name|getProjectDescription
argument_list|(
name|e
argument_list|)
return|;
block|}
block|}
DECL|method|getProjectDescription (final Repository e)
specifier|private
name|String
name|getProjectDescription
parameter_list|(
specifier|final
name|Repository
name|e
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|File
name|d
init|=
operator|new
name|File
argument_list|(
name|e
operator|.
name|getDirectory
argument_list|()
argument_list|,
literal|"description"
argument_list|)
decl_stmt|;
name|String
name|description
decl_stmt|;
try|try
block|{
name|description
operator|=
name|RawParseUtils
operator|.
name|decode
argument_list|(
name|IO
operator|.
name|readFully
argument_list|(
name|d
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|err
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|description
operator|!=
literal|null
condition|)
block|{
name|description
operator|=
name|description
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|description
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|description
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|UNNAMED
operator|.
name|equals
argument_list|(
name|description
argument_list|)
condition|)
block|{
name|description
operator|=
literal|null
expr_stmt|;
block|}
block|}
return|return
name|description
return|;
block|}
annotation|@
name|Override
DECL|method|setProjectDescription (final Project.NameKey name, final String description)
specifier|public
name|void
name|setProjectDescription
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|name
parameter_list|,
specifier|final
name|String
name|description
parameter_list|)
block|{
comment|// Update git's description file, in case gitweb is being used
comment|//
try|try
init|(
name|Repository
name|e
init|=
name|openRepository
argument_list|(
name|name
argument_list|)
init|)
block|{
specifier|final
name|String
name|old
init|=
name|getProjectDescription
argument_list|(
name|e
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|old
operator|==
literal|null
operator|&&
name|description
operator|==
literal|null
operator|)
operator|||
operator|(
name|old
operator|!=
literal|null
operator|&&
name|old
operator|.
name|equals
argument_list|(
name|description
argument_list|)
operator|)
condition|)
block|{
return|return;
block|}
specifier|final
name|LockFile
name|f
init|=
operator|new
name|LockFile
argument_list|(
operator|new
name|File
argument_list|(
name|e
operator|.
name|getDirectory
argument_list|()
argument_list|,
literal|"description"
argument_list|)
argument_list|,
name|FS
operator|.
name|DETECTED
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|lock
argument_list|()
condition|)
block|{
name|String
name|d
init|=
name|description
decl_stmt|;
if|if
condition|(
name|d
operator|!=
literal|null
condition|)
block|{
name|d
operator|=
name|d
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|d
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|d
operator|+=
literal|"\n"
expr_stmt|;
block|}
block|}
else|else
block|{
name|d
operator|=
literal|""
expr_stmt|;
block|}
name|f
operator|.
name|write
argument_list|(
name|Constants
operator|.
name|encode
argument_list|(
name|d
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|commit
argument_list|()
expr_stmt|;
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
literal|"Cannot update description for "
operator|+
name|name
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|isUnreasonableName (final Project.NameKey nameKey)
specifier|private
name|boolean
name|isUnreasonableName
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|nameKey
parameter_list|)
block|{
specifier|final
name|String
name|name
init|=
name|nameKey
operator|.
name|get
argument_list|()
decl_stmt|;
return|return
name|name
operator|.
name|length
argument_list|()
operator|==
literal|0
comment|// no empty paths
operator|||
name|name
operator|.
name|charAt
argument_list|(
name|name
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
literal|'/'
comment|// no suffix
operator|||
name|name
operator|.
name|indexOf
argument_list|(
literal|'\\'
argument_list|)
operator|>=
literal|0
comment|// no windows/dos style paths
operator|||
name|name
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'/'
comment|// no absolute paths
operator|||
operator|new
name|File
argument_list|(
name|name
argument_list|)
operator|.
name|isAbsolute
argument_list|()
comment|// no absolute paths
operator|||
name|name
operator|.
name|startsWith
argument_list|(
literal|"../"
argument_list|)
comment|// no "l../etc/passwd"
operator|||
name|name
operator|.
name|contains
argument_list|(
literal|"/../"
argument_list|)
comment|// no "foo/../etc/passwd"
operator|||
name|name
operator|.
name|contains
argument_list|(
literal|"/./"
argument_list|)
comment|// "foo/./foo" is insane to ask
operator|||
name|name
operator|.
name|contains
argument_list|(
literal|"//"
argument_list|)
comment|// windows UNC path can be "//..."
operator|||
name|name
operator|.
name|contains
argument_list|(
literal|".git/"
argument_list|)
comment|// no path segments that end with '.git' as "foo.git/bar"
operator|||
name|name
operator|.
name|contains
argument_list|(
literal|"?"
argument_list|)
comment|// common unix wildcard
operator|||
name|name
operator|.
name|contains
argument_list|(
literal|"%"
argument_list|)
comment|// wildcard or string parameter
operator|||
name|name
operator|.
name|contains
argument_list|(
literal|"*"
argument_list|)
comment|// wildcard
operator|||
name|name
operator|.
name|contains
argument_list|(
literal|":"
argument_list|)
comment|// Could be used for absolute paths in windows?
operator|||
name|name
operator|.
name|contains
argument_list|(
literal|"<"
argument_list|)
comment|// redirect input
operator|||
name|name
operator|.
name|contains
argument_list|(
literal|">"
argument_list|)
comment|// redirect output
operator|||
name|name
operator|.
name|contains
argument_list|(
literal|"|"
argument_list|)
comment|// pipe
operator|||
name|name
operator|.
name|contains
argument_list|(
literal|"$"
argument_list|)
comment|// dollar sign
operator|||
name|name
operator|.
name|contains
argument_list|(
literal|"\r"
argument_list|)
return|;
comment|// carriage return
block|}
annotation|@
name|Override
DECL|method|list ()
specifier|public
name|SortedSet
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|list
parameter_list|()
block|{
comment|// The results of this method are cached by ProjectCacheImpl. Control only
comment|// enters here if the cache was flushed by the administrator to force
comment|// scanning the filesystem. Don't rely on the cached names collection.
name|namesUpdateLock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|ProjectVisitor
name|visitor
init|=
operator|new
name|ProjectVisitor
argument_list|()
decl_stmt|;
try|try
block|{
name|Files
operator|.
name|walkFileTree
argument_list|(
name|basePath
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|FileVisitOption
operator|.
name|FOLLOW_LINKS
argument_list|)
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|visitor
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
literal|"Error walking repository tree "
operator|+
name|basePath
operator|.
name|toAbsolutePath
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|unmodifiableSortedSet
argument_list|(
name|visitor
operator|.
name|found
argument_list|)
return|;
block|}
finally|finally
block|{
name|namesUpdateLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|ProjectVisitor
specifier|private
class|class
name|ProjectVisitor
extends|extends
name|SimpleFileVisitor
argument_list|<
name|Path
argument_list|>
block|{
DECL|field|found
specifier|private
specifier|final
name|SortedSet
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|found
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|preVisitDirectory (Path dir, BasicFileAttributes attrs)
specifier|public
name|FileVisitResult
name|preVisitDirectory
parameter_list|(
name|Path
name|dir
parameter_list|,
name|BasicFileAttributes
name|attrs
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|dir
operator|.
name|equals
argument_list|(
name|basePath
argument_list|)
operator|&&
name|isRepo
argument_list|(
name|dir
argument_list|)
condition|)
block|{
name|addProject
argument_list|(
name|dir
argument_list|)
expr_stmt|;
return|return
name|FileVisitResult
operator|.
name|SKIP_SUBTREE
return|;
block|}
return|return
name|FileVisitResult
operator|.
name|CONTINUE
return|;
block|}
DECL|method|isRepo (Path p)
specifier|private
name|boolean
name|isRepo
parameter_list|(
name|Path
name|p
parameter_list|)
block|{
name|String
name|name
init|=
name|p
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
return|return
operator|!
name|name
operator|.
name|equals
argument_list|(
name|Constants
operator|.
name|DOT_GIT
argument_list|)
operator|&&
name|name
operator|.
name|endsWith
argument_list|(
name|Constants
operator|.
name|DOT_GIT_EXT
argument_list|)
return|;
block|}
DECL|method|addProject (Path p)
specifier|private
name|void
name|addProject
parameter_list|(
name|Path
name|p
parameter_list|)
block|{
name|Project
operator|.
name|NameKey
name|nameKey
init|=
name|getProjectName
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|isUnreasonableName
argument_list|(
name|nameKey
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Ignoring unreasonably named repository "
operator|+
name|p
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|found
operator|.
name|add
argument_list|(
name|nameKey
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getProjectName (Path p)
specifier|private
name|Project
operator|.
name|NameKey
name|getProjectName
parameter_list|(
name|Path
name|p
parameter_list|)
block|{
name|String
name|projectName
init|=
name|basePath
operator|.
name|relativize
argument_list|(
name|p
argument_list|)
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|File
operator|.
name|separatorChar
operator|!=
literal|'/'
condition|)
block|{
name|projectName
operator|=
name|projectName
operator|.
name|replace
argument_list|(
name|File
operator|.
name|separatorChar
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|projectName
operator|.
name|endsWith
argument_list|(
name|Constants
operator|.
name|DOT_GIT_EXT
argument_list|)
condition|)
block|{
name|int
name|newLen
init|=
name|projectName
operator|.
name|length
argument_list|()
operator|-
name|Constants
operator|.
name|DOT_GIT_EXT
operator|.
name|length
argument_list|()
decl_stmt|;
name|projectName
operator|=
name|projectName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|newLen
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|projectName
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

