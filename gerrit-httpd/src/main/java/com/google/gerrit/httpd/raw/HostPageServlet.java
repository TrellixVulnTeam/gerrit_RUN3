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
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|Hasher
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
name|primitives
operator|.
name|Bytes
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
name|common
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
name|common
operator|.
name|data
operator|.
name|HostPageData
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
name|DynamicSet
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
name|systemstatus
operator|.
name|MessageOfTheDay
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
name|webui
operator|.
name|WebUiPlugin
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
name|server
operator|.
name|CurrentUser
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
name|IdentifiedUser
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
name|linker
operator|.
name|server
operator|.
name|Permutation
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
name|linker
operator|.
name|server
operator|.
name|PermutationSelector
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
name|JsonServlet
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
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
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
name|util
operator|.
name|ArrayList
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
name|List
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
name|HostPageServlet
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|IS_DEV
specifier|private
specifier|static
specifier|final
name|boolean
name|IS_DEV
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"Gerrit.GwtDevMode"
argument_list|)
decl_stmt|;
DECL|field|HPD_ID
specifier|private
specifier|static
specifier|final
name|String
name|HPD_ID
init|=
literal|"gerrit_hostpagedata"
decl_stmt|;
DECL|field|currentUser
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|currentUser
decl_stmt|;
DECL|field|session
specifier|private
specifier|final
name|Provider
argument_list|<
name|WebSession
argument_list|>
name|session
decl_stmt|;
DECL|field|config
specifier|private
specifier|final
name|GerritConfig
name|config
decl_stmt|;
DECL|field|plugins
specifier|private
specifier|final
name|DynamicSet
argument_list|<
name|WebUiPlugin
argument_list|>
name|plugins
decl_stmt|;
DECL|field|messages
specifier|private
specifier|final
name|DynamicSet
argument_list|<
name|MessageOfTheDay
argument_list|>
name|messages
decl_stmt|;
DECL|field|signedOutTheme
specifier|private
specifier|final
name|HostPageData
operator|.
name|Theme
name|signedOutTheme
decl_stmt|;
DECL|field|signedInTheme
specifier|private
specifier|final
name|HostPageData
operator|.
name|Theme
name|signedInTheme
decl_stmt|;
DECL|field|site
specifier|private
specifier|final
name|SitePaths
name|site
decl_stmt|;
DECL|field|template
specifier|private
specifier|final
name|Document
name|template
decl_stmt|;
DECL|field|noCacheName
specifier|private
specifier|final
name|String
name|noCacheName
decl_stmt|;
DECL|field|selector
specifier|private
specifier|final
name|PermutationSelector
name|selector
decl_stmt|;
DECL|field|refreshHeaderFooter
specifier|private
specifier|final
name|boolean
name|refreshHeaderFooter
decl_stmt|;
DECL|field|staticServlet
specifier|private
specifier|final
name|StaticServlet
name|staticServlet
decl_stmt|;
DECL|field|page
specifier|private
specifier|volatile
name|Page
name|page
decl_stmt|;
annotation|@
name|Inject
DECL|method|HostPageServlet (final Provider<CurrentUser> cu, final Provider<WebSession> w, final SitePaths sp, final ThemeFactory themeFactory, final GerritConfig gc, final ServletContext servletContext, final DynamicSet<WebUiPlugin> webUiPlugins, final DynamicSet<MessageOfTheDay> motd, @GerritServerConfig final Config cfg, final StaticServlet ss)
name|HostPageServlet
parameter_list|(
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|cu
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|WebSession
argument_list|>
name|w
parameter_list|,
specifier|final
name|SitePaths
name|sp
parameter_list|,
specifier|final
name|ThemeFactory
name|themeFactory
parameter_list|,
specifier|final
name|GerritConfig
name|gc
parameter_list|,
specifier|final
name|ServletContext
name|servletContext
parameter_list|,
specifier|final
name|DynamicSet
argument_list|<
name|WebUiPlugin
argument_list|>
name|webUiPlugins
parameter_list|,
specifier|final
name|DynamicSet
argument_list|<
name|MessageOfTheDay
argument_list|>
name|motd
parameter_list|,
annotation|@
name|GerritServerConfig
specifier|final
name|Config
name|cfg
parameter_list|,
specifier|final
name|StaticServlet
name|ss
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
name|currentUser
operator|=
name|cu
expr_stmt|;
name|session
operator|=
name|w
expr_stmt|;
name|config
operator|=
name|gc
expr_stmt|;
name|plugins
operator|=
name|webUiPlugins
expr_stmt|;
name|messages
operator|=
name|motd
expr_stmt|;
name|signedOutTheme
operator|=
name|themeFactory
operator|.
name|getSignedOutTheme
argument_list|()
expr_stmt|;
name|signedInTheme
operator|=
name|themeFactory
operator|.
name|getSignedInTheme
argument_list|()
expr_stmt|;
name|site
operator|=
name|sp
expr_stmt|;
name|refreshHeaderFooter
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
name|staticServlet
operator|=
name|ss
expr_stmt|;
name|boolean
name|checkUserAgent
init|=
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"site"
argument_list|,
literal|"checkUserAgent"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|String
name|pageName
init|=
literal|"HostPage.html"
decl_stmt|;
name|template
operator|=
name|HtmlDomUtil
operator|.
name|parseFile
argument_list|(
name|getClass
argument_list|()
argument_list|,
name|pageName
argument_list|)
expr_stmt|;
if|if
condition|(
name|template
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
if|if
condition|(
name|HtmlDomUtil
operator|.
name|find
argument_list|(
name|template
argument_list|,
literal|"gerrit_module"
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"No gerrit_module in "
operator|+
name|pageName
argument_list|)
throw|;
block|}
if|if
condition|(
name|HtmlDomUtil
operator|.
name|find
argument_list|(
name|template
argument_list|,
name|HPD_ID
argument_list|)
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
name|HPD_ID
operator|+
literal|" in "
operator|+
name|pageName
argument_list|)
throw|;
block|}
name|String
name|src
init|=
literal|"gerrit_ui/gerrit_ui.nocache.js"
decl_stmt|;
if|if
condition|(
operator|!
name|IS_DEV
condition|)
block|{
name|Element
name|devmode
init|=
name|HtmlDomUtil
operator|.
name|find
argument_list|(
name|template
argument_list|,
literal|"gwtdevmode"
argument_list|)
decl_stmt|;
if|if
condition|(
name|devmode
operator|!=
literal|null
condition|)
block|{
name|devmode
operator|.
name|getParentNode
argument_list|()
operator|.
name|removeChild
argument_list|(
name|devmode
argument_list|)
expr_stmt|;
block|}
name|InputStream
name|in
init|=
name|servletContext
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
operator|!=
literal|null
condition|)
block|{
name|Hasher
name|md
init|=
name|Hashing
operator|.
name|md5
argument_list|()
operator|.
name|newHasher
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
name|putBytes
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
name|IOException
argument_list|(
literal|"Failed reading "
operator|+
name|src
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|src
operator|+=
literal|"?content="
operator|+
name|md
operator|.
name|hash
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"No "
operator|+
name|src
operator|+
literal|" in webapp root; keeping noncache.js URL"
argument_list|)
expr_stmt|;
block|}
block|}
name|noCacheName
operator|=
name|src
expr_stmt|;
name|selector
operator|=
operator|new
name|PermutationSelector
argument_list|(
literal|"gerrit_ui"
argument_list|)
expr_stmt|;
if|if
condition|(
name|checkUserAgent
operator|&&
operator|!
name|IS_DEV
condition|)
block|{
name|selector
operator|.
name|init
argument_list|(
name|servletContext
argument_list|)
expr_stmt|;
block|}
name|page
operator|=
operator|new
name|Page
argument_list|()
expr_stmt|;
block|}
DECL|method|json (final Object data, final StringWriter w)
specifier|private
name|void
name|json
parameter_list|(
specifier|final
name|Object
name|data
parameter_list|,
specifier|final
name|StringWriter
name|w
parameter_list|)
block|{
name|JsonServlet
operator|.
name|defaultGsonBuilder
argument_list|()
operator|.
name|create
argument_list|()
operator|.
name|toJson
argument_list|(
name|data
argument_list|,
name|w
argument_list|)
expr_stmt|;
block|}
DECL|method|get ()
specifier|private
name|Page
name|get
parameter_list|()
block|{
name|Page
name|p
init|=
name|page
decl_stmt|;
if|if
condition|(
name|refreshHeaderFooter
operator|&&
name|p
operator|.
name|isStale
argument_list|()
condition|)
block|{
specifier|final
name|Page
name|newPage
decl_stmt|;
try|try
block|{
name|newPage
operator|=
operator|new
name|Page
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot refresh site header/footer"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
name|p
operator|=
name|newPage
expr_stmt|;
name|page
operator|=
name|p
expr_stmt|;
block|}
return|return
name|p
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
name|Page
operator|.
name|Content
name|page
init|=
name|select
argument_list|(
name|req
argument_list|)
decl_stmt|;
specifier|final
name|StringWriter
name|w
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
specifier|final
name|CurrentUser
name|user
init|=
name|currentUser
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|user
operator|.
name|isIdentifiedUser
argument_list|()
condition|)
block|{
name|w
operator|.
name|write
argument_list|(
name|HPD_ID
operator|+
literal|".account="
argument_list|)
expr_stmt|;
name|json
argument_list|(
operator|(
operator|(
name|IdentifiedUser
operator|)
name|user
operator|)
operator|.
name|getAccount
argument_list|()
argument_list|,
name|w
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
literal|";"
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
name|HPD_ID
operator|+
literal|".xGerritAuth="
argument_list|)
expr_stmt|;
name|json
argument_list|(
name|session
operator|.
name|get
argument_list|()
operator|.
name|getXGerritAuth
argument_list|()
argument_list|,
name|w
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
literal|";"
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
name|HPD_ID
operator|+
literal|".accountDiffPref="
argument_list|)
expr_stmt|;
name|json
argument_list|(
operator|(
operator|(
name|IdentifiedUser
operator|)
name|user
operator|)
operator|.
name|getAccountDiffPreference
argument_list|()
argument_list|,
name|w
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
literal|";"
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
name|HPD_ID
operator|+
literal|".theme="
argument_list|)
expr_stmt|;
name|json
argument_list|(
name|signedInTheme
argument_list|,
name|w
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
literal|";"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|w
operator|.
name|write
argument_list|(
name|HPD_ID
operator|+
literal|".theme="
argument_list|)
expr_stmt|;
name|json
argument_list|(
name|signedOutTheme
argument_list|,
name|w
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
literal|";"
argument_list|)
expr_stmt|;
block|}
name|plugins
argument_list|(
name|w
argument_list|)
expr_stmt|;
name|messages
argument_list|(
name|w
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|hpd
init|=
name|w
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|raw
init|=
name|Bytes
operator|.
name|concat
argument_list|(
name|page
operator|.
name|part1
argument_list|,
name|hpd
argument_list|,
name|page
operator|.
name|part2
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
name|CacheHeaders
operator|.
name|setNotCacheable
argument_list|(
name|rsp
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
DECL|method|plugins (StringWriter w)
specifier|private
name|void
name|plugins
parameter_list|(
name|StringWriter
name|w
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|urls
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|WebUiPlugin
name|u
range|:
name|plugins
control|)
block|{
name|urls
operator|.
name|add
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"plugins/%s/%s"
argument_list|,
name|u
operator|.
name|getPluginName
argument_list|()
argument_list|,
name|u
operator|.
name|getJavaScriptResourcePath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|urls
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|w
operator|.
name|write
argument_list|(
name|HPD_ID
operator|+
literal|".plugins="
argument_list|)
expr_stmt|;
name|json
argument_list|(
name|urls
argument_list|,
name|w
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
literal|";"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|messages (StringWriter w)
specifier|private
name|void
name|messages
parameter_list|(
name|StringWriter
name|w
parameter_list|)
block|{
name|List
argument_list|<
name|HostPageData
operator|.
name|Message
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
name|MessageOfTheDay
name|motd
range|:
name|messages
control|)
block|{
name|String
name|html
init|=
name|motd
operator|.
name|getHtmlMessage
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|html
argument_list|)
condition|)
block|{
name|HostPageData
operator|.
name|Message
name|m
init|=
operator|new
name|HostPageData
operator|.
name|Message
argument_list|()
decl_stmt|;
name|m
operator|.
name|id
operator|=
name|motd
operator|.
name|getMessageId
argument_list|()
expr_stmt|;
name|m
operator|.
name|redisplay
operator|=
name|motd
operator|.
name|getRedisplay
argument_list|()
expr_stmt|;
name|m
operator|.
name|html
operator|=
name|html
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|w
operator|.
name|write
argument_list|(
name|HPD_ID
operator|+
literal|".messages="
argument_list|)
expr_stmt|;
name|json
argument_list|(
name|list
argument_list|,
name|w
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
literal|";"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|select (HttpServletRequest req)
specifier|private
name|Page
operator|.
name|Content
name|select
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
name|Page
name|pg
init|=
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"1"
operator|.
name|equals
argument_list|(
name|req
operator|.
name|getParameter
argument_list|(
literal|"dbg"
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|pg
operator|.
name|debug
return|;
block|}
elseif|else
if|if
condition|(
literal|"0"
operator|.
name|equals
argument_list|(
name|req
operator|.
name|getParameter
argument_list|(
literal|"s"
argument_list|)
argument_list|)
condition|)
block|{
comment|// If s=0 is used in the URL, the user has explicitly asked us
comment|// to not perform selection on the server side, perhaps due to
comment|// it incorrectly guessing their user agent.
return|return
name|pg
operator|.
name|get
argument_list|(
literal|null
argument_list|)
return|;
block|}
return|return
name|pg
operator|.
name|get
argument_list|(
name|selector
operator|.
name|select
argument_list|(
name|req
argument_list|)
argument_list|)
return|;
block|}
DECL|method|insertETags (Element e)
specifier|private
name|void
name|insertETags
parameter_list|(
name|Element
name|e
parameter_list|)
block|{
if|if
condition|(
literal|"img"
operator|.
name|equalsIgnoreCase
argument_list|(
name|e
operator|.
name|getTagName
argument_list|()
argument_list|)
operator|||
literal|"script"
operator|.
name|equalsIgnoreCase
argument_list|(
name|e
operator|.
name|getTagName
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|src
init|=
name|e
operator|.
name|getAttribute
argument_list|(
literal|"src"
argument_list|)
decl_stmt|;
if|if
condition|(
name|src
operator|!=
literal|null
operator|&&
name|src
operator|.
name|startsWith
argument_list|(
literal|"static/"
argument_list|)
condition|)
block|{
name|String
name|name
init|=
name|src
operator|.
name|substring
argument_list|(
literal|"static/"
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|StaticServlet
operator|.
name|Resource
name|r
init|=
name|staticServlet
operator|.
name|getResource
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
name|e
operator|.
name|setAttribute
argument_list|(
literal|"src"
argument_list|,
name|src
operator|+
literal|"?e="
operator|+
name|r
operator|.
name|etag
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|Node
name|n
init|=
name|e
operator|.
name|getFirstChild
argument_list|()
init|;
name|n
operator|!=
literal|null
condition|;
name|n
operator|=
name|n
operator|.
name|getNextSibling
argument_list|()
control|)
block|{
if|if
condition|(
name|n
operator|instanceof
name|Element
condition|)
block|{
name|insertETags
argument_list|(
operator|(
name|Element
operator|)
name|n
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|FileInfo
specifier|private
specifier|static
class|class
name|FileInfo
block|{
DECL|field|path
specifier|private
specifier|final
name|File
name|path
decl_stmt|;
DECL|field|time
specifier|private
specifier|final
name|long
name|time
decl_stmt|;
DECL|method|FileInfo (final File p)
name|FileInfo
parameter_list|(
specifier|final
name|File
name|p
parameter_list|)
block|{
name|path
operator|=
name|p
expr_stmt|;
name|time
operator|=
name|path
operator|.
name|lastModified
argument_list|()
expr_stmt|;
block|}
DECL|method|isStale ()
name|boolean
name|isStale
parameter_list|()
block|{
return|return
name|time
operator|!=
name|path
operator|.
name|lastModified
argument_list|()
return|;
block|}
block|}
DECL|class|Page
specifier|private
class|class
name|Page
block|{
DECL|field|css
specifier|private
specifier|final
name|FileInfo
name|css
decl_stmt|;
DECL|field|header
specifier|private
specifier|final
name|FileInfo
name|header
decl_stmt|;
DECL|field|footer
specifier|private
specifier|final
name|FileInfo
name|footer
decl_stmt|;
DECL|field|permutations
specifier|private
specifier|final
name|Map
argument_list|<
name|Permutation
argument_list|,
name|Content
argument_list|>
name|permutations
decl_stmt|;
DECL|field|debug
specifier|private
specifier|final
name|Content
name|debug
decl_stmt|;
DECL|method|Page ()
name|Page
parameter_list|()
throws|throws
name|IOException
block|{
name|Document
name|hostDoc
init|=
name|HtmlDomUtil
operator|.
name|clone
argument_list|(
name|template
argument_list|)
decl_stmt|;
name|css
operator|=
name|injectCssFile
argument_list|(
name|hostDoc
argument_list|,
literal|"gerrit_sitecss"
argument_list|,
name|site
operator|.
name|site_css
argument_list|)
expr_stmt|;
name|header
operator|=
name|injectXmlFile
argument_list|(
name|hostDoc
argument_list|,
literal|"gerrit_header"
argument_list|,
name|site
operator|.
name|site_header
argument_list|)
expr_stmt|;
name|footer
operator|=
name|injectXmlFile
argument_list|(
name|hostDoc
argument_list|,
literal|"gerrit_footer"
argument_list|,
name|site
operator|.
name|site_footer
argument_list|)
expr_stmt|;
specifier|final
name|HostPageData
name|pageData
init|=
operator|new
name|HostPageData
argument_list|()
decl_stmt|;
name|pageData
operator|.
name|version
operator|=
name|Version
operator|.
name|getVersion
argument_list|()
expr_stmt|;
name|pageData
operator|.
name|config
operator|=
name|config
expr_stmt|;
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
literal|"var "
operator|+
name|HPD_ID
operator|+
literal|"="
argument_list|)
expr_stmt|;
name|json
argument_list|(
name|pageData
argument_list|,
name|w
argument_list|)
expr_stmt|;
name|w
operator|.
name|write
argument_list|(
literal|";"
argument_list|)
expr_stmt|;
specifier|final
name|Element
name|data
init|=
name|HtmlDomUtil
operator|.
name|find
argument_list|(
name|hostDoc
argument_list|,
name|HPD_ID
argument_list|)
decl_stmt|;
name|asScript
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|data
operator|.
name|appendChild
argument_list|(
name|hostDoc
operator|.
name|createTextNode
argument_list|(
name|w
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|data
operator|.
name|appendChild
argument_list|(
name|hostDoc
operator|.
name|createComment
argument_list|(
name|HPD_ID
argument_list|)
argument_list|)
expr_stmt|;
name|permutations
operator|=
operator|new
name|HashMap
argument_list|<
name|Permutation
argument_list|,
name|Content
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|Permutation
name|p
range|:
name|selector
operator|.
name|getPermutations
argument_list|()
control|)
block|{
specifier|final
name|Document
name|d
init|=
name|HtmlDomUtil
operator|.
name|clone
argument_list|(
name|hostDoc
argument_list|)
decl_stmt|;
name|Element
name|nocache
init|=
name|HtmlDomUtil
operator|.
name|find
argument_list|(
name|d
argument_list|,
literal|"gerrit_module"
argument_list|)
decl_stmt|;
name|nocache
operator|.
name|getParentNode
argument_list|()
operator|.
name|removeChild
argument_list|(
name|nocache
argument_list|)
expr_stmt|;
name|p
operator|.
name|inject
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|permutations
operator|.
name|put
argument_list|(
name|p
argument_list|,
operator|new
name|Content
argument_list|(
name|d
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Element
name|nocache
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
name|asScript
argument_list|(
name|nocache
argument_list|)
expr_stmt|;
name|nocache
operator|.
name|removeAttribute
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
name|nocache
operator|.
name|setAttribute
argument_list|(
literal|"src"
argument_list|,
name|noCacheName
argument_list|)
expr_stmt|;
name|permutations
operator|.
name|put
argument_list|(
literal|null
argument_list|,
operator|new
name|Content
argument_list|(
name|hostDoc
argument_list|)
argument_list|)
expr_stmt|;
name|nocache
operator|.
name|setAttribute
argument_list|(
literal|"src"
argument_list|,
literal|"gerrit_ui/gerrit_dbg.nocache.js"
argument_list|)
expr_stmt|;
name|debug
operator|=
operator|new
name|Content
argument_list|(
name|hostDoc
argument_list|)
expr_stmt|;
block|}
DECL|method|get (Permutation p)
name|Content
name|get
parameter_list|(
name|Permutation
name|p
parameter_list|)
block|{
name|Content
name|c
init|=
name|permutations
operator|.
name|get
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
name|c
operator|=
name|permutations
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|c
return|;
block|}
DECL|method|isStale ()
name|boolean
name|isStale
parameter_list|()
block|{
return|return
name|css
operator|.
name|isStale
argument_list|()
operator|||
name|header
operator|.
name|isStale
argument_list|()
operator|||
name|footer
operator|.
name|isStale
argument_list|()
return|;
block|}
DECL|method|asScript (final Element scriptNode)
specifier|private
name|void
name|asScript
parameter_list|(
specifier|final
name|Element
name|scriptNode
parameter_list|)
block|{
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
block|}
DECL|class|Content
class|class
name|Content
block|{
DECL|field|part1
specifier|final
name|byte
index|[]
name|part1
decl_stmt|;
DECL|field|part2
specifier|final
name|byte
index|[]
name|part2
decl_stmt|;
DECL|method|Content (Document hostDoc)
name|Content
parameter_list|(
name|Document
name|hostDoc
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|raw
init|=
name|HtmlDomUtil
operator|.
name|toString
argument_list|(
name|hostDoc
argument_list|)
decl_stmt|;
specifier|final
name|int
name|p
init|=
name|raw
operator|.
name|indexOf
argument_list|(
literal|"<!--"
operator|+
name|HPD_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No tag in transformed host page HTML"
argument_list|)
throw|;
block|}
name|part1
operator|=
name|raw
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|part2
operator|=
name|raw
operator|.
name|substring
argument_list|(
name|raw
operator|.
name|indexOf
argument_list|(
literal|'>'
argument_list|,
name|p
argument_list|)
operator|+
literal|1
argument_list|)
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|injectCssFile (final Document hostDoc, final String id, final File src)
specifier|private
name|FileInfo
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
name|src
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FileInfo
name|info
init|=
operator|new
name|FileInfo
argument_list|(
name|src
argument_list|)
decl_stmt|;
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
return|return
name|info
return|;
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
name|String
name|css
init|=
name|HtmlDomUtil
operator|.
name|readFile
argument_list|(
name|src
operator|.
name|getParentFile
argument_list|()
argument_list|,
name|src
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|css
operator|==
literal|null
condition|)
block|{
return|return
name|info
return|;
block|}
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
return|return
name|info
return|;
block|}
DECL|method|injectXmlFile (final Document hostDoc, final String id, final File src)
specifier|private
name|FileInfo
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
name|src
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FileInfo
name|info
init|=
operator|new
name|FileInfo
argument_list|(
name|src
argument_list|)
decl_stmt|;
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
return|return
name|info
return|;
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
name|Document
name|html
init|=
name|HtmlDomUtil
operator|.
name|parseFile
argument_list|(
name|src
argument_list|)
decl_stmt|;
if|if
condition|(
name|html
operator|==
literal|null
condition|)
block|{
return|return
name|info
return|;
block|}
name|Element
name|content
init|=
name|html
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|insertETags
argument_list|(
name|content
argument_list|)
expr_stmt|;
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
return|return
name|info
return|;
block|}
block|}
block|}
end_class

end_unit

