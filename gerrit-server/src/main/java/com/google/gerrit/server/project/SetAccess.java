begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2016 The Android Open Source Project
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
name|gerrit
operator|.
name|common
operator|.
name|ChangeHooks
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
name|ResourceNotFoundException
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
name|RestModifyView
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
name|server
operator|.
name|CurrentUser
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
name|GroupBackend
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
name|extensions
operator|.
name|events
operator|.
name|GitReferenceUpdated
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
name|MetaDataUpdate
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
name|ObjectId
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
name|RevCommit
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
name|LinkedList
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
DECL|class|SetAccess
specifier|public
class|class
name|SetAccess
implements|implements
name|RestModifyView
argument_list|<
name|ProjectResource
argument_list|,
name|ProjectAccessInput
argument_list|>
block|{
DECL|field|groupBackend
specifier|protected
specifier|final
name|GroupBackend
name|groupBackend
decl_stmt|;
DECL|field|groupsCollection
specifier|private
specifier|final
name|GroupsCollection
name|groupsCollection
decl_stmt|;
DECL|field|metaDataUpdateFactory
specifier|private
specifier|final
name|Provider
argument_list|<
name|MetaDataUpdate
operator|.
name|User
argument_list|>
name|metaDataUpdateFactory
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
DECL|field|hooks
specifier|private
specifier|final
name|ChangeHooks
name|hooks
decl_stmt|;
DECL|field|gitRefUpdated
specifier|private
specifier|final
name|GitReferenceUpdated
name|gitRefUpdated
decl_stmt|;
DECL|field|getAccess
specifier|private
specifier|final
name|GetAccess
name|getAccess
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|identifiedUser
specifier|private
specifier|final
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|identifiedUser
decl_stmt|;
annotation|@
name|Inject
DECL|method|SetAccess (GroupBackend groupBackend, Provider<MetaDataUpdate.User> metaDataUpdateFactory, AllProjectsName allProjects, Provider<SetParent> setParent, ChangeHooks hooks, GitReferenceUpdated gitRefUpdated, GroupsCollection groupsCollection, ProjectCache projectCache, GetAccess getAccess, Provider<IdentifiedUser> identifiedUser)
specifier|private
name|SetAccess
parameter_list|(
name|GroupBackend
name|groupBackend
parameter_list|,
name|Provider
argument_list|<
name|MetaDataUpdate
operator|.
name|User
argument_list|>
name|metaDataUpdateFactory
parameter_list|,
name|AllProjectsName
name|allProjects
parameter_list|,
name|Provider
argument_list|<
name|SetParent
argument_list|>
name|setParent
parameter_list|,
name|ChangeHooks
name|hooks
parameter_list|,
name|GitReferenceUpdated
name|gitRefUpdated
parameter_list|,
name|GroupsCollection
name|groupsCollection
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
name|GetAccess
name|getAccess
parameter_list|,
name|Provider
argument_list|<
name|IdentifiedUser
argument_list|>
name|identifiedUser
parameter_list|)
block|{
name|this
operator|.
name|groupBackend
operator|=
name|groupBackend
expr_stmt|;
name|this
operator|.
name|metaDataUpdateFactory
operator|=
name|metaDataUpdateFactory
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
name|this
operator|.
name|groupsCollection
operator|=
name|groupsCollection
expr_stmt|;
name|this
operator|.
name|hooks
operator|=
name|hooks
expr_stmt|;
name|this
operator|.
name|gitRefUpdated
operator|=
name|gitRefUpdated
expr_stmt|;
name|this
operator|.
name|getAccess
operator|=
name|getAccess
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|identifiedUser
operator|=
name|identifiedUser
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (ProjectResource rsrc, ProjectAccessInput input)
specifier|public
name|ProjectAccessInfo
name|apply
parameter_list|(
name|ProjectResource
name|rsrc
parameter_list|,
name|ProjectAccessInput
name|input
parameter_list|)
throws|throws
name|ResourceNotFoundException
throws|,
name|ResourceConflictException
throws|,
name|IOException
throws|,
name|AuthException
throws|,
name|BadRequestException
throws|,
name|UnprocessableEntityException
block|{
name|List
argument_list|<
name|AccessSection
argument_list|>
name|removals
init|=
name|getAccessSections
argument_list|(
name|input
operator|.
name|remove
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AccessSection
argument_list|>
name|additions
init|=
name|getAccessSections
argument_list|(
name|input
operator|.
name|add
argument_list|)
decl_stmt|;
name|MetaDataUpdate
operator|.
name|User
name|metaDataUpdateUser
init|=
name|metaDataUpdateFactory
operator|.
name|get
argument_list|()
decl_stmt|;
name|ProjectControl
name|projectControl
init|=
name|rsrc
operator|.
name|getControl
argument_list|()
decl_stmt|;
name|ProjectConfig
name|config
decl_stmt|;
name|ObjectId
name|base
decl_stmt|;
name|Project
operator|.
name|NameKey
name|newParentProjectName
init|=
name|input
operator|.
name|parent
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|Project
operator|.
name|NameKey
argument_list|(
name|input
operator|.
name|parent
argument_list|)
decl_stmt|;
try|try
init|(
name|MetaDataUpdate
name|md
init|=
name|metaDataUpdateUser
operator|.
name|create
argument_list|(
name|rsrc
operator|.
name|getNameKey
argument_list|()
argument_list|)
init|)
block|{
name|config
operator|=
name|ProjectConfig
operator|.
name|read
argument_list|(
name|md
argument_list|)
expr_stmt|;
name|base
operator|=
name|config
operator|.
name|getRevision
argument_list|()
expr_stmt|;
comment|// Perform removal checks
for|for
control|(
name|AccessSection
name|section
range|:
name|removals
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
name|checkGlobalCapabilityPermissions
argument_list|(
name|config
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|projectControl
operator|.
name|controlForRef
argument_list|(
name|section
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|isOwner
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"You are not allowed to edit permissions"
operator|+
literal|"for ref: "
operator|+
name|section
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
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
name|isGlobalCapabilities
condition|)
block|{
name|checkGlobalCapabilityPermissions
argument_list|(
name|config
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
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
if|if
condition|(
operator|!
name|projectControl
operator|.
name|controlForRef
argument_list|(
name|name
argument_list|)
operator|.
name|isOwner
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"You are not allowed to edit permissions"
operator|+
literal|"for ref: "
operator|+
name|name
argument_list|)
throw|;
block|}
name|RefControl
operator|.
name|validateRefPattern
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
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
name|isGlobalCapabilities
operator|&&
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
name|projectControl
argument_list|,
name|MoreObjects
operator|.
name|firstNonNull
argument_list|(
name|newParentProjectName
argument_list|,
name|allProjects
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|true
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
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|input
operator|.
name|message
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|input
operator|.
name|message
operator|.
name|endsWith
argument_list|(
literal|"\n"
argument_list|)
condition|)
block|{
name|input
operator|.
name|message
operator|+=
literal|"\n"
expr_stmt|;
block|}
name|md
operator|.
name|setMessage
argument_list|(
name|input
operator|.
name|message
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|md
operator|.
name|setMessage
argument_list|(
literal|"Modify access rules\n"
argument_list|)
expr_stmt|;
block|}
name|updateProjectConfig
argument_list|(
name|projectControl
operator|.
name|getUser
argument_list|()
argument_list|,
name|config
argument_list|,
name|md
argument_list|,
name|base
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidNameException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ConfigInvalidException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
name|rsrc
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|getAccess
operator|.
name|apply
argument_list|(
name|rsrc
operator|.
name|getNameKey
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getAccessSections ( Map<String, AccessSectionInfo> sectionInfos)
specifier|private
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
name|List
argument_list|<
name|AccessSection
argument_list|>
name|sections
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|sectionInfos
operator|==
literal|null
condition|)
block|{
return|return
name|sections
return|;
block|}
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
name|PermissionRuleInfo
name|pri
init|=
name|permissionRuleInfoEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
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
DECL|method|updateProjectConfig (CurrentUser user, ProjectConfig config, MetaDataUpdate md, ObjectId base)
specifier|private
name|void
name|updateProjectConfig
parameter_list|(
name|CurrentUser
name|user
parameter_list|,
name|ProjectConfig
name|config
parameter_list|,
name|MetaDataUpdate
name|md
parameter_list|,
name|ObjectId
name|base
parameter_list|)
throws|throws
name|IOException
block|{
name|RevCommit
name|commit
init|=
name|config
operator|.
name|commit
argument_list|(
name|md
argument_list|)
decl_stmt|;
name|Account
name|account
init|=
name|user
operator|.
name|isIdentifiedUser
argument_list|()
condition|?
name|user
operator|.
name|asIdentifiedUser
argument_list|()
operator|.
name|getAccount
argument_list|()
else|:
literal|null
decl_stmt|;
name|gitRefUpdated
operator|.
name|fire
argument_list|(
name|config
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|,
name|base
argument_list|,
name|commit
operator|.
name|getId
argument_list|()
argument_list|,
name|account
argument_list|)
expr_stmt|;
name|projectCache
operator|.
name|evict
argument_list|(
name|config
operator|.
name|getProject
argument_list|()
argument_list|)
expr_stmt|;
name|hooks
operator|.
name|doRefUpdatedHook
argument_list|(
operator|new
name|Branch
operator|.
name|NameKey
argument_list|(
name|config
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|)
argument_list|,
name|base
argument_list|,
name|commit
operator|.
name|getId
argument_list|()
argument_list|,
name|user
operator|.
name|asIdentifiedUser
argument_list|()
operator|.
name|getAccount
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|checkGlobalCapabilityPermissions (Project.NameKey projectName)
specifier|private
name|void
name|checkGlobalCapabilityPermissions
parameter_list|(
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|)
throws|throws
name|BadRequestException
throws|,
name|AuthException
block|{
if|if
condition|(
operator|!
name|allProjects
operator|.
name|equals
argument_list|(
name|projectName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Cannot edit global capabilities "
operator|+
literal|"for projects other than "
operator|+
name|allProjects
operator|.
name|get
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|identifiedUser
operator|.
name|get
argument_list|()
operator|.
name|getCapabilities
argument_list|()
operator|.
name|canAdministrateServer
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"Editing global capabilities "
operator|+
literal|"requires "
operator|+
name|GlobalCapability
operator|.
name|ADMINISTRATE_SERVER
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

