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
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|testing
operator|.
name|GerritJUnit
operator|.
name|assertThrows
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Constants
operator|.
name|R_HEADS
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
name|GerritConfig
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
name|RestResponse
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
name|IdString
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
name|MethodNotAllowedException
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
name|ResourceNotFoundException
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
name|RefNames
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
DECL|class|DeleteBranchIT
specifier|public
class|class
name|DeleteBranchIT
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|testBranch
specifier|private
name|Branch
operator|.
name|NameKey
name|testBranch
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
name|project
operator|=
name|createProject
argument_list|(
name|name
argument_list|(
literal|"p"
argument_list|)
argument_list|)
expr_stmt|;
name|testBranch
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
name|branch
argument_list|(
name|testBranch
argument_list|)
operator|.
name|create
argument_list|(
operator|new
name|BranchInput
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteBranch_Forbidden ()
specifier|public
name|void
name|deleteBranch_Forbidden
parameter_list|()
throws|throws
name|Exception
block|{
name|setApiUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|assertDeleteForbidden
argument_list|(
name|testBranch
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteBranchByAdmin ()
specifier|public
name|void
name|deleteBranchByAdmin
parameter_list|()
throws|throws
name|Exception
block|{
name|assertDeleteSucceeds
argument_list|(
name|testBranch
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteBranchByProjectOwner ()
specifier|public
name|void
name|deleteBranchByProjectOwner
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
name|assertDeleteSucceeds
argument_list|(
name|testBranch
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteBranchByAdminForcePushBlocked ()
specifier|public
name|void
name|deleteBranchByAdminForcePushBlocked
parameter_list|()
throws|throws
name|Exception
block|{
name|blockForcePush
argument_list|()
expr_stmt|;
name|assertDeleteSucceeds
argument_list|(
name|testBranch
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteBranchByProjectOwnerForcePushBlocked_Forbidden ()
specifier|public
name|void
name|deleteBranchByProjectOwnerForcePushBlocked_Forbidden
parameter_list|()
throws|throws
name|Exception
block|{
name|grantOwner
argument_list|()
expr_stmt|;
name|blockForcePush
argument_list|()
expr_stmt|;
name|setApiUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|assertDeleteForbidden
argument_list|(
name|testBranch
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteBranchByUserWithForcePushPermission ()
specifier|public
name|void
name|deleteBranchByUserWithForcePushPermission
parameter_list|()
throws|throws
name|Exception
block|{
name|grantForcePush
argument_list|()
expr_stmt|;
name|setApiUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|assertDeleteSucceeds
argument_list|(
name|testBranch
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteBranchByUserWithDeletePermission ()
specifier|public
name|void
name|deleteBranchByUserWithDeletePermission
parameter_list|()
throws|throws
name|Exception
block|{
name|grantDelete
argument_list|()
expr_stmt|;
name|setApiUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|assertDeleteSucceeds
argument_list|(
name|testBranch
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteBranchByRestWithoutRefsHeadsPrefix ()
specifier|public
name|void
name|deleteBranchByRestWithoutRefsHeadsPrefix
parameter_list|()
throws|throws
name|Exception
block|{
name|grantDelete
argument_list|()
expr_stmt|;
name|String
name|ref
init|=
name|testBranch
operator|.
name|getShortName
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|ref
argument_list|)
operator|.
name|doesNotMatch
argument_list|(
name|R_HEADS
argument_list|)
expr_stmt|;
name|assertDeleteByRestSucceeds
argument_list|(
name|testBranch
argument_list|,
name|ref
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteBranchByRestWithFullName ()
specifier|public
name|void
name|deleteBranchByRestWithFullName
parameter_list|()
throws|throws
name|Exception
block|{
name|grantDelete
argument_list|()
expr_stmt|;
name|assertDeleteByRestSucceeds
argument_list|(
name|testBranch
argument_list|,
name|testBranch
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteBranchByRestFailsWithUnencodedFullName ()
specifier|public
name|void
name|deleteBranchByRestFailsWithUnencodedFullName
parameter_list|()
throws|throws
name|Exception
block|{
name|grantDelete
argument_list|()
expr_stmt|;
name|RestResponse
name|r
init|=
name|userRestSession
operator|.
name|delete
argument_list|(
literal|"/projects/"
operator|+
name|project
operator|.
name|get
argument_list|()
operator|+
literal|"/branches/"
operator|+
name|testBranch
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertNotFound
argument_list|()
expr_stmt|;
name|branch
argument_list|(
name|testBranch
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteMetaBranch ()
specifier|public
name|void
name|deleteMetaBranch
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|metaRef
init|=
name|RefNames
operator|.
name|REFS_META
operator|+
literal|"foo"
decl_stmt|;
name|allow
argument_list|(
name|metaRef
argument_list|,
name|Permission
operator|.
name|CREATE
argument_list|,
name|REGISTERED_USERS
argument_list|)
expr_stmt|;
name|allow
argument_list|(
name|metaRef
argument_list|,
name|Permission
operator|.
name|PUSH
argument_list|,
name|REGISTERED_USERS
argument_list|)
expr_stmt|;
name|Branch
operator|.
name|NameKey
name|metaBranch
init|=
operator|new
name|Branch
operator|.
name|NameKey
argument_list|(
name|project
argument_list|,
name|metaRef
argument_list|)
decl_stmt|;
name|branch
argument_list|(
name|metaBranch
argument_list|)
operator|.
name|create
argument_list|(
operator|new
name|BranchInput
argument_list|()
argument_list|)
expr_stmt|;
name|grantDelete
argument_list|()
expr_stmt|;
name|assertDeleteByRestSucceeds
argument_list|(
name|metaBranch
argument_list|,
name|metaRef
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteUserBranch_Conflict ()
specifier|public
name|void
name|deleteUserBranch_Conflict
parameter_list|()
throws|throws
name|Exception
block|{
name|allow
argument_list|(
name|allUsers
argument_list|,
name|RefNames
operator|.
name|REFS_USERS
operator|+
literal|"*"
argument_list|,
name|Permission
operator|.
name|CREATE
argument_list|,
name|REGISTERED_USERS
argument_list|)
expr_stmt|;
name|allow
argument_list|(
name|allUsers
argument_list|,
name|RefNames
operator|.
name|REFS_USERS
operator|+
literal|"*"
argument_list|,
name|Permission
operator|.
name|PUSH
argument_list|,
name|REGISTERED_USERS
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|ResourceConflictException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"Not allowed to delete user branch."
argument_list|)
expr_stmt|;
name|branch
argument_list|(
operator|new
name|Branch
operator|.
name|NameKey
argument_list|(
name|allUsers
argument_list|,
name|RefNames
operator|.
name|refsUsers
argument_list|(
name|admin
operator|.
name|id
argument_list|)
argument_list|)
argument_list|)
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|GerritConfig
argument_list|(
name|name
operator|=
literal|"noteDb.groups.write"
argument_list|,
name|value
operator|=
literal|"true"
argument_list|)
DECL|method|deleteGroupBranch_Conflict ()
specifier|public
name|void
name|deleteGroupBranch_Conflict
parameter_list|()
throws|throws
name|Exception
block|{
name|allow
argument_list|(
name|allUsers
argument_list|,
name|RefNames
operator|.
name|REFS_GROUPS
operator|+
literal|"*"
argument_list|,
name|Permission
operator|.
name|CREATE
argument_list|,
name|REGISTERED_USERS
argument_list|)
expr_stmt|;
name|allow
argument_list|(
name|allUsers
argument_list|,
name|RefNames
operator|.
name|REFS_GROUPS
operator|+
literal|"*"
argument_list|,
name|Permission
operator|.
name|PUSH
argument_list|,
name|REGISTERED_USERS
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|ResourceConflictException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"Not allowed to delete group branch."
argument_list|)
expr_stmt|;
name|branch
argument_list|(
operator|new
name|Branch
operator|.
name|NameKey
argument_list|(
name|allUsers
argument_list|,
name|RefNames
operator|.
name|refsGroups
argument_list|(
name|adminGroupUuid
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|cannotDeleteRefsMetaConfig ()
specifier|public
name|void
name|cannotDeleteRefsMetaConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|MethodNotAllowedException
name|thrown
init|=
name|assertThrows
argument_list|(
name|MethodNotAllowedException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|branch
argument_list|(
operator|new
name|Branch
operator|.
name|NameKey
argument_list|(
name|allUsers
argument_list|,
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|)
argument_list|)
operator|.
name|delete
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|thrown
argument_list|)
operator|.
name|hasMessageThat
argument_list|()
operator|.
name|contains
argument_list|(
literal|"not allowed to delete branch refs/meta/config"
argument_list|)
expr_stmt|;
block|}
DECL|method|blockForcePush ()
specifier|private
name|void
name|blockForcePush
parameter_list|()
throws|throws
name|Exception
block|{
name|block
argument_list|(
literal|"refs/heads/*"
argument_list|,
name|Permission
operator|.
name|PUSH
argument_list|,
name|ANONYMOUS_USERS
argument_list|)
operator|.
name|setForce
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|grantForcePush ()
specifier|private
name|void
name|grantForcePush
parameter_list|()
throws|throws
name|Exception
block|{
name|grant
argument_list|(
name|project
argument_list|,
literal|"refs/heads/*"
argument_list|,
name|Permission
operator|.
name|PUSH
argument_list|,
literal|true
argument_list|,
name|ANONYMOUS_USERS
argument_list|)
expr_stmt|;
block|}
DECL|method|grantDelete ()
specifier|private
name|void
name|grantDelete
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
name|DELETE
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
DECL|method|branch (Branch.NameKey branch)
specifier|private
name|BranchApi
name|branch
parameter_list|(
name|Branch
operator|.
name|NameKey
name|branch
parameter_list|)
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
DECL|method|assertDeleteByRestSucceeds (Branch.NameKey branch, String ref)
specifier|private
name|void
name|assertDeleteByRestSucceeds
parameter_list|(
name|Branch
operator|.
name|NameKey
name|branch
parameter_list|,
name|String
name|ref
parameter_list|)
throws|throws
name|Exception
block|{
name|RestResponse
name|r
init|=
name|userRestSession
operator|.
name|delete
argument_list|(
literal|"/projects/"
operator|+
name|IdString
operator|.
name|fromDecoded
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|encoded
argument_list|()
operator|+
literal|"/branches/"
operator|+
name|IdString
operator|.
name|fromDecoded
argument_list|(
name|ref
argument_list|)
operator|.
name|encoded
argument_list|()
argument_list|)
decl_stmt|;
name|r
operator|.
name|assertNoContent
argument_list|()
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|ResourceNotFoundException
operator|.
name|class
argument_list|)
expr_stmt|;
name|branch
argument_list|(
name|branch
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
DECL|method|assertDeleteSucceeds (Branch.NameKey branch)
specifier|private
name|void
name|assertDeleteSucceeds
parameter_list|(
name|Branch
operator|.
name|NameKey
name|branch
parameter_list|)
throws|throws
name|Exception
block|{
name|assertThat
argument_list|(
name|branch
argument_list|(
name|branch
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|canDelete
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|String
name|branchRev
init|=
name|branch
argument_list|(
name|branch
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|revision
decl_stmt|;
name|branch
argument_list|(
name|branch
argument_list|)
operator|.
name|delete
argument_list|()
expr_stmt|;
name|eventRecorder
operator|.
name|assertRefUpdatedEvents
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|,
name|branch
operator|.
name|get
argument_list|()
argument_list|,
literal|null
argument_list|,
name|branchRev
argument_list|,
name|branchRev
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|ResourceNotFoundException
operator|.
name|class
argument_list|)
expr_stmt|;
name|branch
argument_list|(
name|branch
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
DECL|method|assertDeleteForbidden (Branch.NameKey branch)
specifier|private
name|void
name|assertDeleteForbidden
parameter_list|(
name|Branch
operator|.
name|NameKey
name|branch
parameter_list|)
throws|throws
name|Exception
block|{
name|assertThat
argument_list|(
name|branch
argument_list|(
name|branch
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|canDelete
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|AuthException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"not permitted: delete"
argument_list|)
expr_stmt|;
name|branch
argument_list|(
name|branch
argument_list|)
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

