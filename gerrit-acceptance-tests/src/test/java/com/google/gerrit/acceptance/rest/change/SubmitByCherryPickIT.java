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
name|gerrit
operator|.
name|acceptance
operator|.
name|GitUtil
operator|.
name|checkout
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertSame
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
name|common
operator|.
name|SubmitType
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
name|api
operator|.
name|Git
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
name|List
import|;
end_import

begin_class
DECL|class|SubmitByCherryPickIT
specifier|public
class|class
name|SubmitByCherryPickIT
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
name|CHERRY_PICK
return|;
block|}
annotation|@
name|Test
DECL|method|submitWithCherryPickIfFastForwardPossible ()
specifier|public
name|void
name|submitWithCherryPickIfFastForwardPossible
parameter_list|()
throws|throws
name|Exception
block|{
name|Git
name|git
init|=
name|createProject
argument_list|()
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change
init|=
name|createChange
argument_list|(
name|git
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
name|assertCherryPick
argument_list|(
name|git
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|change
operator|.
name|getCommit
argument_list|()
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
argument_list|,
name|getRemoteHead
argument_list|()
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|submitWithCherryPick ()
specifier|public
name|void
name|submitWithCherryPick
parameter_list|()
throws|throws
name|Exception
block|{
name|Git
name|git
init|=
name|createProject
argument_list|()
decl_stmt|;
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
name|git
argument_list|,
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
name|checkout
argument_list|(
name|git
argument_list|,
name|initialHead
operator|.
name|getId
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change2
init|=
name|createChange
argument_list|(
name|git
argument_list|,
literal|"Change 2"
argument_list|,
literal|"b.txt"
argument_list|,
literal|"other content"
argument_list|)
decl_stmt|;
name|submit
argument_list|(
name|change2
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertCherryPick
argument_list|(
name|git
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|RevCommit
name|newHead
init|=
name|getRemoteHead
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|newHead
operator|.
name|getParentCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldHead
argument_list|,
name|newHead
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertCurrentRevision
argument_list|(
name|change2
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|2
argument_list|,
name|newHead
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
name|assertSubmitter
argument_list|(
name|change2
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|submitWithContentMerge ()
specifier|public
name|void
name|submitWithContentMerge
parameter_list|()
throws|throws
name|Exception
block|{
name|Git
name|git
init|=
name|createProject
argument_list|()
decl_stmt|;
name|setUseContentMerge
argument_list|()
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change
init|=
name|createChange
argument_list|(
name|git
argument_list|,
literal|"Change 1"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"aaa\nbbb\nccc\n"
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
name|PushOneCommit
operator|.
name|Result
name|change2
init|=
name|createChange
argument_list|(
name|git
argument_list|,
literal|"Change 2"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"aaa\nbbb\nccc\nddd\n"
argument_list|)
decl_stmt|;
name|submit
argument_list|(
name|change2
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
name|checkout
argument_list|(
name|git
argument_list|,
name|change
operator|.
name|getCommitId
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change3
init|=
name|createChange
argument_list|(
name|git
argument_list|,
literal|"Change 3"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"bbb\nccc\n"
argument_list|)
decl_stmt|;
name|submit
argument_list|(
name|change3
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertCherryPick
argument_list|(
name|git
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|RevCommit
name|newHead
init|=
name|getRemoteHead
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|oldHead
argument_list|,
name|newHead
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertApproved
argument_list|(
name|change3
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertCurrentRevision
argument_list|(
name|change3
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|2
argument_list|,
name|newHead
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
name|assertSubmitter
argument_list|(
name|change2
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|submitWithContentMerge_Conflict ()
specifier|public
name|void
name|submitWithContentMerge_Conflict
parameter_list|()
throws|throws
name|Exception
block|{
name|Git
name|git
init|=
name|createProject
argument_list|()
decl_stmt|;
name|setUseContentMerge
argument_list|()
expr_stmt|;
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
name|git
argument_list|,
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
name|checkout
argument_list|(
name|git
argument_list|,
name|initialHead
operator|.
name|getId
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change2
init|=
name|createChange
argument_list|(
name|git
argument_list|,
literal|"Change 2"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"other content"
argument_list|)
decl_stmt|;
name|submitWithConflict
argument_list|(
name|change2
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldHead
argument_list|,
name|getRemoteHead
argument_list|()
argument_list|)
expr_stmt|;
name|assertCurrentRevision
argument_list|(
name|change2
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|1
argument_list|,
name|change2
operator|.
name|getCommitId
argument_list|()
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
block|}
annotation|@
name|Test
DECL|method|submitOutOfOrder ()
specifier|public
name|void
name|submitOutOfOrder
parameter_list|()
throws|throws
name|Exception
block|{
name|Git
name|git
init|=
name|createProject
argument_list|()
decl_stmt|;
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
name|git
argument_list|,
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
name|checkout
argument_list|(
name|git
argument_list|,
name|initialHead
operator|.
name|getId
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|createChange
argument_list|(
name|git
argument_list|,
literal|"Change 2"
argument_list|,
literal|"b.txt"
argument_list|,
literal|"other content"
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change3
init|=
name|createChange
argument_list|(
name|git
argument_list|,
literal|"Change 3"
argument_list|,
literal|"c.txt"
argument_list|,
literal|"different content"
argument_list|)
decl_stmt|;
name|submit
argument_list|(
name|change3
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertCherryPick
argument_list|(
name|git
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|RevCommit
name|newHead
init|=
name|getRemoteHead
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|oldHead
argument_list|,
name|newHead
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertApproved
argument_list|(
name|change3
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertCurrentRevision
argument_list|(
name|change3
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|2
argument_list|,
name|newHead
argument_list|)
expr_stmt|;
name|assertSubmitter
argument_list|(
name|change3
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertSubmitter
argument_list|(
name|change3
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|submitOutOfOrder_Conflict ()
specifier|public
name|void
name|submitOutOfOrder_Conflict
parameter_list|()
throws|throws
name|Exception
block|{
name|Git
name|git
init|=
name|createProject
argument_list|()
decl_stmt|;
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
name|git
argument_list|,
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
name|checkout
argument_list|(
name|git
argument_list|,
name|initialHead
operator|.
name|getId
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|createChange
argument_list|(
name|git
argument_list|,
literal|"Change 2"
argument_list|,
literal|"b.txt"
argument_list|,
literal|"other content"
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change3
init|=
name|createChange
argument_list|(
name|git
argument_list|,
literal|"Change 3"
argument_list|,
literal|"b.txt"
argument_list|,
literal|"different content"
argument_list|)
decl_stmt|;
name|submitWithConflict
argument_list|(
name|change3
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|oldHead
argument_list|,
name|getRemoteHead
argument_list|()
argument_list|)
expr_stmt|;
name|assertCurrentRevision
argument_list|(
name|change3
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|1
argument_list|,
name|change3
operator|.
name|getCommitId
argument_list|()
argument_list|)
expr_stmt|;
name|assertSubmitter
argument_list|(
name|change3
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
DECL|method|submitMultipleChanges ()
specifier|public
name|void
name|submitMultipleChanges
parameter_list|()
throws|throws
name|Exception
block|{
name|Git
name|git
init|=
name|createProject
argument_list|()
decl_stmt|;
name|RevCommit
name|initialHead
init|=
name|getRemoteHead
argument_list|()
decl_stmt|;
name|checkout
argument_list|(
name|git
argument_list|,
name|initialHead
operator|.
name|getId
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change2
init|=
name|createChange
argument_list|(
name|git
argument_list|,
literal|"Change 2"
argument_list|,
literal|"b"
argument_list|,
literal|"b"
argument_list|)
decl_stmt|;
name|checkout
argument_list|(
name|git
argument_list|,
name|initialHead
operator|.
name|getId
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change3
init|=
name|createChange
argument_list|(
name|git
argument_list|,
literal|"Change 3"
argument_list|,
literal|"c"
argument_list|,
literal|"c"
argument_list|)
decl_stmt|;
name|checkout
argument_list|(
name|git
argument_list|,
name|initialHead
operator|.
name|getId
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change4
init|=
name|createChange
argument_list|(
name|git
argument_list|,
literal|"Change 4"
argument_list|,
literal|"d"
argument_list|,
literal|"d"
argument_list|)
decl_stmt|;
name|submitStatusOnly
argument_list|(
name|change2
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|submitStatusOnly
argument_list|(
name|change3
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|submit
argument_list|(
name|change4
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RevCommit
argument_list|>
name|log
init|=
name|getRemoteLog
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|change4
operator|.
name|getCommit
argument_list|()
operator|.
name|getShortMessage
argument_list|()
argument_list|,
name|log
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|log
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|log
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|change3
operator|.
name|getCommit
argument_list|()
operator|.
name|getShortMessage
argument_list|()
argument_list|,
name|log
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|log
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
name|log
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|change2
operator|.
name|getCommit
argument_list|()
operator|.
name|getShortMessage
argument_list|()
argument_list|,
name|log
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|log
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|,
name|log
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|initialHead
operator|.
name|getId
argument_list|()
argument_list|,
name|log
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

