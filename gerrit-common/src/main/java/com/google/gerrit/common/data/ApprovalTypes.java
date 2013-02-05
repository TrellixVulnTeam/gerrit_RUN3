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
DECL|package|com.google.gerrit.common.data
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|common
operator|.
name|data
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
DECL|class|ApprovalTypes
specifier|public
class|class
name|ApprovalTypes
block|{
DECL|field|approvalTypes
specifier|protected
name|List
argument_list|<
name|ApprovalType
argument_list|>
name|approvalTypes
decl_stmt|;
DECL|field|byId
specifier|private
specifier|transient
name|Map
argument_list|<
name|String
argument_list|,
name|ApprovalType
argument_list|>
name|byId
decl_stmt|;
DECL|field|byLabel
specifier|private
specifier|transient
name|Map
argument_list|<
name|String
argument_list|,
name|ApprovalType
argument_list|>
name|byLabel
decl_stmt|;
DECL|method|ApprovalTypes ()
specifier|protected
name|ApprovalTypes
parameter_list|()
block|{   }
DECL|method|ApprovalTypes (final List<ApprovalType> approvals)
specifier|public
name|ApprovalTypes
parameter_list|(
specifier|final
name|List
argument_list|<
name|ApprovalType
argument_list|>
name|approvals
parameter_list|)
block|{
name|approvalTypes
operator|=
name|approvals
expr_stmt|;
name|byId
argument_list|()
expr_stmt|;
block|}
DECL|method|getApprovalTypes ()
specifier|public
name|List
argument_list|<
name|ApprovalType
argument_list|>
name|getApprovalTypes
parameter_list|()
block|{
return|return
name|approvalTypes
return|;
block|}
DECL|method|byId (String id)
specifier|public
name|ApprovalType
name|byId
parameter_list|(
name|String
name|id
parameter_list|)
block|{
return|return
name|byId
argument_list|()
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
DECL|method|byId ()
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ApprovalType
argument_list|>
name|byId
parameter_list|()
block|{
if|if
condition|(
name|byId
operator|==
literal|null
condition|)
block|{
name|byId
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ApprovalType
argument_list|>
argument_list|()
expr_stmt|;
if|if
condition|(
name|approvalTypes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|ApprovalType
name|t
range|:
name|approvalTypes
control|)
block|{
name|byId
operator|.
name|put
argument_list|(
name|t
operator|.
name|getId
argument_list|()
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|byId
return|;
block|}
DECL|method|byLabel (String labelName)
specifier|public
name|ApprovalType
name|byLabel
parameter_list|(
name|String
name|labelName
parameter_list|)
block|{
return|return
name|byLabel
argument_list|()
operator|.
name|get
argument_list|(
name|labelName
operator|.
name|toLowerCase
argument_list|()
argument_list|)
return|;
block|}
DECL|method|byLabel ()
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ApprovalType
argument_list|>
name|byLabel
parameter_list|()
block|{
if|if
condition|(
name|byLabel
operator|==
literal|null
condition|)
block|{
name|byLabel
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ApprovalType
argument_list|>
argument_list|()
expr_stmt|;
if|if
condition|(
name|approvalTypes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ApprovalType
name|t
range|:
name|approvalTypes
control|)
block|{
name|byLabel
operator|.
name|put
argument_list|(
name|t
operator|.
name|getName
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|byLabel
return|;
block|}
block|}
end_class

end_unit

