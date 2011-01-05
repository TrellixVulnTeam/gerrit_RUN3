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
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|reviewdb
operator|.
name|ApprovalCategory
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
name|Change
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
name|PatchLineComment
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
name|PatchSetApproval
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
name|PatchSetInfo
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

begin_class
DECL|class|PatchSetPublishDetail
specifier|public
class|class
name|PatchSetPublishDetail
block|{
DECL|field|accounts
specifier|protected
name|AccountInfoCache
name|accounts
decl_stmt|;
DECL|field|patchSetInfo
specifier|protected
name|PatchSetInfo
name|patchSetInfo
decl_stmt|;
DECL|field|change
specifier|protected
name|Change
name|change
decl_stmt|;
DECL|field|drafts
specifier|protected
name|List
argument_list|<
name|PatchLineComment
argument_list|>
name|drafts
decl_stmt|;
DECL|field|labels
specifier|protected
name|List
argument_list|<
name|PermissionRange
argument_list|>
name|labels
decl_stmt|;
DECL|field|given
specifier|protected
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|given
decl_stmt|;
DECL|field|canSubmit
specifier|protected
name|boolean
name|canSubmit
decl_stmt|;
DECL|method|getLabels ()
specifier|public
name|List
argument_list|<
name|PermissionRange
argument_list|>
name|getLabels
parameter_list|()
block|{
return|return
name|labels
return|;
block|}
DECL|method|setLabels (List<PermissionRange> labels)
specifier|public
name|void
name|setLabels
parameter_list|(
name|List
argument_list|<
name|PermissionRange
argument_list|>
name|labels
parameter_list|)
block|{
name|this
operator|.
name|labels
operator|=
name|labels
expr_stmt|;
block|}
DECL|method|getGiven ()
specifier|public
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|getGiven
parameter_list|()
block|{
return|return
name|given
return|;
block|}
DECL|method|setGiven (List<PatchSetApproval> given)
specifier|public
name|void
name|setGiven
parameter_list|(
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|given
parameter_list|)
block|{
name|this
operator|.
name|given
operator|=
name|given
expr_stmt|;
block|}
DECL|method|setAccounts (AccountInfoCache accounts)
specifier|public
name|void
name|setAccounts
parameter_list|(
name|AccountInfoCache
name|accounts
parameter_list|)
block|{
name|this
operator|.
name|accounts
operator|=
name|accounts
expr_stmt|;
block|}
DECL|method|setPatchSetInfo (PatchSetInfo patchSetInfo)
specifier|public
name|void
name|setPatchSetInfo
parameter_list|(
name|PatchSetInfo
name|patchSetInfo
parameter_list|)
block|{
name|this
operator|.
name|patchSetInfo
operator|=
name|patchSetInfo
expr_stmt|;
block|}
DECL|method|setChange (Change change)
specifier|public
name|void
name|setChange
parameter_list|(
name|Change
name|change
parameter_list|)
block|{
name|this
operator|.
name|change
operator|=
name|change
expr_stmt|;
block|}
DECL|method|setDrafts (List<PatchLineComment> drafts)
specifier|public
name|void
name|setDrafts
parameter_list|(
name|List
argument_list|<
name|PatchLineComment
argument_list|>
name|drafts
parameter_list|)
block|{
name|this
operator|.
name|drafts
operator|=
name|drafts
expr_stmt|;
block|}
DECL|method|setCanSubmit (boolean allowed)
specifier|public
name|void
name|setCanSubmit
parameter_list|(
name|boolean
name|allowed
parameter_list|)
block|{
name|canSubmit
operator|=
name|allowed
expr_stmt|;
block|}
DECL|method|getAccounts ()
specifier|public
name|AccountInfoCache
name|getAccounts
parameter_list|()
block|{
return|return
name|accounts
return|;
block|}
DECL|method|getChange ()
specifier|public
name|Change
name|getChange
parameter_list|()
block|{
return|return
name|change
return|;
block|}
DECL|method|getPatchSetInfo ()
specifier|public
name|PatchSetInfo
name|getPatchSetInfo
parameter_list|()
block|{
return|return
name|patchSetInfo
return|;
block|}
DECL|method|getDrafts ()
specifier|public
name|List
argument_list|<
name|PatchLineComment
argument_list|>
name|getDrafts
parameter_list|()
block|{
return|return
name|drafts
return|;
block|}
DECL|method|getRange (final String permissionName)
specifier|public
name|PermissionRange
name|getRange
parameter_list|(
specifier|final
name|String
name|permissionName
parameter_list|)
block|{
for|for
control|(
name|PermissionRange
name|s
range|:
name|labels
control|)
block|{
if|if
condition|(
name|s
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|permissionName
argument_list|)
condition|)
block|{
return|return
name|s
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|getChangeApproval (ApprovalCategory.Id id)
specifier|public
name|PatchSetApproval
name|getChangeApproval
parameter_list|(
name|ApprovalCategory
operator|.
name|Id
name|id
parameter_list|)
block|{
for|for
control|(
name|PatchSetApproval
name|a
range|:
name|given
control|)
block|{
if|if
condition|(
name|a
operator|.
name|getCategoryId
argument_list|()
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
block|{
return|return
name|a
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|canSubmit ()
specifier|public
name|boolean
name|canSubmit
parameter_list|()
block|{
return|return
name|canSubmit
return|;
block|}
block|}
end_class

end_unit

