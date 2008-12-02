begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright 2008 Google Inc.
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
DECL|package|com.google.gerrit.server
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
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
name|client
operator|.
name|Gerrit
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
name|client
operator|.
name|account
operator|.
name|SignInResult
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
name|client
operator|.
name|reviewdb
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
name|client
operator|.
name|reviewdb
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
name|gwt
operator|.
name|user
operator|.
name|server
operator|.
name|rpc
operator|.
name|RPCServletUtils
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
name|JsonServlet
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
name|ValidToken
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
name|XsrfException
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
name|client
operator|.
name|OrmException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|dyuproject
operator|.
name|openid
operator|.
name|Constants
import|;
end_import

begin_import
import|import
name|com
operator|.
name|dyuproject
operator|.
name|openid
operator|.
name|OpenIdContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|dyuproject
operator|.
name|openid
operator|.
name|OpenIdUser
import|;
end_import

begin_import
import|import
name|com
operator|.
name|dyuproject
operator|.
name|openid
operator|.
name|RelyingParty
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|util
operator|.
name|UrlEncoded
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|GZIPOutputStream
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletConfig
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
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
name|Cookie
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
name|HttpServlet
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
comment|/** Handles the<code>/login</code> URL for web based single-sign-on. */
end_comment

begin_class
DECL|class|LoginServlet
specifier|public
class|class
name|LoginServlet
extends|extends
name|HttpServlet
block|{
DECL|field|CALLBACK_PARMETER
specifier|private
specifier|static
specifier|final
name|String
name|CALLBACK_PARMETER
init|=
literal|"callback"
decl_stmt|;
DECL|field|AX_SCHEMA
specifier|private
specifier|static
specifier|final
name|String
name|AX_SCHEMA
init|=
literal|"http://openid.net/srv/ax/1.0"
decl_stmt|;
DECL|field|GMODE_CHKCOOKIE
specifier|private
specifier|static
specifier|final
name|String
name|GMODE_CHKCOOKIE
init|=
literal|"gerrit_chkcookie"
decl_stmt|;
DECL|field|GMODE_SETCOOKIE
specifier|private
specifier|static
specifier|final
name|String
name|GMODE_SETCOOKIE
init|=
literal|"gerrit_setcookie"
decl_stmt|;
DECL|field|server
specifier|private
name|GerritServer
name|server
decl_stmt|;
DECL|field|relyingParty
specifier|private
name|RelyingParty
name|relyingParty
decl_stmt|;
DECL|field|pleaseSetCookieDoc
specifier|private
name|Document
name|pleaseSetCookieDoc
decl_stmt|;
annotation|@
name|Override
DECL|method|init (final ServletConfig config)
specifier|public
name|void
name|init
parameter_list|(
specifier|final
name|ServletConfig
name|config
parameter_list|)
throws|throws
name|ServletException
block|{
name|super
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
try|try
block|{
name|server
operator|=
name|GerritServer
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Cannot configure GerritServer"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|XsrfException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Cannot configure GerritServer"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|String
name|cookieKey
init|=
name|server
operator|.
name|getAccountCookieKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|cookieKey
operator|.
name|length
argument_list|()
operator|>
literal|24
condition|)
block|{
name|cookieKey
operator|=
name|cookieKey
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|24
argument_list|)
expr_stmt|;
block|}
try|try
block|{
specifier|final
name|int
name|sessionAge
init|=
name|server
operator|.
name|getSessionAge
argument_list|()
decl_stmt|;
specifier|final
name|Properties
name|p
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"openid.cookie.name"
argument_list|,
name|Gerrit
operator|.
name|OPENIDUSER_COOKIE
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"openid.cookie.security.secretKey"
argument_list|,
name|cookieKey
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"openid.cookie.maxAge"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|sessionAge
argument_list|)
argument_list|)
expr_stmt|;
name|relyingParty
operator|=
name|RelyingParty
operator|.
name|newInstance
argument_list|(
name|p
argument_list|)
expr_stmt|;
specifier|final
name|OpenIdContext
name|ctx
init|=
name|relyingParty
operator|.
name|getOpenIdContext
argument_list|()
decl_stmt|;
name|ctx
operator|.
name|setDiscovery
argument_list|(
operator|new
name|GoogleAccountDiscovery
argument_list|(
name|ctx
operator|.
name|getDiscovery
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Cannot setup RelyingParty"
argument_list|,
name|e
argument_list|)
throw|;
block|}
specifier|final
name|String
name|scHtmlName
init|=
literal|"com/google/gerrit/public/SetCookie.html"
decl_stmt|;
name|pleaseSetCookieDoc
operator|=
name|HtmlDomUtil
operator|.
name|parseFile
argument_list|(
name|scHtmlName
argument_list|)
expr_stmt|;
if|if
condition|(
name|pleaseSetCookieDoc
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"No "
operator|+
name|scHtmlName
operator|+
literal|" in CLASSPATH"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|doGet (final HttpServletRequest req, final HttpServletResponse rsp)
specifier|public
name|void
name|doGet
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
name|HttpServletResponse
name|rsp
parameter_list|)
throws|throws
name|IOException
block|{
name|doPost
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doPost (final HttpServletRequest req, final HttpServletResponse rsp)
specifier|public
name|void
name|doPost
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
name|HttpServletResponse
name|rsp
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|doAuth
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|getServletContext
argument_list|()
operator|.
name|log
argument_list|(
literal|"Unexpected error during authentication"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|callback
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|SignInResult
operator|.
name|CANCEL
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doAuth (final HttpServletRequest req, final HttpServletResponse rsp)
specifier|private
name|void
name|doAuth
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
name|HttpServletResponse
name|rsp
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|String
name|mode
init|=
name|req
operator|.
name|getParameter
argument_list|(
name|Constants
operator|.
name|OPENID_MODE
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"cancel"
operator|.
name|equals
argument_list|(
name|mode
argument_list|)
condition|)
block|{
comment|// Provider wants us to cancel the attempt.
comment|//
name|callback
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|SignInResult
operator|.
name|CANCEL
argument_list|)
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|GMODE_CHKCOOKIE
operator|.
name|equals
argument_list|(
name|mode
argument_list|)
condition|)
block|{
name|modeChkSetCookie
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|GMODE_SETCOOKIE
operator|.
name|equals
argument_list|(
name|mode
argument_list|)
condition|)
block|{
name|modeChkSetCookie
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|OpenIdUser
name|user
init|=
name|relyingParty
operator|.
name|discover
argument_list|(
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
comment|// User isn't known, no provider is known.
comment|//
name|redirectChooseProvider
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|user
operator|.
name|isAuthenticated
argument_list|()
condition|)
block|{
comment|// User already authenticated.
comment|//
name|initializeAccount
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|user
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|user
operator|.
name|isAssociated
argument_list|()
operator|&&
name|RelyingParty
operator|.
name|isAuthResponse
argument_list|(
name|req
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|relyingParty
operator|.
name|verifyAuth
argument_list|(
name|user
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
condition|)
block|{
comment|// Failed verification... re-authenticate.
comment|//
name|redirectChooseProvider
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Authentication was successful.
comment|//
name|String
name|email
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|nskey
init|=
literal|"openid.ns.ext"
operator|+
name|i
decl_stmt|;
specifier|final
name|String
name|nsval
init|=
name|req
operator|.
name|getParameter
argument_list|(
name|nskey
argument_list|)
decl_stmt|;
if|if
condition|(
name|nsval
operator|==
literal|null
condition|)
block|{
break|break;
block|}
specifier|final
name|String
name|ext
init|=
literal|"openid.ext"
operator|+
name|i
operator|+
literal|"."
decl_stmt|;
if|if
condition|(
name|AX_SCHEMA
operator|.
name|equals
argument_list|(
name|nsval
argument_list|)
operator|&&
literal|"fetch_response"
operator|.
name|equals
argument_list|(
name|req
operator|.
name|getParameter
argument_list|(
name|ext
operator|+
literal|"mode"
argument_list|)
argument_list|)
condition|)
block|{
name|email
operator|=
name|req
operator|.
name|getParameter
argument_list|(
name|ext
operator|+
literal|"value.email"
argument_list|)
expr_stmt|;
block|}
block|}
name|initializeAccount
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|user
argument_list|,
name|email
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|relyingParty
operator|.
name|associate
argument_list|(
name|user
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
condition|)
block|{
comment|// Failed association. Try again.
comment|//
name|redirectChooseProvider
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Authenticate user through his/her OpenID provider
comment|//
specifier|final
name|String
name|realm
init|=
name|serverUrl
argument_list|(
name|req
argument_list|)
decl_stmt|;
specifier|final
name|StringBuilder
name|retTo
init|=
operator|new
name|StringBuilder
argument_list|(
name|req
operator|.
name|getRequestURL
argument_list|()
argument_list|)
decl_stmt|;
name|append
argument_list|(
name|retTo
argument_list|,
name|CALLBACK_PARMETER
argument_list|,
name|req
operator|.
name|getParameter
argument_list|(
name|CALLBACK_PARMETER
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|StringBuilder
name|auth
decl_stmt|;
name|auth
operator|=
name|RelyingParty
operator|.
name|getAuthUrlBuffer
argument_list|(
name|user
argument_list|,
name|realm
argument_list|,
name|realm
argument_list|,
name|retTo
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|auth
argument_list|,
literal|"openid.ns.ext1"
argument_list|,
name|AX_SCHEMA
argument_list|)
expr_stmt|;
specifier|final
name|String
name|ext1
init|=
literal|"openid.ext1."
decl_stmt|;
name|append
argument_list|(
name|auth
argument_list|,
name|ext1
operator|+
literal|"mode"
argument_list|,
literal|"fetch_request"
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|auth
argument_list|,
name|ext1
operator|+
literal|"type.email"
argument_list|,
literal|"http://schema.openid.net/contact/email"
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|auth
argument_list|,
name|ext1
operator|+
literal|"required"
argument_list|,
literal|"email"
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|sendRedirect
argument_list|(
name|auth
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|redirectChooseProvider (final HttpServletRequest req, final HttpServletResponse rsp)
specifier|private
name|void
name|redirectChooseProvider
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
name|HttpServletResponse
name|rsp
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Hard-code to use the Google Account service.
comment|//
specifier|final
name|StringBuilder
name|url
init|=
operator|new
name|StringBuilder
argument_list|(
name|req
operator|.
name|getRequestURL
argument_list|()
argument_list|)
decl_stmt|;
name|append
argument_list|(
name|url
argument_list|,
name|CALLBACK_PARMETER
argument_list|,
name|req
operator|.
name|getParameter
argument_list|(
name|CALLBACK_PARMETER
argument_list|)
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|url
argument_list|,
name|RelyingParty
operator|.
name|DEFAULT_PARAMETER
argument_list|,
name|GoogleAccountDiscovery
operator|.
name|GOOGLE_ACCOUNT
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|sendRedirect
argument_list|(
name|url
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|initializeAccount (final HttpServletRequest req, final HttpServletResponse rsp, final OpenIdUser user, final String email)
specifier|private
name|void
name|initializeAccount
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
name|HttpServletResponse
name|rsp
parameter_list|,
specifier|final
name|OpenIdUser
name|user
parameter_list|,
specifier|final
name|String
name|email
parameter_list|)
throws|throws
name|IOException
block|{
name|Account
name|account
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Account
operator|.
name|OpenId
name|provId
init|=
operator|new
name|Account
operator|.
name|OpenId
argument_list|(
name|user
operator|.
name|getIdentity
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|ReviewDb
name|d
init|=
name|server
operator|.
name|getDatabase
argument_list|()
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
name|account
operator|=
name|d
operator|.
name|accounts
argument_list|()
operator|.
name|byOpenId
argument_list|(
name|provId
argument_list|)
expr_stmt|;
if|if
condition|(
name|account
operator|!=
literal|null
condition|)
block|{
comment|// Existing user; double check the email is current.
comment|//
if|if
condition|(
name|email
operator|!=
literal|null
operator|&&
operator|!
name|email
operator|.
name|equals
argument_list|(
name|account
operator|.
name|getPreferredEmail
argument_list|()
argument_list|)
condition|)
block|{
name|account
operator|.
name|setPreferredEmail
argument_list|(
name|email
argument_list|)
expr_stmt|;
name|d
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
name|account
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// New user; create an account entity for them.
comment|//
name|account
operator|=
operator|new
name|Account
argument_list|(
name|provId
argument_list|,
operator|new
name|Account
operator|.
name|Id
argument_list|(
name|d
operator|.
name|nextAccountId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|account
operator|.
name|setPreferredEmail
argument_list|(
name|email
argument_list|)
expr_stmt|;
name|d
operator|.
name|accounts
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|account
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|getServletContext
argument_list|()
operator|.
name|log
argument_list|(
literal|"Account lookup failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|account
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|rsp
operator|.
name|reset
argument_list|()
expr_stmt|;
name|Cookie
name|c
decl_stmt|;
name|c
operator|=
operator|new
name|Cookie
argument_list|(
name|Gerrit
operator|.
name|OPENIDUSER_COOKIE
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|c
operator|.
name|setMaxAge
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|addCookie
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|String
name|tok
decl_stmt|;
try|try
block|{
specifier|final
name|String
name|idstr
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|account
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|tok
operator|=
name|server
operator|.
name|getAccountToken
argument_list|()
operator|.
name|newToken
argument_list|(
name|idstr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XsrfException
name|e
parameter_list|)
block|{
name|getServletContext
argument_list|()
operator|.
name|log
argument_list|(
literal|"Account cookie signature impossible"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|account
operator|=
literal|null
expr_stmt|;
name|tok
operator|=
literal|""
expr_stmt|;
block|}
name|c
operator|=
operator|new
name|Cookie
argument_list|(
name|Gerrit
operator|.
name|ACCOUNT_COOKIE
argument_list|,
name|tok
argument_list|)
expr_stmt|;
name|c
operator|.
name|setPath
argument_list|(
name|req
operator|.
name|getContextPath
argument_list|()
operator|+
literal|"/"
argument_list|)
expr_stmt|;
if|if
condition|(
name|account
operator|==
literal|null
condition|)
block|{
name|c
operator|.
name|setMaxAge
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|addCookie
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|callback
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|SignInResult
operator|.
name|CANCEL
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|c
operator|.
name|setMaxAge
argument_list|(
name|server
operator|.
name|getSessionAge
argument_list|()
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|addCookie
argument_list|(
name|c
argument_list|)
expr_stmt|;
specifier|final
name|StringBuilder
name|me
init|=
operator|new
name|StringBuilder
argument_list|(
name|req
operator|.
name|getRequestURL
argument_list|()
argument_list|)
decl_stmt|;
name|append
argument_list|(
name|me
argument_list|,
name|Constants
operator|.
name|OPENID_MODE
argument_list|,
name|GMODE_CHKCOOKIE
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|me
argument_list|,
name|CALLBACK_PARMETER
argument_list|,
name|req
operator|.
name|getParameter
argument_list|(
name|CALLBACK_PARMETER
argument_list|)
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|me
argument_list|,
name|Gerrit
operator|.
name|ACCOUNT_COOKIE
argument_list|,
name|tok
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|sendRedirect
argument_list|(
name|me
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|modeChkSetCookie (final HttpServletRequest req, final HttpServletResponse rsp, final boolean isCheck)
specifier|private
name|void
name|modeChkSetCookie
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
name|HttpServletResponse
name|rsp
parameter_list|,
specifier|final
name|boolean
name|isCheck
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|exp
init|=
name|req
operator|.
name|getParameter
argument_list|(
name|Gerrit
operator|.
name|ACCOUNT_COOKIE
argument_list|)
decl_stmt|;
specifier|final
name|ValidToken
name|chk
decl_stmt|;
try|try
block|{
name|chk
operator|=
name|server
operator|.
name|getAccountToken
argument_list|()
operator|.
name|checkToken
argument_list|(
name|exp
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XsrfException
name|e
parameter_list|)
block|{
name|getServletContext
argument_list|()
operator|.
name|log
argument_list|(
literal|"Cannot validate cookie token"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|redirectChooseProvider
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|Account
operator|.
name|Id
name|id
decl_stmt|;
try|try
block|{
name|id
operator|=
operator|new
name|Account
operator|.
name|Id
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|chk
operator|.
name|getData
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|redirectChooseProvider
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
return|return;
block|}
name|Account
name|account
decl_stmt|;
try|try
block|{
specifier|final
name|ReviewDb
name|db
init|=
name|server
operator|.
name|getDatabase
argument_list|()
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
name|account
operator|=
name|db
operator|.
name|accounts
argument_list|()
operator|.
name|byId
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|getServletContext
argument_list|()
operator|.
name|log
argument_list|(
literal|"Account lookup failed for "
operator|+
name|id
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|account
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|account
operator|==
literal|null
condition|)
block|{
name|redirectChooseProvider
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|String
name|act
init|=
name|getCookie
argument_list|(
name|req
argument_list|,
name|Gerrit
operator|.
name|ACCOUNT_COOKIE
argument_list|)
decl_stmt|;
if|if
condition|(
name|isCheck
operator|&&
operator|!
name|exp
operator|.
name|equals
argument_list|(
name|act
argument_list|)
condition|)
block|{
comment|// Cookie won't set without "user interaction" (thanks Safari). Lets
comment|// send an HTML page to the browser and ask the user to click to let
comment|// us set the cookie.
comment|//
name|sendSetCookieHtml
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|exp
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|Cookie
name|c
init|=
operator|new
name|Cookie
argument_list|(
name|Gerrit
operator|.
name|ACCOUNT_COOKIE
argument_list|,
name|exp
argument_list|)
decl_stmt|;
name|c
operator|.
name|setPath
argument_list|(
name|req
operator|.
name|getContextPath
argument_list|()
operator|+
literal|"/"
argument_list|)
expr_stmt|;
name|c
operator|.
name|setMaxAge
argument_list|(
name|server
operator|.
name|getSessionAge
argument_list|()
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|addCookie
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|callback
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
operator|new
name|SignInResult
argument_list|(
name|SignInResult
operator|.
name|Status
operator|.
name|SUCCESS
argument_list|,
name|account
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|sendSetCookieHtml (final HttpServletRequest req, final HttpServletResponse rsp, final String exp)
specifier|private
name|void
name|sendSetCookieHtml
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
name|HttpServletResponse
name|rsp
parameter_list|,
specifier|final
name|String
name|exp
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Document
name|doc
init|=
name|HtmlDomUtil
operator|.
name|clone
argument_list|(
name|pleaseSetCookieDoc
argument_list|)
decl_stmt|;
specifier|final
name|Element
name|set_form
init|=
name|HtmlDomUtil
operator|.
name|find
argument_list|(
name|doc
argument_list|,
literal|"set_form"
argument_list|)
decl_stmt|;
name|set_form
operator|.
name|setAttribute
argument_list|(
literal|"action"
argument_list|,
name|req
operator|.
name|getRequestURL
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|HtmlDomUtil
operator|.
name|addHidden
argument_list|(
name|set_form
argument_list|,
name|Constants
operator|.
name|OPENID_MODE
argument_list|,
name|GMODE_SETCOOKIE
argument_list|)
expr_stmt|;
name|HtmlDomUtil
operator|.
name|addHidden
argument_list|(
name|set_form
argument_list|,
name|Gerrit
operator|.
name|ACCOUNT_COOKIE
argument_list|,
name|exp
argument_list|)
expr_stmt|;
name|HtmlDomUtil
operator|.
name|addHidden
argument_list|(
name|set_form
argument_list|,
name|CALLBACK_PARMETER
argument_list|,
name|req
operator|.
name|getParameter
argument_list|(
name|CALLBACK_PARMETER
argument_list|)
argument_list|)
expr_stmt|;
name|sendHtml
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|HtmlDomUtil
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getCookie (final HttpServletRequest req, final String name)
specifier|private
specifier|static
name|String
name|getCookie
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
block|{
specifier|final
name|Cookie
index|[]
name|allCookies
init|=
name|req
operator|.
name|getCookies
argument_list|()
decl_stmt|;
if|if
condition|(
name|allCookies
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|Cookie
name|c
range|:
name|allCookies
control|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|c
operator|.
name|getValue
argument_list|()
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|callback (final HttpServletRequest req, final HttpServletResponse rsp, final SignInResult result)
specifier|private
name|void
name|callback
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
name|HttpServletResponse
name|rsp
parameter_list|,
specifier|final
name|SignInResult
name|result
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|StringWriter
name|body
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|body
operator|.
name|write
argument_list|(
literal|"<html>"
argument_list|)
expr_stmt|;
name|body
operator|.
name|write
argument_list|(
literal|"<script><!--\n"
argument_list|)
expr_stmt|;
name|body
operator|.
name|write
argument_list|(
name|req
operator|.
name|getParameter
argument_list|(
name|CALLBACK_PARMETER
argument_list|)
argument_list|)
expr_stmt|;
name|body
operator|.
name|write
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
name|JsonServlet
operator|.
name|defaultGsonBuilder
argument_list|()
operator|.
name|create
argument_list|()
operator|.
name|toJson
argument_list|(
name|result
argument_list|,
name|body
argument_list|)
expr_stmt|;
name|body
operator|.
name|write
argument_list|(
literal|");\n"
argument_list|)
expr_stmt|;
name|body
operator|.
name|write
argument_list|(
literal|"// -->\n"
argument_list|)
expr_stmt|;
name|body
operator|.
name|write
argument_list|(
literal|"</script>"
argument_list|)
expr_stmt|;
name|body
operator|.
name|write
argument_list|(
literal|"</html>"
argument_list|)
expr_stmt|;
name|sendHtml
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
name|body
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|sendHtml (final HttpServletRequest req, final HttpServletResponse rsp, final String bodystr)
specifier|private
name|void
name|sendHtml
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|,
specifier|final
name|HttpServletResponse
name|rsp
parameter_list|,
specifier|final
name|String
name|bodystr
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|byte
index|[]
name|raw
init|=
name|bodystr
operator|.
name|getBytes
argument_list|(
name|HtmlDomUtil
operator|.
name|ENC
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|tosend
decl_stmt|;
if|if
condition|(
name|RPCServletUtils
operator|.
name|acceptsGzipEncoding
argument_list|(
name|req
argument_list|)
condition|)
block|{
name|rsp
operator|.
name|setHeader
argument_list|(
literal|"Content-Encoding"
argument_list|,
literal|"gzip"
argument_list|)
expr_stmt|;
specifier|final
name|ByteArrayOutputStream
name|compressed
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
specifier|final
name|GZIPOutputStream
name|gz
init|=
operator|new
name|GZIPOutputStream
argument_list|(
name|compressed
argument_list|)
decl_stmt|;
name|gz
operator|.
name|write
argument_list|(
name|raw
argument_list|)
expr_stmt|;
name|gz
operator|.
name|finish
argument_list|()
expr_stmt|;
name|gz
operator|.
name|flush
argument_list|()
expr_stmt|;
name|tosend
operator|=
name|compressed
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|tosend
operator|=
name|raw
expr_stmt|;
block|}
name|rsp
operator|.
name|setCharacterEncoding
argument_list|(
name|HtmlDomUtil
operator|.
name|ENC
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setContentType
argument_list|(
literal|"text/html"
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setHeader
argument_list|(
literal|"Cache-Control"
argument_list|,
literal|"no-cache"
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setDateHeader
argument_list|(
literal|"Expires"
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setContentLength
argument_list|(
name|tosend
operator|.
name|length
argument_list|)
expr_stmt|;
specifier|final
name|OutputStream
name|out
init|=
name|rsp
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|out
operator|.
name|write
argument_list|(
name|tosend
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|serverUrl (final HttpServletRequest req)
specifier|private
specifier|static
name|String
name|serverUrl
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|)
block|{
comment|// Assume this servlet is in the context with a simple name like "login"
comment|// and we were accessed without any path info. Clipping the last part of
comment|// the name from the URL should generate the web application's root path.
comment|//
specifier|final
name|String
name|uri
init|=
name|req
operator|.
name|getRequestURL
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|int
name|s
init|=
name|uri
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
return|return
name|s
operator|>=
literal|0
condition|?
name|uri
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|s
operator|+
literal|1
argument_list|)
else|:
name|uri
return|;
block|}
DECL|method|append (final StringBuilder buffer, final String name, final String value)
specifier|private
specifier|static
name|void
name|append
parameter_list|(
specifier|final
name|StringBuilder
name|buffer
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|buffer
operator|.
name|indexOf
argument_list|(
literal|"?"
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|'&'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|'?'
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|UrlEncoded
operator|.
name|encodeString
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

