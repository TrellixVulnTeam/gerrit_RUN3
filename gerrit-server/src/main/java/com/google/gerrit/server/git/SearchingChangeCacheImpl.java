begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
name|auto
operator|.
name|value
operator|.
name|AutoValue
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
name|gerrit
operator|.
name|common
operator|.
name|Nullable
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
name|GitReferenceUpdatedListener
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
name|reviewdb
operator|.
name|client
operator|.
name|Change
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
name|reviewdb
operator|.
name|server
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
name|ReviewerSet
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
name|index
operator|.
name|change
operator|.
name|ChangeField
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
name|gerrit
operator|.
name|server
operator|.
name|query
operator|.
name|change
operator|.
name|InternalChangeQuery
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
name|util
operator|.
name|ManualRequestContext
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
name|util
operator|.
name|OneOffRequestContext
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
name|Provider
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|util
operator|.
name|Providers
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
name|Collections
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
name|concurrent
operator|.
name|ExecutionException
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

begin_class
annotation|@
name|Singleton
DECL|class|SearchingChangeCacheImpl
specifier|public
class|class
name|SearchingChangeCacheImpl
implements|implements
name|GitReferenceUpdatedListener
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
name|SearchingChangeCacheImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|ID_CACHE
specifier|static
specifier|final
name|String
name|ID_CACHE
init|=
literal|"changes"
decl_stmt|;
DECL|class|Module
specifier|public
specifier|static
class|class
name|Module
extends|extends
name|CacheModule
block|{
DECL|field|slave
specifier|private
specifier|final
name|boolean
name|slave
decl_stmt|;
DECL|method|Module ()
specifier|public
name|Module
parameter_list|()
block|{
name|this
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|Module (boolean slave)
specifier|public
name|Module
parameter_list|(
name|boolean
name|slave
parameter_list|)
block|{
name|this
operator|.
name|slave
operator|=
name|slave
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
if|if
condition|(
name|slave
condition|)
block|{
name|bind
argument_list|(
name|SearchingChangeCacheImpl
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|Providers
operator|.
expr|<
name|SearchingChangeCacheImpl
operator|>
name|of
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cache
argument_list|(
name|ID_CACHE
argument_list|,
name|Project
operator|.
name|NameKey
operator|.
name|class
argument_list|,
operator|new
name|TypeLiteral
argument_list|<
name|List
argument_list|<
name|CachedChange
argument_list|>
argument_list|>
argument_list|()
block|{}
argument_list|)
operator|.
name|maximumWeight
argument_list|(
literal|0
argument_list|)
operator|.
name|loader
argument_list|(
name|Loader
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|SearchingChangeCacheImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|DynamicSet
operator|.
name|bind
argument_list|(
name|binder
argument_list|()
argument_list|,
name|GitReferenceUpdatedListener
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|SearchingChangeCacheImpl
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|AutoValue
DECL|class|CachedChange
specifier|abstract
specifier|static
class|class
name|CachedChange
block|{
comment|// Subset of fields in ChangeData, specifically fields needed to serve
comment|// VisibleRefFilter without touching the database. More can be added as
comment|// necessary.
DECL|method|change ()
specifier|abstract
name|Change
name|change
parameter_list|()
function_decl|;
annotation|@
name|Nullable
DECL|method|reviewers ()
specifier|abstract
name|ReviewerSet
name|reviewers
parameter_list|()
function_decl|;
block|}
DECL|field|cache
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|List
argument_list|<
name|CachedChange
argument_list|>
argument_list|>
name|cache
decl_stmt|;
DECL|field|changeDataFactory
specifier|private
specifier|final
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|SearchingChangeCacheImpl ( @amedID_CACHE) LoadingCache<Project.NameKey, List<CachedChange>> cache, ChangeData.Factory changeDataFactory)
name|SearchingChangeCacheImpl
parameter_list|(
annotation|@
name|Named
argument_list|(
name|ID_CACHE
argument_list|)
name|LoadingCache
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|List
argument_list|<
name|CachedChange
argument_list|>
argument_list|>
name|cache
parameter_list|,
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
parameter_list|)
block|{
name|this
operator|.
name|cache
operator|=
name|cache
expr_stmt|;
name|this
operator|.
name|changeDataFactory
operator|=
name|changeDataFactory
expr_stmt|;
block|}
comment|/**    * Read changes for the project from the secondary index.    *    *<p>Returned changes only include the {@code Change} object (with id, branch) and the reviewers.    * Additional stored fields are not loaded from the index.    *    * @param db database handle to populate missing change data (probably unused).    * @param project project to read.    * @return list of known changes; empty if no changes.    */
DECL|method|getChangeData (ReviewDb db, Project.NameKey project)
specifier|public
name|List
argument_list|<
name|ChangeData
argument_list|>
name|getChangeData
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Project
operator|.
name|NameKey
name|project
parameter_list|)
block|{
try|try
block|{
name|List
argument_list|<
name|CachedChange
argument_list|>
name|cached
init|=
name|cache
operator|.
name|get
argument_list|(
name|project
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ChangeData
argument_list|>
name|cds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|cached
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|CachedChange
name|cc
range|:
name|cached
control|)
block|{
name|ChangeData
name|cd
init|=
name|changeDataFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|cc
operator|.
name|change
argument_list|()
argument_list|)
decl_stmt|;
name|cd
operator|.
name|setReviewers
argument_list|(
name|cc
operator|.
name|reviewers
argument_list|()
argument_list|)
expr_stmt|;
name|cds
operator|.
name|add
argument_list|(
name|cd
argument_list|)
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|cds
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
literal|"Cannot fetch changes for "
operator|+
name|project
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
DECL|method|onGitReferenceUpdated (GitReferenceUpdatedListener.Event event)
specifier|public
name|void
name|onGitReferenceUpdated
parameter_list|(
name|GitReferenceUpdatedListener
operator|.
name|Event
name|event
parameter_list|)
block|{
if|if
condition|(
name|event
operator|.
name|getRefName
argument_list|()
operator|.
name|startsWith
argument_list|(
name|RefNames
operator|.
name|REFS_CHANGES
argument_list|)
condition|)
block|{
name|cache
operator|.
name|invalidate
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|event
operator|.
name|getProjectName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Loader
specifier|static
class|class
name|Loader
extends|extends
name|CacheLoader
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|List
argument_list|<
name|CachedChange
argument_list|>
argument_list|>
block|{
DECL|field|requestContext
specifier|private
specifier|final
name|OneOffRequestContext
name|requestContext
decl_stmt|;
DECL|field|queryProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
decl_stmt|;
annotation|@
name|Inject
DECL|method|Loader (OneOffRequestContext requestContext, Provider<InternalChangeQuery> queryProvider)
name|Loader
parameter_list|(
name|OneOffRequestContext
name|requestContext
parameter_list|,
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
parameter_list|)
block|{
name|this
operator|.
name|requestContext
operator|=
name|requestContext
expr_stmt|;
name|this
operator|.
name|queryProvider
operator|=
name|queryProvider
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load (Project.NameKey key)
specifier|public
name|List
argument_list|<
name|CachedChange
argument_list|>
name|load
parameter_list|(
name|Project
operator|.
name|NameKey
name|key
parameter_list|)
throws|throws
name|Exception
block|{
try|try
init|(
name|ManualRequestContext
name|ctx
init|=
name|requestContext
operator|.
name|open
argument_list|()
init|)
block|{
name|List
argument_list|<
name|ChangeData
argument_list|>
name|cds
init|=
name|queryProvider
operator|.
name|get
argument_list|()
operator|.
name|setRequestedFields
argument_list|(
name|ChangeField
operator|.
name|CHANGE
argument_list|,
name|ChangeField
operator|.
name|REVIEWER
argument_list|)
operator|.
name|byProject
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|CachedChange
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|cds
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ChangeData
name|cd
range|:
name|cds
control|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|AutoValue_SearchingChangeCacheImpl_CachedChange
argument_list|(
name|cd
operator|.
name|change
argument_list|()
argument_list|,
name|cd
operator|.
name|getReviewers
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|result
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

