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
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Throwables
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
name|Nullable
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
name|extensions
operator|.
name|api
operator|.
name|changes
operator|.
name|AddReviewerInput
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
name|reviewdb
operator|.
name|client
operator|.
name|Change
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
name|PatchSet
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
name|reviewdb
operator|.
name|server
operator|.
name|ReviewDb
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
name|Sequences
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
name|change
operator|.
name|ChangeInserter
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
name|change
operator|.
name|ChangeResource
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
name|ContributorAgreementsChecker
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
name|restapi
operator|.
name|change
operator|.
name|ChangesCollection
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
name|change
operator|.
name|PostReviewers
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
name|gerrit
operator|.
name|server
operator|.
name|update
operator|.
name|BatchUpdate
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
name|update
operator|.
name|UpdateException
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
name|util
operator|.
name|time
operator|.
name|TimeUtil
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
name|ObjectReader
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
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|revwalk
operator|.
name|RevWalk
import|;
end_import

begin_class
DECL|class|ReviewProjectAccess
specifier|public
class|class
name|ReviewProjectAccess
extends|extends
name|ProjectAccessHandler
argument_list|<
name|Change
operator|.
name|Id
argument_list|>
block|{
DECL|interface|Factory
interface|interface
name|Factory
block|{
DECL|method|create ( @ssistedR) Project.NameKey projectName, @Nullable @Assisted ObjectId base, @Assisted List<AccessSection> sectionList, @Nullable @Assisted(R) Project.NameKey parentProjectName, @Nullable @Assisted String message)
name|ReviewProjectAccess
name|create
parameter_list|(
annotation|@
name|Assisted
argument_list|(
literal|"projectName"
argument_list|)
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|,
annotation|@
name|Nullable
annotation|@
name|Assisted
name|ObjectId
name|base
parameter_list|,
annotation|@
name|Assisted
name|List
argument_list|<
name|AccessSection
argument_list|>
name|sectionList
parameter_list|,
annotation|@
name|Nullable
annotation|@
name|Assisted
argument_list|(
literal|"parentProjectName"
argument_list|)
name|Project
operator|.
name|NameKey
name|parentProjectName
parameter_list|,
annotation|@
name|Nullable
annotation|@
name|Assisted
name|String
name|message
parameter_list|)
function_decl|;
block|}
DECL|field|db
specifier|private
specifier|final
name|ReviewDb
name|db
decl_stmt|;
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|seq
specifier|private
specifier|final
name|Sequences
name|seq
decl_stmt|;
DECL|field|reviewersProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|PostReviewers
argument_list|>
name|reviewersProvider
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|changes
specifier|private
specifier|final
name|ChangesCollection
name|changes
decl_stmt|;
DECL|field|changeInserterFactory
specifier|private
specifier|final
name|ChangeInserter
operator|.
name|Factory
name|changeInserterFactory
decl_stmt|;
DECL|field|updateFactory
specifier|private
specifier|final
name|BatchUpdate
operator|.
name|Factory
name|updateFactory
decl_stmt|;
DECL|field|allowProjectOwnersToChangeParent
specifier|private
specifier|final
name|boolean
name|allowProjectOwnersToChangeParent
decl_stmt|;
annotation|@
name|Inject
DECL|method|ReviewProjectAccess ( PermissionBackend permissionBackend, GroupBackend groupBackend, ProjectConfig.Factory projectConfigFactory, MetaDataUpdate.User metaDataUpdateFactory, ReviewDb db, Provider<PostReviewers> reviewersProvider, ProjectCache projectCache, AllProjectsName allProjects, ChangesCollection changes, ChangeInserter.Factory changeInserterFactory, BatchUpdate.Factory updateFactory, Provider<SetParent> setParent, Sequences seq, ContributorAgreementsChecker contributorAgreements, Provider<CurrentUser> user, @GerritServerConfig Config config, @Assisted(R) Project.NameKey projectName, @Nullable @Assisted ObjectId base, @Assisted List<AccessSection> sectionList, @Nullable @Assisted(R) Project.NameKey parentProjectName, @Nullable @Assisted String message)
name|ReviewProjectAccess
parameter_list|(
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|GroupBackend
name|groupBackend
parameter_list|,
name|ProjectConfig
operator|.
name|Factory
name|projectConfigFactory
parameter_list|,
name|MetaDataUpdate
operator|.
name|User
name|metaDataUpdateFactory
parameter_list|,
name|ReviewDb
name|db
parameter_list|,
name|Provider
argument_list|<
name|PostReviewers
argument_list|>
name|reviewersProvider
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
name|AllProjectsName
name|allProjects
parameter_list|,
name|ChangesCollection
name|changes
parameter_list|,
name|ChangeInserter
operator|.
name|Factory
name|changeInserterFactory
parameter_list|,
name|BatchUpdate
operator|.
name|Factory
name|updateFactory
parameter_list|,
name|Provider
argument_list|<
name|SetParent
argument_list|>
name|setParent
parameter_list|,
name|Sequences
name|seq
parameter_list|,
name|ContributorAgreementsChecker
name|contributorAgreements
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|config
parameter_list|,
annotation|@
name|Assisted
argument_list|(
literal|"projectName"
argument_list|)
name|Project
operator|.
name|NameKey
name|projectName
parameter_list|,
annotation|@
name|Nullable
annotation|@
name|Assisted
name|ObjectId
name|base
parameter_list|,
annotation|@
name|Assisted
name|List
argument_list|<
name|AccessSection
argument_list|>
name|sectionList
parameter_list|,
annotation|@
name|Nullable
annotation|@
name|Assisted
argument_list|(
literal|"parentProjectName"
argument_list|)
name|Project
operator|.
name|NameKey
name|parentProjectName
parameter_list|,
annotation|@
name|Nullable
annotation|@
name|Assisted
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|groupBackend
argument_list|,
name|projectConfigFactory
argument_list|,
name|metaDataUpdateFactory
argument_list|,
name|allProjects
argument_list|,
name|setParent
argument_list|,
name|user
operator|.
name|get
argument_list|()
argument_list|,
name|projectName
argument_list|,
name|base
argument_list|,
name|sectionList
argument_list|,
name|parentProjectName
argument_list|,
name|message
argument_list|,
name|contributorAgreements
argument_list|,
name|permissionBackend
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
name|this
operator|.
name|seq
operator|=
name|seq
expr_stmt|;
name|this
operator|.
name|reviewersProvider
operator|=
name|reviewersProvider
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|changes
operator|=
name|changes
expr_stmt|;
name|this
operator|.
name|changeInserterFactory
operator|=
name|changeInserterFactory
expr_stmt|;
name|this
operator|.
name|updateFactory
operator|=
name|updateFactory
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
comment|// TODO(dborowitz): Hack MetaDataUpdate so it can be created within a BatchUpdate and we can avoid
comment|// calling setUpdateRef(false).
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
DECL|method|updateProjectConfig ( ProjectConfig config, MetaDataUpdate md, boolean parentProjectUpdate)
specifier|protected
name|Change
operator|.
name|Id
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
name|OrmException
throws|,
name|AuthException
throws|,
name|PermissionBackendException
throws|,
name|ConfigInvalidException
throws|,
name|ResourceConflictException
block|{
name|PermissionBackend
operator|.
name|ForProject
name|perm
init|=
name|permissionBackend
operator|.
name|user
argument_list|(
name|user
argument_list|)
operator|.
name|project
argument_list|(
name|config
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|check
argument_list|(
name|perm
argument_list|,
name|ProjectPermission
operator|.
name|READ_CONFIG
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
name|RefNames
operator|.
name|REFS_CONFIG
operator|+
literal|" not visible"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|check
argument_list|(
name|perm
argument_list|,
name|ProjectPermission
operator|.
name|WRITE_CONFIG
argument_list|)
operator|&&
operator|!
name|check
argument_list|(
name|perm
operator|.
name|ref
argument_list|(
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|)
argument_list|,
name|RefPermission
operator|.
name|CREATE_CHANGE
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"cannot create change for "
operator|+
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|)
throw|;
block|}
name|projectCache
operator|.
name|checkedGet
argument_list|(
name|config
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|checkStatePermitsWrite
argument_list|()
expr_stmt|;
name|md
operator|.
name|setInsertChangeId
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Change
operator|.
name|Id
name|changeId
init|=
operator|new
name|Change
operator|.
name|Id
argument_list|(
name|seq
operator|.
name|nextChangeId
argument_list|()
argument_list|)
decl_stmt|;
name|RevCommit
name|commit
init|=
name|config
operator|.
name|commitToNewRef
argument_list|(
name|md
argument_list|,
operator|new
name|PatchSet
operator|.
name|Id
argument_list|(
name|changeId
argument_list|,
name|Change
operator|.
name|INITIAL_PATCH_SET_ID
argument_list|)
operator|.
name|toRefName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|commit
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|base
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
try|try
init|(
name|ObjectInserter
name|objInserter
init|=
name|md
operator|.
name|getRepository
argument_list|()
operator|.
name|newObjectInserter
argument_list|()
init|;
name|ObjectReader
name|objReader
operator|=
name|objInserter
operator|.
name|newReader
argument_list|()
init|;
name|RevWalk
name|rw
operator|=
operator|new
name|RevWalk
argument_list|(
name|objReader
argument_list|)
init|;
name|BatchUpdate
name|bu
operator|=
name|updateFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|config
operator|.
name|getProject
argument_list|()
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|user
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
init|)
block|{
name|bu
operator|.
name|setRepository
argument_list|(
name|md
operator|.
name|getRepository
argument_list|()
argument_list|,
name|rw
argument_list|,
name|objInserter
argument_list|)
expr_stmt|;
name|bu
operator|.
name|insertChange
argument_list|(
name|changeInserterFactory
operator|.
name|create
argument_list|(
name|changeId
argument_list|,
name|commit
argument_list|,
name|RefNames
operator|.
name|REFS_CONFIG
argument_list|)
operator|.
name|setValidate
argument_list|(
literal|false
argument_list|)
operator|.
name|setUpdateRef
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// Created by commitToNewRef.
name|bu
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UpdateException
decl||
name|RestApiException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|ChangeResource
name|rsrc
decl_stmt|;
try|try
block|{
name|rsrc
operator|=
name|changes
operator|.
name|parse
argument_list|(
name|changeId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RestApiException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|addProjectOwnersAsReviewers
argument_list|(
name|rsrc
argument_list|)
expr_stmt|;
if|if
condition|(
name|parentProjectUpdate
operator|&&
operator|!
name|allowProjectOwnersToChangeParent
condition|)
block|{
name|addAdministratorsAsReviewers
argument_list|(
name|rsrc
argument_list|)
expr_stmt|;
block|}
return|return
name|changeId
return|;
block|}
DECL|method|addProjectOwnersAsReviewers (ChangeResource rsrc)
specifier|private
name|void
name|addProjectOwnersAsReviewers
parameter_list|(
name|ChangeResource
name|rsrc
parameter_list|)
block|{
specifier|final
name|String
name|projectOwners
init|=
name|groupBackend
operator|.
name|get
argument_list|(
name|SystemGroupBackend
operator|.
name|PROJECT_OWNERS
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
try|try
block|{
name|AddReviewerInput
name|input
init|=
operator|new
name|AddReviewerInput
argument_list|()
decl_stmt|;
name|input
operator|.
name|reviewer
operator|=
name|projectOwners
expr_stmt|;
name|reviewersProvider
operator|.
name|get
argument_list|()
operator|.
name|apply
argument_list|(
name|rsrc
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// one of the owner groups is not visible to the user and this it why it
comment|// can't be added as reviewer
name|Throwables
operator|.
name|throwIfUnchecked
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addAdministratorsAsReviewers (ChangeResource rsrc)
specifier|private
name|void
name|addAdministratorsAsReviewers
parameter_list|(
name|ChangeResource
name|rsrc
parameter_list|)
block|{
name|List
argument_list|<
name|PermissionRule
argument_list|>
name|adminRules
init|=
name|projectCache
operator|.
name|getAllProjects
argument_list|()
operator|.
name|getConfig
argument_list|()
operator|.
name|getAccessSection
argument_list|(
name|AccessSection
operator|.
name|GLOBAL_CAPABILITIES
argument_list|)
operator|.
name|getPermission
argument_list|(
name|GlobalCapability
operator|.
name|ADMINISTRATE_SERVER
argument_list|)
operator|.
name|getRules
argument_list|()
decl_stmt|;
for|for
control|(
name|PermissionRule
name|r
range|:
name|adminRules
control|)
block|{
try|try
block|{
name|AddReviewerInput
name|input
init|=
operator|new
name|AddReviewerInput
argument_list|()
decl_stmt|;
name|input
operator|.
name|reviewer
operator|=
name|r
operator|.
name|getGroup
argument_list|()
operator|.
name|getUUID
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|reviewersProvider
operator|.
name|get
argument_list|()
operator|.
name|apply
argument_list|(
name|rsrc
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
name|Throwables
operator|.
name|throwIfUnchecked
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|check (PermissionBackend.ForRef perm, RefPermission p)
specifier|private
name|boolean
name|check
parameter_list|(
name|PermissionBackend
operator|.
name|ForRef
name|perm
parameter_list|,
name|RefPermission
name|p
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
try|try
block|{
name|perm
operator|.
name|check
argument_list|(
name|p
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|AuthException
name|denied
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|check (PermissionBackend.ForProject perm, ProjectPermission p)
specifier|private
name|boolean
name|check
parameter_list|(
name|PermissionBackend
operator|.
name|ForProject
name|perm
parameter_list|,
name|ProjectPermission
name|p
parameter_list|)
throws|throws
name|PermissionBackendException
block|{
try|try
block|{
name|perm
operator|.
name|check
argument_list|(
name|p
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|AuthException
name|denied
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

