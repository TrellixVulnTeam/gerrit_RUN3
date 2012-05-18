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
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|RequestCleanup
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
name|util
operator|.
name|RequestContext
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
name|util
operator|.
name|ThreadLocalRequestContext
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
name|Module
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

begin_comment
comment|/** Executes any pending {@link RequestCleanup} at the end of a request. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|RequestContextFilter
specifier|public
class|class
name|RequestContextFilter
implements|implements
name|Filter
block|{
DECL|method|module ()
specifier|public
specifier|static
name|Module
name|module
parameter_list|()
block|{
return|return
operator|new
name|ServletModule
argument_list|()
block|{
annotation|@
name|Override
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
name|RequestContextFilter
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
DECL|field|cleanup
specifier|private
specifier|final
name|Provider
argument_list|<
name|RequestCleanup
argument_list|>
name|cleanup
decl_stmt|;
DECL|field|requestContext
specifier|private
specifier|final
name|Provider
argument_list|<
name|HttpRequestContext
argument_list|>
name|requestContext
decl_stmt|;
DECL|field|local
specifier|private
specifier|final
name|ThreadLocalRequestContext
name|local
decl_stmt|;
annotation|@
name|Inject
DECL|method|RequestContextFilter (final Provider<RequestCleanup> r, final Provider<HttpRequestContext> c, final ThreadLocalRequestContext l)
name|RequestContextFilter
parameter_list|(
specifier|final
name|Provider
argument_list|<
name|RequestCleanup
argument_list|>
name|r
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|HttpRequestContext
argument_list|>
name|c
parameter_list|,
specifier|final
name|ThreadLocalRequestContext
name|l
parameter_list|)
block|{
name|cleanup
operator|=
name|r
expr_stmt|;
name|requestContext
operator|=
name|c
expr_stmt|;
name|local
operator|=
name|l
expr_stmt|;
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
block|{   }
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|doFilter (final ServletRequest request, final ServletResponse response, final FilterChain chain)
specifier|public
name|void
name|doFilter
parameter_list|(
specifier|final
name|ServletRequest
name|request
parameter_list|,
specifier|final
name|ServletResponse
name|response
parameter_list|,
specifier|final
name|FilterChain
name|chain
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
name|RequestContext
name|old
init|=
name|local
operator|.
name|setContext
argument_list|(
name|requestContext
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
try|try
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
finally|finally
block|{
name|cleanup
operator|.
name|get
argument_list|()
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|local
operator|.
name|setContext
argument_list|(
name|old
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

