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
name|reviewdb
operator|.
name|client
operator|.
name|AccountExternalId
operator|.
name|SCHEME_MAILTO
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
name|reviewdb
operator|.
name|client
operator|.
name|AccountExternalId
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
name|cache
operator|.
name|Cache
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
name|CacheBuilder
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
name|client
operator|.
name|AccountExternalId
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
name|AccountProjectWatch
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
name|CurrentUser
operator|.
name|PropertyKey
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
name|WatchConfig
operator|.
name|ProjectWatchKey
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_class
DECL|class|AccountState
specifier|public
class|class
name|AccountState
block|{
DECL|field|ACCOUNT_ID_FUNCTION
specifier|public
specifier|static
specifier|final
name|Function
argument_list|<
name|AccountState
argument_list|,
name|Account
operator|.
name|Id
argument_list|>
name|ACCOUNT_ID_FUNCTION
init|=
operator|new
name|Function
argument_list|<
name|AccountState
argument_list|,
name|Account
operator|.
name|Id
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Account
operator|.
name|Id
name|apply
parameter_list|(
name|AccountState
name|in
parameter_list|)
block|{
return|return
name|in
operator|.
name|getAccount
argument_list|()
operator|.
name|getId
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|account
specifier|private
specifier|final
name|Account
name|account
decl_stmt|;
DECL|field|internalGroups
specifier|private
specifier|final
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|internalGroups
decl_stmt|;
DECL|field|externalIds
specifier|private
specifier|final
name|Collection
argument_list|<
name|AccountExternalId
argument_list|>
name|externalIds
decl_stmt|;
DECL|field|projectWatches
specifier|private
specifier|final
name|Map
argument_list|<
name|ProjectWatchKey
argument_list|,
name|Set
argument_list|<
name|NotifyType
argument_list|>
argument_list|>
name|projectWatches
decl_stmt|;
DECL|field|properties
specifier|private
name|Cache
argument_list|<
name|IdentifiedUser
operator|.
name|PropertyKey
argument_list|<
name|Object
argument_list|>
argument_list|,
name|Object
argument_list|>
name|properties
decl_stmt|;
DECL|method|AccountState (Account account, Set<AccountGroup.UUID> actualGroups, Collection<AccountExternalId> externalIds, Map<ProjectWatchKey, Set<NotifyType>> projectWatches)
specifier|public
name|AccountState
parameter_list|(
name|Account
name|account
parameter_list|,
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|actualGroups
parameter_list|,
name|Collection
argument_list|<
name|AccountExternalId
argument_list|>
name|externalIds
parameter_list|,
name|Map
argument_list|<
name|ProjectWatchKey
argument_list|,
name|Set
argument_list|<
name|NotifyType
argument_list|>
argument_list|>
name|projectWatches
parameter_list|)
block|{
name|this
operator|.
name|account
operator|=
name|account
expr_stmt|;
name|this
operator|.
name|internalGroups
operator|=
name|actualGroups
expr_stmt|;
name|this
operator|.
name|externalIds
operator|=
name|externalIds
expr_stmt|;
name|this
operator|.
name|projectWatches
operator|=
name|projectWatches
expr_stmt|;
name|this
operator|.
name|account
operator|.
name|setUserName
argument_list|(
name|getUserName
argument_list|(
name|externalIds
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Get the cached account metadata. */
DECL|method|getAccount ()
specifier|public
name|Account
name|getAccount
parameter_list|()
block|{
return|return
name|account
return|;
block|}
comment|/**    * Get the username, if one has been declared for this user.    *<p>    * The username is the {@link AccountExternalId} using the scheme    * {@link AccountExternalId#SCHEME_USERNAME}.    */
DECL|method|getUserName ()
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
return|return
name|account
operator|.
name|getUserName
argument_list|()
return|;
block|}
comment|/** @return the password matching the requested username; or null. */
DECL|method|getPassword (String username)
specifier|public
name|String
name|getPassword
parameter_list|(
name|String
name|username
parameter_list|)
block|{
for|for
control|(
name|AccountExternalId
name|id
range|:
name|getExternalIds
argument_list|()
control|)
block|{
if|if
condition|(
name|id
operator|.
name|isScheme
argument_list|(
name|AccountExternalId
operator|.
name|SCHEME_USERNAME
argument_list|)
operator|&&
name|username
operator|.
name|equals
argument_list|(
name|id
operator|.
name|getSchemeRest
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|id
operator|.
name|getPassword
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/** The external identities that identify the account holder. */
DECL|method|getExternalIds ()
specifier|public
name|Collection
argument_list|<
name|AccountExternalId
argument_list|>
name|getExternalIds
parameter_list|()
block|{
return|return
name|externalIds
return|;
block|}
comment|/** The project watches of the account. */
DECL|method|getProjectWatches ()
specifier|public
name|Map
argument_list|<
name|ProjectWatchKey
argument_list|,
name|Set
argument_list|<
name|NotifyType
argument_list|>
argument_list|>
name|getProjectWatches
parameter_list|()
block|{
return|return
name|projectWatches
return|;
block|}
comment|/** The set of groups maintained directly within the Gerrit database. */
DECL|method|getInternalGroups ()
specifier|public
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|getInternalGroups
parameter_list|()
block|{
return|return
name|internalGroups
return|;
block|}
DECL|method|getUserName (Collection<AccountExternalId> ids)
specifier|public
specifier|static
name|String
name|getUserName
parameter_list|(
name|Collection
argument_list|<
name|AccountExternalId
argument_list|>
name|ids
parameter_list|)
block|{
for|for
control|(
name|AccountExternalId
name|id
range|:
name|ids
control|)
block|{
if|if
condition|(
name|id
operator|.
name|isScheme
argument_list|(
name|SCHEME_USERNAME
argument_list|)
condition|)
block|{
return|return
name|id
operator|.
name|getSchemeRest
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|getEmails (Collection<AccountExternalId> ids)
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|getEmails
parameter_list|(
name|Collection
argument_list|<
name|AccountExternalId
argument_list|>
name|ids
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|emails
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountExternalId
name|id
range|:
name|ids
control|)
block|{
if|if
condition|(
name|id
operator|.
name|isScheme
argument_list|(
name|SCHEME_MAILTO
argument_list|)
condition|)
block|{
name|emails
operator|.
name|add
argument_list|(
name|id
operator|.
name|getSchemeRest
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|emails
return|;
block|}
comment|/**    * Lookup a previously stored property.    *<p>    * All properties are automatically cleared when the account cache invalidates    * the {@code AccountState}. This method is thread-safe.    *    * @param key unique property key.    * @return previously stored value, or {@code null}.    */
annotation|@
name|Nullable
DECL|method|get (PropertyKey<T> key)
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|get
parameter_list|(
name|PropertyKey
argument_list|<
name|T
argument_list|>
name|key
parameter_list|)
block|{
name|Cache
argument_list|<
name|PropertyKey
argument_list|<
name|Object
argument_list|>
argument_list|,
name|Object
argument_list|>
name|p
init|=
name|properties
argument_list|(
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|T
name|value
init|=
operator|(
name|T
operator|)
name|p
operator|.
name|getIfPresent
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|value
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Store a property for later retrieval.    *<p>    * This method is thread-safe.    *    * @param key unique property key.    * @param value value to store; or {@code null} to clear the value.    */
DECL|method|put (PropertyKey<T> key, @Nullable T value)
specifier|public
parameter_list|<
name|T
parameter_list|>
name|void
name|put
parameter_list|(
name|PropertyKey
argument_list|<
name|T
argument_list|>
name|key
parameter_list|,
annotation|@
name|Nullable
name|T
name|value
parameter_list|)
block|{
name|Cache
argument_list|<
name|PropertyKey
argument_list|<
name|Object
argument_list|>
argument_list|,
name|Object
argument_list|>
name|p
init|=
name|properties
argument_list|(
name|value
operator|!=
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|PropertyKey
argument_list|<
name|Object
argument_list|>
name|k
init|=
operator|(
name|PropertyKey
argument_list|<
name|Object
argument_list|>
operator|)
name|key
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|p
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|p
operator|.
name|invalidate
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|properties ( boolean allocate)
specifier|private
specifier|synchronized
name|Cache
argument_list|<
name|PropertyKey
argument_list|<
name|Object
argument_list|>
argument_list|,
name|Object
argument_list|>
name|properties
parameter_list|(
name|boolean
name|allocate
parameter_list|)
block|{
if|if
condition|(
name|properties
operator|==
literal|null
operator|&&
name|allocate
condition|)
block|{
name|properties
operator|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|concurrencyLevel
argument_list|(
literal|1
argument_list|)
operator|.
name|initialCapacity
argument_list|(
literal|16
argument_list|)
comment|// Use weakKeys to ensure plugins that garbage collect will also
comment|// eventually release data held in any still live AccountState.
operator|.
name|weakKeys
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
return|return
name|properties
return|;
block|}
block|}
end_class

end_unit

