begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
name|truth
operator|.
name|Truth
operator|.
name|assertThat
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
name|testing
operator|.
name|GerritBaseTests
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|IndexServletTest
specifier|public
class|class
name|IndexServletTest
extends|extends
name|GerritBaseTests
block|{
DECL|class|TestIndexServlet
specifier|static
class|class
name|TestIndexServlet
extends|extends
name|IndexServlet
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
DECL|method|TestIndexServlet (String canonicalURL, String cdnPath, String faviconPath)
name|TestIndexServlet
parameter_list|(
name|String
name|canonicalURL
parameter_list|,
name|String
name|cdnPath
parameter_list|,
name|String
name|faviconPath
parameter_list|)
throws|throws
name|URISyntaxException
block|{
name|super
argument_list|(
name|canonicalURL
argument_list|,
name|cdnPath
argument_list|,
name|faviconPath
argument_list|)
expr_stmt|;
block|}
DECL|method|getIndexSource ()
name|String
name|getIndexSource
parameter_list|()
block|{
return|return
operator|new
name|String
argument_list|(
name|indexSource
argument_list|,
name|UTF_8
argument_list|)
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|noPathAndNoCDN ()
specifier|public
name|void
name|noPathAndNoCDN
parameter_list|()
throws|throws
name|URISyntaxException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|data
init|=
name|IndexServlet
operator|.
name|getTemplateData
argument_list|(
literal|"http://example.com/"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|data
operator|.
name|get
argument_list|(
literal|"canonicalPath"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|data
operator|.
name|get
argument_list|(
literal|"staticResourcePath"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|pathAndNoCDN ()
specifier|public
name|void
name|pathAndNoCDN
parameter_list|()
throws|throws
name|URISyntaxException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|data
init|=
name|IndexServlet
operator|.
name|getTemplateData
argument_list|(
literal|"http://example.com/gerrit/"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|data
operator|.
name|get
argument_list|(
literal|"canonicalPath"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"/gerrit"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|data
operator|.
name|get
argument_list|(
literal|"staticResourcePath"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"/gerrit"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|noPathAndCDN ()
specifier|public
name|void
name|noPathAndCDN
parameter_list|()
throws|throws
name|URISyntaxException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|data
init|=
name|IndexServlet
operator|.
name|getTemplateData
argument_list|(
literal|"http://example.com/"
argument_list|,
literal|"http://my-cdn.com/foo/bar/"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|data
operator|.
name|get
argument_list|(
literal|"canonicalPath"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|data
operator|.
name|get
argument_list|(
literal|"staticResourcePath"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"http://my-cdn.com/foo/bar/"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|pathAndCDN ()
specifier|public
name|void
name|pathAndCDN
parameter_list|()
throws|throws
name|URISyntaxException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|data
init|=
name|IndexServlet
operator|.
name|getTemplateData
argument_list|(
literal|"http://example.com/gerrit"
argument_list|,
literal|"http://my-cdn.com/foo/bar/"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|data
operator|.
name|get
argument_list|(
literal|"canonicalPath"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"/gerrit"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|data
operator|.
name|get
argument_list|(
literal|"staticResourcePath"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"http://my-cdn.com/foo/bar/"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|renderTemplate ()
specifier|public
name|void
name|renderTemplate
parameter_list|()
throws|throws
name|URISyntaxException
block|{
name|String
name|testCanonicalUrl
init|=
literal|"foo-url"
decl_stmt|;
name|String
name|testCdnPath
init|=
literal|"bar-cdn"
decl_stmt|;
name|String
name|testFaviconURL
init|=
literal|"zaz-url"
decl_stmt|;
name|TestIndexServlet
name|servlet
init|=
operator|new
name|TestIndexServlet
argument_list|(
name|testCanonicalUrl
argument_list|,
name|testCdnPath
argument_list|,
name|testFaviconURL
argument_list|)
decl_stmt|;
name|String
name|output
init|=
name|servlet
operator|.
name|getIndexSource
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|output
argument_list|)
operator|.
name|contains
argument_list|(
literal|"<!DOCTYPE html>"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|output
argument_list|)
operator|.
name|contains
argument_list|(
literal|"window.CANONICAL_PATH = '"
operator|+
name|testCanonicalUrl
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|output
argument_list|)
operator|.
name|contains
argument_list|(
literal|"<link rel=\"preload\" href=\""
operator|+
name|testCdnPath
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|output
argument_list|)
operator|.
name|contains
argument_list|(
literal|"<link rel=\"icon\" type=\"image/x-icon\" href=\""
operator|+
name|testCanonicalUrl
operator|+
literal|"/"
operator|+
name|testFaviconURL
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

