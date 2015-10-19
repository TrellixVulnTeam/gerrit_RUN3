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
name|common
operator|.
name|errors
operator|.
name|EmailException
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
name|Change
operator|.
name|Status
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
name|GerritPersonIdent
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
name|GitRepositoryManager
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
name|Provider
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
name|Singleton
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
name|ObjectInserter
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
name|lib
operator|.
name|Ref
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
name|Repository
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
name|TimeZone
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|RebaseChange
specifier|public
class|class
name|RebaseChange
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
name|RebaseChange
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|changeControlFactory
specifier|private
specifier|final
name|ChangeControl
operator|.
name|GenericFactory
name|changeControlFactory
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
DECL|field|gitManager
specifier|private
specifier|final
name|GitRepositoryManager
name|gitManager
decl_stmt|;
DECL|field|serverTimeZone
specifier|private
specifier|final
name|TimeZone
name|serverTimeZone
decl_stmt|;
DECL|field|mergeUtilFactory
specifier|private
specifier|final
name|MergeUtil
operator|.
name|Factory
name|mergeUtilFactory
decl_stmt|;
DECL|field|patchSetInserterFactory
specifier|private
specifier|final
name|PatchSetInserter
operator|.
name|Factory
name|patchSetInserterFactory
decl_stmt|;
DECL|field|updateFactory
specifier|private
specifier|final
name|BatchUpdate
operator|.
name|Factory
name|updateFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|RebaseChange (ChangeControl.GenericFactory changeControlFactory, Provider<ReviewDb> db, @GerritPersonIdent PersonIdent myIdent, GitRepositoryManager gitManager, MergeUtil.Factory mergeUtilFactory, PatchSetInserter.Factory patchSetInserterFactory, BatchUpdate.Factory updateFactory)
name|RebaseChange
parameter_list|(
name|ChangeControl
operator|.
name|GenericFactory
name|changeControlFactory
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|myIdent
parameter_list|,
name|GitRepositoryManager
name|gitManager
parameter_list|,
name|MergeUtil
operator|.
name|Factory
name|mergeUtilFactory
parameter_list|,
name|PatchSetInserter
operator|.
name|Factory
name|patchSetInserterFactory
parameter_list|,
name|BatchUpdate
operator|.
name|Factory
name|updateFactory
parameter_list|)
block|{
name|this
operator|.
name|changeControlFactory
operator|=
name|changeControlFactory
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|gitManager
operator|=
name|gitManager
expr_stmt|;
name|this
operator|.
name|serverTimeZone
operator|=
name|myIdent
operator|.
name|getTimeZone
argument_list|()
expr_stmt|;
name|this
operator|.
name|mergeUtilFactory
operator|=
name|mergeUtilFactory
expr_stmt|;
name|this
operator|.
name|patchSetInserterFactory
operator|=
name|patchSetInserterFactory
expr_stmt|;
name|this
operator|.
name|updateFactory
operator|=
name|updateFactory
expr_stmt|;
block|}
comment|/**    * Rebase the change of the given patch set.    *<p>    * If the patch set has no dependency to an open change, then the change is    * rebased on the tip of the destination branch.    *<p>    * If the patch set depends on an open change, it is rebased on the latest    * patch set of this change.    *<p>    * The rebased commit is added as new patch set to the change.    *<p>    * E-mail notification and triggering of hooks happens for the creation of the    * new patch set.    *    * @param git the repository.    * @param rw the RevWalk.    * @param rsrc revision to rebase.    * @param newBaseRev the commit that should be the new base.    * @throws NoSuchChangeException if the change to which the patch set belongs    *     does not exist or is not visible to the user.    * @throws EmailException if sending the e-mail to notify about the new patch    *     set fails.    * @throws OrmException if accessing the database fails.    * @throws IOException if accessing the repository fails.    * @throws InvalidChangeOperationException if rebase is not possible or not    *     allowed.    * @throws RestApiException if updating the change fails due to an underlying    *     API call failing.    * @throws UpdateException if updating the change fails.    */
DECL|method|rebase (Repository git, RevWalk rw, RevisionResource rsrc, String newBaseRev)
specifier|public
name|void
name|rebase
parameter_list|(
name|Repository
name|git
parameter_list|,
name|RevWalk
name|rw
parameter_list|,
name|RevisionResource
name|rsrc
parameter_list|,
name|String
name|newBaseRev
parameter_list|)
throws|throws
name|NoSuchChangeException
throws|,
name|EmailException
throws|,
name|OrmException
throws|,
name|IOException
throws|,
name|InvalidChangeOperationException
throws|,
name|UpdateException
throws|,
name|RestApiException
block|{
name|Change
name|change
init|=
name|rsrc
operator|.
name|getChange
argument_list|()
decl_stmt|;
name|PatchSet
name|patchSet
init|=
name|rsrc
operator|.
name|getPatchSet
argument_list|()
decl_stmt|;
name|IdentifiedUser
name|uploader
init|=
operator|(
name|IdentifiedUser
operator|)
name|rsrc
operator|.
name|getControl
argument_list|()
operator|.
name|getUser
argument_list|()
decl_stmt|;
try|try
init|(
name|ObjectInserter
name|inserter
init|=
name|git
operator|.
name|newObjectInserter
argument_list|()
init|)
block|{
name|String
name|baseRev
init|=
name|newBaseRev
decl_stmt|;
if|if
condition|(
name|baseRev
operator|==
literal|null
condition|)
block|{
name|baseRev
operator|=
name|findBaseRevision
argument_list|(
name|patchSet
argument_list|,
name|change
operator|.
name|getDest
argument_list|()
argument_list|,
name|git
argument_list|,
name|rw
argument_list|)
expr_stmt|;
block|}
name|ObjectId
name|baseObjectId
init|=
name|git
operator|.
name|resolve
argument_list|(
name|baseRev
argument_list|)
decl_stmt|;
if|if
condition|(
name|baseObjectId
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidChangeOperationException
argument_list|(
literal|"Cannot rebase: Failed to resolve baseRev: "
operator|+
name|baseRev
argument_list|)
throw|;
block|}
name|RevCommit
name|baseCommit
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|baseObjectId
argument_list|)
decl_stmt|;
name|PersonIdent
name|committerIdent
init|=
name|uploader
operator|.
name|newCommitterIdent
argument_list|(
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|,
name|serverTimeZone
argument_list|)
decl_stmt|;
name|rebase
argument_list|(
name|git
argument_list|,
name|rw
argument_list|,
name|inserter
argument_list|,
name|change
argument_list|,
name|patchSet
operator|.
name|getId
argument_list|()
argument_list|,
name|uploader
argument_list|,
name|baseCommit
argument_list|,
name|mergeUtilFactory
operator|.
name|create
argument_list|(
name|rsrc
operator|.
name|getControl
argument_list|()
operator|.
name|getProjectControl
argument_list|()
operator|.
name|getProjectState
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|,
name|committerIdent
argument_list|,
literal|true
argument_list|,
name|CommitValidators
operator|.
name|Policy
operator|.
name|GERRIT
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MergeConflictException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * Find the commit onto which a patch set should be rebased.    *<p>    * This is defined as the latest patch set of the change corresponding to    * this commit's parent, or the destination branch tip in the case where the    * parent's change is merged.    *    * @param patchSet patch set for which the new base commit should be found.    * @param destBranch the destination branch.    * @param git the repository.    * @param rw the RevWalk.    * @return the commit onto which the patch set should be rebased.    * @throws InvalidChangeOperationException if rebase is not possible or not    *     allowed.    * @throws IOException if accessing the repository fails.    * @throws OrmException if accessing the database fails.    */
DECL|method|findBaseRevision (PatchSet patchSet, Branch.NameKey destBranch, Repository git, RevWalk rw)
specifier|private
name|String
name|findBaseRevision
parameter_list|(
name|PatchSet
name|patchSet
parameter_list|,
name|Branch
operator|.
name|NameKey
name|destBranch
parameter_list|,
name|Repository
name|git
parameter_list|,
name|RevWalk
name|rw
parameter_list|)
throws|throws
name|InvalidChangeOperationException
throws|,
name|IOException
throws|,
name|OrmException
block|{
name|String
name|baseRev
init|=
literal|null
decl_stmt|;
name|RevCommit
name|commit
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|ObjectId
operator|.
name|fromString
argument_list|(
name|patchSet
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|commit
operator|.
name|getParentCount
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|InvalidChangeOperationException
argument_list|(
literal|"Cannot rebase a change with multiple parents."
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|commit
operator|.
name|getParentCount
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|InvalidChangeOperationException
argument_list|(
literal|"Cannot rebase a change without any parents"
operator|+
literal|" (is this the initial commit?)."
argument_list|)
throw|;
block|}
name|RevId
name|parentRev
init|=
operator|new
name|RevId
argument_list|(
name|commit
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|PatchSet
name|depPatchSet
range|:
name|db
operator|.
name|get
argument_list|()
operator|.
name|patchSets
argument_list|()
operator|.
name|byRevision
argument_list|(
name|parentRev
argument_list|)
control|)
block|{
name|Change
operator|.
name|Id
name|depChangeId
init|=
name|depPatchSet
operator|.
name|getId
argument_list|()
operator|.
name|getParentKey
argument_list|()
decl_stmt|;
name|Change
name|depChange
init|=
name|db
operator|.
name|get
argument_list|()
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|depChangeId
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|depChange
operator|.
name|getDest
argument_list|()
operator|.
name|equals
argument_list|(
name|destBranch
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|depChange
operator|.
name|getStatus
argument_list|()
operator|==
name|Status
operator|.
name|ABANDONED
condition|)
block|{
throw|throw
operator|new
name|InvalidChangeOperationException
argument_list|(
literal|"Cannot rebase a change with an abandoned parent: "
operator|+
name|depChange
operator|.
name|getKey
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|depChange
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
condition|)
block|{
if|if
condition|(
name|depPatchSet
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|depChange
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidChangeOperationException
argument_list|(
literal|"Change is already based on the latest patch set of the"
operator|+
literal|" dependent change."
argument_list|)
throw|;
block|}
name|PatchSet
name|latestDepPatchSet
init|=
name|db
operator|.
name|get
argument_list|()
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|depChange
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
decl_stmt|;
name|baseRev
operator|=
name|latestDepPatchSet
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
break|break;
block|}
if|if
condition|(
name|baseRev
operator|==
literal|null
condition|)
block|{
comment|// We are dependent on a merged PatchSet or have no PatchSet
comment|// dependencies at all.
name|Ref
name|destRef
init|=
name|git
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|exactRef
argument_list|(
name|destBranch
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|destRef
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidChangeOperationException
argument_list|(
literal|"The destination branch does not exist: "
operator|+
name|destBranch
operator|.
name|get
argument_list|()
argument_list|)
throw|;
block|}
name|baseRev
operator|=
name|destRef
operator|.
name|getObjectId
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
if|if
condition|(
name|baseRev
operator|.
name|equals
argument_list|(
name|parentRev
operator|.
name|get
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidChangeOperationException
argument_list|(
literal|"Change is already up to date."
argument_list|)
throw|;
block|}
block|}
return|return
name|baseRev
return|;
block|}
comment|/**    * Rebase the change of the given patch set on the given base commit.    *<p>    * The rebased commit is added as new patch set to the change.    *<p>    * E-mail notification and triggering of hooks is only done for the creation    * of the new patch set if {@code sendEmail} and {@code runHooks} are true,    * respectively.    *    * @param git the repository.    * @param inserter the object inserter.    * @param change the change to rebase.    * @param patchSetId the patch set ID to rebase.    * @param uploader the user that creates the rebased patch set.    * @param baseCommit the commit that should be the new base.    * @param mergeUtil merge utilities for the destination project.    * @param committerIdent the committer's identity.    * @param runHooks if hooks should be run for the new patch set.    * @param validate if commit validation should be run for the new patch set.    * @param rw the RevWalk.    * @return the new patch set, which is based on the given base commit.    * @throws NoSuchChangeException if the change to which the patch set belongs    *     does not exist or is not visible to the user.    * @throws OrmException if accessing the database fails.    * @throws IOException if rebase is not possible.    * @throws InvalidChangeOperationException if rebase is not possible or not    *     allowed.    * @throws RestApiException if updating the change fails due to an underlying    *     API call failing.    * @throws UpdateException if updating the change fails.    */
DECL|method|rebase (Repository git, RevWalk rw, ObjectInserter inserter, Change change, PatchSet.Id patchSetId, IdentifiedUser uploader, RevCommit baseCommit, MergeUtil mergeUtil, PersonIdent committerIdent, boolean runHooks, CommitValidators.Policy validate)
specifier|public
name|PatchSet
name|rebase
parameter_list|(
name|Repository
name|git
parameter_list|,
name|RevWalk
name|rw
parameter_list|,
name|ObjectInserter
name|inserter
parameter_list|,
name|Change
name|change
parameter_list|,
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|,
name|IdentifiedUser
name|uploader
parameter_list|,
name|RevCommit
name|baseCommit
parameter_list|,
name|MergeUtil
name|mergeUtil
parameter_list|,
name|PersonIdent
name|committerIdent
parameter_list|,
name|boolean
name|runHooks
parameter_list|,
name|CommitValidators
operator|.
name|Policy
name|validate
parameter_list|)
throws|throws
name|NoSuchChangeException
throws|,
name|OrmException
throws|,
name|IOException
throws|,
name|InvalidChangeOperationException
throws|,
name|MergeConflictException
throws|,
name|UpdateException
throws|,
name|RestApiException
block|{
if|if
condition|(
operator|!
name|change
operator|.
name|currentPatchSetId
argument_list|()
operator|.
name|equals
argument_list|(
name|patchSetId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidChangeOperationException
argument_list|(
literal|"patch set is not current"
argument_list|)
throw|;
block|}
name|PatchSet
name|originalPatchSet
init|=
name|db
operator|.
name|get
argument_list|()
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|patchSetId
argument_list|)
decl_stmt|;
name|RevCommit
name|rebasedCommit
decl_stmt|;
name|ObjectId
name|oldId
init|=
name|ObjectId
operator|.
name|fromString
argument_list|(
name|originalPatchSet
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|RevCommit
name|original
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|oldId
argument_list|)
decl_stmt|;
name|rw
operator|.
name|parseBody
argument_list|(
name|original
argument_list|)
expr_stmt|;
name|ObjectId
name|newId
init|=
name|rebaseCommit
argument_list|(
name|git
argument_list|,
name|inserter
argument_list|,
name|original
argument_list|,
name|baseCommit
argument_list|,
name|mergeUtil
argument_list|,
name|committerIdent
argument_list|)
decl_stmt|;
name|rebasedCommit
operator|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|newId
argument_list|)
expr_stmt|;
name|ChangeControl
name|changeControl
init|=
name|changeControlFactory
operator|.
name|validateFor
argument_list|(
name|change
argument_list|,
name|uploader
argument_list|)
decl_stmt|;
name|PatchSetInserter
name|patchSetInserter
init|=
name|patchSetInserterFactory
operator|.
name|create
argument_list|(
name|git
argument_list|,
name|rw
argument_list|,
name|changeControl
argument_list|,
name|rebasedCommit
argument_list|)
operator|.
name|setValidatePolicy
argument_list|(
name|validate
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
name|setUploader
argument_list|(
name|uploader
operator|.
name|getAccountId
argument_list|()
argument_list|)
operator|.
name|setSendMail
argument_list|(
literal|false
argument_list|)
operator|.
name|setRunHooks
argument_list|(
name|runHooks
argument_list|)
decl_stmt|;
try|try
init|(
name|BatchUpdate
name|bu
init|=
name|updateFactory
operator|.
name|create
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|change
operator|.
name|getProject
argument_list|()
argument_list|,
name|uploader
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
init|)
block|{
name|bu
operator|.
name|addOp
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|patchSetInserter
operator|.
name|setMessage
argument_list|(
literal|"Patch Set "
operator|+
name|patchSetInserter
operator|.
name|getPatchSetId
argument_list|()
operator|.
name|get
argument_list|()
operator|+
literal|": Patch Set "
operator|+
name|patchSetId
operator|.
name|get
argument_list|()
operator|+
literal|" was rebased"
argument_list|)
argument_list|)
expr_stmt|;
name|bu
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
return|return
name|db
operator|.
name|get
argument_list|()
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|patchSetInserter
operator|.
name|getPatchSetId
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Rebase a commit.    *    * @param git repository to find commits in.    * @param inserter inserter to handle new trees and blobs.    * @param original the commit to rebase.    * @param base base to rebase against.    * @param mergeUtil merge utilities for the destination project.    * @param committerIdent committer identity.    * @return the id of the rebased commit.    * @throws MergeConflictException the rebase failed due to a merge conflict.    * @throws IOException the merge failed for another reason.    */
DECL|method|rebaseCommit (Repository git, ObjectInserter inserter, RevCommit original, RevCommit base, MergeUtil mergeUtil, PersonIdent committerIdent)
specifier|private
name|ObjectId
name|rebaseCommit
parameter_list|(
name|Repository
name|git
parameter_list|,
name|ObjectInserter
name|inserter
parameter_list|,
name|RevCommit
name|original
parameter_list|,
name|RevCommit
name|base
parameter_list|,
name|MergeUtil
name|mergeUtil
parameter_list|,
name|PersonIdent
name|committerIdent
parameter_list|)
throws|throws
name|MergeConflictException
throws|,
name|IOException
throws|,
name|InvalidChangeOperationException
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
name|InvalidChangeOperationException
argument_list|(
literal|"Change is already up to date."
argument_list|)
throw|;
block|}
name|ThreeWayMerger
name|merger
init|=
name|mergeUtil
operator|.
name|newThreeWayMerger
argument_list|(
name|git
argument_list|,
name|inserter
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
name|original
operator|.
name|getFullMessage
argument_list|()
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setCommitter
argument_list|(
name|committerIdent
argument_list|)
expr_stmt|;
name|ObjectId
name|objectId
init|=
name|inserter
operator|.
name|insert
argument_list|(
name|cb
argument_list|)
decl_stmt|;
name|inserter
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|objectId
return|;
block|}
DECL|method|canRebase (RevisionResource r)
specifier|public
name|boolean
name|canRebase
parameter_list|(
name|RevisionResource
name|r
parameter_list|)
block|{
return|return
name|canRebase
argument_list|(
name|r
operator|.
name|getPatchSet
argument_list|()
argument_list|,
name|r
operator|.
name|getChange
argument_list|()
operator|.
name|getDest
argument_list|()
argument_list|)
return|;
block|}
DECL|method|canRebase (PatchSet patchSet, Branch.NameKey dest)
specifier|private
name|boolean
name|canRebase
parameter_list|(
name|PatchSet
name|patchSet
parameter_list|,
name|Branch
operator|.
name|NameKey
name|dest
parameter_list|)
block|{
try|try
init|(
name|Repository
name|git
init|=
name|gitManager
operator|.
name|openRepository
argument_list|(
name|dest
operator|.
name|getParentKey
argument_list|()
argument_list|)
init|;
name|RevWalk
name|rw
operator|=
operator|new
name|RevWalk
argument_list|(
name|git
argument_list|)
init|)
block|{
name|findBaseRevision
argument_list|(
name|patchSet
argument_list|,
name|dest
argument_list|,
name|git
argument_list|,
name|rw
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|InvalidChangeOperationException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Error checking if patch set %s on %s can be rebased"
argument_list|,
name|patchSet
operator|.
name|getId
argument_list|()
argument_list|,
name|dest
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

