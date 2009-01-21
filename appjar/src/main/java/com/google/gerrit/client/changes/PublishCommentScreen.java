begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright 2009 Google Inc.
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
name|LineCommentPanel
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
name|ChangeApproval
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
name|ClickListener
block|{
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
name|TextArea
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
DECL|field|displayedOnce
specifier|private
name|boolean
name|displayedOnce
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
name|super
argument_list|(
name|Util
operator|.
name|M
operator|.
name|publishComments
argument_list|(
name|psi
operator|.
name|getParentKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|psi
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|addStyleName
argument_list|(
literal|"gerrit-PublishCommentsScreen"
argument_list|)
expr_stmt|;
name|patchSetId
operator|=
name|psi
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getScreenCacheToken ()
specifier|public
name|Object
name|getScreenCacheToken
parameter_list|()
block|{
return|return
operator|new
name|ScreenCacheToken
argument_list|(
name|patchSetId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|onLoad ()
specifier|public
name|void
name|onLoad
parameter_list|()
block|{
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
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
name|addClickListener
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
name|addClickListener
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
name|super
operator|.
name|onLoad
argument_list|()
expr_stmt|;
name|message
operator|.
name|setFocus
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|send
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
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
name|GerritCallback
argument_list|<
name|PatchSetPublishDetail
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
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
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|onClick (final Widget sender)
specifier|public
name|void
name|onClick
parameter_list|(
specifier|final
name|Widget
name|sender
parameter_list|)
block|{
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
name|Gerrit
operator|.
name|uncache
argument_list|(
name|this
argument_list|)
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
name|TextArea
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
name|Common
operator|.
name|getGerritConfig
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
name|ChangeApproval
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
specifier|final
name|StringBuilder
name|m
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|buttonValue
operator|.
name|getValue
argument_list|()
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
name|buttonValue
operator|.
name|getValue
argument_list|()
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
name|buttonValue
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|m
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|m
operator|.
name|append
argument_list|(
name|buttonValue
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|setText
argument_list|(
name|m
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|setChecked
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
operator|!
name|displayedOnce
condition|)
block|{
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
block|}
name|draftsPanel
operator|.
name|clear
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
argument_list|)
argument_list|)
expr_stmt|;
name|priorFile
operator|=
name|fn
expr_stmt|;
block|}
name|Label
name|m
decl_stmt|;
name|m
operator|=
operator|new
name|DoubleClickLinkLabel
argument_list|(
name|patchKey
argument_list|)
expr_stmt|;
name|m
operator|.
name|setText
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
name|m
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-LineHeader"
argument_list|)
expr_stmt|;
name|panel
operator|.
name|add
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|m
operator|=
operator|new
name|DoubleClickLinkLabel
argument_list|(
name|patchKey
argument_list|)
expr_stmt|;
name|DOM
operator|.
name|setInnerHTML
argument_list|(
name|m
operator|.
name|getElement
argument_list|()
argument_list|,
name|LineCommentPanel
operator|.
name|toHTML
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
name|m
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-PatchLineComment"
argument_list|)
expr_stmt|;
name|panel
operator|.
name|add
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
block|}
name|displayedOnce
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|onSend ()
specifier|private
name|void
name|onSend
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
name|isChecked
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
DECL|class|DoubleClickLinkLabel
specifier|private
specifier|static
class|class
name|DoubleClickLinkLabel
extends|extends
name|Label
block|{
DECL|field|patchKey
specifier|private
specifier|final
name|Patch
operator|.
name|Key
name|patchKey
decl_stmt|;
DECL|method|DoubleClickLinkLabel (final Patch.Key p)
name|DoubleClickLinkLabel
parameter_list|(
specifier|final
name|Patch
operator|.
name|Key
name|p
parameter_list|)
block|{
name|patchKey
operator|=
name|p
expr_stmt|;
name|sinkEvents
argument_list|(
name|Event
operator|.
name|ONDBLCLICK
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onBrowserEvent (final Event event)
specifier|public
name|void
name|onBrowserEvent
parameter_list|(
specifier|final
name|Event
name|event
parameter_list|)
block|{
switch|switch
condition|(
name|DOM
operator|.
name|eventGetType
argument_list|(
name|event
argument_list|)
condition|)
block|{
case|case
name|Event
operator|.
name|ONDBLCLICK
case|:
name|History
operator|.
name|newItem
argument_list|(
name|Link
operator|.
name|toPatchSideBySide
argument_list|(
name|patchKey
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
block|}
name|super
operator|.
name|onBrowserEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ScreenCacheToken
specifier|private
specifier|static
specifier|final
class|class
name|ScreenCacheToken
block|{
DECL|field|patchSetId
specifier|private
specifier|final
name|PatchSet
operator|.
name|Id
name|patchSetId
decl_stmt|;
DECL|method|ScreenCacheToken (final PatchSet.Id psi)
name|ScreenCacheToken
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
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|ScreenCacheToken
condition|)
block|{
specifier|final
name|ScreenCacheToken
name|c
init|=
operator|(
name|ScreenCacheToken
operator|)
name|obj
decl_stmt|;
return|return
name|patchSetId
operator|.
name|equals
argument_list|(
name|c
operator|.
name|patchSetId
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|patchSetId
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

