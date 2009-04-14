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
name|client
operator|.
name|rpc
operator|.
name|Common
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

begin_class
DECL|class|BecomeAnyAccountLoginServlet
specifier|public
class|class
name|BecomeAnyAccountLoginServlet
extends|extends
name|HttpServlet
block|{
DECL|field|allowed
specifier|private
name|boolean
name|allowed
decl_stmt|;
DECL|field|server
specifier|private
name|GerritServer
name|server
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
name|allowed
operator|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|se
parameter_list|)
block|{
name|allowed
operator|=
literal|false
expr_stmt|;
block|}
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
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Cannot load GerritServer"
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
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Cannot load GerritServer"
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
if|if
condition|(
operator|!
name|allowed
condition|)
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
return|return;
block|}
name|rsp
operator|.
name|setContentType
argument_list|(
literal|"text/html"
argument_list|)
expr_stmt|;
specifier|final
name|ServletOutputStream
name|out
init|=
name|rsp
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<html>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<form method=\"POST\"><b>ssh_user_name:</b> "
operator|+
literal|"<input type=\"text\" size=\"30\" name=\"ssh_user_name\" />"
operator|+
literal|"<input type=\"submit\" value=\"Become Account\" />"
operator|+
literal|"</form>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<form method=\"POST\"><b>preferred_email:</b> "
operator|+
literal|"<input type=\"text\" size=\"30\" name=\"preferred_email\" />"
operator|+
literal|"<input type=\"submit\" value=\"Become Account\" />"
operator|+
literal|"</form>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<form method=\"POST\"><b>account_id:</b> "
operator|+
literal|"<input type=\"text\" size=\"12\" name=\"account_id\" />"
operator|+
literal|"<input type=\"submit\" value=\"Become Account\" />"
operator|+
literal|"</form>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"</html>"
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
if|if
condition|(
operator|!
name|allowed
condition|)
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
return|return;
block|}
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
name|doGet
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
literal|""
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
operator|new
name|AccountCookie
argument_list|(
name|account
operator|.
name|getId
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|set
argument_list|(
name|c
argument_list|,
name|server
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|addCookie
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|sendRedirect
argument_list|(
literal|"Gerrit.html"
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
name|Common
operator|.
name|getSchemaFactory
argument_list|()
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
name|bySshUserName
argument_list|(
name|userName
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
name|Common
operator|.
name|getSchemaFactory
argument_list|()
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
name|Common
operator|.
name|getSchemaFactory
argument_list|()
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

