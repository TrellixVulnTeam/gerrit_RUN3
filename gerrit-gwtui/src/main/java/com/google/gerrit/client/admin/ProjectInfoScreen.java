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
name|common
operator|.
name|data
operator|.
name|ProjectDetail
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
name|NpTextArea
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
DECL|field|project
specifier|private
name|Project
name|project
decl_stmt|;
DECL|field|projectOptionsPanel
specifier|private
name|Panel
name|projectOptionsPanel
decl_stmt|;
DECL|field|requireChangeID
specifier|private
name|CheckBox
name|requireChangeID
decl_stmt|;
DECL|field|submitType
specifier|private
name|ListBox
name|submitType
decl_stmt|;
DECL|field|useContentMerge
specifier|private
name|CheckBox
name|useContentMerge
decl_stmt|;
DECL|field|agreementsPanel
specifier|private
name|Panel
name|agreementsPanel
decl_stmt|;
DECL|field|useContributorAgreements
specifier|private
name|CheckBox
name|useContributorAgreements
decl_stmt|;
DECL|field|useSignedOffBy
specifier|private
name|CheckBox
name|useSignedOffBy
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
name|initDescription
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
name|saveProject
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
name|Util
operator|.
name|PROJECT_SVC
operator|.
name|projectDetail
argument_list|(
name|getProjectKey
argument_list|()
argument_list|,
operator|new
name|ScreenLoadCallback
argument_list|<
name|ProjectDetail
argument_list|>
argument_list|(
name|this
argument_list|)
block|{
specifier|public
name|void
name|preDisplay
parameter_list|(
specifier|final
name|ProjectDetail
name|result
parameter_list|)
block|{
name|enableForm
argument_list|(
name|result
operator|.
name|canModifyAgreements
argument_list|,
name|result
operator|.
name|canModifyDescription
argument_list|,
name|result
operator|.
name|canModifyMergeType
argument_list|)
expr_stmt|;
name|saveProject
operator|.
name|setVisible
argument_list|(
name|result
operator|.
name|canModifyAgreements
operator|||
name|result
operator|.
name|canModifyDescription
operator|||
name|result
operator|.
name|canModifyMergeType
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
DECL|method|enableForm (final boolean canModifyAgreements, final boolean canModifyDescription, final boolean canModifyMergeType)
specifier|private
name|void
name|enableForm
parameter_list|(
specifier|final
name|boolean
name|canModifyAgreements
parameter_list|,
specifier|final
name|boolean
name|canModifyDescription
parameter_list|,
specifier|final
name|boolean
name|canModifyMergeType
parameter_list|)
block|{
name|submitType
operator|.
name|setEnabled
argument_list|(
name|canModifyMergeType
argument_list|)
expr_stmt|;
name|useContentMerge
operator|.
name|setEnabled
argument_list|(
name|canModifyMergeType
argument_list|)
expr_stmt|;
name|descTxt
operator|.
name|setEnabled
argument_list|(
name|canModifyDescription
argument_list|)
expr_stmt|;
name|useContributorAgreements
operator|.
name|setEnabled
argument_list|(
name|canModifyAgreements
argument_list|)
expr_stmt|;
name|useSignedOffBy
operator|.
name|setEnabled
argument_list|(
name|canModifyAgreements
argument_list|)
expr_stmt|;
name|requireChangeID
operator|.
name|setEnabled
argument_list|(
name|canModifyMergeType
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
name|projectOptionsPanel
operator|=
operator|new
name|VerticalPanel
argument_list|()
expr_stmt|;
name|projectOptionsPanel
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
name|projectOptionsPanel
operator|.
name|add
argument_list|(
name|submitType
argument_list|)
expr_stmt|;
name|useContentMerge
operator|=
operator|new
name|CheckBox
argument_list|(
name|Util
operator|.
name|C
operator|.
name|useContentMerge
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|saveEnabler
operator|.
name|listenTo
argument_list|(
name|useContentMerge
argument_list|)
expr_stmt|;
name|projectOptionsPanel
operator|.
name|add
argument_list|(
name|useContentMerge
argument_list|)
expr_stmt|;
name|requireChangeID
operator|=
operator|new
name|CheckBox
argument_list|(
name|Util
operator|.
name|C
operator|.
name|requireChangeID
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|saveEnabler
operator|.
name|listenTo
argument_list|(
name|requireChangeID
argument_list|)
expr_stmt|;
name|projectOptionsPanel
operator|.
name|add
argument_list|(
name|requireChangeID
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|projectOptionsPanel
argument_list|)
expr_stmt|;
block|}
comment|/**    * Enables the {@link #useContentMerge} checkbox if the selected submit type    * allows the usage of content merge.    * If the submit type (currently only 'Fast Forward Only') does not allow    * content merge the useContentMerge checkbox gets disabled.    */
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
name|useContentMerge
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|useContentMerge
operator|.
name|setValue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|useContentMerge
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
name|agreementsPanel
operator|=
operator|new
name|VerticalPanel
argument_list|()
expr_stmt|;
name|agreementsPanel
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
name|headingAgreements
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|useContributorAgreements
operator|=
operator|new
name|CheckBox
argument_list|(
name|Util
operator|.
name|C
operator|.
name|useContributorAgreements
argument_list|()
argument_list|)
expr_stmt|;
name|saveEnabler
operator|.
name|listenTo
argument_list|(
name|useContributorAgreements
argument_list|)
expr_stmt|;
name|agreementsPanel
operator|.
name|add
argument_list|(
name|useContributorAgreements
argument_list|)
expr_stmt|;
name|useSignedOffBy
operator|=
operator|new
name|CheckBox
argument_list|(
name|Util
operator|.
name|C
operator|.
name|useSignedOffBy
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|saveEnabler
operator|.
name|listenTo
argument_list|(
name|useSignedOffBy
argument_list|)
expr_stmt|;
name|agreementsPanel
operator|.
name|add
argument_list|(
name|useSignedOffBy
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|agreementsPanel
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
DECL|method|display (final ProjectDetail result)
name|void
name|display
parameter_list|(
specifier|final
name|ProjectDetail
name|result
parameter_list|)
block|{
name|project
operator|=
name|result
operator|.
name|project
expr_stmt|;
specifier|final
name|boolean
name|isall
init|=
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
name|project
operator|.
name|getNameKey
argument_list|()
argument_list|)
decl_stmt|;
name|projectOptionsPanel
operator|.
name|setVisible
argument_list|(
operator|!
name|isall
argument_list|)
expr_stmt|;
name|agreementsPanel
operator|.
name|setVisible
argument_list|(
operator|!
name|isall
argument_list|)
expr_stmt|;
name|useContributorAgreements
operator|.
name|setVisible
argument_list|(
name|Gerrit
operator|.
name|getConfig
argument_list|()
operator|.
name|isUseContributorAgreements
argument_list|()
argument_list|)
expr_stmt|;
name|descTxt
operator|.
name|setText
argument_list|(
name|project
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|useContributorAgreements
operator|.
name|setValue
argument_list|(
name|project
operator|.
name|isUseContributorAgreements
argument_list|()
argument_list|)
expr_stmt|;
name|useSignedOffBy
operator|.
name|setValue
argument_list|(
name|project
operator|.
name|isUseSignedOffBy
argument_list|()
argument_list|)
expr_stmt|;
name|useContentMerge
operator|.
name|setValue
argument_list|(
name|project
operator|.
name|isUseContentMerge
argument_list|()
argument_list|)
expr_stmt|;
name|requireChangeID
operator|.
name|setValue
argument_list|(
name|project
operator|.
name|isRequireChangeID
argument_list|()
argument_list|)
expr_stmt|;
name|setSubmitType
argument_list|(
name|project
operator|.
name|getSubmitType
argument_list|()
argument_list|)
expr_stmt|;
name|saveProject
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|doSave ()
specifier|private
name|void
name|doSave
parameter_list|()
block|{
name|project
operator|.
name|setDescription
argument_list|(
name|descTxt
operator|.
name|getText
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|project
operator|.
name|setUseContributorAgreements
argument_list|(
name|useContributorAgreements
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|project
operator|.
name|setUseSignedOffBy
argument_list|(
name|useSignedOffBy
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|project
operator|.
name|setUseContentMerge
argument_list|(
name|useContentMerge
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|project
operator|.
name|setRequireChangeID
argument_list|(
name|requireChangeID
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|submitType
operator|.
name|getSelectedIndex
argument_list|()
operator|>=
literal|0
condition|)
block|{
name|project
operator|.
name|setSubmitType
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
expr_stmt|;
block|}
name|enableForm
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Util
operator|.
name|PROJECT_SVC
operator|.
name|changeProjectSettings
argument_list|(
name|project
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
name|result
operator|.
name|canModifyAgreements
argument_list|,
name|result
operator|.
name|canModifyDescription
argument_list|,
name|result
operator|.
name|canModifyMergeType
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
block|}
end_class

end_unit

