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
name|client
operator|.
name|data
operator|.
name|GerritConfig
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
name|spearce
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Constants
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
name|lib
operator|.
name|ObjectId
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
name|File
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
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
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
comment|/** Sends the Gerrit host page to clients. */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
annotation|@
name|Singleton
DECL|class|HostPageServlet
specifier|public
class|class
name|HostPageServlet
extends|extends
name|HttpServlet
block|{
DECL|field|callFactory
specifier|private
specifier|final
name|Provider
argument_list|<
name|GerritCall
argument_list|>
name|callFactory
decl_stmt|;
DECL|field|sitePath
specifier|private
specifier|final
name|File
name|sitePath
decl_stmt|;
DECL|field|config
specifier|private
specifier|final
name|GerritConfig
name|config
decl_stmt|;
DECL|field|canonicalUrl
specifier|private
specifier|final
name|String
name|canonicalUrl
decl_stmt|;
DECL|field|wantSSL
specifier|private
specifier|final
name|boolean
name|wantSSL
decl_stmt|;
DECL|field|hostDoc
specifier|private
name|Document
name|hostDoc
decl_stmt|;
annotation|@
name|Inject
DECL|method|HostPageServlet (final Provider<GerritCall> cf, @SitePath final File path, final GerritConfig gc, @CanonicalWebUrl @Nullable final String cwu)
name|HostPageServlet
parameter_list|(
specifier|final
name|Provider
argument_list|<
name|GerritCall
argument_list|>
name|cf
parameter_list|,
annotation|@
name|SitePath
specifier|final
name|File
name|path
parameter_list|,
specifier|final
name|GerritConfig
name|gc
parameter_list|,
annotation|@
name|CanonicalWebUrl
annotation|@
name|Nullable
specifier|final
name|String
name|cwu
parameter_list|)
block|{
name|callFactory
operator|=
name|cf
expr_stmt|;
name|canonicalUrl
operator|=
name|cwu
expr_stmt|;
name|sitePath
operator|=
name|path
expr_stmt|;
name|config
operator|=
name|gc
expr_stmt|;
name|wantSSL
operator|=
name|canonicalUrl
operator|!=
literal|null
operator|&&
name|canonicalUrl
operator|.
name|startsWith
argument_list|(
literal|"https:"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init (ServletConfig config)
specifier|public
name|void
name|init
parameter_list|(
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
specifier|final
name|String
name|hostPageName
init|=
literal|"WEB-INF/Gerrit.html"
decl_stmt|;
name|hostDoc
operator|=
name|HtmlDomUtil
operator|.
name|parseFile
argument_list|(
name|getServletContext
argument_list|()
argument_list|,
literal|"/"
operator|+
name|hostPageName
argument_list|)
expr_stmt|;
if|if
condition|(
name|hostDoc
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"No "
operator|+
name|hostPageName
operator|+
literal|" in webapp"
argument_list|)
throw|;
block|}
name|fixModuleReference
argument_list|(
name|hostDoc
argument_list|)
expr_stmt|;
name|injectCssFile
argument_list|(
name|hostDoc
argument_list|,
literal|"gerrit_sitecss"
argument_list|,
name|sitePath
argument_list|,
literal|"GerritSite.css"
argument_list|)
expr_stmt|;
name|injectXmlFile
argument_list|(
name|hostDoc
argument_list|,
literal|"gerrit_header"
argument_list|,
name|sitePath
argument_list|,
literal|"GerritSiteHeader.html"
argument_list|)
expr_stmt|;
name|injectXmlFile
argument_list|(
name|hostDoc
argument_list|,
literal|"gerrit_footer"
argument_list|,
name|sitePath
argument_list|,
literal|"GerritSiteFooter.html"
argument_list|)
expr_stmt|;
block|}
DECL|method|injectXmlFile (final Document hostDoc, final String id, final File sitePath, final String fileName)
specifier|private
name|void
name|injectXmlFile
parameter_list|(
specifier|final
name|Document
name|hostDoc
parameter_list|,
specifier|final
name|String
name|id
parameter_list|,
specifier|final
name|File
name|sitePath
parameter_list|,
specifier|final
name|String
name|fileName
parameter_list|)
throws|throws
name|ServletException
block|{
specifier|final
name|Element
name|banner
init|=
name|HtmlDomUtil
operator|.
name|find
argument_list|(
name|hostDoc
argument_list|,
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|banner
operator|==
literal|null
condition|)
block|{
return|return;
block|}
while|while
condition|(
name|banner
operator|.
name|getFirstChild
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|banner
operator|.
name|removeChild
argument_list|(
name|banner
operator|.
name|getFirstChild
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Document
name|html
init|=
name|HtmlDomUtil
operator|.
name|parseFile
argument_list|(
name|sitePath
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|html
operator|==
literal|null
condition|)
block|{
name|banner
operator|.
name|getParentNode
argument_list|()
operator|.
name|removeChild
argument_list|(
name|banner
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|Element
name|content
init|=
name|html
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|banner
operator|.
name|appendChild
argument_list|(
name|hostDoc
operator|.
name|importNode
argument_list|(
name|content
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|injectCssFile (final Document hostDoc, final String id, final File sitePath, final String fileName)
specifier|private
name|void
name|injectCssFile
parameter_list|(
specifier|final
name|Document
name|hostDoc
parameter_list|,
specifier|final
name|String
name|id
parameter_list|,
specifier|final
name|File
name|sitePath
parameter_list|,
specifier|final
name|String
name|fileName
parameter_list|)
throws|throws
name|ServletException
block|{
specifier|final
name|Element
name|banner
init|=
name|HtmlDomUtil
operator|.
name|find
argument_list|(
name|hostDoc
argument_list|,
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|banner
operator|==
literal|null
condition|)
block|{
return|return;
block|}
while|while
condition|(
name|banner
operator|.
name|getFirstChild
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|banner
operator|.
name|removeChild
argument_list|(
name|banner
operator|.
name|getFirstChild
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|css
init|=
name|HtmlDomUtil
operator|.
name|readFile
argument_list|(
name|sitePath
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|css
operator|==
literal|null
condition|)
block|{
name|banner
operator|.
name|getParentNode
argument_list|()
operator|.
name|removeChild
argument_list|(
name|banner
argument_list|)
expr_stmt|;
return|return;
block|}
name|banner
operator|.
name|removeAttribute
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
name|banner
operator|.
name|appendChild
argument_list|(
name|hostDoc
operator|.
name|createCDATASection
argument_list|(
literal|"\n"
operator|+
name|css
operator|+
literal|"\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|injectJson (final Document hostDoc, final String id, final Object obj)
specifier|private
name|void
name|injectJson
parameter_list|(
specifier|final
name|Document
name|hostDoc
parameter_list|,
specifier|final
name|String
name|id
parameter_list|,
specifier|final
name|Object
name|obj
parameter_list|)
block|{
specifier|final
name|Element
name|scriptNode
init|=
name|HtmlDomUtil
operator|.
name|find
argument_list|(
name|hostDoc
argument_list|,
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|scriptNode
operator|==
literal|null
condition|)
block|{
return|return;
block|}
while|while
condition|(
name|scriptNode
operator|.
name|getFirstChild
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|scriptNode
operator|.
name|removeChild
argument_list|(
name|scriptNode
operator|.
name|getFirstChild
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
name|scriptNode
operator|.
name|getParentNode
argument_list|()
operator|.
name|removeChild
argument_list|(
name|scriptNode
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|StringWriter
name|w
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|w
operator|.
name|write
argument_list|(
literal|"<!--\n"
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
literal|"var "
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
literal|"_obj="
argument_list|)
expr_stmt|;
name|GerritJsonServlet
operator|.
name|defaultGsonBuilder
argument_list|()
operator|.
name|create
argument_list|()
operator|.
name|toJson
argument_list|(
name|obj
argument_list|,
name|w
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
literal|";\n// -->\n"
argument_list|)
expr_stmt|;
name|scriptNode
operator|.
name|removeAttribute
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
name|scriptNode
operator|.
name|setAttribute
argument_list|(
literal|"type"
argument_list|,
literal|"text/javascript"
argument_list|)
expr_stmt|;
name|scriptNode
operator|.
name|setAttribute
argument_list|(
literal|"language"
argument_list|,
literal|"javascript"
argument_list|)
expr_stmt|;
name|scriptNode
operator|.
name|appendChild
argument_list|(
name|hostDoc
operator|.
name|createCDATASection
argument_list|(
name|w
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|fixModuleReference (final Document hostDoc)
specifier|private
name|void
name|fixModuleReference
parameter_list|(
specifier|final
name|Document
name|hostDoc
parameter_list|)
throws|throws
name|ServletException
block|{
specifier|final
name|Element
name|scriptNode
init|=
name|HtmlDomUtil
operator|.
name|find
argument_list|(
name|hostDoc
argument_list|,
literal|"gerrit_module"
argument_list|)
decl_stmt|;
if|if
condition|(
name|scriptNode
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"No gerrit_module to rewrite in host document"
argument_list|)
throw|;
block|}
name|scriptNode
operator|.
name|removeAttribute
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|src
init|=
name|scriptNode
operator|.
name|getAttribute
argument_list|(
literal|"src"
argument_list|)
decl_stmt|;
name|InputStream
name|in
init|=
name|getServletContext
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"/"
operator|+
name|src
argument_list|)
decl_stmt|;
if|if
condition|(
name|in
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"No "
operator|+
name|src
operator|+
literal|" in webapp root"
argument_list|)
throw|;
block|}
specifier|final
name|MessageDigest
name|md
init|=
name|Constants
operator|.
name|newMessageDigest
argument_list|()
decl_stmt|;
try|try
block|{
try|try
block|{
specifier|final
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|n
decl_stmt|;
while|while
condition|(
operator|(
name|n
operator|=
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|md
operator|.
name|update
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
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
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Failed reading "
operator|+
name|src
argument_list|,
name|e
argument_list|)
throw|;
block|}
specifier|final
name|String
name|vstr
init|=
name|ObjectId
operator|.
name|fromRaw
argument_list|(
name|md
operator|.
name|digest
argument_list|()
argument_list|)
operator|.
name|name
argument_list|()
decl_stmt|;
name|scriptNode
operator|.
name|setAttribute
argument_list|(
literal|"src"
argument_list|,
name|src
operator|+
literal|"?content="
operator|+
name|vstr
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
specifier|final
name|String
name|screen
init|=
name|req
operator|.
name|getPathInfo
argument_list|()
decl_stmt|;
comment|// If we wanted SSL, but the user didn't come to us over an SSL channel,
comment|// force it to be SSL by issuing a protocol redirect. Try to keep the
comment|// name "localhost" in case this is an SSH port tunnel.
comment|//
if|if
condition|(
name|wantSSL
operator|&&
operator|!
name|isSecure
argument_list|(
name|req
argument_list|)
condition|)
block|{
specifier|final
name|StringBuffer
name|reqUrl
init|=
name|req
operator|.
name|getRequestURL
argument_list|()
decl_stmt|;
if|if
condition|(
name|isLocalHost
argument_list|(
name|req
argument_list|)
condition|)
block|{
name|reqUrl
operator|.
name|replace
argument_list|(
literal|0
argument_list|,
name|reqUrl
operator|.
name|indexOf
argument_list|(
literal|":"
argument_list|)
argument_list|,
literal|"https"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|reqUrl
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|reqUrl
operator|.
name|append
argument_list|(
name|canonicalUrl
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasScreenName
argument_list|(
name|screen
argument_list|)
condition|)
block|{
name|reqUrl
operator|.
name|append
argument_list|(
literal|'#'
argument_list|)
expr_stmt|;
name|reqUrl
operator|.
name|append
argument_list|(
name|screen
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|rsp
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_MOVED_PERMANENTLY
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setHeader
argument_list|(
literal|"Location"
argument_list|,
name|reqUrl
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// If we get a request for "/Gerrit/change,1" rewrite it the way
comment|// it should have been, as "/Gerrit#change,1". This may happen
comment|// coming out of Google Analytics, where its common to replace
comment|// the anchor mark ('#') with '/' so it logs independent pages.
comment|//
if|if
condition|(
name|hasScreenName
argument_list|(
name|screen
argument_list|)
condition|)
block|{
specifier|final
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|r
operator|.
name|append
argument_list|(
name|GerritServer
operator|.
name|serverUrl
argument_list|(
name|req
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"#"
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|screen
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_MOVED_PERMANENTLY
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setHeader
argument_list|(
literal|"Location"
argument_list|,
name|r
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|Account
operator|.
name|Id
name|me
init|=
name|callFactory
operator|.
name|get
argument_list|()
operator|.
name|getAccountId
argument_list|()
decl_stmt|;
specifier|final
name|Account
name|account
init|=
name|Common
operator|.
name|getAccountCache
argument_list|()
operator|.
name|get
argument_list|(
name|me
argument_list|)
decl_stmt|;
specifier|final
name|Document
name|peruser
init|=
name|HtmlDomUtil
operator|.
name|clone
argument_list|(
name|hostDoc
argument_list|)
decl_stmt|;
name|injectJson
argument_list|(
name|peruser
argument_list|,
literal|"gerrit_gerritconfig"
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|injectJson
argument_list|(
name|peruser
argument_list|,
literal|"gerrit_myaccount"
argument_list|,
name|account
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|raw
init|=
name|HtmlDomUtil
operator|.
name|toUTF8
argument_list|(
name|peruser
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|tosend
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
name|HtmlDomUtil
operator|.
name|compress
argument_list|(
name|raw
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tosend
operator|=
name|raw
expr_stmt|;
block|}
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
DECL|method|hasScreenName (final String screen)
specifier|private
specifier|static
name|boolean
name|hasScreenName
parameter_list|(
specifier|final
name|String
name|screen
parameter_list|)
block|{
return|return
name|screen
operator|!=
literal|null
operator|&&
name|screen
operator|.
name|length
argument_list|()
operator|>
literal|1
operator|&&
name|screen
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
return|;
block|}
DECL|method|isSecure (final HttpServletRequest req)
specifier|private
specifier|static
name|boolean
name|isSecure
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|)
block|{
return|return
literal|"https"
operator|.
name|equals
argument_list|(
name|req
operator|.
name|getScheme
argument_list|()
argument_list|)
operator|||
name|req
operator|.
name|isSecure
argument_list|()
return|;
block|}
DECL|method|isLocalHost (final HttpServletRequest req)
specifier|private
specifier|static
name|boolean
name|isLocalHost
parameter_list|(
specifier|final
name|HttpServletRequest
name|req
parameter_list|)
block|{
return|return
literal|"localhost"
operator|.
name|equals
argument_list|(
name|req
operator|.
name|getServerName
argument_list|()
argument_list|)
operator|||
literal|"127.0.0.1"
operator|.
name|equals
argument_list|(
name|req
operator|.
name|getServerName
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

