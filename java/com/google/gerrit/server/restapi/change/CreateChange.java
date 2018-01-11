begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2014 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.restapi.change
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
name|change
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|Constants
operator|.
name|SIGNED_OFF_BY_TAG
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
name|gerrit
operator|.
name|common
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
name|gerrit
operator|.
name|extensions
operator|.
name|client
operator|.
name|ChangeStatus
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
name|GeneralPreferencesInfo
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
name|common
operator|.
name|ChangeInfo
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
name|ChangeInput
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
name|MergeInput
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
name|MethodNotAllowedException
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
name|BooleanProjectConfig
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
name|ChangeFinder
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
name|PatchSetUtil
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
name|AccountCache
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
name|AccountState
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
name|ChangeJson
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
name|NotifyUtil
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
name|AnonymousCowardName
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
name|MergeUtil
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
name|notedb
operator|.
name|ChangeNotes
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
name|ChangePermission
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
name|InvalidChangeOperationException
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
name|gerrit
operator|.
name|server
operator|.
name|restapi
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
name|restapi
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
name|RetryHelper
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
name|RetryingRestModifyView
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
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
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
name|TimeZone
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
name|Ref
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
name|eclipse
operator|.
name|jgit
operator|.
name|lib
operator|.
name|TreeFormatter
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

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jgit
operator|.
name|util
operator|.
name|ChangeIdUtil
import|;
end_import

begin_class
annotation|@
name|Singleton
DECL|class|CreateChange
specifier|public
class|class
name|CreateChange
extends|extends
name|RetryingRestModifyView
argument_list|<
name|TopLevelResource
argument_list|,
name|ChangeInput
argument_list|,
name|Response
argument_list|<
name|ChangeInfo
argument_list|>
argument_list|>
block|{
DECL|field|anonymousCowardName
specifier|private
specifier|final
name|String
name|anonymousCowardName
decl_stmt|;
DECL|field|db
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
decl_stmt|;
DECL|field|gitManager
specifier|private
specifier|final
name|GitRepositoryManager
name|gitManager
decl_stmt|;
DECL|field|accountCache
specifier|private
specifier|final
name|AccountCache
name|accountCache
decl_stmt|;
DECL|field|seq
specifier|private
specifier|final
name|Sequences
name|seq
decl_stmt|;
DECL|field|serverTimeZone
specifier|private
specifier|final
name|TimeZone
name|serverTimeZone
decl_stmt|;
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|user
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
decl_stmt|;
DECL|field|projectsCollection
specifier|private
specifier|final
name|ProjectsCollection
name|projectsCollection
decl_stmt|;
DECL|field|commits
specifier|private
specifier|final
name|CommitsCollection
name|commits
decl_stmt|;
DECL|field|changeInserterFactory
specifier|private
specifier|final
name|ChangeInserter
operator|.
name|Factory
name|changeInserterFactory
decl_stmt|;
DECL|field|jsonFactory
specifier|private
specifier|final
name|ChangeJson
operator|.
name|Factory
name|jsonFactory
decl_stmt|;
DECL|field|changeFinder
specifier|private
specifier|final
name|ChangeFinder
name|changeFinder
decl_stmt|;
DECL|field|psUtil
specifier|private
specifier|final
name|PatchSetUtil
name|psUtil
decl_stmt|;
DECL|field|mergeUtilFactory
specifier|private
specifier|final
name|MergeUtil
operator|.
name|Factory
name|mergeUtilFactory
decl_stmt|;
DECL|field|submitType
specifier|private
specifier|final
name|SubmitType
name|submitType
decl_stmt|;
DECL|field|notifyUtil
specifier|private
specifier|final
name|NotifyUtil
name|notifyUtil
decl_stmt|;
DECL|field|contributorAgreements
specifier|private
specifier|final
name|ContributorAgreementsChecker
name|contributorAgreements
decl_stmt|;
DECL|field|disablePrivateChanges
specifier|private
specifier|final
name|boolean
name|disablePrivateChanges
decl_stmt|;
annotation|@
name|Inject
DECL|method|CreateChange ( @nonymousCowardName String anonymousCowardName, Provider<ReviewDb> db, GitRepositoryManager gitManager, AccountCache accountCache, Sequences seq, @GerritPersonIdent PersonIdent myIdent, PermissionBackend permissionBackend, Provider<CurrentUser> user, ProjectsCollection projectsCollection, CommitsCollection commits, ChangeInserter.Factory changeInserterFactory, ChangeJson.Factory json, ChangeFinder changeFinder, RetryHelper retryHelper, PatchSetUtil psUtil, @GerritServerConfig Config config, MergeUtil.Factory mergeUtilFactory, NotifyUtil notifyUtil, ContributorAgreementsChecker contributorAgreements)
name|CreateChange
parameter_list|(
annotation|@
name|AnonymousCowardName
name|String
name|anonymousCowardName
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|db
parameter_list|,
name|GitRepositoryManager
name|gitManager
parameter_list|,
name|AccountCache
name|accountCache
parameter_list|,
name|Sequences
name|seq
parameter_list|,
annotation|@
name|GerritPersonIdent
name|PersonIdent
name|myIdent
parameter_list|,
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|user
parameter_list|,
name|ProjectsCollection
name|projectsCollection
parameter_list|,
name|CommitsCollection
name|commits
parameter_list|,
name|ChangeInserter
operator|.
name|Factory
name|changeInserterFactory
parameter_list|,
name|ChangeJson
operator|.
name|Factory
name|json
parameter_list|,
name|ChangeFinder
name|changeFinder
parameter_list|,
name|RetryHelper
name|retryHelper
parameter_list|,
name|PatchSetUtil
name|psUtil
parameter_list|,
annotation|@
name|GerritServerConfig
name|Config
name|config
parameter_list|,
name|MergeUtil
operator|.
name|Factory
name|mergeUtilFactory
parameter_list|,
name|NotifyUtil
name|notifyUtil
parameter_list|,
name|ContributorAgreementsChecker
name|contributorAgreements
parameter_list|)
block|{
name|super
argument_list|(
name|retryHelper
argument_list|)
expr_stmt|;
name|this
operator|.
name|anonymousCowardName
operator|=
name|anonymousCowardName
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|gitManager
operator|=
name|gitManager
expr_stmt|;
name|this
operator|.
name|accountCache
operator|=
name|accountCache
expr_stmt|;
name|this
operator|.
name|seq
operator|=
name|seq
expr_stmt|;
name|this
operator|.
name|serverTimeZone
operator|=
name|myIdent
operator|.
name|getTimeZone
argument_list|()
expr_stmt|;
name|this
operator|.
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|projectsCollection
operator|=
name|projectsCollection
expr_stmt|;
name|this
operator|.
name|commits
operator|=
name|commits
expr_stmt|;
name|this
operator|.
name|changeInserterFactory
operator|=
name|changeInserterFactory
expr_stmt|;
name|this
operator|.
name|jsonFactory
operator|=
name|json
expr_stmt|;
name|this
operator|.
name|changeFinder
operator|=
name|changeFinder
expr_stmt|;
name|this
operator|.
name|psUtil
operator|=
name|psUtil
expr_stmt|;
name|this
operator|.
name|submitType
operator|=
name|config
operator|.
name|getEnum
argument_list|(
literal|"project"
argument_list|,
literal|null
argument_list|,
literal|"submitType"
argument_list|,
name|SubmitType
operator|.
name|MERGE_IF_NECESSARY
argument_list|)
expr_stmt|;
name|this
operator|.
name|disablePrivateChanges
operator|=
name|config
operator|.
name|getBoolean
argument_list|(
literal|"change"
argument_list|,
literal|null
argument_list|,
literal|"disablePrivateChanges"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|mergeUtilFactory
operator|=
name|mergeUtilFactory
expr_stmt|;
name|this
operator|.
name|notifyUtil
operator|=
name|notifyUtil
expr_stmt|;
name|this
operator|.
name|contributorAgreements
operator|=
name|contributorAgreements
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|applyImpl ( BatchUpdate.Factory updateFactory, TopLevelResource parent, ChangeInput input)
specifier|protected
name|Response
argument_list|<
name|ChangeInfo
argument_list|>
name|applyImpl
parameter_list|(
name|BatchUpdate
operator|.
name|Factory
name|updateFactory
parameter_list|,
name|TopLevelResource
name|parent
parameter_list|,
name|ChangeInput
name|input
parameter_list|)
throws|throws
name|OrmException
throws|,
name|IOException
throws|,
name|InvalidChangeOperationException
throws|,
name|RestApiException
throws|,
name|UpdateException
throws|,
name|PermissionBackendException
throws|,
name|ConfigInvalidException
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|input
operator|.
name|project
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"project must be non-empty"
argument_list|)
throw|;
block|}
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|input
operator|.
name|branch
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"branch must be non-empty"
argument_list|)
throw|;
block|}
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|input
operator|.
name|subject
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"commit message must be non-empty"
argument_list|)
throw|;
block|}
if|if
condition|(
name|input
operator|.
name|status
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|input
operator|.
name|status
operator|!=
name|ChangeStatus
operator|.
name|NEW
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"unsupported change status"
argument_list|)
throw|;
block|}
block|}
name|ProjectResource
name|rsrc
init|=
name|projectsCollection
operator|.
name|parse
argument_list|(
name|input
operator|.
name|project
argument_list|)
decl_stmt|;
name|boolean
name|privateByDefault
init|=
name|rsrc
operator|.
name|getProjectState
argument_list|()
operator|.
name|is
argument_list|(
name|BooleanProjectConfig
operator|.
name|PRIVATE_BY_DEFAULT
argument_list|)
decl_stmt|;
name|boolean
name|isPrivate
init|=
name|input
operator|.
name|isPrivate
operator|==
literal|null
condition|?
name|privateByDefault
else|:
name|input
operator|.
name|isPrivate
decl_stmt|;
if|if
condition|(
name|isPrivate
operator|&&
name|disablePrivateChanges
condition|)
block|{
throw|throw
operator|new
name|MethodNotAllowedException
argument_list|(
literal|"private changes are disabled"
argument_list|)
throw|;
block|}
name|contributorAgreements
operator|.
name|check
argument_list|(
name|rsrc
operator|.
name|getNameKey
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
name|Project
operator|.
name|NameKey
name|project
init|=
name|rsrc
operator|.
name|getNameKey
argument_list|()
decl_stmt|;
name|String
name|refName
init|=
name|RefNames
operator|.
name|fullName
argument_list|(
name|input
operator|.
name|branch
argument_list|)
decl_stmt|;
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
name|ref
argument_list|(
name|refName
argument_list|)
operator|.
name|check
argument_list|(
name|RefPermission
operator|.
name|CREATE_CHANGE
argument_list|)
expr_stmt|;
try|try
init|(
name|Repository
name|git
init|=
name|gitManager
operator|.
name|openRepository
argument_list|(
name|project
argument_list|)
init|;
name|ObjectInserter
name|oi
operator|=
name|git
operator|.
name|newObjectInserter
argument_list|()
init|;
name|ObjectReader
name|reader
operator|=
name|oi
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
name|reader
argument_list|)
init|)
block|{
name|ObjectId
name|parentCommit
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|groups
decl_stmt|;
if|if
condition|(
name|input
operator|.
name|baseChange
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|ChangeNotes
argument_list|>
name|notes
init|=
name|changeFinder
operator|.
name|find
argument_list|(
name|input
operator|.
name|baseChange
argument_list|)
decl_stmt|;
if|if
condition|(
name|notes
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|UnprocessableEntityException
argument_list|(
literal|"Base change not found: "
operator|+
name|input
operator|.
name|baseChange
argument_list|)
throw|;
block|}
name|ChangeNotes
name|change
init|=
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|notes
argument_list|)
decl_stmt|;
try|try
block|{
name|permissionBackend
operator|.
name|user
argument_list|(
name|user
argument_list|)
operator|.
name|change
argument_list|(
name|change
argument_list|)
operator|.
name|database
argument_list|(
name|db
argument_list|)
operator|.
name|check
argument_list|(
name|ChangePermission
operator|.
name|READ
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
name|UnprocessableEntityException
argument_list|(
literal|"Read not permitted for "
operator|+
name|input
operator|.
name|baseChange
argument_list|)
throw|;
block|}
name|PatchSet
name|ps
init|=
name|psUtil
operator|.
name|current
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|change
argument_list|)
decl_stmt|;
name|parentCommit
operator|=
name|ObjectId
operator|.
name|fromString
argument_list|(
name|ps
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|groups
operator|=
name|ps
operator|.
name|getGroups
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Ref
name|destRef
init|=
name|git
operator|.
name|getRefDatabase
argument_list|()
operator|.
name|exactRef
argument_list|(
name|refName
argument_list|)
decl_stmt|;
if|if
condition|(
name|destRef
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|Boolean
operator|.
name|TRUE
operator|.
name|equals
argument_list|(
name|input
operator|.
name|newBranch
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
literal|"Branch %s already exists."
argument_list|,
name|refName
argument_list|)
argument_list|)
throw|;
block|}
name|parentCommit
operator|=
name|destRef
operator|.
name|getObjectId
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|Boolean
operator|.
name|TRUE
operator|.
name|equals
argument_list|(
name|input
operator|.
name|newBranch
argument_list|)
condition|)
block|{
name|parentCommit
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnprocessableEntityException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Branch %s does not exist."
argument_list|,
name|refName
argument_list|)
argument_list|)
throw|;
block|}
block|}
name|groups
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
name|RevCommit
name|mergeTip
init|=
name|parentCommit
operator|==
literal|null
condition|?
literal|null
else|:
name|rw
operator|.
name|parseCommit
argument_list|(
name|parentCommit
argument_list|)
decl_stmt|;
name|Timestamp
name|now
init|=
name|TimeUtil
operator|.
name|nowTs
argument_list|()
decl_stmt|;
name|IdentifiedUser
name|me
init|=
name|user
operator|.
name|get
argument_list|()
operator|.
name|asIdentifiedUser
argument_list|()
decl_stmt|;
name|PersonIdent
name|author
init|=
name|me
operator|.
name|newCommitterIdent
argument_list|(
name|now
argument_list|,
name|serverTimeZone
argument_list|)
decl_stmt|;
name|AccountState
name|account
init|=
name|accountCache
operator|.
name|get
argument_list|(
name|me
operator|.
name|getAccountId
argument_list|()
argument_list|)
decl_stmt|;
name|GeneralPreferencesInfo
name|info
init|=
name|account
operator|.
name|getGeneralPreferences
argument_list|()
decl_stmt|;
name|ObjectId
name|treeId
init|=
name|mergeTip
operator|==
literal|null
condition|?
name|emptyTreeId
argument_list|(
name|oi
argument_list|)
else|:
name|mergeTip
operator|.
name|getTree
argument_list|()
decl_stmt|;
name|ObjectId
name|id
init|=
name|ChangeIdUtil
operator|.
name|computeChangeId
argument_list|(
name|treeId
argument_list|,
name|mergeTip
argument_list|,
name|author
argument_list|,
name|author
argument_list|,
name|input
operator|.
name|subject
argument_list|)
decl_stmt|;
name|String
name|commitMessage
init|=
name|ChangeIdUtil
operator|.
name|insertId
argument_list|(
name|input
operator|.
name|subject
argument_list|,
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|Boolean
operator|.
name|TRUE
operator|.
name|equals
argument_list|(
name|info
operator|.
name|signedOffBy
argument_list|)
condition|)
block|{
name|commitMessage
operator|+=
name|String
operator|.
name|format
argument_list|(
literal|"%s%s"
argument_list|,
name|SIGNED_OFF_BY_TAG
argument_list|,
name|account
operator|.
name|getAccount
argument_list|()
operator|.
name|getNameEmail
argument_list|(
name|anonymousCowardName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|RevCommit
name|c
decl_stmt|;
if|if
condition|(
name|input
operator|.
name|merge
operator|!=
literal|null
condition|)
block|{
comment|// create a merge commit
if|if
condition|(
operator|!
operator|(
name|submitType
operator|.
name|equals
argument_list|(
name|SubmitType
operator|.
name|MERGE_ALWAYS
argument_list|)
operator|||
name|submitType
operator|.
name|equals
argument_list|(
name|SubmitType
operator|.
name|MERGE_IF_NECESSARY
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Submit type: "
operator|+
name|submitType
operator|+
literal|" is not supported"
argument_list|)
throw|;
block|}
name|c
operator|=
name|newMergeCommit
argument_list|(
name|git
argument_list|,
name|oi
argument_list|,
name|rw
argument_list|,
name|rsrc
operator|.
name|getProjectState
argument_list|()
argument_list|,
name|mergeTip
argument_list|,
name|input
operator|.
name|merge
argument_list|,
name|author
argument_list|,
name|commitMessage
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// create an empty commit
name|c
operator|=
name|newCommit
argument_list|(
name|oi
argument_list|,
name|rw
argument_list|,
name|author
argument_list|,
name|mergeTip
argument_list|,
name|commitMessage
argument_list|)
expr_stmt|;
block|}
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
name|ChangeInserter
name|ins
init|=
name|changeInserterFactory
operator|.
name|create
argument_list|(
name|changeId
argument_list|,
name|c
argument_list|,
name|refName
argument_list|)
decl_stmt|;
name|ins
operator|.
name|setMessage
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Uploaded patch set %s."
argument_list|,
name|ins
operator|.
name|getPatchSetId
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|topic
init|=
name|input
operator|.
name|topic
decl_stmt|;
if|if
condition|(
name|topic
operator|!=
literal|null
condition|)
block|{
name|topic
operator|=
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|topic
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ins
operator|.
name|setTopic
argument_list|(
name|topic
argument_list|)
expr_stmt|;
name|ins
operator|.
name|setPrivate
argument_list|(
name|isPrivate
argument_list|)
expr_stmt|;
name|ins
operator|.
name|setWorkInProgress
argument_list|(
name|input
operator|.
name|workInProgress
operator|!=
literal|null
operator|&&
name|input
operator|.
name|workInProgress
argument_list|)
expr_stmt|;
name|ins
operator|.
name|setGroups
argument_list|(
name|groups
argument_list|)
expr_stmt|;
name|ins
operator|.
name|setNotify
argument_list|(
name|input
operator|.
name|notify
argument_list|)
expr_stmt|;
name|ins
operator|.
name|setAccountsToNotify
argument_list|(
name|notifyUtil
operator|.
name|resolveAccounts
argument_list|(
name|input
operator|.
name|notifyDetails
argument_list|)
argument_list|)
expr_stmt|;
try|try
init|(
name|BatchUpdate
name|bu
init|=
name|updateFactory
operator|.
name|create
argument_list|(
name|db
operator|.
name|get
argument_list|()
argument_list|,
name|project
argument_list|,
name|me
argument_list|,
name|now
argument_list|)
init|)
block|{
name|bu
operator|.
name|setRepository
argument_list|(
name|git
argument_list|,
name|rw
argument_list|,
name|oi
argument_list|)
expr_stmt|;
name|bu
operator|.
name|insertChange
argument_list|(
name|ins
argument_list|)
expr_stmt|;
name|bu
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
name|ChangeJson
name|json
init|=
name|jsonFactory
operator|.
name|noOptions
argument_list|()
decl_stmt|;
return|return
name|Response
operator|.
name|created
argument_list|(
name|json
operator|.
name|format
argument_list|(
name|ins
operator|.
name|getChange
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
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
block|}
DECL|method|newCommit ( ObjectInserter oi, RevWalk rw, PersonIdent authorIdent, RevCommit mergeTip, String commitMessage)
specifier|private
specifier|static
name|RevCommit
name|newCommit
parameter_list|(
name|ObjectInserter
name|oi
parameter_list|,
name|RevWalk
name|rw
parameter_list|,
name|PersonIdent
name|authorIdent
parameter_list|,
name|RevCommit
name|mergeTip
parameter_list|,
name|String
name|commitMessage
parameter_list|)
throws|throws
name|IOException
block|{
name|CommitBuilder
name|commit
init|=
operator|new
name|CommitBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|mergeTip
operator|==
literal|null
condition|)
block|{
name|commit
operator|.
name|setTreeId
argument_list|(
name|emptyTreeId
argument_list|(
name|oi
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|commit
operator|.
name|setTreeId
argument_list|(
name|mergeTip
operator|.
name|getTree
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setParentId
argument_list|(
name|mergeTip
argument_list|)
expr_stmt|;
block|}
name|commit
operator|.
name|setAuthor
argument_list|(
name|authorIdent
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setCommitter
argument_list|(
name|authorIdent
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setMessage
argument_list|(
name|commitMessage
argument_list|)
expr_stmt|;
return|return
name|rw
operator|.
name|parseCommit
argument_list|(
name|insert
argument_list|(
name|oi
argument_list|,
name|commit
argument_list|)
argument_list|)
return|;
block|}
DECL|method|newMergeCommit ( Repository repo, ObjectInserter oi, RevWalk rw, ProjectState projectState, RevCommit mergeTip, MergeInput merge, PersonIdent authorIdent, String commitMessage)
specifier|private
name|RevCommit
name|newMergeCommit
parameter_list|(
name|Repository
name|repo
parameter_list|,
name|ObjectInserter
name|oi
parameter_list|,
name|RevWalk
name|rw
parameter_list|,
name|ProjectState
name|projectState
parameter_list|,
name|RevCommit
name|mergeTip
parameter_list|,
name|MergeInput
name|merge
parameter_list|,
name|PersonIdent
name|authorIdent
parameter_list|,
name|String
name|commitMessage
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|IOException
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|merge
operator|.
name|source
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"merge.source must be non-empty"
argument_list|)
throw|;
block|}
name|RevCommit
name|sourceCommit
init|=
name|MergeUtil
operator|.
name|resolveCommit
argument_list|(
name|repo
argument_list|,
name|rw
argument_list|,
name|merge
operator|.
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|commits
operator|.
name|canRead
argument_list|(
name|projectState
argument_list|,
name|repo
argument_list|,
name|sourceCommit
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"do not have read permission for: "
operator|+
name|merge
operator|.
name|source
argument_list|)
throw|;
block|}
name|MergeUtil
name|mergeUtil
init|=
name|mergeUtilFactory
operator|.
name|create
argument_list|(
name|projectState
argument_list|)
decl_stmt|;
comment|// default merge strategy from project settings
name|String
name|mergeStrategy
init|=
name|MoreObjects
operator|.
name|firstNonNull
argument_list|(
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|merge
operator|.
name|strategy
argument_list|)
argument_list|,
name|mergeUtil
operator|.
name|mergeStrategyName
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|MergeUtil
operator|.
name|createMergeCommit
argument_list|(
name|oi
argument_list|,
name|repo
operator|.
name|getConfig
argument_list|()
argument_list|,
name|mergeTip
argument_list|,
name|sourceCommit
argument_list|,
name|mergeStrategy
argument_list|,
name|authorIdent
argument_list|,
name|commitMessage
argument_list|,
name|rw
argument_list|)
return|;
block|}
DECL|method|insert (ObjectInserter inserter, CommitBuilder commit)
specifier|private
specifier|static
name|ObjectId
name|insert
parameter_list|(
name|ObjectInserter
name|inserter
parameter_list|,
name|CommitBuilder
name|commit
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnsupportedEncodingException
block|{
name|ObjectId
name|id
init|=
name|inserter
operator|.
name|insert
argument_list|(
name|commit
argument_list|)
decl_stmt|;
name|inserter
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|id
return|;
block|}
DECL|method|emptyTreeId (ObjectInserter inserter)
specifier|private
specifier|static
name|ObjectId
name|emptyTreeId
parameter_list|(
name|ObjectInserter
name|inserter
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|inserter
operator|.
name|insert
argument_list|(
operator|new
name|TreeFormatter
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

