begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright 2008 Google Inc.
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
DECL|package|com.google.gerrit.client.changes
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|changes
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
name|Link
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
name|Patch
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
name|FancyFlexTable
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
name|PatchLink
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
name|History
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
name|gwt
operator|.
name|user
operator|.
name|client
operator|.
name|ui
operator|.
name|FlexTable
operator|.
name|FlexCellFormatter
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
name|HTMLTable
operator|.
name|CellFormatter
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
DECL|class|PatchTable
specifier|public
class|class
name|PatchTable
extends|extends
name|FancyFlexTable
argument_list|<
name|Patch
argument_list|>
block|{
DECL|field|C_TYPE
specifier|private
specifier|static
specifier|final
name|int
name|C_TYPE
init|=
literal|1
decl_stmt|;
DECL|field|C_NAME
specifier|private
specifier|static
specifier|final
name|int
name|C_NAME
init|=
literal|2
decl_stmt|;
DECL|field|C_DELTA
specifier|private
specifier|static
specifier|final
name|int
name|C_DELTA
init|=
literal|3
decl_stmt|;
DECL|field|C_COMMENTS
specifier|private
specifier|static
specifier|final
name|int
name|C_COMMENTS
init|=
literal|4
decl_stmt|;
DECL|field|C_DIFF
specifier|private
specifier|static
specifier|final
name|int
name|C_DIFF
init|=
literal|5
decl_stmt|;
DECL|field|N_DIFF
specifier|private
specifier|static
specifier|final
name|int
name|N_DIFF
init|=
literal|2
decl_stmt|;
DECL|method|PatchTable ()
specifier|public
name|PatchTable
parameter_list|()
block|{
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
name|C_TYPE
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
name|C_NAME
argument_list|,
name|Util
operator|.
name|C
operator|.
name|patchTableColumnName
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
name|C_DELTA
argument_list|,
name|Util
operator|.
name|C
operator|.
name|patchTableColumnDelta
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
name|C_COMMENTS
argument_list|,
name|Util
operator|.
name|C
operator|.
name|patchTableColumnComments
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
name|C_DIFF
argument_list|,
name|Util
operator|.
name|C
operator|.
name|patchTableColumnDiff
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|FlexCellFormatter
name|fmt
init|=
name|table
operator|.
name|getFlexCellFormatter
argument_list|()
decl_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|0
argument_list|,
name|C_TYPE
argument_list|,
name|S_ICON_HEADER
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|0
argument_list|,
name|C_NAME
argument_list|,
name|S_DATA_HEADER
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|0
argument_list|,
name|C_DELTA
argument_list|,
name|S_DATA_HEADER
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|0
argument_list|,
name|C_COMMENTS
argument_list|,
name|S_DATA_HEADER
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|0
argument_list|,
name|C_DIFF
argument_list|,
name|S_DATA_HEADER
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|setColSpan
argument_list|(
literal|0
argument_list|,
name|C_DIFF
argument_list|,
name|N_DIFF
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRowItemKey (final Patch item)
specifier|protected
name|Object
name|getRowItemKey
parameter_list|(
specifier|final
name|Patch
name|item
parameter_list|)
block|{
return|return
name|item
operator|.
name|getKey
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|onOpenItem (final Patch item)
specifier|protected
name|void
name|onOpenItem
parameter_list|(
specifier|final
name|Patch
name|item
parameter_list|)
block|{
name|History
operator|.
name|newItem
argument_list|(
name|Link
operator|.
name|toPatchSideBySide
argument_list|(
name|item
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|applyDataRowStyle (final int row)
specifier|protected
name|void
name|applyDataRowStyle
parameter_list|(
specifier|final
name|int
name|row
parameter_list|)
block|{
name|super
operator|.
name|applyDataRowStyle
argument_list|(
name|row
argument_list|)
expr_stmt|;
specifier|final
name|CellFormatter
name|fmt
init|=
name|table
operator|.
name|getCellFormatter
argument_list|()
decl_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|C_TYPE
argument_list|,
literal|"ChangeTypeCell"
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|C_NAME
argument_list|,
name|S_DATA_CELL
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|C_NAME
argument_list|,
literal|"FilePathCell"
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|C_DELTA
argument_list|,
name|S_DATA_CELL
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|C_COMMENTS
argument_list|,
name|S_DATA_CELL
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|C_COMMENTS
argument_list|,
literal|"CommentCell"
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|C_DIFF
operator|+
literal|0
argument_list|,
name|S_DATA_CELL
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|C_DIFF
operator|+
literal|0
argument_list|,
literal|"DiffLinkCell"
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|C_DIFF
operator|+
literal|1
argument_list|,
name|S_DATA_CELL
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|C_DIFF
operator|+
literal|1
argument_list|,
literal|"DiffLinkCell"
argument_list|)
expr_stmt|;
block|}
DECL|method|display (final List<Patch> list)
specifier|public
name|void
name|display
parameter_list|(
specifier|final
name|List
argument_list|<
name|Patch
argument_list|>
name|list
parameter_list|)
block|{
specifier|final
name|int
name|sz
init|=
name|list
operator|!=
literal|null
condition|?
name|list
operator|.
name|size
argument_list|()
else|:
literal|0
decl_stmt|;
name|int
name|dataRows
init|=
name|table
operator|.
name|getRowCount
argument_list|()
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|sz
operator|<
name|dataRows
condition|)
block|{
name|table
operator|.
name|removeRow
argument_list|(
name|dataRows
argument_list|)
expr_stmt|;
name|dataRows
operator|--
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sz
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|dataRows
operator|<=
name|i
condition|)
block|{
name|table
operator|.
name|insertRow
argument_list|(
operator|++
name|dataRows
argument_list|)
expr_stmt|;
name|applyDataRowStyle
argument_list|(
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|populate
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|populate (final int row, final Patch patch)
specifier|private
name|void
name|populate
parameter_list|(
specifier|final
name|int
name|row
parameter_list|,
specifier|final
name|Patch
name|patch
parameter_list|)
block|{
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
name|C_ARROW
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
name|row
argument_list|,
name|C_TYPE
argument_list|,
literal|""
operator|+
name|patch
operator|.
name|getChangeType
argument_list|()
operator|.
name|getCode
argument_list|()
argument_list|)
expr_stmt|;
name|Widget
name|nameLink
decl_stmt|;
if|if
condition|(
name|patch
operator|.
name|getPatchType
argument_list|()
operator|==
name|Patch
operator|.
name|PatchType
operator|.
name|UNIFIED
condition|)
block|{
name|nameLink
operator|=
operator|new
name|PatchLink
operator|.
name|SideBySide
argument_list|(
name|patch
operator|.
name|getKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|patch
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nameLink
operator|=
operator|new
name|PatchLink
operator|.
name|Unified
argument_list|(
name|patch
operator|.
name|getKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|patch
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|patch
operator|.
name|getSourceFileName
argument_list|()
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|secondLine
decl_stmt|;
if|if
condition|(
name|patch
operator|.
name|getChangeType
argument_list|()
operator|==
name|Patch
operator|.
name|ChangeType
operator|.
name|RENAMED
condition|)
block|{
name|secondLine
operator|=
name|Util
operator|.
name|M
operator|.
name|renamedFrom
argument_list|(
name|patch
operator|.
name|getSourceFileName
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|patch
operator|.
name|getChangeType
argument_list|()
operator|==
name|Patch
operator|.
name|ChangeType
operator|.
name|COPIED
condition|)
block|{
name|secondLine
operator|=
name|Util
operator|.
name|M
operator|.
name|copiedFrom
argument_list|(
name|patch
operator|.
name|getSourceFileName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|secondLine
operator|=
name|Util
operator|.
name|M
operator|.
name|otherFrom
argument_list|(
name|patch
operator|.
name|getSourceFileName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|InlineLabel
name|secondLineLabel
init|=
operator|new
name|InlineLabel
argument_list|(
name|secondLine
argument_list|)
decl_stmt|;
name|secondLineLabel
operator|.
name|setStyleName
argument_list|(
literal|"SourceFilePath"
argument_list|)
expr_stmt|;
specifier|final
name|FlowPanel
name|fp
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|fp
operator|.
name|add
argument_list|(
name|nameLink
argument_list|)
expr_stmt|;
name|fp
operator|.
name|add
argument_list|(
name|secondLineLabel
argument_list|)
expr_stmt|;
name|nameLink
operator|=
name|fp
expr_stmt|;
block|}
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
name|C_NAME
argument_list|,
name|nameLink
argument_list|)
expr_stmt|;
name|table
operator|.
name|clearCell
argument_list|(
name|row
argument_list|,
name|C_DELTA
argument_list|)
expr_stmt|;
specifier|final
name|int
name|cnt
init|=
name|patch
operator|.
name|getCommentCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|cnt
operator|==
literal|0
condition|)
block|{
name|table
operator|.
name|clearCell
argument_list|(
name|row
argument_list|,
name|C_COMMENTS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|table
operator|.
name|setText
argument_list|(
name|row
argument_list|,
name|C_COMMENTS
argument_list|,
name|Util
operator|.
name|M
operator|.
name|patchTableComments
argument_list|(
name|cnt
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|patch
operator|.
name|getPatchType
argument_list|()
operator|==
name|Patch
operator|.
name|PatchType
operator|.
name|UNIFIED
condition|)
block|{
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
name|C_DIFF
operator|+
literal|0
argument_list|,
operator|new
name|PatchLink
operator|.
name|SideBySide
argument_list|(
name|Util
operator|.
name|C
operator|.
name|patchTableDiffSideBySide
argument_list|()
argument_list|,
name|patch
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|table
operator|.
name|clearCell
argument_list|(
name|row
argument_list|,
name|C_DIFF
operator|+
literal|0
argument_list|)
expr_stmt|;
block|}
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
name|C_DIFF
operator|+
literal|1
argument_list|,
operator|new
name|PatchLink
operator|.
name|Unified
argument_list|(
name|Util
operator|.
name|C
operator|.
name|patchTableDiffUnified
argument_list|()
argument_list|,
name|patch
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setRowItem
argument_list|(
name|row
argument_list|,
name|patch
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

