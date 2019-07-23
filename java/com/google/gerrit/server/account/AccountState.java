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
name|ImmutableMap
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
name|flogger
operator|.
name|FluentLogger
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
name|extensions
operator|.
name|client
operator|.
name|DiffPreferencesInfo
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
name|EditPreferencesInfo
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
name|server
operator|.
name|account
operator|.
name|ProjectWatches
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
name|ProjectWatches
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
name|ExternalIdNotes
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
name|Optional
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|DecoderException
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
name|ObjectId
import|;
end_import

begin_comment
comment|/**  * Superset of all information related to an Account. This includes external IDs, project watches,  * and properties from the account config file. AccountState maps one-to-one to Account.  *  *<p>Most callers should not construct AccountStates directly but rather lookup accounts via the  * account cache (see {@link AccountCache#get(Account.Id)}).  */
end_comment

begin_class
DECL|class|AccountState
specifier|public
class|class
name|AccountState
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|FluentLogger
name|logger
init|=
name|FluentLogger
operator|.
name|forEnclosingClass
argument_list|()
decl_stmt|;
comment|/**    * Creates an AccountState from the given account config.    *    * @param externalIds class to access external IDs    * @param accountConfig the account config, must already be loaded    * @return the account state, {@link Optional#empty()} if the account doesn't exist    * @throws IOException if accessing the external IDs fails    */
DECL|method|fromAccountConfig ( ExternalIds externalIds, AccountConfig accountConfig)
specifier|public
specifier|static
name|Optional
argument_list|<
name|AccountState
argument_list|>
name|fromAccountConfig
parameter_list|(
name|ExternalIds
name|externalIds
parameter_list|,
name|AccountConfig
name|accountConfig
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fromAccountConfig
argument_list|(
name|externalIds
argument_list|,
name|accountConfig
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Creates an AccountState from the given account config.    *    *<p>If external ID notes are provided the revision of the external IDs branch from which the    * external IDs for the account should be loaded is taken from the external ID notes. If external    * ID notes are not given the revision of the external IDs branch is taken from the account    * config. Updating external IDs is done via {@link ExternalIdNotes} and if external IDs were    * updated the revision of the external IDs branch in account config is outdated. Hence after    * updating external IDs the external ID notes must be provided.    *    * @param externalIds class to access external IDs    * @param accountConfig the account config, must already be loaded    * @param extIdNotes external ID notes, must already be loaded, may be {@code null}    * @return the account state, {@link Optional#empty()} if the account doesn't exist    * @throws IOException if accessing the external IDs fails    */
DECL|method|fromAccountConfig ( ExternalIds externalIds, AccountConfig accountConfig, @Nullable ExternalIdNotes extIdNotes)
specifier|public
specifier|static
name|Optional
argument_list|<
name|AccountState
argument_list|>
name|fromAccountConfig
parameter_list|(
name|ExternalIds
name|externalIds
parameter_list|,
name|AccountConfig
name|accountConfig
parameter_list|,
annotation|@
name|Nullable
name|ExternalIdNotes
name|extIdNotes
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|accountConfig
operator|.
name|getLoadedAccount
argument_list|()
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
name|Account
name|account
init|=
name|accountConfig
operator|.
name|getLoadedAccount
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|Optional
argument_list|<
name|ObjectId
argument_list|>
name|extIdsRev
init|=
name|extIdNotes
operator|!=
literal|null
condition|?
name|Optional
operator|.
name|ofNullable
argument_list|(
name|extIdNotes
operator|.
name|getRevision
argument_list|()
argument_list|)
else|:
name|accountConfig
operator|.
name|getExternalIdsRev
argument_list|()
decl_stmt|;
name|ImmutableSet
argument_list|<
name|ExternalId
argument_list|>
name|extIds
init|=
name|extIdsRev
operator|.
name|isPresent
argument_list|()
condition|?
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|externalIds
operator|.
name|byAccount
argument_list|(
name|account
operator|.
name|id
argument_list|()
argument_list|,
name|extIdsRev
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
else|:
name|ImmutableSet
operator|.
name|of
argument_list|()
decl_stmt|;
comment|// Don't leak references to AccountConfig into the AccountState, since it holds a reference to
comment|// an open Repository instance.
name|ImmutableMap
argument_list|<
name|ProjectWatchKey
argument_list|,
name|ImmutableSet
argument_list|<
name|NotifyType
argument_list|>
argument_list|>
name|projectWatches
init|=
name|accountConfig
operator|.
name|getProjectWatches
argument_list|()
decl_stmt|;
name|GeneralPreferencesInfo
name|generalPreferences
init|=
name|accountConfig
operator|.
name|getGeneralPreferences
argument_list|()
decl_stmt|;
name|DiffPreferencesInfo
name|diffPreferences
init|=
name|accountConfig
operator|.
name|getDiffPreferences
argument_list|()
decl_stmt|;
name|EditPreferencesInfo
name|editPreferences
init|=
name|accountConfig
operator|.
name|getEditPreferences
argument_list|()
decl_stmt|;
return|return
name|Optional
operator|.
name|of
argument_list|(
operator|new
name|AccountState
argument_list|(
name|account
argument_list|,
name|extIds
argument_list|,
name|projectWatches
argument_list|,
name|generalPreferences
argument_list|,
name|diffPreferences
argument_list|,
name|editPreferences
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Creates an AccountState for a given account with no external IDs, no project watches and    * default preferences.    *    * @param account the account    * @return the account state    */
DECL|method|forAccount (Account account)
specifier|public
specifier|static
name|AccountState
name|forAccount
parameter_list|(
name|Account
name|account
parameter_list|)
block|{
return|return
name|forAccount
argument_list|(
name|account
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Creates an AccountState for a given account with no project watches and default preferences.    *    * @param account the account    * @param extIds the external IDs    * @return the account state    */
DECL|method|forAccount (Account account, Collection<ExternalId> extIds)
specifier|public
specifier|static
name|AccountState
name|forAccount
parameter_list|(
name|Account
name|account
parameter_list|,
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|extIds
parameter_list|)
block|{
return|return
operator|new
name|AccountState
argument_list|(
name|account
argument_list|,
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|extIds
argument_list|)
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|,
name|GeneralPreferencesInfo
operator|.
name|defaults
argument_list|()
argument_list|,
name|DiffPreferencesInfo
operator|.
name|defaults
argument_list|()
argument_list|,
name|EditPreferencesInfo
operator|.
name|defaults
argument_list|()
argument_list|)
return|;
block|}
DECL|field|account
specifier|private
specifier|final
name|Account
name|account
decl_stmt|;
DECL|field|externalIds
specifier|private
specifier|final
name|ImmutableSet
argument_list|<
name|ExternalId
argument_list|>
name|externalIds
decl_stmt|;
DECL|field|userName
specifier|private
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|userName
decl_stmt|;
DECL|field|projectWatches
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|ProjectWatchKey
argument_list|,
name|ImmutableSet
argument_list|<
name|NotifyType
argument_list|>
argument_list|>
name|projectWatches
decl_stmt|;
DECL|field|generalPreferences
specifier|private
specifier|final
name|GeneralPreferencesInfo
name|generalPreferences
decl_stmt|;
DECL|field|diffPreferences
specifier|private
specifier|final
name|DiffPreferencesInfo
name|diffPreferences
decl_stmt|;
DECL|field|editPreferences
specifier|private
specifier|final
name|EditPreferencesInfo
name|editPreferences
decl_stmt|;
DECL|method|AccountState ( Account account, ImmutableSet<ExternalId> externalIds, ImmutableMap<ProjectWatchKey, ImmutableSet<NotifyType>> projectWatches, GeneralPreferencesInfo generalPreferences, DiffPreferencesInfo diffPreferences, EditPreferencesInfo editPreferences)
specifier|private
name|AccountState
parameter_list|(
name|Account
name|account
parameter_list|,
name|ImmutableSet
argument_list|<
name|ExternalId
argument_list|>
name|externalIds
parameter_list|,
name|ImmutableMap
argument_list|<
name|ProjectWatchKey
argument_list|,
name|ImmutableSet
argument_list|<
name|NotifyType
argument_list|>
argument_list|>
name|projectWatches
parameter_list|,
name|GeneralPreferencesInfo
name|generalPreferences
parameter_list|,
name|DiffPreferencesInfo
name|diffPreferences
parameter_list|,
name|EditPreferencesInfo
name|editPreferences
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
name|externalIds
operator|=
name|externalIds
expr_stmt|;
name|this
operator|.
name|userName
operator|=
name|ExternalId
operator|.
name|getUserName
argument_list|(
name|externalIds
argument_list|)
expr_stmt|;
name|this
operator|.
name|projectWatches
operator|=
name|projectWatches
expr_stmt|;
name|this
operator|.
name|generalPreferences
operator|=
name|generalPreferences
expr_stmt|;
name|this
operator|.
name|diffPreferences
operator|=
name|diffPreferences
expr_stmt|;
name|this
operator|.
name|editPreferences
operator|=
name|editPreferences
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
comment|/**    * Get the username, if one has been declared for this user.    *    *<p>The username is the {@link ExternalId} using the scheme {@link ExternalId#SCHEME_USERNAME}.    *    * @return the username, {@link Optional#empty()} if the user has no username, or if the username    *     is empty    */
DECL|method|getUserName ()
specifier|public
name|Optional
argument_list|<
name|String
argument_list|>
name|getUserName
parameter_list|()
block|{
return|return
name|userName
return|;
block|}
DECL|method|checkPassword (@ullable String password, String username)
specifier|public
name|boolean
name|checkPassword
parameter_list|(
annotation|@
name|Nullable
name|String
name|password
parameter_list|,
name|String
name|username
parameter_list|)
block|{
if|if
condition|(
name|password
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|ExternalId
name|id
range|:
name|getExternalIds
argument_list|()
control|)
block|{
comment|// Only process the "username:$USER" entry, which is unique.
if|if
condition|(
operator|!
name|id
operator|.
name|isScheme
argument_list|(
name|SCHEME_USERNAME
argument_list|)
operator|||
operator|!
name|username
operator|.
name|equals
argument_list|(
name|id
operator|.
name|key
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|String
name|hashedStr
init|=
name|id
operator|.
name|password
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|hashedStr
argument_list|)
condition|)
block|{
try|try
block|{
return|return
name|HashedPassword
operator|.
name|decode
argument_list|(
name|hashedStr
argument_list|)
operator|.
name|checkPassword
argument_list|(
name|password
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|DecoderException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|log
argument_list|(
literal|"DecoderException for user %s: %s "
argument_list|,
name|username
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/** The external identities that identify the account holder. */
DECL|method|getExternalIds ()
specifier|public
name|ImmutableSet
argument_list|<
name|ExternalId
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
name|ImmutableMap
argument_list|<
name|ProjectWatchKey
argument_list|,
name|ImmutableSet
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
comment|/** The general preferences of the account. */
DECL|method|getGeneralPreferences ()
specifier|public
name|GeneralPreferencesInfo
name|getGeneralPreferences
parameter_list|()
block|{
return|return
name|generalPreferences
return|;
block|}
comment|/** The diff preferences of the account. */
DECL|method|getDiffPreferences ()
specifier|public
name|DiffPreferencesInfo
name|getDiffPreferences
parameter_list|()
block|{
return|return
name|diffPreferences
return|;
block|}
comment|/** The edit preferences of the account. */
DECL|method|getEditPreferences ()
specifier|public
name|EditPreferencesInfo
name|getEditPreferences
parameter_list|()
block|{
return|return
name|editPreferences
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
name|MoreObjects
operator|.
name|ToStringHelper
name|h
init|=
name|MoreObjects
operator|.
name|toStringHelper
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|h
operator|.
name|addValue
argument_list|(
name|getAccount
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|h
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

