begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.restapi.group
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|restapi
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
name|base
operator|.
name|Preconditions
operator|.
name|checkState
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
operator|.
name|toImmutableSet
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
name|common
operator|.
name|data
operator|.
name|GroupDescription
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
name|AccountInfo
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
name|RestReadView
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
name|Account
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
name|account
operator|.
name|AccountInfoComparator
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
name|AccountLoader
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
name|account
operator|.
name|GroupControl
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
name|GroupResource
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
name|InternalGroupDescription
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
name|Optional
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
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Option
import|;
end_import

begin_class
DECL|class|ListMembers
specifier|public
class|class
name|ListMembers
implements|implements
name|RestReadView
argument_list|<
name|GroupResource
argument_list|>
block|{
DECL|field|groupCache
specifier|private
specifier|final
name|GroupCache
name|groupCache
decl_stmt|;
DECL|field|groupControlFactory
specifier|private
specifier|final
name|GroupControl
operator|.
name|Factory
name|groupControlFactory
decl_stmt|;
DECL|field|accountLoader
specifier|private
specifier|final
name|AccountLoader
name|accountLoader
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--recursive"
argument_list|,
name|usage
operator|=
literal|"to resolve included groups recursively"
argument_list|)
DECL|field|recursive
specifier|private
name|boolean
name|recursive
decl_stmt|;
annotation|@
name|Inject
DECL|method|ListMembers ( GroupCache groupCache, GroupControl.Factory groupControlFactory, AccountLoader.Factory accountLoaderFactory)
specifier|protected
name|ListMembers
parameter_list|(
name|GroupCache
name|groupCache
parameter_list|,
name|GroupControl
operator|.
name|Factory
name|groupControlFactory
parameter_list|,
name|AccountLoader
operator|.
name|Factory
name|accountLoaderFactory
parameter_list|)
block|{
name|this
operator|.
name|groupCache
operator|=
name|groupCache
expr_stmt|;
name|this
operator|.
name|groupControlFactory
operator|=
name|groupControlFactory
expr_stmt|;
name|this
operator|.
name|accountLoader
operator|=
name|accountLoaderFactory
operator|.
name|create
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|setRecursive (boolean recursive)
specifier|public
name|ListMembers
name|setRecursive
parameter_list|(
name|boolean
name|recursive
parameter_list|)
block|{
name|this
operator|.
name|recursive
operator|=
name|recursive
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|apply (GroupResource resource)
specifier|public
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|apply
parameter_list|(
name|GroupResource
name|resource
parameter_list|)
throws|throws
name|NotInternalGroupException
throws|,
name|OrmException
block|{
name|GroupDescription
operator|.
name|Internal
name|group
init|=
name|resource
operator|.
name|asInternalGroup
argument_list|()
operator|.
name|orElseThrow
argument_list|(
name|NotInternalGroupException
operator|::
operator|new
argument_list|)
decl_stmt|;
if|if
condition|(
name|recursive
condition|)
block|{
return|return
name|getTransitiveMembers
argument_list|(
name|group
argument_list|,
name|resource
operator|.
name|getControl
argument_list|()
argument_list|)
return|;
block|}
return|return
name|getDirectMembers
argument_list|(
name|group
argument_list|,
name|resource
operator|.
name|getControl
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getTransitiveMembers (AccountGroup.UUID groupUuid)
specifier|public
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|getTransitiveMembers
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|groupUuid
parameter_list|)
throws|throws
name|OrmException
block|{
name|Optional
argument_list|<
name|InternalGroup
argument_list|>
name|group
init|=
name|groupCache
operator|.
name|get
argument_list|(
name|groupUuid
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|InternalGroupDescription
name|internalGroup
init|=
operator|new
name|InternalGroupDescription
argument_list|(
name|group
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|GroupControl
name|groupControl
init|=
name|groupControlFactory
operator|.
name|controlFor
argument_list|(
name|internalGroup
argument_list|)
decl_stmt|;
return|return
name|getTransitiveMembers
argument_list|(
name|internalGroup
argument_list|,
name|groupControl
argument_list|)
return|;
block|}
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
DECL|method|getTransitiveMembers ( GroupDescription.Internal group, GroupControl groupControl)
specifier|private
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|getTransitiveMembers
parameter_list|(
name|GroupDescription
operator|.
name|Internal
name|group
parameter_list|,
name|GroupControl
name|groupControl
parameter_list|)
throws|throws
name|OrmException
block|{
name|checkSameGroup
argument_list|(
name|group
argument_list|,
name|groupControl
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|members
init|=
name|getTransitiveMemberIds
argument_list|(
name|group
argument_list|,
name|groupControl
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|group
operator|.
name|getGroupUUID
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|toAccountInfos
argument_list|(
name|members
argument_list|)
return|;
block|}
DECL|method|getDirectMembers (InternalGroup group)
specifier|public
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|getDirectMembers
parameter_list|(
name|InternalGroup
name|group
parameter_list|)
throws|throws
name|OrmException
block|{
name|InternalGroupDescription
name|internalGroup
init|=
operator|new
name|InternalGroupDescription
argument_list|(
name|group
argument_list|)
decl_stmt|;
return|return
name|getDirectMembers
argument_list|(
name|internalGroup
argument_list|,
name|groupControlFactory
operator|.
name|controlFor
argument_list|(
name|internalGroup
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getDirectMembers ( GroupDescription.Internal group, GroupControl groupControl)
specifier|public
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|getDirectMembers
parameter_list|(
name|GroupDescription
operator|.
name|Internal
name|group
parameter_list|,
name|GroupControl
name|groupControl
parameter_list|)
throws|throws
name|OrmException
block|{
name|checkSameGroup
argument_list|(
name|group
argument_list|,
name|groupControl
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|directMembers
init|=
name|getDirectMemberIds
argument_list|(
name|group
argument_list|,
name|groupControl
argument_list|)
decl_stmt|;
return|return
name|toAccountInfos
argument_list|(
name|directMembers
argument_list|)
return|;
block|}
DECL|method|toAccountInfos (Set<Account.Id> members)
specifier|private
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|toAccountInfos
parameter_list|(
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|members
parameter_list|)
throws|throws
name|OrmException
block|{
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|memberInfos
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|members
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Account
operator|.
name|Id
name|member
range|:
name|members
control|)
block|{
name|memberInfos
operator|.
name|add
argument_list|(
name|accountLoader
operator|.
name|get
argument_list|(
name|member
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|accountLoader
operator|.
name|fill
argument_list|()
expr_stmt|;
name|memberInfos
operator|.
name|sort
argument_list|(
name|AccountInfoComparator
operator|.
name|ORDER_NULLS_FIRST
argument_list|)
expr_stmt|;
return|return
name|memberInfos
return|;
block|}
DECL|method|getTransitiveMemberIds ( GroupDescription.Internal group, GroupControl groupControl, HashSet<AccountGroup.UUID> seenGroups)
specifier|private
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|getTransitiveMemberIds
parameter_list|(
name|GroupDescription
operator|.
name|Internal
name|group
parameter_list|,
name|GroupControl
name|groupControl
parameter_list|,
name|HashSet
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|seenGroups
parameter_list|)
block|{
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|directMembers
init|=
name|getDirectMemberIds
argument_list|(
name|group
argument_list|,
name|groupControl
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|groupControl
operator|.
name|canSeeGroup
argument_list|()
condition|)
block|{
return|return
name|directMembers
return|;
block|}
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|indirectMembers
init|=
name|getIndirectMemberIds
argument_list|(
name|group
argument_list|,
name|seenGroups
argument_list|)
decl_stmt|;
return|return
name|Sets
operator|.
name|union
argument_list|(
name|directMembers
argument_list|,
name|indirectMembers
argument_list|)
return|;
block|}
DECL|method|getDirectMemberIds ( GroupDescription.Internal group, GroupControl groupControl)
specifier|private
specifier|static
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|getDirectMemberIds
parameter_list|(
name|GroupDescription
operator|.
name|Internal
name|group
parameter_list|,
name|GroupControl
name|groupControl
parameter_list|)
block|{
return|return
name|group
operator|.
name|getMembers
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|groupControl
operator|::
name|canSeeMember
argument_list|)
operator|.
name|collect
argument_list|(
name|toImmutableSet
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getIndirectMemberIds ( GroupDescription.Internal group, HashSet<AccountGroup.UUID> seenGroups)
specifier|private
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|getIndirectMemberIds
parameter_list|(
name|GroupDescription
operator|.
name|Internal
name|group
parameter_list|,
name|HashSet
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|seenGroups
parameter_list|)
block|{
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|indirectMembers
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountGroup
operator|.
name|UUID
name|subgroupUuid
range|:
name|group
operator|.
name|getSubgroups
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|seenGroups
operator|.
name|contains
argument_list|(
name|subgroupUuid
argument_list|)
condition|)
block|{
name|seenGroups
operator|.
name|add
argument_list|(
name|subgroupUuid
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|subgroupMembers
init|=
name|groupCache
operator|.
name|get
argument_list|(
name|subgroupUuid
argument_list|)
operator|.
name|map
argument_list|(
name|InternalGroupDescription
operator|::
operator|new
argument_list|)
operator|.
name|map
argument_list|(
name|subgroup
lambda|->
block|{
name|GroupControl
name|subgroupControl
init|=
name|groupControlFactory
operator|.
name|controlFor
argument_list|(
name|subgroup
argument_list|)
decl_stmt|;
return|return
name|getTransitiveMemberIds
argument_list|(
name|subgroup
argument_list|,
name|subgroupControl
argument_list|,
name|seenGroups
argument_list|)
return|;
block|}
argument_list|)
operator|.
name|orElseGet
argument_list|(
name|ImmutableSet
operator|::
name|of
argument_list|)
decl_stmt|;
name|indirectMembers
operator|.
name|addAll
argument_list|(
name|subgroupMembers
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|indirectMembers
return|;
block|}
DECL|method|checkSameGroup (GroupDescription.Internal group, GroupControl groupControl)
specifier|private
specifier|static
name|void
name|checkSameGroup
parameter_list|(
name|GroupDescription
operator|.
name|Internal
name|group
parameter_list|,
name|GroupControl
name|groupControl
parameter_list|)
block|{
name|checkState
argument_list|(
name|group
operator|.
name|equals
argument_list|(
name|groupControl
operator|.
name|getGroup
argument_list|()
argument_list|)
argument_list|,
literal|"Specified group and groupControl do not match"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

