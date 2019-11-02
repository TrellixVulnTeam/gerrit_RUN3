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
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
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
name|Multimap
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
name|entities
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
name|extensions
operator|.
name|api
operator|.
name|projects
operator|.
name|ParentInput
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
name|Response
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
name|ConfigKey
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
name|ConfigUpdatedEvent
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
name|ConfigUpdatedEvent
operator|.
name|ConfigUpdateEntry
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
name|ConfigUpdatedEvent
operator|.
name|UpdateResult
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
name|GerritConfigListener
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
name|errors
operator|.
name|RepositoryNotFoundException
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

begin_class
annotation|@
name|Singleton
DECL|class|SetParent
specifier|public
class|class
name|SetParent
implements|implements
name|RestModifyView
argument_list|<
name|ProjectResource
argument_list|,
name|ParentInput
argument_list|>
implements|,
name|GerritConfigListener
block|{
DECL|field|cache
specifier|private
specifier|final
name|ProjectCache
name|cache
decl_stmt|;
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|updateFactory
specifier|private
specifier|final
name|Provider
argument_list|<
name|MetaDataUpdate
operator|.
name|Server
argument_list|>
name|updateFactory
decl_stmt|;
DECL|field|allProjects
specifier|private
specifier|final
name|AllProjectsName
name|allProjects
decl_stmt|;
DECL|field|allUsers
specifier|private
specifier|final
name|AllUsersName
name|allUsers
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
specifier|volatile
name|boolean
name|allowProjectOwnersToChangeParent
decl_stmt|;
annotation|@
name|Inject
DECL|method|SetParent ( ProjectCache cache, PermissionBackend permissionBackend, Provider<MetaDataUpdate.Server> updateFactory, AllProjectsName allProjects, AllUsersName allUsers, ProjectConfig.Factory projectConfigFactory, @GerritServerConfig Config config)
name|SetParent
parameter_list|(
name|ProjectCache
name|cache
parameter_list|,
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|Provider
argument_list|<
name|MetaDataUpdate
operator|.
name|Server
argument_list|>
name|updateFactory
parameter_list|,
name|AllProjectsName
name|allProjects
parameter_list|,
name|AllUsersName
name|allUsers
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
name|cache
operator|=
name|cache
expr_stmt|;
name|this
operator|.
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
name|this
operator|.
name|updateFactory
operator|=
name|updateFactory
expr_stmt|;
name|this
operator|.
name|allProjects
operator|=
name|allProjects
expr_stmt|;
name|this
operator|.
name|allUsers
operator|=
name|allUsers
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
DECL|method|apply (ProjectResource rsrc, ParentInput input)
specifier|public
name|Response
argument_list|<
name|String
argument_list|>
name|apply
parameter_list|(
name|ProjectResource
name|rsrc
parameter_list|,
name|ParentInput
name|input
parameter_list|)
throws|throws
name|AuthException
throws|,
name|ResourceConflictException
throws|,
name|ResourceNotFoundException
throws|,
name|UnprocessableEntityException
throws|,
name|IOException
throws|,
name|PermissionBackendException
throws|,
name|BadRequestException
block|{
return|return
name|Response
operator|.
name|ok
argument_list|(
name|apply
argument_list|(
name|rsrc
argument_list|,
name|input
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
DECL|method|apply (ProjectResource rsrc, ParentInput input, boolean checkIfAdmin)
specifier|public
name|String
name|apply
parameter_list|(
name|ProjectResource
name|rsrc
parameter_list|,
name|ParentInput
name|input
parameter_list|,
name|boolean
name|checkIfAdmin
parameter_list|)
throws|throws
name|AuthException
throws|,
name|ResourceConflictException
throws|,
name|ResourceNotFoundException
throws|,
name|UnprocessableEntityException
throws|,
name|IOException
throws|,
name|PermissionBackendException
throws|,
name|BadRequestException
block|{
name|IdentifiedUser
name|user
init|=
name|rsrc
operator|.
name|getUser
argument_list|()
operator|.
name|asIdentifiedUser
argument_list|()
decl_stmt|;
name|String
name|parentName
init|=
name|MoreObjects
operator|.
name|firstNonNull
argument_list|(
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|input
operator|.
name|parent
argument_list|)
argument_list|,
name|allProjects
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|validateParentUpdate
argument_list|(
name|rsrc
operator|.
name|getProjectState
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|user
argument_list|,
name|parentName
argument_list|,
name|checkIfAdmin
argument_list|)
expr_stmt|;
try|try
init|(
name|MetaDataUpdate
name|md
init|=
name|updateFactory
operator|.
name|get
argument_list|()
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
name|ProjectConfig
name|config
init|=
name|projectConfigFactory
operator|.
name|read
argument_list|(
name|md
argument_list|)
decl_stmt|;
name|Project
name|project
init|=
name|config
operator|.
name|getProject
argument_list|()
decl_stmt|;
name|project
operator|.
name|setParentName
argument_list|(
name|parentName
argument_list|)
expr_stmt|;
name|String
name|msg
init|=
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|input
operator|.
name|commitMessage
argument_list|)
decl_stmt|;
if|if
condition|(
name|msg
operator|==
literal|null
condition|)
block|{
name|msg
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"Changed parent to %s.\n"
argument_list|,
name|parentName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|msg
operator|.
name|endsWith
argument_list|(
literal|"\n"
argument_list|)
condition|)
block|{
name|msg
operator|+=
literal|"\n"
expr_stmt|;
block|}
name|md
operator|.
name|setAuthor
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|md
operator|.
name|setMessage
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|config
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
name|cache
operator|.
name|evict
argument_list|(
name|rsrc
operator|.
name|getProjectState
argument_list|()
operator|.
name|getProject
argument_list|()
argument_list|)
expr_stmt|;
name|Project
operator|.
name|NameKey
name|parent
init|=
name|project
operator|.
name|getParent
argument_list|(
name|allProjects
argument_list|)
decl_stmt|;
name|requireNonNull
argument_list|(
name|parent
argument_list|)
expr_stmt|;
return|return
name|parent
operator|.
name|get
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryNotFoundException
name|notFound
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|rsrc
operator|.
name|getName
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
name|String
operator|.
name|format
argument_list|(
literal|"invalid project.config: %s"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
DECL|method|validateParentUpdate ( Project.NameKey project, IdentifiedUser user, String newParent, boolean checkIfAdmin)
specifier|public
name|void
name|validateParentUpdate
parameter_list|(
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
name|IdentifiedUser
name|user
parameter_list|,
name|String
name|newParent
parameter_list|,
name|boolean
name|checkIfAdmin
parameter_list|)
throws|throws
name|AuthException
throws|,
name|ResourceConflictException
throws|,
name|UnprocessableEntityException
throws|,
name|PermissionBackendException
throws|,
name|BadRequestException
block|{
if|if
condition|(
name|checkIfAdmin
condition|)
block|{
if|if
condition|(
name|allowProjectOwnersToChangeParent
condition|)
block|{
name|permissionBackend
operator|.
name|user
argument_list|(
name|user
argument_list|)
operator|.
name|project
argument_list|(
name|project
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
else|else
block|{
name|permissionBackend
operator|.
name|user
argument_list|(
name|user
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
block|}
if|if
condition|(
name|project
operator|.
name|equals
argument_list|(
name|allUsers
argument_list|)
operator|&&
operator|!
name|allProjects
operator|.
name|get
argument_list|()
operator|.
name|equals
argument_list|(
name|newParent
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s must inherit from %s"
argument_list|,
name|allUsers
operator|.
name|get
argument_list|()
argument_list|,
name|allProjects
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|project
operator|.
name|equals
argument_list|(
name|allProjects
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"cannot set parent of "
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
name|allUsers
operator|.
name|get
argument_list|()
operator|.
name|equals
argument_list|(
name|newParent
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Cannot inherit from '%s' project"
argument_list|,
name|allUsers
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|newParent
operator|=
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|newParent
argument_list|)
expr_stmt|;
if|if
condition|(
name|newParent
operator|!=
literal|null
condition|)
block|{
name|ProjectState
name|parent
init|=
name|cache
operator|.
name|get
argument_list|(
name|Project
operator|.
name|nameKey
argument_list|(
name|newParent
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnprocessableEntityException
argument_list|(
literal|"parent project "
operator|+
name|newParent
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
if|if
condition|(
name|parent
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|project
operator|.
name|get
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"cannot set parent to self"
argument_list|)
throw|;
block|}
if|if
condition|(
name|Iterables
operator|.
name|tryFind
argument_list|(
name|parent
operator|.
name|tree
argument_list|()
argument_list|,
name|p
lambda|->
block|{
return|return
name|p
operator|.
name|getNameKey
argument_list|()
operator|.
name|equals
argument_list|(
name|project
argument_list|)
return|;
block|}
argument_list|)
operator|.
name|isPresent
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"cycle exists between "
operator|+
name|project
operator|.
name|get
argument_list|()
operator|+
literal|" and "
operator|+
name|parent
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|configUpdated (ConfigUpdatedEvent event)
specifier|public
name|Multimap
argument_list|<
name|UpdateResult
argument_list|,
name|ConfigUpdateEntry
argument_list|>
name|configUpdated
parameter_list|(
name|ConfigUpdatedEvent
name|event
parameter_list|)
block|{
name|ConfigKey
name|receiveSetParent
init|=
name|ConfigKey
operator|.
name|create
argument_list|(
literal|"receive"
argument_list|,
literal|"allowProjectOwnersToChangeParent"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|event
operator|.
name|isValueUpdated
argument_list|(
name|receiveSetParent
argument_list|)
condition|)
block|{
return|return
name|ConfigUpdatedEvent
operator|.
name|NO_UPDATES
return|;
block|}
try|try
block|{
name|boolean
name|enabled
init|=
name|event
operator|.
name|getNewConfig
argument_list|()
operator|.
name|getBoolean
argument_list|(
literal|"receive"
argument_list|,
literal|"allowProjectOwnersToChangeParent"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|this
operator|.
name|allowProjectOwnersToChangeParent
operator|=
name|enabled
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
return|return
name|event
operator|.
name|reject
argument_list|(
name|receiveSetParent
argument_list|)
return|;
block|}
return|return
name|event
operator|.
name|accept
argument_list|(
name|receiveSetParent
argument_list|)
return|;
block|}
block|}
end_class

end_unit

