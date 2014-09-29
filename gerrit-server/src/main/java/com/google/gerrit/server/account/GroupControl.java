begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
name|CurrentUser
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
name|InternalUser
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
name|javax
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_comment
comment|/** Access control management for a group of accounts managed in Gerrit. */
end_comment

begin_class
DECL|class|GroupControl
specifier|public
class|class
name|GroupControl
block|{
annotation|@
name|Singleton
DECL|class|GenericFactory
specifier|public
specifier|static
class|class
name|GenericFactory
block|{
DECL|field|groupBackend
specifier|private
specifier|final
name|GroupBackend
name|groupBackend
decl_stmt|;
annotation|@
name|Inject
DECL|method|GenericFactory (final GroupBackend gb)
name|GenericFactory
parameter_list|(
specifier|final
name|GroupBackend
name|gb
parameter_list|)
block|{
name|groupBackend
operator|=
name|gb
expr_stmt|;
block|}
DECL|method|controlFor (final CurrentUser who, final AccountGroup.UUID groupId)
specifier|public
name|GroupControl
name|controlFor
parameter_list|(
specifier|final
name|CurrentUser
name|who
parameter_list|,
specifier|final
name|AccountGroup
operator|.
name|UUID
name|groupId
parameter_list|)
throws|throws
name|NoSuchGroupException
block|{
specifier|final
name|GroupDescription
operator|.
name|Basic
name|group
init|=
name|groupBackend
operator|.
name|get
argument_list|(
name|groupId
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchGroupException
argument_list|(
name|groupId
argument_list|)
throw|;
block|}
return|return
operator|new
name|GroupControl
argument_list|(
name|who
argument_list|,
name|group
argument_list|)
return|;
block|}
block|}
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
block|{
DECL|field|groupCache
specifier|private
specifier|final
name|GroupCache
name|groupCache
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
decl_stmt|;
DECL|field|groupBackend
specifier|private
specifier|final
name|GroupBackend
name|groupBackend
decl_stmt|;
annotation|@
name|Inject
DECL|method|Factory (final GroupCache gc, final Provider<CurrentUser> cu, final GroupBackend gb)
name|Factory
parameter_list|(
specifier|final
name|GroupCache
name|gc
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|cu
parameter_list|,
specifier|final
name|GroupBackend
name|gb
parameter_list|)
block|{
name|groupCache
operator|=
name|gc
expr_stmt|;
name|user
operator|=
name|cu
expr_stmt|;
name|groupBackend
operator|=
name|gb
expr_stmt|;
block|}
DECL|method|controlFor (final AccountGroup.Id groupId)
specifier|public
name|GroupControl
name|controlFor
parameter_list|(
specifier|final
name|AccountGroup
operator|.
name|Id
name|groupId
parameter_list|)
throws|throws
name|NoSuchGroupException
block|{
specifier|final
name|AccountGroup
name|group
init|=
name|groupCache
operator|.
name|get
argument_list|(
name|groupId
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchGroupException
argument_list|(
name|groupId
argument_list|)
throw|;
block|}
return|return
name|controlFor
argument_list|(
name|GroupDescriptions
operator|.
name|forAccountGroup
argument_list|(
name|group
argument_list|)
argument_list|)
return|;
block|}
DECL|method|controlFor (final AccountGroup.UUID groupId)
specifier|public
name|GroupControl
name|controlFor
parameter_list|(
specifier|final
name|AccountGroup
operator|.
name|UUID
name|groupId
parameter_list|)
throws|throws
name|NoSuchGroupException
block|{
specifier|final
name|GroupDescription
operator|.
name|Basic
name|group
init|=
name|groupBackend
operator|.
name|get
argument_list|(
name|groupId
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchGroupException
argument_list|(
name|groupId
argument_list|)
throw|;
block|}
return|return
name|controlFor
argument_list|(
name|group
argument_list|)
return|;
block|}
DECL|method|controlFor (AccountGroup group)
specifier|public
name|GroupControl
name|controlFor
parameter_list|(
name|AccountGroup
name|group
parameter_list|)
block|{
return|return
name|controlFor
argument_list|(
name|GroupDescriptions
operator|.
name|forAccountGroup
argument_list|(
name|group
argument_list|)
argument_list|)
return|;
block|}
DECL|method|controlFor (GroupDescription.Basic group)
specifier|public
name|GroupControl
name|controlFor
parameter_list|(
name|GroupDescription
operator|.
name|Basic
name|group
parameter_list|)
block|{
return|return
operator|new
name|GroupControl
argument_list|(
name|user
operator|.
name|get
argument_list|()
argument_list|,
name|group
argument_list|)
return|;
block|}
DECL|method|validateFor (final AccountGroup.Id groupId)
specifier|public
name|GroupControl
name|validateFor
parameter_list|(
specifier|final
name|AccountGroup
operator|.
name|Id
name|groupId
parameter_list|)
throws|throws
name|NoSuchGroupException
block|{
specifier|final
name|GroupControl
name|c
init|=
name|controlFor
argument_list|(
name|groupId
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|c
operator|.
name|isVisible
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchGroupException
argument_list|(
name|groupId
argument_list|)
throw|;
block|}
return|return
name|c
return|;
block|}
DECL|method|validateFor (final AccountGroup.UUID groupUUID)
specifier|public
name|GroupControl
name|validateFor
parameter_list|(
specifier|final
name|AccountGroup
operator|.
name|UUID
name|groupUUID
parameter_list|)
throws|throws
name|NoSuchGroupException
block|{
specifier|final
name|GroupControl
name|c
init|=
name|controlFor
argument_list|(
name|groupUUID
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|c
operator|.
name|isVisible
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchGroupException
argument_list|(
name|groupUUID
argument_list|)
throw|;
block|}
return|return
name|c
return|;
block|}
block|}
DECL|field|user
specifier|private
specifier|final
name|CurrentUser
name|user
decl_stmt|;
DECL|field|group
specifier|private
specifier|final
name|GroupDescription
operator|.
name|Basic
name|group
decl_stmt|;
DECL|field|isOwner
specifier|private
name|Boolean
name|isOwner
decl_stmt|;
DECL|method|GroupControl (CurrentUser who, GroupDescription.Basic gd)
name|GroupControl
parameter_list|(
name|CurrentUser
name|who
parameter_list|,
name|GroupDescription
operator|.
name|Basic
name|gd
parameter_list|)
block|{
name|user
operator|=
name|who
expr_stmt|;
name|group
operator|=
name|gd
expr_stmt|;
block|}
DECL|method|getGroup ()
specifier|public
name|GroupDescription
operator|.
name|Basic
name|getGroup
parameter_list|()
block|{
return|return
name|group
return|;
block|}
DECL|method|getCurrentUser ()
specifier|public
name|CurrentUser
name|getCurrentUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
comment|/** Can this user see this group exists? */
DECL|method|isVisible ()
specifier|public
name|boolean
name|isVisible
parameter_list|()
block|{
name|AccountGroup
name|accountGroup
init|=
name|GroupDescriptions
operator|.
name|toAccountGroup
argument_list|(
name|group
argument_list|)
decl_stmt|;
comment|/* Check for canAdministrateServer may seem redundant, but allows      * for visibility of all groups that are not an internal group to      * server administrators.      */
return|return
operator|(
name|accountGroup
operator|!=
literal|null
operator|&&
name|accountGroup
operator|.
name|isVisibleToAll
argument_list|()
operator|)
operator|||
name|user
operator|instanceof
name|InternalUser
operator|||
name|user
operator|.
name|getEffectiveGroups
argument_list|()
operator|.
name|contains
argument_list|(
name|group
operator|.
name|getGroupUUID
argument_list|()
argument_list|)
operator|||
name|isOwner
argument_list|()
operator|||
name|user
operator|.
name|getCapabilities
argument_list|()
operator|.
name|canAdministrateServer
argument_list|()
return|;
block|}
DECL|method|isOwner ()
specifier|public
name|boolean
name|isOwner
parameter_list|()
block|{
name|AccountGroup
name|accountGroup
init|=
name|GroupDescriptions
operator|.
name|toAccountGroup
argument_list|(
name|group
argument_list|)
decl_stmt|;
if|if
condition|(
name|accountGroup
operator|==
literal|null
condition|)
block|{
name|isOwner
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isOwner
operator|==
literal|null
condition|)
block|{
name|AccountGroup
operator|.
name|UUID
name|ownerUUID
init|=
name|accountGroup
operator|.
name|getOwnerGroupUUID
argument_list|()
decl_stmt|;
name|isOwner
operator|=
name|getCurrentUser
argument_list|()
operator|.
name|getEffectiveGroups
argument_list|()
operator|.
name|contains
argument_list|(
name|ownerUUID
argument_list|)
operator|||
name|getCurrentUser
argument_list|()
operator|.
name|getCapabilities
argument_list|()
operator|.
name|canAdministrateServer
argument_list|()
expr_stmt|;
block|}
return|return
name|isOwner
return|;
block|}
DECL|method|canAddMember (Account.Id id)
specifier|public
name|boolean
name|canAddMember
parameter_list|(
name|Account
operator|.
name|Id
name|id
parameter_list|)
block|{
return|return
name|isOwner
argument_list|()
return|;
block|}
DECL|method|canRemoveMember (Account.Id id)
specifier|public
name|boolean
name|canRemoveMember
parameter_list|(
name|Account
operator|.
name|Id
name|id
parameter_list|)
block|{
return|return
name|isOwner
argument_list|()
return|;
block|}
DECL|method|canSeeMember (Account.Id id)
specifier|public
name|boolean
name|canSeeMember
parameter_list|(
name|Account
operator|.
name|Id
name|id
parameter_list|)
block|{
if|if
condition|(
name|user
operator|.
name|isIdentifiedUser
argument_list|()
operator|&&
operator|(
operator|(
name|IdentifiedUser
operator|)
name|user
operator|)
operator|.
name|getAccountId
argument_list|()
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|canSeeMembers
argument_list|()
return|;
block|}
DECL|method|canAddGroup (AccountGroup.UUID uuid)
specifier|public
name|boolean
name|canAddGroup
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|)
block|{
return|return
name|isOwner
argument_list|()
return|;
block|}
DECL|method|canRemoveGroup (AccountGroup.UUID uuid)
specifier|public
name|boolean
name|canRemoveGroup
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|)
block|{
return|return
name|isOwner
argument_list|()
return|;
block|}
DECL|method|canSeeGroup (AccountGroup.UUID uuid)
specifier|public
name|boolean
name|canSeeGroup
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|)
block|{
return|return
name|canSeeMembers
argument_list|()
return|;
block|}
DECL|method|canSeeMembers ()
specifier|private
name|boolean
name|canSeeMembers
parameter_list|()
block|{
name|AccountGroup
name|accountGroup
init|=
name|GroupDescriptions
operator|.
name|toAccountGroup
argument_list|(
name|group
argument_list|)
decl_stmt|;
return|return
operator|(
name|accountGroup
operator|!=
literal|null
operator|&&
name|accountGroup
operator|.
name|isVisibleToAll
argument_list|()
operator|)
operator|||
name|isOwner
argument_list|()
return|;
block|}
block|}
end_class

end_unit

