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
name|events
operator|.
name|CommitReceivedEvent
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
name|MergeException
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
name|CommitValidationException
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
name|gerrit
operator|.
name|server
operator|.
name|project
operator|.
name|RefControl
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
name|ssh
operator|.
name|NoSshInfo
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
name|util
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|errors
operator|.
name|IncorrectObjectTypeException
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
name|errors
operator|.
name|MissingObjectException
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
name|errors
operator|.
name|RepositoryNotFoundException
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
name|RefUpdate
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
name|revwalk
operator|.
name|FooterKey
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
name|eclipse
operator|.
name|jgit
operator|.
name|util
operator|.
name|ChangeIdUtil
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
name|List
import|;
end_import

begin_class
DECL|class|CherryPickChange
specifier|public
class|class
name|CherryPickChange
block|{
DECL|field|CHANGE_ID
specifier|private
specifier|static
specifier|final
name|FooterKey
name|CHANGE_ID
init|=
operator|new
name|FooterKey
argument_list|(
literal|"Change-Id"
argument_list|)
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|ReviewDb
name|db
decl_stmt|;
DECL|field|gitManager
specifier|private
specifier|final
name|GitRepositoryManager
name|gitManager
decl_stmt|;
DECL|field|myIdent
specifier|private
specifier|final
name|PersonIdent
name|myIdent
decl_stmt|;
DECL|field|currentUser
specifier|private
specifier|final
name|IdentifiedUser
name|currentUser
decl_stmt|;
DECL|field|commitValidatorsFactory
specifier|private
specifier|final
name|CommitValidators
operator|.
name|Factory
name|commitValidatorsFactory
decl_stmt|;
DECL|field|changeInserterFactory
specifier|private
specifier|final
name|ChangeInserter
operator|.
name|Factory
name|changeInserterFactory
decl_stmt|;
DECL|field|patchSetInserterFactory
specifier|private
specifier|final
name|PatchSetInserter
operator|.
name|Factory
name|patchSetInserterFactory
decl_stmt|;
DECL|field|mergeUtilFactory
specifier|final
name|MergeUtil
operator|.
name|Factory
name|mergeUtilFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|CherryPickChange (final ReviewDb db, @GerritPersonIdent final PersonIdent myIdent, final GitRepositoryManager gitManager, final IdentifiedUser currentUser, final CommitValidators.Factory commitValidatorsFactory, final ChangeInserter.Factory changeInserterFactory, final PatchSetInserter.Factory patchSetInserterFactory, final MergeUtil.Factory mergeUtilFactory)
name|CherryPickChange
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|,
annotation|@
name|GerritPersonIdent
specifier|final
name|PersonIdent
name|myIdent
parameter_list|,
specifier|final
name|GitRepositoryManager
name|gitManager
parameter_list|,
specifier|final
name|IdentifiedUser
name|currentUser
parameter_list|,
specifier|final
name|CommitValidators
operator|.
name|Factory
name|commitValidatorsFactory
parameter_list|,
specifier|final
name|ChangeInserter
operator|.
name|Factory
name|changeInserterFactory
parameter_list|,
specifier|final
name|PatchSetInserter
operator|.
name|Factory
name|patchSetInserterFactory
parameter_list|,
specifier|final
name|MergeUtil
operator|.
name|Factory
name|mergeUtilFactory
parameter_list|)
block|{
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
name|myIdent
operator|=
name|myIdent
expr_stmt|;
name|this
operator|.
name|currentUser
operator|=
name|currentUser
expr_stmt|;
name|this
operator|.
name|commitValidatorsFactory
operator|=
name|commitValidatorsFactory
expr_stmt|;
name|this
operator|.
name|changeInserterFactory
operator|=
name|changeInserterFactory
expr_stmt|;
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
block|}
DECL|method|cherryPick (final PatchSet.Id patchSetId, final String message, final String destinationBranch, final RefControl refControl)
specifier|public
name|Change
operator|.
name|Id
name|cherryPick
parameter_list|(
specifier|final
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|,
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|String
name|destinationBranch
parameter_list|,
specifier|final
name|RefControl
name|refControl
parameter_list|)
throws|throws
name|NoSuchChangeException
throws|,
name|EmailException
throws|,
name|OrmException
throws|,
name|MissingObjectException
throws|,
name|IncorrectObjectTypeException
throws|,
name|IOException
throws|,
name|InvalidChangeOperationException
throws|,
name|MergeException
block|{
specifier|final
name|Change
operator|.
name|Id
name|changeId
init|=
name|patchSetId
operator|.
name|getParentKey
argument_list|()
decl_stmt|;
specifier|final
name|PatchSet
name|patch
init|=
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|patchSetId
argument_list|)
decl_stmt|;
if|if
condition|(
name|patch
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|changeId
argument_list|)
throw|;
block|}
if|if
condition|(
name|destinationBranch
operator|==
literal|null
operator|||
name|destinationBranch
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|InvalidChangeOperationException
argument_list|(
literal|"Cherry Pick: Destination branch cannot be null or empty"
argument_list|)
throw|;
block|}
name|Project
operator|.
name|NameKey
name|project
init|=
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|changeId
argument_list|)
operator|.
name|getProject
argument_list|()
decl_stmt|;
specifier|final
name|Repository
name|git
decl_stmt|;
try|try
block|{
name|git
operator|=
name|gitManager
operator|.
name|openRepository
argument_list|(
name|project
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NoSuchChangeException
argument_list|(
name|changeId
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|RevWalk
name|revWalk
init|=
operator|new
name|RevWalk
argument_list|(
name|git
argument_list|)
decl_stmt|;
try|try
block|{
name|Ref
name|destRef
init|=
name|git
operator|.
name|getRef
argument_list|(
name|destinationBranch
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
literal|"Branch "
operator|+
name|destinationBranch
operator|+
literal|" does not exist."
argument_list|)
throw|;
block|}
specifier|final
name|RevCommit
name|mergeTip
init|=
name|revWalk
operator|.
name|parseCommit
argument_list|(
name|destRef
operator|.
name|getObjectId
argument_list|()
argument_list|)
decl_stmt|;
name|RevCommit
name|commitToCherryPick
init|=
name|revWalk
operator|.
name|parseCommit
argument_list|(
name|ObjectId
operator|.
name|fromString
argument_list|(
name|patch
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|PersonIdent
name|committerIdent
init|=
name|currentUser
operator|.
name|newCommitterIdent
argument_list|(
name|myIdent
operator|.
name|getWhen
argument_list|()
argument_list|,
name|myIdent
operator|.
name|getTimeZone
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|ObjectId
name|computedChangeId
init|=
name|ChangeIdUtil
operator|.
name|computeChangeId
argument_list|(
name|commitToCherryPick
operator|.
name|getTree
argument_list|()
argument_list|,
name|mergeTip
argument_list|,
name|commitToCherryPick
operator|.
name|getAuthorIdent
argument_list|()
argument_list|,
name|myIdent
argument_list|,
name|message
argument_list|)
decl_stmt|;
name|String
name|commitMessage
init|=
name|ChangeIdUtil
operator|.
name|insertId
argument_list|(
name|message
argument_list|,
name|computedChangeId
argument_list|)
operator|.
name|trim
argument_list|()
operator|+
literal|'\n'
decl_stmt|;
name|RevCommit
name|cherryPickCommit
decl_stmt|;
name|ObjectInserter
name|oi
init|=
name|git
operator|.
name|newObjectInserter
argument_list|()
decl_stmt|;
try|try
block|{
name|ProjectState
name|projectState
init|=
name|refControl
operator|.
name|getProjectControl
argument_list|()
operator|.
name|getProjectState
argument_list|()
decl_stmt|;
name|cherryPickCommit
operator|=
name|mergeUtilFactory
operator|.
name|create
argument_list|(
name|projectState
argument_list|)
operator|.
name|createCherryPickFromCommit
argument_list|(
name|git
argument_list|,
name|oi
argument_list|,
name|mergeTip
argument_list|,
name|commitToCherryPick
argument_list|,
name|committerIdent
argument_list|,
name|commitMessage
argument_list|,
name|revWalk
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|oi
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cherryPickCommit
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|MergeException
argument_list|(
literal|"Cherry pick failed"
argument_list|)
throw|;
block|}
name|Change
operator|.
name|Key
name|changeKey
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|idList
init|=
name|cherryPickCommit
operator|.
name|getFooterLines
argument_list|(
name|CHANGE_ID
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|idList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|String
name|idStr
init|=
name|idList
operator|.
name|get
argument_list|(
name|idList
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|changeKey
operator|=
operator|new
name|Change
operator|.
name|Key
argument_list|(
name|idStr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|changeKey
operator|=
operator|new
name|Change
operator|.
name|Key
argument_list|(
literal|"I"
operator|+
name|computedChangeId
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Change
argument_list|>
name|destChanges
init|=
name|db
operator|.
name|changes
argument_list|()
operator|.
name|byBranchKey
argument_list|(
operator|new
name|Branch
operator|.
name|NameKey
argument_list|(
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|changeId
argument_list|)
operator|.
name|getProject
argument_list|()
argument_list|,
name|destRef
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|changeKey
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
if|if
condition|(
name|destChanges
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|InvalidChangeOperationException
argument_list|(
literal|"Several changes with key "
operator|+
name|changeKey
operator|+
literal|" reside on the same branch. "
operator|+
literal|"Cannot create a new patch set."
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|destChanges
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// The change key exists on the destination branch. The cherry pick
comment|// will be added as a new patch set.
return|return
name|insertPatchSet
argument_list|(
name|git
argument_list|,
name|revWalk
argument_list|,
name|destChanges
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|patchSetId
argument_list|,
name|cherryPickCommit
argument_list|,
name|refControl
argument_list|)
return|;
block|}
else|else
block|{
comment|// Change key not found on destination branch. We can create a new
comment|// change.
return|return
name|createNewChange
argument_list|(
name|git
argument_list|,
name|revWalk
argument_list|,
name|changeKey
argument_list|,
name|project
argument_list|,
name|patchSetId
argument_list|,
name|destRef
argument_list|,
name|cherryPickCommit
argument_list|,
name|refControl
argument_list|)
return|;
block|}
block|}
finally|finally
block|{
name|revWalk
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|git
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|insertPatchSet (Repository git, RevWalk revWalk, Change change, PatchSet.Id patchSetId, RevCommit cherryPickCommit, RefControl refControl)
specifier|private
name|Change
operator|.
name|Id
name|insertPatchSet
parameter_list|(
name|Repository
name|git
parameter_list|,
name|RevWalk
name|revWalk
parameter_list|,
name|Change
name|change
parameter_list|,
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|,
name|RevCommit
name|cherryPickCommit
parameter_list|,
name|RefControl
name|refControl
parameter_list|)
throws|throws
name|InvalidChangeOperationException
throws|,
name|IOException
throws|,
name|OrmException
throws|,
name|NoSuchChangeException
block|{
specifier|final
name|ChangeControl
name|changeControl
init|=
name|refControl
operator|.
name|getProjectControl
argument_list|()
operator|.
name|controlFor
argument_list|(
name|change
argument_list|)
decl_stmt|;
specifier|final
name|PatchSetInserter
name|inserter
init|=
name|patchSetInserterFactory
operator|.
name|create
argument_list|(
name|git
argument_list|,
name|revWalk
argument_list|,
name|changeControl
argument_list|,
name|cherryPickCommit
argument_list|)
decl_stmt|;
specifier|final
name|PatchSet
operator|.
name|Id
name|newPatchSetId
init|=
name|inserter
operator|.
name|getPatchSetId
argument_list|()
decl_stmt|;
specifier|final
name|PatchSet
name|current
init|=
name|db
operator|.
name|patchSets
argument_list|()
operator|.
name|get
argument_list|(
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
decl_stmt|;
name|inserter
operator|.
name|setMessage
argument_list|(
literal|"Uploaded patch set "
operator|+
name|newPatchSetId
operator|.
name|get
argument_list|()
operator|+
literal|"."
argument_list|)
operator|.
name|setDraft
argument_list|(
name|current
operator|.
name|isDraft
argument_list|()
argument_list|)
operator|.
name|insert
argument_list|()
expr_stmt|;
return|return
name|change
operator|.
name|getId
argument_list|()
return|;
block|}
DECL|method|createNewChange (Repository git, RevWalk revWalk, Change.Key changeKey, Project.NameKey project, PatchSet.Id patchSetId, Ref destRef, RevCommit cherryPickCommit, RefControl refControl)
specifier|private
name|Change
operator|.
name|Id
name|createNewChange
parameter_list|(
name|Repository
name|git
parameter_list|,
name|RevWalk
name|revWalk
parameter_list|,
name|Change
operator|.
name|Key
name|changeKey
parameter_list|,
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|,
name|Ref
name|destRef
parameter_list|,
name|RevCommit
name|cherryPickCommit
parameter_list|,
name|RefControl
name|refControl
parameter_list|)
throws|throws
name|OrmException
throws|,
name|InvalidChangeOperationException
throws|,
name|IOException
block|{
name|Change
name|change
init|=
operator|new
name|Change
argument_list|(
name|changeKey
argument_list|,
operator|new
name|Change
operator|.
name|Id
argument_list|(
name|db
operator|.
name|nextChangeId
argument_list|()
argument_list|)
argument_list|,
name|currentUser
operator|.
name|getAccountId
argument_list|()
argument_list|,
operator|new
name|Branch
operator|.
name|NameKey
argument_list|(
name|project
argument_list|,
name|destRef
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
decl_stmt|;
name|ChangeInserter
name|ins
init|=
name|changeInserterFactory
operator|.
name|create
argument_list|(
name|refControl
argument_list|,
name|change
argument_list|,
name|cherryPickCommit
argument_list|)
decl_stmt|;
name|PatchSet
name|newPatchSet
init|=
name|ins
operator|.
name|getPatchSet
argument_list|()
decl_stmt|;
name|CommitValidators
name|commitValidators
init|=
name|commitValidatorsFactory
operator|.
name|create
argument_list|(
name|refControl
argument_list|,
operator|new
name|NoSshInfo
argument_list|()
argument_list|,
name|git
argument_list|)
decl_stmt|;
name|CommitReceivedEvent
name|commitReceivedEvent
init|=
operator|new
name|CommitReceivedEvent
argument_list|(
operator|new
name|ReceiveCommand
argument_list|(
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|,
name|cherryPickCommit
operator|.
name|getId
argument_list|()
argument_list|,
name|newPatchSet
operator|.
name|getRefName
argument_list|()
argument_list|)
argument_list|,
name|refControl
operator|.
name|getProjectControl
argument_list|()
operator|.
name|getProject
argument_list|()
argument_list|,
name|refControl
operator|.
name|getRefName
argument_list|()
argument_list|,
name|cherryPickCommit
argument_list|,
name|currentUser
argument_list|)
decl_stmt|;
try|try
block|{
name|commitValidators
operator|.
name|validateForGerritCommits
argument_list|(
name|commitReceivedEvent
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitValidationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InvalidChangeOperationException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|RefUpdate
name|ru
init|=
name|git
operator|.
name|updateRef
argument_list|(
name|newPatchSet
operator|.
name|getRefName
argument_list|()
argument_list|)
decl_stmt|;
name|ru
operator|.
name|setExpectedOldObjectId
argument_list|(
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|)
expr_stmt|;
name|ru
operator|.
name|setNewObjectId
argument_list|(
name|cherryPickCommit
argument_list|)
expr_stmt|;
name|ru
operator|.
name|disableRefLog
argument_list|()
expr_stmt|;
if|if
condition|(
name|ru
operator|.
name|update
argument_list|(
name|revWalk
argument_list|)
operator|!=
name|RefUpdate
operator|.
name|Result
operator|.
name|NEW
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Failed to create ref %s in %s: %s"
argument_list|,
name|newPatchSet
operator|.
name|getRefName
argument_list|()
argument_list|,
name|change
operator|.
name|getDest
argument_list|()
operator|.
name|getParentKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
name|ru
operator|.
name|getResult
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|ins
operator|.
name|setMessage
argument_list|(
name|buildChangeMessage
argument_list|(
name|patchSetId
argument_list|,
name|change
argument_list|,
name|cherryPickCommit
argument_list|)
argument_list|)
operator|.
name|insert
argument_list|()
expr_stmt|;
return|return
name|change
operator|.
name|getId
argument_list|()
return|;
block|}
DECL|method|buildChangeMessage (PatchSet.Id patchSetId, Change dest, RevCommit cherryPickCommit)
specifier|private
name|ChangeMessage
name|buildChangeMessage
parameter_list|(
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|,
name|Change
name|dest
parameter_list|,
name|RevCommit
name|cherryPickCommit
parameter_list|)
throws|throws
name|OrmException
block|{
name|ChangeMessage
name|cmsg
init|=
operator|new
name|ChangeMessage
argument_list|(
operator|new
name|ChangeMessage
operator|.
name|Key
argument_list|(
name|patchSetId
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|ChangeUtil
operator|.
name|messageUUID
argument_list|(
name|db
argument_list|)
argument_list|)
argument_list|,
name|currentUser
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|,
name|patchSetId
argument_list|)
decl_stmt|;
name|String
name|destBranchName
init|=
name|dest
operator|.
name|getDest
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|StringBuilder
name|msgBuf
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Patch Set "
argument_list|)
operator|.
name|append
argument_list|(
name|patchSetId
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|": Cherry Picked"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"This patchset was cherry picked to branch "
argument_list|)
operator|.
name|append
argument_list|(
name|destBranchName
operator|.
name|substring
argument_list|(
name|destBranchName
operator|.
name|indexOf
argument_list|(
literal|"refs/heads/"
argument_list|)
operator|+
literal|"refs/heads/"
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|" as commit "
argument_list|)
operator|.
name|append
argument_list|(
name|cherryPickCommit
operator|.
name|getId
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|cmsg
operator|.
name|setMessage
argument_list|(
name|msgBuf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|cmsg
return|;
block|}
block|}
end_class

end_unit

