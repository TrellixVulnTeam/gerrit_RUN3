begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2011 The Android Open Source Project
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
comment|// limitations under the License
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
name|gerrit
operator|.
name|common
operator|.
name|ProjectUtil
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
name|client
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
name|events
operator|.
name|NewProjectCreatedListener
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
name|GerritPersonIdent
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
name|ProjectOwnerGroups
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
name|GitRepositoryManager
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
name|git
operator|.
name|RepositoryCaseMismatchException
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
name|CommitBuilder
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
name|Constants
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
name|lib
operator|.
name|ObjectInserter
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
name|PersonIdent
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
name|RefUpdate
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
name|RefUpdate
operator|.
name|Result
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Set
import|;
end_import

begin_comment
comment|/** Common class that holds the code to create projects */
end_comment

begin_class
DECL|class|PerformCreateProject
specifier|public
class|class
name|PerformCreateProject
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PerformCreateProject
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|interface|Factory
specifier|public
interface|interface
name|Factory
block|{
DECL|method|create (CreateProjectArgs createProjectArgs)
name|PerformCreateProject
name|create
parameter_list|(
name|CreateProjectArgs
name|createProjectArgs
parameter_list|)
function_decl|;
block|}
DECL|field|cfg
specifier|private
specifier|final
name|Config
name|cfg
decl_stmt|;
DECL|field|projectOwnerGroups
specifier|private
specifier|final
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|projectOwnerGroups
decl_stmt|;
DECL|field|currentUser
specifier|private
specifier|final
name|IdentifiedUser
name|currentUser
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|referenceUpdated
specifier|private
specifier|final
name|GitReferenceUpdated
name|referenceUpdated
decl_stmt|;
DECL|field|createdListener
specifier|private
specifier|final
name|DynamicSet
argument_list|<
name|NewProjectCreatedListener
argument_list|>
name|createdListener
decl_stmt|;
DECL|field|serverIdent
specifier|private
specifier|final
name|PersonIdent
name|serverIdent
decl_stmt|;
DECL|field|createProjectArgs
specifier|private
specifier|final
name|CreateProjectArgs
name|createProjectArgs
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|groupBackend
specifier|private
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
annotation|@
name|Inject
DECL|method|PerformCreateProject (@erritServerConfig Config cfg, @ProjectOwnerGroups Set<AccountGroup.UUID> pOwnerGroups, IdentifiedUser identifiedUser, GitRepositoryManager gitRepoManager, GitReferenceUpdated referenceUpdated, DynamicSet<NewProjectCreatedListener> createdListener, @GerritPersonIdent PersonIdent personIdent, GroupBackend groupBackend, MetaDataUpdate.User metaDataUpdateFactory, @Assisted CreateProjectArgs createPArgs, ProjectCache pCache)
name|PerformCreateProject
parameter_list|(
annotation|@
name|GerritServerConfig
name|Config
name|cfg
parameter_list|,
annotation|@
name|ProjectOwnerGroups
name|Set
argument_list|<
name|AccountGroup
operator|.
name|UUID
argument_list|>
name|pOwnerGroups
parameter_list|,
name|IdentifiedUser
name|identifiedUser
parameter_list|,
name|GitRepositoryManager
name|gitRepoManager
parameter_list|,
name|GitReferenceUpdated
name|referenceUpdated
parameter_list|,
name|DynamicSet
argument_list|<
name|NewProjectCreatedListener
argument_list|>
name|createdListener
parameter_list|,
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|personIdent
parameter_list|,
name|GroupBackend
name|groupBackend
parameter_list|,
name|MetaDataUpdate
operator|.
name|User
name|metaDataUpdateFactory
parameter_list|,
annotation|@
name|Assisted
name|CreateProjectArgs
name|createPArgs
parameter_list|,
name|ProjectCache
name|pCache
parameter_list|)
block|{
name|this
operator|.
name|cfg
operator|=
name|cfg
expr_stmt|;
name|this
operator|.
name|projectOwnerGroups
operator|=
name|pOwnerGroups
expr_stmt|;
name|this
operator|.
name|currentUser
operator|=
name|identifiedUser
expr_stmt|;
name|this
operator|.
name|repoManager
operator|=
name|gitRepoManager
expr_stmt|;
name|this
operator|.
name|referenceUpdated
operator|=
name|referenceUpdated
expr_stmt|;
name|this
operator|.
name|createdListener
operator|=
name|createdListener
expr_stmt|;
name|this
operator|.
name|serverIdent
operator|=
name|personIdent
expr_stmt|;
name|this
operator|.
name|createProjectArgs
operator|=
name|createPArgs
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|pCache
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
block|}
DECL|method|createProject ()
specifier|public
name|Project
name|createProject
parameter_list|()
throws|throws
name|ProjectCreationFailedException
block|{
name|validateParameters
argument_list|()
expr_stmt|;
specifier|final
name|Project
operator|.
name|NameKey
name|nameKey
init|=
name|createProjectArgs
operator|.
name|getProject
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|String
name|head
init|=
name|createProjectArgs
operator|.
name|permissionsOnly
condition|?
name|RefNames
operator|.
name|REFS_CONFIG
else|:
name|createProjectArgs
operator|.
name|branch
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|createRepository
argument_list|(
name|nameKey
argument_list|)
decl_stmt|;
try|try
block|{
name|NewProjectCreatedListener
operator|.
name|Event
name|event
init|=
operator|new
name|NewProjectCreatedListener
operator|.
name|Event
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getProjectName
parameter_list|()
block|{
return|return
name|nameKey
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getHeadName
parameter_list|()
block|{
return|return
name|head
return|;
block|}
block|}
decl_stmt|;
for|for
control|(
name|NewProjectCreatedListener
name|l
range|:
name|createdListener
control|)
block|{
try|try
block|{
name|l
operator|.
name|onNewProjectCreated
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Failure in NewProjectCreatedListener"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|RefUpdate
name|u
init|=
name|repo
operator|.
name|updateRef
argument_list|(
name|Constants
operator|.
name|HEAD
argument_list|)
decl_stmt|;
name|u
operator|.
name|disableRefLog
argument_list|()
expr_stmt|;
name|u
operator|.
name|link
argument_list|(
name|head
argument_list|)
expr_stmt|;
name|createProjectConfig
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|createProjectArgs
operator|.
name|permissionsOnly
operator|&&
name|createProjectArgs
operator|.
name|createEmptyCommit
condition|)
block|{
name|createEmptyCommits
argument_list|(
name|repo
argument_list|,
name|nameKey
argument_list|,
name|createProjectArgs
operator|.
name|branch
argument_list|)
expr_stmt|;
block|}
return|return
name|projectCache
operator|.
name|get
argument_list|(
name|nameKey
argument_list|)
operator|.
name|getProject
argument_list|()
return|;
block|}
finally|finally
block|{
name|repo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryCaseMismatchException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ProjectCreationFailedException
argument_list|(
literal|"Cannot create "
operator|+
name|nameKey
operator|.
name|get
argument_list|()
operator|+
literal|" because the name is already occupied by another project."
operator|+
literal|" The other project has the same name, only spelled in a"
operator|+
literal|" different case."
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|RepositoryNotFoundException
name|badName
parameter_list|)
block|{
throw|throw
operator|new
name|ProjectCreationFailedException
argument_list|(
literal|"Cannot create "
operator|+
name|nameKey
argument_list|,
name|badName
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|err
parameter_list|)
block|{
try|try
block|{
specifier|final
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|nameKey
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|repo
operator|.
name|getObjectDatabase
argument_list|()
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ProjectCreationFailedException
argument_list|(
literal|"project \""
operator|+
name|nameKey
operator|+
literal|"\" exists"
argument_list|)
throw|;
block|}
throw|throw
name|err
throw|;
block|}
finally|finally
block|{
name|repo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioErr
parameter_list|)
block|{
specifier|final
name|String
name|msg
init|=
literal|"Cannot create "
operator|+
name|nameKey
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|err
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ProjectCreationFailedException
argument_list|(
name|msg
argument_list|,
name|ioErr
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
specifier|final
name|String
name|msg
init|=
literal|"Cannot create "
operator|+
name|nameKey
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ProjectCreationFailedException
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|createProjectConfig ()
specifier|private
name|void
name|createProjectConfig
parameter_list|()
throws|throws
name|IOException
throws|,
name|ConfigInvalidException
block|{
specifier|final
name|MetaDataUpdate
name|md
init|=
name|metaDataUpdateFactory
operator|.
name|create
argument_list|(
name|createProjectArgs
operator|.
name|getProject
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|ProjectConfig
name|config
init|=
name|ProjectConfig
operator|.
name|read
argument_list|(
name|md
argument_list|)
decl_stmt|;
name|config
operator|.
name|load
argument_list|(
name|md
argument_list|)
expr_stmt|;
name|Project
name|newProject
init|=
name|config
operator|.
name|getProject
argument_list|()
decl_stmt|;
name|newProject
operator|.
name|setDescription
argument_list|(
name|createProjectArgs
operator|.
name|projectDescription
argument_list|)
expr_stmt|;
name|newProject
operator|.
name|setSubmitType
argument_list|(
name|MoreObjects
operator|.
name|firstNonNull
argument_list|(
name|createProjectArgs
operator|.
name|submitType
argument_list|,
name|cfg
operator|.
name|getEnum
argument_list|(
literal|"repository"
argument_list|,
literal|"*"
argument_list|,
literal|"defaultSubmitType"
argument_list|,
name|SubmitType
operator|.
name|MERGE_IF_NECESSARY
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|newProject
operator|.
name|setUseContributorAgreements
argument_list|(
name|createProjectArgs
operator|.
name|contributorAgreements
argument_list|)
expr_stmt|;
name|newProject
operator|.
name|setUseSignedOffBy
argument_list|(
name|createProjectArgs
operator|.
name|signedOffBy
argument_list|)
expr_stmt|;
name|newProject
operator|.
name|setUseContentMerge
argument_list|(
name|createProjectArgs
operator|.
name|contentMerge
argument_list|)
expr_stmt|;
name|newProject
operator|.
name|setCreateNewChangeForAllNotInTarget
argument_list|(
name|createProjectArgs
operator|.
name|newChangeForAllNotInTarget
argument_list|)
expr_stmt|;
name|newProject
operator|.
name|setRequireChangeID
argument_list|(
name|createProjectArgs
operator|.
name|changeIdRequired
argument_list|)
expr_stmt|;
name|newProject
operator|.
name|setMaxObjectSizeLimit
argument_list|(
name|createProjectArgs
operator|.
name|maxObjectSizeLimit
argument_list|)
expr_stmt|;
if|if
condition|(
name|createProjectArgs
operator|.
name|newParent
operator|!=
literal|null
condition|)
block|{
name|newProject
operator|.
name|setParentName
argument_list|(
name|createProjectArgs
operator|.
name|newParent
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|createProjectArgs
operator|.
name|ownerIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|AccessSection
name|all
init|=
name|config
operator|.
name|getAccessSection
argument_list|(
name|AccessSection
operator|.
name|ALL
argument_list|,
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|AccountGroup
operator|.
name|UUID
name|ownerId
range|:
name|createProjectArgs
operator|.
name|ownerIds
control|)
block|{
name|GroupDescription
operator|.
name|Basic
name|g
init|=
name|groupBackend
operator|.
name|get
argument_list|(
name|ownerId
argument_list|)
decl_stmt|;
if|if
condition|(
name|g
operator|!=
literal|null
condition|)
block|{
name|GroupReference
name|group
init|=
name|config
operator|.
name|resolve
argument_list|(
name|GroupReference
operator|.
name|forGroup
argument_list|(
name|g
argument_list|)
argument_list|)
decl_stmt|;
name|all
operator|.
name|getPermission
argument_list|(
name|Permission
operator|.
name|OWNER
argument_list|,
literal|true
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|PermissionRule
argument_list|(
name|group
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|md
operator|.
name|setMessage
argument_list|(
literal|"Created project\n"
argument_list|)
expr_stmt|;
name|config
operator|.
name|commit
argument_list|(
name|md
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|md
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|projectCache
operator|.
name|onCreateProject
argument_list|(
name|createProjectArgs
operator|.
name|getProject
argument_list|()
argument_list|)
expr_stmt|;
name|repoManager
operator|.
name|setProjectDescription
argument_list|(
name|createProjectArgs
operator|.
name|getProject
argument_list|()
argument_list|,
name|createProjectArgs
operator|.
name|projectDescription
argument_list|)
expr_stmt|;
block|}
DECL|method|validateParameters ()
specifier|private
name|void
name|validateParameters
parameter_list|()
throws|throws
name|ProjectCreationFailedException
block|{
if|if
condition|(
name|createProjectArgs
operator|.
name|getProjectName
argument_list|()
operator|==
literal|null
operator|||
name|createProjectArgs
operator|.
name|getProjectName
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ProjectCreationFailedException
argument_list|(
literal|"Project name is required"
argument_list|)
throw|;
block|}
name|String
name|nameWithoutSuffix
init|=
name|ProjectUtil
operator|.
name|stripGitSuffix
argument_list|(
name|createProjectArgs
operator|.
name|getProjectName
argument_list|()
argument_list|)
decl_stmt|;
name|createProjectArgs
operator|.
name|setProjectName
argument_list|(
name|nameWithoutSuffix
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|currentUser
operator|.
name|getCapabilities
argument_list|()
operator|.
name|canCreateProject
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ProjectCreationFailedException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s does not have \"Create Project\" capability."
argument_list|,
name|currentUser
operator|.
name|getUserName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|createProjectArgs
operator|.
name|ownerIds
operator|==
literal|null
operator|||
name|createProjectArgs
operator|.
name|ownerIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|createProjectArgs
operator|.
name|ownerIds
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|projectOwnerGroups
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|transformedBranches
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|createProjectArgs
operator|.
name|branch
operator|==
literal|null
operator|||
name|createProjectArgs
operator|.
name|branch
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|createProjectArgs
operator|.
name|branch
operator|=
name|Collections
operator|.
name|singletonList
argument_list|(
name|Constants
operator|.
name|MASTER
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|branch
range|:
name|createProjectArgs
operator|.
name|branch
control|)
block|{
while|while
condition|(
name|branch
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|branch
operator|=
name|branch
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|branch
operator|.
name|startsWith
argument_list|(
name|Constants
operator|.
name|R_HEADS
argument_list|)
condition|)
block|{
name|branch
operator|=
name|Constants
operator|.
name|R_HEADS
operator|+
name|branch
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Repository
operator|.
name|isValidRefName
argument_list|(
name|branch
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ProjectCreationFailedException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Branch \"%s\" is not a valid name."
argument_list|,
name|branch
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|transformedBranches
operator|.
name|contains
argument_list|(
name|branch
argument_list|)
condition|)
block|{
name|transformedBranches
operator|.
name|add
argument_list|(
name|branch
argument_list|)
expr_stmt|;
block|}
block|}
name|createProjectArgs
operator|.
name|branch
operator|=
name|transformedBranches
expr_stmt|;
block|}
DECL|method|createEmptyCommits (final Repository repo, final Project.NameKey project, final List<String> refs)
specifier|private
name|void
name|createEmptyCommits
parameter_list|(
specifier|final
name|Repository
name|repo
parameter_list|,
specifier|final
name|Project
operator|.
name|NameKey
name|project
parameter_list|,
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|refs
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|ObjectInserter
name|oi
init|=
name|repo
operator|.
name|newObjectInserter
argument_list|()
init|)
block|{
name|CommitBuilder
name|cb
init|=
operator|new
name|CommitBuilder
argument_list|()
decl_stmt|;
name|cb
operator|.
name|setTreeId
argument_list|(
name|oi
operator|.
name|insert
argument_list|(
name|Constants
operator|.
name|OBJ_TREE
argument_list|,
operator|new
name|byte
index|[]
block|{}
argument_list|)
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setAuthor
argument_list|(
name|metaDataUpdateFactory
operator|.
name|getUserPersonIdent
argument_list|()
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setCommitter
argument_list|(
name|serverIdent
argument_list|)
expr_stmt|;
name|cb
operator|.
name|setMessage
argument_list|(
literal|"Initial empty repository\n"
argument_list|)
expr_stmt|;
name|ObjectId
name|id
init|=
name|oi
operator|.
name|insert
argument_list|(
name|cb
argument_list|)
decl_stmt|;
name|oi
operator|.
name|flush
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|ref
range|:
name|refs
control|)
block|{
name|RefUpdate
name|ru
init|=
name|repo
operator|.
name|updateRef
argument_list|(
name|ref
argument_list|)
decl_stmt|;
name|ru
operator|.
name|setNewObjectId
argument_list|(
name|id
argument_list|)
expr_stmt|;
specifier|final
name|Result
name|result
init|=
name|ru
operator|.
name|update
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|result
condition|)
block|{
case|case
name|NEW
case|:
name|referenceUpdated
operator|.
name|fire
argument_list|(
name|project
argument_list|,
name|ru
argument_list|)
expr_stmt|;
break|break;
default|default:
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Failed to create ref \"%s\": %s"
argument_list|,
name|ref
argument_list|,
name|result
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Cannot create empty commit for "
operator|+
name|createProjectArgs
operator|.
name|getProjectName
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
end_class

end_unit

