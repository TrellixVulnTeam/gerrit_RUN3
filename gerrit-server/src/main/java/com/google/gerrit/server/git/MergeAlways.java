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
DECL|package|com.google.gerrit.server.git
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|git
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
name|PatchSetApproval
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
DECL|class|MergeAlways
specifier|public
class|class
name|MergeAlways
extends|extends
name|SubmitStrategy
block|{
DECL|method|MergeAlways (final SubmitStrategy.Arguments args)
name|MergeAlways
parameter_list|(
specifier|final
name|SubmitStrategy
operator|.
name|Arguments
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|_run (CodeReviewCommit mergeTip, List<CodeReviewCommit> toMerge)
specifier|protected
name|CodeReviewCommit
name|_run
parameter_list|(
name|CodeReviewCommit
name|mergeTip
parameter_list|,
name|List
argument_list|<
name|CodeReviewCommit
argument_list|>
name|toMerge
parameter_list|)
throws|throws
name|MergeException
block|{
name|args
operator|.
name|mergeUtil
operator|.
name|reduceToMinimalMerge
argument_list|(
name|args
operator|.
name|mergeSorter
argument_list|,
name|toMerge
argument_list|)
expr_stmt|;
if|if
condition|(
name|mergeTip
operator|==
literal|null
condition|)
block|{
comment|// The branch is unborn. Take a fast-forward resolution to
comment|// create the branch.
name|mergeTip
operator|=
name|toMerge
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
operator|!
name|toMerge
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|mergeTip
operator|=
name|args
operator|.
name|mergeUtil
operator|.
name|mergeOneCommit
argument_list|(
name|args
operator|.
name|myIdent
argument_list|,
name|args
operator|.
name|repo
argument_list|,
name|args
operator|.
name|rw
argument_list|,
name|args
operator|.
name|inserter
argument_list|,
name|args
operator|.
name|canMergeFlag
argument_list|,
name|args
operator|.
name|destBranch
argument_list|,
name|mergeTip
argument_list|,
name|toMerge
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|PatchSetApproval
name|submitApproval
init|=
name|args
operator|.
name|mergeUtil
operator|.
name|markCleanMerges
argument_list|(
name|args
operator|.
name|rw
argument_list|,
name|args
operator|.
name|canMergeFlag
argument_list|,
name|mergeTip
argument_list|,
name|args
operator|.
name|alreadyAccepted
argument_list|)
decl_stmt|;
name|setRefLogIdent
argument_list|(
name|submitApproval
argument_list|)
expr_stmt|;
return|return
name|mergeTip
return|;
block|}
annotation|@
name|Override
DECL|method|dryRun (final CodeReviewCommit mergeTip, final CodeReviewCommit toMerge)
specifier|public
name|boolean
name|dryRun
parameter_list|(
specifier|final
name|CodeReviewCommit
name|mergeTip
parameter_list|,
specifier|final
name|CodeReviewCommit
name|toMerge
parameter_list|)
throws|throws
name|MergeException
block|{
return|return
name|args
operator|.
name|mergeUtil
operator|.
name|canMerge
argument_list|(
name|args
operator|.
name|mergeSorter
argument_list|,
name|args
operator|.
name|repo
argument_list|,
name|mergeTip
argument_list|,
name|toMerge
argument_list|)
return|;
block|}
block|}
end_class

end_unit

