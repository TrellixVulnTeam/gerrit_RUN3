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
name|AccountGroupSuggestOracle
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
name|RPCSuggestOracle
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
name|data
operator|.
name|GroupDetail
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
name|data
operator|.
name|GroupOptions
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
name|AccountGroup
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
name|ChangeEvent
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
name|ChangeHandler
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
name|Label
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
name|ListBox
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
name|clippy
operator|.
name|client
operator|.
name|CopyableLabel
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtjsonrpc
operator|.
name|common
operator|.
name|VoidResult
import|;
end_import

begin_class
DECL|class|AccountGroupInfoScreen
specifier|public
class|class
name|AccountGroupInfoScreen
extends|extends
name|AccountGroupScreen
block|{
DECL|field|groupUUIDLabel
specifier|private
name|CopyableLabel
name|groupUUIDLabel
decl_stmt|;
DECL|field|groupNameTxt
specifier|private
name|NpTextBox
name|groupNameTxt
decl_stmt|;
DECL|field|saveName
specifier|private
name|Button
name|saveName
decl_stmt|;
DECL|field|ownerTxtBox
specifier|private
name|NpTextBox
name|ownerTxtBox
decl_stmt|;
DECL|field|ownerTxt
specifier|private
name|SuggestBox
name|ownerTxt
decl_stmt|;
DECL|field|saveOwner
specifier|private
name|Button
name|saveOwner
decl_stmt|;
DECL|field|descTxt
specifier|private
name|NpTextArea
name|descTxt
decl_stmt|;
DECL|field|saveDesc
specifier|private
name|Button
name|saveDesc
decl_stmt|;
DECL|field|typeSystem
specifier|private
name|Label
name|typeSystem
decl_stmt|;
DECL|field|typeSelect
specifier|private
name|ListBox
name|typeSelect
decl_stmt|;
DECL|field|saveType
specifier|private
name|Button
name|saveType
decl_stmt|;
DECL|field|visibleToAllCheckBox
specifier|private
name|CheckBox
name|visibleToAllCheckBox
decl_stmt|;
DECL|field|saveGroupOptions
specifier|private
name|Button
name|saveGroupOptions
decl_stmt|;
DECL|method|AccountGroupInfoScreen (final GroupDetail toShow, final String token)
specifier|public
name|AccountGroupInfoScreen
parameter_list|(
specifier|final
name|GroupDetail
name|toShow
parameter_list|,
specifier|final
name|String
name|token
parameter_list|)
block|{
name|super
argument_list|(
name|toShow
argument_list|,
name|token
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
name|initUUID
argument_list|()
expr_stmt|;
name|initName
argument_list|()
expr_stmt|;
name|initOwner
argument_list|()
expr_stmt|;
name|initDescription
argument_list|()
expr_stmt|;
name|initGroupOptions
argument_list|()
expr_stmt|;
name|initGroupType
argument_list|()
expr_stmt|;
block|}
DECL|method|enableForm (final boolean canModify)
specifier|private
name|void
name|enableForm
parameter_list|(
specifier|final
name|boolean
name|canModify
parameter_list|)
block|{
name|groupNameTxt
operator|.
name|setEnabled
argument_list|(
name|canModify
argument_list|)
expr_stmt|;
name|ownerTxtBox
operator|.
name|setEnabled
argument_list|(
name|canModify
argument_list|)
expr_stmt|;
name|descTxt
operator|.
name|setEnabled
argument_list|(
name|canModify
argument_list|)
expr_stmt|;
name|typeSelect
operator|.
name|setEnabled
argument_list|(
name|canModify
argument_list|)
expr_stmt|;
name|visibleToAllCheckBox
operator|.
name|setEnabled
argument_list|(
name|canModify
argument_list|)
expr_stmt|;
block|}
DECL|method|initUUID ()
specifier|private
name|void
name|initUUID
parameter_list|()
block|{
specifier|final
name|VerticalPanel
name|groupUUIDPanel
init|=
operator|new
name|VerticalPanel
argument_list|()
decl_stmt|;
name|groupUUIDPanel
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
name|groupUUIDPanel
argument_list|()
argument_list|)
expr_stmt|;
name|groupUUIDPanel
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
name|headingGroupUUID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|groupUUIDLabel
operator|=
operator|new
name|CopyableLabel
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|groupUUIDPanel
operator|.
name|add
argument_list|(
name|groupUUIDLabel
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|groupUUIDPanel
argument_list|)
expr_stmt|;
block|}
DECL|method|initName ()
specifier|private
name|void
name|initName
parameter_list|()
block|{
specifier|final
name|VerticalPanel
name|groupNamePanel
init|=
operator|new
name|VerticalPanel
argument_list|()
decl_stmt|;
name|groupNamePanel
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
name|groupNamePanel
argument_list|()
argument_list|)
expr_stmt|;
name|groupNameTxt
operator|=
operator|new
name|NpTextBox
argument_list|()
expr_stmt|;
name|groupNameTxt
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
name|groupNameTextBox
argument_list|()
argument_list|)
expr_stmt|;
name|groupNameTxt
operator|.
name|setVisibleLength
argument_list|(
literal|60
argument_list|)
expr_stmt|;
name|groupNamePanel
operator|.
name|add
argument_list|(
name|groupNameTxt
argument_list|)
expr_stmt|;
name|saveName
operator|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonRenameGroup
argument_list|()
argument_list|)
expr_stmt|;
name|saveName
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|saveName
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
name|String
name|newName
init|=
name|groupNameTxt
operator|.
name|getText
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
name|Util
operator|.
name|GROUP_SVC
operator|.
name|renameGroup
argument_list|(
name|getGroupId
argument_list|()
argument_list|,
name|newName
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|GroupDetail
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
specifier|final
name|GroupDetail
name|groupDetail
parameter_list|)
block|{
name|saveName
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|setPageTitle
argument_list|(
name|Util
operator|.
name|M
operator|.
name|group
argument_list|(
name|groupDetail
operator|.
name|group
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|display
argument_list|(
name|groupDetail
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
name|groupNamePanel
operator|.
name|add
argument_list|(
name|saveName
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|groupNamePanel
argument_list|)
expr_stmt|;
operator|new
name|OnEditEnabler
argument_list|(
name|saveName
argument_list|,
name|groupNameTxt
argument_list|)
expr_stmt|;
block|}
DECL|method|initOwner ()
specifier|private
name|void
name|initOwner
parameter_list|()
block|{
specifier|final
name|VerticalPanel
name|ownerPanel
init|=
operator|new
name|VerticalPanel
argument_list|()
decl_stmt|;
name|ownerPanel
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
name|groupOwnerPanel
argument_list|()
argument_list|)
expr_stmt|;
name|ownerPanel
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
name|headingOwner
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ownerTxtBox
operator|=
operator|new
name|NpTextBox
argument_list|()
expr_stmt|;
name|ownerTxtBox
operator|.
name|setVisibleLength
argument_list|(
literal|60
argument_list|)
expr_stmt|;
name|ownerTxt
operator|=
operator|new
name|SuggestBox
argument_list|(
operator|new
name|RPCSuggestOracle
argument_list|(
operator|new
name|AccountGroupSuggestOracle
argument_list|()
argument_list|)
argument_list|,
name|ownerTxtBox
argument_list|)
expr_stmt|;
name|ownerTxt
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
name|groupOwnerTextBox
argument_list|()
argument_list|)
expr_stmt|;
name|ownerPanel
operator|.
name|add
argument_list|(
name|ownerTxt
argument_list|)
expr_stmt|;
name|saveOwner
operator|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonChangeGroupOwner
argument_list|()
argument_list|)
expr_stmt|;
name|saveOwner
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|saveOwner
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
name|String
name|newOwner
init|=
name|ownerTxt
operator|.
name|getText
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|newOwner
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Util
operator|.
name|GROUP_SVC
operator|.
name|changeGroupOwner
argument_list|(
name|getGroupId
argument_list|()
argument_list|,
name|newOwner
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
name|saveOwner
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|ownerPanel
operator|.
name|add
argument_list|(
name|saveOwner
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|ownerPanel
argument_list|)
expr_stmt|;
operator|new
name|OnEditEnabler
argument_list|(
name|saveOwner
argument_list|,
name|ownerTxtBox
argument_list|)
expr_stmt|;
block|}
DECL|method|initDescription ()
specifier|private
name|void
name|initDescription
parameter_list|()
block|{
specifier|final
name|VerticalPanel
name|vp
init|=
operator|new
name|VerticalPanel
argument_list|()
decl_stmt|;
name|vp
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
name|groupDescriptionPanel
argument_list|()
argument_list|)
expr_stmt|;
name|vp
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
name|headingDescription
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|descTxt
operator|=
operator|new
name|NpTextArea
argument_list|()
expr_stmt|;
name|descTxt
operator|.
name|setVisibleLines
argument_list|(
literal|6
argument_list|)
expr_stmt|;
name|descTxt
operator|.
name|setCharacterWidth
argument_list|(
literal|60
argument_list|)
expr_stmt|;
name|vp
operator|.
name|add
argument_list|(
name|descTxt
argument_list|)
expr_stmt|;
name|saveDesc
operator|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonSaveDescription
argument_list|()
argument_list|)
expr_stmt|;
name|saveDesc
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|saveDesc
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
name|String
name|txt
init|=
name|descTxt
operator|.
name|getText
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
name|GroupApi
operator|.
name|setGroupDescription
argument_list|(
name|getGroupUUID
argument_list|()
argument_list|,
name|txt
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|VoidResult
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
specifier|final
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|VoidResult
name|result
parameter_list|)
block|{
name|saveDesc
operator|.
name|setEnabled
argument_list|(
literal|false
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
name|vp
operator|.
name|add
argument_list|(
name|saveDesc
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|vp
argument_list|)
expr_stmt|;
operator|new
name|OnEditEnabler
argument_list|(
name|saveDesc
argument_list|,
name|descTxt
argument_list|)
expr_stmt|;
block|}
DECL|method|initGroupOptions ()
specifier|private
name|void
name|initGroupOptions
parameter_list|()
block|{
specifier|final
name|VerticalPanel
name|groupOptionsPanel
init|=
operator|new
name|VerticalPanel
argument_list|()
decl_stmt|;
specifier|final
name|VerticalPanel
name|vp
init|=
operator|new
name|VerticalPanel
argument_list|()
decl_stmt|;
name|vp
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
name|groupOptionsPanel
argument_list|()
argument_list|)
expr_stmt|;
name|vp
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
name|headingGroupOptions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|visibleToAllCheckBox
operator|=
operator|new
name|CheckBox
argument_list|(
name|Util
operator|.
name|C
operator|.
name|isVisibleToAll
argument_list|()
argument_list|)
expr_stmt|;
name|vp
operator|.
name|add
argument_list|(
name|visibleToAllCheckBox
argument_list|)
expr_stmt|;
name|groupOptionsPanel
operator|.
name|add
argument_list|(
name|vp
argument_list|)
expr_stmt|;
name|saveGroupOptions
operator|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonSaveGroupOptions
argument_list|()
argument_list|)
expr_stmt|;
name|saveGroupOptions
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|saveGroupOptions
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
name|GroupOptions
name|groupOptions
init|=
operator|new
name|GroupOptions
argument_list|(
name|visibleToAllCheckBox
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|Util
operator|.
name|GROUP_SVC
operator|.
name|changeGroupOptions
argument_list|(
name|getGroupId
argument_list|()
argument_list|,
name|groupOptions
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
name|saveGroupOptions
operator|.
name|setEnabled
argument_list|(
literal|false
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
name|groupOptionsPanel
operator|.
name|add
argument_list|(
name|saveGroupOptions
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|groupOptionsPanel
argument_list|)
expr_stmt|;
specifier|final
name|OnEditEnabler
name|enabler
init|=
operator|new
name|OnEditEnabler
argument_list|(
name|saveGroupOptions
argument_list|)
decl_stmt|;
name|enabler
operator|.
name|listenTo
argument_list|(
name|visibleToAllCheckBox
argument_list|)
expr_stmt|;
block|}
DECL|method|initGroupType ()
specifier|private
name|void
name|initGroupType
parameter_list|()
block|{
name|typeSystem
operator|=
operator|new
name|Label
argument_list|(
name|Util
operator|.
name|C
operator|.
name|groupType_SYSTEM
argument_list|()
argument_list|)
expr_stmt|;
name|typeSelect
operator|=
operator|new
name|ListBox
argument_list|()
expr_stmt|;
name|typeSelect
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
name|groupTypeSelectListBox
argument_list|()
argument_list|)
expr_stmt|;
name|typeSelect
operator|.
name|addItem
argument_list|(
name|Util
operator|.
name|C
operator|.
name|groupType_INTERNAL
argument_list|()
argument_list|,
name|AccountGroup
operator|.
name|Type
operator|.
name|INTERNAL
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|typeSelect
operator|.
name|addChangeHandler
argument_list|(
operator|new
name|ChangeHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onChange
parameter_list|(
name|ChangeEvent
name|event
parameter_list|)
block|{
name|saveType
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
name|saveType
operator|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonChangeGroupType
argument_list|()
argument_list|)
expr_stmt|;
name|saveType
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|saveType
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
name|onSaveType
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getAuthType
argument_list|()
condition|)
block|{
case|case
name|HTTP_LDAP
case|:
case|case
name|LDAP
case|:
case|case
name|LDAP_BIND
case|:
case|case
name|CLIENT_SSL_CERT_LDAP
case|:
break|break;
default|default:
return|return;
block|}
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
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|groupTypePanel
argument_list|()
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
name|headingGroupType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fp
operator|.
name|add
argument_list|(
name|typeSystem
argument_list|)
expr_stmt|;
name|fp
operator|.
name|add
argument_list|(
name|typeSelect
argument_list|)
expr_stmt|;
name|fp
operator|.
name|add
argument_list|(
name|saveType
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|fp
argument_list|)
expr_stmt|;
block|}
DECL|method|setType (final AccountGroup.Type newType)
specifier|private
name|void
name|setType
parameter_list|(
specifier|final
name|AccountGroup
operator|.
name|Type
name|newType
parameter_list|)
block|{
specifier|final
name|boolean
name|system
init|=
name|newType
operator|==
name|AccountGroup
operator|.
name|Type
operator|.
name|SYSTEM
decl_stmt|;
name|typeSystem
operator|.
name|setVisible
argument_list|(
name|system
argument_list|)
expr_stmt|;
name|typeSelect
operator|.
name|setVisible
argument_list|(
operator|!
name|system
argument_list|)
expr_stmt|;
name|saveType
operator|.
name|setVisible
argument_list|(
operator|!
name|system
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|system
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
name|typeSelect
operator|.
name|getItemCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|newType
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|typeSelect
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|typeSelect
operator|.
name|setSelectedIndex
argument_list|(
name|i
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
name|saveType
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|setMembersTabVisible
argument_list|(
name|newType
operator|==
name|AccountGroup
operator|.
name|Type
operator|.
name|INTERNAL
argument_list|)
expr_stmt|;
block|}
DECL|method|onSaveType ()
specifier|private
name|void
name|onSaveType
parameter_list|()
block|{
specifier|final
name|int
name|idx
init|=
name|typeSelect
operator|.
name|getSelectedIndex
argument_list|()
decl_stmt|;
specifier|final
name|AccountGroup
operator|.
name|Type
name|newType
init|=
name|AccountGroup
operator|.
name|Type
operator|.
name|valueOf
argument_list|(
name|typeSelect
operator|.
name|getValue
argument_list|(
name|idx
argument_list|)
argument_list|)
decl_stmt|;
name|typeSelect
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|saveType
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Util
operator|.
name|GROUP_SVC
operator|.
name|changeGroupType
argument_list|(
name|getGroupId
argument_list|()
argument_list|,
name|newType
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|VoidResult
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|VoidResult
name|result
parameter_list|)
block|{
name|typeSelect
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setType
argument_list|(
name|newType
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
name|typeSelect
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|saveType
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
DECL|method|display (final GroupDetail groupDetail)
specifier|protected
name|void
name|display
parameter_list|(
specifier|final
name|GroupDetail
name|groupDetail
parameter_list|)
block|{
specifier|final
name|AccountGroup
name|group
init|=
name|groupDetail
operator|.
name|group
decl_stmt|;
name|groupUUIDLabel
operator|.
name|setText
argument_list|(
name|group
operator|.
name|getGroupUUID
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|groupNameTxt
operator|.
name|setText
argument_list|(
name|group
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|groupDetail
operator|.
name|ownerGroup
operator|!=
literal|null
condition|)
block|{
name|ownerTxt
operator|.
name|setText
argument_list|(
name|groupDetail
operator|.
name|ownerGroup
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ownerTxt
operator|.
name|setText
argument_list|(
name|Util
operator|.
name|M
operator|.
name|deletedReference
argument_list|(
name|group
operator|.
name|getOwnerGroupUUID
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|descTxt
operator|.
name|setText
argument_list|(
name|group
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|visibleToAllCheckBox
operator|.
name|setValue
argument_list|(
name|group
operator|.
name|isVisibleToAll
argument_list|()
argument_list|)
expr_stmt|;
name|setType
argument_list|(
name|group
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|enableForm
argument_list|(
name|groupDetail
operator|.
name|canModify
argument_list|)
expr_stmt|;
name|saveName
operator|.
name|setVisible
argument_list|(
name|groupDetail
operator|.
name|canModify
argument_list|)
expr_stmt|;
name|saveOwner
operator|.
name|setVisible
argument_list|(
name|groupDetail
operator|.
name|canModify
argument_list|)
expr_stmt|;
name|saveDesc
operator|.
name|setVisible
argument_list|(
name|groupDetail
operator|.
name|canModify
argument_list|)
expr_stmt|;
name|saveGroupOptions
operator|.
name|setVisible
argument_list|(
name|groupDetail
operator|.
name|canModify
argument_list|)
expr_stmt|;
name|saveType
operator|.
name|setVisible
argument_list|(
name|groupDetail
operator|.
name|canModify
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

