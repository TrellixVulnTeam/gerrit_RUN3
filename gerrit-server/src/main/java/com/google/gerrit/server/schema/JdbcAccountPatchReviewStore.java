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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|MILLISECONDS
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
operator|.
name|SECONDS
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
name|primitives
operator|.
name|Ints
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
name|events
operator|.
name|LifecycleListener
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
name|registration
operator|.
name|DynamicItem
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
name|lifecycle
operator|.
name|LifecycleModule
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
name|client
operator|.
name|PatchSet
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
name|change
operator|.
name|AccountPatchReviewStore
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
name|ConfigUtil
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
name|OrmDuplicateKeyException
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
name|Collection
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
name|javax
operator|.
name|sql
operator|.
name|DataSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|dbcp
operator|.
name|BasicDataSource
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_class
DECL|class|JdbcAccountPatchReviewStore
specifier|public
specifier|abstract
class|class
name|JdbcAccountPatchReviewStore
implements|implements
name|AccountPatchReviewStore
implements|,
name|LifecycleListener
block|{
DECL|field|ACCOUNT_PATCH_REVIEW_DB
specifier|private
specifier|static
specifier|final
name|String
name|ACCOUNT_PATCH_REVIEW_DB
init|=
literal|"accountPatchReviewDb"
decl_stmt|;
DECL|field|H2_DB
specifier|private
specifier|static
specifier|final
name|String
name|H2_DB
init|=
literal|"h2"
decl_stmt|;
DECL|field|MARIADB
specifier|private
specifier|static
specifier|final
name|String
name|MARIADB
init|=
literal|"mariadb"
decl_stmt|;
DECL|field|MYSQL
specifier|private
specifier|static
specifier|final
name|String
name|MYSQL
init|=
literal|"mysql"
decl_stmt|;
DECL|field|POSTGRESQL
specifier|private
specifier|static
specifier|final
name|String
name|POSTGRESQL
init|=
literal|"postgresql"
decl_stmt|;
DECL|field|URL
specifier|private
specifier|static
specifier|final
name|String
name|URL
init|=
literal|"url"
decl_stmt|;
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|JdbcAccountPatchReviewStore
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|Module
specifier|public
specifier|static
class|class
name|Module
extends|extends
name|LifecycleModule
block|{
DECL|field|cfg
specifier|private
specifier|final
name|Config
name|cfg
decl_stmt|;
DECL|method|Module (Config cfg)
specifier|public
name|Module
parameter_list|(
name|Config
name|cfg
parameter_list|)
block|{
name|this
operator|.
name|cfg
operator|=
name|cfg
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configure ()
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|Class
argument_list|<
name|?
extends|extends
name|JdbcAccountPatchReviewStore
argument_list|>
name|impl
decl_stmt|;
name|String
name|url
init|=
name|cfg
operator|.
name|getString
argument_list|(
name|ACCOUNT_PATCH_REVIEW_DB
argument_list|,
literal|null
argument_list|,
name|URL
argument_list|)
decl_stmt|;
if|if
condition|(
name|url
operator|==
literal|null
operator|||
name|url
operator|.
name|contains
argument_list|(
name|H2_DB
argument_list|)
condition|)
block|{
name|impl
operator|=
name|H2AccountPatchReviewStore
operator|.
name|class
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|url
operator|.
name|contains
argument_list|(
name|POSTGRESQL
argument_list|)
condition|)
block|{
name|impl
operator|=
name|PostgresqlAccountPatchReviewStore
operator|.
name|class
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|url
operator|.
name|contains
argument_list|(
name|MYSQL
argument_list|)
condition|)
block|{
name|impl
operator|=
name|MysqlAccountPatchReviewStore
operator|.
name|class
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|url
operator|.
name|contains
argument_list|(
name|MARIADB
argument_list|)
condition|)
block|{
name|impl
operator|=
name|MariaDBAccountPatchReviewStore
operator|.
name|class
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unsupported driver type for account patch reviews db: "
operator|+
name|url
argument_list|)
throw|;
block|}
name|DynamicItem
operator|.
name|bind
argument_list|(
name|binder
argument_list|()
argument_list|,
name|AccountPatchReviewStore
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|impl
argument_list|)
expr_stmt|;
name|listener
argument_list|()
operator|.
name|to
argument_list|(
name|impl
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|ds
specifier|private
name|DataSource
name|ds
decl_stmt|;
DECL|method|createAccountPatchReviewStore ( Config cfg, SitePaths sitePaths, ThreadSettingsConfig threadSettingsConfig)
specifier|public
specifier|static
name|JdbcAccountPatchReviewStore
name|createAccountPatchReviewStore
parameter_list|(
name|Config
name|cfg
parameter_list|,
name|SitePaths
name|sitePaths
parameter_list|,
name|ThreadSettingsConfig
name|threadSettingsConfig
parameter_list|)
block|{
name|String
name|url
init|=
name|cfg
operator|.
name|getString
argument_list|(
name|ACCOUNT_PATCH_REVIEW_DB
argument_list|,
literal|null
argument_list|,
name|URL
argument_list|)
decl_stmt|;
if|if
condition|(
name|url
operator|==
literal|null
operator|||
name|url
operator|.
name|contains
argument_list|(
name|H2_DB
argument_list|)
condition|)
block|{
return|return
operator|new
name|H2AccountPatchReviewStore
argument_list|(
name|cfg
argument_list|,
name|sitePaths
argument_list|,
name|threadSettingsConfig
argument_list|)
return|;
block|}
if|if
condition|(
name|url
operator|.
name|contains
argument_list|(
name|POSTGRESQL
argument_list|)
condition|)
block|{
return|return
operator|new
name|PostgresqlAccountPatchReviewStore
argument_list|(
name|cfg
argument_list|,
name|sitePaths
argument_list|,
name|threadSettingsConfig
argument_list|)
return|;
block|}
if|if
condition|(
name|url
operator|.
name|contains
argument_list|(
name|MYSQL
argument_list|)
condition|)
block|{
return|return
operator|new
name|MysqlAccountPatchReviewStore
argument_list|(
name|cfg
argument_list|,
name|sitePaths
argument_list|,
name|threadSettingsConfig
argument_list|)
return|;
block|}
if|if
condition|(
name|url
operator|.
name|contains
argument_list|(
name|MARIADB
argument_list|)
condition|)
block|{
return|return
operator|new
name|MariaDBAccountPatchReviewStore
argument_list|(
name|cfg
argument_list|,
name|sitePaths
argument_list|,
name|threadSettingsConfig
argument_list|)
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unsupported driver type for account patch reviews db: "
operator|+
name|url
argument_list|)
throw|;
block|}
DECL|method|JdbcAccountPatchReviewStore ( Config cfg, SitePaths sitePaths, ThreadSettingsConfig threadSettingsConfig)
specifier|protected
name|JdbcAccountPatchReviewStore
parameter_list|(
name|Config
name|cfg
parameter_list|,
name|SitePaths
name|sitePaths
parameter_list|,
name|ThreadSettingsConfig
name|threadSettingsConfig
parameter_list|)
block|{
name|this
operator|.
name|ds
operator|=
name|createDataSource
argument_list|(
name|cfg
argument_list|,
name|sitePaths
argument_list|,
name|threadSettingsConfig
argument_list|)
expr_stmt|;
block|}
DECL|method|JdbcAccountPatchReviewStore (DataSource ds)
specifier|protected
name|JdbcAccountPatchReviewStore
parameter_list|(
name|DataSource
name|ds
parameter_list|)
block|{
name|this
operator|.
name|ds
operator|=
name|ds
expr_stmt|;
block|}
DECL|method|getUrl (@erritServerConfig Config cfg, SitePaths sitePaths)
specifier|private
specifier|static
name|String
name|getUrl
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|,
name|SitePaths
name|sitePaths
parameter_list|)
block|{
name|String
name|url
init|=
name|cfg
operator|.
name|getString
argument_list|(
name|ACCOUNT_PATCH_REVIEW_DB
argument_list|,
literal|null
argument_list|,
name|URL
argument_list|)
decl_stmt|;
if|if
condition|(
name|url
operator|==
literal|null
condition|)
block|{
return|return
name|H2
operator|.
name|createUrl
argument_list|(
name|sitePaths
operator|.
name|db_dir
operator|.
name|resolve
argument_list|(
literal|"account_patch_reviews"
argument_list|)
argument_list|)
return|;
block|}
return|return
name|url
return|;
block|}
DECL|method|createDataSource ( Config cfg, SitePaths sitePaths, ThreadSettingsConfig threadSettingsConfig)
specifier|private
specifier|static
name|DataSource
name|createDataSource
parameter_list|(
name|Config
name|cfg
parameter_list|,
name|SitePaths
name|sitePaths
parameter_list|,
name|ThreadSettingsConfig
name|threadSettingsConfig
parameter_list|)
block|{
name|BasicDataSource
name|datasource
init|=
operator|new
name|BasicDataSource
argument_list|()
decl_stmt|;
name|String
name|url
init|=
name|getUrl
argument_list|(
name|cfg
argument_list|,
name|sitePaths
argument_list|)
decl_stmt|;
name|int
name|poolLimit
init|=
name|threadSettingsConfig
operator|.
name|getDatabasePoolLimit
argument_list|()
decl_stmt|;
name|datasource
operator|.
name|setUrl
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|datasource
operator|.
name|setDriverClassName
argument_list|(
name|getDriverFromUrl
argument_list|(
name|url
argument_list|)
argument_list|)
expr_stmt|;
name|datasource
operator|.
name|setMaxActive
argument_list|(
name|cfg
operator|.
name|getInt
argument_list|(
name|ACCOUNT_PATCH_REVIEW_DB
argument_list|,
literal|"poolLimit"
argument_list|,
name|poolLimit
argument_list|)
argument_list|)
expr_stmt|;
name|datasource
operator|.
name|setMinIdle
argument_list|(
name|cfg
operator|.
name|getInt
argument_list|(
name|ACCOUNT_PATCH_REVIEW_DB
argument_list|,
literal|"poolminidle"
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|datasource
operator|.
name|setMaxIdle
argument_list|(
name|cfg
operator|.
name|getInt
argument_list|(
name|ACCOUNT_PATCH_REVIEW_DB
argument_list|,
literal|"poolmaxidle"
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|poolLimit
argument_list|,
literal|16
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|datasource
operator|.
name|setInitialSize
argument_list|(
name|datasource
operator|.
name|getMinIdle
argument_list|()
argument_list|)
expr_stmt|;
name|datasource
operator|.
name|setMaxWait
argument_list|(
name|ConfigUtil
operator|.
name|getTimeUnit
argument_list|(
name|cfg
argument_list|,
name|ACCOUNT_PATCH_REVIEW_DB
argument_list|,
literal|null
argument_list|,
literal|"poolmaxwait"
argument_list|,
name|MILLISECONDS
operator|.
name|convert
argument_list|(
literal|30
argument_list|,
name|SECONDS
argument_list|)
argument_list|,
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|evictIdleTimeMs
init|=
literal|1000L
operator|*
literal|60
decl_stmt|;
name|datasource
operator|.
name|setMinEvictableIdleTimeMillis
argument_list|(
name|evictIdleTimeMs
argument_list|)
expr_stmt|;
name|datasource
operator|.
name|setTimeBetweenEvictionRunsMillis
argument_list|(
name|evictIdleTimeMs
operator|/
literal|2
argument_list|)
expr_stmt|;
return|return
name|datasource
return|;
block|}
DECL|method|getDriverFromUrl (String url)
specifier|private
specifier|static
name|String
name|getDriverFromUrl
parameter_list|(
name|String
name|url
parameter_list|)
block|{
if|if
condition|(
name|url
operator|.
name|contains
argument_list|(
name|POSTGRESQL
argument_list|)
condition|)
block|{
return|return
literal|"org.postgresql.Driver"
return|;
block|}
if|if
condition|(
name|url
operator|.
name|contains
argument_list|(
name|MYSQL
argument_list|)
condition|)
block|{
return|return
literal|"com.mysql.jdbc.Driver"
return|;
block|}
if|if
condition|(
name|url
operator|.
name|contains
argument_list|(
name|MARIADB
argument_list|)
condition|)
block|{
return|return
literal|"org.mariadb.jdbc.Driver"
return|;
block|}
return|return
literal|"org.h2.Driver"
return|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
try|try
block|{
name|createTableIfNotExists
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Failed to create table to store account patch reviews"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getConnection ()
specifier|public
name|Connection
name|getConnection
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|ds
operator|.
name|getConnection
argument_list|()
return|;
block|}
DECL|method|createTableIfNotExists ()
specifier|public
name|void
name|createTableIfNotExists
parameter_list|()
throws|throws
name|OrmException
block|{
try|try
init|(
name|Connection
name|con
init|=
name|ds
operator|.
name|getConnection
argument_list|()
init|;
name|Statement
name|stmt
operator|=
name|con
operator|.
name|createStatement
argument_list|()
init|)
block|{
name|doCreateTable
argument_list|(
name|stmt
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
name|convertError
argument_list|(
literal|"create"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|doCreateTable (Statement stmt)
specifier|private
specifier|static
name|void
name|doCreateTable
parameter_list|(
name|Statement
name|stmt
parameter_list|)
throws|throws
name|SQLException
block|{
name|stmt
operator|.
name|executeUpdate
argument_list|(
literal|"CREATE TABLE IF NOT EXISTS account_patch_reviews ("
operator|+
literal|"account_id INTEGER DEFAULT 0 NOT NULL, "
operator|+
literal|"change_id INTEGER DEFAULT 0 NOT NULL, "
operator|+
literal|"patch_set_id INTEGER DEFAULT 0 NOT NULL, "
operator|+
literal|"file_name VARCHAR(4096) DEFAULT '' NOT NULL, "
operator|+
literal|"CONSTRAINT primary_key_account_patch_reviews "
operator|+
literal|"PRIMARY KEY (change_id, patch_set_id, account_id, file_name)"
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
DECL|method|dropTableIfExists ()
specifier|public
name|void
name|dropTableIfExists
parameter_list|()
throws|throws
name|OrmException
block|{
try|try
init|(
name|Connection
name|con
init|=
name|ds
operator|.
name|getConnection
argument_list|()
init|;
name|Statement
name|stmt
operator|=
name|con
operator|.
name|createStatement
argument_list|()
init|)
block|{
name|stmt
operator|.
name|executeUpdate
argument_list|(
literal|"DROP TABLE IF EXISTS account_patch_reviews"
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
name|convertError
argument_list|(
literal|"create"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|markReviewed (PatchSet.Id psId, Account.Id accountId, String path)
specifier|public
name|boolean
name|markReviewed
parameter_list|(
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|OrmException
block|{
try|try
init|(
name|Connection
name|con
init|=
name|ds
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
name|stmt
operator|.
name|setInt
argument_list|(
literal|1
argument_list|,
name|accountId
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setInt
argument_list|(
literal|2
argument_list|,
name|psId
operator|.
name|getParentKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setInt
argument_list|(
literal|3
argument_list|,
name|psId
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setString
argument_list|(
literal|4
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|OrmException
name|ormException
init|=
name|convertError
argument_list|(
literal|"insert"
argument_list|,
name|e
argument_list|)
decl_stmt|;
if|if
condition|(
name|ormException
operator|instanceof
name|OrmDuplicateKeyException
condition|)
block|{
return|return
literal|false
return|;
block|}
throw|throw
name|ormException
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|markReviewed (PatchSet.Id psId, Account.Id accountId, Collection<String> paths)
specifier|public
name|void
name|markReviewed
parameter_list|(
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|)
throws|throws
name|OrmException
block|{
if|if
condition|(
name|paths
operator|==
literal|null
operator|||
name|paths
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
try|try
init|(
name|Connection
name|con
init|=
name|ds
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
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|stmt
operator|.
name|setInt
argument_list|(
literal|1
argument_list|,
name|accountId
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setInt
argument_list|(
literal|2
argument_list|,
name|psId
operator|.
name|getParentKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setInt
argument_list|(
literal|3
argument_list|,
name|psId
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setString
argument_list|(
literal|4
argument_list|,
name|path
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
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|OrmException
name|ormException
init|=
name|convertError
argument_list|(
literal|"insert"
argument_list|,
name|e
argument_list|)
decl_stmt|;
if|if
condition|(
name|ormException
operator|instanceof
name|OrmDuplicateKeyException
condition|)
block|{
return|return;
block|}
throw|throw
name|ormException
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|clearReviewed (PatchSet.Id psId, Account.Id accountId, String path)
specifier|public
name|void
name|clearReviewed
parameter_list|(
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|OrmException
block|{
try|try
init|(
name|Connection
name|con
init|=
name|ds
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
literal|"DELETE FROM account_patch_reviews "
operator|+
literal|"WHERE account_id = ? AND change_id = ? AND "
operator|+
literal|"patch_set_id = ? AND file_name = ?"
argument_list|)
init|)
block|{
name|stmt
operator|.
name|setInt
argument_list|(
literal|1
argument_list|,
name|accountId
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setInt
argument_list|(
literal|2
argument_list|,
name|psId
operator|.
name|getParentKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setInt
argument_list|(
literal|3
argument_list|,
name|psId
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setString
argument_list|(
literal|4
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|convertError
argument_list|(
literal|"delete"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|clearReviewed (PatchSet.Id psId)
specifier|public
name|void
name|clearReviewed
parameter_list|(
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|)
throws|throws
name|OrmException
block|{
try|try
init|(
name|Connection
name|con
init|=
name|ds
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
literal|"DELETE FROM account_patch_reviews "
operator|+
literal|"WHERE change_id = ? AND patch_set_id = ?"
argument_list|)
init|)
block|{
name|stmt
operator|.
name|setInt
argument_list|(
literal|1
argument_list|,
name|psId
operator|.
name|getParentKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setInt
argument_list|(
literal|2
argument_list|,
name|psId
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|executeUpdate
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|convertError
argument_list|(
literal|"delete"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|findReviewed (PatchSet.Id psId, Account.Id accountId)
specifier|public
name|Optional
argument_list|<
name|PatchSetWithReviewedFiles
argument_list|>
name|findReviewed
parameter_list|(
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|,
name|Account
operator|.
name|Id
name|accountId
parameter_list|)
throws|throws
name|OrmException
block|{
try|try
init|(
name|Connection
name|con
init|=
name|ds
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
literal|"SELECT patch_set_id, file_name FROM account_patch_reviews APR1 "
operator|+
literal|"WHERE account_id = ? AND change_id = ? AND patch_set_id = "
operator|+
literal|"(SELECT MAX(patch_set_id) FROM account_patch_reviews APR2 WHERE "
operator|+
literal|"APR1.account_id = APR2.account_id "
operator|+
literal|"AND APR1.change_id = APR2.change_id "
operator|+
literal|"AND patch_set_id<= ?)"
argument_list|)
init|)
block|{
name|stmt
operator|.
name|setInt
argument_list|(
literal|1
argument_list|,
name|accountId
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setInt
argument_list|(
literal|2
argument_list|,
name|psId
operator|.
name|getParentKey
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|stmt
operator|.
name|setInt
argument_list|(
literal|3
argument_list|,
name|psId
operator|.
name|get
argument_list|()
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
if|if
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|PatchSet
operator|.
name|Id
name|id
init|=
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|psId
operator|.
name|getParentKey
argument_list|()
argument_list|,
name|rs
operator|.
name|getInt
argument_list|(
literal|"patch_set_id"
argument_list|)
argument_list|)
decl_stmt|;
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|String
argument_list|>
name|builder
init|=
name|ImmutableSet
operator|.
name|builder
argument_list|()
decl_stmt|;
do|do
block|{
name|builder
operator|.
name|add
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|"file_name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
do|;
return|return
name|Optional
operator|.
name|of
argument_list|(
name|AccountPatchReviewStore
operator|.
name|PatchSetWithReviewedFiles
operator|.
name|create
argument_list|(
name|id
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|convertError
argument_list|(
literal|"select"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|convertError (String op, SQLException err)
specifier|public
name|OrmException
name|convertError
parameter_list|(
name|String
name|op
parameter_list|,
name|SQLException
name|err
parameter_list|)
block|{
if|if
condition|(
name|err
operator|.
name|getCause
argument_list|()
operator|==
literal|null
operator|&&
name|err
operator|.
name|getNextException
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|err
operator|.
name|initCause
argument_list|(
name|err
operator|.
name|getNextException
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|OrmException
argument_list|(
name|op
operator|+
literal|" failure on account_patch_reviews"
argument_list|,
name|err
argument_list|)
return|;
block|}
DECL|method|getSQLState (SQLException err)
specifier|private
specifier|static
name|String
name|getSQLState
parameter_list|(
name|SQLException
name|err
parameter_list|)
block|{
name|String
name|ec
decl_stmt|;
name|SQLException
name|next
init|=
name|err
decl_stmt|;
do|do
block|{
name|ec
operator|=
name|next
operator|.
name|getSQLState
argument_list|()
expr_stmt|;
name|next
operator|=
name|next
operator|.
name|getNextException
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|ec
operator|==
literal|null
operator|&&
name|next
operator|!=
literal|null
condition|)
do|;
return|return
name|ec
return|;
block|}
DECL|method|getSQLStateInt (SQLException err)
specifier|protected
specifier|static
name|int
name|getSQLStateInt
parameter_list|(
name|SQLException
name|err
parameter_list|)
block|{
name|String
name|s
init|=
name|getSQLState
argument_list|(
name|err
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|Integer
name|i
init|=
name|Ints
operator|.
name|tryParse
argument_list|(
name|s
argument_list|)
decl_stmt|;
return|return
name|i
operator|!=
literal|null
condition|?
name|i
else|:
operator|-
literal|1
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

