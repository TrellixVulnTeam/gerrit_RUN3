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
DECL|package|com.google.gerrit.server.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|project
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
name|Iterables
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
name|gerrit
operator|.
name|extensions
operator|.
name|common
operator|.
name|ActionInfo
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
name|DynamicMap
operator|.
name|Entry
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
name|RestView
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
name|webui
operator|.
name|UiAction
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
name|Project
operator|.
name|InheritableBoolean
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
operator|.
name|SubmitType
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
name|AllProjectsNameProvider
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
name|PluginConfigFactory
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
name|extensions
operator|.
name|webui
operator|.
name|UiActions
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
name|TransferConfig
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
name|TreeMap
import|;
end_import

begin_class
DECL|class|ConfigInfo
specifier|public
class|class
name|ConfigInfo
block|{
DECL|field|kind
specifier|public
specifier|final
name|String
name|kind
init|=
literal|"gerritcodereview#project_config"
decl_stmt|;
DECL|field|description
specifier|public
name|String
name|description
decl_stmt|;
DECL|field|useContributorAgreements
specifier|public
name|InheritedBooleanInfo
name|useContributorAgreements
decl_stmt|;
DECL|field|useContentMerge
specifier|public
name|InheritedBooleanInfo
name|useContentMerge
decl_stmt|;
DECL|field|useSignedOffBy
specifier|public
name|InheritedBooleanInfo
name|useSignedOffBy
decl_stmt|;
DECL|field|requireChangeId
specifier|public
name|InheritedBooleanInfo
name|requireChangeId
decl_stmt|;
DECL|field|maxObjectSizeLimit
specifier|public
name|MaxObjectSizeLimitInfo
name|maxObjectSizeLimit
decl_stmt|;
DECL|field|submitType
specifier|public
name|SubmitType
name|submitType
decl_stmt|;
DECL|field|state
specifier|public
name|Project
operator|.
name|State
name|state
decl_stmt|;
DECL|field|pluginConfig
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ConfigParameterInfo
argument_list|>
argument_list|>
name|pluginConfig
decl_stmt|;
DECL|field|actions
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|ActionInfo
argument_list|>
name|actions
decl_stmt|;
DECL|field|commentlinks
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|CommentLinkInfo
argument_list|>
name|commentlinks
decl_stmt|;
DECL|field|theme
specifier|public
name|ThemeInfo
name|theme
decl_stmt|;
DECL|method|ConfigInfo (ProjectControl control, TransferConfig config, DynamicMap<ProjectConfigEntry> pluginConfigEntries, PluginConfigFactory cfgFactory, AllProjectsNameProvider allProjects, DynamicMap<RestView<ProjectResource>> views)
specifier|public
name|ConfigInfo
parameter_list|(
name|ProjectControl
name|control
parameter_list|,
name|TransferConfig
name|config
parameter_list|,
name|DynamicMap
argument_list|<
name|ProjectConfigEntry
argument_list|>
name|pluginConfigEntries
parameter_list|,
name|PluginConfigFactory
name|cfgFactory
parameter_list|,
name|AllProjectsNameProvider
name|allProjects
parameter_list|,
name|DynamicMap
argument_list|<
name|RestView
argument_list|<
name|ProjectResource
argument_list|>
argument_list|>
name|views
parameter_list|)
block|{
name|ProjectState
name|projectState
init|=
name|control
operator|.
name|getProjectState
argument_list|()
decl_stmt|;
name|Project
name|p
init|=
name|control
operator|.
name|getProject
argument_list|()
decl_stmt|;
name|this
operator|.
name|description
operator|=
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|p
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|InheritedBooleanInfo
name|useContributorAgreements
init|=
operator|new
name|InheritedBooleanInfo
argument_list|()
decl_stmt|;
name|InheritedBooleanInfo
name|useSignedOffBy
init|=
operator|new
name|InheritedBooleanInfo
argument_list|()
decl_stmt|;
name|InheritedBooleanInfo
name|useContentMerge
init|=
operator|new
name|InheritedBooleanInfo
argument_list|()
decl_stmt|;
name|InheritedBooleanInfo
name|requireChangeId
init|=
operator|new
name|InheritedBooleanInfo
argument_list|()
decl_stmt|;
name|useContributorAgreements
operator|.
name|value
operator|=
name|projectState
operator|.
name|isUseContributorAgreements
argument_list|()
expr_stmt|;
name|useSignedOffBy
operator|.
name|value
operator|=
name|projectState
operator|.
name|isUseSignedOffBy
argument_list|()
expr_stmt|;
name|useContentMerge
operator|.
name|value
operator|=
name|projectState
operator|.
name|isUseContentMerge
argument_list|()
expr_stmt|;
name|requireChangeId
operator|.
name|value
operator|=
name|projectState
operator|.
name|isRequireChangeID
argument_list|()
expr_stmt|;
name|useContributorAgreements
operator|.
name|configuredValue
operator|=
name|p
operator|.
name|getUseContributorAgreements
argument_list|()
expr_stmt|;
name|useSignedOffBy
operator|.
name|configuredValue
operator|=
name|p
operator|.
name|getUseSignedOffBy
argument_list|()
expr_stmt|;
name|useContentMerge
operator|.
name|configuredValue
operator|=
name|p
operator|.
name|getUseContentMerge
argument_list|()
expr_stmt|;
name|requireChangeId
operator|.
name|configuredValue
operator|=
name|p
operator|.
name|getRequireChangeID
argument_list|()
expr_stmt|;
name|ProjectState
name|parentState
init|=
name|Iterables
operator|.
name|getFirst
argument_list|(
name|projectState
operator|.
name|parents
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentState
operator|!=
literal|null
condition|)
block|{
name|useContributorAgreements
operator|.
name|inheritedValue
operator|=
name|parentState
operator|.
name|isUseContributorAgreements
argument_list|()
expr_stmt|;
name|useSignedOffBy
operator|.
name|inheritedValue
operator|=
name|parentState
operator|.
name|isUseSignedOffBy
argument_list|()
expr_stmt|;
name|useContentMerge
operator|.
name|inheritedValue
operator|=
name|parentState
operator|.
name|isUseContentMerge
argument_list|()
expr_stmt|;
name|requireChangeId
operator|.
name|inheritedValue
operator|=
name|parentState
operator|.
name|isRequireChangeID
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|useContributorAgreements
operator|=
name|useContributorAgreements
expr_stmt|;
name|this
operator|.
name|useSignedOffBy
operator|=
name|useSignedOffBy
expr_stmt|;
name|this
operator|.
name|useContentMerge
operator|=
name|useContentMerge
expr_stmt|;
name|this
operator|.
name|requireChangeId
operator|=
name|requireChangeId
expr_stmt|;
name|MaxObjectSizeLimitInfo
name|maxObjectSizeLimit
init|=
operator|new
name|MaxObjectSizeLimitInfo
argument_list|()
decl_stmt|;
name|maxObjectSizeLimit
operator|.
name|value
operator|=
name|config
operator|.
name|getEffectiveMaxObjectSizeLimit
argument_list|(
name|projectState
argument_list|)
operator|==
name|config
operator|.
name|getMaxObjectSizeLimit
argument_list|()
condition|?
name|config
operator|.
name|getFormattedMaxObjectSizeLimit
argument_list|()
else|:
name|p
operator|.
name|getMaxObjectSizeLimit
argument_list|()
expr_stmt|;
name|maxObjectSizeLimit
operator|.
name|configuredValue
operator|=
name|p
operator|.
name|getMaxObjectSizeLimit
argument_list|()
expr_stmt|;
name|maxObjectSizeLimit
operator|.
name|inheritedValue
operator|=
name|config
operator|.
name|getFormattedMaxObjectSizeLimit
argument_list|()
expr_stmt|;
name|this
operator|.
name|maxObjectSizeLimit
operator|=
name|maxObjectSizeLimit
expr_stmt|;
name|this
operator|.
name|submitType
operator|=
name|p
operator|.
name|getSubmitType
argument_list|()
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|p
operator|.
name|getState
argument_list|()
operator|!=
name|Project
operator|.
name|State
operator|.
name|ACTIVE
condition|?
name|p
operator|.
name|getState
argument_list|()
else|:
literal|null
expr_stmt|;
name|this
operator|.
name|commentlinks
operator|=
name|Maps
operator|.
name|newLinkedHashMap
argument_list|()
expr_stmt|;
for|for
control|(
name|CommentLinkInfo
name|cl
range|:
name|projectState
operator|.
name|getCommentLinks
argument_list|()
control|)
block|{
name|this
operator|.
name|commentlinks
operator|.
name|put
argument_list|(
name|cl
operator|.
name|name
argument_list|,
name|cl
argument_list|)
expr_stmt|;
block|}
name|pluginConfig
operator|=
name|getPluginConfig
argument_list|(
name|control
operator|.
name|getProjectState
argument_list|()
argument_list|,
name|pluginConfigEntries
argument_list|,
name|cfgFactory
argument_list|,
name|allProjects
argument_list|)
expr_stmt|;
name|actions
operator|=
name|Maps
operator|.
name|newTreeMap
argument_list|()
expr_stmt|;
for|for
control|(
name|UiAction
operator|.
name|Description
name|d
range|:
name|UiActions
operator|.
name|from
argument_list|(
name|views
argument_list|,
operator|new
name|ProjectResource
argument_list|(
name|control
argument_list|)
argument_list|,
name|Providers
operator|.
name|of
argument_list|(
name|control
operator|.
name|getCurrentUser
argument_list|()
argument_list|)
argument_list|)
control|)
block|{
name|actions
operator|.
name|put
argument_list|(
name|d
operator|.
name|getId
argument_list|()
argument_list|,
operator|new
name|ActionInfo
argument_list|(
name|d
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|theme
operator|=
name|projectState
operator|.
name|getTheme
argument_list|()
expr_stmt|;
block|}
DECL|method|getPluginConfig ( ProjectState project, DynamicMap<ProjectConfigEntry> pluginConfigEntries, PluginConfigFactory cfgFactory, AllProjectsNameProvider allProjects)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ConfigParameterInfo
argument_list|>
argument_list|>
name|getPluginConfig
parameter_list|(
name|ProjectState
name|project
parameter_list|,
name|DynamicMap
argument_list|<
name|ProjectConfigEntry
argument_list|>
name|pluginConfigEntries
parameter_list|,
name|PluginConfigFactory
name|cfgFactory
parameter_list|,
name|AllProjectsNameProvider
name|allProjects
parameter_list|)
block|{
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|ConfigParameterInfo
argument_list|>
argument_list|>
name|pluginConfig
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|ProjectConfigEntry
argument_list|>
name|e
range|:
name|pluginConfigEntries
control|)
block|{
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
name|PluginConfig
name|cfg
init|=
name|cfgFactory
operator|.
name|getFromProjectConfig
argument_list|(
name|project
argument_list|,
name|e
operator|.
name|getPluginName
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|configuredValue
init|=
name|cfg
operator|.
name|getString
argument_list|(
name|e
operator|.
name|getExportName
argument_list|()
argument_list|)
decl_stmt|;
name|ConfigParameterInfo
name|p
init|=
operator|new
name|ConfigParameterInfo
argument_list|()
decl_stmt|;
name|p
operator|.
name|displayName
operator|=
name|configEntry
operator|.
name|getDisplayName
argument_list|()
expr_stmt|;
name|p
operator|.
name|description
operator|=
name|configEntry
operator|.
name|getDescription
argument_list|()
expr_stmt|;
name|p
operator|.
name|warning
operator|=
name|configEntry
operator|.
name|getWarning
argument_list|(
name|project
argument_list|)
expr_stmt|;
name|p
operator|.
name|type
operator|=
name|configEntry
operator|.
name|getType
argument_list|()
expr_stmt|;
name|p
operator|.
name|permittedValues
operator|=
name|configEntry
operator|.
name|getPermittedValues
argument_list|()
expr_stmt|;
name|p
operator|.
name|editable
operator|=
name|configEntry
operator|.
name|isEditable
argument_list|(
name|project
argument_list|)
condition|?
literal|true
else|:
literal|null
expr_stmt|;
if|if
condition|(
name|configEntry
operator|.
name|isInheritable
argument_list|()
operator|&&
operator|!
name|allProjects
operator|.
name|get
argument_list|()
operator|.
name|equals
argument_list|(
name|project
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|)
condition|)
block|{
name|PluginConfig
name|cfgWithInheritance
init|=
name|cfgFactory
operator|.
name|getFromProjectConfigWithInheritance
argument_list|(
name|project
argument_list|,
name|e
operator|.
name|getPluginName
argument_list|()
argument_list|)
decl_stmt|;
name|p
operator|.
name|inheritable
operator|=
literal|true
expr_stmt|;
name|p
operator|.
name|value
operator|=
name|cfgWithInheritance
operator|.
name|getString
argument_list|(
name|e
operator|.
name|getExportName
argument_list|()
argument_list|,
name|configEntry
operator|.
name|getDefaultValue
argument_list|()
argument_list|)
expr_stmt|;
name|p
operator|.
name|configuredValue
operator|=
name|configuredValue
expr_stmt|;
name|p
operator|.
name|inheritedValue
operator|=
name|getInheritedValue
argument_list|(
name|project
argument_list|,
name|cfgFactory
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|p
operator|.
name|value
operator|=
name|configuredValue
operator|!=
literal|null
condition|?
name|configuredValue
else|:
name|configEntry
operator|.
name|getDefaultValue
argument_list|()
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|ConfigParameterInfo
argument_list|>
name|pc
init|=
name|pluginConfig
operator|.
name|get
argument_list|(
name|e
operator|.
name|getPluginName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|pc
operator|==
literal|null
condition|)
block|{
name|pc
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
expr_stmt|;
name|pluginConfig
operator|.
name|put
argument_list|(
name|e
operator|.
name|getPluginName
argument_list|()
argument_list|,
name|pc
argument_list|)
expr_stmt|;
block|}
name|pc
operator|.
name|put
argument_list|(
name|e
operator|.
name|getExportName
argument_list|()
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
return|return
operator|!
name|pluginConfig
operator|.
name|isEmpty
argument_list|()
condition|?
name|pluginConfig
else|:
literal|null
return|;
block|}
DECL|method|getInheritedValue (ProjectState project, PluginConfigFactory cfgFactory, Entry<ProjectConfigEntry> e)
specifier|private
name|String
name|getInheritedValue
parameter_list|(
name|ProjectState
name|project
parameter_list|,
name|PluginConfigFactory
name|cfgFactory
parameter_list|,
name|Entry
argument_list|<
name|ProjectConfigEntry
argument_list|>
name|e
parameter_list|)
block|{
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
name|ProjectState
name|parent
init|=
name|Iterables
operator|.
name|getFirst
argument_list|(
name|project
operator|.
name|parents
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|inheritedValue
init|=
name|configEntry
operator|.
name|getDefaultValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|PluginConfig
name|parentCfgWithInheritance
init|=
name|cfgFactory
operator|.
name|getFromProjectConfigWithInheritance
argument_list|(
name|parent
argument_list|,
name|e
operator|.
name|getPluginName
argument_list|()
argument_list|)
decl_stmt|;
name|inheritedValue
operator|=
name|parentCfgWithInheritance
operator|.
name|getString
argument_list|(
name|e
operator|.
name|getExportName
argument_list|()
argument_list|,
name|configEntry
operator|.
name|getDefaultValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|inheritedValue
return|;
block|}
DECL|class|InheritedBooleanInfo
specifier|public
specifier|static
class|class
name|InheritedBooleanInfo
block|{
DECL|field|value
specifier|public
name|Boolean
name|value
decl_stmt|;
DECL|field|configuredValue
specifier|public
name|InheritableBoolean
name|configuredValue
decl_stmt|;
DECL|field|inheritedValue
specifier|public
name|Boolean
name|inheritedValue
decl_stmt|;
block|}
DECL|class|MaxObjectSizeLimitInfo
specifier|public
specifier|static
class|class
name|MaxObjectSizeLimitInfo
block|{
DECL|field|value
specifier|public
name|String
name|value
decl_stmt|;
DECL|field|configuredValue
specifier|public
name|String
name|configuredValue
decl_stmt|;
DECL|field|inheritedValue
specifier|public
name|String
name|inheritedValue
decl_stmt|;
block|}
DECL|class|ConfigParameterInfo
specifier|public
specifier|static
class|class
name|ConfigParameterInfo
block|{
DECL|field|displayName
specifier|public
name|String
name|displayName
decl_stmt|;
DECL|field|description
specifier|public
name|String
name|description
decl_stmt|;
DECL|field|warning
specifier|public
name|String
name|warning
decl_stmt|;
DECL|field|type
specifier|public
name|ProjectConfigEntry
operator|.
name|Type
name|type
decl_stmt|;
DECL|field|value
specifier|public
name|String
name|value
decl_stmt|;
DECL|field|editable
specifier|public
name|Boolean
name|editable
decl_stmt|;
DECL|field|inheritable
specifier|public
name|Boolean
name|inheritable
decl_stmt|;
DECL|field|configuredValue
specifier|public
name|String
name|configuredValue
decl_stmt|;
DECL|field|inheritedValue
specifier|public
name|String
name|inheritedValue
decl_stmt|;
DECL|field|permittedValues
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|permittedValues
decl_stmt|;
block|}
block|}
end_class

end_unit

