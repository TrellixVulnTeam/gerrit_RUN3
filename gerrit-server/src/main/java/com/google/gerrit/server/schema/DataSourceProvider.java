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
name|server
operator|.
name|config
operator|.
name|ConfigSection
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
name|gwtorm
operator|.
name|jdbc
operator|.
name|SimpleDataSource
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|ProvisionException
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
name|Singleton
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
name|Properties
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

begin_comment
comment|/** Provides access to the DataSource. */
end_comment

begin_class
annotation|@
name|Singleton
DECL|class|DataSourceProvider
specifier|public
specifier|final
class|class
name|DataSourceProvider
implements|implements
name|Provider
argument_list|<
name|DataSource
argument_list|>
implements|,
name|LifecycleListener
block|{
DECL|field|ds
specifier|private
specifier|final
name|DataSource
name|ds
decl_stmt|;
annotation|@
name|Inject
DECL|method|DataSourceProvider (final SitePaths site, @GerritServerConfig final Config cfg, Context ctx, DataSourceType dst)
name|DataSourceProvider
parameter_list|(
specifier|final
name|SitePaths
name|site
parameter_list|,
annotation|@
name|GerritServerConfig
specifier|final
name|Config
name|cfg
parameter_list|,
name|Context
name|ctx
parameter_list|,
name|DataSourceType
name|dst
parameter_list|)
block|{
name|ds
operator|=
name|open
argument_list|(
name|site
argument_list|,
name|cfg
argument_list|,
name|ctx
argument_list|,
name|dst
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
specifier|synchronized
name|DataSource
name|get
parameter_list|()
block|{
return|return
name|ds
return|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|stop ()
specifier|public
specifier|synchronized
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|ds
operator|instanceof
name|BasicDataSource
condition|)
block|{
try|try
block|{
operator|(
operator|(
name|BasicDataSource
operator|)
name|ds
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
comment|// Ignore the close failure.
block|}
block|}
block|}
DECL|enum|Context
specifier|public
specifier|static
enum|enum
name|Context
block|{
DECL|enumConstant|SINGLE_USER
DECL|enumConstant|MULTI_USER
name|SINGLE_USER
block|,
name|MULTI_USER
block|;   }
DECL|method|open (final SitePaths site, final Config cfg, final Context context, final DataSourceType dst)
specifier|private
name|DataSource
name|open
parameter_list|(
specifier|final
name|SitePaths
name|site
parameter_list|,
specifier|final
name|Config
name|cfg
parameter_list|,
specifier|final
name|Context
name|context
parameter_list|,
specifier|final
name|DataSourceType
name|dst
parameter_list|)
block|{
name|ConfigSection
name|dbs
init|=
operator|new
name|ConfigSection
argument_list|(
name|cfg
argument_list|,
literal|"database"
argument_list|)
decl_stmt|;
name|String
name|driver
init|=
name|dbs
operator|.
name|optional
argument_list|(
literal|"driver"
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|driver
argument_list|)
condition|)
block|{
name|driver
operator|=
name|dst
operator|.
name|getDriver
argument_list|()
expr_stmt|;
block|}
name|String
name|url
init|=
name|dbs
operator|.
name|optional
argument_list|(
literal|"url"
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|url
argument_list|)
condition|)
block|{
name|url
operator|=
name|dst
operator|.
name|getUrl
argument_list|()
expr_stmt|;
block|}
name|String
name|username
init|=
name|dbs
operator|.
name|optional
argument_list|(
literal|"username"
argument_list|)
decl_stmt|;
name|String
name|password
init|=
name|dbs
operator|.
name|optional
argument_list|(
literal|"password"
argument_list|)
decl_stmt|;
name|boolean
name|usePool
decl_stmt|;
if|if
condition|(
name|context
operator|==
name|Context
operator|.
name|SINGLE_USER
condition|)
block|{
name|usePool
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|usePool
operator|=
name|cfg
operator|.
name|getBoolean
argument_list|(
literal|"database"
argument_list|,
literal|"connectionpool"
argument_list|,
name|dst
operator|.
name|usePool
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|usePool
condition|)
block|{
specifier|final
name|BasicDataSource
name|ds
init|=
operator|new
name|BasicDataSource
argument_list|()
decl_stmt|;
name|ds
operator|.
name|setDriverClassName
argument_list|(
name|driver
argument_list|)
expr_stmt|;
name|ds
operator|.
name|setUrl
argument_list|(
name|url
argument_list|)
expr_stmt|;
if|if
condition|(
name|username
operator|!=
literal|null
operator|&&
operator|!
name|username
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ds
operator|.
name|setUsername
argument_list|(
name|username
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|password
operator|!=
literal|null
operator|&&
operator|!
name|password
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ds
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
block|}
name|ds
operator|.
name|setMaxActive
argument_list|(
name|cfg
operator|.
name|getInt
argument_list|(
literal|"database"
argument_list|,
literal|"poollimit"
argument_list|,
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|ds
operator|.
name|setMinIdle
argument_list|(
name|cfg
operator|.
name|getInt
argument_list|(
literal|"database"
argument_list|,
literal|"poolminidle"
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|ds
operator|.
name|setMaxIdle
argument_list|(
name|cfg
operator|.
name|getInt
argument_list|(
literal|"database"
argument_list|,
literal|"poolmaxidle"
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|ds
operator|.
name|setMaxWait
argument_list|(
name|ConfigUtil
operator|.
name|getTimeUnit
argument_list|(
name|cfg
argument_list|,
literal|"database"
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
name|ds
operator|.
name|setInitialSize
argument_list|(
name|ds
operator|.
name|getMinIdle
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ds
return|;
block|}
else|else
block|{
comment|// Don't use the connection pool.
comment|//
try|try
block|{
specifier|final
name|Properties
name|p
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"driver"
argument_list|,
name|driver
argument_list|)
expr_stmt|;
name|p
operator|.
name|setProperty
argument_list|(
literal|"url"
argument_list|,
name|url
argument_list|)
expr_stmt|;
if|if
condition|(
name|username
operator|!=
literal|null
condition|)
block|{
name|p
operator|.
name|setProperty
argument_list|(
literal|"user"
argument_list|,
name|username
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|password
operator|!=
literal|null
condition|)
block|{
name|p
operator|.
name|setProperty
argument_list|(
literal|"password"
argument_list|,
name|password
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SimpleDataSource
argument_list|(
name|p
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|se
parameter_list|)
block|{
throw|throw
operator|new
name|ProvisionException
argument_list|(
literal|"Database unavailable"
argument_list|,
name|se
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

