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
DECL|package|com.google.gerrit.httpd.auth.become
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
name|become
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
name|reviewdb
operator|.
name|client
operator|.
name|AccountExternalId
operator|.
name|SCHEME_USERNAME
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
name|httpd
operator|.
name|template
operator|.
name|SiteHeaderFooter
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
name|reviewdb
operator|.
name|client
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
name|reviewdb
operator|.
name|server
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
name|gwtorm
operator|.
name|server
operator|.
name|ResultSet
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
name|SchemaFactory
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
name|FileNotFoundException
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
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletContext
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

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
annotation|@
name|Singleton
DECL|class|BecomeAnyAccountLoginServlet
class|class
name|BecomeAnyAccountLoginServlet
extends|extends
name|HttpServlet
block|{
DECL|field|IS_DEV
specifier|private
specifier|static
specifier|final
name|boolean
name|IS_DEV
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"Gerrit.GwtDevMode"
argument_list|)
decl_stmt|;
DECL|field|schema
specifier|private
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
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
DECL|field|accountManager
specifier|private
specifier|final
name|AccountManager
name|accountManager
decl_stmt|;
DECL|field|headers
specifier|private
specifier|final
name|SiteHeaderFooter
name|headers
decl_stmt|;
annotation|@
name|Inject
DECL|method|BecomeAnyAccountLoginServlet (final Provider<WebSession> ws, final SchemaFactory<ReviewDb> sf, final AccountManager am, final ServletContext servletContext, SiteHeaderFooter shf)
name|BecomeAnyAccountLoginServlet
parameter_list|(
specifier|final
name|Provider
argument_list|<
name|WebSession
argument_list|>
name|ws
parameter_list|,
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|sf
parameter_list|,
specifier|final
name|AccountManager
name|am
parameter_list|,
specifier|final
name|ServletContext
name|servletContext
parameter_list|,
name|SiteHeaderFooter
name|shf
parameter_list|)
block|{
name|webSession
operator|=
name|ws
expr_stmt|;
name|schema
operator|=
name|sf
expr_stmt|;
name|accountManager
operator|=
name|am
expr_stmt|;
name|headers
operator|=
name|shf
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
name|IOException
throws|,
name|ServletException
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
specifier|protected
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
throws|,
name|ServletException
block|{
name|CacheHeaders
operator|.
name|setNotCacheable
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
specifier|final
name|AuthResult
name|res
decl_stmt|;
if|if
condition|(
literal|"create_account"
operator|.
name|equals
argument_list|(
name|req
operator|.
name|getParameter
argument_list|(
literal|"action"
argument_list|)
argument_list|)
condition|)
block|{
name|res
operator|=
name|create
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|req
operator|.
name|getParameter
argument_list|(
literal|"user_name"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|res
operator|=
name|byUserName
argument_list|(
name|rsp
argument_list|,
name|req
operator|.
name|getParameter
argument_list|(
literal|"user_name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|req
operator|.
name|getParameter
argument_list|(
literal|"preferred_email"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|res
operator|=
name|byPreferredEmail
argument_list|(
name|rsp
argument_list|,
name|req
operator|.
name|getParameter
argument_list|(
literal|"preferred_email"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|req
operator|.
name|getParameter
argument_list|(
literal|"account_id"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|res
operator|=
name|byAccountId
argument_list|(
name|rsp
argument_list|,
name|req
operator|.
name|getParameter
argument_list|(
literal|"account_id"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|byte
index|[]
name|raw
decl_stmt|;
try|try
block|{
name|raw
operator|=
name|prepareHtmlOutput
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
name|e
argument_list|)
throw|;
block|}
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
name|HtmlDomUtil
operator|.
name|ENC
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setContentLength
argument_list|(
name|raw
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
name|raw
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
return|return;
block|}
if|if
condition|(
name|res
operator|!=
literal|null
condition|)
block|{
name|webSession
operator|.
name|get
argument_list|()
operator|.
name|login
argument_list|(
name|res
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|StringBuilder
name|rdr
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|rdr
operator|.
name|append
argument_list|(
name|Objects
operator|.
name|firstNonNull
argument_list|(
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|req
operator|.
name|getContextPath
argument_list|()
argument_list|)
argument_list|,
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|IS_DEV
operator|&&
name|req
operator|.
name|getParameter
argument_list|(
literal|"gwt.codesvr"
argument_list|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|rdr
operator|.
name|indexOf
argument_list|(
literal|"?"
argument_list|)
operator|<
literal|0
condition|)
block|{
name|rdr
operator|.
name|append
argument_list|(
literal|"?"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rdr
operator|.
name|append
argument_list|(
literal|"&"
argument_list|)
expr_stmt|;
block|}
name|rdr
operator|.
name|append
argument_list|(
literal|"gwt.codesvr="
argument_list|)
operator|.
name|append
argument_list|(
name|req
operator|.
name|getParameter
argument_list|(
literal|"gwt.codesvr"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|rdr
operator|.
name|append
argument_list|(
literal|'#'
argument_list|)
expr_stmt|;
if|if
condition|(
name|res
operator|.
name|isNew
argument_list|()
condition|)
block|{
name|rdr
operator|.
name|append
argument_list|(
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
name|PageLinks
operator|.
name|MINE
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
else|else
block|{
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
name|HtmlDomUtil
operator|.
name|ENC
argument_list|)
expr_stmt|;
specifier|final
name|Writer
name|out
init|=
name|rsp
operator|.
name|getWriter
argument_list|()
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<html>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<body>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"<h1>Account Not Found</h1>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"</body>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"</html>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|prepareHtmlOutput ()
specifier|private
name|byte
index|[]
name|prepareHtmlOutput
parameter_list|()
throws|throws
name|IOException
throws|,
name|OrmException
block|{
specifier|final
name|String
name|pageName
init|=
literal|"BecomeAnyAccount.html"
decl_stmt|;
name|Document
name|doc
init|=
name|headers
operator|.
name|parse
argument_list|(
name|getClass
argument_list|()
argument_list|,
name|pageName
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"No "
operator|+
name|pageName
operator|+
literal|" in webapp"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|IS_DEV
condition|)
block|{
specifier|final
name|Element
name|devmode
init|=
name|HtmlDomUtil
operator|.
name|find
argument_list|(
name|doc
argument_list|,
literal|"gwtdevmode"
argument_list|)
decl_stmt|;
if|if
condition|(
name|devmode
operator|!=
literal|null
condition|)
block|{
name|devmode
operator|.
name|getParentNode
argument_list|()
operator|.
name|removeChild
argument_list|(
name|devmode
argument_list|)
expr_stmt|;
block|}
block|}
name|Element
name|userlistElement
init|=
name|HtmlDomUtil
operator|.
name|find
argument_list|(
name|doc
argument_list|,
literal|"userlist"
argument_list|)
decl_stmt|;
name|ReviewDb
name|db
init|=
name|schema
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
name|ResultSet
argument_list|<
name|Account
argument_list|>
name|accounts
init|=
name|db
operator|.
name|accounts
argument_list|()
operator|.
name|firstNById
argument_list|(
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|Account
name|a
range|:
name|accounts
control|)
block|{
name|String
name|displayName
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|getUserName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|displayName
operator|=
name|a
operator|.
name|getUserName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|a
operator|.
name|getFullName
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|a
operator|.
name|getFullName
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|displayName
operator|=
name|a
operator|.
name|getFullName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|a
operator|.
name|getPreferredEmail
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|displayName
operator|=
name|a
operator|.
name|getPreferredEmail
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|displayName
operator|=
name|a
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|Element
name|linkElement
init|=
name|doc
operator|.
name|createElement
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|linkElement
operator|.
name|setAttribute
argument_list|(
literal|"href"
argument_list|,
literal|"?account_id="
operator|+
name|a
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|linkElement
operator|.
name|setTextContent
argument_list|(
name|displayName
argument_list|)
expr_stmt|;
name|userlistElement
operator|.
name|appendChild
argument_list|(
name|linkElement
argument_list|)
expr_stmt|;
name|userlistElement
operator|.
name|appendChild
argument_list|(
name|doc
operator|.
name|createElement
argument_list|(
literal|"br"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|db
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|HtmlDomUtil
operator|.
name|toUTF8
argument_list|(
name|doc
argument_list|)
return|;
block|}
DECL|method|auth (final Account account)
specifier|private
name|AuthResult
name|auth
parameter_list|(
specifier|final
name|Account
name|account
parameter_list|)
block|{
if|if
condition|(
name|account
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|AuthResult
argument_list|(
name|account
operator|.
name|getId
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|auth (final AccountExternalId account)
specifier|private
name|AuthResult
name|auth
parameter_list|(
specifier|final
name|AccountExternalId
name|account
parameter_list|)
block|{
if|if
condition|(
name|account
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|AuthResult
argument_list|(
name|account
operator|.
name|getAccountId
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|byUserName (final HttpServletResponse rsp, final String userName)
specifier|private
name|AuthResult
name|byUserName
parameter_list|(
specifier|final
name|HttpServletResponse
name|rsp
parameter_list|,
specifier|final
name|String
name|userName
parameter_list|)
block|{
try|try
block|{
specifier|final
name|ReviewDb
name|db
init|=
name|schema
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
name|AccountExternalId
operator|.
name|Key
name|key
init|=
operator|new
name|AccountExternalId
operator|.
name|Key
argument_list|(
name|SCHEME_USERNAME
argument_list|,
name|userName
argument_list|)
decl_stmt|;
return|return
name|auth
argument_list|(
name|db
operator|.
name|accountExternalIds
argument_list|()
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
return|;
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
literal|"cannot query database"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|byPreferredEmail (final HttpServletResponse rsp, final String email)
specifier|private
name|AuthResult
name|byPreferredEmail
parameter_list|(
specifier|final
name|HttpServletResponse
name|rsp
parameter_list|,
specifier|final
name|String
name|email
parameter_list|)
block|{
try|try
block|{
specifier|final
name|ReviewDb
name|db
init|=
name|schema
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
name|List
argument_list|<
name|Account
argument_list|>
name|matches
init|=
name|db
operator|.
name|accounts
argument_list|()
operator|.
name|byPreferredEmail
argument_list|(
name|email
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
return|return
name|matches
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|?
name|auth
argument_list|(
name|matches
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
else|:
literal|null
return|;
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
literal|"cannot query database"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|byAccountId (final HttpServletResponse rsp, final String idStr)
specifier|private
name|AuthResult
name|byAccountId
parameter_list|(
specifier|final
name|HttpServletResponse
name|rsp
parameter_list|,
specifier|final
name|String
name|idStr
parameter_list|)
block|{
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
name|Account
operator|.
name|Id
operator|.
name|parse
argument_list|(
name|idStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
specifier|final
name|ReviewDb
name|db
init|=
name|schema
operator|.
name|open
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|auth
argument_list|(
name|db
operator|.
name|accounts
argument_list|()
operator|.
name|get
argument_list|(
name|id
argument_list|)
argument_list|)
return|;
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
literal|"cannot query database"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|create ()
specifier|private
name|AuthResult
name|create
parameter_list|()
block|{
name|String
name|fakeId
init|=
name|AccountExternalId
operator|.
name|SCHEME_UUID
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|accountManager
operator|.
name|authenticate
argument_list|(
operator|new
name|AuthRequest
argument_list|(
name|fakeId
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|AccountException
name|e
parameter_list|)
block|{
name|getServletContext
argument_list|()
operator|.
name|log
argument_list|(
literal|"cannot create new account"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

