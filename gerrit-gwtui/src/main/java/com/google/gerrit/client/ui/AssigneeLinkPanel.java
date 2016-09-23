begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
name|AvatarImage
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
name|FormatUtil
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
name|info
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
name|user
operator|.
name|client
operator|.
name|ui
operator|.
name|FlowPanel
import|;
end_import

begin_comment
comment|/** Link to any assignees accounts dashboard. */
end_comment

begin_class
DECL|class|AssigneeLinkPanel
specifier|public
class|class
name|AssigneeLinkPanel
extends|extends
name|FlowPanel
block|{
DECL|method|AssigneeLinkPanel (AccountInfo info)
specifier|public
name|AssigneeLinkPanel
parameter_list|(
name|AccountInfo
name|info
parameter_list|)
block|{
name|addStyleName
argument_list|(
name|Gerrit
operator|.
name|RESOURCES
operator|.
name|css
argument_list|()
operator|.
name|accountLinkPanel
argument_list|()
argument_list|)
expr_stmt|;
name|InlineHyperlink
name|l
init|=
operator|new
name|InlineHyperlink
argument_list|(
name|FormatUtil
operator|.
name|name
argument_list|(
name|info
argument_list|)
argument_list|,
name|PageLinks
operator|.
name|toAssigneeQuery
argument_list|(
name|assignedTo
argument_list|(
name|info
argument_list|)
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|go
parameter_list|()
block|{
name|Gerrit
operator|.
name|display
argument_list|(
name|getTargetHistoryToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|l
operator|.
name|setTitle
argument_list|(
name|FormatUtil
operator|.
name|nameEmail
argument_list|(
name|info
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
operator|new
name|AvatarImage
argument_list|(
name|info
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|l
argument_list|)
expr_stmt|;
block|}
DECL|method|assignedTo (AccountInfo ai)
specifier|public
specifier|static
name|String
name|assignedTo
parameter_list|(
name|AccountInfo
name|ai
parameter_list|)
block|{
if|if
condition|(
name|ai
operator|.
name|email
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|ai
operator|.
name|email
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|ai
operator|.
name|name
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|ai
operator|.
name|name
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|""
return|;
block|}
block|}
block|}
end_class

end_unit

