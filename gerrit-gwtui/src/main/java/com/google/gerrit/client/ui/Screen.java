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
name|gerrit
operator|.
name|client
operator|.
name|projects
operator|.
name|ThemeInfo
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
name|Grid
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
name|HasHorizontalAlignment
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
name|user
operator|.
name|client
operator|.
name|View
import|;
end_import

begin_comment
comment|/**   *  A Screen layout with a header and a body.   *   * The header is mainly a text title, but it can be decorated   * in the West, the East, and the FarEast by any Widget.  The   * West and East decorations will surround the text on the   * left and right respectively, and the FarEast will be right   * justified to the right edge of the screen.  The East   * decoration will expand to take up any extra space.   */
end_comment

begin_class
DECL|class|Screen
specifier|public
specifier|abstract
class|class
name|Screen
extends|extends
name|View
block|{
DECL|field|header
specifier|private
name|Grid
name|header
decl_stmt|;
DECL|field|headerText
specifier|private
name|InlineLabel
name|headerText
decl_stmt|;
DECL|field|body
specifier|private
name|FlowPanel
name|body
decl_stmt|;
DECL|field|token
specifier|private
name|String
name|token
decl_stmt|;
DECL|field|requiresSignIn
specifier|private
name|boolean
name|requiresSignIn
decl_stmt|;
DECL|field|windowTitle
specifier|private
name|String
name|windowTitle
decl_stmt|;
DECL|field|titleWidget
specifier|private
name|Widget
name|titleWidget
decl_stmt|;
DECL|field|theme
specifier|private
name|ThemeInfo
name|theme
decl_stmt|;
DECL|field|setTheme
specifier|private
name|boolean
name|setTheme
decl_stmt|;
DECL|method|Screen ()
specifier|protected
name|Screen
parameter_list|()
block|{
name|initWidget
argument_list|(
operator|new
name|FlowPanel
argument_list|()
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
name|screen
argument_list|()
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
name|header
operator|==
literal|null
condition|)
block|{
name|onInitUI
argument_list|()
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
name|super
operator|.
name|onUnload
argument_list|()
expr_stmt|;
if|if
condition|(
name|setTheme
condition|)
block|{
name|Gerrit
operator|.
name|THEMER
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|registerKeys ()
specifier|public
name|void
name|registerKeys
parameter_list|()
block|{   }
DECL|enum|Cols
specifier|private
specifier|static
enum|enum
name|Cols
block|{
DECL|enumConstant|West
DECL|enumConstant|Title
DECL|enumConstant|East
DECL|enumConstant|FarEast
name|West
block|,
name|Title
block|,
name|East
block|,
name|FarEast
block|;   }
DECL|method|onInitUI ()
specifier|protected
name|void
name|onInitUI
parameter_list|()
block|{
specifier|final
name|FlowPanel
name|me
init|=
operator|(
name|FlowPanel
operator|)
name|getWidget
argument_list|()
decl_stmt|;
name|me
operator|.
name|add
argument_list|(
name|header
operator|=
operator|new
name|Grid
argument_list|(
literal|1
argument_list|,
name|Cols
operator|.
name|values
argument_list|()
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
name|me
operator|.
name|add
argument_list|(
name|body
operator|=
operator|new
name|FlowPanel
argument_list|()
argument_list|)
expr_stmt|;
name|headerText
operator|=
operator|new
name|InlineLabel
argument_list|()
expr_stmt|;
if|if
condition|(
name|titleWidget
operator|==
literal|null
condition|)
block|{
name|titleWidget
operator|=
name|headerText
expr_stmt|;
block|}
name|FlowPanel
name|title
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|title
operator|.
name|add
argument_list|(
name|titleWidget
argument_list|)
expr_stmt|;
name|title
operator|.
name|setStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|screenHeader
argument_list|()
argument_list|)
expr_stmt|;
name|header
operator|.
name|setWidget
argument_list|(
literal|0
argument_list|,
name|Cols
operator|.
name|Title
operator|.
name|ordinal
argument_list|()
argument_list|,
name|title
argument_list|)
expr_stmt|;
name|header
operator|.
name|setStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|screenHeader
argument_list|()
argument_list|)
expr_stmt|;
name|header
operator|.
name|getCellFormatter
argument_list|()
operator|.
name|setHorizontalAlignment
argument_list|(
literal|0
argument_list|,
name|Cols
operator|.
name|FarEast
operator|.
name|ordinal
argument_list|()
argument_list|,
name|HasHorizontalAlignment
operator|.
name|ALIGN_RIGHT
argument_list|)
expr_stmt|;
comment|// force FarEast all the way to the right
name|header
operator|.
name|getCellFormatter
argument_list|()
operator|.
name|setWidth
argument_list|(
literal|0
argument_list|,
name|Cols
operator|.
name|FarEast
operator|.
name|ordinal
argument_list|()
argument_list|,
literal|"100%"
argument_list|)
expr_stmt|;
block|}
DECL|method|setWindowTitle (final String text)
specifier|protected
name|void
name|setWindowTitle
parameter_list|(
specifier|final
name|String
name|text
parameter_list|)
block|{
name|windowTitle
operator|=
name|text
expr_stmt|;
name|Gerrit
operator|.
name|setWindowTitle
argument_list|(
name|this
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
DECL|method|setPageTitle (final String text)
specifier|protected
name|void
name|setPageTitle
parameter_list|(
specifier|final
name|String
name|text
parameter_list|)
block|{
specifier|final
name|String
name|old
init|=
name|headerText
operator|.
name|getText
argument_list|()
decl_stmt|;
if|if
condition|(
name|text
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|header
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|headerText
operator|.
name|setText
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|header
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|windowTitle
operator|==
literal|null
operator|||
name|windowTitle
operator|==
name|old
condition|)
block|{
name|setWindowTitle
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setHeaderVisible (boolean value)
specifier|protected
name|void
name|setHeaderVisible
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
name|header
operator|.
name|setVisible
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|setTitle (final Widget w)
specifier|public
name|void
name|setTitle
parameter_list|(
specifier|final
name|Widget
name|w
parameter_list|)
block|{
name|titleWidget
operator|=
name|w
expr_stmt|;
block|}
DECL|method|setTitleEast (final Widget w)
specifier|protected
name|void
name|setTitleEast
parameter_list|(
specifier|final
name|Widget
name|w
parameter_list|)
block|{
name|header
operator|.
name|setWidget
argument_list|(
literal|0
argument_list|,
name|Cols
operator|.
name|East
operator|.
name|ordinal
argument_list|()
argument_list|,
name|w
argument_list|)
expr_stmt|;
block|}
DECL|method|setTitleFarEast (final Widget w)
specifier|protected
name|void
name|setTitleFarEast
parameter_list|(
specifier|final
name|Widget
name|w
parameter_list|)
block|{
name|header
operator|.
name|setWidget
argument_list|(
literal|0
argument_list|,
name|Cols
operator|.
name|FarEast
operator|.
name|ordinal
argument_list|()
argument_list|,
name|w
argument_list|)
expr_stmt|;
block|}
DECL|method|setTitleWest (final Widget w)
specifier|protected
name|void
name|setTitleWest
parameter_list|(
specifier|final
name|Widget
name|w
parameter_list|)
block|{
name|header
operator|.
name|setWidget
argument_list|(
literal|0
argument_list|,
name|Cols
operator|.
name|West
operator|.
name|ordinal
argument_list|()
argument_list|,
name|w
argument_list|)
expr_stmt|;
block|}
DECL|method|add (final Widget w)
specifier|protected
name|void
name|add
parameter_list|(
specifier|final
name|Widget
name|w
parameter_list|)
block|{
name|body
operator|.
name|add
argument_list|(
name|w
argument_list|)
expr_stmt|;
block|}
DECL|method|setTheme (final ThemeInfo t)
specifier|protected
name|void
name|setTheme
parameter_list|(
specifier|final
name|ThemeInfo
name|t
parameter_list|)
block|{
name|theme
operator|=
name|t
expr_stmt|;
block|}
comment|/** Get the history token for this screen. */
DECL|method|getToken ()
specifier|public
name|String
name|getToken
parameter_list|()
block|{
return|return
name|token
return|;
block|}
comment|/** Set the history token for this screen. */
DECL|method|setToken (final String t)
specifier|public
name|void
name|setToken
parameter_list|(
specifier|final
name|String
name|t
parameter_list|)
block|{
assert|assert
name|t
operator|!=
literal|null
operator|&&
operator|!
name|t
operator|.
name|isEmpty
argument_list|()
assert|;
name|token
operator|=
name|t
expr_stmt|;
if|if
condition|(
name|isCurrentView
argument_list|()
condition|)
block|{
name|Gerrit
operator|.
name|updateImpl
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * If this view can display the given token, update it.    *    * @param newToken token the UI wants to show.    * @return true if this view can show the token immediately, false if not.    */
DECL|method|displayToken (String newToken)
specifier|public
name|boolean
name|displayToken
parameter_list|(
name|String
name|newToken
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
comment|/** Set whether or not {@link Gerrit#isSignedIn()} must be true. */
DECL|method|setRequiresSignIn (final boolean b)
specifier|public
specifier|final
name|void
name|setRequiresSignIn
parameter_list|(
specifier|final
name|boolean
name|b
parameter_list|)
block|{
name|requiresSignIn
operator|=
name|b
expr_stmt|;
block|}
comment|/** Does {@link Gerrit#isSignedIn()} have to be true to be on this screen? */
DECL|method|isRequiresSignIn ()
specifier|public
specifier|final
name|boolean
name|isRequiresSignIn
parameter_list|()
block|{
return|return
name|requiresSignIn
return|;
block|}
DECL|method|onShowView ()
specifier|public
name|void
name|onShowView
parameter_list|()
block|{
if|if
condition|(
name|windowTitle
operator|!=
literal|null
condition|)
block|{
name|Gerrit
operator|.
name|setWindowTitle
argument_list|(
name|this
argument_list|,
name|windowTitle
argument_list|)
expr_stmt|;
block|}
name|Gerrit
operator|.
name|updateMenus
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|Gerrit
operator|.
name|EVENT_BUS
operator|.
name|fireEvent
argument_list|(
operator|new
name|ScreenLoadEvent
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|Gerrit
operator|.
name|setQueryString
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|registerKeys
argument_list|()
expr_stmt|;
if|if
condition|(
name|theme
operator|!=
literal|null
condition|)
block|{
name|Gerrit
operator|.
name|THEMER
operator|.
name|set
argument_list|(
name|theme
argument_list|)
expr_stmt|;
name|setTheme
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|Gerrit
operator|.
name|THEMER
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

