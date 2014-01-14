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
name|FormatUtil
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
name|CommentApi
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
name|core
operator|.
name|client
operator|.
name|JavaScriptObject
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
name|Scheduler
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
name|Scheduler
operator|.
name|ScheduledCommand
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
name|dom
operator|.
name|client
operator|.
name|Element
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
name|DoubleClickEvent
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
name|DoubleClickHandler
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
name|KeyCodes
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
name|KeyDownEvent
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
name|MouseMoveEvent
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
name|MouseMoveHandler
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
name|MouseUpEvent
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
name|MouseUpHandler
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
name|UiField
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
name|Timer
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
name|HTML
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
name|UIObject
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
name|safehtml
operator|.
name|client
operator|.
name|SafeHtmlBuilder
import|;
end_import

begin_comment
comment|/** An HtmlPanel for displaying and editing a draft */
end_comment

begin_class
DECL|class|DraftBox
class|class
name|DraftBox
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
name|DraftBox
argument_list|>
block|{}
DECL|field|uiBinder
specifier|private
specifier|static
specifier|final
name|Binder
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
DECL|field|INITIAL_LINES
specifier|private
specifier|static
specifier|final
name|int
name|INITIAL_LINES
init|=
literal|5
decl_stmt|;
DECL|field|MAX_LINES
specifier|private
specifier|static
specifier|final
name|int
name|MAX_LINES
init|=
literal|30
decl_stmt|;
DECL|field|linkProcessor
specifier|private
specifier|final
name|CommentLinkProcessor
name|linkProcessor
decl_stmt|;
DECL|field|psId
specifier|private
specifier|final
name|PatchSet
operator|.
name|Id
name|psId
decl_stmt|;
DECL|field|comment
specifier|private
name|CommentInfo
name|comment
decl_stmt|;
DECL|field|replyToBox
specifier|private
name|PublishedBox
name|replyToBox
decl_stmt|;
DECL|field|expandTimer
specifier|private
name|Timer
name|expandTimer
decl_stmt|;
DECL|field|resizeTimer
specifier|private
name|Timer
name|resizeTimer
decl_stmt|;
DECL|field|editAreaHeight
specifier|private
name|int
name|editAreaHeight
decl_stmt|;
DECL|field|autoClosed
specifier|private
name|boolean
name|autoClosed
decl_stmt|;
DECL|field|pendingGroup
specifier|private
name|CallbackGroup
name|pendingGroup
decl_stmt|;
DECL|field|header
annotation|@
name|UiField
name|Widget
name|header
decl_stmt|;
DECL|field|summary
annotation|@
name|UiField
name|Element
name|summary
decl_stmt|;
DECL|field|date
annotation|@
name|UiField
name|Element
name|date
decl_stmt|;
DECL|field|p_view
annotation|@
name|UiField
name|Element
name|p_view
decl_stmt|;
DECL|field|message
annotation|@
name|UiField
name|HTML
name|message
decl_stmt|;
DECL|field|edit
annotation|@
name|UiField
name|Button
name|edit
decl_stmt|;
DECL|field|discard1
annotation|@
name|UiField
name|Button
name|discard1
decl_stmt|;
DECL|field|p_edit
annotation|@
name|UiField
name|Element
name|p_edit
decl_stmt|;
DECL|field|editArea
annotation|@
name|UiField
name|NpTextArea
name|editArea
decl_stmt|;
DECL|field|save
annotation|@
name|UiField
name|Button
name|save
decl_stmt|;
DECL|field|cancel
annotation|@
name|UiField
name|Button
name|cancel
decl_stmt|;
DECL|field|discard2
annotation|@
name|UiField
name|Button
name|discard2
decl_stmt|;
DECL|method|DraftBox ( CommentGroup group, CommentLinkProcessor clp, PatchSet.Id id, CommentInfo info)
name|DraftBox
parameter_list|(
name|CommentGroup
name|group
parameter_list|,
name|CommentLinkProcessor
name|clp
parameter_list|,
name|PatchSet
operator|.
name|Id
name|id
parameter_list|,
name|CommentInfo
name|info
parameter_list|)
block|{
name|super
argument_list|(
name|group
argument_list|,
name|info
operator|.
name|range
argument_list|()
argument_list|)
expr_stmt|;
name|linkProcessor
operator|=
name|clp
expr_stmt|;
name|psId
operator|=
name|id
expr_stmt|;
name|initWidget
argument_list|(
name|uiBinder
operator|.
name|createAndBindUi
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|expandTimer
operator|=
operator|new
name|Timer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|expandText
argument_list|()
expr_stmt|;
block|}
block|}
expr_stmt|;
name|set
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|header
operator|.
name|addDomHandler
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
if|if
condition|(
operator|!
name|isEdit
argument_list|()
condition|)
block|{
if|if
condition|(
name|autoClosed
operator|&&
operator|!
name|isOpen
argument_list|()
condition|)
block|{
name|setOpen
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setEdit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setOpen
argument_list|(
operator|!
name|isOpen
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|,
name|ClickEvent
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|addDomHandler
argument_list|(
operator|new
name|DoubleClickHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onDoubleClick
parameter_list|(
name|DoubleClickEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|isEdit
argument_list|()
condition|)
block|{
name|editArea
operator|.
name|setFocus
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setOpen
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setEdit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|,
name|DoubleClickEvent
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|initResizeHandler
argument_list|()
expr_stmt|;
block|}
DECL|method|set (CommentInfo info)
specifier|private
name|void
name|set
parameter_list|(
name|CommentInfo
name|info
parameter_list|)
block|{
name|autoClosed
operator|=
name|info
operator|.
name|message
argument_list|()
operator|!=
literal|null
operator|&&
name|info
operator|.
name|message
argument_list|()
operator|.
name|length
argument_list|()
operator|<
literal|70
expr_stmt|;
name|date
operator|.
name|setInnerText
argument_list|(
name|FormatUtil
operator|.
name|shortFormatDayTime
argument_list|(
name|info
operator|.
name|updated
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|info
operator|.
name|message
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|String
name|msg
init|=
name|info
operator|.
name|message
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
name|summary
operator|.
name|setInnerText
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|message
operator|.
name|setHTML
argument_list|(
name|linkProcessor
operator|.
name|apply
argument_list|(
operator|new
name|SafeHtmlBuilder
argument_list|()
operator|.
name|append
argument_list|(
name|msg
argument_list|)
operator|.
name|wikify
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|comment
operator|=
name|info
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCommentInfo ()
name|CommentInfo
name|getCommentInfo
parameter_list|()
block|{
return|return
name|comment
return|;
block|}
annotation|@
name|Override
DECL|method|isOpen ()
name|boolean
name|isOpen
parameter_list|()
block|{
return|return
name|UIObject
operator|.
name|isVisible
argument_list|(
name|p_view
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setOpen (boolean open)
name|void
name|setOpen
parameter_list|(
name|boolean
name|open
parameter_list|)
block|{
name|UIObject
operator|.
name|setVisible
argument_list|(
name|summary
argument_list|,
operator|!
name|open
argument_list|)
expr_stmt|;
name|UIObject
operator|.
name|setVisible
argument_list|(
name|p_view
argument_list|,
name|open
argument_list|)
expr_stmt|;
name|super
operator|.
name|setOpen
argument_list|(
name|open
argument_list|)
expr_stmt|;
block|}
DECL|method|expandText ()
specifier|private
name|void
name|expandText
parameter_list|()
block|{
name|double
name|cols
init|=
name|editArea
operator|.
name|getCharacterWidth
argument_list|()
decl_stmt|;
name|int
name|rows
init|=
literal|2
decl_stmt|;
for|for
control|(
name|String
name|line
range|:
name|editArea
operator|.
name|getValue
argument_list|()
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
control|)
block|{
name|rows
operator|+=
name|Math
operator|.
name|ceil
argument_list|(
operator|(
literal|1.0
operator|+
name|line
operator|.
name|length
argument_list|()
operator|)
operator|/
name|cols
argument_list|)
expr_stmt|;
block|}
name|rows
operator|=
name|Math
operator|.
name|max
argument_list|(
name|INITIAL_LINES
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|rows
argument_list|,
name|MAX_LINES
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|editArea
operator|.
name|getVisibleLines
argument_list|()
operator|!=
name|rows
condition|)
block|{
name|editArea
operator|.
name|setVisibleLines
argument_list|(
name|rows
argument_list|)
expr_stmt|;
block|}
name|editAreaHeight
operator|=
name|editArea
operator|.
name|getOffsetHeight
argument_list|()
expr_stmt|;
name|getCommentGroup
argument_list|()
operator|.
name|resize
argument_list|()
expr_stmt|;
block|}
DECL|method|isEdit ()
name|boolean
name|isEdit
parameter_list|()
block|{
return|return
name|UIObject
operator|.
name|isVisible
argument_list|(
name|p_edit
argument_list|)
return|;
block|}
DECL|method|setEdit (boolean edit)
name|void
name|setEdit
parameter_list|(
name|boolean
name|edit
parameter_list|)
block|{
name|UIObject
operator|.
name|setVisible
argument_list|(
name|summary
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|UIObject
operator|.
name|setVisible
argument_list|(
name|p_view
argument_list|,
operator|!
name|edit
argument_list|)
expr_stmt|;
name|UIObject
operator|.
name|setVisible
argument_list|(
name|p_edit
argument_list|,
name|edit
argument_list|)
expr_stmt|;
name|setRangeHighlight
argument_list|(
name|edit
argument_list|)
expr_stmt|;
if|if
condition|(
name|edit
condition|)
block|{
name|String
name|msg
init|=
name|comment
operator|.
name|message
argument_list|()
operator|!=
literal|null
condition|?
name|comment
operator|.
name|message
argument_list|()
operator|.
name|trim
argument_list|()
else|:
literal|""
decl_stmt|;
name|editArea
operator|.
name|setValue
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|cancel
operator|.
name|setVisible
argument_list|(
operator|!
name|isNew
argument_list|()
argument_list|)
expr_stmt|;
name|expandText
argument_list|()
expr_stmt|;
name|editAreaHeight
operator|=
name|editArea
operator|.
name|getOffsetHeight
argument_list|()
expr_stmt|;
specifier|final
name|int
name|len
init|=
name|msg
operator|.
name|length
argument_list|()
decl_stmt|;
name|Scheduler
operator|.
name|get
argument_list|()
operator|.
name|scheduleDeferred
argument_list|(
operator|new
name|ScheduledCommand
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|()
block|{
name|editArea
operator|.
name|setFocus
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|editArea
operator|.
name|setCursorPos
argument_list|(
name|len
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|expandTimer
operator|.
name|cancel
argument_list|()
expr_stmt|;
name|resizeTimer
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
name|getCommentManager
argument_list|()
operator|.
name|setUnsaved
argument_list|(
name|this
argument_list|,
name|edit
argument_list|)
expr_stmt|;
name|getCommentGroup
argument_list|()
operator|.
name|resize
argument_list|()
expr_stmt|;
block|}
DECL|method|getReplyToBox ()
name|PublishedBox
name|getReplyToBox
parameter_list|()
block|{
return|return
name|replyToBox
return|;
block|}
DECL|method|setReplyToBox (PublishedBox box)
name|void
name|setReplyToBox
parameter_list|(
name|PublishedBox
name|box
parameter_list|)
block|{
name|replyToBox
operator|=
name|box
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
name|expandTimer
operator|.
name|cancel
argument_list|()
expr_stmt|;
name|resizeTimer
operator|.
name|cancel
argument_list|()
expr_stmt|;
name|super
operator|.
name|onUnload
argument_list|()
expr_stmt|;
block|}
DECL|method|removeUI ()
specifier|private
name|void
name|removeUI
parameter_list|()
block|{
if|if
condition|(
name|replyToBox
operator|!=
literal|null
condition|)
block|{
name|replyToBox
operator|.
name|unregisterReplyBox
argument_list|()
expr_stmt|;
block|}
name|getCommentManager
argument_list|()
operator|.
name|setUnsaved
argument_list|(
name|this
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|setRangeHighlight
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|clearRange
argument_list|()
expr_stmt|;
name|getMark
argument_list|()
operator|.
name|remove
argument_list|()
expr_stmt|;
name|getCommentGroup
argument_list|()
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|getCm
argument_list|()
operator|.
name|focus
argument_list|()
expr_stmt|;
block|}
DECL|method|restoreSelection ()
specifier|private
name|void
name|restoreSelection
parameter_list|()
block|{
if|if
condition|(
name|getFromTo
argument_list|()
operator|!=
literal|null
operator|&&
name|comment
operator|.
name|in_reply_to
argument_list|()
operator|==
literal|null
condition|)
block|{
name|getCm
argument_list|()
operator|.
name|setSelection
argument_list|(
name|getFromTo
argument_list|()
operator|.
name|getFrom
argument_list|()
argument_list|,
name|getFromTo
argument_list|()
operator|.
name|getTo
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"message"
argument_list|)
DECL|method|onMessageClick (ClickEvent e)
name|void
name|onMessageClick
parameter_list|(
name|ClickEvent
name|e
parameter_list|)
block|{
name|e
operator|.
name|stopPropagation
argument_list|()
expr_stmt|;
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"message"
argument_list|)
DECL|method|onMessageDoubleClick (DoubleClickEvent e)
name|void
name|onMessageDoubleClick
parameter_list|(
name|DoubleClickEvent
name|e
parameter_list|)
block|{
name|setEdit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"edit"
argument_list|)
DECL|method|onEdit (ClickEvent e)
name|void
name|onEdit
parameter_list|(
name|ClickEvent
name|e
parameter_list|)
block|{
name|e
operator|.
name|stopPropagation
argument_list|()
expr_stmt|;
name|setEdit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"save"
argument_list|)
DECL|method|onSave (ClickEvent e)
name|void
name|onSave
parameter_list|(
name|ClickEvent
name|e
parameter_list|)
block|{
name|e
operator|.
name|stopPropagation
argument_list|()
expr_stmt|;
name|CallbackGroup
name|group
init|=
operator|new
name|CallbackGroup
argument_list|()
decl_stmt|;
name|save
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|group
operator|.
name|done
argument_list|()
expr_stmt|;
block|}
DECL|method|save (CallbackGroup group)
name|void
name|save
parameter_list|(
name|CallbackGroup
name|group
parameter_list|)
block|{
if|if
condition|(
name|pendingGroup
operator|!=
literal|null
condition|)
block|{
name|pendingGroup
operator|.
name|addListener
argument_list|(
name|group
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|message
init|=
name|editArea
operator|.
name|getValue
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|CommentInfo
name|input
init|=
name|CommentInfo
operator|.
name|copy
argument_list|(
name|comment
argument_list|)
decl_stmt|;
name|input
operator|.
name|message
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|enableEdit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|pendingGroup
operator|=
name|group
expr_stmt|;
name|GerritCallback
argument_list|<
name|CommentInfo
argument_list|>
name|cb
init|=
operator|new
name|GerritCallback
argument_list|<
name|CommentInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|CommentInfo
name|result
parameter_list|)
block|{
name|enableEdit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|pendingGroup
operator|=
literal|null
expr_stmt|;
name|set
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|setEdit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|autoClosed
condition|)
block|{
name|setOpen
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|getCommentManager
argument_list|()
operator|.
name|setUnsaved
argument_list|(
name|DraftBox
operator|.
name|this
argument_list|,
literal|false
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
name|e
parameter_list|)
block|{
name|enableEdit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|pendingGroup
operator|=
literal|null
expr_stmt|;
name|super
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
if|if
condition|(
name|input
operator|.
name|id
argument_list|()
operator|==
literal|null
condition|)
block|{
name|CommentApi
operator|.
name|createDraft
argument_list|(
name|psId
argument_list|,
name|input
argument_list|,
name|group
operator|.
name|add
argument_list|(
name|cb
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|CommentApi
operator|.
name|updateDraft
argument_list|(
name|psId
argument_list|,
name|input
operator|.
name|id
argument_list|()
argument_list|,
name|input
argument_list|,
name|group
operator|.
name|add
argument_list|(
name|cb
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|getCm
argument_list|()
operator|.
name|focus
argument_list|()
expr_stmt|;
block|}
DECL|method|enableEdit (boolean on)
specifier|private
name|void
name|enableEdit
parameter_list|(
name|boolean
name|on
parameter_list|)
block|{
name|editArea
operator|.
name|setEnabled
argument_list|(
name|on
argument_list|)
expr_stmt|;
name|save
operator|.
name|setEnabled
argument_list|(
name|on
argument_list|)
expr_stmt|;
name|cancel
operator|.
name|setEnabled
argument_list|(
name|on
argument_list|)
expr_stmt|;
name|discard2
operator|.
name|setEnabled
argument_list|(
name|on
argument_list|)
expr_stmt|;
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"cancel"
argument_list|)
DECL|method|onCancel (ClickEvent e)
name|void
name|onCancel
parameter_list|(
name|ClickEvent
name|e
parameter_list|)
block|{
name|e
operator|.
name|stopPropagation
argument_list|()
expr_stmt|;
if|if
condition|(
name|isNew
argument_list|()
operator|&&
operator|!
name|isDirty
argument_list|()
condition|)
block|{
name|removeUI
argument_list|()
expr_stmt|;
name|restoreSelection
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|setEdit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|autoClosed
condition|)
block|{
name|setOpen
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|getCm
argument_list|()
operator|.
name|focus
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|UiHandler
argument_list|(
block|{
literal|"discard1"
block|,
literal|"discard2"
block|}
argument_list|)
DECL|method|onDiscard (ClickEvent e)
name|void
name|onDiscard
parameter_list|(
name|ClickEvent
name|e
parameter_list|)
block|{
name|e
operator|.
name|stopPropagation
argument_list|()
expr_stmt|;
if|if
condition|(
name|isNew
argument_list|()
condition|)
block|{
name|removeUI
argument_list|()
expr_stmt|;
name|restoreSelection
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|setEdit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|pendingGroup
operator|=
operator|new
name|CallbackGroup
argument_list|()
expr_stmt|;
name|CommentApi
operator|.
name|deleteDraft
argument_list|(
name|psId
argument_list|,
name|comment
operator|.
name|id
argument_list|()
argument_list|,
name|pendingGroup
operator|.
name|addFinal
argument_list|(
operator|new
name|GerritCallback
argument_list|<
name|JavaScriptObject
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|JavaScriptObject
name|result
parameter_list|)
block|{
name|pendingGroup
operator|=
literal|null
expr_stmt|;
name|removeUI
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"editArea"
argument_list|)
DECL|method|onKeyDown (KeyDownEvent e)
name|void
name|onKeyDown
parameter_list|(
name|KeyDownEvent
name|e
parameter_list|)
block|{
name|resizeTimer
operator|.
name|cancel
argument_list|()
expr_stmt|;
if|if
condition|(
operator|(
name|e
operator|.
name|isControlKeyDown
argument_list|()
operator|||
name|e
operator|.
name|isMetaKeyDown
argument_list|()
operator|)
operator|&&
operator|!
name|e
operator|.
name|isAltKeyDown
argument_list|()
operator|&&
operator|!
name|e
operator|.
name|isShiftKeyDown
argument_list|()
condition|)
block|{
switch|switch
condition|(
name|e
operator|.
name|getNativeKeyCode
argument_list|()
condition|)
block|{
case|case
literal|'s'
case|:
case|case
literal|'S'
case|:
name|e
operator|.
name|preventDefault
argument_list|()
expr_stmt|;
name|CallbackGroup
name|group
init|=
operator|new
name|CallbackGroup
argument_list|()
decl_stmt|;
name|save
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|group
operator|.
name|done
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
elseif|else
if|if
condition|(
name|e
operator|.
name|getNativeKeyCode
argument_list|()
operator|==
name|KeyCodes
operator|.
name|KEY_ESCAPE
operator|&&
operator|!
name|isDirty
argument_list|()
condition|)
block|{
if|if
condition|(
name|isNew
argument_list|()
condition|)
block|{
name|removeUI
argument_list|()
expr_stmt|;
name|restoreSelection
argument_list|()
expr_stmt|;
return|return;
block|}
else|else
block|{
name|setEdit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|autoClosed
condition|)
block|{
name|setOpen
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|getCm
argument_list|()
operator|.
name|focus
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
name|expandTimer
operator|.
name|schedule
argument_list|(
literal|250
argument_list|)
expr_stmt|;
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"editArea"
argument_list|)
DECL|method|onBlur (BlurEvent e)
name|void
name|onBlur
parameter_list|(
name|BlurEvent
name|e
parameter_list|)
block|{
name|resizeTimer
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
DECL|method|initResizeHandler ()
specifier|private
name|void
name|initResizeHandler
parameter_list|()
block|{
name|resizeTimer
operator|=
operator|new
name|Timer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|getCommentGroup
argument_list|()
operator|.
name|resize
argument_list|()
expr_stmt|;
block|}
block|}
expr_stmt|;
name|addDomHandler
argument_list|(
operator|new
name|MouseMoveHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onMouseMove
parameter_list|(
name|MouseMoveEvent
name|event
parameter_list|)
block|{
name|int
name|h
init|=
name|editArea
operator|.
name|getOffsetHeight
argument_list|()
decl_stmt|;
if|if
condition|(
name|isEdit
argument_list|()
operator|&&
name|h
operator|!=
name|editAreaHeight
condition|)
block|{
name|getCommentGroup
argument_list|()
operator|.
name|resize
argument_list|()
expr_stmt|;
name|resizeTimer
operator|.
name|scheduleRepeating
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|editAreaHeight
operator|=
name|h
expr_stmt|;
block|}
block|}
block|}
argument_list|,
name|MouseMoveEvent
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|addDomHandler
argument_list|(
operator|new
name|MouseUpHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onMouseUp
parameter_list|(
name|MouseUpEvent
name|event
parameter_list|)
block|{
name|resizeTimer
operator|.
name|cancel
argument_list|()
expr_stmt|;
name|getCommentGroup
argument_list|()
operator|.
name|resize
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
name|MouseUpEvent
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|isNew ()
specifier|private
name|boolean
name|isNew
parameter_list|()
block|{
return|return
name|comment
operator|.
name|id
argument_list|()
operator|==
literal|null
return|;
block|}
DECL|method|isDirty ()
specifier|private
name|boolean
name|isDirty
parameter_list|()
block|{
name|String
name|msg
init|=
name|editArea
operator|.
name|getValue
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|isNew
argument_list|()
condition|)
block|{
return|return
name|msg
operator|.
name|length
argument_list|()
operator|>
literal|0
return|;
block|}
return|return
operator|!
name|msg
operator|.
name|equals
argument_list|(
name|comment
operator|.
name|message
argument_list|()
operator|!=
literal|null
condition|?
name|comment
operator|.
name|message
argument_list|()
operator|.
name|trim
argument_list|()
else|:
literal|""
argument_list|)
return|;
block|}
block|}
end_class

end_unit

