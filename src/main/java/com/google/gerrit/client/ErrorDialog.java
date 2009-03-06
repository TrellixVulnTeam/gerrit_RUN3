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
name|rpc
operator|.
name|Common
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
name|ClickListener
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
name|AutoCenterDialogBox
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtjsonrpc
operator|.
name|client
operator|.
name|RemoteJsonException
import|;
end_import

begin_comment
comment|/** A dialog box showing an error message, when bad things happen. */
end_comment

begin_class
DECL|class|ErrorDialog
specifier|public
class|class
name|ErrorDialog
extends|extends
name|AutoCenterDialogBox
block|{
DECL|field|body
specifier|private
specifier|final
name|FlowPanel
name|body
decl_stmt|;
DECL|method|ErrorDialog ()
specifier|protected
name|ErrorDialog
parameter_list|()
block|{
name|super
argument_list|(
comment|/* auto hide */
literal|true
argument_list|,
comment|/* modal */
literal|true
argument_list|)
expr_stmt|;
name|setText
argument_list|(
name|Gerrit
operator|.
name|C
operator|.
name|errorDialogTitle
argument_list|()
argument_list|)
expr_stmt|;
name|body
operator|=
operator|new
name|FlowPanel
argument_list|()
expr_stmt|;
specifier|final
name|FlowPanel
name|buttons
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|buttons
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-ErrorDialog-Buttons"
argument_list|)
expr_stmt|;
specifier|final
name|Button
name|closey
init|=
operator|new
name|Button
argument_list|()
decl_stmt|;
name|closey
operator|.
name|setText
argument_list|(
name|Gerrit
operator|.
name|C
operator|.
name|errorDialogClose
argument_list|()
argument_list|)
expr_stmt|;
name|closey
operator|.
name|addClickListener
argument_list|(
operator|new
name|ClickListener
argument_list|()
block|{
specifier|public
name|void
name|onClick
parameter_list|(
specifier|final
name|Widget
name|sender
parameter_list|)
block|{
name|hide
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|buttons
operator|.
name|add
argument_list|(
name|closey
argument_list|)
expr_stmt|;
specifier|final
name|FlowPanel
name|center
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|center
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-ErrorDialog"
argument_list|)
expr_stmt|;
name|center
operator|.
name|add
argument_list|(
name|body
argument_list|)
expr_stmt|;
name|center
operator|.
name|add
argument_list|(
name|buttons
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|center
argument_list|)
expr_stmt|;
block|}
comment|/** Create a dialog box to show a single message string. */
DECL|method|ErrorDialog (final String message)
specifier|public
name|ErrorDialog
parameter_list|(
specifier|final
name|String
name|message
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|label
argument_list|(
name|message
argument_list|,
literal|"gerrit-ErrorDialog-ErrorMessage"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Create a dialog box to nicely format an exception. */
DECL|method|ErrorDialog (final Throwable what)
specifier|public
name|ErrorDialog
parameter_list|(
specifier|final
name|Throwable
name|what
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|String
name|cn
decl_stmt|;
if|if
condition|(
name|what
operator|instanceof
name|RemoteJsonException
condition|)
block|{
name|cn
operator|=
name|Common
operator|.
name|C
operator|.
name|errorRemoteJsonException
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|cn
operator|=
name|what
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
if|if
condition|(
name|cn
operator|.
name|startsWith
argument_list|(
literal|"java.lang."
argument_list|)
condition|)
block|{
name|cn
operator|=
name|cn
operator|.
name|substring
argument_list|(
literal|"java.lang."
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|body
operator|.
name|add
argument_list|(
name|label
argument_list|(
name|cn
argument_list|,
literal|"gerrit-ErrorDialog-ErrorType"
argument_list|)
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|label
argument_list|(
name|what
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"gerrit-ErrorDialog-ErrorMessage"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|label (final String what, final String style)
specifier|private
specifier|static
name|Label
name|label
parameter_list|(
specifier|final
name|String
name|what
parameter_list|,
specifier|final
name|String
name|style
parameter_list|)
block|{
specifier|final
name|Label
name|r
init|=
operator|new
name|Label
argument_list|(
name|what
argument_list|)
decl_stmt|;
name|r
operator|.
name|setStyleName
argument_list|(
name|style
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
block|}
end_class

end_unit

