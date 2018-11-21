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
name|gerrit
operator|.
name|acceptance
operator|.
name|rest
operator|.
name|project
operator|.
name|ProjectAssert
operator|.
name|assertProjectInfo
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
name|common
operator|.
name|ProjectInfo
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
name|Project
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
annotation|@
name|NoHttpd
DECL|class|GetChildProjectIT
specifier|public
class|class
name|GetChildProjectIT
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|projectOperations
annotation|@
name|Inject
specifier|private
name|ProjectOperations
name|projectOperations
decl_stmt|;
annotation|@
name|Test
DECL|method|getNonExistingChildProject_NotFound ()
specifier|public
name|void
name|getNonExistingChildProject_NotFound
parameter_list|()
throws|throws
name|Exception
block|{
name|assertChildNotFound
argument_list|(
name|allProjects
argument_list|,
literal|"non-existing"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getNonChildProject_NotFound ()
specifier|public
name|void
name|getNonChildProject_NotFound
parameter_list|()
throws|throws
name|Exception
block|{
name|Project
operator|.
name|NameKey
name|p1
init|=
name|projectOperations
operator|.
name|newProject
argument_list|()
operator|.
name|create
argument_list|()
decl_stmt|;
name|Project
operator|.
name|NameKey
name|p2
init|=
name|projectOperations
operator|.
name|newProject
argument_list|()
operator|.
name|create
argument_list|()
decl_stmt|;
name|assertChildNotFound
argument_list|(
name|p1
argument_list|,
name|p2
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getChildProject ()
specifier|public
name|void
name|getChildProject
parameter_list|()
throws|throws
name|Exception
block|{
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
name|create
argument_list|()
decl_stmt|;
name|ProjectInfo
name|childInfo
init|=
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|allProjects
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|child
argument_list|(
name|child
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertProjectInfo
argument_list|(
name|projectCache
operator|.
name|get
argument_list|(
name|child
argument_list|)
operator|.
name|getProject
argument_list|()
argument_list|,
name|childInfo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getGrandChildProject_NotFound ()
specifier|public
name|void
name|getGrandChildProject_NotFound
parameter_list|()
throws|throws
name|Exception
block|{
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
name|create
argument_list|()
decl_stmt|;
name|Project
operator|.
name|NameKey
name|grandChild
init|=
name|this
operator|.
name|projectOperations
operator|.
name|newProject
argument_list|()
operator|.
name|parent
argument_list|(
name|child
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|assertChildNotFound
argument_list|(
name|allProjects
argument_list|,
name|grandChild
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getGrandChildProjectWithRecursiveFlag ()
specifier|public
name|void
name|getGrandChildProjectWithRecursiveFlag
parameter_list|()
throws|throws
name|Exception
block|{
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
name|create
argument_list|()
decl_stmt|;
name|Project
operator|.
name|NameKey
name|grandChild
init|=
name|this
operator|.
name|projectOperations
operator|.
name|newProject
argument_list|()
operator|.
name|parent
argument_list|(
name|child
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|ProjectInfo
name|grandChildInfo
init|=
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|allProjects
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|child
argument_list|(
name|grandChild
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|get
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|assertProjectInfo
argument_list|(
name|projectCache
operator|.
name|get
argument_list|(
name|grandChild
argument_list|)
operator|.
name|getProject
argument_list|()
argument_list|,
name|grandChildInfo
argument_list|)
expr_stmt|;
block|}
DECL|method|assertChildNotFound (Project.NameKey parent, String child)
specifier|private
name|void
name|assertChildNotFound
parameter_list|(
name|Project
operator|.
name|NameKey
name|parent
parameter_list|,
name|String
name|child
parameter_list|)
throws|throws
name|Exception
block|{
name|exception
operator|.
name|expect
argument_list|(
name|ResourceNotFoundException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
name|child
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|parent
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|child
argument_list|(
name|child
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

