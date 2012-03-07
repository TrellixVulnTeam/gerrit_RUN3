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
name|shared
operator|.
name|HandlerRegistration
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
name|Image
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
name|ScrollPanel
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
name|globalkey
operator|.
name|client
operator|.
name|KeyCommand
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
name|KeyCommandSet
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
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_class
DECL|class|NavigationTable
specifier|public
specifier|abstract
class|class
name|NavigationTable
parameter_list|<
name|RowItem
parameter_list|>
extends|extends
name|FancyFlexTable
argument_list|<
name|RowItem
argument_list|>
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"serial"
argument_list|)
DECL|field|savedPositions
specifier|private
specifier|static
specifier|final
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|savedPositions
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|(
literal|10
argument_list|,
literal|0.75f
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|removeEldestEntry
parameter_list|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|eldest
parameter_list|)
block|{
return|return
name|size
argument_list|()
operator|>=
literal|20
return|;
block|}
block|}
decl_stmt|;
DECL|field|pointer
specifier|private
specifier|final
name|Image
name|pointer
decl_stmt|;
DECL|field|keysNavigation
specifier|protected
specifier|final
name|KeyCommandSet
name|keysNavigation
decl_stmt|;
DECL|field|keysAction
specifier|protected
specifier|final
name|KeyCommandSet
name|keysAction
decl_stmt|;
DECL|field|regNavigation
specifier|private
name|HandlerRegistration
name|regNavigation
decl_stmt|;
DECL|field|regAction
specifier|private
name|HandlerRegistration
name|regAction
decl_stmt|;
DECL|field|currentRow
specifier|private
name|int
name|currentRow
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|saveId
specifier|private
name|String
name|saveId
decl_stmt|;
DECL|field|computedScrollType
specifier|private
name|boolean
name|computedScrollType
decl_stmt|;
DECL|field|parentScrollPanel
specifier|private
name|ScrollPanel
name|parentScrollPanel
decl_stmt|;
DECL|method|NavigationTable ()
specifier|protected
name|NavigationTable
parameter_list|()
block|{
name|pointer
operator|=
operator|new
name|Image
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|arrowRight
argument_list|()
argument_list|)
expr_stmt|;
name|keysNavigation
operator|=
operator|new
name|KeyCommandSet
argument_list|(
name|Gerrit
operator|.
name|C
operator|.
name|sectionNavigation
argument_list|()
argument_list|)
expr_stmt|;
name|keysAction
operator|=
operator|new
name|KeyCommandSet
argument_list|(
name|Gerrit
operator|.
name|C
operator|.
name|sectionActions
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|onOpenRow (int row)
specifier|protected
specifier|abstract
name|void
name|onOpenRow
parameter_list|(
name|int
name|row
parameter_list|)
function_decl|;
DECL|method|getRowItemKey (RowItem item)
specifier|protected
specifier|abstract
name|Object
name|getRowItemKey
parameter_list|(
name|RowItem
name|item
parameter_list|)
function_decl|;
DECL|method|onUp ()
specifier|private
name|void
name|onUp
parameter_list|()
block|{
for|for
control|(
name|int
name|row
init|=
name|currentRow
operator|-
literal|1
init|;
name|row
operator|>=
literal|0
condition|;
name|row
operator|--
control|)
block|{
if|if
condition|(
name|getRowItem
argument_list|(
name|row
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|movePointerTo
argument_list|(
name|row
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
DECL|method|onDown ()
specifier|private
name|void
name|onDown
parameter_list|()
block|{
specifier|final
name|int
name|max
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
name|currentRow
operator|+
literal|1
init|;
name|row
operator|<
name|max
condition|;
name|row
operator|++
control|)
block|{
if|if
condition|(
name|getRowItem
argument_list|(
name|row
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|movePointerTo
argument_list|(
name|row
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
DECL|method|onOpen ()
specifier|private
name|void
name|onOpen
parameter_list|()
block|{
if|if
condition|(
literal|0
operator|<=
name|currentRow
operator|&&
name|currentRow
operator|<
name|table
operator|.
name|getRowCount
argument_list|()
condition|)
block|{
if|if
condition|(
name|getRowItem
argument_list|(
name|currentRow
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|onOpenRow
argument_list|(
name|currentRow
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getCurrentRow ()
specifier|protected
name|int
name|getCurrentRow
parameter_list|()
block|{
return|return
name|currentRow
return|;
block|}
DECL|method|ensurePointerVisible ()
specifier|protected
name|void
name|ensurePointerVisible
parameter_list|()
block|{
specifier|final
name|int
name|max
init|=
name|table
operator|.
name|getRowCount
argument_list|()
decl_stmt|;
name|int
name|row
init|=
name|currentRow
decl_stmt|;
specifier|final
name|int
name|init
init|=
name|row
decl_stmt|;
if|if
condition|(
name|row
operator|<
literal|0
condition|)
block|{
name|row
operator|=
literal|0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|max
operator|<=
name|row
condition|)
block|{
name|row
operator|=
name|max
operator|-
literal|1
expr_stmt|;
block|}
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
name|sEnd
init|=
name|sTop
operator|+
name|Document
operator|.
name|get
argument_list|()
operator|.
name|getClientHeight
argument_list|()
decl_stmt|;
while|while
condition|(
literal|0
operator|<=
name|row
operator|&&
name|row
operator|<
name|max
condition|)
block|{
specifier|final
name|Element
name|cur
init|=
name|DOM
operator|.
name|getParent
argument_list|(
name|fmt
operator|.
name|getElement
argument_list|(
name|row
argument_list|,
name|C_ARROW
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|cTop
init|=
name|cur
operator|.
name|getAbsoluteTop
argument_list|()
decl_stmt|;
specifier|final
name|int
name|cEnd
init|=
name|cTop
operator|+
name|cur
operator|.
name|getOffsetHeight
argument_list|()
decl_stmt|;
if|if
condition|(
name|cEnd
operator|<
name|sTop
condition|)
block|{
name|row
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sEnd
operator|<
name|cTop
condition|)
block|{
name|row
operator|--
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
if|if
condition|(
name|init
operator|!=
name|row
condition|)
block|{
name|movePointerTo
argument_list|(
name|row
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|movePointerTo (final int newRow)
specifier|protected
name|void
name|movePointerTo
parameter_list|(
specifier|final
name|int
name|newRow
parameter_list|)
block|{
name|movePointerTo
argument_list|(
name|newRow
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|movePointerTo (final int newRow, final boolean scroll)
specifier|protected
name|void
name|movePointerTo
parameter_list|(
specifier|final
name|int
name|newRow
parameter_list|,
specifier|final
name|boolean
name|scroll
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
name|boolean
name|clear
init|=
literal|0
operator|<=
name|currentRow
operator|&&
name|currentRow
operator|<
name|table
operator|.
name|getRowCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|clear
condition|)
block|{
specifier|final
name|Element
name|tr
init|=
name|DOM
operator|.
name|getParent
argument_list|(
name|fmt
operator|.
name|getElement
argument_list|(
name|currentRow
argument_list|,
name|C_ARROW
argument_list|)
argument_list|)
decl_stmt|;
name|UIObject
operator|.
name|setStyleName
argument_list|(
name|tr
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|activeRow
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|newRow
operator|>=
literal|0
condition|)
block|{
name|table
operator|.
name|setWidget
argument_list|(
name|newRow
argument_list|,
name|C_ARROW
argument_list|,
name|pointer
argument_list|)
expr_stmt|;
specifier|final
name|Element
name|tr
init|=
name|DOM
operator|.
name|getParent
argument_list|(
name|fmt
operator|.
name|getElement
argument_list|(
name|newRow
argument_list|,
name|C_ARROW
argument_list|)
argument_list|)
decl_stmt|;
name|UIObject
operator|.
name|setStyleName
argument_list|(
name|tr
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|activeRow
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|scroll
condition|)
block|{
name|scrollIntoView
argument_list|(
name|tr
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|clear
condition|)
block|{
name|table
operator|.
name|setWidget
argument_list|(
name|currentRow
argument_list|,
name|C_ARROW
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|pointer
operator|.
name|removeFromParent
argument_list|()
expr_stmt|;
block|}
name|currentRow
operator|=
name|newRow
expr_stmt|;
block|}
DECL|method|scrollIntoView (final Element tr)
specifier|protected
name|void
name|scrollIntoView
parameter_list|(
specifier|final
name|Element
name|tr
parameter_list|)
block|{
if|if
condition|(
operator|!
name|computedScrollType
condition|)
block|{
name|parentScrollPanel
operator|=
literal|null
expr_stmt|;
name|Widget
name|w
init|=
name|getParent
argument_list|()
decl_stmt|;
while|while
condition|(
name|w
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|w
operator|instanceof
name|ScrollPanel
condition|)
block|{
name|parentScrollPanel
operator|=
operator|(
name|ScrollPanel
operator|)
name|w
expr_stmt|;
break|break;
block|}
name|w
operator|=
name|w
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
name|computedScrollType
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|parentScrollPanel
operator|!=
literal|null
condition|)
block|{
name|parentScrollPanel
operator|.
name|ensureVisible
argument_list|(
operator|new
name|UIObject
argument_list|()
block|{
block|{
name|setElement
parameter_list|(
name|tr
parameter_list|)
constructor_decl|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tr
operator|.
name|scrollIntoView
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|movePointerTo (final Object oldId)
specifier|protected
name|void
name|movePointerTo
parameter_list|(
specifier|final
name|Object
name|oldId
parameter_list|)
block|{
specifier|final
name|int
name|row
init|=
name|findRow
argument_list|(
name|oldId
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|<=
name|row
condition|)
block|{
name|movePointerTo
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|findRow (final Object oldId)
specifier|protected
name|int
name|findRow
parameter_list|(
specifier|final
name|Object
name|oldId
parameter_list|)
block|{
if|if
condition|(
name|oldId
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|max
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
name|max
condition|;
name|row
operator|++
control|)
block|{
specifier|final
name|RowItem
name|c
init|=
name|getRowItem
argument_list|(
name|row
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
operator|&&
name|oldId
operator|.
name|equals
argument_list|(
name|getRowItemKey
argument_list|(
name|c
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|row
return|;
block|}
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|resetHtml (SafeHtml body)
specifier|protected
name|void
name|resetHtml
parameter_list|(
name|SafeHtml
name|body
parameter_list|)
block|{
name|currentRow
operator|=
operator|-
literal|1
expr_stmt|;
name|super
operator|.
name|resetHtml
argument_list|(
name|body
argument_list|)
expr_stmt|;
block|}
DECL|method|finishDisplay ()
specifier|public
name|void
name|finishDisplay
parameter_list|()
block|{
if|if
condition|(
name|saveId
operator|!=
literal|null
condition|)
block|{
name|movePointerTo
argument_list|(
name|savedPositions
operator|.
name|get
argument_list|(
name|saveId
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|currentRow
operator|<
literal|0
condition|)
block|{
name|onDown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|setSavePointerId (final String id)
specifier|public
name|void
name|setSavePointerId
parameter_list|(
specifier|final
name|String
name|id
parameter_list|)
block|{
name|saveId
operator|=
name|id
expr_stmt|;
block|}
DECL|method|setRegisterKeys (final boolean on)
specifier|public
name|void
name|setRegisterKeys
parameter_list|(
specifier|final
name|boolean
name|on
parameter_list|)
block|{
if|if
condition|(
name|on
operator|&&
name|isAttached
argument_list|()
condition|)
block|{
if|if
condition|(
name|regNavigation
operator|==
literal|null
condition|)
block|{
name|regNavigation
operator|=
name|GlobalKey
operator|.
name|add
argument_list|(
name|this
argument_list|,
name|keysNavigation
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|regAction
operator|==
literal|null
condition|)
block|{
name|regAction
operator|=
name|GlobalKey
operator|.
name|add
argument_list|(
name|this
argument_list|,
name|keysAction
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|regNavigation
operator|!=
literal|null
condition|)
block|{
name|regNavigation
operator|.
name|removeHandler
argument_list|()
expr_stmt|;
name|regNavigation
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|regAction
operator|!=
literal|null
condition|)
block|{
name|regAction
operator|.
name|removeHandler
argument_list|()
expr_stmt|;
name|regAction
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|onLoad ()
specifier|protected
name|void
name|onLoad
parameter_list|()
block|{
name|computedScrollType
operator|=
literal|false
expr_stmt|;
name|parentScrollPanel
operator|=
literal|null
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
name|setRegisterKeys
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|saveId
operator|!=
literal|null
operator|&&
name|currentRow
operator|>=
literal|0
condition|)
block|{
specifier|final
name|RowItem
name|c
init|=
name|getRowItem
argument_list|(
name|currentRow
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
name|savedPositions
operator|.
name|put
argument_list|(
name|saveId
argument_list|,
name|getRowItemKey
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|computedScrollType
operator|=
literal|false
expr_stmt|;
name|parentScrollPanel
operator|=
literal|null
expr_stmt|;
name|super
operator|.
name|onUnload
argument_list|()
expr_stmt|;
block|}
DECL|class|PrevKeyCommand
specifier|public
class|class
name|PrevKeyCommand
extends|extends
name|KeyCommand
block|{
DECL|method|PrevKeyCommand (int mask, char key, String help)
specifier|public
name|PrevKeyCommand
parameter_list|(
name|int
name|mask
parameter_list|,
name|char
name|key
parameter_list|,
name|String
name|help
parameter_list|)
block|{
name|super
argument_list|(
name|mask
argument_list|,
name|key
argument_list|,
name|help
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onKeyPress (final KeyPressEvent event)
specifier|public
name|void
name|onKeyPress
parameter_list|(
specifier|final
name|KeyPressEvent
name|event
parameter_list|)
block|{
name|ensurePointerVisible
argument_list|()
expr_stmt|;
name|onUp
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|NextKeyCommand
specifier|public
class|class
name|NextKeyCommand
extends|extends
name|KeyCommand
block|{
DECL|method|NextKeyCommand (int mask, char key, String help)
specifier|public
name|NextKeyCommand
parameter_list|(
name|int
name|mask
parameter_list|,
name|char
name|key
parameter_list|,
name|String
name|help
parameter_list|)
block|{
name|super
argument_list|(
name|mask
argument_list|,
name|key
argument_list|,
name|help
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onKeyPress (final KeyPressEvent event)
specifier|public
name|void
name|onKeyPress
parameter_list|(
specifier|final
name|KeyPressEvent
name|event
parameter_list|)
block|{
name|ensurePointerVisible
argument_list|()
expr_stmt|;
name|onDown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|OpenKeyCommand
specifier|public
class|class
name|OpenKeyCommand
extends|extends
name|KeyCommand
block|{
DECL|method|OpenKeyCommand (int mask, int key, String help)
specifier|public
name|OpenKeyCommand
parameter_list|(
name|int
name|mask
parameter_list|,
name|int
name|key
parameter_list|,
name|String
name|help
parameter_list|)
block|{
name|super
argument_list|(
name|mask
argument_list|,
name|key
argument_list|,
name|help
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onKeyPress (final KeyPressEvent event)
specifier|public
name|void
name|onKeyPress
parameter_list|(
specifier|final
name|KeyPressEvent
name|event
parameter_list|)
block|{
name|ensurePointerVisible
argument_list|()
expr_stmt|;
name|onOpen
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

