begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
name|ImmutableSetMultimap
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

begin_comment
comment|/** Class to access accounts by email. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|Emails
specifier|public
class|class
name|Emails
block|{
DECL|field|externalIds
specifier|private
specifier|final
name|ExternalIds
name|externalIds
decl_stmt|;
DECL|field|queryProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|InternalAccountQuery
argument_list|>
name|queryProvider
decl_stmt|;
annotation|@
name|Inject
DECL|method|Emails (ExternalIds externalIds, Provider<InternalAccountQuery> queryProvider)
specifier|public
name|Emails
parameter_list|(
name|ExternalIds
name|externalIds
parameter_list|,
name|Provider
argument_list|<
name|InternalAccountQuery
argument_list|>
name|queryProvider
parameter_list|)
block|{
name|this
operator|.
name|externalIds
operator|=
name|externalIds
expr_stmt|;
name|this
operator|.
name|queryProvider
operator|=
name|queryProvider
expr_stmt|;
block|}
comment|/**    * Returns the accounts with the given email.    *    *<p>Each email should belong to a single account only. This means if more than one account is    * returned there is an inconsistency in the external IDs.    *    *<p>The accounts are retrieved via the external ID cache. Each access to the external ID cache    * requires reading the SHA1 of the refs/meta/external-ids branch. If accounts for multiple emails    * are needed it is more efficient to use {@link #getAccountsFor(String...)} as this method reads    * the SHA1 of the refs/meta/external-ids branch only once (and not once per email).    *    *<p>In addition accounts are included that have the given email as preferred email even if they    * have no external ID for the preferred email. Having accounts with a preferred email that does    * not exist as external ID is an inconsistency, but existing functionality relies on still    * getting those accounts, which is why they are included. Accounts by preferred email are fetched    * from the account index.    *    * @see #getAccountsFor(String...)    */
DECL|method|getAccountFor (String email)
specifier|public
name|ImmutableSet
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|getAccountFor
parameter_list|(
name|String
name|email
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
block|{
return|return
name|Streams
operator|.
name|concat
argument_list|(
name|externalIds
operator|.
name|byEmail
argument_list|(
name|email
argument_list|)
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
name|accountId
argument_list|()
argument_list|)
argument_list|,
name|queryProvider
operator|.
name|get
argument_list|()
operator|.
name|byPreferredEmail
argument_list|(
name|email
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|a
lambda|->
name|a
operator|.
name|getAccount
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|toImmutableSet
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns the accounts for the given emails.    *    * @see #getAccountFor(String)    */
DECL|method|getAccountsFor (String... emails)
specifier|public
name|ImmutableSetMultimap
argument_list|<
name|String
argument_list|,
name|Account
operator|.
name|Id
argument_list|>
name|getAccountsFor
parameter_list|(
name|String
modifier|...
name|emails
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
block|{
name|ImmutableSetMultimap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|Account
operator|.
name|Id
argument_list|>
name|builder
init|=
name|ImmutableSetMultimap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|externalIds
operator|.
name|byEmails
argument_list|(
name|emails
argument_list|)
operator|.
name|entries
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|forEach
argument_list|(
name|e
lambda|->
name|builder
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|accountId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|queryProvider
operator|.
name|get
argument_list|()
operator|.
name|byPreferredEmail
argument_list|(
name|emails
argument_list|)
operator|.
name|entries
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|forEach
argument_list|(
name|e
lambda|->
name|builder
operator|.
name|put
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|getAccount
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

