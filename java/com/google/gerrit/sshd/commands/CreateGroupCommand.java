begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
name|toList
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
name|GlobalCapability
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
name|entities
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
name|entities
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
name|extensions
operator|.
name|annotations
operator|.
name|RequiresCapability
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
name|groups
operator|.
name|GroupInput
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
name|permissions
operator|.
name|PermissionBackendException
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
name|CreateGroup
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
name|Set
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

begin_comment
comment|/**  * Creates a new group.  *  *<p>Optionally, puts an initial set of user in the newly created group.  */
end_comment

begin_class
annotation|@
name|RequiresCapability
argument_list|(
name|GlobalCapability
operator|.
name|CREATE_GROUP
argument_list|)
annotation|@
name|CommandMetaData
argument_list|(
name|name
operator|=
literal|"create-group"
argument_list|,
name|description
operator|=
literal|"Create a new account group"
argument_list|)
DECL|class|CreateGroupCommand
specifier|final
class|class
name|CreateGroupCommand
extends|extends
name|SshCommand
block|{
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--owner"
argument_list|,
name|aliases
operator|=
block|{
literal|"-o"
block|}
argument_list|,
name|metaVar
operator|=
literal|"GROUP"
argument_list|,
name|usage
operator|=
literal|"owning group, if not specified the group will be self-owning"
argument_list|)
DECL|field|ownerGroupId
specifier|private
name|AccountGroup
operator|.
name|Id
name|ownerGroupId
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--description"
argument_list|,
name|aliases
operator|=
block|{
literal|"-d"
block|}
argument_list|,
name|metaVar
operator|=
literal|"DESC"
argument_list|,
name|usage
operator|=
literal|"description of group"
argument_list|)
DECL|field|groupDescription
specifier|private
name|String
name|groupDescription
init|=
literal|""
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
name|metaVar
operator|=
literal|"GROUP"
argument_list|,
name|usage
operator|=
literal|"name of group to be created"
argument_list|)
DECL|field|groupName
specifier|private
name|String
name|groupName
decl_stmt|;
DECL|field|initialMembers
specifier|private
specifier|final
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|initialMembers
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--member"
argument_list|,
name|aliases
operator|=
block|{
literal|"-m"
block|}
argument_list|,
name|metaVar
operator|=
literal|"USERNAME"
argument_list|,
name|usage
operator|=
literal|"initial set of users to become members of the group"
argument_list|)
DECL|method|addMember (Account.Id id)
name|void
name|addMember
parameter_list|(
name|Account
operator|.
name|Id
name|id
parameter_list|)
block|{
name|initialMembers
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--visible-to-all"
argument_list|,
name|usage
operator|=
literal|"to make the group visible to all registered users"
argument_list|)
DECL|field|visibleToAll
specifier|private
name|boolean
name|visibleToAll
decl_stmt|;
DECL|field|initialGroups
specifier|private
specifier|final
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|initialGroups
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--group"
argument_list|,
name|aliases
operator|=
literal|"-g"
argument_list|,
name|metaVar
operator|=
literal|"GROUP"
argument_list|,
name|usage
operator|=
literal|"initial set of groups to be included in the group"
argument_list|)
DECL|method|addGroup (AccountGroup.UUID id)
name|void
name|addGroup
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|id
parameter_list|)
block|{
name|initialGroups
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
DECL|field|createGroup
annotation|@
name|Inject
specifier|private
name|CreateGroup
name|createGroup
decl_stmt|;
DECL|field|groups
annotation|@
name|Inject
specifier|private
name|GroupsCollection
name|groups
decl_stmt|;
DECL|field|addMembers
annotation|@
name|Inject
specifier|private
name|AddMembers
name|addMembers
decl_stmt|;
DECL|field|addSubgroups
annotation|@
name|Inject
specifier|private
name|AddSubgroups
name|addSubgroups
decl_stmt|;
annotation|@
name|Override
DECL|method|run ()
specifier|protected
name|void
name|run
parameter_list|()
throws|throws
name|Failure
throws|,
name|IOException
throws|,
name|ConfigInvalidException
throws|,
name|PermissionBackendException
block|{
try|try
block|{
name|GroupResource
name|rsrc
init|=
name|createGroup
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|initialMembers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|addMembers
argument_list|(
name|rsrc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|initialGroups
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|addSubgroups
argument_list|(
name|rsrc
argument_list|)
expr_stmt|;
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
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|Failure
argument_list|(
literal|1
argument_list|,
literal|"unavailable"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|createGroup ()
specifier|private
name|GroupResource
name|createGroup
parameter_list|()
throws|throws
name|RestApiException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
throws|,
name|PermissionBackendException
block|{
name|GroupInput
name|input
init|=
operator|new
name|GroupInput
argument_list|()
decl_stmt|;
name|input
operator|.
name|description
operator|=
name|groupDescription
expr_stmt|;
name|input
operator|.
name|visibleToAll
operator|=
name|visibleToAll
expr_stmt|;
if|if
condition|(
name|ownerGroupId
operator|!=
literal|null
condition|)
block|{
name|input
operator|.
name|ownerId
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|ownerGroupId
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|GroupInfo
name|group
init|=
name|createGroup
operator|.
name|apply
argument_list|(
name|TopLevelResource
operator|.
name|INSTANCE
argument_list|,
name|IdString
operator|.
name|fromDecoded
argument_list|(
name|groupName
argument_list|)
argument_list|,
name|input
argument_list|)
operator|.
name|value
argument_list|()
decl_stmt|;
return|return
name|groups
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
name|group
operator|.
name|id
argument_list|)
argument_list|)
return|;
block|}
DECL|method|addMembers (GroupResource rsrc)
specifier|private
name|void
name|addMembers
parameter_list|(
name|GroupResource
name|rsrc
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
throws|,
name|PermissionBackendException
block|{
name|AddMembers
operator|.
name|Input
name|input
init|=
name|AddMembers
operator|.
name|Input
operator|.
name|fromMembers
argument_list|(
name|initialMembers
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
decl_stmt|;
name|addMembers
operator|.
name|apply
argument_list|(
name|rsrc
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
DECL|method|addSubgroups (GroupResource rsrc)
specifier|private
name|void
name|addSubgroups
parameter_list|(
name|GroupResource
name|rsrc
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
throws|,
name|PermissionBackendException
block|{
name|AddSubgroups
operator|.
name|Input
name|input
init|=
name|AddSubgroups
operator|.
name|Input
operator|.
name|fromGroups
argument_list|(
name|initialGroups
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|AccountGroup
operator|.
name|UUID
operator|::
name|get
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|addSubgroups
operator|.
name|apply
argument_list|(
name|rsrc
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

