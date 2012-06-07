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
DECL|package|com.google.gerrit.httpd
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Scopes
operator|.
name|SINGLETON
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
name|PageLinks
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
name|raw
operator|.
name|CatServlet
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
name|raw
operator|.
name|HostPageServlet
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
name|raw
operator|.
name|LegacyGerritServlet
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
name|raw
operator|.
name|SshInfoServlet
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
name|raw
operator|.
name|StaticServlet
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
name|raw
operator|.
name|ToolServlet
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
name|rpc
operator|.
name|account
operator|.
name|AccountCapabilitiesServlet
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
name|rpc
operator|.
name|change
operator|.
name|DeprecatedChangeQueryServlet
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
name|rpc
operator|.
name|change
operator|.
name|ListChangesServlet
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
name|rpc
operator|.
name|plugin
operator|.
name|ListPluginsServlet
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
name|rpc
operator|.
name|project
operator|.
name|ListProjectsServlet
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
name|reviewdb
operator|.
name|client
operator|.
name|Change
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
name|reviewdb
operator|.
name|client
operator|.
name|Project
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
name|gwtexpui
operator|.
name|server
operator|.
name|CacheControlFilter
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
name|Key
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
name|internal
operator|.
name|UniqueAnnotations
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
name|servlet
operator|.
name|ServletModule
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
DECL|class|UrlModule
class|class
name|UrlModule
extends|extends
name|ServletModule
block|{
DECL|class|UrlConfig
specifier|static
class|class
name|UrlConfig
block|{
DECL|field|deprecatedQuery
specifier|private
specifier|final
name|boolean
name|deprecatedQuery
decl_stmt|;
annotation|@
name|Inject
DECL|method|UrlConfig (@erritServerConfig Config cfg)
name|UrlConfig
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|)
block|{
name|deprecatedQuery
operator|=
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"site"
argument_list|,
literal|"enableDeprecatedQuery"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|cfg
specifier|private
specifier|final
name|UrlConfig
name|cfg
decl_stmt|;
DECL|method|UrlModule (UrlConfig cfg)
name|UrlModule
parameter_list|(
name|UrlConfig
name|cfg
parameter_list|)
block|{
name|this
operator|.
name|cfg
operator|=
name|cfg
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configureServlets ()
specifier|protected
name|void
name|configureServlets
parameter_list|()
block|{
name|filter
argument_list|(
literal|"/*"
argument_list|)
operator|.
name|through
argument_list|(
name|Key
operator|.
name|get
argument_list|(
name|CacheControlFilter
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|Key
operator|.
name|get
argument_list|(
name|CacheControlFilter
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|in
argument_list|(
name|SINGLETON
argument_list|)
expr_stmt|;
name|serve
argument_list|(
literal|"/"
argument_list|)
operator|.
name|with
argument_list|(
name|HostPageServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|serve
argument_list|(
literal|"/Gerrit"
argument_list|)
operator|.
name|with
argument_list|(
name|LegacyGerritServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|serve
argument_list|(
literal|"/Gerrit/*"
argument_list|)
operator|.
name|with
argument_list|(
name|legacyGerritScreen
argument_list|()
argument_list|)
expr_stmt|;
name|serve
argument_list|(
literal|"/cat/*"
argument_list|)
operator|.
name|with
argument_list|(
name|CatServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|serve
argument_list|(
literal|"/logout"
argument_list|)
operator|.
name|with
argument_list|(
name|HttpLogoutServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|serve
argument_list|(
literal|"/signout"
argument_list|)
operator|.
name|with
argument_list|(
name|HttpLogoutServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|serve
argument_list|(
literal|"/ssh_info"
argument_list|)
operator|.
name|with
argument_list|(
name|SshInfoServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|serve
argument_list|(
literal|"/static/*"
argument_list|)
operator|.
name|with
argument_list|(
name|StaticServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|serve
argument_list|(
literal|"/tools/*"
argument_list|)
operator|.
name|with
argument_list|(
name|ToolServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|serve
argument_list|(
literal|"/Main.class"
argument_list|)
operator|.
name|with
argument_list|(
name|notFound
argument_list|()
argument_list|)
expr_stmt|;
name|serve
argument_list|(
literal|"/com/google/gerrit/launcher/*"
argument_list|)
operator|.
name|with
argument_list|(
name|notFound
argument_list|()
argument_list|)
expr_stmt|;
name|serve
argument_list|(
literal|"/servlet/*"
argument_list|)
operator|.
name|with
argument_list|(
name|notFound
argument_list|()
argument_list|)
expr_stmt|;
name|serve
argument_list|(
literal|"/all"
argument_list|)
operator|.
name|with
argument_list|(
name|query
argument_list|(
literal|"status:merged"
argument_list|)
argument_list|)
expr_stmt|;
name|serve
argument_list|(
literal|"/mine"
argument_list|)
operator|.
name|with
argument_list|(
name|screen
argument_list|(
name|PageLinks
operator|.
name|MINE
argument_list|)
argument_list|)
expr_stmt|;
name|serve
argument_list|(
literal|"/open"
argument_list|)
operator|.
name|with
argument_list|(
name|query
argument_list|(
literal|"status:open"
argument_list|)
argument_list|)
expr_stmt|;
name|serve
argument_list|(
literal|"/watched"
argument_list|)
operator|.
name|with
argument_list|(
name|query
argument_list|(
literal|"is:watched status:open"
argument_list|)
argument_list|)
expr_stmt|;
name|serve
argument_list|(
literal|"/starred"
argument_list|)
operator|.
name|with
argument_list|(
name|query
argument_list|(
literal|"is:starred"
argument_list|)
argument_list|)
expr_stmt|;
name|serveRegex
argument_list|(
literal|"^/settings/?$"
argument_list|)
operator|.
name|with
argument_list|(
name|screen
argument_list|(
name|PageLinks
operator|.
name|SETTINGS
argument_list|)
argument_list|)
expr_stmt|;
name|serveRegex
argument_list|(
literal|"^/register/?$"
argument_list|)
operator|.
name|with
argument_list|(
name|screen
argument_list|(
name|PageLinks
operator|.
name|REGISTER
operator|+
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|serveRegex
argument_list|(
literal|"^/([1-9][0-9]*)/?$"
argument_list|)
operator|.
name|with
argument_list|(
name|directChangeById
argument_list|()
argument_list|)
expr_stmt|;
name|serveRegex
argument_list|(
literal|"^/p/(.*)$"
argument_list|)
operator|.
name|with
argument_list|(
name|queryProjectNew
argument_list|()
argument_list|)
expr_stmt|;
name|serveRegex
argument_list|(
literal|"^/r/(.+)/?$"
argument_list|)
operator|.
name|with
argument_list|(
name|DirectChangeByCommit
operator|.
name|class
argument_list|)
expr_stmt|;
name|filter
argument_list|(
literal|"/a/*"
argument_list|)
operator|.
name|through
argument_list|(
name|RequireIdentifiedUserFilter
operator|.
name|class
argument_list|)
expr_stmt|;
name|serveRegex
argument_list|(
literal|"^/(?:a/)?accounts/self/capabilities$"
argument_list|)
operator|.
name|with
argument_list|(
name|AccountCapabilitiesServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|serveRegex
argument_list|(
literal|"^/(?:a/)?changes/$"
argument_list|)
operator|.
name|with
argument_list|(
name|ListChangesServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|serveRegex
argument_list|(
literal|"^/(?:a/)?plugins/$"
argument_list|)
operator|.
name|with
argument_list|(
name|ListPluginsServlet
operator|.
name|class
argument_list|)
expr_stmt|;
name|serveRegex
argument_list|(
literal|"^/(?:a/)?projects/(.*)?$"
argument_list|)
operator|.
name|with
argument_list|(
name|ListProjectsServlet
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|cfg
operator|.
name|deprecatedQuery
condition|)
block|{
name|serve
argument_list|(
literal|"/query"
argument_list|)
operator|.
name|with
argument_list|(
name|DeprecatedChangeQueryServlet
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|notFound ()
specifier|private
name|Key
argument_list|<
name|HttpServlet
argument_list|>
name|notFound
parameter_list|()
block|{
return|return
name|key
argument_list|(
operator|new
name|HttpServlet
argument_list|()
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|Override
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
argument_list|)
return|;
block|}
DECL|method|screen (final String target)
specifier|private
name|Key
argument_list|<
name|HttpServlet
argument_list|>
name|screen
parameter_list|(
specifier|final
name|String
name|target
parameter_list|)
block|{
return|return
name|key
argument_list|(
operator|new
name|HttpServlet
argument_list|()
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|Override
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
name|toGerrit
argument_list|(
name|target
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|legacyGerritScreen ()
specifier|private
name|Key
argument_list|<
name|HttpServlet
argument_list|>
name|legacyGerritScreen
parameter_list|()
block|{
return|return
name|key
argument_list|(
operator|new
name|HttpServlet
argument_list|()
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|Override
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
name|token
init|=
name|req
operator|.
name|getPathInfo
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|toGerrit
argument_list|(
name|token
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|directChangeById ()
specifier|private
name|Key
argument_list|<
name|HttpServlet
argument_list|>
name|directChangeById
parameter_list|()
block|{
return|return
name|key
argument_list|(
operator|new
name|HttpServlet
argument_list|()
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|Override
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
try|try
block|{
name|Change
operator|.
name|Id
name|id
init|=
name|Change
operator|.
name|Id
operator|.
name|parse
argument_list|(
name|req
operator|.
name|getPathInfo
argument_list|()
argument_list|)
decl_stmt|;
name|toGerrit
argument_list|(
name|PageLinks
operator|.
name|toChange
argument_list|(
name|id
argument_list|)
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|err
parameter_list|)
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
block|}
argument_list|)
return|;
block|}
DECL|method|queryProjectNew ()
specifier|private
name|Key
argument_list|<
name|HttpServlet
argument_list|>
name|queryProjectNew
parameter_list|()
block|{
return|return
name|key
argument_list|(
operator|new
name|HttpServlet
argument_list|()
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|Override
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
name|String
name|name
init|=
name|req
operator|.
name|getPathInfo
argument_list|()
decl_stmt|;
while|while
condition|(
name|name
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|name
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|name
operator|.
name|endsWith
argument_list|(
literal|".git"
argument_list|)
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|name
operator|.
name|length
argument_list|()
operator|-
literal|4
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|name
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|name
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|Project
operator|.
name|NameKey
name|project
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|toGerrit
argument_list|(
name|PageLinks
operator|.
name|toChangeQuery
argument_list|(
name|PageLinks
operator|.
name|projectQuery
argument_list|(
name|project
argument_list|,
name|Change
operator|.
name|Status
operator|.
name|NEW
argument_list|)
argument_list|)
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|query (final String query)
specifier|private
name|Key
argument_list|<
name|HttpServlet
argument_list|>
name|query
parameter_list|(
specifier|final
name|String
name|query
parameter_list|)
block|{
return|return
name|key
argument_list|(
operator|new
name|HttpServlet
argument_list|()
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
annotation|@
name|Override
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
name|toGerrit
argument_list|(
name|PageLinks
operator|.
name|toChangeQuery
argument_list|(
name|query
argument_list|)
argument_list|,
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|key (final HttpServlet servlet)
specifier|private
name|Key
argument_list|<
name|HttpServlet
argument_list|>
name|key
parameter_list|(
specifier|final
name|HttpServlet
name|servlet
parameter_list|)
block|{
specifier|final
name|Key
argument_list|<
name|HttpServlet
argument_list|>
name|srv
init|=
name|Key
operator|.
name|get
argument_list|(
name|HttpServlet
operator|.
name|class
argument_list|,
name|UniqueAnnotations
operator|.
name|create
argument_list|()
argument_list|)
decl_stmt|;
name|bind
argument_list|(
name|srv
argument_list|)
operator|.
name|toProvider
argument_list|(
operator|new
name|Provider
argument_list|<
name|HttpServlet
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|HttpServlet
name|get
parameter_list|()
block|{
return|return
name|servlet
return|;
block|}
block|}
argument_list|)
operator|.
name|in
argument_list|(
name|SINGLETON
argument_list|)
expr_stmt|;
return|return
name|srv
return|;
block|}
DECL|method|toGerrit (final String target, final HttpServletRequest req, final HttpServletResponse rsp)
specifier|static
name|void
name|toGerrit
parameter_list|(
specifier|final
name|String
name|target
parameter_list|,
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
name|StringBuilder
name|url
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|url
operator|.
name|append
argument_list|(
name|req
operator|.
name|getContextPath
argument_list|()
argument_list|)
expr_stmt|;
name|url
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
name|url
operator|.
name|append
argument_list|(
literal|'#'
argument_list|)
expr_stmt|;
name|url
operator|.
name|append
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|sendRedirect
argument_list|(
name|url
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

