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
name|RestApi
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
name|rpc
operator|.
name|RpcConstants
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
name|http
operator|.
name|client
operator|.
name|Response
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
name|Window
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
name|rpc
operator|.
name|StatusCodeException
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
name|safehtml
operator|.
name|client
operator|.
name|SafeHtml
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
name|PluginSafePopupPanel
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
name|PluginSafePopupPanel
block|{
DECL|field|text
specifier|private
specifier|final
name|Label
name|text
decl_stmt|;
DECL|field|body
specifier|private
specifier|final
name|FlowPanel
name|body
decl_stmt|;
DECL|field|closey
specifier|private
specifier|final
name|Button
name|closey
decl_stmt|;
DECL|method|ErrorDialog ()
specifier|protected
name|ErrorDialog
parameter_list|()
block|{
name|super
argument_list|(
comment|/* auto hide */
literal|false
argument_list|,
comment|/* modal */
literal|true
argument_list|)
expr_stmt|;
name|setGlassEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|getGlassElement
argument_list|()
operator|.
name|addClassName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|errorDialogGlass
argument_list|()
argument_list|)
expr_stmt|;
name|text
operator|=
operator|new
name|Label
argument_list|()
expr_stmt|;
name|text
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
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|errorDialogButtons
argument_list|()
argument_list|)
expr_stmt|;
name|closey
operator|=
operator|new
name|Button
argument_list|()
expr_stmt|;
name|closey
operator|.
name|setText
argument_list|(
name|Gerrit
operator|.
name|C
operator|.
name|errorDialogContinue
argument_list|()
argument_list|)
expr_stmt|;
name|closey
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
name|hide
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|closey
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
name|KeyPressEvent
name|event
parameter_list|)
block|{
comment|// if the close button is triggered by a key we need to consume the key
comment|// event, otherwise the key event would be propagated to the parent
comment|// screen and eventually trigger some unwanted action there after the
comment|// error dialog was closed
name|event
operator|.
name|stopPropagation
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
name|add
argument_list|(
name|text
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
name|addStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|errorDialog
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|center
argument_list|)
expr_stmt|;
name|int
name|l
init|=
name|Window
operator|.
name|getScrollLeft
argument_list|()
operator|+
literal|20
decl_stmt|;
name|int
name|t
init|=
name|Window
operator|.
name|getScrollTop
argument_list|()
operator|+
literal|20
decl_stmt|;
name|setPopupPosition
argument_list|(
name|l
argument_list|,
name|t
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
operator|new
name|Label
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Create a dialog box to show a single message string. */
DECL|method|ErrorDialog (final SafeHtml message)
specifier|public
name|ErrorDialog
parameter_list|(
specifier|final
name|SafeHtml
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
name|message
operator|.
name|toBlockWidget
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|ErrorDialog (final Widget w)
specifier|public
name|ErrorDialog
parameter_list|(
specifier|final
name|Widget
name|w
parameter_list|)
block|{
name|this
argument_list|()
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|w
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
name|hdr
decl_stmt|;
name|String
name|msg
decl_stmt|;
if|if
condition|(
name|what
operator|instanceof
name|StatusCodeException
condition|)
block|{
name|StatusCodeException
name|sc
init|=
operator|(
name|StatusCodeException
operator|)
name|what
decl_stmt|;
if|if
condition|(
name|RestApi
operator|.
name|isExpected
argument_list|(
name|sc
operator|.
name|getStatusCode
argument_list|()
argument_list|)
condition|)
block|{
name|hdr
operator|=
literal|null
expr_stmt|;
name|msg
operator|=
name|sc
operator|.
name|getEncodedResponse
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sc
operator|.
name|getStatusCode
argument_list|()
operator|==
name|Response
operator|.
name|SC_INTERNAL_SERVER_ERROR
condition|)
block|{
name|hdr
operator|=
literal|null
expr_stmt|;
name|msg
operator|=
name|what
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|hdr
operator|=
name|RpcConstants
operator|.
name|C
operator|.
name|errorServerUnavailable
argument_list|()
expr_stmt|;
name|msg
operator|=
name|what
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|what
operator|instanceof
name|RemoteJsonException
condition|)
block|{
comment|// TODO Remove RemoteJsonException from Gerrit sources.
name|hdr
operator|=
name|RpcConstants
operator|.
name|C
operator|.
name|errorRemoteJsonException
argument_list|()
expr_stmt|;
name|msg
operator|=
name|what
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// TODO Fix callers of ErrorDialog to stop passing random types.
name|hdr
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
name|hdr
operator|.
name|startsWith
argument_list|(
literal|"java.lang."
argument_list|)
condition|)
block|{
name|hdr
operator|=
name|hdr
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
elseif|else
if|if
condition|(
name|hdr
operator|.
name|startsWith
argument_list|(
literal|"com.google.gerrit."
argument_list|)
condition|)
block|{
name|hdr
operator|=
name|hdr
operator|.
name|substring
argument_list|(
name|hdr
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|hdr
operator|.
name|endsWith
argument_list|(
literal|"Exception"
argument_list|)
condition|)
block|{
name|hdr
operator|=
name|hdr
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|hdr
operator|.
name|length
argument_list|()
operator|-
literal|"Exception"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|hdr
operator|.
name|endsWith
argument_list|(
literal|"Error"
argument_list|)
condition|)
block|{
name|hdr
operator|=
name|hdr
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|hdr
operator|.
name|length
argument_list|()
operator|-
literal|"Error"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|msg
operator|=
name|what
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|hdr
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Label
name|r
init|=
operator|new
name|Label
argument_list|(
name|hdr
argument_list|)
decl_stmt|;
name|r
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
name|errorDialogErrorType
argument_list|()
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Label
name|m
init|=
operator|new
name|Label
argument_list|(
name|msg
argument_list|)
decl_stmt|;
name|m
operator|.
name|getElement
argument_list|()
operator|.
name|getStyle
argument_list|()
operator|.
name|setProperty
argument_list|(
literal|"whiteSpace"
argument_list|,
literal|"pre"
argument_list|)
expr_stmt|;
name|body
operator|.
name|add
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setText (final String t)
specifier|public
name|void
name|setText
parameter_list|(
specifier|final
name|String
name|t
parameter_list|)
block|{
name|text
operator|.
name|setText
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|center ()
specifier|public
name|void
name|center
parameter_list|()
block|{
name|show
argument_list|()
expr_stmt|;
name|closey
operator|.
name|setFocus
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

