begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
name|Cookie
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
DECL|class|XsrfCookieFilter
specifier|public
class|class
name|XsrfCookieFilter
implements|implements
name|Filter
block|{
DECL|field|user
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
decl_stmt|;
DECL|field|session
specifier|private
specifier|final
name|DynamicItem
argument_list|<
name|WebSession
argument_list|>
name|session
decl_stmt|;
annotation|@
name|Inject
DECL|method|XsrfCookieFilter ( Provider<CurrentUser> user, DynamicItem<WebSession> session)
name|XsrfCookieFilter
parameter_list|(
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
parameter_list|,
name|DynamicItem
argument_list|<
name|WebSession
argument_list|>
name|session
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doFilter (ServletRequest req, ServletResponse rsp, FilterChain chain)
specifier|public
name|void
name|doFilter
parameter_list|(
name|ServletRequest
name|req
parameter_list|,
name|ServletResponse
name|rsp
parameter_list|,
name|FilterChain
name|chain
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
name|WebSession
name|s
init|=
name|user
operator|.
name|get
argument_list|()
operator|.
name|isIdentifiedUser
argument_list|()
condition|?
name|session
operator|.
name|get
argument_list|()
else|:
literal|null
decl_stmt|;
name|setXsrfTokenCookie
argument_list|(
operator|(
name|HttpServletRequest
operator|)
name|req
argument_list|,
operator|(
name|HttpServletResponse
operator|)
name|rsp
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|chain
operator|.
name|doFilter
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
DECL|method|setXsrfTokenCookie (HttpServletRequest req, HttpServletResponse rsp, WebSession session)
specifier|private
specifier|static
name|void
name|setXsrfTokenCookie
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|rsp
parameter_list|,
name|WebSession
name|session
parameter_list|)
block|{
name|String
name|v
init|=
name|session
operator|!=
literal|null
condition|?
name|session
operator|.
name|getXGerritAuth
argument_list|()
else|:
literal|""
decl_stmt|;
name|Cookie
name|c
init|=
operator|new
name|Cookie
argument_list|(
name|HostPageData
operator|.
name|XSRF_COOKIE_NAME
argument_list|,
name|v
argument_list|)
decl_stmt|;
name|c
operator|.
name|setPath
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|c
operator|.
name|setSecure
argument_list|(
name|isSecure
argument_list|(
name|req
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|setMaxAge
argument_list|(
name|session
operator|!=
literal|null
condition|?
operator|-
literal|1
comment|// Set the cookie for this browser session.
else|:
literal|0
argument_list|)
expr_stmt|;
comment|// Remove the cookie (expire immediately).
name|rsp
operator|.
name|addCookie
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
DECL|method|isSecure (HttpServletRequest req)
specifier|private
specifier|static
name|boolean
name|isSecure
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
return|return
name|req
operator|.
name|isSecure
argument_list|()
operator|||
literal|"https"
operator|.
name|equals
argument_list|(
name|req
operator|.
name|getScheme
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|init (FilterConfig config)
specifier|public
name|void
name|init
parameter_list|(
name|FilterConfig
name|config
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{   }
block|}
end_class

end_unit

