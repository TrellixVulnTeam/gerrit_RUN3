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
name|Link
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
name|History
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

begin_class
DECL|class|Screen
specifier|public
class|class
name|Screen
extends|extends
name|View
block|{
DECL|field|header
specifier|private
specifier|final
name|FlowPanel
name|header
decl_stmt|;
DECL|field|headerText
specifier|private
specifier|final
name|InlineLabel
name|headerText
decl_stmt|;
DECL|field|body
specifier|private
specifier|final
name|FlowPanel
name|body
decl_stmt|;
DECL|field|requiresSignIn
specifier|private
name|boolean
name|requiresSignIn
decl_stmt|;
DECL|method|Screen ()
specifier|protected
name|Screen
parameter_list|()
block|{
name|this
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|Screen (final String headingText)
specifier|protected
name|Screen
parameter_list|(
specifier|final
name|String
name|headingText
parameter_list|)
block|{
specifier|final
name|FlowPanel
name|me
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|initWidget
argument_list|(
name|me
argument_list|)
expr_stmt|;
name|setStyleName
argument_list|(
literal|"gerrit-Screen"
argument_list|)
expr_stmt|;
name|me
operator|.
name|add
argument_list|(
name|header
operator|=
operator|new
name|FlowPanel
argument_list|()
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
name|header
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-ScreenHeader"
argument_list|)
expr_stmt|;
name|header
operator|.
name|add
argument_list|(
name|headerText
operator|=
operator|new
name|InlineLabel
argument_list|(
name|headingText
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setTitleText (final String text)
specifier|public
name|void
name|setTitleText
parameter_list|(
specifier|final
name|String
name|text
parameter_list|)
block|{
name|headerText
operator|.
name|setText
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
DECL|method|insertTitleWidget (final Widget w)
specifier|protected
name|void
name|insertTitleWidget
parameter_list|(
specifier|final
name|Widget
name|w
parameter_list|)
block|{
name|header
operator|.
name|insert
argument_list|(
name|w
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|add (final Widget w)
specifier|protected
specifier|final
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
comment|/** Set whether or not {@link Gerrit#isSignedIn()} must be true. */
DECL|method|setRequiresSignIn (final boolean b)
specifier|public
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
name|boolean
name|isRequiresSignIn
parameter_list|()
block|{
return|return
name|requiresSignIn
return|;
block|}
comment|/** Invoked if this screen is the current screen and the user signs out. */
DECL|method|onSignOut ()
specifier|public
name|void
name|onSignOut
parameter_list|()
block|{
if|if
condition|(
name|isRequiresSignIn
argument_list|()
condition|)
block|{
name|History
operator|.
name|newItem
argument_list|(
name|Link
operator|.
name|ALL_OPEN
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Invoked if this screen is the current screen and the user signs in. */
DECL|method|onSignIn ()
specifier|public
name|void
name|onSignIn
parameter_list|()
block|{   }
block|}
end_class

end_unit

