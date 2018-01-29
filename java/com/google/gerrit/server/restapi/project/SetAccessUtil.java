begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.restapi.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|restapi
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
name|common
operator|.
name|data
operator|.
name|GlobalCapability
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
name|GroupDescription
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
name|GroupReference
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
name|Permission
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
name|PermissionRule
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
name|errors
operator|.
name|InvalidNameException
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
name|extensions
operator|.
name|restapi
operator|.
name|BadRequestException
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
name|ResourceConflictException
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
name|UnprocessableEntityException
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
name|git
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
name|project
operator|.
name|RefPattern
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
name|restapi
operator|.
name|group
operator|.
name|GroupsCollection
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
DECL|class|SetAccessUtil
specifier|public
class|class
name|SetAccessUtil
block|{
DECL|field|groupsCollection
specifier|private
specifier|final
name|GroupsCollection
name|groupsCollection
decl_stmt|;
DECL|field|allProjects
specifier|private
specifier|final
name|AllProjectsName
name|allProjects
decl_stmt|;
DECL|field|setParent
specifier|private
specifier|final
name|Provider
argument_list|<
name|SetParent
argument_list|>
name|setParent
decl_stmt|;
annotation|@
name|Inject
DECL|method|SetAccessUtil ( GroupsCollection groupsCollection, AllProjectsName allProjects, Provider<SetParent> setParent)
specifier|private
name|SetAccessUtil
parameter_list|(
name|GroupsCollection
name|groupsCollection
parameter_list|,
name|AllProjectsName
name|allProjects
parameter_list|,
name|Provider
argument_list|<
name|SetParent
argument_list|>
name|setParent
parameter_list|)
block|{
name|this
operator|.
name|groupsCollection
operator|=
name|groupsCollection
expr_stmt|;
name|this
operator|.
name|allProjects
operator|=
name|allProjects
expr_stmt|;
name|this
operator|.
name|setParent
operator|=
name|setParent
expr_stmt|;
block|}
DECL|method|getAccessSections (Map<String, AccessSectionInfo> sectionInfos)
name|List
argument_list|<
name|AccessSection
argument_list|>
name|getAccessSections
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|AccessSectionInfo
argument_list|>
name|sectionInfos
parameter_list|)
throws|throws
name|UnprocessableEntityException
block|{
if|if
condition|(
name|sectionInfos
operator|==
literal|null
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
name|AccessSection
argument_list|>
name|sections
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|sectionInfos
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|AccessSectionInfo
argument_list|>
name|entry
range|:
name|sectionInfos
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|permissions
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|AccessSection
name|accessSection
init|=
operator|new
name|AccessSection
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|PermissionInfo
argument_list|>
name|permissionEntry
range|:
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|permissions
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|permissionEntry
operator|.
name|getValue
argument_list|()
operator|.
name|rules
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|Permission
name|p
init|=
operator|new
name|Permission
argument_list|(
name|permissionEntry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|permissionEntry
operator|.
name|getValue
argument_list|()
operator|.
name|exclusive
operator|!=
literal|null
condition|)
block|{
name|p
operator|.
name|setExclusiveGroup
argument_list|(
name|permissionEntry
operator|.
name|getValue
argument_list|()
operator|.
name|exclusive
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|PermissionRuleInfo
argument_list|>
name|permissionRuleInfoEntry
range|:
name|permissionEntry
operator|.
name|getValue
argument_list|()
operator|.
name|rules
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|GroupDescription
operator|.
name|Basic
name|group
init|=
name|groupsCollection
operator|.
name|parseId
argument_list|(
name|permissionRuleInfoEntry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnprocessableEntityException
argument_list|(
name|permissionRuleInfoEntry
operator|.
name|getKey
argument_list|()
operator|+
literal|" is not a valid group ID"
argument_list|)
throw|;
block|}
name|PermissionRuleInfo
name|pri
init|=
name|permissionRuleInfoEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|PermissionRule
name|r
init|=
operator|new
name|PermissionRule
argument_list|(
name|GroupReference
operator|.
name|forGroup
argument_list|(
name|group
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|pri
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|pri
operator|.
name|max
operator|!=
literal|null
condition|)
block|{
name|r
operator|.
name|setMax
argument_list|(
name|pri
operator|.
name|max
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pri
operator|.
name|min
operator|!=
literal|null
condition|)
block|{
name|r
operator|.
name|setMin
argument_list|(
name|pri
operator|.
name|min
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pri
operator|.
name|action
operator|!=
literal|null
condition|)
block|{
name|r
operator|.
name|setAction
argument_list|(
name|GetAccess
operator|.
name|ACTION_TYPE
operator|.
name|inverse
argument_list|()
operator|.
name|get
argument_list|(
name|pri
operator|.
name|action
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pri
operator|.
name|force
operator|!=
literal|null
condition|)
block|{
name|r
operator|.
name|setForce
argument_list|(
name|pri
operator|.
name|force
argument_list|)
expr_stmt|;
block|}
block|}
name|p
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
name|accessSection
operator|.
name|getPermissions
argument_list|()
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
name|sections
operator|.
name|add
argument_list|(
name|accessSection
argument_list|)
expr_stmt|;
block|}
return|return
name|sections
return|;
block|}
comment|/**    * Checks that the removals and additions are logically valid, but doesn't check current user's    * permission.    */
DECL|method|validateChanges ( ProjectConfig config, List<AccessSection> removals, List<AccessSection> additions)
name|void
name|validateChanges
parameter_list|(
name|ProjectConfig
name|config
parameter_list|,
name|List
argument_list|<
name|AccessSection
argument_list|>
name|removals
parameter_list|,
name|List
argument_list|<
name|AccessSection
argument_list|>
name|additions
parameter_list|)
throws|throws
name|BadRequestException
throws|,
name|InvalidNameException
block|{
comment|// Perform permission checks
for|for
control|(
name|AccessSection
name|section
range|:
name|Iterables
operator|.
name|concat
argument_list|(
name|additions
argument_list|,
name|removals
argument_list|)
control|)
block|{
name|boolean
name|isGlobalCapabilities
init|=
name|AccessSection
operator|.
name|GLOBAL_CAPABILITIES
operator|.
name|equals
argument_list|(
name|section
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|isGlobalCapabilities
condition|)
block|{
if|if
condition|(
operator|!
name|allProjects
operator|.
name|equals
argument_list|(
name|config
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Cannot edit global capabilities for projects other than "
operator|+
name|allProjects
operator|.
name|get
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
comment|// Perform addition checks
for|for
control|(
name|AccessSection
name|section
range|:
name|additions
control|)
block|{
name|String
name|name
init|=
name|section
operator|.
name|getName
argument_list|()
decl_stmt|;
name|boolean
name|isGlobalCapabilities
init|=
name|AccessSection
operator|.
name|GLOBAL_CAPABILITIES
operator|.
name|equals
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|isGlobalCapabilities
condition|)
block|{
if|if
condition|(
operator|!
name|AccessSection
operator|.
name|isValid
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"invalid section name"
argument_list|)
throw|;
block|}
name|RefPattern
operator|.
name|validate
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Check all permissions for soundness
for|for
control|(
name|Permission
name|p
range|:
name|section
operator|.
name|getPermissions
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|GlobalCapability
operator|.
name|isCapability
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Cannot add non-global capability "
operator|+
name|p
operator|.
name|getName
argument_list|()
operator|+
literal|" to global capabilities"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
DECL|method|applyChanges ( ProjectConfig config, List<AccessSection> removals, List<AccessSection> additions)
name|void
name|applyChanges
parameter_list|(
name|ProjectConfig
name|config
parameter_list|,
name|List
argument_list|<
name|AccessSection
argument_list|>
name|removals
parameter_list|,
name|List
argument_list|<
name|AccessSection
argument_list|>
name|additions
parameter_list|)
block|{
comment|// Apply removals
for|for
control|(
name|AccessSection
name|section
range|:
name|removals
control|)
block|{
if|if
condition|(
name|section
operator|.
name|getPermissions
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Remove entire section
name|config
operator|.
name|remove
argument_list|(
name|config
operator|.
name|getAccessSection
argument_list|(
name|section
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
comment|// Remove specific permissions
for|for
control|(
name|Permission
name|p
range|:
name|section
operator|.
name|getPermissions
argument_list|()
control|)
block|{
if|if
condition|(
name|p
operator|.
name|getRules
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|config
operator|.
name|remove
argument_list|(
name|config
operator|.
name|getAccessSection
argument_list|(
name|section
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|PermissionRule
name|r
range|:
name|p
operator|.
name|getRules
argument_list|()
control|)
block|{
name|config
operator|.
name|remove
argument_list|(
name|config
operator|.
name|getAccessSection
argument_list|(
name|section
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|p
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// Apply additions
for|for
control|(
name|AccessSection
name|section
range|:
name|additions
control|)
block|{
name|AccessSection
name|currentAccessSection
init|=
name|config
operator|.
name|getAccessSection
argument_list|(
name|section
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentAccessSection
operator|==
literal|null
condition|)
block|{
comment|// Add AccessSection
name|config
operator|.
name|replace
argument_list|(
name|section
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|Permission
name|p
range|:
name|section
operator|.
name|getPermissions
argument_list|()
control|)
block|{
name|Permission
name|currentPermission
init|=
name|currentAccessSection
operator|.
name|getPermission
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentPermission
operator|==
literal|null
condition|)
block|{
comment|// Add Permission
name|currentAccessSection
operator|.
name|addPermission
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|PermissionRule
name|r
range|:
name|p
operator|.
name|getRules
argument_list|()
control|)
block|{
comment|// AddPermissionRule
name|currentPermission
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
comment|/**    * Updates the parent project in the given config.    *    * @param identifiedUser the user    * @param config the config to modify    * @param projectName the project for which to change access.    * @param newParentProjectName the new parent to set.    * @param checkAdmin if set, verify that user has administrateServer permission    */
DECL|method|setParentName ( IdentifiedUser identifiedUser, ProjectConfig config, Project.NameKey projectName, Project.NameKey newParentProjectName, boolean checkAdmin)
specifier|public
name|void
name|setParentName
parameter_list|(
name|IdentifiedUser
name|identifiedUser
parameter_list|,
name|ProjectConfig
name|config
parameter_list|,
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|,
name|Project
operator|.
name|NameKey
name|newParentProjectName
parameter_list|,
name|boolean
name|checkAdmin
parameter_list|)
throws|throws
name|ResourceConflictException
throws|,
name|AuthException
throws|,
name|PermissionBackendException
throws|,
name|BadRequestException
block|{
if|if
condition|(
name|newParentProjectName
operator|!=
literal|null
operator|&&
operator|!
name|config
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
operator|.
name|equals
argument_list|(
name|allProjects
argument_list|)
operator|&&
operator|!
name|config
operator|.
name|getProject
argument_list|()
operator|.
name|getParent
argument_list|(
name|allProjects
argument_list|)
operator|.
name|equals
argument_list|(
name|newParentProjectName
argument_list|)
condition|)
block|{
try|try
block|{
name|setParent
operator|.
name|get
argument_list|()
operator|.
name|validateParentUpdate
argument_list|(
name|projectName
argument_list|,
name|identifiedUser
argument_list|,
name|newParentProjectName
operator|.
name|get
argument_list|()
argument_list|,
name|checkAdmin
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnprocessableEntityException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|config
operator|.
name|getProject
argument_list|()
operator|.
name|setParentName
argument_list|(
name|newParentProjectName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

