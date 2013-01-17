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
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|FormatUtil
operator|.
name|shortFormat
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
name|Gerrit
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
name|changes
operator|.
name|ChangeInfo
operator|.
name|LabelInfo
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
name|BranchLink
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
name|ChangeLink
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
name|InlineHyperlink
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
name|NavigationTable
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
name|NeedsSignInKeyCommand
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
name|ProjectLink
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
name|common
operator|.
name|PageLinks
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
name|reviewdb
operator|.
name|client
operator|.
name|Change
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
name|dom
operator|.
name|client
operator|.
name|Element
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
name|event
operator|.
name|dom
operator|.
name|client
operator|.
name|ClickEvent
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
name|event
operator|.
name|dom
operator|.
name|client
operator|.
name|ClickHandler
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
name|event
operator|.
name|dom
operator|.
name|client
operator|.
name|KeyPressEvent
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
name|DOM
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
name|HTMLTable
operator|.
name|Cell
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
name|Image
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
name|UIObject
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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
DECL|class|ChangeTable2
specifier|public
class|class
name|ChangeTable2
extends|extends
name|NavigationTable
argument_list|<
name|ChangeInfo
argument_list|>
block|{
DECL|field|C_STAR
specifier|private
specifier|static
specifier|final
name|int
name|C_STAR
init|=
literal|1
decl_stmt|;
DECL|field|C_SUBJECT
specifier|private
specifier|static
specifier|final
name|int
name|C_SUBJECT
init|=
literal|2
decl_stmt|;
DECL|field|C_OWNER
specifier|private
specifier|static
specifier|final
name|int
name|C_OWNER
init|=
literal|3
decl_stmt|;
DECL|field|C_PROJECT
specifier|private
specifier|static
specifier|final
name|int
name|C_PROJECT
init|=
literal|4
decl_stmt|;
DECL|field|C_BRANCH
specifier|private
specifier|static
specifier|final
name|int
name|C_BRANCH
init|=
literal|5
decl_stmt|;
DECL|field|C_LAST_UPDATE
specifier|private
specifier|static
specifier|final
name|int
name|C_LAST_UPDATE
init|=
literal|6
decl_stmt|;
DECL|field|BASE_COLUMNS
specifier|private
specifier|static
specifier|final
name|int
name|BASE_COLUMNS
init|=
literal|7
decl_stmt|;
DECL|field|sections
specifier|private
specifier|final
name|List
argument_list|<
name|Section
argument_list|>
name|sections
decl_stmt|;
DECL|field|columns
specifier|private
name|int
name|columns
decl_stmt|;
DECL|field|labelNames
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|labelNames
decl_stmt|;
DECL|method|ChangeTable2 ()
specifier|public
name|ChangeTable2
parameter_list|()
block|{
name|super
argument_list|(
name|Util
operator|.
name|C
operator|.
name|changeItemHelp
argument_list|()
argument_list|)
expr_stmt|;
name|columns
operator|=
name|BASE_COLUMNS
expr_stmt|;
name|labelNames
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
if|if
condition|(
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
condition|)
block|{
name|keysAction
operator|.
name|add
argument_list|(
operator|new
name|StarKeyCommand
argument_list|(
literal|0
argument_list|,
literal|'s'
argument_list|,
name|Util
operator|.
name|C
operator|.
name|changeTableStar
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sections
operator|=
operator|new
name|ArrayList
argument_list|<
name|Section
argument_list|>
argument_list|()
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
name|C_STAR
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
name|C_SUBJECT
argument_list|,
name|Util
operator|.
name|C
operator|.
name|changeTableColumnSubject
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
name|C_OWNER
argument_list|,
name|Util
operator|.
name|C
operator|.
name|changeTableColumnOwner
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
name|C_PROJECT
argument_list|,
name|Util
operator|.
name|C
operator|.
name|changeTableColumnProject
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
name|C_BRANCH
argument_list|,
name|Util
operator|.
name|C
operator|.
name|changeTableColumnBranch
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
name|C_LAST_UPDATE
argument_list|,
name|Util
operator|.
name|C
operator|.
name|changeTableColumnLastUpdate
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
name|C_STAR
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|iconHeader
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|C_SUBJECT
init|;
name|i
operator|<
name|columns
condition|;
name|i
operator|++
control|)
block|{
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|0
argument_list|,
name|i
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|dataHeader
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|table
operator|.
name|addClickHandler
argument_list|(
operator|new
name|ClickHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onClick
parameter_list|(
specifier|final
name|ClickEvent
name|event
parameter_list|)
block|{
specifier|final
name|Cell
name|cell
init|=
name|table
operator|.
name|getCellForEvent
argument_list|(
name|event
argument_list|)
decl_stmt|;
if|if
condition|(
name|cell
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|cell
operator|.
name|getCellIndex
argument_list|()
operator|==
name|C_STAR
condition|)
block|{
comment|// Don't do anything (handled by star itself).
block|}
elseif|else
if|if
condition|(
name|cell
operator|.
name|getCellIndex
argument_list|()
operator|==
name|C_OWNER
condition|)
block|{
comment|// Don't do anything.
block|}
elseif|else
if|if
condition|(
name|getRowItem
argument_list|(
name|cell
operator|.
name|getRowIndex
argument_list|()
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|movePointerTo
argument_list|(
name|cell
operator|.
name|getRowIndex
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRowItemKey (final ChangeInfo item)
specifier|protected
name|Object
name|getRowItemKey
parameter_list|(
specifier|final
name|ChangeInfo
name|item
parameter_list|)
block|{
return|return
name|item
operator|.
name|legacy_id
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|onOpenRow (final int row)
specifier|protected
name|void
name|onOpenRow
parameter_list|(
specifier|final
name|int
name|row
parameter_list|)
block|{
specifier|final
name|ChangeInfo
name|c
init|=
name|getRowItem
argument_list|(
name|row
argument_list|)
decl_stmt|;
specifier|final
name|Change
operator|.
name|Id
name|id
init|=
name|c
operator|.
name|legacy_id
argument_list|()
decl_stmt|;
name|Gerrit
operator|.
name|display
argument_list|(
name|PageLinks
operator|.
name|toChange
argument_list|(
name|id
argument_list|)
argument_list|,
operator|new
name|ChangeScreen
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|insertNoneRow (final int row)
specifier|private
name|void
name|insertNoneRow
parameter_list|(
specifier|final
name|int
name|row
parameter_list|)
block|{
name|insertRow
argument_list|(
name|row
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
name|row
argument_list|,
literal|0
argument_list|,
name|Util
operator|.
name|C
operator|.
name|changeTableNone
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
name|setColSpan
argument_list|(
name|row
argument_list|,
literal|0
argument_list|,
name|columns
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|setStyleName
argument_list|(
name|row
argument_list|,
literal|0
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|emptySection
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|insertChangeRow (final int row)
specifier|private
name|void
name|insertChangeRow
parameter_list|(
specifier|final
name|int
name|row
parameter_list|)
block|{
name|insertRow
argument_list|(
name|row
argument_list|)
expr_stmt|;
name|applyDataRowStyle
argument_list|(
name|row
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
name|C_STAR
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|iconCell
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|C_SUBJECT
init|;
name|i
operator|<
name|columns
condition|;
name|i
operator|++
control|)
block|{
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|i
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|dataCell
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|C_SUBJECT
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|cSUBJECT
argument_list|()
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|C_OWNER
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|cOWNER
argument_list|()
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|C_LAST_UPDATE
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|cLastUpdate
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|BASE_COLUMNS
init|;
name|i
operator|<
name|columns
condition|;
name|i
operator|++
control|)
block|{
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|i
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|cAPPROVAL
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|updateColumnsForLabels (ChangeList... lists)
specifier|public
name|void
name|updateColumnsForLabels
parameter_list|(
name|ChangeList
modifier|...
name|lists
parameter_list|)
block|{
name|labelNames
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|ChangeList
name|list
range|:
name|lists
control|)
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
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|String
name|name
range|:
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|labels
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|labelNames
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|labelNames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|labelNames
argument_list|)
expr_stmt|;
if|if
condition|(
name|BASE_COLUMNS
operator|+
name|labelNames
operator|.
name|size
argument_list|()
operator|<
name|columns
condition|)
block|{
name|int
name|n
init|=
name|columns
operator|-
operator|(
name|BASE_COLUMNS
operator|+
name|labelNames
operator|.
name|size
argument_list|()
operator|)
decl_stmt|;
for|for
control|(
name|int
name|row
init|=
literal|0
init|;
name|row
operator|<
name|table
operator|.
name|getRowCount
argument_list|()
condition|;
name|row
operator|++
control|)
block|{
name|table
operator|.
name|removeCells
argument_list|(
name|row
argument_list|,
name|columns
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
block|}
name|columns
operator|=
name|BASE_COLUMNS
operator|+
name|labelNames
operator|.
name|size
argument_list|()
expr_stmt|;
name|FlexCellFormatter
name|fmt
init|=
name|table
operator|.
name|getFlexCellFormatter
argument_list|()
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
name|labelNames
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|labelNames
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|col
init|=
name|BASE_COLUMNS
operator|+
name|i
decl_stmt|;
name|StringBuilder
name|abbrev
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|t
range|:
name|name
operator|.
name|split
argument_list|(
literal|"-"
argument_list|)
control|)
block|{
name|abbrev
operator|.
name|append
argument_list|(
name|t
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
name|col
argument_list|,
name|abbrev
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|getCellFormatter
argument_list|()
operator|.
name|getElement
argument_list|(
literal|0
argument_list|,
name|col
argument_list|)
operator|.
name|setTitle
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|0
argument_list|,
name|col
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|dataHeader
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Section
name|s
range|:
name|sections
control|)
block|{
if|if
condition|(
name|s
operator|.
name|titleRow
operator|>=
literal|0
condition|)
block|{
name|fmt
operator|.
name|setColSpan
argument_list|(
name|s
operator|.
name|titleRow
argument_list|,
literal|0
argument_list|,
name|columns
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|populateChangeRow (final int row, final ChangeInfo c, boolean highlightUnreviewed)
specifier|private
name|void
name|populateChangeRow
parameter_list|(
specifier|final
name|int
name|row
parameter_list|,
specifier|final
name|ChangeInfo
name|c
parameter_list|,
name|boolean
name|highlightUnreviewed
parameter_list|)
block|{
if|if
condition|(
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
condition|)
block|{
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
name|C_STAR
argument_list|,
name|StarredChanges
operator|.
name|createIcon
argument_list|(
name|c
operator|.
name|legacy_id
argument_list|()
argument_list|,
name|c
operator|.
name|starred
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|subject
init|=
name|Util
operator|.
name|cropSubject
argument_list|(
name|c
operator|.
name|subject
argument_list|()
argument_list|)
decl_stmt|;
name|Change
operator|.
name|Status
name|status
init|=
name|c
operator|.
name|status
argument_list|()
decl_stmt|;
if|if
condition|(
name|status
operator|!=
name|Change
operator|.
name|Status
operator|.
name|NEW
condition|)
block|{
name|subject
operator|+=
literal|" ("
operator|+
name|Util
operator|.
name|toLongString
argument_list|(
name|status
argument_list|)
operator|+
literal|")"
expr_stmt|;
block|}
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
name|C_SUBJECT
argument_list|,
operator|new
name|TableChangeLink
argument_list|(
name|subject
argument_list|,
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|owner
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|c
operator|.
name|owner
argument_list|()
operator|!=
literal|null
operator|&&
name|c
operator|.
name|owner
argument_list|()
operator|.
name|name
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|owner
operator|=
name|c
operator|.
name|owner
argument_list|()
operator|.
name|name
argument_list|()
expr_stmt|;
block|}
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
name|C_OWNER
argument_list|,
operator|new
name|InlineHyperlink
argument_list|(
name|owner
argument_list|,
name|PageLinks
operator|.
name|toAccountQuery
argument_list|(
name|owner
argument_list|,
name|c
operator|.
name|status
argument_list|()
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
name|C_PROJECT
argument_list|,
operator|new
name|ProjectLink
argument_list|(
name|c
operator|.
name|project_name_key
argument_list|()
argument_list|,
name|c
operator|.
name|status
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
name|C_BRANCH
argument_list|,
operator|new
name|BranchLink
argument_list|(
name|c
operator|.
name|project_name_key
argument_list|()
argument_list|,
name|c
operator|.
name|status
argument_list|()
argument_list|,
name|c
operator|.
name|branch
argument_list|()
argument_list|,
name|c
operator|.
name|topic
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
name|row
argument_list|,
name|C_LAST_UPDATE
argument_list|,
name|shortFormat
argument_list|(
name|c
operator|.
name|updated
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|displayName
init|=
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
operator|&&
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
operator|.
name|getGeneralPreferences
argument_list|()
operator|.
name|isShowUsernameInReviewCategory
argument_list|()
decl_stmt|;
name|CellFormatter
name|fmt
init|=
name|table
operator|.
name|getCellFormatter
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|labelNames
operator|.
name|size
argument_list|()
condition|;
name|idx
operator|++
control|)
block|{
name|String
name|name
init|=
name|labelNames
operator|.
name|get
argument_list|(
name|idx
argument_list|)
decl_stmt|;
name|int
name|col
init|=
name|BASE_COLUMNS
operator|+
name|idx
decl_stmt|;
name|LabelInfo
name|label
init|=
name|c
operator|.
name|label
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|label
operator|==
literal|null
condition|)
block|{
name|table
operator|.
name|clearCell
argument_list|(
name|row
argument_list|,
name|col
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|String
name|user
decl_stmt|;
if|if
condition|(
name|label
operator|.
name|rejected
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|user
operator|=
name|label
operator|.
name|rejected
argument_list|()
operator|.
name|name
argument_list|()
expr_stmt|;
if|if
condition|(
name|displayName
operator|&&
name|user
operator|!=
literal|null
condition|)
block|{
name|FlowPanel
name|panel
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|panel
operator|.
name|add
argument_list|(
operator|new
name|Image
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|redNot
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|panel
operator|.
name|add
argument_list|(
operator|new
name|InlineLabel
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
name|col
argument_list|,
name|panel
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
name|col
argument_list|,
operator|new
name|Image
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|redNot
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|label
operator|.
name|approved
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|user
operator|=
name|label
operator|.
name|approved
argument_list|()
operator|.
name|name
argument_list|()
expr_stmt|;
if|if
condition|(
name|displayName
operator|&&
name|user
operator|!=
literal|null
condition|)
block|{
name|FlowPanel
name|panel
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|panel
operator|.
name|add
argument_list|(
operator|new
name|Image
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|greenCheck
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|panel
operator|.
name|add
argument_list|(
operator|new
name|InlineLabel
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
name|col
argument_list|,
name|panel
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
name|col
argument_list|,
operator|new
name|Image
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|greenCheck
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|label
operator|.
name|disliked
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|user
operator|=
name|label
operator|.
name|disliked
argument_list|()
operator|.
name|name
argument_list|()
expr_stmt|;
name|String
name|vstr
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|label
operator|.
name|_value
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|displayName
operator|&&
name|user
operator|!=
literal|null
condition|)
block|{
name|vstr
operator|=
name|vstr
operator|+
literal|" "
operator|+
name|user
expr_stmt|;
block|}
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|col
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|negscore
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
name|row
argument_list|,
name|col
argument_list|,
name|vstr
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|label
operator|.
name|recommended
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|user
operator|=
name|label
operator|.
name|recommended
argument_list|()
operator|.
name|name
argument_list|()
expr_stmt|;
name|String
name|vstr
init|=
literal|"+"
operator|+
name|label
operator|.
name|_value
argument_list|()
decl_stmt|;
if|if
condition|(
name|displayName
operator|&&
name|user
operator|!=
literal|null
condition|)
block|{
name|vstr
operator|=
name|vstr
operator|+
literal|" "
operator|+
name|user
expr_stmt|;
block|}
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|col
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|posscore
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
name|row
argument_list|,
name|col
argument_list|,
name|vstr
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
name|col
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|col
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|singleLine
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|displayName
operator|&&
name|user
operator|!=
literal|null
condition|)
block|{
comment|// Some web browsers ignore the embedded newline; some like it;
comment|// so we include a space before the newline to accommodate both.
name|fmt
operator|.
name|getElement
argument_list|(
name|row
argument_list|,
name|col
argument_list|)
operator|.
name|setTitle
argument_list|(
name|name
operator|+
literal|" \nby "
operator|+
name|user
argument_list|)
expr_stmt|;
block|}
block|}
name|boolean
name|needHighlight
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|highlightUnreviewed
operator|&&
operator|!
name|c
operator|.
name|reviewed
argument_list|()
condition|)
block|{
name|needHighlight
operator|=
literal|true
expr_stmt|;
block|}
specifier|final
name|Element
name|tr
init|=
name|DOM
operator|.
name|getParent
argument_list|(
name|fmt
operator|.
name|getElement
argument_list|(
name|row
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|UIObject
operator|.
name|setStyleName
argument_list|(
name|tr
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|needsReview
argument_list|()
argument_list|,
name|needHighlight
argument_list|)
expr_stmt|;
name|setRowItem
argument_list|(
name|row
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
DECL|method|addSection (final Section s)
specifier|public
name|void
name|addSection
parameter_list|(
specifier|final
name|Section
name|s
parameter_list|)
block|{
assert|assert
name|s
operator|.
name|parent
operator|==
literal|null
assert|;
name|s
operator|.
name|parent
operator|=
name|this
expr_stmt|;
name|s
operator|.
name|titleRow
operator|=
name|table
operator|.
name|getRowCount
argument_list|()
expr_stmt|;
if|if
condition|(
name|s
operator|.
name|displayTitle
argument_list|()
condition|)
block|{
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
name|setColSpan
argument_list|(
name|s
operator|.
name|titleRow
argument_list|,
literal|0
argument_list|,
name|columns
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
name|s
operator|.
name|titleRow
argument_list|,
literal|0
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|sectionHeader
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|s
operator|.
name|titleRow
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|s
operator|.
name|dataBegin
operator|=
name|table
operator|.
name|getRowCount
argument_list|()
expr_stmt|;
name|insertNoneRow
argument_list|(
name|s
operator|.
name|dataBegin
argument_list|)
expr_stmt|;
name|sections
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|insertRow (final int beforeRow)
specifier|private
name|int
name|insertRow
parameter_list|(
specifier|final
name|int
name|beforeRow
parameter_list|)
block|{
for|for
control|(
specifier|final
name|Section
name|s
range|:
name|sections
control|)
block|{
if|if
condition|(
name|beforeRow
operator|<=
name|s
operator|.
name|titleRow
condition|)
block|{
name|s
operator|.
name|titleRow
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|beforeRow
operator|<
name|s
operator|.
name|dataBegin
condition|)
block|{
name|s
operator|.
name|dataBegin
operator|++
expr_stmt|;
block|}
block|}
return|return
name|table
operator|.
name|insertRow
argument_list|(
name|beforeRow
argument_list|)
return|;
block|}
DECL|method|removeRow (final int row)
specifier|private
name|void
name|removeRow
parameter_list|(
specifier|final
name|int
name|row
parameter_list|)
block|{
for|for
control|(
specifier|final
name|Section
name|s
range|:
name|sections
control|)
block|{
if|if
condition|(
name|row
operator|<
name|s
operator|.
name|titleRow
condition|)
block|{
name|s
operator|.
name|titleRow
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|row
operator|<
name|s
operator|.
name|dataBegin
condition|)
block|{
name|s
operator|.
name|dataBegin
operator|--
expr_stmt|;
block|}
block|}
name|table
operator|.
name|removeRow
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
DECL|class|StarKeyCommand
specifier|public
class|class
name|StarKeyCommand
extends|extends
name|NeedsSignInKeyCommand
block|{
DECL|method|StarKeyCommand (int mask, char key, String help)
specifier|public
name|StarKeyCommand
parameter_list|(
name|int
name|mask
parameter_list|,
name|char
name|key
parameter_list|,
name|String
name|help
parameter_list|)
block|{
name|super
argument_list|(
name|mask
argument_list|,
name|key
argument_list|,
name|help
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onKeyPress (final KeyPressEvent event)
specifier|public
name|void
name|onKeyPress
parameter_list|(
specifier|final
name|KeyPressEvent
name|event
parameter_list|)
block|{
name|int
name|row
init|=
name|getCurrentRow
argument_list|()
decl_stmt|;
name|ChangeInfo
name|c
init|=
name|getRowItem
argument_list|(
name|row
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
operator|&&
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
condition|)
block|{
operator|(
operator|(
name|StarredChanges
operator|.
name|Icon
operator|)
name|table
operator|.
name|getWidget
argument_list|(
name|row
argument_list|,
name|C_STAR
argument_list|)
operator|)
operator|.
name|toggleStar
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|class|TableChangeLink
specifier|private
specifier|final
class|class
name|TableChangeLink
extends|extends
name|ChangeLink
block|{
DECL|method|TableChangeLink (final String text, final ChangeInfo c)
specifier|private
name|TableChangeLink
parameter_list|(
specifier|final
name|String
name|text
parameter_list|,
specifier|final
name|ChangeInfo
name|c
parameter_list|)
block|{
name|super
argument_list|(
name|text
argument_list|,
name|c
operator|.
name|legacy_id
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|go ()
specifier|public
name|void
name|go
parameter_list|()
block|{
name|movePointerTo
argument_list|(
name|cid
argument_list|)
expr_stmt|;
name|super
operator|.
name|go
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|Section
specifier|public
specifier|static
class|class
name|Section
block|{
DECL|field|parent
name|ChangeTable2
name|parent
decl_stmt|;
DECL|field|titleText
name|String
name|titleText
decl_stmt|;
DECL|field|titleWidget
name|Widget
name|titleWidget
decl_stmt|;
DECL|field|titleRow
name|int
name|titleRow
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|dataBegin
name|int
name|dataBegin
decl_stmt|;
DECL|field|rows
name|int
name|rows
decl_stmt|;
DECL|field|highlightUnreviewed
specifier|private
name|boolean
name|highlightUnreviewed
decl_stmt|;
DECL|method|setHighlightUnreviewed (boolean value)
specifier|public
name|void
name|setHighlightUnreviewed
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|this
operator|.
name|highlightUnreviewed
operator|=
name|value
expr_stmt|;
block|}
DECL|method|setTitleText (final String text)
specifier|public
name|void
name|setTitleText
parameter_list|(
specifier|final
name|String
name|text
parameter_list|)
block|{
name|titleText
operator|=
name|text
expr_stmt|;
name|titleWidget
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|titleRow
operator|>=
literal|0
condition|)
block|{
name|parent
operator|.
name|table
operator|.
name|setText
argument_list|(
name|titleRow
argument_list|,
literal|0
argument_list|,
name|titleText
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setTitleWidget (final Widget title)
specifier|public
name|void
name|setTitleWidget
parameter_list|(
specifier|final
name|Widget
name|title
parameter_list|)
block|{
name|titleWidget
operator|=
name|title
expr_stmt|;
name|titleText
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|titleRow
operator|>=
literal|0
condition|)
block|{
name|parent
operator|.
name|table
operator|.
name|setWidget
argument_list|(
name|titleRow
argument_list|,
literal|0
argument_list|,
name|title
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|displayTitle ()
specifier|public
name|boolean
name|displayTitle
parameter_list|()
block|{
if|if
condition|(
name|titleText
operator|!=
literal|null
condition|)
block|{
name|setTitleText
argument_list|(
name|titleText
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|titleWidget
operator|!=
literal|null
condition|)
block|{
name|setTitleWidget
argument_list|(
name|titleWidget
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|display (ChangeList changeList)
specifier|public
name|void
name|display
parameter_list|(
name|ChangeList
name|changeList
parameter_list|)
block|{
specifier|final
name|int
name|sz
init|=
name|changeList
operator|!=
literal|null
condition|?
name|changeList
operator|.
name|size
argument_list|()
else|:
literal|0
decl_stmt|;
specifier|final
name|boolean
name|hadData
init|=
name|rows
operator|>
literal|0
decl_stmt|;
if|if
condition|(
name|hadData
condition|)
block|{
while|while
condition|(
name|sz
operator|<
name|rows
condition|)
block|{
name|parent
operator|.
name|removeRow
argument_list|(
name|dataBegin
argument_list|)
expr_stmt|;
name|rows
operator|--
expr_stmt|;
block|}
block|}
else|else
block|{
name|parent
operator|.
name|removeRow
argument_list|(
name|dataBegin
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sz
operator|==
literal|0
condition|)
block|{
name|parent
operator|.
name|insertNoneRow
argument_list|(
name|dataBegin
argument_list|)
expr_stmt|;
return|return;
block|}
while|while
condition|(
name|rows
operator|<
name|sz
condition|)
block|{
name|parent
operator|.
name|insertChangeRow
argument_list|(
name|dataBegin
operator|+
name|rows
argument_list|)
expr_stmt|;
name|rows
operator|++
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
name|parent
operator|.
name|populateChangeRow
argument_list|(
name|dataBegin
operator|+
name|i
argument_list|,
name|changeList
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|highlightUnreviewed
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

