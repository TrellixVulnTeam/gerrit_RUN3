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
DECL|package|com.google.gwtexpui.safehtml.client
package|package
name|com
operator|.
name|google
operator|.
name|gwtexpui
operator|.
name|safehtml
operator|.
name|client
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotSame
import|;
end_import

begin_class
DECL|class|SafeHtml_LinkifyTest
specifier|public
class|class
name|SafeHtml_LinkifyTest
block|{
annotation|@
name|Test
DECL|method|testLinkify_SimpleHttp1 ()
specifier|public
name|void
name|testLinkify_SimpleHttp1
parameter_list|()
block|{
specifier|final
name|SafeHtml
name|o
init|=
name|html
argument_list|(
literal|"A http://go.here/ B"
argument_list|)
decl_stmt|;
specifier|final
name|SafeHtml
name|n
init|=
name|o
operator|.
name|linkify
argument_list|()
decl_stmt|;
name|assertNotSame
argument_list|(
name|o
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"A<a href=\"http://go.here/\" target=\"_blank\">http://go.here/</a> B"
argument_list|,
name|n
operator|.
name|asString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLinkify_SimpleHttps2 ()
specifier|public
name|void
name|testLinkify_SimpleHttps2
parameter_list|()
block|{
specifier|final
name|SafeHtml
name|o
init|=
name|html
argument_list|(
literal|"A https://go.here/ B"
argument_list|)
decl_stmt|;
specifier|final
name|SafeHtml
name|n
init|=
name|o
operator|.
name|linkify
argument_list|()
decl_stmt|;
name|assertNotSame
argument_list|(
name|o
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"A<a href=\"https://go.here/\" target=\"_blank\">https://go.here/</a> B"
argument_list|,
name|n
operator|.
name|asString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLinkify_Parens1 ()
specifier|public
name|void
name|testLinkify_Parens1
parameter_list|()
block|{
specifier|final
name|SafeHtml
name|o
init|=
name|html
argument_list|(
literal|"A (http://go.here/) B"
argument_list|)
decl_stmt|;
specifier|final
name|SafeHtml
name|n
init|=
name|o
operator|.
name|linkify
argument_list|()
decl_stmt|;
name|assertNotSame
argument_list|(
name|o
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"A (<a href=\"http://go.here/\" target=\"_blank\">http://go.here/</a>) B"
argument_list|,
name|n
operator|.
name|asString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLinkify_Parens ()
specifier|public
name|void
name|testLinkify_Parens
parameter_list|()
block|{
specifier|final
name|SafeHtml
name|o
init|=
name|html
argument_list|(
literal|"A http://go.here/#m() B"
argument_list|)
decl_stmt|;
specifier|final
name|SafeHtml
name|n
init|=
name|o
operator|.
name|linkify
argument_list|()
decl_stmt|;
name|assertNotSame
argument_list|(
name|o
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"A<a href=\"http://go.here/#m()\" target=\"_blank\">http://go.here/#m()</a> B"
argument_list|,
name|n
operator|.
name|asString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLinkify_AngleBrackets1 ()
specifier|public
name|void
name|testLinkify_AngleBrackets1
parameter_list|()
block|{
specifier|final
name|SafeHtml
name|o
init|=
name|html
argument_list|(
literal|"A<http://go.here/> B"
argument_list|)
decl_stmt|;
specifier|final
name|SafeHtml
name|n
init|=
name|o
operator|.
name|linkify
argument_list|()
decl_stmt|;
name|assertNotSame
argument_list|(
name|o
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"A&lt;<a href=\"http://go.here/\" target=\"_blank\">http://go.here/</a>&gt; B"
argument_list|,
name|n
operator|.
name|asString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|html (String text)
specifier|private
specifier|static
name|SafeHtml
name|html
parameter_list|(
name|String
name|text
parameter_list|)
block|{
return|return
operator|new
name|SafeHtmlBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|text
argument_list|)
operator|.
name|toSafeHtml
argument_list|()
return|;
block|}
block|}
end_class

end_unit

