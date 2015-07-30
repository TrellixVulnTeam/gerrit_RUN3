begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|projects
operator|.
name|BranchInput
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
DECL|class|SubmitByMergeIfNecessaryIT
specifier|public
class|class
name|SubmitByMergeIfNecessaryIT
extends|extends
name|AbstractSubmitByMerge
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
name|MERGE_IF_NECESSARY
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
name|RevCommit
name|initialHead
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
literal|"b"
argument_list|,
literal|"b"
argument_list|)
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
name|change3
init|=
name|createChange
argument_list|(
literal|"Change 3"
argument_list|,
literal|"c"
argument_list|,
literal|"c"
argument_list|)
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
name|change4
init|=
name|createChange
argument_list|(
literal|"Change 4"
argument_list|,
literal|"d"
argument_list|,
literal|"d"
argument_list|)
decl_stmt|;
comment|// Change 2 stays untouched.
name|approve
argument_list|(
name|change2
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
comment|// Change 3 is a fast-forward, no need to merge.
name|submit
argument_list|(
name|change3
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|RevCommit
name|tip
init|=
name|getRemoteLog
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tip
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|change3
operator|.
name|getCommit
argument_list|()
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tip
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
name|initialHead
operator|.
name|getId
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
name|tip
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
name|tip
operator|.
name|getCommitterIdent
argument_list|()
argument_list|)
expr_stmt|;
comment|// We need to merge change 4.
name|submit
argument_list|(
name|change4
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|tip
operator|=
name|getRemoteLog
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tip
operator|.
name|getParent
argument_list|(
literal|1
argument_list|)
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|change4
operator|.
name|getCommit
argument_list|()
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tip
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|change3
operator|.
name|getCommit
argument_list|()
operator|.
name|getShortMessage
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
name|tip
operator|.
name|getAuthorIdent
argument_list|()
argument_list|)
expr_stmt|;
name|assertPersonEquals
argument_list|(
name|serverIdent
operator|.
name|get
argument_list|()
argument_list|,
name|tip
operator|.
name|getCommitterIdent
argument_list|()
argument_list|)
expr_stmt|;
name|assertNew
argument_list|(
name|change2
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|submitChangesAcrossRepos ()
specifier|public
name|void
name|submitChangesAcrossRepos
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
literal|"project-where-we-submit"
argument_list|)
decl_stmt|;
name|Project
operator|.
name|NameKey
name|p2
init|=
name|createProject
argument_list|(
literal|"project-impacted-via-topic"
argument_list|)
decl_stmt|;
name|Project
operator|.
name|NameKey
name|p3
init|=
name|createProject
argument_list|(
literal|"project-impacted-indirectly-via-topic"
argument_list|)
decl_stmt|;
name|RevCommit
name|initialHead2
init|=
name|getRemoteHead
argument_list|(
name|p2
argument_list|,
literal|"master"
argument_list|)
decl_stmt|;
name|RevCommit
name|initialHead3
init|=
name|getRemoteHead
argument_list|(
name|p3
argument_list|,
literal|"master"
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
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo2
init|=
name|cloneProject
argument_list|(
name|p2
argument_list|)
decl_stmt|;
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo3
init|=
name|cloneProject
argument_list|(
name|p3
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change1a
init|=
name|createChange
argument_list|(
name|repo1
argument_list|,
literal|"master"
argument_list|,
literal|"An ancestor of the change we want to submit"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"1"
argument_list|,
literal|"dependent-topic"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change1b
init|=
name|createChange
argument_list|(
name|repo1
argument_list|,
literal|"master"
argument_list|,
literal|"We're interested in submitting this change"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"2"
argument_list|,
literal|"topic-to-submit"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change2a
init|=
name|createChange
argument_list|(
name|repo2
argument_list|,
literal|"master"
argument_list|,
literal|"indirection level 1"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"1"
argument_list|,
literal|"topic-indirect"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change2b
init|=
name|createChange
argument_list|(
name|repo2
argument_list|,
literal|"master"
argument_list|,
literal|"should go in with first change"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"2"
argument_list|,
literal|"dependent-topic"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change3
init|=
name|createChange
argument_list|(
name|repo3
argument_list|,
literal|"master"
argument_list|,
literal|"indirection level 2"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"1"
argument_list|,
literal|"topic-indirect"
argument_list|)
decl_stmt|;
name|approve
argument_list|(
name|change1a
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|change2a
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|change2b
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|change3
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|submit
argument_list|(
name|change1b
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|RevCommit
name|tip1
init|=
name|getRemoteLog
argument_list|(
name|p1
argument_list|,
literal|"master"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RevCommit
name|tip2
init|=
name|getRemoteLog
argument_list|(
name|p2
argument_list|,
literal|"master"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RevCommit
name|tip3
init|=
name|getRemoteLog
argument_list|(
name|p3
argument_list|,
literal|"master"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tip1
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|change1b
operator|.
name|getCommit
argument_list|()
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|isSubmitWholeTopicEnabled
argument_list|()
condition|)
block|{
name|assertThat
argument_list|(
name|tip2
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|change2b
operator|.
name|getCommit
argument_list|()
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tip3
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|change3
operator|.
name|getCommit
argument_list|()
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|tip2
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|initialHead2
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tip3
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|initialHead3
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|submitChangesAcrossReposBlocked ()
specifier|public
name|void
name|submitChangesAcrossReposBlocked
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
literal|"project-where-we-submit"
argument_list|)
decl_stmt|;
name|Project
operator|.
name|NameKey
name|p2
init|=
name|createProject
argument_list|(
literal|"project-impacted-via-topic"
argument_list|)
decl_stmt|;
name|Project
operator|.
name|NameKey
name|p3
init|=
name|createProject
argument_list|(
literal|"project-impacted-indirectly-via-topic"
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
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo2
init|=
name|cloneProject
argument_list|(
name|p2
argument_list|)
decl_stmt|;
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo3
init|=
name|cloneProject
argument_list|(
name|p3
argument_list|)
decl_stmt|;
name|RevCommit
name|initialHead1
init|=
name|getRemoteHead
argument_list|(
name|p1
argument_list|,
literal|"master"
argument_list|)
decl_stmt|;
name|RevCommit
name|initialHead2
init|=
name|getRemoteHead
argument_list|(
name|p2
argument_list|,
literal|"master"
argument_list|)
decl_stmt|;
name|RevCommit
name|initialHead3
init|=
name|getRemoteHead
argument_list|(
name|p3
argument_list|,
literal|"master"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change1a
init|=
name|createChange
argument_list|(
name|repo1
argument_list|,
literal|"master"
argument_list|,
literal|"An ancestor of the change we want to submit"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"1"
argument_list|,
literal|"dependent-topic"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change1b
init|=
name|createChange
argument_list|(
name|repo1
argument_list|,
literal|"master"
argument_list|,
literal|"we're interested to submit this change"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"2"
argument_list|,
literal|"topic-to-submit"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change2a
init|=
name|createChange
argument_list|(
name|repo2
argument_list|,
literal|"master"
argument_list|,
literal|"indirection level 2a"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"1"
argument_list|,
literal|"topic-indirect"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change2b
init|=
name|createChange
argument_list|(
name|repo2
argument_list|,
literal|"master"
argument_list|,
literal|"should go in with first change"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"2"
argument_list|,
literal|"dependent-topic"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change3
init|=
name|createChange
argument_list|(
name|repo3
argument_list|,
literal|"master"
argument_list|,
literal|"indirection level 2b"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"1"
argument_list|,
literal|"topic-indirect"
argument_list|)
decl_stmt|;
comment|// Create a merge conflict for change3 which is only indirectly related
comment|// via topics.
name|repo3
operator|.
name|reset
argument_list|(
name|initialHead3
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change3Conflict
init|=
name|createChange
argument_list|(
name|repo3
argument_list|,
literal|"master"
argument_list|,
literal|"conflicting change"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"2\n2"
argument_list|,
literal|"conflicting-topic"
argument_list|)
decl_stmt|;
name|submit
argument_list|(
name|change3Conflict
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|RevCommit
name|tipConflict
init|=
name|getRemoteLog
argument_list|(
name|p3
argument_list|,
literal|"master"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tipConflict
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|change3Conflict
operator|.
name|getCommit
argument_list|()
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|change1a
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|change2a
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|change2b
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|change3
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|isSubmitWholeTopicEnabled
argument_list|()
condition|)
block|{
name|submitWithConflict
argument_list|(
name|change1b
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|"Cannot merge "
operator|+
name|change3
operator|.
name|getCommit
argument_list|()
operator|.
name|name
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"Change could not be merged due to a path conflict.\n\n"
operator|+
literal|"Please rebase the change locally "
operator|+
literal|"and upload the rebased commit for review."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|submit
argument_list|(
name|change1b
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|RevCommit
name|tip1
init|=
name|getRemoteLog
argument_list|(
name|p1
argument_list|,
literal|"master"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RevCommit
name|tip2
init|=
name|getRemoteLog
argument_list|(
name|p2
argument_list|,
literal|"master"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RevCommit
name|tip3
init|=
name|getRemoteLog
argument_list|(
name|p3
argument_list|,
literal|"master"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|isSubmitWholeTopicEnabled
argument_list|()
condition|)
block|{
name|assertThat
argument_list|(
name|tip1
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|initialHead1
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tip2
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|initialHead2
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tip3
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|change3Conflict
operator|.
name|getCommit
argument_list|()
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertNoSubmitter
argument_list|(
name|change1a
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertNoSubmitter
argument_list|(
name|change2a
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertNoSubmitter
argument_list|(
name|change2b
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertNoSubmitter
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
else|else
block|{
name|assertThat
argument_list|(
name|tip1
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|change1b
operator|.
name|getCommit
argument_list|()
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tip2
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|initialHead2
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tip3
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|change3Conflict
operator|.
name|getCommit
argument_list|()
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertNoSubmitter
argument_list|(
name|change2a
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertNoSubmitter
argument_list|(
name|change2b
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertNoSubmitter
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
block|}
annotation|@
name|Test
DECL|method|submitWithMergedAncestorsOnOtherBranch ()
specifier|public
name|void
name|submitWithMergedAncestorsOnOtherBranch
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|change1
init|=
name|createChange
argument_list|(
name|testRepo
argument_list|,
literal|"master"
argument_list|,
literal|"base commit"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"1"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|submit
argument_list|(
name|change1
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|branch
argument_list|(
literal|"branch"
argument_list|)
operator|.
name|create
argument_list|(
operator|new
name|BranchInput
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
name|testRepo
argument_list|,
literal|"master"
argument_list|,
literal|"We want to commit this to master first"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"2"
argument_list|,
literal|""
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
name|tip1
init|=
name|getRemoteLog
argument_list|(
name|project
argument_list|,
literal|"master"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tip1
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|change2
operator|.
name|getCommit
argument_list|()
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
name|RevCommit
name|tip2
init|=
name|getRemoteLog
argument_list|(
name|project
argument_list|,
literal|"branch"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tip2
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|change1
operator|.
name|getCommit
argument_list|()
operator|.
name|getShortMessage
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
name|testRepo
argument_list|,
literal|"branch"
argument_list|,
literal|"This commit is based on master, which includes change2, "
operator|+
literal|"but is targeted at branch, which doesn't include it."
argument_list|,
literal|"a.txt"
argument_list|,
literal|"3"
argument_list|,
literal|""
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
name|List
argument_list|<
name|RevCommit
argument_list|>
name|log3
init|=
name|getRemoteLog
argument_list|(
name|project
argument_list|,
literal|"branch"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|log3
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|change3
operator|.
name|getCommit
argument_list|()
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|log3
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|change2
operator|.
name|getCommit
argument_list|()
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|submitWithOpenAncestorsOnOtherBranch ()
specifier|public
name|void
name|submitWithOpenAncestorsOnOtherBranch
parameter_list|()
throws|throws
name|Exception
block|{
name|PushOneCommit
operator|.
name|Result
name|change1
init|=
name|createChange
argument_list|(
name|testRepo
argument_list|,
literal|"master"
argument_list|,
literal|"base commit"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"1"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|submit
argument_list|(
name|change1
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|branch
argument_list|(
literal|"branch"
argument_list|)
operator|.
name|create
argument_list|(
operator|new
name|BranchInput
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
name|testRepo
argument_list|,
literal|"master"
argument_list|,
literal|"We want to commit this to master first"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"2"
argument_list|,
literal|""
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
name|RevCommit
name|tip1
init|=
name|getRemoteLog
argument_list|(
name|project
argument_list|,
literal|"master"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tip1
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|change1
operator|.
name|getCommit
argument_list|()
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
name|RevCommit
name|tip2
init|=
name|getRemoteLog
argument_list|(
name|project
argument_list|,
literal|"branch"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tip2
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|change1
operator|.
name|getCommit
argument_list|()
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change3a
init|=
name|createChange
argument_list|(
name|testRepo
argument_list|,
literal|"branch"
argument_list|,
literal|"This commit is based on change2 pending for master, "
operator|+
literal|"but is targeted itself at branch, which doesn't include it."
argument_list|,
literal|"a.txt"
argument_list|,
literal|"3"
argument_list|,
literal|"a-topic-here"
argument_list|)
decl_stmt|;
name|Project
operator|.
name|NameKey
name|p3
init|=
name|createProject
argument_list|(
literal|"project-related-to-change3"
argument_list|)
decl_stmt|;
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo3
init|=
name|cloneProject
argument_list|(
name|p3
argument_list|)
decl_stmt|;
name|RevCommit
name|initialHead
init|=
name|getRemoteHead
argument_list|(
name|p3
argument_list|,
literal|"master"
argument_list|)
decl_stmt|;
name|PushOneCommit
operator|.
name|Result
name|change3b
init|=
name|createChange
argument_list|(
name|repo3
argument_list|,
literal|"master"
argument_list|,
literal|"some accompanying changes for change3a in another repo "
operator|+
literal|"tied together via topic"
argument_list|,
literal|"a.txt"
argument_list|,
literal|"1"
argument_list|,
literal|"a-topic-here"
argument_list|)
decl_stmt|;
name|approve
argument_list|(
name|change3b
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|submitWithConflict
argument_list|(
name|change3a
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|"Merge Conflict"
argument_list|)
expr_stmt|;
name|RevCommit
name|tipbranch
init|=
name|getRemoteLog
argument_list|(
name|project
argument_list|,
literal|"branch"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tipbranch
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|change1
operator|.
name|getCommit
argument_list|()
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
name|RevCommit
name|tipmaster
init|=
name|getRemoteLog
argument_list|(
name|p3
argument_list|,
literal|"master"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|tipmaster
operator|.
name|getShortMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|initialHead
operator|.
name|getShortMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

