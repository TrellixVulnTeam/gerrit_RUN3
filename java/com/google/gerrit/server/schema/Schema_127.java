begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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

begin_class
DECL|class|Schema_127
specifier|public
class|class
name|Schema_127
extends|extends
name|ReviewDbSchemaVersion
block|{
DECL|field|MAX_BATCH_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|MAX_BATCH_SIZE
init|=
literal|1000
decl_stmt|;
DECL|field|sitePaths
specifier|private
specifier|final
name|SitePaths
name|sitePaths
decl_stmt|;
DECL|field|cfg
specifier|private
specifier|final
name|Config
name|cfg
decl_stmt|;
DECL|field|threadSettingsConfig
specifier|private
specifier|final
name|ThreadSettingsConfig
name|threadSettingsConfig
decl_stmt|;
annotation|@
name|Inject
DECL|method|Schema_127 ( Provider<Schema_126> prior, SitePaths sitePaths, @GerritServerConfig Config cfg, ThreadSettingsConfig threadSettingsConfig)
name|Schema_127
parameter_list|(
name|Provider
argument_list|<
name|Schema_126
argument_list|>
name|prior
parameter_list|,
name|SitePaths
name|sitePaths
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|,
name|ThreadSettingsConfig
name|threadSettingsConfig
parameter_list|)
block|{
name|super
argument_list|(
name|prior
argument_list|)
expr_stmt|;
name|this
operator|.
name|sitePaths
operator|=
name|sitePaths
expr_stmt|;
name|this
operator|.
name|cfg
operator|=
name|cfg
expr_stmt|;
name|this
operator|.
name|threadSettingsConfig
operator|=
name|threadSettingsConfig
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
name|JdbcAccountPatchReviewStore
name|jdbcAccountPatchReviewStore
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
name|jdbcAccountPatchReviewStore
operator|.
name|dropTableIfExists
argument_list|()
expr_stmt|;
name|jdbcAccountPatchReviewStore
operator|.
name|createTableIfNotExists
argument_list|()
expr_stmt|;
try|try
init|(
name|Connection
name|con
init|=
name|jdbcAccountPatchReviewStore
operator|.
name|getConnection
argument_list|()
init|;
name|PreparedStatement
name|stmt
operator|=
name|con
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
name|int
name|batchCount
init|=
literal|0
decl_stmt|;
try|try
init|(
name|Statement
name|s
init|=
name|newStatement
argument_list|(
name|db
argument_list|)
init|;
name|ResultSet
name|rs
operator|=
name|s
operator|.
name|executeQuery
argument_list|(
literal|"SELECT * from account_patch_reviews"
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
name|stmt
operator|.
name|setInt
argument_list|(
literal|1
argument_list|,
name|rs
operator|.
name|getInt
argument_list|(
literal|"account_id"
argument_list|)
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setInt
argument_list|(
literal|2
argument_list|,
name|rs
operator|.
name|getInt
argument_list|(
literal|"change_id"
argument_list|)
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setInt
argument_list|(
literal|3
argument_list|,
name|rs
operator|.
name|getInt
argument_list|(
literal|"patch_set_id"
argument_list|)
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setString
argument_list|(
literal|4
argument_list|,
name|rs
operator|.
name|getString
argument_list|(
literal|"file_name"
argument_list|)
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|addBatch
argument_list|()
expr_stmt|;
name|batchCount
operator|++
expr_stmt|;
if|if
condition|(
name|batchCount
operator|>=
name|MAX_BATCH_SIZE
condition|)
block|{
name|stmt
operator|.
name|executeBatch
argument_list|()
expr_stmt|;
name|batchCount
operator|=
literal|0
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|batchCount
operator|>
literal|0
condition|)
block|{
name|stmt
operator|.
name|executeBatch
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|jdbcAccountPatchReviewStore
operator|.
name|convertError
argument_list|(
literal|"insert"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

