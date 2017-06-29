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
DECL|package|com.google.gerrit.acceptance.pgm
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|pgm
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
name|collect
operator|.
name|ImmutableMap
operator|.
name|toImmutableMap
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
name|ImmutableSet
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
name|io
operator|.
name|MoreFiles
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
name|io
operator|.
name|RecursiveDeleteOption
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
name|StandaloneSiteTest
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
name|pgm
operator|.
name|IndexUpgradeController
operator|.
name|UpgradeAttempt
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
name|GerritApi
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
name|ChangeInput
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
name|Change
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
name|GerritIndexStatus
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
name|change
operator|.
name|ChangeIndexCollection
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
name|change
operator|.
name|ChangeSchemaDefinitions
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
name|InternalChangeQuery
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
name|Provider
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
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|storage
operator|.
name|file
operator|.
name|FileBasedConfig
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
name|util
operator|.
name|FS
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
DECL|class|ReindexIT
specifier|public
class|class
name|ReindexIT
extends|extends
name|StandaloneSiteTest
block|{
DECL|field|CHANGES
specifier|private
specifier|static
specifier|final
name|String
name|CHANGES
init|=
name|ChangeSchemaDefinitions
operator|.
name|NAME
decl_stmt|;
DECL|field|project
specifier|private
name|Project
operator|.
name|NameKey
name|project
decl_stmt|;
DECL|field|changeId
specifier|private
name|String
name|changeId
decl_stmt|;
annotation|@
name|Test
DECL|method|reindexFromScratch ()
specifier|public
name|void
name|reindexFromScratch
parameter_list|()
throws|throws
name|Exception
block|{
name|setUpChange
argument_list|()
expr_stmt|;
name|MoreFiles
operator|.
name|deleteRecursively
argument_list|(
name|sitePaths
operator|.
name|index_dir
argument_list|,
name|RecursiveDeleteOption
operator|.
name|ALLOW_INSECURE
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createDirectory
argument_list|(
name|sitePaths
operator|.
name|index_dir
argument_list|)
expr_stmt|;
name|assertServerStartupFails
argument_list|()
expr_stmt|;
name|runGerrit
argument_list|(
literal|"reindex"
argument_list|,
literal|"-d"
argument_list|,
name|sitePaths
operator|.
name|site_path
operator|.
name|toString
argument_list|()
argument_list|,
literal|"--show-stack-trace"
argument_list|)
expr_stmt|;
name|assertReady
argument_list|(
name|ChangeSchemaDefinitions
operator|.
name|INSTANCE
operator|.
name|getLatest
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|ServerContext
name|ctx
init|=
name|startServer
argument_list|()
init|)
block|{
name|GerritApi
name|gApi
init|=
name|ctx
operator|.
name|getInjector
argument_list|()
operator|.
name|getInstance
argument_list|(
name|GerritApi
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|query
argument_list|(
literal|"message:Test"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|c
lambda|->
name|c
operator|.
name|changeId
argument_list|)
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|changeId
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|onlineUpgradeChanges ()
specifier|public
name|void
name|onlineUpgradeChanges
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|prevVersion
init|=
name|ChangeSchemaDefinitions
operator|.
name|INSTANCE
operator|.
name|getPrevious
argument_list|()
operator|.
name|getVersion
argument_list|()
decl_stmt|;
name|int
name|currVersion
init|=
name|ChangeSchemaDefinitions
operator|.
name|INSTANCE
operator|.
name|getLatest
argument_list|()
operator|.
name|getVersion
argument_list|()
decl_stmt|;
comment|// Before storing any changes, switch back to the previous version.
name|GerritIndexStatus
name|status
init|=
operator|new
name|GerritIndexStatus
argument_list|(
name|sitePaths
argument_list|)
decl_stmt|;
name|status
operator|.
name|setReady
argument_list|(
name|CHANGES
argument_list|,
name|currVersion
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|status
operator|.
name|setReady
argument_list|(
name|CHANGES
argument_list|,
name|prevVersion
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|status
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertReady
argument_list|(
name|prevVersion
argument_list|)
expr_stmt|;
name|setOnlineUpgradeConfig
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|setUpChange
argument_list|()
expr_stmt|;
name|setOnlineUpgradeConfig
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|IndexUpgradeController
name|u
init|=
operator|new
name|IndexUpgradeController
argument_list|(
literal|1
argument_list|)
decl_stmt|;
try|try
init|(
name|ServerContext
name|ctx
init|=
name|startServer
argument_list|(
name|u
operator|.
name|module
argument_list|()
argument_list|)
init|)
block|{
name|assertSearchVersion
argument_list|(
name|ctx
argument_list|,
name|prevVersion
argument_list|)
expr_stmt|;
name|assertWriteVersions
argument_list|(
name|ctx
argument_list|,
name|prevVersion
argument_list|,
name|currVersion
argument_list|)
expr_stmt|;
comment|// Updating and searching old schema version works.
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
init|=
name|ctx
operator|.
name|getInjector
argument_list|()
operator|.
name|getProvider
argument_list|(
name|InternalChangeQuery
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|queryProvider
operator|.
name|get
argument_list|()
operator|.
name|byKey
argument_list|(
operator|new
name|Change
operator|.
name|Key
argument_list|(
name|changeId
argument_list|)
argument_list|)
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|queryProvider
operator|.
name|get
argument_list|()
operator|.
name|byTopicOpen
argument_list|(
literal|"topic1"
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|GerritApi
name|gApi
init|=
name|ctx
operator|.
name|getInjector
argument_list|()
operator|.
name|getInstance
argument_list|(
name|GerritApi
operator|.
name|class
argument_list|)
decl_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|changeId
argument_list|)
operator|.
name|topic
argument_list|(
literal|"topic1"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|queryProvider
operator|.
name|get
argument_list|()
operator|.
name|byTopicOpen
argument_list|(
literal|"topic1"
argument_list|)
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|u
operator|.
name|runUpgrades
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|u
operator|.
name|getStartedAttempts
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|UpgradeAttempt
operator|.
name|create
argument_list|(
name|CHANGES
argument_list|,
name|prevVersion
argument_list|,
name|currVersion
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|u
operator|.
name|getSucceededAttempts
argument_list|()
argument_list|)
operator|.
name|containsExactly
argument_list|(
name|UpgradeAttempt
operator|.
name|create
argument_list|(
name|CHANGES
argument_list|,
name|prevVersion
argument_list|,
name|currVersion
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|u
operator|.
name|getFailedAttempts
argument_list|()
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertReady
argument_list|(
name|currVersion
argument_list|)
expr_stmt|;
name|assertSearchVersion
argument_list|(
name|ctx
argument_list|,
name|currVersion
argument_list|)
expr_stmt|;
name|assertWriteVersions
argument_list|(
name|ctx
argument_list|,
name|currVersion
argument_list|)
expr_stmt|;
comment|// Updating and searching new schema version works.
name|assertThat
argument_list|(
name|queryProvider
operator|.
name|get
argument_list|()
operator|.
name|byTopicOpen
argument_list|(
literal|"topic1"
argument_list|)
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|queryProvider
operator|.
name|get
argument_list|()
operator|.
name|byTopicOpen
argument_list|(
literal|"topic2"
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|id
argument_list|(
name|changeId
argument_list|)
operator|.
name|topic
argument_list|(
literal|"topic2"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|queryProvider
operator|.
name|get
argument_list|()
operator|.
name|byTopicOpen
argument_list|(
literal|"topic1"
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|queryProvider
operator|.
name|get
argument_list|()
operator|.
name|byTopicOpen
argument_list|(
literal|"topic2"
argument_list|)
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setUpChange ()
specifier|private
name|void
name|setUpChange
parameter_list|()
throws|throws
name|Exception
block|{
name|project
operator|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"project"
argument_list|)
expr_stmt|;
try|try
init|(
name|ServerContext
name|ctx
init|=
name|startServer
argument_list|()
init|)
block|{
name|GerritApi
name|gApi
init|=
name|ctx
operator|.
name|getInjector
argument_list|()
operator|.
name|getInstance
argument_list|(
name|GerritApi
operator|.
name|class
argument_list|)
decl_stmt|;
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|create
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|ChangeInput
name|in
init|=
operator|new
name|ChangeInput
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|,
literal|"master"
argument_list|,
literal|"Test change"
argument_list|)
decl_stmt|;
name|in
operator|.
name|newBranch
operator|=
literal|true
expr_stmt|;
name|changeId
operator|=
name|gApi
operator|.
name|changes
argument_list|()
operator|.
name|create
argument_list|(
name|in
argument_list|)
operator|.
name|info
argument_list|()
operator|.
name|changeId
expr_stmt|;
block|}
block|}
DECL|method|setOnlineUpgradeConfig (boolean enable)
specifier|private
name|void
name|setOnlineUpgradeConfig
parameter_list|(
name|boolean
name|enable
parameter_list|)
throws|throws
name|Exception
block|{
name|FileBasedConfig
name|cfg
init|=
operator|new
name|FileBasedConfig
argument_list|(
name|sitePaths
operator|.
name|gerrit_config
operator|.
name|toFile
argument_list|()
argument_list|,
name|FS
operator|.
name|detect
argument_list|()
argument_list|)
decl_stmt|;
name|cfg
operator|.
name|load
argument_list|()
expr_stmt|;
name|cfg
operator|.
name|setBoolean
argument_list|(
literal|"index"
argument_list|,
literal|null
argument_list|,
literal|"onlineUpgrade"
argument_list|,
name|enable
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
DECL|method|assertSearchVersion (ServerContext ctx, int expected)
specifier|private
name|void
name|assertSearchVersion
parameter_list|(
name|ServerContext
name|ctx
parameter_list|,
name|int
name|expected
parameter_list|)
block|{
name|assertThat
argument_list|(
name|ctx
operator|.
name|getInjector
argument_list|()
operator|.
name|getInstance
argument_list|(
name|ChangeIndexCollection
operator|.
name|class
argument_list|)
operator|.
name|getSearchIndex
argument_list|()
operator|.
name|getSchema
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
operator|.
name|named
argument_list|(
literal|"search version"
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
DECL|method|assertWriteVersions (ServerContext ctx, Integer... expected)
specifier|private
name|void
name|assertWriteVersions
parameter_list|(
name|ServerContext
name|ctx
parameter_list|,
name|Integer
modifier|...
name|expected
parameter_list|)
block|{
name|assertThat
argument_list|(
name|ctx
operator|.
name|getInjector
argument_list|()
operator|.
name|getInstance
argument_list|(
name|ChangeIndexCollection
operator|.
name|class
argument_list|)
operator|.
name|getWriteIndexes
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|i
lambda|->
name|i
operator|.
name|getSchema
argument_list|()
operator|.
name|getVersion
argument_list|()
argument_list|)
argument_list|)
operator|.
name|named
argument_list|(
literal|"write versions"
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertReady (int expectedReady)
specifier|private
name|void
name|assertReady
parameter_list|(
name|int
name|expectedReady
parameter_list|)
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|Integer
argument_list|>
name|allVersions
init|=
name|ChangeSchemaDefinitions
operator|.
name|INSTANCE
operator|.
name|getSchemas
argument_list|()
operator|.
name|keySet
argument_list|()
decl_stmt|;
name|GerritIndexStatus
name|status
init|=
operator|new
name|GerritIndexStatus
argument_list|(
name|sitePaths
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|allVersions
operator|.
name|stream
argument_list|()
operator|.
name|collect
argument_list|(
name|toImmutableMap
argument_list|(
name|v
lambda|->
name|v
argument_list|,
name|v
lambda|->
name|status
operator|.
name|getReady
argument_list|(
name|CHANGES
argument_list|,
name|v
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|named
argument_list|(
literal|"ready state for index versions"
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|allVersions
operator|.
name|stream
argument_list|()
operator|.
name|collect
argument_list|(
name|toImmutableMap
argument_list|(
name|v
lambda|->
name|v
argument_list|,
name|v
lambda|->
name|v
operator|==
name|expectedReady
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

