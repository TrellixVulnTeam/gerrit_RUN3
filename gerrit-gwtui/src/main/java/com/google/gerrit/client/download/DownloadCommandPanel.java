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
name|Widget
import|;
end_import

begin_class
DECL|class|DownloadCommandPanel
specifier|public
class|class
name|DownloadCommandPanel
extends|extends
name|FlowPanel
block|{
DECL|field|currentCommand
specifier|private
name|DownloadCommandLink
name|currentCommand
decl_stmt|;
DECL|method|DownloadCommandPanel ()
specifier|public
name|DownloadCommandPanel
parameter_list|()
block|{
name|setStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|downloadLinkList
argument_list|()
argument_list|)
expr_stmt|;
name|Roles
operator|.
name|getTablistRole
argument_list|()
operator|.
name|set
argument_list|(
name|getElement
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|isEmpty ()
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|getWidgetCount
argument_list|()
operator|==
literal|0
return|;
block|}
DECL|method|select ()
specifier|public
name|void
name|select
parameter_list|()
block|{
name|DownloadCommandLink
name|first
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Widget
name|w
range|:
name|this
control|)
block|{
if|if
condition|(
name|w
operator|instanceof
name|DownloadCommandLink
condition|)
block|{
name|DownloadCommandLink
name|d
init|=
operator|(
name|DownloadCommandLink
operator|)
name|w
decl_stmt|;
if|if
condition|(
name|currentCommand
operator|!=
literal|null
operator|&&
name|d
operator|.
name|getText
argument_list|()
operator|.
name|equals
argument_list|(
name|currentCommand
operator|.
name|getText
argument_list|()
argument_list|)
condition|)
block|{
name|d
operator|.
name|select
argument_list|()
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|first
operator|==
literal|null
condition|)
block|{
name|first
operator|=
name|d
expr_stmt|;
block|}
block|}
block|}
comment|// If none matched the requested type, select the first in the
comment|// group as that will at least give us an initial baseline.
if|if
condition|(
name|first
operator|!=
literal|null
condition|)
block|{
name|first
operator|.
name|select
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|setCurrentCommand (DownloadCommandLink cmd)
name|void
name|setCurrentCommand
parameter_list|(
name|DownloadCommandLink
name|cmd
parameter_list|)
block|{
name|currentCommand
operator|=
name|cmd
expr_stmt|;
block|}
block|}
end_class

end_unit

