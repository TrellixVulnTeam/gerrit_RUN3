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
name|CurrentSchemaVersion
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
name|reviewdb
operator|.
name|server
operator|.
name|ReviewDbUtil
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
name|inject
operator|.
name|AbstractModule
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
name|Inject
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
name|Key
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Stage
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
name|PersonIdent
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
name|sql
operator|.
name|SQLException
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

begin_comment
comment|/** Creates or updates the current database schema. */
end_comment

begin_class
DECL|class|SchemaUpdater
specifier|public
class|class
name|SchemaUpdater
block|{
DECL|field|schema
specifier|private
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
decl_stmt|;
DECL|field|site
specifier|private
specifier|final
name|SitePaths
name|site
decl_stmt|;
DECL|field|creator
specifier|private
specifier|final
name|SchemaCreator
name|creator
decl_stmt|;
DECL|field|updater
specifier|private
specifier|final
name|Provider
argument_list|<
name|SchemaVersion
argument_list|>
name|updater
decl_stmt|;
annotation|@
name|Inject
DECL|method|SchemaUpdater (SchemaFactory<ReviewDb> schema, SitePaths site, SchemaCreator creator, Injector parent)
name|SchemaUpdater
parameter_list|(
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
parameter_list|,
name|SitePaths
name|site
parameter_list|,
name|SchemaCreator
name|creator
parameter_list|,
name|Injector
name|parent
parameter_list|)
block|{
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|site
operator|=
name|site
expr_stmt|;
name|this
operator|.
name|creator
operator|=
name|creator
expr_stmt|;
name|this
operator|.
name|updater
operator|=
name|buildInjector
argument_list|(
name|parent
argument_list|)
operator|.
name|getProvider
argument_list|(
name|SchemaVersion
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|buildInjector (final Injector parent)
specifier|private
specifier|static
name|Injector
name|buildInjector
parameter_list|(
specifier|final
name|Injector
name|parent
parameter_list|)
block|{
comment|// Use DEVELOPMENT mode to allow lazy initialization of the
comment|// graph. This avoids touching ancient schema versions that
comment|// are behind this installation's current version.
return|return
name|Guice
operator|.
name|createInjector
argument_list|(
name|Stage
operator|.
name|DEVELOPMENT
argument_list|,
operator|new
name|AbstractModule
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
name|SchemaVersion
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|SchemaVersion
operator|.
name|C
argument_list|)
expr_stmt|;
for|for
control|(
name|Key
argument_list|<
name|?
argument_list|>
name|k
range|:
operator|new
name|Key
argument_list|<
name|?
argument_list|>
index|[]
block|{
name|Key
operator|.
name|get
argument_list|(
name|PersonIdent
operator|.
name|class
argument_list|,
name|GerritPersonIdent
operator|.
name|class
argument_list|)
operator|,
name|Key
operator|.
name|get
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|AnonymousCowardName
operator|.
name|class
argument_list|)
operator|,
name|Key
operator|.
name|get
argument_list|(
name|Config
operator|.
name|class
argument_list|,
name|GerritServerConfig
operator|.
name|class
argument_list|)
operator|,
block|}
control|)
block|{
name|rebind
argument_list|(
name|parent
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Class
argument_list|<
name|?
argument_list|>
name|c
range|:
operator|new
name|Class
argument_list|<
name|?
argument_list|>
index|[]
block|{
name|AllProjectsName
operator|.
name|class
operator|,
name|AllUsersCreator
operator|.
name|class
operator|,
name|AllUsersName
operator|.
name|class
operator|,
name|GitRepositoryManager
operator|.
name|class
operator|,
name|SitePaths
operator|.
name|class
operator|,
block|}
control|)
block|{
name|rebind
argument_list|(
name|parent
argument_list|,
name|Key
operator|.
name|get
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
parameter_list|<
name|T
parameter_list|>
name|void
name|rebind
parameter_list|(
name|Injector
name|parent
parameter_list|,
name|Key
argument_list|<
name|T
argument_list|>
name|c
parameter_list|)
block|{
name|bind
argument_list|(
name|c
argument_list|)
operator|.
name|toProvider
argument_list|(
name|parent
operator|.
name|getProvider
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|update (final UpdateUI ui)
specifier|public
name|void
name|update
parameter_list|(
specifier|final
name|UpdateUI
name|ui
parameter_list|)
throws|throws
name|OrmException
block|{
try|try
init|(
name|ReviewDb
name|db
init|=
name|ReviewDbUtil
operator|.
name|unwrapDb
argument_list|(
name|schema
operator|.
name|open
argument_list|()
argument_list|)
init|)
block|{
specifier|final
name|SchemaVersion
name|u
init|=
name|updater
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|CurrentSchemaVersion
name|version
init|=
name|getSchemaVersion
argument_list|(
name|db
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|creator
operator|.
name|create
argument_list|(
name|db
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|ConfigInvalidException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"Cannot initialize schema"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
try|try
block|{
name|u
operator|.
name|check
argument_list|(
name|ui
argument_list|,
name|version
argument_list|,
name|db
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"Cannot upgrade schema"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|updateSystemConfig
argument_list|(
name|db
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getSchemaVersion (final ReviewDb db)
specifier|private
name|CurrentSchemaVersion
name|getSchemaVersion
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|)
block|{
try|try
block|{
return|return
name|db
operator|.
name|schemaVersion
argument_list|()
operator|.
name|get
argument_list|(
operator|new
name|CurrentSchemaVersion
operator|.
name|Key
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|updateSystemConfig (final ReviewDb db)
specifier|private
name|void
name|updateSystemConfig
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
block|{
specifier|final
name|SystemConfig
name|sc
init|=
name|db
operator|.
name|systemConfig
argument_list|()
operator|.
name|get
argument_list|(
operator|new
name|SystemConfig
operator|.
name|Key
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|sc
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"No record in system_config table"
argument_list|)
throw|;
block|}
try|try
block|{
name|sc
operator|.
name|sitePath
operator|=
name|site
operator|.
name|site_path
operator|.
name|toRealPath
argument_list|()
operator|.
name|normalize
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|sc
operator|.
name|sitePath
operator|=
name|site
operator|.
name|site_path
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|normalize
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|db
operator|.
name|systemConfig
argument_list|()
operator|.
name|update
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|sc
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

