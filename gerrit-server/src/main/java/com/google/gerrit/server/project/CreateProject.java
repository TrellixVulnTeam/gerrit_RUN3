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
name|Objects
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
name|Lists
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
name|errors
operator|.
name|ProjectCreationFailedException
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
name|RequiresCapability
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
name|common
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
name|common
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
name|extensions
operator|.
name|registration
operator|.
name|DynamicSet
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
name|AccountGroup
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
name|gerrit
operator|.
name|server
operator|.
name|validators
operator|.
name|ProjectCreationValidationListener
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
name|validators
operator|.
name|ValidationException
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
name|assistedinject
operator|.
name|Assisted
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

begin_class
annotation|@
name|RequiresCapability
argument_list|(
name|GlobalCapability
operator|.
name|CREATE_PROJECT
argument_list|)
DECL|class|CreateProject
specifier|public
class|class
name|CreateProject
implements|implements
name|RestModifyView
argument_list|<
name|TopLevelResource
argument_list|,
name|ProjectInput
argument_list|>
block|{
DECL|interface|Factory
specifier|public
specifier|static
interface|interface
name|Factory
block|{
DECL|method|create (String name)
name|CreateProject
name|create
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
block|}
DECL|field|createProjectFactory
specifier|private
specifier|final
name|PerformCreateProject
operator|.
name|Factory
name|createProjectFactory
decl_stmt|;
DECL|field|projectsCollection
specifier|private
specifier|final
name|Provider
argument_list|<
name|ProjectsCollection
argument_list|>
name|projectsCollection
decl_stmt|;
DECL|field|groupsCollection
specifier|private
specifier|final
name|Provider
argument_list|<
name|GroupsCollection
argument_list|>
name|groupsCollection
decl_stmt|;
DECL|field|projectCreationValidationListeners
specifier|private
specifier|final
name|DynamicSet
argument_list|<
name|ProjectCreationValidationListener
argument_list|>
name|projectCreationValidationListeners
decl_stmt|;
DECL|field|json
specifier|private
specifier|final
name|ProjectJson
name|json
decl_stmt|;
DECL|field|projectControlFactory
specifier|private
specifier|final
name|ProjectControl
operator|.
name|GenericFactory
name|projectControlFactory
decl_stmt|;
DECL|field|currentUser
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|currentUser
decl_stmt|;
DECL|field|putConfig
specifier|private
specifier|final
name|Provider
argument_list|<
name|PutConfig
argument_list|>
name|putConfig
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
annotation|@
name|Inject
DECL|method|CreateProject (PerformCreateProject.Factory performCreateProjectFactory, Provider<ProjectsCollection> projectsCollection, Provider<GroupsCollection> groupsCollection, ProjectJson json, DynamicSet<ProjectCreationValidationListener> projectCreationValidationListeners, ProjectControl.GenericFactory projectControlFactory, Provider<CurrentUser> currentUser, Provider<PutConfig> putConfig, @Assisted String name)
name|CreateProject
parameter_list|(
name|PerformCreateProject
operator|.
name|Factory
name|performCreateProjectFactory
parameter_list|,
name|Provider
argument_list|<
name|ProjectsCollection
argument_list|>
name|projectsCollection
parameter_list|,
name|Provider
argument_list|<
name|GroupsCollection
argument_list|>
name|groupsCollection
parameter_list|,
name|ProjectJson
name|json
parameter_list|,
name|DynamicSet
argument_list|<
name|ProjectCreationValidationListener
argument_list|>
name|projectCreationValidationListeners
parameter_list|,
name|ProjectControl
operator|.
name|GenericFactory
name|projectControlFactory
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|currentUser
parameter_list|,
name|Provider
argument_list|<
name|PutConfig
argument_list|>
name|putConfig
parameter_list|,
annotation|@
name|Assisted
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|createProjectFactory
operator|=
name|performCreateProjectFactory
expr_stmt|;
name|this
operator|.
name|projectsCollection
operator|=
name|projectsCollection
expr_stmt|;
name|this
operator|.
name|groupsCollection
operator|=
name|groupsCollection
expr_stmt|;
name|this
operator|.
name|projectCreationValidationListeners
operator|=
name|projectCreationValidationListeners
expr_stmt|;
name|this
operator|.
name|json
operator|=
name|json
expr_stmt|;
name|this
operator|.
name|projectControlFactory
operator|=
name|projectControlFactory
expr_stmt|;
name|this
operator|.
name|currentUser
operator|=
name|currentUser
expr_stmt|;
name|this
operator|.
name|putConfig
operator|=
name|putConfig
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply (TopLevelResource resource, ProjectInput input)
specifier|public
name|Response
argument_list|<
name|ProjectInfo
argument_list|>
name|apply
parameter_list|(
name|TopLevelResource
name|resource
parameter_list|,
name|ProjectInput
name|input
parameter_list|)
throws|throws
name|BadRequestException
throws|,
name|UnprocessableEntityException
throws|,
name|ResourceConflictException
throws|,
name|ProjectCreationFailedException
throws|,
name|ResourceNotFoundException
throws|,
name|IOException
block|{
if|if
condition|(
name|input
operator|==
literal|null
condition|)
block|{
name|input
operator|=
operator|new
name|ProjectInput
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|input
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
name|input
operator|.
name|name
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"name must match URL"
argument_list|)
throw|;
block|}
specifier|final
name|CreateProjectArgs
name|args
init|=
operator|new
name|CreateProjectArgs
argument_list|()
decl_stmt|;
name|args
operator|.
name|setProjectName
argument_list|(
name|name
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
name|parent
argument_list|)
condition|)
block|{
name|args
operator|.
name|newParent
operator|=
name|projectsCollection
operator|.
name|get
argument_list|()
operator|.
name|parse
argument_list|(
name|input
operator|.
name|parent
argument_list|)
operator|.
name|getControl
argument_list|()
expr_stmt|;
block|}
name|args
operator|.
name|createEmptyCommit
operator|=
name|input
operator|.
name|createEmptyCommit
expr_stmt|;
name|args
operator|.
name|permissionsOnly
operator|=
name|input
operator|.
name|permissionsOnly
expr_stmt|;
name|args
operator|.
name|projectDescription
operator|=
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|input
operator|.
name|description
argument_list|)
expr_stmt|;
name|args
operator|.
name|submitType
operator|=
name|input
operator|.
name|submitType
expr_stmt|;
name|args
operator|.
name|branch
operator|=
name|input
operator|.
name|branches
expr_stmt|;
if|if
condition|(
name|input
operator|.
name|owners
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|ownerIds
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
name|input
operator|.
name|owners
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|owner
range|:
name|input
operator|.
name|owners
control|)
block|{
name|ownerIds
operator|.
name|add
argument_list|(
name|groupsCollection
operator|.
name|get
argument_list|()
operator|.
name|parse
argument_list|(
name|owner
argument_list|)
operator|.
name|getGroupUUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|args
operator|.
name|ownerIds
operator|=
name|ownerIds
expr_stmt|;
block|}
name|args
operator|.
name|contributorAgreements
operator|=
name|Objects
operator|.
name|firstNonNull
argument_list|(
name|input
operator|.
name|useContributorAgreements
argument_list|,
name|InheritableBoolean
operator|.
name|INHERIT
argument_list|)
expr_stmt|;
name|args
operator|.
name|signedOffBy
operator|=
name|Objects
operator|.
name|firstNonNull
argument_list|(
name|input
operator|.
name|useSignedOffBy
argument_list|,
name|InheritableBoolean
operator|.
name|INHERIT
argument_list|)
expr_stmt|;
name|args
operator|.
name|contentMerge
operator|=
name|input
operator|.
name|submitType
operator|==
name|SubmitType
operator|.
name|FAST_FORWARD_ONLY
condition|?
name|InheritableBoolean
operator|.
name|FALSE
else|:
name|Objects
operator|.
name|firstNonNull
argument_list|(
name|input
operator|.
name|useContentMerge
argument_list|,
name|InheritableBoolean
operator|.
name|INHERIT
argument_list|)
expr_stmt|;
name|args
operator|.
name|changeIdRequired
operator|=
name|Objects
operator|.
name|firstNonNull
argument_list|(
name|input
operator|.
name|requireChangeId
argument_list|,
name|InheritableBoolean
operator|.
name|INHERIT
argument_list|)
expr_stmt|;
try|try
block|{
name|args
operator|.
name|maxObjectSizeLimit
operator|=
name|ProjectConfig
operator|.
name|validMaxObjectSizeLimit
argument_list|(
name|input
operator|.
name|maxObjectSizeLimit
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConfigInvalidException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
for|for
control|(
name|ProjectCreationValidationListener
name|l
range|:
name|projectCreationValidationListeners
control|)
block|{
try|try
block|{
name|l
operator|.
name|validateNewProject
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ValidationException
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
block|}
name|Project
name|p
init|=
name|createProjectFactory
operator|.
name|create
argument_list|(
name|args
argument_list|)
operator|.
name|createProject
argument_list|()
decl_stmt|;
if|if
condition|(
name|input
operator|.
name|pluginConfigValues
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|ProjectControl
name|projectControl
init|=
name|projectControlFactory
operator|.
name|controlFor
argument_list|(
name|p
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|currentUser
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
name|PutConfig
operator|.
name|Input
name|in
init|=
operator|new
name|PutConfig
operator|.
name|Input
argument_list|()
decl_stmt|;
name|in
operator|.
name|pluginConfigValues
operator|=
name|input
operator|.
name|pluginConfigValues
expr_stmt|;
name|putConfig
operator|.
name|get
argument_list|()
operator|.
name|apply
argument_list|(
name|projectControl
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchProjectException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|p
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
name|Response
operator|.
name|created
argument_list|(
name|json
operator|.
name|format
argument_list|(
name|p
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

