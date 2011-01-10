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
name|reviewdb
operator|.
name|RefRight
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
name|cache
operator|.
name|Cache
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
name|cache
operator|.
name|EntryCreator
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
name|gwtorm
operator|.
name|client
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
specifier|final
name|TypeLiteral
argument_list|<
name|Cache
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|ProjectState
argument_list|>
argument_list|>
name|nameType
init|=
operator|new
name|TypeLiteral
argument_list|<
name|Cache
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|ProjectState
argument_list|>
argument_list|>
argument_list|()
block|{}
decl_stmt|;
name|core
argument_list|(
name|nameType
argument_list|,
name|CACHE_NAME
argument_list|)
operator|.
name|populateWith
argument_list|(
name|Loader
operator|.
name|class
argument_list|)
expr_stmt|;
specifier|final
name|TypeLiteral
argument_list|<
name|Cache
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
argument_list|>
name|listType
init|=
operator|new
name|TypeLiteral
argument_list|<
name|Cache
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
argument_list|>
argument_list|()
block|{}
decl_stmt|;
name|core
argument_list|(
name|listType
argument_list|,
name|CACHE_LIST
argument_list|)
operator|.
name|populateWith
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
DECL|field|byName
specifier|private
specifier|final
name|Cache
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|ProjectState
argument_list|>
name|byName
decl_stmt|;
DECL|field|list
specifier|private
specifier|final
name|Cache
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
annotation|@
name|Inject
DECL|method|ProjectCacheImpl ( @amedCACHE_NAME) final Cache<Project.NameKey, ProjectState> byName, @Named(CACHE_LIST) final Cache<ListKey, SortedSet<Project.NameKey>> list)
name|ProjectCacheImpl
parameter_list|(
annotation|@
name|Named
argument_list|(
name|CACHE_NAME
argument_list|)
specifier|final
name|Cache
argument_list|<
name|Project
operator|.
name|NameKey
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
specifier|final
name|Cache
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
parameter_list|)
block|{
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
block|}
comment|/**    * Get the cached data for a project by its unique name.    *    * @param projectName name of the project.    * @return the cached data; null if no such project exists.    */
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
return|return
name|byName
operator|.
name|get
argument_list|(
name|projectName
argument_list|)
return|;
block|}
comment|/** Invalidate the cached information about the given project. */
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
name|remove
argument_list|(
name|p
operator|.
name|getNameKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Invalidate the cached information about all projects. */
DECL|method|evictAll ()
specifier|public
name|void
name|evictAll
parameter_list|()
block|{
name|byName
operator|.
name|removeAll
argument_list|()
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
name|list
operator|.
name|get
argument_list|(
name|ListKey
operator|.
name|ALL
argument_list|)
decl_stmt|;
name|n
operator|=
operator|new
name|TreeSet
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
argument_list|(
name|n
argument_list|)
expr_stmt|;
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
name|Project
operator|.
name|NameKey
name|next
decl_stmt|;
specifier|private
name|Iterator
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|itr
init|=
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
operator|.
name|iterator
argument_list|()
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
name|EntryCreator
argument_list|<
name|Project
operator|.
name|NameKey
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
DECL|field|schema
specifier|private
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
decl_stmt|;
DECL|field|mgr
specifier|private
specifier|final
name|GitRepositoryManager
name|mgr
decl_stmt|;
annotation|@
name|Inject
DECL|method|Loader (ProjectState.Factory psf, SchemaFactory<ReviewDb> sf, GitRepositoryManager g)
name|Loader
parameter_list|(
name|ProjectState
operator|.
name|Factory
name|psf
parameter_list|,
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|sf
parameter_list|,
name|GitRepositoryManager
name|g
parameter_list|)
block|{
name|projectStateFactory
operator|=
name|psf
expr_stmt|;
name|schema
operator|=
name|sf
expr_stmt|;
name|mgr
operator|=
name|g
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createEntry (Project.NameKey key)
specifier|public
name|ProjectState
name|createEntry
parameter_list|(
name|Project
operator|.
name|NameKey
name|key
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|ReviewDb
name|db
init|=
name|schema
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
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
specifier|final
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
specifier|final
name|Project
name|p
init|=
name|cfg
operator|.
name|getProject
argument_list|()
decl_stmt|;
specifier|final
name|Collection
argument_list|<
name|RefRight
argument_list|>
name|rights
init|=
name|Collections
operator|.
name|unmodifiableCollection
argument_list|(
name|db
operator|.
name|refRights
argument_list|()
operator|.
name|byProject
argument_list|(
name|key
argument_list|)
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|projectStateFactory
operator|.
name|create
argument_list|(
name|p
argument_list|,
name|rights
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
catch|catch
parameter_list|(
name|RepositoryNotFoundException
name|notFound
parameter_list|)
block|{
return|return
literal|null
return|;
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
name|EntryCreator
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
DECL|method|createEntry (ListKey key)
specifier|public
name|SortedSet
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|>
name|createEntry
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

