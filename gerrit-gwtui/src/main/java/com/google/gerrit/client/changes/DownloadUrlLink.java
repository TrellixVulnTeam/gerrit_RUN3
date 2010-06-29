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
DECL|package|com.google.gerrit.client.changes
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|changes
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
name|AccountGeneralPreferences
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
name|Accessibility
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
name|gwtjsonrpc
operator|.
name|client
operator|.
name|VoidResult
import|;
end_import

begin_class
DECL|class|DownloadUrlLink
class|class
name|DownloadUrlLink
extends|extends
name|Anchor
implements|implements
name|ClickHandler
block|{
DECL|field|urlType
specifier|final
name|AccountGeneralPreferences
operator|.
name|DownloadScheme
name|urlType
decl_stmt|;
DECL|field|urlData
specifier|final
name|String
name|urlData
decl_stmt|;
DECL|method|DownloadUrlLink (AccountGeneralPreferences.DownloadScheme urlType, String text, String urlData)
name|DownloadUrlLink
parameter_list|(
name|AccountGeneralPreferences
operator|.
name|DownloadScheme
name|urlType
parameter_list|,
name|String
name|text
parameter_list|,
name|String
name|urlData
parameter_list|)
block|{
name|super
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|this
operator|.
name|urlType
operator|=
name|urlType
expr_stmt|;
name|this
operator|.
name|urlData
operator|=
name|urlData
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
name|Accessibility
operator|.
name|setRole
argument_list|(
name|getElement
argument_list|()
argument_list|,
name|Accessibility
operator|.
name|ROLE_TAB
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
name|setDownloadUrl
argument_list|(
name|urlType
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
DECL|method|select ()
name|void
name|select
parameter_list|()
block|{
name|DownloadUrlPanel
name|parent
init|=
operator|(
name|DownloadUrlPanel
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
name|DownloadUrlLink
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
name|setCurrentUrl
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
block|}
end_class

end_unit

