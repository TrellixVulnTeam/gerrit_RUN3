begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
name|base
operator|.
name|Preconditions
operator|.
name|checkState
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
name|ExternalId
operator|.
name|Key
operator|.
name|toAccountExternalIdKeys
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
name|ExternalId
operator|.
name|toAccountExternalIds
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
name|toSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|rholder
operator|.
name|retry
operator|.
name|RetryerBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|rholder
operator|.
name|retry
operator|.
name|StopStrategies
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|rholder
operator|.
name|retry
operator|.
name|WaitStrategies
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
name|annotations
operator|.
name|VisibleForTesting
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
name|git
operator|.
name|LockFailureException
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
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|// Updates externalIds in ReviewDb.
end_comment

begin_class
DECL|class|ExternalIdsUpdate
specifier|public
class|class
name|ExternalIdsUpdate
block|{
comment|/**    * Factory to create an ExternalIdsUpdate instance for updating external IDs by the Gerrit server.    */
annotation|@
name|Singleton
DECL|class|Server
specifier|public
specifier|static
class|class
name|Server
block|{
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
annotation|@
name|Inject
DECL|method|Server (AccountCache accountCache)
specifier|public
name|Server
parameter_list|(
name|AccountCache
name|accountCache
parameter_list|)
block|{
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
block|}
DECL|method|create ()
specifier|public
name|ExternalIdsUpdate
name|create
parameter_list|()
block|{
return|return
operator|new
name|ExternalIdsUpdate
argument_list|(
name|accountCache
argument_list|)
return|;
block|}
block|}
annotation|@
name|Singleton
DECL|class|User
specifier|public
specifier|static
class|class
name|User
block|{
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
annotation|@
name|Inject
DECL|method|User (AccountCache accountCache)
specifier|public
name|User
parameter_list|(
name|AccountCache
name|accountCache
parameter_list|)
block|{
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
block|}
DECL|method|create ()
specifier|public
name|ExternalIdsUpdate
name|create
parameter_list|()
block|{
return|return
operator|new
name|ExternalIdsUpdate
argument_list|(
name|accountCache
argument_list|)
return|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|retryerBuilder ()
specifier|public
specifier|static
name|RetryerBuilder
argument_list|<
name|Void
argument_list|>
name|retryerBuilder
parameter_list|()
block|{
return|return
name|RetryerBuilder
operator|.
expr|<
name|Void
operator|>
name|newBuilder
argument_list|()
operator|.
name|retryIfException
argument_list|(
name|e
lambda|->
name|e
operator|instanceof
name|LockFailureException
argument_list|)
operator|.
name|withWaitStrategy
argument_list|(
name|WaitStrategies
operator|.
name|join
argument_list|(
name|WaitStrategies
operator|.
name|exponentialWait
argument_list|(
literal|2
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|,
name|WaitStrategies
operator|.
name|randomWait
argument_list|(
literal|50
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
argument_list|)
operator|.
name|withStopStrategy
argument_list|(
name|StopStrategies
operator|.
name|stopAfterDelay
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
return|;
block|}
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|method|ExternalIdsUpdate (AccountCache accountCache)
specifier|public
name|ExternalIdsUpdate
parameter_list|(
name|AccountCache
name|accountCache
parameter_list|)
block|{
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
block|}
comment|/**    * Inserts a new external ID.    *    *<p>If the external ID already exists, the insert fails with {@link OrmDuplicateKeyException}.    */
DECL|method|insert (ReviewDb db, ExternalId extId)
specifier|public
name|void
name|insert
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|ExternalId
name|extId
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
block|{
name|insert
argument_list|(
name|db
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|extId
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Inserts new external IDs.    *    *<p>If any of the external ID already exists, the insert fails with {@link    * OrmDuplicateKeyException}.    */
DECL|method|insert (ReviewDb db, Collection<ExternalId> extIds)
specifier|public
name|void
name|insert
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|extIds
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
block|{
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|insert
argument_list|(
name|toAccountExternalIds
argument_list|(
name|extIds
argument_list|)
argument_list|)
expr_stmt|;
name|evictAccounts
argument_list|(
name|extIds
argument_list|)
expr_stmt|;
block|}
comment|/**    * Inserts or updates an external ID.    *    *<p>If the external ID already exists, it is overwritten, otherwise it is inserted.    */
DECL|method|upsert (ReviewDb db, ExternalId extId)
specifier|public
name|void
name|upsert
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|ExternalId
name|extId
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
block|{
name|upsert
argument_list|(
name|db
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|extId
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Inserts or updates external IDs.    *    *<p>If any of the external IDs already exists, it is overwritten. New external IDs are inserted.    */
DECL|method|upsert (ReviewDb db, Collection<ExternalId> extIds)
specifier|public
name|void
name|upsert
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|extIds
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
block|{
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|upsert
argument_list|(
name|toAccountExternalIds
argument_list|(
name|extIds
argument_list|)
argument_list|)
expr_stmt|;
name|evictAccounts
argument_list|(
name|extIds
argument_list|)
expr_stmt|;
block|}
comment|/**    * Deletes an external ID.    *    *<p>The deletion fails with {@link IllegalStateException} if there is an existing external ID    * that has the same key, but otherwise doesn't match the specified external ID.    */
DECL|method|delete (ReviewDb db, ExternalId extId)
specifier|public
name|void
name|delete
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|ExternalId
name|extId
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
block|{
name|delete
argument_list|(
name|db
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|extId
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Deletes external IDs.    *    *<p>The deletion fails with {@link IllegalStateException} if there is an existing external ID    * that has the same key as any of the external IDs that should be deleted, but otherwise doesn't    * match the that external ID.    */
DECL|method|delete (ReviewDb db, Collection<ExternalId> extIds)
specifier|public
name|void
name|delete
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|extIds
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
block|{
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|delete
argument_list|(
name|toAccountExternalIds
argument_list|(
name|extIds
argument_list|)
argument_list|)
expr_stmt|;
name|evictAccounts
argument_list|(
name|extIds
argument_list|)
expr_stmt|;
block|}
comment|/**    * Delete an external ID by key.    *    *<p>The external ID is only deleted if it belongs to the specified account. If it belongs to    * another account the deletion fails with {@link IllegalStateException}.    */
DECL|method|delete (ReviewDb db, Account.Id accountId, ExternalId.Key extIdKey)
specifier|public
name|void
name|delete
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|ExternalId
operator|.
name|Key
name|extIdKey
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
block|{
name|delete
argument_list|(
name|db
argument_list|,
name|accountId
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|extIdKey
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Delete external IDs by external ID key.    *    *<p>The external IDs are only deleted if they belongs to the specified account. If any of the    * external IDs belongs to another account the deletion fails with {@link IllegalStateException}.    */
DECL|method|delete (ReviewDb db, Account.Id accountId, Collection<ExternalId.Key> extIdKeys)
specifier|public
name|void
name|delete
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|Collection
argument_list|<
name|ExternalId
operator|.
name|Key
argument_list|>
name|extIdKeys
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
block|{
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|deleteKeys
argument_list|(
name|toAccountExternalIdKeys
argument_list|(
name|extIdKeys
argument_list|)
argument_list|)
expr_stmt|;
name|accountCache
operator|.
name|evict
argument_list|(
name|accountId
argument_list|)
expr_stmt|;
block|}
comment|/** Deletes all external IDs of the specified account. */
DECL|method|deleteAll (ReviewDb db, Account.Id accountId)
specifier|public
name|void
name|deleteAll
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
block|{
name|delete
argument_list|(
name|db
argument_list|,
name|ExternalId
operator|.
name|from
argument_list|(
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|byAccount
argument_list|(
name|accountId
argument_list|)
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Replaces external IDs for an account by external ID keys.    *    *<p>Deletion of external IDs is done before adding the new external IDs. This means if an    * external ID key is specified for deletion and an external ID with the same key is specified to    * be added, the old external ID with that key is deleted first and then the new external ID is    * added (so the external ID for that key is replaced).    *    *<p>If any of the specified external IDs belongs to another account the replacement fails with    * {@link IllegalStateException}.    */
DECL|method|replace ( ReviewDb db, Account.Id accountId, Collection<ExternalId.Key> toDelete, Collection<ExternalId> toAdd)
specifier|public
name|void
name|replace
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|Collection
argument_list|<
name|ExternalId
operator|.
name|Key
argument_list|>
name|toDelete
parameter_list|,
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|toAdd
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
block|{
name|checkSameAccount
argument_list|(
name|toAdd
argument_list|,
name|accountId
argument_list|)
expr_stmt|;
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|deleteKeys
argument_list|(
name|toAccountExternalIdKeys
argument_list|(
name|toDelete
argument_list|)
argument_list|)
expr_stmt|;
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|insert
argument_list|(
name|toAccountExternalIds
argument_list|(
name|toAdd
argument_list|)
argument_list|)
expr_stmt|;
name|accountCache
operator|.
name|evict
argument_list|(
name|accountId
argument_list|)
expr_stmt|;
block|}
comment|/**    * Replaces an external ID.    *    *<p>If the specified external IDs belongs to different accounts the replacement fails with    * {@link IllegalStateException}.    */
DECL|method|replace (ReviewDb db, ExternalId toDelete, ExternalId toAdd)
specifier|public
name|void
name|replace
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|ExternalId
name|toDelete
parameter_list|,
name|ExternalId
name|toAdd
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
block|{
name|replace
argument_list|(
name|db
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|toDelete
argument_list|)
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|toAdd
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Replaces external IDs.    *    *<p>Deletion of external IDs is done before adding the new external IDs. This means if an    * external ID is specified for deletion and an external ID with the same key is specified to be    * added, the old external ID with that key is deleted first and then the new external ID is added    * (so the external ID for that key is replaced).    *    *<p>If the specified external IDs belong to different accounts the replacement fails with {@link    * IllegalStateException}.    */
DECL|method|replace (ReviewDb db, Collection<ExternalId> toDelete, Collection<ExternalId> toAdd)
specifier|public
name|void
name|replace
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|toDelete
parameter_list|,
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|toAdd
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
block|{
name|Account
operator|.
name|Id
name|accountId
init|=
name|checkSameAccount
argument_list|(
name|Iterables
operator|.
name|concat
argument_list|(
name|toDelete
argument_list|,
name|toAdd
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|accountId
operator|==
literal|null
condition|)
block|{
comment|// toDelete and toAdd are empty -> nothing to do
return|return;
block|}
name|replace
argument_list|(
name|db
argument_list|,
name|accountId
argument_list|,
name|toDelete
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|e
lambda|->
name|e
operator|.
name|key
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|toSet
argument_list|()
argument_list|)
argument_list|,
name|toAdd
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks that all specified external IDs belong to the same account.    *    * @return the ID of the account to which all specified external IDs belong.    */
DECL|method|checkSameAccount (Iterable<ExternalId> extIds)
specifier|public
specifier|static
name|Account
operator|.
name|Id
name|checkSameAccount
parameter_list|(
name|Iterable
argument_list|<
name|ExternalId
argument_list|>
name|extIds
parameter_list|)
block|{
return|return
name|checkSameAccount
argument_list|(
name|extIds
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Checks that all specified external IDs belong to specified account. If no account is specified    * it is checked that all specified external IDs belong to the same account.    *    * @return the ID of the account to which all specified external IDs belong.    */
DECL|method|checkSameAccount ( Iterable<ExternalId> extIds, @Nullable Account.Id accountId)
specifier|public
specifier|static
name|Account
operator|.
name|Id
name|checkSameAccount
parameter_list|(
name|Iterable
argument_list|<
name|ExternalId
argument_list|>
name|extIds
parameter_list|,
annotation|@
name|Nullable
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
block|{
for|for
control|(
name|ExternalId
name|extId
range|:
name|extIds
control|)
block|{
if|if
condition|(
name|accountId
operator|==
literal|null
condition|)
block|{
name|accountId
operator|=
name|extId
operator|.
name|accountId
argument_list|()
expr_stmt|;
continue|continue;
block|}
name|checkState
argument_list|(
name|accountId
operator|.
name|equals
argument_list|(
name|extId
operator|.
name|accountId
argument_list|()
argument_list|)
argument_list|,
literal|"external id %s belongs to account %s, expected account %s"
argument_list|,
name|extId
operator|.
name|key
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|extId
operator|.
name|accountId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|accountId
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|accountId
return|;
block|}
DECL|method|evictAccounts (Collection<ExternalId> extIds)
specifier|private
name|void
name|evictAccounts
parameter_list|(
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|extIds
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Account
operator|.
name|Id
name|id
range|:
name|extIds
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|ExternalId
operator|::
name|accountId
argument_list|)
operator|.
name|collect
argument_list|(
name|toSet
argument_list|()
argument_list|)
control|)
block|{
name|accountCache
operator|.
name|evict
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

