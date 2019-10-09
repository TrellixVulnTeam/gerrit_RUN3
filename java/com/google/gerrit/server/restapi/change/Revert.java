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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|MoreObjects
operator|.
name|firstNonNull
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|extensions
operator|.
name|conditions
operator|.
name|BooleanCondition
operator|.
name|and
import|;
end_import

begin_import
import|import static
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
operator|.
name|CREATE_CHANGE
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
name|flogger
operator|.
name|FluentLogger
import|;
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
name|NotifyHandling
import|;
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
name|RevertInput
import|;
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
name|webui
operator|.
name|UiAction
import|;
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
name|ChangeMessage
import|;
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
name|server
operator|.
name|ApprovalsUtil
import|;
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
name|ChangeMessagesUtil
import|;
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
name|ChangeUtil
import|;
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
name|ReviewerSet
import|;
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
name|ChangeMessages
import|;
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
name|change
operator|.
name|NotifyResolver
import|;
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
name|ChangeReverted
import|;
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
name|mail
operator|.
name|send
operator|.
name|RevertedSender
import|;
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
name|notedb
operator|.
name|ReviewerStateInternal
import|;
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
name|NoSuchChangeException
import|;
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
name|BatchUpdateOp
import|;
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
name|ChangeContext
import|;
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
name|Context
import|;
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
name|sql
operator|.
name|Timestamp
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|MessageFormat
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
name|Set
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
DECL|class|Revert
specifier|public
class|class
name|Revert
extends|extends
name|RetryingRestModifyView
argument_list|<
name|ChangeResource
argument_list|,
name|RevertInput
argument_list|,
name|ChangeInfo
argument_list|>
implements|implements
name|UiAction
argument_list|<
name|ChangeResource
argument_list|>
block|{
DECL|field|logger
specifier|private
specifier|static
specifier|final
name|FluentLogger
name|logger
init|=
name|FluentLogger
operator|.
name|forEnclosingClass
argument_list|()
decl_stmt|;
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|changeInserterFactory
specifier|private
specifier|final
name|ChangeInserter
operator|.
name|Factory
name|changeInserterFactory
decl_stmt|;
DECL|field|cmUtil
specifier|private
specifier|final
name|ChangeMessagesUtil
name|cmUtil
decl_stmt|;
DECL|field|seq
specifier|private
specifier|final
name|Sequences
name|seq
decl_stmt|;
DECL|field|psUtil
specifier|private
specifier|final
name|PatchSetUtil
name|psUtil
decl_stmt|;
DECL|field|revertedSenderFactory
specifier|private
specifier|final
name|RevertedSender
operator|.
name|Factory
name|revertedSenderFactory
decl_stmt|;
DECL|field|json
specifier|private
specifier|final
name|ChangeJson
operator|.
name|Factory
name|json
decl_stmt|;
DECL|field|serverIdent
specifier|private
specifier|final
name|Provider
argument_list|<
name|PersonIdent
argument_list|>
name|serverIdent
decl_stmt|;
DECL|field|approvalsUtil
specifier|private
specifier|final
name|ApprovalsUtil
name|approvalsUtil
decl_stmt|;
DECL|field|changeReverted
specifier|private
specifier|final
name|ChangeReverted
name|changeReverted
decl_stmt|;
DECL|field|contributorAgreements
specifier|private
specifier|final
name|ContributorAgreementsChecker
name|contributorAgreements
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|notifyResolver
specifier|private
specifier|final
name|NotifyResolver
name|notifyResolver
decl_stmt|;
annotation|@
name|Inject
DECL|method|Revert ( PermissionBackend permissionBackend, GitRepositoryManager repoManager, ChangeInserter.Factory changeInserterFactory, ChangeMessagesUtil cmUtil, RetryHelper retryHelper, Sequences seq, PatchSetUtil psUtil, RevertedSender.Factory revertedSenderFactory, ChangeJson.Factory json, @GerritPersonIdent Provider<PersonIdent> serverIdent, ApprovalsUtil approvalsUtil, ChangeReverted changeReverted, ContributorAgreementsChecker contributorAgreements, ProjectCache projectCache, NotifyResolver notifyResolver)
name|Revert
parameter_list|(
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|ChangeInserter
operator|.
name|Factory
name|changeInserterFactory
parameter_list|,
name|ChangeMessagesUtil
name|cmUtil
parameter_list|,
name|RetryHelper
name|retryHelper
parameter_list|,
name|Sequences
name|seq
parameter_list|,
name|PatchSetUtil
name|psUtil
parameter_list|,
name|RevertedSender
operator|.
name|Factory
name|revertedSenderFactory
parameter_list|,
name|ChangeJson
operator|.
name|Factory
name|json
parameter_list|,
annotation|@
name|GerritPersonIdent
name|Provider
argument_list|<
name|PersonIdent
argument_list|>
name|serverIdent
parameter_list|,
name|ApprovalsUtil
name|approvalsUtil
parameter_list|,
name|ChangeReverted
name|changeReverted
parameter_list|,
name|ContributorAgreementsChecker
name|contributorAgreements
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
name|NotifyResolver
name|notifyResolver
parameter_list|)
block|{
name|super
argument_list|(
name|retryHelper
argument_list|)
expr_stmt|;
name|this
operator|.
name|permissionBackend
operator|=
name|permissionBackend
expr_stmt|;
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|this
operator|.
name|changeInserterFactory
operator|=
name|changeInserterFactory
expr_stmt|;
name|this
operator|.
name|cmUtil
operator|=
name|cmUtil
expr_stmt|;
name|this
operator|.
name|seq
operator|=
name|seq
expr_stmt|;
name|this
operator|.
name|psUtil
operator|=
name|psUtil
expr_stmt|;
name|this
operator|.
name|revertedSenderFactory
operator|=
name|revertedSenderFactory
expr_stmt|;
name|this
operator|.
name|json
operator|=
name|json
expr_stmt|;
name|this
operator|.
name|serverIdent
operator|=
name|serverIdent
expr_stmt|;
name|this
operator|.
name|approvalsUtil
operator|=
name|approvalsUtil
expr_stmt|;
name|this
operator|.
name|changeReverted
operator|=
name|changeReverted
expr_stmt|;
name|this
operator|.
name|contributorAgreements
operator|=
name|contributorAgreements
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|notifyResolver
operator|=
name|notifyResolver
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|applyImpl ( BatchUpdate.Factory updateFactory, ChangeResource rsrc, RevertInput input)
specifier|public
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
name|ChangeResource
name|rsrc
parameter_list|,
name|RevertInput
name|input
parameter_list|)
throws|throws
name|IOException
throws|,
name|RestApiException
throws|,
name|UpdateException
throws|,
name|NoSuchChangeException
throws|,
name|PermissionBackendException
throws|,
name|NoSuchProjectException
throws|,
name|ConfigInvalidException
block|{
name|Change
name|change
init|=
name|rsrc
operator|.
name|getChange
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|change
operator|.
name|isMerged
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"change is "
operator|+
name|ChangeUtil
operator|.
name|status
argument_list|(
name|change
argument_list|)
argument_list|)
throw|;
block|}
name|contributorAgreements
operator|.
name|check
argument_list|(
name|rsrc
operator|.
name|getProject
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
name|permissionBackend
operator|.
name|user
argument_list|(
name|rsrc
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|ref
argument_list|(
name|change
operator|.
name|getDest
argument_list|()
argument_list|)
operator|.
name|check
argument_list|(
name|CREATE_CHANGE
argument_list|)
expr_stmt|;
name|projectCache
operator|.
name|checkedGet
argument_list|(
name|rsrc
operator|.
name|getProject
argument_list|()
argument_list|)
operator|.
name|checkStatePermitsWrite
argument_list|()
expr_stmt|;
name|Change
operator|.
name|Id
name|revertId
init|=
name|revert
argument_list|(
name|updateFactory
argument_list|,
name|rsrc
operator|.
name|getNotes
argument_list|()
argument_list|,
name|rsrc
operator|.
name|getUser
argument_list|()
argument_list|,
name|input
argument_list|)
decl_stmt|;
return|return
name|Response
operator|.
name|ok
argument_list|(
name|json
operator|.
name|noOptions
argument_list|()
operator|.
name|format
argument_list|(
name|rsrc
operator|.
name|getProject
argument_list|()
argument_list|,
name|revertId
argument_list|)
argument_list|)
return|;
block|}
DECL|method|revert ( BatchUpdate.Factory updateFactory, ChangeNotes notes, CurrentUser user, RevertInput input)
specifier|private
name|Change
operator|.
name|Id
name|revert
parameter_list|(
name|BatchUpdate
operator|.
name|Factory
name|updateFactory
parameter_list|,
name|ChangeNotes
name|notes
parameter_list|,
name|CurrentUser
name|user
parameter_list|,
name|RevertInput
name|input
parameter_list|)
throws|throws
name|IOException
throws|,
name|RestApiException
throws|,
name|UpdateException
throws|,
name|ConfigInvalidException
block|{
name|String
name|message
init|=
name|Strings
operator|.
name|emptyToNull
argument_list|(
name|input
operator|.
name|message
argument_list|)
decl_stmt|;
name|Change
operator|.
name|Id
name|changeIdToRevert
init|=
name|notes
operator|.
name|getChangeId
argument_list|()
decl_stmt|;
name|PatchSet
operator|.
name|Id
name|patchSetId
init|=
name|notes
operator|.
name|getChange
argument_list|()
operator|.
name|currentPatchSetId
argument_list|()
decl_stmt|;
name|PatchSet
name|patch
init|=
name|psUtil
operator|.
name|get
argument_list|(
name|notes
argument_list|,
name|patchSetId
argument_list|)
decl_stmt|;
if|if
condition|(
name|patch
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|changeIdToRevert
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|Project
operator|.
name|NameKey
name|project
init|=
name|notes
operator|.
name|getProjectName
argument_list|()
decl_stmt|;
try|try
init|(
name|Repository
name|git
init|=
name|repoManager
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
name|revWalk
operator|=
operator|new
name|RevWalk
argument_list|(
name|reader
argument_list|)
init|)
block|{
name|RevCommit
name|commitToRevert
init|=
name|revWalk
operator|.
name|parseCommit
argument_list|(
name|patch
operator|.
name|commitId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|commitToRevert
operator|.
name|getParentCount
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"Cannot revert initial commit"
argument_list|)
throw|;
block|}
name|Timestamp
name|now
init|=
name|TimeUtil
operator|.
name|nowTs
argument_list|()
decl_stmt|;
name|PersonIdent
name|committerIdent
init|=
name|serverIdent
operator|.
name|get
argument_list|()
decl_stmt|;
name|PersonIdent
name|authorIdent
init|=
name|user
operator|.
name|asIdentifiedUser
argument_list|()
operator|.
name|newCommitterIdent
argument_list|(
name|now
argument_list|,
name|committerIdent
operator|.
name|getTimeZone
argument_list|()
argument_list|)
decl_stmt|;
name|RevCommit
name|parentToCommitToRevert
init|=
name|commitToRevert
operator|.
name|getParent
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|revWalk
operator|.
name|parseHeaders
argument_list|(
name|parentToCommitToRevert
argument_list|)
expr_stmt|;
name|CommitBuilder
name|revertCommitBuilder
init|=
operator|new
name|CommitBuilder
argument_list|()
decl_stmt|;
name|revertCommitBuilder
operator|.
name|addParentId
argument_list|(
name|commitToRevert
argument_list|)
expr_stmt|;
name|revertCommitBuilder
operator|.
name|setTreeId
argument_list|(
name|parentToCommitToRevert
operator|.
name|getTree
argument_list|()
argument_list|)
expr_stmt|;
name|revertCommitBuilder
operator|.
name|setAuthor
argument_list|(
name|authorIdent
argument_list|)
expr_stmt|;
name|revertCommitBuilder
operator|.
name|setCommitter
argument_list|(
name|authorIdent
argument_list|)
expr_stmt|;
name|Change
name|changeToRevert
init|=
name|notes
operator|.
name|getChange
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
name|message
operator|=
name|MessageFormat
operator|.
name|format
argument_list|(
name|ChangeMessages
operator|.
name|get
argument_list|()
operator|.
name|revertChangeDefaultMessage
argument_list|,
name|changeToRevert
operator|.
name|getSubject
argument_list|()
argument_list|,
name|patch
operator|.
name|commitId
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ObjectId
name|generatedChangeId
init|=
name|Change
operator|.
name|generateChangeId
argument_list|()
decl_stmt|;
name|revertCommitBuilder
operator|.
name|setMessage
argument_list|(
name|ChangeIdUtil
operator|.
name|insertId
argument_list|(
name|message
argument_list|,
name|generatedChangeId
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|Change
operator|.
name|Id
name|changeId
init|=
name|Change
operator|.
name|id
argument_list|(
name|seq
operator|.
name|nextChangeId
argument_list|()
argument_list|)
decl_stmt|;
name|ObjectId
name|id
init|=
name|oi
operator|.
name|insert
argument_list|(
name|revertCommitBuilder
argument_list|)
decl_stmt|;
name|RevCommit
name|revertCommit
init|=
name|revWalk
operator|.
name|parseCommit
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|NotifyResolver
operator|.
name|Result
name|notify
init|=
name|notifyResolver
operator|.
name|resolve
argument_list|(
name|firstNonNull
argument_list|(
name|input
operator|.
name|notify
argument_list|,
name|NotifyHandling
operator|.
name|ALL
argument_list|)
argument_list|,
name|input
operator|.
name|notifyDetails
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
name|revertCommit
argument_list|,
name|notes
operator|.
name|getChange
argument_list|()
operator|.
name|getDest
argument_list|()
operator|.
name|branch
argument_list|()
argument_list|)
operator|.
name|setTopic
argument_list|(
name|input
operator|.
name|topic
operator|==
literal|null
condition|?
name|changeToRevert
operator|.
name|getTopic
argument_list|()
else|:
name|input
operator|.
name|topic
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
name|ins
operator|.
name|setMessage
argument_list|(
literal|"Uploaded patch set 1."
argument_list|)
expr_stmt|;
name|ReviewerSet
name|reviewerSet
init|=
name|approvalsUtil
operator|.
name|getReviewers
argument_list|(
name|notes
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|reviewers
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|reviewers
operator|.
name|add
argument_list|(
name|changeToRevert
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|reviewers
operator|.
name|addAll
argument_list|(
name|reviewerSet
operator|.
name|byState
argument_list|(
name|ReviewerStateInternal
operator|.
name|REVIEWER
argument_list|)
argument_list|)
expr_stmt|;
name|reviewers
operator|.
name|remove
argument_list|(
name|user
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|Account
operator|.
name|Id
argument_list|>
name|ccs
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|reviewerSet
operator|.
name|byState
argument_list|(
name|ReviewerStateInternal
operator|.
name|CC
argument_list|)
argument_list|)
decl_stmt|;
name|ccs
operator|.
name|remove
argument_list|(
name|user
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
name|ins
operator|.
name|setReviewersAndCcs
argument_list|(
name|reviewers
argument_list|,
name|ccs
argument_list|)
expr_stmt|;
name|ins
operator|.
name|setRevertOf
argument_list|(
name|changeIdToRevert
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
name|project
argument_list|,
name|user
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
name|revWalk
argument_list|,
name|oi
argument_list|)
expr_stmt|;
name|bu
operator|.
name|setNotify
argument_list|(
name|notify
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
name|addOp
argument_list|(
name|changeId
argument_list|,
operator|new
name|NotifyOp
argument_list|(
name|changeToRevert
argument_list|,
name|ins
argument_list|)
argument_list|)
expr_stmt|;
name|bu
operator|.
name|addOp
argument_list|(
name|changeToRevert
operator|.
name|getId
argument_list|()
argument_list|,
operator|new
name|PostRevertedMessageOp
argument_list|(
name|generatedChangeId
argument_list|)
argument_list|)
expr_stmt|;
name|bu
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
return|return
name|changeId
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
name|changeIdToRevert
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getDescription (ChangeResource rsrc)
specifier|public
name|UiAction
operator|.
name|Description
name|getDescription
parameter_list|(
name|ChangeResource
name|rsrc
parameter_list|)
block|{
name|Change
name|change
init|=
name|rsrc
operator|.
name|getChange
argument_list|()
decl_stmt|;
name|boolean
name|projectStatePermitsWrite
init|=
literal|false
decl_stmt|;
try|try
block|{
name|projectStatePermitsWrite
operator|=
name|projectCache
operator|.
name|checkedGet
argument_list|(
name|rsrc
operator|.
name|getProject
argument_list|()
argument_list|)
operator|.
name|statePermitsWrite
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|withCause
argument_list|(
name|e
argument_list|)
operator|.
name|log
argument_list|(
literal|"Failed to check if project state permits write: %s"
argument_list|,
name|rsrc
operator|.
name|getProject
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|UiAction
operator|.
name|Description
argument_list|()
operator|.
name|setLabel
argument_list|(
literal|"Revert"
argument_list|)
operator|.
name|setTitle
argument_list|(
literal|"Revert the change"
argument_list|)
operator|.
name|setVisible
argument_list|(
name|and
argument_list|(
name|change
operator|.
name|isMerged
argument_list|()
operator|&&
name|projectStatePermitsWrite
argument_list|,
name|permissionBackend
operator|.
name|user
argument_list|(
name|rsrc
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|ref
argument_list|(
name|change
operator|.
name|getDest
argument_list|()
argument_list|)
operator|.
name|testCond
argument_list|(
name|CREATE_CHANGE
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|class|NotifyOp
specifier|private
class|class
name|NotifyOp
implements|implements
name|BatchUpdateOp
block|{
DECL|field|change
specifier|private
specifier|final
name|Change
name|change
decl_stmt|;
DECL|field|ins
specifier|private
specifier|final
name|ChangeInserter
name|ins
decl_stmt|;
DECL|method|NotifyOp (Change change, ChangeInserter ins)
name|NotifyOp
parameter_list|(
name|Change
name|change
parameter_list|,
name|ChangeInserter
name|ins
parameter_list|)
block|{
name|this
operator|.
name|change
operator|=
name|change
expr_stmt|;
name|this
operator|.
name|ins
operator|=
name|ins
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|postUpdate (Context ctx)
specifier|public
name|void
name|postUpdate
parameter_list|(
name|Context
name|ctx
parameter_list|)
throws|throws
name|Exception
block|{
name|changeReverted
operator|.
name|fire
argument_list|(
name|change
argument_list|,
name|ins
operator|.
name|getChange
argument_list|()
argument_list|,
name|ctx
operator|.
name|getWhen
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|RevertedSender
name|cm
init|=
name|revertedSenderFactory
operator|.
name|create
argument_list|(
name|ctx
operator|.
name|getProject
argument_list|()
argument_list|,
name|change
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|cm
operator|.
name|setFrom
argument_list|(
name|ctx
operator|.
name|getAccountId
argument_list|()
argument_list|)
expr_stmt|;
name|cm
operator|.
name|setNotify
argument_list|(
name|ctx
operator|.
name|getNotify
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|cm
operator|.
name|send
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|err
parameter_list|)
block|{
name|logger
operator|.
name|atSevere
argument_list|()
operator|.
name|withCause
argument_list|(
name|err
argument_list|)
operator|.
name|log
argument_list|(
literal|"Cannot send email for revert change %s"
argument_list|,
name|change
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|PostRevertedMessageOp
specifier|private
class|class
name|PostRevertedMessageOp
implements|implements
name|BatchUpdateOp
block|{
DECL|field|computedChangeId
specifier|private
specifier|final
name|ObjectId
name|computedChangeId
decl_stmt|;
DECL|method|PostRevertedMessageOp (ObjectId computedChangeId)
name|PostRevertedMessageOp
parameter_list|(
name|ObjectId
name|computedChangeId
parameter_list|)
block|{
name|this
operator|.
name|computedChangeId
operator|=
name|computedChangeId
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateChange (ChangeContext ctx)
specifier|public
name|boolean
name|updateChange
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|)
block|{
name|Change
name|change
init|=
name|ctx
operator|.
name|getChange
argument_list|()
decl_stmt|;
name|PatchSet
operator|.
name|Id
name|patchSetId
init|=
name|change
operator|.
name|currentPatchSetId
argument_list|()
decl_stmt|;
name|ChangeMessage
name|changeMessage
init|=
name|ChangeMessagesUtil
operator|.
name|newMessage
argument_list|(
name|ctx
argument_list|,
literal|"Created a revert of this change as I"
operator|+
name|computedChangeId
operator|.
name|name
argument_list|()
argument_list|,
name|ChangeMessagesUtil
operator|.
name|TAG_REVERT
argument_list|)
decl_stmt|;
name|cmUtil
operator|.
name|addChangeMessage
argument_list|(
name|ctx
operator|.
name|getUpdate
argument_list|(
name|patchSetId
argument_list|)
argument_list|,
name|changeMessage
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

