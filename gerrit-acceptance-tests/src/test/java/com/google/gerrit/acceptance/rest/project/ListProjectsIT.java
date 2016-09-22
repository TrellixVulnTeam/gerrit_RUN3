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
name|acceptance
operator|.
name|rest
operator|.
name|project
operator|.
name|ProjectAssert
operator|.
name|assertThatNameList
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
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
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
name|Projects
operator|.
name|ListRequest
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
name|Projects
operator|.
name|ListRequest
operator|.
name|FilterType
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
name|AllUsersName
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
name|project
operator|.
name|Util
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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

begin_class
annotation|@
name|NoHttpd
DECL|class|ListProjectsIT
specifier|public
class|class
name|ListProjectsIT
extends|extends
name|AbstractDaemonTest
block|{
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
name|Exception
block|{
name|Project
operator|.
name|NameKey
name|someProject
init|=
name|createProject
argument_list|(
literal|"some-project"
argument_list|)
decl_stmt|;
name|assertThatNameList
argument_list|(
name|filter
argument_list|(
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|allProjects
argument_list|,
name|allUsers
argument_list|,
name|project
argument_list|,
name|someProject
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|listProjectsFiltersInvisibleProjects ()
specifier|public
name|void
name|listProjectsFiltersInvisibleProjects
parameter_list|()
throws|throws
name|Exception
block|{
name|setApiUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|assertThatNameList
argument_list|(
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|contains
argument_list|(
name|project
argument_list|)
expr_stmt|;
name|ProjectConfig
name|cfg
init|=
name|projectCache
operator|.
name|checkedGet
argument_list|(
name|project
argument_list|)
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|Util
operator|.
name|block
argument_list|(
name|cfg
argument_list|,
name|Permission
operator|.
name|READ
argument_list|,
name|REGISTERED_USERS
argument_list|,
literal|"refs/*"
argument_list|)
expr_stmt|;
name|saveProjectConfig
argument_list|(
name|project
argument_list|,
name|cfg
argument_list|)
expr_stmt|;
name|assertThatNameList
argument_list|(
name|filter
argument_list|(
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
operator|.
name|doesNotContain
argument_list|(
name|project
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
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|ProjectInfo
argument_list|>
name|result
init|=
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|addShowBranch
argument_list|(
literal|"master"
argument_list|)
operator|.
name|getAsMap
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|containsKey
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|ProjectInfo
name|info
init|=
name|result
operator|.
name|get
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|branches
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|branches
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|branches
operator|.
name|get
argument_list|(
literal|"master"
argument_list|)
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestProjectInput
argument_list|(
name|description
operator|=
literal|"Description of some-project"
argument_list|)
DECL|method|listProjectWithDescription ()
specifier|public
name|void
name|listProjectWithDescription
parameter_list|()
throws|throws
name|Exception
block|{
comment|// description not be included in the results by default.
name|Map
argument_list|<
name|String
argument_list|,
name|ProjectInfo
argument_list|>
name|result
init|=
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|getAsMap
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|containsKey
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
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
name|description
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|result
operator|=
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|withDescription
argument_list|(
literal|true
argument_list|)
operator|.
name|getAsMap
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|containsKey
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
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
name|description
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Description of some-project"
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
name|Exception
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
literal|"someProject"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|String
name|p
init|=
name|name
argument_list|(
literal|""
argument_list|)
decl_stmt|;
comment|// 5, plus p which was automatically created.
name|int
name|n
init|=
literal|6
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|n
operator|+
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|assertThatNameList
argument_list|(
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|withPrefix
argument_list|(
name|p
argument_list|)
operator|.
name|withLimit
argument_list|(
name|i
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|hasSize
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|i
argument_list|,
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|listProjectsWithPrefix ()
specifier|public
name|void
name|listProjectsWithPrefix
parameter_list|()
throws|throws
name|Exception
block|{
name|Project
operator|.
name|NameKey
name|someProject
init|=
name|createProject
argument_list|(
literal|"some-project"
argument_list|)
decl_stmt|;
name|Project
operator|.
name|NameKey
name|someOtherProject
init|=
name|createProject
argument_list|(
literal|"some-other-project"
argument_list|)
decl_stmt|;
name|createProject
argument_list|(
literal|"project-awesome"
argument_list|)
expr_stmt|;
name|String
name|p
init|=
name|name
argument_list|(
literal|"some"
argument_list|)
decl_stmt|;
name|assertBadRequest
argument_list|(
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|withPrefix
argument_list|(
name|p
argument_list|)
operator|.
name|withRegex
argument_list|(
literal|".*"
argument_list|)
argument_list|)
expr_stmt|;
name|assertBadRequest
argument_list|(
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|withPrefix
argument_list|(
name|p
argument_list|)
operator|.
name|withSubstring
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|assertThatNameList
argument_list|(
name|filter
argument_list|(
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|withPrefix
argument_list|(
name|p
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|someOtherProject
argument_list|,
name|someProject
argument_list|)
operator|.
name|inOrder
argument_list|()
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
name|Exception
block|{
name|Project
operator|.
name|NameKey
name|someProject
init|=
name|createProject
argument_list|(
literal|"some-project"
argument_list|)
decl_stmt|;
name|Project
operator|.
name|NameKey
name|someOtherProject
init|=
name|createProject
argument_list|(
literal|"some-other-project"
argument_list|)
decl_stmt|;
name|Project
operator|.
name|NameKey
name|projectAwesome
init|=
name|createProject
argument_list|(
literal|"project-awesome"
argument_list|)
decl_stmt|;
name|assertBadRequest
argument_list|(
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|withRegex
argument_list|(
literal|"[.*"
argument_list|)
argument_list|)
expr_stmt|;
name|assertBadRequest
argument_list|(
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|withRegex
argument_list|(
literal|".*"
argument_list|)
operator|.
name|withPrefix
argument_list|(
literal|"p"
argument_list|)
argument_list|)
expr_stmt|;
name|assertBadRequest
argument_list|(
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|withRegex
argument_list|(
literal|".*"
argument_list|)
operator|.
name|withSubstring
argument_list|(
literal|"p"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThatNameList
argument_list|(
name|filter
argument_list|(
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|withRegex
argument_list|(
literal|".*some"
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|projectAwesome
argument_list|)
expr_stmt|;
name|String
name|r
init|=
name|name
argument_list|(
literal|"some-project$"
argument_list|)
operator|.
name|replace
argument_list|(
literal|"."
argument_list|,
literal|"\\."
argument_list|)
decl_stmt|;
name|assertThatNameList
argument_list|(
name|filter
argument_list|(
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|withRegex
argument_list|(
name|r
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|someProject
argument_list|)
expr_stmt|;
name|assertThatNameList
argument_list|(
name|filter
argument_list|(
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|withRegex
argument_list|(
literal|".*"
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|allProjects
argument_list|,
name|allUsers
argument_list|,
name|project
argument_list|,
name|projectAwesome
argument_list|,
name|someOtherProject
argument_list|,
name|someProject
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|listProjectsWithStart ()
specifier|public
name|void
name|listProjectsWithStart
parameter_list|()
throws|throws
name|Exception
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
name|String
name|p
init|=
name|name
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ProjectInfo
argument_list|>
name|all
init|=
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|withPrefix
argument_list|(
name|p
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
comment|// 5, plus p which was automatically created.
name|int
name|n
init|=
literal|6
decl_stmt|;
name|assertThat
argument_list|(
name|all
argument_list|)
operator|.
name|hasSize
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|assertThatNameList
argument_list|(
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|withPrefix
argument_list|(
name|p
argument_list|)
operator|.
name|withStart
argument_list|(
name|n
operator|-
literal|1
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|Iterables
operator|.
name|getLast
argument_list|(
name|all
argument_list|)
operator|.
name|name
argument_list|)
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
name|Exception
block|{
name|Project
operator|.
name|NameKey
name|someProject
init|=
name|createProject
argument_list|(
literal|"some-project"
argument_list|)
decl_stmt|;
name|Project
operator|.
name|NameKey
name|someOtherProject
init|=
name|createProject
argument_list|(
literal|"some-other-project"
argument_list|)
decl_stmt|;
name|Project
operator|.
name|NameKey
name|projectAwesome
init|=
name|createProject
argument_list|(
literal|"project-awesome"
argument_list|)
decl_stmt|;
name|assertBadRequest
argument_list|(
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|withSubstring
argument_list|(
literal|"some"
argument_list|)
operator|.
name|withRegex
argument_list|(
literal|".*"
argument_list|)
argument_list|)
expr_stmt|;
name|assertBadRequest
argument_list|(
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|withSubstring
argument_list|(
literal|"some"
argument_list|)
operator|.
name|withPrefix
argument_list|(
literal|"some"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThatNameList
argument_list|(
name|filter
argument_list|(
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|withSubstring
argument_list|(
literal|"some"
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|projectAwesome
argument_list|,
name|someOtherProject
argument_list|,
name|someProject
argument_list|)
operator|.
name|inOrder
argument_list|()
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
name|Exception
block|{
name|Project
operator|.
name|NameKey
name|someParentProject
init|=
name|createProject
argument_list|(
literal|"some-parent-project"
argument_list|)
decl_stmt|;
name|Project
operator|.
name|NameKey
name|someChildProject
init|=
name|createProject
argument_list|(
literal|"some-child-project"
argument_list|,
name|someParentProject
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ProjectInfo
argument_list|>
name|result
init|=
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|withTree
argument_list|(
literal|true
argument_list|)
operator|.
name|getAsMap
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|containsKey
argument_list|(
name|someChildProject
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
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
operator|.
name|parent
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|someParentProject
operator|.
name|get
argument_list|()
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
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|ProjectInfo
argument_list|>
name|result
init|=
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|withType
argument_list|(
name|FilterType
operator|.
name|PERMISSIONS
argument_list|)
operator|.
name|getAsMap
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|)
operator|.
name|containsKey
argument_list|(
name|allProjects
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertThatNameList
argument_list|(
name|filter
argument_list|(
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|list
argument_list|()
operator|.
name|withType
argument_list|(
name|FilterType
operator|.
name|ALL
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|allProjects
argument_list|,
name|allUsers
argument_list|,
name|project
argument_list|)
operator|.
name|inOrder
argument_list|()
expr_stmt|;
block|}
DECL|method|assertBadRequest (ListRequest req)
specifier|private
name|void
name|assertBadRequest
parameter_list|(
name|ListRequest
name|req
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|req
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected BadRequestException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BadRequestException
name|expected
parameter_list|)
block|{
comment|// Expected.
block|}
block|}
DECL|method|filter (Iterable<ProjectInfo> infos)
specifier|private
name|Iterable
argument_list|<
name|ProjectInfo
argument_list|>
name|filter
parameter_list|(
name|Iterable
argument_list|<
name|ProjectInfo
argument_list|>
name|infos
parameter_list|)
block|{
name|String
name|prefix
init|=
name|name
argument_list|(
literal|""
argument_list|)
decl_stmt|;
return|return
name|Iterables
operator|.
name|filter
argument_list|(
name|infos
argument_list|,
name|p
lambda|->
block|{
return|return
name|p
operator|.
name|name
operator|!=
literal|null
operator|&&
operator|(
name|p
operator|.
name|name
operator|.
name|equals
argument_list|(
name|allProjects
operator|.
name|get
argument_list|()
argument_list|)
operator|||
name|p
operator|.
name|name
operator|.
name|equals
argument_list|(
name|allUsers
operator|.
name|get
argument_list|()
argument_list|)
operator|||
name|p
operator|.
name|name
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
operator|)
return|;
block|}
argument_list|)
return|;
block|}
block|}
end_class

end_unit

