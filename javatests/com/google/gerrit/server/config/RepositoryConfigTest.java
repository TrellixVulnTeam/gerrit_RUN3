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
DECL|package|com.google.gerrit.server.config
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|config
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
name|Truth8
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
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|client
operator|.
name|SubmitType
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
name|testing
operator|.
name|GerritBaseTests
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
DECL|class|RepositoryConfigTest
specifier|public
class|class
name|RepositoryConfigTest
extends|extends
name|GerritBaseTests
block|{
DECL|field|cfg
specifier|private
name|Config
name|cfg
decl_stmt|;
DECL|field|repoCfg
specifier|private
name|RepositoryConfig
name|repoCfg
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
name|cfg
operator|=
operator|new
name|Config
argument_list|()
expr_stmt|;
name|repoCfg
operator|=
operator|new
name|RepositoryConfig
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|defaultSubmitTypeWhenNotConfigured ()
specifier|public
name|void
name|defaultSubmitTypeWhenNotConfigured
parameter_list|()
block|{
comment|// Check expected value explicitly rather than depending on constant.
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getDefaultSubmitType
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"someProject"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SubmitType
operator|.
name|INHERIT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|defaultSubmitTypeForStarFilter ()
specifier|public
name|void
name|defaultSubmitTypeForStarFilter
parameter_list|()
block|{
name|configureDefaultSubmitType
argument_list|(
literal|"*"
argument_list|,
name|SubmitType
operator|.
name|CHERRY_PICK
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getDefaultSubmitType
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"someProject"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SubmitType
operator|.
name|CHERRY_PICK
argument_list|)
expr_stmt|;
name|configureDefaultSubmitType
argument_list|(
literal|"*"
argument_list|,
name|SubmitType
operator|.
name|FAST_FORWARD_ONLY
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getDefaultSubmitType
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"someProject"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SubmitType
operator|.
name|FAST_FORWARD_ONLY
argument_list|)
expr_stmt|;
name|configureDefaultSubmitType
argument_list|(
literal|"*"
argument_list|,
name|SubmitType
operator|.
name|REBASE_IF_NECESSARY
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getDefaultSubmitType
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"someProject"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SubmitType
operator|.
name|REBASE_IF_NECESSARY
argument_list|)
expr_stmt|;
name|configureDefaultSubmitType
argument_list|(
literal|"*"
argument_list|,
name|SubmitType
operator|.
name|REBASE_ALWAYS
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getDefaultSubmitType
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"someProject"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SubmitType
operator|.
name|REBASE_ALWAYS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|defaultSubmitTypeForSpecificFilter ()
specifier|public
name|void
name|defaultSubmitTypeForSpecificFilter
parameter_list|()
block|{
name|configureDefaultSubmitType
argument_list|(
literal|"someProject"
argument_list|,
name|SubmitType
operator|.
name|CHERRY_PICK
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getDefaultSubmitType
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"someOtherProject"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|RepositoryConfig
operator|.
name|DEFAULT_SUBMIT_TYPE
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getDefaultSubmitType
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"someProject"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SubmitType
operator|.
name|CHERRY_PICK
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|defaultSubmitTypeForStartWithFilter ()
specifier|public
name|void
name|defaultSubmitTypeForStartWithFilter
parameter_list|()
block|{
name|configureDefaultSubmitType
argument_list|(
literal|"somePath/somePath/*"
argument_list|,
name|SubmitType
operator|.
name|REBASE_IF_NECESSARY
argument_list|)
expr_stmt|;
name|configureDefaultSubmitType
argument_list|(
literal|"somePath/*"
argument_list|,
name|SubmitType
operator|.
name|CHERRY_PICK
argument_list|)
expr_stmt|;
name|configureDefaultSubmitType
argument_list|(
literal|"*"
argument_list|,
name|SubmitType
operator|.
name|MERGE_ALWAYS
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getDefaultSubmitType
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"someProject"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SubmitType
operator|.
name|MERGE_ALWAYS
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getDefaultSubmitType
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"somePath/someProject"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SubmitType
operator|.
name|CHERRY_PICK
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getDefaultSubmitType
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"somePath/somePath/someProject"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|SubmitType
operator|.
name|REBASE_IF_NECESSARY
argument_list|)
expr_stmt|;
block|}
DECL|method|configureDefaultSubmitType (String projectFilter, SubmitType submitType)
specifier|private
name|void
name|configureDefaultSubmitType
parameter_list|(
name|String
name|projectFilter
parameter_list|,
name|SubmitType
name|submitType
parameter_list|)
block|{
name|cfg
operator|.
name|setString
argument_list|(
name|RepositoryConfig
operator|.
name|SECTION_NAME
argument_list|,
name|projectFilter
argument_list|,
name|RepositoryConfig
operator|.
name|DEFAULT_SUBMIT_TYPE_NAME
argument_list|,
name|submitType
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|ownerGroupsWhenNotConfigured ()
specifier|public
name|void
name|ownerGroupsWhenNotConfigured
parameter_list|()
block|{
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getOwnerGroups
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"someProject"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|ownerGroupsForStarFilter ()
specifier|public
name|void
name|ownerGroupsForStarFilter
parameter_list|()
block|{
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|ownerGroups
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"group1"
argument_list|,
literal|"group2"
argument_list|)
decl_stmt|;
name|configureOwnerGroups
argument_list|(
literal|"*"
argument_list|,
name|ownerGroups
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getOwnerGroups
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"someProject"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|ownerGroups
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|ownerGroupsForSpecificFilter ()
specifier|public
name|void
name|ownerGroupsForSpecificFilter
parameter_list|()
block|{
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|ownerGroups
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"group1"
argument_list|,
literal|"group2"
argument_list|)
decl_stmt|;
name|configureOwnerGroups
argument_list|(
literal|"someProject"
argument_list|,
name|ownerGroups
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getOwnerGroups
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"someOtherProject"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getOwnerGroups
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"someProject"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|ownerGroups
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|ownerGroupsForStartWithFilter ()
specifier|public
name|void
name|ownerGroupsForStartWithFilter
parameter_list|()
block|{
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|ownerGroups1
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"group1"
argument_list|)
decl_stmt|;
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|ownerGroups2
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"group2"
argument_list|)
decl_stmt|;
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|ownerGroups3
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"group3"
argument_list|)
decl_stmt|;
name|configureOwnerGroups
argument_list|(
literal|"*"
argument_list|,
name|ownerGroups1
argument_list|)
expr_stmt|;
name|configureOwnerGroups
argument_list|(
literal|"somePath/*"
argument_list|,
name|ownerGroups2
argument_list|)
expr_stmt|;
name|configureOwnerGroups
argument_list|(
literal|"somePath/somePath/*"
argument_list|,
name|ownerGroups3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getOwnerGroups
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"someProject"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|ownerGroups1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getOwnerGroups
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"somePath/someProject"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|ownerGroups2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getOwnerGroups
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"somePath/somePath/someProject"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|ownerGroups3
argument_list|)
expr_stmt|;
block|}
DECL|method|configureOwnerGroups (String projectFilter, List<String> ownerGroups)
specifier|private
name|void
name|configureOwnerGroups
parameter_list|(
name|String
name|projectFilter
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|ownerGroups
parameter_list|)
block|{
name|cfg
operator|.
name|setStringList
argument_list|(
name|RepositoryConfig
operator|.
name|SECTION_NAME
argument_list|,
name|projectFilter
argument_list|,
name|RepositoryConfig
operator|.
name|OWNER_GROUP_NAME
argument_list|,
name|ownerGroups
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|basePathWhenNotConfigured ()
specifier|public
name|void
name|basePathWhenNotConfigured
parameter_list|()
block|{
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getBasePath
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"someProject"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|basePathForStarFilter ()
specifier|public
name|void
name|basePathForStarFilter
parameter_list|()
block|{
name|String
name|basePath
init|=
literal|"/someAbsolutePath/someDirectory"
decl_stmt|;
name|configureBasePath
argument_list|(
literal|"*"
argument_list|,
name|basePath
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getBasePath
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"someProject"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|basePath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|basePathForSpecificFilter ()
specifier|public
name|void
name|basePathForSpecificFilter
parameter_list|()
block|{
name|String
name|basePath
init|=
literal|"/someAbsolutePath/someDirectory"
decl_stmt|;
name|configureBasePath
argument_list|(
literal|"someProject"
argument_list|,
name|basePath
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getBasePath
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"someOtherProject"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getBasePath
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"someProject"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|basePath
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|basePathForStartWithFilter ()
specifier|public
name|void
name|basePathForStartWithFilter
parameter_list|()
block|{
name|String
name|basePath1
init|=
literal|"/someAbsolutePath1/someDirectory"
decl_stmt|;
name|String
name|basePath2
init|=
literal|"someRelativeDirectory2"
decl_stmt|;
name|String
name|basePath3
init|=
literal|"/someAbsolutePath3/someDirectory"
decl_stmt|;
name|String
name|basePath4
init|=
literal|"/someAbsolutePath4/someDirectory"
decl_stmt|;
name|configureBasePath
argument_list|(
literal|"pro*"
argument_list|,
name|basePath1
argument_list|)
expr_stmt|;
name|configureBasePath
argument_list|(
literal|"project/project/*"
argument_list|,
name|basePath2
argument_list|)
expr_stmt|;
name|configureBasePath
argument_list|(
literal|"project/*"
argument_list|,
name|basePath3
argument_list|)
expr_stmt|;
name|configureBasePath
argument_list|(
literal|"*"
argument_list|,
name|basePath4
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getBasePath
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"project1"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|basePath1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getBasePath
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"project/project/someProject"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|basePath2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getBasePath
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"project/someProject"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|basePath3
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getBasePath
argument_list|(
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"someProject"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|basePath4
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|allBasePath ()
specifier|public
name|void
name|allBasePath
parameter_list|()
block|{
name|ImmutableList
argument_list|<
name|Path
argument_list|>
name|allBasePaths
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"/someBasePath1"
argument_list|)
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
literal|"/someBasePath2"
argument_list|)
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
literal|"/someBasePath2"
argument_list|)
argument_list|)
decl_stmt|;
name|configureBasePath
argument_list|(
literal|"*"
argument_list|,
name|allBasePaths
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|configureBasePath
argument_list|(
literal|"project/*"
argument_list|,
name|allBasePaths
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|configureBasePath
argument_list|(
literal|"project/project/*"
argument_list|,
name|allBasePaths
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|repoCfg
operator|.
name|getAllBasePaths
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|allBasePaths
argument_list|)
expr_stmt|;
block|}
DECL|method|configureBasePath (String projectFilter, String basePath)
specifier|private
name|void
name|configureBasePath
parameter_list|(
name|String
name|projectFilter
parameter_list|,
name|String
name|basePath
parameter_list|)
block|{
name|cfg
operator|.
name|setString
argument_list|(
name|RepositoryConfig
operator|.
name|SECTION_NAME
argument_list|,
name|projectFilter
argument_list|,
name|RepositoryConfig
operator|.
name|BASE_PATH_NAME
argument_list|,
name|basePath
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

