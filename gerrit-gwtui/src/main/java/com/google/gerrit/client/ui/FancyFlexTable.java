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
DECL|package|com.google.gerrit.client.ui
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|ui
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
name|Document
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
name|HTMLTable
operator|.
name|CellFormatter
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
name|safehtml
operator|.
name|client
operator|.
name|SafeHtml
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_class
DECL|class|FancyFlexTable
specifier|public
specifier|abstract
class|class
name|FancyFlexTable
parameter_list|<
name|RowItem
parameter_list|>
extends|extends
name|Composite
block|{
DECL|field|impl
specifier|private
specifier|static
specifier|final
name|FancyFlexTableImpl
name|impl
init|=
name|GWT
operator|.
name|create
argument_list|(
name|FancyFlexTableImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|C_ARROW
specifier|protected
specifier|static
specifier|final
name|int
name|C_ARROW
init|=
literal|0
decl_stmt|;
DECL|field|table
specifier|protected
specifier|final
name|MyFlexTable
name|table
decl_stmt|;
DECL|method|FancyFlexTable ()
specifier|protected
name|FancyFlexTable
parameter_list|()
block|{
name|table
operator|=
name|createFlexTable
argument_list|()
expr_stmt|;
name|table
operator|.
name|addStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|changeTable
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|setWidth
argument_list|(
literal|"100%"
argument_list|)
expr_stmt|;
name|initWidget
argument_list|(
name|table
argument_list|)
expr_stmt|;
name|table
operator|.
name|setText
argument_list|(
literal|0
argument_list|,
name|C_ARROW
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|table
operator|.
name|getCellFormatter
argument_list|()
operator|.
name|addStyleName
argument_list|(
literal|0
argument_list|,
name|C_ARROW
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|iconHeader
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|createFlexTable ()
specifier|protected
name|MyFlexTable
name|createFlexTable
parameter_list|()
block|{
return|return
operator|new
name|MyFlexTable
argument_list|()
return|;
block|}
DECL|method|getRowItem (final int row)
specifier|protected
name|RowItem
name|getRowItem
parameter_list|(
specifier|final
name|int
name|row
parameter_list|)
block|{
return|return
name|FancyFlexTable
operator|.
expr|<
name|RowItem
operator|>
name|getRowItem
argument_list|(
name|table
operator|.
name|getCellFormatter
argument_list|()
operator|.
name|getElement
argument_list|(
name|row
argument_list|,
literal|0
argument_list|)
argument_list|)
return|;
block|}
DECL|method|setRowItem (final int row, final RowItem item)
specifier|protected
name|void
name|setRowItem
parameter_list|(
specifier|final
name|int
name|row
parameter_list|,
specifier|final
name|RowItem
name|item
parameter_list|)
block|{
name|setRowItem
argument_list|(
name|table
operator|.
name|getCellFormatter
argument_list|()
operator|.
name|getElement
argument_list|(
name|row
argument_list|,
literal|0
argument_list|)
argument_list|,
name|item
argument_list|)
expr_stmt|;
block|}
DECL|method|resetHtml (final SafeHtml body)
specifier|protected
name|void
name|resetHtml
parameter_list|(
specifier|final
name|SafeHtml
name|body
parameter_list|)
block|{
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|Widget
argument_list|>
name|i
init|=
name|table
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|impl
operator|.
name|resetHtml
argument_list|(
name|table
argument_list|,
name|body
argument_list|)
expr_stmt|;
block|}
DECL|method|scrollIntoView (final int topRow, final int endRow)
specifier|protected
name|void
name|scrollIntoView
parameter_list|(
specifier|final
name|int
name|topRow
parameter_list|,
specifier|final
name|int
name|endRow
parameter_list|)
block|{
specifier|final
name|CellFormatter
name|fmt
init|=
name|table
operator|.
name|getCellFormatter
argument_list|()
decl_stmt|;
specifier|final
name|Element
name|top
init|=
name|DOM
operator|.
name|getParent
argument_list|(
name|fmt
operator|.
name|getElement
argument_list|(
name|topRow
argument_list|,
name|C_ARROW
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Element
name|end
init|=
name|DOM
operator|.
name|getParent
argument_list|(
name|fmt
operator|.
name|getElement
argument_list|(
name|endRow
argument_list|,
name|C_ARROW
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|rTop
init|=
name|top
operator|.
name|getAbsoluteTop
argument_list|()
decl_stmt|;
specifier|final
name|int
name|rEnd
init|=
name|end
operator|.
name|getAbsoluteTop
argument_list|()
operator|+
name|end
operator|.
name|getOffsetHeight
argument_list|()
decl_stmt|;
specifier|final
name|int
name|rHeight
init|=
name|rEnd
operator|-
name|rTop
decl_stmt|;
specifier|final
name|int
name|sTop
init|=
name|Document
operator|.
name|get
argument_list|()
operator|.
name|getScrollTop
argument_list|()
decl_stmt|;
specifier|final
name|int
name|sHeight
init|=
name|Document
operator|.
name|get
argument_list|()
operator|.
name|getClientHeight
argument_list|()
decl_stmt|;
specifier|final
name|int
name|sEnd
init|=
name|sTop
operator|+
name|sHeight
decl_stmt|;
specifier|final
name|int
name|nTop
decl_stmt|;
if|if
condition|(
name|sHeight
operator|<=
name|rHeight
condition|)
block|{
comment|// The region is larger than the visible area, make the top
comment|// exactly the top of the region, its the most visible area.
comment|//
name|nTop
operator|=
name|rTop
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sTop
operator|<=
name|rTop
operator|&&
name|rTop
operator|<=
name|sEnd
condition|)
block|{
comment|// At least part of the region is already visible.
comment|//
if|if
condition|(
name|rEnd
operator|<=
name|sEnd
condition|)
block|{
comment|// ... actually its all visible. Don't scroll.
comment|//
return|return;
block|}
comment|// Move only enough to make the end visible.
comment|//
name|nTop
operator|=
name|sTop
operator|+
operator|(
name|rHeight
operator|-
operator|(
name|sEnd
operator|-
name|rTop
operator|)
operator|)
expr_stmt|;
block|}
else|else
block|{
comment|// None of the region is visible. Make it visible.
comment|//
name|nTop
operator|=
name|rTop
expr_stmt|;
block|}
name|Document
operator|.
name|get
argument_list|()
operator|.
name|setScrollTop
argument_list|(
name|nTop
argument_list|)
expr_stmt|;
block|}
DECL|method|applyDataRowStyle (final int newRow)
specifier|protected
name|void
name|applyDataRowStyle
parameter_list|(
specifier|final
name|int
name|newRow
parameter_list|)
block|{
name|table
operator|.
name|getCellFormatter
argument_list|()
operator|.
name|addStyleName
argument_list|(
name|newRow
argument_list|,
name|C_ARROW
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|iconCell
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|getCellFormatter
argument_list|()
operator|.
name|addStyleName
argument_list|(
name|newRow
argument_list|,
name|C_ARROW
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|leftMostCell
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the td element that contains another element.    *    * @param target the child element whose parent td is required.    * @return the td containing element {@code target}; null if {@code target} is    *         not a member of this table.    */
DECL|method|getParentCell (final Element target)
specifier|protected
name|Element
name|getParentCell
parameter_list|(
specifier|final
name|Element
name|target
parameter_list|)
block|{
specifier|final
name|Element
name|body
init|=
name|FancyFlexTableImpl
operator|.
name|getBodyElement
argument_list|(
name|table
argument_list|)
decl_stmt|;
for|for
control|(
name|Element
name|td
init|=
name|target
init|;
name|td
operator|!=
literal|null
operator|&&
name|td
operator|!=
name|body
condition|;
name|td
operator|=
name|DOM
operator|.
name|getParent
argument_list|(
name|td
argument_list|)
control|)
block|{
comment|// If it's a TD, it might be the one we're looking for.
if|if
condition|(
literal|"td"
operator|.
name|equalsIgnoreCase
argument_list|(
name|td
operator|.
name|getTagName
argument_list|()
argument_list|)
condition|)
block|{
comment|// Make sure it's directly a part of this table.
name|Element
name|tr
init|=
name|DOM
operator|.
name|getParent
argument_list|(
name|td
argument_list|)
decl_stmt|;
if|if
condition|(
name|DOM
operator|.
name|getParent
argument_list|(
name|tr
argument_list|)
operator|==
name|body
condition|)
block|{
return|return
name|td
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/** @return the row of the child element; -1 if the child is not in the table. */
DECL|method|rowOf (final Element target)
specifier|protected
name|int
name|rowOf
parameter_list|(
specifier|final
name|Element
name|target
parameter_list|)
block|{
specifier|final
name|Element
name|td
init|=
name|getParentCell
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|td
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
specifier|final
name|Element
name|tr
init|=
name|DOM
operator|.
name|getParent
argument_list|(
name|td
argument_list|)
decl_stmt|;
specifier|final
name|Element
name|body
init|=
name|DOM
operator|.
name|getParent
argument_list|(
name|tr
argument_list|)
decl_stmt|;
return|return
name|DOM
operator|.
name|getChildIndex
argument_list|(
name|body
argument_list|,
name|tr
argument_list|)
return|;
block|}
comment|/** @return the cell of the child element; -1 if the child is not in the table. */
DECL|method|columnOf (final Element target)
specifier|protected
name|int
name|columnOf
parameter_list|(
specifier|final
name|Element
name|target
parameter_list|)
block|{
specifier|final
name|Element
name|td
init|=
name|getParentCell
argument_list|(
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
name|td
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
specifier|final
name|Element
name|tr
init|=
name|DOM
operator|.
name|getParent
argument_list|(
name|td
argument_list|)
decl_stmt|;
return|return
name|DOM
operator|.
name|getChildIndex
argument_list|(
name|tr
argument_list|,
name|td
argument_list|)
return|;
block|}
DECL|class|MyFlexTable
specifier|protected
specifier|static
class|class
name|MyFlexTable
extends|extends
name|FlexTable
block|{   }
DECL|method|setRowItem (Element td, ItemType c)
specifier|private
specifier|static
specifier|final
specifier|native
parameter_list|<
name|ItemType
parameter_list|>
name|void
name|setRowItem
parameter_list|(
name|Element
name|td
parameter_list|,
name|ItemType
name|c
parameter_list|)
comment|/*-{ td['__gerritRowItem'] = c; }-*/
function_decl|;
DECL|method|getRowItem (Element td)
specifier|private
specifier|static
specifier|final
specifier|native
parameter_list|<
name|ItemType
parameter_list|>
name|ItemType
name|getRowItem
parameter_list|(
name|Element
name|td
parameter_list|)
comment|/*-{ return td['__gerritRowItem']; }-*/
function_decl|;
block|}
end_class

end_unit

