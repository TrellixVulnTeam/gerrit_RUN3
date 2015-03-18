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
name|acceptance
operator|.
name|GitUtil
operator|.
name|cloneProject
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
name|GitUtil
operator|.
name|fetch
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
name|reviewdb
operator|.
name|client
operator|.
name|RefNames
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
name|eclipse
operator|.
name|jgit
operator|.
name|api
operator|.
name|Git
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
name|Config
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
DECL|class|ProjectLevelConfigIT
specifier|public
class|class
name|ProjectLevelConfigIT
extends|extends
name|AbstractDaemonTest
block|{
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
name|fetch
argument_list|(
name|git
argument_list|,
name|RefNames
operator|.
name|REFS_CONFIG
operator|+
literal|":refs/heads/config"
argument_list|)
expr_stmt|;
name|testRepo
operator|.
name|reset
argument_list|(
literal|"refs/heads/config"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|accessProjectSpecificConfig ()
specifier|public
name|void
name|accessProjectSpecificConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|configName
init|=
literal|"test.config"
decl_stmt|;
name|Config
name|cfg
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
name|cfg
operator|.
name|setString
argument_list|(
literal|"s1"
argument_list|,
literal|null
argument_list|,
literal|"k1"
argument_list|,
literal|"v1"
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setString
argument_list|(
literal|"s2"
argument_list|,
literal|"ss"
argument_list|,
literal|"k2"
argument_list|,
literal|"v2"
argument_list|)
expr_stmt|;
name|PushOneCommit
name|push
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|,
name|testRepo
argument_list|,
literal|"Create Project Level Config"
argument_list|,
name|configName
argument_list|,
name|cfg
operator|.
name|toText
argument_list|()
argument_list|)
decl_stmt|;
name|push
operator|.
name|to
argument_list|(
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|)
expr_stmt|;
name|ProjectState
name|state
init|=
name|projectCache
operator|.
name|get
argument_list|(
name|project
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|state
operator|.
name|getConfig
argument_list|(
name|configName
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|toText
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|cfg
operator|.
name|toText
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|nonExistingConfig ()
specifier|public
name|void
name|nonExistingConfig
parameter_list|()
block|{
name|ProjectState
name|state
init|=
name|projectCache
operator|.
name|get
argument_list|(
name|project
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|state
operator|.
name|getConfig
argument_list|(
literal|"test.config"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|toText
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|withInheritance ()
specifier|public
name|void
name|withInheritance
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|configName
init|=
literal|"test.config"
decl_stmt|;
name|Config
name|parentCfg
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
name|parentCfg
operator|.
name|setString
argument_list|(
literal|"s1"
argument_list|,
literal|null
argument_list|,
literal|"k1"
argument_list|,
literal|"parentValue1"
argument_list|)
expr_stmt|;
name|parentCfg
operator|.
name|setString
argument_list|(
literal|"s1"
argument_list|,
literal|null
argument_list|,
literal|"k2"
argument_list|,
literal|"parentValue2"
argument_list|)
expr_stmt|;
name|parentCfg
operator|.
name|setString
argument_list|(
literal|"s2"
argument_list|,
literal|"ss"
argument_list|,
literal|"k3"
argument_list|,
literal|"parentValue3"
argument_list|)
expr_stmt|;
name|parentCfg
operator|.
name|setString
argument_list|(
literal|"s2"
argument_list|,
literal|"ss"
argument_list|,
literal|"k4"
argument_list|,
literal|"parentValue4"
argument_list|)
expr_stmt|;
name|TestRepository
argument_list|<
name|?
argument_list|>
name|parentTestRepo
init|=
name|cloneProject
argument_list|(
name|allProjects
argument_list|,
name|sshSession
argument_list|)
decl_stmt|;
name|Git
name|parentGit
init|=
name|Git
operator|.
name|wrap
argument_list|(
name|parentTestRepo
operator|.
name|getRepository
argument_list|()
argument_list|)
decl_stmt|;
name|fetch
argument_list|(
name|parentGit
argument_list|,
name|RefNames
operator|.
name|REFS_CONFIG
operator|+
literal|":refs/heads/config"
argument_list|)
expr_stmt|;
name|parentTestRepo
operator|.
name|reset
argument_list|(
literal|"refs/heads/config"
argument_list|)
expr_stmt|;
name|PushOneCommit
name|push
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|,
name|parentTestRepo
argument_list|,
literal|"Create Project Level Config"
argument_list|,
name|configName
argument_list|,
name|parentCfg
operator|.
name|toText
argument_list|()
argument_list|)
decl_stmt|;
name|push
operator|.
name|to
argument_list|(
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|)
expr_stmt|;
name|Config
name|cfg
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
name|cfg
operator|.
name|setString
argument_list|(
literal|"s1"
argument_list|,
literal|null
argument_list|,
literal|"k1"
argument_list|,
literal|"childValue1"
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setString
argument_list|(
literal|"s2"
argument_list|,
literal|"ss"
argument_list|,
literal|"k3"
argument_list|,
literal|"childValue2"
argument_list|)
expr_stmt|;
name|push
operator|=
name|pushFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|,
name|testRepo
argument_list|,
literal|"Create Project Level Config"
argument_list|,
name|configName
argument_list|,
name|cfg
operator|.
name|toText
argument_list|()
argument_list|)
expr_stmt|;
name|push
operator|.
name|to
argument_list|(
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|)
expr_stmt|;
name|ProjectState
name|state
init|=
name|projectCache
operator|.
name|get
argument_list|(
name|project
argument_list|)
decl_stmt|;
name|Config
name|expectedCfg
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
name|expectedCfg
operator|.
name|setString
argument_list|(
literal|"s1"
argument_list|,
literal|null
argument_list|,
literal|"k1"
argument_list|,
literal|"childValue1"
argument_list|)
expr_stmt|;
name|expectedCfg
operator|.
name|setString
argument_list|(
literal|"s1"
argument_list|,
literal|null
argument_list|,
literal|"k2"
argument_list|,
literal|"parentValue2"
argument_list|)
expr_stmt|;
name|expectedCfg
operator|.
name|setString
argument_list|(
literal|"s2"
argument_list|,
literal|"ss"
argument_list|,
literal|"k3"
argument_list|,
literal|"childValue2"
argument_list|)
expr_stmt|;
name|expectedCfg
operator|.
name|setString
argument_list|(
literal|"s2"
argument_list|,
literal|"ss"
argument_list|,
literal|"k4"
argument_list|,
literal|"parentValue4"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|state
operator|.
name|getConfig
argument_list|(
name|configName
argument_list|)
operator|.
name|getWithInheritance
argument_list|()
operator|.
name|toText
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expectedCfg
operator|.
name|toText
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|state
operator|.
name|getConfig
argument_list|(
name|configName
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|toText
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|cfg
operator|.
name|toText
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

