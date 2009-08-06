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
DECL|package|com.google.gerrit.server.http
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|http
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
name|Nullable
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
name|google
operator|.
name|gwtorm
operator|.
name|client
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
name|List
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
specifier|public
class|class
name|BecomeAnyAccountLoginServlet
extends|extends
name|HttpServlet
block|{
DECL|field|schema
specifier|private
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
decl_stmt|;
DECL|field|callFactory
specifier|private
specifier|final
name|Provider
argument_list|<
name|GerritCall
argument_list|>
name|callFactory
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
DECL|field|raw
specifier|private
specifier|final
name|byte
index|[]
name|raw
decl_stmt|;
annotation|@
name|Inject
DECL|method|BecomeAnyAccountLoginServlet (final Provider<GerritCall> cf, final SchemaFactory<ReviewDb> sf, final @CanonicalWebUrl @Nullable Provider<String> up, final ServletContext servletContext)
name|BecomeAnyAccountLoginServlet
parameter_list|(
specifier|final
name|Provider
argument_list|<
name|GerritCall
argument_list|>
name|cf
parameter_list|,
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|sf
parameter_list|,
specifier|final
annotation|@
name|CanonicalWebUrl
annotation|@
name|Nullable
name|Provider
argument_list|<
name|String
argument_list|>
name|up
parameter_list|,
specifier|final
name|ServletContext
name|servletContext
parameter_list|)
throws|throws
name|IOException
block|{
name|callFactory
operator|=
name|cf
expr_stmt|;
name|schema
operator|=
name|sf
expr_stmt|;
name|urlProvider
operator|=
name|up
expr_stmt|;
specifier|final
name|String
name|hostPageName
init|=
literal|"WEB-INF/BecomeAnyAccount.html"
decl_stmt|;
specifier|final
name|String
name|doc
init|=
name|HtmlDomUtil
operator|.
name|readFile
argument_list|(
name|servletContext
argument_list|,
literal|"/"
operator|+
name|hostPageName
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
name|hostPageName
operator|+
literal|" in webapp"
argument_list|)
throw|;
block|}
name|raw
operator|=
name|doc
operator|.
name|getBytes
argument_list|(
name|HtmlDomUtil
operator|.
name|ENC
argument_list|)
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
block|{
specifier|final
name|List
argument_list|<
name|Account
argument_list|>
name|accounts
decl_stmt|;
if|if
condition|(
name|req
operator|.
name|getParameter
argument_list|(
literal|"ssh_user_name"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|accounts
operator|=
name|bySshUserName
argument_list|(
name|rsp
argument_list|,
name|req
operator|.
name|getParameter
argument_list|(
literal|"ssh_user_name"
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
name|accounts
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
name|accounts
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
name|accounts
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
specifier|final
name|Account
name|account
init|=
name|accounts
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|GerritCall
name|call
init|=
name|callFactory
operator|.
name|get
argument_list|()
decl_stmt|;
name|call
operator|.
name|noCache
argument_list|()
expr_stmt|;
name|call
operator|.
name|setAccount
argument_list|(
name|account
operator|.
name|getId
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|sendRedirect
argument_list|(
name|urlProvider
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rsp
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|bySshUserName (final HttpServletResponse rsp, final String userName)
specifier|private
name|List
argument_list|<
name|Account
argument_list|>
name|bySshUserName
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
specifier|final
name|Account
name|account
init|=
name|db
operator|.
name|accounts
argument_list|()
operator|.
name|bySshUserName
argument_list|(
name|userName
argument_list|)
decl_stmt|;
return|return
name|account
operator|!=
literal|null
condition|?
name|Collections
operator|.
expr|<
name|Account
operator|>
name|singletonList
argument_list|(
name|account
argument_list|)
else|:
name|Collections
operator|.
expr|<
name|Account
operator|>
name|emptyList
argument_list|()
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
name|Collections
operator|.
expr|<
name|Account
operator|>
name|emptyList
argument_list|()
return|;
block|}
block|}
DECL|method|byPreferredEmail (final HttpServletResponse rsp, final String email)
specifier|private
name|List
argument_list|<
name|Account
argument_list|>
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
return|return
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
name|Collections
operator|.
expr|<
name|Account
operator|>
name|emptyList
argument_list|()
return|;
block|}
block|}
DECL|method|byAccountId (final HttpServletResponse rsp, final String idStr)
specifier|private
name|List
argument_list|<
name|Account
argument_list|>
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
name|Collections
operator|.
expr|<
name|Account
operator|>
name|emptyList
argument_list|()
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
specifier|final
name|Account
name|account
init|=
name|db
operator|.
name|accounts
argument_list|()
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
return|return
name|account
operator|!=
literal|null
condition|?
name|Collections
operator|.
expr|<
name|Account
operator|>
name|singletonList
argument_list|(
name|account
argument_list|)
else|:
name|Collections
operator|.
expr|<
name|Account
operator|>
name|emptyList
argument_list|()
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
name|Collections
operator|.
expr|<
name|Account
operator|>
name|emptyList
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

