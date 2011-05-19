begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
name|gerrit
operator|.
name|httpd
operator|.
name|HtmlDomUtil
operator|.
name|compress
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
name|httpd
operator|.
name|HtmlDomUtil
operator|.
name|newDocument
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
name|httpd
operator|.
name|HtmlDomUtil
operator|.
name|toUTF8
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|util
operator|.
name|HttpSupport
operator|.
name|HDR_CACHE_CONTROL
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|util
operator|.
name|HttpSupport
operator|.
name|HDR_EXPIRES
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|util
operator|.
name|HttpSupport
operator|.
name|HDR_PRAGMA
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
name|Version
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
name|tools
operator|.
name|ToolsCatalog
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
name|tools
operator|.
name|ToolsCatalog
operator|.
name|Entry
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
comment|/** Sends the client side tools we keep within our software. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|ToolServlet
specifier|public
class|class
name|ToolServlet
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
DECL|field|toc
specifier|private
specifier|final
name|ToolsCatalog
name|toc
decl_stmt|;
annotation|@
name|Inject
DECL|method|ToolServlet (ToolsCatalog toc)
name|ToolServlet
parameter_list|(
name|ToolsCatalog
name|toc
parameter_list|)
block|{
name|this
operator|.
name|toc
operator|=
name|toc
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
block|{
name|Entry
name|ent
init|=
name|toc
operator|.
name|get
argument_list|(
name|req
operator|.
name|getPathInfo
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ent
operator|==
literal|null
condition|)
block|{
name|rsp
operator|.
name|sendError
argument_list|(
name|SC_NOT_FOUND
argument_list|)
expr_stmt|;
return|return;
block|}
switch|switch
condition|(
name|ent
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|FILE
case|:
name|doGetFile
argument_list|(
name|ent
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
case|case
name|DIR
case|:
name|doGetDirectory
argument_list|(
name|ent
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
break|break;
default|default:
name|rsp
operator|.
name|sendError
argument_list|(
name|SC_NOT_FOUND
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
DECL|method|doGetFile (Entry ent, HttpServletRequest req, HttpServletResponse rsp)
specifier|private
name|void
name|doGetFile
parameter_list|(
name|Entry
name|ent
parameter_list|,
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|rsp
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|tosend
init|=
name|ent
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|rsp
operator|.
name|setDateHeader
argument_list|(
name|HDR_EXPIRES
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setHeader
argument_list|(
name|HDR_PRAGMA
argument_list|,
literal|"no-cache"
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setHeader
argument_list|(
name|HDR_CACHE_CONTROL
argument_list|,
literal|"no-cache, must-revalidate"
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setContentType
argument_list|(
literal|"application/octet-stream"
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
DECL|method|doGetDirectory (Entry ent, HttpServletRequest req, HttpServletResponse rsp)
specifier|private
name|void
name|doGetDirectory
parameter_list|(
name|Entry
name|ent
parameter_list|,
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|rsp
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|path
init|=
literal|"/tools/"
operator|+
name|ent
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|Document
name|page
init|=
name|newDocument
argument_list|()
decl_stmt|;
name|Element
name|html
init|=
name|page
operator|.
name|createElement
argument_list|(
literal|"html"
argument_list|)
decl_stmt|;
name|Element
name|head
init|=
name|page
operator|.
name|createElement
argument_list|(
literal|"head"
argument_list|)
decl_stmt|;
name|Element
name|title
init|=
name|page
operator|.
name|createElement
argument_list|(
literal|"title"
argument_list|)
decl_stmt|;
name|Element
name|body
init|=
name|page
operator|.
name|createElement
argument_list|(
literal|"body"
argument_list|)
decl_stmt|;
name|page
operator|.
name|appendChild
argument_list|(
name|html
argument_list|)
expr_stmt|;
name|html
operator|.
name|appendChild
argument_list|(
name|head
argument_list|)
expr_stmt|;
name|html
operator|.
name|appendChild
argument_list|(
name|body
argument_list|)
expr_stmt|;
name|head
operator|.
name|appendChild
argument_list|(
name|title
argument_list|)
expr_stmt|;
name|title
operator|.
name|setTextContent
argument_list|(
literal|"Gerrit Code Review - "
operator|+
name|path
argument_list|)
expr_stmt|;
name|Element
name|h1
init|=
name|page
operator|.
name|createElement
argument_list|(
literal|"h1"
argument_list|)
decl_stmt|;
name|h1
operator|.
name|setTextContent
argument_list|(
name|title
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|body
operator|.
name|appendChild
argument_list|(
name|h1
argument_list|)
expr_stmt|;
name|Element
name|ul
init|=
name|page
operator|.
name|createElement
argument_list|(
literal|"ul"
argument_list|)
decl_stmt|;
name|body
operator|.
name|appendChild
argument_list|(
name|ul
argument_list|)
expr_stmt|;
for|for
control|(
name|Entry
name|e
range|:
name|ent
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|e
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|e
operator|.
name|getType
argument_list|()
operator|==
name|Entry
operator|.
name|Type
operator|.
name|DIR
operator|&&
operator|!
name|name
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|name
operator|+=
literal|"/"
expr_stmt|;
block|}
name|Element
name|li
init|=
name|page
operator|.
name|createElement
argument_list|(
literal|"li"
argument_list|)
decl_stmt|;
name|Element
name|a
init|=
name|page
operator|.
name|createElement
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|a
operator|.
name|setAttribute
argument_list|(
literal|"href"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|a
operator|.
name|setTextContent
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|li
operator|.
name|appendChild
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|ul
operator|.
name|appendChild
argument_list|(
name|li
argument_list|)
expr_stmt|;
block|}
name|body
operator|.
name|appendChild
argument_list|(
name|page
operator|.
name|createElement
argument_list|(
literal|"hr"
argument_list|)
argument_list|)
expr_stmt|;
name|Element
name|footer
init|=
name|page
operator|.
name|createElement
argument_list|(
literal|"p"
argument_list|)
decl_stmt|;
name|footer
operator|.
name|setAttribute
argument_list|(
literal|"style"
argument_list|,
literal|"text-align: right; font-style: italic"
argument_list|)
expr_stmt|;
name|footer
operator|.
name|setTextContent
argument_list|(
literal|"Powered by Gerrit Code Review "
operator|+
name|Version
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|body
operator|.
name|appendChild
argument_list|(
name|footer
argument_list|)
expr_stmt|;
name|byte
index|[]
name|tosend
init|=
name|toUTF8
argument_list|(
name|page
argument_list|)
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
name|tosend
operator|=
name|compress
argument_list|(
name|tosend
argument_list|)
expr_stmt|;
block|}
name|rsp
operator|.
name|setDateHeader
argument_list|(
name|HDR_EXPIRES
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setHeader
argument_list|(
name|HDR_PRAGMA
argument_list|,
literal|"no-cache"
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setHeader
argument_list|(
name|HDR_CACHE_CONTROL
argument_list|,
literal|"no-cache, must-revalidate"
argument_list|)
expr_stmt|;
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
literal|"UTF-8"
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

