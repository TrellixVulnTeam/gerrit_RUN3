begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
DECL|package|com.google.gerrit.client.admin
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|admin
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
name|common
operator|.
name|data
operator|.
name|GlobalCapability
operator|.
name|CREATE_GROUP
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
name|Dispatcher
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
name|NotFoundScreen
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
name|account
operator|.
name|AccountCapabilities
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
name|groups
operator|.
name|GroupApi
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
name|info
operator|.
name|GroupInfo
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
name|OnEditEnabler
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
name|Screen
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
name|KeyCodes
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
name|event
operator|.
name|dom
operator|.
name|client
operator|.
name|KeyPressHandler
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
name|VerticalPanel
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
name|NpTextBox
import|;
end_import

begin_class
DECL|class|CreateGroupScreen
specifier|public
class|class
name|CreateGroupScreen
extends|extends
name|Screen
block|{
DECL|field|addTxt
specifier|private
name|NpTextBox
name|addTxt
decl_stmt|;
DECL|field|addNew
specifier|private
name|Button
name|addNew
decl_stmt|;
DECL|method|CreateGroupScreen ()
specifier|public
name|CreateGroupScreen
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|setRequiresSignIn
argument_list|(
literal|true
argument_list|)
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
name|super
operator|.
name|onLoad
argument_list|()
expr_stmt|;
name|AccountCapabilities
operator|.
name|all
argument_list|(
operator|new
name|GerritCallback
argument_list|<
name|AccountCapabilities
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|AccountCapabilities
name|ac
parameter_list|)
block|{
if|if
condition|(
name|ac
operator|.
name|canPerform
argument_list|(
name|CREATE_GROUP
argument_list|)
condition|)
block|{
name|display
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Gerrit
operator|.
name|display
argument_list|(
name|PageLinks
operator|.
name|ADMIN_CREATE_GROUP
argument_list|,
operator|new
name|NotFoundScreen
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|,
name|CREATE_GROUP
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onInitUI ()
specifier|protected
name|void
name|onInitUI
parameter_list|()
block|{
name|super
operator|.
name|onInitUI
argument_list|()
expr_stmt|;
name|setPageTitle
argument_list|(
name|AdminConstants
operator|.
name|I
operator|.
name|createGroupTitle
argument_list|()
argument_list|)
expr_stmt|;
name|addCreateGroupPanel
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onShowView ()
specifier|public
name|void
name|onShowView
parameter_list|()
block|{
name|super
operator|.
name|onShowView
argument_list|()
expr_stmt|;
if|if
condition|(
name|addTxt
operator|!=
literal|null
condition|)
block|{
name|addTxt
operator|.
name|setFocus
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addCreateGroupPanel ()
specifier|private
name|void
name|addCreateGroupPanel
parameter_list|()
block|{
name|VerticalPanel
name|addPanel
init|=
operator|new
name|VerticalPanel
argument_list|()
decl_stmt|;
name|addPanel
operator|.
name|setStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|addSshKeyPanel
argument_list|()
argument_list|)
expr_stmt|;
name|addPanel
operator|.
name|add
argument_list|(
operator|new
name|SmallHeading
argument_list|(
name|AdminConstants
operator|.
name|I
operator|.
name|headingCreateGroup
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|addTxt
operator|=
operator|new
name|NpTextBox
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onBrowserEvent
parameter_list|(
name|Event
name|event
parameter_list|)
block|{
name|super
operator|.
name|onBrowserEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
if|if
condition|(
name|event
operator|.
name|getTypeInt
argument_list|()
operator|==
name|Event
operator|.
name|ONPASTE
condition|)
block|{
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
if|if
condition|(
name|addTxt
operator|.
name|getValue
argument_list|()
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|addNew
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
name|addTxt
operator|.
name|sinkEvents
argument_list|(
name|Event
operator|.
name|ONPASTE
argument_list|)
expr_stmt|;
name|addTxt
operator|.
name|setVisibleLength
argument_list|(
literal|60
argument_list|)
expr_stmt|;
name|addTxt
operator|.
name|addKeyPressHandler
argument_list|(
operator|new
name|KeyPressHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onKeyPress
parameter_list|(
name|KeyPressEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|event
operator|.
name|getNativeEvent
argument_list|()
operator|.
name|getKeyCode
argument_list|()
operator|==
name|KeyCodes
operator|.
name|KEY_ENTER
condition|)
block|{
name|doCreateGroup
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|addPanel
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
name|AdminConstants
operator|.
name|I
operator|.
name|buttonCreateGroup
argument_list|()
argument_list|)
expr_stmt|;
name|addNew
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|addNew
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
name|ClickEvent
name|event
parameter_list|)
block|{
name|doCreateGroup
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|addPanel
operator|.
name|add
argument_list|(
name|addNew
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|addPanel
argument_list|)
expr_stmt|;
operator|new
name|OnEditEnabler
argument_list|(
name|addNew
argument_list|,
name|addTxt
argument_list|)
expr_stmt|;
block|}
DECL|method|doCreateGroup ()
specifier|private
name|void
name|doCreateGroup
parameter_list|()
block|{
specifier|final
name|String
name|newName
init|=
name|addTxt
operator|.
name|getText
argument_list|()
decl_stmt|;
if|if
condition|(
name|newName
operator|==
literal|null
operator|||
name|newName
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|addNew
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|GroupApi
operator|.
name|createGroup
argument_list|(
name|newName
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|GroupInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|GroupInfo
name|result
parameter_list|)
block|{
name|History
operator|.
name|newItem
argument_list|(
name|Dispatcher
operator|.
name|toGroup
argument_list|(
name|result
operator|.
name|getGroupId
argument_list|()
argument_list|,
name|AccountGroupScreen
operator|.
name|MEMBERS
argument_list|)
argument_list|)
expr_stmt|;
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
name|super
operator|.
name|onFailure
argument_list|(
name|caught
argument_list|)
expr_stmt|;
name|addNew
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

