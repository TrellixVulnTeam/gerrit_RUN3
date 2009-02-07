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
name|mediumFormat
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
name|SignedInListener
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
name|AccountInfoCache
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
name|ChangeInfo
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
name|Account
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
name|Change
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
name|rpc
operator|.
name|GerritCallback
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
name|AccountDashboardLink
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
name|ProjectOpenLink
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
name|Event
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
name|AbstractImagePrototype
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
name|SourcesTableEvents
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
name|TableListener
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
name|com
operator|.
name|google
operator|.
name|gwtjsonrpc
operator|.
name|client
operator|.
name|VoidResult
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
DECL|class|ChangeTable
specifier|public
class|class
name|ChangeTable
extends|extends
name|FancyFlexTable
argument_list|<
name|ChangeInfo
argument_list|>
block|{
DECL|field|S_C_ID
specifier|private
specifier|static
specifier|final
name|String
name|S_C_ID
init|=
literal|"C_ID"
decl_stmt|;
DECL|field|S_C_SUBJECT
specifier|private
specifier|static
specifier|final
name|String
name|S_C_SUBJECT
init|=
literal|"C_SUBJECT"
decl_stmt|;
DECL|field|S_C_PROJECT
specifier|private
specifier|static
specifier|final
name|String
name|S_C_PROJECT
init|=
literal|"C_PROJECT"
decl_stmt|;
DECL|field|S_C_LAST_UPDATE
specifier|private
specifier|static
specifier|final
name|String
name|S_C_LAST_UPDATE
init|=
literal|"C_LAST_UPDATE"
decl_stmt|;
DECL|field|S_SECTION_HEADER
specifier|private
specifier|static
specifier|final
name|String
name|S_SECTION_HEADER
init|=
literal|"SectionHeader"
decl_stmt|;
DECL|field|S_EMPTY_SECTION
specifier|private
specifier|static
specifier|final
name|String
name|S_EMPTY_SECTION
init|=
literal|"EmptySection"
decl_stmt|;
DECL|field|C_STAR
specifier|private
specifier|static
specifier|final
name|int
name|C_STAR
init|=
literal|1
decl_stmt|;
DECL|field|C_ID
specifier|private
specifier|static
specifier|final
name|int
name|C_ID
init|=
literal|2
decl_stmt|;
DECL|field|C_SUBJECT
specifier|private
specifier|static
specifier|final
name|int
name|C_SUBJECT
init|=
literal|3
decl_stmt|;
DECL|field|C_OWNER
specifier|private
specifier|static
specifier|final
name|int
name|C_OWNER
init|=
literal|4
decl_stmt|;
DECL|field|C_PROJECT
specifier|private
specifier|static
specifier|final
name|int
name|C_PROJECT
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
DECL|field|COLUMNS
specifier|private
specifier|static
specifier|final
name|int
name|COLUMNS
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
DECL|field|signedInListener
specifier|private
specifier|final
name|SignedInListener
name|signedInListener
decl_stmt|;
DECL|field|accountCache
specifier|private
name|AccountInfoCache
name|accountCache
init|=
name|AccountInfoCache
operator|.
name|empty
argument_list|()
decl_stmt|;
DECL|method|ChangeTable ()
specifier|public
name|ChangeTable
parameter_list|()
block|{
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
name|C_ID
argument_list|,
name|Util
operator|.
name|C
operator|.
name|changeTableColumnID
argument_list|()
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
name|S_ICON_HEADER
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|0
argument_list|,
name|C_ID
argument_list|,
name|S_C_ID
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|C_ID
init|;
name|i
operator|<
name|COLUMNS
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
name|S_DATA_HEADER
argument_list|)
expr_stmt|;
block|}
name|table
operator|.
name|addTableListener
argument_list|(
operator|new
name|TableListener
argument_list|()
block|{
specifier|public
name|void
name|onCellClicked
parameter_list|(
name|SourcesTableEvents
name|sender
parameter_list|,
name|int
name|row
parameter_list|,
name|int
name|cell
parameter_list|)
block|{
if|if
condition|(
name|cell
operator|==
name|C_STAR
condition|)
block|{
name|onStarClick
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cell
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
name|row
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|movePointerTo
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|signedInListener
operator|=
operator|new
name|SignedInListener
argument_list|()
block|{
specifier|public
name|void
name|onSignIn
parameter_list|()
block|{
if|if
condition|(
name|table
operator|.
name|getRowCount
argument_list|()
operator|<=
name|sections
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// There are no data rows in this table, so star status is
comment|// simply not relevant to the caller.
comment|//
return|return;
block|}
name|Util
operator|.
name|LIST_SVC
operator|.
name|myStarredChangeIds
argument_list|(
operator|new
name|GerritCallback
argument_list|<
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
specifier|final
name|Set
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
name|result
parameter_list|)
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
specifier|final
name|int
name|max
init|=
name|table
operator|.
name|getRowCount
argument_list|()
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
name|max
condition|;
name|row
operator|++
control|)
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
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
name|c
operator|.
name|setStarred
argument_list|(
name|result
operator|.
name|contains
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|setStar
argument_list|(
name|row
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onSignOut
parameter_list|()
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
specifier|final
name|int
name|max
init|=
name|table
operator|.
name|getRowCount
argument_list|()
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
name|max
condition|;
name|row
operator|++
control|)
block|{
if|if
condition|(
name|getRowItem
argument_list|(
name|row
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|table
operator|.
name|clearCell
argument_list|(
name|row
argument_list|,
name|C_STAR
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
expr_stmt|;
block|}
DECL|method|onStarClick (final int row)
specifier|protected
name|void
name|onStarClick
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
specifier|final
name|boolean
name|prior
init|=
name|c
operator|.
name|isStarred
argument_list|()
decl_stmt|;
name|c
operator|.
name|setStarred
argument_list|(
operator|!
name|prior
argument_list|)
expr_stmt|;
name|setStar
argument_list|(
name|row
argument_list|,
name|c
argument_list|)
expr_stmt|;
specifier|final
name|ToggleStarRequest
name|req
init|=
operator|new
name|ToggleStarRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|toggle
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|,
name|c
operator|.
name|isStarred
argument_list|()
argument_list|)
expr_stmt|;
name|Util
operator|.
name|LIST_SVC
operator|.
name|toggleStars
argument_list|(
name|req
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|VoidResult
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
specifier|final
name|VoidResult
name|result
parameter_list|)
block|{         }
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
specifier|final
name|Throwable
name|caught
parameter_list|)
block|{
name|super
operator|.
name|onFailure
argument_list|(
name|caught
argument_list|)
expr_stmt|;
name|c
operator|.
name|setStarred
argument_list|(
name|prior
argument_list|)
expr_stmt|;
name|setStar
argument_list|(
name|row
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
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
name|getId
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|onKeyPress (final char keyCode, final int modifiers)
specifier|protected
name|boolean
name|onKeyPress
parameter_list|(
specifier|final
name|char
name|keyCode
parameter_list|,
specifier|final
name|int
name|modifiers
parameter_list|)
block|{
if|if
condition|(
name|super
operator|.
name|onKeyPress
argument_list|(
name|keyCode
argument_list|,
name|modifiers
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|modifiers
operator|==
literal|0
condition|)
block|{
switch|switch
condition|(
name|keyCode
condition|)
block|{
case|case
literal|'s'
case|:
name|onStarClick
argument_list|(
name|getCurrentRow
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|onOpenItem (final ChangeInfo c)
specifier|protected
name|void
name|onOpenItem
parameter_list|(
specifier|final
name|ChangeInfo
name|c
parameter_list|)
block|{
name|Gerrit
operator|.
name|display
argument_list|(
name|Link
operator|.
name|toChange
argument_list|(
name|c
argument_list|)
argument_list|,
operator|new
name|ChangeScreen
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onLoad ()
specifier|public
name|void
name|onLoad
parameter_list|()
block|{
name|super
operator|.
name|onLoad
argument_list|()
expr_stmt|;
name|Gerrit
operator|.
name|addSignedInListener
argument_list|(
name|signedInListener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onUnload ()
specifier|public
name|void
name|onUnload
parameter_list|()
block|{
name|Gerrit
operator|.
name|removeSignedInListener
argument_list|(
name|signedInListener
argument_list|)
expr_stmt|;
name|super
operator|.
name|onUnload
argument_list|()
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
name|COLUMNS
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
name|S_EMPTY_SECTION
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
name|S_ICON_CELL
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|C_ID
init|;
name|i
operator|<
name|COLUMNS
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
name|S_DATA_CELL
argument_list|)
expr_stmt|;
block|}
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|C_ID
argument_list|,
name|S_C_ID
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|C_SUBJECT
argument_list|,
name|S_C_SUBJECT
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|C_PROJECT
argument_list|,
name|S_C_PROJECT
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
name|S_C_LAST_UPDATE
argument_list|)
expr_stmt|;
block|}
DECL|method|populateChangeRow (final int row, final ChangeInfo c)
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
parameter_list|)
block|{
specifier|final
name|String
name|idstr
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|c
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
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
if|if
condition|(
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
condition|)
block|{
name|setStar
argument_list|(
name|row
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
name|C_ID
argument_list|,
operator|new
name|TableChangeLink
argument_list|(
name|idstr
argument_list|,
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|s
init|=
name|c
operator|.
name|getSubject
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|>
literal|80
condition|)
block|{
name|s
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|80
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|c
operator|.
name|getStatus
argument_list|()
operator|!=
literal|null
operator|&&
name|c
operator|.
name|getStatus
argument_list|()
operator|!=
name|Change
operator|.
name|Status
operator|.
name|NEW
condition|)
block|{
name|s
operator|+=
literal|" ("
operator|+
name|c
operator|.
name|getStatus
argument_list|()
operator|.
name|name
argument_list|()
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
name|s
argument_list|,
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
name|C_OWNER
argument_list|,
name|link
argument_list|(
name|c
operator|.
name|getOwner
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
name|C_PROJECT
argument_list|,
operator|new
name|ProjectOpenLink
argument_list|(
name|c
operator|.
name|getProject
argument_list|()
operator|.
name|getKey
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
name|mediumFormat
argument_list|(
name|c
operator|.
name|getLastUpdatedOn
argument_list|()
argument_list|)
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
DECL|method|link (final Account.Id id)
specifier|private
name|AccountDashboardLink
name|link
parameter_list|(
specifier|final
name|Account
operator|.
name|Id
name|id
parameter_list|)
block|{
return|return
name|AccountDashboardLink
operator|.
name|link
argument_list|(
name|accountCache
argument_list|,
name|id
argument_list|)
return|;
block|}
DECL|method|setStar (final int row, final ChangeInfo c)
specifier|private
name|void
name|setStar
parameter_list|(
specifier|final
name|int
name|row
parameter_list|,
specifier|final
name|ChangeInfo
name|c
parameter_list|)
block|{
specifier|final
name|AbstractImagePrototype
name|star
decl_stmt|;
if|if
condition|(
name|c
operator|.
name|isStarred
argument_list|()
condition|)
block|{
name|star
operator|=
name|Gerrit
operator|.
name|ICONS
operator|.
name|starFilled
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|star
operator|=
name|Gerrit
operator|.
name|ICONS
operator|.
name|starOpen
argument_list|()
expr_stmt|;
block|}
specifier|final
name|Widget
name|i
init|=
name|table
operator|.
name|getWidget
argument_list|(
name|row
argument_list|,
name|C_STAR
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|instanceof
name|Image
condition|)
block|{
name|star
operator|.
name|applyTo
argument_list|(
operator|(
name|Image
operator|)
name|i
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
name|C_STAR
argument_list|,
name|star
operator|.
name|createImage
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|s
operator|.
name|titleText
operator|!=
literal|null
condition|)
block|{
name|s
operator|.
name|titleRow
operator|=
name|table
operator|.
name|getRowCount
argument_list|()
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
name|s
operator|.
name|titleRow
argument_list|,
literal|0
argument_list|,
name|s
operator|.
name|titleText
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
name|s
operator|.
name|titleRow
argument_list|,
literal|0
argument_list|,
name|COLUMNS
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
name|S_SECTION_HEADER
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
name|parent
operator|=
name|this
expr_stmt|;
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
DECL|method|setAccountInfoCache (final AccountInfoCache aic)
specifier|public
name|void
name|setAccountInfoCache
parameter_list|(
specifier|final
name|AccountInfoCache
name|aic
parameter_list|)
block|{
assert|assert
name|aic
operator|!=
literal|null
assert|;
name|accountCache
operator|=
name|aic
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
name|boolean
name|dirty
init|=
literal|false
decl_stmt|;
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
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onClick (final Event event)
specifier|protected
name|void
name|onClick
parameter_list|(
specifier|final
name|Event
name|event
parameter_list|)
block|{
name|movePointerTo
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|super
operator|.
name|onClick
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Section
specifier|public
specifier|static
class|class
name|Section
block|{
DECL|field|titleText
name|String
name|titleText
decl_stmt|;
DECL|field|parent
name|ChangeTable
name|parent
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
DECL|method|Section ()
specifier|public
name|Section
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|Section (final String titleText)
specifier|public
name|Section
parameter_list|(
specifier|final
name|String
name|titleText
parameter_list|)
block|{
name|setTitleText
argument_list|(
name|titleText
argument_list|)
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
DECL|method|display (final List<ChangeInfo> changeList)
specifier|public
name|void
name|display
parameter_list|(
specifier|final
name|List
argument_list|<
name|ChangeInfo
argument_list|>
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
if|if
condition|(
name|sz
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|hadData
condition|)
block|{
name|parent
operator|.
name|insertNoneRow
argument_list|(
name|dataBegin
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
operator|!
name|hadData
condition|)
block|{
name|parent
operator|.
name|removeRow
argument_list|(
name|dataBegin
argument_list|)
expr_stmt|;
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
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

