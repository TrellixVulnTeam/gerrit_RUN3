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
name|client
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
name|client
operator|.
name|reviewdb
operator|.
name|ProjectRight
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
name|client
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
name|SelfPopulatingCache
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
name|WildProjectName
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
name|type
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
name|type
argument_list|,
name|CACHE_NAME
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
DECL|field|projectStateFactory
specifier|private
specifier|final
name|ProjectState
operator|.
name|Factory
name|projectStateFactory
decl_stmt|;
DECL|field|wildProject
specifier|private
specifier|final
name|Project
operator|.
name|NameKey
name|wildProject
decl_stmt|;
DECL|field|inheritedRights
specifier|private
specifier|final
name|ProjectState
operator|.
name|InheritedRights
name|inheritedRights
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
DECL|field|byName
specifier|private
specifier|final
name|SelfPopulatingCache
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|ProjectState
argument_list|>
name|byName
decl_stmt|;
annotation|@
name|Inject
DECL|method|ProjectCacheImpl (final ProjectState.Factory psf, final SchemaFactory<ReviewDb> sf, @WildProjectName final Project.NameKey wp, @Named(CACHE_NAME) final Cache<Project.NameKey, ProjectState> byName)
name|ProjectCacheImpl
parameter_list|(
specifier|final
name|ProjectState
operator|.
name|Factory
name|psf
parameter_list|,
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|sf
parameter_list|,
annotation|@
name|WildProjectName
specifier|final
name|Project
operator|.
name|NameKey
name|wp
parameter_list|,
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
name|wildProject
operator|=
name|wp
expr_stmt|;
name|this
operator|.
name|byName
operator|=
operator|new
name|SelfPopulatingCache
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|ProjectState
argument_list|>
argument_list|(
name|byName
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|ProjectState
name|createEntry
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|key
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|lookup
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
expr_stmt|;
name|this
operator|.
name|inheritedRights
operator|=
operator|new
name|ProjectState
operator|.
name|InheritedRights
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|ProjectRight
argument_list|>
name|get
parameter_list|()
block|{
return|return
name|ProjectCacheImpl
operator|.
name|this
operator|.
name|get
argument_list|(
name|wildProject
argument_list|)
operator|.
name|getLocalRights
argument_list|()
return|;
block|}
block|}
expr_stmt|;
block|}
DECL|method|lookup (final Project.NameKey key)
specifier|private
name|ProjectState
name|lookup
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|key
parameter_list|)
throws|throws
name|OrmException
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
specifier|final
name|Project
name|p
init|=
name|db
operator|.
name|projects
argument_list|()
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|Collection
argument_list|<
name|ProjectRight
argument_list|>
name|rights
init|=
name|Collections
operator|.
name|unmodifiableCollection
argument_list|(
name|db
operator|.
name|projectRights
argument_list|()
operator|.
name|byProject
argument_list|(
name|p
operator|.
name|getNameKey
argument_list|()
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
argument_list|,
name|inheritedRights
argument_list|)
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
block|}
end_class

end_unit

