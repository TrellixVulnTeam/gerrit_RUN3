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
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toList
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
name|gerrit
operator|.
name|server
operator|.
name|group
operator|.
name|db
operator|.
name|Groups
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
name|ProjectState
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
name|ObjectId
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
DECL|field|groups
specifier|private
specifier|final
name|Groups
name|groups
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
DECL|method|InternalGroupBackend ( GroupControl.Factory groupControlFactory, GroupCache groupCache, Groups groups, SchemaFactory<ReviewDb> schema, IncludingGroupMembership.Factory groupMembershipFactory)
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
name|Groups
name|groups
parameter_list|,
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
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
name|groups
operator|=
name|groups
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
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
comment|// See AccountGroup.isInternalGroup
return|return
name|ObjectId
operator|.
name|isId
argument_list|(
name|uuid
operator|.
name|get
argument_list|()
argument_list|)
return|;
comment|// [0-9a-f]{40};
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
return|return
name|groupCache
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
operator|.
name|map
argument_list|(
name|InternalGroupDescription
operator|::
operator|new
argument_list|)
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|suggest (String name, ProjectState project)
specifier|public
name|Collection
argument_list|<
name|GroupReference
argument_list|>
name|suggest
parameter_list|(
name|String
name|name
parameter_list|,
name|ProjectState
name|project
parameter_list|)
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
return|return
name|groups
operator|.
name|getAll
argument_list|(
name|db
argument_list|)
comment|// TODO(aliceks): Filter the groups by name before loading them (if possible with NoteDb).
operator|.
name|filter
argument_list|(
name|group
lambda|->
name|startsWithIgnoreCase
argument_list|(
name|group
argument_list|,
name|name
argument_list|)
argument_list|)
operator|.
name|map
argument_list|(
name|InternalGroupDescription
operator|::
operator|new
argument_list|)
operator|.
name|filter
argument_list|(
name|this
operator|::
name|isVisible
argument_list|)
operator|.
name|map
argument_list|(
name|GroupReference
operator|::
name|forGroup
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
block|}
DECL|method|startsWithIgnoreCase (InternalGroup group, String name)
specifier|private
specifier|static
name|boolean
name|startsWithIgnoreCase
parameter_list|(
name|InternalGroup
name|group
parameter_list|,
name|String
name|name
parameter_list|)
block|{
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
return|;
block|}
DECL|method|isVisible (GroupDescription.Internal group)
specifier|private
name|boolean
name|isVisible
parameter_list|(
name|GroupDescription
operator|.
name|Internal
name|group
parameter_list|)
block|{
return|return
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
annotation|@
name|Override
DECL|method|isVisibleToAll (AccountGroup.UUID uuid)
specifier|public
name|boolean
name|isVisibleToAll
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|)
block|{
name|GroupDescription
operator|.
name|Internal
name|g
init|=
name|get
argument_list|(
name|uuid
argument_list|)
decl_stmt|;
return|return
name|g
operator|!=
literal|null
operator|&&
name|g
operator|.
name|isVisibleToAll
argument_list|()
return|;
block|}
block|}
end_class

end_unit

