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
DECL|package|com.google.gerrit.server.git.strategy
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
operator|.
name|strategy
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
name|server
operator|.
name|git
operator|.
name|CodeReviewCommit
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
name|server
operator|.
name|git
operator|.
name|IntegrationException
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
name|server
operator|.
name|git
operator|.
name|MergeTip
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|PersonIdent
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
DECL|method|MergeAlways (SubmitStrategy.Arguments args)
name|MergeAlways
parameter_list|(
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
DECL|method|_run (CodeReviewCommit branchTip, Collection<CodeReviewCommit> toMerge)
specifier|protected
name|MergeTip
name|_run
parameter_list|(
name|CodeReviewCommit
name|branchTip
parameter_list|,
name|Collection
argument_list|<
name|CodeReviewCommit
argument_list|>
name|toMerge
parameter_list|)
throws|throws
name|IntegrationException
block|{
name|List
argument_list|<
name|CodeReviewCommit
argument_list|>
name|sorted
init|=
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
decl_stmt|;
name|MergeTip
name|mergeTip
decl_stmt|;
if|if
condition|(
name|branchTip
operator|==
literal|null
condition|)
block|{
comment|// The branch is unborn. Take a fast-forward resolution to
comment|// create the branch.
name|mergeTip
operator|=
operator|new
name|MergeTip
argument_list|(
name|sorted
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|toMerge
argument_list|)
expr_stmt|;
name|sorted
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mergeTip
operator|=
operator|new
name|MergeTip
argument_list|(
name|branchTip
argument_list|,
name|toMerge
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
operator|!
name|sorted
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|CodeReviewCommit
name|mergedFrom
init|=
name|sorted
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|PersonIdent
name|serverIdent
init|=
name|args
operator|.
name|serverIdent
operator|.
name|get
argument_list|()
decl_stmt|;
name|PersonIdent
name|caller
init|=
name|args
operator|.
name|caller
operator|.
name|newCommitterIdent
argument_list|(
name|serverIdent
operator|.
name|getWhen
argument_list|()
argument_list|,
name|serverIdent
operator|.
name|getTimeZone
argument_list|()
argument_list|)
decl_stmt|;
name|CodeReviewCommit
name|newTip
init|=
name|args
operator|.
name|mergeUtil
operator|.
name|mergeOneCommit
argument_list|(
name|caller
argument_list|,
name|serverIdent
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
operator|.
name|getCurrentTip
argument_list|()
argument_list|,
name|mergedFrom
argument_list|)
decl_stmt|;
name|mergeTip
operator|.
name|moveTipTo
argument_list|(
name|newTip
argument_list|,
name|mergedFrom
argument_list|)
expr_stmt|;
block|}
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
operator|.
name|getCurrentTip
argument_list|()
argument_list|,
name|args
operator|.
name|alreadyAccepted
argument_list|)
expr_stmt|;
return|return
name|mergeTip
return|;
block|}
annotation|@
name|Override
DECL|method|dryRun (CodeReviewCommit mergeTip, CodeReviewCommit toMerge)
specifier|public
name|boolean
name|dryRun
parameter_list|(
name|CodeReviewCommit
name|mergeTip
parameter_list|,
name|CodeReviewCommit
name|toMerge
parameter_list|)
throws|throws
name|IntegrationException
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

