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
DECL|package|com.google.gerrit.server.git.validators
package|package
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
name|validators
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
name|Joiner
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
name|ImmutableList
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
name|flogger
operator|.
name|FluentLogger
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
name|api
operator|.
name|projects
operator|.
name|ProjectConfigEntryType
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
name|DynamicMap
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
name|Extension
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
name|restapi
operator|.
name|AuthException
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
name|Branch
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
name|reviewdb
operator|.
name|client
operator|.
name|RefNames
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
name|IdentifiedUser
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
name|account
operator|.
name|AccountProperties
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
name|AllProjectsName
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
name|AllUsersName
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
name|PluginConfig
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
name|ProjectConfigEntry
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
name|CodeReviewCommit
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
name|permissions
operator|.
name|GlobalPermission
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
name|permissions
operator|.
name|PermissionBackend
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
name|permissions
operator|.
name|PermissionBackendException
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
name|permissions
operator|.
name|ProjectPermission
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
name|plugincontext
operator|.
name|PluginSetContext
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
name|ProjectConfig
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
name|gerrit
operator|.
name|server
operator|.
name|query
operator|.
name|change
operator|.
name|ChangeData
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
name|List
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
name|lib
operator|.
name|Repository
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
name|revwalk
operator|.
name|RevWalk
import|;
end_import

begin_class
DECL|class|MergeValidators
specifier|public
class|class
name|MergeValidators
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|FluentLogger
name|logger
init|=
name|FluentLogger
operator|.
name|forEnclosingClass
argument_list|()
decl_stmt|;
DECL|field|mergeValidationListeners
specifier|private
specifier|final
name|PluginSetContext
argument_list|<
name|MergeValidationListener
argument_list|>
name|mergeValidationListeners
decl_stmt|;
DECL|field|projectConfigValidatorFactory
specifier|private
specifier|final
name|ProjectConfigValidator
operator|.
name|Factory
name|projectConfigValidatorFactory
decl_stmt|;
DECL|field|accountValidatorFactory
specifier|private
specifier|final
name|AccountMergeValidator
operator|.
name|Factory
name|accountValidatorFactory
decl_stmt|;
DECL|field|groupValidatorFactory
specifier|private
specifier|final
name|GroupMergeValidator
operator|.
name|Factory
name|groupValidatorFactory
decl_stmt|;
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create ()
name|MergeValidators
name|create
parameter_list|()
function_decl|;
block|}
annotation|@
name|Inject
DECL|method|MergeValidators ( PluginSetContext<MergeValidationListener> mergeValidationListeners, ProjectConfigValidator.Factory projectConfigValidatorFactory, AccountMergeValidator.Factory accountValidatorFactory, GroupMergeValidator.Factory groupValidatorFactory)
name|MergeValidators
parameter_list|(
name|PluginSetContext
argument_list|<
name|MergeValidationListener
argument_list|>
name|mergeValidationListeners
parameter_list|,
name|ProjectConfigValidator
operator|.
name|Factory
name|projectConfigValidatorFactory
parameter_list|,
name|AccountMergeValidator
operator|.
name|Factory
name|accountValidatorFactory
parameter_list|,
name|GroupMergeValidator
operator|.
name|Factory
name|groupValidatorFactory
parameter_list|)
block|{
name|this
operator|.
name|mergeValidationListeners
operator|=
name|mergeValidationListeners
expr_stmt|;
name|this
operator|.
name|projectConfigValidatorFactory
operator|=
name|projectConfigValidatorFactory
expr_stmt|;
name|this
operator|.
name|accountValidatorFactory
operator|=
name|accountValidatorFactory
expr_stmt|;
name|this
operator|.
name|groupValidatorFactory
operator|=
name|groupValidatorFactory
expr_stmt|;
block|}
DECL|method|validatePreMerge ( Repository repo, CodeReviewCommit commit, ProjectState destProject, Branch.NameKey destBranch, PatchSet.Id patchSetId, IdentifiedUser caller)
specifier|public
name|void
name|validatePreMerge
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|CodeReviewCommit
name|commit
parameter_list|,
name|ProjectState
name|destProject
parameter_list|,
name|Branch
operator|.
name|NameKey
name|destBranch
parameter_list|,
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|,
name|IdentifiedUser
name|caller
parameter_list|)
throws|throws
name|MergeValidationException
block|{
name|List
argument_list|<
name|MergeValidationListener
argument_list|>
name|validators
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|PluginMergeValidationListener
argument_list|(
name|mergeValidationListeners
argument_list|)
argument_list|,
name|projectConfigValidatorFactory
operator|.
name|create
argument_list|()
argument_list|,
name|accountValidatorFactory
operator|.
name|create
argument_list|()
argument_list|,
name|groupValidatorFactory
operator|.
name|create
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|MergeValidationListener
name|validator
range|:
name|validators
control|)
block|{
name|validator
operator|.
name|onPreMerge
argument_list|(
name|repo
argument_list|,
name|commit
argument_list|,
name|destProject
argument_list|,
name|destBranch
argument_list|,
name|patchSetId
argument_list|,
name|caller
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ProjectConfigValidator
specifier|public
specifier|static
class|class
name|ProjectConfigValidator
implements|implements
name|MergeValidationListener
block|{
DECL|field|INVALID_CONFIG
specifier|private
specifier|static
specifier|final
name|String
name|INVALID_CONFIG
init|=
literal|"Change contains an invalid project configuration."
decl_stmt|;
DECL|field|PARENT_NOT_FOUND
specifier|private
specifier|static
specifier|final
name|String
name|PARENT_NOT_FOUND
init|=
literal|"Change contains an invalid project configuration:\nParent project does not exist."
decl_stmt|;
DECL|field|PLUGIN_VALUE_NOT_EDITABLE
specifier|private
specifier|static
specifier|final
name|String
name|PLUGIN_VALUE_NOT_EDITABLE
init|=
literal|"Change contains an invalid project configuration:\n"
operator|+
literal|"One of the plugin configuration parameters is not editable."
decl_stmt|;
DECL|field|PLUGIN_VALUE_NOT_PERMITTED
specifier|private
specifier|static
specifier|final
name|String
name|PLUGIN_VALUE_NOT_PERMITTED
init|=
literal|"Change contains an invalid project configuration:\n"
operator|+
literal|"One of the plugin configuration parameters has a value that is not"
operator|+
literal|" permitted."
decl_stmt|;
DECL|field|ROOT_NO_PARENT
specifier|private
specifier|static
specifier|final
name|String
name|ROOT_NO_PARENT
init|=
literal|"Change contains an invalid project configuration:\n"
operator|+
literal|"The root project cannot have a parent."
decl_stmt|;
DECL|field|SET_BY_ADMIN
specifier|private
specifier|static
specifier|final
name|String
name|SET_BY_ADMIN
init|=
literal|"Change contains a project configuration that changes the parent"
operator|+
literal|" project.\n"
operator|+
literal|"The change must be submitted by a Gerrit administrator."
decl_stmt|;
DECL|field|SET_BY_OWNER
specifier|private
specifier|static
specifier|final
name|String
name|SET_BY_OWNER
init|=
literal|"Change contains a project configuration that changes the parent"
operator|+
literal|" project.\n"
operator|+
literal|"The change must be submitted by a Gerrit administrator or the project owner."
decl_stmt|;
DECL|field|allProjectsName
specifier|private
specifier|final
name|AllProjectsName
name|allProjectsName
decl_stmt|;
DECL|field|allUsersName
specifier|private
specifier|final
name|AllUsersName
name|allUsersName
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|pluginConfigEntries
specifier|private
specifier|final
name|DynamicMap
argument_list|<
name|ProjectConfigEntry
argument_list|>
name|pluginConfigEntries
decl_stmt|;
DECL|field|projectConfigFactory
specifier|private
specifier|final
name|ProjectConfig
operator|.
name|Factory
name|projectConfigFactory
decl_stmt|;
DECL|field|allowProjectOwnersToChangeParent
specifier|private
specifier|final
name|boolean
name|allowProjectOwnersToChangeParent
decl_stmt|;
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create ()
name|ProjectConfigValidator
name|create
parameter_list|()
function_decl|;
block|}
annotation|@
name|Inject
DECL|method|ProjectConfigValidator ( AllProjectsName allProjectsName, AllUsersName allUsersName, ProjectCache projectCache, PermissionBackend permissionBackend, DynamicMap<ProjectConfigEntry> pluginConfigEntries, ProjectConfig.Factory projectConfigFactory, @GerritServerConfig Config config)
specifier|public
name|ProjectConfigValidator
parameter_list|(
name|AllProjectsName
name|allProjectsName
parameter_list|,
name|AllUsersName
name|allUsersName
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|DynamicMap
argument_list|<
name|ProjectConfigEntry
argument_list|>
name|pluginConfigEntries
parameter_list|,
name|ProjectConfig
operator|.
name|Factory
name|projectConfigFactory
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|config
parameter_list|)
block|{
name|this
operator|.
name|allProjectsName
operator|=
name|allProjectsName
expr_stmt|;
name|this
operator|.
name|allUsersName
operator|=
name|allUsersName
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
name|this
operator|.
name|pluginConfigEntries
operator|=
name|pluginConfigEntries
expr_stmt|;
name|this
operator|.
name|projectConfigFactory
operator|=
name|projectConfigFactory
expr_stmt|;
name|this
operator|.
name|allowProjectOwnersToChangeParent
operator|=
name|config
operator|.
name|getBoolean
argument_list|(
literal|"receive"
argument_list|,
literal|"allowProjectOwnersToChangeParent"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onPreMerge ( final Repository repo, final CodeReviewCommit commit, final ProjectState destProject, final Branch.NameKey destBranch, final PatchSet.Id patchSetId, IdentifiedUser caller)
specifier|public
name|void
name|onPreMerge
parameter_list|(
specifier|final
name|Repository
name|repo
parameter_list|,
specifier|final
name|CodeReviewCommit
name|commit
parameter_list|,
specifier|final
name|ProjectState
name|destProject
parameter_list|,
specifier|final
name|Branch
operator|.
name|NameKey
name|destBranch
parameter_list|,
specifier|final
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|,
name|IdentifiedUser
name|caller
parameter_list|)
throws|throws
name|MergeValidationException
block|{
if|if
condition|(
name|RefNames
operator|.
name|REFS_CONFIG
operator|.
name|equals
argument_list|(
name|destBranch
operator|.
name|get
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|Project
operator|.
name|NameKey
name|newParent
decl_stmt|;
try|try
block|{
name|ProjectConfig
name|cfg
init|=
name|projectConfigFactory
operator|.
name|create
argument_list|(
name|destProject
operator|.
name|getNameKey
argument_list|()
argument_list|)
decl_stmt|;
name|cfg
operator|.
name|load
argument_list|(
name|destProject
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|repo
argument_list|,
name|commit
argument_list|)
expr_stmt|;
name|newParent
operator|=
name|cfg
operator|.
name|getProject
argument_list|()
operator|.
name|getParent
argument_list|(
name|allProjectsName
argument_list|)
expr_stmt|;
specifier|final
name|Project
operator|.
name|NameKey
name|oldParent
init|=
name|destProject
operator|.
name|getProject
argument_list|()
operator|.
name|getParent
argument_list|(
name|allProjectsName
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldParent
operator|==
literal|null
condition|)
block|{
comment|// update of the 'All-Projects' project
if|if
condition|(
name|newParent
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|MergeValidationException
argument_list|(
name|ROOT_NO_PARENT
argument_list|)
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
operator|!
name|oldParent
operator|.
name|equals
argument_list|(
name|newParent
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|allowProjectOwnersToChangeParent
condition|)
block|{
try|try
block|{
name|permissionBackend
operator|.
name|user
argument_list|(
name|caller
argument_list|)
operator|.
name|check
argument_list|(
name|GlobalPermission
operator|.
name|ADMINISTRATE_SERVER
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MergeValidationException
argument_list|(
name|SET_BY_ADMIN
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionBackendException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atWarning
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Cannot check ADMINISTRATE_SERVER"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|MergeValidationException
argument_list|(
literal|"validation unavailable"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
try|try
block|{
name|permissionBackend
operator|.
name|user
argument_list|(
name|caller
argument_list|)
operator|.
name|project
argument_list|(
name|destProject
operator|.
name|getNameKey
argument_list|()
argument_list|)
operator|.
name|check
argument_list|(
name|ProjectPermission
operator|.
name|WRITE_CONFIG
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MergeValidationException
argument_list|(
name|SET_BY_OWNER
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionBackendException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atWarning
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Cannot check WRITE_CONFIG"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|MergeValidationException
argument_list|(
literal|"validation unavailable"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|allUsersName
operator|.
name|equals
argument_list|(
name|destProject
operator|.
name|getNameKey
argument_list|()
argument_list|)
operator|&&
operator|!
name|allProjectsName
operator|.
name|equals
argument_list|(
name|newParent
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|MergeValidationException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|" %s must inherit from %s"
argument_list|,
name|allUsersName
operator|.
name|get
argument_list|()
argument_list|,
name|allProjectsName
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|projectCache
operator|.
name|get
argument_list|(
name|newParent
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|MergeValidationException
argument_list|(
name|PARENT_NOT_FOUND
argument_list|)
throw|;
block|}
block|}
block|}
for|for
control|(
name|Extension
argument_list|<
name|ProjectConfigEntry
argument_list|>
name|e
range|:
name|pluginConfigEntries
control|)
block|{
name|PluginConfig
name|pluginCfg
init|=
name|cfg
operator|.
name|getPluginConfig
argument_list|(
name|e
operator|.
name|getPluginName
argument_list|()
argument_list|)
decl_stmt|;
name|ProjectConfigEntry
name|configEntry
init|=
name|e
operator|.
name|getProvider
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|pluginCfg
operator|.
name|getString
argument_list|(
name|e
operator|.
name|getExportName
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|oldValue
init|=
name|destProject
operator|.
name|getConfig
argument_list|()
operator|.
name|getPluginConfig
argument_list|(
name|e
operator|.
name|getPluginName
argument_list|()
argument_list|)
operator|.
name|getString
argument_list|(
name|e
operator|.
name|getExportName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|value
operator|==
literal|null
condition|?
name|oldValue
operator|!=
literal|null
else|:
operator|!
name|value
operator|.
name|equals
argument_list|(
name|oldValue
argument_list|)
operator|)
operator|&&
operator|!
name|configEntry
operator|.
name|isEditable
argument_list|(
name|destProject
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|MergeValidationException
argument_list|(
name|PLUGIN_VALUE_NOT_EDITABLE
argument_list|)
throw|;
block|}
if|if
condition|(
name|ProjectConfigEntryType
operator|.
name|LIST
operator|.
name|equals
argument_list|(
name|configEntry
operator|.
name|getType
argument_list|()
argument_list|)
operator|&&
name|value
operator|!=
literal|null
operator|&&
operator|!
name|configEntry
operator|.
name|getPermittedValues
argument_list|()
operator|.
name|contains
argument_list|(
name|value
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|MergeValidationException
argument_list|(
name|PLUGIN_VALUE_NOT_PERMITTED
argument_list|)
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|ConfigInvalidException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MergeValidationException
argument_list|(
name|INVALID_CONFIG
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|/** Execute merge validation plug-ins */
DECL|class|PluginMergeValidationListener
specifier|public
specifier|static
class|class
name|PluginMergeValidationListener
implements|implements
name|MergeValidationListener
block|{
DECL|field|mergeValidationListeners
specifier|private
specifier|final
name|PluginSetContext
argument_list|<
name|MergeValidationListener
argument_list|>
name|mergeValidationListeners
decl_stmt|;
DECL|method|PluginMergeValidationListener ( PluginSetContext<MergeValidationListener> mergeValidationListeners)
specifier|public
name|PluginMergeValidationListener
parameter_list|(
name|PluginSetContext
argument_list|<
name|MergeValidationListener
argument_list|>
name|mergeValidationListeners
parameter_list|)
block|{
name|this
operator|.
name|mergeValidationListeners
operator|=
name|mergeValidationListeners
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onPreMerge ( Repository repo, CodeReviewCommit commit, ProjectState destProject, Branch.NameKey destBranch, PatchSet.Id patchSetId, IdentifiedUser caller)
specifier|public
name|void
name|onPreMerge
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|CodeReviewCommit
name|commit
parameter_list|,
name|ProjectState
name|destProject
parameter_list|,
name|Branch
operator|.
name|NameKey
name|destBranch
parameter_list|,
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|,
name|IdentifiedUser
name|caller
parameter_list|)
throws|throws
name|MergeValidationException
block|{
name|mergeValidationListeners
operator|.
name|runEach
argument_list|(
name|l
lambda|->
name|l
operator|.
name|onPreMerge
argument_list|(
name|repo
argument_list|,
name|commit
argument_list|,
name|destProject
argument_list|,
name|destBranch
argument_list|,
name|patchSetId
argument_list|,
name|caller
argument_list|)
argument_list|,
name|MergeValidationException
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|AccountMergeValidator
specifier|public
specifier|static
class|class
name|AccountMergeValidator
implements|implements
name|MergeValidationListener
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create ()
name|AccountMergeValidator
name|create
parameter_list|()
function_decl|;
block|}
DECL|field|dbProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
decl_stmt|;
DECL|field|allUsersName
specifier|private
specifier|final
name|AllUsersName
name|allUsersName
decl_stmt|;
DECL|field|changeDataFactory
specifier|private
specifier|final
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
decl_stmt|;
DECL|field|accountValidator
specifier|private
specifier|final
name|AccountValidator
name|accountValidator
decl_stmt|;
annotation|@
name|Inject
DECL|method|AccountMergeValidator ( Provider<ReviewDb> dbProvider, AllUsersName allUsersName, ChangeData.Factory changeDataFactory, AccountValidator accountValidator)
specifier|public
name|AccountMergeValidator
parameter_list|(
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
parameter_list|,
name|AllUsersName
name|allUsersName
parameter_list|,
name|ChangeData
operator|.
name|Factory
name|changeDataFactory
parameter_list|,
name|AccountValidator
name|accountValidator
parameter_list|)
block|{
name|this
operator|.
name|dbProvider
operator|=
name|dbProvider
expr_stmt|;
name|this
operator|.
name|allUsersName
operator|=
name|allUsersName
expr_stmt|;
name|this
operator|.
name|changeDataFactory
operator|=
name|changeDataFactory
expr_stmt|;
name|this
operator|.
name|accountValidator
operator|=
name|accountValidator
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onPreMerge ( Repository repo, CodeReviewCommit commit, ProjectState destProject, Branch.NameKey destBranch, PatchSet.Id patchSetId, IdentifiedUser caller)
specifier|public
name|void
name|onPreMerge
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|CodeReviewCommit
name|commit
parameter_list|,
name|ProjectState
name|destProject
parameter_list|,
name|Branch
operator|.
name|NameKey
name|destBranch
parameter_list|,
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|,
name|IdentifiedUser
name|caller
parameter_list|)
throws|throws
name|MergeValidationException
block|{
name|Account
operator|.
name|Id
name|accountId
init|=
name|Account
operator|.
name|Id
operator|.
name|fromRef
argument_list|(
name|destBranch
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|allUsersName
operator|.
name|equals
argument_list|(
name|destProject
operator|.
name|getNameKey
argument_list|()
argument_list|)
operator|||
name|accountId
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|ChangeData
name|cd
init|=
name|changeDataFactory
operator|.
name|create
argument_list|(
name|dbProvider
operator|.
name|get
argument_list|()
argument_list|,
name|destProject
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|patchSetId
operator|.
name|getParentKey
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|cd
operator|.
name|currentFilePaths
argument_list|()
operator|.
name|contains
argument_list|(
name|AccountProperties
operator|.
name|ACCOUNT_CONFIG
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|OrmException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Cannot validate account update"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|MergeValidationException
argument_list|(
literal|"account validation unavailable"
argument_list|)
throw|;
block|}
try|try
init|(
name|RevWalk
name|rw
init|=
operator|new
name|RevWalk
argument_list|(
name|repo
argument_list|)
init|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|errorMessages
init|=
name|accountValidator
operator|.
name|validate
argument_list|(
name|accountId
argument_list|,
name|repo
argument_list|,
name|rw
argument_list|,
literal|null
argument_list|,
name|commit
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|errorMessages
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|MergeValidationException
argument_list|(
literal|"invalid account configuration: "
operator|+
name|Joiner
operator|.
name|on
argument_list|(
literal|"; "
argument_list|)
operator|.
name|join
argument_list|(
name|errorMessages
argument_list|)
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Cannot validate account update"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|MergeValidationException
argument_list|(
literal|"account validation unavailable"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|class|GroupMergeValidator
specifier|public
specifier|static
class|class
name|GroupMergeValidator
implements|implements
name|MergeValidationListener
block|{
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create ()
name|GroupMergeValidator
name|create
parameter_list|()
function_decl|;
block|}
DECL|field|allUsersName
specifier|private
specifier|final
name|AllUsersName
name|allUsersName
decl_stmt|;
annotation|@
name|Inject
DECL|method|GroupMergeValidator (AllUsersName allUsersName)
specifier|public
name|GroupMergeValidator
parameter_list|(
name|AllUsersName
name|allUsersName
parameter_list|)
block|{
name|this
operator|.
name|allUsersName
operator|=
name|allUsersName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onPreMerge ( Repository repo, CodeReviewCommit commit, ProjectState destProject, Branch.NameKey destBranch, PatchSet.Id patchSetId, IdentifiedUser caller)
specifier|public
name|void
name|onPreMerge
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|CodeReviewCommit
name|commit
parameter_list|,
name|ProjectState
name|destProject
parameter_list|,
name|Branch
operator|.
name|NameKey
name|destBranch
parameter_list|,
name|PatchSet
operator|.
name|Id
name|patchSetId
parameter_list|,
name|IdentifiedUser
name|caller
parameter_list|)
throws|throws
name|MergeValidationException
block|{
comment|// Groups are stored inside the 'All-Users' repository.
if|if
condition|(
operator|!
name|allUsersName
operator|.
name|equals
argument_list|(
name|destProject
operator|.
name|getNameKey
argument_list|()
argument_list|)
operator|||
operator|!
name|RefNames
operator|.
name|isGroupRef
argument_list|(
name|destBranch
operator|.
name|get
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
throw|throw
operator|new
name|MergeValidationException
argument_list|(
literal|"group update not allowed"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

