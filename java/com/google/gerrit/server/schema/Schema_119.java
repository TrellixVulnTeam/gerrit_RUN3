begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
import|import static
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
name|CoreDownloadSchemes
operator|.
name|ANON_GIT
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
name|reviewdb
operator|.
name|client
operator|.
name|CoreDownloadSchemes
operator|.
name|ANON_HTTP
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
name|reviewdb
operator|.
name|client
operator|.
name|CoreDownloadSchemes
operator|.
name|HTTP
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
name|reviewdb
operator|.
name|client
operator|.
name|CoreDownloadSchemes
operator|.
name|REPO_DOWNLOAD
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
name|reviewdb
operator|.
name|client
operator|.
name|CoreDownloadSchemes
operator|.
name|SSH
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
name|config
operator|.
name|ConfigUtil
operator|.
name|storeSection
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
name|base
operator|.
name|Preconditions
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
name|base
operator|.
name|Strings
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
name|ImmutableMap
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
name|GeneralPreferencesInfo
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
name|GeneralPreferencesInfo
operator|.
name|DateFormat
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
name|GeneralPreferencesInfo
operator|.
name|DiffView
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
name|GeneralPreferencesInfo
operator|.
name|DownloadCommand
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
name|GeneralPreferencesInfo
operator|.
name|EmailStrategy
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
name|GeneralPreferencesInfo
operator|.
name|ReviewCategoryStrategy
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
name|GeneralPreferencesInfo
operator|.
name|TimeFormat
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
name|UserConfigSections
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
name|Provider
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
name|Connection
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
name|sql
operator|.
name|Statement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|BatchRefUpdate
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
name|NullProgressMonitor
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
name|revwalk
operator|.
name|RevWalk
import|;
end_import

begin_class
DECL|class|Schema_119
specifier|public
class|class
name|Schema_119
extends|extends
name|SchemaVersion
block|{
DECL|field|LEGACY_DISPLAYNAME_MAP
specifier|private
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|LEGACY_DISPLAYNAME_MAP
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|String
decl|>
name|of
argument_list|(
literal|"ANON_GIT"
argument_list|,
name|ANON_GIT
argument_list|,
literal|"ANON_HTTP"
argument_list|,
name|ANON_HTTP
argument_list|,
literal|"HTTP"
argument_list|,
name|HTTP
argument_list|,
literal|"SSH"
argument_list|,
name|SSH
argument_list|,
literal|"REPO_DOWNLOAD"
argument_list|,
name|REPO_DOWNLOAD
argument_list|)
decl_stmt|;
DECL|field|mgr
specifier|private
specifier|final
name|GitRepositoryManager
name|mgr
decl_stmt|;
DECL|field|allUsersName
specifier|private
specifier|final
name|AllUsersName
name|allUsersName
decl_stmt|;
DECL|field|serverUser
specifier|private
specifier|final
name|PersonIdent
name|serverUser
decl_stmt|;
annotation|@
name|Inject
DECL|method|Schema_119 ( Provider<Schema_118> prior, GitRepositoryManager mgr, AllUsersName allUsersName, @GerritPersonIdent PersonIdent serverUser)
name|Schema_119
parameter_list|(
name|Provider
argument_list|<
name|Schema_118
argument_list|>
name|prior
parameter_list|,
name|GitRepositoryManager
name|mgr
parameter_list|,
name|AllUsersName
name|allUsersName
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
name|mgr
operator|=
name|mgr
expr_stmt|;
name|this
operator|.
name|allUsersName
operator|=
name|allUsersName
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
throws|,
name|SQLException
block|{
name|JdbcSchema
name|schema
init|=
operator|(
name|JdbcSchema
operator|)
name|db
decl_stmt|;
name|Connection
name|connection
init|=
name|schema
operator|.
name|getConnection
argument_list|()
decl_stmt|;
name|String
name|tableName
init|=
literal|"accounts"
decl_stmt|;
name|String
name|emailStrategy
init|=
literal|"email_strategy"
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|columns
init|=
name|schema
operator|.
name|getDialect
argument_list|()
operator|.
name|listColumns
argument_list|(
name|connection
argument_list|,
name|tableName
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|GeneralPreferencesInfo
argument_list|>
name|imports
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
try|try
init|(
name|Statement
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
name|createStatement
argument_list|()
init|;
name|ResultSet
name|rs
operator|=
name|stmt
operator|.
name|executeQuery
argument_list|(
literal|"select "
operator|+
literal|"account_id, "
operator|+
literal|"maximum_page_size, "
operator|+
literal|"show_site_header, "
operator|+
literal|"use_flash_clipboard, "
operator|+
literal|"download_url, "
operator|+
literal|"download_command, "
operator|+
operator|(
name|columns
operator|.
name|contains
argument_list|(
name|emailStrategy
argument_list|)
condition|?
name|emailStrategy
operator|+
literal|", "
else|:
literal|"copy_self_on_email, "
operator|)
operator|+
literal|"date_format, "
operator|+
literal|"time_format, "
operator|+
literal|"relative_date_in_change_table, "
operator|+
literal|"diff_view, "
operator|+
literal|"size_bar_in_change_table, "
operator|+
literal|"legacycid_in_change_table, "
operator|+
literal|"review_category_strategy, "
operator|+
literal|"mute_common_path_prefixes "
operator|+
literal|"from "
operator|+
name|tableName
argument_list|)
init|)
block|{
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|GeneralPreferencesInfo
name|p
init|=
operator|new
name|GeneralPreferencesInfo
argument_list|()
decl_stmt|;
name|Account
operator|.
name|Id
name|accountId
init|=
operator|new
name|Account
operator|.
name|Id
argument_list|(
name|rs
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|p
operator|.
name|changesPerPage
operator|=
operator|(
name|int
operator|)
name|rs
operator|.
name|getShort
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|p
operator|.
name|showSiteHeader
operator|=
name|toBoolean
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|useFlashClipboard
operator|=
name|toBoolean
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|downloadScheme
operator|=
name|convertToModernNames
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|downloadCommand
operator|=
name|toDownloadCommand
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|emailStrategy
operator|=
name|toEmailStrategy
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|7
argument_list|)
argument_list|,
name|columns
operator|.
name|contains
argument_list|(
name|emailStrategy
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|dateFormat
operator|=
name|toDateFormat
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|timeFormat
operator|=
name|toTimeFormat
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|9
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|relativeDateInChangeTable
operator|=
name|toBoolean
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|diffView
operator|=
name|toDiffView
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|11
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|sizeBarInChangeTable
operator|=
name|toBoolean
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|12
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|legacycidInChangeTable
operator|=
name|toBoolean
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|13
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|reviewCategoryStrategy
operator|=
name|toReviewCategoryStrategy
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|14
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|muteCommonPathPrefixes
operator|=
name|toBoolean
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|15
argument_list|)
argument_list|)
expr_stmt|;
name|p
operator|.
name|defaultBaseForMerges
operator|=
name|GeneralPreferencesInfo
operator|.
name|defaults
argument_list|()
operator|.
name|defaultBaseForMerges
expr_stmt|;
name|imports
operator|.
name|put
argument_list|(
name|accountId
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|imports
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
try|try
init|(
name|Repository
name|git
init|=
name|mgr
operator|.
name|openRepository
argument_list|(
name|allUsersName
argument_list|)
init|;
name|RevWalk
name|rw
operator|=
operator|new
name|RevWalk
argument_list|(
name|git
argument_list|)
init|)
block|{
name|BatchRefUpdate
name|bru
init|=
name|git
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|newBatchUpdate
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Account
operator|.
name|Id
argument_list|,
name|GeneralPreferencesInfo
argument_list|>
name|e
range|:
name|imports
operator|.
name|entrySet
argument_list|()
control|)
block|{
try|try
init|(
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
name|allUsersName
argument_list|,
name|git
argument_list|,
name|bru
argument_list|)
init|)
block|{
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
name|VersionedAccountPreferences
name|p
init|=
name|VersionedAccountPreferences
operator|.
name|forUser
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|p
operator|.
name|load
argument_list|(
name|md
argument_list|)
expr_stmt|;
name|storeSection
argument_list|(
name|p
operator|.
name|getConfig
argument_list|()
argument_list|,
name|UserConfigSections
operator|.
name|GENERAL
argument_list|,
literal|null
argument_list|,
name|e
operator|.
name|getValue
argument_list|()
argument_list|,
name|GeneralPreferencesInfo
operator|.
name|defaults
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
block|}
name|bru
operator|.
name|execute
argument_list|(
name|rw
argument_list|,
name|NullProgressMonitor
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConfigInvalidException
decl||
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
DECL|method|convertToModernNames (String s)
specifier|private
name|String
name|convertToModernNames
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|s
argument_list|)
operator|&&
name|LEGACY_DISPLAYNAME_MAP
operator|.
name|containsKey
argument_list|(
name|s
argument_list|)
condition|?
name|LEGACY_DISPLAYNAME_MAP
operator|.
name|get
argument_list|(
name|s
argument_list|)
else|:
name|s
return|;
block|}
DECL|method|toDownloadCommand (String v)
specifier|private
specifier|static
name|DownloadCommand
name|toDownloadCommand
parameter_list|(
name|String
name|v
parameter_list|)
block|{
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return
name|DownloadCommand
operator|.
name|CHECKOUT
return|;
block|}
return|return
name|DownloadCommand
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
return|;
block|}
DECL|method|toDateFormat (String v)
specifier|private
specifier|static
name|DateFormat
name|toDateFormat
parameter_list|(
name|String
name|v
parameter_list|)
block|{
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return
name|DateFormat
operator|.
name|STD
return|;
block|}
return|return
name|DateFormat
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
return|;
block|}
DECL|method|toTimeFormat (String v)
specifier|private
specifier|static
name|TimeFormat
name|toTimeFormat
parameter_list|(
name|String
name|v
parameter_list|)
block|{
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return
name|TimeFormat
operator|.
name|HHMM_12
return|;
block|}
return|return
name|TimeFormat
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
return|;
block|}
DECL|method|toDiffView (String v)
specifier|private
specifier|static
name|DiffView
name|toDiffView
parameter_list|(
name|String
name|v
parameter_list|)
block|{
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return
name|DiffView
operator|.
name|SIDE_BY_SIDE
return|;
block|}
return|return
name|DiffView
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
return|;
block|}
DECL|method|toEmailStrategy (String v, boolean emailStrategyColumnExists)
specifier|private
specifier|static
name|EmailStrategy
name|toEmailStrategy
parameter_list|(
name|String
name|v
parameter_list|,
name|boolean
name|emailStrategyColumnExists
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return
name|EmailStrategy
operator|.
name|ENABLED
return|;
block|}
if|if
condition|(
name|emailStrategyColumnExists
condition|)
block|{
return|return
name|EmailStrategy
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
return|;
block|}
if|if
condition|(
name|v
operator|.
name|equals
argument_list|(
literal|"N"
argument_list|)
condition|)
block|{
comment|// EMAIL_STRATEGY='ENABLED' WHERE (COPY_SELF_ON_EMAIL='N')
return|return
name|EmailStrategy
operator|.
name|ENABLED
return|;
block|}
elseif|else
if|if
condition|(
name|v
operator|.
name|equals
argument_list|(
literal|"Y"
argument_list|)
condition|)
block|{
comment|// EMAIL_STRATEGY='CC_ON_OWN_COMMENTS' WHERE (COPY_SELF_ON_EMAIL='Y')
return|return
name|EmailStrategy
operator|.
name|CC_ON_OWN_COMMENTS
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|OrmException
argument_list|(
literal|"invalid value in accounts.copy_self_on_email: "
operator|+
name|v
argument_list|)
throw|;
block|}
block|}
DECL|method|toReviewCategoryStrategy (String v)
specifier|private
specifier|static
name|ReviewCategoryStrategy
name|toReviewCategoryStrategy
parameter_list|(
name|String
name|v
parameter_list|)
block|{
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return
name|ReviewCategoryStrategy
operator|.
name|NONE
return|;
block|}
return|return
name|ReviewCategoryStrategy
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
return|;
block|}
DECL|method|toBoolean (String v)
specifier|private
specifier|static
name|boolean
name|toBoolean
parameter_list|(
name|String
name|v
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|v
operator|.
name|equals
argument_list|(
literal|"Y"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

