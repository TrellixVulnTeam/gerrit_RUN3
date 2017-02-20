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
DECL|package|com.google.gerrit.server.api.projects
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|api
operator|.
name|projects
package|;
end_package

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
name|api
operator|.
name|projects
operator|.
name|BranchApi
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
name|BranchInfo
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
name|ChildProjectApi
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
name|CommitApi
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
name|ConfigInfo
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
name|ConfigInput
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
name|DeleteBranchesInput
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
name|DeleteTagsInput
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
name|DescriptionInput
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
name|ProjectApi
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
name|ProjectInput
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
name|TagApi
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
name|TagInfo
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
name|ProjectInfo
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
name|IdString
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
name|RestApiException
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
name|TopLevelResource
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
name|project
operator|.
name|ChildProjectsCollection
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
name|CommitsCollection
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
name|CreateProject
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
name|DeleteBranches
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
name|DeleteTags
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
name|GetAccess
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
name|GetConfig
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
name|GetDescription
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
name|ListBranches
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
name|ListChildProjects
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
name|ListTags
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
name|ProjectJson
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
name|ProjectsCollection
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
name|PutConfig
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
name|PutDescription
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
name|SetAccess
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
name|assistedinject
operator|.
name|Assisted
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
name|assistedinject
operator|.
name|AssistedInject
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
DECL|class|ProjectApiImpl
specifier|public
class|class
name|ProjectApiImpl
implements|implements
name|ProjectApi
block|{
DECL|interface|Factory
interface|interface
name|Factory
block|{
DECL|method|create (ProjectResource project)
name|ProjectApiImpl
name|create
parameter_list|(
name|ProjectResource
name|project
parameter_list|)
function_decl|;
DECL|method|create (String name)
name|ProjectApiImpl
name|create
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
block|}
DECL|field|user
specifier|private
specifier|final
name|CurrentUser
name|user
decl_stmt|;
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|createProjectFactory
specifier|private
specifier|final
name|CreateProject
operator|.
name|Factory
name|createProjectFactory
decl_stmt|;
DECL|field|projectApi
specifier|private
specifier|final
name|ProjectApiImpl
operator|.
name|Factory
name|projectApi
decl_stmt|;
DECL|field|projects
specifier|private
specifier|final
name|ProjectsCollection
name|projects
decl_stmt|;
DECL|field|getDescription
specifier|private
specifier|final
name|GetDescription
name|getDescription
decl_stmt|;
DECL|field|putDescription
specifier|private
specifier|final
name|PutDescription
name|putDescription
decl_stmt|;
DECL|field|childApi
specifier|private
specifier|final
name|ChildProjectApiImpl
operator|.
name|Factory
name|childApi
decl_stmt|;
DECL|field|children
specifier|private
specifier|final
name|ChildProjectsCollection
name|children
decl_stmt|;
DECL|field|project
specifier|private
specifier|final
name|ProjectResource
name|project
decl_stmt|;
DECL|field|projectJson
specifier|private
specifier|final
name|ProjectJson
name|projectJson
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|branchApi
specifier|private
specifier|final
name|BranchApiImpl
operator|.
name|Factory
name|branchApi
decl_stmt|;
DECL|field|tagApi
specifier|private
specifier|final
name|TagApiImpl
operator|.
name|Factory
name|tagApi
decl_stmt|;
DECL|field|getAccess
specifier|private
specifier|final
name|GetAccess
name|getAccess
decl_stmt|;
DECL|field|setAccess
specifier|private
specifier|final
name|SetAccess
name|setAccess
decl_stmt|;
DECL|field|getConfig
specifier|private
specifier|final
name|GetConfig
name|getConfig
decl_stmt|;
DECL|field|putConfig
specifier|private
specifier|final
name|PutConfig
name|putConfig
decl_stmt|;
DECL|field|listBranches
specifier|private
specifier|final
name|ListBranches
name|listBranches
decl_stmt|;
DECL|field|listTags
specifier|private
specifier|final
name|ListTags
name|listTags
decl_stmt|;
DECL|field|deleteBranches
specifier|private
specifier|final
name|DeleteBranches
name|deleteBranches
decl_stmt|;
DECL|field|deleteTags
specifier|private
specifier|final
name|DeleteTags
name|deleteTags
decl_stmt|;
DECL|field|commitsCollection
specifier|private
specifier|final
name|CommitsCollection
name|commitsCollection
decl_stmt|;
DECL|field|commitApi
specifier|private
specifier|final
name|CommitApiImpl
operator|.
name|Factory
name|commitApi
decl_stmt|;
annotation|@
name|AssistedInject
DECL|method|ProjectApiImpl ( CurrentUser user, PermissionBackend permissionBackend, CreateProject.Factory createProjectFactory, ProjectApiImpl.Factory projectApi, ProjectsCollection projects, GetDescription getDescription, PutDescription putDescription, ChildProjectApiImpl.Factory childApi, ChildProjectsCollection children, ProjectJson projectJson, BranchApiImpl.Factory branchApiFactory, TagApiImpl.Factory tagApiFactory, GetAccess getAccess, SetAccess setAccess, GetConfig getConfig, PutConfig putConfig, ListBranches listBranches, ListTags listTags, DeleteBranches deleteBranches, DeleteTags deleteTags, CommitsCollection commitsCollection, CommitApiImpl.Factory commitApi, @Assisted ProjectResource project)
name|ProjectApiImpl
parameter_list|(
name|CurrentUser
name|user
parameter_list|,
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|CreateProject
operator|.
name|Factory
name|createProjectFactory
parameter_list|,
name|ProjectApiImpl
operator|.
name|Factory
name|projectApi
parameter_list|,
name|ProjectsCollection
name|projects
parameter_list|,
name|GetDescription
name|getDescription
parameter_list|,
name|PutDescription
name|putDescription
parameter_list|,
name|ChildProjectApiImpl
operator|.
name|Factory
name|childApi
parameter_list|,
name|ChildProjectsCollection
name|children
parameter_list|,
name|ProjectJson
name|projectJson
parameter_list|,
name|BranchApiImpl
operator|.
name|Factory
name|branchApiFactory
parameter_list|,
name|TagApiImpl
operator|.
name|Factory
name|tagApiFactory
parameter_list|,
name|GetAccess
name|getAccess
parameter_list|,
name|SetAccess
name|setAccess
parameter_list|,
name|GetConfig
name|getConfig
parameter_list|,
name|PutConfig
name|putConfig
parameter_list|,
name|ListBranches
name|listBranches
parameter_list|,
name|ListTags
name|listTags
parameter_list|,
name|DeleteBranches
name|deleteBranches
parameter_list|,
name|DeleteTags
name|deleteTags
parameter_list|,
name|CommitsCollection
name|commitsCollection
parameter_list|,
name|CommitApiImpl
operator|.
name|Factory
name|commitApi
parameter_list|,
annotation|@
name|Assisted
name|ProjectResource
name|project
parameter_list|)
block|{
name|this
argument_list|(
name|user
argument_list|,
name|permissionBackend
argument_list|,
name|createProjectFactory
argument_list|,
name|projectApi
argument_list|,
name|projects
argument_list|,
name|getDescription
argument_list|,
name|putDescription
argument_list|,
name|childApi
argument_list|,
name|children
argument_list|,
name|projectJson
argument_list|,
name|branchApiFactory
argument_list|,
name|tagApiFactory
argument_list|,
name|getAccess
argument_list|,
name|setAccess
argument_list|,
name|getConfig
argument_list|,
name|putConfig
argument_list|,
name|listBranches
argument_list|,
name|listTags
argument_list|,
name|deleteBranches
argument_list|,
name|deleteTags
argument_list|,
name|project
argument_list|,
name|commitsCollection
argument_list|,
name|commitApi
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AssistedInject
DECL|method|ProjectApiImpl ( CurrentUser user, PermissionBackend permissionBackend, CreateProject.Factory createProjectFactory, ProjectApiImpl.Factory projectApi, ProjectsCollection projects, GetDescription getDescription, PutDescription putDescription, ChildProjectApiImpl.Factory childApi, ChildProjectsCollection children, ProjectJson projectJson, BranchApiImpl.Factory branchApiFactory, TagApiImpl.Factory tagApiFactory, GetAccess getAccess, SetAccess setAccess, GetConfig getConfig, PutConfig putConfig, ListBranches listBranches, ListTags listTags, DeleteBranches deleteBranches, DeleteTags deleteTags, CommitsCollection commitsCollection, CommitApiImpl.Factory commitApi, @Assisted String name)
name|ProjectApiImpl
parameter_list|(
name|CurrentUser
name|user
parameter_list|,
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|CreateProject
operator|.
name|Factory
name|createProjectFactory
parameter_list|,
name|ProjectApiImpl
operator|.
name|Factory
name|projectApi
parameter_list|,
name|ProjectsCollection
name|projects
parameter_list|,
name|GetDescription
name|getDescription
parameter_list|,
name|PutDescription
name|putDescription
parameter_list|,
name|ChildProjectApiImpl
operator|.
name|Factory
name|childApi
parameter_list|,
name|ChildProjectsCollection
name|children
parameter_list|,
name|ProjectJson
name|projectJson
parameter_list|,
name|BranchApiImpl
operator|.
name|Factory
name|branchApiFactory
parameter_list|,
name|TagApiImpl
operator|.
name|Factory
name|tagApiFactory
parameter_list|,
name|GetAccess
name|getAccess
parameter_list|,
name|SetAccess
name|setAccess
parameter_list|,
name|GetConfig
name|getConfig
parameter_list|,
name|PutConfig
name|putConfig
parameter_list|,
name|ListBranches
name|listBranches
parameter_list|,
name|ListTags
name|listTags
parameter_list|,
name|DeleteBranches
name|deleteBranches
parameter_list|,
name|DeleteTags
name|deleteTags
parameter_list|,
name|CommitsCollection
name|commitsCollection
parameter_list|,
name|CommitApiImpl
operator|.
name|Factory
name|commitApi
parameter_list|,
annotation|@
name|Assisted
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|user
argument_list|,
name|permissionBackend
argument_list|,
name|createProjectFactory
argument_list|,
name|projectApi
argument_list|,
name|projects
argument_list|,
name|getDescription
argument_list|,
name|putDescription
argument_list|,
name|childApi
argument_list|,
name|children
argument_list|,
name|projectJson
argument_list|,
name|branchApiFactory
argument_list|,
name|tagApiFactory
argument_list|,
name|getAccess
argument_list|,
name|setAccess
argument_list|,
name|getConfig
argument_list|,
name|putConfig
argument_list|,
name|listBranches
argument_list|,
name|listTags
argument_list|,
name|deleteBranches
argument_list|,
name|deleteTags
argument_list|,
literal|null
argument_list|,
name|commitsCollection
argument_list|,
name|commitApi
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|ProjectApiImpl ( CurrentUser user, PermissionBackend permissionBackend, CreateProject.Factory createProjectFactory, ProjectApiImpl.Factory projectApi, ProjectsCollection projects, GetDescription getDescription, PutDescription putDescription, ChildProjectApiImpl.Factory childApi, ChildProjectsCollection children, ProjectJson projectJson, BranchApiImpl.Factory branchApiFactory, TagApiImpl.Factory tagApiFactory, GetAccess getAccess, SetAccess setAccess, GetConfig getConfig, PutConfig putConfig, ListBranches listBranches, ListTags listTags, DeleteBranches deleteBranches, DeleteTags deleteTags, ProjectResource project, CommitsCollection commitsCollection, CommitApiImpl.Factory commitApi, String name)
specifier|private
name|ProjectApiImpl
parameter_list|(
name|CurrentUser
name|user
parameter_list|,
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|CreateProject
operator|.
name|Factory
name|createProjectFactory
parameter_list|,
name|ProjectApiImpl
operator|.
name|Factory
name|projectApi
parameter_list|,
name|ProjectsCollection
name|projects
parameter_list|,
name|GetDescription
name|getDescription
parameter_list|,
name|PutDescription
name|putDescription
parameter_list|,
name|ChildProjectApiImpl
operator|.
name|Factory
name|childApi
parameter_list|,
name|ChildProjectsCollection
name|children
parameter_list|,
name|ProjectJson
name|projectJson
parameter_list|,
name|BranchApiImpl
operator|.
name|Factory
name|branchApiFactory
parameter_list|,
name|TagApiImpl
operator|.
name|Factory
name|tagApiFactory
parameter_list|,
name|GetAccess
name|getAccess
parameter_list|,
name|SetAccess
name|setAccess
parameter_list|,
name|GetConfig
name|getConfig
parameter_list|,
name|PutConfig
name|putConfig
parameter_list|,
name|ListBranches
name|listBranches
parameter_list|,
name|ListTags
name|listTags
parameter_list|,
name|DeleteBranches
name|deleteBranches
parameter_list|,
name|DeleteTags
name|deleteTags
parameter_list|,
name|ProjectResource
name|project
parameter_list|,
name|CommitsCollection
name|commitsCollection
parameter_list|,
name|CommitApiImpl
operator|.
name|Factory
name|commitApi
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
name|this
operator|.
name|createProjectFactory
operator|=
name|createProjectFactory
expr_stmt|;
name|this
operator|.
name|projectApi
operator|=
name|projectApi
expr_stmt|;
name|this
operator|.
name|projects
operator|=
name|projects
expr_stmt|;
name|this
operator|.
name|getDescription
operator|=
name|getDescription
expr_stmt|;
name|this
operator|.
name|putDescription
operator|=
name|putDescription
expr_stmt|;
name|this
operator|.
name|childApi
operator|=
name|childApi
expr_stmt|;
name|this
operator|.
name|children
operator|=
name|children
expr_stmt|;
name|this
operator|.
name|projectJson
operator|=
name|projectJson
expr_stmt|;
name|this
operator|.
name|project
operator|=
name|project
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|branchApi
operator|=
name|branchApiFactory
expr_stmt|;
name|this
operator|.
name|tagApi
operator|=
name|tagApiFactory
expr_stmt|;
name|this
operator|.
name|getAccess
operator|=
name|getAccess
expr_stmt|;
name|this
operator|.
name|setAccess
operator|=
name|setAccess
expr_stmt|;
name|this
operator|.
name|getConfig
operator|=
name|getConfig
expr_stmt|;
name|this
operator|.
name|putConfig
operator|=
name|putConfig
expr_stmt|;
name|this
operator|.
name|listBranches
operator|=
name|listBranches
expr_stmt|;
name|this
operator|.
name|listTags
operator|=
name|listTags
expr_stmt|;
name|this
operator|.
name|deleteBranches
operator|=
name|deleteBranches
expr_stmt|;
name|this
operator|.
name|deleteTags
operator|=
name|deleteTags
expr_stmt|;
name|this
operator|.
name|commitsCollection
operator|=
name|commitsCollection
expr_stmt|;
name|this
operator|.
name|commitApi
operator|=
name|commitApi
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|create ()
specifier|public
name|ProjectApi
name|create
parameter_list|()
throws|throws
name|RestApiException
block|{
return|return
name|create
argument_list|(
operator|new
name|ProjectInput
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|create (ProjectInput in)
specifier|public
name|ProjectApi
name|create
parameter_list|(
name|ProjectInput
name|in
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"Project already exists"
argument_list|)
throw|;
block|}
if|if
condition|(
name|in
operator|.
name|name
operator|!=
literal|null
operator|&&
operator|!
name|name
operator|.
name|equals
argument_list|(
name|in
operator|.
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"name must match input.name"
argument_list|)
throw|;
block|}
name|CreateProject
name|impl
init|=
name|createProjectFactory
operator|.
name|create
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|permissionBackend
operator|.
name|user
argument_list|(
name|user
argument_list|)
operator|.
name|checkAny
argument_list|(
name|GlobalPermission
operator|.
name|fromAnnotation
argument_list|(
name|impl
operator|.
name|getClass
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|impl
operator|.
name|apply
argument_list|(
name|TopLevelResource
operator|.
name|INSTANCE
argument_list|,
name|in
argument_list|)
expr_stmt|;
return|return
name|projectApi
operator|.
name|create
argument_list|(
name|projects
operator|.
name|parse
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|ConfigInvalidException
decl||
name|PermissionBackendException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot create project: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|get ()
specifier|public
name|ProjectInfo
name|get
parameter_list|()
throws|throws
name|RestApiException
block|{
if|if
condition|(
name|project
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
block|}
return|return
name|projectJson
operator|.
name|format
argument_list|(
name|project
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|description ()
specifier|public
name|String
name|description
parameter_list|()
throws|throws
name|RestApiException
block|{
return|return
name|getDescription
operator|.
name|apply
argument_list|(
name|checkExists
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|access ()
specifier|public
name|ProjectAccessInfo
name|access
parameter_list|()
throws|throws
name|RestApiException
block|{
try|try
block|{
return|return
name|getAccess
operator|.
name|apply
argument_list|(
name|checkExists
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot get access rights"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|access (ProjectAccessInput p)
specifier|public
name|ProjectAccessInfo
name|access
parameter_list|(
name|ProjectAccessInput
name|p
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
return|return
name|setAccess
operator|.
name|apply
argument_list|(
name|checkExists
argument_list|()
argument_list|,
name|p
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot put access rights"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|description (DescriptionInput in)
specifier|public
name|void
name|description
parameter_list|(
name|DescriptionInput
name|in
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
name|putDescription
operator|.
name|apply
argument_list|(
name|checkExists
argument_list|()
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot put project description"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|config ()
specifier|public
name|ConfigInfo
name|config
parameter_list|()
throws|throws
name|RestApiException
block|{
return|return
name|getConfig
operator|.
name|apply
argument_list|(
name|checkExists
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|config (ConfigInput in)
specifier|public
name|ConfigInfo
name|config
parameter_list|(
name|ConfigInput
name|in
parameter_list|)
throws|throws
name|RestApiException
block|{
return|return
name|putConfig
operator|.
name|apply
argument_list|(
name|checkExists
argument_list|()
argument_list|,
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|branches ()
specifier|public
name|ListRefsRequest
argument_list|<
name|BranchInfo
argument_list|>
name|branches
parameter_list|()
block|{
return|return
operator|new
name|ListRefsRequest
argument_list|<
name|BranchInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|BranchInfo
argument_list|>
name|get
parameter_list|()
throws|throws
name|RestApiException
block|{
return|return
name|listBranches
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|listBranches (ListRefsRequest<BranchInfo> request)
specifier|private
name|List
argument_list|<
name|BranchInfo
argument_list|>
name|listBranches
parameter_list|(
name|ListRefsRequest
argument_list|<
name|BranchInfo
argument_list|>
name|request
parameter_list|)
throws|throws
name|RestApiException
block|{
name|listBranches
operator|.
name|setLimit
argument_list|(
name|request
operator|.
name|getLimit
argument_list|()
argument_list|)
expr_stmt|;
name|listBranches
operator|.
name|setStart
argument_list|(
name|request
operator|.
name|getStart
argument_list|()
argument_list|)
expr_stmt|;
name|listBranches
operator|.
name|setMatchSubstring
argument_list|(
name|request
operator|.
name|getSubstring
argument_list|()
argument_list|)
expr_stmt|;
name|listBranches
operator|.
name|setMatchRegex
argument_list|(
name|request
operator|.
name|getRegex
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|listBranches
operator|.
name|apply
argument_list|(
name|checkExists
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot list branches"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|tags ()
specifier|public
name|ListRefsRequest
argument_list|<
name|TagInfo
argument_list|>
name|tags
parameter_list|()
block|{
return|return
operator|new
name|ListRefsRequest
argument_list|<
name|TagInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|TagInfo
argument_list|>
name|get
parameter_list|()
throws|throws
name|RestApiException
block|{
return|return
name|listTags
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
return|;
block|}
DECL|method|listTags (ListRefsRequest<TagInfo> request)
specifier|private
name|List
argument_list|<
name|TagInfo
argument_list|>
name|listTags
parameter_list|(
name|ListRefsRequest
argument_list|<
name|TagInfo
argument_list|>
name|request
parameter_list|)
throws|throws
name|RestApiException
block|{
name|listTags
operator|.
name|setLimit
argument_list|(
name|request
operator|.
name|getLimit
argument_list|()
argument_list|)
expr_stmt|;
name|listTags
operator|.
name|setStart
argument_list|(
name|request
operator|.
name|getStart
argument_list|()
argument_list|)
expr_stmt|;
name|listTags
operator|.
name|setMatchSubstring
argument_list|(
name|request
operator|.
name|getSubstring
argument_list|()
argument_list|)
expr_stmt|;
name|listTags
operator|.
name|setMatchRegex
argument_list|(
name|request
operator|.
name|getRegex
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|listTags
operator|.
name|apply
argument_list|(
name|checkExists
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot list tags"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|children ()
specifier|public
name|List
argument_list|<
name|ProjectInfo
argument_list|>
name|children
parameter_list|()
throws|throws
name|RestApiException
block|{
return|return
name|children
argument_list|(
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|children (boolean recursive)
specifier|public
name|List
argument_list|<
name|ProjectInfo
argument_list|>
name|children
parameter_list|(
name|boolean
name|recursive
parameter_list|)
throws|throws
name|RestApiException
block|{
name|ListChildProjects
name|list
init|=
name|children
operator|.
name|list
argument_list|()
decl_stmt|;
name|list
operator|.
name|setRecursive
argument_list|(
name|recursive
argument_list|)
expr_stmt|;
return|return
name|list
operator|.
name|apply
argument_list|(
name|checkExists
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|child (String name)
specifier|public
name|ChildProjectApi
name|child
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
return|return
name|childApi
operator|.
name|create
argument_list|(
name|children
operator|.
name|parse
argument_list|(
name|checkExists
argument_list|()
argument_list|,
name|IdString
operator|.
name|fromDecoded
argument_list|(
name|name
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot parse child project"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|branch (String ref)
specifier|public
name|BranchApi
name|branch
parameter_list|(
name|String
name|ref
parameter_list|)
throws|throws
name|ResourceNotFoundException
block|{
return|return
name|branchApi
operator|.
name|create
argument_list|(
name|checkExists
argument_list|()
argument_list|,
name|ref
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|tag (String ref)
specifier|public
name|TagApi
name|tag
parameter_list|(
name|String
name|ref
parameter_list|)
throws|throws
name|ResourceNotFoundException
block|{
return|return
name|tagApi
operator|.
name|create
argument_list|(
name|checkExists
argument_list|()
argument_list|,
name|ref
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|deleteBranches (DeleteBranchesInput in)
specifier|public
name|void
name|deleteBranches
parameter_list|(
name|DeleteBranchesInput
name|in
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
name|deleteBranches
operator|.
name|apply
argument_list|(
name|checkExists
argument_list|()
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot delete branches"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|deleteTags (DeleteTagsInput in)
specifier|public
name|void
name|deleteTags
parameter_list|(
name|DeleteTagsInput
name|in
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
name|deleteTags
operator|.
name|apply
argument_list|(
name|checkExists
argument_list|()
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|OrmException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot delete tags"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|commit (String commit)
specifier|public
name|CommitApi
name|commit
parameter_list|(
name|String
name|commit
parameter_list|)
throws|throws
name|RestApiException
block|{
try|try
block|{
return|return
name|commitApi
operator|.
name|create
argument_list|(
name|commitsCollection
operator|.
name|parse
argument_list|(
name|checkExists
argument_list|()
argument_list|,
name|IdString
operator|.
name|fromDecoded
argument_list|(
name|commit
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RestApiException
argument_list|(
literal|"Cannot parse commit"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|checkExists ()
specifier|private
name|ProjectResource
name|checkExists
parameter_list|()
throws|throws
name|ResourceNotFoundException
block|{
if|if
condition|(
name|project
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|name
argument_list|)
throw|;
block|}
return|return
name|project
return|;
block|}
block|}
end_class

end_unit

