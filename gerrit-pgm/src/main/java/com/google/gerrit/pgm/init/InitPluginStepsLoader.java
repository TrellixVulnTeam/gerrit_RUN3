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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Objects
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
name|pgm
operator|.
name|util
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
name|plugins
operator|.
name|PluginLoader
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
name|Singleton
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
name|IOException
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
name|Arrays
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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

begin_class
annotation|@
name|Singleton
DECL|class|InitPluginStepsLoader
specifier|public
class|class
name|InitPluginStepsLoader
block|{
DECL|field|pluginsDir
specifier|private
specifier|final
name|File
name|pluginsDir
decl_stmt|;
DECL|field|initInjector
specifier|private
specifier|final
name|Injector
name|initInjector
decl_stmt|;
DECL|field|ui
specifier|final
name|ConsoleUI
name|ui
decl_stmt|;
annotation|@
name|Inject
DECL|method|InitPluginStepsLoader (final ConsoleUI ui, final SitePaths sitePaths, final Injector initInjector)
specifier|public
name|InitPluginStepsLoader
parameter_list|(
specifier|final
name|ConsoleUI
name|ui
parameter_list|,
specifier|final
name|SitePaths
name|sitePaths
parameter_list|,
specifier|final
name|Injector
name|initInjector
parameter_list|)
block|{
name|this
operator|.
name|pluginsDir
operator|=
name|sitePaths
operator|.
name|plugins_dir
expr_stmt|;
name|this
operator|.
name|initInjector
operator|=
name|initInjector
expr_stmt|;
name|this
operator|.
name|ui
operator|=
name|ui
expr_stmt|;
block|}
DECL|method|getInitSteps ()
specifier|public
name|Collection
argument_list|<
name|InitStep
argument_list|>
name|getInitSteps
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
name|ArrayList
argument_list|<
name|InitStep
argument_list|>
name|pluginsInitSteps
init|=
operator|new
name|ArrayList
argument_list|<
name|InitStep
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|jar
range|:
name|jars
control|)
block|{
name|InitStep
name|init
init|=
name|loadInitStep
argument_list|(
name|jar
argument_list|)
decl_stmt|;
if|if
condition|(
name|init
operator|!=
literal|null
condition|)
block|{
name|pluginsInitSteps
operator|.
name|add
argument_list|(
name|init
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|pluginsInitSteps
return|;
block|}
DECL|method|loadInitStep (File jar)
specifier|private
name|InitStep
name|loadInitStep
parameter_list|(
name|File
name|jar
parameter_list|)
block|{
try|try
init|(
name|URLClassLoader
name|pluginLoader
init|=
operator|new
name|URLClassLoader
argument_list|(
operator|new
name|URL
index|[]
block|{
name|jar
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
block|}
argument_list|,
name|InitPluginStepsLoader
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
init|;
name|JarFile
name|jarFile
operator|=
operator|new
name|JarFile
argument_list|(
name|jar
argument_list|)
init|;
init|)
block|{
name|Attributes
name|jarFileAttributes
init|=
name|jarFile
operator|.
name|getManifest
argument_list|()
operator|.
name|getMainAttributes
argument_list|()
decl_stmt|;
name|String
name|initClassName
init|=
name|jarFileAttributes
operator|.
name|getValue
argument_list|(
literal|"Gerrit-InitStep"
argument_list|)
decl_stmt|;
if|if
condition|(
name|initClassName
operator|==
literal|null
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
name|InitStep
argument_list|>
name|initStepClass
init|=
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|InitStep
argument_list|>
operator|)
name|pluginLoader
operator|.
name|loadClass
argument_list|(
name|initClassName
argument_list|)
decl_stmt|;
return|return
name|getPluginInjector
argument_list|(
name|jar
argument_list|)
operator|.
name|getInstance
argument_list|(
name|initStepClass
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
name|ui
operator|.
name|message
argument_list|(
literal|"WARN: InitStep from plugin %s does not implement %s (Exception: %s)"
argument_list|,
name|jar
operator|.
name|getName
argument_list|()
argument_list|,
name|InitStep
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|ui
operator|.
name|message
argument_list|(
literal|"WARN: Cannot load and get plugin init step for %s (Exception: %s)"
argument_list|,
name|jar
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|getPluginInjector (File jarFile)
specifier|private
name|Injector
name|getPluginInjector
parameter_list|(
name|File
name|jarFile
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|pluginName
init|=
name|Objects
operator|.
name|firstNonNull
argument_list|(
name|PluginLoader
operator|.
name|getGerritPluginName
argument_list|(
name|jarFile
argument_list|)
argument_list|,
name|PluginLoader
operator|.
name|nameOf
argument_list|(
name|jarFile
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|initInjector
operator|.
name|createChildInjector
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
name|pluginName
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
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
name|String
name|n
init|=
name|pathname
operator|.
name|getName
argument_list|()
decl_stmt|;
return|return
operator|(
name|n
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
operator|)
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
name|ui
operator|.
name|message
argument_list|(
literal|"WARN: Cannot list %s"
argument_list|,
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
name|Arrays
operator|.
name|sort
argument_list|(
name|matches
argument_list|,
operator|new
name|Comparator
argument_list|<
name|File
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|File
name|o1
parameter_list|,
name|File
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
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

