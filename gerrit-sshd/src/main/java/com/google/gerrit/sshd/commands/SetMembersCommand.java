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
DECL|package|com.google.gerrit.sshd.commands
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|sshd
operator|.
name|commands
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
name|Joiner
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
name|Objects
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
name|extensions
operator|.
name|restapi
operator|.
name|IdString
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
name|TopLevelResource
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
name|AccountCache
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
name|AddIncludedGroups
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
name|AddMembers
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
name|DeleteIncludedGroups
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
name|DeleteMembers
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
name|GroupsCollection
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
name|sshd
operator|.
name|CommandMetaData
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
name|sshd
operator|.
name|SshCommand
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
name|org
operator|.
name|kohsuke
operator|.
name|args4j
operator|.
name|Argument
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
name|io
operator|.
name|UnsupportedEncodingException
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

begin_class
annotation|@
name|CommandMetaData
argument_list|(
name|name
operator|=
literal|"set-members"
argument_list|,
name|description
operator|=
literal|"Modifies members of specific group or number of groups"
argument_list|)
DECL|class|SetMembersCommand
specifier|public
class|class
name|SetMembersCommand
extends|extends
name|SshCommand
block|{
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--add"
argument_list|,
name|aliases
operator|=
block|{
literal|"-a"
block|}
argument_list|,
name|metaVar
operator|=
literal|"USER"
argument_list|,
name|usage
operator|=
literal|"users that should be added as group member"
argument_list|)
DECL|field|accountsToAdd
specifier|private
name|List
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|accountsToAdd
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--remove"
argument_list|,
name|aliases
operator|=
block|{
literal|"-r"
block|}
argument_list|,
name|metaVar
operator|=
literal|"USER"
argument_list|,
name|usage
operator|=
literal|"users that should be removed from the group"
argument_list|)
DECL|field|accountsToRemove
specifier|private
name|List
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|accountsToRemove
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--include"
argument_list|,
name|aliases
operator|=
block|{
literal|"-i"
block|}
argument_list|,
name|metaVar
operator|=
literal|"GROUP"
argument_list|,
name|usage
operator|=
literal|"group that should be included as group member"
argument_list|)
DECL|field|groupsToInclude
specifier|private
name|List
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|groupsToInclude
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--exclude"
argument_list|,
name|aliases
operator|=
block|{
literal|"-e"
block|}
argument_list|,
name|metaVar
operator|=
literal|"GROUP"
argument_list|,
name|usage
operator|=
literal|"group that should be excluded from the group"
argument_list|)
DECL|field|groupsToRemove
specifier|private
name|List
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|groupsToRemove
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
annotation|@
name|Argument
argument_list|(
name|index
operator|=
literal|0
argument_list|,
name|required
operator|=
literal|true
argument_list|,
name|multiValued
operator|=
literal|true
argument_list|,
name|metaVar
operator|=
literal|"GROUP"
argument_list|,
name|usage
operator|=
literal|"groups to modify"
argument_list|)
DECL|field|groups
specifier|private
name|List
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|groups
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|field|addMembers
specifier|private
name|Provider
argument_list|<
name|AddMembers
argument_list|>
name|addMembers
decl_stmt|;
annotation|@
name|Inject
DECL|field|deleteMembers
specifier|private
name|Provider
argument_list|<
name|DeleteMembers
argument_list|>
name|deleteMembers
decl_stmt|;
annotation|@
name|Inject
DECL|field|addIncludedGroups
specifier|private
name|Provider
argument_list|<
name|AddIncludedGroups
argument_list|>
name|addIncludedGroups
decl_stmt|;
annotation|@
name|Inject
DECL|field|deleteIncludedGroups
specifier|private
name|Provider
argument_list|<
name|DeleteIncludedGroups
argument_list|>
name|deleteIncludedGroups
decl_stmt|;
annotation|@
name|Inject
DECL|field|groupsCollection
specifier|private
name|GroupsCollection
name|groupsCollection
decl_stmt|;
annotation|@
name|Inject
DECL|field|groupCache
specifier|private
name|GroupCache
name|groupCache
decl_stmt|;
annotation|@
name|Inject
DECL|field|accountCache
specifier|private
name|AccountCache
name|accountCache
decl_stmt|;
annotation|@
name|Override
DECL|method|run ()
specifier|protected
name|void
name|run
parameter_list|()
throws|throws
name|UnloggedFailure
throws|,
name|Failure
throws|,
name|Exception
block|{
for|for
control|(
name|AccountGroup
operator|.
name|UUID
name|groupUuid
range|:
name|groups
control|)
block|{
name|GroupResource
name|resource
init|=
name|groupsCollection
operator|.
name|parse
argument_list|(
name|TopLevelResource
operator|.
name|INSTANCE
argument_list|,
name|IdString
operator|.
name|fromUrl
argument_list|(
name|groupUuid
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|accountsToRemove
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|deleteMembers
operator|.
name|get
argument_list|()
operator|.
name|apply
argument_list|(
name|resource
argument_list|,
name|fromMembers
argument_list|(
name|accountsToRemove
argument_list|)
argument_list|)
expr_stmt|;
name|reportMembersAction
argument_list|(
literal|"removed from"
argument_list|,
name|resource
argument_list|,
name|accountsToRemove
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|groupsToRemove
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|deleteIncludedGroups
operator|.
name|get
argument_list|()
operator|.
name|apply
argument_list|(
name|resource
argument_list|,
name|fromGroups
argument_list|(
name|groupsToRemove
argument_list|)
argument_list|)
expr_stmt|;
name|reportGroupsAction
argument_list|(
literal|"excluded from"
argument_list|,
name|resource
argument_list|,
name|groupsToRemove
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|accountsToAdd
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|addMembers
operator|.
name|get
argument_list|()
operator|.
name|apply
argument_list|(
name|resource
argument_list|,
name|fromMembers
argument_list|(
name|accountsToAdd
argument_list|)
argument_list|)
expr_stmt|;
name|reportMembersAction
argument_list|(
literal|"added to"
argument_list|,
name|resource
argument_list|,
name|accountsToAdd
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|groupsToInclude
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|addIncludedGroups
operator|.
name|get
argument_list|()
operator|.
name|apply
argument_list|(
name|resource
argument_list|,
name|fromGroups
argument_list|(
name|groupsToInclude
argument_list|)
argument_list|)
expr_stmt|;
name|reportGroupsAction
argument_list|(
literal|"included to"
argument_list|,
name|resource
argument_list|,
name|groupsToInclude
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|reportMembersAction (String action, GroupResource group, List<Account.Id> accountIdList)
specifier|private
name|void
name|reportMembersAction
parameter_list|(
name|String
name|action
parameter_list|,
name|GroupResource
name|group
parameter_list|,
name|List
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|accountIdList
parameter_list|)
throws|throws
name|UnsupportedEncodingException
throws|,
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Members %s group %s: %s\n"
argument_list|,
name|action
argument_list|,
name|group
operator|.
name|getName
argument_list|()
argument_list|,
name|Joiner
operator|.
name|on
argument_list|(
literal|", "
argument_list|)
operator|.
name|join
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|accountIdList
argument_list|,
operator|new
name|Function
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|firstNonNull
argument_list|(
name|accountCache
operator|.
name|get
argument_list|(
name|accountId
argument_list|)
operator|.
name|getAccount
argument_list|()
operator|.
name|getPreferredEmail
argument_list|()
argument_list|,
literal|"n/a"
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
argument_list|)
operator|.
name|getBytes
argument_list|(
name|ENC
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|reportGroupsAction (String action, GroupResource group, List<AccountGroup.UUID> groupUuidList)
specifier|private
name|void
name|reportGroupsAction
parameter_list|(
name|String
name|action
parameter_list|,
name|GroupResource
name|group
parameter_list|,
name|List
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|groupUuidList
parameter_list|)
throws|throws
name|UnsupportedEncodingException
throws|,
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Groups %s group %s: %s\n"
argument_list|,
name|action
argument_list|,
name|group
operator|.
name|getName
argument_list|()
argument_list|,
name|Joiner
operator|.
name|on
argument_list|(
literal|", "
argument_list|)
operator|.
name|join
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|groupUuidList
argument_list|,
operator|new
name|Function
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|)
block|{
return|return
name|groupCache
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
argument_list|)
operator|.
name|getBytes
argument_list|(
name|ENC
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|fromGroups (List<AccountGroup.UUID> accounts)
specifier|private
name|AddIncludedGroups
operator|.
name|Input
name|fromGroups
parameter_list|(
name|List
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|accounts
parameter_list|)
block|{
return|return
name|AddIncludedGroups
operator|.
name|Input
operator|.
name|fromGroups
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|accounts
argument_list|,
operator|new
name|Function
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|)
block|{
return|return
name|uuid
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|fromMembers (List<Account.Id> accounts)
specifier|private
name|AddMembers
operator|.
name|Input
name|fromMembers
parameter_list|(
name|List
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|accounts
parameter_list|)
block|{
return|return
name|AddMembers
operator|.
name|Input
operator|.
name|fromMembers
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|accounts
argument_list|,
operator|new
name|Function
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|Account
operator|.
name|Id
name|id
parameter_list|)
block|{
return|return
name|id
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

