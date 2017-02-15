begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|// Copyright (C) 2017 The Android Open Source Project
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
DECL|package|com.google.gerrit.acceptance.rest.account
package|package
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|rest
operator|.
name|account
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
name|truth
operator|.
name|Truth
operator|.
name|assertThat
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
name|acceptance
operator|.
name|GitUtil
operator|.
name|fetch
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
name|account
operator|.
name|externalids
operator|.
name|ExternalId
operator|.
name|SCHEME_USERNAME
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
name|group
operator|.
name|SystemGroupBackend
operator|.
name|REGISTERED_USERS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|rholder
operator|.
name|retry
operator|.
name|BlockStrategy
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|rholder
operator|.
name|retry
operator|.
name|Retryer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|rholder
operator|.
name|retry
operator|.
name|RetryerBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|rholder
operator|.
name|retry
operator|.
name|StopStrategies
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|AbstractDaemonTest
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|PushOneCommit
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|RestResponse
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gerrit
operator|.
name|acceptance
operator|.
name|Sandboxed
import|;
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
name|extensions
operator|.
name|common
operator|.
name|AccountExternalIdInfo
import|;
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
name|account
operator|.
name|externalids
operator|.
name|DisabledExternalIdCache
import|;
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
name|externalids
operator|.
name|ExternalId
import|;
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
name|externalids
operator|.
name|ExternalIds
import|;
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
name|externalids
operator|.
name|ExternalIdsUpdate
import|;
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
name|git
operator|.
name|LockFailureException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|reflect
operator|.
name|TypeToken
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
name|Collection
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|api
operator|.
name|errors
operator|.
name|TransportException
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
name|internal
operator|.
name|storage
operator|.
name|dfs
operator|.
name|InMemoryRepository
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
name|junit
operator|.
name|TestRepository
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
annotation|@
name|Sandboxed
DECL|class|ExternalIdIT
specifier|public
class|class
name|ExternalIdIT
extends|extends
name|AbstractDaemonTest
block|{
DECL|field|allUsers
annotation|@
name|Inject
specifier|private
name|AllUsersName
name|allUsers
decl_stmt|;
DECL|field|extIdsUpdate
annotation|@
name|Inject
specifier|private
name|ExternalIdsUpdate
operator|.
name|Server
name|extIdsUpdate
decl_stmt|;
DECL|field|externalIds
annotation|@
name|Inject
specifier|private
name|ExternalIds
name|externalIds
decl_stmt|;
annotation|@
name|Test
DECL|method|getExternalIDs ()
specifier|public
name|void
name|getExternalIDs
parameter_list|()
throws|throws
name|Exception
block|{
name|Collection
argument_list|<
name|ExternalId
argument_list|>
name|expectedIds
init|=
name|accountCache
operator|.
name|get
argument_list|(
name|user
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|getExternalIds
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AccountExternalIdInfo
argument_list|>
name|expectedIdInfos
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ExternalId
name|id
range|:
name|expectedIds
control|)
block|{
name|AccountExternalIdInfo
name|info
init|=
operator|new
name|AccountExternalIdInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|identity
operator|=
name|id
operator|.
name|key
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|info
operator|.
name|emailAddress
operator|=
name|id
operator|.
name|email
argument_list|()
expr_stmt|;
name|info
operator|.
name|canDelete
operator|=
operator|!
name|id
operator|.
name|isScheme
argument_list|(
name|SCHEME_USERNAME
argument_list|)
condition|?
literal|true
else|:
literal|null
expr_stmt|;
name|info
operator|.
name|trusted
operator|=
literal|true
expr_stmt|;
name|expectedIdInfos
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
name|RestResponse
name|response
init|=
name|userRestSession
operator|.
name|get
argument_list|(
literal|"/accounts/self/external.ids"
argument_list|)
decl_stmt|;
name|response
operator|.
name|assertOK
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|AccountExternalIdInfo
argument_list|>
name|results
init|=
name|newGson
argument_list|()
operator|.
name|fromJson
argument_list|(
name|response
operator|.
name|getReader
argument_list|()
argument_list|,
operator|new
name|TypeToken
argument_list|<
name|List
argument_list|<
name|AccountExternalIdInfo
argument_list|>
argument_list|>
argument_list|()
block|{}
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|expectedIdInfos
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|results
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|results
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|expectedIdInfos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteExternalIDs ()
specifier|public
name|void
name|deleteExternalIDs
parameter_list|()
throws|throws
name|Exception
block|{
name|setApiUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|AccountExternalIdInfo
argument_list|>
name|externalIds
init|=
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|self
argument_list|()
operator|.
name|getExternalIds
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|toDelete
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|AccountExternalIdInfo
argument_list|>
name|expectedIds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|AccountExternalIdInfo
name|id
range|:
name|externalIds
control|)
block|{
if|if
condition|(
name|id
operator|.
name|canDelete
operator|!=
literal|null
operator|&&
name|id
operator|.
name|canDelete
condition|)
block|{
name|toDelete
operator|.
name|add
argument_list|(
name|id
operator|.
name|identity
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|expectedIds
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|toDelete
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|RestResponse
name|response
init|=
name|userRestSession
operator|.
name|post
argument_list|(
literal|"/accounts/self/external.ids:delete"
argument_list|,
name|toDelete
argument_list|)
decl_stmt|;
name|response
operator|.
name|assertNoContent
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|AccountExternalIdInfo
argument_list|>
name|results
init|=
name|gApi
operator|.
name|accounts
argument_list|()
operator|.
name|self
argument_list|()
operator|.
name|getExternalIds
argument_list|()
decl_stmt|;
comment|// The external ID in WebSession will not be set for tests, resulting that
comment|// "mailto:user@example.com" can be deleted while "username:user" can't.
name|assertThat
argument_list|(
name|results
argument_list|)
operator|.
name|hasSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|results
argument_list|)
operator|.
name|containsExactlyElementsIn
argument_list|(
name|expectedIds
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteExternalIDs_Conflict ()
specifier|public
name|void
name|deleteExternalIDs_Conflict
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|toDelete
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|externalIdStr
init|=
literal|"username:"
operator|+
name|user
operator|.
name|username
decl_stmt|;
name|toDelete
operator|.
name|add
argument_list|(
name|externalIdStr
argument_list|)
expr_stmt|;
name|RestResponse
name|response
init|=
name|userRestSession
operator|.
name|post
argument_list|(
literal|"/accounts/self/external.ids:delete"
argument_list|,
name|toDelete
argument_list|)
decl_stmt|;
name|response
operator|.
name|assertConflict
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getEntityContent
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"External id %s cannot be deleted"
argument_list|,
name|externalIdStr
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|deleteExternalIDs_UnprocessableEntity ()
specifier|public
name|void
name|deleteExternalIDs_UnprocessableEntity
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|toDelete
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|externalIdStr
init|=
literal|"mailto:user@domain.com"
decl_stmt|;
name|toDelete
operator|.
name|add
argument_list|(
name|externalIdStr
argument_list|)
expr_stmt|;
name|RestResponse
name|response
init|=
name|userRestSession
operator|.
name|post
argument_list|(
literal|"/accounts/self/external.ids:delete"
argument_list|,
name|toDelete
argument_list|)
decl_stmt|;
name|response
operator|.
name|assertUnprocessableEntity
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getEntityContent
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"External id %s does not exist"
argument_list|,
name|externalIdStr
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|fetchExternalIdsBranch ()
specifier|public
name|void
name|fetchExternalIdsBranch
parameter_list|()
throws|throws
name|Exception
block|{
name|TestRepository
argument_list|<
name|InMemoryRepository
argument_list|>
name|allUsersRepo
init|=
name|cloneProject
argument_list|(
name|allUsers
argument_list|,
name|user
argument_list|)
decl_stmt|;
comment|// refs/meta/external-ids is only visible to users with the 'Access Database' capability
try|try
block|{
name|fetch
argument_list|(
name|allUsersRepo
argument_list|,
name|RefNames
operator|.
name|REFS_EXTERNAL_IDS
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected TransportException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransportException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"Remote does not have "
operator|+
name|RefNames
operator|.
name|REFS_EXTERNAL_IDS
operator|+
literal|" available for fetch."
argument_list|)
expr_stmt|;
block|}
name|allowGlobalCapabilities
argument_list|(
name|REGISTERED_USERS
argument_list|,
name|GlobalCapability
operator|.
name|ACCESS_DATABASE
argument_list|)
expr_stmt|;
comment|// re-clone to get new request context, otherwise the old global capabilities are still cached
comment|// in the IdentifiedUser object
name|allUsersRepo
operator|=
name|cloneProject
argument_list|(
name|allUsers
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|fetch
argument_list|(
name|allUsersRepo
argument_list|,
name|RefNames
operator|.
name|REFS_EXTERNAL_IDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|pushToExternalIdsBranch ()
specifier|public
name|void
name|pushToExternalIdsBranch
parameter_list|()
throws|throws
name|Exception
block|{
name|grant
argument_list|(
name|Permission
operator|.
name|READ
argument_list|,
name|allUsers
argument_list|,
name|RefNames
operator|.
name|REFS_EXTERNAL_IDS
argument_list|)
expr_stmt|;
name|grant
argument_list|(
name|Permission
operator|.
name|PUSH
argument_list|,
name|allUsers
argument_list|,
name|RefNames
operator|.
name|REFS_EXTERNAL_IDS
argument_list|)
expr_stmt|;
name|TestRepository
argument_list|<
name|InMemoryRepository
argument_list|>
name|allUsersRepo
init|=
name|cloneProject
argument_list|(
name|allUsers
argument_list|)
decl_stmt|;
name|fetch
argument_list|(
name|allUsersRepo
argument_list|,
name|RefNames
operator|.
name|REFS_EXTERNAL_IDS
operator|+
literal|":externalIds"
argument_list|)
expr_stmt|;
name|allUsersRepo
operator|.
name|reset
argument_list|(
literal|"externalIds"
argument_list|)
expr_stmt|;
name|PushOneCommit
name|push
init|=
name|pushFactory
operator|.
name|create
argument_list|(
name|db
argument_list|,
name|admin
operator|.
name|getIdent
argument_list|()
argument_list|,
name|allUsersRepo
argument_list|)
decl_stmt|;
name|push
operator|.
name|to
argument_list|(
name|RefNames
operator|.
name|REFS_EXTERNAL_IDS
argument_list|)
operator|.
name|assertErrorStatus
argument_list|(
literal|"not allowed to update "
operator|+
name|RefNames
operator|.
name|REFS_EXTERNAL_IDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|retryOnLockFailure ()
specifier|public
name|void
name|retryOnLockFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|Retryer
argument_list|<
name|ObjectId
argument_list|>
name|retryer
init|=
name|ExternalIdsUpdate
operator|.
name|retryerBuilder
argument_list|()
operator|.
name|withBlockStrategy
argument_list|(
operator|new
name|BlockStrategy
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|block
parameter_list|(
name|long
name|sleepTime
parameter_list|)
block|{
comment|// Don't sleep in tests.
block|}
block|}
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ExternalId
operator|.
name|Key
name|fooId
init|=
name|ExternalId
operator|.
name|Key
operator|.
name|create
argument_list|(
literal|"foo"
argument_list|,
literal|"foo"
argument_list|)
decl_stmt|;
name|ExternalId
operator|.
name|Key
name|barId
init|=
name|ExternalId
operator|.
name|Key
operator|.
name|create
argument_list|(
literal|"bar"
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|doneBgUpdate
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|ExternalIdsUpdate
name|update
init|=
operator|new
name|ExternalIdsUpdate
argument_list|(
name|repoManager
argument_list|,
name|allUsers
argument_list|,
name|externalIds
argument_list|,
operator|new
name|DisabledExternalIdCache
argument_list|()
argument_list|,
name|serverIdent
operator|.
name|get
argument_list|()
argument_list|,
name|serverIdent
operator|.
name|get
argument_list|()
argument_list|,
parameter_list|()
lambda|->
block|{
if|if
condition|(
operator|!
name|doneBgUpdate
operator|.
name|getAndSet
argument_list|(
literal|true
argument_list|)
condition|)
block|{
try|try
block|{
name|extIdsUpdate
operator|.
name|create
argument_list|()
operator|.
name|insert
argument_list|(
name|db
argument_list|,
name|ExternalId
operator|.
name|create
argument_list|(
name|barId
argument_list|,
name|admin
operator|.
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|ConfigInvalidException
decl||
name|OrmException
name|e
parameter_list|)
block|{
comment|// Ignore, the successful insertion of the external ID is asserted later
block|}
block|}
block|}
argument_list|,
name|retryer
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|doneBgUpdate
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|isFalse
argument_list|()
expr_stmt|;
name|update
operator|.
name|insert
argument_list|(
name|db
argument_list|,
name|ExternalId
operator|.
name|create
argument_list|(
name|fooId
argument_list|,
name|admin
operator|.
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doneBgUpdate
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|externalIds
operator|.
name|get
argument_list|(
name|db
argument_list|,
name|fooId
argument_list|)
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|externalIds
operator|.
name|get
argument_list|(
name|db
argument_list|,
name|barId
argument_list|)
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|failAfterRetryerGivesUp ()
specifier|public
name|void
name|failAfterRetryerGivesUp
parameter_list|()
throws|throws
name|Exception
block|{
name|ExternalId
operator|.
name|Key
index|[]
name|extIdsKeys
init|=
block|{
name|ExternalId
operator|.
name|Key
operator|.
name|create
argument_list|(
literal|"foo"
argument_list|,
literal|"foo"
argument_list|)
block|,
name|ExternalId
operator|.
name|Key
operator|.
name|create
argument_list|(
literal|"bar"
argument_list|,
literal|"bar"
argument_list|)
block|,
name|ExternalId
operator|.
name|Key
operator|.
name|create
argument_list|(
literal|"baz"
argument_list|,
literal|"baz"
argument_list|)
block|}
decl_stmt|;
specifier|final
name|AtomicInteger
name|bgCounter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ExternalIdsUpdate
name|update
init|=
operator|new
name|ExternalIdsUpdate
argument_list|(
name|repoManager
argument_list|,
name|allUsers
argument_list|,
name|externalIds
argument_list|,
operator|new
name|DisabledExternalIdCache
argument_list|()
argument_list|,
name|serverIdent
operator|.
name|get
argument_list|()
argument_list|,
name|serverIdent
operator|.
name|get
argument_list|()
argument_list|,
parameter_list|()
lambda|->
block|{
try|try
block|{
name|extIdsUpdate
operator|.
name|create
argument_list|()
operator|.
name|insert
argument_list|(
name|db
argument_list|,
name|ExternalId
operator|.
name|create
argument_list|(
name|extIdsKeys
index|[
name|bgCounter
operator|.
name|getAndAdd
argument_list|(
literal|1
argument_list|)
index|]
argument_list|,
name|admin
operator|.
name|id
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|ConfigInvalidException
decl||
name|OrmException
name|e
parameter_list|)
block|{
comment|// Ignore, the successful insertion of the external ID is asserted later
block|}
block|}
argument_list|,
name|RetryerBuilder
operator|.
expr|<
name|ObjectId
operator|>
name|newBuilder
argument_list|()
operator|.
name|retryIfException
argument_list|(
name|e
lambda|->
name|e
operator|instanceof
name|LockFailureException
argument_list|)
operator|.
name|withStopStrategy
argument_list|(
name|StopStrategies
operator|.
name|stopAfterAttempt
argument_list|(
name|extIdsKeys
operator|.
name|length
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|bgCounter
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|0
argument_list|)
expr_stmt|;
try|try
block|{
name|update
operator|.
name|insert
argument_list|(
name|db
argument_list|,
name|ExternalId
operator|.
name|create
argument_list|(
name|ExternalId
operator|.
name|Key
operator|.
name|create
argument_list|(
literal|"abc"
argument_list|,
literal|"abc"
argument_list|)
argument_list|,
name|admin
operator|.
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected LockFailureException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockFailureException
name|e
parameter_list|)
block|{
comment|// Ignore, expected
block|}
name|assertThat
argument_list|(
name|bgCounter
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|extIdsKeys
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|ExternalId
operator|.
name|Key
name|extIdKey
range|:
name|extIdsKeys
control|)
block|{
name|assertThat
argument_list|(
name|externalIds
operator|.
name|get
argument_list|(
name|db
argument_list|,
name|extIdKey
argument_list|)
argument_list|)
operator|.
name|isNotNull
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

