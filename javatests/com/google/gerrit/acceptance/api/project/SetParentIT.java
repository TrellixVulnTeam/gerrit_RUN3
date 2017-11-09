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
DECL|package|com.google.gerrit.acceptance.api.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|api
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
name|BadRequestException
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
name|UnprocessableEntityException
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
name|config
operator|.
name|AllProjectsNameProvider
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
DECL|class|SetParentIT
specifier|public
class|class
name|SetParentIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Test
DECL|method|setParentNotAllowed ()
specifier|public
name|void
name|setParentNotAllowed
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|parent
init|=
name|createProject
argument_list|(
literal|"parent"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|setApiUser
argument_list|(
name|user
argument_list|)
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
name|parent
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|setParent ()
specifier|public
name|void
name|setParent
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|parent
init|=
name|createProject
argument_list|(
literal|"parent"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
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
name|parent
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
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
name|parent
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|parent
argument_list|)
expr_stmt|;
comment|// When the parent name is not explicitly set, it should be
comment|// set to "All-Projects".
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
name|parent
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
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
name|parent
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|AllProjectsNameProvider
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|setParentForAllProjectsNotAllowed ()
specifier|public
name|void
name|setParentForAllProjectsNotAllowed
parameter_list|()
throws|throws
name|Exception
block|{
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
literal|"cannot set parent of "
operator|+
name|AllProjectsNameProvider
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
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
name|parent
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|setParentToSelfNotAllowed ()
specifier|public
name|void
name|setParentToSelfNotAllowed
parameter_list|()
throws|throws
name|Exception
block|{
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
literal|"cannot set parent to self"
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
name|parent
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|setParentToOwnChildNotAllowed ()
specifier|public
name|void
name|setParentToOwnChildNotAllowed
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|child
init|=
name|createProject
argument_list|(
literal|"child"
argument_list|,
name|project
argument_list|,
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
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
literal|"cycle exists between"
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
name|parent
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|setParentToGrandchildNotAllowed ()
specifier|public
name|void
name|setParentToGrandchildNotAllowed
parameter_list|()
throws|throws
name|Exception
block|{
name|Project
operator|.
name|NameKey
name|child
init|=
name|createProject
argument_list|(
literal|"child"
argument_list|,
name|project
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|String
name|grandchild
init|=
name|createProject
argument_list|(
literal|"grandchild"
argument_list|,
name|child
argument_list|,
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
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
literal|"cycle exists between"
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
name|parent
argument_list|(
name|grandchild
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|setParentToNonexistentProject ()
specifier|public
name|void
name|setParentToNonexistentProject
parameter_list|()
throws|throws
name|Exception
block|{
name|exception
operator|.
name|expect
argument_list|(
name|UnprocessableEntityException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"not found"
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
name|parent
argument_list|(
literal|"non-existing"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|setParentForAllUsersMustBeAllProjects ()
specifier|public
name|void
name|setParentForAllUsersMustBeAllProjects
parameter_list|()
throws|throws
name|Exception
block|{
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|allUsers
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|parent
argument_list|(
name|allProjects
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|parent
init|=
name|createProject
argument_list|(
literal|"parent"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|exception
operator|.
name|expect
argument_list|(
name|BadRequestException
operator|.
name|class
argument_list|)
expr_stmt|;
name|exception
operator|.
name|expectMessage
argument_list|(
literal|"All-Users must inherit from All-Projects"
argument_list|)
expr_stmt|;
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|allUsers
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|parent
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

