begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2011 The Android Open Source Project
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
name|AccessSection
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
name|GlobalCapability
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
name|PermissionRule
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
name|PermissionRule
operator|.
name|Action
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
name|config
operator|.
name|AllProjectsNameProvider
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
name|extensions
operator|.
name|events
operator|.
name|GitReferenceUpdated
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
name|MetaDataUpdate
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
name|PreparedStatement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
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

begin_import
import|import
name|javax
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_class
DECL|class|Schema_57
specifier|public
class|class
name|Schema_57
extends|extends
name|SchemaVersion
block|{
DECL|field|site
specifier|private
specifier|final
name|SitePaths
name|site
decl_stmt|;
DECL|field|mgr
specifier|private
specifier|final
name|GitRepositoryManager
name|mgr
decl_stmt|;
DECL|field|serverUser
specifier|private
specifier|final
name|PersonIdent
name|serverUser
decl_stmt|;
annotation|@
name|Inject
DECL|method|Schema_57 (Provider<Schema_56> prior, SitePaths site, GitRepositoryManager mgr, @GerritPersonIdent PersonIdent serverUser)
name|Schema_57
parameter_list|(
name|Provider
argument_list|<
name|Schema_56
argument_list|>
name|prior
parameter_list|,
name|SitePaths
name|site
parameter_list|,
name|GitRepositoryManager
name|mgr
parameter_list|,
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|serverUser
parameter_list|)
block|{
name|super
argument_list|(
name|prior
argument_list|)
expr_stmt|;
name|this
operator|.
name|site
operator|=
name|site
expr_stmt|;
name|this
operator|.
name|mgr
operator|=
name|mgr
expr_stmt|;
name|this
operator|.
name|serverUser
operator|=
name|serverUser
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|migrateData (ReviewDb db, UpdateUI ui)
specifier|protected
name|void
name|migrateData
parameter_list|(
name|ReviewDb
name|db
parameter_list|,
name|UpdateUI
name|ui
parameter_list|)
throws|throws
name|OrmException
block|{
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
name|Project
operator|.
name|NameKey
name|allProjects
init|=
name|sc
operator|.
name|wildProjectName
decl_stmt|;
name|FileBasedConfig
name|cfg
init|=
operator|new
name|FileBasedConfig
argument_list|(
name|site
operator|.
name|gerrit_config
argument_list|,
name|FS
operator|.
name|DETECTED
argument_list|)
decl_stmt|;
name|boolean
name|cfgDirty
init|=
literal|false
decl_stmt|;
try|try
block|{
name|cfg
operator|.
name|load
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConfigInvalidException
name|err
parameter_list|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"Cannot read "
operator|+
name|site
operator|.
name|gerrit_config
argument_list|,
name|err
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|err
parameter_list|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"Cannot read "
operator|+
name|site
operator|.
name|gerrit_config
argument_list|,
name|err
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|allProjects
operator|.
name|get
argument_list|()
operator|.
name|equals
argument_list|(
name|AllProjectsNameProvider
operator|.
name|DEFAULT
argument_list|)
condition|)
block|{
name|ui
operator|.
name|message
argument_list|(
literal|"Setting gerrit.allProjects = "
operator|+
name|allProjects
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|setString
argument_list|(
literal|"gerrit"
argument_list|,
literal|null
argument_list|,
literal|"allProjects"
argument_list|,
name|allProjects
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|cfgDirty
operator|=
literal|true
expr_stmt|;
block|}
try|try
block|{
name|Repository
name|git
init|=
name|mgr
operator|.
name|openRepository
argument_list|(
name|allProjects
argument_list|)
decl_stmt|;
try|try
block|{
name|MetaDataUpdate
name|md
init|=
operator|new
name|MetaDataUpdate
argument_list|(
name|GitReferenceUpdated
operator|.
name|DISABLED
argument_list|,
name|allProjects
argument_list|,
name|git
argument_list|)
decl_stmt|;
name|md
operator|.
name|getCommitBuilder
argument_list|()
operator|.
name|setAuthor
argument_list|(
name|serverUser
argument_list|)
expr_stmt|;
name|md
operator|.
name|getCommitBuilder
argument_list|()
operator|.
name|setCommitter
argument_list|(
name|serverUser
argument_list|)
expr_stmt|;
name|ProjectConfig
name|config
init|=
name|ProjectConfig
operator|.
name|read
argument_list|(
name|md
argument_list|)
decl_stmt|;
name|AccessSection
name|cap
init|=
name|config
operator|.
name|getAccessSection
argument_list|(
name|AccessSection
operator|.
name|GLOBAL_CAPABILITIES
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// Move the Administrators group reference to All-Projects.
name|cap
operator|.
name|getPermission
argument_list|(
name|GlobalCapability
operator|.
name|ADMINISTRATE_SERVER
argument_list|,
literal|true
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|PermissionRule
argument_list|(
name|config
operator|.
name|resolve
argument_list|(
name|db
operator|.
name|accountGroups
argument_list|()
operator|.
name|get
argument_list|(
name|sc
operator|.
name|adminGroupId
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Move the repository.*.createGroup to Create Project.
name|String
index|[]
name|createGroupList
init|=
name|cfg
operator|.
name|getStringList
argument_list|(
literal|"repository"
argument_list|,
literal|"*"
argument_list|,
literal|"createGroup"
argument_list|)
decl_stmt|;
comment|// Prepare the account_group_includes query
name|PreparedStatement
name|stmt
init|=
operator|(
operator|(
name|JdbcSchema
operator|)
name|db
operator|)
operator|.
name|getConnection
argument_list|()
operator|.
name|prepareStatement
argument_list|(
literal|"SELECT COUNT(1) FROM account_group_includes WHERE group_id = ?"
argument_list|)
decl_stmt|;
name|boolean
name|isAccountGroupEmpty
init|=
literal|false
decl_stmt|;
try|try
block|{
name|stmt
operator|.
name|setInt
argument_list|(
literal|1
argument_list|,
name|sc
operator|.
name|batchUsersGroupId
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|ResultSet
name|rs
init|=
name|stmt
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
if|if
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|isAccountGroupEmpty
operator|=
name|rs
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
operator|==
literal|0
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|stmt
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|String
name|name
range|:
name|createGroupList
control|)
block|{
name|AccountGroup
operator|.
name|NameKey
name|key
init|=
operator|new
name|AccountGroup
operator|.
name|NameKey
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|AccountGroupName
name|groupName
init|=
name|db
operator|.
name|accountGroupNames
argument_list|()
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|groupName
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|AccountGroup
name|group
init|=
name|db
operator|.
name|accountGroups
argument_list|()
operator|.
name|get
argument_list|(
name|groupName
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|cap
operator|.
name|getPermission
argument_list|(
name|GlobalCapability
operator|.
name|CREATE_PROJECT
argument_list|,
literal|true
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|PermissionRule
argument_list|(
name|config
operator|.
name|resolve
argument_list|(
name|group
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|createGroupList
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
name|ui
operator|.
name|message
argument_list|(
literal|"Moved repository.*.createGroup to 'Create Project' capability"
argument_list|)
expr_stmt|;
name|cfg
operator|.
name|unset
argument_list|(
literal|"repository"
argument_list|,
literal|"*"
argument_list|,
literal|"createGroup"
argument_list|)
expr_stmt|;
name|cfgDirty
operator|=
literal|true
expr_stmt|;
block|}
name|AccountGroup
name|batch
init|=
name|db
operator|.
name|accountGroups
argument_list|()
operator|.
name|get
argument_list|(
name|sc
operator|.
name|batchUsersGroupId
argument_list|)
decl_stmt|;
if|if
condition|(
name|batch
operator|!=
literal|null
operator|&&
name|db
operator|.
name|accountGroupMembers
argument_list|()
operator|.
name|byGroup
argument_list|(
name|sc
operator|.
name|batchUsersGroupId
argument_list|)
operator|.
name|toList
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|isAccountGroupEmpty
condition|)
block|{
comment|// If the batch user group is not used, delete it.
comment|//
name|db
operator|.
name|accountGroups
argument_list|()
operator|.
name|delete
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|batch
argument_list|)
argument_list|)
expr_stmt|;
name|AccountGroupName
name|name
init|=
name|db
operator|.
name|accountGroupNames
argument_list|()
operator|.
name|get
argument_list|(
name|batch
operator|.
name|getNameKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|db
operator|.
name|accountGroupNames
argument_list|()
operator|.
name|delete
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|batch
operator|!=
literal|null
condition|)
block|{
name|cap
operator|.
name|getPermission
argument_list|(
name|GlobalCapability
operator|.
name|PRIORITY
argument_list|,
literal|true
argument_list|)
operator|.
name|getRule
argument_list|(
name|config
operator|.
name|resolve
argument_list|(
name|batch
argument_list|)
argument_list|,
literal|true
argument_list|)
operator|.
name|setAction
argument_list|(
name|Action
operator|.
name|BATCH
argument_list|)
expr_stmt|;
block|}
name|md
operator|.
name|setMessage
argument_list|(
literal|"Upgrade to Gerrit Code Review schema 57\n"
argument_list|)
expr_stmt|;
name|config
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|err
parameter_list|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"Cannot read account_group_includes"
argument_list|,
name|err
argument_list|)
throw|;
block|}
finally|finally
block|{
name|git
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ConfigInvalidException
name|err
parameter_list|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"Cannot read "
operator|+
name|allProjects
argument_list|,
name|err
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|err
parameter_list|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"Cannot update "
operator|+
name|allProjects
argument_list|,
name|err
argument_list|)
throw|;
block|}
if|if
condition|(
name|cfgDirty
condition|)
block|{
try|try
block|{
name|cfg
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|err
parameter_list|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"Cannot update "
operator|+
name|site
operator|.
name|gerrit_config
argument_list|,
name|err
argument_list|)
throw|;
block|}
block|}
comment|// We cannot set the columns to NULL, so use 0 and a DELETED tag.
name|sc
operator|.
name|adminGroupId
operator|=
operator|new
name|AccountGroup
operator|.
name|Id
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|sc
operator|.
name|adminGroupUUID
operator|=
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
literal|"DELETED"
argument_list|)
expr_stmt|;
name|sc
operator|.
name|anonymousGroupId
operator|=
operator|new
name|AccountGroup
operator|.
name|Id
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|sc
operator|.
name|registeredGroupId
operator|=
operator|new
name|AccountGroup
operator|.
name|Id
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|sc
operator|.
name|wildProjectName
operator|=
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
literal|"DELETED"
argument_list|)
expr_stmt|;
name|sc
operator|.
name|ownerGroupId
operator|=
operator|new
name|AccountGroup
operator|.
name|Id
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|sc
operator|.
name|batchUsersGroupId
operator|=
operator|new
name|AccountGroup
operator|.
name|Id
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|sc
operator|.
name|batchUsersGroupUUID
operator|=
operator|new
name|AccountGroup
operator|.
name|UUID
argument_list|(
literal|"DELETED"
argument_list|)
expr_stmt|;
name|sc
operator|.
name|registerEmailPrivateKey
operator|=
literal|"DELETED"
expr_stmt|;
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

