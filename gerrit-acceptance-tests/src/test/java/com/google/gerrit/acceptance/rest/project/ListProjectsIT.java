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
name|gerrit
operator|.
name|acceptance
operator|.
name|GitUtil
operator|.
name|createProject
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
name|acceptance
operator|.
name|rest
operator|.
name|project
operator|.
name|ProjectAssert
operator|.
name|assertProjects
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
name|assertNotNull
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
name|assertNull
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
name|extensions
operator|.
name|api
operator|.
name|projects
operator|.
name|ProjectInput
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
name|AllProjectsName
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
name|AllUsersName
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|reflect
operator|.
name|TypeToken
import|;
end_import

begin_import
import|import
name|com
operator|.
name|jcraft
operator|.
name|jsch
operator|.
name|JSchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpStatus
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_class
DECL|class|ListProjectsIT
specifier|public
class|class
name|ListProjectsIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Inject
DECL|field|allProjects
specifier|private
name|AllProjectsName
name|allProjects
decl_stmt|;
annotation|@
name|Inject
DECL|field|allUsers
specifier|private
name|AllUsersName
name|allUsers
decl_stmt|;
annotation|@
name|Test
DECL|method|listProjects ()
specifier|public
name|void
name|listProjects
parameter_list|()
throws|throws
name|IOException
throws|,
name|JSchException
block|{
name|Project
operator|.
name|NameKey
name|someProject
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"some-project"
argument_list|)
decl_stmt|;
name|createProject
argument_list|(
name|sshSession
argument_list|,
name|someProject
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|RestResponse
name|r
init|=
name|GET
argument_list|(
literal|"/projects/"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ProjectInfo
argument_list|>
name|result
init|=
name|toProjectInfoMap
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertProjects
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|allUsers
argument_list|,
name|someProject
argument_list|,
name|project
argument_list|)
argument_list|,
name|result
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|listProjectsWithBranch ()
specifier|public
name|void
name|listProjectsWithBranch
parameter_list|()
throws|throws
name|IOException
throws|,
name|JSchException
block|{
name|RestResponse
name|r
init|=
name|GET
argument_list|(
literal|"/projects/?b=master"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ProjectInfo
argument_list|>
name|result
init|=
name|toProjectInfoMap
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
operator|.
name|get
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|result
operator|.
name|get
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|branches
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|get
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|branches
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|result
operator|.
name|get
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|branches
operator|.
name|get
argument_list|(
literal|"master"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|listProjectWithDescription ()
specifier|public
name|void
name|listProjectWithDescription
parameter_list|()
throws|throws
name|RestApiException
throws|,
name|IOException
block|{
name|ProjectInput
name|projectInput
init|=
operator|new
name|ProjectInput
argument_list|()
decl_stmt|;
name|projectInput
operator|.
name|name
operator|=
literal|"some-project"
expr_stmt|;
name|projectInput
operator|.
name|description
operator|=
literal|"Description of some-project"
expr_stmt|;
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|projectInput
operator|.
name|name
argument_list|)
operator|.
name|create
argument_list|(
name|projectInput
argument_list|)
expr_stmt|;
comment|// description not be included in the results by default.
name|RestResponse
name|r
init|=
name|GET
argument_list|(
literal|"/projects/"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ProjectInfo
argument_list|>
name|result
init|=
name|toProjectInfoMap
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
operator|.
name|get
argument_list|(
name|projectInput
operator|.
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|result
operator|.
name|get
argument_list|(
name|projectInput
operator|.
name|name
argument_list|)
operator|.
name|description
argument_list|)
expr_stmt|;
name|r
operator|=
name|GET
argument_list|(
literal|"/projects/?d"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|toProjectInfoMap
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|result
operator|.
name|get
argument_list|(
name|projectInput
operator|.
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|projectInput
operator|.
name|description
argument_list|,
name|result
operator|.
name|get
argument_list|(
name|projectInput
operator|.
name|name
argument_list|)
operator|.
name|description
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|listProjectsWithLimit ()
specifier|public
name|void
name|listProjectsWithLimit
parameter_list|()
throws|throws
name|IOException
throws|,
name|JSchException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|createProject
argument_list|(
name|sshSession
argument_list|,
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"someProject"
operator|+
name|i
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|RestResponse
name|r
init|=
name|GET
argument_list|(
literal|"/projects/"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ProjectInfo
argument_list|>
name|result
init|=
name|toProjectInfoMap
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// 5 plus 2 existing projects: p and
comment|// All-Users
name|r
operator|=
name|GET
argument_list|(
literal|"/projects/?n=2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|toProjectInfoMap
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|listProjectsWithPrefix ()
specifier|public
name|void
name|listProjectsWithPrefix
parameter_list|()
throws|throws
name|IOException
throws|,
name|JSchException
block|{
name|Project
operator|.
name|NameKey
name|someProject
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"some-project"
argument_list|)
decl_stmt|;
name|createProject
argument_list|(
name|sshSession
argument_list|,
name|someProject
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|Project
operator|.
name|NameKey
name|someOtherProject
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"some-other-project"
argument_list|)
decl_stmt|;
name|createProject
argument_list|(
name|sshSession
argument_list|,
name|someOtherProject
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|Project
operator|.
name|NameKey
name|projectAwesome
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"project-awesome"
argument_list|)
decl_stmt|;
name|createProject
argument_list|(
name|sshSession
argument_list|,
name|projectAwesome
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_BAD_REQUEST
argument_list|,
name|GET
argument_list|(
literal|"/projects/?p=some&r=.*"
argument_list|)
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_BAD_REQUEST
argument_list|,
name|GET
argument_list|(
literal|"/projects/?p=some&m=some"
argument_list|)
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|RestResponse
name|r
init|=
name|GET
argument_list|(
literal|"/projects/?p=some"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ProjectInfo
argument_list|>
name|result
init|=
name|toProjectInfoMap
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertProjects
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|someProject
argument_list|,
name|someOtherProject
argument_list|)
argument_list|,
name|result
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|listProjectsWithRegex ()
specifier|public
name|void
name|listProjectsWithRegex
parameter_list|()
throws|throws
name|IOException
throws|,
name|JSchException
block|{
name|Project
operator|.
name|NameKey
name|someProject
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"some-project"
argument_list|)
decl_stmt|;
name|createProject
argument_list|(
name|sshSession
argument_list|,
name|someProject
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|Project
operator|.
name|NameKey
name|someOtherProject
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"some-other-project"
argument_list|)
decl_stmt|;
name|createProject
argument_list|(
name|sshSession
argument_list|,
name|someOtherProject
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|Project
operator|.
name|NameKey
name|projectAwesome
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"project-awesome"
argument_list|)
decl_stmt|;
name|createProject
argument_list|(
name|sshSession
argument_list|,
name|projectAwesome
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_BAD_REQUEST
argument_list|,
name|GET
argument_list|(
literal|"/projects/?r=[.*some"
argument_list|)
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_BAD_REQUEST
argument_list|,
name|GET
argument_list|(
literal|"/projects/?r=.*&p=s"
argument_list|)
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_BAD_REQUEST
argument_list|,
name|GET
argument_list|(
literal|"/projects/?r=.*&m=s"
argument_list|)
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|RestResponse
name|r
init|=
name|GET
argument_list|(
literal|"/projects/?r=.*some"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ProjectInfo
argument_list|>
name|result
init|=
name|toProjectInfoMap
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertProjects
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|projectAwesome
argument_list|)
argument_list|,
name|result
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|=
name|GET
argument_list|(
literal|"/projects/?r=some-project$"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|toProjectInfoMap
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertProjects
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|someProject
argument_list|)
argument_list|,
name|result
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|r
operator|=
name|GET
argument_list|(
literal|"/projects/?r=.*"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|toProjectInfoMap
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertProjects
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|someProject
argument_list|,
name|someOtherProject
argument_list|,
name|projectAwesome
argument_list|,
name|project
argument_list|,
name|allUsers
argument_list|)
argument_list|,
name|result
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|listProjectsWithSkip ()
specifier|public
name|void
name|listProjectsWithSkip
parameter_list|()
throws|throws
name|IOException
throws|,
name|JSchException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|createProject
argument_list|(
name|sshSession
argument_list|,
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"someProject"
operator|+
name|i
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|RestResponse
name|r
init|=
name|GET
argument_list|(
literal|"/projects/"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ProjectInfo
argument_list|>
name|result
init|=
name|toProjectInfoMap
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// 5 plus 2 existing projects: p and
comment|// All-Users
name|r
operator|=
name|GET
argument_list|(
literal|"/projects/?S=6"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|toProjectInfoMap
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|listProjectsWithSubstring ()
specifier|public
name|void
name|listProjectsWithSubstring
parameter_list|()
throws|throws
name|IOException
throws|,
name|JSchException
block|{
name|Project
operator|.
name|NameKey
name|someProject
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"some-project"
argument_list|)
decl_stmt|;
name|createProject
argument_list|(
name|sshSession
argument_list|,
name|someProject
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|Project
operator|.
name|NameKey
name|someOtherProject
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"some-other-project"
argument_list|)
decl_stmt|;
name|createProject
argument_list|(
name|sshSession
argument_list|,
name|someOtherProject
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|Project
operator|.
name|NameKey
name|projectAwesome
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"project-awesome"
argument_list|)
decl_stmt|;
name|createProject
argument_list|(
name|sshSession
argument_list|,
name|projectAwesome
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_BAD_REQUEST
argument_list|,
name|GET
argument_list|(
literal|"/projects/?m=some&r=.*"
argument_list|)
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_BAD_REQUEST
argument_list|,
name|GET
argument_list|(
literal|"/projects/?m=some&p=some"
argument_list|)
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|RestResponse
name|r
init|=
name|GET
argument_list|(
literal|"/projects/?m=some"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ProjectInfo
argument_list|>
name|result
init|=
name|toProjectInfoMap
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertProjects
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|someProject
argument_list|,
name|someOtherProject
argument_list|,
name|projectAwesome
argument_list|)
argument_list|,
name|result
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|listProjectsWithTree ()
specifier|public
name|void
name|listProjectsWithTree
parameter_list|()
throws|throws
name|IOException
throws|,
name|JSchException
block|{
name|Project
operator|.
name|NameKey
name|someParentProject
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"some-parent-project"
argument_list|)
decl_stmt|;
name|createProject
argument_list|(
name|sshSession
argument_list|,
name|someParentProject
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|Project
operator|.
name|NameKey
name|someChildProject
init|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"some-child-project"
argument_list|)
decl_stmt|;
name|createProject
argument_list|(
name|sshSession
argument_list|,
name|someChildProject
operator|.
name|get
argument_list|()
argument_list|,
name|someParentProject
argument_list|)
expr_stmt|;
name|RestResponse
name|r
init|=
name|GET
argument_list|(
literal|"/projects/?tree"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ProjectInfo
argument_list|>
name|result
init|=
name|toProjectInfoMap
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
operator|.
name|get
argument_list|(
name|someChildProject
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|someParentProject
operator|.
name|get
argument_list|()
argument_list|,
name|result
operator|.
name|get
argument_list|(
name|someChildProject
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|parent
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|listProjectWithType ()
specifier|public
name|void
name|listProjectWithType
parameter_list|()
throws|throws
name|RestApiException
throws|,
name|IOException
block|{
name|RestResponse
name|r
init|=
name|GET
argument_list|(
literal|"/projects/?type=PERMISSIONS"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ProjectInfo
argument_list|>
name|result
init|=
name|toProjectInfoMap
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|result
operator|.
name|get
argument_list|(
name|allProjects
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|r
operator|=
name|GET
argument_list|(
literal|"/projects/?type=ALL"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|HttpStatus
operator|.
name|SC_OK
argument_list|,
name|r
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|toProjectInfoMap
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|result
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertProjects
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|allProjects
argument_list|,
name|allUsers
argument_list|,
name|project
argument_list|)
argument_list|,
name|result
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|toProjectInfoMap (RestResponse r)
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|ProjectInfo
argument_list|>
name|toProjectInfoMap
parameter_list|(
name|RestResponse
name|r
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|ProjectInfo
argument_list|>
name|result
init|=
name|newGson
argument_list|()
operator|.
name|fromJson
argument_list|(
name|r
operator|.
name|getReader
argument_list|()
argument_list|,
operator|new
name|TypeToken
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|ProjectInfo
argument_list|>
argument_list|>
argument_list|()
block|{}
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
DECL|method|GET (String endpoint)
specifier|private
name|RestResponse
name|GET
parameter_list|(
name|String
name|endpoint
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|adminSession
operator|.
name|get
argument_list|(
name|endpoint
argument_list|)
return|;
block|}
block|}
end_class

end_unit

