begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
name|Response
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
name|RestModifyView
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
name|PutPreferred
operator|.
name|Input
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
name|util
operator|.
name|Collections
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|PutPreferred
specifier|public
class|class
name|PutPreferred
implements|implements
name|RestModifyView
argument_list|<
name|AccountResource
operator|.
name|Email
argument_list|,
name|Input
argument_list|>
block|{
DECL|class|Input
specifier|static
class|class
name|Input
block|{   }
DECL|field|self
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
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
DECL|field|byIdCache
specifier|private
specifier|final
name|AccountCache
name|byIdCache
decl_stmt|;
annotation|@
name|Inject
DECL|method|PutPreferred (Provider<CurrentUser> self, Provider<ReviewDb> dbProvider, AccountCache byIdCache)
name|PutPreferred
parameter_list|(
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
parameter_list|,
name|AccountCache
name|byIdCache
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
name|dbProvider
operator|=
name|dbProvider
expr_stmt|;
name|this
operator|.
name|byIdCache
operator|=
name|byIdCache
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (AccountResource.Email rsrc, Input input)
specifier|public
name|Response
argument_list|<
name|String
argument_list|>
name|apply
parameter_list|(
name|AccountResource
operator|.
name|Email
name|rsrc
parameter_list|,
name|Input
name|input
parameter_list|)
throws|throws
name|AuthException
throws|,
name|ResourceNotFoundException
throws|,
name|OrmException
block|{
if|if
condition|(
name|self
operator|.
name|get
argument_list|()
operator|!=
name|rsrc
operator|.
name|getUser
argument_list|()
operator|&&
operator|!
name|self
operator|.
name|get
argument_list|()
operator|.
name|getCapabilities
argument_list|()
operator|.
name|canModifyAccount
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"not allowed to set preferred email address"
argument_list|)
throw|;
block|}
return|return
name|apply
argument_list|(
name|rsrc
operator|.
name|getUser
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getEmail
argument_list|()
argument_list|)
return|;
block|}
DECL|method|apply (IdentifiedUser user, String email)
specifier|public
name|Response
argument_list|<
name|String
argument_list|>
name|apply
parameter_list|(
name|IdentifiedUser
name|user
parameter_list|,
name|String
name|email
parameter_list|)
throws|throws
name|ResourceNotFoundException
throws|,
name|OrmException
block|{
name|Account
name|a
init|=
name|dbProvider
operator|.
name|get
argument_list|()
operator|.
name|accounts
argument_list|()
operator|.
name|get
argument_list|(
name|user
operator|.
name|getAccountId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
literal|"account not found"
argument_list|)
throw|;
block|}
if|if
condition|(
name|email
operator|.
name|equals
argument_list|(
name|a
operator|.
name|getPreferredEmail
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|Response
operator|.
name|ok
argument_list|(
literal|""
argument_list|)
return|;
block|}
name|a
operator|.
name|setPreferredEmail
argument_list|(
name|email
argument_list|)
expr_stmt|;
name|dbProvider
operator|.
name|get
argument_list|()
operator|.
name|accounts
argument_list|()
operator|.
name|update
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
name|byIdCache
operator|.
name|evict
argument_list|(
name|a
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Response
operator|.
name|created
argument_list|(
literal|""
argument_list|)
return|;
block|}
block|}
end_class

end_unit

