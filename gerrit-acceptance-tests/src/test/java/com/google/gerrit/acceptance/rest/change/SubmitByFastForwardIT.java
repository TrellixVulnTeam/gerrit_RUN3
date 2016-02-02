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
DECL|package|com.google.gerrit.acceptance.rest.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|rest
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
name|truth
operator|.
name|Truth
operator|.
name|assertThat
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
name|SubmitInput
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
name|client
operator|.
name|SubmitType
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
name|ActionInfo
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
name|ChangeMessageInfo
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
name|server
operator|.
name|change
operator|.
name|Submit
operator|.
name|TestSubmitInput
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
name|Map
import|;
end_import

begin_class
DECL|class|SubmitByFastForwardIT
specifier|public
class|class
name|SubmitByFastForwardIT
extends|extends
name|AbstractSubmit
block|{
annotation|@
name|Override
DECL|method|getSubmitType ()
specifier|protected
name|SubmitType
name|getSubmitType
parameter_list|()
block|{
return|return
name|SubmitType
operator|.
name|FAST_FORWARD_ONLY
return|;
block|}
annotation|@
name|Test
DECL|method|submitWithFastForward ()
specifier|public
name|void
name|submitWithFastForward
parameter_list|()
throws|throws
name|Exception
block|{
name|RevCommit
name|oldHead
init|=
name|getRemoteHead
argument_list|()
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change
init|=
name|createChange
argument_list|()
decl_stmt|;
name|submit
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|RevCommit
name|head
init|=
name|getRemoteHead
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|head
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|change
operator|.
name|getCommitId
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|head
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|oldHead
argument_list|)
expr_stmt|;
name|assertSubmitter
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|submitTwoChangesWithFastForward ()
specifier|public
name|void
name|submitTwoChangesWithFastForward
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|change
init|=
name|createChange
argument_list|()
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change2
init|=
name|createChange
argument_list|()
decl_stmt|;
name|String
name|id1
init|=
name|change
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|String
name|id2
init|=
name|change2
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|approve
argument_list|(
name|id1
argument_list|)
expr_stmt|;
name|submit
argument_list|(
name|id2
argument_list|)
expr_stmt|;
name|RevCommit
name|head
init|=
name|getRemoteHead
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|head
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|change2
operator|.
name|getCommitId
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|head
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|change
operator|.
name|getCommitId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSubmitter
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertSubmitter
argument_list|(
name|change2
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertPersonEquals
argument_list|(
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|,
name|head
operator|.
name|getAuthorIdent
argument_list|()
argument_list|)
expr_stmt|;
name|assertPersonEquals
argument_list|(
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|,
name|head
operator|.
name|getCommitterIdent
argument_list|()
argument_list|)
expr_stmt|;
name|assertSubmittedTogether
argument_list|(
name|id1
argument_list|,
name|id2
argument_list|,
name|id1
argument_list|)
expr_stmt|;
name|assertSubmittedTogether
argument_list|(
name|id2
argument_list|,
name|id2
argument_list|,
name|id1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|submitTwoChangesWithFastForward_missingDependency ()
specifier|public
name|void
name|submitTwoChangesWithFastForward_missingDependency
parameter_list|()
throws|throws
name|Exception
block|{
name|RevCommit
name|oldHead
init|=
name|getRemoteHead
argument_list|()
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change1
init|=
name|createChange
argument_list|()
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change2
init|=
name|createChange
argument_list|()
decl_stmt|;
name|Change
operator|.
name|Id
name|id1
init|=
name|change1
operator|.
name|getPatchSetId
argument_list|()
operator|.
name|getParentKey
argument_list|()
decl_stmt|;
name|submitWithConflict
argument_list|(
name|change2
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|"Failed to submit 2 changes due to the following problems:\n"
operator|+
literal|"Change "
operator|+
name|id1
operator|+
literal|": needs Code-Review"
argument_list|)
expr_stmt|;
name|RevCommit
name|head
init|=
name|getRemoteHead
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|head
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|oldHead
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|submitFastForwardNotPossible_Conflict ()
specifier|public
name|void
name|submitFastForwardNotPossible_Conflict
parameter_list|()
throws|throws
name|Exception
block|{
name|RevCommit
name|initialHead
init|=
name|getRemoteHead
argument_list|()
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change
init|=
name|createChange
argument_list|(
literal|"Change 1"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"content"
argument_list|)
decl_stmt|;
name|submit
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|RevCommit
name|oldHead
init|=
name|getRemoteHead
argument_list|()
decl_stmt|;
name|testRepo
operator|.
name|reset
argument_list|(
name|initialHead
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change2
init|=
name|createChange
argument_list|(
literal|"Change 2"
argument_list|,
literal|"b.txt"
argument_list|,
literal|"other content"
argument_list|)
decl_stmt|;
name|approve
argument_list|(
name|change2
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ActionInfo
argument_list|>
name|actions
init|=
name|getActions
argument_list|(
name|change2
operator|.
name|getChangeId
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|actions
argument_list|)
operator|.
name|containsKey
argument_list|(
literal|"submit"
argument_list|)
expr_stmt|;
name|ActionInfo
name|info
init|=
name|actions
operator|.
name|get
argument_list|(
literal|"submit"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|enabled
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|submitWithConflict
argument_list|(
name|change2
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|"Failed to submit 1 change due to the following problems:\n"
operator|+
literal|"Change "
operator|+
name|change2
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
operator|+
literal|": Project policy requires "
operator|+
literal|"all submissions to be a fast-forward. Please rebase the change "
operator|+
literal|"locally and upload again for review."
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|getRemoteHead
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|oldHead
argument_list|)
expr_stmt|;
name|assertSubmitter
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|repairChangeStateAfterFailure ()
specifier|public
name|void
name|repairChangeStateAfterFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|change
init|=
name|createChange
argument_list|(
literal|"Change 1"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"content"
argument_list|)
decl_stmt|;
name|Change
operator|.
name|Id
name|id
init|=
name|change
operator|.
name|getChange
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
name|SubmitInput
name|failAfterRefUpdates
init|=
operator|new
name|TestSubmitInput
argument_list|(
operator|new
name|SubmitInput
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|submit
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|,
name|failAfterRefUpdates
argument_list|,
name|ResourceConflictException
operator|.
name|class
argument_list|,
literal|"Failing after ref updates"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Bad: ref advanced but change wasn't updated.
name|PatchSet
operator|.
name|Id
name|psId
init|=
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|id
argument_list|,
literal|1
argument_list|)
decl_stmt|;
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
name|NEW
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|revisions
operator|.
name|get
argument_list|(
name|info
operator|.
name|currentRevision
argument_list|)
operator|.
name|_number
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|ChangeMessageInfo
name|lastMessage
init|=
name|Iterables
operator|.
name|getLast
argument_list|(
name|info
operator|.
name|messages
argument_list|)
decl_stmt|;
name|ObjectId
name|rev
decl_stmt|;
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|project
argument_list|)
init|;
name|RevWalk
name|rw
operator|=
operator|new
name|RevWalk
argument_list|(
name|repo
argument_list|)
init|)
block|{
name|rev
operator|=
name|repo
operator|.
name|exactRef
argument_list|(
name|psId
operator|.
name|toRefName
argument_list|()
argument_list|)
operator|.
name|getObjectId
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|rev
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|repo
operator|.
name|exactRef
argument_list|(
literal|"refs/heads/master"
argument_list|)
operator|.
name|getObjectId
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|rev
argument_list|)
expr_stmt|;
block|}
name|submit
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
comment|// Change status was updated, and branch tip stayed the same.
name|info
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
expr_stmt|;
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
name|assertThat
argument_list|(
name|info
operator|.
name|revisions
operator|.
name|get
argument_list|(
name|info
operator|.
name|currentRevision
argument_list|)
operator|.
name|_number
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|1
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
name|lastMessage
operator|.
name|message
argument_list|)
expr_stmt|;
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|project
argument_list|)
init|)
block|{
name|assertThat
argument_list|(
name|repo
operator|.
name|exactRef
argument_list|(
literal|"refs/heads/master"
argument_list|)
operator|.
name|getObjectId
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|rev
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

