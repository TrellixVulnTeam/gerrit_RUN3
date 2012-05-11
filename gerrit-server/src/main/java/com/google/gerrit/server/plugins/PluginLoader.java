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
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|Sets
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
name|systemstatus
operator|.
name|ServerInformation
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
name|Singleton
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
name|FileFilter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|ReferenceQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLClassLoader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|Date
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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

begin_class
annotation|@
name|Singleton
DECL|class|PluginLoader
specifier|public
class|class
name|PluginLoader
implements|implements
name|LifecycleListener
block|{
DECL|field|log
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PluginLoader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|pluginsDir
specifier|private
specifier|final
name|File
name|pluginsDir
decl_stmt|;
DECL|field|dataDir
specifier|private
specifier|final
name|File
name|dataDir
decl_stmt|;
DECL|field|tmpDir
specifier|private
specifier|final
name|File
name|tmpDir
decl_stmt|;
DECL|field|env
specifier|private
specifier|final
name|PluginGuiceEnvironment
name|env
decl_stmt|;
DECL|field|srvInfoImpl
specifier|private
specifier|final
name|ServerInformationImpl
name|srvInfoImpl
decl_stmt|;
DECL|field|running
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|Plugin
argument_list|>
name|running
decl_stmt|;
DECL|field|broken
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FileSnapshot
argument_list|>
name|broken
decl_stmt|;
DECL|field|cleanupQueue
specifier|private
specifier|final
name|ReferenceQueue
argument_list|<
name|ClassLoader
argument_list|>
name|cleanupQueue
decl_stmt|;
DECL|field|cleanupHandles
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|CleanupHandle
argument_list|,
name|Boolean
argument_list|>
name|cleanupHandles
decl_stmt|;
DECL|field|scanner
specifier|private
specifier|final
name|PluginScannerThread
name|scanner
decl_stmt|;
annotation|@
name|Inject
DECL|method|PluginLoader (SitePaths sitePaths, PluginGuiceEnvironment pe, ServerInformationImpl sii, @GerritServerConfig Config cfg)
specifier|public
name|PluginLoader
parameter_list|(
name|SitePaths
name|sitePaths
parameter_list|,
name|PluginGuiceEnvironment
name|pe
parameter_list|,
name|ServerInformationImpl
name|sii
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|)
block|{
name|pluginsDir
operator|=
name|sitePaths
operator|.
name|plugins_dir
expr_stmt|;
name|dataDir
operator|=
name|sitePaths
operator|.
name|data_dir
expr_stmt|;
name|tmpDir
operator|=
name|sitePaths
operator|.
name|tmp_dir
expr_stmt|;
name|env
operator|=
name|pe
expr_stmt|;
name|srvInfoImpl
operator|=
name|sii
expr_stmt|;
name|running
operator|=
name|Maps
operator|.
name|newConcurrentMap
argument_list|()
expr_stmt|;
name|broken
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
name|cleanupQueue
operator|=
operator|new
name|ReferenceQueue
argument_list|<
name|ClassLoader
argument_list|>
argument_list|()
expr_stmt|;
name|cleanupHandles
operator|=
name|Maps
operator|.
name|newConcurrentMap
argument_list|()
expr_stmt|;
name|long
name|checkFrequency
init|=
name|ConfigUtil
operator|.
name|getTimeUnit
argument_list|(
name|cfg
argument_list|,
literal|"plugins"
argument_list|,
literal|null
argument_list|,
literal|"checkFrequency"
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMillis
argument_list|(
literal|1
argument_list|)
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|checkFrequency
operator|>
literal|0
condition|)
block|{
name|scanner
operator|=
operator|new
name|PluginScannerThread
argument_list|(
name|this
argument_list|,
name|checkFrequency
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|scanner
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|getPlugins ()
specifier|public
name|Iterable
argument_list|<
name|Plugin
argument_list|>
name|getPlugins
parameter_list|()
block|{
return|return
name|running
operator|.
name|values
argument_list|()
return|;
block|}
DECL|method|installPluginFromStream (String name, InputStream in)
specifier|public
name|void
name|installPluginFromStream
parameter_list|(
name|String
name|name
parameter_list|,
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
throws|,
name|PluginInstallException
block|{
if|if
condition|(
operator|!
name|name
operator|.
name|endsWith
argument_list|(
literal|".jar"
argument_list|)
condition|)
block|{
name|name
operator|+=
literal|".jar"
expr_stmt|;
block|}
name|File
name|jar
init|=
operator|new
name|File
argument_list|(
name|pluginsDir
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|name
operator|=
name|nameOf
argument_list|(
name|jar
argument_list|)
expr_stmt|;
name|File
name|old
init|=
operator|new
name|File
argument_list|(
name|pluginsDir
argument_list|,
literal|".last_"
operator|+
name|name
operator|+
literal|".zip"
argument_list|)
decl_stmt|;
name|File
name|tmp
init|=
name|asTemp
argument_list|(
name|in
argument_list|,
literal|".next_"
operator|+
name|name
argument_list|,
literal|".zip"
argument_list|,
name|pluginsDir
argument_list|)
decl_stmt|;
name|boolean
name|clean
init|=
literal|false
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|Plugin
name|active
init|=
name|running
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|active
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Replacing plugin %s"
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|old
operator|.
name|delete
argument_list|()
expr_stmt|;
name|jar
operator|.
name|renameTo
argument_list|(
name|old
argument_list|)
expr_stmt|;
block|}
operator|new
name|File
argument_list|(
name|pluginsDir
argument_list|,
name|name
operator|+
literal|".jar.disabled"
argument_list|)
operator|.
name|delete
argument_list|()
expr_stmt|;
name|tmp
operator|.
name|renameTo
argument_list|(
name|jar
argument_list|)
expr_stmt|;
try|try
block|{
name|runPlugin
argument_list|(
name|name
argument_list|,
name|jar
argument_list|,
name|active
argument_list|)
expr_stmt|;
if|if
condition|(
name|active
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Installed plugin %s"
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|clean
operator|=
literal|true
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|PluginInstallException
name|e
parameter_list|)
block|{
name|jar
operator|.
name|delete
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
if|if
condition|(
name|clean
condition|)
block|{
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|processPendingCleanups
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|asTemp (InputStream in, String prefix, String suffix, File dir)
specifier|private
specifier|static
name|File
name|asTemp
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|String
name|prefix
parameter_list|,
name|String
name|suffix
parameter_list|,
name|File
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|tmp
init|=
name|File
operator|.
name|createTempFile
argument_list|(
name|prefix
argument_list|,
name|suffix
argument_list|,
name|dir
argument_list|)
decl_stmt|;
name|boolean
name|keep
init|=
literal|false
decl_stmt|;
try|try
block|{
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|tmp
argument_list|)
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|8192
index|]
decl_stmt|;
name|int
name|n
decl_stmt|;
while|while
condition|(
operator|(
name|n
operator|=
name|in
operator|.
name|read
argument_list|(
name|data
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
name|keep
operator|=
literal|true
expr_stmt|;
return|return
name|tmp
return|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|keep
condition|)
block|{
name|tmp
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|disablePlugins (Set<String> names)
specifier|public
name|void
name|disablePlugins
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
name|boolean
name|clean
init|=
literal|false
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|Plugin
name|active
init|=
name|running
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|active
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|log
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Disabling plugin %s"
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|File
name|off
init|=
operator|new
name|File
argument_list|(
name|pluginsDir
argument_list|,
name|active
operator|.
name|getName
argument_list|()
operator|+
literal|".jar.disabled"
argument_list|)
decl_stmt|;
name|active
operator|.
name|getSrcJar
argument_list|()
operator|.
name|renameTo
argument_list|(
name|off
argument_list|)
expr_stmt|;
name|active
operator|.
name|stop
argument_list|()
expr_stmt|;
name|running
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|clean
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|clean
condition|)
block|{
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|processPendingCleanups
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|()
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Loading plugins from "
operator|+
name|pluginsDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|srvInfoImpl
operator|.
name|state
operator|=
name|ServerInformation
operator|.
name|State
operator|.
name|STARTUP
expr_stmt|;
name|rescan
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|srvInfoImpl
operator|.
name|state
operator|=
name|ServerInformation
operator|.
name|State
operator|.
name|RUNNING
expr_stmt|;
if|if
condition|(
name|scanner
operator|!=
literal|null
condition|)
block|{
name|scanner
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
if|if
condition|(
name|scanner
operator|!=
literal|null
condition|)
block|{
name|scanner
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
name|srvInfoImpl
operator|.
name|state
operator|=
name|ServerInformation
operator|.
name|State
operator|.
name|SHUTDOWN
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|boolean
name|clean
init|=
operator|!
name|running
operator|.
name|isEmpty
argument_list|()
decl_stmt|;
for|for
control|(
name|Plugin
name|p
range|:
name|running
operator|.
name|values
argument_list|()
control|)
block|{
name|p
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|running
operator|.
name|clear
argument_list|()
expr_stmt|;
name|broken
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|clean
condition|)
block|{
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|processPendingCleanups
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|rescan (boolean forceCleanup)
specifier|public
name|void
name|rescan
parameter_list|(
name|boolean
name|forceCleanup
parameter_list|)
block|{
if|if
condition|(
name|rescanImp
argument_list|()
operator|||
name|forceCleanup
condition|)
block|{
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|processPendingCleanups
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|rescanImp ()
specifier|private
specifier|synchronized
name|boolean
name|rescanImp
parameter_list|()
block|{
name|List
argument_list|<
name|File
argument_list|>
name|jars
init|=
name|scanJarsInPluginsDirectory
argument_list|()
decl_stmt|;
name|boolean
name|clean
init|=
name|stopRemovedPlugins
argument_list|(
name|jars
argument_list|)
decl_stmt|;
for|for
control|(
name|File
name|jar
range|:
name|jars
control|)
block|{
name|String
name|name
init|=
name|nameOf
argument_list|(
name|jar
argument_list|)
decl_stmt|;
name|FileSnapshot
name|brokenTime
init|=
name|broken
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|brokenTime
operator|!=
literal|null
operator|&&
operator|!
name|brokenTime
operator|.
name|isModified
argument_list|(
name|jar
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|Plugin
name|active
init|=
name|running
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|active
operator|!=
literal|null
operator|&&
operator|!
name|active
operator|.
name|isModified
argument_list|(
name|jar
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|active
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Reloading plugin %s"
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|runPlugin
argument_list|(
name|name
argument_list|,
name|jar
argument_list|,
name|active
argument_list|)
expr_stmt|;
if|if
condition|(
name|active
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Loaded plugin %s"
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|clean
operator|=
literal|true
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|PluginInstallException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Cannot load plugin %s"
argument_list|,
name|name
argument_list|)
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|clean
return|;
block|}
DECL|method|runPlugin (String name, File jar, Plugin oldPlugin)
specifier|private
name|void
name|runPlugin
parameter_list|(
name|String
name|name
parameter_list|,
name|File
name|jar
parameter_list|,
name|Plugin
name|oldPlugin
parameter_list|)
throws|throws
name|PluginInstallException
block|{
name|FileSnapshot
name|snapshot
init|=
name|FileSnapshot
operator|.
name|save
argument_list|(
name|jar
argument_list|)
decl_stmt|;
try|try
block|{
name|Plugin
name|newPlugin
init|=
name|loadPlugin
argument_list|(
name|name
argument_list|,
name|jar
argument_list|,
name|snapshot
argument_list|)
decl_stmt|;
name|boolean
name|reload
init|=
name|oldPlugin
operator|!=
literal|null
operator|&&
name|oldPlugin
operator|.
name|canReload
argument_list|()
operator|&&
name|newPlugin
operator|.
name|canReload
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|reload
operator|&&
name|oldPlugin
operator|!=
literal|null
condition|)
block|{
name|oldPlugin
operator|.
name|stop
argument_list|()
expr_stmt|;
name|running
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|newPlugin
operator|.
name|start
argument_list|(
name|env
argument_list|)
expr_stmt|;
if|if
condition|(
name|reload
condition|)
block|{
name|env
operator|.
name|onReloadPlugin
argument_list|(
name|oldPlugin
argument_list|,
name|newPlugin
argument_list|)
expr_stmt|;
name|oldPlugin
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|env
operator|.
name|onStartPlugin
argument_list|(
name|newPlugin
argument_list|)
expr_stmt|;
block|}
name|running
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|newPlugin
argument_list|)
expr_stmt|;
name|broken
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|err
parameter_list|)
block|{
name|broken
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|snapshot
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|PluginInstallException
argument_list|(
name|err
argument_list|)
throw|;
block|}
block|}
DECL|method|stopRemovedPlugins (List<File> jars)
specifier|private
name|boolean
name|stopRemovedPlugins
parameter_list|(
name|List
argument_list|<
name|File
argument_list|>
name|jars
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|unload
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|running
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|File
name|jar
range|:
name|jars
control|)
block|{
name|unload
operator|.
name|remove
argument_list|(
name|nameOf
argument_list|(
name|jar
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|name
range|:
name|unload
control|)
block|{
name|log
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Unloading plugin %s"
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|running
operator|.
name|remove
argument_list|(
name|name
argument_list|)
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
return|return
operator|!
name|unload
operator|.
name|isEmpty
argument_list|()
return|;
block|}
DECL|method|processPendingCleanups ()
specifier|private
specifier|synchronized
name|void
name|processPendingCleanups
parameter_list|()
block|{
name|CleanupHandle
name|h
decl_stmt|;
while|while
condition|(
operator|(
name|h
operator|=
operator|(
name|CleanupHandle
operator|)
name|cleanupQueue
operator|.
name|poll
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|h
operator|.
name|cleanup
argument_list|()
expr_stmt|;
name|cleanupHandles
operator|.
name|remove
argument_list|(
name|h
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|nameOf (File jar)
specifier|private
specifier|static
name|String
name|nameOf
parameter_list|(
name|File
name|jar
parameter_list|)
block|{
name|String
name|name
init|=
name|jar
operator|.
name|getName
argument_list|()
decl_stmt|;
name|int
name|ext
init|=
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
return|return
literal|0
operator|<
name|ext
condition|?
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|ext
argument_list|)
else|:
name|name
return|;
block|}
DECL|method|loadPlugin (String name, File srcJar, FileSnapshot snapshot)
specifier|private
name|Plugin
name|loadPlugin
parameter_list|(
name|String
name|name
parameter_list|,
name|File
name|srcJar
parameter_list|,
name|FileSnapshot
name|snapshot
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|File
name|tmp
decl_stmt|;
name|FileInputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|srcJar
argument_list|)
decl_stmt|;
try|try
block|{
name|tmp
operator|=
name|asTemp
argument_list|(
name|in
argument_list|,
name|tempNameFor
argument_list|(
name|name
argument_list|)
argument_list|,
literal|".jar"
argument_list|,
name|tmpDir
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|JarFile
name|jarFile
init|=
operator|new
name|JarFile
argument_list|(
name|tmp
argument_list|)
decl_stmt|;
name|boolean
name|keep
init|=
literal|false
decl_stmt|;
try|try
block|{
name|Manifest
name|manifest
init|=
name|jarFile
operator|.
name|getManifest
argument_list|()
decl_stmt|;
name|Attributes
name|main
init|=
name|manifest
operator|.
name|getMainAttributes
argument_list|()
decl_stmt|;
name|String
name|sysName
init|=
name|main
operator|.
name|getValue
argument_list|(
literal|"Gerrit-Module"
argument_list|)
decl_stmt|;
name|String
name|sshName
init|=
name|main
operator|.
name|getValue
argument_list|(
literal|"Gerrit-SshModule"
argument_list|)
decl_stmt|;
name|String
name|httpName
init|=
name|main
operator|.
name|getValue
argument_list|(
literal|"Gerrit-HttpModule"
argument_list|)
decl_stmt|;
name|URL
index|[]
name|urls
init|=
block|{
name|tmp
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
block|}
decl_stmt|;
name|ClassLoader
name|parentLoader
init|=
name|PluginLoader
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
decl_stmt|;
name|ClassLoader
name|pluginLoader
init|=
operator|new
name|URLClassLoader
argument_list|(
name|urls
argument_list|,
name|parentLoader
argument_list|)
decl_stmt|;
name|cleanupHandles
operator|.
name|put
argument_list|(
operator|new
name|CleanupHandle
argument_list|(
name|tmp
argument_list|,
name|jarFile
argument_list|,
name|pluginLoader
argument_list|,
name|cleanupQueue
argument_list|)
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|sysModule
init|=
name|load
argument_list|(
name|sysName
argument_list|,
name|pluginLoader
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|sshModule
init|=
name|load
argument_list|(
name|sshName
argument_list|,
name|pluginLoader
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|httpModule
init|=
name|load
argument_list|(
name|httpName
argument_list|,
name|pluginLoader
argument_list|)
decl_stmt|;
name|keep
operator|=
literal|true
expr_stmt|;
return|return
operator|new
name|Plugin
argument_list|(
name|name
argument_list|,
name|srcJar
argument_list|,
name|snapshot
argument_list|,
name|jarFile
argument_list|,
name|manifest
argument_list|,
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
name|name
argument_list|)
argument_list|,
name|pluginLoader
argument_list|,
name|sysModule
argument_list|,
name|sshModule
argument_list|,
name|httpModule
argument_list|)
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|keep
condition|)
block|{
name|jarFile
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|tempNameFor (String name)
specifier|private
specifier|static
name|String
name|tempNameFor
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|SimpleDateFormat
name|fmt
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyMMdd_HHmm"
argument_list|)
decl_stmt|;
return|return
literal|"plugin_"
operator|+
name|name
operator|+
literal|"_"
operator|+
name|fmt
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
operator|+
literal|"_"
return|;
block|}
DECL|method|load (String name, ClassLoader pluginLoader)
specifier|private
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|load
parameter_list|(
name|String
name|name
parameter_list|,
name|ClassLoader
name|pluginLoader
parameter_list|)
throws|throws
name|ClassNotFoundException
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|clazz
init|=
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|name
argument_list|,
literal|false
argument_list|,
name|pluginLoader
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Module
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ClassCastException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Class %s does not implement %s"
argument_list|,
name|name
argument_list|,
name|Module
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|clazz
return|;
block|}
DECL|method|scanJarsInPluginsDirectory ()
specifier|private
name|List
argument_list|<
name|File
argument_list|>
name|scanJarsInPluginsDirectory
parameter_list|()
block|{
if|if
condition|(
name|pluginsDir
operator|==
literal|null
operator|||
operator|!
name|pluginsDir
operator|.
name|exists
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
name|File
index|[]
name|matches
init|=
name|pluginsDir
operator|.
name|listFiles
argument_list|(
operator|new
name|FileFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|pathname
parameter_list|)
block|{
return|return
name|pathname
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".jar"
argument_list|)
operator|&&
name|pathname
operator|.
name|isFile
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|matches
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot list "
operator|+
name|pluginsDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|matches
argument_list|)
return|;
block|}
block|}
end_class

end_unit

