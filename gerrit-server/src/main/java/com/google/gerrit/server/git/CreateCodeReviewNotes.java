begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2010 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.git
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
package|;
end_package

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
name|git
operator|.
name|GitRepositoryManager
operator|.
name|REFS_NOTES_REVIEW
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
name|ApprovalType
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
name|ApprovalTypes
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
name|ApprovalCategory
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
name|account
operator|.
name|AccountCache
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
name|AnonymousCowardName
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
name|CanonicalWebUrl
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
name|gwtorm
operator|.
name|server
operator|.
name|ResultSet
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|errors
operator|.
name|CorruptObjectException
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
name|Constants
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
name|RefUpdate
operator|.
name|Result
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
name|Note
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
name|notes
operator|.
name|NoteMapMerger
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
name|NoteMerger
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * This class create code review notes for given {@link CodeReviewCommit}s.  *<p>  * After the {@link #create(List, PersonIdent)} method is invoked once this  * instance must not be reused. Create a new instance of this class if needed.  */
end_comment

begin_class
DECL|class|CreateCodeReviewNotes
specifier|public
class|class
name|CreateCodeReviewNotes
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (ReviewDb reviewDb, Repository db)
name|CreateCodeReviewNotes
name|create
parameter_list|(
name|ReviewDb
name|reviewDb
parameter_list|,
name|Repository
name|db
parameter_list|)
function_decl|;
block|}
DECL|field|MAX_LOCK_FAILURE_CALLS
specifier|private
specifier|static
specifier|final
name|int
name|MAX_LOCK_FAILURE_CALLS
init|=
literal|10
decl_stmt|;
DECL|field|SLEEP_ON_LOCK_FAILURE_MS
specifier|private
specifier|static
specifier|final
name|int
name|SLEEP_ON_LOCK_FAILURE_MS
init|=
literal|25
decl_stmt|;
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
DECL|field|schema
specifier|private
specifier|final
name|ReviewDb
name|schema
decl_stmt|;
DECL|field|gerritIdent
specifier|private
specifier|final
name|PersonIdent
name|gerritIdent
decl_stmt|;
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
DECL|field|approvalTypes
specifier|private
specifier|final
name|ApprovalTypes
name|approvalTypes
decl_stmt|;
DECL|field|canonicalWebUrl
specifier|private
specifier|final
name|String
name|canonicalWebUrl
decl_stmt|;
DECL|field|anonymousCowardName
specifier|private
specifier|final
name|String
name|anonymousCowardName
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|Repository
name|db
decl_stmt|;
DECL|field|revWalk
specifier|private
specifier|final
name|RevWalk
name|revWalk
decl_stmt|;
DECL|field|inserter
specifier|private
specifier|final
name|ObjectInserter
name|inserter
decl_stmt|;
DECL|field|reader
specifier|private
specifier|final
name|ObjectReader
name|reader
decl_stmt|;
DECL|field|baseCommit
specifier|private
name|RevCommit
name|baseCommit
decl_stmt|;
DECL|field|base
specifier|private
name|NoteMap
name|base
decl_stmt|;
DECL|field|oursCommit
specifier|private
name|RevCommit
name|oursCommit
decl_stmt|;
DECL|field|ours
specifier|private
name|NoteMap
name|ours
decl_stmt|;
DECL|field|commits
specifier|private
name|List
argument_list|<
name|CodeReviewCommit
argument_list|>
name|commits
decl_stmt|;
DECL|field|author
specifier|private
name|PersonIdent
name|author
decl_stmt|;
annotation|@
name|Inject
DECL|method|CreateCodeReviewNotes ( @erritPersonIdent final PersonIdent gerritIdent, final AccountCache accountCache, final ApprovalTypes approvalTypes, final @Nullable @CanonicalWebUrl String canonicalWebUrl, final @AnonymousCowardName String anonymousCowardName, final @Assisted ReviewDb reviewDb, final @Assisted Repository db)
name|CreateCodeReviewNotes
parameter_list|(
annotation|@
name|GerritPersonIdent
specifier|final
name|PersonIdent
name|gerritIdent
parameter_list|,
specifier|final
name|AccountCache
name|accountCache
parameter_list|,
specifier|final
name|ApprovalTypes
name|approvalTypes
parameter_list|,
specifier|final
annotation|@
name|Nullable
annotation|@
name|CanonicalWebUrl
name|String
name|canonicalWebUrl
parameter_list|,
specifier|final
annotation|@
name|AnonymousCowardName
name|String
name|anonymousCowardName
parameter_list|,
specifier|final
annotation|@
name|Assisted
name|ReviewDb
name|reviewDb
parameter_list|,
specifier|final
annotation|@
name|Assisted
name|Repository
name|db
parameter_list|)
block|{
name|schema
operator|=
name|reviewDb
expr_stmt|;
name|this
operator|.
name|author
operator|=
name|gerritIdent
expr_stmt|;
name|this
operator|.
name|gerritIdent
operator|=
name|gerritIdent
expr_stmt|;
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
name|this
operator|.
name|approvalTypes
operator|=
name|approvalTypes
expr_stmt|;
name|this
operator|.
name|canonicalWebUrl
operator|=
name|canonicalWebUrl
expr_stmt|;
name|this
operator|.
name|anonymousCowardName
operator|=
name|anonymousCowardName
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|revWalk
operator|=
operator|new
name|RevWalk
argument_list|(
name|db
argument_list|)
expr_stmt|;
name|inserter
operator|=
name|db
operator|.
name|newObjectInserter
argument_list|()
expr_stmt|;
name|reader
operator|=
name|db
operator|.
name|newObjectReader
argument_list|()
expr_stmt|;
block|}
DECL|method|create (List<CodeReviewCommit> commits, PersonIdent author)
specifier|public
name|void
name|create
parameter_list|(
name|List
argument_list|<
name|CodeReviewCommit
argument_list|>
name|commits
parameter_list|,
name|PersonIdent
name|author
parameter_list|)
throws|throws
name|CodeReviewNoteCreationException
block|{
try|try
block|{
name|this
operator|.
name|commits
operator|=
name|commits
expr_stmt|;
name|this
operator|.
name|author
operator|=
name|author
expr_stmt|;
name|loadBase
argument_list|()
expr_stmt|;
name|applyNotes
argument_list|()
expr_stmt|;
name|updateRef
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CodeReviewNoteCreationException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CodeReviewNoteCreationException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|release
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|loadBase ()
specifier|public
name|void
name|loadBase
parameter_list|()
throws|throws
name|IOException
block|{
name|Ref
name|notesBranch
init|=
name|db
operator|.
name|getRef
argument_list|(
name|REFS_NOTES_REVIEW
argument_list|)
decl_stmt|;
if|if
condition|(
name|notesBranch
operator|!=
literal|null
condition|)
block|{
name|baseCommit
operator|=
name|revWalk
operator|.
name|parseCommit
argument_list|(
name|notesBranch
operator|.
name|getObjectId
argument_list|()
argument_list|)
expr_stmt|;
name|base
operator|=
name|NoteMap
operator|.
name|read
argument_list|(
name|revWalk
operator|.
name|getObjectReader
argument_list|()
argument_list|,
name|baseCommit
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|baseCommit
operator|!=
literal|null
condition|)
block|{
name|ours
operator|=
name|NoteMap
operator|.
name|read
argument_list|(
name|db
operator|.
name|newObjectReader
argument_list|()
argument_list|,
name|baseCommit
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ours
operator|=
name|NoteMap
operator|.
name|newEmptyMap
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|applyNotes ()
specifier|private
name|void
name|applyNotes
parameter_list|()
throws|throws
name|IOException
throws|,
name|CodeReviewNoteCreationException
block|{
name|StringBuilder
name|message
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Update notes for submitted changes\n\n"
argument_list|)
decl_stmt|;
for|for
control|(
name|CodeReviewCommit
name|c
range|:
name|commits
control|)
block|{
name|add
argument_list|(
name|c
operator|.
name|change
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|message
operator|.
name|append
argument_list|(
literal|"* "
argument_list|)
operator|.
name|append
argument_list|(
name|c
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|commit
argument_list|(
name|message
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|commit (String message)
specifier|public
name|void
name|commit
parameter_list|(
name|String
name|message
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|baseCommit
operator|!=
literal|null
condition|)
block|{
name|oursCommit
operator|=
name|createCommit
argument_list|(
name|ours
argument_list|,
name|author
argument_list|,
name|message
argument_list|,
name|baseCommit
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|oursCommit
operator|=
name|createCommit
argument_list|(
name|ours
argument_list|,
name|author
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|add (Change change, ObjectId commit)
specifier|public
name|void
name|add
parameter_list|(
name|Change
name|change
parameter_list|,
name|ObjectId
name|commit
parameter_list|)
throws|throws
name|MissingObjectException
throws|,
name|IncorrectObjectTypeException
throws|,
name|IOException
throws|,
name|CodeReviewNoteCreationException
block|{
if|if
condition|(
operator|!
operator|(
name|commit
operator|instanceof
name|RevCommit
operator|)
condition|)
block|{
name|commit
operator|=
name|revWalk
operator|.
name|parseCommit
argument_list|(
name|commit
argument_list|)
expr_stmt|;
block|}
name|RevCommit
name|c
init|=
operator|(
name|RevCommit
operator|)
name|commit
decl_stmt|;
name|ObjectId
name|noteContent
init|=
name|createNoteContent
argument_list|(
name|change
argument_list|,
name|c
argument_list|)
decl_stmt|;
if|if
condition|(
name|ours
operator|.
name|contains
argument_list|(
name|c
argument_list|)
condition|)
block|{
comment|// merge the existing and the new note as if they are both new
comment|// means: base == null
comment|// there is not really a common ancestry for these two note revisions
comment|// use the same NoteMerger that is used from the NoteMapMerger
name|NoteMerger
name|noteMerger
init|=
operator|new
name|ReviewNoteMerger
argument_list|()
decl_stmt|;
name|Note
name|newNote
init|=
operator|new
name|Note
argument_list|(
name|c
argument_list|,
name|noteContent
argument_list|)
decl_stmt|;
name|noteContent
operator|=
name|noteMerger
operator|.
name|merge
argument_list|(
literal|null
argument_list|,
name|newNote
argument_list|,
name|ours
operator|.
name|getNote
argument_list|(
name|c
argument_list|)
argument_list|,
name|reader
argument_list|,
name|inserter
argument_list|)
operator|.
name|getData
argument_list|()
expr_stmt|;
block|}
name|ours
operator|.
name|set
argument_list|(
name|c
argument_list|,
name|noteContent
argument_list|)
expr_stmt|;
block|}
DECL|method|createNoteContent (Change change, RevCommit commit)
specifier|private
name|ObjectId
name|createNoteContent
parameter_list|(
name|Change
name|change
parameter_list|,
name|RevCommit
name|commit
parameter_list|)
throws|throws
name|CodeReviewNoteCreationException
throws|,
name|IOException
block|{
try|try
block|{
name|ReviewNoteHeaderFormatter
name|formatter
init|=
operator|new
name|ReviewNoteHeaderFormatter
argument_list|(
name|author
operator|.
name|getTimeZone
argument_list|()
argument_list|,
name|anonymousCowardName
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|idList
init|=
name|commit
operator|.
name|getFooterLines
argument_list|(
name|CHANGE_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|idList
operator|.
name|isEmpty
argument_list|()
condition|)
name|formatter
operator|.
name|appendChangeId
argument_list|(
name|change
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|ResultSet
argument_list|<
name|PatchSetApproval
argument_list|>
name|approvals
init|=
name|schema
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|byPatchSet
argument_list|(
name|change
operator|.
name|currentPatchSetId
argument_list|()
argument_list|)
decl_stmt|;
name|PatchSetApproval
name|submit
init|=
literal|null
decl_stmt|;
for|for
control|(
name|PatchSetApproval
name|a
range|:
name|approvals
control|)
block|{
if|if
condition|(
name|a
operator|.
name|getValue
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// Ignore 0 values.
block|}
elseif|else
if|if
condition|(
name|ApprovalCategory
operator|.
name|SUBMIT
operator|.
name|equals
argument_list|(
name|a
operator|.
name|getCategoryId
argument_list|()
argument_list|)
condition|)
block|{
name|submit
operator|=
name|a
expr_stmt|;
block|}
else|else
block|{
name|ApprovalType
name|type
init|=
name|approvalTypes
operator|.
name|byId
argument_list|(
name|a
operator|.
name|getCategoryId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
name|formatter
operator|.
name|appendApproval
argument_list|(
name|type
operator|.
name|getCategory
argument_list|()
argument_list|,
name|a
operator|.
name|getValue
argument_list|()
argument_list|,
name|accountCache
operator|.
name|get
argument_list|(
name|a
operator|.
name|getAccountId
argument_list|()
argument_list|)
operator|.
name|getAccount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|submit
operator|!=
literal|null
condition|)
block|{
name|formatter
operator|.
name|appendSubmittedBy
argument_list|(
name|accountCache
operator|.
name|get
argument_list|(
name|submit
operator|.
name|getAccountId
argument_list|()
argument_list|)
operator|.
name|getAccount
argument_list|()
argument_list|)
expr_stmt|;
name|formatter
operator|.
name|appendSubmittedAt
argument_list|(
name|submit
operator|.
name|getGranted
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|canonicalWebUrl
operator|!=
literal|null
condition|)
block|{
name|formatter
operator|.
name|appendReviewedOn
argument_list|(
name|canonicalWebUrl
argument_list|,
name|change
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|formatter
operator|.
name|appendProject
argument_list|(
name|change
operator|.
name|getProject
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|formatter
operator|.
name|appendBranch
argument_list|(
name|change
operator|.
name|getDest
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|inserter
operator|.
name|insert
argument_list|(
name|Constants
operator|.
name|OBJ_BLOB
argument_list|,
name|formatter
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
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
name|CodeReviewNoteCreationException
argument_list|(
name|commit
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|updateRef ()
specifier|public
name|void
name|updateRef
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|CodeReviewNoteCreationException
throws|,
name|MissingObjectException
throws|,
name|IncorrectObjectTypeException
throws|,
name|CorruptObjectException
block|{
if|if
condition|(
name|baseCommit
operator|!=
literal|null
operator|&&
name|oursCommit
operator|.
name|getTree
argument_list|()
operator|.
name|equals
argument_list|(
name|baseCommit
operator|.
name|getTree
argument_list|()
argument_list|)
condition|)
block|{
comment|// If the trees are identical, there is no change in the notes.
comment|// Avoid saving this commit as it has no new information.
return|return;
block|}
name|int
name|remainingLockFailureCalls
init|=
name|MAX_LOCK_FAILURE_CALLS
decl_stmt|;
name|RefUpdate
name|refUpdate
init|=
name|createRefUpdate
argument_list|(
name|oursCommit
argument_list|,
name|baseCommit
argument_list|)
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|Result
name|result
init|=
name|refUpdate
operator|.
name|update
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|==
name|Result
operator|.
name|LOCK_FAILURE
condition|)
block|{
if|if
condition|(
operator|--
name|remainingLockFailureCalls
operator|>
literal|0
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|SLEEP_ON_LOCK_FAILURE_MS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|CodeReviewNoteCreationException
argument_list|(
literal|"Failed to lock the ref: "
operator|+
name|REFS_NOTES_REVIEW
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|result
operator|==
name|Result
operator|.
name|REJECTED
condition|)
block|{
name|RevCommit
name|theirsCommit
init|=
name|revWalk
operator|.
name|parseCommit
argument_list|(
name|refUpdate
operator|.
name|getOldObjectId
argument_list|()
argument_list|)
decl_stmt|;
name|NoteMap
name|theirs
init|=
name|NoteMap
operator|.
name|read
argument_list|(
name|revWalk
operator|.
name|getObjectReader
argument_list|()
argument_list|,
name|theirsCommit
argument_list|)
decl_stmt|;
name|NoteMapMerger
name|merger
init|=
operator|new
name|NoteMapMerger
argument_list|(
name|db
argument_list|)
decl_stmt|;
name|NoteMap
name|merged
init|=
name|merger
operator|.
name|merge
argument_list|(
name|base
argument_list|,
name|ours
argument_list|,
name|theirs
argument_list|)
decl_stmt|;
name|RevCommit
name|mergeCommit
init|=
name|createCommit
argument_list|(
name|merged
argument_list|,
name|gerritIdent
argument_list|,
literal|"Merged note commits\n"
argument_list|,
name|theirsCommit
argument_list|,
name|oursCommit
argument_list|)
decl_stmt|;
name|refUpdate
operator|=
name|createRefUpdate
argument_list|(
name|mergeCommit
argument_list|,
name|theirsCommit
argument_list|)
expr_stmt|;
name|remainingLockFailureCalls
operator|=
name|MAX_LOCK_FAILURE_CALLS
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|result
operator|==
name|Result
operator|.
name|IO_FAILURE
condition|)
block|{
throw|throw
operator|new
name|CodeReviewNoteCreationException
argument_list|(
literal|"Couldn't create code review notes because of IO_FAILURE"
argument_list|)
throw|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
DECL|method|release ()
specifier|public
name|void
name|release
parameter_list|()
block|{
name|reader
operator|.
name|release
argument_list|()
expr_stmt|;
name|inserter
operator|.
name|release
argument_list|()
expr_stmt|;
name|revWalk
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
DECL|method|createCommit (NoteMap map, PersonIdent author, String message, RevCommit... parents)
specifier|private
name|RevCommit
name|createCommit
parameter_list|(
name|NoteMap
name|map
parameter_list|,
name|PersonIdent
name|author
parameter_list|,
name|String
name|message
parameter_list|,
name|RevCommit
modifier|...
name|parents
parameter_list|)
throws|throws
name|IOException
block|{
name|CommitBuilder
name|b
init|=
operator|new
name|CommitBuilder
argument_list|()
decl_stmt|;
name|b
operator|.
name|setTreeId
argument_list|(
name|map
operator|.
name|writeTree
argument_list|(
name|inserter
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|.
name|setAuthor
argument_list|(
name|author
operator|!=
literal|null
condition|?
name|author
else|:
name|gerritIdent
argument_list|)
expr_stmt|;
name|b
operator|.
name|setCommitter
argument_list|(
name|gerritIdent
argument_list|)
expr_stmt|;
if|if
condition|(
name|parents
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|b
operator|.
name|setParentIds
argument_list|(
name|parents
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|setMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|ObjectId
name|commitId
init|=
name|inserter
operator|.
name|insert
argument_list|(
name|b
argument_list|)
decl_stmt|;
name|inserter
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|revWalk
operator|.
name|parseCommit
argument_list|(
name|commitId
argument_list|)
return|;
block|}
DECL|method|createRefUpdate (ObjectId newObjectId, ObjectId expectedOldObjectId)
specifier|private
name|RefUpdate
name|createRefUpdate
parameter_list|(
name|ObjectId
name|newObjectId
parameter_list|,
name|ObjectId
name|expectedOldObjectId
parameter_list|)
throws|throws
name|IOException
block|{
name|RefUpdate
name|refUpdate
init|=
name|db
operator|.
name|updateRef
argument_list|(
name|REFS_NOTES_REVIEW
argument_list|)
decl_stmt|;
name|refUpdate
operator|.
name|setNewObjectId
argument_list|(
name|newObjectId
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectedOldObjectId
operator|==
literal|null
condition|)
block|{
name|refUpdate
operator|.
name|setExpectedOldObjectId
argument_list|(
name|ObjectId
operator|.
name|zeroId
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|refUpdate
operator|.
name|setExpectedOldObjectId
argument_list|(
name|expectedOldObjectId
argument_list|)
expr_stmt|;
block|}
return|return
name|refUpdate
return|;
block|}
block|}
end_class

end_unit

