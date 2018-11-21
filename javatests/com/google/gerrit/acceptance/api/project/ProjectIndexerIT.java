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
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
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
name|index
operator|.
name|IndexConfig
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
name|index
operator|.
name|QueryOptions
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
name|index
operator|.
name|RefState
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
name|index
operator|.
name|project
operator|.
name|ProjectField
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
name|index
operator|.
name|project
operator|.
name|ProjectIndex
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
name|index
operator|.
name|project
operator|.
name|ProjectIndexCollection
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
name|index
operator|.
name|project
operator|.
name|ProjectIndexer
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
name|index
operator|.
name|query
operator|.
name|FieldBundle
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
name|index
operator|.
name|project
operator|.
name|StalenessChecker
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
name|ProjectConfig
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
name|java
operator|.
name|util
operator|.
name|Collection
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
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
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
name|internal
operator|.
name|storage
operator|.
name|dfs
operator|.
name|InMemoryRepository
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
name|Ref
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
DECL|class|ProjectIndexerIT
specifier|public
class|class
name|ProjectIndexerIT
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|projectIndexer
annotation|@
name|Inject
specifier|private
name|ProjectIndexer
name|projectIndexer
decl_stmt|;
DECL|field|indexes
annotation|@
name|Inject
specifier|private
name|ProjectIndexCollection
name|indexes
decl_stmt|;
DECL|field|indexConfig
annotation|@
name|Inject
specifier|private
name|IndexConfig
name|indexConfig
decl_stmt|;
DECL|field|stalenessChecker
annotation|@
name|Inject
specifier|private
name|StalenessChecker
name|stalenessChecker
decl_stmt|;
DECL|field|projectOperations
annotation|@
name|Inject
specifier|private
name|ProjectOperations
name|projectOperations
decl_stmt|;
DECL|field|FIELDS
specifier|private
specifier|static
specifier|final
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|FIELDS
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|ProjectField
operator|.
name|NAME
operator|.
name|getName
argument_list|()
argument_list|,
name|ProjectField
operator|.
name|REF_STATE
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|indexProject_indexesRefStateOfProjectAndParents ()
specifier|public
name|void
name|indexProject_indexesRefStateOfProjectAndParents
parameter_list|()
throws|throws
name|Exception
block|{
name|projectIndexer
operator|.
name|index
argument_list|(
name|project
argument_list|)
expr_stmt|;
name|ProjectIndex
name|i
init|=
name|indexes
operator|.
name|getSearchIndex
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|i
operator|.
name|getSchema
argument_list|()
operator|.
name|hasField
argument_list|(
name|ProjectField
operator|.
name|REF_STATE
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|Optional
argument_list|<
name|FieldBundle
argument_list|>
name|result
init|=
name|i
operator|.
name|getRaw
argument_list|(
name|project
argument_list|,
name|QueryOptions
operator|.
name|create
argument_list|(
name|indexConfig
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
name|FIELDS
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|result
operator|.
name|isPresent
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|Iterable
argument_list|<
name|byte
index|[]
argument_list|>
name|refState
init|=
name|result
operator|.
name|get
argument_list|()
operator|.
name|getValue
argument_list|(
name|ProjectField
operator|.
name|REF_STATE
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|refState
argument_list|)
operator|.
name|isNotEmpty
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|Project
operator|.
name|NameKey
argument_list|,
name|Collection
argument_list|<
name|RefState
argument_list|>
argument_list|>
name|states
init|=
name|RefState
operator|.
name|parseStates
argument_list|(
name|refState
argument_list|)
operator|.
name|asMap
argument_list|()
decl_stmt|;
name|fetch
argument_list|(
name|testRepo
argument_list|,
literal|"refs/meta/config:refs/meta/config"
argument_list|)
expr_stmt|;
name|Ref
name|projectConfigRef
init|=
name|testRepo
operator|.
name|getRepository
argument_list|()
operator|.
name|exactRef
argument_list|(
literal|"refs/meta/config"
argument_list|)
decl_stmt|;
name|TestRepository
argument_list|<
name|InMemoryRepository
argument_list|>
name|allProjectsRepo
init|=
name|cloneProject
argument_list|(
name|allProjects
argument_list|,
name|admin
argument_list|)
decl_stmt|;
name|fetch
argument_list|(
name|allProjectsRepo
argument_list|,
literal|"refs/meta/config:refs/meta/config"
argument_list|)
expr_stmt|;
name|Ref
name|allProjectConfigRef
init|=
name|allProjectsRepo
operator|.
name|getRepository
argument_list|()
operator|.
name|exactRef
argument_list|(
literal|"refs/meta/config"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|states
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|project
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|RefState
operator|.
name|of
argument_list|(
name|projectConfigRef
argument_list|)
argument_list|)
argument_list|,
name|allProjects
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
name|RefState
operator|.
name|of
argument_list|(
name|allProjectConfigRef
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|stalenessChecker_currentProject_notStale ()
specifier|public
name|void
name|stalenessChecker_currentProject_notStale
parameter_list|()
throws|throws
name|Exception
block|{
name|assertThat
argument_list|(
name|stalenessChecker
operator|.
name|isStale
argument_list|(
name|project
argument_list|)
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|stalenessChecker_currentProjectUpdates_isStale ()
specifier|public
name|void
name|stalenessChecker_currentProjectUpdates_isStale
parameter_list|()
throws|throws
name|Exception
block|{
name|updateProjectConfigWithoutIndexUpdate
argument_list|(
name|project
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|stalenessChecker
operator|.
name|isStale
argument_list|(
name|project
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|stalenessChecker_parentProjectUpdates_isStale ()
specifier|public
name|void
name|stalenessChecker_parentProjectUpdates_isStale
parameter_list|()
throws|throws
name|Exception
block|{
name|updateProjectConfigWithoutIndexUpdate
argument_list|(
name|allProjects
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|stalenessChecker
operator|.
name|isStale
argument_list|(
name|project
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|stalenessChecker_hierarchyChange_isStale ()
specifier|public
name|void
name|stalenessChecker_hierarchyChange_isStale
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
try|try
init|(
name|ProjectConfigUpdate
name|u
init|=
name|updateProject
argument_list|(
name|project
argument_list|)
init|)
block|{
name|u
operator|.
name|getConfig
argument_list|()
operator|.
name|getProject
argument_list|()
operator|.
name|setParentName
argument_list|(
name|p1
argument_list|)
expr_stmt|;
name|u
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|stalenessChecker
operator|.
name|isStale
argument_list|(
name|project
argument_list|)
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|updateProjectConfigWithoutIndexUpdate
argument_list|(
name|p1
argument_list|,
name|c
lambda|->
name|c
operator|.
name|getProject
argument_list|()
operator|.
name|setParentName
argument_list|(
name|p2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|stalenessChecker
operator|.
name|isStale
argument_list|(
name|project
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
block|}
DECL|method|updateProjectConfigWithoutIndexUpdate (Project.NameKey project)
specifier|private
name|void
name|updateProjectConfigWithoutIndexUpdate
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|)
throws|throws
name|Exception
block|{
name|updateProjectConfigWithoutIndexUpdate
argument_list|(
name|project
argument_list|,
name|c
lambda|->
name|c
operator|.
name|getProject
argument_list|()
operator|.
name|setDescription
argument_list|(
literal|"making it stale"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|updateProjectConfigWithoutIndexUpdate ( Project.NameKey project, Consumer<ProjectConfig> update)
specifier|private
name|void
name|updateProjectConfigWithoutIndexUpdate
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|Consumer
argument_list|<
name|ProjectConfig
argument_list|>
name|update
parameter_list|)
throws|throws
name|Exception
block|{
try|try
init|(
name|AutoCloseable
name|ignored
init|=
name|disableProjectIndex
argument_list|()
init|)
block|{
try|try
init|(
name|ProjectConfigUpdate
name|u
init|=
name|updateProject
argument_list|(
name|project
argument_list|)
init|)
block|{
name|update
operator|.
name|accept
argument_list|(
name|u
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|u
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
comment|// Drop, as we just wanted to drop the index update
return|return;
block|}
name|fail
argument_list|(
literal|"should have a UnsupportedOperationException"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

