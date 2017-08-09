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
name|server
operator|.
name|account
operator|.
name|externalids
operator|.
name|ExternalId
operator|.
name|SCHEME_USERNAME
import|;
end_import

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
name|externalids
operator|.
name|ExternalId
operator|.
name|SCHEME_UUID
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
name|Accounts
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
name|externalids
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
name|query
operator|.
name|account
operator|.
name|InternalAccountQuery
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
name|Optional
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
DECL|field|webSession
specifier|private
specifier|final
name|DynamicItem
argument_list|<
name|WebSession
argument_list|>
name|webSession
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
DECL|field|accounts
specifier|private
specifier|final
name|Accounts
name|accounts
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
DECL|field|headers
specifier|private
specifier|final
name|SiteHeaderFooter
name|headers
decl_stmt|;
DECL|field|queryProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|InternalAccountQuery
argument_list|>
name|queryProvider
decl_stmt|;
annotation|@
name|Inject
DECL|method|BecomeAnyAccountLoginServlet ( DynamicItem<WebSession> ws, SchemaFactory<ReviewDb> sf, Accounts a, AccountCache ac, AccountManager am, SiteHeaderFooter shf, Provider<InternalAccountQuery> qp)
name|BecomeAnyAccountLoginServlet
parameter_list|(
name|DynamicItem
argument_list|<
name|WebSession
argument_list|>
name|ws
parameter_list|,
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|sf
parameter_list|,
name|Accounts
name|a
parameter_list|,
name|AccountCache
name|ac
parameter_list|,
name|AccountManager
name|am
parameter_list|,
name|SiteHeaderFooter
name|shf
parameter_list|,
name|Provider
argument_list|<
name|InternalAccountQuery
argument_list|>
name|qp
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
name|accounts
operator|=
name|a
expr_stmt|;
name|accountCache
operator|=
name|ac
expr_stmt|;
name|accountManager
operator|=
name|am
expr_stmt|;
name|headers
operator|=
name|shf
expr_stmt|;
name|queryProvider
operator|=
name|qp
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doGet (HttpServletRequest req, HttpServletResponse rsp)
specifier|protected
name|void
name|doGet
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
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
DECL|method|doPost (HttpServletRequest req, HttpServletResponse rsp)
specifier|protected
name|void
name|doPost
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
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
operator|.
name|name
argument_list|()
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
try|try
init|(
name|OutputStream
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
name|raw
argument_list|)
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
name|req
operator|.
name|getContextPath
argument_list|()
argument_list|)
expr_stmt|;
name|rdr
operator|.
name|append
argument_list|(
literal|"/"
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
literal|'#'
operator|+
name|PageLinks
operator|.
name|REGISTER
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rdr
operator|.
name|append
argument_list|(
name|LoginUrlToken
operator|.
name|getToken
argument_list|(
name|req
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|Writer
name|out
init|=
name|rsp
operator|.
name|getWriter
argument_list|()
init|)
block|{
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
block|}
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
try|try
init|(
name|ReviewDb
name|db
init|=
name|schema
operator|.
name|open
argument_list|()
init|)
block|{
for|for
control|(
name|Account
operator|.
name|Id
name|accountId
range|:
name|accounts
operator|.
name|firstNIds
argument_list|(
literal|100
argument_list|)
control|)
block|{
name|Account
name|a
init|=
name|accountCache
operator|.
name|get
argument_list|(
name|accountId
argument_list|)
operator|.
name|getAccount
argument_list|()
decl_stmt|;
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
name|accountId
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
return|return
name|HtmlDomUtil
operator|.
name|toUTF8
argument_list|(
name|doc
argument_list|)
return|;
block|}
DECL|method|auth (Account account)
specifier|private
name|AuthResult
name|auth
parameter_list|(
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
DECL|method|auth (Account.Id account)
specifier|private
name|AuthResult
name|auth
parameter_list|(
name|Account
operator|.
name|Id
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
DECL|method|byUserName (String userName)
specifier|private
name|AuthResult
name|byUserName
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
try|try
block|{
name|List
argument_list|<
name|AccountState
argument_list|>
name|accountStates
init|=
name|queryProvider
operator|.
name|get
argument_list|()
operator|.
name|byExternalId
argument_list|(
name|SCHEME_USERNAME
argument_list|,
name|userName
argument_list|)
decl_stmt|;
if|if
condition|(
name|accountStates
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|getServletContext
argument_list|()
operator|.
name|log
argument_list|(
literal|"No accounts with username "
operator|+
name|userName
operator|+
literal|" found"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
name|accountStates
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|getServletContext
argument_list|()
operator|.
name|log
argument_list|(
literal|"Multiple accounts with username "
operator|+
name|userName
operator|+
literal|" found"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|auth
argument_list|(
name|accountStates
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getAccount
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
return|;
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
literal|"cannot query account index"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|byPreferredEmail (String email)
specifier|private
name|AuthResult
name|byPreferredEmail
parameter_list|(
name|String
name|email
parameter_list|)
block|{
try|try
init|(
name|ReviewDb
name|db
init|=
name|schema
operator|.
name|open
argument_list|()
init|)
block|{
name|Optional
argument_list|<
name|Account
argument_list|>
name|match
init|=
name|queryProvider
operator|.
name|get
argument_list|()
operator|.
name|byPreferredEmail
argument_list|(
name|email
argument_list|)
operator|.
name|stream
argument_list|()
comment|// the index query also matches prefixes, filter those out
operator|.
name|filter
argument_list|(
name|a
lambda|->
name|email
operator|.
name|equalsIgnoreCase
argument_list|(
name|a
operator|.
name|getAccount
argument_list|()
operator|.
name|getPreferredEmail
argument_list|()
argument_list|)
argument_list|)
operator|.
name|map
argument_list|(
name|AccountState
operator|::
name|getAccount
argument_list|)
operator|.
name|findFirst
argument_list|()
decl_stmt|;
return|return
name|match
operator|.
name|isPresent
argument_list|()
condition|?
name|auth
argument_list|(
name|match
operator|.
name|get
argument_list|()
argument_list|)
else|:
literal|null
return|;
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
DECL|method|byAccountId (String idStr)
specifier|private
name|AuthResult
name|byAccountId
parameter_list|(
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
init|(
name|ReviewDb
name|db
init|=
name|schema
operator|.
name|open
argument_list|()
init|)
block|{
return|return
name|auth
argument_list|(
name|accounts
operator|.
name|get
argument_list|(
name|db
argument_list|,
name|id
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|IOException
decl||
name|ConfigInvalidException
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
throws|throws
name|IOException
block|{
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
name|ExternalId
operator|.
name|Key
operator|.
name|create
argument_list|(
name|SCHEME_UUID
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
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

