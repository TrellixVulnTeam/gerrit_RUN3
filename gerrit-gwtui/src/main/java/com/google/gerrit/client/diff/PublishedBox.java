begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|//Copyright (C) 2013 The Android Open Source Project
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
DECL|package|com.google.gerrit.client.diff
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|diff
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
name|CommentInfo
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
name|GWT
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
name|uibinder
operator|.
name|client
operator|.
name|UiBinder
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
name|uibinder
operator|.
name|client
operator|.
name|UiHandler
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
name|HTMLPanel
import|;
end_import

begin_comment
comment|/** An HtmlPanel for displaying a published comment */
end_comment

begin_class
DECL|class|PublishedBox
class|class
name|PublishedBox
extends|extends
name|CommentBox
block|{
DECL|interface|Binder
interface|interface
name|Binder
extends|extends
name|UiBinder
argument_list|<
name|HTMLPanel
argument_list|,
name|PublishedBox
argument_list|>
block|{}
DECL|field|uiBinder
specifier|private
specifier|static
name|UiBinder
argument_list|<
name|HTMLPanel
argument_list|,
name|CommentBox
argument_list|>
name|uiBinder
init|=
name|GWT
operator|.
name|create
argument_list|(
name|Binder
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|replyBox
specifier|private
name|DraftBox
name|replyBox
decl_stmt|;
DECL|method|PublishedBox (CodeMirrorDemo host, PatchSet.Id id, CommentInfo info, CommentLinkProcessor linkProcessor)
name|PublishedBox
parameter_list|(
name|CodeMirrorDemo
name|host
parameter_list|,
name|PatchSet
operator|.
name|Id
name|id
parameter_list|,
name|CommentInfo
name|info
parameter_list|,
name|CommentLinkProcessor
name|linkProcessor
parameter_list|)
block|{
name|super
argument_list|(
name|host
argument_list|,
name|uiBinder
argument_list|,
name|id
argument_list|,
name|info
argument_list|,
name|linkProcessor
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|registerReplyBox (DraftBox box)
name|void
name|registerReplyBox
parameter_list|(
name|DraftBox
name|box
parameter_list|)
block|{
name|replyBox
operator|=
name|box
expr_stmt|;
name|box
operator|.
name|registerReplyToBox
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|unregisterReplyBox ()
name|void
name|unregisterReplyBox
parameter_list|()
block|{
name|replyBox
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|openReplyBox ()
specifier|private
name|void
name|openReplyBox
parameter_list|()
block|{
name|replyBox
operator|.
name|setOpen
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|replyBox
operator|.
name|setEdit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"reply"
argument_list|)
DECL|method|onReply (ClickEvent e)
name|void
name|onReply
parameter_list|(
name|ClickEvent
name|e
parameter_list|)
block|{
if|if
condition|(
name|replyBox
operator|==
literal|null
condition|)
block|{
name|DraftBox
name|box
init|=
name|getDiffView
argument_list|()
operator|.
name|addReplyBox
argument_list|(
name|getOriginal
argument_list|()
argument_list|,
literal|""
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|registerReplyBox
argument_list|(
name|box
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|openReplyBox
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"replyDone"
argument_list|)
DECL|method|onReplyDone (ClickEvent e)
name|void
name|onReplyDone
parameter_list|(
name|ClickEvent
name|e
parameter_list|)
block|{
if|if
condition|(
name|replyBox
operator|==
literal|null
condition|)
block|{
name|DraftBox
name|box
init|=
name|getDiffView
argument_list|()
operator|.
name|addReplyBox
argument_list|(
name|getOriginal
argument_list|()
argument_list|,
literal|"Done"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|registerReplyBox
argument_list|(
name|box
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|openReplyBox
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

