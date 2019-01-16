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
DECL|package|com.google.gerrit.server.query.account
package|package
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
package|;
end_package

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
name|toList
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
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ArrayListMultimap
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
name|ImmutableList
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
name|ImmutableListMultimap
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
name|Multimap
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
name|StorageException
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
name|index
operator|.
name|FieldDef
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
name|index
operator|.
name|IndexConfig
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
name|index
operator|.
name|Schema
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
name|index
operator|.
name|query
operator|.
name|InternalQuery
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
name|Project
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
name|index
operator|.
name|account
operator|.
name|AccountField
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
name|AccountIndexCollection
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
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Set
import|;
end_import

begin_comment
comment|/**  * Query wrapper for the account index.  *  *<p>Instances are one-time-use. Other singleton classes should inject a Provider rather than  * holding on to a single instance.  */
end_comment

begin_class
DECL|class|InternalAccountQuery
specifier|public
class|class
name|InternalAccountQuery
extends|extends
name|InternalQuery
argument_list|<
name|AccountState
argument_list|,
name|InternalAccountQuery
argument_list|>
block|{
annotation|@
name|Inject
DECL|method|InternalAccountQuery ( AccountQueryProcessor queryProcessor, AccountIndexCollection indexes, IndexConfig indexConfig)
name|InternalAccountQuery
parameter_list|(
name|AccountQueryProcessor
name|queryProcessor
parameter_list|,
name|AccountIndexCollection
name|indexes
parameter_list|,
name|IndexConfig
name|indexConfig
parameter_list|)
block|{
name|super
argument_list|(
name|queryProcessor
argument_list|,
name|indexes
argument_list|,
name|indexConfig
argument_list|)
expr_stmt|;
block|}
DECL|method|byDefault (String query)
specifier|public
name|List
argument_list|<
name|AccountState
argument_list|>
name|byDefault
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|StorageException
block|{
return|return
name|query
argument_list|(
name|AccountPredicates
operator|.
name|defaultPredicate
argument_list|(
name|schema
argument_list|()
argument_list|,
literal|true
argument_list|,
name|query
argument_list|)
argument_list|)
return|;
block|}
DECL|method|byExternalId (String scheme, String id)
specifier|public
name|List
argument_list|<
name|AccountState
argument_list|>
name|byExternalId
parameter_list|(
name|String
name|scheme
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|StorageException
block|{
return|return
name|byExternalId
argument_list|(
name|ExternalId
operator|.
name|Key
operator|.
name|create
argument_list|(
name|scheme
argument_list|,
name|id
argument_list|)
argument_list|)
return|;
block|}
DECL|method|byExternalId (ExternalId.Key externalId)
specifier|public
name|List
argument_list|<
name|AccountState
argument_list|>
name|byExternalId
parameter_list|(
name|ExternalId
operator|.
name|Key
name|externalId
parameter_list|)
throws|throws
name|StorageException
block|{
return|return
name|query
argument_list|(
name|AccountPredicates
operator|.
name|externalIdIncludingSecondaryEmails
argument_list|(
name|externalId
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|byFullName (String fullName)
specifier|public
name|List
argument_list|<
name|AccountState
argument_list|>
name|byFullName
parameter_list|(
name|String
name|fullName
parameter_list|)
throws|throws
name|StorageException
block|{
return|return
name|query
argument_list|(
name|AccountPredicates
operator|.
name|fullName
argument_list|(
name|fullName
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Queries for accounts that have a preferred email that exactly matches the given email.    *    * @param email preferred email by which accounts should be found    * @return list of accounts that have a preferred email that exactly matches the given email    * @throws StorageException if query cannot be parsed    */
DECL|method|byPreferredEmail (String email)
specifier|public
name|List
argument_list|<
name|AccountState
argument_list|>
name|byPreferredEmail
parameter_list|(
name|String
name|email
parameter_list|)
throws|throws
name|StorageException
block|{
if|if
condition|(
name|hasPreferredEmailExact
argument_list|()
condition|)
block|{
return|return
name|query
argument_list|(
name|AccountPredicates
operator|.
name|preferredEmailExact
argument_list|(
name|email
argument_list|)
argument_list|)
return|;
block|}
if|if
condition|(
operator|!
name|hasPreferredEmail
argument_list|()
condition|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
return|return
name|query
argument_list|(
name|AccountPredicates
operator|.
name|preferredEmail
argument_list|(
name|email
argument_list|)
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|a
lambda|->
name|a
operator|.
name|getAccount
argument_list|()
operator|.
name|getPreferredEmail
argument_list|()
operator|.
name|equals
argument_list|(
name|email
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Makes multiple queries for accounts by preferred email (exact match).    *    * @param emails preferred emails by which accounts should be found    * @return multimap of the given emails to accounts that have a preferred email that exactly    *     matches this email    * @throws StorageException if query cannot be parsed    */
DECL|method|byPreferredEmail (String... emails)
specifier|public
name|Multimap
argument_list|<
name|String
argument_list|,
name|AccountState
argument_list|>
name|byPreferredEmail
parameter_list|(
name|String
modifier|...
name|emails
parameter_list|)
throws|throws
name|StorageException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|emailList
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|emails
argument_list|)
decl_stmt|;
if|if
condition|(
name|hasPreferredEmailExact
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|List
argument_list|<
name|AccountState
argument_list|>
argument_list|>
name|r
init|=
name|query
argument_list|(
name|emailList
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|AccountPredicates
operator|::
name|preferredEmailExact
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Multimap
argument_list|<
name|String
argument_list|,
name|AccountState
argument_list|>
name|accountsByEmail
init|=
name|ArrayListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|emailList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|accountsByEmail
operator|.
name|putAll
argument_list|(
name|emailList
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|r
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|accountsByEmail
return|;
block|}
if|if
condition|(
operator|!
name|hasPreferredEmail
argument_list|()
condition|)
block|{
return|return
name|ImmutableListMultimap
operator|.
name|of
argument_list|()
return|;
block|}
name|List
argument_list|<
name|List
argument_list|<
name|AccountState
argument_list|>
argument_list|>
name|r
init|=
name|query
argument_list|(
name|emailList
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|AccountPredicates
operator|::
name|preferredEmail
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Multimap
argument_list|<
name|String
argument_list|,
name|AccountState
argument_list|>
name|accountsByEmail
init|=
name|ArrayListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|emailList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|email
init|=
name|emailList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|AccountState
argument_list|>
name|matchingAccounts
init|=
name|r
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|a
lambda|->
name|a
operator|.
name|getAccount
argument_list|()
operator|.
name|getPreferredEmail
argument_list|()
operator|.
name|equals
argument_list|(
name|email
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|toSet
argument_list|()
argument_list|)
decl_stmt|;
name|accountsByEmail
operator|.
name|putAll
argument_list|(
name|email
argument_list|,
name|matchingAccounts
argument_list|)
expr_stmt|;
block|}
return|return
name|accountsByEmail
return|;
block|}
DECL|method|byWatchedProject (Project.NameKey project)
specifier|public
name|List
argument_list|<
name|AccountState
argument_list|>
name|byWatchedProject
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|)
throws|throws
name|StorageException
block|{
return|return
name|query
argument_list|(
name|AccountPredicates
operator|.
name|watchedProject
argument_list|(
name|project
argument_list|)
argument_list|)
return|;
block|}
DECL|method|hasField (FieldDef<AccountState, ?> field)
specifier|private
name|boolean
name|hasField
parameter_list|(
name|FieldDef
argument_list|<
name|AccountState
argument_list|,
name|?
argument_list|>
name|field
parameter_list|)
block|{
name|Schema
argument_list|<
name|AccountState
argument_list|>
name|s
init|=
name|schema
argument_list|()
decl_stmt|;
return|return
operator|(
name|s
operator|!=
literal|null
operator|&&
name|s
operator|.
name|hasField
argument_list|(
name|field
argument_list|)
operator|)
return|;
block|}
DECL|method|hasPreferredEmail ()
specifier|private
name|boolean
name|hasPreferredEmail
parameter_list|()
block|{
return|return
name|hasField
argument_list|(
name|AccountField
operator|.
name|PREFERRED_EMAIL
argument_list|)
return|;
block|}
DECL|method|hasPreferredEmailExact ()
specifier|private
name|boolean
name|hasPreferredEmailExact
parameter_list|()
block|{
return|return
name|hasField
argument_list|(
name|AccountField
operator|.
name|PREFERRED_EMAIL_EXACT
argument_list|)
return|;
block|}
block|}
end_class

end_unit

