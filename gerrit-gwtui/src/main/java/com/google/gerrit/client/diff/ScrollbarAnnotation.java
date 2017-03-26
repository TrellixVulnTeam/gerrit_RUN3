begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
name|dom
operator|.
name|client
operator|.
name|Style
operator|.
name|Unit
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
name|Widget
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
name|CodeMirror
operator|.
name|RegisteredHandler
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
name|Pos
import|;
end_import

begin_comment
comment|/** Displayed on the vertical scrollbar to place a chunk or comment. */
end_comment

begin_class
DECL|class|ScrollbarAnnotation
class|class
name|ScrollbarAnnotation
extends|extends
name|Widget
implements|implements
name|ClickHandler
block|{
DECL|field|cm
specifier|private
specifier|final
name|CodeMirror
name|cm
decl_stmt|;
DECL|field|cmB
specifier|private
name|CodeMirror
name|cmB
decl_stmt|;
DECL|field|refresh
specifier|private
name|RegisteredHandler
name|refresh
decl_stmt|;
DECL|field|from
specifier|private
name|Pos
name|from
decl_stmt|;
DECL|field|to
specifier|private
name|Pos
name|to
decl_stmt|;
DECL|field|scale
specifier|private
name|double
name|scale
decl_stmt|;
DECL|method|ScrollbarAnnotation (CodeMirror cm)
name|ScrollbarAnnotation
parameter_list|(
name|CodeMirror
name|cm
parameter_list|)
block|{
name|setElement
argument_list|(
operator|(
name|Element
operator|)
name|DOM
operator|.
name|createDiv
argument_list|()
argument_list|)
expr_stmt|;
name|getElement
argument_list|()
operator|.
name|setAttribute
argument_list|(
literal|"not-content"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|addDomHandler
argument_list|(
name|this
argument_list|,
name|ClickEvent
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|cm
operator|=
name|cm
expr_stmt|;
name|this
operator|.
name|cmB
operator|=
name|cm
expr_stmt|;
block|}
DECL|method|remove ()
name|void
name|remove
parameter_list|()
block|{
name|removeFromParent
argument_list|()
expr_stmt|;
block|}
DECL|method|at (int line)
name|void
name|at
parameter_list|(
name|int
name|line
parameter_list|)
block|{
name|at
argument_list|(
name|Pos
operator|.
name|create
argument_list|(
name|line
argument_list|)
argument_list|,
name|Pos
operator|.
name|create
argument_list|(
name|line
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|at (Pos from, Pos to)
name|void
name|at
parameter_list|(
name|Pos
name|from
parameter_list|,
name|Pos
name|to
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|to
operator|=
name|to
expr_stmt|;
block|}
DECL|method|renderOn (CodeMirror cm)
name|void
name|renderOn
parameter_list|(
name|CodeMirror
name|cm
parameter_list|)
block|{
name|this
operator|.
name|cmB
operator|=
name|cm
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
name|cmB
operator|.
name|getWrapperElement
argument_list|()
operator|.
name|appendChild
argument_list|(
name|getElement
argument_list|()
argument_list|)
expr_stmt|;
name|refresh
operator|=
name|cmB
operator|.
name|on
argument_list|(
literal|"refresh"
argument_list|,
parameter_list|()
lambda|->
block|{
if|if
condition|(
name|updateScale
argument_list|()
condition|)
block|{
name|updatePosition
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|updateScale
argument_list|()
expr_stmt|;
name|updatePosition
argument_list|()
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
name|cmB
operator|.
name|off
argument_list|(
literal|"refresh"
argument_list|,
name|refresh
argument_list|)
expr_stmt|;
block|}
DECL|method|updateScale ()
specifier|private
name|boolean
name|updateScale
parameter_list|()
block|{
name|double
name|old
init|=
name|scale
decl_stmt|;
name|double
name|docHeight
init|=
name|cmB
operator|.
name|getWrapperElement
argument_list|()
operator|.
name|getClientHeight
argument_list|()
decl_stmt|;
name|double
name|lineHeight
init|=
name|cmB
operator|.
name|heightAtLine
argument_list|(
name|cmB
operator|.
name|lastLine
argument_list|()
operator|+
literal|1
argument_list|,
literal|"local"
argument_list|)
decl_stmt|;
name|scale
operator|=
operator|(
name|docHeight
operator|-
name|cmB
operator|.
name|barHeight
argument_list|()
operator|)
operator|/
name|lineHeight
expr_stmt|;
return|return
name|old
operator|!=
name|scale
return|;
block|}
DECL|method|updatePosition ()
specifier|private
name|void
name|updatePosition
parameter_list|()
block|{
name|double
name|top
init|=
name|cm
operator|.
name|charCoords
argument_list|(
name|from
argument_list|,
literal|"local"
argument_list|)
operator|.
name|top
argument_list|()
operator|*
name|scale
decl_stmt|;
name|double
name|bottom
init|=
name|cm
operator|.
name|charCoords
argument_list|(
name|to
argument_list|,
literal|"local"
argument_list|)
operator|.
name|bottom
argument_list|()
operator|*
name|scale
decl_stmt|;
name|Element
name|e
init|=
name|getElement
argument_list|()
decl_stmt|;
name|e
operator|.
name|getStyle
argument_list|()
operator|.
name|setTop
argument_list|(
name|top
argument_list|,
name|Unit
operator|.
name|PX
argument_list|)
expr_stmt|;
name|e
operator|.
name|getStyle
argument_list|()
operator|.
name|setWidth
argument_list|(
name|Math
operator|.
name|max
argument_list|(
literal|2
argument_list|,
name|cm
operator|.
name|barWidth
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|,
name|Unit
operator|.
name|PX
argument_list|)
expr_stmt|;
name|e
operator|.
name|getStyle
argument_list|()
operator|.
name|setHeight
argument_list|(
name|Math
operator|.
name|max
argument_list|(
literal|3
argument_list|,
name|bottom
operator|-
name|top
argument_list|)
argument_list|,
name|Unit
operator|.
name|PX
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onClick (ClickEvent event)
specifier|public
name|void
name|onClick
parameter_list|(
name|ClickEvent
name|event
parameter_list|)
block|{
name|event
operator|.
name|stopPropagation
argument_list|()
expr_stmt|;
name|int
name|line
init|=
name|from
operator|.
name|line
argument_list|()
decl_stmt|;
name|int
name|h
init|=
name|to
operator|.
name|line
argument_list|()
operator|-
name|line
decl_stmt|;
if|if
condition|(
name|h
operator|>
literal|5
condition|)
block|{
comment|// Map click inside of the annotation to the relative position
comment|// within the region covered by the annotation.
name|double
name|s
init|=
operator|(
operator|(
name|double
operator|)
name|event
operator|.
name|getY
argument_list|()
operator|)
operator|/
name|getElement
argument_list|()
operator|.
name|getOffsetHeight
argument_list|()
decl_stmt|;
name|line
operator|+=
call|(
name|int
call|)
argument_list|(
name|s
operator|*
name|h
argument_list|)
expr_stmt|;
block|}
name|double
name|y
init|=
name|cm
operator|.
name|heightAtLine
argument_list|(
name|line
argument_list|,
literal|"local"
argument_list|)
decl_stmt|;
name|double
name|viewport
init|=
name|cm
operator|.
name|getScrollInfo
argument_list|()
operator|.
name|clientHeight
argument_list|()
decl_stmt|;
name|cm
operator|.
name|setCursor
argument_list|(
name|from
argument_list|)
expr_stmt|;
name|cm
operator|.
name|scrollTo
argument_list|(
literal|0
argument_list|,
name|y
operator|-
literal|0.5
operator|*
name|viewport
argument_list|)
expr_stmt|;
name|cm
operator|.
name|focus
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

