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
name|SitePath
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
name|lib
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

begin_comment
comment|/** Class managing Git repositories. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|GitRepositoryManager
specifier|public
class|class
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
name|GitRepositoryManager
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
DECL|field|sitePath
specifier|private
specifier|final
name|File
name|sitePath
decl_stmt|;
DECL|field|basepath
specifier|private
specifier|final
name|File
name|basepath
decl_stmt|;
annotation|@
name|Inject
DECL|method|GitRepositoryManager (@itePath final File path, @GerritServerConfig final Config cfg)
name|GitRepositoryManager
parameter_list|(
annotation|@
name|SitePath
specifier|final
name|File
name|path
parameter_list|,
annotation|@
name|GerritServerConfig
specifier|final
name|Config
name|cfg
parameter_list|)
block|{
name|sitePath
operator|=
name|path
expr_stmt|;
specifier|final
name|String
name|basePath
init|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"gerrit"
argument_list|,
literal|null
argument_list|,
literal|"basepath"
argument_list|)
decl_stmt|;
if|if
condition|(
name|basePath
operator|!=
literal|null
condition|)
block|{
name|File
name|root
init|=
operator|new
name|File
argument_list|(
name|basePath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|root
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|root
operator|=
operator|new
name|File
argument_list|(
name|sitePath
argument_list|,
name|basePath
argument_list|)
expr_stmt|;
block|}
name|basepath
operator|=
name|root
expr_stmt|;
block|}
else|else
block|{
name|basepath
operator|=
literal|null
expr_stmt|;
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
name|basepath
return|;
block|}
comment|/**    * Get (or open) a repository by name.    *    * @param name the repository name, relative to the base directory.    * @return the cached Repository instance. Caller must call {@code close()}    *         when done to decrement the resource handle.    * @throws RepositoryNotFoundException the name does not denote an existing    *         repository, or the name cannot be read as a repository.    */
DECL|method|openRepository (String name)
specifier|public
name|Repository
name|openRepository
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryNotFoundException
block|{
if|if
condition|(
name|basepath
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RepositoryNotFoundException
argument_list|(
literal|"No gerrit.basepath configured"
argument_list|)
throw|;
block|}
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
operator|new
name|File
argument_list|(
name|basepath
argument_list|,
name|name
argument_list|)
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
comment|/**    * Create (and open) a repository by name.    *    * @param name the repository name, relative to the base directory.    * @return the cached Repository instance. Caller must call {@code close()}    *         when done to decrement the resource handle.    * @throws RepositoryNotFoundException the name does not denote an existing    *         repository, or the name cannot be read as a repository.    */
DECL|method|createRepository (String name)
specifier|public
name|Repository
name|createRepository
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RepositoryNotFoundException
block|{
if|if
condition|(
name|basepath
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RepositoryNotFoundException
argument_list|(
literal|"No gerrit.basepath configured"
argument_list|)
throw|;
block|}
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
if|if
condition|(
operator|!
name|name
operator|.
name|endsWith
argument_list|(
literal|".git"
argument_list|)
condition|)
block|{
name|name
operator|=
name|name
operator|+
literal|".git"
expr_stmt|;
block|}
specifier|final
name|FileKey
name|loc
init|=
name|FileKey
operator|.
name|exact
argument_list|(
operator|new
name|File
argument_list|(
name|basepath
argument_list|,
name|name
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|RepositoryCache
operator|.
name|open
argument_list|(
name|loc
argument_list|,
literal|false
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
comment|/**    * Read the {@code GIT_DIR/description} file for gitweb.    *<p>    * NB: This code should really be in JGit, as a member of the Repository    * object. Until it moves there, its here.    *    * @param name the repository name, relative to the base directory.    * @return description text; null if no description has been configured.    * @throws RepositoryNotFoundException the named repository does not exist.    * @throws IOException the description file exists, but is not readable by    *         this process.    */
DECL|method|getProjectDescription (final String name)
specifier|public
name|String
name|getProjectDescription
parameter_list|(
specifier|final
name|String
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
comment|/**    * Set the {@code GIT_DIR/description} file for gitweb.    *<p>    * NB: This code should really be in JGit, as a member of the Repository    * object. Until it moves there, its here.    *    * @param name the repository name, relative to the base directory.    * @param description new description text for the repository.    */
DECL|method|setProjectDescription (final String name, final String description)
specifier|public
name|void
name|setProjectDescription
parameter_list|(
specifier|final
name|String
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
decl_stmt|;
specifier|final
name|LockFile
name|f
decl_stmt|;
name|e
operator|=
name|openRepository
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|f
operator|=
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
argument_list|)
expr_stmt|;
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
name|e
operator|.
name|close
argument_list|()
expr_stmt|;
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
DECL|method|isUnreasonableName (final String name)
specifier|private
name|boolean
name|isUnreasonableName
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
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
block|}
end_class

end_unit

