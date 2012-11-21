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
DECL|package|com.google.gerrit.client
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
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
name|QueryScreen
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
name|HintTextBox
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
name|common
operator|.
name|PageLinks
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
name|Change
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
name|animation
operator|.
name|client
operator|.
name|Animation
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
name|SuggestOracle
operator|.
name|Suggestion
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

begin_class
DECL|class|SearchPanel
class|class
name|SearchPanel
extends|extends
name|Composite
block|{
DECL|field|FULL_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|FULL_SIZE
init|=
literal|70
decl_stmt|;
DECL|field|SMALL_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|SMALL_SIZE
init|=
literal|45
decl_stmt|;
DECL|class|SizeAnimation
specifier|private
class|class
name|SizeAnimation
extends|extends
name|Animation
block|{
DECL|field|targetSize
name|int
name|targetSize
decl_stmt|;
DECL|field|startSize
name|int
name|startSize
decl_stmt|;
DECL|method|run (boolean expand)
specifier|public
name|void
name|run
parameter_list|(
name|boolean
name|expand
parameter_list|)
block|{
if|if
condition|(
name|expand
condition|)
block|{
name|targetSize
operator|=
name|FULL_SIZE
expr_stmt|;
name|startSize
operator|=
name|SMALL_SIZE
expr_stmt|;
block|}
else|else
block|{
name|targetSize
operator|=
name|SMALL_SIZE
expr_stmt|;
name|startSize
operator|=
name|FULL_SIZE
expr_stmt|;
block|}
name|super
operator|.
name|run
argument_list|(
literal|300
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onUpdate (double progress)
specifier|protected
name|void
name|onUpdate
parameter_list|(
name|double
name|progress
parameter_list|)
block|{
name|int
name|size
init|=
call|(
name|int
call|)
argument_list|(
name|targetSize
operator|*
name|progress
operator|+
name|startSize
operator|*
operator|(
literal|1
operator|-
name|progress
operator|)
argument_list|)
decl_stmt|;
name|searchBox
operator|.
name|setVisibleLength
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onComplete ()
specifier|protected
name|void
name|onComplete
parameter_list|()
block|{
name|searchBox
operator|.
name|setVisibleLength
argument_list|(
name|targetSize
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|searchBox
specifier|private
specifier|final
name|HintTextBox
name|searchBox
decl_stmt|;
DECL|field|regFocus
specifier|private
name|HandlerRegistration
name|regFocus
decl_stmt|;
DECL|field|sizeAnimation
specifier|private
specifier|final
name|SizeAnimation
name|sizeAnimation
decl_stmt|;
DECL|method|SearchPanel ()
name|SearchPanel
parameter_list|()
block|{
specifier|final
name|FlowPanel
name|body
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|sizeAnimation
operator|=
operator|new
name|SizeAnimation
argument_list|()
expr_stmt|;
name|initWidget
argument_list|(
name|body
argument_list|)
expr_stmt|;
name|setStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|searchPanel
argument_list|()
argument_list|)
expr_stmt|;
name|searchBox
operator|=
operator|new
name|HintTextBox
argument_list|()
expr_stmt|;
specifier|final
name|MySuggestionDisplay
name|suggestionDisplay
init|=
operator|new
name|MySuggestionDisplay
argument_list|()
decl_stmt|;
name|searchBox
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
name|getNativeEvent
argument_list|()
operator|.
name|getKeyCode
argument_list|()
operator|==
name|KeyCodes
operator|.
name|KEY_ENTER
condition|)
block|{
if|if
condition|(
operator|!
name|suggestionDisplay
operator|.
name|isSuggestionSelected
condition|)
block|{
name|doSearch
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|searchBox
operator|.
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
if|if
condition|(
name|searchBox
operator|.
name|getVisibleLength
argument_list|()
operator|==
name|SMALL_SIZE
condition|)
block|{
name|sizeAnimation
operator|.
name|run
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|searchBox
operator|.
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
if|if
condition|(
name|searchBox
operator|.
name|getVisibleLength
argument_list|()
operator|!=
name|SMALL_SIZE
condition|)
block|{
name|sizeAnimation
operator|.
name|run
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|SuggestBox
name|suggestBox
init|=
operator|new
name|SuggestBox
argument_list|(
operator|new
name|SearchSuggestOracle
argument_list|()
argument_list|,
name|searchBox
argument_list|,
name|suggestionDisplay
argument_list|)
decl_stmt|;
name|searchBox
operator|.
name|setStyleName
argument_list|(
literal|"gwt-TextBox"
argument_list|)
expr_stmt|;
name|searchBox
operator|.
name|setVisibleLength
argument_list|(
name|SMALL_SIZE
argument_list|)
expr_stmt|;
name|searchBox
operator|.
name|setHintText
argument_list|(
name|Gerrit
operator|.
name|C
operator|.
name|searchHint
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Button
name|searchButton
init|=
operator|new
name|Button
argument_list|(
name|Gerrit
operator|.
name|C
operator|.
name|searchButton
argument_list|()
argument_list|)
decl_stmt|;
name|searchButton
operator|.
name|addClickHandler
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
name|doSearch
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|suggestBox
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|searchButton
argument_list|)
expr_stmt|;
block|}
DECL|method|setText (final String query)
name|void
name|setText
parameter_list|(
specifier|final
name|String
name|query
parameter_list|)
block|{
name|searchBox
operator|.
name|setText
argument_list|(
name|query
argument_list|)
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
name|super
operator|.
name|onLoad
argument_list|()
expr_stmt|;
if|if
condition|(
name|regFocus
operator|==
literal|null
condition|)
block|{
name|regFocus
operator|=
name|GlobalKey
operator|.
name|addApplication
argument_list|(
name|this
argument_list|,
operator|new
name|KeyCommand
argument_list|(
literal|0
argument_list|,
literal|'/'
argument_list|,
name|Gerrit
operator|.
name|C
operator|.
name|keySearch
argument_list|()
argument_list|)
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
name|event
operator|.
name|preventDefault
argument_list|()
expr_stmt|;
name|searchBox
operator|.
name|setFocus
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|searchBox
operator|.
name|selectAll
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onUnload ()
specifier|protected
name|void
name|onUnload
parameter_list|()
block|{
if|if
condition|(
name|regFocus
operator|!=
literal|null
condition|)
block|{
name|regFocus
operator|.
name|removeHandler
argument_list|()
expr_stmt|;
name|regFocus
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|doSearch ()
specifier|private
name|void
name|doSearch
parameter_list|()
block|{
specifier|final
name|String
name|query
init|=
name|searchBox
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
name|query
argument_list|)
condition|)
block|{
return|return;
block|}
name|searchBox
operator|.
name|setFocus
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|query
operator|.
name|matches
argument_list|(
literal|"^[1-9][0-9]*$"
argument_list|)
condition|)
block|{
name|Gerrit
operator|.
name|display
argument_list|(
name|PageLinks
operator|.
name|toChange
argument_list|(
name|Change
operator|.
name|Id
operator|.
name|parse
argument_list|(
name|query
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Gerrit
operator|.
name|display
argument_list|(
name|PageLinks
operator|.
name|toChangeQuery
argument_list|(
name|query
argument_list|)
argument_list|,
name|QueryScreen
operator|.
name|forQuery
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|MySuggestionDisplay
specifier|private
specifier|static
class|class
name|MySuggestionDisplay
extends|extends
name|SuggestBox
operator|.
name|DefaultSuggestionDisplay
block|{
DECL|field|isSuggestionSelected
specifier|private
name|boolean
name|isSuggestionSelected
decl_stmt|;
annotation|@
name|Override
DECL|method|getCurrentSelection ()
specifier|protected
name|Suggestion
name|getCurrentSelection
parameter_list|()
block|{
name|Suggestion
name|currentSelection
init|=
name|super
operator|.
name|getCurrentSelection
argument_list|()
decl_stmt|;
name|isSuggestionSelected
operator|=
name|currentSelection
operator|!=
literal|null
expr_stmt|;
return|return
name|currentSelection
return|;
block|}
block|}
block|}
end_class

end_unit

