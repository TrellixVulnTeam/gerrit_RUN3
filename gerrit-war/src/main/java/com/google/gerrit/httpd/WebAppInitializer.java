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
DECL|package|com.google.gerrit.httpd
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
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
name|gerrit
operator|.
name|lifecycle
operator|.
name|LifecycleManager
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
name|AuthConfigModule
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
name|CanonicalWebUrlModule
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
name|GerritGlobalModule
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
name|MasterNodeStartup
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
name|gerrit
operator|.
name|sshd
operator|.
name|SshModule
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
name|sshd
operator|.
name|commands
operator|.
name|MasterCommandModule
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
name|servlet
operator|.
name|GuiceServletContextListener
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
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletContextEvent
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
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
comment|/** Configures the web application environment for Gerrit Code Review. */
end_comment

begin_class
DECL|class|WebAppInitializer
specifier|public
class|class
name|WebAppInitializer
extends|extends
name|GuiceServletContextListener
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
name|WebAppInitializer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|sitePath
specifier|private
name|File
name|sitePath
decl_stmt|;
DECL|field|dbInjector
specifier|private
name|Injector
name|dbInjector
decl_stmt|;
DECL|field|cfgInjector
specifier|private
name|Injector
name|cfgInjector
decl_stmt|;
DECL|field|sysInjector
specifier|private
name|Injector
name|sysInjector
decl_stmt|;
DECL|field|webInjector
specifier|private
name|Injector
name|webInjector
decl_stmt|;
DECL|field|sshInjector
specifier|private
name|Injector
name|sshInjector
decl_stmt|;
DECL|field|manager
specifier|private
name|LifecycleManager
name|manager
decl_stmt|;
DECL|method|init ()
specifier|private
specifier|synchronized
name|void
name|init
parameter_list|()
block|{
if|if
condition|(
name|manager
operator|==
literal|null
condition|)
block|{
specifier|final
name|String
name|path
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"gerrit.site_path"
argument_list|)
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|sitePath
operator|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|dbInjector
operator|=
name|createDbInjector
argument_list|()
expr_stmt|;
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
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
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
name|Throwable
name|why
init|=
name|first
operator|.
name|getCause
argument_list|()
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
if|if
condition|(
name|first
operator|.
name|getCause
argument_list|()
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
literal|"\nResolve above errors before continuing."
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"\nComplete stack trace follows:"
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|error
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|,
name|first
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|CreationException
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|first
argument_list|)
argument_list|)
throw|;
block|}
name|cfgInjector
operator|=
name|createCfgInjector
argument_list|()
expr_stmt|;
name|sysInjector
operator|=
name|createSysInjector
argument_list|()
expr_stmt|;
name|sshInjector
operator|=
name|createSshInjector
argument_list|()
expr_stmt|;
name|webInjector
operator|=
name|createWebInjector
argument_list|()
expr_stmt|;
comment|// Push the Provider<HttpServletRequest> down into the canonical
comment|// URL provider. Its optional for that provider, but since we can
comment|// supply one we should do so, in case the administrator has not
comment|// setup the canonical URL in the configuration file.
comment|//
comment|// Note we have to do this manually as Guice failed to do the
comment|// injection here because the HTTP environment is not visible
comment|// to the core server modules.
comment|//
name|sysInjector
operator|.
name|getInstance
argument_list|(
name|HttpCanonicalWebUrlProvider
operator|.
name|class
argument_list|)
operator|.
name|setHttpServletRequest
argument_list|(
name|webInjector
operator|.
name|getProvider
argument_list|(
name|HttpServletRequest
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|manager
operator|=
operator|new
name|LifecycleManager
argument_list|()
expr_stmt|;
name|manager
operator|.
name|add
argument_list|(
name|dbInjector
argument_list|)
expr_stmt|;
name|manager
operator|.
name|add
argument_list|(
name|sysInjector
argument_list|)
expr_stmt|;
name|manager
operator|.
name|add
argument_list|(
name|sshInjector
argument_list|)
expr_stmt|;
name|manager
operator|.
name|add
argument_list|(
name|webInjector
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createDbInjector ()
specifier|private
name|Injector
name|createDbInjector
parameter_list|()
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
argument_list|<
name|Module
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|sitePath
operator|!=
literal|null
condition|)
block|{
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
name|DataSourceProvider
operator|.
name|Context
operator|.
name|MULTI_USER
argument_list|)
expr_stmt|;
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
name|DataSourceProvider
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
name|DataSourceProvider
operator|.
name|class
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
block|}
else|else
block|{
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
name|ReviewDbDataSourceProvider
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
name|ReviewDbDataSourceProvider
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|modules
operator|.
name|add
argument_list|(
operator|new
name|DatabaseModule
argument_list|()
argument_list|)
expr_stmt|;
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
DECL|method|createCfgInjector ()
specifier|private
name|Injector
name|createCfgInjector
parameter_list|()
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
argument_list|<
name|Module
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|sitePath
operator|==
literal|null
condition|)
block|{
comment|// If we didn't get the site path from the system property
comment|// we need to get it from the database, as that's our old
comment|// method of locating the site path on disk.
comment|//
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
name|toProvider
argument_list|(
name|SitePathFromSystemConfigProvider
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
name|modules
operator|.
name|add
argument_list|(
operator|new
name|GerritServerConfigModule
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|AuthConfigModule
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|dbInjector
operator|.
name|createChildInjector
argument_list|(
name|modules
argument_list|)
return|;
block|}
DECL|method|createSysInjector ()
specifier|private
name|Injector
name|createSysInjector
parameter_list|()
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
argument_list|<
name|Module
argument_list|>
argument_list|()
decl_stmt|;
name|modules
operator|.
name|add
argument_list|(
name|cfgInjector
operator|.
name|getInstance
argument_list|(
name|GerritGlobalModule
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|CanonicalWebUrlModule
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Class
argument_list|<
name|?
extends|extends
name|Provider
argument_list|<
name|String
argument_list|>
argument_list|>
name|provider
parameter_list|()
block|{
return|return
name|HttpCanonicalWebUrlProvider
operator|.
name|class
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|MasterNodeStartup
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|cfgInjector
operator|.
name|createChildInjector
argument_list|(
name|modules
argument_list|)
return|;
block|}
DECL|method|createSshInjector ()
specifier|private
name|Injector
name|createSshInjector
parameter_list|()
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
argument_list|<
name|Module
argument_list|>
argument_list|()
decl_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|SshModule
argument_list|()
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|MasterCommandModule
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sysInjector
operator|.
name|createChildInjector
argument_list|(
name|modules
argument_list|)
return|;
block|}
DECL|method|createWebInjector ()
specifier|private
name|Injector
name|createWebInjector
parameter_list|()
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
argument_list|<
name|Module
argument_list|>
argument_list|()
decl_stmt|;
name|modules
operator|.
name|add
argument_list|(
name|sshInjector
operator|.
name|getInstance
argument_list|(
name|WebModule
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sysInjector
operator|.
name|createChildInjector
argument_list|(
name|modules
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getInjector ()
specifier|protected
name|Injector
name|getInjector
parameter_list|()
block|{
name|init
argument_list|()
expr_stmt|;
return|return
name|webInjector
return|;
block|}
annotation|@
name|Override
DECL|method|contextInitialized (final ServletContextEvent event)
specifier|public
name|void
name|contextInitialized
parameter_list|(
specifier|final
name|ServletContextEvent
name|event
parameter_list|)
block|{
name|super
operator|.
name|contextInitialized
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
name|manager
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|contextDestroyed (final ServletContextEvent event)
specifier|public
name|void
name|contextDestroyed
parameter_list|(
specifier|final
name|ServletContextEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|manager
operator|!=
literal|null
condition|)
block|{
name|manager
operator|.
name|stop
argument_list|()
expr_stmt|;
name|manager
operator|=
literal|null
expr_stmt|;
block|}
name|super
operator|.
name|contextDestroyed
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

