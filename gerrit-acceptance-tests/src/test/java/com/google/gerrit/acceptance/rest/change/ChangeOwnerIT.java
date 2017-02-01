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
name|common
operator|.
name|data
operator|.
name|Permission
operator|.
name|LABEL
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
name|common
operator|.
name|data
operator|.
name|AccessSection
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
name|Permission
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
name|PermissionRule
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
name|server
operator|.
name|git
operator|.
name|MetaDataUpdate
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
name|ProjectConfig
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
name|accounts
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
argument_list|()
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
argument_list|()
expr_stmt|;
name|approve
argument_list|(
name|user
argument_list|,
name|createMyChange
argument_list|()
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
argument_list|()
expr_stmt|;
name|assertApproveFails
argument_list|(
name|user2
argument_list|,
name|createMyChange
argument_list|()
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
DECL|method|grantApproveToChangeOwner ()
specifier|private
name|void
name|grantApproveToChangeOwner
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|MetaDataUpdate
name|md
init|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|project
argument_list|)
init|)
block|{
name|md
operator|.
name|setMessage
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Grant approve to change owner"
argument_list|)
argument_list|)
expr_stmt|;
name|ProjectConfig
name|config
init|=
name|ProjectConfig
operator|.
name|read
argument_list|(
name|md
argument_list|)
decl_stmt|;
name|AccessSection
name|s
init|=
name|config
operator|.
name|getAccessSection
argument_list|(
literal|"refs/heads/*"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Permission
name|p
init|=
name|s
operator|.
name|getPermission
argument_list|(
name|LABEL
operator|+
literal|"Code-Review"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|PermissionRule
name|rule
init|=
operator|new
name|PermissionRule
argument_list|(
name|config
operator|.
name|resolve
argument_list|(
name|systemGroupBackend
operator|.
name|getGroup
argument_list|(
name|SystemGroupBackend
operator|.
name|CHANGE_OWNER
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|rule
operator|.
name|setMin
argument_list|(
operator|-
literal|2
argument_list|)
expr_stmt|;
name|rule
operator|.
name|setMax
argument_list|(
operator|+
literal|2
argument_list|)
expr_stmt|;
name|p
operator|.
name|add
argument_list|(
name|rule
argument_list|)
expr_stmt|;
name|config
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
name|projectCache
operator|.
name|evict
argument_list|(
name|config
operator|.
name|getProject
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createMyChange ()
specifier|private
name|String
name|createMyChange
parameter_list|()
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
name|db
argument_list|,
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

