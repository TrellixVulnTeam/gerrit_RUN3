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
DECL|package|com.google.gerrit.httpd.auth.container
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
operator|.
name|auth
operator|.
name|container
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
name|base
operator|.
name|MoreObjects
operator|.
name|firstNonNull
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
name|base
operator|.
name|Strings
operator|.
name|emptyToNull
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
name|AUTHORIZATION
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
name|server
operator|.
name|account
operator|.
name|externalids
operator|.
name|ExternalId
operator|.
name|SCHEME_GERRIT
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
name|ISO_8859_1
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
name|extensions
operator|.
name|registration
operator|.
name|DynamicItem
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
name|RemoteUserUtil
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
name|server
operator|.
name|account
operator|.
name|externalids
operator|.
name|ExternalId
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
name|AuthConfig
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
name|Locale
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|Filter
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterChain
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|FilterConfig
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
name|ServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletResponse
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
comment|/**  * Watches request for the host page and requires login if not yet signed in.  *  *<p>If HTTP authentication has been enabled on this server this filter is bound in front of the  * {@link HostPageServlet} and redirects users who are not yet signed in to visit {@code /login/},  * so the web container can force login. This redirect is performed with JavaScript, such that any  * existing anchor token in the URL can be rewritten and preserved through the authentication  * process of any enterprise single sign-on solutions.  */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|HttpAuthFilter
class|class
name|HttpAuthFilter
implements|implements
name|Filter
block|{
DECL|field|sessionProvider
specifier|private
specifier|final
name|DynamicItem
argument_list|<
name|WebSession
argument_list|>
name|sessionProvider
decl_stmt|;
DECL|field|signInRaw
specifier|private
specifier|final
name|byte
index|[]
name|signInRaw
decl_stmt|;
DECL|field|signInGzip
specifier|private
specifier|final
name|byte
index|[]
name|signInGzip
decl_stmt|;
DECL|field|loginHeader
specifier|private
specifier|final
name|String
name|loginHeader
decl_stmt|;
DECL|field|displaynameHeader
specifier|private
specifier|final
name|String
name|displaynameHeader
decl_stmt|;
DECL|field|emailHeader
specifier|private
specifier|final
name|String
name|emailHeader
decl_stmt|;
DECL|field|externalIdHeader
specifier|private
specifier|final
name|String
name|externalIdHeader
decl_stmt|;
DECL|field|userNameToLowerCase
specifier|private
specifier|final
name|boolean
name|userNameToLowerCase
decl_stmt|;
annotation|@
name|Inject
DECL|method|HttpAuthFilter (DynamicItem<WebSession> webSession, AuthConfig authConfig)
name|HttpAuthFilter
parameter_list|(
name|DynamicItem
argument_list|<
name|WebSession
argument_list|>
name|webSession
parameter_list|,
name|AuthConfig
name|authConfig
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|sessionProvider
operator|=
name|webSession
expr_stmt|;
specifier|final
name|String
name|pageName
init|=
literal|"LoginRedirect.html"
decl_stmt|;
specifier|final
name|String
name|doc
init|=
name|HtmlDomUtil
operator|.
name|readFile
argument_list|(
name|getClass
argument_list|()
argument_list|,
name|pageName
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
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
name|signInRaw
operator|=
name|doc
operator|.
name|getBytes
argument_list|(
name|HtmlDomUtil
operator|.
name|ENC
argument_list|)
expr_stmt|;
name|signInGzip
operator|=
name|HtmlDomUtil
operator|.
name|compress
argument_list|(
name|signInRaw
argument_list|)
expr_stmt|;
name|loginHeader
operator|=
name|firstNonNull
argument_list|(
name|emptyToNull
argument_list|(
name|authConfig
operator|.
name|getLoginHttpHeader
argument_list|()
argument_list|)
argument_list|,
name|AUTHORIZATION
argument_list|)
expr_stmt|;
name|displaynameHeader
operator|=
name|emptyToNull
argument_list|(
name|authConfig
operator|.
name|getHttpDisplaynameHeader
argument_list|()
argument_list|)
expr_stmt|;
name|emailHeader
operator|=
name|emptyToNull
argument_list|(
name|authConfig
operator|.
name|getHttpEmailHeader
argument_list|()
argument_list|)
expr_stmt|;
name|externalIdHeader
operator|=
name|emptyToNull
argument_list|(
name|authConfig
operator|.
name|getHttpExternalIdHeader
argument_list|()
argument_list|)
expr_stmt|;
name|userNameToLowerCase
operator|=
name|authConfig
operator|.
name|isUserNameToLowerCase
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doFilter (final ServletRequest request, ServletResponse response, FilterChain chain)
specifier|public
name|void
name|doFilter
parameter_list|(
specifier|final
name|ServletRequest
name|request
parameter_list|,
name|ServletResponse
name|response
parameter_list|,
name|FilterChain
name|chain
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
if|if
condition|(
name|isSessionValid
argument_list|(
operator|(
name|HttpServletRequest
operator|)
name|request
argument_list|)
condition|)
block|{
name|chain
operator|.
name|doFilter
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Not signed in yet. Since the browser state might have an anchor
comment|// token which we want to capture and carry through the auth process
comment|// we send back JavaScript now to capture that, and do the real work
comment|// of redirecting to the authentication area.
comment|//
specifier|final
name|HttpServletRequest
name|req
init|=
operator|(
name|HttpServletRequest
operator|)
name|request
decl_stmt|;
specifier|final
name|HttpServletResponse
name|rsp
init|=
operator|(
name|HttpServletResponse
operator|)
name|response
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
name|signInGzip
expr_stmt|;
block|}
else|else
block|{
name|tosend
operator|=
name|signInRaw
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
operator|.
name|name
argument_list|()
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
try|try
init|(
name|OutputStream
name|out
init|=
name|rsp
operator|.
name|getOutputStream
argument_list|()
init|)
block|{
name|out
operator|.
name|write
argument_list|(
name|tosend
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|isSessionValid (HttpServletRequest req)
specifier|private
name|boolean
name|isSessionValid
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
name|WebSession
name|session
init|=
name|sessionProvider
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|session
operator|.
name|isSignedIn
argument_list|()
condition|)
block|{
name|String
name|user
init|=
name|getRemoteUser
argument_list|(
name|req
argument_list|)
decl_stmt|;
return|return
name|user
operator|==
literal|null
operator|||
name|correctUser
argument_list|(
name|user
argument_list|,
name|session
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|correctUser (String user, WebSession session)
specifier|private
specifier|static
name|boolean
name|correctUser
parameter_list|(
name|String
name|user
parameter_list|,
name|WebSession
name|session
parameter_list|)
block|{
name|ExternalId
operator|.
name|Key
name|id
init|=
name|session
operator|.
name|getLastLoginExternalId
argument_list|()
decl_stmt|;
return|return
name|id
operator|!=
literal|null
operator|&&
name|id
operator|.
name|equals
argument_list|(
name|ExternalId
operator|.
name|Key
operator|.
name|create
argument_list|(
name|SCHEME_GERRIT
argument_list|,
name|user
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getRemoteUser (HttpServletRequest req)
name|String
name|getRemoteUser
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
name|String
name|remoteUser
init|=
name|RemoteUserUtil
operator|.
name|getRemoteUser
argument_list|(
name|req
argument_list|,
name|loginHeader
argument_list|)
decl_stmt|;
return|return
operator|(
name|userNameToLowerCase
operator|&&
name|remoteUser
operator|!=
literal|null
operator|)
condition|?
name|remoteUser
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
else|:
name|remoteUser
return|;
block|}
DECL|method|getRemoteDisplayname (HttpServletRequest req)
name|String
name|getRemoteDisplayname
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
if|if
condition|(
name|displaynameHeader
operator|!=
literal|null
condition|)
block|{
name|String
name|raw
init|=
name|req
operator|.
name|getHeader
argument_list|(
name|displaynameHeader
argument_list|)
decl_stmt|;
return|return
name|emptyToNull
argument_list|(
operator|new
name|String
argument_list|(
name|raw
operator|.
name|getBytes
argument_list|(
name|ISO_8859_1
argument_list|)
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getRemoteEmail (HttpServletRequest req)
name|String
name|getRemoteEmail
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
if|if
condition|(
name|emailHeader
operator|!=
literal|null
condition|)
block|{
return|return
name|emptyToNull
argument_list|(
name|req
operator|.
name|getHeader
argument_list|(
name|emailHeader
argument_list|)
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getRemoteExternalIdToken (HttpServletRequest req)
name|String
name|getRemoteExternalIdToken
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
if|if
condition|(
name|externalIdHeader
operator|!=
literal|null
condition|)
block|{
return|return
name|emptyToNull
argument_list|(
name|req
operator|.
name|getHeader
argument_list|(
name|externalIdHeader
argument_list|)
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|getLoginHeader ()
name|String
name|getLoginHeader
parameter_list|()
block|{
return|return
name|loginHeader
return|;
block|}
annotation|@
name|Override
DECL|method|init (FilterConfig filterConfig)
specifier|public
name|void
name|init
parameter_list|(
name|FilterConfig
name|filterConfig
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{}
block|}
end_class

end_unit

