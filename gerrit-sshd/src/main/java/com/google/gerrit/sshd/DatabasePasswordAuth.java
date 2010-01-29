begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
DECL|package|com.google.gerrit.sshd
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|sshd
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
name|reviewdb
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
name|server
operator|.
name|AccessPath
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
name|AccountCache
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
name|sshd
operator|.
name|SshScope
operator|.
name|Context
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
name|org
operator|.
name|apache
operator|.
name|mina
operator|.
name|core
operator|.
name|future
operator|.
name|IoFuture
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|mina
operator|.
name|core
operator|.
name|future
operator|.
name|IoFutureListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|server
operator|.
name|PasswordAuthenticator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|sshd
operator|.
name|server
operator|.
name|session
operator|.
name|ServerSession
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketAddress
import|;
end_import

begin_comment
comment|/**  * Authenticates by password through {@link AccountExternalId} entities.  */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|DatabasePasswordAuth
class|class
name|DatabasePasswordAuth
implements|implements
name|PasswordAuthenticator
block|{
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
DECL|field|log
specifier|private
specifier|final
name|SshLog
name|log
decl_stmt|;
DECL|field|userFactory
specifier|private
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|userFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|DatabasePasswordAuth (final AccountCache ac, final SshLog l, final IdentifiedUser.GenericFactory uf)
name|DatabasePasswordAuth
parameter_list|(
specifier|final
name|AccountCache
name|ac
parameter_list|,
specifier|final
name|SshLog
name|l
parameter_list|,
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|uf
parameter_list|)
block|{
name|accountCache
operator|=
name|ac
expr_stmt|;
name|log
operator|=
name|l
expr_stmt|;
name|userFactory
operator|=
name|uf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|authenticate (final String username, final String password, final ServerSession session)
specifier|public
name|boolean
name|authenticate
parameter_list|(
specifier|final
name|String
name|username
parameter_list|,
specifier|final
name|String
name|password
parameter_list|,
specifier|final
name|ServerSession
name|session
parameter_list|)
block|{
specifier|final
name|SshSession
name|sd
init|=
name|session
operator|.
name|getAttribute
argument_list|(
name|SshSession
operator|.
name|KEY
argument_list|)
decl_stmt|;
name|AccountState
name|state
init|=
name|accountCache
operator|.
name|getByUsername
argument_list|(
name|username
argument_list|)
decl_stmt|;
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
name|sd
operator|.
name|authenticationError
argument_list|(
name|username
argument_list|,
literal|"user-not-found"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
specifier|final
name|String
name|p
init|=
name|state
operator|.
name|getPassword
argument_list|(
name|username
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
name|sd
operator|.
name|authenticationError
argument_list|(
name|username
argument_list|,
literal|"no-password"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|equals
argument_list|(
name|password
argument_list|)
condition|)
block|{
name|sd
operator|.
name|authenticationError
argument_list|(
name|username
argument_list|,
literal|"incorrect-password"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|sd
operator|.
name|getCurrentUser
argument_list|()
operator|==
literal|null
condition|)
block|{
name|sd
operator|.
name|authenticationSuccess
argument_list|(
name|username
argument_list|,
name|createUser
argument_list|(
name|sd
argument_list|,
name|state
argument_list|)
argument_list|)
expr_stmt|;
comment|// If this is the first time we've authenticated this
comment|// session, record a login event in the log and add
comment|// a close listener to record a logout event.
comment|//
name|Context
name|ctx
init|=
operator|new
name|Context
argument_list|(
name|sd
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Context
name|old
init|=
name|SshScope
operator|.
name|set
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
try|try
block|{
name|log
operator|.
name|onLogin
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|SshScope
operator|.
name|set
argument_list|(
name|old
argument_list|)
expr_stmt|;
block|}
name|session
operator|.
name|getIoSession
argument_list|()
operator|.
name|getCloseFuture
argument_list|()
operator|.
name|addListener
argument_list|(
operator|new
name|IoFutureListener
argument_list|<
name|IoFuture
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|operationComplete
parameter_list|(
name|IoFuture
name|future
parameter_list|)
block|{
specifier|final
name|Context
name|ctx
init|=
operator|new
name|Context
argument_list|(
name|sd
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|Context
name|old
init|=
name|SshScope
operator|.
name|set
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
try|try
block|{
name|log
operator|.
name|onLogout
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|SshScope
operator|.
name|set
argument_list|(
name|old
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|createUser (final SshSession sd, final AccountState state)
specifier|private
name|IdentifiedUser
name|createUser
parameter_list|(
specifier|final
name|SshSession
name|sd
parameter_list|,
specifier|final
name|AccountState
name|state
parameter_list|)
block|{
return|return
name|userFactory
operator|.
name|create
argument_list|(
name|AccessPath
operator|.
name|SSH_COMMAND
argument_list|,
operator|new
name|Provider
argument_list|<
name|SocketAddress
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|SocketAddress
name|get
parameter_list|()
block|{
return|return
name|sd
operator|.
name|getRemoteAddress
argument_list|()
return|;
block|}
block|}
argument_list|,
name|state
operator|.
name|getAccount
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

