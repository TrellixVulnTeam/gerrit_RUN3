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
name|FormatUtil
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
name|AccountSshKey
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
name|SmallHeading
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
name|HTML
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
name|TextArea
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
name|VerticalPanel
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
DECL|class|SshKeyPanel
class|class
name|SshKeyPanel
extends|extends
name|Composite
block|{
DECL|field|keys
specifier|private
name|SshKeyTable
name|keys
decl_stmt|;
DECL|field|addNew
specifier|private
name|Button
name|addNew
decl_stmt|;
DECL|field|addTxt
specifier|private
name|TextArea
name|addTxt
decl_stmt|;
DECL|field|delSel
specifier|private
name|Button
name|delSel
decl_stmt|;
DECL|method|SshKeyPanel ()
name|SshKeyPanel
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
name|keys
operator|=
operator|new
name|SshKeyTable
argument_list|()
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|keys
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
name|keys
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
block|{
specifier|final
name|VerticalPanel
name|fp
init|=
operator|new
name|VerticalPanel
argument_list|()
decl_stmt|;
name|fp
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-AddSshKeyPanel"
argument_list|)
expr_stmt|;
name|fp
operator|.
name|add
argument_list|(
operator|new
name|SmallHeading
argument_list|(
name|Util
operator|.
name|C
operator|.
name|addSshKeyPanelHeader
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fp
operator|.
name|add
argument_list|(
operator|new
name|HTML
argument_list|(
name|Util
operator|.
name|C
operator|.
name|addSshKeyHelp
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|addTxt
operator|=
operator|new
name|TextArea
argument_list|()
expr_stmt|;
name|addTxt
operator|.
name|setVisibleLines
argument_list|(
literal|12
argument_list|)
expr_stmt|;
name|addTxt
operator|.
name|setCharacterWidth
argument_list|(
literal|80
argument_list|)
expr_stmt|;
name|fp
operator|.
name|add
argument_list|(
name|addTxt
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
name|buttonAddSshKey
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
name|txt
init|=
name|addTxt
operator|.
name|getText
argument_list|()
decl_stmt|;
if|if
condition|(
name|txt
operator|!=
literal|null
operator|&&
name|txt
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|addNew
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Util
operator|.
name|ACCOUNT_SEC
operator|.
name|addSshKey
argument_list|(
name|txt
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|AccountSshKey
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
specifier|final
name|AccountSshKey
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
name|addTxt
operator|.
name|setText
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|keys
operator|.
name|addOneKey
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
name|ACCOUNT_SEC
operator|.
name|mySshKeys
argument_list|(
operator|new
name|GerritCallback
argument_list|<
name|List
argument_list|<
name|AccountSshKey
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
name|AccountSshKey
argument_list|>
name|result
parameter_list|)
block|{
name|keys
operator|.
name|display
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|keys
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
DECL|class|SshKeyTable
specifier|private
class|class
name|SshKeyTable
extends|extends
name|FancyFlexTable
argument_list|<
name|AccountSshKey
argument_list|>
block|{
DECL|field|S_INVALID
specifier|private
specifier|static
specifier|final
name|String
name|S_INVALID
init|=
literal|"gerrit-SshKeyPanel-Invalid"
decl_stmt|;
DECL|method|SshKeyTable ()
name|SshKeyTable
parameter_list|()
block|{
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
name|sshKeyAlgorithm
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|,
name|Util
operator|.
name|C
operator|.
name|sshKeyKey
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|,
name|Util
operator|.
name|C
operator|.
name|sshKeyComment
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
literal|6
argument_list|,
name|Util
operator|.
name|C
operator|.
name|sshKeyLastUsed
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
literal|7
argument_list|,
name|Util
operator|.
name|C
operator|.
name|sshKeyStored
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
if|if
condition|(
name|cell
operator|!=
literal|1
operator|&&
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
name|addStyleName
argument_list|(
literal|0
argument_list|,
literal|4
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
literal|5
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
literal|6
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
literal|7
argument_list|,
name|S_DATA_HEADER
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getRowItemKey (final AccountSshKey item)
specifier|protected
name|Object
name|getRowItemKey
parameter_list|(
specifier|final
name|AccountSshKey
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
DECL|method|onOpenItem (final AccountSshKey item)
specifier|protected
name|void
name|onOpenItem
parameter_list|(
specifier|final
name|AccountSshKey
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
name|AccountSshKey
operator|.
name|Id
argument_list|>
name|ids
init|=
operator|new
name|HashSet
argument_list|<
name|AccountSshKey
operator|.
name|Id
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
name|AccountSshKey
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
name|ACCOUNT_SEC
operator|.
name|deleteSshKeys
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
name|AccountSshKey
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
DECL|method|display (final List<AccountSshKey> result)
name|void
name|display
parameter_list|(
specifier|final
name|List
argument_list|<
name|AccountSshKey
argument_list|>
name|result
parameter_list|)
block|{
while|while
condition|(
literal|1
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
name|AccountSshKey
name|k
range|:
name|result
control|)
block|{
name|addOneKey
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addOneKey (final AccountSshKey k)
name|void
name|addOneKey
parameter_list|(
specifier|final
name|AccountSshKey
name|k
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
if|if
condition|(
name|k
operator|.
name|isValid
argument_list|()
condition|)
block|{
name|table
operator|.
name|setText
argument_list|(
name|row
argument_list|,
literal|2
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|removeStyleName
argument_list|(
name|row
argument_list|,
literal|2
argument_list|,
name|S_INVALID
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
literal|2
argument_list|,
name|Util
operator|.
name|C
operator|.
name|sshKeyInvalid
argument_list|()
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
name|S_INVALID
argument_list|)
expr_stmt|;
block|}
name|table
operator|.
name|setText
argument_list|(
name|row
argument_list|,
literal|3
argument_list|,
name|k
operator|.
name|getAlgorithm
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
name|row
argument_list|,
literal|4
argument_list|,
name|elide
argument_list|(
name|k
operator|.
name|getEncodedKey
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
literal|5
argument_list|,
name|k
operator|.
name|getComment
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
name|row
argument_list|,
literal|6
argument_list|,
name|FormatUtil
operator|.
name|mediumFormat
argument_list|(
name|k
operator|.
name|getLastUsedOn
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
literal|7
argument_list|,
name|FormatUtil
operator|.
name|mediumFormat
argument_list|(
name|k
operator|.
name|getStoredOn
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
name|S_ICON_CELL
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
literal|"gerrit-SshKeyPanel-EncodedKey"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|c
init|=
literal|3
init|;
name|c
operator|<=
literal|7
condition|;
name|c
operator|++
control|)
block|{
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
name|c
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
literal|6
argument_list|,
literal|"C_LAST_UPDATE"
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
literal|7
argument_list|,
literal|"C_LAST_UPDATE"
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
DECL|method|elide (final String s)
name|String
name|elide
parameter_list|(
specifier|final
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|==
literal|null
operator|||
name|s
operator|.
name|length
argument_list|()
operator|<
literal|40
condition|)
block|{
return|return
name|s
return|;
block|}
return|return
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|30
argument_list|)
operator|+
literal|"..."
operator|+
name|s
operator|.
name|substring
argument_list|(
name|s
operator|.
name|length
argument_list|()
operator|-
literal|10
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

