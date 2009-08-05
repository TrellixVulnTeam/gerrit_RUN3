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
name|client
operator|.
name|ui
operator|.
name|TextSaveButtonListener
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
name|logical
operator|.
name|shared
operator|.
name|ValueChangeEvent
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
name|logical
operator|.
name|shared
operator|.
name|ValueChangeHandler
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
DECL|class|ProjectInfoPanel
specifier|public
class|class
name|ProjectInfoPanel
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
DECL|field|project
specifier|private
name|Project
name|project
decl_stmt|;
DECL|field|submitTypePanel
specifier|private
name|Panel
name|submitTypePanel
decl_stmt|;
DECL|field|submitType
specifier|private
name|ListBox
name|submitType
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
DECL|method|ProjectInfoPanel (final Project.NameKey toShow)
specifier|public
name|ProjectInfoPanel
parameter_list|(
specifier|final
name|Project
operator|.
name|NameKey
name|toShow
parameter_list|)
block|{
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
specifier|final
name|FlowPanel
name|body
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|initDescription
argument_list|(
name|body
argument_list|)
expr_stmt|;
name|initSubmitType
argument_list|(
name|body
argument_list|)
expr_stmt|;
name|initAgreements
argument_list|(
name|body
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|saveProject
argument_list|)
expr_stmt|;
name|initWidget
argument_list|(
name|body
argument_list|)
expr_stmt|;
name|projectName
operator|=
name|toShow
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
name|saveProject
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
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
name|saveProject
operator|.
name|setEnabled
argument_list|(
literal|false
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
name|submitType
operator|.
name|setEnabled
argument_list|(
name|on
argument_list|)
expr_stmt|;
name|descTxt
operator|.
name|setEnabled
argument_list|(
name|on
argument_list|)
expr_stmt|;
name|useContributorAgreements
operator|.
name|setEnabled
argument_list|(
name|on
argument_list|)
expr_stmt|;
name|useSignedOffBy
operator|.
name|setEnabled
argument_list|(
name|on
argument_list|)
expr_stmt|;
block|}
DECL|method|initDescription (final Panel body)
specifier|private
name|void
name|initDescription
parameter_list|(
specifier|final
name|Panel
name|body
parameter_list|)
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
name|body
operator|.
name|add
argument_list|(
name|vp
argument_list|)
expr_stmt|;
operator|new
name|TextSaveButtonListener
argument_list|(
name|descTxt
argument_list|,
name|saveProject
argument_list|)
expr_stmt|;
block|}
DECL|method|initSubmitType (final Panel body)
specifier|private
name|void
name|initSubmitType
parameter_list|(
specifier|final
name|Panel
name|body
parameter_list|)
block|{
name|submitTypePanel
operator|=
operator|new
name|VerticalPanel
argument_list|()
expr_stmt|;
name|submitTypePanel
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
name|headingSubmitType
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
specifier|final
name|ChangeEvent
name|event
parameter_list|)
block|{
name|saveProject
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
name|submitTypePanel
operator|.
name|add
argument_list|(
name|submitType
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|submitTypePanel
argument_list|)
expr_stmt|;
block|}
DECL|method|initAgreements (final Panel body)
specifier|private
name|void
name|initAgreements
parameter_list|(
specifier|final
name|Panel
name|body
parameter_list|)
block|{
specifier|final
name|ValueChangeHandler
argument_list|<
name|Boolean
argument_list|>
name|onChangeSave
init|=
operator|new
name|ValueChangeHandler
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onValueChange
parameter_list|(
name|ValueChangeEvent
argument_list|<
name|Boolean
argument_list|>
name|event
parameter_list|)
block|{
name|saveProject
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
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
name|useContributorAgreements
operator|.
name|addValueChangeHandler
argument_list|(
name|onChangeSave
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
name|useSignedOffBy
operator|.
name|addValueChangeHandler
argument_list|(
name|onChangeSave
argument_list|)
expr_stmt|;
name|agreementsPanel
operator|.
name|add
argument_list|(
name|useSignedOffBy
argument_list|)
expr_stmt|;
name|body
operator|.
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
name|submitType
operator|.
name|setSelectedIndex
argument_list|(
name|i
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|submitType
operator|.
name|setSelectedIndex
argument_list|(
operator|-
literal|1
argument_list|)
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
name|Common
operator|.
name|getGerritConfig
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
name|submitTypePanel
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
name|Common
operator|.
name|getGerritConfig
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
name|setSubmitType
argument_list|(
name|project
operator|.
name|getSubmitType
argument_list|()
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
argument_list|)
expr_stmt|;
name|saveProject
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
literal|true
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
name|refresh
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
block|}
end_class

end_unit

