begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2018 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.server.permissions
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
name|permissions
package|;
end_package

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
name|assertNotEquals
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
name|conditions
operator|.
name|BooleanCondition
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
name|BranchNameKey
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
name|CurrentUser
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
name|permissions
operator|.
name|ChangePermission
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
name|permissions
operator|.
name|GlobalPermission
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
name|permissions
operator|.
name|PermissionBackend
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
name|permissions
operator|.
name|ProjectPermission
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
name|permissions
operator|.
name|RefPermission
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
name|query
operator|.
name|change
operator|.
name|ChangeData
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|PermissionBackendConditionIT
specifier|public
class|class
name|PermissionBackendConditionIT
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|pb
annotation|@
name|Inject
name|PermissionBackend
name|pb
decl_stmt|;
DECL|field|projectOperations
annotation|@
name|Inject
name|ProjectOperations
name|projectOperations
decl_stmt|;
annotation|@
name|Test
DECL|method|globalPermissions_sameUserAndPermissionEquals ()
specifier|public
name|void
name|globalPermissions_sameUserAndPermissionEquals
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanCondition
name|cond1
init|=
name|pb
operator|.
name|user
argument_list|(
name|user
argument_list|()
argument_list|)
operator|.
name|testCond
argument_list|(
name|GlobalPermission
operator|.
name|CREATE_GROUP
argument_list|)
decl_stmt|;
name|BooleanCondition
name|cond2
init|=
name|pb
operator|.
name|user
argument_list|(
name|user
argument_list|()
argument_list|)
operator|.
name|testCond
argument_list|(
name|GlobalPermission
operator|.
name|CREATE_GROUP
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|cond1
argument_list|,
name|cond2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cond1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|cond2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|globalPermissions_differentPermissionDoesNotEquals ()
specifier|public
name|void
name|globalPermissions_differentPermissionDoesNotEquals
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanCondition
name|cond1
init|=
name|pb
operator|.
name|user
argument_list|(
name|user
argument_list|()
argument_list|)
operator|.
name|testCond
argument_list|(
name|GlobalPermission
operator|.
name|CREATE_GROUP
argument_list|)
decl_stmt|;
name|BooleanCondition
name|cond2
init|=
name|pb
operator|.
name|user
argument_list|(
name|user
argument_list|()
argument_list|)
operator|.
name|testCond
argument_list|(
name|GlobalPermission
operator|.
name|ACCESS_DATABASE
argument_list|)
decl_stmt|;
name|assertNotEquals
argument_list|(
name|cond1
argument_list|,
name|cond2
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|cond1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|cond2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|globalPermissions_differentUserDoesNotEqual ()
specifier|public
name|void
name|globalPermissions_differentUserDoesNotEqual
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanCondition
name|cond1
init|=
name|pb
operator|.
name|user
argument_list|(
name|user
argument_list|()
argument_list|)
operator|.
name|testCond
argument_list|(
name|GlobalPermission
operator|.
name|CREATE_GROUP
argument_list|)
decl_stmt|;
name|BooleanCondition
name|cond2
init|=
name|pb
operator|.
name|user
argument_list|(
name|admin
argument_list|()
argument_list|)
operator|.
name|testCond
argument_list|(
name|GlobalPermission
operator|.
name|CREATE_GROUP
argument_list|)
decl_stmt|;
name|assertNotEquals
argument_list|(
name|cond1
argument_list|,
name|cond2
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|cond1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|cond2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|changePermissions_sameResourceAndUserEquals ()
specifier|public
name|void
name|changePermissions_sameResourceAndUserEquals
parameter_list|()
throws|throws
name|Exception
block|{
name|ChangeData
name|change
init|=
name|createChange
argument_list|()
operator|.
name|getChange
argument_list|()
decl_stmt|;
name|BooleanCondition
name|cond1
init|=
name|pb
operator|.
name|user
argument_list|(
name|user
argument_list|()
argument_list|)
operator|.
name|change
argument_list|(
name|change
argument_list|)
operator|.
name|testCond
argument_list|(
name|ChangePermission
operator|.
name|READ
argument_list|)
decl_stmt|;
name|BooleanCondition
name|cond2
init|=
name|pb
operator|.
name|user
argument_list|(
name|user
argument_list|()
argument_list|)
operator|.
name|change
argument_list|(
name|change
argument_list|)
operator|.
name|testCond
argument_list|(
name|ChangePermission
operator|.
name|READ
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|cond1
argument_list|,
name|cond2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cond1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|cond2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|changePermissions_sameResourceDifferentUserDoesNotEqual ()
specifier|public
name|void
name|changePermissions_sameResourceDifferentUserDoesNotEqual
parameter_list|()
throws|throws
name|Exception
block|{
name|ChangeData
name|change
init|=
name|createChange
argument_list|()
operator|.
name|getChange
argument_list|()
decl_stmt|;
name|BooleanCondition
name|cond1
init|=
name|pb
operator|.
name|user
argument_list|(
name|user
argument_list|()
argument_list|)
operator|.
name|change
argument_list|(
name|change
argument_list|)
operator|.
name|testCond
argument_list|(
name|ChangePermission
operator|.
name|READ
argument_list|)
decl_stmt|;
name|BooleanCondition
name|cond2
init|=
name|pb
operator|.
name|user
argument_list|(
name|admin
argument_list|()
argument_list|)
operator|.
name|change
argument_list|(
name|change
argument_list|)
operator|.
name|testCond
argument_list|(
name|ChangePermission
operator|.
name|READ
argument_list|)
decl_stmt|;
name|assertNotEquals
argument_list|(
name|cond1
argument_list|,
name|cond2
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|cond1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|cond2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|changePermissions_differentResourceSameUserDoesNotEqual ()
specifier|public
name|void
name|changePermissions_differentResourceSameUserDoesNotEqual
parameter_list|()
throws|throws
name|Exception
block|{
name|ChangeData
name|change1
init|=
name|createChange
argument_list|()
operator|.
name|getChange
argument_list|()
decl_stmt|;
name|ChangeData
name|change2
init|=
name|createChange
argument_list|()
operator|.
name|getChange
argument_list|()
decl_stmt|;
name|BooleanCondition
name|cond1
init|=
name|pb
operator|.
name|user
argument_list|(
name|user
argument_list|()
argument_list|)
operator|.
name|change
argument_list|(
name|change1
argument_list|)
operator|.
name|testCond
argument_list|(
name|ChangePermission
operator|.
name|READ
argument_list|)
decl_stmt|;
name|BooleanCondition
name|cond2
init|=
name|pb
operator|.
name|user
argument_list|(
name|user
argument_list|()
argument_list|)
operator|.
name|change
argument_list|(
name|change2
argument_list|)
operator|.
name|testCond
argument_list|(
name|ChangePermission
operator|.
name|READ
argument_list|)
decl_stmt|;
name|assertNotEquals
argument_list|(
name|cond1
argument_list|,
name|cond2
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|cond1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|cond2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|projectPermissions_sameResourceAndUserEquals ()
specifier|public
name|void
name|projectPermissions_sameResourceAndUserEquals
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanCondition
name|cond1
init|=
name|pb
operator|.
name|user
argument_list|(
name|user
argument_list|()
argument_list|)
operator|.
name|project
argument_list|(
name|project
argument_list|)
operator|.
name|testCond
argument_list|(
name|ProjectPermission
operator|.
name|READ
argument_list|)
decl_stmt|;
name|BooleanCondition
name|cond2
init|=
name|pb
operator|.
name|user
argument_list|(
name|user
argument_list|()
argument_list|)
operator|.
name|project
argument_list|(
name|project
argument_list|)
operator|.
name|testCond
argument_list|(
name|ProjectPermission
operator|.
name|READ
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|cond1
argument_list|,
name|cond2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cond1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|cond2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|projectPermissions_sameResourceDifferentUserDoesNotEqual ()
specifier|public
name|void
name|projectPermissions_sameResourceDifferentUserDoesNotEqual
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanCondition
name|cond1
init|=
name|pb
operator|.
name|user
argument_list|(
name|user
argument_list|()
argument_list|)
operator|.
name|project
argument_list|(
name|project
argument_list|)
operator|.
name|testCond
argument_list|(
name|ProjectPermission
operator|.
name|READ
argument_list|)
decl_stmt|;
name|BooleanCondition
name|cond2
init|=
name|pb
operator|.
name|user
argument_list|(
name|admin
argument_list|()
argument_list|)
operator|.
name|project
argument_list|(
name|project
argument_list|)
operator|.
name|testCond
argument_list|(
name|ProjectPermission
operator|.
name|READ
argument_list|)
decl_stmt|;
name|assertNotEquals
argument_list|(
name|cond1
argument_list|,
name|cond2
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|cond1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|cond2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|projectPermissions_differentResourceSameUserDoesNotEqual ()
specifier|public
name|void
name|projectPermissions_differentResourceSameUserDoesNotEqual
parameter_list|()
throws|throws
name|Exception
block|{
name|Project
operator|.
name|NameKey
name|project2
init|=
name|projectOperations
operator|.
name|newProject
argument_list|()
operator|.
name|create
argument_list|()
decl_stmt|;
name|BooleanCondition
name|cond1
init|=
name|pb
operator|.
name|user
argument_list|(
name|user
argument_list|()
argument_list|)
operator|.
name|project
argument_list|(
name|project
argument_list|)
operator|.
name|testCond
argument_list|(
name|ProjectPermission
operator|.
name|READ
argument_list|)
decl_stmt|;
name|BooleanCondition
name|cond2
init|=
name|pb
operator|.
name|user
argument_list|(
name|user
argument_list|()
argument_list|)
operator|.
name|project
argument_list|(
name|project2
argument_list|)
operator|.
name|testCond
argument_list|(
name|ProjectPermission
operator|.
name|READ
argument_list|)
decl_stmt|;
name|assertNotEquals
argument_list|(
name|cond1
argument_list|,
name|cond2
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|cond1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|cond2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|refPermissions_sameResourceAndUserEquals ()
specifier|public
name|void
name|refPermissions_sameResourceAndUserEquals
parameter_list|()
throws|throws
name|Exception
block|{
name|BranchNameKey
name|branch
init|=
name|BranchNameKey
operator|.
name|create
argument_list|(
name|project
argument_list|,
literal|"branch"
argument_list|)
decl_stmt|;
name|BooleanCondition
name|cond1
init|=
name|pb
operator|.
name|user
argument_list|(
name|user
argument_list|()
argument_list|)
operator|.
name|ref
argument_list|(
name|branch
argument_list|)
operator|.
name|testCond
argument_list|(
name|RefPermission
operator|.
name|READ
argument_list|)
decl_stmt|;
name|BooleanCondition
name|cond2
init|=
name|pb
operator|.
name|user
argument_list|(
name|user
argument_list|()
argument_list|)
operator|.
name|ref
argument_list|(
name|branch
argument_list|)
operator|.
name|testCond
argument_list|(
name|RefPermission
operator|.
name|READ
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|cond1
argument_list|,
name|cond2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cond1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|cond2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|refPermissions_sameResourceAndDifferentUserDoesNotEqual ()
specifier|public
name|void
name|refPermissions_sameResourceAndDifferentUserDoesNotEqual
parameter_list|()
throws|throws
name|Exception
block|{
name|BranchNameKey
name|branch
init|=
name|BranchNameKey
operator|.
name|create
argument_list|(
name|project
argument_list|,
literal|"branch"
argument_list|)
decl_stmt|;
name|BooleanCondition
name|cond1
init|=
name|pb
operator|.
name|user
argument_list|(
name|user
argument_list|()
argument_list|)
operator|.
name|ref
argument_list|(
name|branch
argument_list|)
operator|.
name|testCond
argument_list|(
name|RefPermission
operator|.
name|READ
argument_list|)
decl_stmt|;
name|BooleanCondition
name|cond2
init|=
name|pb
operator|.
name|user
argument_list|(
name|admin
argument_list|()
argument_list|)
operator|.
name|ref
argument_list|(
name|branch
argument_list|)
operator|.
name|testCond
argument_list|(
name|RefPermission
operator|.
name|READ
argument_list|)
decl_stmt|;
name|assertNotEquals
argument_list|(
name|cond1
argument_list|,
name|cond2
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|cond1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|cond2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|refPermissions_differentResourceAndSameUserDoesNotEqual ()
specifier|public
name|void
name|refPermissions_differentResourceAndSameUserDoesNotEqual
parameter_list|()
throws|throws
name|Exception
block|{
name|BranchNameKey
name|branch1
init|=
name|BranchNameKey
operator|.
name|create
argument_list|(
name|project
argument_list|,
literal|"branch"
argument_list|)
decl_stmt|;
name|BranchNameKey
name|branch2
init|=
name|BranchNameKey
operator|.
name|create
argument_list|(
name|project
argument_list|,
literal|"branch2"
argument_list|)
decl_stmt|;
name|BooleanCondition
name|cond1
init|=
name|pb
operator|.
name|user
argument_list|(
name|user
argument_list|()
argument_list|)
operator|.
name|ref
argument_list|(
name|branch1
argument_list|)
operator|.
name|testCond
argument_list|(
name|RefPermission
operator|.
name|READ
argument_list|)
decl_stmt|;
name|BooleanCondition
name|cond2
init|=
name|pb
operator|.
name|user
argument_list|(
name|user
argument_list|()
argument_list|)
operator|.
name|ref
argument_list|(
name|branch2
argument_list|)
operator|.
name|testCond
argument_list|(
name|RefPermission
operator|.
name|READ
argument_list|)
decl_stmt|;
name|assertNotEquals
argument_list|(
name|cond1
argument_list|,
name|cond2
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|cond1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|cond2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|refPermissions_differentResourceAndSameUserDoesNotEqual2 ()
specifier|public
name|void
name|refPermissions_differentResourceAndSameUserDoesNotEqual2
parameter_list|()
throws|throws
name|Exception
block|{
name|BranchNameKey
name|branch1
init|=
name|BranchNameKey
operator|.
name|create
argument_list|(
name|project
argument_list|,
literal|"branch"
argument_list|)
decl_stmt|;
name|BranchNameKey
name|branch2
init|=
name|BranchNameKey
operator|.
name|create
argument_list|(
name|projectOperations
operator|.
name|newProject
argument_list|()
operator|.
name|create
argument_list|()
argument_list|,
literal|"branch"
argument_list|)
decl_stmt|;
name|BooleanCondition
name|cond1
init|=
name|pb
operator|.
name|user
argument_list|(
name|user
argument_list|()
argument_list|)
operator|.
name|ref
argument_list|(
name|branch1
argument_list|)
operator|.
name|testCond
argument_list|(
name|RefPermission
operator|.
name|READ
argument_list|)
decl_stmt|;
name|BooleanCondition
name|cond2
init|=
name|pb
operator|.
name|user
argument_list|(
name|user
argument_list|()
argument_list|)
operator|.
name|ref
argument_list|(
name|branch2
argument_list|)
operator|.
name|testCond
argument_list|(
name|RefPermission
operator|.
name|READ
argument_list|)
decl_stmt|;
name|assertNotEquals
argument_list|(
name|cond1
argument_list|,
name|cond2
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
name|cond1
operator|.
name|hashCode
argument_list|()
argument_list|,
name|cond2
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|user ()
specifier|private
name|CurrentUser
name|user
parameter_list|()
block|{
return|return
name|identifiedUserFactory
operator|.
name|create
argument_list|(
name|user
operator|.
name|id
argument_list|()
argument_list|)
return|;
block|}
DECL|method|admin ()
specifier|private
name|CurrentUser
name|admin
parameter_list|()
block|{
return|return
name|identifiedUserFactory
operator|.
name|create
argument_list|(
name|admin
operator|.
name|id
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

