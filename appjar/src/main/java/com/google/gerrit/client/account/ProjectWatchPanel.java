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
DECL|package|com.google.gerrit.client.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|account
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
name|reviewdb
operator|.
name|AccountProjectWatch
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
name|ProjectNameSuggestOracle
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
name|Button
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
name|CheckBox
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
name|ClickListener
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
name|Composite
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
name|FocusListenerAdapter
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
name|SuggestBox
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
name|TextBox
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
name|HashSet
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
DECL|class|ProjectWatchPanel
class|class
name|ProjectWatchPanel
extends|extends
name|Composite
block|{
DECL|field|watches
specifier|private
name|WatchTable
name|watches
decl_stmt|;
DECL|field|addNew
specifier|private
name|Button
name|addNew
decl_stmt|;
DECL|field|nameTxt
specifier|private
name|SuggestBox
name|nameTxt
decl_stmt|;
DECL|field|delSel
specifier|private
name|Button
name|delSel
decl_stmt|;
DECL|method|ProjectWatchPanel ()
name|ProjectWatchPanel
parameter_list|()
block|{
specifier|final
name|FlowPanel
name|body
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
block|{
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
name|setStyleName
argument_list|(
literal|"gerrit-ProjectWatchPanel-AddPanel"
argument_list|)
expr_stmt|;
specifier|final
name|TextBox
name|box
init|=
operator|new
name|TextBox
argument_list|()
decl_stmt|;
name|nameTxt
operator|=
operator|new
name|SuggestBox
argument_list|(
operator|new
name|ProjectNameSuggestOracle
argument_list|()
argument_list|,
name|box
argument_list|)
expr_stmt|;
name|box
operator|.
name|setVisibleLength
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|box
operator|.
name|setText
argument_list|(
name|Util
operator|.
name|C
operator|.
name|defaultProjectName
argument_list|()
argument_list|)
expr_stmt|;
name|box
operator|.
name|addStyleName
argument_list|(
literal|"gerrit-InputFieldTypeHint"
argument_list|)
expr_stmt|;
name|box
operator|.
name|addFocusListener
argument_list|(
operator|new
name|FocusListenerAdapter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onFocus
parameter_list|(
name|Widget
name|sender
parameter_list|)
block|{
if|if
condition|(
name|Util
operator|.
name|C
operator|.
name|defaultProjectName
argument_list|()
operator|.
name|equals
argument_list|(
name|box
operator|.
name|getText
argument_list|()
argument_list|)
condition|)
block|{
name|box
operator|.
name|setText
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|box
operator|.
name|removeStyleName
argument_list|(
literal|"gerrit-InputFieldTypeHint"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onLostFocus
parameter_list|(
name|Widget
name|sender
parameter_list|)
block|{
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|box
operator|.
name|getText
argument_list|()
argument_list|)
condition|)
block|{
name|box
operator|.
name|setText
argument_list|(
name|Util
operator|.
name|C
operator|.
name|defaultProjectName
argument_list|()
argument_list|)
expr_stmt|;
name|box
operator|.
name|addStyleName
argument_list|(
literal|"gerrit-InputFieldTypeHint"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|fp
operator|.
name|add
argument_list|(
name|nameTxt
argument_list|)
expr_stmt|;
name|addNew
operator|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonWatchProject
argument_list|()
argument_list|)
expr_stmt|;
name|addNew
operator|.
name|addClickListener
argument_list|(
operator|new
name|ClickListener
argument_list|()
block|{
specifier|public
name|void
name|onClick
parameter_list|(
specifier|final
name|Widget
name|sender
parameter_list|)
block|{
name|doAddNew
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|fp
operator|.
name|add
argument_list|(
name|addNew
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|fp
argument_list|)
expr_stmt|;
block|}
name|watches
operator|=
operator|new
name|WatchTable
argument_list|()
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|watches
argument_list|)
expr_stmt|;
block|{
specifier|final
name|FlowPanel
name|fp
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|delSel
operator|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonDeleteSshKey
argument_list|()
argument_list|)
expr_stmt|;
name|delSel
operator|.
name|addClickListener
argument_list|(
operator|new
name|ClickListener
argument_list|()
block|{
specifier|public
name|void
name|onClick
parameter_list|(
specifier|final
name|Widget
name|sender
parameter_list|)
block|{
name|watches
operator|.
name|deleteChecked
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|fp
operator|.
name|add
argument_list|(
name|delSel
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|fp
argument_list|)
expr_stmt|;
block|}
name|initWidget
argument_list|(
name|body
argument_list|)
expr_stmt|;
block|}
DECL|method|doAddNew ()
name|void
name|doAddNew
parameter_list|()
block|{
specifier|final
name|String
name|projectName
init|=
name|nameTxt
operator|.
name|getText
argument_list|()
decl_stmt|;
if|if
condition|(
name|projectName
operator|==
literal|null
operator|||
name|projectName
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
name|Util
operator|.
name|C
operator|.
name|defaultProjectName
argument_list|()
operator|.
name|equals
argument_list|(
name|projectName
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|watches
operator|.
name|moveToExistingProject
argument_list|(
name|projectName
argument_list|)
condition|)
block|{
name|nameTxt
operator|.
name|setText
argument_list|(
literal|""
argument_list|)
expr_stmt|;
return|return;
block|}
name|addNew
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Util
operator|.
name|ACCOUNT_SVC
operator|.
name|addProjectWatch
argument_list|(
name|projectName
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|AccountProjectWatchInfo
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
specifier|final
name|AccountProjectWatchInfo
name|result
parameter_list|)
block|{
name|addNew
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|nameTxt
operator|.
name|setText
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|watches
operator|.
name|insertWatch
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
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
name|addNew
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|onFailure
argument_list|(
name|caught
argument_list|)
expr_stmt|;
block|}
block|}
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
name|Util
operator|.
name|ACCOUNT_SVC
operator|.
name|myProjectWatch
argument_list|(
operator|new
name|GerritCallback
argument_list|<
name|List
argument_list|<
name|AccountProjectWatchInfo
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
specifier|final
name|List
argument_list|<
name|AccountProjectWatchInfo
argument_list|>
name|result
parameter_list|)
block|{
name|watches
operator|.
name|display
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|watches
operator|.
name|finishDisplay
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|class|WatchTable
specifier|private
class|class
name|WatchTable
extends|extends
name|FancyFlexTable
argument_list|<
name|AccountProjectWatchInfo
argument_list|>
block|{
DECL|method|WatchTable ()
name|WatchTable
parameter_list|()
block|{
name|table
operator|.
name|insertRow
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|,
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
literal|3
argument_list|,
name|Util
operator|.
name|C
operator|.
name|watchedProjectColumnEmailNotifications
argument_list|()
argument_list|)
expr_stmt|;
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
switch|switch
condition|(
name|cell
condition|)
block|{
case|case
literal|1
case|:
case|case
literal|3
case|:
comment|// Don't do anything, these cells also contain check boxes.
break|break;
default|default:
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
break|break;
block|}
block|}
block|}
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
literal|1
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
literal|2
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
literal|3
argument_list|,
name|S_DATA_HEADER
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|setRowSpan
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|setRowSpan
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|setRowSpan
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|DOM
operator|.
name|setElementProperty
argument_list|(
name|fmt
operator|.
name|getElement
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|,
literal|"align"
argument_list|,
literal|"center"
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|setColSpan
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
name|Util
operator|.
name|C
operator|.
name|watchedProjectColumnNewChanges
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
name|Util
operator|.
name|C
operator|.
name|watchedProjectColumnAllComments
argument_list|()
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
name|S_DATA_HEADER
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
name|S_DATA_HEADER
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRowItemKey (final AccountProjectWatchInfo item)
specifier|protected
name|Object
name|getRowItemKey
parameter_list|(
specifier|final
name|AccountProjectWatchInfo
name|item
parameter_list|)
block|{
return|return
name|item
operator|.
name|getWatch
argument_list|()
operator|.
name|getKey
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
case|case
literal|'c'
case|:
name|toggleCurrentRow
argument_list|()
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
DECL|method|onOpenItem (final AccountProjectWatchInfo item)
specifier|protected
name|void
name|onOpenItem
parameter_list|(
specifier|final
name|AccountProjectWatchInfo
name|item
parameter_list|)
block|{
name|toggleCurrentRow
argument_list|()
expr_stmt|;
block|}
DECL|method|toggleCurrentRow ()
specifier|private
name|void
name|toggleCurrentRow
parameter_list|()
block|{
specifier|final
name|CheckBox
name|cb
init|=
operator|(
name|CheckBox
operator|)
name|table
operator|.
name|getWidget
argument_list|(
name|getCurrentRow
argument_list|()
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|cb
operator|.
name|setChecked
argument_list|(
operator|!
name|cb
operator|.
name|isChecked
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteChecked ()
name|void
name|deleteChecked
parameter_list|()
block|{
specifier|final
name|HashSet
argument_list|<
name|AccountProjectWatch
operator|.
name|Key
argument_list|>
name|ids
init|=
operator|new
name|HashSet
argument_list|<
name|AccountProjectWatch
operator|.
name|Key
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|row
init|=
literal|1
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
specifier|final
name|AccountProjectWatchInfo
name|k
init|=
name|getRowItem
argument_list|(
name|row
argument_list|)
decl_stmt|;
if|if
condition|(
name|k
operator|!=
literal|null
operator|&&
operator|(
operator|(
name|CheckBox
operator|)
name|table
operator|.
name|getWidget
argument_list|(
name|row
argument_list|,
literal|1
argument_list|)
operator|)
operator|.
name|isChecked
argument_list|()
condition|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|k
operator|.
name|getWatch
argument_list|()
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|ids
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Util
operator|.
name|ACCOUNT_SVC
operator|.
name|deleteProjectWatches
argument_list|(
name|ids
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
block|{
for|for
control|(
name|int
name|row
init|=
literal|1
init|;
name|row
operator|<
name|table
operator|.
name|getRowCount
argument_list|()
condition|;
control|)
block|{
specifier|final
name|AccountProjectWatchInfo
name|k
init|=
name|getRowItem
argument_list|(
name|row
argument_list|)
decl_stmt|;
if|if
condition|(
name|k
operator|!=
literal|null
operator|&&
name|ids
operator|.
name|contains
argument_list|(
name|k
operator|.
name|getWatch
argument_list|()
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|table
operator|.
name|removeRow
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|row
operator|++
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|moveToExistingProject (final String projectName)
name|boolean
name|moveToExistingProject
parameter_list|(
specifier|final
name|String
name|projectName
parameter_list|)
block|{
for|for
control|(
name|int
name|row
init|=
literal|1
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
specifier|final
name|AccountProjectWatchInfo
name|i
init|=
name|getRowItem
argument_list|(
name|row
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|!=
literal|null
operator|&&
name|i
operator|.
name|getProject
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|projectName
argument_list|)
condition|)
block|{
name|movePointerTo
argument_list|(
name|row
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
DECL|method|insertWatch (final AccountProjectWatchInfo k)
name|void
name|insertWatch
parameter_list|(
specifier|final
name|AccountProjectWatchInfo
name|k
parameter_list|)
block|{
specifier|final
name|String
name|newName
init|=
name|k
operator|.
name|getProject
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|int
name|row
init|=
literal|1
decl_stmt|;
for|for
control|(
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
specifier|final
name|AccountProjectWatchInfo
name|i
init|=
name|getRowItem
argument_list|(
name|row
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|!=
literal|null
operator|&&
name|i
operator|.
name|getProject
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|newName
argument_list|)
operator|>=
literal|0
condition|)
block|{
break|break;
block|}
block|}
name|table
operator|.
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
name|populate
argument_list|(
name|row
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
DECL|method|display (final List<AccountProjectWatchInfo> result)
name|void
name|display
parameter_list|(
specifier|final
name|List
argument_list|<
name|AccountProjectWatchInfo
argument_list|>
name|result
parameter_list|)
block|{
while|while
condition|(
literal|2
operator|<
name|table
operator|.
name|getRowCount
argument_list|()
condition|)
name|table
operator|.
name|removeRow
argument_list|(
name|table
operator|.
name|getRowCount
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|AccountProjectWatchInfo
name|k
range|:
name|result
control|)
block|{
specifier|final
name|int
name|row
init|=
name|table
operator|.
name|getRowCount
argument_list|()
decl_stmt|;
name|table
operator|.
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
name|populate
argument_list|(
name|row
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|populate (final int row, final AccountProjectWatchInfo k)
name|void
name|populate
parameter_list|(
specifier|final
name|int
name|row
parameter_list|,
specifier|final
name|AccountProjectWatchInfo
name|k
parameter_list|)
block|{
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
literal|1
argument_list|,
operator|new
name|CheckBox
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
literal|2
argument_list|,
operator|new
name|ProjectOpenLink
argument_list|(
name|k
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|{
specifier|final
name|CheckBox
name|notifyNewChanges
init|=
operator|new
name|CheckBox
argument_list|()
decl_stmt|;
name|notifyNewChanges
operator|.
name|addClickListener
argument_list|(
operator|new
name|ClickListener
argument_list|()
block|{
specifier|public
name|void
name|onClick
parameter_list|(
specifier|final
name|Widget
name|sender
parameter_list|)
block|{
specifier|final
name|boolean
name|oldVal
init|=
name|k
operator|.
name|getWatch
argument_list|()
operator|.
name|isNotifyNewChanges
argument_list|()
decl_stmt|;
name|k
operator|.
name|getWatch
argument_list|()
operator|.
name|setNotifyNewChanges
argument_list|(
name|notifyNewChanges
operator|.
name|isChecked
argument_list|()
argument_list|)
expr_stmt|;
name|Util
operator|.
name|ACCOUNT_SVC
operator|.
name|updateProjectWatch
argument_list|(
name|k
operator|.
name|getWatch
argument_list|()
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
block|{                   }
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
name|k
operator|.
name|getWatch
argument_list|()
operator|.
name|setNotifyNewChanges
argument_list|(
name|oldVal
argument_list|)
expr_stmt|;
name|notifyNewChanges
operator|.
name|setChecked
argument_list|(
name|oldVal
argument_list|)
expr_stmt|;
name|super
operator|.
name|onFailure
argument_list|(
name|caught
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|notifyNewChanges
operator|.
name|setChecked
argument_list|(
name|k
operator|.
name|getWatch
argument_list|()
operator|.
name|isNotifyNewChanges
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
literal|3
argument_list|,
name|notifyNewChanges
argument_list|)
expr_stmt|;
block|}
block|{
specifier|final
name|CheckBox
name|notifyAllComments
init|=
operator|new
name|CheckBox
argument_list|()
decl_stmt|;
name|notifyAllComments
operator|.
name|addClickListener
argument_list|(
operator|new
name|ClickListener
argument_list|()
block|{
specifier|public
name|void
name|onClick
parameter_list|(
specifier|final
name|Widget
name|sender
parameter_list|)
block|{
specifier|final
name|boolean
name|oldVal
init|=
name|k
operator|.
name|getWatch
argument_list|()
operator|.
name|isNotifyAllComments
argument_list|()
decl_stmt|;
name|k
operator|.
name|getWatch
argument_list|()
operator|.
name|setNotifyAllComments
argument_list|(
name|notifyAllComments
operator|.
name|isChecked
argument_list|()
argument_list|)
expr_stmt|;
name|Util
operator|.
name|ACCOUNT_SVC
operator|.
name|updateProjectWatch
argument_list|(
name|k
operator|.
name|getWatch
argument_list|()
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
block|{                   }
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
name|k
operator|.
name|getWatch
argument_list|()
operator|.
name|setNotifyAllComments
argument_list|(
name|oldVal
argument_list|)
expr_stmt|;
name|notifyAllComments
operator|.
name|setChecked
argument_list|(
name|oldVal
argument_list|)
expr_stmt|;
name|super
operator|.
name|onFailure
argument_list|(
name|caught
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|notifyAllComments
operator|.
name|setChecked
argument_list|(
name|k
operator|.
name|getWatch
argument_list|()
operator|.
name|isNotifyAllComments
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
literal|4
argument_list|,
name|notifyAllComments
argument_list|)
expr_stmt|;
block|}
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
name|row
argument_list|,
literal|1
argument_list|,
name|S_ICON_CELL
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
literal|2
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
literal|3
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
literal|4
argument_list|,
name|S_DATA_CELL
argument_list|)
expr_stmt|;
name|setRowItem
argument_list|(
name|row
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

