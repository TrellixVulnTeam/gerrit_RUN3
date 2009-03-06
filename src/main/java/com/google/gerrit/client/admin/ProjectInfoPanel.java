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
name|gwtjsonrpc
operator|.
name|client
operator|.
name|VoidResult
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
DECL|field|projectId
specifier|private
name|Project
operator|.
name|Id
name|projectId
decl_stmt|;
DECL|field|ownerPanel
specifier|private
name|Panel
name|ownerPanel
decl_stmt|;
DECL|field|ownerTxtBox
specifier|private
name|TextBox
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
name|TextArea
name|descTxt
decl_stmt|;
DECL|field|saveDesc
specifier|private
name|Button
name|saveDesc
decl_stmt|;
DECL|method|ProjectInfoPanel (final Project.Id toShow)
specifier|public
name|ProjectInfoPanel
parameter_list|(
specifier|final
name|Project
operator|.
name|Id
name|toShow
parameter_list|)
block|{
specifier|final
name|FlowPanel
name|body
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|initOwner
argument_list|(
name|body
argument_list|)
expr_stmt|;
name|initDescription
argument_list|(
name|body
argument_list|)
expr_stmt|;
name|initWidget
argument_list|(
name|body
argument_list|)
expr_stmt|;
name|projectId
operator|=
name|toShow
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
name|enableForm
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|saveOwner
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|saveDesc
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
name|Util
operator|.
name|PROJECT_SVC
operator|.
name|projectDetail
argument_list|(
name|projectId
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
name|saveOwner
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|saveDesc
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
name|ownerTxtBox
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
block|}
DECL|method|initOwner (final Panel body)
specifier|private
name|void
name|initOwner
parameter_list|(
specifier|final
name|Panel
name|body
parameter_list|)
block|{
name|ownerPanel
operator|=
operator|new
name|VerticalPanel
argument_list|()
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
name|TextBox
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
name|AccountGroupSuggestOracle
argument_list|()
argument_list|,
name|ownerTxtBox
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
name|Widget
name|sender
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
name|PROJECT_SVC
operator|.
name|changeProjectOwner
argument_list|(
name|projectId
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
name|body
operator|.
name|add
argument_list|(
name|ownerPanel
argument_list|)
expr_stmt|;
operator|new
name|TextSaveButtonListener
argument_list|(
name|ownerTxtBox
argument_list|,
name|saveOwner
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
name|TextArea
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
name|Widget
name|sender
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
name|Util
operator|.
name|PROJECT_SVC
operator|.
name|changeProjectDescription
argument_list|(
name|projectId
argument_list|,
name|txt
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
name|saveDesc
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
specifier|final
name|Project
name|project
init|=
name|result
operator|.
name|project
decl_stmt|;
specifier|final
name|AccountGroup
name|owner
init|=
name|result
operator|.
name|groups
operator|.
name|get
argument_list|(
name|project
operator|.
name|getOwnerGroupId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|owner
operator|!=
literal|null
condition|)
block|{
name|ownerTxt
operator|.
name|setText
argument_list|(
name|owner
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
name|deletedGroup
argument_list|(
name|project
operator|.
name|getOwnerGroupId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ProjectRight
operator|.
name|WILD_PROJECT
operator|.
name|equals
argument_list|(
name|project
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
name|ownerPanel
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ownerPanel
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
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
block|}
block|}
end_class

end_unit

