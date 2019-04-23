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
name|exceptions
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
name|CreateGroupPermissionSyncer
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
name|git
operator|.
name|meta
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
name|RefPermission
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
name|ProjectResource
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
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
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
DECL|field|accessUtil
specifier|private
specifier|final
name|SetAccessUtil
name|accessUtil
decl_stmt|;
DECL|field|createGroupPermissionSyncer
specifier|private
specifier|final
name|CreateGroupPermissionSyncer
name|createGroupPermissionSyncer
decl_stmt|;
DECL|field|projectConfigFactory
specifier|private
specifier|final
name|ProjectConfig
operator|.
name|Factory
name|projectConfigFactory
decl_stmt|;
annotation|@
name|Inject
DECL|method|SetAccess ( GroupBackend groupBackend, PermissionBackend permissionBackend, Provider<MetaDataUpdate.User> metaDataUpdateFactory, ProjectCache projectCache, GetAccess getAccess, Provider<IdentifiedUser> identifiedUser, SetAccessUtil accessUtil, CreateGroupPermissionSyncer createGroupPermissionSyncer, ProjectConfig.Factory projectConfigFactory)
specifier|private
name|SetAccess
parameter_list|(
name|GroupBackend
name|groupBackend
parameter_list|,
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|Provider
argument_list|<
name|MetaDataUpdate
operator|.
name|User
argument_list|>
name|metaDataUpdateFactory
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
parameter_list|,
name|SetAccessUtil
name|accessUtil
parameter_list|,
name|CreateGroupPermissionSyncer
name|createGroupPermissionSyncer
parameter_list|,
name|ProjectConfig
operator|.
name|Factory
name|projectConfigFactory
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
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
name|this
operator|.
name|metaDataUpdateFactory
operator|=
name|metaDataUpdateFactory
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
name|this
operator|.
name|accessUtil
operator|=
name|accessUtil
expr_stmt|;
name|this
operator|.
name|createGroupPermissionSyncer
operator|=
name|createGroupPermissionSyncer
expr_stmt|;
name|this
operator|.
name|projectConfigFactory
operator|=
name|projectConfigFactory
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
throws|,
name|PermissionBackendException
block|{
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
name|ProjectConfig
name|config
decl_stmt|;
name|List
argument_list|<
name|AccessSection
argument_list|>
name|removals
init|=
name|accessUtil
operator|.
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
name|accessUtil
operator|.
name|getAccessSections
argument_list|(
name|input
operator|.
name|add
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
name|projectConfigFactory
operator|.
name|read
argument_list|(
name|md
argument_list|)
expr_stmt|;
comment|// Check that the user has the right permissions.
name|boolean
name|checkedAdmin
init|=
literal|false
decl_stmt|;
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
name|checkedAdmin
condition|)
block|{
name|permissionBackend
operator|.
name|currentUser
argument_list|()
operator|.
name|check
argument_list|(
name|GlobalPermission
operator|.
name|ADMINISTRATE_SERVER
argument_list|)
expr_stmt|;
name|checkedAdmin
operator|=
literal|true
expr_stmt|;
block|}
block|}
else|else
block|{
name|permissionBackend
operator|.
name|currentUser
argument_list|()
operator|.
name|project
argument_list|(
name|rsrc
operator|.
name|getNameKey
argument_list|()
argument_list|)
operator|.
name|ref
argument_list|(
name|section
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|check
argument_list|(
name|RefPermission
operator|.
name|WRITE_CONFIG
argument_list|)
expr_stmt|;
block|}
block|}
name|accessUtil
operator|.
name|validateChanges
argument_list|(
name|config
argument_list|,
name|removals
argument_list|,
name|additions
argument_list|)
expr_stmt|;
name|accessUtil
operator|.
name|applyChanges
argument_list|(
name|config
argument_list|,
name|removals
argument_list|,
name|additions
argument_list|)
expr_stmt|;
name|accessUtil
operator|.
name|setParentName
argument_list|(
name|identifiedUser
operator|.
name|get
argument_list|()
argument_list|,
name|config
argument_list|,
name|rsrc
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|input
operator|.
name|parent
operator|==
literal|null
condition|?
literal|null
else|:
name|Project
operator|.
name|nameKey
argument_list|(
name|input
operator|.
name|parent
argument_list|)
argument_list|,
operator|!
name|checkedAdmin
argument_list|)
expr_stmt|;
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
name|config
operator|.
name|commit
argument_list|(
name|md
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
name|createGroupPermissionSyncer
operator|.
name|syncIfNeeded
argument_list|()
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
block|}
end_class

end_unit

