begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2009 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.schema
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|schema
package|;
end_package

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
name|SystemConfig
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
name|server
operator|.
name|ReviewDb
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
name|GerritPersonIdent
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
name|GerritPersonIdentProvider
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
name|AnonymousCowardName
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
name|AnonymousCowardNameProvider
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
name|FactoryModule
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
name|GerritServerConfig
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
name|git
operator|.
name|GitRepositoryManager
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
name|LocalDiskRepositoryManager
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
name|testutil
operator|.
name|InMemoryDatabase
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
name|testutil
operator|.
name|InMemoryH2Type
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|server
operator|.
name|OrmException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|server
operator|.
name|SchemaFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gwtorm
operator|.
name|server
operator|.
name|StatementExecutor
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
name|Guice
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
name|TypeLiteral
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|PersonIdent
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
name|FileNotFoundException
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_class
DECL|class|SchemaUpdaterTest
specifier|public
class|class
name|SchemaUpdaterTest
extends|extends
name|TestCase
block|{
DECL|field|db
specifier|private
name|InMemoryDatabase
name|db
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp ()
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|db
operator|=
operator|new
name|InMemoryDatabase
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown ()
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|InMemoryDatabase
operator|.
name|drop
argument_list|(
name|db
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
DECL|method|testUpdate ()
specifier|public
name|void
name|testUpdate
parameter_list|()
throws|throws
name|OrmException
throws|,
name|FileNotFoundException
throws|,
name|IOException
block|{
name|db
operator|.
name|create
argument_list|()
expr_stmt|;
specifier|final
name|File
name|site
init|=
operator|new
name|File
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|SitePaths
name|paths
init|=
operator|new
name|SitePaths
argument_list|(
name|site
argument_list|)
decl_stmt|;
name|SchemaUpdater
name|u
init|=
name|Guice
operator|.
name|createInjector
argument_list|(
operator|new
name|FactoryModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
operator|new
name|TypeLiteral
argument_list|<
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
argument_list|>
argument_list|()
block|{}
argument_list|)
operator|.
name|toInstance
argument_list|(
name|db
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|SitePaths
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|paths
argument_list|)
expr_stmt|;
name|install
argument_list|(
operator|new
name|SchemaVersion
operator|.
name|Module
argument_list|()
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
literal|"gerrit"
argument_list|,
literal|null
argument_list|,
literal|"basePath"
argument_list|,
literal|"git"
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setString
argument_list|(
literal|"user"
argument_list|,
literal|null
argument_list|,
literal|"name"
argument_list|,
literal|"Gerrit Code Review"
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setString
argument_list|(
literal|"user"
argument_list|,
literal|null
argument_list|,
literal|"email"
argument_list|,
literal|"gerrit@localhost"
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|Config
operator|.
name|class
argument_list|)
comment|//
operator|.
name|annotatedWith
argument_list|(
name|GerritServerConfig
operator|.
name|class
argument_list|)
comment|//
operator|.
name|toInstance
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|PersonIdent
operator|.
name|class
argument_list|)
comment|//
operator|.
name|annotatedWith
argument_list|(
name|GerritPersonIdent
operator|.
name|class
argument_list|)
comment|//
operator|.
name|toProvider
argument_list|(
name|GerritPersonIdentProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|AllProjectsName
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
operator|new
name|AllProjectsName
argument_list|(
literal|"All-Projects"
argument_list|)
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|GitRepositoryManager
operator|.
name|class
argument_list|)
comment|//
operator|.
name|to
argument_list|(
name|LocalDiskRepositoryManager
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|String
operator|.
name|class
argument_list|)
comment|//
operator|.
name|annotatedWith
argument_list|(
name|AnonymousCowardName
operator|.
name|class
argument_list|)
comment|//
operator|.
name|toProvider
argument_list|(
name|AnonymousCowardNameProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|DataSourceType
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|InMemoryH2Type
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
operator|.
name|getInstance
argument_list|(
name|SchemaUpdater
operator|.
name|class
argument_list|)
decl_stmt|;
name|u
operator|.
name|update
argument_list|(
operator|new
name|UpdateUI
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|message
parameter_list|(
name|String
name|msg
parameter_list|)
block|{       }
annotation|@
name|Override
specifier|public
name|boolean
name|yesno
parameter_list|(
name|boolean
name|def
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
return|return
name|def
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isBatch
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|pruneSchema
parameter_list|(
name|StatementExecutor
name|e
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|pruneList
parameter_list|)
throws|throws
name|OrmException
block|{
for|for
control|(
name|String
name|sql
range|:
name|pruneList
control|)
block|{
name|e
operator|.
name|execute
argument_list|(
name|sql
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|db
operator|.
name|assertSchemaVersion
argument_list|()
expr_stmt|;
specifier|final
name|SystemConfig
name|sc
init|=
name|db
operator|.
name|getSystemConfig
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|paths
operator|.
name|site_path
operator|.
name|getCanonicalPath
argument_list|()
argument_list|,
name|sc
operator|.
name|sitePath
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

