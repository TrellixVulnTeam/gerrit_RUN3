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
DECL|package|com.google.gerrit.client.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|account
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
name|VoidResult
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
name|rpc
operator|.
name|NativeString
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
name|ScreenLoadCallback
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
name|i18n
operator|.
name|client
operator|.
name|LocaleInfo
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
name|Anchor
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
name|clippy
operator|.
name|client
operator|.
name|CopyableLabel
import|;
end_import

begin_class
DECL|class|MyPasswordScreen
specifier|public
class|class
name|MyPasswordScreen
extends|extends
name|SettingsScreen
block|{
DECL|field|password
specifier|private
name|CopyableLabel
name|password
decl_stmt|;
DECL|field|generatePassword
specifier|private
name|Button
name|generatePassword
decl_stmt|;
DECL|field|clearPassword
specifier|private
name|Button
name|clearPassword
decl_stmt|;
annotation|@
name|Override
DECL|method|onInitUI ()
specifier|protected
name|void
name|onInitUI
parameter_list|()
block|{
name|super
operator|.
name|onInitUI
argument_list|()
expr_stmt|;
name|String
name|url
init|=
name|Gerrit
operator|.
name|info
argument_list|()
operator|.
name|auth
argument_list|()
operator|.
name|httpPasswordUrl
argument_list|()
decl_stmt|;
if|if
condition|(
name|url
operator|!=
literal|null
condition|)
block|{
name|Anchor
name|link
init|=
operator|new
name|Anchor
argument_list|()
decl_stmt|;
name|link
operator|.
name|setText
argument_list|(
name|Util
operator|.
name|C
operator|.
name|linkObtainPassword
argument_list|()
argument_list|)
expr_stmt|;
name|link
operator|.
name|setHref
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|link
operator|.
name|setTarget
argument_list|(
literal|"_blank"
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|link
argument_list|)
expr_stmt|;
return|return;
block|}
name|password
operator|=
operator|new
name|CopyableLabel
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|password
operator|.
name|addStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|accountPassword
argument_list|()
argument_list|)
expr_stmt|;
name|generatePassword
operator|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonGeneratePassword
argument_list|()
argument_list|)
expr_stmt|;
name|generatePassword
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
name|doGeneratePassword
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|clearPassword
operator|=
operator|new
name|Button
argument_list|(
name|Util
operator|.
name|C
operator|.
name|buttonClearPassword
argument_list|()
argument_list|)
expr_stmt|;
name|clearPassword
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
name|doClearPassword
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|Grid
name|userInfo
init|=
operator|new
name|Grid
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|CellFormatter
name|fmt
init|=
name|userInfo
operator|.
name|getCellFormatter
argument_list|()
decl_stmt|;
name|userInfo
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
name|infoBlock
argument_list|()
argument_list|)
expr_stmt|;
name|userInfo
operator|.
name|addStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|accountInfoBlock
argument_list|()
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|userInfo
argument_list|)
expr_stmt|;
name|row
argument_list|(
name|userInfo
argument_list|,
literal|0
argument_list|,
name|Util
operator|.
name|C
operator|.
name|userName
argument_list|()
argument_list|,
operator|new
name|UsernameField
argument_list|()
argument_list|)
expr_stmt|;
name|row
argument_list|(
name|userInfo
argument_list|,
literal|1
argument_list|,
name|Util
operator|.
name|C
operator|.
name|password
argument_list|()
argument_list|,
name|password
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|topmost
argument_list|()
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|topmost
argument_list|()
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|bottomheader
argument_list|()
argument_list|)
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
name|add
argument_list|(
name|generatePassword
argument_list|)
expr_stmt|;
name|buttons
operator|.
name|add
argument_list|(
name|clearPassword
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|buttons
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
name|password
operator|==
literal|null
condition|)
block|{
name|display
argument_list|()
expr_stmt|;
return|return;
block|}
name|enableUI
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|AccountApi
operator|.
name|getUsername
argument_list|(
literal|"self"
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|NativeString
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|NativeString
name|user
parameter_list|)
block|{
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
operator|.
name|setUserName
argument_list|(
name|user
operator|.
name|asString
argument_list|()
argument_list|)
expr_stmt|;
name|refreshHttpPassword
argument_list|()
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
if|if
condition|(
name|RestApi
operator|.
name|isNotFound
argument_list|(
name|caught
argument_list|)
condition|)
block|{
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
operator|.
name|setUserName
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|display
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|onFailure
argument_list|(
name|caught
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|refreshHttpPassword ()
specifier|private
name|void
name|refreshHttpPassword
parameter_list|()
block|{
name|AccountApi
operator|.
name|getHttpPassword
argument_list|(
literal|"self"
argument_list|,
operator|new
name|ScreenLoadCallback
argument_list|<
name|NativeString
argument_list|>
argument_list|(
name|this
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|preDisplay
parameter_list|(
name|NativeString
name|httpPassword
parameter_list|)
block|{
name|display
argument_list|(
name|httpPassword
operator|.
name|asString
argument_list|()
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
if|if
condition|(
name|RestApi
operator|.
name|isNotFound
argument_list|(
name|caught
argument_list|)
condition|)
block|{
name|display
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|display
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|onFailure
argument_list|(
name|caught
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|display (String pass)
specifier|private
name|void
name|display
parameter_list|(
name|String
name|pass
parameter_list|)
block|{
name|password
operator|.
name|setText
argument_list|(
name|pass
operator|!=
literal|null
condition|?
name|pass
else|:
literal|""
argument_list|)
expr_stmt|;
name|password
operator|.
name|setVisible
argument_list|(
name|pass
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|enableUI
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|row (final Grid info, final int row, final String name, final Widget field)
specifier|private
name|void
name|row
parameter_list|(
specifier|final
name|Grid
name|info
parameter_list|,
specifier|final
name|int
name|row
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|Widget
name|field
parameter_list|)
block|{
specifier|final
name|CellFormatter
name|fmt
init|=
name|info
operator|.
name|getCellFormatter
argument_list|()
decl_stmt|;
if|if
condition|(
name|LocaleInfo
operator|.
name|getCurrentLocale
argument_list|()
operator|.
name|isRTL
argument_list|()
condition|)
block|{
name|info
operator|.
name|setText
argument_list|(
name|row
argument_list|,
literal|1
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|info
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
literal|0
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
literal|1
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|header
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|info
operator|.
name|setText
argument_list|(
name|row
argument_list|,
literal|0
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|info
operator|.
name|setWidget
argument_list|(
name|row
argument_list|,
literal|1
argument_list|,
name|field
argument_list|)
expr_stmt|;
name|fmt
operator|.
name|addStyleName
argument_list|(
name|row
argument_list|,
literal|0
argument_list|,
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|header
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doGeneratePassword ()
specifier|private
name|void
name|doGeneratePassword
parameter_list|()
block|{
if|if
condition|(
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
operator|.
name|getUserName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|enableUI
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|AccountApi
operator|.
name|generateHttpPassword
argument_list|(
literal|"self"
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|NativeString
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|NativeString
name|newPassword
parameter_list|)
block|{
name|display
argument_list|(
name|newPassword
operator|.
name|asString
argument_list|()
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
name|enableUI
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doClearPassword ()
specifier|private
name|void
name|doClearPassword
parameter_list|()
block|{
if|if
condition|(
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
operator|.
name|getUserName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|enableUI
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|AccountApi
operator|.
name|clearHttpPassword
argument_list|(
literal|"self"
argument_list|,
operator|new
name|GerritCallback
argument_list|<
name|VoidResult
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|VoidResult
name|result
parameter_list|)
block|{
name|display
argument_list|(
literal|null
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
name|enableUI
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|enableUI (boolean on)
specifier|private
name|void
name|enableUI
parameter_list|(
name|boolean
name|on
parameter_list|)
block|{
name|on
operator|&=
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
operator|.
name|getUserName
argument_list|()
operator|!=
literal|null
expr_stmt|;
name|generatePassword
operator|.
name|setEnabled
argument_list|(
name|on
argument_list|)
expr_stmt|;
name|clearPassword
operator|.
name|setVisible
argument_list|(
name|on
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|password
operator|.
name|getText
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

