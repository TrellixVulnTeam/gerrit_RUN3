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
DECL|package|com.google.gerrit.server.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|project
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
name|cache
operator|.
name|CacheLoader
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
name|cache
operator|.
name|LoadingCache
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
name|reviewdb
operator|.
name|client
operator|.
name|AccountGroup
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
name|cache
operator|.
name|CacheModule
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
name|AllProjectsName
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
name|AllUsersName
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
name|ProjectConfig
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|TypeLiteral
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
name|name
operator|.
name|Named
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
name|Repository
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
name|Collections
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
name|NoSuchElementException
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
name|SortedSet
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

begin_import
import|import
name|javax
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_comment
comment|/** Cache of project information, including access rights. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|ProjectCacheImpl
specifier|public
class|class
name|ProjectCacheImpl
implements|implements
name|ProjectCache
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
name|ProjectCacheImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CACHE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|CACHE_NAME
init|=
literal|"projects"
decl_stmt|;
DECL|field|CACHE_LIST
specifier|private
specifier|static
specifier|final
name|String
name|CACHE_LIST
init|=
literal|"project_list"
decl_stmt|;
DECL|method|module ()
specifier|public
specifier|static
name|Module
name|module
parameter_list|()
block|{
return|return
operator|new
name|CacheModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|cache
argument_list|(
name|CACHE_NAME
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|ProjectState
operator|.
name|class
argument_list|)
operator|.
name|loader
argument_list|(
name|Loader
operator|.
name|class
argument_list|)
expr_stmt|;
name|cache
argument_list|(
name|CACHE_LIST
argument_list|,
name|ListKey
operator|.
name|class
argument_list|,
operator|new
name|TypeLiteral
argument_list|<
name|SortedSet
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
argument_list|>
argument_list|()
block|{}
argument_list|)
operator|.
name|maximumWeight
argument_list|(
literal|1
argument_list|)
operator|.
name|loader
argument_list|(
name|Lister
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ProjectCacheImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|ProjectCache
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|ProjectCacheImpl
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|field|allProjectsName
specifier|private
specifier|final
name|AllProjectsName
name|allProjectsName
decl_stmt|;
DECL|field|allUsersName
specifier|private
specifier|final
name|AllUsersName
name|allUsersName
decl_stmt|;
DECL|field|byName
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|String
argument_list|,
name|ProjectState
argument_list|>
name|byName
decl_stmt|;
DECL|field|list
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|ListKey
argument_list|,
name|SortedSet
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
argument_list|>
name|list
decl_stmt|;
DECL|field|listLock
specifier|private
specifier|final
name|Lock
name|listLock
decl_stmt|;
DECL|field|clock
specifier|private
specifier|final
name|ProjectCacheClock
name|clock
decl_stmt|;
annotation|@
name|Inject
DECL|method|ProjectCacheImpl ( final AllProjectsName allProjectsName, final AllUsersName allUsersName, @Named(CACHE_NAME) LoadingCache<String, ProjectState> byName, @Named(CACHE_LIST) LoadingCache<ListKey, SortedSet<Project.NameKey>> list, ProjectCacheClock clock)
name|ProjectCacheImpl
parameter_list|(
specifier|final
name|AllProjectsName
name|allProjectsName
parameter_list|,
specifier|final
name|AllUsersName
name|allUsersName
parameter_list|,
annotation|@
name|Named
argument_list|(
name|CACHE_NAME
argument_list|)
name|LoadingCache
argument_list|<
name|String
argument_list|,
name|ProjectState
argument_list|>
name|byName
parameter_list|,
annotation|@
name|Named
argument_list|(
name|CACHE_LIST
argument_list|)
name|LoadingCache
argument_list|<
name|ListKey
argument_list|,
name|SortedSet
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
argument_list|>
name|list
parameter_list|,
name|ProjectCacheClock
name|clock
parameter_list|)
block|{
name|this
operator|.
name|allProjectsName
operator|=
name|allProjectsName
expr_stmt|;
name|this
operator|.
name|allUsersName
operator|=
name|allUsersName
expr_stmt|;
name|this
operator|.
name|byName
operator|=
name|byName
expr_stmt|;
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
name|this
operator|.
name|listLock
operator|=
operator|new
name|ReentrantLock
argument_list|(
literal|true
comment|/* fair */
argument_list|)
expr_stmt|;
name|this
operator|.
name|clock
operator|=
name|clock
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAllProjects ()
specifier|public
name|ProjectState
name|getAllProjects
parameter_list|()
block|{
name|ProjectState
name|state
init|=
name|get
argument_list|(
name|allProjectsName
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
comment|// This should never occur, the server must have this
comment|// project to process anything.
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Missing project "
operator|+
name|allProjectsName
argument_list|)
throw|;
block|}
return|return
name|state
return|;
block|}
annotation|@
name|Override
DECL|method|getAllUsers ()
specifier|public
name|ProjectState
name|getAllUsers
parameter_list|()
block|{
name|ProjectState
name|state
init|=
name|get
argument_list|(
name|allUsersName
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
comment|// This should never occur.
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Missing project "
operator|+
name|allUsersName
argument_list|)
throw|;
block|}
return|return
name|state
return|;
block|}
annotation|@
name|Override
DECL|method|get (final Project.NameKey projectName)
specifier|public
name|ProjectState
name|get
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|)
block|{
try|try
block|{
return|return
name|checkedGet
argument_list|(
name|projectName
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|checkedGet (Project.NameKey projectName)
specifier|public
name|ProjectState
name|checkedGet
parameter_list|(
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|projectName
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
name|ProjectState
name|state
init|=
name|byName
operator|.
name|get
argument_list|(
name|projectName
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|!=
literal|null
operator|&&
name|state
operator|.
name|needsRefresh
argument_list|(
name|clock
operator|.
name|read
argument_list|()
argument_list|)
condition|)
block|{
name|byName
operator|.
name|invalidate
argument_list|(
name|projectName
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|state
operator|=
name|byName
operator|.
name|get
argument_list|(
name|projectName
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|state
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|RepositoryNotFoundException
operator|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Cannot read project %s"
argument_list|,
name|projectName
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|Throwables
operator|.
name|propagateIfInstanceOf
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|,
name|IOException
operator|.
name|class
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|evict (final Project p)
specifier|public
name|void
name|evict
parameter_list|(
specifier|final
name|Project
name|p
parameter_list|)
block|{
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|byName
operator|.
name|invalidate
argument_list|(
name|p
operator|.
name|getNameKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Invalidate the cached information about the given project. */
DECL|method|evict (final Project.NameKey p)
specifier|public
name|void
name|evict
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|p
parameter_list|)
block|{
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|byName
operator|.
name|invalidate
argument_list|(
name|p
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|remove (final Project p)
specifier|public
name|void
name|remove
parameter_list|(
specifier|final
name|Project
name|p
parameter_list|)
block|{
name|listLock
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
name|Sets
operator|.
name|newTreeSet
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|ListKey
operator|.
name|ALL
argument_list|)
argument_list|)
decl_stmt|;
name|n
operator|.
name|remove
argument_list|(
name|p
operator|.
name|getNameKey
argument_list|()
argument_list|)
expr_stmt|;
name|list
operator|.
name|put
argument_list|(
name|ListKey
operator|.
name|ALL
argument_list|,
name|Collections
operator|.
name|unmodifiableSortedSet
argument_list|(
name|n
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
name|warn
argument_list|(
literal|"Cannot list avaliable projects"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|listLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|evict
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onCreateProject (Project.NameKey newProjectName)
specifier|public
name|void
name|onCreateProject
parameter_list|(
name|Project
operator|.
name|NameKey
name|newProjectName
parameter_list|)
block|{
name|listLock
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
name|Sets
operator|.
name|newTreeSet
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|ListKey
operator|.
name|ALL
argument_list|)
argument_list|)
decl_stmt|;
name|n
operator|.
name|add
argument_list|(
name|newProjectName
argument_list|)
expr_stmt|;
name|list
operator|.
name|put
argument_list|(
name|ListKey
operator|.
name|ALL
argument_list|,
name|Collections
operator|.
name|unmodifiableSortedSet
argument_list|(
name|n
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
name|warn
argument_list|(
literal|"Cannot list avaliable projects"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|listLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|all ()
specifier|public
name|Iterable
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|all
parameter_list|()
block|{
try|try
block|{
return|return
name|list
operator|.
name|get
argument_list|(
name|ListKey
operator|.
name|ALL
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot list available projects"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|guessRelevantGroupUUIDs ()
specifier|public
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|guessRelevantGroupUUIDs
parameter_list|()
block|{
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|groups
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|Project
operator|.
name|NameKey
name|n
range|:
name|all
argument_list|()
control|)
block|{
name|ProjectState
name|p
init|=
name|byName
operator|.
name|getIfPresent
argument_list|(
name|n
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|groups
operator|.
name|addAll
argument_list|(
name|p
operator|.
name|getConfig
argument_list|()
operator|.
name|getAllGroupUUIDs
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|groups
return|;
block|}
annotation|@
name|Override
DECL|method|byName (final String pfx)
specifier|public
name|Iterable
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|byName
parameter_list|(
specifier|final
name|String
name|pfx
parameter_list|)
block|{
specifier|final
name|Iterable
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|src
decl_stmt|;
try|try
block|{
name|src
operator|=
name|list
operator|.
name|get
argument_list|(
name|ListKey
operator|.
name|ALL
argument_list|)
operator|.
name|tailSet
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|pfx
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
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
return|return
operator|new
name|Iterable
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
argument_list|()
block|{
specifier|private
name|Iterator
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|itr
init|=
name|src
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|private
name|Project
operator|.
name|NameKey
name|next
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
name|itr
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Project
operator|.
name|NameKey
name|r
init|=
name|itr
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|get
argument_list|()
operator|.
name|startsWith
argument_list|(
name|pfx
argument_list|)
condition|)
block|{
name|next
operator|=
name|r
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|itr
operator|=
name|Collections
operator|.
expr|<
name|Project
operator|.
name|NameKey
operator|>
name|emptyList
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Project
operator|.
name|NameKey
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|Project
operator|.
name|NameKey
name|r
init|=
name|next
decl_stmt|;
name|next
operator|=
literal|null
expr_stmt|;
return|return
name|r
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
DECL|class|Loader
specifier|static
class|class
name|Loader
extends|extends
name|CacheLoader
argument_list|<
name|String
argument_list|,
name|ProjectState
argument_list|>
block|{
DECL|field|projectStateFactory
specifier|private
specifier|final
name|ProjectState
operator|.
name|Factory
name|projectStateFactory
decl_stmt|;
DECL|field|mgr
specifier|private
specifier|final
name|GitRepositoryManager
name|mgr
decl_stmt|;
annotation|@
name|Inject
DECL|method|Loader (ProjectState.Factory psf, GitRepositoryManager g)
name|Loader
parameter_list|(
name|ProjectState
operator|.
name|Factory
name|psf
parameter_list|,
name|GitRepositoryManager
name|g
parameter_list|)
block|{
name|projectStateFactory
operator|=
name|psf
expr_stmt|;
name|mgr
operator|=
name|g
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load (String projectName)
specifier|public
name|ProjectState
name|load
parameter_list|(
name|String
name|projectName
parameter_list|)
throws|throws
name|Exception
block|{
name|Project
operator|.
name|NameKey
name|key
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|projectName
argument_list|)
decl_stmt|;
name|Repository
name|git
init|=
name|mgr
operator|.
name|openRepository
argument_list|(
name|key
argument_list|)
decl_stmt|;
try|try
block|{
name|ProjectConfig
name|cfg
init|=
operator|new
name|ProjectConfig
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|cfg
operator|.
name|load
argument_list|(
name|git
argument_list|)
expr_stmt|;
return|return
name|projectStateFactory
operator|.
name|create
argument_list|(
name|cfg
argument_list|)
return|;
block|}
finally|finally
block|{
name|git
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|class|ListKey
specifier|static
class|class
name|ListKey
block|{
DECL|field|ALL
specifier|static
specifier|final
name|ListKey
name|ALL
init|=
operator|new
name|ListKey
argument_list|()
decl_stmt|;
DECL|method|ListKey ()
specifier|private
name|ListKey
parameter_list|()
block|{     }
block|}
DECL|class|Lister
specifier|static
class|class
name|Lister
extends|extends
name|CacheLoader
argument_list|<
name|ListKey
argument_list|,
name|SortedSet
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
argument_list|>
block|{
DECL|field|mgr
specifier|private
specifier|final
name|GitRepositoryManager
name|mgr
decl_stmt|;
annotation|@
name|Inject
DECL|method|Lister (GitRepositoryManager mgr)
name|Lister
parameter_list|(
name|GitRepositoryManager
name|mgr
parameter_list|)
block|{
name|this
operator|.
name|mgr
operator|=
name|mgr
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load (ListKey key)
specifier|public
name|SortedSet
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|load
parameter_list|(
name|ListKey
name|key
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|mgr
operator|.
name|list
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

