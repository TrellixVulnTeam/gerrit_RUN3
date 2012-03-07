begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
name|BlurHandler
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
name|FocusEvent
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
name|FocusHandler
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
name|KeyDownHandler
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
name|ui
operator|.
name|SuggestBox
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
name|SuggestBox
operator|.
name|DefaultSuggestionDisplay
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
name|NpTextBox
import|;
end_import

begin_class
DECL|class|HintTextBox
specifier|public
class|class
name|HintTextBox
extends|extends
name|NpTextBox
block|{
DECL|field|hintFocusHandler
specifier|private
name|HandlerRegistration
name|hintFocusHandler
decl_stmt|;
DECL|field|hintBlurHandler
specifier|private
name|HandlerRegistration
name|hintBlurHandler
decl_stmt|;
DECL|field|keyDownHandler
specifier|private
name|HandlerRegistration
name|keyDownHandler
decl_stmt|;
DECL|field|hintText
specifier|private
name|String
name|hintText
decl_stmt|;
DECL|field|hintStyleName
specifier|private
name|String
name|hintStyleName
init|=
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|inputFieldTypeHint
argument_list|()
decl_stmt|;
DECL|field|prevText
specifier|private
name|String
name|prevText
decl_stmt|;
DECL|field|hintOn
specifier|private
name|boolean
name|hintOn
decl_stmt|;
DECL|field|isFocused
specifier|private
name|boolean
name|isFocused
decl_stmt|;
DECL|method|getText ()
specifier|public
name|String
name|getText
parameter_list|()
block|{
if|if
condition|(
name|hintOn
condition|)
block|{
return|return
literal|""
return|;
block|}
return|return
name|super
operator|.
name|getText
argument_list|()
return|;
block|}
DECL|method|setText (String text)
specifier|public
name|void
name|setText
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|focusHint
argument_list|()
expr_stmt|;
name|super
operator|.
name|setText
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|prevText
operator|=
name|text
expr_stmt|;
if|if
condition|(
operator|!
name|isFocused
condition|)
block|{
name|blurHint
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getHintText ()
specifier|public
name|String
name|getHintText
parameter_list|()
block|{
return|return
name|hintText
return|;
block|}
DECL|method|setHintText (String text)
specifier|public
name|void
name|setHintText
parameter_list|(
name|String
name|text
parameter_list|)
block|{
if|if
condition|(
name|text
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|hintText
operator|==
literal|null
condition|)
block|{
comment|// was not set, still not set, no change.
return|return;
block|}
comment|// Clearing a previously set Hint
name|hintFocusHandler
operator|.
name|removeHandler
argument_list|()
expr_stmt|;
name|hintFocusHandler
operator|=
literal|null
expr_stmt|;
name|hintBlurHandler
operator|.
name|removeHandler
argument_list|()
expr_stmt|;
name|hintBlurHandler
operator|=
literal|null
expr_stmt|;
name|keyDownHandler
operator|.
name|removeHandler
argument_list|()
expr_stmt|;
name|keyDownHandler
operator|=
literal|null
expr_stmt|;
name|hintText
operator|=
literal|null
expr_stmt|;
name|focusHint
argument_list|()
expr_stmt|;
return|return;
block|}
comment|// Setting Hints
if|if
condition|(
name|hintText
operator|==
literal|null
condition|)
block|{
comment|// first time (was not already set)
name|hintText
operator|=
name|text
expr_stmt|;
name|hintFocusHandler
operator|=
name|addFocusHandler
argument_list|(
operator|new
name|FocusHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onFocus
parameter_list|(
name|FocusEvent
name|event
parameter_list|)
block|{
name|focusHint
argument_list|()
expr_stmt|;
name|prevText
operator|=
name|getText
argument_list|()
expr_stmt|;
name|isFocused
operator|=
literal|true
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|hintBlurHandler
operator|=
name|addBlurHandler
argument_list|(
operator|new
name|BlurHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onBlur
parameter_list|(
name|BlurEvent
name|event
parameter_list|)
block|{
name|blurHint
argument_list|()
expr_stmt|;
name|isFocused
operator|=
literal|false
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|/*       * There seems to be a strange bug (at least on firefox 3.5.9 ubuntu) with       * the textbox under the following circumstances:       *  1) The field is not focused with BText in it.       *  2) The field receives focus and a focus listener changes the text to FText       *  3) The ESC key is pressed and the value of the field has not changed       *     (ever) from FText       *  4) BUG: The text value gets reset to BText!       *       *  A counter to this bug seems to be to force setFocus(false) on ESC.       */
comment|/* Chrome does not create a KeyPressEvent on ESC, so use KeyDownEvents */
name|keyDownHandler
operator|=
name|addKeyDownHandler
argument_list|(
operator|new
name|KeyDownHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onKeyDown
parameter_list|(
specifier|final
name|KeyDownEvent
name|event
parameter_list|)
block|{
name|onKey
argument_list|(
name|event
operator|.
name|getNativeKeyCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Changing an already set Hint
name|focusHint
argument_list|()
expr_stmt|;
name|hintText
operator|=
name|text
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|isFocused
condition|)
block|{
name|blurHint
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|onKey (int key)
specifier|private
name|void
name|onKey
parameter_list|(
name|int
name|key
parameter_list|)
block|{
if|if
condition|(
name|key
operator|==
name|KeyCodes
operator|.
name|KEY_ESCAPE
condition|)
block|{
name|setText
argument_list|(
name|prevText
argument_list|)
expr_stmt|;
name|Widget
name|p
init|=
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|instanceof
name|SuggestBox
condition|)
block|{
comment|// Since the text was changed, ensure that the SuggestBox is
comment|// aware of this change so that it will refresh properly on
comment|// the next keystroke.  Without this, if the first keystroke
comment|// recreates the same string as before ESC was pressed, the
comment|// SuggestBox will think that the string has not changed, and
comment|// it will not yet provide any Suggestions.
operator|(
operator|(
name|SuggestBox
operator|)
name|p
operator|)
operator|.
name|showSuggestionList
argument_list|()
expr_stmt|;
comment|// The suggestion list lingers if we don't hide it.
operator|(
call|(
name|DefaultSuggestionDisplay
call|)
argument_list|(
operator|(
name|SuggestBox
operator|)
name|p
argument_list|)
operator|.
name|getSuggestionDisplay
argument_list|()
operator|)
operator|.
name|hideSuggestions
argument_list|()
expr_stmt|;
block|}
name|setFocus
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setHintStyleName (String styleName)
specifier|public
name|void
name|setHintStyleName
parameter_list|(
name|String
name|styleName
parameter_list|)
block|{
if|if
condition|(
name|hintStyleName
operator|!=
literal|null
operator|&&
name|hintOn
condition|)
block|{
name|removeStyleName
argument_list|(
name|hintStyleName
argument_list|)
expr_stmt|;
block|}
name|hintStyleName
operator|=
name|styleName
expr_stmt|;
if|if
condition|(
name|styleName
operator|!=
literal|null
operator|&&
name|hintOn
condition|)
block|{
name|addStyleName
argument_list|(
name|styleName
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getHintStyleName ()
specifier|public
name|String
name|getHintStyleName
parameter_list|()
block|{
return|return
name|hintStyleName
return|;
block|}
DECL|method|blurHint ()
specifier|protected
name|void
name|blurHint
parameter_list|()
block|{
if|if
condition|(
operator|!
name|hintOn
operator|&&
name|getHintText
argument_list|()
operator|!=
literal|null
operator|&&
literal|""
operator|.
name|equals
argument_list|(
name|super
operator|.
name|getText
argument_list|()
argument_list|)
condition|)
block|{
name|hintOn
operator|=
literal|true
expr_stmt|;
name|super
operator|.
name|setText
argument_list|(
name|getHintText
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|getHintStyleName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|addStyleName
argument_list|(
name|getHintStyleName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|focusHint ()
specifier|protected
name|void
name|focusHint
parameter_list|()
block|{
if|if
condition|(
name|hintOn
condition|)
block|{
name|super
operator|.
name|setText
argument_list|(
literal|""
argument_list|)
expr_stmt|;
if|if
condition|(
name|getHintStyleName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|removeStyleName
argument_list|(
name|getHintStyleName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|hintOn
operator|=
literal|false
expr_stmt|;
block|}
block|}
DECL|method|setFocus (boolean focus)
specifier|public
name|void
name|setFocus
parameter_list|(
name|boolean
name|focus
parameter_list|)
block|{
name|super
operator|.
name|setFocus
argument_list|(
name|focus
argument_list|)
expr_stmt|;
if|if
condition|(
name|focus
operator|!=
name|isFocused
condition|)
block|{
if|if
condition|(
name|focus
condition|)
block|{
name|focusHint
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|blurHint
argument_list|()
expr_stmt|;
block|}
block|}
name|isFocused
operator|=
name|focus
expr_stmt|;
block|}
block|}
end_class

end_unit

