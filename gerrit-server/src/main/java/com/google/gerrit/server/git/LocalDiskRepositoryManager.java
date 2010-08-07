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
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|lifecycle
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
name|inject
operator|.
name|AbstractModule
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
name|storage
operator|.
name|file
operator|.
name|WindowCache
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
name|AbstractModule
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
name|install
argument_list|(
operator|new
name|LifecycleModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
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
DECL|field|cfg
specifier|private
specifier|final
name|Config
name|cfg
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
name|cfg
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
specifier|final
name|WindowCacheConfig
name|c
init|=
operator|new
name|WindowCacheConfig
argument_list|()
decl_stmt|;
name|c
operator|.
name|fromConfig
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
name|WindowCache
operator|.
name|reconfigure
argument_list|(
name|c
argument_list|)
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
name|File
name|basePath
decl_stmt|;
annotation|@
name|Inject
DECL|method|LocalDiskRepositoryManager (final SitePaths site, @GerritServerConfig final Config cfg)
name|LocalDiskRepositoryManager
parameter_list|(
specifier|final
name|SitePaths
name|site
parameter_list|,
annotation|@
name|GerritServerConfig
specifier|final
name|Config
name|cfg
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
block|}
comment|/** @return base directory under which all projects are stored. */
DECL|method|getBasePath ()
specifier|public
name|File
name|getBasePath
parameter_list|()
block|{
return|return
name|basePath
return|;
block|}
DECL|method|gitDirOf (Project.NameKey name)
specifier|private
name|File
name|gitDirOf
parameter_list|(
name|Project
operator|.
name|NameKey
name|name
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|getBasePath
argument_list|()
argument_list|,
name|name
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
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
try|try
block|{
specifier|final
name|FileKey
name|loc
init|=
name|FileKey
operator|.
name|lenient
argument_list|(
name|gitDirOf
argument_list|(
name|name
argument_list|)
argument_list|,
name|FS
operator|.
name|DETECTED
argument_list|)
decl_stmt|;
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
DECL|method|createRepository (final Project.NameKey name)
specifier|public
name|Repository
name|createRepository
parameter_list|(
specifier|final
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
try|try
block|{
name|File
name|dir
init|=
name|FileKey
operator|.
name|resolve
argument_list|(
name|gitDirOf
argument_list|(
name|name
argument_list|)
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
decl_stmt|;
if|if
condition|(
operator|!
name|n
operator|.
name|endsWith
argument_list|(
name|Constants
operator|.
name|DOT_GIT_EXT
argument_list|)
condition|)
block|{
name|n
operator|=
name|n
operator|+
name|Constants
operator|.
name|DOT_GIT_EXT
expr_stmt|;
block|}
name|loc
operator|=
name|FileKey
operator|.
name|exact
argument_list|(
operator|new
name|File
argument_list|(
name|basePath
argument_list|,
name|n
argument_list|)
argument_list|,
name|FS
operator|.
name|DETECTED
argument_list|)
expr_stmt|;
block|}
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
specifier|final
name|Repository
name|e
init|=
name|openRepository
argument_list|(
name|name
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|getProjectDescription
argument_list|(
name|e
argument_list|)
return|;
block|}
finally|finally
block|{
name|e
operator|.
name|close
argument_list|()
expr_stmt|;
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
block|{
specifier|final
name|Repository
name|e
init|=
name|openRepository
argument_list|(
name|name
argument_list|)
decl_stmt|;
try|try
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
finally|finally
block|{
name|e
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryNotFoundException
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
if|if
condition|(
name|name
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|true
return|;
comment|// no empty paths
if|if
condition|(
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
condition|)
return|return
literal|true
return|;
comment|// no suffix
if|if
condition|(
name|name
operator|.
name|indexOf
argument_list|(
literal|'\\'
argument_list|)
operator|>=
literal|0
condition|)
return|return
literal|true
return|;
comment|// no windows/dos stlye paths
if|if
condition|(
name|name
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'/'
condition|)
return|return
literal|true
return|;
comment|// no absolute paths
if|if
condition|(
operator|new
name|File
argument_list|(
name|name
argument_list|)
operator|.
name|isAbsolute
argument_list|()
condition|)
return|return
literal|true
return|;
comment|// no absolute paths
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"../"
argument_list|)
condition|)
return|return
literal|true
return|;
comment|// no "l../etc/passwd"
if|if
condition|(
name|name
operator|.
name|contains
argument_list|(
literal|"/../"
argument_list|)
condition|)
return|return
literal|true
return|;
comment|// no "foo/../etc/passwd"
if|if
condition|(
name|name
operator|.
name|contains
argument_list|(
literal|"/./"
argument_list|)
condition|)
return|return
literal|true
return|;
comment|// "foo/./foo" is insane to ask
if|if
condition|(
name|name
operator|.
name|contains
argument_list|(
literal|"//"
argument_list|)
condition|)
return|return
literal|true
return|;
comment|// windows UNC path can be "//..."
return|return
literal|false
return|;
comment|// is a reasonable name
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
name|SortedSet
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|names
init|=
operator|new
name|TreeSet
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
argument_list|()
decl_stmt|;
name|scanProjects
argument_list|(
name|basePath
argument_list|,
literal|""
argument_list|,
name|names
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|unmodifiableSortedSet
argument_list|(
name|names
argument_list|)
return|;
block|}
DECL|method|scanProjects (final File dir, final String prefix, final SortedSet<Project.NameKey> names)
specifier|private
name|void
name|scanProjects
parameter_list|(
specifier|final
name|File
name|dir
parameter_list|,
specifier|final
name|String
name|prefix
parameter_list|,
specifier|final
name|SortedSet
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|names
parameter_list|)
block|{
specifier|final
name|File
index|[]
name|ls
init|=
name|dir
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|ls
operator|==
literal|null
condition|)
block|{
return|return;
block|}
for|for
control|(
name|File
name|f
range|:
name|ls
control|)
block|{
name|String
name|fileName
init|=
name|f
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|FileKey
operator|.
name|isGitRepository
argument_list|(
name|f
argument_list|,
name|FS
operator|.
name|DETECTED
argument_list|)
condition|)
block|{
name|String
name|projectName
decl_stmt|;
if|if
condition|(
name|fileName
operator|.
name|equals
argument_list|(
name|Constants
operator|.
name|DOT_GIT
argument_list|)
condition|)
block|{
name|projectName
operator|=
name|prefix
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|prefix
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fileName
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
name|fileName
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
name|prefix
operator|+
name|fileName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|newLen
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|projectName
operator|=
name|prefix
operator|+
name|fileName
expr_stmt|;
block|}
name|Project
operator|.
name|NameKey
name|nameKey
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|projectName
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
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|names
operator|.
name|add
argument_list|(
name|nameKey
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|f
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|scanProjects
argument_list|(
name|f
argument_list|,
name|prefix
operator|+
name|f
operator|.
name|getName
argument_list|()
operator|+
literal|"/"
argument_list|,
name|names
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

