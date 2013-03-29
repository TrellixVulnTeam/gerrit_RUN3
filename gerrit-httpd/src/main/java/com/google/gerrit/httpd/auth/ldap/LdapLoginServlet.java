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
name|common
operator|.
name|base
operator|.
name|Objects
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
name|server
operator|.
name|config
operator|.
name|SitePaths
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
name|java
operator|.
name|io
operator|.
name|File
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
name|annotation
operator|.
name|Nullable
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

begin_comment
comment|/** Handles username/password based authentication against the directory. */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
annotation|@
name|Singleton
DECL|class|LdapLoginServlet
class|class
name|LdapLoginServlet
extends|extends
name|HttpServlet
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
name|LdapLoginServlet
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|accountManager
specifier|private
specifier|final
name|AccountManager
name|accountManager
decl_stmt|;
DECL|field|webSession
specifier|private
specifier|final
name|Provider
argument_list|<
name|WebSession
argument_list|>
name|webSession
decl_stmt|;
DECL|field|urlProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|String
argument_list|>
name|urlProvider
decl_stmt|;
DECL|field|sitePaths
specifier|private
specifier|final
name|SitePaths
name|sitePaths
decl_stmt|;
annotation|@
name|Inject
DECL|method|LdapLoginServlet (AccountManager accountManager, Provider<WebSession> webSession, @CanonicalWebUrl @Nullable Provider<String> urlProvider, SitePaths sitePaths)
name|LdapLoginServlet
parameter_list|(
name|AccountManager
name|accountManager
parameter_list|,
name|Provider
argument_list|<
name|WebSession
argument_list|>
name|webSession
parameter_list|,
annotation|@
name|CanonicalWebUrl
annotation|@
name|Nullable
name|Provider
argument_list|<
name|String
argument_list|>
name|urlProvider
parameter_list|,
name|SitePaths
name|sitePaths
parameter_list|)
block|{
name|this
operator|.
name|accountManager
operator|=
name|accountManager
expr_stmt|;
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
name|sitePaths
operator|=
name|sitePaths
expr_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|urlProvider
operator|.
name|get
argument_list|()
argument_list|)
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"gerrit.canonicalWebUrl must be set in gerrit.config"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|sendForm (HttpServletRequest req, HttpServletResponse res, @Nullable String errorMessage)
specifier|private
name|void
name|sendForm
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|,
annotation|@
name|Nullable
name|String
name|errorMessage
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|self
init|=
name|req
operator|.
name|getRequestURI
argument_list|()
decl_stmt|;
name|String
name|cancel
init|=
name|Objects
operator|.
name|firstNonNull
argument_list|(
name|urlProvider
operator|.
name|get
argument_list|()
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|String
name|token
init|=
name|getToken
argument_list|(
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|token
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|cancel
operator|+=
literal|"#"
operator|+
name|token
expr_stmt|;
block|}
name|Document
name|doc
init|=
name|HtmlDomUtil
operator|.
name|parseFile
argument_list|(
name|LdapLoginServlet
operator|.
name|class
argument_list|,
literal|"LoginForm.html"
argument_list|)
decl_stmt|;
name|injectCssFile
argument_list|(
name|doc
argument_list|,
literal|"gerrit_sitecss"
argument_list|,
name|sitePaths
operator|.
name|site_css
argument_list|)
expr_stmt|;
name|injectXmlFile
argument_list|(
name|doc
argument_list|,
literal|"gerrit_header"
argument_list|,
name|sitePaths
operator|.
name|site_header
argument_list|)
expr_stmt|;
name|injectXmlFile
argument_list|(
name|doc
argument_list|,
literal|"gerrit_footer"
argument_list|,
name|sitePaths
operator|.
name|site_footer
argument_list|)
expr_stmt|;
name|HtmlDomUtil
operator|.
name|find
argument_list|(
name|doc
argument_list|,
literal|"hostName"
argument_list|)
operator|.
name|setTextContent
argument_list|(
name|req
operator|.
name|getServerName
argument_list|()
argument_list|)
expr_stmt|;
name|HtmlDomUtil
operator|.
name|find
argument_list|(
name|doc
argument_list|,
literal|"login_form"
argument_list|)
operator|.
name|setAttribute
argument_list|(
literal|"action"
argument_list|,
name|self
argument_list|)
expr_stmt|;
name|HtmlDomUtil
operator|.
name|find
argument_list|(
name|doc
argument_list|,
literal|"cancel_link"
argument_list|)
operator|.
name|setAttribute
argument_list|(
literal|"href"
argument_list|,
name|cancel
argument_list|)
expr_stmt|;
name|Element
name|emsg
init|=
name|HtmlDomUtil
operator|.
name|find
argument_list|(
name|doc
argument_list|,
literal|"error_message"
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|errorMessage
argument_list|)
condition|)
block|{
name|emsg
operator|.
name|getParentNode
argument_list|()
operator|.
name|removeChild
argument_list|(
name|emsg
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|emsg
operator|.
name|setTextContent
argument_list|(
name|errorMessage
argument_list|)
expr_stmt|;
block|}
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
name|res
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_UNAUTHORIZED
argument_list|)
expr_stmt|;
name|res
operator|.
name|setContentType
argument_list|(
literal|"text/html"
argument_list|)
expr_stmt|;
name|res
operator|.
name|setCharacterEncoding
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|res
operator|.
name|setContentLength
argument_list|(
name|bin
operator|.
name|length
argument_list|)
expr_stmt|;
name|ServletOutputStream
name|out
init|=
name|res
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
name|bin
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
DECL|method|injectCssFile (final Document hostDoc, final String id, final File src)
specifier|private
name|void
name|injectCssFile
parameter_list|(
specifier|final
name|Document
name|hostDoc
parameter_list|,
specifier|final
name|String
name|id
parameter_list|,
specifier|final
name|File
name|src
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Element
name|banner
init|=
name|HtmlDomUtil
operator|.
name|find
argument_list|(
name|hostDoc
argument_list|,
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|banner
operator|!=
literal|null
condition|)
block|{
while|while
condition|(
name|banner
operator|.
name|getFirstChild
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|banner
operator|.
name|removeChild
argument_list|(
name|banner
operator|.
name|getFirstChild
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|css
init|=
name|HtmlDomUtil
operator|.
name|readFile
argument_list|(
name|src
operator|.
name|getParentFile
argument_list|()
argument_list|,
name|src
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|css
operator|==
literal|null
condition|)
block|{
name|banner
operator|.
name|getParentNode
argument_list|()
operator|.
name|removeChild
argument_list|(
name|banner
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|banner
operator|.
name|removeAttribute
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
name|banner
operator|.
name|appendChild
argument_list|(
name|hostDoc
operator|.
name|createCDATASection
argument_list|(
literal|"\n"
operator|+
name|css
operator|+
literal|"\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|injectXmlFile (final Document hostDoc, final String id, final File src)
specifier|private
name|void
name|injectXmlFile
parameter_list|(
specifier|final
name|Document
name|hostDoc
parameter_list|,
specifier|final
name|String
name|id
parameter_list|,
specifier|final
name|File
name|src
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Element
name|banner
init|=
name|HtmlDomUtil
operator|.
name|find
argument_list|(
name|hostDoc
argument_list|,
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|banner
operator|!=
literal|null
condition|)
block|{
while|while
condition|(
name|banner
operator|.
name|getFirstChild
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|banner
operator|.
name|removeChild
argument_list|(
name|banner
operator|.
name|getFirstChild
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Document
name|html
init|=
name|HtmlDomUtil
operator|.
name|parseFile
argument_list|(
name|src
argument_list|)
decl_stmt|;
if|if
condition|(
name|html
operator|==
literal|null
condition|)
block|{
name|banner
operator|.
name|getParentNode
argument_list|()
operator|.
name|removeChild
argument_list|(
name|banner
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|Element
name|content
init|=
name|html
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|banner
operator|.
name|appendChild
argument_list|(
name|hostDoc
operator|.
name|importNode
argument_list|(
name|content
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|doGet (HttpServletRequest req, HttpServletResponse res)
specifier|protected
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|)
throws|throws
name|IOException
block|{
name|sendForm
argument_list|(
name|req
argument_list|,
name|res
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doPost (HttpServletRequest req, HttpServletResponse res)
specifier|protected
name|void
name|doPost
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|String
name|username
init|=
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|req
operator|.
name|getParameter
argument_list|(
literal|"username"
argument_list|)
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|password
init|=
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|req
operator|.
name|getParameter
argument_list|(
literal|"password"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|remember
init|=
name|Strings
operator|.
name|nullToEmpty
argument_list|(
name|req
operator|.
name|getParameter
argument_list|(
literal|"rememberme"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|username
operator|.
name|isEmpty
argument_list|()
operator|||
name|password
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|sendForm
argument_list|(
name|req
argument_list|,
name|res
argument_list|,
literal|"Invalid username or password."
argument_list|)
expr_stmt|;
return|return;
block|}
name|AuthRequest
name|areq
init|=
name|AuthRequest
operator|.
name|forUser
argument_list|(
name|username
argument_list|)
decl_stmt|;
name|areq
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
name|AuthResult
name|ares
decl_stmt|;
try|try
block|{
name|ares
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
name|AccountUserNameException
name|e
parameter_list|)
block|{
name|sendForm
argument_list|(
name|req
argument_list|,
name|res
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
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
name|sendForm
argument_list|(
name|req
argument_list|,
name|res
argument_list|,
literal|"Authentication unavailable at this time."
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
name|log
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"'%s' failed to sign in: %s"
argument_list|,
name|username
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|sendForm
argument_list|(
name|req
argument_list|,
name|res
argument_list|,
literal|"Invalid username or password."
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"LDAP authentication failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|sendForm
argument_list|(
name|req
argument_list|,
name|res
argument_list|,
literal|"Authentication unavailable at this time."
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|token
init|=
name|getToken
argument_list|(
name|req
argument_list|)
decl_stmt|;
name|StringBuilder
name|dest
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|dest
operator|.
name|append
argument_list|(
name|urlProvider
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|dest
operator|.
name|append
argument_list|(
literal|'#'
argument_list|)
expr_stmt|;
name|dest
operator|.
name|append
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|CacheHeaders
operator|.
name|setNotCacheable
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|webSession
operator|.
name|get
argument_list|()
operator|.
name|login
argument_list|(
name|ares
argument_list|,
literal|"1"
operator|.
name|equals
argument_list|(
name|remember
argument_list|)
argument_list|)
expr_stmt|;
name|res
operator|.
name|sendRedirect
argument_list|(
name|dest
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getToken (final HttpServletRequest req)
specifier|private
specifier|static
name|String
name|getToken
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|)
block|{
name|String
name|token
init|=
name|req
operator|.
name|getPathInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
operator|||
name|token
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|token
operator|=
name|PageLinks
operator|.
name|MINE
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|token
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|token
operator|=
literal|"/"
operator|+
name|token
expr_stmt|;
block|}
return|return
name|token
return|;
block|}
block|}
end_class

end_unit

