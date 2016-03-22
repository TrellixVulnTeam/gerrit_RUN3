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
DECL|package|com.google.gerrit.acceptance.git
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
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
name|GerritConfig
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
name|eclipse
operator|.
name|jgit
operator|.
name|revwalk
operator|.
name|RevTree
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
name|transport
operator|.
name|RefSpec
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
DECL|class|SubmoduleSubscriptionsIT
specifier|public
class|class
name|SubmoduleSubscriptionsIT
extends|extends
name|AbstractSubmoduleSubscription
block|{
annotation|@
name|Test
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"submodule.enableSuperProjectSubscriptions"
argument_list|,
name|value
operator|=
literal|"false"
argument_list|)
DECL|method|testSubscriptionWithoutServerSetting ()
specifier|public
name|void
name|testSubscriptionWithoutServerSetting
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRepository
argument_list|<
name|?
argument_list|>
name|superRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"super-project"
argument_list|)
decl_stmt|;
name|TestRepository
argument_list|<
name|?
argument_list|>
name|subRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"subscribed-to-project"
argument_list|)
decl_stmt|;
name|createSubmoduleSubscription
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hasSubmodule
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|)
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubscriptionToEmptyRepo ()
specifier|public
name|void
name|testSubscriptionToEmptyRepo
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRepository
argument_list|<
name|?
argument_list|>
name|superRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"super-project"
argument_list|)
decl_stmt|;
name|TestRepository
argument_list|<
name|?
argument_list|>
name|subRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"subscribed-to-project"
argument_list|)
decl_stmt|;
name|allowSubmoduleSubscription
argument_list|(
literal|"subscribed-to-project"
argument_list|,
literal|"refs/heads/master"
argument_list|,
literal|"super-project"
argument_list|,
literal|"refs/heads/master"
argument_list|)
expr_stmt|;
name|createSubmoduleSubscription
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|ObjectId
name|subHEAD
init|=
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
decl_stmt|;
name|expectToHaveSubmoduleState
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
name|subHEAD
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubscriptionToExistingRepo ()
specifier|public
name|void
name|testSubscriptionToExistingRepo
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRepository
argument_list|<
name|?
argument_list|>
name|superRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"super-project"
argument_list|)
decl_stmt|;
name|TestRepository
argument_list|<
name|?
argument_list|>
name|subRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"subscribed-to-project"
argument_list|)
decl_stmt|;
name|allowSubmoduleSubscription
argument_list|(
literal|"subscribed-to-project"
argument_list|,
literal|"refs/heads/master"
argument_list|,
literal|"super-project"
argument_list|,
literal|"refs/heads/master"
argument_list|)
expr_stmt|;
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|createSubmoduleSubscription
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|ObjectId
name|subHEAD
init|=
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
decl_stmt|;
name|expectToHaveSubmoduleState
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
name|subHEAD
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubscriptionWildcardACLForSingleBranch ()
specifier|public
name|void
name|testSubscriptionWildcardACLForSingleBranch
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRepository
argument_list|<
name|?
argument_list|>
name|superRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"super-project"
argument_list|)
decl_stmt|;
name|TestRepository
argument_list|<
name|?
argument_list|>
name|subRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"subscribed-to-project"
argument_list|)
decl_stmt|;
comment|// master is allowed to be subscribed to any superprojects branch:
name|allowSubmoduleSubscription
argument_list|(
literal|"subscribed-to-project"
argument_list|,
literal|"refs/heads/master"
argument_list|,
literal|"super-project"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// create 'branch':
name|pushChangeTo
argument_list|(
name|superRepo
argument_list|,
literal|"branch"
argument_list|)
expr_stmt|;
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|createSubmoduleSubscription
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|createSubmoduleSubscription
argument_list|(
name|superRepo
argument_list|,
literal|"branch"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|ObjectId
name|subHEAD
init|=
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
decl_stmt|;
name|expectToHaveSubmoduleState
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
name|subHEAD
argument_list|)
expr_stmt|;
name|expectToHaveSubmoduleState
argument_list|(
name|superRepo
argument_list|,
literal|"branch"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
name|subHEAD
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubscriptionWildcardACLOneOnOneMapping ()
specifier|public
name|void
name|testSubscriptionWildcardACLOneOnOneMapping
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRepository
argument_list|<
name|?
argument_list|>
name|superRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"super-project"
argument_list|)
decl_stmt|;
name|TestRepository
argument_list|<
name|?
argument_list|>
name|subRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"subscribed-to-project"
argument_list|)
decl_stmt|;
comment|// any branch is allowed to be subscribed to the same superprojects branch:
name|allowSubmoduleSubscription
argument_list|(
literal|"subscribed-to-project"
argument_list|,
literal|"refs/heads/*"
argument_list|,
literal|"super-project"
argument_list|,
literal|"refs/heads/*"
argument_list|)
expr_stmt|;
comment|// create 'branch' in both repos:
name|pushChangeTo
argument_list|(
name|superRepo
argument_list|,
literal|"branch"
argument_list|)
expr_stmt|;
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"branch"
argument_list|)
expr_stmt|;
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|createSubmoduleSubscription
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|createSubmoduleSubscription
argument_list|(
name|superRepo
argument_list|,
literal|"branch"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
literal|"branch"
argument_list|)
expr_stmt|;
name|ObjectId
name|subHEAD1
init|=
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
decl_stmt|;
name|ObjectId
name|subHEAD2
init|=
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"branch"
argument_list|)
decl_stmt|;
name|expectToHaveSubmoduleState
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
name|subHEAD1
argument_list|)
expr_stmt|;
name|expectToHaveSubmoduleState
argument_list|(
name|superRepo
argument_list|,
literal|"branch"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
name|subHEAD2
argument_list|)
expr_stmt|;
comment|// Now test that cross subscriptions do not work:
name|createSubmoduleSubscription
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
literal|"branch"
argument_list|)
expr_stmt|;
name|ObjectId
name|subHEAD3
init|=
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"branch"
argument_list|)
decl_stmt|;
name|expectToHaveSubmoduleState
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
name|subHEAD1
argument_list|)
expr_stmt|;
name|expectToHaveSubmoduleState
argument_list|(
name|superRepo
argument_list|,
literal|"branch"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
name|subHEAD3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"submodule.verboseSuperprojectUpdate"
argument_list|,
name|value
operator|=
literal|"false"
argument_list|)
DECL|method|testSubmoduleShortCommitMessage ()
specifier|public
name|void
name|testSubmoduleShortCommitMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRepository
argument_list|<
name|?
argument_list|>
name|superRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"super-project"
argument_list|)
decl_stmt|;
name|TestRepository
argument_list|<
name|?
argument_list|>
name|subRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"subscribed-to-project"
argument_list|)
decl_stmt|;
name|allowSubmoduleSubscription
argument_list|(
literal|"subscribed-to-project"
argument_list|,
literal|"refs/heads/master"
argument_list|,
literal|"super-project"
argument_list|,
literal|"refs/heads/master"
argument_list|)
expr_stmt|;
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|createSubmoduleSubscription
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
comment|// The first update doesn't include any commit messages
name|ObjectId
name|subRepoId
init|=
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
decl_stmt|;
name|expectToHaveSubmoduleState
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
name|subRepoId
argument_list|)
expr_stmt|;
name|expectToHaveCommitMessage
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"Update git submodules\n\n"
argument_list|)
expr_stmt|;
comment|// Any following update also has a short message
name|subRepoId
operator|=
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|expectToHaveSubmoduleState
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
name|subRepoId
argument_list|)
expr_stmt|;
name|expectToHaveCommitMessage
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"Update git submodules\n\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubmoduleCommitMessage ()
specifier|public
name|void
name|testSubmoduleCommitMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRepository
argument_list|<
name|?
argument_list|>
name|superRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"super-project"
argument_list|)
decl_stmt|;
name|TestRepository
argument_list|<
name|?
argument_list|>
name|subRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"subscribed-to-project"
argument_list|)
decl_stmt|;
name|allowSubmoduleSubscription
argument_list|(
literal|"subscribed-to-project"
argument_list|,
literal|"refs/heads/master"
argument_list|,
literal|"super-project"
argument_list|,
literal|"refs/heads/master"
argument_list|)
expr_stmt|;
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|createSubmoduleSubscription
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|ObjectId
name|subHEAD
init|=
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
decl_stmt|;
comment|// The first update doesn't include the rev log
name|RevWalk
name|rw
init|=
name|subRepo
operator|.
name|getRevWalk
argument_list|()
decl_stmt|;
name|RevCommit
name|subCommitMsg
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|subHEAD
argument_list|)
decl_stmt|;
name|expectToHaveCommitMessage
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"Update git submodules\n\n"
operator|+
literal|"Project: "
operator|+
name|name
argument_list|(
literal|"subscribed-to-project"
argument_list|)
operator|+
literal|" master "
operator|+
name|subHEAD
operator|.
name|name
argument_list|()
operator|+
literal|"\n\n"
argument_list|)
expr_stmt|;
comment|// The next commit should generate only its commit message,
comment|// omitting previous commit logs
name|subHEAD
operator|=
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|subCommitMsg
operator|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|subHEAD
argument_list|)
expr_stmt|;
name|expectToHaveCommitMessage
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"Update git submodules\n\n"
operator|+
literal|"Project: "
operator|+
name|name
argument_list|(
literal|"subscribed-to-project"
argument_list|)
operator|+
literal|" master "
operator|+
name|subHEAD
operator|.
name|name
argument_list|()
operator|+
literal|"\n\n"
operator|+
name|subCommitMsg
operator|.
name|getFullMessage
argument_list|()
operator|+
literal|"\n\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubscriptionUnsubscribe ()
specifier|public
name|void
name|testSubscriptionUnsubscribe
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRepository
argument_list|<
name|?
argument_list|>
name|superRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"super-project"
argument_list|)
decl_stmt|;
name|TestRepository
argument_list|<
name|?
argument_list|>
name|subRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"subscribed-to-project"
argument_list|)
decl_stmt|;
name|allowSubmoduleSubscription
argument_list|(
literal|"subscribed-to-project"
argument_list|,
literal|"refs/heads/master"
argument_list|,
literal|"super-project"
argument_list|,
literal|"refs/heads/master"
argument_list|)
expr_stmt|;
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|createSubmoduleSubscription
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|ObjectId
name|subHEADbeforeUnsubscribing
init|=
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
decl_stmt|;
name|deleteAllSubscriptions
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|expectToHaveSubmoduleState
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
name|subHEADbeforeUnsubscribing
argument_list|)
expr_stmt|;
name|pushChangeTo
argument_list|(
name|superRepo
argument_list|,
literal|"refs/heads/master"
argument_list|,
literal|"commit after unsubscribe"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"refs/heads/master"
argument_list|,
literal|"commit after unsubscribe"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|expectToHaveSubmoduleState
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
name|subHEADbeforeUnsubscribing
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubscriptionUnsubscribeByDeletingGitModules ()
specifier|public
name|void
name|testSubscriptionUnsubscribeByDeletingGitModules
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRepository
argument_list|<
name|?
argument_list|>
name|superRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"super-project"
argument_list|)
decl_stmt|;
name|TestRepository
argument_list|<
name|?
argument_list|>
name|subRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"subscribed-to-project"
argument_list|)
decl_stmt|;
name|allowSubmoduleSubscription
argument_list|(
literal|"subscribed-to-project"
argument_list|,
literal|"refs/heads/master"
argument_list|,
literal|"super-project"
argument_list|,
literal|"refs/heads/master"
argument_list|)
expr_stmt|;
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|createSubmoduleSubscription
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|ObjectId
name|subHEADbeforeUnsubscribing
init|=
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
decl_stmt|;
name|deleteGitModulesFile
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|expectToHaveSubmoduleState
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
name|subHEADbeforeUnsubscribing
argument_list|)
expr_stmt|;
name|pushChangeTo
argument_list|(
name|superRepo
argument_list|,
literal|"refs/heads/master"
argument_list|,
literal|"commit after unsubscribe"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"refs/heads/master"
argument_list|,
literal|"commit after unsubscribe"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|expectToHaveSubmoduleState
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
name|subHEADbeforeUnsubscribing
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubscriptionToDifferentBranches ()
specifier|public
name|void
name|testSubscriptionToDifferentBranches
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRepository
argument_list|<
name|?
argument_list|>
name|superRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"super-project"
argument_list|)
decl_stmt|;
name|TestRepository
argument_list|<
name|?
argument_list|>
name|subRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"subscribed-to-project"
argument_list|)
decl_stmt|;
name|allowSubmoduleSubscription
argument_list|(
literal|"subscribed-to-project"
argument_list|,
literal|"refs/heads/foo"
argument_list|,
literal|"super-project"
argument_list|,
literal|"refs/heads/master"
argument_list|)
expr_stmt|;
name|createSubmoduleSubscription
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|ObjectId
name|subFoo
init|=
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|expectToHaveSubmoduleState
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
name|subFoo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCircularSubscriptionIsDetected ()
specifier|public
name|void
name|testCircularSubscriptionIsDetected
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRepository
argument_list|<
name|?
argument_list|>
name|superRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"super-project"
argument_list|)
decl_stmt|;
name|TestRepository
argument_list|<
name|?
argument_list|>
name|subRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"subscribed-to-project"
argument_list|)
decl_stmt|;
name|allowSubmoduleSubscription
argument_list|(
literal|"subscribed-to-project"
argument_list|,
literal|"refs/heads/master"
argument_list|,
literal|"super-project"
argument_list|,
literal|"refs/heads/master"
argument_list|)
expr_stmt|;
name|allowSubmoduleSubscription
argument_list|(
literal|"super-project"
argument_list|,
literal|"refs/heads/master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
literal|"refs/heads/master"
argument_list|)
expr_stmt|;
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|createSubmoduleSubscription
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|createSubmoduleSubscription
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|,
literal|"super-project"
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|ObjectId
name|subHEAD
init|=
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
decl_stmt|;
name|pushChangeTo
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|expectToHaveSubmoduleState
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
name|subHEAD
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hasSubmodule
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|,
literal|"super-project"
argument_list|)
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubscriptionFailOnMissingACL ()
specifier|public
name|void
name|testSubscriptionFailOnMissingACL
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRepository
argument_list|<
name|?
argument_list|>
name|superRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"super-project"
argument_list|)
decl_stmt|;
name|TestRepository
argument_list|<
name|?
argument_list|>
name|subRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"subscribed-to-project"
argument_list|)
decl_stmt|;
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|createSubmoduleSubscription
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hasSubmodule
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|)
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubscriptionFailOnWrongProjectACL ()
specifier|public
name|void
name|testSubscriptionFailOnWrongProjectACL
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRepository
argument_list|<
name|?
argument_list|>
name|superRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"super-project"
argument_list|)
decl_stmt|;
name|TestRepository
argument_list|<
name|?
argument_list|>
name|subRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"subscribed-to-project"
argument_list|)
decl_stmt|;
name|allowSubmoduleSubscription
argument_list|(
literal|"subscribed-to-project"
argument_list|,
literal|"refs/heads/master"
argument_list|,
literal|"wrong-super-project"
argument_list|,
literal|"refs/heads/master"
argument_list|)
expr_stmt|;
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|createSubmoduleSubscription
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hasSubmodule
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|)
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSubscriptionFailOnWrongBranchACL ()
specifier|public
name|void
name|testSubscriptionFailOnWrongBranchACL
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRepository
argument_list|<
name|?
argument_list|>
name|superRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"super-project"
argument_list|)
decl_stmt|;
name|TestRepository
argument_list|<
name|?
argument_list|>
name|subRepo
init|=
name|createProjectWithPush
argument_list|(
literal|"subscribed-to-project"
argument_list|)
decl_stmt|;
name|allowSubmoduleSubscription
argument_list|(
literal|"subscribed-to-project"
argument_list|,
literal|"refs/heads/master"
argument_list|,
literal|"super-project"
argument_list|,
literal|"refs/heads/wrong-branch"
argument_list|)
expr_stmt|;
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|createSubmoduleSubscription
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|pushChangeTo
argument_list|(
name|subRepo
argument_list|,
literal|"master"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hasSubmodule
argument_list|(
name|superRepo
argument_list|,
literal|"master"
argument_list|,
literal|"subscribed-to-project"
argument_list|)
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
block|}
DECL|method|deleteAllSubscriptions (TestRepository<?> repo, String branch)
specifier|private
name|void
name|deleteAllSubscriptions
parameter_list|(
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo
parameter_list|,
name|String
name|branch
parameter_list|)
throws|throws
name|Exception
block|{
name|repo
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
name|repo
operator|.
name|reset
argument_list|(
literal|"refs/remotes/origin/"
operator|+
name|branch
argument_list|)
expr_stmt|;
name|ObjectId
name|expectedId
init|=
name|repo
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
name|message
argument_list|(
literal|"delete contents in .gitmodules"
argument_list|)
operator|.
name|add
argument_list|(
literal|".gitmodules"
argument_list|,
literal|""
argument_list|)
comment|// Just remove the contents of the file!
operator|.
name|create
argument_list|()
decl_stmt|;
name|repo
operator|.
name|git
argument_list|()
operator|.
name|push
argument_list|()
operator|.
name|setRemote
argument_list|(
literal|"origin"
argument_list|)
operator|.
name|setRefSpecs
argument_list|(
operator|new
name|RefSpec
argument_list|(
literal|"HEAD:refs/heads/"
operator|+
name|branch
argument_list|)
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
name|ObjectId
name|actualId
init|=
name|repo
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
operator|.
name|getAdvertisedRef
argument_list|(
literal|"refs/heads/master"
argument_list|)
operator|.
name|getObjectId
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|actualId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expectedId
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteGitModulesFile (TestRepository<?> repo, String branch)
specifier|private
name|void
name|deleteGitModulesFile
parameter_list|(
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo
parameter_list|,
name|String
name|branch
parameter_list|)
throws|throws
name|Exception
block|{
name|repo
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
name|repo
operator|.
name|reset
argument_list|(
literal|"refs/remotes/origin/"
operator|+
name|branch
argument_list|)
expr_stmt|;
name|ObjectId
name|expectedId
init|=
name|repo
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
name|message
argument_list|(
literal|"delete .gitmodules"
argument_list|)
operator|.
name|rm
argument_list|(
literal|".gitmodules"
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|repo
operator|.
name|git
argument_list|()
operator|.
name|push
argument_list|()
operator|.
name|setRemote
argument_list|(
literal|"origin"
argument_list|)
operator|.
name|setRefSpecs
argument_list|(
operator|new
name|RefSpec
argument_list|(
literal|"HEAD:refs/heads/"
operator|+
name|branch
argument_list|)
argument_list|)
operator|.
name|call
argument_list|()
expr_stmt|;
name|ObjectId
name|actualId
init|=
name|repo
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
operator|.
name|getAdvertisedRef
argument_list|(
literal|"refs/heads/master"
argument_list|)
operator|.
name|getObjectId
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|actualId
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expectedId
argument_list|)
expr_stmt|;
block|}
DECL|method|hasSubmodule (TestRepository<?> repo, String branch, String submodule)
specifier|private
name|boolean
name|hasSubmodule
parameter_list|(
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo
parameter_list|,
name|String
name|branch
parameter_list|,
name|String
name|submodule
parameter_list|)
throws|throws
name|Exception
block|{
name|ObjectId
name|commitId
init|=
name|repo
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
operator|.
name|getAdvertisedRef
argument_list|(
literal|"refs/heads/"
operator|+
name|branch
argument_list|)
operator|.
name|getObjectId
argument_list|()
decl_stmt|;
name|RevWalk
name|rw
init|=
name|repo
operator|.
name|getRevWalk
argument_list|()
decl_stmt|;
name|RevCommit
name|c
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|commitId
argument_list|)
decl_stmt|;
name|rw
operator|.
name|parseBody
argument_list|(
name|c
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
name|RevTree
name|tree
init|=
name|c
operator|.
name|getTree
argument_list|()
decl_stmt|;
try|try
block|{
name|repo
operator|.
name|get
argument_list|(
name|tree
argument_list|,
name|submodule
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|expectToHaveCommitMessage (TestRepository<?> repo, String branch, String expectedMessage)
specifier|private
name|void
name|expectToHaveCommitMessage
parameter_list|(
name|TestRepository
argument_list|<
name|?
argument_list|>
name|repo
parameter_list|,
name|String
name|branch
parameter_list|,
name|String
name|expectedMessage
parameter_list|)
throws|throws
name|Exception
block|{
name|ObjectId
name|commitId
init|=
name|repo
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
operator|.
name|getAdvertisedRef
argument_list|(
literal|"refs/heads/"
operator|+
name|branch
argument_list|)
operator|.
name|getObjectId
argument_list|()
decl_stmt|;
name|RevWalk
name|rw
init|=
name|repo
operator|.
name|getRevWalk
argument_list|()
decl_stmt|;
name|RevCommit
name|c
init|=
name|rw
operator|.
name|parseCommit
argument_list|(
name|commitId
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|c
operator|.
name|getFullMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expectedMessage
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

