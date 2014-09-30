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
name|base
operator|.
name|Function
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
name|base
operator|.
name|Predicate
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
name|Iterables
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
name|Lists
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
name|common
operator|.
name|data
operator|.
name|GroupDescriptions
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
name|GroupReference
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
name|IdentifiedUser
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
name|project
operator|.
name|ProjectControl
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
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_comment
comment|/** Implementation of GroupBackend for the internal group system. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|InternalGroupBackend
specifier|public
class|class
name|InternalGroupBackend
implements|implements
name|GroupBackend
block|{
DECL|field|ACT_GROUP_TO_GROUP_REF
specifier|private
specifier|static
specifier|final
name|Function
argument_list|<
name|AccountGroup
argument_list|,
name|GroupReference
argument_list|>
name|ACT_GROUP_TO_GROUP_REF
init|=
operator|new
name|Function
argument_list|<
name|AccountGroup
argument_list|,
name|GroupReference
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|GroupReference
name|apply
parameter_list|(
name|AccountGroup
name|group
parameter_list|)
block|{
return|return
name|GroupReference
operator|.
name|forGroup
argument_list|(
name|group
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|field|groupControlFactory
specifier|private
specifier|final
name|GroupControl
operator|.
name|Factory
name|groupControlFactory
decl_stmt|;
DECL|field|groupCache
specifier|private
specifier|final
name|GroupCache
name|groupCache
decl_stmt|;
DECL|field|groupMembershipFactory
specifier|private
specifier|final
name|IncludingGroupMembership
operator|.
name|Factory
name|groupMembershipFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|InternalGroupBackend (GroupControl.Factory groupControlFactory, GroupCache groupCache, IncludingGroupMembership.Factory groupMembershipFactory)
name|InternalGroupBackend
parameter_list|(
name|GroupControl
operator|.
name|Factory
name|groupControlFactory
parameter_list|,
name|GroupCache
name|groupCache
parameter_list|,
name|IncludingGroupMembership
operator|.
name|Factory
name|groupMembershipFactory
parameter_list|)
block|{
name|this
operator|.
name|groupControlFactory
operator|=
name|groupControlFactory
expr_stmt|;
name|this
operator|.
name|groupCache
operator|=
name|groupCache
expr_stmt|;
name|this
operator|.
name|groupMembershipFactory
operator|=
name|groupMembershipFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handles (AccountGroup.UUID uuid)
specifier|public
name|boolean
name|handles
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|)
block|{
return|return
name|AccountGroup
operator|.
name|isInternalGroup
argument_list|(
name|uuid
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|get (AccountGroup.UUID uuid)
specifier|public
name|GroupDescription
operator|.
name|Internal
name|get
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|)
block|{
if|if
condition|(
operator|!
name|handles
argument_list|(
name|uuid
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|AccountGroup
name|g
init|=
name|groupCache
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
decl_stmt|;
if|if
condition|(
name|g
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|GroupDescriptions
operator|.
name|forAccountGroup
argument_list|(
name|g
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|suggest (final String name, final ProjectControl project)
specifier|public
name|Collection
argument_list|<
name|GroupReference
argument_list|>
name|suggest
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|ProjectControl
name|project
parameter_list|)
block|{
name|Iterable
argument_list|<
name|AccountGroup
argument_list|>
name|filtered
init|=
name|Iterables
operator|.
name|filter
argument_list|(
name|groupCache
operator|.
name|all
argument_list|()
argument_list|,
operator|new
name|Predicate
argument_list|<
name|AccountGroup
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|AccountGroup
name|group
parameter_list|)
block|{
comment|// startsWithIgnoreCase&& isVisible
return|return
name|group
operator|.
name|getName
argument_list|()
operator|.
name|regionMatches
argument_list|(
literal|true
argument_list|,
literal|0
argument_list|,
name|name
argument_list|,
literal|0
argument_list|,
name|name
operator|.
name|length
argument_list|()
argument_list|)
operator|&&
name|groupControlFactory
operator|.
name|controlFor
argument_list|(
name|group
argument_list|)
operator|.
name|isVisible
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|Lists
operator|.
name|newArrayList
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|filtered
argument_list|,
name|ACT_GROUP_TO_GROUP_REF
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|membershipsOf (IdentifiedUser user)
specifier|public
name|GroupMembership
name|membershipsOf
parameter_list|(
name|IdentifiedUser
name|user
parameter_list|)
block|{
return|return
name|groupMembershipFactory
operator|.
name|create
argument_list|(
name|user
argument_list|)
return|;
block|}
block|}
end_class

end_unit

