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
name|ChangeMessage
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
name|PatchSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Set
import|;
end_import

begin_comment
comment|/** Detail necessary to display a change. */
end_comment

begin_class
DECL|class|ChangeDetail
specifier|public
class|class
name|ChangeDetail
block|{
DECL|field|accounts
specifier|protected
name|AccountInfoCache
name|accounts
decl_stmt|;
DECL|field|allowsAnonymous
specifier|protected
name|boolean
name|allowsAnonymous
decl_stmt|;
DECL|field|canAbandon
specifier|protected
name|boolean
name|canAbandon
decl_stmt|;
DECL|field|canRestore
specifier|protected
name|boolean
name|canRestore
decl_stmt|;
DECL|field|canRevert
specifier|protected
name|boolean
name|canRevert
decl_stmt|;
DECL|field|change
specifier|protected
name|Change
name|change
decl_stmt|;
DECL|field|starred
specifier|protected
name|boolean
name|starred
decl_stmt|;
DECL|field|dependsOn
specifier|protected
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|dependsOn
decl_stmt|;
DECL|field|neededBy
specifier|protected
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|neededBy
decl_stmt|;
DECL|field|patchSets
specifier|protected
name|List
argument_list|<
name|PatchSet
argument_list|>
name|patchSets
decl_stmt|;
DECL|field|approvals
specifier|protected
name|List
argument_list|<
name|ApprovalDetail
argument_list|>
name|approvals
decl_stmt|;
DECL|field|missingApprovals
specifier|protected
name|Set
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|>
name|missingApprovals
decl_stmt|;
DECL|field|canSubmit
specifier|protected
name|boolean
name|canSubmit
decl_stmt|;
DECL|field|messages
specifier|protected
name|List
argument_list|<
name|ChangeMessage
argument_list|>
name|messages
decl_stmt|;
DECL|field|currentPatchSetId
specifier|protected
name|PatchSet
operator|.
name|Id
name|currentPatchSetId
decl_stmt|;
DECL|field|currentDetail
specifier|protected
name|PatchSetDetail
name|currentDetail
decl_stmt|;
DECL|method|ChangeDetail ()
specifier|public
name|ChangeDetail
parameter_list|()
block|{   }
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
DECL|method|setAccounts (AccountInfoCache aic)
specifier|public
name|void
name|setAccounts
parameter_list|(
name|AccountInfoCache
name|aic
parameter_list|)
block|{
name|accounts
operator|=
name|aic
expr_stmt|;
block|}
DECL|method|isAllowsAnonymous ()
specifier|public
name|boolean
name|isAllowsAnonymous
parameter_list|()
block|{
return|return
name|allowsAnonymous
return|;
block|}
DECL|method|setAllowsAnonymous (final boolean anon)
specifier|public
name|void
name|setAllowsAnonymous
parameter_list|(
specifier|final
name|boolean
name|anon
parameter_list|)
block|{
name|allowsAnonymous
operator|=
name|anon
expr_stmt|;
block|}
DECL|method|canAbandon ()
specifier|public
name|boolean
name|canAbandon
parameter_list|()
block|{
return|return
name|canAbandon
return|;
block|}
DECL|method|setCanAbandon (final boolean a)
specifier|public
name|void
name|setCanAbandon
parameter_list|(
specifier|final
name|boolean
name|a
parameter_list|)
block|{
name|canAbandon
operator|=
name|a
expr_stmt|;
block|}
DECL|method|canRestore ()
specifier|public
name|boolean
name|canRestore
parameter_list|()
block|{
return|return
name|canRestore
return|;
block|}
DECL|method|setCanRestore (final boolean a)
specifier|public
name|void
name|setCanRestore
parameter_list|(
specifier|final
name|boolean
name|a
parameter_list|)
block|{
name|canRestore
operator|=
name|a
expr_stmt|;
block|}
DECL|method|canRevert ()
specifier|public
name|boolean
name|canRevert
parameter_list|()
block|{
return|return
name|canRevert
return|;
block|}
DECL|method|setCanRevert (boolean a)
specifier|public
name|void
name|setCanRevert
parameter_list|(
name|boolean
name|a
parameter_list|)
block|{
name|canRevert
operator|=
name|a
expr_stmt|;
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
DECL|method|setCanSubmit (boolean a)
specifier|public
name|void
name|setCanSubmit
parameter_list|(
name|boolean
name|a
parameter_list|)
block|{
name|canSubmit
operator|=
name|a
expr_stmt|;
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
DECL|method|setChange (final Change change)
specifier|public
name|void
name|setChange
parameter_list|(
specifier|final
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
name|this
operator|.
name|currentPatchSetId
operator|=
name|change
operator|.
name|currentPatchSetId
argument_list|()
expr_stmt|;
block|}
DECL|method|isStarred ()
specifier|public
name|boolean
name|isStarred
parameter_list|()
block|{
return|return
name|starred
return|;
block|}
DECL|method|setStarred (final boolean s)
specifier|public
name|void
name|setStarred
parameter_list|(
specifier|final
name|boolean
name|s
parameter_list|)
block|{
name|starred
operator|=
name|s
expr_stmt|;
block|}
DECL|method|getDependsOn ()
specifier|public
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|getDependsOn
parameter_list|()
block|{
return|return
name|dependsOn
return|;
block|}
DECL|method|setDependsOn (List<ChangeInfo> d)
specifier|public
name|void
name|setDependsOn
parameter_list|(
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|d
parameter_list|)
block|{
name|dependsOn
operator|=
name|d
expr_stmt|;
block|}
DECL|method|getNeededBy ()
specifier|public
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|getNeededBy
parameter_list|()
block|{
return|return
name|neededBy
return|;
block|}
DECL|method|setNeededBy (List<ChangeInfo> d)
specifier|public
name|void
name|setNeededBy
parameter_list|(
name|List
argument_list|<
name|ChangeInfo
argument_list|>
name|d
parameter_list|)
block|{
name|neededBy
operator|=
name|d
expr_stmt|;
block|}
DECL|method|getMessages ()
specifier|public
name|List
argument_list|<
name|ChangeMessage
argument_list|>
name|getMessages
parameter_list|()
block|{
return|return
name|messages
return|;
block|}
DECL|method|setMessages (List<ChangeMessage> m)
specifier|public
name|void
name|setMessages
parameter_list|(
name|List
argument_list|<
name|ChangeMessage
argument_list|>
name|m
parameter_list|)
block|{
name|messages
operator|=
name|m
expr_stmt|;
block|}
DECL|method|getPatchSets ()
specifier|public
name|List
argument_list|<
name|PatchSet
argument_list|>
name|getPatchSets
parameter_list|()
block|{
return|return
name|patchSets
return|;
block|}
DECL|method|setPatchSets (List<PatchSet> s)
specifier|public
name|void
name|setPatchSets
parameter_list|(
name|List
argument_list|<
name|PatchSet
argument_list|>
name|s
parameter_list|)
block|{
name|patchSets
operator|=
name|s
expr_stmt|;
block|}
DECL|method|getApprovals ()
specifier|public
name|List
argument_list|<
name|ApprovalDetail
argument_list|>
name|getApprovals
parameter_list|()
block|{
return|return
name|approvals
return|;
block|}
DECL|method|setApprovals (Collection<ApprovalDetail> list)
specifier|public
name|void
name|setApprovals
parameter_list|(
name|Collection
argument_list|<
name|ApprovalDetail
argument_list|>
name|list
parameter_list|)
block|{
name|approvals
operator|=
operator|new
name|ArrayList
argument_list|<
name|ApprovalDetail
argument_list|>
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|approvals
argument_list|,
name|ApprovalDetail
operator|.
name|SORT
argument_list|)
expr_stmt|;
block|}
DECL|method|getMissingApprovals ()
specifier|public
name|Set
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|>
name|getMissingApprovals
parameter_list|()
block|{
return|return
name|missingApprovals
return|;
block|}
DECL|method|setMissingApprovals (Set<ApprovalCategory.Id> a)
specifier|public
name|void
name|setMissingApprovals
parameter_list|(
name|Set
argument_list|<
name|ApprovalCategory
operator|.
name|Id
argument_list|>
name|a
parameter_list|)
block|{
name|missingApprovals
operator|=
name|a
expr_stmt|;
block|}
DECL|method|isCurrentPatchSet (final PatchSetDetail detail)
specifier|public
name|boolean
name|isCurrentPatchSet
parameter_list|(
specifier|final
name|PatchSetDetail
name|detail
parameter_list|)
block|{
return|return
name|currentPatchSetId
operator|!=
literal|null
operator|&&
name|detail
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|currentPatchSetId
argument_list|)
return|;
block|}
DECL|method|getCurrentPatchSet ()
specifier|public
name|PatchSet
name|getCurrentPatchSet
parameter_list|()
block|{
if|if
condition|(
name|currentPatchSetId
operator|!=
literal|null
condition|)
block|{
comment|// We search through the list backwards because its *very* likely
comment|// that the current patch set is also the last patch set.
comment|//
for|for
control|(
name|int
name|i
init|=
name|patchSets
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
specifier|final
name|PatchSet
name|ps
init|=
name|patchSets
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|currentPatchSetId
argument_list|)
condition|)
block|{
return|return
name|ps
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|getCurrentPatchSetDetail ()
specifier|public
name|PatchSetDetail
name|getCurrentPatchSetDetail
parameter_list|()
block|{
return|return
name|currentDetail
return|;
block|}
DECL|method|setCurrentPatchSetDetail (PatchSetDetail d)
specifier|public
name|void
name|setCurrentPatchSetDetail
parameter_list|(
name|PatchSetDetail
name|d
parameter_list|)
block|{
name|currentDetail
operator|=
name|d
expr_stmt|;
block|}
DECL|method|getDescription ()
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|currentDetail
operator|!=
literal|null
condition|?
name|currentDetail
operator|.
name|getInfo
argument_list|()
operator|.
name|getMessage
argument_list|()
else|:
literal|""
return|;
block|}
block|}
end_class

end_unit

