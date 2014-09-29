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
DECL|package|com.google.gerrit.httpd
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
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
name|MoreObjects
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
name|NoSuchUserException
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
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|Filter
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterChain
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterConfig
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
name|ServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletResponse
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

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponseWrapper
import|;
end_import

begin_comment
comment|/**  * Authenticates the current user by HTTP basic authentication.  *<p>  * The current HTTP request is authenticated by looking up the username and  * password from the Base64 encoded Authorization header and validating them  * against any username/password configured authentication system in Gerrit.  * This filter is intended only to protect the {@link GitOverHttpServlet} and  * its handled URLs, which provide remote repository access over HTTP.  *  * @see<a href="http://www.ietf.org/rfc/rfc2617.txt">RFC 2617</a>  */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|ProjectBasicAuthFilter
class|class
name|ProjectBasicAuthFilter
implements|implements
name|Filter
block|{
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
name|ProjectBasicAuthFilter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|REALM_NAME
specifier|public
specifier|static
specifier|final
name|String
name|REALM_NAME
init|=
literal|"Gerrit Code Review"
decl_stmt|;
DECL|field|AUTHORIZATION
specifier|private
specifier|static
specifier|final
name|String
name|AUTHORIZATION
init|=
literal|"Authorization"
decl_stmt|;
DECL|field|LIT_BASIC
specifier|private
specifier|static
specifier|final
name|String
name|LIT_BASIC
init|=
literal|"Basic "
decl_stmt|;
DECL|field|session
specifier|private
specifier|final
name|DynamicItem
argument_list|<
name|WebSession
argument_list|>
name|session
decl_stmt|;
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
DECL|field|accountManager
specifier|private
specifier|final
name|AccountManager
name|accountManager
decl_stmt|;
DECL|field|authConfig
specifier|private
specifier|final
name|AuthConfig
name|authConfig
decl_stmt|;
annotation|@
name|Inject
DECL|method|ProjectBasicAuthFilter (DynamicItem<WebSession> session, AccountCache accountCache, AccountManager accountManager, AuthConfig authConfig)
name|ProjectBasicAuthFilter
parameter_list|(
name|DynamicItem
argument_list|<
name|WebSession
argument_list|>
name|session
parameter_list|,
name|AccountCache
name|accountCache
parameter_list|,
name|AccountManager
name|accountManager
parameter_list|,
name|AuthConfig
name|authConfig
parameter_list|)
block|{
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
name|this
operator|.
name|accountManager
operator|=
name|accountManager
expr_stmt|;
name|this
operator|.
name|authConfig
operator|=
name|authConfig
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init (FilterConfig config)
specifier|public
name|void
name|init
parameter_list|(
name|FilterConfig
name|config
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|doFilter (ServletRequest request, ServletResponse response, FilterChain chain)
specifier|public
name|void
name|doFilter
parameter_list|(
name|ServletRequest
name|request
parameter_list|,
name|ServletResponse
name|response
parameter_list|,
name|FilterChain
name|chain
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
name|HttpServletRequest
name|req
init|=
operator|(
name|HttpServletRequest
operator|)
name|request
decl_stmt|;
name|Response
name|rsp
init|=
operator|new
name|Response
argument_list|(
operator|(
name|HttpServletResponse
operator|)
name|response
argument_list|)
decl_stmt|;
if|if
condition|(
name|verify
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
condition|)
block|{
name|chain
operator|.
name|doFilter
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|verify (HttpServletRequest req, Response rsp)
specifier|private
name|boolean
name|verify
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|Response
name|rsp
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|hdr
init|=
name|req
operator|.
name|getHeader
argument_list|(
name|AUTHORIZATION
argument_list|)
decl_stmt|;
if|if
condition|(
name|hdr
operator|==
literal|null
operator|||
operator|!
name|hdr
operator|.
name|startsWith
argument_list|(
name|LIT_BASIC
argument_list|)
condition|)
block|{
comment|// Allow an anonymous connection through, or it might be using a
comment|// session cookie instead of basic authentication.
return|return
literal|true
return|;
block|}
specifier|final
name|byte
index|[]
name|decoded
init|=
name|Base64
operator|.
name|decodeBase64
argument_list|(
name|hdr
operator|.
name|substring
argument_list|(
name|LIT_BASIC
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|usernamePassword
init|=
operator|new
name|String
argument_list|(
name|decoded
argument_list|,
name|encoding
argument_list|(
name|req
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|splitPos
init|=
name|usernamePassword
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|splitPos
operator|<
literal|1
condition|)
block|{
name|rsp
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
name|String
name|username
init|=
name|usernamePassword
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|splitPos
argument_list|)
decl_stmt|;
name|String
name|password
init|=
name|usernamePassword
operator|.
name|substring
argument_list|(
name|splitPos
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|password
argument_list|)
condition|)
block|{
name|rsp
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
if|if
condition|(
name|authConfig
operator|.
name|isUserNameToLowerCase
argument_list|()
condition|)
block|{
name|username
operator|=
name|username
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
expr_stmt|;
block|}
specifier|final
name|AccountState
name|who
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
name|who
operator|==
literal|null
operator|||
operator|!
name|who
operator|.
name|getAccount
argument_list|()
operator|.
name|isActive
argument_list|()
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Authentication failed for "
operator|+
name|username
operator|+
literal|": account inactive or not provisioned in Gerrit"
argument_list|)
expr_stmt|;
name|rsp
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
if|if
condition|(
operator|!
name|authConfig
operator|.
name|isLdapAuthType
argument_list|()
operator|&&
operator|!
name|passwordMatchesTheUserGeneratedOne
argument_list|(
name|who
argument_list|,
name|username
argument_list|,
name|password
argument_list|)
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Authentication failed for "
operator|+
name|username
operator|+
literal|": password does not match the one stored in Gerrit"
argument_list|)
expr_stmt|;
name|rsp
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
name|AuthRequest
name|whoAuth
init|=
name|AuthRequest
operator|.
name|forUser
argument_list|(
name|username
argument_list|)
decl_stmt|;
name|whoAuth
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
try|try
block|{
name|AuthResult
name|whoAuthResult
init|=
name|accountManager
operator|.
name|authenticate
argument_list|(
name|whoAuth
argument_list|)
decl_stmt|;
name|WebSession
name|ws
init|=
name|session
operator|.
name|get
argument_list|()
decl_stmt|;
name|ws
operator|.
name|setUserAccountId
argument_list|(
name|whoAuthResult
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
name|ws
operator|.
name|setAccessPathOk
argument_list|(
name|AccessPath
operator|.
name|GIT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ws
operator|.
name|setAccessPathOk
argument_list|(
name|AccessPath
operator|.
name|REST_API
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchUserException
name|e
parameter_list|)
block|{
if|if
condition|(
name|password
operator|.
name|equals
argument_list|(
name|who
operator|.
name|getPassword
argument_list|(
name|who
operator|.
name|getUserName
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
name|WebSession
name|ws
init|=
name|session
operator|.
name|get
argument_list|()
decl_stmt|;
name|ws
operator|.
name|setUserAccountId
argument_list|(
name|who
operator|.
name|getAccount
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|ws
operator|.
name|setAccessPathOk
argument_list|(
name|AccessPath
operator|.
name|GIT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ws
operator|.
name|setAccessPathOk
argument_list|(
name|AccessPath
operator|.
name|REST_API
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Authentication failed for "
operator|+
name|username
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|rsp
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
catch|catch
parameter_list|(
name|AccountException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Authentication failed for "
operator|+
name|username
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|rsp
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
DECL|method|passwordMatchesTheUserGeneratedOne (AccountState who, String username, String password)
specifier|private
name|boolean
name|passwordMatchesTheUserGeneratedOne
parameter_list|(
name|AccountState
name|who
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
block|{
name|String
name|accountPassword
init|=
name|who
operator|.
name|getPassword
argument_list|(
name|username
argument_list|)
decl_stmt|;
return|return
name|accountPassword
operator|!=
literal|null
operator|&&
name|password
operator|!=
literal|null
operator|&&
name|accountPassword
operator|.
name|equals
argument_list|(
name|password
argument_list|)
return|;
block|}
DECL|method|encoding (HttpServletRequest req)
specifier|private
name|String
name|encoding
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
return|return
name|MoreObjects
operator|.
name|firstNonNull
argument_list|(
name|req
operator|.
name|getCharacterEncoding
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
DECL|class|Response
class|class
name|Response
extends|extends
name|HttpServletResponseWrapper
block|{
DECL|field|WWW_AUTHENTICATE
specifier|private
specifier|static
specifier|final
name|String
name|WWW_AUTHENTICATE
init|=
literal|"WWW-Authenticate"
decl_stmt|;
DECL|method|Response (HttpServletResponse rsp)
name|Response
parameter_list|(
name|HttpServletResponse
name|rsp
parameter_list|)
block|{
name|super
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
block|}
DECL|method|status (int sc)
specifier|private
name|void
name|status
parameter_list|(
name|int
name|sc
parameter_list|)
block|{
if|if
condition|(
name|sc
operator|==
name|SC_UNAUTHORIZED
condition|)
block|{
name|StringBuilder
name|v
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|v
operator|.
name|append
argument_list|(
name|LIT_BASIC
argument_list|)
expr_stmt|;
name|v
operator|.
name|append
argument_list|(
literal|"realm=\""
argument_list|)
operator|.
name|append
argument_list|(
name|REALM_NAME
argument_list|)
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
name|setHeader
argument_list|(
name|WWW_AUTHENTICATE
argument_list|,
name|v
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|containsHeader
argument_list|(
name|WWW_AUTHENTICATE
argument_list|)
condition|)
block|{
name|setHeader
argument_list|(
name|WWW_AUTHENTICATE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|sendError (int sc, String msg)
specifier|public
name|void
name|sendError
parameter_list|(
name|int
name|sc
parameter_list|,
name|String
name|msg
parameter_list|)
throws|throws
name|IOException
block|{
name|status
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|super
operator|.
name|sendError
argument_list|(
name|sc
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sendError (int sc)
specifier|public
name|void
name|sendError
parameter_list|(
name|int
name|sc
parameter_list|)
throws|throws
name|IOException
block|{
name|status
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|super
operator|.
name|sendError
argument_list|(
name|sc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Deprecated
DECL|method|setStatus (int sc, String sm)
specifier|public
name|void
name|setStatus
parameter_list|(
name|int
name|sc
parameter_list|,
name|String
name|sm
parameter_list|)
block|{
name|status
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|super
operator|.
name|setStatus
argument_list|(
name|sc
argument_list|,
name|sm
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setStatus (int sc)
specifier|public
name|void
name|setStatus
parameter_list|(
name|int
name|sc
parameter_list|)
block|{
name|status
argument_list|(
name|sc
argument_list|)
expr_stmt|;
name|super
operator|.
name|setStatus
argument_list|(
name|sc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

