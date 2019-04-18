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
name|Lists
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
name|exceptions
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
name|client
operator|.
name|AuthType
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
name|DefaultInput
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
name|MethodNotAllowedException
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
name|RestCollectionCreateView
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
name|UserInitiated
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
name|AccountException
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
name|AccountManager
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
name|AccountResolver
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
name|AccountResolver
operator|.
name|UnresolvableAccountException
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
name|AccountState
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
name|AuthRequest
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
name|account
operator|.
name|externalids
operator|.
name|ExternalId
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
name|AuthConfig
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
name|MemberResource
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
operator|.
name|Input
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
name|Singleton
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
name|LinkedHashSet
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
name|eclipse
operator|.
name|jgit
operator|.
name|errors
operator|.
name|ConfigInvalidException
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|AddMembers
specifier|public
class|class
name|AddMembers
implements|implements
name|RestModifyView
argument_list|<
name|GroupResource
argument_list|,
name|Input
argument_list|>
block|{
DECL|class|Input
specifier|public
specifier|static
class|class
name|Input
block|{
DECL|field|_oneMember
annotation|@
name|DefaultInput
name|String
name|_oneMember
decl_stmt|;
DECL|field|members
name|List
argument_list|<
name|String
argument_list|>
name|members
decl_stmt|;
DECL|method|fromMembers (List<String> members)
specifier|public
specifier|static
name|Input
name|fromMembers
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|members
parameter_list|)
block|{
name|Input
name|in
init|=
operator|new
name|Input
argument_list|()
decl_stmt|;
name|in
operator|.
name|members
operator|=
name|members
expr_stmt|;
return|return
name|in
return|;
block|}
DECL|method|init (Input in)
specifier|static
name|Input
name|init
parameter_list|(
name|Input
name|in
parameter_list|)
block|{
if|if
condition|(
name|in
operator|==
literal|null
condition|)
block|{
name|in
operator|=
operator|new
name|Input
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|members
operator|==
literal|null
condition|)
block|{
name|in
operator|.
name|members
operator|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|in
operator|.
name|_oneMember
argument_list|)
condition|)
block|{
name|in
operator|.
name|members
operator|.
name|add
argument_list|(
name|in
operator|.
name|_oneMember
argument_list|)
expr_stmt|;
block|}
return|return
name|in
return|;
block|}
block|}
DECL|field|accountManager
specifier|private
specifier|final
name|AccountManager
name|accountManager
decl_stmt|;
DECL|field|authType
specifier|private
specifier|final
name|AuthType
name|authType
decl_stmt|;
DECL|field|accountResolver
specifier|private
specifier|final
name|AccountResolver
name|accountResolver
decl_stmt|;
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
DECL|field|infoFactory
specifier|private
specifier|final
name|AccountLoader
operator|.
name|Factory
name|infoFactory
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
annotation|@
name|Inject
DECL|method|AddMembers ( AccountManager accountManager, AuthConfig authConfig, AccountResolver accountResolver, AccountCache accountCache, AccountLoader.Factory infoFactory, @UserInitiated Provider<GroupsUpdate> groupsUpdateProvider)
name|AddMembers
parameter_list|(
name|AccountManager
name|accountManager
parameter_list|,
name|AuthConfig
name|authConfig
parameter_list|,
name|AccountResolver
name|accountResolver
parameter_list|,
name|AccountCache
name|accountCache
parameter_list|,
name|AccountLoader
operator|.
name|Factory
name|infoFactory
parameter_list|,
annotation|@
name|UserInitiated
name|Provider
argument_list|<
name|GroupsUpdate
argument_list|>
name|groupsUpdateProvider
parameter_list|)
block|{
name|this
operator|.
name|accountManager
operator|=
name|accountManager
expr_stmt|;
name|this
operator|.
name|authType
operator|=
name|authConfig
operator|.
name|getAuthType
argument_list|()
expr_stmt|;
name|this
operator|.
name|accountResolver
operator|=
name|accountResolver
expr_stmt|;
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
name|this
operator|.
name|infoFactory
operator|=
name|infoFactory
expr_stmt|;
name|this
operator|.
name|groupsUpdateProvider
operator|=
name|groupsUpdateProvider
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (GroupResource resource, Input input)
specifier|public
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|apply
parameter_list|(
name|GroupResource
name|resource
parameter_list|,
name|Input
name|input
parameter_list|)
throws|throws
name|AuthException
throws|,
name|NotInternalGroupException
throws|,
name|UnprocessableEntityException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
throws|,
name|ResourceNotFoundException
throws|,
name|PermissionBackendException
block|{
name|GroupDescription
operator|.
name|Internal
name|internalGroup
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
name|input
operator|=
name|Input
operator|.
name|init
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|GroupControl
name|control
init|=
name|resource
operator|.
name|getControl
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|control
operator|.
name|canAddMember
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"Cannot add members to group "
operator|+
name|internalGroup
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|newMemberIds
init|=
operator|new
name|LinkedHashSet
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
name|newMemberIds
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
name|AccountGroup
operator|.
name|UUID
name|groupUuid
init|=
name|internalGroup
operator|.
name|getGroupUUID
argument_list|()
decl_stmt|;
try|try
block|{
name|addMembers
argument_list|(
name|groupUuid
argument_list|,
name|newMemberIds
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchGroupException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Group %s not found"
argument_list|,
name|groupUuid
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|toAccountInfoList
argument_list|(
name|newMemberIds
argument_list|)
return|;
block|}
DECL|method|findAccount (String nameOrEmailOrId)
name|Account
name|findAccount
parameter_list|(
name|String
name|nameOrEmailOrId
parameter_list|)
throws|throws
name|UnprocessableEntityException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|AccountResolver
operator|.
name|Result
name|result
init|=
name|accountResolver
operator|.
name|resolve
argument_list|(
name|nameOrEmailOrId
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|result
operator|.
name|asUnique
argument_list|()
operator|.
name|getAccount
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|UnresolvableAccountException
name|e
parameter_list|)
block|{
switch|switch
condition|(
name|authType
condition|)
block|{
case|case
name|HTTP_LDAP
case|:
case|case
name|CLIENT_SSL_CERT_LDAP
case|:
case|case
name|LDAP
case|:
if|if
condition|(
operator|!
name|e
operator|.
name|isSelf
argument_list|()
operator|&&
name|result
operator|.
name|asList
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Account does not exist, try to create it. This may leak account existence, since we
comment|// can't distinguish between a nonexistent account and one that the caller can't see.
name|Optional
argument_list|<
name|Account
argument_list|>
name|a
init|=
name|createAccountByLdap
argument_list|(
name|nameOrEmailOrId
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
name|a
operator|.
name|get
argument_list|()
return|;
block|}
block|}
break|break;
case|case
name|CUSTOM_EXTENSION
case|:
case|case
name|DEVELOPMENT_BECOME_ANY_ACCOUNT
case|:
case|case
name|HTTP
case|:
case|case
name|LDAP_BIND
case|:
case|case
name|OAUTH
case|:
case|case
name|OPENID
case|:
case|case
name|OPENID_SSO
case|:
default|default:
block|}
throw|throw
name|e
throw|;
block|}
block|}
DECL|method|addMembers (AccountGroup.UUID groupUuid, Set<Account.Id> newMemberIds)
specifier|public
name|void
name|addMembers
parameter_list|(
name|AccountGroup
operator|.
name|UUID
name|groupUuid
parameter_list|,
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|newMemberIds
parameter_list|)
throws|throws
name|IOException
throws|,
name|NoSuchGroupException
throws|,
name|ConfigInvalidException
block|{
name|InternalGroupUpdate
name|groupUpdate
init|=
name|InternalGroupUpdate
operator|.
name|builder
argument_list|()
operator|.
name|setMemberModification
argument_list|(
name|memberIds
lambda|->
name|Sets
operator|.
name|union
argument_list|(
name|memberIds
argument_list|,
name|newMemberIds
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|groupsUpdateProvider
operator|.
name|get
argument_list|()
operator|.
name|updateGroup
argument_list|(
name|groupUuid
argument_list|,
name|groupUpdate
argument_list|)
expr_stmt|;
block|}
DECL|method|createAccountByLdap (String user)
specifier|private
name|Optional
argument_list|<
name|Account
argument_list|>
name|createAccountByLdap
parameter_list|(
name|String
name|user
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|ExternalId
operator|.
name|isValidUsername
argument_list|(
name|user
argument_list|)
condition|)
block|{
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
try|try
block|{
name|AuthRequest
name|req
init|=
name|AuthRequest
operator|.
name|forUser
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|req
operator|.
name|setSkipAuthentication
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|accountCache
operator|.
name|get
argument_list|(
name|accountManager
operator|.
name|authenticate
argument_list|(
name|req
argument_list|)
operator|.
name|getAccountId
argument_list|()
argument_list|)
operator|.
name|map
argument_list|(
name|AccountState
operator|::
name|getAccount
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|AccountException
name|e
parameter_list|)
block|{
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
block|}
DECL|method|toAccountInfoList (Set<Account.Id> accountIds)
specifier|private
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|toAccountInfoList
parameter_list|(
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|accountIds
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|AccountLoader
name|loader
init|=
name|infoFactory
operator|.
name|create
argument_list|(
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|Account
operator|.
name|Id
name|accId
range|:
name|accountIds
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|loader
operator|.
name|get
argument_list|(
name|accId
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|loader
operator|.
name|fill
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Singleton
DECL|class|CreateMember
specifier|public
specifier|static
class|class
name|CreateMember
implements|implements
name|RestCollectionCreateView
argument_list|<
name|GroupResource
argument_list|,
name|MemberResource
argument_list|,
name|Input
argument_list|>
block|{
DECL|field|put
specifier|private
specifier|final
name|AddMembers
name|put
decl_stmt|;
annotation|@
name|Inject
DECL|method|CreateMember (AddMembers put)
specifier|public
name|CreateMember
parameter_list|(
name|AddMembers
name|put
parameter_list|)
block|{
name|this
operator|.
name|put
operator|=
name|put
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (GroupResource resource, IdString id, Input input)
specifier|public
name|AccountInfo
name|apply
parameter_list|(
name|GroupResource
name|resource
parameter_list|,
name|IdString
name|id
parameter_list|,
name|Input
name|input
parameter_list|)
throws|throws
name|AuthException
throws|,
name|MethodNotAllowedException
throws|,
name|ResourceNotFoundException
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
name|in
init|=
operator|new
name|AddMembers
operator|.
name|Input
argument_list|()
decl_stmt|;
name|in
operator|.
name|_oneMember
operator|=
name|id
operator|.
name|get
argument_list|()
expr_stmt|;
try|try
block|{
name|List
argument_list|<
name|AccountInfo
argument_list|>
name|list
init|=
name|put
operator|.
name|apply
argument_list|(
name|resource
argument_list|,
name|in
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
catch|catch
parameter_list|(
name|UnprocessableEntityException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|id
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Singleton
DECL|class|UpdateMember
specifier|public
specifier|static
class|class
name|UpdateMember
implements|implements
name|RestModifyView
argument_list|<
name|MemberResource
argument_list|,
name|Input
argument_list|>
block|{
DECL|field|get
specifier|private
specifier|final
name|GetMember
name|get
decl_stmt|;
annotation|@
name|Inject
DECL|method|UpdateMember (GetMember get)
specifier|public
name|UpdateMember
parameter_list|(
name|GetMember
name|get
parameter_list|)
block|{
name|this
operator|.
name|get
operator|=
name|get
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (MemberResource resource, Input input)
specifier|public
name|AccountInfo
name|apply
parameter_list|(
name|MemberResource
name|resource
parameter_list|,
name|Input
name|input
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
comment|// Do nothing, the user is already a member.
return|return
name|get
operator|.
name|apply
argument_list|(
name|resource
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

