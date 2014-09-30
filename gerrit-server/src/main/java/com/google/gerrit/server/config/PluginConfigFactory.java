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
DECL|package|com.google.gerrit.server.config
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|config
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
name|gerrit
operator|.
name|reviewdb
operator|.
name|client
operator|.
name|Project
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
name|ProjectLevelConfig
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
name|Plugin
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
name|ReloadPluginListener
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
name|project
operator|.
name|NoSuchProjectException
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
name|project
operator|.
name|ProjectCache
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
name|project
operator|.
name|ProjectState
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
name|errors
operator|.
name|ConfigInvalidException
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
name|internal
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
name|FileBasedConfig
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
name|util
operator|.
name|FS
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
name|IOException
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

begin_class
annotation|@
name|Singleton
DECL|class|PluginConfigFactory
specifier|public
class|class
name|PluginConfigFactory
implements|implements
name|ReloadPluginListener
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
name|PluginConfigFactory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|EXTENSION
specifier|private
specifier|static
specifier|final
name|String
name|EXTENSION
init|=
literal|".config"
decl_stmt|;
DECL|field|site
specifier|private
specifier|final
name|SitePaths
name|site
decl_stmt|;
DECL|field|cfgProvider
specifier|private
specifier|final
name|GerritServerConfigProvider
name|cfgProvider
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|projectStateFactory
specifier|private
specifier|final
name|ProjectState
operator|.
name|Factory
name|projectStateFactory
decl_stmt|;
DECL|field|pluginConfigs
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Config
argument_list|>
name|pluginConfigs
decl_stmt|;
DECL|field|cfgSnapshot
specifier|private
specifier|volatile
name|FileSnapshot
name|cfgSnapshot
decl_stmt|;
DECL|field|cfg
specifier|private
specifier|volatile
name|Config
name|cfg
decl_stmt|;
annotation|@
name|Inject
DECL|method|PluginConfigFactory (SitePaths site, GerritServerConfigProvider cfgProvider, ProjectCache projectCache, ProjectState.Factory projectStateFactory)
name|PluginConfigFactory
parameter_list|(
name|SitePaths
name|site
parameter_list|,
name|GerritServerConfigProvider
name|cfgProvider
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
name|ProjectState
operator|.
name|Factory
name|projectStateFactory
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
name|cfgProvider
operator|=
name|cfgProvider
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|projectStateFactory
operator|=
name|projectStateFactory
expr_stmt|;
name|this
operator|.
name|pluginConfigs
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
name|this
operator|.
name|cfgSnapshot
operator|=
name|FileSnapshot
operator|.
name|save
argument_list|(
name|site
operator|.
name|gerrit_config
argument_list|)
expr_stmt|;
name|this
operator|.
name|cfg
operator|=
name|cfgProvider
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns the configuration for the specified plugin that is stored in the    * 'gerrit.config' file.    *    * The returned plugin configuration provides access to all parameters of the    * 'gerrit.config' file that are set in the 'plugin' subsection of the    * specified plugin.    *    * E.g.:    *   [plugin "my-plugin"]    *     myKey = myValue    *    * @param pluginName the name of the plugin for which the configuration should    *        be returned    * @return the plugin configuration from the 'gerrit.config' file    */
DECL|method|getFromGerritConfig (String pluginName)
specifier|public
name|PluginConfig
name|getFromGerritConfig
parameter_list|(
name|String
name|pluginName
parameter_list|)
block|{
return|return
name|getFromGerritConfig
argument_list|(
name|pluginName
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Returns the configuration for the specified plugin that is stored in the    * 'gerrit.config' file.    *    * The returned plugin configuration provides access to all parameters of the    * 'gerrit.config' file that are set in the 'plugin' subsection of the    * specified plugin.    *    * E.g.: [plugin "my-plugin"] myKey = myValue    *    * @param pluginName the name of the plugin for which the configuration should    *        be returned    * @param refresh if<code>true</code> it is checked if the 'gerrit.config'    *        file was modified and if yes the Gerrit configuration is reloaded,    *        if<code>false</code> the cached Gerrit configuration is used    * @return the plugin configuration from the 'gerrit.config' file    */
DECL|method|getFromGerritConfig (String pluginName, boolean refresh)
specifier|public
name|PluginConfig
name|getFromGerritConfig
parameter_list|(
name|String
name|pluginName
parameter_list|,
name|boolean
name|refresh
parameter_list|)
block|{
if|if
condition|(
name|refresh
operator|&&
name|cfgSnapshot
operator|.
name|isModified
argument_list|(
name|site
operator|.
name|gerrit_config
argument_list|)
condition|)
block|{
name|cfgSnapshot
operator|=
name|FileSnapshot
operator|.
name|save
argument_list|(
name|site
operator|.
name|gerrit_config
argument_list|)
expr_stmt|;
name|cfg
operator|=
name|cfgProvider
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|PluginConfig
argument_list|(
name|pluginName
argument_list|,
name|cfg
argument_list|)
return|;
block|}
comment|/**    * Returns the configuration for the specified plugin that is stored in the    * 'project.config' file of the specified project.    *    * The returned plugin configuration provides access to all parameters of the    * 'project.config' file that are set in the 'plugin' subsection of the    * specified plugin.    *    * E.g.:    *   [plugin "my-plugin"]    *     myKey = myValue    *    * @param projectName the name of the project for which the plugin    *        configuration should be returned    * @param pluginName the name of the plugin for which the configuration should    *        be returned    * @return the plugin configuration from the 'project.config' file of the    *         specified project    * @throws NoSuchProjectException thrown if the specified project does not    *         exist    */
DECL|method|getFromProjectConfig (Project.NameKey projectName, String pluginName)
specifier|public
name|PluginConfig
name|getFromProjectConfig
parameter_list|(
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|,
name|String
name|pluginName
parameter_list|)
throws|throws
name|NoSuchProjectException
block|{
name|ProjectState
name|projectState
init|=
name|projectCache
operator|.
name|get
argument_list|(
name|projectName
argument_list|)
decl_stmt|;
if|if
condition|(
name|projectState
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchProjectException
argument_list|(
name|projectName
argument_list|)
throw|;
block|}
return|return
name|getFromProjectConfig
argument_list|(
name|projectState
argument_list|,
name|pluginName
argument_list|)
return|;
block|}
comment|/**    * Returns the configuration for the specified plugin that is stored in the    * 'project.config' file of the specified project.    *    * The returned plugin configuration provides access to all parameters of the    * 'project.config' file that are set in the 'plugin' subsection of the    * specified plugin.    *    * E.g.: [plugin "my-plugin"] myKey = myValue    *    * @param projectState the project for which the plugin configuration should    *        be returned    * @param pluginName the name of the plugin for which the configuration should    *        be returned    * @return the plugin configuration from the 'project.config' file of the    *         specified project    */
DECL|method|getFromProjectConfig (ProjectState projectState, String pluginName)
specifier|public
name|PluginConfig
name|getFromProjectConfig
parameter_list|(
name|ProjectState
name|projectState
parameter_list|,
name|String
name|pluginName
parameter_list|)
block|{
return|return
name|projectState
operator|.
name|getConfig
argument_list|()
operator|.
name|getPluginConfig
argument_list|(
name|pluginName
argument_list|)
return|;
block|}
comment|/**    * Returns the configuration for the specified plugin that is stored in the    * 'project.config' file of the specified project. Parameters which are not    * set in the 'project.config' of this project are inherited from the parent    * project's 'project.config' files.    *    * The returned plugin configuration provides access to all parameters of the    * 'project.config' file that are set in the 'plugin' subsection of the    * specified plugin.    *    * E.g.:    * child project:    *   [plugin "my-plugin"]    *     myKey = childValue    *    * parent project:    *   [plugin "my-plugin"]    *     myKey = parentValue    *     anotherKey = someValue    *    * return:    *   [plugin "my-plugin"]    *     myKey = childValue    *     anotherKey = someValue    *    * @param projectName the name of the project for which the plugin    *        configuration should be returned    * @param pluginName the name of the plugin for which the configuration should    *        be returned    * @return the plugin configuration from the 'project.config' file of the    *         specified project with inherited non-set parameters from the    *         parent projects    * @throws NoSuchProjectException thrown if the specified project does not    *         exist    */
DECL|method|getFromProjectConfigWithInheritance ( Project.NameKey projectName, String pluginName)
specifier|public
name|PluginConfig
name|getFromProjectConfigWithInheritance
parameter_list|(
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|,
name|String
name|pluginName
parameter_list|)
throws|throws
name|NoSuchProjectException
block|{
return|return
name|getFromProjectConfig
argument_list|(
name|projectName
argument_list|,
name|pluginName
argument_list|)
operator|.
name|withInheritance
argument_list|(
name|projectStateFactory
argument_list|)
return|;
block|}
comment|/**    * Returns the configuration for the specified plugin that is stored in the    * 'project.config' file of the specified project. Parameters which are not    * set in the 'project.config' of this project are inherited from the parent    * project's 'project.config' files.    *    * The returned plugin configuration provides access to all parameters of the    * 'project.config' file that are set in the 'plugin' subsection of the    * specified plugin.    *    * E.g.: child project: [plugin "my-plugin"] myKey = childValue    *    * parent project: [plugin "my-plugin"] myKey = parentValue anotherKey =    * someValue    *    * return: [plugin "my-plugin"] myKey = childValue anotherKey = someValue    *    * @param projectState the project for which the plugin configuration should    *        be returned    * @param pluginName the name of the plugin for which the configuration should    *        be returned    * @return the plugin configuration from the 'project.config' file of the    *         specified project with inherited non-set parameters from the parent    *         projects    */
DECL|method|getFromProjectConfigWithInheritance ( ProjectState projectState, String pluginName)
specifier|public
name|PluginConfig
name|getFromProjectConfigWithInheritance
parameter_list|(
name|ProjectState
name|projectState
parameter_list|,
name|String
name|pluginName
parameter_list|)
block|{
return|return
name|getFromProjectConfig
argument_list|(
name|projectState
argument_list|,
name|pluginName
argument_list|)
operator|.
name|withInheritance
argument_list|(
name|projectStateFactory
argument_list|)
return|;
block|}
comment|/**    * Returns the configuration for the specified plugin that is stored in the    * plugin configuration file 'etc/<plugin-name>.config'.    *    * The plugin configuration is only loaded once and is then cached.    *    * @param pluginName the name of the plugin for which the configuration should    *        be returned    * @return the plugin configuration from the 'etc/<plugin-name>.config' file    */
DECL|method|getGlobalPluginConfig (String pluginName)
specifier|public
specifier|synchronized
name|Config
name|getGlobalPluginConfig
parameter_list|(
name|String
name|pluginName
parameter_list|)
block|{
if|if
condition|(
name|pluginConfigs
operator|.
name|containsKey
argument_list|(
name|pluginName
argument_list|)
condition|)
block|{
return|return
name|pluginConfigs
operator|.
name|get
argument_list|(
name|pluginName
argument_list|)
return|;
block|}
name|File
name|pluginConfigFile
init|=
operator|new
name|File
argument_list|(
name|site
operator|.
name|etc_dir
argument_list|,
name|pluginName
operator|+
literal|".config"
argument_list|)
decl_stmt|;
name|FileBasedConfig
name|cfg
init|=
operator|new
name|FileBasedConfig
argument_list|(
name|pluginConfigFile
argument_list|,
name|FS
operator|.
name|DETECTED
argument_list|)
decl_stmt|;
name|pluginConfigs
operator|.
name|put
argument_list|(
name|pluginName
argument_list|,
name|cfg
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|cfg
operator|.
name|getFile
argument_list|()
operator|.
name|exists
argument_list|()
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"No "
operator|+
name|pluginConfigFile
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"; assuming defaults"
argument_list|)
expr_stmt|;
return|return
name|cfg
return|;
block|}
try|try
block|{
name|cfg
operator|.
name|load
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to load "
operator|+
name|pluginConfigFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConfigInvalidException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failed to load "
operator|+
name|pluginConfigFile
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|cfg
return|;
block|}
comment|/**    * Returns the configuration for the specified plugin that is stored in the    * '<plugin-name>.config' file in the 'refs/meta/config' branch of the    * specified project.    *    * @param projectName the name of the project for which the plugin    *        configuration should be returned    * @param pluginName the name of the plugin for which the configuration should    *        be returned    * @return the plugin configuration from the '<plugin-name>.config' file of    *         the specified project    * @throws NoSuchProjectException thrown if the specified project does not    *         exist    */
DECL|method|getProjectPluginConfig (Project.NameKey projectName, String pluginName)
specifier|public
name|Config
name|getProjectPluginConfig
parameter_list|(
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|,
name|String
name|pluginName
parameter_list|)
throws|throws
name|NoSuchProjectException
block|{
return|return
name|getPluginConfig
argument_list|(
name|projectName
argument_list|,
name|pluginName
argument_list|)
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Returns the configuration for the specified plugin that is stored in the    * '<plugin-name>.config' file in the 'refs/meta/config' branch of the    * specified project.    *    * @param projectState the project for which the plugin configuration should    *        be returned    * @param pluginName the name of the plugin for which the configuration should    *        be returned    * @return the plugin configuration from the '<plugin-name>.config' file of    *         the specified project    */
DECL|method|getProjectPluginConfig (ProjectState projectState, String pluginName)
specifier|public
name|Config
name|getProjectPluginConfig
parameter_list|(
name|ProjectState
name|projectState
parameter_list|,
name|String
name|pluginName
parameter_list|)
block|{
return|return
name|projectState
operator|.
name|getConfig
argument_list|(
name|pluginName
operator|+
name|EXTENSION
argument_list|)
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Returns the configuration for the specified plugin that is stored in the    * '<plugin-name>.config' file in the 'refs/meta/config' branch of the    * specified project. Parameters which are not set in the    * '<plugin-name>.config' of this project are inherited from the parent    * project's '<plugin-name>.config' files.    *    * E.g.: child project: [mySection "mySubsection"] myKey = childValue    *    * parent project: [mySection "mySubsection"] myKey = parentValue anotherKey =    * someValue    *    * return: [mySection "mySubsection"] myKey = childValue anotherKey =    * someValue    *    * @param projectName the name of the project for which the plugin    *        configuration should be returned    * @param pluginName the name of the plugin for which the configuration should    *        be returned    * @return the plugin configuration from the '<plugin-name>.config' file of    *         the specified project with inheriting non-set parameters from the    *         parent projects    * @throws NoSuchProjectException thrown if the specified project does not    *         exist    */
DECL|method|getProjectPluginConfigWithInheritance (Project.NameKey projectName, String pluginName)
specifier|public
name|Config
name|getProjectPluginConfigWithInheritance
parameter_list|(
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|,
name|String
name|pluginName
parameter_list|)
throws|throws
name|NoSuchProjectException
block|{
return|return
name|getPluginConfig
argument_list|(
name|projectName
argument_list|,
name|pluginName
argument_list|)
operator|.
name|getWithInheritance
argument_list|()
return|;
block|}
comment|/**    * Returns the configuration for the specified plugin that is stored in the    * '<plugin-name>.config' file in the 'refs/meta/config' branch of the    * specified project. Parameters which are not set in the    * '<plugin-name>.config' of this project are inherited from the parent    * project's '<plugin-name>.config' files.    *    * E.g.: child project: [mySection "mySubsection"] myKey = childValue    *    * parent project: [mySection "mySubsection"] myKey = parentValue anotherKey =    * someValue    *    * return: [mySection "mySubsection"] myKey = childValue anotherKey =    * someValue    *    * @param projectState the project for which the plugin configuration should    *        be returned    * @param pluginName the name of the plugin for which the configuration should    *        be returned    * @return the plugin configuration from the '<plugin-name>.config' file of    *         the specified project with inheriting non-set parameters from the    *         parent projects    */
DECL|method|getProjectPluginConfigWithInheritance (ProjectState projectState, String pluginName)
specifier|public
name|Config
name|getProjectPluginConfigWithInheritance
parameter_list|(
name|ProjectState
name|projectState
parameter_list|,
name|String
name|pluginName
parameter_list|)
block|{
return|return
name|projectState
operator|.
name|getConfig
argument_list|(
name|pluginName
operator|+
name|EXTENSION
argument_list|)
operator|.
name|getWithInheritance
argument_list|()
return|;
block|}
DECL|method|getPluginConfig (Project.NameKey projectName, String pluginName)
specifier|private
name|ProjectLevelConfig
name|getPluginConfig
parameter_list|(
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|,
name|String
name|pluginName
parameter_list|)
throws|throws
name|NoSuchProjectException
block|{
name|ProjectState
name|projectState
init|=
name|projectCache
operator|.
name|get
argument_list|(
name|projectName
argument_list|)
decl_stmt|;
if|if
condition|(
name|projectState
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchProjectException
argument_list|(
name|projectName
argument_list|)
throw|;
block|}
return|return
name|projectState
operator|.
name|getConfig
argument_list|(
name|pluginName
operator|+
name|EXTENSION
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|onReloadPlugin (Plugin oldPlugin, Plugin newPlugin)
specifier|public
specifier|synchronized
name|void
name|onReloadPlugin
parameter_list|(
name|Plugin
name|oldPlugin
parameter_list|,
name|Plugin
name|newPlugin
parameter_list|)
block|{
name|pluginConfigs
operator|.
name|remove
argument_list|(
name|oldPlugin
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

