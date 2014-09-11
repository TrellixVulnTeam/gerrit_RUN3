begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|//Copyright (C) 2013 The Android Open Source Project
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//Licensed under the Apache License, Version 2.0 (the "License");
end_comment

begin_comment
comment|//you may not use this file except in compliance with the License.
end_comment

begin_comment
comment|//You may obtain a copy of the License at
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//http://www.apache.org/licenses/LICENSE-2.0
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|//Unless required by applicable law or agreed to in writing, software
end_comment

begin_comment
comment|//distributed under the License is distributed on an "AS IS" BASIS,
end_comment

begin_comment
comment|//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
end_comment

begin_comment
comment|//See the License for the specific language governing permissions and
end_comment

begin_comment
comment|//limitations under the License.
end_comment

begin_package
DECL|package|com.google.gerrit.client.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|client
operator|.
name|change
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
name|reviewdb
operator|.
name|client
operator|.
name|PatchSet
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
name|logical
operator|.
name|shared
operator|.
name|CloseEvent
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
name|logical
operator|.
name|shared
operator|.
name|CloseHandler
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
name|PopupPanel
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
name|globalkey
operator|.
name|client
operator|.
name|GlobalKey
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
DECL|class|EditFileAction
specifier|public
class|class
name|EditFileAction
block|{
DECL|field|id
specifier|private
specifier|final
name|PatchSet
operator|.
name|Id
name|id
decl_stmt|;
DECL|field|content
specifier|private
specifier|final
name|String
name|content
decl_stmt|;
DECL|field|file
specifier|private
specifier|final
name|String
name|file
decl_stmt|;
DECL|field|style
specifier|private
specifier|final
name|String
name|style
decl_stmt|;
DECL|field|editMessageButton
specifier|private
specifier|final
name|Widget
name|editMessageButton
decl_stmt|;
DECL|field|relativeTo
specifier|private
specifier|final
name|Widget
name|relativeTo
decl_stmt|;
DECL|field|editBox
specifier|private
name|EditFileBox
name|editBox
decl_stmt|;
DECL|field|popup
specifier|private
name|PopupPanel
name|popup
decl_stmt|;
DECL|method|EditFileAction ( PatchSet.Id id, String content, String file, String style, Widget editButton, Widget relativeTo)
specifier|public
name|EditFileAction
parameter_list|(
name|PatchSet
operator|.
name|Id
name|id
parameter_list|,
name|String
name|content
parameter_list|,
name|String
name|file
parameter_list|,
name|String
name|style
parameter_list|,
name|Widget
name|editButton
parameter_list|,
name|Widget
name|relativeTo
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|content
operator|=
name|content
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|style
operator|=
name|style
expr_stmt|;
name|this
operator|.
name|editMessageButton
operator|=
name|editButton
expr_stmt|;
name|this
operator|.
name|relativeTo
operator|=
name|relativeTo
expr_stmt|;
block|}
DECL|method|onEdit ()
specifier|public
name|void
name|onEdit
parameter_list|()
block|{
if|if
condition|(
name|popup
operator|!=
literal|null
condition|)
block|{
name|popup
operator|.
name|hide
argument_list|()
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|editBox
operator|==
literal|null
condition|)
block|{
name|editBox
operator|=
operator|new
name|EditFileBox
argument_list|(
name|id
argument_list|,
name|content
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
specifier|final
name|PluginSafePopupPanel
name|p
init|=
operator|new
name|PluginSafePopupPanel
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|p
operator|.
name|setStyleName
argument_list|(
name|style
argument_list|)
expr_stmt|;
if|if
condition|(
name|editMessageButton
operator|!=
literal|null
condition|)
block|{
name|p
operator|.
name|addAutoHidePartner
argument_list|(
name|editMessageButton
operator|.
name|getElement
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|p
operator|.
name|addCloseHandler
argument_list|(
operator|new
name|CloseHandler
argument_list|<
name|PopupPanel
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onClose
parameter_list|(
name|CloseEvent
argument_list|<
name|PopupPanel
argument_list|>
name|event
parameter_list|)
block|{
if|if
condition|(
name|popup
operator|==
name|p
condition|)
block|{
name|popup
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|p
operator|.
name|add
argument_list|(
name|editBox
argument_list|)
expr_stmt|;
name|p
operator|.
name|showRelativeTo
argument_list|(
name|relativeTo
argument_list|)
expr_stmt|;
name|GlobalKey
operator|.
name|dialog
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|popup
operator|=
name|p
expr_stmt|;
block|}
block|}
end_class

end_unit

