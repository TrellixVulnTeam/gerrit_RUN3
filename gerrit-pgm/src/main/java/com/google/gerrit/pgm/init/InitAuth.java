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
DECL|package|com.google.gerrit.pgm.init
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|init
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
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|InitUtil
operator|.
name|dnOf
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
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|ConsoleUI
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
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|InitFlags
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
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|InitStep
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
name|pgm
operator|.
name|init
operator|.
name|api
operator|.
name|Section
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtjsonrpc
operator|.
name|server
operator|.
name|SignedToken
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

begin_comment
comment|/** Initialize the {@code auth} configuration section. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|InitAuth
class|class
name|InitAuth
implements|implements
name|InitStep
block|{
DECL|field|RECEIVE
specifier|private
specifier|static
specifier|final
name|String
name|RECEIVE
init|=
literal|"receive"
decl_stmt|;
DECL|field|ENABLE_SIGNED_PUSH
specifier|private
specifier|static
specifier|final
name|String
name|ENABLE_SIGNED_PUSH
init|=
literal|"enableSignedPush"
decl_stmt|;
DECL|field|ui
specifier|private
specifier|final
name|ConsoleUI
name|ui
decl_stmt|;
DECL|field|auth
specifier|private
specifier|final
name|Section
name|auth
decl_stmt|;
DECL|field|ldap
specifier|private
specifier|final
name|Section
name|ldap
decl_stmt|;
DECL|field|receive
specifier|private
specifier|final
name|Section
name|receive
decl_stmt|;
DECL|field|flags
specifier|private
specifier|final
name|InitFlags
name|flags
decl_stmt|;
annotation|@
name|Inject
DECL|method|InitAuth (InitFlags flags, ConsoleUI ui, Section.Factory sections)
name|InitAuth
parameter_list|(
name|InitFlags
name|flags
parameter_list|,
name|ConsoleUI
name|ui
parameter_list|,
name|Section
operator|.
name|Factory
name|sections
parameter_list|)
block|{
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
name|this
operator|.
name|ui
operator|=
name|ui
expr_stmt|;
name|this
operator|.
name|auth
operator|=
name|sections
operator|.
name|get
argument_list|(
literal|"auth"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|ldap
operator|=
name|sections
operator|.
name|get
argument_list|(
literal|"ldap"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|receive
operator|=
name|sections
operator|.
name|get
argument_list|(
name|RECEIVE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|ui
operator|.
name|header
argument_list|(
literal|"User Authentication"
argument_list|)
expr_stmt|;
name|initAuthType
argument_list|()
expr_stmt|;
if|if
condition|(
name|auth
operator|.
name|getSecure
argument_list|(
literal|"registerEmailPrivateKey"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|auth
operator|.
name|setSecure
argument_list|(
literal|"registerEmailPrivateKey"
argument_list|,
name|SignedToken
operator|.
name|generateRandomKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|initSignedPush
argument_list|()
expr_stmt|;
block|}
DECL|method|initAuthType ()
specifier|private
name|void
name|initAuthType
parameter_list|()
block|{
name|AuthType
name|authType
init|=
name|auth
operator|.
name|select
argument_list|(
literal|"Authentication method"
argument_list|,
literal|"type"
argument_list|,
name|flags
operator|.
name|dev
condition|?
name|AuthType
operator|.
name|DEVELOPMENT_BECOME_ANY_ACCOUNT
else|:
name|AuthType
operator|.
name|OPENID
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|authType
condition|)
block|{
case|case
name|HTTP
case|:
case|case
name|HTTP_LDAP
case|:
block|{
name|String
name|hdr
init|=
name|auth
operator|.
name|get
argument_list|(
literal|"httpHeader"
argument_list|)
decl_stmt|;
if|if
condition|(
name|ui
operator|.
name|yesno
argument_list|(
name|hdr
operator|!=
literal|null
argument_list|,
literal|"Get username from custom HTTP header"
argument_list|)
condition|)
block|{
name|auth
operator|.
name|string
argument_list|(
literal|"Username HTTP header"
argument_list|,
literal|"httpHeader"
argument_list|,
literal|"SM_USER"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|hdr
operator|!=
literal|null
condition|)
block|{
name|auth
operator|.
name|unset
argument_list|(
literal|"httpHeader"
argument_list|)
expr_stmt|;
block|}
name|auth
operator|.
name|string
argument_list|(
literal|"SSO logout URL"
argument_list|,
literal|"logoutUrl"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|CLIENT_SSL_CERT_LDAP
case|:
case|case
name|CUSTOM_EXTENSION
case|:
case|case
name|DEVELOPMENT_BECOME_ANY_ACCOUNT
case|:
case|case
name|LDAP
case|:
case|case
name|LDAP_BIND
case|:
case|case
name|OAUTH
case|:
case|case
name|OPENID
case|:
case|case
name|OPENID_SSO
case|:
break|break;
block|}
switch|switch
condition|(
name|authType
condition|)
block|{
case|case
name|LDAP
case|:
case|case
name|LDAP_BIND
case|:
case|case
name|HTTP_LDAP
case|:
block|{
name|String
name|server
init|=
name|ldap
operator|.
name|string
argument_list|(
literal|"LDAP server"
argument_list|,
literal|"server"
argument_list|,
literal|"ldap://localhost"
argument_list|)
decl_stmt|;
if|if
condition|(
name|server
operator|!=
literal|null
comment|//
operator|&&
operator|!
name|server
operator|.
name|startsWith
argument_list|(
literal|"ldap://"
argument_list|)
comment|//
operator|&&
operator|!
name|server
operator|.
name|startsWith
argument_list|(
literal|"ldaps://"
argument_list|)
condition|)
block|{
if|if
condition|(
name|ui
operator|.
name|yesno
argument_list|(
literal|false
argument_list|,
literal|"Use SSL"
argument_list|)
condition|)
block|{
name|server
operator|=
literal|"ldaps://"
operator|+
name|server
expr_stmt|;
block|}
else|else
block|{
name|server
operator|=
literal|"ldap://"
operator|+
name|server
expr_stmt|;
block|}
name|ldap
operator|.
name|set
argument_list|(
literal|"server"
argument_list|,
name|server
argument_list|)
expr_stmt|;
block|}
name|ldap
operator|.
name|string
argument_list|(
literal|"LDAP username"
argument_list|,
literal|"username"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ldap
operator|.
name|password
argument_list|(
literal|"username"
argument_list|,
literal|"password"
argument_list|)
expr_stmt|;
name|String
name|aBase
init|=
name|ldap
operator|.
name|string
argument_list|(
literal|"Account BaseDN"
argument_list|,
literal|"accountBase"
argument_list|,
name|dnOf
argument_list|(
name|server
argument_list|)
argument_list|)
decl_stmt|;
name|ldap
operator|.
name|string
argument_list|(
literal|"Group BaseDN"
argument_list|,
literal|"groupBase"
argument_list|,
name|aBase
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|CLIENT_SSL_CERT_LDAP
case|:
case|case
name|CUSTOM_EXTENSION
case|:
case|case
name|DEVELOPMENT_BECOME_ANY_ACCOUNT
case|:
case|case
name|HTTP
case|:
case|case
name|OAUTH
case|:
case|case
name|OPENID
case|:
case|case
name|OPENID_SSO
case|:
break|break;
block|}
block|}
DECL|method|initSignedPush ()
specifier|private
name|void
name|initSignedPush
parameter_list|()
block|{
name|boolean
name|def
init|=
name|flags
operator|.
name|cfg
operator|.
name|getBoolean
argument_list|(
name|RECEIVE
argument_list|,
name|ENABLE_SIGNED_PUSH
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|boolean
name|enable
init|=
name|ui
operator|.
name|yesno
argument_list|(
name|def
argument_list|,
literal|"Enable signed push support"
argument_list|)
decl_stmt|;
name|receive
operator|.
name|set
argument_list|(
literal|"enableSignedPush"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|enable
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

