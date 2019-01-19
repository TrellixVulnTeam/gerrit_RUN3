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
name|server
operator|.
name|AnonymousUser
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
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
DECL|class|AccountResolver
specifier|public
class|class
name|AccountResolver
block|{
DECL|field|self
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
decl_stmt|;
DECL|field|realm
specifier|private
specifier|final
name|Realm
name|realm
decl_stmt|;
DECL|field|accounts
specifier|private
specifier|final
name|Accounts
name|accounts
decl_stmt|;
DECL|field|byId
specifier|private
specifier|final
name|AccountCache
name|byId
decl_stmt|;
DECL|field|userFactory
specifier|private
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
decl_stmt|;
DECL|field|accountControlFactory
specifier|private
specifier|final
name|AccountControl
operator|.
name|Factory
name|accountControlFactory
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
DECL|field|emails
specifier|private
specifier|final
name|Emails
name|emails
decl_stmt|;
annotation|@
name|Inject
DECL|method|AccountResolver ( Provider<CurrentUser> self, Realm realm, Accounts accounts, AccountCache byId, IdentifiedUser.GenericFactory userFactory, AccountControl.Factory accountControlFactory, Provider<InternalAccountQuery> accountQueryProvider, Emails emails)
name|AccountResolver
parameter_list|(
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
parameter_list|,
name|Realm
name|realm
parameter_list|,
name|Accounts
name|accounts
parameter_list|,
name|AccountCache
name|byId
parameter_list|,
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
parameter_list|,
name|AccountControl
operator|.
name|Factory
name|accountControlFactory
parameter_list|,
name|Provider
argument_list|<
name|InternalAccountQuery
argument_list|>
name|accountQueryProvider
parameter_list|,
name|Emails
name|emails
parameter_list|)
block|{
name|this
operator|.
name|self
operator|=
name|self
expr_stmt|;
name|this
operator|.
name|realm
operator|=
name|realm
expr_stmt|;
name|this
operator|.
name|accounts
operator|=
name|accounts
expr_stmt|;
name|this
operator|.
name|byId
operator|=
name|byId
expr_stmt|;
name|this
operator|.
name|userFactory
operator|=
name|userFactory
expr_stmt|;
name|this
operator|.
name|accountControlFactory
operator|=
name|accountControlFactory
expr_stmt|;
name|this
operator|.
name|accountQueryProvider
operator|=
name|accountQueryProvider
expr_stmt|;
name|this
operator|.
name|emails
operator|=
name|emails
expr_stmt|;
block|}
comment|/**    * Locate exactly one account matching the input string.    *    * @param input a string of the format "Full Name&lt;email@example&gt;", just the email address    *     ("email@example"), a full name ("Full Name"), an account ID ("18419") or a user name    *     ("username").    * @return the single account that matches; null if no account matches or there are multiple    *     candidates. If {@code input} is a numeric string, returns an account if and only if that    *     number corresponds to an actual account ID.    */
DECL|method|find (String input)
specifier|public
name|Account
name|find
parameter_list|(
name|String
name|input
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|r
init|=
name|findAll
argument_list|(
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|byId
operator|.
name|get
argument_list|(
name|r
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|map
argument_list|(
name|AccountState
operator|::
name|getAccount
argument_list|)
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
return|;
block|}
name|Account
name|match
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Account
operator|.
name|Id
name|id
range|:
name|r
control|)
block|{
name|Optional
argument_list|<
name|Account
argument_list|>
name|account
init|=
name|byId
operator|.
name|get
argument_list|(
name|id
argument_list|)
operator|.
name|map
argument_list|(
name|AccountState
operator|::
name|getAccount
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|account
operator|.
name|map
argument_list|(
name|Account
operator|::
name|isActive
argument_list|)
operator|.
name|orElse
argument_list|(
literal|false
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|match
operator|!=
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|match
operator|=
name|account
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
return|return
name|match
return|;
block|}
comment|/**    * Find all accounts matching the input string.    *    * @param input a string of the format "Full Name&lt;email@example&gt;", just the email address    *     ("email@example"), a full name ("Full Name"), an account ID ("18419") or a user name    *     ("username").    * @return the accounts that match, empty set if none. Never null. If {@code input} is a numeric    *     string, returns a singleton set if that number corresponds to a real account ID, and an    *     empty set otherwise if it does not.    */
DECL|method|findAll (String input)
specifier|public
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|findAll
parameter_list|(
name|String
name|input
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|Matcher
name|m
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^.* \\(([1-9][0-9]*)\\)$"
argument_list|)
operator|.
name|matcher
argument_list|(
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|matches
argument_list|()
condition|)
block|{
name|Optional
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|id
init|=
name|Account
operator|.
name|Id
operator|.
name|tryParse
argument_list|(
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|.
name|isPresent
argument_list|()
condition|)
block|{
comment|// Short circuit if the format matched.
return|return
name|Streams
operator|.
name|stream
argument_list|(
name|accounts
operator|.
name|get
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
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
operator|.
name|collect
argument_list|(
name|toImmutableSet
argument_list|()
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|input
operator|.
name|matches
argument_list|(
literal|"^[1-9][0-9]*$"
argument_list|)
condition|)
block|{
name|Optional
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|id
init|=
name|Account
operator|.
name|Id
operator|.
name|tryParse
argument_list|(
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|.
name|isPresent
argument_list|()
condition|)
block|{
comment|// Short circuit if the format matched.
return|return
name|Streams
operator|.
name|stream
argument_list|(
name|accounts
operator|.
name|get
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
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
operator|.
name|collect
argument_list|(
name|toImmutableSet
argument_list|()
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|ExternalId
operator|.
name|isValidUsername
argument_list|(
name|input
argument_list|)
condition|)
block|{
name|Optional
argument_list|<
name|AccountState
argument_list|>
name|who
init|=
name|byId
operator|.
name|getByUsername
argument_list|(
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|who
operator|.
name|isPresent
argument_list|()
condition|)
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|(
name|who
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
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
comment|// Don't short-circuit just because the format matches.
block|}
return|return
name|findAllByNameOrEmail
argument_list|(
name|input
argument_list|)
return|;
block|}
comment|/**    * Locate exactly one account matching the name or name/email string.    *    * @param nameOrEmail a string of the format "Full Name&lt;email@example&gt;", just the email    *     address ("email@example"), a full name ("Full Name").    * @return the single account that matches; null if no account matches or there are multiple    *     candidates.    */
DECL|method|findByNameOrEmail (String nameOrEmail)
specifier|public
name|Account
name|findByNameOrEmail
parameter_list|(
name|String
name|nameOrEmail
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
block|{
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|r
init|=
name|findAllByNameOrEmail
argument_list|(
name|nameOrEmail
argument_list|)
decl_stmt|;
return|return
name|r
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|?
name|byId
operator|.
name|get
argument_list|(
name|r
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|map
argument_list|(
name|AccountState
operator|::
name|getAccount
argument_list|)
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
else|:
literal|null
return|;
block|}
comment|/**    * Locate exactly one account matching the name or name/email string.    *    * @param nameOrEmail a string of the format "Full Name&lt;email@example&gt;", just the email    *     address ("email@example"), a full name ("Full Name").    * @return the accounts that match, empty collection if none. Never null.    */
DECL|method|findAllByNameOrEmail (String nameOrEmail)
specifier|public
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|findAllByNameOrEmail
parameter_list|(
name|String
name|nameOrEmail
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
block|{
name|int
name|lt
init|=
name|nameOrEmail
operator|.
name|indexOf
argument_list|(
literal|'<'
argument_list|)
decl_stmt|;
name|int
name|gt
init|=
name|nameOrEmail
operator|.
name|indexOf
argument_list|(
literal|'>'
argument_list|)
decl_stmt|;
if|if
condition|(
name|lt
operator|>=
literal|0
operator|&&
name|gt
operator|>
name|lt
operator|&&
name|nameOrEmail
operator|.
name|contains
argument_list|(
literal|"@"
argument_list|)
condition|)
block|{
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|ids
init|=
name|emails
operator|.
name|getAccountFor
argument_list|(
name|nameOrEmail
operator|.
name|substring
argument_list|(
name|lt
operator|+
literal|1
argument_list|,
name|gt
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|ids
operator|.
name|isEmpty
argument_list|()
operator|||
name|ids
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|ids
return|;
block|}
comment|// more than one match, try to return the best one
name|String
name|name
init|=
name|nameOrEmail
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lt
operator|-
literal|1
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|nameMatches
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Account
operator|.
name|Id
name|id
range|:
name|ids
control|)
block|{
name|Optional
argument_list|<
name|Account
argument_list|>
name|a
init|=
name|byId
operator|.
name|get
argument_list|(
name|id
argument_list|)
operator|.
name|map
argument_list|(
name|AccountState
operator|::
name|getAccount
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|isPresent
argument_list|()
operator|&&
name|name
operator|.
name|equals
argument_list|(
name|a
operator|.
name|get
argument_list|()
operator|.
name|getFullName
argument_list|()
argument_list|)
condition|)
block|{
name|nameMatches
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Short circuit if the format matched.
return|return
name|nameMatches
operator|.
name|isEmpty
argument_list|()
condition|?
name|ids
else|:
name|nameMatches
return|;
block|}
if|if
condition|(
name|nameOrEmail
operator|.
name|contains
argument_list|(
literal|"@"
argument_list|)
condition|)
block|{
comment|// Short circuit if the format matched.
return|return
name|emails
operator|.
name|getAccountFor
argument_list|(
name|nameOrEmail
argument_list|)
return|;
block|}
name|Account
operator|.
name|Id
name|id
init|=
name|realm
operator|.
name|lookup
argument_list|(
name|nameOrEmail
argument_list|)
decl_stmt|;
comment|// Short circuit only if it returned a result.
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|id
argument_list|)
return|;
block|}
name|List
argument_list|<
name|AccountState
argument_list|>
name|m
init|=
name|accountQueryProvider
operator|.
name|get
argument_list|()
operator|.
name|byFullName
argument_list|(
name|nameOrEmail
argument_list|)
decl_stmt|;
comment|// Short circuit only if it returned exactly one result.
if|if
condition|(
name|m
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|m
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getAccount
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
comment|// At this point we have no clue. Just perform a whole bunch of suggestions
comment|// and pray we come up with a reasonable result list.
comment|// TODO(dborowitz): This doesn't match the documentation; consider whether it's possible to be
comment|// more strict here.
return|return
name|accountQueryProvider
operator|.
name|get
argument_list|()
operator|.
name|byDefault
argument_list|(
name|nameOrEmail
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
operator|.
name|collect
argument_list|(
name|toSet
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Parses a account ID from a request body and returns the user.    *    * @param id ID of the account, can be a string of the format "{@code Full Name    *<email@example.com>}", just the email address, a full name if it is unique, an account ID,    *     a user name or "{@code self}" for the calling user    * @return the user, never null.    * @throws UnprocessableEntityException thrown if the account ID cannot be resolved or if the    *     account is not visible to the calling user    */
DECL|method|parse (String id)
specifier|public
name|IdentifiedUser
name|parse
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|AuthException
throws|,
name|UnprocessableEntityException
throws|,
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
return|return
name|parseOnBehalfOf
argument_list|(
literal|null
argument_list|,
name|id
argument_list|)
return|;
block|}
comment|/**    * Parses an account ID and returns the user without making any permission check whether the    * current user can see the account.    *    * @param id ID of the account, can be a string of the format "{@code Full Name    *<email@example.com>}", just the email address, a full name if it is unique, an account ID,    *     a user name or "{@code self}" for the calling user    * @return the user, null if no user is found for the given account ID    * @throws AuthException thrown if 'self' is used as account ID and the current user is not    *     authenticated    * @throws OrmException    * @throws ConfigInvalidException    * @throws IOException    */
DECL|method|parseId (String id)
specifier|public
name|IdentifiedUser
name|parseId
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|AuthException
throws|,
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
return|return
name|parseIdOnBehalfOf
argument_list|(
literal|null
argument_list|,
name|id
argument_list|)
return|;
block|}
comment|/**    * Like {@link #parse(String)}, but also sets the {@link CurrentUser#getRealUser()} on the result.    */
DECL|method|parseOnBehalfOf (@ullable CurrentUser caller, String id)
specifier|public
name|IdentifiedUser
name|parseOnBehalfOf
parameter_list|(
annotation|@
name|Nullable
name|CurrentUser
name|caller
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|AuthException
throws|,
name|UnprocessableEntityException
throws|,
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|IdentifiedUser
name|user
init|=
name|parseIdOnBehalfOf
argument_list|(
name|caller
argument_list|,
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
operator|||
operator|!
name|accountControlFactory
operator|.
name|get
argument_list|()
operator|.
name|canSee
argument_list|(
name|user
operator|.
name|getAccount
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
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
literal|"Account '%s' is not found or ambiguous"
argument_list|,
name|id
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|user
return|;
block|}
DECL|method|parseIdOnBehalfOf (@ullable CurrentUser caller, String id)
specifier|private
name|IdentifiedUser
name|parseIdOnBehalfOf
parameter_list|(
annotation|@
name|Nullable
name|CurrentUser
name|caller
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|AuthException
throws|,
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
if|if
condition|(
name|id
operator|.
name|equals
argument_list|(
literal|"self"
argument_list|)
condition|)
block|{
name|CurrentUser
name|user
init|=
name|self
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|user
operator|.
name|isIdentifiedUser
argument_list|()
condition|)
block|{
return|return
name|user
operator|.
name|asIdentifiedUser
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|user
operator|instanceof
name|AnonymousUser
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"Authentication required"
argument_list|)
throw|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
name|Account
name|match
init|=
name|find
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|match
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|CurrentUser
name|realUser
init|=
name|caller
operator|!=
literal|null
condition|?
name|caller
operator|.
name|getRealUser
argument_list|()
else|:
literal|null
decl_stmt|;
return|return
name|userFactory
operator|.
name|runAs
argument_list|(
literal|null
argument_list|,
name|match
operator|.
name|getId
argument_list|()
argument_list|,
name|realUser
argument_list|)
return|;
block|}
block|}
end_class

end_unit

