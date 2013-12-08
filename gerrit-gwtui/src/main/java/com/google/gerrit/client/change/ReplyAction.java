begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
DECL|package|com.google.gerrit.client.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|change
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
name|changes
operator|.
name|ChangeInfo
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
name|changes
operator|.
name|ChangeInfo
operator|.
name|LabelInfo
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
name|changes
operator|.
name|ChangeInfo
operator|.
name|MessageInfo
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
name|ui
operator|.
name|CommentLinkProcessor
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
name|core
operator|.
name|client
operator|.
name|JsArrayString
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
name|CloseEvent
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
name|CloseHandler
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
name|Window
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
name|PopupPanel
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
name|GlobalKey
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
name|PluginSafePopupPanel
import|;
end_import

begin_class
DECL|class|ReplyAction
class|class
name|ReplyAction
block|{
DECL|field|psId
specifier|private
specifier|final
name|PatchSet
operator|.
name|Id
name|psId
decl_stmt|;
DECL|field|revision
specifier|private
specifier|final
name|String
name|revision
decl_stmt|;
DECL|field|style
specifier|private
specifier|final
name|ChangeScreen2
operator|.
name|Style
name|style
decl_stmt|;
DECL|field|clp
specifier|private
specifier|final
name|CommentLinkProcessor
name|clp
decl_stmt|;
DECL|field|replyButton
specifier|private
specifier|final
name|Widget
name|replyButton
decl_stmt|;
DECL|field|allLabels
specifier|private
name|NativeMap
argument_list|<
name|LabelInfo
argument_list|>
name|allLabels
decl_stmt|;
DECL|field|permittedLabels
specifier|private
name|NativeMap
argument_list|<
name|JsArrayString
argument_list|>
name|permittedLabels
decl_stmt|;
DECL|field|replyBox
specifier|private
name|ReplyBox
name|replyBox
decl_stmt|;
DECL|field|popup
specifier|private
name|PopupPanel
name|popup
decl_stmt|;
DECL|method|ReplyAction ( ChangeInfo info, String revision, ChangeScreen2.Style style, CommentLinkProcessor clp, Widget replyButton)
name|ReplyAction
parameter_list|(
name|ChangeInfo
name|info
parameter_list|,
name|String
name|revision
parameter_list|,
name|ChangeScreen2
operator|.
name|Style
name|style
parameter_list|,
name|CommentLinkProcessor
name|clp
parameter_list|,
name|Widget
name|replyButton
parameter_list|)
block|{
name|this
operator|.
name|psId
operator|=
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|info
operator|.
name|legacy_id
argument_list|()
argument_list|,
name|info
operator|.
name|revisions
argument_list|()
operator|.
name|get
argument_list|(
name|revision
argument_list|)
operator|.
name|_number
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|revision
operator|=
name|revision
expr_stmt|;
name|this
operator|.
name|style
operator|=
name|style
expr_stmt|;
name|this
operator|.
name|clp
operator|=
name|clp
expr_stmt|;
name|this
operator|.
name|replyButton
operator|=
name|replyButton
expr_stmt|;
name|boolean
name|current
init|=
name|revision
operator|.
name|equals
argument_list|(
name|info
operator|.
name|current_revision
argument_list|()
argument_list|)
decl_stmt|;
name|allLabels
operator|=
name|info
operator|.
name|all_labels
argument_list|()
expr_stmt|;
name|permittedLabels
operator|=
name|current
operator|&&
name|info
operator|.
name|has_permitted_labels
argument_list|()
condition|?
name|info
operator|.
name|permitted_labels
argument_list|()
else|:
name|NativeMap
operator|.
expr|<
name|JsArrayString
operator|>
name|create
argument_list|()
expr_stmt|;
block|}
DECL|method|hide ()
name|void
name|hide
parameter_list|()
block|{
if|if
condition|(
name|popup
operator|!=
literal|null
condition|)
block|{
name|popup
operator|.
name|hide
argument_list|()
expr_stmt|;
block|}
return|return;
block|}
DECL|method|onReply (MessageInfo msg)
name|void
name|onReply
parameter_list|(
name|MessageInfo
name|msg
parameter_list|)
block|{
if|if
condition|(
name|popup
operator|!=
literal|null
condition|)
block|{
name|popup
operator|.
name|hide
argument_list|()
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|replyBox
operator|==
literal|null
condition|)
block|{
name|replyBox
operator|=
operator|new
name|ReplyBox
argument_list|(
name|clp
argument_list|,
name|psId
argument_list|,
name|revision
argument_list|,
name|allLabels
argument_list|,
name|permittedLabels
argument_list|)
expr_stmt|;
name|allLabels
operator|=
literal|null
expr_stmt|;
name|permittedLabels
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
name|replyBox
operator|.
name|replyTo
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
specifier|final
name|PluginSafePopupPanel
name|p
init|=
operator|new
name|PluginSafePopupPanel
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|p
operator|.
name|setStyleName
argument_list|(
name|style
operator|.
name|replyBox
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|.
name|addAutoHidePartner
argument_list|(
name|replyButton
operator|.
name|getElement
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|.
name|addCloseHandler
argument_list|(
operator|new
name|CloseHandler
argument_list|<
name|PopupPanel
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onClose
parameter_list|(
name|CloseEvent
argument_list|<
name|PopupPanel
argument_list|>
name|event
parameter_list|)
block|{
if|if
condition|(
name|popup
operator|==
name|p
condition|)
block|{
name|popup
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|p
operator|.
name|add
argument_list|(
name|replyBox
argument_list|)
expr_stmt|;
name|Window
operator|.
name|scrollTo
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|p
operator|.
name|showRelativeTo
argument_list|(
name|replyButton
argument_list|)
expr_stmt|;
name|GlobalKey
operator|.
name|dialog
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|popup
operator|=
name|p
expr_stmt|;
block|}
block|}
end_class

end_unit

