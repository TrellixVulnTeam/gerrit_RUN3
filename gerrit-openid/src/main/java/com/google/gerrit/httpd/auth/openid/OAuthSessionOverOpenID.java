begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
DECL|package|com.google.gerrit.httpd.auth.openid
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
name|openid
package|;
end_package

begin_import
import|import static
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
operator|.
name|SC_UNAUTHORIZED
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
name|base
operator|.
name|Strings
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
name|auth
operator|.
name|oauth
operator|.
name|OAuthServiceProvider
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
name|auth
operator|.
name|oauth
operator|.
name|OAuthToken
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
name|auth
operator|.
name|oauth
operator|.
name|OAuthUserInfo
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
name|auth
operator|.
name|oauth
operator|.
name|OAuthVerifier
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
name|DynamicItem
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
name|Url
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
name|CanonicalWebUrl
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
name|LoginUrlToken
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
name|AuthResult
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
name|servlet
operator|.
name|SessionScoped
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|codec
operator|.
name|binary
operator|.
name|Base64
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|SecureRandom
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_comment
comment|/** OAuth protocol implementation */
end_comment

begin_class
annotation|@
name|SessionScoped
DECL|class|OAuthSessionOverOpenID
class|class
name|OAuthSessionOverOpenID
block|{
DECL|field|GERRIT_LOGIN
specifier|static
specifier|final
name|String
name|GERRIT_LOGIN
init|=
literal|"/login"
decl_stmt|;
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|OAuthSessionOverOpenID
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|randomState
specifier|private
specifier|static
specifier|final
name|SecureRandom
name|randomState
init|=
name|newRandomGenerator
argument_list|()
decl_stmt|;
DECL|field|state
specifier|private
specifier|final
name|String
name|state
decl_stmt|;
DECL|field|webSession
specifier|private
specifier|final
name|DynamicItem
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
DECL|field|urlProvider
specifier|private
specifier|final
name|CanonicalWebUrl
name|urlProvider
decl_stmt|;
DECL|field|serviceProvider
specifier|private
name|OAuthServiceProvider
name|serviceProvider
decl_stmt|;
DECL|field|token
specifier|private
name|OAuthToken
name|token
decl_stmt|;
DECL|field|user
specifier|private
name|OAuthUserInfo
name|user
decl_stmt|;
DECL|field|redirectToken
specifier|private
name|String
name|redirectToken
decl_stmt|;
annotation|@
name|Inject
DECL|method|OAuthSessionOverOpenID (DynamicItem<WebSession> webSession, AccountManager accountManager, CanonicalWebUrl urlProvider)
name|OAuthSessionOverOpenID
parameter_list|(
name|DynamicItem
argument_list|<
name|WebSession
argument_list|>
name|webSession
parameter_list|,
name|AccountManager
name|accountManager
parameter_list|,
name|CanonicalWebUrl
name|urlProvider
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|generateRandomState
argument_list|()
expr_stmt|;
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
name|urlProvider
operator|=
name|urlProvider
expr_stmt|;
block|}
DECL|method|isLoggedIn ()
name|boolean
name|isLoggedIn
parameter_list|()
block|{
return|return
name|token
operator|!=
literal|null
operator|&&
name|user
operator|!=
literal|null
return|;
block|}
DECL|method|isOAuthFinal (HttpServletRequest request)
name|boolean
name|isOAuthFinal
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
return|return
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|request
operator|.
name|getParameter
argument_list|(
literal|"code"
argument_list|)
argument_list|)
operator|!=
literal|null
return|;
block|}
DECL|method|login (HttpServletRequest request, HttpServletResponse response, OAuthServiceProvider oauth)
name|boolean
name|login
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|,
name|OAuthServiceProvider
name|oauth
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isLoggedIn
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"Login "
operator|+
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|isOAuthFinal
argument_list|(
name|request
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|checkState
argument_list|(
name|request
argument_list|)
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"Login-Retrieve-User "
operator|+
name|this
argument_list|)
expr_stmt|;
name|token
operator|=
name|oauth
operator|.
name|getAccessToken
argument_list|(
operator|new
name|OAuthVerifier
argument_list|(
name|request
operator|.
name|getParameter
argument_list|(
literal|"code"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|user
operator|=
name|oauth
operator|.
name|getUserInfo
argument_list|(
name|token
argument_list|)
expr_stmt|;
if|if
condition|(
name|isLoggedIn
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Login-SUCCESS "
operator|+
name|this
argument_list|)
expr_stmt|;
name|authenticateAndRedirect
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|response
operator|.
name|sendError
argument_list|(
name|SC_UNAUTHORIZED
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Login-PHASE1 "
operator|+
name|this
argument_list|)
expr_stmt|;
name|redirectToken
operator|=
name|LoginUrlToken
operator|.
name|getToken
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|response
operator|.
name|sendRedirect
argument_list|(
name|oauth
operator|.
name|getAuthorizationUrl
argument_list|()
operator|+
literal|"&state="
operator|+
name|state
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
DECL|method|authenticateAndRedirect (HttpServletRequest req, HttpServletResponse rsp)
specifier|private
name|void
name|authenticateAndRedirect
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|rsp
parameter_list|)
throws|throws
name|IOException
block|{
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
name|areq
init|=
operator|new
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
argument_list|(
name|user
operator|.
name|getExternalId
argument_list|()
argument_list|)
decl_stmt|;
name|AuthResult
name|arsp
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|claimedIdentifier
init|=
name|user
operator|.
name|getClaimedIdentity
argument_list|()
decl_stmt|;
name|Account
operator|.
name|Id
name|actualId
init|=
name|accountManager
operator|.
name|lookup
argument_list|(
name|user
operator|.
name|getExternalId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|claimedIdentifier
argument_list|)
condition|)
block|{
name|Account
operator|.
name|Id
name|claimedId
init|=
name|accountManager
operator|.
name|lookup
argument_list|(
name|claimedIdentifier
argument_list|)
decl_stmt|;
if|if
condition|(
name|claimedId
operator|!=
literal|null
operator|&&
name|actualId
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|claimedId
operator|.
name|equals
argument_list|(
name|actualId
argument_list|)
condition|)
block|{
comment|// Both link to the same account, that's what we expected.
block|}
else|else
block|{
comment|// This is (for now) a fatal error. There are two records
comment|// for what might be the same user.
comment|//
name|log
operator|.
name|error
argument_list|(
literal|"OAuth accounts disagree over user identity:\n"
operator|+
literal|"  Claimed ID: "
operator|+
name|claimedId
operator|+
literal|" is "
operator|+
name|claimedIdentifier
operator|+
literal|"\n"
operator|+
literal|"  Delgate ID: "
operator|+
name|actualId
operator|+
literal|" is "
operator|+
name|user
operator|.
name|getExternalId
argument_list|()
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
elseif|else
if|if
condition|(
name|claimedId
operator|!=
literal|null
operator|&&
name|actualId
operator|==
literal|null
condition|)
block|{
comment|// Claimed account already exists: link to it.
comment|//
try|try
block|{
name|accountManager
operator|.
name|link
argument_list|(
name|claimedId
argument_list|,
name|areq
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot link: "
operator|+
name|user
operator|.
name|getExternalId
argument_list|()
operator|+
literal|" to user identity:\n"
operator|+
literal|"  Claimed ID: "
operator|+
name|claimedId
operator|+
literal|" is "
operator|+
name|claimedIdentifier
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
name|areq
operator|.
name|setUserName
argument_list|(
name|user
operator|.
name|getUserName
argument_list|()
argument_list|)
expr_stmt|;
name|areq
operator|.
name|setEmailAddress
argument_list|(
name|user
operator|.
name|getEmailAddress
argument_list|()
argument_list|)
expr_stmt|;
name|areq
operator|.
name|setDisplayName
argument_list|(
name|user
operator|.
name|getDisplayName
argument_list|()
argument_list|)
expr_stmt|;
name|arsp
operator|=
name|accountManager
operator|.
name|authenticate
argument_list|(
name|areq
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccountException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Unable to authenticate user \""
operator|+
name|user
operator|+
literal|"\""
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|)
expr_stmt|;
return|return;
block|}
name|webSession
operator|.
name|get
argument_list|()
operator|.
name|login
argument_list|(
name|arsp
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|StringBuilder
name|rdr
init|=
operator|new
name|StringBuilder
argument_list|(
name|urlProvider
operator|.
name|get
argument_list|(
name|req
argument_list|)
argument_list|)
decl_stmt|;
name|rdr
operator|.
name|append
argument_list|(
name|Url
operator|.
name|decode
argument_list|(
name|redirectToken
argument_list|)
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|sendRedirect
argument_list|(
name|rdr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|logout ()
name|void
name|logout
parameter_list|()
block|{
name|token
operator|=
literal|null
expr_stmt|;
name|user
operator|=
literal|null
expr_stmt|;
name|redirectToken
operator|=
literal|null
expr_stmt|;
name|serviceProvider
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|checkState (ServletRequest request)
specifier|private
name|boolean
name|checkState
parameter_list|(
name|ServletRequest
name|request
parameter_list|)
block|{
name|String
name|s
init|=
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|request
operator|.
name|getParameter
argument_list|(
literal|"state"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|s
operator|.
name|equals
argument_list|(
name|state
argument_list|)
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Illegal request state '"
operator|+
name|s
operator|+
literal|"' on OAuthProtocol "
operator|+
name|this
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|newRandomGenerator ()
specifier|private
specifier|static
name|SecureRandom
name|newRandomGenerator
parameter_list|()
block|{
try|try
block|{
return|return
name|SecureRandom
operator|.
name|getInstance
argument_list|(
literal|"SHA1PRNG"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No SecureRandom available for GitHub authentication"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|generateRandomState ()
specifier|private
specifier|static
name|String
name|generateRandomState
parameter_list|()
block|{
name|byte
index|[]
name|state
init|=
operator|new
name|byte
index|[
literal|32
index|]
decl_stmt|;
name|randomState
operator|.
name|nextBytes
argument_list|(
name|state
argument_list|)
expr_stmt|;
return|return
name|Base64
operator|.
name|encodeBase64URLSafeString
argument_list|(
name|state
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"OAuthSession [token="
operator|+
name|token
operator|+
literal|", user="
operator|+
name|user
operator|+
literal|"]"
return|;
block|}
DECL|method|setServiceProvider (OAuthServiceProvider provider)
specifier|public
name|void
name|setServiceProvider
parameter_list|(
name|OAuthServiceProvider
name|provider
parameter_list|)
block|{
name|this
operator|.
name|serviceProvider
operator|=
name|provider
expr_stmt|;
block|}
DECL|method|getServiceProvider ()
specifier|public
name|OAuthServiceProvider
name|getServiceProvider
parameter_list|()
block|{
return|return
name|serviceProvider
return|;
block|}
block|}
end_class

end_unit

