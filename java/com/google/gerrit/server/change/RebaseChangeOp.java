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
name|entities
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
name|CurrentUser
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
name|GroupCollector
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
name|notedb
operator|.
name|ChangeNotes
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
name|permissions
operator|.
name|PermissionBackendException
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
name|ProjectCache
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
name|gerrit
operator|.
name|server
operator|.
name|update
operator|.
name|BatchUpdateOp
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
name|update
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
name|update
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
name|update
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
name|inject
operator|.
name|Inject
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
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_class
DECL|class|RebaseChangeOp
specifier|public
class|class
name|RebaseChangeOp
implements|implements
name|BatchUpdateOp
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (ChangeNotes notes, PatchSet originalPatchSet, ObjectId baseCommitId)
name|RebaseChangeOp
name|create
parameter_list|(
name|ChangeNotes
name|notes
parameter_list|,
name|PatchSet
name|originalPatchSet
parameter_list|,
name|ObjectId
name|baseCommitId
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
DECL|field|notes
specifier|private
specifier|final
name|ChangeNotes
name|notes
decl_stmt|;
DECL|field|originalPatchSet
specifier|private
specifier|final
name|PatchSet
name|originalPatchSet
decl_stmt|;
DECL|field|identifiedUserFactory
specifier|private
specifier|final
name|IdentifiedUser
operator|.
name|GenericFactory
name|identifiedUserFactory
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|baseCommitId
specifier|private
name|ObjectId
name|baseCommitId
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
name|boolean
name|validate
init|=
literal|true
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
DECL|field|sendEmail
specifier|private
name|boolean
name|sendEmail
init|=
literal|true
decl_stmt|;
DECL|field|matchAuthorToCommitterDate
specifier|private
name|boolean
name|matchAuthorToCommitterDate
init|=
literal|false
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
name|Inject
DECL|method|RebaseChangeOp ( PatchSetInserter.Factory patchSetInserterFactory, MergeUtil.Factory mergeUtilFactory, RebaseUtil rebaseUtil, ChangeResource.Factory changeResourceFactory, IdentifiedUser.GenericFactory identifiedUserFactory, ProjectCache projectCache, @Assisted ChangeNotes notes, @Assisted PatchSet originalPatchSet, @Assisted ObjectId baseCommitId)
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
name|IdentifiedUser
operator|.
name|GenericFactory
name|identifiedUserFactory
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
annotation|@
name|Assisted
name|ChangeNotes
name|notes
parameter_list|,
annotation|@
name|Assisted
name|PatchSet
name|originalPatchSet
parameter_list|,
annotation|@
name|Assisted
name|ObjectId
name|baseCommitId
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
name|identifiedUserFactory
operator|=
name|identifiedUserFactory
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|notes
operator|=
name|notes
expr_stmt|;
name|this
operator|.
name|originalPatchSet
operator|=
name|originalPatchSet
expr_stmt|;
name|this
operator|.
name|baseCommitId
operator|=
name|baseCommitId
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
DECL|method|setValidate (boolean validate)
specifier|public
name|RebaseChangeOp
name|setValidate
parameter_list|(
name|boolean
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
DECL|method|setCheckAddPatchSetPermission (boolean checkAddPatchSetPermission)
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
DECL|method|setDetailedCommitMessage (boolean detailedCommitMessage)
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
DECL|method|setSendEmail (boolean sendEmail)
specifier|public
name|RebaseChangeOp
name|setSendEmail
parameter_list|(
name|boolean
name|sendEmail
parameter_list|)
block|{
name|this
operator|.
name|sendEmail
operator|=
name|sendEmail
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setMatchAuthorToCommitterDate (boolean matchAuthorToCommitterDate)
specifier|public
name|RebaseChangeOp
name|setMatchAuthorToCommitterDate
parameter_list|(
name|boolean
name|matchAuthorToCommitterDate
parameter_list|)
block|{
name|this
operator|.
name|matchAuthorToCommitterDate
operator|=
name|matchAuthorToCommitterDate
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
name|NoSuchChangeException
throws|,
name|PermissionBackendException
block|{
comment|// Ok that originalPatchSet was not read in a transaction, since we just
comment|// need its revision.
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
name|originalPatchSet
operator|.
name|commitId
argument_list|()
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
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|baseCommitId
argument_list|)
decl_stmt|;
name|CurrentUser
name|changeOwner
init|=
name|identifiedUserFactory
operator|.
name|create
argument_list|(
name|notes
operator|.
name|getChange
argument_list|()
operator|.
name|getOwner
argument_list|()
argument_list|)
decl_stmt|;
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
name|notes
argument_list|,
name|originalPatchSet
operator|.
name|id
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
name|notes
argument_list|,
name|changeOwner
argument_list|)
argument_list|,
name|originalPatchSet
argument_list|)
argument_list|,
name|baseCommitId
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|rebasedPatchSetId
operator|=
name|ChangeUtil
operator|.
name|nextPatchSetIdFromChangeRefs
argument_list|(
name|ctx
operator|.
name|getRepoView
argument_list|()
operator|.
name|getRefs
argument_list|(
name|originalPatchSet
operator|.
name|id
argument_list|()
operator|.
name|changeId
argument_list|()
operator|.
name|toRefPrefix
argument_list|()
argument_list|)
operator|.
name|keySet
argument_list|()
argument_list|,
name|notes
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
name|notes
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
name|setFireRevisionCreated
argument_list|(
name|fireRevisionCreated
argument_list|)
operator|.
name|setCheckAddPatchSetPermission
argument_list|(
name|checkAddPatchSetPermission
argument_list|)
operator|.
name|setValidate
argument_list|(
name|validate
argument_list|)
operator|.
name|setSendEmail
argument_list|(
name|sendEmail
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
name|id
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
operator|&&
operator|!
name|base
operator|.
name|notes
argument_list|()
operator|.
name|getChange
argument_list|()
operator|.
name|isMerged
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|base
operator|.
name|notes
argument_list|()
operator|.
name|getChange
argument_list|()
operator|.
name|isMerged
argument_list|()
condition|)
block|{
comment|// Add to end of relation chain for open base change.
name|patchSetInserter
operator|.
name|setGroups
argument_list|(
name|base
operator|.
name|patchSet
argument_list|()
operator|.
name|groups
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// If the base is merged, start a new relation chain.
name|patchSetInserter
operator|.
name|setGroups
argument_list|(
name|GroupCollector
operator|.
name|getDefaultGroups
argument_list|(
name|rebasedCommit
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
throws|throws
name|IOException
block|{
name|ProjectState
name|project
init|=
name|projectCache
operator|.
name|checkedGet
argument_list|(
name|notes
operator|.
name|getProjectName
argument_list|()
argument_list|)
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
name|getInserter
argument_list|()
argument_list|,
name|ctx
operator|.
name|getRepoView
argument_list|()
operator|.
name|getConfig
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
name|boolean
name|success
init|=
name|merger
operator|.
name|merge
argument_list|(
name|original
argument_list|,
name|base
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|success
operator|||
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
if|if
condition|(
name|matchAuthorToCommitterDate
condition|)
block|{
name|cb
operator|.
name|setAuthor
argument_list|(
operator|new
name|PersonIdent
argument_list|(
name|cb
operator|.
name|getAuthor
argument_list|()
argument_list|,
name|cb
operator|.
name|getCommitter
argument_list|()
operator|.
name|getWhen
argument_list|()
argument_list|,
name|cb
operator|.
name|getCommitter
argument_list|()
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

