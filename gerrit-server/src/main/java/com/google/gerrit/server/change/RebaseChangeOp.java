begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|change
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
name|Preconditions
operator|.
name|checkState
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
name|Nullable
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
name|api
operator|.
name|changes
operator|.
name|NotifyHandling
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
name|ResourceConflictException
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
name|RevId
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
name|change
operator|.
name|RebaseUtil
operator|.
name|Base
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
name|MergeUtil
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
name|ChangeControl
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|assistedinject
operator|.
name|Assisted
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|assistedinject
operator|.
name|AssistedInject
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
name|CommitBuilder
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
name|lib
operator|.
name|PersonIdent
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
name|merge
operator|.
name|ThreeWayMerger
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
name|revwalk
operator|.
name|RevCommit
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
name|revwalk
operator|.
name|RevWalk
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

begin_class
DECL|class|RebaseChangeOp
specifier|public
class|class
name|RebaseChangeOp
extends|extends
name|BatchUpdate
operator|.
name|Op
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (ChangeControl ctl, PatchSet originalPatchSet, @Nullable String baseCommitish)
name|RebaseChangeOp
name|create
parameter_list|(
name|ChangeControl
name|ctl
parameter_list|,
name|PatchSet
name|originalPatchSet
parameter_list|,
annotation|@
name|Nullable
name|String
name|baseCommitish
parameter_list|)
function_decl|;
block|}
DECL|field|patchSetInserterFactory
specifier|private
specifier|final
name|PatchSetInserter
operator|.
name|Factory
name|patchSetInserterFactory
decl_stmt|;
DECL|field|mergeUtilFactory
specifier|private
specifier|final
name|MergeUtil
operator|.
name|Factory
name|mergeUtilFactory
decl_stmt|;
DECL|field|rebaseUtil
specifier|private
specifier|final
name|RebaseUtil
name|rebaseUtil
decl_stmt|;
DECL|field|changeResourceFactory
specifier|private
specifier|final
name|ChangeResource
operator|.
name|Factory
name|changeResourceFactory
decl_stmt|;
DECL|field|ctl
specifier|private
specifier|final
name|ChangeControl
name|ctl
decl_stmt|;
DECL|field|originalPatchSet
specifier|private
specifier|final
name|PatchSet
name|originalPatchSet
decl_stmt|;
DECL|field|baseCommitish
specifier|private
name|String
name|baseCommitish
decl_stmt|;
DECL|field|committerIdent
specifier|private
name|PersonIdent
name|committerIdent
decl_stmt|;
DECL|field|fireRevisionCreated
specifier|private
name|boolean
name|fireRevisionCreated
init|=
literal|true
decl_stmt|;
DECL|field|validate
specifier|private
name|CommitValidators
operator|.
name|Policy
name|validate
decl_stmt|;
DECL|field|checkAddPatchSetPermission
specifier|private
name|boolean
name|checkAddPatchSetPermission
init|=
literal|true
decl_stmt|;
DECL|field|forceContentMerge
specifier|private
name|boolean
name|forceContentMerge
decl_stmt|;
DECL|field|copyApprovals
specifier|private
name|boolean
name|copyApprovals
init|=
literal|true
decl_stmt|;
DECL|field|detailedCommitMessage
specifier|private
name|boolean
name|detailedCommitMessage
decl_stmt|;
DECL|field|postMessage
specifier|private
name|boolean
name|postMessage
init|=
literal|true
decl_stmt|;
DECL|field|rebasedCommit
specifier|private
name|RevCommit
name|rebasedCommit
decl_stmt|;
DECL|field|rebasedPatchSetId
specifier|private
name|PatchSet
operator|.
name|Id
name|rebasedPatchSetId
decl_stmt|;
DECL|field|patchSetInserter
specifier|private
name|PatchSetInserter
name|patchSetInserter
decl_stmt|;
DECL|field|rebasedPatchSet
specifier|private
name|PatchSet
name|rebasedPatchSet
decl_stmt|;
annotation|@
name|AssistedInject
DECL|method|RebaseChangeOp ( PatchSetInserter.Factory patchSetInserterFactory, MergeUtil.Factory mergeUtilFactory, RebaseUtil rebaseUtil, ChangeResource.Factory changeResourceFactory, @Assisted ChangeControl ctl, @Assisted PatchSet originalPatchSet, @Assisted @Nullable String baseCommitish)
name|RebaseChangeOp
parameter_list|(
name|PatchSetInserter
operator|.
name|Factory
name|patchSetInserterFactory
parameter_list|,
name|MergeUtil
operator|.
name|Factory
name|mergeUtilFactory
parameter_list|,
name|RebaseUtil
name|rebaseUtil
parameter_list|,
name|ChangeResource
operator|.
name|Factory
name|changeResourceFactory
parameter_list|,
annotation|@
name|Assisted
name|ChangeControl
name|ctl
parameter_list|,
annotation|@
name|Assisted
name|PatchSet
name|originalPatchSet
parameter_list|,
annotation|@
name|Assisted
annotation|@
name|Nullable
name|String
name|baseCommitish
parameter_list|)
block|{
name|this
operator|.
name|patchSetInserterFactory
operator|=
name|patchSetInserterFactory
expr_stmt|;
name|this
operator|.
name|mergeUtilFactory
operator|=
name|mergeUtilFactory
expr_stmt|;
name|this
operator|.
name|rebaseUtil
operator|=
name|rebaseUtil
expr_stmt|;
name|this
operator|.
name|changeResourceFactory
operator|=
name|changeResourceFactory
expr_stmt|;
name|this
operator|.
name|ctl
operator|=
name|ctl
expr_stmt|;
name|this
operator|.
name|originalPatchSet
operator|=
name|originalPatchSet
expr_stmt|;
name|this
operator|.
name|baseCommitish
operator|=
name|baseCommitish
expr_stmt|;
block|}
DECL|method|setCommitterIdent (PersonIdent committerIdent)
specifier|public
name|RebaseChangeOp
name|setCommitterIdent
parameter_list|(
name|PersonIdent
name|committerIdent
parameter_list|)
block|{
name|this
operator|.
name|committerIdent
operator|=
name|committerIdent
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setValidatePolicy (CommitValidators.Policy validate)
specifier|public
name|RebaseChangeOp
name|setValidatePolicy
parameter_list|(
name|CommitValidators
operator|.
name|Policy
name|validate
parameter_list|)
block|{
name|this
operator|.
name|validate
operator|=
name|validate
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setCheckAddPatchSetPermission ( boolean checkAddPatchSetPermission)
specifier|public
name|RebaseChangeOp
name|setCheckAddPatchSetPermission
parameter_list|(
name|boolean
name|checkAddPatchSetPermission
parameter_list|)
block|{
name|this
operator|.
name|checkAddPatchSetPermission
operator|=
name|checkAddPatchSetPermission
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setFireRevisionCreated (boolean fireRevisionCreated)
specifier|public
name|RebaseChangeOp
name|setFireRevisionCreated
parameter_list|(
name|boolean
name|fireRevisionCreated
parameter_list|)
block|{
name|this
operator|.
name|fireRevisionCreated
operator|=
name|fireRevisionCreated
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setForceContentMerge (boolean forceContentMerge)
specifier|public
name|RebaseChangeOp
name|setForceContentMerge
parameter_list|(
name|boolean
name|forceContentMerge
parameter_list|)
block|{
name|this
operator|.
name|forceContentMerge
operator|=
name|forceContentMerge
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setCopyApprovals (boolean copyApprovals)
specifier|public
name|RebaseChangeOp
name|setCopyApprovals
parameter_list|(
name|boolean
name|copyApprovals
parameter_list|)
block|{
name|this
operator|.
name|copyApprovals
operator|=
name|copyApprovals
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setDetailedCommitMessage ( boolean detailedCommitMessage)
specifier|public
name|RebaseChangeOp
name|setDetailedCommitMessage
parameter_list|(
name|boolean
name|detailedCommitMessage
parameter_list|)
block|{
name|this
operator|.
name|detailedCommitMessage
operator|=
name|detailedCommitMessage
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setPostMessage (boolean postMessage)
specifier|public
name|RebaseChangeOp
name|setPostMessage
parameter_list|(
name|boolean
name|postMessage
parameter_list|)
block|{
name|this
operator|.
name|postMessage
operator|=
name|postMessage
expr_stmt|;
return|return
name|this
return|;
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
name|MergeConflictException
throws|,
name|InvalidChangeOperationException
throws|,
name|RestApiException
throws|,
name|IOException
throws|,
name|OrmException
throws|,
name|NoSuchChangeException
block|{
comment|// Ok that originalPatchSet was not read in a transaction, since we just
comment|// need its revision.
name|RevId
name|oldRev
init|=
name|originalPatchSet
operator|.
name|getRevision
argument_list|()
decl_stmt|;
name|RevWalk
name|rw
init|=
name|ctx
operator|.
name|getRevWalk
argument_list|()
decl_stmt|;
name|RevCommit
name|original
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|ObjectId
operator|.
name|fromString
argument_list|(
name|oldRev
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|rw
operator|.
name|parseBody
argument_list|(
name|original
argument_list|)
expr_stmt|;
name|RevCommit
name|baseCommit
decl_stmt|;
if|if
condition|(
name|baseCommitish
operator|!=
literal|null
condition|)
block|{
name|baseCommit
operator|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|ctx
operator|.
name|getRepository
argument_list|()
operator|.
name|resolve
argument_list|(
name|baseCommitish
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|baseCommit
operator|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|rebaseUtil
operator|.
name|findBaseRevision
argument_list|(
name|originalPatchSet
argument_list|,
name|ctl
operator|.
name|getChange
argument_list|()
operator|.
name|getDest
argument_list|()
argument_list|,
name|ctx
operator|.
name|getRepository
argument_list|()
argument_list|,
name|ctx
operator|.
name|getRevWalk
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|newCommitMessage
decl_stmt|;
if|if
condition|(
name|detailedCommitMessage
condition|)
block|{
name|rw
operator|.
name|parseBody
argument_list|(
name|baseCommit
argument_list|)
expr_stmt|;
name|newCommitMessage
operator|=
name|newMergeUtil
argument_list|()
operator|.
name|createCommitMessageOnSubmit
argument_list|(
name|original
argument_list|,
name|baseCommit
argument_list|,
name|ctl
argument_list|,
name|originalPatchSet
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newCommitMessage
operator|=
name|original
operator|.
name|getFullMessage
argument_list|()
expr_stmt|;
block|}
name|rebasedCommit
operator|=
name|rebaseCommit
argument_list|(
name|ctx
argument_list|,
name|original
argument_list|,
name|baseCommit
argument_list|,
name|newCommitMessage
argument_list|)
expr_stmt|;
name|RevId
name|baseRevId
init|=
operator|new
name|RevId
argument_list|(
operator|(
name|baseCommitish
operator|!=
literal|null
operator|)
condition|?
name|baseCommitish
else|:
name|ObjectId
operator|.
name|toString
argument_list|(
name|baseCommit
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Base
name|base
init|=
name|rebaseUtil
operator|.
name|parseBase
argument_list|(
operator|new
name|RevisionResource
argument_list|(
name|changeResourceFactory
operator|.
name|create
argument_list|(
name|ctl
argument_list|)
argument_list|,
name|originalPatchSet
argument_list|)
argument_list|,
name|baseRevId
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|rebasedPatchSetId
operator|=
name|ChangeUtil
operator|.
name|nextPatchSetId
argument_list|(
name|ctx
operator|.
name|getRepository
argument_list|()
argument_list|,
name|ctl
operator|.
name|getChange
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
expr_stmt|;
name|patchSetInserter
operator|=
name|patchSetInserterFactory
operator|.
name|create
argument_list|(
name|ctl
argument_list|,
name|rebasedPatchSetId
argument_list|,
name|rebasedCommit
argument_list|)
operator|.
name|setDescription
argument_list|(
literal|"Rebase"
argument_list|)
operator|.
name|setDraft
argument_list|(
name|originalPatchSet
operator|.
name|isDraft
argument_list|()
argument_list|)
operator|.
name|setNotify
argument_list|(
name|NotifyHandling
operator|.
name|NONE
argument_list|)
operator|.
name|setFireRevisionCreated
argument_list|(
name|fireRevisionCreated
argument_list|)
operator|.
name|setCopyApprovals
argument_list|(
name|copyApprovals
argument_list|)
operator|.
name|setCheckAddPatchSetPermission
argument_list|(
name|checkAddPatchSetPermission
argument_list|)
expr_stmt|;
if|if
condition|(
name|postMessage
condition|)
block|{
name|patchSetInserter
operator|.
name|setMessage
argument_list|(
literal|"Patch Set "
operator|+
name|rebasedPatchSetId
operator|.
name|get
argument_list|()
operator|+
literal|": Patch Set "
operator|+
name|originalPatchSet
operator|.
name|getId
argument_list|()
operator|.
name|get
argument_list|()
operator|+
literal|" was rebased"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|base
operator|!=
literal|null
condition|)
block|{
name|patchSetInserter
operator|.
name|setGroups
argument_list|(
name|base
operator|.
name|patchSet
argument_list|()
operator|.
name|getGroups
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|validate
operator|!=
literal|null
condition|)
block|{
name|patchSetInserter
operator|.
name|setValidatePolicy
argument_list|(
name|validate
argument_list|)
expr_stmt|;
block|}
name|patchSetInserter
operator|.
name|updateRepo
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateChange (ChangeContext ctx)
specifier|public
name|boolean
name|updateChange
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|)
throws|throws
name|ResourceConflictException
throws|,
name|OrmException
throws|,
name|IOException
block|{
name|boolean
name|ret
init|=
name|patchSetInserter
operator|.
name|updateChange
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
name|rebasedPatchSet
operator|=
name|patchSetInserter
operator|.
name|getPatchSet
argument_list|()
expr_stmt|;
return|return
name|ret
return|;
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
name|patchSetInserter
operator|.
name|postUpdate
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
block|}
DECL|method|getRebasedCommit ()
specifier|public
name|RevCommit
name|getRebasedCommit
parameter_list|()
block|{
name|checkState
argument_list|(
name|rebasedCommit
operator|!=
literal|null
argument_list|,
literal|"getRebasedCommit() only valid after updateRepo"
argument_list|)
expr_stmt|;
return|return
name|rebasedCommit
return|;
block|}
DECL|method|getPatchSetId ()
specifier|public
name|PatchSet
operator|.
name|Id
name|getPatchSetId
parameter_list|()
block|{
name|checkState
argument_list|(
name|rebasedPatchSetId
operator|!=
literal|null
argument_list|,
literal|"getPatchSetId() only valid after updateRepo"
argument_list|)
expr_stmt|;
return|return
name|rebasedPatchSetId
return|;
block|}
DECL|method|getPatchSet ()
specifier|public
name|PatchSet
name|getPatchSet
parameter_list|()
block|{
name|checkState
argument_list|(
name|rebasedPatchSet
operator|!=
literal|null
argument_list|,
literal|"getPatchSet() only valid after executing update"
argument_list|)
expr_stmt|;
return|return
name|rebasedPatchSet
return|;
block|}
DECL|method|newMergeUtil ()
specifier|private
name|MergeUtil
name|newMergeUtil
parameter_list|()
block|{
name|ProjectState
name|project
init|=
name|ctl
operator|.
name|getProjectControl
argument_list|()
operator|.
name|getProjectState
argument_list|()
decl_stmt|;
return|return
name|forceContentMerge
condition|?
name|mergeUtilFactory
operator|.
name|create
argument_list|(
name|project
argument_list|,
literal|true
argument_list|)
else|:
name|mergeUtilFactory
operator|.
name|create
argument_list|(
name|project
argument_list|)
return|;
block|}
comment|/**    * Rebase a commit.    *    * @param ctx repo context.    * @param original the commit to rebase.    * @param base base to rebase against.    * @return the rebased commit.    * @throws MergeConflictException the rebase failed due to a merge conflict.    * @throws IOException the merge failed for another reason.    */
DECL|method|rebaseCommit ( RepoContext ctx, RevCommit original, ObjectId base, String commitMessage)
specifier|private
name|RevCommit
name|rebaseCommit
parameter_list|(
name|RepoContext
name|ctx
parameter_list|,
name|RevCommit
name|original
parameter_list|,
name|ObjectId
name|base
parameter_list|,
name|String
name|commitMessage
parameter_list|)
throws|throws
name|ResourceConflictException
throws|,
name|IOException
block|{
name|RevCommit
name|parentCommit
init|=
name|original
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|base
operator|.
name|equals
argument_list|(
name|parentCommit
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"Change is already up to date."
argument_list|)
throw|;
block|}
name|ThreeWayMerger
name|merger
init|=
name|newMergeUtil
argument_list|()
operator|.
name|newThreeWayMerger
argument_list|(
name|ctx
operator|.
name|getRepository
argument_list|()
argument_list|,
name|ctx
operator|.
name|getInserter
argument_list|()
argument_list|)
decl_stmt|;
name|merger
operator|.
name|setBase
argument_list|(
name|parentCommit
argument_list|)
expr_stmt|;
name|merger
operator|.
name|merge
argument_list|(
name|original
argument_list|,
name|base
argument_list|)
expr_stmt|;
if|if
condition|(
name|merger
operator|.
name|getResultTreeId
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|MergeConflictException
argument_list|(
literal|"The change could not be rebased due to a conflict during merge."
argument_list|)
throw|;
block|}
name|CommitBuilder
name|cb
init|=
operator|new
name|CommitBuilder
argument_list|()
decl_stmt|;
name|cb
operator|.
name|setTreeId
argument_list|(
name|merger
operator|.
name|getResultTreeId
argument_list|()
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setParentId
argument_list|(
name|base
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setAuthor
argument_list|(
name|original
operator|.
name|getAuthorIdent
argument_list|()
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setMessage
argument_list|(
name|commitMessage
argument_list|)
expr_stmt|;
if|if
condition|(
name|committerIdent
operator|!=
literal|null
condition|)
block|{
name|cb
operator|.
name|setCommitter
argument_list|(
name|committerIdent
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cb
operator|.
name|setCommitter
argument_list|(
name|ctx
operator|.
name|getIdentifiedUser
argument_list|()
operator|.
name|newCommitterIdent
argument_list|(
name|ctx
operator|.
name|getWhen
argument_list|()
argument_list|,
name|ctx
operator|.
name|getTimeZone
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ObjectId
name|objectId
init|=
name|ctx
operator|.
name|getInserter
argument_list|()
operator|.
name|insert
argument_list|(
name|cb
argument_list|)
decl_stmt|;
name|ctx
operator|.
name|getInserter
argument_list|()
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|ctx
operator|.
name|getRevWalk
argument_list|()
operator|.
name|parseCommit
argument_list|(
name|objectId
argument_list|)
return|;
block|}
block|}
end_class

end_unit

