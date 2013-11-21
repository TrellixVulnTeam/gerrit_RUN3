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
name|access
operator|.
name|AccessMap
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
name|access
operator|.
name|ProjectAccessInfo
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
name|actions
operator|.
name|ActionButton
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
name|actions
operator|.
name|ActionInfo
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
name|change
operator|.
name|Resources
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
name|download
operator|.
name|DownloadPanel
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
name|projects
operator|.
name|ConfigInfo
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
name|projects
operator|.
name|ConfigInfo
operator|.
name|InheritedBooleanInfo
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
name|projects
operator|.
name|ProjectApi
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
name|CallbackGroup
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
name|rpc
operator|.
name|NativeMap
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
name|ScreenLoadCallback
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
name|reviewdb
operator|.
name|client
operator|.
name|AccountGeneralPreferences
operator|.
name|DownloadCommand
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
name|reviewdb
operator|.
name|client
operator|.
name|Project
operator|.
name|InheritableBoolean
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
name|Project
operator|.
name|SubmitType
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
name|FlexTable
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
name|HorizontalPanel
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

begin_class
DECL|class|ProjectInfoScreen
specifier|public
class|class
name|ProjectInfoScreen
extends|extends
name|ProjectScreen
block|{
DECL|field|isOwner
specifier|private
name|boolean
name|isOwner
decl_stmt|;
DECL|field|grid
specifier|private
name|LabeledWidgetsGrid
name|grid
decl_stmt|;
DECL|field|actionsGrid
specifier|private
name|LabeledWidgetsGrid
name|actionsGrid
decl_stmt|;
comment|// Section: Project Options
DECL|field|requireChangeID
specifier|private
name|ListBox
name|requireChangeID
decl_stmt|;
DECL|field|submitType
specifier|private
name|ListBox
name|submitType
decl_stmt|;
DECL|field|state
specifier|private
name|ListBox
name|state
decl_stmt|;
DECL|field|contentMerge
specifier|private
name|ListBox
name|contentMerge
decl_stmt|;
DECL|field|maxObjectSizeLimit
specifier|private
name|NpTextBox
name|maxObjectSizeLimit
decl_stmt|;
DECL|field|effectiveMaxObjectSizeLimit
specifier|private
name|Label
name|effectiveMaxObjectSizeLimit
decl_stmt|;
comment|// Section: Contributor Agreements
DECL|field|contributorAgreements
specifier|private
name|ListBox
name|contributorAgreements
decl_stmt|;
DECL|field|signedOffBy
specifier|private
name|ListBox
name|signedOffBy
decl_stmt|;
DECL|field|descTxt
specifier|private
name|NpTextArea
name|descTxt
decl_stmt|;
DECL|field|saveProject
specifier|private
name|Button
name|saveProject
decl_stmt|;
DECL|field|saveEnabler
specifier|private
name|OnEditEnabler
name|saveEnabler
decl_stmt|;
DECL|method|ProjectInfoScreen (final Project.NameKey toShow)
specifier|public
name|ProjectInfoScreen
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|toShow
parameter_list|)
block|{
name|super
argument_list|(
name|toShow
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
name|Resources
operator|.
name|I
operator|.
name|style
argument_list|()
operator|.
name|ensureInjected
argument_list|()
expr_stmt|;
name|saveProject
operator|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonSaveChanges
argument_list|()
argument_list|)
expr_stmt|;
name|saveProject
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
name|doSave
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|ProjectDownloadPanel
argument_list|(
name|getProjectKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|initDescription
argument_list|()
expr_stmt|;
name|grid
operator|=
operator|new
name|LabeledWidgetsGrid
argument_list|()
expr_stmt|;
name|actionsGrid
operator|=
operator|new
name|LabeledWidgetsGrid
argument_list|()
expr_stmt|;
name|initProjectOptions
argument_list|()
expr_stmt|;
name|initAgreements
argument_list|()
expr_stmt|;
name|add
argument_list|(
name|grid
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|saveProject
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|actionsGrid
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
name|Project
operator|.
name|NameKey
name|project
init|=
name|getProjectKey
argument_list|()
decl_stmt|;
name|CallbackGroup
name|cbg
init|=
operator|new
name|CallbackGroup
argument_list|()
decl_stmt|;
name|AccessMap
operator|.
name|get
argument_list|(
name|project
argument_list|,
name|cbg
operator|.
name|add
argument_list|(
operator|new
name|GerritCallback
argument_list|<
name|ProjectAccessInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|ProjectAccessInfo
name|result
parameter_list|)
block|{
name|isOwner
operator|=
name|result
operator|.
name|isOwner
argument_list|()
expr_stmt|;
name|enableForm
argument_list|()
expr_stmt|;
name|saveProject
operator|.
name|setVisible
argument_list|(
name|isOwner
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|ProjectApi
operator|.
name|getConfig
argument_list|(
name|project
argument_list|,
name|cbg
operator|.
name|addFinal
argument_list|(
operator|new
name|ScreenLoadCallback
argument_list|<
name|ConfigInfo
argument_list|>
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|preDisplay
parameter_list|(
name|ConfigInfo
name|result
parameter_list|)
block|{
name|display
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|savedPanel
operator|=
name|INFO
expr_stmt|;
block|}
DECL|method|enableForm ()
specifier|private
name|void
name|enableForm
parameter_list|()
block|{
name|enableForm
argument_list|(
name|isOwner
argument_list|)
expr_stmt|;
block|}
DECL|method|enableForm (boolean isOwner)
specifier|private
name|void
name|enableForm
parameter_list|(
name|boolean
name|isOwner
parameter_list|)
block|{
name|submitType
operator|.
name|setEnabled
argument_list|(
name|isOwner
argument_list|)
expr_stmt|;
name|state
operator|.
name|setEnabled
argument_list|(
name|isOwner
argument_list|)
expr_stmt|;
name|contentMerge
operator|.
name|setEnabled
argument_list|(
name|isOwner
argument_list|)
expr_stmt|;
name|descTxt
operator|.
name|setEnabled
argument_list|(
name|isOwner
argument_list|)
expr_stmt|;
name|contributorAgreements
operator|.
name|setEnabled
argument_list|(
name|isOwner
argument_list|)
expr_stmt|;
name|signedOffBy
operator|.
name|setEnabled
argument_list|(
name|isOwner
argument_list|)
expr_stmt|;
name|requireChangeID
operator|.
name|setEnabled
argument_list|(
name|isOwner
argument_list|)
expr_stmt|;
name|maxObjectSizeLimit
operator|.
name|setEnabled
argument_list|(
name|isOwner
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
name|add
argument_list|(
name|vp
argument_list|)
expr_stmt|;
name|saveEnabler
operator|=
operator|new
name|OnEditEnabler
argument_list|(
name|saveProject
argument_list|)
expr_stmt|;
name|saveEnabler
operator|.
name|listenTo
argument_list|(
name|descTxt
argument_list|)
expr_stmt|;
block|}
DECL|method|initProjectOptions ()
specifier|private
name|void
name|initProjectOptions
parameter_list|()
block|{
name|grid
operator|.
name|addHeader
argument_list|(
operator|new
name|SmallHeading
argument_list|(
name|Util
operator|.
name|C
operator|.
name|headingProjectOptions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|submitType
operator|=
operator|new
name|ListBox
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|Project
operator|.
name|SubmitType
name|type
range|:
name|Project
operator|.
name|SubmitType
operator|.
name|values
argument_list|()
control|)
block|{
name|submitType
operator|.
name|addItem
argument_list|(
name|Util
operator|.
name|toLongString
argument_list|(
name|type
argument_list|)
argument_list|,
name|type
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|submitType
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
name|setEnabledForUseContentMerge
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|saveEnabler
operator|.
name|listenTo
argument_list|(
name|submitType
argument_list|)
expr_stmt|;
name|grid
operator|.
name|add
argument_list|(
name|Util
operator|.
name|C
operator|.
name|headingProjectSubmitType
argument_list|()
argument_list|,
name|submitType
argument_list|)
expr_stmt|;
name|state
operator|=
operator|new
name|ListBox
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|Project
operator|.
name|State
name|stateValue
range|:
name|Project
operator|.
name|State
operator|.
name|values
argument_list|()
control|)
block|{
name|state
operator|.
name|addItem
argument_list|(
name|Util
operator|.
name|toLongString
argument_list|(
name|stateValue
argument_list|)
argument_list|,
name|stateValue
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|saveEnabler
operator|.
name|listenTo
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|grid
operator|.
name|add
argument_list|(
name|Util
operator|.
name|C
operator|.
name|headingProjectState
argument_list|()
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|contentMerge
operator|=
name|newInheritedBooleanBox
argument_list|()
expr_stmt|;
name|saveEnabler
operator|.
name|listenTo
argument_list|(
name|contentMerge
argument_list|)
expr_stmt|;
name|grid
operator|.
name|add
argument_list|(
name|Util
operator|.
name|C
operator|.
name|useContentMerge
argument_list|()
argument_list|,
name|contentMerge
argument_list|)
expr_stmt|;
name|requireChangeID
operator|=
name|newInheritedBooleanBox
argument_list|()
expr_stmt|;
name|saveEnabler
operator|.
name|listenTo
argument_list|(
name|requireChangeID
argument_list|)
expr_stmt|;
name|grid
operator|.
name|addHtml
argument_list|(
name|Util
operator|.
name|C
operator|.
name|requireChangeID
argument_list|()
argument_list|,
name|requireChangeID
argument_list|)
expr_stmt|;
name|maxObjectSizeLimit
operator|=
operator|new
name|NpTextBox
argument_list|()
expr_stmt|;
name|saveEnabler
operator|.
name|listenTo
argument_list|(
name|maxObjectSizeLimit
argument_list|)
expr_stmt|;
name|effectiveMaxObjectSizeLimit
operator|=
operator|new
name|Label
argument_list|()
expr_stmt|;
name|effectiveMaxObjectSizeLimit
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
name|maxObjectSizeLimitEffectiveLabel
argument_list|()
argument_list|)
expr_stmt|;
name|HorizontalPanel
name|p
init|=
operator|new
name|HorizontalPanel
argument_list|()
decl_stmt|;
name|p
operator|.
name|add
argument_list|(
name|maxObjectSizeLimit
argument_list|)
expr_stmt|;
name|p
operator|.
name|add
argument_list|(
name|effectiveMaxObjectSizeLimit
argument_list|)
expr_stmt|;
name|grid
operator|.
name|addHtml
argument_list|(
name|Util
operator|.
name|C
operator|.
name|headingMaxObjectSizeLimit
argument_list|()
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
DECL|method|newInheritedBooleanBox ()
specifier|private
specifier|static
name|ListBox
name|newInheritedBooleanBox
parameter_list|()
block|{
name|ListBox
name|box
init|=
operator|new
name|ListBox
argument_list|()
decl_stmt|;
for|for
control|(
name|InheritableBoolean
name|b
range|:
name|InheritableBoolean
operator|.
name|values
argument_list|()
control|)
block|{
name|box
operator|.
name|addItem
argument_list|(
name|b
operator|.
name|name
argument_list|()
argument_list|,
name|b
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|box
return|;
block|}
comment|/**    * Enables the {@link #contentMerge} checkbox if the selected submit type    * allows the usage of content merge.    * If the submit type (currently only 'Fast Forward Only') does not allow    * content merge the useContentMerge checkbox gets disabled.    */
DECL|method|setEnabledForUseContentMerge ()
specifier|private
name|void
name|setEnabledForUseContentMerge
parameter_list|()
block|{
if|if
condition|(
name|SubmitType
operator|.
name|FAST_FORWARD_ONLY
operator|.
name|equals
argument_list|(
name|Project
operator|.
name|SubmitType
operator|.
name|valueOf
argument_list|(
name|submitType
operator|.
name|getValue
argument_list|(
name|submitType
operator|.
name|getSelectedIndex
argument_list|()
argument_list|)
argument_list|)
argument_list|)
condition|)
block|{
name|contentMerge
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|InheritedBooleanInfo
name|b
init|=
name|InheritedBooleanInfo
operator|.
name|create
argument_list|()
decl_stmt|;
name|b
operator|.
name|setConfiguredValue
argument_list|(
name|InheritableBoolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|setBool
argument_list|(
name|contentMerge
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|contentMerge
operator|.
name|setEnabled
argument_list|(
name|submitType
operator|.
name|isEnabled
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|initAgreements ()
specifier|private
name|void
name|initAgreements
parameter_list|()
block|{
name|grid
operator|.
name|addHeader
argument_list|(
operator|new
name|SmallHeading
argument_list|(
name|Util
operator|.
name|C
operator|.
name|headingAgreements
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|contributorAgreements
operator|=
name|newInheritedBooleanBox
argument_list|()
expr_stmt|;
if|if
condition|(
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|isUseContributorAgreements
argument_list|()
condition|)
block|{
name|saveEnabler
operator|.
name|listenTo
argument_list|(
name|contributorAgreements
argument_list|)
expr_stmt|;
name|grid
operator|.
name|add
argument_list|(
name|Util
operator|.
name|C
operator|.
name|useContributorAgreements
argument_list|()
argument_list|,
name|contributorAgreements
argument_list|)
expr_stmt|;
block|}
name|signedOffBy
operator|=
name|newInheritedBooleanBox
argument_list|()
expr_stmt|;
name|saveEnabler
operator|.
name|listenTo
argument_list|(
name|signedOffBy
argument_list|)
expr_stmt|;
name|grid
operator|.
name|addHtml
argument_list|(
name|Util
operator|.
name|C
operator|.
name|useSignedOffBy
argument_list|()
argument_list|,
name|signedOffBy
argument_list|)
expr_stmt|;
block|}
DECL|method|setSubmitType (final Project.SubmitType newSubmitType)
specifier|private
name|void
name|setSubmitType
parameter_list|(
specifier|final
name|Project
operator|.
name|SubmitType
name|newSubmitType
parameter_list|)
block|{
name|int
name|index
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|submitType
operator|!=
literal|null
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
name|submitType
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
name|newSubmitType
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|submitType
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|index
operator|=
name|i
expr_stmt|;
break|break;
block|}
block|}
name|submitType
operator|.
name|setSelectedIndex
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|setEnabledForUseContentMerge
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|setState (final Project.State newState)
specifier|private
name|void
name|setState
parameter_list|(
specifier|final
name|Project
operator|.
name|State
name|newState
parameter_list|)
block|{
if|if
condition|(
name|state
operator|!=
literal|null
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
name|state
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
name|newState
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|state
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|state
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
block|}
DECL|method|setBool (ListBox box, InheritedBooleanInfo inheritedBoolean)
specifier|private
name|void
name|setBool
parameter_list|(
name|ListBox
name|box
parameter_list|,
name|InheritedBooleanInfo
name|inheritedBoolean
parameter_list|)
block|{
name|int
name|inheritedIndex
init|=
operator|-
literal|1
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
name|box
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
name|box
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
operator|.
name|startsWith
argument_list|(
name|InheritableBoolean
operator|.
name|INHERIT
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|inheritedIndex
operator|=
name|i
expr_stmt|;
block|}
if|if
condition|(
name|box
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
operator|.
name|startsWith
argument_list|(
name|inheritedBoolean
operator|.
name|configured_value
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|box
operator|.
name|setSelectedIndex
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|inheritedIndex
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|getProjectKey
argument_list|()
operator|.
name|equals
argument_list|(
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|getWildProject
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|box
operator|.
name|getSelectedIndex
argument_list|()
operator|==
name|inheritedIndex
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
name|box
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
name|box
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
operator|.
name|equals
argument_list|(
name|InheritableBoolean
operator|.
name|FALSE
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|box
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
name|box
operator|.
name|removeItem
argument_list|(
name|inheritedIndex
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|box
operator|.
name|setItemText
argument_list|(
name|inheritedIndex
argument_list|,
name|InheritableBoolean
operator|.
name|INHERIT
operator|.
name|name
argument_list|()
operator|+
literal|" ("
operator|+
name|inheritedBoolean
operator|.
name|inherited_value
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getBool (ListBox box)
specifier|private
specifier|static
name|InheritableBoolean
name|getBool
parameter_list|(
name|ListBox
name|box
parameter_list|)
block|{
name|int
name|i
init|=
name|box
operator|.
name|getSelectedIndex
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|>=
literal|0
condition|)
block|{
specifier|final
name|String
name|selectedValue
init|=
name|box
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|selectedValue
operator|.
name|startsWith
argument_list|(
name|InheritableBoolean
operator|.
name|INHERIT
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|InheritableBoolean
operator|.
name|INHERIT
return|;
block|}
return|return
name|InheritableBoolean
operator|.
name|valueOf
argument_list|(
name|selectedValue
argument_list|)
return|;
block|}
return|return
name|InheritableBoolean
operator|.
name|INHERIT
return|;
block|}
DECL|method|display (ConfigInfo result)
name|void
name|display
parameter_list|(
name|ConfigInfo
name|result
parameter_list|)
block|{
name|descTxt
operator|.
name|setText
argument_list|(
name|result
operator|.
name|description
argument_list|()
argument_list|)
expr_stmt|;
name|setBool
argument_list|(
name|contributorAgreements
argument_list|,
name|result
operator|.
name|use_contributor_agreements
argument_list|()
argument_list|)
expr_stmt|;
name|setBool
argument_list|(
name|signedOffBy
argument_list|,
name|result
operator|.
name|use_signed_off_by
argument_list|()
argument_list|)
expr_stmt|;
name|setBool
argument_list|(
name|contentMerge
argument_list|,
name|result
operator|.
name|use_content_merge
argument_list|()
argument_list|)
expr_stmt|;
name|setBool
argument_list|(
name|requireChangeID
argument_list|,
name|result
operator|.
name|require_change_id
argument_list|()
argument_list|)
expr_stmt|;
name|setSubmitType
argument_list|(
name|result
operator|.
name|submit_type
argument_list|()
argument_list|)
expr_stmt|;
name|setState
argument_list|(
name|result
operator|.
name|state
argument_list|()
argument_list|)
expr_stmt|;
name|maxObjectSizeLimit
operator|.
name|setText
argument_list|(
name|result
operator|.
name|max_object_size_limit
argument_list|()
operator|.
name|configured_value
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|.
name|max_object_size_limit
argument_list|()
operator|.
name|inherited_value
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|effectiveMaxObjectSizeLimit
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|effectiveMaxObjectSizeLimit
operator|.
name|setText
argument_list|(
name|Util
operator|.
name|M
operator|.
name|effectiveMaxObjectSizeLimit
argument_list|(
name|result
operator|.
name|max_object_size_limit
argument_list|()
operator|.
name|value
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|effectiveMaxObjectSizeLimit
operator|.
name|setTitle
argument_list|(
name|Util
operator|.
name|M
operator|.
name|globalMaxObjectSizeLimit
argument_list|(
name|result
operator|.
name|max_object_size_limit
argument_list|()
operator|.
name|inherited_value
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|effectiveMaxObjectSizeLimit
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|saveProject
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|initProjectActions
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
DECL|method|initProjectActions (ConfigInfo info)
specifier|private
name|void
name|initProjectActions
parameter_list|(
name|ConfigInfo
name|info
parameter_list|)
block|{
name|actionsGrid
operator|.
name|clear
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|actionsGrid
operator|.
name|removeAllRows
argument_list|()
expr_stmt|;
name|NativeMap
argument_list|<
name|ActionInfo
argument_list|>
name|actions
init|=
name|info
operator|.
name|actions
argument_list|()
decl_stmt|;
if|if
condition|(
name|actions
operator|==
literal|null
operator|||
name|actions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|actions
operator|.
name|copyKeysIntoChildren
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
name|actionsGrid
operator|.
name|addHeader
argument_list|(
operator|new
name|SmallHeading
argument_list|(
name|Util
operator|.
name|C
operator|.
name|headingProjectCommands
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|FlowPanel
name|actionsPanel
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|actionsPanel
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
name|projectActions
argument_list|()
argument_list|)
expr_stmt|;
name|actionsPanel
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|actionsGrid
operator|.
name|add
argument_list|(
name|Util
operator|.
name|C
operator|.
name|headingCommands
argument_list|()
argument_list|,
name|actionsPanel
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|id
range|:
name|actions
operator|.
name|keySet
argument_list|()
control|)
block|{
name|actionsPanel
operator|.
name|add
argument_list|(
operator|new
name|ActionButton
argument_list|(
name|getProjectKey
argument_list|()
argument_list|,
name|actions
operator|.
name|get
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doSave ()
specifier|private
name|void
name|doSave
parameter_list|()
block|{
name|enableForm
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|saveProject
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ProjectApi
operator|.
name|setConfig
argument_list|(
name|getProjectKey
argument_list|()
argument_list|,
name|descTxt
operator|.
name|getText
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|,
name|getBool
argument_list|(
name|contributorAgreements
argument_list|)
argument_list|,
name|getBool
argument_list|(
name|contentMerge
argument_list|)
argument_list|,
name|getBool
argument_list|(
name|signedOffBy
argument_list|)
argument_list|,
name|getBool
argument_list|(
name|requireChangeID
argument_list|)
argument_list|,
name|maxObjectSizeLimit
operator|.
name|getText
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|,
name|Project
operator|.
name|SubmitType
operator|.
name|valueOf
argument_list|(
name|submitType
operator|.
name|getValue
argument_list|(
name|submitType
operator|.
name|getSelectedIndex
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|Project
operator|.
name|State
operator|.
name|valueOf
argument_list|(
name|state
operator|.
name|getValue
argument_list|(
name|state
operator|.
name|getSelectedIndex
argument_list|()
argument_list|)
argument_list|)
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|ConfigInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|ConfigInfo
name|result
parameter_list|)
block|{
name|enableForm
argument_list|()
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
name|Throwable
name|caught
parameter_list|)
block|{
name|enableForm
argument_list|()
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
DECL|class|ProjectDownloadPanel
specifier|public
class|class
name|ProjectDownloadPanel
extends|extends
name|DownloadPanel
block|{
DECL|method|ProjectDownloadPanel (String project, boolean isAllowsAnonymous)
specifier|public
name|ProjectDownloadPanel
parameter_list|(
name|String
name|project
parameter_list|,
name|boolean
name|isAllowsAnonymous
parameter_list|)
block|{
name|super
argument_list|(
name|project
argument_list|,
literal|null
argument_list|,
name|isAllowsAnonymous
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|populateDownloadCommandLinks ()
specifier|public
name|void
name|populateDownloadCommandLinks
parameter_list|()
block|{
if|if
condition|(
operator|!
name|urls
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|allowedCommands
operator|.
name|contains
argument_list|(
name|DownloadCommand
operator|.
name|CHECKOUT
argument_list|)
operator|||
name|allowedCommands
operator|.
name|contains
argument_list|(
name|DownloadCommand
operator|.
name|DEFAULT_DOWNLOADS
argument_list|)
condition|)
block|{
name|commands
operator|.
name|add
argument_list|(
name|cmdLinkfactory
operator|.
expr|new
name|CloneCommandLink
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|class|LabeledWidgetsGrid
specifier|private
class|class
name|LabeledWidgetsGrid
extends|extends
name|FlexTable
block|{
DECL|field|labelSuffix
specifier|private
name|String
name|labelSuffix
decl_stmt|;
DECL|method|LabeledWidgetsGrid ()
specifier|public
name|LabeledWidgetsGrid
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|labelSuffix
operator|=
literal|":"
expr_stmt|;
block|}
DECL|method|addHeader (Widget widget)
specifier|private
name|void
name|addHeader
parameter_list|(
name|Widget
name|widget
parameter_list|)
block|{
name|int
name|row
init|=
name|getRowCount
argument_list|()
decl_stmt|;
name|insertRow
argument_list|(
name|row
argument_list|)
expr_stmt|;
name|setWidget
argument_list|(
name|row
argument_list|,
literal|0
argument_list|,
name|widget
argument_list|)
expr_stmt|;
name|getCellFormatter
argument_list|()
operator|.
name|getElement
argument_list|(
name|row
argument_list|,
literal|0
argument_list|)
operator|.
name|setAttribute
argument_list|(
literal|"colSpan"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
block|}
DECL|method|add (String label, boolean labelIsHtml, Widget widget)
specifier|private
name|void
name|add
parameter_list|(
name|String
name|label
parameter_list|,
name|boolean
name|labelIsHtml
parameter_list|,
name|Widget
name|widget
parameter_list|)
block|{
name|int
name|row
init|=
name|getRowCount
argument_list|()
decl_stmt|;
name|insertRow
argument_list|(
name|row
argument_list|)
expr_stmt|;
if|if
condition|(
name|label
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|labelIsHtml
condition|)
block|{
name|setHTML
argument_list|(
name|row
argument_list|,
literal|0
argument_list|,
name|label
operator|+
name|labelSuffix
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setText
argument_list|(
name|row
argument_list|,
literal|0
argument_list|,
name|label
operator|+
name|labelSuffix
argument_list|)
expr_stmt|;
block|}
block|}
name|setWidget
argument_list|(
name|row
argument_list|,
literal|1
argument_list|,
name|widget
argument_list|)
expr_stmt|;
block|}
DECL|method|add (String label, Widget widget)
specifier|public
name|void
name|add
parameter_list|(
name|String
name|label
parameter_list|,
name|Widget
name|widget
parameter_list|)
block|{
name|add
argument_list|(
name|label
argument_list|,
literal|false
argument_list|,
name|widget
argument_list|)
expr_stmt|;
block|}
DECL|method|addHtml (String label, Widget widget)
specifier|public
name|void
name|addHtml
parameter_list|(
name|String
name|label
parameter_list|,
name|Widget
name|widget
parameter_list|)
block|{
name|add
argument_list|(
name|label
argument_list|,
literal|true
argument_list|,
name|widget
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

