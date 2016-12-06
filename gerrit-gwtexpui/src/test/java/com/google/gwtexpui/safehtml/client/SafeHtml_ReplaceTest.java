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
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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

begin_class
DECL|class|SafeHtml_ReplaceTest
specifier|public
class|class
name|SafeHtml_ReplaceTest
block|{
annotation|@
name|Test
DECL|method|replaceEmpty ()
specifier|public
name|void
name|replaceEmpty
parameter_list|()
block|{
name|SafeHtml
name|o
init|=
name|html
argument_list|(
literal|"A\nissue42\nB"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|o
operator|.
name|replaceAll
argument_list|(
literal|null
argument_list|)
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|o
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|o
operator|.
name|replaceAll
argument_list|(
name|Collections
operator|.
expr|<
name|FindReplace
operator|>
name|emptyList
argument_list|()
argument_list|)
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|replaceOneLink ()
specifier|public
name|void
name|replaceOneLink
parameter_list|()
block|{
name|SafeHtml
name|o
init|=
name|html
argument_list|(
literal|"A\nissue 42\nB"
argument_list|)
decl_stmt|;
name|SafeHtml
name|n
init|=
name|o
operator|.
name|replaceAll
argument_list|(
name|repls
argument_list|(
operator|new
name|RawFindReplace
argument_list|(
literal|"(issue\\s(\\d+))"
argument_list|,
literal|"<a href=\"?$2\">$1</a>"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|o
argument_list|)
operator|.
name|isNotSameAs
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|n
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"A\n<a href=\"?42\">issue 42</a>\nB"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|replaceNoLeadingOrTrailingText ()
specifier|public
name|void
name|replaceNoLeadingOrTrailingText
parameter_list|()
block|{
name|SafeHtml
name|o
init|=
name|html
argument_list|(
literal|"issue 42"
argument_list|)
decl_stmt|;
name|SafeHtml
name|n
init|=
name|o
operator|.
name|replaceAll
argument_list|(
name|repls
argument_list|(
operator|new
name|RawFindReplace
argument_list|(
literal|"(issue\\s(\\d+))"
argument_list|,
literal|"<a href=\"?$2\">$1</a>"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|o
argument_list|)
operator|.
name|isNotSameAs
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|n
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"<a href=\"?42\">issue 42</a>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|replaceTwoLinks ()
specifier|public
name|void
name|replaceTwoLinks
parameter_list|()
block|{
name|SafeHtml
name|o
init|=
name|html
argument_list|(
literal|"A\nissue 42\nissue 9918\nB"
argument_list|)
decl_stmt|;
name|SafeHtml
name|n
init|=
name|o
operator|.
name|replaceAll
argument_list|(
name|repls
argument_list|(
operator|new
name|RawFindReplace
argument_list|(
literal|"(issue\\s(\\d+))"
argument_list|,
literal|"<a href=\"?$2\">$1</a>"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|o
argument_list|)
operator|.
name|isNotSameAs
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|n
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"A\n"
operator|+
literal|"<a href=\"?42\">issue 42</a>\n"
operator|+
literal|"<a href=\"?9918\">issue 9918</a>\n"
operator|+
literal|"B"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|replaceInOrder ()
specifier|public
name|void
name|replaceInOrder
parameter_list|()
block|{
name|SafeHtml
name|o
init|=
name|html
argument_list|(
literal|"A\nissue 42\nReally GWTEXPUI-9918 is better\nB"
argument_list|)
decl_stmt|;
name|SafeHtml
name|n
init|=
name|o
operator|.
name|replaceAll
argument_list|(
name|repls
argument_list|(
operator|new
name|RawFindReplace
argument_list|(
literal|"(GWTEXPUI-(\\d+))"
argument_list|,
literal|"<a href=\"gwtexpui-bug?$2\">$1</a>"
argument_list|)
argument_list|,
operator|new
name|RawFindReplace
argument_list|(
literal|"(issue\\s+(\\d+))"
argument_list|,
literal|"<a href=\"generic-bug?$2\">$1</a>"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|o
argument_list|)
operator|.
name|isNotSameAs
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|n
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"A\n"
operator|+
literal|"<a href=\"generic-bug?42\">issue 42</a>\n"
operator|+
literal|"Really<a href=\"gwtexpui-bug?9918\">GWTEXPUI-9918</a> is better\n"
operator|+
literal|"B"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|replaceOverlappingAfterFirstChar ()
specifier|public
name|void
name|replaceOverlappingAfterFirstChar
parameter_list|()
block|{
name|SafeHtml
name|o
init|=
name|html
argument_list|(
literal|"abcd"
argument_list|)
decl_stmt|;
name|RawFindReplace
name|ab
init|=
operator|new
name|RawFindReplace
argument_list|(
literal|"ab"
argument_list|,
literal|"AB"
argument_list|)
decl_stmt|;
name|RawFindReplace
name|bc
init|=
operator|new
name|RawFindReplace
argument_list|(
literal|"bc"
argument_list|,
literal|"23"
argument_list|)
decl_stmt|;
name|RawFindReplace
name|cd
init|=
operator|new
name|RawFindReplace
argument_list|(
literal|"cd"
argument_list|,
literal|"YZ"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|o
operator|.
name|replaceAll
argument_list|(
name|repls
argument_list|(
name|ab
argument_list|,
name|bc
argument_list|)
argument_list|)
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"ABcd"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|o
operator|.
name|replaceAll
argument_list|(
name|repls
argument_list|(
name|bc
argument_list|,
name|ab
argument_list|)
argument_list|)
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"ABcd"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|o
operator|.
name|replaceAll
argument_list|(
name|repls
argument_list|(
name|ab
argument_list|,
name|bc
argument_list|,
name|cd
argument_list|)
argument_list|)
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"ABYZ"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|replaceOverlappingAtFirstCharLongestMatch ()
specifier|public
name|void
name|replaceOverlappingAtFirstCharLongestMatch
parameter_list|()
block|{
name|SafeHtml
name|o
init|=
name|html
argument_list|(
literal|"abcd"
argument_list|)
decl_stmt|;
name|RawFindReplace
name|ab
init|=
operator|new
name|RawFindReplace
argument_list|(
literal|"ab"
argument_list|,
literal|"AB"
argument_list|)
decl_stmt|;
name|RawFindReplace
name|abc
init|=
operator|new
name|RawFindReplace
argument_list|(
literal|"[^d][^d][^d]"
argument_list|,
literal|"234"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|o
operator|.
name|replaceAll
argument_list|(
name|repls
argument_list|(
name|ab
argument_list|,
name|abc
argument_list|)
argument_list|)
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"ABcd"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|o
operator|.
name|replaceAll
argument_list|(
name|repls
argument_list|(
name|abc
argument_list|,
name|ab
argument_list|)
argument_list|)
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"234d"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|replaceOverlappingAtFirstCharFirstMatch ()
specifier|public
name|void
name|replaceOverlappingAtFirstCharFirstMatch
parameter_list|()
block|{
name|SafeHtml
name|o
init|=
name|html
argument_list|(
literal|"abcd"
argument_list|)
decl_stmt|;
name|RawFindReplace
name|ab1
init|=
operator|new
name|RawFindReplace
argument_list|(
literal|"ab"
argument_list|,
literal|"AB"
argument_list|)
decl_stmt|;
name|RawFindReplace
name|ab2
init|=
operator|new
name|RawFindReplace
argument_list|(
literal|"[^cd][^cd]"
argument_list|,
literal|"12"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|o
operator|.
name|replaceAll
argument_list|(
name|repls
argument_list|(
name|ab1
argument_list|,
name|ab2
argument_list|)
argument_list|)
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"ABcd"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|o
operator|.
name|replaceAll
argument_list|(
name|repls
argument_list|(
name|ab2
argument_list|,
name|ab1
argument_list|)
argument_list|)
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"12cd"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|failedSanitization ()
specifier|public
name|void
name|failedSanitization
parameter_list|()
block|{
name|SafeHtml
name|o
init|=
name|html
argument_list|(
literal|"abcd"
argument_list|)
decl_stmt|;
name|LinkFindReplace
name|evil
init|=
operator|new
name|LinkFindReplace
argument_list|(
literal|"(b)"
argument_list|,
literal|"javascript:alert('$1')"
argument_list|)
decl_stmt|;
name|LinkFindReplace
name|ok
init|=
operator|new
name|LinkFindReplace
argument_list|(
literal|"(b)"
argument_list|,
literal|"/$1"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|o
operator|.
name|replaceAll
argument_list|(
name|repls
argument_list|(
name|evil
argument_list|)
argument_list|)
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"abcd"
argument_list|)
expr_stmt|;
name|String
name|linked
init|=
literal|"a<a href=\"/b\">b</a>cd"
decl_stmt|;
name|assertThat
argument_list|(
name|o
operator|.
name|replaceAll
argument_list|(
name|repls
argument_list|(
name|ok
argument_list|)
argument_list|)
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|linked
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|o
operator|.
name|replaceAll
argument_list|(
name|repls
argument_list|(
name|evil
argument_list|,
name|ok
argument_list|)
argument_list|)
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|linked
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
DECL|method|repls (FindReplace... repls)
specifier|private
specifier|static
name|List
argument_list|<
name|FindReplace
argument_list|>
name|repls
parameter_list|(
name|FindReplace
modifier|...
name|repls
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|repls
argument_list|)
return|;
block|}
block|}
end_class

end_unit

