begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
name|server
operator|.
name|config
operator|.
name|SitePath
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|user
operator|.
name|server
operator|.
name|rpc
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
name|org
operator|.
name|spearce
operator|.
name|jgit
operator|.
name|util
operator|.
name|NB
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|GZIPOutputStream
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
comment|/** Sends static content from the site 's<code>static/</code> subdirectory. */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
annotation|@
name|Singleton
DECL|class|StaticServlet
specifier|public
class|class
name|StaticServlet
extends|extends
name|HttpServlet
block|{
DECL|field|MAX_AGE
specifier|private
specifier|static
specifier|final
name|long
name|MAX_AGE
init|=
literal|12
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000L
comment|/* milliseconds */
decl_stmt|;
DECL|field|CACHE_CTRL
specifier|private
specifier|static
specifier|final
name|String
name|CACHE_CTRL
init|=
literal|"public, max-age="
operator|+
operator|(
name|MAX_AGE
operator|/
literal|1000L
operator|)
decl_stmt|;
DECL|field|MIME_TYPES
specifier|private
specifier|static
specifier|final
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|MIME_TYPES
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|MIME_TYPES
operator|.
name|put
argument_list|(
literal|"html"
argument_list|,
literal|"text/html"
argument_list|)
expr_stmt|;
name|MIME_TYPES
operator|.
name|put
argument_list|(
literal|"htm"
argument_list|,
literal|"text/html"
argument_list|)
expr_stmt|;
name|MIME_TYPES
operator|.
name|put
argument_list|(
literal|"js"
argument_list|,
literal|"application/x-javascript"
argument_list|)
expr_stmt|;
name|MIME_TYPES
operator|.
name|put
argument_list|(
literal|"css"
argument_list|,
literal|"text/css"
argument_list|)
expr_stmt|;
name|MIME_TYPES
operator|.
name|put
argument_list|(
literal|"rtf"
argument_list|,
literal|"text/rtf"
argument_list|)
expr_stmt|;
name|MIME_TYPES
operator|.
name|put
argument_list|(
literal|"txt"
argument_list|,
literal|"text/plain"
argument_list|)
expr_stmt|;
name|MIME_TYPES
operator|.
name|put
argument_list|(
literal|"text"
argument_list|,
literal|"text/plain"
argument_list|)
expr_stmt|;
name|MIME_TYPES
operator|.
name|put
argument_list|(
literal|"pdf"
argument_list|,
literal|"application/pdf"
argument_list|)
expr_stmt|;
name|MIME_TYPES
operator|.
name|put
argument_list|(
literal|"jpeg"
argument_list|,
literal|"image/jpeg"
argument_list|)
expr_stmt|;
name|MIME_TYPES
operator|.
name|put
argument_list|(
literal|"jpg"
argument_list|,
literal|"image/jpeg"
argument_list|)
expr_stmt|;
name|MIME_TYPES
operator|.
name|put
argument_list|(
literal|"gif"
argument_list|,
literal|"image/gif"
argument_list|)
expr_stmt|;
name|MIME_TYPES
operator|.
name|put
argument_list|(
literal|"png"
argument_list|,
literal|"image/png"
argument_list|)
expr_stmt|;
name|MIME_TYPES
operator|.
name|put
argument_list|(
literal|"tiff"
argument_list|,
literal|"image/tiff"
argument_list|)
expr_stmt|;
name|MIME_TYPES
operator|.
name|put
argument_list|(
literal|"tif"
argument_list|,
literal|"image/tiff"
argument_list|)
expr_stmt|;
name|MIME_TYPES
operator|.
name|put
argument_list|(
literal|"svg"
argument_list|,
literal|"image/svg+xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|contentType (final String name)
specifier|private
specifier|static
name|String
name|contentType
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
specifier|final
name|int
name|dot
init|=
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
specifier|final
name|String
name|ext
init|=
literal|0
operator|<
name|dot
condition|?
name|name
operator|.
name|substring
argument_list|(
name|dot
operator|+
literal|1
argument_list|)
else|:
literal|""
decl_stmt|;
specifier|final
name|String
name|type
init|=
name|MIME_TYPES
operator|.
name|get
argument_list|(
name|ext
argument_list|)
decl_stmt|;
return|return
name|type
operator|!=
literal|null
condition|?
name|type
else|:
literal|"application/octet-stream"
return|;
block|}
DECL|method|readFile (final File p)
specifier|private
specifier|static
name|byte
index|[]
name|readFile
parameter_list|(
specifier|final
name|File
name|p
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FileInputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|p
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|byte
index|[]
name|r
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|in
operator|.
name|getChannel
argument_list|()
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|NB
operator|.
name|readFully
argument_list|(
name|in
argument_list|,
name|r
argument_list|,
literal|0
argument_list|,
name|r
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|compress (final byte[] raw)
specifier|private
specifier|static
name|byte
index|[]
name|compress
parameter_list|(
specifier|final
name|byte
index|[]
name|raw
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
specifier|final
name|GZIPOutputStream
name|gz
init|=
operator|new
name|GZIPOutputStream
argument_list|(
name|out
argument_list|)
decl_stmt|;
name|gz
operator|.
name|write
argument_list|(
name|raw
argument_list|)
expr_stmt|;
name|gz
operator|.
name|finish
argument_list|()
expr_stmt|;
name|gz
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|out
operator|.
name|toByteArray
argument_list|()
return|;
block|}
DECL|field|staticBase
specifier|private
specifier|final
name|File
name|staticBase
decl_stmt|;
annotation|@
name|Inject
DECL|method|StaticServlet (@itePath final File sitePath)
name|StaticServlet
parameter_list|(
annotation|@
name|SitePath
specifier|final
name|File
name|sitePath
parameter_list|)
block|{
name|staticBase
operator|=
operator|new
name|File
argument_list|(
name|sitePath
argument_list|,
literal|"static"
argument_list|)
expr_stmt|;
block|}
DECL|method|local (final HttpServletRequest req)
specifier|private
name|File
name|local
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|)
block|{
specifier|final
name|String
name|name
init|=
name|req
operator|.
name|getPathInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|length
argument_list|()
operator|<
literal|2
operator|||
operator|!
name|name
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
comment|// Too short to be a valid file name, or doesn't start with
comment|// the path info separator like we expected.
comment|//
return|return
literal|null
return|;
block|}
if|if
condition|(
name|name
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|,
literal|1
argument_list|)
operator|>
literal|0
operator|||
name|name
operator|.
name|indexOf
argument_list|(
literal|'\\'
argument_list|,
literal|1
argument_list|)
operator|>
literal|0
condition|)
block|{
comment|// Contains a path separator. Don't serve it as the client
comment|// might be trying something evil like "/../../etc/passwd".
comment|// This static servlet is just meant to facilitate simple
comment|// assets like banner images.
comment|//
return|return
literal|null
return|;
block|}
specifier|final
name|File
name|p
init|=
operator|new
name|File
argument_list|(
name|staticBase
argument_list|,
name|name
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|p
operator|.
name|isFile
argument_list|()
condition|?
name|p
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getLastModified (final HttpServletRequest req)
specifier|protected
name|long
name|getLastModified
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|)
block|{
specifier|final
name|File
name|p
init|=
name|local
argument_list|(
name|req
argument_list|)
decl_stmt|;
return|return
name|p
operator|!=
literal|null
condition|?
name|p
operator|.
name|lastModified
argument_list|()
else|:
operator|-
literal|1
return|;
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
name|File
name|p
init|=
name|local
argument_list|(
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
name|rsp
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|String
name|type
init|=
name|contentType
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|tosend
decl_stmt|;
if|if
condition|(
operator|!
name|type
operator|.
name|equals
argument_list|(
literal|"application/x-javascript"
argument_list|)
operator|&&
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
name|tosend
operator|=
name|compress
argument_list|(
name|readFile
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tosend
operator|=
name|readFile
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|setHeader
argument_list|(
literal|"Cache-Control"
argument_list|,
name|CACHE_CTRL
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setDateHeader
argument_list|(
literal|"Expires"
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|MAX_AGE
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setDateHeader
argument_list|(
literal|"Last-Modified"
argument_list|,
name|p
operator|.
name|lastModified
argument_list|()
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setContentType
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setContentLength
argument_list|(
name|tosend
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
name|tosend
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
end_class

end_unit

