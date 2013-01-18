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
name|ImmutableSet
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
name|AccountGroupIncludeByUuid
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
DECL|field|BYINCLUDE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|BYINCLUDE_NAME
init|=
literal|"groups_byinclude"
decl_stmt|;
DECL|field|MEMBERS_NAME
specifier|private
specifier|static
specifier|final
name|String
name|MEMBERS_NAME
init|=
literal|"groups_members"
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
name|BYINCLUDE_NAME
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
name|Set
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
name|MemberInLoader
operator|.
name|class
argument_list|)
expr_stmt|;
name|cache
argument_list|(
name|MEMBERS_NAME
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
name|Set
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
name|MembersOfLoader
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
DECL|field|membersOf
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
argument_list|>
name|membersOf
decl_stmt|;
DECL|field|memberIn
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
argument_list|>
name|memberIn
decl_stmt|;
annotation|@
name|Inject
DECL|method|GroupIncludeCacheImpl ( @amedMEMBERS_NAME) LoadingCache<AccountGroup.UUID, Set<AccountGroup.UUID>> membersOf, @Named(BYINCLUDE_NAME) LoadingCache<AccountGroup.UUID, Set<AccountGroup.UUID>> memberIn)
name|GroupIncludeCacheImpl
parameter_list|(
annotation|@
name|Named
argument_list|(
name|MEMBERS_NAME
argument_list|)
name|LoadingCache
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
argument_list|>
name|membersOf
parameter_list|,
annotation|@
name|Named
argument_list|(
name|BYINCLUDE_NAME
argument_list|)
name|LoadingCache
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
argument_list|>
name|memberIn
parameter_list|)
block|{
name|this
operator|.
name|membersOf
operator|=
name|membersOf
expr_stmt|;
name|this
operator|.
name|memberIn
operator|=
name|memberIn
expr_stmt|;
block|}
DECL|method|membersOf (AccountGroup.UUID groupId)
specifier|public
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|membersOf
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
name|membersOf
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
DECL|method|memberIn (AccountGroup.UUID groupId)
specifier|public
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|memberIn
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
name|memberIn
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
DECL|method|evictMembersOf (AccountGroup.UUID groupId)
specifier|public
name|void
name|evictMembersOf
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
name|membersOf
operator|.
name|invalidate
argument_list|(
name|groupId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|evictMemberIn (AccountGroup.UUID groupId)
specifier|public
name|void
name|evictMemberIn
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
name|memberIn
operator|.
name|invalidate
argument_list|(
name|groupId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|MembersOfLoader
specifier|static
class|class
name|MembersOfLoader
extends|extends
name|CacheLoader
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|Set
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
DECL|method|MembersOfLoader (final SchemaFactory<ReviewDb> sf)
name|MembersOfLoader
parameter_list|(
specifier|final
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
name|Set
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
name|Collections
operator|.
name|emptySet
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
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountGroupIncludeByUuid
name|agi
range|:
name|db
operator|.
name|accountGroupIncludesByUuid
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
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|ids
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
block|}
DECL|class|MemberInLoader
specifier|static
class|class
name|MemberInLoader
extends|extends
name|CacheLoader
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|Set
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
DECL|method|MemberInLoader (final SchemaFactory<ReviewDb> sf)
name|MemberInLoader
parameter_list|(
specifier|final
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
name|Set
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
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|ids
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountGroupIncludeByUuid
name|agi
range|:
name|db
operator|.
name|accountGroupIncludesByUuid
argument_list|()
operator|.
name|byIncludeUUID
argument_list|(
name|group
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getGroupUUID
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
name|Sets
operator|.
name|newHashSet
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
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|groupArray
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
block|}
block|}
end_class

end_unit

