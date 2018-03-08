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
DECL|package|com.google.gerrit.httpd.auth.container
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
name|container
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
name|server
operator|.
name|account
operator|.
name|ExternalId
operator|.
name|SCHEME_EXTERNAL
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
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
name|PageLinks
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
name|HtmlDomUtil
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
name|account
operator|.
name|ExternalId
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
name|gwtexpui
operator|.
name|server
operator|.
name|CacheHeaders
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
name|ServletOutputStream
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
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
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
name|NodeList
import|;
end_import

begin_comment
comment|/**  * Initializes the user session if HTTP authentication is enabled.  *  *<p>If HTTP authentication has been enabled this servlet binds to {@code /login/} and initializes  * the user session based on user information contained in the HTTP request.  */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|HttpLoginServlet
class|class
name|HttpLoginServlet
extends|extends
name|HttpServlet
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
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
name|HttpLoginServlet
operator|.
name|class
argument_list|)
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
DECL|field|urlProvider
specifier|private
specifier|final
name|CanonicalWebUrl
name|urlProvider
decl_stmt|;
DECL|field|accountManager
specifier|private
specifier|final
name|AccountManager
name|accountManager
decl_stmt|;
DECL|field|authFilter
specifier|private
specifier|final
name|HttpAuthFilter
name|authFilter
decl_stmt|;
DECL|field|authConfig
specifier|private
specifier|final
name|AuthConfig
name|authConfig
decl_stmt|;
annotation|@
name|Inject
DECL|method|HttpLoginServlet ( final DynamicItem<WebSession> webSession, final CanonicalWebUrl urlProvider, final AccountManager accountManager, final HttpAuthFilter authFilter, final AuthConfig authConfig)
name|HttpLoginServlet
parameter_list|(
specifier|final
name|DynamicItem
argument_list|<
name|WebSession
argument_list|>
name|webSession
parameter_list|,
specifier|final
name|CanonicalWebUrl
name|urlProvider
parameter_list|,
specifier|final
name|AccountManager
name|accountManager
parameter_list|,
specifier|final
name|HttpAuthFilter
name|authFilter
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
name|urlProvider
operator|=
name|urlProvider
expr_stmt|;
name|this
operator|.
name|accountManager
operator|=
name|accountManager
expr_stmt|;
name|this
operator|.
name|authFilter
operator|=
name|authFilter
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
DECL|method|doGet (final HttpServletRequest req, final HttpServletResponse rsp)
specifier|protected
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
name|ServletException
throws|,
name|IOException
block|{
specifier|final
name|String
name|token
init|=
name|LoginUrlToken
operator|.
name|getToken
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|CacheHeaders
operator|.
name|setNotCacheable
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
specifier|final
name|String
name|user
init|=
name|authFilter
operator|.
name|getRemoteUser
argument_list|(
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|user
argument_list|)
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Unable to authenticate user by "
operator|+
name|authFilter
operator|.
name|getLoginHeader
argument_list|()
operator|+
literal|" request header.  Check container or server configuration."
argument_list|)
expr_stmt|;
specifier|final
name|Document
name|doc
init|=
name|HtmlDomUtil
operator|.
name|parseFile
argument_list|(
comment|//
name|HttpLoginServlet
operator|.
name|class
argument_list|,
literal|"ConfigurationError.html"
argument_list|)
decl_stmt|;
name|replace
argument_list|(
name|doc
argument_list|,
literal|"loginHeader"
argument_list|,
name|authFilter
operator|.
name|getLoginHeader
argument_list|()
argument_list|)
expr_stmt|;
name|replace
argument_list|(
name|doc
argument_list|,
literal|"ServerName"
argument_list|,
name|req
operator|.
name|getServerName
argument_list|()
argument_list|)
expr_stmt|;
name|replace
argument_list|(
name|doc
argument_list|,
literal|"ServerPort"
argument_list|,
literal|":"
operator|+
name|req
operator|.
name|getServerPort
argument_list|()
argument_list|)
expr_stmt|;
name|replace
argument_list|(
name|doc
argument_list|,
literal|"ContextPath"
argument_list|,
name|req
operator|.
name|getContextPath
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|bin
init|=
name|HtmlDomUtil
operator|.
name|toUTF8
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|rsp
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
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
name|setCharacterEncoding
argument_list|(
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setContentLength
argument_list|(
name|bin
operator|.
name|length
argument_list|)
expr_stmt|;
try|try
init|(
name|ServletOutputStream
name|out
init|=
name|rsp
operator|.
name|getOutputStream
argument_list|()
init|)
block|{
name|out
operator|.
name|write
argument_list|(
name|bin
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
specifier|final
name|AuthRequest
name|areq
init|=
name|AuthRequest
operator|.
name|forUser
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|areq
operator|.
name|setDisplayName
argument_list|(
name|authFilter
operator|.
name|getRemoteDisplayname
argument_list|(
name|req
argument_list|)
argument_list|)
expr_stmt|;
name|areq
operator|.
name|setEmailAddress
argument_list|(
name|authFilter
operator|.
name|getRemoteEmail
argument_list|(
name|req
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|AuthResult
name|arsp
decl_stmt|;
try|try
block|{
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
name|String
name|remoteExternalId
init|=
name|authFilter
operator|.
name|getRemoteExternalIdToken
argument_list|(
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
name|remoteExternalId
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Associating external identity \"{}\" to user \"{}\""
argument_list|,
name|remoteExternalId
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|updateRemoteExternalId
argument_list|(
name|arsp
argument_list|,
name|remoteExternalId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccountException
decl||
name|OrmException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Unable to associate external identity \""
operator|+
name|remoteExternalId
operator|+
literal|"\" to user \""
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
block|}
specifier|final
name|StringBuilder
name|rdr
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|arsp
operator|.
name|isNew
argument_list|()
operator|&&
name|authConfig
operator|.
name|getRegisterPageUrl
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|rdr
operator|.
name|append
argument_list|(
name|authConfig
operator|.
name|getRegisterPageUrl
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rdr
operator|.
name|append
argument_list|(
name|urlProvider
operator|.
name|get
argument_list|(
name|req
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|arsp
operator|.
name|isNew
argument_list|()
operator|&&
operator|!
name|token
operator|.
name|startsWith
argument_list|(
name|PageLinks
operator|.
name|REGISTER
operator|+
literal|"/"
argument_list|)
condition|)
block|{
name|rdr
operator|.
name|append
argument_list|(
literal|'#'
operator|+
name|PageLinks
operator|.
name|REGISTER
argument_list|)
expr_stmt|;
block|}
name|rdr
operator|.
name|append
argument_list|(
name|token
argument_list|)
expr_stmt|;
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
comment|/* persistent cookie */
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
DECL|method|updateRemoteExternalId (AuthResult arsp, String remoteAuthToken)
specifier|private
name|void
name|updateRemoteExternalId
parameter_list|(
name|AuthResult
name|arsp
parameter_list|,
name|String
name|remoteAuthToken
parameter_list|)
throws|throws
name|AccountException
throws|,
name|OrmException
throws|,
name|IOException
block|{
name|accountManager
operator|.
name|updateLink
argument_list|(
name|arsp
operator|.
name|getAccountId
argument_list|()
argument_list|,
operator|new
name|AuthRequest
argument_list|(
name|ExternalId
operator|.
name|Key
operator|.
name|create
argument_list|(
name|SCHEME_EXTERNAL
argument_list|,
name|remoteAuthToken
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|replace (Document doc, String name, String value)
specifier|private
name|void
name|replace
parameter_list|(
name|Document
name|doc
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|Element
name|e
init|=
name|HtmlDomUtil
operator|.
name|find
argument_list|(
name|doc
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|!=
literal|null
condition|)
block|{
name|e
operator|.
name|setTextContent
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|replaceByClass
argument_list|(
name|doc
argument_list|,
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|replaceByClass (Node parent, String name, String value)
specifier|private
name|void
name|replaceByClass
parameter_list|(
name|Node
name|parent
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
specifier|final
name|NodeList
name|list
init|=
name|parent
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Node
name|n
init|=
name|list
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|instanceof
name|Element
condition|)
block|{
specifier|final
name|Element
name|e
init|=
operator|(
name|Element
operator|)
name|n
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|e
operator|.
name|getAttribute
argument_list|(
literal|"class"
argument_list|)
argument_list|)
condition|)
block|{
name|e
operator|.
name|setTextContent
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|replaceByClass
argument_list|(
name|n
argument_list|,
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

