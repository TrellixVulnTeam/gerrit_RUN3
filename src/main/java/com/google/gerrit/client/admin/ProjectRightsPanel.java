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
name|data
operator|.
name|ApprovalType
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
name|GerritConfig
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
name|AccountGroup
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
name|ApprovalCategory
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
name|ApprovalCategoryValue
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
name|Project
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
name|ProjectRight
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
name|event
operator|.
name|dom
operator|.
name|client
operator|.
name|BlurEvent
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
name|BlurHandler
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
name|event
operator|.
name|dom
operator|.
name|client
operator|.
name|FocusEvent
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
name|FocusHandler
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
name|Grid
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
name|Panel
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
name|gwtexpui
operator|.
name|safehtml
operator|.
name|client
operator|.
name|SafeHtml
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
DECL|class|ProjectRightsPanel
specifier|public
class|class
name|ProjectRightsPanel
extends|extends
name|Composite
block|{
DECL|field|projectName
specifier|private
name|Project
operator|.
name|NameKey
name|projectName
decl_stmt|;
DECL|field|rights
specifier|private
name|RightsTable
name|rights
decl_stmt|;
DECL|field|delRight
specifier|private
name|Button
name|delRight
decl_stmt|;
DECL|field|addRight
specifier|private
name|Button
name|addRight
decl_stmt|;
DECL|field|catBox
specifier|private
name|ListBox
name|catBox
decl_stmt|;
DECL|field|rangeMinBox
specifier|private
name|ListBox
name|rangeMinBox
decl_stmt|;
DECL|field|rangeMaxBox
specifier|private
name|ListBox
name|rangeMaxBox
decl_stmt|;
DECL|field|nameTxtBox
specifier|private
name|NpTextBox
name|nameTxtBox
decl_stmt|;
DECL|field|nameTxt
specifier|private
name|SuggestBox
name|nameTxt
decl_stmt|;
DECL|method|ProjectRightsPanel (final Project.NameKey toShow)
specifier|public
name|ProjectRightsPanel
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|toShow
parameter_list|)
block|{
name|projectName
operator|=
name|toShow
expr_stmt|;
specifier|final
name|FlowPanel
name|body
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|initRights
argument_list|(
name|body
argument_list|)
expr_stmt|;
name|initWidget
argument_list|(
name|body
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
name|enableForm
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|super
operator|.
name|onLoad
argument_list|()
expr_stmt|;
name|Util
operator|.
name|PROJECT_SVC
operator|.
name|projectDetail
argument_list|(
name|projectName
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|ProjectDetail
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
specifier|final
name|ProjectDetail
name|result
parameter_list|)
block|{
name|enableForm
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|display
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|enableForm (final boolean on)
specifier|private
name|void
name|enableForm
parameter_list|(
specifier|final
name|boolean
name|on
parameter_list|)
block|{
name|delRight
operator|.
name|setEnabled
argument_list|(
name|on
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|canAdd
init|=
name|on
operator|&&
name|catBox
operator|.
name|getItemCount
argument_list|()
operator|>
literal|0
decl_stmt|;
name|addRight
operator|.
name|setEnabled
argument_list|(
name|canAdd
argument_list|)
expr_stmt|;
name|nameTxtBox
operator|.
name|setEnabled
argument_list|(
name|canAdd
argument_list|)
expr_stmt|;
name|catBox
operator|.
name|setEnabled
argument_list|(
name|canAdd
argument_list|)
expr_stmt|;
name|rangeMinBox
operator|.
name|setEnabled
argument_list|(
name|canAdd
argument_list|)
expr_stmt|;
name|rangeMaxBox
operator|.
name|setEnabled
argument_list|(
name|canAdd
argument_list|)
expr_stmt|;
block|}
DECL|method|initRights (final Panel body)
specifier|private
name|void
name|initRights
parameter_list|(
specifier|final
name|Panel
name|body
parameter_list|)
block|{
specifier|final
name|FlowPanel
name|addPanel
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|addPanel
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-AddSshKeyPanel"
argument_list|)
expr_stmt|;
specifier|final
name|Grid
name|addGrid
init|=
operator|new
name|Grid
argument_list|(
literal|4
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|catBox
operator|=
operator|new
name|ListBox
argument_list|()
expr_stmt|;
name|rangeMinBox
operator|=
operator|new
name|ListBox
argument_list|()
expr_stmt|;
name|rangeMaxBox
operator|=
operator|new
name|ListBox
argument_list|()
expr_stmt|;
name|catBox
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
specifier|final
name|ChangeEvent
name|event
parameter_list|)
block|{
name|populateRangeBoxes
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|ApprovalType
name|at
range|:
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getApprovalTypes
argument_list|()
operator|.
name|getApprovalTypes
argument_list|()
control|)
block|{
specifier|final
name|ApprovalCategory
name|c
init|=
name|at
operator|.
name|getCategory
argument_list|()
decl_stmt|;
name|catBox
operator|.
name|addItem
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|,
name|c
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
specifier|final
name|ApprovalType
name|at
range|:
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getApprovalTypes
argument_list|()
operator|.
name|getActionTypes
argument_list|()
control|)
block|{
specifier|final
name|ApprovalCategory
name|c
init|=
name|at
operator|.
name|getCategory
argument_list|()
decl_stmt|;
if|if
condition|(
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getWildProject
argument_list|()
operator|.
name|equals
argument_list|(
name|projectName
argument_list|)
operator|&&
name|ApprovalCategory
operator|.
name|OWN
operator|.
name|equals
argument_list|(
name|c
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
comment|// Giving out control of the WILD_PROJECT to other groups beyond
comment|// Administrators is dangerous. Having control over WILD_PROJECT
comment|// is about the same as having Administrator access as users are
comment|// able to affect grants in all projects on the system.
comment|//
continue|continue;
block|}
name|catBox
operator|.
name|addItem
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|,
name|c
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|catBox
operator|.
name|getItemCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|catBox
operator|.
name|setSelectedIndex
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|populateRangeBoxes
argument_list|()
expr_stmt|;
block|}
name|addGrid
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|Util
operator|.
name|C
operator|.
name|columnApprovalCategory
argument_list|()
operator|+
literal|":"
argument_list|)
expr_stmt|;
name|addGrid
operator|.
name|setWidget
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
name|catBox
argument_list|)
expr_stmt|;
name|nameTxtBox
operator|=
operator|new
name|NpTextBox
argument_list|()
expr_stmt|;
name|nameTxt
operator|=
operator|new
name|SuggestBox
argument_list|(
operator|new
name|AccountGroupSuggestOracle
argument_list|()
argument_list|,
name|nameTxtBox
argument_list|)
expr_stmt|;
name|nameTxtBox
operator|.
name|setVisibleLength
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|nameTxtBox
operator|.
name|setText
argument_list|(
name|Util
operator|.
name|C
operator|.
name|defaultAccountGroupName
argument_list|()
argument_list|)
expr_stmt|;
name|nameTxtBox
operator|.
name|addStyleName
argument_list|(
literal|"gerrit-InputFieldTypeHint"
argument_list|)
expr_stmt|;
name|nameTxtBox
operator|.
name|addFocusHandler
argument_list|(
operator|new
name|FocusHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onFocus
parameter_list|(
name|FocusEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|Util
operator|.
name|C
operator|.
name|defaultAccountGroupName
argument_list|()
operator|.
name|equals
argument_list|(
name|nameTxtBox
operator|.
name|getText
argument_list|()
argument_list|)
condition|)
block|{
name|nameTxtBox
operator|.
name|setText
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|nameTxtBox
operator|.
name|removeStyleName
argument_list|(
literal|"gerrit-InputFieldTypeHint"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|nameTxtBox
operator|.
name|addBlurHandler
argument_list|(
operator|new
name|BlurHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onBlur
parameter_list|(
name|BlurEvent
name|event
parameter_list|)
block|{
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|nameTxtBox
operator|.
name|getText
argument_list|()
argument_list|)
condition|)
block|{
name|nameTxtBox
operator|.
name|setText
argument_list|(
name|Util
operator|.
name|C
operator|.
name|defaultAccountGroupName
argument_list|()
argument_list|)
expr_stmt|;
name|nameTxtBox
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
name|addGrid
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
name|columnGroupName
argument_list|()
operator|+
literal|":"
argument_list|)
expr_stmt|;
name|addGrid
operator|.
name|setWidget
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
name|nameTxt
argument_list|)
expr_stmt|;
name|addGrid
operator|.
name|setText
argument_list|(
literal|2
argument_list|,
literal|0
argument_list|,
name|Util
operator|.
name|C
operator|.
name|columnRightRange
argument_list|()
operator|+
literal|":"
argument_list|)
expr_stmt|;
name|addGrid
operator|.
name|setWidget
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|,
name|rangeMinBox
argument_list|)
expr_stmt|;
name|addGrid
operator|.
name|setText
argument_list|(
literal|3
argument_list|,
literal|0
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|addGrid
operator|.
name|setWidget
argument_list|(
literal|3
argument_list|,
literal|1
argument_list|,
name|rangeMaxBox
argument_list|)
expr_stmt|;
name|addRight
operator|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonAddProjectRight
argument_list|()
argument_list|)
expr_stmt|;
name|addRight
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
name|doAddNewRight
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
name|addGrid
argument_list|)
expr_stmt|;
name|addPanel
operator|.
name|add
argument_list|(
name|addRight
argument_list|)
expr_stmt|;
name|rights
operator|=
operator|new
name|RightsTable
argument_list|()
expr_stmt|;
name|delRight
operator|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonDeleteGroupMembers
argument_list|()
argument_list|)
expr_stmt|;
name|delRight
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
name|rights
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
operator|new
name|SmallHeading
argument_list|(
name|Util
operator|.
name|C
operator|.
name|headingAccessRights
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|rights
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|delRight
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|addPanel
argument_list|)
expr_stmt|;
block|}
DECL|method|display (final ProjectDetail result)
name|void
name|display
parameter_list|(
specifier|final
name|ProjectDetail
name|result
parameter_list|)
block|{
name|rights
operator|.
name|display
argument_list|(
name|result
operator|.
name|groups
argument_list|,
name|result
operator|.
name|rights
argument_list|)
expr_stmt|;
block|}
DECL|method|doAddNewRight ()
specifier|private
name|void
name|doAddNewRight
parameter_list|()
block|{
name|int
name|idx
init|=
name|catBox
operator|.
name|getSelectedIndex
argument_list|()
decl_stmt|;
specifier|final
name|ApprovalType
name|at
decl_stmt|;
name|ApprovalCategoryValue
name|min
decl_stmt|,
name|max
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
return|return;
block|}
name|at
operator|=
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getApprovalTypes
argument_list|()
operator|.
name|getApprovalType
argument_list|(
operator|new
name|ApprovalCategory
operator|.
name|Id
argument_list|(
name|catBox
operator|.
name|getValue
argument_list|(
name|idx
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|at
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|idx
operator|=
name|rangeMinBox
operator|.
name|getSelectedIndex
argument_list|()
expr_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
return|return;
block|}
name|min
operator|=
name|at
operator|.
name|getValue
argument_list|(
name|Short
operator|.
name|parseShort
argument_list|(
name|rangeMinBox
operator|.
name|getValue
argument_list|(
name|idx
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|min
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|idx
operator|=
name|rangeMaxBox
operator|.
name|getSelectedIndex
argument_list|()
expr_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
return|return;
block|}
name|max
operator|=
name|at
operator|.
name|getValue
argument_list|(
name|Short
operator|.
name|parseShort
argument_list|(
name|rangeMaxBox
operator|.
name|getValue
argument_list|(
name|idx
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|max
operator|==
literal|null
condition|)
block|{
return|return;
block|}
specifier|final
name|String
name|groupName
init|=
name|nameTxt
operator|.
name|getText
argument_list|()
decl_stmt|;
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|groupName
argument_list|)
operator|||
name|Util
operator|.
name|C
operator|.
name|defaultAccountGroupName
argument_list|()
operator|.
name|equals
argument_list|(
name|groupName
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|min
operator|.
name|getValue
argument_list|()
operator|>
name|max
operator|.
name|getValue
argument_list|()
condition|)
block|{
comment|// If the user selects it backwards in the web UI, help them out
comment|// by reversing the order to what we would expect.
comment|//
specifier|final
name|ApprovalCategoryValue
name|newMin
init|=
name|max
decl_stmt|;
specifier|final
name|ApprovalCategoryValue
name|newMax
init|=
name|min
decl_stmt|;
name|min
operator|=
name|newMin
expr_stmt|;
name|max
operator|=
name|newMax
expr_stmt|;
block|}
name|addRight
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Util
operator|.
name|PROJECT_SVC
operator|.
name|addRight
argument_list|(
name|projectName
argument_list|,
name|at
operator|.
name|getCategory
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|groupName
argument_list|,
name|min
operator|.
name|getValue
argument_list|()
argument_list|,
name|max
operator|.
name|getValue
argument_list|()
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|ProjectDetail
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
specifier|final
name|ProjectDetail
name|result
parameter_list|)
block|{
name|addRight
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
name|display
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
name|addRight
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
DECL|method|populateRangeBoxes ()
specifier|private
name|void
name|populateRangeBoxes
parameter_list|()
block|{
specifier|final
name|int
name|idx
init|=
name|catBox
operator|.
name|getSelectedIndex
argument_list|()
decl_stmt|;
specifier|final
name|ApprovalType
name|at
decl_stmt|;
if|if
condition|(
name|idx
operator|>=
literal|0
condition|)
block|{
name|at
operator|=
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getApprovalTypes
argument_list|()
operator|.
name|getApprovalType
argument_list|(
operator|new
name|ApprovalCategory
operator|.
name|Id
argument_list|(
name|catBox
operator|.
name|getValue
argument_list|(
name|idx
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|at
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|at
operator|!=
literal|null
operator|&&
operator|!
name|at
operator|.
name|getValues
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|int
name|curIndex
init|=
literal|0
decl_stmt|,
name|minIndex
init|=
operator|-
literal|1
decl_stmt|,
name|maxIndex
init|=
operator|-
literal|1
decl_stmt|;
name|rangeMinBox
operator|.
name|clear
argument_list|()
expr_stmt|;
name|rangeMaxBox
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|ApprovalCategoryValue
name|v
range|:
name|at
operator|.
name|getValues
argument_list|()
control|)
block|{
specifier|final
name|String
name|vStr
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|v
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|nStr
init|=
name|vStr
operator|+
literal|": "
operator|+
name|v
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|getValue
argument_list|()
operator|>
literal|0
condition|)
block|{
name|nStr
operator|=
literal|"+"
operator|+
name|nStr
expr_stmt|;
block|}
name|rangeMinBox
operator|.
name|addItem
argument_list|(
name|nStr
argument_list|,
name|vStr
argument_list|)
expr_stmt|;
name|rangeMaxBox
operator|.
name|addItem
argument_list|(
name|nStr
argument_list|,
name|vStr
argument_list|)
expr_stmt|;
if|if
condition|(
name|v
operator|.
name|getValue
argument_list|()
operator|<
literal|0
condition|)
block|{
name|minIndex
operator|=
name|curIndex
expr_stmt|;
block|}
if|if
condition|(
name|maxIndex
argument_list|<
literal|0
operator|&&
name|v
operator|.
name|getValue
operator|(
operator|)
argument_list|>
literal|0
condition|)
block|{
name|maxIndex
operator|=
name|curIndex
expr_stmt|;
block|}
name|curIndex
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|ApprovalCategory
operator|.
name|READ
operator|.
name|equals
argument_list|(
name|at
operator|.
name|getCategory
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
comment|// Special case; for READ the most logical range is just
comment|// +1 READ, so assume that as the default for both.
name|minIndex
operator|=
name|maxIndex
expr_stmt|;
block|}
name|rangeMinBox
operator|.
name|setSelectedIndex
argument_list|(
name|minIndex
operator|>=
literal|0
condition|?
name|minIndex
else|:
literal|0
argument_list|)
expr_stmt|;
name|rangeMaxBox
operator|.
name|setSelectedIndex
argument_list|(
name|maxIndex
operator|>=
literal|0
condition|?
name|maxIndex
else|:
name|curIndex
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rangeMinBox
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|rangeMaxBox
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|RightsTable
specifier|private
class|class
name|RightsTable
extends|extends
name|FancyFlexTable
argument_list|<
name|ProjectRight
argument_list|>
block|{
DECL|method|RightsTable ()
name|RightsTable
parameter_list|()
block|{
name|table
operator|.
name|setWidth
argument_list|(
literal|""
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
name|Util
operator|.
name|C
operator|.
name|columnApprovalCategory
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
name|columnGroupName
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
name|columnRightRange
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
DECL|method|deleteChecked ()
name|void
name|deleteChecked
parameter_list|()
block|{
specifier|final
name|HashSet
argument_list|<
name|ProjectRight
operator|.
name|Key
argument_list|>
name|ids
init|=
operator|new
name|HashSet
argument_list|<
name|ProjectRight
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
name|ProjectRight
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
name|table
operator|.
name|getWidget
argument_list|(
name|row
argument_list|,
literal|1
argument_list|)
operator|instanceof
name|CheckBox
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
name|getValue
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
name|PROJECT_SVC
operator|.
name|deleteRight
argument_list|(
name|projectName
argument_list|,
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
name|ProjectRight
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
DECL|method|display (final Map<AccountGroup.Id, AccountGroup> groups, final List<ProjectRight> result)
name|void
name|display
parameter_list|(
specifier|final
name|Map
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|,
name|AccountGroup
argument_list|>
name|groups
parameter_list|,
specifier|final
name|List
argument_list|<
name|ProjectRight
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
name|ProjectRight
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
name|groups
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|populate (final int row, final Map<AccountGroup.Id, AccountGroup> groups, final ProjectRight k)
name|void
name|populate
parameter_list|(
specifier|final
name|int
name|row
parameter_list|,
specifier|final
name|Map
argument_list|<
name|AccountGroup
operator|.
name|Id
argument_list|,
name|AccountGroup
argument_list|>
name|groups
parameter_list|,
specifier|final
name|ProjectRight
name|k
parameter_list|)
block|{
specifier|final
name|GerritConfig
name|config
init|=
name|Gerrit
operator|.
name|getConfig
argument_list|()
decl_stmt|;
specifier|final
name|ApprovalType
name|ar
init|=
name|config
operator|.
name|getApprovalTypes
argument_list|()
operator|.
name|getApprovalType
argument_list|(
name|k
operator|.
name|getApprovalCategoryId
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|AccountGroup
name|group
init|=
name|groups
operator|.
name|get
argument_list|(
name|k
operator|.
name|getAccountGroupId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getWildProject
argument_list|()
operator|.
name|equals
argument_list|(
name|k
operator|.
name|getProjectNameKey
argument_list|()
argument_list|)
operator|&&
operator|!
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getWildProject
argument_list|()
operator|.
name|equals
argument_list|(
name|projectName
argument_list|)
condition|)
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
else|else
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
if|if
condition|(
name|ar
operator|!=
literal|null
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
name|ar
operator|.
name|getCategory
argument_list|()
operator|.
name|getName
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
literal|2
argument_list|,
name|k
operator|.
name|getApprovalCategoryId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|group
operator|!=
literal|null
condition|)
block|{
name|table
operator|.
name|setText
argument_list|(
name|row
argument_list|,
literal|3
argument_list|,
name|group
operator|.
name|getName
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
literal|3
argument_list|,
name|Util
operator|.
name|M
operator|.
name|deletedGroup
argument_list|(
name|k
operator|.
name|getAccountGroupId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
specifier|final
name|SafeHtmlBuilder
name|m
init|=
operator|new
name|SafeHtmlBuilder
argument_list|()
decl_stmt|;
specifier|final
name|ApprovalCategoryValue
name|min
decl_stmt|,
name|max
decl_stmt|;
name|min
operator|=
name|ar
operator|!=
literal|null
condition|?
name|ar
operator|.
name|getValue
argument_list|(
name|k
operator|.
name|getMinValue
argument_list|()
argument_list|)
else|:
literal|null
expr_stmt|;
name|max
operator|=
name|ar
operator|!=
literal|null
condition|?
name|ar
operator|.
name|getValue
argument_list|(
name|k
operator|.
name|getMaxValue
argument_list|()
argument_list|)
else|:
literal|null
expr_stmt|;
name|formatValue
argument_list|(
name|m
argument_list|,
name|k
operator|.
name|getMinValue
argument_list|()
argument_list|,
name|min
argument_list|)
expr_stmt|;
if|if
condition|(
name|k
operator|.
name|getMinValue
argument_list|()
operator|!=
name|k
operator|.
name|getMaxValue
argument_list|()
condition|)
block|{
name|m
operator|.
name|br
argument_list|()
expr_stmt|;
name|formatValue
argument_list|(
name|m
argument_list|,
name|k
operator|.
name|getMaxValue
argument_list|()
argument_list|,
name|max
argument_list|)
expr_stmt|;
block|}
name|SafeHtml
operator|.
name|set
argument_list|(
name|table
argument_list|,
name|row
argument_list|,
literal|4
argument_list|,
name|m
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
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
literal|4
argument_list|,
literal|"gerrit-ProjectAdmin-ApprovalCategoryRangeLine"
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
DECL|method|formatValue (final SafeHtmlBuilder m, final short v, final ApprovalCategoryValue e)
specifier|private
name|void
name|formatValue
parameter_list|(
specifier|final
name|SafeHtmlBuilder
name|m
parameter_list|,
specifier|final
name|short
name|v
parameter_list|,
specifier|final
name|ApprovalCategoryValue
name|e
parameter_list|)
block|{
name|m
operator|.
name|openSpan
argument_list|()
expr_stmt|;
name|m
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-ProjectAdmin-ApprovalCategoryValue"
argument_list|)
expr_stmt|;
if|if
condition|(
name|v
operator|==
literal|0
condition|)
block|{
name|m
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|v
operator|>
literal|0
condition|)
block|{
name|m
operator|.
name|append
argument_list|(
literal|'+'
argument_list|)
expr_stmt|;
block|}
name|m
operator|.
name|append
argument_list|(
name|v
argument_list|)
expr_stmt|;
name|m
operator|.
name|closeSpan
argument_list|()
expr_stmt|;
if|if
condition|(
name|e
operator|!=
literal|null
condition|)
block|{
name|m
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
name|m
operator|.
name|append
argument_list|(
name|e
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

