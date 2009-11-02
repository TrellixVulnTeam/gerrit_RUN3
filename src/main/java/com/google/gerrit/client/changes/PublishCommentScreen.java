begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
DECL|package|com.google.gerrit.client.changes
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|changes
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
name|Link
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
name|patches
operator|.
name|CommentEditorPanel
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
name|patches
operator|.
name|PatchUtil
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
name|Change
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
name|Patch
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
name|PatchLineComment
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
name|PatchSet
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
name|PatchSetApproval
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
name|AccountScreen
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
name|PatchLink
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
name|DOM
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
name|FormPanel
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
name|RadioButton
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
name|FormPanel
operator|.
name|SubmitEvent
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
DECL|class|PublishCommentScreen
specifier|public
class|class
name|PublishCommentScreen
extends|extends
name|AccountScreen
implements|implements
name|ClickHandler
block|{
DECL|field|lastState
specifier|private
specifier|static
name|SavedState
name|lastState
decl_stmt|;
DECL|field|patchSetId
specifier|private
specifier|final
name|PatchSet
operator|.
name|Id
name|patchSetId
decl_stmt|;
DECL|field|approvalButtons
specifier|private
name|Collection
argument_list|<
name|ValueRadioButton
argument_list|>
name|approvalButtons
decl_stmt|;
DECL|field|descBlock
specifier|private
name|ChangeDescriptionBlock
name|descBlock
decl_stmt|;
DECL|field|approvalPanel
specifier|private
name|Panel
name|approvalPanel
decl_stmt|;
DECL|field|message
specifier|private
name|NpTextArea
name|message
decl_stmt|;
DECL|field|draftsPanel
specifier|private
name|Panel
name|draftsPanel
decl_stmt|;
DECL|field|send
specifier|private
name|Button
name|send
decl_stmt|;
DECL|field|cancel
specifier|private
name|Button
name|cancel
decl_stmt|;
DECL|field|saveStateOnUnload
specifier|private
name|boolean
name|saveStateOnUnload
init|=
literal|true
decl_stmt|;
DECL|field|commentEditors
specifier|private
name|List
argument_list|<
name|CommentEditorPanel
argument_list|>
name|commentEditors
decl_stmt|;
DECL|method|PublishCommentScreen (final PatchSet.Id psi)
specifier|public
name|PublishCommentScreen
parameter_list|(
specifier|final
name|PatchSet
operator|.
name|Id
name|psi
parameter_list|)
block|{
name|patchSetId
operator|=
name|psi
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
name|addStyleName
argument_list|(
literal|"gerrit-PublishCommentsScreen"
argument_list|)
expr_stmt|;
name|approvalButtons
operator|=
operator|new
name|ArrayList
argument_list|<
name|ValueRadioButton
argument_list|>
argument_list|()
expr_stmt|;
name|descBlock
operator|=
operator|new
name|ChangeDescriptionBlock
argument_list|()
expr_stmt|;
name|add
argument_list|(
name|descBlock
argument_list|)
expr_stmt|;
specifier|final
name|FormPanel
name|form
init|=
operator|new
name|FormPanel
argument_list|()
decl_stmt|;
specifier|final
name|FlowPanel
name|body
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|form
operator|.
name|setWidget
argument_list|(
name|body
argument_list|)
expr_stmt|;
name|form
operator|.
name|addSubmitHandler
argument_list|(
operator|new
name|FormPanel
operator|.
name|SubmitHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSubmit
parameter_list|(
specifier|final
name|SubmitEvent
name|event
parameter_list|)
block|{
name|event
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|form
argument_list|)
expr_stmt|;
name|approvalPanel
operator|=
operator|new
name|FlowPanel
argument_list|()
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|approvalPanel
argument_list|)
expr_stmt|;
name|initMessage
argument_list|(
name|body
argument_list|)
expr_stmt|;
name|draftsPanel
operator|=
operator|new
name|FlowPanel
argument_list|()
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|draftsPanel
argument_list|)
expr_stmt|;
specifier|final
name|FlowPanel
name|buttonRow
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|buttonRow
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-CommentEditor-Buttons"
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|buttonRow
argument_list|)
expr_stmt|;
name|send
operator|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonPublishCommentsSend
argument_list|()
argument_list|)
expr_stmt|;
name|send
operator|.
name|addClickHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|buttonRow
operator|.
name|add
argument_list|(
name|send
argument_list|)
expr_stmt|;
name|cancel
operator|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonPublishCommentsCancel
argument_list|()
argument_list|)
expr_stmt|;
name|cancel
operator|.
name|addClickHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|buttonRow
operator|.
name|add
argument_list|(
name|cancel
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
name|DETAIL_SVC
operator|.
name|patchSetPublishDetail
argument_list|(
name|patchSetId
argument_list|,
operator|new
name|ScreenLoadCallback
argument_list|<
name|PatchSetPublishDetail
argument_list|>
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|preDisplay
parameter_list|(
specifier|final
name|PatchSetPublishDetail
name|result
parameter_list|)
block|{
name|send
operator|.
name|setEnabled
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
specifier|protected
name|void
name|postDisplay
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
name|Override
DECL|method|onUnload ()
specifier|protected
name|void
name|onUnload
parameter_list|()
block|{
name|super
operator|.
name|onUnload
argument_list|()
expr_stmt|;
name|lastState
operator|=
name|saveStateOnUnload
condition|?
operator|new
name|SavedState
argument_list|(
name|this
argument_list|)
else|:
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onClick (final ClickEvent event)
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
name|Widget
name|sender
init|=
operator|(
name|Widget
operator|)
name|event
operator|.
name|getSource
argument_list|()
decl_stmt|;
if|if
condition|(
name|send
operator|==
name|sender
condition|)
block|{
name|onSend
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cancel
operator|==
name|sender
condition|)
block|{
name|saveStateOnUnload
operator|=
literal|false
expr_stmt|;
name|goChange
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|initMessage (final Panel body)
specifier|private
name|void
name|initMessage
parameter_list|(
specifier|final
name|Panel
name|body
parameter_list|)
block|{
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
name|headingCoverMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|VerticalPanel
name|mwrap
init|=
operator|new
name|VerticalPanel
argument_list|()
decl_stmt|;
name|mwrap
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-CoverMessage"
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|mwrap
argument_list|)
expr_stmt|;
name|message
operator|=
operator|new
name|NpTextArea
argument_list|()
expr_stmt|;
name|message
operator|.
name|setCharacterWidth
argument_list|(
literal|60
argument_list|)
expr_stmt|;
name|message
operator|.
name|setVisibleLines
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|DOM
operator|.
name|setElementPropertyBoolean
argument_list|(
name|message
operator|.
name|getElement
argument_list|()
argument_list|,
literal|"spellcheck"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|mwrap
operator|.
name|add
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|initApprovals (final PatchSetPublishDetail r, final Panel body)
specifier|private
name|void
name|initApprovals
parameter_list|(
specifier|final
name|PatchSetPublishDetail
name|r
parameter_list|,
specifier|final
name|Panel
name|body
parameter_list|)
block|{
for|for
control|(
specifier|final
name|ApprovalType
name|ct
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
if|if
condition|(
name|r
operator|.
name|isAllowed
argument_list|(
name|ct
operator|.
name|getCategory
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
name|initApprovalType
argument_list|(
name|r
argument_list|,
name|body
argument_list|,
name|ct
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|initApprovalType (final PatchSetPublishDetail r, final Panel body, final ApprovalType ct)
specifier|private
name|void
name|initApprovalType
parameter_list|(
specifier|final
name|PatchSetPublishDetail
name|r
parameter_list|,
specifier|final
name|Panel
name|body
parameter_list|,
specifier|final
name|ApprovalType
name|ct
parameter_list|)
block|{
name|body
operator|.
name|add
argument_list|(
operator|new
name|SmallHeading
argument_list|(
name|ct
operator|.
name|getCategory
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|":"
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"gerrit-ApprovalCategoryList"
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|ApprovalCategoryValue
argument_list|>
name|lst
init|=
operator|new
name|ArrayList
argument_list|<
name|ApprovalCategoryValue
argument_list|>
argument_list|(
name|ct
operator|.
name|getValues
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|reverse
argument_list|(
name|lst
argument_list|)
expr_stmt|;
specifier|final
name|ApprovalCategory
operator|.
name|Id
name|catId
init|=
name|ct
operator|.
name|getCategory
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|ApprovalCategoryValue
operator|.
name|Id
argument_list|>
name|allowed
init|=
name|r
operator|.
name|getAllowed
argument_list|(
name|catId
argument_list|)
decl_stmt|;
specifier|final
name|PatchSetApproval
name|prior
init|=
name|r
operator|.
name|getChangeApproval
argument_list|(
name|catId
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|ApprovalCategoryValue
name|buttonValue
range|:
name|lst
control|)
block|{
if|if
condition|(
operator|!
name|allowed
operator|.
name|contains
argument_list|(
name|buttonValue
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
specifier|final
name|ValueRadioButton
name|b
init|=
operator|new
name|ValueRadioButton
argument_list|(
name|buttonValue
argument_list|,
name|ct
operator|.
name|getCategory
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|b
operator|.
name|setText
argument_list|(
name|buttonValue
operator|.
name|format
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|lastState
operator|!=
literal|null
operator|&&
name|patchSetId
operator|.
name|equals
argument_list|(
name|lastState
operator|.
name|patchSetId
argument_list|)
operator|&&
name|lastState
operator|.
name|approvals
operator|.
name|containsKey
argument_list|(
name|buttonValue
operator|.
name|getCategoryId
argument_list|()
argument_list|)
condition|)
block|{
name|b
operator|.
name|setValue
argument_list|(
name|lastState
operator|.
name|approvals
operator|.
name|get
argument_list|(
name|buttonValue
operator|.
name|getCategoryId
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
name|buttonValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|b
operator|.
name|setValue
argument_list|(
name|prior
operator|!=
literal|null
condition|?
name|buttonValue
operator|.
name|getValue
argument_list|()
operator|==
name|prior
operator|.
name|getValue
argument_list|()
else|:
name|buttonValue
operator|.
name|getValue
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
name|approvalButtons
operator|.
name|add
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|vp
operator|.
name|add
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
name|body
operator|.
name|add
argument_list|(
name|vp
argument_list|)
expr_stmt|;
block|}
DECL|method|display (final PatchSetPublishDetail r)
specifier|private
name|void
name|display
parameter_list|(
specifier|final
name|PatchSetPublishDetail
name|r
parameter_list|)
block|{
name|setPageTitle
argument_list|(
name|Util
operator|.
name|M
operator|.
name|publishComments
argument_list|(
name|r
operator|.
name|getChange
argument_list|()
operator|.
name|getKey
argument_list|()
operator|.
name|abbreviate
argument_list|()
argument_list|,
name|patchSetId
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|descBlock
operator|.
name|display
argument_list|(
name|r
operator|.
name|getChange
argument_list|()
argument_list|,
name|r
operator|.
name|getPatchSetInfo
argument_list|()
argument_list|,
name|r
operator|.
name|getAccounts
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|r
operator|.
name|getChange
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|initApprovals
argument_list|(
name|r
argument_list|,
name|approvalPanel
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lastState
operator|!=
literal|null
operator|&&
name|patchSetId
operator|.
name|equals
argument_list|(
name|lastState
operator|.
name|patchSetId
argument_list|)
condition|)
block|{
name|message
operator|.
name|setText
argument_list|(
name|lastState
operator|.
name|message
argument_list|)
expr_stmt|;
block|}
name|draftsPanel
operator|.
name|clear
argument_list|()
expr_stmt|;
name|commentEditors
operator|=
operator|new
name|ArrayList
argument_list|<
name|CommentEditorPanel
argument_list|>
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|r
operator|.
name|getDrafts
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|draftsPanel
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
name|headingPatchComments
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Panel
name|panel
init|=
literal|null
decl_stmt|;
name|String
name|priorFile
init|=
literal|""
decl_stmt|;
for|for
control|(
specifier|final
name|PatchLineComment
name|c
range|:
name|r
operator|.
name|getDrafts
argument_list|()
control|)
block|{
specifier|final
name|Patch
operator|.
name|Key
name|patchKey
init|=
name|c
operator|.
name|getKey
argument_list|()
operator|.
name|getParentKey
argument_list|()
decl_stmt|;
specifier|final
name|String
name|fn
init|=
name|patchKey
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|fn
operator|.
name|equals
argument_list|(
name|priorFile
argument_list|)
condition|)
block|{
name|panel
operator|=
operator|new
name|FlowPanel
argument_list|()
expr_stmt|;
name|panel
operator|.
name|addStyleName
argument_list|(
literal|"gerrit-PatchComments"
argument_list|)
expr_stmt|;
name|draftsPanel
operator|.
name|add
argument_list|(
name|panel
argument_list|)
expr_stmt|;
comment|// Parent table can be null here since we are not showing any
comment|// next/previous links
name|panel
operator|.
name|add
argument_list|(
operator|new
name|PatchLink
operator|.
name|SideBySide
argument_list|(
name|fn
argument_list|,
name|patchKey
argument_list|,
literal|0
argument_list|,
literal|null
comment|/*                                                                     * parent                                                                     * table                                                                     */
argument_list|)
argument_list|)
expr_stmt|;
name|priorFile
operator|=
name|fn
expr_stmt|;
block|}
specifier|final
name|CommentEditorPanel
name|editor
init|=
operator|new
name|CommentEditorPanel
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|editor
operator|.
name|setAuthorNameText
argument_list|(
name|Util
operator|.
name|M
operator|.
name|lineHeader
argument_list|(
name|c
operator|.
name|getLine
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|editor
operator|.
name|setOpen
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|commentEditors
operator|.
name|add
argument_list|(
name|editor
argument_list|)
expr_stmt|;
name|panel
operator|.
name|add
argument_list|(
name|editor
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|onSend ()
specifier|private
name|void
name|onSend
parameter_list|()
block|{
if|if
condition|(
name|commentEditors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|onSend2
argument_list|()
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|GerritCallback
argument_list|<
name|VoidResult
argument_list|>
name|afterSaveDraft
init|=
operator|new
name|GerritCallback
argument_list|<
name|VoidResult
argument_list|>
argument_list|()
block|{
specifier|private
name|int
name|done
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
specifier|final
name|VoidResult
name|result
parameter_list|)
block|{
if|if
condition|(
operator|++
name|done
operator|==
name|commentEditors
operator|.
name|size
argument_list|()
condition|)
block|{
name|onSend2
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
for|for
control|(
specifier|final
name|CommentEditorPanel
name|p
range|:
name|commentEditors
control|)
block|{
name|p
operator|.
name|saveDraft
argument_list|(
name|afterSaveDraft
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|onSend2 ()
specifier|private
name|void
name|onSend2
parameter_list|()
block|{
specifier|final
name|Map
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|,
name|ApprovalCategoryValue
operator|.
name|Id
argument_list|>
name|values
init|=
operator|new
name|HashMap
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|,
name|ApprovalCategoryValue
operator|.
name|Id
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|ValueRadioButton
name|b
range|:
name|approvalButtons
control|)
block|{
if|if
condition|(
name|b
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|values
operator|.
name|put
argument_list|(
name|b
operator|.
name|value
operator|.
name|getCategoryId
argument_list|()
argument_list|,
name|b
operator|.
name|value
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|PatchUtil
operator|.
name|DETAIL_SVC
operator|.
name|publishComments
argument_list|(
name|patchSetId
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
name|HashSet
argument_list|<
name|ApprovalCategoryValue
operator|.
name|Id
argument_list|>
argument_list|(
name|values
operator|.
name|values
argument_list|()
argument_list|)
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
name|saveStateOnUnload
operator|=
literal|false
expr_stmt|;
name|goChange
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|goChange ()
specifier|private
name|void
name|goChange
parameter_list|()
block|{
specifier|final
name|Change
operator|.
name|Id
name|ck
init|=
name|patchSetId
operator|.
name|getParentKey
argument_list|()
decl_stmt|;
name|Gerrit
operator|.
name|display
argument_list|(
name|Link
operator|.
name|toChange
argument_list|(
name|ck
argument_list|)
argument_list|,
operator|new
name|ChangeScreen
argument_list|(
name|ck
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|ValueRadioButton
specifier|private
specifier|static
class|class
name|ValueRadioButton
extends|extends
name|RadioButton
block|{
DECL|field|value
specifier|final
name|ApprovalCategoryValue
name|value
decl_stmt|;
DECL|method|ValueRadioButton (final ApprovalCategoryValue v, final String label)
name|ValueRadioButton
parameter_list|(
specifier|final
name|ApprovalCategoryValue
name|v
parameter_list|,
specifier|final
name|String
name|label
parameter_list|)
block|{
name|super
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|value
operator|=
name|v
expr_stmt|;
block|}
block|}
DECL|class|SavedState
specifier|private
specifier|static
class|class
name|SavedState
block|{
DECL|field|patchSetId
specifier|final
name|PatchSet
operator|.
name|Id
name|patchSetId
decl_stmt|;
DECL|field|message
specifier|final
name|String
name|message
decl_stmt|;
DECL|field|approvals
specifier|final
name|Map
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|,
name|ApprovalCategoryValue
argument_list|>
name|approvals
decl_stmt|;
DECL|method|SavedState (final PublishCommentScreen p)
name|SavedState
parameter_list|(
specifier|final
name|PublishCommentScreen
name|p
parameter_list|)
block|{
name|patchSetId
operator|=
name|p
operator|.
name|patchSetId
expr_stmt|;
name|message
operator|=
name|p
operator|.
name|message
operator|.
name|getText
argument_list|()
expr_stmt|;
name|approvals
operator|=
operator|new
name|HashMap
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|,
name|ApprovalCategoryValue
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|ValueRadioButton
name|b
range|:
name|p
operator|.
name|approvalButtons
control|)
block|{
if|if
condition|(
name|b
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|approvals
operator|.
name|put
argument_list|(
name|b
operator|.
name|value
operator|.
name|getCategoryId
argument_list|()
argument_list|,
name|b
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

