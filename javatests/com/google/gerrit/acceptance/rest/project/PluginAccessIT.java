begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2018 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.rest.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|rest
operator|.
name|project
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|truth
operator|.
name|Truth
operator|.
name|assertThat
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
name|ImmutableMap
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
name|acceptance
operator|.
name|AbstractDaemonTest
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
name|data
operator|.
name|AccessSection
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
name|Exports
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
name|access
operator|.
name|AccessSectionInfo
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
name|access
operator|.
name|PermissionInfo
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
name|access
operator|.
name|PermissionRuleInfo
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
name|access
operator|.
name|ProjectAccessInfo
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
name|access
operator|.
name|ProjectAccessInput
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
name|config
operator|.
name|CapabilityDefinition
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
name|config
operator|.
name|PluginProjectPermissionDefinition
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
name|group
operator|.
name|SystemGroupBackend
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
name|PluginPermissionsUtil
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
name|Module
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|PluginAccessIT
specifier|public
class|class
name|PluginAccessIT
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|TEST_PLUGIN_NAME
specifier|private
specifier|static
specifier|final
name|String
name|TEST_PLUGIN_NAME
init|=
literal|"gerrit"
decl_stmt|;
DECL|field|TEST_PLUGIN_CAPABILITY
specifier|private
specifier|static
specifier|final
name|String
name|TEST_PLUGIN_CAPABILITY
init|=
literal|"aPluginCapability"
decl_stmt|;
DECL|field|TEST_PLUGIN_PROJECT_PERMISSION
specifier|private
specifier|static
specifier|final
name|String
name|TEST_PLUGIN_PROJECT_PERMISSION
init|=
literal|"aPluginProjectPermission"
decl_stmt|;
DECL|field|pluginPermissionsUtil
annotation|@
name|Inject
name|PluginPermissionsUtil
name|pluginPermissionsUtil
decl_stmt|;
annotation|@
name|Override
DECL|method|createModule ()
specifier|public
name|Module
name|createModule
parameter_list|()
block|{
return|return
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
name|CapabilityDefinition
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|Exports
operator|.
name|named
argument_list|(
name|TEST_PLUGIN_CAPABILITY
argument_list|)
argument_list|)
operator|.
name|toInstance
argument_list|(
operator|new
name|CapabilityDefinition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"A Plugin Capability"
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|PluginProjectPermissionDefinition
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|Exports
operator|.
name|named
argument_list|(
name|TEST_PLUGIN_PROJECT_PERMISSION
argument_list|)
argument_list|)
operator|.
name|toInstance
argument_list|(
operator|new
name|PluginProjectPermissionDefinition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"A Plugin Project Permission"
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Test
DECL|method|addPluginCapability ()
specifier|public
name|void
name|addPluginCapability
parameter_list|()
throws|throws
name|Exception
block|{
name|addPluginPermission
argument_list|(
name|AccessSection
operator|.
name|GLOBAL_CAPABILITIES
argument_list|,
name|TEST_PLUGIN_CAPABILITY
argument_list|)
expr_stmt|;
comment|// Verifies the plugin defined capability could be listed.
name|assertThat
argument_list|(
name|pluginPermissionsUtil
operator|.
name|collectPluginCapabilities
argument_list|()
argument_list|)
operator|.
name|containsKey
argument_list|(
name|TEST_PLUGIN_NAME
operator|+
literal|"-"
operator|+
name|TEST_PLUGIN_CAPABILITY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|addPluginProjectPermission ()
specifier|public
name|void
name|addPluginProjectPermission
parameter_list|()
throws|throws
name|Exception
block|{
name|addPluginPermission
argument_list|(
literal|"refs/heads/plugin-permission"
argument_list|,
name|TEST_PLUGIN_PROJECT_PERMISSION
argument_list|)
expr_stmt|;
comment|// Verifies the plugin defined capability could be listed.
name|assertThat
argument_list|(
name|pluginPermissionsUtil
operator|.
name|collectPluginProjectPermissions
argument_list|()
argument_list|)
operator|.
name|containsKey
argument_list|(
literal|"plugin-"
operator|+
name|TEST_PLUGIN_NAME
operator|+
literal|"-"
operator|+
name|TEST_PLUGIN_PROJECT_PERMISSION
argument_list|)
expr_stmt|;
block|}
DECL|method|addPluginPermission (String accessSection, String permission)
specifier|private
name|void
name|addPluginPermission
parameter_list|(
name|String
name|accessSection
parameter_list|,
name|String
name|permission
parameter_list|)
throws|throws
name|Exception
block|{
name|ProjectAccessInput
name|accessInput
init|=
operator|new
name|ProjectAccessInput
argument_list|()
decl_stmt|;
name|PermissionRuleInfo
name|ruleInfo
init|=
operator|new
name|PermissionRuleInfo
argument_list|(
name|PermissionRuleInfo
operator|.
name|Action
operator|.
name|ALLOW
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|PermissionInfo
name|email
init|=
operator|new
name|PermissionInfo
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|email
operator|.
name|rules
operator|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|SystemGroupBackend
operator|.
name|REGISTERED_USERS
operator|.
name|get
argument_list|()
argument_list|,
name|ruleInfo
argument_list|)
expr_stmt|;
name|String
name|permissionConfigName
init|=
name|TEST_PLUGIN_NAME
operator|+
literal|"-"
operator|+
name|permission
decl_stmt|;
if|if
condition|(
operator|!
name|accessSection
operator|.
name|equals
argument_list|(
name|AccessSection
operator|.
name|GLOBAL_CAPABILITIES
argument_list|)
condition|)
block|{
name|permissionConfigName
operator|=
literal|"plugin-"
operator|+
name|permissionConfigName
expr_stmt|;
block|}
name|AccessSectionInfo
name|accessSectionInfo
init|=
operator|new
name|AccessSectionInfo
argument_list|()
decl_stmt|;
name|accessSectionInfo
operator|.
name|permissions
operator|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|permissionConfigName
argument_list|,
name|email
argument_list|)
expr_stmt|;
name|accessInput
operator|.
name|add
operator|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|accessSection
argument_list|,
name|accessSectionInfo
argument_list|)
expr_stmt|;
name|ProjectAccessInfo
name|updatedAccessSectionInfo
init|=
name|gApi
operator|.
name|projects
argument_list|()
operator|.
name|name
argument_list|(
name|allProjects
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|access
argument_list|(
name|accessInput
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|updatedAccessSectionInfo
operator|.
name|local
operator|.
name|get
argument_list|(
name|accessSection
argument_list|)
operator|.
name|permissions
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|containsAllIn
argument_list|(
name|accessSectionInfo
operator|.
name|permissions
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

