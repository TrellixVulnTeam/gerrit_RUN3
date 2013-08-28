begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
name|AccountInfo
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
name|InlineHyperlink
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
name|PluginSafePopupPanel
import|;
end_import

begin_class
DECL|class|UserPopupPanel
specifier|public
class|class
name|UserPopupPanel
extends|extends
name|PluginSafePopupPanel
block|{
DECL|interface|Binder
interface|interface
name|Binder
extends|extends
name|UiBinder
argument_list|<
name|Widget
argument_list|,
name|UserPopupPanel
argument_list|>
block|{   }
DECL|field|binder
specifier|private
specifier|static
specifier|final
name|Binder
name|binder
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
argument_list|(
name|provided
operator|=
literal|true
argument_list|)
DECL|field|avatar
name|AvatarImage
name|avatar
decl_stmt|;
annotation|@
name|UiField
DECL|field|userName
name|Label
name|userName
decl_stmt|;
annotation|@
name|UiField
DECL|field|userEmail
name|Label
name|userEmail
decl_stmt|;
annotation|@
name|UiField
DECL|field|logout
name|Anchor
name|logout
decl_stmt|;
annotation|@
name|UiField
DECL|field|settings
name|InlineHyperlink
name|settings
decl_stmt|;
DECL|method|UserPopupPanel (AccountInfo account, boolean canLogOut, boolean showSettingsLink)
specifier|public
name|UserPopupPanel
parameter_list|(
name|AccountInfo
name|account
parameter_list|,
name|boolean
name|canLogOut
parameter_list|,
name|boolean
name|showSettingsLink
parameter_list|)
block|{
name|super
argument_list|(
comment|/* auto hide */
literal|true
argument_list|,
comment|/* modal */
literal|false
argument_list|)
expr_stmt|;
name|avatar
operator|=
operator|new
name|AvatarImage
argument_list|(
name|account
argument_list|,
literal|100
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|setWidget
argument_list|(
name|binder
operator|.
name|createAndBindUi
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
comment|// We must show and then hide this popup so that it is part of the DOM.
comment|// Otherwise the image does not get any events.  Calling hide() would
comment|// remove it from the DOM so we use setVisible(false) instead.
name|show
argument_list|()
expr_stmt|;
name|setVisible
argument_list|(
literal|false
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
name|userInfoPopup
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|account
operator|.
name|name
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|userName
operator|.
name|setText
argument_list|(
name|account
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|account
operator|.
name|email
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|userEmail
operator|.
name|setText
argument_list|(
name|account
operator|.
name|email
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|canLogOut
condition|)
block|{
name|logout
operator|.
name|setHref
argument_list|(
name|Gerrit
operator|.
name|selfRedirect
argument_list|(
literal|"/logout"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logout
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|showSettingsLink
condition|)
block|{
name|settings
operator|.
name|setTargetHistoryToken
argument_list|(
name|PageLinks
operator|.
name|SETTINGS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|settings
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

