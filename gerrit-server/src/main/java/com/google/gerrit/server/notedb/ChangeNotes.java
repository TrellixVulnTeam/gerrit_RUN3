begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
name|server
operator|.
name|notedb
operator|.
name|ChangeNoteUtil
operator|.
name|GERRIT_PLACEHOLDER_HOST
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
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|ImmutableMap
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
name|ImmutableSet
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
name|ImmutableSetMultimap
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
name|ImmutableSortedMap
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
name|ImmutableSortedSet
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
name|Ordering
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
name|primitives
operator|.
name|Ints
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
name|ReviewDbUtil
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
name|config
operator|.
name|AllUsersName
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
name|config
operator|.
name|AllUsersNameProvider
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

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
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

begin_comment
comment|/** View of a single {@link Change} based on the log of its notes branch. */
end_comment

begin_class
DECL|class|ChangeNotes
specifier|public
class|class
name|ChangeNotes
extends|extends
name|AbstractChangeNotes
argument_list|<
name|ChangeNotes
argument_list|>
block|{
DECL|field|PSA_BY_TIME
specifier|static
specifier|final
name|Ordering
argument_list|<
name|PatchSetApproval
argument_list|>
name|PSA_BY_TIME
init|=
name|Ordering
operator|.
name|natural
argument_list|()
operator|.
name|onResultOf
argument_list|(
operator|new
name|Function
argument_list|<
name|PatchSetApproval
argument_list|,
name|Timestamp
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Timestamp
name|apply
parameter_list|(
name|PatchSetApproval
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getGranted
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
DECL|field|MESSAGE_BY_TIME
specifier|public
specifier|static
specifier|final
name|Ordering
argument_list|<
name|ChangeMessage
argument_list|>
name|MESSAGE_BY_TIME
init|=
name|Ordering
operator|.
name|natural
argument_list|()
operator|.
name|onResultOf
argument_list|(
operator|new
name|Function
argument_list|<
name|ChangeMessage
argument_list|,
name|Timestamp
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Timestamp
name|apply
parameter_list|(
name|ChangeMessage
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getWrittenOn
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
DECL|method|parseException (Change.Id changeId, String fmt, Object... args)
specifier|public
specifier|static
name|ConfigInvalidException
name|parseException
parameter_list|(
name|Change
operator|.
name|Id
name|changeId
parameter_list|,
name|String
name|fmt
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
return|return
operator|new
name|ConfigInvalidException
argument_list|(
literal|"Change "
operator|+
name|changeId
operator|+
literal|": "
operator|+
name|String
operator|.
name|format
argument_list|(
name|fmt
argument_list|,
name|args
argument_list|)
argument_list|)
return|;
block|}
DECL|method|parseIdent (PersonIdent ident, Change.Id changeId)
specifier|public
specifier|static
name|Account
operator|.
name|Id
name|parseIdent
parameter_list|(
name|PersonIdent
name|ident
parameter_list|,
name|Change
operator|.
name|Id
name|changeId
parameter_list|)
throws|throws
name|ConfigInvalidException
block|{
name|String
name|email
init|=
name|ident
operator|.
name|getEmailAddress
argument_list|()
decl_stmt|;
name|int
name|at
init|=
name|email
operator|.
name|indexOf
argument_list|(
literal|'@'
argument_list|)
decl_stmt|;
if|if
condition|(
name|at
operator|>=
literal|0
condition|)
block|{
name|String
name|host
init|=
name|email
operator|.
name|substring
argument_list|(
name|at
operator|+
literal|1
argument_list|,
name|email
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|Integer
name|id
init|=
name|Ints
operator|.
name|tryParse
argument_list|(
name|email
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|at
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
operator|&&
name|host
operator|.
name|equals
argument_list|(
name|GERRIT_PLACEHOLDER_HOST
argument_list|)
condition|)
block|{
return|return
operator|new
name|Account
operator|.
name|Id
argument_list|(
name|id
argument_list|)
return|;
block|}
block|}
throw|throw
name|parseException
argument_list|(
name|changeId
argument_list|,
literal|"invalid identity, expected<id>@%s: %s"
argument_list|,
name|GERRIT_PLACEHOLDER_HOST
argument_list|,
name|email
argument_list|)
throw|;
block|}
annotation|@
name|Singleton
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
block|{
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|migration
specifier|private
specifier|final
name|NotesMigration
name|migration
decl_stmt|;
DECL|field|allUsersProvider
specifier|private
specifier|final
name|AllUsersNameProvider
name|allUsersProvider
decl_stmt|;
annotation|@
name|VisibleForTesting
annotation|@
name|Inject
DECL|method|Factory (GitRepositoryManager repoManager, NotesMigration migration, AllUsersNameProvider allUsersProvider)
specifier|public
name|Factory
parameter_list|(
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|NotesMigration
name|migration
parameter_list|,
name|AllUsersNameProvider
name|allUsersProvider
parameter_list|)
block|{
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|this
operator|.
name|migration
operator|=
name|migration
expr_stmt|;
name|this
operator|.
name|allUsersProvider
operator|=
name|allUsersProvider
expr_stmt|;
block|}
DECL|method|create (Change change)
specifier|public
name|ChangeNotes
name|create
parameter_list|(
name|Change
name|change
parameter_list|)
block|{
return|return
operator|new
name|ChangeNotes
argument_list|(
name|repoManager
argument_list|,
name|migration
argument_list|,
name|allUsersProvider
argument_list|,
name|change
argument_list|)
return|;
block|}
block|}
DECL|field|change
specifier|private
specifier|final
name|Change
name|change
decl_stmt|;
DECL|field|patchSets
specifier|private
name|ImmutableSortedMap
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|,
name|PatchSet
argument_list|>
name|patchSets
decl_stmt|;
DECL|field|approvals
specifier|private
name|ImmutableListMultimap
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|,
name|PatchSetApproval
argument_list|>
name|approvals
decl_stmt|;
DECL|field|reviewers
specifier|private
name|ImmutableSetMultimap
argument_list|<
name|ReviewerStateInternal
argument_list|,
name|Account
operator|.
name|Id
argument_list|>
name|reviewers
decl_stmt|;
DECL|field|allPastReviewers
specifier|private
name|ImmutableList
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|allPastReviewers
decl_stmt|;
DECL|field|submitRecords
specifier|private
name|ImmutableList
argument_list|<
name|SubmitRecord
argument_list|>
name|submitRecords
decl_stmt|;
DECL|field|allChangeMessages
specifier|private
name|ImmutableList
argument_list|<
name|ChangeMessage
argument_list|>
name|allChangeMessages
decl_stmt|;
DECL|field|changeMessagesByPatchSet
specifier|private
name|ImmutableListMultimap
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|,
name|ChangeMessage
argument_list|>
name|changeMessagesByPatchSet
decl_stmt|;
DECL|field|comments
specifier|private
name|ImmutableListMultimap
argument_list|<
name|RevId
argument_list|,
name|PatchLineComment
argument_list|>
name|comments
decl_stmt|;
DECL|field|hashtags
specifier|private
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|hashtags
decl_stmt|;
comment|// Mutable note map state, only used by ChangeUpdate to make in-place editing
comment|// of notes easier.
DECL|field|noteMap
name|NoteMap
name|noteMap
decl_stmt|;
DECL|field|revisionNotes
name|Map
argument_list|<
name|RevId
argument_list|,
name|RevisionNote
argument_list|>
name|revisionNotes
decl_stmt|;
DECL|field|allUsers
specifier|private
specifier|final
name|AllUsersName
name|allUsers
decl_stmt|;
DECL|field|draftCommentNotes
specifier|private
name|DraftCommentNotes
name|draftCommentNotes
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|method|ChangeNotes (GitRepositoryManager repoManager, NotesMigration migration, AllUsersNameProvider allUsersProvider, Change change)
specifier|public
name|ChangeNotes
parameter_list|(
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|NotesMigration
name|migration
parameter_list|,
name|AllUsersNameProvider
name|allUsersProvider
parameter_list|,
name|Change
name|change
parameter_list|)
block|{
name|super
argument_list|(
name|repoManager
argument_list|,
name|migration
argument_list|,
name|change
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|allUsers
operator|=
name|allUsersProvider
operator|.
name|get
argument_list|()
expr_stmt|;
name|this
operator|.
name|change
operator|=
operator|new
name|Change
argument_list|(
name|change
argument_list|)
expr_stmt|;
block|}
DECL|method|getChange ()
specifier|public
name|Change
name|getChange
parameter_list|()
block|{
return|return
name|change
return|;
block|}
DECL|method|getPatchSets ()
specifier|public
name|ImmutableMap
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|,
name|PatchSet
argument_list|>
name|getPatchSets
parameter_list|()
block|{
return|return
name|patchSets
return|;
block|}
DECL|method|getApprovals ()
specifier|public
name|ImmutableListMultimap
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|,
name|PatchSetApproval
argument_list|>
name|getApprovals
parameter_list|()
block|{
return|return
name|approvals
return|;
block|}
DECL|method|getReviewers ()
specifier|public
name|ImmutableSetMultimap
argument_list|<
name|ReviewerStateInternal
argument_list|,
name|Account
operator|.
name|Id
argument_list|>
name|getReviewers
parameter_list|()
block|{
return|return
name|reviewers
return|;
block|}
comment|/**    *    * @return a ImmutableSet of all hashtags for this change sorted in alphabetical order.    */
DECL|method|getHashtags ()
specifier|public
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|getHashtags
parameter_list|()
block|{
return|return
name|ImmutableSortedSet
operator|.
name|copyOf
argument_list|(
name|hashtags
argument_list|)
return|;
block|}
comment|/**    * @return a list of all users who have ever been a reviewer on this change.    */
DECL|method|getAllPastReviewers ()
specifier|public
name|ImmutableList
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|getAllPastReviewers
parameter_list|()
block|{
return|return
name|allPastReviewers
return|;
block|}
comment|/**    * @return submit records stored during the most recent submit; only for    *     changes that were actually submitted.    */
DECL|method|getSubmitRecords ()
specifier|public
name|ImmutableList
argument_list|<
name|SubmitRecord
argument_list|>
name|getSubmitRecords
parameter_list|()
block|{
return|return
name|submitRecords
return|;
block|}
comment|/** @return all change messages, in chronological order, oldest first. */
DECL|method|getChangeMessages ()
specifier|public
name|ImmutableList
argument_list|<
name|ChangeMessage
argument_list|>
name|getChangeMessages
parameter_list|()
block|{
return|return
name|allChangeMessages
return|;
block|}
comment|/**    * @return change messages by patch set, in chronological order, oldest    *     first.    */
specifier|public
name|ImmutableListMultimap
argument_list|<
name|PatchSet
operator|.
name|Id
argument_list|,
name|ChangeMessage
argument_list|>
DECL|method|getChangeMessagesByPatchSet ()
name|getChangeMessagesByPatchSet
parameter_list|()
block|{
return|return
name|changeMessagesByPatchSet
return|;
block|}
comment|/** @return inline comments on each revision. */
DECL|method|getComments ()
specifier|public
name|ImmutableListMultimap
argument_list|<
name|RevId
argument_list|,
name|PatchLineComment
argument_list|>
name|getComments
parameter_list|()
block|{
return|return
name|comments
return|;
block|}
DECL|method|getDraftComments ( Account.Id author)
specifier|public
name|ImmutableListMultimap
argument_list|<
name|RevId
argument_list|,
name|PatchLineComment
argument_list|>
name|getDraftComments
parameter_list|(
name|Account
operator|.
name|Id
name|author
parameter_list|)
throws|throws
name|OrmException
block|{
name|loadDraftComments
argument_list|(
name|author
argument_list|)
expr_stmt|;
return|return
name|draftCommentNotes
operator|.
name|getComments
argument_list|()
return|;
block|}
comment|/**    * If draft comments have already been loaded for this author, then they will    * not be reloaded. However, this method will load the comments if no draft    * comments have been loaded or if the caller would like the drafts for    * another author.    */
DECL|method|loadDraftComments (Account.Id author)
specifier|private
name|void
name|loadDraftComments
parameter_list|(
name|Account
operator|.
name|Id
name|author
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
name|draftCommentNotes
operator|==
literal|null
operator|||
operator|!
name|author
operator|.
name|equals
argument_list|(
name|draftCommentNotes
operator|.
name|getAuthor
argument_list|()
argument_list|)
condition|)
block|{
name|draftCommentNotes
operator|=
operator|new
name|DraftCommentNotes
argument_list|(
name|repoManager
argument_list|,
name|migration
argument_list|,
name|allUsers
argument_list|,
name|getChangeId
argument_list|()
argument_list|,
name|author
argument_list|)
expr_stmt|;
name|draftCommentNotes
operator|.
name|load
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getDraftCommentNotes ()
name|DraftCommentNotes
name|getDraftCommentNotes
parameter_list|()
block|{
return|return
name|draftCommentNotes
return|;
block|}
DECL|method|containsComment (PatchLineComment c)
specifier|public
name|boolean
name|containsComment
parameter_list|(
name|PatchLineComment
name|c
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
name|containsCommentPublished
argument_list|(
name|c
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|loadDraftComments
argument_list|(
name|c
operator|.
name|getAuthor
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|draftCommentNotes
operator|.
name|containsComment
argument_list|(
name|c
argument_list|)
return|;
block|}
DECL|method|containsCommentPublished (PatchLineComment c)
specifier|public
name|boolean
name|containsCommentPublished
parameter_list|(
name|PatchLineComment
name|c
parameter_list|)
block|{
for|for
control|(
name|PatchLineComment
name|l
range|:
name|getComments
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|c
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|l
operator|.
name|getKey
argument_list|()
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
comment|/** @return the NoteMap */
DECL|method|getNoteMap ()
name|NoteMap
name|getNoteMap
parameter_list|()
block|{
return|return
name|noteMap
return|;
block|}
DECL|method|getRevisionNotes ()
name|Map
argument_list|<
name|RevId
argument_list|,
name|RevisionNote
argument_list|>
name|getRevisionNotes
parameter_list|()
block|{
return|return
name|revisionNotes
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
name|ChangeNoteUtil
operator|.
name|changeRefName
argument_list|(
name|getChangeId
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getCurrentPatchSet ()
specifier|public
name|PatchSet
name|getCurrentPatchSet
parameter_list|()
block|{
name|PatchSet
operator|.
name|Id
name|psId
init|=
name|change
operator|.
name|currentPatchSetId
argument_list|()
decl_stmt|;
return|return
name|checkNotNull
argument_list|(
name|patchSets
operator|.
name|get
argument_list|(
name|psId
argument_list|)
argument_list|,
literal|"missing current patch set %s"
argument_list|,
name|psId
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|onLoad ()
specifier|protected
name|void
name|onLoad
parameter_list|()
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
name|ObjectId
name|rev
init|=
name|getRevision
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
try|try
init|(
name|RevWalk
name|walk
init|=
operator|new
name|RevWalk
argument_list|(
name|reader
argument_list|)
init|;
name|ChangeNotesParser
name|parser
operator|=
operator|new
name|ChangeNotesParser
argument_list|(
name|change
argument_list|,
name|rev
argument_list|,
name|walk
argument_list|,
name|repoManager
argument_list|)
init|)
block|{
name|parser
operator|.
name|parseAll
argument_list|()
expr_stmt|;
if|if
condition|(
name|parser
operator|.
name|status
operator|!=
literal|null
condition|)
block|{
name|change
operator|.
name|setStatus
argument_list|(
name|parser
operator|.
name|status
argument_list|)
expr_stmt|;
block|}
name|approvals
operator|=
name|parser
operator|.
name|buildApprovals
argument_list|()
expr_stmt|;
name|changeMessagesByPatchSet
operator|=
name|parser
operator|.
name|buildMessagesByPatchSet
argument_list|()
expr_stmt|;
name|allChangeMessages
operator|=
name|parser
operator|.
name|buildAllMessages
argument_list|()
expr_stmt|;
name|comments
operator|=
name|ImmutableListMultimap
operator|.
name|copyOf
argument_list|(
name|parser
operator|.
name|comments
argument_list|)
expr_stmt|;
name|noteMap
operator|=
name|parser
operator|.
name|noteMap
expr_stmt|;
name|revisionNotes
operator|=
name|parser
operator|.
name|revisionNotes
expr_stmt|;
name|change
operator|.
name|setDest
argument_list|(
operator|new
name|Branch
operator|.
name|NameKey
argument_list|(
name|getProjectName
argument_list|()
argument_list|,
name|parser
operator|.
name|branch
argument_list|)
argument_list|)
expr_stmt|;
name|change
operator|.
name|setTopic
argument_list|(
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|parser
operator|.
name|topic
argument_list|)
argument_list|)
expr_stmt|;
name|change
operator|.
name|setCreatedOn
argument_list|(
name|parser
operator|.
name|createdOn
argument_list|)
expr_stmt|;
name|change
operator|.
name|setLastUpdatedOn
argument_list|(
name|parser
operator|.
name|lastUpdatedOn
argument_list|)
expr_stmt|;
name|change
operator|.
name|setOwner
argument_list|(
name|parser
operator|.
name|ownerId
argument_list|)
expr_stmt|;
name|change
operator|.
name|setSubmissionId
argument_list|(
name|parser
operator|.
name|submissionId
argument_list|)
expr_stmt|;
name|patchSets
operator|=
name|ImmutableSortedMap
operator|.
name|copyOf
argument_list|(
name|parser
operator|.
name|patchSets
argument_list|,
name|ReviewDbUtil
operator|.
name|intKeyOrdering
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|patchSets
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|change
operator|.
name|setCurrentPatchSet
argument_list|(
name|parser
operator|.
name|currentPatchSetId
argument_list|,
name|parser
operator|.
name|subject
argument_list|,
name|parser
operator|.
name|originalSubject
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// TODO(dborowitz): This should be an error, but for now it's required
comment|// for some tests to pass.
name|change
operator|.
name|clearCurrentPatchSet
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|parser
operator|.
name|hashtags
operator|!=
literal|null
condition|)
block|{
name|hashtags
operator|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|parser
operator|.
name|hashtags
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|hashtags
operator|=
name|ImmutableSet
operator|.
name|of
argument_list|()
expr_stmt|;
block|}
name|ImmutableSetMultimap
operator|.
name|Builder
argument_list|<
name|ReviewerStateInternal
argument_list|,
name|Account
operator|.
name|Id
argument_list|>
name|reviewers
init|=
name|ImmutableSetMultimap
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|ReviewerStateInternal
argument_list|>
name|e
range|:
name|parser
operator|.
name|reviewers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|reviewers
operator|.
name|put
argument_list|(
name|e
operator|.
name|getValue
argument_list|()
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|reviewers
operator|=
name|reviewers
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|allPastReviewers
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|parser
operator|.
name|allPastReviewers
argument_list|)
expr_stmt|;
name|submitRecords
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|parser
operator|.
name|submitRecords
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|loadDefaults ()
specifier|protected
name|void
name|loadDefaults
parameter_list|()
block|{
name|approvals
operator|=
name|ImmutableListMultimap
operator|.
name|of
argument_list|()
expr_stmt|;
name|reviewers
operator|=
name|ImmutableSetMultimap
operator|.
name|of
argument_list|()
expr_stmt|;
name|submitRecords
operator|=
name|ImmutableList
operator|.
name|of
argument_list|()
expr_stmt|;
name|allChangeMessages
operator|=
name|ImmutableList
operator|.
name|of
argument_list|()
expr_stmt|;
name|changeMessagesByPatchSet
operator|=
name|ImmutableListMultimap
operator|.
name|of
argument_list|()
expr_stmt|;
name|comments
operator|=
name|ImmutableListMultimap
operator|.
name|of
argument_list|()
expr_stmt|;
name|hashtags
operator|=
name|ImmutableSet
operator|.
name|of
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onSave (CommitBuilder commit)
specifier|protected
name|boolean
name|onSave
parameter_list|(
name|CommitBuilder
name|commit
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" is read-only"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getProjectName ()
specifier|protected
name|Project
operator|.
name|NameKey
name|getProjectName
parameter_list|()
block|{
return|return
name|getChange
argument_list|()
operator|.
name|getProject
argument_list|()
return|;
block|}
block|}
end_class

end_unit

