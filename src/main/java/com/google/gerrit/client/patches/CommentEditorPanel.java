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
DECL|package|com.google.gerrit.client.patches
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|patches
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
name|KeyPressEvent
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
name|KeyPressHandler
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
name|FlexTable
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
name|Focusable
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
name|InlineLabel
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
name|sql
operator|.
name|Timestamp
import|;
end_import

begin_class
DECL|class|CommentEditorPanel
class|class
name|CommentEditorPanel
extends|extends
name|Composite
implements|implements
name|ClickHandler
block|{
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
DECL|field|comment
specifier|private
name|PatchLineComment
name|comment
decl_stmt|;
DECL|field|renderedPanel
specifier|private
specifier|final
name|LineCommentPanel
name|renderedPanel
decl_stmt|;
DECL|field|text
specifier|private
specifier|final
name|NpTextArea
name|text
decl_stmt|;
DECL|field|edit
specifier|private
specifier|final
name|Button
name|edit
decl_stmt|;
DECL|field|save
specifier|private
specifier|final
name|Button
name|save
decl_stmt|;
DECL|field|cancel
specifier|private
specifier|final
name|Button
name|cancel
decl_stmt|;
DECL|field|discard
specifier|private
specifier|final
name|Button
name|discard
decl_stmt|;
DECL|field|savedAt
specifier|private
specifier|final
name|Label
name|savedAt
decl_stmt|;
DECL|field|expandTimer
specifier|private
specifier|final
name|Timer
name|expandTimer
decl_stmt|;
DECL|method|CommentEditorPanel (final PatchLineComment plc)
name|CommentEditorPanel
parameter_list|(
specifier|final
name|PatchLineComment
name|plc
parameter_list|)
block|{
name|comment
operator|=
name|plc
expr_stmt|;
specifier|final
name|FlowPanel
name|body
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|initWidget
argument_list|(
name|body
argument_list|)
expr_stmt|;
name|setStyleName
argument_list|(
literal|"gerrit-CommentEditor"
argument_list|)
expr_stmt|;
name|renderedPanel
operator|=
operator|new
name|LineCommentPanel
argument_list|(
name|comment
argument_list|)
block|{
block|{
name|sinkEvents
parameter_list|(
name|Event
operator|.
name|ONDBLCLICK
parameter_list|)
constructor_decl|;
block|}
annotation|@
name|Override
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
name|edit
argument_list|()
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
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|renderedPanel
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
name|text
operator|=
operator|new
name|NpTextArea
argument_list|()
expr_stmt|;
name|text
operator|.
name|setText
argument_list|(
name|comment
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|text
operator|.
name|setCharacterWidth
argument_list|(
name|INITIAL_COLS
argument_list|)
expr_stmt|;
name|text
operator|.
name|setVisibleLines
argument_list|(
name|INITIAL_LINES
argument_list|)
expr_stmt|;
name|DOM
operator|.
name|setElementPropertyBoolean
argument_list|(
name|text
operator|.
name|getElement
argument_list|()
argument_list|,
literal|"spellcheck"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|text
operator|.
name|addKeyPressHandler
argument_list|(
operator|new
name|KeyPressHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onKeyPress
parameter_list|(
specifier|final
name|KeyPressEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|event
operator|.
name|getCharCode
argument_list|()
operator|==
name|KeyCodes
operator|.
name|KEY_ESCAPE
operator|&&
operator|!
name|event
operator|.
name|isAnyModifierKeyDown
argument_list|()
condition|)
block|{
name|event
operator|.
name|preventDefault
argument_list|()
expr_stmt|;
if|if
condition|(
name|isNew
argument_list|()
condition|)
block|{
name|onDiscard
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|render
argument_list|()
expr_stmt|;
block|}
return|return;
block|}
if|if
condition|(
operator|(
name|event
operator|.
name|isControlKeyDown
argument_list|()
operator|||
name|event
operator|.
name|isMetaKeyDown
argument_list|()
operator|)
operator|&&
operator|!
name|event
operator|.
name|isAltKeyDown
argument_list|()
operator|&&
operator|!
name|event
operator|.
name|isShiftKeyDown
argument_list|()
condition|)
block|{
switch|switch
condition|(
name|event
operator|.
name|getCharCode
argument_list|()
condition|)
block|{
case|case
literal|'s'
case|:
name|event
operator|.
name|preventDefault
argument_list|()
expr_stmt|;
name|onSave
argument_list|()
expr_stmt|;
return|return;
case|case
literal|'d'
case|:
name|event
operator|.
name|preventDefault
argument_list|()
expr_stmt|;
if|if
condition|(
name|isNew
argument_list|()
condition|)
block|{
name|onDiscard
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Window
operator|.
name|confirm
argument_list|(
name|PatchUtil
operator|.
name|C
operator|.
name|confirmDiscard
argument_list|()
argument_list|)
condition|)
block|{
name|onDiscard
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|text
operator|.
name|setFocus
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
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
block|}
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|text
argument_list|)
expr_stmt|;
specifier|final
name|FlowPanel
name|buttons
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|buttons
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
name|buttons
argument_list|)
expr_stmt|;
name|edit
operator|=
operator|new
name|Button
argument_list|()
expr_stmt|;
name|edit
operator|.
name|setText
argument_list|(
name|PatchUtil
operator|.
name|C
operator|.
name|buttonEdit
argument_list|()
argument_list|)
expr_stmt|;
name|edit
operator|.
name|addClickHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|buttons
operator|.
name|add
argument_list|(
name|edit
argument_list|)
expr_stmt|;
name|save
operator|=
operator|new
name|Button
argument_list|()
expr_stmt|;
name|save
operator|.
name|setText
argument_list|(
name|PatchUtil
operator|.
name|C
operator|.
name|buttonSave
argument_list|()
argument_list|)
expr_stmt|;
name|save
operator|.
name|addClickHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
operator|new
name|TextSaveButtonListener
argument_list|(
name|text
argument_list|,
name|save
argument_list|)
expr_stmt|;
name|save
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|buttons
operator|.
name|add
argument_list|(
name|save
argument_list|)
expr_stmt|;
name|cancel
operator|=
operator|new
name|Button
argument_list|()
expr_stmt|;
name|cancel
operator|.
name|setText
argument_list|(
name|PatchUtil
operator|.
name|C
operator|.
name|buttonCancel
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
name|buttons
operator|.
name|add
argument_list|(
name|cancel
argument_list|)
expr_stmt|;
name|discard
operator|=
operator|new
name|Button
argument_list|()
expr_stmt|;
name|discard
operator|.
name|setText
argument_list|(
name|PatchUtil
operator|.
name|C
operator|.
name|buttonDiscard
argument_list|()
argument_list|)
expr_stmt|;
name|discard
operator|.
name|addClickHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|buttons
operator|.
name|add
argument_list|(
name|discard
argument_list|)
expr_stmt|;
name|savedAt
operator|=
operator|new
name|InlineLabel
argument_list|()
expr_stmt|;
name|savedAt
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-CommentEditor-SavedDraft"
argument_list|)
expr_stmt|;
name|buttons
operator|.
name|add
argument_list|(
name|savedAt
argument_list|)
expr_stmt|;
if|if
condition|(
name|isNew
argument_list|()
condition|)
block|{
name|edit
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|render
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|expandText ()
specifier|private
name|void
name|expandText
parameter_list|()
block|{
specifier|final
name|double
name|cols
init|=
name|text
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
specifier|final
name|String
name|line
range|:
name|text
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
name|text
operator|.
name|getVisibleLines
argument_list|()
operator|!=
name|rows
condition|)
block|{
name|text
operator|.
name|setVisibleLines
argument_list|(
name|rows
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|edit ()
specifier|private
name|void
name|edit
parameter_list|()
block|{
name|text
operator|.
name|setText
argument_list|(
name|comment
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|expandText
argument_list|()
expr_stmt|;
name|stateEdit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|text
operator|.
name|setFocus
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|render ()
specifier|private
name|void
name|render
parameter_list|()
block|{
specifier|final
name|Timestamp
name|on
init|=
name|comment
operator|.
name|getWrittenOn
argument_list|()
decl_stmt|;
name|savedAt
operator|.
name|setText
argument_list|(
name|PatchUtil
operator|.
name|M
operator|.
name|draftSaved
argument_list|(
operator|new
name|java
operator|.
name|util
operator|.
name|Date
argument_list|(
name|on
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|renderedPanel
operator|.
name|update
argument_list|(
name|comment
argument_list|)
expr_stmt|;
name|stateEdit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|stateEdit (final boolean inEdit)
specifier|private
name|void
name|stateEdit
parameter_list|(
specifier|final
name|boolean
name|inEdit
parameter_list|)
block|{
name|expandTimer
operator|.
name|cancel
argument_list|()
expr_stmt|;
name|renderedPanel
operator|.
name|setVisible
argument_list|(
operator|!
name|inEdit
argument_list|)
expr_stmt|;
name|edit
operator|.
name|setVisible
argument_list|(
operator|!
name|inEdit
argument_list|)
expr_stmt|;
name|text
operator|.
name|setVisible
argument_list|(
name|inEdit
argument_list|)
expr_stmt|;
name|save
operator|.
name|setVisible
argument_list|(
name|inEdit
argument_list|)
expr_stmt|;
name|cancel
operator|.
name|setVisible
argument_list|(
name|inEdit
operator|&&
operator|!
name|isNew
argument_list|()
argument_list|)
expr_stmt|;
name|discard
operator|.
name|setVisible
argument_list|(
name|inEdit
argument_list|)
expr_stmt|;
block|}
DECL|method|setFocus (final boolean take)
name|void
name|setFocus
parameter_list|(
specifier|final
name|boolean
name|take
parameter_list|)
block|{
if|if
condition|(
name|text
operator|.
name|isVisible
argument_list|()
condition|)
block|{
name|text
operator|.
name|setFocus
argument_list|(
name|take
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|take
condition|)
block|{
name|edit
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|isNew ()
name|boolean
name|isNew
parameter_list|()
block|{
return|return
name|comment
operator|.
name|getKey
argument_list|()
operator|.
name|get
argument_list|()
operator|==
literal|null
return|;
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
name|sender
operator|==
name|edit
condition|)
block|{
name|edit
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sender
operator|==
name|save
condition|)
block|{
name|onSave
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sender
operator|==
name|cancel
condition|)
block|{
name|render
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sender
operator|==
name|discard
condition|)
block|{
name|onDiscard
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|onSave ()
specifier|private
name|void
name|onSave
parameter_list|()
block|{
name|expandTimer
operator|.
name|cancel
argument_list|()
expr_stmt|;
specifier|final
name|String
name|txt
init|=
name|text
operator|.
name|getText
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|txt
argument_list|)
condition|)
block|{
return|return;
block|}
name|comment
operator|.
name|setMessage
argument_list|(
name|txt
argument_list|)
expr_stmt|;
name|text
operator|.
name|setReadOnly
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|save
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|cancel
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|discard
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|PatchUtil
operator|.
name|DETAIL_SVC
operator|.
name|saveDraft
argument_list|(
name|comment
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|PatchLineComment
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
specifier|final
name|PatchLineComment
name|result
parameter_list|)
block|{
if|if
condition|(
name|isNew
argument_list|()
condition|)
block|{
name|notifyDraftDelta
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|comment
operator|=
name|result
expr_stmt|;
name|text
operator|.
name|setReadOnly
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|cancel
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|discard
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|render
argument_list|()
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
name|text
operator|.
name|setReadOnly
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|save
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|cancel
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|discard
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
DECL|method|notifyDraftDelta (final int delta)
specifier|private
name|void
name|notifyDraftDelta
parameter_list|(
specifier|final
name|int
name|delta
parameter_list|)
block|{
name|Widget
name|p
init|=
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
name|p
operator|=
name|p
operator|.
name|getParent
argument_list|()
expr_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
operator|(
operator|(
name|AbstractPatchContentTable
operator|)
name|p
operator|)
operator|.
name|notifyDraftDelta
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|onDiscard ()
specifier|private
name|void
name|onDiscard
parameter_list|()
block|{
name|expandTimer
operator|.
name|cancel
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
return|return;
block|}
specifier|final
name|boolean
name|saveOn
init|=
name|save
operator|.
name|isEnabled
argument_list|()
decl_stmt|;
name|text
operator|.
name|setReadOnly
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|save
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|cancel
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|discard
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|PatchUtil
operator|.
name|DETAIL_SVC
operator|.
name|deleteDraft
argument_list|(
name|comment
operator|.
name|getKey
argument_list|()
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
name|notifyDraftDelta
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|removeUI
argument_list|()
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
name|text
operator|.
name|setReadOnly
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|save
operator|.
name|setEnabled
argument_list|(
name|saveOn
argument_list|)
expr_stmt|;
name|cancel
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|discard
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
DECL|method|removeUI ()
specifier|private
name|void
name|removeUI
parameter_list|()
block|{
specifier|final
name|FlexTable
name|table
init|=
operator|(
name|FlexTable
operator|)
name|getParent
argument_list|()
decl_stmt|;
specifier|final
name|int
name|nRows
init|=
name|table
operator|.
name|getRowCount
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|row
init|=
literal|0
init|;
name|row
operator|<
name|nRows
condition|;
name|row
operator|++
control|)
block|{
specifier|final
name|int
name|nCells
init|=
name|table
operator|.
name|getCellCount
argument_list|(
name|row
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|cell
init|=
literal|0
init|;
name|cell
operator|<
name|nCells
condition|;
name|cell
operator|++
control|)
block|{
if|if
condition|(
name|table
operator|.
name|getWidget
argument_list|(
name|row
argument_list|,
name|cell
argument_list|)
operator|==
name|this
condition|)
block|{
name|AbstractPatchContentTable
operator|.
name|destroyEditor
argument_list|(
name|table
argument_list|,
name|row
argument_list|,
name|cell
argument_list|)
expr_stmt|;
name|Widget
name|p
init|=
name|table
decl_stmt|;
while|while
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|p
operator|instanceof
name|Focusable
condition|)
block|{
operator|(
operator|(
name|Focusable
operator|)
name|p
operator|)
operator|.
name|setFocus
argument_list|(
literal|true
argument_list|)
expr_stmt|;
break|break;
block|}
name|p
operator|=
name|p
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
return|return;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

