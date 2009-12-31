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
name|common
operator|.
name|data
operator|.
name|ChangeDetail
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
name|PatchSet
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
name|rpc
operator|.
name|AsyncCallback
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
name|user
operator|.
name|client
operator|.
name|AutoCenterDialogBox
import|;
end_import

begin_class
DECL|class|AbandonChangeDialog
specifier|public
class|class
name|AbandonChangeDialog
extends|extends
name|AutoCenterDialogBox
block|{
DECL|field|panel
specifier|private
specifier|final
name|FlowPanel
name|panel
decl_stmt|;
DECL|field|message
specifier|private
specifier|final
name|NpTextArea
name|message
decl_stmt|;
DECL|field|sendButton
specifier|private
specifier|final
name|Button
name|sendButton
decl_stmt|;
DECL|field|cancelButton
specifier|private
specifier|final
name|Button
name|cancelButton
decl_stmt|;
DECL|field|psid
specifier|private
specifier|final
name|PatchSet
operator|.
name|Id
name|psid
decl_stmt|;
DECL|method|AbandonChangeDialog (final PatchSet.Id psi, final AsyncCallback<ChangeDetail> callback)
specifier|public
name|AbandonChangeDialog
parameter_list|(
specifier|final
name|PatchSet
operator|.
name|Id
name|psi
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|ChangeDetail
argument_list|>
name|callback
parameter_list|)
block|{
name|super
argument_list|(
comment|/* auto hide */
literal|false
argument_list|,
comment|/* modal */
literal|true
argument_list|)
expr_stmt|;
name|setGlassEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|psid
operator|=
name|psi
expr_stmt|;
name|addStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|abandonChangeDialog
argument_list|()
argument_list|)
expr_stmt|;
name|setText
argument_list|(
name|Util
operator|.
name|C
operator|.
name|abandonChangeTitle
argument_list|()
argument_list|)
expr_stmt|;
name|panel
operator|=
operator|new
name|FlowPanel
argument_list|()
expr_stmt|;
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
name|SmallHeading
argument_list|(
name|Util
operator|.
name|C
operator|.
name|headingAbandonMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|FlowPanel
name|mwrap
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|mwrap
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
name|abandonMessage
argument_list|()
argument_list|)
expr_stmt|;
name|panel
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
specifier|final
name|FlowPanel
name|buttonPanel
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|panel
operator|.
name|add
argument_list|(
name|buttonPanel
argument_list|)
expr_stmt|;
name|sendButton
operator|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonAbandonChangeSend
argument_list|()
argument_list|)
expr_stmt|;
name|sendButton
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
name|sendButton
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Util
operator|.
name|MANAGE_SVC
operator|.
name|abandonChange
argument_list|(
name|psid
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
name|ChangeDetail
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
name|ChangeDetail
name|result
parameter_list|)
block|{
if|if
condition|(
name|callback
operator|!=
literal|null
condition|)
block|{
name|callback
operator|.
name|onSuccess
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
name|hide
argument_list|()
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
name|sendButton
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
block|}
argument_list|)
expr_stmt|;
name|buttonPanel
operator|.
name|add
argument_list|(
name|sendButton
argument_list|)
expr_stmt|;
name|cancelButton
operator|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonAbandonChangeCancel
argument_list|()
argument_list|)
expr_stmt|;
name|cancelButton
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
if|if
condition|(
name|callback
operator|!=
literal|null
condition|)
block|{
name|callback
operator|.
name|onFailure
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|hide
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|buttonPanel
operator|.
name|add
argument_list|(
name|cancelButton
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
name|message
operator|.
name|setFocus
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

