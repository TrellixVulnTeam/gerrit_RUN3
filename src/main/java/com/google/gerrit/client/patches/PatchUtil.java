begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2008 The Android Open Source Project
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
DECL|package|com.google.gerrit.client.patches
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|patches
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|core
operator|.
name|client
operator|.
name|GWT
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
name|safehtml
operator|.
name|client
operator|.
name|SafeHtml
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
name|safehtml
operator|.
name|client
operator|.
name|SafeHtmlBuilder
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
name|client
operator|.
name|JsonUtil
import|;
end_import

begin_class
DECL|class|PatchUtil
specifier|public
class|class
name|PatchUtil
block|{
DECL|field|C
specifier|public
specifier|static
specifier|final
name|PatchConstants
name|C
init|=
name|GWT
operator|.
name|create
argument_list|(
name|PatchConstants
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|M
specifier|public
specifier|static
specifier|final
name|PatchMessages
name|M
init|=
name|GWT
operator|.
name|create
argument_list|(
name|PatchMessages
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|DETAIL_SVC
specifier|public
specifier|static
specifier|final
name|PatchDetailService
name|DETAIL_SVC
decl_stmt|;
DECL|field|DEFAULT_LINE_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_LINE_LENGTH
init|=
literal|100
decl_stmt|;
static|static
block|{
name|DETAIL_SVC
operator|=
name|GWT
operator|.
name|create
argument_list|(
name|PatchDetailService
operator|.
name|class
argument_list|)
expr_stmt|;
name|JsonUtil
operator|.
name|bind
argument_list|(
name|DETAIL_SVC
argument_list|,
literal|"rpc/PatchDetailService"
argument_list|)
expr_stmt|;
block|}
DECL|method|lineToSafeHtml (final String src, final int lineLength, final boolean showWhiteSpaceErrors)
specifier|public
specifier|static
name|SafeHtml
name|lineToSafeHtml
parameter_list|(
specifier|final
name|String
name|src
parameter_list|,
specifier|final
name|int
name|lineLength
parameter_list|,
specifier|final
name|boolean
name|showWhiteSpaceErrors
parameter_list|)
block|{
specifier|final
name|boolean
name|hasTab
init|=
name|src
operator|.
name|indexOf
argument_list|(
literal|'\t'
argument_list|)
operator|>=
literal|0
decl_stmt|;
name|String
name|brokenSrc
init|=
name|wrapLines
argument_list|(
name|src
argument_list|,
name|hasTab
argument_list|,
name|lineLength
argument_list|)
decl_stmt|;
name|SafeHtml
name|html
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|brokenSrc
argument_list|)
decl_stmt|;
if|if
condition|(
name|showWhiteSpaceErrors
condition|)
block|{
name|html
operator|=
name|showTabAfterSpace
argument_list|(
name|html
argument_list|)
expr_stmt|;
name|html
operator|=
name|showTrailingWhitespace
argument_list|(
name|html
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|brokenSrc
operator|!=
name|src
condition|)
block|{
comment|// If we had line breaks inserted into the source text we need
comment|// to expand the line breaks into<br> tags in HTML, so the
comment|// line will wrap around.
comment|//
name|html
operator|=
name|expandLFs
argument_list|(
name|html
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hasTab
condition|)
block|{
comment|// We had at least one horizontal tab, so we should expand it out.
comment|//
name|html
operator|=
name|expandTabs
argument_list|(
name|html
argument_list|)
expr_stmt|;
block|}
return|return
name|html
return|;
block|}
DECL|method|wrapLines (final String src, final boolean hasTabs, final int lineLength)
specifier|private
specifier|static
name|String
name|wrapLines
parameter_list|(
specifier|final
name|String
name|src
parameter_list|,
specifier|final
name|boolean
name|hasTabs
parameter_list|,
specifier|final
name|int
name|lineLength
parameter_list|)
block|{
if|if
condition|(
name|lineLength
operator|<=
literal|0
condition|)
block|{
comment|// Caller didn't request for line wrapping; use it unmodified.
comment|//
return|return
name|src
return|;
block|}
if|if
condition|(
name|src
operator|.
name|length
argument_list|()
operator|<
name|lineLength
operator|&&
operator|!
name|hasTabs
condition|)
block|{
comment|// We're too short and there are no horizontal tabs, line is fine
comment|// as-is so bypass the longer line wrapping code below.
return|return
name|src
return|;
block|}
specifier|final
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|lineLen
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|src
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|char
name|c
init|=
name|src
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|int
name|cLen
init|=
name|c
operator|==
literal|'\t'
condition|?
literal|8
else|:
literal|1
decl_stmt|;
if|if
condition|(
name|lineLen
operator|>=
name|lineLength
condition|)
block|{
name|r
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|lineLen
operator|=
literal|0
expr_stmt|;
block|}
name|r
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|lineLen
operator|+=
name|cLen
expr_stmt|;
block|}
return|return
name|r
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|expandTabs (SafeHtml src)
specifier|private
specifier|static
name|SafeHtml
name|expandTabs
parameter_list|(
name|SafeHtml
name|src
parameter_list|)
block|{
return|return
name|src
operator|.
name|replaceAll
argument_list|(
literal|"\t"
argument_list|,
literal|"<span title=\"Visual Tab\" class=\"gerrit-visualtab\">&raquo;</span>\t"
argument_list|)
return|;
block|}
DECL|method|expandLFs (SafeHtml src)
specifier|private
specifier|static
name|SafeHtml
name|expandLFs
parameter_list|(
name|SafeHtml
name|src
parameter_list|)
block|{
return|return
name|src
operator|.
name|replaceAll
argument_list|(
literal|"\n"
argument_list|,
literal|"<br />"
argument_list|)
return|;
block|}
DECL|method|showTabAfterSpace (SafeHtml src)
specifier|private
specifier|static
name|SafeHtml
name|showTabAfterSpace
parameter_list|(
name|SafeHtml
name|src
parameter_list|)
block|{
return|return
name|src
operator|.
name|replaceFirst
argument_list|(
literal|"^(  *\t)"
argument_list|,
literal|"<span class=\"gerrit-whitespaceerror\">$1</span>"
argument_list|)
return|;
block|}
DECL|method|showTrailingWhitespace (SafeHtml src)
specifier|private
specifier|static
name|SafeHtml
name|showTrailingWhitespace
parameter_list|(
name|SafeHtml
name|src
parameter_list|)
block|{
return|return
name|src
operator|.
name|replaceFirst
argument_list|(
literal|"([ \t][ \t]*)(\r?\n?)$"
argument_list|,
literal|"<span class=\"gerrit-whitespaceerror\">$1</span>$2"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

