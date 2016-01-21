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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|MoreObjects
operator|.
name|firstNonNull
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkState
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|notedb
operator|.
name|ReviewerStateInternal
operator|.
name|REVIEWER
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
import|;
end_import

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
name|Iterables
import|;
end_import

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
name|Maps
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
name|data
operator|.
name|SubmitRecord
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
name|Account
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
name|Branch
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
name|client
operator|.
name|LabelId
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
name|reviewdb
operator|.
name|client
operator|.
name|Project
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
name|RefNames
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
name|server
operator|.
name|ReviewDb
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
name|ChangeUtil
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
name|IdentifiedUser
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
name|LabelNormalizer
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
name|ProjectConfig
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
name|notedb
operator|.
name|ChangeUpdate
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
name|ProjectState
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|transport
operator|.
name|ReceiveCommand
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_class
DECL|class|SubmitStrategyOp
specifier|abstract
class|class
name|SubmitStrategyOp
extends|extends
name|BatchUpdate
operator|.
name|Op
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SubmitStrategyOp
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|args
specifier|protected
specifier|final
name|SubmitStrategy
operator|.
name|Arguments
name|args
decl_stmt|;
DECL|field|toMerge
specifier|protected
specifier|final
name|CodeReviewCommit
name|toMerge
decl_stmt|;
DECL|field|command
specifier|private
name|ReceiveCommand
name|command
decl_stmt|;
DECL|field|submitter
specifier|private
name|PatchSetApproval
name|submitter
decl_stmt|;
DECL|field|mergeResultRev
specifier|private
name|ObjectId
name|mergeResultRev
decl_stmt|;
DECL|field|mergedPatchSet
specifier|private
name|PatchSet
name|mergedPatchSet
decl_stmt|;
DECL|field|updatedChange
specifier|private
name|Change
name|updatedChange
decl_stmt|;
DECL|method|SubmitStrategyOp (SubmitStrategy.Arguments args, CodeReviewCommit toMerge)
specifier|protected
name|SubmitStrategyOp
parameter_list|(
name|SubmitStrategy
operator|.
name|Arguments
name|args
parameter_list|,
name|CodeReviewCommit
name|toMerge
parameter_list|)
block|{
name|this
operator|.
name|args
operator|=
name|args
expr_stmt|;
name|this
operator|.
name|toMerge
operator|=
name|toMerge
expr_stmt|;
block|}
DECL|method|getId ()
specifier|final
name|Change
operator|.
name|Id
name|getId
parameter_list|()
block|{
return|return
name|toMerge
operator|.
name|change
argument_list|()
operator|.
name|getId
argument_list|()
return|;
block|}
DECL|method|getCommit ()
specifier|final
name|CodeReviewCommit
name|getCommit
parameter_list|()
block|{
return|return
name|toMerge
return|;
block|}
DECL|method|getDest ()
specifier|protected
specifier|final
name|Branch
operator|.
name|NameKey
name|getDest
parameter_list|()
block|{
return|return
name|toMerge
operator|.
name|change
argument_list|()
operator|.
name|getDest
argument_list|()
return|;
block|}
DECL|method|getProject ()
specifier|protected
specifier|final
name|Project
operator|.
name|NameKey
name|getProject
parameter_list|()
block|{
return|return
name|getDest
argument_list|()
operator|.
name|getParentKey
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|updateRepo (RepoContext ctx)
specifier|public
specifier|final
name|void
name|updateRepo
parameter_list|(
name|RepoContext
name|ctx
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Run the submit strategy implementation and record the merge tip state so
comment|// we can create the ref update.
name|CodeReviewCommit
name|tipBefore
init|=
name|args
operator|.
name|mergeTip
operator|.
name|getCurrentTip
argument_list|()
decl_stmt|;
name|updateRepoImpl
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|CodeReviewCommit
name|tipAfter
init|=
name|args
operator|.
name|mergeTip
operator|.
name|getCurrentTip
argument_list|()
decl_stmt|;
if|if
condition|(
name|Objects
operator|.
name|equals
argument_list|(
name|tipBefore
argument_list|,
name|tipAfter
argument_list|)
condition|)
block|{
return|return;
block|}
elseif|else
if|if
condition|(
name|tipAfter
operator|==
literal|null
condition|)
block|{
name|logDebug
argument_list|(
literal|"No merge tip, no update to perform"
argument_list|)
expr_stmt|;
return|return;
block|}
name|checkProjectConfig
argument_list|(
name|ctx
argument_list|,
name|tipAfter
argument_list|)
expr_stmt|;
comment|// Needed by postUpdate, at which point mergeTip will have advanced further,
comment|// so it's easier to just snapshot the command.
name|command
operator|=
operator|new
name|ReceiveCommand
argument_list|(
name|firstNonNull
argument_list|(
name|tipBefore
argument_list|,
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|)
argument_list|,
name|tipAfter
argument_list|,
name|getDest
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|addRefUpdate
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
DECL|method|checkProjectConfig (RepoContext ctx, CodeReviewCommit commit)
specifier|private
name|void
name|checkProjectConfig
parameter_list|(
name|RepoContext
name|ctx
parameter_list|,
name|CodeReviewCommit
name|commit
parameter_list|)
throws|throws
name|IntegrationException
block|{
name|String
name|refName
init|=
name|getDest
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|RefNames
operator|.
name|REFS_CONFIG
operator|.
name|equals
argument_list|(
name|refName
argument_list|)
condition|)
block|{
name|logDebug
argument_list|(
literal|"Loading new configuration from {}"
argument_list|,
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|)
expr_stmt|;
try|try
block|{
name|ProjectConfig
name|cfg
init|=
operator|new
name|ProjectConfig
argument_list|(
name|getProject
argument_list|()
argument_list|)
decl_stmt|;
name|cfg
operator|.
name|load
argument_list|(
name|ctx
operator|.
name|getRepository
argument_list|()
argument_list|,
name|commit
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IntegrationException
argument_list|(
literal|"Submit would store invalid"
operator|+
literal|" project configuration "
operator|+
name|commit
operator|.
name|name
argument_list|()
operator|+
literal|" for "
operator|+
name|getProject
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|updateChange (ChangeContext ctx)
specifier|public
specifier|final
name|boolean
name|updateChange
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|)
throws|throws
name|Exception
block|{
name|toMerge
operator|.
name|setControl
argument_list|(
name|ctx
operator|.
name|getControl
argument_list|()
argument_list|)
expr_stmt|;
comment|// Update change and notes from ctx.
name|PatchSet
name|newPatchSet
init|=
name|updateChangeImpl
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
name|PatchSet
operator|.
name|Id
name|oldPsId
init|=
name|checkNotNull
argument_list|(
name|toMerge
operator|.
name|getPatchsetId
argument_list|()
argument_list|)
decl_stmt|;
name|PatchSet
operator|.
name|Id
name|newPsId
init|=
name|checkNotNull
argument_list|(
name|ctx
operator|.
name|getChange
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|newPatchSet
operator|==
literal|null
condition|)
block|{
name|checkState
argument_list|(
name|oldPsId
operator|.
name|equals
argument_list|(
name|newPsId
argument_list|)
argument_list|,
literal|"patch set advanced from %s to %s but updateChangeImpl did not return"
operator|+
literal|" new patch set instance"
argument_list|,
name|oldPsId
argument_list|,
name|newPsId
argument_list|)
expr_stmt|;
comment|// Ok to use stale notes to get the old patch set, which didn't change
comment|// during the submit strategy.
name|mergedPatchSet
operator|=
name|checkNotNull
argument_list|(
name|args
operator|.
name|psUtil
operator|.
name|get
argument_list|(
name|ctx
operator|.
name|getDb
argument_list|()
argument_list|,
name|ctx
operator|.
name|getNotes
argument_list|()
argument_list|,
name|oldPsId
argument_list|)
argument_list|,
literal|"missing old patch set %s"
argument_list|,
name|oldPsId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|PatchSet
operator|.
name|Id
name|n
init|=
name|newPatchSet
operator|.
name|getId
argument_list|()
decl_stmt|;
name|checkState
argument_list|(
operator|!
name|n
operator|.
name|equals
argument_list|(
name|oldPsId
argument_list|)
operator|&&
name|n
operator|.
name|equals
argument_list|(
name|newPsId
argument_list|)
argument_list|,
literal|"current patch was %s and is now %s, but updateChangeImpl returned"
operator|+
literal|" new patch set instance at %s"
argument_list|,
name|oldPsId
argument_list|,
name|newPsId
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|mergedPatchSet
operator|=
name|newPatchSet
expr_stmt|;
block|}
name|Change
name|c
init|=
name|ctx
operator|.
name|getChange
argument_list|()
decl_stmt|;
name|Change
operator|.
name|Id
name|id
init|=
name|c
operator|.
name|getId
argument_list|()
decl_stmt|;
try|try
block|{
name|CodeReviewCommit
name|commit
init|=
name|args
operator|.
name|commits
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|CommitMergeStatus
name|s
init|=
name|commit
operator|!=
literal|null
condition|?
name|commit
operator|.
name|getStatusCode
argument_list|()
else|:
literal|null
decl_stmt|;
name|logDebug
argument_list|(
literal|"Status of change {} ({}) on {}: {}"
argument_list|,
name|id
argument_list|,
name|commit
operator|.
name|name
argument_list|()
argument_list|,
name|c
operator|.
name|getDest
argument_list|()
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|checkState
argument_list|(
name|s
operator|!=
literal|null
argument_list|,
literal|"status not set for change %s; expected to previously fail fast"
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|setApproval
argument_list|(
name|ctx
argument_list|,
name|args
operator|.
name|caller
argument_list|)
expr_stmt|;
name|mergeResultRev
operator|=
name|args
operator|.
name|mergeTip
operator|!=
literal|null
condition|?
name|args
operator|.
name|mergeTip
operator|.
name|getMergeResults
argument_list|()
operator|.
name|get
argument_list|(
name|commit
argument_list|)
else|:
literal|null
expr_stmt|;
name|String
name|txt
init|=
name|s
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|ChangeMessage
name|msg
decl_stmt|;
if|if
condition|(
name|s
operator|==
name|CommitMergeStatus
operator|.
name|CLEAN_MERGE
condition|)
block|{
name|msg
operator|=
name|message
argument_list|(
name|ctx
argument_list|,
name|commit
operator|.
name|getPatchsetId
argument_list|()
argument_list|,
name|txt
operator|+
name|getByAccountName
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s
operator|==
name|CommitMergeStatus
operator|.
name|CLEAN_REBASE
operator|||
name|s
operator|==
name|CommitMergeStatus
operator|.
name|CLEAN_PICK
condition|)
block|{
name|msg
operator|=
name|message
argument_list|(
name|ctx
argument_list|,
name|commit
operator|.
name|getPatchsetId
argument_list|()
argument_list|,
name|txt
operator|+
literal|" as "
operator|+
name|commit
operator|.
name|name
argument_list|()
operator|+
name|getByAccountName
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s
operator|==
name|CommitMergeStatus
operator|.
name|ALREADY_MERGED
condition|)
block|{
name|msg
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"unexpected status "
operator|+
name|s
operator|+
literal|" for change "
operator|+
name|c
operator|.
name|getId
argument_list|()
operator|+
literal|"; expected to previously fail fast"
argument_list|)
throw|;
block|}
name|setMerged
argument_list|(
name|ctx
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|err
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Error updating change status for "
operator|+
name|id
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|err
argument_list|)
expr_stmt|;
name|args
operator|.
name|commits
operator|.
name|logProblem
argument_list|(
name|id
argument_list|,
name|msg
argument_list|)
expr_stmt|;
comment|// It's possible this happened before updating anything in the db, but
comment|// it's hard to know for sure, so just return true below to be safe.
block|}
name|updatedChange
operator|=
name|c
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|setApproval (ChangeContext ctx, IdentifiedUser user)
specifier|private
name|void
name|setApproval
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|,
name|IdentifiedUser
name|user
parameter_list|)
throws|throws
name|OrmException
block|{
name|Change
operator|.
name|Id
name|id
init|=
name|ctx
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SubmitRecord
argument_list|>
name|records
init|=
name|args
operator|.
name|commits
operator|.
name|getSubmitRecords
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|PatchSet
operator|.
name|Id
name|oldPsId
init|=
name|toMerge
operator|.
name|getPatchsetId
argument_list|()
decl_stmt|;
name|PatchSet
operator|.
name|Id
name|newPsId
init|=
name|ctx
operator|.
name|getChange
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
decl_stmt|;
name|logDebug
argument_list|(
literal|"Add approval for "
operator|+
name|id
argument_list|)
expr_stmt|;
name|ChangeUpdate
name|origPsUpdate
init|=
name|ctx
operator|.
name|getUpdate
argument_list|(
name|oldPsId
argument_list|)
decl_stmt|;
name|origPsUpdate
operator|.
name|putReviewer
argument_list|(
name|user
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|REVIEWER
argument_list|)
expr_stmt|;
name|LabelNormalizer
operator|.
name|Result
name|normalized
init|=
name|approve
argument_list|(
name|ctx
argument_list|,
name|origPsUpdate
argument_list|)
decl_stmt|;
name|ChangeUpdate
name|newPsUpdate
init|=
name|ctx
operator|.
name|getUpdate
argument_list|(
name|newPsId
argument_list|)
decl_stmt|;
name|newPsUpdate
operator|.
name|merge
argument_list|(
name|records
argument_list|)
expr_stmt|;
comment|// If the submit strategy created a new revision (rebase, cherry-pick), copy
comment|// approvals as well.
if|if
condition|(
operator|!
name|newPsId
operator|.
name|equals
argument_list|(
name|oldPsId
argument_list|)
condition|)
block|{
name|saveApprovals
argument_list|(
name|normalized
argument_list|,
name|ctx
argument_list|,
name|newPsUpdate
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|submitter
operator|=
name|convertPatchSet
argument_list|(
name|newPsId
argument_list|)
operator|.
name|apply
argument_list|(
name|submitter
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|approve (ChangeContext ctx, ChangeUpdate update)
specifier|private
name|LabelNormalizer
operator|.
name|Result
name|approve
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|,
name|ChangeUpdate
name|update
parameter_list|)
throws|throws
name|OrmException
block|{
name|PatchSet
operator|.
name|Id
name|psId
init|=
name|update
operator|.
name|getPatchSetId
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|PatchSetApproval
operator|.
name|Key
argument_list|,
name|PatchSetApproval
argument_list|>
name|byKey
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSetApproval
name|psa
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
name|ctx
operator|.
name|getControl
argument_list|()
argument_list|,
name|psId
argument_list|)
control|)
block|{
name|byKey
operator|.
name|put
argument_list|(
name|psa
operator|.
name|getKey
argument_list|()
argument_list|,
name|psa
argument_list|)
expr_stmt|;
block|}
name|submitter
operator|=
operator|new
name|PatchSetApproval
argument_list|(
operator|new
name|PatchSetApproval
operator|.
name|Key
argument_list|(
name|psId
argument_list|,
name|ctx
operator|.
name|getUser
argument_list|()
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|LabelId
operator|.
name|SUBMIT
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|ctx
operator|.
name|getWhen
argument_list|()
argument_list|)
expr_stmt|;
name|byKey
operator|.
name|put
argument_list|(
name|submitter
operator|.
name|getKey
argument_list|()
argument_list|,
name|submitter
argument_list|)
expr_stmt|;
name|submitter
operator|.
name|setValue
argument_list|(
operator|(
name|short
operator|)
literal|1
argument_list|)
expr_stmt|;
name|submitter
operator|.
name|setGranted
argument_list|(
name|ctx
operator|.
name|getWhen
argument_list|()
argument_list|)
expr_stmt|;
comment|// Flatten out existing approvals for this patch set based upon the current
comment|// permissions. Once the change is closed the approvals are not updated at
comment|// presentation view time, except for zero votes used to indicate a reviewer
comment|// was added. So we need to make sure votes are accurate now. This way if
comment|// permissions get modified in the future, historical records stay accurate.
name|LabelNormalizer
operator|.
name|Result
name|normalized
init|=
name|args
operator|.
name|labelNormalizer
operator|.
name|normalize
argument_list|(
name|ctx
operator|.
name|getControl
argument_list|()
argument_list|,
name|byKey
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|update
operator|.
name|putApproval
argument_list|(
name|submitter
operator|.
name|getLabel
argument_list|()
argument_list|,
name|submitter
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|saveApprovals
argument_list|(
name|normalized
argument_list|,
name|ctx
argument_list|,
name|update
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|normalized
return|;
block|}
DECL|method|saveApprovals (LabelNormalizer.Result normalized, ChangeContext ctx, ChangeUpdate update, boolean includeUnchanged)
specifier|private
name|void
name|saveApprovals
parameter_list|(
name|LabelNormalizer
operator|.
name|Result
name|normalized
parameter_list|,
name|ChangeContext
name|ctx
parameter_list|,
name|ChangeUpdate
name|update
parameter_list|,
name|boolean
name|includeUnchanged
parameter_list|)
throws|throws
name|OrmException
block|{
name|PatchSet
operator|.
name|Id
name|psId
init|=
name|update
operator|.
name|getPatchSetId
argument_list|()
decl_stmt|;
name|ctx
operator|.
name|getDb
argument_list|()
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|upsert
argument_list|(
name|convertPatchSet
argument_list|(
name|normalized
operator|.
name|getNormalized
argument_list|()
argument_list|,
name|psId
argument_list|)
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|getDb
argument_list|()
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|delete
argument_list|(
name|convertPatchSet
argument_list|(
name|normalized
operator|.
name|deleted
argument_list|()
argument_list|,
name|psId
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|PatchSetApproval
name|psa
range|:
name|normalized
operator|.
name|updated
argument_list|()
control|)
block|{
name|update
operator|.
name|putApprovalFor
argument_list|(
name|psa
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|psa
operator|.
name|getLabel
argument_list|()
argument_list|,
name|psa
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|PatchSetApproval
name|psa
range|:
name|normalized
operator|.
name|deleted
argument_list|()
control|)
block|{
name|update
operator|.
name|removeApprovalFor
argument_list|(
name|psa
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|psa
operator|.
name|getLabel
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// TODO(dborowitz): Don't use a label in notedb; just check when status
comment|// change happened.
for|for
control|(
name|PatchSetApproval
name|psa
range|:
name|normalized
operator|.
name|unchanged
argument_list|()
control|)
block|{
if|if
condition|(
name|includeUnchanged
operator|||
name|psa
operator|.
name|isSubmit
argument_list|()
condition|)
block|{
name|logDebug
argument_list|(
literal|"Adding submit label "
operator|+
name|psa
argument_list|)
expr_stmt|;
name|update
operator|.
name|putApprovalFor
argument_list|(
name|psa
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|psa
operator|.
name|getLabel
argument_list|()
argument_list|,
name|psa
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|Function
argument_list|<
name|PatchSetApproval
argument_list|,
name|PatchSetApproval
argument_list|>
DECL|method|convertPatchSet (final PatchSet.Id psId)
name|convertPatchSet
parameter_list|(
specifier|final
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|)
block|{
return|return
operator|new
name|Function
argument_list|<
name|PatchSetApproval
argument_list|,
name|PatchSetApproval
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PatchSetApproval
name|apply
parameter_list|(
name|PatchSetApproval
name|in
parameter_list|)
block|{
if|if
condition|(
name|in
operator|.
name|getPatchSetId
argument_list|()
operator|.
name|equals
argument_list|(
name|psId
argument_list|)
condition|)
block|{
return|return
name|in
return|;
block|}
else|else
block|{
return|return
operator|new
name|PatchSetApproval
argument_list|(
name|psId
argument_list|,
name|in
argument_list|)
return|;
block|}
block|}
block|}
return|;
block|}
DECL|method|convertPatchSet ( Iterable<PatchSetApproval> approvals, PatchSet.Id psId)
specifier|private
specifier|static
name|Iterable
argument_list|<
name|PatchSetApproval
argument_list|>
name|convertPatchSet
parameter_list|(
name|Iterable
argument_list|<
name|PatchSetApproval
argument_list|>
name|approvals
parameter_list|,
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|)
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|approvals
argument_list|,
name|convertPatchSet
argument_list|(
name|psId
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getByAccountName ()
specifier|private
name|String
name|getByAccountName
parameter_list|()
block|{
name|checkNotNull
argument_list|(
name|submitter
argument_list|,
literal|"getByAccountName called before submitter populated"
argument_list|)
expr_stmt|;
name|Account
name|account
init|=
name|args
operator|.
name|accountCache
operator|.
name|get
argument_list|(
name|submitter
operator|.
name|getAccountId
argument_list|()
argument_list|)
operator|.
name|getAccount
argument_list|()
decl_stmt|;
if|if
condition|(
name|account
operator|!=
literal|null
operator|&&
name|account
operator|.
name|getFullName
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
literal|" by "
operator|+
name|account
operator|.
name|getFullName
argument_list|()
return|;
block|}
return|return
literal|""
return|;
block|}
DECL|method|message (ChangeContext ctx, PatchSet.Id psId, String body)
specifier|private
name|ChangeMessage
name|message
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|,
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|,
name|String
name|body
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|psId
argument_list|)
expr_stmt|;
name|String
name|uuid
decl_stmt|;
try|try
block|{
name|uuid
operator|=
name|ChangeUtil
operator|.
name|messageUUID
argument_list|(
name|ctx
operator|.
name|getDb
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
name|ChangeMessage
name|m
init|=
operator|new
name|ChangeMessage
argument_list|(
operator|new
name|ChangeMessage
operator|.
name|Key
argument_list|(
name|psId
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|uuid
argument_list|)
argument_list|,
literal|null
argument_list|,
name|ctx
operator|.
name|getWhen
argument_list|()
argument_list|,
name|psId
argument_list|)
decl_stmt|;
name|m
operator|.
name|setMessage
argument_list|(
name|body
argument_list|)
expr_stmt|;
return|return
name|m
return|;
block|}
DECL|method|setMerged (ChangeContext ctx, ChangeMessage msg)
specifier|private
name|void
name|setMerged
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|,
name|ChangeMessage
name|msg
parameter_list|)
throws|throws
name|OrmException
block|{
name|Change
name|c
init|=
name|ctx
operator|.
name|getChange
argument_list|()
decl_stmt|;
name|ReviewDb
name|db
init|=
name|ctx
operator|.
name|getDb
argument_list|()
decl_stmt|;
name|logDebug
argument_list|(
literal|"Setting change {} merged"
argument_list|,
name|c
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|.
name|setStatus
argument_list|(
name|Change
operator|.
name|Status
operator|.
name|MERGED
argument_list|)
expr_stmt|;
name|c
operator|.
name|setSubmissionId
argument_list|(
name|args
operator|.
name|submissionId
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|saveChange
argument_list|()
expr_stmt|;
comment|// TODO(dborowitz): We need to be able to change the author of the message,
comment|// which is not the user from the update context. addMergedMessage was able
comment|// to do this in the past.
if|if
condition|(
name|msg
operator|!=
literal|null
condition|)
block|{
name|args
operator|.
name|cmUtil
operator|.
name|addChangeMessage
argument_list|(
name|db
argument_list|,
name|ctx
operator|.
name|getUpdate
argument_list|(
name|msg
operator|.
name|getPatchSetId
argument_list|()
argument_list|)
argument_list|,
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|postUpdate (Context ctx)
specifier|public
specifier|final
name|void
name|postUpdate
parameter_list|(
name|Context
name|ctx
parameter_list|)
throws|throws
name|Exception
block|{
name|postUpdateImpl
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
if|if
condition|(
name|command
operator|!=
literal|null
condition|)
block|{
name|args
operator|.
name|tagCache
operator|.
name|updateFastForward
argument_list|(
name|getProject
argument_list|()
argument_list|,
name|command
operator|.
name|getRefName
argument_list|()
argument_list|,
name|command
operator|.
name|getOldId
argument_list|()
argument_list|,
name|command
operator|.
name|getNewId
argument_list|()
argument_list|)
expr_stmt|;
comment|// TODO(dborowitz): Move to BatchUpdate? Would also allow us to run once
comment|// per project even if multiple changes to refs/meta/config are submitted.
if|if
condition|(
name|RefNames
operator|.
name|REFS_CONFIG
operator|.
name|equals
argument_list|(
name|getDest
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
condition|)
block|{
name|args
operator|.
name|projectCache
operator|.
name|evict
argument_list|(
name|getProject
argument_list|()
argument_list|)
expr_stmt|;
name|ProjectState
name|p
init|=
name|args
operator|.
name|projectCache
operator|.
name|get
argument_list|(
name|getProject
argument_list|()
argument_list|)
decl_stmt|;
name|args
operator|.
name|repoManager
operator|.
name|setProjectDescription
argument_list|(
name|p
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|p
operator|.
name|getProject
argument_list|()
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Assume the change must have been merged at this point, otherwise we would
comment|// have failed fast in one of the other steps.
try|try
block|{
name|args
operator|.
name|mergedSenderFactory
operator|.
name|create
argument_list|(
name|getId
argument_list|()
argument_list|,
name|submitter
operator|.
name|getAccountId
argument_list|()
argument_list|)
operator|.
name|sendAsync
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot email merged notification for "
operator|+
name|getId
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mergeResultRev
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|args
operator|.
name|hooks
operator|.
name|doChangeMergedHook
argument_list|(
name|updatedChange
argument_list|,
name|args
operator|.
name|accountCache
operator|.
name|get
argument_list|(
name|submitter
operator|.
name|getAccountId
argument_list|()
argument_list|)
operator|.
name|getAccount
argument_list|()
argument_list|,
name|mergedPatchSet
argument_list|,
name|ctx
operator|.
name|getDb
argument_list|()
argument_list|,
name|mergeResultRev
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|ex
parameter_list|)
block|{
name|logError
argument_list|(
literal|"Cannot run hook for submitted patch set "
operator|+
name|getId
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * @see #updateRepo(RepoContext)    * @param ctx    */
DECL|method|updateRepoImpl (RepoContext ctx)
specifier|protected
name|void
name|updateRepoImpl
parameter_list|(
name|RepoContext
name|ctx
parameter_list|)
throws|throws
name|Exception
block|{   }
comment|/**    * @see #updateChange(ChangeContext)    * @param ctx    * @return a new patch set if one was created by the submit strategy, or null    *     if not.    */
DECL|method|updateChangeImpl (ChangeContext ctx)
specifier|protected
name|PatchSet
name|updateChangeImpl
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
comment|/**    * @see #postUpdate(Context)    * @param ctx    */
DECL|method|postUpdateImpl (Context ctx)
specifier|protected
name|void
name|postUpdateImpl
parameter_list|(
name|Context
name|ctx
parameter_list|)
throws|throws
name|Exception
block|{   }
DECL|method|logDebug (String msg, Object... args)
specifier|protected
specifier|final
name|void
name|logDebug
parameter_list|(
name|String
name|msg
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"["
operator|+
name|this
operator|.
name|args
operator|.
name|submissionId
operator|+
literal|"]"
operator|+
name|msg
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|logWarn (String msg, Throwable t)
specifier|protected
specifier|final
name|void
name|logWarn
parameter_list|(
name|String
name|msg
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|log
operator|.
name|isWarnEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"["
operator|+
name|args
operator|.
name|submissionId
operator|+
literal|"]"
operator|+
name|msg
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|logError (String msg, Throwable t)
specifier|protected
name|void
name|logError
parameter_list|(
name|String
name|msg
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|log
operator|.
name|isErrorEnabled
argument_list|()
condition|)
block|{
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"["
operator|+
name|args
operator|.
name|submissionId
operator|+
literal|"]"
operator|+
name|msg
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|error
argument_list|(
literal|"["
operator|+
name|args
operator|.
name|submissionId
operator|+
literal|"]"
operator|+
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|logError (String msg)
specifier|protected
name|void
name|logError
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|logError
argument_list|(
name|msg
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

