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
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|joining
import|;
end_import

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
name|base
operator|.
name|MoreObjects
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
name|Streams
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
name|restapi
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
name|restapi
operator|.
name|group
operator|.
name|AddSubgroups
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
name|restapi
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
name|restapi
operator|.
name|group
operator|.
name|DeleteSubgroups
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
name|restapi
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
name|ArrayList
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
literal|"Modify members of specific group or number of groups"
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
operator|new
name|ArrayList
argument_list|<>
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
operator|new
name|ArrayList
argument_list|<>
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
operator|new
name|ArrayList
argument_list|<>
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
operator|new
name|ArrayList
argument_list|<>
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
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|addMembers
annotation|@
name|Inject
specifier|private
name|AddMembers
name|addMembers
decl_stmt|;
DECL|field|deleteMembers
annotation|@
name|Inject
specifier|private
name|DeleteMembers
name|deleteMembers
decl_stmt|;
DECL|field|addSubgroups
annotation|@
name|Inject
specifier|private
name|AddSubgroups
name|addSubgroups
decl_stmt|;
DECL|field|deleteSubgroups
annotation|@
name|Inject
specifier|private
name|DeleteSubgroups
name|deleteSubgroups
decl_stmt|;
DECL|field|groupsCollection
annotation|@
name|Inject
specifier|private
name|GroupsCollection
name|groupsCollection
decl_stmt|;
DECL|field|groupCache
annotation|@
name|Inject
specifier|private
name|GroupCache
name|groupCache
decl_stmt|;
DECL|field|accountCache
annotation|@
name|Inject
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
try|try
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
name|deleteSubgroups
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
name|addSubgroups
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
catch|catch
parameter_list|(
name|RestApiException
name|e
parameter_list|)
block|{
throw|throw
name|die
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|reportMembersAction ( String action, GroupResource group, List<Account.Id> accountIdList)
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
name|String
name|names
init|=
name|accountIdList
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|accountId
lambda|->
name|MoreObjects
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
argument_list|)
operator|.
name|collect
argument_list|(
name|joining
argument_list|(
literal|", "
argument_list|)
argument_list|)
decl_stmt|;
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
name|names
argument_list|)
operator|.
name|getBytes
argument_list|(
name|ENC
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|reportGroupsAction ( String action, GroupResource group, List<AccountGroup.UUID> groupUuidList)
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
name|String
name|names
init|=
name|groupUuidList
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|uuid
lambda|->
name|groupCache
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
operator|.
name|map
argument_list|(
name|InternalGroup
operator|::
name|getName
argument_list|)
argument_list|)
operator|.
name|flatMap
argument_list|(
name|Streams
operator|::
name|stream
argument_list|)
operator|.
name|collect
argument_list|(
name|joining
argument_list|(
literal|", "
argument_list|)
argument_list|)
decl_stmt|;
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
name|names
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
name|AddSubgroups
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
name|AddSubgroups
operator|.
name|Input
operator|.
name|fromGroups
argument_list|(
name|accounts
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|Object
operator|::
name|toString
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
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
name|accounts
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|Object
operator|::
name|toString
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

