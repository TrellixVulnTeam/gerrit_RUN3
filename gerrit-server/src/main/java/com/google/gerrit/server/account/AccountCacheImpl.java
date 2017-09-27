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
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
operator|.
name|toImmutableSet
import|;
end_import

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
name|common
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
name|config
operator|.
name|AllUsersName
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
name|index
operator|.
name|group
operator|.
name|GroupField
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
name|group
operator|.
name|GroupIndex
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
name|group
operator|.
name|GroupIndexCollection
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
name|group
operator|.
name|InternalGroupQuery
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
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
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
operator|new
name|TypeLiteral
argument_list|<
name|Optional
argument_list|<
name|AccountState
argument_list|>
argument_list|>
argument_list|()
block|{}
argument_list|)
operator|.
name|loader
argument_list|(
name|ByIdLoader
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
DECL|field|allUsersName
specifier|private
specifier|final
name|AllUsersName
name|allUsersName
decl_stmt|;
DECL|field|externalIds
specifier|private
specifier|final
name|ExternalIds
name|externalIds
decl_stmt|;
DECL|field|byId
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|Optional
argument_list|<
name|AccountState
argument_list|>
argument_list|>
name|byId
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
DECL|method|AccountCacheImpl ( AllUsersName allUsersName, ExternalIds externalIds, @Named(BYID_NAME) LoadingCache<Account.Id, Optional<AccountState>> byId, Provider<AccountIndexer> indexer)
name|AccountCacheImpl
parameter_list|(
name|AllUsersName
name|allUsersName
parameter_list|,
name|ExternalIds
name|externalIds
parameter_list|,
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
name|Optional
argument_list|<
name|AccountState
argument_list|>
argument_list|>
name|byId
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
name|allUsersName
operator|=
name|allUsersName
expr_stmt|;
name|this
operator|.
name|externalIds
operator|=
name|externalIds
expr_stmt|;
name|this
operator|.
name|byId
operator|=
name|byId
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
operator|.
name|orElse
argument_list|(
name|missing
argument_list|(
name|accountId
argument_list|)
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
annotation|@
name|Nullable
DECL|method|getOrNull (Account.Id accountId)
specifier|public
name|AccountState
name|getOrNull
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
operator|.
name|orElse
argument_list|(
literal|null
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
literal|"Cannot load AccountState for ID "
operator|+
name|accountId
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
name|ExternalId
name|extId
init|=
name|externalIds
operator|.
name|get
argument_list|(
name|ExternalId
operator|.
name|Key
operator|.
name|create
argument_list|(
name|SCHEME_USERNAME
argument_list|,
name|username
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|extId
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|getOrNull
argument_list|(
name|extId
operator|.
name|accountId
argument_list|()
argument_list|)
return|;
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
literal|"Cannot load AccountState for username "
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
DECL|method|evictAllNoReindex ()
specifier|public
name|void
name|evictAllNoReindex
parameter_list|()
block|{
name|byId
operator|.
name|invalidateAll
argument_list|()
expr_stmt|;
block|}
DECL|method|missing (Account.Id accountId)
specifier|private
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
name|allUsersName
argument_list|,
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
name|Optional
argument_list|<
name|AccountState
argument_list|>
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
DECL|field|allUsersName
specifier|private
specifier|final
name|AllUsersName
name|allUsersName
decl_stmt|;
DECL|field|accounts
specifier|private
specifier|final
name|Accounts
name|accounts
decl_stmt|;
DECL|field|groupIndexProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|GroupIndex
argument_list|>
name|groupIndexProvider
decl_stmt|;
DECL|field|groupQueryProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|InternalGroupQuery
argument_list|>
name|groupQueryProvider
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
DECL|method|ByIdLoader ( SchemaFactory<ReviewDb> sf, AllUsersName allUsersName, Accounts accounts, GroupIndexCollection groupIndexCollection, Provider<InternalGroupQuery> groupQueryProvider, GroupCache groupCache, GeneralPreferencesLoader loader, Provider<WatchConfig.Accessor> watchConfig, ExternalIds externalIds)
name|ByIdLoader
parameter_list|(
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|sf
parameter_list|,
name|AllUsersName
name|allUsersName
parameter_list|,
name|Accounts
name|accounts
parameter_list|,
name|GroupIndexCollection
name|groupIndexCollection
parameter_list|,
name|Provider
argument_list|<
name|InternalGroupQuery
argument_list|>
name|groupQueryProvider
parameter_list|,
name|GroupCache
name|groupCache
parameter_list|,
name|GeneralPreferencesLoader
name|loader
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
name|allUsersName
operator|=
name|allUsersName
expr_stmt|;
name|this
operator|.
name|accounts
operator|=
name|accounts
expr_stmt|;
name|this
operator|.
name|groupIndexProvider
operator|=
name|groupIndexCollection
operator|::
name|getSearchIndex
expr_stmt|;
name|this
operator|.
name|groupQueryProvider
operator|=
name|groupQueryProvider
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
name|Optional
argument_list|<
name|AccountState
argument_list|>
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
return|return
name|load
argument_list|(
name|db
argument_list|,
name|key
argument_list|)
return|;
block|}
block|}
DECL|method|load (ReviewDb db, Account.Id who)
specifier|private
name|Optional
argument_list|<
name|AccountState
argument_list|>
name|load
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
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
name|accounts
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
return|return
name|Optional
operator|.
name|empty
argument_list|()
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
name|getGroupsWithMember
argument_list|(
name|db
argument_list|,
name|who
argument_list|)
decl_stmt|;
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
name|Optional
operator|.
name|of
argument_list|(
operator|new
name|AccountState
argument_list|(
name|allUsersName
argument_list|,
name|account
argument_list|,
name|internalGroups
argument_list|,
name|externalIds
operator|.
name|byAccount
argument_list|(
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
argument_list|)
return|;
block|}
DECL|method|getGroupsWithMember (ReviewDb db, Account.Id memberId)
specifier|private
name|ImmutableSet
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|getGroupsWithMember
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Account
operator|.
name|Id
name|memberId
parameter_list|)
throws|throws
name|OrmException
block|{
name|Stream
argument_list|<
name|InternalGroup
argument_list|>
name|internalGroupStream
decl_stmt|;
if|if
condition|(
name|groupIndexProvider
operator|.
name|get
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|hasField
argument_list|(
name|GroupField
operator|.
name|MEMBER
argument_list|)
condition|)
block|{
name|internalGroupStream
operator|=
name|groupQueryProvider
operator|.
name|get
argument_list|()
operator|.
name|byMember
argument_list|(
name|memberId
argument_list|)
operator|.
name|stream
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|internalGroupStream
operator|=
name|Groups
operator|.
name|getGroupsWithMemberFromReviewDb
argument_list|(
name|db
argument_list|,
name|memberId
argument_list|)
operator|.
name|map
argument_list|(
name|groupCache
operator|::
name|get
argument_list|)
operator|.
name|flatMap
argument_list|(
name|Streams
operator|::
name|stream
argument_list|)
expr_stmt|;
block|}
return|return
name|internalGroupStream
operator|.
name|map
argument_list|(
name|InternalGroup
operator|::
name|getGroupUUID
argument_list|)
operator|.
name|collect
argument_list|(
name|toImmutableSet
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

