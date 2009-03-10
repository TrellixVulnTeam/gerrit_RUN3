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
name|account
operator|.
name|SignInResult
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
name|account
operator|.
name|SignInResult
operator|.
name|Status
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
name|openid
operator|.
name|OpenIdLoginPanel
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
name|reviewdb
operator|.
name|Account
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
name|Common
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
name|user
operator|.
name|client
operator|.
name|Command
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
name|DeferredCommand
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
name|AsyncCallback
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
name|CallbackHandle
import|;
end_import

begin_comment
comment|/**  * Prompts the user to sign in to their account.  *<p>  * This dialog performs the login within an iframe, allowing normal HTML based  * login pages to be used, including those which aren't served from the same  * server as Gerrit. This is important to permit an OpenID provider or some  * other web based single-sign-on system to be used for authentication.  *<p>  * Post login the iframe content is expected to execute the JavaScript snippet:  *   *<pre>  * $callback(account);  *</pre>  *   * where<code>$callback</code> is the parameter in the initial request and  *<code>account</code> is either<code>!= null</code> (the user is now signed  * in) or<code>null</code> (the sign in was aborted/canceled before it  * completed).  */
end_comment

begin_class
DECL|class|SignInDialog
specifier|public
class|class
name|SignInDialog
extends|extends
name|AutoCenterDialogBox
block|{
DECL|enum|Mode
specifier|public
specifier|static
enum|enum
name|Mode
block|{
DECL|enumConstant|SIGN_IN
DECL|enumConstant|LINK_IDENTIY
name|SIGN_IN
block|,
name|LINK_IDENTIY
block|;   }
DECL|field|current
specifier|private
specifier|static
name|SignInDialog
name|current
decl_stmt|;
DECL|field|mode
specifier|private
specifier|final
name|Mode
name|mode
decl_stmt|;
DECL|field|signInCallback
specifier|private
specifier|final
name|CallbackHandle
argument_list|<
name|SignInResult
argument_list|>
name|signInCallback
decl_stmt|;
DECL|field|appCallback
specifier|private
specifier|final
name|AsyncCallback
argument_list|<
name|?
argument_list|>
name|appCallback
decl_stmt|;
DECL|field|panel
specifier|private
name|Widget
name|panel
decl_stmt|;
comment|/**    * Create a new dialog to handle user sign in.    *     * @param callback optional; onSuccess will be called if sign is completed.    *        This can be used to trigger sending an RPC or some other action.    */
DECL|method|SignInDialog (final AsyncCallback<?> callback)
specifier|public
name|SignInDialog
parameter_list|(
specifier|final
name|AsyncCallback
argument_list|<
name|?
argument_list|>
name|callback
parameter_list|)
block|{
name|this
argument_list|(
name|Mode
operator|.
name|SIGN_IN
argument_list|,
name|callback
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a new dialog to handle user sign in.    *     * @param signInMode type of mode the login will perform.    * @param callback optional; onSuccess will be called if sign is completed.    *        This can be used to trigger sending an RPC or some other action.    */
DECL|method|SignInDialog (final Mode signInMode, final AsyncCallback<?> callback)
specifier|public
name|SignInDialog
parameter_list|(
specifier|final
name|Mode
name|signInMode
parameter_list|,
specifier|final
name|AsyncCallback
argument_list|<
name|?
argument_list|>
name|callback
parameter_list|)
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
name|mode
operator|=
name|signInMode
expr_stmt|;
name|signInCallback
operator|=
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|account
operator|.
name|Util
operator|.
name|LOGIN_SVC
operator|.
name|signIn
argument_list|(
operator|new
name|GerritCallback
argument_list|<
name|SignInResult
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
specifier|final
name|SignInResult
name|result
parameter_list|)
block|{
name|onCallback
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|appCallback
operator|=
name|callback
expr_stmt|;
switch|switch
condition|(
name|Common
operator|.
name|getGerritConfig
argument_list|()
operator|.
name|getLoginType
argument_list|()
condition|)
block|{
case|case
name|OPENID
case|:
name|panel
operator|=
operator|new
name|OpenIdLoginPanel
argument_list|(
name|signInMode
argument_list|,
name|signInCallback
argument_list|)
expr_stmt|;
break|break;
default|default:
block|{
specifier|final
name|FlowPanel
name|fp
init|=
operator|new
name|FlowPanel
argument_list|()
decl_stmt|;
name|fp
operator|.
name|add
argument_list|(
operator|new
name|Label
argument_list|(
name|Gerrit
operator|.
name|C
operator|.
name|loginTypeUnsupported
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|panel
operator|=
name|fp
expr_stmt|;
break|break;
block|}
block|}
name|add
argument_list|(
name|panel
argument_list|)
expr_stmt|;
name|onResize
argument_list|(
name|Window
operator|.
name|getClientWidth
argument_list|()
argument_list|,
name|Window
operator|.
name|getClientHeight
argument_list|()
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|signInMode
condition|)
block|{
case|case
name|LINK_IDENTIY
case|:
name|setText
argument_list|(
name|Gerrit
operator|.
name|C
operator|.
name|linkIdentityDialogTitle
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
name|setText
argument_list|(
name|Gerrit
operator|.
name|C
operator|.
name|signInDialogTitle
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
annotation|@
name|Override
DECL|method|onResize (final int width, final int height)
specifier|protected
name|void
name|onResize
parameter_list|(
specifier|final
name|int
name|width
parameter_list|,
specifier|final
name|int
name|height
parameter_list|)
block|{
name|resizeFrame
argument_list|(
name|width
argument_list|,
name|height
argument_list|)
expr_stmt|;
name|super
operator|.
name|onResize
argument_list|(
name|width
argument_list|,
name|height
argument_list|)
expr_stmt|;
block|}
DECL|method|resizeFrame (final int width, final int height)
specifier|private
name|void
name|resizeFrame
parameter_list|(
specifier|final
name|int
name|width
parameter_list|,
specifier|final
name|int
name|height
parameter_list|)
block|{
specifier|final
name|int
name|w
init|=
name|Math
operator|.
name|min
argument_list|(
literal|630
argument_list|,
name|width
operator|-
literal|15
argument_list|)
decl_stmt|;
specifier|final
name|int
name|h
init|=
name|Math
operator|.
name|min
argument_list|(
literal|460
argument_list|,
name|height
operator|-
literal|60
argument_list|)
decl_stmt|;
name|panel
operator|.
name|setWidth
argument_list|(
name|w
operator|+
literal|"px"
argument_list|)
expr_stmt|;
name|panel
operator|.
name|setHeight
argument_list|(
name|h
operator|+
literal|"px"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|show ()
specifier|public
name|void
name|show
parameter_list|()
block|{
if|if
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
name|current
operator|.
name|hide
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|show
argument_list|()
expr_stmt|;
name|current
operator|=
name|this
expr_stmt|;
name|signInCallback
operator|.
name|install
argument_list|()
expr_stmt|;
if|if
condition|(
name|panel
operator|instanceof
name|OpenIdLoginPanel
condition|)
block|{
operator|(
operator|(
name|OpenIdLoginPanel
operator|)
name|panel
operator|)
operator|.
name|setFocus
argument_list|(
literal|true
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
name|current
operator|==
name|this
condition|)
block|{
name|signInCallback
operator|.
name|cancel
argument_list|()
expr_stmt|;
name|current
operator|=
literal|null
expr_stmt|;
block|}
name|super
operator|.
name|onUnload
argument_list|()
expr_stmt|;
block|}
DECL|method|onCallback (final SignInResult result)
specifier|private
name|void
name|onCallback
parameter_list|(
specifier|final
name|SignInResult
name|result
parameter_list|)
block|{
specifier|final
name|Status
name|rc
init|=
name|result
operator|.
name|getStatus
argument_list|()
decl_stmt|;
if|if
condition|(
name|rc
operator|==
name|SignInResult
operator|.
name|Status
operator|.
name|CANCEL
condition|)
block|{
name|hide
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|rc
operator|==
name|SignInResult
operator|.
name|Status
operator|.
name|SUCCESS
condition|)
block|{
name|onSuccess
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|GWT
operator|.
name|log
argument_list|(
literal|"Unexpected SignInResult.Status "
operator|+
name|rc
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|onSuccess (final SignInResult result)
specifier|private
name|void
name|onSuccess
parameter_list|(
specifier|final
name|SignInResult
name|result
parameter_list|)
block|{
specifier|final
name|AsyncCallback
argument_list|<
name|?
argument_list|>
name|ac
init|=
name|appCallback
decl_stmt|;
switch|switch
condition|(
name|mode
condition|)
block|{
default|default:
case|case
name|LINK_IDENTIY
case|:
name|hide
argument_list|()
expr_stmt|;
if|if
condition|(
name|ac
operator|!=
literal|null
condition|)
block|{
name|DeferredCommand
operator|.
name|addCommand
argument_list|(
operator|new
name|Command
argument_list|()
block|{
specifier|public
name|void
name|execute
parameter_list|()
block|{
name|ac
operator|.
name|onSuccess
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|SIGN_IN
case|:
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|account
operator|.
name|Util
operator|.
name|ACCOUNT_SVC
operator|.
name|myAccount
argument_list|(
operator|new
name|GerritCallback
argument_list|<
name|Account
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|onSuccess
parameter_list|(
specifier|final
name|Account
name|result
parameter_list|)
block|{
name|DeferredCommand
operator|.
name|addCommand
argument_list|(
operator|new
name|Command
argument_list|()
block|{
specifier|public
name|void
name|execute
parameter_list|()
block|{
name|hide
argument_list|()
expr_stmt|;
name|Gerrit
operator|.
name|postSignIn
argument_list|(
name|result
argument_list|,
name|ac
argument_list|)
expr_stmt|;
block|}
block|}
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
name|hide
argument_list|()
expr_stmt|;
name|super
operator|.
name|onFailure
argument_list|(
name|caught
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
end_class

end_unit

