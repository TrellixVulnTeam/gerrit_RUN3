begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
DECL|package|com.google.gerrit.client.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|change
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
name|ChangeApi
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
name|TextBoxChangeListener
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
name|gwt
operator|.
name|core
operator|.
name|client
operator|.
name|JavaScriptObject
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
name|core
operator|.
name|client
operator|.
name|Scheduler
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
name|core
operator|.
name|client
operator|.
name|Scheduler
operator|.
name|ScheduledCommand
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
name|uibinder
operator|.
name|client
operator|.
name|UiBinder
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
name|uibinder
operator|.
name|client
operator|.
name|UiField
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
name|uibinder
operator|.
name|client
operator|.
name|UiHandler
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
name|HTMLPanel
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
name|PopupPanel
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
name|globalkey
operator|.
name|client
operator|.
name|NpTextArea
import|;
end_import

begin_class
DECL|class|EditMessageBox
class|class
name|EditMessageBox
extends|extends
name|Composite
block|{
DECL|interface|Binder
interface|interface
name|Binder
extends|extends
name|UiBinder
argument_list|<
name|HTMLPanel
argument_list|,
name|EditMessageBox
argument_list|>
block|{}
DECL|field|uiBinder
specifier|private
specifier|static
specifier|final
name|Binder
name|uiBinder
init|=
name|GWT
operator|.
name|create
argument_list|(
name|Binder
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|changeId
specifier|private
specifier|final
name|Change
operator|.
name|Id
name|changeId
decl_stmt|;
DECL|field|revision
specifier|private
specifier|final
name|String
name|revision
decl_stmt|;
DECL|field|originalMessage
specifier|private
name|String
name|originalMessage
decl_stmt|;
DECL|field|message
annotation|@
name|UiField
name|NpTextArea
name|message
decl_stmt|;
DECL|field|save
annotation|@
name|UiField
name|Button
name|save
decl_stmt|;
DECL|field|cancel
annotation|@
name|UiField
name|Button
name|cancel
decl_stmt|;
DECL|method|EditMessageBox ( Change.Id changeId, String revision, String msg)
name|EditMessageBox
parameter_list|(
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
name|String
name|revision
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|this
operator|.
name|changeId
operator|=
name|changeId
expr_stmt|;
name|this
operator|.
name|revision
operator|=
name|revision
expr_stmt|;
name|this
operator|.
name|originalMessage
operator|=
name|msg
operator|.
name|trim
argument_list|()
expr_stmt|;
name|initWidget
argument_list|(
name|uiBinder
operator|.
name|createAndBindUi
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|message
operator|.
name|getElement
argument_list|()
operator|.
name|setAttribute
argument_list|(
literal|"wrap"
argument_list|,
literal|"off"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|""
argument_list|)
expr_stmt|;
operator|new
name|TextBoxChangeListener
argument_list|(
name|message
argument_list|)
block|{
specifier|public
name|void
name|onTextChanged
parameter_list|(
name|String
name|newText
parameter_list|)
block|{
name|save
operator|.
name|setEnabled
argument_list|(
operator|!
name|newText
operator|.
name|trim
argument_list|()
operator|.
name|equals
argument_list|(
name|originalMessage
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onLoad ()
specifier|protected
name|void
name|onLoad
parameter_list|()
block|{
if|if
condition|(
name|message
operator|.
name|getText
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|message
operator|.
name|setText
argument_list|(
name|originalMessage
argument_list|)
expr_stmt|;
name|save
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|Scheduler
operator|.
name|get
argument_list|()
operator|.
name|scheduleDeferred
argument_list|(
operator|new
name|ScheduledCommand
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|()
block|{
name|message
operator|.
name|setFocus
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"save"
argument_list|)
DECL|method|onSave (ClickEvent e)
name|void
name|onSave
parameter_list|(
name|ClickEvent
name|e
parameter_list|)
block|{
name|save
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ChangeApi
operator|.
name|message
argument_list|(
name|changeId
operator|.
name|get
argument_list|()
argument_list|,
name|revision
argument_list|,
name|message
operator|.
name|getText
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|JavaScriptObject
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|JavaScriptObject
name|msg
parameter_list|)
block|{
name|Gerrit
operator|.
name|display
argument_list|(
name|PageLinks
operator|.
name|toChange
argument_list|(
name|changeId
argument_list|)
argument_list|)
expr_stmt|;
name|hide
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"cancel"
argument_list|)
DECL|method|onCancel (ClickEvent e)
name|void
name|onCancel
parameter_list|(
name|ClickEvent
name|e
parameter_list|)
block|{
name|message
operator|.
name|setText
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|hide
argument_list|()
expr_stmt|;
block|}
DECL|method|hide ()
specifier|private
name|void
name|hide
parameter_list|()
block|{
for|for
control|(
name|Widget
name|w
init|=
name|getParent
argument_list|()
init|;
name|w
operator|!=
literal|null
condition|;
name|w
operator|=
name|w
operator|.
name|getParent
argument_list|()
control|)
block|{
if|if
condition|(
name|w
operator|instanceof
name|PopupPanel
condition|)
block|{
operator|(
operator|(
name|PopupPanel
operator|)
name|w
operator|)
operator|.
name|hide
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
end_class

end_unit

