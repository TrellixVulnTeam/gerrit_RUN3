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
name|account
operator|.
name|AccountException
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
name|AccountManager
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
name|AuthRequest
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
name|AuthResult
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|X509Certificate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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

begin_class
annotation|@
name|Singleton
DECL|class|HttpsClientSslCertAuthFilter
class|class
name|HttpsClientSslCertAuthFilter
implements|implements
name|Filter
block|{
DECL|field|REGEX_USERID
specifier|private
specifier|static
specifier|final
name|Pattern
name|REGEX_USERID
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"CN=([^,]*)"
argument_list|)
decl_stmt|;
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
name|HttpsClientSslCertAuthFilter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|webSession
specifier|private
specifier|final
name|DynamicItem
argument_list|<
name|WebSession
argument_list|>
name|webSession
decl_stmt|;
DECL|field|accountManager
specifier|private
specifier|final
name|AccountManager
name|accountManager
decl_stmt|;
annotation|@
name|Inject
DECL|method|HttpsClientSslCertAuthFilter (final DynamicItem<WebSession> webSession, final AccountManager accountManager)
name|HttpsClientSslCertAuthFilter
parameter_list|(
specifier|final
name|DynamicItem
argument_list|<
name|WebSession
argument_list|>
name|webSession
parameter_list|,
specifier|final
name|AccountManager
name|accountManager
parameter_list|)
block|{
name|this
operator|.
name|webSession
operator|=
name|webSession
expr_stmt|;
name|this
operator|.
name|accountManager
operator|=
name|accountManager
expr_stmt|;
block|}
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
name|X509Certificate
index|[]
name|certs
init|=
operator|(
name|X509Certificate
index|[]
operator|)
name|req
operator|.
name|getAttribute
argument_list|(
literal|"javax.servlet.request.X509Certificate"
argument_list|)
decl_stmt|;
if|if
condition|(
name|certs
operator|==
literal|null
operator|||
name|certs
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Couldn't get the attribute javax.servlet.request.X509Certificate from the request"
argument_list|)
throw|;
block|}
name|String
name|name
init|=
name|certs
index|[
literal|0
index|]
operator|.
name|getSubjectDN
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|Matcher
name|m
init|=
name|REGEX_USERID
operator|.
name|matcher
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|String
name|userName
decl_stmt|;
if|if
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
name|userName
operator|=
name|m
operator|.
name|group
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Couldn't extract username from your certificate"
argument_list|)
throw|;
block|}
specifier|final
name|AuthRequest
name|areq
init|=
name|AuthRequest
operator|.
name|forUser
argument_list|(
name|userName
argument_list|)
decl_stmt|;
specifier|final
name|AuthResult
name|arsp
decl_stmt|;
try|try
block|{
name|arsp
operator|=
name|accountManager
operator|.
name|authenticate
argument_list|(
name|areq
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AccountException
name|e
parameter_list|)
block|{
name|String
name|err
init|=
literal|"Unable to authenticate user \""
operator|+
name|userName
operator|+
literal|"\""
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|err
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ServletException
argument_list|(
name|err
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|webSession
operator|.
name|get
argument_list|()
operator|.
name|login
argument_list|(
name|arsp
argument_list|,
literal|true
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
annotation|@
name|Override
DECL|method|init (FilterConfig arg0)
specifier|public
name|void
name|init
parameter_list|(
name|FilterConfig
name|arg0
parameter_list|)
throws|throws
name|ServletException
block|{   }
block|}
end_class

end_unit

