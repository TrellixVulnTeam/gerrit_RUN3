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
DECL|package|com.google.gerrit.acceptance
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
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
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
operator|.
name|joining
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
name|Streams
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
name|Nullable
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
name|api
operator|.
name|groups
operator|.
name|GroupInput
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
name|launcher
operator|.
name|GerritLauncher
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
name|Account
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
name|CurrentUser
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
name|SitePaths
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
name|util
operator|.
name|ManualRequestContext
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
name|util
operator|.
name|OneOffRequestContext
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
name|util
operator|.
name|RequestContext
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
name|ConfigSuite
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
name|Injector
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
name|Module
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|Collections
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
name|errors
operator|.
name|ConfigInvalidException
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
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|StoredConfig
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
name|eclipse
operator|.
name|jgit
operator|.
name|util
operator|.
name|SystemReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|RuleChain
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|model
operator|.
name|Statement
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|ConfigSuite
operator|.
name|class
argument_list|)
annotation|@
name|UseLocalDisk
DECL|class|StandaloneSiteTest
specifier|public
specifier|abstract
class|class
name|StandaloneSiteTest
block|{
DECL|class|ServerContext
specifier|protected
class|class
name|ServerContext
implements|implements
name|RequestContext
implements|,
name|AutoCloseable
block|{
DECL|field|server
specifier|private
specifier|final
name|GerritServer
name|server
decl_stmt|;
DECL|field|ctx
specifier|private
specifier|final
name|ManualRequestContext
name|ctx
decl_stmt|;
DECL|method|ServerContext (GerritServer server)
specifier|private
name|ServerContext
parameter_list|(
name|GerritServer
name|server
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|server
operator|=
name|server
expr_stmt|;
name|Injector
name|i
init|=
name|server
operator|.
name|getTestInjector
argument_list|()
decl_stmt|;
if|if
condition|(
name|adminId
operator|==
literal|null
condition|)
block|{
name|adminId
operator|=
name|i
operator|.
name|getInstance
argument_list|(
name|AccountCreator
operator|.
name|class
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|id
argument_list|()
expr_stmt|;
block|}
name|ctx
operator|=
name|i
operator|.
name|getInstance
argument_list|(
name|OneOffRequestContext
operator|.
name|class
argument_list|)
operator|.
name|openAs
argument_list|(
name|adminId
argument_list|)
expr_stmt|;
name|GerritApi
name|gApi
init|=
name|i
operator|.
name|getInstance
argument_list|(
name|GerritApi
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
comment|// ServerContext ctor is called multiple times but the group can be only created once
name|gApi
operator|.
name|groups
argument_list|()
operator|.
name|id
argument_list|(
literal|"Group"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ResourceNotFoundException
name|e
parameter_list|)
block|{
name|GroupInput
name|in
init|=
operator|new
name|GroupInput
argument_list|()
decl_stmt|;
name|in
operator|.
name|members
operator|=
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"admin"
argument_list|)
expr_stmt|;
name|in
operator|.
name|name
operator|=
literal|"Group"
expr_stmt|;
name|gApi
operator|.
name|groups
argument_list|()
operator|.
name|create
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getUser ()
specifier|public
name|CurrentUser
name|getUser
parameter_list|()
block|{
return|return
name|ctx
operator|.
name|getUser
argument_list|()
return|;
block|}
DECL|method|getInjector ()
specifier|public
name|Injector
name|getInjector
parameter_list|()
block|{
return|return
name|server
operator|.
name|getTestInjector
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|ctx
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|server
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|field|baseConfig
annotation|@
name|ConfigSuite
operator|.
name|Parameter
specifier|public
name|Config
name|baseConfig
decl_stmt|;
DECL|field|configName
annotation|@
name|ConfigSuite
operator|.
name|Name
specifier|private
name|String
name|configName
decl_stmt|;
DECL|field|tempSiteDir
specifier|private
specifier|final
name|TemporaryFolder
name|tempSiteDir
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
DECL|field|testRunner
specifier|private
specifier|final
name|TestRule
name|testRunner
init|=
parameter_list|(
name|base
parameter_list|,
name|description
parameter_list|)
lambda|->
operator|new
name|Statement
argument_list|()
block|{
block|@Override             public void evaluate(
init|)
throws|throws
name|Throwable
block|{
decl|try
block|{
name|beforeTest
argument_list|(
name|description
argument_list|)
expr_stmt|;
name|base
operator|.
name|evaluate
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|afterTest
argument_list|()
expr_stmt|;
block|}
block|}
end_class

begin_decl_stmt
unit|};
DECL|field|ruleChain
annotation|@
name|Rule
specifier|public
name|RuleChain
name|ruleChain
init|=
name|RuleChain
operator|.
name|outerRule
argument_list|(
name|tempSiteDir
argument_list|)
operator|.
name|around
argument_list|(
name|testRunner
argument_list|)
decl_stmt|;
end_decl_stmt

begin_decl_stmt
DECL|field|sitePaths
specifier|protected
name|SitePaths
name|sitePaths
decl_stmt|;
end_decl_stmt

begin_decl_stmt
DECL|field|adminId
specifier|protected
name|Account
operator|.
name|Id
name|adminId
decl_stmt|;
end_decl_stmt

begin_decl_stmt
DECL|field|serverDesc
specifier|private
name|GerritServer
operator|.
name|Description
name|serverDesc
decl_stmt|;
end_decl_stmt

begin_decl_stmt
DECL|field|oldSystemReader
specifier|private
name|SystemReader
name|oldSystemReader
decl_stmt|;
end_decl_stmt

begin_function
DECL|method|beforeTest (Description description)
specifier|private
name|void
name|beforeTest
parameter_list|(
name|Description
name|description
parameter_list|)
throws|throws
name|Exception
block|{
comment|// SystemReader must be overridden before creating any repos, since they read the user/system
comment|// configs at initialization time, and are then stored in the RepositoryCache forever.
name|oldSystemReader
operator|=
name|setFakeSystemReader
argument_list|(
name|tempSiteDir
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|serverDesc
operator|=
name|GerritServer
operator|.
name|Description
operator|.
name|forTestMethod
argument_list|(
name|description
argument_list|,
name|configName
argument_list|)
expr_stmt|;
name|sitePaths
operator|=
operator|new
name|SitePaths
argument_list|(
name|tempSiteDir
operator|.
name|getRoot
argument_list|()
operator|.
name|toPath
argument_list|()
argument_list|)
expr_stmt|;
name|GerritServer
operator|.
name|init
argument_list|(
name|serverDesc
argument_list|,
name|baseConfig
argument_list|,
name|sitePaths
operator|.
name|site_path
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|setFakeSystemReader (File tempDir)
specifier|private
specifier|static
name|SystemReader
name|setFakeSystemReader
parameter_list|(
name|File
name|tempDir
parameter_list|)
block|{
name|SystemReader
name|oldSystemReader
init|=
name|SystemReader
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|SystemReader
operator|.
name|setInstance
argument_list|(
operator|new
name|SystemReader
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getHostname
parameter_list|()
block|{
return|return
name|oldSystemReader
operator|.
name|getHostname
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getenv
parameter_list|(
name|String
name|variable
parameter_list|)
block|{
return|return
name|oldSystemReader
operator|.
name|getenv
argument_list|(
name|variable
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getProperty
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|oldSystemReader
operator|.
name|getProperty
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|FileBasedConfig
name|openUserConfig
parameter_list|(
name|Config
name|parent
parameter_list|,
name|FS
name|fs
parameter_list|)
block|{
return|return
operator|new
name|FileBasedConfig
argument_list|(
name|parent
argument_list|,
operator|new
name|File
argument_list|(
name|tempDir
argument_list|,
literal|"user.config"
argument_list|)
argument_list|,
name|FS
operator|.
name|detect
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|FileBasedConfig
name|openSystemConfig
parameter_list|(
name|Config
name|parent
parameter_list|,
name|FS
name|fs
parameter_list|)
block|{
return|return
operator|new
name|FileBasedConfig
argument_list|(
name|parent
argument_list|,
operator|new
name|File
argument_list|(
name|tempDir
argument_list|,
literal|"system.config"
argument_list|)
argument_list|,
name|FS
operator|.
name|detect
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getCurrentTime
parameter_list|()
block|{
return|return
name|oldSystemReader
operator|.
name|getCurrentTime
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getTimezone
parameter_list|(
name|long
name|when
parameter_list|)
block|{
return|return
name|oldSystemReader
operator|.
name|getTimezone
argument_list|(
name|when
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|StoredConfig
name|getUserConfig
parameter_list|()
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
return|return
name|oldSystemReader
operator|.
name|getUserConfig
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|StoredConfig
name|getSystemConfig
parameter_list|()
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
return|return
name|oldSystemReader
operator|.
name|getSystemConfig
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|oldSystemReader
return|;
block|}
end_function

begin_function
DECL|method|afterTest ()
specifier|private
name|void
name|afterTest
parameter_list|()
throws|throws
name|Exception
block|{
name|SystemReader
operator|.
name|setInstance
argument_list|(
name|oldSystemReader
argument_list|)
expr_stmt|;
name|oldSystemReader
operator|=
literal|null
expr_stmt|;
block|}
end_function

begin_function
DECL|method|startServer ()
specifier|protected
name|ServerContext
name|startServer
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|startServer
argument_list|(
literal|null
argument_list|)
return|;
block|}
end_function

begin_function
DECL|method|startServer (@ullable Module testSysModule, String... additionalArgs)
specifier|protected
name|ServerContext
name|startServer
parameter_list|(
annotation|@
name|Nullable
name|Module
name|testSysModule
parameter_list|,
name|String
modifier|...
name|additionalArgs
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|ServerContext
argument_list|(
name|startImpl
argument_list|(
name|testSysModule
argument_list|,
name|additionalArgs
argument_list|)
argument_list|)
return|;
block|}
end_function

begin_function
DECL|method|assertServerStartupFails ()
specifier|protected
name|void
name|assertServerStartupFails
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|GerritServer
name|server
init|=
name|startImpl
argument_list|(
literal|null
argument_list|)
init|)
block|{
name|fail
argument_list|(
literal|"expected server startup to fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|GerritServer
operator|.
name|StartupException
name|e
parameter_list|)
block|{
comment|// Expected.
block|}
block|}
end_function

begin_function
DECL|method|startImpl (@ullable Module testSysModule, String... additionalArgs)
specifier|private
name|GerritServer
name|startImpl
parameter_list|(
annotation|@
name|Nullable
name|Module
name|testSysModule
parameter_list|,
name|String
modifier|...
name|additionalArgs
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|GerritServer
operator|.
name|start
argument_list|(
name|serverDesc
argument_list|,
name|baseConfig
argument_list|,
name|sitePaths
operator|.
name|site_path
argument_list|,
name|testSysModule
argument_list|,
literal|null
argument_list|,
name|additionalArgs
argument_list|)
return|;
block|}
end_function

begin_function
DECL|method|runGerrit (String... args)
specifier|protected
specifier|static
name|void
name|runGerrit
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Use invokeProgram with the current classloader, rather than mainImpl, which would create a
comment|// new classloader. This is necessary so that static state, particularly the SystemReader, is
comment|// shared with the test method.
name|assertThat
argument_list|(
name|GerritLauncher
operator|.
name|invokeProgram
argument_list|(
name|StandaloneSiteTest
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|,
name|args
argument_list|)
argument_list|)
operator|.
name|named
argument_list|(
literal|"gerrit.war "
operator|+
name|Arrays
operator|.
name|stream
argument_list|(
name|args
argument_list|)
operator|.
name|collect
argument_list|(
name|joining
argument_list|(
literal|" "
argument_list|)
argument_list|)
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
annotation|@
name|SafeVarargs
DECL|method|runGerrit (Iterable<String>.... multiArgs)
specifier|protected
specifier|static
name|void
name|runGerrit
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
modifier|...
name|multiArgs
parameter_list|)
throws|throws
name|Exception
block|{
name|runGerrit
argument_list|(
name|Arrays
operator|.
name|stream
argument_list|(
name|multiArgs
argument_list|)
operator|.
name|flatMap
argument_list|(
name|Streams
operator|::
name|stream
argument_list|)
operator|.
name|toArray
argument_list|(
name|String
index|[]
operator|::
operator|new
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

unit|}
end_unit

