begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2011 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|account
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
name|ImmutableList
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
name|AccountGroupById
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
name|gwtorm
operator|.
name|server
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
name|server
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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

begin_comment
comment|/** Tracks group inclusions in memory for efficient access. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|GroupIncludeCacheImpl
specifier|public
class|class
name|GroupIncludeCacheImpl
implements|implements
name|GroupIncludeCache
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
name|GroupIncludeCacheImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|PARENT_GROUPS_NAME
specifier|private
specifier|static
specifier|final
name|String
name|PARENT_GROUPS_NAME
init|=
literal|"groups_byinclude"
decl_stmt|;
DECL|field|SUBGROUPS_NAME
specifier|private
specifier|static
specifier|final
name|String
name|SUBGROUPS_NAME
init|=
literal|"groups_members"
decl_stmt|;
DECL|field|EXTERNAL_NAME
specifier|private
specifier|static
specifier|final
name|String
name|EXTERNAL_NAME
init|=
literal|"groups_external"
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
name|PARENT_GROUPS_NAME
argument_list|,
name|AccountGroup
operator|.
name|UUID
operator|.
name|class
argument_list|,
operator|new
name|TypeLiteral
argument_list|<
name|ImmutableList
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
argument_list|>
argument_list|()
block|{}
argument_list|)
operator|.
name|loader
argument_list|(
name|ParentGroupsLoader
operator|.
name|class
argument_list|)
expr_stmt|;
name|cache
argument_list|(
name|SUBGROUPS_NAME
argument_list|,
name|AccountGroup
operator|.
name|UUID
operator|.
name|class
argument_list|,
operator|new
name|TypeLiteral
argument_list|<
name|ImmutableList
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
argument_list|>
argument_list|()
block|{}
argument_list|)
operator|.
name|loader
argument_list|(
name|SubgroupsLoader
operator|.
name|class
argument_list|)
expr_stmt|;
name|cache
argument_list|(
name|EXTERNAL_NAME
argument_list|,
name|String
operator|.
name|class
argument_list|,
operator|new
name|TypeLiteral
argument_list|<
name|ImmutableList
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
argument_list|>
argument_list|()
block|{}
argument_list|)
operator|.
name|loader
argument_list|(
name|AllExternalLoader
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|GroupIncludeCacheImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|GroupIncludeCache
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|GroupIncludeCacheImpl
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|field|subgroups
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|ImmutableList
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
argument_list|>
name|subgroups
decl_stmt|;
DECL|field|parentGroups
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|ImmutableList
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
argument_list|>
name|parentGroups
decl_stmt|;
DECL|field|external
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|String
argument_list|,
name|ImmutableList
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
argument_list|>
name|external
decl_stmt|;
annotation|@
name|Inject
DECL|method|GroupIncludeCacheImpl ( @amedSUBGROUPS_NAME) LoadingCache<AccountGroup.UUID, ImmutableList<AccountGroup.UUID>> subgroups, @Named(PARENT_GROUPS_NAME) LoadingCache<AccountGroup.UUID, ImmutableList<AccountGroup.UUID>> parentGroups, @Named(EXTERNAL_NAME) LoadingCache<String, ImmutableList<AccountGroup.UUID>> external)
name|GroupIncludeCacheImpl
parameter_list|(
annotation|@
name|Named
argument_list|(
name|SUBGROUPS_NAME
argument_list|)
name|LoadingCache
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|ImmutableList
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
argument_list|>
name|subgroups
parameter_list|,
annotation|@
name|Named
argument_list|(
name|PARENT_GROUPS_NAME
argument_list|)
name|LoadingCache
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|ImmutableList
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
argument_list|>
name|parentGroups
parameter_list|,
annotation|@
name|Named
argument_list|(
name|EXTERNAL_NAME
argument_list|)
name|LoadingCache
argument_list|<
name|String
argument_list|,
name|ImmutableList
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
argument_list|>
name|external
parameter_list|)
block|{
name|this
operator|.
name|subgroups
operator|=
name|subgroups
expr_stmt|;
name|this
operator|.
name|parentGroups
operator|=
name|parentGroups
expr_stmt|;
name|this
operator|.
name|external
operator|=
name|external
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|subgroupsOf (AccountGroup.UUID groupId)
specifier|public
name|Collection
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|subgroupsOf
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|groupId
parameter_list|)
block|{
try|try
block|{
return|return
name|subgroups
operator|.
name|get
argument_list|(
name|groupId
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
literal|"Cannot load members of group"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|parentGroupsOf (AccountGroup.UUID groupId)
specifier|public
name|Collection
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|parentGroupsOf
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|groupId
parameter_list|)
block|{
try|try
block|{
return|return
name|parentGroups
operator|.
name|get
argument_list|(
name|groupId
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
literal|"Cannot load included groups"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|evictSubgroupsOf (AccountGroup.UUID groupId)
specifier|public
name|void
name|evictSubgroupsOf
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|groupId
parameter_list|)
block|{
if|if
condition|(
name|groupId
operator|!=
literal|null
condition|)
block|{
name|subgroups
operator|.
name|invalidate
argument_list|(
name|groupId
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|evictParentGroupsOf (AccountGroup.UUID groupId)
specifier|public
name|void
name|evictParentGroupsOf
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|groupId
parameter_list|)
block|{
if|if
condition|(
name|groupId
operator|!=
literal|null
condition|)
block|{
name|parentGroups
operator|.
name|invalidate
argument_list|(
name|groupId
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|AccountGroup
operator|.
name|isInternalGroup
argument_list|(
name|groupId
argument_list|)
condition|)
block|{
name|external
operator|.
name|invalidate
argument_list|(
name|EXTERNAL_NAME
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|allExternalMembers ()
specifier|public
name|Collection
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|allExternalMembers
parameter_list|()
block|{
try|try
block|{
return|return
name|external
operator|.
name|get
argument_list|(
name|EXTERNAL_NAME
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
literal|"Cannot load set of non-internal groups"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
block|}
DECL|class|SubgroupsLoader
specifier|static
class|class
name|SubgroupsLoader
extends|extends
name|CacheLoader
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|ImmutableList
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
argument_list|>
block|{
DECL|field|schema
specifier|private
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
decl_stmt|;
annotation|@
name|Inject
DECL|method|SubgroupsLoader (SchemaFactory<ReviewDb> sf)
name|SubgroupsLoader
parameter_list|(
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|sf
parameter_list|)
block|{
name|schema
operator|=
name|sf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load (AccountGroup.UUID key)
specifier|public
name|ImmutableList
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|load
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|key
parameter_list|)
throws|throws
name|OrmException
block|{
try|try
init|(
name|ReviewDb
name|db
init|=
name|schema
operator|.
name|open
argument_list|()
init|)
block|{
name|List
argument_list|<
name|AccountGroup
argument_list|>
name|group
init|=
name|db
operator|.
name|accountGroups
argument_list|()
operator|.
name|byUUID
argument_list|(
name|key
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
if|if
condition|(
name|group
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|ids
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountGroupById
name|agi
range|:
name|db
operator|.
name|accountGroupById
argument_list|()
operator|.
name|byGroup
argument_list|(
name|group
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
control|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|agi
operator|.
name|getIncludeUUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|ids
argument_list|)
return|;
block|}
block|}
block|}
DECL|class|ParentGroupsLoader
specifier|static
class|class
name|ParentGroupsLoader
extends|extends
name|CacheLoader
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|ImmutableList
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
argument_list|>
block|{
DECL|field|schema
specifier|private
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
decl_stmt|;
annotation|@
name|Inject
DECL|method|ParentGroupsLoader (SchemaFactory<ReviewDb> sf)
name|ParentGroupsLoader
parameter_list|(
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|sf
parameter_list|)
block|{
name|schema
operator|=
name|sf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load (AccountGroup.UUID key)
specifier|public
name|ImmutableList
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|load
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|key
parameter_list|)
throws|throws
name|OrmException
block|{
try|try
init|(
name|ReviewDb
name|db
init|=
name|schema
operator|.
name|open
argument_list|()
init|)
block|{
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|ids
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountGroupById
name|agi
range|:
name|db
operator|.
name|accountGroupById
argument_list|()
operator|.
name|byIncludeUUID
argument_list|(
name|key
argument_list|)
control|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|agi
operator|.
name|getGroupId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|groupArray
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountGroup
name|g
range|:
name|db
operator|.
name|accountGroups
argument_list|()
operator|.
name|get
argument_list|(
name|ids
argument_list|)
control|)
block|{
name|groupArray
operator|.
name|add
argument_list|(
name|g
operator|.
name|getGroupUUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|groupArray
argument_list|)
return|;
block|}
block|}
block|}
DECL|class|AllExternalLoader
specifier|static
class|class
name|AllExternalLoader
extends|extends
name|CacheLoader
argument_list|<
name|String
argument_list|,
name|ImmutableList
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
argument_list|>
block|{
DECL|field|schema
specifier|private
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
decl_stmt|;
annotation|@
name|Inject
DECL|method|AllExternalLoader (SchemaFactory<ReviewDb> sf)
name|AllExternalLoader
parameter_list|(
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|sf
parameter_list|)
block|{
name|schema
operator|=
name|sf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load (String key)
specifier|public
name|ImmutableList
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|load
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|Exception
block|{
try|try
init|(
name|ReviewDb
name|db
init|=
name|schema
operator|.
name|open
argument_list|()
init|)
block|{
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|ids
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountGroupById
name|agi
range|:
name|db
operator|.
name|accountGroupById
argument_list|()
operator|.
name|all
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|AccountGroup
operator|.
name|isInternalGroup
argument_list|(
name|agi
operator|.
name|getIncludeUUID
argument_list|()
argument_list|)
condition|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|agi
operator|.
name|getIncludeUUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|ids
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

