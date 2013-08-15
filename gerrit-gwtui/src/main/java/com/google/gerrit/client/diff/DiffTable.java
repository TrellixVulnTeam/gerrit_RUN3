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
name|diff
operator|.
name|SideBySide2
operator|.
name|DisplaySide
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

begin_comment
comment|/**  * A table with one row and two columns to hold the two CodeMirrors displaying  * the files to be diffed.  */
end_comment

begin_class
DECL|class|DiffTable
class|class
name|DiffTable
extends|extends
name|Composite
block|{
DECL|interface|Binder
interface|interface
name|Binder
extends|extends
name|UiBinder
argument_list|<
name|HTMLPanel
argument_list|,
name|DiffTable
argument_list|>
block|{}
DECL|field|uiBinder
specifier|private
specifier|static
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
DECL|interface|DiffTableStyle
interface|interface
name|DiffTableStyle
extends|extends
name|CssResource
block|{
DECL|method|intralineBg ()
name|String
name|intralineBg
parameter_list|()
function_decl|;
DECL|method|diff ()
name|String
name|diff
parameter_list|()
function_decl|;
DECL|method|padding ()
name|String
name|padding
parameter_list|()
function_decl|;
DECL|method|activeLine ()
name|String
name|activeLine
parameter_list|()
function_decl|;
DECL|method|activeLineBg ()
name|String
name|activeLineBg
parameter_list|()
function_decl|;
DECL|method|hideNumber ()
name|String
name|hideNumber
parameter_list|()
function_decl|;
DECL|method|range ()
name|String
name|range
parameter_list|()
function_decl|;
DECL|method|rangeHighlight ()
name|String
name|rangeHighlight
parameter_list|()
function_decl|;
block|}
annotation|@
name|UiField
DECL|field|cmA
name|Element
name|cmA
decl_stmt|;
annotation|@
name|UiField
DECL|field|cmB
name|Element
name|cmB
decl_stmt|;
annotation|@
name|UiField
DECL|field|sidePanel
name|SidePanel
name|sidePanel
decl_stmt|;
annotation|@
name|UiField
DECL|field|patchsetNavRow
name|Element
name|patchsetNavRow
decl_stmt|;
annotation|@
name|UiField
DECL|field|patchsetNavCellA
name|Element
name|patchsetNavCellA
decl_stmt|;
annotation|@
name|UiField
DECL|field|patchsetNavCellB
name|Element
name|patchsetNavCellB
decl_stmt|;
annotation|@
name|UiField
argument_list|(
name|provided
operator|=
literal|true
argument_list|)
DECL|field|patchSelectBoxA
name|PatchSelectBox2
name|patchSelectBoxA
decl_stmt|;
annotation|@
name|UiField
argument_list|(
name|provided
operator|=
literal|true
argument_list|)
DECL|field|patchSelectBoxB
name|PatchSelectBox2
name|patchSelectBoxB
decl_stmt|;
annotation|@
name|UiField
DECL|field|fileCommentRow
name|Element
name|fileCommentRow
decl_stmt|;
annotation|@
name|UiField
DECL|field|fileCommentCellA
name|Element
name|fileCommentCellA
decl_stmt|;
annotation|@
name|UiField
DECL|field|fileCommentCellB
name|Element
name|fileCommentCellB
decl_stmt|;
annotation|@
name|UiField
argument_list|(
name|provided
operator|=
literal|true
argument_list|)
DECL|field|fileCommentPanelA
name|FileCommentPanel
name|fileCommentPanelA
decl_stmt|;
annotation|@
name|UiField
argument_list|(
name|provided
operator|=
literal|true
argument_list|)
DECL|field|fileCommentPanelB
name|FileCommentPanel
name|fileCommentPanelB
decl_stmt|;
annotation|@
name|UiField
DECL|field|style
specifier|static
name|DiffTableStyle
name|style
decl_stmt|;
DECL|field|host
specifier|private
name|SideBySide2
name|host
decl_stmt|;
DECL|method|DiffTable (SideBySide2 host, String path)
name|DiffTable
parameter_list|(
name|SideBySide2
name|host
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|patchSelectBoxA
operator|=
operator|new
name|PatchSelectBox2
argument_list|(
name|this
argument_list|,
name|DisplaySide
operator|.
name|A
argument_list|)
expr_stmt|;
name|patchSelectBoxB
operator|=
operator|new
name|PatchSelectBox2
argument_list|(
name|this
argument_list|,
name|DisplaySide
operator|.
name|B
argument_list|)
expr_stmt|;
name|fileCommentPanelA
operator|=
operator|new
name|FileCommentPanel
argument_list|(
name|host
argument_list|,
name|this
argument_list|,
name|path
argument_list|,
name|DisplaySide
operator|.
name|A
argument_list|)
expr_stmt|;
name|fileCommentPanelB
operator|=
operator|new
name|FileCommentPanel
argument_list|(
name|host
argument_list|,
name|this
argument_list|,
name|path
argument_list|,
name|DisplaySide
operator|.
name|B
argument_list|)
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
name|this
operator|.
name|host
operator|=
name|host
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
name|updateFileCommentVisibility
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|updateFileCommentVisibility (boolean forceHide)
name|void
name|updateFileCommentVisibility
parameter_list|(
name|boolean
name|forceHide
parameter_list|)
block|{
name|UIObject
operator|.
name|setVisible
argument_list|(
name|patchsetNavRow
argument_list|,
operator|!
name|forceHide
argument_list|)
expr_stmt|;
if|if
condition|(
name|forceHide
operator|||
operator|(
name|fileCommentPanelA
operator|.
name|getBoxCount
argument_list|()
operator|==
literal|0
operator|&&
name|fileCommentPanelB
operator|.
name|getBoxCount
argument_list|()
operator|==
literal|0
operator|)
condition|)
block|{
name|UIObject
operator|.
name|setVisible
argument_list|(
name|fileCommentRow
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|UIObject
operator|.
name|setVisible
argument_list|(
name|fileCommentRow
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|host
operator|.
name|resizeCodeMirror
argument_list|()
expr_stmt|;
block|}
DECL|method|getPanelFromSide (DisplaySide side)
specifier|private
name|FileCommentPanel
name|getPanelFromSide
parameter_list|(
name|DisplaySide
name|side
parameter_list|)
block|{
return|return
name|side
operator|==
name|DisplaySide
operator|.
name|A
condition|?
name|fileCommentPanelA
else|:
name|fileCommentPanelB
return|;
block|}
DECL|method|createOrEditFileComment (DisplaySide side)
name|void
name|createOrEditFileComment
parameter_list|(
name|DisplaySide
name|side
parameter_list|)
block|{
name|getPanelFromSide
argument_list|(
name|side
argument_list|)
operator|.
name|createOrEditFileComment
argument_list|()
expr_stmt|;
name|updateFileCommentVisibility
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|addFileCommentBox (CommentBox box)
name|void
name|addFileCommentBox
parameter_list|(
name|CommentBox
name|box
parameter_list|)
block|{
name|getPanelFromSide
argument_list|(
name|box
operator|.
name|getSide
argument_list|()
argument_list|)
operator|.
name|addFileComment
argument_list|(
name|box
argument_list|)
expr_stmt|;
block|}
DECL|method|onRemoveDraftBox (DraftBox box)
name|void
name|onRemoveDraftBox
parameter_list|(
name|DraftBox
name|box
parameter_list|)
block|{
name|getPanelFromSide
argument_list|(
name|box
operator|.
name|getSide
argument_list|()
argument_list|)
operator|.
name|onRemoveDraftBox
argument_list|(
name|box
argument_list|)
expr_stmt|;
block|}
DECL|method|getHeaderHeight ()
name|int
name|getHeaderHeight
parameter_list|()
block|{
return|return
name|fileCommentRow
operator|.
name|getOffsetHeight
argument_list|()
operator|+
name|patchSelectBoxA
operator|.
name|getOffsetHeight
argument_list|()
return|;
block|}
DECL|method|add (Widget widget)
name|void
name|add
parameter_list|(
name|Widget
name|widget
parameter_list|)
block|{
operator|(
operator|(
name|HTMLPanel
operator|)
name|getWidget
argument_list|()
operator|)
operator|.
name|add
argument_list|(
name|widget
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

