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
name|changes
operator|.
name|CommentInput
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
name|resources
operator|.
name|client
operator|.
name|CssResource
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
name|HTMLPanel
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
name|net
operator|.
name|codemirror
operator|.
name|lib
operator|.
name|CodeMirror
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
DECL|interface|DraftBoxStyle
interface|interface
name|DraftBoxStyle
extends|extends
name|CssResource
block|{
DECL|method|edit ()
name|String
name|edit
parameter_list|()
function_decl|;
DECL|method|view ()
name|String
name|view
parameter_list|()
function_decl|;
DECL|method|newDraft ()
name|String
name|newDraft
parameter_list|()
function_decl|;
block|}
annotation|@
name|UiField
DECL|field|editArea
name|NpTextArea
name|editArea
decl_stmt|;
annotation|@
name|UiField
DECL|field|edit
name|Button
name|edit
decl_stmt|;
annotation|@
name|UiField
DECL|field|save
name|Button
name|save
decl_stmt|;
annotation|@
name|UiField
DECL|field|cancel
name|Button
name|cancel
decl_stmt|;
annotation|@
name|UiField
DECL|field|discard
name|Button
name|discard
decl_stmt|;
annotation|@
name|UiField
DECL|field|draftStyle
name|DraftBoxStyle
name|draftStyle
decl_stmt|;
DECL|field|INITIAL_COLS
specifier|private
specifier|static
specifier|final
name|int
name|INITIAL_COLS
init|=
literal|60
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
DECL|field|isNew
specifier|private
name|boolean
name|isNew
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
DECL|method|DraftBox ( SideBySide2 host, CodeMirror cm, PatchSet.Id id, CommentInfo info, CommentLinkProcessor linkProcessor, boolean isNewDraft, boolean saveOnInit)
name|DraftBox
parameter_list|(
name|SideBySide2
name|host
parameter_list|,
name|CodeMirror
name|cm
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
parameter_list|,
name|boolean
name|isNewDraft
parameter_list|,
name|boolean
name|saveOnInit
parameter_list|)
block|{
name|super
argument_list|(
name|host
argument_list|,
name|cm
argument_list|,
name|uiBinder
argument_list|,
name|id
argument_list|,
name|info
argument_list|,
name|linkProcessor
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|isNew
operator|=
name|isNewDraft
expr_stmt|;
name|editArea
operator|.
name|setText
argument_list|(
name|info
operator|.
name|message
argument_list|()
argument_list|)
expr_stmt|;
name|editArea
operator|.
name|setCharacterWidth
argument_list|(
name|INITIAL_COLS
argument_list|)
expr_stmt|;
name|editArea
operator|.
name|setVisibleLines
argument_list|(
name|INITIAL_LINES
argument_list|)
expr_stmt|;
name|editArea
operator|.
name|setSpellCheck
argument_list|(
literal|true
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
if|if
condition|(
name|saveOnInit
condition|)
block|{
name|onSave
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isNew
condition|)
block|{
name|addStyleName
argument_list|(
name|draftStyle
operator|.
name|newDraft
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|resizePaddingWidget
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
name|MouseMoveEvent
operator|.
name|getType
argument_list|()
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
name|getText
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
name|resizePaddingWidget
argument_list|()
expr_stmt|;
block|}
DECL|method|setEdit (boolean edit)
name|void
name|setEdit
parameter_list|(
name|boolean
name|edit
parameter_list|)
block|{
if|if
condition|(
name|edit
condition|)
block|{
name|setOpen
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|removeStyleName
argument_list|(
name|draftStyle
operator|.
name|view
argument_list|()
argument_list|)
expr_stmt|;
name|addStyleName
argument_list|(
name|draftStyle
operator|.
name|edit
argument_list|()
argument_list|)
expr_stmt|;
name|editArea
operator|.
name|setText
argument_list|(
name|getOriginal
argument_list|()
operator|.
name|message
argument_list|()
argument_list|)
expr_stmt|;
name|expandText
argument_list|()
expr_stmt|;
name|editArea
operator|.
name|setReadOnly
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|editArea
operator|.
name|setFocus
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|disableClickFocusHandler
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|expandTimer
operator|.
name|cancel
argument_list|()
expr_stmt|;
name|editArea
operator|.
name|setReadOnly
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|removeStyleName
argument_list|(
name|draftStyle
operator|.
name|edit
argument_list|()
argument_list|)
expr_stmt|;
name|addStyleName
argument_list|(
name|draftStyle
operator|.
name|view
argument_list|()
argument_list|)
expr_stmt|;
name|enableClickFocusHandler
argument_list|()
expr_stmt|;
block|}
name|resizePaddingWidget
argument_list|()
expr_stmt|;
block|}
DECL|method|registerReplyToBox (PublishedBox box)
name|void
name|registerReplyToBox
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
DECL|method|removeUI ()
specifier|private
name|void
name|removeUI
parameter_list|()
block|{
name|setEdit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|expandTimer
operator|.
name|cancel
argument_list|()
expr_stmt|;
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
name|CommentInfo
name|info
init|=
name|getOriginal
argument_list|()
decl_stmt|;
name|getDiffView
argument_list|()
operator|.
name|removeDraft
argument_list|(
name|info
operator|.
name|side
argument_list|()
argument_list|,
name|info
operator|.
name|line
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|removeFromParent
argument_list|()
expr_stmt|;
name|getSelfWidget
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
name|PaddingManager
name|manager
init|=
name|getPaddingManager
argument_list|()
decl_stmt|;
name|manager
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|manager
operator|.
name|resizePaddingWidget
argument_list|()
expr_stmt|;
name|getCm
argument_list|()
operator|.
name|focus
argument_list|()
expr_stmt|;
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"contentPanelMessage"
argument_list|)
DECL|method|onDoubleClick (DoubleClickEvent e)
name|void
name|onDoubleClick
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
specifier|final
name|String
name|message
init|=
name|editArea
operator|.
name|getText
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
return|return;
block|}
name|CommentInfo
name|original
init|=
name|getOriginal
argument_list|()
decl_stmt|;
name|CommentInput
name|input
init|=
name|CommentInput
operator|.
name|create
argument_list|(
name|original
argument_list|)
decl_stmt|;
name|input
operator|.
name|setMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|setEdit
argument_list|(
literal|false
argument_list|)
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
name|updateOriginal
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|setMessageText
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|setDate
argument_list|(
name|result
operator|.
name|updated
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|isNew
condition|)
block|{
name|removeStyleName
argument_list|(
name|draftStyle
operator|.
name|newDraft
argument_list|()
argument_list|)
expr_stmt|;
name|isNew
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
if|if
condition|(
name|isNew
condition|)
block|{
name|CommentApi
operator|.
name|createDraft
argument_list|(
name|getPatchSetId
argument_list|()
argument_list|,
name|input
argument_list|,
name|cb
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|CommentApi
operator|.
name|updateDraft
argument_list|(
name|getPatchSetId
argument_list|()
argument_list|,
name|original
operator|.
name|id
argument_list|()
argument_list|,
name|input
argument_list|,
name|cb
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
name|setEdit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|getCm
argument_list|()
operator|.
name|focus
argument_list|()
expr_stmt|;
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"discard"
argument_list|)
DECL|method|onDiscard (ClickEvent e)
name|void
name|onDiscard
parameter_list|(
name|ClickEvent
name|e
parameter_list|)
block|{
if|if
condition|(
name|isNew
condition|)
block|{
name|removeUI
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
name|CommentApi
operator|.
name|deleteDraft
argument_list|(
name|getPatchSetId
argument_list|()
argument_list|,
name|getOriginal
argument_list|()
operator|.
name|id
argument_list|()
argument_list|,
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
name|removeUI
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|UiHandler
argument_list|(
literal|"editArea"
argument_list|)
DECL|method|onCtrlS (KeyDownEvent e)
name|void
name|onCtrlS
parameter_list|(
name|KeyDownEvent
name|e
parameter_list|)
block|{
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
name|onSave
argument_list|(
literal|null
argument_list|)
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
comment|/** TODO: Unused now. Re-enable this after implementing auto-save */
DECL|method|onEsc (KeyDownEvent e)
name|void
name|onEsc
parameter_list|(
name|KeyDownEvent
name|e
parameter_list|)
block|{
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
condition|)
block|{
if|if
condition|(
name|isNew
condition|)
block|{
name|removeUI
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|onCancel
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|e
operator|.
name|preventDefault
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

