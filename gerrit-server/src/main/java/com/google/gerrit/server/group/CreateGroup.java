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
DECL|package|com.google.gerrit.server.group
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|group
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
name|base
operator|.
name|Strings
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
name|TimeUtil
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
name|client
operator|.
name|ListGroupsOption
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
name|registration
operator|.
name|DynamicSet
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
name|AuthException
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
name|BadRequestException
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
name|ResourceConflictException
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
name|ResourceNotFoundException
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
name|RestModifyView
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
name|extensions
operator|.
name|restapi
operator|.
name|UnprocessableEntityException
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
name|Url
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
name|GerritPersonIdent
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
name|account
operator|.
name|CreateGroupArgs
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
name|GroupUUID
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
name|config
operator|.
name|GerritServerConfig
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
name|validators
operator|.
name|GroupCreationValidationListener
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
name|validators
operator|.
name|ValidationException
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
name|OrmDuplicateKeyException
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|assistedinject
operator|.
name|Assisted
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
name|ArrayList
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Config
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
name|PersonIdent
import|;
end_import

begin_class
annotation|@
name|RequiresCapability
argument_list|(
name|GlobalCapability
operator|.
name|CREATE_GROUP
argument_list|)
DECL|class|CreateGroup
specifier|public
class|class
name|CreateGroup
implements|implements
name|RestModifyView
argument_list|<
name|TopLevelResource
argument_list|,
name|GroupInput
argument_list|>
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (@ssisted String name)
name|CreateGroup
name|create
parameter_list|(
annotation|@
name|Assisted
name|String
name|name
parameter_list|)
function_decl|;
block|}
DECL|field|self
specifier|private
specifier|final
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|self
decl_stmt|;
DECL|field|serverIdent
specifier|private
specifier|final
name|PersonIdent
name|serverIdent
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|ReviewDb
name|db
decl_stmt|;
DECL|field|groupsUpdateProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|GroupsUpdate
argument_list|>
name|groupsUpdateProvider
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
name|GroupsCollection
name|groups
decl_stmt|;
DECL|field|json
specifier|private
specifier|final
name|GroupJson
name|json
decl_stmt|;
DECL|field|groupCreationValidationListeners
specifier|private
specifier|final
name|DynamicSet
argument_list|<
name|GroupCreationValidationListener
argument_list|>
name|groupCreationValidationListeners
decl_stmt|;
DECL|field|addMembers
specifier|private
specifier|final
name|AddMembers
name|addMembers
decl_stmt|;
DECL|field|systemGroupBackend
specifier|private
specifier|final
name|SystemGroupBackend
name|systemGroupBackend
decl_stmt|;
DECL|field|defaultVisibleToAll
specifier|private
specifier|final
name|boolean
name|defaultVisibleToAll
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
annotation|@
name|Inject
DECL|method|CreateGroup ( Provider<IdentifiedUser> self, @GerritPersonIdent PersonIdent serverIdent, ReviewDb db, @UserInitiated Provider<GroupsUpdate> groupsUpdateProvider, GroupCache groupCache, GroupsCollection groups, GroupJson json, DynamicSet<GroupCreationValidationListener> groupCreationValidationListeners, AddMembers addMembers, SystemGroupBackend systemGroupBackend, @GerritServerConfig Config cfg, @Assisted String name)
name|CreateGroup
parameter_list|(
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|self
parameter_list|,
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|serverIdent
parameter_list|,
name|ReviewDb
name|db
parameter_list|,
annotation|@
name|UserInitiated
name|Provider
argument_list|<
name|GroupsUpdate
argument_list|>
name|groupsUpdateProvider
parameter_list|,
name|GroupCache
name|groupCache
parameter_list|,
name|GroupsCollection
name|groups
parameter_list|,
name|GroupJson
name|json
parameter_list|,
name|DynamicSet
argument_list|<
name|GroupCreationValidationListener
argument_list|>
name|groupCreationValidationListeners
parameter_list|,
name|AddMembers
name|addMembers
parameter_list|,
name|SystemGroupBackend
name|systemGroupBackend
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|,
annotation|@
name|Assisted
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|self
operator|=
name|self
expr_stmt|;
name|this
operator|.
name|serverIdent
operator|=
name|serverIdent
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|groupsUpdateProvider
operator|=
name|groupsUpdateProvider
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
name|json
operator|=
name|json
expr_stmt|;
name|this
operator|.
name|groupCreationValidationListeners
operator|=
name|groupCreationValidationListeners
expr_stmt|;
name|this
operator|.
name|addMembers
operator|=
name|addMembers
expr_stmt|;
name|this
operator|.
name|systemGroupBackend
operator|=
name|systemGroupBackend
expr_stmt|;
name|this
operator|.
name|defaultVisibleToAll
operator|=
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"groups"
argument_list|,
literal|"newGroupsVisibleToAll"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|addOption (ListGroupsOption o)
specifier|public
name|CreateGroup
name|addOption
parameter_list|(
name|ListGroupsOption
name|o
parameter_list|)
block|{
name|json
operator|.
name|addOption
argument_list|(
name|o
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addOptions (Collection<ListGroupsOption> o)
specifier|public
name|CreateGroup
name|addOptions
parameter_list|(
name|Collection
argument_list|<
name|ListGroupsOption
argument_list|>
name|o
parameter_list|)
block|{
name|json
operator|.
name|addOptions
argument_list|(
name|o
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|apply (TopLevelResource resource, GroupInput input)
specifier|public
name|GroupInfo
name|apply
parameter_list|(
name|TopLevelResource
name|resource
parameter_list|,
name|GroupInput
name|input
parameter_list|)
throws|throws
name|AuthException
throws|,
name|BadRequestException
throws|,
name|UnprocessableEntityException
throws|,
name|ResourceConflictException
throws|,
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
throws|,
name|ResourceNotFoundException
block|{
if|if
condition|(
name|input
operator|==
literal|null
condition|)
block|{
name|input
operator|=
operator|new
name|GroupInput
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|input
operator|.
name|name
operator|!=
literal|null
operator|&&
operator|!
name|name
operator|.
name|equals
argument_list|(
name|input
operator|.
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"name must match URL"
argument_list|)
throw|;
block|}
name|AccountGroup
operator|.
name|Id
name|ownerId
init|=
name|owner
argument_list|(
name|input
argument_list|)
decl_stmt|;
name|CreateGroupArgs
name|args
init|=
operator|new
name|CreateGroupArgs
argument_list|()
decl_stmt|;
name|args
operator|.
name|setGroupName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|args
operator|.
name|groupDescription
operator|=
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|input
operator|.
name|description
argument_list|)
expr_stmt|;
name|args
operator|.
name|visibleToAll
operator|=
name|MoreObjects
operator|.
name|firstNonNull
argument_list|(
name|input
operator|.
name|visibleToAll
argument_list|,
name|defaultVisibleToAll
argument_list|)
expr_stmt|;
name|args
operator|.
name|ownerGroupId
operator|=
name|ownerId
expr_stmt|;
if|if
condition|(
name|input
operator|.
name|members
operator|!=
literal|null
operator|&&
operator|!
name|input
operator|.
name|members
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|members
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|nameOrEmailOrId
range|:
name|input
operator|.
name|members
control|)
block|{
name|Account
name|a
init|=
name|addMembers
operator|.
name|findAccount
argument_list|(
name|nameOrEmailOrId
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|a
operator|.
name|isActive
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|UnprocessableEntityException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Account Inactive: %s"
argument_list|,
name|nameOrEmailOrId
argument_list|)
argument_list|)
throw|;
block|}
name|members
operator|.
name|add
argument_list|(
name|a
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|args
operator|.
name|initialMembers
operator|=
name|members
expr_stmt|;
block|}
else|else
block|{
name|args
operator|.
name|initialMembers
operator|=
name|ownerId
operator|==
literal|null
condition|?
name|Collections
operator|.
name|singleton
argument_list|(
name|self
operator|.
name|get
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|)
else|:
name|Collections
operator|.
expr|<
name|Account
operator|.
name|Id
operator|>
name|emptySet
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|GroupCreationValidationListener
name|l
range|:
name|groupCreationValidationListeners
control|)
block|{
try|try
block|{
name|l
operator|.
name|validateNewGroup
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ValidationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|json
operator|.
name|format
argument_list|(
name|GroupDescriptions
operator|.
name|forAccountGroup
argument_list|(
name|createGroup
argument_list|(
name|args
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|owner (GroupInput input)
specifier|private
name|AccountGroup
operator|.
name|Id
name|owner
parameter_list|(
name|GroupInput
name|input
parameter_list|)
throws|throws
name|UnprocessableEntityException
block|{
if|if
condition|(
name|input
operator|.
name|ownerId
operator|!=
literal|null
condition|)
block|{
name|GroupDescription
operator|.
name|Internal
name|d
init|=
name|groups
operator|.
name|parseInternal
argument_list|(
name|Url
operator|.
name|decode
argument_list|(
name|input
operator|.
name|ownerId
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|d
operator|.
name|getId
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|createGroup (CreateGroupArgs createGroupArgs)
specifier|private
name|AccountGroup
name|createGroup
parameter_list|(
name|CreateGroupArgs
name|createGroupArgs
parameter_list|)
throws|throws
name|OrmException
throws|,
name|ResourceConflictException
throws|,
name|IOException
block|{
name|String
name|nameLower
init|=
name|createGroupArgs
operator|.
name|getGroupName
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|systemGroupBackend
operator|.
name|getNames
argument_list|()
control|)
block|{
if|if
condition|(
name|name
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
operator|.
name|equals
argument_list|(
name|nameLower
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"group '"
operator|+
name|name
operator|+
literal|"' already exists"
argument_list|)
throw|;
block|}
block|}
for|for
control|(
name|String
name|name
range|:
name|systemGroupBackend
operator|.
name|getReservedNames
argument_list|()
control|)
block|{
if|if
condition|(
name|name
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
operator|.
name|equals
argument_list|(
name|nameLower
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"group name '"
operator|+
name|name
operator|+
literal|"' is reserved"
argument_list|)
throw|;
block|}
block|}
name|AccountGroup
operator|.
name|Id
name|groupId
init|=
operator|new
name|AccountGroup
operator|.
name|Id
argument_list|(
name|db
operator|.
name|nextAccountGroupId
argument_list|()
argument_list|)
decl_stmt|;
name|AccountGroup
operator|.
name|UUID
name|uuid
init|=
name|GroupUUID
operator|.
name|make
argument_list|(
name|createGroupArgs
operator|.
name|getGroupName
argument_list|()
argument_list|,
name|self
operator|.
name|get
argument_list|()
operator|.
name|newCommitterIdent
argument_list|(
name|serverIdent
operator|.
name|getWhen
argument_list|()
argument_list|,
name|serverIdent
operator|.
name|getTimeZone
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|AccountGroup
name|group
init|=
operator|new
name|AccountGroup
argument_list|(
name|createGroupArgs
operator|.
name|getGroup
argument_list|()
argument_list|,
name|groupId
argument_list|,
name|uuid
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|group
operator|.
name|setVisibleToAll
argument_list|(
name|createGroupArgs
operator|.
name|visibleToAll
argument_list|)
expr_stmt|;
if|if
condition|(
name|createGroupArgs
operator|.
name|ownerGroupId
operator|!=
literal|null
condition|)
block|{
name|AccountGroup
name|ownerGroup
init|=
name|groupCache
operator|.
name|get
argument_list|(
name|createGroupArgs
operator|.
name|ownerGroupId
argument_list|)
decl_stmt|;
if|if
condition|(
name|ownerGroup
operator|!=
literal|null
condition|)
block|{
name|group
operator|.
name|setOwnerGroupUUID
argument_list|(
name|ownerGroup
operator|.
name|getGroupUUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|createGroupArgs
operator|.
name|groupDescription
operator|!=
literal|null
condition|)
block|{
name|group
operator|.
name|setDescription
argument_list|(
name|createGroupArgs
operator|.
name|groupDescription
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|groupsUpdateProvider
operator|.
name|get
argument_list|()
operator|.
name|addGroup
argument_list|(
name|db
argument_list|,
name|group
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|createGroupArgs
operator|.
name|initialMembers
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmDuplicateKeyException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"group '"
operator|+
name|createGroupArgs
operator|.
name|getGroupName
argument_list|()
operator|+
literal|"' already exists"
argument_list|)
throw|;
block|}
return|return
name|group
return|;
block|}
block|}
end_class

end_unit

