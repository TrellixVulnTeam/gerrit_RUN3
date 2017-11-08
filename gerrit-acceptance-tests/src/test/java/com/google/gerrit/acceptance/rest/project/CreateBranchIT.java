begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.rest.project
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
name|project
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
name|server
operator|.
name|group
operator|.
name|SystemGroupBackend
operator|.
name|ANONYMOUS_USERS
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
name|group
operator|.
name|SystemGroupBackend
operator|.
name|REGISTERED_USERS
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
name|NoHttpd
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
name|extensions
operator|.
name|api
operator|.
name|projects
operator|.
name|BranchApi
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
name|BranchInfo
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
name|extensions
operator|.
name|restapi
operator|.
name|RestApiException
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
name|Branch
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
annotation|@
name|NoHttpd
DECL|class|CreateBranchIT
specifier|public
class|class
name|CreateBranchIT
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|branch
specifier|private
name|Branch
operator|.
name|NameKey
name|branch
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
name|branch
operator|=
operator|new
name|Branch
operator|.
name|NameKey
argument_list|(
name|project
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|createBranch_Forbidden ()
specifier|public
name|void
name|createBranch_Forbidden
parameter_list|()
throws|throws
name|Exception
block|{
name|setApiUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|assertCreateFails
argument_list|(
name|AuthException
operator|.
name|class
argument_list|,
literal|"create not permitted for refs/heads/test"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|createBranchByAdmin ()
specifier|public
name|void
name|createBranchByAdmin
parameter_list|()
throws|throws
name|Exception
block|{
name|assertCreateSucceeds
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|branchAlreadyExists_Conflict ()
specifier|public
name|void
name|branchAlreadyExists_Conflict
parameter_list|()
throws|throws
name|Exception
block|{
name|assertCreateSucceeds
argument_list|()
expr_stmt|;
name|assertCreateFails
argument_list|(
name|ResourceConflictException
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|createBranchByProjectOwner ()
specifier|public
name|void
name|createBranchByProjectOwner
parameter_list|()
throws|throws
name|Exception
block|{
name|grantOwner
argument_list|()
expr_stmt|;
name|setApiUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|assertCreateSucceeds
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|createBranchByAdminCreateReferenceBlocked_Forbidden ()
specifier|public
name|void
name|createBranchByAdminCreateReferenceBlocked_Forbidden
parameter_list|()
throws|throws
name|Exception
block|{
name|blockCreateReference
argument_list|()
expr_stmt|;
name|assertCreateFails
argument_list|(
name|AuthException
operator|.
name|class
argument_list|,
literal|"create not permitted for refs/heads/test"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|createBranchByProjectOwnerCreateReferenceBlocked_Forbidden ()
specifier|public
name|void
name|createBranchByProjectOwnerCreateReferenceBlocked_Forbidden
parameter_list|()
throws|throws
name|Exception
block|{
name|grantOwner
argument_list|()
expr_stmt|;
name|blockCreateReference
argument_list|()
expr_stmt|;
name|setApiUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|assertCreateFails
argument_list|(
name|AuthException
operator|.
name|class
argument_list|,
literal|"create not permitted for refs/heads/test"
argument_list|)
expr_stmt|;
block|}
DECL|method|blockCreateReference ()
specifier|private
name|void
name|blockCreateReference
parameter_list|()
throws|throws
name|Exception
block|{
name|block
argument_list|(
literal|"refs/*"
argument_list|,
name|Permission
operator|.
name|CREATE
argument_list|,
name|ANONYMOUS_USERS
argument_list|)
expr_stmt|;
block|}
DECL|method|grantOwner ()
specifier|private
name|void
name|grantOwner
parameter_list|()
throws|throws
name|Exception
block|{
name|allow
argument_list|(
literal|"refs/*"
argument_list|,
name|Permission
operator|.
name|OWNER
argument_list|,
name|REGISTERED_USERS
argument_list|)
expr_stmt|;
block|}
DECL|method|branch ()
specifier|private
name|BranchApi
name|branch
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|branch
operator|.
name|getParentKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|branch
argument_list|(
name|branch
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
DECL|method|assertCreateSucceeds ()
specifier|private
name|void
name|assertCreateSucceeds
parameter_list|()
throws|throws
name|Exception
block|{
name|BranchInfo
name|created
init|=
name|branch
argument_list|()
operator|.
name|create
argument_list|(
operator|new
name|BranchInput
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|created
operator|.
name|ref
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|Constants
operator|.
name|R_HEADS
operator|+
name|branch
operator|.
name|getShortName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertCreateFails (Class<? extends RestApiException> errType, String errMsg)
specifier|private
name|void
name|assertCreateFails
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|RestApiException
argument_list|>
name|errType
parameter_list|,
name|String
name|errMsg
parameter_list|)
throws|throws
name|Exception
block|{
name|exception
operator|.
name|expect
argument_list|(
name|errType
argument_list|)
expr_stmt|;
if|if
condition|(
name|errMsg
operator|!=
literal|null
condition|)
block|{
name|exception
operator|.
name|expectMessage
argument_list|(
name|errMsg
argument_list|)
expr_stmt|;
block|}
name|branch
argument_list|()
operator|.
name|create
argument_list|(
operator|new
name|BranchInput
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertCreateFails (Class<? extends RestApiException> errType)
specifier|private
name|void
name|assertCreateFails
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|RestApiException
argument_list|>
name|errType
parameter_list|)
throws|throws
name|Exception
block|{
name|assertCreateFails
argument_list|(
name|errType
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

