begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2012 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.plugins
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|plugins
package|;
end_package

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
name|annotations
operator|.
name|PluginName
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
name|RegistrationHandle
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
name|LifecycleManager
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
name|Module
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
name|FileSnapshot
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
name|jar
operator|.
name|Attributes
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|jar
operator|.
name|JarFile
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|jar
operator|.
name|Manifest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_class
DECL|class|Plugin
specifier|public
class|class
name|Plugin
block|{
static|static
block|{
comment|// Guice logs warnings about multiple injectors being created.
comment|// Silence this in case HTTP plugins are used.
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
operator|.
name|getLogger
argument_list|(
literal|"com.google.inject.servlet.GuiceFilter"
argument_list|)
operator|.
name|setLevel
argument_list|(
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Level
operator|.
name|OFF
argument_list|)
expr_stmt|;
block|}
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|srcJar
specifier|private
specifier|final
name|File
name|srcJar
decl_stmt|;
DECL|field|snapshot
specifier|private
specifier|final
name|FileSnapshot
name|snapshot
decl_stmt|;
DECL|field|jarFile
specifier|private
specifier|final
name|JarFile
name|jarFile
decl_stmt|;
DECL|field|manifest
specifier|private
specifier|final
name|Manifest
name|manifest
decl_stmt|;
DECL|field|classLoader
specifier|private
specifier|final
name|ClassLoader
name|classLoader
decl_stmt|;
DECL|field|sysModule
specifier|private
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|sysModule
decl_stmt|;
DECL|field|sshModule
specifier|private
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|sshModule
decl_stmt|;
DECL|field|httpModule
specifier|private
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|httpModule
decl_stmt|;
DECL|field|sysInjector
specifier|private
name|Injector
name|sysInjector
decl_stmt|;
DECL|field|sshInjector
specifier|private
name|Injector
name|sshInjector
decl_stmt|;
DECL|field|httpInjector
specifier|private
name|Injector
name|httpInjector
decl_stmt|;
DECL|field|manager
specifier|private
name|LifecycleManager
name|manager
decl_stmt|;
DECL|method|Plugin (String name, File srcJar, FileSnapshot snapshot, JarFile jarFile, Manifest manifest, ClassLoader classLoader, @Nullable Class<? extends Module> sysModule, @Nullable Class<? extends Module> sshModule, @Nullable Class<? extends Module> httpModule)
specifier|public
name|Plugin
parameter_list|(
name|String
name|name
parameter_list|,
name|File
name|srcJar
parameter_list|,
name|FileSnapshot
name|snapshot
parameter_list|,
name|JarFile
name|jarFile
parameter_list|,
name|Manifest
name|manifest
parameter_list|,
name|ClassLoader
name|classLoader
parameter_list|,
annotation|@
name|Nullable
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|sysModule
parameter_list|,
annotation|@
name|Nullable
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|sshModule
parameter_list|,
annotation|@
name|Nullable
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|httpModule
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|srcJar
operator|=
name|srcJar
expr_stmt|;
name|this
operator|.
name|snapshot
operator|=
name|snapshot
expr_stmt|;
name|this
operator|.
name|jarFile
operator|=
name|jarFile
expr_stmt|;
name|this
operator|.
name|manifest
operator|=
name|manifest
expr_stmt|;
name|this
operator|.
name|classLoader
operator|=
name|classLoader
expr_stmt|;
name|this
operator|.
name|sysModule
operator|=
name|sysModule
expr_stmt|;
name|this
operator|.
name|sshModule
operator|=
name|sshModule
expr_stmt|;
name|this
operator|.
name|httpModule
operator|=
name|httpModule
expr_stmt|;
block|}
DECL|method|getSrcJar ()
name|File
name|getSrcJar
parameter_list|()
block|{
return|return
name|srcJar
return|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getVersion ()
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
name|Attributes
name|main
init|=
name|manifest
operator|.
name|getMainAttributes
argument_list|()
decl_stmt|;
return|return
name|main
operator|.
name|getValue
argument_list|(
name|Attributes
operator|.
name|Name
operator|.
name|IMPLEMENTATION_VERSION
argument_list|)
return|;
block|}
DECL|method|canReload ()
name|boolean
name|canReload
parameter_list|()
block|{
name|Attributes
name|main
init|=
name|manifest
operator|.
name|getMainAttributes
argument_list|()
decl_stmt|;
name|String
name|v
init|=
name|main
operator|.
name|getValue
argument_list|(
literal|"Gerrit-ReloadMode"
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|v
argument_list|)
operator|||
literal|"reload"
operator|.
name|equalsIgnoreCase
argument_list|(
name|v
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
literal|"restart"
operator|.
name|equalsIgnoreCase
argument_list|(
name|v
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|PluginLoader
operator|.
name|log
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Plugin %s has invalid Gerrit-ReloadMode %s; assuming restart"
argument_list|,
name|name
argument_list|,
name|v
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
DECL|method|isModified (File jar)
name|boolean
name|isModified
parameter_list|(
name|File
name|jar
parameter_list|)
block|{
return|return
name|snapshot
operator|.
name|lastModified
argument_list|()
operator|!=
name|jar
operator|.
name|lastModified
argument_list|()
return|;
block|}
DECL|method|start (PluginGuiceEnvironment env)
specifier|public
name|void
name|start
parameter_list|(
name|PluginGuiceEnvironment
name|env
parameter_list|)
throws|throws
name|Exception
block|{
name|Injector
name|root
init|=
name|newRootInjector
argument_list|(
name|env
argument_list|)
decl_stmt|;
name|manager
operator|=
operator|new
name|LifecycleManager
argument_list|()
expr_stmt|;
name|AutoRegisterModules
name|auto
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|sysModule
operator|==
literal|null
operator|&&
name|sshModule
operator|==
literal|null
operator|&&
name|httpModule
operator|==
literal|null
condition|)
block|{
name|auto
operator|=
operator|new
name|AutoRegisterModules
argument_list|(
name|name
argument_list|,
name|env
argument_list|,
name|jarFile
argument_list|,
name|classLoader
argument_list|)
expr_stmt|;
name|auto
operator|.
name|discover
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|sysModule
operator|!=
literal|null
condition|)
block|{
name|sysInjector
operator|=
name|root
operator|.
name|createChildInjector
argument_list|(
name|root
operator|.
name|getInstance
argument_list|(
name|sysModule
argument_list|)
argument_list|)
expr_stmt|;
name|manager
operator|.
name|add
argument_list|(
name|sysInjector
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|auto
operator|!=
literal|null
operator|&&
name|auto
operator|.
name|sysModule
operator|!=
literal|null
condition|)
block|{
name|sysInjector
operator|=
name|root
operator|.
name|createChildInjector
argument_list|(
name|auto
operator|.
name|sysModule
argument_list|)
expr_stmt|;
name|manager
operator|.
name|add
argument_list|(
name|sysInjector
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sysInjector
operator|=
name|root
expr_stmt|;
block|}
if|if
condition|(
name|env
operator|.
name|hasSshModule
argument_list|()
condition|)
block|{
if|if
condition|(
name|sshModule
operator|!=
literal|null
condition|)
block|{
name|sshInjector
operator|=
name|sysInjector
operator|.
name|createChildInjector
argument_list|(
name|env
operator|.
name|getSshModule
argument_list|()
argument_list|,
name|sysInjector
operator|.
name|getInstance
argument_list|(
name|sshModule
argument_list|)
argument_list|)
expr_stmt|;
name|manager
operator|.
name|add
argument_list|(
name|sshInjector
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|auto
operator|!=
literal|null
operator|&&
name|auto
operator|.
name|sshModule
operator|!=
literal|null
condition|)
block|{
name|sshInjector
operator|=
name|sysInjector
operator|.
name|createChildInjector
argument_list|(
name|env
operator|.
name|getSshModule
argument_list|()
argument_list|,
name|auto
operator|.
name|sshModule
argument_list|)
expr_stmt|;
name|manager
operator|.
name|add
argument_list|(
name|sshInjector
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|env
operator|.
name|hasHttpModule
argument_list|()
condition|)
block|{
if|if
condition|(
name|httpModule
operator|!=
literal|null
condition|)
block|{
name|httpInjector
operator|=
name|sysInjector
operator|.
name|createChildInjector
argument_list|(
name|env
operator|.
name|getHttpModule
argument_list|()
argument_list|,
name|sysInjector
operator|.
name|getInstance
argument_list|(
name|httpModule
argument_list|)
argument_list|)
expr_stmt|;
name|manager
operator|.
name|add
argument_list|(
name|httpInjector
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|auto
operator|!=
literal|null
operator|&&
name|auto
operator|.
name|httpModule
operator|!=
literal|null
condition|)
block|{
name|httpInjector
operator|=
name|sysInjector
operator|.
name|createChildInjector
argument_list|(
name|env
operator|.
name|getHttpModule
argument_list|()
argument_list|,
name|auto
operator|.
name|httpModule
argument_list|)
expr_stmt|;
name|manager
operator|.
name|add
argument_list|(
name|httpInjector
argument_list|)
expr_stmt|;
block|}
block|}
name|manager
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|newRootInjector (PluginGuiceEnvironment env)
specifier|private
name|Injector
name|newRootInjector
parameter_list|(
name|PluginGuiceEnvironment
name|env
parameter_list|)
block|{
return|return
name|Guice
operator|.
name|createInjector
argument_list|(
name|env
operator|.
name|getSysModule
argument_list|()
argument_list|,
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
name|String
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|PluginName
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
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
name|sysInjector
operator|=
literal|null
expr_stmt|;
name|sshInjector
operator|=
literal|null
expr_stmt|;
name|httpInjector
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|getJarFile ()
specifier|public
name|JarFile
name|getJarFile
parameter_list|()
block|{
return|return
name|jarFile
return|;
block|}
annotation|@
name|Nullable
DECL|method|getSshInjector ()
specifier|public
name|Injector
name|getSshInjector
parameter_list|()
block|{
return|return
name|sshInjector
return|;
block|}
annotation|@
name|Nullable
DECL|method|getHttpInjector ()
specifier|public
name|Injector
name|getHttpInjector
parameter_list|()
block|{
return|return
name|httpInjector
return|;
block|}
DECL|method|add (final RegistrationHandle handle)
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|RegistrationHandle
name|handle
parameter_list|)
block|{
name|add
argument_list|(
operator|new
name|LifecycleListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
block|{       }
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|handle
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|add (LifecycleListener listener)
specifier|public
name|void
name|add
parameter_list|(
name|LifecycleListener
name|listener
parameter_list|)
block|{
name|manager
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Plugin ["
operator|+
name|name
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

