begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2013 The Android Open Source Project
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
DECL|package|com.google.gerrit.pgm.init
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|pgm
operator|.
name|init
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
name|server
operator|.
name|schema
operator|.
name|DataSourceProvider
operator|.
name|Context
operator|.
name|SINGLE_USER
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
name|base
operator|.
name|MoreObjects
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
name|common
operator|.
name|Die
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
name|IoUtil
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
name|init
operator|.
name|api
operator|.
name|ConsoleUI
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
name|init
operator|.
name|api
operator|.
name|InitFlags
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
name|init
operator|.
name|api
operator|.
name|InstallPlugins
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
name|plugins
operator|.
name|JarScanner
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
name|SchemaUpdater
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
name|UpdateUI
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
name|securestore
operator|.
name|SecureStore
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
name|securestore
operator|.
name|SecureStoreClassName
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
name|securestore
operator|.
name|SecureStoreProvider
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
name|spi
operator|.
name|Message
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
name|util
operator|.
name|Providers
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
name|ArrayList
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
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_comment
comment|/** Initialize a new Gerrit installation. */
end_comment

begin_class
DECL|class|BaseInit
specifier|public
class|class
name|BaseInit
extends|extends
name|SiteProgram
block|{
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
name|BaseInit
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|standalone
specifier|private
specifier|final
name|boolean
name|standalone
decl_stmt|;
DECL|field|initDb
specifier|private
specifier|final
name|boolean
name|initDb
decl_stmt|;
DECL|field|pluginsDistribution
specifier|protected
specifier|final
name|PluginsDistribution
name|pluginsDistribution
decl_stmt|;
DECL|field|pluginsToInstall
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|pluginsToInstall
decl_stmt|;
DECL|field|sysInjector
specifier|private
name|Injector
name|sysInjector
decl_stmt|;
DECL|method|BaseInit (PluginsDistribution pluginsDistribution, List<String> pluginsToInstall)
specifier|protected
name|BaseInit
parameter_list|(
name|PluginsDistribution
name|pluginsDistribution
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|pluginsToInstall
parameter_list|)
block|{
name|this
operator|.
name|standalone
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|initDb
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|pluginsDistribution
operator|=
name|pluginsDistribution
expr_stmt|;
name|this
operator|.
name|pluginsToInstall
operator|=
name|pluginsToInstall
expr_stmt|;
block|}
DECL|method|BaseInit (File sitePath, boolean standalone, boolean initDb, PluginsDistribution pluginsDistribution, List<String> pluginsToInstall)
specifier|public
name|BaseInit
parameter_list|(
name|File
name|sitePath
parameter_list|,
name|boolean
name|standalone
parameter_list|,
name|boolean
name|initDb
parameter_list|,
name|PluginsDistribution
name|pluginsDistribution
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|pluginsToInstall
parameter_list|)
block|{
name|this
argument_list|(
name|sitePath
argument_list|,
literal|null
argument_list|,
name|standalone
argument_list|,
name|initDb
argument_list|,
name|pluginsDistribution
argument_list|,
name|pluginsToInstall
argument_list|)
expr_stmt|;
block|}
DECL|method|BaseInit (File sitePath, final Provider<DataSource> dsProvider, boolean standalone, boolean initDb, PluginsDistribution pluginsDistribution, List<String> pluginsToInstall)
specifier|public
name|BaseInit
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
parameter_list|,
name|boolean
name|standalone
parameter_list|,
name|boolean
name|initDb
parameter_list|,
name|PluginsDistribution
name|pluginsDistribution
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|pluginsToInstall
parameter_list|)
block|{
name|super
argument_list|(
name|sitePath
argument_list|,
name|dsProvider
argument_list|)
expr_stmt|;
name|this
operator|.
name|standalone
operator|=
name|standalone
expr_stmt|;
name|this
operator|.
name|initDb
operator|=
name|initDb
expr_stmt|;
name|this
operator|.
name|pluginsDistribution
operator|=
name|pluginsDistribution
expr_stmt|;
name|this
operator|.
name|pluginsToInstall
operator|=
name|pluginsToInstall
expr_stmt|;
block|}
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
specifier|final
name|SiteInit
name|init
init|=
name|createSiteInit
argument_list|()
decl_stmt|;
if|if
condition|(
name|beforeInit
argument_list|(
name|init
argument_list|)
condition|)
block|{
return|return
literal|0
return|;
block|}
name|init
operator|.
name|flags
operator|.
name|autoStart
operator|=
name|getAutoStart
argument_list|()
operator|&&
name|init
operator|.
name|site
operator|.
name|isNew
expr_stmt|;
name|init
operator|.
name|flags
operator|.
name|skipPlugins
operator|=
name|skipPlugins
argument_list|()
expr_stmt|;
specifier|final
name|SiteRun
name|run
decl_stmt|;
try|try
block|{
name|init
operator|.
name|initializer
operator|.
name|run
argument_list|()
expr_stmt|;
name|init
operator|.
name|flags
operator|.
name|deleteOnFailure
operator|=
literal|false
expr_stmt|;
name|run
operator|=
name|createSiteRun
argument_list|(
name|init
argument_list|)
expr_stmt|;
name|run
operator|.
name|upgradeSchema
argument_list|()
expr_stmt|;
name|init
operator|.
name|initializer
operator|.
name|postRun
argument_list|(
name|createSysInjector
argument_list|(
name|init
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|failure
parameter_list|)
block|{
if|if
condition|(
name|init
operator|.
name|flags
operator|.
name|deleteOnFailure
condition|)
block|{
name|recursiveDelete
argument_list|(
name|getSitePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
throw|throw
name|failure
throw|;
block|}
catch|catch
parameter_list|(
name|Error
name|failure
parameter_list|)
block|{
if|if
condition|(
name|init
operator|.
name|flags
operator|.
name|deleteOnFailure
condition|)
block|{
name|recursiveDelete
argument_list|(
name|getSitePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
throw|throw
name|failure
throw|;
block|}
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Initialized "
operator|+
name|getSitePath
argument_list|()
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|afterInit
argument_list|(
name|run
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
DECL|method|skipPlugins ()
specifier|protected
name|boolean
name|skipPlugins
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|getSecureStoreLib ()
specifier|protected
name|String
name|getSecureStoreLib
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Invoked before site init is called.    *    * @param init initializer instance.    * @throws Exception    */
DECL|method|beforeInit (SiteInit init)
specifier|protected
name|boolean
name|beforeInit
parameter_list|(
name|SiteInit
name|init
parameter_list|)
throws|throws
name|Exception
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Invoked after site init is called.    *    * @param run completed run instance.    * @throws Exception    */
DECL|method|afterInit (SiteRun run)
specifier|protected
name|void
name|afterInit
parameter_list|(
name|SiteRun
name|run
parameter_list|)
throws|throws
name|Exception
block|{   }
DECL|method|getInstallPlugins ()
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|getInstallPlugins
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|pluginsToInstall
operator|!=
literal|null
operator|&&
name|pluginsToInstall
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|pluginsDistribution
operator|.
name|listPluginNames
argument_list|()
decl_stmt|;
if|if
condition|(
name|pluginsToInstall
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|i
init|=
name|names
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|n
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|pluginsToInstall
operator|.
name|contains
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|names
return|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Couldn't find distribution archive location."
operator|+
literal|" No plugin will be installed"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|getAutoStart ()
specifier|protected
name|boolean
name|getAutoStart
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|class|SiteInit
specifier|public
specifier|static
class|class
name|SiteInit
block|{
DECL|field|site
specifier|public
specifier|final
name|SitePaths
name|site
decl_stmt|;
DECL|field|flags
specifier|final
name|InitFlags
name|flags
decl_stmt|;
DECL|field|ui
specifier|final
name|ConsoleUI
name|ui
decl_stmt|;
DECL|field|initializer
specifier|final
name|SitePathInitializer
name|initializer
decl_stmt|;
annotation|@
name|Inject
DECL|method|SiteInit (final SitePaths site, final InitFlags flags, final ConsoleUI ui, final SitePathInitializer initializer)
name|SiteInit
parameter_list|(
specifier|final
name|SitePaths
name|site
parameter_list|,
specifier|final
name|InitFlags
name|flags
parameter_list|,
specifier|final
name|ConsoleUI
name|ui
parameter_list|,
specifier|final
name|SitePathInitializer
name|initializer
parameter_list|)
block|{
name|this
operator|.
name|site
operator|=
name|site
expr_stmt|;
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
name|this
operator|.
name|ui
operator|=
name|ui
expr_stmt|;
name|this
operator|.
name|initializer
operator|=
name|initializer
expr_stmt|;
block|}
block|}
DECL|method|createSiteInit ()
specifier|private
name|SiteInit
name|createSiteInit
parameter_list|()
block|{
specifier|final
name|ConsoleUI
name|ui
init|=
name|getConsoleUI
argument_list|()
decl_stmt|;
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
name|m
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|SecureStoreInitData
name|secureStoreInitData
init|=
name|discoverSecureStoreClass
argument_list|()
decl_stmt|;
specifier|final
name|String
name|currentSecureStoreClassName
init|=
name|getConfiguredSecureStoreClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|secureStoreInitData
operator|!=
literal|null
operator|&&
name|currentSecureStoreClassName
operator|!=
literal|null
operator|&&
operator|!
name|currentSecureStoreClassName
operator|.
name|equals
argument_list|(
name|secureStoreInitData
operator|.
name|className
argument_list|)
condition|)
block|{
name|String
name|err
init|=
name|String
operator|.
name|format
argument_list|(
literal|"Different secure store was previously configured: %s."
argument_list|,
name|currentSecureStoreClassName
argument_list|)
decl_stmt|;
name|die
argument_list|(
name|err
argument_list|,
operator|new
name|RuntimeException
argument_list|(
literal|"secure store mismatch"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|m
operator|.
name|add
argument_list|(
operator|new
name|InitModule
argument_list|(
name|standalone
argument_list|,
name|initDb
argument_list|)
argument_list|)
expr_stmt|;
name|m
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
name|ConsoleUI
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|ui
argument_list|)
expr_stmt|;
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
name|List
argument_list|<
name|String
argument_list|>
name|plugins
init|=
name|MoreObjects
operator|.
name|firstNonNull
argument_list|(
name|getInstallPlugins
argument_list|()
argument_list|,
name|Lists
operator|.
expr|<
name|String
operator|>
name|newArrayList
argument_list|()
argument_list|)
decl_stmt|;
name|bind
argument_list|(
operator|new
name|TypeLiteral
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
block|{}
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|InstallPlugins
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|plugins
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|PluginsDistribution
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|pluginsDistribution
argument_list|)
expr_stmt|;
name|String
name|secureStoreClassName
decl_stmt|;
if|if
condition|(
name|secureStoreInitData
operator|!=
literal|null
condition|)
block|{
name|secureStoreClassName
operator|=
name|secureStoreInitData
operator|.
name|className
expr_stmt|;
block|}
else|else
block|{
name|secureStoreClassName
operator|=
name|currentSecureStoreClassName
expr_stmt|;
block|}
if|if
condition|(
name|secureStoreClassName
operator|!=
literal|null
condition|)
block|{
name|ui
operator|.
name|message
argument_list|(
literal|"Using secure store: %s\n"
argument_list|,
name|secureStoreClassName
argument_list|)
expr_stmt|;
block|}
name|bind
argument_list|(
name|SecureStoreInitData
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|Providers
operator|.
name|of
argument_list|(
name|secureStoreInitData
argument_list|)
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|String
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|SecureStoreClassName
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|Providers
operator|.
name|of
argument_list|(
name|secureStoreClassName
argument_list|)
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|SecureStore
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|SecureStoreProvider
operator|.
name|class
argument_list|)
operator|.
name|in
argument_list|(
name|SINGLETON
argument_list|)
expr_stmt|;
block|}
block|}
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
name|m
argument_list|)
operator|.
name|getInstance
argument_list|(
name|SiteInit
operator|.
name|class
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
name|Die
condition|)
block|{
throw|throw
operator|(
name|Die
operator|)
name|why
throw|;
block|}
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
name|ce
operator|.
name|getMessage
argument_list|()
argument_list|)
decl_stmt|;
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
literal|"\n"
argument_list|)
expr_stmt|;
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
literal|"\n  caused by "
argument_list|)
expr_stmt|;
block|}
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
literal|"InitInjector failed"
argument_list|,
name|ce
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|method|getConsoleUI ()
specifier|protected
name|ConsoleUI
name|getConsoleUI
parameter_list|()
block|{
return|return
name|ConsoleUI
operator|.
name|getInstance
argument_list|(
literal|false
argument_list|)
return|;
block|}
DECL|method|discoverSecureStoreClass ()
specifier|private
name|SecureStoreInitData
name|discoverSecureStoreClass
parameter_list|()
block|{
name|String
name|secureStore
init|=
name|getSecureStoreLib
argument_list|()
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|secureStore
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
block|{
name|File
name|secureStoreLib
init|=
operator|new
name|File
argument_list|(
name|secureStore
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|secureStoreLib
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InvalidSecureStoreException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"File %s doesn't exist"
argument_list|,
name|secureStore
argument_list|)
argument_list|)
throw|;
block|}
name|JarScanner
name|scanner
init|=
operator|new
name|JarScanner
argument_list|(
name|secureStoreLib
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|secureStores
init|=
name|scanner
operator|.
name|findImplementationsOf
argument_list|(
name|SecureStore
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|secureStores
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|InvalidSecureStoreException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Cannot find class implementing %s interface in %s"
argument_list|,
name|SecureStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|secureStore
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|secureStores
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|InvalidSecureStoreException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s has more that one implementation of %s interface"
argument_list|,
name|secureStore
argument_list|,
name|SecureStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|IoUtil
operator|.
name|loadJARs
argument_list|(
name|secureStoreLib
argument_list|)
expr_stmt|;
return|return
operator|new
name|SecureStoreInitData
argument_list|(
name|secureStoreLib
argument_list|,
name|secureStores
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InvalidSecureStoreException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s is not a valid jar"
argument_list|,
name|secureStore
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|class|SiteRun
specifier|public
specifier|static
class|class
name|SiteRun
block|{
DECL|field|ui
specifier|public
specifier|final
name|ConsoleUI
name|ui
decl_stmt|;
DECL|field|site
specifier|public
specifier|final
name|SitePaths
name|site
decl_stmt|;
DECL|field|flags
specifier|public
specifier|final
name|InitFlags
name|flags
decl_stmt|;
DECL|field|schemaUpdater
specifier|final
name|SchemaUpdater
name|schemaUpdater
decl_stmt|;
DECL|field|schema
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
decl_stmt|;
DECL|field|repositoryManager
specifier|final
name|GitRepositoryManager
name|repositoryManager
decl_stmt|;
annotation|@
name|Inject
DECL|method|SiteRun (final ConsoleUI ui, final SitePaths site, final InitFlags flags, final SchemaUpdater schemaUpdater, final SchemaFactory<ReviewDb> schema, final GitRepositoryManager repositoryManager)
name|SiteRun
parameter_list|(
specifier|final
name|ConsoleUI
name|ui
parameter_list|,
specifier|final
name|SitePaths
name|site
parameter_list|,
specifier|final
name|InitFlags
name|flags
parameter_list|,
specifier|final
name|SchemaUpdater
name|schemaUpdater
parameter_list|,
specifier|final
name|SchemaFactory
argument_list|<
name|ReviewDb
argument_list|>
name|schema
parameter_list|,
specifier|final
name|GitRepositoryManager
name|repositoryManager
parameter_list|)
block|{
name|this
operator|.
name|ui
operator|=
name|ui
expr_stmt|;
name|this
operator|.
name|site
operator|=
name|site
expr_stmt|;
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
name|this
operator|.
name|schemaUpdater
operator|=
name|schemaUpdater
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|repositoryManager
operator|=
name|repositoryManager
expr_stmt|;
block|}
DECL|method|upgradeSchema ()
name|void
name|upgradeSchema
parameter_list|()
throws|throws
name|OrmException
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|pruneList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|schemaUpdater
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
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
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
name|ui
operator|.
name|yesno
argument_list|(
name|def
argument_list|,
name|msg
argument_list|)
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
name|ui
operator|.
name|isBatch
argument_list|()
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
name|prune
parameter_list|)
block|{
for|for
control|(
name|String
name|p
range|:
name|prune
control|)
block|{
if|if
condition|(
operator|!
name|pruneList
operator|.
name|contains
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|pruneList
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|pruneList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|StringBuilder
name|msg
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|msg
operator|.
name|append
argument_list|(
literal|"Execute the following SQL to drop unused objects:\n"
argument_list|)
expr_stmt|;
name|msg
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|sql
range|:
name|pruneList
control|)
block|{
name|msg
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
expr_stmt|;
name|msg
operator|.
name|append
argument_list|(
name|sql
argument_list|)
expr_stmt|;
name|msg
operator|.
name|append
argument_list|(
literal|";\n"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ui
operator|.
name|isBatch
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ui
operator|.
name|yesno
argument_list|(
literal|true
argument_list|,
literal|"%s\nExecute now"
argument_list|,
name|msg
argument_list|)
condition|)
block|{
try|try
init|(
name|JdbcSchema
name|db
init|=
operator|(
name|JdbcSchema
operator|)
name|schema
operator|.
name|open
argument_list|()
init|;               JdbcExecutor e = new JdbcExecutor(db)
block|)
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
block|}
block|}
block|}
end_class

begin_function
DECL|method|createSiteRun (final SiteInit init)
specifier|private
name|SiteRun
name|createSiteRun
parameter_list|(
specifier|final
name|SiteInit
name|init
parameter_list|)
block|{
return|return
name|createSysInjector
argument_list|(
name|init
argument_list|)
operator|.
name|getInstance
argument_list|(
name|SiteRun
operator|.
name|class
argument_list|)
return|;
block|}
end_function

begin_function
DECL|method|createSysInjector (final SiteInit init)
specifier|private
name|Injector
name|createSysInjector
parameter_list|(
specifier|final
name|SiteInit
name|init
parameter_list|)
block|{
if|if
condition|(
name|sysInjector
operator|==
literal|null
condition|)
block|{
specifier|final
name|List
argument_list|<
name|Module
argument_list|>
name|modules
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|ConsoleUI
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|init
operator|.
name|ui
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|InitFlags
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|init
operator|.
name|flags
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|sysInjector
operator|=
name|createDbInjector
argument_list|(
name|SINGLE_USER
argument_list|)
operator|.
name|createChildInjector
argument_list|(
name|modules
argument_list|)
expr_stmt|;
block|}
return|return
name|sysInjector
return|;
block|}
end_function

begin_function
DECL|method|recursiveDelete (File path)
specifier|private
specifier|static
name|void
name|recursiveDelete
parameter_list|(
name|File
name|path
parameter_list|)
block|{
name|File
index|[]
name|entries
init|=
name|path
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|entries
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|File
name|e
range|:
name|entries
control|)
block|{
name|recursiveDelete
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|path
operator|.
name|delete
argument_list|()
operator|&&
name|path
operator|.
name|exists
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"warn: Cannot remove "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
block|}
end_function

unit|}
end_unit

