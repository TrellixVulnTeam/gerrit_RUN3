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
DECL|package|com.google.gerrit.server
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
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
name|reviewdb
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
name|Change
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
name|reviewdb
operator|.
name|StarredChange
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
name|Realm
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
name|config
operator|.
name|CanonicalWebUrl
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
name|Nullable
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
name|client
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
name|OutOfScopeException
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
name|ProvisionException
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

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|util
operator|.
name|SystemReader
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

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|Date
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
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_comment
comment|/** An authenticated user. */
end_comment

begin_class
DECL|class|IdentifiedUser
specifier|public
class|class
name|IdentifiedUser
extends|extends
name|CurrentUser
block|{
comment|/** Create an IdentifiedUser, ignoring any per-request state. */
annotation|@
name|Singleton
DECL|class|GenericFactory
specifier|public
specifier|static
class|class
name|GenericFactory
block|{
DECL|field|authConfig
specifier|private
specifier|final
name|AuthConfig
name|authConfig
decl_stmt|;
DECL|field|canonicalUrl
specifier|private
specifier|final
name|Provider
argument_list|<
name|String
argument_list|>
name|canonicalUrl
decl_stmt|;
DECL|field|realm
specifier|private
specifier|final
name|Realm
name|realm
decl_stmt|;
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
annotation|@
name|Inject
DECL|method|GenericFactory (final AuthConfig authConfig, final @CanonicalWebUrl Provider<String> canonicalUrl, final Realm realm, final AccountCache accountCache)
name|GenericFactory
parameter_list|(
specifier|final
name|AuthConfig
name|authConfig
parameter_list|,
specifier|final
annotation|@
name|CanonicalWebUrl
name|Provider
argument_list|<
name|String
argument_list|>
name|canonicalUrl
parameter_list|,
specifier|final
name|Realm
name|realm
parameter_list|,
specifier|final
name|AccountCache
name|accountCache
parameter_list|)
block|{
name|this
operator|.
name|authConfig
operator|=
name|authConfig
expr_stmt|;
name|this
operator|.
name|canonicalUrl
operator|=
name|canonicalUrl
expr_stmt|;
name|this
operator|.
name|realm
operator|=
name|realm
expr_stmt|;
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
block|}
DECL|method|create (final Account.Id id)
specifier|public
name|IdentifiedUser
name|create
parameter_list|(
specifier|final
name|Account
operator|.
name|Id
name|id
parameter_list|)
block|{
return|return
operator|new
name|IdentifiedUser
argument_list|(
name|AccessPath
operator|.
name|UNKNOWN
argument_list|,
name|authConfig
argument_list|,
name|canonicalUrl
argument_list|,
name|realm
argument_list|,
name|accountCache
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|id
argument_list|)
return|;
block|}
block|}
comment|/**    * Create an IdentifiedUser, relying on current request state.    *<p>    * Can only be used from within a module that has defined request scoped    * {@code @RemotePeer SocketAddress} and {@code ReviewDb} providers.    */
annotation|@
name|Singleton
DECL|class|RequestFactory
specifier|public
specifier|static
class|class
name|RequestFactory
block|{
DECL|field|authConfig
specifier|private
specifier|final
name|AuthConfig
name|authConfig
decl_stmt|;
DECL|field|canonicalUrl
specifier|private
specifier|final
name|Provider
argument_list|<
name|String
argument_list|>
name|canonicalUrl
decl_stmt|;
DECL|field|realm
specifier|private
specifier|final
name|Realm
name|realm
decl_stmt|;
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
DECL|field|remotePeerProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|SocketAddress
argument_list|>
name|remotePeerProvider
decl_stmt|;
DECL|field|dbProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
decl_stmt|;
annotation|@
name|Inject
DECL|method|RequestFactory (final AuthConfig authConfig, final @CanonicalWebUrl Provider<String> canonicalUrl, final Realm realm, final AccountCache accountCache, final @RemotePeer Provider<SocketAddress> remotePeerProvider, final Provider<ReviewDb> dbProvider)
name|RequestFactory
parameter_list|(
specifier|final
name|AuthConfig
name|authConfig
parameter_list|,
specifier|final
annotation|@
name|CanonicalWebUrl
name|Provider
argument_list|<
name|String
argument_list|>
name|canonicalUrl
parameter_list|,
specifier|final
name|Realm
name|realm
parameter_list|,
specifier|final
name|AccountCache
name|accountCache
parameter_list|,
specifier|final
annotation|@
name|RemotePeer
name|Provider
argument_list|<
name|SocketAddress
argument_list|>
name|remotePeerProvider
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
parameter_list|)
block|{
name|this
operator|.
name|authConfig
operator|=
name|authConfig
expr_stmt|;
name|this
operator|.
name|canonicalUrl
operator|=
name|canonicalUrl
expr_stmt|;
name|this
operator|.
name|realm
operator|=
name|realm
expr_stmt|;
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
name|this
operator|.
name|remotePeerProvider
operator|=
name|remotePeerProvider
expr_stmt|;
name|this
operator|.
name|dbProvider
operator|=
name|dbProvider
expr_stmt|;
block|}
DECL|method|create (final AccessPath accessPath, final Account.Id id)
specifier|public
name|IdentifiedUser
name|create
parameter_list|(
specifier|final
name|AccessPath
name|accessPath
parameter_list|,
specifier|final
name|Account
operator|.
name|Id
name|id
parameter_list|)
block|{
return|return
operator|new
name|IdentifiedUser
argument_list|(
name|accessPath
argument_list|,
name|authConfig
argument_list|,
name|canonicalUrl
argument_list|,
name|realm
argument_list|,
name|accountCache
argument_list|,
name|remotePeerProvider
argument_list|,
name|dbProvider
argument_list|,
name|id
argument_list|)
return|;
block|}
block|}
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
name|IdentifiedUser
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|canonicalUrl
specifier|private
specifier|final
name|Provider
argument_list|<
name|String
argument_list|>
name|canonicalUrl
decl_stmt|;
DECL|field|realm
specifier|private
specifier|final
name|Realm
name|realm
decl_stmt|;
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
annotation|@
name|Nullable
DECL|field|remotePeerProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|SocketAddress
argument_list|>
name|remotePeerProvider
decl_stmt|;
annotation|@
name|Nullable
DECL|field|dbProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
decl_stmt|;
DECL|field|accountId
specifier|private
specifier|final
name|Account
operator|.
name|Id
name|accountId
decl_stmt|;
DECL|field|state
specifier|private
name|AccountState
name|state
decl_stmt|;
DECL|field|emailAddresses
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|emailAddresses
decl_stmt|;
DECL|field|effectiveGroups
specifier|private
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|effectiveGroups
decl_stmt|;
DECL|field|starredChanges
specifier|private
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|starredChanges
decl_stmt|;
DECL|method|IdentifiedUser (final AccessPath accessPath, final AuthConfig authConfig, final Provider<String> canonicalUrl, final Realm realm, final AccountCache accountCache, @Nullable final Provider<SocketAddress> remotePeerProvider, @Nullable final Provider<ReviewDb> dbProvider, final Account.Id id)
specifier|private
name|IdentifiedUser
parameter_list|(
specifier|final
name|AccessPath
name|accessPath
parameter_list|,
specifier|final
name|AuthConfig
name|authConfig
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|String
argument_list|>
name|canonicalUrl
parameter_list|,
specifier|final
name|Realm
name|realm
parameter_list|,
specifier|final
name|AccountCache
name|accountCache
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|Provider
argument_list|<
name|SocketAddress
argument_list|>
name|remotePeerProvider
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
parameter_list|,
specifier|final
name|Account
operator|.
name|Id
name|id
parameter_list|)
block|{
name|super
argument_list|(
name|accessPath
argument_list|,
name|authConfig
argument_list|)
expr_stmt|;
name|this
operator|.
name|canonicalUrl
operator|=
name|canonicalUrl
expr_stmt|;
name|this
operator|.
name|realm
operator|=
name|realm
expr_stmt|;
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
name|this
operator|.
name|remotePeerProvider
operator|=
name|remotePeerProvider
expr_stmt|;
name|this
operator|.
name|dbProvider
operator|=
name|dbProvider
expr_stmt|;
name|this
operator|.
name|accountId
operator|=
name|id
expr_stmt|;
block|}
DECL|method|state ()
specifier|private
name|AccountState
name|state
parameter_list|()
block|{
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
name|state
operator|=
name|accountCache
operator|.
name|get
argument_list|(
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|state
return|;
block|}
comment|/** The account identity for the user. */
DECL|method|getAccountId ()
specifier|public
name|Account
operator|.
name|Id
name|getAccountId
parameter_list|()
block|{
return|return
name|accountId
return|;
block|}
DECL|method|getAccount ()
specifier|public
name|Account
name|getAccount
parameter_list|()
block|{
return|return
name|state
argument_list|()
operator|.
name|getAccount
argument_list|()
return|;
block|}
DECL|method|getEmailAddresses ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getEmailAddresses
parameter_list|()
block|{
if|if
condition|(
name|emailAddresses
operator|==
literal|null
condition|)
block|{
name|emailAddresses
operator|=
name|state
argument_list|()
operator|.
name|getEmailAddresses
argument_list|()
expr_stmt|;
block|}
return|return
name|emailAddresses
return|;
block|}
annotation|@
name|Override
DECL|method|getEffectiveGroups ()
specifier|public
name|Set
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|>
name|getEffectiveGroups
parameter_list|()
block|{
if|if
condition|(
name|effectiveGroups
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|authConfig
operator|.
name|isIdentityTrustable
argument_list|(
name|state
argument_list|()
operator|.
name|getExternalIds
argument_list|()
argument_list|)
condition|)
block|{
name|effectiveGroups
operator|=
name|realm
operator|.
name|groups
argument_list|(
name|state
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|effectiveGroups
operator|=
name|authConfig
operator|.
name|getRegisteredGroups
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|effectiveGroups
return|;
block|}
annotation|@
name|Override
DECL|method|getStarredChanges ()
specifier|public
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|getStarredChanges
parameter_list|()
block|{
if|if
condition|(
name|starredChanges
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|dbProvider
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|OutOfScopeException
argument_list|(
literal|"Not in request scoped user"
argument_list|)
throw|;
block|}
specifier|final
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|h
init|=
operator|new
name|HashSet
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
specifier|final
name|StarredChange
name|sc
range|:
name|dbProvider
operator|.
name|get
argument_list|()
operator|.
name|starredChanges
argument_list|()
operator|.
name|byAccount
argument_list|(
name|getAccountId
argument_list|()
argument_list|)
control|)
block|{
name|h
operator|.
name|add
argument_list|(
name|sc
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ProvisionException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot query starred by user changes"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot query starred by user changes"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|starredChanges
operator|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|h
argument_list|)
expr_stmt|;
block|}
return|return
name|starredChanges
return|;
block|}
DECL|method|newRefLogIdent ()
specifier|public
name|PersonIdent
name|newRefLogIdent
parameter_list|()
block|{
return|return
name|newRefLogIdent
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|,
name|TimeZone
operator|.
name|getDefault
argument_list|()
argument_list|)
return|;
block|}
DECL|method|newRefLogIdent (final Date when, final TimeZone tz)
specifier|public
name|PersonIdent
name|newRefLogIdent
parameter_list|(
specifier|final
name|Date
name|when
parameter_list|,
specifier|final
name|TimeZone
name|tz
parameter_list|)
block|{
specifier|final
name|Account
name|ua
init|=
name|getAccount
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|ua
operator|.
name|getFullName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
operator|||
name|name
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|name
operator|=
name|ua
operator|.
name|getPreferredEmail
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|name
operator|==
literal|null
operator|||
name|name
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|name
operator|=
literal|"Anonymous Coward"
expr_stmt|;
block|}
name|String
name|user
init|=
name|ua
operator|.
name|getSshUserName
argument_list|()
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
name|user
operator|=
literal|""
expr_stmt|;
block|}
name|user
operator|=
name|user
operator|+
literal|"|"
operator|+
literal|"account-"
operator|+
name|ua
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|String
name|host
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|remotePeerProvider
operator|!=
literal|null
condition|)
block|{
specifier|final
name|SocketAddress
name|remotePeer
init|=
name|remotePeerProvider
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|remotePeer
operator|instanceof
name|InetSocketAddress
condition|)
block|{
specifier|final
name|InetSocketAddress
name|sa
init|=
operator|(
name|InetSocketAddress
operator|)
name|remotePeer
decl_stmt|;
specifier|final
name|InetAddress
name|in
init|=
name|sa
operator|.
name|getAddress
argument_list|()
decl_stmt|;
name|host
operator|=
name|in
operator|!=
literal|null
condition|?
name|in
operator|.
name|getCanonicalHostName
argument_list|()
else|:
name|sa
operator|.
name|getHostName
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|host
operator|==
literal|null
operator|||
name|host
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|host
operator|=
literal|"unknown"
expr_stmt|;
block|}
return|return
operator|new
name|PersonIdent
argument_list|(
name|name
argument_list|,
name|user
operator|+
literal|"@"
operator|+
name|host
argument_list|,
name|when
argument_list|,
name|tz
argument_list|)
return|;
block|}
DECL|method|newCommitterIdent (final Date when, final TimeZone tz)
specifier|public
name|PersonIdent
name|newCommitterIdent
parameter_list|(
specifier|final
name|Date
name|when
parameter_list|,
specifier|final
name|TimeZone
name|tz
parameter_list|)
block|{
specifier|final
name|Account
name|ua
init|=
name|getAccount
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|ua
operator|.
name|getFullName
argument_list|()
decl_stmt|;
name|String
name|email
init|=
name|ua
operator|.
name|getPreferredEmail
argument_list|()
decl_stmt|;
if|if
condition|(
name|email
operator|==
literal|null
operator|||
name|email
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// No preferred email is configured. Use a generic identity so we
comment|// don't leak an address the user may have given us, but doesn't
comment|// necessarily want to publish through Git records.
comment|//
name|String
name|user
init|=
name|ua
operator|.
name|getSshUserName
argument_list|()
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
operator|||
name|user
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|user
operator|=
literal|"account-"
operator|+
name|ua
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|String
name|host
decl_stmt|;
if|if
condition|(
name|canonicalUrl
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|host
operator|=
operator|new
name|URL
argument_list|(
name|canonicalUrl
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|getHost
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
name|host
operator|=
name|SystemReader
operator|.
name|getInstance
argument_list|()
operator|.
name|getHostname
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|host
operator|=
name|SystemReader
operator|.
name|getInstance
argument_list|()
operator|.
name|getHostname
argument_list|()
expr_stmt|;
block|}
name|email
operator|=
name|user
operator|+
literal|"@"
operator|+
name|host
expr_stmt|;
block|}
if|if
condition|(
name|name
operator|==
literal|null
operator|||
name|name
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|int
name|at
init|=
name|email
operator|.
name|indexOf
argument_list|(
literal|'@'
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|<
name|at
condition|)
block|{
name|name
operator|=
name|email
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|at
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|name
operator|=
literal|"Anonymous Coward"
expr_stmt|;
block|}
block|}
return|return
operator|new
name|PersonIdent
argument_list|(
name|name
argument_list|,
name|email
argument_list|,
name|when
argument_list|,
name|tz
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"IdentifiedUser[account "
operator|+
name|getAccountId
argument_list|()
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

