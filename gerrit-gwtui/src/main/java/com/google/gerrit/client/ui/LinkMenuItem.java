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
name|dom
operator|.
name|client
operator|.
name|AnchorElement
import|;
end_import

begin_class
DECL|class|LinkMenuItem
specifier|public
class|class
name|LinkMenuItem
extends|extends
name|InlineHyperlink
implements|implements
name|ScreenLoadHandler
block|{
DECL|field|bar
specifier|private
name|LinkMenuBar
name|bar
decl_stmt|;
DECL|method|LinkMenuItem (final String text, final String targetHistoryToken)
specifier|public
name|LinkMenuItem
parameter_list|(
specifier|final
name|String
name|text
parameter_list|,
specifier|final
name|String
name|targetHistoryToken
parameter_list|)
block|{
name|super
argument_list|(
name|text
argument_list|,
name|targetHistoryToken
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
name|menuItem
argument_list|()
argument_list|)
expr_stmt|;
name|Roles
operator|.
name|getMenuitemRole
argument_list|()
operator|.
name|set
argument_list|(
name|getElement
argument_list|()
argument_list|)
expr_stmt|;
name|Gerrit
operator|.
name|EVENT_BUS
operator|.
name|addHandler
argument_list|(
name|ScreenLoadEvent
operator|.
name|TYPE
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|go ()
specifier|public
name|void
name|go
parameter_list|()
block|{
name|super
operator|.
name|go
argument_list|()
expr_stmt|;
name|AnchorElement
operator|.
name|as
argument_list|(
name|getElement
argument_list|()
argument_list|)
operator|.
name|blur
argument_list|()
expr_stmt|;
block|}
DECL|method|setMenuBar (LinkMenuBar bar)
specifier|public
name|void
name|setMenuBar
parameter_list|(
name|LinkMenuBar
name|bar
parameter_list|)
block|{
name|this
operator|.
name|bar
operator|=
name|bar
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onScreenLoad (ScreenLoadEvent event)
specifier|public
name|void
name|onScreenLoad
parameter_list|(
name|ScreenLoadEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|match
argument_list|(
name|event
operator|.
name|getScreen
argument_list|()
operator|.
name|getToken
argument_list|()
argument_list|)
condition|)
block|{
name|Gerrit
operator|.
name|selectMenu
argument_list|(
name|bar
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
name|activeRow
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|removeStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|activeRow
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|match (String token)
specifier|protected
name|boolean
name|match
parameter_list|(
name|String
name|token
parameter_list|)
block|{
return|return
name|token
operator|.
name|equals
argument_list|(
name|getTargetHistoryToken
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

