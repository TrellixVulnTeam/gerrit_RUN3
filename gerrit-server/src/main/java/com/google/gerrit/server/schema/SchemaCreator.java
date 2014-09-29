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
name|common
operator|.
name|data
operator|.
name|GroupReference
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
name|AccountGroup
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
name|AccountGroupName
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
name|account
operator|.
name|GroupUUID
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
name|SitePath
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
name|gwtorm
operator|.
name|jdbc
operator|.
name|JdbcExecutor
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
name|jdbc
operator|.
name|JdbcSchema
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
name|IOException
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
name|javax
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_comment
comment|/** Creates the current database schema and populates initial code rows. */
end_comment

begin_class
DECL|class|SchemaCreator
specifier|public
class|class
name|SchemaCreator
block|{
specifier|private
specifier|final
annotation|@
name|SitePath
DECL|field|site_path
name|File
name|site_path
decl_stmt|;
DECL|field|allProjectsCreator
specifier|private
specifier|final
name|AllProjectsCreator
name|allProjectsCreator
decl_stmt|;
DECL|field|allUsersCreator
specifier|private
specifier|final
name|AllUsersCreator
name|allUsersCreator
decl_stmt|;
DECL|field|serverUser
specifier|private
specifier|final
name|PersonIdent
name|serverUser
decl_stmt|;
DECL|field|dataSourceType
specifier|private
specifier|final
name|DataSourceType
name|dataSourceType
decl_stmt|;
DECL|field|versionNbr
specifier|private
specifier|final
name|int
name|versionNbr
decl_stmt|;
DECL|field|admin
specifier|private
name|AccountGroup
name|admin
decl_stmt|;
DECL|field|batch
specifier|private
name|AccountGroup
name|batch
decl_stmt|;
annotation|@
name|Inject
DECL|method|SchemaCreator (SitePaths site, @Current SchemaVersion version, AllProjectsCreator ap, AllUsersCreator auc, @GerritPersonIdent PersonIdent au, DataSourceType dst)
specifier|public
name|SchemaCreator
parameter_list|(
name|SitePaths
name|site
parameter_list|,
annotation|@
name|Current
name|SchemaVersion
name|version
parameter_list|,
name|AllProjectsCreator
name|ap
parameter_list|,
name|AllUsersCreator
name|auc
parameter_list|,
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|au
parameter_list|,
name|DataSourceType
name|dst
parameter_list|)
block|{
name|this
argument_list|(
name|site
operator|.
name|site_path
argument_list|,
name|version
argument_list|,
name|ap
argument_list|,
name|auc
argument_list|,
name|au
argument_list|,
name|dst
argument_list|)
expr_stmt|;
block|}
DECL|method|SchemaCreator (@itePath File site, @Current SchemaVersion version, AllProjectsCreator ap, AllUsersCreator auc, @GerritPersonIdent PersonIdent au, DataSourceType dst)
specifier|public
name|SchemaCreator
parameter_list|(
annotation|@
name|SitePath
name|File
name|site
parameter_list|,
annotation|@
name|Current
name|SchemaVersion
name|version
parameter_list|,
name|AllProjectsCreator
name|ap
parameter_list|,
name|AllUsersCreator
name|auc
parameter_list|,
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|au
parameter_list|,
name|DataSourceType
name|dst
parameter_list|)
block|{
name|site_path
operator|=
name|site
expr_stmt|;
name|allProjectsCreator
operator|=
name|ap
expr_stmt|;
name|allUsersCreator
operator|=
name|auc
expr_stmt|;
name|serverUser
operator|=
name|au
expr_stmt|;
name|dataSourceType
operator|=
name|dst
expr_stmt|;
name|versionNbr
operator|=
name|version
operator|.
name|getVersionNbr
argument_list|()
expr_stmt|;
block|}
DECL|method|create (final ReviewDb db)
specifier|public
name|void
name|create
parameter_list|(
specifier|final
name|ReviewDb
name|db
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
block|{
specifier|final
name|JdbcSchema
name|jdbc
init|=
operator|(
name|JdbcSchema
operator|)
name|db
decl_stmt|;
specifier|final
name|JdbcExecutor
name|e
init|=
operator|new
name|JdbcExecutor
argument_list|(
name|jdbc
argument_list|)
decl_stmt|;
try|try
block|{
name|jdbc
operator|.
name|updateSchema
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|e
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|final
name|CurrentSchemaVersion
name|sVer
init|=
name|CurrentSchemaVersion
operator|.
name|create
argument_list|()
decl_stmt|;
name|sVer
operator|.
name|versionNbr
operator|=
name|versionNbr
expr_stmt|;
name|db
operator|.
name|schemaVersion
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|sVer
argument_list|)
argument_list|)
expr_stmt|;
name|initSystemConfig
argument_list|(
name|db
argument_list|)
expr_stmt|;
name|allProjectsCreator
operator|.
name|setAdministrators
argument_list|(
name|GroupReference
operator|.
name|forGroup
argument_list|(
name|admin
argument_list|)
argument_list|)
operator|.
name|setBatchUsers
argument_list|(
name|GroupReference
operator|.
name|forGroup
argument_list|(
name|batch
argument_list|)
argument_list|)
operator|.
name|create
argument_list|()
expr_stmt|;
name|allUsersCreator
operator|.
name|create
argument_list|()
expr_stmt|;
name|dataSourceType
operator|.
name|getIndexScript
argument_list|()
operator|.
name|run
argument_list|(
name|db
argument_list|)
expr_stmt|;
block|}
DECL|method|newGroup (ReviewDb c, String name, AccountGroup.UUID uuid)
specifier|private
name|AccountGroup
name|newGroup
parameter_list|(
name|ReviewDb
name|c
parameter_list|,
name|String
name|name
parameter_list|,
name|AccountGroup
operator|.
name|UUID
name|uuid
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
name|uuid
operator|==
literal|null
condition|)
block|{
name|uuid
operator|=
name|GroupUUID
operator|.
name|make
argument_list|(
name|name
argument_list|,
name|serverUser
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|AccountGroup
argument_list|(
comment|//
operator|new
name|AccountGroup
operator|.
name|NameKey
argument_list|(
name|name
argument_list|)
argument_list|,
comment|//
operator|new
name|AccountGroup
operator|.
name|Id
argument_list|(
name|c
operator|.
name|nextAccountGroupId
argument_list|()
argument_list|)
argument_list|,
comment|//
name|uuid
argument_list|)
return|;
block|}
DECL|method|initSystemConfig (final ReviewDb c)
specifier|private
name|SystemConfig
name|initSystemConfig
parameter_list|(
specifier|final
name|ReviewDb
name|c
parameter_list|)
throws|throws
name|OrmException
block|{
name|admin
operator|=
name|newGroup
argument_list|(
name|c
argument_list|,
literal|"Administrators"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|admin
operator|.
name|setDescription
argument_list|(
literal|"Gerrit Site Administrators"
argument_list|)
expr_stmt|;
name|c
operator|.
name|accountGroups
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|admin
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|accountGroupNames
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|AccountGroupName
argument_list|(
name|admin
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|batch
operator|=
name|newGroup
argument_list|(
name|c
argument_list|,
literal|"Non-Interactive Users"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|batch
operator|.
name|setDescription
argument_list|(
literal|"Users who perform batch actions on Gerrit"
argument_list|)
expr_stmt|;
name|batch
operator|.
name|setOwnerGroupUUID
argument_list|(
name|admin
operator|.
name|getGroupUUID
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|.
name|accountGroups
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|batch
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|accountGroupNames
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|AccountGroupName
argument_list|(
name|batch
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|SystemConfig
name|s
init|=
name|SystemConfig
operator|.
name|create
argument_list|()
decl_stmt|;
try|try
block|{
name|s
operator|.
name|sitePath
operator|=
name|site_path
operator|.
name|getCanonicalPath
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|s
operator|.
name|sitePath
operator|=
name|site_path
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
block|}
name|c
operator|.
name|systemConfig
argument_list|()
operator|.
name|insert
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
block|}
end_class

end_unit

