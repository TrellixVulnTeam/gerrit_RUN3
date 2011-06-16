begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2011 The Android Open Source Project
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
DECL|package|com.google.gerrit.client.admin
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|admin
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
name|DivElement
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
name|NativeEvent
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
name|Display
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
name|editor
operator|.
name|client
operator|.
name|EditorError
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
name|editor
operator|.
name|client
operator|.
name|HasEditorErrors
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
name|editor
operator|.
name|client
operator|.
name|IsEditor
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
name|editor
operator|.
name|client
operator|.
name|LeafValueEditor
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
name|editor
operator|.
name|ui
operator|.
name|client
operator|.
name|adapters
operator|.
name|ValueBoxEditor
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
name|UiChild
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
name|SimplePanel
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
name|ValueBoxBase
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
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_class
DECL|class|ValueEditor
specifier|public
class|class
name|ValueEditor
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Composite
implements|implements
name|HasEditorErrors
argument_list|<
name|T
argument_list|>
implements|,
name|IsEditor
argument_list|<
name|ValueBoxEditor
argument_list|<
name|T
argument_list|>
argument_list|>
implements|,
name|LeafValueEditor
argument_list|<
name|T
argument_list|>
implements|,
name|Focusable
block|{
DECL|interface|Binder
interface|interface
name|Binder
extends|extends
name|UiBinder
argument_list|<
name|Widget
argument_list|,
name|ValueEditor
argument_list|<
name|?
argument_list|>
argument_list|>
block|{   }
DECL|field|uiBinder
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
annotation|@
name|UiField
DECL|field|textPanel
name|SimplePanel
name|textPanel
decl_stmt|;
DECL|field|textLabel
specifier|private
name|Label
name|textLabel
decl_stmt|;
DECL|field|startHandlers
specifier|private
name|StartEditHandlers
name|startHandlers
decl_stmt|;
annotation|@
name|UiField
DECL|field|editIcon
name|Image
name|editIcon
decl_stmt|;
annotation|@
name|UiField
DECL|field|editPanel
name|SimplePanel
name|editPanel
decl_stmt|;
annotation|@
name|UiField
DECL|field|errorLabel
name|DivElement
name|errorLabel
decl_stmt|;
DECL|field|editChild
specifier|private
name|ValueBoxBase
argument_list|<
name|T
argument_list|>
name|editChild
decl_stmt|;
DECL|field|editProxy
specifier|private
name|ValueBoxEditor
argument_list|<
name|T
argument_list|>
name|editProxy
decl_stmt|;
DECL|field|ignoreEditorValue
specifier|private
name|boolean
name|ignoreEditorValue
decl_stmt|;
DECL|field|value
specifier|private
name|T
name|value
decl_stmt|;
DECL|method|ValueEditor ()
specifier|public
name|ValueEditor
parameter_list|()
block|{
name|startHandlers
operator|=
operator|new
name|StartEditHandlers
argument_list|()
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
name|editPanel
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|editIcon
operator|.
name|addClickHandler
argument_list|(
name|startHandlers
argument_list|)
expr_stmt|;
block|}
DECL|method|edit ()
specifier|public
name|void
name|edit
parameter_list|()
block|{
name|textPanel
operator|.
name|removeFromParent
argument_list|()
expr_stmt|;
name|textPanel
operator|=
literal|null
expr_stmt|;
name|textLabel
operator|=
literal|null
expr_stmt|;
name|editIcon
operator|.
name|removeFromParent
argument_list|()
expr_stmt|;
name|editIcon
operator|=
literal|null
expr_stmt|;
name|startHandlers
operator|=
literal|null
expr_stmt|;
name|editPanel
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|asEditor ()
specifier|public
name|ValueBoxEditor
argument_list|<
name|T
argument_list|>
name|asEditor
parameter_list|()
block|{
if|if
condition|(
name|editProxy
operator|==
literal|null
condition|)
block|{
name|editProxy
operator|=
operator|new
name|EditorProxy
argument_list|()
expr_stmt|;
block|}
return|return
name|editProxy
return|;
block|}
annotation|@
name|Override
DECL|method|getValue ()
specifier|public
name|T
name|getValue
parameter_list|()
block|{
return|return
name|ignoreEditorValue
condition|?
name|value
else|:
name|asEditor
argument_list|()
operator|.
name|getValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setValue (T value)
specifier|public
name|void
name|setValue
parameter_list|(
name|T
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|asEditor
argument_list|()
operator|.
name|setValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|setIgnoreEditorValue (boolean off)
name|void
name|setIgnoreEditorValue
parameter_list|(
name|boolean
name|off
parameter_list|)
block|{
name|ignoreEditorValue
operator|=
name|off
expr_stmt|;
block|}
DECL|method|setEditTitle (String title)
specifier|public
name|void
name|setEditTitle
parameter_list|(
name|String
name|title
parameter_list|)
block|{
name|editIcon
operator|.
name|setTitle
argument_list|(
name|title
argument_list|)
expr_stmt|;
block|}
annotation|@
name|UiChild
argument_list|(
name|limit
operator|=
literal|1
argument_list|,
name|tagname
operator|=
literal|"display"
argument_list|)
DECL|method|setDisplay (Label widget)
specifier|public
name|void
name|setDisplay
parameter_list|(
name|Label
name|widget
parameter_list|)
block|{
name|textLabel
operator|=
name|widget
expr_stmt|;
name|textPanel
operator|.
name|add
argument_list|(
name|textLabel
argument_list|)
expr_stmt|;
name|textLabel
operator|.
name|addClickHandler
argument_list|(
name|startHandlers
argument_list|)
expr_stmt|;
name|textLabel
operator|.
name|addDoubleClickHandler
argument_list|(
name|startHandlers
argument_list|)
expr_stmt|;
block|}
annotation|@
name|UiChild
argument_list|(
name|limit
operator|=
literal|1
argument_list|,
name|tagname
operator|=
literal|"editor"
argument_list|)
DECL|method|setEditor (ValueBoxBase<T> widget)
specifier|public
name|void
name|setEditor
parameter_list|(
name|ValueBoxBase
argument_list|<
name|T
argument_list|>
name|widget
parameter_list|)
block|{
name|editChild
operator|=
name|widget
expr_stmt|;
name|editPanel
operator|.
name|add
argument_list|(
name|editChild
argument_list|)
expr_stmt|;
name|editProxy
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|setEnabled (boolean enabled)
specifier|public
name|void
name|setEnabled
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|editIcon
operator|.
name|setVisible
argument_list|(
name|enabled
argument_list|)
expr_stmt|;
name|startHandlers
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
block|}
DECL|method|showErrors (List<EditorError> errors)
specifier|public
name|void
name|showErrors
parameter_list|(
name|List
argument_list|<
name|EditorError
argument_list|>
name|errors
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|EditorError
name|error
range|:
name|errors
control|)
block|{
if|if
condition|(
name|error
operator|.
name|getEditor
argument_list|()
operator|.
name|equals
argument_list|(
name|editProxy
argument_list|)
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
if|if
condition|(
name|error
operator|.
name|getUserData
argument_list|()
operator|instanceof
name|ParseException
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
operator|(
operator|(
name|ParseException
operator|)
name|error
operator|.
name|getUserData
argument_list|()
operator|)
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
name|error
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
literal|0
operator|<
name|buf
operator|.
name|length
argument_list|()
condition|)
block|{
name|errorLabel
operator|.
name|setInnerText
argument_list|(
name|buf
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|errorLabel
operator|.
name|getStyle
argument_list|()
operator|.
name|setDisplay
argument_list|(
name|Display
operator|.
name|BLOCK
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|errorLabel
operator|.
name|setInnerText
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|errorLabel
operator|.
name|getStyle
argument_list|()
operator|.
name|setDisplay
argument_list|(
name|Display
operator|.
name|NONE
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|setAccessKey (char key)
specifier|public
name|void
name|setAccessKey
parameter_list|(
name|char
name|key
parameter_list|)
block|{
name|editChild
operator|.
name|setAccessKey
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setFocus (boolean focused)
specifier|public
name|void
name|setFocus
parameter_list|(
name|boolean
name|focused
parameter_list|)
block|{
name|editChild
operator|.
name|setFocus
argument_list|(
name|focused
argument_list|)
expr_stmt|;
if|if
condition|(
name|focused
condition|)
block|{
name|editChild
operator|.
name|setCursorPos
argument_list|(
name|editChild
operator|.
name|getText
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getTabIndex ()
specifier|public
name|int
name|getTabIndex
parameter_list|()
block|{
return|return
name|editChild
operator|.
name|getTabIndex
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setTabIndex (int index)
specifier|public
name|void
name|setTabIndex
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|editChild
operator|.
name|setTabIndex
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
DECL|class|StartEditHandlers
specifier|private
class|class
name|StartEditHandlers
implements|implements
name|ClickHandler
implements|,
name|DoubleClickHandler
block|{
DECL|field|enabled
name|boolean
name|enabled
decl_stmt|;
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
if|if
condition|(
name|enabled
operator|&&
name|event
operator|.
name|getNativeButton
argument_list|()
operator|==
name|NativeEvent
operator|.
name|BUTTON_LEFT
condition|)
block|{
name|edit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onDoubleClick (DoubleClickEvent event)
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
name|enabled
operator|&&
name|event
operator|.
name|getNativeButton
argument_list|()
operator|==
name|NativeEvent
operator|.
name|BUTTON_LEFT
condition|)
block|{
name|edit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|class|EditorProxy
specifier|private
class|class
name|EditorProxy
extends|extends
name|ValueBoxEditor
argument_list|<
name|T
argument_list|>
block|{
DECL|method|EditorProxy ()
name|EditorProxy
parameter_list|()
block|{
name|super
argument_list|(
name|editChild
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setValue (T value)
specifier|public
name|void
name|setValue
parameter_list|(
name|T
name|value
parameter_list|)
block|{
name|super
operator|.
name|setValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|textLabel
operator|==
literal|null
condition|)
block|{
name|setDisplay
argument_list|(
operator|new
name|Label
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|textLabel
operator|.
name|setText
argument_list|(
name|editChild
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

