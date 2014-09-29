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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|net
operator|.
name|HttpHeaders
operator|.
name|CONTENT_ENCODING
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|net
operator|.
name|HttpHeaders
operator|.
name|ETAG
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|net
operator|.
name|HttpHeaders
operator|.
name|IF_NONE_MATCH
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|DAYS
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MINUTES
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
operator|.
name|SC_NOT_MODIFIED
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
name|CharMatcher
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
name|cache
operator|.
name|CacheBuilder
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
name|cache
operator|.
name|CacheLoader
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
name|cache
operator|.
name|LoadingCache
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
name|cache
operator|.
name|Weigher
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
name|collect
operator|.
name|Maps
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
name|hash
operator|.
name|Hashing
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
name|io
operator|.
name|ByteStreams
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
name|Nullable
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
name|GerritServerConfig
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
name|Singleton
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
name|lib
operator|.
name|Config
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
name|Map
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
name|ExecutionException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|inject
operator|.
name|Inject
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
comment|/** Sends static content from the site 's {@code static/} subdirectory. */
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
name|StaticServlet
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|JS
specifier|private
specifier|static
specifier|final
name|String
name|JS
init|=
literal|"application/x-javascript"
decl_stmt|;
DECL|field|MIME_TYPES
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|MIME_TYPES
init|=
name|Maps
operator|.
name|newHashMap
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
name|JS
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
DECL|field|staticBase
specifier|private
specifier|final
name|File
name|staticBase
decl_stmt|;
DECL|field|staticBasePath
specifier|private
specifier|final
name|String
name|staticBasePath
decl_stmt|;
DECL|field|refresh
specifier|private
specifier|final
name|boolean
name|refresh
decl_stmt|;
DECL|field|cache
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
name|cache
decl_stmt|;
annotation|@
name|Inject
DECL|method|StaticServlet (@erritServerConfig Config cfg, SitePaths site)
name|StaticServlet
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|,
name|SitePaths
name|site
parameter_list|)
block|{
name|File
name|f
decl_stmt|;
try|try
block|{
name|f
operator|=
name|site
operator|.
name|static_dir
operator|.
name|getCanonicalFile
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|f
operator|=
name|site
operator|.
name|static_dir
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
block|}
name|staticBase
operator|=
name|f
expr_stmt|;
name|staticBasePath
operator|=
name|staticBase
operator|.
name|getPath
argument_list|()
operator|+
name|File
operator|.
name|separator
expr_stmt|;
name|refresh
operator|=
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"site"
argument_list|,
literal|"refreshHeaderFooter"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cache
operator|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|maximumWeight
argument_list|(
literal|1
operator|<<
literal|20
argument_list|)
operator|.
name|weigher
argument_list|(
operator|new
name|Weigher
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|weigh
parameter_list|(
name|String
name|name
parameter_list|,
name|Resource
name|r
parameter_list|)
block|{
return|return
literal|2
operator|*
name|name
operator|.
name|length
argument_list|()
operator|+
name|r
operator|.
name|raw
operator|.
name|length
return|;
block|}
block|}
argument_list|)
operator|.
name|build
argument_list|(
operator|new
name|CacheLoader
argument_list|<
name|String
argument_list|,
name|Resource
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Resource
name|load
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|loadResource
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nullable
DECL|method|getResource (String name)
name|Resource
name|getResource
parameter_list|(
name|String
name|name
parameter_list|)
block|{
try|try
block|{
return|return
name|cache
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Cannot load static resource %s"
argument_list|,
name|name
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|getResource (HttpServletRequest req)
specifier|private
name|Resource
name|getResource
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
throws|throws
name|ExecutionException
block|{
name|String
name|name
init|=
name|CharMatcher
operator|.
name|is
argument_list|(
literal|'/'
argument_list|)
operator|.
name|trimFrom
argument_list|(
name|req
operator|.
name|getPathInfo
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|isUnreasonableName
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|Resource
operator|.
name|NOT_FOUND
return|;
block|}
name|Resource
name|r
init|=
name|cache
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|==
name|Resource
operator|.
name|NOT_FOUND
condition|)
block|{
return|return
name|Resource
operator|.
name|NOT_FOUND
return|;
block|}
if|if
condition|(
name|refresh
operator|&&
name|r
operator|.
name|isStale
argument_list|()
condition|)
block|{
name|cache
operator|.
name|invalidate
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|r
operator|=
name|cache
operator|.
name|get
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
DECL|method|isUnreasonableName (String name)
specifier|private
specifier|static
name|boolean
name|isUnreasonableName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|length
argument_list|()
operator|<
literal|1
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|name
operator|.
name|contains
argument_list|(
literal|"\\"
argument_list|)
condition|)
return|return
literal|true
return|;
comment|// no windows/dos style paths
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"../"
argument_list|)
condition|)
return|return
literal|true
return|;
comment|// no "../etc/passwd"
if|if
condition|(
name|name
operator|.
name|contains
argument_list|(
literal|"/../"
argument_list|)
condition|)
return|return
literal|true
return|;
comment|// no "foo/../etc/passwd"
if|if
condition|(
name|name
operator|.
name|contains
argument_list|(
literal|"/./"
argument_list|)
condition|)
return|return
literal|true
return|;
comment|// "foo/./foo" is insane to ask
if|if
condition|(
name|name
operator|.
name|contains
argument_list|(
literal|"//"
argument_list|)
condition|)
return|return
literal|true
return|;
comment|// windows UNC path can be "//..."
return|return
literal|false
return|;
comment|// is a reasonable name
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
name|Resource
name|r
decl_stmt|;
try|try
block|{
name|r
operator|=
name|getResource
argument_list|(
name|req
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Cannot load static resource %s"
argument_list|,
name|req
operator|.
name|getPathInfo
argument_list|()
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|CacheHeaders
operator|.
name|setNotCacheable
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setStatus
argument_list|(
name|SC_INTERNAL_SERVER_ERROR
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|e
init|=
name|req
operator|.
name|getParameter
argument_list|(
literal|"e"
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|==
name|Resource
operator|.
name|NOT_FOUND
operator|||
operator|(
name|e
operator|!=
literal|null
operator|&&
operator|!
name|r
operator|.
name|etag
operator|.
name|equals
argument_list|(
name|e
argument_list|)
operator|)
condition|)
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
name|setStatus
argument_list|(
name|SC_NOT_FOUND
argument_list|)
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|r
operator|.
name|etag
operator|.
name|equals
argument_list|(
name|req
operator|.
name|getHeader
argument_list|(
name|IF_NONE_MATCH
argument_list|)
argument_list|)
condition|)
block|{
name|rsp
operator|.
name|setStatus
argument_list|(
name|SC_NOT_MODIFIED
argument_list|)
expr_stmt|;
return|return;
block|}
name|byte
index|[]
name|tosend
init|=
name|r
operator|.
name|raw
decl_stmt|;
if|if
condition|(
operator|!
name|r
operator|.
name|contentType
operator|.
name|equals
argument_list|(
name|JS
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
name|byte
index|[]
name|gz
init|=
name|HtmlDomUtil
operator|.
name|compress
argument_list|(
name|tosend
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|gz
operator|.
name|length
operator|+
literal|24
operator|)
operator|<
name|tosend
operator|.
name|length
condition|)
block|{
name|rsp
operator|.
name|setHeader
argument_list|(
name|CONTENT_ENCODING
argument_list|,
literal|"gzip"
argument_list|)
expr_stmt|;
name|tosend
operator|=
name|gz
expr_stmt|;
block|}
block|}
if|if
condition|(
name|e
operator|!=
literal|null
operator|&&
name|r
operator|.
name|etag
operator|.
name|equals
argument_list|(
name|e
argument_list|)
condition|)
block|{
name|CacheHeaders
operator|.
name|setCacheable
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
literal|360
argument_list|,
name|DAYS
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|CacheHeaders
operator|.
name|setCacheable
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|,
literal|15
argument_list|,
name|MINUTES
argument_list|,
name|refresh
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|setHeader
argument_list|(
name|ETAG
argument_list|,
name|r
operator|.
name|etag
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setContentType
argument_list|(
name|r
operator|.
name|contentType
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
DECL|method|loadResource (String name)
specifier|private
name|Resource
name|loadResource
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|p
init|=
operator|new
name|File
argument_list|(
name|staticBase
argument_list|,
name|name
argument_list|)
decl_stmt|;
try|try
block|{
name|p
operator|=
name|p
operator|.
name|getCanonicalFile
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
name|Resource
operator|.
name|NOT_FOUND
return|;
block|}
if|if
condition|(
operator|!
name|p
operator|.
name|getPath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|staticBasePath
argument_list|)
condition|)
block|{
return|return
name|Resource
operator|.
name|NOT_FOUND
return|;
block|}
name|long
name|ts
init|=
name|p
operator|.
name|lastModified
argument_list|()
decl_stmt|;
name|FileInputStream
name|in
decl_stmt|;
try|try
block|{
name|in
operator|=
operator|new
name|FileInputStream
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
return|return
name|Resource
operator|.
name|NOT_FOUND
return|;
block|}
name|byte
index|[]
name|raw
decl_stmt|;
try|try
block|{
name|raw
operator|=
name|ByteStreams
operator|.
name|toByteArray
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|Resource
argument_list|(
name|p
argument_list|,
name|ts
argument_list|,
name|contentType
argument_list|(
name|name
argument_list|)
argument_list|,
name|raw
argument_list|)
return|;
block|}
DECL|class|Resource
specifier|static
class|class
name|Resource
block|{
DECL|field|NOT_FOUND
specifier|static
specifier|final
name|Resource
name|NOT_FOUND
init|=
operator|new
name|Resource
argument_list|(
literal|null
argument_list|,
operator|-
literal|1
argument_list|,
literal|""
argument_list|,
operator|new
name|byte
index|[]
block|{}
argument_list|)
decl_stmt|;
DECL|field|src
specifier|final
name|File
name|src
decl_stmt|;
DECL|field|lastModified
specifier|final
name|long
name|lastModified
decl_stmt|;
DECL|field|contentType
specifier|final
name|String
name|contentType
decl_stmt|;
DECL|field|etag
specifier|final
name|String
name|etag
decl_stmt|;
DECL|field|raw
specifier|final
name|byte
index|[]
name|raw
decl_stmt|;
DECL|method|Resource (File src, long lastModified, String contentType, byte[] raw)
name|Resource
parameter_list|(
name|File
name|src
parameter_list|,
name|long
name|lastModified
parameter_list|,
name|String
name|contentType
parameter_list|,
name|byte
index|[]
name|raw
parameter_list|)
block|{
name|this
operator|.
name|src
operator|=
name|src
expr_stmt|;
name|this
operator|.
name|lastModified
operator|=
name|lastModified
expr_stmt|;
name|this
operator|.
name|contentType
operator|=
name|contentType
expr_stmt|;
name|this
operator|.
name|etag
operator|=
name|Hashing
operator|.
name|md5
argument_list|()
operator|.
name|hashBytes
argument_list|(
name|raw
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|raw
operator|=
name|raw
expr_stmt|;
block|}
DECL|method|isStale ()
name|boolean
name|isStale
parameter_list|()
block|{
return|return
name|lastModified
operator|!=
name|src
operator|.
name|lastModified
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

