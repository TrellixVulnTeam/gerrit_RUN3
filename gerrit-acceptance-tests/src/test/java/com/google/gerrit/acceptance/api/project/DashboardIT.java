begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
name|api
operator|.
name|projects
operator|.
name|DashboardInfo
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
name|server
operator|.
name|project
operator|.
name|DashboardsCollection
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
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Repository
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
name|revwalk
operator|.
name|RevCommit
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
DECL|class|DashboardIT
specifier|public
class|class
name|DashboardIT
extends|extends
name|AbstractDaemonTest
block|{
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|allow
argument_list|(
literal|"refs/meta/dashboards/*"
argument_list|,
name|Permission
operator|.
name|CREATE
argument_list|,
name|REGISTERED_USERS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|defaultDashboardDoesNotExist ()
specifier|public
name|void
name|defaultDashboardDoesNotExist
parameter_list|()
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
name|defaultDashboard
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|dashboardDoesNotExist ()
specifier|public
name|void
name|dashboardDoesNotExist
parameter_list|()
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
name|dashboard
argument_list|(
literal|"my:dashboard"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|getDashboard ()
specifier|public
name|void
name|getDashboard
parameter_list|()
throws|throws
name|Exception
block|{
name|assertThat
argument_list|(
name|dashboards
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|DashboardInfo
name|info
init|=
name|createDashboard
argument_list|(
name|DashboardsCollection
operator|.
name|DEFAULT_DASHBOARD_NAME
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
name|DashboardInfo
name|result
init|=
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
name|dashboard
argument_list|(
name|info
operator|.
name|id
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|id
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|info
operator|.
name|id
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|path
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|info
operator|.
name|path
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|ref
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|info
operator|.
name|ref
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|project
argument_list|)
operator|.
name|isEqualTo
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
name|definingProject
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|dashboards
argument_list|()
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|setDefaultDashboard ()
specifier|public
name|void
name|setDefaultDashboard
parameter_list|()
throws|throws
name|Exception
block|{
name|DashboardInfo
name|info
init|=
name|createDashboard
argument_list|(
name|DashboardsCollection
operator|.
name|DEFAULT_DASHBOARD_NAME
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|isDefault
argument_list|)
operator|.
name|isNull
argument_list|()
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
name|dashboard
argument_list|(
name|info
operator|.
name|id
argument_list|)
operator|.
name|setDefault
argument_list|()
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
name|dashboard
argument_list|(
name|info
operator|.
name|id
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|isDefault
argument_list|)
operator|.
name|isTrue
argument_list|()
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
name|defaultDashboard
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|id
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|info
operator|.
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|setDefaultDashboardByProject ()
specifier|public
name|void
name|setDefaultDashboardByProject
parameter_list|()
throws|throws
name|Exception
block|{
name|DashboardInfo
name|info
init|=
name|createDashboard
argument_list|(
name|DashboardsCollection
operator|.
name|DEFAULT_DASHBOARD_NAME
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|isDefault
argument_list|)
operator|.
name|isNull
argument_list|()
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
name|defaultDashboard
argument_list|(
name|info
operator|.
name|id
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
name|dashboard
argument_list|(
name|info
operator|.
name|id
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|isDefault
argument_list|)
operator|.
name|isTrue
argument_list|()
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
name|defaultDashboard
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|id
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|info
operator|.
name|id
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|replaceDefaultDashboard ()
specifier|public
name|void
name|replaceDefaultDashboard
parameter_list|()
throws|throws
name|Exception
block|{
name|DashboardInfo
name|d1
init|=
name|createDashboard
argument_list|(
name|DashboardsCollection
operator|.
name|DEFAULT_DASHBOARD_NAME
argument_list|,
literal|"test1"
argument_list|)
decl_stmt|;
name|DashboardInfo
name|d2
init|=
name|createDashboard
argument_list|(
name|DashboardsCollection
operator|.
name|DEFAULT_DASHBOARD_NAME
argument_list|,
literal|"test2"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|d1
operator|.
name|isDefault
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|d2
operator|.
name|isDefault
argument_list|)
operator|.
name|isNull
argument_list|()
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
name|dashboard
argument_list|(
name|d1
operator|.
name|id
argument_list|)
operator|.
name|setDefault
argument_list|()
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
name|dashboard
argument_list|(
name|d1
operator|.
name|id
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|isDefault
argument_list|)
operator|.
name|isTrue
argument_list|()
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
name|dashboard
argument_list|(
name|d2
operator|.
name|id
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|isDefault
argument_list|)
operator|.
name|isNull
argument_list|()
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
name|defaultDashboard
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|id
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|d1
operator|.
name|id
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
name|dashboard
argument_list|(
name|d2
operator|.
name|id
argument_list|)
operator|.
name|setDefault
argument_list|()
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
name|defaultDashboard
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|id
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|d2
operator|.
name|id
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
name|dashboard
argument_list|(
name|d1
operator|.
name|id
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|isDefault
argument_list|)
operator|.
name|isNull
argument_list|()
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
name|dashboard
argument_list|(
name|d2
operator|.
name|id
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|isDefault
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|cannotGetDashboardWithInheritedForNonDefault ()
specifier|public
name|void
name|cannotGetDashboardWithInheritedForNonDefault
parameter_list|()
throws|throws
name|Exception
block|{
name|DashboardInfo
name|info
init|=
name|createDashboard
argument_list|(
name|DashboardsCollection
operator|.
name|DEFAULT_DASHBOARD_NAME
argument_list|,
literal|"test"
argument_list|)
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
literal|"inherited flag can only be used with default"
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
name|dashboard
argument_list|(
name|info
operator|.
name|id
argument_list|)
operator|.
name|get
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|dashboards ()
specifier|private
name|List
argument_list|<
name|DashboardInfo
argument_list|>
name|dashboards
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
name|project
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|dashboards
argument_list|()
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|createDashboard (String ref, String path)
specifier|private
name|DashboardInfo
name|createDashboard
parameter_list|(
name|String
name|ref
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
name|DashboardInfo
name|info
init|=
name|DashboardsCollection
operator|.
name|newDashboardInfo
argument_list|(
name|ref
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|String
name|canonicalRef
init|=
name|DashboardsCollection
operator|.
name|normalizeDashboardRef
argument_list|(
name|info
operator|.
name|ref
argument_list|)
decl_stmt|;
try|try
block|{
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
name|branch
argument_list|(
name|canonicalRef
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
catch|catch
parameter_list|(
name|ResourceConflictException
name|e
parameter_list|)
block|{
comment|// The branch already exists if this method has already been called once.
if|if
condition|(
operator|!
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"already exists"
argument_list|)
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
try|try
init|(
name|Repository
name|r
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|project
argument_list|)
init|)
block|{
name|TestRepository
argument_list|<
name|Repository
argument_list|>
operator|.
name|CommitBuilder
name|cb
init|=
operator|new
name|TestRepository
argument_list|<>
argument_list|(
name|r
argument_list|)
operator|.
name|branch
argument_list|(
name|canonicalRef
argument_list|)
operator|.
name|commit
argument_list|()
decl_stmt|;
name|String
name|content
init|=
literal|"[dashboard]\n"
operator|+
literal|"Description = Test\n"
operator|+
literal|"foreach = owner:self\n"
operator|+
literal|"[section \"Mine\"]\n"
operator|+
literal|"query = is:open"
decl_stmt|;
name|cb
operator|.
name|add
argument_list|(
name|info
operator|.
name|path
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|RevCommit
name|c
init|=
name|cb
operator|.
name|create
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
name|commit
argument_list|(
name|c
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|info
return|;
block|}
block|}
end_class

end_unit

