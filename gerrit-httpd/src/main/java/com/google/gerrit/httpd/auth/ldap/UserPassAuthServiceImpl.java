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
DECL|package|com.google.gerrit.httpd.auth.ldap
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
operator|.
name|auth
operator|.
name|ldap
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
name|auth
operator|.
name|userpass
operator|.
name|LoginResult
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
name|auth
operator|.
name|userpass
operator|.
name|UserPassAuthService
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
name|httpd
operator|.
name|WebSession
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
name|AuthType
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
name|AccountException
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
name|AccountManager
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
name|AccountUserNameException
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
name|AuthRequest
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
name|AuthResult
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
name|auth
operator|.
name|AuthenticationUnavailableException
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
name|config
operator|.
name|AuthConfig
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|rpc
operator|.
name|AsyncCallback
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

begin_class
DECL|class|UserPassAuthServiceImpl
class|class
name|UserPassAuthServiceImpl
implements|implements
name|UserPassAuthService
block|{
DECL|field|webSession
specifier|private
specifier|final
name|Provider
argument_list|<
name|WebSession
argument_list|>
name|webSession
decl_stmt|;
DECL|field|accountManager
specifier|private
specifier|final
name|AccountManager
name|accountManager
decl_stmt|;
DECL|field|authType
specifier|private
specifier|final
name|AuthType
name|authType
decl_stmt|;
annotation|@
name|Inject
DECL|method|UserPassAuthServiceImpl (final Provider<WebSession> webSession, final AccountManager accountManager, final AuthConfig authConfig)
name|UserPassAuthServiceImpl
parameter_list|(
specifier|final
name|Provider
argument_list|<
name|WebSession
argument_list|>
name|webSession
parameter_list|,
specifier|final
name|AccountManager
name|accountManager
parameter_list|,
specifier|final
name|AuthConfig
name|authConfig
parameter_list|)
block|{
name|this
operator|.
name|webSession
operator|=
name|webSession
expr_stmt|;
name|this
operator|.
name|accountManager
operator|=
name|accountManager
expr_stmt|;
name|this
operator|.
name|authType
operator|=
name|authConfig
operator|.
name|getAuthType
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|authenticate (String username, final String password, final AsyncCallback<LoginResult> callback)
specifier|public
name|void
name|authenticate
parameter_list|(
name|String
name|username
parameter_list|,
specifier|final
name|String
name|password
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|LoginResult
argument_list|>
name|callback
parameter_list|)
block|{
name|LoginResult
name|result
init|=
operator|new
name|LoginResult
argument_list|(
name|authType
argument_list|)
decl_stmt|;
if|if
condition|(
name|username
operator|==
literal|null
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|username
operator|.
name|trim
argument_list|()
argument_list|)
comment|//
operator|||
name|password
operator|==
literal|null
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|password
argument_list|)
condition|)
block|{
name|result
operator|.
name|setError
argument_list|(
name|LoginResult
operator|.
name|Error
operator|.
name|INVALID_LOGIN
argument_list|)
expr_stmt|;
name|callback
operator|.
name|onSuccess
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return;
block|}
name|username
operator|=
name|username
operator|.
name|trim
argument_list|()
expr_stmt|;
specifier|final
name|AuthRequest
name|req
init|=
name|AuthRequest
operator|.
name|forUser
argument_list|(
name|username
argument_list|)
decl_stmt|;
name|req
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
specifier|final
name|AuthResult
name|res
decl_stmt|;
try|try
block|{
name|res
operator|=
name|accountManager
operator|.
name|authenticate
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccountUserNameException
name|e
parameter_list|)
block|{
comment|// entered user name and password were correct, but user name could not be
comment|// set for the newly created account and this is why the login fails,
comment|// error screen with error message should be shown to the user
name|callback
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|AuthenticationUnavailableException
name|e
parameter_list|)
block|{
name|result
operator|.
name|setError
argument_list|(
name|LoginResult
operator|.
name|Error
operator|.
name|AUTHENTICATION_UNAVAILABLE
argument_list|)
expr_stmt|;
name|callback
operator|.
name|onSuccess
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|AccountException
name|e
parameter_list|)
block|{
name|result
operator|.
name|setError
argument_list|(
name|LoginResult
operator|.
name|Error
operator|.
name|INVALID_LOGIN
argument_list|)
expr_stmt|;
name|callback
operator|.
name|onSuccess
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return;
block|}
name|result
operator|.
name|success
operator|=
literal|true
expr_stmt|;
name|result
operator|.
name|isNew
operator|=
name|res
operator|.
name|isNew
argument_list|()
expr_stmt|;
name|webSession
operator|.
name|get
argument_list|()
operator|.
name|login
argument_list|(
name|res
argument_list|,
literal|true
comment|/* persistent cookie */
argument_list|)
expr_stmt|;
name|callback
operator|.
name|onSuccess
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

