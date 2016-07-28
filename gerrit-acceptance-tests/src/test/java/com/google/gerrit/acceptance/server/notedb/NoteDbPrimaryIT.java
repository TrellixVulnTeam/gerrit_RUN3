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
DECL|package|com.google.gerrit.acceptance.server.notedb
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
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
name|truth
operator|.
name|Truth
operator|.
name|assertThat
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
name|truth
operator|.
name|TruthJUnit
operator|.
name|assume
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
name|NoteDbChangeState
operator|.
name|PrimaryStorage
operator|.
name|REVIEW_DB
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|toList
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
name|gerrit
operator|.
name|acceptance
operator|.
name|AbstractDaemonTest
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
name|acceptance
operator|.
name|PushOneCommit
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
name|DraftInput
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
name|ReviewInput
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
name|client
operator|.
name|ChangeStatus
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
name|common
operator|.
name|ApprovalInfo
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
name|common
operator|.
name|ChangeInfo
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
name|common
operator|.
name|CommentInfo
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
name|notedb
operator|.
name|NoteDbChangeState
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
name|testutil
operator|.
name|NoteDbMode
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
name|lib
operator|.
name|Repository
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
DECL|class|NoteDbPrimaryIT
specifier|public
class|class
name|NoteDbPrimaryIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Inject
DECL|field|allUsers
specifier|private
name|AllUsersName
name|allUsers
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|assume
argument_list|()
operator|.
name|that
argument_list|(
name|NoteDbMode
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|NoteDbMode
operator|.
name|READ_WRITE
argument_list|)
expr_stmt|;
name|db
operator|=
name|ReviewDbUtil
operator|.
name|unwrapDb
argument_list|(
name|db
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|updateChange ()
specifier|public
name|void
name|updateChange
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|Change
operator|.
name|Id
name|id
init|=
name|r
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
name|setNoteDbPrimary
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|approve
argument_list|()
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|submit
argument_list|()
expr_stmt|;
name|ChangeInfo
name|info
init|=
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|status
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|ChangeStatus
operator|.
name|MERGED
argument_list|)
expr_stmt|;
name|ApprovalInfo
name|approval
init|=
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|info
operator|.
name|labels
operator|.
name|get
argument_list|(
literal|"Code-Review"
argument_list|)
operator|.
name|all
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|approval
operator|.
name|_accountId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|admin
operator|.
name|id
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|approval
operator|.
name|value
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|messages
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Iterables
operator|.
name|getLast
argument_list|(
name|info
operator|.
name|messages
argument_list|)
operator|.
name|message
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Change has been successfully merged by "
operator|+
name|admin
operator|.
name|fullName
argument_list|)
expr_stmt|;
name|ChangeNotes
name|notes
init|=
name|notesFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|project
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|notes
operator|.
name|getChange
argument_list|()
operator|.
name|getStatus
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|Change
operator|.
name|Status
operator|.
name|MERGED
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|notes
operator|.
name|getChange
argument_list|()
operator|.
name|getNoteDbState
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|NoteDbChangeState
operator|.
name|NOTE_DB_PRIMARY_STATE
argument_list|)
expr_stmt|;
comment|// Writes weren't reflected in ReviewDb.
name|assertThat
argument_list|(
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|id
argument_list|)
operator|.
name|getStatus
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|Change
operator|.
name|Status
operator|.
name|NEW
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|db
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|byChange
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|db
operator|.
name|changeMessages
argument_list|()
operator|.
name|byChange
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteDraftComment ()
specifier|public
name|void
name|deleteDraftComment
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|Change
operator|.
name|Id
name|id
init|=
name|r
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
name|setNoteDbPrimary
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|DraftInput
name|din
init|=
operator|new
name|DraftInput
argument_list|()
decl_stmt|;
name|din
operator|.
name|path
operator|=
name|PushOneCommit
operator|.
name|FILE_NAME
expr_stmt|;
name|din
operator|.
name|line
operator|=
literal|1
expr_stmt|;
name|din
operator|.
name|message
operator|=
literal|"A comment"
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|createDraft
argument_list|(
name|din
argument_list|)
expr_stmt|;
name|CommentInfo
name|di
init|=
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|drafts
argument_list|()
operator|.
name|get
argument_list|(
name|PushOneCommit
operator|.
name|FILE_NAME
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|di
operator|.
name|message
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|din
operator|.
name|message
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|db
operator|.
name|patchComments
argument_list|()
operator|.
name|draftByChangeFileAuthor
argument_list|(
name|id
argument_list|,
name|din
operator|.
name|path
argument_list|,
name|admin
operator|.
name|id
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|draft
argument_list|(
name|di
operator|.
name|id
argument_list|)
operator|.
name|delete
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|drafts
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteVote ()
specifier|public
name|void
name|deleteVote
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|Change
operator|.
name|Id
name|id
init|=
name|r
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
name|setNoteDbPrimary
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|approve
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ApprovalInfo
argument_list|>
name|approvals
init|=
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|labels
operator|.
name|get
argument_list|(
literal|"Code-Review"
argument_list|)
operator|.
name|all
decl_stmt|;
name|assertThat
argument_list|(
name|approvals
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|approvals
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|value
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|reviewer
argument_list|(
name|admin
operator|.
name|id
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|deleteVote
argument_list|(
literal|"Code-Review"
argument_list|)
expr_stmt|;
name|approvals
operator|=
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|labels
operator|.
name|get
argument_list|(
literal|"Code-Review"
argument_list|)
operator|.
name|all
expr_stmt|;
name|assertThat
argument_list|(
name|approvals
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|approvals
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|value
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteVoteViaReview ()
specifier|public
name|void
name|deleteVoteViaReview
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|Change
operator|.
name|Id
name|id
init|=
name|r
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
name|setNoteDbPrimary
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|approve
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ApprovalInfo
argument_list|>
name|approvals
init|=
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|labels
operator|.
name|get
argument_list|(
literal|"Code-Review"
argument_list|)
operator|.
name|all
decl_stmt|;
name|assertThat
argument_list|(
name|approvals
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|approvals
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|value
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|review
argument_list|(
name|ReviewInput
operator|.
name|noScore
argument_list|()
argument_list|)
expr_stmt|;
name|approvals
operator|=
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|labels
operator|.
name|get
argument_list|(
literal|"Code-Review"
argument_list|)
operator|.
name|all
expr_stmt|;
name|assertThat
argument_list|(
name|approvals
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|approvals
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|value
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteReviewer ()
specifier|public
name|void
name|deleteReviewer
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|r
init|=
name|createChange
argument_list|()
decl_stmt|;
name|Change
operator|.
name|Id
name|id
init|=
name|r
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
name|setNoteDbPrimary
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|addReviewer
argument_list|(
name|user
operator|.
name|id
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|getReviewers
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|user
operator|.
name|id
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|reviewer
argument_list|(
name|user
operator|.
name|id
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|getReviewers
argument_list|(
name|id
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
block|}
DECL|method|setNoteDbPrimary (Change.Id id)
specifier|private
name|void
name|setNoteDbPrimary
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|)
throws|throws
name|Exception
block|{
name|Change
name|c
init|=
name|db
operator|.
name|changes
argument_list|()
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|c
argument_list|)
operator|.
name|named
argument_list|(
literal|"change "
operator|+
name|id
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|NoteDbChangeState
name|state
init|=
name|NoteDbChangeState
operator|.
name|parse
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|state
operator|.
name|getPrimaryStorage
argument_list|()
argument_list|)
operator|.
name|named
argument_list|(
literal|"storage of "
operator|+
name|id
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|REVIEW_DB
argument_list|)
expr_stmt|;
try|try
init|(
name|Repository
name|changeRepo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|c
operator|.
name|getProject
argument_list|()
argument_list|)
init|;
name|Repository
name|allUsersRepo
operator|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|allUsers
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|state
operator|.
name|isUpToDate
argument_list|(
operator|new
name|RepoRefCache
argument_list|(
name|changeRepo
argument_list|)
argument_list|,
operator|new
name|RepoRefCache
argument_list|(
name|allUsersRepo
argument_list|)
argument_list|)
argument_list|)
operator|.
name|named
argument_list|(
literal|"change "
operator|+
name|id
operator|+
literal|" up to date"
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
name|c
operator|.
name|setNoteDbState
argument_list|(
name|NoteDbChangeState
operator|.
name|NOTE_DB_PRIMARY_STATE
argument_list|)
expr_stmt|;
name|db
operator|.
name|changes
argument_list|()
operator|.
name|update
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getReviewers (Change.Id id)
specifier|private
name|List
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|getReviewers
parameter_list|(
name|Change
operator|.
name|Id
name|id
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|id
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|reviewers
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|flatMap
argument_list|(
name|Collection
operator|::
name|stream
argument_list|)
operator|.
name|map
argument_list|(
name|a
lambda|->
operator|new
name|Account
operator|.
name|Id
argument_list|(
name|a
operator|.
name|_accountId
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|toList
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

