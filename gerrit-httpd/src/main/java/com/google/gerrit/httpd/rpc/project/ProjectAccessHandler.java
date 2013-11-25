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
DECL|package|com.google.gerrit.httpd.rpc.project
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|httpd
operator|.
name|rpc
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
name|gerrit
operator|.
name|common
operator|.
name|ProjectAccessUtil
operator|.
name|mergeSections
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
name|Objects
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
name|common
operator|.
name|errors
operator|.
name|NoSuchGroupException
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
name|UpdateParentFailedException
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
name|httpd
operator|.
name|rpc
operator|.
name|Handler
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
name|account
operator|.
name|GroupBackends
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
name|ProjectControl
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
name|RefControl
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
name|SetParent
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
name|Provider
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
name|ObjectId
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
name|HashSet
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
name|Set
import|;
end_import

begin_class
DECL|class|ProjectAccessHandler
specifier|public
specifier|abstract
class|class
name|ProjectAccessHandler
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Handler
argument_list|<
name|T
argument_list|>
block|{
DECL|field|projectControlFactory
specifier|private
specifier|final
name|ProjectControl
operator|.
name|Factory
name|projectControlFactory
decl_stmt|;
DECL|field|groupBackend
specifier|protected
specifier|final
name|GroupBackend
name|groupBackend
decl_stmt|;
DECL|field|metaDataUpdateFactory
specifier|private
specifier|final
name|MetaDataUpdate
operator|.
name|User
name|metaDataUpdateFactory
decl_stmt|;
DECL|field|allProjects
specifier|private
specifier|final
name|AllProjectsNameProvider
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
DECL|field|projectName
specifier|protected
specifier|final
name|Project
operator|.
name|NameKey
name|projectName
decl_stmt|;
DECL|field|base
specifier|protected
specifier|final
name|ObjectId
name|base
decl_stmt|;
DECL|field|sectionList
specifier|private
name|List
argument_list|<
name|AccessSection
argument_list|>
name|sectionList
decl_stmt|;
DECL|field|parentProjectName
specifier|private
specifier|final
name|Project
operator|.
name|NameKey
name|parentProjectName
decl_stmt|;
DECL|field|message
specifier|protected
name|String
name|message
decl_stmt|;
DECL|field|checkIfOwner
specifier|private
name|boolean
name|checkIfOwner
decl_stmt|;
DECL|method|ProjectAccessHandler (ProjectControl.Factory projectControlFactory, GroupBackend groupBackend, MetaDataUpdate.User metaDataUpdateFactory, AllProjectsNameProvider allProjects, Provider<SetParent> setParent, Project.NameKey projectName, ObjectId base, List<AccessSection> sectionList, Project.NameKey parentProjectName, String message, boolean checkIfOwner)
specifier|protected
name|ProjectAccessHandler
parameter_list|(
name|ProjectControl
operator|.
name|Factory
name|projectControlFactory
parameter_list|,
name|GroupBackend
name|groupBackend
parameter_list|,
name|MetaDataUpdate
operator|.
name|User
name|metaDataUpdateFactory
parameter_list|,
name|AllProjectsNameProvider
name|allProjects
parameter_list|,
name|Provider
argument_list|<
name|SetParent
argument_list|>
name|setParent
parameter_list|,
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|,
name|ObjectId
name|base
parameter_list|,
name|List
argument_list|<
name|AccessSection
argument_list|>
name|sectionList
parameter_list|,
name|Project
operator|.
name|NameKey
name|parentProjectName
parameter_list|,
name|String
name|message
parameter_list|,
name|boolean
name|checkIfOwner
parameter_list|)
block|{
name|this
operator|.
name|projectControlFactory
operator|=
name|projectControlFactory
expr_stmt|;
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
name|projectName
operator|=
name|projectName
expr_stmt|;
name|this
operator|.
name|base
operator|=
name|base
expr_stmt|;
name|this
operator|.
name|sectionList
operator|=
name|sectionList
expr_stmt|;
name|this
operator|.
name|parentProjectName
operator|=
name|parentProjectName
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
name|this
operator|.
name|checkIfOwner
operator|=
name|checkIfOwner
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
specifier|final
name|T
name|call
parameter_list|()
throws|throws
name|NoSuchProjectException
throws|,
name|IOException
throws|,
name|ConfigInvalidException
throws|,
name|InvalidNameException
throws|,
name|NoSuchGroupException
throws|,
name|OrmException
throws|,
name|UpdateParentFailedException
block|{
specifier|final
name|ProjectControl
name|projectControl
init|=
name|projectControlFactory
operator|.
name|controlFor
argument_list|(
name|projectName
argument_list|)
decl_stmt|;
specifier|final
name|MetaDataUpdate
name|md
decl_stmt|;
try|try
block|{
name|md
operator|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|projectName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RepositoryNotFoundException
name|notFound
parameter_list|)
block|{
throw|throw
operator|new
name|NoSuchProjectException
argument_list|(
name|projectName
argument_list|)
throw|;
block|}
try|try
block|{
name|ProjectConfig
name|config
init|=
name|ProjectConfig
operator|.
name|read
argument_list|(
name|md
argument_list|,
name|base
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|toDelete
init|=
name|scanSectionNames
argument_list|(
name|config
argument_list|)
decl_stmt|;
for|for
control|(
name|AccessSection
name|section
range|:
name|mergeSections
argument_list|(
name|sectionList
argument_list|)
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
if|if
condition|(
name|AccessSection
operator|.
name|GLOBAL_CAPABILITIES
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|checkIfOwner
operator|&&
operator|!
name|projectControl
operator|.
name|isOwner
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|replace
argument_list|(
name|config
argument_list|,
name|toDelete
argument_list|,
name|section
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|AccessSection
operator|.
name|isValid
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
name|checkIfOwner
operator|&&
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
continue|continue;
block|}
name|RefControl
operator|.
name|validateRefPattern
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|replace
argument_list|(
name|config
argument_list|,
name|toDelete
argument_list|,
name|section
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|String
name|name
range|:
name|toDelete
control|)
block|{
if|if
condition|(
name|AccessSection
operator|.
name|GLOBAL_CAPABILITIES
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|checkIfOwner
operator|||
name|projectControl
operator|.
name|isOwner
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
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|checkIfOwner
operator|||
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
name|config
operator|.
name|remove
argument_list|(
name|config
operator|.
name|getAccessSection
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|boolean
name|parentProjectUpdate
init|=
literal|false
decl_stmt|;
if|if
condition|(
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
operator|.
name|get
argument_list|()
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
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
name|parentProjectName
argument_list|)
condition|)
block|{
name|parentProjectUpdate
operator|=
literal|true
expr_stmt|;
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
name|Objects
operator|.
name|firstNonNull
argument_list|(
name|parentProjectName
argument_list|,
name|allProjects
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|checkIfOwner
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
name|UpdateParentFailedException
argument_list|(
literal|"You are not allowed to change the parent project since you are "
operator|+
literal|"not an administrator. You may save the modifications for review "
operator|+
literal|"so that an administrator can approve them."
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ResourceConflictException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|UpdateParentFailedException
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
catch|catch
parameter_list|(
name|UnprocessableEntityException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|UpdateParentFailedException
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
name|parentProjectName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|message
operator|!=
literal|null
operator|&&
operator|!
name|message
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|message
operator|.
name|endsWith
argument_list|(
literal|"\n"
argument_list|)
condition|)
block|{
name|message
operator|+=
literal|"\n"
expr_stmt|;
block|}
name|md
operator|.
name|setMessage
argument_list|(
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
return|return
name|updateProjectConfig
argument_list|(
name|config
argument_list|,
name|md
argument_list|,
name|parentProjectUpdate
argument_list|)
return|;
block|}
finally|finally
block|{
name|md
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|updateProjectConfig (ProjectConfig config, MetaDataUpdate md, boolean parentProjectUpdate)
specifier|protected
specifier|abstract
name|T
name|updateProjectConfig
parameter_list|(
name|ProjectConfig
name|config
parameter_list|,
name|MetaDataUpdate
name|md
parameter_list|,
name|boolean
name|parentProjectUpdate
parameter_list|)
throws|throws
name|IOException
throws|,
name|NoSuchProjectException
throws|,
name|ConfigInvalidException
throws|,
name|OrmException
function_decl|;
DECL|method|replace (ProjectConfig config, Set<String> toDelete, AccessSection section)
specifier|private
name|void
name|replace
parameter_list|(
name|ProjectConfig
name|config
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|toDelete
parameter_list|,
name|AccessSection
name|section
parameter_list|)
throws|throws
name|NoSuchGroupException
block|{
for|for
control|(
name|Permission
name|permission
range|:
name|section
operator|.
name|getPermissions
argument_list|()
control|)
block|{
for|for
control|(
name|PermissionRule
name|rule
range|:
name|permission
operator|.
name|getRules
argument_list|()
control|)
block|{
name|lookupGroup
argument_list|(
name|rule
argument_list|)
expr_stmt|;
block|}
block|}
name|config
operator|.
name|replace
argument_list|(
name|section
argument_list|)
expr_stmt|;
name|toDelete
operator|.
name|remove
argument_list|(
name|section
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|scanSectionNames (ProjectConfig config)
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|scanSectionNames
parameter_list|(
name|ProjectConfig
name|config
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|AccessSection
name|section
range|:
name|config
operator|.
name|getAccessSections
argument_list|()
control|)
block|{
name|names
operator|.
name|add
argument_list|(
name|section
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|names
return|;
block|}
DECL|method|lookupGroup (PermissionRule rule)
specifier|private
name|void
name|lookupGroup
parameter_list|(
name|PermissionRule
name|rule
parameter_list|)
throws|throws
name|NoSuchGroupException
block|{
name|GroupReference
name|ref
init|=
name|rule
operator|.
name|getGroup
argument_list|()
decl_stmt|;
if|if
condition|(
name|ref
operator|.
name|getUUID
argument_list|()
operator|==
literal|null
condition|)
block|{
specifier|final
name|GroupReference
name|group
init|=
name|GroupBackends
operator|.
name|findBestSuggestion
argument_list|(
name|groupBackend
argument_list|,
name|ref
operator|.
name|getName
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
name|NoSuchGroupException
argument_list|(
name|ref
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
name|ref
operator|.
name|setUUID
argument_list|(
name|group
operator|.
name|getUUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

