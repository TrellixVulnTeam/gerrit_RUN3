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
name|extensions
operator|.
name|common
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
name|AccountResource
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
name|AccountsUpdate
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
name|permissions
operator|.
name|GlobalPermission
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
name|permissions
operator|.
name|PermissionBackend
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
name|permissions
operator|.
name|PermissionBackendException
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
DECL|field|self
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
decl_stmt|;
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|accountsUpdate
specifier|private
specifier|final
name|AccountsUpdate
operator|.
name|Server
name|accountsUpdate
decl_stmt|;
annotation|@
name|Inject
DECL|method|PutPreferred ( Provider<CurrentUser> self, PermissionBackend permissionBackend, AccountsUpdate.Server accountsUpdate)
name|PutPreferred
parameter_list|(
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|self
parameter_list|,
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|AccountsUpdate
operator|.
name|Server
name|accountsUpdate
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
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
name|this
operator|.
name|accountsUpdate
operator|=
name|accountsUpdate
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
throws|,
name|IOException
throws|,
name|PermissionBackendException
throws|,
name|ConfigInvalidException
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
condition|)
block|{
name|permissionBackend
operator|.
name|user
argument_list|(
name|self
argument_list|)
operator|.
name|check
argument_list|(
name|GlobalPermission
operator|.
name|MODIFY_ACCOUNT
argument_list|)
expr_stmt|;
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
name|IOException
throws|,
name|ConfigInvalidException
throws|,
name|OrmException
block|{
name|AtomicBoolean
name|alreadyPreferred
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|AccountState
name|accountState
init|=
name|accountsUpdate
operator|.
name|create
argument_list|()
operator|.
name|update
argument_list|(
literal|"Set Preferred Email via API"
argument_list|,
name|user
operator|.
name|getAccountId
argument_list|()
argument_list|,
parameter_list|(
name|a
parameter_list|,
name|u
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|email
operator|.
name|equals
argument_list|(
name|a
operator|.
name|getAccount
argument_list|()
operator|.
name|getPreferredEmail
argument_list|()
argument_list|)
condition|)
block|{
name|alreadyPreferred
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|u
operator|.
name|setPreferredEmail
argument_list|(
name|email
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|accountState
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
return|return
name|alreadyPreferred
operator|.
name|get
argument_list|()
condition|?
name|Response
operator|.
name|ok
argument_list|(
literal|""
argument_list|)
else|:
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

