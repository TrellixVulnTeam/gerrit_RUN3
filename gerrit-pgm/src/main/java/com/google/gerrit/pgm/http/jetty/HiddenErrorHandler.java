begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
DECL|package|com.google.gerrit.pgm.http.jetty
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|http
operator|.
name|jetty
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
name|Charsets
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
name|gwtexpui
operator|.
name|server
operator|.
name|CacheHeaders
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|http
operator|.
name|HttpHeaders
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|http
operator|.
name|HttpStatus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|AbstractHttpConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Request
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|handler
operator|.
name|ErrorHandler
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
DECL|class|HiddenErrorHandler
class|class
name|HiddenErrorHandler
extends|extends
name|ErrorHandler
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
name|HiddenErrorHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|handle (String target, Request baseRequest, HttpServletRequest req, HttpServletResponse res)
specifier|public
name|void
name|handle
parameter_list|(
name|String
name|target
parameter_list|,
name|Request
name|baseRequest
parameter_list|,
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|)
throws|throws
name|IOException
block|{
name|AbstractHttpConnection
name|conn
init|=
name|AbstractHttpConnection
operator|.
name|getCurrentConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|getRequest
argument_list|()
operator|.
name|setHandled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
try|try
block|{
name|log
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|reply
argument_list|(
name|conn
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|reply (AbstractHttpConnection conn, HttpServletResponse res)
specifier|private
name|void
name|reply
parameter_list|(
name|AbstractHttpConnection
name|conn
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|msg
init|=
name|message
argument_list|(
name|conn
argument_list|)
decl_stmt|;
name|res
operator|.
name|setHeader
argument_list|(
name|HttpHeaders
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"text/plain; charset=ISO-8859-1"
argument_list|)
expr_stmt|;
name|res
operator|.
name|setContentLength
argument_list|(
name|msg
operator|.
name|length
argument_list|)
expr_stmt|;
try|try
block|{
name|CacheHeaders
operator|.
name|setNotCacheable
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
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
name|msg
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
block|}
DECL|method|message (AbstractHttpConnection conn)
specifier|private
specifier|static
name|byte
index|[]
name|message
parameter_list|(
name|AbstractHttpConnection
name|conn
parameter_list|)
block|{
name|String
name|msg
init|=
name|conn
operator|.
name|getResponse
argument_list|()
operator|.
name|getReason
argument_list|()
decl_stmt|;
if|if
condition|(
name|msg
operator|==
literal|null
condition|)
block|{
name|msg
operator|=
name|HttpStatus
operator|.
name|getMessage
argument_list|(
name|conn
operator|.
name|getResponse
argument_list|()
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|msg
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|ISO_8859_1
argument_list|)
return|;
block|}
DECL|method|log (HttpServletRequest req)
specifier|private
specifier|static
name|void
name|log
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
name|Throwable
name|err
init|=
operator|(
name|Throwable
operator|)
name|req
operator|.
name|getAttribute
argument_list|(
literal|"javax.servlet.error.exception"
argument_list|)
decl_stmt|;
if|if
condition|(
name|err
operator|!=
literal|null
condition|)
block|{
name|String
name|uri
init|=
name|req
operator|.
name|getRequestURI
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|req
operator|.
name|getQueryString
argument_list|()
argument_list|)
condition|)
block|{
name|uri
operator|+=
literal|"?"
operator|+
name|req
operator|.
name|getQueryString
argument_list|()
expr_stmt|;
block|}
name|log
operator|.
name|error
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Error in %s %s"
argument_list|,
name|req
operator|.
name|getMethod
argument_list|()
argument_list|,
name|uri
argument_list|)
argument_list|,
name|err
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

