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
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|TimeUtil
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
name|extensions
operator|.
name|restapi
operator|.
name|MergeConflictException
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
name|extensions
operator|.
name|restapi
operator|.
name|RestApiException
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
name|client
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
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|change
operator|.
name|RebaseChangeOp
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
name|BatchUpdate
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
name|BatchUpdate
operator|.
name|ChangeContext
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
name|BatchUpdate
operator|.
name|Context
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
name|BatchUpdate
operator|.
name|RepoContext
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
name|RebaseSorter
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
name|UpdateException
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
name|validators
operator|.
name|CommitValidators
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
name|project
operator|.
name|InvalidChangeOperationException
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
name|project
operator|.
name|NoSuchChangeException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|server
operator|.
name|OrmException
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
name|ObjectId
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_class
DECL|class|RebaseIfNecessary
specifier|public
class|class
name|RebaseIfNecessary
extends|extends
name|SubmitStrategy
block|{
DECL|method|RebaseIfNecessary (SubmitStrategy.Arguments args)
name|RebaseIfNecessary
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
DECL|method|run (final CodeReviewCommit branchTip, final Collection<CodeReviewCommit> toMerge)
specifier|public
name|MergeTip
name|run
parameter_list|(
specifier|final
name|CodeReviewCommit
name|branchTip
parameter_list|,
specifier|final
name|Collection
argument_list|<
name|CodeReviewCommit
argument_list|>
name|toMerge
parameter_list|)
throws|throws
name|IntegrationException
block|{
name|MergeTip
name|mergeTip
init|=
operator|new
name|MergeTip
argument_list|(
name|branchTip
argument_list|,
name|toMerge
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|CodeReviewCommit
argument_list|>
name|sorted
init|=
name|sort
argument_list|(
name|toMerge
argument_list|)
decl_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
try|try
init|(
name|BatchUpdate
name|u
init|=
name|args
operator|.
name|newBatchUpdate
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
init|)
block|{
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
name|n
init|=
name|sorted
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Change
operator|.
name|Id
name|cid
init|=
name|n
operator|.
name|change
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
if|if
condition|(
name|first
operator|&&
name|branchTip
operator|==
literal|null
condition|)
block|{
name|u
operator|.
name|addOp
argument_list|(
name|cid
argument_list|,
operator|new
name|RebaseUnbornRootOp
argument_list|(
name|mergeTip
argument_list|,
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|n
operator|.
name|getParentCount
argument_list|()
operator|==
literal|0
condition|)
block|{
name|u
operator|.
name|addOp
argument_list|(
name|cid
argument_list|,
operator|new
name|RebaseRootOp
argument_list|(
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|n
operator|.
name|getParentCount
argument_list|()
operator|==
literal|1
condition|)
block|{
name|u
operator|.
name|addOp
argument_list|(
name|cid
argument_list|,
operator|new
name|RebaseOneOp
argument_list|(
name|mergeTip
argument_list|,
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|u
operator|.
name|addOp
argument_list|(
name|cid
argument_list|,
operator|new
name|RebaseMultipleParentsOp
argument_list|(
name|mergeTip
argument_list|,
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|first
operator|=
literal|false
expr_stmt|;
block|}
name|u
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UpdateException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|IntegrationException
condition|)
block|{
throw|throw
operator|new
name|IntegrationException
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
throw|throw
operator|new
name|IntegrationException
argument_list|(
literal|"Cannot rebase onto "
operator|+
name|args
operator|.
name|destBranch
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|RestApiException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IntegrationException
argument_list|(
literal|"Cannot rebase onto "
operator|+
name|args
operator|.
name|destBranch
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|mergeTip
return|;
block|}
DECL|class|RebaseUnbornRootOp
specifier|private
class|class
name|RebaseUnbornRootOp
extends|extends
name|BatchUpdate
operator|.
name|Op
block|{
DECL|field|mergeTip
specifier|private
specifier|final
name|MergeTip
name|mergeTip
decl_stmt|;
DECL|field|toMerge
specifier|private
specifier|final
name|CodeReviewCommit
name|toMerge
decl_stmt|;
DECL|method|RebaseUnbornRootOp (MergeTip mergeTip, CodeReviewCommit toMerge)
specifier|private
name|RebaseUnbornRootOp
parameter_list|(
name|MergeTip
name|mergeTip
parameter_list|,
name|CodeReviewCommit
name|toMerge
parameter_list|)
block|{
name|this
operator|.
name|mergeTip
operator|=
name|mergeTip
expr_stmt|;
name|this
operator|.
name|toMerge
operator|=
name|toMerge
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateRepo (RepoContext ctx)
specifier|public
name|void
name|updateRepo
parameter_list|(
name|RepoContext
name|ctx
parameter_list|)
block|{
comment|// The branch is unborn. Take fast-forward resolution to create the
comment|// branch.
name|toMerge
operator|.
name|setStatusCode
argument_list|(
name|CommitMergeStatus
operator|.
name|CLEAN_MERGE
argument_list|)
expr_stmt|;
name|mergeTip
operator|.
name|moveTipTo
argument_list|(
name|toMerge
argument_list|,
name|toMerge
argument_list|)
expr_stmt|;
name|acceptMergeTip
argument_list|(
name|mergeTip
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|RebaseRootOp
specifier|private
specifier|static
class|class
name|RebaseRootOp
extends|extends
name|BatchUpdate
operator|.
name|Op
block|{
DECL|field|toMerge
specifier|private
specifier|final
name|CodeReviewCommit
name|toMerge
decl_stmt|;
DECL|method|RebaseRootOp (CodeReviewCommit toMerge)
specifier|private
name|RebaseRootOp
parameter_list|(
name|CodeReviewCommit
name|toMerge
parameter_list|)
block|{
name|this
operator|.
name|toMerge
operator|=
name|toMerge
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateRepo (RepoContext ctx)
specifier|public
name|void
name|updateRepo
parameter_list|(
name|RepoContext
name|ctx
parameter_list|)
block|{
comment|// Refuse to merge a root commit into an existing branch, we cannot obtain
comment|// a delta for the cherry-pick to apply.
name|toMerge
operator|.
name|setStatusCode
argument_list|(
name|CommitMergeStatus
operator|.
name|CANNOT_REBASE_ROOT
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|RebaseOneOp
specifier|private
class|class
name|RebaseOneOp
extends|extends
name|BatchUpdate
operator|.
name|Op
block|{
DECL|field|mergeTip
specifier|private
specifier|final
name|MergeTip
name|mergeTip
decl_stmt|;
DECL|field|toMerge
specifier|private
specifier|final
name|CodeReviewCommit
name|toMerge
decl_stmt|;
DECL|field|rebaseOp
specifier|private
name|RebaseChangeOp
name|rebaseOp
decl_stmt|;
DECL|method|RebaseOneOp (MergeTip mergeTip, CodeReviewCommit toMerge)
specifier|private
name|RebaseOneOp
parameter_list|(
name|MergeTip
name|mergeTip
parameter_list|,
name|CodeReviewCommit
name|toMerge
parameter_list|)
block|{
name|this
operator|.
name|mergeTip
operator|=
name|mergeTip
expr_stmt|;
name|this
operator|.
name|toMerge
operator|=
name|toMerge
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateRepo (RepoContext ctx)
specifier|public
name|void
name|updateRepo
parameter_list|(
name|RepoContext
name|ctx
parameter_list|)
throws|throws
name|IntegrationException
throws|,
name|InvalidChangeOperationException
throws|,
name|RestApiException
throws|,
name|IOException
throws|,
name|OrmException
block|{
comment|// TODO(dborowitz): args.rw is needed because it's a CodeReviewRevWalk.
comment|// When hoisting BatchUpdate into MergeOp, we will need to teach
comment|// BatchUpdate how to produce CodeReviewRevWalks.
if|if
condition|(
name|args
operator|.
name|mergeUtil
operator|.
name|canFastForward
argument_list|(
name|args
operator|.
name|mergeSorter
argument_list|,
name|mergeTip
operator|.
name|getCurrentTip
argument_list|()
argument_list|,
name|args
operator|.
name|rw
argument_list|,
name|toMerge
argument_list|)
condition|)
block|{
name|toMerge
operator|.
name|setStatusCode
argument_list|(
name|CommitMergeStatus
operator|.
name|CLEAN_MERGE
argument_list|)
expr_stmt|;
name|mergeTip
operator|.
name|moveTipTo
argument_list|(
name|toMerge
argument_list|,
name|toMerge
argument_list|)
expr_stmt|;
name|acceptMergeTip
argument_list|(
name|mergeTip
argument_list|)
expr_stmt|;
return|return;
block|}
name|rebaseOp
operator|=
name|args
operator|.
name|rebaseFactory
operator|.
name|create
argument_list|(
name|toMerge
operator|.
name|getControl
argument_list|()
argument_list|,
comment|// Racy read of patch set is ok; see comments in RebaseChangeOp.
name|args
operator|.
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|toMerge
operator|.
name|getPatchsetId
argument_list|()
argument_list|)
argument_list|,
name|mergeTip
operator|.
name|getCurrentTip
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|setRunHooks
argument_list|(
literal|false
argument_list|)
operator|.
name|setValidatePolicy
argument_list|(
name|CommitValidators
operator|.
name|Policy
operator|.
name|NONE
argument_list|)
expr_stmt|;
try|try
block|{
name|rebaseOp
operator|.
name|updateRepo
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MergeConflictException
name|e
parameter_list|)
block|{
name|toMerge
operator|.
name|setStatusCode
argument_list|(
name|CommitMergeStatus
operator|.
name|REBASE_MERGE_CONFLICT
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IntegrationException
argument_list|(
literal|"Cannot rebase "
operator|+
name|toMerge
operator|.
name|name
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|updateChange (ChangeContext ctx)
specifier|public
name|void
name|updateChange
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|)
throws|throws
name|NoSuchChangeException
throws|,
name|InvalidChangeOperationException
throws|,
name|OrmException
throws|,
name|IOException
block|{
if|if
condition|(
name|rebaseOp
operator|==
literal|null
condition|)
block|{
comment|// Took the fast-forward option, nothing to do.
return|return;
block|}
name|rebaseOp
operator|.
name|updateChange
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|PatchSet
name|newPatchSet
init|=
name|rebaseOp
operator|.
name|getPatchSet
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|approvals
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSetApproval
name|a
range|:
name|args
operator|.
name|approvalsUtil
operator|.
name|byPatchSet
argument_list|(
name|ctx
operator|.
name|getDb
argument_list|()
argument_list|,
name|toMerge
operator|.
name|getControl
argument_list|()
argument_list|,
name|toMerge
operator|.
name|getPatchsetId
argument_list|()
argument_list|)
control|)
block|{
name|approvals
operator|.
name|add
argument_list|(
operator|new
name|PatchSetApproval
argument_list|(
name|newPatchSet
operator|.
name|getId
argument_list|()
argument_list|,
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// rebaseOp may already have copied some approvals; use upsert, not
comment|// insert, to avoid constraint violation on database.
name|args
operator|.
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|upsert
argument_list|(
name|approvals
argument_list|)
expr_stmt|;
comment|// TODO(dborowitz): Make RevWalk available via BatchUpdate.
name|CodeReviewCommit
name|newTip
init|=
name|args
operator|.
name|rw
operator|.
name|parseCommit
argument_list|(
name|ObjectId
operator|.
name|fromString
argument_list|(
name|newPatchSet
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|mergeTip
operator|.
name|moveTipTo
argument_list|(
name|newTip
argument_list|,
name|newTip
argument_list|)
expr_stmt|;
name|toMerge
operator|.
name|change
argument_list|()
operator|.
name|setCurrentPatchSet
argument_list|(
name|args
operator|.
name|patchSetInfoFactory
operator|.
name|get
argument_list|(
name|args
operator|.
name|rw
argument_list|,
name|mergeTip
operator|.
name|getCurrentTip
argument_list|()
argument_list|,
name|newPatchSet
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|mergeTip
operator|.
name|getCurrentTip
argument_list|()
operator|.
name|copyFrom
argument_list|(
name|toMerge
argument_list|)
expr_stmt|;
name|mergeTip
operator|.
name|getCurrentTip
argument_list|()
operator|.
name|setControl
argument_list|(
name|args
operator|.
name|changeControlFactory
operator|.
name|controlFor
argument_list|(
name|toMerge
operator|.
name|change
argument_list|()
argument_list|,
name|args
operator|.
name|caller
argument_list|)
argument_list|)
expr_stmt|;
name|mergeTip
operator|.
name|getCurrentTip
argument_list|()
operator|.
name|setPatchsetId
argument_list|(
name|newPatchSet
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|mergeTip
operator|.
name|getCurrentTip
argument_list|()
operator|.
name|setStatusCode
argument_list|(
name|CommitMergeStatus
operator|.
name|CLEAN_REBASE
argument_list|)
expr_stmt|;
name|args
operator|.
name|commits
operator|.
name|put
argument_list|(
name|mergeTip
operator|.
name|getCurrentTip
argument_list|()
argument_list|)
expr_stmt|;
name|acceptMergeTip
argument_list|(
name|mergeTip
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|postUpdate (Context ctx)
specifier|public
name|void
name|postUpdate
parameter_list|(
name|Context
name|ctx
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
name|rebaseOp
operator|!=
literal|null
condition|)
block|{
name|rebaseOp
operator|.
name|postUpdate
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|RebaseMultipleParentsOp
specifier|private
class|class
name|RebaseMultipleParentsOp
extends|extends
name|BatchUpdate
operator|.
name|Op
block|{
DECL|field|mergeTip
specifier|private
specifier|final
name|MergeTip
name|mergeTip
decl_stmt|;
DECL|field|toMerge
specifier|private
specifier|final
name|CodeReviewCommit
name|toMerge
decl_stmt|;
DECL|method|RebaseMultipleParentsOp (MergeTip mergeTip, CodeReviewCommit toMerge)
specifier|private
name|RebaseMultipleParentsOp
parameter_list|(
name|MergeTip
name|mergeTip
parameter_list|,
name|CodeReviewCommit
name|toMerge
parameter_list|)
block|{
name|this
operator|.
name|mergeTip
operator|=
name|mergeTip
expr_stmt|;
name|this
operator|.
name|toMerge
operator|=
name|toMerge
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateRepo (RepoContext ctx)
specifier|public
name|void
name|updateRepo
parameter_list|(
name|RepoContext
name|ctx
parameter_list|)
throws|throws
name|IntegrationException
throws|,
name|IOException
block|{
comment|// There are multiple parents, so this is a merge commit. We don't want
comment|// to rebase the merge as clients can't easily rebase their history with
comment|// that merge present and replaced by an equivalent merge with a different
comment|// first parent. So instead behave as though MERGE_IF_NECESSARY was
comment|// configured.
if|if
condition|(
name|args
operator|.
name|rw
operator|.
name|isMergedInto
argument_list|(
name|mergeTip
operator|.
name|getCurrentTip
argument_list|()
argument_list|,
name|toMerge
argument_list|)
condition|)
block|{
name|mergeTip
operator|.
name|moveTipTo
argument_list|(
name|toMerge
argument_list|,
name|toMerge
argument_list|)
expr_stmt|;
name|acceptMergeTip
argument_list|(
name|mergeTip
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// TODO(dborowitz): Can't use repo from ctx due to canMergeFlag.
name|CodeReviewCommit
name|newTip
init|=
name|args
operator|.
name|mergeUtil
operator|.
name|mergeOneCommit
argument_list|(
name|args
operator|.
name|serverIdent
argument_list|,
name|args
operator|.
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
name|toMerge
argument_list|)
decl_stmt|;
name|mergeTip
operator|.
name|moveTipTo
argument_list|(
name|newTip
argument_list|,
name|toMerge
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
name|acceptMergeTip
argument_list|(
name|mergeTip
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|acceptMergeTip (MergeTip mergeTip)
specifier|private
name|void
name|acceptMergeTip
parameter_list|(
name|MergeTip
name|mergeTip
parameter_list|)
block|{
name|args
operator|.
name|alreadyAccepted
operator|.
name|add
argument_list|(
name|mergeTip
operator|.
name|getCurrentTip
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|sort (Collection<CodeReviewCommit> toSort)
specifier|private
name|List
argument_list|<
name|CodeReviewCommit
argument_list|>
name|sort
parameter_list|(
name|Collection
argument_list|<
name|CodeReviewCommit
argument_list|>
name|toSort
parameter_list|)
throws|throws
name|IntegrationException
block|{
try|try
block|{
name|List
argument_list|<
name|CodeReviewCommit
argument_list|>
name|result
init|=
operator|new
name|RebaseSorter
argument_list|(
name|args
operator|.
name|rw
argument_list|,
name|args
operator|.
name|alreadyAccepted
argument_list|,
name|args
operator|.
name|canMergeFlag
argument_list|)
operator|.
name|sort
argument_list|(
name|toSort
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|result
argument_list|,
name|CodeReviewCommit
operator|.
name|ORDER
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IntegrationException
argument_list|(
literal|"Commit sorting failed"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|dryRun (SubmitDryRun.Arguments args, CodeReviewCommit mergeTip, CodeReviewCommit toMerge)
specifier|static
name|boolean
name|dryRun
parameter_list|(
name|SubmitDryRun
operator|.
name|Arguments
name|args
parameter_list|,
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
operator|!
name|args
operator|.
name|mergeUtil
operator|.
name|hasMissingDependencies
argument_list|(
name|args
operator|.
name|mergeSorter
argument_list|,
name|toMerge
argument_list|)
operator|&&
name|args
operator|.
name|mergeUtil
operator|.
name|canCherryPick
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
name|args
operator|.
name|rw
argument_list|,
name|toMerge
argument_list|)
return|;
block|}
block|}
end_class

end_unit

