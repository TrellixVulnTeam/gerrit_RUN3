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
name|SimplePanel
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

begin_import
import|import
name|net
operator|.
name|codemirror
operator|.
name|lib
operator|.
name|Configuration
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
name|LineWidget
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
name|TextMarker
operator|.
name|FromTo
import|;
end_import

begin_comment
comment|/**  * LineWidget attached to a CodeMirror container.  *  * When a comment is placed on a line a CommentWidget is created.  */
end_comment

begin_class
DECL|class|CommentGroup
specifier|abstract
class|class
name|CommentGroup
extends|extends
name|Composite
block|{
DECL|field|side
specifier|final
name|DisplaySide
name|side
decl_stmt|;
DECL|field|line
specifier|final
name|int
name|line
decl_stmt|;
DECL|field|manager
specifier|private
specifier|final
name|CommentManager
name|manager
decl_stmt|;
DECL|field|cm
specifier|private
specifier|final
name|CodeMirror
name|cm
decl_stmt|;
DECL|field|comments
specifier|private
specifier|final
name|FlowPanel
name|comments
decl_stmt|;
DECL|field|lineWidget
specifier|private
name|LineWidget
name|lineWidget
decl_stmt|;
DECL|field|resizeTimer
specifier|private
name|Timer
name|resizeTimer
decl_stmt|;
DECL|method|CommentGroup (CommentManager manager, CodeMirror cm, DisplaySide side, int line)
name|CommentGroup
parameter_list|(
name|CommentManager
name|manager
parameter_list|,
name|CodeMirror
name|cm
parameter_list|,
name|DisplaySide
name|side
parameter_list|,
name|int
name|line
parameter_list|)
block|{
name|this
operator|.
name|manager
operator|=
name|manager
expr_stmt|;
name|this
operator|.
name|cm
operator|=
name|cm
expr_stmt|;
name|this
operator|.
name|side
operator|=
name|side
expr_stmt|;
name|this
operator|.
name|line
operator|=
name|line
expr_stmt|;
name|comments
operator|=
operator|new
name|FlowPanel
argument_list|()
expr_stmt|;
name|comments
operator|.
name|setStyleName
argument_list|(
name|Resources
operator|.
name|I
operator|.
name|style
argument_list|()
operator|.
name|commentWidgets
argument_list|()
argument_list|)
expr_stmt|;
name|comments
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|initWidget
argument_list|(
operator|new
name|SimplePanel
argument_list|(
name|comments
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getCommentManager ()
name|CommentManager
name|getCommentManager
parameter_list|()
block|{
return|return
name|manager
return|;
block|}
DECL|method|getCm ()
name|CodeMirror
name|getCm
parameter_list|()
block|{
return|return
name|cm
return|;
block|}
DECL|method|getLine ()
name|int
name|getLine
parameter_list|()
block|{
return|return
name|line
return|;
block|}
DECL|method|getSide ()
name|DisplaySide
name|getSide
parameter_list|()
block|{
return|return
name|side
return|;
block|}
DECL|method|add (PublishedBox box)
name|void
name|add
parameter_list|(
name|PublishedBox
name|box
parameter_list|)
block|{
name|comments
operator|.
name|add
argument_list|(
name|box
argument_list|)
expr_stmt|;
name|comments
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|add (DraftBox box)
name|void
name|add
parameter_list|(
name|DraftBox
name|box
parameter_list|)
block|{
name|PublishedBox
name|p
init|=
name|box
operator|.
name|getReplyToBox
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
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
name|getBoxCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|p
operator|==
name|getCommentBox
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|comments
operator|.
name|insert
argument_list|(
name|box
argument_list|,
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
name|comments
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|resize
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
block|}
name|comments
operator|.
name|add
argument_list|(
name|box
argument_list|)
expr_stmt|;
name|comments
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|resize
argument_list|()
expr_stmt|;
block|}
DECL|method|getCommentBox (int i)
name|CommentBox
name|getCommentBox
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
operator|(
name|CommentBox
operator|)
name|comments
operator|.
name|getWidget
argument_list|(
name|i
argument_list|)
return|;
block|}
DECL|method|getBoxCount ()
name|int
name|getBoxCount
parameter_list|()
block|{
return|return
name|comments
operator|.
name|getWidgetCount
argument_list|()
return|;
block|}
DECL|method|openCloseLast ()
name|void
name|openCloseLast
parameter_list|()
block|{
if|if
condition|(
literal|0
operator|<
name|getBoxCount
argument_list|()
condition|)
block|{
name|CommentBox
name|box
init|=
name|getCommentBox
argument_list|(
name|getBoxCount
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|box
operator|.
name|setOpen
argument_list|(
operator|!
name|box
operator|.
name|isOpen
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|openCloseAll ()
name|void
name|openCloseAll
parameter_list|()
block|{
name|boolean
name|open
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|getBoxCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|getCommentBox
argument_list|(
name|i
argument_list|)
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|open
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
name|setOpenAll
argument_list|(
name|open
argument_list|)
expr_stmt|;
block|}
DECL|method|setOpenAll (boolean open)
name|void
name|setOpenAll
parameter_list|(
name|boolean
name|open
parameter_list|)
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
name|getBoxCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|getCommentBox
argument_list|(
name|i
argument_list|)
operator|.
name|setOpen
argument_list|(
name|open
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|remove (DraftBox box)
name|void
name|remove
parameter_list|(
name|DraftBox
name|box
parameter_list|)
block|{
name|comments
operator|.
name|remove
argument_list|(
name|box
argument_list|)
expr_stmt|;
name|comments
operator|.
name|setVisible
argument_list|(
literal|0
operator|<
name|getBoxCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|detach ()
name|void
name|detach
parameter_list|()
block|{
if|if
condition|(
name|lineWidget
operator|!=
literal|null
condition|)
block|{
name|lineWidget
operator|.
name|clear
argument_list|()
expr_stmt|;
name|lineWidget
operator|=
literal|null
expr_stmt|;
name|updateSelection
argument_list|()
expr_stmt|;
block|}
name|manager
operator|.
name|clearLine
argument_list|(
name|side
argument_list|,
name|line
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|removeFromParent
argument_list|()
expr_stmt|;
block|}
DECL|method|attach (DiffTable parent)
name|void
name|attach
parameter_list|(
name|DiffTable
name|parent
parameter_list|)
block|{
name|parent
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|lineWidget
operator|=
name|cm
operator|.
name|addLineWidget
argument_list|(
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|line
operator|-
literal|1
argument_list|)
argument_list|,
name|getElement
argument_list|()
argument_list|,
name|Configuration
operator|.
name|create
argument_list|()
operator|.
name|set
argument_list|(
literal|"coverGutter"
argument_list|,
literal|true
argument_list|)
operator|.
name|set
argument_list|(
literal|"noHScroll"
argument_list|,
literal|true
argument_list|)
operator|.
name|set
argument_list|(
literal|"above"
argument_list|,
name|line
operator|<=
literal|0
argument_list|)
operator|.
name|set
argument_list|(
literal|"insertAt"
argument_list|,
literal|0
argument_list|)
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
if|if
condition|(
name|resizeTimer
operator|!=
literal|null
condition|)
block|{
name|resizeTimer
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|updateSelection ()
name|void
name|updateSelection
parameter_list|()
block|{
if|if
condition|(
name|cm
operator|.
name|somethingSelected
argument_list|()
condition|)
block|{
name|FromTo
name|r
init|=
name|cm
operator|.
name|getSelectedRange
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|to
argument_list|()
operator|.
name|line
argument_list|()
operator|>=
name|line
condition|)
block|{
name|cm
operator|.
name|setSelection
argument_list|(
name|r
operator|.
name|from
argument_list|()
argument_list|,
name|r
operator|.
name|to
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|canComputeHeight ()
name|boolean
name|canComputeHeight
parameter_list|()
block|{
return|return
operator|!
name|comments
operator|.
name|isVisible
argument_list|()
operator|||
name|comments
operator|.
name|getOffsetHeight
argument_list|()
operator|>
literal|0
return|;
block|}
DECL|method|getLineWidget ()
name|LineWidget
name|getLineWidget
parameter_list|()
block|{
return|return
name|lineWidget
return|;
block|}
DECL|method|setLineWidget (LineWidget widget)
name|void
name|setLineWidget
parameter_list|(
name|LineWidget
name|widget
parameter_list|)
block|{
name|lineWidget
operator|=
name|widget
expr_stmt|;
block|}
DECL|method|getResizeTimer ()
name|Timer
name|getResizeTimer
parameter_list|()
block|{
return|return
name|resizeTimer
return|;
block|}
DECL|method|setResizeTimer (Timer timer)
name|void
name|setResizeTimer
parameter_list|(
name|Timer
name|timer
parameter_list|)
block|{
name|resizeTimer
operator|=
name|timer
expr_stmt|;
block|}
DECL|method|getComments ()
name|FlowPanel
name|getComments
parameter_list|()
block|{
return|return
name|comments
return|;
block|}
DECL|method|getManager ()
name|CommentManager
name|getManager
parameter_list|()
block|{
return|return
name|manager
return|;
block|}
DECL|method|init (DiffTable parent)
specifier|abstract
name|void
name|init
parameter_list|(
name|DiffTable
name|parent
parameter_list|)
function_decl|;
DECL|method|handleRedraw ()
specifier|abstract
name|void
name|handleRedraw
parameter_list|()
function_decl|;
DECL|method|resize ()
specifier|abstract
name|void
name|resize
parameter_list|()
function_decl|;
block|}
end_class

end_unit

