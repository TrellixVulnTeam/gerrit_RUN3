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
DECL|package|com.google.gerrit.acceptance.ssh
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|ssh
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
name|common
operator|.
name|truth
operator|.
name|Truth
operator|.
name|assert_
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
name|project
operator|.
name|ProjectState
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
DECL|class|CreateProjectIT
specifier|public
class|class
name|CreateProjectIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Test
DECL|method|withValidGroupName ()
specifier|public
name|void
name|withValidGroupName
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|newGroupName
init|=
literal|"newGroup"
decl_stmt|;
name|adminRestSession
operator|.
name|put
argument_list|(
literal|"/groups/"
operator|+
name|newGroupName
argument_list|)
expr_stmt|;
name|String
name|newProjectName
init|=
literal|"newProject"
decl_stmt|;
name|adminSshSession
operator|.
name|exec
argument_list|(
literal|"gerrit create-project --branch master --owner "
operator|+
name|newGroupName
operator|+
literal|" "
operator|+
name|newProjectName
argument_list|)
expr_stmt|;
name|assert_
argument_list|()
operator|.
name|withFailureMessage
argument_list|(
name|adminSshSession
operator|.
name|getError
argument_list|()
argument_list|)
operator|.
name|that
argument_list|(
name|adminSshSession
operator|.
name|hasError
argument_list|()
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|ProjectState
name|projectState
init|=
name|projectCache
operator|.
name|get
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|newProjectName
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|projectState
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|withInvalidGroupName ()
specifier|public
name|void
name|withInvalidGroupName
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|newGroupName
init|=
literal|"newGroup"
decl_stmt|;
name|adminRestSession
operator|.
name|put
argument_list|(
literal|"/groups/"
operator|+
name|newGroupName
argument_list|)
expr_stmt|;
name|String
name|wrongGroupName
init|=
literal|"newG"
decl_stmt|;
name|String
name|newProjectName
init|=
literal|"newProject"
decl_stmt|;
name|adminSshSession
operator|.
name|exec
argument_list|(
literal|"gerrit create-project --branch master --owner "
operator|+
name|wrongGroupName
operator|+
literal|" "
operator|+
name|newProjectName
argument_list|)
expr_stmt|;
name|assert_
argument_list|()
operator|.
name|withFailureMessage
argument_list|(
name|adminSshSession
operator|.
name|getError
argument_list|()
argument_list|)
operator|.
name|that
argument_list|(
name|adminSshSession
operator|.
name|hasError
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|ProjectState
name|projectState
init|=
name|projectCache
operator|.
name|get
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|newProjectName
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|projectState
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

