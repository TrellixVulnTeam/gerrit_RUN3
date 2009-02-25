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
name|SignInDialog
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
name|AccountExternalId
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
name|Common
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
DECL|class|ExternalIdPanel
class|class
name|ExternalIdPanel
extends|extends
name|Composite
block|{
DECL|field|identites
specifier|private
name|IdTable
name|identites
decl_stmt|;
DECL|field|deleteIdentity
specifier|private
name|Button
name|deleteIdentity
decl_stmt|;
DECL|method|ExternalIdPanel ()
name|ExternalIdPanel
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
name|identites
operator|=
operator|new
name|IdTable
argument_list|()
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|identites
argument_list|)
expr_stmt|;
name|deleteIdentity
operator|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonDeleteIdentity
argument_list|()
argument_list|)
expr_stmt|;
name|deleteIdentity
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
name|identites
operator|.
name|deleteChecked
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|deleteIdentity
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|Common
operator|.
name|getGerritConfig
argument_list|()
operator|.
name|getLoginType
argument_list|()
condition|)
block|{
case|case
name|OPENID
case|:
block|{
specifier|final
name|Button
name|linkIdentity
init|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonLinkIdentity
argument_list|()
argument_list|)
decl_stmt|;
name|linkIdentity
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
name|doLinkIdentity
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|linkIdentity
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|initWidget
argument_list|(
name|body
argument_list|)
expr_stmt|;
block|}
DECL|method|doLinkIdentity ()
name|void
name|doLinkIdentity
parameter_list|()
block|{
specifier|final
name|SignInDialog
name|d
init|=
operator|new
name|SignInDialog
argument_list|(
name|SignInDialog
operator|.
name|Mode
operator|.
name|LINK_IDENTIY
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
specifier|final
name|Object
name|result
parameter_list|)
block|{
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|d
operator|.
name|center
argument_list|()
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
name|refresh
argument_list|()
expr_stmt|;
block|}
DECL|method|refresh ()
specifier|private
name|void
name|refresh
parameter_list|()
block|{
name|Util
operator|.
name|ACCOUNT_SEC
operator|.
name|myExternalIds
argument_list|(
operator|new
name|GerritCallback
argument_list|<
name|List
argument_list|<
name|AccountExternalId
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
name|AccountExternalId
argument_list|>
name|result
parameter_list|)
block|{
name|identites
operator|.
name|display
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|identites
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
DECL|class|IdTable
specifier|private
class|class
name|IdTable
extends|extends
name|FancyFlexTable
argument_list|<
name|AccountExternalId
argument_list|>
block|{
DECL|method|IdTable ()
name|IdTable
parameter_list|()
block|{
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|,
name|Util
operator|.
name|C
operator|.
name|webIdLastUsed
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
name|webIdEmail
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
name|webIdIdentity
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
block|}
annotation|@
name|Override
DECL|method|getRowItemKey (final AccountExternalId item)
specifier|protected
name|Object
name|getRowItemKey
parameter_list|(
specifier|final
name|AccountExternalId
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
DECL|method|onOpenItem (final AccountExternalId item)
specifier|protected
name|void
name|onOpenItem
parameter_list|(
specifier|final
name|AccountExternalId
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
if|if
condition|(
name|cb
operator|!=
literal|null
condition|)
block|{
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
block|}
DECL|method|deleteChecked ()
name|void
name|deleteChecked
parameter_list|()
block|{
specifier|final
name|HashSet
argument_list|<
name|AccountExternalId
operator|.
name|Key
argument_list|>
name|keys
init|=
operator|new
name|HashSet
argument_list|<
name|AccountExternalId
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
name|AccountExternalId
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
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
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
name|row
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|cb
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|cb
operator|.
name|isChecked
argument_list|()
condition|)
block|{
name|keys
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
name|keys
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|deleteIdentity
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
name|deleteExternalIds
argument_list|(
name|keys
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|Set
argument_list|<
name|AccountExternalId
operator|.
name|Key
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
name|AccountExternalId
operator|.
name|Key
argument_list|>
name|removed
parameter_list|)
block|{
name|deleteIdentity
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
name|AccountExternalId
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
name|removed
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
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|caught
parameter_list|)
block|{
name|deleteIdentity
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
DECL|method|display (final List<AccountExternalId> result)
name|void
name|display
parameter_list|(
specifier|final
name|List
argument_list|<
name|AccountExternalId
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
name|AccountExternalId
name|k
range|:
name|result
control|)
block|{
name|addOneId
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
specifier|final
name|AccountExternalId
name|mostRecent
init|=
name|AccountExternalId
operator|.
name|mostRecent
argument_list|(
name|result
argument_list|)
decl_stmt|;
if|if
condition|(
name|mostRecent
operator|!=
literal|null
condition|)
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
if|if
condition|(
name|getRowItem
argument_list|(
name|row
argument_list|)
operator|==
name|mostRecent
condition|)
block|{
comment|// Remove the box from the most recent row, this prevents
comment|// the user from trying to delete the identity they last used
comment|// to login, possibly locking themselves out of the account.
comment|//
name|table
operator|.
name|setText
argument_list|(
name|row
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
DECL|method|addOneId (final AccountExternalId k)
name|void
name|addOneId
parameter_list|(
specifier|final
name|AccountExternalId
name|k
parameter_list|)
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
if|if
condition|(
name|k
operator|.
name|canUserDelete
argument_list|()
condition|)
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
block|}
else|else
block|{
name|table
operator|.
name|setText
argument_list|(
name|row
argument_list|,
literal|1
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
name|table
operator|.
name|setText
argument_list|(
name|row
argument_list|,
literal|2
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
literal|3
argument_list|,
name|k
operator|.
name|getEmailAddress
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
name|k
operator|.
name|getExternalId
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
literal|"C_LAST_UPDATE"
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

