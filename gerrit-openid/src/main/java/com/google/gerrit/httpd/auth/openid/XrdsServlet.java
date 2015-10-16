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
DECL|package|com.google.gerrit.httpd.auth.openid
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
name|openid
package|;
end_package

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
name|ServletOutputStream
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
annotation|@
name|Singleton
DECL|class|XrdsServlet
class|class
name|XrdsServlet
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
DECL|field|LOCATION
specifier|static
specifier|final
name|String
name|LOCATION
init|=
literal|"OpenID.XRDS"
decl_stmt|;
DECL|field|url
specifier|private
specifier|final
name|Provider
argument_list|<
name|String
argument_list|>
name|url
decl_stmt|;
annotation|@
name|Inject
DECL|method|XrdsServlet (@anonicalWebUrl final Provider<String> url)
name|XrdsServlet
parameter_list|(
annotation|@
name|CanonicalWebUrl
specifier|final
name|Provider
argument_list|<
name|String
argument_list|>
name|url
parameter_list|)
block|{
name|this
operator|.
name|url
operator|=
name|url
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
literal|"<?xml version=\"1.0\" encoding=\""
argument_list|)
operator|.
name|append
argument_list|(
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\"?>"
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"<xrds:XRDS"
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|" xmlns:xrds=\"xri://$xrds\""
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|" xmlns:openid=\"http://openid.net/xmlns/1.0\""
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|" xmlns=\"xri://$xrd*($v*2.0)\">"
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"<XRD>"
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"<Service priority=\"1\">"
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"<Type>http://specs.openid.net/auth/2.0/return_to</Type>"
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"<URI>"
argument_list|)
operator|.
name|append
argument_list|(
name|url
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|OpenIdServiceImpl
operator|.
name|RETURN_URL
argument_list|)
operator|.
name|append
argument_list|(
literal|"</URI>"
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"</Service>"
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"</XRD>"
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"</xrds:XRDS>"
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
specifier|final
name|byte
index|[]
name|raw
init|=
name|r
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
decl_stmt|;
name|rsp
operator|.
name|setContentLength
argument_list|(
name|raw
operator|.
name|length
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setContentType
argument_list|(
literal|"application/xrds+xml"
argument_list|)
expr_stmt|;
name|rsp
operator|.
name|setCharacterEncoding
argument_list|(
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|ServletOutputStream
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
name|raw
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

