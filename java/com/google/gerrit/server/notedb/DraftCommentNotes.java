begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.notedb
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|notedb
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
name|checkArgument
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
name|gerrit
operator|.
name|reviewdb
operator|.
name|client
operator|.
name|RefNames
operator|.
name|refsDraftComments
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
name|NoteDbTable
operator|.
name|CHANGES
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
name|annotations
operator|.
name|VisibleForTesting
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
name|ImmutableListMultimap
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
name|ListMultimap
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
name|MultimapBuilder
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
name|flogger
operator|.
name|FluentLogger
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
name|metrics
operator|.
name|Timer1
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
name|Comment
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
name|PatchLineComment
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
name|git
operator|.
name|RepoRefCache
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
name|NoteDbChangeState
operator|.
name|PrimaryStorage
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
name|NoteDbUpdateManager
operator|.
name|StagedResult
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
name|rebuild
operator|.
name|ChangeRebuilder
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
name|concurrent
operator|.
name|TimeUnit
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
name|ConfigInvalidException
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
name|ObjectReader
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
name|notes
operator|.
name|NoteMap
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
name|transport
operator|.
name|ReceiveCommand
import|;
end_import

begin_comment
comment|/** View of the draft comments for a single {@link Change} based on the log of its drafts branch. */
end_comment

begin_class
DECL|class|DraftCommentNotes
specifier|public
class|class
name|DraftCommentNotes
extends|extends
name|AbstractChangeNotes
argument_list|<
name|DraftCommentNotes
argument_list|>
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|FluentLogger
name|logger
init|=
name|FluentLogger
operator|.
name|forEnclosingClass
argument_list|()
decl_stmt|;
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (Change change, Account.Id accountId)
name|DraftCommentNotes
name|create
parameter_list|(
name|Change
name|change
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
function_decl|;
DECL|method|createWithAutoRebuildingDisabled (Change.Id changeId, Account.Id accountId)
name|DraftCommentNotes
name|createWithAutoRebuildingDisabled
parameter_list|(
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
function_decl|;
block|}
DECL|field|change
specifier|private
specifier|final
name|Change
name|change
decl_stmt|;
DECL|field|author
specifier|private
specifier|final
name|Account
operator|.
name|Id
name|author
decl_stmt|;
DECL|field|rebuildResult
specifier|private
specifier|final
name|NoteDbUpdateManager
operator|.
name|Result
name|rebuildResult
decl_stmt|;
DECL|field|ref
specifier|private
specifier|final
name|Ref
name|ref
decl_stmt|;
DECL|field|comments
specifier|private
name|ImmutableListMultimap
argument_list|<
name|RevId
argument_list|,
name|Comment
argument_list|>
name|comments
decl_stmt|;
DECL|field|revisionNoteMap
specifier|private
name|RevisionNoteMap
argument_list|<
name|ChangeRevisionNote
argument_list|>
name|revisionNoteMap
decl_stmt|;
annotation|@
name|AssistedInject
DECL|method|DraftCommentNotes (Args args, @Assisted Change change, @Assisted Account.Id author)
name|DraftCommentNotes
parameter_list|(
name|Args
name|args
parameter_list|,
annotation|@
name|Assisted
name|Change
name|change
parameter_list|,
annotation|@
name|Assisted
name|Account
operator|.
name|Id
name|author
parameter_list|)
block|{
name|this
argument_list|(
name|args
argument_list|,
name|change
argument_list|,
name|author
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AssistedInject
DECL|method|DraftCommentNotes (Args args, @Assisted Change.Id changeId, @Assisted Account.Id author)
name|DraftCommentNotes
parameter_list|(
name|Args
name|args
parameter_list|,
annotation|@
name|Assisted
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
annotation|@
name|Assisted
name|Account
operator|.
name|Id
name|author
parameter_list|)
block|{
comment|// PrimaryStorage is unknown; this should only called by
comment|// PatchLineCommentsUtil#draftByAuthor, which can live with this.
name|super
argument_list|(
name|args
argument_list|,
name|changeId
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|change
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|author
operator|=
name|author
expr_stmt|;
name|this
operator|.
name|rebuildResult
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|ref
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|DraftCommentNotes ( Args args, Change change, Account.Id author, boolean autoRebuild, @Nullable NoteDbUpdateManager.Result rebuildResult, @Nullable Ref ref)
name|DraftCommentNotes
parameter_list|(
name|Args
name|args
parameter_list|,
name|Change
name|change
parameter_list|,
name|Account
operator|.
name|Id
name|author
parameter_list|,
name|boolean
name|autoRebuild
parameter_list|,
annotation|@
name|Nullable
name|NoteDbUpdateManager
operator|.
name|Result
name|rebuildResult
parameter_list|,
annotation|@
name|Nullable
name|Ref
name|ref
parameter_list|)
block|{
name|super
argument_list|(
name|args
argument_list|,
name|change
operator|.
name|getId
argument_list|()
argument_list|,
name|PrimaryStorage
operator|.
name|of
argument_list|(
name|change
argument_list|)
argument_list|,
name|autoRebuild
argument_list|)
expr_stmt|;
name|this
operator|.
name|change
operator|=
name|change
expr_stmt|;
name|this
operator|.
name|author
operator|=
name|author
expr_stmt|;
name|this
operator|.
name|rebuildResult
operator|=
name|rebuildResult
expr_stmt|;
name|this
operator|.
name|ref
operator|=
name|ref
expr_stmt|;
if|if
condition|(
name|ref
operator|!=
literal|null
condition|)
block|{
name|checkArgument
argument_list|(
name|ref
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|getRefName
argument_list|()
argument_list|)
argument_list|,
literal|"draft ref not for change %s and account %s: %s"
argument_list|,
name|getChangeId
argument_list|()
argument_list|,
name|author
argument_list|,
name|ref
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getRevisionNoteMap ()
name|RevisionNoteMap
argument_list|<
name|ChangeRevisionNote
argument_list|>
name|getRevisionNoteMap
parameter_list|()
block|{
return|return
name|revisionNoteMap
return|;
block|}
DECL|method|getAuthor ()
specifier|public
name|Account
operator|.
name|Id
name|getAuthor
parameter_list|()
block|{
return|return
name|author
return|;
block|}
DECL|method|getComments ()
specifier|public
name|ImmutableListMultimap
argument_list|<
name|RevId
argument_list|,
name|Comment
argument_list|>
name|getComments
parameter_list|()
block|{
return|return
name|comments
return|;
block|}
DECL|method|containsComment (Comment c)
specifier|public
name|boolean
name|containsComment
parameter_list|(
name|Comment
name|c
parameter_list|)
block|{
for|for
control|(
name|Comment
name|existing
range|:
name|comments
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|c
operator|.
name|key
operator|.
name|equals
argument_list|(
name|existing
operator|.
name|key
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getRefName ()
specifier|protected
name|String
name|getRefName
parameter_list|()
block|{
return|return
name|refsDraftComments
argument_list|(
name|getChangeId
argument_list|()
argument_list|,
name|author
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readRef (Repository repo)
specifier|protected
name|ObjectId
name|readRef
parameter_list|(
name|Repository
name|repo
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|ref
operator|!=
literal|null
condition|)
block|{
return|return
name|ref
operator|.
name|getObjectId
argument_list|()
return|;
block|}
return|return
name|super
operator|.
name|readRef
argument_list|(
name|repo
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|onLoad (LoadHandle handle)
specifier|protected
name|void
name|onLoad
parameter_list|(
name|LoadHandle
name|handle
parameter_list|)
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|ObjectId
name|rev
init|=
name|handle
operator|.
name|id
argument_list|()
decl_stmt|;
if|if
condition|(
name|rev
operator|==
literal|null
condition|)
block|{
name|loadDefaults
argument_list|()
expr_stmt|;
return|return;
block|}
name|logger
operator|.
name|atFine
argument_list|()
operator|.
name|log
argument_list|(
literal|"Load draft comment notes for change %s of project %s"
argument_list|,
name|getChangeId
argument_list|()
argument_list|,
name|getProjectName
argument_list|()
argument_list|)
expr_stmt|;
name|RevCommit
name|tipCommit
init|=
name|handle
operator|.
name|walk
argument_list|()
operator|.
name|parseCommit
argument_list|(
name|rev
argument_list|)
decl_stmt|;
name|ObjectReader
name|reader
init|=
name|handle
operator|.
name|walk
argument_list|()
operator|.
name|getObjectReader
argument_list|()
decl_stmt|;
name|revisionNoteMap
operator|=
name|RevisionNoteMap
operator|.
name|parse
argument_list|(
name|args
operator|.
name|changeNoteJson
argument_list|,
name|args
operator|.
name|legacyChangeNoteRead
argument_list|,
name|getChangeId
argument_list|()
argument_list|,
name|reader
argument_list|,
name|NoteMap
operator|.
name|read
argument_list|(
name|reader
argument_list|,
name|tipCommit
argument_list|)
argument_list|,
name|PatchLineComment
operator|.
name|Status
operator|.
name|DRAFT
argument_list|)
expr_stmt|;
name|ListMultimap
argument_list|<
name|RevId
argument_list|,
name|Comment
argument_list|>
name|cs
init|=
name|MultimapBuilder
operator|.
name|hashKeys
argument_list|()
operator|.
name|arrayListValues
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
for|for
control|(
name|ChangeRevisionNote
name|rn
range|:
name|revisionNoteMap
operator|.
name|revisionNotes
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|Comment
name|c
range|:
name|rn
operator|.
name|getComments
argument_list|()
control|)
block|{
name|cs
operator|.
name|put
argument_list|(
operator|new
name|RevId
argument_list|(
name|c
operator|.
name|revId
argument_list|)
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
name|comments
operator|=
name|ImmutableListMultimap
operator|.
name|copyOf
argument_list|(
name|cs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|loadDefaults ()
specifier|protected
name|void
name|loadDefaults
parameter_list|()
block|{
name|comments
operator|=
name|ImmutableListMultimap
operator|.
name|of
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getProjectName ()
specifier|public
name|Project
operator|.
name|NameKey
name|getProjectName
parameter_list|()
block|{
return|return
name|args
operator|.
name|allUsers
return|;
block|}
annotation|@
name|Override
DECL|method|openHandle (Repository repo)
specifier|protected
name|LoadHandle
name|openHandle
parameter_list|(
name|Repository
name|repo
parameter_list|)
throws|throws
name|NoSuchChangeException
throws|,
name|IOException
block|{
if|if
condition|(
name|rebuildResult
operator|!=
literal|null
condition|)
block|{
name|StagedResult
name|sr
init|=
name|checkNotNull
argument_list|(
name|rebuildResult
operator|.
name|staged
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|LoadHandle
operator|.
name|create
argument_list|(
name|ChangeNotesCommit
operator|.
name|newStagedRevWalk
argument_list|(
name|repo
argument_list|,
name|sr
operator|.
name|allUsersObjects
argument_list|()
argument_list|)
argument_list|,
name|findNewId
argument_list|(
name|sr
operator|.
name|allUsersCommands
argument_list|()
argument_list|,
name|getRefName
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|change
operator|!=
literal|null
operator|&&
name|autoRebuild
condition|)
block|{
name|NoteDbChangeState
name|state
init|=
name|NoteDbChangeState
operator|.
name|parse
argument_list|(
name|change
argument_list|)
decl_stmt|;
comment|// Only check if this particular user's drafts are up to date, to avoid
comment|// reading unnecessary refs.
if|if
condition|(
operator|!
name|NoteDbChangeState
operator|.
name|areDraftsUpToDate
argument_list|(
name|state
argument_list|,
operator|new
name|RepoRefCache
argument_list|(
name|repo
argument_list|)
argument_list|,
name|getChangeId
argument_list|()
argument_list|,
name|author
argument_list|)
condition|)
block|{
return|return
name|rebuildAndOpen
argument_list|(
name|repo
argument_list|)
return|;
block|}
block|}
return|return
name|super
operator|.
name|openHandle
argument_list|(
name|repo
argument_list|)
return|;
block|}
DECL|method|findNewId (Iterable<ReceiveCommand> cmds, String refName)
specifier|private
specifier|static
name|ObjectId
name|findNewId
parameter_list|(
name|Iterable
argument_list|<
name|ReceiveCommand
argument_list|>
name|cmds
parameter_list|,
name|String
name|refName
parameter_list|)
block|{
for|for
control|(
name|ReceiveCommand
name|cmd
range|:
name|cmds
control|)
block|{
if|if
condition|(
name|cmd
operator|.
name|getRefName
argument_list|()
operator|.
name|equals
argument_list|(
name|refName
argument_list|)
condition|)
block|{
return|return
name|cmd
operator|.
name|getNewId
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|rebuildAndOpen (Repository repo)
specifier|private
name|LoadHandle
name|rebuildAndOpen
parameter_list|(
name|Repository
name|repo
parameter_list|)
throws|throws
name|NoSuchChangeException
throws|,
name|IOException
block|{
name|Timer1
operator|.
name|Context
name|timer
init|=
name|args
operator|.
name|metrics
operator|.
name|autoRebuildLatency
operator|.
name|start
argument_list|(
name|CHANGES
argument_list|)
decl_stmt|;
try|try
block|{
name|Change
operator|.
name|Id
name|cid
init|=
name|getChangeId
argument_list|()
decl_stmt|;
name|ReviewDb
name|db
init|=
name|args
operator|.
name|db
operator|.
name|get
argument_list|()
decl_stmt|;
name|ChangeRebuilder
name|rebuilder
init|=
name|args
operator|.
name|rebuilder
operator|.
name|get
argument_list|()
decl_stmt|;
name|NoteDbUpdateManager
operator|.
name|Result
name|r
decl_stmt|;
try|try
init|(
name|NoteDbUpdateManager
name|manager
init|=
name|rebuilder
operator|.
name|stage
argument_list|(
name|db
argument_list|,
name|cid
argument_list|)
init|)
block|{
if|if
condition|(
name|manager
operator|==
literal|null
condition|)
block|{
return|return
name|super
operator|.
name|openHandle
argument_list|(
name|repo
argument_list|)
return|;
comment|// May be null in tests.
block|}
name|r
operator|=
name|manager
operator|.
name|stageAndApplyDelta
argument_list|(
name|change
argument_list|)
expr_stmt|;
try|try
block|{
name|rebuilder
operator|.
name|execute
argument_list|(
name|db
argument_list|,
name|cid
argument_list|,
name|manager
argument_list|)
expr_stmt|;
name|repo
operator|.
name|scanForRepoChanges
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|IOException
name|e
parameter_list|)
block|{
comment|// See ChangeNotes#rebuildAndOpen.
name|logger
operator|.
name|atFine
argument_list|()
operator|.
name|log
argument_list|(
literal|"Rebuilding change %s via drafts failed: %s"
argument_list|,
name|getChangeId
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|args
operator|.
name|metrics
operator|.
name|autoRebuildFailureCount
operator|.
name|increment
argument_list|(
name|CHANGES
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|r
operator|.
name|staged
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|LoadHandle
operator|.
name|create
argument_list|(
name|ChangeNotesCommit
operator|.
name|newStagedRevWalk
argument_list|(
name|repo
argument_list|,
name|r
operator|.
name|staged
argument_list|()
operator|.
name|allUsersObjects
argument_list|()
argument_list|)
argument_list|,
name|draftsId
argument_list|(
name|r
argument_list|)
argument_list|)
return|;
block|}
block|}
return|return
name|LoadHandle
operator|.
name|create
argument_list|(
name|ChangeNotesCommit
operator|.
name|newRevWalk
argument_list|(
name|repo
argument_list|)
argument_list|,
name|draftsId
argument_list|(
name|r
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchChangeException
name|e
parameter_list|)
block|{
return|return
name|super
operator|.
name|openHandle
argument_list|(
name|repo
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|logger
operator|.
name|atFine
argument_list|()
operator|.
name|log
argument_list|(
literal|"Rebuilt change %s in %s in %s ms via drafts"
argument_list|,
name|getChangeId
argument_list|()
argument_list|,
name|change
operator|!=
literal|null
condition|?
literal|"project "
operator|+
name|change
operator|.
name|getProject
argument_list|()
else|:
literal|"unknown project"
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|timer
operator|.
name|stop
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|draftsId (NoteDbUpdateManager.Result r)
specifier|private
name|ObjectId
name|draftsId
parameter_list|(
name|NoteDbUpdateManager
operator|.
name|Result
name|r
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|r
operator|.
name|newState
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|r
operator|.
name|newState
argument_list|()
operator|.
name|getDraftIds
argument_list|()
operator|.
name|get
argument_list|(
name|author
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getNoteMap ()
name|NoteMap
name|getNoteMap
parameter_list|()
block|{
return|return
name|revisionNoteMap
operator|!=
literal|null
condition|?
name|revisionNoteMap
operator|.
name|noteMap
else|:
literal|null
return|;
block|}
block|}
end_class

end_unit

