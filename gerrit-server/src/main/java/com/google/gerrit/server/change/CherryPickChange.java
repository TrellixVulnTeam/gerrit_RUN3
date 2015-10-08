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
name|common
operator|.
name|base
operator|.
name|Strings
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
name|FooterConstants
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
name|ChangeMessagesUtil
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
name|CodeReviewCommit
operator|.
name|CodeReviewRevWalk
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
name|MergeIdenticalTreeException
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
name|query
operator|.
name|change
operator|.
name|ChangeData
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
name|query
operator|.
name|change
operator|.
name|InternalChangeQuery
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
DECL|class|CherryPickChange
specifier|public
class|class
name|CherryPickChange
block|{
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
DECL|field|queryProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
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
DECL|field|currentUser
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|currentUser
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
specifier|private
specifier|final
name|MergeUtil
operator|.
name|Factory
name|mergeUtilFactory
decl_stmt|;
DECL|field|changeMessagesUtil
specifier|private
specifier|final
name|ChangeMessagesUtil
name|changeMessagesUtil
decl_stmt|;
DECL|field|updateFactory
specifier|private
specifier|final
name|ChangeUpdate
operator|.
name|Factory
name|updateFactory
decl_stmt|;
DECL|field|batchUpdateFactory
specifier|private
specifier|final
name|BatchUpdate
operator|.
name|Factory
name|batchUpdateFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|CherryPickChange (Provider<ReviewDb> db, Provider<InternalChangeQuery> queryProvider, @GerritPersonIdent PersonIdent myIdent, GitRepositoryManager gitManager, Provider<CurrentUser> currentUser, ChangeInserter.Factory changeInserterFactory, PatchSetInserter.Factory patchSetInserterFactory, MergeUtil.Factory mergeUtilFactory, ChangeMessagesUtil changeMessagesUtil, ChangeUpdate.Factory updateFactory, BatchUpdate.Factory batchUpdateFactory)
name|CherryPickChange
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
parameter_list|,
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|myIdent
parameter_list|,
name|GitRepositoryManager
name|gitManager
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|currentUser
parameter_list|,
name|ChangeInserter
operator|.
name|Factory
name|changeInserterFactory
parameter_list|,
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
name|ChangeMessagesUtil
name|changeMessagesUtil
parameter_list|,
name|ChangeUpdate
operator|.
name|Factory
name|updateFactory
parameter_list|,
name|BatchUpdate
operator|.
name|Factory
name|batchUpdateFactory
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
name|queryProvider
operator|=
name|queryProvider
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
name|currentUser
operator|=
name|currentUser
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
name|this
operator|.
name|changeMessagesUtil
operator|=
name|changeMessagesUtil
expr_stmt|;
name|this
operator|.
name|updateFactory
operator|=
name|updateFactory
expr_stmt|;
name|this
operator|.
name|batchUpdateFactory
operator|=
name|batchUpdateFactory
expr_stmt|;
block|}
DECL|method|cherryPick (Change change, PatchSet patch, final String message, final String ref, final RefControl refControl)
specifier|public
name|Change
operator|.
name|Id
name|cherryPick
parameter_list|(
name|Change
name|change
parameter_list|,
name|PatchSet
name|patch
parameter_list|,
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|String
name|ref
parameter_list|,
specifier|final
name|RefControl
name|refControl
parameter_list|)
throws|throws
name|NoSuchChangeException
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
throws|,
name|UpdateException
throws|,
name|RestApiException
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|ref
argument_list|)
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
name|change
operator|.
name|getProject
argument_list|()
decl_stmt|;
name|String
name|destinationBranch
init|=
name|RefNames
operator|.
name|shortName
argument_list|(
name|ref
argument_list|)
decl_stmt|;
name|IdentifiedUser
name|identifiedUser
init|=
operator|(
name|IdentifiedUser
operator|)
name|currentUser
operator|.
name|get
argument_list|()
decl_stmt|;
try|try
init|(
name|Repository
name|git
init|=
name|gitManager
operator|.
name|openRepository
argument_list|(
name|project
argument_list|)
init|;
name|CodeReviewRevWalk
name|revWalk
operator|=
name|CodeReviewCommit
operator|.
name|newRevWalk
argument_list|(
name|git
argument_list|)
init|)
block|{
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
name|ref
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
name|String
operator|.
name|format
argument_list|(
literal|"Branch %s does not exist."
argument_list|,
name|destinationBranch
argument_list|)
argument_list|)
throw|;
block|}
name|CodeReviewCommit
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
name|CodeReviewCommit
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
name|identifiedUser
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
name|committerIdent
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
name|CodeReviewCommit
name|cherryPickCommit
decl_stmt|;
try|try
init|(
name|ObjectInserter
name|oi
init|=
name|git
operator|.
name|newObjectInserter
argument_list|()
init|)
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
catch|catch
parameter_list|(
name|MergeIdenticalTreeException
decl||
name|MergeConflictException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MergeException
argument_list|(
literal|"Cherry pick failed: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
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
name|FooterConstants
operator|.
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
name|Branch
operator|.
name|NameKey
name|newDest
init|=
operator|new
name|Branch
operator|.
name|NameKey
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
argument_list|,
name|destRef
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ChangeData
argument_list|>
name|destChanges
init|=
name|queryProvider
operator|.
name|get
argument_list|()
operator|.
name|setLimit
argument_list|(
literal|2
argument_list|)
operator|.
name|byBranchKey
argument_list|(
name|newDest
argument_list|,
name|changeKey
argument_list|)
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
operator|.
name|change
argument_list|()
argument_list|,
name|cherryPickCommit
argument_list|,
name|refControl
argument_list|,
name|identifiedUser
argument_list|)
return|;
block|}
else|else
block|{
comment|// Change key not found on destination branch. We can create a new
comment|// change.
name|String
name|newTopic
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|change
operator|.
name|getTopic
argument_list|()
argument_list|)
condition|)
block|{
name|newTopic
operator|=
name|change
operator|.
name|getTopic
argument_list|()
operator|+
literal|"-"
operator|+
name|newDest
operator|.
name|getShortName
argument_list|()
expr_stmt|;
block|}
name|Change
name|newChange
init|=
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
name|destRef
argument_list|,
name|cherryPickCommit
argument_list|,
name|refControl
argument_list|,
name|identifiedUser
argument_list|,
name|newTopic
argument_list|,
name|change
operator|.
name|getDest
argument_list|()
argument_list|)
decl_stmt|;
name|addMessageToSourceChange
argument_list|(
name|change
argument_list|,
name|patch
operator|.
name|getId
argument_list|()
argument_list|,
name|destinationBranch
argument_list|,
name|cherryPickCommit
argument_list|,
name|identifiedUser
argument_list|,
name|refControl
argument_list|)
expr_stmt|;
return|return
name|newChange
operator|.
name|getId
argument_list|()
return|;
block|}
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
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|insertPatchSet (Repository git, RevWalk revWalk, Change change, CodeReviewCommit cherryPickCommit, RefControl refControl, IdentifiedUser identifiedUser)
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
name|CodeReviewCommit
name|cherryPickCommit
parameter_list|,
name|RefControl
name|refControl
parameter_list|,
name|IdentifiedUser
name|identifiedUser
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
throws|,
name|UpdateException
throws|,
name|RestApiException
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
name|PatchSet
name|current
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
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|BatchUpdate
name|bu
init|=
name|batchUpdateFactory
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
name|getDest
argument_list|()
operator|.
name|getParentKey
argument_list|()
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
name|changeControl
argument_list|,
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
name|setUploader
argument_list|(
name|identifiedUser
operator|.
name|getAccountId
argument_list|()
argument_list|)
operator|.
name|setSendMail
argument_list|(
literal|false
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
name|change
operator|.
name|getId
argument_list|()
return|;
block|}
DECL|method|createNewChange (Repository git, RevWalk revWalk, Change.Key changeKey, Project.NameKey project, Ref destRef, CodeReviewCommit cherryPickCommit, RefControl refControl, IdentifiedUser identifiedUser, String topic, Branch.NameKey sourceBranch)
specifier|private
name|Change
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
name|Ref
name|destRef
parameter_list|,
name|CodeReviewCommit
name|cherryPickCommit
parameter_list|,
name|RefControl
name|refControl
parameter_list|,
name|IdentifiedUser
name|identifiedUser
parameter_list|,
name|String
name|topic
parameter_list|,
name|Branch
operator|.
name|NameKey
name|sourceBranch
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
name|get
argument_list|()
operator|.
name|nextChangeId
argument_list|()
argument_list|)
argument_list|,
name|identifiedUser
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
name|change
operator|.
name|setTopic
argument_list|(
name|topic
argument_list|)
expr_stmt|;
name|ChangeInserter
name|ins
init|=
name|changeInserterFactory
operator|.
name|create
argument_list|(
name|git
argument_list|,
name|revWalk
argument_list|,
name|refControl
operator|.
name|getProjectControl
argument_list|()
argument_list|,
name|change
argument_list|,
name|cherryPickCommit
argument_list|)
operator|.
name|setValidatePolicy
argument_list|(
name|CommitValidators
operator|.
name|Policy
operator|.
name|GERRIT
argument_list|)
decl_stmt|;
return|return
name|ins
operator|.
name|setMessage
argument_list|(
name|messageForDestinationChange
argument_list|(
name|ins
operator|.
name|getPatchSet
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|sourceBranch
argument_list|)
argument_list|)
operator|.
name|insert
argument_list|()
return|;
block|}
DECL|method|addMessageToSourceChange (Change change, PatchSet.Id patchSetId, String destinationBranch, CodeReviewCommit cherryPickCommit, IdentifiedUser identifiedUser, RefControl refControl)
specifier|private
name|void
name|addMessageToSourceChange
parameter_list|(
name|Change
name|change
parameter_list|,
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|,
name|String
name|destinationBranch
parameter_list|,
name|CodeReviewCommit
name|cherryPickCommit
parameter_list|,
name|IdentifiedUser
name|identifiedUser
parameter_list|,
name|RefControl
name|refControl
parameter_list|)
throws|throws
name|OrmException
block|{
name|ChangeMessage
name|changeMessage
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
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|identifiedUser
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
name|StringBuilder
name|sb
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
name|destinationBranch
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
name|changeMessage
operator|.
name|setMessage
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ChangeControl
name|ctl
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
name|ChangeUpdate
name|update
init|=
name|updateFactory
operator|.
name|create
argument_list|(
name|ctl
argument_list|,
name|change
operator|.
name|getCreatedOn
argument_list|()
argument_list|)
decl_stmt|;
name|changeMessagesUtil
operator|.
name|addChangeMessage
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|update
argument_list|,
name|changeMessage
argument_list|)
expr_stmt|;
block|}
DECL|method|messageForDestinationChange (PatchSet.Id patchSetId, Branch.NameKey sourceBranch)
specifier|private
name|String
name|messageForDestinationChange
parameter_list|(
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|,
name|Branch
operator|.
name|NameKey
name|sourceBranch
parameter_list|)
block|{
return|return
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
literal|": Cherry Picked from branch "
argument_list|)
operator|.
name|append
argument_list|(
name|sourceBranch
operator|.
name|getShortName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

