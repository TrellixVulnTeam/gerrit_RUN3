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
DECL|package|com.google.gerrit.client.download
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|download
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
name|reviewdb
operator|.
name|client
operator|.
name|AccountGeneralPreferences
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
name|AccountGeneralPreferences
operator|.
name|DownloadCommand
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
name|aria
operator|.
name|client
operator|.
name|Roles
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtjsonrpc
operator|.
name|common
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
name|gwtjsonrpc
operator|.
name|common
operator|.
name|VoidResult
import|;
end_import

begin_class
DECL|class|DownloadCommandLink
specifier|public
specifier|abstract
class|class
name|DownloadCommandLink
extends|extends
name|Anchor
implements|implements
name|ClickHandler
block|{
DECL|class|CopyableCommandLinkFactory
specifier|public
specifier|static
class|class
name|CopyableCommandLinkFactory
block|{
DECL|field|copyLabel
specifier|protected
name|CopyableLabel
name|copyLabel
init|=
literal|null
decl_stmt|;
DECL|field|widget
specifier|protected
name|Widget
name|widget
decl_stmt|;
DECL|class|CheckoutCommandLink
specifier|public
class|class
name|CheckoutCommandLink
extends|extends
name|DownloadCommandLink
block|{
DECL|method|CheckoutCommandLink ()
specifier|public
name|CheckoutCommandLink
parameter_list|()
block|{
name|super
argument_list|(
name|DownloadCommand
operator|.
name|CHECKOUT
argument_list|,
literal|"checkout"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setCurrentUrl (DownloadUrlLink link)
specifier|protected
name|void
name|setCurrentUrl
parameter_list|(
name|DownloadUrlLink
name|link
parameter_list|)
block|{
name|widget
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|copyLabel
operator|.
name|setText
argument_list|(
literal|"git fetch "
operator|+
name|link
operator|.
name|getUrlData
argument_list|()
operator|+
literal|"&& git checkout FETCH_HEAD"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|PullCommandLink
specifier|public
class|class
name|PullCommandLink
extends|extends
name|DownloadCommandLink
block|{
DECL|method|PullCommandLink ()
specifier|public
name|PullCommandLink
parameter_list|()
block|{
name|super
argument_list|(
name|DownloadCommand
operator|.
name|PULL
argument_list|,
literal|"pull"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setCurrentUrl (DownloadUrlLink link)
specifier|protected
name|void
name|setCurrentUrl
parameter_list|(
name|DownloadUrlLink
name|link
parameter_list|)
block|{
name|widget
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|copyLabel
operator|.
name|setText
argument_list|(
literal|"git pull "
operator|+
name|link
operator|.
name|getUrlData
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|CherryPickCommandLink
specifier|public
class|class
name|CherryPickCommandLink
extends|extends
name|DownloadCommandLink
block|{
DECL|method|CherryPickCommandLink ()
specifier|public
name|CherryPickCommandLink
parameter_list|()
block|{
name|super
argument_list|(
name|DownloadCommand
operator|.
name|CHERRY_PICK
argument_list|,
literal|"cherry-pick"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setCurrentUrl (DownloadUrlLink link)
specifier|protected
name|void
name|setCurrentUrl
parameter_list|(
name|DownloadUrlLink
name|link
parameter_list|)
block|{
name|widget
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|copyLabel
operator|.
name|setText
argument_list|(
literal|"git fetch "
operator|+
name|link
operator|.
name|getUrlData
argument_list|()
operator|+
literal|"&& git cherry-pick FETCH_HEAD"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|FormatPatchCommandLink
specifier|public
class|class
name|FormatPatchCommandLink
extends|extends
name|DownloadCommandLink
block|{
DECL|method|FormatPatchCommandLink ()
specifier|public
name|FormatPatchCommandLink
parameter_list|()
block|{
name|super
argument_list|(
name|DownloadCommand
operator|.
name|FORMAT_PATCH
argument_list|,
literal|"patch"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setCurrentUrl (DownloadUrlLink link)
specifier|protected
name|void
name|setCurrentUrl
parameter_list|(
name|DownloadUrlLink
name|link
parameter_list|)
block|{
name|widget
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|copyLabel
operator|.
name|setText
argument_list|(
literal|"git fetch "
operator|+
name|link
operator|.
name|getUrlData
argument_list|()
operator|+
literal|"&& git format-patch -1 --stdout FETCH_HEAD"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|RepoCommandLink
specifier|public
class|class
name|RepoCommandLink
extends|extends
name|DownloadCommandLink
block|{
DECL|field|projectName
name|String
name|projectName
decl_stmt|;
DECL|field|ref
name|String
name|ref
decl_stmt|;
DECL|method|RepoCommandLink (String project, String ref)
specifier|public
name|RepoCommandLink
parameter_list|(
name|String
name|project
parameter_list|,
name|String
name|ref
parameter_list|)
block|{
name|super
argument_list|(
name|DownloadCommand
operator|.
name|REPO_DOWNLOAD
argument_list|,
literal|"checkout"
argument_list|)
expr_stmt|;
name|this
operator|.
name|projectName
operator|=
name|project
expr_stmt|;
name|this
operator|.
name|ref
operator|=
name|ref
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setCurrentUrl (DownloadUrlLink link)
specifier|protected
name|void
name|setCurrentUrl
parameter_list|(
name|DownloadUrlLink
name|link
parameter_list|)
block|{
name|widget
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|StringBuilder
name|r
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|"repo download "
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|r
operator|.
name|append
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|copyLabel
operator|.
name|setText
argument_list|(
name|r
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|CloneCommandLink
specifier|public
class|class
name|CloneCommandLink
extends|extends
name|DownloadCommandLink
block|{
DECL|method|CloneCommandLink ()
specifier|public
name|CloneCommandLink
parameter_list|()
block|{
name|super
argument_list|(
name|DownloadCommand
operator|.
name|CHECKOUT
argument_list|,
literal|"clone"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setCurrentUrl (DownloadUrlLink link)
specifier|protected
name|void
name|setCurrentUrl
parameter_list|(
name|DownloadUrlLink
name|link
parameter_list|)
block|{
name|widget
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|copyLabel
operator|.
name|setText
argument_list|(
literal|"git clone "
operator|+
name|link
operator|.
name|getUrlData
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|CopyableCommandLinkFactory (CopyableLabel label, Widget widget)
specifier|public
name|CopyableCommandLinkFactory
parameter_list|(
name|CopyableLabel
name|label
parameter_list|,
name|Widget
name|widget
parameter_list|)
block|{
name|copyLabel
operator|=
name|label
expr_stmt|;
name|this
operator|.
name|widget
operator|=
name|widget
expr_stmt|;
block|}
block|}
DECL|field|cmdType
specifier|final
name|DownloadCommand
name|cmdType
decl_stmt|;
DECL|method|DownloadCommandLink (DownloadCommand cmdType, String text)
specifier|public
name|DownloadCommandLink
parameter_list|(
name|DownloadCommand
name|cmdType
parameter_list|,
name|String
name|text
parameter_list|)
block|{
name|super
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|this
operator|.
name|cmdType
operator|=
name|cmdType
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
name|downloadLink
argument_list|()
argument_list|)
expr_stmt|;
name|Roles
operator|.
name|getTabRole
argument_list|()
operator|.
name|set
argument_list|(
name|getElement
argument_list|()
argument_list|)
expr_stmt|;
name|addClickHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onClick (ClickEvent event)
specifier|public
name|void
name|onClick
parameter_list|(
name|ClickEvent
name|event
parameter_list|)
block|{
name|event
operator|.
name|preventDefault
argument_list|()
expr_stmt|;
name|event
operator|.
name|stopPropagation
argument_list|()
expr_stmt|;
name|select
argument_list|()
expr_stmt|;
if|if
condition|(
name|Gerrit
operator|.
name|isSignedIn
argument_list|()
condition|)
block|{
comment|// If the user is signed-in, remember this choice for future panels.
comment|//
name|AccountGeneralPreferences
name|pref
init|=
name|Gerrit
operator|.
name|getUserAccount
argument_list|()
operator|.
name|getGeneralPreferences
argument_list|()
decl_stmt|;
name|pref
operator|.
name|setDownloadCommand
argument_list|(
name|cmdType
argument_list|)
expr_stmt|;
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
name|changePreferences
argument_list|(
name|pref
argument_list|,
operator|new
name|AsyncCallback
argument_list|<
name|VoidResult
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|caught
parameter_list|)
block|{             }
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
name|VoidResult
name|result
parameter_list|)
block|{             }
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getCmdType ()
specifier|public
name|DownloadCommand
name|getCmdType
parameter_list|()
block|{
return|return
name|cmdType
return|;
block|}
DECL|method|select ()
name|void
name|select
parameter_list|()
block|{
name|DownloadCommandPanel
name|parent
init|=
operator|(
name|DownloadCommandPanel
operator|)
name|getParent
argument_list|()
decl_stmt|;
for|for
control|(
name|Widget
name|w
range|:
name|parent
control|)
block|{
if|if
condition|(
name|w
operator|!=
name|this
operator|&&
name|w
operator|instanceof
name|DownloadCommandLink
condition|)
block|{
name|w
operator|.
name|removeStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|downloadLink_Active
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|parent
operator|.
name|setCurrentCommand
argument_list|(
name|this
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
name|downloadLink_Active
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setCurrentUrl (DownloadUrlLink link)
specifier|protected
specifier|abstract
name|void
name|setCurrentUrl
parameter_list|(
name|DownloadUrlLink
name|link
parameter_list|)
function_decl|;
block|}
end_class

end_unit

