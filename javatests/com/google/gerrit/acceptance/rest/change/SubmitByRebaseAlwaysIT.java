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
import|import static
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
name|ListChangesOption
operator|.
name|CURRENT_REVISION
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
name|extensions
operator|.
name|client
operator|.
name|InheritableBoolean
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
name|registration
operator|.
name|DynamicSet
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
name|registration
operator|.
name|RegistrationHandle
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
name|ChangeMessageModifier
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
name|java
operator|.
name|util
operator|.
name|List
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

begin_class
DECL|class|SubmitByRebaseAlwaysIT
specifier|public
class|class
name|SubmitByRebaseAlwaysIT
extends|extends
name|AbstractSubmitByRebase
block|{
DECL|field|changeMessageModifiers
annotation|@
name|Inject
specifier|private
name|DynamicSet
argument_list|<
name|ChangeMessageModifier
argument_list|>
name|changeMessageModifiers
decl_stmt|;
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
name|REBASE_ALWAYS
return|;
block|}
annotation|@
name|Test
annotation|@
name|TestProjectInput
argument_list|(
name|useContentMerge
operator|=
name|InheritableBoolean
operator|.
name|TRUE
argument_list|)
DECL|method|submitWithPossibleFastForward ()
specifier|public
name|void
name|submitWithPossibleFastForward
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
name|isNotEqualTo
argument_list|(
name|change
operator|.
name|getCommit
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
name|assertApproved
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertCurrentRevision
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|2
argument_list|,
name|head
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
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|,
literal|2
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
name|assertRefUpdatedEvents
argument_list|(
name|oldHead
argument_list|,
name|head
argument_list|)
expr_stmt|;
name|assertChangeMergedEvents
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|,
name|head
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestProjectInput
argument_list|(
name|useContentMerge
operator|=
name|InheritableBoolean
operator|.
name|TRUE
argument_list|)
DECL|method|alwaysAddFooters ()
specifier|public
name|void
name|alwaysAddFooters
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
name|assertThat
argument_list|(
name|getCurrentCommit
argument_list|(
name|change1
argument_list|)
operator|.
name|getFooterLines
argument_list|(
name|FooterConstants
operator|.
name|REVIEWED_BY
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|getCurrentCommit
argument_list|(
name|change2
argument_list|)
operator|.
name|getFooterLines
argument_list|(
name|FooterConstants
operator|.
name|REVIEWED_BY
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
comment|// change1 is a fast-forward, but should be rebased in cherry pick style
comment|// anyway, making change2 not a fast-forward, requiring a rebase.
name|approve
argument_list|(
name|change1
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|submit
argument_list|(
name|change2
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
comment|// ... but both changes should get reviewed-by footers.
name|assertLatestRevisionHasFooters
argument_list|(
name|change1
argument_list|)
expr_stmt|;
name|assertLatestRevisionHasFooters
argument_list|(
name|change2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestProjectInput
argument_list|(
name|useContentMerge
operator|=
name|InheritableBoolean
operator|.
name|TRUE
argument_list|)
DECL|method|changeMessageOnSubmit ()
specifier|public
name|void
name|changeMessageOnSubmit
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
name|RegistrationHandle
name|handle
init|=
name|changeMessageModifiers
operator|.
name|add
argument_list|(
literal|"gerrit"
argument_list|,
parameter_list|(
name|newCommitMessage
parameter_list|,
name|original
parameter_list|,
name|mergeTip
parameter_list|,
name|destination
parameter_list|)
lambda|->
block|{
name|List
argument_list|<
name|String
argument_list|>
name|custom
init|=
name|mergeTip
operator|.
name|getFooterLines
argument_list|(
literal|"Custom"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|custom
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|newCommitMessage
operator|+=
literal|"Custom-Parent: "
operator|+
name|custom
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|+
literal|"\n"
expr_stmt|;
block|}
return|return
name|newCommitMessage
operator|+
literal|"Custom: "
operator|+
name|destination
operator|.
name|get
argument_list|()
return|;
block|}
argument_list|)
decl_stmt|;
try|try
block|{
comment|// change1 is a fast-forward, but should be rebased in cherry pick style
comment|// anyway, making change2 not a fast-forward, requiring a rebase.
name|approve
argument_list|(
name|change1
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
name|submit
argument_list|(
name|change2
operator|.
name|getChangeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|handle
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
comment|// ... but both changes should get custom footers.
name|assertThat
argument_list|(
name|getCurrentCommit
argument_list|(
name|change1
argument_list|)
operator|.
name|getFooterLines
argument_list|(
literal|"Custom"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"refs/heads/master"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|getCurrentCommit
argument_list|(
name|change2
argument_list|)
operator|.
name|getFooterLines
argument_list|(
literal|"Custom"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"refs/heads/master"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|getCurrentCommit
argument_list|(
name|change2
argument_list|)
operator|.
name|getFooterLines
argument_list|(
literal|"Custom-Parent"
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
literal|"refs/heads/master"
argument_list|)
expr_stmt|;
block|}
DECL|method|assertLatestRevisionHasFooters (PushOneCommit.Result change)
specifier|private
name|void
name|assertLatestRevisionHasFooters
parameter_list|(
name|PushOneCommit
operator|.
name|Result
name|change
parameter_list|)
throws|throws
name|Exception
block|{
name|RevCommit
name|c
init|=
name|getCurrentCommit
argument_list|(
name|change
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|getFooterLines
argument_list|(
name|FooterConstants
operator|.
name|CHANGE_ID
argument_list|)
argument_list|)
operator|.
name|isNotEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|getFooterLines
argument_list|(
name|FooterConstants
operator|.
name|REVIEWED_BY
argument_list|)
argument_list|)
operator|.
name|isNotEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|getFooterLines
argument_list|(
name|FooterConstants
operator|.
name|REVIEWED_ON
argument_list|)
argument_list|)
operator|.
name|isNotEmpty
argument_list|()
expr_stmt|;
block|}
DECL|method|getCurrentCommit (PushOneCommit.Result change)
specifier|private
name|RevCommit
name|getCurrentCommit
parameter_list|(
name|PushOneCommit
operator|.
name|Result
name|change
parameter_list|)
throws|throws
name|Exception
block|{
name|testRepo
operator|.
name|git
argument_list|()
operator|.
name|fetch
argument_list|()
operator|.
name|setRemote
argument_list|(
literal|"origin"
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
name|ChangeInfo
name|info
init|=
name|get
argument_list|(
name|change
operator|.
name|getChangeId
argument_list|()
argument_list|,
name|CURRENT_REVISION
argument_list|)
decl_stmt|;
name|RevCommit
name|c
init|=
name|testRepo
operator|.
name|getRevWalk
argument_list|()
operator|.
name|parseCommit
argument_list|(
name|ObjectId
operator|.
name|fromString
argument_list|(
name|info
operator|.
name|currentRevision
argument_list|)
argument_list|)
decl_stmt|;
name|testRepo
operator|.
name|getRevWalk
argument_list|()
operator|.
name|parseBody
argument_list|(
name|c
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
block|}
end_class

end_unit

