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
import|import static
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
operator|.
name|SCHEME_USERNAME
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
name|cache
operator|.
name|CacheLoader
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
name|cache
operator|.
name|LoadingCache
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
name|extensions
operator|.
name|client
operator|.
name|GeneralPreferencesInfo
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
name|account
operator|.
name|WatchConfig
operator|.
name|NotifyType
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
name|WatchConfig
operator|.
name|ProjectWatchKey
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
name|cache
operator|.
name|CacheModule
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
name|index
operator|.
name|account
operator|.
name|AccountIndexer
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
name|Module
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|TypeLiteral
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
name|name
operator|.
name|Named
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
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
comment|/** Caches important (but small) account state to avoid database hits. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|AccountCacheImpl
specifier|public
class|class
name|AccountCacheImpl
implements|implements
name|AccountCache
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
name|AccountCacheImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|BYID_NAME
specifier|private
specifier|static
specifier|final
name|String
name|BYID_NAME
init|=
literal|"accounts"
decl_stmt|;
DECL|field|BYUSER_NAME
specifier|private
specifier|static
specifier|final
name|String
name|BYUSER_NAME
init|=
literal|"accounts_byname"
decl_stmt|;
DECL|method|module ()
specifier|public
specifier|static
name|Module
name|module
parameter_list|()
block|{
return|return
operator|new
name|CacheModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|cache
argument_list|(
name|BYID_NAME
argument_list|,
name|Account
operator|.
name|Id
operator|.
name|class
argument_list|,
name|AccountState
operator|.
name|class
argument_list|)
operator|.
name|loader
argument_list|(
name|ByIdLoader
operator|.
name|class
argument_list|)
expr_stmt|;
name|cache
argument_list|(
name|BYUSER_NAME
argument_list|,
name|String
operator|.
name|class
argument_list|,
operator|new
name|TypeLiteral
argument_list|<
name|Optional
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
argument_list|>
argument_list|()
block|{}
argument_list|)
operator|.
name|loader
argument_list|(
name|ByNameLoader
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|AccountCacheImpl
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|AccountCache
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|AccountCacheImpl
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|field|byId
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|AccountState
argument_list|>
name|byId
decl_stmt|;
DECL|field|byName
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|String
argument_list|,
name|Optional
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
argument_list|>
name|byName
decl_stmt|;
DECL|field|indexer
specifier|private
specifier|final
name|Provider
argument_list|<
name|AccountIndexer
argument_list|>
name|indexer
decl_stmt|;
annotation|@
name|Inject
DECL|method|AccountCacheImpl ( @amedBYID_NAME) LoadingCache<Account.Id, AccountState> byId, @Named(BYUSER_NAME) LoadingCache<String, Optional<Account.Id>> byUsername, Provider<AccountIndexer> indexer)
name|AccountCacheImpl
parameter_list|(
annotation|@
name|Named
argument_list|(
name|BYID_NAME
argument_list|)
name|LoadingCache
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|AccountState
argument_list|>
name|byId
parameter_list|,
annotation|@
name|Named
argument_list|(
name|BYUSER_NAME
argument_list|)
name|LoadingCache
argument_list|<
name|String
argument_list|,
name|Optional
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
argument_list|>
name|byUsername
parameter_list|,
name|Provider
argument_list|<
name|AccountIndexer
argument_list|>
name|indexer
parameter_list|)
block|{
name|this
operator|.
name|byId
operator|=
name|byId
expr_stmt|;
name|this
operator|.
name|byName
operator|=
name|byUsername
expr_stmt|;
name|this
operator|.
name|indexer
operator|=
name|indexer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get (Account.Id accountId)
specifier|public
name|AccountState
name|get
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
block|{
try|try
block|{
return|return
name|byId
operator|.
name|get
argument_list|(
name|accountId
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot load AccountState for "
operator|+
name|accountId
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|missing
argument_list|(
name|accountId
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getIfPresent (Account.Id accountId)
specifier|public
name|AccountState
name|getIfPresent
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
block|{
return|return
name|byId
operator|.
name|getIfPresent
argument_list|(
name|accountId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getByUsername (String username)
specifier|public
name|AccountState
name|getByUsername
parameter_list|(
name|String
name|username
parameter_list|)
block|{
try|try
block|{
name|Optional
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|id
init|=
name|byName
operator|.
name|get
argument_list|(
name|username
argument_list|)
decl_stmt|;
return|return
name|id
operator|!=
literal|null
operator|&&
name|id
operator|.
name|isPresent
argument_list|()
condition|?
name|byId
operator|.
name|get
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
else|:
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot load AccountState for "
operator|+
name|username
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|evict (Account.Id accountId)
specifier|public
name|void
name|evict
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|accountId
operator|!=
literal|null
condition|)
block|{
name|byId
operator|.
name|invalidate
argument_list|(
name|accountId
argument_list|)
expr_stmt|;
name|indexer
operator|.
name|get
argument_list|()
operator|.
name|index
argument_list|(
name|accountId
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|evictAll ()
specifier|public
name|void
name|evictAll
parameter_list|()
throws|throws
name|IOException
block|{
name|byId
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
for|for
control|(
name|Account
operator|.
name|Id
name|accountId
range|:
name|byId
operator|.
name|asMap
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
name|indexer
operator|.
name|get
argument_list|()
operator|.
name|index
argument_list|(
name|accountId
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|evictByUsername (String username)
specifier|public
name|void
name|evictByUsername
parameter_list|(
name|String
name|username
parameter_list|)
block|{
if|if
condition|(
name|username
operator|!=
literal|null
condition|)
block|{
name|byName
operator|.
name|invalidate
argument_list|(
name|username
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|missing (Account.Id accountId)
specifier|private
specifier|static
name|AccountState
name|missing
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
block|{
name|Account
name|account
init|=
operator|new
name|Account
argument_list|(
name|accountId
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|account
operator|.
name|setActive
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|anon
init|=
name|ImmutableSet
operator|.
name|of
argument_list|()
decl_stmt|;
return|return
operator|new
name|AccountState
argument_list|(
name|account
argument_list|,
name|anon
argument_list|,
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<
name|ProjectWatchKey
argument_list|,
name|Set
argument_list|<
name|NotifyType
argument_list|>
argument_list|>
argument_list|()
argument_list|)
return|;
block|}
DECL|class|ByIdLoader
specifier|static
class|class
name|ByIdLoader
extends|extends
name|CacheLoader
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|AccountState
argument_list|>
block|{
DECL|field|schema
specifier|private
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
decl_stmt|;
DECL|field|groupCache
specifier|private
specifier|final
name|GroupCache
name|groupCache
decl_stmt|;
DECL|field|loader
specifier|private
specifier|final
name|GeneralPreferencesLoader
name|loader
decl_stmt|;
DECL|field|byName
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|String
argument_list|,
name|Optional
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
argument_list|>
name|byName
decl_stmt|;
DECL|field|watchConfig
specifier|private
specifier|final
name|Provider
argument_list|<
name|WatchConfig
operator|.
name|Accessor
argument_list|>
name|watchConfig
decl_stmt|;
DECL|field|externalIds
specifier|private
specifier|final
name|ExternalIds
name|externalIds
decl_stmt|;
annotation|@
name|Inject
DECL|method|ByIdLoader ( SchemaFactory<ReviewDb> sf, GroupCache groupCache, GeneralPreferencesLoader loader, @Named(BYUSER_NAME) LoadingCache<String, Optional<Account.Id>> byUsername, Provider<WatchConfig.Accessor> watchConfig, ExternalIds externalIds)
name|ByIdLoader
parameter_list|(
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|sf
parameter_list|,
name|GroupCache
name|groupCache
parameter_list|,
name|GeneralPreferencesLoader
name|loader
parameter_list|,
annotation|@
name|Named
argument_list|(
name|BYUSER_NAME
argument_list|)
name|LoadingCache
argument_list|<
name|String
argument_list|,
name|Optional
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
argument_list|>
name|byUsername
parameter_list|,
name|Provider
argument_list|<
name|WatchConfig
operator|.
name|Accessor
argument_list|>
name|watchConfig
parameter_list|,
name|ExternalIds
name|externalIds
parameter_list|)
block|{
name|this
operator|.
name|schema
operator|=
name|sf
expr_stmt|;
name|this
operator|.
name|groupCache
operator|=
name|groupCache
expr_stmt|;
name|this
operator|.
name|loader
operator|=
name|loader
expr_stmt|;
name|this
operator|.
name|byName
operator|=
name|byUsername
expr_stmt|;
name|this
operator|.
name|watchConfig
operator|=
name|watchConfig
expr_stmt|;
name|this
operator|.
name|externalIds
operator|=
name|externalIds
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load (Account.Id key)
specifier|public
name|AccountState
name|load
parameter_list|(
name|Account
operator|.
name|Id
name|key
parameter_list|)
throws|throws
name|Exception
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
specifier|final
name|AccountState
name|state
init|=
name|load
argument_list|(
name|db
argument_list|,
name|key
argument_list|)
decl_stmt|;
name|String
name|user
init|=
name|state
operator|.
name|getUserName
argument_list|()
decl_stmt|;
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
block|{
name|byName
operator|.
name|put
argument_list|(
name|user
argument_list|,
name|Optional
operator|.
name|of
argument_list|(
name|state
operator|.
name|getAccount
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|state
return|;
block|}
block|}
DECL|method|load (final ReviewDb db, final Account.Id who)
specifier|private
name|AccountState
name|load
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|,
specifier|final
name|Account
operator|.
name|Id
name|who
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|Account
name|account
init|=
name|db
operator|.
name|accounts
argument_list|()
operator|.
name|get
argument_list|(
name|who
argument_list|)
decl_stmt|;
if|if
condition|(
name|account
operator|==
literal|null
condition|)
block|{
comment|// Account no longer exists? They are anonymous.
return|return
name|missing
argument_list|(
name|who
argument_list|)
return|;
block|}
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|internalGroups
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountGroupMember
name|g
range|:
name|db
operator|.
name|accountGroupMembers
argument_list|()
operator|.
name|byAccount
argument_list|(
name|who
argument_list|)
control|)
block|{
specifier|final
name|AccountGroup
operator|.
name|Id
name|groupId
init|=
name|g
operator|.
name|getAccountGroupId
argument_list|()
decl_stmt|;
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
operator|!=
literal|null
operator|&&
name|group
operator|.
name|getGroupUUID
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|internalGroups
operator|.
name|add
argument_list|(
name|group
operator|.
name|getGroupUUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|internalGroups
operator|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|internalGroups
argument_list|)
expr_stmt|;
try|try
block|{
name|account
operator|.
name|setGeneralPreferences
argument_list|(
name|loader
operator|.
name|load
argument_list|(
name|who
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|ConfigInvalidException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Cannot load GeneralPreferences for "
operator|+
name|who
operator|+
literal|" (using default)"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|account
operator|.
name|setGeneralPreferences
argument_list|(
name|GeneralPreferencesInfo
operator|.
name|defaults
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|AccountState
argument_list|(
name|account
argument_list|,
name|internalGroups
argument_list|,
name|externalIds
operator|.
name|byAccount
argument_list|(
name|db
argument_list|,
name|who
argument_list|)
argument_list|,
name|watchConfig
operator|.
name|get
argument_list|()
operator|.
name|getProjectWatches
argument_list|(
name|who
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|ByNameLoader
specifier|static
class|class
name|ByNameLoader
extends|extends
name|CacheLoader
argument_list|<
name|String
argument_list|,
name|Optional
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
argument_list|>
block|{
DECL|field|accountQueryProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|InternalAccountQuery
argument_list|>
name|accountQueryProvider
decl_stmt|;
annotation|@
name|Inject
DECL|method|ByNameLoader (Provider<InternalAccountQuery> accountQueryProvider)
name|ByNameLoader
parameter_list|(
name|Provider
argument_list|<
name|InternalAccountQuery
argument_list|>
name|accountQueryProvider
parameter_list|)
block|{
name|this
operator|.
name|accountQueryProvider
operator|=
name|accountQueryProvider
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|load (String username)
specifier|public
name|Optional
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|load
parameter_list|(
name|String
name|username
parameter_list|)
throws|throws
name|Exception
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
name|SCHEME_USERNAME
argument_list|,
name|username
argument_list|)
decl_stmt|;
return|return
name|Optional
operator|.
name|ofNullable
argument_list|(
name|accountState
argument_list|)
operator|.
name|map
argument_list|(
name|s
lambda|->
name|s
operator|.
name|getAccount
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

