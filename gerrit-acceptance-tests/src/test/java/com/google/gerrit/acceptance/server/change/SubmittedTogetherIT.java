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
DECL|package|com.google.gerrit.acceptance.server.change
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
name|pushHead
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
name|GitUtil
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
name|TestProjectInput
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
name|reviewdb
operator|.
name|client
operator|.
name|Project
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
name|junit
operator|.
name|TestRepository
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
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|SubmittedTogetherIT
specifier|public
class|class
name|SubmittedTogetherIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Test
DECL|method|returnsAncestors ()
specifier|public
name|void
name|returnsAncestors
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create two commits and push.
name|RevCommit
name|c1_1
init|=
name|commitBuilder
argument_list|()
operator|.
name|add
argument_list|(
literal|"a.txt"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|message
argument_list|(
literal|"subject: 1"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|String
name|id1
init|=
name|getChangeId
argument_list|(
name|c1_1
argument_list|)
decl_stmt|;
name|RevCommit
name|c2_1
init|=
name|commitBuilder
argument_list|()
operator|.
name|add
argument_list|(
literal|"b.txt"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|message
argument_list|(
literal|"subject: 2"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|String
name|id2
init|=
name|getChangeId
argument_list|(
name|c2_1
argument_list|)
decl_stmt|;
name|pushHead
argument_list|(
name|testRepo
argument_list|,
literal|"refs/for/master"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSubmittedTogether
argument_list|(
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
DECL|method|respectsWholeTopicAndAncestors ()
specifier|public
name|void
name|respectsWholeTopicAndAncestors
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
comment|// Create two independent commits and push.
name|RevCommit
name|c1_1
init|=
name|commitBuilder
argument_list|()
operator|.
name|add
argument_list|(
literal|"a.txt"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|message
argument_list|(
literal|"subject: 1"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|String
name|id1
init|=
name|getChangeId
argument_list|(
name|c1_1
argument_list|)
decl_stmt|;
name|pushHead
argument_list|(
name|testRepo
argument_list|,
literal|"refs/for/master/"
operator|+
name|name
argument_list|(
literal|"connectingTopic"
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testRepo
operator|.
name|reset
argument_list|(
name|initialHead
argument_list|)
expr_stmt|;
name|RevCommit
name|c2_1
init|=
name|commitBuilder
argument_list|()
operator|.
name|add
argument_list|(
literal|"b.txt"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|message
argument_list|(
literal|"subject: 2"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|String
name|id2
init|=
name|getChangeId
argument_list|(
name|c2_1
argument_list|)
decl_stmt|;
name|pushHead
argument_list|(
name|testRepo
argument_list|,
literal|"refs/for/master/"
operator|+
name|name
argument_list|(
literal|"connectingTopic"
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|isSubmitWholeTopicEnabled
argument_list|()
condition|)
block|{
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
else|else
block|{
name|assertSubmittedTogether
argument_list|(
name|id1
argument_list|)
expr_stmt|;
name|assertSubmittedTogether
argument_list|(
name|id2
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testTopicChaining ()
specifier|public
name|void
name|testTopicChaining
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
comment|// Create two independent commits and push.
name|RevCommit
name|c1_1
init|=
name|commitBuilder
argument_list|()
operator|.
name|add
argument_list|(
literal|"a.txt"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|message
argument_list|(
literal|"subject: 1"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|String
name|id1
init|=
name|getChangeId
argument_list|(
name|c1_1
argument_list|)
decl_stmt|;
name|pushHead
argument_list|(
name|testRepo
argument_list|,
literal|"refs/for/master/"
operator|+
name|name
argument_list|(
literal|"connectingTopic"
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testRepo
operator|.
name|reset
argument_list|(
name|initialHead
argument_list|)
expr_stmt|;
name|RevCommit
name|c2_1
init|=
name|commitBuilder
argument_list|()
operator|.
name|add
argument_list|(
literal|"b.txt"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|message
argument_list|(
literal|"subject: 2"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|String
name|id2
init|=
name|getChangeId
argument_list|(
name|c2_1
argument_list|)
decl_stmt|;
name|pushHead
argument_list|(
name|testRepo
argument_list|,
literal|"refs/for/master/"
operator|+
name|name
argument_list|(
literal|"connectingTopic"
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|RevCommit
name|c3_1
init|=
name|commitBuilder
argument_list|()
operator|.
name|add
argument_list|(
literal|"b.txt"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|message
argument_list|(
literal|"subject: 2"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|String
name|id3
init|=
name|getChangeId
argument_list|(
name|c3_1
argument_list|)
decl_stmt|;
name|pushHead
argument_list|(
name|testRepo
argument_list|,
literal|"refs/for/master/"
operator|+
name|name
argument_list|(
literal|"unrelated-topic"
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|isSubmitWholeTopicEnabled
argument_list|()
condition|)
block|{
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
name|assertSubmittedTogether
argument_list|(
name|id3
argument_list|,
name|id3
argument_list|,
name|id2
argument_list|,
name|id1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertSubmittedTogether
argument_list|(
name|id1
argument_list|)
expr_stmt|;
name|assertSubmittedTogether
argument_list|(
name|id2
argument_list|)
expr_stmt|;
name|assertSubmittedTogether
argument_list|(
name|id3
argument_list|,
name|id3
argument_list|,
name|id2
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNewBranchTwoChangesTogether ()
specifier|public
name|void
name|testNewBranchTwoChangesTogether
parameter_list|()
throws|throws
name|Exception
block|{
name|Project
operator|.
name|NameKey
name|p1
init|=
name|createProject
argument_list|(
literal|"a-new-project"
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo1
init|=
name|cloneProject
argument_list|(
name|p1
argument_list|)
decl_stmt|;
name|RevCommit
name|c1
init|=
name|repo1
operator|.
name|branch
argument_list|(
literal|"HEAD"
argument_list|)
operator|.
name|commit
argument_list|()
operator|.
name|insertChangeId
argument_list|()
operator|.
name|add
argument_list|(
literal|"a.txt"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|message
argument_list|(
literal|"subject: 1"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|String
name|id1
init|=
name|GitUtil
operator|.
name|getChangeId
argument_list|(
name|repo1
argument_list|,
name|c1
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|pushHead
argument_list|(
name|repo1
argument_list|,
literal|"refs/for/master"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|RevCommit
name|c2
init|=
name|repo1
operator|.
name|branch
argument_list|(
literal|"HEAD"
argument_list|)
operator|.
name|commit
argument_list|()
operator|.
name|insertChangeId
argument_list|()
operator|.
name|add
argument_list|(
literal|"b.txt"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|message
argument_list|(
literal|"subject: 2"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|String
name|id2
init|=
name|GitUtil
operator|.
name|getChangeId
argument_list|(
name|repo1
argument_list|,
name|c2
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|pushHead
argument_list|(
name|repo1
argument_list|,
literal|"refs/for/master"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSubmittedTogether
argument_list|(
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
annotation|@
name|TestProjectInput
argument_list|(
name|submitType
operator|=
name|SubmitType
operator|.
name|CHERRY_PICK
argument_list|)
DECL|method|testCherryPickWithoutAncestors ()
specifier|public
name|void
name|testCherryPickWithoutAncestors
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create two commits and push.
name|RevCommit
name|c1_1
init|=
name|commitBuilder
argument_list|()
operator|.
name|add
argument_list|(
literal|"a.txt"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|message
argument_list|(
literal|"subject: 1"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|String
name|id1
init|=
name|getChangeId
argument_list|(
name|c1_1
argument_list|)
decl_stmt|;
name|RevCommit
name|c2_1
init|=
name|commitBuilder
argument_list|()
operator|.
name|add
argument_list|(
literal|"b.txt"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|message
argument_list|(
literal|"subject: 2"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|String
name|id2
init|=
name|getChangeId
argument_list|(
name|c2_1
argument_list|)
decl_stmt|;
name|pushHead
argument_list|(
name|testRepo
argument_list|,
literal|"refs/for/master"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSubmittedTogether
argument_list|(
name|id1
argument_list|)
expr_stmt|;
name|assertSubmittedTogether
argument_list|(
name|id2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubmissionIdSavedOnMergeInOneProject ()
specifier|public
name|void
name|testSubmissionIdSavedOnMergeInOneProject
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Create two commits and push.
name|RevCommit
name|c1_1
init|=
name|commitBuilder
argument_list|()
operator|.
name|add
argument_list|(
literal|"a.txt"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|message
argument_list|(
literal|"subject: 1"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|String
name|id1
init|=
name|getChangeId
argument_list|(
name|c1_1
argument_list|)
decl_stmt|;
name|RevCommit
name|c2_1
init|=
name|commitBuilder
argument_list|()
operator|.
name|add
argument_list|(
literal|"b.txt"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|message
argument_list|(
literal|"subject: 2"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|String
name|id2
init|=
name|getChangeId
argument_list|(
name|c2_1
argument_list|)
decl_stmt|;
name|pushHead
argument_list|(
name|testRepo
argument_list|,
literal|"refs/for/master"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSubmittedTogether
argument_list|(
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
name|approve
argument_list|(
name|id1
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|id2
argument_list|)
expr_stmt|;
name|submit
argument_list|(
name|id2
argument_list|)
expr_stmt|;
name|assertMerged
argument_list|(
name|id1
argument_list|)
expr_stmt|;
name|assertMerged
argument_list|(
name|id2
argument_list|)
expr_stmt|;
comment|// Prior to submission this was empty, but the post-merge value is what was
comment|// actually submitted.
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
DECL|method|getRemoteHead ()
specifier|private
name|RevCommit
name|getRemoteHead
parameter_list|()
throws|throws
name|IOException
block|{
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
return|return
name|rw
operator|.
name|parseCommit
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
return|;
block|}
block|}
DECL|method|getChangeId (RevCommit c)
specifier|private
name|String
name|getChangeId
parameter_list|(
name|RevCommit
name|c
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|GitUtil
operator|.
name|getChangeId
argument_list|(
name|testRepo
argument_list|,
name|c
argument_list|)
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|submit (String changeId)
specifier|private
name|void
name|submit
parameter_list|(
name|String
name|changeId
parameter_list|)
throws|throws
name|Exception
block|{
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|changeId
argument_list|)
operator|.
name|current
argument_list|()
operator|.
name|submit
argument_list|()
expr_stmt|;
block|}
DECL|method|assertMerged (String changeId)
specifier|private
name|void
name|assertMerged
parameter_list|(
name|String
name|changeId
parameter_list|)
throws|throws
name|Exception
block|{
name|assertThat
argument_list|(
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|changeId
argument_list|)
operator|.
name|get
argument_list|()
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
block|}
block|}
end_class

end_unit

