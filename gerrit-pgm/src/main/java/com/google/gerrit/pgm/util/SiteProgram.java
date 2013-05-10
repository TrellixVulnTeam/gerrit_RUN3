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
DECL|package|com.google.gerrit.pgm.util
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|util
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Scopes
operator|.
name|SINGLETON
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Stage
operator|.
name|PRODUCTION
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
name|Lists
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
name|GerritServerConfigModule
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
name|server
operator|.
name|schema
operator|.
name|DataSourceModule
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
name|DataSourceType
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
name|DatabaseModule
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
name|SchemaModule
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
name|Binding
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
name|CreationException
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
name|Module
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
name|TypeLiteral
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
name|name
operator|.
name|Named
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
name|name
operator|.
name|Names
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
name|spi
operator|.
name|Message
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
name|lang
operator|.
name|annotation
operator|.
name|Annotation
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
name|SQLException
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
name|javax
operator|.
name|sql
operator|.
name|DataSource
import|;
end_import

begin_class
DECL|class|SiteProgram
specifier|public
specifier|abstract
class|class
name|SiteProgram
extends|extends
name|AbstractProgram
block|{
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"--site-path"
argument_list|,
name|aliases
operator|=
block|{
literal|"-d"
block|}
argument_list|,
name|usage
operator|=
literal|"Local directory containing site data"
argument_list|)
DECL|field|sitePath
specifier|private
name|File
name|sitePath
init|=
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
DECL|field|dsProvider
specifier|protected
name|Provider
argument_list|<
name|DataSource
argument_list|>
name|dsProvider
decl_stmt|;
DECL|method|SiteProgram ()
specifier|protected
name|SiteProgram
parameter_list|()
block|{   }
DECL|method|SiteProgram (File sitePath, final Provider<DataSource> dsProvider)
specifier|protected
name|SiteProgram
parameter_list|(
name|File
name|sitePath
parameter_list|,
specifier|final
name|Provider
argument_list|<
name|DataSource
argument_list|>
name|dsProvider
parameter_list|)
block|{
name|this
operator|.
name|sitePath
operator|=
name|sitePath
expr_stmt|;
name|this
operator|.
name|dsProvider
operator|=
name|dsProvider
expr_stmt|;
block|}
comment|/** @return the site path specified on the command line. */
DECL|method|getSitePath ()
specifier|protected
name|File
name|getSitePath
parameter_list|()
block|{
name|File
name|path
init|=
name|sitePath
operator|.
name|getAbsoluteFile
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"."
operator|.
name|equals
argument_list|(
name|path
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|path
operator|=
name|path
operator|.
name|getParentFile
argument_list|()
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
comment|/** Ensures we are running inside of a valid site, otherwise throws a Die. */
DECL|method|mustHaveValidSite ()
specifier|protected
name|void
name|mustHaveValidSite
parameter_list|()
throws|throws
name|Die
block|{
if|if
condition|(
operator|!
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|getSitePath
argument_list|()
argument_list|,
literal|"etc"
argument_list|)
argument_list|,
literal|"gerrit.config"
argument_list|)
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
name|die
argument_list|(
literal|"not a Gerrit site: '"
operator|+
name|getSitePath
argument_list|()
operator|+
literal|"'\n"
operator|+
literal|"Perhaps you need to run init first?"
argument_list|)
throw|;
block|}
block|}
comment|/** @return provides database connectivity and site path. */
DECL|method|createDbInjector (final DataSourceProvider.Context context)
specifier|protected
name|Injector
name|createDbInjector
parameter_list|(
specifier|final
name|DataSourceProvider
operator|.
name|Context
name|context
parameter_list|)
block|{
specifier|final
name|File
name|sitePath
init|=
name|getSitePath
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Module
argument_list|>
name|modules
init|=
operator|new
name|ArrayList
argument_list|<
name|Module
argument_list|>
argument_list|()
decl_stmt|;
name|Module
name|sitePathModule
init|=
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
name|File
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|SitePath
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|sitePath
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|modules
operator|.
name|add
argument_list|(
name|sitePathModule
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|LifecycleModule
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
name|DataSourceProvider
operator|.
name|Context
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|dsProvider
operator|!=
literal|null
condition|)
block|{
name|bind
argument_list|(
name|Key
operator|.
name|get
argument_list|(
name|DataSource
operator|.
name|class
argument_list|,
name|Names
operator|.
name|named
argument_list|(
literal|"ReviewDb"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|toProvider
argument_list|(
name|dsProvider
argument_list|)
operator|.
name|in
argument_list|(
name|SINGLETON
argument_list|)
expr_stmt|;
if|if
condition|(
name|LifecycleListener
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|dsProvider
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
name|listener
argument_list|()
operator|.
name|toInstance
argument_list|(
operator|(
name|LifecycleListener
operator|)
name|dsProvider
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|bind
argument_list|(
name|Key
operator|.
name|get
argument_list|(
name|DataSource
operator|.
name|class
argument_list|,
name|Names
operator|.
name|named
argument_list|(
literal|"ReviewDb"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|toProvider
argument_list|(
name|SiteLibraryBasedDataSourceProvider
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|SINGLETON
argument_list|)
expr_stmt|;
name|listener
argument_list|()
operator|.
name|to
argument_list|(
name|SiteLibraryBasedDataSourceProvider
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|Module
name|configModule
init|=
operator|new
name|GerritServerConfigModule
argument_list|()
decl_stmt|;
name|modules
operator|.
name|add
argument_list|(
name|configModule
argument_list|)
expr_stmt|;
name|Injector
name|cfgInjector
init|=
name|Guice
operator|.
name|createInjector
argument_list|(
name|sitePathModule
argument_list|,
name|configModule
argument_list|)
decl_stmt|;
name|Config
name|cfg
init|=
name|cfgInjector
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
name|dbType
decl_stmt|;
if|if
condition|(
name|dsProvider
operator|!=
literal|null
condition|)
block|{
name|dbType
operator|=
name|getDbType
argument_list|(
name|dsProvider
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dbType
operator|=
name|cfg
operator|.
name|getString
argument_list|(
literal|"database"
argument_list|,
literal|null
argument_list|,
literal|"type"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|DataSourceType
name|dst
init|=
name|Guice
operator|.
name|createInjector
argument_list|(
operator|new
name|DataSourceModule
argument_list|()
argument_list|,
name|configModule
argument_list|,
name|sitePathModule
argument_list|)
operator|.
name|getInstance
argument_list|(
name|Key
operator|.
name|get
argument_list|(
name|DataSourceType
operator|.
name|class
argument_list|,
name|Names
operator|.
name|named
argument_list|(
name|dbType
operator|.
name|toLowerCase
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|modules
operator|.
name|add
argument_list|(
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
name|DataSourceType
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|dst
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|DatabaseModule
argument_list|()
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|SchemaModule
argument_list|()
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|LocalDiskRepositoryManager
operator|.
name|Module
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|Guice
operator|.
name|createInjector
argument_list|(
name|PRODUCTION
argument_list|,
name|modules
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|CreationException
name|ce
parameter_list|)
block|{
specifier|final
name|Message
name|first
init|=
name|ce
operator|.
name|getErrorMessages
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|Throwable
name|why
init|=
name|first
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|why
operator|instanceof
name|SQLException
condition|)
block|{
throw|throw
name|die
argument_list|(
literal|"Cannot connect to SQL database"
argument_list|,
name|why
argument_list|)
throw|;
block|}
if|if
condition|(
name|why
operator|instanceof
name|OrmException
operator|&&
name|why
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
operator|&&
literal|"Unable to determine driver URL"
operator|.
name|equals
argument_list|(
name|why
operator|.
name|getMessage
argument_list|()
argument_list|)
condition|)
block|{
name|why
operator|=
name|why
operator|.
name|getCause
argument_list|()
expr_stmt|;
if|if
condition|(
name|isCannotCreatePoolException
argument_list|(
name|why
argument_list|)
condition|)
block|{
throw|throw
name|die
argument_list|(
literal|"Cannot connect to SQL database"
argument_list|,
name|why
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
throw|throw
name|die
argument_list|(
literal|"Cannot connect to SQL database"
argument_list|,
name|why
argument_list|)
throw|;
block|}
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|why
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|why
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|why
operator|=
name|why
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
name|first
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|why
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"\n  caused by "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|why
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|why
operator|=
name|why
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
throw|throw
name|die
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|,
operator|new
name|RuntimeException
argument_list|(
literal|"DbInjector failed"
argument_list|,
name|ce
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|method|getDbType (Provider<DataSource> dsProvider)
specifier|private
name|String
name|getDbType
parameter_list|(
name|Provider
argument_list|<
name|DataSource
argument_list|>
name|dsProvider
parameter_list|)
block|{
name|String
name|dbProductName
decl_stmt|;
try|try
block|{
name|Connection
name|conn
init|=
name|dsProvider
operator|.
name|get
argument_list|()
operator|.
name|getConnection
argument_list|()
decl_stmt|;
try|try
block|{
name|dbProductName
operator|=
name|conn
operator|.
name|getMetaData
argument_list|()
operator|.
name|getDatabaseProductName
argument_list|()
operator|.
name|toLowerCase
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|conn
operator|.
name|close
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
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|Module
argument_list|>
name|modules
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|modules
operator|.
name|add
argument_list|(
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
name|File
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|SitePath
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|sitePath
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|GerritServerConfigModule
argument_list|()
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|DataSourceModule
argument_list|()
argument_list|)
expr_stmt|;
name|Injector
name|i
init|=
name|Guice
operator|.
name|createInjector
argument_list|(
name|modules
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Binding
argument_list|<
name|DataSourceType
argument_list|>
argument_list|>
name|dsTypeBindings
init|=
name|i
operator|.
name|findBindingsByType
argument_list|(
operator|new
name|TypeLiteral
argument_list|<
name|DataSourceType
argument_list|>
argument_list|()
block|{}
argument_list|)
decl_stmt|;
for|for
control|(
name|Binding
argument_list|<
name|DataSourceType
argument_list|>
name|binding
range|:
name|dsTypeBindings
control|)
block|{
name|Annotation
name|annotation
init|=
name|binding
operator|.
name|getKey
argument_list|()
operator|.
name|getAnnotation
argument_list|()
decl_stmt|;
if|if
condition|(
name|annotation
operator|instanceof
name|Named
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|Named
operator|)
name|annotation
operator|)
operator|.
name|value
argument_list|()
operator|.
name|toLowerCase
argument_list|()
operator|.
name|contains
argument_list|(
name|dbProductName
argument_list|)
condition|)
block|{
return|return
operator|(
operator|(
name|Named
operator|)
name|annotation
operator|)
operator|.
name|value
argument_list|()
return|;
block|}
block|}
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Cannot guess database type from the database product name '%s'"
argument_list|,
name|dbProductName
argument_list|)
argument_list|)
throw|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|isCannotCreatePoolException (Throwable why)
specifier|private
specifier|static
name|boolean
name|isCannotCreatePoolException
parameter_list|(
name|Throwable
name|why
parameter_list|)
block|{
return|return
name|why
operator|instanceof
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|dbcp
operator|.
name|SQLNestedException
operator|&&
name|why
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
operator|&&
name|why
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Cannot create PoolableConnectionFactory"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

