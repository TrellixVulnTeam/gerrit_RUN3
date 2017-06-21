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
name|gerrit
operator|.
name|audit
operator|.
name|AuditService
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
name|AccessSection
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
name|Permission
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
name|NameAlreadyUsedException
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
name|AccountFieldName
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
name|client
operator|.
name|AccountGroupMember
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
name|account
operator|.
name|externalids
operator|.
name|ExternalIds
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
name|ExternalIdsUpdate
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
name|project
operator|.
name|ProjectCache
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
name|account
operator|.
name|InternalAccountQuery
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
name|Optional
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/** Tracks authentication related details for user accounts. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|AccountManager
specifier|public
class|class
name|AccountManager
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AccountManager
operator|.
name|class
argument_list|)
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
DECL|field|accounts
specifier|private
specifier|final
name|Accounts
name|accounts
decl_stmt|;
DECL|field|accountsUpdateFactory
specifier|private
specifier|final
name|AccountsUpdate
operator|.
name|Server
name|accountsUpdateFactory
decl_stmt|;
DECL|field|byIdCache
specifier|private
specifier|final
name|AccountCache
name|byIdCache
decl_stmt|;
DECL|field|byEmailCache
specifier|private
specifier|final
name|AccountByEmailCache
name|byEmailCache
decl_stmt|;
DECL|field|realm
specifier|private
specifier|final
name|Realm
name|realm
decl_stmt|;
DECL|field|userFactory
specifier|private
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
decl_stmt|;
DECL|field|changeUserNameFactory
specifier|private
specifier|final
name|ChangeUserName
operator|.
name|Factory
name|changeUserNameFactory
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|awaitsFirstAccountCheck
specifier|private
specifier|final
name|AtomicBoolean
name|awaitsFirstAccountCheck
decl_stmt|;
DECL|field|auditService
specifier|private
specifier|final
name|AuditService
name|auditService
decl_stmt|;
DECL|field|accountQueryProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|InternalAccountQuery
argument_list|>
name|accountQueryProvider
decl_stmt|;
DECL|field|externalIds
specifier|private
specifier|final
name|ExternalIds
name|externalIds
decl_stmt|;
DECL|field|externalIdsUpdateFactory
specifier|private
specifier|final
name|ExternalIdsUpdate
operator|.
name|Server
name|externalIdsUpdateFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|AccountManager ( SchemaFactory<ReviewDb> schema, @GerritServerConfig Config cfg, Accounts accounts, AccountsUpdate.Server accountsUpdateFactory, AccountCache byIdCache, AccountByEmailCache byEmailCache, Realm accountMapper, IdentifiedUser.GenericFactory userFactory, ChangeUserName.Factory changeUserNameFactory, ProjectCache projectCache, AuditService auditService, Provider<InternalAccountQuery> accountQueryProvider, ExternalIds externalIds, ExternalIdsUpdate.Server externalIdsUpdateFactory)
name|AccountManager
parameter_list|(
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|,
name|Accounts
name|accounts
parameter_list|,
name|AccountsUpdate
operator|.
name|Server
name|accountsUpdateFactory
parameter_list|,
name|AccountCache
name|byIdCache
parameter_list|,
name|AccountByEmailCache
name|byEmailCache
parameter_list|,
name|Realm
name|accountMapper
parameter_list|,
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
parameter_list|,
name|ChangeUserName
operator|.
name|Factory
name|changeUserNameFactory
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
name|AuditService
name|auditService
parameter_list|,
name|Provider
argument_list|<
name|InternalAccountQuery
argument_list|>
name|accountQueryProvider
parameter_list|,
name|ExternalIds
name|externalIds
parameter_list|,
name|ExternalIdsUpdate
operator|.
name|Server
name|externalIdsUpdateFactory
parameter_list|)
block|{
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|accounts
operator|=
name|accounts
expr_stmt|;
name|this
operator|.
name|accountsUpdateFactory
operator|=
name|accountsUpdateFactory
expr_stmt|;
name|this
operator|.
name|byIdCache
operator|=
name|byIdCache
expr_stmt|;
name|this
operator|.
name|byEmailCache
operator|=
name|byEmailCache
expr_stmt|;
name|this
operator|.
name|realm
operator|=
name|accountMapper
expr_stmt|;
name|this
operator|.
name|userFactory
operator|=
name|userFactory
expr_stmt|;
name|this
operator|.
name|changeUserNameFactory
operator|=
name|changeUserNameFactory
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|awaitsFirstAccountCheck
operator|=
operator|new
name|AtomicBoolean
argument_list|(
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"capability"
argument_list|,
literal|"makeFirstUserAdmin"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|auditService
operator|=
name|auditService
expr_stmt|;
name|this
operator|.
name|accountQueryProvider
operator|=
name|accountQueryProvider
expr_stmt|;
name|this
operator|.
name|externalIds
operator|=
name|externalIds
expr_stmt|;
name|this
operator|.
name|externalIdsUpdateFactory
operator|=
name|externalIdsUpdateFactory
expr_stmt|;
block|}
comment|/** @return user identified by this external identity string */
DECL|method|lookup (String externalId)
specifier|public
name|Optional
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|lookup
parameter_list|(
name|String
name|externalId
parameter_list|)
throws|throws
name|AccountException
block|{
try|try
block|{
name|AccountState
name|accountState
init|=
name|accountQueryProvider
operator|.
name|get
argument_list|()
operator|.
name|oneByExternalId
argument_list|(
name|externalId
argument_list|)
decl_stmt|;
return|return
name|accountState
operator|!=
literal|null
condition|?
name|Optional
operator|.
name|of
argument_list|(
name|accountState
operator|.
name|getAccount
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
else|:
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AccountException
argument_list|(
literal|"Cannot lookup account "
operator|+
name|externalId
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Authenticate the user, potentially creating a new account if they are new.    *    * @param who identity of the user, with any details we received about them.    * @return the result of authenticating the user.    * @throws AccountException the account does not exist, and cannot be created, or exists, but    *     cannot be located, or is inactive.    */
DECL|method|authenticate (AuthRequest who)
specifier|public
name|AuthResult
name|authenticate
parameter_list|(
name|AuthRequest
name|who
parameter_list|)
throws|throws
name|AccountException
throws|,
name|IOException
block|{
name|who
operator|=
name|realm
operator|.
name|authenticate
argument_list|(
name|who
argument_list|)
expr_stmt|;
try|try
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
name|ExternalId
name|id
init|=
name|externalIds
operator|.
name|get
argument_list|(
name|who
operator|.
name|getExternalIdKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
comment|// New account, automatically create and return.
comment|//
return|return
name|create
argument_list|(
name|db
argument_list|,
name|who
argument_list|)
return|;
block|}
comment|// Account exists
name|Account
name|act
init|=
name|byIdCache
operator|.
name|get
argument_list|(
name|id
operator|.
name|accountId
argument_list|()
argument_list|)
operator|.
name|getAccount
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|act
operator|.
name|isActive
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AccountException
argument_list|(
literal|"Authentication error, account inactive"
argument_list|)
throw|;
block|}
comment|// return the identity to the caller.
name|update
argument_list|(
name|db
argument_list|,
name|who
argument_list|,
name|id
argument_list|)
expr_stmt|;
return|return
operator|new
name|AuthResult
argument_list|(
name|id
operator|.
name|accountId
argument_list|()
argument_list|,
name|who
operator|.
name|getExternalIdKey
argument_list|()
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|ConfigInvalidException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AccountException
argument_list|(
literal|"Authentication error"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|update (ReviewDb db, AuthRequest who, ExternalId extId)
specifier|private
name|void
name|update
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|AuthRequest
name|who
parameter_list|,
name|ExternalId
name|extId
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|IdentifiedUser
name|user
init|=
name|userFactory
operator|.
name|create
argument_list|(
name|extId
operator|.
name|accountId
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Consumer
argument_list|<
name|Account
argument_list|>
argument_list|>
name|accountUpdates
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// If the email address was modified by the authentication provider,
comment|// update our records to match the changed email.
comment|//
name|String
name|newEmail
init|=
name|who
operator|.
name|getEmailAddress
argument_list|()
decl_stmt|;
name|String
name|oldEmail
init|=
name|extId
operator|.
name|email
argument_list|()
decl_stmt|;
if|if
condition|(
name|newEmail
operator|!=
literal|null
operator|&&
operator|!
name|newEmail
operator|.
name|equals
argument_list|(
name|oldEmail
argument_list|)
condition|)
block|{
if|if
condition|(
name|oldEmail
operator|!=
literal|null
operator|&&
name|oldEmail
operator|.
name|equals
argument_list|(
name|user
operator|.
name|getAccount
argument_list|()
operator|.
name|getPreferredEmail
argument_list|()
argument_list|)
condition|)
block|{
name|accountUpdates
operator|.
name|add
argument_list|(
name|a
lambda|->
name|a
operator|.
name|setPreferredEmail
argument_list|(
name|newEmail
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|externalIdsUpdateFactory
operator|.
name|create
argument_list|()
operator|.
name|replace
argument_list|(
name|extId
argument_list|,
name|ExternalId
operator|.
name|create
argument_list|(
name|extId
operator|.
name|key
argument_list|()
argument_list|,
name|extId
operator|.
name|accountId
argument_list|()
argument_list|,
name|newEmail
argument_list|,
name|extId
operator|.
name|password
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|realm
operator|.
name|allowsEdit
argument_list|(
name|AccountFieldName
operator|.
name|FULL_NAME
argument_list|)
operator|&&
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|who
operator|.
name|getDisplayName
argument_list|()
argument_list|)
operator|&&
operator|!
name|eq
argument_list|(
name|user
operator|.
name|getAccount
argument_list|()
operator|.
name|getFullName
argument_list|()
argument_list|,
name|who
operator|.
name|getDisplayName
argument_list|()
argument_list|)
condition|)
block|{
name|accountUpdates
operator|.
name|add
argument_list|(
name|a
lambda|->
name|a
operator|.
name|setFullName
argument_list|(
name|who
operator|.
name|getDisplayName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|realm
operator|.
name|allowsEdit
argument_list|(
name|AccountFieldName
operator|.
name|USER_NAME
argument_list|)
operator|&&
name|who
operator|.
name|getUserName
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|eq
argument_list|(
name|user
operator|.
name|getUserName
argument_list|()
argument_list|,
name|who
operator|.
name|getUserName
argument_list|()
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Not changing already set username %s to %s"
argument_list|,
name|user
operator|.
name|getUserName
argument_list|()
argument_list|,
name|who
operator|.
name|getUserName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|accountUpdates
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Account
name|account
init|=
name|accountsUpdateFactory
operator|.
name|create
argument_list|()
operator|.
name|update
argument_list|(
name|db
argument_list|,
name|user
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|accountUpdates
argument_list|)
decl_stmt|;
if|if
condition|(
name|account
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"Account "
operator|+
name|user
operator|.
name|getAccountId
argument_list|()
operator|+
literal|" has been deleted"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|newEmail
operator|!=
literal|null
operator|&&
operator|!
name|newEmail
operator|.
name|equals
argument_list|(
name|oldEmail
argument_list|)
condition|)
block|{
name|byEmailCache
operator|.
name|evict
argument_list|(
name|oldEmail
argument_list|)
expr_stmt|;
name|byEmailCache
operator|.
name|evict
argument_list|(
name|newEmail
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|eq (String a, String b)
specifier|private
specifier|static
name|boolean
name|eq
parameter_list|(
name|String
name|a
parameter_list|,
name|String
name|b
parameter_list|)
block|{
return|return
operator|(
name|a
operator|==
literal|null
operator|&&
name|b
operator|==
literal|null
operator|)
operator|||
operator|(
name|a
operator|!=
literal|null
operator|&&
name|a
operator|.
name|equals
argument_list|(
name|b
argument_list|)
operator|)
return|;
block|}
DECL|method|create (ReviewDb db, AuthRequest who)
specifier|private
name|AuthResult
name|create
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|AuthRequest
name|who
parameter_list|)
throws|throws
name|OrmException
throws|,
name|AccountException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|Account
operator|.
name|Id
name|newId
init|=
operator|new
name|Account
operator|.
name|Id
argument_list|(
name|db
operator|.
name|nextAccountId
argument_list|()
argument_list|)
decl_stmt|;
name|ExternalId
name|extId
init|=
name|ExternalId
operator|.
name|createWithEmail
argument_list|(
name|who
operator|.
name|getExternalIdKey
argument_list|()
argument_list|,
name|newId
argument_list|,
name|who
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|isFirstAccount
init|=
name|awaitsFirstAccountCheck
operator|.
name|getAndSet
argument_list|(
literal|false
argument_list|)
operator|&&
operator|!
name|accounts
operator|.
name|hasAnyAccount
argument_list|()
decl_stmt|;
name|Account
name|account
decl_stmt|;
try|try
block|{
name|AccountsUpdate
name|accountsUpdate
init|=
name|accountsUpdateFactory
operator|.
name|create
argument_list|()
decl_stmt|;
name|account
operator|=
name|accountsUpdate
operator|.
name|insert
argument_list|(
name|db
argument_list|,
name|newId
argument_list|,
name|a
lambda|->
block|{
name|a
operator|.
name|setFullName
argument_list|(
name|who
operator|.
name|getDisplayName
argument_list|()
argument_list|)
expr_stmt|;
name|a
operator|.
name|setPreferredEmail
argument_list|(
name|extId
operator|.
name|email
argument_list|()
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|ExternalId
name|existingExtId
init|=
name|externalIds
operator|.
name|get
argument_list|(
name|extId
operator|.
name|key
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|existingExtId
operator|!=
literal|null
operator|&&
operator|!
name|existingExtId
operator|.
name|accountId
argument_list|()
operator|.
name|equals
argument_list|(
name|extId
operator|.
name|accountId
argument_list|()
argument_list|)
condition|)
block|{
comment|// external ID is assigned to another account, do not overwrite
name|accountsUpdate
operator|.
name|delete
argument_list|(
name|db
argument_list|,
name|account
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|AccountException
argument_list|(
literal|"Cannot assign external ID \""
operator|+
name|extId
operator|.
name|key
argument_list|()
operator|.
name|get
argument_list|()
operator|+
literal|"\" to account "
operator|+
name|newId
operator|+
literal|"; external ID already in use."
argument_list|)
throw|;
block|}
name|externalIdsUpdateFactory
operator|.
name|create
argument_list|()
operator|.
name|upsert
argument_list|(
name|extId
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// If adding the account failed, it may be that it actually was the
comment|// first account. So we reset the 'check for first account'-guard, as
comment|// otherwise the first account would not get administration permissions.
name|awaitsFirstAccountCheck
operator|.
name|set
argument_list|(
name|isFirstAccount
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isFirstAccount
condition|)
block|{
comment|// This is the first user account on our site. Assume this user
comment|// is going to be the site's administrator and just make them that
comment|// to bootstrap the authentication database.
comment|//
name|Permission
name|admin
init|=
name|projectCache
operator|.
name|getAllProjects
argument_list|()
operator|.
name|getConfig
argument_list|()
operator|.
name|getAccessSection
argument_list|(
name|AccessSection
operator|.
name|GLOBAL_CAPABILITIES
argument_list|)
operator|.
name|getPermission
argument_list|(
name|GlobalCapability
operator|.
name|ADMINISTRATE_SERVER
argument_list|)
decl_stmt|;
name|AccountGroup
operator|.
name|UUID
name|uuid
init|=
name|admin
operator|.
name|getRules
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getGroup
argument_list|()
operator|.
name|getUUID
argument_list|()
decl_stmt|;
name|AccountGroup
name|g
init|=
name|db
operator|.
name|accountGroups
argument_list|()
operator|.
name|byUUID
argument_list|(
name|uuid
argument_list|)
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|AccountGroup
operator|.
name|Id
name|adminId
init|=
name|g
operator|.
name|getId
argument_list|()
decl_stmt|;
name|AccountGroupMember
name|m
init|=
operator|new
name|AccountGroupMember
argument_list|(
operator|new
name|AccountGroupMember
operator|.
name|Key
argument_list|(
name|newId
argument_list|,
name|adminId
argument_list|)
argument_list|)
decl_stmt|;
name|auditService
operator|.
name|dispatchAddAccountsToGroup
argument_list|(
name|newId
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|m
argument_list|)
argument_list|)
expr_stmt|;
name|db
operator|.
name|accountGroupMembers
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|m
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|who
operator|.
name|getUserName
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// Only set if the name hasn't been used yet, but was given to us.
comment|//
name|IdentifiedUser
name|user
init|=
name|userFactory
operator|.
name|create
argument_list|(
name|newId
argument_list|)
decl_stmt|;
try|try
block|{
name|changeUserNameFactory
operator|.
name|create
argument_list|(
name|user
argument_list|,
name|who
operator|.
name|getUserName
argument_list|()
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NameAlreadyUsedException
name|e
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Cannot assign user name \""
operator|+
name|who
operator|.
name|getUserName
argument_list|()
operator|+
literal|"\" to account "
operator|+
name|newId
operator|+
literal|"; name already in use."
decl_stmt|;
name|handleSettingUserNameFailure
argument_list|(
name|db
argument_list|,
name|account
argument_list|,
name|extId
argument_list|,
name|message
argument_list|,
name|e
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidUserNameException
name|e
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Cannot assign user name \""
operator|+
name|who
operator|.
name|getUserName
argument_list|()
operator|+
literal|"\" to account "
operator|+
name|newId
operator|+
literal|"; name does not conform."
decl_stmt|;
name|handleSettingUserNameFailure
argument_list|(
name|db
argument_list|,
name|account
argument_list|,
name|extId
argument_list|,
name|message
argument_list|,
name|e
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Cannot assign user name"
decl_stmt|;
name|handleSettingUserNameFailure
argument_list|(
name|db
argument_list|,
name|account
argument_list|,
name|extId
argument_list|,
name|message
argument_list|,
name|e
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
name|byEmailCache
operator|.
name|evict
argument_list|(
name|account
operator|.
name|getPreferredEmail
argument_list|()
argument_list|)
expr_stmt|;
name|realm
operator|.
name|onCreateAccount
argument_list|(
name|who
argument_list|,
name|account
argument_list|)
expr_stmt|;
return|return
operator|new
name|AuthResult
argument_list|(
name|newId
argument_list|,
name|extId
operator|.
name|key
argument_list|()
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**    * This method handles an exception that occurred during the setting of the user name for a newly    * created account. If the realm does not allow the user to set a user name manually this method    * deletes the newly created account and throws an {@link AccountUserNameException}. In any case    * the error message is logged.    *    * @param db the database    * @param account the newly created account    * @param extId the newly created external id    * @param errorMessage the error message    * @param e the exception that occurred during the setting of the user name for the new account    * @param logException flag that decides whether the exception should be included into the log    * @throws AccountUserNameException thrown if the realm does not allow the user to manually set    *     the user name    * @throws OrmException thrown if cleaning the database failed    */
DECL|method|handleSettingUserNameFailure ( ReviewDb db, Account account, ExternalId extId, String errorMessage, Exception e, boolean logException)
specifier|private
name|void
name|handleSettingUserNameFailure
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Account
name|account
parameter_list|,
name|ExternalId
name|extId
parameter_list|,
name|String
name|errorMessage
parameter_list|,
name|Exception
name|e
parameter_list|,
name|boolean
name|logException
parameter_list|)
throws|throws
name|AccountUserNameException
throws|,
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
if|if
condition|(
name|logException
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
name|errorMessage
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|error
argument_list|(
name|errorMessage
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|realm
operator|.
name|allowsEdit
argument_list|(
name|AccountFieldName
operator|.
name|USER_NAME
argument_list|)
condition|)
block|{
comment|// setting the given user name has failed, but the realm does not
comment|// allow the user to manually set a user name,
comment|// this means we would end with an account without user name
comment|// (without 'username:<USERNAME>' entry in
comment|// account_external_ids table),
comment|// such an account cannot be used for uploading changes,
comment|// this is why the best we can do here is to fail early and cleanup
comment|// the database
name|accountsUpdateFactory
operator|.
name|create
argument_list|()
operator|.
name|delete
argument_list|(
name|db
argument_list|,
name|account
argument_list|)
expr_stmt|;
name|externalIdsUpdateFactory
operator|.
name|create
argument_list|()
operator|.
name|delete
argument_list|(
name|extId
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|AccountUserNameException
argument_list|(
name|errorMessage
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Link another authentication identity to an existing account.    *    * @param to account to link the identity onto.    * @param who the additional identity.    * @return the result of linking the identity to the user.    * @throws AccountException the identity belongs to a different account, or it cannot be linked at    *     this time.    */
DECL|method|link (Account.Id to, AuthRequest who)
specifier|public
name|AuthResult
name|link
parameter_list|(
name|Account
operator|.
name|Id
name|to
parameter_list|,
name|AuthRequest
name|who
parameter_list|)
throws|throws
name|AccountException
throws|,
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
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
name|ExternalId
name|extId
init|=
name|externalIds
operator|.
name|get
argument_list|(
name|who
operator|.
name|getExternalIdKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|extId
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|extId
operator|.
name|accountId
argument_list|()
operator|.
name|equals
argument_list|(
name|to
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AccountException
argument_list|(
literal|"Identity in use by another account"
argument_list|)
throw|;
block|}
name|update
argument_list|(
name|db
argument_list|,
name|who
argument_list|,
name|extId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|externalIdsUpdateFactory
operator|.
name|create
argument_list|()
operator|.
name|insert
argument_list|(
name|ExternalId
operator|.
name|createWithEmail
argument_list|(
name|who
operator|.
name|getExternalIdKey
argument_list|()
argument_list|,
name|to
argument_list|,
name|who
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|who
operator|.
name|getEmailAddress
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|accountsUpdateFactory
operator|.
name|create
argument_list|()
operator|.
name|update
argument_list|(
name|db
argument_list|,
name|to
argument_list|,
name|a
lambda|->
block|{
if|if
condition|(
name|a
operator|.
name|getPreferredEmail
argument_list|()
operator|==
literal|null
condition|)
block|{
name|a
operator|.
name|setPreferredEmail
argument_list|(
name|who
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|byEmailCache
operator|.
name|evict
argument_list|(
name|who
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|AuthResult
argument_list|(
name|to
argument_list|,
name|who
operator|.
name|getExternalIdKey
argument_list|()
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
comment|/**    * Update the link to another unique authentication identity to an existing account.    *    *<p>Existing external identities with the same scheme will be removed and replaced with the new    * one.    *    * @param to account to link the identity onto.    * @param who the additional identity.    * @return the result of linking the identity to the user.    * @throws OrmException    * @throws AccountException the identity belongs to a different account, or it cannot be linked at    *     this time.    */
DECL|method|updateLink (Account.Id to, AuthRequest who)
specifier|public
name|AuthResult
name|updateLink
parameter_list|(
name|Account
operator|.
name|Id
name|to
parameter_list|,
name|AuthRequest
name|who
parameter_list|)
throws|throws
name|OrmException
throws|,
name|AccountException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|filteredExtIdsByScheme
init|=
name|externalIds
operator|.
name|byAccount
argument_list|(
name|to
argument_list|,
name|who
operator|.
name|getExternalIdKey
argument_list|()
operator|.
name|scheme
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|filteredExtIdsByScheme
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|(
name|filteredExtIdsByScheme
operator|.
name|size
argument_list|()
operator|>
literal|1
operator|||
operator|!
name|filteredExtIdsByScheme
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|e
lambda|->
name|e
operator|.
name|key
argument_list|()
operator|.
name|equals
argument_list|(
name|who
operator|.
name|getExternalIdKey
argument_list|()
argument_list|)
argument_list|)
operator|.
name|findAny
argument_list|()
operator|.
name|isPresent
argument_list|()
operator|)
condition|)
block|{
name|externalIdsUpdateFactory
operator|.
name|create
argument_list|()
operator|.
name|delete
argument_list|(
name|filteredExtIdsByScheme
argument_list|)
block|;     }
return|return
name|link
argument_list|(
name|to
argument_list|,
name|who
argument_list|)
return|;
block|}
comment|/**    * Unlink an authentication identity from an existing account.    *    * @param from account to unlink the identity from.    * @param who the identity to delete    * @return the result of unlinking the identity from the user.    * @throws AccountException the identity belongs to a different account, or it cannot be unlinked    *     at this time.    */
DECL|method|unlink (Account.Id from, AuthRequest who)
specifier|public
name|AuthResult
name|unlink
parameter_list|(
name|Account
operator|.
name|Id
name|from
parameter_list|,
name|AuthRequest
name|who
parameter_list|)
throws|throws
name|AccountException
throws|,
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
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
name|ExternalId
name|extId
init|=
name|externalIds
operator|.
name|get
argument_list|(
name|who
operator|.
name|getExternalIdKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|extId
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|extId
operator|.
name|accountId
argument_list|()
operator|.
name|equals
argument_list|(
name|from
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AccountException
argument_list|(
literal|"Identity '"
operator|+
name|who
operator|.
name|getExternalIdKey
argument_list|()
operator|.
name|get
argument_list|()
operator|+
literal|"' in use by another account"
argument_list|)
throw|;
block|}
name|externalIdsUpdateFactory
operator|.
name|create
argument_list|()
operator|.
name|delete
argument_list|(
name|extId
argument_list|)
expr_stmt|;
if|if
condition|(
name|who
operator|.
name|getEmailAddress
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|accountsUpdateFactory
operator|.
name|create
argument_list|()
operator|.
name|update
argument_list|(
name|db
argument_list|,
name|from
argument_list|,
name|a
lambda|->
block|{
if|if
condition|(
name|a
operator|.
name|getPreferredEmail
argument_list|()
operator|!=
literal|null
operator|&&
name|a
operator|.
name|getPreferredEmail
argument_list|()
operator|.
name|equals
argument_list|(
name|who
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
condition|)
block|{
name|a
operator|.
name|setPreferredEmail
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|byEmailCache
operator|.
name|evict
argument_list|(
name|who
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|AccountException
argument_list|(
literal|"Identity '"
operator|+
name|who
operator|.
name|getExternalIdKey
argument_list|()
operator|.
name|get
argument_list|()
operator|+
literal|"' not found"
argument_list|)
throw|;
block|}
return|return
operator|new
name|AuthResult
argument_list|(
name|from
argument_list|,
name|who
operator|.
name|getExternalIdKey
argument_list|()
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

