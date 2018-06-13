begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.restapi.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|restapi
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
name|registration
operator|.
name|DynamicMap
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
name|AcceptsCreate
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
name|IdString
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
name|ResourceNotFoundException
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
name|RestApiException
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
name|RestCollection
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
name|RestView
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
name|TopLevelResource
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
name|AccountControl
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
name|AccountResolver
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
name|AccountResource
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
DECL|class|AccountsCollection
specifier|public
class|class
name|AccountsCollection
implements|implements
name|RestCollection
argument_list|<
name|TopLevelResource
argument_list|,
name|AccountResource
argument_list|>
implements|,
name|AcceptsCreate
argument_list|<
name|TopLevelResource
argument_list|>
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
DECL|field|resolver
specifier|private
specifier|final
name|AccountResolver
name|resolver
decl_stmt|;
DECL|field|accountControlFactory
specifier|private
specifier|final
name|AccountControl
operator|.
name|Factory
name|accountControlFactory
decl_stmt|;
DECL|field|userFactory
specifier|private
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
decl_stmt|;
DECL|field|list
specifier|private
specifier|final
name|Provider
argument_list|<
name|QueryAccounts
argument_list|>
name|list
decl_stmt|;
DECL|field|views
specifier|private
specifier|final
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|AccountResource
argument_list|>
argument_list|>
name|views
decl_stmt|;
DECL|field|createAccountFactory
specifier|private
specifier|final
name|CreateAccount
operator|.
name|Factory
name|createAccountFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|AccountsCollection ( Provider<CurrentUser> self, AccountResolver resolver, AccountControl.Factory accountControlFactory, IdentifiedUser.GenericFactory userFactory, Provider<QueryAccounts> list, DynamicMap<RestView<AccountResource>> views, CreateAccount.Factory createAccountFactory)
specifier|public
name|AccountsCollection
parameter_list|(
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
parameter_list|,
name|AccountResolver
name|resolver
parameter_list|,
name|AccountControl
operator|.
name|Factory
name|accountControlFactory
parameter_list|,
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
parameter_list|,
name|Provider
argument_list|<
name|QueryAccounts
argument_list|>
name|list
parameter_list|,
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|AccountResource
argument_list|>
argument_list|>
name|views
parameter_list|,
name|CreateAccount
operator|.
name|Factory
name|createAccountFactory
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
name|resolver
operator|=
name|resolver
expr_stmt|;
name|this
operator|.
name|accountControlFactory
operator|=
name|accountControlFactory
expr_stmt|;
name|this
operator|.
name|userFactory
operator|=
name|userFactory
expr_stmt|;
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
name|this
operator|.
name|views
operator|=
name|views
expr_stmt|;
name|this
operator|.
name|createAccountFactory
operator|=
name|createAccountFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parse (TopLevelResource root, IdString id)
specifier|public
name|AccountResource
name|parse
parameter_list|(
name|TopLevelResource
name|root
parameter_list|,
name|IdString
name|id
parameter_list|)
throws|throws
name|ResourceNotFoundException
throws|,
name|AuthException
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
name|parseId
argument_list|(
name|id
operator|.
name|get
argument_list|()
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
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
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
operator|new
name|AccountResource
argument_list|(
name|user
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
name|resolver
operator|.
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
annotation|@
name|Override
DECL|method|list ()
specifier|public
name|RestView
argument_list|<
name|TopLevelResource
argument_list|>
name|list
parameter_list|()
throws|throws
name|ResourceNotFoundException
block|{
return|return
name|list
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|views ()
specifier|public
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|AccountResource
argument_list|>
argument_list|>
name|views
parameter_list|()
block|{
return|return
name|views
return|;
block|}
annotation|@
name|Override
DECL|method|create (TopLevelResource parent, IdString username)
specifier|public
name|CreateAccount
name|create
parameter_list|(
name|TopLevelResource
name|parent
parameter_list|,
name|IdString
name|username
parameter_list|)
throws|throws
name|RestApiException
block|{
return|return
name|createAccountFactory
operator|.
name|create
argument_list|(
name|username
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

