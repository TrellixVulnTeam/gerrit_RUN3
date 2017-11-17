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
DECL|package|com.google.gerrit.pgm
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|auto
operator|.
name|value
operator|.
name|AutoValue
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
name|gerrit
operator|.
name|pgm
operator|.
name|util
operator|.
name|SiteProgram
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
name|config
operator|.
name|ThreadSettingsConfig
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
name|schema
operator|.
name|DataSourceProvider
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
name|schema
operator|.
name|JdbcAccountPatchReviewStore
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
name|ArrayList
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
name|kohsuke
operator|.
name|args4j
operator|.
name|Option
import|;
end_import

begin_comment
comment|/** Migrates AccountPatchReviewDb from one to another */
end_comment

begin_class
DECL|class|MigrateAccountPatchReviewDb
specifier|public
class|class
name|MigrateAccountPatchReviewDb
extends|extends
name|SiteProgram
block|{
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--sourceUrl"
argument_list|,
name|usage
operator|=
literal|"Url of source database"
argument_list|)
DECL|field|sourceUrl
specifier|private
name|String
name|sourceUrl
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--chunkSize"
argument_list|,
name|usage
operator|=
literal|"chunk size of fetching from source and push to target on each time"
argument_list|)
DECL|field|chunkSize
specifier|private
specifier|static
name|long
name|chunkSize
init|=
literal|100000
decl_stmt|;
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|int
name|run
parameter_list|()
throws|throws
name|Exception
block|{
name|Injector
name|dbInjector
init|=
name|createDbInjector
argument_list|(
name|DataSourceProvider
operator|.
name|Context
operator|.
name|SINGLE_USER
argument_list|)
decl_stmt|;
name|SitePaths
name|sitePaths
init|=
operator|new
name|SitePaths
argument_list|(
name|getSitePath
argument_list|()
argument_list|)
decl_stmt|;
name|ThreadSettingsConfig
name|threadSettingsConfig
init|=
name|dbInjector
operator|.
name|getInstance
argument_list|(
name|ThreadSettingsConfig
operator|.
name|class
argument_list|)
decl_stmt|;
name|Config
name|fakeCfg
init|=
operator|new
name|Config
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|sourceUrl
argument_list|)
condition|)
block|{
name|fakeCfg
operator|.
name|setString
argument_list|(
literal|"accountPatchReviewDb"
argument_list|,
literal|null
argument_list|,
literal|"url"
argument_list|,
name|sourceUrl
argument_list|)
expr_stmt|;
block|}
name|JdbcAccountPatchReviewStore
name|sourceJdbcAccountPatchReviewStore
init|=
name|JdbcAccountPatchReviewStore
operator|.
name|createAccountPatchReviewStore
argument_list|(
name|fakeCfg
argument_list|,
name|sitePaths
argument_list|,
name|threadSettingsConfig
argument_list|)
decl_stmt|;
name|Config
name|cfg
init|=
name|dbInjector
operator|.
name|getInstance
argument_list|(
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
argument_list|)
decl_stmt|;
name|String
name|targetUrl
init|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"accountPatchReviewDb"
argument_list|,
literal|null
argument_list|,
literal|"url"
argument_list|)
decl_stmt|;
if|if
condition|(
name|targetUrl
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"accountPatchReviewDb.url is null in gerrit.config"
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"target Url: "
operator|+
name|targetUrl
argument_list|)
expr_stmt|;
name|JdbcAccountPatchReviewStore
name|targetJdbcAccountPatchReviewStore
init|=
name|JdbcAccountPatchReviewStore
operator|.
name|createAccountPatchReviewStore
argument_list|(
name|cfg
argument_list|,
name|sitePaths
argument_list|,
name|threadSettingsConfig
argument_list|)
decl_stmt|;
name|targetJdbcAccountPatchReviewStore
operator|.
name|createTableIfNotExists
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|isTargetTableEmpty
argument_list|(
name|targetJdbcAccountPatchReviewStore
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"target table is not empty, cannot proceed"
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
try|try
init|(
name|Connection
name|sourceCon
init|=
name|sourceJdbcAccountPatchReviewStore
operator|.
name|getConnection
argument_list|()
init|;
name|Connection
name|targetCon
operator|=
name|targetJdbcAccountPatchReviewStore
operator|.
name|getConnection
argument_list|()
init|;
name|PreparedStatement
name|sourceStmt
operator|=
name|sourceCon
operator|.
name|prepareStatement
argument_list|(
literal|"SELECT account_id, change_id, patch_set_id, file_name "
operator|+
literal|"FROM account_patch_reviews "
operator|+
literal|"LIMIT ? "
operator|+
literal|"OFFSET ?"
argument_list|)
init|;
name|PreparedStatement
name|targetStmt
operator|=
name|targetCon
operator|.
name|prepareStatement
argument_list|(
literal|"INSERT INTO account_patch_reviews "
operator|+
literal|"(account_id, change_id, patch_set_id, file_name) VALUES "
operator|+
literal|"(?, ?, ?, ?)"
argument_list|)
init|)
block|{
name|targetCon
operator|.
name|setAutoCommit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|long
name|offset
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|Row
argument_list|>
name|rows
init|=
name|selectRows
argument_list|(
name|sourceStmt
argument_list|,
name|offset
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|rows
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|insertRows
argument_list|(
name|targetCon
argument_list|,
name|targetStmt
argument_list|,
name|rows
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|rows
operator|.
name|size
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|printf
argument_list|(
literal|"%8d rows migrated\n"
argument_list|,
name|offset
argument_list|)
expr_stmt|;
name|rows
operator|=
name|selectRows
argument_list|(
name|sourceStmt
argument_list|,
name|offset
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Done"
argument_list|)
expr_stmt|;
block|}
return|return
literal|0
return|;
block|}
annotation|@
name|AutoValue
DECL|class|Row
specifier|abstract
specifier|static
class|class
name|Row
block|{
DECL|method|accountId ()
specifier|abstract
name|int
name|accountId
parameter_list|()
function_decl|;
DECL|method|changeId ()
specifier|abstract
name|int
name|changeId
parameter_list|()
function_decl|;
DECL|method|patchSetId ()
specifier|abstract
name|int
name|patchSetId
parameter_list|()
function_decl|;
DECL|method|fileName ()
specifier|abstract
name|String
name|fileName
parameter_list|()
function_decl|;
block|}
DECL|method|isTargetTableEmpty (JdbcAccountPatchReviewStore store)
specifier|private
specifier|static
name|boolean
name|isTargetTableEmpty
parameter_list|(
name|JdbcAccountPatchReviewStore
name|store
parameter_list|)
throws|throws
name|SQLException
block|{
try|try
init|(
name|Connection
name|con
init|=
name|store
operator|.
name|getConnection
argument_list|()
init|;
name|Statement
name|s
operator|=
name|con
operator|.
name|createStatement
argument_list|()
init|;
name|ResultSet
name|r
operator|=
name|s
operator|.
name|executeQuery
argument_list|(
literal|"SELECT COUNT(1) FROM account_patch_reviews"
argument_list|)
init|)
block|{
if|if
condition|(
name|r
operator|.
name|next
argument_list|()
condition|)
block|{
return|return
name|r
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
operator|==
literal|0
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
DECL|method|selectRows (PreparedStatement stmt, long offset)
specifier|private
specifier|static
name|List
argument_list|<
name|Row
argument_list|>
name|selectRows
parameter_list|(
name|PreparedStatement
name|stmt
parameter_list|,
name|long
name|offset
parameter_list|)
throws|throws
name|SQLException
block|{
name|List
argument_list|<
name|Row
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|stmt
operator|.
name|setLong
argument_list|(
literal|1
argument_list|,
name|chunkSize
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setLong
argument_list|(
literal|2
argument_list|,
name|offset
argument_list|)
expr_stmt|;
try|try
init|(
name|ResultSet
name|rs
init|=
name|stmt
operator|.
name|executeQuery
argument_list|()
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
name|results
operator|.
name|add
argument_list|(
operator|new
name|AutoValue_MigrateAccountPatchReviewDb_Row
argument_list|(
name|rs
operator|.
name|getInt
argument_list|(
literal|"account_id"
argument_list|)
argument_list|,
name|rs
operator|.
name|getInt
argument_list|(
literal|"change_id"
argument_list|)
argument_list|,
name|rs
operator|.
name|getInt
argument_list|(
literal|"patch_set_id"
argument_list|)
argument_list|,
name|rs
operator|.
name|getString
argument_list|(
literal|"file_name"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|results
return|;
block|}
DECL|method|insertRows (Connection con, PreparedStatement stmt, List<Row> rows)
specifier|private
specifier|static
name|void
name|insertRows
parameter_list|(
name|Connection
name|con
parameter_list|,
name|PreparedStatement
name|stmt
parameter_list|,
name|List
argument_list|<
name|Row
argument_list|>
name|rows
parameter_list|)
throws|throws
name|SQLException
block|{
for|for
control|(
name|Row
name|r
range|:
name|rows
control|)
block|{
name|stmt
operator|.
name|setLong
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|accountId
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setLong
argument_list|(
literal|2
argument_list|,
name|r
operator|.
name|changeId
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setLong
argument_list|(
literal|3
argument_list|,
name|r
operator|.
name|patchSetId
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setString
argument_list|(
literal|4
argument_list|,
name|r
operator|.
name|fileName
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|addBatch
argument_list|()
expr_stmt|;
block|}
name|stmt
operator|.
name|executeBatch
argument_list|()
expr_stmt|;
name|con
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

