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
DECL|package|com.google.gerrit.client.openid
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|openid
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
name|SignInDialog
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
name|GerritCallback
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
name|SmallHeading
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
name|FormElement
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
name|Cookies
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
name|Event
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
name|ui
operator|.
name|AbstractImagePrototype
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
name|CheckBox
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
name|FormHandler
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
name|FormPanel
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
name|FormSubmitCompleteEvent
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
name|FormSubmitEvent
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
name|HTML
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
name|Hidden
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
name|KeyboardListenerAdapter
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
name|TextBox
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
name|gwtjsonrpc
operator|.
name|client
operator|.
name|CallbackHandle
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
DECL|class|OpenIdLoginPanel
specifier|public
class|class
name|OpenIdLoginPanel
extends|extends
name|Composite
implements|implements
name|FormHandler
block|{
DECL|field|mode
specifier|private
specifier|final
name|SignInDialog
operator|.
name|Mode
name|mode
decl_stmt|;
DECL|field|icons
specifier|private
specifier|final
name|LoginIcons
name|icons
decl_stmt|;
DECL|field|panelWidget
specifier|private
specifier|final
name|FlowPanel
name|panelWidget
decl_stmt|;
DECL|field|form
specifier|private
specifier|final
name|FormPanel
name|form
decl_stmt|;
DECL|field|formBody
specifier|private
specifier|final
name|FlowPanel
name|formBody
decl_stmt|;
DECL|field|redirectForm
specifier|private
specifier|final
name|FormPanel
name|redirectForm
decl_stmt|;
DECL|field|redirectBody
specifier|private
specifier|final
name|FlowPanel
name|redirectBody
decl_stmt|;
DECL|field|errorLine
specifier|private
name|FlowPanel
name|errorLine
decl_stmt|;
DECL|field|errorMsg
specifier|private
name|InlineLabel
name|errorMsg
decl_stmt|;
DECL|field|login
specifier|private
name|Button
name|login
decl_stmt|;
DECL|field|providerId
specifier|private
name|TextBox
name|providerId
decl_stmt|;
DECL|field|rememberId
specifier|private
name|CheckBox
name|rememberId
decl_stmt|;
DECL|field|discovering
specifier|private
name|boolean
name|discovering
decl_stmt|;
DECL|method|OpenIdLoginPanel (final SignInDialog.Mode m, final CallbackHandle<?> sc)
specifier|public
name|OpenIdLoginPanel
parameter_list|(
specifier|final
name|SignInDialog
operator|.
name|Mode
name|m
parameter_list|,
specifier|final
name|CallbackHandle
argument_list|<
name|?
argument_list|>
name|sc
parameter_list|)
block|{
name|mode
operator|=
name|m
expr_stmt|;
name|icons
operator|=
name|GWT
operator|.
name|create
argument_list|(
name|LoginIcons
operator|.
name|class
argument_list|)
expr_stmt|;
name|formBody
operator|=
operator|new
name|FlowPanel
argument_list|()
expr_stmt|;
name|formBody
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-OpenID-loginform"
argument_list|)
expr_stmt|;
name|form
operator|=
operator|new
name|FormPanel
argument_list|()
expr_stmt|;
name|form
operator|.
name|setMethod
argument_list|(
name|FormPanel
operator|.
name|METHOD_GET
argument_list|)
expr_stmt|;
name|form
operator|.
name|addFormHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|form
operator|.
name|add
argument_list|(
name|formBody
argument_list|)
expr_stmt|;
name|redirectBody
operator|=
operator|new
name|FlowPanel
argument_list|()
expr_stmt|;
name|redirectBody
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|redirectForm
operator|=
operator|new
name|FormPanel
argument_list|()
expr_stmt|;
name|redirectForm
operator|.
name|add
argument_list|(
name|redirectBody
argument_list|)
expr_stmt|;
name|panelWidget
operator|=
operator|new
name|FlowPanel
argument_list|()
expr_stmt|;
name|panelWidget
operator|.
name|add
argument_list|(
name|form
argument_list|)
expr_stmt|;
name|panelWidget
operator|.
name|add
argument_list|(
name|redirectForm
argument_list|)
expr_stmt|;
name|initWidget
argument_list|(
name|panelWidget
argument_list|)
expr_stmt|;
name|createHeaderLogo
argument_list|()
expr_stmt|;
name|createHeaderText
argument_list|()
expr_stmt|;
name|createErrorBox
argument_list|()
expr_stmt|;
name|createIdentBox
argument_list|()
expr_stmt|;
name|link
argument_list|(
name|OpenIdUtil
operator|.
name|URL_GOOGLE
argument_list|,
name|OpenIdUtil
operator|.
name|C
operator|.
name|nameGoogle
argument_list|()
argument_list|,
name|icons
operator|.
name|iconGoogle
argument_list|()
argument_list|)
expr_stmt|;
name|link
argument_list|(
name|OpenIdUtil
operator|.
name|URL_YAHOO
argument_list|,
name|OpenIdUtil
operator|.
name|C
operator|.
name|nameYahoo
argument_list|()
argument_list|,
name|icons
operator|.
name|iconYahoo
argument_list|()
argument_list|)
expr_stmt|;
name|formBody
operator|.
name|add
argument_list|(
operator|new
name|HTML
argument_list|(
name|OpenIdUtil
operator|.
name|C
operator|.
name|whatIsOpenIDHtml
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setFocus (final boolean take)
specifier|public
name|void
name|setFocus
parameter_list|(
specifier|final
name|boolean
name|take
parameter_list|)
block|{
if|if
condition|(
name|take
condition|)
block|{
name|providerId
operator|.
name|selectAll
argument_list|()
expr_stmt|;
block|}
name|providerId
operator|.
name|setFocus
argument_list|(
name|take
argument_list|)
expr_stmt|;
block|}
DECL|method|createHeaderLogo ()
specifier|private
name|void
name|createHeaderLogo
parameter_list|()
block|{
specifier|final
name|FlowPanel
name|headerLogo
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|headerLogo
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-OpenID-logobox"
argument_list|)
expr_stmt|;
name|headerLogo
operator|.
name|add
argument_list|(
name|icons
operator|.
name|openidLogo
argument_list|()
operator|.
name|createImage
argument_list|()
argument_list|)
expr_stmt|;
name|formBody
operator|.
name|add
argument_list|(
name|headerLogo
argument_list|)
expr_stmt|;
block|}
DECL|method|createHeaderText ()
specifier|private
name|void
name|createHeaderText
parameter_list|()
block|{
specifier|final
name|FlowPanel
name|headerText
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
specifier|final
name|String
name|me
init|=
name|Window
operator|.
name|Location
operator|.
name|getHostName
argument_list|()
decl_stmt|;
specifier|final
name|SmallHeading
name|headerLabel
init|=
operator|new
name|SmallHeading
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|LINK_IDENTIY
case|:
name|headerLabel
operator|.
name|setText
argument_list|(
name|OpenIdUtil
operator|.
name|M
operator|.
name|linkAt
argument_list|(
name|me
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|SIGN_IN
case|:
default|default:
name|headerLabel
operator|.
name|setText
argument_list|(
name|OpenIdUtil
operator|.
name|M
operator|.
name|signInAt
argument_list|(
name|me
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
name|headerText
operator|.
name|add
argument_list|(
name|headerLabel
argument_list|)
expr_stmt|;
name|formBody
operator|.
name|add
argument_list|(
name|headerText
argument_list|)
expr_stmt|;
block|}
DECL|method|createErrorBox ()
specifier|private
name|void
name|createErrorBox
parameter_list|()
block|{
name|errorLine
operator|=
operator|new
name|FlowPanel
argument_list|()
expr_stmt|;
name|errorLine
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|errorMsg
operator|=
operator|new
name|InlineLabel
argument_list|(
name|OpenIdUtil
operator|.
name|C
operator|.
name|notSupported
argument_list|()
argument_list|)
expr_stmt|;
name|errorLine
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-OpenID-errorline"
argument_list|)
expr_stmt|;
name|errorLine
operator|.
name|add
argument_list|(
name|errorMsg
argument_list|)
expr_stmt|;
name|formBody
operator|.
name|add
argument_list|(
name|errorLine
argument_list|)
expr_stmt|;
block|}
DECL|method|showError ()
specifier|private
name|void
name|showError
parameter_list|()
block|{
name|errorLine
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|hideError ()
specifier|private
name|void
name|hideError
parameter_list|()
block|{
name|errorLine
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|createIdentBox ()
specifier|private
name|void
name|createIdentBox
parameter_list|()
block|{
specifier|final
name|FlowPanel
name|group
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|group
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-OpenID-loginline"
argument_list|)
expr_stmt|;
specifier|final
name|FlowPanel
name|line1
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|group
operator|.
name|add
argument_list|(
name|line1
argument_list|)
expr_stmt|;
name|providerId
operator|=
operator|new
name|TextBox
argument_list|()
expr_stmt|;
name|providerId
operator|.
name|setVisibleLength
argument_list|(
literal|60
argument_list|)
expr_stmt|;
name|providerId
operator|.
name|setStyleName
argument_list|(
literal|"gerrit-OpenID-openid_identifier"
argument_list|)
expr_stmt|;
name|providerId
operator|.
name|setTabIndex
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|providerId
operator|.
name|addKeyboardListener
argument_list|(
operator|new
name|KeyboardListenerAdapter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onKeyPress
parameter_list|(
name|Widget
name|sender
parameter_list|,
name|char
name|keyCode
parameter_list|,
name|int
name|modifiers
parameter_list|)
block|{
if|if
condition|(
name|keyCode
operator|==
name|KEY_ENTER
condition|)
block|{
specifier|final
name|Event
name|event
init|=
name|DOM
operator|.
name|eventGetCurrentEvent
argument_list|()
decl_stmt|;
name|DOM
operator|.
name|eventCancelBubble
argument_list|(
name|event
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|DOM
operator|.
name|eventPreventDefault
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|form
operator|.
name|submit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|line1
operator|.
name|add
argument_list|(
name|providerId
argument_list|)
expr_stmt|;
name|login
operator|=
operator|new
name|Button
argument_list|()
expr_stmt|;
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|LINK_IDENTIY
case|:
name|login
operator|.
name|setText
argument_list|(
name|OpenIdUtil
operator|.
name|C
operator|.
name|buttonLinkId
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|SIGN_IN
case|:
default|default:
name|login
operator|.
name|setText
argument_list|(
name|OpenIdUtil
operator|.
name|C
operator|.
name|buttonSignIn
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
name|login
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
name|Widget
name|sender
parameter_list|)
block|{
name|form
operator|.
name|submit
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|login
operator|.
name|setTabIndex
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|line1
operator|.
name|add
argument_list|(
name|login
argument_list|)
expr_stmt|;
if|if
condition|(
name|mode
operator|==
name|SignInDialog
operator|.
name|Mode
operator|.
name|SIGN_IN
condition|)
block|{
name|rememberId
operator|=
operator|new
name|CheckBox
argument_list|(
name|OpenIdUtil
operator|.
name|C
operator|.
name|rememberMe
argument_list|()
argument_list|)
expr_stmt|;
name|rememberId
operator|.
name|setTabIndex
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|group
operator|.
name|add
argument_list|(
name|rememberId
argument_list|)
expr_stmt|;
specifier|final
name|String
name|last
init|=
name|Cookies
operator|.
name|getCookie
argument_list|(
name|OpenIdUtil
operator|.
name|LASTID_COOKIE
argument_list|)
decl_stmt|;
if|if
condition|(
name|last
operator|!=
literal|null
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|last
argument_list|)
condition|)
block|{
name|providerId
operator|.
name|setText
argument_list|(
name|last
argument_list|)
expr_stmt|;
name|rememberId
operator|.
name|setChecked
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
name|formBody
operator|.
name|add
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
DECL|method|link (final String identUrl, final String who, final AbstractImagePrototype icon)
specifier|private
name|void
name|link
parameter_list|(
specifier|final
name|String
name|identUrl
parameter_list|,
specifier|final
name|String
name|who
parameter_list|,
specifier|final
name|AbstractImagePrototype
name|icon
parameter_list|)
block|{
specifier|final
name|ClickListener
name|i
init|=
operator|new
name|ClickListener
argument_list|()
block|{
specifier|public
name|void
name|onClick
parameter_list|(
name|Widget
name|sender
parameter_list|)
block|{
if|if
condition|(
operator|!
name|discovering
condition|)
block|{
name|providerId
operator|.
name|setText
argument_list|(
name|identUrl
argument_list|)
expr_stmt|;
name|form
operator|.
name|submit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
specifier|final
name|FlowPanel
name|line
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|line
operator|.
name|addStyleName
argument_list|(
literal|"gerrit-OpenID-directlink"
argument_list|)
expr_stmt|;
specifier|final
name|Image
name|img
init|=
name|icon
operator|.
name|createImage
argument_list|()
decl_stmt|;
name|img
operator|.
name|addClickListener
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|line
operator|.
name|add
argument_list|(
name|img
argument_list|)
expr_stmt|;
specifier|final
name|InlineLabel
name|lbl
init|=
operator|new
name|InlineLabel
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|LINK_IDENTIY
case|:
name|lbl
operator|.
name|setText
argument_list|(
name|OpenIdUtil
operator|.
name|M
operator|.
name|linkWith
argument_list|(
name|who
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|SIGN_IN
case|:
default|default:
name|lbl
operator|.
name|setText
argument_list|(
name|OpenIdUtil
operator|.
name|M
operator|.
name|signInWith
argument_list|(
name|who
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
name|lbl
operator|.
name|addClickListener
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|line
operator|.
name|add
argument_list|(
name|lbl
argument_list|)
expr_stmt|;
name|formBody
operator|.
name|add
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
DECL|method|enable (final boolean on)
specifier|private
name|void
name|enable
parameter_list|(
specifier|final
name|boolean
name|on
parameter_list|)
block|{
name|providerId
operator|.
name|setEnabled
argument_list|(
name|on
argument_list|)
expr_stmt|;
name|login
operator|.
name|setEnabled
argument_list|(
name|on
argument_list|)
expr_stmt|;
block|}
DECL|method|onDiscovery (final DiscoveryResult result)
specifier|private
name|void
name|onDiscovery
parameter_list|(
specifier|final
name|DiscoveryResult
name|result
parameter_list|)
block|{
name|discovering
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|result
operator|.
name|validProvider
condition|)
block|{
specifier|final
name|String
name|url
init|=
name|providerId
operator|.
name|getText
argument_list|()
decl_stmt|;
name|redirectForm
operator|.
name|setMethod
argument_list|(
name|FormPanel
operator|.
name|METHOD_POST
argument_list|)
expr_stmt|;
name|redirectForm
operator|.
name|setAction
argument_list|(
name|result
operator|.
name|providerUrl
argument_list|)
expr_stmt|;
name|redirectBody
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|e
range|:
name|result
operator|.
name|providerArgs
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|redirectBody
operator|.
name|add
argument_list|(
operator|new
name|Hidden
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// The provider won't support operation inside an IFRAME, so we
comment|// replace our entire application. No fancy waits are needed,
comment|// the browser won't update anything until its started to load
comment|// the provider's page.
comment|//
name|FormElement
operator|.
name|as
argument_list|(
name|redirectForm
operator|.
name|getElement
argument_list|()
argument_list|)
operator|.
name|setTarget
argument_list|(
literal|"_top"
argument_list|)
expr_stmt|;
name|redirectForm
operator|.
name|submit
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// We failed discovery. We have to use a deferred command here
comment|// as we are being called from within an invisible IFRAME. Jump
comment|// back to the main event loop in the parent window.
comment|//
name|onDiscoveryFailure
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|onDiscoveryFailure ()
specifier|private
name|void
name|onDiscoveryFailure
parameter_list|()
block|{
name|showError
argument_list|()
expr_stmt|;
name|enable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|providerId
operator|.
name|selectAll
argument_list|()
expr_stmt|;
name|providerId
operator|.
name|setFocus
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|onSubmit (final FormSubmitEvent event)
specifier|public
name|void
name|onSubmit
parameter_list|(
specifier|final
name|FormSubmitEvent
name|event
parameter_list|)
block|{
name|event
operator|.
name|setCancelled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|String
name|openidIdentifier
init|=
name|providerId
operator|.
name|getText
argument_list|()
decl_stmt|;
if|if
condition|(
name|openidIdentifier
operator|==
literal|null
operator|||
name|openidIdentifier
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|enable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return;
block|}
name|discovering
operator|=
literal|true
expr_stmt|;
name|enable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|hideError
argument_list|()
expr_stmt|;
specifier|final
name|boolean
name|remember
init|=
name|rememberId
operator|!=
literal|null
operator|&&
name|rememberId
operator|.
name|isChecked
argument_list|()
decl_stmt|;
specifier|final
name|String
name|token
init|=
name|History
operator|.
name|getToken
argument_list|()
decl_stmt|;
name|OpenIdUtil
operator|.
name|SVC
operator|.
name|discover
argument_list|(
name|openidIdentifier
argument_list|,
name|mode
argument_list|,
name|remember
argument_list|,
name|token
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|DiscoveryResult
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
specifier|final
name|DiscoveryResult
name|result
parameter_list|)
block|{
name|onDiscovery
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
specifier|final
name|Throwable
name|caught
parameter_list|)
block|{
name|super
operator|.
name|onFailure
argument_list|(
name|caught
argument_list|)
expr_stmt|;
name|onDiscoveryFailure
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|onSubmitComplete (final FormSubmitCompleteEvent event)
specifier|public
name|void
name|onSubmitComplete
parameter_list|(
specifier|final
name|FormSubmitCompleteEvent
name|event
parameter_list|)
block|{   }
block|}
end_class

end_unit

