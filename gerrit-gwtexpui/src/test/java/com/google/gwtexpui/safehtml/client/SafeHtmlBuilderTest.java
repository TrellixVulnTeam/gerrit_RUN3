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
name|Rule
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
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|ExpectedException
import|;
end_import

begin_class
DECL|class|SafeHtmlBuilderTest
specifier|public
class|class
name|SafeHtmlBuilderTest
block|{
annotation|@
name|Rule
DECL|field|exception
specifier|public
name|ExpectedException
name|exception
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testEmpty ()
specifier|public
name|void
name|testEmpty
parameter_list|()
block|{
specifier|final
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|isEmpty
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|hasContent
argument_list|()
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|hasContent
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testToSafeHtml ()
specifier|public
name|void
name|testToSafeHtml
parameter_list|()
block|{
specifier|final
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|SafeHtml
name|h
init|=
name|b
operator|.
name|toSafeHtml
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|h
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|h
argument_list|)
operator|.
name|isNotSameAs
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|h
argument_list|)
operator|.
name|isNotInstanceOf
argument_list|(
name|SafeHtmlBuilder
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|h
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppend_boolean ()
specifier|public
name|void
name|testAppend_boolean
parameter_list|()
block|{
specifier|final
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"truefalse"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppend_char ()
specifier|public
name|void
name|testAppend_char
parameter_list|()
block|{
specifier|final
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
literal|'a'
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
literal|'b'
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"ab"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppend_int ()
specifier|public
name|void
name|testAppend_int
parameter_list|()
block|{
specifier|final
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
operator|-
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"42-100"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppend_long ()
specifier|public
name|void
name|testAppend_long
parameter_list|()
block|{
specifier|final
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
literal|4L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"42"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppend_float ()
specifier|public
name|void
name|testAppend_float
parameter_list|()
block|{
specifier|final
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
literal|0.0f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"0.0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppend_double ()
specifier|public
name|void
name|testAppend_double
parameter_list|()
block|{
specifier|final
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
literal|0.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"0.0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppend_String ()
specifier|public
name|void
name|testAppend_String
parameter_list|()
block|{
specifier|final
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
operator|(
name|String
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"foobar"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppend_StringBuilder ()
specifier|public
name|void
name|testAppend_StringBuilder
parameter_list|()
block|{
specifier|final
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
operator|(
name|StringBuilder
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
operator|new
name|StringBuilder
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
operator|new
name|StringBuilder
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"foobar"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppend_StringBuffer ()
specifier|public
name|void
name|testAppend_StringBuffer
parameter_list|()
block|{
specifier|final
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
operator|(
name|StringBuffer
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
operator|new
name|StringBuffer
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
operator|new
name|StringBuffer
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"foobar"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppend_Object ()
specifier|public
name|void
name|testAppend_Object
parameter_list|()
block|{
specifier|final
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
operator|(
name|Object
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
operator|new
name|Object
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"foobar"
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"foobar"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppend_CharSequence ()
specifier|public
name|void
name|testAppend_CharSequence
parameter_list|()
block|{
specifier|final
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
operator|(
name|CharSequence
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
operator|(
name|CharSequence
operator|)
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
operator|(
name|CharSequence
operator|)
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"foobar"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppend_SafeHtml ()
specifier|public
name|void
name|testAppend_SafeHtml
parameter_list|()
block|{
specifier|final
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
operator|(
name|SafeHtml
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
operator|new
name|SafeHtmlString
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
operator|new
name|SafeHtmlBuilder
argument_list|()
operator|.
name|append
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"foobar"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHtmlSpecialCharacters ()
specifier|public
name|void
name|testHtmlSpecialCharacters
parameter_list|()
block|{
name|assertThat
argument_list|(
name|escape
argument_list|(
literal|"&"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"&amp;"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|escape
argument_list|(
literal|"<"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"&lt;"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|escape
argument_list|(
literal|">"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"&gt;"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|escape
argument_list|(
literal|"\""
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"&quot;"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|escape
argument_list|(
literal|"'"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"&#39;"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|escape
argument_list|(
literal|'&'
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"&amp;"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|escape
argument_list|(
literal|'<'
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"&lt;"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|escape
argument_list|(
literal|'>'
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"&gt;"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|escape
argument_list|(
literal|'"'
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"&quot;"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|escape
argument_list|(
literal|'\''
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"&#39;"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|escape
argument_list|(
literal|"<b>"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"&lt;b&gt;"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|escape
argument_list|(
literal|"&lt;b&gt;"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"&amp;lt;b&amp;gt;"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEntityNbsp ()
specifier|public
name|void
name|testEntityNbsp
parameter_list|()
block|{
specifier|final
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|nbsp
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"&nbsp;"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTagBr ()
specifier|public
name|void
name|testTagBr
parameter_list|()
block|{
specifier|final
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|br
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"<br />"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTagTableTrTd ()
specifier|public
name|void
name|testTagTableTrTd
parameter_list|()
block|{
specifier|final
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|openElement
argument_list|(
literal|"table"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|openTr
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|openTd
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
literal|"d<a>ta"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|closeTd
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|closeTr
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|closeElement
argument_list|(
literal|"table"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"<table><tr><td>d&lt;a&gt;ta</td></tr></table>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTagDiv ()
specifier|public
name|void
name|testTagDiv
parameter_list|()
block|{
specifier|final
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|openDiv
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
literal|"d<a>ta"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|closeDiv
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"<div>d&lt;a&gt;ta</div>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTagAnchor ()
specifier|public
name|void
name|testTagAnchor
parameter_list|()
block|{
specifier|final
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|openAnchor
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|getAttribute
argument_list|(
literal|"href"
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|setAttribute
argument_list|(
literal|"href"
argument_list|,
literal|"http://here"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|getAttribute
argument_list|(
literal|"href"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"http://here"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|setAttribute
argument_list|(
literal|"href"
argument_list|,
literal|"d<a>ta"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|getAttribute
argument_list|(
literal|"href"
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"d<a>ta"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|getAttribute
argument_list|(
literal|"target"
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|setAttribute
argument_list|(
literal|"target"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|getAttribute
argument_list|(
literal|"target"
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
literal|"go"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|closeAnchor
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"<a href=\"d&lt;a&gt;ta\">go</a>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTagHeightWidth ()
specifier|public
name|void
name|testTagHeightWidth
parameter_list|()
block|{
specifier|final
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|openElement
argument_list|(
literal|"img"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|setHeight
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|setWidth
argument_list|(
literal|42
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|closeSelf
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"<img height=\"100\" width=\"42\" />"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStyleName ()
specifier|public
name|void
name|testStyleName
parameter_list|()
block|{
specifier|final
name|SafeHtmlBuilder
name|b
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|openSpan
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|setStyleName
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|addStyleName
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|append
argument_list|(
literal|"d<a>ta"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
argument_list|)
operator|.
name|isSameAs
argument_list|(
name|b
operator|.
name|closeSpan
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|asString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"<span class=\"foo bar\">d&lt;a&gt;ta</span>"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRejectJavaScript_AnchorHref ()
specifier|public
name|void
name|testRejectJavaScript_AnchorHref
parameter_list|()
block|{
specifier|final
name|String
name|href
init|=
literal|"javascript:window.close();"
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|RuntimeException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"javascript unsafe in href: "
operator|+
name|href
argument_list|)
expr_stmt|;
operator|new
name|SafeHtmlBuilder
argument_list|()
operator|.
name|openAnchor
argument_list|()
operator|.
name|setAttribute
argument_list|(
literal|"href"
argument_list|,
name|href
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRejectJavaScript_ImgSrc ()
specifier|public
name|void
name|testRejectJavaScript_ImgSrc
parameter_list|()
block|{
specifier|final
name|String
name|href
init|=
literal|"javascript:window.close();"
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|RuntimeException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"javascript unsafe in href: "
operator|+
name|href
argument_list|)
expr_stmt|;
operator|new
name|SafeHtmlBuilder
argument_list|()
operator|.
name|openElement
argument_list|(
literal|"img"
argument_list|)
operator|.
name|setAttribute
argument_list|(
literal|"src"
argument_list|,
name|href
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRejectJavaScript_FormAction ()
specifier|public
name|void
name|testRejectJavaScript_FormAction
parameter_list|()
block|{
specifier|final
name|String
name|href
init|=
literal|"javascript:window.close();"
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|RuntimeException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"javascript unsafe in href: "
operator|+
name|href
argument_list|)
expr_stmt|;
operator|new
name|SafeHtmlBuilder
argument_list|()
operator|.
name|openElement
argument_list|(
literal|"form"
argument_list|)
operator|.
name|setAttribute
argument_list|(
literal|"action"
argument_list|,
name|href
argument_list|)
expr_stmt|;
block|}
DECL|method|escape (final char c)
specifier|private
specifier|static
name|String
name|escape
parameter_list|(
specifier|final
name|char
name|c
parameter_list|)
block|{
return|return
operator|new
name|SafeHtmlBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|c
argument_list|)
operator|.
name|asString
argument_list|()
return|;
block|}
DECL|method|escape (final String c)
specifier|private
specifier|static
name|String
name|escape
parameter_list|(
specifier|final
name|String
name|c
parameter_list|)
block|{
return|return
operator|new
name|SafeHtmlBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|c
argument_list|)
operator|.
name|asString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

