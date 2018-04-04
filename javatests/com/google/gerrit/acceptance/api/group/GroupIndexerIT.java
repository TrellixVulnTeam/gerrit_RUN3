begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2018 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.api.group
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|api
operator|.
name|group
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
name|truth
operator|.
name|Truth
operator|.
name|assertWithMessage
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|truth
operator|.
name|ListSubject
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|truth
operator|.
name|OptionalSubject
operator|.
name|assertThat
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
name|gerrit
operator|.
name|common
operator|.
name|errors
operator|.
name|NoSuchGroupException
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
name|api
operator|.
name|GerritApi
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
name|common
operator|.
name|GroupInfo
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
name|restapi
operator|.
name|RestApiException
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
name|server
operator|.
name|ServerInitiated
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
name|account
operator|.
name|GroupCache
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
name|group
operator|.
name|InternalGroup
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
name|group
operator|.
name|db
operator|.
name|GroupsUpdate
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
name|group
operator|.
name|db
operator|.
name|InternalGroupUpdate
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
name|group
operator|.
name|testing
operator|.
name|InternalGroupSubject
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
name|group
operator|.
name|GroupIndexer
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
name|group
operator|.
name|InternalGroupQuery
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
name|testing
operator|.
name|InMemoryTestEnvironment
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
name|truth
operator|.
name|ListSubject
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
name|truth
operator|.
name|OptionalSubject
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
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
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|GroupIndexerIT
specifier|public
class|class
name|GroupIndexerIT
block|{
DECL|field|testEnvironment
annotation|@
name|Rule
specifier|public
name|InMemoryTestEnvironment
name|testEnvironment
init|=
operator|new
name|InMemoryTestEnvironment
argument_list|()
decl_stmt|;
DECL|field|groupIndexer
annotation|@
name|Inject
specifier|private
name|GroupIndexer
name|groupIndexer
decl_stmt|;
DECL|field|gApi
annotation|@
name|Inject
specifier|private
name|GerritApi
name|gApi
decl_stmt|;
DECL|field|groupCache
annotation|@
name|Inject
specifier|private
name|GroupCache
name|groupCache
decl_stmt|;
DECL|field|groupsUpdate
annotation|@
name|Inject
annotation|@
name|ServerInitiated
specifier|private
name|GroupsUpdate
name|groupsUpdate
decl_stmt|;
DECL|field|groupQueryProvider
annotation|@
name|Inject
specifier|private
name|Provider
argument_list|<
name|InternalGroupQuery
argument_list|>
name|groupQueryProvider
decl_stmt|;
annotation|@
name|Test
DECL|method|indexingUpdatesTheIndex ()
specifier|public
name|void
name|indexingUpdatesTheIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|AccountGroup
operator|.
name|UUID
name|groupUuid
init|=
name|createGroup
argument_list|(
literal|"users"
argument_list|)
decl_stmt|;
name|AccountGroup
operator|.
name|UUID
name|subgroupUuid
init|=
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
literal|"contributors"
argument_list|)
decl_stmt|;
name|updateGroupWithoutCacheOrIndex
argument_list|(
name|groupUuid
argument_list|,
name|newGroupUpdate
argument_list|()
operator|.
name|setSubgroupModification
argument_list|(
name|subgroups
lambda|->
name|ImmutableSet
operator|.
name|of
argument_list|(
name|subgroupUuid
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|groupIndexer
operator|.
name|index
argument_list|(
name|groupUuid
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|InternalGroup
argument_list|>
name|parentGroups
init|=
name|groupQueryProvider
operator|.
name|get
argument_list|()
operator|.
name|bySubgroup
argument_list|(
name|subgroupUuid
argument_list|)
decl_stmt|;
name|assertThatGroups
argument_list|(
name|parentGroups
argument_list|)
operator|.
name|onlyElement
argument_list|()
operator|.
name|groupUuid
argument_list|()
operator|.
name|isEqualTo
argument_list|(
name|groupUuid
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|indexCannotBeCorruptedByStaleCache ()
specifier|public
name|void
name|indexCannotBeCorruptedByStaleCache
parameter_list|()
throws|throws
name|Exception
block|{
name|AccountGroup
operator|.
name|UUID
name|groupUuid
init|=
name|createGroup
argument_list|(
literal|"verifiers"
argument_list|)
decl_stmt|;
name|loadGroupToCache
argument_list|(
name|groupUuid
argument_list|)
expr_stmt|;
name|AccountGroup
operator|.
name|UUID
name|subgroupUuid
init|=
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
literal|"contributors"
argument_list|)
decl_stmt|;
name|updateGroupWithoutCacheOrIndex
argument_list|(
name|groupUuid
argument_list|,
name|newGroupUpdate
argument_list|()
operator|.
name|setSubgroupModification
argument_list|(
name|subgroups
lambda|->
name|ImmutableSet
operator|.
name|of
argument_list|(
name|subgroupUuid
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|groupIndexer
operator|.
name|index
argument_list|(
name|groupUuid
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|InternalGroup
argument_list|>
name|parentGroups
init|=
name|groupQueryProvider
operator|.
name|get
argument_list|()
operator|.
name|bySubgroup
argument_list|(
name|subgroupUuid
argument_list|)
decl_stmt|;
name|assertThatGroups
argument_list|(
name|parentGroups
argument_list|)
operator|.
name|onlyElement
argument_list|()
operator|.
name|groupUuid
argument_list|()
operator|.
name|isEqualTo
argument_list|(
name|groupUuid
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|indexingUpdatesStaleUuidCache ()
specifier|public
name|void
name|indexingUpdatesStaleUuidCache
parameter_list|()
throws|throws
name|Exception
block|{
name|AccountGroup
operator|.
name|UUID
name|groupUuid
init|=
name|createGroup
argument_list|(
literal|"verifiers"
argument_list|)
decl_stmt|;
name|loadGroupToCache
argument_list|(
name|groupUuid
argument_list|)
expr_stmt|;
name|updateGroupWithoutCacheOrIndex
argument_list|(
name|groupUuid
argument_list|,
name|newGroupUpdate
argument_list|()
operator|.
name|setDescription
argument_list|(
literal|"Modified"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|groupIndexer
operator|.
name|index
argument_list|(
name|groupUuid
argument_list|)
expr_stmt|;
name|Optional
argument_list|<
name|InternalGroup
argument_list|>
name|updatedGroup
init|=
name|groupCache
operator|.
name|get
argument_list|(
name|groupUuid
argument_list|)
decl_stmt|;
name|assertThatGroup
argument_list|(
name|updatedGroup
argument_list|)
operator|.
name|value
argument_list|()
operator|.
name|description
argument_list|()
operator|.
name|isEqualTo
argument_list|(
literal|"Modified"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|reindexingStaleGroupUpdatesTheIndex ()
specifier|public
name|void
name|reindexingStaleGroupUpdatesTheIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|AccountGroup
operator|.
name|UUID
name|groupUuid
init|=
name|createGroup
argument_list|(
literal|"users"
argument_list|)
decl_stmt|;
name|AccountGroup
operator|.
name|UUID
name|subgroupUuid
init|=
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
literal|"contributors"
argument_list|)
decl_stmt|;
name|updateGroupWithoutCacheOrIndex
argument_list|(
name|groupUuid
argument_list|,
name|newGroupUpdate
argument_list|()
operator|.
name|setSubgroupModification
argument_list|(
name|subgroups
lambda|->
name|ImmutableSet
operator|.
name|of
argument_list|(
name|subgroupUuid
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|groupIndexer
operator|.
name|reindexIfStale
argument_list|(
name|groupUuid
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|InternalGroup
argument_list|>
name|parentGroups
init|=
name|groupQueryProvider
operator|.
name|get
argument_list|()
operator|.
name|bySubgroup
argument_list|(
name|subgroupUuid
argument_list|)
decl_stmt|;
name|assertThatGroups
argument_list|(
name|parentGroups
argument_list|)
operator|.
name|onlyElement
argument_list|()
operator|.
name|groupUuid
argument_list|()
operator|.
name|isEqualTo
argument_list|(
name|groupUuid
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|notStaleGroupIsNotReindexed ()
specifier|public
name|void
name|notStaleGroupIsNotReindexed
parameter_list|()
throws|throws
name|Exception
block|{
name|AccountGroup
operator|.
name|UUID
name|groupUuid
init|=
name|createGroup
argument_list|(
literal|"verifiers"
argument_list|)
decl_stmt|;
name|updateGroupWithoutCacheOrIndex
argument_list|(
name|groupUuid
argument_list|,
name|newGroupUpdate
argument_list|()
operator|.
name|setDescription
argument_list|(
literal|"Modified"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|groupIndexer
operator|.
name|index
argument_list|(
name|groupUuid
argument_list|)
expr_stmt|;
name|boolean
name|reindexed
init|=
name|groupIndexer
operator|.
name|reindexIfStale
argument_list|(
name|groupUuid
argument_list|)
decl_stmt|;
name|assertWithMessage
argument_list|(
literal|"Group should not have been reindexed"
argument_list|)
operator|.
name|that
argument_list|(
name|reindexed
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|indexStalenessIsNotDerivedFromCacheStaleness ()
specifier|public
name|void
name|indexStalenessIsNotDerivedFromCacheStaleness
parameter_list|()
throws|throws
name|Exception
block|{
name|AccountGroup
operator|.
name|UUID
name|groupUuid
init|=
name|createGroup
argument_list|(
literal|"verifiers"
argument_list|)
decl_stmt|;
name|updateGroupWithoutCacheOrIndex
argument_list|(
name|groupUuid
argument_list|,
name|newGroupUpdate
argument_list|()
operator|.
name|setDescription
argument_list|(
literal|"Modified"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|reloadGroupToCache
argument_list|(
name|groupUuid
argument_list|)
expr_stmt|;
name|boolean
name|reindexed
init|=
name|groupIndexer
operator|.
name|reindexIfStale
argument_list|(
name|groupUuid
argument_list|)
decl_stmt|;
name|assertWithMessage
argument_list|(
literal|"Group should have been reindexed"
argument_list|)
operator|.
name|that
argument_list|(
name|reindexed
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
DECL|method|createGroup (String name)
specifier|private
name|AccountGroup
operator|.
name|UUID
name|createGroup
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RestApiException
block|{
name|GroupInfo
name|group
init|=
name|gApi
operator|.
name|groups
argument_list|()
operator|.
name|create
argument_list|(
name|name
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
return|return
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
name|group
operator|.
name|id
argument_list|)
return|;
block|}
DECL|method|reloadGroupToCache (AccountGroup.UUID groupUuid)
specifier|private
name|void
name|reloadGroupToCache
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|groupUuid
parameter_list|)
block|{
name|groupCache
operator|.
name|evict
argument_list|(
name|groupUuid
argument_list|)
expr_stmt|;
name|loadGroupToCache
argument_list|(
name|groupUuid
argument_list|)
expr_stmt|;
block|}
DECL|method|loadGroupToCache (AccountGroup.UUID groupUuid)
specifier|private
name|void
name|loadGroupToCache
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|groupUuid
parameter_list|)
block|{
name|groupCache
operator|.
name|get
argument_list|(
name|groupUuid
argument_list|)
expr_stmt|;
block|}
DECL|method|newGroupUpdate ()
specifier|private
specifier|static
name|InternalGroupUpdate
operator|.
name|Builder
name|newGroupUpdate
parameter_list|()
block|{
return|return
name|InternalGroupUpdate
operator|.
name|builder
argument_list|()
return|;
block|}
DECL|method|updateGroupWithoutCacheOrIndex ( AccountGroup.UUID groupUuid, InternalGroupUpdate groupUpdate)
specifier|private
name|void
name|updateGroupWithoutCacheOrIndex
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|groupUuid
parameter_list|,
name|InternalGroupUpdate
name|groupUpdate
parameter_list|)
throws|throws
name|OrmException
throws|,
name|NoSuchGroupException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|groupsUpdate
operator|.
name|updateGroupInNoteDb
argument_list|(
name|groupUuid
argument_list|,
name|groupUpdate
argument_list|)
expr_stmt|;
block|}
DECL|method|assertThatGroup ( Optional<InternalGroup> updatedGroup)
specifier|private
specifier|static
name|OptionalSubject
argument_list|<
name|InternalGroupSubject
argument_list|,
name|InternalGroup
argument_list|>
name|assertThatGroup
parameter_list|(
name|Optional
argument_list|<
name|InternalGroup
argument_list|>
name|updatedGroup
parameter_list|)
block|{
return|return
name|assertThat
argument_list|(
name|updatedGroup
argument_list|,
name|InternalGroupSubject
operator|::
name|assertThat
argument_list|)
return|;
block|}
DECL|method|assertThatGroups ( List<InternalGroup> parentGroups)
specifier|private
specifier|static
name|ListSubject
argument_list|<
name|InternalGroupSubject
argument_list|,
name|InternalGroup
argument_list|>
name|assertThatGroups
parameter_list|(
name|List
argument_list|<
name|InternalGroup
argument_list|>
name|parentGroups
parameter_list|)
block|{
return|return
name|assertThat
argument_list|(
name|parentGroups
argument_list|,
name|InternalGroupSubject
operator|::
name|assertThat
argument_list|)
return|;
block|}
block|}
end_class

end_unit

