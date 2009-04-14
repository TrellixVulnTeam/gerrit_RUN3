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
name|gerrit
operator|.
name|client
operator|.
name|data
operator|.
name|SideBySideLine
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
name|client
operator|.
name|data
operator|.
name|SideBySidePatchDetail
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
name|client
operator|.
name|reviewdb
operator|.
name|PatchLineComment
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
name|client
operator|.
name|ui
operator|.
name|ComplexDisclosurePanel
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|ui
operator|.
name|FlowPanel
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|ui
operator|.
name|InlineLabel
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|ui
operator|.
name|Widget
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
name|java
operator|.
name|util
operator|.
name|Iterator
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
DECL|class|SideBySideTable
specifier|public
class|class
name|SideBySideTable
extends|extends
name|AbstractPatchContentTable
block|{
DECL|field|fileCnt
specifier|private
name|int
name|fileCnt
decl_stmt|;
DECL|field|maxLineNumber
specifier|private
name|int
name|maxLineNumber
decl_stmt|;
DECL|method|getFileCount ()
specifier|protected
name|int
name|getFileCount
parameter_list|()
block|{
return|return
name|fileCnt
return|;
block|}
DECL|method|getFileTitle (int file)
specifier|protected
name|String
name|getFileTitle
parameter_list|(
name|int
name|file
parameter_list|)
block|{
return|return
name|table
operator|.
name|getText
argument_list|(
literal|0
argument_list|,
literal|1
operator|+
name|file
operator|*
literal|2
operator|+
literal|1
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|onCellDoubleClick (final int row, int column)
specifier|protected
name|void
name|onCellDoubleClick
parameter_list|(
specifier|final
name|int
name|row
parameter_list|,
name|int
name|column
parameter_list|)
block|{
if|if
condition|(
name|column
operator|>
literal|0
operator|&&
name|getRowItem
argument_list|(
name|row
argument_list|)
operator|instanceof
name|SideBySideLineList
condition|)
block|{
specifier|final
name|SideBySideLineList
name|pl
init|=
operator|(
name|SideBySideLineList
operator|)
name|getRowItem
argument_list|(
name|row
argument_list|)
decl_stmt|;
specifier|final
name|short
name|file
init|=
call|(
name|short
call|)
argument_list|(
operator|(
name|column
operator|-
literal|1
operator|)
operator|/
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|column
operator|<
operator|(
literal|1
operator|+
name|file
operator|*
literal|2
operator|+
literal|1
operator|)
condition|)
block|{
name|column
operator|++
expr_stmt|;
block|}
specifier|final
name|SideBySideLine
name|line
init|=
name|pl
operator|.
name|lines
operator|.
name|get
argument_list|(
name|file
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|line
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|DELETE
case|:
case|case
name|EQUAL
case|:
case|case
name|INSERT
case|:
block|{
name|createCommentEditor
argument_list|(
name|row
operator|+
literal|1
argument_list|,
name|column
argument_list|,
name|line
operator|.
name|getLineNumber
argument_list|()
argument_list|,
name|file
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|onOpenItem (final Object item)
specifier|protected
name|void
name|onOpenItem
parameter_list|(
specifier|final
name|Object
name|item
parameter_list|)
block|{
if|if
condition|(
name|item
operator|instanceof
name|SideBySideLineList
condition|)
block|{
specifier|final
name|SideBySideLineList
name|pl
init|=
operator|(
name|SideBySideLineList
operator|)
name|item
decl_stmt|;
specifier|final
name|short
name|file
init|=
call|(
name|short
call|)
argument_list|(
name|pl
operator|.
name|lines
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|int
name|row
init|=
name|getCurrentRow
argument_list|()
decl_stmt|;
specifier|final
name|int
name|column
init|=
literal|1
operator|+
name|file
operator|*
literal|2
operator|+
literal|1
decl_stmt|;
specifier|final
name|SideBySideLine
name|line
init|=
name|pl
operator|.
name|lines
operator|.
name|get
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|createCommentEditor
argument_list|(
name|row
operator|+
literal|1
argument_list|,
name|column
argument_list|,
name|line
operator|.
name|getLineNumber
argument_list|()
argument_list|,
name|file
argument_list|)
expr_stmt|;
return|return;
block|}
name|super
operator|.
name|onOpenItem
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|bindDrafts (final List<PatchLineComment> drafts)
specifier|protected
name|void
name|bindDrafts
parameter_list|(
specifier|final
name|List
argument_list|<
name|PatchLineComment
argument_list|>
name|drafts
parameter_list|)
block|{
name|int
index|[]
name|rows
init|=
operator|new
name|int
index|[
name|fileCnt
index|]
decl_stmt|;
for|for
control|(
specifier|final
name|PatchLineComment
name|c
range|:
name|drafts
control|)
block|{
specifier|final
name|int
name|side
init|=
name|fileFor
argument_list|(
name|c
argument_list|)
decl_stmt|;
if|if
condition|(
name|side
operator|<
literal|0
operator|||
name|fileCnt
operator|<=
name|side
condition|)
block|{
comment|// We shouldn't have been given this draft; it doesn't display
comment|// in our current UI layout.
comment|//
continue|continue;
block|}
name|int
name|row
init|=
name|rows
index|[
name|side
index|]
decl_stmt|;
while|while
condition|(
name|row
operator|<
name|table
operator|.
name|getRowCount
argument_list|()
condition|)
block|{
if|if
condition|(
name|getRowItem
argument_list|(
name|row
argument_list|)
operator|instanceof
name|SideBySideLineList
condition|)
block|{
specifier|final
name|SideBySideLineList
name|pl
init|=
operator|(
name|SideBySideLineList
operator|)
name|getRowItem
argument_list|(
name|row
argument_list|)
decl_stmt|;
specifier|final
name|SideBySideLine
name|line
init|=
name|pl
operator|.
name|lines
operator|.
name|get
argument_list|(
name|side
argument_list|)
decl_stmt|;
if|if
condition|(
name|line
operator|!=
literal|null
operator|&&
name|line
operator|.
name|getLineNumber
argument_list|()
operator|>=
name|c
operator|.
name|getLine
argument_list|()
condition|)
block|{
break|break;
block|}
block|}
name|row
operator|++
expr_stmt|;
block|}
name|row
operator|++
expr_stmt|;
name|boolean
name|needInsert
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|cell
init|=
literal|0
init|;
name|cell
operator|<
name|table
operator|.
name|getCellCount
argument_list|(
name|row
argument_list|)
condition|;
name|cell
operator|++
control|)
block|{
specifier|final
name|Widget
name|w
init|=
name|table
operator|.
name|getWidget
argument_list|(
name|row
argument_list|,
name|cell
argument_list|)
decl_stmt|;
if|if
condition|(
name|w
operator|instanceof
name|CommentEditorPanel
operator|||
name|w
operator|instanceof
name|ComplexDisclosurePanel
condition|)
block|{
name|needInsert
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|needInsert
condition|)
block|{
name|table
operator|.
name|insertRow
argument_list|(
name|row
argument_list|)
expr_stmt|;
name|table
operator|.
name|getCellFormatter
argument_list|()
operator|.
name|setStyleName
argument_list|(
name|row
argument_list|,
literal|0
argument_list|,
name|S_ICON_CELL
argument_list|)
expr_stmt|;
block|}
name|bindComment
argument_list|(
name|row
argument_list|,
literal|1
operator|+
name|side
operator|*
literal|2
operator|+
literal|1
argument_list|,
name|c
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|rows
index|[
name|side
index|]
operator|=
name|row
operator|+
literal|1
expr_stmt|;
block|}
block|}
DECL|method|display (final SideBySidePatchDetail detail)
specifier|public
name|void
name|display
parameter_list|(
specifier|final
name|SideBySidePatchDetail
name|detail
parameter_list|)
block|{
name|setPatchKey
argument_list|(
name|detail
operator|.
name|getPatch
argument_list|()
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|initVersions
argument_list|(
name|detail
operator|.
name|getFileCount
argument_list|()
argument_list|)
expr_stmt|;
name|setAccountInfoCache
argument_list|(
name|detail
operator|.
name|getAccounts
argument_list|()
argument_list|)
expr_stmt|;
name|fileCnt
operator|=
name|detail
operator|.
name|getFileCount
argument_list|()
expr_stmt|;
name|maxLineNumber
operator|=
name|detail
operator|.
name|getLineCount
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|SideBySideLine
argument_list|>
name|prior
init|=
literal|null
decl_stmt|;
comment|// Generate the table in HTML, because its quicker than by DOM.
comment|// This pass does not include the line comments; they need full
comment|// GWT widgets and are relatively infrequent. We do them later.
comment|//
specifier|final
name|SafeHtmlBuilder
name|nc
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
name|appendHeader
argument_list|(
name|nc
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|List
argument_list|<
name|SideBySideLine
argument_list|>
name|pLine
range|:
name|detail
operator|.
name|getLines
argument_list|()
control|)
block|{
if|if
condition|(
name|skipped
argument_list|(
name|prior
argument_list|,
name|pLine
argument_list|)
operator|>
literal|0
condition|)
block|{
name|appendSkipLine
argument_list|(
name|nc
argument_list|)
expr_stmt|;
block|}
name|prior
operator|=
name|pLine
expr_stmt|;
name|appendFileLine
argument_list|(
name|nc
argument_list|,
name|pLine
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|skipped
argument_list|(
name|prior
argument_list|,
literal|null
argument_list|)
operator|>
literal|0
condition|)
block|{
name|appendSkipLine
argument_list|(
name|nc
argument_list|)
expr_stmt|;
block|}
name|resetHtml
argument_list|(
name|nc
argument_list|)
expr_stmt|;
comment|// Insert the comment widgets now that the table DOM has been
comment|// parsed out of the HTML by the browser. We also bind each
comment|// of the row item objects.
comment|//
name|int
name|row
init|=
literal|1
decl_stmt|;
name|prior
operator|=
literal|null
expr_stmt|;
for|for
control|(
specifier|final
name|List
argument_list|<
name|SideBySideLine
argument_list|>
name|pLine
range|:
name|detail
operator|.
name|getLines
argument_list|()
control|)
block|{
specifier|final
name|int
name|skipCnt
init|=
name|skipped
argument_list|(
name|prior
argument_list|,
name|pLine
argument_list|)
decl_stmt|;
if|if
condition|(
name|skipCnt
operator|>
literal|0
condition|)
block|{
name|bindSkipLine
argument_list|(
name|row
argument_list|,
name|skipCnt
argument_list|)
expr_stmt|;
name|row
operator|++
expr_stmt|;
block|}
name|prior
operator|=
name|pLine
expr_stmt|;
name|setRowItem
argument_list|(
name|row
argument_list|,
operator|new
name|SideBySideLineList
argument_list|(
name|pLine
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|lineRow
init|=
name|row
decl_stmt|;
for|for
control|(
name|int
name|fileId
init|=
literal|0
init|;
name|fileId
operator|<
name|fileCnt
condition|;
name|fileId
operator|++
control|)
block|{
specifier|final
name|SideBySideLine
name|s
init|=
name|pLine
operator|.
name|get
argument_list|(
name|fileId
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
specifier|final
name|List
argument_list|<
name|PatchLineComment
argument_list|>
name|comments
init|=
name|s
operator|.
name|getComments
argument_list|()
decl_stmt|;
if|if
condition|(
name|comments
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|int
name|commentRow
init|=
name|lineRow
operator|+
literal|1
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|PatchLineComment
argument_list|>
name|ci
init|=
name|comments
operator|.
name|iterator
argument_list|()
init|;
name|ci
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|PatchLineComment
name|c
init|=
name|ci
operator|.
name|next
argument_list|()
decl_stmt|;
name|boolean
name|needInsert
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|commentRow
operator|<
name|table
operator|.
name|getRowCount
argument_list|()
condition|)
block|{
for|for
control|(
name|int
name|cell
init|=
literal|0
init|;
name|cell
operator|<
name|table
operator|.
name|getCellCount
argument_list|(
name|commentRow
argument_list|)
condition|;
name|cell
operator|++
control|)
block|{
specifier|final
name|Widget
name|w
init|=
name|table
operator|.
name|getWidget
argument_list|(
name|commentRow
argument_list|,
name|cell
argument_list|)
decl_stmt|;
if|if
condition|(
name|w
operator|instanceof
name|CommentEditorPanel
operator|||
name|w
operator|instanceof
name|ComplexDisclosurePanel
condition|)
block|{
name|needInsert
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|needInsert
condition|)
block|{
name|table
operator|.
name|insertRow
argument_list|(
name|commentRow
argument_list|)
expr_stmt|;
name|table
operator|.
name|getCellFormatter
argument_list|()
operator|.
name|setStyleName
argument_list|(
name|commentRow
argument_list|,
literal|0
argument_list|,
name|S_ICON_CELL
argument_list|)
expr_stmt|;
block|}
name|table
operator|.
name|setWidget
argument_list|(
name|commentRow
argument_list|,
literal|1
operator|+
literal|2
operator|*
name|fileId
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|bindComment
argument_list|(
name|commentRow
argument_list|,
literal|1
operator|+
literal|2
operator|*
name|fileId
operator|+
literal|1
argument_list|,
name|c
argument_list|,
operator|!
name|ci
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|commentRow
operator|++
expr_stmt|;
block|}
name|row
operator|=
name|Math
operator|.
name|max
argument_list|(
name|row
argument_list|,
name|commentRow
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|row
operator|++
expr_stmt|;
block|}
specifier|final
name|int
name|skipCnt
init|=
name|skipped
argument_list|(
name|prior
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|skipCnt
operator|>
literal|0
condition|)
block|{
name|bindSkipLine
argument_list|(
name|row
argument_list|,
name|skipCnt
argument_list|)
expr_stmt|;
name|row
operator|++
expr_stmt|;
block|}
block|}
DECL|method|appendHeader (final SafeHtmlBuilder m)
specifier|private
name|void
name|appendHeader
parameter_list|(
specifier|final
name|SafeHtmlBuilder
name|m
parameter_list|)
block|{
specifier|final
name|String
name|width
init|=
operator|(
literal|100
operator|/
name|fileCnt
operator|)
operator|+
literal|"%"
decl_stmt|;
name|m
operator|.
name|openTr
argument_list|()
expr_stmt|;
name|m
operator|.
name|openTd
argument_list|()
expr_stmt|;
name|m
operator|.
name|addStyleName
argument_list|(
name|S_ICON_CELL
argument_list|)
expr_stmt|;
name|m
operator|.
name|addStyleName
argument_list|(
literal|"FileColumnHeader"
argument_list|)
expr_stmt|;
name|m
operator|.
name|nbsp
argument_list|()
expr_stmt|;
name|m
operator|.
name|closeTd
argument_list|()
expr_stmt|;
if|if
condition|(
name|fileCnt
operator|==
literal|2
condition|)
block|{
name|m
operator|.
name|openTd
argument_list|()
expr_stmt|;
name|m
operator|.
name|addStyleName
argument_list|(
literal|"FileColumnHeader"
argument_list|)
expr_stmt|;
name|m
operator|.
name|addStyleName
argument_list|(
literal|"LineNumber"
argument_list|)
expr_stmt|;
name|m
operator|.
name|nbsp
argument_list|()
expr_stmt|;
name|m
operator|.
name|closeTd
argument_list|()
expr_stmt|;
name|m
operator|.
name|openTd
argument_list|()
expr_stmt|;
name|m
operator|.
name|setStyleName
argument_list|(
literal|"FileColumnHeader"
argument_list|)
expr_stmt|;
name|m
operator|.
name|setAttribute
argument_list|(
literal|"width"
argument_list|,
name|width
argument_list|)
expr_stmt|;
name|m
operator|.
name|append
argument_list|(
name|PatchUtil
operator|.
name|C
operator|.
name|patchHeaderOld
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|closeTd
argument_list|()
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|int
name|fileId
init|=
literal|0
init|;
name|fileId
operator|<
name|fileCnt
operator|-
literal|1
condition|;
name|fileId
operator|++
control|)
block|{
name|m
operator|.
name|openTd
argument_list|()
expr_stmt|;
name|m
operator|.
name|addStyleName
argument_list|(
literal|"FileColumnHeader"
argument_list|)
expr_stmt|;
name|m
operator|.
name|addStyleName
argument_list|(
literal|"LineNumber"
argument_list|)
expr_stmt|;
name|m
operator|.
name|nbsp
argument_list|()
expr_stmt|;
name|m
operator|.
name|closeTd
argument_list|()
expr_stmt|;
name|m
operator|.
name|openTd
argument_list|()
expr_stmt|;
name|m
operator|.
name|setStyleName
argument_list|(
literal|"FileColumnHeader"
argument_list|)
expr_stmt|;
name|m
operator|.
name|setAttribute
argument_list|(
literal|"width"
argument_list|,
name|width
argument_list|)
expr_stmt|;
name|m
operator|.
name|append
argument_list|(
name|PatchUtil
operator|.
name|M
operator|.
name|patchHeaderAncestor
argument_list|(
name|fileId
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|.
name|closeTd
argument_list|()
expr_stmt|;
block|}
block|}
name|m
operator|.
name|openTd
argument_list|()
expr_stmt|;
name|m
operator|.
name|addStyleName
argument_list|(
literal|"FileColumnHeader"
argument_list|)
expr_stmt|;
name|m
operator|.
name|addStyleName
argument_list|(
literal|"LineNumber"
argument_list|)
expr_stmt|;
name|m
operator|.
name|nbsp
argument_list|()
expr_stmt|;
name|m
operator|.
name|closeTd
argument_list|()
expr_stmt|;
name|m
operator|.
name|openTd
argument_list|()
expr_stmt|;
name|m
operator|.
name|setStyleName
argument_list|(
literal|"FileColumnHeader"
argument_list|)
expr_stmt|;
name|m
operator|.
name|setAttribute
argument_list|(
literal|"width"
argument_list|,
name|width
argument_list|)
expr_stmt|;
name|m
operator|.
name|append
argument_list|(
name|PatchUtil
operator|.
name|C
operator|.
name|patchHeaderNew
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|closeTd
argument_list|()
expr_stmt|;
name|m
operator|.
name|closeTr
argument_list|()
expr_stmt|;
block|}
DECL|method|skipped (List<SideBySideLine> prior, final List<SideBySideLine> pLine)
specifier|private
name|int
name|skipped
parameter_list|(
name|List
argument_list|<
name|SideBySideLine
argument_list|>
name|prior
parameter_list|,
specifier|final
name|List
argument_list|<
name|SideBySideLine
argument_list|>
name|pLine
parameter_list|)
block|{
name|int
name|existCnt
init|=
literal|0
decl_stmt|;
name|int
name|gapCnt
init|=
literal|0
decl_stmt|;
name|int
name|lines
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|prior
operator|!=
literal|null
operator|&&
name|pLine
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fileCnt
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|SideBySideLine
name|ps
init|=
name|prior
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|SideBySideLine
name|cs
init|=
name|pLine
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|!=
literal|null
operator|&&
name|cs
operator|!=
literal|null
condition|)
block|{
name|existCnt
operator|++
expr_stmt|;
if|if
condition|(
name|ps
operator|.
name|getLineNumber
argument_list|()
operator|+
literal|1
operator|!=
name|cs
operator|.
name|getLineNumber
argument_list|()
condition|)
block|{
name|lines
operator|=
name|Math
operator|.
name|max
argument_list|(
name|lines
argument_list|,
name|cs
operator|.
name|getLineNumber
argument_list|()
operator|-
name|ps
operator|.
name|getLineNumber
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|gapCnt
operator|++
expr_stmt|;
block|}
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|prior
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fileCnt
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|SideBySideLine
name|ps
init|=
name|prior
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|!=
literal|null
condition|)
block|{
name|existCnt
operator|++
expr_stmt|;
if|if
condition|(
name|ps
operator|.
name|getLineNumber
argument_list|()
operator|<
name|maxLineNumber
condition|)
block|{
name|lines
operator|=
name|Math
operator|.
name|max
argument_list|(
name|lines
argument_list|,
name|maxLineNumber
operator|-
name|ps
operator|.
name|getLineNumber
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|gapCnt
operator|++
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fileCnt
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|SideBySideLine
name|cs
init|=
name|pLine
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|cs
operator|!=
literal|null
condition|)
block|{
name|existCnt
operator|++
expr_stmt|;
if|if
condition|(
literal|1
operator|!=
name|cs
operator|.
name|getLineNumber
argument_list|()
condition|)
block|{
name|lines
operator|=
name|Math
operator|.
name|max
argument_list|(
name|lines
argument_list|,
name|cs
operator|.
name|getLineNumber
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|gapCnt
operator|++
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|existCnt
operator|==
name|gapCnt
condition|?
name|lines
else|:
literal|0
return|;
block|}
DECL|method|appendSkipLine (final SafeHtmlBuilder m)
specifier|private
name|void
name|appendSkipLine
parameter_list|(
specifier|final
name|SafeHtmlBuilder
name|m
parameter_list|)
block|{
name|m
operator|.
name|openTr
argument_list|()
expr_stmt|;
name|m
operator|.
name|openTd
argument_list|()
expr_stmt|;
name|m
operator|.
name|setStyleName
argument_list|(
name|S_ICON_CELL
argument_list|)
expr_stmt|;
name|m
operator|.
name|nbsp
argument_list|()
expr_stmt|;
name|m
operator|.
name|closeTd
argument_list|()
expr_stmt|;
name|m
operator|.
name|openTd
argument_list|()
expr_stmt|;
name|m
operator|.
name|setStyleName
argument_list|(
literal|"SkipLine"
argument_list|)
expr_stmt|;
name|m
operator|.
name|setAttribute
argument_list|(
literal|"colspan"
argument_list|,
name|fileCnt
operator|*
literal|2
argument_list|)
expr_stmt|;
name|m
operator|.
name|closeTd
argument_list|()
expr_stmt|;
name|m
operator|.
name|closeTr
argument_list|()
expr_stmt|;
block|}
DECL|method|bindSkipLine (int row, final int skipCnt)
specifier|private
name|void
name|bindSkipLine
parameter_list|(
name|int
name|row
parameter_list|,
specifier|final
name|int
name|skipCnt
parameter_list|)
block|{
specifier|final
name|FlowPanel
name|skipPanel
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|skipPanel
operator|.
name|add
argument_list|(
operator|new
name|InlineLabel
argument_list|(
name|PatchUtil
operator|.
name|M
operator|.
name|patchSkipRegion
argument_list|(
name|skipCnt
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
literal|1
argument_list|,
name|skipPanel
argument_list|)
expr_stmt|;
block|}
DECL|method|appendFileLine (final SafeHtmlBuilder m, final List<SideBySideLine> line)
specifier|private
name|void
name|appendFileLine
parameter_list|(
specifier|final
name|SafeHtmlBuilder
name|m
parameter_list|,
specifier|final
name|List
argument_list|<
name|SideBySideLine
argument_list|>
name|line
parameter_list|)
block|{
name|m
operator|.
name|openTr
argument_list|()
expr_stmt|;
name|m
operator|.
name|setAttribute
argument_list|(
literal|"valign"
argument_list|,
literal|"top"
argument_list|)
expr_stmt|;
name|m
operator|.
name|openTd
argument_list|()
expr_stmt|;
name|m
operator|.
name|setStyleName
argument_list|(
name|S_ICON_CELL
argument_list|)
expr_stmt|;
name|m
operator|.
name|nbsp
argument_list|()
expr_stmt|;
name|m
operator|.
name|closeTd
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|fileId
init|=
literal|0
init|;
name|fileId
operator|<
name|fileCnt
condition|;
name|fileId
operator|++
control|)
block|{
specifier|final
name|SideBySideLine
name|s
init|=
name|line
operator|.
name|get
argument_list|(
name|fileId
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|m
operator|.
name|openTd
argument_list|()
expr_stmt|;
name|m
operator|.
name|setStyleName
argument_list|(
literal|"LineNumber"
argument_list|)
expr_stmt|;
name|m
operator|.
name|append
argument_list|(
name|s
operator|.
name|getLineNumber
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|closeTd
argument_list|()
expr_stmt|;
name|m
operator|.
name|openTd
argument_list|()
expr_stmt|;
name|m
operator|.
name|addStyleName
argument_list|(
literal|"FileLine"
argument_list|)
expr_stmt|;
name|m
operator|.
name|addStyleName
argument_list|(
literal|"FileLine-"
operator|+
name|s
operator|.
name|getType
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|s
operator|.
name|getText
argument_list|()
argument_list|)
condition|)
block|{
name|boolean
name|showWhitespaceErrors
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|fileId
operator|==
name|fileCnt
operator|-
literal|1
operator|&&
name|s
operator|.
name|getType
argument_list|()
operator|==
name|SideBySideLine
operator|.
name|Type
operator|.
name|INSERT
condition|)
block|{
comment|// Only show whitespace errors in the last column, and
comment|// only if the line is introduced here.
comment|//
name|showWhitespaceErrors
operator|=
literal|true
expr_stmt|;
block|}
name|m
operator|.
name|append
argument_list|(
name|PatchUtil
operator|.
name|lineToSafeHtml
argument_list|(
name|s
operator|.
name|getText
argument_list|()
argument_list|,
name|PatchUtil
operator|.
name|DEFAULT_LINE_LENGTH
argument_list|,
name|showWhitespaceErrors
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|m
operator|.
name|nbsp
argument_list|()
expr_stmt|;
block|}
name|m
operator|.
name|closeTd
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|m
operator|.
name|openTd
argument_list|()
expr_stmt|;
name|m
operator|.
name|setStyleName
argument_list|(
literal|"LineNumber"
argument_list|)
expr_stmt|;
name|m
operator|.
name|nbsp
argument_list|()
expr_stmt|;
name|m
operator|.
name|closeTd
argument_list|()
expr_stmt|;
name|m
operator|.
name|openTd
argument_list|()
expr_stmt|;
name|m
operator|.
name|addStyleName
argument_list|(
literal|"FileLine"
argument_list|)
expr_stmt|;
name|m
operator|.
name|addStyleName
argument_list|(
literal|"FileLineNone"
argument_list|)
expr_stmt|;
name|m
operator|.
name|nbsp
argument_list|()
expr_stmt|;
name|m
operator|.
name|closeTd
argument_list|()
expr_stmt|;
block|}
block|}
name|m
operator|.
name|closeTr
argument_list|()
expr_stmt|;
block|}
DECL|class|SideBySideLineList
specifier|private
specifier|static
class|class
name|SideBySideLineList
block|{
DECL|field|lines
specifier|final
name|List
argument_list|<
name|SideBySideLine
argument_list|>
name|lines
decl_stmt|;
DECL|method|SideBySideLineList (final List<SideBySideLine> a)
name|SideBySideLineList
parameter_list|(
specifier|final
name|List
argument_list|<
name|SideBySideLine
argument_list|>
name|a
parameter_list|)
block|{
name|lines
operator|=
name|a
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

