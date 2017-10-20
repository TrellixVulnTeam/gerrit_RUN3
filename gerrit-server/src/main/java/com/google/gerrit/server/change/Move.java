begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2015 The Android Open Source Project
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
DECL|package|com.google.gerrit.server.change
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
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
name|ChangePermission
operator|.
name|ABANDON
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
import|import static
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|server
operator|.
name|query
operator|.
name|change
operator|.
name|ChangeData
operator|.
name|asChanges
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
name|common
operator|.
name|data
operator|.
name|LabelType
import|;
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
name|MoveInput
import|;
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
name|Change
operator|.
name|Status
import|;
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
name|LabelId
import|;
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
name|PatchSetApproval
import|;
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
name|notedb
operator|.
name|ChangeUpdate
import|;
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
name|query
operator|.
name|change
operator|.
name|InternalChangeQuery
import|;
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

begin_class
annotation|@
name|Singleton
DECL|class|Move
specifier|public
class|class
name|Move
extends|extends
name|RetryingRestModifyView
argument_list|<
name|ChangeResource
argument_list|,
name|MoveInput
argument_list|,
name|ChangeInfo
argument_list|>
implements|implements
name|UiAction
argument_list|<
name|ChangeResource
argument_list|>
block|{
DECL|field|permissionBackend
specifier|private
specifier|final
name|PermissionBackend
name|permissionBackend
decl_stmt|;
DECL|field|dbProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
decl_stmt|;
DECL|field|json
specifier|private
specifier|final
name|ChangeJson
operator|.
name|Factory
name|json
decl_stmt|;
DECL|field|repoManager
specifier|private
specifier|final
name|GitRepositoryManager
name|repoManager
decl_stmt|;
DECL|field|queryProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
decl_stmt|;
DECL|field|cmUtil
specifier|private
specifier|final
name|ChangeMessagesUtil
name|cmUtil
decl_stmt|;
DECL|field|psUtil
specifier|private
specifier|final
name|PatchSetUtil
name|psUtil
decl_stmt|;
DECL|field|approvalsUtil
specifier|private
specifier|final
name|ApprovalsUtil
name|approvalsUtil
decl_stmt|;
DECL|field|projectCache
specifier|private
specifier|final
name|ProjectCache
name|projectCache
decl_stmt|;
DECL|field|userProvider
specifier|private
specifier|final
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|userProvider
decl_stmt|;
annotation|@
name|Inject
DECL|method|Move ( PermissionBackend permissionBackend, Provider<ReviewDb> dbProvider, ChangeJson.Factory json, GitRepositoryManager repoManager, Provider<InternalChangeQuery> queryProvider, ChangeMessagesUtil cmUtil, RetryHelper retryHelper, PatchSetUtil psUtil, ApprovalsUtil approvalsUtil, ProjectCache projectCache, Provider<CurrentUser> userProvider)
name|Move
parameter_list|(
name|PermissionBackend
name|permissionBackend
parameter_list|,
name|Provider
argument_list|<
name|ReviewDb
argument_list|>
name|dbProvider
parameter_list|,
name|ChangeJson
operator|.
name|Factory
name|json
parameter_list|,
name|GitRepositoryManager
name|repoManager
parameter_list|,
name|Provider
argument_list|<
name|InternalChangeQuery
argument_list|>
name|queryProvider
parameter_list|,
name|ChangeMessagesUtil
name|cmUtil
parameter_list|,
name|RetryHelper
name|retryHelper
parameter_list|,
name|PatchSetUtil
name|psUtil
parameter_list|,
name|ApprovalsUtil
name|approvalsUtil
parameter_list|,
name|ProjectCache
name|projectCache
parameter_list|,
name|Provider
argument_list|<
name|CurrentUser
argument_list|>
name|userProvider
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
name|dbProvider
operator|=
name|dbProvider
expr_stmt|;
name|this
operator|.
name|json
operator|=
name|json
expr_stmt|;
name|this
operator|.
name|repoManager
operator|=
name|repoManager
expr_stmt|;
name|this
operator|.
name|queryProvider
operator|=
name|queryProvider
expr_stmt|;
name|this
operator|.
name|cmUtil
operator|=
name|cmUtil
expr_stmt|;
name|this
operator|.
name|psUtil
operator|=
name|psUtil
expr_stmt|;
name|this
operator|.
name|approvalsUtil
operator|=
name|approvalsUtil
expr_stmt|;
name|this
operator|.
name|projectCache
operator|=
name|projectCache
expr_stmt|;
name|this
operator|.
name|userProvider
operator|=
name|userProvider
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|applyImpl ( BatchUpdate.Factory updateFactory, ChangeResource rsrc, MoveInput input)
specifier|protected
name|ChangeInfo
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
name|MoveInput
name|input
parameter_list|)
throws|throws
name|RestApiException
throws|,
name|OrmException
throws|,
name|UpdateException
throws|,
name|PermissionBackendException
block|{
name|Change
name|change
init|=
name|rsrc
operator|.
name|getChange
argument_list|()
decl_stmt|;
name|Project
operator|.
name|NameKey
name|project
init|=
name|rsrc
operator|.
name|getProject
argument_list|()
decl_stmt|;
name|IdentifiedUser
name|caller
init|=
name|rsrc
operator|.
name|getUser
argument_list|()
operator|.
name|asIdentifiedUser
argument_list|()
decl_stmt|;
name|input
operator|.
name|destinationBranch
operator|=
name|RefNames
operator|.
name|fullName
argument_list|(
name|input
operator|.
name|destinationBranch
argument_list|)
expr_stmt|;
if|if
condition|(
name|change
operator|.
name|getStatus
argument_list|()
operator|.
name|isClosed
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"Change is "
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
name|Branch
operator|.
name|NameKey
name|newDest
init|=
operator|new
name|Branch
operator|.
name|NameKey
argument_list|(
name|project
argument_list|,
name|input
operator|.
name|destinationBranch
argument_list|)
decl_stmt|;
if|if
condition|(
name|change
operator|.
name|getDest
argument_list|()
operator|.
name|equals
argument_list|(
name|newDest
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"Change is already destined for the specified branch"
argument_list|)
throw|;
block|}
comment|// Move requires abandoning this change, and creating a new change.
try|try
block|{
name|rsrc
operator|.
name|permissions
argument_list|()
operator|.
name|database
argument_list|(
name|dbProvider
argument_list|)
operator|.
name|check
argument_list|(
name|ABANDON
argument_list|)
expr_stmt|;
name|permissionBackend
operator|.
name|user
argument_list|(
name|caller
argument_list|)
operator|.
name|database
argument_list|(
name|dbProvider
argument_list|)
operator|.
name|ref
argument_list|(
name|newDest
argument_list|)
operator|.
name|check
argument_list|(
name|CREATE_CHANGE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthException
name|denied
parameter_list|)
block|{
throw|throw
operator|new
name|AuthException
argument_list|(
literal|"move not permitted"
argument_list|,
name|denied
argument_list|)
throw|;
block|}
try|try
init|(
name|BatchUpdate
name|u
init|=
name|updateFactory
operator|.
name|create
argument_list|(
name|dbProvider
operator|.
name|get
argument_list|()
argument_list|,
name|project
argument_list|,
name|caller
argument_list|,
name|TimeUtil
operator|.
name|nowTs
argument_list|()
argument_list|)
init|)
block|{
name|u
operator|.
name|addOp
argument_list|(
name|change
operator|.
name|getId
argument_list|()
argument_list|,
operator|new
name|Op
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
name|u
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
return|return
name|json
operator|.
name|noOptions
argument_list|()
operator|.
name|format
argument_list|(
name|project
argument_list|,
name|rsrc
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
DECL|class|Op
specifier|private
class|class
name|Op
implements|implements
name|BatchUpdateOp
block|{
DECL|field|input
specifier|private
specifier|final
name|MoveInput
name|input
decl_stmt|;
DECL|field|change
specifier|private
name|Change
name|change
decl_stmt|;
DECL|field|newDestKey
specifier|private
name|Branch
operator|.
name|NameKey
name|newDestKey
decl_stmt|;
DECL|method|Op (MoveInput input)
name|Op
parameter_list|(
name|MoveInput
name|input
parameter_list|)
block|{
name|this
operator|.
name|input
operator|=
name|input
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
throws|throws
name|OrmException
throws|,
name|ResourceConflictException
throws|,
name|IOException
block|{
name|change
operator|=
name|ctx
operator|.
name|getChange
argument_list|()
expr_stmt|;
if|if
condition|(
name|change
operator|.
name|getStatus
argument_list|()
operator|!=
name|Status
operator|.
name|NEW
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"Change is "
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
name|Project
operator|.
name|NameKey
name|projectKey
init|=
name|change
operator|.
name|getProject
argument_list|()
decl_stmt|;
name|newDestKey
operator|=
operator|new
name|Branch
operator|.
name|NameKey
argument_list|(
name|projectKey
argument_list|,
name|input
operator|.
name|destinationBranch
argument_list|)
expr_stmt|;
name|Branch
operator|.
name|NameKey
name|changePrevDest
init|=
name|change
operator|.
name|getDest
argument_list|()
decl_stmt|;
if|if
condition|(
name|changePrevDest
operator|.
name|equals
argument_list|(
name|newDestKey
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"Change is already destined for the specified branch"
argument_list|)
throw|;
block|}
specifier|final
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
try|try
init|(
name|Repository
name|repo
init|=
name|repoManager
operator|.
name|openRepository
argument_list|(
name|projectKey
argument_list|)
init|;
name|RevWalk
name|revWalk
operator|=
operator|new
name|RevWalk
argument_list|(
name|repo
argument_list|)
init|)
block|{
name|RevCommit
name|currPatchsetRevCommit
init|=
name|revWalk
operator|.
name|parseCommit
argument_list|(
name|ObjectId
operator|.
name|fromString
argument_list|(
name|psUtil
operator|.
name|current
argument_list|(
name|ctx
operator|.
name|getDb
argument_list|()
argument_list|,
name|ctx
operator|.
name|getNotes
argument_list|()
argument_list|)
operator|.
name|getRevision
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|currPatchsetRevCommit
operator|.
name|getParentCount
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"Merge commit cannot be moved"
argument_list|)
throw|;
block|}
name|ObjectId
name|refId
init|=
name|repo
operator|.
name|resolve
argument_list|(
name|input
operator|.
name|destinationBranch
argument_list|)
decl_stmt|;
comment|// Check if destination ref exists in project repo
if|if
condition|(
name|refId
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"Destination "
operator|+
name|input
operator|.
name|destinationBranch
operator|+
literal|" not found in the project"
argument_list|)
throw|;
block|}
name|RevCommit
name|refCommit
init|=
name|revWalk
operator|.
name|parseCommit
argument_list|(
name|refId
argument_list|)
decl_stmt|;
if|if
condition|(
name|revWalk
operator|.
name|isMergedInto
argument_list|(
name|currPatchsetRevCommit
argument_list|,
name|refCommit
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"Current patchset revision is reachable from tip of "
operator|+
name|input
operator|.
name|destinationBranch
argument_list|)
throw|;
block|}
block|}
name|Change
operator|.
name|Key
name|changeKey
init|=
name|change
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|asChanges
argument_list|(
name|queryProvider
operator|.
name|get
argument_list|()
operator|.
name|byBranchKey
argument_list|(
name|newDestKey
argument_list|,
name|changeKey
argument_list|)
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"Destination "
operator|+
name|newDestKey
operator|.
name|getShortName
argument_list|()
operator|+
literal|" has a different change with same change key "
operator|+
name|changeKey
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|change
operator|.
name|currentPatchSetId
argument_list|()
operator|.
name|equals
argument_list|(
name|patchSetId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ResourceConflictException
argument_list|(
literal|"Patch set is not current"
argument_list|)
throw|;
block|}
name|PatchSet
operator|.
name|Id
name|psId
init|=
name|change
operator|.
name|currentPatchSetId
argument_list|()
decl_stmt|;
name|ChangeUpdate
name|update
init|=
name|ctx
operator|.
name|getUpdate
argument_list|(
name|psId
argument_list|)
decl_stmt|;
name|update
operator|.
name|setBranch
argument_list|(
name|newDestKey
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|change
operator|.
name|setDest
argument_list|(
name|newDestKey
argument_list|)
expr_stmt|;
name|updateApprovals
argument_list|(
name|ctx
argument_list|,
name|update
argument_list|,
name|psId
argument_list|,
name|projectKey
argument_list|)
expr_stmt|;
name|StringBuilder
name|msgBuf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|msgBuf
operator|.
name|append
argument_list|(
literal|"Change destination moved from "
argument_list|)
expr_stmt|;
name|msgBuf
operator|.
name|append
argument_list|(
name|changePrevDest
operator|.
name|getShortName
argument_list|()
argument_list|)
expr_stmt|;
name|msgBuf
operator|.
name|append
argument_list|(
literal|" to "
argument_list|)
expr_stmt|;
name|msgBuf
operator|.
name|append
argument_list|(
name|newDestKey
operator|.
name|getShortName
argument_list|()
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
name|msgBuf
operator|.
name|append
argument_list|(
literal|"\n\n"
argument_list|)
expr_stmt|;
name|msgBuf
operator|.
name|append
argument_list|(
name|input
operator|.
name|message
argument_list|)
expr_stmt|;
block|}
name|ChangeMessage
name|cmsg
init|=
name|ChangeMessagesUtil
operator|.
name|newMessage
argument_list|(
name|ctx
argument_list|,
name|msgBuf
operator|.
name|toString
argument_list|()
argument_list|,
name|ChangeMessagesUtil
operator|.
name|TAG_MOVE
argument_list|)
decl_stmt|;
name|cmUtil
operator|.
name|addChangeMessage
argument_list|(
name|ctx
operator|.
name|getDb
argument_list|()
argument_list|,
name|update
argument_list|,
name|cmsg
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**      * We have a long discussion about how to deal with its votes after moving a change from one      * branch to another. In the end, we think only keeping the veto votes is the best way since      * it's simple for us and less confusing for our users. See the discussion in the following      * proposal: https://gerrit-review.googlesource.com/c/gerrit/+/129171      */
DECL|method|updateApprovals ( ChangeContext ctx, ChangeUpdate update, PatchSet.Id psId, Project.NameKey project)
specifier|private
name|void
name|updateApprovals
parameter_list|(
name|ChangeContext
name|ctx
parameter_list|,
name|ChangeUpdate
name|update
parameter_list|,
name|PatchSet
operator|.
name|Id
name|psId
parameter_list|,
name|Project
operator|.
name|NameKey
name|project
parameter_list|)
throws|throws
name|IOException
throws|,
name|OrmException
block|{
name|List
argument_list|<
name|PatchSetApproval
argument_list|>
name|approvals
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|PatchSetApproval
name|psa
range|:
name|approvalsUtil
operator|.
name|byPatchSet
argument_list|(
name|ctx
operator|.
name|getDb
argument_list|()
argument_list|,
name|ctx
operator|.
name|getNotes
argument_list|()
argument_list|,
name|userProvider
operator|.
name|get
argument_list|()
argument_list|,
name|psId
argument_list|,
name|ctx
operator|.
name|getRevWalk
argument_list|()
argument_list|,
name|ctx
operator|.
name|getRepoView
argument_list|()
operator|.
name|getConfig
argument_list|()
argument_list|)
control|)
block|{
name|ProjectState
name|projectState
init|=
name|projectCache
operator|.
name|checkedGet
argument_list|(
name|project
argument_list|)
decl_stmt|;
name|LabelType
name|type
init|=
name|projectState
operator|.
name|getLabelTypes
argument_list|(
name|ctx
operator|.
name|getNotes
argument_list|()
argument_list|,
name|ctx
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|byLabel
argument_list|(
name|psa
operator|.
name|getLabelId
argument_list|()
argument_list|)
decl_stmt|;
comment|// Only keep veto votes, defined as votes where:
comment|// 1- the label function allows minimum values to block submission.
comment|// 2- the vote holds the minimum value.
if|if
condition|(
name|type
operator|.
name|isMaxNegative
argument_list|(
name|psa
argument_list|)
operator|&&
name|type
operator|.
name|getFunction
argument_list|()
operator|.
name|isBlock
argument_list|()
condition|)
block|{
continue|continue;
block|}
comment|// Remove votes from NoteDb.
name|update
operator|.
name|removeApprovalFor
argument_list|(
name|psa
operator|.
name|getAccountId
argument_list|()
argument_list|,
name|psa
operator|.
name|getLabel
argument_list|()
argument_list|)
expr_stmt|;
name|approvals
operator|.
name|add
argument_list|(
operator|new
name|PatchSetApproval
argument_list|(
operator|new
name|PatchSetApproval
operator|.
name|Key
argument_list|(
name|psId
argument_list|,
name|psa
operator|.
name|getAccountId
argument_list|()
argument_list|,
operator|new
name|LabelId
argument_list|(
name|psa
operator|.
name|getLabel
argument_list|()
argument_list|)
argument_list|)
argument_list|,
operator|(
name|short
operator|)
literal|0
argument_list|,
name|ctx
operator|.
name|getWhen
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Remove votes from ReviewDb.
name|ctx
operator|.
name|getDb
argument_list|()
operator|.
name|patchSetApprovals
argument_list|()
operator|.
name|upsert
argument_list|(
name|approvals
argument_list|)
expr_stmt|;
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
return|return
operator|new
name|UiAction
operator|.
name|Description
argument_list|()
operator|.
name|setLabel
argument_list|(
literal|"Move Change"
argument_list|)
operator|.
name|setTitle
argument_list|(
literal|"Move change to a different branch"
argument_list|)
operator|.
name|setVisible
argument_list|(
name|and
argument_list|(
name|change
operator|.
name|getStatus
argument_list|()
operator|.
name|isOpen
argument_list|()
argument_list|,
name|and
argument_list|(
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
argument_list|,
name|rsrc
operator|.
name|permissions
argument_list|()
operator|.
name|database
argument_list|(
name|dbProvider
argument_list|)
operator|.
name|testCond
argument_list|(
name|ABANDON
argument_list|)
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

