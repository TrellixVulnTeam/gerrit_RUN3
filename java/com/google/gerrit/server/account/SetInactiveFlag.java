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
name|ResourceConflictException
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
DECL|class|SetInactiveFlag
specifier|public
class|class
name|SetInactiveFlag
block|{
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
DECL|method|SetInactiveFlag (AccountsUpdate.Server accountsUpdate)
name|SetInactiveFlag
parameter_list|(
name|AccountsUpdate
operator|.
name|Server
name|accountsUpdate
parameter_list|)
block|{
name|this
operator|.
name|accountsUpdate
operator|=
name|accountsUpdate
expr_stmt|;
block|}
DECL|method|deactivate (Account.Id accountId)
specifier|public
name|Response
argument_list|<
name|?
argument_list|>
name|deactivate
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
throws|,
name|OrmException
block|{
name|AtomicBoolean
name|alreadyInactive
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Account
name|account
init|=
name|accountsUpdate
operator|.
name|create
argument_list|()
operator|.
name|update
argument_list|(
literal|"Deactivate Account via API"
argument_list|,
name|accountId
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
operator|!
name|a
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|alreadyInactive
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
name|setActive
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|account
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
name|alreadyInactive
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"account not active"
argument_list|)
throw|;
block|}
return|return
name|Response
operator|.
name|none
argument_list|()
return|;
block|}
DECL|method|activate (Account.Id accountId)
specifier|public
name|Response
argument_list|<
name|String
argument_list|>
name|activate
parameter_list|(
name|Account
operator|.
name|Id
name|accountId
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
name|alreadyActive
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Account
name|account
init|=
name|accountsUpdate
operator|.
name|create
argument_list|()
operator|.
name|update
argument_list|(
literal|"Activate Account via API"
argument_list|,
name|accountId
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
name|a
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|alreadyActive
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
name|setActive
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|account
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
name|alreadyActive
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

