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
name|AcceptanceTestRequestScope
operator|.
name|Context
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
name|TestAccount
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
name|acceptance
operator|.
name|testsuite
operator|.
name|project
operator|.
name|ProjectOperations
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
name|restapi
operator|.
name|AuthException
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
name|AccountGroup
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
name|server
operator|.
name|group
operator|.
name|SystemGroupBackend
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
name|internal
operator|.
name|storage
operator|.
name|dfs
operator|.
name|InMemoryRepository
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

begin_class
DECL|class|ChangeOwnerIT
specifier|public
class|class
name|ChangeOwnerIT
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|user2
specifier|private
name|TestAccount
name|user2
decl_stmt|;
DECL|field|projectOperations
annotation|@
name|Inject
specifier|private
name|ProjectOperations
name|projectOperations
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
name|setApiUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|user2
operator|=
name|accountCreator
operator|.
name|user2
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestProjectInput
argument_list|(
name|cloneAs
operator|=
literal|"user"
argument_list|)
DECL|method|testChangeOwner_OwnerACLNotGranted ()
specifier|public
name|void
name|testChangeOwner_OwnerACLNotGranted
parameter_list|()
throws|throws
name|Exception
block|{
name|assertApproveFails
argument_list|(
name|user
argument_list|,
name|createMyChange
argument_list|(
name|testRepo
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestProjectInput
argument_list|(
name|cloneAs
operator|=
literal|"user"
argument_list|)
DECL|method|testChangeOwner_OwnerACLGranted ()
specifier|public
name|void
name|testChangeOwner_OwnerACLGranted
parameter_list|()
throws|throws
name|Exception
block|{
name|grantApproveToChangeOwner
argument_list|(
name|project
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|user
argument_list|,
name|createMyChange
argument_list|(
name|testRepo
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestProjectInput
argument_list|(
name|cloneAs
operator|=
literal|"user"
argument_list|)
DECL|method|testChangeOwner_NotOwnerACLGranted ()
specifier|public
name|void
name|testChangeOwner_NotOwnerACLGranted
parameter_list|()
throws|throws
name|Exception
block|{
name|grantApproveToChangeOwner
argument_list|(
name|project
argument_list|)
expr_stmt|;
name|assertApproveFails
argument_list|(
name|user2
argument_list|,
name|createMyChange
argument_list|(
name|testRepo
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testChangeOwner_OwnerACLGrantedOnParentProject ()
specifier|public
name|void
name|testChangeOwner_OwnerACLGrantedOnParentProject
parameter_list|()
throws|throws
name|Exception
block|{
name|setApiUser
argument_list|(
name|admin
argument_list|)
expr_stmt|;
name|grantApproveToChangeOwner
argument_list|(
name|project
argument_list|)
expr_stmt|;
name|Project
operator|.
name|NameKey
name|child
init|=
name|projectOperations
operator|.
name|newProject
argument_list|()
operator|.
name|parent
argument_list|(
name|project
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|setApiUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|TestRepository
argument_list|<
name|InMemoryRepository
argument_list|>
name|childRepo
init|=
name|cloneProject
argument_list|(
name|child
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|approve
argument_list|(
name|user
argument_list|,
name|createMyChange
argument_list|(
name|childRepo
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testChangeOwner_BlockedOnParentProject ()
specifier|public
name|void
name|testChangeOwner_BlockedOnParentProject
parameter_list|()
throws|throws
name|Exception
block|{
name|setApiUser
argument_list|(
name|admin
argument_list|)
expr_stmt|;
name|blockApproveForChangeOwner
argument_list|(
name|project
argument_list|)
expr_stmt|;
name|Project
operator|.
name|NameKey
name|child
init|=
name|projectOperations
operator|.
name|newProject
argument_list|()
operator|.
name|parent
argument_list|(
name|project
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|setApiUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|grantApproveToAll
argument_list|(
name|child
argument_list|)
expr_stmt|;
name|TestRepository
argument_list|<
name|InMemoryRepository
argument_list|>
name|childRepo
init|=
name|cloneProject
argument_list|(
name|child
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|String
name|changeId
init|=
name|createMyChange
argument_list|(
name|childRepo
argument_list|)
decl_stmt|;
comment|// change owner cannot approve because Change-Owner group is blocked on parent
name|assertApproveFails
argument_list|(
name|user
argument_list|,
name|changeId
argument_list|)
expr_stmt|;
comment|// other user can approve
name|approve
argument_list|(
name|user2
argument_list|,
name|changeId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testChangeOwner_BlockedOnParentProjectAndExclusiveAllowOnChild ()
specifier|public
name|void
name|testChangeOwner_BlockedOnParentProjectAndExclusiveAllowOnChild
parameter_list|()
throws|throws
name|Exception
block|{
name|setApiUser
argument_list|(
name|admin
argument_list|)
expr_stmt|;
name|blockApproveForChangeOwner
argument_list|(
name|project
argument_list|)
expr_stmt|;
name|Project
operator|.
name|NameKey
name|child
init|=
name|projectOperations
operator|.
name|newProject
argument_list|()
operator|.
name|parent
argument_list|(
name|project
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|setApiUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|grantExclusiveApproveToAll
argument_list|(
name|child
argument_list|)
expr_stmt|;
name|TestRepository
argument_list|<
name|InMemoryRepository
argument_list|>
name|childRepo
init|=
name|cloneProject
argument_list|(
name|child
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|String
name|changeId
init|=
name|createMyChange
argument_list|(
name|childRepo
argument_list|)
decl_stmt|;
comment|// change owner cannot approve because Change-Owner group is blocked on parent
name|assertApproveFails
argument_list|(
name|user
argument_list|,
name|changeId
argument_list|)
expr_stmt|;
comment|// other user can approve
name|approve
argument_list|(
name|user2
argument_list|,
name|changeId
argument_list|)
expr_stmt|;
block|}
DECL|method|approve (TestAccount a, String changeId)
specifier|private
name|void
name|approve
parameter_list|(
name|TestAccount
name|a
parameter_list|,
name|String
name|changeId
parameter_list|)
throws|throws
name|Exception
block|{
name|Context
name|old
init|=
name|setApiUser
argument_list|(
name|a
argument_list|)
decl_stmt|;
try|try
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
name|review
argument_list|(
name|ReviewInput
operator|.
name|approve
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|atrScope
operator|.
name|set
argument_list|(
name|old
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertApproveFails (TestAccount a, String changeId)
specifier|private
name|void
name|assertApproveFails
parameter_list|(
name|TestAccount
name|a
parameter_list|,
name|String
name|changeId
parameter_list|)
throws|throws
name|Exception
block|{
name|exception
operator|.
name|expect
argument_list|(
name|AuthException
operator|.
name|class
argument_list|)
expr_stmt|;
name|approve
argument_list|(
name|a
argument_list|,
name|changeId
argument_list|)
expr_stmt|;
block|}
DECL|method|grantApproveToChangeOwner (Project.NameKey project)
specifier|private
name|void
name|grantApproveToChangeOwner
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|)
throws|throws
name|Exception
block|{
name|grantApprove
argument_list|(
name|project
argument_list|,
name|SystemGroupBackend
operator|.
name|CHANGE_OWNER
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|grantApproveToAll (Project.NameKey project)
specifier|private
name|void
name|grantApproveToAll
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|)
throws|throws
name|Exception
block|{
name|grantApprove
argument_list|(
name|project
argument_list|,
name|SystemGroupBackend
operator|.
name|REGISTERED_USERS
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|grantExclusiveApproveToAll (Project.NameKey project)
specifier|private
name|void
name|grantExclusiveApproveToAll
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|)
throws|throws
name|Exception
block|{
name|grantApprove
argument_list|(
name|project
argument_list|,
name|SystemGroupBackend
operator|.
name|REGISTERED_USERS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|grantApprove (Project.NameKey project, AccountGroup.UUID groupUUID, boolean exclusive)
specifier|private
name|void
name|grantApprove
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|AccountGroup
operator|.
name|UUID
name|groupUUID
parameter_list|,
name|boolean
name|exclusive
parameter_list|)
throws|throws
name|Exception
block|{
name|grantLabel
argument_list|(
literal|"Code-Review"
argument_list|,
operator|-
literal|2
argument_list|,
literal|2
argument_list|,
name|project
argument_list|,
literal|"refs/heads/*"
argument_list|,
literal|false
argument_list|,
name|groupUUID
argument_list|,
name|exclusive
argument_list|)
expr_stmt|;
block|}
DECL|method|blockApproveForChangeOwner (Project.NameKey project)
specifier|private
name|void
name|blockApproveForChangeOwner
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|)
throws|throws
name|Exception
block|{
name|blockLabel
argument_list|(
literal|"Code-Review"
argument_list|,
operator|-
literal|2
argument_list|,
literal|2
argument_list|,
name|SystemGroupBackend
operator|.
name|CHANGE_OWNER
argument_list|,
literal|"refs/heads/*"
argument_list|,
name|project
argument_list|)
expr_stmt|;
block|}
DECL|method|createMyChange (TestRepository<InMemoryRepository> testRepo)
specifier|private
name|String
name|createMyChange
parameter_list|(
name|TestRepository
argument_list|<
name|InMemoryRepository
argument_list|>
name|testRepo
parameter_list|)
throws|throws
name|Exception
block|{
name|PushOneCommit
name|push
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|user
operator|.
name|getIdent
argument_list|()
argument_list|,
name|testRepo
argument_list|)
decl_stmt|;
return|return
name|push
operator|.
name|to
argument_list|(
literal|"refs/for/master"
argument_list|)
operator|.
name|getChangeId
argument_list|()
return|;
block|}
block|}
end_class

end_unit

