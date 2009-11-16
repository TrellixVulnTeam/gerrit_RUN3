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
DECL|package|com.google.gerrit.httpd.raw
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
operator|.
name|raw
package|;
end_package

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
name|InputStream
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
name|Singleton
DECL|class|PrettifyServlet
specifier|public
class|class
name|PrettifyServlet
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
DECL|field|VERSION
specifier|private
specifier|static
specifier|final
name|String
name|VERSION
init|=
literal|"20090521"
decl_stmt|;
DECL|field|content
specifier|private
specifier|final
name|byte
index|[]
name|content
decl_stmt|;
annotation|@
name|Inject
DECL|method|PrettifyServlet (final ServletContext servletContext)
name|PrettifyServlet
parameter_list|(
specifier|final
name|ServletContext
name|servletContext
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|myDir
init|=
literal|"/gerrit/prettify"
operator|+
name|VERSION
operator|+
literal|"/"
decl_stmt|;
specifier|final
name|ByteArrayOutputStream
name|buffer
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|load
argument_list|(
name|buffer
argument_list|,
name|servletContext
argument_list|,
name|myDir
operator|+
literal|"prettify.js"
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|p
range|:
name|servletContext
operator|.
name|getResourcePaths
argument_list|(
name|myDir
argument_list|)
control|)
block|{
name|String
name|name
init|=
operator|(
name|String
operator|)
name|p
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
name|myDir
operator|+
literal|"lang-"
argument_list|)
operator|&&
name|name
operator|.
name|endsWith
argument_list|(
literal|".js"
argument_list|)
condition|)
block|{
name|load
argument_list|(
name|buffer
argument_list|,
name|servletContext
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
name|content
operator|=
name|buffer
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
block|}
DECL|method|load (final OutputStream buffer, final ServletContext servletContext, final String path)
specifier|private
name|void
name|load
parameter_list|(
specifier|final
name|OutputStream
name|buffer
parameter_list|,
specifier|final
name|ServletContext
name|servletContext
parameter_list|,
specifier|final
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|InputStream
name|in
init|=
name|servletContext
operator|.
name|getResourceAsStream
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|in
operator|!=
literal|null
condition|)
block|{
try|try
block|{
specifier|final
name|byte
index|[]
name|tmp
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
name|int
name|cnt
decl_stmt|;
while|while
condition|(
operator|(
name|cnt
operator|=
name|in
operator|.
name|read
argument_list|(
name|tmp
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|buffer
operator|.
name|write
argument_list|(
name|tmp
argument_list|,
literal|0
argument_list|,
name|cnt
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|write
argument_list|(
literal|';'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|write
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
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
name|IOException
argument_list|(
literal|"Cannot read "
operator|+
name|path
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
specifier|final
name|String
name|want
init|=
name|req
operator|.
name|getPathInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|want
operator|.
name|equals
argument_list|(
literal|"/"
operator|+
name|VERSION
operator|+
literal|".js"
argument_list|)
condition|)
block|{
specifier|final
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|rsp
operator|.
name|setHeader
argument_list|(
literal|"Cache-Control"
argument_list|,
literal|"max-age=31536000,public"
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setDateHeader
argument_list|(
literal|"Expires"
argument_list|,
name|now
operator|+
literal|31536000000L
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setDateHeader
argument_list|(
literal|"Date"
argument_list|,
name|now
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setContentType
argument_list|(
literal|"application/x-javascript"
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setContentLength
argument_list|(
name|content
operator|.
name|length
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|getOutputStream
argument_list|()
operator|.
name|write
argument_list|(
name|content
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rsp
operator|.
name|setHeader
argument_list|(
literal|"Expires"
argument_list|,
literal|"Fri, 01 Jan 1980 00:00:00 GMT"
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setHeader
argument_list|(
literal|"Pragma"
argument_list|,
literal|"no-cache"
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setHeader
argument_list|(
literal|"Cache-Control"
argument_list|,
literal|"no-cache, must-revalidate"
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setDateHeader
argument_list|(
literal|"Date"
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
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
block|}
end_class

end_unit

