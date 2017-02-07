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

begin_comment
comment|/**  * Status codes set on {@link com.google.gerrit.server.git.CodeReviewCommit}s by {@link  * SubmitStrategy} implementations.  */
end_comment

begin_enum
DECL|enum|CommitMergeStatus
specifier|public
enum|enum
name|CommitMergeStatus
block|{
DECL|enumConstant|CLEAN_MERGE
name|CLEAN_MERGE
argument_list|(
literal|"Change has been successfully merged"
argument_list|)
block|,
DECL|enumConstant|CLEAN_PICK
name|CLEAN_PICK
argument_list|(
literal|"Change has been successfully cherry-picked"
argument_list|)
block|,
DECL|enumConstant|CLEAN_REBASE
name|CLEAN_REBASE
argument_list|(
literal|"Change has been successfully rebased"
argument_list|)
block|,
DECL|enumConstant|ALREADY_MERGED
name|ALREADY_MERGED
argument_list|(
literal|""
argument_list|)
block|,
DECL|enumConstant|PATH_CONFLICT
name|PATH_CONFLICT
argument_list|(
literal|"Change could not be merged due to a path conflict.\n"
operator|+
literal|"\n"
operator|+
literal|"Please rebase the change locally and upload the rebased commit for review."
argument_list|)
block|,
DECL|enumConstant|REBASE_MERGE_CONFLICT
name|REBASE_MERGE_CONFLICT
argument_list|(
literal|"Change could not be merged due to a conflict.\n"
operator|+
literal|"\n"
operator|+
literal|"Please rebase the change locally and upload the rebased commit for review."
argument_list|)
block|,
DECL|enumConstant|SKIPPED_IDENTICAL_TREE
name|SKIPPED_IDENTICAL_TREE
argument_list|(
literal|"Marking change merged without cherry-picking to branch, as the resulting commit would be empty."
argument_list|)
block|,
DECL|enumConstant|MISSING_DEPENDENCY
name|MISSING_DEPENDENCY
argument_list|(
literal|""
argument_list|)
block|,
DECL|enumConstant|MANUAL_RECURSIVE_MERGE
name|MANUAL_RECURSIVE_MERGE
argument_list|(
literal|"The change requires a local merge to resolve.\n"
operator|+
literal|"\n"
operator|+
literal|"Please merge (or rebase) the change locally and upload the resolution for review."
argument_list|)
block|,
DECL|enumConstant|CANNOT_CHERRY_PICK_ROOT
name|CANNOT_CHERRY_PICK_ROOT
argument_list|(
literal|"Cannot cherry-pick an initial commit onto an existing branch.\n"
operator|+
literal|"\n"
operator|+
literal|"Please merge the change locally and upload the merge commit for review."
argument_list|)
block|,
DECL|enumConstant|CANNOT_REBASE_ROOT
name|CANNOT_REBASE_ROOT
argument_list|(
literal|"Cannot rebase an initial commit onto an existing branch.\n"
operator|+
literal|"\n"
operator|+
literal|"Please merge the change locally and upload the merge commit for review."
argument_list|)
block|,
DECL|enumConstant|NOT_FAST_FORWARD
name|NOT_FAST_FORWARD
argument_list|(
literal|"Project policy requires all submissions to be a fast-forward.\n"
operator|+
literal|"\n"
operator|+
literal|"Please rebase the change locally and upload again for review."
argument_list|)
block|;
DECL|field|message
specifier|private
name|String
name|message
decl_stmt|;
DECL|method|CommitMergeStatus (String message)
name|CommitMergeStatus
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
DECL|method|getMessage ()
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|message
return|;
block|}
block|}
end_enum

end_unit

