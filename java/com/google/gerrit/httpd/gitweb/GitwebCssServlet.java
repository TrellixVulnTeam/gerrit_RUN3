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
DECL|package|com.google.gerrit.httpd.gitweb
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
operator|.
name|gitweb
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
name|common
operator|.
name|FileUtil
operator|.
name|lastModified
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
name|server
operator|.
name|config
operator|.
name|GitwebCgiConfig
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
name|gerrit
operator|.
name|util
operator|.
name|http
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
name|gwtjsonrpc
operator|.
name|server
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
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|class|GitwebCssServlet
specifier|abstract
class|class
name|GitwebCssServlet
extends|extends
name|HttpServlet
block|{
annotation|@
name|Singleton
DECL|class|Site
specifier|static
class|class
name|Site
extends|extends
name|GitwebCssServlet
block|{
annotation|@
name|Inject
DECL|method|Site (SitePaths paths)
name|Site
parameter_list|(
name|SitePaths
name|paths
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|paths
operator|.
name|site_css
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Singleton
DECL|class|Default
specifier|static
class|class
name|Default
extends|extends
name|GitwebCssServlet
block|{
annotation|@
name|Inject
DECL|method|Default (GitwebCgiConfig gwcc)
name|Default
parameter_list|(
name|GitwebCgiConfig
name|gwcc
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|gwcc
operator|.
name|getGitwebCss
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|modified
specifier|private
specifier|final
name|long
name|modified
decl_stmt|;
DECL|field|raw_css
specifier|private
specifier|final
name|byte
index|[]
name|raw_css
decl_stmt|;
DECL|field|gz_css
specifier|private
specifier|final
name|byte
index|[]
name|gz_css
decl_stmt|;
DECL|method|GitwebCssServlet (Path src)
name|GitwebCssServlet
parameter_list|(
name|Path
name|src
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|src
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Path
name|dir
init|=
name|src
operator|.
name|getParent
argument_list|()
decl_stmt|;
specifier|final
name|String
name|name
init|=
name|src
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|String
name|raw
init|=
name|HtmlDomUtil
operator|.
name|readFile
argument_list|(
name|dir
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|raw
operator|!=
literal|null
condition|)
block|{
name|modified
operator|=
name|lastModified
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|raw_css
operator|=
name|raw
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
expr_stmt|;
name|gz_css
operator|=
name|HtmlDomUtil
operator|.
name|compress
argument_list|(
name|raw_css
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|modified
operator|=
operator|-
literal|1L
expr_stmt|;
name|raw_css
operator|=
literal|null
expr_stmt|;
name|gz_css
operator|=
literal|null
expr_stmt|;
block|}
block|}
else|else
block|{
name|modified
operator|=
operator|-
literal|1
expr_stmt|;
name|raw_css
operator|=
literal|null
expr_stmt|;
name|gz_css
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLastModified (HttpServletRequest req)
specifier|protected
name|long
name|getLastModified
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
return|return
name|modified
return|;
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
block|{
if|if
condition|(
name|raw_css
operator|!=
literal|null
condition|)
block|{
name|rsp
operator|.
name|setContentType
argument_list|(
literal|"text/css"
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
specifier|final
name|byte
index|[]
name|toSend
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
name|toSend
operator|=
name|gz_css
expr_stmt|;
block|}
else|else
block|{
name|toSend
operator|=
name|raw_css
expr_stmt|;
block|}
name|rsp
operator|.
name|setContentLength
argument_list|(
name|toSend
operator|.
name|length
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setDateHeader
argument_list|(
literal|"Last-Modified"
argument_list|,
name|modified
argument_list|)
expr_stmt|;
name|CacheHeaders
operator|.
name|setCacheable
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
try|try
init|(
name|ServletOutputStream
name|os
init|=
name|rsp
operator|.
name|getOutputStream
argument_list|()
init|)
block|{
name|os
operator|.
name|write
argument_list|(
name|toSend
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|CacheHeaders
operator|.
name|setNotCacheable
argument_list|(
name|rsp
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

